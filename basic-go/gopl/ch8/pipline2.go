package ch8

import "fmt"

func pipline2() {
	naturals := make(chan int)
	squares := make(chan int)

	// Counter
	go func() {
		for x := 0; x < 100; x++ {
			naturals <- x
		}
		close(naturals)
	}()

	// Squarer
	go func() {
		for x := range naturals {
			squares <- x
		}
		close(squares)
	}()

	// Printer
	for {
		fmt.Println(<-squares)
	}
}
