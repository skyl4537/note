[TOC]

# log

## 基础概念

> 日志级别：`OFF < TRACE < DEBUG < INFO < WARN < ERROR`

```shell
日志门面，日志的接口 #如：slf4j，jcl，jboss-logging
日志实现，具体实现类 #如：logback，log4j，log4j2（apache），jul
```

```shell
logback.xml        #直接被日志框架加载
logback-spring.xml #跳过日志框架，直接被 SpringBoot 加载，可以使用高级特性 Profile
```

> 配置多环境

```properties
#application.properties 中激活开发环境: spring.profiles.active=dev
开发环境: application-dev.properties ---> logging.level=DEBUG
生产环境: application-pro.properties ---> logging.level=INFO

#动态读取 application.properties 中配置的日志级别
log4j.rootLogger = ${logging.level}, stdout, file
log4j.logger.com.x.controller = ${logging.level}, ctrl
```

> 日志格式

```sh
#{%5p} -> 日志级别，5字符长度（5左边补空格，-5右边补）. %t -> 线程名；
%d{yyyy-MM-dd HH:mm:ss.SSS} [%5p] [%t] - %m%n
```
```sh
#【不建议】使用的参数，影响效率
%c -> 类全名相关；%l -> 日志发生位置； %r -> 自应用启动到输出该log信息耗费的毫秒数
```

>动态修改日志级别：`前提是 logback 日志框架`

```sh
#开启监控的所有节点
management.endpoints.web.exposure.include=*

#所有模块的日志级别
http://localhost:8080/web/actuator/loggers
#具体模块的日志级别
http://localhost:8080/web/actuator/loggers/com.example.web.controller

#发送 POST 请求到以上路径，动态修改以上模块的日志级别为 WARN，成功状态码为 '204'
curl -X POST http://localhost:8080/web/actuator/loggers/com.example.web.controller \
-H "Content-Type: application/json" --data '{"configuredLevel":"WARN"}'
```
> 使用方式

```java
log.info("变量os的取值为: " + macOS);  //方式-1（×）
log.info("变量os的取值为: {}", macOS); //方式-2（√）
//方式-1 对于日志输出设为 WARN 情况，也会先进行'字符串拼接'影响性能。但是，方式-2 则不存在这种问题，先判断输出级别，再进行字符串拼接。
```

> boot项目简单配置

```properties
#boot日志，默认只打印控制台。配置保存到文件，及日志级别
logging.file=/logs/web.log
logging.level.root=info
logging.level.com.example.web.controller=debug
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} [%-5p] [%t] - %m%n
```

> 使用原则

```sh
#ERROR：影响到程序正常运行的异常情况
打开配置文件失败
所有第三方对接的异常（包括第三方返回错误码）
所有影响功能使用的异常，包括：SQLException 和 除了业务异常之外的所有异常（RuntimeException和Exception）
不应该出现的情况：比如要使用Azure传图片，但是Azure未响应
```

```sh
#WARN：不应该出现但是不影响程序，当前请求正常运行的异常情况
有容错机制的时候出现的错误情况
找不到配置文件，但是系统能自动创建配置文件
即将接近临界值的时候，例如：缓存池占用达到警告线
业务异常的记录，比如：当接口抛出业务异常时，应该记录此异常
```

```sh
#INFO：系统运行信息
Service方法中对于 系统/业务 状态的变更
主要逻辑中的分步骤
客户端请求参数（REST/WS）
调用第三方时的 调用参数 和 调用结果
```

```sh
#DEBUG：生产环境需要关闭 DEBUG 信息
可以填写所有的想知道的相关信息(但不代表可以随便写，DEBUG 信息要有意义,最好有相关参数)
如果在生产情况下需要开启 DEBUG，需要使用开关进行管理，不能一直开启
```

##日志过滤

> `LevelFilter`：级别过滤器。如只输出级别 ERROR

