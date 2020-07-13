 00866865 
## Kubernetes 架构
Kubernetes是一个可移植的、可扩展的、开放源码的平台，用于促进声明性配置和自动化管理容器化的工作负载和服务。

![架构图](./img/k8s.JPG)

Kubernetes 集群主要由两部分组成：Master 节点和 Worker 节点。其中 Master 节点包含以下组件：
- ```kube-apiserver```：暴露 Kubernetes API，用于创建对象
- ```kube-scheduler```：根据集群内各个 Node 的可用资源以及要运行的容器的资源需求做出调度决策
- ```kube-controller-manager```：运行管理集群的 controller 线程，包含：
  - ```Node Controller```：负责监听和报告节点的状态
  - ```Replication Controller```：负责维护集群中的 pod 数量和需求的数量一致
  - ```Endopoints Controller```：操作 Endpoints 对象，也就是使 Service 和 Pod 链接起来
  - ```Service Account & Token Controller```：为新 namespace 创建账户和 token

Worker 节点包含以下组件：
- ```kubelet```：与 API Server 交互以更新状态并启动调度程序调用的新工作负载，管理 Pod 及其中的容器、镜像、卷等
- ```kube-proxy```：在主机上维护网络规则并执行连接转发来实现 Kubernetes 服务抽象
### Kubernetes 核心概念
#### Pod
Pod 是 Kubernetes 的基本调度单元，一个 Pod 包含一个或多个容器，这些容器在同一个宿主机上并且共享相同的网络命名空间、IP 地址和端口以及一块存储卷空间。Kubernetes 中的每个 Pod 都被分配一个唯一的 IP 地址。
#### Service
Service 是服务应用的抽象，定义了 Pod 的逻辑集合和访问这个集合的策略。Service 是一组协同工作的 Pod，用于保证 Pod 的动态变化对其他访问端 Pod 的透明，访问端 Pod 只需要知道 Service 的地址，由 Service 代理请求并路由到对应的服务提供 Pod。Service 通过 Label 选择器将流量负载均衡到匹配的 Pod 中。
#### Label
Label 是一个键-值对，用于附加在 Kubernetes 的对象上进行标识。标签可以在资源对象创建时添加也可以在对象创建后添加。附加了 Label 的 API 对象可以由 LabelSelector 进行分组和查询。每个对象可以有多个 Label，并且一个 Label 可以添加到多个对象上。

标签在资源创建的时候可以在 metadata 中使用 labels 字段定义，对象创建后使用 kubectl get 命令并且使用 --show-labels 选项额外显示对象的标签信息

kubectl label 命令可以在对象创建之后添加到对象之上```kubectl label <pod_name> key=value```。

注解(annotation)和标签相似，也是键值类型的数据，但是不能用于标签选择器，仅可用于为资源提供元数据。
#### NameSpace
命名空间是 Kubernetes 集群级别的资源，用于将集群分隔为多个隔离的逻辑分区以配置给不同的用户、租户。Kubernetes 命名空间的隔离只是资源名称上的隔离，而并非操作系统的命名空间隔离也不是物理上的隔离。
### Kubernetes 安装
#### minikube 安装
```shell
```
#### kubeadm 安装
kubeadm 是 kubernetes 项目自带的集群构建工具，负责执行构建一个最小化的可用集群以及将其启动等的必要基本步骤。kubeadm 包含多个组件：
- kubeadm init：部署 Master 节点的各个组件
- kubeadm join：将节点加入到指定集群中
#### Rancher 安装
安装 rancher
```shell
# 通过 docker 安装 rancher
docker run -d --restart=unless-stopped -p 80:80 -p 443:443 rancher/rancher:stable
```
通过 https://ip:443 访问 rancher，默认用户名和密码都是 admin
### Kubernetes 基本操作
Kubernetes 使用 API 对象来描述集群的所需状态，通过 Kubernetes API(通常通过命令行界面 kubectl) 创建对象来设置所需的状态，一旦设置了所需的状态，Kubernetes 就会通过 Pod 生命周期事件生成器使集群的当前状态与所需的状态匹配。

Kubernetes 提供了命令行工具 kubectl 用于和 api-server 交互并管理资源。kubectl 提供了多种子命令用于资源的增删改查，其语法为：
```shell
kubectl <COMMAND> <TYPE> [NAME] [flags]
```
- ```COMMAND```：对资源执行的子命令
  - ```get```
  - ```create```：创建资源
  - ```logs```：打印 Pod 内容器的日志
