

#docker

##安装配置

> Ubuntu安装

```sh
$ uname -r                     #内核版本必须是3.10及以上
$ apt-get install docker.io    #安装Docker -(可能存在权限错误,使用时添加 sudo 前缀)
$ service docker status/start  #启动服务和守护进程
$ docker -v                    #检测是否安装成功
$ ln -sf /usr/bin/docker.io /usr/local/bin/docker #创建软连接（方便使用docker命令）
```
```sh
#权限问题：permission denied. Are you trying to connect to a TLS-enabled daemon without TLS?
#注意: 默认情况,执行 docker 都需要运行 sudo 命令. 如何免去 sudo?

$ sudo groupadd docker            #如果还没有 docker group 就添加一个
$ sudo gpasswd -a ${USER} docker  #将用户加入该 group 内.然后退出并重新登录就生效啦
$ sudo service docker restart     #重启 docker 服务
$ newgrp - docker                 #切换当前会话到新 group
```

> CentOS-6.9安装

```sh
$ cat /etc/issue    #查看发行版信息：CentOS release 6.9 (Final)
$ cat /proc/version #查看正在运行的内核版本：...Red Hat 4.4.7-18...
$ uname -r          #docker要求内核版本3.10以上

$ yum install docker
$ systemctl start/restart docker
$ docker -v                #docker版本
$ systemctl enable docker  #开机启动
```

```sh
#可能出现问题：Segmentation Fault or Critical Error encountered. Dumping core and aborting.
yum list installed |grep docker #找到安装的docker
yum -y remove docker.x86_64     #然后进行卸载后重新安装
```
## 相关指令

> 基础指令

```sh
service docker status|stop|start|restart
docker -v
docker info
```

>镜像相关

```sh
docker search mysql
docker pull mysql:5.6.7
docker pull registry.docker-cn.com/library/mysql:5.6.7 #官方加速

docker images [-q]                   #-q: 只显示id
docker rmi [-f] IMAGE_ID             #删除某个
docker rmi $(docker image -q)        #删除所有

docker inspect IMAGE_ID              #相关信息
docker tag IMAGE_ID NEW_NAME:NEW_TAG #拷贝重命名
```

>容器相关

```sh
docker ps [-a]                           #运行中的容器（-a: 所有）
docker start|stop|restart CONTAINER_NAME #容器的启动，停止，重启
docker rm CONTAINER_NAME                 #移除容器（停止状态） rm -> 移除容器; rmi -> 移除镜像！

docker top CONTAINER_NAME      #容器内进程
docker inspect CONTAINER_NAME  #容器相关信息
docker logs [-t] [--tail 10] CONTAINER_NAME    #容器日志(-t: 显示时间, --tail: 最新10条)

#动态的更新一个或多个容器的配置
docker update --restart=always mysql #'restart'退出容器时，总是重启
```

>拷贝文件

```sh
docker exec -it CONTAINER_NAME /bin/bash     #进入容器.(exit: 退出)
docker cp CONTAINER_NAME:SRC_PATH DEST_PATH  #拷出来
docker cp SRC_PATH CONTAINER_NAME:DEST_PATH  #拷进去
```

##配置容器

```shell
--name #为容器指定一个名称：--name ES01
-d     #后台运行容器，并返回容器ID
-e     #设置环境变量：-e ES_JAVA_OPTS="-Xms256m -Xmx256m"    
-p     #端口映射（宿主机:容器） -p 9200:9200

-it    #配合 exec 使用，开启一个交互模式的终端
-v     #挂载宿主机的目录作为配置文件（宿主机目录:容器目录）：-v /conf/mysql:/etc/mysql/conf.d

--restart #三种重启策略：
          #no：容器退出时，不重启容器
          #on-failure n：重新启动容器的最大次数n
          #always：无论退出状态是如何，都重启容器。如若开机自启时，配置此项
```
> tomcat

