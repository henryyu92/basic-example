### IP 地址配置
ifconfig	查看网络状态命令
		
		ifconfig eth0 192.168.0.200 netmask 255.255.255.0		临时设置 eth0 网卡的 IP 地址与子网掩码
		ifconfig eth0 down			使 eth0 网卡失效
		ifconfig eth0 up			使 eth0 网卡生效
### Linux 网络配置文件
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
### 常用网络命令
- ifdown 网卡设备名			禁用该网卡设备
- ifup 网卡设备名				启用该网卡设备
- netstat [选项]				查询网络状态
  * -t		列出 TCP 协议端口
  * -u		列出 UDP 协议端口
  * -n		不使用域名与服务名，而使用 IP 地址和端口号
  * -l		仅列出在监听状态的网络服务
  * -a		列出所有的网络连接	
  * -r		列出路由列表
```shell				
netstat -tuln
netstat -an
netstat -rn
```
- route -n			查看路由列表
- nslookup [主机名或 IP]		进行域名与 IP 地址解析
			nslookup		查看本机 DNS 服务器
		ping [选项] ip或域名		探测指定 IP 或域名的网络状况
			-c 次数			指定 ping 包的次数
		telnet [域名或IP] [端口]		远程管理与端口探测命令
			注意：telnet 协议是明文传输，不建议开启此服务
		traceroute [选项] IP或域名		路由跟踪命令
			-n		使用 IP，不使用域名，速度更快
		wget 地址			下载命令
			wget http://soft.vpser.net/lnmp/lnmp1.1-full.tar.gz
		
### SSH远程管理服务
#### SSH 简介
- SSH 是 Secure Shell 的缩写，SSH 为建立在应用层和传输层基础上的安全协议
- SSH 端口：22
- Linux 中守护进程：sshd
- 安装服务：OpenSSH
- 服务端配置文件：/etc/ssh/sshd_config
- 客户端配置文件：/etc/ssh/ssh_config
#### SSH 原理
- 对称加密算法：采用单密钥码系统的加密方法，同一个密钥可以同时用作信息的加密和解密
- 非对称加密算法：非对称加密算法需要两个密钥：公开密钥和私有密钥
#### SSH 配置文件
/etc/ssh/sshd_config
- Port 22				端口(建议修改)
- ListenAddress 0.0.0.0.0		监听的IP
- Protocol 2			SSH 版本选择
- HostKey /etc/ssh/ssh_host_rsa_key		私钥保存位置
- ServerKeyBits 1024		私钥的位数
- SyslogFacility AUTH		日志记录SSH登陆情况
- LogLevel INFO			日志等级
- GSSAPIAuthentication yes	GSSAPI认证开启
- PermitRootLogin yes							允许 root 的 ssh 登陆
- PubkeyAuthentication yes					是否使用公钥验证
- AuthorizedKeysFile .ssh/authorized_keys		公钥的保存位置
- PasswordAuthentication yes					允许使用密码验证登陆
- PermitEmptyPasswords no						不允许空密码登陆
#### 常用 SSH 命令
配置主机之间的 ssh 免密登陆：
- ssh [user@]host			登陆远程服务器
- ssh-keygen				生成客户端密钥对(~/root/.ssh/id_rsa)
- ssh-copy-id	server_host	拷贝客户端的公钥并追加到服务端的授权列表(~/.ssh/authorized_keys)
- ssh-copy-id [-p port] [user@]hostname			只有家目录下的授权列表中有公钥的用户才能免密登录

远程复制文件
- scp [-r] [[user1@]host1:]file1 [[user2@]host2:]file2
  * file1		源文件，如果是远程则是下载，如果是本地则是上传
  * file2		目标目录
  * -r			表示传输目录
- sftp root@192.168.4.2		sftp 文件传输
  * ls			查看远程服务器端数据
  * cd			切换远程服务器端目录
  * lls			查看本地数据
  * lcd			切换本地目录
  * get			从远程服务器下载
  * put			上传到远程服务器

### 防火墙设置
Linux 防火墙是作为一个服务被管理的
		
- 启动防火墙服务：systemctl start firewalld
- 查看防火墙服务状态：systemctl status firewalld
- 停止防火墙服务：systemctl disable firewalld
- 禁用防火墙：systemctl stop firewalld
	
也可以用 .service 管理服务：
- 启动一个服务：systemctl start firewalld.service
- 关闭一个服务：systemctl stop firewalld.service
- 重启一个服务：systemctl restart firewalld.service
- 显示一个服务的状态：systemctl status firewalld.service
- 在开机时启用一个服务：systemctl enable firewalld.service
- 在开机时禁用一个服务：systemctl disable firewalld.service
- 查看服务是否开机启动：systemctl is-enabled firewalld.service
- 查看已启动的服务列表：systemctl list-unit-files|grep enabled
- 查看启动失败的服务列表：systemctl --failed

防火墙的一些常用配置：
- 查看版本： firewall-cmd --version
- 查看帮助： firewall-cmd --help
- 显示状态： firewall-cmd --state
- 查看所有打开的端口： firewall-cmd --zone=public --list-ports
- 更新防火墙规则： firewall-cmd --reload
- 查看区域信息:  firewall-cmd --get-active-zones
- 查看指定接口所属区域： firewall-cmd --get-zone-of-interface=eth0
- 拒绝所有包：firewall-cmd --panic-on
- 取消拒绝状态： firewall-cmd --panic-off
- 查看是否拒绝： firewall-cmd --query-panic
- 永久开放端口： firewall-cmd --zone=public --add-port=8080/tcp --permanent
  * --zone		作用域
  * --add-port=8080/tcp		添加端口 端口/协议
  * --permanent	永久生效
- 查看端口：firewall-cmd --zone= public --query-port=80/tcp
- 删除端口：firewall-cmd --zone= public --remove-port=80/tcp --permanent