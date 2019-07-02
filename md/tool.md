[TOC]



# Idea

## 相关插件

>`MybatisPlugin`

```
安装方式：Settings -> Plugins -> Intall Plugin from disk -> 选择压缩包即可
```

> `MavenRunHelper`

```
maven插件，可右键启动、打包、测试mvn项目
```

> `Alibaba Java Coding Guidelines-1.0.6`

```
ali开发手册的插件版，约束开发习惯
```

> `lombok`：简化POJO的getter/setter/toString；异常处理；I/O流的关闭操作等等

```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope> <!-- 只在编译阶段生效，不需要打入包中 -->
    <optional>true</optional> <!--默认false。当A依赖B，B依赖 lombok 并设为true，若A没有显式的引入lombok，则A不依赖lombok-->
</dependency>
```

```java
//eclipse-安装
下载: https://projectlombok.org/download
将 lombok.jar 放在eclipse安装目录下，与 eclipse/sts.ini 同级
当前目录打开cmder，使用命令: "java -jar ./lombok.jar"，弹框选择 Install/Update
成功标识: sts.ini最后一行：-javaagent:F:\sts-bundle\sts-3.9.3.RELEASE\lombok.jar
```

```java
//idea-安装
下载: https://github.com/mplushnikov/lombok-intellij-plugin/releases
Settings-Plugins-'install from disk'，选择下载的*.zip，即可安装成功 
```

```java
//常用注解
@Slf4j: 生成slf4j注解式logger
@NonNull: 调用字段的setter方法时,传参为null，则报空指针异常。
@Data: 组合注解，包含 @Getter; @Setter; @ToString, @EqualsAndHashCode; 无参构造函数.

@Accessors: 定制化@Getter与@Setter
//(chain = true): 链式编写setter方法,如 Person hua = new Person().setName("HUA").setAge(18);
//(fluent  = true): 流式编写setter方法,如 Person wang = new Person().name("WANG").age(18);

@SneakyThrows(*.class): 
//用在'方法'上，可将方法中的代码用 try-catch 语句包裹起来
//捕获异常并在 catch 中用 Lombok.sneakyThrow(e) 把异常抛出
```








> `cmder`（cmd升级版）http://cmder.net/

```java
mini与full版：差别在于有没有内建 msysgit 工具

右键菜单：'配置系统环境变量,然后使用系统cmd执行命令: Cmder.exe /register ALL'
中文乱码：'settings -> Environment -> 添加: set LANG=zh_CN.UTF-8'
```

> `JSON-Handle`（Chrome插件）

```java
浏览器输入：'chrome://extensions/' 将下载后的文件拖入浏览器即可
```
> `Advanced_Rest_Client_Chrome`（Chrome插件）

```
用于调试 http请求，类似 postman
```

>`OneTab_v1.18`（Chrome插件）

```
将已打开的 chrome 网页，保存成类书签形式，以便后续阅读，减少内存
```





## 常见问题

>如何在IDEA中多次启动同一个程序呢

```java
点击工具栏启动标识 小三角 左边的 'Edit Configurations'，在弹出窗口中取消勾选 Single instance only（单例模式） ，点击OK
每次运行 SpringBoot 项目前，修改配置文件中的端口号即可。
```

>`如何在单个窗口打开多个Maven工程啊？`

```
随便新建一个文件夹，然后将工程都扔进去，使用IDEA打开这个文件夹。
```
>复制警告或错误信息

```
方式一：鼠标光标悬浮在报错的地方，待错误提示出现后，键盘按住 Alt，同时点击鼠标左键，Ctrl+V 到度娘即可。

方式二：直接在界面的底部右键copy，错误信息显示在底部。
```

> mvn打包时，跳过Test

```java
打开右侧"Maven-Projects"，当前项目'Lifecycle'，选中'Test'，点击菜单栏的"小闪电"，此时Test上多了一条横。
```



## 默认配置

>当前项目配置 VS 默认配置

```
当前配置：顶部导航栏 -> File -> Settings / ProjectStructs

默认配置：顶部导航栏 -> File -> Other Settings -> Default Settings /Project Structs
```

> （默认配置）JDK

```
顶部工具栏 File -> Other Settins -> Default Project Structure -> SDKs -> JDK
```

> （默认配置）Maven

```java
顶部工具栏 File -> Other Settings -> Default Settings -> Build & Tools -> Maven

mvn命令：右侧工具栏 Maven -> 点击展开某工程或模块 ->快速执行Maven命令。
        clean：清空； compile：编译； package：打包； install：发布到仓库
```

>（默认配置）Maven DarchetypeCatalog。IDEA 创建Maven项目速度慢问题

```java
解决 ：IDEA根据 maven archetype 的本质，其实是执行'mvn archetype:generate'命令，
该命令执行时，需要指定一个'archetype-catalog.xml'文件。
该命令的参数-DarchetypeCatalog，可选值为：remote，internal，local等，用来指定 archetype-catalog.xml 文件从哪里获取。
默认为remote，即从'http://repo1.maven.org/maven2/archetype-catalog.xml'路径下载archetype-catalog.xml文件。

顶部工具栏 File -> Other Settings -> Default Settings -> Build，... -> maven -> Runner -> VM Options 填写：
-DarchetypeCatalog=internal
```

> （默认配置）版本控制Git/Svn

```
顶部工具栏 File -> Other Settings -> Default Settings -> Version Control -> Git
```

>（默认配置）自动导包和智能移除 

```
顶部工具栏 File -> Other Settings -> Default Settings -> Auto Import
    （√）add unambiguous...自动导入依赖
    （√）Optimize from... 优化导入和智能删除无关依赖
```

> （当前项目配置）Tomcat Server

```
顶部工具栏 File -> Settings -> Deployment -> Application Servers -> Tomcat Server，选择 Tomcat 的根目录
```

> 其他配置

```
自动编译：File -> Other Settings -> Default Settings -> Build... -> Compiler -> （√）Build project auto...

取消大小写敏感：File | Settings | Editor | General | Code Completion | Case Sensitive Completion = None

调整字体大小：打开配置，搜索Font，然后再Font可以调整字体类型，Size可以调整字体大小

F2修改文件名：File -> Settings -> Keymap -> 搜索 Rename -> 将快捷键设置为F2

F3浏览目录结构：File -> Settings -> Keymap -> 搜索 Show In Explorer -> 将快捷键设置为F3
```





