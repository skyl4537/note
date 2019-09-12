



# 基础概念

## 概念相关







> 引入父工程

```sh
'父工程'对各种常用依赖（并非全部）进行了版本管理，引入父工程，则不用关心版本冲突问题，需要什么依赖，直接引入坐标即可！
```

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.5.RELEASE</version>
</parent>
```

```sh

```



# 配置相关

##基础概念

> properties

```properties
#(1).引用配置变量（无则使用默认值 8090）
info.msg=hello ${server.port : 8090}

#(2).随机值。微服务中不需要记录 ip:prot，所以可随机指定端口
info.random=${random.int[1000,9999]}
```

> yaml

```yaml
k:(空格)v #基础语法。表示一对键值对(空格必须有)，其中属性和值大小写敏感

pets: [cat,dog,pig] #数组（List/Set）的行内写法

${random.int}; ${random.int(10)}; ${random.int(10,100)} #随机数3种写法

person.age = ${random.int}
person.last_name = 张${person.age:18} #引用配置变量（无则取默认值 18）

spring:
  datasource:
    password: "#$%_BC13439677375" #""双引号里的内容不会转义符，''单括号则会。（层级关系使用 2个或4个 空格）
```

> 加载顺序

```sh
– classpath:/         #项目 src/main/resources
– classpath:/config/
– file:./             #与 jar 包同级
– file:./config/

#加载顺序：从上到下，从里到外，后加载的优先级更高，高优先级覆盖低优先级。所以，jar包外的配置优先级更高。
```

> 不同环境加载不同配置

```sh
application-dev.yml   #开发环境
application-test.yml  #测试环境
application-prod.yml  #生产环境

#在默认配置文件 application.properties 中激活
spring.profiles.active=dev
```

> 配置外部 logback

```properties
#配置文件中指定log位置（内部或外部yml都可以）
#推荐外部 -> logback的 scan 和 scanPeriod 两个属性保证了 热部署，即改即生效
logging.config=file:./config/logback-spring.xml
```

## 读取配置

> 配置文件

```properties
voice.in=欢迎光临

info.enabled=false
info.remote-address=192.168.1.1
info.security.username=user
info.security.password=pwd
info.security.roles=USER,ADMIN
```

> 读取单个配置

```java
@Value("${info.enabled}") //(1).@Value
public String infoEnabled;
```

```java
@Autowired //(2).Environment
Environment env;

String pwd = env.getProperty("spring.mail.password");
```

> 批量读取

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

```java
/**
 * prefix:                 属性前缀，通过【prefix+字段名】匹配属性，默认""
 * ignoreUnknownFields:    是否忽略未知的字段，默认 true
 * ignoreInvalidFields:    是否忽略验证失败（类型转换异常）的字段，默认 false
 * <p>
 * value:                  需要加载的属性文件，可以一次性加载多个
 * encoding:               编码格式，默认""
 * ignoreResourceNotFound: 当指定的配置文件不存在是否报错，默认 false
 */
@Data
@Component
@ConfigurationProperties(/*prefix = "info",*/ ignoreUnknownFields = true, ignoreInvalidFields = true)
@PropertySource(value = "classpath:my.properties", encoding = "UTF-8", ignoreResourceNotFound = false)
public class MyProperties {
    public Voice voice; //voice开头
    public Info info;   //info开头

    @Data
    public static class Voice { //必须使用静态内部类
        public String in;
    }

    @Data
    private static class Info {
        private boolean enabled;
        private InetAddress remoteAddress; //下划线语法，驼峰语法等都能匹配属性

        private Security security;
    }

    @Data
    private static class Security {
        private String username;
        private String password;
        private List<String> roles = new ArrayList<>(Collections.singleton("USER"));
    }
}
```

> 区别：`@Value("#{}") 与 @Value("${}")`

```java
//${}：获取配置文件中配置的属性
@Value("${info.enabled:'false'}")
String enabled; //默认'false'
```

```java
//#{} -> 通过SpEl表达式获取：bean属性值，调用bean的某个方法
@Value("#{info.remoteAddress？:'127.0.0.1'}") //info的属性，默认 127.0.0.1
String address;
```

## 加密配置

> 配置文件中敏感信息的加密

```xml
<dependency>
    <groupId>com.github.ulisesbocchio</groupId>
    <artifactId>jasypt-spring-boot-starter</artifactId>
    <version>2.1.1</version>
</dependency>
```

```properties
#druid 也可以做到数据库明文加密，jasypt 任何配置都可以加密
#配置文件中指定加密时使用的盐（salt）
jasypt.encryptor.password=EbfYkitulv73I2p0mXI50JMXoaxZTKJ0
```

> 生成加密后的密钥

```java
public void getPwd() {
    BasicTextEncryptor encryptor = new BasicTextEncryptor();
    encryptor.setPassword("EbfYkitulv73I2p0mXI50JMXoaxZTKJ0"); //盐

    String username = encryptor.encrypt("bluecardsoft"); //加密
    System.out.println(username); //同一个字符加密多次结果不一样，但解密后是一样的

    username = encryptor.decrypt(username); //解密
    System.out.println(username);
}
```

> 用生成的密钥替换配置文件的相应位置

```properties
#ENC()是固定写法，（）里面是加密后的信息
spring.datasource.username=ENC(kZ11PHFbXpNzLsJ7bKq2atpDiCzJOAs8)
```

> 盐的安全性：直接写在配置文件中不安全，可以使用以下两个办法

```sh
#（1）在项目部署的时候使用命令传入salt值
java -jar -Djasypt.encryptor.password=G0CvDz7oJn6 xxx.jar
```

```sh
#（2）在服务器的环境变量里配置，进一步提高安全性
vim /etc/profile
export JASYPT_PASSWORD = G0CvDz7oJn6 #末尾插入
source /etc/profile

java -jar -Djasypt.encryptor.password=${JASYPT_PASSWORD} xxx.jar
```

# 基础功能

## druid

> pom

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
    <version>1.1.10</version>
</dependency>
```

```properties
#boot-1.x默认数据源为：org.apache.tomcat.jdbc.pool.DataSource
#boot-2.x默认数据源为：com.zaxxer.hikari.HikariDataSource
spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
```

> 进阶配置

