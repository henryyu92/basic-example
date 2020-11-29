## 程序结构

Go 语言主要有四种类型的声明语句：
- ```var```：声明变量， 变量在声明时会初始化为零值，因此 Go 语言中不存在未初始化的变量；包级别的变量声明会在 main 入口函数执行前完成初始化，局部变量在声明语句执行时初始化
- ```const```：声明常量，常量表达式的值在编译期计算，而不是在运行期，常量间的运算结果也是常量，在编译期完成
- ```type```：声明类型，类型声明语句创建了一个新的类型名称，和现有类型具有相同的底层结构；对每一个类型 T，都有一个对应的类型转换操作 T(x)，用于将 x 转换为 T 类型，只有两个类型的底层类型相同才能转换
- ```func```：声明函数

### 变量

### 包
Go语言的代码通过包（package）组织，一个包由位于单个目录下的一个或多个.go源代码文件组成, 目录定义包的作用。

每个源文件都以一条package声明语句开始，表示该文件属于哪个包，紧跟着一系列导入（import）的包，之后是存储在这个文件里的程序语句。

## 数据类型

字符串格式化：
%d      十进制整数
%x, %o, %b      十六进制、八进制、二进制整数
%f, %g, %e      浮点数
%t      布尔值
%c      字符
%s, %q      字符串
%v      自然形式
%T      类型


### 原生类型

### 复合类型


## 程序控制

### 条件



### 循环

Go语言只有 for 循环这一种循环语句， for 循环有多种形式，最基本的形式为：
```go
for initialization; condition; post {
    // zero or more statements
}
```
其中 `initialization` 是初始值；`condition` 是执行条件，只有满足条件才能执行循环体；`post` 是在循环体执行结束后执行。

`initialization` 省略则表示初始值在 for 语句之前存在；`post` 省略则表示循环体执行后不需要后续处理；`condition` 省略则表示 for 循环是无限循环。
```go
// a traditional "while" loop
for condition {
    // ...
}

// a traditional infinite loop
for {
    // ...
}
```




### 包
Go 语言编译器的编译速度明显快于其他编译语言，主要得益于三个语言特性：
- 所有导入的包必须在每个文件开头显式声明，编译器无需读取和分析整个源文件来判断包的依赖关系
- 禁止包的环状依赖，包的依赖关系形成一个有向无环图，每个包可以被独立编译，而且可以被并发编译
- 编译后包的目标文件不仅仅记录包本身的导出信息，目标文件同时还记录了包的依赖关系，减少重复的间接依赖包导入

包是由一个全局唯一的字符串所标识的导入路径定位。每个 Go 语言源文件的开头都必须有包声明语句，包声明语句用于确定当前包被其他包导入时的默认包名。默认的包名是包导入路径名的最后一段，因此存在不同导入路径的包有相同的包名。

main 包对应一个可执行程序，使用 ```go build``` 构建命令编译完之后必须调用连接器生成一个可执行程序。

包所在目录中存在 test.go 为后缀的 Go 源文件，并且这些源文件声明的包名也是以 ```_test``` 为后缀的，这种目录可以除了可以包含普通包外还可以包含测试的外部扩展包，所有以 _test 为后缀的包名的测试外部扩展包都由 ```go test``` 命令独立编译。普通包和测试的外部扩展包是相互独立的。

包导入路径后有版本号信息的时候，默认的包的名字不包含版本号，如 ```gopkg.in/yaml.v2``` 这个包默认的包名为 yaml。

包的导入声明使用 import 语法，每个导入声明可以单独指定一个导入路径，也可以通过圆括号同时导入多个导入路径。
```go
import "fmt"
import "os"

import(
    "fmt"
    "os"
)
```
导入的包之间可以通过添加空行来分组，通常将来自不同组织的包独立分组：
```go
import(
    "fmt"
    "os"

    "golang.org/x/nex/html"
    "golang.org/x/nex/ipv4"
)
```
如果导入的两个包名字相同则会冲突，可以重命名包来避免冲突，导入包的重命名只影响当前的源文件，其他源文件不受影响：
```go
import(
    "crypto/rand"
    mrand "math/rand"
)
```
如果只是导入一个包而并不使用导入的包则会导致编译错误，使用 _ 来重命名导入的包称为包的匿名导入。使用包的匿名导入可以在在不发生编译错误的情况下使用包级变量和包的 init 初始化函数。
```go
package main

import (
    "fmt"
    "image"
    "image/jpeg"
    _ "image/png" // register PNG decoder
    "io"
    "os"
)

func main() {
    if err := toJPEG(os.Stdin, os.Stdout); err != nil {
        fmt.Fprintf(os.Stderr, "jpeg: %v\n", err)
        os.Exit(1)
    }
}

// 将 PNG 输入图像转换为 JPEG 图像输出 
func toJPEG(in io.Reader, out io.Writer) error {
    img, kind, err := image.Decode(in)
    if err != nil {
        return err
    }
    fmt.Fprintln(os.Stderr, "Input format =", kind)
    return jpeg.Encode(out, img, &jpeg.Options{Quality: 95})
}
```


包的初始化：
- 初始化包级变量；
- init() 函数；一个文件中可以有多个 init 函数，且 init 函数不能被调用或者引用
- main

new 函数创建变量：new(T) 创建 T 类型的匿名变量，初始化为 T 类型的零值，返回 *T 类型的指针；new 函数每次调用都是返回一个新的地址；

### 命令行
Go 语言的工具箱集合了一系列的功能的命令集，使用 ```go``` 或者 ```go help``` 命令可以查看内置的帮助文档。


## 类型
Go语言内置的数据类型分为四类：基础类型、复合类型、引用类型和接口类型。其中基础类型包括数字、字符串和布尔类型，复合数据类型包括数组和结构体，引用类型包括指针、切片、字典、函数、通道。

除了 Go 语言内置的数据类型，还可以通过类型声明语句创建新的数据类型，用于隔离具有相同底层类型但有不同概念的类型，使得它们即使有相同的底层类型也是不兼容的
```go
type bigint int64
```
不同类型的值不能比较或者运算，Go 语言不支持隐式的类型转换，必须使用 ```T(x)``` 形式的显式类型转换。只有两个类型的底层类型相同，或者两者都是指向相同底层结构的指针类型时才允许类型转换操作，类型转换的错误发生在编译期。
```go
var a int64 = 64
var x bigint = bigint(a)    // bigint 是新的类型，所以必须显式类型转换

var b int32 = 32
var y int64 = int64(b)  // 数字类型是可以互相转换，但是有可能会丧失精度
```
除了通过声明语句创建新的类型，还可以通过类型别名为类型创建一个别名。类型别名只是一个别名，并没有创建新的类型，其本质还是底层类型。
```go
type byte = int8
type rune = int32

var a int32 = 32
var x rune = a  // rune 类型本质是 int32，所以无需类型转换
```
### 数值类型
Go 语言的数值类型包括整数、浮点数和复数，每种数值类型都有对应的大小范围和是否支持正负符号。Go 语言的数值类型的零值是 0。

#### 整数
Go 语言同时提供了有符号和无符号整数类型，不同整数类型之间即使可以表示相同的数值，在需要赋值时也需要显式的类型转换：

|整数类型|bit 数(字节数)|取值范围|零值|说明|
|-|-|-|-|-|
|int8|8(1)|
|unint8|8(1)||
|int16|16(2)||
|unint16|16(2)||
|int32|32(4)||
|uint32|32(4)||
|int64|64(8)||
|uint64|64(8)||
|int|32(4)/64(8)||
|uint|32(4)/64(8)||
|byte|32(4)||
|rune|32(4)||
|uintptr|||

任何大小整数字面值都可以用以 0 开始的八进制格式书写，或者用以 0x 或 0X 开头的十六进制格式书写：
```go
fmt.Printf("%016b\n", 0666)
fmt.Printf("%032b\n", 0xdeadbeef)
```

#### 浮点数
Go 语言提供了两种精度的浮点数类型：float32 和 float64，其范围极限可以在 math 包中找到。float32 类型的浮点数可以提供大约6个十进制数的精度，float64 类型则可以提供约15个十进制数的精度，通常应该优先使用 float64 类型避免由于精度问题导致的累计误差扩散太快。

浮点数支持科学计数法的形式，通过 e 或者 E 指定指数部分：
```go
const Avogadro = 6.02214129e23  // 阿伏伽德罗常数
const Planck   = 6.62606957e-34 // 普朗克常数
```

#### 复数
Go 语言提供了 complex64 和 complex128 两种精度的复数类型，分别对应 float32 和 float64 两种精度。

