package example.tree;

import sun.tools.jstat.Jstat;

import java.util.*;

/**
 * 二叉树
 */
public class BinaryTree<T extends Comparable<T>> {

    private TreeNode<T> root;


    /**
     * 递归前序遍历：根 -> 左 -> 右
     */
    public void preOrderRecur() {
        if (root == null) {
            return;
        }
        System.out.print(root.value() + " ");

        // 左子树
        BinaryTree<T> left = new BinaryTree<>();
        left.root = root.left();
        left.preOrderRecur();

        // 右子树
        BinaryTree<T> right = new BinaryTree<>();
        right.root = root.right();
        right.preOrderRecur();
    }

    /**
     * 非递归前序遍历
     * 非递归遍历需要使用到栈结构，非递归前序遍历时右子结点需要先入栈，这样可以保证出栈时左子结点先出栈
     */
    public void preOrderNonRecur() {
        if (root == null) {
            return;
        }

        Stack<TreeNode<T>> stack = new Stack<>();

        stack.push(root);

        while (!stack.isEmpty()) {

            // pop 结点为子树根结点
            TreeNode<T> head = stack.pop();
            System.out.print(head.value() + " ");

            // 右子结点先入栈
            if (head.right() != null) {
                stack.push(head.right());
            }

            // 左子结点后入栈
            if (head.left() != null) {
                stack.push(head.left());
            }
        }

    }

    /**
     * 递归中序遍历：左 -> 根 -> 右
     */
    public void inOrderRecur() {

        if (root == null) {
            return;
        }

        // 遍历左子树
        BinaryTree<T> left = new BinaryTree<>();
        left.root = root.left();
        left.inOrderRecur();

        // 根节点
        System.out.print(root.value() + " ");

        // 遍历右子树
        BinaryTree<T> right = new BinaryTree<>();
        right.root = root.right();
        right.inOrderRecur();

    }

    /**
     * 非递归中序遍历
     * 非递归中序遍历将所有左子结点入栈直到没有左子结点，此时栈顶元素为最左子树的根节点
     */
    public void inOrderNonRecur() {

        if (root == null) {
            return;
        }
        Stack<TreeNode<T>> stack = new Stack<>();
        TreeNode<T> curr = root;
        while (!stack.isEmpty() || curr != null) {
            if (curr != null) {
                // 左子树处理
                stack.push(curr);
                curr = curr.left();
            } else {
                // 没有左子结点表示为最左子树
                TreeNode<T> node = stack.pop();
                System.out.print(node.value() + " ");
                // 右子树处理
                curr = node.right();
            }
        }

    }


    /**
     * 递归后序遍历：左 -> 右 -> 根
     */
    public void postOrderRecur() {

        if (root == null) {
            return;
        }

        // 遍历左子树
        BinaryTree<T> left = new BinaryTree<>();
        left.root = root.left();
        left.postOrderRecur();

        // 遍历右子树
        BinaryTree<T> right = new BinaryTree<>();
        right.root = root.right();
        right.postOrderRecur();

        System.out.print(root.value() + " ");
    }

    /**
     * 非递归后序遍历
     *    使用两个栈
     */
    public void postOrderNonRecur() {
        if (root == null) {
            return;
        }

        Stack<TreeNode<T>> stack = new Stack<>();
        Stack<TreeNode<T>> help = new Stack<>();

        stack.push(root);

        while (!stack.isEmpty()){
            // 根节点先入 help 栈
            TreeNode<T> node = stack.pop();
            help.push(node);

            // 左子树入 stack 栈
            if (node.left() != null){
                stack.push(node.left());
            }

            // 右子树入 stack 栈
            if (node.right() != null){
                stack.push(node.right());
            }
        }

        while (!help.isEmpty()){
            System.out.print(help.pop().value() + " ");
        }

    }

    public void postOrderNonRecur2(){
        if (root == null){
            return;
        }

        Stack<TreeNode<T>> stack = new Stack<>();
        stack.push(root);

        TreeNode<T> h = null;
        while (!stack.isEmpty()){
            TreeNode<T> node = stack.peek();
            if (node.left() != null && node.left() != h && node.right() != h){
                stack.push(node.left());
            }else if(node.right() != null && node.right() != h){
                stack.push(node.right());
            }else{
                // 结点出栈
                System.out.print(stack.pop().value() + " ");
                h = node;
            }
        }
    }

    /**
     * 按层遍历：使用队列
     */
    public void levelTraversal(){

        if (root == null){
            return;
        }

        Queue<TreeNode<T>> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()){
            // 结点出队列后，将左子结点和右子结点入队列
            TreeNode<T> node = queue.poll();
            System.out.print(node.value() + " ");
            if (node.left() != null){
                queue.offer(node.left());
            }
            if (node.right() != null){
                queue.offer(node.right());
            }
        }

    }

    /**
     * 添加结点
     * @param value
     * @return
     */
    public boolean add(T value) {
        TreeNode<T> node = new TreeNode<>(value);
        if (root == null) {
            root = node;
            return true;
        }

        Queue<TreeNode<T>> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()){
            TreeNode<T> n = queue.poll();
            if (n.left() == null){
                n.setLeft(node);
                return true;
            }
            queue.offer(n.left());

            if (n.right() == null){
                n.setRight(node);
                return true;
            }
            queue.offer(n.right());
        }
        return false;
    }


    public static void main(String[] args) {

        final int SIZE = 10;
        Integer[] array = new Integer[SIZE];
        Random r = new Random(System.currentTimeMillis());

        BinaryTree<Integer> tree = new BinaryTree<>();
        for (int i = 0; i < SIZE; i++){
            array[i] = r.nextInt(100);
            tree.add(array[i]);
        }

        System.out.println("arr: " + Arrays.asList(array));

        System.out.print("树=========");
        tree.levelTraversal();

        System.out.println();
        System.out.print("前序遍历 递归============");
        tree.preOrderRecur();
        System.out.println();
        System.out.print("前序遍历 非递归============");
        tree.preOrderNonRecur();

        System.out.println();
        System.out.print("中序遍历 递归============");
        tree.inOrderRecur();
        System.out.println();
        System.out.print("中序遍历 非递归============");
        tree.inOrderNonRecur();
        System.out.println();

        System.out.println();
        System.out.print("后序遍历============");
        tree.postOrderRecur();
        System.out.println();
        System.out.print("后序遍历 非递归============");
        tree.postOrderNonRecur();

    }

}
