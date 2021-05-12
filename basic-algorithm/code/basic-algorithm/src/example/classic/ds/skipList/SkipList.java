package example.classic.ds.skipList;

import example.leetcode.list.ListNode;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * SkipList 核心数据结构：
 *  - level 表示每个节点的层数
 *  - 每层都是链表结构
 */
public class SkipList<T> {

  private static final int MAX_LEVEL = 1<<6;

  private int level;

  private List<SkipListNode<T>> head;

  private Random r = new Random();

  public SkipList(int level){
    this.level = level;
    head = new ArrayList<>(level);
  }

  private int getRandomLevel(){
    int level = 1;
    while (r.nextInt() % 2 == 0){
      level++;
    }
    return Math.min(level, MAX_LEVEL);
  }

  public T get(double score){

    int h = level;
    SkipListNode<T> curr = head.get(level);
    while (h-- > 0){
      curr = findNext(curr, score);
    }

    return curr == null ? null : curr.value;
  }

  public void add(T v, double score){

  }


  /**
   * 返回不大于 score 的 SkipListNode
   */
  public SkipListNode<T> findNext(SkipListNode<T> current, double score){
    SkipListNode<T> next = current.next;
    while (next != null){
      if (next.score > score){
        break;
      }
      current = next;
      next = next.next;
    }
    return current;
  }

}
