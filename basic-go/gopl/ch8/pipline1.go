package ch8

import (
	"fmt"
)

func pipline() {
	naturals := make(chan int)
	squares := make(chan int)

	// Counter
	go func() {
		for x := 0; ; x++ {
			naturals <- x
		}
	}()

	// Squarer
	go func() {
		for {
			x := <-naturals
			squares <- x
		}
	}()

	// Printer
	for {
		fmt.Println(<-squares)
	}
}
