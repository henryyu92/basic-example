func ch4

func appendIntTest(t *testing.T){
	var x, y []int
	for i := 0; i < 10; i++{
		y = appendInt(x, i)
		fmt.Printf("%d cap=%d\t%v\n", i, cap(y), y)
	}
}