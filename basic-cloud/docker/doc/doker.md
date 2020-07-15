## Docker 核心概念
- 镜像：Docker 镜像类似于虚拟机镜像，可以将它理解为一个只读的模板。镜像是创建 Docker 容器的基础。
- 容器：Docker 容器类似于一个轻量级的沙箱，Docker 利用容器来运行和隔离应用。容器是从镜像创建的应用运行实例，可以启动、停止、删除，容器之间是彼此隔离、互不相见的。
- 仓库：Docker 仓库类似于代码仓库，是 Docker 集中存放镜像的地方。

## Docker 常用命令
### Docker 镜像
- ```docker images```：列出本地机器上已有的镜像的基本信息.
  - repository：表示镜像来自的仓库
  - tag：表示镜像的标签信息，用于标记来自同一个仓库的不同镜像
  - image id：表示镜像的唯一标识，如果相同则表示指向同一个镜像
  - created：表示镜像最后的更新时间
  - size：表示镜像的大小
- ```docker pull NAME[:TAG]```：从镜像仓库中拉取镜像，其中 NAME 是镜像的名字(严格讲需要添加镜像仓库地址作为前缀，默认使用官方 Docker Hub 服务忽略前缀)，TAG 是镜像的标签(用于表示镜像的版本信息，默认 latest)
- ```docker image inspect IMAGE```：获取镜像的详细信息，返回 JSON 格式的信息
- ```docker tag IMAGE NEW_IMAGE```：为本地镜像添加任意的新标签。添加标签后会多出一个镜像，该镜像的 ID 和之前的镜像 ID 相同意味着二者实际上指向了同一个镜像文件
- ```docker rmi [IMAGE...]```：删除镜像
  - -f：强制删除镜像，即使有容器依赖它
- ```docker image prune```：清理没有被使用的镜像或临时的镜像
- ```docker commit CONTAINER```：从已有容器创建镜像
- ```docker build [OPTIONS] PATH|URL|-```：编译 Dockerfile 文件创建新的镜像
- ```docker save [OPTIONS] IMAGE [IMAGE...]```：将镜像文件以 tar 包的形式导出到本地文件系统
  - -o：导出到指定文件中
- ```docker load [OPTIONS] IMAGE```：将指定的 tar 文件加载为镜像
  - -i：从指定文件中加载
- ```docker push NAME[:TAG]```：将本地镜像上传到远程镜像仓库，默认是 Docker Hub 仓库
### Docker 容器
- ```docker create [OPTIONS] IMAGE [COMMAND] [ARG...]```：创建一个容器，新建的容器处于停止状态
- ```docker start [OPTIONS] CONTAINER [CONTAINER...]```：启动容器
- ```docker run [OPTIONS] IMAGE [COMMAND] [ARG...]```：创建并运行容器，等价于先执行 docker create 再执行 docker start 命令。使用 docker run 来创建并启动容器时，Docker 在后台运行的标准操作包括：
  - 检查本地是否存在指定的镜像，不存在则从公有仓库下载
  - 利用镜像创建一个容器并启动该容器
  - 分配一个文件系统给容器并在只读的镜像层外面挂载一层可读写层
  - 从宿主主机配置的网桥接口中桥接一个虚拟接口到容器中去
  - 从网桥的地址池配置一个 IP 地址给容器
  - 执行用户指定的应用程序
  - 执行完毕后容器被自动终止
- ```docker logs [OPTIONS] CONTAINER```：查看容器的输出日志
  - -f
  - -tail
- ```docker attach [OPTIONS] CONTAINER```：将本地的标准input、output和 error 流 attach 到运行中的容器。当多个流 attach 到同一个容器时，所有的流是同步的，某一个阻塞了其他的都无法操作了
- ```docker exec [OPTIONS] CONTAINER COMMAND [ARG...]```：在运行中的容器内直接执行任意命令
  - -d：在容器后台执行命令
  - -e：指定环境变量
  - -t：分配伪终端
- ```docker pause CONTAINER [CONTAINER...]```：暂停运行中的容器
- ```docker unpause CONTAINER [CONTAINER...]```：恢复运行暂停的容器
- ```docker stop CONTAINER [CONTAINER...]```：终止运行中的容器
  - -t：
