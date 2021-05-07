package example.leetcode.dp;

/**
 * 给定一个包含非负整数的 m x n 网格 grid ，请找出一条从左上角到右下角的路径，使得路径上的数字总和为最小
 * 每次只能向下或者向右移动一步
 */
public class MinPathSum {

    /**
     *  dp[i][j] 表示从左上角到 [i][j] 的路径最小和，则有 dp[i][j] = min(dp[i-1][j], dp[i][j-1]) + arr[i][j]
     *
     *
     */
    public int minPathSum_dp(int[][] grid){

        int m = grid.length;
        int n = grid[0].length;

        int[] dp = new int[n];

        for (int i = 0; i < m; i++){
            for (int j = 0; j < n; j++){
                if (j == 0){
                    dp[j] = dp[j] + grid[i][j];
                    continue;
                }
                if (i == 0){
                    dp[j] = dp[j-1] + grid[i][j];
                    continue;
                }
                dp[j] = Math.min(dp[j-1], dp[j]) + grid[i][j];
            }
        }
        return dp[n-1];
    }
}
