## ConcurrentHashMap
ConcurrentHashMap 是 HashMap 的线程安全版本实现，和 HashMap 类似，ConcurrentHashMap 使用变量 table 表示 Node 数组，使用 key 的 HashCode 来寻找存储的 index，处理哈希冲突的方式也同 HashMap 类似是在同一个 index 处形成一条链表，当链表长度超过 8 时将链表转化为一棵红黑树，从而将查询复杂度从 O(N) 降低到 O(lgN)。
```java
public class ConcurrentHashMap<K,V>{
    // table 最大容量
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    // table 默认初始化的容量
    private static final int DEFAULT_CAPACITY = 16;
    // table 的负载因子，当超过此值时需要扩容
    private static final float LOAD_FACTOR = 0.75f;
    // 链表长度超过阈值时转化成一棵红黑树
    static final int TREEIFY_THRESHOLD = 8;

    // Node 数组，put 到容器中的键值对会包装成 Node 存在 table 中
    transient volatile Node<K,V>[] table;
    // 只有在扩容时才会使用
    private transient volatile Node<K,V>[] nextTable;

    /**
    * 控制 table 初始化和扩容：
    *   当为负数时，表示 table 正在初始化或者扩容：-1 表示正在初始化，扩容时为 -(1 + 扩容线程数)
	*   当 table 为 null 时保存 table 创建时指定的大小，默认为 0；当 table 初始化后保存 table 的扩容的大小
    */
    private transient volatile int sizeCtl;

    // ...
}
```
### Node
Node 是 ConcurrentHashMap 中最核心的内部类，所有插入 ConcurrentHashMap 的数据都会包装成 Node，然后再插入链表或者红黑树：
```java
static class Node<K,V> implements Map.Entry<K,V> {
    final int hash;
    final K key;
    volatile V val;
    volatile Node<K,V> next;

    // ...
}
```
Node 的 val 和 next 属性都是 volatile 修饰，因此对节点值的修改可以立即被其他线程可以见；同理其他线程对当前节点的值和后继节点的修改也立即对当前线程可见。
### TreeNode
ConcurrentHashMap 中如果链表的长度超过阈值时会转换成红黑树，链表的转换是将节点包装成 TreeNode 放在 TreeBin 中，然后由 TreeBin 完成链表到红黑树的转换：
```java
static final class TreeNode<K,V> extends Node<K,V> {
    TreeNode<K,V> parent;  // red-black tree links
    TreeNode<K,V> left;
    TreeNode<K,V> right;
    TreeNode<K,V> prev;    // needed to unlink next upon deletion
    boolean red;

    // ...
}
```
### TreeBin
TreeBin 不持有键和值，而是持有 TreeNode 的列表，TreeBin 负责将 TreeNode 组装成红黑树：
```java
static final class TreeBin<K,V> extends Node<K,V> {
    TreeNode<K,V> root;
    volatile TreeNode<K,V> first;
    volatile Thread waiter;
    // 锁状态
    volatile int lockState;
    // values for lockState
    static final int WRITER = 1; // set while holding write lock
    static final int WAITER = 2; // set when waiting for write lock
    static final int READER = 4; // increment value for setting read lock

    // ...
}
```
#### ForwardingNode
ForwardingNode 用于 ConcurrentHashMap 扩容操作，该节点只是一个标志节点并且指向 nextTable：
```java
static final class ForwardingNode<K,V> extends Node<K,V> {
    final Node<K,V>[] nextTable;
    ForwardingNode(Node<K,V>[] tab) {
        super(MOVED, null, null, null);
        this.nextTable = tab;
    }
    // ...
}
```
### 初始化容器
ConcurrentHashMap 在 put 数据之前如果 table 还没有初始化则需要初始化，table 的初始化是调用 initTable 方法来进行的：
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
                    // 下次扩容的大小
                    sc = n - (n >>> 2);
                }
            } finally {
                // 设置共享变量为正数
                sizeCtl = sc;
            }
            break;
        }
    }
    return tab;
}
```
sizeCtl 是一个用于同步多个线程的共享变量(volatile 变量)，如果它的当前值小于 0 则说明 table 正在被某个线程初始化或者扩容，线程将会让出资源；如果 sizeCtl 不小于 0 则使用 CAS 尝试将 sizeCtl 设置为 -1，如果失败则自旋；当线程设置 sizeCtl 为 -1 并完成工作之后将其值变为正数，使得其他的线程可以尝试设置 sizeCtl 为 -1 从而可以执行后续逻辑。
### Get
在 ConcurrentHashMap 中查找键值对首先需要知道键值对存储的 table 的位置(也就是槽，每个槽中都会有一个链表或者一棵红黑树)，然后在该位置对应的链表或者红黑树上查找：
```java
public V get(Object key) {
    Node<K,V>[] tab; Node<K,V> e, p; int n, eh; K ek;
    // 根据 Key 的 hashCode 计算散列值用于定位 slot
    int h = spread(key.hashCode());
	// tab 表示 table，n 表示 table 的长度，e 表示 key 所在的槽
    if ((tab = table) != null && (n = tab.length) > 0 && (e = tabAt(tab, (n - 1) & h)) != null) {
        if ((eh = e.hash) == h) {
            if ((ek = e.key) == key || (ek != null && key.equals(ek)))
                return e.val;
        }
        // 树节点
        else if (eh < 0)
            return (p = e.find(h, key)) != null ? p.val : null;
        // 链表
        while ((e = e.next) != null) {
            if (e.hash == h && ((ek = e.key) == key || (ek != null && key.equals(ek))))
                return e.val;
        }
    }
    return null;
}
```
get 方法首先根据 key 的 hashCode 计算散列值 h，然后使用 ```(h & (length - 1))``` 计算 key 所在的槽。如果槽为空则表示不存在键值对，返回 null；如果该位置上的元素（链表头节点或者红黑树的根节点）与要查找的 key 匹配，则直接返回这个节点的值；如果该位置的 hashCode 小于 0 则说明该位置上是一棵红黑树，通过调用 Node 的 find 方法来查找到节点；如果该位置的 hashCode 大于等于 0 则表示为链表，通过遍历整个链表直到找到匹配的键值对。

get 方法的整个过程并没有加锁，是由于 table 是 volatile 修饰的，并且如果 table 的槽中是链表，Node 的 val 和 next 都是 volatile 修饰的，即在 get 过程中如果有其他线程对容器进行了修改操作，该线程可以立即得知；当 table 的槽中是红黑树时，查找操作会由 lockState 的判断，因此只会锁住当前的槽而 table 的其他槽不受影响。
#### Put
ConcurrentHashMap 的 put 操作核心思想依然是根据 key 的 hash 值计算节点插入 table 的位置，如果该位置为空则直接插入，否则插入到链表或者红黑树中，如果 table 负载超过阈值则进行扩容和 rehash 过程：
```java
public V put(K key, V value) {
    return putVal(key, value, false);
}

