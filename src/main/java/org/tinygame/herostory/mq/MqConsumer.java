package org.tinygame.herostory.mq;

import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.rank.RankService;

import java.util.List;

public final class MqConsumer {
    static private final Logger logger = LoggerFactory.getLogger(MqConsumer.class);

    private MqConsumer() {
    }

    static public void init() {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("herostory");
        consumer.setNamesrvAddr("62.234.136.79:9876");

        try {
            consumer.subscribe("herostory_victor", "*");

            consumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgExtList, ConsumeConcurrentlyContext ctx) {

                    for (MessageExt msgExt : msgExtList) {
                        VictorMsg victorMsg = JSONObject.parseObject(msgExt.getBody(), VictorMsg.class);

                        logger.info("从消息队列收到胜利消息, winnerId = {}, loserId = {}", victorMsg.getWinnerId(), victorMsg.getLoserId());

                        RankService.getInstance().refreshRank(victorMsg.getWinnerId(), victorMsg.getLoserId());
                    }

                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });

            consumer.start();

            logger.info("消息队列 （消费者）连接成功");
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }


    }
}
