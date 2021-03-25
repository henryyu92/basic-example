package tree

// 给定二叉树，判断是否是高度平衡的二叉树
// 高度平衡的二叉树：每个节点的左右两个子树的高度差的绝对值不超过 1

// 自顶向下递归： 左子树是平衡 & 右子树平衡 & 左子树高度和右子树高度差不超过 1
// 时间复杂度： O(N^2), 每个结点都会访问到，每个结点需要计算高度
// 空间复杂度： O(N)
func isBalanced(root *TreeNode) bool {

	if root == nil {
		return true
	}
	return isBalanced(root.Left) && isBalanced(root.Right) && abs(height(root.Left)-height(root.Right)) <= 1

}

func height(root *TreeNode) int {
	if root == nil {
		return 0
	}
	return max(height(root.Left), height(root.Right)) + 1
}

func max(x, y int) int {
	if x > y {
		return x
	}
	return y
}

func abs(x int) int {
	if x < 0 {
		return -1 * x
	}
	return x
}

// 自顶向下递归会重复计算高度，采用自底向上递归可以复用已经计算的高度
// 如果子树是平衡的则返回高度，如果不平衡则返回 -1
// 时间复杂度： O(N)，每个结点只访问一次
// 空间复杂度： O(N)，递归调用
func isBalanced1(root *TreeNode) bool {
	return height(root) > 0
}

func height1(root *TreeNode) int {
	if root == nil {
		return 0
	}
	left := height(root.Left)
	right := height(root.Right)
	// 左子树或者右子树不平衡则直接返回
	if left == -1 || right == -1 {
		return -1
	}
	if abs(left-right) > 1 {
		return -1
	}
	return max(left, right) + 1
}