- ```TYPE```：操作资源对象的类型
  - ```pods```
  - ```services```
  - ```namespace```
- ```NAME```：资源名称，省略时表示指定 TYPE 的所有资源对象
- ```flags```：资源操作的额外选项

- ```kubectl applay```：创建资源对象
- ```kubectl get```：查看资源对象
- ```kubectl describe```: 描述资源对象
- ```kubectl exec```：在容器中执行命令
- ```kubectl delete```：删除对象 
## Kubernetes 核心对象
Kubernetes 对象时 Kubernetes 系统中的持久实体，Kubernetes 使用这些实体来表示集群的状态。Kubernetes 对象一旦创建就能确保存在，使用 Kubernetes 对象必须使用 kubernetes API。

Kubernetes 对象由 yaml 格式的文件定义，该文件包含四个部分：
- apiVersion
- kind
- metedata
- spec
### Pod
Pod 是 Kubernetes 的最基本操作单元，包含一个或多个容器。Pod 使用 yaml 格式的文件定义：
```yaml
# Kubernetes api 版本
apiVersion: v1
# Pod 类型
kind: Pod
# Pod 元数据
metedata:
  # Pod 名称
  name: string
  # Pod 命名空间
  namespace: string
  # Pod 标签
  labels:
    # k-v 键值对
    - key: value
  annotations:
    - key: value
# Pod 详细定义
spec:
  # Pod 重启策略
  restartPolicy: [Always | Never | OnFailure]
  # host 网络设置
  hostNetwork: false
  # Pod 中容器定义
  containers:
    # 容器名
    - name: string
      # 镜像名
      image: string
      # 镜像拉取策略
      imagePullPolicy: [Always | IfNotPresent | Never]
      # 容器启动命令
      command: [string]
      # 容器启动命令参数
      args: [string]
      # 容器工作目录
      workingDir: string
      # 容器内的存储卷
      volumeMounts:
        - name: string
          mountPath: string
          readOnly: boolean
      # 容器暴露的端口映射规则
      ports:
        - name: string
          containerPort: int
          hostIp: string
          hostPort: int
          protocol: string
      # 容器的环境变量
      env:
        - name: string
          value: string
      # 容器资源
      resources:
        limits:
          cpu: string
          memory: string
        requests:
          cpu: string
          memory: string
      # 容器存活探测
      livenessProbe:
        # exec 方式执行脚本
        exec:
          command: [string]
        # httpGet 方式发送 HTTP 请求
        httpGet:
          path: string
          port: number
          host: string
          scheme: string
          HttpHeaders:
            - name: string
              value: string
        # tcp 方式发送心跳请求
        tcpSocket:
          port: number
        # 容器启动后首次探测推迟时间
        initialDelaySeconds: 0
        # 健康探测等待响应超时时间
        timeoutSeconds: 0
        # 健康探测周期
        periodSeconds: 0
        successThreshold: 0
        failureThreshold: 0
        securityContext:
          privileged: false
  nodeSelector: object
  imagePullSecrets:
    - name: string
  # 定义 Pod 的存储卷
  volumes:
    - name: string
      emptyDir: {}
      hostPath: string
      path: string
      secret:
        secretName: string
        items:
          - key: string
            path: string
      configMap:
        name: string
        items:
          - key: string
            path: string
```
#### Pod 的生命周期
Pod 对象从其创建开始至其终止退出的时间范围称为其生命周期。在 Pod 的生命周期内，Pod 会处于多种状态并执行相应操作。

- Pending：api-server 创建了 Pod 资源对象并已经存入 etcd，但尚未调度完成
- Running：Pod 已经被调度至某节点，并且所有容器都已经被 kubelet 创建完成
- Succeeded：Pod 中的所有容器都已经成功终止并且不会被重启
- Failed：所有容器都已经终止，但至少一个容器终止失败，即容器返回了非 0 值得退出状态或已经被系统终止
- Unknown：api-server 无法正常获取到 Pod 对象的状态信息，通常是由于其无法与所在工作节点的 kubelet 通信所致

