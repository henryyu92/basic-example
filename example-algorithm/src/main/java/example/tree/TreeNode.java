package example.tree;

/**
 * 树结点
 */
public class TreeNode<T> {
    private T value;
    private TreeNode<T> left;
    private TreeNode<T> right;

    public TreeNode(T value){
        this.value = value;
    }

    public TreeNode<T> left(){
        return left;
    }

    public void setLeft(TreeNode<T> left){
        this.left = left;
    }

    public TreeNode<T> right(){
        return right;
    }

    public void setRight(TreeNode<T> right){
        this.right = right;
    }

    public T value(){
        return value;
    }
}
