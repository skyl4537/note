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



# HTML

## 常见方法

>  <https://blog.csdn.net/h13783313210/article/details/79832318>
>
> <https://www.jianshu.com/p/7fa6175f1db2>

>**（1）** 文档就绪函数（两种）

```js
$(function () {
    //TODO ---> 区别于：window.onload = function(){}
});

$(document).ready(function () {
    //TODO
});
```

>**（2）** text()    html()    val()

```sh
text() 设置或返回被选元素的文本内容
html() 设置或返回被选元素的内容（innerHTML）。返回只返回第一个匹配元素的内容
val()  设置或返回被选元素的 value 属性。设置所有匹配元素的 value 属性的值。返回只返回第一个匹配元素的 value 属性的值
```

```js
let empName = $("#input_empName_add").val(); //获取指定标签用户输入的 value 值
```



> **（5）** parent()    children()    parents()    find()

```sh
parent()   返回被选元素的直接父元素
parents()  返回被选元素的'所有'祖先元素
children() 返回被选元素的所有直接子元素
find()     返回被选元素的后代元素，根据条件进行过滤
```

```js
$(document).on("click", ".delete_btn", function () {
    let empName = $(this).parents("tr").find("td:eq(2)").text(); //表格中删除单个元素时，找到当前行第二列的 text 内容
    if (confirm("是否删除【" + empName + "】吗？")) { //弹框提示: 是否删除【xxx】吗？
        //TODO
    }
});
```

> **（6）** eq()    siblings()    index()

```sh
eq()       返回带有被选元素的指定索引号的元素。索引号从 0 开头，使用负数将从被选元素的结尾开始计算索引
siblings() 返回被选元素的所有同级元素。同级元素是共享相同父元素的元素
index()    返回指定元素相对于其他指定元素的 index 位置。 #如果未找到元素，返回 -1
```

> **（7）** height()    width()

```sh
height()  设置或返回被选元素的高度
width()   设置或返回被选元素的宽度
```

> **（8）** addClass()    removeClass()    hasClass() 

```sh
addClass()    向被选元素添加一个或多个类名
removeClass() 从被选元素移除一个或多个类
hasClass()    检查被选元素是否包含指定的类名称
```

```js
$("table_test").attr({"border": "1", "cellspacing": "0", "cellpadding": "5"}); //设置多个属性
```

> **（10）** show()    hide()

```sh
show() 显示隐藏的被选元素  #适用于通过 style="display: none" 隐藏（完全消失）的元素。不能用于 style="visibility: hidden"
hide() 隐藏被选元素       #完全消失
```





> **（14）** add()    remove()    append()    appendTo()

```sh
add()    把元素添加到已存在的元素组合中
remove() 移除被选元素，包括所有的文本和子节点。该方法也会移除被选元素的数据和事件
#提示：如需移除元素，但保留数据和事件，请使用 detach() 方法代替
#提示：如只需从被选元素移除内容，请使用 empty() 方法

append() 方法在被选元素的结尾插入指定内容（可以是DOM对象、HTML string、jQuery对象）
#提示：如需在被选元素的开头插入内容，请使用 prepend() 方法

appendTo() 方法在被选元素的结尾插入指定内容（可以是selector、DOM对象、HTML string、元素集合、jQuery对象）
#提示：如需在被选元素的开头插入 HTML 元素，请使用 prependTo() 方法
```





## 方法练习

> js 对象和 jQuery 对象的相互转换

```js
// js ---> jQuery
let v = document.getElementById("v"); //js对象 
let $v = $(v); //jQuery对象 
```

```js
// jQuery ---> js 两种方式
//（1）.jQuery对象是一个数据对象，可以通过[index]的方法，来得到相应的js对象
let $v = $("#v"); //jQuery
let v = $v[0];    //js

//（2）.jQuery本身提供，通过.get(index)方法，得到相应的js对象
let $v = $("#v");  //jQuery
let v = $v.get(0); //js
```

> 标签区别

```sh
#<span> & <div>
多个<span/>的内容显示在同一行。一个<div/>显示一行，多个<div/>显示在不同的行。

#style="display: none" & style="visibility: hidden"
前者'完全消失'。后者只是看不见，但占据的位置还在。
```

> 获取选中的值：checkbox   radio    select 

