package list

type node struct {
	value Element
	next  *node
}

type Element interface{}

type List interface {
	Add(e Element)
	Get(i int32) Element
	Remove(i int32)
	Length() int32
}