```shell
#tomcat：最后一个参数是 镜像名:版本号(latest可省)
docker run --name tomcat01 -d -p 9090:8080 tomcat:8.5-jre8-alpine

#-v 将宿主主机目录和容器目录建立映射关系，冒号前为宿主主机目录，冒号后为容器对应目录
docker run --name tomcat -d -p 80:8080 -v /var/lib/cloudService/webapps:/usr/local/tomcat/webapps tomcat
```
> mysql

```shell
#配置 root 密码
docker run --name mysql01 -d -p 33066:3306 -e MYSQL_ROOT_PASSWORD=123456 mysql

#配置其他参数
docker run --name mysql02 -d -e MYSQL_ROOT_PASSWORD=123456 mysql:tag \ 
--character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

#将上述配置参数保存到宿主机文件'/conf/mysql'，启动时加载宿主机的配置文件。以后，可通过修改宿主机的配置文件来配置mysql
docker run --name mysql02 -d -e MYSQL_ROOT_PASSWORD=123456 mysql:tag -v /conf/mysql:/etc/mysql/conf.d
```

```shell
#对于 mysql-8.0.4 之后版本，不能简单的通过 '-e MYSQL_ROOT_PASSWORD=123456' 来指定root密码
docker exec -it 1457d60b0375  /bin/bash
mysql -u root -p  #进入docker-mysql
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '123456'; #修改root密码
exit              #执行两次，依次退出 docker-mysql 和 docker。
```
> rabbitmq

```sh
#4369：erlang发现；5672：client通信；15672：UI管理界面；25672：server间内部通信    
docker run --name rabbitmq -d -p 4369:4369 -p 5671:5671 -p 5672:5672 -p 15671:15671 \
-p 15672:15672 -p 15674:15674 -p 25672:25672 rabbitmq
```

> redis

```shell
docker run --name redis -d -p 6379:6379 redis
```
> elasticsearch

```shell
#指定端口（web通信端口9200，分布式集群节点间通信9300），内存大小
docker run --name ES01 -d -p 9200:9200 -p 9300:9300 -e ES_JAVA_OPTS="-Xms256m -Xmx256m" elasticsearch

#将 IK 插件解压到宿主机，然后配置docker容器加载宿主机 /plugins 目录
docker run --name ES02 -d -p 9201:9200 -p 9301:9300  -e ES_JAVA_OPTS="-Xms256m -Xmx256m" \
-v /var/tmp/plugins:/usr/share/elasticsearch/plugins elasticsearch
```
## 构建镜像

> commit：通过已有的容器，打包成新的镜像

```shell
#-a：作者相关，-m：描述信息，mysql01：已有容器，skyl/mysql：新镜像
docker commit -a 'skyl' -m 'hello skyl' mysql01 skyl/mysql

#使用新镜像
docker run --name skyl-mysql -d -e MYSQL_ROOT_PASSWORD=123456 mysql
```

> Dockerfile：一系列命令和参数构成的脚本，这些命令应用于基础镜像并最终创建一个新的镜像（只是镜像，而非容器）。

```shell
#依赖镜像名称和ID。基础镜像，必须写在第一行
FROM centos:7
#指定镜像创建者信息
MAINTAINER SKYL
#切换工作目录
WORKDIR /usr
#创建容器的文件夹
RUN mkdir /usr/local/java
#把jdk添加到容器中，ADD：复制+解压
ADD jdk-8u171-linux-x64.tar.gz /usr/local/java/
#配置jdk环境变量
ENV JAVA_HOME /usr/local/java/jdk1.8.0_171
ENV JRE_HOME $JAVA_HOME/jre
ENV CLASSPATH $JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JRE_HOME/lib:$CLASSPATH
ENV PATH $JAVA_HOME/bin:$PATH
```

```shell
#切换到 dockerfile 所在目录，执行命令。最后一个点，表示 dockerfile 文件在当前目录
docker build -t='skyl-jdk1.8' .
docker images   #查看镜像是否建立完成
docker run ‐it ‐‐name=myjdk8 skyl-jdk1.8 /bin/bash #构建容器jdk8
```

