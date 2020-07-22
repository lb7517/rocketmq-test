package com.lb.rocketmq.consumer;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.rebalance.AllocateMessageQueueAveragely;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.RPCHook;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author lb
 * @version 1.0.0
 * @ClassName InvokeConsumer.java
 * @Description 鉴权消费者
 * @createTime 2019年05月27日 18:16:00
 */
//@Component
//@Slf4j
public class InvokeConsumer implements InitializingBean, DisposableBean {

//    @Autowired
//    private TCFeignClient tcFeignClient;

    Logger log = LoggerFactory.getLogger(InvokeConsumer.class);

    @Value("${rocketmq.namesrvAddr}")
    private String namesrvAddr;

    @Value("${rocketmq.consumerGroup}")
    private String consumerGroup;

    @Value("${rocketmq.topic}")
    private String topic;

    private static String accessKey;

    private static String secretKey;

    @Value("${rocketmq.accessKey}")
    public void setAccessKey(String access_key) {
        accessKey = access_key;
    }

    @Value("${rocketmq.secretKey}")
    public void setSecretKey(String secret_key) {
        secretKey = secret_key;
    }

    private DefaultMQPushConsumer consumer;

    static RPCHook getAclRPCHook() {
        return new AclClientRPCHook(new SessionCredentials(accessKey, secretKey));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            consumer = new DefaultMQPushConsumer(consumerGroup, getAclRPCHook(), new AllocateMessageQueueAveragely());
            consumer.setNamesrvAddr(namesrvAddr);
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            //consumer.setUseTLS(true);
            // 设置consumer所订阅的Topic和Tag，*代表全部的Tag
            consumer.subscribe(topic, "*");
            consume();
            consumer.start();
            log.info("InvokeConsumer start...[{}]", consumer.getInstanceName());
        } catch (MQClientException e) {
            log.error("InvokeConsumer start failed...[{}-{}]", consumer.getInstanceName(), e.getErrorMessage(), e);
        } catch (Exception e) {
            log.error("InvokeConsumer start exception", e);
        }
    }

    public void consume() {
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {

                try {
                    for (MessageExt messageExt : list) {
                        String tag = messageExt.getTags();
                        log.info("1111111  InvokeConsumer consumeing tag: "+tag);
                        String strBody = new String(messageExt.getBody(), RemotingHelper.DEFAULT_CHARSET);
                        Map map = JSONObject.parseObject(strBody);
                        log.info("InvokeConsumer consumeing...[msgBody:{}]",strBody);
                    }
                } catch (Exception e) {
                    log.error("InvokeConsumer Exception...[listMsg:{}]", list, e);
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                log.info("InvokeConsumer consumeing succeed");
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
    }

    @Override
    public void destroy() {
        if (consumer != null) {
            consumer.shutdown();
            log.info("InvokeConsumer shutdown...[{}]", consumer.getInstanceName());
        }
    }
}
