package example.rpc.exchanger;

import java.util.concurrent.Future;

/**
 * 封装底层网络实现，将方法调用转换为 Request -> Response
 */
public interface Invoker {

    Response invoke(Request request);

    Future<Response> asyncInvoke(Request request);
}
