package leetcode.dp;

/**
 * 
 * 
 * 最大子数组问题：
 *  - 最大子数组之和： 给定整数数组 nums，找到具有最大和的连续子数组，返回最大值
 *  - 最大子数组乘积
 * 
 */
public class MaxSubArray {

    /**
     *  f(i) 为以 i 结尾的最大连续子数组，因此 f(i) = max(f(i-1)+nums[i], nums[i])
     *
     *  维护 [0, i] 数组中最大的连续子数组和 max
     *
     */
    public int maxSubArray(int[] nums){
        if (nums == null || nums.length == 0){
            return 0;
        }
        int pre = 0, max = nums[0];
        for (int i = 0; i < nums.length; i++){
            pre = Math.max(nums[i], pre + nums[i]);
            max = Math.max(pre, max);
        }
        return max;
    }


    /**
     * 数组中的元素可以为负数，因此需要同时维护最大值和最小值
     * 
     * f(i) 表示以 i 结尾的最大连续子数组的乘积，因此 f(i) = max(g(i-1)*nums[i], f(i-1)*nums[i], nums[i])
     * g(i) 表示以 i 结尾的乘积最小的连续子数组，因此 g(i) = min(g(i-1)*nums[i], f(i-1)*nums[i], nums[i])
     * 
     */
    public int maxProductArray(int[] nums){

        if(nums == null || nums.length == 0){
            return 0;
        }
        int preMax = nums[0], preMin = nums[0], max = nums[0];
        for(int i = 1; i < nums.length; i++){
            int tmpMax = Math.max(Math.max(preMax*nums[i], preMin*nums[i]), nums[i]);
            int tmpMin = Math.min(Math.min(preMax*nums[i], preMin*nums[i]), nums[i]);
            preMax = tmpMax;
            preMin = tmpMin;
            max = Math.max(preMax, max);
        }

        return max;
    }
}