## 配置tomcat

导入非 maven 项目的流程：

https://www.cnblogs.com/Miracle-Maker/articles/6476687.html

https://blog.csdn.net/small_mouse0/article/details/77506060

## 基础配置

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





> 快捷键

|                    描述                    |            idea             |       eclipse        |
| :----------------------------------------: | :-------------------------: | :------------------: |
|                  main方法                  |            psvm             |  main（Alt+/补全）   |
|                 输出控制台                 |            sout             |         syso         |
|               for普通 / 增强               |         fori / iter         |   for（Alt+/选择）   |
|                 try/catch                  |         Ctrl+Alt+T          |          *           |
|            自动生成Get/实现方法            |         Alt+Insert          |          *           |
|                                            |                             |                      |
|              抽取变量（方法）              |      Ctrl+Alt+V （M）       |   Shift+Alt+L（M）   |
|                   格式化                   |         Ctrl+Alt+L          |     Ctrl+Shift+F     |
|               代码提示/补全                |       Ctrl+Alt+Space        |        Alt+/         |
|                  导包优化                  |         Ctrl+Alt+O          |     Ctrl+Shift+O     |
|                                            |                             |                      |
|        上下移动单行语句（整个方法）        | Shift+Alt+↑（Shift+Ctrl+↑） |      Alt+↑（）       |
|                 批量重命名                 |          Shift+F6           |     Shift+Alt+R      |
|            注释单行（选中部分）            |   Ctrl+/（Ctrl+Shift+/）    |        Ctrl+/        |
|                  参数提示                  |           Ctrl+P            |                      |
|              查看类的继承关系              |           Ctrl+H            |        Ctrl+T        |
|                   定位行                   |           Ctrl+G            |        Ctrl+L        |
|            整行复制（整行删除）            |      Ctrl+D（Ctrl+X）       | Ctrl+Alt+↓（Ctrl+D） |
|                                            |                             |                      |
|                 大小写转化                 |        Ctrl+Shift+U         |                      |
|                返回上次修改                |    Ctrl+Shift+BackSpace     |                      |
|           当前工作空间查找java类           |      Ctrl+Shift+Alt+N       |                      |
|                 查看类结构                 |            Alt+7            |                      |
|   跳到下(上)一个选择项（配合Ctrl+F使用）   |       F3（Shift+F3）        |                      |
| 在全局（当前类）查找方法调用，可配合F3使用 |      Alt+F7（Ctrl+F7）      |      Ctrl+Alt+H      |
|             折叠（展开）代码块             |   Shift+Ctrl+减号（加号）   | Shift+Ctrl+/（*号）  |
|                                            |                             |                      |
|        调试：跳到下一步（进入代码）        |          F8（F7）           |       F6（*）        |

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

# Utils

包名统一使用小写，点分隔符之间有且仅有一个自然语义的英语单词。**包名统一使用单数形式**，但是类名如果有复数含义，**类名可以使用复数形式**。`com.example.spring.util.CommonUtils`

## 常用包

> apache

```xml
<!-- 该版本完全支持 Java5 的特性，如泛型和可变参数。该版本无法兼容以前的版本，简化很多平时经常要用到的写法，如判断字符串是否为空等等 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.8.1</version>
</dependency>

<!-- IO工具类，文件操作及字符串比较功能 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-io</artifactId>
    <version>1.3.2</version>
</dependency>

<!-- 对象池的实现，显著的提升了性能和可伸缩性，特别是在高并发加载的情况下 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
    <version>2.4.2</version>
</dependency>

<!-- email -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-email</artifactId>
    <version>1.4</version>
</dependency>
<!-- spring-boot email -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

> Spring

```xml

```
## 工具类

> 命名规则

```java
//【强制】包名统一使用小写，点分隔符之间有且仅有一个自然语义的英语单词。包名统一使用单数形式，但是类名如果有复数含义，类名可以使用复数形式。

正例：应用工具类包名为 com.alibaba.ai.util、类名为 MessageUtils（此规则参考 spring 的框架结构）
```



> 常用方法

```java
//获取项目class路径
public static String getClassPath() throws FileNotFoundException {
    // ClassUtils.getDefaultClassLoader().getResource("").getPath();//等同
    return ResourceUtils.getURL("classpath:").getPath();
}

//获取项目根路径
public static String getFilePath() throws FileNotFoundException {
    return ResourceUtils.getURL("").getPath();
}

//系统换行
public static String newline() {
    return System.getProperty("line.separator");
}
```

> 使用占位符拼接字符串

```java
//域名"www.qq.com"被访问了123.456次
MessageFormat.format("域名{0}被访问了{1}次", "\"www.qq.com\"", 123.456);

//创建格式化的字符串，及连接多个字符串对象：域名"www.qq.com"被访问了123.46次
String.format("域名%s被访问了%3.2f次", "\"www.qq.com\"", 123.456); 

//先转化十六进制,再高位补0
String.format("%04d",Integer.parseInt(String.format("%x", 16))); //0010
```

> 定时任务：不建议使用Timer

```java
//【强制】线程池不允许使用 Executors 去创建，而是通过 ThreadPoolExecutor 的方式。
//       这样的处理方式让写的同学更加明确线程池的运行规则，规避资源耗尽的风险。
private static ScheduledExecutorService scheduledExecutor = null;

public static ScheduledExecutorService getScheduleExecutor() {
    if (scheduledExecutor == null) {
        synchronized (Test02.class) {
            if (scheduledExecutor == null) {                
                scheduledExecutor = new ScheduledThreadPoolExecutor(10,
                        //源自：org.apache.commons.lang3.concurrent.BasicThreadFactory
                        new BasicThreadFactory.Builder().namingPattern("schedule-pool-%d").daemon(true).build());
            }
        }
    }
    return scheduledExecutor;
}
```

```java
public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command,
                                                 long initialDelay,
                                                 long delay,
                                                 TimeUnit unit);
```

>通过类名获取类的对象

```java
@Component // 获取bean的工具类
public class MyApplicationContextAware implements ApplicationContextAware {

    private static ApplicationContext context;

    // 实现接口的回调方法,设置上下文环境
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        MyApplicationContextAware.context = context;
    }

    // 获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return context;
    }

    // 通过name获取Bean.
    public static Object getBean(String name) {
        return context.getBean(name);
    }

    // 通过clazz获取Bean.
    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    // 通过name及clazz返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return context.getBean(name, clazz);
    }
}
```

## 其他概念

> 淘汰算法

```java
//LRU（least_recently_used）：最近最少使用
将最近使用的条目存放到缓存的顶部位置。达到缓存极限时，从底部开始移除

