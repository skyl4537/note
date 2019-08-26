[TOC]

# Tomcat

> 本质就是一个 ServerSocket

```shell
ServerSocket 指定端口，启动后等待客户端连接
客户端建立连接后，为每一个客户端开启一个线程去处理
从客户端的输入流中解析请求信息 Request
根据请求 url 查找对应的处理 Servlet.（其中，<请求url, 处理Servlet>，映射关系从 web.xml 读取）
反射调用 Servlet 中的 service() 方法，处理业务逻辑
封装响应结果 Response，发送到客户端
```

> 代码实现

```java
public class WebServer {
    private ServerSocket serverSocket;  //服务端的 ServerSocket
    private boolean isRunning;          //服务端是否允许
    ScheduledExecutorService pool;      //服务端处理线程池
    private static Map<String, String> servletMap = new HashMap<>(); //请求url 与 处理Servlet 映射关系

    //模拟从 web.xml 读取映射关系.<请求url, 处理Servlet>
    static {
        servletMap.put("/hello", "webserver.HelloServlet");
        servletMap.put("/login", "webserver.LoginServlet");
        servletMap.put("/error", "webserver.ErrorServlet");
    }

    public static void main(String[] args) {
        WebServer webServer = new WebServer();
        webServer.start(); //启动服务端
    }

    private void start() {
        serverSocket = new ServerSocket(8090); //Web容器底层就是一个 ServerSocket
        pool = Executors.newScheduledThreadPool(5);
        isRunning = true;

        while (isRunning) {
            Socket socket = serverSocket.accept();
            pool.execute(() -> dispatcherServlet(socket));
        }
    }

    private void dispatcherServlet(Socket socket) {
        Request request = new Request(socket);
        Response response = new Response(socket);

        String requestUrl = request.getUrl(); //请求url -> 业务Servlet
        String servletStr = servletMap.getOrDefault(requestUrl, servletMap.get("/error"));

        Class<?> clazz = Class.forName(servletStr); //反射执行service方法【核心逻辑封装到Servlet】
        IServlet iServlet = (IServlet) clazz.newInstance();
        iServlet.service(request, response);

        socket.close(); //处理完，关闭 Socket
    }

    private void stop() {
        serverSocket.close();
        isRunning = false;
    }
}
```





#Web

## 重定向

> 路径前缀 /

```shell
#由服务器解析 --> 表示：web应用的根目录 --> http:127.0.0.1:8090/demo/

@GetMapping("/hello")                                  #Serlvet映射的访问路径
req.getRequestDispatcher("/hello").forward(req, res);  #请求转发
```

```shell
#由浏览器解析 --> 表示：web站点的根目录 --> http:127.0.0.1:8090/

<a href="/hello.html">测试</a>         #超链接<a>
<form method="post" action="/hello">  #Form表单的action
req.sendRedirect("/hello");           #请求重定向
```
> 转发 & 重定向

```shell
Servlet接收到浏览器请求后，进行一定的处理，先不进行响应，而是在服务端内部'转发'给其他Servlet继续处理。
浏览器只发出 1次请求，'浏览器url不会改变'，用户也感知不到请求被转发。
```
```shell
Servlet接收到浏览器请求并处理后，响应浏览器'302状态码 和 新地址'，状态码302要求浏览器去请求新地址，整个过程浏览器发出 2次请求。
```

```shell
1 ; 2    #发送请求次数(request个数)
否; 是    #浏览器url地址是否改变
是; 否    #是否共享对象 request，传递request中数据
是; 否    #目标资源是否可以是 WEB-INF 下资源

转发：只能是当前web应用的资源。
重定向：任意资源，甚至网络资源

转发  ：'/'代表当前'web应用'的根目录; //http://localhost:8090/demo/
重定向：'/'代表当前'web站点'的根目录. //http://localhost:8090/
```

> 代码实现

```java
req.setAttribute(key, value); //转发前绑定数据,在目标资源取出数据
req.getRequestDispatcher("转发地址").forward(req, resp);

resp.sendRedirect("重定向地址"); //重定向
```

## jsp

> jsp页面本质就是一个Servlet

