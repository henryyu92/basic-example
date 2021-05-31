package example.leetcode.list;

/**
 * 将链表按照一个给定的值划分为左、中、右三部分，要求左部分的节点值小于给定值，中间部分的节点值等于给定值，右边部分的节点值大于给定值
 *
 * 解题思路：这是荷兰国旗问题的变种，考虑到链表不适合像数组一样交换位置，因此需要使用额外的指针实现。定义 6 个指针分别为 gtHead, gtTail, eqHead, eqTail, ltHead, ltTail 表示大于区的指针范围、等于区指针范围、小于区指针范围。遍历整个链表，根据与给定值得比较结果调整链表的指针即可完成划分。
 *
 * 算法实现：
 *
 * ```java
 * public Node partitionNode(Node head, int k){
 *     Node gtHead, gtTail, eqHead, eqTail, ltHead, ltTail = null;
 *     while(head != null){
 *         if(head.val < k){
 *             if(ltHead == null){
 *                 ltHead = ltTail = head;
 *             }else{
 *                 // 实现小于区的链接
 *                 ltTail.next = head;
 *                 ltTail= ltTail.next;
 *             }
 *         }else if(head.val == k){
 *             if(eqHead == null){
 *                 eqHead = eqTail = head;
 *             }else{
 *                 // 实现等于去的连接
 *                 eqTail.next = head;
 *                 eqTail = eqTail.next;
 *             }
 *         }else {
 *             if(gtHead == null){
 *                 gtHead = gtTail = head;
 *             }else{
 *                 // 实现大于区的连接
 *                 gtTail.next = head;
 *                 gtTail = gtTail.next;
 *             }
 *         }
 *         head = head.next;
 *     }
 *     // 合并小于区、等于区、大于区
 *     if(ltTail != null){
 *         if(eqTail != null){
 *             ltTail.next = eqHead;
 *             if(gtTail != null){
 *                 eqTail.next = gtHead;
 *                 // 此处需要设置为 null 避免发生死循环
 *                 gtTail.next = null;
 *                 return ltHead;
 *             }else{
 *                 eqTail.next = null;
 *                 return ltHead;
 *             }
 *         }else{
 *             if(gtTail != null){
 *                 ltTail.next = gtHead;
 *                 gtTail.next = null;
 *                 return ltHead;
 *             }else{
 *                 ltTail.next = null;
 *                 return ltHead;
 *             }
 *         }
 *     }else{
 *         if(eqTail != null){
 *             if(gtTail != null){
 *                 eqTail.next = gtHead;
 *                 gtTail.next = null;
 *                 return eqHead;
 *             }else{
 *                 eqTail.next = null;
 *                 return eqHead;
 *             }
 *         }else{
 *            return gtHead;
 *         }
 *     }
 * }
 * ```
 *
 */
public class PartitionNode {
}
