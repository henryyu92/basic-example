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
docker save -o ~/hello-world.jar hello-world && ls -ha ~/ | grep hello-world


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
容器是由镜像创建的，使用命令 `docker create IMAGE [COMMAND] [ARG...]` 可以由镜像创建容器。`docker create` 命令有很多可选项：
- `-e env_list` 设置容器的环境变量
- `-h hostname` 设置容器的主机名
- `-p port` 设置容器暴露给主机的端口
- `-v volumn` 设置容器挂载的数据卷
- `--name container_name` 设置容器的名称，如果没有设置则会默认生成一个

`COMMAND` 是容器启动时执行的命令，`ARG` 是执行命令是需要传入的参数。
```shell script
docker create  \
--name docker-hello \
-p 8100:8100 -p 127.0.0.1:9100:9100/tcp \
-e JAVA_HOME='/var/lib/jdk' \
-h docker-hello \
-v /local/data:/container/data \
hello-world:latest \
'/hello' \
&& docker ps -a | grep hello
```
使用 `docker create` 命令创建的容器处于停止状态，使用 `docker start CONTAINER [CONTAINER...]` 命令可以启动处于停止状态的容器，选项 -i 可以开启容器的交互。
```shell script
docker start -i docker-hello
```
`docker run IMAGE [COMMAND] [ARG...]` 命令是 `docker create` 和 `docker start` 命令的结合。`docker run` 命令首先会检查本地仓库是否含有这个镜像，如果本地仓库没有这个镜像的话会从远程仓库拉取镜像，然后由该镜像启动容器。

`docker run` 命令也有很多可选参数，除了和 `docker create` 相同的可选参数外，还提供了一些新的选项：
- `-it` 分配一个伪终端用于和容器交互
- `-d` 使容器在后台运行
```shell script
docker run \
--name docker-run-hello \
-p 8100:8100 -p 127.0.0.1:9100:9100/tcp \
-e JAVA_HOME='/var/lib/jdk' \
-h docker-hello \
-v /local/data:/container/data \
hello-world:latest '/hello' \
&& docker ps -a | grep hello
```
容器启动后会等到容器中的应用进程执行完毕后才会退出，使用命令 `docker pause CONTAINER [CONTAINER...]` 可以时运行中的容器暂停：
```shell script
docker pause `docker ps | grep hello | awk '{print $1}'`
```
容器还可以通过 `docker unpause CONTAINER [CONTAINER...]` 来恢复暂停的容器中的所有进程：
```shell script
docker unpause $(docker ps -a | grep hello | awk '{print $1}')
```
使用命令 `docker stop CONTAINER [CONTAINER...]` 可以停止运行中的容器，选项 -t 可以设置等待指定时长(单位: ms)后杀掉进程：
```shell script
docker ps -a | grep hello | awk '{print $1}' | xargs docker stop -t 2000
```
`docker restart CONTAINER [CONTAINER...]` 命令可以重启容器，选项 -t 可以设置等待指定时长之后重启：
```shell script
docker ps -a | grep hello | awk '{print $1}' | xargs docker restart -t 2000
```
命令 `docker rm CONTAINER [CONTAINER...]` 可以删除处于停止或者退出(进程执行完毕)状态的容器，选项 -f 可以强制删除处于运行状态的容器，选项 -v 会移除容器关联的匿名数据卷
```shell script
docker rm -v docker-hello && docker ps -a | grep docker-hello
```
容器运行时可以通过命令 `docker logs CONTAINER` 来查看容器的日志，可选项 -f 可以动态的查看日志， -t 可以显示日志的时间戳， --tail 则可以设置显示尾部行数：
```shell script
docker ps | grep docker-hello | awk '{print $1}' | xargs docker logs -tf --tail 100
```
`docker port CONTAINER` 命令可以查看容器的端口映射
```shell script
docker ps -a | grep docker-hello | awk '{print $1}'  | xargs docker port


8100/tcp -> 0.0.0.0:8100
9100/tcp -> 127.0.0.1:9100
```
除了查看容器运行时日志外，还可以通过 `docker top CONTAINER` 命令来查看容器内进程的信息：
- uid
- pid
- ppid
- c
- stime
- tty
- time
- cmd
```shell script
docker top docker-hello


UID      PID      PPID      C      STIME      TTY      TIME      CMD
```
使用 `docker stats [CONTAINER...]` 可以查看容器内进程的资源使用情况，包括 CPU, 内存, 网络 IO, 磁盘 IO：
```shell script
docker stats | grep docker-hello

CONTAINER ID     NAME     CPU %     MEM USAGE / LIMIT     MEM %     NET I/O     BLOCK I/O     PIDS
```
使用 `docker kill CONTAINER [CONTAINER...]` 命令可以强行终止容器
```shell script
docker ps | grep docker-hello | awk '{print $1}'  | xargs docker kill
```
如果需要和容器进行交互则可以使用 `docker exec CONTAINER COMMAND [ARG...]` 命令在运行中的容器内直接执行命令，这个命令有多个可选项：
- `-d` 表示容器执行的命令在后台执行
- `-it` 表示分配一个交互式的伪终端
- `e k=v` 设置容器中的环境变量
```shell script
docker exec -it -e GO_ROOT=/usr/bin/go docker-hello '/hello'
``` 
`docker cp` 命令则可以使容器和宿主机之间复制文件
```shell script
# 容器向宿主机拷贝文件
docker cp docker-hello:/data/logs /data/logs

# 宿主机向容器拷贝文件
docker cp /data/logs  docker-hello:/data/logs
```
`docker update CONTAINER [CONTAINER...]` 可以更新容器的运行时配置，可选项包括 CPU 的配置以及内存的配置
```shell script
docker update --cpus 10 --memory 1024 docker-hello
```
容器也可以通过 `docker export CONTAINER` 命令以 tar 包的形式导出容器的文件系统，使用选项 -o 指定导出的文件目录：
```shell script
docker export -o ~/docker-hello.tar docker-hello
```

### Repository

仓库是集中存放镜像的地方，注册服务器是存放仓库的具体服务器，一个注册服务器上可以有很多仓库，每个仓库可以有多个镜像。在使用 `docker pull` 命令拉取镜像时可以指定镜像的注册服务器，格式为 `<image_registry_server>/<repository>:<tag>`。

Docker 提供了 registry 镜像用于创建私有仓库，可以使用如下命令来在本地创建仓库：
```shell script
# 仓库的容器存放在 /var/lib/registry 下，可以通过 -v 来绑定到本地目录
docker run --name registry -d -p 5000:5000 -v /opt/data/registry:/var/lib/registry registry:2
```
Registry 的配置文件默认为 `/etc/docker/registry/config.yml`，默认的仓库地址为 `/var/lib/registry`,registry 容器在创建时使用 -v 参数可以挂载到本地磁盘：
```shell script
docker run -d \
--name registry \
-p 5000:5000 \
-v /home/user/registry/config.yml:/etc/docker/registry/config.yml \
-v /home/user/registry/lib:/var/lib/registry \
registry:2
``` 