```shell
jsp善于处理页面显示，Servlet善于处理业务逻辑，二者结合使用。

执行时，先转化为java文件，再编译成class文件。
转化过程：java代码照搬；html + css + 表达式等通过流输出'out.write()'
```

> 9大内置对象

```shell
#pageContext < request < session < application （作用域：从小到大）

ServletContext application;
HttpSession session;
HttpServletRequest request;            #同一个请求
PageContext pageContext;               #当前页面的上下文.(可从中获取到其余 8 个隐含对象)

JspWriter out = response.getWriter();  #用于页面显示信息，out.println();

HttpServletResponse response;          #几乎不用（X）
Throwable exception                    #<%@ page isErrorPage="true" %>，才可以使用（X）
ServletConfig config;                  #Servlet.ServletConfig.（X）
Object page = this;                    #jsp对象本身，或编译后的Servlet对象; 实际上就是this.（X）
```

> jsp中的java

```jsp
<% Date date = new Date(); out.print(date); %> <!--嵌入jsp的Java代码段-->

<%= date %> <!-- 脚本表达式.(上述的简化版)->
```

> jstl：jsp标准标签库。jsp标签集合，封装了jsp应用的通用核心功能。

```jsp
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%> <!--声明-->
```

```jsp
<!-- test：判断条件； var：用于存储条件结果的变量； scope：var的作用域 -->
<c:if test="${salary > 20000}" var="flag" scope="session">
    <p>我的工资为: <c:out value="${salary}"/><p>
</c:if>
<c:if test="${not flag}"> <!--等同于: ${!flag}-->
    <p>我的工钱为: <c:out value="${salary}"/><p>
</c:if>
```

```jsp
<c:if test="${empty emps}">
    <h2 align="center">没有任何员工信息.</h2>
</c:if>
<c:if test="${!empty emps}">
    <table border="1px" width="70%" align="center" cellspacing="0px">
        <!--items: 要迭代的集合; var: 当前迭代出的元素-->
        <c:forEach items="${emps}" var="emp"> 
            <tr align="center">
                <td>${emp.id }</td>
                <td>${emp.lastName }</td>
                <td>${emp.gender==0?"女":"男" }</td>
                <td>${emp.dept.deptName }</td>
                <td>
                    <a href="#">修改</a> &nbsp;&nbsp; <a href="#">删除</a>
                </td>
            </tr>
        </c:forEach>
    </table>
</c:if>
```



## Servlet

>HTTP请求

```shell
建立连接 --> 发送请求信息 --> 回送响应信息 --> 关闭连接

每一次请求，都要重新建立一次连接，都会调用一次 service() 方法
每次连接只能处理一个请求和响应
```

> Servlet是什么

```shell
浏览器发送一个HTTP请求，HTTP请求由Servlet容器（如tomcat）分配给特定的Servlet进行处理
Servlet的本质是一个Java对象，这个对象拥有一系列的方法来处理HTTP请求。常见的方法有doGet()，doPost()等。
Web容器中包含了多个Servlet，特定的HTTP请求该由哪一个Servlet来处理是由Web容器中的 web.xml 来决定的。
```

```shell
Servlet本质就是javax.servlet包下的一个接口，工作原理就是：反射+回调。目前所有的MVC框架的Controller基本都是这么个模式。

Servlet的执行是由容器（如Tomcat）通过 web.xml 的配置反射出 Servlet 对象后回调其 service()方法。
```

> Servlet作用

```shell
将 url 映射到一个java类的处理方法上
接收请求的数据, 并与服务器其他资源进行通信（如数据库）
将处理结果展示到页面，以及处理页面的跳转

编译存储在目录"web应用/WEB-INF/classes/*".
```

>Servlet容器

```shell
负责处理客户请求。当客户请求到来时，调用某个 Servlet，并把 Servlet 的执行结果返回给客户.
典型的 Servlet 应用是监听器，过滤器的实现

Servlet 本质是一个java接口类，部署运行在Servlet容器中
Servlet 容器管理Servlet的整个生命周期，并负责调用Servlet方法响应客户端请求
Servlet 和客户端的通信采用"请求/响应"模式，基于请求
```

> Servlet生命周期：Servlet容器管理，所有请求公用同一个Servlet对象，`非线程安全`