这里会使用到昂贵的算法，而且它需要记录"年龄位"来精确显示条目是何时被访问的。
此外，当一个LRU缓存算法删除某个条目后，"年龄位"将随其他条目发生改变。

[]; 'A'->[A]; 'B'->[B,A]; 'C'->[C,B,A]; 'D'->[D,C,B,A]; 'C'->[C,D,B,A]; 'E'->[E,C,D,B]
缓存容量4，初始为空。访问A则缓存为[A]，...当再次访问C时，将C提到首位；最后访问E，由于缓存已达上限，则将最后的A移除
```
```java
//LFU（least_frequently_used）：最不经常使用
使用一个计数器来记录条目被访问的频率，最低访问频率的条目首先被移除

此算法并不经常使用，因为它无法对一个拥有最初高访问率，但之后长时间没有被访问的条目缓存负责

[A-32,B-30,C-26,D-26]; 'D'->[A-32,B-30,D-27,C-26]; 'B'->[A-32,B-31,D-27,C-26]; 'F'->[A-32,B-31,D-27,F-1]
首先访问D，则D的频率+1，并和C调换位置；再访问B，将B频率+1；最后访问F，由于容量为4，则必须将末位C移除，并将F加入，评率设为1
```
```java
//FIFO（first_in_first_out）：先进先出
与普通存储器的区别是没有外部读写地址线，这样使用起来非常简单

但缺点就是只能顺序写入数据，顺序的读出数据，其数据地址由内部读写指针自动加1完成。不能像普通存储器那样可以由地址线决定读取或写入某个指定的地址
```

```java
//MRU（most_recently_used）：最近最常使用
最先移除最近最常使用的条目。一个MRU算法擅长处理一个条目越久，越容易被访问的情况
```






#log

## 基础概念

> `TRACE < DEBUG < INFO < WARN < ERROR`

```
日志门面：日志的接口，如slf4j，jcl，jboss-logging

日志实现：具体实现类，如logback，log4j，log4j2，jul
```

> 配置文件

```java
logback.xml //直接被日志框架加载
logback-spring.xml //跳过日志框架，直接被 SpringBoot 加载，可以使用高级特性 Profile
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

```properties
%t    -> 线程名   
%m    -> 日志主体
%n    -> 平台换行符
%r    -> 自应用启动到输出该log信息耗费的毫秒数
%p    -> 日志级别 {%-5p} --> 5字符长度,左边补空格
%d    -> 时间及格式 %d{yyyy-MMM-dd HH:mm:ss,SSS} --> 2002-10-18 22:10:28,921
```
```properties
#不建议使用,影响效率
#a.不输入： 表示输出完整的<包名>+<类名>
#b.输入0：  表示只输出<类名>
#c.任意数字：表示输出小数点最后边点号之前的字符数量
%c    -> %clength} -> length有三种情况(↑) -> 类全名

%l -> 日志发生位置: 包括类目名,发生的线程,以及在代码中的行数
```

> 输出线程id

slf4j默认不提供线程id输出，不过可利用 'MDC' 特性实现。

```java
//1.配置拦截器：在线程开始时加入 ThreadId; 在线程结束时删除 ThreadId
public class ThreadIdInterceptor implements HandlerInterceptor {
    private final static String THREAD_ID = "ThreadId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        String ThreadId = java.util.UUID.randomUUID().toString()
            .replaceAll("-", "").toUpperCase();
        MDC.put(THREAD_ID, ThreadId); //加入 ThreadId
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // .... 其他逻辑代码

        MDC.remove(THREAD_ID); //删除 ThreadId
    }
}
```
```java
//2.注册拦截器
@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ThreadIdInterceptor()).addPathPatterns("/**");
    }
}
```
```xml
<!-- 3.logback.xml -->
<property name="CONSOLE_PATTERN" <!-- %X{ThreadId}，输出MDC中key的值 -->
        value="%d{yyyy-MM-dd HH:mm:ss.SSS} %5p [%t] %X{ThreadId} %c{0} - %m%n" />
```
```java
//4.输出结果
2018-12-06 20:59:04.436  INFO [http-nio-8090-exec-1] 41C85FC1684A4F37B64BEFC22D288C0C
    HelloController - 2018-12-06 20:59:04.436 -> 50 - http-nio-8090-exec-1 ==> 
    java.lang.RuntimeException: asyncFuture - / by zero
```
>动态修改日志级别：利用 SpringBoot 的 Actuator 监控

```properties
#所有模块的日志级别
http://127.0.0.1:8090/demo/actuator/loggers
#具体模块的日志级别
http://127.0.0.1:8090/demo/actuator/loggers/com.example.controller

#发送 POST 请求到以上路径，动态修改以上模块的日志级别为 DEBUG，成功状态码为 '204'
POST - 请求体: {"configuredLevel": "DEBUG"} - Content-Type: application/json
```
##日志过滤

> LevelFilter：级别过滤器。根据配置的过滤级别，选择性的接收或拒绝日志

```java
//DENY      -> 日志将立即被抛弃,不再经过其他过滤器
//ACCEPT    -> 日志会被立即处理,..................
//NEUTRAL   -> 有序列表里的下一个过滤器会接着处理日志
```
```xml
<!--A.logback.xml-->
<filter class="ch.qos.logback.classic.filter.LevelFilter"> <!--过滤ERROR-->
    <level>ERROR</level>
    <onMatch>ACCEPT</onMatch>
    <onMismatch>DENY</onMismatch>
</filter>
```
```properties
#B.log4j.properties
log4j.logger.com.x.sm = DEBUG, sm, err ---> sm包日志级别, 输出路径1, 输出路径2

log4j.appender.err.filter.a=org.apache.log4j.varia.LevelRangeFilter ---> 过滤ERROR
log4j.appender.err.filter.a.LevelMin=ERROR
log4j.appender.err.filter.a.LevelMax=ERROR
log4j.appender.err.filter.a.acceptOnMatch=true
log4j.appender.err=org.apache.log4j.DailyRollingFileAppender
log4j.appender.err.File=/var/lib/X/logs/sm/error
log4j.appender.err.DatePattern='.'yyyyMMdd'.log'
log4j.appender.err.layout=org.apache.log4j.PatternLayout
log4j.appender.err.layout.ConversionPattern=%d{HH:mm:ss.SSS} - %m%n
```

