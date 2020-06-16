package org.tinygame.herostory.cmdhandler;

import io.netty.channel.ChannelHandlerContext;
import org.tinygame.herostory.msg.GameMsgProtocol;
import org.tinygame.herostory.rank.RankItem;
import org.tinygame.herostory.rank.RankService;

import java.util.Collections;

public class GetRankCmdHandler implements ICmdHandler<GameMsgProtocol.GetRankCmd> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.GetRankCmd cmd) {
        if (ctx == null || cmd == null) {
            return;
        }

        RankService.getInstance().getRank((rankItems) -> {
            if (rankItems == null) {
                rankItems = Collections.emptyList();
            }

            GameMsgProtocol.GetRankResult.Builder resultBuilder = GameMsgProtocol.GetRankResult.newBuilder();

            for (RankItem rankItem : rankItems) {
                if (rankItem == null) {
                    continue;
                }

                GameMsgProtocol.GetRankResult.RankItem.Builder rankItemBuilder = GameMsgProtocol.GetRankResult.RankItem.newBuilder();
                rankItemBuilder.setUserId(rankItem.getUserId());
                rankItemBuilder.setUserName(rankItem.getUserName());
                rankItemBuilder.setRankId(rankItem.getRankId());
                rankItemBuilder.setHeroAvatar(rankItem.getHeroAvatar());
                rankItemBuilder.setWin(rankItem.getWin());

                resultBuilder.addRankItem(rankItemBuilder);
            }

            GameMsgProtocol.GetRankResult newResult = resultBuilder.build();
            ctx.writeAndFlush(newResult);

            return null;
        });
    }
}