```shell
构造方法    #默认，只在第一次请求时调用（可配置创建时机）。创建【单实例】的Servlet对象。减小服务端内存开销，快速响应客户端
init()     #只被调用一次。调用构造器方法后立即被调用。用于初始化当前Servlet
service()  #每次请求都会调用。用于响应客户端请求
destory()  #只被调用一次。应用被卸载前调用。用于释放Servlet所占用的资源（如数据库连接）
```

>Servlet容器默认采用`单实例多线程`的方式处理多个请求

```shell
当web服务器启动的时候（或客户端发送请求到服务器时），Servlet就被加载并实例化（只存在一个Servlet实例）

容器初始化Servlet。主要就是读取配置文件
（如tomcat，可以通过servlet.xml的<Connector>设置线程池中线程数目，初始化线程池；通过web.xml，初始化每个参数值等等）；

当请求到达时，Servlet容器通过调度线程（Dispatchaer Thread）调度它管理下的线程池中等待执行的线程（Worker Thread）给请求者；
线程执行Servlet的service()方法；
请求结束，放回线程池，等到被调用；
```

> BOOTの自定义Servlet

```java
@ServletComponentScan //全局注解; 启动时自动扫描 @WebServlet,并将该类实例化
```

```java
@WebServlet(name = "testServlet", urlPatterns = "/test")
public class TestServlet extends HttpServlet {

    //每次请求都会调用。用于响应客户端请求
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=utf-8");
        resp.getWriter().write("自定义 Servlet");
    }
}
```

>`ServletConfig`：封装了Servlet的初始化参数，可获取对象 ServletContext

```java
/**
 * @param name          Servlet别名
 * @param urlPatterns   访问url，同一个Servlet可以被映射到多个URL上。其中'/'代表当前web应用的根目录
 * @param loadOnStartup Servlet实例被创建的时机。默认-1
 *                      负数：在第一次请求时被创建
 *                      正数或0：在当前应用被Servlet容器加载时创建实例，且数值越小越早被创建
 * @param initParams    配置Servlet的初始化参数，在init()方法中读取
 */
@WebServlet(name = "testServlet", urlPatterns = {"/test", "/servlet"}, loadOnStartup = -1,
            initParams = @WebInitParam(name = "name", value = "123"))
```

```xml
<servlet> <!-- xml版本配置 -->
    <servlet-name>testServlet</servlet-name>
    <servlet-class>com.x.javaweb.TestServlet</servlet-class>

    <init-param> <!--初始化参数. 此节点必须在<load-on-startup>之前-->
        <param-name>name</param-name>
        <param-value>123</param-value>
    </init-param>
    <load-on-startup>-1</load-on-startup> <!--指定 Servlet 被创建的时机-->
</servlet>

<servlet-mapping>
    <servlet-name>testServlet</servlet-name>
    <url-pattern>/test</url-pattern> <!--访问路径. 其中'/'代表当前web应用的根目录-->
</servlet-mapping>
```

```java
@Override
public void init(ServletConfig config) {
    System.out.println("生命周期 - init()");

    String user = config.getInitParameter("name"); //获取单个初始化参数. "123"

    Enumeration<String> names = config.getInitParameterNames(); //获取所有
    while (names.hasMoreElements()) {
        String name = names.nextElement();
        String value = config.getInitParameter(name);
    }
    
    ServletContext servletContext = config.getServletContext(); //获取 ServletContext 对象
}
```

>`ServletContext`：代表当前WEB应用，可以从中获取到应用的各个方面信息。如，web应用的初始化参数：`IoC容器也是配置在此`

```xml
<context-param> <!-- 配置web应用的初始化参数。与节点 servlet 同级-->
    <param-name>driver</param-name>
    <param-value>com.mysql.jdbc.Driver</param-value>
</context-param>
```

```java
public void init(ServletConfig config) throws ServletException {
    ServletContext context = config.getServletContext();
    String driver = context.getInitParameter("driver"); //获取单个初始化参数

    Enumeration<String> names = context.getInitParameterNames(); //获取所有
    while (names.hasMoreElements()) {
        String name = names.nextElement();
        String value = context.getInitParameter(name);
    }
}
```
>ServletContext常用方法

