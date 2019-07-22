



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

> SpringBoot 与 SpringCloud 版本对应

```
1.5.x - Dalston、Edgware
2.0.x - Finchley
2.1.x - Greenwich
```

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

> `demo-parent` 父项目。项目创建：https://start.spring.io/

```java
项目名最好使用下划线进行分割 'demo_parent'。但是，微服务名称只能用-进行分割，不能用下划线 'demo-parent'。

由于父项目不写代码逻辑，所以可将 src 目录删除。
```

>项目规划

```java
父项目 'demo-parent' 不写逻辑
公共模块 'demo-common' 最终以jar包形式供其他微服务调用，非微服务
用户微服务 'demo-user' 依赖 'demo-common'
交友微服务 'demo-friend' 依赖 'demo-common'，并调用 'demo-user'

父项目的打包方式选择 pom 类型，'不要配置 build 节点的打包插件，子微服务（demo-common 除外）自行配置 build 插件'。
```

>区别 IDEA 中的 project 和 module

```

```

>随机端口号：采用随机端口的方式来设置各个服务实例

```properties
#两种方式。
#只设置 server.port=0，虽然可以随机端口，但是在注册到Eureka时会出现一个问题：所有实例都使用了同样的实例名（如：demo-user）
server.port=0
eureka.instance.instance-id=${spring.application.name}:${random.int[1,100]}

#这种方式有问题，将导致 项目端口，Eureka注册端口，Eureka显示端口都不一样。
server.port=${random.int[10000,19999]}
eureka.instance.instance-id=${spring.application.name}:${server.port}
```

##基础配置

> 父项目打包类型必须选择 pom 类型。

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
<version>1.0-SNAPSHOT</version>
<packaging>pom</packaging> <!--父项目的打包类型必须设置为 pom-->

<modules>
    <module>demo_common</module>
    <module>demo_user</module>
    <module>demo_friend</module>
    <module>demo_eureka</module>
    <module>demo_zuul</module>
    <module>demo_config</module>
</modules>
```

> 父项目只依赖通用的jar包。如 mysql 驱动包只在部分子模块使用，就不要写在父项目中。

```xml
<properties>
    <!--G:2.1.x; F:2.0.x-->
    <spring-cloud.version>Greenwich.SR1</spring-cloud.version>
    <java.version>1.8</java.version>
</properties>

<!--父项目锁定 spring-cloud 和 demo-common 版本-->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <dependency>
            <groupId>com.example</groupId>
            <artifactId>demo_common</artifactId>
            <version>${demo_common.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>

<!--公共依赖-->
<dependencies>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <scope>provided</scope> <!--参与编译，测试，运行，但不会打包-->
        <optional>true</optional> <!--true: 依赖不会传递，但是该依赖写在父项目则所有子类都可用。false: 会传递-->
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope> <!--只在运行时起作用-->
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope> <!--测试时起作用-->
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>

<!--SpringBoot打包插件-->
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

#公共模块

##基础配置

> `demo-common` 公共模块，最终是以jar包被其他微服务调用。`没有配置文件，也勿需指定微服务名`。

```java
选中父项目，然后右键选择 new -> module，项目名称：'demo_common'

对于公共模块只写公共方法，不写业务逻辑，所以 pom.xml 不用引用其他jar包
```

> 基础配置 `一定要跳过 SpringBoot 打包插件`

```xml
<parent>
    <artifactId>demo_parent</artifactId>
    <groupId>com.example</groupId>
    <version>1.0-SNAPSHOT</version>
</parent>
<modelVersion>4.0.0</modelVersion>

<artifactId>demo_common</artifactId>

<dependencies>
    <!-- https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt</artifactId>
        <version>0.9.1</version>
    </dependency>
</dependencies>

<build>
    <finalName>demo-common</finalName>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <skip>true</skip> <!--公共模块demo-common，跳过此打包插件-->
            </configuration>
        </plugin>
    </plugins>
</build>
```

##公共方法

> 状态码实体类。新建包`entity`

```java
public class StatusCode {
    public static final int OK = 20000; //成功
    public static final int ERROR = 20001; //失败
    public static final int LOGIN_ERROR = 20002; //用户名或密码错误
    public static final int ACCESS_ERROR = 20003; //权限不足
    public static final int REMOTE_ERROR = 20004; //远程调用失败
    public static final int REP_ERROR = 20005; //重复操作
}
```

> 控制类的统一返回

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private boolean flag; //是否成功 （规范2）
    private Integer code; //返回码 （规范1）
    private String message; //返回信息

    private Object data; //返回数据

    public Result(boolean flag, Integer code, String message) { //三个参数的构造
        this.flag = flag;
        this.code = code;
        this.message = message;
    }
}
```

> Ali规约

```java
//【强制】所有的 POJO 类属性必须使用包装数据类型。
//【强制】RPC 方法的返回值和参数必须使用包装数据类型。

//【推荐】所有的局部变量使用基本数据类型。

说明：POJO 类属性没有初值是提醒使用者在需要使用时，必须自己显式地进行赋值，任何 NPE 问题，或者入库检查，都由使用者来保证。

正例：数据库的查询结果可能是 null，因为自动拆箱，用基本数据类型接收有 NPE 风险。

反例：比如显示成交总额涨跌情况，即正负 x%，x 为基本数据类型，调用的 RPC 服务，调用不成功时，返回的是默认值，页面显示为 0%，
这是不合理的，应该显示成中划线。所以包装数据类型的 null 值，能够表示额外的信息，如：远程调用失败，异常退出。
```

```java
//【强制】POJO 类中布尔类型变量都不要加 is 前缀，否则部分框架解析会引起序列化错误。

说明：在本文 MySQL 规约中的建表约定第一条，表达是与否的值采用 is_xxx 的命名方式，所以，需要在<resultMap>设置从 is_xxx 到 xxx 的映射关系。

反例：定义为基本数据类型 Boolean isDeleted 的属性，它的方法也是 isDeleted()，RPC 框架在反向解析的时候，“误以为”对应的属性名称是 deleted，
导致属性获取不到，进而抛出异常。
```
## 工具类

> 雪花算法（代码略）

> JWT认证（代码略）



#用户微服务

## 基础配置

> `demo-user` 用户微服务。

```java
//与公共模块的区别：
公共模块只写一些公共类，不写逻辑代码，最终是以 jar 包形式在各个微服务中被依赖。
基础微服务是整个项目的一个子模块，也是微服务的一个。

二者创建方式相同，都是右键选择 new -> module，项目名称：'demo_user'
```

```properties
server.port=9001

#微服务名称只能用-进行分割，不能用下划线
spring.application.name=demo-user

spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://192.168.8.7:33306/demo_user?useSSL=false&allowMultiQueries=true&serverTimezone=GMT%2B8
spring.datasource.username=bluecardsoft
spring.datasource.password=#$%_BC13439677375

#jpa
spring.jpa.database=mysql
spring.jpa.show-sql=true
spring.jpa.generate-ddl=true
spring.jpa.open-in-view=false
```

```xml
<parent>
    <artifactId>demo_parent</artifactId>
    <groupId>com.example</groupId>
    <version>1.0-SNAPSHOT</version>
</parent>
<modelVersion>4.0.0</modelVersion>

<artifactId>demo_user</artifactId>

<dependencies>
    <!--引用公共模块-->
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>demo_common</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>druid-spring-boot-starter</artifactId>
        <version>1.1.10</version>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>

