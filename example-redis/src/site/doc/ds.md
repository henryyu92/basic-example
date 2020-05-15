## 数据结构
Redis 中的数据是以 K-V 的模型存储的，Redis 支持 String, List, Set, SortedSet, Hash, HyperLogLog, Bitmap 等多种数据类型，这些数据类型都是由底层的数据结构组成。

### 简单动态字符串
简单动态字符串(Simple Dynamic String, SDS) 是 Redis 底层基础的数据结构，用于存储字符串和整形数据。SDS 兼容 C 语言标准字符串处理函数，并在此基础上保证了二进制安全。

*C 语言中字符串是以 \0 结尾，如果字符串中包含 \0 就会被截断，即非二进制安全。Redis 使用 len 判断字符串是否结束因此是二进制安全的。*

SDS 的数据结构：
```c
struct sdshdr {
    // buf 中已经使用的字节数量
    int len;

    // buf 中未使用的字节数量
    int free;

    // 存储数据的字节数组 
    char buf[];
}
```

SDS 的优点：
- 常数复杂度获取字符串长度。SDS 使用 len 记录字符串的长度，在获取时不需要使用时间复杂度为 O(N) 的遍历而直接读取 len 字段
- 杜绝缓冲区溢出。当 SDS 需要进行修改时，先检查空间是否满足修改所需的要求，如果不满足的话会将 SDS 的空间扩展至执行修改所需的大小
- 减少字符串修改带来的内存重分配。SDS 中 buf 包含了字符串和未使用的字节，未使用的字节长度由 free 记录。
- 二进制安全。SDS 使用 len 来判断结束而不是使用特殊字符(C 中的'\0')表示结束，因此可以存储任意字符串
- 兼容 C 字符串函数。SDS 中的字符串遵循 C 语言 '\0' 结尾的习惯，可以使用 C 语言的一些字符串函数处理 SDS 中的数据

SDS 通过未使用空间实现了空间预分配和惰性空间释放两种优化策略：
- 空间预分配：用于优化 SDS 的字符串增长操作，当需要对 SDS 进行空间扩展的时候，除了分配必须的空间外还分配了额外的未使用的空间，通过空间预分配策略可以减少连续执行字符串增长操作所需的内存重分配次数
  - 如果 SDS 进行修改后的长度(len)小于 1MB，则分配和 len 一样大小的未使用空间，即 free = len
  - 如果 SDS 进行修改后的长度(len)大于等于 1MB，则会分配 1MB 的未使用空间
- 惰性空间释放：用于优化 SDS 的字符串缩短操作，当需要缩短 SDS 的字符串时不立即回收多出的字节而是使用 free 记录多出的字节


### 压缩列表

压缩列表 (ZipList) 本质上是一个字节数组，是 Redis 为了节约内存而涉及的一种线性数据结构，ZipList 可以包含多个节点 (Entry) ，每个 Entry 可以是一个字节数组或一个整数。Redis 的有序集合、Hash、列表等数据类型的底层实现都直接或间接的使用了 ZipList。

zlbytes|zltail|zllen|entry|entry|...|zlend
-|-|-|-|-|-|-

压缩列表是一块连续内存，其各个组成部分为：
- ```zlbytes```：压缩列表占用的内存字节数，在对压缩列表进行内存重分配或者计算 zlend 时使用，占 4 个字节
- ```zltail```：ZipList 最后一个 Entry 相对于压缩列表的起始地址的偏移量(字节数)，占 4 个字节。无需遍历整个压缩列表可以确定表尾节点的地址，从而使得 push 和 pop 操作的时间复杂度为 O(1)
- ```zllen```：压缩列表包含的 Entry 数量，占 2 个字节，当压缩列表中的 Entry 超过 2^16-1 需要遍历整个压缩列表才能获取到元素
- ```entry```：压缩列表的节点，可以是字节数组或者整数，节点的长度由节点保存的内容决定
- ```zlend```：标记压缩列表的末端，固定为 0xFF

