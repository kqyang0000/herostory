package org.tinygame.herostory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {
    static private Logger logger = LoggerFactory.getLogger(GameMsgHandler.class);

    /**
     * 信道组，这里一定要用static，
     * 否则无法实现群发
     */
    static private final ChannelGroup _channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    static private final Map<Integer, User> _userMap = new HashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (ctx == null) {
            return;
        }
        try {
            super.channelActive(ctx);
            _channelGroup.add(ctx.channel());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        try {

            Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

            if (userId == null) {
                return;
            }

            _userMap.remove(userId);

            GameMsgProtocol.UserQuitResult.Builder resultBuilder = GameMsgProtocol.UserQuitResult.newBuilder();
            resultBuilder.setQuitUserId(userId);

            GameMsgProtocol.UserQuitResult newResult = resultBuilder.build();
            _channelGroup.writeAndFlush(newResult);

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

        /**
         * 用户入场
         */
        if (msg instanceof GameMsgProtocol.UserEntryCmd) {
            GameMsgProtocol.UserEntryCmd cmd = (GameMsgProtocol.UserEntryCmd) msg;
            int userId = cmd.getUserId();
            String heroAvatar = cmd.getHeroAvatar();

            User newUser = new User();
            newUser.setUserId(userId);
            newUser.setHeroAvatar(heroAvatar);
            _userMap.putIfAbsent(userId, newUser);

            //存储用户id，保存至session
            ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);

            GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
            resultBuilder.setUserId(userId);
            resultBuilder.setHeroAvatar(heroAvatar);

            //构建结果，发送广播
            GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
            _channelGroup.writeAndFlush(newResult);
            /**
             * 用户询问谁在场
             */
        } else if (msg instanceof GameMsgProtocol.WhoElseIsHereCmd) {
            GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();

            for (User currUser : _userMap.values()) {
                if (currUser == null) {
                    continue;
                }
                GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder userInfoBuilder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
                userInfoBuilder.setUserId(currUser.getUserId());
                userInfoBuilder.setHeroAvatar(currUser.getHeroAvatar());
                resultBuilder.addUserInfo(userInfoBuilder);
            }

            GameMsgProtocol.WhoElseIsHereResult newBuilder = resultBuilder.build();
            ctx.writeAndFlush(newBuilder);
            /**
             * 用户移动
             */
        } else if (msg instanceof GameMsgProtocol.UserMoveToCmd) {

            Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
            if (userId == null) {
                return;
            }

            GameMsgProtocol.UserMoveToCmd cmd = (GameMsgProtocol.UserMoveToCmd) msg;
            GameMsgProtocol.UserMoveToResult.Builder resultBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();
            resultBuilder.setMoveUserId(userId);
            resultBuilder.setMoveToPosX(cmd.getMoveToPosX());
            resultBuilder.setMoveToPosY(cmd.getMoveToPosY());

            GameMsgProtocol.UserMoveToResult newResult = resultBuilder.build();
            _channelGroup.writeAndFlush(newResult);

        }
    }
}
