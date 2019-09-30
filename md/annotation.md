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

## PropertySource

> 批量读取配置文件

```java
详见： boot --> 配置相关
```
## Configuration

> 定义配置类。被注解的类内部包含有一个或多个被`@Bean`注解的方法

```sh
@Configuration 不可以是'final'类型
@Configuration 不可以是匿名类
嵌套的 @Configuration 必须是静态类
```

## Bean

> 只会被Spring调用一次，产生一个id为方法名的Bean对象，对象管理在IOC容器中

```java
@Bean("infoService1") //默认，bean的id和方法名相同，也可通过name属性自定义
public TnfoService infoService() {
    return new TnfoServiceImpl();
}
```





## ComponentScan

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

## Component

> `组件标识` 标识一个受 Spring IOC 容器管理的组件

```sh
#通用注解     持久层注解      业务逻辑层注解   控制层注解
@Component   @Respository  @Service       @Controller
```

```sh
事实上，Spring 并没有能力识别一个组件到底是不是它所标记的类型，即使将 @Respository 注解用在一个表述层控制器组件上，也不会产生任何错误，
所以 @Respository、@Service、@Controller 这几个注解，仅仅是为了让开发人员自己明确当前的组件扮演的角色。
```

## Autowired

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

## RequestMapping

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

## RequestParam

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

##RequestBody

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

## PathVariable

>将 URL 中的 `占位符` 映射到方法的入参

```java
@GetMapping("/hello/{name}")
public String hello(@PathVariable("name") String args) { }//括号内 == 占位符
```

## CookieValue

> `RequestHeader/CookieValue` 获取 `请求头/Cookie` 中的参数

```java
@GetMapping("/hello")
public String hello(@RequestHeader("header") String header, @CookieValue("JSESSIONID") String jsessionId) { }
```

##1

>





# Boot

## SpringBootApplication

```sh
#组合注解：@ComponentScan + @SpringBootConfiguration + @EnableAutoConfiguration
```

>EnableAutoConfiguration

```
作用是让 SpringBoot 根据项目所添加的jar包依赖，来对应用进行自动化配置

如，spring-boot-starter-web添加了 Tomcat 和 SpringMVC，所以 AutoConfiguration 将假定你正在开发一个web应用并相应地对Spring进行设置
```



#其他问题

## Spring

>