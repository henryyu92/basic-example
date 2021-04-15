package string

func BruteForceMatch(str1, str2 string) int {
	l1, l2 := len(str1), len(str2)
	if l1 < l2 {
		return -1
	}
	for i := 0; i <= l1-l2; i++ {
		for j := 0; j < l2; j++ {
			if str1[i+j] == str2[j] {
				if j == l2-1 {
					return i
				}
				continue
			}
			break
		}
	}
	return -1
}

func NextArray(str string) []int {
	l := len(str)
	next := make([]int, 0)

	next = append(next, 0)
	i := 1
	// next[i-1]
	cn := 0
	for i < l {
		if str[cn] == str[i] {
			cn++
			next = append(next, cn)
			i++
		} else if cn != 0 {
			cn = next[cn-1]
		} else {
			next = append(next, 0)
			i++
		}
	}

	return next
}
