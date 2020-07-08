package main

//Celsius c
type Celsius float64

//Fahrenheit F
type Fahrenheit float64

const (
	//AbsoluteZeroC absolute zero
	AbsoluteZeroC Celsius = -273.15
	//FreezingC freezing
	FreezingC Celsius = 0
	//BoilingC boiling
	BoilingC Celsius = 100
)

func tempconv() {

}

//CToF Celsius to Fahrenheit
func CToF(c Celsius) Fahrenheit {
	return Fahrenheit(c*9/5 + 32)
}

//FToC Fahrenheit to Celsius
func FToC(f Fahrenheit) Celsius {
	return Celsius((f - 32) * 5 / 9)
}
