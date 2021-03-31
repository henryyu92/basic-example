## 栈
栈是一种数据项按序排列的数据结构，只能在一端对数据项进行插入和删除，允许进行插入和删除的一端称为栈顶，另一端称为栈底。向一个栈插入新元素又称作进栈、入栈或压栈，它是把新元素放到栈顶元素的上面，使之成为新的栈顶元素；从一个栈删除元素又称作出栈或退栈，它是把栈顶元素删除掉，使其相邻的元素成为新的栈顶元素。

栈只能在栈顶插入和删除数据使得栈中的数据满足 FILO(先入后出)的特性，并且也使得栈具有记忆性。
### 数组实现栈
使用数组实现栈时只能在数组的最后位置进行添加或删除操作以模拟栈的入栈和出栈。

数组在物理上是一块连续的内存空间，当有大量的入栈操作时数组需要不断的开辟新的内存空间并复制数据导致性能低下，且数组中的元素即使删除也不会使得数组大小收缩，因此当栈的大小分布不均匀且远小于栈的容量时会导致内存泄漏。

```go
// Item of stack
type Item interface{}

// ArrayStack is a stack implements by array
type ArrayStack struct {
	items []Item
	lock  sync.RWMutex
}

// NewArrayStack returns a ArrayStack
func NewArrayStack(cap int) *ArrayStack {
	s := &ArrayStack{}
	s.items = make([]Item, cap)
	return s
}

// ArrayStackPush push an item to stack
func (s *ArrayStack) ArrayStackPush(item Item) {
	s.lock.Lock()
	defer s.lock.Unlock()
	s.items = append(s.items, item)
}

// ArrayStackPop return and remove the top element
func (s *ArrayStack) ArrayStackPop() Item {
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
func (s *ArrayStack) ArrayStackPeek() Item {
	s.lock.Lock()
	defer s.lock.Unlock()
	if len(s.items) == 0 {
		return nil
	}
	return s.items[len(s.items)-1]
}
```
### 链表实现栈
使用链表实现栈时，只需要保证入栈和出栈操作在链表尾部或头部执行即可。
```go
type (
	// ListStack define a stack implements with list
	ListStack struct {
		top    *node
		lock   sync.Mutex
	}
	node struct {
		value Item
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
func (s *ListStack) ListStackPush(item Item) {

	s.lock.Lock()
	defer s.lock.Unlock()

	node := &node{}
	node.value = item
	node.next = s.top
	s.top = node
}

// ListStackPop pop an item from ListStack
func (s *ListStack) ListStackPop() Item {

	s.lock.Lock()
	defer s.lock.Unlock()

	if s.top = nil{
		return nil
	}

	node := s.top
	s.top = node.next
	// for gc
	node.next = nil
	return node.value
}

// ListStackPeek pop but not delete an item from ListStack
func (s *ListStack) ListStackPeek() Item {

	s.lock.Lock()
	defer s.lock.Unlock()

	if s.top = nil{
		return nil
	}

	return s.top.value
}
```
### 队列实现栈
队列中的数据满足 FIFO(先入先出)特性，因此使用单个队列无法满足栈的需求，通过引入额外的辅助队列将主队列中出栈元素前的所有元素移动到辅助队列中可以实现 FILO 特性，并且在数据移动完成之后两个队列指针互换可以达到复用的效果。

实现原理：
> 创建主队列 q 和辅助队列 help，push 操作时往主队列 q 里面插入数据；<br>
> pop 操作时先将队列尾部元素之前的所有元素入队到 help 队列中，然后将队尾元素出队返回；peek 操作时队尾元素返回之前需要入队到 help 队列中；<br>
> 队尾元素出队后将主队列 q 和 辅助队列 help 的指针互换，实现内存复用；<br>

- push 时间复杂度：O(1)
- pop 和 peek 时间复杂度：O(M)，M 表示已经入栈的元素个数
- 空间复杂度：O(N)，引入了额外的辅助队列

算法实现：
```go
type TwoQueueStack struct{
	queue Queue[int]
	help Queue[int]
}

func (s *TwoQueueStack) push(e int){
	s.queue.add(e)
}

func (s *TwoQueueStack) peek() int{
	if s.queue.isEmpty(){
		return nil
	}
	for ;s.queue.size() != 1;{
		s.help.add(s.queue.poll())
	}
	res := s.queue.poll()
	s.help.add(res)
	swap()
	return res
}

func (s *TwoQueueStack) pop() int{
	if s.queue.isEmpty(){
		return nil
	}
	for ;s.queue.size() > 1;{
		s.help.add(s.queue.poll())
	}
	res := s.queue.poll()
	swap()
	return res
}

func (s *TwoQueueStack) swap(){
	tmp := s.help
	s.help = s.queue
	s.queue = tmp
}
```
### 最小栈实现
最小栈是栈的一种变化，其在保留了栈的先入后出特性的基础上增加了获取最小值的功能，且最小栈的 push, pop, min 三种操作的时间复杂度都是 O(1)。