内置的 complex 函数用于构造复数，real 和 imag 函数用于获取复数的实部和虚部
```go
var x complex128 = complex(1,2)     // 1+2i
var y complex128 = complex(3, 4)    // 3+4i
fmt.Println(x*y)                    // "(-5+10i)"
fmt.Println(real(x*y))              // "-5"
fmt.Println(imag(x*y))              // "10"
```
复数支持直接使用*整数后面直接加 i* 的形式直接构造虚部，math/cmplx 包提供了复数处理的许多函数：
```go
var minusOne = cmplx.Exp(math.Pi * -1i)  // Euler’s formula
```
### 布尔
布尔值只有 true 和 flase 两种，通常会在 if、for 或者 switch 中作为条件判断。布尔值不会隐式转换为 0 和 1，也不会由 0 和 1 转换而来，使用一个显式的 if 语句辅助转换：
```go
func btoi(b bool) int{
    if b {
        return 1
    }
    return 0
}

func itob(i int) bool{
    return i != 0
}
```
### 运算符
Go 语言中的二元运算符包括算术运算符、逻辑运算符和比较运算符，二元运算符有五种优先级，在同一个优先级使用左右先结合规则，使用括号可以明确优先顺序或者提升优先级。
```go
*   /   %   <<  >>  &   %^
+   -   |   ^
==  !=  <   <=  >   >=
&&
||
```
算术运算符可以和赋值结合用于简化赋值语句：
```go
var x = 1
x += 2
```
算术运算符 ```+```、```-```、```*``` 和 ```/``` 可以适用与整数、浮点数和复数，但是 ```%``` 仅用于整数间的运算，**Go 语言中 ```%``` 运算符的符号总是和被取模数的符号一致**。
```go
fmt.Println(-5%3, -5%-3)    // "-2 -2"
```
算术运算溢出时，超出的高位 bit 部分将被丢弃，如果原始的数值是有符号类型，那么在计算溢出时有可能导致正数计算结果为负数：
```go
var u uint8 = 255
fmt.Println(u, u+1, u*u)    // "255 0 1"

var i int8 = 127
fmt.Println(i, i+1, i*i)    // "127 -128 1"
```
运算符 ```+```、```-``` 可作为一元运算符表示正数和负数，而位运算符 ```^``` 作为一元运算符表示按位取反。
```go

```
位运算符 ```^``` 作为二元运算符表示按位异或(XOR)，位运算符 ```&^``` 表示按位清零(AND NOT)：
```go
var x uint8 = 1<<1 | 1<<5
var y uint8 = 1<<1 | 1<<2

fmt.Println("08b\n", x)     // "00100010"
fmt.Printf("%08b\n", y)     // "00000110"

fmt.Printf("%08b\n", x&y)   // "00000010"
fmt.Printf("%08b\n", x|y)   // "00100110"
fmt.Printf("%08b\n", x^y)   // "00100100"
fmt.Printf("%08b\n", x&^y)  // "00100000", y 对应位为 1 则清零，否则为 x 对应位的值
```
位移运算符可以作用于有符号数和无符号数上，算术上 ```x<<n``` 运算等价于乘以 ```2^n```，```x>>n``` 运算等价于除以 ```2^n```。需要注意**无符号数右移运算是用 0 填充空缺位，但是有符号数右移运算是用 1 填充空缺位**。

算术和逻辑运算的二元操作中必须是相同的类型，否则会编译异常。如果类型之间可以允许类型转换，则通过类型转换可以解决编译异常问题，但是需要注意类型转换带来的精度丢失问题。

逻辑运算符具有短路行为：如果运算符左边值已经可以确定整个布尔表达式的值，那么运算符右边的值将不再被求值：
```go
s != "" && s[0] == 'x'  // s 为 "" 时使用 s[0] 会导致 panic 异常，但是由于短路行为使得 s 为 "" 时 s[0] 不会被求值
```
逻辑运算符 ```&&``` 的优先级高于 ```||```，因此在一些情况下可以省去不必要的括号：
```go
if 'a' <= c && c <= 'z' ||
    'A' <= c && c <= 'Z' ||
    '0' <= c && c <= '9' {
    // ...ASCII letter or digit...
}
```
### 字符串
字符串是一个不可改变的字节序列，可以存放任意的数据，存储的字节是采用 UTF8 编码的字符对应的 Unicode 码点。

Go 语言内置的 ```len``` 函数返回字符串的字节数，字符串支持的索引操作也是对字节的索引，超出索引范围会导致 panic 异常
```go
s := "hello, world"
fmt.Println(len(s))     // "12"
fmt.Println(s[0], s[7]) // "104 119" ('h' and 'w')

c := s[len(s)] // panic: index out of range
```
字符串支持切片操作 ```s[i:j]```，表示基于字符串 s 的第 i 个字节(省略表示 0)到第 j 个字节(不包含第 j 个字节，省略表示 len(s))生成一个新的字符串， 生成的新字符串长度为 j-i 个字节。*切片生成的字符串和源字符串公用底层的字节序列*：
```

+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
|...| h | e | l | l | o | , |   | w | o | r | l | d |...|
+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
      ^^                          ^
      ||-------------|            |
s     |        hello |      world |
+-----|--+       +---|----+   +---|-----+
| data:  |       | data:  |   | data:   |
+--------+       +--------+   +---------+
| len: 12|       | len: 5 |   | len: 5  |
+--------+       +--------+   +---------+
```
```go
s := "hello world"
hello := s[:5]
world := s[7:]
```
字符串时不可变的字节序列，因此在字符串上的所有操作都是创建一个新的字符串，并且任何试图修改字符串的操作都是不允许的，会导致编译异常。字符串的不可变性使得其支持比较运算符，*字符串之间的比较是通过逐个字节比较的，因此比较的结果是字符串自然编码的顺序*。
```go
s := "left foot"
t := s
s += ", right foot"

fmt.Printf("%s\t%s\n", t, s)    // "left foot	left foot, right foot"
s[0] = 'L' // compile error: cannot assign to s[0]
```
Go 语言字符串使用 UTF8 编码的方式处理，因此可以在字符串字面值中使用以 ```\``` 开头的转义序列插入任意数据，也可以以 ```\xhh```(两位十六进制数) 或 ```ooo```(三位八进制数) 的形式通过十六进制或八进制转义在字符串面值中包含任意字节，还可以以 ```uhhhh``` 或 ```Uhhhhhhhh``` 的 Unicode 码点值形式包含在字符串面值中：
```go
fmt.Println("\141\x61,hello world")     // "aa,hello world"

fmt.Println("\u4e16\u754c")
fmt.Println("\U00004e16\U0000754c")
```
原生字符串使用 ``` ` ` ```(反引号) 代替双引号，在原生字符串中所有的内容都是字面值，不存在任何转义。原生字符串面值用于编写正则表达式会很方便，因为正则表达式往往会包含很多反斜杠。原生字符串面值同时被广泛应用于HTML模板、JSON面值、命令行提示信息以及那些需要扩展到多行的场景
```go
const GoUsage = `Go is a tool for managing Go source code.

Usage:
    go command [arguments]
...`
```
UTF8 是一个将 Unicode 码点编码为字节序列的变长编码，UTF8编码使用 1 到 4 个字节来表示每个Unicode码点，每个符号编码后第一个字节的高端bit位用于表示总共有多少编码个字节。
```
0xxxxxxx                                一个字节
110xxxxx 10xxxxxx                       两个字节
1110xxxx 10xxxxxx 10xxxxxx              三个字节
11110xxx 10xxxxxx 10xxxxxx 10xxxxxx     四个字节
```
当字符串中包含非 ASCII 码字符时，字符串切片操作和基于 len 遍历的操作的结果会超出实际的预想，而采用 range 方式遍历字符串则会遍历出每个字符
```go
a := "Hello, 世界"
for i, r := range a {
    fmt.Printf("%d\t%[2]q\t%[2]d\n", i, r)
}

for i, r := 0, len(a); i< r; i++ {
    fmt.Printf("%d\t%[2]q\t%[2]d\n", i, a[i])
}

for i := 0; i < len(a); {
    r, size := utf8.DecodeRuneInString(a[i:])
    fmt.Printf("%d\t%c\n", i, r)
    i += size
}
```
字符串可以和 ```[]rune``` 类型互相转换：将字符串转换为 ```[]rune``` 类型将返回字符串编码的 Unicode 码点序列；将 ```[]rune``` 类型的 Unicode 字符 slice 或数组转为字符串，则对它们进行UTF8编码：
```go
s := "プログラム"
fmt.Printf("% x\n", s) // "e3 83 97 e3 83 ad e3 82 b0 e3 83 a9 e3 83 a0"
r := []rune(s)
fmt.Printf("%x\n", r)  // "[30d7 30ed 30b0 30e9 30e0]"
fmt.Println(string(r)) // "プログラム"
```
字符串和字节 slice 之间也可以相互转换，```[]byte(s)``` 转换是分配了一个新的字节数组用于保存字符串数据的拷贝，然后引用这个底层的字节数组，将一个字节slice转到字符串的 ```string(b)``` 操作则是构造一个字符串拷贝
```go
s := "abc"
b := []byte(s)
s2 := string(b)
```
**直接使用 string(x) 的方式将整数转换为字符串，则会生成整数对应的 Unicode 码点字符的字符串**；如果需要将整数转换为字符串需要使用 strconv.Itoa(x) 函数，同理将字符串转换为整数时需要使用 strconv.Atoi(s) 函数
```go
x := 123
y := strconv.Itoa(x)
x = strconv.Atoi(y)
```
标准库提供了 4 个字符串处理相关的比较重要的包：
- ```bytes```：主要包含字节处理相关功能，但是提供了字符串和字节相关的功能，如字节查询、字符拼接等功能
- ```strings```：提供了字符串的查询、替换、比较、截断、拆分、合并等功能
- ```strconv```：提供了布尔型、整型数、浮点数和对应字符串的相互转换，还提供了双引号转义相关的转换
- ```unicode```：用于给字符分类，每个函数有一个单一的rune类型的参数，然后返回一个布尔值
### 常量
常量表达式的值在编译期计算，而不是在运行期，每种常量的潜在类型都是基础类型。常量声明语法和变量声明语法类似，只是关键字为 const，常量的值不可修改，这样可以防止在运行期被意外或恶意的修改，比较适合声明数学常数：
```go
const IPv4Len = 4

