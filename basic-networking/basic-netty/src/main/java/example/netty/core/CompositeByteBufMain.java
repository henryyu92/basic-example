package example.netty.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Netty 使用 CompositeByteBuf 包装多个 ByteBuf 从而避免多个 ByteBuf 之间的数据拷贝
 */
public class CompositeByteBufMain {

    public static void warp(ByteBuf buf){
        ByteBuf byteBuf = Unpooled.wrappedBuffer(buf);
    }
}
