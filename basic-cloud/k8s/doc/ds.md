#### DaemonSet

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

#### 