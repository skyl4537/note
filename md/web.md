[TOC]

# Tomcat

##手动模拟

> 处理流程

```shell
本质就是一个 ServerSocket。ServerSocket 指定端口，启动后等待客户端连接
客户端建立连接后，为每一个客户端开启一个线程去处理
从客户端的输入流中解析请求信息 Request
根据请求 url 查找对应的处理 Servlet.（其中，<请求url, 处理Servlet>，映射关系从 web.xml 读取）
反射调用 Servlet 中的 service() 方法，处理业务逻辑
封装响应结果 Response，发送到客户端

#核心技术：Socket编程 + IO流 + 线程池 + Http请求响应 + xml解析 + 反射 + html
```

> web.xml

```xml
<servlet>
    <servlet-name>helloServlet</servlet-name>
    <servlet-class>com.x.javaweb.HelloServlet</servlet-class>
</servlet>
<servlet-mapping>
    <servlet-name>helloServlet</servlet-name>
    <url-pattern>/hello</url-pattern>
</servlet-mapping>
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
        servletMap.put("/hello", "com.x.javaweb.HelloServlet");
        servletMap.put("/login", "com.x.javaweb.LoginServlet");
        servletMap.put("/error", "com.x.javaweb.ErrorServlet");
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
            pool.execute(() -> dispatchServlet(socket));
        }
    }

    private void dispatchServlet(Socket socket) {
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

## web.xml

> 基本作用

```shell
#用来初始化项目信息：比如 welcome页面、servlet、servlet-mapping、filter、listener、启动加载级别等。
web项目启动时，tomcat容器首先会读取 web.xml 里的配置，当这一步骤没有出错并且完成之后，项目才能正常地被启动起来。
```

>初始化 `SpringIoC` 容器的监听器

```java
@WebInitParam(name = "contextConfigLocation", value = "classpath:applicationContext.xml") //注解版
```

```xml
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath:applicationContext.xml</param-value>
</context-param>
<listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
```

> 加载过程

```shell
web项目启动后，首先读取web.xml文件中的'初始化参数节点'<context-param/>和'监听器节点'<listener/>。
将初始化参数解析成map键值对，保存在'全局上下文' ServletContext 对象中。
然后，实例化监听器对象，在监听器对象的'初始化方法'中读取初始化参数，即初始化 Spring-IoC 容器的配置。

注意，以上都是在web项目还没有完全启动起来的时候就已经完成了的工作，比所有的 Servlet 都要早。
总的来说，加载顺序是：'<context-param> -> <listener> -> <filter> -> <servlet>'
其中，如果 web.xml 中出现了相同的元素，则按照在配置文件中出现的先后顺序来加载。
```

##其他概念

> classpath & classpath*

```sh
classpath ：只能加载找到的第一个资源文件
classpath*：能够加载多个路径下的资源文件
```

```sh
mybatis 需要加载的 xml 文件分别在：'com/example/web/student/sqlxml/*.xml' 和 'com/example/web/teacher/sqlxml/*.xml'

假如，配置文件：'mybatis.mapper-locations=classpath:com/example/web/*/sqlxml/*.xml'，
则报：Invalid bound statement (not found)。修改为：'... classpath* ...'，则不再出问题。
```



# html

## js

> html标签

```sh
radio：单选框，只有加上'name'才具有互斥效果。表单提交的是'value'值
```

```html
男 <input type="radio" name="gender" value="1" />
女 <input type="radio" name="gender" value="0" />
```

> jQuery：js函数库。js：操作html标签

```js
$(this)	    //当前 HTML 元素
$("p")	    //所有 <p> 元素              元素选择器：document.getElementsByTagName("p");
$(".intro")	//所有 class="intro" 的元素  .class选择器：document.getElementsByClassName("test");
$("#intro")	//id="intro" 的元素          #id选择器：document.getElementById("test");

