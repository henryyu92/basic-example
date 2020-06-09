## 数据结构
Redis 中的数据是以 K-V 的模型存储的，Redis 支持 String, List, Set, SortedSet, Hash, HyperLogLog, Bitmap 等多种数据类型，这些数据类型都是由底层的数据结构组成。

### 简单动态字符串
简单动态字符串(Simple Dynamic String, SDS) 是 Redis 底层基础的数据结构，用于存储字符串和整形数据。SDS 兼容 C 语言标准字符串处理函数，并在此基础上保证了二进制安全。

> C 语言中字符串是以 \0 结尾，如果字符串中包含 \0 就会被截断，即非二进制安全。Redis 使用 len 判断字符串是否结束因此是二进制安全的。

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

压缩列表 (ZipList) 本质上是一个字节数组，是 Redis 为了节约内存而设计的一种线性数据结构，ZipList 包含多个节点 (Entry) ，每个 Entry 可以是一个字节数组或一个整数。Redis 的有序集合、Hash、列表等数据类型的底层实现都直接或间接的使用了 ZipList。

zlbytes|zltail|zllen|entry|entry|...|zlend
-|-|-|-|-|-|-

压缩列表是一块连续内存，其各个组成部分为：
- ```zlbytes```：压缩列表占用的内存字节数，在对压缩列表进行内存重分配或者计算 zlend 时使用，占 4 个字节
- ```zltail```：ZipList 最后一个 Entry 相对于压缩列表的起始地址的偏移量(字节数)，占 4 个字节。无需遍历整个压缩列表可以确定表尾节点的地址，从而使得 push 和 pop 操作的时间复杂度为 O(1)
- ```zllen```：压缩列表包含的 Entry 数量，占 2 个字节，当压缩列表中的 Entry 超过 2^16-1 需要遍历整个压缩列表才能获取到元素个数
- ```entry```：压缩列表的节点，可以是字节数组或者整数，节点的长度由节点保存的内容决定
- ```zlend```：标记压缩列表的末端，固定为 0xFF

压缩列表中的节点可以保存字节数组或者整数，保存的数据都是经过了特殊的编码，编码的结构为：

previous_entry_length|encoding|content
-|-|-

- ```previous_entry_length```：前一个 Entry 的长度，占用 1 个或者 5 个字节。如果前一个 Entry 小于 254 字节时占用 1 个字节，如果前一个 Entry 大于 254 则占用 5 个字节，其中第一个字节是 0xFE，后面四个字节存储具体大小
- ```encoding```：Entry 的编码，表示 content 存储的数据类型，占用 1 个或者 5 个字节
- ```content```：Entry 保存的值，可以是字节数据或者整数，值的数据类型和长度由 encoding 决定

Redis 使用结构体 zlentry 缓存解码后的压缩列表元素：
```c
typedef struct zlentry {

    // previous_entry_length 占用的字节数
    unsigned int prevrawlensize;

    // previous_entry_length 存储的内容
    unsigned int prevrawlen;

    // encoding 占用的字节数
    unsigned int lensize;

    // 存储数据占用的字节数
    unsigned int len;
    // 存储数据的数据类型
    unsigned char encoding;

    // previous_entry_length + encoding 占用的字节数
    unsigned int headersize;

    // Entry 的首地址
    unsigned char *p;
}zlentry;
```

压缩列表是一个连续的内存块，节点的更新会使 ```previous_entry_length``` 属性占用的字节数变化而需要重新分配空间，而前一个 Entry 的 ```previous_entry_length``` 的变化引起后面 Entry 的 ```previous_entry_length``` 字节数变化再次引起空间重新分配，这种情况为连锁更新。

连锁更新在最坏的情况下需要对压缩列表执行 N 次空间重分配操作，而每次空间重分配的最坏复杂度为 O(N)，因此连锁更新的最坏复杂度为 O(N^2)。实际情况中连锁更新发生的可能性较小，且连锁更新节点数量不多对性能不会造成很大影响，实际中对 ziplist 操作的平均时间复杂度为 O(N) 


### quickList

quickList 由 List 和 ZipList 结合而成，是 Redis 中 List 数据类型的底层实现。考虑到双向链表在保存大量数据时需要更多额外内存保存指针并容易产生大量内存碎片，以及 ziplist 的插入和删除的高时间复杂度，Redis 将双向链表和 ziplist 结合成为 quicklist。

quickList 是一个双向链表，链表中的每个节点是 ziplist 结构，因此可以将 quickList 看作是用双向链表将若干个小型的 ziplist 连接到一起组成的数据结构。