> ThresholdFilter：临界值过滤器，`过滤 <配置级别，只输出 >=`

```xml
<!--A.logback.xml-->
<filter class="ch.qos.logback.classic.filter.ThresholdFilter">
    <level>INFO</level>
</filter>
```

```properties
#B.log4j.properties
log4j.logger.com.x.sm = DEBUG, sm, err

log4j.appender.err.Threshold=ERROR
log4j.appender.err=org.apache.log4j.DailyRollingFileAppender
log4j.appender.err.File=/var/lib/webpark/logs/sm/error
log4j.appender.err.DatePattern='.'yyyyMMdd'.log'
log4j.appender.err.layout=org.apache.log4j.PatternLayout
log4j.appender.err.layout.ConversionPattern=%d{HH:mm:ss.SSS} - %m%n
```

## 异步输出

每次输出日志就会发生一次磁盘IO，损耗性能。

异步输出，不让此次写日志发生磁盘IO，阻塞日志线程，从而减少不必要的性能损耗。

```xml
<!--同步appender-->
<appender name="info" class="ch.qos.logback.core.rolling.RollingFileAppender">
    //... ...
</appender>
```

```xml
<!--异步appender 必须跟在同步后面,否则不起作用-->
<appender name="async4info" class="ch.qos.logback.classic.AsyncAppender">

    <!--当 BlockingQueue 还有20%容量，将丢弃 TRACE、DEBUG 和 INFO 级别的日志-->
    <!--只保留 WARN 和 ERROR 级别的日志。为保持所有的日志，将该值设置为0。默认值20-->
    <discardingThreshold>0</discardingThreshold>
    <queueSize>256</queueSize> <!--BlockingQueue 的最大容量,该值影响性能. 默认值256-->

    <!--异步appender并不自己写日志，只是将日志输出到 BlockingQueue-->
    <!--最终还是具体的appender将日志输出到文件-->
    <!--图示详见: http://www.importnew.com/27247.html-->
    <appender-ref ref="info"/>
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

## 基本概念

>什么是构建???

```java
构建并不是创建，创建一个工程并不等于构建一个项目。

构建是以 Java源码，框架配置文件，JSP页面，html，图片...等静态资源作为'原材料'去'生产'出一个可以运行的项目的'过程'。
```

>pom文件（Project Object Model，项目对象模型）

```java
将Java工程'project'的相关信息封装为对象'object'，作为便于操作和管理的模型'model'，Maven工程的核心配置。
```

>仓库种类

```java
本地仓库：本机电脑上的 Maven 仓库
私服仓库：架设在本地局域网内的 Maven 仓库，直连中央仓库
中央仓库：架设在 Internet 上，为全世界所有 Maven 工程服务
```

>mvn命令：与项目构建相关的命令，必须切换到 pom.xml 同级目录

```shell
mvn clean         #删除以前的编译结果,为重新编译做准备
mvn compile       #编译主程序
mvn test-compile  #编译测试程序
mvn test          #执行测试
mvn package       #打包
mvn install       #将打包的结果(jar/war)安装到本地仓库中
mvn site          #生成站点
```

>依赖的范围：Scope 

```
compile  缺省值，适用于所有阶段，会随着项目一起发布。
provided 类似compile，该依赖参与编译，测试，运行等阶段，但打包时不会打包进去，期望JDK、容器或使用者会提供这个依赖。如：servlet.jar，lombok。
test     只在测试时使用，用于编译和运行测试代码。不会随项目发布。 
runtime  无需参与项目的编译，只在后期的测试和运行时使用，如JDBC驱动，适用运行和测试阶段。
```

```properties
compile  #编译阶段(√); 测试阶段(√); 打包(√); 部署(√); 如: spring-core
provied  #编译阶段(√); 测试阶段(√); 打包(x); 部署(x); 如: servlet-api（tomcat提供），lomboklombok
test     #编译阶段(x); 测试阶段(√); 打包(x); 部署(X); 如: junit
runtime  #编译阶段(x); 测试阶段(√); 打包(√); 部署(√); 如: mysql-connector-java
```

>依赖的传递：Optional

```java
true 该依赖只能在本项目中传递，不会传递到引用该项目的父项目中，父项目需要主动引用该依赖才行。

//A依赖B，B依赖C，A能否使用C呢？
'非compile'范围的依赖不能传递，必须在有需要的工程 A 中单独加入 C.
```

>依赖的排除

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <artifactId>spring-boot-starter-logging</artifactId> <!--排除'sp默认logback包'-->
            <groupId>org.springframework.boot</groupId>
        </exclusion>
    </exclusions>
</dependency>
```

>版本号统一声明

```xml
<properties>
    <activemq.version>5.15.4</activemq.version> <!--声明-->
</properties>

<dependency>
    <groupId>org.apache.activemq</groupId>
    <artifactId>activemq-amqp</artifactId>
    <version>${activemq.version}</version> <!--使用-->
</dependency>
```

## 安装配置

> 安装测试：https://maven.apache.org/download.cgi

 ```java
配置系统环境变量，cmd命令验证'mvn -version'
 ```

>eclipse使用mvn

```java
本地mvn：window - preferences - maven - installations - add - External - 本地maven路径，如：'D:/apache-maven-3.3.9'

mvn仓库: window - preferences - maven - user_settings - add Global_Settings 和 User_Settings 都选择本地mvn的"settings.xml"文件
如：'D:\apache-maven-3.3.9\conf\settings.xml'
```

> 配置mvn：配置文件 settings.xml

