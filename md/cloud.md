[TOC]

# 基础概念

> 什么是SpringCloud？ http://projects.spring.io/spring-cloud/

```
Spring Cloud是一系列框架的有序集合。

它利用Spring Boot的开发便利性巧妙地简化了分布式系统基础设施的开发，
如服务发现注册、配置中心、消息总线、负载均衡、熔断器、数据监控等，都可以用Spring Boot的开发风格做到一键启动和部署。

Spring并没有重复制造轮子，它只是将目前各家公司开发的比较成熟、经得起实际考验的服务框架组合起来，
通过Spring Boot风格进行再封装，屏蔽掉了复杂的配置和实现原理，最终给开发者留出了一套简单易懂、易部署和易维护的分布式系统开发工具包。
```

>SpringCloud 与 SpringBoot

```
SpringBoot 是 Spring 的一套快速配置框架，可以基于 SpringBoot 快速开发单个微服务，SpringCloud 是一个基于 SpringBoot 实现的云应用开发工具；

SpringBoot 专注于快速、方便集成的单个微服务个体，Spring Cloud关注全局的服务治理框架；

SpringBoot 使用了'默认大于配置'的理念，很多集成方案已经帮你选择好了，能不配置就不配置，
SpringCloud 很大的一部分是基于 SpringBoot 来实现，可以不基于 SpringBoot吗？ 不可以。

SpringBoot 可以离开 SpringCloud 独立使用开发项目，但是 SpringCloud 离不开 SpringBoot，属于依赖的关系。
```

> SpringCloud 和 Dubbo

```
Dubbo 只是实现了服务治理，而 SpringCloud 下面有 21 个子项目（可能还会新增）分别覆盖了微服务架构下的方方面面，
服务治理只是其中的一个方面，一定程度来说，Dubbo 只是 SpringCloud Netflix 中的一个子集。
```

> SpringCloud主要框架

```
服务发现——Netflix Eureka
服务调用——Netflix Feign
熔断器——Netflix Hystrix
服务网关——Netflix Zuul
分布式配置——Spring Cloud Config
消息总线 —— Spring Cloud Bus
```

> SpringCloud 版本

| SpringBoot |       SpringCloud        |
| :--------: | :----------------------: |
|   1.5.x    | Dalston版本、Edgware版本 |
|   2.0.x    |       Finchley版本       |
|   2.1.x    |      Greenwich版本       |

> 镜像，容器，微服务

```
子项目做成镜像，镜像做成容器。

微服务就是同类容器的集合，一个微服务包括一组容器。如，已有的 mysql 容器，或者自定义的 交友 容器等。
```

> 微服务的子项目中是否需要接口？ `不需要`

```
普通项目分为MVC三层，不同层由不同人员维护，所以在不同层进行相互调用时，就需要一套规范，即不同层的接口。

但是对于微服务项目，每一个子项目都是单独的一个服务，由单独的人员进行维护，所以也就不需要定义接口。
```

> 概念解析

```java
'SpringBoot'：Spring的纯注解版，零配置文件，以最少的配置来开发项目。是一种开发风格。

'SpringCloud'：Spring整合第三方技术的架构，将已有的第三方技术以'SpringBoot'的形式（即零配置的形式）封装到Spring框架中。
               所以，SpringCloud 依赖于 SpringBoot，但 SpringBoot 不依赖 SpringCloud。

'SpringMVC'：一种web层MVC框架，用于替代servlet（处理-响应请求，获取表单参数，表单校验等）。

'SpringData'：持久层框架。不仅能够适用于关系型数据库，还能够适用于非~。如 MongoDB，Redis，Hadoop
```

# 父项目

##基础概念


> 父项目：`demo-parent`。创建：<https://start.spring.io/>

```java
项目名最好使用下划线进行分割 'demo_parent'。但是，微服务名称只能用-进行分割，不能用下划线 'demo-parent'。

由于父项目不写代码逻辑，所以可将 src 目录删除。
```

> 区别 IDEA 中的 project 和 module

```

```

##基础配置

>父项目打包类型必须选择 pom 类型。`不要配置build节点`

