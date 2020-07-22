## rokcetmq使用acl启动过程(window下执行), rocketmq解压直接用
----------
1. 启动nameserver,
start mqnamesrv.cmd
2. 启动broker数据存储服务，注意-c 后面的配置
start mqbroker.cmd -n 127.0.0.1:9876 -c ../conf/broker.conf

注意:
注意:
1. 使用acl注意修改E:\soft\rocketMq\rocketmq-all-4.7.0-bin-release\conf文件下的broker.conf和plain_acl.yml文件
2. 如报org.apache.rocketmq.acl.common.AclException: [10015:signature-faile异常:
解决方案: 把jdk下的E:\soft\Java\jdk1.8.0_211\jre\lib\ext中sunjce_provider.jar包拷贝到E:\soft\rocketMq\rocketmq-all-4.7.0-bin-release\lib下面