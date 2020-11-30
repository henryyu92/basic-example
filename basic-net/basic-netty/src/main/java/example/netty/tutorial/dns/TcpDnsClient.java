package example.netty.tutorial.dns;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.dns.TcpDnsQueryEncoder;
import io.netty.handler.codec.dns.TcpDnsResponseDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.concurrent.TimeUnit;

public class TcpDnsClient {

    public static final String QUERY_DOMAIN = "www.example.com";
    private static final int DNS_SERVER_PORT = 53;
    private static final String DNS_SERVER_HOST = "8.8.8.8";

    private final Bootstrap b = new Bootstrap();
    private final NioEventLoopGroup group;

    public TcpDnsClient(){
        group = new NioEventLoopGroup();

        b.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                p.addLast(new LoggingHandler(LogLevel.INFO))
                    .addLast(new TcpDnsQueryEncoder())
                    .addLast(new TcpDnsResponseDecoder())
                    .addLast(new TcpDnsClientHandler());
            }
        });
    }

    public void connect() throws Exception {
        try{
            ChannelFuture f = b.connect(DNS_SERVER_HOST, DNS_SERVER_PORT).sync();

            boolean success = f.channel().closeFuture().await(10, TimeUnit.SECONDS);
            if (!success){
                System.err.println("dns query timeout!");
                f.channel().close().sync();
            }
        }finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        TcpDnsClient client = new TcpDnsClient();
        client.connect();
    }
}