- ```docker kill CONTAINER```：直接发送 SIGKILL 信号来强行终止容器
- ```docker restart CONTAINER```：重启运行态的容器
- ```docker rm  CONTAINER [CONTAINER...]```：删除处于终止或退出状态的容器。默认情况下只能删除已经处于终止或退出状态的容器，使用 -f 参数可以删除处于运行状态的容器
- ```docker export CONTAINER```：以 tar 包的形式导出容器的文件系统
- ```docker import file|URL|- [REPOSITORY[:TAG]]```：将导出的 tar 文件导入创建镜像
- ```docker ps ```：列出运行的容器
- ```docker inspect CONTAINER [CONTAINER...]```：查看容器的详情
- ```docker top CONTAINER```：查看容器内运行的进程
- ```docker stats [CONTAINER...]```：实时展示容器内的资源使用统计
- ```docker cp```：在容器和主机之间复制文件，```docker cp CONTAINER:SRC_PATH DEST_PATH|-``` 表示将容器内的文件复制到主机；```docker cp SRC_PATH|- CONTAINER:DEST_PATH``` 表示从主机复制文件到容器内
- ```docker port CONTAINER```：查看容器的端口映射情况
- ```docker update [OPTIONS] CONTAINER [CONTAINER...]```：更新容器的一些运行时配置

## 容器仓库
仓库是集中存放镜像的地方，注册服务器是存放仓库的具体服务器，一个注册服务器上可以有很多仓库，每个仓库可以有多个镜像。
### 搭建私有仓库

## Docker 数据管理
容器中的数据管理主要有两种方式：
- 数据卷(Data Volumes)：容器内数据直接映射到本地主机环境
- 数据卷容器(Data Vlume Containers)：使用特定容器维护数据卷
### 数据卷
数据卷(Data Volumes)是一个可供容器使用的特殊目录，将主机操作系统目录直接映射进容器。数据卷可以提供很多有用的特性：
- 数据卷可以在容器之间共享和重用，容器间传递数据将变得高效与方便
- 对数据卷内数据的修改会立马生效，无论是容器内操作还是本地操作
- 对数据卷的更新不会影响镜像，解耦应用与数据
- 卷会一直存在，直到没有容器使用则可以安全地卸载
#### volume 命令
Docker 提供了 ```volume``` 子命令来管理数据卷，默认操作的本地目录为 ```/var/lib/docker/volumes```。
- create 创建一个数据卷
- inspect 查看详细信息
- ls 列出已有的数据卷
- prune 清理无用的数据卷
- rm 删除数据卷
```shell
# 在本地创建数据卷
docker volume create -d local test
```
#### 绑定数据卷
可以在创建容器时将主机本地的任意路径挂载到容器内作为数据卷，这种形式创建的数据卷称为绑定数据卷。使用 ```docker [container] run``` 命令的时候可以使用 -mount 选项使用数据卷。

--mount 选项支持三种类型的数据卷：
- ```volume```：普通数据卷，映射到主机 ```/var/lib/docker/volumes``` 路径下
- ```bind```：绑定数据卷，映射到主机指定路径下，相当于 -v 选项
- ```tmpfs```：临时数据卷，只存在于内存中

```shell
docker run -d -P --name web --mount type=bind, source=/webapp, destination=/opt/web

docker run -d -P --name web -v /webapp:/opt/web
```
### 数据卷容器
## Docker 网络
### 端口映射
Docker 提供连个功能满足服务访问：
- 允许映射容器内应用程序服务端口到本地宿主主机
- 互联机制实现多个容器间通过容器名来快捷访问
### 互联机制

## Dockerfile
Dockerfile 是一个文本格式的配置文件，可以使用 Dockerfile 快速创建自定义的镜像。

