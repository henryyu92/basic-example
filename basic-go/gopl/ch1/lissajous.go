package ch1

import (
	"image"
	"image/color"
	"image/gif"
	"io"
	"math"
	"math/rand"
)

var palette = []color.Color{color.White, color.Black}

const (
	// first color in palette
	whiteIndex = 0
	// next color in palette
	blackIndex = 1
)

func lissajous(out io.Writer) {
	const (
		// number of complete x oscillator revolutions
		cycles = 5
		// angular resolution
		res = 0.001
		// image canvas covers [-size, size]
		size = 100
		// number of animation frames
		nframes = 64
		// delay between frames in 10ms units
		delay = 8
	)

	// relative frequency of y oscillator
	freq := rand.Float64() * 0.3
	anim := gif.GIF{LoopCount: nframes}
	// phase difference
	phase := 0.0
	rect := image.Rect(0, 0, 2*size+1, 2*size+1)

	for i := 0; i < nframes; i++ {
		img := image.NewPaletted(rect, palette)
		for t := 0.0; t < cycles*2*math.Pi; t += res {
			x := math.Sin(t)
			y := math.Sin(t*freq + phase)
			img.SetColorIndex(size+int(x*size+0.5), size+int(y*size+0.5), blackIndex)
		}
		phase += 0.1
		anim.Delay = append(anim.Delay, delay)
		anim.Image = append(anim.Image, img)
	}
	gif.EncodeAll(out, &anim)
}
