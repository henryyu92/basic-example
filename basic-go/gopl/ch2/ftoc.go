package main

import (
	"fmt"
)

func ftoc() {
	const freezingF, boilingF = 32.0, 212.0
	fmt.Printf("%g F = %g C", freezingF, fToC(freezingF))
	fmt.Printf("%g F = %g C", boilingF, fToC(boilingF))
}

func fToC(f float64) float64 {
	return (f - 32) * 5 / 9
}
