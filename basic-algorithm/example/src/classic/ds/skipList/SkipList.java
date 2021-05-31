package example.classic.ds.skipList;

import java.util.Random;

/**
 * SkipList 核心数据结构：
 *  - level 表示每个节点的层数
 *  - 每层都是链表结构
 */
public class SkipList<T> {

  private static final int MAX_LEVEL = 1<<6;

  private int level;

  private SkipListNode<T> head;

  private int size;

  private Random r = new Random();

  public SkipList(){
    this.level = 0;
    size = 0;
    head = new SkipListNode<>(null, Integer.MIN_VALUE);
    head.nextNodes.add(null);
  }

  private int getRandomLevel(){
    int level = 1;
    while (r.nextInt() % 2 == 0){
      level++;
    }
    return Math.min(level, MAX_LEVEL);
  }

  public T get(double score){
    SkipListNode<T> node = find(head, score, level);
    if (node.score != score){
      return null;
    }
    return node.value;
  }

  /**
   * 添加值需要先找到对应节点的位置，然后哦随机生成 level，最后需要维护每层之间的引用关系
   */
  public void add(T v, double score){
    SkipListNode<T> prev = find(head, score, level);
    // 已经存在值了
    if (prev.score == score){
      return;
    }
    size++;

    // 随机生成 level
    int newLevel = getRandomLevel();
    // head 节点的 level 最高
    while (level < newLevel){
      head.nextNodes.add(null);
      level++;
    }

    // 从最高层开始构建引用关系
    SkipListNode<T> newNode = new SkipListNode<>(v, score);
    SkipListNode<T> current = head;
    do {
      current = findNext(current, score, newLevel);
      newNode.nextNodes.add(0, current.nextNodes.get(newLevel));
      current.nextNodes.set(newLevel, newNode);
    }while (newLevel-- > 0);
  }

  /**
   *  先找到指定位置，删除后需要调整每层之间的引用关系 
   */
  public void delete(double score){
    SkipListNode<T> prev = find(head, score, level);
    // 没有找到
    if(prev.score != score){
      return;
    }
    // 从上层开始重新调整引用关系
    for(int i = prev.nextNodes.size() - 1; i >= 0; i--){
      
    }
  }

  public int size(){
    return this.size;
  }


  public SkipListNode<T> find(SkipListNode<T> current, double score, int level){
    do {
      current = findNext(current, score, level);
    }while (level-- > 0);
    return current;
  }


  /**
   * 返回当前层不大于给定值的最大节点
   */
  public SkipListNode<T> findNext(SkipListNode<T> current, double score, int level){

    SkipListNode<T> nextNode = current.nextNodes.get(level);
    while (nextNode != null){
      if (score < nextNode.score){
        break;
      }
      current = nextNode;
      nextNode = current.nextNodes.get(level);
    }
    return current;
  }

}