```properties
# 连接池的补充设置。初始化大小，最小，最大
spring.datasource.druid.initial-size=5
spring.datasource.druid.min-idle=5
spring.datasource.druid.max-active=20
# 配置获取连接等待超时的时间，单位是毫秒
spring.datasource.druid.max-wait=60000
# 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
spring.datasource.druid.time-between-eviction-runs-millis=60000
# 配置一个连接在池中最小生存的时间，单位是毫秒
spring.datasource.druid.min-evictable-idle-time-millis=300000
spring.datasource.druid.validation-query=SELECT 1 FROM DUAL
# 空闲时，检测连接的可用性
spring.datasource.druid.test-while-idle=true
# 每次获取到连接时，不检测连接的可用性
spring.datasource.druid.test-on-borrow=false
spring.datasource.druid.test-on-return=false

```

```properties
# 打开PSCache，并且指定每个连接上PSCache的大小
spring.datasource.druid.pool-prepared-statements=true
spring.datasource.druid.max-pool-prepared-statement-per-connection-size=20
# 配置监控统计拦截的filters，去掉后监控界面sql无法统计，'wall'用于防火墙
spring.datasource.druid.filter.commons-log.connection-logger-name=stat,wall,log4j
# 合并多个DruidDataSource的监控数据
spring.datasource.druid.use-global-data-source-stat=true
# 慢SQL记录
spring.datasource.druid.filter.stat.log-slow-sql=true
spring.datasource.druid.filter.stat.slow-sql-millis=2000
# 通过connectProperties属性来打开mergeSql功能，慢SQL记录
#spring.datasource.druid.connect-properties.=druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
```

```properties
# 是否启用StatFilter，默认值true
spring.datasource.druid.web-stat-filter.enabled=true
#spring.datasource.druid.web-stat-filter.url-pattern=
spring.datasource.druid.web-stat-filter.exclusions=*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*

# StatViewServlet配置，说明请参考Druid Wiki，配置_StatViewServlet配置
# 是否启用StatViewServlet，默认值true
spring.datasource.druid.stat-view-servlet.enabled=true
spring.datasource.druid.stat-view-servlet.url-pattern=/druid/*
spring.datasource.druid.stat-view-servlet.login-username=admin
spring.datasource.druid.stat-view-servlet.login-password=123456
#spring.datasource.druid.stat-view-servlet.allow=
spring.datasource.druid.stat-view-servlet.deny=192.168.8.8
spring.datasource.druid.stat-view-servlet.reset-enable=false
```
> 数据库密码加密

```sh
#生成数据库密码的密文和公钥（密码包含特殊符号，用引号括起来）
#命令行切换到 'druid-1.1.18.jar' 所在的路径，然后打开 cmder 工具，执行以下命令：
java -cp .\druid-1.1.18.jar com.alibaba.druid.filter.config.ConfigTools '#$%_BC13439677375'
```

```properties
# 用户名不加密
spring.datasource.username=bluecardsoft
#spring.datasource.password=#$%_BC13439677375
# 生成的加密后的密码
spring.datasource.password=nKGSYPDvmT2ytyLKH4u5yL7/s
# 生成的公钥
public-key=MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAKUx2YO6H
# 配置 connection-properties，启用加密，配置公钥。
spring.datasource.druid.connection-properties=config.decrypt=true;config.decrypt.key=${public-key}
# 启用ConfigFilter
spring.datasource.druid.filter.config.enabled=true
```

## 静态资源

> 默认目录：存放在以下目录的资源都可以直接访问

```properties
classpath:/static/
classpath:/public/
classpath:/resources/
classpath:/META-INFO/resouces/

#classpath:/static/img/sql.png   ---> http://127.0.0.1:8090/demo/img/sql.png
```

> 自定义目录（1）：配置文件

```sh
'file:/logs/'      表示与jar包同级目录的 /logs 目录
'classpath:/logs/' 表示与配置文件同级的 /logs 目录
```

```properties
spring.mvc.static-path-pattern=/log/**
#此配置会覆盖 springboot 默认配置，所以需要手动追加默认配置
spring.resources.static-locations=classpath:/logs/,file:/logs/,classpath:/static/
```

> 自定义目录（2）：代码配置

```java
@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/log/**")
            .addResourceLocations("classpath:/logs/", "file:/logs/");
    }
}
```

> webjars：将前端资源（js，css等）打成jar包，使用Maven统一管理。http://www.webjars.org/

```xml
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>bootstrap</artifactId>
    <version>3.3.7-1</version>
</dependency>
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>jquery</artifactId>
    <version>3.3.1</version>
</dependency>
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>webjars-locator</artifactId> <!--页面引用时，可省略版本号（如 jquery-3.3.1）-->
    <version>0.32</version>
</dependency>
```

```html
<head>
    <!--webjars-locator: 页面引用时，可省略版本号.(如 3.3.1)-->
    <!--省略前: <script th:src="@{/webjars/jquery/3.3.1/jquery.min.js}"/>-->
    <script th:src="@{/webjars/jquery/jquery.min.js}"></script>
    <script th:src="@{/webjars/bootstrap/js/bootstrap.min.js}"></script>
    <link rel="stylesheet" th:href="@{/webjars/bootstrap/css/bootstrap.min.css}"/>
</head>
```

## CORS跨域

> 问题描述

```sh
Access to XMLHttpRequest at 'http://localhost:9005/qrcode/java' from origin 'http://localhost:9006' 
has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present on the requested resource.
```

> 问题原因

```sh
'跨域'：指的是浏览器不能执行其他网站的脚本。'域名、端口、协议任一不同'，就是跨域。
采用前后端分离开发，前后端分离部署，必然会存在跨域问题。

CORS（Cross-Origin Resource Sharing，跨源资源共享）是 w3c 出的一个标准，其思想是使用自定义的HTTP头部让浏览器与服务器进行沟通，
从而决定请求或响应是应该成功，还是应该失败。因此，要想实现CORS进行跨域，需要服务器进行一些设置，同时前端也需要做一些配置和分析。

#注意：localhost 和 127.0.0.1 虽然都指向本机，但也属于跨域。
```

```properties
# 非跨域
http://www.123.com/index.html  --->  http://www.123.com/server.PHP

# 跨域（主域名不同：123/456）
http://www.123.com/index.html  --->  http://www.456.com/server.php

# 跨域（子域名不同：abc/def）
http://abc.123.com/index.html  --->  http://def.123.com/server.php

# 跨域（协议不同：http/https）
http://www.123.com/index.html  --->  https://www.123.com/server.php

# 跨域（端口不同：8080/8081）
http://www.123.com:8080/index.html  --->  http://www.123.com:8081/server.php
```

> 服务端配置（1）：细粒度

```java
/**
 * 细粒度的跨域注解
 *
 * @origins 允许可访问的域列表
 * @maxAge  准备响应前的 缓存持续的 最大时间（以秒为单位）
 */
@CrossOrigin(origins = "http://localhost:9005", maxAge = 3600)
@GetMapping("/java")
public String java() {
    return LocalDateTime.now().toString();
}
```

