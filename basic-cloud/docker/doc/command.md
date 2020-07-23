## Docker

Docker 是基于 Linux 内核的 cgroup 和 namespace 技术开发容器平台，Docker 将容器中的进程与宿主的进程隔离开来，使得容器可以像宿主机一样工作。

### Image

镜像(Image)是一个特殊的文件系统，除了提供容器运行时所需的程序、库、资源、配置等文件外，还包含了一些为运行时准备的一些配置参数（如匿名卷、环境变量、用户等）。 镜像不包含任何动态数据，其内容在构建之后也不会被改变。

镜像构建时，会一层层构建，前一层是后一层的基础。每一层构建完就不会再发生改变，后一层上的任何改变只发生在自己这一层。比如，删除前一层文件的操作，实际不是真的删除前一层的文件，而是仅在当前层标记为该文件已删除。在最终容器运行的时候，虽然不会看到这个文件，但是实际上该文件会一直跟随镜像。因此，在构建镜像的时候，需要额外小心，每一层尽量只包含该层需要添加的东西，任何额外的东西应该在该层构建结束前清理掉。

分层存储的特征还使得镜像的复用、定制变的更为容易。甚至可以用之前构建好的镜像作为基础层，然后进一步添加新的层，以定制自己所需的内容，构建新的镜像。

#### 镜像命令

`docker images` 命令用于列出本地镜像仓库的的镜像列表，包括镜像的基本信息：
- repository：表示镜像来自的仓库
- tag：表示镜像的标签信息，用于标记来自同一个仓库的不同镜像
- image id：表示镜像的唯一标识，如果相同则表示指向同一个镜像
- created：表示镜像最后的更新时间
- size：表示镜像的大小
```shell script
docker images


REPOSITORY             TAG                 IMAGE ID            CREATED             SIZE
hello-world            latest              bf756fb1ae65        6 months ago        13.3kB
```

```docker inspect NAME|ID [NAME|ID...]``` 命令可以获取镜像的详细信息并以 json 的格式返回，镜像可以用名字或者 ID 来表示，可以获取多个镜像的详细信息。
```shell script
docker inspect hello-world

[
  {
    "ID":"sha256:image_id",
    ...
  }
]
```
`docker pull NAME[:TAG]` 可以从远程仓库拉取镜像到本地仓库，如果没有指定标签则会拉去 TAG 为 latest 的镜像。
```shell script
docker pull hello-world && docker images | grep hello-world


hello-world            latest              bf756fb1ae65        6 months ago        13.3kB
```
可以使用 `docker tag SOURCE_IAMGE[:TAG] TARGET_IMAGE[:TAG]` 为镜像创建新的 TAG，新创建的镜像和源镜像是同一份文件，只是 TAG 不同而已。
```shell script
docker tag hello-world hello-world:0.0.1 && docker images | grep hello-world


hello-world            0.0.1               bf756fb1ae65        6 months ago        13.3kB
hello-world            latest              bf756fb1ae65        6 months ago        13.3kB
```
当需要删除镜像时可以使用 `docker rmi IMAGE [IMAGE...]` 命令，如果同一个镜像有多个标签则只会删除对应的标签而不会真正的删除镜像文件，如果镜像已经创建了容器则必须先删除容器后才能删除镜像，使用 -f 选项可以强制删除镜像。
```shell script
docker rmi hello-world:latest && docker images | grep hello-world


hello-world            0.0.1               bf756fb1ae65        6 months ago        13.3kB
```
对于没有使用的镜像可以使用 `docker image prune` 命令清除，使用 -f 选项可以强制删除镜像。
```shell script
docker image prune -f && docker iamges


REPOSITORY             TAG                 IMAGE ID            CREATED             SIZE
```
`docker save IMAGE [IMAGE...]`可以从本地的镜像仓库将镜像导出到宿主机，使用 -o 选项可以指定以 tar 包的形式导出到指定的目录。
```shell script
docker save -o ~/hello-world.jar hello-world | ls -ha ~/ | grep hello-world


hello-world.jar
```
镜像也可以通过 `docker load` 命令从本地加载到仓库中，使用 -i 选项可以将指定的 tar 包加载到本地仓库中。
```shell script
docker load -i ~/hello-world.jar && docker images | grep hello-world


hello-world            latest              bf756fb1ae65        6 months ago        13.3kB
```
本地仓库的镜像可以使用 `docker push NAME[:TAG]` 命令推送到远程仓库。
```shell script
docker push hello-world:0.0.1
```

### Container

容器(Container)是镜像的一个运行时实例，容器的实质是进程，但与直接在宿主执行的进程不同，容器进程运行于属于自己的独立的命名空间。

和镜像不同的是，容器在运行时有一个可写的文件层，容器存储层的生存周期和容器一样，容器消亡时，容器存储层也随之消亡，因此任何保存于容器存储层的信息都会随容器删除而丢失。

容器不应该向其存储层内写入任何数据 ，容器存储层要保持无状态化。所有的文件写入操作，都应该使用数据卷（Volume）、或者绑定宿主目录，在这些位置的读写会跳过容器存储层，直接对宿主（或网络存储）发生读写，其性能和稳定性更高。数据卷的生存周期独立于容器，容器消亡，数据卷不会消亡。因此， 使用数据卷后，容器可以随意删除、重新run，数据却不会丢失。

#### 容器命令

`docker ps` 命令列出所有正在运行的容器，使用 -a 选项可以查看全部的容器(包括没有运行的)。容器的信息包括：
- CONTAINER ID：容器的 ID
- IMAGE：创建容器的镜像的唯一标识，可以是 NAME:TAG 也可以是 IMAGE ID
- COMMAND：容器启动时的命令
- CREATED：容器创建时间
- STATUS：容器当前状态
- PORTS：容器占用的端口，包括和宿主机的映射关系
- NAME：容器的名称，如果在创建容器时没有指定则会随机生成一个
```shell script
docker ps -a | grep hello-world


CONTAINER ID     IMAGE                  COMMAND       CREATED             STATUS                      PORTS         NAMES
1612e054a847     hello-world:latest     "/hello"      17 seconds ago      Exited (0) 16 seconds ago                 exciting_roentgen
```
可以通过 `docker inspect NAME|ID [NAME|ID]` 来查看容器的详细信息，NAME|ID 表示容器的名称或者容器的 ID，返回的结果以 json 的格式展示：
```shell script
docker inspect 1612e054a847


[
  {
    "Id": "container_id",
    ...
  }
]
``` 

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
- ```docker top CONTAINER```：查看容器内运行的进程
- ```docker stats [CONTAINER...]```：实时展示容器内的资源使用统计
- ```docker cp```：在容器和主机之间复制文件，```docker cp CONTAINER:SRC_PATH DEST_PATH|-``` 表示将容器内的文件复制到主机；```docker cp SRC_PATH|- CONTAINER:DEST_PATH``` 表示从主机复制文件到容器内
- ```docker port CONTAINER```：查看容器的端口映射情况
- ```docker update [OPTIONS] CONTAINER [CONTAINER...]```：更新容器的一些运行时配置

## 容器仓库
仓库是集中存放镜像的地方，注册服务器是存放仓库的具体服务器，一个注册服务器上可以有很多仓库，每个仓库可以有多个镜像。
### 搭建私有仓库

## Docker 架构
Docker 采用 C/S 架构，包括客户端和服务器两大核心组件