package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.Broadcaster;
import org.tinygame.herostory.model.User;
import org.tinygame.herostory.model.UserManager;
import org.tinygame.herostory.mq.MqProducer;
import org.tinygame.herostory.mq.VictorMsg;
import org.tinygame.herostory.msg.GameMsgProtocol;

public class UserAttkCmdHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd> {
    static private final Logger LOGGER = LoggerFactory.getLogger(UserAttkCmdHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd cmd) {
        if (ctx == null || cmd == null) {
            return;
        }

        //获取攻击用户id
        Integer attkUserId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();

        if (attkUserId == null) {
            return;
        }

        //获取目标用户id
        int targetUserId = cmd.getTargetUserId();
        //获取目标用户
        User targetUser = UserManager.getByUserId(targetUserId);

        if (targetUser == null) {
            broadcastAttkResult(attkUserId, -1);
            return;
        }

        LOGGER.info("当前线程 = {}", Thread.currentThread().getName());


        final int dmgPoint = 10;
        targetUser.setCurHp(targetUser.getCurHp() - dmgPoint);

        //广播攻击效果
        broadcastAttkResult(attkUserId, targetUserId);
        //广播减血结果
        broadcastSubtractHpResult(targetUserId, dmgPoint);
        //广播死亡结果
        if (targetUser.getCurHp() <= 0) {
            broadcastDieResult(targetUserId);

            VictorMsg newMsg = new VictorMsg();
            newMsg.setWinnerId(attkUserId);
            newMsg.setLoserId(targetUserId);

            MqProducer.sendMsg("herostory_victor", newMsg);
        }
    }

    /**
     * 广播攻击结果
     *
     * @param attkUserId
     * @param targetUserId
     */
    static private void broadcastAttkResult(int attkUserId, int targetUserId) {
        if (attkUserId <= 0) {
            return;
        }

        GameMsgProtocol.UserAttkResult.Builder resultBuilder = GameMsgProtocol.UserAttkResult.newBuilder();
        resultBuilder.setAttkUserId(attkUserId);
        resultBuilder.setTargetUserId(targetUserId);

        GameMsgProtocol.UserAttkResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);
    }

    /**
     * 广播减雪结果
     *
     * @param targetUserId
     * @param subtractHp
     */
    static private void broadcastSubtractHpResult(int targetUserId, int subtractHp) {
        if (targetUserId <= 0 || subtractHp <= 0) {
            return;
        }

        GameMsgProtocol.UserSubtractHpResult.Builder resultBuilder = GameMsgProtocol.UserSubtractHpResult.newBuilder();
        resultBuilder.setTargetUserId(targetUserId);
        resultBuilder.setSubtractHp(subtractHp);

        GameMsgProtocol.UserSubtractHpResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);

    }

    /**
     * 广播死亡结果
     *
     * @param targetUserId
     */
    static private void broadcastDieResult(int targetUserId) {
        if (targetUserId <= 0) {
            return;
        }

        GameMsgProtocol.UserDieResult.Builder resultBuilder = GameMsgProtocol.UserDieResult.newBuilder();
        resultBuilder.setTargetUserId(targetUserId);

        GameMsgProtocol.UserDieResult newResult = resultBuilder.build();
        Broadcaster.broadcast(newResult);

    }
}
