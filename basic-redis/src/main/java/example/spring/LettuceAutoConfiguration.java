package example.spring;

import io.lettuce.core.RedisURI;
import io.lettuce.core.resource.ClientResources;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(RedisURI.class)
@EnableConfigurationProperties(LettuceProperties.class)
public class LettuceAutoConfiguration {

    private LettuceProperties properties;

    @Bean(destroyMethod = "shutdown")
    public ClientResources clientResources() {
        return ClientResources.create();
    }

    @Bean
    @ConditionalOnProperty("lettuce.single.host")
    public RedisURI standalone(){
        return RedisURI.builder().build();
    }
}