算法思想：
> 栈元素在出栈和入栈操作时需要保证获取栈中最小元素的时间复杂度为 O(1)。在时间复杂度 O(1) 内获取元素通常可以考虑索引、哈希或者栈，但是考虑到栈中索引需要移动数据而哈希并不具备记忆功能，因此可以使用额外的栈作为辅助空间使得在 push, pop, min 三种操作的时间复杂度都满足 O(1)。<br>
> 辅助栈的方法是使用一个额外的栈记录栈的最小值，当元素压栈时在数据入栈的同时需要和辅助栈顶的元素比较，若小于辅助栈顶元素则该元素也需要压入到辅助栈中；当元素出栈时需要比较数据栈顶元素是否和辅助栈元素相等，如果相等则辅助栈顶元素也需要出栈；获取最小值时只需要获取辅助栈顶的元素即可。

算法实现：
```go
type MyStack struct{
	dataStack Stack[int]
	minStack Stack[int]
}

func (s *MyStack) getMin() int{
	if s.minStack.isEmpty(){
		return nil
	}
	return s.minStack.peek()
}

func (s *MyStack) push(e int){
	if s.minStack.isEmpty(){
		s.minStack.push(e)
	// 入栈元素和最小元素相等时最小值栈入栈
	}else if e <= s.getMin(){
		s.dataStack.push(e)
		s.minStack.push(e)
	}else{
		s.dataStack.push(e)
	}
}

func (s *MyStack) pop() int{
	if s.dataStack.isEmpty(){
		return nil
	}
	e := s.dataStack.pop()
	// 出栈元素和最小值栈的栈顶值相等时最小值栈出栈
	if e == s.getMin(){
		s.minStack.pop()
	}
	return e
}
```
### 栈的应用
栈具有先进后出的特性，即能够再次访问到之前的数据，具有记忆功能，常用于递归。
#### 括号匹配问题([LeetCode](https://leetcode-cn.com/problems/valid-parentheses/))
问题描述：
> 
解题思路：
> 遍历字符串，依次判断字符是否和栈顶字符对应，如不对应则入栈，否则栈顶元素出栈；当字符串遍历完栈为空则说明字符匹配
```go
func isValid(s string) bool{
    bracketsMap := map[uint8]unit8{'{': '}', '[': ']', '(': ')'}
	if s == ""{
	    return true
	}
	stack := NewStack()
	for i := 0; i < len(s); i++{
	    if stack.Len() > 0{
		    if c, ok := bracketsMap[stack.Peek().(unit8)]; ok && c == s[i]{
		        stack.Pop()
			    continue
		    }
	    }
	    stack.Push(s[i])
	}
	return stack.Len() == 0
}
```
#### 路径简化问题([leetCode](https://leetcode-cn.com/problems/simplify-path/submissions/))
解题思路：
> 使用 "/" 分割路径后遍历，如果是 "." 或者 "" 则继续遍历；如果是 ".." 则需要判断栈是否为空，非空则需要栈顶出栈；否则需要进行入栈操作；遍历完成后如果栈为空则返回 "/"，否则将栈底到栈顶的字符串用 "/" 连接起来返回
```go
type Node struct{
    prev *Node
    value string
}

func simplifyPath(path string) string {
    if path == ""{
        return ""
    }

    var top *Node
    for _, s := range strings.Split(path, "/"){
        if s == "" || s == "."{
            continue
        }
        if s == ".."{
            if top == nil{
                continue
            }
            // 出栈
            tem := top.prev
            // 释放内存
            top.prev = nil
            top = tem
            continue
        }
        // 入栈
        node := &Node{top, s}
        top = node
    }
    if top == nil {
        return "/"
    }
    var s string
    for ; top != nil; top = top.prev{
        s = "/" + top.value + s
    }
    return s
}
```


## 单调栈
单调栈是栈的一种衍生，单调栈中的数据是有序的，单调栈分为单调递增栈和单调递减栈：
- 单调递增栈中的元素从栈底到栈顶单调递增
- 单调递减栈中的元素从栈底到栈顶单调递减

> 数组的 MaxTree 定义：数组必须没有重复元素；MaxTree 是一棵二叉树，数组的每一个值对应一个二叉树节点；MaxTree 树在内的子树上，值最大的节点都是树的头。在时间复杂度 O(N) 内生成数组的 MaxTree

