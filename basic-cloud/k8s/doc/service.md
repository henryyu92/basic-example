### Service

Service 是 Kubernetes 中的核心资源对象，是应用服务的抽象，定义了多个 Pod 对象组合而成的逻辑集合和访问这个集合的策略。

Service 通过 `LabelSelector` 将一组 Pod 定义成一个逻辑集合，内部实现服务负载均衡和回话保持机制，并通过运行在每个 Node 上的 `kube-proxy` 实现请求的转发。

Kubernetes 在创建 Service 的时候会在集群指定的虚拟 IP 范围内分配一个全局唯一的 Cluster IP，并且在 Service 的整个生命周期内不会发生改变。

```yaml
apiVersion: v1
kind: Service
metadata:
  name: svc_name
spec:
  # Service 代理的 Pod 选择器
  selector:
    label_key: label_value
  # Service 的
  type: ClusterIP
  # Service 暴露的端口
  ports:
  - port: svc_port

```



#### IP

Kubernetes 中存在三种 IP：Node IP, Pod IP, Cluster IP。

![Kubernetes IP]()

Node IP 是 Kubernetes 集群中每个节点的物理网卡的 IP 地址，是一个真实存在的物理网络，所有属于这个网络的服务器都可以通过这个网络通信，而不管服务器是否属于这个 Kubernetes 集群，集群外的服务器访问当前节点时必须通过 Node IP 通信。

Pod IP 是节点上运行的 Pod 的 IP 地址，是 Docker Engine 根据 docker0 网桥的 IP 地址段进行分配的，通常是一个虚拟的二层网络，Kubernetes 集群中 Pod 间的通信就是通过 Pod IP 所在的虚拟二层网络完成，而真实的 TCP/IP 流量是通过  Node IP 所在的物理网卡流出的。

Cluster IP 是一种虚拟的 IP，仅仅作用于 Service 对象，并由 Kubernetes 管理和分配 IP 地址。Cluster IP 无法响应 Ping 请求，因为并没有一个 “实体网络对象” 来响应。Cluster IP 只能和 Service Port 组成一个具体的通信端口，并且只能被 Kubernetes 集群内的节点访问，集群外部无法直接使用。



#### 请求代理

Service 是一组协同工作的 Pod，用于保证 Pod 的动态变化对其他访问端 Pod 的透明，访问端 Pod 只需要知道 Service 的地址，由 Service 代理请求并路由到对应的服务提供 Pod。Service 通过 Label 选择器将流量负载均衡到匹配的 Pod 中。



Service 是一种抽象：通过规则定义出由多个 Pod 对象组合而成的逻辑集合，以及访问这组 Pod 的策略。Service 关联 Pod 对象的规则要借助于标签选择器来完成，由 Deployment 控制器管理的 Pod 对象中断后会由新建的对象取代，而扩容后的应用则会带来 Pod 对象的 IP 地址的变动，Service 对象基于标签选择器将一组 Pod 定义成一个逻辑组合，并通过自己的 IP 地址和端口代理请求至组内的 Pod 对象之上。

Service 对象的 IP 地址称为 ClusterIp，位于 Kubernetes 集群配置指定专用 IP 地址的范围之内并且是一种虚拟 IP 地址，它在 Service 对象创建后即保持不变，并且能够被同一集群中的 Pod 资源所访问。Service 端口用于接收客户端请求并将其转发至后端的 Pod 中的应用相应端口之上，因此这种代理机制称为四层代理，工作于 TCP/IP 协议栈的传输层。

通过标签选择器匹配到的后端 Pod 对象不止一个时，Service 对象能够以负载均衡的方式进行流量调度，实现了请求流量的分发机制。Service 对象会通过 apiserver 持续监视(watch)标签选择器匹配到的后端对象，并实时跟踪各对象的变动。Service 并不直接链接至 Pod 对象，它们之间还有一个中间层 Endpoints 对象，它是由一个 IP 地址和端口组成的列表，这些 IP 地址和端口则来自由 Service 的标签选择器匹配到的 Pod 资源，默认情况下创建 Service 对象的是偶会创建关联的 Endpoints 对象。