##常见问题

> 镜像加速

```shell
#默认配置文件：/etc/docker/daemon.json（没有，则新建）。然后重启docker
{"registry-mirrors": ["https://docker.mirrors.ustc.edu.cn"]}
```

```shell
#aliyun 加速
https://cr.console.aliyun.com/cn-hangzhou/mirrors
```

```shell
#daocloud 加速
#docker pull daocloud.io/library/logstash
https://hub.daocloud.io/
```

```shell
#直接设置 –registry-mirror 参数，仅对当前的命令有效 
docker run hello-world --registry-mirror=https://docker.mirrors.ustc.edu.cn

#修改 /etc/default/docker，加入 DOCKER_OPTS=”镜像地址”，可以有多个
DOCKER_OPTS="--registry-mirror=https://docker.mirrors.ustc.edu.cn"
```

```shell
#支持 systemctl 的系统,通过 sudo systemctl edit docker.service
#会生成 etc/systemd/system/docker.service.d/override.conf 覆盖默认的参数,在该文件中加入如下内容
[Service] 
ExecStart= 
ExecStart=/usr/bin/docker -d -H fd:// --registry-mirror=https://docker.mirrors.ustc.edu.cn       
```
> 配置http

```shell
#FATA[0010] Error response from daemon: v1 ping attempt failed with error: Get https://registry.docker-cn.com/v1/_ping: dial tcp

#在最新的docker1.3.3中 无法pull，因为默认的是https。在'/etc/default/docker'中追加：
DOCKER_OPTS="--insecure-registry juandapc:5000"
```

> 重启报错：使用 sudo service docker restart

```shell
使用命令'service docker restart'重启 docker 报错。
#stop: Rejected send message, 1 matched rules; type="method_call", sender=":1.430" (uid=117 pid=28917 comm="stop docker ") 
```



#RabbitMQ

## 基础配置

> BOOT

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

```properties
# rabbitmq
spring.rabbitmq.host=192.168.5.23
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

# listener
spring.rabbitmq.listener.simple.prefetch:0 消费者每次从队列获取消息的数量（默认0，无限制）。设置过大，且消息处理耗时，则等待消息就处理不及时
spring.rabbitmq.listener.simple.concurrency: 最小的消费者数量
spring.rabbitmq.listener.simple.max-concurrency: 最大的消费者数量
spring.rabbitmq.listener.simple.acknowledge-mode: 表示消息确认方式，其有三种配置方式，分别是 none、manual和auto；默认 auto

spring.rabbitmq.listener.simple.auto-startup: 是否启动时自动启动容器
spring.rabbitmq.listener.simple.transaction-size: 指定一个事务处理的消息数量，最好是小于等于prefetch的数量.
spring.rabbitmq.listener.simple.default-requeue-rejected: 决定被拒绝的消息是否重新入队；默认是true（与参数acknowledge-mode有关系）
spring.rabbitmq.listener.simple.idle-event-interval: 多少长时间发布空闲容器时间，单位毫秒

spring.rabbitmq.listener.simple.retry.enabled: 监听重试是否可用
spring.rabbitmq.listener.simple.retry.max-attempts: 最大重试次数
spring.rabbitmq.listener.simple.retry.initial-interval: 第一次和第二次尝试发布或传递消息之间的间隔
spring.rabbitmq.listener.simple.retry.multiplier: 应用于上一重试间隔的乘数
spring.rabbitmq.listener.simple.retry.max-interval: 最大重试时间间隔
spring.rabbitmq.listener.simple.retry.stateless: 重试是有状态or无状态

# template
spring.rabbitmq.template.mandatory: 启用强制信息；默认false
spring.rabbitmq.template.receive-timeout: receive() 操作的超时时间
spring.rabbitmq.template.reply-timeout: sendAndReceive() 操作的超时时间
spring.rabbitmq.template.retry.enabled: 发送重试是否可用
spring.rabbitmq.template.retry.max-attempts: 最大重试次数
spring.rabbitmq.template.retry.initial-interval: 第一次和第二次尝试发布或传递消息之间的间隔
spring.rabbitmq.template.retry.multiplier: 应用于上一重试间隔的乘数
spring.rabbitmq.template.retry.max-interval: 最大重试时间间隔
```

