[TOC]



# Config

> properties

```properties
#server.port=8090
#server.servlet.context-path=/demo

spring.application.name=amqp-publisher

spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
spring.datasource.url=jdbc:mysql://192.168.8.7:33306/test0329?useSSL=false&allowMultiQueries=true&serverTimezone=GMT%2B8
spring.datasource.username=bluecardsoft
spring.datasource.password=#$%_BC13439677375

#(1).引用配置变量(无则使用默认值 spring)
info.msg=hello ${server.servlet.context-path : /spring}

#(2).随机值；微服务中不需要记录 ip:prot，所以可随机指定端口
info.random=${random.int[1000,9999]}

#动态web修改 context-path -> 项目右键 -> properties -> 搜索web -> 修改 Web Project Settings
```

> yaml

```yaml
k:(空格)v #表示一对键值对(空格必须有),其中属性和值大小写敏感

#1.数组(List/Set)的标准写法：
pets: 
 - cat
 - dog

#行内写法：
pets: [cat,dog,pig]

#2.随机数 
${random.int}; ${random.int(10)}; ${random.int(10,100)}

#3.引用配置变量
person.age = ${random.int}
person.last_name = 张三${person.age:18}

#4.转义
spring:
 datasource:
  url: jdbc:mysql://192.168.8.7:33306/test0329?useSSL=false&allowMultiQueries=true
  username: bluecardsoft
  password: "#$%_BC13439677375" #""双引号里的内容不会转义符，''单括号则会
  driver-class-name: com.mysql.jdbc.Driver
  type: com.alibaba.druid.pool.DruidDataSource
```

> 加载顺序（先后）

```java
– classpath:/            //路径src/main/resources
– classpath:/config/
– file:./                //当前项目的根路径，与pom同级（jar包同级目录）。
– file:./config/

//优先级别: 低--->高. 高优先级覆盖低优先级.
//加载顺序: 先--->后. (由里到外). 后加载的覆盖先加载的. [互补配置]
```
```java
0.以上是开发时配置文件的位置—对于打成jar包
    由于 classpath 会被打成jar包; 而 file 则不会,所以应该把配置文件放到jar包同级目录.
    //jar包同级 '/config/*.yml' 优先级最高, jar包内部默认位置的 '*.yml' 优先级最低!

1.配置外部log
    //在配置文件中指定log位置.(内部或外部yml都可以)
    //推荐外部 -> logback的 scan 和 scanPeriod 两个属性保证了 热部署,即改即生效!!!
    logging.config=file:./config/logback-spring.xml
```
> 读取配置

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```
```properties
voice.in=欢迎光临

info.enabled=false
info.remote-address=192.168.1.1
info.security.username=user
info.security.password=pwd
info.security.roles=USER,ADMIN
```
```java
//1.批量读取，将配置文件转成javabean

//@PropertySource: 加载指定的配置文件
//value: 设置需要加载的属性文件,可以一次性加载多个. (默认参数)
//encoding: 编码格式,默认""
//ignoreResourceNotFound: 当指定的配置文件不存在是否报错,默认 false
//name: 在Springboot的环境中必须唯一. 默认"class path resource [config/my.properties]"

//@Value: 用于读取单个属性值

//@ConfigurationProperties: 用于批量读取属性值
//prefix: 属性前缀,通过 'prefix+字段名' 匹配属性
//ignoreUnknownFields: 是否忽略未知的字段
//ignoreInvalidFields: 是否忽略验证失败(类型转换异常)的字段
//-----------<<<<<<一定要有GET/SET方法>>>>--------------------

@Data// lombok插件，自动生成 GET/SET
@Component
@ConfigurationProperties(/* prefix = "info", */ ignoreUnknownFields = true, ignoreInvalidFields = true)
@PropertySource(value = { "file:./my.properties",
                         "classpath:my.properties" }, encoding = "utf-8", ignoreResourceNotFound = false, name = "my.properties")
public class MyProperties {
    public Voice voice; //voice开头
    public Info info; //info开头

    @Data
    public static class Voice {
        public String in;
    }

    @Data
    private static class Info {
        private boolean enabled;
        private InetAddress remoteAddress;// 下划线语法,驼峰语法等都能匹配属性
        private Security security;
    }

    @Data
    private static class Security {
        private String username;
        private String password;
        private List<String> roles = new ArrayList<>(Collections.singleton("USER"));
    }
}

//通过 @Autowired 方式取值 MyProperties
logger.info("MyProperties--res: {}", JSON.toJSON(myProperties));
{
    "info": {
        "enabled": false,
        "remoteAddress": "192.168.1.1",
        "security": {
            "password": "pwd",
            "roles": [
                "USER",
                "ADMIN"
            ],
            "username": "user"
        }
    },
    "voice": {
        "in": "欢迎光临"
    }
}
```

```java
//2.单个读取

@Value("${info.enabled}") //(1). @Value
public String infoEnabled;

@Autowired //(2). Environment
Environment env;

String pwd = env.getProperty("spring.mail.password");
```

```java
//3.通过IO读取

// 默认从此类所在包下读取,path需要添加前缀"/"
// InputStream in = getClass().getResourceAsStream("/my.properties");

