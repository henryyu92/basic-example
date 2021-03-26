package divide

// 给定一个大小为 n 的数组，找到其中的多数元素，多数元素指的是出现次数大于 [n/2] 的元素

// 使用 map 记录每个元素出现的次数，然后遍历 map 得到出现次数最多元素

func majorityElement(nums []int) int {

	m := make(map[int]int)

	for _, num := range nums {
		if n, ok := m[num]; ok {
			m[num] = n + 1
		} else {
			m[num] = 1
		}
	}

	var e int
	n := 0
	for k, v := range m {
		if v >= n {
			e = k
			n = v
		}
	}
	return e
}

// 分治法： 如果元素 a 是数组的众数(出现次数最多的数)，那么 a 必然是最少一部分子区间的众数

func majorityElement2(nums []int) int {
	return majorityElementRec(nums, 0, len(nums)-1)
}

func majorityElementRec(nums []int, left, right int) int {
	if left == right {
		return nums[left]
	}
	mid := left + (right-left)>>1
	leftMajority := majorityElementRec(nums, left, mid)
	rightMajority := majorityElementRec(nums, mid+1, right)

	if leftMajority == rightMajority {
		return leftMajority
	}

	leftCount := countInrange(nums, leftMajority, left, right)
	rightCount := countInrange(nums, rightMajority, left, right)
	if leftCount > rightCount {
		return leftMajority
	}
	return rightMajority
}

func countInrange(nums []int, num, left, right int) int {
	count := 0
	for i := left; i <= right; i++ {
		if nums[i] == num {
			count++
		}
	}
	return count
}
