package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.model.MoveState;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.msg.GameMsgProtocol;

public class UserMoveToCmdHandler implements ICmdHandler<GameMsgProtocol.UserMoveToCmd> {
    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd cmd) {
        if (ctx == null || cmd == null) {
            return;
        }
        Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
        if (userId == null) {
            return;
        }

        //获取已有用户
        User existUser = UserManager.getByUserId(userId);
        if (existUser == null) {
            return;
        }

        long nowTime = System.currentTimeMillis();

        MoveState moveState = existUser.getMoveState();
        moveState.setFromPosX(cmd.getMoveFromPosX());
        moveState.setFromPosY(cmd.getMoveFromPosY());
        moveState.setToPosX(cmd.getMoveToPosX());
        moveState.setToPosY(cmd.getMoveToPosY());
        moveState.setStartTime(nowTime);

        GameMsgProtocol.UserMoveToResult.Builder resultBuilder = GameMsgProtocol.UserMoveToResult.newBuilder();
        resultBuilder.setMoveUserId(userId);
        resultBuilder.setMoveFromPosX(cmd.getMoveFromPosX());
        resultBuilder.setMoveFromPosY(cmd.getMoveFromPosY());
        resultBuilder.setMoveToPosX(cmd.getMoveToPosX());
        resultBuilder.setMoveToPosY(cmd.getMoveToPosY());
        resultBuilder.setMoveStartTime(nowTime);

        GameMsgProtocol.UserMoveToResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }
}
