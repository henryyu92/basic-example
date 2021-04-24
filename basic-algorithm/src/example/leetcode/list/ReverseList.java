package example.leetcode.list;

public class ReverseList {

    /**
     *  给定单链表头节点 head，返回反转后的链表
     *
     *  穿针引线法：依次将每个节点移动到头节点前面
     */
    public ListNode reverseList(ListNode head){

        if (head == null){
            return null;
        }

        ListNode dummy = new ListNode(-1, head);

        while (head.next != null){
            ListNode reverse = head.next;
            head.next = reverse.next;
            reverse.next = dummy.next;
            dummy.next = reverse;
        }
        return dummy.next;
    }

    /**
     * 反转指定位置之间的链表
     *
     * 穿针引线法
     */
    public ListNode reverseBetween(ListNode head, int left, int right){
        ListNode dummy = new ListNode(-1, head);
        ListNode pre = dummy;
        int i = 0;
        while (i < left - 1){
            i++;
            pre = pre.next;
        }
        int j = 0;
        ListNode curr = pre.next;
        while (j < right - left){
            ListNode reserve = curr.next;
            curr.next = reserve.next;
            reserve.next = pre.next;
            pre.next = reserve;
            j++;
        }
        return dummy.next;
    }
}
