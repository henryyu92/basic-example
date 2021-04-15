package list

type (
	LinkedList struct {
		len  int
		head *node
	}
)

func NewLinkedList() *LinkedList {
	return &LinkedList{}
}

func (l *LinkedList) Add(item Element) {
	node := &node{item, l.head}
	l.head = node
	l.len++
}

func (l *LinkedList) Get(i int32) Element {
	p := l.head
	for index := 0; index < int(i); index++ {
		p = p.next
	}
	return p.value
}

func (l *LinkedList) GetFirst() Element {
	return l.head.value
}

func (l *LinkedList) Length() int {
	return l.len
}
