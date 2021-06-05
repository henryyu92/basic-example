## 用户管理

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
		​	修改最大有效权限
		​		setfacl -m m:rx 文件名		设定 mask 权限为 r-x 使用 m:权限 格式
		​	删除 ACL 权限
		​		setfacl -x u:用户名 文件名	删除指定用户的 ACL 权限
		​		setfacl -x g:组名 文件名	删除指定用户组的 ACL 权限
		​		setfacl -b 文件名			删除文件的所有的 ACL 权限
		默认 ACL 权限和递归 ACL 权限
		​	递归 ACL 权限
		​		递归是父目录在设定 ACL 权限时，所有的子文件和目录也会拥有相同的 ACL 权限
		​		setfacl -m u:用户名:权限 -R 目录名
		​		

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