> 服务端配置（2）：粗粒度

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:9005"); //允许端口 9005 访问
    }
}
```


















#小众功能

## 单元测试

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

```java
@RunWith(SpringRunner.class)
@SpringBootTest//(classes = {SpringMain.class}) //加载项目启动类，当测试类的路径同启动类时，可省。
public class HelloServiceTest {
    
}
```

## 常用接口

>`CommandLineRunner`：用于在应用初始化完成后执行代码逻辑，代码逻辑在整个应用生命周期内只会执行一次

```java
@Order(value=n) //对于多个该配置的情况，设置执行顺序。n越小，越先执行。
@Component
public class ApplicationStartupRunner implements CommandLineRunner { }
```

> 匹配后缀访问

```java
@Configuration //boot2.x默认将 '/test' 和 '/test.do' 作为2个url
public class MyWebMvcConfigurer implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseRegisteredSuffixPatternMatch(true); //true，统一以上两个url
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean(DispatcherServlet dispatcherServlet) {
        ServletRegistrationBean<DispatcherServlet> bean = new ServletRegistrationBean<>(dispatcherServlet);
        bean.addUrlMappings("*.do"); //拦截'.do'结尾的url
        return bean;
    }
}
```





## Actuator

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

> 常用端点

```sh
(*)mappings     #描述全部的URI路径，以及它们和控制器(包含Actuator端点)的映射关系
(*)metrics      #报告各种应用程序度量信息，比如内存用量和HTTP请求计数
(*)loggers      #显示和修改应用程序中的loggers配置
(*)env          #获取全部环境属性

health          #报告应用程序的健康指标，这些值由 HealthIndicator 的实现类提供
info            #显示配置文件中以 'info' 打头的属性
auditevents     #审计事件
beans           #应用程序上下文里全部的Bean,以及它们的关系
conditions      #自动配置报告,记录哪些自动配置条件通过了,哪些没通过
configprops     #描述配置属性(包含默认值)如何注入Bean
threaddump      #获取线程活动的快照
scheduledtasks  #定时任务
httptrace       #跟踪 HTTP 请求-响应交换的情况
```

> 暴露端点

```properties
#Boot2.x 默认只暴露两个端点: health 和 info
management.endpoints.web.exposure.include=*   #暴露所有
management.endpoints.web.exposure.exclude=env #不暴露: env

#访问端点
http://localhost:8090/demo/actuator        --> 返回所有已暴露的端点
http://localhost:8090/demo/actuator/health --> 访问health端点
```

> 动态修改日志级别 `必须 logback`

```properties
#所有模块的日志级别
http://127.0.0.1:8090/demo/actuator/loggers
#具体模块的日志级别
http://127.0.0.1:8090/demo/actuator/loggers/com.example.controller
```

```properties
#发送 POST 请求到以上路径，动态修改以上模块的日志级别为 DEBUG，成功状态码为 '204'
POST - 请求体: {"configuredLevel": "DEBUG"} - Content-Type: application/json
```

## Admin

> 客户端（被监控者）：基于 Actuator 的可视化 WebUI

```xml
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-client</artifactId>
    <version>2.1.3</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

```properties
management.endpoints.web.exposure.include=*
spring.boot.admin.client.url=http://localhost:9090/hello
```

```java
@Configuration
public class SecurityPermitAllConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()      //Security权限，授权的固定格式
            .anyRequest().permitAll() //所有请求，所有权限都可以访问
            .and().csrf().disable();  //固定写法：使 csrf()拦截失效
    }
}
```

> 服务端

```xml
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>spring-boot-admin-starter-server</artifactId>
    <version>2.1.3</version>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

```java
@EnableAdminServer //全局注解
```

## war

> 修改打包方式

```xml
<groupId>com.example</groupId>
<artifactId>amqp_publisher</artifactId>
<version>0.0.1-SNAPSHOT</version>
<packaging>war</packaging> <!-- war -->
```

> 移除自带的嵌入式Tomcat

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

> 添加 servlet-api

```xml
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>3.1.0</version>
    <scope>provided</scope> <!--该依赖参与编译，测试，运行，但不会被打进项目包。由容器tomcat提供-->
</dependency>
```

> 修改启动类

```java
@MapperScan(value = "com.example.*.mapper")
@SpringBootApplication
public class Application extends SpringBootServletInitializer { //新增 extends

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override //新增方法
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return super.configure(builder);
    }
}
```

## email

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

```properties
#邮箱开启SMTP功能: https://blog.csdn.net/caimengyuan/article/details/51224269
#授权码作为密码使用
spring.mail.host=smtp.163.com
spring.mail.username=***@163.com
spring.mail.password=***
```

> 邮件：普通 + 附件 + 静态资源 + 模板

```java
@RestController
@RequestMapping("mail")
public class MailController {
    private static final String EMAIL_FROM = "dongyan3131@163.com";
    private static final String EMAIL_TO = "453705197@qq.com";
    private static final String EMAIL_SUBJECT = "主题：邮件主题";

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    //普通邮件
    @GetMapping("/simple")
    public void simpleEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(EMAIL_FROM);
        message.setTo(EMAIL_TO);
        message.setSubject(EMAIL_SUBJECT);
        message.setText("内容：邮件内容");

        mailSender.send(message);
    }

    //三种复杂邮件
    @GetMapping("/attach")
    public void attachEmail() throws Exception {

        // 含附件，静态资源，模板，则增加第二个参数，并为true
        MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(EMAIL_FROM); // 发送方
        helper.setTo(EMAIL_TO); // 接收方
        helper.setSubject(EMAIL_SUBJECT); //主题

        //（1）附件
        File file = new File(SystemUtils.getFilePath(), "/logs/sm/sm.log");//附件位置
        if (file.exists()) {
            String path = file.getPath();
            String fileName = path.substring(path.lastIndexOf(File.separator) + 1);
            helper.addAttachment(fileName, file); //添加附件(附件名,附件路径)
        }

        //（2）静态资源 -> 在邮件正文中查看图片,而非附件
        String sb = "<h1>大标题-h1</h1>" +
            "<p style='color:#F00'>红色字</p>" +
            "<p style='text-align:right'>右对齐</p>" +
            "<p><img src=\"cid:weixin\"></p>";
        helper.setText(sb, true); //true表示启动HTML格式的邮件
        file = new File(SystemUtils.getFilePath(), "/imgs/a.jpg");
        if (file.exists()) {
            // 注意: 资源名称"weixin" 需要与正文中 cid:weixin 对应起来
            helper.addInline("weixin", file);
        }

        //（3）模板邮件 -> 固定的场景,如重置密码、注册确认等,只有小部分是变化的
        org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
        context.setVariable("username", "skyl");
        String content = templateEngine.process("email", context);
        helper.setText(content, true);

        mailSender.send(mimeMessage);
    }
}
```






# thymeleaf

##基础概念

> 模板引擎：`将后台数据 填充 到前台模板的表达式中！`

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
```

