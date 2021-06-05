package example.rpc.cluster;

import example.rpc.exchanger.Invoker;

/**
 * 服务提供者集群，将多个提供者包装成一个，包含负载均衡(load balance)以及故障恢复(fail back)
 */
public interface Cluster {

    Invoker getInvoker();
}