>RabbitMQ 启用 HTTP 后台认证

```sh
https://blog.csdn.net/isea533/article/details/85096253
```



## 基本使用

> **序列化**：默认以java序列化，现配置json序列化

```java
@EnableRabbit //rabbitmq-全局注解
@Configuration
public class AppConfig {
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter(); //对于传递对象，可配置此项。对于传递JSON字符串，则不用配置
    }
}
```

> **队列** の创建和删除

```java
/**
 * @param name        队列名称
 * @param durable     Rabbitmq服务重启，队列是否还存在？ 默认 true
 * @param autoDelete  当所有消费客户端连接断开后，是否自动删除队列？ 默认 false
 * @param exclusive   是否排外及私有。默认 false
 *        true： 私有队列，其他通道channel不能访问，强制访问抛异常。并且该channel的conn断开后，队列将自动删除包括里面的msg
 * @param arguments   额外参数。
 */
Queue queue = new Queue("queue.expires", true, false, false, args);

String queueName = amqpAdmin.declareQueue(queue); //创建队列，返回队列名
boolean deleteQueue = amqpAdmin.deleteQueue("queue.expires.999"); //删除队列
```

> **队列** 其他属性

```java
Map<String, Object> args = new HashMap<>();
args.put("x-expires", 3 * 60 * 1000);     //3分钟内，没有消费者消费 msg，则自动删除该队列
args.put("x-message-ttl", 5 * 60 * 1000); //队列中 msg 被丢弃前，能够存活的时间
args.put("x-max-length", 5);              //队列的最大长度。超过最大长度，后面的消息会顶替前面的
args.put("x-max-length-bytes", 5);        //队列的最大容量。作用同上，但这个是靠队列大小（bytes）来达到限制
args.put("x-max-priority", 5);            //队列的优先级。建议使用1到10之间，表示队列应该支持的最大优先级。
args.put("x-queue-mode", "lazy"); //取值范围：default和lazy。lazy：先将消息保存到磁盘上，不放在内存中，当消费者开始消费的时候才加载到内存中
Queue queue = new Queue("queue.expires", true, false, false, args);
```

> **交换器** の创建和删除

```java
/**
 * @param name       交换器名称
 * @param durable    Rabbitmq重启是否还存在？ 默认 true
 * @param autoDelete 当所有绑定队列都不在使用时，是否自动删除交换器。默认 false
 */
FanoutExchange exchange = new FanoutExchange("exchange.fanout", true, false);

amqpAdmin.declareExchange(exchange); //创建交换器
boolean deleteExchange = amqpAdmin.deleteExchange("exchange.fanout"); //删除交换器
```

> **绑定关系**の创建和删除

```java
/**
 * @param destination     目的地，队列名 或 交换器名
 * @param destinationType 目的地类型（可选值 QUEUE，EXCHANGE）
 * @param exchange        交换器
 * @param routingKey      路由键
 * @param arguments       额外参数
 */
Binding binding = new Binding("queue.admin.0", Binding.DestinationType.QUEUE, "exchange.admin", "admin.#", null);

amqpAdmin.declareBinding(binding); //声明绑定关系（队列，交换器，路由键）
amqpAdmin.removeBinding(binding); //删除绑定关系
```

> **发送消息**の两种方式

```java
//msg需要自己构造，自定义消息头和消息体
amqpTemplate.send("exchange", "routing-key", new Message("".getBytes(), null));

//【推荐】只需传入要发送的对象 obj，系统自动将其当成消息体，并自动序列化。
amqpTemplate.convertAndSend("exchange", "routing-key", new Object());
```

## 三种模式