```java
context.getContextPath(); //server.servlet.context-path=/demo
```

```java
//F:\sp_project\webpark\src\main\webapp\demo.log
context.getRealPath("abc.log"); //文件在服务器上的绝对路径，而非部署前路径
```

```java
InputStream in = context.getResourceAsStream("my.properties"); //读取配置
//InputStream in = getClass().getClassLoader().getResourceAsStream("my.properties"); //同上
Properties properties = new Properties();
properties.load(in);
String url = properties.getProperty("info.url");
```

```java
context.setAttribute("user", "123"); //属性相关的三个方法（设置，获取，移除）
String user = (String) context.getAttribute("user");
context.removeAttribute("user");
```









## filter

>过滤器：对发送到 Servlet 的请求，以及对发送到客户端的响应进行拦截





## listener

> Servlet监听器

```shell
#四大域对象: ServletContext; HttpSession; ServletRequest; PageContext

用于监听Web程序'三大域对象(PageContext 除外)'的创建与销毁事件，及属性发生变化事件。
其中，PageContext 生命周期为当前页面，所以不用监听。

注册和调用过程都是由web容器自动完成的。当监听的事件被触发时，自动调用自定义的处理方法。
一个web程序只会为每个事件监听器创建一个对象，所以在自定义事件监听器时，应考虑'多线程安全'问题。
```

>Servlet监听器分类

```shell
三大域对象'创建和销毁'的事件监听器
三大域对象'属性变更'的事件监听器   #较少使用
'感知Session绑定'的事件监听器    #较少使用
```

>三大对象の创建和销毁

```xml
<listener> <!-- xml配置 -->
    <listener-class>com.example.config.MyServletListener</listener-class>
</listener>
```

```java
@WebListener //@ServletComponentScan 全局配置，注解配置
public class MyServletListener implements ServletContextListener, HttpSessionListener, ServletRequestListener {

    /**
     * ServletContext 对象创建时调用，即Web程序在服务器上部署时
     * 用途: 创建Spring的IOC容器；创建数据库连接池; 读取当前WEB应用的初始化参数
     *
     * @param sce 代表当前WEB应用，可以从中获取到应用的各个方面信息。最早创建，最晚销毁
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {}

    /**
     * SC对象销毁时调用，即web程序从服务器卸载时
     * SpringBoot项目 --> 只有 'Run_As->Boot_App' 这种方式启动，关闭时才会回调此方法
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {}
}
```

> 三大对象の属性变更

```java
@WebListener
public class MyAttributeListener implements ServletContextAttributeListener, HttpSessionAttributeListener,
ServletRequestAttributeListener { }
```

>感知Session绑定：监听实现该接口的 Java 类对象被绑定到 Session 或从 Session 中解除绑定的事件

```java
//不需要添加注解 @WebListener
public class MyListener implements HttpSessionBindingListener, HttpSessionActivationListener, Serializable { }
```





## 表单提交

> 表单重复提交的3种场景

```shell
（1）网络延迟，用户多次点击'submit'按钮
（2）表单提交后，转发到目标页面，用户点击'刷新'按钮
（3）提交表单后，点击浏览器的'后退'按钮，回退到表单页面，再次进行'提交'

点击'返回'，'刷新'原表单页面，再'提交' #不属于表单重复提交
```

>场景（1）--> 用js控制表单只能提交一次（两种方案，推荐1）

```html
<form method="post" onsubmit="doSubmit()" th:action="@{/user/login}"></form>
```

```javascript
//方式1：只能提交一次
var isCommitted = false;
function doSubmit() {
    if (false === isCommitted) {
        isCommitted = true;
        return true; //返回true，让表单正常提交
    } else {
        return false; //返回false，则表单将不提交
    }
}
```

```javascript
//方式2：提交后按钮置为不可用
function doSubmit() {
    var btnSubmit = document.getElementById("submit");
    btnSubmit.disabled = true;
    return true;
}
```

> 场景（2） --> 表单提交后直接重定向到目标页面

```java
resp.sendRedirect("重定向地址"); //转发到目标页面，点击[刷新]会一直请求之前的表单
```

