// 给定两个非空的链表，每位数字按照逆序方式存储，每个节点只能存储一位数字

// 非递归版本，引入额外的进位 carry
func addTwoNumbers(l1 *ListNode, l2 *ListNode) *ListNode {
	dummy := &ListNode{0, nil}
	curr := dummy
	carry := 0
	for l1 != nil || l2 != nil {
		n1, n2 := 0, 0
		if l1 != nil {
			n1 = l1.Val
			l1 = l1.Next
		}
		if l2 != nil {
			n2 = l2.Val
			l2 = l2.Next
		}
		sum := n1 + n2 + carry
		carry = sum / 10
		curr.Next = &ListNode{sum % 10, nil}
		curr = curr.Next

	}
	if carry > 0 {
		curr.Next = &ListNode{carry, nil}
	}
	return dummy.Next
}

// 递归版本
func addTowNumbersHelper(l1 *ListNode, l2 *ListNode, carry int) *ListNode {
	dummy := &ListNode{0, nil}
	if l1 == nil && l2 == nil && carry == 0 {
		return nil
	}
	n1, n2 := 0, 0
	if l1 != nil {
		n1 = l1.Val
		l1 = l1.Next
	}
	if l2 != nil {
		n2 = l2.Val
		l2 = l2.Next
	}
	sum := n1 + n2 + carry
	dummy.Val = sum % 10
	dummy.Next = addTowNumbersHelper(l1, l2, sum/10)
	return dummy
}

