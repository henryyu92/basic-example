package example.leetcode.pointer;

import java.util.List;

public class Sum {

    /**
     *  两数之和：
     *      给定数组 nums 和整数 sum，返回数组中满足 a+b=sum 的元素
     *
     *  使用 Map 映射数组的之和下标，其中 key 为数组的值，value 为数组的下标
     */
    public List<List<Integer>> twoSum(int[] nums, int sum){

        return null;
    }

    /**
     *  两数之和2：
     *      给定有序数组 nums 和整数 sum，返回数组中满足 a+b=sum 的元素
     *
     *
     *  双指针法：
     *      数组有序，则可以使用左右指针分别指向数组的第一个和最后一个位置，如果和小于 sum 则移动左指针，如果大于 sum 则移动右指针
     */
    public List<List<Integer>> twoSumOrderedArray(int[] nums, int sum){
        return null;
    }


    /**
     *  三数之和：
     *      给定有序数组 nums 和整数 sum，返回数组中满足 a+b+c=sum 的元素
     *
     *  将数组排序后借助双指针算法可以将时间复杂度降低到 O(Nlog + N^2) = O(N^2)
     *
     */
    public List<List<Integer>> threeSum(int[] nums, int sum){

        return null;
    }

    /**
     *  三数之和2：
     *      给定有序数组 nums 和整数 sum，返回数组中满足 a+b+c 与 sum 最接近的元素
     *
     *      最接近即绝对值之差最小，维护一个表示绝对值与 sum 的差的变量，利用两数之和将时间复杂度降低至 O(N^2)
     */
    public List<List<Integer>> threeSumClose(int[] nums, int sum){

        return null;
    }

    /**
     *  四数之和：
     *      给定有序数组 nums 和整数 sum，返回数组中满足 a+b+c+d=sum 的元素
     */
    public List<List<Integer>> fourSum(int[] nums, int sum){
        return null;
    }
}
