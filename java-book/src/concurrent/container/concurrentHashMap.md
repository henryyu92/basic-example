## HashMap

HashMap 是基于 k-v 存储的容器，其中 key 和 value 都可以为 null，HashMap 不能保证放入元素的顺序，也不能保证多线程条件下的并发安全。

HashMap 底层存储结构采用数组和红黑树(或者链表)，数据插入和读取时先将 key 进行 hash 运算得到对应的位置，然后从将数据插入位置上的红黑树或者从对应位置的红黑树中读取数据。HashMap 插入数据时如果链表的长度超过 8 则会将链表转换为红黑树。

HashMap 定义了负载因子，当数组中存储数据的比例超过负载因子则会触发数组扩容。HashMap 扩容是将组数扩大为原来的 2 倍，然后将原来数据上的数据全部重新插入新的数组。

### Get

`get` 方法返回 key 对应的 value，如果不存在则返回 null。`get` 方法获取 key 对应 value 的逻辑在 `getNode` 方法中实现，主要流程为：

- 根据 key 的 hash 值定位数组的位置。数组定位通过取模的方式，因为 hashMap 的数组长度为 2 的幂次，因此取模操作可以直接简化位操作 `(n-1) & hash`；hashMap 允许 key 为 null，此时 key 的 hash 值为 0，也就是会从数组的第一个位置查找
- 判断数组中保存的 Node 是否是需要查找的数据，如果是则直接返回，判断的条件是 hash 值相等并且 key 相等
- 如果数组中保存的 Node 不是需要查找的数据，则会沿着链表或者红黑树进行查找，如果遍历完没有查找到则返回 null