```c
typedef struct quicklist{
    // 首节点
    quicklistNode *head;
    // 尾节点
    quicklistNode *tail;
    // 元素个数，可由 list-max-ziplist-size 设置
    unsigend long count;
    // 节点个数
    unsigend int len;
    // ziplist 的长度
    int fill:16;
    // 压缩程度，可由参数 list-compress-depth 设置
    unsigned int compress:16;
}quicklist;
```
quickListNode 是 quickList 中的一个节点，节点包含了 ziplist 数据结构：

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

quickList 中的操作需要首先从双向链表中找到对应的 ZipList，对于数据量不大的情况 ziplist 操作的时间复杂度为 O(1)，双向链表获取头尾节点的时间复杂度也为 O(1)，因此 quickList 的 push 和 pop 操作的时间复杂度为 O(1)。


### 字典

字典是一种用于保存键值对的抽象数据结构，可以根据键以 O(1) 的时间复杂度取出或插入键值。

Redis 字典实现依赖的数据结构主要包含三部分：字典、Hash 表、Hash 表节点。其中哈希表由 dictht 结构定义：
```c
typedef struct dictht{
    // 每个元素指向一个 Hash 表节点
    dictEntry **table;
    // Hash 表的节点数量
    unsigned long size;
    //用于计算键对应的 Hash 表节点，值为 size-1
    unsigned long sizemask;
    // 哈希表中已存在的节点数
    unsigned long used;
}dictht;
```
哈希表节点使用 dictEntry 结构表示，每个 dictEntry 结构都保存着一个键值对：
```c
typedef struct dictEntry {
    // 键
    void *key;
    // 值，可以是指针、uint64_t 整数或 int64_t 整数
    union{
        void *val;
        uint64_t u64;
        int64_t s64;
    }v;
    // 下一个哈希表节点的指针，使用单链表解决哈希冲突
    struct dictEntry *next;
}dictEntry;
```
Redis 字典的实现是对 Hash 表的封装，其数据结构用 dict 表示：
```c
typedef struct dict {
    // 指向 dictType 结构的指针，每个 dictType 结构保存了一簇用于操作特定类型键值对的函数
    // Redis 会为用途不同的字典设置不同的类型特定函数
    dictType *type;

    // 保存了需要传给类型特定函数的可选参数
    void *privdata;

    // Hash 表数组，每次只使用其中一个，另一个用于 rehash
    dictht ht[2];

    // 没有进行 rehash 时为 -1，否则记录 rehash 的进度
    int rehashidx;
}dict;
```
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

#### 跳跃表

跳跃表是一个分层的有序链表，每层都有指向下一个节点的指针，因此跳跃表的查询性能可以达到 O(lgN)。跳跃表由多个节点组成，Redis 中的跳跃表节点使用 zskiplistNode 结构体定义：
```c
typedef struct zskiplistNode {
    // 存储字符串类型的数据
    sds els;
    // 排序的分值
    double score;
    // 后退指针，只能指向当前节点的前一个节点
    struct zskiplistNode *backward;
    // 跳跃表的层，每个节点上的层数随机
    struct zskiplistLevel {
        // 指向本层下一个节点，尾节点的 forward 指针指向 NULL
        struct zskiplistNode *forward;
        // forward 节点与当前接待你之间的元素个数
        unsigned int span;
    } level[];
}zskiplistNode;
```
Redis 使用 zskiplist 结构体实现跳跃表结构，zskiplist 中的节点即为 zskiplistNode：
```c
typedef struct zskiplist{
    // 跳跃表的头、尾节点
    struct zskiplistNode *header, *tail;
    // 跳跃表长度
    unsigned long length;
    // 跳跃表的高度，即所有节点中层数的最大值
    int level;
}zskiplist;
```
通过跳跃表的属性可以在 O(1) 的时间复杂度内获取到头、尾节点以及跳跃表的高度。由于底层使用了跳跃表结构，所以 Redis 中 ZSet 数据结构操作的时间复杂度一般为 O(lgN)。

跳跃表中各个节点保存的成员对象必须是唯一的，但是不同节点保存的分值可以是相同的，分值相同的节点将按照成员对象在字典序中的大小来进行排序，成员对象较小的节点会排在前面(靠近表头方向)，而成员对象较大的节点则会排在后面(靠近表尾的方向)。

### 整数集合

整数集合(intset)是一个有序的、存储整形数据的结构。Reids 使用整数集合保存类型为 int16_t, int32_t 或者 int64_t 的整数值，并且保证集合中不会出现重复元素。

整数集合使用 intset 结构表示：
```c
typedef struct intset {
    // 编码类型，不同的编码决定 contens 中保存不同类型的值
    uint32_t encoding;
    // 元素的个数，即 contents 的长度
    uint32_t length;
    // 存储整形数据的数组，数据从小到大排序，不包含重复项
    int8_t contents[];
}intset;
```


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