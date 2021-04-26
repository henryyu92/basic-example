package example.jvm;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class ClassLoaderMain {

    public static void main(String[] args) {

        ServiceLoader<SpiInterface> services = ServiceLoader.load(SpiInterface.class);
        for (SpiInterface service : services) {
            service.sayHello("tom");
        }
//        services.iterator().forEachRemaining(s ->{
//            // do something
//            s.sayHello("tom");
//        });

        ClassLoaderMain classLoaderMain = new ClassLoaderMain();
        classLoaderMain.spiSimulator();
    }

    /**
     * 模拟 java spi 机制
     */
    public void spiSimulator(){
        try(InputStream inputStream =
            ClassLoader.getSystemClassLoader()
//            Thread.currentThread().getContextClassLoader()
            .getResourceAsStream("META-INF/services/example.jvm.SpiInterface");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));){
            int ln = 1;
            ArrayList<String> names = new ArrayList<>();
            while ((ln = parseLine(reader, ln, names)) >= 0);
            names.forEach(s ->{
                System.out.println(s);
                try {
                    SpiInterface o = (SpiInterface) Class.forName(s).newInstance();
                    o.sayHello("tom");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private int parseLine(BufferedReader reader, int lc, List<String> names) throws IOException {
        String ln = reader.readLine();
        if (ln == null) {
            return -1;
        }
        int ci = ln.indexOf('#');
        if (ci >= 0) ln = ln.substring(0, ci);
        ln = ln.trim();
        int n = ln.length();
        if (n != 0) {
            if ((ln.indexOf(' ') >= 0) || (ln.indexOf('\t') >= 0)) {
                System.out.println("Illegal configuration-file syntax");
            }
            int cp = ln.codePointAt(0);
            if (!Character.isJavaIdentifierStart(cp)) {
                System.out.println("Illegal provider-class name: " + ln);
            }
            for (int i = Character.charCount(cp); i < n; i += Character.charCount(cp)) {
                cp = ln.codePointAt(i);
                if (!Character.isJavaIdentifierPart(cp) && (cp != '.'))
                    System.out.println("Illegal provider-class name: " + ln);
            }
            if (!names.contains(ln))
                names.add(ln);
        }
        return lc + 1;
    }
}
