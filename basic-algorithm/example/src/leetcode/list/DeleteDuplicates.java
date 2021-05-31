package example.leetcode.list;

/**
 *  给定升序链表头节点 head，删除重复的元素，使得每个元素只出现一次
 */
public class DeleteDuplicates {

    /**
     * 双指针，记录重复出现的开始和结束位置
     */
    public ListNode deleteDuplicates(ListNode head){

        ListNode dummy = new ListNode(Integer.MIN_VALUE, head);
        ListNode left = dummy, right = head;

        while (right != null){
            if (left.val != right.val){
                left.next = right;
                left = right;
            }
            right = right.next;
        }
        if (left.next != null){
            left.next = null;
        }
        return dummy.next;
    }


    /**
     *  给定升序链表头节点 head，只保留没有重复的节点
     *
     *  双指针，记录重复开始的前缀和重复结束的位置
     *
     */
    public ListNode deleteDuplicates_1(ListNode head){
        if (head == null || head.next == null){
            return head;
        }
        ListNode dummy = new ListNode(Integer.MIN_VALUE, head);
        ListNode left = dummy, right = head.next;
        while (right != null){

            if (left.next.val != right.val){
                if (left.next.next == right){
                    left = left.next;
                }else{
                    left.next = right;
                }
            }
            right = right.next;
        }
        if (left.next.next != null){
            left.next = null;
        }
        return dummy.next;
    }
}
