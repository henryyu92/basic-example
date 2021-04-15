package string

import (
	"testing"
)

var palindromTestCase = []struct {
	src, expect  string
	maxPalindrom int
}{
	{"aba", "#a#b#a#", 3},
	{"abba", "#a#b#b#a#", 4},
}

func TestPreHandle(t *testing.T) {

	for _, c := range palindromTestCase {
		if r := preHandle(c.src); r != c.expect {
			t.Errorf("preHandle(\"%s\") result \"%s\", expect \"%s\"", c.src, r, c.expect)
		}

	}
}

func TestMaxSubPalindrome(t *testing.T) {
	for _, c := range palindromTestCase {
		if r := maxSubPalindrome(c.src); r != c.maxPalindrom {
			t.Errorf("maxSubPalindrome(\"%s\") result %d, expect %d", c.src, r, c.maxPalindrom)
		}
	}
}

func TestManacher(t *testing.T) {
	for _, c := range palindromTestCase {
		if r := Manacher(c.src); r != c.maxPalindrom {
			t.Errorf("manacher(\"%s\") result %d, expect %d", c.src, r, c.maxPalindrom)
		}
	}
}