```html
<div>
    <h5>单选框</h5> <!-- radio：单选框，只有加上'name'才具有互斥效果。表单提交的是'value'值 -->
    <input type="radio" name="sex" value="true" checked="checked"><span>boy</span>
    <input type="radio" name="sex" value='false'><span>girl</span>
</div>
<div>
    <h5>下拉列表</h5>
    <select id="weekday">
        <option>---</option>
        <option value="6" selected="selected">星期六</option>
        <option value="7">星期天</option>
    </select>
</div>
<div>
    <h5>多选框</h5>
    <input type="checkbox" name="hobby" value="rap" checked><span>RAP</span>
    <input type="checkbox" name="hobby" value="篮球"><span>篮球</span>
</div>
```

```js
function get_check_value() {
    // 获取 radio 的 Value
    // let radio = $("input[type='radio']:checked").val();
    // let radio = $("input[name='sex']:checked").val();
    let radio = $("input:radio:checked").val();
    console.log("radio: " + radio);

    // 获取 selector 选中项的 Value
    // let selectorValue = $('select#weekday').find('option:selected').val();
    // let selectorValue = $('select#weekday option:selected').val();
    let selectorValue = $("#weekday").val();
    console.log("selectorValue: " + selectorValue);

    // 获取 selector 选中项的 Text
    // let selectorText = $('select#weekday').find('option:selected').text();
    let selectorText = $('select#weekday option:selected').text();
    // let selectorText = $("#weekday").text(); //不行
    console.log("selectorText: " + selectorText);

    // 获取 checkbox 的 Value
    let checkbox = $("input[name='hobby']:checked");
    for (let i = 0; i < checkbox.length; i++) {
        console.log("checkbox: " + $(checkbox[i]).val());
    }
}
```

> ajax请求

```js
var jsonStr = JSON.stringify(jsonObj); //json -> String
var jsonObj = JSON.parse(jsonStr);     //String -> json
```

```js
$(function () { //简写前: $(document).ready(function () {
    var data = {
        "uid": 1,
        "ipAddress": "地址"
    };
    $.ajax({
        url: "/webpark/fanChang",
        type: "POST",
        data: JSON.stringify(data),      //入参 jsonString
        contentType: 'application/json', //请求参数的格式        
        dataType: "json",                //返回参数的格式 json，可直接解析
        success: function (result) {
            console.log(result.errcode);
        }
    });
});
```

```js
$.get(url,data,success,dataType)); //简化$.ajax()
$.post(url,data,success,dataType);

$.getJSON(url,data,success);   //简化$.get(). 相当于设置 $.get() 中 dataType=”json”
$.getScript(url,data,success); //相当于设置 $.get 中 dataType=”script”
```

```js
$.getJSON('http://192.168.8.7:9091/crud/emp/100', function (data, status) {
    console.log("data: " + JSON.stringify(data) + " status: " + status);
    $("#show_msg").append(data.datas.empName);
})
```
> 动态生成表格 TABLE

```js
function create_table_2_2() {
    let $createTable = $("#create_table");
    $createTable.empty();

    let idTd = $("<th></th>").append("ID");
    let nameTd = $("<th></th>").append("Name");
    let handleTd = $("<th></th>").append("操作");
    let headTr = $("<tr></tr>").append(idTd).append(nameTd).append(handleTd);

    idTd = $("<td></td>").append("111");
    nameTd = $("<td></td>").append("张三");
    handleTd = $("<button></button>").append("删除").attr("id", "btn_del");
    let dataTr = $("<tr></tr>").append(idTd).append(nameTd).append(handleTd);

    $("<table></table>")
        .attr({"id": "mytable", "border": "1", "cellspacing": "0", "cellpadding": "10", "align": "center"})
        .append(headTr).append(dataTr)
        .appendTo($createTable);
}
```

> 动态删除表格行

```js
/**
 * $("#btn_del").click(function () {});
 * 这种写法：删除按钮在创建之前就绑定了click事件，所以肯定绑定不上。解决方案：
 * (1).在创建按钮的时候绑定click事件
 * (2).绑定事件.live()。但是jquery新版没有方法 live()，使用 on() 进行替代
 */
$(document).on('click', '#btn_del', function () {
    let parentTr = $(this).parents("tr");
    let empNam = parentTr.find("td:eq(1)").text(); //序号从 0 开始
    if (confirm("是否删除【" + empNam + "】吗？")) {
        parentTr.remove();
    }
});
```

