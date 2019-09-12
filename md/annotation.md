

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
@Component  @Respository  @Service       @Controller
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

> 将 `GET POST 请求行/请求体` 中的 `键值对` 解析为简单类型，不能解析为自定义Bean

```java
@PostMapping("/hello")
public String hello0(@RequestParam(value = "id", required = false, defaultValue = "1") Integer id,
                     @RequestParam String name) { }
```

```sh
使用 @RequestParam 接收参数时，请求参数必须携带，不然报错。
#可使用 required = false 或者 直接不写，来避免这种错误。【不推荐】
```

```java
@PostMapping("/hello")
public String hello0(Integer id, @RequestParam(required = false) String name) { }
```

##RequestBody

> 将`POST 请求体` 中的 `JSON` 解析为 Bean 或者 Map

```java
@PostMapping("/hello")
public String hello1(@RequestBody City city) { }
```

## PathVariable

>将 URL 中的 `占位符` 映射到方法的入参

```java
@GetMapping("/hello/{name}")
public String hello(@PathVariable("name") String args) { }//括号内 == 占位符
```

## NULL

> 什么也不写，可以将 `GET POST 请求行/请求体` 的 `键值对` 解析为自定义Bean

```java
@GetMapping("/hello")
public String hello11(Person person) { } //支持级联解析 Person.Address.Name
```

## CookieValue

> `RequestHeader/CookieValue` 获取 `请求头/Cookie` 中的参数

```java
@GetMapping("/hello")
public String hello(@RequestHeader("header") String arg) { }
```

```java
@GetMapping("/hello")
public String hello(@CookieValue("JSESSIONID") String arg) { }
```

##1

>