```properties
#开发阶段关闭 thymeleaf 模板缓存，CTRL+F9
spring.thymeleaf.cache=false
```

```html
<html lang="en" xmlns:th="http://www.thymeleaf.org"> <!--命名空间-->
```

> 存放位置

```sh
模板文件存放位置：'src/main/resources/templates'
该目录下的内容不允许外界直接访问，必须经过服务器的渲染，所以是安全的。
```

## 基础语法

> `~{...}` 片段引用表达式

> `@{...}` 定义URL

```html
<!--http://ip:8080/order/details/3-->
<a href="emp" th:href="@{/details/}+${emp.id}">传参-restful</a>

<!--http://ip:8080/order/details?orderId=3-->
<a th:href="@{/details(orderId=${o.id})}">传参</a>

<!--http://ip:8080/order/3/details?orderId=3&orderName=li -->
<a th:href="@{/{orderId}/details(orderId=${o.id}, orderName=${o.name})}">传多参</a>
```

> `${...}` 变量取值

```html
(1).获取对象的属性，调用方法：${person.name}
(2).使用内置的基本对象：${! #strings.isEmpty(msg)}
(3).内置的一些工具对象
```

> `#{...}` 用于获取 properties 文件内容，常用于【国际化】场景

```html
home.welcome=this messages is from home.properties! <!--properties文件-->

<p th:text="#{home.welcome}">This text will not be show!</p> <!--读取配置文件中的 home.welcome-->
```

> `*{...}` 类似`${...}`功能，配合`th:object`使用，获取指定对象的变量值

```html
<div> <!--(1).类似${}功能-->
    <p>Name: <span th:text="*{session.user.name}">Sebastian</span>.</p>
    <p>Surname: <span th:text="*{session.user.surname}">Pepper</span>.</p>
    <p>Nationality: <span th:text="*{session.user.nationality}">Saturn</span>.</p>
</div>
```

```html
<div> <!--(2-1).原始表达式-->
    <p>Name: <span th:text="${session.user.firstName}">Sebastian</span>.</p>
    <p>Surname: <span th:text="${session.user.lastName}">Pepper</span>.</p>
    <p>Nationality: <span th:text="${session.user.nationality}">Saturn</span>.</p>
</div>
```

```html
<div th:object="${session.user}"> <!--(2-2).指定对象-->
    <p>Name: <span th:text="*{firstName}">Sebastian</span>.</p>
    <p>Surname: <span th:text="*{lastName}">Pepper</span>.</p>
    <p>Nationality: <span th:text="*{nationality}">Saturn</span>.</p>
</div>
```

```html
<div th:object="${session.user}"> <!--(2-3).混合使用-->
    <p>Name: <span th:text="*{firstName}">Sebastian</span>.</p> <!--指定对象-->
    <p>Surname: <span th:text="${session.user.lastName}">Pepper</span>.</p> <!--上下文变量取值-->
    <p>Nationality: <span th:text="${#object.nationality}">Saturn</span>.</p> <!--从 #object 中获取属性-->
</div>
```

## 常用语法

> `th:value` 可以将一个值放入到 input 标签的 value 中

> `th:href`GET请求

```html
<a th:href="@{/emp}">用户添加</a> <!--发送get请求到 '/项目名/emp'-->
```

> `th:src`图片类地址引入

```html
<img alt="app-logo" th:src="@{/img/logo.png}" />
```

> `th:id`动态指定id属性

```html
<div th:id = "stu+(${rowStat.index}+1)" class="student"></div>
```

> `th:each`

```html
<!--每次遍历都会产生当前标签<tr>-->
<tr th:each="item : ${list}">
    <td th:text="${item.name}">name</td>
    <td>[[${item.price}]]</td>
    <td>[(${item.production})]</td>
    <td th:text="${itemStat.odd}?'odd':${item.memo}">memo</td>
</tr>
```

```sh
#(1).itemStat 称作状态变量，属性有:
index       -> 当前迭代对象的索引。（从0开始）
count       -> 当前迭代对象的计数。（从1开始）
size        -> 迭代集合的长度
current     -> 当前迭代变量
even/odd    -> 布尔值，当前循环是否是偶数/奇数。(从0开始）
first/last  -> 布尔值，当前循环是否是第一个/最后一个

#(2).用法：${itemStat.index}; ${itemStat.odd}; ${itemStat.current.name}
#(3).在写 th:each="obj,objStat:${objList}"，可不写 objStat，自动添加，默认命名 objStat（如itemStat）
```

> `th:text`

```html
<div th:text="${emp.name}">将被替换</div> <!--一般写法-->

<div>[[${emp.name}]]</div>               <!--行内写法-->
```

```html
<!--[[...]] & [(...)]-->
<!--前者，会转义，等同于 th:text-->
<p>The message is "[[${msg}]]"</p>
<p>The message is "This is &lt;b&gt;great!&lt;/b&gt;"</p>

<!--后者，不会转义，等同于 th:utext-->
<p>The message is "[(${msg})]"</p>
<p>The message is "This is <b>great!</b>"</p>
```

> `th:action` 表单提交的地址

```html
<!--对于操作符'@'，模板解析时会自动加上 'context-path' 作为前缀-->
<form th:action="@{/batch/upload}" method="post" enctype="multipart/form-data">
    File: <input type="file" name="file"><br>
    Desc: <input type="text" name="desc"><br>
    <input type="submit" value="提交">
</form>
```

> `th:selected` 选择框，选中情况

```html
<td>住址:</td>
<td>
    <select name="city.id"> <!--th:text 用于显示; th:value 用于存值-->
        <option th:each="city : ${cityList}" th:value="${city.id}" th:text="${city.name}"
                th:selected="${null!=person}?${person.city.id}==${city.id}">
        </option> <!--th:selected 回显对象 person.city.id 和遍历 city.id 相同，则选中-->
    </select>
</td>
```

```html
<td>
    <select name="city.id"> <!--简化写法 th:object-->
        <option th:each="city : ${cityList}" th:object="${city}" th:value="*{id}" th:text="*{name}"
                th:selected="${null!=person}?${person.city.id}==*{id}">
        </option>
    </select>
</td>
```

> `th:if` 条件判断

