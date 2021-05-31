package leetcode.dp;

import example.leetcode.tree.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class NumTrees {

    /**
     *  给定一个整数 n，求以 1 ... n 为节点组成的二叉搜索树有多少种
     *
     *  设 f(i) 表示 1 ... i 为节点组成的二叉搜索树的个数，则 f(i) 等于 1...i 中每个数为根结点的二叉搜索树之和
     *
     *  如果以 m 为根结点，则对应的二叉树个数为 f(m-1)*f(i-m)
     *
     *  于是 f(i) = f(0)(i-1) + ... + f(i-1)(0)
     *
     */
    public int numTress_dp(int n){

        int[] dp = new int[n+1];
        dp[0] = 1;

        // 计算每个 n 对应的二叉搜索树个数
        for (int i = 1; i <= n; i++){
            // 循环 1 - i 之内的每个数为根结点
            for (int j = 1; j <= i; j++){
                dp[i] += dp[j-1] * dp[i - j];
            }
        }
        return dp[n];
    }


    /**
     * 递归：以 i 为根结点，分别构造左二叉搜索树和右二叉搜索树
     *
     */
    public List<TreeNode> generateTrees(int n){
        return generateTrees(1, n);
    }

    public List<TreeNode> generateTrees(int left, int right){

        List<TreeNode> allTrees = new ArrayList<>();
        // 边界条件
        if (left > right){
            allTrees.add(null);
            return allTrees;
        }

        // 每个都可以作为根结点
        for (int i = left; i <= right; i++){

            List<TreeNode> leftTrees = generateTrees(left, i - 1);
            List<TreeNode> rightTrees = generateTrees(i + 1, right);

            for (TreeNode leftTree : leftTrees) {
                for (TreeNode rightTree : rightTrees) {
                    TreeNode root = new TreeNode(i);
                    root.left = leftTree;
                    root.right = rightTree;
                    allTrees.add(root);
                }
            }

        }
        return allTrees;
    }
}
