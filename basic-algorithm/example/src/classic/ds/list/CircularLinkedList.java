package classic.ds.list;

/**
 * 循环链表：尾节点的 next 指针指向头结点
 */
public class CircularLinkedList<T> {

    private ListNode<T> head;
    private ListNode<T> tail;

    public CircularLinkedList(ListNode<T> head){
        this.head = head;
        this.tail = head;
        this.tail.setNext(head);
    }

    public void add(T value){
        ListNode<T> node = new ListNode<>(value);
        node.setNext(head);
        tail.setNext(node);
        tail = node;
    }

    public T remove(int n){
        ListNode<T> prev = head;
        while (n-- > 0){
            prev = prev.next();
        }
        ListNode<T> remove = prev.next();
        prev.setNext(remove.next());
        remove.setNext(null);
        return remove.value();
    }

    public int size(){
        ListNode<T> next = head.next();
        int size = 1;
        while (next != head){
            size++;
            next = next.next();
        }
        return size;
    }

}