```xml
<modelVersion>4.0.0</modelVersion>
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.5.RELEASE</version>
    <relativePath /> <!-- lookup parent from repository -->
</parent>
<groupId>com.example</groupId>
<artifactId>demo_parent</artifactId>
<version>0.0.1-SNAPSHOT</version>
<packaging>pom</packaging> <!--父项目必须设置类型 pom-->

<name>demo_parent</name>
<description>Demo project for Spring Boot</description>

<modules>
    <module>demo_common</module> <!--子项目列表-->
    <module>demo_base</module>
    <module>demo_user</module>
    <module>demo_eureka</module>
</modules>

<properties>
    <java.version>1.8</java.version>
</properties>

<!--父项目 demo-parent，锁定 SpringCloud 版本-->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId> <!--G 对应 2.1.x-->
            <!--<version>Finchley.M9</version>--> <!--F 对应 2.0.x-->
            <version>Greenwich.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

> 父项目的 pom 文件只写通用的jar包。如 mysql 驱动包只在部分子模块使用，就不要写在父项目中

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <optional>true</optional> <!-- 依赖只在当前项目生效,不会传递到引用项目中 -->
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <scope>provided</scope> <!--参与编译，测试，运行，但不会打包-->
        <optional>true</optional> <!--true: 依赖不会传递，但是该依赖写在父项目则所有子类都可用。false: 会传递-->
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

> 配置 Spring 的仓库地址，与 build 标签同级

```xml
<repositories> <!--jar包仓库-->
    <repository>
        <id>spring-snapshots</id>
        <name>Spring Snapshots</name>
        <url>https://repo.spring.io/snapshot</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
    <repository>
        <id>spring-milestones</id>
        <name>Spring Milestones</name>
        <url>https://repo.spring.io/milestone</url>
    </repository>
</repositories>

<pluginRepositories> <!--插件仓库-->
    <pluginRepository>
        <id>spring-snapshots</id>
        <name>Spring Snapshots</name>
        <url>https://repo.spring.io/snapshot</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </pluginRepository>
    <pluginRepository>
        <id>spring-milestones</id>
        <name>Spring Milestones</name>
        <url>https://repo.spring.io/milestone</url>
    </pluginRepository>
</pluginRepositories>
```

# 公共模块

##基础配置

> 公共模块，最终是以jar包形式存在。`没有配置文件，所以，也勿需指定微服务名 demo-common`。

```java
选中父项目，然后右键选择 new -> module，项目名称：'demo_common'

对于公共模块只写公共方法，不写业务逻辑，所以 pom.xml 不用引用其他jar包
```

> 基础配置

```xml
<parent>
    <artifactId>demo_parent</artifactId> <!--指定父项目-->
    <groupId>com.example</groupId>
    <version>0.0.1-SNAPSHOT</version>
</parent>
<modelVersion>4.0.0</modelVersion>

<artifactId>demo_common</artifactId> <!--当前项目名。GV同父项目，所以省略-->

<dependencies>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
        <version>3.8.1</version>
    </dependency>
    <dependency>
        <groupId>commons-collections</groupId>
        <artifactId>commons-collections</artifactId>
        <version>3.2.2</version>
    </dependency>
</dependencies>

<build>
    <finalName>demo-common</finalName>
</build>
```

```properties
server.port=9001

#微服务名称只能用-进行分割，不能用下划线
spring.application.name=demo-base
```

##公共方法

> 状态码实体类

```java
package entity; //新建 entity 包，包下创建类 Result

public class StatusCode {
    public static final int OK = 20000; //成功
    public static final int ERROR = 20001; //失败
    public static final int LOGIN_ERROR = 20002; //用户名或密码错误
    public static final int ACCESS_ERROR = 20003; //权限不足
    public static final int REMOTE_ERROR = 20004; //远程调用失败
    public static final int REP_ERROR = 20005; //重复操作
}
```
>返回结果实体类：用于控制器类返回结果

```java
@Data
public class Result {
    private boolean flag; //是否成功 （规范2）
    private Integer code; //返回码 （规范1）
    private String message; //返回信息
    private Object data; //返回数据

    public Result() {
    }

    public Result(boolean flag, Integer code, String message) {
        this.flag = flag;
        this.code = code;
        this.message = message;
    }