压缩列表中的节点可以保存字节数组或者整数，保存的数据都是经过了特殊的编码，编码的结构为：

previous_entry_length|encoding|content
-|-|-

- ```previous_entry_length```：前一个 Entry 的长度，占用 1 个或者 5 个字节。如果前一个 Entry 小于 254 字节时占用 1 个字节，如果前一个 Entry 大于 254 则占用 5 个字节，其中第一个字节是 0xFE，后面四个字节存储具体大小
- ```encoding```：Entry 的编码，
- ```content```：Entry 保存的值，可以是字节数据或者整数，值的数据类型和长度由 encoding 决定

压缩列表是一个连续的内存块，节点的更新会使 ```previous_entry_length``` 属性重新分配，从而导致后续的节点的 ```previous_entry_length``` 都需要重新分配，即连锁更新。

#### 压缩列表的节点
压缩列表的每个节点可以保存一个字节数组或一个整数值，其中字节数组有三种(63,16383,4294967295 字节)长度，整数值有六种(4,8,24 字节,int16_t,int32_t,int64_t)长度。

每个压缩列表节点由三部分组成：
- prevrawlen 表示 ziplist 中前一个 entry 的长度。压缩列表从表尾到表头的遍历就是使用节点的起始位置和 prevrawlen 实现的。如果前一个 entry 占用字节小于 254 那么就只用一个字节表示；如果前一个 entry 占用字节大于 254，则使用 5 个字节表示，第一个字节为 254，后面四个字节为一个整型数值存储前一个 entry 占用的字节数
- len 表示当前所有 entry 的数据长度
- encoding 表示 entry 的数据的编码
- content 属性负责保存节点的值，节点值可以是一个字节数组或者整数，值得的类型和长度由节点的 encoding 决定
#### 连锁更新
添加新节点或者删除节点导致 previous_entry_length 属性需要重新分配从而导致后续的 previous_entry_length 属性重新分配产生的连续多次空间扩展操作称为连锁更新。

连锁更新在最坏的情况下需要对压缩列表执行 N 次空间重分配操作，而每次空间重分配的最坏复杂度为 O(N)，因此连锁更新的最坏复杂度为 O(N^2)。实际情况中连锁更新发生的可能性较小，且连锁更新节点数量不多对性能不会造成很大影响，实际中对 ziplist 操作的平均时间复杂度为 O(N) 

### quickList

quickList 由 List 和 ZipList 结合而成，是 Redis 中 List 数据类型的底层实现。


考虑到双向链表在保存大量数据时需要更多额外内存保存指针并容易产生大量内存碎片，以及 ziplist 的插入和删除的高时间复杂度，Redis 将双向链表和 ziplist 结合成为 quicklist

```c
typedef struct quicklist{
    quicklistNode *head;
    quicklistNode *tail;
    unsigend long count;
    unsigend int len;
    int fill:16;
    unsigned int compress:16;
}quicklist;
```
- head 表示 quicklist 的头结点
- tail 表示 quicklist 的尾节点
- len 表示 quicklist 的节点的数量
- count 表示 ziplist 中所有 entry 的数量，可由 ```list-max-ziplist-size``` 设置
- fill 表示节点压缩深度，可由 ```list-compress-depth``` 设置
- compress 保存压缩程度，0 表示不压缩

```c
typedef struct quicklistNode{
    struct quicklistNode *prev;
    struct quicklistNode *next;
    // 不设置压缩则指向 ziplist，设置压缩则指向 quicklistLZF
    unsigned char *zl;
    // ziplist 的总大小(byte)
    unsigned int sz;
    // ziplist 中 item 的数量
    unsigned int count:16;
    // 是否采用 LZF 压缩算法压缩 quicklis 节点，1 表示压缩过，2 表示没压缩
    unsigned int encoding:2;
    ...
}quicklistNode;
```

