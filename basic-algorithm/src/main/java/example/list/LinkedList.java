package example.list;

public class LinkedList<T extends Comparable<T>> {

    private final ListNode<T> head;

    public LinkedList(ListNode<T> head) {
        this.head = head;
    }

    /**
     * 使用快慢指针法找到链表的中间位置
     *
     * @return
     */
    public ListNode<T> middle() {

        ListNode<T> fast = head, slow = head;
        while (fast.next() != null && fast.next().next() != null) {
            fast = fast.next().next();
            slow = slow.next();
        }
        return slow;
    }

    /**
     * 链表有环判断，使用快慢指针法，如有环则快慢指针会在某一时刻指向同一个节点，此时快指针回到初始位置每次移动一步，慢指针在当前位置继续移动，当再次相遇时的位置即为入环节点
     *
     * @return
     */
    public ListNode<T> loopNode() {
        if (head == null || head.next() == null){
            return null;
        }
        ListNode<T> fast = head.next().next(), slow = head.next();
        while (fast != null && fast.next() != null && fast != slow) {
            fast = fast.next().next();
            slow = slow.next();
        }
        if (fast == null || fast.next() == null) {
            return null;
        }
        // 快慢指针相遇
        fast = head;
        while (fast != slow){
            fast = fast.next();
            slow = slow.next();
        }
        return fast;
    }


    public static LinkedList<Integer> generate(int len, int max, int min) {
        int size = 0;
        ListNode<Integer> head = null, curr = head;
        while (size < len) {
            ListNode<Integer> node = new ListNode<>((int) (Math.random() * (max - min)));
            if (head == null) {
                head = node;
                curr = head;
            } else {
                curr.setNext(node);
                curr = curr.next();
            }
            size++;
        }
        return new LinkedList<>(head);
    }

    public void print() {
        ListNode<T> curr = head;
        while (curr != null) {
            System.out.print(curr.value() + " ");
            curr = curr.next();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        LinkedList<Integer> list = generate(10, 20, 1);
        list.print();

        ListNode<Integer> middle = list.middle();
        System.out.println(middle.value());
    }
}
