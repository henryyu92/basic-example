package method

import (
	"testing"
)

func TestDistance(t *testing.T) {

	p := Point{1, 2}
	q := Point{4, 6}

	// 子测试
	t.Run("function", func(t *testing.T) {
		if r := Distance(p, q); r != 5 {
			t.Errorf("Distance(%v, %v) expect 5, but %f got", p, q, r)
		}
	})
	t.Run("method", func(t *testing.T) {
		if r := p.Distance(q); r != 5 {
			t.Errorf("%v.Distance(%v), q exepect 5, but %f got", p, q, r)
		}
	})
}

func TestPathDistance(t *testing.T) {
	perim := Path{
		{1, 1},
		{5, 1},
		{5, 4},
		{1, 1},
	}

	if r := perim.Distance(); r != 12 {
		t.Errorf("%v.Distance() expect 12, but %f got", perim, r)
	}
}