### 字典
字典是一种用于保存键值对的抽象数据结构，在 Redis 中用于哈希键和数据库的底层实现。

Reids 字典所使用的哈希表由 dictht 结构定义：
```c
typedef struct dictht{
    dictEntry **table;
    unsigned long size;
    unsigned long sizemask;
    unsigned long used;
}dictht;
```
- table 属性是一个数组，数组中每个元素都是指向 dictEntry 的指针，每个 dictEntry 结构保存着一个键值对
- size 属性记录了哈希表的大小，即 table 数组的大小
- used 属性记录了哈希表目前已有节点的数量
- sizemask 属性的值总是等于 size-1，和哈希值一起决定一个键应该被放到 table 数组的哪个索引上面

哈希表节点使用 dictEntry 结构表示，每个 dictEntry 结构都保存着一个键值对：
```c
typedef struct dictEntry{
    void *key;
    union{
        void *val;
        uint64_t u64;
        int64_t s64;
    }v;
    struct dictEntry *next;
}dictEntry;
```
- key 属性保存着键值对中的键
- v 属性保存着键值对中的值，可以是一个指针、一个 uint64_t 整数或一个 int64_t 整数
- next 属性是指向另一个哈希表节点的指针，可以将多个哈希值相同的键值对连接在一起，以此解决哈希冲突问题

Reids 中字典是由 dict 结构表示：
```c
typedef struct dict{
    dictType *type;
    void *privdata;
    dictht ht[2];
    int rehashidx;
}dict;
```
- type 属性是指向 dictType 结构的指针，每个 dictType 结构保存了一簇用于操作特定类型键值对的函数，Redis 会为用途不同的字典设置不同的类型特定函数
- privdata 属性保存了需要传给类型特定函数的可选参数
- ht 属性是一个包含两个项的数组，数组中的每个项都是一个 dictht 哈希表，一般情况下字典使用 ht[0] 哈希表，当对 ht[0] 哈希表进行 rehash 是使用 ht[1] 哈希表
- rehashidx 记录了 rehash 目前的进度，如果目前没有在进行 rehash 则为 -1
#### 哈希算法
当要将一个新的键值对添加到字典中时，Reids 会先根据键值对的键计算出哈希值和索引值，然后根据索引值将包含新键值对的哈希表节点放到哈希表数组的指定索引中。

Reids 计算哈希值和索引值时先使用字典设置的哈希函数计算 key 得到哈希值，然后使用哈希表的 sizemask 和哈希值做位与运算得到索引值。Redis 使用 MurmurHash 算法来计算哈希值，该算法优点在于即使输入的键是有规律的，算法仍然可以给出一个很好的随机分布性并且计算速度和很快。
#### 键冲突
当有两个或以上数量的键被分配到哈希表数组的同一个索引上面时，Redis 使用链地址法解决键冲突。每个哈希表节点都有一个 next 指针指向下一个节点，多个节点可以用 next 指针构成一个单向链表，被分配到同一个索引上的多个节点可以用这个单向链表连接起来从而解决了键冲突问题。因为 dictEntry 节点组成的链表没有指向链表表尾的指针，所以为了速度考虑总是将新结点添加到链表的表头位置。
#### rehash
为了让哈希表的负载因子(load factor)维持在一个合理的范围内，当哈希表保存的键值对数量太多或者太少时，需要对哈希表的大小进行相应的扩展或者收缩。

哈希表的扩展或收缩是通过执行 rehash 操作完成的，Reids 对字典的哈希表执行 rehash 步骤：
- 为字典的 ht[1] 哈希表分配空间，大小取决于执行的操作和 ht[0] 当前包含的键值对数量。如果是扩展操作，则 ht[1] 的大小为第一个大于等于 ht[0].used*2 的 2 的幂次值；如果是收缩操作，则 ht[1] 大小为第一个大于等于 ht[0].used 的 2 的幂次值
- 将保存在 ht[1] 中的所有键值对 rehash 到 ht[1] 上面。rehash 是指重新计算键的哈希值和索引值，然后将键值对放置到 ht[1] 哈希表的指定位置
- 将 ht[0] 包含的所有键值对都迁移到 ht[1] 之后释放 ht[0] 并将 ht[1] 设置为 ht[0] 然后在 ht[1] 新创建一个空白哈希表为下一次 rehash 做准备

