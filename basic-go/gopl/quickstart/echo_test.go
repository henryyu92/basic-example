package quickstart

import (
	"os"
	"strconv"
	"testing"
)

func TestEcho1(t *testing.T) {
	os.Args = prepare()
	echo1()
}

func TestEcho2(t *testing.T) {
	os.Args = prepare()
	echo2()
}

func TestEcho3(t *testing.T) {
	os.Args = prepare()
	echo3()
}

func TestEcho4(t *testing.T) {
	os.Args = prepare()
	echo4()
}

func BenchmarkEcho4(b *testing.B) {
	b.N = 1000
	echo4()
}

func prepare() []string {
	os.Args = make([]string, 3)
	for i := range os.Args {
		os.Args[i] = "hello" + strconv.Itoa(i)
	}
	return os.Args
}
