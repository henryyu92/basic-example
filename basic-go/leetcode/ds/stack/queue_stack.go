package stack

import (
	"goAlgorithm/algorithm/ds/queue"
	"sync"
)

// QueueStack is a stack constructed by two queue
type QueueStack struct {
	q, help *queue.Queue
	lock    sync.Mutex
}

// NewQueueStack create a new QueueStack
func NewQueueStack() *QueueStack {
	s := &QueueStack{}
	s.q = queue.NewQueue()
	s.help = queue.NewQueue()
	return s
}

func (s *QueueStack) QueueStackPush(item interface{}) {
	s.lock.Lock()
	defer s.lock.Unlock()

	node := &node{}
	node.value = item

}

func (s *QueueStack) QueueStackPop() interface{} {
	return nil
}

func (s *QueueStack) QueueStackPeek() interface{} {
	return nil
}
