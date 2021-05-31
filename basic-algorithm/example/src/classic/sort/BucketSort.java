package example.classic.sort;

/**
 * 桶排序已知数据分布，将数据放入对应的桶中，然后遍历桶即可排序
 */
public class BucketSort {

    public static void bucketSort(int[] arr){
        if (arr == null || arr.length < 2){
            return;
        }
        // 找出数据中的最大值用于确定桶的个数
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < arr.length; i++){
            max = Math.max(arr[i], max);
        }
        // 创建桶
        int[] bucket = new int[max];
        // 遍历数据放入桶中
        for (int i = 0; i < arr.length; i++){
            bucket[arr[i]]++;
        }
        // 遍历桶将数据排序
        int index = 0;
        for (int i = 0; i < bucket.length; i++){
            while (bucket[i]-- > 0){
                arr[index++] = bucket[i];
            }
        }

    }
}
