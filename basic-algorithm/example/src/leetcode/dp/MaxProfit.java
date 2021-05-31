package leetcode.dp;

public class MaxProfit {

    /**
     *
     *  给定一个数组 prices ，它的第 i 个元素 prices[i] 表示一支给定股票第 i 天的价格
     *  你只能选择 某一天 买入这只股票，并选择在 未来的某一个不同的日子 卖出该股票。设计一个算法来计算你所能获取的最大利润。
     *
     *  返回你可以从这笔交易中获取的最大利润。如果你不能获取任何利润，返回 0 。
     *
     *      记录 0 - i 天的最低价格，然后计算第 i 天的最利润
     *
     */
    public int maxProfit_1(int[] prices){

        int minPrice = prices[0];
        int maxProfit = 0;

        for (int i = 0; i < prices.length; i++){

            if (prices[i] < minPrice){
                minPrice = prices[i];
            }
            int profit = prices[i] - minPrice;
            if (profit > maxProfit){
                maxProfit = profit;
            }
        }
        return maxProfit;
    }
}