```xml
<!--配置本地仓库-->
<localRepository>D:\apache-maven-3.3.9-repo</localRepository>

<!--配置阿里云镜像加速下载-->
<mirror> 
    <id>alimaven</id>
    <name>aliyun maven</name> 
    <url>http://maven.aliyun.com/nexus/content/groups/public/</url> 
    <mirrorOf>central</mirrorOf> 
</mirror>

<!--配置下载jar包源码和文档-->
<!--相应jar包或整个项目，右键'Maven->Download_Sources/javaDoc'，即可下载-->
<profile>  
    <id>downloadSources</id>
    <properties>  
        <downloadSources>true</downloadSources>  
        <downloadJavadocs>true</downloadJavadocs>             
    </properties>  
</profile>

<!--配置mvn私服-->
<profile>  
    <id>nexus</id>  
    <repositories> //<!--私服库地址-->
        <repository>  
            <id>central</id>
            <url>http://192.168.102.20:8081/nexus/content/groups/public</url>  
            <releases><enabled>true</enabled></releases>  
            <snapshots><enabled>true</enabled></snapshots>  
        </repository>  
    </repositories>  
    <pluginRepositories> //<!--插件库地址-->
        <pluginRepository>  
            <id>central</id>  
            <url>http://maven.com:8081/nexus/content/groups/public</url>  
            <releases><enabled>true</enabled></releases>  
            <snapshots><enabled>true</enabled></snapshots>  
        </pluginRepository>  
    </pluginRepositories>  
</profile>
```

>配置nexus的阿里云仓库

```java
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
<!--（1）配置mvn-->
<server>
    <id>thirdparty</id>
    <username>admin</username>
    <password>admin123</password>
</server>

<!--（2）使用cmd命令将第三方包加入私服-->
<!--其中，-DgroupId="随意指定"; -DartifactId="第三方jar包名"; -Dversion="版本号"; -Dfile="jar所在本地路径"; -Durl="私服路径"-->
mvn deploy:deploy-file -DgroupId=com.bluecard -DartifactId=wxpay-sdk-0.0.3 -Dversion=0.0.3 -Dpackaging=jar -Dfile=G:\wxpay-sdk-0.0.3.jar -Durl=http://192.168.102.20:8081/nexus/content/repositories/thirdparty/ -DrepositoryId=thirdparty

<!--项目pom.xml添加引用-->
<dependency>
    <groupId>com.bluecard</groupId> <!--同上文 -DgroupId-->
    <artifactId>wxpay-sdk-0.0.3</artifactId> <!--同上文 -DartifactId-->
    <version>0.0.3</version> <!--同上文 -Dversion-->
</dependency>
```

##三种关系

>依赖关系

```xml
使用标签<dependency>把另一个项目的 jar 引入到当前项目
自动下载另一个项目所依赖的其他项目
```
>继承关系

```xml
<!--pom类型表示逻辑父项目，只要一个项目有子项目，则它必须是 pom 类型-->
<!--父项目必须是 pom 类型，如果子项目(jar/war)还是其他项目的父项目，子项目也必须是 pom 类型-->

<!--(1)继承-父项目：pom.xml 中看不到有哪些子项目（只在逻辑上具有父子关系）-->

<!--(2)继承-子项目：出现<parent>标签，GV标签同父项目，即可省-->
<parent>
    <groupId>com.example</groupId>
    <artifactId>parent</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</parent>

<!-- <groupId>com.example</groupId> --> 可省
<artifactId>child</artifactId>
<!-- <version>0.0.1-SNAPSHOT</version> --> 可省
```
> 聚合关系（多模块模式，分布式项目推荐）

```xml
<!--前提是继承关系，父项目会把子项目包含到父项目中-->
<!--新建聚合项目的子项目时，点击父项目右键新建 "Maven Module"，而不是 "maven project"-->

<!--(1)聚合-父项目：可在 pom.xml 中查看所有子项目-->
<groupId>com.example</groupId>
<artifact>parent-module</artifact>
<version>1.0.0-SNAPSHOT</version>
<packaging>pom</packaging> <!--打包方式必须是 pom 类型-->
<name>My Parent Module</name>
<modules>
    <module>child-module-1</module>
    <module>child-module-2</module>
</modules>

<!--(2)聚合-子项目：可在 pom.xml 中查看父项目-->
<parent>
    <groupId>com.example</groupId>
    <artifactId>parent-module</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</parent>
<artifactId>child-module-1</artifactId>
```
> 继承和聚合的意义和区别

    意义: 统一管理各个子项目的依赖版本.（子项目GV默认继承自父项目）
    区别: (1).聚合项目 可在父项目的 pom.xml 中查看所有子项目.
         (2).'继承'必须得先install父项目，再install子项目； '聚合'则可以直接install子项目

> 依赖管理：将父项目中的<dependencies>和<plugin>，用<dependencyManagement>和<pluginManagement>管理起来。

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

<!--(2)子项目中，也不是立即引用，也得写GAV，不过<Version>继承自父项目，即可省-->
<dependencies>
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-webmvc</artifactId>
    </dependency>
</dependencies>
```
## 使用相关

> 资源拷贝插件

```xml
<!--mvn默认只把 src/main/resources 里的非java文件编译到classes中-->
<!--如果希望 src/main/java 下的文件（如mapper.xml）也被编辑到 classes 中，在 pom.xml 中配置-->
<resources>
    <resource>
        <directory>src/main/java</directory>
    </resource>
    <resource>
        <directory>src/main/resources</directory>
    </resource>
</resources>
```

>新建war项目

```xml
<!--(1)创建 maven project 时，packaging 选择 war-->
<!--(2)在 webapp 文件夹下新建"META-INF"和"WEB-INF/web.xml"-->
<!--(3)在 pom.xml 中添加 javaEE 相关的三个 jar-->
<dependency>
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>3.0.1</version>
    <scope>provided</scope> <!--依赖的范围，这个必须是provided，因为 tomcat 中自带此jar包-->
</dependency>
<dependency>
    <groupId>javax.servlet.jsp</groupId>
    <artifactId>jsp-api</artifactId>
    <version>2.2</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>jstl</groupId>
    <artifactId>jstl</artifactId>
    <version>1.2</version>
</dependency>
    
<!--(4)使用 tomcat 插件，而非本地tomcat。可实现不同项目发布到不同的tomcat，端口号不能相同.-->
<plugin>
    <groupId>org.apache.tomcat.maven</groupId>
    <artifactId>tomcat7-maven-plugin</artifactId>
    <version>2.2</version>
    <configuration>
        <!--本地启动时项目的端口号; 热部署到远程服务器则不起作用，以远程tomcat端口号为准-->
        <port>8099</port> 

        <!--项目发布到 tomcat 后的名称，只写'/'则名称为 ROOT-->
        <!--测试tomcat http://localhost:8080/ 其访问的是tomcat的 ROOT 项目-->
        <path>/hello</path>
    </configuration>
</plugin>
    