```java
final Node<K,V> getNode(int hash, Object key) {
    Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (first = tab[(n - 1) & hash]) != null) {
        if (first.hash == hash && // always check first node
            ((k = first.key) == key || (key != null && key.equals(k))))
            return first;
        if ((e = first.next) != null) {
            if (first instanceof TreeNode)
                return ((TreeNode<K,V>)first).getTreeNode(hash, key);
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

`put` 操作将指定的 key 和指定的 value 存储到 map 中并返回旧的 value，如果 key 对应的 value 已经存在则直接覆盖。`put` 方法的实现在 `putVal` 方法中，主要流程为：

- 如果数组还未初始化，则初始化数组，数组的初始化长度为 16
- 根据 hash 值定位数组对应的位置，如果数组对应位置为 null，则创建新的 Node 并返回
- 判断数组对应位置的 Node 是否是 key 对应的节点，如果是则直接覆盖值，否则继续查找
- 沿着红黑树或者链表查找，如果查找到则直接覆盖值，否则创建新的节点

- 如果是链表创建新节点后需要判断节点数是否小于 8，如果小于则需要调用 `treeifyBin` 方法尝试转换成红黑树，`treeifyBin` 方法会判断数组长度是否小于 64，如果小于则会调用 `resize` 方法对数组扩容，否则将链表转换为红黑树

### Remove

`Remove` 操作删除给定 key 对应的 Node，并返回旧的值，如果不存在则返回 null。`remove` 操作的实现在 `removeNode` 方法中，主要流程为：

- 利用查找流程查找到 key 对应的 Node
- 如果 Node 在红黑树上，则删除红黑树节点；如果 Node 在链表上则删除链表节点

### Resize

`Resize` 操作用于初始化数组或者数组扩容，初始化时会创建大小为 16，负载因子为 0.75 的数组，当数组的节点数达到阈值后会进行扩容，扩容时创建 2 倍大小的新数组并且将旧数组中的节点移动到新的数组。

由于是两倍扩容并且数组长度是 2 的幂次，因此根据 hash 算法扩容后再次 hash 只有两种可能，因此将索引位置的元素根据 ```hash&oldCap``` 是否为 0 分为两部分，为 0 的部分表示扩容后再次 hash 之后还是在当前索引处，所以数据不需要移动，只需要调整指针即可；为 0 的部分表示扩容后再次 hash 之后索引位置为当前索引位置加扩容前的容量即 ```index+oldCap```，这部分需要移动。

对于索引位置为红黑树结构来说，在将元素分为两部分时判断元素的个数是否到达阈值 ```UNTREEIFY_THRESHOLD```，默认是 6，如果到达了则需要将红黑树结构转换成链表结构。

```java
do {
	next = e.next;
    // 原位置，不需要移动
	if ((e.hash & oldCap) == 0) {
		if (loTail == null)
			loHead = e;
		else
			loTail.next = e;
		loTail = e;
	}
    // 需要移动到 index + oldCap 位置
	else {
		if (hiTail == null)
			hiHead = e;
		else
			hiTail.next = e;
		hiTail = e;
	}
} while ((e = next) != null);
// 移动拆分的链表到新的数组中
if (loTail != null) {
    loTail.next = null;
    newTab[j] = loHead;
}
if (hiTail != null) {
    hiTail.next = null;
    newTab[j + oldCap] = hiHead;
}
```



## ConcurrentHashMap

ConcurrentHashMap 是 HashMap 的线程安全实现，ConcurrentHashMap 依然采用数组加链表和红黑树的数据结构，链表到红黑树的转换阈值依然是 8，ConcurrentHashMap 依然采用双倍扩容的方式扩容数组，只是在扩容的时候采用了机制保证并发安全。

ConcurrentHashMap 的 key 和 value 都不允许为 null。ConcurrentHashMap 定义了几个常量

```java
// 控制数组的初始化以及扩容
// -1 表示数组正在初始化， -(1+n) 表示有 n 个线程正在执行扩容
// 如果数组为 null 则表示数组初始化大小，如果数组初始化完成则表示数组的容量，默认是数组大小的 0.75
private transient volatile int sizeCtl;
```

### InitTable

`IniiTable` 方法用于初始化数组，初始化时会检查 `sizeCtl` 变量，如果值为负数则表示其他线程正在执行初始化或者扩容，于是当前线程需要让出调度从而使得其他线程能够完成初始化或者扩容，否则使用 CAS 的方式将 `sizeCtl` 设置为 -1 表示当前线程正在执行初始化。

```java
private final Node<K,V>[] initTable() {
    Node<K,V>[] tab; int sc;
    while ((tab = table) == null || tab.length == 0) {
        // sizeCtl < 0 表示有其他线程在进行初始化或扩容操作
        if ((sc = sizeCtl) < 0)
            Thread.yield(); // lost initialization race; just spin
        // CAS 设置共享变量成功表示由该线程初始化 table，其他线程需要自旋
        else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
            try {
                if ((tab = table) == null || tab.length == 0) {
                    // 初始大小为 16
                    int n = (sc > 0) ? sc : DEFAULT_CAPACITY;
                    @SuppressWarnings("unchecked")
                    Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n];
                    table = tab = nt;
                    sc = n - (n >>> 2);
                }
            } finally {
                // 初始化完成保存 table 的容量，默认是 table 大小的 0.75 即 (n - (n >>> 2))
                sizeCtl = sc;
            }
            break;
        }
    }
    return tab;
}
```

### Get

ConcurrentHashMap 获取元素的流程和 HashMap 类似，先通过 hash 值计算数组索引位置，然后判断索引位置的数据结构，如果为红黑树则使用红黑树结果查询，如果是链表则使用链表结构查询。

ConcuurentHashMap 获取元素的整个流程并没有加锁，因为数组中如果是链表结构则由于 val 和 next 都是 volatile 修饰的，其他线程添加了元素或者修改了元素是立即可见的，如果是红黑树结构则在 find 方法中增加了对 lockState 的判断，因此只会锁住当前位置而其他位置不受影响。

```java
public V get(Object key) {
    Node<K,V>[] tab; Node<K,V> e, p; int n, eh; K ek;
    // 计算 hash 值，算法为 h^(h>>>16)
    int h = spread(key.hashCode());
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (e = tabAt(tab, (n - 1) & h)) != null) {
        // 数组索引位置即为需要查找的元素，直接返回
        if ((eh = e.hash) == h) {
            if ((ek = e.key) == key || (ek != null && key.equals(ek)))
                return e.val;
        }
        // 非链表结构
        else if (eh < 0)
            return (p = e.find(h, key)) != null ? p.val : null;
        while ((e = e.next) != null) {
            if (e.hash == h &&
                ((ek = e.key) == key || (ek != null && key.equals(ek))))
                return e.val;
            }
        }
        return null;
    }
}
```

### Put

ConcurrentHashMap 的 put 操作核心思想依然是根据 key 的 hash 值计算节点插入 table 的位置，如果该位置为空则直接插入，否则插入到链表或者红黑树中，如果 table 负载超过阈值则进行扩容和 rehash 过程。

`ConcurrentHashMap` 的 `put` 操作是在 `putVal` 方法中实现。`putVal` 方法以死循环的方式执行添加元素的动作，如果数组正在执行扩容并且当前索引位置正在执行迁移则需要等待扩容完成后将元素添加到新的数组中，其主要流程为：

- 判断数组是否为空，如果为空则调用 `initTable` 方法初始化数组
- 通过 hash 值定位到数组的索引位置，如果该位置没有元素则创建 Node 并以 CAS 的方式添加
- 如果索引位置存在 Node 并且该 Node 的 hash 值为 -1 则表示数组正在扩容，当前线程调用 `helpTransfer` 方法协助数组扩容，扩容完成后 tab 引用新的数组
- 数组索引位置可以添加，则当前线程会通过 `synchronized` 对索引位置的 Node 加锁，然后根据索引位置的 Node 判断是链表还是红黑树，如果 Node 的 hash 值大于等于 0 则表示是链表，则创建链表结点加入链表；如果是 `TreeBin` 则表示是红黑树，于是将元素添加到红黑树中
- 如果是链表结构则在添加元素之后需要判断结点数是否超过 8，如果超过则调用 `treeifyBin` 方法尝试将链表结构转换成红黑树
- 添加元素之后调用 `addCount` 将 `ConcurrentHashMap` 的元素个数 +1，增加元素时如果数组正在扩容也会帮助扩容

`pub` 操作会对数组中的单个索引位置加锁，如果该索引位置正在执行添加操作时，数组对该索引位置的迁移会阻塞直到添加操作完成；同理如果执行添加操作时节点正在迁移则添加动作阻塞直到迁移完成，此时 `tabAt(tab, i) == f` 将不再成立，添加操作会重新循环执行添加

`put` 操作只会对数组中的单个索引位置加锁，因此其他索引位置的操作不会受到影响。

#### HelpTransfer

`helpTransfer`  方法是辅助方法，在添加元素或者删除元素时如果数组正在扩容则调用该方法协助数组扩容。

```java
final Node<K,V>[] helpTransfer(Node<K,V>[] tab, Node<K,V> f) {
    Node<K,V>[] nextTab; int sc;
    // 数组结点为 ForwardingNode 表示索引位置的节点已经迁移完成
    if (tab != null && (f instanceof ForwardingNode) &&
        (nextTab = ((ForwardingNode<K,V>)f).nextTable) != null) {
        int rs = resizeStamp(tab.length);
        while (nextTab == nextTable && table == tab &&
               (sc = sizeCtl) < 0) {
            if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 ||
                sc == rs + MAX_RESIZERS || transferIndex <= 0)
                break;
            if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1)) {
                transfer(tab, nextTab);
                break;
            }
        }
        return nextTab;
    }
    return table;
}
```



#### TreeifyBin

`TreeifyBin` 方法尝试将链表转换成红黑树结构，如果当前数组的长度小于 64，则不会转换成红黑树而是对数组进行扩容。

```java
private final void treeifyBin(Node<K,V>[] tab, int index) {
    Node<K,V> b; int n, sc;
    if (tab != null) {
        if ((n = tab.length) < MIN_TREEIFY_CAPACITY)
            // 扩容数组
            tryPresize(n << 1);
        else if ((b = tabAt(tab, index)) != null && b.hash >= 0) {
            synchronized (b) {
                // 双重检锁并将链表节点转换为红黑树结点
                if (tabAt(tab, index) == b) {
                    TreeNode<K,V> hd = null, tl = null;
                    for (Node<K,V> e = b; e != null; e = e.next) {
                        TreeNode<K,V> p =
                            new TreeNode<K,V>(e.hash, e.key, e.val,
                                              null, null);
                        if ((p.prev = tl) == null)
                            hd = p;
                        else
                            tl.next = p;
                        tl = p;
                    }
                    // 将 TreeNode 添加到红黑树
                    setTabAt(tab, index, new TreeBin<K,V>(hd));
                }
            }
        }
    }
}
```

#### AddCount



### Remove

`remove` 操作将 key 对应的 Node 删除，如果 `ConcurrentHashMap` 中不存在对应的元素则直接返回。`remove` 操作的实现在 `replaceNode` 方法中， 其流程为：

- 根据 hash 值定位到数组对应的索引位置，如果找不到则直接返回 null
- 如果索引位置结点的 hash 值为 -1 表示数组正在扩容，则当前线程调用 `helpTransfer` 方法协助扩容，扩容完成后返回 null
- 对索引位置的 Node 加锁，根据 Node 判断是链表结构还是红黑树结构。如果 Node 的 `hash` 大于等于 0 则表示是链表，则删除链表对应的节点；如果 Node 是 `TreeBin` 则表示是红黑树，于是删除红黑树对应的结点
- 删除结点后需要调用 `addCount` 将 `ConcurrentHashMap` 的数量 -1



### Transfer

`transfer` 方法实现了数组的扩容，并将原数组上的元素拷贝到新的数组上。`ConcurrentHashMap` 采用 CAS 实现了多线程并发扩容，每个线程负责指定的区间。

`transfer` 方法在扩容时首先会为每个 cpu 内核分配需要处理的桶，保证每个 cpu 内核处理的桶不少于 16 个，然后初始化新的数组 nextTab 使得其容量为原始数组的 2 倍：

```java
int n = tab.length, stride;
// stride 表示每个 cpu 分配到的桶数
if ((stride = (NCPU > 1) ? (n >>> 3) / NCPU : n) < MIN_TRANSFER_STRIDE)
    stride = MIN_TRANSFER_STRIDE; // subdivide range
