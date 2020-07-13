package example.list;

/**
 * 链表节点
 * @param <T>
 */
public class ListNode<T extends Comparable<T>> {

    private T value;
    private ListNode<T> prev;
    private ListNode<T> next;

    public ListNode(){}

    public ListNode(T t){
        this.value = t;
    }

    public ListNode<T> next(){
        return this.next;
    }

    public void setNext(ListNode<T> next){
        this.next = next;
    }

    public ListNode<T> prev(){
        return this.prev;
    }

    public T value(){
        return this.value;
    }
}
