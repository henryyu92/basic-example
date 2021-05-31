package leetcode.dp;

import java.util.List;

/**
 * 给定三角形，找出自顶向下的最小路径和
 *
 * 每一步只能移动到下一行中相邻的结点上。相邻的结点 在这里指的是 下标 与 上一层结点下标 相同或者等于 上一层结点下标 + 1 的两个结点。也就是说，如果正位于当前行的下标 i ，那么下一步可以移动到下一行的下标 i 或 i + 1
 *      [2]
 *      [3, 4]
 *      [6, 5, 7]
 *      [4, 1, 8, 3]
 *
 *      f(i, j) 表示从 (0, 0) 到 (i, j) 的最小路径和，则 f(i, j) = min(f(i-1, j), f(i-1,j-1)) + arr(i,j)，其中 0 <= j <= i
 *
 *      base case:
 *          f(i, 0) = f(i-1, 0) + arr(i, 0)
 *          f(i, i) = f(i-1, i-1) + arr(i, i)
 *
 */
public class MinimumTotal {

    public int minimumTotal(List<List<Integer>> triangle){
        if (triangle == null || triangle.size() == 0){
            return 0;
        }

        int row = triangle.size();

        int[] dp = new int[row];
        int min = Integer.MAX_VALUE;

        for (int i = 0; i < row; i++){
            for (int j = i; j >= 0; j--){
                Integer v = triangle.get(i).get(j);
                if (j == 0){
                    dp[j] += v;
                }else if (j == i){
                    dp[j] = dp[j-1] + v;
                }else{
                    dp[j] = Math.min(dp[j-1], dp[j])+ v;
                }
                if (i == row - 1){
                    min = Math.min(min, dp[j]);
                }
            }
        }
        return min;
    }
}
