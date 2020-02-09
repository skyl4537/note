[TOC]

# IDEA

## 插件相关

> IDEA插件

```sh
#IDEA-安装 lombok
下载：https://github.com/mplushnikov/lombok-intellij-plugin/releases
Settings -> Plugins -> install from disk -> 选择下载的*.zip

#eclipse-安装
下载: https://projectlombok.org/download
将 lombok.jar 放在eclipse安装目录下，与 eclipse/sts.ini 同级
当前目录打开cmder，使用命令: "java -jar ./lombok.jar"，弹框选择 Install/Update
成功标识: sts.ini最后一行：-javaagent:F:\sts-bundle\sts-3.9.3.RELEASE\lombok.jar
```

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope> <!-- 只在编译期生效，不会打入包中 -->
    <optional>true</optional> <!-- 依赖不传递? 默认 false. 即会传递到引用项目中 -->
</dependency>
```

> 谷歌插件

```sh
JSON-Handle
OneTab_v1.18
Advanced_Rest_Client_Chrome
```

> 日常工具

```sh
#cmder：mini 与 full 版的差别在于有没有内建 msysgit 工具
右键菜单：配置系统环境变量,然后使用系统cmd执行命令: Cmder.exe /register ALL
中文乱码：settings -> Environment -> 添加: set LANG=zh_CN.UTF-8
```

```sh
#WinScp-代理上网
登陆时，高级 - 连接 - 代理 - HTTP
```

```sh
#SecurityCRT-代理上网
选项 - 全局选项 - 防火墙 - 添加 - HTTP(no-auth)
登录时，连接 - Sessions - 右键属性 - SSH2 - 防火墙 - 选择上一步添加的规则
```

## 常见问题

>如何在IDEA中多次启动同一个项目

```sh
启动绿三角 左边的 'Edit Config...'，在弹出窗口中取消勾选 'Single-instance-only（单例模式）'，点击OK
每次运行 SpringBoot 项目前，修改配置文件中的端口号即可。
```

>复制警告或错误信息

```sh
方式一：鼠标光标悬浮在报错的地方，待错误提示出现后，键盘按住 Alt，同时点击鼠标左键，Ctrl+V 到度娘即可。
方式二：直接在界面的底部右键copy，错误信息显示在底部。
```

> mvn打包时，跳过Test

```sh
'mvn命令'：mvn clean package -Dmaven.test.skip=true
打开右侧"Maven-Projects"，当前项目'Lifecycle'，选中'Test'，点击菜单栏的"小闪电"，此时Test上多了一条横
```

## 默认配置

>当前项目配置 VS 默认配置

```sh
'当前配置'：顶部导航栏 -> File -> Settings
'默认配置'：顶部导航栏 -> File -> Other_Settings -> Default Settings | Default Project Structure
```

> 默认配置

```SH
#jdk
Default Project Structure -> SDKs -> JDK
#mvn
Default Settings -> Build -> Maven
#创建Maven项目速度慢问题
Default Settings -> Builder -> Maven -> Runner -> VM Options：-DarchetypeCatalog=internal
#svn
Default Settings -> Version Control -> Git

#自动编译
Default Settings -> Build... -> Compiler -> （√）Build project auto...
#自动导包
Default_Settings -> Auto Import
（√）add unambiguous...自动导入依赖
（√）Optimize from... 优化导入和智能删除无关依赖

#取消大小写敏感
Default Settings -> Editor -> General -> Code Completion -> Case Sensitive Completion (选择)None
#调整字体大小
打开配置，搜索Font，然后再Font可以调整字体类型，Size可以调整字体大小

#Tomcat
顶部工具栏 File -> Settings -> Deployment -> Application Servers -> Tomcat Server，选择 Tomcat 的根目录
```

> 快捷键

```sh
'F2修改文件名'  ：File -> Settings -> Keymap -> 搜索 Rename -> 将快捷键设置为F2
'F3浏览目录结构'：File -> Settings -> Keymap -> 搜索 Show In Explorer -> 将快捷键设置为F3
```



## 其他配置

> 快捷键

```sh
psvm           #main方法
sout           #输出到控制台
fori           #for循环
iter           #增强for循环
```

```shell
Ctrl+Alt+Space #代码补全
Ctrl+Alt+L     #格式化
Ctrl+Alt+O     #优化导包
Ctrl+Shift+F   #全局查找

