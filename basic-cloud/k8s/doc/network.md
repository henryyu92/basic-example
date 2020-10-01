## Kubernetes 网络

Kubernetes 的网络使用插件的方式，任何实现方式都必须满足几个需求：

- 所有 Pod 间的通信不需要经过 NAT 机制而可以直接通信
- 不同 Pod 的容器间的通信不需要经过 NAT 机制而可以直接通信
- 容器自己使用的 IP 也是其他容器或节点直接看到的 IP

kubernetes 网络中有四种通信方式：

- 同一 Pode 的容器间通信：同一个 Pod 的容器共享同一个网络命名空间，它们之间的访问可以通过 localhost 地址和容器端口实现
- 同一个 Node 的 Pod 间通信：同一个 Node 的 Pod 处于同一个网段内，可以直接通信
- 不同 Node 的 Pod 间通信：不同 Pod 间的通信只能通过宿主机的物理网卡进行，通过 Service 对象将 kube-proxy 配置为 iptables 或者 ipvs 规则实现流量转发