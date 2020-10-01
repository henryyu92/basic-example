## 核心组件

### API Server

### Controller Manager

### Scheduler

#### Pod 资源调度

Kubernetes 的核心任务是创建 Pod 对象并确保其以期望的状态运行，创建 Pod 对象时调度器(scheduler)负责为每一个未经调度的 Pod 对象以一定的规则从集群中挑选一个合适的节点来运行，因此也称为 Pod 调度器。

Kubernetes 调度的核心目标是基于资源可用性将各个 Pod 资源公平地分布于集群节点之上。目前 Kubernetes 提供的默认调度器通过三个步骤完成调度：

- 节点预选(Predicate)：基于一系列预选规则对每个节点进行检查，将不符合条件的节点过滤
- 节点优先级排序(Priority)：对预选出的节点进行优先级排序，以便找出最适合运行 Pod 的对象节点
- 节点选择(Select)：从优先级排序结果中挑选出优先级别最高的节点运行 Pod 对象

#### 预选策略

#### 优先级排序函数

### kubelet

### kube-proxy