> 表单提交

```sh
详见: MVC注解
```

> 

```js

```






# Web

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





## MVC

> `@RequestMapping`：指定映射 URL，可标注类或方法上

```sh
value      #映射url，默认属性
method     #请求方式。RequestMethod.GET/POST/PUT/DELETE
params     #请求参数必须满足条件，才能进行处理
```

```java
//参数必须包含'name'。如有'age'，则不能等于10
@GetMapping(value = "/hello", params = { "name", "age!=10" })
public String hello() { }
```

> `@RequestParam`：将 `GET/POST 请求行/体` 中的 `键值对` 解析为简单类型，不能解析为自定义Bean

```js
function to_page(pageNum) {
    $.ajax({
        url: "/crud/emp",
        type: "GET",
        data: "pageNum=" + pageNum, //不传 pageSize，使用默认
        success: function (result) {
            if (200 === result.code) {
                let datas = result.datas;
                build_emps_table(datas); //1、解析并显示员工数据
                build_page_info(datas);  //2、解析并显示分页信息
                build_page_nav(datas);   //3、解析显示分页条数据
            }
        }
    });
}
```

```java
@GetMapping("") //获取列表 GET /crud/emp
public Result listEmpByPage(
    @RequestParam(name = "pageNum", defaultValue = "1", required = false) Integer pageNum, //required 默认 true
    @RequestParam(name = "pageSize", defaultValue = "5", required = false) Integer pageSize) {
    PageHelper.startPage(pageNum, pageSize);
    List<Emp> empList = empMapper.listAll();
    PageInfo<Emp> pageInfo = new PageInfo<>(empList, 5); //5：连续显示的页码个数
    return Result.success(pageInfo); //员工分页
}
```

> `NULL`：什么也不写，可以将 `GET/POST 请求行/体` 的 `键值对` 解析为自定义Bean

```js
$("#btn_form").click(function () {
    let data = form2kv("#form_test");
    $.post('http://192.168.8.7:9091/crud/dept/kv', data, function (data, status) {
        if ('fail' === status) return false;
        console.log("data: " + data)
    })
});

//form表单 --> key-value
function form2kv(e) {
    return $(e).serialize();
}
```

```java
@PostMapping("/kv")
public Result addDept1(Dept dept) {
    int insert = deptMapper.insert(dept);
    return Result.success();
}
```

> `@RequestBody`：将`POST 请求体` 中的 `JSON` 解析为 Bean 或者 Map

```js
$("#btn_form").click(function () {
    let data = form2json("#form_test");
    $.post('http://192.168.8.7:9091/crud/dept/json', data, function (data, status) {
        if ('fail' === status) return false;
        console.log("data: " + data)
    })
});

//form表单 --> json
function form2json(e) {
    let data = {};
    let formData = $(e).serializeArray();
    for (let i in formData) {
        if (!formData.hasOwnProperty(i)) continue;
        data[$(formData[i]).attr('name')] = $(formData[i]).val();
    }
    return JSON.stringify(data);
}
```

```java
@PostMapping("/json")
public Result addDept(@RequestBody Dept dept) {
    int insert = deptMapper.insert(dept);
    return Result.success();
}
```

>`@PathVariable`：将 URL 中的 `占位符` 映射到方法的入参

```sh
#同上
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

## Web

> ### Servlet

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
--> 加载Servlet的class --> 调用构造函数实例化Servlet【1次】
--> 调用Servlet的init()完成初始化【1次】
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
#jsp 是 Servlet 技术的扩展，本质是也是一个Servlet。
Servlet 和 JSP 最主要的不同点在于：JSP侧重于视图，Servlet主要用于控制逻辑。
Servlet如果要实现html的功能，必须使用 writer 输出对应的html，比较麻烦。
而，JSP的情况是Java和HTML可以组合成一个扩展名为 .jsp 的文件，做界面展示比较方便而嵌入逻辑比较复杂。
```

> ### jsp

