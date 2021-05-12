package example.classic.ds.skipList;

public class SkipListNode<T> {

    T value;

    double score;

    SkipListNode<T> next;

    public SkipListNode(T value, double score){
        this.value = value;
        this.score = score;
    }
}
