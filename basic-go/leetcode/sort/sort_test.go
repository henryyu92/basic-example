package sort

import (
	"fmt"
	"math/rand"
	"testing"
)

const MAX = 50

var arr []int

func initArr() {
	N := rand.Intn(20)
	for i := 0; i <= N; {
		arr = append(arr, rand.Intn(MAX))
	}
}

func printArr(arr []int) {
	for _, v := range arr {
		fmt.Printf("[ %d, ", v)
	}
	fmt.Printf("]")
}

func TestBubbleSort(t *testing.T) {
	initArr()
	printArr(arr)
	bubbleSort(arr)
	printArr(arr)
}

func BenchmarkBubbleSort(b *testing.B) {
}
