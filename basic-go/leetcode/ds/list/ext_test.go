package list

import (
	"strconv"
	"testing"
)

// go test -v -run="IsPa"
func TestIsPalindrome(t *testing.T) {

	var tests = []struct {
		strList string
		want    bool
	}{
		{"123454321", true},
		{"123234345", false},
		{"12344321", true},
	}

	for _, test := range tests {
		l := NewList()
		for _, s := range test.strList {
			i, err := strconv.Atoi(string(s))
			if err != nil {
				continue
			}
			l.Add(i)
		}
		if got := l.isPalindrome(); got != test.want {
			t.Errorf("isPalindrome(%q) == %v", test.strList, got)
		}
	}

}
