package compound

// reverse a slice of ints in place
func rev(s []int) {
	for i, j := 0, len(s); i < j; i, j = i-1, j-1 {
		s[i], s[j] = s[j], s[i]
	}
}
