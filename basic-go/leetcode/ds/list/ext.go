package list

// 快慢指针法找到链表中间位置
func (l *List) middle() *node {
	// 快、慢指针
	slow, fast := l.head, l.head
	// 快指针每次移动两个位置，慢指针每次移动一个位置
	for fast.next != nil && fast.next.next != nil {
		slow = slow.next
		fast = fast.next.next
	}
	return slow
}

// 判断链表是否为回文结构
func (l *List) isPalindrome() bool {
	// 空链表返回 true
	if l == nil || l.head == nil {
		return true
	}
	// 只有一个结点的链表返回 true
	if l.head.next == nil {
		return true
	}
	// 找到链表中间位置
	mid := l.middle()
	// 从中间位置开始到链表尾部指针反转
	p := mid.next
	mid.next = nil
	for p != nil {
		help := p.next
		p.next = mid
		mid = p
		p = help
	}
	// 从两端向中间比较移动
	h := l.head
	t := mid
	flag := true
	for h != nil {
		if h.value != t.value {
			flag = false
			break
		}
		h = h.next
		t = t.next
	}
	// 还原链表
	node := mid.next
	mid.next = nil
	for node != nil {
		help := node.next
		node.next = mid
		mid = node
		node = help
	}
	return flag
}
