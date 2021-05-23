package example.leetcode.dp;

import java.util.Arrays;

/**
 * 零钱兑换：给定不同面额的硬币 coins 和总金额 amount,返回凑成总金额所需的最小金币数
 *
 *      假设 F(i) 为凑成总数为 i 所需最少的金币数，则 F(i) = min(F(i-j1), F(i-j2), ...) + 1，其中 j 表示金币的面额种类
 *
 * 零钱兑换是 0-1 背包问题：
 *    
 *
 */
public class CoinChange {

    public int coinChange(int[] coins, int amount){
        int max = amount + 1;
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, max);
        dp[0] = 0;
        for (int i = 1; i <= amount; i++) {
            for (int j = 0; j < coins.length; j++) {
                if (coins[j] <= i) {
                    dp[i] = Math.min(dp[i], dp[i - coins[j]] + 1);
                }
            }
        }
        return dp[amount] > amount ? -1 : dp[amount];
    }


    /**
     *  将硬币按照额度排序，然后采用 dfs 遍历(回溯)
     *
     */
    private static res = Integer.MAX_VALUE;

     public int coinChange_2(int[] coins, int amount){
         if(coins == null || coins.length == 0){
             return -1;
         }
         Arrasy.sort(coins);

         bfs(coins, amount, 0, coins.length);
        return res == Integer.MAX_VALUE ? -1 : res;
     }

    /**
     * coins 表示硬币数组，已排好序
     * amount 表示需要组合的金额
     * index 表示使用金币的额度下标
     */
     private void bfs(int[] coins, int amount,int count, int index){
         if(amount == 0) {
             res = Math.min(res, count);
             return;
         }
         if(index < 0){
             return;
         }
         for(int i = amount/coins[index]; i >= 0 && count + i < res; i--){
            bfs(coins, amount - coins[index] * i, count + i, index-1)
         }
     }
}
