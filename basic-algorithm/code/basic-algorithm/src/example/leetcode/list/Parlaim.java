package example.leetcode.list;

/**
 * 问题描述：
 *
 * > 对于一个单向链表，设计一个时间复杂度为 O(N)，额外空间复杂度为 O(1) 的算法，判断其是否为回文结构
 *
 * 思路：
 *
 * > 对于回文问题，一般可以考虑栈结构，先将所有数据按顺序入栈，然后按顺序和出栈元素比较，如果完全相同则可以断定一定为回文结构，此时的时间复杂度为 O(N)，额外的空间复杂度为 O(N)。<br>
 * > 考虑到额外空间复杂度要求为 O(1)，则需要从回文结构本身性质出发。如果能够找到链表的中间位置，然后将中间位置之后的链表逆序之后再从链表两端向中间移动比较，如果两个指针相遇前都相等则可断定链表是回文结构。对于找到链表的中间位置可以使用快慢指针法：快指针每次移动两个位置，慢指针每次移动一个位置，当快指针移动到链表末尾时慢指针刚好指向链表的中间位置。<br>
 *
 * 复杂度分析：
 *
 * > 快慢指针法找到链表中间位置的时间复杂度为 O(N)，找到中间位置后将中间位置之后的链表逆序的时间复杂度为 O(N)，链表从两端向中间位置比较移动的时间复杂度为 O(N)，链表还原的时间复杂度为 O(N)，因此总的时间复杂度为 O(N)。<br>
 * > 整个算法过程中只是引入了常数个指针，因此整体的额外空间复杂度为 O(1)。
 *
 * 算法实现
 *
 * ```go
 * // 快慢指针法找到链表中间位置
 * func (l *List) middle() *node {
 * 	// 快、慢指针
 * 	slow, fast := l.head, l.head
 * 	// 快指针每次移动两个位置，慢指针每次移动一个位置
 * 	for fast.next != nil && fast.next.next != nil {
 * 		slow = slow.next
 * 		fast = fast.next.next
 *        }
 * 	return slow
 * }
 * ```
 *
 * 通过快慢指针法找到链表中间位置后将后面的链表反转，然后从两端向中间移动比较，如果发现有不相同则说明链表不是回文结构：
 *
 * ```go
 * // 判断链表是否为回文结构
 * func (l *List) isPalindrome() bool {
 * 	// 空链表返回 true
 * 	if l == nil || l.head == nil {
 * 		return true
 *    }
 * 	// 只有一个结点的链表返回 true
 * 	if l.head.next == nil {
 * 		return true
 *    }
 * 	// 找到链表中间位置
 * 	mid := l.middle()
 * 	// 从中间位置开始到链表尾部指针反转
 * 	p := mid.next
 * 	mid.next = nil
 * 	for p != nil {
 * 		help := p.next
 * 		p.next = mid
 * 		mid = p
 * 		p = help
 *    }
 * 	// 从两端向中间比较移动
 * 	h := l.head
 * 	t := mid
 * 	flag := true
 * 	for h != nil {
 * 		if h.value != t.value {
 * 			flag = false
 * 			break
 *        }
 * 		h = h.next
 * 		t = t.next
 *    }
 * 	// 还原链表
 * 	node := mid.next
 * 	mid.next = nil
 * 	for node != nil {
 * 		help := node.next
 * 		node.next = mid
 * 		mid = node
 * 		node = help
 *    }
 * 	return flag
 * }
 * ```
 *
 * ```java
 * public Node midleNode(Node head){
 *     Node slow = head;
 *     Node fast = head;
 *     while(fast.next != null && fast.next.next != null){
 *         slow = slow.next;
 *         fast = fast.next.next;
 *     }
 *     return slow;
 * }
 *
 * public boolean isPalindrome(Node head){
 *     if (head == null || head.next == null){
 *         return true;
 *     }
 *     // 找到链表中间位置
 *     Node mid = midleNode(head);
 *     // 将链表中间位置之后的节点逆序
 *     Node node = mid.next;
 *     mid.next = null;
 *     while(node != null){
 *         help = node.next
 *         node.next = mid;
 *         node = help
 *     }
 *     // 遍历比较是否是回文
 *     // 还原链表
 * }
 * ```
 *
 * ###
 */
public class Parlaim {
}
