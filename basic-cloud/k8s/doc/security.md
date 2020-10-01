## Kubernetes 安全

Kubernetes 通过一系列的安全机制来实现集群安全，集群的安全性需要考虑几个目标：

- 容器与宿主机的隔离
- 符合最小权限原则，确保组件只执行被授予权限的行为
- 允许拥有 Secret 数据(keys, certs, passwords) 的应用在集群中运行