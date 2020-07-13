package main

import (
	"fmt"
)

func squares() func() int {
	var x int
	return func() int {
		x++
		return x * x
	}
}

func runSquares() {
	// call squares() function and return a function to f
	f := squares()
	fmt.Println(f()) // "1"
	fmt.Println(f()) // "4"
	fmt.Println(f()) // "9"
}