<build>
    <finalName>demo-user</finalName> <!--打包名称-->
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

> 启动类

```java
@SpringBootApplication
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }

    //id生成器，并不是所有的微服务项目都使用，所以不要在 IdWork 类上加 @Component
    //而应该在使用 id生成器 的微服务中用 @Bean 标签注入
    @Bean
    public IdWorker idWorker() {
        return new IdWorker();
    }
}
```

## 常用方法

> pojo：实体类

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_user") //数据表名
public class User {
    @Id //主键id
    private String id;

    @Column(name = "loginname") //非主键，列名。默认是驼峰转下划线
    private String loginName;

    @Column(name = "password")
    private String password;

    @Column(name = "fanscount")
    private Integer fansCount;

    @Column(name = "followcount")
    private Integer followCount;
}
```

> DAO：JpaRepository（常用接口）

```java
public interface UserDao extends JpaRepository<User, String> { }
```

>Service：`微服务中不需要使用接口，直接写实现类即可`

```java
@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public User findById(String userId) {
        return userDao.findById(userId).get();
    }
}
```

> Controller：

```java
@Slf4j
@RequestMapping("/user")
@RestController
public class UserController {

    @Autowired
    UserService userService;

    //获取多个对象的方法用 list 做前缀，复数形式结尾如：listObjects
    //获取单个对象的方法用 get 做前缀
    //插入的方法用 save/insert 做前缀
    //修改的方法用 update 做前缀
    //删除的方法用 remove/delete 做前缀
    //获取统计值的方法用 count 做前缀
    @GetMapping("/{userId}")
    public Result getUser(@PathVariable("userId") String userId) {
        User user = userService.findById(userId);
        log.info("User: {}", user);
        return new Result(true, StatusCode.OK, "查询成功", user);
    }
}
```

##特殊说明

> JWT认证的定义（依赖和工具类）写在公共模块 `demo_common`，供其他微服务所共享。

```java
@Data
@ConfigurationProperties(prefix = "jwt.config")
public class JwtUtil {
    //...略
}
```

>各个微服务在使用JWT认证时，必须在启动类中注入 Bean，并添加配置

```java
@Bean
public JwtUtil jwtUtil() {
    return new JwtUtil();
}
```

```properties
jwt.config.key=bluecard
#jwt.config.ttl=300000
```



#交友微服务

##基础配置

> `demo-friend` 交友微服务。

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
  -- tb_nofriend 去掉此行
  `islike` varchar(1) DEFAULT NULL COMMENT '是否互相喜欢', --0：单向喜欢，1：互相喜欢
  PRIMARY KEY (`userid`,`friendid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
```

##业务逻辑

> 主要逻辑

```
当用户登陆后，在推荐好友列表中点击“心”，表示喜欢此人，在数据库 tb_friend 表中插入一条数据，islike 为0

当你点击了喜欢过的人，也喜欢了你，表示互粉成功！ 也向 tb_friend 表中插入一条数据，islike为1，并且将你喜欢她的数据 islike 也修改为1

当你点击了不喜欢某人（点击了叉），向 tb_nofriend 添加一条记录

当两个人互粉后，其中一人不喜欢对方了，删除好友表中的记录 ，向非好友表中添加记录
```

>什么场景下使用 SpringCloud 呢？

```
用户表有两列：fanscount 表示粉丝数，followcount 表示关注数

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

#------------

> SpringCloud主要框架

```
服务发现——Netflix Eureka
服务调用——Netflix Feign
熔断器——Netflix Hystrix
服务网关——Netflix Zuul
分布式配置——Spring Cloud Config
消息总线——Spring Cloud Bus
```








# Eureka

##基础概念

> `服务发现`：遵守AP原则

```
Eureka 是 Netflix 开发的服务发现框架，包含两个组件：Eureka-Server 和 Eureka-Client。

Eureka-Server 提供服务注册服务，各个节点启动后，会在 Eureka-Server 中进行注册，
这样 Eureka-Server 中的服务注册表中将会存储所有可用服务节点的信息，服务节点的信息可以在界面中直观的看到。

Eureka-Client 是一个java客户端，用于简化与 Eureka-Server 的交互，客户端同时也是一个内置的、使用轮询(round-robin)负载算法的负载均衡器。
在应用启动后，将会向 Eureka-Server 发送心跳，默认周期为 30 秒，如果 Eureka-Server 在多个心跳周期内没有接收到某个节点的心跳，
Eureka-Server 将会从服务注册表中把这个服务节点移除（默认90秒，3个周期）。

Eureka-Server 之间通过复制的方式完成数据的同步，Eureka 还提供了客户端缓存机制，即使所有的 Eureka-Server 都挂掉，
客户端依然可以利用缓存中的信息消费其他服务的API。综上，Eureka 通过心跳检查、客户端缓存等机制，确保了系统的高可用性、灵活性和可伸缩性。
```

> Eureka三大角色

```
Eureka Server 提供服务注册和发现

Service Provider服务提供方将自身服务注册到Eureka，从而使服务消费方能够找到

Service Consumer服务消费方从Eureka获取注册服务列表，从而能够消费服务
```

> 保护模式：某时刻某个微服务不可用，Eureka不会立刻清理，依旧会对该微服务的信息进行保存。`应对网络异常的安全保护措施`

```java
'EMERGENCY! EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP WHEN THEYRE NOT...'
```

```
Eureka-Server 在一定时间内没有接收到某个微服务实例的心跳，将会注销该实例（默认90秒，3个心跳周期）。

Eureka-Server 在运行期间，会统计心跳失败的比例在 15 分钟之内是否低于 85%，如果出现低于的情况（在单机调试的时候很容易满足，
实际在生产环境上通常是由于网络不稳定导致），Eureka-Server 会将当前的实例注册信息保护起来，同时提示这个警告。

保护模式 主要用于一组客户端和 Eureka-Server 之间存在网络分区场景下的保护。
一旦进入保护模式，Eureka-Server 将会尝试保护其服务注册表中的信息，不再删除服务注册表中的数据（也就是不会注销任何微服务）。

eureka.server.enable-self-preservation=false #禁用自我保护模式
```

> Eureka & Zookeeper

```
 
作为服务注册中心，Eureka比Zookeeper好在哪里
著名的CAP理论指出，一个分布式系统不可能同时满足C(一致性)、A(可用性)和P(分区容错性)。由于分区容错性P在是分布式系统中必须要保证的，因此我们只能在A和C之间进行权衡。
因此
Zookeeper保证的是CP,
Eureka则是AP。
 
4.1 Zookeeper保证CP
当向注册中心查询服务列表时，我们可以容忍注册中心返回的是几分钟以前的注册信息，但不能接受服务直接down掉不可用。也就是说，服务注册功能对可用性的要求要高于一致性。但是zk会出现这样一种情况，当master节点因为网络故障与其他节点失去联系时，剩余节点会重新进行leader选举。问题在于，选举leader的时间太长，30 ~ 120s, 且选举期间整个zk集群都是不可用的，这就导致在选举期间注册服务瘫痪。在云部署的环境下，因网络问题使得zk集群失去master节点是较大概率会发生的事，虽然服务能够最终恢复，但是漫长的选举时间导致的注册长期不可用是不能容忍的。
 
4.2 Eureka保证AP
Eureka看明白了这一点，因此在设计时就优先保证可用性。Eureka各个节点都是平等的，几个节点挂掉不会影响正常节点的工作，剩余的节点依然可以提供注册和查询服务。而Eureka的客户端在向某个Eureka注册或时如果发现连接失败，则会自动切换至其它节点，只要有一台Eureka还在，就能保证注册服务可用(保证可用性)，只不过查到的信息可能不是最新的(不保证强一致性)。除此之外，Eureka还有一种自我保护机制，如果在15分钟内超过85%的节点都没有正常的心跳，那么Eureka就认为客户端与注册中心出现了网络故障，此时会出现以下几种情况： 
1. Eureka不再从注册列表中移除因为长时间没收到心跳而应该过期的服务 
2. Eureka仍然能够接受新服务的注册和查询请求，但是不会被同步到其它节点上(即保证当前节点依然可用) 
3. 当网络稳定时，当前实例新的注册信息会被同步到其它节点中
 
因此， Eureka可以很好的应对因网络故障导致部分节点失去联系的情况，而不会像zookeeper那样使整个注册服务瘫痪。
```



