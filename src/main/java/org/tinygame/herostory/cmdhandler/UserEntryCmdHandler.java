package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.User;
import org.tinygame.herostory.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd cmd) {
        if (ctx == null || cmd == null) {
            return;
        }
        int userId = cmd.getUserId();
        String heroAvatar = cmd.getHeroAvatar();

        User newUser = new User();
        newUser.setUserId(userId);
        newUser.setHeroAvatar(heroAvatar);
        UserManager.addUser(newUser);

        //存储用户id，保存至session
        ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);

        GameMsgProtocol.UserEntryResult.Builder resultBuilder = GameMsgProtocol.UserEntryResult.newBuilder();
        resultBuilder.setUserId(userId);
        resultBuilder.setHeroAvatar(heroAvatar);

        //构建结果，发送广播
        GameMsgProtocol.UserEntryResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }
}
