## Shell 基础
Shell 是一个命令行解释器，它为用户提供了一个向 Linux 内核发送请求以便运行程序的界面系统级程序，用户可以用 Shell 来启动、挂起、停止甚至是编写一些程序。

Shell 还是一个功能强大的编程语言，易编写，易调试，灵活性较强。Shell 是解释执行的脚本语言，在 Shell 中可以直接调用 Linux 系统命令。

使用 sh 命令可以进入 Shell，使用 exit 命令退出 Shell 环境

### Shell 分类
可以查看 /etc/shell 文件获取当前系统支持的 Shell 类型，一般的 Linux 使用 Bash 作为用户的基本 Shell。

### shell 执行

### Bash 的基本功能
#### 常用快捷键
- ```ctrl+A```：把光标移动到命令行开头。如果我们输入的命令过长，想要把光标移动到命令行开头时使用
- ```ctrl+E```：把光标移动到命令行结尾
- ```ctrl+C```：强制终止当前的命令
- ```ctrl+L```：清屏，相当于 clear 命令
- ```ctrl+U```：删除或剪切光标之前的命令。如果输入了一行很长的命令，不使用退格键一个一个字符的删除，使用这个快捷键很方便
- ```ctrl+K```：删除或剪切光标之后的内容
- ```ctrl+Y```：粘贴 ctrl+U 或 ctrl+K 剪切的内容
- ```ctrl+R```：在历史命令中搜索，按下 ctrl+R 之后，就会出现搜索界面，只要输入搜索内容，就会从历史命令中搜索
- ```ctrl+D```：退出当前终端
- ```ctrl+Z```：暂停，并放入后台。
- ```ctrl+S```：暂停屏幕输出
- ```ctrl+Q```：恢复屏幕输出
#### 命令别名
使用 ```alias alias_command='origion_command'``` 设置别名，使用 ```alias``` 命令查看别名：
```shell
alias llh_test='ls -lh'
```
alias 命令设置的别名只能在当前 Shell 中有效，如果需要在关闭 Shell 之后仍然有效，需要将命令别名写入 ```/<user_home>/.bashrc``` 中:
```shell
echo "alias llh_tests='ls -h'" >> /home/test/.bashrc
# 使别名生效
source .bashrc
```
使用 ```unalias alias_command``` 命令删除别名，如果命令别名写入了 .bashrc 则需要在文件中删除：
```shell
unalias llh_test
```
#### 输入输出重定向
标准输入输出：
- ```/dev/stdin```：标准输入，文件描述符为 0，表示键盘输入
- ```/dev/stdout```：保准输出，文件描述符为 1，表示显示器
- ```/dev/stderr```：标准错误输出，文件描述符为 2，表示显示器
- ```/dev/null```：表示不输出
输出重定向将标准输出重定向到其他文件或设备中：
- ```command > file_name```：以*覆盖*的方式，把命令的*正确输出*重定向到指定的文件或设备中
- ```command >> file_name```：以*追加*的方式，把命令的*正确输出*重定向到指定的文件或设备中
- ```command 2> file_name```：以*覆盖*的方式，把命令的*错误输出*重定向到指定的文件或设备中
- ```command 2>> file_name```：以*追加*的方式，把命令的*错误输出*重定向到指定的文件或设备中
- ```command > file_name 2>&1```：以*覆盖*的方式，把*正确输出和错误输出*都重定向到同一个文件或设备中
- ```command >> file_name 2>&1```：以*追加*的方式，把*正确输出和错误输出*都重定向到同一个文件或设备中
- ```command &>> file_name```：以*追加*的方式，把*正确输出和错误输出*都重定向到同一个文件或设备中
- ```command >> file1_name 2>> file2_name```：把正确的输出追加到文件1中，把错误的输出追加到文件2中