```html
<p th:if="${! #strings.isEmpty(msg)}" th:text="${msg}"></p> <!--msg不为空,则显示<p>-->
```

```sh
#th:if="${xx}" 表达式为 true 的各种情况：
boolean xx =true;
int xx !=0;
character xx !=0;
String xx !="false","off","no";
If xx is not a boolean, a number, a character or a String
```

##其他语法

> `th:switch`多路选择，配合使用 `th:case`

```html
<div th:switch="${user.role}">
    <p th:case="'admin'">超级管理员</p>
    <p th:case="#{roles.manager}">普通管理员</p>
    <p th:case="*">其他</p> <!--默认值-->
</div>
```

> `th:with` 变量赋值运算

```html
<div th:with="first=${persons[0]}"> <!--th:with="x=${y}"-->
    <p>The name of the first person is 
        <span th:text="${first.name}">Julius Caesar</span>
    </p>
</div>
```

> `th:attr` 设置标签属性，多个属性用逗号分隔

```html
th:attr="src=@{/image/aa.jpg},title=#{logo}" <!--一般用于自定义标签-->
```

> `th:remove` 删除某个属性

```html
<tr th:remove="all"> <!--all：删除包含标签和所有的孩子-->
```

> `th:errors` 数据校验相关，用于显示数据校验的错误信息

>`日期格式化`

```html
<input type="text" name="birth" placeholder="birth day" 
       th:value="${#dates.format(emp.birth, 'yyyy-MM-dd HH:mm:ss')}">

<!--格式化日期，默认以浏览器默认语言为格式化标准-->
${#dates.format(key)}
${#dates.format(key,'yyy/MM/dd')}

<!--按照自定义的格式做日期转换（取值年月日）-->
${#dates.year(key)}
${#dates.month(key)}
${#dates.day(key)}
```

>`字符串相关`

```html
<!--调用内置对象一定要用 # -->
<!--大部分的内置对象都以s结尾：strings，numbers，dates-->

<!--判断字符串是否为空，如果为空返回 true，否则返回 false-->
${#strings.isEmpty(key)}

<!--判断字符串是否包含指定的子串，如果包含返回 true，否则返回 false-->
${#strings.contains(msg, 'T')}

<!--判断当前字符串是否以子串开头，如果是返回 true，否则返回 false-->
${#strings.startsWith(msg, 'a')}
${#strings.endsWith(msg, 'a')}

<!--返回字符串的长度-->
${#strings.length(msg)}

<!--查找子串的位置，并返回该子串的下标，如果没找到则返回 -1 -->
${#strings.indexOf(msg, 'h')}

<!--截取子串，用法与 jdk String 类下 SubString 方法相同-->
${#strings.substring(msg, 13)}
${#strings.substring(msg, 13, 15)}

<!--字符串转大小写-->
${#strings.toUpperCase(msg)}
${#strings.toLowerCase(msg)}
```

>`页面公共元素抽取`

```html
<!--（1）抽取公共片段-->
<footer th:fragment="copy">
    &copy; 2011 The Good Thymes Virtual Grocery
</footer>
```
```html
<!--（2）对于一般写法可以省略 ~{}; 但是，对于行内写法不可省！ [[~{x::y}]] 或 [(x::y)]-->
~{templatename::selector}     <!--模板名::选择器-->
~{templatename::fragmentname} <!--模板名::片段名-->
```
```html
<!--（3）不同引入-->
<div th:insert="footer :: copy"></div>  <!--footer为文件名: footer.html-->

<div th:replace="footer :: copy"></div>

<div th:include="footer :: copy"></div> <!--已过时-->
```
```html
<!--（4）不同效果 -->
<div> <!--insert - div内层才是公共元素-->
    <footer>
        &copy; 2011 The Good Thymes Virtual Grocery
    </footer>
</div>

<footer> <!--replace - 公共元素替换div-->
    &copy; 2011 The Good Thymes Virtual Grocery
</footer>

<div> <!--include - 公共元素-->
    &copy; 2011 The Good Thymes Virtual Grocery
</div>
```

>`引入页面公共元素时传参`（两种方式？）

```html
<!--（1）引用fragment时传入'参数名和参数值'-->
<div th:replace="comm/bar :: sidebar(activeUri='main.html')"></div> <!--主页面-->

<div th:replace="comm/bar :: sidebar(activeUri='logDir')"></div> <!--目录页面-->

<!--（2）定义fragment时指定'参数名'-->
<nav th:fragment="sidebar(activeUri)">

<!--（3）引用fragment时传入'参数值'-->
<div th:replace="comm/bar :: sidebar('main.html')"></div>

<div th:replace="comm/bar :: sidebar('logDir')"></div>

<!--（4）根据传入参数值，改变a的class属性-->
<!--activeUri为 'main.html' 时，高亮-->
<a th:href="@{/main.html}" th:class="${activeUri=='main.html'?'nav-link active':'nav-link'}">

<!--activeUri为 'logDir' 时，高亮-->
<a th:href="@{/logDir}" th:class="${activeUri=='logDir'?'nav-link active':'nav-link'}">>
```

>`域对象操作`

```html
<!--HttpServletRequest-->
<!--request.setAttribute("req", "hello");-->
Request: <span th:text="${#httpServletRequest.getAttribute('req')}"></span><br/>
```

```html
<!--HttpSession-->
<!--request.getSession().setAttribute("sess", "world");-->
Session: <span th:text="${session.sess}"></span><br/>
```

```html
<!--ServletContext-->
<!--request.getServletContext().setAttribute("app", "java");-->
Application: <span th:text="${application.app}"></span>
```
> `#maps`  工具对象表达式

```html
#dates #calendars #numbers #strings #objects #bools #arrays #lists #sets

<!--有msg对象则显示<p>; 反之不显示-->
<p style="color:red" th:text="${msg}" th:if="${not #strings.isEmpty(msg)}" />
```

# WebSocket

## 基础概念

> 简介

```sh
B/S 结构的软件项目中有时客户端需要实时的获得服务器消息，但默认HTTP协议只支持 请求响应模式。
对于这种需求可以通过 polling，Long-polling，长连接，Flash-Socket，HTML5中定义的WebSocket 完成。

HTTP模式可以简化Web服务器，减少服务器的负担，加快响应速度，因为服务器不需要与客户端长时间建立一个通信链接。
但不容易直接完成实时的消息推送功能（如聊天室，后台信息提示，实时更新数据等）。

应用程序通过 Socket 向网络发出请求或者应答网络请求。Socket 可以使用TCP/IP协议或UDP协议。
```