```java
public Node getMaxTree(int[] arr){
    Node[] nArr = new Node[arr.length];
    for(int i = 0; i != arr.length; i++){
        nArr[i] = new Node(arr[i]);
    }
    Stack<Node> stack = new Stack<Node>();
    HashMap<Node, Node> lBigMap = new HashMap<Node, Node>();
    HashMap<Node, Node> rBigMap = new HashMap<Node, Node>();
    for(int i = 0; i != nArr.length; i++){
        Node curNode = nArr[i];
        while((!stack.isEmpty()) && stack.peek().value < curNode.value){
            popStackSetMap(stack, lBigMap);
        }
        stack.push(curNode);
    }
    while(!stack.isEmpty()){
        popStackSetMap(stack, lBigMap);
    }
    for(int i = nArr.length - 1; i != -1; i--){
        Node curNode = nArr[i];
        while((!stack.isEmpty()) && stack.peek().value < curNode.value){
            popStackSetMap(stack, rBigMap);
        }
        stack.push(curNode);
    }
    while(!stack.isEmpty()){
        popStackSetMap(stack, rBigMap);
    }
    Node head = null;
    for(int i = 0; i != nArr.length; i++){
        Node curNode = nArr[i];
        Node left = lBigMap.get(curNode);
        Node right = rBigMap.get(curNode);
        if(left == null && right == null){
            head = curNode;
        }else if(left == null){
            if(right.left == null){
                right.left = curNode;
            }else{
                right.right = curNode;
            }
        }else if(right == null){
            if(left.left == null){
                left.left = curNode;
            }else{
                left.right = curNode;
            }
        }else{
            Node parent = left.value < right.value ? left : right;
            if(parent.left == null){
                parent.left = curNode;
            }else{
                parent.right = curNode;
            }
        }
    }
    return head;
}

public void popStackSetMap(Stack<Node> stack, HashMap<Node, Node> map){
    Node popNode = stack.pop();
    if(stack.isEmpty()){
        map.put(popNode, null);
    }else{
        map.put(popNode, stack.peek());
    }
}
```
> 给定一个整型矩阵 map，其中的值只有 0 和 1 两种，求其中全是 1 的所有矩阵区域中，最大的矩形区域为 1 的数量

```java
// 直方图中找到最大的矩形面积
public int maxRecFormBottom(int[] height){
    if(height == null || height.length == 0){
        return 0;
    }
    int maxArea = 0;
    Stack<Integer> stack = new Stack<Integer>();
    for(int i = 0; i < height.length; i++){
        while(!stack.isEmpty() && height[i] <= height[stack.peek()]){
            int j = stack.pop();
            int k = stack.isEmpty() ? -1 : stack.peek();
            int curArea = (i - k - 1) * height[j];
            maxArea = Math.max(maxArea, curArea);
        }
        stack.push(i);
    }
    while(!stack.isEmpty()){
        int j = stack.pop();
        int k = stack.ieEmpty() ? -1 : stack.peek();
        int curArea = (height.length - k - 1) * height[j];
        maxArea = Math.max(maxArea, curArea);
    }
    return maxArea;
}
// 矩阵最大矩形区域面积，转换为直方图问题
public int maxRecSize(int[][] map){
    if(map == null || map.length == 0 || map[0].length == 0){
        return 0;
    }
    int maxArea = 0;
    int[] height = new int[map[0].lenght];
    for(int i = 0; i < map.length; i++){
        for(int j = 0; j < map[0].lenght; j++){
            height[j] = map[i][j] == 0 ? 0 : height[j] + 1;
        }
        maxArea = Math.max(maxRecFormBottom(height), maxArea);
    }
    return maxArea;
}
```

> 环形数组

```java
public class Pair{
    private int value;
    private int times;

    public Pair(int value){
        this.value = value;
        this.times = 1;
    }
}
public long communications(int[] arr){
    if(arr == null || arr.length < 2){
        return 0;
    }
    int size = arr.length;
    int maxIndex = 0;
    // 找到数组中最大值位置
    for(int i = 0; i < size; i++){
        maxIndex = arr[maxIndex] < arr[i] ? i : maxIndex;
    }
    int value = arr[maxIndex];
    // 最大值的下一个位置
    int index = nextIndex(size, maxIndex);
    long res = 0L;
    Stack<Pair> stack = new Stack<Pair>();
    // 单调栈数据从大到小
    stack.push(new Pair(value));
    // 绕环遍历
    while(index != maxIndex){
        value = arr[index];
        // 处理不满足单调栈的规则的情况
        while(!stack.isEmpty() && stack.peek().value < value){
            int times = stack.pop().times;
            res += getInternalSum(times) + times;
            res += stack.isEmtpy() ? 0 : times;
        }
        // 元素相等时，只需要增加元素出现的次数
        if(!stack.isEmpty() && stack.peek().value == value){
            stack.peek().times++;
        }else{
            stack.push(new Pair(value));
        }
        index = nextIndex(size, index);
    }
    // 处理遍历完之后栈中剩余的元素
    while(!stack.isEmpty()){
        int times = stack.pop().times;
        res += getInternalSum(times);
        if(!stack.isEmpty()){
            res += times;
            if(stack.size() > 1){
                res += times;
            }else{
                res += stack.peek().times > 1 ? times : 0;
            }
        }
    }
    return res;
}

public int nextIndex(int size, int i){
    return i < (size - 1) ? (i + 1) : 0;
}

public long getInternalSum(int n){
    return n == 1L ? 0L : (long)n * (long)(n - 1) / 2L;
}
```