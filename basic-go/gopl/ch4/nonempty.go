package ch4

// nonempty returns a slice holding only the non-empty strings.
func nonempty(strings []string) []string {
	i := 0
	for _, s := range strings {
		if s != "" {
			strings[i] = s
			i++
		}
	}
	return strings[:i]
}

func remove(slice []int, i int)[]int{
	copy(slice[i:], slice[i+1:])
	return slice[:len(slice) - 1]
}