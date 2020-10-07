## HashMap
HashMap 基于 Map 接口实现，key 和 value 都可以为 null，HashMap 不能保证放入元素的顺序。HashMap 是线程不安全的。

HashMap 是基于数据和红黑树实现，数据插入和读取时先将 key 进行 hash 运算得到对应的位置，然后从将数据插入位置上的红黑树或者从对应位置的红黑树中读取数据。

HashMap 定义了负载因子，当数组中存储数据的比例超过负载因子则会触发数组扩容。HashMap 扩容是将组数扩大为原来的 2 倍，然后将原来数据上的数据全部重新插入新的数组。

HashMap 插入数据到对应的桶时，如果桶中的数据小于 8 则使用链表结构，如果超过 8 则使用红黑树结构。

```java
// 默认的初始数组大小
static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16
// 数组大小的最大值
static final int MAXIMUM_CAPACITY = 1 << 30;

// 默认的负载因子，当数组存储数据比例超过负载因子则触发数组扩容
static final float DEFAULT_LOAD_FACTOR = 0.75f;

// 链表转换成红黑树的阈值，超过阈值将链表转换为红黑树
static final int TREEIFY_THRESHOLD = 8;

// 扩容转移数据时如果红黑树结构数据量小于阈值，则转换为链表结构
static final int UNTREEIFY_THRESHOLD = 6;

```
HashMap 中的数组存放的是链表或者红黑树，其节点由内部类 Node 以及其子类 TreeNode 表示：
```java
static class Node<K, V> implements Map.Entry<K, V> {
    // 节点 hash 值
    final int hash;
    final K key;
    V value;
    Node<K, V> next;

    // ...
}

static class TreeNode<K, V> extends LinkeHashMap.Entry<K, V> {
    TreeNode<K, V> parent;
    TreeNode<K, V> left;
    TreeNode<K, V> right;
    boolean red;

    // ...
}
```

### Hash

HashMap 读取或者插入数据前需要对 key 做 hash 运算确定桶的位置。HashMap 的 hash 算法是将 key 的 hashCode 的高 16 位与低 16 位做异或运算：
```java
static final int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```
HashMap 使用 ```hash&(len-1)``` 确定数据的索引位置，当 len 为 2 的幂次时等同于 ```hash%len```，相比取模运算，位与运算效率会高得多。hash 算法将 hashCode 的高 16 参与计算是为了在数组长度小于 16 时做到尽可能的散列。

当扩容时，由于 len 是 2 的幂次，所以 ```hash&(len-1)``` 的结果要么是当前索引，要么是当前索引加上扩容前的长度，红黑树转移数据时根据 ```hash&len``` 是否为 0 将整个树分裂成两部分为，为 0 的部分表示扩容后的仍然是当前索引，不为 0 的部分表示扩容后的索引为当前索引加上扩容前的长度。

### Get
HashMap 的查找流程比较简单，先计算 key 的 hash 值，然后通过 ```getNode``` 方法查找对应的值，在查找时由于索引位置有可能是红黑树结构也有可能是链表结构，所以第一个位置要首先判断。

```java
final Node<K,V> getNode(int hash, Object key) {
    Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
    // Node 数组索引位置不为 null
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (first = tab[(n - 1) & hash]) != null) {
        // 索引位置第一个节点是查找的就直接返回
        if (first.hash == hash && // always check first node
            ((k = first.key) == key || (key != null && key.equals(k))))
            return first;
        if ((e = first.next) != null) {
            // 红黑树查找
            if (first instanceof TreeNode)
                return ((TreeNode<K,V>)first).getTreeNode(hash, key);
            // 链表查找
            do {
                if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k))))
                    return e;
            } while ((e = e.next) != null);
        }
    }
    return null;
}
```

### Put

HashMap 的插入操作比较复杂，在插入前需要判断 Node 数组是否初始化，然后在插入的过程中需要判断是否已经存在相同 key 的元素，如果存在则覆盖元素的值。

HashMap 在向桶中插入元素时如果是链表结构则直接插入到链表尾部，然后判断是否达到阈值，如果达到则需要将桶中的元素转换成红黑树结构。

```java
final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                boolean evict) {
    Node<K,V>[] tab; Node<K,V> p; int n, i;
    // 数组未初始化则调用扩容方法初始化
    if ((tab = table) == null || (n = tab.length) == 0)
        n = (tab = resize()).length;
    // 索引位置为 null 则直接创建新的 Node 节点插入
    if ((p = tab[i = (n - 1) & hash]) == null)
        tab[i] = newNode(hash, key, value, null);
    else {
        Node<K,V> e; K k;
        // 索引位置节点有相同的 key
        if (p.hash == hash &&
            ((k = p.key) == key || (key != null && key.equals(k))))
            e = p;
        // 红黑树插入
        else if (p instanceof TreeNode)
            e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
        // 链表插入，达到阈值则转换成红黑树结构
        else {
            for (int binCount = 0; ; ++binCount) {
                if ((e = p.next) == null) {
                    p.next = newNode(hash, key, value, null);
                    // 长度达到阈值需要转换成红黑树
                    if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                        treeifyBin(tab, hash);
                    break;
                }
                // 有相同 key 则直接跳出循环
                if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k))))
                    break;
                p = e;
            }
        }
        // 已经存在相同 key 的元素
        if (e != null) { // existing mapping for key
            V oldValue = e.value;
            if (!onlyIfAbsent || oldValue == null)
                e.value = value;
            afterNodeAccess(e);
            return oldValue;
        }
    }
    ++modCount;
    // 超过负载因子需要扩容
    if (++size > threshold)
        resize();
    afterNodeInsertion(evict);
    return null;
}
```

