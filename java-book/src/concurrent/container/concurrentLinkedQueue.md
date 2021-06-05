## ConcurrentLinkedQueue

ConcurrentLinkedQueue 是基于链表实现的无界线程安全队列，采用 CAS 的方式实现无锁入队和出队。`ConcrruentListQueue` 内部维护着指向头节点的指针 head 和指向尾节点的指针 tail。
```java
public class ConcurrentLinkedQueue<E> extends AbstractQueue<E>
        implements Queue<E>{
    
    private transient volatile Node<E> head;

    private transient volatile Node<E> tail;
        
}
```
`ConcurrentLinkedQueue` 入队采用 cas 实现，如果 tail 指向尾节点则使用 CAS 方式向尾部追加节点，此时 tail 指针并未发生变化，当再次向链表尾部追加节点后就会将 tail 指针调整到链表的尾节点：
```java
public boolean offer(E e) {
    checkNotNull(e);
    final Node<E> newNode = new Node<E>(e);

    for (Node<E> t = tail, p = t;;) {
        Node<E> q = p.next;
        if (q == null) {
            // p is last node
            if (p.casNext(null, newNode)) {
                // Successful CAS is the linearization point
                // for e to become an element of this queue,
                // and for newNode to become "live".
                if (p != t) // hop two nodes at a time
                    casTail(t, newNode);  // Failure is OK.
                return true;
            }
            // Lost CAS race to another thread; re-read next
        }
        else if (p == q)
            // We have fallen off list.  If tail is unchanged, it
            // will also be off-list, in which case we need to
            // jump to head, from which all live nodes are always
            // reachable.  Else the new tail is a better bet.
            p = (t != (t = tail)) ? t : head;
        else
            // Check for tail updates after two hops.
            p = (p != t && t != (t = tail)) ? t : q;
    }
}
```
出队操作也是采用 CAS 保证的，出队时并不是将头节点移除，而是将头节点的元素设置为 null 然后在下次执行出队时将 head 指针指向真正的头节点。
```java
public E poll() {
    restartFromHead:
    for (;;) {
        for (Node<E> h = head, p = h, q;;) {
            E item = p.item;

            if (item != null && p.casItem(item, null)) {
                // Successful CAS is the linearization point
                // for item to be removed from this queue.
                if (p != h) // hop two nodes at a time
                    updateHead(h, ((q = p.next) != null) ? q : p);
                return item;
            }
            else if ((q = p.next) == null) {
                updateHead(h, p);
                return null;
            }
            else if (p == q)
                continue restartFromHead;
            else
                p = q;
        }
    }
}
```