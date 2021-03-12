package list

// 反转链表

// 朴素算法： 使用双指针 p 和 q 分别指向需要反转的节点以及其前一个节点，遍历整个链表直到链表尾部
// 时间复杂度: O(N)，需要遍历每个节点
// 空间复杂度： O(1)
func reverseList(head *ListNode) *ListNode {
	var prev *ListNode
	for head != nil {
		prev, head, head.Next = head, head.Next, prev
	}
	return prev
}

// 穿针引线法
func reverseInsertion(head *ListNode) *ListNode {
	dummy := &ListNode{0, head}
	prev, curr := dummy, head
	for curr.Next != nil {
		next := curr.Next
		prev.Next, curr.Next, next.Next = next, next.Next, curr
	}
	return dummy.Next
}

// 递归实现： 考虑将链表分为当前节点和已经反转完成的链表，则只需要将当前链表和已经反转完成的链表链接起来即可
// 时间复杂度： O(N)
// 空间复杂度： O(N)
func recursiveReverseList(head *ListNode) *ListNode {
	if head == nil || head.Next == nil {
		return head
	}
	newHead := recursiveReverseList(head.Next)
	head.Next.Next = head
	head.Next = nil
	return newHead
}

// 反转从位置 m 到位置 n 的链表

// 穿针引线法：将每一个需要反转的节点反转到需要反转的部分的起始位置
//    		2->5->4->3
// step1	5->2->4->3
// step2	4->5->2->3
// step3	3->4->5->2
func reverseBetween(head *ListNode, left int, right int) *ListNode {
	dummy := &ListNode{0, head}
	prev := dummy
	for i := 0; i < left; i++ {
		prev = prev.Next
	}
	curr := prev.Next
	// curr 指向需要反转部分的第一个节点，随着后续节点的插入会移动到需要反转的部分的尾部
	for i := left; i <= right; i++ {
		next := curr.Next
		prev.Next, curr.Next, next.Next = next, next.Next, curr
	}
	return dummy.Next
}