```sh
#jsp-9个内置对象
'pageContext' PageContext.        页面上下文，即页面环境。代表与一个页面相关的对象和属性
'request'     HttpServletRequest. 可以获取客户端请求相关的数据，如请求头，请求url，请求方式
'session'     HttpSession.        保存用户的会话信息和会话状态 
'application' ServletContext.     代表这个web应用程序相关的对象和属性。用于用户间的数据共享，可以存放全局变量

'out'         用于页面显示信息，out.println(); 
'response'    网页传回用户端的回应 

config        servlet的构架部件 
page          jsp网页本身 
exception     针对错误网页，未捕捉的例外

#jsp-四大作用域：pageContext < request < session < application （作用域：从小到大）
```

>  jsp是怎样转化为html？

```sh
jsp本质就是一个Servlet。jsp页面先被转译成java文件，再编译成.class文件。
客户端请求时，服务端将jsp页面的动态数据解析成以 html 标记的内容，然后将整个页面以字节流的方式输出到客户端。
```

> ###Cookie

```sh
HTTP 是'一种无状态协议'。Web服务器本身无法识别出哪些请求是同一个浏览器发出，浏览器的每一次请求都是孤立的。
所以，Web服务器必须采用一种机制来唯一标识一个用户，同时记录该用户的状态。
```

```sh
#Cookie：在【客户端】保持 HTTP 状态信息的方案
第一次访问，没有Cookie，服务器返回，浏览器保存。一旦浏览器有了 Cookie，以后每次请求都会带上，服务器收到请求后，就可以根据该信息处理请求。

#局限性：
(1).Cookie 作为请求或响应报文发送，无形中增加了网络流量
(2).Cookie 是明文传送的安全性差
(3).各个浏览器对 Cookie 有限制，使用上有局限（单个Cookie保存的数据不能超过 4 K，很多浏览器都限制一个站点最多保存 20 个Cookie）
```

> Cookie 持久化

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

> ###Session

```sh
#Session：在【服务器端】保持 HTTP 状态信息的方案
当服务端要为某个客户端的请求创建 Session 时，首先检查请求中是否包含 Session 标识 SessionId。
如果包含，则说明服务端之前已为该浏览器创建过 Session 对象，根据 SessionId 查找 Session 对象（查找不到，服务端已删除，则新建一个）。
如果未包含，则新建一个 Session 对象，并将 'SessionId' 以 'Cookie' 形式返回给浏览器。
浏览器将 Cookie 保存在本地，再次发送请求时，自动携带该 Cookie
```

```sh
#Session 创建
服务端调用方法 HttpSession session = request.getSession(); 时创建。

#Session 销毁
(1).服务器端调用方法 HttpSession.invalidate();
(2).Session过期。两次请求的时间间隔超过 Session 的最大过期时间（默认 30 分钟），则服务端自动删除 Session 对象。
session.setMaxInactiveInterval(30 * 60); #tomcat配置文件设置 或 代码设置，单位秒

(0).关闭浏览器，并不意味着 Session 销毁。
当浏览器关闭再打开，浏览器可能丢失之前 Cookie 中的 SessionId，也就找不到服务器端的 Session 对象，所以无法自动登录

#怎么实现关闭浏览器时，删除Session？
在页面中添加 onunload 事件，当关闭浏览器时，执行服务器端删除 Session 代码。
优点：退出时，能及时处理。缺点：当用户打开多个页面时，关闭任何一个都可能导致用户的退出。
```

>Cookie & Session

```sh
(1).Cookie 数据存放在客户的浏览器上，Session 数据放在服务器上
(2).Cookie 不是很安全，别人可以分析存放在本地的 Cookie 并进行 Cookie 欺骗
(3).单个 Cookie 保存的数据不能超过4K，很多浏览器都限制一个站点最多保存 20 个 Cookie
(4).Session 会在一定时间内保存在服务器上。当访问增多，会比较占用服务器的性能
(5).所以：将重要信息（如登陆）存放为 Session。其他信息如果需要保留，可以放在 Cookie 中，比如购物车
```

```java
request.getSession(false); //没有SessionId 或 有SessionId但没找到Session对象，均返回 null
request.getSession(true);  //有则返回，无则创建。同 getSession(); ---> 一定能得到一个 HttpSession 对象
```

> Session 持久化