Shift+Alt+↑      #上下移动单行语句（整个方法：Shift+Ctrl+↑）
Ctrl+/           #单行注释（多行注释：Ctrl+Shift+/）
F3（Shift+F3）    #跳到下（上）一个选择项（配合Ctrl+F使用）
Alt+F7（Ctrl+F7） #在全局（当前类）查找方法调用，可配合F3使用

Ctrl+Shift+BackSpace  #跳转到上次编辑的地方
Ctrl+Shift+Alt+N      #当前工作空间查找java类
Shift+Ctrl+减号（加号） #折叠（展开）代码块
```

```sh
Ctrl+Alt+T     #try-catch
Alt+Insert     #GET/SET，Test方法
Ctrl+Shift+T   #创建对应的测试类
F8 / F7        #调试：跳到下一步（进入代码）
```

```sh
Ctrl+Alt+V     #抽取局部变量
Ctrl+Alt+F     #抽取成员变量
Ctrl+Alt+C     #抽取静态常量 public static final
Ctrl+Alt+M     #抽取方法

Shift+F6       #批量重命名
Ctrl+Shift+U   #大小写转化
```

```sh
Ctrl+D         #整行复制
Ctrl+X         #整行删除
Ctrl+P         #参数提示
Ctrl+G         #定位行
Ctrl+H         #查看类的继承关系
Alt+7          #查看类结构
```
> class类的doc模板

```java
（1）File -> Setting -> Editor -> File and Code Templates

（2）选择Tab页'Includes'，点击'+'，name填写'File Header'，内容填写
   /**  
    * @desc: TODO
    * @author: ${USER}
    * @date: ${DATE} ${TIME}
    */
```

> 方法的doc模板

```java
（1）File -> Setting -> Editor -> Live Templates
（2）点击最右边+，创建一个Template Group
（3）填写group名，任意填写。选中你刚刚创建的group，创建Live Template

（4）Abbreviation，即快捷方式，可填写 doc，然后在函数体上方输入doc加回车即可
（5）输入注释模板
   /**
    * desc: TODO
    * @author: $user$
    * @date: $date$ $time$
    */
（6）右侧'edit variables'，分别选择 $user$，$date$，$time$ 对应的表达式 user()，date()，time()
（7）设置doc快捷键生效位置：最低端的'change'，打开选择'everywhere'
```






> 基本设置

```java
//黑色主题 --> 界面的字体大小,非代码字体
Appearance & Behavior - Appearance - Theme(选为Darcula) 
勾选 Override default fonts by(......) - Name(Mircrosoft Yahei UI) - Size(12)
    
//改变代码的字体和大小
Editor - Colors & Fonts
//首先,点击 Save As...,自定义一个名为 skyl 的样式
//然后,选择具体的字体和大小 Primary font(Source Code Pro) - Size(15)
    
//缩进采用4个空格,禁止使用tab字符
Editor - Code Style - java - Tabs and Indents - Use tab character(取消勾选)
    
//自动换行
Editor - Code Style - Java
右侧标签 Wrapping and Braces, (√) Line breaks 和 (√) Ensure right margin is not exceeded
    
//悬浮文档提示
Editor - General - Show quick documentation on...
    
//代码提示忽略大小写
Editor - General - Code Completion - Case sensitive...(None)
//代码补全快捷键: Ctrl + Alt + Space
    
//编码格式
Editor - File Encodings - 3个UTF-8

//显示行号等
Editor - General - Appearance //勾选以下
    (√)Show line number(行号) + (√)Show right margin(右边线) + (√)Show method sep...(方法分割线)

//自动导包
Editor - General - Auto Import 
    Insert imports...(All) + (√)Add unambiguous... + (√)Optimize imports...
    
//设置文件和代码的模板
Editor - File and Code Templates - Includes - 自行添加

//取消单行显示tabs
Editor - General - Editor Tabs - (X)show tabs in single...
    
