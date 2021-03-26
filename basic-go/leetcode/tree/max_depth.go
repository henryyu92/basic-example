package tree

// 给定二叉树，找出最大深度

// 深度优先搜索
// 递归：左子树和右子树的最大深度的最大值 + 1
// 时间复杂度： O(N)， 每个节点都访问到
// 空间复杂度： O(h)，递归的栈空间最多存储 h 个元素，h 表示二叉树的高度
func maxDepth(root *TreeNode) int {
	if root == nil {
		return 0
	}
	left := maxDepth(root.Left)
	right := maxDepth(root.Right)
	if left > right {
		return left + 1
	} else {
		return right + 1
	}
}

// 广度优先搜索： 队列中存放的当前层的所有节点，并且每次将当前层的所有节点取出，然后获取下一层的节点放入队列，直到下一层没有元素，遍历的层数即为最大深度
func maxDepthBFS(root *TreeNode) int {

	if root == nil {
		return 0
	}
	// 广度优先搜索需要定义一个队列
	queue := []*TreeNode{}
	queue = append(queue, root)
	ans := 0
	for len(queue) > 0 {
		sz := len(queue)
		// 将当前层的所有节点取出
		for sz > 0 {
			node := queue[0]
			if node.Left != nil {
				queue = append(queue, node.Left)
			}
			if node.Right != nil {
				queue = append(queue, node.Right)
			}
			sz--
		}
		ans++
	}
	return ans
}