    public Result(boolean flag, Integer code, String message, Object data) {
        this.flag = flag;
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
```

> ali规范

```java
//【强制】所有的 POJO 类属性必须使用包装数据类型。
//【强制】RPC 方法的返回值和参数必须使用包装数据类型。

//【推荐】所有的局部变量使用基本数据类型。

说明：POJO 类属性没有初值是提醒使用者在需要使用时，必须自己显式地进行赋值，任何 NPE 问题，或者入库检查，都由使用者来保证。

正例：数据库的查询结果可能是 null，因为自动拆箱，用基本数据类型接收有 NPE 风险。

反例：比如显示成交总额涨跌情况，即正负 x%，x 为基本数据类型，调用的 RPC 服务，调用不成功时，返回的是默认值，页面显示为 0%，这是不合理的，应该显示成中划线。所以包装数据类型的 null 值，能够表示额外的信息，如：远程调用失败，异常退出。
```

```java
//【强制】POJO 类中布尔类型变量都不要加 is 前缀，否则部分框架解析会引起序列化错误。

说明：在本文 MySQL 规约中的建表约定第一条，表达是与否的值采用 is_xxx 的命名方式，所以，需要在<resultMap>设置从 is_xxx 到 xxx 的映射关系。

反例：定义为基本数据类型 Boolean isDeleted 的属性，它的方法也是 isDeleted()，RPC 框架在反向解析的时候，“误以为”对应的属性名称是 deleted，导致属性获取不到，进而抛出异常。
```

>分布式ID生成器

```java
由于数据库在生产环境中要分片部署（MyCat），所以不能使用数据库本身的自增功能来产生主键值，只能由程序来生成唯一的主键值。
采用开源的 twitter 的 snowflake（雪花）算法，总长度64bit。

'优点'：（1）整体上按照时间自增排序（2）整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分) （3）效率较高
经测试，SnowFlake每秒能够产生26万ID左右。

0        //最高位是符号位,始终为0,不可用.
1-41     //41 位的时间序列，精确到毫秒级，可使用到2082年。时间位另一作用是可以根据时间进行排序
42-51    //10 位的机器标识，10位的长度最多支持部署1024个节点（2^10）
52-63    //12 位的计数序列，是一系列的自增id，支持每个节点每毫秒产生4096个ID序号（2^12）
```

![](assets/cloud0.png)



# 基础微服务

##基础配置

> 基础微服务：`demo-base`

```java
//与公共模块的区别：
公共模块只写一些公共类，不写逻辑代码，最终是以 jar 包形式在各个微服务中调用。
基础微服务是整个项目的一个子模块，也是微服务的一个。

但创建方式相同，都是右键选择 new -> module，项目名称：'demo_base'
```

> 基础配置 `父项目不写build节点，所以子项目必须添加打包插件`

```xml
<parent>
    <artifactId>demo_parent</artifactId> <!--父项目-->
    <groupId>com.example</groupId>
    <version>0.0.1-SNAPSHOT</version>
</parent>
<modelVersion>4.0.0</modelVersion>

<artifactId>demo_base</artifactId>

<dependencies>
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>demo_common</artifactId> <!--公共模块-->
        <version>0.0.1-SNAPSHOT</version>
    </dependency>

    <!--添加依赖 config-client-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-config</artifactId>
    </dependency>

    <!--添加依赖 Eureka-Client-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>

    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>druid-spring-boot-starter</artifactId>
        <version>1.1.10</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
</dependencies>

<build>
    <finalName>demo-base</finalName> <!--打包名称-->
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId> <!--打包插件-->
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
    <resources>
        <resource> <!--资源拷贝插件-->
            <directory>src/main/java</directory>
        </resource>
        <resource>
            <directory>src/main/resources</directory>
        </resource>
    </resources>
</build>
```

```properties
server.port=9001

#微服务名称只能用-进行分割，不能用下划线
spring.application.name=demo-base

spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://192.168.8.7:33306/demo_base?useSSL=false&allowMultiQueries=true&serverTimezone=GMT%2B8
spring.datasource.username=bluecardsoft
spring.datasource.password=#$%_BC13439677375

spring.jpa.database=mysql
spring.jpa.show-sql=true
#是否自动生成ddl
spring.jpa.generate-ddl=true
spring.jpa.open-in-view=false
```

##常规方法

> 启动类

```java
@SpringBootApplication
public class BaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaseApplication.class, args);
    }

    //id生成器，并不是所有的微服务项目都使用，所以不要在 IdWork 类上加 @Component
    //而应该在使用 id生成器 的微服务中用 @Bean 标签注入
    @Bean
    public IdWorker idWorker() {
        return new IdWorker();
    }
}
```

> pojo

```java
@Data
@Entity
@Table(name = "tb_label") //同数据表名
public class Label {
    @Id
    private String id;
    private String labelname;//标签名称
    private String state;//状态
    private Long count;//使用数量
    private Long fans;//关注数
    private String recommend;//是否推荐
}
```

> Controller

```java
@Slf4j
@RequestMapping("/label")
@RestController
public class LabelController {