```sh
Session超时：web服务器会将空闲时间(默认 30 分钟)过长的Session对象删除掉，以节省服务器内存空间资源。
#持久化 Session <--> 持久化 Cookie --> 设置Cookie过期时间
默认情况，SessionId 保存在浏览器的内存中，并不持久化到硬盘。所以，再次打开浏览器，SessionId 丢失
```

```java
Cookie cookie = new Cookie("JSESSIONID", session.getId());
cookie.setMaxAge(30);
response.addCookie(cookie); //持久化 Cookie，并发送到浏览器
```

```java
//Session 机制也是依赖于 Cookie 来实现的。当浏览器禁用 Cookie，怎么办？
//可以通过 'URL重写' 机制解决这一问题：URL;jsessionid=xxx （将 SessionID 拼接URL后面）
String encodeURL = response.encodeURL(url);
response.sendRedirect(encodeURL);
```
> 

```sh

```

```sh

```

## 概念

> ###转发 & 重定向

```sh
`转发`：服务端收到请求，进行一定的处理后，先不进行响应，而是在'服务端内部'转发给其他 Servlet 继续处理。
`重定向`：服务端处理完请求后，响应给浏览器一个 302 状态码和重定向地址，浏览器收到响应后，立即向重定向地址再次发送请求。

(1).转发：浏览器只会发送 '1' 次请求，组件间共享数据。重定向：浏览器发送 2 次请求，不会共享数据
(2).转发：浏览器的地址栏'不会'发送改变。重定向：浏览器的地址栏会发生改变
(3).转发：只能转发到'当前web项目的内部资源'。重定向：可以是任意资源，甚至是网络资源
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

> ###表单重复提交

```sh
(1).网络延迟，用户多次点击'submit'按钮
(2).表单提交后，'转发'到目标页面，用户点击'刷新'按钮
(3).提交表单后，点击浏览器的'后退'按钮，回退到表单页面，再次进行'提交'

(0).点击'后退'，'刷新'原表单页面，再'提交' #不属于表单重复提交
```

```js
//场景(1)：用js控制表单只能提交一次（两种方案，推荐-1）
<form method="post" onsubmit="doSubmit()" th:action="@{/user/login}"></form>

//方式-1：只能提交一次
var isCommitted = false;
function doSubmit() {
    if (false === isCommitted) {
        isCommitted = true;
        return true; //返回true，让表单正常提交
    } else {
        return false; //返回false，则表单将不提交
    }
}

//方式2：提交后按钮置为不可用
function doSubmit() {
    var btnSubmit = document.getElementById("submit");
    btnSubmit.disabled = true;
    return true;
}
```

```java
//场景(2)：转发到目标页面，点击【刷新】会一直请求之前的表单。使用重定向解决
resp.sendRedirect("重定向地址");
```

```java
//场景(1-2-3)：利用 Session + token 解决
//<1>.token 由服务器来创建，并交给浏览器，浏览器在向服务器发送请求时需要带着这个 token。
//<2>.服务器处理请求前检查token是否正确，如果正确，则正常处理，处理完移除服务端 token；否则返回一个错误页面
//<3>.服务器所创建的 token 只能使用一次
@GetMapping("/token")
public String token(HttpSession session) {
    session.setAttribute("token", UUID.randomUUID().toString()); //(1).生成token，存Session，并转发前台页面
    return "/mvc/token";
}
```

```html
<!--（2）前台页面使用 hidden 存储 token，表单提交时携带-->
<form id="form-login" method="post" th:action="@{/mvc/login}">
    <input type="hidden" name="token" th:value="${session.token}"/>
    ...
    <button class="btn btn-primary" type="submit">登录</button>
</form>
```

> ### 路径前缀 /

```sh
'由服务器解析'：web应用的根目录 --> http:localhost:8090/demo/
'由浏览器解析'：web站点的根目录 --> http:localhost:8090/
```

```java
//由服务器解析：(1).控制器 (2).服务器重定向
@GetMapping("/hello")
req.getRequestDispatcher("/hello").forward(req, res);
```

```html
<!--由浏览器解析：(1).超链接 (2).form表单提交 (3).服务器转发 -->
<a th:href="@{/hello}">测试</a>
<form method="post" th:action="@{/hello}"/>
req.sendRedirect("/demo/hello");
```
> 

```sh

```

