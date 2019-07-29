







## log

> 对于各个级别的日志输出，必须使用条件输出形式 或者 使用占位符的方式。

```java
//正例：（条件）
if (logger.isDebugEnabled()) {
    logger.debug("Processing trade with id: {} and symbol : {} ", id, symbol); //占位符
}
```





#Dubbo

## ZooKeeper

> 基础概念

```shell
ZooKeeper 是一个分布式的，开放源码的分布式应用程序协调服务。它是一个为分布式应用提供一致性服务的软件，
提供的功能包括：配置维护、域名服务、分布式同步、组服务等。
```



> 安装配置

```shell
docker pull zookeeper

#镜像需要端口 2181 2888 3888（客户端端口，从机端口，选举端口）
docker run --name zk01 --restart always -d -p 2181:2181 zookeeper
```

## 基础概念

> 将服务提供者注册到注册中心

```shell
引入 dubbo 和 zk 依赖
配置 dubbo 扫描宝和注册中心地址
使用 @Service（dubbo的注解） 发布服务
```

>服务消费者消费服务

```shell
引入 dubbo 和 zk 依赖
配置 dubbo 的注册中心地址
将服务提供者的接口定义拷贝到消费者
引入服务 @Reference
```