const (
    E  = 2.71828182845904523536028747135266249775724709369995957496696763
    Pi = 3.14159265358979323846264338327950288419716939937510582097494459
)
```
*常量的运算都可以在编译期完成，方便编译优化*

常量间的所有算术运算、逻辑运算和比较运算的结果也是常量，对常量的类型转换操作或 ```len```、```cap```、```real```、```imag```、```complex``` 和 ```usafe.Sizeof``` 函数处理返回结果也是常量
```go

```
常量声明也包含类型和值，如果没有显式指明类型则根据值来推断类型。如果是批量声明常量，则处理第一个声明需要类型和值，后续的声明可以省略类型和值，其类型和值和和前面一样：
```go
const noDelay time.Duration = 0
const timeout = 5 * time.Minute
fmt.Printf("%T %[1]v\n", noDelay)     // "time.Duration 0"
fmt.Printf("%T %[1]v\n", timeout)     // "time.Duration 5m0s"

const (
    a = 1
    b
    c = 2
    d
)
fmt.Println(a, b, c, d) // "1 1 2 2"
```
常量声明可以使用 iota 常量生成器初始化，用于生成一组以相似规则初始化的常量，但是不用每行都写一遍初始化表达式。

在一个const声明语句中，在第一个声明的常量所在的行，iota将会被置为0，然后在每一个有常量声明的行加一
```go
type Weekday int

const (
    Sunday Weekday = iota       // 0
    Monday                      // 1
    Tuesday                     // 2
    Wednesday                   // 3
    Thursday                    // 4
    Friday                      // 5
    Saturday                    // 6
)

type Flags uint

const (
    FlagUp Flags = 1 << iota // is up
    FlagBroadcast            // supports broadcast access capability
    FlagLoopback             // is a loopback interfaces
    FlagPointToPoint         // belongs to a point-to-point link
    FlagMulticast            // supports multicast access capability
)

const (
    _ = 1 << (10 * iota)
    KiB // 1024
    MiB // 1048576
    GiB // 1073741824
    TiB // 1099511627776             (exceeds 1 << 32)
    PiB // 1125899906842624
    EiB // 1152921504606846976
    ZiB // 1180591620717411303424    (exceeds 1 << 64)
    YiB // 1208925819614629174706176
)
```
无类型常量指的是在声明时没有明确指定类型的常量。对于无类型常量，编译器会提供比基础类型更高的精度，即使其已经超过了所有基础类型的范围
```go
fmt.Println(YiB/ZiB) // "1024"
```
只有常量才可以是无类型的，无类型的常量被赋值给变量的时候，如果转换合法的话，无类型的常量就会被隐式转换为对应的类型。
### 格式化
### 数组
数组是一个由固定长度的特定类型元素组成的序列。数组的长度是固定的，需要在编译阶段确定，因此在声明时需要指定数组的长度。没有显式初始化时，数组的每个元素都被初始化为元素类型对应的零值。

数组的每个元素可以通过索引下标来访问，len 函数返回数组中元素的个数。使用 range 方式可用于遍历数组
```go
var a [3]int             // array of 3 integers
fmt.Println(a[0])        // print the first element
fmt.Println(a[len(a)-1]) // print the last element, a[2]

// Print the indices and elements.
for i, v := range a {
    fmt.Printf("%d %d\n", i, v)
}

// Print the elements only.
for _, v := range a {
    fmt.Printf("%d\n", v)
}
```
数组在声明时可以使用字面值初始化，同时也可以使用 ```...``` 来表示根据初始化值来计算数组的长度
```go
var q [3]int = [3]int{1, 2, 3}
var r [3]int = [3]int{1, 2}
var s [3]int = [3]int{
    1: 1,
    2: 2,
}
fmt.Println(r[2]) // "0"

q := [...]int{1, 2, 3}
fmt.Printf("%T\n", q) // "[3]int"
```
**数组的长度是数组类型的一个组成部分，不同长度的数组是不同的类型**，因此不同长度的数组不能直接类型转换。数组的长度必须是常量表达式，因为数组的长度需要在编译阶段确定。

如果数组类型相同且数组的元素是可比较的，那么数组是可以比较的。只有数组的所有元素相等时数组才是相等的
```go
a := [2]int{1, 2}
b := [...]int{1, 2}
c := [2]int{1, 3}
fmt.Println(a == b, a == c, b == c) // "true false false"
d := [3]int{1, 2}
fmt.Println(a == d) // compile error: cannot compare [2]int == [3]int
```
数组作为参数传递到函数中时，实际传递的时数组的一份拷贝，在函数中对数组参数的修改并不会影响原始的数组。显式的传入数组指针可以使函数内对数组的修改能够反映到原始数组
```go
func zero(ptr *[32]byte) {
    *ptr = [32]byte{}
}
```
### slice
slice 代表变长的序列，序列中每个元素都有相同的类型。slice 的声明和数组类似，只是没有固定长度。slice 底层引用一个数组对象。slice 的零值是 nil，其长度为 0，容量也为 0。

slice 由三个部分组成：指针、长度和容量。指针指向 slice 的第一个元素，slice 的第一个元素不一定是底层数组的第一个元素；容量是 slice 第一个元素到底层数组最后位置的长度；长度是 slice 中元素的数量，slice 的长度不能大于容量。

多个 slice 之间可以共享底层数组，并且引用的数组部分区间可能重叠。slice 的切片操作用于创建一个新的 slice，新的 slice 和源 slice 共享底层数组，如果切片操作超出 cap(s) 的上限将导致一个 panic 异常，但是超出 len(s) 则是意味着扩展了 slice：
```
months
+----+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+
| "" | "Jan" | "Feb" | "Mar" | "Apr" | "May" | "Jun" | "Jul" | "Aug" | "Sep" | "Oct" | "Nov" | "Dec" |
+----+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+
                                ^                 ^
                                |                 |
                            +---|----+        +---|----+
                            | data   |        | data   |
                            +--------+        +--------+
                            | len: 3 |        | len: 3 |
                            +--------+        +--------+
                            | cap: 9 |        | cap: 7 |
                            +--------+        +--------+
```
```go
Q2 := months[4:7]
summer := months[6:9]
fmt.Println(Q2)     // ["April" "May" "June"]
fmt.Println(summer) // ["June" "July" "August"]


fmt.Println(summer[:20]) // panic: out of range

endlessSummer := summer[:5] // extend a slice (within capacity)
fmt.Println(endlessSummer)  // "[June July August September October]"
```
内置的 make 函数可以创建一个指定元素类型、长度和容量的 slice。函数在底层创建了一个长度为指定容量的匿名数组，然后返回一个 slice，只有通过返回的slice才能引用底层匿名的数组变量。
```go
make([]T, len)
make([]T, len, cap) // same as make([]T, cap)[:len]
```
slice 包含底层数组的指针，因此将 slice 作为函数参数传递时可以在函数内部对底层数组进行修改
```go
func reverse(s []int) {
    for i, j := 0, len(s)-1; i < j; i, j = i+1, j-1 {
        s[i], s[j] = s[j], s[i]
    }
}

