package sort

const (
	// RED strip
	RED int = iota
	// WHITE strip
	WHITE
	// BLUE strip
	BLUE
)

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

func partition(arr []int) {
	start, end := -1, len(arr)
	for i := 0; i < end; {
		if arr[i] == RED {
			start++
			arr[start], arr[i] = arr[i], arr[start]
			i++
		}
		if arr[i] == WHITE {
			i++
		}
		if arr[i] == BLUE {
			end--
			arr[end], arr[i] = arr[i], arr[end]
		}
	}
}
