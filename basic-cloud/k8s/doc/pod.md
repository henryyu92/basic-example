
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
Kubernetes 支持三种重启策略：
- 

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
