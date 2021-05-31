package leetcode.tree;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * 路径总和
 */
public class PathSum {

    /**
     *  路径总和 - 1： 给定二叉树根结点和目标整数，判断是否存在根结点到叶子结点路径上的结点和等于目标整数
     *
     *      - 树中节点的数目在范围 [0, 5000] 内
     *      - -1000 <= Node.val <= 1000
     *      - -1000 <= targetSum <= 1000
     *
     *  1. 广度优先搜索：记录根结点到当前结点的路径和，当路径和相等且没有子结点时则存在，遍历最后一层都不存在结点满足则不存在，遍历过程中可以剪枝(当前结点路径和大于目标整数，则子结点不需要遍历)
     *  2. 递归： 将问题转换为左子树存在 target - node.val 的路径或者右子树存在 target - node.val 的路径
     *
     */
    public boolean hasPathSum_1_recursive(TreeNode root, int targetSum) {
        if (root == null || root.val > targetSum){
            return false;
        }
        return hasPathSum_1_recursive(root.left, targetSum - root.val) || hasPathSum_1_recursive(root.right, targetSum - root.val);
    }

    public boolean hasPathSum_1_bfs(TreeNode root, int targetSum){
        Deque<ScoredTreeNode> queue = new LinkedList<>();
        if (root == null){
            return false;
        }
        queue.offer(new ScoredTreeNode(root, root.val));
        while (!queue.isEmpty()){
            ScoredTreeNode next = queue.poll();
            // 剪枝
//            if (next.score > targetSum){
//                continue;
//            }
            // 叶子结点且路径和等于目标值
            if (next.score == targetSum && next.node.left == null && next.node.right == null){
                return true;
            }
            if (next.node.left != null){
                queue.offer(new ScoredTreeNode(next.node.left, next.score + next.node.left.val));
            }
            if (next.node.right != null){
                queue.offer(new ScoredTreeNode(next.node.right, next.score + next.node.right.val));
            }
        }
        return false;
    }

    public static class ScoredTreeNode{
        TreeNode node;
        int score;

        public ScoredTreeNode(TreeNode node, int score){
            this.node = node;
            this.score = score;
        }
    }


    /**
     *  路径总和 - 2：给定二叉树根结点和目标整数，找出从根结点到叶子结点的路径和等于给定值的路径
     *
     *      1, 深度优先 + 回溯法：采用深度优先遍历所有叶子结点，如果不满足则回溯到上层结点
     *      2. 递归： 将问题转换为左子树路径和等于 target - node.val 的路径和右子树路径和等于 target - node.val 的路径
     *      3. 广度优先搜索： todo
     */
    public List<List<Integer>> pathSum_2_dfs(TreeNode root, int targetSum){
        List<List<Integer>> result = new ArrayList<>();
        if (root == null){
            return result;
        }
        dfs(root, targetSum, 0, new ArrayList<>(), result);
        return result;
    }

    // 回溯法需要将结果作为参数传入方法中记录
    public void dfs(TreeNode node, int target, int currentSum, List<Integer> currentPath, List<List<Integer>> result){
        currentPath.add(node.val);
        currentSum += node.val;
        // 符合的叶子结点
        if (node.left == null && node.right == null && currentSum == target){
            // 需要拷贝当前的路径，应为后续会有改动
            result.add(new ArrayList<>(currentPath));
        }
        // 做选择
        if (node.left != null){
            dfs(node.left, target, currentSum, currentPath, result);
        }
        if (node.right != null){
            dfs(node.right, target, currentSum, currentPath, result);
        }
        // 撤销选择，回溯
        currentPath.remove(currentPath.size() - 1);
    }

    /**
     * 时间复杂度：
     *
     */
    public List<List<Integer>> pathSum_2_recursive(TreeNode root, int targetSum){
        List<List<Integer>> result = new ArrayList<>();
        if (root.left == null && root.right == null && root.val == targetSum){
            List<Integer> newPath = new ArrayList<>();
            newPath.add(root.val);
            result.add(newPath);
        }
        if (root.left != null){
            List<List<Integer>> left = pathSum_2_recursive(root.left, targetSum - root.val);
            if (left.size() != 0){
                for (List<Integer> path : left){
                    ArrayList<Integer> newPath = new ArrayList<>();
                    newPath.add(root.val);
                    newPath.addAll(path);
                    result.add(newPath);
                }
            }
        }
        if (root.right != null){
            List<List<Integer>> right = pathSum_2_recursive(root.right, targetSum - root.val);
            if (right.size() != 0){
                for (List<Integer> path : right){
                    ArrayList<Integer> newPath = new ArrayList<>();
                    newPath.add(root.val);
                    newPath.addAll(path);
                    result.add(newPath);
                }
            }
        }
        return result;
    }


    /**
     *  路径总和 - 3：给定二叉树根结点和目标整数，找出路径和等于给定值的路径总数，路径不需要从根结点开始也不需要在叶子结点结束，但方向必须是向下的
     *
     *  todo
     */
    public int pathSum_3(TreeNode root, int targetSum){

        return 0;
    }

    public static void main(String[] args) {

        TreeNode root = TreeTestUtil.getTree(20, 1, 20);

        PathSum hasPathSum = new PathSum();

        // HashPathSum Test
        System.out.println(hasPathSum.hasPathSum_1_recursive(root, 10));
        System.out.println(hasPathSum.hasPathSum_1_bfs(root, 10));

        // PathSum Test
        System.out.println(hasPathSum.pathSum_2_dfs(root, 30));
        System.out.println(hasPathSum.pathSum_2_recursive(root, 30));

    }
}
