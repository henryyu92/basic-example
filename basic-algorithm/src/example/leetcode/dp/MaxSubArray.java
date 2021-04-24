package example.leetcode.dp;

/**
 * 给定整数数组 nums，找到具有最大和的连续子数组，返回最大值
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
            pre = Math.max(pre, pre + nums[i]);
            max = Math.max(pre, max);
        }
        return max;
    }
}
