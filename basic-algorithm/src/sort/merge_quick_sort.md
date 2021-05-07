## 归并排序
归并排序利用了递归的思想：将待排序元素集合划分为两个较小的集合，然后分别排序两个较小的集合，最后将已排序好的两个小集合合并为最终排序集合；通过递归的调用这个过程则可以在时间复杂度 O(NlgN) 下完成集合的排序。

算法原理：
> 将集合划分为两个大小相近的集合；<br>
> 对两个集合递归调用排序过程；<br>
> 合并两个已经排序好的子集合，完成集合的排序；<br>

- 时间复杂度：O(NlgN)
- 空间复杂度：O(N)，需要使用额外的数组用于合并
- 稳定性：稳定，相同的元素可以保证排序后和原始相对位置一致

算法实现：
```go
func sort(arr []int) {
    if arr == nil || len(arr) < 2 {
        return
    }
    mergeSort(arr, 0, len(arr) - 1)
}

// 递归过程
func mergeSort(arr []int, left, right int){
    if left == right{
        return
    }
    // 将元素集合分为两个较小的集合
    int mid = left + (right - left) >> 1
    // 递归排序两个小集合
    mergeSort(arr, left, mid)
    mergeSort(arr, mid + 1, right)
    // 合并集合
    merge(arr, left, mid, right)
}

// 合并两个集合
func merge(arr []int, left, mid, right int){
    help := int[right - left + 1]
    i, p1, p2 := 0, left, mid + 1
    for ;p1 <= mid && p2 <= right; i++ {
        // 集合元素较小的下标增加
        if arr[p1] <= arr[p2]{
            help[i] = arr[p1]
            p1++
        }else{
            help[i] = arr[p2]
            p2++
        }
        
    }
    // 右部分先越界
    for ;p1 <= mid; {
        help[i++] = arr[p1++]
    }
    // 左部分先越界
    for ;p2 <= right; {
        help[i++] = arr[p2++]
    }
    // 将合并后的数组拷贝回原数组
    for i:= 0; i < len(help); i++{
        arr[left + i] = help[i]
    }
}
```
## 快速排序
算法原理：
> 以元素集合最后一个元素作为划分值将集合划分为两部分：小于划分值的元素位于集合的左边，大于等于划分值的元素位于集合的右边；然后递归排序左右两个集合

- 平均时间复杂度：O(NlgN)
- 空间复杂度：O(lgN)，因为递归过程需要记录划分值的位置
- 稳定性：不稳定，因为存在小于区和待定区的交换，相同的元素不能保证排序后和原始相对位置一致

算法实现：
```go
func sort(arr []int){
    if arr == nil || len(arr) < 2{
        return
    }
    quickSort(arr, 0, len(arr) - 1)
}

func quickSort(arr []int, left int, right int){
    if left < right{
        // p 表示划分位置需要额外空间
        p := partition(arr, left, right)
        quickSort(arr, left, p)
        quickSort(arr, p + 1, right)
    }
}

/**
* 划分原理：
*  将集合根据划分值视为小于区和大于等于区。初始小于区为空，大于等于区为整个集合。
*  遍历集合如果元素大于等于划分值则继续遍历，否则将小于区的下一个元素与该元素互换并扩大小于区，
*  遍历完成后，整个元素集合就以划分值划分为小于区和大于等于区
*
* 时间复杂度：O(N)
* 空间复杂度：O(1)
*/
func partition(arr []int, left int, right int) int{
    // 初始小于区为 nil
    less := left - 1
    // 以集合最后一个元素作为划分值
    p := arr[right]
    for i := left; i <= right; i++{
        // 将小于划分值得元素交换到小于区
        if arr[i] < p {
            less += 1
            arr[i], arr[less] = arr[less], arr[i] 
        }
    }
    // 返回小于区和大于等于区的划分位置
    return less
}
```
算法优化：
> 分区划分只是划分为小于区和大于等于区导致相等的元素会进入下一步的递归中，可以在分区划分的时候将元素集合划分为小于区、等于区和大于区减少递归的数据量<br>
> 选用集合最后一个元素作为划分值在集合中元素顺序较差时算法时间复杂度退化为 O(N^2)，因此工程上通常使用随机位置作为划分值使得算法平均时间复杂度为 O(NlgN)

```go
func quickSort(arr []int, left int, right int){
    if left < right{
        // 选取集合随机元素和最后元素交换作为划分值
        i := left + int(Math.random() * (right - left + 1))
        arr[i], arr[right] = arr[right], arr[i]

        // 分区方法返回两个值 p[0] 表示小于区的最后位置，p[1] 表示大于区开始位置
        p := partition(arr, left, right)
        quickSort(arr, left, p[0])
        quickSort(arr, p[1], right)
    }
}

func partition(arr []int, left int, right int) (int, int){
    // 小于区边界
    less := left - 1
    // 大于区边界
    more := right + 1
    // 划分值
    p := arr[right]
    for i := left; i < more;{
        // 小于区
        if arr[i] < p{
            less += 1
            arr[less], arr[i] = arr[i], arr[less]
            i++
        // 大于区
        } else if arr[i] > p{
            more -= 1
            arr[more], arr[i] = arr[i], arr[more]
        // 等于区
        } else{
            i++
        }
    }
    return (less, more)
}
```
### 荷兰国旗问题
问题描述：
> 荷兰国旗是由红白蓝3种颜色的条纹拼接而成，假设这样的条纹有多条，且各种颜色的数量不一，并且随机组成了一个新的图形。设计算法把这些条纹按照颜色排好，红色的在上半部分，白色的在中间部分，蓝色的在下半部分，时间复杂度为 O(N)，空间复杂度为 O(1)。

思路1：
> 考虑到整个集合的值空间有限，因此可以使用计数排序的方法：先遍历一遍集合得到所有值及其出现的次数，然后再遍历一遍集合依次填充收集的值及其次数即可。

算法实现：
```go
func countingSortPartition(arr []int) {
	color := make([]int, 3)
	// 一次遍历收集集合所有值及其出现的次数
	for i, j := 0, len(arr); i < j; i++ {
		if arr[i] == RED {
			color[RED]++
		}
		if arr[i] == WHITE {
			color[WHITE]++
		}
		if arr[i] == BLUE {
			color[BLUE]++
		}
	}
	// 再次遍历填充集合
	i := 0
	// padding RED
	for k := 0; k < color[RED]; k, i = k+1, i+1 {
		arr[i] = RED
	}
	// padding WHITE
	for k := 0; k < color[WHITE]; k, i = k+1, i+1 {
		arr[i] = WHITE
	}
	//padding BLUE
	for k := 0; k < color[BLUE]; k, i = k+1, i+1 {
		arr[i] = BLUE
	}
}
```
思路2：
> 更一般的，荷兰国旗问题实质是一个分区问题。考虑需要将集合分为 3 个分区，可以使用两个指针分别表示 RED 区域的终止位置和 BLUE 区域的起始位置，通过一次遍历集合可以将对应颜色交换到对应区域。
```go
func partition(arr []int){
    start, end := -1, len(arr)
    // 通过一次遍历及指针的移动能够将集合划分为三个分区
    for i := 0; i < end;{
        if arr[i] == RED{
            start++
            arr[start], arr[i] = arr[i], arr[start]
            i++
        }
        if arr[i] == WHITE{
            i++
        }
        if arr[i] == BLUE{
            end--
            arr[end], arr[i] = arr[i], arr[end]
        }
    }
}
```

**[Back](../)**