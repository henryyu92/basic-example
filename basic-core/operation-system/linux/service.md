## 服务管理
CentOS 7以上是用 Systemd 进行系统初始化的，Systemd 是 Linux 系统中最新的初始化系统，主要目标是克服 sysvint 固有的缺点，提高系统的启动速度。

Systemd 服务文件以 ```.service``` 为后缀，如果是 yum install 命令安装的，则系统会自动创建对应的 .service 文件；.service 文件存放在 /lib/systemd/system 下；
如果是源码包安装，则需要手动创建 .service 文件：
```
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
```
- Unit 表示服务的说明
  * Description 是服务描述
  * After 指定服务的启动时机
  * Conflicts 指定服务的冲突
- Service 服务运行参数设置
  * Type=forking 表示后台运行
  * ExecStart 指定服务运行的命令，一定要使用绝对路径
  * ExecReload 指定服务重启命令
  * ExecStop 指定服务停止命令
  * PrivateTmp=true	表示给服务分配独立的临时空间
- Install 运行级别相关设置
  * WantedBy=multi-user	表示在多用户时启动

创建完 .service 文件后，使用 systemctl enable xxx.service 命令使开机自启动生效

#### 服务管理命令	
- systemctl start xxx.service				启动服务
- systemctl stop xxx.service				停止服务
- systemctl enable xxx.service			设置开机启动服务
- systemctl disable xxx.service			停止开机启动服务
- systemctl status xxx.service			查看服务当前状态
- systemctl restart xxx.service			重启服务
- systemctl list-units --type=service		查看所有服务
- systemctl reload xxx.service			重新加载配置文件而不关闭服务
- systemctl is-enabled xxx.service		是否设置为开机重启

	

## 系统资源
### 进程管理
### 资源管理