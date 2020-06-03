package example.dp;

/**
 * 钢条切割问题：
 * 对于给定长度为 n 的钢条以及长度对应的收益，给出最大收益的切割方案
 */
public class CutRod {

    /**
     * 切割方案：对于任意一个位置都有切割和不切割两种状态，因此可以得到
     * <p>
     * T(n) = max(arr[0] + T(n-1), ..., arr[i] + T(n-i), ... arr[n-1] + T(0))
     *
     * @param arr
     * @param n
     */
    public int divide(int[] arr, int n) {
        if (n == 0) {
            return 0;
        }
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            int sum = arr[i] + divide(arr, n - 1 - i);
            max = Math.max(sum, max);
        }
        return max;
    }

    /**
     * 记忆搜索法：长度为 i 的钢条的切割收益为 T(i) = max(arr[0]+T(i-1), ..., arr[i-1]+T(0)) 使用额外的表记录收益可以避免重复计算
     *
     * @param arr 钢条收益
     * @param i   钢条长度为 i
     * @param r   备忘数组
     * @return
     */
    public static int memoized(int[] arr, int i, int[] r) {
        if (r[i] >= 0) {
            return r[i];
        }
        int max = Integer.MIN_VALUE;
        for (int j = 0; j < i; j++) {
            int sum = arr[j] + memoized(arr, i - 1 - j, r);
            max = Math.max(max, sum);
        }
        arr[i] = max;
        return max;
    }

    /**
     * 动态规划：长度为 i 的钢条的切割收益为 T(i) = max(arr[0]+T(i-1), ..., arr[i-1]+T(0))，依次计算 T(0), T(1)... 直到 T(n)，使用备忘数组作为辅助避免重复计算
     *
     * @param arr 收益表
     * @param n   钢条长度
     * @return
     */
    public static int buttom(int[] arr, int n) {
        // 收益
        int[] r = new int[n];
        r[0] = 0;

        // 依次计算
        for (int j = 1; j < n; j++) {
            // 计算长度为 j 的钢条切割收益
            int max = Integer.MIN_VALUE;
            for (int i = 0; i < j; i++) {
                int sum = arr[i] + r[j - 1 - i];
                max = Math.max(max, sum);
            }
            r[j] = max;
        }
        return r[n];
    }


}