$("p.intro")	     //所有 class="intro" 的 <p> 元素
$("ul li:first")	 //每个 <ul> 的第一个 <li> 元素
$("[href$='.jpg']")	 //所有带有以 ".jpg" 结尾的属性值的 href 属性
$("div#intro .head") //id="intro" 的 <div> 元素中的所有 class="head" 的元素
```

>动态绑定事件

```html
<button id="btn0">动态绑定事件</button>
```

```javascript
$(function () { //简写前: $(document).ready(function () { ---> 文档就绪函数：为了防止文档在完全加载（就绪）之前运行 jQuery 代码
    $("#btn0").click(function () {
        alert("点我上王者..." + $(this).attr('id'));
    });
}); //在DOM载入就绪时就对其进行操纵，并调用执行绑定的函数
```

```js
window.onload = function () {
    //在网页中所有的元素（包括元素的所有关联文件）完全加载到浏览器后才执行
}
```

>静态指定方法

```html
<button id="btn1" onclick="clickBtn1(this)">静态指定方法</button>
```

```javascript
function clickBtn1(e) {
    alert("点我上王者..." + $(e).attr('id')); //两种获取对象属性的方法 --> $(e).attr('id')
}
```

## ajax

> JSON 方法

```javascript
var jsonStr = JSON.stringify(jsonObj); //json -> String
var jsonObj = JSON.parse(jsonStr);     //String -> json
```

> `$.get(url[,data][,callback][,type])` 通过GET请求从服务器请求数据

```html
<button id="btnGetJSON" th:attr="url=@{/mvc/getJSON}">getJSON</button> <!--后台返回json-->
```

```javascript
$(function () { //简写前: $(document).ready(function () {
    $('#btnGetJSON').click(function () {
        $.getJSON($(this).attr('url'),
                  {username: $('#username').val(), password: $('#password').val()},
                  function (data) {
            			alert(data.uName + " - " + data.uPwd);
        });
    });
});
```

```html
<button id="getStr" onclick="getStrFun(this)" th:attr="url=@{/mvc/getMsg}">getStr</button> <!--后台返回String-->
```

```javascript
function getStrFun(e) {
    $.get($(e).attr('url'),
          function (data) {
        		alert(data);
    });
}
```

>`POST` 

```html
<button id="postJSON" onclick="postJSONFun(this)" th:attr="url=@{/mvc/postJSON}">postJSON</button>
```

```javascript
function postJSONFun(e) {
    $.post($(e).attr('url'),
           {username: $('#username').val(), password: $('#password').val()},
           function (data) {
        		alert(data.uName + " - " + data.uPwd);
    }, 'json');
}
```

> `DELETE`

```html
<button id="deleteBtn" th:href="@{/mvc/deleteMsg/5}">deleteMsg</button>
```

```javascript
$('#deleteBtn').click(function () {
    $.ajax({
        type: 'DELETE', //仅部分浏览器支持
        url: $(this).attr('href'),
        dataType: 'text',
        success: function (data) {
            $('#showMsg').val(data);
        }
    });
});
```

> `ajax` 底层原理。如果需要在出错时执行函数，请使用此方法

```html
<button id="ajaxBtn" onclick="clickAjax(this)" th:attr="url=@{/mvc/getJSON}">ajax</button>
```

```javascript
function clickAjax(e) {
    $.ajax({
        url: $(e).attr('url'), //url
        type: 'GET', //GET/POST/DELETE/PUT
        data: {username: $('#username').val(), password: $('#password').val()}, //args; 后台接收 @RequestParam
        dataType: 'json', //返回json
        success: function (data, status) { //成功时,回调
            alert(JSON.stringify(data) + " - " + status);
        },
        error: function (data, status) { 
            alert(JSON.stringify(data) + " - " + status);
        }
    });
}
```

#Web

## jsp

> jsp中的java

```jsp
<% Date date = new Date(); out.print(date); %> <!--嵌入jsp的Java代码段-->
<%= date %> <!-- 脚本表达式.(上述的简化版)->
```

> JSTL：jsp标准标签库。jsp标签集合，封装了jsp应用的通用核心功能。

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
每一次请求，都要重新建立一次连接，都会调用一次 service() 方法。'每次连接只能处理一个请求和响应'。
```

> `Boot の Servlet`

```java
@ServletComponentScan //全局注解; 启动时自动扫描 @WebServlet,并将该类实例化
```

```java
@WebServlet(name = "testServlet", urlPatterns = "/test")
public class TestServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.getWriter().write("自定义 Servlet");
    }
}
```

>`ServletConfig`：封装了Servlet的初始化参数，可获取对象 ServletContext

