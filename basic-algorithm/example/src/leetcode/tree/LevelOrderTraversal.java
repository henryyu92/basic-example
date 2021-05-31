package leetcode.tree;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * 二叉树层次遍历
 */
public class LevelOrderTraversal {

    /**
     * 层次遍历借助队列实现
     *
     */
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> leverOrder = new ArrayList<>();
        if (root == null){
            return leverOrder;
        }
        Deque<TreeNode> treeNodeDeque = new LinkedList<>();
        treeNodeDeque.offer(root);
        while (!treeNodeDeque.isEmpty()){
            // 每次将一层的结点取出
            int levelSize = treeNodeDeque.size();
            List<Integer> level = new ArrayList<>(levelSize);
            for (int i = 0; i < levelSize; i++){
                TreeNode node = treeNodeDeque.poll();
                level.add(node.val);
                if (node.left != null){
                    treeNodeDeque.offer(node.left);
                }
                if (node.right != null){
                    treeNodeDeque.offer(node.right);
                }
            }
            leverOrder.add(level);
        }
        return leverOrder;
    }

    public static void main(String[] args) {
        TreeNode root = TreeTestUtil.getTree(20, 1, 20);

        LevelOrderTraversal levelOrder = new LevelOrderTraversal();
        List<List<Integer>> lists = levelOrder.levelOrder(root);
        System.out.println("level order traversal: " + lists);
    }
}
