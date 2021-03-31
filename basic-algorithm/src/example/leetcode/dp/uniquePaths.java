package example.leetcode.dp;

/**
 * 从 M * N 的网格左上角移动到右下角，每次只能向下或者向右移动一步，总共有多少种不同的路径
 */
public class uniquePaths {

    /**
     * f(i, j) 表示移动到 (i,j) 位置的路径数，则 f(i,j) = f(i-1,j) + f(i, j-1)
     * 边界条件： i = 0 或者 j=0 时只有一条路径
     */
    public int solution_dp(int m, int n) {

        if (m == 0 || n == 0) {
            return 1;
        }

        int[][] dp = new int[m][n];
        for (int i = 0; i < m; i++) {
            dp[i][0] = 1;
        }
        for (int i = 0; i < n; i++) {
            dp[0][i] = 1;
        }
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
            }
        }
        return dp[m - 1][n - 1];
    }
}
