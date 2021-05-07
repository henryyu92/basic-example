## 散列表
散列表是普通数组概念的推广，对于普通数组可以直接寻址使得能够在 O(1) 时间内访问数组中的任意位置，当实际存储的关键字数目比全部的可能关键字总数要小时采用散列表就成为直接数组寻址的一种有效替代，因为散列表使用一个长度与实际存储的关键字数目成比例的数组来存储。
### 直接寻址表
当关键字的全域 U 比较小时，可以使用一个数组(直接寻址表) T 表示动态集合，T 的大小为 U 中关键字的个数(U 中没有两个元素具有相同的关键字)，T 中的每个位置称为槽(slot) 对应全域 U 中的一个关键字。

直接寻址表的字典操作实现很简单，所有的操作时间复杂度为 O(1)：
- ```search(T, k)```：从集合中查找指定关键字的元素，直接返回 T[k] 
- ```insert(T, x)```：向集合中插入元素，在 x.key 槽处插入元素即可 T[x.key] = x 
- ```delete(T, x)```：删除集合中的元素，删除 x.key 槽的元素集合 T[x.key] = null

### 散列表
如果全域 U 很大则可能导致不能分配一个这么大的数组 T，另外当实际存储的关键字集合 K 相对较小则会导致分配给 T 的大部分空间都将浪费掉。

在散列方式下，具有关键字 k 的元素存放在槽 h(k) 中，即利用散列函数 h 计算出关键字 k 存放的槽的位置。如果散列函数将全域 U 映射到散列表 T[0,..,m-1] 上则可以将 U 大小的数组减小到 m

散列表的映射关系可能会出现不同的关键字映射到同一个槽中，这种情况称为冲突。

### 链接法
当散列表出现冲突，即不同的元素经过散列函数后映射到同一个槽中，此时可以把散列到同一槽中的所有元素都放在一个链表中。
### 开放寻址法

### 哈希函数
哈希函数性质：
- 哈希函数有无限大的输入域
- 哈希函数的输出域固定
- 传入相同的值到同一个哈希函数，返回值一定相同
- 传入不同的值到同一个哈希函数，返回值可能相同
- 输出值会尽可能的分布在输出域上

### RandomPool
设计一种结构，在时间复杂度为 O(1) 的情况下满足三个功能：
- insert(key)：将某个 key 加入到该结构，做到不重复加入
- delete(key)：将原本在结构中的某个 key 移除
- getRandom()：等概率随机返回结构中的任意一个 key

算法思想：
> 在时间复杂度为 O(1) 的情况下无重复插入和删除可以考虑使用 Hash 作为存储结构；在时间复杂度 O(1) 下随机获取 key 则需要保存随机数与 key 的对应关系

算法实现：
```java
public class RandomPool{
    private kvMap<String, Integer>;
    private vkMap<Integer, String>;
    private Integer index;

    public RandomPool(){
        kvMap = new HashMap<>();
        vkMap = new HashMap<>();
        index = Integer.valueOf(0);
    }

    public void insert(String key){
        if(!kvMap.containsKey(key)){
            kvMap.put(key, index);
            vkMap.put(index, key);
            index++;
        }
    }

    public void delete(String key){
        if(kvMap.containsKey(key)){
            int i = kvMap.get(key);
            kvMap.put(kvMap.get(index - 1), i);
            kvMap.remove(key);
            vkMap.put(i, kvMap.get(index - 1))
            vkMap.remove(index - 1)
        }
    }

    public String getRandom(){
        Random r = new Random();
        return vkMap.get(r.nextInt(index));
    }
}
```

## 哈希算法


### `Murmur`

`Murmur` 哈希是一种非加密散列函数