[TOC]

# 基础功能

##pom

>SpringBoot 并不是对 Spring 功能上的增强，而是提供了一种快速使用 Spring 的方式

```xml
<dependencies>
    <dependency>
        <groupId>org.mybatis.spring.boot</groupId>
        <artifactId>mybatis-spring-boot-starter</artifactId>
        <version>1.3.2</version>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <scope>runtime</scope> <!--只在运行时使用-->
    </dependency>
    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>druid-spring-boot-starter</artifactId>
        <version>1.1.10</version>
    </dependency>

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <scope>provided</scope>
        <optional>true</optional> <!--依赖不会传递，依赖该项目的项目需要重新引入该依赖-->
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope> <!--只在运行时使用-->
        <optional>true</optional> <!--只在当前项目生效，不传递-->
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope> <!--只在测试时使用-->
    </dependency>
</dependencies>

<build>
    <finalName>demo-user</finalName> <!--配置项目打包名-->
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
    <resources> <!--资源拷贝插件-->
        <resource>
            <directory>src/main/java</directory>
            <includes>
                <include>**/*.xml</include>
            </includes>
        </resource>
        <resource>
            <directory>src/main/resources</directory>
            <includes>
                <include>**/*.xml</include>
                <include>**/*.properties</include>
            </includes>
        </resource>
    </resources>
</build>
```

> 额外配置

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <executable>true</executable> <!--打成的jar包可直接运行-->
    </configuration>
</plugin>
```

##yml

>application.properties

```properties
server.port=8090
server.servlet.context-path=/demo
#微服务项目名称：以横岗-分割
#spring.application.name=demo-base

#spring.profiles.active=dev

spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://127.0.0.1:33306/webpark?useSSL=false&serverTimezone=GMT%2B8
spring.datasource.username=bluecardsoft
spring.datasource.password=#$%_BC13439677375

#mybatis
mybatis.mapper-locations=classpath*:com/example/amqp_publisher/mapper/sqlxml/*.xml
mybatis.configuration.map-underscore-to-camel-case=true
#日志打印（可省）
logging.level.com.example.amqp_publisher.mapper=debug

spring.thymeleaf.cache=false
#debug=true
```

```properties
#(1).引用配置变量（无则使用默认值 spring）
info.msg=hello ${server.servlet.context-path : /spring}

#(2).随机值。微服务中不需要记录 ip:prot，所以可随机指定端口
info.random=${random.int[1000,9999]}
```

> applicatopn.yml

```yaml
k:(空格)v #表示一对键值对(空格必须有)，其中属性和值大小写敏感。

#1.数组(List/Set)的标准写法：
pets: 
 - cat
 - dog

#数组的行内写法：
pets: [cat,dog,pig]

#2.随机数 
${random.int}; ${random.int(10)}; ${random.int(10,100)}

#3.引用配置变量（无则取默认值）
person.age = ${random.int}
person.last_name = 张三${person.age:18}

#4.转义（层级关系使用 2个或4个 空格）
spring:
  datasource:
    url: jdbc:mysql://192.168.8.7:33306/test0329?useSSL=false&allowMultiQueries=true
    username: bluecardsoft
    password: "#$%_BC13439677375" #""双引号里的内容不会转义符，''单括号则会
    driver-class-name: com.mysql.jdbc.Driver
    type: com.alibaba.druid.pool.DruidDataSource
```

## config

> 加载顺序：配置文件应该放置在jar包同级的 `/config/*.yml`

```shell
– classpath:/            #路径src/main/resources
– classpath:/config/
– file:./                #当前项目的根路径，与pom同级（jar包同级目录）。
– file:./config/

#优先级别: 低--->高. 高优先级覆盖低优先级.
#加载顺序: 先--->后. (由里到外). 后加载的覆盖先加载的. [互补配置]
```

> profile特性：不同环境加载不同配置

```shell
#以下文件与 application.yml 存放在同级目录下。其中，前者配置特殊信息；后者配置公用信息。二者相互补充
application-dev.yml   #开发环境
application-test.yml  #测试环境
application-prod.yml  #生产环境

#在默认配置文件 application.properties 中激活
spring.profiles.active=dev
```

> 外部配置log

```properties
#配置文件中指定log位置（内部或外部yml都可以）
#推荐外部 -> logback的 scan 和 scanPeriod 两个属性保证了 热部署，即改即生效
logging.config=file:./config/logback-spring.xml
```

> log的 profile 特性

```xml
<springProfile name="dev"> <!-- 控制台 * 测试环境 -->
    <root level="info">
        <appender-ref ref="console" />
    </root>
</springProfile>

<springProfile name="prod"> <!-- 控制台 * 生产环境 -->
    <root level="warn">
        <appender-ref ref="console" />
    </root>
</springProfile>
```

##读取配置

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

> 单个属性读取：@value 和 env

```java
@Value("${info.enabled}") //(1).@Value
public String infoEnabled;
```

```java
@Autowired //(2).Environment
Environment env;

String pwd = env.getProperty("spring.mail.password");
```

>批量属性读取：必须使用静态内部类

```java
@Data //lombok
@Component
/**
 * prefix: 属性前缀，通过 'prefix+字段名' 匹配属性，默认""
 * ignoreUnknownFields: 是否忽略未知的字段，默认 true
 * ignoreInvalidFields: 是否忽略验证失败（类型转换异常）的字段，默认 false
 */
@ConfigurationProperties(/*prefix = "info",*/ ignoreUnknownFields = true, ignoreInvalidFields = true)
/**
 * value: 需要加载的属性文件，可以一次性加载多个。
 * encoding: 编码格式，默认""
 * ignoreResourceNotFound: 当指定的配置文件不存在是否报错，默认 false
 * name: 唯一标识，整个项目中唯一。
 */
@PropertySource(value = {/*"file:./my.properties",*/ "classpath:my.properties"}, encoding = "utf-8",
                ignoreResourceNotFound = false, name = "my.properties")
