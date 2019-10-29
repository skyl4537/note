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

> `web.xml` 基本作用

```shell
#用来初始化配置信息：比如 welcome页面、servlet、servlet-mapping、filter、listener、启动加载级别等。
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

> Spring 源码

```java
public class ContextLoaderListener extends ContextLoader implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {
        servletContext sc = event.getServletContext();
        String configLocationParam = sc.getInitParameter(CONFIG_LOCATION_PARAM); //"contextConfigLocation"
        //... 初始化 SpringIoc 容器 ...
    }
}
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

> js（事件驱动）

```sh
用户事件：用户操作；如单击，鼠标移入，鼠标移出等
系统事件：由系统触发的事件；如文档加载完成
```

```javascript
document.getElementById("id");            <==>   $('#id')    //根据 id 查询（单个元素）
document.getElementsByClassName("class"); <==>   $('.class') //根据 class 查询（元素数组）
document.getElementsByTagName("tag");     <==>   $('tag')    //根据 tag 查询（元素数组）

document.getElementsByName("name");       //返回带有指定 name 的对象集合
```

>js在页面的位置

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

> jQuery（js函数库）：通过选取html元素，并对选取的元素执行某些操作

```javascript
//jQuery 选择器
$("#test")  --> document.getElementById("test");          //#id 选择器。  查询id为'test'的【单个元素】
$("p")      --> document.getElementsByTagName("p");       //元素选择器。   查询标签<p>【所有元素】
$(".test")  --> document.getElementsByClassName("test");  //.class选择器。查询class为'test'的【所有元素】
```

```javascript
$(this).hide()       //隐藏当前元素
$("p").hide()        //隐藏所有 <p> 元素.(tag)
$("p.test").hide()   //隐藏所有 class="test" 的 <p> 元素
$("p:first")         //选取第一个 <p> 元素
$("[href]")          //选取带有 href 属性的元素 

$("#test").hide()    //隐藏所有 id="test" 的元素.(#id)
```

> 动态绑定事件

```html
<button id="btn0">动态绑定事件</button>
```

```javascript
$(function () { //简写前: $(document).ready(function () {
    $("#btn0").click(function () {
        alert("点我上王者..." + $(this).attr('id'));
    });
});
```

```sh
#$(function(){}) 与 window.onload
window.onload 方法是在网页中所有的元素(包括元素的所有关联文件)完全加载到浏览器后才执行的。
$(document).ready() 方法可以在DOM载入就绪时就对其进行操纵，并调用执行绑定的函数。
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

## Cookie

> 基本概念

```sh
HTTP是无状态协议，服务器不能记录浏览器的访问状态。也就是说服务器不能区分两次请求是否由同一个客户端发出。

Cookie 实际上就是服务器保存在浏览器上的一段信息，完成会话跟踪的一种机制。
第一次访问，没有Cookie，服务器返回，浏览器保存。一旦浏览器有了 Cookie，以后每次请求都会带上，服务器收到请求后，就可以根据该信息处理请求。

储存空间比较小 4KB
数量限制，每一个域名下最多建 20个
用户可以清除 Cookie，客户端还可以禁用 Cookie
```

>持久化Cookie

```sh
#设置过期时间，浏览器就会把Cookie持久化到磁盘，再次打开浏览器，依然有效，直到过期！

会话Cookie  ---> 是在会话结束时（浏览器关闭）会被删除
持久Cookie  ---> 在到达失效日期时会被删除
数量的上限   ---> 浏览器中的 Cookie 数量达到上限（默认20）
```

>相关属性

```java
cookie.setMaxAge(30);                 //最大时效，单位秒。 0：立即删除. 负数：永不删除, 默认-1
cookie.setPath(req.getContextPath()); //Cookie生效的路径，默认当前路径及子路径
```

##Session

>基本概念

```sh
当程序需要为某客户端的请求创建一个 session 时，服务器首先检查这个客户端的请求里是否已包含了一个 session 标识（SessionId）

如果已包含，则说明之前已为此客户端创建过session，服务器就按照 SessionId 把这个session检索出来使用（检索不到，新建一个）;
如果不包含，则为此客户端新建一个Session，并将与此session相关联的 SessionId 返回给客户端保存。

保存 SsessionId 的方式可以采用Cookie，这样在交互过程中浏览器可以自动的按照规则把这个标识发挥给服务器。
```

> Cookie & Session

```sh
#Cookie 保存在客户端;
用户可删除或禁用 Cookie;
浏览器对 Cookie 的数量限制(20); 
增加客户端与服务端之间的数据传输量; 

