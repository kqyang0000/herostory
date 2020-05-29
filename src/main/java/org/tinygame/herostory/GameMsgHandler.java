package org.tinygame.herostory;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.cmdhandler.ICmdHandler;
import org.tinygame.herostory.cmdhandler.UserEntryCmdHandler;
import org.tinygame.herostory.cmdhandler.UserMoveToCmdHandler;
import org.tinygame.herostory.cmdhandler.WhoElseIsHereCmdHandler;
import org.tinygame.herostory.msg.GameMsgProtocol;

public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {
    static private Logger logger = LoggerFactory.getLogger(GameMsgHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (ctx == null) {
            return;
        }
        try {
            super.channelActive(ctx);
            Broadcaster.addChannel(ctx.channel());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        if (ctx == null) {
            return;
        }

        try {

            Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

            if (userId == null) {
                return;
            }

            UserManager.removeByUserId(userId);

            GameMsgProtocol.UserQuitResult.Builder resultBuilder = GameMsgProtocol.UserQuitResult.newBuilder();
            resultBuilder.setQuitUserId(userId);

            GameMsgProtocol.UserQuitResult newResult = resultBuilder.build();
            Broadcaster.broadcast(newResult);
            Broadcaster.removeChannel(ctx.channel());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg == null) {
            return;
        }

        logger.info(
                "收到客户端消息，msgType = {}, msg = {}",
                msg.getClass().getSimpleName(),
                msg
        );
        try {
            ICmdHandler<? extends GeneratedMessageV3> cmdHandler = CmdHandlerFactory.create(msg.getClass());
            if (cmdHandler != null) {
                cmdHandler.handle(ctx, cast(msg));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    static private <TCmd extends GeneratedMessageV3> TCmd cast(Object msg) {
        if (msg == null) {
            return null;
        } else {
            return (TCmd) msg;
        }
    }
}