    @Autowired
    LabelService labelService;

    //获取多个对象的方法用 list 做前缀，复数形式结尾如：listObjects
    //获取单个对象的方法用 get 做前缀
    //插入的方法用 save/insert 做前缀
    //修改的方法用 update 做前缀
    //删除的方法用 remove/delete 做前缀
    //获取统计值的方法用 count 做前缀
    @GetMapping
    public Result listLabels() {
        List<Label> labels = labelService.listLabels();
        log.info("listLabels: {}", labels);
        return new Result(true, StatusCode.OK, "查询成功", labels);
    }
}
```

> Service：`微服务中不需要使用接口，直接写实现类即可`

```java
@Slf4j
@Service
@Transactional
public class LabelService {

    @Autowired
    LabelDao labelDao;

    @Autowired
    IdWorker idWorker;

    //查询全部
    public List<Label> listLabels() {
        Iterable<Label> iterable = labelDao.findAll();
        List<Label> labels = new ArrayList<>();
        iterable.forEach(labels::add);
        return labels;
    }
}
```

> DAO

```java
public interface LabelDao extends JpaRepository<Label, String> { }
```

# 用户微服务

##基础配置

> 用户微服务：`demo-user`

```java
创建方式同上，右键选择 new -> module，项目名称：'demo_user'
```

> 基础配置

```xml
<parent>
    <artifactId>demo_parent</artifactId>
    <groupId>com.example</groupId>
    <version>0.0.1-SNAPSHOT</version>
</parent>
<modelVersion>4.0.0</modelVersion>

<artifactId>demo_user</artifactId>

<dependencies>
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>demo_common</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
</dependencies>
```

```properties
server.port=9002

#微服务名，横岗分割
spring.application.name=demo-user