```sh
TCP协议：面向连接的，可靠的，基于字节流的传输层通信协议，负责数据的可靠性传输问题。
UDP协议："无连接，不可靠"，基于报文的传输层协议，优点：发送后不用管，速度比TCP快。

HTTP协议："无状态协议"，通过 Internet 发送请求消息和响应消息，默认使用80端口。（底层Socket）
```

> Http协议

```sh
HTTP 协议原本是设计用于传输简单的文档和文件，而非实时的交互。

根据 HTTP 协议，一个客户端如浏览器，向服务器打开一个连接，发出请求，等待回应，之后关闭连接。
如果客户端需要更多数据，则需要打开一个新连接，以此循环往复。如果服务器有了新的信息，它必须等待客户端发出请求而不是立即发送消息。

那么要看到页面中要展示信息的最新情况，应该怎么办？不断刷新！

缺点：这种方式现在已经被完全淘汰，发送了很多不必要的请求，浪费大量带宽，页面不断刷新，用户体验差，
而且做不到真正的实时，服务端有了新数据也不能立马推送给客户端，使得秒级的实时信息交互难以实现。

HTTP协议决定了服务器与客户端之间的连接方式，无法直接实现消息推送（F5已坏），一些变相的解决办法：
```

> 双向通信

```sh
Websocket：Html5 提供的一种通过 js 与远程服务器建立连接，从而实现客户端与服务器间双向的通信。
优点：事件驱动，异步，使用ws或者wss协议的客户端socket，能够实现真正意义上的推送功能。
缺点：少部分浏览器不支持，浏览器支持的程度与方式有区别
```

## 客户端

```html
<body>
    <input id="text" type="text"/>
    <button onclick="send()">Send</button>
    <div id="message"></div>

    <script>
        var websocket = null;
        if ('WebSocket' in window) {
            websocket = new WebSocket("ws://localhost:9005/qrcode/webSocket"); //注意大小写
        } else {
            alert("浏览器不支持WebSocket!")
        }

        //连接建立
        websocket.onopen = function (event) {
            console.log("连接建立: " + event);
        };

        //连接关闭
        websocket.onclose = function (event) {
            console.log("连接关闭: " + event);
            websocket.send(event.code);
        };

        //连接错误
        websocket.onerror = function (event) {
            setMessageInnerHTML("连接错误: " + event);
        };

        //监听事件 -> 监听窗口关闭事件
        //当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常
        window.onbeforeunload = function () {
            if (null != websocket) {
                websocket.close();
            }
        };

        //接收到服务器发来消息
        websocket.onmessage = function (event) {
            console.log("接收到服务器发来消息: " + event.data);
            setMessageInnerHTML(event.data);
        };

        //将消息显示在网页上
        function setMessageInnerHTML(innerHTML) {
            document.getElementById('message').innerHTML += innerHTML + '<br/>';
            // $('#message').html(innerHTML);
        }

        //向远程服务器发送数据
        function send() {
            var message = document.getElementById('text').value;
            websocket.send(message);
            // websocket.send($('#text'));
        }
    </script>
</body>
```

## 服务端

````xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
````

```java
@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
```

```java
@Slf4j
@Component
@ServerEndpoint("/webSocket")
public class Websocket {

    //区别: 静态变量 和 非静态变量
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    // 旧版：concurrent包的线程安全Set，用来存放每个客户端对应的 MyWebSocket 对象
    private static final Set<Websocket> webSocketSet = new CopyOnWriteArraySet<>();

    //客户端注册时调用
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);
        log.info("【WebSockt消息】 有连接建立，总连接数: {}", webSocketSet.size());
    }

    //客户端关闭
    @OnClose
    public void onClose(Session session, CloseReason reason) {
        webSocketSet.remove(this);
        log.info("【WebSockt消息】 有连接断开，总连接数: {}", webSocketSet.size());
    }

    //客户端异常
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("【WebSockt消息】 有连接异常: {}", error);
    }

    //收到浏览器客户端消息后调用的方法
    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("【WebSockt消息】 收到客户端消息: {}", message);

        // sendMsg2One("服务器->客户端: " + message);
        sendMsg2All("服务器->客户端: " + message);
    }

    //群发消息 --> 可供外部调用
    public void sendMsg2All(String message) {
        webSocketSet.forEach(websocket -> websocket.sendMsg(message));
    }

    //点对点发送消息
    public void sendMsg2One(String message) {
        sendMsg(message);
    }

    //实现服务器主动推送
    private void sendMsg(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
            // this.session.getAsyncRemote().sendText(message);
        } catch (Exception e) {
            log.error("【WebSockt消息】 向客户端发送消息异常: {}", e);
        }
    }

    //更高级的注解，MaxMessageSize 属性可以被用来定义消息字节最大限制，
    //在示例程序中，如果超过6个字节的信息被接收，就报告错误和连接关闭。
    // @Message(maxMessageSize = 6)
    // public void receiveMessage(String s) {}
}
```

# 上传下载

## 文件上传

> 前台页面

```sh
'form表单'： POST + enctype="multipart/form-data"
'file标签'：必须要有 name 属性
'后台使用'： MultipartFile 接收文件资源
```

```html
<form action="/upload" method="post" enctype="multipart/form-data">
    File0: <input type="file" name="file"><br>
    <!-- File1: <input type="file" name="file"><br> --> <!-- 多文件上传，name必须一致 -->
    Desc: <input type="text" name="desc"><br>
    <input type="submit" value="提交">
</form>
```

> 单个上传

```java
@PostMapping("/upload")
public void upload(@RequestParam("file") MultipartFile file,
                   @RequestParam("desc") String desc) {
    log.info("文件名: {}. 文件大小: {}. 文件描述: {}", file.getOriginalFilename(), file.getSize(), desc);
    file.transferTo(new File(getUploadDir(), file.getOriginalFilename())); //文件另存
}
```

```java
public File getUploadDir() throws FileNotFoundException {
    File dir = new File(ResourceUtils.getURL("").getPath(), "/upload"); //项目根目录下
    if (!dir.exists()) {
        dir.mkdirs(); //创建当前及父目录.(区别于 mkdir())
    }
    return dir;
}
```

> 批量上传

```java
@PostMapping("/uploads")
public void batchUplocad(@RequestParam("file") List<MultipartFile> files,
                         @RequestParam("desc") List<String> descs) {
    files.forEach(x -> x.transferTo(getUploadDir(), x.getOriginalFilename())); //文件另存 
    descs.forEach(log::info); //文件描述
}
```

> 相关配置

```properties
#单个上传文件的大小
spring.servlet.multipart.max-file-size=10MB
#一次请求上传文件的总容量
spring.servlet.multipart.max-request-size=20MB
```