##基础配置

>服务端の微服务：`demo-eureka` 。网页验证：<http://localhost:8761/>

```properties
server.port=8761

#euraka
#是否注册到 Eureka 服务中，本身就是服务端，勿需注册（不做高可用的前提）
eureka.client.register-with-eureka=false
#是否从 Eureka 中检索服务，同上
eureka.client.fetch-registry=false
eureka.client.service-url.defaultZone=http://localhost:${server.port}/eureka
#服务端实例名
#eureka.instance.hostname=eureka
```

```xml
<parent>
    <artifactId>demo_parent</artifactId>
    <groupId>com.example</groupId>
    <version>1.0-SNAPSHOT</version>
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

```java
//启动类添加注解
@EnableEurekaServer
```

> 客户端の微服务：以 `demo-user` 为例

```properties
#eureka
#将ip注册到 Eureka。默认注册的是主机名
eureka.instance.prefer-ip-address=true
eureka.client.service-url.defaultZone=http://localhost:8761/eureka

#自定义服务名称信息，默认显示 -> 主机名:微服务名:端口号
#当 多个实例 以相同名字注册到eureka服务中时，只会显示最后一个实例。所以，最好用不同的名字
#eureka.instance.instance-id=${spring.application.name}:${server.port}
```

```xml
<!--添加依赖 Eureka-Client-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

```java
@EnableEurekaClient //可省，默认添加
```

> 服务发现

```java
@Autowired
DiscoveryClient client;

@GetMapping("/discovery")
public void discovery() {
    List<String> services = client.getServices();
    System.out.println("services: " + services);

    List<ServiceInstance> instances = client.getInstances("demo-friend");
    instances.forEach(x -> System.out.println(x.getServiceId() + " - " + x.getHost() + " - "
                                              + x.getPort() + " - " + "" + x.getUri()));
}
```

##集群配置

> 基本原理

```
服务启动后向 Eureka 注册，Eureka-Server 会将注册信息向其他 Eureka-Server 进行同步，当服务消费者要调用服务提供者，则向服务注册中心获取服务提供者地址，然后会将服务提供者地址缓存在本地，下次再调用时，则直接从本地缓存中取，完成一次调用。


当服务注册中心Eureka Server检测到服务提供者因为宕机、网络原因不可用时，则在服务注册中心将服务置为DOWN状态，并把当前服务提供者状态向订阅者发布，订阅过的服务消费者更新本地缓存。


服务提供者在启动后，周期性（默认30秒）向Eureka Server发送心跳，以证明当前服务是可用状态。Eureka Server在一定的时间（默认90秒）未收到客户端的心跳，则认为服务宕机，注销该实例。
```

> 三个服务端の微服务：`demo-eureka1`，`demo-eureka2`，`demo-eureka3`

```properties
server.port=8761

#euraka
#服务端实例名
#eureka.instance.hostname=eureka
eureka.instance.hostname=eureka8761.com
#是否注册到 Eureka 服务中，本身就是服务端，勿需注册（不做高可用的前提）
eureka.client.register-with-eureka=false
#是否从 Eureka 中检索服务，同上
eureka.client.fetch-registry=false
#eureka.client.service-url.defaultZone=http://${eureka.instance.hostname}:${server.port}/eureka
eureka.client.service-url.defaultZone=http://eureka8762.com:8762/eureka, http://eureka8763.com:8763/eureka
```

```properties
server.port=8762

eureka.instance.hostname=eureka8762.com
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
eureka.client.service-url.defaultZone=http://eureka8761.com:8761/eureka, http://eureka8763.com:8763/eureka
```

```properties
server.port=8763

eureka.instance.hostname=eureka8763.com
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
eureka.client.service-url.defaultZone=http://eureka8761.com:8761/eureka, http://eureka8762.com:8762/eureka
```

> 客户端の微服务：`demo-friend`

```properties
#eureka
#将ip注册到 Eureka。默认注册的是主机名
eureka.instance.prefer-ip-address=true
#eureka.client.service-url.defaultZone=http://localhost:8761/eureka
eureka.client.service-url.defaultZone=http://eureka8761.com:8761/eureka, http://eureka8762.com:8762/eureka, \
  http://eureka8763.com:8763/eureka
```

> 修改hosts文件，模拟三台主机：`C:\Windows\System32\drivers\etc\hosts`

```
127.0.0.1 eureka8761.com
127.0.0.1 eureka8762.com
127.0.0.1 eureka8763.com
```

> 集群启动

```shell
demo-eureka1，demo-eureka2，demo-eureka3，demo-user，demo-friend #依次启动

http://eureka8761.com:8761 #浏览器查看 Eureka集群版 的注册情况
http://eureka8762.com:8762
http://eureka8763.com:8763

GET http://localhost:9002/friend/1 #接口测试
```

# Ribbon

> RestTemplate 和注解 @LoadBalanced

```java
@Bean //主配置类中注入bean
@LoadBalanced //开启负载均衡
public RestTemplate restTemplate(){ 
    return new RestTemplate();
}
```

```java
@RunWith(SpringRunner.class)
@SpringBootTest(classes = FriendApplication.class)
public class FriendTest {

    @Autowired
    RestTemplate restTemplate;

    @Test
    public void test01() {
        //RPC远程调用服务，两种方式：1.名称（可以负载均衡），2.域名（不可以负载均衡）
        
        // String url = "http://demo-user/user/1";
        String url = "http://192.168.5.23:9001/user/1";

        Result result = restTemplate.getForObject(url, Result.class);
        System.out.println("Result: " + result);
    }
}
```


# Feign

## 基础概念

>`服务调用`：一个声明式的 WebService（Web服务）客户端