```sh
DENY    #日志将立即被抛弃，不再经过其他过滤器
ACCEPT  #日志会被立即接收，..................
NEUTRAL #有序列表里的下一个过滤器会接着处理日志
```
```xml
<filter class="ch.qos.logback.classic.filter.LevelFilter">
    <level>ERROR</level>
    <onMatch>ACCEPT</onMatch> 
    <onMismatch>DENY</onMismatch>
</filter>
```
```properties
log4j.logger.com.x.sm = DEBUG, err

log4j.appender.err.filter.a=org.apache.log4j.varia.LevelRangeFilter
log4j.appender.err.filter.a.LevelMin=ERROR
log4j.appender.err.filter.a.LevelMax=ERROR
log4j.appender.err.filter.a.acceptOnMatch=true
log4j.appender.err=org.apache.log4j.DailyRollingFileAppender
log4j.appender.err.File=/var/lib/X/logs/sm/error
log4j.appender.err.DatePattern='.'yyyyMMdd'.log'
log4j.appender.err.layout=org.apache.log4j.PatternLayout
log4j.appender.err.layout.ConversionPattern=%d{HH:mm:ss.SSS} - %m%n
```

> `ThresholdFilter`：临界值过滤器。如输出级别 >= INFO

```xml
<filter class="ch.qos.logback.classic.filter.ThresholdFilter">
    <level>INFO</level>
</filter>
```

```properties
log4j.logger.com.x.sm = DEBUG, info

log4j.appender.info.Threshold=INFO
log4j.appender.info=org.apache.log4j.DailyRollingFileAppender
log4j.appender.info.File=/var/lib/webpark/logs/sm/error
log4j.appender.info.DatePattern='.'yyyyMMdd'.log'
log4j.appender.info.layout=org.apache.log4j.PatternLayout
log4j.appender.info.layout.ConversionPattern=%d{HH:mm:ss.SSS} - %m%n
```

## 异步输出

> 每次输出日志就会发生一次磁盘IO，损耗性能。

> 异步输出：日志先缓存，缓存达到一定量级，再一次性的输出。

```xml
<!--异步 appender 必须跟在同步后面，否则不起作用-->
<appender name="ASYNC_INFO_APPENDER" class="ch.qos.logback.classic.AsyncAppender">

    <!--当 BlockingQueue 还有 20% 容量，将丢弃 TRACE、DEBUG 和 INFO 级别的日志-->
    <!--只保留 WARN 和 ERROR 级别的日志。为保持所有的日志，将该值设置为0。默认值20-->
    <discardingThreshold>0</discardingThreshold>
    <queueSize>256</queueSize> <!-- 缓冲区大小，默认值256个-->

    <!--异步appender并不自己写日志，只是将日志输出到 BlockingQueue-->
    <!--最终还是具体的appender将日志输出到文件-->
    <appender-ref ref="INFO_APPENDER"/>
</appender>
```

## 框架切换

> logback ---> log4j

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
    <exclusions>
        <exclusion> <!--根据 Dependency Hierarchy 界面,搜索"logback"找到其父依赖-->
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId> <!--排除boot自带logging-->
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId> <!--引入'log4j'-->
    <artifactId>spring-boot-starter-log4j</artifactId>
    <version>1.3.8.RELEASE</version>
</dependency>
```

> logback ---> log4j2

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId> <!--排除boot自带'logging'-->
            <artifactId>spring-boot-starter-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>    <!--引入'log4j2'包-->
    <artifactId>spring-boot-starter-log4j2</artifactId>
</dependency>
```

## DEMO

> logback

