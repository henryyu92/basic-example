package classic.ds.heap;

import java.util.Arrays;

/**
 * 二叉堆
 */
public class BinaryHeap {

  /**
   * 最大堆 向下调节 过程
   */
  public void maxHeapDown(int[] arr, int i, int heapSize){

    int left = 2*i+1;
    while (left < heapSize){
      int leftValue = arr[left];
      int rightValue = 0;
      if (left < heapSize - 1){
        rightValue = arr[left+1];
      }
      int maxIndex = leftValue >= rightValue ? left : left + 1;
      if (arr[i] >= arr[maxIndex]){
        break;
      }
      swap(arr, i, maxIndex);
      i = maxIndex;
      left = maxIndex * 2 + 1;
    }
  }

  public void maxHeapUp(int[] arr, int i){
    int parent = (i-1)>>1;
    while (parent >= 0){
      if (arr[i] <= arr[parent]){
        break;
      }
      swap(arr, i, parent);
      i = parent;
      parent = (parent -1) >> 1;
    }
  }

  /**
   * 建堆从 n/2 开始向前遍历，并通过 heapDown 过程调整
   *
   *  n/2 到数组尾部的结点为叶子结点
   */
  public void buildHeap(int[] arr){
    int n = arr.length;
    int start = n/2 -1;
    while (start >= 0){
      maxHeapDown(arr, start, n);
      start--;
    }

  }

  /**
   * 插入操作通常将元素添加到末尾，然后调用 heapUp 过程完成调整
   */
  public int[] insert(int[] arr, int v){
    int n = arr.length;
    int[] newArr = Arrays.copyOf(arr, n + 1);
    int newLen = newArr.length;
    newArr[newLen-1] = v;
    maxHeapUp(newArr, newLen-1);
    return newArr;
  }

  /**
   * 删除元素通常将元素和数组最后元素交换，然后在通过 heapDown 过程完成调整
   */
  public void remove(int[] arr, int i){
    int last = arr.length - 1;
    swap(arr, i, last);
    maxHeapDown(arr, i, arr.length-1);
  }


  private void swap(int[] arr, int i, int j){
    int tmp = arr[i];
    arr[i] = arr[j];
    arr[j] = tmp;
  }


  public static void main(String[] args) {

    int[] arr = new int[]{2, 6, 5, 10, 8, 7, 6, 11};

    BinaryHeap binaryHeap = new BinaryHeap();

    binaryHeap.buildHeap(arr);
    System.out.println(Arrays.toString(arr));

    arr = binaryHeap.insert(arr, 30);
    System.out.println(Arrays.toString(arr));

    binaryHeap.remove(arr, 0);
    System.out.println(Arrays.toString(arr));

  }

}
