package main

import (
	"bufio"
	"fmt"
	"os"
)

// 通过 key 作为索引下标来访问 map 将产生一个 value,
// 如果 key 在 map 中是存在的，那么将得到与 key 对应的 value；
// 如果 key 不存在，那么将得到 value 对应的零值

func dedup() {
	// a set of strings
	seen := make(map[string]bool)
	input := bufio.NewScanner(os.Stdin)
	for input.Scan() {
		line := input.Text()
		if !seen[line] {
			seen[line] = true
			fmt.Println(line)
		}
	}

	if err := input.Err(); err != nil {
		fmt.Fprintf(os.Stderr, "dedup: %v\n", err)
		os.Exit(1)
	}
}
