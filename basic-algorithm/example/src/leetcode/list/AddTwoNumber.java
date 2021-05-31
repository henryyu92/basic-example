package example.leetcode.list;

/**
 * 两数相加：给你两个 非空 的链表，表示两个非负的整数。它们每位数字都是按照 逆序 的方式存储的，并且每个节点只能存储 一位 数字。将两个数相加，并以相同形式返回一个表示和的链表。
 *
 *
 */
public class AddTwoNumber {

    /**
     *  两数相加时考虑进位
     */
    public ListNode addTwoNumber(ListNode l1, ListNode l2){
        ListNode dummy = new ListNode(-1);
        ListNode next = dummy;
        int carry = 0;
        while (l1 != null || l2 != null || carry != 0){
            int[] helper = helper(l1, l2, carry);
            carry = helper[1];
            next.next = new ListNode(helper[0]);
            l1 = l1 == null ? null : l1.next;
            l2 = l2 == null ? null : l2.next;
            next = next.next;
        }
        return dummy.next;
    }

    public int[] helper(ListNode l1, ListNode l2, int carry){
        int v1 = l1 == null ? 0 : l1.val;
        int v2 = l2 == null ? 0 : l2.val;

        int[] result = new int[2];
        result[0] = (v1 + v2 + carry) % 10;
        result[1] = (v1 + v2 + carry) / 10;

        return result;
    }


    public ListNode addTwoNumber_1(ListNode l1, ListNode l2){

        ListNode dummy = new ListNode(-1);
        ListNode head = dummy;
        int carry = 0;

        while (l1 != null || l2 != null){
            int v1 = 0, v2 = 0, sum;
            if (l1 != null){
                v1 = l1.val;
                l1 = l1.next;
            }
            if (l2 != null){
                v2 = l2.val;
                l2 = l2.next;
            }
            sum = v1 + v2 + carry;
            carry = sum / 10;
            head.next = new ListNode(sum % 10);
            head = head.next;
        }
        if (carry == 1){
            head.next = new ListNode(carry);
        }
        return dummy.next;
    }
}