jwt.config.key=bluecard
jwt.config.ttl=300000
```

##特殊说明

> JWT认证的声明，如 pom 中依赖的引用，及工具类 JwtUtil 都写在 `demo_common` 模块（不用写配置），为其他微服务所共享。

```java
@Data
@ConfigurationProperties(prefix = "jwt.config")
public class JwtUtil {
    //...略
}
```

>但是，JWT认证的使用是在 `demo-user，demo-friend` 中，所以，必须在使用微服务的启动类中注入 Bean，并增加配置

```java
@Bean
public JwtUtil jwtUtil() {
    return new JwtUtil();
}
```

```properties
jwt.config.key=bluecard
jwt.config.ttl=300000
```

#交友微服务

##基础配置

> 交友微服务 `demo-friend`

```properties
server.port=9003
spring.application.name=demo-friend
```

> 两张数据表：好友表（tb_friend） 和 非好友表（tb_nofriend）

```sql
DROP TABLE IF EXISTS `tb_friend`;
CREATE TABLE `tb_friend` (
  `userid` varchar(20) NOT NULL COMMENT '用户ID',
  `friendid` varchar(20) NOT NULL COMMENT '好友ID',
  `islike` varchar(1) DEFAULT NULL COMMENT '是否互相喜欢', -- tb_nofriend 去掉此行
  PRIMARY KEY (`userid`,`friendid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

| 字段名称 |   字段含义   | 字段类型 |           备注           |
| :------: | :----------: | :------: | :----------------------: |
|  userid  |    用户ID    |   文本   |                          |
| friendid |    好友ID    |   文本   |                          |
|  islike  | 是否互相喜欢 |   文本   | 0：单向喜欢，1：互相喜欢 |

| 字段名称 | 字段含义 | 字段类型 | 备注 |
| :------: | :------: | :------: | :--: |
|  userid  |  用户ID  |   文本   |      |
| friendid |  好友ID  |   文本   |      |

##业务逻辑

> 主要业务逻辑

```
（1）当用户登陆后，在推荐好友列表中点击“心”，表示喜欢此人，在数据库 tb_friend 表中插入一条数据，islike 为0

（2）当你点击了喜欢过的人，也喜欢了你，表示互粉成功！ 也向 tb_friend 表中插入一条数据，islike为1，并且将你喜欢她的数据 islike 也修改为1

（3）当你点击了不喜欢某人（点击了叉），向 tb_nofriend 添加一条记录

（4）当两个人互粉后，其中一人不喜欢对方了，删除好友表中的记录 ，向非好友表中添加记录
```

>什么场景下使用 SpringCloud 呢？

```
用户表，有两列：fanscount 表示粉丝数，followcount 表示关注数

（1）当用户点击了喜欢：比如小宝关注了楚楚，小宝的 followcount（关注数）加1， 楚楚的 fanscount（粉丝数）加1

（2）当用户删除了好友：比如楚楚删除了好友小宝，小宝的 fanscount（粉丝数）减1，楚楚的 followcount（关注数）减1
```

> 逻辑梳理

```java
//A 添加 B 好友时，A 的 followcount 加1，B的 fanscount 加1。
若 A，B之间毫无关系。添加后，tb_friend 插入一条数据，islike 为0。

若 B 已添加 A 好友。添加后，tb_friend 插入一条数据，islike 为1。'并且，tb_friend 中B对A的 islike 为1'。

//A 删除 B 好友时，A 的 followcount 减1，B的 fanscount 减1。
若 A，B只是单向好友。删除后，tb_friend 移除一条数据，tb_nofriend 增加一条数据。

若 A，B是双向的好友。删除后，tb_friend 移除一条数据，tb_nofriend 增加一条数据。'并且，将 tb_friend 中B对A的 islike 为0'。
```



#Cloud技术

## Eureka

>`服务发现` Netflix-Eureka

```
Eureka 是 Netflix 开发的服务发现框架，SpringCloud 将它集成在自己的子项目 spring-cloud-netflix 中，实现 SpringCloud 的服务发现功能。
Eureka 包含两个组件：Eureka-Server 和 Eureka-Client。

Eureka-Server 提供服务注册服务，各个节点启动后，会在 Eureka-Server 中进行注册，
这样 Eureka-Server 中的服务注册表中将会存储所有可用服务节点的信息，服务节点的信息可以在界面中直观的看到。

Eureka-Client 是一个java客户端，用于简化与 Eureka-Server 的交互，客户端同时也是一个内置的、使用轮询(round-robin)负载算法的负载均衡器。
在应用启动后，将会向 Eureka-Server 发送心跳，默认周期为 30 秒，如果 Eureka-Server 在多个心跳周期内没有接收到某个节点的心跳，
Eureka-Server 将会从服务注册表中把这个服务节点移除（默认90秒，3个周期）。

Eureka-Server 之间通过复制的方式完成数据的同步，Eureka 还提供了客户端缓存机制，即使所有的 Eureka-Server 都挂掉，
客户端依然可以利用缓存中的信息消费其他服务的API。综上，Eureka 通过心跳检查、客户端缓存等机制，确保了系统的高可用性、灵活性和可伸缩性。
```

>Eureka服务端の微服务：`demo-eureka`

```xml
<parent>
    <artifactId>demo_parent</artifactId>
    <groupId>com.example</groupId>
    <version>0.0.1-SNAPSHOT</version>
</parent>
<modelVersion>4.0.0</modelVersion>

<artifactId>demo_eureka</artifactId>

<dependencies>
    <!--添加依赖 Eureka-Server-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
</dependencies>
```

```properties
server.port=6868

#Euraka
#是否注册到Eureka服务中，本身就是服务端，勿需注册
eureka.client.register-with-eureka=false
#是否从Eureka中获取注册信息
eureka.client.fetch-registry=false
eureka.client.service-url.defaultZone=http://127.0.0.1:${server.port}/eureka
```

```java
@EnableEurekaServer //Eureka-Server启动类
@SpringBootApplication
public class EurekaApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaApplication.class, args);
    }
}
```

> Eureka客户端の微服务：以基础微服务为例 `demo-base`

```xml
<!--添加依赖 Eureka-Client-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

```properties
#Eureka
eureka.client.service-url.defaultZone=http://localhost:6868/eureka
#将IP注册到 Eureka-Server。默认注册的是主机名
eureka.instance.prefer-ip-address=true
```

```java
//启动类添加注解
@EnableEurekaClient
```

> 页面验证：<http://localhost:6868/>

```java
主界面中 'System-Status' 系统信息，'General-Info' 一般信息。'Instances-currently-registered-with-Eureka' 注册的所有微服务列表
```

> Eureka 保护模式

```java
如果在 Eureka-Server 的首页看到以下这段提示，则说明Eureka已经进入了保护模式：
'EMERGENCY! EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP WHEN THEYRE NOT...'
```

