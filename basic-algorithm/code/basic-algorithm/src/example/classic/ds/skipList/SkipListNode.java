package example.classic.ds.skipList;

import example.leetcode.list.ListNode;

/**
 * SkipList 核心数据结构：
 *  - level 表示每个节点的层数
 *  - 每层都是链表结构
 */
public class SkipListNode {

    private int level;

    private ListNode[] nodes;
}
