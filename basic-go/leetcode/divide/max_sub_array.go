package divide

// 给定一个整数数组 nums，找到一个具有最大和的连续子数组，返回最大和

// 分治法：将数组分成两份，有三种情况：
//	1. 最大子数组在左侧
//	2. 最大子数组在右侧
//	3. 最大子数组包含切分点

// [l, r] 上维护 4 个变量：
// l_sum		以 l 为左边界的最大子数组和
// r_sum		以 r 为右边界的最大子数组和
// m_sum		[l, r] 内最大的子数组和
// i_sum		[l, r] 的和

// l_sum 可能是 [l, r] 左子区间的 l_sum 或者 左子区间的 i_sum 与 右子区间的 l_sum 之和，二者取较大值
// r_sum 可能是 [l, r] 右子区间的 r_sum 或者 左子区间的 r_sum 与 右子区间的 i_sum 之和，二者取较大值
// m_sum 可能是 [l, r] 左子区间的 m_sum 或者 右子区间的 m_sum 或者是 左子区间的 r_sum 与 右子区间的 l_sum 之和，三者取较大值
func maxSubArray(nums []int) int {
	return get(nums, 0, len(nums)-1).mSum
}

func get(nums []int, l, r int) Status {
	if l == r {
		return Status{nums[l], nums[l], nums[l], nums[l]}
	}
	m := l + (r-l)>>1
	lSub := get(nums, l, m)
	rSub := get(nums, m+1, r)
	return pushUp(lSub, rSub)
}

func pushUp(l, r Status) Status {
	iSum := l.iSum + r.iSum
	lSum := max(l.lSum, l.iSum+r.lSum)
	rSum := max(r.rSum, l.rSum+r.iSum)
	mSum := max(max(l.mSum, r.mSum), l.rSum+r.lSum)
	return Status{lSum, rSum, mSum, iSum}
}

type Status struct {
	lSum, rSum, mSum, iSum int
}

func max(x, y int) int {
	if x > y {
		return x
	}
	return y
}

// 动态规划法：假定最大连续子数组的最后元素位置为 i 则可得 f(i) = max(f(i-1) + nums[i], nums[i])
// 遍历数组，计算每个以当前下标结尾的子数组的和，并比较最大值
func maxSubArray2(nums []int) int {
	max := nums[0]
	for i := 1; i < len(nums); i++ {
		if nums[i-1]+nums[i] > nums[i] {
			nums[i] += nums[i-1]
		}
		if nums[i] > max {
			max = nums[i]
		}
	}
	return max
}
