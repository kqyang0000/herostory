package org.tinygame.herostory;

import com.google.protobuf.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 解码器
 */
public class GameMsgDecoder extends ChannelInboundHandlerAdapter {
    static private final Logger LOGGER = LoggerFactory.getLogger(GameMsgDecoder.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (ctx == null || msg == null) {
            return;
        }

        if (!(msg instanceof BinaryWebSocketFrame)) {
            return;
        }

        try {
            BinaryWebSocketFrame socketFrame = (BinaryWebSocketFrame) msg;
            ByteBuf byteBuf = socketFrame.content();

            //消息的长度
            short msgLenth = byteBuf.readShort();
            //消息编码
            short msgCode = byteBuf.readShort();

            //拿到消息体
            byte[] msgBody = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(msgBody);

            //获取消息构造器
            Message.Builder msgBuilder = GameMsgRecognizer.getBuilderByMsgCode(msgCode);
            msgBuilder.clear();
            msgBuilder.mergeFrom(msgBody);
            Message cmd = msgBuilder.build();

            if (cmd != null) {
                ctx.fireChannelRead(cmd);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
