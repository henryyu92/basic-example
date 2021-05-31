package example.classic.sort;

/**
 * 归并排序将待排序数据划分为两部分分别排序完成后合并
 */
public class MergeSort {

    public static void mergeSort(int[] arr){
        if (arr == null || arr.length < 2){
            return;
        }
        mergeSort(arr, 0, arr.length - 1);
    }

    public static void mergeSort(int[] arr, int left, int right){
        if (left == right){
            return;
        }
        int mid = left + (right - left) >> 1;
        mergeSort(arr, left, mid);
        mergeSort(arr, mid + 1, right);
        merge(arr, left, mid, right);
    }

    public static void merge(int[] arr, int left, int mid, int right){
        int[] help = new int[right - left + 1];
        int index = left;
        int p1 = mid, p2 = mid + 1;
        while (p1 <= mid && p2 <= right){
            help[index++] = arr[p1] < arr[p2] ? arr[p1++] : arr[p2++];
        }
        // 左边部分有剩余，直接放在后面
        while (p1 <= mid){
            help[index++] = arr[p1++];
        }
        // 右边部分有剩余
        while (p2 <= right){
            help[index++] = arr[p2++];
        }
        // 拷贝数据
        for (int i = left; i <= right; i++){
            arr[i] = help[i - left];
        }
    }
}
