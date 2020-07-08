package ch4

func appendInt(x []int, y int) []int {
	var z []int
	zlen := len(x) + 1
	if zlen <= cap(x) {
		// There is root to grow
		z = x[:zlen]
	} else {
		// There is insufficient space. Allocate a new array
		zcap := zlen
		if zcap < 2*len(x) {
			zcap = 2 * len(x)
		}
		z = make([]int, zlen, zcap)
		// a built-in function
		copy(z, x)
	}
	z[len(x)] = y
	return z
}
