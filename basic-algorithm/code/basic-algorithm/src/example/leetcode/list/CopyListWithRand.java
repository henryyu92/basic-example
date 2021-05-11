package example.leetcode.list;

/**
 * 复制含有随机指针节点的链表
 *
 * 解题思路：
 *
 * ```java
 * public Node copyListWithRand(Node head){
 *     HashMap<Node, Node> map = new HashMap<>();
 *     Node cur = head;
 *     while(cur != null){
 *         map.put(cur, new Node(cur.value));
 *         cur = cur.next;
 *     }
 *     Node x = head;
 *     while(x != null){
 *         map.get(x).next = map.get(x.next);
 *         map.get(x).rand = map.get(x.rand);
 *         x = x.next;
 *     }
 *     return map.get(head);
 * }
 * ```
 *
 * 另外一种实现：
 *
 * ```java
 * public Node copyListWithRand(Node head){
 *     if (head == null){
 *         return null;
 *     }
 *     // 拷贝节点加入原始链表
 *     Node cur = head;
 *     Node next = null;
 *     while(cur != null){
 *         next = cur.next;
 *         cur.next = new Node(cur.value);
 *         cur.next.next = next;
 *         cur = next;
 *     }
 *     // rand 指针设置
 *     cur = head;
 *     Node curCopy = null;
 *     while(cur != null){
 *         curCopy = cur.next;
 *         curCopy.rand = cur.rand != null ? cur.rand.next : null;
 *         cur = cur.next.next;
 *     }
 *     // 分离
 *     Node res = head.next;
 *     cur = head;
 *     while(cur != null){
 *         next = cur.next.next;
 *         curCopy = cur.next;
 *         cur.next = next;
 *         cur.Copy = next != null ? next.next : null;
 *         cur = next;
 *     }
 *     return res;
 * }
 * ```
 *
 * ###
 */
public class CopyListWithRand {
}