```sh
`Fanout`：任何发送到Exchange的消息，都会路由到与其绑定的'所有Queue'中。#Queue与Exchange 直接相连，不需要 RoutingKey
Queue与Exchange 的绑定是：多对多的关系。一个E上可以绑定多个Q，一个Q可以同多个E进行绑定
```

```sh
`Direct`：任何发送到 E 的消息，都会被转发到'RK一致'的 Q 中。#Q与E 通过 RK 相连，RK需要完全匹配
如，绑定的RK为"rk.info"，则只会转发标记为"rk.info"的消息，不会转发"rk.debug"，"rk.info.log"等
```

```sh
`Topic`：任何发送到 E 的消息，都会被转发到'RK模糊匹配'的 Q 中。#Q与E 通过 RK 相连，RK支持模糊匹配
模糊匹配规则："#"表示0个或若干个关键词，"*"表示1个关键词
所以，如"rk.*"能与"rk.info"匹配，无法与"rk.info.log"匹配；但是"rk.#"能与上述两者匹配。

RK='quick.orange.rabbit'的消息会同时路由到 Q1 + Q2，
RK='lazy.orange.fox'的消息会路由到 Q1 + Q2，
RK='lazy.brown.fox'的消息会路由到 Q2，
RK='lazy.pink.rabbit'的消息会路由到 Q2（只会投递给Q2一次，虽然这个 RK 与Q2的两个 Binding-Key 都匹配）
RK='quick.brown.fox'，'orange'，'quick.orange.male.rabbit'的消息将会被丢弃，因为它们没有匹配任何 Binding-Key

`Fanout 多个消费者，都能收到消息。Topic 是特殊的 Direct，多个同类型消费者，交替收到消息。`
```

![](assets/rabbitmq-01.png)

> ### Fanout：广播

```java
@Test
public void fanoutProviderTest() {
    String exchange = "exchange.fanout";
    String routingKey = "";
    for (int i = 0; i < 10; i++) {
        String msg = "msg-fanout-" + i;
        amqpTemplate.convertAndSend(exchange, routingKey, msg);
    }
}
```

```java
@Slf4j
@Component
public class FanoutConsumer {

    // value = @Queue(value = "${queue.fanout.info}"), //可以使用 ${} 直接从配置文件读取
    @RabbitListener(bindings = {@QueueBinding(
        value = @Queue(value = "queue.fanout.info"),
        exchange = @Exchange(value = "exchange.fanout", type = ExchangeTypes.FANOUT),
        key = "" //广播模式，路由键不需要指定。每个Queue都能收到 10 条消息
    )})
    @RabbitHandler
    public void fanoutInfoRecv(String msg) {
        log.info("FANOUT-INFO  recv: {}", msg);
    }

    @RabbitListener(bindings = {@QueueBinding(
        value = @Queue(value = "queue.fanout.error"),
        exchange = @Exchange(value = "exchange.fanout", type = ExchangeTypes.FANOUT),
        key = ""
    )})
    @RabbitHandler
    public void fanoutErrorRecv(String msg) {
        log.info("FANOUT-ERROR recv: {}", msg);
    }
}
```

> ### Direct：发布与订阅

```java
@Test
public void directProviderTest() {
    String exchange = "exchange.direct";

    for (int i = 0; i < 10; i++) {
        String routingKey = "rk.debug";
        String msg = "msg-rk-debug-" + i;
        amqpTemplate.convertAndSend(exchange, routingKey, msg); //msg-01

        routingKey = "rk.info";
        msg = "msg-rk-info-" + i;
        amqpTemplate.convertAndSend(exchange, routingKey, msg); //msg-02
    }
}
```

```java
@Slf4j
@Component
public class DirectConsumer {

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = "queue.direct.info"),
        exchange = @Exchange(value = "exchange.direct", type = ExchangeTypes.DIRECT),
        key = "rk.info" //只能接收 rk 完全匹配的 msg-02。并且两个接收端【交替】接收到 msg-02
    ))
    @RabbitHandler
    public void directInfoRecv(String msg) {
        log.info("DIRECT-INFO recv-1: {}", msg);
    }

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = "queue.direct.info"),
        exchange = @Exchange(value = "exchange.direct", type = ExchangeTypes.DIRECT),
        key = "rk.info"
    ))
    @RabbitHandler
    public void directInfoRecv0(String msg) {
        log.info("DIRECT-INFO recv-2: {}", msg);
    }
}
```

