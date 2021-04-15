package ds

// 每个 Node 表示有多层
type SkipListNode struct {
	value int64
	nextNodes []SkipListNode
}

const PROBABILITY  = 0.5

type SkipList struct {
	// head 的 level 就是整个跳跃表的 level
	head *SkipList
	maxLevel int
	size int
}