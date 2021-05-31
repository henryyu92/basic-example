## 队列
队列是一种先进先出的数据结构
### 链表实现队列
```go
type(
    Queue struct{
	    head *node
		tail *node
		length int
		lock *sync.RWMutex
	}
	node struct{
	    value interface{}
		pre *node
		next *node
	}
)

func NewQueue() *Queue{
    return &Queue{nil, nil, 0, &sync.RWMutex{}}
}

func (q *Queue) Offer(value interface{}){
    this.lock.Lock()
	defer this.lock.Unlock()
    node := &node{value, q.tail, nil}
    q.tail = node
    q.length++	
}

func (q *Queue) Poll() interface{}{
    q.lock.Lock()
	defer q.lock.Unlock()
	if q.length == 0{
	    return nil
	}
	res := q.head.value
	// 移除头结点
	q.head = q.head.next
	q.head.pre.next = nil
	q.head.pre = nil
	q.length--
}
```
### 数组实现队列
数组实现队列使用了三个变量：
- end 表示下次填充的位置
- start 表示下次弹出的位置
- size 表示队列中的元素个数

算法实现：
```go
type ArrayQueue struct{
	arr int[]
	size int
	start int
	end int
}

func (q *ArrayQueue) newArrayQueue(initialSize int){
	q.arr = int[initialSize]
	size = 0
	start = 0
	end = 0
}

func (q *ArrayQueue) push(e int) Error{
	if q.size == len(arr){
		return error
	}
	size++
	arr[end] = e
	end = end == arr.length - 1 ? 0 : end + 1
	return nil
}

func (q *ArrayQueue) poll() (int, Error){
	if q.size == 0{
		return (nil, error)
	}
	size--
	tmp := arr[start]
	start = start == arr.length - 1 ? 0 : start + 1
	return (tmp, nil)
}

func (q *ArrayQueue) peek() (int, Error){
	if size == 0{
		return (nil, nil)
	}
	return (arr[start], nil)
}
```
### 栈实现队列
> 使用两个栈 push 栈和 pop 栈，队列入队时向 push 栈压栈，当队列出队时如果 pop 栈有数据则直接出栈，如果 pop 栈没有数据则需要将 push 栈中的所有数据出栈并压入 pop 栈中

算法实现：
```go
type TwoStackQueue struct {
	pushStack Stack[int]
	popStack Stack[int]
}

func (q *TwoStackQueue) transfer(){
	// 只有当 pop 栈为空时才能将 push 栈中的数据全部出栈压入 pop 栈中
	if q.popStack.empty(){
		for ;q.pushStack.empty();{
			q.popStack.push(q.pushStack.pop())
		}
	}
}

func (q *TwoStackQueue) push(e int){
	q.pushStack.push(e)
	q.transfer()
}

func (q *TwoStackQueue) pop() int{
	if q.popStack.empty() && q.pushStack.empty(){
		return nil
	}
	q.transfer()
	return q.popStack.pop()
}

func (q *TwoStackQueue) peek() int{
	if q.popStack.empty() && q.pushStack.empty(){
		return nil
	}
	q.transfer()
	return q.popStack.peek()
}
```