## Dockerfile

Dockerfile 是一个文本格式的配置文件，可以使用 Dockerfile 快速创建自定义的镜像。

```shell script
echo '
# 基础镜像
FROM openjdk:8
# 维护信息
LABEL user=test
# 配置指令
WORKDIR /usr/src/javaapp/
ADD ./Hello.java .
# 操作指令
RUN javac Hello.java
CMD ["java", "Hello"]' > Dockerfile && docker build -t hello .
```
Dockerfile 首先使用 `FROM` 指令指明基础镜像，然后使用 `LABEL` 指令说明维护者信息，接下来就是镜像操作指令执行镜像构建的过程，最后会跟随执行指令表明容器启动时执行的命令。

### 配置指令

Dockerfile 的配置指令主要是用来配置镜像的属性，包括基础镜像、环境变量、工作空间、数据卷挂载等信息。

`ARG` 指令用于定义容器创建过程中使用的变量，在执行 `docker build` 时通过 `-build-arg` 来为变量赋值。`ARG` 指令的格式为：
```shell script
ARG <name>[=<default_value>]
```
Docker 内置了一些镜像在创建时可以直接使用的变量，包括 `HTTP_PROXY`, `HTTPS_PROXY`, `FTP_PROXY`, `NO_PROXY`。

`FROM` 指令指定该镜像创建时需要的基础镜像，任何 Dockerfile 中第一条指令必须为 FROM 指令，并且同一个 Dockerfile 中每个镜像只能有一个 FROM 指令。`FROM` 指令的格式为：
```shell script
FROM <image>:<tag>
```
`LABLE` 指令用于为镜像添加元数据标签信息，可以多次使用 `LABEL` 指令为镜像生成多个标签，`LABEL` 指令的语法为：
```shell script
LABLE <key>=<value> [key=value ...]
```
`EXPOSE` 指令声明镜像内进程监听的端口，该指令只是声明监听的端口，并不会完成端口的映射，端口映射需要在启动容器时使用 -p 选项指定。`EXPOSE` 指令的语法格式为：
```shell script
EXPOSE <port> [port/protocol ...]
```
`ENV` 指令指定容器中的环境变量，命令设置的环境变脸可以在运行时使用 `docker run --env` 进行设定，语法格式为：
```shell script
ENV <key>=<value> [key=value ...]
```
`VOLUME` 命令为运行的容器创建一个数据卷挂载点，在运行时使用可以 `-v` 选项重新指定，语法格式为：
```shell script
VOLUME ["/volume/data/path"]
```
`USER` 指令可以指定运行容器的用户，在运行 `RUN` 指令时也会以该用户来运行，语法格式为：
```shell script
USER <user_name>
```
`WORKDIR` 指令配置 `RUN`, `CMD`, `ENTRYPOINT` 指令的工作目录，语法格式为：
```shell script
WORKDIR /path/to/workdir
```

- ```ENTRYPOINT```：指定镜像的默认入口命令，入口命令会在启动容器时作为根命令执行，所有的传入值作为该命令的参数。每个 Dockerfile 中只能有一个 ENTRYPOINT ，当指定多个时只有最后一个生效
  - ```ENTRYPOINT ["executable", "param1", "param2"]```：exec 命令调用执行
  - ```ENTRYPOINT command param1 param2```：shell 中执行
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