```java
//Feign使用方法：定义一个接口，然后在上面添加注解即可。

Feign 也支持可拔插式的编码器和解码器。SpringCloud 对 Feign 进行了封装，使其支持了 SpringMVC 标准注解和 HttpMessageConverters。
Feign 可以与 Eureka 和 Ribbon 组合使用以支持负载均衡。
 
//Feign能干什么？
Feign旨在使编写 Java-Http 客户端变得更容易。
前面在使用 Ribbon + RestTemplate 时，利用 RestTemplate 对http请求的封装处理，形成了一套模版化的调用方法。
实际开发中，由于对服务依赖的调用可能不止一处，往往一个接口会被多处调用，所以通常都会针对每个微服务自行封装一些客户端类来包装这些依赖服务的调用。
所以，Feign在此基础上做了进一步封装，由他来帮助我们定义和实现依赖服务接口的定义。
在Feign的实现下，我们只需创建一个接口并使用注解的方式来配置它（以前是Dao接口上面标注Mapper注解,现在是一个微服务接口上面标注一个Feign注解即可），
即可完成对服务提供方的接口绑定，简化了使用 Spring-cloud-Ribbon 时，自动封装服务调用客户端的开发量。
 
//Feign集成了Ribbon
利用 Ribbon 维护了 MicroServiceCloud-Dept 的服务列表信息，并且通过轮询实现了客户端的负载均衡。
而与Ribbon不同的是，通过 Feign 只需要定义服务绑定接口且以声明式的方法，优雅而简单的实现了服务调用。
```

> Feign & Ribbon

```
Feign 通过接口的方法调用Rest服务，Ribbon 是通过 RestTemplate

请求发送给 Eureka服务器，通过 Feign 直接找到服务接口，由于在进行服务调用的时候融合了 Ribbon 技术，所以也支持负载均衡作用。
```

##基础配置

> 交友微服务 `demo-friend` 调用用户微服务 `demo-user` 。所以，在 `demo-friend` 中添加依赖

```xml
<artifactId>demo_friend</artifactId>

<dependencies>
    <!--添加依赖 Feign-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-openfeign</artifactId>
    </dependency>
    <!--添加依赖 Eureka-Client-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
</dependencies>
```

```java
@EnableFeignClients //添加注解feign
@EnableDiscoveryClient //开启服务发现功能
```

> `请求头转发`，默认过滤请求头（还有问题，待解决？？）。

```java
@Slf4j
@Configuration
public class FeignConfig implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (null != headerNames) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                String values = request.getHeader(name);
                template.header(name, values);
            }
        }
    }
}
```

> `demo-friend` 中新建包 `com.example.friend.client`，存放 `demo-user` 中的接口

```java
@Component //加不加都行，无意义
@FeignClient(name = "demo-user", configuration = FeignConfig.class) //指定微服务名，不能包含下划线
public interface UserClient {

    //"/user"不可省，用于对被调用的微服务进行地址映射
    //@PathVariable 注解一定要指定参数名称，否则出错
    @GetMapping("/user/{userId}")
    Result getUser(@PathVariable("userId") String userId);
}
```

> `demo-user`中的元接口

```java
@Slf4j
@RequestMapping("/user")
@RestController
public class UserController {
    
    @GetMapping("/{userId}")
    public Result getUser(@PathVariable("userId") String userId) {
        //... ...
        return new Result(true, StatusCode.OK, "查询成功", user);
    }
}
```

> `demo-friend`的Controller使用Client

```java
@RequestMapping("/friend")
@RestController
public class FriendController {

    @Autowired
    UserClient userClient;

    @GetMapping("/{userId}")
    public Result getUser(@PathVariable("userId") String userId) {
        return userClient.getUser(userId);
    }
}
```

> 负载均衡：同时启动多次 `demo-user`，多次请求，轮流调用。

```java
同时启动多次单个 SpringBoot 项目：启动绿三角左边的 'Edit Config...'，选中待启动项目，取消构造'Single-instance-only'
每次运行 SpringBoot 项目前，修改配置文件中的端口号即可。

注意：'demo-base'最好排除依赖'spring-boot-devtools'
```

#Hystrix

##基础概念

> `熔断器`：用于处理分布式系统的延迟和容错的开源库

```java
Hystrix [hɪstrɪks]，中文含义是豪猪，因其背上长满棘刺，从而拥有了自我保护的能力。

在分布式系统里，许多依赖不可避免的会调用失败，比如超时、异常等。
Hystrix 能够保证在一个依赖出问题的情况下，不会导致整体服务失败，避免级联故障，以提高分布式系统的弹性。
 
'熔断器' 本身是一种开关装置，当某个服务单元发生故障之后，通过熔断器的故障监控（类似熔断保险丝），
向调用方返回一个符合预期的、可处理的备选响应（FallBack），而不是长时间的等待或者抛出调用方无法处理的异常，
这样就保证了服务调用方的线程不会被长时间、不必要地占用，从而避免了故障在分布式系统中的蔓延，乃至雪崩。
```

> 雪崩效应

```java
在微服务架构中通常会有多个服务层调用，基础服务的故障可能会导致级联故障，进而造成整个系统不可用的情况，这种现象被称为服务雪崩效应。
服务雪崩效应是一种因'服务提供者'的不可用导致'服务消费者'的不可用，并将不可用逐渐放大的过程。

如：A作为服务提供者，B为A的服务消费者，C和D是B的服务消费者。A不可用引起了B的不可用，并将不可用像滚雪球一样放大到C和D时，雪崩效应就形成了。
```

> 主要作用

```java
//服务降级
其实就是线程池中单个线程障处理，防止单个线程请求时间太长，导致资源长期被占有而得不到释放，从而导致线程池被快速占用完，导致服务崩溃。

Hystrix能解决如下问题：
1.请求超时降级，线程资源不足降级，降级之后可以返回自定义数据
2.线程池隔离降级，分布式服务可以针对不同的服务使用不同的线程池，从而互不影响
3.自动触发降级与恢复
4.实现请求缓存和请求合并 
```

```java
//服务熔断
熔断机制是应对雪崩效应的一种微服务链路保护机制。
当'扇出'链路的某个微服务不可用或者响应时间太长时，会进行服务的降级，进而熔断该节点微服务的调用，快速返回"错误"的响应信息。
当检测到该节点微服务调用响应正常后恢复调用链路。

Hystrix 会监控微服务间调用的状况，当失败的调用到一定阈值，缺省是5秒内20次调用失败就会启动熔断机制。熔断机制的注解是 @HystrixCommand。
```

```java
//服务限流
主要是提前对各个类型的请求设置最高的QPS阈值，若高于设置的阈值则对该请求直接返回，不再调用后续资源。
这种模式不能解决服务依赖的问题，只能解决系统整体资源分配问题，因为没有被限流的请求依然有可能造成雪崩效应。
```

##熔断Feign

> Feign 本身支持Hystrix，不需要额外引入依赖。`demo-friend`中开启Hystrix

```properties
#feign-hystrix
feign.hystrix.enabled=true
```

> `demo-friend` 中新建包 `com.example.friend.client.impl`，存放 `demo-friend` 中接口的熔断实现类

```java
@Component
public class UserClientImpl implements UserClient {

    @Override
    public Result getUser(String userId) {
        return new Result(true, StatusCode.OK, "熔断器启动...");
    }
}
```

> `com.example.friend.client` 包中的接口添加配置

```java
@Component //加不加都行，无意义
@FeignClient(name = "DEMO-USER" //指定微服务名，不能包含下划线
        , configuration = FeignConfig.class
        , fallback = UserClientImpl.class //接口的实现类
        // , fallbackFactory = UserClientFallBackFactory.class //接口实现 FallbackFactory，不推荐
)
public interface UserClient {

    //"/user"不可省，用于对被调用的微服务进行地址映射
    //@PathVariable注解一定要指定参数名称，否则出错
    @GetMapping("/user/{userId}")
    Result getUser(@PathVariable("userId") String userId);
}
```
##熔断Hystrix