> 场景（123） --> 利用Session的token机制

```java
//（1）生成token，存Session，并转发前台页面
@GetMapping("/token")
public String token(HttpSession session) {
    session.setAttribute("token", UUID.randomUUID().toString()); //token 存 Session 中
    return "/mvc/token";
}
```

```html
<!--（2）前台页面使用hidden存储token，表单提交时携带-->
<form id="form-login" method="post" th:action="@{/mvc/login}">
    <input type="hidden" name="token" th:value="${session.token}"/>
    ...
    <button class="btn btn-primary" type="submit">登录</button>
</form>
```



#Spring

## IoC

> 轻量级框架

```shell
#什么是轻量级
非侵入式，不需要实现所使用框架的任何接口。这样，就算以后切换框架也勿需修改源码。
```

```shell
#什么是框架
框架是软件的半成品。为解决问题制定的一套整体解决方案，在提供功能基础上进行扩充。框架中不能被封装的代码（变量），需要使用配置文件（xml）。

#类库 & 框架
类库：`提供的类没有封装一定逻辑`。举例：类库就是名言警句，写作文时引入名言警句
框架：`区别与类库，里面有一些约束`。举例：框架是填空题
```

```shell
#每一层递进，都是代码重用的结果
基础语法 --> 方法 --> 类 --> jar --> 框架 
```

> 基础概念

```shell
Spring是一个 'IoC'（DI）和 'AOP' 容器框架。

```





> IoC容器创建：`详见 Servlet`

```xml
<!-- tomcat启动时，默认加载'web.xml'文件。在Web应用的初始化信息中配置 IoC 容器的配置文件 -->
<!-- 在 Web 应用被 tomcat 加载时创建IoC容器，然后放到 ServletContext 属性中，供其他模块使用 -->
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath:applicationContext.xml</param-value> <!--Spring配置文件的名称和位置-->
</context-param>
```

```xml
<listener> <!--启动 IOC 容器的 ServletContextListener-->
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
```





## AOP

> AOP（Aspect-Oriented-Programing）面向切面编程

```shell
通过'动态代理'实现程序功能的统一维护
通过对既有程序定义一个切入点，然后在切入点前后切入不同的执行内容。如：打开/关闭数据库连接，打开/关闭事务，记录日志等

#基于AOP不会破坏原来程序逻辑。
因此，它可以很好的对业务逻辑的各个部分进行隔离，从而使得业务逻辑各部分之间的耦合度降低，提高程序的可重用性，同时提高了开发的效率
```

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```

> 优先级

```shell
#使用注解 @Order(i) 定义每个切面的优先级。i 的值越小，优先级越高。
#前置通知，从小到大。后置返回通知，从大到小
@Before          @Order(5)  --> @Order(10)
@After           @Order(10) --> @Order(5)
@AfterReturning  @Order(10) --> @Order(5)
```

>切入点表达式：定位 一个或多个具体的切入点

```shell
execution(<权限修饰符> [返回值类型] [简单类名/全类名] [方法名]([参数列表]) <异常列表>)

*            --> 匹配任意类型
..           --> 匹配任意多个任意类型入参，或当前包及其子孙包
(*, String)  --> 匹配第一个参数任意类型，第二个参数String
(..)         --> 匹配零个或多个任意类型的参数
```

```shell
execution(public * *(..))                        #匹配 所有类中的 public 方法
execution(* *To(..))                             #匹配 所有以 To 为后缀的方法
execution(* com..*.*Dao.find*(..))               #匹配 包名前缀 com，类名后缀 Dao，方法名前缀 find 的所有方法

execution(* com.demo.service.InfoService.*(..))  #匹配 com.demo.service.InfoService 类中的所有方法
execution(* com.demo.service.*.*(..))            #匹配 com.demo.service 包下所有类的所有方法
execution(* com.demo.service..*(..))             #匹配 com.demo.service 包，及其子孙包下，所有类的所有方法
```

```shell
#可以使用 && || ! 三个运算符连接各个切点签名（&& 表示同时满足）

#任意类中第一个参数为 int 类型的 add 方法或 sub 方法
execution (* *.add(int,..)) || execution(* *.sub(int,..))

