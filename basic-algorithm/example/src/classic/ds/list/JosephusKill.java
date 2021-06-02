package classic.ds.list;


/**
 *
 * 约瑟夫环：N个人围成一圈，从第一个开始报数，第 M个将被杀掉，下一个人接着重新报数，直到最后剩下一个，其余人都将被杀掉。
 *
 * https://www.cnblogs.com/kkrisen/p/3569281.html#undefined
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

    /**
     * 循环链表解约瑟夫环问题
     *
     */
    public int getAlive_duLinkList(ListNode<Integer> root, int m){
        if (root == null){
            return 0;
        }
        CircularLinkedList<Integer> duLinkedList = new CircularLinkedList<>(root);
        while (duLinkedList.size() > m){
            Integer remove = duLinkedList.remove(m);
        }

        return 0;
    }
}
