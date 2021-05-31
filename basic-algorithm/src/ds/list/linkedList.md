## 链表
链表时一种物理存储单元上非连续、非顺序的存储结构，数据元素的逻辑顺序是通过链表中的指针链接顺序实现。

链表由一些列结点(链表中每一个元素称为结点)组成，结点可以在运行时动态生成，每个结点包括两部分：存储数据元素的数据域和存储下一个结点地址的指针域。

链表数据元素不需要按照顺序存储，因此在插入数据时可以达到 O(1) 的时间复杂度；但是也是由于不是顺序存储的，链表不能随机读取数据元素，即数据元素的读取时间复杂度为 O(N)。

链表的结构使得其不需要预先直到数据的大小，可以充分利用内存空间实现灵活的内存动态管理。链表的结点增加了指针域，因此需要额外的空间消耗；并且由于其非连续的特点，当链表数据量较大时会使得内存碎片化变得严重。

链表通常使用节点相连，节点中包含下一个节点的指针、节点中的数据元素：
```java
public class Node<T>{
    T value;
    Node<T> next;
}
```

- 双向链表

- 环形链表

```java

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
**[Back](../)**