/** Implementation for put and putIfAbsent */
final V putVal(K key, V value, boolean onlyIfAbsent) {
    if (key == null || value == null) throw new NullPointerException();
    // 计算 hashCode 的散列值
    int hash = spread(key.hashCode());
	// 记录槽中 Node 的个数
    int binCount = 0;
    for (Node<K,V>[] tab = table;;) {
        Node<K,V> f; int n, i, fh;
        // table 为 null 则进行初始化
        if (tab == null || (n = tab.length) == 0)
            tab = initTable();
        // 如果 i 位置没有节点则直接插入，CAS 保证不需要加锁
        else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
            if (casTabAt(tab, i, null, new Node<K,V>(hash, key, value, null)))
                break;                   // no lock when adding to empty bin
        }
        // 有线程在进行扩容则先扩容
        else if ((fh = f.hash) == MOVED)
            tab = helpTransfer(tab, f);
        else {
            V oldVal = null;
            // 槽加锁
            synchronized (f) {
                if (tabAt(tab, i) == f) {
                    // 大于等于 0 表示为链表
                    if (fh >= 0) {
                        binCount = 1;
                        for (Node<K,V> e = f;; ++binCount) {
                            K ek;
							// onlyIfAbsent 为 true 表示存在则不插入，false 表示存在则覆盖
                            if (e.hash == hash && ((ek = e.key) == key || (ek != null && key.equals(ek)))) {
                                oldVal = e.val;
                                if (!onlyIfAbsent)
                                    e.val = value;
                                break;
                            }
                            Node<K,V> pred = e;
                            if ((e = e.next) == null) {
                                pred.next = new Node<K,V>(hash, key, value, null);
                                break;
                            }
                        }
                    }
                    // 树节点，按照树的插入操作进行插入
                    else if (f instanceof TreeBin) {
                        Node<K,V> p;
                        binCount = 2;
                        if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key, value)) != null {
                            oldVal = p.val;
                            if (!onlyIfAbsent)
                                p.val = value;
                        }
                    }
                }
            }
            if (binCount != 0) {
                // 链表长度超过阈值则转换为红黑树
                if (binCount >= TREEIFY_THRESHOLD)
                    treeifyBin(tab, i);
                if (oldVal != null)
                    return oldVal;
                break;
            }
        }
    }
	// 调整 Node 数组大小确定是否需要扩容
    addCount(1L, binCount);
    return null;
}
```
put 的整个流程为：
- 判断 key 和 value 是否为空，ConcurrentHashMap 的 key 和 value 都不允许为 null
- 计算 key 的 hashCode 的散列值
- 遍历 table 进行节点插入操作：
  - 如果 table 为 null 表示 ConcurrentHashMap 还没有初始化，则调用 initTable 进行初始化操作
  - 根据 hash 的值计算节点的位置 i，如果该位置为 null 则直接通过调用 casTabAt 方法插入，这个过程不需要加锁，计算位置的方法为 ```i=(n-1)&hash```
  - 如果检测到 ```fh = f.hash == -1``` 即 f 是 ForwardingNode，表示有其他线程正在进行扩容操作，则帮助线程一起进行扩容操作
  - 如果 ```f.hash >= 0``` 表示 f 是链表结构则遍历链表并插入数据；否则 f 是红黑树节点，调用 putTreeVal 方法进行插入操作。这个过程使用 synchronized 关键字对 f 加锁，但是其他位置上没有锁住，所以此时其他线程可以安全的获得 table 其他的位置来进行操作
  - 插入完成之后需要检测链表长度是否大于 TREEIFY_THRESHOLD (默认 8)，若大于则需要将链表转换为红黑树
- 调用 addCount 方法需要判断本次操作是否是更新操作，如果是更新操作则不会造成size的变化，否则如果本次 put操作是一次添加操作，那么就需要进行更新size的操作，如果在更新 size 之后发现 table 中的记录数量达到了阈值，就需要进行扩容操作
### Remove
```java
public V remove(Object key) {
	return replaceNode(key, null, null);
}