<!--(5)项目启动: 右键项目 -> run as -> maven build -> Goals中输入 "clean tomcat7:run"-->
```

> 远程部署

```xml
(1).修改 tomcat/conf/tomcat-users.xml 添加角色，然后重启tomcat
    <role rolename="manager-gui"/> //图形界面角色
    <role rolename="manager-script"/> //脚本角色
    <user username="tomcat" password="tomcat" roles="manager-gui,manager-script"/>

(2).在 pom.xml 中 tomcat 插件的<configuration>里配置
    </configuration>
        //...
        <username>tomcat</username>
        <password>tomcat</password>
        <url>http://192.168.8.8:8080/manager/text</url>
    </configuration>

(3).右键项目--> run as --> maven build(以前写过,选择第二个) 
    -->输入 tomcat7:deploy(第一次发布); tomcat7:redeploy(非第一次发布).
```

# fastjson

##基础概念

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.47</version>
</dependency>
```

>`getIntValue()和getInteger()`的区别

```java
json.getInteger("a"); //null --->对于空的key
json.getIntValue("a"); //0
```

##相互转化

> X ---> JSONString 

```java
String json = JSON.toJSONString(list / map / javabean);
String json = JSON.toJSONString(list, true);//args1: json是否格式化(有空格和换行).
```

> JSONString --->X `必须有空构造方法`

```java
Dog dog = JSON.parseObject(json, Dog.class);
Map map = JSON.parseObject(json, Map.class);
List<Dog> list = JSON.parseArray(json, Dog.class);
```

> X ---> JSONObject，先转换为JSONString。其中，`javabean必须有get/set`

```java
JSONObject obj = JSON.parseObject(JSON.toJSONString(dog));//javabean
JSONObject obj = JSON.parseObject(JSON.toJSONString(map));//map
JSONArray array = JSON.parseArray(JSON.toJSONString(list));//list
```
##Null值处理

> null值处理：list ---> JSONString

```java
QuoteFieldNames         //输出key时是否使用双引号，默认为true
WriteMapNullValue       //是否输出值为null的字段，默认为false
WriteNullListAsEmpty    //List字段如果为null，输出为[]，而非null
WriteNullNumberAsZero   //数值字段如果为null，输出为0，而非null
WriteNullBooleanAsFalse //Boolean字段如果为null，输出为false，而非null

WriteNullStringAsEmpty  //字符类型字段如果为null，输出为""，而非null (√，默认不输出null字段)
```

```java
List<Dog> list = Arrays.asList(new Dog("11", 11), new Dog(null, 22));

// [{"age":11,"name":"11"},{"age":22}] ---> 默认不输出null字段
// String json = JSON.toJSONString(list);

// [{"age":11,"name":"11"},{"age":22,"name":""}]
String json = JSON.toJSONString(list, SerializerFeature.WriteNullStringAsEmpty);
```

##Boot配置

> SpringBoot2.x默认使用 jacksonJson 解析，现转换为 fastjson，并且解决中文乱码问题。

```java
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //1.构建了一个消息转换器 converter
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();

        //2.添加fastjson配置,如: 是否格式化返回的json数据;设置编码方式
        FastJsonConfig config = new FastJsonConfig();

        config.setSerializerFeatures(SerializerFeature.PrettyFormat);//格式化

        List<MediaType> list = new ArrayList<>();//中文乱码
        list.add(MediaType.APPLICATION_JSON_UTF8);
        converter.setSupportedMediaTypes(list);

        //3.在消息转换器中添加fastjson配置
        converter.setFastJsonConfig(config);
        converters.add(converter);
    }
}
```





# HttpClient

##基础概念

```xml
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
</dependency>
```
> 区别 GET & POST

```java
超链接<a/>    ---> //只能用 GET 提交HTTP请求
表单<form/>   ---> //可以用 GET，POST .......

GET          ---> //参数只能在请求行（request-line）
POST         ---> //参数可在请求行，亦可在请求体（request-body）
```
> 区别 URL & URI：http://ip:port/demo/hello/hello & /demo/hello/hello

<https://www.cnblogs.com/wuyun-blog/p/5706703.html>

<https://blog.csdn.net/koflance/article/details/79635240>

## GET-请求行

> 两种方式获取HttpGet

```java
//(1).直接将参数拼接在 URI 之后
String uri = "http://127.0.0.1:8090/demo/http/get?name=中国&age=70";
HttpGet httpGet = new HttpGet(uri);
```
```java
//(2).通过 URIUtils 工具类生成带参数的 URI
String param = "name=中国&age=70";
// String param = "name=" + URLEncoder.encode("中国", "UTF-8") + "&age=70"; //中文参数,encode
URI uri = URIUtils.createURI("http", "127.0.0.1", 8090, "/demo/http/get", param, null);
HttpGet httpGet = new HttpGet(uri);
```

## POST-请求行

> 两种方式获取httpPost （同GET）

```java
//(1).拼接字符串
String uri = "http://127.0.0.1:8090/demo/http/post?name=中国&age=70";
HttpPost httpPost = new HttpPost(uri);
```

```java
//(2).工具类 URIUtils
String param = "name=中国&age=70";
// String param = "name=" + URLEncoder.encode("中国", "UTF-8") + "&age=70"; //中文参数,encode
URI uri = URIUtils.createURI("http", "127.0.0.1", 8090, "/demo/http/post", param, null);
HttpPost httpPost = new HttpPost(uri);
```

## POST-请求体

> 传输 表单键值对 keyValue

```java
//1.POST表单
List<NameValuePair> nvps = new ArrayList<>(2);
nvps.add(new BasicNameValuePair("name", "中国"));
nvps.add(new BasicNameValuePair("age", "70"));
UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps, Charset.forName("UTF-8")); //中文乱码

HttpPost httpPost = new HttpPost("http://127.0.0.1:8090/demo/http/post");
httpPost.setEntity(entity);
```

```java
//2.查看HTTP数据
System.out.println(entity.getContentType()); //Content-Type: application/x-www-form-urlencoded; charset=UTF-8
System.out.println(entity.getContentLength()); //30
System.out.println(EntityUtils.toString(entity)); //name=%E4%B8%AD%E5%9B%BD&age=70
```

> 传输 JSON

```java
String json = "{\"name\":\"中国\",\"age\":\"70\"}";
StringEntity entity = new StringEntity(json, "UTF-8"); //中文乱码,默认"ISO-8859-1"
entity.setContentEncoding("UTF-8");
entity.setContentType("application/json");//设置contentType --> json

HttpPost httpPost = new HttpPost("http://127.0.0.1:8090/demo/http/postBody");
httpPost.setEntity(entity);
```

