package list

import (
	"fmt"
	"math/rand"
	"testing"
)

func TestNewList(t *testing.T) {
	l := NewLinkedList()
	if l == nil {
		t.Error(`NewList() == nil`)
	}
}

func TestAdd(t *testing.T) {
	l := NewLinkedList()
	for i := 0; i < 10; i++ {
		l.Add(rand.Intn(20))
	}
	if !t.Failed() {
		for h := l.head; h != nil; h = h.next {
			fmt.Printf("Add(%d)\t", h.value)
		}
	}
}

func TestGetFirst(t *testing.T) {
	l := NewLinkedList()
	for i := 0; i < 10; i++ {
		l.Add(rand.Intn(20))
	}
	v := l.GetFirst()
	if v == nil {
		t.Error("GetFirst() == nil")
	}
	if !t.Failed() {
		fmt.Println(v)
	}
}

func BenchmarkNewList(b *testing.B) {

}

func BenchmarkAdd(b *testing.B) {

}
