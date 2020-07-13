package main

import (
	"fmt"
)

// Point contains x and y coordinate
type Point struct {
	X, Y int
}

// Circle contains center and radius
type Circle struct {
	Point
	Radius int
}

// Wheel contains circle and spokers
type Wheel struct {
	Circle
	Spokes int
}

func embed() {
	w := Wheel{
		Circle: Circle{
			Point:  Point{X: 8, Y: 8},
			Radius: 5,
		},
		Spokes: 20,
	}
	// Wheel{Circle:Circle{Point:Point{X:8, Y:8}, Radius:5}, Spokes:20}
	fmt.Printf("%#v\n", w)
}
