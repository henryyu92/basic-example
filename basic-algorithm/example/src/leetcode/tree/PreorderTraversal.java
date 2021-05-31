package leetcode.tree;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * 二叉树前序遍历
 */
public class PreorderTraversal {


    /**
     *  递归：
     *      前序遍历按照 根 - 左 - 右 遍历树
     *
     *  时间复杂度： O(N)
     *  空间复杂度： O(N)，和递归栈的深度相关，如果树退化为链表则需要递归 N 次
     */
    public List<Integer> preorderTraversal_recursive(TreeNode root){

        List<Integer> preorder = new ArrayList<>();
        if (root == null){
            return preorder;
        }

        // 遍历根结点
        preorder.add(root.val);

        // 遍历左子树
        preorder.addAll(preorderTraversal_recursive(root.left));

        // 遍历右子树
        preorder.addAll(preorderTraversal_recursive(root.right));

        return preorder;
    }


    /**
     *  栈：
     *      右子结点先入栈，然后左子结点入栈，然后出栈就会先遍历左子树，后遍历右子树
     *
     *  时间复杂度： O(N)
     *  空间复杂度： O(N)
     */
    public List<Integer> preorderTraversal_stack(TreeNode root){

        List<Integer> preorder = new ArrayList<>();
        Deque<TreeNode> stack = new LinkedList<>();
        stack.push(root);

        while (!stack.isEmpty()){
            // 入栈之后立即出栈，保证在根结点在子树之前遍历
            root = stack.pop();
            // 没有子树则继续出栈
            if (root == null){
                continue;
            }
            preorder.add(root.val);
            stack.push(root.right);
            stack.push(root.left);
        }
        return preorder;
    }


    /**
     * 莫里斯遍历：
     *
     *      采用莫里斯遍历时，建立和子树最右结点关联后需要先输出根结点，然后在转向左子树
     *
     *  时间复杂度： O(N)
     *  空间复杂度： O(1)
     *
     */
    public List<Integer> preorderTraversal_morris(TreeNode root){
        List<Integer> preorder = new ArrayList<>();
        TreeNode pre;

        while (root != null){
            // 没有左子树，则遍历右子树
            if (root.left == null){
                preorder.add(root.val);
                root = root.right;
            }else{
                // 寻找左子树的最右结点
                pre = root.left;
                while (pre.right != null && pre.right != root){
                    pre = pre.right;
                }
                // 未建立连接，则建立连接，输出根结点，然后转入左子树
                if (pre.right == null){
                    preorder.add(root.val);
                    pre.right = root;
                    root = root.left;
                }else{
                    // 已经建立连接，说明已经遍历完左子树，转向右子树
                    root = root.right;
                    pre.right = null;
                }
            }
        }
        return preorder;
    }


    public static void main(String[] args) {

        TreeNode root = TreeTestUtil.getTree(20, 1, 20);

        PreorderTraversal preorder = new PreorderTraversal();

        List<Integer> recursive = preorder.preorderTraversal_recursive(root);

        System.out.println("recursive preorder: " + recursive);

        List<Integer> stack = preorder.preorderTraversal_stack(root);
        System.out.println("stack preorder: " + stack);

        List<Integer> morris = preorder.preorderTraversal_morris(root);
        System.out.println("morris preorder: " + morris);

    }
}
