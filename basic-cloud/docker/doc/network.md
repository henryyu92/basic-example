## 网络





Docker 服务在启动时会首先在宿主机上自动创建一个 `docker0` 虚拟网桥，负责挂载其上的接口之间进行转发。同时 Docker 会随机分配本地未占用的私有网段中的一个地址给 `docker0` 接口，此后启动的容器会自动分配一个该网段的地址。

当创建一个 Docker 容器的时候，同时也会创建一对 `veth pair` 互联接口，当向任意一个接口发送数据包时，另一个接口就会自动接收到数据包。互联接口的一端位于容器内(`eth0`)，另一端挂载在 `docker0` 网桥上(`vethXX`)，通过这种方式主机就可以和容器通信，容器之间也可以通信，容器与宿主机之间组成了一个虚拟的网络：
```
    +-----------+     +-----------+
    | Container |     | Container |
    +----eth0---+     +----eth0---+
          |                 |
  +----vethXX------------vethXX------+
  |              bridge              |
  +-------------docker0--------------+
```
Docker 服务启动后会默认启用一个内嵌的 `DNS` 服务来自动解析同一个网络中的容器主机名和地址，如果无法解析则通过容器内的 DNS 相关配置进行解析。

容器中主机名和 DNS 配置信息可以通过三个系统配置文件来管理：`/etc/resolv.cnf`, `/etc/hostname` 和 `/etc/hosts`，容器启动时会从宿主机上复制 `/etc/resolv.conf` 文件并删除掉其中无法连接到的 DNS 服务器。

在容器启动时可以使用参数指定网络配置信息：
- `-h hostname`：设定容器的主机名，容器的主机名会写入到容器内的 `/etc/hostname` 和 `/etc/hosts` 文件中
- `--link=container_name:alias`：记录其他容器的主机名，在创建容器的时候添加一个所连接容器的主机名到容器内 `/etc/hosts` 文件中，这样就可以直接使用主机名进行通信
- `--dns=ip_address`：指定 DNS 服务器并添加 DNS 服务器到 `/etc/resolv.conf` 中，容器会用指定的服务器来解析所有不在 `/etc/hosts` 中的主机名
- `--dns-option list`：指定 DNS 相关选项
- `--dns-search=DOMAIN`：指定 DNS 搜索域

### `DNS`

### 访问控制

