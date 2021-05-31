package leetcode.pointer;


public class RemoveDuplicates {

    /**
     *  给定有序数组，删除重复出现的元素，使每个元素只出现一次，返回删除后数组的新长度，需要原地修改数组而不引入额外的空间
     *
     *  双指针：类似分区问题，右指针移动到和左指针不同的元素覆盖，然后左指针移动
     */
    public int removeDuplicates(int[] nums){
        int n = nums.length;
        if (n < 1){
            return n;
        }
        // left 表示需要被替换的位置，left 左侧是满足条件的元素
        int left = 1, right = 1;
        while (right < n){
            if (nums[right - 1] != nums[right]){
                nums[left++] = nums[right];
            }
            right++;
        }
        return left;
    }

    /**
     *
     *  给定有序数组 nums, 删除重复元素使得每个元素最多出现两次，返回删除后新数组的长度
     *
     *  双指针法：
     *      右指针移动，如果不相等但是间距大于 2 则左指针移动 2 并且被右指针覆盖
     *
     */
    public int removeDuplicates_2(int[] nums){
        int n = nums.length;
        if (n < 2){
            return n;
        }
        int left = 2, right = 2;
        while (right < n){
            // left 表示需要覆盖的位置，保证 left 左侧最多只能有 2 个相同的
            if (nums[left-2] != nums[right]){
                nums[left++] = nums[right];
            }
            right++;
        }
        return left;
    }

    /**
     *  给定数组 nums 和值 val 移除所有数值等于 val 的元素，返回移除后数组的新长度，不需要引入额外的空间
     */
    public int removeElement(int[] nums, int val){
        int n = nums.length;
        int left = 0, right = 0;
        while (right < n){
            if (nums[right] != val){
                nums[left++] = nums[right];
            }
            right++;
        }
        return left;
    }

    /**
     *  不需要保证数组的顺序，则可以将尾部的数据覆盖
     */
    public int removeElements_1(int[] nums, int val){
        int n = nums.length;
        int left = 0, right = n - 1;
        while (left <= right){
            if (nums[left] == val){
                nums[left] = nums[right--];
            }else{
                left++;
            }
        }
        return left;
    }

    public static void main(String[] args) {

        RemoveDuplicates r = new RemoveDuplicates();

        int[] nums = new int[]{0,0,1,1,1,2,2,3,3,4};
//        System.out.println(r.removeDuplicates(nums));

        System.out.println(r.removeElements_1(nums, 1));
    }
}
