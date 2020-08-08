package start

import (
	"fmt"
	"os"
	"strings"
)

func echo1(){

	// var 声明变量时会初始化零值
	var s, sep string

	// os.Args 变量包含命令行参数，其中 Args[0] 表示命令本身，Args[1:] 表示命令行参数
	for i := 1; i < len(os.Args); i++ {
		s += sep + os.Args[i]
		sep = " "
	}
	fmt.Println(s)
}

func echo2(){
	s, sep := "", ""

	// os.Args 变量是 slice 类型，使用 range 可以遍历 slice
	// range 会产生 (index, value) 一对值，可以使用 _ 表示不需要使用这个变量
	for _, arg := range os.Args[1:] {
		s += sep + arg
		sep = " "
	}
	fmt.Println(s)
}

func echo3(){
	// 使用 += 会创建新的字符串,使用 strings.Join 函数可以拼接字符，内部使用 builder 实现
	fmt.Println(strings.Join(os.Args[1:], " "))
}

// todo  slice append 直接追加到最后
func echo4(){

	sep := " "

	buf := make([]string, 2 * len(os.Args[1:]) - 1)

	for _, arg := range os.Args[1:]{
		fmt.Println(arg)
		buf = append(buf, arg)
		fmt.Println(len(buf))
		buf = append(buf, sep)
	}

	fmt.Println(len(buf))

	fmt.Println(buf)
}