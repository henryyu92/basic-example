## 堆排序算法
堆排序算法使用了大根堆这个数据结构实现一次遍历即可完成集合排序，排序过程中每次插入新元素后需要调整堆结构使其满足大根堆的性质。调整堆结构的时间复杂度为 O(lgN)，因此整个排序过程的时间复杂度为 O(N) * O(lgN) = O(NlgN)。

算法原理：
> 将集合构建成大根堆结构(根节点元素的值是整个集合中的最大值)；<br>
> 将大根堆根节点元素与集合最后一个元素交换，并调整剩余元素集合使其满足大根堆结构，同时从堆中去除这个元素(通过减少堆的大小实现)，此时集合中最大元素到达最后位置；<br>
> 重复上述过程 N 次直到大根堆大小为 0，此时所有元素都已经正确排序；<br>

- 时间复杂度为 O(NlgN)
- 空间复杂度为 O(1)
- 稳定性：不稳定

算法实现：
```java
public void heapSort(int[] arr){
    // 构建大根堆
    buildMaxHeap(arr);

    int heapSize = arr.length;
    // 将堆的根结点和最后一个结点交换
    swap(arr, 0, --heapSize);
    while(heapSize > 0){
        // 根结点变更需要调整堆使其满足性质
        maxHeapify(arr, 0, heapSize);
        swap(arr, 0, --heapSize);
    }
}

```
## 堆
堆是一个数组，可以被看成一个近似的完全二叉树，树上的每一个结点对应数组中的一个元素，除了最底层外，该树是完全充满的，而且是从左向右填充。

表示堆的数组 A 包括两个属性：
- A.length 表示数组元素的个数
- A.heapSize 表示数组中使用堆存储的元素个数

树的根结点为 A[0]，则给定一个结点对应的数组下标为 i 可以得出：
- 父结点对应数组下标为 (i-1)/2
- 左孩子对应数组下标为 2*i+1
- 右孩子对应数组下标为 2*i+2

二叉堆可以分为两种形式：
- 最大堆：除了根结点外，所有结点的值小于等于其父结点的值。因此堆中的最大元素存放在根结点中，因此对于任意一个子树包含的结点值都不大于该子树根结点的值
- 最小堆：除了根结点外，所有结点的值大于等于其父结点的值，即堆中的最小元素存放在根结点中

如果把堆看成完全二叉树，则堆中结点的高度就是该结点到叶结点最长简单路径上的长度，堆有一些有用的基本过程：
- max_heapify 过程：时间复杂度为 O(lgN)，用于维护最大堆的性质
- build_max_heap 过程：具有线性时间复杂度，用于从无序的输入数据数组中构造一个最大堆
- heapSort 过程：时间复杂度为 O(NlgN)，用于对一个数组进行原址排序
- max_heap_insert, heap_extract_max, heap_increase_key, heap_maximum 过程：时间复杂度为 O(lgN)，用于实现一个优先队列

### 维护堆性质
max_heapify 过程用于维护最大堆性质，输入为一个数组 A 和一个下标 i。调用 max_heapify 过程时，假定根结点为 left(i) 和 right(i) 的二叉树都是最大堆(即当前节点的左子树和右子树都是最大堆)，但此时 A[i] 有可能小于其孩子，通过让 A[i] 逐级下降使得以下标为 i 的根结点的子树重新遵循最大堆的性质。

算法原理：
> 首先从 A[i], A[left(i)], A[right(i)] 中选出最大的并将其下标存储在 largest 中。如果 A[i] 最大则程序结束，否则最大元素是 i 的某个孩子结点，交换 A[i] 和 A[largest] 的值使得下标为 i 的结点满足最大堆的性质，但是下标为 largets 的结点可能违反最大堆性质，因此需要对该子树递归调用 max_heapify 过程。

max_heapify 的时间复杂度为 O(lgN)

算法实现：
```go
func maxHeapify(arr int[], i int, heapSize int){
    left := 2 * i + 1
    for ;left < heapSize; {
        largest := left + 1 < size && arr[left + 1] > arr[left] ? left + 1 : left
        largest = arr[largest] > arr[i] ? largest : index;
        if largest == i{
            break
        }
        arr[i], arr[largest] = arr[largest], arr[i]
        i = largest
        left = 2 * i + 1
    }
    
}
```
### 建堆
可以使用自底向上的方法利用过程 max_heapify 把一个数组 A[0,..,n-1] 转换为最大堆。

> 通过堆的定义可以得出对于数组 A 而言，下标从 n/2 开始都是树的叶结点(满足最大堆的性质)，因此从 n/2 下标开始向前遍历并调整结点使其满足最大堆的性质，当遍历完成之后整个堆就都满足最大堆的性质

建堆的时间复杂度为 O(N)

```go
func buildMaxHeap(arr int[]){
    for i := len(arr)/2 - 1; i >= 0; i--{
        max_heapify(arr, i)
    }
}
```
### 插入元素
> 先将元素插入堆尾，然后判断该元素与其父节点的大小，如果父结点小于该元素则交换，交换之后再次递归比较交换

```java
public void maxHeadpInsert(int[] arr, int index){
    while(arr[index] > arr[(indx-1)/2]){
        swap(arr, index, (index-1)/2);
        index = (index-1)/2;
    }
}
```
### 最大堆和最小堆
### 优先队列(priority queue)
优先队列有两种形式：最大优先队列和最小优先队列。优先队列是一种用来维护由一组元素构成的集合 S 的数据结构，其中的每一个元素都有一个称为关键字的值。优先队列支持的操作：
- ```insert(S, x)```：将元素 x 插入集合 S 中
- ```maximum(S)/minimum(S)```：返回最大优先队列的最大键元素/最小优先队列的最小键元素
- ```extractMax(S)/extractMin(S)```：移除并返回最大优先队列的最大键元素/最小优先队列的最小键元素
- ```increaseKey(S, x, k)/decreaseKey(S, x, k)```：将元素 x 的关键字值增加/减少到 k

