## 二叉堆

二叉堆是一种特殊的堆，可以看作近似的完全二叉树，树上的每个结点对应数组中的一个元素，除了最底层外，该树是完全充满的，而且是从左向右填充。

二叉堆同时具有堆的性质和完全二叉树的性质，假设堆的高度为 d，因此有：

- 父结点的值总是大于等于或者小于等于子结点的值
- 叶子结点不是在第 `d` 层，就是在第 `d-1` 层
- 若 `d-1` 层上有分支结点，则这些分支结点都集中在树的最左边
- 下标大于等于 `n/2` 的结点都是叶子结点

将二叉堆存储在数组中时，二叉堆的根结点为 A[0]，则给定一个结点对应的数组下标为 i 可以得出：

- 父结点对应数组下标为 (i-1)/2
- 左孩子对应数组下标为 2*i+1
- 右孩子对应数组下标为 2*i+2

### 维护堆性质

`Heapify` 过程用于维护堆的性质，其通过将指定结点和其子结点比较，如果不满足堆的性质则交换结点使得满足堆的性质，然后递归的调用 `heapify` 过程直到满足堆的性质。

`Heapify` 过程的时间复杂度为 `O(lgN)`

```java
// 最大堆向下调节
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

// 最大堆向上调节
public void maxHeapUp(int[] arr, int i, int heapSize){
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
```



### 建堆

使用自底向上的方法将数组 `A` 转换成堆，根据二叉堆的性质可得下标从 n/2 开始结点都是树的叶结点，因此从 `n/2-1` 下标的结点开始利用 `heapDown` 过程调整即可构建堆。

建堆的时间复杂度为 `O(N)`

```go
// 构建最大堆
public void buildMaxHeap(int[] arr){
    int n = arr.length;
    int start = n/2 -1;
    while (start >= 0){
      maxHeapDown(arr, start, n);
      start--;
    }
}
```

### 插入元素

插入元素通过将元素添加到数组末尾，然后通过 `heapUp` 过程调整堆实现。插入数据的时间复杂度为 `O(lgN)`。

```java
// 向最大堆中插入元素
public int[] insert(int[] arr, int v){
    int n = arr.length;
    int[] newArr = Arrays.copyOf(arr, n + 1);
    int newLen = newArr.length;
    newArr[newLen-1] = v;
    maxHeapUp(newArr, newLen-1);
    return newArr;
}
```

### 删除元素

删除元素通过将指定元素和数组末尾元素交换，然后调用 `heapDown` 过程调整堆实现。删除数据的时间复杂度为 `O(lgN)`。

```java
// 删除最大堆中元素
public void remove(int[] arr, int i){
    int len = arr.length;
    swap(arr, i, last-1);
    maxHeapDown(arr, i, len-1);
}
```



### 优先队列

优先队列有两种形式：最大优先队列和最小优先队列。优先队列是一种用来维护由一组元素构成的集合 S 的数据结构，其中的每一个元素都有一个称为关键字的值。优先队列支持的操作：

- ```insert(S, x)```：将元素 x 插入集合 S 中
- ```maximum(S)/minimum(S)```：返回最大优先队列的最大键元素/最小优先队列的最小键元素
- ```extractMax(S)/extractMin(S)```：移除并返回最大优先队列的最大键元素/最小优先队列的最小键元素
- ```increaseKey(S, x, k)/decreaseKey(S, x, k)```：将元素 x 的关键字值增加/减少到 k