package classic.divide;

/**
 * 最大子数组问题：
 * 给定一个包含负数的整数数组 arr，返回一个子数组使得数组中的整数和最大
 */
public class MaxSubArray {

    static class Pair {
        int left;
        int right;
        int maxValue;

        public Pair(int left, int right, int maxValue) {
            this.left = left;
            this.right = right;
            this.maxValue = maxValue;
        }
    }

    /**
     * 暴力解遍历每个可能的子数组，返回最大的子数组
     *
     * @param arr
     * @return
     */
    public static Pair brutal(int[] arr) {
        if (arr == null) {
            return null;
        }
        int max = Integer.MIN_VALUE;
        int maxLeft = 0;
        int maxRight = 0;
        for (int left = 0; left < arr.length; left++) {
            int sum = arr[left];
            for (int right = left; right < arr.length; right++) {
                // 计算和
                sum += arr[right];
                if (sum > max) {
                    max = sum;
                    maxLeft = left;
                    maxRight = right;
                }
            }
        }
        return new Pair(maxLeft, maxRight, max);
    }

    public static Pair divideSubArray(int[] arr) {
        if (arr == null) {
            return null;
        }
        return divide(arr, 0, arr.length - 1);
    }

    /**
     * 分治将数组分为两个子数组并递归的求解子数组的解，然后合并子数组的解，需要考虑三种情况，由于需要递归调用因此需要子过程
     *
     * @param arr
     * @param low
     * @param high
     * @return
     */
    public static Pair divide(int[] arr, int low, int high) {
        if (low == high) {
            return new Pair(low, low, arr[low]);
        }
        int mid = low + (high - low) >> 2;
        // 最大子数组在左子数组
        Pair left = divide(arr, low, mid);
        // 最大子数组在右子数组
        Pair right = divide(arr, mid + 1, high);
        // 最大子数组包含中间点
        Pair cross = cross(arr, low, mid, high);

        if (left.maxValue >= cross.maxValue && left.maxValue >= right.maxValue) {
            return left;
        }
        if (cross.maxValue >= left.maxValue && cross.maxValue >= right.maxValue) {
            return cross;
        }
        return right;
    }

    /**
     * 处理最大子数组包含中间点
     *
     * @param arr
     * @param low
     * @param mid
     * @param high
     * @return
     */
    public static Pair cross(int[] arr, int low, int mid, int high) {
        int maxLeft = Integer.MIN_VALUE, maxRight = Integer.MIN_VALUE;
        int leftIndex = mid, rightIndex = mid;

        int index = mid, sum = 0;
        // 左边界
        while (index >= low) {
            sum += arr[index];
            if (sum > maxLeft) {
                maxLeft = sum;
                leftIndex = index;
            }
            index--;
        }

        // 右边界
        index = mid + 1;
        sum = 0;
        while (index <= high) {
            sum += arr[index];
            if (sum > maxRight) {
                maxRight = sum;
                rightIndex = index;
            }
            index++;
        }

        return new Pair(leftIndex, rightIndex, maxLeft + maxRight);
    }

    /**
     * 线性时间复杂度算法：如果已知 arr[1..j] 的最大子数组，则 arr[1..j+1] 的最大子数组要么是 arr[1..j] 的最大子数组，要么是子数组 arr[i..j+1] (1 <= i<= j+1)
     *
     * 如果 sum(arr[i..j]) + arr[j+1] > arr[j+1] 则说明包含 j+1 的最大子数组为 arr[i..j+1]，否则为 arr[j+1]
     *
     * @param arr
     * @return
     */
    public static Pair linear(int[] arr) {
        if (arr == null) {
            return null;
        }

        // arr[1..j] 的最大子数组
        Pair max = new Pair(0, 0, arr[0]);
        // arr[1..j] 上包含 j 的最大子数组
        Pair include = new Pair(0, 0, arr[0]);

        for (int i = 1; i < arr.length; i++){
            // 计算包含 j+1 的最大子数组
            int sum = arr[i] + include.maxValue;
            if (sum > arr[i]){
                include = new Pair(include.left, i, sum);
            }else{
                include = new Pair(i, i, arr[i]);
            }

            if (sum > max.maxValue){
                max = new Pair(include.left, include.right, sum);
            }
        }
        return max;
    }
}
