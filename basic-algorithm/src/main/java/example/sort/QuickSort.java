package example.sort;

/**
 * 快速排序先根据一个值将待排序数据分割为两部分，左部分小于这个值，右边部分大于这个值，然后递归调用这个过程
 */
public class QuickSort {

    public static void quickSort(int[] arr){
        if (arr == null || arr.length < 2){
            return;
        }
        quickSort(arr, 0, arr.length - 1);
    }

    public static void quickSort(int[] arr, int left, int right){
        if (left < right){
            // 指定一个值，为了平衡取随机值
            swap(arr, left + (int) (Math.random() * (right - left + 1)), right);
            // 分区
            int[] p = partition(arr, left, right);
            // 递归调用过程
            quickSort(arr, left, p[0] - 1);
            quickSort(arr, p[1] + 1, right);
        }
    }

    /**
     * 最后一个值作为划分值，将数据划分为小于、等于、大于三部分，返回等于部分的左右边界
     * @param arr
     * @param left
     * @param right
     * @return
     */
    public static int[] partition(int[] arr, int left, int right){
        int less = left - 1;
        int more = right;
        // 遍历划分为三部分
        while (left < more){
            if (arr[left] < arr[right]){
                swap(arr, less++, left++);
            }else if (arr[left] > arr[right]){
                swap(arr, left, --more);
            }else{
                left++;
            }
        }
        swap(arr, more, right);
        return new int[]{less + 1, more};
    }

    public static void swap(int[] arr, int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}
