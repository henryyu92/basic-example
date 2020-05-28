package example.spi;

import org.apache.dubbo.common.extension.ExtensionLoader;

public class SpiTest {
    public static void main(String[] args) {

        ExtensionLoader<HelloTest> loader = ExtensionLoader.getExtensionLoader(HelloTest.class);

        HelloTest hello = loader.getExtension("hello");

        hello.hello("world");

    }
}