//自动编译
Build,Exe... - Compiler - (√)Build project automatically
    
//Gradle配置
Build,Exe... - Build Tools - Gradle - Offline work
```

> 版本相关：Alpha，Beta，SNAPSHOT，Release，GA

```java
'Alpha': 内部测试版。一般不向外部发布，会有很多Bug。一般只有测试人员使用。
'Beta': 测试版。这个阶段的版本会一直加入新的功能，在Alpha版之后推出。
'RC(Release Candidate)': 候选版本。不会再加入新的功能，主要着重于除错。
'SNAPSHOT': 不稳定，尚处于开发中的版本。

'GA(General Availability)': 正式发布版本。在国外都是用GA来说明'Release'版本的。
```











#log

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

#Maven

`约定 > 配置 > 编码` ==》 能用配置解决的问题就不编码，能基于约定的就不进行配置

## 基础概念

>为什么使用Maven这样的构建工具？

```shell
#一个项目就是一个工程
如果项目非常庞大，就不适合使用 package 来划分模块，最好是每一个模块对应一个工程，利于分工协作。
借助于maven就可以将一个项目拆分成多个工程。

#项目中使用jar包，需要[复制+粘贴]项目的lib中
同样的jar包重复的出现在不同的项目工程中，你需要做不停的复制粘贴的重复工作。
借助于maven，可以将jar包保存在'仓库'中，不管在哪个项目只要使用引用即可就行。

#jar包需要的时候每次都要自己准备好或到官网下载
借助于maven我们可以使用统一的规范方式下载jar包，规范

#jar包版本不一致的风险
不同的项目在使用jar包的时候，有可能会导致各个项目的jar包版本不一致，导致未知错误。
借助于maven，所有的jar包都放在'仓库'中，所有的项目都使用仓库的一份jar包。

#一个jar包依赖其他的jar包需要自己手动的加入到项目中
借助maven，它会自动的将依赖的jar包导入进来。
```

> 构建：把动态的Web工程经过编译得到的编译结果，并部署到服务器上的整个过程。

```shell
#构建的各个环节
清理-clean   ：将以前编译得到的旧文件class字节码文件删除
编译-compile ：将java源程序编译成class字节码文件
测试-test    ：自动测试，自动调用junit程序
报告-report  ：测试程序执行的结果
打包-package ：动态Web工程打War包，java工程打jar包
安装-install ：Maven特定的概念--->将打包得到的文件复制到'仓库'中的指定位置
部署-deploy  ：部署--->将动态Web工程生成的war包复制到Servlet容器下，使其可以运行
```

> 常用命令：执行mvn命令必须进入到 pom.xml 的目录中进行执行

```shell
mvn clean        ：清理
mvn compile      ：编译主程序
mvn test-compile ：编译测试程序
mvn test         ：执行测试
mvn package      ：项目打包
mvn install      ：项目打包，'并将jar/war复制到本地仓库'
```

>依赖范围

```sh
'compile'：默认值，适用于所有阶段（build、test、runtime），本jar会一直存在。 #如: spring-core
'test'   ：只在test阶段使用，用于编译和运行测试代码，不会随项目发布。 #如: junit
```

```sh
'provided'：只在build、test阶段使用，在runtime时由容器提供。#如: servlet-api（tomcat提供），lombok

例如，在开发 web 应用时，编译期需要一个 servlet.jar 来编译程序中的 servlet，但打包时，不需要此 servlet.jar。
因为，程序运行时，由servlet容器（tomcat）来提供 servlet.jar。
```

```xml
<!-- 如需将 SpringBoot 项目放在外置 tomcat 中运行，可将tomcat依赖的使用范围改写为 provided -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>
</dependency>
```

```shell
'runtime'：在build阶段无需参与项目的编译，不过后期的test和runtime需要。#如: mysql-connector-java

