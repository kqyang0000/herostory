package org.tinygame.herostory.mq;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息队列生产者
 */
public final class MqProducer {
    static private final Logger logger = LoggerFactory.getLogger(MqProducer.class);

    static private DefaultMQProducer _producer = null;

    private MqProducer() {
    }

    /**
     * 初始化
     */
    static public void init() {
        try {
            DefaultMQProducer producer = new DefaultMQProducer("herostory");
            producer.setNamesrvAddr("62.234.136.79:9876");
            producer.start();
            producer.setRetryTimesWhenSendAsyncFailed(3);

            _producer = producer;

            logger.info("消息队列连接成功");
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * 发送消息
     *
     * @param topic
     * @param msg
     */
    static public void sendMsg(String topic, Object msg) {
        if (topic == null || msg == null) {
            return;
        }

        Message newMsg = new Message();
        newMsg.setTopic(topic);
        newMsg.setBody(JSONObject.toJSONBytes(msg));

        try {
            _producer.send(newMsg);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

    }
}
