package leetcode.list;

public class ReverseList {

    /**
     * 两两交换链表中相邻的节点，返回交换后的链表
     */
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

    /**
     *  反转指定节点之间的节点
     */
    public ListNode[] reverseBetween(ListNode head, ListNode tail){
        ListNode prev = tail.next;
        ListNode p = head;
        while (prev != tail){
            ListNode next = p.next;
            p.next = prev;
            prev = p;
            p = next;
        }
        return new ListNode[]{tail, head};
    }

    /**
     *  使用 head 和 tail 指针指向需要反转的位置，如果长度小于 k 则不反转
     */
    public ListNode reverseKGroup(ListNode head, int k){

        ListNode dummy = new ListNode(-1, head);
        ListNode prev = dummy;

        while (head != null){

            ListNode tail = prev;
            for (int i = 0; i < k; i++){
                tail = tail.next;
                if (tail == null){
                    return dummy.next;
                }
            }

            ListNode next = tail.next;
            ListNode[] reverse =reverseBetween(head, tail);
            head = reverse[0];
            tail = reverse[1];

            // 拼接
            prev.next = head;
            tail.next = next;

            prev = tail;
            head = tail.next;
        }
        return dummy.next;
    }
}
