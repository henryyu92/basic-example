### 定义
二叉搜索树中的关键字总是以满足二叉搜索树性质的方式存储：
> 设 x 是二叉搜索树种的一个结点，如果 y 是 x 左子树中的一个结点，那么 y.key <= x.key；如果 y 是 x 右子树中的一个结点，那么 y.key >= x.key。

二叉搜索树可以使用一个链表数据结构来表示，其中每个结点是一个对象。除了 key 和数据之外，每个结点还包含属性 left、right 和 p 分别表示指向左孩子、右孩子和 parent 的指针：
```go
type (
    BinarySearchTree struct{
        root *TreeNode
        size int32
    },
    TreeNode struct{
        left *TreeNode
        right *TreeNode
        p *TreeNode
        key int32
    }
)
```
### 遍历
#### 中序遍历
中序遍历输出的根的关键字位于其左子树的关键字和右子树的关键字之间。

递归方式实现：
> 递归实现是从根节点开始递归遍历左子树，然后输出根节点，最后再遍历右子树
```go
func tree_inorder_walk(tree *TreeNode){
    if tree == nil{
        return
    }
    tree_inorder_walk(tree.left)
    fmt.Println(tree.key)
    tree_inorder_walk(tree.right)
}
```
非递归方式实现：
> 非递归的方式使用了栈来实现，从根节点开始选择左子结点入栈直到左子结点为 nil 然后栈顶元素出栈，再转向右子树执行入栈出栈操作
```go
type stackNode struct{
    prev *stackNode
    treeNode *TreeNode
}
func inorderTraversal(root *TreeNode) []int {
    var res []int
    var top *stackNode
    curr := root

    for curr != nil || top != nil{
       
        // 左子节点入栈
        for curr != nil {
            tmp := top
            top = &stackNode{tmp, curr}
            curr = curr.Left
        }      
        
        // 左子节点为 Nil，栈顶元素出栈
        res = append(res, top.treeNode.Val)
        // 转向右子节点
        curr = top.treeNode.Right
            
        tmp := top
        top = tmp.prev
        tmp.prev = nil
    }
    return res
    
}
```
#### 前序遍历
前序遍历输出的根的关键字位于其左右子树的关键字前面。

递归方式实现：
> 递归方式从先遍历根节点，然后以递归的方式遍历左子树节点，最后再以递归的方式遍历右子树节点
```go
func tree_preorder_walk(tree *TreeNode){
    if tree == nil{
        return
    }
    fmt.Println(tree.key)
    tree_preorder(tree.left)
    tree_preorder(tree.right)
}
```
非递归方式实现：
> 非递归方式需要借助栈来实现，如果栈不为空则栈顶元素先出栈，然后如果右子树不为空则将右子树压栈，最后判断左子树是否为空不为空则压栈(必须要先判断右子树再判断左子树，这样右子树在左子树后面)
```go
type stackNode struct{
    prev *stackNode
    treeNode *TreeNode
}
func preorderTraversal(root *TreeNode) []int {
    if root == nil{
        return nil
    }
    var res []int
    top := &stackNode{nil, root}
    for top != nil{
        // 出栈
        node := top.treeNode
        res = append(res, node.Val)
        tmp := top
        top = tmp.prev
        tmp.prev = nil
        
        if node.Right != nil{
            top = &stackNode{top, node.Right}
        }
        
        if node.Left != nil{
            top = &stackNode{top, node.Left}
        }
    }
    return res
} 
```
#### 后序遍历
后序遍历输出的根的关键字位于其左右子树的关键字后面。

递归方式实现：
```go
func tree_postorder_walk(tree *TreeNode){
    if tree == nil{
        return
    }
    tree_postorder_walk(tree.left)
    tree_postorder_walk(tree.right)
    fmt.Println(tree.key)
}
```
非递归方式实现：
> 非递归实现可以先实现 “根-右-左” 的遍历，然后将遍历结果压栈，最后元素出栈即为 “左-右-根” 方式的后序遍历
```go
```
#### 层次遍历
### 查找
除了 search 操作之外，二叉搜索树还能支持如 minmum、maxmum、successor 和 predecessor 的查询操作，这些操作都可以在 O(h) 的时间内执行完。
> 在一棵高度为 h 的二叉搜索树上，动态集合上的操作 search、minmum、maxmum、successor 和 predecessor 可以在 O(h) 时间内完成，因为这些操作过程都是遵循一条简单路径沿树向上或者遵循一条简单路径沿树向下。
#### 关键字查找
在一颗二叉搜索树中查找一个具有给定关键字 k 的结点需要从树根开始查找，并沿着树中的一条简单路径向下进行，对于遇到的每个结点 x 需要比较关键字 k 和 x.key 的大小，如果 k == x.key 则查找终止，如果 k < x.key 则需要在 x 的左子树查找，如果 k > x.key 则需要在 x 的右子树中查找。

