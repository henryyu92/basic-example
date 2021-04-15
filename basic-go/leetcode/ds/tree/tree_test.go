package tree

import (
	"math/rand"
	"testing"
)

func (tree *Tree) testPreOrderRecur(t *testing.T){

	root := &node{
		left:nil,
		right:nil,
		value:rand.Int(),
	}

	tree.root = root

}