#匹配任意类中 第一个参数为 int 类型的 add 方法 以外的方法
!execution (* *.add(int,..))
```

> DEMO：记录一次请求所需时间 + ThreadLocal

```java
@Slf4j
@Aspect     // 标明该类是一个切面类
@Order(5)   // 切面的优先级 -> 值越小，优先级越高
@Component  // Spring管理
public class AopConfig {

    // 定义切入点（重用机制）: 切入点表达式 + 切入点签名
    @Pointcut("execution(* com.example.mybatis.controller..*(..))")
    private void ctrl() {}

    @Pointcut("execution(* com.example.mybatis.service..*(..))")
    private void service() {}

    // 可以将一些公用的 切入点 放到一个类中，以供整个应用程序使用。使用时，指定完整的类名加切入点签名
    // 如: @Before("com.x.config.AopConfig.Pointcuts.ctrl()")
    class Pointcuts {
        @Pointcut("execution(* com.example.mybatis.controller..*(..))")
        private void ctrl() {
        }
    }

    // {目标} -> 记录一次请求所需时间
    // 定义一个成员变量来给 @doBefore 和 @doAfterReturning 一起访问??? 是否会有同步问题???
    // 答案是肯定的。正确做法是引入 ThreadLocal 对象
    private ThreadLocal<Long> startTime = new ThreadLocal<>();

    // (0).前置通知 - 在方法执行之前执行
    @Before("ctrl() || service()")
    public void doBefore(JoinPoint joinPoint) {
        startTime.set(System.currentTimeMillis()); // 请求开始时间

        List<Object> args = Arrays.asList(joinPoint.getArgs());
        // log.info(System.getProperty("line.separator"));// 系统级别的换行符
        log.info("【前置通知】 {} BEGIN WITH: {}", getMethodAllName(joinPoint), args);
    }

    // (1).后置通知 - 在方法返回结果或抛出异常，都会执行
    @After("ctrl()")
    public void doAfter(JoinPoint joinPoint) {
        long spendTime = System.currentTimeMillis() - startTime.get();// 请求总消耗时间

        log.info("【后置通知】 {] END. SPEND TIME: {} ", getMethodAllName(joinPoint), spendTime);
    }

    // (2).返回通知 - 正常返回才会执行，抛异常则不会
    @AfterReturning(value = "ctrl()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {// 第二个参数名必须和 returning 属性值相同
        log.info("【返回通知】 {} END WITH: {} ", getMethodAllName(joinPoint), result);
    }

    // (3).异常通知 - 只在连接点抛出异常时才执行
    @AfterThrowing(value = "ctrl()", throwing = "t")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable t) {// Throwable是所有错误和异常类的超类，推荐
        log.error("【异常通知】 {} throws {} ", getMethodAllName(joinPoint), t);
    }

    // (4).环绕通知
    // @Around(value = "")
    public void doAround(JoinPoint joinPoint) {
        // TODO
    }

    // 获取完整方法名 - 类全名.方法名()
    private String getMethodAllName(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        String className = signature.getDeclaringTypeName();// 类全名
        String methodName = signature.getName(); // 方法名
        return className.concat(".").concat(methodName).concat("()");
    }
}
```





# 相关注解

>@RequestParam：将`请求行或请求体`的参数（String）转化为简单类型

```shell
本质是将 Request.getParameter(); 获取的 String 转换为简单类型（由 ConversionService 配置的转换器来完成）
所以，可以处理 GET POST 的请求行，也可以处理 POST 的请求体。
```

```java
//GET，请求行 ---> 默认格式（application/x-www-form-urlencoded）

@GetMapping("/param0")
public ResultVO param0(@RequestParam(value = "name", required = true) String uName,
                       @RequestParam(value = "age", required = false, defaultValue = "18") Integer uAge) {
    System.out.println("param0: " + uName + "-" + uAge);
    return ResultVOUtil.success();
}
```

```java
//POST，请求行 ---> 默认格式 或 JSON
//POST，请求体 ---> 默认格式

