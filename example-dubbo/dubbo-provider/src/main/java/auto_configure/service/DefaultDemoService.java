package auto_configure.service;

import api.HelloService;


public class DefaultDemoService implements HelloService {

    private String serviceName;

    public DefaultDemoService(){
        System.out.println("hello ...");
    }

    @Override
    public String sayHello(String name) {
        return String.format("[%s] : Hello, %s", serviceName, name);
    }
}
