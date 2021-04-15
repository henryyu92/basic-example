package example.leetcode.tree;

import sun.awt.X11.XStateProtocol;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

/**
 * 二叉树中序遍历
 */
public class InorderTraversal {

    /**
     * 递归：
     *      二叉树的中序遍历按照  左 - 根 - 右 遍历结点
     *
     * 时间复杂度： O(N)
     * 空间复杂度： O(N)   使用递归需要消耗调用栈，递归的次数决定栈的深度
     */
    public List<Integer> inorderTraversal_recursive(TreeNode root){

        if (root == null){
            return new ArrayList<>();
        }

        // 递归遍历左子树
        List<Integer> leftTree = inorderTraversal_recursive(root.left);

        // 遍历根结点
        leftTree.add(root.val);

        // 递归遍历右子树
        leftTree.addAll(inorderTraversal_recursive(root.right));

        return leftTree;
    }


    /**
     *  栈：
     *      递归实际上使用了隐藏的栈，可以使用栈来显示模拟递归过程
     *
     *      root 指针一直向左子树方向移动直到最左结点，然后 root 指针转换到右子结点，重复操作
     *
     *      采用栈的方式是一种自底向上的方式
     *
     *  时间复杂度： O(N)
     *  空间复杂度： O(N)，当二叉树退化到链表时则栈中需要存储 n 个结点
     *
     */
    public List<Integer> inorderTraversal_stack(TreeNode root){
        List<Integer> inorder = new ArrayList<>();
        Deque<TreeNode> queue = new LinkedList<>();

        while (root != null || !queue.isEmpty()){
            // root 指针一直向左子树方向移动
            while (root != null){
                queue.push(root);
                root = root.left;
            }
            // root 指针移动到最左结点之后出栈，然后切换到右子结点
            root = queue.pop();
            inorder.add(root.val);
            root = root.right;
        }
        return inorder;
    }


    /**
     *
     *  莫里斯遍历：
     *      莫里斯遍历使用线索二叉树的数据结构遍历
     *
     *      - 将当前节点 current 初始化为根节点
     *      - 当 current 没有左子节点时将 current 输出并进入右子树
     *      - 当 current 有左子树，则找到左子树最右侧的结点 prev
     *          - 如果 prev 有右子结点，则说明已经左子树已经遍历完成，此时需要断开链接并转入右子树
     *          - 如果 prev 没有子结点，说明还没有建立线索二叉树，此时将 current = prev.next，然后进入左子树
     *
     */
    public List<Integer> inorderTraversal_morris(TreeNode root){

        List<Integer> inorder = new ArrayList<>();

        TreeNode pre;

        while (root != null){

            // 不存在左子树
            if (root.left ==  null){
                inorder.add(root.val);
                root = root.right;
            }else{

                pre =  root.left;
                // 寻找左子树的最右结点
                while (pre.right != null && pre.right != root){
                    pre = pre.right;
                }
                // 最右结点没有右子结点，则建立和 root 的关系，并且进入左子树
                if (pre.right == null){
                    pre.right = root;
                    root = root.left;
                }else {
                    // 说明左子树已经遍历完，此时需要断开链接，输出 root 结点，并进入右子树
                    inorder.add(root.val);
                    pre.right = null;
                    root = root.right;
                }
            }
        }
        return inorder;
    }




    public static void main(String[] args) {

        InorderTraversal inorderTraversal = new InorderTraversal();

        List<Integer> nodes = inorderTraversal.initNodes(20, 1, 20);

        System.out.println("pre inorder: " + nodes);

        TreeNode root = inorderTraversal.buildTree(nodes);

        List<Integer> recursive = inorderTraversal.inorderTraversal_recursive(root);

        System.out.println("inorder: " + recursive);

        List<Integer> stack = inorderTraversal.inorderTraversal_stack(root);

        if (Objects.equals(recursive, stack)){
            System.out.println("everything is ok for inorderTraversal_stack");
        }

        List<Integer> morris = inorderTraversal.inorderTraversal_morris(root);
        if (Objects.equals(recursive, morris)){
            System.out.println("everything is ok for inorderTraversal_morris");
        }
    }

    private List<Integer> initNodes(int length, int rangeLeft, int rangeRight){
        List<Integer> nodes = new ArrayList<>(length);
        for (int i = 0; i < length; i++){
            nodes.add((int) (Math.random() * (rangeRight - rangeLeft)));
        }
        return nodes;
    }

    private TreeNode buildTree(List<Integer> nodes){
        TreeNode root = new TreeNode(nodes.get(0));
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);
        TreeNode next;
        int i = 1, l = nodes.size();
        while ((next = queue.poll()) != null){
            if (i < l){
                next.left = new TreeNode(nodes.get(i++));
                queue.add(next.left);
            }
            if (i < l){
                next.right = new TreeNode(nodes.get(i++));
                queue.add(next.right);
            }
        }
        return root;
    }
}