```
Eureka-Server 在运行期间，会统计心跳失败的比例在 15 分钟之内是否低于 85%，如果出现低于的情况（在单机调试的时候很容易满足，
实际在生产环境上通常是由于网络不稳定导致），Eureka-Server 会将当前的实例注册信息保护起来，同时提示这个警告。

保护模式 主要用于一组客户端和 Eureka-Server 之间存在网络分区场景下的保护。
一旦进入保护模式，Eureka-Server 将会尝试保护其服务注册表中的信息，不再删除服务注册表中的数据（也就是不会注销任何微服务）。
```

##Feign

>`服务调用` Feign简介

```
Feign 是简化 Java-HTTP 客户端开发的工具（java-to-httpclient-binder），它的灵感来自于 Retrofit、JAXRS-2.0 和 WebSocket。

Feign 的初衷是降低统一绑定 Denominator 到 HTTP-API 的复杂度，不区分是否为 restful。
```

> 用户微服务 `demo-user` 调用基础微服务 `demo-base` 。所以，在 `demo-user` 中添加依赖。

```xml
<artifactId>demo_user</artifactId>

<dependencies>
    <!--添加依赖 Feign-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
</dependencies>
```

```java
//启动类添加注解
@EnableDiscoveryClient
@EnableFeignClients
```

>`demo-user` 中新建包 `com.example.user.client`，存放 `demo-base` 中的接口。

```java
@Component //加不加都行，无意义
@FeignClient("demo-base") //指定微服务名，不能包含下划线
public interface LabelClient {

    //用于对被调用的微服务进行地址映射。
    @GetMapping("/label")
    Result listLabels();

    //@PathVariable注解一定要指定参数名称，否则出错
    @GetMapping("/label/{labelId}")
    Result getById(@PathVariable("labelId") String id);
}
```

> `demo-user` 中调用远程接口

```java
@Slf4j
@RequestMapping("/user")
@RestController
public class UserController {

    @Autowired
    LabelClient labelClient;

    @GetMapping("/label")
    public Result listLabels() {
        return labelClient.listLabels();
    }

    @GetMapping("/label/{labelId}")
    public Result getById(@PathVariable("labelId") String id) {
        return labelClient.getById(id);
    }
}
```

> `demo-base`中的元接口

```java
@Slf4j
@RequestMapping("/label")
@RestController
public class LabelController {

    @Autowired
    LabelService labelService;

    //获取多个对象的方法用 list 做前缀，复数形式结尾如：listObjects
    @GetMapping
    public Result listLabels() {
        List<Label> labels = labelService.listLabels();
        return new Result(true, StatusCode.OK, "查询成功", labels);
    }

    //获取单个对象的方法用 get 做前缀
    @GetMapping("/{labelId}")
    public Result getById(@PathVariable String id) { //获取请求行参数
        Optional<Label> label = labelService.getById(id);
        log.info("getById: {}", label.get());
        return new Result(true, StatusCode.OK, "查询成功", label);
    }
}
```

> 负载均衡：同时启动多次 `demo-base`，多次请求，轮流调用。

```java
同时启动多次单个 SpringBoot 项目：启动绿三角左边的 'Edit Config...'，选中待启动项目，取消构造'Single-instance-only'
每次运行 SpringBoot 项目前，修改配置文件中的端口号即可。

注意：'demo-base'最好排除依赖'spring-boot-devtools'
```

##Hystrix

>`熔断器` Hystrix

```
Hystrix [hɪst'rɪks]，中文含义是豪猪，因其背上长满棘刺，从而拥有了自我保护的能力。

Hystrix设计目标：
对来自依赖的延迟和故障进行防护和控制——这些依赖通常都是通过网络访问的
阻止故障的连锁反应
快速失败并迅速恢复
回退并优雅降级
提供近实时的监控与告警
```

> 雪崩效应

```java
在微服务架构中通常会有多个服务层调用，基础服务的故障可能会导致级联故障，进而造成整个系统不可用的情况，这种现象被称为服务雪崩效应。
服务雪崩效应是一种因'服务提供者'的不可用导致'服务消费者'的不可用，并将不可用逐渐放大的过程。

如果下图所示：A作为服务提供者，B为A的服务消费者，C和D是B的服务消费者。A不可用引起了B的不可用，并将不可用像滚雪球一样放大到C和D时，雪崩效应就形成了。
```

![](assets/cloud4.png)

> 用户微服务 `demo-user` 调用基础微服务 `demo-base` 。其中，Feign 本身支持Hystrix，不需要额外引入依赖。`demo-user`中开启Hystrix。