### Remove

HashMap 删除元素前需要确定元素的位置，如果在索引位置则直接使用下一个元素替换并返回，如果在红黑树结构上则使用红黑树结构删除元素，如果在链表上则使用链表结果后删除。

```java
final Node<K,V> removeNode(int hash, Object key, Object value,
                            boolean matchValue, boolean movable) {
    Node<K,V>[] tab; Node<K,V> p; int n, index;
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (p = tab[index = (n - 1) & hash]) != null) {
        Node<K,V> node = null, e; K k; V v;
        // 索引位置是需要删除的元素
        if (p.hash == hash &&
            ((k = p.key) == key || (key != null && key.equals(k))))
            node = p;
        else if ((e = p.next) != null) {
            // 要删除元素在红黑树上
            if (p instanceof TreeNode)
                node = ((TreeNode<K,V>)p).getTreeNode(hash, key);
            // 要删除元素在链表上
            else {
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key ||
                            (key != null && key.equals(k)))) {
                        node = e;
                        break;
                    }
                    p = e;
                } while ((e = e.next) != null);
            }
        }
        // 要删除的元素存在则删除，否则返回 null
        if (node != null && (!matchValue || (v = node.value) == value ||
                              (value != null && value.equals(v)))) {
            // 要删除的元素在红黑树上则在红黑树上删除元素
            if (node instanceof TreeNode)
                ((TreeNode<K,V>)node).removeTreeNode(this, tab, movable);
            // 要删除元素在索引位置则将下一个元素替换到索引位置
            else if (node == p)
                tab[index] = node.next;
            // 要删除元素在链表中
            else
                p.next = node.next;
            ++modCount;
            // 元素个数减一，并发时并不准确
            --size;
            afterNodeRemoval(node);
            return node;
        }
    }
    return null;
} 
```

### ReSize

HashMap 数组在初始化时或者扩容时都会调用 ReSize 过程创建新的数组，初始化时直接创建默认大小或者指定大小和负载因子的数组，扩容时创建 2 倍大小的数组并重新计算负载因子。

扩容时创建两倍大小数组后需要将元素组中的数据转移到新的数组中，由于是两倍扩容并且数组长度是 2 的幂次，因此根据 hash 算法扩容后再次 hash 只有两种可能，因此将索引位置的元素根据 ```hash&oldCap``` 是否为 0 分为两部分，为 0 的部分表示扩容后再次 hash 之后还是在当前索引处，所以数据不需要移动，只需要调整指针即可；为 0 的部分表示扩容后再次 hash 之后索引位置为当前索引位置加扩容前的容量即 ```index+oldCap```，这部分需要移动。

对于索引位置为红黑树结构来说，在将元素分为两部分时判断元素的个数是否到达阈值 ```UNTREEIFY_THRESHOLD```，默认是 6，如果到达了则需要将红黑树结构转换成链表结构。

```java
final Node<K,V>[] resize() {
    Node<K,V>[] oldTab = table;
    int oldCap = (oldTab == null) ? 0 : oldTab.length;
    int oldThr = threshold;
    int newCap, newThr = 0;
    if (oldCap > 0) {
        if (oldCap >= MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return oldTab;
        }
        // 双倍扩容
        else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                  oldCap >= DEFAULT_INITIAL_CAPACITY)
            newThr = oldThr << 1; // double threshold
        }
    else if (oldThr > 0) // initial capacity was placed in threshold
        newCap = oldThr;
    // 初始化容量
    else {               // zero initial threshold signifies using defaults
        newCap = DEFAULT_INITIAL_CAPACITY;
        newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
    }
    // 计算负载因子
    if (newThr == 0) {
        float ft = (float)newCap * loadFactor;
        newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                  (int)ft : Integer.MAX_VALUE);
    }
    threshold = newThr;
    // 创建数组
    @SuppressWarnings({"rawtypes","unchecked"})
    Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
    table = newTab;
    // 扩容之后转移数据
    if (oldTab != null) {
        for (int j = 0; j < oldCap; ++j) {
            Node<K,V> e;
            if ((e = oldTab[j]) != null) {
                oldTab[j] = null;
                // 数组索引位置只有一个元素，则只需要转移这个元素
                if (e.next == null)
                    newTab[e.hash & (newCap - 1)] = e;
                // 数组索引位置是红黑树结构，则分裂红黑树转移数据，当红黑树元素小于等于 6 时转换成链表结构
                else if (e instanceof TreeNode)
                    ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                // 数组索引位置是链表
                else { // preserve order
                    Node<K,V> loHead = null, loTail = null;
                    Node<K,V> hiHead = null, hiTail = null;
                    Node<K,V> next;
                    do {
                        // hash 冲突只有两种可能，所以分为两部分即可
                        next = e.next;
                        // 等于 0 表示扩容 hash 后还是在原索引位置
                        if ((e.hash & oldCap) == 0) {
                            if (loTail == null)
                                loHead = e;
                            else
                                loTail.next = e;
                            loTail = e;
                        }
                        // 不等于 0 表示扩容 hash 后索引为原索引加扩容前的长度
                        else {
                            if (hiTail == null)
                                hiHead = e;
                            else
                                hiTail.next = e;
                            hiTail = e;
                        }
                    } while ((e = next) != null);
                    // 等于 0 的部分在原位置
                    if (loTail != null) {
                        loTail.next = null;
                        newTab[j] = loHead;
                    }
                    // 不等于 0 的部分在 index + len 位置
                    if (hiTail != null) {
                        hiTail.next = null;
                        newTab[j + oldCap] = hiHead;
                    }
                }
            }
        }
    }
    return newTab;
}
```

**[Back](../../)**