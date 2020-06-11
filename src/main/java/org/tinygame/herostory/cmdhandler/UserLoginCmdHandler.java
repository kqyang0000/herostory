package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.login.LoginService;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

/**
 * 用户登录
 */
public class UserLoginCmdHandler implements ICmdHandler<GameMsgProtocol.UserLoginCmd> {
    static private final Logger LOGGER = LoggerFactory.getLogger(UserLoginCmdHandler.class);

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

        LOGGER.info("当前线程 = {}", Thread.currentThread().getName());

        //获取用户实体
        LoginService.getInstance().userLogin(userName, password, (userEntity) -> {
            GameMsgProtocol.UserLoginResult.Builder resultBuilder = GameMsgProtocol.UserLoginResult.newBuilder();

            LOGGER.info("当前线程 = {}", Thread.currentThread().getName());

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

            return null;
        });
    }
}
