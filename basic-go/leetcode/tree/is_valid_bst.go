package tree

import "math"

// 给定二叉树，判断是否是一个有效的二叉搜索树
// 二叉搜索树特征：
// 	1. 节点的左子树只包含小于当前节点的数
//	2. 节点的右子树只包含大于当前节点的数
//	3. 所有左子树和右子树本身必须也是二叉搜索树

// 递归： 左子树所有节点小于当前节点 && 右子树所有节点大于当前节点
func isValidBST(root *TreeNode) bool {
	return helper(root, math.MinInt64, math.MaxInt64)
}

func helper(root *TreeNode, min, max int) bool {
	if root == nil {
		return true
	}
	if root.Val <= min || root.Val >= max {
		return false
	}
	return helper(root.Left, min, root.Val) && helper(root.Right, root.Val, max)
}