```java
/**
 * @param name          Servlet别名
 * @param urlPatterns   访问url，可以有多个。其中'/'代表当前web应用的根目录
 * @param loadOnStartup Servlet实例被创建的时机。默认-1
 *                      负数   ：在第一次请求时被创建
 *                      正数或0：在当前应用被Servlet容器加载时创建实例，且数值越小越早被创建
 * @param initParams    Servlet的初始化参数，在init()方法中读取
 */
@WebServlet(name = "testServlet", urlPatterns = {"/test", "/servlet"}, loadOnStartup = -1,
            initParams = @WebInitParam(name = "name", value = "123"))
```

```xml
<servlet>
    <servlet-name>testServlet</servlet-name>
    <servlet-class>com.x.javaweb.TestServlet</servlet-class>
    <init-param> <!--初始化参数. 此节点必须在<load-on-startup>之前-->
        <param-name>name</param-name>
        <param-value>123</param-value>
    </init-param>
    <load-on-startup>-1</load-on-startup>
</servlet>
<servlet-mapping>
    <servlet-name>testServlet</servlet-name>
    <url-pattern>/test</url-pattern>
</servlet-mapping>
```

```java
public void init(ServletConfig config) {
    String user = config.getInitParameter("name"); //获取单个初始化参数. "123"

    Enumeration<String> names = config.getInitParameterNames(); //获取所有
    while (names.hasMoreElements()) {
        String name = names.nextElement();
        String value = config.getInitParameter(name);
    }

    ServletContext servletContext = config.getServletContext(); //获取 ServletContext 对象
}
```

## 监听器

> Listener

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

## 过滤器

> 生命周期 `与Servlet一样`

```sh
init（仅调用一次，单例） --> filter（每次调用） --> destory（仅调用一次）
```

> `Bootの过滤器`

```java
/**
 * @param filterName      Filter名称，首字母小写
 * @param urlPatterns     指定拦截的路径
 * @param servletNames    指定对哪些Servlet进行过滤（与上一个注解互补使用）
 * @param initParams      初始化参数
 */
@Order(1) //@ServletComponentScan 全局注解。order越小，优先级越高
@WebFilter(filterName = "testFilter", urlPatterns = "/servlet/*", servletNames = "helloServlet")
public class TestFilter extends HttpFilter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        chain.doFilter(request, response); //过滤器核心逻辑，每次请求过滤都会调用一次
    }
}
```

## 拦截器

>`Interceptor`：拦截用户的请求并进行相应的处理

```sh
'preHandle'      ：在目标方法之前执行              #适用于权限. 日志, 事务等
'postHandle'     ：在目标方法之后，渲染视图之前执行  #可以对请求域中的属性或视图做出修改
'afterCompletion'：在渲染视图之后执行              #释放资源
```

> 多个拦截器的执行顺序

```sh
#（1）.第二个拦截器的 preHandle()返回 true
interceptor1: preHandle()
interceptor2: preHandle() #return true;
...调用目标方法
interceptor2: postHandle()
interceptor1: postHandle()
...渲染视图
interceptor2: afterCompletion()
interceptor1: afterCompletion()
```

```sh
#（2）.第二个拦截器的 preHandle()返回 false
interceptor1: preHandle()
interceptor2: preHandle() #return false;
...不再调用目标方法
...不再渲染视图
interceptor1: afterCompletion()
```

> `Bootの拦截器`

```java
@Slf4j
@Component
public class MyInterceptor1 implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("preHandle...");
        return true;
    }
}
```

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    MyInterceptor1 myInterceptor1;

    @Autowired
    MyInterceptor2 myInterceptor2;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor1).order(1);
        registry.addInterceptor(loginInterceptor2).order(2); //注册两个拦截器
    }
}
```


## ServletContext

>代表当前WEB应用，可以从中获取到应用的各个方面信息。如，web应用的初始化参数：`IoC容器也是配置在此`

```xml
<context-param> <!-- 配置web应用的初始化参数。与节点 servlet 同级-->
    <param-name>driver</param-name>
    <param-value>com.mysql.jdbc.Driver</param-value>
