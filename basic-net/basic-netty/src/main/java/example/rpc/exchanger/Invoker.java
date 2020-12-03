package example.rpc.exchanger;

import java.util.concurrent.Future;

/**
 * 封装底层网络实现，将底层网络实现转换为 Request -> Response 模式
 */
public interface Invoker {

    Response invoke(Request request);

    Future<Response> asyncInvoke(Request request);
}
