package stack

import (
	"fmt"
	"testing"
)

func TestNewListStack(t *testing.T) {
	s := NewListStack()
	fmt.Println(s.top)
	fmt.Println(s.top.value)
}

func TestListStackPush(t *testing.T) {

}

func BenchmarkListStackPush(b *testing.B) {

}

func TestListStackPop(t *testing.T) {

}

func BenchmarkListStackPop(b *testing.B) {

}

func TestListStackPeek(t *testing.T) {

}

func BenchmarkListStackPeek(b *testing.B) {

}
