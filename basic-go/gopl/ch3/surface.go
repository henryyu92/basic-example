package main

import (
	"fmt"
	"math"
)

const (
	// canvas size in pixels
	width, height = 600, 320
	// number of grid cells
	cells = 100
	// axis ranges (-xyrange, +xyrange)
	xyrange = 30.0
	// pixels per x or y unit
	xyscale = width / 2 / xyrange
	// pixels per z unit
	zscale = height * 0.4
	// angle of x, y axes(=30)
	angle = math.Pi / 6
)

var sin30, cons30 = math.Sin(angle), math.Cos(angle)

func surface() {
	fmt.Printf("<svg xmlns='http://www.w3.org/2000/svg' style='stroke:grey;fill:white;stroke-width:0.7' width:'%d' height:'%d'>", width, height)

	for i := 0; i < cells; i++ {
		for j := 0; j < cells; j++ {
			ax, ay := corner(i+1, j)
			bx, by := corner(i, j)
			cx, cy := corner(i, j+1)
			dx, dy := corner(i+1, j+1)
			fmt.Printf("<polygon points='%g,%g %g,%g %g,%g %g,%g'/>\n", ax, ay, bx, by, cx, cy, dx, dy)
		}
	}
	fmt.Printf("</svg>")
}

func corner(i, j int) (float64, float64) {
	// Find point (x, y) at corner of cell (i, j)
	x := xyrange * (float64(i)/cells - 0.5)
	y := xyrange * (float64(j)/cells - 0.5)
	// Compute surface height z
	z := f(x, y)
	// Project (x, y, z) isometrically onto 2-D SVG canvas (sx, xy)
	sx := width/2 + (x-y)*cons30*xyscale
	sy := height/2 + (x-y)*sin30*xyscale - z*zscale

	return sx, sy
}

func f(x, y float64) float64 {
	r := math.Hypot(x, y)
	return math.Sin(r) / r
}
