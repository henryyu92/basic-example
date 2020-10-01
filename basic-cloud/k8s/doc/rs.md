## 无状态资源对象

### Replication Controller

Replication Controller(RC) 是 Kubernetes 中重要的资源对象，定义了 Pod 的副本数量在任意时刻都符合预期值。RC 的描述文件需要包含几个部分：

- Pod 期望的副本数
- 用于筛选 Pod 的 `LabelSelector`
- 创建 Pod 的资源模板

```yaml
apiVersion: v1
kind: ReplicationController
metadata:
	name: frontend
spec:
	# 期望副本数
	replicas: 1
	# 选择器
	selector:
		tier: frontend
	# Pod 模板
	template:
		metadata:
			labels:
				app: app-demo
				tier: frontend
		spec:
			containers:
			- name: tomcat-demo
			  image: tomcat_image
			  # 镜像拉取策略
			  imagePullPolicy: IfNotPresent
			  env:
			  - name: GET_HOST_FROM
			    value: dns
			  port:
			  - containerPort: 80
```

创建 RC 后，Master 的 `ControllerManager` 组件就会得到通知定期检查系统中当前运行的目标 Pod，并确保目标 Pod 实例的数量与 RC 的期望值相等。

在运行时也可以通过修改 RC 的副本数量来实现 Pod 的动态缩放，使用命令 `kubectl scale` 可以实现：

```sh
# Pod 扩容
kubectl scale rc <rc_name> --replicas=n

# 停止 RC 及其控制的 Pod
kubectl stop rc <rc_name>
# 删除 RC 及其控制的 Pod
kubectl delete rc <rc_name>
```



### Replica Set

Replica Set(RS) 是 RC 的升级，用于确保由其管控的 Pod 对象副本数在任意时刻都能精确满足期望的数量。和 RC 不同的是，RS 使用基于集合的标签选择，这使得 RS 的功能更加强大。*RS 一般被 Deployment 对象使用而很少单独使用*。

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
  	# 二者同时存在时是 AND 的关系
    matchLabels:
      label_key: label_value
    matchExpressions:
      expression: 
      	- {key: label_key, operator: In, values: [lavel_value]}
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

使用 `kubectl` 命令可以创建和查看 Deployment：

```sh
# 创建资源对象
kubectl create -f dp.yaml

# 查看资源对象
kubectl get deployments
```



### Horizontal Pod Autoscaler

手动执行 `kubectl scale` 命令可以实现 Pod 的扩容和缩容，Kubernetes 进一步的提供了 Horizontal Pod Autoscaler(HPA) 用于自动的扩容和缩容。

HPA 通过追踪分析指定 RS 控制的目标 Pod 的负载情况来确定是否需要有针对性地调整目标 Pod 的副本数量，当前 HPA 通过两种方式度量 Pod 的负载：

- CPUUtilizationPercentage
- 应用自定义度量指标，比如服务的 TPS 或者 QPS

`CPUUtilizationPercentage` 是一个算术平均值，即目标 Pod 所有副本自身的 CPU 利用率的平均值。Pod 自身 CPU 利用率是该 Pod 当前 CPU 的使用量除以 Pod Request 的值，如果目标 Pod 没有设置 Pod Request 则无法使用 `CPUUtilizationPercentage`。

Kubernetes 定义了一套标准的数据采集接口 Respurce Metrics API 用于客户端从 Metrics Server 中获取目标资源对象的性能数据。

```yaml
apiVersion: autoscaling/v1
kind: HorizontalPodAutoscaler
metadata:
  name: hpa_name
  namespace: default
spec:
  maxReplicas: 10
  minReplicas: 1
  scaleTargetRef:
    kind: Deployment
    name: dep_name
  targetCPUUtilizationPercentage: 90
  # ...
```