> 传输 File

```xml
<!-- HttpClient-File -->
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpmime</artifactId>
</dependency>
```

```html
<!-- 前台页面 -->
<form action="http://127.0.0.1:8090/demo/http/postFile" method="POST" enctype="multipart/form-data">  
    <input type="text" name="fileName" value="中国"/>  
    <input type="file" name="file"/>  
    <inupt type="submit" value="提交"/>  
</form>
```

```java
//后台逻辑
MultipartEntityBuilder builder = MultipartEntityBuilder.create();
ContentType contentType = ContentType.create("text/plain","UTF-8");//中文乱码,默认"ISO-8859-1"
builder.addTextBody("fileName", "中国", contentType);
builder.addBinaryBody("file", new File("C:\\Users\\BlueCard\\Desktop\\StatusCode.png"));
HttpEntity entity = builder.build();

HttpPost httpPost = new HttpPost("http://127.0.0.1:8090/demo/http/postFile");
httpPost.setEntity(entity);
```

## 请求结果解析

请求结果解析通用于 GET 和 POST。

```java
String uri = "http://127.0.0.1:8090/demo/http/get?name=中国&age=70";
HttpGet httpGet = new HttpGet(uri); //组装请求-GET
// HttpPost httpPost = new HttpPost(uri); //组装请求-POST

try (CloseableHttpResponse httpResponse =
             HttpClients.createDefault().execute(httpGet)) { //发送请求，连接自动关闭
    if (null != httpResponse && HttpStatus.SC_OK ==
            httpResponse.getStatusLine().getStatusCode()) {
        String res = EntityUtils.toString(httpResponse.getEntity(), "UTF-8"); //获取结果
        System.out.println(res);
    }
} catch (IOException e) {
    e.printStackTrace();
}
```



#Commons

## Lang

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
</dependency>
```

> StringUtils

```java
//null和""
boolean empty = StringUtils.isEmpty(" "); //false

//null和""，以及" "
boolean blank = StringUtils.isBlank(""); //true

//删除所有空格（空格+制表符+换行）
String deleteWhitespace = StringUtils.deleteWhitespace("   ab   c  "); //abc

//trim()的升级版，去除前导和后续的指定字符，不再限于空白符
String trim = StringUtils.strip("01 2 30", "0"); //1 2 3

//stripAll：去除字符串数组中每个元素中的指定字符
String[] strs = {"010", "02", "30"};
String[] stripAll = StringUtils.stripAll(strs, "0"); //1 2 3
```

```java
//判断是否包含这个字符
boolean contains = StringUtils.contains("中华人民共和国", "共和"); //true

//截取从from开始字符串，区分大小写。截取失败返回空字符串""
String from = StringUtils.substringAfter("SELECT * FROM PERSON", "from"); //""

//截取左边两个字符
String left = StringUtils.left("中华人民共和国", 2); //中华

//在左边填充指定字符,使之总长度为6
String x = StringUtils.leftPad("123", 6, '0'); //000123
String format = String.format("%06d", 123); //jdk自带，不好用。000123
```

```java
//判断该字符串是不是为数字(0~9)组成，如果是，返回true。但该方法不识别有小数点和 请注意。
boolean numeric = StringUtils.isNumeric("45453.4");//false

//将数组中的内容以","分隔
List<String> list = Arrays.asList("a", "b", "c");
String join = StringUtils.join(list, ","); //a,b,c

//首字母大写
String capitalize = StringUtils.capitalize("中华人民共和国"); //Abc
```

```java
//字符串进行省略操作，省略字符以省略号填充，最小长度为4（省略号占3个字符）
StringUtils.abbreviate("abcdefg", 6); //abc...
StringUtils.abbreviate("abcdefg", 4); //a...
StringUtils.abbreviate("abcdefg", 3); //IllegalArgumentException
```

> NumberUtils

```java
int i = NumberUtils.toInt("5f", 5); //字符串转int，默认值5

boolean parsable = NumberUtils.isParsable("5.5"); //字符串是否是数字? true
boolean digits = NumberUtils.isDigits("5.5"); //字符串中是否全为数字? false
```

> RandomStringUtils：指定长度的随机 数字，字母，字母和数字

```java
String randomNumeric = RandomStringUtils.randomNumeric(5); //60954
String randomAlphabetic = RandomStringUtils.randomAlphabetic(5); //MgQgI
String randomAlphanumeric = RandomStringUtils.randomAlphanumeric(5); //Mq985
```

> ClassUtils

```java
//取得类名和包名
String shortClassName = ClassUtils.getShortClassName(Test.class);
String packageName = ClassUtils.getPackageName(Test.class);
```

## IO

```xml
<!-- https://mvnrepository.com/artifact/commons-io/commons-io -->
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.6</version>
</dependency>
```

> IOUtils

```java
IOUtils.closeQuietly(in); //不再推荐使用这种关闭流方式，推荐使用java7新特性：try-with-resources
```

```java
try (FileInputStream in = new FileInputStream(src);
     FileWriter out = new FileWriter(dest)) {
    IOUtils.copy(in, out, "UTF-8"); //拷贝流，从输入到输出
} catch (IOException e) {
    e.printStackTrace();
}
```

```java
//拷贝较大的数据流，比如2G以上
IOUtils.copyLarge(new FileInputStream(src), new FileOutputStream(dest));
```

```java
String line = IOUtils.toString(in, "UTF-8"); //读取流中的字符串
```

```java
IOUtils.write("1234", new FileOutputStream(dest), "UTF-8"); //字符串写入输出流
```

> FileUtils

```java
List<String> lines = FileUtils.readLines(file, "UTF-8"); //读取文件
```

```java
FileUtils.listFiles(dir, null, true); //迭代遍历目录

FileUtils.listFiles(dir, EmptyFileFilter.NOT_EMPTY, null); //过滤非空文件，不过滤目录

FileUtils.deleteDirectory(new File(path)); //迭代删除文件夹
```


```java
FileUtils.copyFile(src, dest); //拷贝文件
```

```java
URLCodec urlCodec = new URLCodec();
String url = "http://192.168.5.25:8080/webpark/image/20190518/" +
    urlCodec.encode("十二pass.log", "UTF-8"); //url中文 进行编码和解码
String dest = "C:\\Users\\BlueCard\\Desktop";