## 文件下载

> 文件下载一般都借助于以下两个 `响应头` 达到效果

```sh
'Content—Type'：告知浏览器当前的响应体是什么类型的数据
---------------当为'application/octet-stream'时，就说明 body 里是一堆不知道是啥的二进制数据

'Content—Disposition'：用于向浏览器提供一些关于如何处理响应内容的额外的信息，同时也可以附带一些其它数据
----------------------比如，在保存响应体到本地的时候应该使用什么样的文件名
```

> 代码实现

```java
@GetMapping("/{name}")
public void download(@PathVariable String name, HttpServletResponse resp) {
    try (InputStream in = new FileInputStream(new File(getUploadDir(), name));
         OutputStream out = resp.getOutputStream()) {
        resp.setContentType("application/x-download");
        resp.addHeader("Content-Disposition", "attachment;filename=" + name); //注意中文乱码
        IOUtils.copy(in, out);
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

# 错误处理

## 错误页面

> 4xx，5xx 错误页面

```sh
在项目的 /static 目录下新建页面：'/error/4xx.html'和'/error/5xx.html'
```

## 异常捕获

> 问题需求

```sh
#当前问题
当程序出现错误，如获取值为空或出现异常时，并不希望用户看到异常的具体信息，而是希望对对应的错误和异常做相应提示
在MVC框架中很多时候会出现执行异常，那我们就需要加 try/catch 进行捕获，如果 service 层和 controller 层都加上，那就会造成代码冗余

#解决方法
编程时，先进行参数校验，有问题则'直接抛出异常'；没问题则继续执行具体的业务操作，最后返回成功信息。
在'异常处理类'中捕获异常，统一处理，向用户返回规范的响应信息（如 json），无需在代码中 try/catch。

#处理流程
自定义异常类型，错误码，以及错误信息。
对于可预知的异常由程序员在代码中（controller、service、dao）主动抛出；框架异常则由框架自行抛出
在'异常处理类'中，统一处理异常：对于程序员抛出的异常，捕捉自定义异常处理。对于框架异常，则捕捉 Exception 处理。
```
> 异常处理类

```java
@Slf4j
@ControllerAdvice
// @RestControllerAdvice
public class ExceptionConfig {

    @ResponseBody //返回 json 数据，默认返回错误页面
    @ResponseStatus(HttpStatus.BAD_REQUEST) //自定义响应状态码：400，默认 200
    @ExceptionHandler(UserException.class)
    public ResultVO userException(UserException e) {
        log.error("【异常处理类】处理异常: UserException");
        return ResultVOUtil.fail(e.getResultEnum());
    }

    @ResponseBody
    @ExceptionHandler(CustomException.class)
    public ResultVO customException(CustomException e) {
        log.error("【异常处理类】处理异常: CustomException");
        return ResultVOUtil.fail(e.getResultEnum());
    }

    @ExceptionHandler(Exception.class) //捕获未知错误，返回 404 页面
    public String exception(Exception e) {
        log.error("【异常处理类】处理异常: Exception");
        return "/error/404";
    }
}
```

> 异常处理の优先级

```sh
#查找-优先级
当执行过程中出现异常，首先在本类中查找 @ExceptionHandler 标识的方法。
找不到，再去查找 @ControllerAdvice 标识类中的 @ExceptionHandler 标识方法来处理异常。

#继承-优先级
例如发生异常 NullPointerException; 但是声明的异常有 RuntimeException 和 Exception
此时，根据异常的最近继承关系，找到继承深度最浅的那个，即 RuntimeException 的声明方法
```

> 异常处理の方法的常用参数

```java
/**
 * @param e 异常参数(包括自定义异常);
 *          请求或响应对象(HttpServletRequest; ServletRequest; PortleRequest/ActionRequest/RenderRequest)
 *          Session对象(HttpSession; PortletSession)
 *          WebRequest; NativeWebRequest; Locale;
 *          InputStream/Reader; OutputStream/Writer; Model
 * @return  ModelAndView; Model; Map; View; String; @ResponseBody; HttpEntity<?>或ResponseEntity<?>; 以及void
 */
```
# CRUD

## 基础概念

> restful

```sh
#http请求的安全和幂等，是指多次调用同一个请求对资源状态的影响
'安全' ---> 请求不会影响资源的状态。只读的请求：GET，HEAD，OPTIONS
'幂等' ---> 多次相同的请求，效果一致
```

```sh
GET    /crud/list  查询员工列表  -> 只是请求，不改变资源状态                   #安全，幂等
POST   /crud/emp   新增一个员工  -> 多次请求会新增多条相同的数据                #不安全，不幂等
PUT    /crud/emp   更新员工信息  -> 多次请求都是将id为 5 的员工姓名修改成'wang'  #不安全，幂等
DELETE /crud/{id}  删除员工信息  -> 多次请求目的都是删除id为 5 的员工           #不安全，幂等
```

```sh
#第一次成功删除，第二次及以后虽资源已不存在，但也得返回 200 OK，不能返回 404

GET    /crud/emp  跳转新增页面
GET    /crud/{id} 跳转更新页面
```

> GET POST

```sh
get ：默认方式，用于获取资源，请求参数在url上可见，不安全。
post：用于请求资源，
```

> `POST 转化为 PUT DELETE`

```xml
<!--（1）配置 HiddenHttpMethodFilter，SpringBoot 默认已配置-->
<filter>
    <filter-name>HiddenHttpMethodFilter</filter-name>  
    <filter-class>org.springframework.web.filter.HiddenHttpMethodFilter</filter-class>  
</filter>
```

```html
<!--（2）页面创建（POST表单 + 隐藏标签）-->
<form method="post" th:action="@{/emp/}+${emp.id}">
    <input type="hidden" name="_method" value="delete"> <!--隐藏标签 name + value-->
    <a href="#" onclick="delEmp(this)" th:attr="url=@{/emp/}+${emp.id}">删除</a>