#Session 保存在服务端.
传递给用户端一个名为'JSESSIONID'的 Cookie，通过它可以获取到用户信息对象 Session.
```

>Session创建

```sh
#Session 机制也是依赖于 Cookie 来实现的.
当'第一次'访问jsp或Servlet，并且该资源显示指定'需要创建Session'。此时服务器才会创建一个 Session 对象。
Session 创建之后，同时还会自动创建一个名为'JSESSIONID'的 Cookie，返回给客户端。
客户端收到 Cookie 后，存储在浏览器内存中(可持久化到磁盘)。

以后浏览器在发送就会携带这个特殊的 Cookie 对象。服务器通过'JSESSIONID'查找与之对应的Session对象，以区分不同的用户。
```

```java
//显示指定需要创建Session对象
request.getSession(true);  //若存在则返回,否则新建一个Session.（默认为true）
request.getSession(false); //若存在则返回,否则返回null
```

> Session销毁

```sh
(1).服务器关闭或web应用卸载;
(2).request.getSession().invalidate(); #服务端显示销毁

(3).Session过期。浏览器关闭，或闲置一段时间(session-timeout值)没有请求服务器，session会自动销毁。
----这个时间是根据服务器来计算的，而不是客户端。所以在调试程序时。应该修改服务器端时间来测试，而不是客户端。

(4).关闭浏览器，但不意味着Session被销毁。#Session的创建和销毁是在服务器端进行的.
----当浏览器访问服务器就会创建一个 SessionID，浏览器通过这个ID来访问服务器中所存储的Session。
----当浏览器关闭再打开，此时浏览器已丢失之前的 SessionID，也就找不到服务器端的Session对象，所以无法自动登录。

之前的Session对象会在<session-timeout>后自动销毁。
但是，关闭再打开，访问时携带上次的 SessionID，则可以重新找到之前的Session对象。
//http://127.0.0.1:8090/demo/session;JSESSIONID=FAAABD1D2B89791DEB647E74D49A7C3D #URL重写
```

>URL重写

```sh
客户端保存'JSESSIONID'，默认采用 Cookie 实现。
当浏览器禁用 Cookie 时，可通过URL重写实现： URL;jsessionid=xxx (将JSESSIONID拼接URL后面)
```

```java
String encodeURL = response.encodeURL(url); //url重写
response.sendRedirect(encodeURL);
```

> 持久化Session

```java
//持久化Session --> 持久化Cookie --> 设置Cookie过期时间
Cookie cookie = new Cookie("JSESSIONID",session.getId());
cookie.setMaxAge(90);       //持久化Cookie
response.addCookie(cookie); //将Cookie发送浏览器

//默认保存: C:\Users\BlueCard\AppData\Local\Temp\9121B10A811596BD85A3431BFBE71078B2880509\servlet-sessions
```
#Web

## 重定向

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
转化过程：java代码照搬，html + css + 表达式等通过流输出'out.write()'
```

> 9大内置对象：`pageContext < request < session < application （作用域：从小到大）`

```sh
#内置对象名         类型
application       ServletContext
session           HttpSession
request	          HttpServletRequest   #同一个请求
pageContext       PageContext          #用于页面显示信息，out.println();

response          HttpServletResponse  #几乎不用（X）
config            ServletConfig        #Servlet.ServletConfig.（X）
exception         Throwable            #<%@ page isErrorPage="true" %>，才可以使用（X）
page              Object(this)         #jsp对象本身，或编译后的Servlet对象; 实际上就是this.（X）
out               JspWriter            #用于页面显示信息，out.println();
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

每一次请求，都要重新建立一次连接，都会调用一次 service() 方法。'每次连接只能处理一个请求和响应'。
```

> Servlet是什么

```shell
浏览器发送一个HTTP请求，HTTP请求由Servlet容器（如tomcat）分配给特定的Servlet进行处理
Servlet的本质是'一个Java对象'，这个对象拥有一系列的方法来处理HTTP请求。常见的方法有doGet()，doPost()等。
Web容器中包含了多个Servlet，特定的HTTP请求该由哪一个Servlet来处理是由Web容器中的 web.xml 来决定的。
```

