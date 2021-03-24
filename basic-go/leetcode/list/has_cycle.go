package list

// 给定链表，判断链表中是否有环

// 使用 Map 保存每个节点，如果在遍历链表的过程中发现已经在 Map 中则说明有环

func hasCycle(head *ListNode) bool {
	seen := map[*ListNode]struct{}{}
	for head != nil {
		if _, ok := seen[head]; ok {
			return true
		}
		seen[head] = struct{}{}
		head = head.Next
	}
	return false
}

// 快慢指针法，如果有环，快指针一定会和慢指针相遇
func hasCycle1(head *ListNode) bool {
	if head == nil || head.Next == nil {
		return false
	}
	slow, fast := head, head.Next
	for slow != fast {
		if fast == nil || fast.Next == nil {
			return false
		}
		slow, fast = slow.Next, fast.Next.Next
	}
	return true
}