s := []int{0, 1, 2, 3, 4, 5}
// Rotate s left by two positions.
reverse(s[:2])
reverse(s[2:])
reverse(s)
fmt.Println(s) // "[2 3 4 5 0 1]"
```
slice 不能直接比较，因为 slice 的元素是间接引用的，底层数据元素的变化可能导致 slice 发生变化，因此需要展开每个元素进行比较。：
```go
func equal(x, y []string) bool{
    if len(x) != len(y){
        return false
    }
    for i := range x{
        if x[i] != y[i]{
            return false
        }
    }
    return true
}
```
slice 的零值是 nil，其没有底层数组，长度为 0，容量也为 0，但是长度为 0 并且容量为 0 的 slice 不一定是零值 nil，如 []int{}。可以使用 ```[]int(nil)``` 形式的类型转换来生成一个对应类型 slice 的零值
```go
var s []int    // len(s) == 0, s == nil
s = nil        // len(s) == 0, s == nil
s = []int(nil) // len(s) == 0, s == nil
s = []int{}    // len(s) == 0, s != nil
```
内置的 append 函数用于向 slice 中追加元素，每次调用 append 函数，必须先检测 slice 底层数组是否有足够的容量来保存新添加的元素。如果有足够空间的话，直接在原有的底层数组添加新元素并返回slice，追加前的 slice 和追加后的 slice 共享相同的底层数组；如果没有足够的空间的话，append 函数则会先分配一个足够大的 slice 用于保存新的结果，先将追加前的 slice 数组复制到新的 slice 数组中（使用 copy 函数），然后添加新元素，追加前的 slice 和追加后的 slice 引用的将是不同的底层数组

为了提高内存使用效率，通过在每次扩展数组时直接将长度翻倍从而避免了多次内存分配，也确保了添加单个元素操的平均时间是一个常数时间，通常是将append返回的结果直接赋值给输入的slice变量
```go
var y []int
for i := 0; i < 10; i++ {
    y = appendInt(y, i)
    fmt.Printf("%d cap=%d\t%v\n", i, cap(y), y)
}
```
内置的 copy 函数可以方便的将一个 slice 复制到另一个相同类型的 slice，两个 slice 共享同一个底层数据，copy 函数返回成功复制的元素的个数，因此不需要考虑复制超出目标 slice 的范围
```go
x := []int{1, 2, 3}
z := make([]int, 1, 2)
y := copy(z, x)
fmt.Println(y)
```
### map
map 就是一个哈希表的引用，map 类型可以写为 ```map[K]V```，其中K和V分别对应key和value。map 中所有的 key 都有相同的类型，所有的 value 也有着相同的类型，但是 key 和 value 之间可以是不同的数据类型。key 必须是支持 == 比较运算符的数据类型

内置的 make 函数可以创建一个 map，也可以用字面值的语法创建 map：
```go
ages := make(map[string]int)

ags := map[string]int{
    "alice": 31,
    "charlie": 34,
}
```
map 中的元素通过 key 对应的下标语句访问，通过内置的 delete 函数删除元素。在 map 上的查找和删除是安全的，即使 map 中不存在对应的 K-V，查找返回的是对应 value 类型的零值。map 通过 key 作为索引下标查询 value 时还会返回一个 bool 类型的值，用于表示查找的值是否存在：
```go
fmt.Println(ages["noKey"])  // "0"

delete(ages, "haha")

if age, ok := ages["bob"]; ok{
    fmt.Println(age)
}
```
```+=``` 和 ```++``` 等简短赋值语句可以用于 map。但是 map 中的元素并不是一个变量，不能对 map 的元素取地址操作，因为 map 可能随着元素数量的增长而重新分配更大的内存空间，从而可能导致之前的地址无效
```go
ages["bob"]++

_ = &ages["bob"] // compile error: cannot take address of map element
```
使用 range 风格的 for 循环可以遍历 map，但是 map 的迭代顺序是不确定的，且每次遍历的顺序都是不同的。

map 的零值是 nil，在其上的查找、delete、len、range 都能和在空 map 上运行一样，但是不能向 nil 的 map 中存入数据

和 slice 一样，map 之间不能进行比较，要判断两个 map 是否包含相同的 key 和 value 必须通过循环实现：
```go
func equal(x, y map[string]int) bool{
    if len(x) != len(y) {
        return false
    }
    for k, xv := range x{
        if yv, ok := y[k]; !ok || xv != yv {
            return false
        }
    }
    return true
}
```
### struct
结构体是一种聚合的数据类型，是由零个或多个任意类型的值聚合成的实体，每个值称为结构体的成员。

结构体成员变量既可以通过 ```变量名.成员变量名``` 方式或者通过 ```变量指针.成员变量名``` 方式访问。如果结构体成员名字是以大写字母开头的，那么该成员就是导出的，非导出的成员不能在外部包中直接访问。

结构体成员的顺序不同定义了不同的结构体类型。
```go
type A struct{
    a, b int
}

type B struct{
    b, a int
}

```

结构体类型不能包含相同类型的成员，但是可以包含相同类型的指针类型的成员，这可以创建递归的数据结构，如链表和树结构。
```go
type tree struct{
    value int
    left, right *tree
}
```

**结构体类型的零值是每个成员都是零值**。没有任何成员的结构体是空结构体，记作 ```struct{}```，其不包含任何信息，大小为 0。

使用结构体字面值可以创建新的结构体值，结构体字面值可以指定每个成员的值也可以指定部分成员的值，但是两种形式不能混用，并且不能在外部包中初始化非导出的成员：
```go
type Point{X, Y int}

p := Point{1, 2}

anim := gif.GIF{LoopCount: nframes}
```

结构体可以作为函数的参数和返回值，需要注意函数的参数和返回值都是值拷贝，也就是会拷贝结构体中的所有成员，且函数内对结构体的改动不会影响原始变量。如果要在函数内部修改结构体成员的话，用指针传入是必须的。
```go
func AwardAnnualRaise(e *Employee){
    e.Salary = e.Salary * 105 / 100
}

AwardAnnualRaise(&Employee{Salary: 100.0})
```

**如果结构体的全部成员都是可以比较的，那么结构体也是可以比较的**，比较运算符将比较两个结构体的每个成员。可比较的结构体变量可作为 map 的 key
```go
type Point struct{ X, Y int }

p := Point{1, 2}
q := Point{2, 1}
fmt.Println(p.X == q.X && p.Y == q.Y) // "false"
fmt.Println(p == q)                   // "false"
```

Go语言可以在结构体中只声明成员对应的数据类型而不指明成员名字的匿名成员。匿名成员的数据类型必须是命名的类型或指向一个命名的类型的指针，匿名成员的名字就是命名的类型名字。
```go
type Point struct{
    X, Y int
}

type Circle struct{
    Point
    Radius int
}

type Wheel struct{
    Circle
    Spokes int
}
```
结构体字面值并没有简短表示匿名成员的语法，必须遵循形状类型声明时的结构
```go
w := Wheel{
    Circle: Circle{
        Point: Point{
            X: x,
            Y: y,
        },
    },
    Spokes: 20,
}
```
因为匿名成员也有一个隐式的名字，因此不能同时包含两个类型相同的匿名成员，这会导致名字冲突。同时因为成员的名字是由其类型隐式地决定的，所有匿名成员也有可见性的规则约束。
           
外层的结构体不仅仅是获得了匿名成员类型的所有成员，而且也获得了该类型导出的全部的方法，这个机制可以用于将一个有简单行为的对象组合成有复杂行为的对象，是 Go 语言中面向对象的核心。

## 函数
函数将一个语句序列打包为一个单元，可以从程序中其他地方多次调用。函数将大任务分解为小任务，隐藏了实现细节。

函数声明包括函数名、形参列表、返回值列表(可省略) 和函数体。形参列表描述了函数的参数名以及参数类型，返回值列表描述了函数返回值的变量名以及类型，如果函数没有返回值则可以省略返回值列表。**没有函数体的函数声明表示该函数不是以Go实现的**：
```go
func name(parameter-list) (result-list){
    body
}
```
函数定义时参数列表可以不指定参数名，默认和参数类型同名
```go
var testHookServerServe func(*Server, net.Listener)
```
如果一组形参或返回值有相同的类型，可以不必为每个形参写出参数类型。
```go
func f(i, j, k int, s, t string){ /* ... */}
```
函数返回值可以像形数一样被命名，这时每个返回值被声明成一个局部变量，并根据该返回值的类型将其初始化为零值。如果声明函数包含返回值列表则必须使用 return 语句结尾，除非在函数中调用了 panic 异常。
```go
func sub(x, y int)(z int){
    z = x - y
    return
}
```
如果两个函数形参列表和返回值列表中的变量类型一一对应，那么这两个函数被认为有相同的类型。
```go
func a(s, t string)(x, y string){
	return
}

func b(s, t string)(x, y string){
	return
}

A := a
B := b
// "func(string, string) (string, string)	func(string, string) (string, string)"
fmt.Printf("%T\t%T\n", A, B)    
```
每一次函数调用都必须按照声明顺序为所有参数提供实参，Go语言没有默认参数值，也不可以通过参数名指定形参。

函数的形参和返回值在函数体中作为局部变量，存储在相同的词法块中，因此形参和返回之的变量名对于函数调用者而言没有意义。

函数是可以递归的，即函数可以直接或间接的调用自身。函数调用时将函数参数和返回值等数据作为栈帧压入栈中，Go语言使用可变栈，栈的大小按需增加(初始时很小)，可以避免栈溢出(递归调用时经常发生)和安全性问题。

**实参通过值的方式传递，因此函数的形参是实参的拷贝，对形参进行修改不会影响实参。对于引用参数，拷贝的是引用，通过引用可以对被引用的数据进行修改**。
```go

