package sort

type Type int

const (
	Bubble Type = iota
	Selection
	Insertion
	Merge
)

func sort(arr []int, t Type) {
	if arr == nil || len(arr) == 1 {
		return
	}
	switch t {
	case Bubble:
		bubbleSort(arr)
	case Selection:
		selectionSort(arr)
	case Insertion:
		insertionSort(arr)
	case Merge:
		mergeSort(arr, 0, len(arr)-1)
	}
}

func bubbleSort(arr []int) {
	// go 循环中的 condition 会在每次循环时都执行一次，因此保持 condition 尽可能简单是一个比较好的习惯
	for count, total := 0, len(arr)-1; count <= total; count++ {
		bubble(arr, 0, total-count)
	}
}

func bubble(arr []int, start, end int) {
	for i := start; i < end; i++ {
		if arr[i] > arr[i+1] {
			arr[i], arr[i+1] = arr[i+1], arr[i]
		}
	}
}

func selectionSort(arr []int) {
	for start, end := 0, len(arr)-1; start <= end; start, end = start+1, end-1 {
		selection(arr, start, end)
	}
}

func selection(arr []int, start, end int) {
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

// 归并的结果是 left ~ right 范围内的元素已经排好序
func mergeSort(arr []int, left, right int) {
	// base case
	if left == right {
		return
	}
	mid := left + (right-left)>>2
	mergeSort(arr, left, mid)
	mergeSort(arr, mid+1, right)
	merge(arr, left, mid, right)
}

// 将 left ~ mid 和 mid + 1 ~ right 范围内已排好序的元素在 left ~ right 范围内排序
func merge(arr []int, left, mid, right int) {
	help := make([]int, right-left+1)
	i, j := 0, mid+1
	for ; left <= mid && j <= right; i++ {
		if arr[left] <= arr[j] {
			help[i] = arr[left]
			left++
		} else {
			help[i] = arr[j]
			j++
		}
	}
	// 左侧遍历完
	for ; j <= right; i++ {
		arr[i] = arr[j]
		j++
	}
	// 右侧遍历完
	for ; left <= mid; i++ {
		arr[i] = arr[left]
		left++
	}
}