> `服务熔断`：某个微服务不可用或者响应时间太长时，会进行服务熔断，快速返回"错误"的响应信息。

```xml
<!-- 引入熔断器 hystrix -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
```

```properties
#配置说明：https://www.jianshu.com/p/39763a0bd9b8
#https://www.e-learn.cn/content/qita/1592490
#https://blog.csdn.net/chengqiuming/article/details/81568234

#hystrix
#执行超时时间，默认 1000ms
hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds=2000
```

```java
@EnableHystrix //熔断器注解，主启动类
```

```java
@RequestMapping("/friend")
@RestController
public class FriendController {

    @Autowired
    UserClient userClient;

    @HystrixCommand(fallbackMethod = "fallBack", //指定降级方法，在熔断和异常时会走降级方法
                    /*略去N个参数*/
                    commandProperties = { //超时时间，不起作用？？
                        @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")
                    })
    @GetMapping("/{userId}")
    public Result getUser(@PathVariable("userId") String userId) {
        System.out.println("getUser: " + LocalTime.now());
        return userClient.getUser(userId);
    }

    public Result fallBack(String userId) { //降级方法
        System.out.println("fallBack: " + LocalTime.now());
        return new Result(false, StatusCode.REMOTE_ERROR, "熔断机制的降级方法");
    }
}
```

##服务监控

> 服务监控の微服务`demo-hystrix`

```xml
<artifactId>demo_hystrix</artifactId>

<dependencies>
    <!--引入服务监控-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
    </dependency>
</dependencies>
```

```properties
server.port=8070
```

```java
@EnableHystrixDashboard //服务监控
```

> 服务监控の消费者`demo-friend`

```xml
<!-- 引入熔断器 hystrix -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
</dependency>
```

```properties
#actuator：暴露所有端口
management.endpoints.web.exposure.include=*
```

```java
@EnableHystrix //熔断器
```

> 监控测试

```shell
#浏览器打开以下页面，出现豪猪，则证明启动成功
http://localhost:8070/hystrix

#第一个输入框填写 服务消费者的 ip:端口/...
http://localhost:9002/actuator/hystrix.stream

Delay：该参数用来控制服务器上轮询监控信息的延迟时间，默认 2000ms。可通过配置该属性来降低客户端的网络和CPU消耗。
Title：该参数对应了头部标题 Hystrix-Stream 之后的内容，默认会使用具体监控实例的URL，可以通过配置该信息来展示更合适的标题。 
```

```java
实心圆：共有两种含义。它通过颜色的变化代表了实例的健康程度，它的健康度从 '绿色 < 黄色 < 橙色 < 红色' 递减。

该实心圆除了颜色的变化之外，它的大小也会根据实例的请求流量发生变化，流量越大该实心圆就越大。
所以通过该实心圆的展示，就可以在大量的实例中 '快速的发现故障实例和高压力实例'。

曲线：用来记录2分钟内流量的相对变化，可以通过它来观察到流量的上升和下降趋势。
```





# Zuul

## 基础概念

> `服务网关`：对请求的 路由 + 过滤

```java
路由：负责将外部请求转发到具体的微服务实例上，是实现外部访问统一入口的基础。
过滤：负责对请求的处理过程进行干预，是实现请求校验、服务聚合等功能的基础（集中处理权限问题）。

Zuul 和 Eureka 进行整合，'将Zuul自身注册为Eureka服务治理下的应用'，同时从Eureka中获得其他微服务的消息，
也即以后的访问微服务都是通过Zuul跳转后获得。
```

##基本配置

> 网关微服务：`demo-zuul`

```xml
<artifactId>demo_zuul</artifactId>

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
spring.application.name=demo-zuul

#eureka
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
#将IP注册到 Eureka-Server。默认注册的是主机名
eureka.instance.prefer-ip-address=true

#zuul
#配置请求URL的请求规则，指定Eureka注册中心中的服务id，转发请求头（默认过滤请求头）
zuul.routes.demo-user.path=/users/**
zuul.routes.demo-user.service-id=demo-user
zuul.routes.demo-user.custom-sensitive-headers=true
zuul.routes.demo-friend.path=/friends/**
zuul.routes.demo-friend.service-id=demo-friend
zuul.routes.demo-friend.custom-sensitive-headers=true
#忽略所有微服务
#zuul.ignored-services=*
#所有访问都加前缀
zuul.prefix=/demo

#查看所有的网关映射
management.endpoints.web.exposure.include=routes

#jwt
jwt.config.key=bluecard
```

```java
@EnableZuulProxy //zuul
@EnableEurekaClient //eureka-client
```

> 查看网关映射

```shell
#后者更为详细
http://localhost:9011/actuator/routes
http://localhost:9011/actuator/routes/details
```



> 配置网关后，使用 IDEA 的 REST_API 测试

```properties
###获取 user 的id为1信息
GET http://localhost:9002/friend/1

###获取 user 的id为1信息（zuul）
GET http://localhost:9011/demo/friends/friend/1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.*.*
```

```properties
###登陆
POST http://localhost:9001/user/login
Content-Type: application/json

{"loginName": "aaa","password": "111"}

###登陆（zuul）
POST http://localhost:9011/demo/users/user/login
Content-Type: application/json

{"loginName": "aaa","password": "111"}
```

```properties
###删除用户
DELETE http://localhost:9001/user/5
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.*.*

###删除用户（zuul）
DELETE http://localhost:9011/demo/users/user/5
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.*.*
```
## 性能调优

><https://github.com/leonzm/springcloud_zuul>

```properties
#zuul内部路由可以理解为使用一个线程池去发送路由请求，所以需要扩大这个线程池的容量。默认：20,200
zuul.host.max-per-route-connections=1000
zuul.host.max-total-connections=1000
zuul.host.connect-timeout-millis=60000
zuul.host.socket-timeout-millis=60000

#同时，设置SpringBoot内嵌Tomcat的并发。默认：100,200,10000
server.tomcat.accept-count=1000
server.tomcat.max-threads=1000
server.tomcat.max-connections=2000
```

# Config

##基础概念

> `分布式配置`：将配置文件放到云端，方便后期维护

```java
在分布式系统中，由于服务数量巨多，为了方便服务配置文件统一管理，实时更新，所以需要分布式配置中心组件。
Spring-Cloud-Config 支持配置服务放在配置服务的内存中（即本地），也支持放在远程Git仓库中。

//主要功能：
集中管理配置文件
不同环境不同配置，动态化的配置更新，分环境部署比如：dev/test/prod/beta/release
运行期间动态调整配置，不再需要在每个服务部署的机器上编写配置文件，服务会向配置中心统一拉取配置自己的信息
当配置发生变动时，服务不需要重启即可感知到配置的变化并应用新的配置
将配置信息以REST接口的形式暴露
```

```shell
两个角色：Config-Server，Config-Client。

Config-Server 是一个可横向扩展、集中式的配置服务器，它用于集中管理应用程序各个环境下的配置，
默认使用Git存储配置文件内容，也可以使用SVN存储，或者是本地文件存储。

Config-Client 用于操作存储在 Config-Server 中的配置内容。微服务在启动时会请求 Config-Server 获取配置文件的内容，请求到后再启动容器。
```

##基础配置

> 服务端の微服务：`demo-config`。页面测试：<http://192.168.5.23:12000/user-dev.properties>