简单来讲，一个 Service 对象就是工作节点上的一些 iptables 或 ipvs 规则，用于将到达 Service 对象 IP 地址的流量调度转发至相应的 Endpoints 对象指向的 IP 地址和端口之上，工作与每个工作节点的 kube-proxy 组件通过 apiserver 持续监控着各 Service 及预期关联的 Pod 对象，并将其创建或变动实时反映至当前工作节点上相应的 iptables 或 ipvs 规则上。

kube-proxy 将请求代理至相应端点的方式有三种：

- userspace：此处的 userspace 是指 Linux 操作系统的用户空间。这种模型中，kube-proxy 负责跟踪 apiserver 上 Service 和 Endpoints 对象的变动(创建和移除)，并据此调整 Service 对象的定义。对于每个 Service 对象，会随机打开一个本地端口(运行于用户空间的kube-proxy 进程负责监听)，任何到达此代理端口的连接请求都将被代理至当前 Service 对象后端的各 Pod 对象上，然后采用调度算法挑选一个 Pod 运行，默认的调度算法是轮询(round-robin)。此类 Service 对象还会创建 iptables 规则以捕获任何到达 ClusterIP 和端口的流量。这种代理模式中，请求流量到达内核空间后经由套接字送往用户空间的 kube-proxy，然后再由它送回内核空间并调度至后端 Pod，这种方式请求在内核空间和用户空间来回转发必然会导致效率不高
- iptables：iptables 代理模型中，kube-proxy 负责跟踪 apiservice 上 Service 和 Endpoints 对象的变动(创建或移除)，并据此做出 Service 资源定义的变动。同时对于每个 Service 都会创建 iptables 规则直接捕获到达 clusterIp 和 port 的流量，并将其重定向至当前 Service 的后端。对于每个 Endpoints 对象，Service 对象会为其创建 iptables 规则并关联至挑选的后端 Pod 对象，默认算法是随机调度(random)
- ipvs：ipvs 代理模型中，kube-proxy 跟踪 apiserver 上 Service 和 Endpoints 对象的变动，据此来调用 netlink 接口创建 ipvs 规则，并确保与 apiserver 中的变动保持同步。与 iptables 不同之处在于其请求流量的调度功能由  ipvs 实现，其余功能仍由 iptables 完成。ipvs 构建与 netfilter 的钩子函数上，但使用 hash 表作为底层数据结构并工作与内核空间，因此具有流量转发速度快、支持众多调度算法

定义 Service 对象是，spec 的两个常用的内嵌字段是 selector 和 ports：

```yaml
apiVersion: v1
kind: Service
metadata: 
  name: myapp-svc
spec:
  selector:
    app: myapp
  # 默认为 ClusterIP
  type: ClusterIP
  port:
  - protocol: TCP
    port: 80
    targetPort: 80
```

使用```kubectl get svc <service_name>``` 可以获取 Service 对象的信息，使用 ```kubectl get endpoints <service_name>``` 可以获取 Service 对应的 Endpoint 对象。Service 对象创建完成后即可作为服务对外提供，但是真正响应请求的是后端的 Pod 对象。

```sh
# 查看 Service 信息
kubectl get svc <svc_name>

# 查看 Service 代理的端点
kubectl get endpoints <svc_name>
```



Service 对象还支持会话粘性(Session affinity)机制，它能够将来自同一客户端的请求始终转发至同一个后端的 Pod 对象。Session affinity 的效果会在一定时间期限内生效，默认值为 10800 秒，超出此时长之后客户端的再次访问会被调度算法重新调度。

Service 对象通过 ```spec.sessionAffinity``` 和 ```spec.sessionAffinityConfg``` 两个字段配置会话粘性，其中 sepc.sessionAffinity 字段用于定义要使用会话粘性的类型，支持：

