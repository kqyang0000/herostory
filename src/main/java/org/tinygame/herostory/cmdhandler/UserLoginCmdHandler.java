package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.login.LoginService;
import org.tinygame.herostory.login.db.UserEntity;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * 用户登录
 */
public class UserLoginCmdHandler implements ICmdHandler<GameMsgProtocol.UserLoginCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserLoginCmd cmd) {
        if (ctx == null || cmd == null) {
            return;
        }

        String userName = cmd.getUserName();
        String password = cmd.getPassword();

        if (userName == null || password == null) {
            return;
        }

        //获取用户实体
        UserEntity userEntity = LoginService.getInstance().userLogin(userName, password);

        GameMsgProtocol.UserLoginResult.Builder resultBuilder = GameMsgProtocol.UserLoginResult.newBuilder();

        if (userEntity == null) {
            resultBuilder.setUserId(-1);
            resultBuilder.setUserName("");
            resultBuilder.setHeroAvatar("");
        } else {

            User newUser = new User();
            newUser.setUserId(userEntity.getUserId());
            newUser.setUserName(userEntity.getUserName());
            newUser.setHeroAvatar(userEntity.getHeroAvatar());
            newUser.setCurHp(100);
            UserManager.addUser(newUser);

            //存储用户id，保存至session
            ctx.channel().attr(AttributeKey.valueOf("userId")).set(userEntity.getUserId());

            resultBuilder.setUserId(userEntity.getUserId());
            resultBuilder.setUserName(userEntity.getUserName());
            resultBuilder.setHeroAvatar(userEntity.getHeroAvatar());
        }

        GameMsgProtocol.UserLoginResult newResult = resultBuilder.build();
        ctx.writeAndFlush(newResult);
    }
}