```xml
<?xml version="1.0" encoding="UTF-8"?>

<!-- scan: 配置文件发生改变,是否重新加载,默认 true -->
<!-- scanPeriod: 监测配置文件是否改变的频率(scan为true时才生效),默认1分钟.如未给出时间单位,默认毫秒-->
<!-- debug: 是否打印logback内部日志信息,实时查看logback运行状态. 默认 false -->
<configuration scan="true" scanPeriod="60 seconds" debug="false">

    <!-- 设置上下文, 一旦设置，不能修改,可以通过 %contextName 在日志中输出上下文对应的值 -->
    <!-- <contextName>logback</contextName> 
    <property name="PATTERN_FILE" value="%d{HH:mm:ss.SSS} %contextName [%5level] %logger{5} - %m%n" /> -->

    <!--加载外部的yml配置文件.(文件名不能使用 logback.xml,加载太早,必须改为 logback-spring.xml)-->
    <!--scope固定值,用${}取值; name配置文件中属性对应的变量名; source配置文件中属性，defaultValue为缺省值 -->
    <!-- <springProperty scope="context" name="LOG_HOME" source="aopAll.home" defaultValue="blues/logs" />-->

    <!-- 设置变量 -->
    <!-- <property name="LOG_HOME" value="${LOG_HOME}" />--> <!-- 对应上面的加载外部配置文件-->
    <!-- <property name="APP_NAME" value="blue"/>-->
    <!-- <property name="LOG_HOME" value="${APP_NAME}/logs"/>-->

    <property name="LOG_HOME" value="logs" />
    <property name="PATTERN_CONSOLE" value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%5p] [%t] - %m%n" />
    <property name="PATTERN_FILE" value="%d{HH:mm:ss.SSS} [%5p] [%t] - %m%n" />

    <!-- CTRL_DEBUG -->
    <!-- 滚动记录文件: 先将日志记录到临时文件,当符合某个条件时,再将日志归档到目标文件 -->
    <appender name="CTRL_DEBUG" class="ch.qos.logback.core.rolling.RollingFileAppender">

        <!-- 可选节点，归档前临时文件的路径。不指定，默认直接写入归档后的目标文件 -->
        <file>${LOG_HOME}/ctrl/debug/debug</file>

        <!-- 按照'大小和时间'两种策略综合滚动 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <!-- 必选节点，归档后的文件路径 -->
            <!-- 支持 *.zip和 *.gz压缩模式（单个文件超过最大容量才会压缩，后缀名改为 .zip即可压缩） -->
            <!-- 每小时一归档：当大小超过 maxFileSize 时，按照 i 进行文件归档 -->
            <fileNamePattern>${LOG_HOME}/ctrl/debug/debug_%d{yyyyMMdd_HH}_%i.zip</fileNamePattern>

            <!-- 单个日志文件最大1MB, 最多保存5个小时的日志, 总日志大小不能超过5MB -->
            <!-- 当 MaxHistory 或 totalSizeCap 都满足时,自动删除旧的日志 -->
            <maxFileSize>1MB</maxFileSize>
            <MaxHistory>5</MaxHistory>
            <totalSizeCap>5MB</totalSizeCap>
        </rollingPolicy>

        <encoder>  <!-- 日志输出格式 -->
            <pattern>${PATTERN_FILE}</pattern>
        </encoder>
    </appender>

    <!--  异步appender 必须跟在同步后面,否则不起作用  -->
    <appender name="CTRL_DEBUG_ASYNC" class="ch.qos.logback.classic.AsyncAppender">
        <!--当 BlockingQueue 还有20%容量,将丢弃 TRACE,DEBUG,INFO 级别的event,只保留 WARN和ERROR 级别的event-->
        <!--为保持所有的events,将该值设置为0. 默认值20 -->
        <discardingThreshold>0</discardingThreshold>
        <!-- BlockingQueue 的最大容量,该值影响性能. 默认值256 -->
        <queueSize>256</queueSize>
        <!-- 异步appender并不自己写日志,只是将日志输出到 BlockingQueue,最终还是具体的appender将日志输出到文件 -->
        <!-- 图示详见: http:www.importnew.com/27247.html -->
        <appender-ref ref="CTRL_DEBUG" />
    </appender>

    <!-- CTRL_WARN -->
    <appender name="CTRL_WARN" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_HOME}/ctrl/warn/warn</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_HOME}/ctrl/warn/warn_%d{yyyyMMdd_HH}_%i.log
            </fileNamePattern>
            <maxFileSize>1MB</maxFileSize>
            <MaxHistory>5</MaxHistory>
            <totalSizeCap>5MB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>${PATTERN_FILE}</pattern>
        </encoder>

        <!--LevelFilter: 级别过滤器. 等于配置级别,根据 onMath 和 onMismatch 接收或拒绝日志 -->
        <!--ThresholdFilter: 临界值过滤器,过滤掉低于指定临界值的日志(只输出等于或高于临界值的日志).-->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>WARN</level>  <!-- ONLY WARN -->
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
    </appender>

    <!-- CTRL_ERROR -->
    <appender name="CTRL_ERROR" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_HOME}/ctrl/error/error</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_HOME}/ctrl/error/error_%d{yyyyMMdd_HH}_%i.log
            </fileNamePattern>
            <maxFileSize>1MB</maxFileSize>
            <MaxHistory>5</MaxHistory>
            <totalSizeCap>5MB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>${PATTERN_FILE}</pattern>
        </encoder>
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>ERROR</level>  <!-- ERROR+ -->
        </filter>
    </appender>

    <!-- 控制台 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${PATTERN_CONSOLE}</pattern>
        </encoder>
    </appender>

    <!-- LOGFILE -->
    <appender name="LOGFILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_HOME}/log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>${LOG_HOME}/log_%d{yyyyMMdd}_%i.log</fileNamePattern>
            <maxFileSize>1MB</maxFileSize>
            <MaxHistory>5</MaxHistory>
            <totalSizeCap>5MB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>${PATTERN_FILE}</pattern>
        </encoder>
    </appender>

    <!-- 必选节点; 特殊的logger元素,用来指定最基础的日志输出级别 -->
    <root level="info" additivity="true">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="LOGFILE" />
    </root>

    <!-- 可选节点; 设置某个包或具体某个类的日志级别,以及<appender> (覆盖root节点的输出级别) -->
    <!-- name: 受此logger约束的某一个包或具体某一个类 -->
    <!-- level: 日志级别, 默认继承上级的打级别 -->
    <!-- additivity: 是否向上级logger传递打印信息. 默认是true -->
    <logger name="com.example.spring.controller" level="DEBUG">
        <appender-ref ref="CTRL_DEBUG_ASYNC" /> <!-- DEBUG+ -->
        <appender-ref ref="CTRL_WARN" /> <!-- ONLY WARN -->
        <appender-ref ref="CTRL_ERROR" /> <!-- ERROR+ -->
    </logger>
</configuration>
```

