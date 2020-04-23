package auto_configure.service;

import api.HelloService;

/**
 * @author Administrator
 * @date 2019/6/7
 */

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
