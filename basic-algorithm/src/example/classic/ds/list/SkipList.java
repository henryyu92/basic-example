package example.classic.ds.list;

import java.util.ArrayList;

/**
 * 跳跃表
 * @param <T>
 */
public class SkipList<T> {

    /**
     * 跳跃表节点包含三个属性：
     *  - value     节点存储的值
     *  - level     节点的层数
     *  - list      节点的每层都指向一个跳跃表节点(可相同)
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
