package example.leetcode.dp;

import java.util.Arrays;

/**
 * 零钱兑换：给定不同面额的硬币 coins 和总金额 amount,返回凑成总金额所需的最小金币数
 *
 *      假设 F(i) 为凑成总数为 i 所需最少的金币数，则 F(i) = min(F(i-j1), F(i-j2), ...) + 1，其中 j 表示金币的面额种类
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
}