这种主要是指代码里并没有直接引用而是根据配置在运行时动态加载并实例化的情况。
虽然用runtime的地方改成compile也不会出大问题，但是runtime的'好处'是可以避免在程序里意外地直接引用到原本应该动态加载的包。
#另外，runtime 的依赖通常和 optional 搭配使用，optional为true。我可以用A实现，也可以用B实现。
```

```sh
#一种理解
通过 compile 和 provided 引入的jar包，里面的类，你在项目中可以直接 import 进来用，编译没问题。
但是，runtime 引入的jar包中的类，项目代码里不能直接用，用了无法通过编译，只能通过'反射'之类的方式来用。

通过 compile 和 runtime 引入的jar包，会出现在项目war包里，而 provided 引入的jar包则不会。
```

>依赖的传递性：Optional

```sh
'<optional>true</optional>': 依赖不会传递，但是该依赖写在父项目则所有子类都可用。false: 会传递，默认false

#（1） 如果依赖的是自己或者团队开发的maven工程，需要先使用 install 命令把被依赖的maven工程的jar包导入到本地仓库中
#（2）【注意】compile 范围以外的依赖不具备传递性：Web -> Java1 -> Java2
如果为 Java2 增加一个 spring-core.jar 包后，会惊喜的发现依赖的两个项目（Web，Java1）都自动的增加了这个jar包，这就是依赖的传递性。
```

>依赖版本的原则

```shell
#（1）最短路径原则：Web -> Java1（log4j-1.2.9.jar） -> Java2（log4j-1.2.7.jar）
web 直接依赖 java1，java1依赖java2。所以，根据最短路径原则，以 java1 优先，即web中log4j版本为 1.2.9

#（2）声明优先原则：Web -> Java1（log4j-1.2.9.jar） + Java2（log4j-1.2.7.jar）
web 中同时依赖 java1 java2。但是，pom文件先写 java1 再写 java2，所以以 java1 优先，即web中log4j版本为 1.2.9
```

```xml
<!--统一管理依赖的版本-->
<properties>
    <demo_common.version>1.0-SNAPSHOT</demo_common.version> <!--声明版本-->
</properties>

<dependency>
    <groupId>com.example</groupId>
    <artifactId>demo_common</artifactId>
    <version>${demo_common.version}</version> <!--使用声明-->
