package example.list;

import java.util.ArrayList;

/**
 * 跳跃表
 * @param <T>
 */
public class SkipList<T> {

    /**
     * 跳跃表节点包含一个值 value 和多层 level，每层都指向一个跳跃表节点
     * @param <T>
     */
    class SkipListNode<T>{
        T value;
        Integer level;
        ArrayList<SkipListNode<T>> nexts;

        SkipListNode(T value, Integer level){
            this.value = value;
            this.level = level;
            nexts = new ArrayList<>(level);
        }

        SkipListNode<T> growth(int n){
            this.level += n;
            ArrayList<SkipListNode<T>> newNode = new ArrayList<>(this.level);
            newNode.addAll(nexts);
            return this;
        }
    }
}
