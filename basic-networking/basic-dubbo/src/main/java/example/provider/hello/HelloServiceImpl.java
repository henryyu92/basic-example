package example.provider.hello;

import org.apache.dubbo.rpc.RpcContext;
import example.api.hello.HelloService;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {

        System.out.println("[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] Hello " + name +
            ", request from consumer: " + RpcContext.getContext().getRemoteAddress());
        return "Hello " + name + ", response from provider: " + RpcContext.getContext().getLocalAddress();
    }
}
