package string

import (
	"math/rand"
	"testing"
)

const letters = "abcdefghijklmnopqrstuvwxyz"

var testCase = make(map[string]string)

func randStr(n int) string {
	b := make([]byte, n)
	for i := range b {
		b[i] = letters[rand.Intn(len(letters))]
	}
	return string(b)
}

func setup() {
	r := rand.Intn(1000)
	for i := 0; i < r; i++ {
		k := randStr(rand.Intn(20))
		v := randStr(rand.Intn(5))
		if len(k) < len(v) {
			continue
		}
		testCase[k] = v
	}
}

func verify(str1, str2 string, p int) bool {

	l1, l2 := len(str1), len(str2)

	if l1 < l2 || l2+p > l1 {
		return false
	}
	for i, j := p, 0; i <= l1-l2 && j < l2; i, j = i+1, j+1 {
		if str1[i] != str2[j] {
			return false
		}
	}
	return true

}

func TestBruteForceMatch(t *testing.T) {
	setup()
	t.Log("Start")
	for k, v := range testCase {
		p := BruteForceMatch(k, v)
		t.Logf("%s match %s returns %d", k, v, p)
		if p == -1 || !verify(k, v, p) {
			// t.Errorf("%s match %s returns %d", k, v, p)
		}
	}
}

func TestNextArr(t *testing.T) {
	s := "abcdabd"
	arr := NextArray(s)
	t.Logf("next array of %s is %v", s, arr)
}
