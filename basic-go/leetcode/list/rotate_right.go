package list

// 给定一个链表，将链表的每个节点向右移动 k 个位置

// 找到倒数 k 个节点，断开链接，然后将尾节点指向头节点
// 使用双指针法，使两个指针的间隔为 k，当快指针到达链表尾部时慢指针到达倒数 k 个节点
// 时间复杂度 O(N)
func rotateRight(head *ListNode, k int) *ListNode {
	if head == nil {
		return head
	}

	// 计算单链表长度
	length := 0
	for curr := head; head != nil; curr = curr.Next {
		length++
	}

	fast, slow := head, head

	// 取模防止 k > length
	for fast != nil && k%length > 0 {
		fast, k = fast.Next, k-1
	}

	// fast 指向尾节点
	for fast.Next != nil {
		slow, fast = slow.Next, fast.Next
	}

	fast.Next = head
	head = slow.Next
	slow.Next = nil
	return head

}

// 将链表转换成环形链表，然后找到倒数 k 位置断开链接
// k = n - (k % n)
func rotateRight1(head *ListNode, k int) *ListNode {

	if head == nil || head.Next == nil {
		return head
	}

	curr := head
	n := 1
	// 转换单链表为环形链表，并且计算链表长度
	for curr.Next != nil {
		curr, n = curr.Next, n+1
	}
	curr.Next = head

	curr = head
	// 找到倒数 k 位置节点断开链接
	for i := 0; i < n-k%n-1; i++ {
		curr = curr.Next
	}
	head := curr.Next
	curr.Next = nil
	return head
}