```
调用多返回值函数时，返回给调用者的是一组值，调用者必须显式的将这些值分配给变量，如果某个值不被使用，可以将其分配给 "_"：
```go
links, err := findLinks(url)

links, _ := findLinks(url)
```
参数数量可变的函数称为为可变参数函数，在声明可变参数函数时，需要在参数列表的最后一个参数类型之前加上省略符号“...”，这表示该函数会接收任意数量的该类型参数。在函数体中，变量被看作类型为 T 的切片，可以接收任意数量的 T 类型的参数；在调用时隐式创建了一个数组，将实参的一个切片作为参数传给函数：
```go
func sum(vals ...int) int {
    total := 0
    for _, val := range vals{
        total += val
    }
    return total
}

fmt.Println(sum(1,2,3,4))   // "10"
```
将切片变量传入可变参数函数时，需要在最后一个参数后加上省略符号“...”
```go
values := []int{1,2,3,4}
fmt.Println(sum(values...))
```
可变参数函数类型和切片作为参数的函数并不是相同的类型：
```go
func f(...int){}
func g([]int){}
fmt.Println("%T\n", f)  // "func(...int)"
fmt.Printf("%T\n", g)   // "func([]int)"
```
### 函数值
Go 语言中函数被看作第一类值（first-class values），拥有类型，可以被赋值给变量，传递给函数，从函数返回。对函数值的调用类似于函数调用，不同类型的函数值也不可以互相赋值。
```go
func square(n int) int{return n * n}

f := square
fmt.Println(f(3))   // "9"

fmt.Printf("%T\n", f) // "func(int) int"
```
函数类型的零值是 nil，直接调用零值的函数值会引起 panic 异常。函数值之间是不可比较的，因此函数值不能作为 map 的 key。
```go
var f func(int) int
// 直接调用 nil 的函数值会引起 panic
if f != nil{
    f(3)
}
```

通过函数字面量(function literal)可以在任何表达式中表示一个函数值。函数字面量的语法和函数声明相似，除了 func 关键字没有函数名，函数字面量的值称为匿名函数(anonymous function)。通过匿名函数定义的内部函数可以访问和更新函数的局部变量，即匿名内部函数中记录了变量的引用，因此函数值是属于引用类型且函数值不可比较。
```go
func squares() func() int {
    var x int
    // 函数值中保存着变量 x 的引用
    return func() int {
        x++
        return x * x
    }
}

f := squares()

fmt.Println(f())  // 1
fmt.Println(f())  // 4
```
循环变量只会在首次循环时创建，后续的循环只是重新复制而不会重新创建。函数值中记录的是变量的内存地址，而不是变量某一时刻的值，当循环开始遍历时，循环变量的值发生了变化但是变量的地址并未发生改变，因此在遍历完成后所有的函数值引用的变量值相同。
```go
var dirs []func()
for _, dir := range []int{1,2,3}{
    // 函数值引用 dir，但是 dir 每次循环都会创建，因此每个函数值引用的 dir 都不相同
    dir := dir
    fmt.Println(&dir)
    dirs = append(dirs, func(){
        fmt.Println(dir)
    })
}
for _, f := range dirs{
    f()     // "1 2 3"
}

// dir 不会在每次循环重新创建，而是复用变量，因此每个函数值引用的 dir 都是相同的，其值为集合最后一个 dir 的值
var dirs []func()
for _, dir := range []int{1,2,3}{
    fmt.Println(&dir)
    dirs = append(dirs, func(){
        fmt.Println(dir)
    })
}
for _, f := range dirs{
    f()     // "1 2 3"
}
```
### 延迟函数
延迟函数是在普通函数或方法调用前加关键字 defer，当 defer 语句被执行时，defer 关键字后面的函数就会被延迟执行。直到包含该 defer 语句的函数执行结束时，defer 后面的语句才会被执行(defer 后面的函数参数已经拷贝，只是执行被延迟)，无论 defer 是由于函数正常结束还是由于 panic 导致异常结束；

defer 语句经常被用于处理成对的操作，如打开、关闭、连接、断开连接、加锁、释放锁。通过defer机制，不论函数逻辑多复杂，都能保证在任何执行路径下，资源被释放。
```go
func ReadFile(filename string) ([]byte, error) {
    f, err := os.Open(filename)
    if err != nil {
        return nil, err
    }
    defer f.Close()
    return ReadAll(f)
}
```
defer 语句只是延迟执行函数，因此对于返回函数值的延迟函数在 defer 语句执行时会执行并返回函数值，而函数值的执行需要等到整个函数结束时才能执行。
```go
func sayHello(){
    // defer 语句执行时执行 trace 函数返回函数值，返回的函数值在 sayHello 函数结束时执行
    defer trace("hello")()
    time.Sleep(time.MillSeconds(10))
}

func trace(s string)func(){
    start := time.Now()
    log.Printf("enter %s", msg)
    return func() { 
        log.Printf("exit %s (%s)", msg,time.Since(start)) 
    }
}
```
函数中如果包含多条 defer 语句，则它们的执行顺序与声明顺序相反。
```go
func multiDefer(){
    defer fmt.Println("hello");fmt.Println("world")
    fmt.Println("function")
}
```
defer 语句中的函数会在 return 语句更新返回值变量后再执行
```go
func triple(x int) (result int) {
    defer func() { result += x }()
    return x * x
}
fmt.Println(triple(4)) // "12"
```
在循环中使用 defer 需要注意 defer 语句后的延迟函数是在函数执行完毕才会执行：
```go
for _, filename := range filenames {
    f, err := os.Open(filename)
    if err != nil {
        return err
    }
    // defer 语句后的延迟函数并不会在每次循环时调用，有可能会耗尽文件描述符
    defer f.Close()
    // ...process f…
}

// 将关闭文件的 defer 语句移到新的函数中，每次函数调用结束时延迟函数会执行
for _, filename := range filenames {
    if err := doFile(filename); err != nil {
        return err
    }
}
func doFile(filename string) error {
    f, err := os.Open(filename)
    if err != nil {
        return err
    }
    defer f.Close()
    // ...process f…
}
```
### 错误处理
Go 语言错误处理中，程序运行失败被认为是预期的结果之一。将运行失败看作预期结果的函数会返回一个额外的返回值来传递错误信息，如果失败的原因只有一个，额外的返回值通是一个常命名为 ok 布尔值；如果失败的原因有多个，额外的返回值一般是 error 类型：
```go
value, ok := cache.Lookup(key)

resp, err := http.Get(url)
```
在 Go 语言中，函数运行失败时会返回错误信息，这些错误信息被认为是一种预期的值而非异常(exception)，Go 语言中的异常用于处理未被预料到的错误，即 bug。

函数调用时返回错误通常有 5 中处理方式，根据错误发生的情况不同而采用不同的处理方式。

最常用的方式是**传播错误**，当函数中某个子程序的调用失败转变为当前函数的调用失败，此时一般使用 ```fmt.Errorf``` 函数在子程序返回的失败信息前缀额外添加上下文信息，并返回错误。由于错误信息经常是以链式组合在一起的，所以错误信息中应避免大写和换行符，编写错误信息时，要确保错误信息对问题细节的描述是详尽的且相同的函数或同包内的同一组函数返回的错误在构成和处理方式上是相似的。
```go
doc, err := html.Parse(resp.Body)
resp.Body.Close()
if err != nil {
    return nil, fmt.Errorf("parsing %s as HTML: %v", url, err)
}
```
如果错误的发生是偶然的或者由不可预知的问题导致的，一般采用的是**重新尝试失败的操作**，在重试时需要限制重试的次数以及时间间隔。
```go
func WaitForServer(url string) error {
    const timeout = 1 * time.Minute
    deadline := time.Now().Add(timeout)
    for tries := 0; time.Now().Before(deadline); tries++ {
        _, err := http.Head(url)
        if err == nil {
            return nil
        }
        log.Printf("server not responding (%s);retrying…", err)
        time.Sleep(time.Second << uint(tries)) // exponential back-off
    }
    return fmt.Errorf("server %s failed to respond after %s", url, timeout)
}
```
如果错误发生后程序无法运行，则需要**输出错误信息并结束程序**，这种策略应该只在 main 函数中执行。对于库函数而言除非程序内部遇到了 bug，才能在库函数中结束程序。
```go
func main(){
    url := ""
    if err := WaitForServer(url); err != nil {
        fmt.Fprintf(os.Stderr, "Site is down: %v\n", err)
        os.Exit(1)
    }
}
```
调用 ```log.Fatalf``` 函数可以以更简洁的方式实现：
```go
if err := WaitForServer(url); err != nil {
    log.Fatalf("Site is down: %v\n", err)
}
```
如果错误发生后不需要中断程序的运行，则**只输出错误信息**即可，log 包提供了多个函数用于输出错误信息：
```go
if err := Ping(); err != nil {
    log.Printf("ping failed: %v; networking disabled",err)
}

