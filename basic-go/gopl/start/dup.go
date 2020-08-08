package start

import (
	"bufio"
	"fmt"
	"io/ioutil"
	"os"
	"strings"
)

func dup1(){

	// map 类型需要使用 make 创建
	counts := make(map[string]int)
	// 从标准输入中获取
	input := bufio.NewScanner(os.Stdin)
	for input.Scan(){
		// map 是引用类型
		counts[input.Text()]++
	}
	// map 使用 range 遍历得到 (key, value) 的值，map 的遍历是随机的
	// go 中 map 在遍历时删除是安全的
	for line, n := range counts{
		if n > 1 {
			fmt.Printf("%d\t%s\n", n, line)
		}
	}
}

func dup2(){
	counts := make(map[string]int)
	files := os.Args[1:]
	if len(files) == 0 {
		countLines(os.Stdin, counts)
	}else {
		for _, file := range files{
			f, err := os.Open(file)
			if err != nil {
				fmt.Fprintf(os.Stderr, "dup2: %v\n", err)
				continue
			}
			// map 是引用类型，作为形参时传递的是引用的拷贝
			countLines(f, counts)
			f.Close()
		}
	}
}

func countLines(f *os.File, counts map[string]int){
	input := bufio.NewScanner(f)
	for input.Scan() {
		counts[input.Text()]++
	}
}

func dup3(){
	counts := make(map[string]int)
	for _, filename := range os.Args[1:]{
		data, err := ioutil.ReadFile(filename)
		if err != nil {
			fmt.Fprintf(os.Stderr, "dup3: %v\n", err)
			continue
		}
		for _, line := range strings.Split(string(data), "\n"){
			counts[line]++
		}
		for line, n := range counts{
			if n > 1{
				fmt.Printf("%d\t%s\n", n, line)
			}
		}
	}
}