URL httpUrl = new URL(url);
String fileName = urlCodec.decode(FilenameUtils.getName(httpUrl.getFile()), "UTF-8");

//下载URL资源，注意设置超时时间,单位毫秒
FileUtils.copyURLToFile(httpUrl, new File(dest, fileName), 5 * 1000, 5 * 1000);
```

> FilenameUtils

```java
String filePath = "D:\\abc\\123.txt";
String name = FilenameUtils.getName(filePath); //123.txt
String baseName = FilenameUtils.getBaseName(filePath); //123
String extension = FilenameUtils.getExtension(filePath); //txt
```

> Files

```java
//已过时，推荐使用：java.nio.file.Files
long freeSpace = FileSystemUtils.freeSpace("D:/");

FileStore fileStore = Files.getFileStore(Paths.get("D:/"));
long totalSpace = fileStore.getTotalSpace(); //总容量
long usableSpace = fileStore.getUsableSpace(); //可用容量
```

## codec

```xml
<!-- https://mvnrepository.com/artifact/commons-codec/commons-codec -->
<dependency>
    <groupId>commons-codec</groupId>
    <artifactId>commons-codec</artifactId>
    <version>1.12</version>
</dependency>
```

> Base64

```java
try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
     BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dest))) {
    Base64 base64 = new Base64();

    byte[] buff = new byte[(int) new File(src).length()];
    bis.read(buff);
    String encode = base64.encodeAsString(buff);
    System.out.println("encode：" + encode); //File -> Base64

    byte[] decode = base64.decode(encode);
    for (int i = 0; i < decode.length; ++i) {
        if (decode[i] < 0) { //调整异常数据
            decode[i] += 256;
        }
    }
    bos.write(decode); //Base64 -> File
} catch (IOException e) {
    e.printStackTrace();
}
```

> MD5

```java
//org.apache.commons.codec.digest;
String md5Hex = DigestUtils.md5Hex("123456");

//org.springframework.util.DigestUtils;
String md5Hex = DigestUtils.md5DigestAsHex("123456".getBytes());
```

> URLCode

```java
URLCodec urlCodec = new URLCodec();
String encode = urlCodec.encode("abcdef", "UTF-8"); 

String decode = urlCodec.decode(encode, "UTF-8");
```

## Collections

```xml
<!-- https://mvnrepository.com/artifact/org.apache.commons/commons-collections4 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.3</version>
</dependency>
```

<http://www.imooc.com/article/271570>

> CollectionUtils：公用的接口和工具类（覆盖所有子类）

```java
List<String> list0 = Arrays.asList("1", "3", "5");
List<String> list1 = Arrays.asList("1", "13", "5");

//得到两个集合中相同的元素：[1, 5]
Collection<String> retainAll = CollectionUtils.retainAll(list0, list1);

//移除第二集合中的元素：[3]
Collection<String> removeAll = CollectionUtils.removeAll(list0, list1);
```

> ArrayUtils

```java
int[] array = {1, 3, 5, 7, 8};
int[] removeElement = ArrayUtils.removeElement(array, 5); //删除指定元素：1 3 7 8
```

```java
int[] insert = ArrayUtils.insert(3, array, 0, 69); //在 index 为3的位置添加两个元素 0,69
```

```java
ArrayUtils.reverse(array); //数组反转
```



# SonarQube

国外版ali开发手册。代码质量管理平台，可以快速的定位代码中潜在的或者明显的错误

## 下载配置

```properties
#其中汉化包plugins拷入 F:\sonarqube-7.3\extensions\plugins
server: https://www.sonarqube.org/downloads/
plugins: https://github.com/SonarQubeCommunity/sonar-l10n-zh/releases
client(可省): https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner
```
```properties
#编辑配置文件：F:\sonarqube-7.3\conf\sonar.properties
#其中，数据库默认使用内置H2，推荐使用mysql。新建mysql数据库'sonarqube'
sonar.web.host=0.0.0.0
sonar.web.port=9000
sonar.login=admin
sonar.password=admin
sonar.jdbc.username=bluecardsoft
sonar.jdbc.password=#$%_BC13439677375
sonar.jdbc.url=jdbc:mysql://192.168.8.7:33306/sonarqube?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true&useConfigs=maxPerformance&useSSL=false
#sonar.web.context=/your_prefix  //非必须，若要在访问sonarqube时加上统一前缀则配置此项
```
```properties
#启动服务
启动脚本: "F:\sonarqube-7.3\bin\windows-x86-64\StartSonar.bat"
cmd验证: 屏幕最后出现"xxx SonarQube is up"
web验证: 默认用户名密码admin，连接 http://localhost:9000

停止服务: 命令行Ctrl+C 或 kill端口'netstat -aon | findstr 9000 ===> taskkill -f /pid xxx'
异常日志: "F:\sonarqube-7.3\logs\sonar.log"
```
```properties
#分析项目
项目-->分析新项目-->新建令牌(admin)-->待测项目的pom同级目录执行以下命令
mvn sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=ea23d2ae8d458cf020f8028b7f2b32fca909c83f
```

## idea插件

```java
解压安装包，将'SonarLint'文件夹拷贝至'idea安装目录/plugins'
idea->settings->plugins->Install plugin from disk，选中'sonarlint-intellij-4.0.0.2916.jar'

重启idea，完成安装。以下进行配置sona：
idea->File-->Settings-->Other Settings-->SonarLint General Settings
```
```java
//配置Client（可省）
配环境变量: name=SONAR_HOME, value=F:\sonar-scanner-3.2.0.1227-windows
path前添加: %SONAR_HOME%\bin
cmd验证: 'sonar-scanner -v'

//在分析项目demo的根目录下新建文件 F:\sp_project\demo\sonar-project.properties
sonar.projectKey=TGB-demo
sonar.projectName=demo
sonar.projectVersion=1.0
sonar.sources=src
sonar.language=java
sonar.sourceEncoding=UTF-8
sonar.java.binaries=F:/sp_project/demo/target/classes

切换到分析项目demo根目录 "F:\sp_project\demo", 
使用命令分析项目: 'F:\sonar-scanner-3.2.0.1227-windows\bin\sonar-scanner.bat'
打开web,查看分析结果: http://localhost:9000/
```

##常见问题

```properties
https://blog.csdn.net/happyzwh/article/details/77991095
https://www.jianshu.com/p/b50f01eeba4d
```


















































































































