package classic.ds.list;

/**
 * 链表节点
 */
public class ListNode<T> {

    private T value;
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

    public T value(){
        return this.value;
    }
}
