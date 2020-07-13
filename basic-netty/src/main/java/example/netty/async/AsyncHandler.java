package example.netty.async;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class AsyncHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {


    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("read complete", CharsetUtil.UTF_8));
    }

    /**
     * 提交任务到 NioEventLoop 的任务队列 taskQueue
     * @param ctx
     * @param msg
     */
    private void asyncHandle(ChannelHandlerContext ctx, Object msg){
        ctx.channel().eventLoop().execute(()->{
            try {
                Thread.sleep(10 * 1000);
                ctx.writeAndFlush(Unpooled.copiedBuffer("hello world", CharsetUtil.UTF_8));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 提交定时执行的任务到 NioEventLoop 的任务队列 scheduledQueue
     * @param ctx
     * @param msg
     */
    private void scheduledHandle(ChannelHandlerContext ctx, Object msg){
        ScheduledFuture<?> future = ctx.channel().eventLoop().schedule(() -> {
            try {
                Thread.sleep(10 * 1000);
                ctx.writeAndFlush(Unpooled.copiedBuffer("hello world", CharsetUtil.UTF_8));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, 10, TimeUnit.SECONDS);

        try {
            Object result = future.get();
            System.out.printf(result.toString());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

}
