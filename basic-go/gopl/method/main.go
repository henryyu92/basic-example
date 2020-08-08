package main

import (
	"fmt"
)

func main() {

	var point = Point{X: 1, Y: 2}
	// X:1,Y:2
	fmt.Printf("%f-%f\n", point.X, point.Y)
	point.change()
	// X:1, Y:2
	fmt.Printf("%f-%f\n", point.X, point.Y)
}
