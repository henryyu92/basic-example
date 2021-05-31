package leetcode.dp;

/**
 * 从 M * N 的网格左上角移动到右下角，每次只能向下或者向右移动一步，总共有多少种不同的路径
 */
public class UniquePaths {

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

//        int[] dp = new int[n];
//        for (int i = 0; i < m; i++){
//            for (int j = 0; j < n; j++){
//                if (j == 0){
//                    dp[j] = 1;
//                    continue;
//                }
//                if (i == 0){
//                    dp[j] = dp[j-1];
//                    continue;
//                }
//                dp[j] += dp[j-1];
//            }
//        }
//        return dp[n-1];
    }


    /**
     * 从 M * N 的网格左上角移动到右下角，每次只能向下或者向右移动一步，总共有多少种不同的路径
     * 如果网格中有障碍物则不能通过，有障碍物用 1 表示
     *
     *      dp[i][j] = dp[i-1][j] + dp[i][j-1]
     *      dp[i][j] = 0 where arr[i][j] = 1
     *
     *  由于　dp[i][j] 只与 dp[i-1][j] 和 dp[i][j-1] 相关，因此只需要保留一个数组即可
     *
     *  dp[j-1] 表示当前行的前一列位置的路径数
     *  dp[j] 在更新前存储着上一行当前列位置的路径数
     */
    public int uniquePathsWithObstacles_dp(int[][] obstacleGrid){
        int row = obstacleGrid.length;
        int col = obstacleGrid[0].length;

        int[] dp = new int[col];

//        dp[0] = obstacleGrid[0][0] == 1 ? 0 : 1;
//
//        for (int i = 0; i < row; i++){
//            for (int j = 0;j < col; j++){
//                if (obstacleGrid[i][j] == 1){
//                    dp[j] = 0;
//                    continue;
//                }
//                if (j-1 >= 0 && obstacleGrid[i][j-1] == 0){
//                    dp[j] += dp[j-1];
//                }
//            }
//        }
        dp[0] = obstacleGrid[0][0] == 1 ? 0 : 1;
        for (int i = 0; i < row; i++){
            for (int j = 0; j < col; j++){
                if (obstacleGrid[i][j] == 1){
                    dp[j] = 0;
                    continue;
                }
                if (j == 0){
                    continue;
                }
                if (i == 0){
                    dp[j] = dp[j-1];
                    continue;
                }
                dp[j] += dp[j-1];
            }
        }

        return dp[col-1];
    }
}
