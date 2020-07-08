package main

import (
	"fmt"
	"sort"
)

var prereqs = map[string][]string{
	"algorithms":            {"data structures"},
	"calculus":              {"linear algebra"},
	"compilers":             {"data structures", "formal languages", "computer organization"},
	"data structures":       {"discrete math"},
	"databases":             {"data structures"},
	"discrete math":         {"intro to programming"},
	"formal languages":      {"discrete math"},
	"networks":              {"operation systems"},
	"operation systems":     {"data structures", "computer organization"},
	"programming languages": {"data structures", "computer organization"},
}

func topoSort() {
	var order []string
	seen := make(map[string]bool)
	var visitAll func(items []string)
	visitAll = func(items []string) {
		for _, item := range items {
			if !seen[item] {
				seen[item] = true
				visitAll(prereqs[item])
				order = append(order, item)
			}
		}
	}

	var keys []string
	for key := range prereqs {
		keys = append(keys, key)
	}
	sort.Strings(keys)
	fmt.Printf("%v\n", keys)
	visitAll(keys)

	for i, course := range order {
		fmt.Printf("%d:\t%s\n", i+1, course)
	}
}