输入重定向将标准输入作为命令的数据源：
- ```comamnd < file_name```
#### 多命令执行顺序
Shell 允许一次执行多条命令，命令的执行顺序由命令执行符控制：
- ```;```：命令执行符两侧的命令顺序执行，命令之间没有任何逻辑联系
- ```&&```：只有当控制符左侧的命令正确执行，右侧的命令才会执行；左侧命令执行不正确(出现错误)，则右侧命令不会执行
- ```||```：控制符左侧命令执行出错，右侧命令才会执行；左侧命令执行没有出错，则右侧命令不会执行
```shell
# ls /root 命令发生错误对后续命令的执行没有影响
ls /root;cd /user/local; cd /home/test
# 列出当前目录，且打印 yes
ls && echo yes || echo no
# ls /root 由于权限问题会执行错误，所以 && 左侧命令错误不会打印 yes，|| 左侧命令错误会打印 no
ls /root && echo yes || echo no
```
#### 管道符
管道符 ```|``` 使左侧命令的输出作为右侧命令的操作对象：
```shell
netstat -an | grep "ESTABLISHED"
```
#### 通配符
通配符是完全匹配，通常用来匹配文件名：
- ```?```：匹配一个任意字符
- ```*```：匹配0个或任意多个任意字符，也就是可以匹配任何内容
- ```[]```：匹配中括号中任意一个字符
- ```[-]```：匹配中括号中任意一个字符，-代表一个范围
- ```[^]```：表示匹配不是中括号内的一个字符
```shell
# 列出所有目录和文件
ls /*
```
#### 特殊符号
- ```''```：单引号，在单引号中所有的特殊字符都没有特殊含义
- ```""```：双引号，在双引号中的字符除了 $(表示调用变量的值)、`(表示引用命令)、\(表示转义符)外都没有特殊含义
- ``` `` ```：反引号,反引号括起来的内容是系统命令，在 Bash 中会先执行它
- ```$()```：和反引号作用一样，用来引用系统命令，推荐使用
- ```#```：在 Shell 脚本中，# 开头的行代表注释
- ```$```：用于调用变量的值，如 $var_name 可得到变量的值
- ```\```：转义符，跟在 \ 之后的特殊符号失去特殊含义，变为普通字符

```shell

```
### Bash 的变量
变量是计算机内存的单元，其中存放的值可以改变，当 Shell 脚本需要保存一些信息时，如一个文件名或是一个数字，就把它放在一个变量中。
#### 自定义变量
自定义变量使用 ```var_name=var_value``` 形式定义，变量的默认类型都是字符串型，如果要进行数值运算，则必须指定变量类型为数值型，如果是把命令的结果作为变量值赋予变量，则需要使用 反引号 或 $() 包含命令，变量的值如果有空格，需要使用单引号或双引号包括
```shell
var_test="hello world"
```
定义的变量可以叠加，变量叠加时需要用双引号包含(```"$var_name"```) 或使用 ``` ${}``` 包含(``` $(var_name)```)。
```shell
var_test=${var_test}1234
```
使用 ```$var_name``` 方式可以调用变量获取变量的值：
```shell
echo $var_test
```
可以使用 ```unset var_name``` 方式删除定义的变量：
```shell
unset var_test
```
#### 环境变量
自定义变量只在当前的 Shell 中生效，环境变量会在当前 Shell 和这个 Shell 的所有子 Shell 中生效。使用 ```export``` 命令可以设置环境变量：
```shell
# 创建新的环境变量
export var_test1="hello world"
# 设置已存在的变量为环境变量
export var_test
```
可以使用 ```env``` 命令查看所有的环境变量，同样也可以使用 ```unset var_name``` 的方式删除环境变量。

常见的环境变量：
- PATH：系统查找命令的路径
- PWD：当前所在目录
#### 预定义变量
Bash 中已经定义好的变量，变量名不能自定义，变量作用也是固定的。
- ```$?```：最后一次执行的命令的返回状态。如果这个变量的值为0，证明上一个命令正确执行；如果这个变量值为非0(具体是哪个数由命令自己决定)，则证明上一个命令执行不正确
- ```$$```：当前进程的进程号(PID)
- ```$!```：后台运行的最后一个进程的进程号
```shell
#!/bin/bash
# 输出当前进程的 ID
echo "the current process is $$"
# 在 /root 目录下查找 hello.sh 文件，& 的意思是把命令放入后台执行
find /root -name hello.sh &
# 输出后台运行的最后一个进程 ID
echo "the last one deamon process is $!"
```
#### 位置参数变量
主要是用来向脚本当中传递参数或数据的，变量名不能自定义，变量作用是固定的：
- ```$n```：n为数字，$0代表命令本身，$1-$9代表第一到第九个参数，十以上的参数需要用大括号包含，如 ${10}
- ```$*```：代表命令行中的所有参数，$*把所有的参数看成一个整体
- ```$@```：代表命令行中的所有参数，表示参数集合
- ```$#```：命令行中所有参数的个数
```shell
function position_var(){
    # $* 中的所有参数看成是一个整体，所以只会循环一遍
    for i in "$*"
	do
        echo "The parameter is: $i"
	done
			
	# $@ 中的每一个参数都是独立的，所以不止循环一遍
	x=1
	for y in "$@"
	do
        echo "the parameter $x is: $y"
        x=$(($x+1))
	done
}
```
#### 环境变量配置文件
环境变量配置文件中主要是定义对系统的操作环境生效的系统默认环境变量：
- ```/etc/profile``` 中的环境变量对所有用户有效
- ```<user_hoame>/.bash_profile``` 中的环境变量只对该用户有效

