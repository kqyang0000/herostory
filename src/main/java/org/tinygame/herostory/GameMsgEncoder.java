package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {
    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgEncoder.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        if (ctx == null || msg == null) {
            return;
        }
        try {
            if (!(msg instanceof GeneratedMessageV3)) {
                super.write(ctx, msg, promise);
                return;
            }

            //消息编码
            int msgCode = GameMsgRecognizer.getMsgCodeByClass(msg.getClass());
            if (msgCode == -1) {
                LOGGER.error("无法识别的消息类型，magClass = {} ", msg.getClass().getSimpleName());
                super.write(ctx, msg, promise);
            }

            //消息体
            byte[] msgBody = ((GeneratedMessageV3) msg).toByteArray();

            ByteBuf byteBuf = ctx.alloc().buffer();
            //消息长度
            byteBuf.writeShort((short) msgBody.length);
            //消息编码
            byteBuf.writeShort((short) msgCode);
            //消息体
            byteBuf.writeBytes(msgBody);

            BinaryWebSocketFrame outputFrame = new BinaryWebSocketFrame(byteBuf);
            super.write(ctx, outputFrame, promise);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }
}
