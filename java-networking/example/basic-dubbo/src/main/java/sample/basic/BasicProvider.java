package sample.basic;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import sample.basic.api.DemoService;
import sample.basic.impl.DemoServiceImpl;

public class BasicProvider {

    public static void main(String[] args) {

        DemoService demoService = new DemoServiceImpl();

        // application
        ApplicationConfig application = new ApplicationConfig("demoService");

        // registry
        RegistryConfig registry = new RegistryConfig("zookeeper://127.0.0.1:2181");

        // protocol
        ProtocolConfig protocol = new ProtocolConfig("dubbo", 12345);
        protocol.setThreads(2);

        // service
        ServiceConfig<DemoService> service = new ServiceConfig<>();
        service.setApplication(application);
        service.setRegistry(registry);
        service.setProtocol(protocol);
        service.setRef(demoService);

        // export service
        service.export();
    }
}
