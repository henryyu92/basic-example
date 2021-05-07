## 链表
链表时一种物理存储单元上非连续、非顺序的存储结构，数据元素的逻辑顺序是通过链表中的指针链接顺序实现。链表由一些列结点(链表中每一个元素称为结点)组成，结点可以在运行时动态生成，每个结点包括两部分：存储数据元素的数据域和存储下一个结点地址的指针域。

链表数据元素不需要按照顺序存储，因此在插入数据时可以达到 O(1) 的时间复杂度；但是也是由于不是顺序存储的，链表不能随机读取数据元素，即数据元素的读取时间复杂度为 O(N)。

链表的结构使得其不需要预先直到数据的大小，可以充分利用内存空间实现灵活的内存动态管理。链表的结点增加了指针域，因此需要额外的空间消耗；并且由于其非连续的特点，当链表数据量较大时会使得内存碎片化变得严重。

链表通常使用节点相连，节点中包含下一个节点的指针、节点中的数据元素：
```java
public class Node<T>{
    T value;
    Node<T> next;
}
```

双向链表

环形链表

### 链表回文问题
问题描述：
> 对于一个单向链表，设计一个时间复杂度为 O(N)，额外空间复杂度为 O(1) 的算法，判断其是否为回文结构

思路：
> 对于回文问题，一般可以考虑栈结构，先将所有数据按顺序入栈，然后按顺序和出栈元素比较，如果完全相同则可以断定一定为回文结构，此时的时间复杂度为 O(N)，额外的空间复杂度为 O(N)。<br>
> 考虑到额外空间复杂度要求为 O(1)，则需要从回文结构本身性质出发。如果能够找到链表的中间位置，然后将中间位置之后的链表逆序之后再从链表两端向中间移动比较，如果两个指针相遇前都相等则可断定链表是回文结构。对于找到链表的中间位置可以使用快慢指针法：快指针每次移动两个位置，慢指针每次移动一个位置，当快指针移动到链表末尾时慢指针刚好指向链表的中间位置。<br>

复杂度分析：
> 快慢指针法找到链表中间位置的时间复杂度为 O(N)，找到中间位置后将中间位置之后的链表逆序的时间复杂度为 O(N)，链表从两端向中间位置比较移动的时间复杂度为 O(N)，链表还原的时间复杂度为 O(N)，因此总的时间复杂度为 O(N)。<br>
> 整个算法过程中只是引入了常数个指针，因此整体的额外空间复杂度为 O(1)。

算法实现
```go
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
```
通过快慢指针法找到链表中间位置后将后面的链表反转，然后从两端向中间移动比较，如果发现有不相同则说明链表不是回文结构：
```go
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
```

```java
public Node midleNode(Node head){
    Node slow = head;
    Node fast = head;
    while(fast.next != null && fast.next.next != null){
        slow = slow.next;
        fast = fast.next.next;
    }
    return slow;
}

public boolean isPalindrome(Node head){
    if (head == null || head.next == null){
        return true;
    }
    // 找到链表中间位置
    Node mid = midleNode(head);
    // 将链表中间位置之后的节点逆序
    Node node = mid.next;
    mid.next = null;
    while(node != null){
        help = node.next
        node.next = mid;
        node = help
    }
    // 遍历比较是否是回文
    // 还原链表
}
```
### 链表分区问题
将链表按照一个给定的值划分为左、中、右三部分，要求左部分的节点值小于给定值，中间部分的节点值等于给定值，右边部分的节点值大于给定值

解题思路：这是荷兰国旗问题的变种，考虑到链表不适合像数组一样交换位置，因此需要使用额外的指针实现。定义 6 个指针分别为 gtHead, gtTail, eqHead, eqTail, ltHead, ltTail 表示大于区的指针范围、等于区指针范围、小于区指针范围。遍历整个链表，根据与给定值得比较结果调整链表的指针即可完成划分。

