package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import org.tinygame.herostory.User;
import org.tinygame.herostory.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

public class WhoElseIsHereCmdHandler implements ICmdHandler<GameMsgProtocol.WhoElseIsHereCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.WhoElseIsHereCmd cmd) {
        if (ctx == null || cmd == null) {
            return;
        }
        GameMsgProtocol.WhoElseIsHereResult.Builder resultBuilder = GameMsgProtocol.WhoElseIsHereResult.newBuilder();

        for (User currUser : UserManager.listUser()) {
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
    }
}
