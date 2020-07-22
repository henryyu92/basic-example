package leetcode

import (
	"fmt"
	"testing"
)

var nums []int
var target int

func init() {
	nums = []int{2, 7, 11, 15}
	target = 9
}

func TestBruteForce(t *testing.T) {

	fmt.Println(bruteForce(nums, target))
}

func TestTwoPassHash(t *testing.T) {

	fmt.Println(twoPassHash(nums, target))
}

func TestOnePassHash(t *testing.T) {

	fmt.Println(onePassHash(nums, target))
}
