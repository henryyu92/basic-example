## 分治
分治法的思想是将原问题分解为几个规模较小但类似于原问题的子问题，递归地求解这些子问题，然后再合并这些子问题的解来建立原问题的解。

分治法通常使用递归来实现，在每层递归时都有三个步骤：
- 分解原问题为若干个子问题，这些子问题的形式与原问题一样，只是规模更小
- 递归地求解子问题，如果子问题规模足够小则直接求解(边界)
- 将子问题的解组合成原问题的解

### 递归式

递归式与分治法是紧密相关的，使用递归式可以自然的刻画分治法的运行时间。递归式通常是一个等式或者不等式，形式上表示为 ```T(n) = aT(n/b) + f(n)```，通过主定理可以求解递归式的渐进边界：

### 最大子数组

问题描述：对于给定的数组 arr，找到其中的子数组使得子数组中的数字之和最大。

**暴力求解法**

遍历数组所有可能的子数组，找到和最大的子数组，时间复杂度 O(N^2)
```
func maxSubArray(arr)
  max = -INF
  for i = 1 to n-1
    sum = 0
    for j = i+1 to n
      sum += arr
      if sum > max
        max = sum
  return max
```

**分治法**

考虑将数组 arr[low, high] 分为两个子数组 arr[low, mid] 和 arr[mid+1, high]，所以最大子数  arr[i, j] 的位置有三种情况：
- 完全位于 arr[low, mid] 上，low <= i <= j <= mid
- 完全位于 arr[mid+1, high] 上，mid < i <= j <= high
- 跨越了中点，low <= i <= mid <= j <= high，此时从中间向两边遍历分别获取最大值的边界

求解子问题的解后需要将子问题的解合并，合并时需要考虑这三种情况：
```go
```

**线性时间算法**

从数组左边界开始从左到右处理，记录目前为止已经处理过的最大子数组。若已知 arr[1..j] 的最大子数组，则 arr[1..j+1] 的最大子数组要么是 arr[1..j] 的最大子数组，要么是子数组 arr[i..j+1]。
```java
public static Pair(int[] arr){
    if(arr == null){
        return null;
    }

    // 最大子数组
    Pair max = new Pair(0, 0, arr[0]);
    // 包含 j 的最大子数组
    Pair include = new Pair(0, 0, arr[0]);

    for(int i = 0; i < arr.length; i++){
        int sum = arr[i] + include.value;
        if(sum > arr[i]){
            include = new Pair(include.left, i, sum);
        }else{
            include = new Pair(i, i, arr[i]);
        }

        if(sum > max.value){
            max = new Pair(include.left, include.right, sum);
        }
    }
    return max;
}
```

