package leetcode.dp;

import java.lang.reflect.Array;

/**
 * 在一个 0 和 1 组成的二维矩阵内，找到只包含 1 的最大正方形，返回其面积
 */
public class MaximalSquare {

    /**
     *  动态规划：考虑 dp(i,j) 表示以 (i,j) 为正方形右下角且只包含 1 的最大正方形边长，则
     *     - 如果 (i, j) = 0 则 dp(i, j) = 0
     *     - 如果 (i, j) != 0 则 dp(i, j) = min(dp(i-1,j), dp(i, j-1), dp(i-1,j-1)) + 1
     */
    public int maximalSquare_dp(char[][] matrix){
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0){
            return 0;
        }
        int row = Array.getLength(matrix);
        int col = Array.getLength(matrix[0]);
        int maxSize = 0;

        // dp 初始化为 0
        int[][] dp = new int[row][col];

        for (int i = 0; i < row; i++){
            for (int j = 0; j < col; j++){
                if(matrix[i][j] == '1'){
                    if (i == 0 || j == 0){
                        dp[i][j] = 1;
                    }else{
                        dp[i][j] = Math.min(dp[i-1][j], Math.min(dp[i-1][j-1], dp[i][j-1])) + 1;
                    }
                    if (dp[i][j] > maxSize){
                        maxSize = dp[i][j];
                    }
                }
            }
        }
        return maxSize * maxSize;
    }


    /**
     * 优化： dp 数组实际上只使用了当前行和上一行的数据，因此只需要保存这两个数组即可
     */
    public int maximalSquare_dp2(char[][] matrix){
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0){
            return 0;
        }
        int row = Array.getLength(matrix);
        int col = Array.getLength(matrix[0]);
        int maxSize = 0;

        int[] upper = new int[col];
        int[] current = new int[col];
        for(int i = 0; i < row; i++){
            for (int j = 0; j < col; j++){
                if (matrix[i][j] == '1'){
                    if (i == 0){
                        current[j] = 1;
                    }else if (j == 0){
                        current[j] = 1;
                    }else {
                        current[j] = Math.min(current[j-1], Math.min(upper[j-1], upper[j]));
                    }
                    if (current[j] > maxSize){
                        maxSize = current[j];
                    }
                }
            }
            upper = current;
        }
        return maxSize * maxSize;
    }
}
