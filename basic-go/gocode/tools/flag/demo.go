package main

import (
	"flag"
	"log"
	"os"
)

func main() {
	// cmd()
	subCmd()
}

func cmd() {
	var name string

	// 命令行参数支持三种语法：
	// -flag   	仅支持 bool 类型
	// -flag x	仅支持非 bool 类型
	// -flag=x	支持所有数据类型

	flag.StringVar(&name, "name", "Go Flag Demo", "usage -name=value")
	flag.StringVar(&name, "n", "Go Flag Demo", "usage -n=value")

	flag.Parse()

	log.Printf("name: %s", name)
}

func subCmd() {
	var name string

	// 创建子命令
	goCmd := flag.NewFlagSet("go", flag.ExitOnError)
	goCmd.StringVar(&name, "name", "default value", "help info")

	javaCmd := flag.NewFlagSet("java", flag.ExitOnError)
	javaCmd.StringVar(&name, "n", "default value", "help info")

	args := os.Args[1:]
	switch args[0] {
	case "go":
		_ = goCmd.Parse(args[1:])
	case "java":
		_ = javaCmd.Parse(args[1:])
	}

	log.Printf("name: %s", name)
}
