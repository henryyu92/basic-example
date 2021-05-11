package example.leetcode.list;

/**
 *
 * 约瑟夫环
 *
 * public Node josephusKill(Node node, int m){
 *     if(head == null || head.next == head || m < 1){
 *         return head;
 *     }
 *     Node cur = head.next;
 *     int tmp = 1;
 *     while(cur != head){
 *         tmp++;
 *         cur = cur.next;
 *     }
 *     tmp = getLive(tmp, m);
 *     while(--tmp != 0){
 *         head = head.next;
 *     }
 *     head.next = head;
 *     return head;
 * }
 *
 * public int getLive(int i, int m){
 *     if( == 1){
 *         return 1;
 *     }
 *     return (getLive(i - 1, m) + m - 1) % i + 1;
 * }
 */
public class JosephusKill {
}
