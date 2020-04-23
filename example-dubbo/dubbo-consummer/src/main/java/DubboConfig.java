import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Administrator
 * @date 2019/10/22
 */
@Configuration
@EnableConfigurationProperties({DubboProperties.class})
public class DubboConfig {
    @Autowired
    private DubboProperties dubboProperties;

}