// 默认从ClassPath下读取,path不需要添加前缀
InputStream in = getClass().getClassLoader().getResourceAsStream("my.properties");
Properties properties = new Properties();
properties.load(new InputStreamReader(in, "UTF-8")); //U8方式读取
properties.forEach((key, value) -> log.info(key + " - " + value));
```

>`@Value("#{}")与@Value("${}")的区别`

```java
//(1).@Value("#{}") -> 通过SpEl表达式获取: 常量; bean属性值; 调用bean的某个方法    
@Value("#{1}")
private int number; // 获取数字1

@Value("#{'Spring Expression Language'}") // 获取字符串常量
private String language;

@Value("#{info.remoteAddress}") // 获取bean的属性
InetAddress address;
```
```java
//(2).@Value("${}") -> 获取属性文件中定义的属性值    
@Value("${info.enabled:}")
public String enabled; //获取配置属性,默认空字符串
```
```java
//(3).总结
${ property : default_value }
#{ obj.property? : default_value } //二者取默认值时,语法不同(多个?)
#{ '${}' } //二者可以结合使用,注意单引号!~! 但不能反过来,如: ${ '#{}' }
```
>@PostConstruct



>多环境切换，不同环境加载不同配置，`profile特性`

```java
//以下文件与 application.yml 存放在同级目录下。其中，前者配置特殊信息；后者配置公用信息。二者相互补充
application-dev.yml     ->    开发环境
application-test.yml    ->    测试环境 
application-prod.yml    ->    生产环境
```
    0.激活profile特性的三种方法.
    (1).在默认配置文件中激活: spring.profiles.active=dev
    (2).(略)命令行: java -jar demo.jar --spring.profiles.active=dev
    (3).(略)虚拟机参数(VM argumments): -Dspring.profiles.active=dev
```xml
<!--1.log的profile特性-->
<springProfile name="dev"> //<!-- 控制台 * 测试环境 -->
    <root level="info">
        <appender-ref ref="console" />
    </root>
</springProfile>

<springProfile name="prod"> //<!-- 控制台 * 生产环境 -->
    <root level="warn">
        <appender-ref ref="console" />
    </root>
</springProfile>
```
# Actuator

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

> 各个端点endpoint

```properties
auditevents        --    审计事件
beans            --    应用程序上下文里全部的Bean,以及它们的关系
health            --    报告应用程序的健康指标,这些值由 HealthIndicator 的实现类提供
conditions        --    自动配置报告,记录哪些自动配置条件通过了,哪些没通过
configprops        --    描述配置属性(包含默认值)如何注入Bean
info            --    显示配置文件中以 'info' 打头的属性
threaddump        --    获取线程活动的快照
scheduledtasks    --    定时任务
httptrace        --    跟踪 HTTP 请求-响应交换的情况
mappings        --    描述全部的URI路径，以及它们和控制器(包含Actuator端点)的映射关系
(*)metrics        --    报告各种应用程序度量信息,比如内存用量和HTTP请求计数.
(*)loggers        --    显示和修改应用程序中的loggers配置
(*)env            --    获取全部环境属性
```
> Spring Boot2.x 所有端点访问路径都移到了/actuator

```properties
#默认只暴露两个端点: health 和 info
management.endpoints.web.exposure.include=* //暴露所有
management.endpoints.web.exposure.exclude=env //不暴露: env
    
http://localhost:8090/demo/actuator        --> 返回所有已暴露的端点
http://localhost:8090/demo/actuator/health --> 访问health端点
```

```properties
#以上，端点带(*)表示当前路径只能获取目录信息,详情信息得需要进一步访问获取. 如，获取系统cpu个数:
http://localhost:8090/demo/actuator/metrics/system.cpu.count

#所有模块的日志级别
http://127.0.0.1:8090/demo/actuator/loggers
#具体模块的日志级别
http://127.0.0.1:8090/demo/actuator/loggers/com.example.controller

#发送 POST 请求到以上路径，动态修改以上模块的日志级别为 DEBUG，成功状态码为 '204'
POST - 请求体: {"configuredLevel": "DEBUG"} - Content-Type: application/json
```

> 定制端点endpoint

```properties
#若要恢复 1.x 方式(即用 /health 代替 /actuator/health), 设置以下属性:
management.endpoints.web.base-path=/
```

```properties
#开启应用的远程关闭功能.【post请求】
management.endpoint.shutdown.enabled=true 
```

```properties
management.server.port=8091
#只有在设置了 management.server.port 时才有效 (可选)
management.server.servlet.context-path=/management
#管理端的基本路径 (可选)
management.endpoints.web.base-path=/application

#设置了以上三项，则访问 health 端点路径
http://localhost:8091/demo/management/application/health
```

```properties
#关闭端点 - health
management.endpoint.health.enabled=false

#默认，只显示health部分信息，开启显示全部信息
management.endpoint.health.show-details=always
```

```properties
#配置文件加入以下内容，可在端口info看到, 访问 http://192.168.8.7:8090/demo/actuator/info
info.myinfo.port=9527
```

> 端点 Beans

```java
druid: { //Spring应用程序上下文中的Bean名称或ID
    aliases: [ ], //
    scope: "singleton", //Bean的作用域.(通常是单例,这也是默认作用域)
    type: "com.alibaba.druid.pool.DruidDataSource", //Bean的Java类型
        //.class文件的物理位置,通常是一个URL,指向构建出的JAR文件.会随着应用程序的构建和运行方式发生变化
    resource: "class path resource [com/example/demo/config/DruidConfig.class]", 
    dependencies: [ ] //当前Bean注入的Bean ID列表
},
```



