> log4j

```properties
#系统 -> 配置(基础-级别, 控制台, 文件)
#log4j.rootCategory=INFO, CONSOLE, LOGFILE //rootCategory 已过时
log4j.rootLogger=INFO, CONSOLE, LOGFILE

#系统 -> 控制台
log4j.appender.CONSOLE=org.apache.log4j.ConsoleAppender
log4j.appender.CONSOLE.layout=org.apache.log4j.PatternLayout
log4j.appender.CONSOLE.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss.SSS} [%5p] [%t] - %m%n

#系统 -> 文件
log4j.appender.LOGFILE=org.apache.log4j.DailyRollingFileAppender
log4j.appender.LOGFILE.file=/logs/log
log4j.appender.LOGFILE.DatePattern='_'yyyyMMdd'.log'
log4j.appender.LOGFILE.layout=org.apache.log4j.PatternLayout
log4j.appender.LOGFILE.layout.ConversionPattern= %d{HH:mm:ss.SSS} [%5p] [%t] - %m%n

#controller -> 配置 -> (包-级别, 文件1, 文件2, 文件3)
log4j.logger.com.example.spring.controller=DEBUG, CTRL_DEBUG, CTRL_WARN, CTRL_ERROR

#controller -> 文件1 -> DEBUG+
log4j.appender.CTRL_DEBUG=org.apache.log4j.DailyRollingFileAppender
log4j.appender.CTRL_DEBUG.File=/logs/ctrl/debug
log4j.appender.CTRL_DEBUG.DatePattern='_'yyyyMMdd_HH'.log'
log4j.appender.CTRL_DEBUG.layout=org.apache.log4j.PatternLayout
log4j.appender.CTRL_DEBUG.layout.ConversionPattern=%d{HH:mm:ss.SSS} [%5p] [%t] - %m%n

#controller -> 文件2 -> ONLY WARN
log4j.appender.CTRL_WARN.filter.a=org.apache.log4j.varia.LevelRangeFilter
log4j.appender.CTRL_WARN.filter.a.LevelMin=WARN
log4j.appender.CTRL_WARN.filter.a.LevelMax=WARN
log4j.appender.CTRL_WARN.filter.a.acceptOnMatch=true
log4j.appender.CTRL_WARN=org.apache.log4j.DailyRollingFileAppender
log4j.appender.CTRL_WARN.File=/logs/ctrl/warn/warn
log4j.appender.CTRL_WARN.DatePattern='_'yyyyMMdd_HH'.log'
log4j.appender.CTRL_WARN.layout=org.apache.log4j.PatternLayout
log4j.appender.CTRL_WARN.layout.ConversionPattern=%d{HH:mm:ss.SSS} [%5p] - %m%n

#controller -> 文件3 -> ERROR+
log4j.appender.CTRL_ERROR.Threshold=ERROR
log4j.appender.CTRL_ERROR=org.apache.log4j.DailyRollingFileAppender
log4j.appender.CTRL_ERROR.File=/logs/ctrl/error/error
log4j.appender.CTRL_ERROR.DatePattern='_'yyyyMMdd_HH'.log'
log4j.appender.CTRL_ERROR.layout=org.apache.log4j.PatternLayout
log4j.appender.CTRL_ERROR.layout.ConversionPattern=%d{HH:mm:ss.SSS} [%5p] [%t] - %m%n
```