算法实现：
```java
public Node partitionNode(Node head, int k){
    Node gtHead, gtTail, eqHead, eqTail, ltHead, ltTail = null;
    while(head != null){
        if(head.val < k){
            if(ltHead == null){
                ltHead = ltTail = head;
            }else{
                // 实现小于区的链接
                ltTail.next = head;
                ltTail= ltTail.next;
            }
        }else if(head.val == k){
            if(eqHead == null){
                eqHead = eqTail = head;
            }else{
                // 实现等于去的连接
                eqTail.next = head;
                eqTail = eqTail.next;
            }
        }else {
            if(gtHead == null){
                gtHead = gtTail = head;
            }else{
                // 实现大于区的连接
                gtTail.next = head;
                gtTail = gtTail.next;
            }
        }
        head = head.next;
    }
    // 合并小于区、等于区、大于区
    if(ltTail != null){
        if(eqTail != null){
            ltTail.next = eqHead;
            if(gtTail != null){
                eqTail.next = gtHead;
                // 此处需要设置为 null 避免发生死循环
                gtTail.next = null;
                return ltHead;
            }else{
                eqTail.next = null;
                return ltHead;
            }
        }else{
            if(gtTail != null){
                ltTail.next = gtHead;
                gtTail.next = null;
                return ltHead;
            }else{
                ltTail.next = null;
                return ltHead;
            }
        }
    }else{
        if(eqTail != null){
            if(gtTail != null){
                eqTail.next = gtHead;
                gtTail.next = null;
                return eqHead;
            }else{
                eqTail.next = null;
                return eqHead;
            }
        }else{
           return gtHead;
        }
    }
}
```
### 链表拷贝问题
复制含有随机指针节点的链表

解题思路：

```java
public Node copyListWithRand(Node head){
    HashMap<Node, Node> map = new HashMap<>();
    Node cur = head;
    while(cur != null){
        map.put(cur, new Node(cur.value));
        cur = cur.next;
    }
    Node x = head;
    while(x != null){
        map.get(x).next = map.get(x.next);
        map.get(x).rand = map.get(x.rand);
        x = x.next;
    }
    return map.get(head);
}
```
另外一种实现：
```java
public Node copyListWithRand(Node head){
    if (head == null){
        return null;
    }
    // 拷贝节点加入原始链表
    Node cur = head;
    Node next = null;
    while(cur != null){
        next = cur.next;
        cur.next = new Node(cur.value);
        cur.next.next = next;
        cur = next;
    }
    // rand 指针设置
    cur = head;
    Node curCopy = null;
    while(cur != null){
        curCopy = cur.next;
        curCopy.rand = cur.rand != null ? cur.rand.next : null;
        cur = cur.next.next;
    }
    // 分离
    Node res = head.next;
    cur = head;
    while(cur != null){
        next = cur.next.next;
        curCopy = cur.next;
        cur.next = next;
        cur.Copy = next != null ? next.next : null;
        cur = next;
    }
    return res;
}
```
### 链表相交问题
单链表可能有环，可能无环，给定两个单链表的头结点 head1 和 head2 返回两个链表相交的第一个节点。时间复杂度 O(N+M)，空间复杂度 O(1)

算法思想：
> 单链表有环判断：<br>
> 思路1：根据有环的概念，遍历链表如果一个结点出现两次则说明有环，因此可以使用 Hash 存储已经遍历的节点，在遍历前查询该结点是否存在，如果存在则表示有环，而第一个已经存在的节点即为入环节点，这种判断方法需要的空间复杂度为 O(N)。<br>
> 思路2：考虑使用两个指针遍历链表，快指针每次移动两个节点，慢指针每次移动一个结点，如果快指针指向了 Null 则肯定无环，如果在某一时刻快指针和慢指针指向同一个节点则说明该链表有环。当快慢指针相遇时，快指针回到头结点并每次移动一个结点，慢指针继续每次移动一个结点，当快慢指针再次相遇时，该节点即为第一个入环节点。

