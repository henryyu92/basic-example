package example.rpc.dubbo.provider;

import example.rpc.dubbo.client.HelloService;

/**
 * 暴露服务的实现
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String msg) {
        return "hello " + msg;
    }
}