Pod 在其生命周期中会执行多种操作：
- 初始化容器：应用程序的主容器启动之前要运行的容器，在 yaml 文件的 spec.initContainers 以列表的形式定义可用的初始化容器。初始化容器常用于为主容器执行一些预置操作，具有两种典型特征：
  - 初始化容器必须运行完成直至结束，若某初始化容器运行失败，那么 kubernetes 需要重启它直到成功完成
  - 每个初始化容器都必须按照定义的顺序串行运行
- 生命周期钩子：容器生命周期钩子使得容器能够感知其自身生命周期管理中的事件，并在相应的时刻到来时运行由用户指定的处理程序代码。Kubernetes 为容器提供了两种生命周期钩子：
  - postStart：容器创建之后会立即运行，但并不能确保一定会在容器额 ENTRYPOINT 之前运行
  - preStop：容器终止之前立即运行，以同步方式调用，在完成之前会阻塞删除容器的操作的调用  
  钩子处理器有 exec 和 http 两种，分别在触发时执行定义的命令和向指定 URL 发送请求。postStart 和 preStop 在 spec.lifecycle 字段定义
- 容器探测

### Replica Set
ReplicaSet 简称 RS，是 Pod 控制器的一种类型，用于确保由其管控的 Pod 对象副本数在任意时刻都能精确满足期望的数量。

ReplicaSet 可以实现以下功能：
- 确保 Pod 资源对象的数量精确反映期望值，ReplicaSet 会计算其控制运行的 Pod 的数量并向期望值匹配，如果 Pod 对象不足则会根据模板创建新的对象，如果超出则会删除多余的对象
- 确保 Pod 健康运行：RS 探测到由其管控的 Pod 对象因其所在的工作节点故障而不可用时，自动请求由调度器于其他工作节点创建缺失的 Pod 副本
- 弹性伸缩：业务规模因各种原因时常存在明显波动，在波峰或波谷期间，可以通过 ReplicaSet 控制器动态调整相关 Pod 资源对象的数量

ReplicaSet 对象使用 yaml 文件描述，其 spec 字段一般包含几个属性字段：
- replicas：期望的 Pod 对象副本数
- selector：控制器匹配 Pod 对象副本的标签选择器，支持 matchLabels 和 matchExpressions 两种匹配机制
- template：用于创建 Pod 副本的模板
- minReadySeconds：新建 Pod 对象在启动后容器未发生异常等待的时间，默认 0 秒

```yaml
aipVersion: api_version
kind: ReplicationSet
# 元数据
metadata:
  name: rs_name
spec:
  # 期望副本数
  replicas: rep_num
  # 标签选择器
  selector:
    matchLabels:
      label_key: label_value
    matchExpressions:
      expression: 
  # 容器启动后认为未发生异常前等待的时间
  minReadySeconds: 0
  # Pod 模板，和 Pod 定义相同
  template:
    ...
```
### Deployment
Deployment 构建于 ReplicaSet 控制器之上，可以为 Pod 和 ReplicaSet 资源提供声明式更新。Deployment 控制器资源的主要职责是为了保证 Pod 资源的健康运行，其大部分功能可以通过 ReplicaSet 实现，同时新增了部分特性：
- 事件和状态查看：可以查看 Deployment 对象升级的详细进度和状态
- 回滚：支持将应用回滚到前一个或用户指定的历史记录的版本上
- 版本记录：对 Deployment 对象的每一次操作都会保存，以供后续可能执行的回滚操作使用
- 暂停和启动：对于每一次升级都能够随时暂停和启动
- 多种自动更新方案：支持重建更新机制和滚动升级机制

Deployment 构建于 ReplicaSet 对象之上，因此 yaml 文件中 spec 字段中嵌套使用的字段包含了 ReplicaSet 支持的 replicas, selector, template 和 minReadySeconds，Deployment 利用这些字段完成 ReplicaSet 对象的创建。
```yaml
apiVersion: api_version
kid: Deployment
metadata:
  name: deployment_name
spec:
  replicas: rep_num
  selecotr:
    matchLabels:
  minReadyDelaySeconds: 0
  template:
    ...
```
### DaemonSet
DaemonSet 用于在集群中的全部节点上同时运行一份指定的 Pod 副本。当节点加入集群的时候会自动创建新的 Pod 对象，当节点从集群中移除时 Pod 对象会自动回收。

DaemonSet 通常运行执行系统操作任务的应用，如：
- 集群存储进程，如 glusterd, ceph
- 日志收集进程，如 fluentd, logstash
- 监控系统代理进程，如 prometheus node expoter