```java
public Noe getLoopNode(Node head){
    if(head == null || head.next == null || head.next.next == null){
        return null;
    }
    Node slow = head;
    Node fast = head.next.next;
    while(slow != fast){
        // 快指针指向 Null 表示无环
        if(fast.next == null || fast.next.next == null){
            return null;
        }
        slow = slow.next;
        fast = fast.next.next;
    }
    // 快指针回到头结点，每次移动一个节点，当快慢指针再次相遇的节点即为第一个入环节点
    fast = head;
    while(slow != fast){
        fast = fast.next;
        slow = slow.next;
    }
    return slow;
}
```
链表相交思路：如果两个链表都无环则只有 “Y” 这一种相交方式；如果两个链表都有环则会出现两种相交方式，一种是在非环部分相交，这种情况和两个链表都无环一样，另一种是在环上相交；如果只有一个链表有环则不会出现相交

```java
public Node noLoop(Node head1, Node head2){
    if(head1 == null || head2 == null){
        return null;
    }
    Node cur1 = head1;
    Node cur2 = head2;
    // 计算两个链表长度的差值
    int n = 0;
    while(cur1 != null){
        n++;
        cur1 = cur1.next;
    }
    while(cur2 != null){
        n--;
        cur2 = cur2.next;
    }
    // 两个链表最后节点不相等则说明绝对不相交(单链表)
    if(cur1 != cur2){
        return null;
    }
    cur1 = n > 0 ? head1 : head2;
    cur2 = cur1 == head1 ? head2 : head1;
    n = Math.abs(n);
    while(n != 0){
        cur1 = cur1.next;
        n--;
    }
    while(cur1 != cur2){
        cur1 = cur1.next;
        cur2 = cur2.next;
    }
    return cur1;
}
```
对于有环的情况有：
```java
public Node bothLoop(head1, loop1, head2, loop2){
    Node cur1 = null;
    Node cur2 = null;
    // 两个链表相交于环外
    if(loop1 == loop2){
        cur1 = head1;
        cur2 = head2;
        int n = 0;
        while(cur1 != loop1){
            n++;
            cur1 = cur1.next;
        }
        while(cur2 != loop1){
            n--;
            cur2 = cur2.next;
        }
        cur1 = n > 0 ? head1 : head2;
        cur2 = cur1 == head1 ? head2 : head1;
        n = Math.abs(n);
        while(n != 0){
            cur1 = cur1.next;
            n--;
        }
        while(cur1 != cur2){
            cur1 = cur1.next;
            cur2 = cur2.next;
        }
        return cur1;
    // 链表相交于环上
    }else{
        cur1 = loop1.next;
        while(cur1 != loop1){
            if(cur1 == loop2){
                return loop1;
            }
            cur1 = cur1.next;
        }
        return null;
    }
}
```
于是整个获取单链表相交节点的实现为：
```java
public Node getIntersactNode(Node head1, Node head2){
    if(head1 == null || head2 == null){
        return null;
    }
    Node loop1 = getLoopNode(head1);
    Node loop2 = getLoopNode(head2);
    // 两个单链表都无环
    if(loop1 == null && loop2 == null){
        return noLoop(head1, head2);
    }
    // 两个链表都有环
    if(loop1 != null && loop2 != null){
        return bothLoop(head1, loop1, head2, loop2);
    }
    return null;
}
```
### 约瑟夫环问题

模拟约瑟夫环过程：
```go
```
利用数学归纳法：
```go
```

```java
public Node josephusKill(Node node, int m){
    if(head == null || head.next == head || m < 1){
        return head;
    }
    Node cur = head.next;
    int tmp = 1;
    while(cur != head){
        tmp++;
        cur = cur.next;
    }
    tmp = getLive(tmp, m);
    while(--tmp != 0){
        head = head.next;
    }
    head.next = head;
    return head;
}

public int getLive(int i, int m){
    if( == 1){
        return 1;
    }
    return (getLive(i - 1, m) + m - 1) % i + 1;
}
```

**[Back](../)**