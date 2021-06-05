package example.provider;

import example.provider.hello.HelloServiceImpl;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import example.api.hello.HelloService;

public class BasicProvider {

    public static void main(String[] args) {

        HelloService helloService = new HelloServiceImpl();

        // application
        ApplicationConfig application = new ApplicationConfig("demoService");

        // registry
        RegistryConfig registry = new RegistryConfig("zookeeper://127.0.0.1:2181");

        // protocol
        ProtocolConfig protocol = new ProtocolConfig("dubbo", 12345);
        protocol.setThreads(2);

        // service
        ServiceConfig<HelloService> service = new ServiceConfig<>();
        service.setApplication(application);
        service.setRegistry(registry);
        service.setProtocol(protocol);
        service.setRef(helloService);

        // export service
        service.export();
    }
}
