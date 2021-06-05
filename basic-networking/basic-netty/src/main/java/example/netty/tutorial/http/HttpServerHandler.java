package example.netty.tutorial.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

        System.out.println("client address: " + ctx.channel().remoteAddress());

        if (msg instanceof HttpRequest){
            HttpRequest req = (HttpRequest) msg;

            boolean keepAlive = HttpUtil.isKeepAlive(req);

            ByteBuf content = Unpooled.copiedBuffer("hello world", CharsetUtil.UTF_8);
            FullHttpResponse response = new DefaultFullHttpResponse(req.protocolVersion(), HttpResponseStatus.OK, content);

            response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN)
                .setInt(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

            if (keepAlive){
                if (!req.protocolVersion().isKeepAliveDefault()){
                    response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                }
            }else{
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            }

            ChannelFuture f = ctx.write(response);
            if (!keepAlive){
                f.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