</dependency>
```

> build配置

```xml
<build>
    <finalName>WebMavenDemo</finalName> <!-- 项目的名字 -->

    <resources> <!-- 资源打包 -->
        <resource>
            <directory>src/main/java</directory>
            <includes>
                <include>**/*.xml</include>
            </includes>
            <excludes>
                <exclude>**/*.txt</exclude>
                <exclude>**/*.doc</exclude>
            </excludes>
        </resource>
    </resources>

    <plugins> <!-- 打包插件 -->
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>

        <plugin> <!-- war插件（将项目打成war包）--> 
            <groupId>org.apache.maven.plugins</groupId> 
            <artifactId>maven-war-plugin</artifactId> 
            <version>2.1</version> 
            <configuration>                
                <warName>WebMavenDemo1</warName> <!-- war包名字 --> 
            </configuration> 
        </plugin> 
    </plugins>
</build>
```
## 安装配置

> mvn配置

```sh
maven-home-directory：mvn本地安装路径： D:/apache-maven-3.3.9
user-settings-file  ：mvn本地安装路径： D:/apache-maven-3.3.9/config/settings.xml
```

> 搭建私服

```shell
将'nexus-2.12.0-01-bundle.zip'解压到任意非中文目录中。
进入' 进入 nexus-2.12.0-01\bin\jsw\windows-x86-64'（对应自己系统）
    - install-nexus.bat   #安装服务
    - start-nexus.bat     #开启服务
    - stop-nexus.bat      #停止服务
    - uninstall-nexus.bat #卸载服务

修改nexus端口（默认8081）：'nexus-2.12.0-01\conf\nexus.properties'中的'application-prot=8081'
以用户名'admin'，密码'admin123'登陆网址： http://localhost:8081/nexus
```

>配置Mvn：`D:\apache-maven-3.3.9\conf\settings.xml`

```xml
<!--配置本地仓库-->
<localRepository>D:\apache-maven-3.3.9-repo</localRepository>
```

```xml
<!--配置阿里云镜像加速下载（可选）-->
<mirror> 
    <id>alimaven</id>
    <name>aliyun maven</name> 
    <url>http://maven.aliyun.com/nexus/content/groups/public/</url> 
    <mirrorOf>central</mirrorOf> 
</mirror>
```

```xml
<!--配置jdk-->
<profile>    
    <id>jdk-1.8</id>    
    <activation>    
        <activeByDefault>true</activeByDefault>    
        <jdk>1.8</jdk>    
    </activation>    
    <properties>    
        <maven.compiler.source>1.8</maven.compiler.source>    
        <maven.compiler.target>1.8</maven.compiler.target>    
        <maven.compiler.compilerVersion>1.8</maven.compiler.compilerVersion>    
    </properties>    
</profile>
```

```xml
<!--配置私服-->
<profile>  
    <id>nexus</id>  
    <repositories>
        <repository>  
            <id>central</id>
            <url>http://192.168.102.20:8081/nexus/content/groups/public</url>  
            <releases><enabled>true</enabled></releases>  
            <snapshots><enabled>true</enabled></snapshots>  
        </repository>  
    </repositories>  
</profile>
```

```xml
<!--配置下载jar包源码和文档-->
<!--相应jar包或整个项目，右键'Maven->Download_Sources/javaDoc'，即可下载-->
<profile>  
    <id>downloadSources</id>
    <properties>  
        <downloadSources>true</downloadSources>  
        <downloadJavadocs>true</downloadJavadocs>             
    </properties>  
</profile>
```

```xml
<!--激活私服和下载源码的profile-->
<activeProfiles>
    <activeProfile>nexus</activeProfile>
    <activeProfile>downloadSources</activeProfile>  
</activeProfiles>
```

```xml
<!--配置镜像，maven连接私服-->
<mirror>
    <id>nexus-releases</id>
    <mirrorOf>*</mirrorOf>
    <url>http://192.168.102.20:8081/nexus/content/groups/public</url>
</mirror>
<mirror>
    <id>nexus-snapshots</id>
    <mirrorOf>*</mirrorOf>
    <url>http://192.168.102.20:8081/nexus/content/repositories/apache-snapshots/</url>
</mirror>
```

> 将项目发布到私服，完成以下配置，然后项目右键：`run maven - deploy`

```xml
<!--pom文件配置，与<build>节点同级-->
<distributionManagement>
    <repository>
        <id>releases</id>
        <url>http://192.168.102.20:8081/nexus/content/repositories/releases</url>
    </repository>
    <snapshotRepository>
        <id>snapshots</id>
        <url>http://192.168.102.20:8081/nexus/content/repositories/snapshots</url>
    </snapshotRepository>
</distributionManagement>
```

```xml
<!--maven的settings配置-->
<!--其中，其中，<server>节点的<id>和 pom.xml 中<repository>节点的<id>相对应-->
<server>
    <id>releases</id>
    <username>admin</username>
    <password>admin123</password>
</server>
<server>
    <id>snapshots</id>
    <username>admin</username>
    <password>admin123</password>
</server>
```

>配置nexus的阿里云仓库

```sh
以用户名 "admin" 密码 "admin123"，登陆 http://localhost:8081/nexus

主界面 -> Add... -> Proxy Repository
Repository ID: aliyun
Repository Name: Aliyun Repository
Remote Storage Location: http://maven.aliyun.com/nexus/content/groups/public/

选中仓库组"Public Repositories" -> Configuration 
把"Aliyun Repository"从右侧移到左侧, 并拖到"Central"上边
这样,就可以优先访问阿里云仓库了.
```

>加入第三方jar

```xml
<!--配置settings-->
<server>
    <id>thirdparty</id>
    <username>admin</username>
    <password>admin123</password>
</server>
```

```shell
#使用以下cmd命令将第三方包加入私服
#其中，-DgroupId="随意指定"; -DartifactId="第三方jar包名"; -Dversion="版本号"; -Dfile="jar所在本地路径"; -Durl="私服路径"
mvn deploy:deploy-file -DgroupId=com.bluecard -DartifactId=wxpay-sdk-0.0.3 -Dversion=0.0.3 -Dpackaging=jar -Dfile=G:\wxpay-sdk-0.0.3.jar -Durl=http://192.168.102.20:8081/nexus/content/repositories/thirdparty/ -DrepositoryId=thirdparty
```

```xml
<!--项目pom.xml添加引用-->
<dependency>
    <groupId>com.bluecard</groupId> <!--同上文 -DgroupId，-DartifactId，-Dversion-->
    <artifactId>wxpay-sdk-0.0.3</artifactId>
    <version>0.0.3</version>
</dependency>
```

##三种关系

>依赖关系

```xml
使用标签<dependency>把另一个项目的 jar 引入到当前项目，自动下载另一个项目所依赖的其他项目
```
> 继承 & 聚合

```shell
#都是统一管理各个子项目的依赖版本（子项目GV默认继承自父项目）。
1、聚合项目：可在父项目的 pom.xml 中查看所有子项目。
2、继承项目：必须得先 install 父项目，再 install 子项目。'聚合'则可以直接 install 子项目
```

>继承关系

```xml
<!--pom类型表示逻辑父项目，只要一个项目有子项目，则它必须是 pom 类型-->

<!--(1)继承の父项目：pom.xml 中看不到有哪些子项目（只在逻辑上具有父子关系）-->
<!--(2)继承の子项目：出现<parent>标签，GV标签同父项目，即可省-->
<parent>
    <groupId>com.example</groupId>
    <artifactId>parent</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</parent>

<!-- <groupId>com.example</groupId> -->
<artifactId>child</artifactId>
<!-- <version>0.0.1-SNAPSHOT</version> -->
```
> 聚合关系（多模块模式，微服务项目推荐）

```xml
<!--前提是继承关系，父项目会把子项目包含到父项目中-->
<!--新建聚合项目的子项目时，点击父项目右键新建 "Maven Module"，而不是 "maven project"-->

<!--(1)聚合の父项目：可在 pom.xml 中查看所有子项目-->
<groupId>com.example</groupId>
<artifactId>demo_parent</artifactId>
<version>1.0-SNAPSHOT</version>
<packaging>pom</packaging> <!--父项目的打包类型必须设置为 pom-->

<modules>
    <module>demo_common</module>
    <module>demo_user</module>
    <module>demo_friend</module>
</modules>

<!--(2)聚合の子项目：可在 pom.xml 中查看父项目-->
<parent>
    <artifactId>demo_parent</artifactId>
    <groupId>com.example</groupId>
    <version>1.0-SNAPSHOT</version>
</parent>
<modelVersion>4.0.0</modelVersion>

<artifactId>demo_user</artifactId>
```
> 依赖管理：`dependencyManagement`和`pluginManagement`

```xml
<!--(1).父项目中，声明所有可能用到的jar； 再使用<properties>抽取版本,方便集中管理-->
<properties>
    <spring-version>4.1.6.RELEASE</spring-version> <!--自定义标签-->
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>${spring-version}</version> <!--引用自定义标签-->
        </dependency>
    </dependencies>
</dependencyManagement>

<!--(2)子项目中，也不是立即引用，也得写GA，<Version>继承自父项目，可省-->
<dependencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-webmvc</artifactId>
    </dependency>
</dependencies>
```
## 使用相关

> 远程部署

```xml
(1).修改 tomcat/conf/tomcat-users.xml 添加角色，然后重启tomcat
    <role rolename="manager-gui"/> //图形界面角色
    <role rolename="manager-script"/> //脚本角色
    <user username="tomcat" password="tomcat" roles="manager-gui,manager-script"/>

(2).在 pom.xml 中 tomcat 插件的<configuration>里配置
    </configuration>
        <username>tomcat</username>
        <password>tomcat</password>
        <url>http://192.168.8.8:8080/manager/text</url>
    </configuration>

(3).右键项目--> run as --> maven build(以前写过,选择第二个) 
    -->输入 tomcat7:deploy(第一次发布); tomcat7:redeploy(非第一次发布).
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
Spring Security提供了 BCryptPasswordEncoder 类，实现Spring的 PasswordEncoder 接口使用'BCrypt强哈希方法'来加密密码。
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













































































