# 短信Sms

##基础配置

> 短信服务使用 阿里云通信

```java
（1）注册，登陆，实名，产品选择'短信服务'
（2）申请签名，申请模板，创建 AccessKey，充值
```

> 配置文件

```xml
<!-- https://mvnrepository.com/artifact/com.aliyun/aliyun-java-sdk-dysmsapi -->
<dependency>
    <groupId>com.aliyun</groupId>
    <artifactId>aliyun-java-sdk-dysmsapi</artifactId>
    <version>1.1.0</version>
</dependency>
<!-- https://mvnrepository.com/artifact/com.aliyun/aliyun-java-sdk-core -->
<dependency>
    <groupId>com.aliyun</groupId>
    <artifactId>aliyun-java-sdk-core</artifactId>
    <version>3.2.8</version>
</dependency>
```

```properties
aliyun.sms.accessKeyId=*
aliyun.sms.accessKeySecret=*
aliyun.sms.templateCode=sms_20190328 #模板CODE
aliyun.sms.signName=短信测试          #签名名称
```

>SMS工具类

```java
/**
 * 短信工具类
 *
 * @author Administrator
 */
@Component
public class SmsUtils {

    //产品名称:云通信短信API产品,开发者无需替换
    static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    static final String domain = "dysmsapi.aliyuncs.com";

    @Autowired
    private Environment env;

    // TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)

    /**
     * 发送短信
     *
     * @param mobile        手机号
     * @param template_code 模板号
     * @param sign_name     签名
     * @param param         参数
     * @return
     * @throws ClientException
     */
    public SendSmsResponse sendSms(String mobile, String template_code, String sign_name, String param) throws
            ClientException {
        String accessKeyId = env.getProperty("aliyun.sms.accessKeyId");
        String accessKeySecret = env.getProperty("aliyun.sms.accessKeySecret");
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);
        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填：待发送手机号
        request.setPhoneNumbers(mobile);
        //必填：短信签名-可在短信控制台中找到
        request.setSignName(sign_name);
        //必填：短信模板-可在短信控制台中找到
        request.setTemplateCode(template_code);
        //可选：模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam(param);
        //选填，上行短信扩展码(无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");
        //可选，outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId("yourOutId");
        //hint 此处可能会抛出异常，注意catch
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
        return sendSmsResponse;
    }

    //查询发送短信的详情：如当天的发送消息总数
    public QuerySendDetailsResponse querySendDetails(String mobile, String bizId) throws ClientException {
        String accessKeyId = env.getProperty("accessKeyId");
        String accessKeySecret = env.getProperty("accessKeySecret");
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);
        //组装请求对象
        QuerySendDetailsRequest request = new QuerySendDetailsRequest();
        //必填-号码
        request.setPhoneNumber(mobile);
        //可选-流水号
        request.setBizId(bizId);
        //必填-发送日期 支持30天内记录查询，格式yyyyMMdd
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
        request.setSendDate(ft.format(new Date()));
        //必填-页大小
        request.setPageSize(10L);
        //必填-当前页码从1开始计数
        request.setCurrentPage(1L);
        //hint 此处可能会抛出异常，注意catch
        QuerySendDetailsResponse querySendDetailsResponse = acsClient.getAcsResponse(request);
        return querySendDetailsResponse;
    }
}
```

>SMS发送

```java
@Value("${aliyun.sms.templateCode}") //模板编号Code
String templateCode;

@Value("${aliyun.sms.signName}") //签名
String signName;

@Autowired
SmsUtil smsUtil;

//发送短信
smsUtil.sendSms(moblieNum, templateCode, signName, "{\"checkCode\":\"" + checkCode + "\"}");
```

##注册DEMO


>用户填写手机号，点击获取验证码。服务器向用户填写的手机号发送验证码。