> ### Topic：主题

```java
@Test
public void topicProviderTest() {
    String exchange = "exchange.topic";

    for (int i = 0; i < 10; i++) {
        String routingKey = "rk.debug";
        String msg = "msg-rk-debug-" + i;
        amqpTemplate.convertAndSend(exchange, routingKey, msg); //msg-01

        routingKey = "rk.info";
        msg = "msg-rk-info-" + i;
        amqpTemplate.convertAndSend(exchange, routingKey, msg); //msg-02

        routingKey = "rk.info.log";
        msg = "msg-rk-info-log-" + i;
        amqpTemplate.convertAndSend(exchange, routingKey, msg); //msg-03
    }
}
```

```java
@Slf4j
@Component
public class TopicConsumer {

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = "queue.topic.info"),
        exchange = @Exchange(value = "exchange.topic", type = ExchangeTypes.TOPIC),
        key = "rk.*" //*：匹配 1 个关键字。所以，只能接收 msg-01、msg-02。若有多个接收端，也是【交替】接收消息
    ))
    @RabbitHandler
    public void recv(String msg) {
        log.info("TOPIC-INFO  recv: {}", msg);
    }
}
```

## Stomp接收

>启用 Web-STOMP 插件

```sh
rabbitmq-plugins enable rabbitmq_management  #开启插件 web-ui
http://localhost:15672/                      #登陆UI，默认用户名-密码都是: guest
```

```sh
docker exec -it rabbitmq /bin/bash           #进入容器
rabbitmq-plugins list                        #查看已启动的插件
rabbitmq-plugins enable rabbitmq_web_stomp   #开启插件 web-stomp
docker restart rabbitmq                      #重启容器
```

> guest 用户配置远程登录

```sh
#修改 /etc/rabbitmq/rabbitmq.conf
loopback_users.guest = false #false：远程访问；true：本地访问
```

> WebSocket 连接 RabbitMQ

```sh
#subscribe() 消息头可以指定的参数：
x-queue-name：指定队列名，默认随机生成，如 stomp-subscription-*****。   durable：持久化。   auto-delete：自动删除。   exclusive：独占。
其他还有：x-expires、x-message-ttl 等等，详见'基本使用的队列参数'

#云坐席要求：打开云坐席界面，能看到 5 分钟以内、未办理的异常消息。
#队列属性：
(1).'auto-delete': false. 当所有消费客户端连接断开后，是否自动删除队列?
删除队列，就不能接受交换器的消息。要保存 5 分钟以内的消息，就不能删除队列。
(2).'x-queue-name': 'queue.topic.info.web'. 每次连接系统都会默认生成一个新的队列名。
消费者断开以后，消息保存在旧的队列中，再次连接，系统生成新的队列名，在新的队列中肯定收不到旧的消息。所以，必须手动指定队列名
(3).'x-message-ttl': 5 * 60 * 1000. 队列中消息被丢弃前，能够存活的时间。
队列中保存 5 五分钟以内的消息
#未办理的消息，ACK？
Stomp默认是自动 ACK，消息发送成功，就会从队列中移除消息。改成手动 ACK 就可以。
```

