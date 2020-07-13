package main

import (
	"fmt"
	"math"
)

// Point refer to a point use x and y
type Point struct {
	X, Y float64
}

// Distance compute traditional functions
func Distance(p, q Point) float64 {
	return math.Hypot(q.X-p.Y, q.Y-p.Y)
}

// Distance same thing, but as a method of the Point type
func (p Point) Distance(q Point) float64 {
	return math.Hypot(q.X-p.X, q.Y-p.Y)
}

func (p Point) change() {
	p.X++
	p.Y++
	fmt.Printf("%f, %f\n", p.X, p.Y)
}
