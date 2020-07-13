package example.spi;

import org.apache.dubbo.common.extension.SPI;

@SPI
public class HelloTestImpl implements HelloTest {
    @Override
    public String hello(String msg) {
        return "hello " + msg;
    }
}