```html
<body>
    <div id="show_msg"></div>

    <script src="../js/stomp.js"></script> <!-- 必须使用 stomp.js -->
    <script src="../js/jquery2.0.0min.js"></script>
    <script>
        let ws = new WebSocket('ws://47.103.68.238:15674/ws');
        let client = Stomp.over(ws);

        let on_connect = function () {
            // 参数依次为：目的地(/exchange/交换机名/路由键)，回调方法，消息头
            client.subscribe('/exchange/exchange.cloudseat/*.*.*.*.*', function (data) {
                console.log('msg: ' + data.body);
                $("#show_msg").prop("innerHTML", msg);
            }, {'x-queue-name': 'queue.topic.info.web', 'auto-delete': false, 'x-message-ttl': 10 * 60 * 1000});
        };

        let on_error = function () {
            console.log('error');
        };

        // 参数依次为：用户名，密码，连接成功，连接出错，虚拟主机名
        client.connect('guest', 'guest', on_connect, on_error, '/');

        // 关闭控制台信息。stomp.js会去检测debug是否是函数，不是函数就不会调用输出
        // client.debug = null;
    </script>
</body>
```

## 消息确认

<https://www.jianshu.com/p/2c5eebfd0e95>

> 消息发送确认

```properties
# 开启发送确认回调 & 路由失败回调
spring.rabbitmq.publisher-confirms=true
spring.rabbitmq.publisher-returns=true
# 对 rabbitmqTemplate 进行监听,当消息由于server的原因无法到达queue时，就会被监听到，以便执行 ReturnCallback() 方法
# 默认 false，Server端会自动删除不可达消息
spring.rabbitmq.template.mandatory=true
```

```java
@Test
public void rabbitSendTest() {
    String msg = "你好，现在是 " + LocalTime.now();

    // 确认消息是否成功发送到 Exchange 中
    // 消息唯一标识, 是否发送成功, 失败原因
    rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
        System.out.println("是否发送成功: " + (ack ? "成功" : "失败"));
    });

    // 消息没有从 Exchange 路由到 Queue 回调此方法（需配置文件开启）. 路由到则不会调用
    // 消息主体, 返回代码, 返回描述, 消息交换器, 消息路由键
    rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
        System.out.println("返回描述: " + replyText);
    });

    // 回调方法的定义，必须在发送消息之前
    rabbitTemplate.convertAndSend("exchange.topic", "rk1.info1", msg);
}
```

> 消息接收确认

```properties
# 开启接收ACK
spring.rabbitmq.listener.simple.acknowledge-mode=MANUAL
```

```sh
#消息确认有三种模式：NONE(自动确认). AUTO(根据情况确认). MANUAL(手动确认)
#NONE  ：自动确认。只要确认消息发送成功，无须等待应答就会丢弃消息。两个弊端：
(1).容易丢失消息.
(2).只要队列不空，RabbitMQ会源源不断的把消息推送给客户端，而不管客户端能否消费的完

#MANUAL：手动确认。一定要对消息做出应答，否则rabbit认为当前队列没有消费完成，将不再继续向该队列发送新的消息。
https://blog.csdn.net/youbl/article/details/80425959

#AUTO  ：根据情况确认。
(1). 如果消息成功被消费（成功的意思是在消费的过程中没有抛出异常），则自动确认
(2). 当抛出 AmqpRejectAndDontRequeueException 异常的时候，则消息会被拒绝，且 requeue = false（不重新入队列）
(3). 当抛出 ImmediateAcknowledgeAmqpException 异常，则消费者会被确认
(4). 其他的异常，则消息会被拒绝，且 requeue = true，此时会发生死循环，可以通过 setDefaultRequeueRejected（默认是true）去设置抛弃消息
```

```java
@Slf4j
@Component
public class TopicInfoConsumer {

    @RabbitHandler
    @RabbitListener(queues = "queue.topic.info")
    public void recv(@Payload String msg, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        log.info("【接收消息】TOPIC: " + msg);

        try {
            if (msg.startsWith("3")) {
                System.out.println("处理消息: " + msg);
                channel.basicNack(deliveryTag, false, false); //否定消息
                // channel.basicReject(deliveryTag, false); //拒绝消息
                // channel.basicRecover(false); //重新投递
                return;
            }
            channel.basicAck(deliveryTag, false); // 确认消息
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

> 方法参数

```sh
# channel.basicAck(deliveryTag, false); // 确认消息
'deliveryTag（唯一标识 ID）'：当一个消费者向 RabbitMQ 注册后，会建立起一个 Channel，RabbitMQ会用 basic.deliver() 方法向消费者推送消息，
这个方法携带了一个 deliveryTag，它代表了 RabbitMQ 向该 Channel 投递的这条消息的唯一标识 ID，是一个单调递增的正整数，deliveryTag 的范围仅限于 Channel.

