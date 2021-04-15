package sort

import (
	"fmt"
	"math/rand"
	"testing"
)

func TestCountingSortPartition(t *testing.T) {
	s := rand.Intn(30)
	arr := make([]int, s)
	for i := 0; i < s; i++ {
		arr[i] = rand.Intn(2)
	}
	fmt.Println(arr)

	countingSortPartition(arr)

	fmt.Println(arr)

}

func BenchmarkCountingSortPartition(b *testing.B) {

}

func TestPartition(t *testing.T) {
	s := rand.Intn(30)
	arr := make([]int, s)
	for i := 0; i < s; i++ {
		arr[i] = rand.Intn(2)
	}

	fmt.Println(arr)

	partition(arr)

	fmt.Println(arr)
}

func BenchmarkPartition(b *testing.B) {

}
