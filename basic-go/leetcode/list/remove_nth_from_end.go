package list

// 删除给定链表的倒数第 n 个节点，并且返回链表的头节点
// 只遍历链表一次

// 解题思路：
//	只能使用一次遍历则必须要能够在遍历到链表结尾时定位到倒数第 n 个节点，定义双指针 p 和 q 使得 p 和 q 的间隔为 n 这样当 p 到达链表尾部时 q 刚好位于链表倒数第 n 个节点。

type ListNode struct {
	Val  int
	Next *ListNode
}

func removeNthFromEnd(head *ListNode, n int) *ListNode {

	dummy := &ListNode{0, head}
	left, right := dummy, dummy
	i := 0
	// 使得 p 和 q 的间隔为 n
	for i < n && right.Next != nil {
		right = right.Next
		i++
	}
	if i < n {
		return nil
	}
	for right.Next != nil {
		left = left.Next
		right = right.Next
	}
	deleted := left.Next
	left.Next = left.Next.Next
	deleted.Next = nil
	return dummy.Next
}
