package list

// 合并给定的两个链表

// 迭代实现
func mergeTwoLists(l1 *ListNode, l2 *ListNode) *ListNode {
	dummy := &ListNode{0, nil}
	prev := dummy

	for l1 != nil && l2 != nil {
		if l1.Val <= l2.Val {
			prev.Next = l1
			l1 = l1.Next
		} else {
			prev.Next = l2
			l2 = l2.Next
		}
		prev = prev.Next
	}

	if l1 != nil {
		prev.Next = l1
	}

	if l2 != nil {
		prev.Next = l2
	}

	return dummy.Next

}

// 递归实现
// 时间复杂度 O(m+n)
// 空间复杂度：递归需要消耗栈空间 O(m+n)
func recursiveMerge(l1 *ListNode, l2 *ListNode) *ListNode {
	if l1 == nil {
		return l2
	}
	if l2 == nil {
		return l1
	}
	if l1.Val <= l2.Val {
		l1.Next = recursiveMerge(l1.Next, l2)
		return l1
	} else {
		l2.Next = recursiveMerge(l1, l2.Next)
		return l2
	}
}

// 合并 k 个链表

// 使用分治法
// 时间复杂度：
//		两个链表合并的时间复杂度为 O(m+n)，K 个链表使用分治算法后的时间复杂度为 k/2*O(2k) + k/4*O(4k) + ... = k
func mergeKLists(lists []*ListNode) *ListNode {
	return mergeLists(lists, 0, len(lists)-1)
}

func mergeLists(lists []*ListNode, left, right int) *ListNode {
	if left == right {
		return lists[left]
	}
	if left > right {
		return nil
	}
	mid := left + (right-left)>>2
	leftMerge := mergeLists(lists, left, mid)
	rightMerge := mergeLists(lists, mid+1, right)
	return mergeTwoLists(leftMerge, rightMerge)
}
