package example.leetcode.list;

/**
 * 删除链表的倒数 N 个结点，并返回链表头结点
 */
public class RemoveNthFromEnd {


    /**
     *  使用双指针法定位到倒数 N 个节点
     */
    public ListNode removeNthFromEnd(ListNode head, int n){

        // 定义 dummy 节点，防止 head 为 null
        ListNode dummy = new ListNode(-1, head);

        ListNode p = dummy;
        ListNode prev = dummy;
        int i = 0;
        while (p.next != null){
            p = p.next;
            if (i < n){
                i++;
            }else{
                prev = prev.next;
            }
        }
        if (i < n){
            return head;
        }

        ListNode tmp = prev.next;
        prev.next = tmp.next;
        tmp.next = null;

        return dummy.next;

    }
}