使用递归方式实现：
```go
func tree_search(x *TreeNode, k int) {
    if x == nil || k == x.key {
        return x
    }
    if k < x.key {
        return tree_search(x.left, k)
    }
    if k > x.key {
        return tree_search(x.right, k)
    }
}
```
使用非递归方式实现：
```go
func tree_search(x *TreeNode, k int) {
    for x != nil && k != x.key{
        if k < x.key{
            x = x.left
        }else{
            x = x.right
        }
    }
    return x
}
```
#### 最大关键字和最小关键字
通过从根结点开始沿着 left 孩子指针直到遇到一个 nil 则可搜索到二叉搜索树的最小关键字：
```go
func tree_minimum(x *TreeNode){
    for x.left != nil{
        x = x.left
    }
    return x
}
```
沿着 right 孩子指针直到遇到第一个 nil 则可以搜索到二叉搜索树的最大关键字：
```go
func tree_maximum(x *TreeNode){
    for x.right != nil{
        x = x.right
    }
    return x
}
```
#### 后继和前驱
如果二叉搜索树种的所有关键字互不相同，则一个结点 x 的后继是大于 x.key 的最小关键字的结点，前驱是小于 x.key 的最大关键字的结点。一棵二叉搜索树的结构允许通过没有任何关键字比较来确定一个结点的后继(中序遍历该结点后的结点)和前驱(中序遍历该结点前的结点)。

查找一个结点的后继分为两种情况：如果结点 x 的右子树非空，则 x 的后继是 x 的右子树的最小节点；如果 x 的右子树为空则需要沿树向上查找直到找到第一个节点 y 使得 x 是 y 的左子树中的结点。
```go
func tree_successor(x *TreeNode){
    if x.right != nil{
        return tree_minimum(x.right)
    }
    y = x.p
    for y != nil && x == y.right{
        x, y = y, y.p
    } 
    return y
}
```
同理，查找一个结点的前驱也分为两种情况：如果结点 x 的左子树非空，则 x 的前驱是 x 的左子树的最大结点；如果 x 的左子树为空则需要沿树向上查找直到找到第一个节点 y 使得 x 是 y 的右子树中的结点。
```go
func tree_predecessor(x *TreeNode){
    if x.left != nil{
        return tree_maximum(x.left)
    }
    y = x.p
    for y != nil && x == y.left{
        x, y = y, y.p
    }
    return y
}
```
### 插入和删除
插入和删除操作会引起由二叉搜索树表示的动态集合的变化，需要修改数据结构但在修改时需要保持二叉搜索树的性质成立。
#### 插入
向一棵二叉搜索树插入一个数据 v 时需要从根结点触发，查找到可以插入结点的位置，然后将结点插入到该位置：
```go
func tree_insert(root *TreeNode, z *TreeNode){
    x = root
    y = nil
    // 找到可以插入的结点位置
    for x != nil{
        y = x
        if z.key < x.key{
            x = x.left
        }else{
            x = x.right
        }
    }
    // 插入节点
    z.p = y
    // 空树
    if y == nil{
        root = z
    }else if z.key < y.key{
        y.left = z
    }else{
        y.right = z
    }

}
```
#### 删除
从二叉搜索树删除一个结点分为三种情况：
- 如果 z 没有孩子结点，那么只是简单的删除它并修改父节点的孩子指针为 nil
- 如果 z 只有一个孩子，那么在删除 z 之后需要将 z 的孩子提升到 z 的位置上并修改父节点的孩子指针为 z 的孩子
- 如果 z 有两个孩子，那么找 z 的后继 y (一定在 z 的右子树中)并让 y 占据 z 的位置，z 原来的右子树部分成为 y 的新右子树，z 原来的左子树成为 y 的新左子树。

二叉搜索树删除操作涉及子树的移动，定义一个 transplant 过程表示用一棵子树替换另一棵子树并成为其双亲的孩子节点：
```go
// 使用一棵以 v 为根的子树替换一棵以 u 为根的子树
func transplant(root *TreeNode, u *TreeNode, v *TreeNode){
    if u.p == nil{
        root = v
    }else if u == u.p.left{
        u.p.left = v
    }else{
        u.p.right = v
    }
    if v != nil{
        v.p = u.p
    }
}
```
v 替换 u 时如果 u 是树根则直接将树根替换为 v，如果 u 是左孩子则需要将父结点的左孩子指针更换为 v，如果 u 是右孩子则需要将父结点的右孩子指针替换为 v。在替换完父结点的孩孩子指针后，需要将 v 的父指针替换为新的父指针。

利用 transplant 过程可以从二叉搜索树种删除 z 结点：
```go
```
### 构建

### 二叉搜索树判断
二叉搜索树的中序遍历是一个递增的序列



