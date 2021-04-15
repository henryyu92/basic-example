package string

import (
	"math"
	"strings"
)

func preHandle(str string) string {
	s := make([]rune, 0)
	s = append(s, '#')
	for _, r := range str {
		s = append(s, r)
		s = append(s, '#')
	}
	return string(s)
}

func maxSubPalindrome(str string) int {
	l := len(strings.TrimSpace(str))
	if l == 0 {
		return 0
	}
	s := preHandle(str)
	max := 0
	for i, l := 0, len(s); i < l; i++ {
		p := 0
		for j := 1; i-j >= 0 && i+j < l; j++ {
			if s[i-j] == s[i+j] {
				p++
				continue
			}
			break
		}
		if p > max {
			max = p
		}
	}
	return max
}

func Manacher(str string) int {

	l := len(strings.TrimSpace(str))
	if l == 0 {
		return 0
	}
	s := preHandle(str)
	p := make([]int, len(s))
	r := -1
	c := 0
	max := math.MinInt32

	for i := 0; i < len(s); i++ {
		if r > i {
			if p[2*c-i] > r-i {
				p[i] = p[2*c-i]
			} else {
				p[i] = r - i
			}
		} else {
			p[i] = 1
		}
		for i+p[i] < len(s) && i-p[i] >= 0 {
			if s[i+p[i]] == s[i-p[i]] {
				p[i]++
			} else {
				break
			}
		}
		if i+p[i] > r {
			r = i + p[i]
			c = i
		}
		if max < p[i] {
			max = p[i]
		}
	}

	return max - 1
}
