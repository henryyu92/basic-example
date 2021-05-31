package classic.ds.list;

import java.util.ArrayList;
import java.util.List;

public class SkipListNode<T> {

    T value;

    double score;

    List<SkipListNode<T>> nextNodes;

    public SkipListNode(T value, double score){
        this.value = value;
        this.score = score;
        nextNodes = new ArrayList<>();
    }
}