```java
@PostMapping("/sendSms/{mobile}")
public String sendSms(@PathVariable String mobile) {
    if (StringUtils.isEmpty(mobile)) { //正则检测略
        return "手机号不合法";
    }
    return HelloService.sendSms(mobile);
}
```
>发送短信Service

```java
public void sendSms(String mobile) {
    //(1).生成验证码
    String checkCode = RandomStringUtils.randomNumeric(6); //org.apache.commons.lang3

    //(2).验证码存入redis，5分钟失效
    redisTemplate.opsForValue().set("checkCode_" + mobile, checkCode, 5, TimeUnit.MINUTES);

    //(3).发送消息 RabbitMQ，短信验证
    JSONObject object = new JSONObject();
    object.put("mobile", mobile);
    object.put("checkCode", checkCode);
    rabbitTemplate.convertAndSend("spring.sms", object);
}
```

>用户将收到的验证码填入，然后点击注册

```java
@PostMapping("/regist/{mobile}/{checkCode}")
public String regist(@PathVariable String mobile, @PathVariable String checkCode) {
    if (StringUtils.isEmpty(mobile)) { //正则检测略
        return "手机号不合法";
    }
    return HelloService.regist(mobile, checkCode);
}
```
>用户注册Service

```java
//service, dao --> 抛出异常; Controller/全局异常处理器 --> 处理异常.
private void regist(String mobile, String checkCode) {
    if (StringUtils.isEmpty(checkCode))
        throw new RuntimeException("请输入验证码");

    if (!smsCodeRedis.equalsIgnoreCase(checkCode))
        throw new RuntimeException("验证码不正确或已过期");

    personDao.add(person); //写库
}
```
##中国网建

>测试账户

```java
private static final String ZGWJ_NAME = "bluecardsoft"; //中国网建账号
private static final String ZGWJ_PASS = "537915b43b2b99a355df"; //中国网建发短息密钥

SmsUtils.sendSMS(ZGWJ_NAME, ZGWJ_PASS, "1761061****", "测试文档");
```
>短信工具类

```java
public static String sendSMS(String userName, String key, String toMobile, String smsText) {
    List<NameValuePair> nvps = new ArrayList<>(4);
    nvps.add(new BasicNameValuePair("Uid", userName));
    nvps.add(new BasicNameValuePair("Key", key));
    nvps.add(new BasicNameValuePair("smsMob", toMobile));
    nvps.add(new BasicNameValuePair("smsText", smsText));
    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps, Charset.forName("GBK")); //中文乱码
    entity.setContentType("application/x-www-form-urlencoded;charset=gbk");

    HttpPost httpPost = new HttpPost("http://gbk.sms.webchinese.cn");
    httpPost.setEntity(entity);

    try (CloseableHttpResponse httpResponse =
         HttpClients.createDefault().execute(httpPost)) { //发送请求，连接自动关闭
        if (null != httpResponse && HttpStatus.SC_OK ==
            httpResponse.getStatusLine().getStatusCode()) {
            String res = EntityUtils.toString(httpResponse.getEntity(), "UTF-8"); //获取结果
            System.out.println(res);
            return res;
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
    return null;
}
```
# BCrypt加密

##基础概念

>HASH算法不可逆，所以不能解密

```java
任何应用考虑到安全，绝不能明文的方式保存密码。密码应该通过哈希算法进行加密。有很多标准的算法比如 SHA 或 MD5，结合salt（盐）是一个不错的选择。
Spring Security 提供了 BCryptPasswordEncoder 类，实现Spring的 PasswordEncoder 接口使用'BCrypt强哈希方法'来加密密码。
```
```java
//都是HASH算法
'纯md5加密'：可以使用密码字典破解，暴力破解。（X）
'md5加盐加密'：一旦知道 salt 和加密规则，就可以破解所有的密码。（X）
'md5随机盐加密'：不同用户分配不同的salt。必须得单独保存salt，验证时使用。（X）

'BCrypt加密'：随机生成salt，并混入最终加密后的密码。验证时也无需单独提供之前的salt，从而无需单独处理salt问题。
```

```java
//BCrypt加密对于同一个密码，每次生成的hash不一样。因为随机salt
但是，hash结果中包含了salt（hash产生过程：先随机生成salt，salt跟password进行hash）。

在下次校验时，从hash中取出salt，salt跟password进行hash。得到的结果跟保存在DB中的hash进行比对。
```

##配置使用

