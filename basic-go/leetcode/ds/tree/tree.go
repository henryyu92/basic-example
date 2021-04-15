package tree

import (
	"fmt"
	"goAlgorithm/algorithm/ds"
)

type (
	Tree struct {
		root *node
	}
	node struct {
		value ds.Element
		left  *node
		right *node
	}
)

// 递归前序遍历：根 -> 左 -> 右
func (tree *Tree) PreOrderRecur() {
	if tree == nil || tree.root == nil {
		return
	}

	fmt.Printf("%d, ", tree.root.value)

	left := tree.root.left
	right := tree.root.right

	// 遍历左子树
	tree.root = left
	tree.PreOrderRecur()

	// 遍历右子树
	tree.root = right
	tree.PreOrderRecur()
}

// 非递归前序遍历： 根 -> 左 -> 右
func (tree *Tree) PreOrderNonRecur() {
}