if err := Ping(); err != nil {
    fmt.Fprintf(os.Stderr, "ping failed: %v; networking disabled\n", err)
}
```
如果程序发生错误且没有处理并不会对程序的逻辑产生影响，则可以**直接忽略错误**。在忽略错误的时候应该记录忽略的缘由：
```go
dir, err := ioutil.TempDir("", "scratch")
if err != nil {
    return fmt.Errorf("failed to create temp dir: %v",err)
}
// ...use temp dir…
os.RemoveAll(dir) // ignore errors; $TMPDIR is cleaned periodically
```

Go 程序中有些错误只能在运行时检查，当发生这些运行时错误时会引起 panic 异常，导致程序中断并立即执行在该 goroutine 中被延迟的函数(defer)，然后程序崩溃并输出包括 panic value 和函数调用堆栈的日志信息。

除了运行时会产生 panic 异常外，直接调用内置的 panic 函数也会引发 panic 异常。panic函数接受任何值作为参数，当一些不可能发生的情况发生时，此时认为程序出现了 bug，需要调用 panic 函数触发异常：
```go
switch s := suit(drawCard()); s{
    case "Spades":
    case "Hearts":
    case "Diamonds":
    case "Clubs":
    default:
      panic(fmt.Sprintf("invalid suit %q", s))
}
```
程序触发 panic 异常并在释放堆栈信息之前，在触发 panic 异常之前的延迟函数(defer)会被调用，可以通过这种方式输出 panic 异常时的堆栈信息：
```go
defer fmt.Println("before") // before
panic("panic")
defer fmt.Println("after")  // 不会执行

func main(){
    defer printStack()
}

func printStack(){
    var buf [4096]byte
    // 获取 panic 时的堆栈信息
    n := runtime.Stack(buf[:], false)
    os.Stdout.Write(buf[:n])
}
```
如果在延迟函数(defer)中调用了 recover 并且定义该 defer 语句的函数发生了 panic 异常，recover 会使程序从 panic 中恢复，并返回 panic value。

recover 使得导致 panic 异常的函数不会继续运行，但能正常返回。在未发生 panic 时调用 recover 会返回 nil。
```go
func Parse(input string) (s *Syntax, err error){
    defer func(){
        if p := recover(); p != nil {
            err = fmt.Errorf("internal error: %v", p)
        }
    }()
    // ...parser...
}
```
对 panic 异常的恢复不能不加区分，需要遵守一些规范：
- 不应该试图恢复其他包引起的 panic
- 公有的 API 应该将函数的运行失败作为 error 返回，而不是 panic

在实际处理中，将需要恢复的 panic 的 panic value 设置成特殊类型，在 recover 时对 panic value 进行检查，如果是特殊类型则作为 error 处理，否则按照正常的 panic 进行处理。
```go
func soleTitle(doc *html.Node) (title string, err error) {
    type bailout struct{}
    defer func() {
        switch p := recover(); p {
            case nil:       // no panic
            case bailout{}: // "expected" panic
              err = fmt.Errorf("multiple title elements")
            default:
              panic(p) // unexpected panic; carry on panicking
        }
    }()

    // Bail out of recursion if we find more than one nonempty title.
    forEachNode(doc, func(n *html.Node) {
        if n.Type == html.ElementNode && n.Data == "title" &&
            n.FirstChild != nil {
            if title != "" {
                panic(bailout{}) // multiple title elements
            }
            // 如果 panic 则不会执行
            title = n.FirstChild.Data
        }
    }, nil)
    if title == "" {
        return "", fmt.Errorf("no title element")
    }
    return title, nil
}
```

## 方法
对象其实也就是一个简单的值或者变量，在这个对象中包含一些方法，这些方法则是和这个特殊类型关联的函数，面向对象就是借助方法而非直接操作对象来表达属性或者对应的操作。

在函数名前添加一个接收器参数(receiver)即定义了一个方法，这个附加的参数会将函数附加到参数对应类型上，相当于为这种类型定义了一个独占的函数。
```go
func (r type) name(parameter_list) retrun_list{
    func_body
}
```
**方法可以被声明到任意类型，只要不是一个指针或者 interface**。对于一个给定的类型，其内部的方法都必须有唯一的方法名(go 不支持重载)，但是不同的类型可以有相同的方法名。
```go
perim := geometry.Path({1,1}, {5,1}, {5,4}, {1,1})
fmt.Println(geometry.Path.Distance(perim))
fmt.Println(perim.Distance())
```
方法的接收器参数在方法调用时也需要拷贝，当方法的接收器变量本身比较大时，可以使用指针而不是对象来声明方法：
```go
func (p *Point) ScaleBy(factor float64){
    p.X *= factor
    p.Y *= factor
}
```
go 语言为方法调用提供了语法糖，无论方法的 receiver 是指针类型还是非指针类型，都是可以通过指针/非指针类型进行调用，编译器会帮你做类型转换：
```go
r := Point{1, 2}

(&r).ScaleBy(2)
fmt.Println(*r) // "{2, 4}"

r.ScaleBy(2)
fmt.Println(*r) // "{2, 4}"
```
如果方法的 receiver 是非指针类型，则调用方法时存在一次 receiver 拷贝，如果 receiver 是指针类型则在调用方法的时候只是一次指针拷贝，指针指向的还是同一块内存地址
```go
type Point struct {
    x, y int
}

func (p *Point) distance(){
    fmt.Printf("address: %q\n", p)
}

func (p Point) distance2(){
    fmt.Printf("address: %q\n", &p)
}

func show(){
    p := Point{
        x:1,
        y:2,
    }

    fmt.Printf("address: %q\n", &p)

    (&p).distance()
    p.distance()

    (&p).distance2()
    p.distance2()
}
```
receiver 对于方法来说也是参数，因此对于接收器的零值为 Nil 的方法来说，Nil 是一个合法的接收器，但是在方法内部对接收器的 nil 零值操作必须合法：
```go
// An IntList is a linked list of integers.
// A nil *IntList represents the empty list.
type IntList struct {
    Value int
    Tail  *IntList
}
// Sum returns the sum of the list elements.
func (list *IntList) Sum() int {
    if list == nil {
        return 0
    }
    return list.Value + list.Tail.Sum()
}
```

通过嵌入类型，外部类型可以作为接收器调用嵌入类型的方法，即使外部类型没有声明这些方法。这种内嵌类型的方式不是 "is a"，而是 "has a"，编译器会生成额外的包装方法来适配内嵌类型的方法。

可以定义和嵌入类型相同的方法名以覆盖嵌入类型的同名方法；编译器在调用方法时首先查找直接定义在类型的方法，然后查找嵌入类型的方法
```go
type ColorPoint struct {
    Point
    color.RGBA
}

func (c *ColoredPoint) distance(){
    fmt.Println("distance in coloredPoint")
}

c.distance() // distance in coloredPoint
```
内嵌类型可以是指针，此时字段和方法会被间接地引入当前类型中，可以共享通用的结构并动态改变对象之间的关系：
```go
type ColorPoint struct{
    *Point
    Color color.RGBA
}

p := ColoredPoint{&Point{1, 1}, red}
q := ColoredPoint{&Point{5, 4}, blue}
fmt.Println(p.Distance(*q.Point)) // "5"
q.Point = p.Point                 // p and q now share the same Point
p.ScaleBy(2)
fmt.Println(*p.Point, *q.Point) // "{2 2} {2 2}"
```
方法只能在命名类型或者指向类型的指针上定义，通过内嵌类型可以使匿名 struct 类型拥有了方法
```go
var cache = struct{
    sync.Mutex
    mapping map[string]string
}{
    mapping: make(map[string]string),
}

func Lookup(key string) string{
    cache.Lock()
    v := cache.mapping[key]
    cache.Unlock()
    return v
}
```
### 方法值
方法是绑定到接收器参数的特殊函数，因此方法也可以向函数一样作为值赋值给变量。方法值在调用时不需要指定接收器，只需要传入参数即可。
```go
s := p.distance
func methodValue(f func()){
    f()
}
methodValue(s)
```
和方法值不同，方法表达式不会绑定接收器，但是需要将第一个参数作为接收器。方法表达式写作 ```T.f``` 或者 ```(*T).f```，其中 T 是类型，f 是该类型的方法名。：
```go
func (p *Point) Add(q *Point) *Point{
    return &Point{
        x: p.x + q.x,
        y: p.y + q.y,
    }
}
func (p *Point) Sub(q *Point) *Point{
    return &Point{
        x: p.x - q.x,
        y: p.y - q.y,
    }
}

type Path []Point