> 基础配置

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```
```java
@Bean //注入Bean
public BCryptPasswordEncoder encoder(){
    return new BCryptPasswordEncoder();
}
```

> 必要配置

```java
添加了spring security依赖后，所有的地址都被spring security所控制了。
目前只是需要用到'BCrypt密码加密'的部分，所以要添加一个配置类，配置为所有地址都可以匿名访问。
```

```java
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // super.configure(http); //必须注掉
        
        http.authorizeRequests() //开启权限验证
                .antMatchers("/**").permitAll() //拦截所有路径，任何权限都可以访问
                .anyRequest().authenticated() //任意请求，认证后才可以访问
                .and().csrf().disable(); //固定写法：表示使CRSF（网络攻击技术）拦截失效
    }
}
```

> 加密测试

```java
@Autowired
BCryptPasswordEncoder encoder;

@Test
public void encoder() {
    String encode = encoder.encode("123");
    System.out.println("encode: " + encode); //加密: $2a$10$ujGzEaaHHU0y72yzfwMk.OA0KUNpKfRFr291I5YuGqnWawmnAQV1y

    boolean matches = encoder.matches("123", "$2a$10$ujGzEaaHHU0y72yzfwMk.OA0KUNpKfRFr291I5YuGqnWawmnAQV1y");
    System.out.println("matches: " + matches); //比对: true
}
```


# 开发手册

## 日志规约

>【强制】对 trace/debug/info 级别的日志输出，必须使用条件输出形式或者使用占位符的方式

```sh
说明：logger.debug("Processing trade with id: " +  id + " and symbol: " +  symbol);
如果日志级别是 warn，上述日志不会打印，但是会执行字符串拼接操作。
如果 symbol 是对象，会执行 toString() 方法，浪费了系统资源，执行了上述操作，最终日志却没有打印。
```

```java
//正例：（占位符）
logger.debug("Processing trade with id: {} and symbol : {} ", id, symbol);
```

```java
//正例：（条件）建设采用如下方式
if (logger.isDebugEnabled()) {
    logger.debug("Processing trade with id: " + id + " and symbol: " + symbol);
}
```

> 【强制】异常信息应该包括两类信息：案发现场信息和异常堆栈信息。如果不处理，那么通过关键字 throws 往上抛出

```java
logger.error(各类参数或者对象.toString() + "_" + e.getMessage(), e); //正例
```

>【强制】应用中的扩展日志（如打点、临时监控、访问日志等）命名方式：`appName_logType_logName.log`

```sh
logType：日志类型，如 stats/monitor/access 等 ；logName：日志描述。
这种命名的好处：通过文件名就可知道日志文件属于什么应用，什么类型，什么目的，也有利于归类查找。

正例： mppserver 应用中单独监控时区转换异常，如：mppserver_monitor_timeZoneConvert.log
说明：推荐对日志进行分类，如将错误日志和业务日志分开存放，便于开发人员查看，也便于通过日志对系统进行及时监控
```

> 其他注意

```sh
#【强制】应用中不可直接使用日志系统（log4j、logback）中的 API，而应依赖使用日志框架 slf4j 中的 API
使用门面模式的日志框架，有利于维护和各个类的日志处理方式统一。
private static final Logger logger = LoggerFactory.getLogger(Abc.class);

#【强制】日志文件至少保存 15 天，因为有些异常具备以“周”为频次发生的特点

#【强制】避免重复打印日志，浪费磁盘空间，务必在 log4j.xml 中设置 additivity = false
正例： <logger name="com.taobao.dubbo.config" additivity="false">

#【推荐】谨慎地记录日志。生产环境禁止输出 debug 日志；有选择地输出 info 日志；
如果使用 warn 来记录刚上线时的业务行为信息，一定要注意日志输出量的问题，避免把服务器磁盘撑爆，并记得及时删除这些观察日志。
说明：大量地输出无效日志，不利于系统性能提升，也不利于快速定位错误点。
记录日志时请思考：这些日志真的有人看吗？看到这条日志你能做什么？能不能给问题排查带来好处？

#【推荐】可以使用 warn 日志级别来记录用户输入参数错误的情况，避免用户投诉时，无所适从。如非必要，请不要在此场景打出 error 级别，避免频繁报警。
说明：注意日志输出的级别，error 级别只记录系统逻辑出错、异常或者重要的错误信息。
```
