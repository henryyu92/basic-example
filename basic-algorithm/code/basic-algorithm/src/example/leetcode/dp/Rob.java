package example.leetcode.dp;

import example.leetcode.tree.TreeNode;

/**
 *
 */
public class Rob {

    /**
     * 你是一个专业的小偷，计划偷窃沿街的房屋。每间房内都藏有一定的现金，影响你偷窃的唯一制约因素就是相邻的房屋装有相互连通的防盗系统，如果两间相邻的房屋在同一晚上被小偷闯入，系统会自动报警。
     *
     *  给定一个代表每个房屋存放金额的非负整数数组，计算你 不触动警报装置的情况下 ，一夜之内能够偷窃到的最高金额
     *
     *  dp[i] 偷窃 i 个房子的最高金额
     *          dp[i] = max(dp[i-1], dp[i-2]+arr[i])
     *
     *          dp[0] = nums[0]
     *
     */
    public int rob_1(int[] nums){

        if (nums == null || nums.length == 0){
            return 0;
        }

        int n = nums.length;

        if (n == 1){
            return nums[0];
        }

        int[] dp = new int[n];
        dp[0] = nums[0];
        dp[1] = Math.max(nums[0], nums[1]);

        for (int i = 2; i < n; i++){
            dp[i] = Math.max(dp[i-1], dp[i-2] + nums[i]);
        }
        return dp[n-1];
    }


    /**
     * 你是一个专业的小偷，计划偷窃沿街的房屋，每间房内都藏有一定的现金。这个地方所有的房屋都 围成一圈 ，这意味着第一个房屋和最后一个房屋是紧挨着的。同时，相邻的房屋装有相互连通的防盗系统，如果两间相邻的房屋在同一晚上被小偷闯入，系统会自动报警 。
     *
     *    给定一个代表每个房屋存放金额的非负整数数组，计算你 在不触动警报装置的情况下 ，今晚能够偷窃到的最高金额
     *
     *
     *    如果偷窃第一个房子，则最大金额只能在 [0, i-1] 之间产生，如果不偷窃第一个房子，则最大金额在 [1, i] 之间产生
     *
     *    边界条件： 如果只有一个房子则为 nums[0]
     *              如果有两个房子，则为 Math.max(nums[0], nums[1])
     *
     */
    public int rob_2(int[] nums){

        if (nums == null || nums.length == 0){
            return 0;
        }
        int n = nums.length;
        if (n == 1){
            return nums[0];
        }
        if (n == 2){
            return Math.max(nums[0], nums[1]);
        }

        return Math.max(rob_helper(nums, 0, n-2), rob_helper(nums, 1, n-1));
    }

    public int rob_helper(int[] nums, int left, int right){
        int first = nums[left];
        int second = Math.max(nums[left], nums[left + 1]);

        for (int i = left + 2; i <= right; i++){
            int temp = second;
            second = Math.max(first + nums[i], second);
            first = temp;
        }
        return second;
    }


    /**
     * 相邻结点之间偷窃则会触发报警，计算最高能偷窃的金额
     *
     *  设 f(i) 为 i 结点被偷窃的最高金额， g(i) 为结点不被偷窃的最高金额
     *
     *  - 如果结点 o 被偷窃，则其左、右子结点不能被偷窃，最大金额为 f(o) + g(o.left) + g(o.right)
     *  - 如果结点 o 没有被偷窃，则左、右子结点可以被偷窃，也可以不被偷窃，最大金额为 max(f(o.left), g(o.left)) + max(f(o.right), g(o,right))
     *
     *  因此需要存储每个结点在偷窃和不偷窃时的最高金额
     *
     *  边界条件：
     *      - 结点为 null 返回 0
     *
     */
    public int rob_3(TreeNode root){

        int[] dfs = dfs(root);
        return Math.max(dfs[0], dfs[1]);
    }

    /**
     *
     *  arr[0] 存储偷窃时的最高金额
     *  arr[1] 存储不偷窃时的最高金额
     *
     */
    public int[] dfs(TreeNode node){
        if (node == null){
            return new int[]{0, 0};
        }
        int[] left = dfs(node.left);
        int[] right = dfs(node.right);

        int[] mount = new int[2];
        mount[0] = node.val + left[1] + right[1];
        mount[1] = Math.max(left[0], left[1]) + Math.max(right[0], right[1]);

        return mount;
    }

}
