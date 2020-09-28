## 文件

Linux 中所有资源以文件形式保存，包括各种硬件设备。Linux 系统将每个硬件都看成是一个称为设备文件的文件，这样就可以通过读写文件实现对硬件的访问。

### 文件管理



#### 文件目录

Linux 系统中文件是以目录树结构存储，

Linux 各文件目录的作用：

- `/boot/`：系统启动目录，保存系统启动相关的文件，如内核文件和启动引导程序(grub)文件等
- `/dev/`：设备文件保存位置
- `/usr`：系统软件资源目录
  - `/user/sbin/`：保存和系统环境设置相关的命令，只有超级用户可以使用这些命令进行系统环境设置，但是有些命令可以允许普通用户查看
  - /usr/bin/`：存放系统命令的目录，普通用户和超级用户都可以执行。这些命令和系统启动无关，在单用户模式下不能执行
  - `/usr/lib/`：系统调用的函数库保存位置
- `/etc/`：配置文件保存位置，系统内所有默认安装方式(rpm安装)的服务的配置文件全部都保存在这个目录当中，如用户账号和密码，服务的启动脚本，常用服务的配置文件等
- `/home/`：普通用户的家目录。建立每个用户时，每个用户要有一个默认登陆位置，这个位置就是这个用户的家目录，所有普通用户的家目录就是在 /home 下建立一个和用户名相同的目录。如用户user1的家目录就是 /home/user1
- `/lost+found/`：当系统意外崩溃或机器意外关机而产生一些文件碎片会存放在这里。当系统启动的过程中 fsck 工具会检查这里，并修复已经损坏的文件系统。这个目录只在每个分区中出现，例如 /lost+found 就是根分区的备份恢复目录，/boot/lost+found 就是 /boot 分区的备份恢复目录
- `/media/`：挂载目录。系统建议是用来挂载媒体设备，例如软盘和光盘
- `/mnt/`：挂载目录，早期 Linux 中只有这一个挂载目录，并没有细分。现在这个目录系统建议挂载额外设备，如U盘、移动硬盘和其他操作系统的分区		
- `/opt/`：第三方安装的软件保存位置。这个目录就是放置和安装其他软件的位置，手工安装的源码包软件都可以安装到这个目录当中。/user/local/目录也可以用来安装软件，也更推荐安装在该目录下(行业默认习惯)
- `/proc/`：虚拟文件系统，该目录中的数据并不保存到硬盘当中，而是保存到内存当中。主要保存系统的内存，进程，外部设备状态和网络状态等。如/proc/cpuinfo是保存CPU信息的，/proc/devices是保存设备驱动的列表的，/proc/filesystems是保存文件系统列表的，/proc/net/是保存网络协议信息的
- `/sys/`：虚拟文件系统。和/proc 目录相似，都是保存在内存当中的，主要是保存于内核相关信息的
- `/root/`：超级用户的家目录。普通用户家目录在 /home 下，超级用于家目录直接在 / 下
- `/srv/`：服务数据目录。一些系统服务启动之后，可以在这个目录中保存所需要的数据
- `/tmp/`：临时目录。系统存放临时文件的目录，该目录下所有用户都可以访问和写入。建议此目录中不能保存重要数据，最好每次开机都把该目录清空
- `/var/`：动态数据保存位置。主要保存缓存、日志以及软件运行所产生的文件

#### 文件属性

Linux 中的文件具有文件类型、文件权限、文件大小、文件创建时间等多种属性，通过命令 `ls -a` 可以查看文件或者目录的属性信息。

Linux 中文件具有多种类型，不同的类型以不同的符号展示：

| 符号 | 文件类型     |
| ---- | ------------ |
| -    | 普通文件     |
| d    | 目录文件     |
| l    | 链接文件     |
| b    | 块设备文件   |
| c    | 字符设备文件 |
| p    | 管道文件     |



#### 文件管理命令

##### `ls`

`ls` 命令位于 `/usr/bin` 目录下，用于列出指定文件(默认当前目录下的文件)的信息。语法格式为 `ls [option] [file]...`，`ls` 命令有一些常用的选项：

- `-a`：列出所有文件的信息，包括隐藏文件(文件名以 `.` 开始)
- `-l`：显示文件的详细信息
- `-s`：显示文件分配的大小，以 block 为单位
- `-h`：与 `-l` 和 `-s` 一起使用，以更可读的方式展示

```sh
# 列出根目录所有文件信息
ls -alh /

ls -dlh /data			
```

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
列出的文件信息包括文件的类型，文件的权限，文件大小、文件创建时间等信息。

```sh

```

##### `mkdir`

`mkdir` 命令位于 `/usr/bin` 目录下，用于创建新的目录，语法格式为 `mkdir [option] directory...`，使用 `-p` 选项可以递归的创建不存在的父目录。

```sh
# 如果目录存在则会报错
mkdir -p /data/es /data/hbase /data/flink
```

#### 文件权限管理

linux 中所有的文件和目录都有三种权限：

| 权限    | 权重 | 文件           | 目录                                         |
| ------- | ---- | -------------- | -------------------------------------------- |
| 读(r)   | 4    | 查看文件       | 列出目录信息                                 |
| 写(w)   | 2    | 修改文件内容   | 创建、修改、删除目录，在目录中创建、删除文件 |
| 执行(x) | 1    | 执行可执行文件 | 进入目录                                     |

文件的权限是和用户相关联，在 Linux 中每个文件有三个用户相关的属性：所有者(u)，所属组(g)，其他人(o)，每个属性拥有文件的不同权限

##### chmod

`chmod` 命令位于 `/usr/bin/`下，用于改变目录或者文件的权限，语法格式为 `chmod [option] MODE FILE`，使用 `-R` 选项可以递归的修改文件和目录的权限。

`MODE` 是权限的模式，`{ugoa}{+-=}{rwx}`

```sh
# 递归增加写权限
chmod -R +w /data/hbase
# 移除执行权限
chmod -x /data/hbase/testfile
# 递归增加所属组用户的写权限
chmod -R g+w /data/hbase
# 增加所属者执行权限，赋予所属组读写权限，移除其他人读权限
chmod u+x,g=rw-,o-r /data/hbase/testfile
# 递归赋予所有人读、写、执行权限
chmod -R 777 /date/hbase
```

##### chown

`chown` 命令位于 `/usr/bin` ，用于该文文件或者目录的所有者和所属组，语法为 `chown [option] [owner][:group] FILE...`，选项 `-R` 可以递归的修改。

```sh
# 递归修改目录的所有者
chown -R root /data/hbase
# 递归修改目录所属组
chown -R :root /data/hbase
# 递归修改目录的所有者和所属组
chown -R guest:root /data/hbase
```

##### chgrp

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
### 文件系统

Linux 默认的文件系统是 `ext4`，

### 磁盘管理



#### 磁盘分区

Linux 系统将磁盘逻辑化为分区，在 Linux 中包含三种分区：

- 主分区
- 扩展分区
- 逻辑分区

#### 磁盘挂载

##### mount

挂载命令
	mount
		命令所在路径：/bin/mount
		执行权限：所有用户
		命令语法：mount [-t 文件系统] 设备文件名 挂载点
		

		mount -t iso9660 /dev/sr0 /mnt/cdrom		挂载光盘
		umount /dev/sr0		卸载光盘
#### 磁盘监控

##### df

##### du

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