</context-param>
```

> 从监听器 `ServletContextListener` 中获取web应用的初始化参数

```java
public void contextInitialized(ServletContextEvent sce) {
    ServletContext sc = sce.getServletContext();
    String driver = context.getInitParameter("driver"); //获取单个初始化参数

    Enumeration<String> names = context.getInitParameterNames(); //获取所有
    while (names.hasMoreElements()) {
        String name = names.nextElement();
        String value = context.getInitParameter(name);
    }
}
```
>常用方法

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
## 表单重复提交

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



# 注解

##基础

> 基本概念

```sh
Java代码里的特殊标记，可看作注释（这一点和普通注释没区别）。可被其他程序读取（又区别于注释）。
#元注解：可以注解到注解上的注解，或者说元注解是一种基本注解，但是它能够应用到其它的注解上面。
```

```sh
#@Target：描述注解的使用范围
ElementType.TYPE        --> 可用在：类、接口、枚举
ElementType.METHOD      --> 可用在：方法
ElementType.PARAMETER   --> 可用在：方法的参数
ElementType.FIELD       --> 可用在：类的属性
```

> 内置注解

```sh
@Deprecated          #用来标记过时
@Override            #提示子类要复写父类中被 @Override 修饰的方法
@Test                #测试方法
@FunctionalInterface #一个只有一个方法的普通接口
```

>自定义注解

```sh
#使用 @interface 自定义注解（接口）时，自动继承 java.lang.annotation.Annotation <---> 接口 extends 接口
```

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    String value();
}
```

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String name();
    String type() default "varchar"; 
    int length();
}
```

> 使用自定义注解

```java
@Table("t_student")
public class Student {
    @Column(name = "s_name", /*type = "varchar",*/ length = 10) //有默认值，则可不指定
    private String name;

    @Column(name = "s_age", type = "int", length = 3)
    private int age;
}
```

> 反射读取自定义注解

```java
public void Test() throws Exception {
    Class<?> clazz = Class.forName("com.example.annotation.Student");

    Table table = clazz.getAnnotation(Table.class);
    System.out.println(table.value()); //t_student

    Field[] declaredFields = clazz.getDeclaredFields();
    Arrays.stream(declaredFields).forEach(x -> {
        Column column = x.getAnnotation(Column.class);

        // name: s_name,varchar,10
        System.out.println(x.getName() + ": " + column.name() + "," + column.type() + "," + column.length());
    });
}
```

## Spring

> @PropertySource：批量读取配置文件

```sh
详见： boot --> 配置相关
```
> @Value：通过注解将常量、配置文件中的值、其他bean的属性值注入到变量中，作为变量的初始值

```java
//配置文件属性注入 ${}
@Value("$(server.port)")
```

```java
//bean属性、系统属性、表达式注入 #{}
@Value("#{device.deviceName}")
String deviceName; // 注入其他Bean属性：lcd-10086

@Value("#{systemProperties['os.name']}")
String osName; // 注入操作系统属性：Windows 7

