### 冒泡排序
算法原理：
> 从第一个位置开始依次比较相邻两个元素 A 和 B，如果 A 大于 B 则交换，通过一次遍历可将集合中的最大元素交换至集合的最后位置<br>
> 重复上述过程 N 次，依次将剩余集合中的最大值交换到集合的最后位置，完成集合的排序；

- 时间复杂度：O(N^2)
- 空间复杂度：O(1)
- 稳定性：稳定，相同值的元素可以保证排序后和原始相对顺序一致

算法实现：
```go
func bubbleSort(arr []int){
    if arr == nil || len(arr) == 1 {
		return
    }
    // 重复过程 N-1 次(最后一个元素不需要比较交换)，完成集合的排序
    for count, total := 0, len(arr) - 1; count <= total; count++ {
        bubble(arr, 0, total-count)
    }
}

// 从 start 位置开始两两比较交换后将集合最大元素移动到 end 位置(类似气泡冒起的过程)
func bubble(arr []int, start, end int) {
	for i := start; i < end; i++ {
		if arr[i] > arr[i+1] {
			arr[i], arr[i+1] = arr[i+1], arr[i]
		}
	}
}
```
冒泡排序的缺陷：
- 遍历集合并交换元素的过程中有大量不必要的交换(非集合最大元素的交换)
- 相同元素会在不同的遍历过程中比较多次
### 选择排序
选择排序相对于冒泡排序在交换上进行了优化，选择排序在遍历集合找到集合最大元素过程中不交换元素，只记录最大元素的位置，到集合遍历完之后才会交换。

算法原理：
> 从第一个位置开始比较元素与集合最大元素的大小，记录最大元素的位置，通过一次遍历可确定集合中最大元素的位置；然后将最大元素与集合最后位置的元素交换；<br>
> 重复遍历交换过程 N 次，依次可将剩余集合中的最大元素交换到集合最后位置，完成集合排序；<br>

- 时间复杂度：O(N^2)
- 空间复杂度：O(1)
- 稳定性：稳定，相同的元素可以保证排序后和原始相对位置一致

算法实现：
```go
func selectionSort(arr []int) {
    if arr == nil || len(arr) == 1 {
		return
	}
    // 重复过程 N-1 次，完成集合的排序
	for end := len(arr)-1; end > 0; end-- {
		selection(arr, 0, end)
	}
}

// 从 start 位置开始，遍历集合记录最大元素位置并交换到 end 位置
func selection(arr []int, start, end int) {
	maxIndex := end
	for i := start; i <= end; i++ {
		if arr[i] > arr[maxIndex] {
			maxIndex = i
		}
	}
	arr[maxIndex], arr[end] = arr[end], arr[maxIndex]
}
```
单向选择排序的 selection 过程每次只是找到集合中的最大值交换到末尾，这样需要循环 N 次才能将所有元素排好序。可以在一次循环中同时找出集合中最大元素和最小元素分别交换到末尾和最前位置，这种优化称为双向选择排序：
```go

func doubleSelectionSort(arr []int) {
    if arr == nil || len(arr) == 1 {
		return
    }
    // 每次调用 selection 过程能够同时确定最大元素和最小元素，只需要循环 N/2 次即可完成排序
    for start, end := 0, len(arr)-1; start <= end; start, end = start+1, end-1 {
		dubbleSelection(arr, start, end)
	}
}

func dubbleSelection(arr []int, start, end int) {
	minIndex, maxIndex := start, end
	for i := start; i <= end; i++ {
		if arr[i] > arr[maxIndex] {
			maxIndex = i
		}
		if arr[i] < arr[minIndex] {
			minIndex = i
		}
	}
	arr[minIndex], arr[start] = arr[start], arr[minIndex]
	arr[maxIndex], arr[end] = arr[end], arr[maxIndex]
}
```
选择排序缺陷：
- 相同元素的比较会在不同的遍历过程中出现多次
### 插入排序
插入排序在选择排序算法上进行了进一步的优化，插入排序将待排序集合划分为两部分：左部分为已经排序好的，右部分为待排序部分；通过遍历右部分的元素并与左部分元素比较找到合适的位置并插入其中完成元素集合的排序。在整个遍历比较插入过程中，任意两个元素只会比较一次。

算法原理：
> 从元素集合的第二个位置开始，左部分为已排序好的，依次遍历右部分的元素，每个元素在左部分找到合适的位置插入，遍历完集合即完成集合元素排序；

- 时间复杂度：O(N^2)
- 空间复杂度：O(1)
- 稳定性：稳定，相同的元素可以保证排序后和原始相对位置一致

算法实现：
```go
func insertionSort(arr []int) {
	// 遍历集合依次将当前元素插入到合适的位置，遍历完成即完成集合排序
	for i, end := 1, len(arr)-1; i <= end; i++ {
		insertion(arr, 0, i)
	}
}

// 将 end 位置的元素插入到合适的位置
func insertion(arr []int, start, end int) {
	for i := end - 1; i >= start && arr[i] > arr[i+1]; i-- {
		arr[i], arr[i+1] = arr[i+1], arr[i]
	}
}
```
插入算法缺陷：
- 每个元素在插入时会导致大于该元素的所有元素发生移动，在极端情况下(逆序)算法常数项会比较大

**[Back](../)**