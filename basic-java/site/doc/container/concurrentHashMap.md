## ConcurrentHashMap

ConcurrentHashMap 是 HashMap 的线程安全实现，ConcurrentHashMap 依然采用数组加链表和红黑树的数据结构，链表到红黑树的转换阈值依然是 8，ConcurrentHashMap 依然采用双倍扩容的方式扩容数组，只是在扩容的时候采用了机制保证并发安全。

和 HashMap 不同的是，ConcurrentHashMap 的 key 和 value 都不允许为 null。

```java

// 数组最大容量
private static final int MAXIMUM_CAPACITY = 1 << 30;
    
// 数组默认初始化的容量
private static final int DEFAULT_CAPACITY = 16;

// 负载因子，达到阈值时需要扩容
private static final float LOAD_FACTOR = 0.75f;

// 链表长度达到阈值时转化成一棵红黑树
static final int TREEIFY_THRESHOLD = 8;
```
ConcurrentHashMap 元素依然是包装成 Node 和 TreeNode 后插入链表或者红黑树：
```java
static class Node<K,V> implements Map.Entry<K,V> {
    final int hash;
    final K key;
    // volatile 修饰表示当前线程的修改其他线程立即可见
    volatile V val;
    volatile Node<K,V> next;

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
TreeNode 是红黑树实际存储数据的节点，ConcurrentHashMap 还定义了 TreeBin 数据结构指向红黑树的根节点，并维护了读写锁用于控制数据写入时的并发安全：
```java
static final class TreeBin<K,V> extends Node<K,V> {
    TreeNode<K,V> root;
    volatile TreeNode<K,V> first;
    volatile Thread waiter;
    volatile int lockState;
    // values for lockState
    static final int WRITER = 1; // set while holding write lock
    static final int WAITER = 2; // set when waiting for write lock
    static final int READER = 4; // increment value for setting read lock

    // ...
}
```

### Get

ConcurrentHashMap 获取元素的流程和 HashMap 类似，先通过 hash 值计算数组索引位置，然后判断索引位置的数据结构，如果为红黑树则使用红黑树结果查询，如果是链表则使用链表结构铲鲟。

ConcuurentHashMap 获取元素的整个流程并没有加锁，因为数组中如果是链表结构则由于 val 和 next 都是 volatile 修饰的，其他线程添加了元素或者修改了元素是立即可见的，如果是红黑树结构则在 fand 方法中增加了对 lockState 的判断，因此只会锁住当前位置而其他位置不受影响。

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

ConcurrentHashMap 插入数据的流程在 putVal 中实现，如果插入数据时数组为空则需要执行初始化流程；如果索引位置没有元素则创建新的 Node 并以 CAS 方式插入数据；如果容器正在扩容则在扩容转移数据时完成插入；否则将当前索引位置加上锁，之后执行插入操作。数据插入完毕之后检查数组是否需要扩容，如果需要则扩容并转移数据。

```java

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
        // 元素为扩容是的首元素，说明正在进行扩容
        else if ((fh = f.hash) == MOVED)
            tab = helpTransfer(tab, f);
        else {
            V oldVal = null;
            // 槽加锁
            synchronized (f) {
                if (tabAt(tab, i) == f) {
                    // 链表结构
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
### InitTable

ConcurrentHashMap 在插入数据时会检查数组是否初始化，如果没有则调用 initTable 方法来初始化。ConcurrentHashMap 初始化时检查 sizeCtl 变量，如果为负数表示容器正在初始化或者扩容，其中 -1 表示正在初始化，此时线程让出执行调度让其他线程完成初始化或者扩容。否则以 CAS 方式将 sizeCtl 变量设置为 -1 表示当前线程在执行初始化。

ConcurrentHashMap 初始化时先创建默认大小的数组，然后计算 ```cap - (cap>>>2)``` 赋值给 sizeCtl，该值在后续扩容时使用。

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
                sizeCtl = sc;
            }
            break;
        }
    }
    return tab;
}
```
### Resize

ConcurrentHashMap 在插入数据之后检查是否需要扩容，如果达到负载因子阈值则需要扩容，ConcurrentHashMap 扩容操作在 transfer 方法中完成。

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

### Remove
```java
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

***[ConcurrentHashMap](../../)*