- None：不使用 sessionAffinity，默认值
- ClientIP：基于客户端 IP 地址，把来自同一个 IP 地址的请求始终调度至I一个 Pod 对象

启动会话粘性机制时，spec.sessionAffinityConfig 用于配置会话保持时长，默认 10800 秒。会话粘性的 yaml 配置为：

```yaml
spec:
  sessionAffinity: ClientIP
  sessionAffinityConfig:
    ClientIP: 10800
```



#### 服务发现

服务发现机制的基本实现一般是事先部署好一个网络位置较为稳定的服务注册中心，服务提供者(服务端)向注册中心注册自己的位置信息并在变动后及时给予更新，服务消费者则周期性的从注册中心获取服务提供者的最新位置从而发现要访问的目标服务资源。

根据服务发现过程的实现方式，服务发现可以分为两种类型：

- 客户端发现：客户端到服务注册中心发现其依赖到的服务的相关信息，因此需要内置特定的服务发现逻辑
- 服务端发现：服务消费者将请求发往负载均衡器，由它们负责查询服务注册中心获取服务提供者的位置信息并将服务消费者的请求转发给服务提供者

Kubernetes 引入了 DNS 系统把服务名作为域名，使得程序可以直接使用服务名来建立通信连接。





Service 对象的 yaml 定义为：

```yaml
apiVersion: v1
kind: Service
metadata:
  # 指定 Service 的名称
  name: string
  # 命名空间默认为 default
  namespace: string
  labels:
    - name: string
  annatations:
    - name: string
spec:
  # 指定 LabelSelector
  selector: []
  # Service 类型，默认为 ClusterIP，还可以为 NodePort 和 LoadBalancer
  type: string
  # spec.type=ClusterIP 时指定虚拟 IP，如果不指定则会自动分配；当 type=LoadBalancer 时需要指定
  clusterIp: string
  sessionAffinity: string
  ports:
    - name: string
      protocol: string
      # 服务监听的端口
      port: int
      # 需要转发到后端 Pod 端口
      targetPort: int
      # 当 spec.type=NodePort 时指定映射到物理机的端口号
      nodePort: int
  # 当 spec.type=LoadBalancer 时需要设置外部负载均衡的地址
  status:
    loadBalancer:
      ingress:
        ip: string
        hostname: string

```

Service 的 spec.type 可以指定为 ClusterIP, NodePort, LodaBalancer 三个值，其中：

- ClusterIP 指定虚拟服务 IP 地址用于 Kubernetes 集群内部的 Pod 访问，通过 kube-proxy 实现
- NodePort 使用宿主机的端口，使能够访问各 Node 的外部客户端通过 Node 的 IP 和端口就能访问服务
- LoadBalancer 使用外部负载均衡器完成到服务的负载分发，需要在 spec.status.loadBalancer 指定外部负载均衡器的 IP 地址，并同时定义 nodePort 和 clusterIP 用于公有云环境

#### ClusterIP

#### NodePort

NodePort 实现方式是在 Kubernetes 集群中的每个 Node 上为需要外部访问的 Service 开启一个对应的 TCP 监听端口，外部系统只要用任意一个 Node 的 IP 地址和 NodePort 即可访问此服务。

```yaml
apiVersion: v1
kind: Service
metadata:
  name: svc_name
spec:
  selector:
    label_key: label_value
  # 通信模式为 NodePort
  type: NodePort
  prots:
  # Service 暴露的端口
  - port: svc_port
    # Node 为 Service 开放的外部访问端口
    nodePort: node_port
```

在任意一个 Node 上运行 `netstat` 命令就可以看到设置的 `nodePort` 端口被监听：

```sh
netstat -tlp | grep <nodePort>
```

NodePort 模式可以使得 Service 可以对外服务，但是需要外部客户端指定 Node 的 IP，因此一般在使用 Node Port 模式的时候会在 Kubernetes 集群外部署负载均衡器(Load balancer)，如 HAProxy 或者 Nginx，对每个需要对外暴露服务的 Service 配置负载均衡。

#### LoadBalancer