使用 ```source profile_name``` 或者 ```/profile/path``` 可以使环境配置文件立即生效。
### Bash 的运算符

## Shell 编程
### printf
### awk
### sed

#### xargs

### 条件判断表达式
使用 ```test expr``` 可以断言表达式：
- ```-e FILE```：判断文件是否存在
- ```-d FILE```：判断文件是否是目录
- ```-f FILE```：判断文件是否是文件
- ```-s FILE```：判断文件大小是否大于 0
```shell
test -e hello.txt && test -d hello.txt && echo yes || echo no

test -f hello.txt && test -s hello.txt && echo yes || echo no
```
- ```-r FILE```：判断文件对于当前用户是否有读权限
- ```-w FILE```：判断文件对于当前用户是否有写权限
- ```-x FILE```：判断文件对于当前用户是否有执行权限
```shell
# 当前用户非 root 没有 /root 访问权限所以打印 no
sudo test -r /root echo yes || echo no
# 切换到 root 用户后拥有 /root 访问权限所以打印 yes
sudo test -r /root && echo yes || echo no
```
- ```INTEGER_1 -eq INTEGER_2```：判断整数1是否等于整数2(是则为真)
- ```INTEGER_1 -ne INTEGER_2```：判断整数1是否不等于整数2(是则为真)
- ```INTEGER_1 -gt INTEGER_2```：判断整数1是否大于整数2(是则为真)
- ```INTEGER_1 -lt INTEGER_2```：判断整数1是否小于整数2(是则为真)
- ```INTEGER_1 -ge INTEGER_2```：判断整数1是否大于等于整数2(是则为真)
- ```INTEGER_1 -le INTEGER_2```：判断整数1是否小于等于整数2(是则为真)
```shell
test 1 -eq 1 && test 1 -gt 0 && echo yes || echo no
```
- ```-z STRING```：判断字符串是否为空(是则为真)
- ```STRING_1 == STRING_2```：判断字符串1是否和字符串2相等(是则为真)
- ```STRING_1 != STRING_2```：判断字符串1是否和字符串2不相等(是则为真)
```shell
test -z "$var_name" && test "hello" == "$var_name" && echo yes || echo no
```
- ```epxr_1 -a expr_2```：判断1和判断2都成立，最终结果才为真
- ```expr_1 -o expr_2```：判断1或判断2为真，最终结果为真
- ```! expr```：原始的判断取反
```shell
test -n "$aa" -a "$aa" -gt 23 && echo "yes" || echo "no"
```
### 流程控制
#### if 语句
if 语句有两种语法：
```shell
if [ expr ];then
    body
fi


if [ expr ]
then
    body
fi
```
```[ expr ]``` 就是使用 test 命令判断，所以中括号和条件判断式之间必须有空格；```then``` 后面跟符合条件之后执行的程序，可以放在 ```[]``` 之后用 ```;``` 分隔，也可以换行写入就不需要 ```;``` 了。
```shell
#!bin/bash
#统计根分区使用率
rate=$(df -h | grep "/dev/sda3" | awk '{print $5}' | cut -d "%" -fl)
if [ $rate -ge 80 ];then
    echo "Warning ! /dev/sda3 is full !!"
fi
```

