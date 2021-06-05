package example.netty.tutorial.http.soop;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;

import java.util.Map;
import java.util.Set;

public class HttpSnoopServerHandler extends SimpleChannelInboundHandler<Object> {

    private HttpRequest request;
    private final StringBuilder buf = new StringBuilder();

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest){
            HttpRequest request = this.request = (HttpRequest) msg;
            if (HttpUtil.is100ContinueExpected(request)){
                send100Continue(ctx);
            }
            buf.setLength(0);
            buf.append("WELCOME TO THE WILD WILD WEB SERVER\r\n");
            buf.append("===================================\r\n");

            buf.append("VERSION: ").append(request.protocolVersion()).append("\r\n");
            buf.append("HOSTNAME: ").append(request.headers().get(HttpHeaderNames.HOST, "unknown")).append("\r\n");
            buf.append("REQUEST_URI: ").append(request.uri()).append("\r\n\r\n");

            HttpHeaders headers = request.headers();
            if (!headers.isEmpty()){
                for (Map.Entry<String, String> h : headers){
                    String key = h.getKey();
                    String value = h.getValue();
                    buf.append("HEADER: ").append(key).append(" = ").append(value).append("\r\n");
                }
                buf.append("\r\n");
            }

            appendDecoderResult(buf, request);
        }
        if (msg instanceof HttpContent){
            HttpContent httpContent = (HttpContent) msg;

            ByteBuf content = httpContent.content();
            if (content.isReadable()){
                buf.append("CONTENT: ").append(content.toString(CharsetUtil.UTF_8)).append("\r\n");
                appendDecoderResult(buf, request);
            }

            if (msg instanceof LastHttpContent){
                buf.append("END OF CONTENT \r\n");

                LastHttpContent trailer = (LastHttpContent) msg;
                if (!trailer.trailingHeaders().isEmpty()){
                    buf.append("\r\n");
                    for (String name : trailer.trailingHeaders().names()){
                        for (String value : trailer.trailingHeaders().getAll(name)){
                            buf.append("TRAILING HEADER: ").append(name).append(" = ").append("\r\n");
                        }
                    }
                    buf.append("\r\n");
                }

                if (!writeResponse(trailer, ctx)){
                    // If keep-alive is off, close the connection once the content is fully written.
                    ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                }
            }
        }
    }

    private void appendDecoderResult(StringBuilder buf, HttpObject o){
        DecoderResult result = o.decoderResult();
        if (result.isSuccess()){
            return;
        }

        buf.append(".. WITH DECODER FAILURE: ").append(result.cause()).append("\r\n");
    }

    private boolean writeResponse(HttpObject currentObj, ChannelHandlerContext ctx){

        boolean keepAlive = HttpUtil.isKeepAlive(request);
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
            currentObj.decoderResult().isSuccess() ? HttpResponseStatus.OK : HttpResponseStatus.BAD_REQUEST,
            Unpooled.copiedBuffer(buf.toString(), CharsetUtil.UTF_8));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);

        if (keepAlive){
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }

        String cookieString = request.headers().get(HttpHeaderNames.COOKIE);
        if (!StringUtil.isNullOrEmpty(cookieString)){
            Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieString);
            if (!cookies.isEmpty()){
                response.headers().set(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookies));
            }
        }else{
            response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode("key", "value"));
        }

        ctx.write(response);
        return keepAlive;
    }

    private void send100Continue(ChannelHandlerContext ctx){
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE, Unpooled.EMPTY_BUFFER);
        ctx.write(response);
    }
}