func (path *Path) TranslateBy(offset *Point, add bool){
    // 函数的第一个参数作为接收器
    var op fun(p, q *Point) *Point
    if add{
        op = (*Point).Add
    }else{
        op = (*Point).Sub
    }
    for i := range *path{
        (*path)[i] = *op(&(*path)[i], offset)
    }
}
```
### 封装
一个对象的变量或者方法如果对调用方是不可见的话，一般就被定义为封装。封装也称为信息隐藏，Go 语言中控制字段的可见性使用首字母大写标识，基于名字的可见性使得在语言中最小的封装单元是 package。

封装提供了三方面的优点：
- 调用方不能看到对象的隐藏变量和方法，只需要关注少量的方法和变量即可，减少调用方在使用对象时的复杂度
- 隐藏实现的细节，防止调用方依赖那些可能变化的具体实现
- 调用方不能直接修改对象的变量值，阻止了外部调用方对对象内部的值任意地进行修改
```go
type Counter struct { n int }
func (c *Counter) N() int     { return c.n }
func (c *Counter) Increment() { c.n++ }
func (c *Counter) Reset()     { c.n = 0 }
```
## 接口
通过组合、封装和接口使得 Go 语言具备了对 OOP 的支持。接口类型是一种抽象的类型，描述了一系列方法的集合。

Go语言中接口类型的独特之处在于它是隐式实现的，也就是说在定义具体类型的时候不需要显式声明其实现的接口类型，而是简单的实现这些接口类型的方法即可。这种设计可以使新创建的接口类型满足已经存在的具体类型却不需要改变具体类型的定义，当使用的具体类型来自不受控制的包时这种设计尤其有用。

接口类型也可以内嵌，和结构体内嵌相似，外层的接口获取了嵌入接口的所有定义的方法
```go
type Reader interface{
    Read(p []byte) (n int, err error)
}

type Writer interface{
    Write(p []byte) (n int, err error)
}

// 只有实现了 Reader 和 Writer 的所有方法才实现了 ReadWriter 接口类型
type ReadWriter interface{
    Reader
    Writer
}
```
一个类型如果拥有一个接口需要的所有方法，那么这个类型就实现了这个接口。**一个类型实现了某个接口类型则该类型就属于这个接口类型**，即可以直接将实现接口的类型值赋值给接口类型变量：
```go
// io.Writer 是接口
var w io.Writer
// os.Stdout 实现了 io.Writer 的所有方法
w = os.Stdout

var rwc io.ReadWriteCloser
rwc = os.Stdout

// 接口类型也符合(类似于 is-a 的关系)
w = rwc
```
T 类型的值(对象)不拥有 *T 指针类型的方法，但是 *T 指针类型拥有 T 类型的所有方法，也就是说 T 类型会比 *T 类型实现更少的接口类型
```go
type IntSet struct{}
// *IntSet 类型实现了 fmt.Stringer 接口，而 IntSet 没有实现
func (*IntSet) String() string{return ""}

var _ fmt.Stringer = IntSet{}   // 编译错误，IntSet 类型并不属于 fmt.Stringer 类型
var _ fmt.Stringer = &IntSet{}  // 编译成功，*IntSet 类型属于 fmt.Stringer 类型
```
空接口 ```interface{}``` 对实现它的类型没有要求，因此可以将任意一个类型的值赋给空接口类型
```go
var any interface{}

any = 1234
any = true
```
### 接口值
接口类型的值是由两部分构成：动态类型和动态值(类型的值)。**接口值的零值是 nil，此时它的类型和值都是 nil**。

两个接口值具有相同的动态类型且动态类型可比较的情况下才是可比较的。因此当接口类型作为 map 的 key 或者结构体类型时需要注意可能会由于接口类型的值不具有可比较性而 panic
```go
var x interface{} = []int{1, 2, 3}
fmt.Println(x == x)  // panic，slice 类型不可比较
```
*接口类型的零值 nil 和动态值为 nil 的接口值是不同的*，接口类型的零值是动态类型和动态值都是 nil，而动态值为 nil 的接口值的动态类型不为 nil，因此动态值为 nil 的接口值和接口类型的零值不可比较。
```go
var buf bytes.Buffer

func f(out io.Writer){  //调用 f(buf) 时，out 的动态类型是 bytes.Buffer，动态值是 nil，因此 out != nil 成立
    if out != nil{
        out.Write([]byte("done!\n"))  // 调用 (*bytes.Buffer)Write() 方法，而 bytes.Buffer 为 nil 所有会 panic
    }
}

f(buf)  // panic: nil pointer dereference

// 将变量 buf 的类型改为 io.Writer，此时 buf 为零值 nil
var buf io.Writer

f(buf)
```
在接口值上使用类型断言操作，用以检查操作对象的动态类型是否和断言的类型匹配。类型断言使用 ```x.(T)``` 的语法，x 表示一个接口类型变量，T 表示一个具体类型。
```go
var w io.Writer = os.Stdout
f, ok := w.(*os.File)      // success:  ok, f == os.Stdout
b, ok := w.(*bytes.Buffer) // failure: !ok, b == nil
```
如果断言成功则返回动态值(动态类型及其值)，如果断言失败则返回被断言类型的零值，如果断言操作的对象是一个 nil 接口值则类型断言会失败。
```go
var w io.Writer
w = os.Stdout
if w, ok := w.(*os.File); ok {
	fmt.Printf("%T\n", w)   // "*os.File"
}
```
类型断言可用于区别错误类型，当有多种错误且不同的错误处理逻辑不同时，可以创建多个错误类型分别代表不同的错误，在处理错误时可以通过类型断言处理不同的错误：
```go
func IsNotExist(err error) bool {
    if pe, ok := err.(*PathError); ok {
        err = pe.Err
    }
    return err == syscall.ENOENT || err == ErrNotExist
}
```
类型分支和 switch 语句相似，其运算对象是 ```x.(type)```，每个 case 有一个到多个类型，nil 的 case 和 x == nil 匹配，default 表示和其他所有 case 都不匹配。
```go
swithc x.(type){
    case nil:       // ...
    case int, uint: // ...
    case bool:      // ...
    case string:    // ...
    default:        // ...
}
```
## 并发
Go 语言中的并发编程有两种实现手段：goroutine-channel 和 多线程共享内存。goroutine-channel 支持顺序通信进程(communication sequential processes, CSP)编程模型，这种模型总值会在不同的运行实例(goroutine)中传递。

### goroutine
Go 语言中每个并发地执行单元称为一个 goroutine。主函数在一个单独的 goroutine 中运行，称为 main goroutine。新建 goroutine 需要使用 go 语句实现，即在函数或方法调用前加关键字 go，go 语句将语句中的函数在一个新创建的 goroutine 中运行。当 main goroutine 退出时，所有的 goroutine 会中断。
```go
func main(){
    go spinner(100 * time.Millisecond)
    const n = 45
    fibN := fib(n)
    fmt.Printf("\rFibonacci(%d) = %d\n", n, fibN)
}

func spinner(delay time.Duration){
    for{
        for _, r := range `-\|/`{
            fmt.Printf("\r%c", r)
            time.Sleep(delay)
        }
    }
}

func fib(x int) int{
    if x < 2{
        return x
    }
    return fib(x-1) + fib(x-2)
}
```
### channel
channel 是 goroutine 之间的通信机制，可以让 goroutine 间通过 channel 发送消息。每个 channel 都有一个特殊的类型，即可以发送数据的类型。使用内置的 make 函数创建 channel。
```go
ch := make(chan int)
```
channel 是一个引用，当复制一个 channel 时只是拷贝了一个 channel 的引用，二者引用同一个 channel。channel 的零值是 nil

channel 有发送和接收两个操作，发送语句将一个值从一个 goroutine 通过 channel 发送到另一个执行接收操作的 goroutine。发送和接收两个操作都是使用 ```<-``` 运算符，在发送语句中 ```<-``` 运算符分割 channel 和要发送的值，在接收语句中 ```<-``` 运算符写在 channel 对象之前。
```go
ch <- x  // a send statement
x = <-ch // a receive expression in an assignment statement
<-ch     // a receive statement; result is discarded
```
使用内置 close 函数可以关闭 channel，向已经关闭的 channel 发送数据会导致 panic 异常，但是仍然可以从关闭的 channel 中接收数据。当一个被关闭的 channel 中已经发送的数据都被成功接收后，后续的接收操作将不再阻塞，会立即返回一个零值。
```go
close(ch)
fmt.Println(<- ch)
```

基于无缓存的 channel 的发送操作将导致发送 goroutine 阻塞，直到另一个 goroutine 在相同的 channel 上执行接收操作；同理在无缓存的 channel 上执行接收操作的 goroutine 也会阻塞直到另一个 goroutine 在相同的 channel 上执行发送操作。

基于无缓存的 channel 的发送和接收操作将导致两个 goroutine 进行一次同步操作。当向无缓存的 channel 发送数据时，接收的 goroutine 接收到数据发生在唤醒发送的 goroutine 之前。

带缓存的 channel 内部持有一个元素队列，队列的最大容量在使用 make 函数创建 channel 时指定。
```go
ch = make(chan string, 3)
```
带缓存的 channel 的发送操作就是向内部缓存队列的尾部插入元素，接收操作是从队列的头部删除元素。如果内部缓存队列是满的，那么发送操作将阻塞直到另一个 goroutine 执行接收操作释放队列空间；如果 channel 是空的，接收操作将阻塞直到另一个 goroutine 执行发送操作向队列中插入元素。

内置的 cap 函数可以获取 channel 内部缓存的容量，len 函数内部缓存队列中有效元素的个数：
```go
fmt.Println(cap(ch))