```shell
Servlet本质就是 javax.servlet 包下的一个接口，工作原理就是：反射+回调。目前所有的MVC框架的 Controller 基本都是这么个模式。

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

```sh
同一浏览器多个窗口，同时访问，多线程'顺序'执行。不同浏览器，同时访问，多线程'并行'执行。
```

> `BootのServlet`

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



# 注解基础

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

# Spring

## @PropertySource

> 批量读取配置文件

```java
详见： boot --> 配置相关
```
## @Value

> 通过注解将常量、配置文件中的值、其他bean的属性值注入到变量中，作为变量的初始值

```java
//配置文件属性注入 @Value("${}")
@Value("$(server.port)")
```

```java
//bean属性、系统属性、表达式注入 @Value("#{}")
@Value("#{beanInject.another}")                 // 注入其他Bean属性：注入beanInject对象的属性another
@Value("#{systemProperties['os.name']}")        // 注入操作系统属性
@Value("#{T(java.lang.Math).random() * 100.0}") //注入表达式结果
```







## @Configuration

> 定义配置类。被注解的类内部包含有一个或多个被`@Bean`注解的方法

```sh
@Configuration 不可以是'final'类型
@Configuration 不可以是匿名类
嵌套的 @Configuration 必须是静态类
```

## @Bean

> 只会被Spring调用一次，产生一个id为方法名的Bean对象，对象管理在IOC容器中

```java
@Bean("infoService1") //默认，bean的id和方法名相同，也可通过name属性自定义
public TnfoService infoService() {
    return new TnfoServiceImpl();
}
```

##@Scope

> Bean作用域的注解

```java
@Scope("singleton") //常用取值范围：singleton, prototype
```



## @ComponentScan

> `组件扫描`  扫描加了注解的类，并管理到 IOC 容器中

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

> 排除注解 @Repository 标注的组件

```java
@ComponentScan(value = "com.x.web", useDefaultFilters = true, //true + excludeFilters
               excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
```

> 只扫描注解 @Repository 标注的组件

```java
@ComponentScan(value = "com.x.web", useDefaultFilters = false, //false + includeFilters
               includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
```

## @Component

> `组件标识` 标识一个受 Spring IOC 容器管理的组件

```sh
#通用注解     持久层注解      业务逻辑层注解   控制层注解
@Component   @Respository  @Service       @Controller
```

```sh
事实上，Spring 并没有能力识别一个组件到底是不是它所标记的类型，即使将 @Respository 注解用在一个表述层控制器组件上，也不会产生任何错误。
所以 @Respository、@Service、@Controller 这几个注解，仅仅是为了让开发人员自己明确当前的组件扮演的角色。
```

## @Autowired

> `组件装配`  利用依赖注入（DI），对 IOC容器中各个组件的依赖关系进行自动装配

```sh
#实现依据
在指定要扫描的包时，<context:component-scan> 元素会自动注册一个 bean 的后置处理器：AutowiredAnnotationBeanPostProcessor 的实例。
该后置处理器可以自动装配标记了 @Autowired、@Resource 或 @Inject 注解的属性。
```

> （1）先使用 `byType`，再使用 `byName`

```sh
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

>（2）如若在 IOC 容器中通过 `byType byName` 都未找到相匹配的bean，也会抛出异常

```java
@Autowired(required = false) //设置该属性非必须装配
HelloService helloService;
```

>（3）如果匹配到多个兼容类型的bean，也可以使用 `@Qualifier` 来进一步指定要装配的bean的 id 值

```java
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





# MVC

## @RequestMapping

> 指定映射 URL，可标注类或方法上

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

## @RequestParam

> 将 `GET POST 请求行/体` 中的 `键值对` 解析为简单类型，不能解析为自定义Bean

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

##@RequestBody

> 将`POST 请求体` 中的 `JSON` 解析为 Bean 或者 Map

```java
@PostMapping("/hello")
public String hello(@RequestBody City city) { }
```

## NULL

> 什么也不写，可以将 `GET POST 请求行/体` 的 `键值对` 解析为自定义Bean

```java
@GetMapping("/hello")
public String hello(Person person) { } //支持级联解析 Person.Address.Name
```

## @PathVariable

>将 URL 中的 `占位符` 映射到方法的入参

```java
@GetMapping("/hello/{name}")
public String hello(@PathVariable("name") String args) { }//括号内 == 占位符
```

## @CookieValue

> `RequestHeader/CookieValue` 获取 `请求头/Cookie` 中的参数

```java
@GetMapping("/hello")
public String hello(@RequestHeader("header") String header, @CookieValue("JSESSIONID") String jsessionId) { }
```

##1

>





# Boot

## @SpringBootApplication

```sh
#组合注解：@ComponentScan + @SpringBootConfiguration + @EnableAutoConfiguration
```

>EnableAutoConfiguration

```sh
作用是让 SpringBoot 根据项目所添加的jar包依赖，来对应用进行自动化配置
如，spring-boot-starter-web添加了 Tomcat 和 SpringMVC，所以 AutoConfiguration 将假定你正在开发一个web应用并相应地对Spring进行设置
```