public class MyProperties {
    public Voice voice; //voice开头
    public Info info; //info开头

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

> 通过IO读取

```java
// 默认从此类所在包下读取，path需要添加前缀"/"
// InputStream in = getClass().getResourceAsStream("/my.properties");

// 默认从ClassPath下读取，path不需要添加前缀
InputStream in = getClass().getClassLoader().getResourceAsStream("my.properties");
Properties properties = new Properties();
properties.load(new InputStreamReader(in, "UTF-8")); //U8方式读取
properties.forEach((key, value) -> log.info(key + " - " + value));
```

>区别：`@Value("#{}") 与 @Value("${}")`

```java
//${}：获取配置文件中配置的属性
@Value("${info.enabled:'false'}")
String enabled; //默认'false'
```
```java
//#{} -> 通过SpEl表达式获取：常量，bean属性值，调用bean的某个方法
@Value("#{‘abc’}")
int str; //获取字符串常量'abc'

@Value("#{info.remoteAddress？:'127.0.0.1'}") //获取bean的属性，默认'127.0.0.1'
String address;
```
##常用接口

>匹配带后缀url访问：http://192.168.8.7:8090/spring/test.do，其中 .do 可以省略

```java
@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {
    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        //boot2.x默认将'/test'和'/test.do'作为2个url
        configurer.setUseRegisteredSuffixPatternMatch(true); //true，统一以上两个url
    }
}
```

```java
@Bean
public ServletRegistrationBean servletRegistrationBean(DispatcherServlet dispatcherServlet) {
    ServletRegistrationBean<DispatcherServlet> bean = new ServletRegistrationBean<>(dispatcherServlet);
    bean.addUrlMappings("*.do"); //拦截'.do'结尾的url
    return bean;
}
```

> `CommandLineRunner`：用于在应用初始化完成后执行代码逻辑（可使用任何依赖），代码逻辑在整个应用生命周期内只会执行一次。

```java
@Order(value=n) //对于多个该配置的情况，设置执行顺序。n越小，越先执行。
@Component
public class ApplicationStartupRunner implements CommandLineRunner { }
```

> `SpringBootServletInitializer`：使用外置的tomcat启动时，项目启动类继承该类，并复写configure()方法。`待证`

```java
@SpringBootApplication
public class MyApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return super.configure(builder);
    }
}
```

#高级功能

##druid

><https://github.com/alibaba/druid/tree/master/druid-spring-boot-starter>

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
    <version>1.1.10</version>
</dependency>
```
```properties
#boot1.x默认数据源为：org.apache.tomcat.jdbc.pool.DataSource
#boot2.x默认数据源为：com.zaxxer.hikari.HikariDataSource
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

##静态资源

>默认目录：存放在以下目录的资源都可以直接访问

```properties
classpath:/public/
classpath:/resources/
classpath:/static/
classpath:/META-INFO/resouces/
```

```properties
classpath:/static/a.html        ---> http://127.0.0.1:8090/demo/a.html
classpath:/static/abc/c.html    ---> http://127.0.0.1:8090/demo/abc/c.html
classpath:/static/img/sql.png   ---> http://127.0.0.1:8090/demo/img/sql.png
```

> 自定义目录：配置文件方式 + java代码方式

```properties
spring.mvc.static-path-pattern=/log/**
#此配置会覆盖 springboot 默认配置，所以需要手动追加默认配置
spring.resources.static-locations=classpath:/log/,file:logs/,classpath:/public/
```

```java
//file:logs/ 表示jar包同级目录下的 /logs 目录
//classpath:/log/demo.log     ---> http://127.0.0.1:8090/demo/logs/demo.log
//jar包同级目录/logs/test.log   ---> http://127.0.0.1:8090/demo/logs/test.log

@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/log/**")
            .addResourceLocations("classpath:/log/", "file:logs/");
    }
}
```

> webjars：将前端资源（js，css等）打成jar包，使用Maven统一管理。http://www.webjars.org/

```xml
<dependency>
    <groupId>org.webjars</groupId>
    <artifactId>webjars-locator</artifactId> <!--页面引用时，可省略版本号（如 jquery-3.3.1）-->
    <version>0.32</version>
</dependency>
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
```

##跨域

```shell
跨域是什么？
浏览器从一个域名的网页去请求另一个域名的资源时，域名、端口、协议任一不同，都是跨域。采用前后端分离开发，前后端分离部署，必然会存在跨域问题。

怎么解决跨域？
很简单，只需要在 Controller 类上添加注解 @CrossOrigin 即可！这个注解其实是CORS的实现。

CORS（Cross-Origin Resource Sharing，跨源资源共享）是W3C出的一个标准，其思想是使用自定义的HTTP头部让浏览器与服务器进行沟通，
从而决定请求或响应是应该成功，还是应该失败。因此，要想实现CORS进行跨域，需要服务器进行一些设置，同时前端也需要做一些配置和分析。
本文简单的对服务端的配置和前端的一些设置进行分析。
```

## login

>前台表单

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
    <head>
        <meta charset="UTF-8">
        <title>login page</title>

        <!--小叶子图标，存放目录 /static/ico/favicon.ico-->
        <link rel="shortcut icon" th:href="@{/img/favicon.ico}"/>

        <!--webjars-locator: 页面引用时，可省略版本号.(如 3.3.1)-->
        <!--省略前: <script th:src="@{/webjars/jquery/3.3.1/jquery.min.js}"/>-->
        <script th:src="@{/webjars/jquery/jquery.min.js}"></script>
        <script th:src="@{/webjars/bootstrap/js/bootstrap.min.js}"></script>
        <link rel="stylesheet" th:href="@{/webjars/bootstrap/css/bootstrap.min.css}"/>
    </head>
    <body>
        <!--th:action 表单提交-->
        <form method="post" th:action="@{/login}">
            <!--th:value name为null则不显示-->
            账户：<input type="text" th:name="userName" th:value="${uName}"/>
            密码：<input type="password" th:name="userPwd"/>
            <center>
                <button type="submit" class="btn btn-primary">登陆</button>
                <!--th:if errorMsg为空则不显示-->
                <p th:if="${! #strings.isEmpty(errMsg)}" th:text="${errMsg}"></p>
            </center>
        </form>
    </body>
</html>
```

> 后台接口

```java
@Controller
public class UserController {

    @PostMapping("/login")
    public String login(@RequestParam("userName") String uName, @RequestParam("userPwd") String uPwd,
                        HttpSession session, Model model) {
        if (StringUtils.isNotBlank(uPwd)) {
            session.setAttribute("userName", uName); //缓存Session，用于后续验证

            //重定向到接口，可以防止表单重复提交！其实质是重定向到 MyWebMvcConfig#addViewControllers()方法
            return "redirect:/main";
        } else {
            model.addAttribute("uName", uName); //用于表单回显
            model.addAttribute("errMsg", "用户名或密码不正确");
            return "/index"; //转发到页面：/templates/index.html
        }
    }
}
```

> 登陆拦截器：未登录用户不允许访问非登录页面以外的页面

```java
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    //在目标方法之前被调用。适用于权限，日志，事务等
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        StringBuffer requestURL = request.getRequestURL();
        Object userName = request.getSession().getAttribute("userName");
        log.info("{} {}", requestURL, userName);

