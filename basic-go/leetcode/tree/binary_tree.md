### 二叉树的中序遍历([leetcode-94](https://leetcode-cn.com/problems/binary-tree-inorder-traversal/))
构造二叉树结点
```go
type TreeNode struct {
	Left  *TreeNode
	Right *TreeNode
	Val   int32
}
```
#### 递归遍历
- 时间复杂度：O(n)。递归的时间 T(n) = 2 * T(n/2) + 1
- 空间复杂度：最坏情况下 O(n)，平均情况 O(logN)
```go
func inorderTraversal(root *TreeNode) []int {
    var res []int
    
    if root == nil{
        return nil
    }
    if root.Left != nil{
        res = append(res, inorderTraversal(root.Left)...)
    }
    res = append(res, root.Val)
    if root.Right != nil{
        res = append(res, inorderTraversal(root.Right)...)
    }
    return res
}
```
#### 基于栈的遍历
基于栈的遍历是将遍历过程模拟为二叉树的入栈和出栈的过程。
- 时间复杂度：O(n)
- 空间复杂度：O(n)
```go
public List<Integer> inorderTraversal(TreeNode root){
    List<Integer> res = new ArrayList<>();
	Stack<TreeNode> stack = new Stack<>();
	TreeNode curr = root;
	while(curr != null || !stack.isEmpty()){
	    while(curr != null){
		    stack.push(curr);
			curr = curr.left;
		}
		curr = stack.pop();
		res.add(curr.val);
		curr = curr.right;
	}
	return res;
}
```
#### 莫里斯遍历
莫里斯遍历使用线索二叉树的数据结构遍历：
- 将当前节点 current 初始化为根节点
- 当 current 没有左子节点时将 current 输出并进入右子树
- 当 current 有左子树，另 current 成为左子树最右侧结点的右子节点并进入左子树

复杂度分析：
- 时间复杂度：O(n)O(n)。 想要证明时间复杂度是O(n)O(n)，最大的问题是找到每个节点的前驱节点的时间复杂度。乍一想，找到每个节点的前驱节点的时间复杂度应该是 O(n\log n)O(nlogn)，因为找到一个节点的前驱节点和树的高度有关。
但事实上，找到所有节点的前驱节点只需要O(n)O(n) 时间。一棵 nn 个节点的二叉树只有 n-1n−1 条边，每条边只可能使用2次，一次是定位节点，一次是找前驱节点。
故复杂度为 O(n)O(n)。
- 空间复杂度：O(n)O(n)。使用了长度为 nn 的数组。
```java
public List<Integer> inorderTraversal(TreeNode root){
    List<Integer> res = new ArrayList<>();
	TreeNode curr = root;
	TreeNode pre;
	while(curr != null){
		if(curr.left == null){
			res.add(curr.val);
			curr = curr.right;
		}else{
			pre = curr.left;
			while(pre.right != null){
			    pre = pre.right;
			}
			pre.right = curr;
			TreeNode temp = curr;
			curr = curr.left
			temp.left = null;
		}
	}
	return res;
}
```
### 二叉树的前序遍历
#### 递归
前序遍历是先输出根结点，然后输出左子树，然后输出右子树
```go
func preorderTraversal(root *TreeNode) []int {
    var res []int
    if root == nil{
        return nil
    }
    res = append(res, root.Val)
    if root.Left != nil{
        res = append(res, preorderTraversal(root.Left)...)
    }
    if root.Right != nil{
        res = append(res, preorderTraversal(root.Right)...)
    }
    return res
}
```
#### 莫里斯遍历
算法思想：从当前节点向下访问先序遍历的前驱节点，每个前驱节点恰好被访问两次

首先从当前节点开始，找到当前节点的中序遍历的前驱节点(左子树中最右的节点)，建立一条伪边 predecessor.right = root，
### 二叉树的后序遍历
### 不同的二叉搜索树
给定一个整数 n，所有由 1...n 为节点所组成的二叉搜索树有多少种
#### 动态规划
为了根据序列创建一棵二叉搜索树，可以遍历每个数字 i，将该数字作为树根，小于 i 的数字作为左子树，大于 i 的数字作为右子树，由于根是不相同的，所以每棵二叉搜索树都能保证唯一。

定义 G(n) 表示长度为 n 的序列的不同二叉搜索树的个数，F(i,n) 表示以 i 为树根的不同二叉搜索树的个数(1 < i < n)

根据动态规划的思想，G(n) = F(1,n)+F(2,n)+...+F(n,n)，对于边界情况有 G(0)=1，G(1)=1。考虑到以 i 为树根的左子树和右子树也分别是序列 1...i-1 和 i+1...n 组成的二叉搜索树，因此 F(i,n)=G(i-1)*G(n-i)，所以有 G(n)=G(0)*G(n-1)+G(1)*G(n-2)+...+G(n-1)*G(0)

于是有：
```java
public int numTrees(int n){
    int[] G = new int[n+1];
	G[0] = 1;
	G[1] = 1;
	
	for(int i = 2; i <= n; i++){
	    for(int j = 1; j<=i;j++){
		    G[i] += G[j-1]*G[i-j]
		}
	]
	return G[n]
}
```
复杂度分析：
- 时间复杂度：O(N^2)
- 空间复杂度：O(N)
#### 卡塔兰数
卡塔兰数满足递归式：h(n)=h(0)h(n-1)+...+h(n-1)h(0)，可以简化为 h(n)=(4n-2)/(n+1)*h(n-1)

卡塔兰数的解为：h(n)=(2n)!/(n!*(n+1)!)

```java
public int numTrees(int n){
    long C = 1;
	for(int i = 0; i < n; i++){
	    C = C*2*(2*i+1)/(i+2)
	}
}
```
复杂度分析：
- 时间复杂度：O(N)，只有一层循环
- 空间复杂度：O(1)，只需要一个变量来存储中间与最终结果
### 不同的二叉搜索树([leetcode-95](https://leetcode-cn.com/problems/unique-binary-search-trees-ii/))
给定一个整数 n，生成所有由 1...n 为节点所组成的二叉搜索树