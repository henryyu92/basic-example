package example.jvm;

/**
 * SPI 接口实现类
 */
public class SpiInterfaceImpl implements SpiInterface {

  @Override
  public void sayHello(String name) {
    System.out.println("hello " + name);
  }
}
