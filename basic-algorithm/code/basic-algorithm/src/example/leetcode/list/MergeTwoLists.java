package example.leetcode.list;

/**
 * 将两个升序链表合并为一个新的升序链表
 */
public class MergeTwoLists {


    public ListNode mergeTwoLists(ListNode l1, ListNode l2){
        ListNode dummy = new ListNode(-1);
        ListNode curr = dummy;
        while (l1 != null && l2 != null){
            if (l1.val < l2.val){
                curr.next = l1;
                l1 = l1.next;
            }else {
                curr.next = l2;
                l2 = l2.next;
            }
            curr = curr.next;
        }
        if (l1 != null){
            curr.next = l1;
        }
        if (l2 != null){
            curr.next = l2;
        }
        return dummy.next;
    }


    /**
     *  将链表 1 的 a 到 b 区间的节点删掉，然后将 list2 接在被删除节点的位置
     */
    public ListNode mergeInBetween(ListNode list1, int a, int b, ListNode list2){

        ListNode dummy = new ListNode(-1, list1);
        ListNode prev = dummy, curr = dummy;
        int i = 0;
        while (i < a){
            prev = prev.next;
            i++;
        }
        ListNode tail = list2;
        while (tail.next != null){
            tail = tail.next;
        }
        int j = 0;
        while (j <= b){
            curr = curr.next;
            j++;
        }
        ListNode tmp = curr.next;
        curr.next = null;
        tail.next = tmp;

        prev.next = null;
        prev.next = list2;

        return dummy.next;
    }
}