当 if 条件不满足时可以使用 else 语句：
```shell
if [ expr ]
then
    if_body
else
    else_body
fi
```
```shell
#!/bin/bash
#备份 mysql 数据库
				
#同步系统时间
ntpdate asia.pool.ntp.org &> /dev/null
#把当前系统时间按照 "年月日" 格式赋予变量 date
date=$(date +%y%m%d)
#统计 mysql 数据库的大小，并赋值给 size 变量
size=$(du -sh /var/lib/mysql)
if [ -d /tmp/dbbak ]
then
    echo "Date: $date!" > /tmp/dbbak/dbinfo.txt
    echo "Data size: $size" > /tmp/dbbak/dbinfo.txt
    cd /tmp/dbbak
    tar -zcvf mysql-lib-$date.tar.gz /var/lib/mysql dbinfo.txt &> /dev/null
	rm -rf /tmp/dbbak/dbinfo.txt
else
    mkdir /tmp/dbbak
    echo "Date: $date!" > /tmp/dbbak/dbinfo.txt
    echo "Data size: $size" > /tmp/dbbak/dbinfo.txt
    cd /tmp/dbbak
    tar -zcvf mysql-lib-$date.tar.gz /var/lib/mysql dbinfo.txt &> /dev/null
	rm -rf /tmp/dbbak/dbinfo.txt
fi


#!/bin/bash
#判断 apache 是否启动
				
#使用 nmap 命令扫描服务器，并截取 apache 服务的状态，赋予变量 port
prot=$(nmap -sT 192.168.1.156 | grep tcp | grep http | awk '{print $2}')
if [ "$port" == "open"]
then
    echo "$(date) httpd is ok " >> /tmp/autostart-acc.log
else
    /etc/rc.d/init/httpd start &> /dev/null
    echo "$(date) restart httpd !!" >> /tmp/autostart-err.log
fi
```
if 语句还支持多分支情况：
```shell
if [ expr_1 ]
then
    if_body
elif [ expr_2 ]
then
    elif_body
else
    else_body
fi
```
```shell
#!/bin/bash
#判断用户输入的是什么文件
				
#接收键盘的输入，并赋予变量 file
read -p "Please input a file name: " file
if [ -z "$file" ]
then
    echo "Error,please input a filename"
    exit 1
elif [ ! -e "$file" ]
then
    echo "Your input is not a file !"
    exit 2
elif [ -f "$file" ]
then
    echo "$file is a regular file!"
elif [ -d "$file" ]
then
    echo "$file is a directory!"
else
    echo "$file is another file!"
fi
```
#### case 语句
case 语句和 if...elif...else 语句一样都是多分支条件语句，不过和 if 多分支条件语句不同的是，case 语句只能判断一种条件关系，而 if 语句可以判断多种条件关系。
```shell
case $var_name in
"value_1")
    value_1_body
    ;;
"value_2")
    value_2_body
    ;;
*)
    default_body
    ;;
esac
```
```shell
#!/bin/bash
#判断用户输入
			
read -p "Please choose yes/no: " -t 30 var
case $var in
"yes")
    echo "You choosed yes"
    ;;
"no")
    echo "You choosed no"
    ;;
*)
    echo "input error"
    ;;
esac
```
#### for 语句
for 语句有两种语法，一种基于 in 的方式，另一种包含迭代变量：
```shell
for var_name in value_1 value_2 ...
do
    body
done

for((init_value;condition;expr))
do
    body
done
```
```shell
#!/bin/bash
#批量解压缩脚本
				
cd /lamp
ls *.tar.gz > ls.log
for i in $(cat ls.log)
do
    tar -zxvf $i &> /dev/null
done
rm -rf /lamp/ls.log


#!/bin/bash
#批量添加指定数量的用户
				
read -p "Please input user name: " -t 30 name
read -p "Please input the number of users: " -t 30 num
read -p "Please input the password of users: " -t 30 pass
if [ ! -z "$name" -a ! -z "$num" -a ! -z "$pass" ]
then
    y=$(echo $num | sed 's/[0-9]//g')
	if [ -z "$y" ]
	then
	    for ((i=1;i<=$num;i++))
		do
		    /usr/sbin/useradd/ $name$i &> /dev/null
			echo $pass | /usr/bin/passwd --stdin $name$i &> /dev/null
		done
	fi
fi
```
#### while 语句
while 循环是不定循环，也称作条件循环。只要条件判断式成立，循环就会一直继续，知道条件判断式不成立，循环才会停止。
```shell
while [ expr ]
do
    body
done
```
```shell
#!/bin/bash
#从1加到100
				
i=1
s=0
while [ $i -le 100 ]
do
    s=$(($s+$i))
    i=$(($i+1))
done
echo "The sum is: $s"
```
#### until 语句
until 循环和 while 循环相反，until 循环只要条件判断式不成立就执行循环，直到条件判断式成立才停止循环。
```shell
until [ expr ]
do
    body
done
```
```shell
#!/bin/bash
#从1加到100
				
i=1
s=0
until [ $i -gt 100 ]
do
    s=$(($s+$i))
    i=$(($i+1))
done
echo "The sum is: $s"
```
### 函数
必须在调用函数地方之前声明函数；函数只能返回 0~255 之间的数字；函数的返回值只能通过 $? 获取；
```shell
func_name () {
    func_body
	[return result]
}
```