@Value("#{T(java.lang.Math).random() * 100.0}")
String random; //注入表达式结果：60.969226153296965
```

> @Configuration：定义配置类。被注解的类内部包含有一个或多个被`@Bean`注解的方法

```sh
@Configuration 不可以是'final'类型，不可以是匿名类。嵌套的 @Configuration 必须是静态类
```

> @Bean：只会被Spring调用一次，产生一个id同方法名的对象，对象管理在IOC容器中

```java
@Bean("infoService1") //默认，bean的id和方法名相同，也可通过 name 属性自定义
public TnfoService infoService() {
    return new TnfoServiceImpl();
}
```

> @Scope：Bean作用域的注解

```java
@Scope("singleton") //常用取值范围：singleton, prototype
```

> @ComponentScan：`组件扫描`。扫描加了注解的类，并管理到 IOC 容器中

```sh
#Boot项目: 该注解包含在 @SpringBootApplication，所以只需在启动类中配置此注解即可
#其他项目： 可使用 '注解' 和 'xml' 两种方式配置
```

```java
@Configuration
@ComponentScan(basePackages = "com.x.web") //注解版
public class SpringConfiguration { }
```

```xml
<context:component-scan base-package="com.x.web"/>
```

```java
//排除注解 @Repository 标注的组件
@ComponentScan(value = "com.x.web", useDefaultFilters = true, //true + excludeFilters
               excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
```

```java
//只扫描注解 @Repository 标注的组件
@ComponentScan(value = "com.x.web", useDefaultFilters = false, //false + includeFilters
               includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
```

> @Component：`组件标识`。标识一个受 Spring-IOC 容器管理的组件

```sh
#通用注解     持久层注解      业务逻辑层注解   控制层注解
@Component   @Respository  @Service       @Controller
```

```sh
事实上，Spring 并没有能力识别一个组件到底是不是它所标记的类型，即使将 @Respository 注解用在一个表述层控制器组件上，也不会产生任何错误。
所以 @Respository、@Service、@Controller 这几个注解，仅仅是为了让开发人员自己明确当前的组件扮演的角色。
```

> @Autowired：`组件装配`  利用依赖注入（DI），对 IOC容器中各个组件的依赖关系进行自动装配

```sh
#实现依据
在指定要扫描的包时，<context:component-scan> 元素会自动注册一个 bean 的后置处理器：AutowiredAnnotationBeanPostProcessor 的实例。
该后置处理器可以自动装配标记了 @Autowired、@Resource 或 @Inject 注解的属性。
```

```sh
#（1）.先使用 byType，再使用 byName
首先，会使用 'byType' 的方式进行自动装配，如果能唯一匹配，则装配成功。
如果匹配到多个兼容类型的bean，还会尝试使用 'byName' 的方式进行唯一确定，如果能唯一确定，则装配成功。
如果都不能唯一确定，则装配失败，抛出异常。
```

```java
@Autowired
HelloService helloService; //Controller ---> 匹配到多个兼容类型，则可通过指定 name 的形式装配

@Service
public class HelloServiceImpl0 implements HelloService {}

@Service("helloService") //指定 name
public class HelloServiceImpl1 implements HelloService {}
```

```java
//（2）.如若在 IOC 容器中通过 byType byName 都未找到相匹配的bean，也会抛出异常
@Autowired(required = false) //设置该属性非必须装配
HelloService helloService;
```

```java
//（3）如果匹配到多个兼容类型的bean，也可以使用 @Qualifier 来进一步指定要装配的bean的 id 值
@Autowired
@Qualifier("helloServiceImpl1") //指定id
HelloService helloService;

@Service
public class HelloServiceImpl0 implements HelloService {}

@Service/*("helloService")*/
public class HelloServiceImpl1 implements HelloService {}
```

> 区分 `@Autowired @Resource @Inject`

```sh
@Resource：先 'byName'，再 'byType'
@Inject  ：只 'byType'，需要导入 javax.inject 的包
```

```sh
@Autowired       ：Spring 定义的，使用 Spring 框架，推荐使用
@Resource,@Inject：java 规范，通用性强
```





## MVC

> @RequestMapping：指定映射 URL，可标注类或方法上

```sh
value      #映射url，默认属性
method     #请求方式。RequestMethod.GET/POST/PUT/DELETE

name       #给这个mapping分配一个名称，类似于注释
params     #请求参数必须满足条件，才能进行处理
headers    #同上，不常用
```

```java
//参数必须包含'name'。如有'age'，则不能等于10
@GetMapping(value = "/hello", params = { "name", "age!=10" })
public String hello() { }
```

> @RequestParam：将 `GET POST 请求行/体` 中的 `键值对` 解析为简单类型，不能解析为自定义Bean

```java
@PostMapping("/hello")
public String hello(@RequestParam(value = "id", required = false, defaultValue = "1") Integer id,
                     @RequestParam String name) { }
```

```java
//使用 @RequestParam 接收参数时，请求参数必须携带，不然报错。可使用 required = false 或者 直接不写（不推荐），来避免这种错误。
@PostMapping("/hello")
public String hello(Integer id, @RequestParam(required = false) String name) { }
```

> @RequestBody：将`POST 请求体` 中的 `JSON` 解析为 Bean 或者 Map

```java
@PostMapping("/hello")
public String hello(@RequestBody City city) { }
```

> NULL：什么也不写，可以将 `GET POST 请求行/体` 的 `键值对` 解析为自定义Bean

```java
@GetMapping("/hello")
public String hello(Person person) { } //支持级联解析 Person.Address.Name
```

>@PathVariable：将 URL 中的 `占位符` 映射到方法的入参

```java
@GetMapping("/hello/{name}")
public String hello(@PathVariable("name") String args) { }//括号内 == 占位符
```

> @CookieValue：`RequestHeader/CookieValue` 获取 `请求头/Cookie` 中的参数

```java
@GetMapping("/hello")
public String hello(@RequestHeader("header") String header, @CookieValue("JSESSIONID") String jsessionId) { }
```

>





## Boot

> @SpringBootApplication

```sh
#组合注解：@ComponentScan + @SpringBootConfiguration + @EnableAutoConfiguration
```

>@EnableAutoConfiguration

```sh
作用是让 SpringBoot 根据项目所添加的jar包依赖，来对应用进行自动化配置
如，'spring-boot-starter-web'添加了 Tomcat 和 SpringMVC，所以 AutoConfiguration 将假定你正在开发一个web应用并相应地对Spring进行设置
```





# 概念

## html

> js

```sh
javaScript 用于操作html标签：改变 HTML 内容、属性、样式，显示、隐藏 HTML 元素等
jQuery 是一个js框架，封装了js的属性和方法，并且增强了js的功能，让用户使用起来更加便利。
```

```sh
使用原生js是要处理很多兼容性的问题(注册事件等)，由jQuery封装了底层，就不用处理兼容性问题。
原生js的dom和事件绑定和ajax等操作非常麻烦，jQuery封装以后操作非常方便。
```

> js在页面的位置

```sh
js 作为一种脚本语言可以放在html页面中'任何位置'。但是浏览器解释 html 时是按先后顺序的，所以前面的 js 就先被执行。
比如，进行页面显示初始化的 js 必须放在 <head/> 里面，因为初始化都要求提前进行（如给页面body设置css等）；
而如果是通过事件调用执行的 function() 那么对位置没什么要求的，可以放在 <body/> 标签内。
```

```sh
#yahoo团队的最佳实践：样式在上，脚本在下
当把样式放在 <head> 标签中时，浏览器在渲染页面时就能尽早的知道每个标签的样式，用户就会感觉这个页面加载的很快。
但是如果将样式放在页面的结尾，浏览器在渲染页面时就无法知道每个标签的样式，直到CSS被下载执行后。

另一方面，对于 js 来说，因为它在执行过程中会阻塞页面的渲染，所以我们要把它放在页面的结尾
```

```sh
通常会把 jQuery 代码放到 <head>部分的事件处理方法中。
如果网站包含许多页面，并且希望 jQuery 函数易于维护，那么把 jQuery 函数放到独立的 .js 文件中。
```

```js
<head>
    <script type="text/javascript" src="jquery.js"></script>
	<script type="text/javascript" src="my_jquery_functions.js"></script>
</head>
```
> jQuery选择器

```js
$("p")	    //所有 <p> 元素              元素选择器：document.getElementsByTagName("p");
$(".intro")	//所有 class="intro" 的元素  .class选择器：document.getElementsByClassName("test");
$("#intro")	//id="intro" 的元素          #id选择器：document.getElementById("test");
```

>  jQuery的页面加载完毕事件？

```js
$(function () { //简写前: $(document).ready(function () { ---> 文档就绪函数：为了防止文档在完全加载（就绪）之前运行 jQuery 代码
    //jQuery的方法。在DOM加载完成时运行的代码，如果有多个定义则依次执行
});
```

```js
window.onload = function () {
    //js原生的事件。在页面所有资源加载完后执行，如果有多个定义则只执行最后一个
}
```

```sh
比如：当页面中只有一个 <img/> 标签，当img节点创建完后就会执行 $(function(){}) 中的代码，
当 <img/> 的src指定的图片完全加载完后，才会触发'window.onload'事件。所以，前者的效率更高。
```

> ajax

```sh
ajax 异步的 JavaScript 和 XML。
一种用来改善用户体验的技术，其实质是使用 XMLHttpRequest 对象异步地向服务器发送请求。
服务器返回部分数据，而不是一个完整的页面，以页面无刷新的效果更改页面中的局部内容。
```

>jQuery的ajax和原生js实现ajax有什么关系？

```sh
jQuery中的ajax也是通过原生的js封装的。封装完成后使用起来更加便利，不用考虑底层实现或兼容性等处理。
```

> 简单说一下html5?你对现在的那些新技术有了解?

```sh
Html5 是最新版本的html，是在原来 html4 的基础上增强了一些标签。

Html增加一些像画板、声音、视频、web存储等高级功能。但是 html5 有一个不好的地方，那就是 html5 太强调语义了，导致开发中都不知道要选择那个标签。
在做页面布局是，无论头部、主题、导航等模块都使用div来表示，但是html5的规范，需要使用不同的标签来表示。(header、footer等)
```

>BootStrap 是什么？

```sh
BootStrap 是一个移动设备优先的UI框架。可以不用写任何css、js代码就能实现比较漂亮的有交互性的页面。
程序员对页面的编写是有硬伤的，所有要自己写页面的话就要使用类似于 BootStrap 这样的UI框架。

平时用得很多的：1、模态框 2、表单，表单项 3、布局 4、删格系统
```

## web

> Servlet是什么？

```sh
Servlet 的本质是'一个Java对象（接口）'，这个对象拥有处理 HTTP 请求的方法。常见的有 doGet()，doPost() 等。
Web容器（如tomcat）中包含了多个 Servlet，特定的 HTTP 请求该由哪一个 Servlet 来处理是由Web容器中的 web.xml 来决定的。
```

```sh
'工作原理'：反射 + 回调。Servlet 和客户端的通信采用"请求/响应"模式，基于请求
'主要作用'：将url映射到一个java类的处理方法上。接收请求的数据，并与服务器其他资源进行通信（如数据库）。将处理结果展示到页面，以及处理页面的跳转。
'Servlet容器'：负责处理客户请求。当客户请求到来时，调用某个 Servlet，并把 Servlet 的执行结果返回给客户.
```

> Servlet的生命周期：Servlet容器管理，所有请求公用同一个Servlet对象，`非线程安全`

```sh
#Servlet对象是【单实例】。减小服务端内存开销，快速响应客户端
--> 加载Servlet的class --> 调用构造函数实例化Servlet【1次】 --> 调用Servlet的init()完成初始化【1次】
--> 每一次http请求，都会调用一次service()响应请求【n次】
--> Web容器关闭时，调用destory()释放资源【1次】

Servlet启动时，加载servlet生命周期开始。Servlet被服务器实例化后，容器运行其init()方法，请求到达时运行其service()方法，
service()方法自动派遣运行与请求对应的doXXX方法（doGet，doPost）等，当服务器决定将实例销毁的时候(服务器关闭)调用其destroy方法。
```

```sh
#Servlet容器默认采用 <单实例、多线程> 的方式处理多个请求
同一浏览器多个窗口，同时访问，多线程'顺序'执行。不同浏览器，同时访问，多线程'并行'执行。
```

> Servlet & jsp

```sh
jsp 是 Servlet 技术的扩展，所有的jsp文件都会被翻译为一个继承 HttpServlet 的类。也就是jsp最终也是一个Servlet。这个Servlet对外提供服务。

#Servlet 和 JSP 最主要的不同点在于JSP侧重于视图，Servlet主要用于控制逻辑。
Servlet如果要实现html的功能，必须使用Writer输出对应的html，比较麻烦。
而，JSP的情况是Java和HTML可以组合成一个扩展名为 .jsp 的文件，做界面展示比较方便而嵌入逻辑比较复杂。
```

> jsp 9个内置对象

```sh
'pageContext' 网页的属性是在这里管理 
'request'     用户端请求，此请求会包含来自GET/POST请求的参数 
'session'     与请求有关的会话期 
'application' Servlet正在执行的内容 

'out'         用于页面显示信息，out.println(); 
'response'    网页传回用户端的回应 

config        servlet的构架部件 
page          JSP网页本身 
exception     针对错误网页，未捕捉的例外 

#四大作用域：pageContext < request < session < application （作用域：从小到大）
Jsp传递值：request session application cookie也能传值
```

> 

```sh

```

```sh

```

```java

```

> 

```sh

```

```sh

```

## 细节

> 转发 & 重定向

```sh
`转发`：服务端收到请求，进行一定的处理后，先不进行响应，而是在'服务端内部'转发给其他 Servlet 继续处理。
`重定向`：服务端处理完请求后，响应给浏览器一个 302 状态码和重定向地址，浏览器收到响应后，立即向重定向地址再次发送请求。

(1).转发：浏览器只会发送 '1' 次请求，组件间共享数据。重定向：浏览器发送 2 次请求，不会共享数据
(2).转发：浏览器的地址栏'不会'发送改变。重定向：浏览器的地址栏会发生改变
(3).转发：只能转发到'当前web的资源'。重定向：可以是任意资源，甚至是网络资源
```

```sh
转发  ：'/'代表当前'web应用'的根目录; //http://localhost:8090/demo/
重定向：'/'代表当前'web站点'的根目录. //http://localhost:8090/
```

```java
req.setAttribute(key, value);  //转发前绑定数据,在目标资源取出数据
req.getRequestDispatcher("转发地址").forward(req, resp);

resp.sendRedirect("重定向地址"); //重定向
```

> Cookie

```sh
HTTP 是'一种无状态协议'。WEB 服务器本身无法识别出哪些请求是同一个浏览器发出，浏览器的每一次请求都是孤立的。
所以，WEB 服务器必须采用一种机制来唯一标识一个用户，同时记录该用户的状态。
```

```sh
#Cookie：客户端记录信息确定用户身份
第一次访问，没有Cookie，服务器返回，浏览器保存。一旦浏览器有了 Cookie，以后每次请求都会带上，服务器收到请求后，就可以根据该信息处理请求。

#局限性：
(1).Cookie 作为请求或响应报文发送，无形中增加了网络流量
(2).Cookie 是明文传送的安全性差
(3).各个浏览器对 Cookie 有限制，使用上有局限（单个 Cookie 保存的数据不能超过4K，很多浏览器都限制一个站点最多保存20个 Cookie）
```

```sh
#持久化：设置过期时间，浏览器就会把 Cookie 持久化到磁盘，再次打开浏览器，依然有效，直到过期！
会话Cookie  ---> 是在会话结束时（浏览器关闭）会被删除
持久Cookie  ---> 在到达失效日期时会被删除
数量的上限   ---> 浏览器中的 Cookie 数量达到上限（默认20）
```

```java
Cookie cookie = new Cookie("amqp", "value");
cookie.setMaxAge(30); //过期时间，单位秒。 0：立即删除。 负数：永不删除。 默认-1
cookie.setPath("");   //Cookie生效的路径，默认当前路径及子路径
response.addCookie(cookie);

Cookie[] cookies = request.getCookies(); //遍历读取 Cookie
```

> Session

```sh
#Session：服务端记录信息确定用户身份。
用户登录访问，服务端验证通过后，为该用户生成一个 Session 对象，保存在数据库 或 Redis。
并将 'SessionId' 以 'Cookie' 的形式返回给客户端，客户端保存到本地。用户再次发起请求时，自动携带 Cookie。
服务端收到请求后，通过 SessionId 查找与之对应的 Session 对象，用以区分不同的用户
```

```sh
#Session 销毁
(1).服务器端调用方法 HttpSession.invalidate();
(2).Session 过期。两次请求的时间间隔超过 Session 的最大过期时间（默认 30 分钟），则服务端自动删除 Session 对象。

(0).关闭浏览器，并不意味着 Session 销毁。
当浏览器关闭再打开，浏览器可能丢失之前 Cookie 中的 SessionId，也就找不到服务器端的 Session 对象，所以无法自动登录
```

```sh
`Cookie & Session`
(1).Cookie 数据存放在客户的浏览器上，Session 数据放在服务器上
(2).Cookie 不是很安全，别人可以分析存放在本地的 Cookie 并进行 Cookie 欺骗
(3).单个 Cookie 保存的数据不能超过4K，很多浏览器都限制一个站点最多保存 20 个 Cookie
(4).Session 会在一定时间内保存在服务器上。当访问增多，会比较占用服务器的性能
(5).所以：将重要信息（如登陆）存放为 Session。其他信息如果需要保留，可以放在 Cookie 中，比如购物车
```

```java
request.getSession(true);  //根据 SessionId 查找，有则返回，无则创建。同 request.getSession();
request.getSession(false); //..........................，无则为 null
```

```java
//持久化Session --> 持久化Cookie --> 设置Cookie过期时间
Cookie cookie = new Cookie("JSESSIONID", session.getId()); //将 SessionId 以 Cookie 形式返回
cookie.setMaxAge(90);       //持久化Cookie
response.addCookie(cookie); //将Cookie发送浏览器

//默认保存: C:\Users\BlueCard\AppData\Local\Temp\9121B10A811596BD85A3431BFBE71078B2880509\servlet-sessions
```

```java
//Session 机制也是依赖于 Cookie 来实现的。当浏览器禁用 Cookie，怎么办？
//可以通过 'URL重写' 机制解决这一问题：URL;jsessionid=xxx （将 SessionID 拼接URL后面）
String encodeURL = response.encodeURL(url);
response.sendRedirect(encodeURL);
```

