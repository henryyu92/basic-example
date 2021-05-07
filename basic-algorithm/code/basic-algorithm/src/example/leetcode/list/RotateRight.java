package example.leetcode.list;

/**
 * 给定链表头节点 head，将链表节点向右移动 K 个位置
 *
 */
public class RotateRight {

    public ListNode rotateRight(ListNode head, int k){

        /*
         * 注意 K 有可能会大于链表长度
         */
        int length = 0;
        ListNode p = head;
        while (p != null){
            length++;
            p = p.next;
        }
        if(length == 0){
            return head;
        }
        if (k > length){
            k = k % length;
        }
        if(k == 0){
            return head;
        }

        ListNode dummy = new ListNode(-1, head);

        // 找到倒数 K 个节点，断开连接，将尾结点和头结点连接
        ListNode left = dummy, right = dummy;
        int i = 0;
        while (right.next != null){
            right = right.next;
            if (i < k){
                i++;
            }else{
                left = left.next;
            }
        }
        // 不足 k 个
        if (i < k){
            return dummy.next;
        }
        ListNode tmp = left.next;
        left.next = null;
        right.next = dummy.next;
        dummy.next = tmp;

        return dummy.next;
    }


    /**
     * 构造成环形链表
     */
    public ListNode rotateRight_1(ListNode head, int k){

        if (head == null){
            return null;
        }
        ListNode curr = head;
        int length = 1;
        while (curr.next != null){
            curr = curr.next;
            length++;
        }
        curr.next = head;

        k = k % length;
        int move = length - k - 1;
        while (move > 0){
            head = head.next;
            move--;
        }
        ListNode result = head.next;
        head.next = null;
        return result;
    }
}
