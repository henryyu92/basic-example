package example.rpc.dubbo.common.cluster;

import java.util.List;

/**
 * 服务提供者集群，将多个提供者包装成一个
 */
public interface Cluster {

    List<Invoker> getInvokers();
}