DaemonSet 的 spec 字段中同样包含 selector, template 和 minReadySeconds，但是不支持 replicas，其 yaml 描述示例：
```yaml
apiVersion: apps/v1
# 设置对象类型为 DaemonSet
kid: DaemonSet
metadata:
  name: filebeat-ds
  labels:
    app: filebeat
spec:
  seelctor:
    matchLabels:
      app: filebeat
  template:
    metadata:
      name: filebeat-pod
      labels:
        app: filebeat
    spec:
      containers:
        - name: filebeat-c
          image: ikubernetes/filebeat:5.6.5-alpine
          env:
            - name: REDIS_HOST
              value: db.linux.io:6379
            - name: LOG_LEVEL
              value: info
```
使用 ```kubectl applay -f <yaml_file>``` 可创建 DaemonSet，```kubectl get daemonset``` 可以查看创建的 DaemonSet，```kubectl describe daemonset <ds_anme>``` 可以查看创建的 DaemonSet 详情：
```shell
# 创建 DaemonSet
kubectl apply -f filebeat-ds.yaml

# 查看 DaemonSet
kubectl get daemonset

# 查看 DaemonSet 详情
kubectl describe daemonset filebeat-ds
``` 
DaemonSet 支持滚动更新(RollingUpdate)和删除更新(OnDelete)两种更新策略，默认为滚动更新。DaemonSet 的更新策略在 ```sepc.updateStrategy``` 字段设置，且通过 ```spec.maxUnavailabe``` 字段设定最大不可用的 Pod 数，通过 ```spec.minReadySeconds``` 设置认为 Pod 创建成功前等待的时间。
```shell
# 更新 daemonSet

```
### Job
Job 用于调配 Pod 对象运行一次性任务，容器中的进程在正常运行完成后不会重启，而是将 Pod 对象设置为 "Completed" 状态。如果容器中的进程因错误而终止，则需要根据配置确定其是否需要重启，未运行完成的 Pod 对象因其所在的节点故障而意外终止后会被重新调度。

Job 对象的 spec 属性中只有 template 是必须的，Job 会自动为创建的 Pod 对象添加 "job-name=JOB_NAME" 和 "controller-uid=UID" 标签，并使用标签选择器完成对 controller-uid 标签的关联：
```yaml
apiVersion: batch/v1
kind: Job
metadata:
  name: job-example
spec:
  template:
    spec:
      restartPolicy: Never
      containers:
      - name: myjob
        image: alpine
        command: ["bin/bash", "-c", "sleep 120"]
```
属性 ```spec.parallelism``` 设置运行任务的并行度，```spec.completions``` 属性设置任务运行的次数，可以以并行的形式运行任务：
```yaml
apiVersion: batch/v1
kind: Job
metadata:
  name: job-multi
spec:
  parallelism: 2
  completions: 5
  template:
    spec:
      restartPolicy: OnFailure
      containers:
      - name: myjob
        image: alpine
        command: ["/bin/sh", "-c", "sleep 20"]
```
```spec.parallelism``` 定义了任务的并行度，即同时运行任务的 Pod 的数量，此属性值支持运行时调整意味着可以实现动态扩容和缩容：
```shell
kubctl scale job <job_name> --replicas=<rep_num>
```
Job 调度的任务在完成后会被清理，如果任务一直运行失败且 ```spec.restartPolicy``` 设置为 OnFailure 则可能导致 Pod 一直处于不停的重启状态。Job 提供了 ```spec.activeDeadlineSeconds``` 属性用于指定任务最大的活动时间长度，```spec.backoffLimit``` 属性用于指定任务失败重试的次数，默认设置为 6
```yaml
spec:
  activeDeadlineSeconde: 100
  backoffLimit: 5
```
### CronJob
Job 定义的任务在其资源创建之后便会立即执行，但 CronJob 可以周期性的运行任务。CronJob 的 spec 字段可以嵌套使用以下字段：
- ```jobTemplate```：Job 控制器模板，用于为 CronJob 控制器生成 Job 对象，必须
- ```schedule```：Cron 格式的任务调度运行时间点
- ```concurrencyPolicy```：并发执行策略，可用值有 "Allow"，"Forbid" 和 "Replace"，用于定义前一次作业运行尚未完成时是否以及如何运行后一次的作业
- ```failedJobHistoryLimit```：为失败的任务执行保留的历史记录，默认为 1
- ```successfulJobsHistoryLimit```：为成功的任务执行保留的历史记录数，默认为 3
- ```startingDeadlineSeconds```：
- ```suspend```：是否挂起后续的任务执行，默认为 false

