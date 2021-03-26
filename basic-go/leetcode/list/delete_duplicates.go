package list

// 给定排序链表，删除所有重复元素，使得每个元素只出现一次

// 朴素法：当前元素和下一个元素相等，则跳过下一个元素
func deleteDuplicates(head *ListNode) *ListNode {
	curr := head

	for curr != nil && curr.Next != nil {
		if curr.Val == curr.Next.Val {
			tmp := curr.Next
			curr.Next = curr.Next.Next
			tmp.Next = nil
		} else {
			curr = curr.Next
		}
	}

	return head
}

// 使用双指针，记录重复元素开始和结束
// 1. left 和 right 相同则需要同时移动
// 2. left 慢于 right 则中间有相等的元素，需要移除，然后将 left 和 right 相等
func deleteDuplicates1(head *ListNode) *ListNode {

	if head == nil {
		return nil
	}
	left, right := head, head
	for right.Next != nil {
		prev := right
		right = right.Next
		if left.Val != right.Val {
			// 顺序不可调换，prev 有可能和 left 相等
			prev.Next = nil
			left.Next = right
			left = right
		}
	}
	if left.Val == right.Val {
		left.Next = nil
	}
	return head
}

// 给定排序链表，删除所有含有重复数字的节点，只保留原始链表中 没有重复出现 的数字

// 采用递归实现
// 1. 1->1->2->3	需要先将前面的重复去掉
// 2. 1->2->2->3	递归会变成 1

func deleteDuplicates2(head *ListNode) *ListNode {

	// base case
	if head == nil || head.Next == nil {
		return head
	}
	if head.Val == head.Next.Val {
		next := head.Next
		for next != nil && next.Val == head.Val {
			next = next.Next
		}
		return deleteDuplicates(next)
	}
	head.Next = deleteDuplicates(head.Next)
	return head
}