        if (null == userName) {
            request.getRequestDispatcher("/").forward(request, response);
            return false;
        }
        return true; //有登陆Session，则不拦截
    }
}
```

> 注册拦截器

```java
@Configuration
public class MyWebMvcConfig implements WebMvcConfigurer {

    // @GetMapping("/main")
    // public String main() { return "main"; }

    //配置视图映射。等同于以上代码，相当于集合版
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/main").setViewName("main");
    }

    //配置静态资源映射
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**") //与 /static 同级的 /log 目录
            .addResourceLocations("classpath:/static/", "classpath:/log/");
    }

    //注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
            .addPathPatterns("/**")
            .excludePathPatterns("/webjars/**", "/img/**", "/html/**", "/log/**") //不拦截：静态资源
            .excludePathPatterns("/", "/login"); //不拦截：登陆接口
    }
}
```

##文件上传

>流程步骤：`html标签的 id 属性是给js使用，name 属性是给后台使用`

```html
(1).<form>表单: POST + enctype="multipart/form-data"
(2).文件上传域(<file>)必须要有name属性: <input type='file' name="file">
(3).后台使用 MultipartFile 接收文件资源
```
> 前台页面

```html
<form action="/upload" method="post" enctype="multipart/form-data">
    File0: <input type="file" name="file"><br>
    <!-- File1: <input type="file" name="file"><br> --> <!-- 多文件上传，name必须一致 -->
    Desc: <input type="text" name="desc"><br>
    <input type="submit" value="提交">
</form>
```
> 后台代码：单文件上传

```java
@ResponseBody
@PostMapping("/upload")
public String upload(@RequestParam("file") MultipartFile file, @RequestParam("desc") String desc) {
    //<form>表单中文件name, 文件名字, 文件大小
    System.out.println(file.getName() + " - " + file.getOriginalFilename() + " - " + file.getSize());
    file.transferTo(new File(getUploadDir(), file.getOriginalFilename())); //文件另存
}

public File getUploadDir() throws FileNotFoundException {
    File dir = new File(ResourceUtils.getURL("").getPath(), "/upload"); //项目根目录下
    if (!dir.exists()) {
        dir.mkdirs(); //创建当前及父目录.(区别于 mkdir())
    }
    return dir;
}
```
> 后台代码：多文件上传

```java
@PostMapping("/uploads")
public void batchUplocad(@RequestParam("file") List<MultipartFile> files,
                         @RequestParam("desc") List<String> descs) {
    files.forEach(x -> x.transferTo(getUploadDir(), x.getOriginalFilename())); //文件另存 
    descs.forEach(log::info); //文件描述
}
```
> 限制上传文件的大小：两种方式

```properties
#单个上传文件的大小
spring.servlet.multipart.max-file-size=10MB
#一次请求上传文件的总容量
spring.servlet.multipart.max-request-size=20MB
```

```java
@Bean //代码配置
MultipartConfigElement multipartConfigElement() {
    MultipartConfigFactory factory = new MultipartConfigFactory();
    factory.setMaxFileSize("10MB"); //单个文件
    factory.setMaxRequestSize("20MB"); //一次请求多个文件
    return factory.createMultipartConfig();
}
```









#小众功能

##外置tomcat

>修改打包方式

```xml
<groupId>com.example</groupId>
<artifactId>amqp_publisher</artifactId>
<version>0.0.1-SNAPSHOT</version>
<packaging>war</packaging> <!--打包war，tomcat必须有-->
```

> 移除自带的嵌入式Tomcat

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions> <!-- 移除嵌入式tomcat插件 -->
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

>添加servlet-api依赖

```xml
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>3.1.0</version>
    <scope>provided</scope> <!--该依赖参与编译，测试，运行，但不会被打进项目包。由容器tomcat提供-->
</dependency>
```

>修改启动类，并重写初始化方法

```java
@MapperScan(value = "com.example.*.mapper")
@SpringBootApplication
public class AmqpPublisherApp extends SpringBootServletInitializer { //新增 extends

    public static void main(String[] args) {
        SpringApplication.run(AmqpPublisherApp.class, args);
    }

    @Override //新增方法
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(AmqpPublisherApp.class);
    }
}
```

## 热部署

> `DevTools`：Ctrl+F9 重新部署

```properties
#禁用模板缓存
spring.thymeleaf.cache=false
```

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <optional>true</optional> <!-- 依赖只在当前项目生效，不会传递到引用项目中 -->
</dependency>
```
##email

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
> 邮件（普通 + 附件 + 静态资源 + 模板）

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


















## junit

>`@SpringBootTest`

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope> <!-- 添加 junit 环境的 jar 包 -->
</dependency>
```
```java
@RunWith(SpringRunner.class) //junit 与 spring 进行整合，也可用 SpringJUnit4ClassRunner.class
@SpringBootTest//(classes = {SpringMain.class}) //加载项目启动类，当测试类的路径同启动类时，可省。
public class HelloServiceTest {

    @Autowired
    private HelloService helloService;