Dockerfile 是由一行行指令语句组成，并且支持以 # 开头的注释行。Dockerfile 中指令分为配置指令和操作指定。
### 配置指令
- ```ARG <name>[=<default_value>]```：定义创建镜像过程中使用的变量。Docker 内置了一些镜像创建变量：HTTP_PROXY, HTTPS_PROXY, FTP_PROXY, NO_PROXY
- ```FROM <image> [AS <name>]```：指定创建镜像的基础镜像，任何 Dockerfile 中第一条指令必须为 FROM 指令，并且同一个 Dockerfile 中每个镜像只能有一个 FROM 指令
- ```LABLE <key>=<value> [key=value ...]```：为生成的镜像添加元数据标签信息
- ```EXPOSE <port> [port/protocol ...]```：声明镜像内服务监听的端口，该声明不会自动完成端口映射
- ```ENV <key>=<value>```：指定环境变量，在镜像生成过程中和容器中可用
- ```ENTRYPOINT```：指定镜像的默认入口命令，入口命令会在启动容器时作为根命令执行，所有的传入值作为该命令的参数。每个 Dockerfile 中只能有一个 ENTRYPOINT ，当指定多个时只有最后一个生效
  - ```ENTRYPOINT ["executable", "param1", "param2"]```：exec 命令调用执行
  - ```ENTRYPOINT command param1 param2```：shell 中执行
- ```VOLUME ["/valume/data/path"]```：创建一个数据卷挂载点
- ```USER <user_name>```：指定运行容器时的用户名或 UID
- ```WORKDIR /path/to/workdir```：为 RUN, CMD, ENTRYPOINT 指令配置工作目录
- ```ONBUILD [INSTRUCTION]```：指定当基于所生成镜像创建子镜像时，自动执行的操作命令
- ```STOPSIGNAL signal```：指定所创建镜像启动的容器接收退出的信号值
- ```HEALTHCHECK```：配置所启动容器如何进行健康检查
  - ```HEALTHCHECK [OPTIONS] CMD command``` 根据所执行命令返回值是否为 0 来判断
    - -interval=30： 指定检查间隔，默认 30s
    - -timeout=30：检查等待结果的超时，默认 30s
    - -retries=3：设置失败重试的次数，默认 3
  - ```HEALTHCHECK NONE```：禁止基础镜像中的健康检查
  - ```SHELL ["executable", "parameters"]```：指定其他命令使用 shell 时的默认 shell 类型，默认为 ["/bin/sh", "-c"]
### 操作指令
- ```RUN```：运行指定的命令，每条 RUN 指令将在当前镜像基础上执行指定命令并提交为新的镜像层
  - ```RUN <command>```：默认在 shell 终端中运行命令
  - ```RUN ["executable", "param1", "param2"]```：使用 exec 执行命令，不会启动 shell 环境
- ```CMD```：指定容器启动时默认执行的命令，每个 Dockerfile 只能有一条 CMD 命令，如果指定了多条命令只有最后一条会被执行
  - ```CMD ["executable", "param1", "param2"]```：相当于直接执行命令
  - ```CMD command param1 param2```：在默认的 Shell 中执行，提供给需要交互的应用
  - ```CMD ["param1", "param2"]```：提供给 ENTRYPOINT 的默认参数
- ```ADD <src> <dest>```：将 src 路径下的内容复制到容器 dest 路径下。src 可以是 Dockerfile 所在目录的一个相对路径，也可以是一个 URL，还可以是一个 tar 文件；dest 可以是镜像内绝对路径，也可以是相对于工作目录(WORKDIR)的相对路径
- ```COPY <src> <dest>```：将主机本地的 src 路径下的内容复制到镜像中 dest 的目录，和 ADD 指令功能类似

### Dockerfile 创建镜像
使用 ```docker build [OPTIONS] PATH|URL|-``` 命令读取指定路径下的 Dockerfile 并将该路径下所有数据作为上下文发送给 Docker 服务端，Docker 服务端在校验 Dockerfile 格式后会逐条执行其中定义的指令，ADD, COPY, RUN 指令会生成一层新的镜像，镜像创建成功后会返回最终镜像 ID。

通过 .dockerignore 文件可以让 Docker 忽略匹配路径或文件，在创建镜像时不将无关数据发送到服务端。

Docker 创建最佳实践：
- 精简镜像用途：尽量让每个镜像的用途都比较集中单一，避免构造大而复杂的镜像
- 选用合适的基础镜像：选择只包含所需要功能的镜像作为基础镜像，避免使用功能臃肿的镜像作为基础镜像
- 提供注释和维护者信息
- 明确使用版本号
- 减少镜像的层数：尽可能合并 RUN, ADD, COPY 指令

## Docker 架构
Docker 采用 C/S 架构，包括客户端和服务器两大核心组件