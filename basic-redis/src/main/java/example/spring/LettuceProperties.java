package example.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "lettuce")
public class LettuceProperties {

    private String host;
    private Integer port;
    private String password;
}