```properties
#feign-hystrix
feign.hystrix.enabled=true
```

>`demo-user` 中新建包 `com.example.user.client.impl`，存放 `demo-base` 中接口的熔断实现类。

```java
@Component
public class LabelClientImpl implements LabelClient {

    @Override
    public Result listLabels() {
        return new Result(false, StatusCode.ERROR, "熔断器启动了");
    }

    @Override
    public Result getById(String id) {
        return new Result(false, StatusCode.ERROR, "熔断器启动了");
    }
}
```

> `demo-user`中的`com.example.user.client`增加注解配置。

```java
@Component //加不加都行，无意义
// @FeignClient("demo-base") //指定微服务名，不能包含下划线
@FeignClient(value = "demo-base", fallback = LabelClientImpl.class) //增加熔断器后的配置
public interface LabelClient {

    //用于对被调用的微服务进行地址映射。
    @GetMapping("/label")
    Result listLabels();

    //@PathVariable注解一定要指定参数名称，否则出错
    @GetMapping("/label/{labelId}")
    Result getById(@PathVariable("labelId") String id);
}
```

## Zuul

> `服务网管` Zuul

```java
不同的微服务一般有不同的网络地址，而外部的客户端可能需要调用多个服务的接口才能完成一个业务需求。
比如一个电影购票的收集APP,可能回调用电影分类微服务，用户微服务，支付微服务等。如果客户端直接和微服务进行通信，会存在一下问题：

# 客户端会多次请求不同微服务，增加客户端的复杂性
# 存在跨域请求，在一定场景下处理相对复杂
# 认证复杂，每一个服务都需要独立认证
# 难以重构，随着项目的迭代，可能需要重新划分微服务，如果客户端直接和微服务通信，那么重构会难以实施
# 某些微服务可能使用了其他协议，直接访问有一定困难

上述问题，都可以借助微服务网关解决。'微服务网关是介于客户端和服务器端之间的中间层，所有的外部请求都会先经过微服务网关'。
```

```java
Zuul 是 Netflix 开源的微服务网关，他可以和 Eureka，Ribbon，Hystrix 等组件配合使用。
Zuul组件的核心是一系列的过滤器，这些过滤器可以完成以下功能：

# 身份认证和安全: 识别每一个资源的验证要求，并拒绝那些不符的请求
# 审查与监控：
# 动态路由：动态将请求路由到不同后端集群
# 压力测试：逐渐增加指向集群的流量，以了解性能
# 负载分配：为每一种负载类型分配对应容量，并弃用超出限定值的请求
# 静态响应处理：边缘位置进行响应，避免转发到内部集群
# 多区域弹性：跨域AWS Region进行请求路由，旨在实现ELB（ElasticLoad Balancing）使用多样化
```

![](assets/cloud5.png)



> 网关微服务 `demo-manager`

```xml
<parent>
    <artifactId>demo_parent</artifactId>
    <groupId>com.example</groupId>
    <version>0.0.1-SNAPSHOT</version>
</parent>
<modelVersion>4.0.0</modelVersion>

<artifactId>demo_manager</artifactId>

<dependencies>
    <!--引入依赖 zuul-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
    </dependency>
    <!--引入依赖 eureka-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
</dependencies>
```

```properties
server.port=9011
spring.application.name=demo-manager

#Eureka
eureka.client.service-url.defaultZone=http://localhost:6868/eureka
#将IP注册到 Eureka-Server。默认注册的是主机名
eureka.instance.prefer-ip-address=true

#zuul
#配置请求URL的请求规则，指定Eureka注册中心中的服务id，转发请求头（默认过滤请求头）
zuul.routes.demo-base.path=/base/**
zuul.routes.demo-base.service-id=demo-base
zuul.routes.demo-base.custom-sensitive-headers=true
zuul.routes.demo-friend.path=/friend/**
zuul.routes.demo-friend.service-id=demo-friend
zuul.routes.demo-friend.custom-sensitive-headers=true
zuul.routes.demo-user.path=/user/**
zuul.routes.demo-user.service-id=demo-user
zuul.routes.demo-user.custom-sensitive-headers=true

#jwt
jwt.config.key=bluecard
```

```java
@EnableZuulProxy //zuul注解
@EnableEurekaClient
@SpringBootApplication
public class ManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ManagerApplication.class, args);
    }
}
```

> zuul过滤器配置

