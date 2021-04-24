package example.leetcode.list;

/**
 * 环形链表
 */
public class DetectCycle {

    /**
     * 判断链表是否有环
     *
     *  快慢指针法
     *
     */
    public boolean hasCycle(ListNode head){
        if (head == null || head.next == null){
            return false;
        }
        ListNode fast = head, slow = head;
        while (fast.next != null && fast.next.next != null){
            fast = fast.next.next;
            slow = slow.next;
            if (fast == slow){
                return true;
            }
        }
        return false;
    }

    /**
     * 返回链表入环第一个节点
     *
     *  快慢指针相遇时，快指针绕了 n 圈，于是有：
     *
     *      a + (n+1)b + nc = 2(a+b) ==> a = c + (n-1)(b+c)
     *
     *  相遇之后，慢指针继续运行，使用额外指针从链表头节点遍历，两个指针相遇时的节点为入环节点
     *
     */
    public ListNode detectCycle(ListNode head){

        if (head == null){
            return null;
        }
        ListNode fast = head, slow = head;
        while (fast != null && fast.next != null){
            slow = slow.next;
            fast = fast.next.next;
            // 相遇说明有环
            if (fast == slow){
                ListNode ptr = head;
                while (ptr != slow){
                    slow = slow.next;
                    ptr = ptr.next;
                }
                return ptr;
            }
        }
        return null;
    }
}