fmt.Println(len(ch))
```
阻塞在 channel 的 goroutine 会由于没有接收操作而被永久阻塞，这种情况称为 goroutine 泄漏，泄漏的 goroutine 并不会被自动回收，因此需要确保每个不再需要的 goroutine 能正常退出。

channel 可以用于将多个 goroutine 链接起来，一个 channel 的输出作为下一个 channel 的输入，这种串联 channel 就是 pipline。Go 语言的 range 循环可以直接在 channel 上迭代，当 channel 关闭且没有值可接收时跳出循环：
```go
func main(){
    naturals := make(chan int)
    squares := make(chan int)

    go func(){
        for x := 0; x < 100; x++{
            naturals <- x
        }
        // 关闭 channel
        close(naturals)
    }()

    go func(){
        x, ok := range naturals {
            squares <- x*x
        }
        close(squares)
    }()

    for{
        fmt.Println(<- squares)
    }
}
```
channel 并不一定需要关闭，不管一个 channel 是否关闭，当它没有被引用时会被垃圾收集器自动回收而不会造成泄漏。**重复关闭一个 channel 会导致 panic 异常，关闭一个 nil 的 channel 也会导致 panic 异常。**

Go 语言提供了单方向的 channel 类型，```chan<-``` 表示只有发送操作的 channel，```<-chan``` 表示只有接收操作的 channel，这种限制将在编译期检测。关闭操作只用于断言不再向 channel 发送新的数据，因此只有发送 goroutine 才会调用 close 函数，所以只接收的 channel 上使用 close 函数会编译错误。**双向 channel 都可以隐式转换为单向 channel，但是单向 channel 不能转换为双向 channel。**
```go
func counter(out chan<- int){
    for x := 0; x < 100; x++{
        out <- x
    }
    close(out)
}

func squarer(out chan<- int, in <-chan int){
    for v := range in{
        out <- v*v
    }
    close(out)
}

func printer(in <-chan int){
    for v := range in{
        fmt.Println(v)
    }
}

func main(){
    naturals := make(chan int)
    squares := make(chan int)
    // chan int 类型隐式转换为 chan<- int
    go counter(naturals)
    go squarer(squares, naturals)
    // chan int 类型隐式转换为 <-han int
    printer(squares)
}
```
channel 支持多路复用(multiplex)操作，使得在一个 goroutine 内可以监听多个 channel 的数据，当某个 channel 满足条件则会执行对应的操作。channel 的多路复用使用 select 语句，形式和 switch 类似，每个 case 表示一个通信操作(发送或者接收操作)并且会包含一些语句组成的语句块。如果多个 case 同时就绪，select 会随机选择一个执行，这样保证每一个 channel 都有平等的被 select 的机会。
```go
select {
    case <-ch1:
    // ...
    case x := <-ch2:
    // ...
    case ch3 <- y:
    // ...
    default:
    // ...
}
```
Go 语言没有提供在一个 goroutine 中终止另一个 goroutine 的方法，通过 channel 可以把终止消息广播到其他 goroutine。当一个 goroutine 关闭了 channel 后其他的 goroutine 就会收到零值，此时接收到零值的 goroutine 就可以执行终止操作了，通过这种方式就可以实现广播。
```go
var done = make(chan struct{})

func cancelled() bool{
    select {
        case <-done:
            return true
        default:
            return false
    }
}
```
### 共享内存
如果一个函数在并发地情况下依然可以正确地工作，则这个函数是并发安全的，并发安全地函数不需要额外的同步工作。如果一个类型的所有访问方法和操作都是并发安全的，则这个类型就是并发安全的。

竞争条件指的是程序在多个 goroutine 并发执行时导致错误的结果。数据竞争是一个特定的竞争条件，会在两个以上的 goroutine 并发访问相同的变量且至少其中一个为写操作时发生。

数据竞争会导致不确定性，有三种方式可以避免数据竞争：
- 不要写变量，变量在初始化后不会被修改
- 避免多个 goroutine 访问变量，使用 channel 来共享数据
- 允许多个 goroutine 访问变量，但是在同一时刻最多只有一个 goroutine 在访问，即互斥

Go 提供了 sync.Mutex 互斥锁用于在访问共享变量前加锁以保证在同一时刻最多只有一个 goroutine 在访问共享变量。**sync.Mutex 不支持重入**，因此在已经加锁的互斥量上再次加锁会导致死锁，通常的解决办法是将一个函数分离成多个函数，并使用非导出的函数完成逻辑，而调用次函数的函数完成互斥量的加锁。

Go 提供 sync.RWMutex 读写锁，RWMutex 只有当获得锁的大部分 goroutine 都是读操作，才会带来好处，在一般的情况下由于 RWMutex 需要更复杂的内部记录，性能会比 Mutex 差一些。

现代计算机中每个处理器都会有其本地缓存(local cache)，为了效率在对内存的写入一般会在每一个处理器中缓冲，并在必要时一起 flush 到主存，这种情况下缓冲中的数据可能会与当初 goroutine 写入顺序不同的顺序被提交到主存。channel 通信或者互斥量操作这样的原语会使处理器将其缓 flush 并 commit，这样 goroutine 在某个时间点上的执行结果能被其他处理器上运行的 goroutine 得到。

在一个独立的 goroutine 中，每一个语句的执行顺序是连续的，但是在不使用 channel 和 mutex 这样的显示同步操作时，就没法保证事件在不同的 goroutine 看到的执行顺序是一致的。

Go 提供了 sync.Once 来解决一次性初始化的问题，可以避免在变量被构建完成之前和其他 goroutine 共享该变量：
```go
var loadIconsOnce sync.Once
var icons map[string]image.Image

func Icon(name string) image.Image {
    loadIconsOnce.Do(loadIcons)
    return icons[name]
}
```
Go 提供了竞争检查器(the race detector) 用于动态分析竞争条件，只需要在 ```go build``` 或者 ```go run``` 或者 ```go test``` 命令后面添加 ```-race``` 的 flag 就会使编译器创建一个附带了能够记录所有运行期对共享变量访问工具的 test，并且会记录下每一个读或者写共享变量的 goroutine 的身份信息。

### goroutine 和线程
每个 OS 线程都有一个固定大小的内存块(一般是 2MB)来做栈，这个栈会用来存储当前线程正在被调用或挂起的的函数的内部变量，这个固定大小的内存对于一个很小的 goroutine 来说是很大的内存浪费，另一方面对于层次比较深的递归函数来说这个固定大小的栈内存又会显得不够。goroutine 会在创建时只会分配一个很小的栈内存(2 KB)，但是 goroutine 的栈大小并不固定，可以动态的伸缩，最大值有 1 GB，因此 go 程序中可以同时创建成百上千的 goroutine。

OS 线程会被操作系统内核调度，每几毫秒一个硬件计时器就会中断处理器并调用一个叫做 scheduler 的内核函数，这个函数会挂起当前执行的线程并将此线程的寄存器内容保存在内存，然后通过检查线程列表决定下个被执行的线程并从内存中恢复该线程的寄存器信息，然后恢复执行该线程的现场并开始执行该线程。操作系统线程被内核调度，从一个线程向另一个线程切换的过程需要几次内存(用户态)到寄存器(内核态)的数据访问，会增加运行 cpu 周期。

Go 的运行时包含了自己的调度器，这个调度器使用了一些技术手段(比如 m:n 调度)，在 n 个操作系统线程上多工 m 个 goroutine。和操作系统的线程调度不同的是，Go 调度器并不是用一个硬件定时器而是被 Go 本身进行调度的，这种调度方式不需要进入内核的上下文，因此调度一个 goroutine 比调度一个线程代价要低的多。

Go 的调度器使用 ```GOMAXPROCS``` 变量来决定会有多少个操作系统的线程同时执行 Go 程序，默认值是运行机器上的 CPU 的核心数。在休眠中的或者在通信中被阻塞的 goroutine 是不需要一个对应的线程来做调度的。可以在运行时用 ```runtime.GOMAXPROCS``` 函数来修改运行程序的线程数
```go

```
Go 中 goroutine 没有 ID，因此不会向其他语言意向可以做 thread-local 存储。
## 测试

### 工具

## 反射

## cgo

## gc


- https://books.studygolang.com/gopl-zh/
- https://books.studygolang.com/advanced-go-programming-book/

- https://www.bookstack.cn/read/qcrao-Go-Questions/README.md

- http://shouce.jb51.net/gopl-zh/index.html

http://wuchong.me/blog/2019/02/12/how-to-become-apache-committer/

go 语言核心编程

go 语言学习笔记--雨痕

effective go

go in action

http://books.studygolang.com/The-Golang-Standard-Library-by-Example/