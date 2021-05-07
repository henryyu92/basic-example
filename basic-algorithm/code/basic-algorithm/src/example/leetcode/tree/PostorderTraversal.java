package example.leetcode.tree;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * 二叉树后序遍历
 */
public class PostorderTraversal {


    /**
     * 递归：
     *      二叉树的后序遍历： 左 - 右 - 根
     */
    public List<Integer> postorderTraversal_recursive(TreeNode root){

        if (root == null){
            return new ArrayList<>();
        }

        // 遍历左子树
        List<Integer> orders = postorderTraversal_recursive(root.left);

        // 遍历右子树
        orders.addAll(postorderTraversal_recursive(root.right));

        // 遍历根结点
        orders.add(root.val);

        return orders;
    }


    /**
     *  栈：
     *      递归隐式使用了栈，可以通过栈来模拟递归
     *
     */
    public List<Integer> postorderTraversal_stack(TreeNode root){

        List<Integer> postorder = new ArrayList<>();
        if (root == null){
            return postorder;
        }
        Deque<TreeNode> stack = new LinkedList<>();

        TreeNode pre = null;

        // 自底向上，先遍历最左子树，然后向上
        while (!stack.isEmpty() || root != null){
            while (root != null){
                stack.push(root);
                root = root.left;
            }
            root = stack.pop();
            // 在向上时需要记录已经遍历的右子树
            if (root.right == null || root.right == pre){
                postorder.add(root.val);
                pre = root;
                // 防止再次进入左子树
                root = null;
            }else {
                stack.push(root);
                root = root.right;
            }
        }
        return postorder;
    }

    /**
     *  栈：
     *      根结点入栈后，先让右子结点入栈，然后左子结点入栈，如果没有子结点则出栈并标记当前遍历的位置
     *      - 如果左子结点有标记，则说明左子树遍历完成，此时出栈结点必然是右子结点或者根结点(没有右子结点)
     *      - 如果右子结点右标记，则说明右子树遍历完成，此时出栈结点必然是根结点
     *
     */
    public List<Integer> postorderTraversal_stack_1(TreeNode root){
        List<Integer> postorder = new ArrayList<>();
        Deque<TreeNode> stack = new LinkedList<>();
        if (root == null){
            return postorder;
        }
        stack.push(root);
        TreeNode pre = root;
        while (!stack.isEmpty()){
            root = stack.peek();
            // 子树已经遍历完，左子结点右标记则说明没有右子结点，左子树遍历完，右子结点有标记说明右子树遍历完，此时左子树一定已经遍历完
            if (root.right == pre || root.left == pre){
                root = stack.pop();
                pre = root;
                postorder.add(root.val);
            }else if (root.left != null || root.right != null){
                // 右子结点入栈
                if (root.right != null){
                    stack.push(root.right);
                }
                // 左子结点入栈
                if (root.left != null){
                    stack.push(root.left);
                }
            }else{
                // 没有子结点
                root = stack.pop();
                pre = root;
                postorder.add(root.val);
            }

        }
        return postorder;
    }

    /**
     *  莫里斯遍历：
     *      todo
     */
    public List<Integer> postorderTraversal_morris(TreeNode root){
        return null;
    }



    public static void main(String[] args) {

        TreeNode root = TreeTestUtil.getTree(20, 1, 20);

        PostorderTraversal postorder = new PostorderTraversal();

        List<Integer> recursive = postorder.postorderTraversal_recursive(root);
        System.out.println("recursive postorder: " + recursive);

        List<Integer> stack = postorder.postorderTraversal_stack(root);
        System.out.println("stack postorder: " + stack);

        List<Integer> stack_1 = postorder.postorderTraversal_stack_1(root);
        System.out.println("stack_1 postorder: " + stack_1);


    }

}
