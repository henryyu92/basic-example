## Try-with-resource

在 Java 中被打开的系统资源，比如文件流、Socket 连接等都需要在使用完手动关闭，以免发生资源泄露。通常关闭资源的动作在 finally 代码块中处理，当关闭资源的动作出现异常时需要捕捉并处理异常，当 try 中的资源较多则 finally 代码块就会显得很臃肿：
```java
public void tryFinallyResource(String inFile, String outFile){
    BufferedInputStream in;
    BufferedOutputStream out;
    
    try{
        in = new BufferedInputStream(new FileInputStream(new File(inFile)));
        out = new BufferedOutputStream(new FileOutputStream(new File(outFile)));

        int b = 0;
        while((b == in.read()) != -1){
            out.writeAndFlush(b)
        }
    }finally{
        if(in != null){
            try{
                in.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        if(out != null){
            try{
                out.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
```

Java 中提供了 try-with-resource 解决这种问题，能使用 try-with-resource 的资源一定需要实现 AutoCloseable 接口并在实现的 close 方法中自定义资源的关闭逻辑，编译器会在遇到 try-with-resource 时自动生成 finally 代码块并在其中调用 close 方法实现自定义的关闭资源。
```java
public class Connection implements AutoCloseable {

    @Override
    public void close() throws Exception {
        System.out.println("正在关闭资源");
    }
}

try(Connection conn = new Connection()){

}catch(Exception e){
    e.printStackTrace();
}
```

在使用 try-with-resource 关闭资源时一定要注意 close 方法的实现，避免依然会出现资源泄露的问题。