    @Test
    public void test() {
        helloService.hello();
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

> 各个端点endpoint

```shell
auditevents     #审计事件
beans           #应用程序上下文里全部的Bean,以及它们的关系
health          #报告应用程序的健康指标,这些值由 HealthIndicator 的实现类提供
conditions      #自动配置报告,记录哪些自动配置条件通过了,哪些没通过
configprops     #描述配置属性(包含默认值)如何注入Bean
info            #显示配置文件中以 'info' 打头的属性
threaddump      #获取线程活动的快照
scheduledtasks  #定时任务
httptrace       #跟踪 HTTP 请求-响应交换的情况
mappings        #描述全部的URI路径，以及它们和控制器(包含Actuator端点)的映射关系
(*)metrics      #报告各种应用程序度量信息,比如内存用量和HTTP请求计数.
(*)loggers      #显示和修改应用程序中的loggers配置
(*)env          #获取全部环境属性
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
#以上，端点带(*)表示当前路径只能获取目录信息，详情信息得需要进一步访问获取。如，获取系统cpu个数:
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

##Admin

https://github.com/codecentric/spring-boot-admin

SpringBoot-Admin 用于监控BOOT项目，基于 Actuator 的可视化 WEB UI

> 客户端（被监控者）

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
#actuator
management.endpoints.web.exposure.include=*

#admin服务端
spring.boot.admin.client.url=http://localhost:9090/hello
```
```java
@Configuration
public /*static*/ class SecurityPermitAllConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests() //Security权限，授权的固定格式
            .anyRequest().permitAll() //所有请求，所有权限都可以访问
            .and().csrf().disable(); //固定写法：使 csrf()拦截失效
    }
}
```
>服务端

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
//全局注解
@EnableAdminServer
```

# WebSocket

## 基础概念

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
```

> 简介

B/S结构的软件项目中有时客户端需要实时的获得服务器消息，但默认HTTP协议只支持 `请求响应模式`。 对于这种需求可以通过 polling，Long-polling，长连接，Flash-Socket，HTML5中定义的WebSocket 完成。

HTTP模式可以简化Web服务器，减少服务器的负担，加快响应速度，因为服务器不需要与客户端长时间建立一个通信链接。但不容易直接完成实时的消息推送功能（如聊天室，后台信息提示，实时更新数据等）。

应用程序通过 Socket 向网络发出请求或者应答网络请求。Socket 可以使用TCP/IP协议或UDP协议。

```java
TCP协议：面向连接的，可靠的，基于字节流的传输层通信协议，负责数据的可靠性传输问题。
UDP协议："无连接，不可靠"，基于报文的传输层协议，优点：发送后不用管，速度比TCP快。

HTTP协议："无状态协议"，通过 Internet 发送请求消息和响应消息，默认使用80端口。（底层Socket）
```

> Http协议

HTTP 协议原本是设计用于传输简单的文档和文件，而非实时的交互。

根据 HTTP 协议，一个客户端如浏览器，向服务器打开一个连接，发出请求，等待回应，之后关闭连接。如果客户端需要更多数据，则需要打开一个新连接，以此循环往复。如果服务器有了新的信息，它必须等待客户端发出请求而不是立即发送消息。

那么要看到页面中要展示信息的最新情况，应该怎么办？不断刷新！

缺点：这种方式现在已经被完全淘汰，发送了很多不必要的请求，浪费大量带宽，页面不断刷新，用户体验差，而且做不到真正的实时，服务端有了新数据也不能立马推送给客户端，使得秒级的实时信息交互难以实现。

> 双向通信

HTTP协议决定了服务器与客户端之间的连接方式，无法直接实现消息推送（F5已坏），一些变相的解决办法：

1. 轮询（Polling）

   客户端定时向服务器发送Ajax请求，服务器接到请求后马上返回响应信息并关闭连接。

   优点：后端程序编写比较容易

   缺点：请求中有大半是无用，浪费带宽和服务器资源

   实例：适于小型应用

2. 长轮询（Long-Polling）

   客户端向服务器发送Ajax请求，服务器接到请求后hold住连接，直到有新消息才返回响应信息并关闭连接。客户端处理完响应信息后再向服务器发送新的请求

   优点：在无消息的情况下不会频繁的请求，耗费资小

   缺点：服务器hold连接会消耗资源，返回数据顺序无保证，难于管理维护

   实例：WebQQ，Hi网页版，Facebook-IM

3. 长连接

   在页面里嵌入一个隐蔵iframe，将这个隐蔵iframe的src属性设为对一个长连接的请求或是采用xhr请求，服务器端就能源源不断地往客户端输入数据

   优点：消息即时到达，不发无用请求，管理起来也相对便

   缺点：服务器维护一个长连接会增加开销

   实例：Gmail聊天

4. Flash-Socket

   在页面中内嵌入一个使用了Socket类的 Flash 程序，JavaScript通过调用此Flash程序提供的Socket接口，与服务器端的Socket接口进行通信，JavaScript在收到服务器端传送的信息后控制页面的显示。

   优点：实现真正的即时通信,而不是伪即时
   缺点：客户端必须安装Flash插件，非HTTP协议，无法自动穿越防火墙
   实例：网络互动游戏

5. Websocket

   Html5提供的一种浏览器与服务器间进行全双工通讯的网络技术。依靠这种技术可以实现客户端和服务器端的长连接，双向实时通信。

   优点：事件驱动，异步，使用ws或者wss协议的客户端socket，能够实现真正意义上的推送功能。

   缺点：少部分浏览器不支持，浏览器支持的程度与方式有区别。


> WebSocket

Websocket 允许通过js与远程服务器建立连接，从而实现客户端与服务器间双向的通信。Websocket 的url开头是ws，如果需要ssl加密可以使用wss。

当调用构造方法构建一个 Websocket 对象后，就可以进行即时通信了`（new WebSocket(url)）`。

## 客户端

```html
<body>
    <input id="text" type="text"/>
    <button onclick="send()">Send</button>
    <button onclick="closeWebSocket()">Close</button>
    <div id="message"></div>
</body>
<script type="text/javascript">
    var websocket = null;

    //判断当前浏览器是否支持WebSocket
    if (!'WebSocket' in window) {
        alert('浏览器不支持WebSocket')
    } else {
        var userId = parseInt(Math.random() * (99 + 1), 10); //生成[0,99]的任意随机数
        websocket = new WebSocket("ws://localhost:8090/demo/websocket?id=" + userId);

        //监听事件 -> 连接成功建立时触发该事件
        websocket.onopen = function (event) {
            setMessageInnerHTML("open: " + new Date());
        };

        //监听事件 -> 连接关闭
        websocket.onclose = function (event) {
            setMessageInnerHTML("close: " + new Date() + " - " + event.code);
            websocket.send(event.code);
        };

        //监听事件 -> 接收到服务器发来的消息
        websocket.onmessage = function (event) {
            setMessageInnerHTML(event.data);
        };

        //监听事件 -> 连接发生错误
        websocket.onerror = function () {
            setMessageInnerHTML("error: " + new Date());
        };

        //监听事件 -> 监听窗口关闭事件
        //当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常
        window.onbeforeunload = function () {
            if (null != websocket) {
                websocket.close();
            }
        };

        //将消息显示在网页上
        function setMessageInnerHTML(innerHTML) {
            document.getElementById('message').innerHTML += innerHTML + '<br/>';
        }

        //关闭连接
        function closeWebSocket() {
            websocket.close();
        }

        //向远程服务器发送数据
        function send() {
            var message = document.getElementById('text').value;
            websocket.send(message);
        }
    }
</script>
```

## 服务端

```java
@Configuration
public class WebSocketConfig {

    //这个bean会自动注册使用 @ServerEndpoint 注解声明的 WebSocket-Endpoint.
    //注意: 如果使用独立的servlet容器，而不是直接使用 SpringBoot 内置容器，就不要注入此bean，
    //因为它将由容器自己提供和管理
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
```

```java
// 使用 SpringBoot 要使用注解 @Component
// 使用独立容器(tomcat)是由容器自己管理 WebSocket，但在 SpringBoot 中连容器都是 Spring 管理。
//
// 虽然 @Component 默认是单例模式的
// 但 SpringBoot 还是会为每个 WebSocket 连接初始化一个bean,所以可以用一个静态 Set/Map 保存起来.
@Component

// 使用注解 @ServerEndpoint 可以将一个普通Java类作为 WebSocket 服务器的端点
// 使用 ServerEndpoint 注解的类必须有一个公共的无参数构造函数.
//
// WebSocket 服务端运行在 ws://[Server端IP或域名]:[Server端口]/项目/push
// 客户端浏览器已经可以对WebSocket客户端API发起 <<<HTTP长连接>>>.
@ServerEndpoint("/push")
public class EchoEndpoint {

    //客户端注册时调用
    @OnOpen
    public void onOpen(Session session) {}

    //客户端关闭
    @OnClose
    public void onClose(Session session, CloseReason reason) {}

    //客户端异常
    @OnError
    public void onError(Throwable t) {}

    //收到浏览器客户端消息后调用
    @OnMessage
    public void onMessage(String message) {}

    //更高级的注解，MaxMessageSize 属性可以被用来定义消息字节最大限制，
    //在示例程序中，如果超过6个字节的信息被接收，就报告错误和连接关闭。
    // @Message(maxMessageSize = 6)
    // public void receiveMessage(String s) {
    // }
}
```

## 后台Demo

```java
@Component
@ServerEndpoint(value = "/websocket")
public class MyWebSocket {
    
    // 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的
    private static int onlineCount = 0;

    // 旧版：concurrent包的线程安全Set，用来存放每个客户端对应的 MyWebSocket 对象
    // private static CopyOnWriteArraySet<MyWebSocket> webSocketSet =
    //         new CopyOnWriteArraySet<>();

    //新版：使用map对象，便于根据 userId 来获取对应的 MyWebSocket
    private static Map<String, MyWebSocket> webSocketMap = new ConcurrentHashMap<>();

    //区别: 非静态变量 和 静态变量
    //与某个客户端的连接会话,需要通过它来给客户端发送数据
    private Session session;

    //当前会话session对应的显式id
    private String userId;

    //客户端注册
    @OnOpen
    public void onOpen(Session session) {
        String id = this.userId = session.getRequestParameterMap().get("id").get(0);
        this.session = session;
        addOnlineCount(); //在线数加1
        webSocketMap.put(id, this); //加入Map
        System.out.println("有新连接加入: " + this.userId + " 当前在线人数为: "
                + getOnlineCount());

        sendMsg2All(this.userId + " - 已上线! 欢迎");
    }

    //客户端关闭
    @OnClose
    public void onClose() {
        if (null != webSocketMap.get(this.userId)) {
            subOnlineCount(); //在线数减1
            webSocketMap.remove(this.userId); //从Map中删除
            System.out.println("有一连接关闭: " + this.userId + " 当前在线人数为: " 
                    + getOnlineCount() + reason);

            sendMsg2All(this.userId + " - 已下线! 再见");
        }
    }

    //客户端异常
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误: " + error);
        error.printStackTrace();
    }

    ///收到浏览器客户端消息后调用的方法
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("来自客户端的消息: " + this.userId + " - " + message);

        if (message.contains("-")) {
            String[] split = message.split("-");
            webSocketMap.keySet().forEach(x -> {
                if (split[0].equalsIgnoreCase(x))
                    sendMsg2One(this.userId + "->" + x + " - " + split[1], x); //点对点
            });
        } else {
            sendMsg2All(userId + " - " + message); //群发
        }
    }

    ///群发消息
    public static void sendMsg2All(String message) {
        webSocketMap.values().forEach(x -> x.sendMsg(message));
    }

    ///点对点发送消息
    public static void sendMsg2One(String message, String userId) {
        webSocketMap.get(userId).sendMsg(message);
    }

    ///实现服务器主动推送
    private void sendMsg(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
            // this.session.getAsyncRemote().sendText(message);
        } catch (IOException e) {
            System.out.println("异常---发送消息: " + e);
        }
    }

    //三个同步方法,线程安全
    private static synchronized int getOnlineCount() {
        return onlineCount;
    }

    private static synchronized void addOnlineCount() {
        MyWebSocket.onlineCount++;
    }

    private static synchronized void subOnlineCount() {
        MyWebSocket.onlineCount--;
    }
}
```
# thymeleaf

##基础概念

模板引擎：`将后台数据 填充 到前台模板的表达式中！`thymeleaf，freemarker，jsp，velocity

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>

<html lang="en" xmlns:th="http://www.thymeleaf.org"> <!--命名空间-->
```

> sts的html代码提示

```xml
(0).下载STS插件: https://github.com/thymeleaf/thymeleaf-extras-eclipse-plugin/releases
(1).在STS安装目录dropins下新建文件夹: thymeleaf-2.1.2
(2).只将压缩包中的 features 和 plugins 文件夹拷贝到以上目录并重启eclise!!!
(3).在thymeleaf的html页面引入命名空间：<html lang="en" xmlns:th="http://www.thymeleaf.org">
```
> 低版本异常

```xml
<!--org.xml.sax.SAXParseException：元素类型 "meta" 必须由匹配的结束标记 "</meta>" 终止-->
<!--这是由于低版本对于html语法解析比较严格，必须有头有尾-->

(1).html标记按照严谨的语法去编写
    <meta charset="UTF-8" />
    
(2).升级为高版本
    <!--Thymeleaf.jar: 更新为 3.0 以上的版本-->
    <!--thymeleaf-layout-dialect.jar: 更新为 2.0 以上的版本-->
    <properties>
        <java.version>1.8</java.version>
        <thymeleaf.version>3.0.2.RELEASE</thymeleaf.version>
        <thymeleaf-layout-dialect.version>2.0.4</thymeleaf-layout-dialect.version>
    </properties>
```
> thymeleaf 模板文件存放位置：src/main/resources/templates

```
templates 目录是安全的，意味着该目录下的内容不允许外界直接访问，必须经过服务器的渲染
```

##常用符号

>`~{...}` <!--片段引用表达式-->

>`@{...}` <!--定义URL-->

```html
<!--http://ip:8080/order/details/3-->
<a href="emp" th:href="@{/details/}+${emp.id}">相对路径-传参-restful</a>

<!--http://ip:8080/order/details?orderId=3-->
<a th:href="@{http://ip:8080/order/details(orderId=${o.id})}">绝对路径-传参</a>

<!--http://ip:8080/order/details?orderId=3-->
<a th:href="@{/details(orderId=${o.id})}">相对路径-传参</a>

<!--http://ip:8080/order/3/details?orderId=3 -->
<a th:href="@{/{orderId}/details(orderId=${o.id}, orderName=${o.name})}">相对路径-传参-restful</a>
```
>`${...}` <!--变量值--> https://www.cnblogs.com/xiaohu1218/p/9634126.html

```html
(1).获取对象的属性，调用方法：${person.name}
(2).使用内置的基本对象：${! #strings.isEmpty(msg)}
(3).内置的一些工具对象
```
>`#{...}` <!--用于获取 properties 文件内容，常用于'国际化'场景-->

```html
home.welcome=this messages is from home.properties! <!--properties文件-->
<p th:text="#{home.welcome}">This text will not be show!</p> <!--读取properties文件中的 home.welcome-->
```
>`#maps`  <!--工具对象表达式-->

```html
#dates #calendars #numbers #strings #objects #bools #arrays #lists #sets

<!--有msg对象则显示<p>; 反之不显示-->
<p style="color:red" th:text="${msg}" th:if="${not #strings.isEmpty(msg)}" />
```
>`*{...}`    <类似${}功能，配合th:object使用，获取指定对象的变量值>

```html
<div> <!--(1).类似${}功能-->
    <p>Name: <span th:text="*{session.user.name}">Sebastian</span>.</p>
    <p>Surname: <span th:text="*{session.user.surname}">Pepper</span>.</p>
    <p>Nationality: <span th:text="*{session.user.nationality}">Saturn</span>.</p>
</div>

<div> <!--(2-1).原始表达式-->
    <p>Name: <span th:text="${session.user.firstName}">Sebastian</span>.</p>
    <p>Surname: <span th:text="${session.user.lastName}">Pepper</span>.</p>
    <p>Nationality: <span th:text="${session.user.nationality}">Saturn</span>.</p>
</div>

<div th:object="${session.user}"> <!--(2-2).指定对象-->
    <p>Name: <span th:text="*{firstName}">Sebastian</span>.</p>
    <p>Surname: <span th:text="*{lastName}">Pepper</span>.</p>
    <p>Nationality: <span th:text="*{nationality}">Saturn</span>.</p>
</div>

<div th:object="${session.user}"> <!--(2-3).混合使用-->
    <p>Name: <span th:text="*{firstName}">Sebastian</span>.</p> <!--指定对象-->
    <p>Surname: <span th:text="${session.user.lastName}">Pepper</span>.</p> <!--上下文变量取值-->
    <p>Nationality: <span th:text="${#object.nationality}">Saturn</span>.</p> <!--从 #object 中获取属性-->
</div>
```
>`th:href`发送get请求

```html
<a th:href="@{/emp}">用户添加</a> <!--发送get请求到 '/项目名/emp'-->
```
>`th:src`图片类地址引入

```html
<img alt="app-logo" th:src="@{/img/logo.png}" />
```
>`th:id`动态指定id属性

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

<!--(1).itemStat 称作状态变量，属性有:-->
index        -> 当前迭代对象的索引. (从0开始)
count        -> 当前迭代对象的计数. (从1开始)
size        -> 迭代集合的长度
current        -> 当前迭代变量
even/odd    -> 布尔值,当前循环是否是偶数/奇数. (从0开始)
first/last    -> 布尔值,当前循环是否是第一个/最后一个

<!--(2).用法：${itemStat.index}; ${itemStat.odd}; ${itemStat.current.name}... ...-->

<!--(3).在写 th:each="obj,objStat:${objList}"，可不写 objStat，自动添加，默认命名 objStat（如itemStat）-->
```
> `th:value` 可以将一个值放入到 input 标签的 value 中

> `th:text`

```html
<div th:text="${emp.name}">将被替换</div> <!--一般写法-->
<div>[[${emp.name}]]</div> <!--行内写法行内写法-->
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

```html
<!--禁用行内写法（禁用内联）-->
<p th:inline="none">A double array looks like this: [[1, 2, 3], [4, 5]]!</p> 
```
```html
<!--js内联-->
<script th:inline="javascript">
    ...
    var username = [[${session.user.name}]];
    ...
</script>
```
```html
<!--css内联-->
<style th:inline="css">
    .[[${classname}]] {
        text-align: [[${align}]];
    }
</style>
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

>`th:selected` selected选择框，选中情况

```html
<td>住址:</td> <!--th:text 用于显示; th:value 用于存值-->
<td>
    <select name="city.id"> <!--th:selected 回显对象 person.city.id 和遍历 city.id 相同，则选中-->
        <option th:each="city : ${cityList}" th:value="${city.id}" th:text="${city.name}"
                th:selected="${null!=person}?${person.city.id}==${city.id}">
        </option>
    </select>
</td>

<td>
    <select name="city.id"> <!--配合使用 th:object-->
        <option th:each="city : ${cityList}" th:object="${city}" th:value="*{id}" th:text="*{name}"
                th:selected="${null!=person}?${person.city.id}==*{id}">
        </option>
    </select>
</td>
```

>`th:if` 条件判断

```html
<p th:if="${! #strings.isEmpty(msg)}" th:text="${msg}"></p> <!--msg不为空,则显示<p>-->

<!--th:if="${xx}" 表达式为 true 的各种情况:-->
boolean xx =true;
int xx !=0;
character xx !=0;
String xx !="false","off","no";
If xx is not a boolean, a number, a character or a String
```

>`th:switch`多路选择，配合使用 `th:case`

```html
<div th:switch="${user.role}">
    <p th:case="'admin'">超级管理员</p>
    <p th:case="#{roles.manager}">普通管理员</p>
    <p th:case="*">其他</p> <!--默认值-->
</div>
```

>`th:with` 变量赋值运算

```html
<div th:with="first=${persons[0]}"> <!--th:with="x=${y}"-->
    <p>The name of the first person is 
        <span th:text="${first.name}">Julius Caesar</span>
    </p>
</div>
```

>`th:attr` 设置标签属性，多个属性用逗号分隔

```html
th:attr="src=@{/image/aa.jpg},title=#{logo}" <!--一般用于自定义标签-->
```

>`th:remove` 删除某个属性

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

>

```html

```

>



# CRUD

##restful

>restful是对于同一个服务器资源的一组不同的操作，包括：GET，POST，PUT，DELETE，PATCH，HEAD，OPTIONS

```java
http请求的安全和幂等，是指多次调用同一个请求对资源状态的影响。
'安全' -> 请求不会影响资源的状态。只读的请求：GET,HEAD,OPTIONS
'幂等' -> 多次相同的请求，目的一致。
```
| 请求方式 |  请求url  |                             说明                             |    是否幂等    |
| :------: | :-------: | :----------------------------------------------------------: | :------------: |
|   GET    | /emp/list |                   只读请求，不改变资源状态                   |   安全，幂等   |
|   PUT    |  /emp/5   |         多次请求都是将id为 5 的员工姓名修改成'wang'          |  不安全，幂等  |
|   POST   | /emp/emp  |                 多次请求会新增多条相同的数据                 | 不安全，不幂等 |
|  DELETE  |  /emp/5   |              多次请求目的都是删除id为 5 的员工               |  不安全，幂等  |
|          |           | `第一次成功删除，第二次及以后虽资源已不存在，但也得返回 200 OK，不能返回 404` |                |

> 测试接口

|     请求说明     |  请求url  | 请求方式 |
| :--------------: | :-------: | :------: |
|     列表页面     | /emp/list |   GET    |
| 跳转页面（新增） | /emp/emp  |   GET    |
|     新增接口     | /emp/emp  |   POST   |
| 跳转页面（修改） | /emp/{id} |   GET    |
|     修改接口     | /emp/emp  |   PUT    |
|     删除接口     | /emp/{id} |  DELETE  |

> `将 POST 请求转化为 PUT，DELETE`

```xml
<!--（1）配置 HiddenHttpMethodFilter，SpringBoot默认已配置-->
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
##列表：get

> 跳转到列表页面 `a标签对应的是 GET 请求`

```html
<a th:href="@{/emp/list}">员工列表</a>
```

>跳转逻辑

```java
@GetMapping("/list")
public String list(Model model) {
    model.addAttribute("emps", EmpUtils.listAll()); //模拟查库
    return "/emp/list";
}
```

> 列表页面

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>列表页面</title>

    <script th:src="@{/webjars/jquery/jquery.min.js}"></script>
    <script th:src="@{/webjars/bootstrap/js/bootstrap.min.js}"></script>
    <link rel="stylesheet" th:href="@{/webjars/bootstrap/css/bootstrap.min.css}"/>

    <script>/*删除记录的js...*/</script>
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
            <a th:href="@{/emp/}+*{id}">修改</a> <!--路径拼接-->
            <a href="#" onclick="deleteEmp(this)" th:attr="url=@{/emp/}+${emp.id}">删除</a>
        </td>
    </tr>
</table>
<a th:href="@{/emp/emp}">新增员工</a>
</body>
</html>
```

##新增：post

>跳转新增页面

```html
<a th:href="@{/emp/emp}">新增员工</a>
```

>跳转逻辑

```java
@GetMapping("/emp")
public String toAdd(Model model) {
    model.addAttribute("citys", EmpUtils.listCity()); //新增页面要显示的城市列表信息
    return "/emp/emp"; //转发-页面
}
```

> 新增页面（同修改页面，略）

> 新增接口

```java
@PostMapping("/emp")
public String add(Emp emp) {
    empList.add(emp);
    return "redirect:/emp/list"; //重定向-接口
}
```

##修改：put

>跳转修改页面

```html
<a th:href="@{/emp/}+*{id}">修改</a> <!--路径拼接-->
```

>跳转逻辑

```java
@GetMapping("/{id}")
public String toUpdate(@PathVariable Integer id, Model model) {
    Emp emp = EmpUtils.empList.get(id); //根据ID查找
    
    model.addAttribute("emp", emp);
    model.addAttribute("citys", EmpUtils.cityList); //用于页面回显
    return "/emp/emp";
}
```

>回显数据到修改页面

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>员工信息页</title>
</head>
<body>
<form method="post" th:action="@{/emp/emp}">
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
                <input type="radio" name="gender" value="1" th:checked="${null!=emp}?${emp.gender}">男
                <input type="radio" name="gender" value="0" th:checked="${null!=emp}?${!emp.gender}">女
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

>修改接口

```java
@PutMapping("/emp")
public String update(Emp emp) {
    EmpUtils.empList.update(emp);
    return "redirect:/emp/list";
}
```

##删除：delete

>删除方式（1）form表单

```html
<a href="#" onclick="deleteEmp(this)" th:attr="url=@{/emp/}+${emp.id}">删除</a>
```

```html
<form id="deleteForm" method="post" action="#"> <!--独立于列表Table的<form/>表单-->
    <input type="hidden" name="_method" value="DELETE">
</form>
```

```html
<script>
    function deleteEmp(e) {
        alert($(e).attr('url')); //按钮的url属性

        //动态设置<form>的action属性，并提交
        $('#deleteForm').attr('action', $(e).attr('url')).submit();
        return false; //取消按钮的默认行为
    }
</script>
```

>删除方式（1）后台逻辑

```java
@DeleteMapping("/{id}")
public String delete(@PathVariable Integer id) {
    EmpUtils.empList.remove(id.intValue());
    return "redirect:/emp/list";
}
```

> 删除方式（2）不使用form表单，而使用ajax异步请求

```html
<a href="#" onclick="deleteEmp(this)" th:attr="url=@{/emp/}+${emp.id}">删除</a>
```

```html
<script>
    function deleteEmp(e) {
        $.ajax({
            type: 'delete',
            url: $(e).attr('url'),
            dataType: 'text',
            success: function (data) {
                //e 表示当前emp所在行的标签<a/>
                //$(e).parent().parent() 表示<a/> -> td -> tr
                $(e).parent().parent().remove();
                alert(data);
            },
            error: function (data) {
                var res = JSON.parse(data.responseText); //转化json
                alert(res.status + " - " + res.error + " - " + res.message);
            }
        });
        return false;
    }
</script>
```

> 删除方式（2）后台逻辑

```java
@DeleteMapping("/{id}")
@ResponseBody
public String delete(@PathVariable Integer id) {
    EmpUtils.empList.deleteById(id);
    return "success";
}
```






# exception

Boot对于异常处理提供了五种处理方式，`推荐使用方式 3 或 5` http://blog.51cto.com/13902811/2170945?source=dra

##五种方式

> （1）自定义错误页面（默认）

```java
一旦程序出现异常，SpringBoot 会向url '/error' 发送请求。
通过默认的 BasicExceptionController 来处理请求 '/error'，然后跳转到默认异常页面，显示异常信息。

所以，如果需要将所有异常统一跳转到自定义错误页面，需新建页面 '/templates/error.html'，必须叫 error.html
缺点：不符合实际需求，应该对于不同错误跳转不同页面。
```

>（2）注解处理异常 @ExceptionHandler

>（3）注解处理异常 @ExceptionHandler + @ControllerAdvice

```java
当执行过程中出现异常，首先在本类中查找 @ExceptionHandler 标识的方法。
找不到，再去查找 @ControllerAdvice 标识类中的 @ExceptionHandler 标识方法来处理异常。

//处理优先级：异常的最近继承关系
例如发生异常 NullPointerException; 但是声明的异常有 RuntimeException 和 Exception
此时，根据异常的最近继承关系，找到继承深度最浅的那个，即 RuntimeException 的声明方法
```

```java
@ControllerAdvice //异常处理类
public class GlobalException {
    /**
     * 参数(可选):
     *         异常参数(包括自定义异常);
     *         请求或响应对象(HttpServletRequest; ServletRequest; PortleRequest/ActionRequest/RenderRequest) 
     *         Session对象(HttpSession; PortletSession) 
     *         WebRequest; NativeWebRequest; Locale; 
     *         InputStream/Reader; OutputStream/Writer; Model
     * 
     * 返回值(可选):
     *         ModelAndView; Model; Map; View; String; @ResponseBody;
     *         HttpEntity<?>或ResponseEntity<?>; 以及void
     */
    @ExceptionHandler(ArithmeticException.class) //ex对应发生的异常对象
    public ModelAndView arithmeticException(HttpServletRequest request, ArithmeticException ex) {
        
        //区分 URL & URI： http://ip:port/demo/hello/hello & /demo/hello/hello
        log.info("{} & {}", request.getRequestURL(), request.getRequestURI());

        ModelAndView mv = new ModelAndView("/error/airth");
        mv.addObject("errMsg", ex.getLocalizedMessage());
        return mv; //跳转异常页，并携带异常信息
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ModelAndView runtimeException(HttpServletRequest request, RuntimeException ex) {                
        ModelAndView mv = new ModelAndView("/error/runtime");
        mv.addObject("errMsg", ex.getLocalizedMessage());
        return mv;
    }
}
```
> （4）配置 SimpleMappingExceptionResolver （3的简化）

```java
//优点：在全局异常类的一个方法中完成所有异常的统一处理
//缺点：只能进行异常与视图的映射，不能传递异常信息

@Configuration //（1）此处的注解不同
public class GlobalException {
    
    //（2）方法必须有返回值。返回值类型必须是：SimpleMappingExceptionResolver
    @Bean
    public SimpleMappingExceptionResolver getSimpleMappingExceptionResolver() {
        SimpleMappingExceptionResolver resolver = new SimpleMappingExceptionResolver();
        Properties mappings = new Properties();

        //arg0：异常的类型，注意必须是异常类型的全名； arg1：视图名称
        mappings.put("java.lang.ArithmeticException", "error1");
        mappings.put("java.lang.RuntimeException", "error2");

        //（3）设置异常与视图的映射，但不能传递异常信息
        resolver.setExceptionMappings(mappings);
        return resolver;
    }
}
```
> （5）自定义类处理异常 HandlerExceptionResolver

```java
@Configuration
public class GlobalException implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(
            HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
        ModelAndView mv = new ModelAndView();

        //不同异常类型，不同视图跳转
        if (ex instanceof ArithmeticException) {
            mv.setViewName("error1");
        }
        if (ex instanceof NullPointerException) {
            mv.setViewName("error2");
        }
        //并传递异常信息
        mv.addObject("errMsg", ex.toString());
        return mv;
    }
}
```
## 自动处理

参照 ErrorMvcAutoConfiguration，错误处理的自动配置

> 一旦系统出现 4xx 或 5xx 之类的错误，ErrorPageCustomizer 就会生效，它会发送 /error 请求

```java
@Value("${error.path:/error}")    
private String path = "/error";
```
>/error 请求会被 BasicErrorController 处理，它有两种处理机制：浏览器 + 接口

```java
@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class BasicErrorController extends AbstractErrorController {
    // 针对浏览器请求的响应页面，产生html类型的数据
    @RequestMapping(produces = "text/html")
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) { }

    // 针对其他客户端请求的响应数据，产生json数据
    @RequestMapping
    @ResponseBody 
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) { }
}
```
>其中，html类型数据由 DefaultErrorViewResolver 解析产生，规则为

    （1）有模板引擎的情况下： error/状态码。如：error/404.html
    （2）没有模板引擎（模板引擎找不到这个错误页面），静态资源文件夹下找
    （3）以上都没有错误页面，就使用 SpringBoot 默认的错误提示页面
> json类型数据由 DefaultErrorAttributes 提供，其中包括

    timestamp：时间戳;    status：状态码; 
    error：错误提示;      exception：异常对象
    message：异常消息;    errors：JSR303数据校验的错误都在这里
##定制错误

> 定制错误页面

```java
将错误页面命名为 '错误状态码.html'，存放路径: 'templates/error/*.html'，发生错误就会来到 对应状态码的页面
文件名也可以使用 4xx 和 5xx 来模糊匹配状态码，当然精确匹配优先考虑！！
```
>第1版：接口和浏览器返回皆为json，没有做到自适应!!!

```java
//controller 的一个辅助类，最常用作全局异常处理的AOP切面类
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody //返回json
    @ExceptionHandler(BlueException.class)
    private Map<Object, Object> notFound(BlueException e) {
        Map<Object, Object> map = new HashMap<>();
        map.put("errMsg", e.errMsg);
        return map;
    }
}
```
>第2版：转发到 /error，进行自适应响应处理。未能显示用户自定义的异常信息

```java
@ExceptionHandler(BlueException.class)
private String notFound(HttpServletRequest req, BlueException e) {
    Map<Object, Object> map = new HashMap<>();
    map.put("errMsg", e.errMsg);

    // 传入自定义的错误状态码 4xx 5xx,否则就不会进入定制错误页面的解析流程
    req.setAttribute("javax.servlet.error.status_code", 500);

    // 转发到/error
    return "forword:/error";
}
```
>第3版：错误请求的自适应反馈（转发到定制错误页面或返回json），以及携带自定义的数据内容

```java
@ExceptionHandler(BlueException.class)
private String notFound(HttpServletRequest req, BlueException e) {
    Map<Object, Object> map = new HashMap<>();
    map.put("errCode", e.errCode);
    map.put("errMsg", e.errMsg);

    req.setAttribute("javax.servlet.error.status_code", 500);
    req.setAttribute("err", map);

    return "forward:/error";
}
```
>配合第3版共同使用：给容器中加入我们自己定义的 ErrorAttributes。`待完善`

```java
'再次强调：错误页面的数据集合由 DefaultErrorAttributes#getErrorAttributes() 提供!!!'
@Component
class BlueErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes, 
                                                  boolean includeStackTrace) {
        Map<String, Object> map = super.getErrorAttributes(requestAttributes, includeStackTrace);

        // 取出上述方法的'err'，放入错误页面的数据集合
        // 第二个参数：0代表从 request 中读取数据， 1代表从 session 中
        map.put("data", requestAttributes.getAttribute("err", 0));

        // 此map就是页面和json都能获取到的所有字段
        return map;
    }
}
```