/**
 * Implementation for the four public remove/replace methods:
 * Replaces node value with v, conditional upon match of cv if
 * non-null.  If resulting value is null, delete.
 */
final V replaceNode(Object key, V value, Object cv) {
  int hash = spread(key.hashCode());
  for (Node<K,V>[] tab = table;;) {
    Node<K,V> f; int n, i, fh;
    if (tab == null || (n = tab.length) == 0 || (f = tabAt(tab, i = (n - 1) & hash)) == null)
      break;
	// 有线程在进行扩容则先扩容
    else if ((fh = f.hash) == MOVED)
      tab = helpTransfer(tab, f);
    else {
      V oldVal = null;
      boolean validated = false;
      synchronized (f) {
        if (tabAt(tab, i) == f) {
		  // 链表
          if (fh >= 0) {
            validated = true;
            for (Node<K,V> e = f, pred = null;;) {
              K ek;
              if (e.hash == hash && ((ek = e.key) == key || (ek != null && key.equals(ek)))) {
                V ev = e.val;
                if (cv == null || cv == ev || (ev != null && cv.equals(ev))) {
                  oldVal = ev;
                  if (value != null)
                    e.val = value;
                  else if (pred != null)
                    pred.next = e.next;
                  else
                    setTabAt(tab, i, e.next);
                }
                break;
              }
              pred = e;
              if ((e = e.next) == null)
                break;
            }
		  } else if (f instanceof TreeBin) {
            validated = true;
            TreeBin<K,V> t = (TreeBin<K,V>)f;
            TreeNode<K,V> r, p;
            if ((r = t.root) != null && (p = r.findTreeNode(hash, key, null)) != null) {
              V pv = p.val;
              if (cv == null || cv == pv || (pv != null && cv.equals(pv))) {
                oldVal = pv;
                if (value != null)
                  p.val = value;
                else if (t.removeTreeNode(p))
                  setTabAt(tab, i, untreeify(t.first));
              }
            }
		  }
		}
	  }
	  if (validated) {
		if (oldVal != null) {
		  if (value == null)
			addCount(-1L, -1);
		  return oldVal;
		}
		break;
	  }
	}
  }
  return null;
}
```
删除操作属于写类型的操作，所以在进行删除的时候需要对table中的index位置加锁，ConcurrentHashMap使用synchronized关键字将table中的index位置锁住，然后进行删除，remove方法调用了replaceNode方法来进行实际的操作，而删除操作的步骤首先依然是计算记录的hashCode，然后根据hashCode来计算table中的index值，然后根据table中的index位置上是一条链表还是一棵红黑树来使用不同的方法来删除这个记录，删除记录的操作需要进行记录数量的更新（调用addCount方法进行）。
### 容器扩容
在完成一次put操作之后，需要更新table中的记录数量，并且在更新之后如果发现超出了阈值，那么就需要进行table扩容操作，更新记录数量的操作通过调用方法addCount来完成：
```java
private final void addCount(long x, int check) {
	CounterCell[] as; long b, s;
	// 增加 baseCount
	if ((as = counterCells) != null ||
		!U.compareAndSwapLong(this, BASECOUNT, b = baseCount, s = b + x)) {
		CounterCell a; long v; int m;
		boolean uncontended = true;
		if (as == null || (m = as.length - 1) < 0 ||
			(a = as[ThreadLocalRandom.getProbe() & m]) == null ||
			!(uncontended =
			  U.compareAndSwapLong(a, CELLVALUE, v = a.value, v + x))) {
			fullAddCount(x, uncontended);
			return;
		}
		if (check <= 1)
			return;
		s = sumCount();
	}
	// 检查是否扩容
	if (check >= 0) {
		Node<K,V>[] tab, nt; int n, sc;
		while (s >= (long)(sc = sizeCtl) && (tab = table) != null &&
			   (n = tab.length) < MAXIMUM_CAPACITY) {
			int rs = resizeStamp(n);
			if (sc < 0) {
				if ((sc >>> RESIZE_STAMP_SHIFT) != rs || sc == rs + 1 ||
					sc == rs + MAX_RESIZERS || (nt = nextTable) == null ||
					transferIndex <= 0)
					break;
				if (U.compareAndSwapInt(this, SIZECTL, sc, sc + 1))
					transfer(tab, nt);
			}
			// 当前线程是唯一的或是第一个发起扩容的线程，此时 nextTable=null
			else if (U.compareAndSwapInt(this, SIZECTL, sc,
										 (rs << RESIZE_STAMP_SHIFT) + 2))
				transfer(tab, null);
			s = sumCount();
		}
	}
}
```
扩容的核心是 transfer 方法，整个扩容分为两步：
- 构建一个 nextTable，其大小为原来大小的 2 倍，这个步骤是在单线程环境下完成的
- 将原来的 table 里面的内容复制到 nextTable 中，这个步骤允许多线程操作
```java
private final void transfer(Node<K,V>[] tab, Node<K,V>[] nextTab) {
	int n = tab.length, stride;
	// 每个核处理的量
	if ((stride = (NCPU > 1) ? (n >>> 3) / NCPU : n) < MIN_TRANSFER_STRIDE)
		stride = MIN_TRANSFER_STRIDE; // subdivide range
	if (nextTab == null) {            // initiating
		try {
			@SuppressWarnings("unchecked")
			Node<K,V>[] nt = (Node<K,V>[])new Node<?,?>[n << 1];
			nextTab = nt;
		} catch (Throwable ex) {      // try to cope with OOME
			sizeCtl = Integer.MAX_VALUE;
			return;
		}
		nextTable = nextTab;
		transferIndex = n;
	}
	int nextn = nextTab.length;
	ForwardingNode<K,V> fwd = new ForwardingNode<K,V>(nextTab);
	// advance 为 true 表示已经处理过了
	boolean advance = true;
	boolean finishing = false; // to ensure sweep before committing nextTab
	for (int i = 0, bound = 0;;) {
		Node<K,V> f; int fh;
		// 遍历 hash 表中的节点
		while (advance) {
			int nextIndex, nextBound;
			if (--i >= bound || finishing)
				advance = false;
			else if ((nextIndex = transferIndex) <= 0) {
				i = -1;
				advance = false;
			}
			// CAS 更新 transferIndex
			else if (U.compareAndSwapInt
					 (this, TRANSFERINDEX, nextIndex,
					  nextBound = (nextIndex > stride ?
								   nextIndex - stride : 0))) {
				bound = nextBound;
				i = nextIndex - 1;
				advance = false;
			}
		}
		if (i < 0 || i >= n || i + n >= nextn) {
			int sc;
			if (finishing) {
				nextTable = null;
				table = nextTab;
				sizeCtl = (n << 1) - (n >>> 1);
				return;
			}
			// CAS 扩容
			if (U.compareAndSwapInt(this, SIZECTL, sc = sizeCtl, sc - 1)) {
				if ((sc - 2) != resizeStamp(n) << RESIZE_STAMP_SHIFT)
					return;
				finishing = advance = true;
				i = n; // recheck before commit
			}
		}
		else if ((f = tabAt(tab, i)) == null)
			advance = casTabAt(tab, i, null, fwd);
		// f.hash == -1 表示遍历到了 ForwardingNode 节点，意味着该节点已经处理过了
		else if ((fh = f.hash) == MOVED)
			advance = true; // already processed
		else {
			synchronized (f) {
				if (tabAt(tab, i) == f) {
					Node<K,V> ln, hn;
					if (fh >= 0) {
						int runBit = fh & n;
						Node<K,V> lastRun = f;
						for (Node<K,V> p = f.next; p != null; p = p.next) {
							int b = p.hash & n;
							if (b != runBit) {
								runBit = b;
								lastRun = p;
							}
						}
						if (runBit == 0) {
							ln = lastRun;
							hn = null;
						}
						else {
							hn = lastRun;
							ln = null;
						}
						for (Node<K,V> p = f; p != lastRun; p = p.next) {
							int ph = p.hash; K pk = p.key; V pv = p.val;
							if ((ph & n) == 0)
								ln = new Node<K,V>(ph, pk, pv, ln);
							else
								hn = new Node<K,V>(ph, pk, pv, hn);
						}
						setTabAt(nextTab, i, ln);
						setTabAt(nextTab, i + n, hn);
						setTabAt(tab, i, fwd);
						advance = true;
					}
					else if (f instanceof TreeBin) {
						TreeBin<K,V> t = (TreeBin<K,V>)f;
						TreeNode<K,V> lo = null, loTail = null;
						TreeNode<K,V> hi = null, hiTail = null;
						int lc = 0, hc = 0;
						for (Node<K,V> e = t.first; e != null; e = e.next) {
							int h = e.hash;
							TreeNode<K,V> p = new TreeNode<K,V>
								(h, e.key, e.val, null, null);
							if ((h & n) == 0) {
								if ((p.prev = loTail) == null)
									lo = p;
								else
									loTail.next = p;
								loTail = p;
								++lc;
							}
							else {
								if ((p.prev = hiTail) == null)
									hi = p;
								else
									hiTail.next = p;
								hiTail = p;
								++hc;
							}
						}
						ln = (lc <= UNTREEIFY_THRESHOLD) ? untreeify(lo) :
							(hc != 0) ? new TreeBin<K,V>(lo) : t;
						hn = (hc <= UNTREEIFY_THRESHOLD) ? untreeify(hi) :
							(lc != 0) ? new TreeBin<K,V>(hi) : t;
						setTabAt(nextTab, i, ln);
						setTabAt(nextTab, i + n, hn);
						setTabAt(tab, i, fwd);
						advance = true;
					}
				}
			}
		}
	}
}
```
- 为每个内核分配任务，保证每个内核任务量不小于 16
- 检查 nextTable 是否为 null，如果是则初始化 nextTab，使其容量为 2*table
- 死循环遍历节点直到 finished，将节点从 table 复制到 nextTable：
  - 如果节点 f 为 null，插入 ForwardingNode
  - 如果 f 为链表头节点(fh>=0)，则先构造一个反序链表，然后把他们分别放在 nextTab 的 i 和 i+n 位置，并将 ForwardingNode 插入原节点位置表示已经处理过了
  - 如果 f 为 TreeBin 节点，同样构造反序，同时需要判断是否需要进行 untreeify 操作，并把处理的结果分别插入到 nextTab 的 i 和 i+n 位置，并在原节点位置插入 ForwardingNode 节点
- 所有节点复制完成后将 table 指向 nextTable，同时更新 sizeCtl=nextTable * 0.75，完成扩容操作

在多线程时，扩容遍历到的节点如果是 ForwardingNode 则表示该节点已经处理过继续遍历，如果不是则对该节点加锁放置其他线程进入。
### Size
ConcurrentHashMap 通过 size 方法来获得记录数量，size 方法返回的是一个不精确的值，因为在进行统计的时候有其他线程正在进行插入和删除操作：
```java
public int size() {
	long n = sumCount();
	return ((n < 0L) ? 0 :
			(n > (long)Integer.MAX_VALUE) ? Integer.MAX_VALUE :
			(int)n);
}

final long sumCount() {
	CounterCell[] as = counterCells; CounterCell a;
	// baseCount 是当前 Map 的真实元素个数
	long sum = baseCount;
	if (as != null) {
		for (int i = 0; i < as.length; ++i) {
			if ((a = as[i]) != null)
				sum += a.value;
		}
	}
	return sum;
}
```
ConcurrentHashMap的记录数量需要结合baseCount和counterCells数组来得到，通过累计两者的数量即可获得当前ConcurrentHashMap中的记录总量。推荐使用 ```mappingCount()``` 方法获取容量大小。