在满足任意条件时会自动对哈希表执行扩展操作：
- 服务器目前没有在执行 BGSAVE 命令或者 BGREWRITEAOF 命令，并且哈希表的负载因子大于等于 1
- 服务器目前正在执行 BGSAVE 命令或者 BGREWRITEAOF 命令，并且哈希表的负载因子大于等于 5

负载因子可以通过公式 ```load_factor=ht[0].used/ht[0].size``` 计算得出，当哈希表的负载因子小于 0.1 时会自动开始对哈希表执行收缩操作。

rehash 过程中将 ht[0] 中的键值对 rehash 到 ht[1] 不是一次性完成的，而是渐进式的完成的：
- 为 ht[1] 分配空间，让字典同时持有 ht[0] 和 ht[1] 两个哈希表
- 在字典中维持着一个索引计数器变量 rehashidx 并设置为 0 表示 rehash 工作正式开始
- 在 rehash 期间，每次对字典执行添加、删除、查找和更新操作时除了执行指定的操作外，还需要将 ht[0] 哈希表在 rehashidx 索引上的所有键值对 rehash 到 ht[1]，当 rehash 工作完成后 rehashidx 的值加 1
- 当 ht[0] 的所有键值对都被 rehash 到 ht[1]，这时 rehashidx 的值设置为 -1，表示 rehash 操作完成

在渐进 rehash 过程中，字典的删除、查找、更新等操作都需要在两个哈希表上进行；新增的键值对会保存到 ht[1] 中，这样保证 ht[0] 包含的键值对随着 rehash 操作的执行而最终变为空

### 跳跃表
跳跃表(skiplist)通过在每个节点中维持多个指向其他节点的指针，从而达到快速访问节点的目的。跳跃表支持平均 O(logN) 最坏 O(N) 复杂度的节点查找。

Redis 使用跳跃表作为有序集合键的底层实现，由 zskiplistNode 和 zskiplist 两个结构定义，其中 zskiplistNode 用于表示跳跃表节点，zskiplist 用于保存跳跃表节点的相关信息。
#### 跳跃表节点
```c
typedef struct zskiplistNode{
    struct zskiplistLevel{
        struct zskiplistNode *forward;
        unsigned int span;
    }level[];
    struct zskiplistNode *backward;
    double score;
    robj *obj;
}zskiplistNode;
```
##### 层
跳跃表节点的 level 数组可以包含多个元素，每个元素都包含一个指向其他节点的指针，通过这些层可以加快访问其他节点的速度。一般来说，层的数量越多，访问其他节点的速度就越快。

每次创建一个新跳跃表节点的时候，根据幂次定律(越大的数出现的概率越小)随机生成一个介于 1 和 32 之间的值作为 level 数组的大小，这个大小就是层的“高度”。
##### 前进指针
每个层都有一个指向表尾方向的前进指针(level[i].forward 属性)，用于从表头向表尾方向访问节点。

从表头向表尾方向，遍历跳跃表种所有节点：
- 首先访问跳跃表的第一个节点(表头)，然后根据层的前进指针和跨度移动到表中的下一个节点
- 当移动的指针指向 null 时表明到达了跳跃表的表尾，于是结束遍历
##### 跨度
跨度(level[i].span 属性)用于记录两个节点之间的距离：
- 两个节点之间的跨度越大表示相距得越远
- 指向 null 的所有前进指针的跨度都是 0
##### 后退指针
节点的后退指针(backward)用于从表尾向表头方向访问节点。和前进指针不同，每个节点只有一个后退指针，所以每次只能后退至前一个节点。
##### 分值和成员
节点的分值(socre 属性)是一个 double 类型的浮点数，跳跃表中的所有节点都按照分值从小到大来排序。