```java
将项目中的配置文件重命名为： '{application}-{profile}.yml' 或 '{application}-{profile}.properties'。
如'user-dev.properties'，'eureka-dev.properties'等

码云上新建仓库 'demo-config'，然后上传以上的配置文件（除了'demo-config.properties'）。
```

```properties
server.port=12000
spring.application.name=demo-config

#eureka
eureka.instance.prefer-ip-address=true
eureka.client.service-url.defaultZone=http://localhost:8761/eureka

#config
spring.cloud.config.server.git.uri=https://gitee.com/skyl4537/demo-config.git
#spring.cloud.config.server.git.username=
#spring.cloud.config.server.git.password=
#默认，Config-Server克隆下来的文件保存在 C:/Users/<当前用户>/AppData/Local/Temp 目录下
#spring.cloud.config.server.git.basedir=/var/tmp
```

```xml
<artifactId>demo_config</artifactId>

<dependencies>
    <!--添加依赖 Config-Server-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-server</artifactId>
    </dependency>
    <!--引入依赖 Eureka-Client-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
</dependencies>
```

```java
@EnableConfigServer
@EnableEurekaClient
```

> 客户端の微服务：`demo-user`。

```java
//添加配置文件 bootstrap.properties，删除原有配置文件
applicaiton.yml //是用户级的资源配置项
bootstrap.yml   //是系统级的，优先级更加高

Spring-Cloud 会创建一个 'Bootstrap Context'，作为Spring应用的 'Application Context'的父上下文。
初始化的时候，Bootstrap Context 负责从'外部源'加载配置属性并解析配置。这两个上下文共享一个从外部获取的 'Environment'。
Bootstrap 属性有高优先级，默认情况下，它们不会被本地配置覆盖。

Bootstrap context 和 Application Context 有着不同的约定，所以新增了一个 'bootstrap.yml'文件，
保证 Bootstrap Context 和 Application Context 配置的分离。
```

```properties
#config
spring.cloud.config.name=friend
spring.cloud.config.profile=dev
spring.cloud.config.label=master
#此种配置可实现高可用，当有多个微服务 demo-config
spring.cloud.config.discovery.service-id=demo-config
spring.cloud.config.discovery.enabled=true

#12000为 demo-config 的端口号（无法高可用）
#spring.cloud.config.uri=http://localhost:12000
```

```xml
<!--引入依赖 config-client-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```
> 结果测试

```properties
#将 demo-user 模块的配置文件，重命名为 user.properties，上传至 git。可通过以下方式访问：
http://localhost:12000/user.properties
http://localhost:12000/user.yml
http://localhost:12000/user.json         #自动转换格式
http://localhost:12000/user-a.properties #a换成其他字符也是可以访问的
```

# Bus

##基础概念

> `消息总线`：可以在不重启微服务的情况下，更新码云中的配置文件，让其立刻生效

```java
事件、消息总线，用于在集群（例如，配置变化事件）中传播状态变化，可与 Spring-Cloud-Config 联合实现热部署。
```

##基础配置

> 服务端の微服务：Bus 配合 Config 使用，在 `demo-config` 中配置

```properties
#rabbitmq
spring.rabbitmq.host=192.168.5.23
spring.rabbitmq.port=5672

#bus
#暴露触发消息总线的地址（Actuator模块）
management.endpoints.web.exposure.include=bus-refresh
```

```xml
<!--添加依赖 Bus-Server-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```

> 客户端の微服务：以 `demo-user` 为例

```properties
#rabbitmq
spring.rabbitmq.host=192.168.5.23
spring.rabbitmq.port=5672
```

```xml
<!--引入依赖 Bus-Client-->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```

> 测试`默认配置`

```java
修改码云上的配置文件 'user-dev.properties'，将数据库连接ip改为 192.168.8.8。发送 POST 请求刷新配置（状态码 204 表示成功）。
POST http://192.168.5.23:12000/actuator/bus-refresh

然后再次请求，观察返回结果，确认数据库是否切换。
GET http://192.168.5.23:9011/user/user/1
```

> 测试`自定义配置`

```properties
info.msg=2019-7-10 19:52:59
```

```java
@RefreshScope //很重要
@Slf4j
@RequestMapping("/user")
@RestController
public class UserController {
    @Value("${info.msg}")
    private String infoMsg;

    @GetMapping("/info")
    public Result info() {
        return new Result(true, StatusCode.OK, "查询成功!", infoMsg);
    }
}
```

```shell
码云上的配置文件 'user-dev.properties'，新增字段 info.msg。
代码中新增对外接口，获取自定义配置。一定不能忘了类注解 @RefreshScope

修改码云上的自定义配置之后，再次发送：
POST http://192.168.5.23:12000/actuator/bus-refresh

测试是否已修改：
GET http://192.168.5.23:9001/user/info
```

# 容器部署

##Dockerfile

> 一系列命令和参数构成的脚本，这些命令应用于基础镜像并最终创建一个新的镜像（只是镜像，而非容器）。

```
（1）对于开发人员：可以为开发团队提供一个完全一致的开发环境；
（2）对于测试人员：可以直接拿开发时所构建的镜像或者通过 Dockerfile 文件构建一个新的镜像开始工作了；
（3）对于运维人员：在部署时，可以实现应用的无缝移植。
```

> 构建镜像：jdk1.8

```shell
#新建目录，将'jdk-8u191-linux-x64.tar.gz'上传至此目录，目录下新建文件：dockerfile
mkdir -p /var/tmp/docker-jdk8
```

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
ADD jdk-8u191-linux-x64.tar.gz /usr/local/java/
#配置jdk环境变量
ENV JAVA_HOME /usr/local/java/jdk1.8.0_191
ENV JRE_HOME $JAVA_HOME/jre
ENV CLASSPATH $JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JRE_HOME/lib:$CLASSPATH
ENV PATH $JAVA_HOME/bin:$PATH
```

```shell
docker run --name jdk8 -d jdk1.8#执行命令构建镜像（最后一个点，表示 dockerfile 在当前目录）
docker build -t='jdk1.8' .

#查看镜像是否建立完成
docker images

#构建容器jdk8
docker run --name jdk8 -d jdk1.8
```

>私有仓库搭建与配置

```shell
#拉取私有仓库镜像
docker pull registry

#创建私有仓库容器
docker run --name registry -d -p 5000:5000 registry

#打开浏览器，输入以下地址。看到 {"repositories":[]} 表示私有仓库搭建成功，并且内容为空
http://192.168.5.23:5000/v2/_catalog

#修改配置使得 docker 信任私有仓库地址
#对于ubuntu系统，更改 /etc/default/docker
DOCKER_OPTS="--registry-mirror=https://docker.mirrors.ustc.edu.cn" #代理，加快下载
DOCKER_OPTS="--insecure-registry juandapc:5000 --insecure-registry 192.168.5.23:5000" #解决http问题

#对于CentOS系统，更改 /etc/dokcer/daemon.json（没有则新建）
{
"registry-mirrors": ["https://docker.mirrors.ustc.edu.cn"],
"insecure-registries":["0.0.0.0:5000"]
}

#重启docker
sudo service docker restart
```

>镜像上传至私有仓库

```shell
#标记此镜像为私有仓库的镜像
docker tag jdk1.8 192.168.5.23:5000/jdk1.8

#启动私服容器
docker start registry