```yaml
apiVersion: batch/v1beta1
kind: CronJob
metadata:
  name: cronjob-example
  labels:
    app: mycrojob
spec:
  schedule: "*/2 * * * *"
  jobTemplate:
    metadata:
      labels:
        app: mycronjob-jobs
    spec:
      parallelism: 2
      template:
        spec:
          restartPolicy: OnFailure
          containers:
          - name: myjob
            image: alpine
            command:
            - /bin/sh
            - -c
            - date; echo Hello from the kubernetes cluster; sleep 10
```
### StatefulSet
### ConfigMap
### Service
Service 用于为 Pod 对象提供一个固定的、统一的访问接口及负载均衡能力，并支持借助于新一代 DNS 系统的服务发现功能，解决客户端发现并访问容器化应用的问题。

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
服务发现机制的基本实现一般是事先部署好一个网络位置较为稳定的服务注册中心，服务提供者(服务端)向注册中心注册自己的位置信息并在表动后及时给予更新，服务消费者则周期性的从注册中心获取服务提供者的最新位置从而“发现”要访问的目标服务资源。

根据服务发现过程的实现方式，服务发现可以分为两种类型：
- 客户端发现：客户端到服务注册中心发现其依赖到的服务的相关信息，因此需要内置特定的服务发现逻辑
- 服务端发现：服务消费者将请求发往负载均衡器，由它们负责查询服务注册中心获取服务提供者的位置信息并将服务消费者的请求转发给服务提供者



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
#### LoadBalancer
### Ingress
## Kubernetes 网络
Kubernetes 的网络使用插件的方式，任何实现方式都必须满足几个需求：
- 所有 Pod 间的通信不需要经过 NAT 机制而可以直接通信
- 不同 Pod 的容器间的通信不需要经过 NAT 机制而可以直接通信
- 容器自己使用的 IP 也是其他容器或节点直接看到的 IP

kubernetes 网络中有四种通信方式：
- 同一 Pode 的容器间通信：同一个 Pod 的容器共享同一个网络命名空间，它们之间的访问可以通过 localhost 地址和容器端口实现
- 同一个 Node 的 Pod 间通信：同一个 Node 的 Pod 处于同一个网段内，可以直接通信
- 不同 Node 的 Pod 间通信：不同 Pod 间的通信只能通过宿主机的物理网卡进行，通过 Service 对象将 kube-proxy 配置为 iptables 或者 ipvs 规则实现流量转发
### kube-proxy

## Kubernetes 存储
## Kubernetes 安全
Kubernetes 通过一系列的安全机制来实现集群安全，集群的安全性需要考虑几个目标：
- 容器与宿主机的隔离
- 符合最小权限原则，确保组件只执行被授予权限的行为
- 允许拥有 Secret 数据(keys, certs, passwords) 的应用在集群中运行

### API 服务器认证
## Pod 资源调度
Kubernetes 的核心任务是创建 Pod 对象并确保其以期望的状态运行，创建 Pod 对象时调度器(scheduler)负责为每一个未经调度的 Pod 对象以一定的规则从集群中挑选一个合适的节点来运行，因此也称为 Pod 调度器。

Kubernetes 调度的核心目标是基于资源可用性将各个 Pod 资源公平地分布于集群节点之上。目前 Kubernetes 提供的默认调度器通过三个步骤完成调度：
- 节点预选(Predicate)：基于一系列预选规则对每个节点进行检查，将不符合条件的节点过滤
- 节点优先级排序(Priority)：对预选出的节点进行优先级排序，以便找出最适合运行 Pod 的对象节点
- 节点选择(Select)：从优先级排序结果中挑选出优先级别最高的节点运行 Pod 对象
### 预选策略
### 优先级排序函数

## Kubernetes 源码
### apiserver
### kube-scheduler
### 


ref:
- https://blog.upweto.top/gitbooks/kubeSourceCodeNote/
- https://github.com/kubernetes/community/tree/master/contributors/devel
- http://hutao.tech/k8s-source-code-analysis/