节点的成员对象(obj 属性)是一个指针，它指向一个字符串对象，而字符串对象则保存着一个 SDS 值。

在同一个跳跃表中，各个节点保存的成员对象必须是唯一的，但是不同节点保存的分值可以是相同的，分值相同的节点将按照成员对象在字典序中的大小来进行排序，成员对象较小的节点会排在前面(靠近表头方向)，而成员对象较大的节点则会排在后面(靠近表尾的方向)。

#### 跳跃表
使用一个 zskiplist 结构持有跳跃表节点可以方便地对整个跳跃表进行处理，比如快速访问跳跃表的表头节点和表尾节点，快速获取跳跃表节点的数量等信息。zskiplist 结构定义：
```c
typedef struct zskiplist{
    struct zskiplistNode *header, *tail;
    unsigned long length;
    int level;
}zskiplist;
```
- header 和 tail 指针分别指向跳跃表的表头和表尾节点，通过这两个指针使得定位表头节点和表尾节点的复杂度为 O(1)
- length 属性记录节点的数量，可以在 O(1) 复杂度内返回跳跃表的长度
- level 属性用于在 O(1)复杂度内获取跳跃表中层高最大的那个节点的层数量，表头节点的层高并不计算在内
### 整数集合
整数集合(intset)是集合键的底层实现，Reids 使用整数集合保存类型为 int16_t, int32_t 或者 int64_t 的整数值，并且保证集合中不会出现重复元素。

整数集合使用 intset 结构表示：
```c
typedef struct intset{
    uint32_t encoding;
    uint32_t length;
    int8_t contents[];
}intset;
```
- contents 数组是整数集合的底层实现：整数集合的每个元素都是 contents 数组中的一个数组项(item)，各个项在数组中按值的大小从小到大有序地排列，并且数组中不包含任何重复项
- length 属性记录了整数集合包含的元素数量，也就是 contents 数组的长度
- encoding 属性决定 contents 数组保存的真实数据类型：
  - INTSET_ENC_INT16 表示 contents 数组里每个项都是一个 int16_t 类型的整数值
  - INTSET_ENC_INT32 表示 contents 数组里每个项都是一个 int32_t 类型的整数值
  - INTSET_ENC_INT64 表示 contents 数组里每个项都是一个 int64_t 类型的整数值

#### 升级
当添加新元素到整数集合且新元素的类型比现有元素的集合类型都要长时，整数集合需要先进行升级(upgrade)，然后才能将新元素添加到整数集合里面。升级整数集合并添加新元素分为三步：
- 根据新元素的类型，扩展整数集合底层数组的空间大小，并为新元素分配空间
- 将底层数组现有的所有元素都转换成与新元素相同的类型，并将类型转换后的元素放置到正确的位置上，在放置的过程中需要保持底层数组的有序性质不变
- 将新元素添加到底层数组里面，如果新元素长度小于现在所有元素则放置在底层数组的最开头(索引为 0)，如果新元素的长度大于所有元素则放置在底层数组的最末尾(索引为 length-1)

每次向整数集合添加新元素都有可能会引起升级，每次升级需要对底层数组中已有的所有元素进行类型转换，因此向整数集合添加新元素的时间复杂度为 O(N)

整数升级的策略有两个好处：
- 提升灵活性：C 语言静态类型语言，不允许不同数据类型使用同一个数据结构。使用升级策略可以使多种不同类型的数值存储在同一个数据结构里面，从而避免了类型错误
- 节约内存：升级策略只有在新元素类型比原有数据类型长时才会升级，这样可以尽可能的节省内存

整数集合不支持降级操作，一旦对底层数组进行了升级操作，encoding 就会一直保持升级后的状态。


**[Back](../)**