#上传标记的镜像
docker push 192.168.5.23:5000/jdk1.8
```

##Mvm插件

>微服务部署有两种方法

```
（1）手动部署：首先基于源码打包生成jar包（或war包），将jar包（或war包）上传至虚拟机并拷贝至JDK容器。
```

```
（2）通过Maven插件自动部署
```

>Maven插件自动部署步骤

```shell
#修改宿主机的docker配置，让其可以远程访问
#对于ubuntu系统，更改 /etc/default/docker。添加
DOCKER_OPTS="-H unix:///var/run/docker.sock -H tcp://0.0.0.0:2375"

##对于CentOS系统，更改 /lib/systemd/system/docker.service。ExecStart=后追加配置
ExecStart=/usr/bin/dockerd ‐H tcp://0.0.0.0:2375 ‐H unix:///var/run/docker.sock

#刷新配置，重启 docker 和私有仓库
sudo service docker restart
docker start registry
```

```xml
<!--以 'demo-config' 工程为例，pom文件新增配置-->
<build>
<plugins>
    <!-- docker的maven插件，官网：https://github.com/spotify/docker-maven-plugin -->
    <plugin>
        <groupId>com.spotify</groupId>
        <artifactId>docker-maven-plugin</artifactId>
        <version>0.4.13</version>
        <configuration>
            <!--注意ip地址-->
            <imageName>192.168.5.23:5000/${project.artifactId}:${project.version}</imageName>
            <baseImage>jdk1.8</baseImage>
            <entryPoint>["java", "-jar","/${project.build.finalName}.jar"]</entryPoint>
            <resources>
                <resource>
                    <targetPath>/</targetPath>
                    <directory>${project.build.directory}</directory>
                    <include>${project.build.finalName}.jar</include>
                </resource>
            </resources>
            <dockerHost>http://192.168.5.23:2375</dockerHost> <!--对应上文修改的配置 -H 0.0.0.0:2375 -->
        </configuration>
    </plugin>
</plugins>
</build>
```

```shell
#在 IDEA 的 Terminal 命令提示符下，进入 demo-common 工程所在的目录，输入以下命令，进行打包和上传镜像
F:\sp_project\demo_parent\demo_config> mvn clean package docker:build -DpushImage

#命令执行完成之后，浏览器输入以下路径。得到 {"repositories":["demo-config"]}
http://192.168.5.23:5000/v2/_catalog

#进入宿主机，查看镜像。确认微服务 demo-config 已经做成镜像
docker images

#通过该镜像构建容器
docker run --name demo-config -d -p 12000:12000 192.168.5.23:5000/demo_config:1.0-SNAPSHOT

#浏览器测试，容器是否启动成功
http://192.168.5.23:12000/user-dev.properties
```

## 常见问题

>http请求方式

```shell
#测试用的是ubuntu14.04.1，docker1.62。push上传时，报以下异常。
#Error response from daemon: v1 ping attempt failed with error: Get http://19

#这是由于客户端采用https，docker-registry未采用https服务所致。一种处理方式是把客户对私有库地址请求改为http。

#对于ubuntu系统，更改 /etc/default/docker
DOCKER_OPTS="--registry-mirror=https://docker.mirrors.ustc.edu.cn" #代理，加快下载
DOCKER_OPTS="--insecure-registry juandapc:5000 --insecure-registry 192.168.5.23:5000" #解决http问题

#对于CentOS系统，更改 /etc/dokcer/daemon.json（没有则新建）
{
"registry-mirrors": ["https://docker.mirrors.ustc.edu.cn"],
"insecure-registries":["0.0.0.0:5000"]
}
```

> mvn配置

```xml
<!--No plugin found for prefix 'docker' in the current project and in the plugin groups....-->

<!--解决方案：在 maven 的 conf/setting.xml 中加入-->
<pluginGroups>
    <pluginGroup>com.spotify</pluginGroup>  
</pluginGroups>
```

# 持续集成

##基础概念

>持续集成 Continuous integration ，简称CI

```
随着软件开发复杂度的不断提高，团队开发成员间如何更好地协同工作以确保软件开发的质量已经慢慢成为开发过程中不可回避的问题。
尤其是近些年来，敏捷（Agile）在软件工程领域越来越红火，如何能再不断变化的需求中快速适应和保证软件的质量也显得尤其的重要。

持续集成正是针对这一类问题的一种软件开发实践。它倡导团队开发成员必须经常集成他们的工作，甚至每天都可能发生多次集成。
而每次的集成都是通过自动化的构建来验证，包括自动编译、发布和测试，从而尽快地发现集成错误，让团队能够更快的开发内聚的软件。
```

```
持续集成的特点：
1.它是一个自动化的周期性的集成测试过程，从检出代码、编译构建、运行测试、结果记录、测试统计等都是自动完成的，无需人工干预；
2.需要有专门的集成服务器来执行集成构建；
3.需要有代码托管工具支持，下一小节将介绍Git以及可视化界面Gogs的使用
```

```
持续集成作用：
1.保证团队开发人员提交代码的质量，减轻了软件发布时的压力；
2.持续集成中的任何一个环节都是自动完成的，无需太多的人工干预，有利于减少重复过程以节省时间、费用和工作量；
```

##Gogs

>Gogs：一款极易搭建的自助 Git 服务，`管理代码`。

```
Gogs 的目标是打造一个最简单、最快速和最轻松的方式搭建自助 Git 服务。
使用 Go 语言开发使得 Gogs 能够通过独立的二进制分发，并且支持 Go 语言支持的 所有平台，包括 Linux、Mac OS X、Windows 以及 ARM 平台。
```

> Gogs安装与配置

```shell
#下载镜像
docker pull gogs/gogs

#构建容器（10022内部使用，3000为外部使用）
docker run --name gogs -d -p 10022:22 -p 3000:3000 -v /var/gogsdata:/var/tmp/gogsdata gogs/gogs

#浏览器输入以下网址，进入首次安装页面。
http://192.168.5.23:3000

#gogs的数据存储选择 SQLite3，大型公司可选择 mysql。域名修改为：192.168.5.23。SSH端口33022。立即安装。
#注册用户：用户名：skyl，邮箱：skyl@qq.com，密码：skyl
#创建仓库：demo，不用选择 私有。创建完成，复制当前仓库的地址，备用。
http://192.168.5.23:3000/skyl/demo.git
```

> IDEA配置Git

```shell
本地已安装 git（windows版本）
IDEA - CVS - Enable Version Control Integration... - 选择 git #当前 project 选择git管理
IDEA - Settings - 搜索git - Path to Git...：选择本地的 git.exe  #本地 git 路径

右键父项目 demo-parent
Git - Repository - Remotes... - 粘贴上一步复制的仓库地址。#配置git仓库地址
Git - Add #将当前项目添加到仓库中
Git - Commit Directory... #提交到本地仓库
Git - Repository - Push - push - 输入用户名和密码 #提交到远程仓库
```

##Jenkins

>Jenkins：一款持续集成工具，可以将更新后的代码自动部署到服务器上运行。

```
Jenkins 能实施监控集成中存在的错误，提供详细的日志文件和提醒功能，还能用图表的形式形象地展示项目构建的趋势和稳定性。

