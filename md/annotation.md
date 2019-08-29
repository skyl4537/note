





#Spring

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

> `@Autowired` 

```sh
#（1）首先，会使用 'byType' 的方式进行自动装配，如果能唯一匹配，则装配成功。
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

```sh
#（2）如若，在 IOC 容器中通过 'byType''byName' 都未找到相匹配的bean，则也会抛出异常。
```

```java
@Autowired(required = false) //设置该属性非必须装配
HelloService helloService;
```

```sh
#（3）如果匹配到多个兼容类型的bean，也可以使用 '@Qualifier' 来进一步指定要装配的bean的 id 值
```

```java
@Autowired
@Qualifier("helloServiceImpl1") //指定id
HelloService helloService;

@Service
public class HelloServiceImpl0 implements HelloService {}

@Service /*("helloService")*/
public class HelloServiceImpl1 implements HelloService {}
```

> `@Autowired @Resource @Inject`

```sh
@Resource：先 'byName'，再 'byType'
@Inject  ：只 'byType'，需要导入 javax.inject 的包

#@Autowired       ： Spring 定义的，使用 Spring 框架，推荐使用。
#@Resource,@Inject： java 规范，通用性强
```



# MVC

## RequestParam

> 将`请求行或请求体`的参数（String）转化为简单类型

```sh
#可以处理 GET POST 的请求行，也可以处理 POST 的请求体
本质是将 Request.getParameter(); 获取的 String 转换为简单类型（由 ConversionService 配置的转换器来完成）
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

##RequestBody

> 将`请求体`的参数（JSON）转化为 Bean 或者 Map

```sh
#@RequestParam 和 @RequestBody 可以相结合使用
本质是用 HandlerAdapter 配置的 HttpMessageConverters 来解析请求体，然后绑定到相应的 bean 上
```

```java
//POST，请求体 ---> JSON

@PostMapping("/param3")
public ResultVO param3(@RequestBody Student student) {
    System.out.println("param3: " + student);
    return ResultVOUtil.success(student);
}
```






