package stack

import "sync"

type (
	// ListStack define a stack implements with list
	ListStack struct {
		top  *node
		lock sync.Mutex
	}
	node struct {
		value interface{}
		next  *node
	}
)

// NewListStack returns a new stack instance
func NewListStack() *ListStack {
	s := &ListStack{}
	s.top = &node{}

	return s
}

// ListStackPush push an item to ListStack
func (s *ListStack) ListStackPush(item interface{}) {

	s.lock.Lock()
	defer s.lock.Unlock()

	node := &node{}
	node.value = item
	node.next = s.top
	s.top = node
}

// ListStackPop pop an item from ListStack
func (s *ListStack) ListStackPop() interface{} {

	s.lock.Lock()
	defer s.lock.Unlock()

	if s.top == nil {
		return nil
	}

	node := s.top
	s.top = node.next
	// for gc
	node.next = nil
	return node.value
}

// ListStackPeek pop but not delete an item from ListStack
func (s *ListStack) ListStackPeek() interface{} {

	s.lock.Lock()
	defer s.lock.Unlock()

	if s.top == nil {
		return nil
	}

	return s.top.value
}
