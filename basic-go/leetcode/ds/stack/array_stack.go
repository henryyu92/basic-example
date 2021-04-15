package stack

import (
	"goAlgorithm/algorithm/ds"
	"sync"
)

// ArrayStack is a stack implements by array
type ArrayStack struct {
	items []ds.Element
	lock  sync.RWMutex
}

// NewArrayStack returns a ArrayStack
func NewArrayStack(cap int) *ArrayStack {
	s := &ArrayStack{}
	s.items = make([]ds.Element, cap)
	return s
}

// ArrayStackPush push an item to stack
func (s *ArrayStack) ArrayStackPush(element ds.Element) {
	s.lock.Lock()
	defer s.lock.Unlock()
	s.items = append(s.items, element)
}

// ArrayStackPop return and remove the top element
func (s *ArrayStack) ArrayStackPop() ds.Element{
	s.lock.Lock()
	defer s.lock.Unlock()
	if len(s.items) == 0 {
		return nil
	}
	p := len(s.items) - 1
	i := s.items[p]
	// for gc
	s.items[p] = nil
	return i

}

// ArrayStackPeek return without remove the top element
func (s *ArrayStack) ArrayStackPeek() ds.Element {
	s.lock.Lock()
	defer s.lock.Unlock()
	if len(s.items) == 0 {
		return nil
	}
	return s.items[len(s.items)-1]
}