// 初始化新的数组
if (nextTab == null) {            // initiating
    try {
        // 新数组长度为旧数组的 2 倍
        Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n << 1];
        nextTab = nt;
    } catch (Throwable ex) {      // try to cope with OOME
        sizeCtl = Integer.MAX_VALUE;
        return;
    }
    nextTable = nextTab;
    transferIndex = n;
}
```

`transfer` 方法中完成新数组的初始化后以死循环的方式复制旧数组中的元素到新数组中，在开始复制数组前需要确定当前线程负责的桶，采用 cas 的方式将 `transferIndex` 修改为下一个线程的区间起始位置：

```java
Node<K,V> f; int fh;
// advance 为 false 表示当前线程对应的桶已经确定
while (advance) {
    int nextIndex, nextBound;
    if (--i >= bound || finishing)
        advance = false;
    else if ((nextIndex = transferIndex) <= 0) {
        i = -1;
        advance = false;
    }
    // 以 cas 的方式将 transferIndex 修改为下一个线程的起始位置，修改失败则说明多个线程在分配桶
    else if (U.compareAndSwapInt
             (this, TRANSFERINDEX, nextIndex,
              nextBound = (nextIndex > stride ?
                           nextIndex - stride : 0))) {
        bound = nextBound;
        i = nextIndex - 1;
        advance = false;
    }
}
```

确定每个线程扩容时负责的桶之后，`transfer` 方法开始执行元素的复制迁移。

- 如果数组索引位置为 null 则直接插入 `ForwardingNode` 表示当前位置的元素已经迁移完成
- 如果数组索引位置节点的 hash 为 -1 表示其他线程已经处迁移完成，否则需要迁移当前索引位置的节点
- 对索引位置的节点加锁，然后处理索引位置上的链表或者红黑树
  - 如果是链表节点则遍历链表根据 `(hash & n)` 为 0 或者为 1 将节点构成两个新的反序链表，然后将值为 0 的链表的头节点添加到 `i` 位置(和节点在原数组的位置相同)，将值为 1 的链表的头节点添加到 `i+n` 位置，最后将原数组的索引位置的节点修改为 `ForwardingNode` 表示当前索引位置已经迁移完成
  - 如果是红黑树节点，则遍历树结点并根据 `hash & n` 为 0 或者 1 将树结点构造成两个新的红黑树结构，然后和链表同样的规则将树的头结点添加到指定的索引位置，不同的是红黑树结构在添加到索引位置之前需要判断是否需要将红黑树结构转换为链表结构，如果需要则通过 `untreeify` 方法转换
- 所有节点复制完成后将 table 指向 nextTable，同时更新 sizeCtl=nextTable * 0.75，完成扩容操作

