package example.leetcode.list;

/**
 * 两两交换链表中相邻的节点，返回交换后的链表
 */
public class SwapPairs {


    public ListNode swapPairs(ListNode head){

        ListNode dummy = new ListNode(-1, head);
        ListNode prev = dummy;
        while (prev.next != null && prev.next.next != null){
            ListNode end = prev.next.next;
            prev.next.next = end.next;
            end.next = prev.next;
            prev.next = end;

            prev = prev.next.next;
        }
        return dummy.next;
    }
}
