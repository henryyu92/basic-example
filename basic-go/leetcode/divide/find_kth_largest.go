package divide

// 在未排序的数组中找到第 k 个最大的元素，k 是排序后第 k 个最大的元素，而不是第 k 个不同的元素

// 排序之后取倒数 k 个元素
func findKthLargest(nums []int, k int) int {
	quickSort(nums, 0, len(nums)-1)
	return nums[len(nums)-k]
}

func quickSort(nums []int, left, right int) {

}

func partition(nums []int, left, right int) (int, int) {
	less, more := left-1, right
	for left < more {
		if nums[left] < nums[right] {
			less++
			nums[less], nums[left] = nums[left], nums[less]
			left++
		} else if nums[left] > nums[right] {
			more--
			nums[left], nums[more] = nums[more], nums[left]
		} else {
			left++
		}
	}
	nums[left], nums[right] = nums[right], nums[left]
	return (less +1, left)
}