易安装：仅仅一个 java -jar jenkins.war，从官网下载该文件后，直接运行，无需额外的安装，更无需安装数据库；
易配置：提供友好的GUI配置界面；
变更支持：Jenkins能从代码仓库（Subversion/CVS）中获取并产生代码更新列表并输出到编译输出信息中；
支持永久链接：用户是通过web来访问Jenkins的，而这些web页面的链接地址都是永久链接地址，因此，你可以在各种文档中直接使用该链接；

集成E-Mail/RSS/IM：当完成一次集成时，可通过这些工具实时告诉你集成结果（据我所知，构建一次集成需要花费一定时间，有了这个功能，
你就可以在等待结果过程中，干别的事情）；
JUnit/TestNG测试报告：也就是用以图表等形式提供详细的测试报表功能；
支持分布式构建：Jenkins可以把集成构建等工作分发到多台计算机中完成；
文件指纹信息：Jenkins会保存哪次集成构建产生了哪些jars文件，哪一次集成构建使用了哪个版本的jars文件等构建记录；
支持第三方插件：使得 Jenkins 变得越来越强大
```

>Jenkins安装

```shell
#搜索和拉取镜像
docker search jenkins
docker pull jenkins

#8080为默认的访问端口，映射为8090。-v 挂载目录。-v /etc/localtime... 让容器使用和服务器同样的时间设置
docker run  --name jenkins -d -p 8090:8080 -p 50000:50000 -v /var/tmp/jenkins:/var/jenkins_home \
-v /etc/localtime:/etc/localtime jenkins
```

```shell
#启动报错：touch: cannot touch '/var/jenkins_home/copy_reference_file.log': Permission denied
#原因是Jenkins镜像内部使用的用户是jenkons（uid为1000），但是我们启动容器时的账号是root。导致用户 jenkons 没有权限操作挂载目录。
#更新挂载目录的权限，重新启动
sudo chown -R 1000:1000 /var/tmp/jenkins
```

```shell
#浏览器打开页面
http://192.168.5.23:8090/

#从容器中的路径（/var/lib/jenkins/secrets/initialAdminPassword）获取到初始化密码
docker exec jenkins tail /var/jenkins_home/secrets/initialAdminPassword

#在页面输入密码，点击 Continue，进入插件安装页面。点击左边的 Install suggested plugins，安装推荐插件就好。
#安装好插件后，系统会提示建立管理员账户。
```

```shell
#系统管理 - 管理插件 - 可选插件 
搜索maven - 勾选：Maven Integration -立即安装
搜索git - 勾选Git - 立即安装

#系统管理 - Global Tool Configuration
新增jdk - 别名：jdk1.8 - JAVA_HOME：/var/tmp/jenkins/jdk1.8.0_191 （此目录为挂载目录，事先解压一份 'jdk-8u191-linux-x64.tar.gz'）
新增mvn - 同上，（过程如下）
```

```shell
tar -zxvf /var/tmp/apache-maven-3.6.1-bin.tar.gz

#编辑 setting.xml 配置文件，配置本地仓库目录
 vi /var/tmp/maven/conf/settings.xml
```

```xml
<localRepository>/var/tmp/maven-repo</localRepository>
 
<pluginGroups>
    <pluginGroup>com.spotify</pluginGroup>  
</pluginGroups>
```

> 持续集成

```shell
#创建任务，Enter an item name：demo。构建一个mvn项目。
#源码管理，选择Git
#Build 
Root POM：demo_config/pom.xml。
Goals and options：clean package docker:build ‐DpushImage #用于清除、打包，构建docker镜像

#返回首页，在列表中找到刚才创建的任务。点击右边的绿色箭头按钮，即可执行此任务。

#当日志打印结果：Success时，通过浏览器查看 docker 私有仓库
http://192.168.5.23:5000/v2/_catalog
{"repositories":["jdk1.8","demo_config"]}

#对于其他项目，同上操作。
```



# 容器管理

## Rancher

> 一个开源的企业级全栈化容器部署及管理平台。

```
Rancher 为容器提供一揽子基础架构服务：CNI兼容的网络服务、存储服务、主机管理、负载均衡、防护墙...
Rancher 让上述服务跨越公有云、私有云、虚拟机、物理机环境运行，真正实现一键式应用部署和管理。
```

> 安装配置

```shell
docker pull rancher/server

#--restart 重启策略
#no，默认策略，在容器退出时不重启容器
#on-failure，在容器非正常退出时（退出状态非0），才会重启容器
#on-failure:3，在容器非正常退出时重启容器，最多重启3次
#always，在容器退出时总是重启容器
#unless-stopped，在容器退出时总是重启容器，但是不考虑在Docker守护进程启动时就已经停止了的容器
docker run --name rancher -d --restart always -p 9090:8080 rancher/server

#浏览器打开连接，可能有点慢。点击右下角的 English，切换为中文。
http://192.168.5.23:9090/
```

> 初始化

```
Rancher 支持将资源分组归属到多个环境。每个环境具有自己独立的基础架构资源及服务，并由一个或多个用户、团队或组织所管理。
例如，您可以创建独立的“开发”、“测试”及“生产”环境以确保环境之间的安全隔离，
将“开发”环境的访问权限赋予全部人员，但限制“生产”环境的访问权限给一个小的团队。
```

```
Default - 环境管理 - 添加环境（demo_dev，demo测试） - 创建测试环境。
生产环境创建（demo_pro，demo生产）同上，左上角可以切换生产环境。
```

```shell 
#构建容器所需的镜像库
基础架构 - 镜像库 -添加镜像库 - Custom - 192.168.5.23（仅添加主机名或IP地址, 不要包含协议 https://) - 用户名密码（可不填）

#构建完成的容器存放位置
基础架构 - 主机 - 添加主机 - 直接使用默认即可 - ④.填写ip地址：192.168.5.23 - ⑤.点击右侧的拷贝，备用。

sudo docker run -e CATTLE_AGENT_IP="192.168.5.23"  --rm --privileged \
-v /var/run/docker.sock:/var/run/docker.sock -v /var/lib/rancher:/var/lib/rancher \
rancher/agent:v1.2.11 http://192.168.5.23:9090/v1/scripts/304DB40722A32FF5FA63:1546214400000:rHPvYtXon5y3mC1YKbnAmOIMk

#将以上的脚本拷贝到 192.168.5.23 执行。
#点击关闭按钮后，会看到界面中显示此主机。可以很方便地管理主机的每个容器的开启和关闭。
```

## 应用部署

> 添加应用

```
应用 - 添加应用 - 名称：demo-dev - 描述：demo微服务应用
```

> mysql容器

```shell
应用 - 选择应用'demo' - 添加服务

名称：mysql8，描述：mysql8.0
选择镜像：mysql:latest（镜像名通过 docker images 读取。取消勾选：创建前总是拉取镜像）
端口映射：33306 + 3306
拉到最下面，添加环境变量：MYSQL_ROOT_PASSWORD=123456

#以上步骤，等同于以下命令
docker run ‐‐name mysql8 -d ‐p 33306:3306 ‐e MYSQL_ROOT_PASSWORD=123456 mysql
```

> rabbitMQ容器

```shell
名称：rabbitmq，描述：rabbitmq
选择镜像：rabbitmq
端口映射：5671 5672 4369 15671 15672 25672
```

> Eureka容器

```shell
名称：demo-eureka，描述：demo-eureka
选择镜像：192.168.5.23:5000/demo_config:1.0-SNAPSHOT（从 docker images 读取）
端口映射：8761 + 8761

http://192.168.5.23:8761/ #浏览器测试
```





