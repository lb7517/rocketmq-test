package com.lb.rocketmq.producer;

//import com.cn21.mec.oms.invoke.consumer.common.consts.Constans;
//import com.cn21.mec.oms.invoke.consumer.feign.TCFeignClient;
//import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSONObject;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.RPCHook;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lb
 * @version 1.0.0
 * @ClassName InvokeConsumer.java
 * @Description 鉴权消费者
 * @createTime 2019年05月27日 18:16:00
 */
@Component
public class InvokeProducer implements InitializingBean, DisposableBean {

    Logger log = LoggerFactory.getLogger(com.lb.rocketmq.consumer.InvokeConsumer.class);

    @Value("${rocketmq.namesrvAddr}")
    private String namesrvAddr;

    @Value("${rocketmq.consumerGroup}")
    private String consumerGroup;

    @Value("${rocketmq.producerGroup}")
    private String producerGroup;

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


    private DefaultMQProducer producer;


    static RPCHook getAclRPCHook() {
        return new AclClientRPCHook(new SessionCredentials(accessKey,secretKey));
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        // todo
        //创建一个消息生产者，并设置一个消息生产者组
        producer = new DefaultMQProducer(producerGroup, getAclRPCHook());

        //指定 NameServer 地址
        producer.setNamesrvAddr(namesrvAddr);

        //初始化 Producer，整个应用生命周期内只需要初始化一次
        producer.start();
        Map map1 = new HashMap();
        map1.put("startTimestamp", "1587742007");
        map1.put("endTimestamp", "1587742307");
        map1.put("nodeId", 10003);
        map1.put("flowType", 2);
        map1.put("caller", "1111");
        map1.put("receiver", "2222");
        map1.put("size", 575);
        List list = new ArrayList();
        list.add(map1);
        Map map = new HashMap();
        map.put("dataType", "2");
        map.put("requestNo", "34322856562760089610003");
        map.put("data", list);
        for (int i = 0; i < 1; i++) {
            //创建一条消息对象，指定其主题、标签和消息内容
            Message msg = new Message(
                    topic /* 消息主题名 */,
                    "TagA" /* 消息标签 */,
//                    ("Hello Java demo RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* 消息内容 */
                    (JSONObject.toJSONString(map)).getBytes(RemotingHelper.DEFAULT_CHARSET) /* 消息内容 */
            );
//            map.put("countB", i);
//            Message msg1 = new Message(
//                    topic /* 消息主题名 */,
//                    "TagB" /* 消息标签 */,
////                    ("Hello Java demo RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* 消息内容 */
//                    (JSONObject.toJSONString(map)).getBytes(RemotingHelper.DEFAULT_CHARSET) /* 消息内容 */
//            );

            //发送消息并返回结果
            SendResult sendResult = producer.send(msg);
//            SendResult sendResult1 = producer.send(msg1);

            System.out.printf("%s%n", sendResult);
//            System.out.printf("111%s%n", sendResult1);
        }

        // 一旦生产者实例不再被使用则将其关闭，包括清理资源，关闭网络连接等
//        producer.shutdown();

    }

    @Override
    public void destroy() {
        if (producer != null) {
            // 一旦生产者实例不再被使用则将其关闭，包括清理资源，关闭网络连接等
            producer.shutdown();
            log.info("InvokeProducer shutdown...[{}]", producer.getInstanceName());
        }
    }
}
