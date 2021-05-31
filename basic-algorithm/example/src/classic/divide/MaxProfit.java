package classic.divide;

/**
 * 最大利润
 */
public class MaxProfit {

    private final int TRADING_DAY = 20;
    private final int MAX_PRICE = 100;
    private int buyDay;
    private int sellDay;

    private int[] startPrice = new int[TRADING_DAY];

    public MaxProfit(){
        for (int i = 0; i < TRADING_DAY; i++){
            startPrice[i] = (int)(Math.random() * MAX_PRICE + 1);
        }
    }

    /**
     * 暴力枚举法：比较任意两个的差值，返回最大的差值，时间复杂度 O(N^2)
     *
     */
    public int maxProfit1(int[] price){
        if (price == null){
            return 0;
        }
        int maxProfit = 0;
        for (int i = 0; i < price.length - 1; i++){
            for (int j = i + 1; j < price.length; j++){
                maxProfit = Math.max(price[j], maxProfit);
            }
        }

        return maxProfit;
    }

    /**
     * 分治法：将数组一分为二，分别求最大的差值，最后比较二者的差值，时间复杂度 O(NlgN)
     *
     */
    public int maxProfit2(int[] price){
        if (price == null){
            return 0;
        }
        return process(price, 0, price.length - 1);
    }

    public int process(int[] price, int start, int end){
        if (start == end){
            return price[start];
        }
        int mid = start + (end - start) >> 2;
        // 左侧最大
        int left = process(price, start, mid);
        // 右侧最大
        int right = process(price, mid + 1, end);
        // 中间
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int j = mid; j >= start; j++){
            min = Math.min(price[j], min);
        }
        for (int j = mid; j <= end; j++){
            max = Math.max(price[j], max);
        }
        int m = max - min;

        if (left >= right && left >= m){
            return left;
        }
        if (m > left && m >= right){
            return m;
        }
        return right;
    }

    /**
     * N 位置的值与前面 N-1 个位置的最小值的差值是当前位置能得到的最大值，遍历一遍数组比较每个位置能够得到的最大值，时间复杂度 O(N)
     *
     */
    public int maxProfit3(int[] price){
        if (price == null){
            return 0;
        }
        int maxProfit = 0;
        // 前面 N-1 个位置的最小值
        int minPrice = price[0];
        // 前面 N-1 个位置最小值所在的位置
        int minDay = 0;
        for (int i = 0; i < price.length; i++){
            if (price[i] < price[minDay]){
                minDay = i;
                minPrice = price[minDay];
            }
            if (price[i] - minPrice > maxProfit){
                maxProfit = price[i] - minPrice;
            }
        }
        return maxProfit;
    }
}