'multiple'：为了减少网络流量，手动确认可以被批处理，当该参数为 true 时，则可以一次性确认 deliveryTag 小于等于传入值的所有消息。
设置 false 只确认当前这一条消息。
```

```sh
# channel.basicNack(deliveryTag, false, false); //否定消息
前两个参数同上，'第三个参数'表示：是否重新放回队列? true 重新入队, false 直接删除
当消息回滚到消息队列时，这条消息不会回到队列尾部，而是仍是在队列头部，这时消费者会又接收到这条消息，容易陷入死循环。
如果想让消息进入队尾，须确认消息后，再次发送消息。
```

```sh
# channel.basicReject(deliveryTag, false); //拒绝消息
第二个参数表示是否重新放回队列。拒绝消息后，如果配置了死信队列，则进入死信队列。
```

```sh
# channel.basicRecover(false); //重新投递
重新投递并没有 basicReject() 中的 deliveryTag 参数，所以，重新投递是将消费者还没有处理的所有的消息都重新放入到队列中，而不是将某一条消息放入到队列中。
与 basicReject() 不同的是，重新投递可以指定投递的消息是否允许当前消费者消费。
true：表示会被其他消费者消费. false：表示重新递送的消息还会被当前消费者消费
```

```sh
#channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, BasicProperties , message.getBytes()); //再次发送消息

```



> 相关问题

```sh
#(1).自动 ack 机制会导致消息丢失的问题
MQ只要确认消息发送成功，无须等待应答就会丢弃消息，这会导致客户端还未处理完时，出异常或断电了，导致消息丢失的后果。

#(2).启用 ack 机制后，没有及时ack导致的队列异常
先处理消息，处理完成后，再做 ACK 响应，失败就不做ack响应，这样消息会储存在MQ的 Unacked 消息里，不会丢失。
但是，如果 ACK 代码触发BUG，将会导致所有消息都抛出异常，然后队列的 Unacked 消息数暴涨，导致MQ响应越来越慢，甚至崩溃的问题。
原因是如果MQ没得到ack响应，这些消息会堆积在Unacked消息里，不会抛弃，直至客户端断开重连时，才变回ready。

#(3).启用 nack 机制后，导致的死循环
针对(2)存在的问题，改为：正常就ack，不正常就nack，并等下一次重新消费。
此时，如果 ACK 代码触发BUG，就会把消息塞回队列头部，下一步又消费这条会出异常的消息，又出错，塞回队列...进入死循环，当然也不会消费新的消息，导致堆积...
解决方案：所有消息都ack，记录日志，异常消息后续单独处理。
```

```sh
#启用 ack 机制和 prefetch 后，没有及时ack导致的队列堵塞。
开启 prefetch，当MQ的队列达到 5 条Unacked消息时，不会再推送消息给Consumer。
此时，如果 ACK 代码触发BUG，将导致无法继续处理后续的消息。
```





# 相关概念

## docker

> 相关概念

```sh
能够把应用程序自动部署到容器的开源引擎，轻量级容器技术！
'简化程序'：将软件做好配置依赖 --> 编译成镜像 --> 镜像发布 --> 其他使用者就可以直接使用这个镜像。
'简化部署'：传统做法先安装（包管理工具或者源码包编译），再配置和运行。Docker模式为复制镜像，然后运行。
```
```sh
主机(Host)       ->  安装了Docker程序的机器(Docker直接安装在操作系统之上)
客户端(Client)   ->  命令行
仓库(Registry)   ->  用来保存各种打包好的软件镜像
镜像(Images)     ->  软件打包好的镜像，放在docker仓库中
容器(Container)  ->  运行中的这个镜像称为容器，容器启动是非常快速的！
```
