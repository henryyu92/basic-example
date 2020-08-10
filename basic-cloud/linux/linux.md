```
UNIX主要发行版本
	操作系统		公司		硬件平台
	--------------------------------------
	AIX				IBM			PowerPC
	HP-UX			HP			RA-RISC
	Solaris			Sun			SPARC
	Linux						Intel/AMD
Linux内核官网：www.kernel.org
Linux 发行版本
	redhat 系
		centos redhat fedoro suse gentoolinux
	debian 系
		debian ubuntu
Linux 的特点
	Linux 严格区分大小写
	Linux 不靠扩展名区分文件类型，但是会有一些约定俗成的扩展名
		压缩包			*.gz/*.bz2/*.tar.bz2/*.tgz等
		二进制软件包	*.rpm
		网页文件		*.html/*
系统分区
	磁盘分区：把大硬盘分为小的逻辑分区
		磁盘分区是使用分区编辑器(partition editor)在磁盘上划分几个逻辑部分。
		碟片一旦划分成数个分区(Patition)，不同类的目录与文件可以存储进不同的分区。
		分区类型：
			主分区：最多只能有4个
			扩展分区：
				最多只能有1个；
				主分区加扩展分区最多有4个；
				不能写入数据，只能包含逻辑分区；
			逻辑分区
	硬件设备文件名：给每个分区定义设备文件名(分区的同时会给每个分区指定设备文件名)
		IDE硬盘					/dev/hd[a-d]
		SCSI/SATA/USB硬盘		/dev/sd[a-p]
		光驱					/dev/cdrom或/dev/hdc
		软盘					/dev/fd[0-1]
		打印机(25针)			/dev/lp[0-2]
		打印机(USB)				/dev/usb/lp[0-15]
		鼠标					/dev/mouse
		分区设备文件名
			/dev/had1		IDE硬盘接口
			/dev/sda1		SCSI硬盘接口、SATA硬盘接口
	格式化：写入文件系统
		格式化(高级格式化)又称为逻辑格式化，它是根据用户选定的文件系统(fat16/fat32/ntfs/ext2/ext3/ext4等)，在磁盘的特定区域写入特定数据，在分区中划出一片用于存放文件分配表、目录表等用于文件管理的磁盘空间
	挂载：给每个分区分配挂载点
		必需分区：
			/			根分区
			/swap		交换分区，内存2倍，不超过2GB
		推荐分区：
			/boot		启动分区，200MB
			
Linux 各目录的作用
	/bin/				存放系统命令的目录，普通用户和超级用户都可以执行。不过放在/bin下的命令在单用户模式下也可以执行
	/sbin/				保存和系统环境设置相关的命令，只有超级用户可以使用这些命令进行系统环境设置，但是有些命令可以允许普通用户查看
	/usr/bin/			存放系统命令的目录，普通用户和超级用户都可以执行。这些命令和系统启动无关，在单用户模式下不能执行
	/usr/sbin/			存放根文件系统不必要的系统管理命令，例如多数服务程序。只有超级用户可以使用
	/boot/				系统启动目录，保存系统启动相关的文件，如内核文件和启动引导程序(grub)文件等
	/dev/				设备文件保存位置
	/etc/				配置文件保存位置，系统内所有默认安装方式(rpm安装)的服务的配置文件全部都保存在这个目录当中，如用户账号和密码，服务的启动脚本，常用服务的配置文件等
	/home/				普通用户的家目录。建立每个用户时，每个用户要有一个默认登陆位置，这个位置就是这个用户的家目录，所有普通用户的家目录就是在 /home 下建立一个和用户名相同的目录。如用户user1的家目录就是 /home/user1
	/lib/				系统调用的函数库保存位置
	/lost+found/		当系统意外崩溃或机器意外关机而产生一些文件碎片会存放在这里。当系统启动的过程中 fsck 工具会检查这里，并修复已经损坏的文件系统。这个目录只在每个分区中出现，例如 /lost+found 就是根分区的备份恢复目录，/boot/lost+found 就是 /boot 分区的备份恢复目录
	/media/				挂载目录。系统建议是用来挂载媒体设备，例如软盘和光盘
	/mnt/				挂载目录，早期 Linux 中只有这一个挂载目录，并没有细分。现在这个目录系统建议挂载额外设备，如U盘、移动硬盘和其他操作系统的分区
	/misc/				挂载目录。系统建议用来挂在NFS服务的共享目录。只要是一个已经建立的空目录就可以作为挂载点，系统虽然准备了三个默认挂载目录/media /mnt /misc 但是到底在哪个目录中挂在什么设备都可以由管理员自己决定			
	/opt/				第三方安装的软件保存位置。这个目录就是放置和安装其他软件的位置，手工安装的源码包软件都可以安装到这个目录当中。/user/local/目录也可以用来安装软件，也更推荐安装在该目录下(行业默认习惯)
	/proc/				虚拟文件系统，该目录中的数据并不保存到硬盘当中，而是保存到内存当中。主要保存系统的内存，进程，外部设备状态和网络状态等。如/proc/cpuinfo是保存CPU信息的，/proc/devices是保存设备驱动的列表的，/proc/filesystems是保存文件系统列表的，/proc/net/是保存网络协议信息的
	/sys/				虚拟文件系统。和/proc 目录相似，都是保存在内存当中的，主要是保存于内核相关信息的
	/root/				超级用户的家目录。普通用户家目录在 /home 下，超级用于家目录直接在 / 下
	/srv/				服务数据目录。一些系统服务启动之后，可以在这个目录中保存所需要的数据
	/tmp/				临时目录。系统存放临时文件的目录，该目录下所有用户都可以访问和写入。建议此目录中不能保存重要数据，最好每次开机都把该目录清空
	/usr/				系统软件资源目录。系统中安装的软件大多数保存在这里 /usr/local/
	/var/				动态数据保存位置。主要保存缓存、日志以及软件运行所产生的文件
	
Linux 常用命令
	文件处理命令
		命令格式与目录处理命令ls
			命令格式：命令 [-选项] [参数]
			说明：
				个别命令使用不遵顼此格式；
				当有多个选项时，可以写在一起；
				简化选项与完整选项 -a 等于 --all
			ls
				命令所在路径：/bin/ls
				执行权限：所有用户
				功能描述：显示目录文件
				语法：ls [-选项] [文件或目录]
					-a		显示所有文件，包括隐藏文件(.file_name)
					-l		详细信息显示
					-d		查看目录属性
					-i		查看文件的 inode
					
			-rw------. 1 root root 1205 3月 3 08:10 anaconda-ks.cfg
				1		引用计数
				root	所有者
				root	所属组
				1205	文件大小(字节)
				3月 3 08:10		最后修改时间
				anaconda-ks.cfg	文件名称
			-rw-r--r-x	文件的权限
				-		文件类型
					-	文件
					d	目录
					l	软链接文件
					c	字符设备文件
					b	块设备文件
					s	套接字文件
					p	管道符文件
					
				rw-		u所有者
				r--		g所属组
				r-x		o其他人
				r		读
				w		写
				x		执行
		目录处理命令
			mkdir
				命令所在路径：/bin/mkdir
				执行权限：所有用户
				功能描述：创建新目录
				语法：mkdir [-选项] [目录名]
					-p		递归创建(上级目录不存在则创建)
					
				mkdir -p /tmp/java/class			创建 /tmp/java/class 目录，如果 /tmp/java/ 不存在则会自动创建
				mkdir /tmp/java/class				创建 /tmp/java/class 目录，如果 /tmp/java/ 不存在会报错(No such file or directory)
				mkdir -p /tmp/java/ /tmp/redis/		创建 /tmp/java/ 和 /tmp/redis/ 目录
			pwd
				命令所在路径：/bin/pwd
				执行权限：所有用户
				功能描述：显示当前目录
				语法：pwd
			cd
				命令所在路径：shell内置命令
				执行权限：所有用户
				功能描述：切换目录
				语法：cd [目录]
					cd /tmp/java	切换到 /tmp/java 目录
					cd ..			回到上一级目录
					cd ~			回到家目录
					cd /			切换到根目录
					cd .			当前目录
			rmdir
				命令所在路径：/bin/rmdir
				执行权限：所有用户
				功能描述：删除空目录，如果目录不为空则会报错(Directory not empty)
				语法：rmdir [目录名]
				
				rmdir /tmp/java/class		删除 /tmp/java/class 目录
			dirname 从文件名中删除最后一个组件
				dirname /usr/local			/usr
				dirname /home/test/test.sh 	/home/test/
		文件处理命令
			touch
				命令所在路径：/bin/touch
				执行权限：所有用户
				功能描述：创建空文件
				语法：touch [文件名]
				
				touch /tmp/java/class/hello.class		创建单个文件
				touch /tmp/java/class/hello.class /tmp/redis/readme.txt		创建多个文件
			cp
				命令所在路径：/bin/cp
				执行权限：所有用户
				功能描述：复制文件或目录
				语法：cp [-rp] [源文件或源目录]... [目标目录]
					-r		复制目录
					-p		保留文件属性(文件最后修改时间...)
					
				cp -r /tmp/java/class/ /tmp/redis/						将 /tmp/java/class/ 目录复制到 /tmp/redis/ 下
				cp -rp /tmp/java/class/ /tmp/redis/config/ /tmp/ngix/	将 /tmp/java/class/ 目录和 /tmp/redis/config/ 目录复制到 /tmp/ngix/ 下并保存目录属性，如果两个目录相同则会覆盖
				cp -rp /tmp/java/class/ /tmp/redis/redis-class/			将 /tmp/java/class/ 目录改名为 /tmp/redis/redis-class/ 后复制
				cp -p /tmp/java/class/hello.class /tmp/redis/			将 /tmp/java/class/hello.class 复制到 /tmp/redis/ 目录下
				cp -p /tmp/java/class/hello.class /tmp/java/class/world.class /tmp/redis/		复制多个文件
			mv
				命令所在路径：/bin/mv
				执行权限：所有用户
				功能描述：剪切文件/改名(相同目录则改名，不同目录则剪切)
				语法：mv [源文件/目录] [目标目录/文件]
				
				mv /tmp/java/class /tmp/ngix/		剪切 /tmp/java/class 目录到 /tmp/ngix/ 目录下
				mv /tmp/java/class/hello.class /tmp/java/class/world.class	文件改名
				mv /tmp/java/class/hello.class /tmp/redis/				剪切文件
			rm
				命令所在路径：/bin/rm
				执行权限：所有用户
				功能描述：删除文件
				语法：rm -rf [文件或目录]
					-r		删除目录(逐个询问是否删除目录下的文件)
					-f		强制执行
					
				rm /tmp/java/class/hello.class /tmp/java/class/world.class	删除多个文件
				rm -rf /tmp/java/class/			删除目录下的内容（包括文件和目录）
			cat
				命令所在路径：/bin/cat
				执行权限：所有用户
				功能描述：显示文件内容(不太适合内容比较多的文件)
				语法：cat -n [文件名]
					-n		显示行号
					
				cat /etc/issue
				cat -n /etc/issue
			tac
				命令所在路径：/usr/bin/tac
				执行权限：所有用户
				功能描述：显示文件内容(反向列示)
				语法：tac [文件名]
				
				tac /etc/issue
			more
				命令所在路径：/bin/more
				执行权限：所有用户
				功能描述：分页显示文件内容
				语法：more [文件名]
					(空格)/f		翻页
					(Enter)			换行
					q/Q				退出
					
				more /etc/services
			less
				命令所在路径：/usr/bin/less
				执行权限：所有用户
				功能描述：显示文件内容(可向上翻页)
				语法：less [文件名]
					空格		翻页
					enter		换行
					q			退出
					pageUp		往上翻页 pageDown 往下翻页
					上箭头		向上换行 下箭头 向下换行
					/关键词		搜索关键词；n 下一个匹配的关键字
				less /etc/services
			head
				命令所在路径：/usr/bin/head
				执行权限：所有用户
				功能描述：显示文件前面几行内容
				语法：head -n [文件名]
					-n		指定行数
					
				head -n 20 /etc/services
			tail
				命令所在路径：/usr/bin/tail
				执行权限：所有用户
				功能描述：显示文件后面几行内容
				语法：cat -nf [文件名]
					-n		指定显示的行数
					-f		动态显示文件末尾内容
					
				tail -f -n 18 /etc/services
		链接命令
			ln
				命令所在路径：/bin/ln
				执行权限：所有用户
				功能描述：生成链接文件
				语法：ln -s [源文件] [目标文件]
					-s		创建软链接
				软链接特征：
					操作软连接实际上操作的是源文件(类似 Windows 的快捷方式)
					软链接文件权限都为：rwxrwxrwx 实际权限由所指向的文件的权限决定
					文件很小，只是符号链接
					形式：/tmp/issue.soft->/etc/issue	箭头指向源文件
					源文件丢失，软连接不能操作
				硬链接特征：
					同步更新，源文件或者硬链接文件发生改变时，两个文件都会更新
					源文件丢失，硬链接文件依然存在
					可通过i节点识别(i节点相同，一个文件必须对应一个i节点，一个i节点可对应多个文件)；
					不能跨分区；
					不能针对目录使用；
					
				ln -s /etc/issue /tmp/issue.soft	创建软链接
				ln /etc/issue /tmp/issue.hard		创建硬链接
				
				删除软链接：
					rm -rf /soft_link_dir
					注意：
						删除软链接时，如果路径后面带 / 则会将源文件也删除
							rm -rf /mysql.s			删除软链接而不会删除源文件
							rm -rf /mysql.s/		删除软链接的同时会删除源文件
				
	权限管理命令
		chmod
			命令所在路径：/bin/chmod
			执行权限：所有用户(所有者和root)
			功能描述：改变文件或目录权限
			语法：chmod [{ugoa}{+-=}{rwx}] [文件或目录]
						-R		递归修改
						
				u		所属者
				g		所属组
				o		其他用户
				a		全部用户
				
				+		增加权限
				-		减少权限
				=		更改权限
			权限的数字表示：
				r		4
				w		2
				x		1
				
			chmod g+w testfile		增加文件 testfile 所属组写权限
			chmod u+x,g=rw-,o-r testfile	同时修改所有者、所属组、其他用户的权限
			chmod -R 777 testdir	修改目录testfile及其目录下文件为所有用户具有全部权限
		
									文件								目录
			-------------------------------------------------------------------------------------
			r		读权限		可以查看文件内容				可以列出目录中的内容
								(cat tac more less head tail)	(ls)
			w		写权限		可以修改文件内容				可以在目录中创建目录、删除目录、创建文件、删除文件
								(vim)							(mkdir rmdir touch rm cp mv)
			x		执行权限	可执行文件						可以进入目录
								(sh)							(cd)
			--------------------------------------------------------------------------------------
		chown
			命令所在路径：/bin/chown
			执行权限：所有用户(root)
			功能描述：改变文件或目录的所有者
			语法：chown [用户] [文件或目录]
			
			chown zs test.tmp		改变文件test.tmp的所有者为zs
		chgrp
			命令所在路径：/bin/chgrp
			执行权限：所有用户(root)
			功能描述：改变文件或目录的所属组
			语法：chgrp [用户组] [文件或目录]
			
			chgrp guest test.tmp	改变文件test.tmp的所属组为guest
		umask
			命令所在路径：Shell内置命令
			执行权限：所有用户
			功能描述：显示、设置文件的缺省权限(默认755)
			语法：umask [-S]
				-S		以rwx形式显示新建文件缺省权限 rwxr-xr-x
				umask	显示缺省权限022 (777-022=755)
			Linux 默认新建的文件不具有可执行权限
			umask 023	设置缺省权限(777-023=754 rwxr--r--)
	文件索搜命令
		find
			命令所在路径：/bin/find
			执行权限：所有用户
			功能描述：文件搜索
			语法：find [搜索范围] [匹配条件]
			
			find /etc -name init	在目录/etc中查找文件init(完全匹配)
			find /etc -name *init*	在目录/etc中查找文件名包含init的文件(模糊匹配)
			find /etc -name init?
				-iname	不区分大小写
			find / -size +204800	在根目录下查找大于100MB(204800*0.5B=100MB)的文件
				1数据块=512B=0.5KB
				+n		大于n数据块
				-n		小于n数据块
				n		等于n数据块
			find /home -user zs		在/home目录下查找所有者为zs的文件
				-group	根据所属组查找
			find /etc -cmin -5		在/etc下查找5分钟内被修改过属性(ls查看到的)的文件和目录
				+5		5分钟外
				-5		5分钟内
				-amin	访问时间 access
				-cmin	文件属性 change
				-mmin	文件内容 modify
			find /etc -size +163840 -a -size -204800	在/etc下查找大于80MB小于100MB的文件
				-a		两个条件同时满足
				-o		两个条件满足一个即可
			find /etc -type d		在 /etc/ 下查找目录
				f		文件
				d		目录
				l		软链接文件
			find /etc -inum 1034	在 /etc/ 下查找i节点为1034的文件
			
			find /etc -name inittab -exec ls -l {} \;	在 /etc/ 下查找init文件并显示其详细信息
				-exec 命令 {} \;	对索搜结果执行操作(没有询问)
				-ok 命令 {} \;		对索搜结果执行操作(有询问)
		locate
			命令所在路径：/usr/bin/locate
			执行权限：所有用户
			功能描述：在文件资料库中查找文件(速度快)
			语法：locate 文件名
			
			locate 查找不到 /tmp 下的文件
			系统会定时自动更新文件资料库，所以不在文件资料库的文件搜索不到
			updatedb		更新文件资料库
			
			locate init
			locate -i init	不区分大小写
		which
			命令所在路径：/usr/bin/which
			执行权限：所有用户
			功能描述：搜索命令所在目录及别名信息
			语法：which 命令
			
			which ls
		whereis
			命令所在路径：/usr/bin/whereis
			执行权限：所有用户
			功能描述：索搜命令所在目录及帮助文档路径
			语法：whereis [命令名称]
			whereis ls
		grep
			命令所在路径：/bin/grep
			执行权限：所有用户
			功能描述：在文件中搜寻字符串匹配的行并输出
			语法：grep -iv [指定字符串] [文件]
				-i		不区分大小写
				-v		排除指定字符串
				-c		统计匹配的行数
				
			grep -i mysql /root/install.log
			grep -v ^# /etc/inittab		查找不是以#开头的行
	帮助命令
		man
			命令所在路径：/usr/bin/man
			执行权限：所有用户
			功能描述：获得帮助信息
			语法：man [命令或配置文件]
			
			man ls			查看ls命令的帮助信息
			man services	查看配置文件services的帮助信息(不需要绝对路径)
			
		whatis	命令			查看命令的简短信息
		apropos 配置文件名称	查看配置文件的简短信息
		命令 --help				查看命令的选项信息
		info 命令
		
		help
			命令所在路径：Shell内置命令
			执行权限：所有用户
			功能描述：获得Shell内置命令的帮助信息
			语法：help 命令
			
			help umask			查看umask命令的帮助信息
		
	用户管理命令
		useradd
			命令所在路径：/usr/sbin/useradd
			执行权限：root
			功能描述：添加新用户
			语法：useradd 用户名
			
			useradd zs
		passwd
			命令所在路径：/usr/bin/passwd
			执行权限：所有用户
			功能描述：设置用户密码
			语法：passwd 用户名
			passwd zs
		who
			命令所在路径：/usr/bin/who
			执行权限：所有用户
			功能描述：查看登陆用户信息
			语法：who
			who
			
			root	tty1	2014-03-11 18:29
			root	pts/0	2014-03-14 09:27 (192.168.191.1)
			zs		pts/1	2014-03-14 10:50 (192.168.198.1)
				root		登陆用户名(一个用户可多次登陆)
				tty			登陆终端(本地终端)
				pts			登陆终端(远程终端)
				2014-03-11 18:29	登录时间
				192.168.191.1	远程终端的IP地址
		whoami
			当前用户
		w
			命令所在路径：/usr/bin/w
			执行权限：所有用户
			功能描述：查看登陆用户详细信息
			语法：w
			w
	压缩解压缩命令
		gzip
			命令所在路径：/bin/gzip
			执行权限：所有用户
			功能描述：压缩文件
			压缩后的文件格式：.gz
			语法：gzip [文件]
			
			gzip test.tmp	只能压缩文件，不能压缩目录
		gunzip
			命令所在路径：/bin/gunzip
			执行权限：所有用户
			功能描述：解压缩.gz的压缩文件
			语法：gunzip [压缩文件]
			gunzip test.gz
		tar
			命令所在路径：/bin/tar
			执行权限：所有用户
			功能描述：打包目录
			压缩后文件格式：.tar.gz
			语法：tar [-zcf] [压缩后文件名] [目录]
				-c			打包
				-v			显示详细信息
				-f			指定文件名
				-z			压缩
			tar -zcf demo.tar.gz demo		将目录demo打包并压缩为demo.tar.gz文件
			tar命令解压缩：
				-x			解包
				-v			显示详细信息
				-f			指定解压缩文件
				-z			解压缩
				-C			指定解压缩后文件的目录
			tar -zxf demo.tar.gz -C /usr/local
		zip
			命令所在路径：/usr/bin/zip
			执行权限：所有用户
			功能描述：压缩文件或目录
			压缩后文件格式：.zip
			语法：zip [-r] [压缩后文件名] [文件或目录]
				-r			压缩目录
			zip demo.zip demo.tmp		压缩文件
			zip test.zip test			压缩目录
		unzip
			命令所在路径：/usr/bin/unzip
			执行权限：所有用户
			功能描述：解压缩.zip的压缩文件
			语法：unzip [压缩文件]
			unzip test.zip
		bzip2
			命令所在路径：/usr/bin/bzip2
			执行权限：所有用户
			功能描述：压缩文件
			压缩后文件格式：.bz2
			语法：bzip2 [-k] [文件]
				-k		产生压缩文件后保留源文件
			bzip2 -k demo.tmp
			tar -jcf test.tar.bz2 test
		bunzip2
			命令所在路径：/usr/bin/bunzip2
			执行权限：所有用户
			功能描述：解压缩
			语法：bunzip2 [-k] [压缩文件]
				-k		解压缩后保留源文件
			bunzip2 -k demo.bz2		压缩文件
			tar -jxf test.tar.bz2	打包目录并压缩
	网络命令
		write
			命令所在路径：/usr/bin/write
			执行权限：所有用户
			功能描述：给在线用户发信息，以Ctrl+D保存结束
			语法：write <用户名>
			write zs
		wall
			命令所在路径：/usr/bin/wall
			执行权限：所有用户
			功能描述：发广播信息
			语法：wall [message]
			wall hello world !
		ping
			命令所在路径：/bin/ping
			执行权限：所有用户
			功能描述：测试网络连通性
			语法：ping 选项 IP地址
				-c		指定发送次数
			ping 192.168.1.123
			ping -c 3 192.168.1.123
		ifconfig
			命令所在路径：/sbin/ifconfig
			执行权限：root
			功能描述：查看和设置网卡信息
			语法：ifconfig 网卡名称 IP地址
			ifconfig		查看本地网卡
				eth0	本地真实网卡
				lo		虚拟网卡，IP地址 127.0.0.1
			ifconfig eth0					查看网卡信息
			ifconfig eth0 192.168.1.250		设置网卡IP
			ifconfig eth0 up|down			启动|关闭网卡
		mail
			命令所在路径：/bin/mail
			执行权限：所有用户
			功能描述：查看发送电子邮件
			语法：mail [用户名]
			
			mail root	给 root 发送邮件，Ctrl+D 结束，即使root没有登陆也能发送
			mail		查看邮件列表
		last
			命令所在路径：/usr/bin/last
			执行权限：所有用户
			功能描述：列出目前与过去登入系统的用户信息
			语法：last
			last
		lastlog
			命令所在路径：/usr/bin/lastlog
			执行权限：所有用户
			功能描述：列出所有用户的最后登陆信息
			语法：lastlog
			lastlog -u 502		根据uid查看用户登录信息
		traceroute
			命令所在路径：/bin/traceroute
			执行权限：所有用户
			功能描述：显示数据包到主机间的路径
			traceroute www.baidu.com
		netstat
			命令所在路径：/bin/netstat
			执行权限：所有用户
			功能描述：显示网络相关信息
			语法：netstat [选项]
				-t			TCP协议
				-u			UDP协议
				-l			监听
				-r			路由
				-n			显示IP地址和端口
				-p			显示运行的程序
				-a			所有网络连接
			netstat -tlun		查看本机监听的端口
			netstat -an			查看本机所有的网络连接
			netstat -rn			查看本机路由表
		setup
			命令所在路径：/usr/bin/setup
			执行权限：root
			功能描述：配置网络(redhat 系列专有)
			语法：setup
			setup
		ip
			从 CentOS7 开始，最小安装的网络命令都集成为 ip
			功能描述：显示、操纵 路由(routing) 设备(devices) 策略路由(policy routing) 管道(tunnel)
			语法：
				ip [options] object {command|help}
				options:
					-s			显示统计数据
				object:
					link		网络设备						
					addr		IPv4 或 IPv6 地址
					route		路由相关
			ip link
				ip [-s] link show				查看网卡信息
					ip -s link show
				ip link set [device] [动作与参数]	设置网卡
					动作与参数：
						up/down		启动/关闭网卡
						address		修改网卡 MAC 地址
						name		给予网卡特殊的名字
						mtu			设置最大传输单元，单位是字节
					ip link set eth0 down
					ip link set eth0 mtu 1000
			ip addr
				ip [-s] addr show		查看 IP 信息
					ip -s addr show
				ip addr [add|delete] [IP参数] [dev 网卡名称] [相关参数]
					add/delete		增加/删除IP
					IP 参数			IP 地址 例如 192.168.100.100/24
					网卡名称		例如 eth0 eth1
					相关参数：
						broadcast		设置广播地址，如果是 + 表示自动计算
						label			设备别名
						scope			global	允许来自所有源的连接
										site	仅支持 IPv6 仅允许本主机连接
										link	仅允许本设备自我连接
										host	仅允许本主机内部的连线
					ip addr add 192.168.50.50/24 broadcast + dev eth0 label eth0:hello scope global
					ip addr del 192.168.50.50/24 dev eth0
			ip route
				ip route show
				ip route [add|del] [IP或路由] [via 网关] [dev 网卡] [mtu]
					add|del			增加或删除路由
					IP或路由		可使用 192.168.50.0/24 之类的
					via				从哪个网关出去
					dev				从哪个网卡连出去
					mtu				最大传输单元
				ip route add 192.168.10.0/24 via 192.168.5.100 dev eth0
		ss
			ss 命令用来显示处于活动状态的套接字信息。ss 命令可以用来获取 socket 统计信息，它可以显示和 netstat 类似的内容。但 ss 的优势在于它能够显示更多详细的有关 TCP 和连接状态的信息，而且比 netstat 更快更高效
			语法：
				ss [选项]
					选项：
						-h			显示帮助信息
						-V			显示指令版本信息
						-n			不解析服务名称，以数字方式显示
						-a			显示所有的套接字
						-l			显示处于监听状态的套接字
						-o			显示计时器信息
						-m			显示套接字的内存使用情况
						-p			显示使用套接字的进程信息
						-i			显示内部的 TCP 信息
						-4			只显示 ipv4 的套接字
						-6			只显示 ipv6 的套接字
						-t			只显示 tcp 套接字
						-u			只显示 udp 套接字
						-d			只显示 DCCP 套接字
						-w			只显示 RAW 套接字
						-x			仅显示 UNIX 域套接字
	挂载命令
		mount
			命令所在路径：/bin/mount
			执行权限：所有用户
			命令语法：mount [-t 文件系统] 设备文件名 挂载点
			
			mount -t iso9660 /dev/sr0 /mnt/cdrom		挂载光盘
			umount /dev/sr0		卸载光盘
	关机重启命令
		shutdown [选项] 时间
			-c		取消前一个关机命令
			-h		关机
			-r		重启
		shutdown -h now		马上关机
		关机：
			halt
			poweroff
			init 0
		重启：
			reboot
			init 6
		系统运行级别：
			0			关机
			1			单用户
			2			不完全多用户，不含NFS服务(文件共享服务)
			3			完全多用户
			4			未分配
			5			图形界面
			6			重启
		cat /etc/inittab	修改系统默认运行级别
			id:3:initdefault:
		runlevel		查询系统运行级别
		logout			退出登录
	系统命令：
		hostname		查看主机名
			hostname hadoop			修改主机名(关机后无效，永久有效可以修改 /etc/sysconfig/network 配置文件)
		ifconfig		查看ip
			ifconfig 网卡名 ip地址		修改ip(关机后无效，永久有效修改 /etc/sysconfig/network-scripts/ifcfg-网卡名 配置文件)
		uname -a		查看系统与内核相关信息
		date			查看当前日期
			date +%Y-%m-%d			YYYY-mm-dd 格式显示日期
			date +%T
			date +%Y-%m-%d" "%T
		file filename		查看文件信息

文本编辑器Vim	www.vim.org
	Vim简介
		Vim是一个功能强大的全屏幕文本编辑器，是Linux/UNIX上最常用的文本编辑器，它的作用是建立、编辑、显示文本文件
		Vim没有菜单，只有命令
	Vim工作模式
		进入命令模式：vi file_name
			命令模式中所有的输入都是命令
		退出命令模式：:wq
		从命令模式进入插入模式：iao
			插入模式可以编辑文件
		从插入模式返回命令模式：ESC键
		从命令模式进入编辑模式：:
		从编辑模式返回命令模式：命令以回车结束运行
	Vim常用操作
		插入命令
			a			在光标所在字符后插入
			A			在光标所在行尾插入
			i			在光标所在字符前插入
			I			在光标所在行行首插入
			o			在光标下插入新行
			O			在光标上插入新行
		定位命令
			:set nu		设置行号
			:set nonu	取消行号
			gg			到第一行
			G			到最后一行
			nG			到第n行
			:n			到第n行
			$			移至行尾
			0			移至行首
		删除命令
			x			删除光标所在处字符
			nx			删除光标所在处后n个字符
			dd			删除贯标所在行，ndd删除n行
			dG			删除光标所在行到文件末尾内容
			D			删除光标所在处到行尾内容
			:n1,n2d		删除指定范围的行
		复制和剪切命令
			yy			复制当前行
			nyy			复制当前行以下n行
			dd			剪切当前行
			ndd			剪切当前行以下n行
			p P			粘贴在当前光标所在行下或行上
			v			进入视觉模式，可以选择部分字符
			shift+v		进入行选择模式，可以选择多行
			ctrl+v		进入块选择模式，可以选择一块字符
		替换和取消命令
			r			取代光标所在处字符
			R			从光标所在处开始替换字符，按Esc结束
			u			取消上一步操作
		搜索和搜索替换命令
			/string					搜索指定字符串，搜索时忽略大小写 :set ic
			n						索搜指定字符串的下一个出现位置
			:%s/old/new/g			全文替换指定字符串
			:n1,n2s/old/new/g		在一定范围内替换指定字符串
		保存和退出命令
			:w						保存修改
			:w new_filename			另存为指定文件
			:wq						保存修改并退出
			ZZ						快捷键，保存修改并退出
			:q!						不保存修改退出
			:wq!					保存修改并退出(文件所有者及root可使用)
	Vim使用技巧
		:r 文件名					导入文件的内容
		:r !命令					导入命令执行结果
		:map 快捷键 触发命令		定义快捷键
			:map ^P I#<ESC>
			:map ^B 0x
		:n1,n2s/^/#/g				连续行注释
			:1,4s/^/#/g		1-4行行首加上#
			:1,4s/#//g		1-4行所有的#删除
			:1,4s/^#//g		1-4行行首的#删除
			:1,4s/^/\/\//g	1-4行行首添加//
		:ab mymail zs@163.com		替换
		
		家目录下 .vimrc 文件是 vim 配置文件

Linux 软件安装
	软件包管理简介
		软件包分类
			源码包(脚本安装包)：
				源码包的优点：
					开源，如果有足够的能力，可以修改源代码；
					可以自由选择所需的功能；
					软件是编译安装，所以更加适合自己的系统，更加稳定也更高效；
					卸载方便；
				源码包的缺点：
					安装过程步骤较多，尤其安装较大的软件集合时，容易出现拼写错误
					编译过程时间较长，安装比二进制安装时间长；
					因为是编译安装，安装过程中一旦报错新手很难解决；
			二进制包(RPM包、系统默认包)
				二进制包的优点：
					包管理系统简单，只通过几个命令就可以实现包的安装、升级、查询和卸载；
					安装速度比源码包安装快的多；
				二进制包的缺点：
					经过编译，不再可以看到源代码；
					功能选择不如源码包灵活；
					依赖性；
	RPM包管理-rpm命令管理
		RPM包命名原则
			httpd-2.2.15-15.e16.centos.1.i686.rpm
				httpd			软件包名
				2.2.15			软件版本
				15				软件发布的次数
				e16.centos		适合的Linux平台
				i686			适合的硬件平台
				rpm				rpm包扩展名
		RPM包依赖性
			树形依赖：a-->b-->c
			环形依赖：a-->b-->c-->a
			模块依赖：模块依赖查询网站 www.rpmfind.net
		包全名与包名
			包全名：操作的包是没有安装的软件包时，使用包全名，而且要注意路径
			包名：操作已经安装的软件包时，使用包名，是搜索/var/lib/rpm中的数据库
		RPM安装
			rpm -ivh 包全名
			选项：
				-i			install(安装)
				-v			verbose(显示详细信息)
				-h			hash(显示进度)
				--nodeps	不检测依赖性
		RPM包升级
			rpm -Uvh 包全名
			选项：
				-U			upgrade(升级)
		RPM包卸载
			rpm -e 包名
			选项：
				-e			卸载
				--nodeps	不检查依赖性
		查询是否安装
			rpm -q 包名
			选项：
				-q			查询
			rpm -qa		查询所有已经安装的RPM包
				-a			所有
		查询软件包的详细信息
			rpm -qi 包名
			选项：
				-i			查询软件信息
				-p			查询未安装包信息
		查询包中文件安装位置
			rpm -ql 包名
			选项：
				-l			列表
				-p			查询未安装包信息
		查询系统文件属于哪个RPM包
			rpm -qf 系统文件名
			选项：
				-f			查询系统文件属于哪个软件包
		查询软件包的依赖性
			rpm -qR 包名
			选项：
				-R			查询软件包的依赖性
				-p			查询未安装包信息
		RPM包校验
			rpm -V 包名		校验软件包安装后是否被修改，如果有修改返回校验信息
			选项：
				-V			校验指定RPM包中的文件
			验证信息中的8个信息的具体内容：
				S			文件大小是否改变
				M			文件的类型或文件的权限是否被改变
				5			文件MD5校验和是否改变(文件内容是否改变)
				D			设备从代码是否改变
				L			文件路径是否改变
				U			文件的属主(所有者)是否改变
				G			文件的属组是否改变
				T			文件的修改时间是否改变
			文件类型
				c			配置文件
				d			普通文件
				g			ghost file，该文件不应该被这个RPM包含
				l			授权文件
				r			描述文件
			
			rpm -V httpd
		RPM包中文件提取
			rmp2cpio 包全名 | cpio -idv .文件绝对路径
				rpm2cpio		将rmp包转换为cpio格式
				cpio			是一个标准工具，它用于创建软件档案文件和从档案文件中提取文件
			cpio 选项 < [设备|文件]
				选项：
					-i:			copy-in模式，还原
					-d:			还原时自动新建目录
					-v:			显示还原过程
			rpm -qf /bin/ls			查询ls命令属于哪个软件包
			mv /bin/ls /tmp/		造成ls命令误删除假象
			rpm2cpio /mnt/cdrom/Packages/coreutils-8.4-19.e16.i686.rmp | cpio -idv ./bin/ls			提取RPM包中的ls命令到当前目录的/bin/ls下
			cp /root/bin/ls /bin/	把ls命令复制回/bin/目录，修复文件丢失
	RPM包管理-yum在线管理
		IP地址配置和网络yum源
			IP地址配置
				setup			使用setup工具
				vi /etc/sysconfig/network-scripts/ifcfg-eth0 把ONBOOT="no"改为ONBOOT="yes"	启动网卡
				services network restart	重启网络服务
			网络yum源
				vi /etc/yum.repos.d/CentOS-Base.repo
					[base]				容器说明，一定要放在[]中
					name				容器说明，可以自己随便写
					mirrorlist			镜像站点，这个可以注释掉
					baseurl				我们的yum源服务器的地址。默认是CentOS官方的yum源服务器是可以使用的，也可以更改
					enabled				此容器是否生效，如果不写或写成enable=1都是生效，写成enable=0就是不生效
					gpgcheck			如果是1是指RPM的数字证书生效，如果是0则不生效
					gpgkey				数字证书的公钥文件保存位置。不用修改
			yum命令
				查询
					yum list				查询所有可用软件包列表
					yum serche 关键字		搜索服务器上所有和关键字相关的包
				安装
					yum -y install 包名
					选项：
						install		安装
						-y			自动回答yes
				升级
					yum -y update 包名
					选项：
						update		升级
						-y			自动回答yes
				卸载
					yum -y remove 包名
					选项：
						remove		卸载
						-y			自动回答yes
				yum grouplist					列出所有可用的软件组列表
				yum groupinstall 软件组名		安装指定软件组，组名可以由groupplist查询出来
				yum groupremove 软件组名		卸载指定软件组
			yum 源管理
				列出所有 yum 源：
					yum repolist all
				查看启用的 yum 源：
					yum repolist enabled
				查看禁用的 yum 源：
					yum repolist disabled
				禁用 yum 源
					yum-config-manager --disable yum_repository_name
						yum-config-manager --disable docker-ce.repo
				启动 yum 源：
					yum-config-manager --enable yum_repository_name
						yum-config-manager --enable docker-ce.repo
				添加 yum 源：
					yum-config-manager --add-repo yum_repository_url
						yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
		光盘yum源搭建
			光盘yum源搭建步骤：
				1.挂载光盘 mount /dev/cdrom /mnt/cdrom/
				2.让网络yum源文件失效
					cd /etc/yum.repos.d/
					mv CentOS-Base.repo CentOS-Base.repo.bak
					mv CentOS-Base-Debuginfo.repo CentOS-Base-Debuginfo.repo.bak
					mv CentOS-Vault.repo CentOS-Vault.repo.bak
				3.修改光盘yum源文件
					vim CentOS-Media.repo
					[c6-media]
					name=CentOS-$releasever - Media
					baseurl=file:///mnt/cdrom
					gpgcheck=1
					enabled=1
					gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-CentOS-7
	源码包管理
		源码包和RPM包的区别
			安装之前的区别：概念上的区别
			安装之后的区别：安装位置的不同
			RPM包默认安装位置：
				/etc/					配置文件安装目录
				/usr/bin/				可执行的命令安装目录
				/usr/lib/				程序所使用的函数库保存位置
				/usr/share/doc/			基本的软件使用手册保存位置
				/usr/share/man/			帮助文件保存位置
			源码包安装在指定位置当中，一般是 /usr/local/软件名/
			安装位置不同带来的影响：
				RPM包安装的服务可以使用系统服务管理命令(service)来管理，例如RPM包安装的apache的启动方法是：
					/etc/rc.d/init.d/httpd start
					service httpd start
				源码包安装的服务则不能被服务管理命令管理，因为没有安装到默认路径中。所以只能用绝对路径进行服务的管理，如：
					/usr/local/apache2/bin/apachectl start
		源码包安装过程
			1.安装准备
				安装C语言编译器
				下载源码包 http://mirror.bit.edu.cn/apache/httpd/
			2.安装注意事项
				源代码保存位置：/usr/local/src/
				软件安装位置：/usr/local/
				如何确定安装过程报错：
					安装过程停止并出现error warning或no的提示
			3.源码包安装过程
				下载源码包
				解压缩下载的源码包
				进入解压缩目录
				./configure 软件配置与检查
					定义需要的功能选项
						./configure --help
						./configure --prefix=/usr/local/apach
					检测系统环境是否符合安装要求
					把定义好的功能选项和检测系统环境的信息都写入Makefile文件，用于后续的编辑
					make 编译
						make clean	清空编译产生的文件
					make install	安装
			4.源码包的卸载
				不需要卸载命令
					rm -rf 源码包安装位置
				不会遗留任何垃圾文件
	脚本安装包
		脚本安装包并不是独立的软件包类型，常见安装的是源码包
		是人为把安装过程写成了自动安装的脚本，只要执行脚本，定义了简单的参数，就可以完成安装
		非常类似于Windows下软件的安装方式
		
		Webmin的作用：
			Webmin是一个基于Web的Linux系统管理界面，可以通过图形化的方式设置用户账号、Apache、DNS、文件共享等服务
		Webmin安装过程：
			下载软件：http://sourceforge.net/projects/webadmin/files/webmin/
			解压缩并进入压缩目录
			执行安装脚本

用户和用户组管理
	用户配置文件
		用户信息文件/etc/passwd
			越是对服务器安全性要求高的服务器，越需要建立合理的用户权限等级制度和服务器操作规范；
			在Linux中主要是通过用户配置文件来查看和修改用户信息；
			/etc/passwd
				man 5 passwd
					account:password:UID:GID:CECOS:directory:shell
				第一字段：用户名称
				第二字段：密码标志
				第三字段：UID(用户ID)
					0			超级用户
					1-499		系统用户(伪用户)
					500-65535	普通用户
				第四字段：		GID(用户初始组)
				第五字段：		用户说明
				第六字段：		家目录
					普通用户：/home/用户名/
					超级用户：/root/
				第七字段：		登陆之后的Shell
			初始组合附加组；
				初始组：指用户一登陆就立刻拥有这个用户组的相关权限，每个用户的初始组只能有一个，一般就是和这个用户的用户名相同的祖名作为这个用户的初始组；
				附加组：指用户可以加入多个其它的用户组，并拥有这些组的权限，附加组可以有多个；
			Shell
				Shell就是Linux的命令解释器；
				在 /etc/passwd 当中，除了标准 Shell 是 /bin/bash 之外，还可以写如：/sbin/nologin /usr/bin/passwd 等
		影子文件/etc/shadow
			第一字段：用户名
			第二字段：加密密码
				加密算法升级为 SHA512 散列加密算法
				如果密码位是 * 代表没有密码，!! 表示禁用密码，不能登陆
			第三字段：密码最后一次修改时间
				使用 1970 年 1 月 1 日 作为标准时间，每过一天时间戳加 1
			第四字段：两次密码的修改间隔时间
				在该时间间隔内不允许修改密码
			第五字段：密码的有效期
				该密码多少天之后失效
			第六字段：密码修改到期前的警告天数
			第七字段：密码过期后的宽限天数
				不写也是立即失效
				0		代表过期后立即失效
				-1		代表密码永远不会失效
			第八字段：账号失效时间
				要用时间戳表示
			第九字段：保留
			时间戳换算：
				把时间戳换算为日期：date -d "1970-01-01 16066 days"
				把日期换算为时间戳：echo $(($(date --date="2014/01/06"+%s)/86400+1))
		组信息文件/etc/group和组密码文件/etc/gshadow
			组信息文件 /etc/group
				第一字段：组名
				第二字段：组密码标志
				第三字段：GID
				第四字段：组中附加用户
			组密码文件 /etc/gshadow
				第一字段：组名
				第二字段：组密码
				第三字段：组管理员用户名
				第四字段：组中附加用户
	用户管理相关文件
		用户的家目录
			普通用户： /home/用户名，所有者和所属组都是此用户，权限是700
			超级用户：/root/ 所有者和所属组都是 root 用户，权限是550
		用户的邮箱
			/var/spool/mail/用户名/
		用户模板目录
			/etc/skel/
			创建用户时在家目录会自动添加模板目录内的文件
	用户管理命令
		用户添加命令 useradd
			useradd [选项] 用户名
				选项：
					-u UID			手工指定用户的UID号
					-d 家目录		手工指定用户的家目录
					-c 用户说明		手工指定用户的说明
					-g 组名			手工指定用户的初始组
					-G 组名			指定用户的附加组
					-s shell		手工指定用户的登陆shell 默认是/bin/bash
				useradd -u 550 -G root,bin -d /home/test -c "test user" -s /bin/bash testuser
			用户默认值文件
				/etc/default/useradd
					GROUP=100			用户默认组
					HOME=/home			用户家目录
					INACTIVE=-1			密码过期宽限天数(7)
					EXPIRE=				密码失效时间(8)
					SHELL=/bin/bash		默认shell
					SKEL=/etc/skel		模板目录
					CREATE_MAIL_SPOOL=yes	是否建立邮箱
				/etc/login.defs
					PASS_MAX_DAYS 99999			密码有效期(5)
					PASS_MIN_DAYS 0				密码修改间隔(4)
					PASS_MIN_LEN 5				密码最小5位(PAM)
					PASS_WARN_AGE 7				密码到期警告(6)
					UID_MIN 500					最小和最大UID范围
					GID_MAX 60000
					ENCRYPT_METHOD SHA512		加密模式
		修改用户密码 passwd
			passwd [选项] 用户名
				选项：
					-S				查询用户密码的密码状态。仅root用户可用
					-l				暂时锁定用户。仅root用户可用
					-u				解锁用户。仅root用户可用
					--stdin			可以通过管道输出的数据作为用户的密码
						echo "123"|passwd --stdin lamp
		修改用户信息 usermod
			usermod [选项] 用户名
				选项：
					-u UID				修改用户的UID号
					-c 用户说明			修改用户的说明信息
					-G 组名				修改用户的附加组
					-L					临时锁定用户
					-U					解锁用户锁定
				usermod -c "test user" lamp
				usermod -G root lamp
				usermod -L lamp
				usermod -U lamp
		修改用户密码状态 chage
			chage [选项] 用户名
				选项：
					-l				列出用户的详细密码状态
					-d 日期			修改密码最后一次更改日期(shadow第三字段)
					-m 天数			两次密码的修改间隔时间(4字段)
					-M 天数			密码有效期(5字段)
					-W 天数			密码过期前警告天数(6字段)
					-I 天数			密码过期后宽限天数(7字段)
					-E 日期			账号失效时间(8字段)
				chage -d 0 testuser		使用户一登陆就需要修改密码
		删除用户 userdel
			userdel [-r] 用户名
				选项：
					-r		删除用户的同时删除用户家目录
				userdel -r testuser
		查看用户 id
			id 用户名
		用户切换命令 su
			su [选项] 用户名
				选项：
					-			连带用户的环境变量一起切换
					-c 命令		仅执行一次命令，而不切换用户身份
				su - root	切换成 root
				su - root -c "useradd user3"		不切换成 root 但是执行 useradd 命令添加 user3 用户
			env	查看环境变量
	用户组管理命令
		添加用户组
			groupadd [选项] 组名
				选项：
					-g GID		指定组ID
		修改用户组
			groupmod [选项] 组名
				选项：
					-g GID		修改组ID
					-n 新组名	修改组名
			groupmod -n testgrp group1
		删除用户组
			groupdel [选项] 组名
			如果组中有初始用户则不能删除，如果是附加用户则可以删除
		把用户添加入组或从组中删除
			gpasswd [选项] 组名
				选项：
					-a 用户名		把用户加入组
					-d 用户名		把用户从组中删除
权限管理
	ACL权限
		ACL 权限简介与开启
			ACL(Access Control List)，可以针对单个用户，单个文件或目录进行 r w x 的权限设定，特别适合用于需要特殊权限的使用情况
			查看分区 ACL 权限是否开启：
				dumpe2fs -h /dev/sda3
					dumpe2fs 命令是查询指定分区详细文件系统信息的命令
					-h		仅显示超级块中信息，而不显示磁盘块组的详细信息
				df -h		查看系统分区使用状况
			临时开启分区 ACL 权限：
				mount -o remount,acl /		重新挂载根分区，并挂载加入 acl 权限
			永久开启分区 ACL 权限：
				vi /etc/fstab UUID=c2ca6f57-b15c-43ea-bca0-f239083d8bd2 / ext4 default,acl 1 1		加入 acl
				mount -o remount /			重新挂载文件系统或重启动系统，使修改生效
		查看与设定 ACL 权限
			查看 ACL 权限的命令：
				getfacl 文件名			查看 acl 权限
			设定 ACL 权限的命令：
				setfacl 选项 文件名
					选项：
						-m				设定 ACL 权限
						-x				删除指定的 ACL 权限
						-b				删除所有的 ACL 权限
						-d				设定默认 ACL 权限
						-k				删除默认 ACL 权限
						-R				递归设定 ACL 权限
					useradd zhangsan
					useradd lisi
					useradd st
					groupadd tgroup
					mkdir /project
					chown root:tgroup /project/
					chmod 770 /project/
					setfacl -m u:st:rx /project/
				给用户 st 赋予 r-x 权限，使用 u:用户名:权限 格式
					groupadd tgroup2
					setfacl -m g:tgroup2:rwx project/
				为组 tgroup2 分配 acl 权限，使用 g:组名:权限 格式
		最大有效权限与删除 ACL 权限
			最大有效权限 mask
				mask 是用来指定最大有效权限的，如果给用户赋予了 ACL 权限，是需要和 mask 的权限 “相与” 才能得到用户的真正权限
					A			B			and
					—————————————————————————————
					r			r			r
					r			-			-
					-			r			-
					-			-			-
			修改最大有效权限
				setfacl -m m:rx 文件名		设定 mask 权限为 r-x 使用 m:权限 格式
			删除 ACL 权限
				setfacl -x u:用户名 文件名	删除指定用户的 ACL 权限
				setfacl -x g:组名 文件名	删除指定用户组的 ACL 权限
				setfacl -b 文件名			删除文件的所有的 ACL 权限
		默认 ACL 权限和递归 ACL 权限
			递归 ACL 权限
				递归是父目录在设定 ACL 权限时，所有的子文件和目录也会拥有相同的 ACL 权限
				setfacl -m u:用户名:权限 -R 目录名
				
				setfacl -m u:st:rx -R /project/
			默认 ACL 权限
				默认 ACL 权限的作用是如果给父目录设定了默认的 ACL 权限，那么父目录中所有新建的子文件都会继承父目录的 ACL 权限
				setfacl -m d:u:用户名:权限 目录名
				
				setfacl -m d:u:st:rx /project/
	文件特殊权限
		SetUID
			SetUID 的功能
				只有可以执行的二进制程序才能设定 SUID 权限；
				命令执行者要对该程序拥有 x (执行) 权限；
				命令执行者在执行该程序时获得该程序文件属主的身份(在执行程序的过程中灵魂附体为文件的属主)；
				SetUID 权限只在该程序执行过程中有效，也就是说身份改变只在程序执行过程中有效；
				passwd命令拥有SetUID权限，所以普通用户可以修改自己的密码；
				cat命令没有SetUID权限，所以普通用户不能查看/etc/shadow文件内容；
			设定SetUID的方法
				4代表SUID
					chmod 4755 文件名
					chmod u+s 文件名
			取消SetUID的方法
				chmod 755 文件名
				chmod u-s 文件名
			危险的SetUID
				关键目录应严格控制写权限；比如 / /usr 等
				用户的密码设置要严格遵守密码三原则；
				对系统中默认应该具有SetUID权限的文件作一列表，定时检查有没有这之外的文件被设置了SetUID权限；
		SetGID
			SetGID针对文件的作用
				只有可执行的二进制程序才能设置SGID权限；
				命令执行者要对该程序拥有 x (执行) 权限；
				命令执行者在执行程序的时候，组身份升级为该程序文件的属组；
				SetGID权限同样只在该程序执行过程中有效，也就是说组身份改变只在程序执行过程中有效；
				ll /usr/bin/locate
					-rwx--s--x. 1 root slocate 40496 8月 24 2010 /usr/bin/locate
				ll /var/lib/mlocate/mlocate.db
					-rw-r----- l root slocate 1838859 1月 20 04:29 /var/lib/mlocate/mlocate.db
				/usr/bin/locate是可执行二进制程序，可以赋予SGID；
				执行用户lamp对/usr/bin/locate命令拥有执行权限；
				执行/usr/bin/locate命令时，组身份会升级为slocate组，而slocate组对/var/lib/mlocate/mlocate.db数据库拥有r权限，所以普通用户可以使用locate命令查询mlocate.db数据库；
				命令结束，lamp用户的组身份返回为lamp组；
			SetGID针对目录的作用
				普通用户必须对此目录拥有r和x权限，才能进入此目录；
				普通用户在此目录中的有效组会变成此目录的属组；
				若普通用户对此目录拥有w权限时，新建的文件的默认属组是这个目录的属组；
					cd /tmp/
					mkdir dtest
					chmod g+s dtest 或者 chmod 2755 dtest
					ll -d dtest/
					chmod 777 dtest/
					su - lamp
					cd /tmp/dtest/
					touch abc
					ll
			设定SetGID
				2代表SGID
					chmod 2755 文件名
					chmod g+s 文件名
			取消SetGID
				chmod 755 文件名
				chomd g-s 文件名
		Sticky BIT
			SBIT粘着位作用
				粘着位目前只对目录有效；
				普通用户对该目录拥有w和x权限，即普通用户可以在此目录拥有写入权限；
				如果没有粘着位，因为普通用户拥有w权限，所以可以删除此目录下所有文件，包括其他用户建立的文件。一旦赋予了粘着位，除了root可以删除所有文件，普通用户就算拥有w权限，也只能删除自己建立的文件，但是不能删除其他用户建立的文件
					ll -d /tmp/		--->	drwxrwxrwt. 3 root root 4096 12月 13 11：22 /tmp/
			设置与取消粘着位
				设置粘着位
					chmod 1755 目录名
					chmod o+t 目录名
				取消粘着位
					chmod 777 目录名
					chmod o-t 目录名
	文件系统属性 chattr 权限
		chattr命令格式
			对 root 用户也起作用
			chattr [+-=] [选项] 文件或目录名
				+		增加权限
				-		删除权限
				=		等于某权限
			选项：
				i	如果对文件设置i属性，那么不允许对文件进行删除、改名，也不能添加和修改数据；如果对目录设置i属性，那么只能修改目录下文件的数据，但不允许建立和删除文件
				a	如果对文件设置a属性，那么只能在文件中增加数据，但是不能删除也不能修改数据；如果对目录设置a属性，那么只允许在目录中建立和修改文件，但是不允许删除；
		查看文件系统属性
			lsattr 选项 文件名
				选项：
					-a		显示所有文件和目录
					-d		若目标是目录，仅列出目录本身的属性，而不是子文件的
	系统命令 sudo 权限
		sudo 权限
			root 把本来只能超级用户执行的命令赋予普通用户执行；
			sudo 的操作对象是系统命令；
		sudo 使用
			visudo 实际修改的是/etc/sudoers文件
			
				用户名 被管理主机的地址=(可使用的身份) 授权命令(绝对路径)
				%组名 被管理主机的地址=(可使用的身份) 授权命令(绝对路径)
			
			sc ALL=/sbin/shutdown -r now		root用户授权sc用户可以重启服务器
			
			sudo -l		普通用户查看可用的sudo命令
			sudo /sbin/shutdown -r now	普通用户执行sudo赋予的命令

文件系统管理
	回顾分区和文件系统
		分区类型
			主分区：总共最多只能分四个
			扩展分区：只能有一个，也算作主分区的一种，也就是说主分区加扩展分区最多有四个。但是扩展分区不能存储数据和格式化，必须再划分成逻辑分区才能使用；
			逻辑分区：逻辑分区是在扩展分区中划分的，如果是IDE硬盘，Linux最多支持59个逻辑分区，如果是SCSI硬盘Linux最多支持11个逻辑分区
		分区表示方法
			如果有三个主分区，和三个逻辑分区：
			——————————————————————————————————————
									分区设备文件名
			——————————————————————————————————————
				主分区1				/dev/sda1
				主分区2				/dev/sda2
				主分区3				/dev/sda3
				扩展分区			/dev/sda4
				逻辑分区1			/dev/sda5
				逻辑分区2			/dev/sda6
				逻辑分区3			/dev/sda7
			——————————————————————————————————————
			如果只有一个主分区和3个逻辑分区：
			——————————————————————————————————————
				主分区1				/dev/sda1
				扩展分区			/dev/sda2
				逻辑分区1			/dev/sda5
				逻辑分区2			/dev/sda6
				逻辑分区3			/dev/sda7
			——————————————————————————————————————
		文件系统
			ext2：是 ext 文件系统的升级版本，最大支持16TB的分区和最大2TB的文件(1TB=1024GB=1024*1024MB=1024*1024*1024KB)
			ext3：是 ext2 文件系统的升级版本，最大的区别就是带日志功能，以在系统突然停止时提高文件系统的可靠性。支持最大16TB的分区和最大2TB的文件
			ext4：是 ext3 文件系统的升级版本，ext4 在性能、伸缩性和可靠性方面进行了大量改进；ext4 的变化可以说是翻天覆地的，比如向下兼容 ext3 最大1EB文件系统和16TB文件、无限数量子目录、Extents连续数据块概念、多块分配、延迟分配、持久预分配、快速FSCK、日志校验、无日志模式、在线碎片整理、inode增强、默认启用barrier等(1EB=1024PB=1024*1024TB)
	文件系统常用命令
		df命令、du命令、fsck命令和dumpe2fs命令
			文件系统查看命令 df
				df [选项] [挂载点]
					选项：
						-a		显示所有的文件系统信息。包括特殊文件系统，如/proc /sysfs
						-h		使用习惯单位显示容量，如KB MB GB
						-T		显示文件系统类型
						-m		以MB为单位显示容量
						-k		以KB为单位显示容量。默认是以KB为单位
				df -h	显示分区的占用状况
			统计目录或文件大小命令 du
				du [选项] [目录或文件名]
					选项：
						-a		显示每个子文件的磁盘占用量。默认只统计子目录的磁盘占用量
						-h		使用习惯单位显示磁盘占用量，如KB MB GB
						-s		统计总占用量，而不列出子目录和子文件的占用量
				du -sh /etc/
			df命令和du命令的区别：
				df命令是从文件系统考虑的，不光要考虑文件占用的空间，还要统计被命令或程序占用的空间(最常见的就是文件已经删除，但是程序并没有释放空间)
				du命令是面向文件的，只会计算文件或目录占用的空间，扫描目录下的所有文件，比较慢
			文件系统修复命令 fsck
				系统开机会自动检查，一般不需要手动修复
				fsck [选项] 分区设备文件名
					选项：
						-a		不用显示用户提示，自动修复文件系统
						-y		自动修复，和-a作用一致，不过有些文件系统只支持-y
			显示磁盘状态命令dumpe2fs
				dumpe2fs 分区设备文件名
				
				dumpe2fs /dev/sda1
		挂载命令
			查询与自动挂载
				mount [-l]		查询系统中已经挂载的设备，-l会显示卷标名称
				mount -a		依据配置文件 /etc/fstab 的内容自动挂载
			挂载命令格式
				mount [选项] 设备文件名 挂载点
					选项：
						-t 文件系统			加入文件系统类型来指定挂载的类型，可以是ext3 ext4 iso9660等文件系统
						-L 卷标名			挂载指定卷标的分区，而不是安装设备文件名挂载
						-o 特殊选项			可以指定挂载的额外选项(针对分区，一般使用默认值)
					————————————————————————————————————————————————————————
						参数						说明
					————————————————————————————————————————————————————————
					atime/noatime			更新访问时间/不更新访问时间；访问分区文件时，是否跟新文件的访问时间，默认为更新
					async/sync				异步/同步，默认为异步
					auto/noauto				自动/手动；mount -a命令执行时，是否会自动安装/etc/fstab文件内容挂载，默认为自动
					defaults				定义默认值，相当于rw suid exec auto nouser async 这七个选项
					exec/noexec				执行/不执行；设定是否允许在文件系统中执行可执行文件，默认是exec允许
					remount					重新挂载已经挂载的文件系统，一般用于指定修改特殊权限
					rw/ro					读写/只读；文件系统挂载时，是否具有读写权限，默认是rw
					suid/nosuid				具有/不具有SUID权限；设定文件系统是否具有SUID和SGID的权限，默认是具有
					user/nouser				允许/不允许普通用户挂在；设定文件系统是否允许普通用户挂载，morn是不允许，只有root可以挂载分区
					usrquota				写入代表文件系统支持用户磁盘配额，默认不支持
					grpquota				写入代表文件系统支持组磁盘配额，默认不支持
					——————————————————————————————————————————————————————————————————————————————————————————
				mount -o remount,noexec /home			重新挂载 /home 分区，并使用noexec权限
				cd /home
				vi hello.sh
				chomod 755 hello.sh
				./hello.sh		文件执行不了
		挂载光盘与U盘
			挂载光盘
				mkdir /mnt/cdrom			建立挂载点
				mount -t iso9660 /dev/cdrom /mnt/cdrom/		挂载光盘
				或者 mount /dev/sr0 /mnt/cdrom		/dev/cdrom 是 /dev/sr0 的软链接
			卸载命令
				umount 设备文件名或挂载点
				umount /mnt/cdrom
			挂载U盘
				fdisk -l			查看U盘设备文件名(和硬盘设备文件名同样的命名规则)
				
				mkdir -p /mnt/usb/
				mount -t vfat /dev/sdb1 /mnt/usb/
					fat32 文件系统为 vfat
					fat16 文件系统为 fat
				注意：Linux默认是不支持NTFS文件系统的
		支持NTFS文件系统
			方案一：修改内核
			方案二：使用第三方插件
			下载NTFS-3G插件：http://www.tuxera.com/community/ntfs-3g-download/
			安装NTFS-3G
				tar -zxvf ntfs-3g ntfsprogs-2013.1.13.tgz
				cd ntfs-3g_ntfsprogs-2013.1.13
				./configure && make && make install
			使用
				mount -t ntfs-3g 分区设备文件名 挂载点
				
				fdik -l
				mount -t ntfs-3g /dev/sdb1 /mnt/usb/
				umount /dev/sdb1
	fdisk 分区
		fdisk 命令手动硬盘分区过程
			1.添加新硬盘
			2.查看新硬盘
				fdisk -l
			3.使用fdisk命令分区
				fdisk /dev/sdb
				
				————————————————————————————————————————————
						fdisk交互指令说明
				————————————————————————————————————————————
				命令					说明
				————————————————————————————————————————————
				a			设置可引导标记
				b			编辑 bsd 磁盘标签
				c			设置 DOS 操作系统兼容标记
				d			删除一个分区
				l			显示已知的文件系统类型。82为 Linux swap 分区，83为 Linux 分区
				m			显示帮助菜单
				n			新建分区
				o			建立空白 DOS 分区表
				p			显示分区列表
				q			不保存退出
				s			新建空白 SUN 磁盘标签
				t			改变一个分区的系统 ID
				u			改变现实记录单位
				v			验证分区表
				w			保存退出
				x			附加功能(仅专家)
			4.重新读取分区表信息
				partprobe
			5.格式化分区
				mkfs -t ext4 /dev/sdb1
			6.建立挂载点并挂载
				mkdir /disk1
				mount /dev/sdb1 /disk1
		分区自动挂载与fstab文件修复
			手动挂载在系统重启后失效，只有将挂载信息写入 /etc/fstab 中才能在系统重启时自动挂载
			/etc/fstab 文件
				第一字段：分区设备文件名或UUID(硬盘通用唯一识别码)
				第二字段：挂载点
				第三字段；文件系统名称
				第四字段：挂载参数，使用默认权限
				第五字段：指定分区是否被 dump 备份，0代表不备份，1代表每天备份，2代表不定期备份
				第六字段：指定分区是否被 fsck 检测，0代表不检测，其他数字代表检测的优先级，那么当然1的优先级比2高
			分区自动挂载
				dumpe2fs /dev/sdb1	查看UUID
				vi /etc/fstab		按照格式写入需要系统开机自动挂载分区
					/dev/sdb1	/fdisk1/	ext4	defaults	1 2
				mount -a 	根据 /etc/fstab 自动挂载，可以查看是否挂载成功
			/etc/fstab文件修复
				mount -o remount,rw /
				修正 /etc/fstab 文件
	分配swap分区
		free		查看内存与swap分区使用状况
			cached		把读取出来的数据保存在内存当中，当再次读取时不用读取硬盘而直接从内存中读取，加速了数据的读取过程
			buffer		在写入数据时，先把分散的写入操作保存到内存当中，当达到一定程度再集中写入硬盘，减少了磁盘碎片和硬盘的反复寻道，加速了数据的写入

		新建 swap 分区
			fdisk /dev/sdb
			把分区ID改为82
		格式化
			mkswap /dev/sdb6
		加入 swap 分区
			swapon /dev/sdb6		加入 swap 分区
			swapoff /dev/sdb6		取消 swap 分区
		swap 分区开机自动挂载
			vi /etc/fstab	按照格式写入需要系统开机自动挂载分区
				/dev/sdb6	swap	swap	defaults	0 0
		
Shell 基础
	Shell 概述
		Shell 是什么
			Shell 是一个命令行解释器，它为用户提供了一个向 Linux 内核发送请求以便运行程序的界面系统级程序，用户可以用 Shell 来启动、挂起、停止甚至是编写一些程序
				硬件<---->内核<--->Shell 命令解释器<--->外层应用程序
			Shell 还是一个功能强大的编程语言，易编写，易调试，灵活性较强。Shell 是解释执行的脚本语言，在 Shell 中可以直接调用 Linux 系统命令
		Shell 的分类
			Bourne Shell	从1979起Unix就开始使用 Bourne Shell，Bourne Shell 的主文件名为 sh
			C Shell			C Shell 主要在 BSD 版的 Unix 系统中使用，其语法和C语言相类似而得名
			Shell 的两种主要语法类型有 Bourne 和 C，这两种语法彼此不兼容。Bourne 家族主要包括：sh ksh Bash psh zsh；C 家族主要包括：csh tcsh
			Bash	Bash 与 sh 兼容，现在使用的 Linux 就是使用 Bash 作为用户的基本 Shell
		Linux 支持的 Shell
			/etc/shells
		Shell 切换
			sh			切换为 sh
			exit		退出当前 shell
	Shell 脚本的执行方式
		echo 输出命令
			echo [选项] [输出内容]
				选项：
					-e			支持反斜线控制的字符转换
					——————————————————————————————————
					控制字符				作用
					——————————————————————————————————
						\\			输出\本身
						\a			输出警告音
						\b			退格键，也就是向左删除键
						\c			取消输出行尾的换行符。和"-n"选项一致
						\e			ESCAPE键
						\f			换页符
						\n			换行符
						\r			回车键
						\t			制表符
						\v			垂直制表符
						\0nnn		按照八进制ASCII码表输出字符。其中0为数字零，nnn是三位八进制数
						\xhh		按照十六进制ASCII码表输出字符。其中hh是两位十六进制数
					——————————————————————————————————————————————————————————————————
					echo -e "ab\bc"						删除左侧字符
					echo -e "a\tb\tc\nd\te\tf"			制表符与换行符
					echo -e "\x61\t\x62\t\x63\n\x64\t\x65\t\x66"	按照十六进制ASCII码表也同样可以输出
					echo -e "\e[1;31m abcd \e[0m"		输出颜色
						\e[1;	表示开启颜色输出
						\e[0m	表示结束颜色输出
						30m		黑色
						31m		红色
						32m		绿色
						33m		黄色
						34m		蓝色
						35m		洋红
						36m		青色
						37m		白色
		第一个脚本
			vi hello.sh
			#!/bin/Bash
			#The first program
			echo -e "hello world"
		脚本执行
			赋予执行权限，直接运行
				chmod 755 hello.sh
				./hello.sh(相对路径)
				/root/test/hello.sh(绝对路径)
			通过 Bash 调用执行脚本
				bash hello.sh
		dos2unix 文件名		将 windows 格式的文件转换为 linux 格式的文件
		unix2dos 文件名
	Bash 的基本功能
		历史命令与命令补全
			历史命令
				history [选项] [历史命令保存文件]
					选项：
						-c		清空历史命令
						-w		把缓存中的历史命令写入历史命令保存文件 ~/.bash_history
				历史命令默认会保存1000条，可以在环境变量配置文件 /etc/profile 中进行修改
				历史命令的调用
					使用上、下箭头调用以前的历史命令
					使用 !n 重复执行第 n 条历史命令
					使用 !! 重复执行上一条命令
					使用 !字符串 重复执行最后一条以该字符串开头的命令
			命令与文件补全
				在 Bash 中，命令与文件补全是非常方便与常用的功能，我们只要在输入 命令或文件时，按 Tab 键就会自动进行补全
		命令别名与常用快捷键
			命令别名
				alias 别名='原命令'		设定命令别名
				alias					查询命令别名
				vi /root/.bashrc		让别名永久生效
				unalias	别名			删除别名
				命令执行时顺序：
					第一顺位执行用绝对路径或相对路径执行的命令
					第二顺位执行别名
					第三顺位执行 Bash 的内部命令
					第四顺位执行按照 $PATH 环境变量定义的目录查找顺序找到的第一个命令
			Bash 常用快捷键
				ctrl+A				把光标移动到命令行开头。如果我们输入的命令过长，想要把光标移动到命令行开头时使用
				ctrl+E				把光标移动到命令行结尾
				ctrl+C				强制终止当前的命令
				ctrl+L				清屏，相当于 clear 命令
				ctrl+U				删除或剪切光标之前的命令。如果输入了一行很长的命令，不使用退格键一个一个字符的删除，使用这个快捷键很方便
				ctrl+K				删除或剪切光标之后的内容
				ctrl+Y				粘贴 ctrl+U 或 ctrl+K 剪切的内容
				ctrl+R				在历史命令中搜索，按下 ctrl+R 之后，就会出现搜索界面，只要输入搜索内容，就会从历史命令中搜索
				ctrl+D				退出当前终端
				ctrl+Z				暂停，并放入后台。
				ctrl+S				暂停屏幕输出
				ctrl+Q				恢复屏幕输出
		输入输出重定向
			标准输入输出
				设备			设备文件名			文件描述符			类型
				---------------------------------------------------------------
				键盘			/dev/stdin			0					标准输入
				显示器			/dev/stdout			1					标准输出
				显示器			/dev/stderr			2					标准错误输出
			输出重定向
				类型							符号				作用
				-----------------------------------------------------------------------------------------------------------
				标准输出重定向					命令 > 文件			以覆盖的方式，把命令的正确输出输出到指定的文件或设备中
												命令 >> 文件		以追加的方式，把命令的正确输出输出到指定的文件或设备当中
				-----------------------------------------------------------------------------------------------------------
				标准错误输出重定向				错误命令 2> 文件	以覆盖的方式，把命令的错误输出输出到指定的文件或设备当中
												错误命令 2>> 文件	以追加的方式，把命令的错误输出输出到指定的文件或设备当中
				-----------------------------------------------------------------------------------------------------------
				正确输出和错误输出同时保存		命令 > 文件 2>&1	以覆盖的方式，把正确输出和错误输出都保存到同一个文件当中
												命令 >> 文件 2>&1	以追加的方式，把正确输出和错误输出都保存到同一个文件当中
												命令 &>> 文件		以追加的方式，把正确输出和错误输出都保存到同一个文件当中
												命令 >> 文件1 2>> 文件2		把正确的输出追加到文件1中，把错误的输出追加到文件2中
				-----------------------------------------------------------------------------------------------------------
				
				ls &>> /dev/null			输出将不会保存
				
			输入重定向
				wc [选项] [文件名]
					选项：
						-c		统计字节数
						-w		统计单词数
						-l		统计行数
				命令 < 文件		把文件作为命令的输入
				命令 << 标识符
				...
				标识符			把标识符之间的内容作为命令的输入
		多命令顺序执行与管道符
			多命令执行顺序
				---------------------------------------------------------------------------
				多命令执行符		格式						作用
				----------------------------------------------------------------------------
					;			命令1;命令2			多个命令顺序执行，命令之间没有任何逻辑联系
					&&			命令1&&命令2		逻辑与。当命令1正确执行，则命令2才会执行；当命令1执行不正确，则命令2不会执行
					||			命令1||命令2		逻辑或。当命令1没有正确执行(包括没有执行)，则命令2才会执行；当命令1正确执行，则命令2不会执行
				------------------------------------------------------------------------
				ls;data;cd /usr/;pwd
				
				dd if=输入文件 of=输出文件 bs=字节数 count=个数
					if=输入文件			指定源文件或源设备
					of=输出文件			指定目标文件或目标设备
					bs=字节数			指定一次输入/输出多少个字节，即把这些字节看作一个数据块
					count=个数			指定输入/输出多少个数据块
				data;dd if=/dev/zero of=/root/testfile bs=1k count=100000;date
				
				命令 && echo yes || echo  no
					ls anaconda-ks.cfg && echo yes
					ls /root/test || echo "hello world"
			管道符
				命令1 | 命令2			命令1的正确输出作为命令2的操作对象
					ll -a /etc/ | more
					netstat -an | grep "ESTABLISHED"
				grep [选项] "搜索内容" 文件名
					选项：
						-i			忽略大小写
						-n			输出行号
						-v			反向查找
						--color=auto	搜索出的关键字用颜色显示
		通配符与其他特殊符号
			通配符是完全匹配，通常用来匹配文件名
			通配符
				?			匹配一个任意字符
				*			匹配0个或任意多个任意字符，也就是可以匹配任何内容
				[]			匹配中括号中任意一个字符。[abc]代表匹配a或b或c
				[-]			匹配中括号中任意一个字符，-代表一个返回。[a-z]代表匹配一个小写字母
				[^]			逻辑非，表示匹配不是中括号内的一个字符。[^0-9]代表匹配一个不是数字的字符
				
				rm -rf /tmp/*
				touch /tmp/abc /tmp/abcd /tmp/012 /tmp/0abc
				ls /tmp/?abc
				ls /tmp/[0-9]*
				ls /tmp/[^0-9]*
		Bash 中的其他特殊符号
			''			单引号。在单引号中所有的特殊字符都没有特殊含义
			""			双引号。在双引号中的字符除了 $ ` \ 外都没有特殊含义
						$ 在 "" 中表示 调用变量的值
						` 在 "" 中表示 引用命令
						\ 在 "" 中表示 转义符
			``			反引号。反引号括起来的内容是系统命令，在 Bash 中会先执行它
			$()			和反引号作用一样，用来引用系统命令，推荐使用
			#			在 Shell 脚本中，# 开头的行代表注释
			$			用于调用变量的值，$var_name 可得到变量的值
			\			转义符，跟在 \ 之后的特殊符号失去特殊含义，变为普通字符
	Bash 的变量
		用户自定义变量
			变量是计算机内存的单元，其中存放的值可以改变，当 Shell 脚本需要保存一些信息时，如一个文件名或是一个数字，就把它放在一个变量中。每个变量有一个名字，所以很容易引用它。
			使用变量可以保存有用信息，使系统获知用户相关设置，变量也可以用于保存暂时信息
			变量设置规则：
				变量名称可以由字母、数字和下划线组成，但是不能以数字开头
				在 Bash 中，变量的默认类型都是字符串型，如果要进行数值运算，则必须指定变量类型为数值型
				变量用等号连接值，等号左右两侧不能有空格
				变量的值如果有空格，需要使用单引号或双引号包括
				在变量的值中，可以使用 \ 转义符
				如果需要增加变量的值，那么可以进行变量值的叠加，不过变量需要用双引号包含 "$变量名" 或使用 ${变量名} 包含
				如果是把命令的结果作为变量值赋予变量，则需要使用 反引号 或 $() 包含命令
				环境变量名建议大写，便于区分
			变量分类：
				用户自定义变量
				环境变量：这种变量中主要保存的是和系统操作环境相关的数据
				位置参数变量：这种变量主要是用来向脚本当中传递参数或数据的，变量名不能自定义，变量作用是固定的
				预定义变量：是 Bash 中已经定义好的变量，变量名不能自定义，变量作用也是固定的
			本地变量：
				定义变量
					var_name=var_value
					如果 var_value 中有空格，需要用单引号或双引号括起来；
					= 左右不能有空格
				变量叠加
					aa=123
					aa="$aa"456
					aa=${aa}789
				变量调用
					echo $var_name
				变量查看
					set		查看所有的变量
						set | grep PATH		查看系统查找命令的路径
				变量删除
					unset var_name
		环境变量
			用户自定义变量只在当前的 Shell 中生效，而环境变量会在当前 Shell 和这个 Shell 的所有子 Shell 中生效。
			如果把环境变量写入相应的配置文件，那么这个环境变量就会在所有的 Shell 中生效
			pstree		查看 shell 树
			设置环境变量：
				export var_name=var_value
				export var_name		将本地变量(已存在)设置为环境变量
			查询环境变量
				env			查看所有环境变量
			删除环境变量
				unset var_name
			系统常见环境变量
				PATH	系统查找命令的路径
					echo $PATH
					PATH="$PATH":/root/sh		PAHT 变量叠加
				PS1		定义系统提示符的变量
					\d		显示日期，格式为 星期 月 日
					\h		显示简写主机名
					\t		显示24小时制时间，格式为 HH:MM:SS
					\T		显示12小时制时间，格式为 HH:MM:SS
					\A		显示24小时制时间，格式为 HH:MM
					\u		显示当前用户名
					\w		显示当前所在目录的完整名称
					\W		显示当前所在目录的最后一个目录
					\#		执行的第几个命令
					\$		提示符，如果是root用户会显示提示符为 # 如果是普通用户会显示提示符为 $
					
					[root@localhost~]#PS1='[\u@\t \w]\$ '
					[root@04:50:08 /usr/local/src]#PS1='[\u@\@\h\#\W]\$ '
				$PWD	当前目录
				$HOME				当前用户
		位置参数变量
			$n		n为数字，$0代表命令本身，$1-$9代表第一到第九个参数，十以上的参数需要用大括号包含，如 ${10}
			$*		这个变量代表命令行中的所有参数，$*把所有的参数看称一个整体		
			$@		这个变量也代表命令行中的所有参数，不过 $@把每个参数区分对待
			$#		这个变量代表命令行中所有参数的个数
			
			#!/bin/bash
			num1=$1
			num2=$2
			sum=$(($num1 + $num2))
			echo $sum
			
			./test.sh 2 3		执行脚本
			
			
			#使用 $# 代表所有参数的个数
			echo "a total of $# parameters"
			#使用 $* 代表所有参数
			echo "the parameters are: $*"
			#使用 $@ 代表所有参数
			echo "the paremeters are: $@"
			
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
			
		预定义变量
			$?			最后一次执行的命令的返回状态。如果这个变量的值为0，证明上一个命令正确执行；如果这个变量值为非0(具体是哪个数由命令自己决定)，则证明上一个命令执行不正确
			$$			当前进程的进程号(PID)
			$!			后台运行的最后一个进程的进程号
			
			#!/bin/bash
			# 输出当前进程的 ID
			echo "the current process is $$"
			# 在 /root 目录下查找 hello.sh 文件，& 的意思是把命令放入后台执行
			find /root -name hello.sh &
			# 输出后台运行的最后一个进程 ID
			echo "the last one deamon process is $!"
		接收键盘输入
			read [选项] [变量名]
				选项：
					-p "提示信息"		在等待 read 输入时，输出提示信息
					-t 秒数				read 命令会一直等待用户输入，使用此选项可以指定等待时间
					-n 字符数			read 命令只接受指定的字符数，就会执行
					-s					隐藏输入的数据，适用于机密信息的输入
					
					# 提示用户输入姓名，并等待30秒，把用户输入保存入变量 name 中
					read -t 30 -p "please input your name: " name
					read -s -t 30 -p "please input your age: " age
					echo -e "\n"
					echo "age is $age"
					# 使用 -n l 选项只接收一个输入字符就会执行(不用回车就会执行)
					read -n l -t 30 -p "please select your gender[M/F]: " gender
					echo -e "\n"
					echo "sex is $gender"
	Bash 的运算符
		数值运算与运算符
			declare 声明变量类型
				declare [+/-][选项] 变量名
					选项：
						-			给变量设定类型属性
						+			取消变量的类型属性
						-i			将变量声明为整数型
						-x			将变量声明为环境变量
						-p			显示指定变量的被声明的类型
			数值运算
				aa=11
				bb=22
				# 给变量 aa 和 bb 赋值方式一
				declare -i cc=$aa+$bb
				# 给变量 aa 和 bb 赋值方式二，+号左右必须有空格
				dd=$(expr $aa + $bb)
				# 给变量 aa 和 bb 赋值方式三
				ff=$(($aa+$bb))
				gg=$[$aa+$bb]
			运算符
				优先级			运算符					说明
				------------------------------------------------------
					13			+，-				单目正、负运算符
					12			!，~				逻辑非、按位取反或补码
					11			*，/，%				乘、除、取模
					10			+，-				加、减
					9			<<，>>				按位左移、按位右移
					8			<=，>=，<，>		小于等于、大于等于、小于、大于
					7			==，!=				等于、不等于
					6			&					按位与
					5			^					按位异或
					4			|					按位或
					3			&&					逻辑与
					2			||					逻辑或
					1		=，+=，-=，*=，/=，%=
							&=，^=，|=，<<=，>>=	赋值、运算且赋值
							
					aa=$(((11+3)*3/2))
					bb=$((14%3))
					cc=$((1&&0))
		变量测试与内容替换
			通过 x 测试 y 的值
			变量置换方式			变量y没有设置			变量y为空值			变量y设置值
			---------------------------------------------------------------------------------------
			x=${y-新值}				x=新值					x为空				x=$y
			x=${y:-新值}			x=新值					x=新值				x=$y
			x=${y+新值}				x为空					x=新值				x=新值
			x=${y:+新值}			x为空					x为空				x=新值
			x=${y=新值}			x=新值，y=新值				x为空				x=$y
			x=${y:=新值}		x=新值，y=新值				x=新值，y=新值		x=$y
			x=${y?新值}			新值输出到标准错误输出		x为空				x=$y
			x=${y:?新值}		新值输出到标准错误输出	新值输出到标准错误输出	x=$y
								(屏幕)
			# 删除变量 y
			uset y
			x=${y-new}
			# 输出 new 因为变量 y 不存在，所以 x=new
			echo $x
			
			# 将变量 y 赋值为空
			y=""
			x=${y-new}
			echo $x
			
			y=old
			x=${y-new}
			echo $x
			echo $y
		${} 用法总结
			${var_name#*pattr}			第一个匹配到 pattr 之后的字符串
				file=/dir1/dir2/dir3/my.file.txt
				echo ${file#*/}
			${var_name##*pattr}			最后一个匹配到 pattr 之后的字符串
				file=/dir1/dir2/dir3/my.file.txt
				echo ${file##*/}
			${var_name%pattr*}			最后一个匹配到 pattr 之前的字符串
				file=/dir1/dir2/dir3/my.file.txt
				echo ${file%#/*}
			${var_name%%pattr*}			第一个匹配到 pattr 之前的字符串
				file=/dir1/dir2/dir3/my.file.txt
				echo ${file%%/*}
			${var_name:start:num}		提取从 start 位置开始后的 num 个字符（第一个字符位置为 0）
				file=/dir1/dir2/dir3/my.file.txt
				echo ${file:2:3}
			${var_name/pattr/var}		使用 $var 替换 $var_name 中第一个匹配到 pattr 的字符串
				file=/dir1/dir2/dir3/my.file.txt
				echo ${file/test/ttt}
			${var_name//pattr/var}		使用 $var 替换 $var_name 中所有匹配到 pattr 的字符串
				file=/dir1/dir2/dir3/my.file.txt
				echo ${file//test/ttt}
			${#var_name}				计算 $var_name 的长度
				file=/dir1/dir2/dir3/my.file.txt
				echo ${#file}
	环境变量配置文件
		环境变量配置文件简介
			source 命令
				source 配置文件		使配置文件立即生效
				或
				. 配置文件
			
			环境变量配置文件简介
				环境变量配置文件中主要是定义对系统的操作环境生效的系统默认环境变量，比如PAHT HISTSIZE PS1 HOSTNAME 等默认环境变量
				/etc/profile					环境变量对所有用户有效
				/etc/profile.d/*.sh
				~/.bash_profile					环境变量只对该用户有效
				~/.bashrc
				/etc/bashrc
		环境变量配置文件作用
			配置文件调用顺序：
			/etc/profile ---> ~/.bash_profile ---> ~/.bashrc ---> /etc/bashrc --->命令提示符
				|														|
				-------------------------> /etc/profile.d/*.sh <---------
												|
												--> /etc/profile.d/lang.sh ---> /etc/sysconfig/i18n
			/etc/profile 的作用
				USER 变量
				LOGNAME 变量
				MAIL 变量
				PATH 变量
				HOSTNAME 变量
				HISTSIZE 变量
				umask
				调用 /etc/profile.d/*.sh 文件
			~/.bash_profile 的作用
				调用了 ~/.bashrc 文件
				在 PATH 变量后面加入了 ":$HOME/bin" 这个目录
			~/.bashrc 的作用
				定义默认别名
				调用 /etc/bashrc
			/etc/bashrc 的作用
				PS1 变量
				umask
				PATH 变量
				调用 /etc/profile.d/*.sh 文件
		其他配置文件和登陆信息
			注销时生效的环境变量配置文件
				~/.bash_logout
			其他配置文件
				~/bash_history		历史命令记录文件，会受缓存影响
			Shell 登陆信息
				本地终端欢迎信息：/etc/issue
				
					\d			显示当前系统日期
					\s			显示操作系统名称
					\l			显示登陆的终端号
					\m			显示硬件体系架构
					\n			显示主机名
					\o			显示域名
					\r			显示内核版本
					\t			显示当前系统时间
					\u			显示当前登录用户的序列号
				远程终端欢迎信息：/etc/issue.net
					转义符在 /etc/issue.net 文件中不能使用
					是否显示此欢迎信息，由 ssh 的配置文件 /etc/ssh/sshd_config 决定，加入 "Banner" /etc/issue.net 行才能显示(记得重启SSH服务)
				登陆后欢迎信息：/etc/motd
					不管是本地登陆，还是远程登陆，都可以显示此欢迎信息
Shell 编程
	基础正则表达式
		正则表达式与通配符
			正则表达式用来在文件中匹配符合条件的字符串，正则是包含匹配。grep awk sed 等命令可以支持正则表达式
			通配符用来匹配符合条件的文件名，通配符是完全匹配。ls find cp 这些命令不支持正则表达式，所以只能使用 shell 自己的通配符来进行匹配
		基础正则表达式
			*				前一个字符匹配0次或任意多次
			.				匹配除了换行符外任意一个字符
			^				匹配行首。^hello 会匹配以 hello 开头的行
			$				匹配行尾。hello& 会匹配以 hello 结尾的行
			[]				匹配中括号中任意一个字符，只匹配一个字符。[a-z] 匹配人一个小写字母 [0-9] 匹配任意一个数字
			[^]				匹配除中括号的字符以外的任意一个字符。[^0-9] 匹配任意一个非数字字符
			\				转义符。用于取消特殊符号的含义
			\{n\}			表示其前面的字符恰好出现 n 次。[0-9]\{4\} 匹配4位数字
			\{n,\}			表示其前面的字符出现不小于 n 次。[0-9]\{2,\} 匹配两位数以上的数字
			\{n,m\}			表示其前面的字符出现至少 n 次，最多 m 次。[a-z]\{6,8\} 匹配6到8位的小写字母
	字符截取命令
		cut 字段提取命令
			cut [选项] 文件名
				选项：
					-f 列号			提取第几列
					-d 分隔符		按照指定的分隔符分割列
			cut -f 2 student.txt		按照制表符为分隔符提取 student.txt 文件的第二列
			cut -f 2,4 student.txt		按照制表符为分隔符提取 student.txt 文件的第2列到第4列
			cut -d ":" -f 1,3 /etc/passwd	按照冒号为分隔符提取第一列和第三列
			cut -d ":" -f 1-3 /etc/passwd						第一列到第三列
			cat /etc/passwd | grep /bin/bash | grep -v root | cut -d ":" -f 1
		printf 命令
			printf '输出类型输出格式' 输出内容
				输出类型：
					%ns			输出字符串。n 是数字，指代输出几个字符
					%ni			输出整数。n 是数字，指代输出几个数字
					%m.nf		输出浮点数。m n 是数字，指代输出的整数位和小数位。%8.2f代表共输出8位数，其中2位是小数，6位是整数
				输出格式：
					\a			输出警告声音
					\b			输出退格键，也就是 BackSpace 键
					\f			清除屏幕
					\n			换行
					\r			回车，也就是 Enter 键
					\t			水平输出退格键，也就是 Tab 键
					\v			垂直输出退格键，也就是 Tab 键
			printf %s 1 2 3 4 5 6
			printf '%s %s %s\n' 1 2 3 4 5 6
			printf '%s\t%s\t%s\t%s\t\n' $(cat student.txt)
		awk 命令
			在 awk 命令的输出中支持 print 和 printf 命令
				print 会在每个输出之后自动加入一个换行符(Linux 默认没有 print 命令)
				printf 是标准格式输出命令，并不会自动加入换行符，如果需要换行，需要手工加入换行符
			awk '条件1{动作1}...' 文件名
				条件：
					一般使用关系表达式作为条件
						x>10		判断变量x是否大于10
						x>=10		大于等于
						x<=10		小于等于
				动作：
					格式化输出 printf
					流程控制语句
				awk 默认先以空格或制表符为分隔符将数据分隔赋值给变量，然后根据格式输出变量
					awk '{printf $2 "\t" $6 "\n"}' student.txt
					df -h | awk '{print $1 "\t" $5 "\t" $6}'
					df -h | grep sda5 | awk '{print $5}' | cut -d "%" -f 1
				BEGIN 在 awk 处理数据之前执行
					awk 'BEGIN{print "Test"} {print $2 "\t" $5}' student.txt
				FS 指定分隔符(类似于 cut -d 的作用)
					cat /etc/passwd | grep "/bin/bash" | awk 'BEGIN {FS=":"} {print $1 "\t" $3}'
				END 在 awk 处理完数据之后执行
					awk 'END{printf "The End\n"} {printf $2 "\t" $6 "\n"}' student.txt
				关系运算符
					cat student.txt | grep -v Name | awk '$6>=87 {print $2}'
		sed 命令
			sed 是一种几乎包括所有 UNIX 平台(包括 Linux) 的轻量级流编辑器。sed 主要是用来将数据进行选取、替换、删除、新增的命令
			sed [选项] '[动作]' 文件名
				选项：
					-n		一般 sed 命令会把所有的数据都输出到屏幕，如果加入次选择，则只会把经过 sed 命令处理的行输出到屏幕
					-e		允许对输入数据应用多条 sed 命令进行编辑
					-i		用 sed 的修改结果直接修改读取数据的文件，而不是由屏幕输出
				动作：
					a\		追加，在当前行后添加一行或多行。添加多行时，除最后一行外，每行末尾需要用 \ 代表数据未完结
					c\		行替换，用 c 后面的字符串替换原数据行，替换多行时，除最后一行外，每行末尾需用 \ 代表数据未完结
					i\		插入，在当前行前插入一行或多行。插入多行时，除最后一行外，每行末尾需要用 \ 代表数据未完结
					d		删除，删除指定的行
					p		打印，输出指定的行
					s		字符串替换，用一个字符串替换另外一个字符串。格式为 "行范围s/旧字符串/新字符串/g"
			sed -n '2p' student.txt			打印文件第二行
			df -h | sed -n '2p'
			sed '2,4d' student.txt			删除第二行到第四行的数据，但不修改文件本身
			sed '2a hello' student.txt		在第二行后追加 hello
			sed '2i hello world' student.txt	在第二行前插入数据
			sed '2c No such person' student.txt		将第二行替换成 No such person
			sed 's/74/99/g'					将第三行的 74 替换成 99
			sed -e 's/Liming//g;s/Gao//g' student.txt	批量替换成空串
	字符处理命令
		排序命令sort
			sort [选项] 文件名
				选项：
					-f				忽略大小写
					-n				以数值型进行排序，默认使用字符串型排序
					-r				反向排序
					-t				指定分隔符，默认的分隔符是制表符
					-k n[,m]		按照指定的字段范围排序，从第 n 字段开始，m 字段结束(默认到行尾)
				
			sort /etc/passwd
			sort -n -t ":" -k 3,3 /etc/passwd
		统计命令 wc
			wc [选项] 文件名
				选项：
					-l			只统计行数
					-w			只统计单词数
					-m			只统计字符数
			df -h | wc -l
	条件判断
		按照文件类型进行判断
			-b 文件			判断该文件是否存在，并且是否为块设备文件(是块设备文件为真)
			-c 文件			判断该文件是否存在，并且是否为字符设备文件(是字符设备文件为真)
			-d 文件			判断该文件是否存在，并且是否为目录文件(是目录文件为真)
			-e 文件			判断该文件是否存在(存在为真)
			-f 文件			判断该文件是否存在，并且是否为普通文件(是普通文件为真)
			-L 文件			判断该文件是否存在，并且是否是符号链接文件(是符号链接文件为真)
			-p 文件			判断该文件是否存在，并且是否为管道文件(是管道文件为真)
			-s 文件			判断该文件是否存在，并且是否为非空(非空为真)
			-S 文件			判断该文件是否存在，并且是否为套接字文件(是套接字文件为真)
			
			-a				判断该文件是否存在(存在为真)存在(存在为真)
			-h				判断该文件是否为符号链接(也称作软链接，是则为真)
			
			判断格式：
				test -e "/root/install.log"
				[ -e "/root/install.log" ]
				[ -d "/root" ] && echo "yes" || echo "no"
		按照文件权限进行判断(不区分所有者、所属组、其他人)
			-r 文件			判断该文件是否存在，并且是否拥有读权限(有读权限为真)
			-w 文件			判断该文件是否存在，并且是否拥有写权限(有写权限为真)
			-x 文件			判断该文件是否存在，并且是否拥有可执行权限(有可执行权限为真)
			-u 文件			判断该文件是否存在，并且是否拥有SUID权限(有SUID权限为真)
			-g 文件			判断该文件是否存在，并且是否拥有SGID权限(有SGID权限为真)
			-k 文件			判断该文件是否存在，并且是否拥有SBit权限(有SBit权限为真)
			
			[ -w "student.txt" ] && echo "yes" || echo "no"		student.txt 如果有写权限输出 yes 否则输出 no
		两个文件之间进行比较
			文件1 -nt 文件2			判断文件1的修改时间是否比文件2的新(如果是则为真)
			文件1 -ot 文件2			判断文件1的修改时间是否比文件2的旧(如果是则为真)
			文件1 -ef 文件2			判断文件1是否和文件2的 Inode 一致，可以判断硬链接
			
			ln /root/student.txt /tmp/stu.txt		创建硬链接
			[ "/root/student.txt" -ef "/tmp/stu.txt" ] && echo "yes" || echo "no"
		两个整数之间的比较
			整数1 -eq 整数2			判断整数1是否等于整数2(是则为真)
			整数1 -ne 整数2			判断整数1是否不等于整数2(是则为真)
			整数1 -gt 整数2			判断整数1是否大于整数2(是则为真)
			整数1 -lt 整数2			判断整数1是否小于整数2(是则为真)
			整数1 -ge 整数2			判断整数1是否大于等于整数2(是则为真)
			整数1 -le 整数2			判断整数1是否小于等于整数2(是则为真)
			
			[ 23 -ge 22 ] && echo "yes" || echo "no"
		字符串的判断
			-z 字符串				判断字符串是否为空(是则为真)
			-n 字符串				判断字符串是否为非空(是则为真)
			字符串1 == 字符串2		判断字符串1是否和字符串2相等(是则为真)
			字符串1 != 字符串2		判断字符串1是否和字符串2不相等(是则为真)
			字符串					字符串非空为真
			
			# 给变量 name 赋值
			name=sc
			# 判断 name 变量是否为空
			[ -z "$name" ] && echo "yes" || echo "no"
			# 判断是否相等
			[ "$name" == abc ] && echo yes || echo no
		多重条件判断
			判断1 -a 判断2			逻辑与，判断1和判断2都成立，最终结果才为真
			判断1 -o 判断2			逻辑或，判断1或判断2为真，最终结果为真
			! 判断					逻辑非，原始的判断取反
			
			aa=11
			[ -n "$aa" -a "$aa" -gt 23 ] && echo "yes" || echo "no"
	流程控制
		if 语句
			单分支 if 条件语句
				if [ 条件判断式 ];then
					程序
				fi
			或
				if [ 条件判断式 ]
					then
						程序
				fi
				
				注意：
					if 语句使用 fi 结尾，和一般语言使用大括号结尾不同
					[ 条件判断式 ] 就是使用 test 命令判断，所以中括号和条件判断式之间必须有空格
					then 后面跟符合条件之后执行的程序，可以放在 [] 之后，用 ; 分隔。也可以换行写入就不需要 ; 了
				
				#!bin/bash
				#统计根分区使用率
				rate=$(df -h | grep "/dev/sda3" | awk '{print $5}' | cut -d "%" -fl)
				if [ $rate -ge 80 ];then
					echo "Warning ! /dev/sda3 is full !!"
				fi
			双分支 if 条件语句
				if []
					then
						条件成立时执行的程序
					else
						条件不成立时执行的程序
				fi
				
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
						tar -zcvf mysql-lib-$date.tar.gz /var/lig/mysql dbinfo.txt &> /dev/null
						rm -rf /tmp/dbbak/dbinfo.txt
					else
						mkdir /tmp/dbbak
						echo "Date: $date!" > /tmp/dbbak/dbinfo.txt
						echo "Data size: $size" > /tmp/dbbak/dbinfo.txt
						cd /tmp/dbbak
						tar -zcvf mysql-lib-$date.tar.gz /var/lig/mysql dbinfo.txt &> /dev/null
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
			多分支 if 语句
				if [ 条件判断式1 ]
					then
						条件成立时执行的程序
				elif [ 条件判断式2 ]
					then
						条件成立时执行的程序
				else
					条件不成立时执行的程序
				fi
				
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
					 
		case 语句
			case 语句和 if...elif...else 语句一样都是多分支条件语句，不过和 if 多分支条件语句不同的是，case 语句只能判断一种条件关系，而 if 语句可以判断多种条件关系
			case $变量名 in
				"值1")
					符合条件的执行程序
					;;
				"值2")
					符合条件的执行程序
					;;
				*)
					如果变量的值都不匹配则执行此程序
					;;
			esac
			
			#!/bin/bash
			#判断用户输入
			
			read -p "Please choose yes/no: " -t 30 cho
			case $cho in
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
		for 循环
			语法一：
				for 变量 in 值1 值2 值3
					do
						程序
					done
				
				#!/bin/bash
				#打印时间
				
				for time in morning noon afternoon evening
					do
						echo "This time is $time!"
					done
				
				#!/bin/bash
				#批量解压缩脚本
				
				cd /lamp
				ls *.tar.gz > ls.log
				for i in $(cat ls.log)
					do
						tar -zxvf $i &> /dev/null
					done
				rm -rf /lamp/ls.log
			语法二：
				for((初始值;循环控制条件;变量变化))
					do
						程序
					done
			
				#!/bin/bash
				#从 1 加到 100				
				
				s=0
				for((i=1;i<=100;i=i+1))
					do
						s=$(($s+$i))
					done
				echo "The sum of 1+2+...+100 = $s"
				
				#!/bin/bash
				#批量添加指定数量的用户
				
				read -p "Please input user name: " -t 30 name
				read -p "Please input the number of users: " -t 30 num
				read -p "Please input the password of users: " -t 30 pass
				if[ ! -z "$name" -a ! -z "$num" -a ! -z "$pass" ]
					then
						y=$(echo $num | sed 's/[0-9]//g')
							if[ -z "$y" ]
								then
									for((i=1;i<=$num;i++))
										do
											/usr/sbin/useradd/ $name$i &> /dev/null
											echo $pass | /usr/bin/passwd --stdin $name$i &> /dev/null
										done
							fi
				fi
		while 循环与 until 循环
			while 循环
				while 循环是不定循环，也称作条件循环。只要条件判断式成立，循环就会一直继续，知道条件判断式不成立，循环才会停止
				
				while [ 条件判断式 ]
					do
						程序
					done
					
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
			until 循环
				until 循环和 while 循环相反，until 循环只要条件判断式不成立就执行循环，直到条件判断式成立才停止循环
				
				until [ 条件判断式 ]
					do
						程序
					done
					
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
	函数
		自定义函数
			func_name () {
				func_body
				[return result]
			}
			注意：
				必须在调用函数地方之前声明函数；
				函数只能返回 0~255 之间的数字；
				函数的返回值只能通过 $? 获取；
	脚本调试：
		sh -vx xxxx.sh
Linux 服务管理
	服务简介与分类
		服务的分类
												  |-------独立的服务
					  |-------RPM 包默认安装的服务|
					  |							  |-------基于 xinetd 服务
			Linux 服务|
					  |-------源码包安装的服务
		启动与自启动
			服务启动：在当前系统中让服务运行并提供功能
			服务自启动：让服务在开机或重启之后自动启动服务
		查询已安装的服务
			RPM 包安装的服务
				chkconfig --list		查看服务自启动状态，可以看到所有 RPM 包安装的服务
			源码包安装的服务
				查看服务安装位置，一般是 /usr/local/ 下
			RPM 安装服务和源码包安装服务的区别
				RPM 包安装服务和源码包安装服务的区别就是安装的位置不同：
					源码包安装在指定位置，一般是 /usr/local/
					RPM 包安装在默认位置中
						/etc/init.d/				启动脚本位置
						/etc/sysconfig/				初始化环境配置文件位置
						/etc/						配置文件位置
						/etc/xinetd.config/			xinetd 配置文件
						/etc/xinetd.d/				基于 xinetd 服务的启动脚本
						/var/lib/					服务产生的数据
						/var/log/					日志
	RPM 包安装服务的管理
		独立服务的管理
			独立服务的启动：
				/etc/init.d/服务启动名 start|stop|status|restart		推荐使用
				service 独立服务名 start|stop|status|restart			redhat 系列专有命令
			独立服务的自启动：
				chkconfig [--level 运行级别] [独立服务名] [on|off]
				修改 /etc/rc.d/rc.local 文件
					该文件见系统启动会运行，在该文件中写入服务启动命令则会在开机时开启服务
				使用 ntsysv 命令管理自启动			redhat 专有
		基于 xinetd 服务
			安装 xinetd 与 telnet
				yum -y install xinetd
				yum -y install telnet-server
			xinetd 服务的启动
				vi /etc/xinetd.d/telnet
				
				#服务的名称为 telnet
				service telnet
				{
					#设定 TCP/IP socket 可重用
					flags=REUSE
					#使用 TCP 协议数据包
					socket_type=stream
					#允许多个连接同时连接
					wait=no
					#启动服务的用户
					user=root
					#服务的启动程序
					server=/usr/sbin/in.telnetd
					#登录失败后记录用户的ID
					log_on_failure +=USERID
					#服务不启动
					disable=no
				}
				#重启 xinetd 服务
				service xinetd restart
			xinetd 服务自启动
				chkconfig telnet on
				ntsysv
	源码包安装服务的管理
		源码包安装服务的启动
			使用绝对路径，调用启动脚本来启动。不同的源码包的启动脚本不同。可以查看源码包的安装说明，查看启动脚本的方法
				/usr/local/apache2/bin/apachectl start|stop
		源码包服务的自启动
			vi /etc/rc.d/rc.local
			加入：
				/usr/local/apache2/bin/apachectl start
		让源码包服务被服务管理命令识别
			让源码包的服务能被 service 命令管理启动
				ln -s /usr/local/apache2/bin/apachectl /etc/init.d/apache
			让源码包的服务能被 chkconfig 和 ntsysv 命令管理自动启动
				vi /etc/init.d/apache
				#指定 httpd 脚本可以被 chkconfig 命令管理。格式是 chkconfig:运行级别 启动顺序 关闭顺序
				#chkconfig:25 86 76
				#说明
				#description:source package apache
				
				chkconfig --add apache			把源码包 apache 加入 chkconfig 命令
	服务管理总结
		Linux 服务
			RPM 默认安装的服务
				独立的服务
					启动
						使用 /etc/init.d/ 目录中脚本启动服务，如：/etc/init.d/httpd start|restart|stop|status
						使用 service 启动服务，如：sevice http start|restart|stop|status
					自启动
						使用 chkconfig 命令管理服务自启动，如：chkconfig --level 2345 on|off
						修改 /etc/rc.d/ 目录下的文件，如：vi /etc/rc.d/rc.local 加入 /etc/init.d/httpd start
						使用 ntsysv 命令管理服务自启动
				基于 xinetd 服务
					启动
						修改 /etc/xinetd.d/配置文件，如 vi /etc/xinetd.d/telnet 把 disable=yes 改为 disable=no 重启 xinetd 服务
					自启动
						使用 chkconfig 管理，如：chkconfig telnet on|off
			源码包安装的服务
				启动
					使用源码包服务启动脚本，如：/usr/local/apache2/bin/apachectl start
				自启动
					修改 /etc/rc.d/rc.local 文件，如：vi /etc/rc.d/rc.local 加入 /usr/local/apache2/bin/apachectl start

	CentOS7 使用 systemctl 命令管理服务
		systemctl [command] [unit]
			command:
				start			立即启动 unit
				stop			立即关闭 unit
				restart			立即重启 unit
				reload			不关闭 unit 的情况下重新载入配置
				enable			设定下次开机时自动启动 unit
				disable			设定下次开机时不起动 unit
				status			查看 unit 的状态
				is-active		unit 是否在运行
				is-enabled		unit 是否是开机启动
				list-units		查看所有启动的服务


日志管理
	日志管理简介
		日志服务
			rsyslogd 的特点：
				基于 TCP 网络协议传输日志信息；
				更安全的网络传输方式；
				有日志消息的及时分析框架；
				后台数据库；
				配置文件中可以写简单的逻辑判断；
				与 syslog 配置文件兼容；
			确定服务启动
				ps aux | grep rsyslogd				查看服务是否启动
				chkconfig --list | grep rsyslog		查看服务是否自启动
		常见日志的作用
			日志文件						说明
			------------------------------------------------------
				/var/log/cron			记录了系统定时任务相关的日志
				/var/log/cups/			记录打印信息的日志
				/var/log/dmesg			记录了系统正在开机时内核自检的信息。也可以使用 dmesg 命令直接查看内核自检信息
				/var/log/btmp			记录错误登录的日志。这个文件是二进制文件，不能直接 vi 查看，而要使用 lastb 命令查看
				/var/log/lastlog		记录系统中所有用户最后一次的登录时间的日志。这个文件也是二进制文件，不能直接 vi，而要用 lastlog 命令查看
				/var/log/maillog		记录邮件信息
				/var/log/message		记录系统重要信息的日志。这个日志文件中会记录 Linux 系统的绝大多数重要信息，如果系统出现问题时，首先要检查的就应该是这个日志文件
				/var/log/secure			记录验证和授权方面的信息，只要涉及账户和密码的程序都会记录。比如说系统的登陆，ssh的登陆，su切换用户，sudo授权，甚至添加用户和修改用户密码都会记录在这个日志文件中
				/var/log/wtmp			永久记录所有用户的登陆、注销信息，同时记录系统的启动、重启、关机事件。同样这个文件也是一个二进制文件，不能直接 vi，而需要使用 last 命令来查看
				/var/run/utmp			记录当前已经登陆的用户的信息。这个文件会随着用户的登陆和注销不断变化，只记录当前登录用户的信息。同样这个文件不能直接使用 vi 查看，而要使用w who users 等命令来查询
		除了系统默认的日志之外，采用 RPM 方式安装的系统服务也会默认把日志记录在 /var/log/ 目录中(源码包安装的服务日志是在源码包指定目录中)。不过这些日志不是由 rsyslogd 服务来记录和管理的，而是各个服务使用自己的日志管理文档来记录自身日志
	ryslogd 日志服务
		日志文件格式
			基本日志格式包含以下四列：
				事件产生的时间；
				发生事件的服务器的主机名；
				产生事件的服务名或程序名；
				事件的具体信息；
			/etc/rsyslog.conf 配置文件
				格式：
					服务名称.日志等级 日志记录位置
				服务名称
					auth					安全和认证相关消息(不推荐使用 authpriv 替代)
					authpriv				安全和认证相关消息(私有的)
					cron					系统定时任务 cront 和 at 产生的日志
					daemon					和各个守护进程相关的日志
					ftp						ftp 守护进程产生的日志
					kern					内核产生的日志(不是用户进程产生的)
					local0-local7			为本地使用预留的服务
					lpr						打印产生的日志
					mail					邮件收发信息
					news					与新闻服务器相关的日志
					syslog					有 syslogd 服务产生的日志信息(虽然服务名称已经改为 rsyslogd 但是很多配置都还是沿用了 syslogd 的，这里并没有修改服务名)
					user					用户等级类别的日志信息
					uucp					uucp 子系统的日志信息，uucp 是早期 linux 系统进行数据传递的协议，后来也常用在新闻组服务中
				日志等级
					debug					一般的调试信息说明
					info					基本的通知信息
					notice					普通信息，但是有一定的重要性
					warning					警告信息，但是还不会影响到服务或系统的运行
					err						错误信息，一般会影响到服务或系统的运行
					crit					临界状况信息，比较严重
					alert					警告状态信息，更加严重，必须立即采取行动
					emerg					紧急情况，系统已经无法使用了
				.*			代表记录所有日志等级的日志
				.info		代表记录日志等级大于或等于 info 级别的日志
				.=err		代表只记录日志等级等于 err 级别的日志
				.!=debug	代表除了 debug 等级的日志外，其他级别日志都记录
				
				日志记录位置
					日志文件的绝对路径，如 /var/log/secure
					系统设备文件，如 /dev/lp0
					转发给远程主机，如 @192.168.0.210:514
					用户名，如 root
					忽略或丢弃日志，如 ~
	日志轮替
		日志文件的命名规则
			如果配置文件中有 dateext 参数，那么日志会用日期来作为日志文件的后缀，如 secure-20130605。这样的话日志文件不会重叠，所以也就不需要日志文件改名，只需要保存指定的日志个数，删除多余的日志文件即可
			如果配置文件中没有 dateext 参数，那么日志文件就需要进行改名了。当第一次进行日志轮替时，当前的 secure 日志会自动改名为 secure.1 然后新建 secure 日志用来保存新的日志；当第二次进行日志轮替时，secure.1 会自动改名为 secure.2 当前的 secure 会自动改名为 secure.1 然后新建 secure 用来保存新的日志；以此类推
		logrotate 配置文件 /etc/logrotate.conf
			daily/weekly/monthly			日志的轮替周期是每天/每周/每月
			rotate 数字						保留的日志文件的个数，0指没有备份
			compress						日志轮替时，旧的日志进行压缩
			create mode owner group			建立新日志，同时指定新日志的权限与所有者和所属组，如 create 0600 root utmp
			mail address					当日志轮替时，输出内容通过邮件发送到指定的邮件地址，如 mail henry@qq.com
			missingok						如果日志为空文件，则不进行日志轮替
			notifempty						如果日志文件为空文件，则不进行日志轮替
			minsize 大小					日志轮替的最小值，也就是日志一定要达到这个最小值才会轮替，否则就算时间达到也不轮替
			size 大小						日志只有大于指定大小才进行日志轮替，而不是按照时间轮替，如 size 100K
			dateext							使用日期作为日志轮替文件的后缀，如 secure-20130605
		把源码包安装的程序日志加入轮替
			rpm 包安装的程序日志自动加入轮替，但是源码包安装的程序日志没有加入轮替
			vi /etc/logrotate.conf	加入apache 日志轮替配置
			/usr/local/apache2/logs/access_log{
				daily
				create
				rotate 30
			}
		logrotate 命令
			logrotate [选项] 配置文件名
				选项：
					如果此命令没有选项，则会按照配置文件中的条件进行日志轮替
					-v			显示日志轮替过程
					-f			强制配置文件中的所有日志进行轮替

启动管理
	CentOS 6.3 启动管理
		系统运行级别
			0		关机
			1		单用户模式，主要用于系统修复
			2		不完全的命令模式，不含 NFS 服务
			3		完全的命令模式，是标准的字符界面
			4		系统保留
			5		图形模式
			6		重启动
			
			runlevel			查看运行级别
			init 运行级别		改变运行级别
			
			系统默认运行级别
				vim /etc/inittab
				
				id:3:initdefault:			系统开机后直接进入运行级别
		系统启动过程
			BIOS->加载->MBR->加载->MBR中启动引导程序->单系统直接启动->加载内核---------------------------------------------------
													|																			|
													->多系统->调用其它分区启动区中的启动引导程序->启动不同的操作系统->加载内核---
																																|
								  /sbin/init<-调用<-挂载真正系统根目录<-加载驱动<-建立仿真目录<-找到 initramfs<-内核解压并自检<-|
									|
									调用				  |->调用->/etc/rc.d/rc.sysinit->系统初始化
									|->/etc/init/rcS.conf-|
														  |->调用->/etc/inittab->传入运行级别->/etc/init/rc.conf->调用->/etc/rc.d/rc
																																|
			  登陆界面<-/etc/rc.d/rc.local中的程序<-启动和关闭<-按照优先级启动和关闭相应脚本<-/etc/rc[0-6].d<-按照运行级别调用<-|
				|
			输入用户名和密码->进入系统
			
			initramfs 内存文件系统
				CentOS 6.3 中使用 initramfs 内存文件系统取代了 CentOS 5 中的 initrd RAM Disk。他们的作用类似，可以通过启动引导程序加载到内存中，然后加载启动过程中所需要的内核模块，比如 USB SATA SCSI 硬盘的驱动和 LVM RAID 文件系统的驱动
				
				mkdir /tmp/initramfs
				cp /boot/initramfs-2.6.32-279.el6.i686.img /tmp/initramfs/
				cd /tmp/initramfs/
				file initramfs-2.6.32-279.e16.i686.img
				mv initramfs-2.6.32-279.el6.i686.img initramfs-2.6.32-279.el6.i686.img.gz
				gunzip initramfs-2.6.32-279.el6.i686.img.gz
				file initramfs-2.6.32-279.el6.i686.img
				cpio -ivcdu <initramfs-2.6.32-279.el6.i686.img
			调用 /etc/init/rcS.conf 配置文件
				主要功能是两个：
					先调用 /etc/rc.d/rc.sysinit，然后由 /etc/rc.d/rc.sysinit 配置文件进行 Linux 系统初始化
					然后再调用 /etc/inittab，然后由 /etc/inittab 配置文件确定系统的默认运行级别
			/etc/rc.d/rc.sysinit 初始化
				1.获得网络环境
				2.挂载设备
				3.开机启动画面 Plymouth
				4.判断是否启用 SELinux
				5.显示开机过程中的欢迎画面
				6.初始化硬件
				7.用户自定义模块加载
				8.配置内核的参数
				9.设置主机名
				10.同步存储器
				11.设备映射器及相关的初始化
				12.初始化软件磁盘阵列(RAID)
				13.初始化 LVM 的文件系统功能
				14.检验磁盘文件系统(fsck)
				15.设置磁盘配额(quota)
				16.重新以可读写模式挂载系统磁盘
				17.更新 quota
				18.启动系统虚拟随机数生成器
				19.配置机器
				20.清除开机过程中的临时文件
				21.创建 ICE 目录
				22.启动交换分区
				23.将开机信息写入 /var/log/dmesg 文件中
			调用 /etc/rc.d/rc 文件
				运行级别参数传入 /etc/rc.d/rc 这个脚本之后，由这个脚本文件按照不同的运行级别启动 /etc/rc[0-6].d/ 目录中的相应的程序
					/etc/rc3.d/K?? 开头的文件(??是数字)，会按照数字顺序依次关闭
					/etc/rc3.d/S?? 开头的文件(??是数字)，会按照数字顺序依次启动
	启动引导程序 grub
		grub 配置文件
			grub 中分区表示
				硬盘				分区			Linux 中设备文件名		Grub 中设备文件名
				-------------------------------------------------------------------------------
				第一块SCSI硬盘		第一个主分区		/dev/sda1				hd(0,0)
									第二个主分区		/dev/sda2				hd(0,1)
									扩展分区			/dev/sda3				hd(0,2)
									第一个逻辑分区		/dev/sda5				hd(0,4)
				-------------------------------------------------------------------------------
				第二块SCSI硬盘		第一个主分区		/dev/sdb1				hd(1,0)
									第二个主分区		/dev/sdb2				hd(1,1)
									扩展分区			/dev/sdb3				hd(1,2)
									第一个逻辑分区		/dev/sdb5				hd(1,4)
				--------------------------------------------------------------------------------
			
			grub 配置文件 /boot/grub/bgrub.conf
				default=0			默认启动第一个系统
				timeout=5			等待时间，默认是5秒
				splashimage=(hd0,0)/grub/splash.xpm.gz		指定 grub 启动时的背景图像文件的保存位置
				hiddenmenu			隐藏菜单
				title CentOS(2.6.32-279.el6.i686)
					root(hd0,0)		启动程序的保存分区
					kernel /vmlinuz-2.6....			定义内核加载时的选项
					initrd /initramfs...		指定 initramfs 内存文件系统镜像文件的所在位置
					
		grub 加密与字符界面分辨率调整
			grub 加密
				grub-md5-crypt			生成加密密码串
				
				vi /boot/grub/grub.conf
				
				default=0
				timeout=5
				password --md5 加密密码串
				splashimage=(hd0,0)/grub/splash.xpm.gz
				...
			纯字符界面分辨率调整
				grep "CONFIG_FRAMEBUFFER_CONSOLE" /boot/config-2.6.32-279.el6.i686		查询内核是否支持分辨率修改
				
						640*480		800*600		1024*768	1280*1024
				-------------------------------------------------------
				8位		769				771			773			775
				15位	784				787			790			793
				16位	785				788			791			794
				32位	786				789			792			795
				-------------------------------------------------------
				vi /boot/grub/grub.conf
					kernal /vmlinuz-....... vga=791
	系统修复模式
		单用户模式常见的错误修复
			启动时按任意键盘，进入单用户模式
			遗忘 root 密码
			修改系统默认运行级别
		光盘修复模式
			启动时按 F2 进入光盘修复安装模式
			重要系统文件丢失，导致系统无法启动
				# 改变主目录
				bash-4.1# chroot /mnt/sysimage
				sh-4.1# cd /root
				# 查询 /etc/inittab 文件属于哪个 rpm 包
				sh-4.1# rpm -qf /etc/inittab
				# 建立挂载点
				sh-4.1# mkdir /mnt/cdrom
				# 挂载光盘
				sh-4.1# mount /dev/sr0 /mnt/cdrom
				# 提取 inittab 文件到当前目录
				sh-4.1# rpm2cpio /mnt/cdrom/Packages/initscripts.. |cpio -idv ./etcinittab
				# 复制 inittab 文件到指定位置
				cp etc/inittab /etc/inittab
		
备份与恢复
	备份概述
		Linux 系统需要备份的数据
			/root/ 目录
			/home/ 目录
			/var/spool/mail/ 目录
			/etc/ 目录
			其他目录
		安装服务的数据
			apache 需要备份的数据
				配置文件
				网页主目录
				日志文件
			mysql 需要备份的数据
				源码包安装的 mysql /usr/local/mysql/data/
				rpm 包安装的 mysql /var/lib/mysql/
		备份策略
			完全备份：每次都把所有需要备份的数据全部备份
			增量备份：只备份新增的数据，而不需要备份重复的数据
			差异备份：每次只备份第一次备份中不存在的数据
	dump 和 restore 命令
		dump 命令
			dump [选项] -f 备份之后的文件名 原文件或目录
				选项：
					-[0-9]			0-9十个备份级别
					-u				备份成功后把备份时间记录在 /etc/dumpdates 文件
					-v				显示备份过程中更多的输出信息
					-j				调用 bzlib 库压缩备份文件，把文件压缩为 .bz2 格式
					-W				显示允许被 dump 的分区的备份等级及备份时间
			备份分区
				# 完全备份
				dump -0uj -f /root/boot.bak.bz2 /root/
				# 查看备份时间
				cat /etc/dumpdates
				cp install.log /boot/
				# 增量备份
				dump -1uj -f /root/boot.bak1.bz2 /root/
				# 查看分区的备份时间及备份级别
				dump -W
			备份文件或目录
				文件和目录只能完全备份，不支持增量备份
				dump -0j -f /root/etc.dump.bz2 /etc/
		restore 命令
			restore [模式选项] [选项]
				模式选项：
					-C		比较备份数据和实际数据的变化
					-i		进入交互模式，手工选择需要恢复的文件
					-t		查看模式，用于查看备份文件中拥有哪些数据
					-r		还原模式，用于数据还原
				选项：
					-f		指定备份文件的文件名
					
					
CentOS 7以上是用 Systemd 进行系统初始化的，Systemd 是 Linux 系统中最新的初始化系统，主要目标是克服 sysvint 固有的缺点，提高系统的启动速度
Systemd 服务文件以 .service 结尾，如果是 yum install 命令安装的，则系统会自动创建对应的 .service 文件；
.service 文件存放在 /lib/systemd/system 下；
如果是源码包安装，则需要手动创建 .service 文件
	[Unit] 
	Description=nginx 
	After=network.target 

	[Service] 
	Type=forking 
	ExecStart=/usr/local/nginx/sbin/nginx 
	ExecReload=/usr/local/nginx/sbin/nginx -s reload 
	ExecStop=/usr/local/nginx/sbin/nginx -s quit
	PrivateTmp=true 

	[Install] 
	WantedBy=multi-user.target
	
	Unit		服务的说明
		Description		服务描述
		After			服务在什么时候启动
		Conflicts		和什么服务冲突
	Service		服务运行参数设置
		Type=forking	后台运行
			simple	在设置 nohup java -jar 时服务器启动一段时间后自动关闭，设置为 simple 解决
		ExecStart		服务运行的命令，一定要使用绝对路径
		ExecReload		服务重启命令
		ExecStop		服务停止命令
		PrivateTmp=true	表示给服务分配独立的临时空间
	Install		运行级别
		WantedBy=multi-user	表示在多用户时启动
创建完 .service 文件后，使用 systemctl enable xxx.service 命令使开机自启动生效
		
systemctl start xxx.service				启动服务
systemctl stop xxx.service				停止服务
systemctl enable xxx.service			设置开机启动服务
systemctl disable xxx.service			停止开机启动服务
systemctl status xxx.service			查看服务当前状态
systemctl restart xxx.service			重启服务
systemctl list-units --type=service		查看所有服务
systemctl reload xxx.service			重新加载配置文件而不关闭服务
systemctl is-enabled xxx.service		是否设置为开机重启

运行级别
	Sysvinit			Systemd
	0					runlevel0.target/poweroff.target
	1					runlevel1.target/rescue.target
	2					runlevel2.target
	3					runlevel3.target/multi-target
	4					runlevel4.target
	5					runlevel5.target/grphical.target
	6					runlevel6.target/reboot.target
	
journalctl -u xxx.service
	
防火墙设置
	防火墙也是作为一个服务被管理的
		启动防火墙服务：systemctl start firewalld
		查看防火墙服务状态：systemctl status firewalld
		停止防火墙服务：systemctl disable firewalld
		禁用防火墙：systemctl stop firewalld
	也可以用 .service 管理服务：
		启动一个服务：systemctl start firewalld.service
		关闭一个服务：systemctl stop firewalld.service
		重启一个服务：systemctl restart firewalld.service
		显示一个服务的状态：systemctl status firewalld.service
		在开机时启用一个服务：systemctl enable firewalld.service
		在开机时禁用一个服务：systemctl disable firewalld.service
		查看服务是否开机启动：systemctl is-enabled firewalld.service
		查看已启动的服务列表：systemctl list-unit-files|grep enabled
		查看启动失败的服务列表：systemctl --failed
	防火墙的一些常用配置
		查看版本： firewall-cmd --version
		查看帮助： firewall-cmd --help
		显示状态： firewall-cmd --state
		查看所有打开的端口： firewall-cmd --zone=public --list-ports
		更新防火墙规则： firewall-cmd --reload
		查看区域信息:  firewall-cmd --get-active-zones
		查看指定接口所属区域： firewall-cmd --get-zone-of-interface=eth0
		拒绝所有包：firewall-cmd --panic-on
		取消拒绝状态： firewall-cmd --panic-off
		查看是否拒绝： firewall-cmd --query-panic
		永久开放端口： firewall-cmd --zone=public --add-port=8080/tcp --permanent		
							--zone		作用域
							--add-port=8080/tcp		添加端口 端口/协议
							--permanent	永久生效
		查看端口：firewall-cmd --zone= public --query-port=80/tcp
		删除端口：firewall-cmd --zone= public --remove-port=80/tcp --permanent


Linux 网络基础
	常见一级域名
		组织一级域名
			edu			教育机构
			com			商业组织
			gov			非军事政府机构
			mil			军事机构
			org			其他组织
			net			网络服务机构
		地区一级域名
			au			澳大利亚
			cn			中国
			in			印度
			us			美国
			uk			英国
网络通信协议
	OSI/ISO七层模型和TCP/IP四层模型
		OSI的七层框架
			7 应用层<--------应用层协议--------->应用层	APDU
				|									|
			6 表示层<--------表示层协议--------->表示层	PPDU
				|									
			5 会话层<--------会话层协议--------->会话层	SPDU
				|									|
			4 传输层<--------传输层协议--------->传输层	TPDU
				|									|
			3 网络层<--------网络层协议--------->网络层	报文
				|									|
			2 数据链路层<----数据链路层协议----->数据链路层	帧
				|									|
			1 物理层<--------物理层协议--------->物理层	比特
			
			层 主机A							主机B	数据单位
			
			应用层			用户接口
			表示层			数据的表现形式、特定功能的实现(如：加密)
			会话层			对应用层会话的管理、同步
			传输层			可靠(TCP)与不可靠(UDP)的传输、传输前的错误检测、流量控制
			网络层			提供逻辑地址(IP)、路由选择
			数据链路层		成帧、用 MAC 地址访问媒介、错误检测与修正
			物理层			设备之间的比特流的传输、物理接口、电气特性等
		TCP/IP协议四层模型
			应用层(对应 OSI模型的应用层、表示层、会话层)
				|
			传输层(对应 OSI 模型的传输层)
				|
			网际互联层(对应 OSI 模型的网络层)
				|
			网络接口层(对应 OSI 模型的数据链路层、物理层)
			
			网络接口层
				网络接口层与 OSI 参考模型中的物理层和数据链路层相对应。它负责监视数据在主机和网络之间的交换。
				事实上，TCP/IP 本身并未定义该层的协议，而由参与互联的各网络使用自己的物理层和数据链路层协议，然后与 TCP/IP 网络接入层进行连接。
				地址解析协议(ARP)工作在此层，即 OSI 参考模型的数据链路层。
				arp -a		查看缓存的 arp 协议 MAC 地址
			网际互联层
				网际互联层对应于 OSI 参考模型的网络层，主要解决主机到主机的通信问题。
				它所包含的协议涉及数据包在整个网络上的逻辑传输
				该层有三个主要协议：网际协议(IP)、互联网组管理协议(IGMP) 和互联网控制报文协议(ICMP)
			传输层
				传输层对应于 OSI 参考模型的传输层，为应用层实体提供端到端的通信能力，保证了数据包的顺序传送及数据的完整性。
				该层定义了两个主要的协议：传输控制协议(TCP) 和用户数据报协议(UDP)
					TCP/IP 三次握手
									主机A						主机B
										|							|
							 发送SYN信息|-------------------------->|接收SYN信息
							（序列号=x）|							|(序列号=x)
										|							|
										|							|
						接收SYN、ACK信息|<--------------------------|发送SYN、ACK信息
				   (序列号=y,确认号=x+1)|							|(序列号=y,确认号=x+1)
										|							|
										|							|
							 发送ACK信息|-------------------------->|接收ACK信息
							(确认号=y+1)|							|(确认号=y+1)
										|							|
					端口
						netstat -an		查看所有端口
						
						-----------------------------------------------------
							  |		|		|		|		|		|		|
						应用层|ftp	|telnet	|smtp	|dns	|tftp	|snmp	|
							  |		|		|		|		|		|		|
					------------21-----23------25------53------69------161-------	端口号
							  |							|					|
						传输层|			TCP				|			UDP		|
							  |							|					|
						-----------------------------------------------------
			应用层
				应用层对应于 OSI 参考模型的高层，为用户提供所需要的各种服务，例如：FTP、Telnet、DNS、SMTP 等
				
			数据封装过程(FTP文件传输)
				应用数据						字节流(数据)
				应用层							FTP 头 + 数据
				传输层							TCP 头 + FTP 头 + 数据
				网络层							IP 头 + TCP 头 + FTP 头 + 数据
				数据链路层						以太帧头 + IP 头 + TCP 头 + FTP 头 + 数据
												以太帧头 + 目的地址 + 源地址 + 包类型 + 包数据 + CRC
		TCP/IP 模型与 OSI 模型的比较
			共同点：
				OSI 参考模型和 TCP/IP 参考模型都采用了层次结构的概念
				都能够提供面向连接和无连接两种通信服务机制
			不同点：
				OSI 参考模型是七层；TCP/IP 模型是四层
				OSI 参考模型对可靠性要求更高
				OSI 模型是在协议开发前设计的，具有通用性；TCP/IP 是先有协议集然后建立模型，不适用于非 TCP/IP 网络
								
	网络层协议和IP地址划分
		IP 地址分类
			网络类别		IP 地址范围					子网掩码		最大网络数		最大主机数		私有 IP 地址范围
				A		1.0.0.0 ~ 126.255.255.255		255.0.0.0		126(2^7 - 2)	2^24 - 2		10.0.0.0 ~ 10.255.255.255
				B		128.0.0.0 ~ 191.255.255.255		255.255.0.0		16384(2^14)		2^16 - 2		127.16.0.0 ~ 172.31.255.255
				C		192.0.0.0 ~ 223.255.255.255		255.255.255.0	2097152(2^21)	2^8 - 2			192.168.0.0 ~ 192.168.255.255
Linux 网络基础
	Linux 的 IP 地址配置
		ifconfig	查看网络状态命令
		
		ifconfig eth0 192.168.0.200 netmask 255.255.255.0		临时设置 eth0 网卡的 IP 地址与子网掩码
		ifconfig eth0 down			使 eth0 网卡失效
		ifconfig eth0 up			使 eth0 网卡生效
	Linux 网络配置文件
		/etc/sysconfig/network-scripts/ifcfg-网卡名
			DEVICE=eth0				网卡设备名（需要和配置文件名匹配）
			BOOTPROTO=none			是否自动获取 IP（none、static(静态分配)、dhcp(自动获取)）
			HWADDR=00:0c:29:17:c4:09	MAC 地址
			NM_CONTROLLED=yes		是否可以由 Network Manager 图形管理工具托管
			ONBOOT=yes				是否随网络服务启动，eth0 生效
			TYPE=Ethernet			类型为以太网
			UUID="...."				唯一识别码
			IPADDR=192.168.0.252	IP 地址
			NETMASK=255.255.255.0	子网掩码
			GATEWAY=192.168.0.1		网关
			DNS1=201.106.0.20		DNS
			IPV6INIT=no				IPv6没有启用
			USERCTL=no				不允许非 root 用户控制此网卡
			
			centos 7 设置静态 IP
				BOOTPROTO="static"
				ONBOOT="yes"
				IPADDR="192.168.28.200"
				NETMASK="255.255.255.0"
				GATEWAY="192.168.28.2"
				DNS1="192.168.28.2"
		/etc/sysconfig/network	主机名文件
			NETWORKING=yes
			HOSTNAME=localhost.localdomain
			
			hostname 主机名			临时设置主机名
		/etc/resolv.conf		DNS 配置文件
			search localdomain
			nameserver 192.168.28.2
	常用网络命令
		ifdown 网卡设备名			禁用该网卡设备
		ifup 网卡设备名				启用该网卡设备
		netstat [选项]				查询网络状态
			-t		列出 TCP 协议端口
			-u		列出 UDP 协议端口
			-n		不使用域名与服务名，而使用 IP 地址和端口号
			-l		仅列出在监听状态的网络服务
			-a		列出所有的网络连接	
			-r		列出路由列表
				netstat -tuln
				netstat -an
				netstat -rn
		route -n			查看路由列表
		nslookup [主机名或 IP]		进行域名与 IP 地址解析
			nslookup		查看本机 DNS 服务器
		ping [选项] ip或域名		探测指定 IP 或域名的网络状况
			-c 次数			指定 ping 包的次数
		telnet [域名或IP] [端口]		远程管理与端口探测命令
			注意：telnet 协议是明文传输，不建议开启此服务
		traceroute [选项] IP或域名		路由跟踪命令
			-n		使用 IP，不使用域名，速度更快
		wget 地址			下载命令
			wget http://soft.vpser.net/lnmp/lnmp1.1-full.tar.gz
		
SSH远程管理服务
	SSH 简介
		SSH 是 Secure Shell 的缩写，SSH 为建立在应用层和传输层基础上的安全协议
		SSH 端口：22
		Linux 中守护进程：sshd
		安装服务：OpenSSH
		服务端配置文件：/etc/ssh/sshd_config
		客户端配置文件：/etc/ssh/ssh_config
	SSH 原理
		对称加密算法：
			采用单密钥码系统的加密方法，同一个密钥可以同时用作信息的加密和解密
		非对称加密算法：
			非对称加密算法需要两个密钥：公开密钥和私有密钥
	SSH 配置文件
		/etc/ssh/sshd_config
			Port 22				端口(建议修改)
			ListenAddress 0.0.0.0.0		监听的IP
			Protocol 2			SSH 版本选择
			HostKey /etc/ssh/ssh_host_rsa_key		私钥保存位置
			ServerKeyBits 1024		私钥的位数
			SyslogFacility AUTH		日志记录SSH登陆情况
			LogLevel INFO			日志等级
			GSSAPIAuthentication yes	GSSAPI认证开启
			
			PermitRootLogin yes							允许 root 的 ssh 登陆
			PubkeyAuthentication yes					是否使用公钥验证
			AuthorizedKeysFile .ssh/authorized_keys		公钥的保存位置
			PasswordAuthentication yes					允许使用密码验证登陆
			PermitEmptyPasswords no						不允许空密码登陆
	常用 SSH 命令
		配置主机之间的 ssh 免密登陆：
			ssh [user@]host			登陆远程服务器
			ssh-keygen				生成客户端密钥对(~/root/.ssh/id_rsa)
			ssh-copy-id	server_host	拷贝客户端的公钥并追加到服务端的授权列表(~/.ssh/authorized_keys)
				ssh-copy-id [-p port] [user@]hostname			只有家目录下的授权列表中有公钥的用户才能免密登录
		远程复制文件
			scp [-r] [[user1@]host1:]file1 [[user2@]host2:]file2
				file1		源文件，如果是远程则是下载，如果是本地则是上传
				file2		目标目录
				-r			表示传输目录
		sftp root@192.168.4.2		sftp 文件传输
			ls			查看远程服务器端数据
			cd			切换远程服务器端目录
			lls			查看本地数据
			lcd			切换本地目录
			get			从远程服务器下载
			put			上传到远程服务器
```