@PostMapping("/param1")
public ResultVO param1(@RequestParam(value = "name", required = true) String uName,
                       @RequestParam(value = "age", required = false, defaultValue = "18") Integer uAge) {
    System.out.println("param1: " + uName + "-" + uAge);
    return ResultVOUtil.success();
}
```

> @RequestBody：将`请求体`的参数（JSON）转化为bean

```shell
本质是用 HandlerAdapter 配置的 HttpMessageConverters 来解析请求体，然后绑定到相应的 bean 上

'@RequestParam 和 @RequestBody 可以相结合使用'
```

```java
//POST，请求体 ---> JSON

@PostMapping("/param3")
public ResultVO param3(@RequestBody Student student) {
    System.out.println("param3: " + student);
    return ResultVOUtil.success(student);
}
```





# SSM

##web.xml

> 加载过程

```shell
web.xml 用来初始化配置信息：比如 welcome页面、servlet、servlet-mapping、filter、listener、启动加载级别等。

web项目启动时，tomcat容器首先会读取 web.xml 里的配置，当这一步骤没有出错并且完成之后，项目才能正常地被启动起来。
```

```xml
<!-- 初始化 SpringIoC 容器的监听器 -->
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath:applicationContext.xml</param-value>
</context-param>
<listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
```

```shell
（1）.启动 web 项目时，tomcat 首先读取 web.xml 中的两个节点: <context-param/> 和 <listener/>

（2）.紧接着，容器创建一个 ServletContext（application），应用范围内即整个 web 项目都能使用这个上下文
（3）.再接着，容器会将读取到 <context-param> 转化为键值对，存入 ServletContext

（4）.容器创建 <listener/> 中的类实例，即创建监听。在监听器的 contextInitialized() 方法中，通过
     'event.getServletContext().getInitParameter("contextConfigLocation")'方法来得到 <context-param/> 设定的值。
     
（6）.得到 <context-param/> 值之后，就可以 '初始化 Spring-IoC 容器'。

（7）.注意，以上都是在 web 项目还没有完全启动起来的时候就已经完成了的工作，比所有的 Servlet 都要早。

（8）.总的来说，加载顺序是：'<context-param> ---> <listener> ---> <filter> ---> <servlet>'
     其中，如果 web.xml 中出现了相同的元素，则按照在配置文件中出现的先后顺序来加载。
```

```java
public class ContextLoaderListener extends ContextLoader implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        servletContext sc = event.getServletContext();
        String configLocationParam = sc.getInitParameter(CONFIG_LOCATION_PARAM); //"contextConfigLocation"
        //... 初始化 SpringIoc 容器 ...
        //
    }
}
```





> SSM整合

```xml
<!-- 初始化 SpringIoC 容器的监听器 -->
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath:applicationContext.xml</param-value>
</context-param>
<listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>

<!-- 配置 SpringMVC 核心控制器 -->
<servlet>
    <servlet-name>springmvc</servlet-name>
    <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
    <init-param>
        <param-name>contextConfigLocation</param-name> 
        <param-value>classpath:springmvc.xml</param-value>
    </init-param>
    <load-on-startup>1</load-on-startup>
</servlet>
<servlet-mapping>
    <servlet-name>springmvc</servlet-name>
    <url-pattern>/</url-pattern> <!-- 3种可选值 -->
</servlet-mapping>

<!-- 可选参数: ①/ ②*.action ③/* -->
<!--①. 所有地址的访问都要由'前端控制器'进行解析, 静态文件另配不解析-->
<!--②. 以".action"结尾的访问,由'前端控制器'解析-->
<!--③. 错误配置; 当转发到jsp页面时,仍由'前端控制器'解析jsp地址,找不到导致报错-->

<!-- 字符编码过滤器 -->
<filter>
    <filter-name>CharacterEncodingFilter</filter-name>
    <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
    <init-param>
        <param-name>encoding</param-name>
        <param-value>UTF-8</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>CharacterEncodingFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>

<!-- REST 过滤器，POST 转换成 PUT DELETE -->
<filter>
    <filter-name>HiddenHttpMethodFilter</filter-name>
    <filter-class>org.springframework.web.filter.HiddenHttpMethodFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>HiddenHttpMethodFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>

<!-- 欢迎页面 -->
<welcome-file-list>
    <welcome-file>/index.html</welcome-file>
</welcome-file-list>
```



