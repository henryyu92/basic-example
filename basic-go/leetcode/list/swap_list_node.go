package list

// 两两交换链表中的节点

// 递归实现：交换链表前两个节点，然后和后面交换好的链表链接起来
// 时间复杂度： O(N)，链表的每个节点都需要遍历到
// 空间复杂度： O(N), 递归需要占用栈空间

func recursiveSwapPairs(head *ListNode) *ListNode {

	if head == nil || head.Next == nil {
		return head
	}

	tmp := head.Next
	head.Next = recursiveSwapPairs(head.Next.Next)
	tmp.Next = head
	return tmp
}

// 非递归方式实现：使用两个指针指向两个节点，交换完之后移动两个节点
// 时间复杂度： O(N)，每个节点都需要遍历到
// 空间复杂度： O(1)
func swapPairs(head *ListNode) *ListNode {
	dummy := &ListNode{0, head}
	p := dummy
	for p.Next != nil && p.Next.Next != nil {
		node1 := p.Next
		node2 := p.Next.Next

		node1.Next = node2.Next
		node2.Next = node1
		p.Next = node2
		p = node1
	}
	return dummy.Next
}

// 对于给定的链表，每 k 个节点一组进行翻转，返回翻转后的链表

// 递归实现：翻转前 k 个节点，然后和后面翻转好的链表链接起来，
// 时间复杂度： O(N)，每个节点都需要遍历到
// 空间复杂度： O(N)，递归占用栈空间
func recursiveReverseKGroup(head *ListNode) *ListNode {

}
