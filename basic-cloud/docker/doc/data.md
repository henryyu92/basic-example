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