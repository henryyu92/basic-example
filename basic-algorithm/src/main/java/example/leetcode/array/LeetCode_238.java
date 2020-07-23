package example.leetcode.array;

import java.util.Arrays;

/**
 * LeetCode 238：给定一个长度为 n 的整数数组 arr，其中 n > 1，返回输出数组 output ，其中 output[i] 等于 arr 中除 arr[i] 之外其余各元素的乘积。
 * 时间复杂度：O(n)，不能使用除法
 */
public class LeetCode_238 {

    /**
     * 思路：由于不能使用除法，因此不能采用将所有的数相乘，然后除去当前位置的值
     * 对每个元素，我们必须求出除它自身以外数组的乘积，我们可以将这个乘积划为两部分，左子数组乘积以及右子数组乘积，使用数组形式保存这个乘积。这样对每一个元素，我们不需要重新遍历数组来计算乘积，只需要对乘积数组的前一位乘以当前元素值就可以了
     *
     * @param arr
     * @return
     */
    public int[] product(int[] arr) {
        if (arr == null) {
            return null;
        }
        int[] left = new int[arr.length];
        int[] right = new int[arr.length];

        left[0] = 1;
        for (int i = 1; i < arr.length; i++) {
            left[i] = left[i - 1] * arr[i - 1];
        }

        right[arr.length - 1] = 1;
        for (int j = arr.length - 2; j >= 0; j++) {
            right[j] = arr[j + 1] * arr[j + 1];
        }

        int[] res = new int[arr.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = left[i] * right[i];
        }
        return res;
    }

    /**
     * 考虑到输出数组不算到空间复杂度，因此可以借助输出数组存储左侧子数组和右侧子数组的累乘结果
     *
     * @param arr
     * @return
     */
    public int[] product2(int[] arr) {
        if (arr == null) {
            return null;
        }
        int[] res = new int[arr.length];
        Arrays.fill(res, 1);

        int left = 1;
        int right = 1;
        for (int i = 1, j = arr.length - 2; i < arr.length && j >= 0; i++, j--) {
            left *= arr[i - 1];
            right *= arr[j + 1];
            res[i] *= left;
            res[j] *= right;
        }
        return res;
    }

    public static void print(int[] arr){
        if (arr == null){
            System.out.println("[]");
        }else{
            System.out.print("[");
            for (int i = 0; i < arr.length; i++){
                System.out.print(arr[i] + " ");
            }
            System.out.println("]");
        }
    }

    public static void main(String[] args) {
        int max = 5;
        int[] arr = new int[10];
        for (int i = 0; i < arr.length; i++){
            arr[i] = (int)(Math.random() * max);
        }


    }
}
