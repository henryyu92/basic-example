package cache

type Element struct{
	Key, Value interface{}
	Last, Next *Element
}

type LinkedList struct{
	head, tail *Element
}

func NewLinkedList() *LinkedList{
	return nil
}

func (l *LinkedList) Add(e Element){

}

func (l *LinkedList) MoveToTail(e Element){

}

func (l *LinkedList) RemoveHead(){

}

type LRUCache struct{
	list LinkedList
	keyNode map[interface{}]interface{}
	cap int
}

func NewLRUCache(cap int) *LRUCache{
	return nil
}

func (c *LRUCache) Get(key interface{}) (value interface){
	return nil
}

func (c *LRUCache) Set(key, value interface{}){
	
}