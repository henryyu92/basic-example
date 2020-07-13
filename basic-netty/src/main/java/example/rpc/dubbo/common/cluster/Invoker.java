package example.rpc.dubbo.common.cluster;

import example.rpc.dubbo.common.exchanger.Request;
import example.rpc.dubbo.common.exchanger.Response;

/**
 *
 */
public interface Invoker {

    Response invoke(Request request);
}
