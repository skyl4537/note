[TOC]

# 基础概念

> 路径前缀 /

```shell
由服务器解析 --> 表示：web应用的根目录 --> http:127.0.0.1:8090/demo/

@GetMapping("/hello")                                  #Serlvet映射的访问路径
req.getRequestDispatcher("/hello").forward(req, res);  #请求转发
```

```shell
由浏览器解析 --> 表示：web站点的根目录 --> http:127.0.0.1:8090/

<a href="/hello.html">测试</a>         #超链接<a>
<form method="post" action="/hello">  #Form表单的action
req.sendRedirect("/hello");           #请求重定向
```





# Servlet

>HTTP请求

```shell
建立连接 --> 发送请求信息 --> 回送响应信息 --> 关闭连接

每一次请求，都要重新建立一次连接，都会调用一次 service() 方法
每次连接只能处理一个请求和响应
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

> Servlet生命周期：Servlet容器管理，`非线程安全`，所有请求公用同一个Servlet对象

```java
@ServletComponentScan //全局注解; 启动时自动扫描 @WebServlet,并将该类实例化
```

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
            initParams = @WebInitParam(name = "user", value = "123"))
public class TestServlet extends HttpServlet {

    //只在第一次请求时调用。创建【单实例】的Servlet对象。减小服务端内存开销，快速响应客户端
    public TestServlet() {
        System.out.println("生命周期 - 构造器");
    }

    /**
     * 只被调用一次。调用构造器方法后立即被调用。用于初始化当前Servlet
     *
     * @param config 封装了Servlet的初始化信息，可获取对象 ServletContext
     */
    @Override
    public void init(ServletConfig config) {
        System.out.println("生命周期 - init()");
    }

    //每次请求都会调用。用于响应客户端请求
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("生命周期 - service()");

        resp.setContentType("text/html;charset=utf-8");
        resp.getWriter().write("自定义 Servlet");
    }

    //只被调用一次。应用被卸载前调用。用于释放Servlet所占用的资源（如数据库连接）
    @Override
    public void destroy() {
        System.out.println("生命周期 - destroy()");
    }
}
```

>ServletConfig：封装了Servlet的初始化信息，可获取对象 ServletContext

```java
@Override
public void init(ServletConfig config) {
    System.out.println("生命周期 - init()");

    String user = config.getInitParameter("user"); //获取单个初始化参数. "123"

    Enumeration<String> names = config.getInitParameterNames(); //获取所有
    while (names.hasMoreElements()) {
        String name = names.nextElement();
        String value = config.getInitParameter(name);
    }
    
    ServletContext servletContext = config.getServletContext(); //获取 ServletContext 对象
}
```

>ServletContext：代表当前WEB应用，可以从中获取到应用的各个方面信息

```java
context.getContextPath(); //server.servlet.context-path=/demo

//F:\sp_project\webpark\src\main\webapp\demo.log
context.getRealPath("abc.log"); //文件在服务器上的绝对路径，而非部署前路径

InputStream in = context.getResourceAsStream("my.properties"); //读取配置
//InputStream in = getClass().getClassLoader().getResourceAsStream("my.properties"); //同上
Properties properties = new Properties();
properties.load(in);
String url = properties.getProperty("info.url");

context.setAttribute("user", "123"); //属性相关的三个方法（设置，获取，移除）
String user = (String) context.getAttribute("user");
context.removeAttribute("user");
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




# AOP

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