</form>
```
## 后台逻辑

> 列表：超链接跳转

```java
//员工列表页面
@GetMapping("/list")
public String list(Model model) {
    List<Emp> emps = empMapper.selectList(null); //使用 mybatis-plus
    List<EmpVO> empVOs = emps.stream().map(emp -> {
        Long cityId = emp.getCityId();
        City city = cityMapper.selectById(cityId);

        EmpVO empVO = new EmpVO();
        BeanUtils.copyProperties(emp, empVO);
        empVO.setCity(city);
        return empVO;
    }).collect(Collectors.toList());
    log.info("empVOs: {}", JSON.toJSONString(empVOs, true));

    model.addAttribute("emps", empVOs);
    return "/emps/list";
}
```

> 新增：超链接跳转

```java
//跳转新增页面
@GetMapping("/emp")
public String toAdd(Model model) {
    List<City> cities = cityMapper.selectList(null);
    log.info("city: {}", JSON.toJSONString(cities, true));

    model.addAttribute("citys", cities); //初始化新增页面：城市列表
    return "/emps/emp";
}
```

```java
//新增员工，跳转列表页面
@PostMapping("/emp")
public String addOne(/*@RequestBody*/ EmpVO empVO, Model model) { //表单提交不能用 @RequestBody
    Emp emp = new Emp();
    BeanUtils.copyProperties(empVO, emp);
    emp.setCityId(empVO.getCity().getId());
    int insert = empMapper.insert(emp);
    log.info("insert: {}", JSON.toJSONString(emp, true));

    return "redirect:/crud/list"; //重定向，/代表站点根目录
}
```

> 更新：超链接跳转

```java
//跳转修改页面
@GetMapping("/{id}")
public String toUpdate(@PathVariable Long id, Model model) {
    Emp emp = empMapper.selectById(id);
    City city = cityMapper.selectById(emp.getCityId());
    EmpVO empVO = new EmpVO();
    BeanUtils.copyProperties(emp, empVO);
    empVO.setCity(city);
    log.info("empVO: {}", JSON.toJSONString(empVO, true));

    List<City> citys = cityMapper.selectList(null);

    model.addAttribute("emp", empVO);   //用于修改回显 
    model.addAttribute("citys", citys); //初始化修改页面：城市列表
    return "/emps/emp";
}
```

```java
//修改员工，跳转列表页面
@PutMapping("/emp")
public String update(EmpVO empVO) {
    Emp emp = new Emp();
    BeanUtils.copyProperties(empVO, emp);
    emp.setCityId(empVO.getCity().getId());
    int update = empMapper.updateById(emp);
    log.info("update: {}", JSON.toJSONString(emp, true));

    return "redirect:/crud/list";
}
```

>删除（1）：表单提交删除 `POST 转 DELETE`

```html
<a href="#" onclick="deleteByForm(this)" th:attr="url=@{/crud/}+${emp.id}">表单删除</a>
```

```html
<form id="deleteForm" method="post" action="#">
    <input type="hidden" name="_method" value="DELETE">
</form>
```

```javascript
function deleteByForm(e) {
    //$(e).attr('url')      --> 获取url属性
    //$(e).attr('url', xxx) --> 为url属性赋值
    $('#deleteForm').attr('action', $(e).attr('url')).submit();

    return false; //取消<a>的默认行为
}
```

```java
//删除员工，跳转列表页面
@DeleteMapping("/{id}")
public String delete(@PathVariable Long id) {
    int delete = empMapper.deleteById(id);

    return "redirect:/crud/list";
}
```

> 删除（2）：ajax异步删除

```html
<a href="#" onclick="deleteByAjax(this)" th:attr="url=@{/crud/}+${emp.id}">ajax删除</a>
```

```javascript
function deleteByAjax(e) {
    $.ajax({
        type: 'delete',
        url: $(e).attr('url'),
        dataType: 'text',
        success: function (data) {
            //e表示<a>, parent表示<td>, 再parent表示<tr>
            $(e).parent().parent().remove();
            alert(data);
        }
    });
    return false;
}
```

```java
//ajax异步删除员工
@ResponseBody
@DeleteMapping("/{id}")
public String delete(@PathVariable Long id) {
    int delete = empMapper.deleteById(id);

    return "SUCCESS";
}
```

## 前台页面

> 存放于 `\templates\emps\` 的 `list.html + emp.html`

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>列表页面</title>

    <script th:src="@{/webjars/jquery/jquery.min.js}"></script>
    <script th:src="@{/webjars/bootstrap/js/bootstrap.min.js}"></script>
    <link th:href="@{/webjars/bootstrap/css/bootstrap.min.css}"/>

    <script>/*单独叙述*/</script>
</head>
<body>
<table>
    <tr>
        <th>姓名</th>
        <th>年龄</th>
        <th>城市</th>
        <th>操作</th>
    </tr>
    <tr th:if="${null==emps || 0==emps.size()}">
        <td colspan="4" th:text="员工列表为空"></td>
    </tr>
    <tr th:each="emp:${emps}" th:object="${emp}"> <!--th:object 和 *{...} 配合使用-->
        <td th:text="${emp.name}"></td>
        <td th:text="*{gender}?'男':'女'"></td>
        <td th:text="*{city.name}"></td>
        <td>
            <a th:href="@{/crud/}+*{id}">修改</a> <!--路径拼接-->
            <a href="#" onclick="deleteByForm(this)" th:attr="url=@{/crud/}+${emp.id}">删除</a>
            <a href="#" onclick="deleteByAjax(this)" th:attr="url=@{/crud/}+${emp.id}">删除</a>
        </td>
    </tr>
</table>
<a th:href="@{/crud/emp}">新增员工</a>

<form id="deleteForm" method="post" action="#">
    <input type="hidden" name="_method" value="DELETE">
</form>
</body>
</html>
```

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>员工信息页</title>
</head>
<body>
<form method="post" th:action="@{/crud/emp}">
    <!--新增和修改使用同一页面，区分方式：回显 emp 是否为空 ${null!=person}-->
    <input type="hidden" name="_method" value="put" th:if="${null!=emp}">
    <!--修改：PUT请求 + emp.id-->
    <input type="hidden" name="id" th:value="${emp.id}" th:if="${null!=emp}">

    <table>
        <tr>
            <td>姓名：</td>
            <td><input type="text" name="name" th:value="${null!=emp}?${emp.name}"></td>
        </tr>
        <tr>
            <td>性别：</td>
            <td>
                <!--th:checked radio标签是否选中-->
                <input type="radio" name="gender" value="1" th:checked="${null!=emp}?${1==emp.gender}">男
                <input type="radio" name="gender" value="0" th:checked="${null!=emp}?${1!=emp.gender}">女
            </td>
        </tr>
        <tr>
            <td>住址：</td>
            <td>
                <select name="city.id">
                    <!--th:selected 回显emp.city.id == 遍历city.id，则选中-->
                    <option th:each="city:${citys}" th:object="${city}" th:value="*{id}" th:text="*{name}"
                            th:selected="${null!=emp}?${emp.city.id}==*{id}"></option>
                </select>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <!--回显 emp 为空，则显示'新增'；否则显示'修改'-->
                <input type="submit" th:value="${null==emp}?'新增':'修改'">
            </td>
        </tr>
    </table>
</form>
</body>
</html>
```

