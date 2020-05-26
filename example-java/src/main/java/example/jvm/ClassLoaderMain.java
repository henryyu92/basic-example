package example.jvm;


import java.util.ServiceLoader;

public class ClassLoaderMain {

    public static void main(String[] args) {

        ServiceLoader<SpiInterface> services = ServiceLoader.load(SpiInterface.class);
        services.iterator().forEachRemaining(s ->{
            // do something
        });
    }
}
