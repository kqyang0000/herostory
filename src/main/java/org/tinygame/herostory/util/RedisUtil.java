package org.tinygame.herostory.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public final class RedisUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisUtil.class);

    static private JedisPool _jedisPool = null;

    private RedisUtil() {
    }

    /**
     * 连接池初始化
     */
    static public void init() {
        try {
            _jedisPool = new JedisPool("62.234.136.79", 26379);
            LOGGER.info("Redis 连接成功");
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /**
     * 获取 Jedis 实例
     *
     * @return
     */
    static public Jedis getJedis() {
        if (_jedisPool == null) {
            throw new RuntimeException("_jedisPool 尚未初始化");
        }

        return _jedisPool.getResource();
    }

}
