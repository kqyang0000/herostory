package org.tinygame.herostory.rank;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.async.AsyncOperationProcessor;
import org.tinygame.herostory.async.IAsyncOperation;
import org.tinygame.herostory.util.RedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * 排行榜服务
 */
public final class RankService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RankService.class);

    static private final RankService _instance = new RankService();

    private RankService() {
    }

    /**
     * 获取单例对象
     *
     * @return
     */
    static public RankService getInstance() {
        return _instance;
    }

    /**
     * 获取排行榜
     *
     * @param callback 回调函数
     */
    public void getRank(Function<List<RankItem>, Void> callback) {
        if (callback == null) {
            return;
        }

        AsyncOperationProcessor.getInstance().process(new AsyncGetRank() {
            @Override
            public void doFinish() {
                callback.apply(this.getRankItemList());
            }
        });
    }

    /**
     * 刷新排行榜
     *
     * @param winnerId
     * @param loserId
     */
    public void refreshRank(int winnerId, int loserId) {
        if (winnerId <= 0 || loserId <= 0) {
            return;
        }

        try (Jedis jedis = RedisUtil.getJedis()) {
            jedis.hincrBy("User_" + winnerId, "Win", 1);
            jedis.hincrBy("User_" + loserId, "Lose", 1);

            String winStr = jedis.hget("User_" + winnerId, "Win");
            int winNum = Integer.parseInt(winStr);

            jedis.zadd("Rank", winNum, String.valueOf(winnerId));

        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * 异步方式获取排行榜
     */
    static private class AsyncGetRank implements IAsyncOperation {
        private List<RankItem> _rankItemList;

        public List<RankItem> getRankItemList() {
            return _rankItemList;
        }

        @Override
        public void doAsync() {
            try (Jedis jedis = RedisUtil.getJedis()) {
                Set<Tuple> valSet = jedis.zrevrangeWithScores("Rank", 0, 9);

                int i = 0;
                List<RankItem> rankItemList = new LinkedList<>();

                for (Tuple t : valSet) {
                    if (t == null) {
                        continue;
                    }

                    //获取用户id
                    int userId = Integer.parseInt(t.getElement());
                    //获取用户信息
                    String jsonStr = jedis.hget("User_" + userId, "BasicInfo");

                    if (jsonStr == null) {
                        continue;
                    }

                    RankItem newItem = new RankItem();
                    newItem.setUserId(userId);
                    newItem.setRankId(++i);
                    newItem.setWin((int) t.getScore());

                    JSONObject jsonObj = JSONObject.parseObject(jsonStr);
                    newItem.setUserName(jsonObj.getString("userName"));
                    newItem.setHeroAvatar(jsonObj.getString("heroAvatar"));

                    rankItemList.add(newItem);
                }

                _rankItemList = rankItemList;
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }
}
