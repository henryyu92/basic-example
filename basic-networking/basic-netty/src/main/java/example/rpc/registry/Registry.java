package example.rpc.registry;

import java.net.URL;

/**
 * 注册中心
 */
public interface Registry {

    void register(String appName, URL url);

    URL subscribe(String appName);
}