```java
@Slf4j
@Component
public class ManagerFilter extends ZuulFilter {
    @Autowired
    JwtUtil jwtUtil;

    @Override
    public String filterType() { //过滤器类型：pre，route，post，error
        return "pre";
    }

    @Override
    public int filterOrder() { //过滤器的执行顺序，值越小越先执行
        return 0;
    }

    @Override
    public boolean shouldFilter() { //过滤器是否开启
        return true;
    }

    @Override
    public Object run() throws ZuulException { //过滤器的具体逻辑
        log.info("zuul过滤器...");

        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();

        if ("OPTIONS".equals(request.getMethod())) { //好像不起作用
            log.info("跳过-zuul的网关分发...");
            return null;
        }

        String url = request.getRequestURL().toString();
        if (url.indexOf("/user/login") > 0) {
            log.info("跳过-登陆接口：" + url);
            return null;
        }

        String header = request.getHeader(GlobalString.TOKEN_KEY);
        if (StringUtils.isNotBlank(header) && header.startsWith(GlobalString.TOKEN_PREFIX)) {
            String token = header.substring(GlobalString.TOKEN_PREFIX.length());
            log.info("token：" + token);

            try {
                Claims claims = jwtUtil.parseJWT(token);
                log.info("claims：" + claims);

                if (null != claims) {
                    if ("admin".equals(claims.get("roles"))) {
                        requestContext.addZuulRequestHeader(GlobalString.TOKEN_KEY, header);
                        log.info("token验证通过，添加了头信息：" + header);
                        return null;
                    }
                }
            } catch (Exception e) {
                log.error("token验证异常：", e);
            }
        }

        requestContext.setSendZuulResponse(false); //终止运行
        requestContext.setResponseStatusCode(401); //http状态码
        requestContext.setResponseBody("无权访问");
        requestContext.getResponse().setContentType("text/html;charset=UTF-8");
        return null;
    }
}
```

> zuul网关配置后，使用 IDEA 的 REST_API 测试

```properties
###获取label的id为1信息
GET http://localhost:9001/label/1

###获取label的id为1信息（zuul）
GET http://localhost:9011/base/label/1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.*.*
```

```properties
###登陆
POST http://localhost:9002/user/login
Content-Type: application/json

{"loginName": "aaa","password": "111"}

###登陆（zuul）
POST http://localhost:9011/user/user/login
Content-Type: application/json

{"loginName": "aaa","password": "111"}
```

```properties
###删除用户
DELETE http://localhost:9002/user/5
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.*.*

###删除用户（zuul）
DELETE http://localhost:9011/user/user/5
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.*.*
```

##Config

>`分布式配置 ` Config

```
在分布式系统中，由于服务数量巨多，为了方便服务配置文件统一管理，实时更新，所以需要分布式配置中心组件。
Spring-Cloud-Config 支持配置服务放在配置服务的内存中（即本地），也支持放在远程Git仓库中。两个角色：Config-Server，Config-Client。

Config-Server 是一个可横向扩展、集中式的配置服务器，它用于集中管理应用程序各个环境下的配置，
默认使用Git存储配置文件内容，也可以使用SVN存储，或者是本地文件存储。

Config-Client 用于操作存储在 Config-Server 中的配置内容。微服务在启动时会请求 Config-Server 获取配置文件的内容，请求到后再启动容器。
```

>服务端：配置微服务`demo-config`

```java
将项目中的配置文件重命名为： '{application}-{profile}.yml' 或 '{application}-{profile}.properties'

码云上新建仓库 'demo-config'，然后上传以上的配置文件。
```

```xml
<dependencies>
    <!--添加依赖 Eureka-Server-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-server</artifactId>
    </dependency>
</dependencies>
```

```properties
server.port=12000
spring.application.name=demo-config

#config
spring.cloud.config.server.git.uri=https://gitee.com/skyl4537/demo-config.git
```

```java
@EnableConfigServer
@SpringBootApplication
public class ConfigApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigApplication.class, args);
    }
}
```

> 客户端：以基础微服务为例 `demo-base`

```xml
<!--添加依赖 config-client-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

```properties
#添加配置文件 bootstrap.properties（优先级比原有的高），删除原有配置文件

#config
spring.cloud.config.name=base
spring.cloud.config.profile=dev
spring.cloud.config.label=master
#spring.cloud.config.discovery.enabled=true
#spring.cloud.config.discovery.service-id=demo-config
```





































































