## VIM

​	Vim简介
​		Vim是一个功能强大的全屏幕文本编辑器，是Linux/UNIX上最常用的文本编辑器，它的作用是建立、编辑、显示文本文件
​		Vim没有菜单，只有命令
​	Vim工作模式
​		进入命令模式：vi file_name
​			命令模式中所有的输入都是命令
​		退出命令模式：:wq
​		从命令模式进入插入模式：iao
​			插入模式可以编辑文件
​		从插入模式返回命令模式：ESC键
​		从命令模式进入编辑模式：:
​		从编辑模式返回命令模式：命令以回车结束运行
​	Vim常用操作
​		插入命令
​			a			在光标所在字符后插入
​			A			在光标所在行尾插入
​			i			在光标所在字符前插入
​			I			在光标所在行行首插入
​			o			在光标下插入新行
​			O			在光标上插入新行
​		定位命令
​			:set nu		设置行号
​			:set nonu	取消行号
​			gg			到第一行
​			G			到最后一行
​			nG			到第n行
​			:n			到第n行
​			$			移至行尾
​			0			移至行首
​		删除命令
​			x			删除光标所在处字符
​			nx			删除光标所在处后n个字符
​			dd			删除贯标所在行，ndd删除n行
​			dG			删除光标所在行到文件末尾内容
​			D			删除光标所在处到行尾内容
​			:n1,n2d		删除指定范围的行
​		复制和剪切命令
​			yy			复制当前行
​			nyy			复制当前行以下n行
​			dd			剪切当前行
​			ndd			剪切当前行以下n行
​			p P			粘贴在当前光标所在行下或行上
​			v			进入视觉模式，可以选择部分字符
​			shift+v		进入行选择模式，可以选择多行
​			ctrl+v		进入块选择模式，可以选择一块字符
​		替换和取消命令
​			r			取代光标所在处字符
​			R			从光标所在处开始替换字符，按Esc结束
​			u			取消上一步操作
​		搜索和搜索替换命令
​			/string					搜索指定字符串，搜索时忽略大小写 :set ic
​			n						索搜指定字符串的下一个出现位置
​			:%s/old/new/g			全文替换指定字符串
​			:n1,n2s/old/new/g		在一定范围内替换指定字符串
​		保存和退出命令
​			:w						保存修改
​			:w new_filename			另存为指定文件
​			:wq						保存修改并退出
​			ZZ						快捷键，保存修改并退出
​			:q!						不保存修改退出
​			:wq!					保存修改并退出(文件所有者及root可使用)
​	Vim使用技巧
​		:r 文件名					导入文件的内容
​		:r !命令					导入命令执行结果
​		:map 快捷键 触发命令		定义快捷键
​			:map ^P I#<ESC>
​			:map ^B 0x
​		:n1,n2s/^/#/g				连续行注释
​			:1,4s/^/#/g		1-4行行首加上#
​			:1,4s/#//g		1-4行所有的#删除
​			:1,4s/^#//g		1-4行行首的#删除
​			:1,4s/^/\/\//g	1-4行行首添加//
​		:ab mymail zs@163.com		替换
​		

		家目录下 .vimrc 文件是 vim 配置文件