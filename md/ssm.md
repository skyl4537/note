



# SSM

##Web


> 初始化`SpringIoC`容器的监听器

```xml
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath:applicationContext.xml</param-value>
</context-param>
<listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
```

>`SpringMVC`核心控制器，拦截所有请求

```xml
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
```

```sh
#可选参数: ①/ ②*.action ③/*
①. 所有地址的访问都要由'前端控制器'进行解析, 静态文件另配不解析
②. 以".action"结尾的访问,由'前端控制器'解析
③. 错误配置; 当转发到jsp页面时,仍由'前端控制器'解析jsp地址,找不到导致报错
```

>字符编码过滤器 `第1个过滤器`

```xml
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
```

>REST过滤器，POST 转换成 PUT DELETE

```xml
<filter>
    <filter-name>HiddenHttpMethodFilter</filter-name>
    <filter-class>org.springframework.web.filter.HiddenHttpMethodFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>HiddenHttpMethodFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```

##Spring

> SpringIoC 组件扫描：`排除@Controller`

```xml
<context:component-scan base-package="com.example.spring">
    <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller" />
</context:component-scan>
```

>配置数据源

```xml
<context:property-placeholder location="classpath:db.properties"/> <!--引用外部属性文件-->
<bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource">
    <property name="driverClass" value="${jdbc.driver}"/>
    <property name="jdbcUrl" value="${jdbc.url}"/>
    <property name="user" value="${jdbc.username}"/>
    <property name="password" value="${jdbc.password}"/>
</bean>
```

>事务管理器 + 注解事务

```xml
<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <property name="dataSource" ref="dataSource"/>
</bean>
<tx:annotation-driven transaction-manager="transactionManager"/>
```

>Spring 整合 Mybatis

```xml
<!-- （1）.SqlSession 对象的创建，管理等  -->
<bean class="org.mybatis.spring.SqlSessionFactoryBean">
    <property name="dataSource" ref="dataSource"/>
    <property name="mapperLocations" value="classpath:mybatis/mapper/*.xml"/> <!-- mapper.xml文件位置 -->
    <property name="configLocation" value="classpath:mybatis-config.xml"/>    <!-- （可选）mybatis配置文件 -->    
    <property name="typeAliasesPackage" value="com.example.spring.beans"/>    <!-- （可选）别名处理 -->
</bean>

<!-- （2）.mapper接口扫描 -->
<mybatis-spring:scan base-package="com.example.spring.**.mapper"/>
```

## MVC

> 组件扫描：`只扫描@Controller`

```xml
<context:component-scan base-package="com.example.spring" use-default-filters="false">
    <context:include-filter type="annotation" expression="org.springframework.stereotype.Controller" />
</context:component-scan>
```

> 视图解析器

```xml
<bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
    <property name="prefix" value="/WEB-INF/views/" />
    <property name="suffix" value=".jsp" />
</bean>
```

>开启 mvc 注解支持：映射动态请求

```xml
<mvc:annotation-driven />
```

```sh
将在 SpringMVC 上下文中定义一个 DefaultServletHttpRequestHandler，
它会对进入 DispatcherServlet 的请求进行筛查，如果发现是没有经过映射的请求，
就将该请求交由 WEB 应用服务器默认的 Servlet 处理，如果是由映射的请求，才由 DispatcherServlet 继续处理
如果web应用服务器的默认的Serlvet的名字不叫"default",则需要通过default-servlet-name来进行指定

配置了 default-serlvet-handler 后，RequestMapping的映射会失效，需要加上 annotation-driven 的配置。
```

```sh
#简化配置
（1）自动注册 DefaultAnootationHandlerMapping，AnotationMethodHandlerAdapter。是 SpringMVC 为 @Controllers 分发请求所必须的
（2）提供：数据绑定，数字和日期的format，@NumberFormat，@DateTimeFormat，xml，json默认读写支持
```

>释放静态资源：将MVC不能处理的请求交给Tomcat

```xml
<mvc:default-servlet-handler />
```

> 相关问题

```sh
#需要在 Spring 配置中整合 SpringMVC 吗？ 还是否需要再加入 SpringIOC 容器 ?
#是否需要在 web.xml 中配置启动 SpringIOC 容器的 ContextLoaderListener ?

（1）需要: 通常情况下, 类似于数据源, 事务, 整合其他框架都是放在 Spring 的配置文件中（而不是放在 SpringMVC 的配置文件中）
实际上放入 Spring 配置文件对应的 IOC 容器中的还有 Service 和 Dao. 
（2）不需要: 都放在 SpringMVC 的配置文件中. 也可以分多个 Spring 的配置文件, 然后使用 import 节点导入其他的配置文件
```

```sh
#若 Spring 的 IOC 容器和 SpringMVC 的 IOC 容器扫描的包有重合的部分, 就会导致有的 bean 会被创建 2 次.

（1）使 Spring 的 IOC 容器扫描的包和 SpringMVC 的 IOC 容器扫描的包没有重合的部分. 
（2）使用 exclude-filter 和 include-filter 子节点来规定只能扫描的注解
```

```sh
#SpringMVC-IOC 容器中的bean可以来引用 Spring-IOC 容器中的 bean，反过来呢? 

反之则不行。Spring-IOC 容器中的 bean 却不能来引用 SpringMVC-IOC 容器中的 bean！
```

## Mybatis

> mybatis-config.xml

```xml
<!-- Spring 整合 MyBatis 后，MyBatis中配置数据源，事务等一些配置都可以迁移到 Spring 的配置中。
     MyBatis配置文件中只需要配置与MyBatis相关的即可 -->
<configuration>
    <settings>
        <setting name="mapUnderscoreToCamelCase" value="true"/> <!-- 映射下划线到驼峰命名 -->
        <setting name="lazyLoadingEnabled" value="true"/>       <!-- 开启延迟加载 -->
        <setting name="aggressiveLazyLoading" value="false"/>   <!-- 配置按需加载-->
    </settings>
</configuration>
```


# Spring

## 概述

> 轻量级框架

```shell
'轻量级'：非侵入式，不需要实现所使用框架的任何接口。这样，就算以后切换框架也不需要修改源码
每一层递进，都是代码重用的结果：基础语法 --> 方法 --> 类 --> jar --> 框架 
```

```shell
#什么是框架
框架是软件的半成品。为解决问题制定的一套整体解决方案，在提供功能基础上进行扩充。框架中不能被封装的代码（变量），需要使用配置文件（xml）。

#类库 & 框架
类库：`提供的类没有封装一定逻辑`。举例：类库就是名言警句，写作文时引入名言警句
框架：`区别与类库，里面有一些约束`。举例：框架是填空题
```

> 基础概念

```sh

```

```shell
Spring是一个'IoC'（DI）和'AOP'容器框架。

'非侵入式'     基于 Spring 开发的应用中的对象可以不依赖于 Spring 的 API
'依赖注入'     DI——Dependency Injection，反转控制（IOC）思想的经典实现
'面向切面编程' Aspect Oriented Programming——AOP
'容器'        Sring 是一个容器，因为它包含并且管理应用对象的生命周期
'组件化'      实现了使用简单的组件配置组合成一个复杂的应用。 在 Spring 中可以使用 XML 和 Java 注解组合这些对象
```

> 设计模式

```sh
代理模式：AOP
单例模式：默认 Bean 为单例
工厂模式：BeanFactory
IOC：依赖倒置 or 依赖注入
MVC：spring web
模版方法模式：JdbcTemplate
```

## IOC

> Inversion of Control 控制反转。反转了资源的获取方向，改由容器主动的将资源推送给需要的组件

```sh
#谁控制谁？控制什么？
传统 JavaSE 程序设计，直接在对象内部通过 new 进行创建对象，是程序主动去创建依赖对象。
而 IoC 是有专门一个容器来创建这些对象，即由 IoC 容器来控制对象的创建。

谁控制谁？当然是 IoC 容器控制了对象；控制什么？那就是主要控制了外部资源获取（不只是对象，还包括比如文件等）。 
```

```sh
#为何是反转，哪些方面反转了？
有反转就有正转，传统应用程序是由我们自己在对象中主动控制去直接获取依赖对象，也就是正转；而反转则是由容器来帮忙创建及注入依赖对象；

为何是反转？因为由容器帮我们查找及注入依赖对象，对象只是被动的接受依赖对象，所以是反转；哪些方面反转了？依赖对象的获取被反转了。
```

```sh
传统应用程序都是由我们在类内部主动创建依赖对象，从而导致类与类之间高耦合，难于测试；
有了IoC容器后，把创建和查找依赖对象的控制权交给了容器，由容器进行注入组合对象，
所以对象与对象之间是 松散耦合，这样也方便测试，利于功能复用，更重要的是使得程序的整个体系结构变得非常灵活。
```

```sh
#传统的资源查找方式是：直接通过 new 进行创建对象，是程序主动去创建依赖对象
开发人员往往需要知道在具体容器中特定资源的获取方式，增加了学习成本，同时降低了开发效率。

#IOC（反转控制）的查找方式：反转了资源的获取方向，改由容器主动的将资源推送给需要的组件
开发人员不需要知道容器是如何创建资源对象的，只需要提供接收资源的方式即可，极大的降低了学习成本，提高了开发的效率
```

```sh
# DI（Dependency Injection）依赖注入
组件之间依赖关系由容器在运行期决定，形象的说，由容器动态的将某个依赖关系注入到组件之中。
依赖注入的目的并非为软件系统带来更多功能，而是为了提升组件重用的频率，并为系统搭建一个灵活、可扩展的平台。
通过依赖注入机制，我们只需要通过简单的配置，而无需任何代码就可指定目标需要的资源，完成自身的业务逻辑，而不需要关心具体的资源来自何处，由谁实现。

#IOC 描述的是一种思想，而 DI 是对 IOC 思想的具体实现。
相对 IoC 而言，'依赖注入'明确描述了'被注入对象依赖IoC容器配置依赖对象'。
```

> 常见概念

```sh
'BeanFactory'：IOC 容器的基本实现，是 Spring 内部的基础设施，是面向Spring 本身的，不是提供给开发人员使用的

'ApplicationContext'：BeanFactory 的子接口，提供了更多高级特性。面向 Spring 的使用者，几乎所有场合都使用
```

```sh
#ApplicationContext 的主要实现类
'ClassPathXmlApplicationContext'：对应类路径下的 XML 格式的配置文件
'FileSystemXmlApplicationContext'：对应文件系统中的 XML 格式的配置文件
```

```sh
#ConfigurableApplicationContext
ApplicationContext 的子接口，包含一些扩展方法
refresh() 和 close() 让 ApplicationContext 具有启动、关闭和刷新上下文的能力
```

```sh
#WebApplicationContext
专门为 WEB 应用而准备的，它允许从相对于 WEB 根目录的路径中完成初始化工作
```

> `IOC容器创建`：详见 SSM-web

> `依赖注入`の通过Bean的属性赋值

```xml
<bean id="people" class="com.x.pojo.People">
    <property name="id" value="123"/>
    <property name="car" ref="car"/> <!--ref: 引用其他bean-->
</bean>
```

```xml
<bean id="people" class="com.x.pojo.People" p:id="123" p:car-ref="car"/> <!--引入 p 命名空间-->
```

>`依赖注入`の通过Bean的构造器赋值

```xml
<bean id="person" class="com.x.pojo.Person"/> <!--无参构造-->
```

```xml
<bean id="people" class="com.x.pojo.People"> <!--有参构造-->
    <constructor-arg name="id" value="123"/>
    <constructor-arg name="car" ref="car"/>
</bean>
```

```xml
<bean id="people" class="com.x.pojo.People" c:id="123" c:car-ref="car"/> <!--引入 c 命名空间-->
```

> `bean的高级配置`の继承

```sh
#Spring 允许继承 bean 的配置，被继承的 bean 称为父 bean。继承的 Bean 称为子 Bean。
(0).子 Bean 可以 '继承并覆盖' 父 Bean 中的配置（属性 autowire，abstract 除外）
(1).若父 Bean 只作为模板，可以设置 abstract=true，这样 Spring 将不会实例化这个 Bean
(2).父 Bean 可不配置 class 属性（必须 abstract=true），让子 Bean 自己指定类，只继承父 Bean 其他的属性
```

```xml
<bean id="people" p:id="123" p:name="wang" abstract="true"/>
<bean id="chinese" class="com.x.pojo.Chinese" parent="people"/> <!--p:id="123" p:name="wang"-->
```

>`bean的高级配置`の依赖

```sh
#依赖关系不等于引用关系。
people 依赖 car，即必须先创建 car 才能创建 people，但 people 不一定要引用 car
```

```xml
<bean id="car" class="com.x.pojo.Car" p:brand="Audi" p:price="720000"/>
<bean id="people" class="com.x.pojo.people" p:id="123" p:name="wang" depends-on="car"/> <!--前置依赖-->
```

> FactoryBean

```sh
#Spring 中有两种类型的 bean： 一种是普通 bean，另一种是工厂 bean，即 FactoryBean。
工厂 bean 跟普通 bean 不同，其返回的对象不是指定类的一个实例，其返回的是该工厂 bean 的 getObject 方法所返回的对象。
工厂 bean 必须实现 org.springframework.beans.factory.FactoryBean 接口。

#其中，mybatis 的 SqlSessionFactoryBean 就是工厂 Bean。
```

```java
public interface FactoryBean<T> {
    T getObject() throws Exception; //将创建好的 bean 返回给 IOC 容器

    Class<?> getObjectType(); //返回 bean 类型

    default boolean isSingleton() { //创建的 bean 是否单例
        return true;
    }
}
```

> 引用外部属性文件

```xml
<context:property-placeholder location="classpath:db.properties"/>
```

> bean的作用域

```sh
'singleton': 默认值，唯一实例。只在IoC容器初始化时创建一次，以后每次获取都是从容器中直接拿
'prototype': 原型的，多实例的。IoC容器初始化时并不会创建，而是在每次调用时重新创建一个新的对象
```

```java
@Scope("prototype") //使用注解，定义作用域
```

```xml
<bean id="car" class="com.x.pojo.Car" scope="prototype" p:brand="Audi" p:price="720000"/>
```

>bean的生命周期

```sh
#Spring IOC 容器可以管理 bean 的生命周期，Spring 允许在 bean 生命周期内特定的时间点执行指定的任务。
```

```sh
通过构造器或工厂方法'创建 Bean 实例'      #constuctor...
为 Bean 的'属性赋值'和对其他 Bean 的引用  #setter...
将 Bean 实例传递给 Bean 后置处理器的 postProcessBeforeInitialization() 方法
调用 Bean 的'初始化'方法                #init...
将 Bean 实例传递给 Bean 后置处理器的 postProcessAfterInitialization() 方法
Bean 此时可以使用了                    #Car [brand=Audi, price=720000.0]
当容器关闭时，调用 Bean 的'销毁方法'      #destroy...
```

```sh
#bean 的后置处理器
（1）bean 后置处理器允许在调用 '初始化方法 前+后' 对 bean 进行额外的处理
（2）bean 后置处理器对 IOC 容器里的所有 bean 实例逐一处理，而非单一实例。其典型应用是：检查 bean 属性的正确性或根据特定的标准更改 bean 的属性。
（3）bean 后置处理器时需要实现接口 BeanPostProcessor
```

```xml
//首先，bean 中必须定义 init(); destroy(); 方法
//其次，实现bean后置处理器: implements BeanPostProcessor
<bean id="car" class="com.example.bean.Car" init-method="init"
      destroy-method="destroy" p:brand="Audi" p:price="720000" /> //配置init(),destroy()方法
<bean class="com.x.config.myBeanPostProcessor" /> //配置bean后置处理器  
```

> `自动装配` & 手动装配

```sh
手动装配： 以 value 或 ref 的方式，'明确指定属性值'都是手动装配。
自动装配： 根据指定的装配规则，'不需要明确指定'，Spring '自动'将匹配的属性值'注入' bean 中。
```

> `自动装配`の两种模式

```sh
#ByType： 将类型匹配的 bean 作为属性注入到另一个 bean 中。
若 IOC 容器中有多个与目标 bean 类型一致的 bean，Spring 将无法判定哪个 bean 最合适该属性，所以不能执行自动装配
```

```xml
<bean id="car0" class="com.x.pojo.Car" p:brand="Audi"/>
<bean id="car1" class="com.x.pojo.Car" p:brand="Bens"/> <!-- 多个相匹配的 类型，自动装配失败-->

<bean id="people" class="com.x.pojo.People" p:name="wang" autowire="byType" /> <!-- p:car-ref="car" -->
```

```sh
#ByName： 必须将目标 bean 的名称（Car的id）和属性名（People的属性名）设置的完全相同
```

```xml
<bean id="car" class="com.x.pojo.Car" p:brand="Audi"/> <!--若 id 换成 'car1' 则不能自动装配-->
<bean id="people" class="com.x.pojo.People" p:name="wang" autowire="byName" />
```

> `自动装配`の使用建议

```sh
#相对于使用注解的方式实现的自动装配，在 XML 文档中进行的自动装配略显笨拙，在项目中更多的使用注解的方式实现。

(1).属性 autowire 作用于 Bean 的所有属性。所以，希望只自动装配个别属性时，不能实现
(2).属性 autowire 要么 byType，要么 byName, 不能两者兼得
(3).所以，实际项目中很少使用自动装配功能，明确清晰的配置文档更有说服力
```


## AOP

> AOP（Aspect-Oriented-Programing）面向切面编程

```shell
通过'动态代理'实现程序功能的统一维护
通过对既有程序定义一个切入点，然后在切入点前后切入不同的执行内容。如：打开/关闭数据库连接，打开/关闭事务，记录日志等

#基于AOP不会破坏原来程序逻辑。
因此，它可以很好的对业务逻辑的各个部分进行隔离，从而使得业务逻辑各部分之间的耦合度降低，提高程序的可重用性，同时提高了开发的效率
```

```sh
#Spring创建代理的规则为：
默认，使用'Java动态代理'来创建AOP代理，这样就可以为任何接口实例创建代理了
当需要代理的类不是代理接口的时候，Spring会切换为使用'CGLIB动态代理'，也可强制使用CGLIB
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



# MVC

## 数据校验

> 常见校验

```java
/** 
 * @Null                   必须为 null
 * @NotNull                必须不为 null
 * @AssertTrue             必须为 true
 * @AssertFalse            必须为 false
 *
 * @Min(value)             必须是数字，其值必须 >= 指定的最小值
 * @Max(value)             必须是数字，其值必须 <= 指定的最大值
 * @DecimalMin(value)      必须是数字，其值必须 >= 指定的最小值
 * @DecimalMax(value)      必须是数字，其值必须 <= 指定的最大值
 * @Size(max=, min=)       大小必须在指定的范围内
 *
 * @Digits(integer, fraction) 必须是数字,其值必须在可接受的范围内。
 *                                (integer->指定整数部分的数字位数; fraction->指定小数部分的数字位数)
 *
 * @Past                     必须是一个过去的日期
 * @Future                   必须是一个将来的日期
 *
 * @Pattern(regex=,flag=)    必须符合指定的正则表达式
 *                             regex->正则表达式；flag->指定 Pattern.Flag 的数组，表示正则表达式的相关选项
 */
```

```java
/**
 * @NotBlank(message=)           字符串非null,非空. (去掉首尾空格)
 * @NotEmpty                     字符串必须非空. (不会去掉...)
 * @Length(min=,max=)            字符串长度必须在指定的范围内
 * @Range(min=,max=,message=)    数值必须在合适的范围内 
 *
 * @Email                        必须是电子邮箱地址
 * @URL(protocol=,host=,port=,regexp=,flags=)    合法的url
 */

```

> 在实体类中添加校验规则

```java
public class Car {
    @NotBlank(message = "车标不能为空")
    @Length(min = 3, max = 10, message = "车标长度必须在3-10之间")
    @Pattern(regexp = "^[a-zA-Z_]\\w{2,9}$", message = "车标必须以字母下划线开头")
    public String brand;

    @Range(max = 1000000, min = 0, message = "价格必须在0-10000之间")
    @NumberFormat(pattern = "####.##")
    public Double price;

    @Past(message = "日期不能晚于当前时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public Date birth;
}
```

>也可将校验信息抽取到配置文件

```properties
#在'resources'目录下新建配置文件 ValidationMessages.properties
car.brand.length=车标长度必须在{min}-{max}之间
```

```java
@Length(min = 3, max = 10, message = "{car.brand.length}") //对应属性的注解设置
public String brand;
```

>控制器中开启校验

```java
//注意: @Valid 和 BindingResult 两个入参必须紧挨着，中间不能插入其他入参
@PostMapping("/car")
public String car(@Valid @RequestParam Car car, BindingResult result) {
    if (result.hasErrors()) { //校验有错
        result.getAllErrors().forEach(x -> log.info(x.getDefaultMessage()));//错误信息
        return "car";
    }
    return "success";
}
```

> 前台页面回显校验错误

```html
<!--使用 thymeleaf 的内置标签 th:errors-->
<!--第一次跳转该页面时，没有局部变量 car，所以应该判断: th:if="${null!=car}"-->
<form th:action="@{/save}" method="post">
    车标：<input type="text" name="brand"/> 
    <font color="red" th:if="${null!=car}" th:errors="${car.name}"></font><br/>
    <input type="submit" value="添加"/>
</form>
```



## 国际化

>两种方式

```sh
（1）页面根据'浏览器的语言设置'对文本（不是内容），时间，数值等自动进行本地化处理
（2）页面可以通过'超链接'切换，而不再依赖于浏览器的语言设置
```

> 资源文件

```sh
目录'/resources/i18n/'下新建三个属性文件：login; login_zh_CN; login_en_US
分别对应: ①默认配置; ②中文环境; ③英文环境
分别编辑: login.user=用户名; login.user=用户名; login.user=user
```

> 配置文件

```xml
<bean id="messageSource" class="org.springframework.context.support.ResourceBundleMessageSource">
    <property name="basename" value="i18n.login"/>
</bean>
```

```properties
#SpringBoot 默认已配置 ResourceBundleMessageSource，即只需配置 basename。
#此属性默认值为 message。所以，在 /resources 下新建 message 资源文件，则可以省去此配置
spring.messages.basename=i18n.login 
```

> 前台页面

```html
<span th:text="#{login.user}"/> <!--thymeleaf页面使用标签： #{} -->
```

```jsp
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%> <!--jsp页面使用标签： fmt -->
<fmt:bundle basename="i18n.login"> <!--可省，由basename指定-->
    <fmt:message key="login.user"/>
</fmt:bundle>
```

> 点击链接切换

```sh
到此，已经可以实现方式(1)，对于方式(2)，还需以下两步配置。#注意: 实现方式(2)，则方式(1)不起作用
```

> 页面配置超链接

```html
<a th:href="@{/?l=zh_CN}">中文</a>
<a th:href="@{/?l=en_US}">英文</a>
<span th:text="#{login.user}"/>
```

>后台注册自定义国际化配置

```java
@Bean
public LocaleResolver localeResolver() { //注册自定义国际化配置
    return new LocaleResolver() {
        @Override
        public Locale resolveLocale(HttpServletRequest request) {
            Locale locale = Locale.getDefault(); //默认
            String parameter = request.getParameter("l"); //获取自定义
            if (!StringUtils.isEmpty(parameter)) {
                String[] split = parameter.split("_"); //zh_CN
                locale = new Locale(split[0], split[1]);
            }
            return locale;

            @Override
            public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {}
        }
    };
}
```






# 常见问题

## Spring

> xml解析

```sh
#xml解析：DOM SAX
DOM：解析时先'将整个文档加载到内存'。占用内存大，解析大型文件时性能有所下降。适合对xml进行'随机访问'
SAX：顺序读取xml文件，'不需要一次性加载整个文件'。属于事件驱动型解析，当遇到文档开头/结束，标签开头/结束时，都会触发一个事件，
用户只需要在事件对应的回调函数中写入响应的处理逻辑即可。适合对xml进行'顺序访问'
```

> 简述Spring

```sh
Spring 是J2EE应用程序框架，是轻量级的 IoC 和 AOP 的'容器框架'(相对于重量级的EJB)。
主要是针对javaBean的生命周期进行管理的轻量级容器，可以单独使用，也可以和 mybatis框架，SpringMVC框架等组合使用。
```

```sh
#IOC（Inversion of Control）控制权反转
'以前'：Service需要调用DAO，Service就需要创建DAO
'现在'：Spring发现Service依赖于dao，就给你注入。
'核心原理'：就是配置文件 + 反射（工厂也可以） + 容器(map) 
```

```sh
#AOP：面向切面编程
'核心原理'：使用动态代理的设计模式，在执行方法前后或出现异常处加入相关逻辑。使用场景：
声明式事务 --> 执行方法前开启事务，执行完成后关闭事务，出现异常后回滚事务
权限判断   --> 在执行方法前，判断是否具有权限
日志      --> 在执行前进行日志处理
```

> Spring 的 bean 有哪些作用域

```sh
'singleton': 默认值，唯一实例。只在IoC容器初始化时创建一次，以后每次获取都是从容器中直接获取
'prototype': 原型的，多实例的。IoC容器初始化时并不会创建，而是在每次调用时重新创建一个新的对象

'request'：每次http请求都会产生一个新的实例
'session'：每次会话创建一个实例
'global'：全局的 httpSession中，容器会返回该bean的同一个实例
```

> 拦截器的作用？ 事务拦截器的实现原理？

```sh
拦截器适合封装一些共通处理，便于重复利用。例如日志的记录，权限检查，事务处理等。拦截器是通过配置方式调用，隐藏使用方法比较灵活，便于维护和扩展。

Spring 中的事务管理是通过 AOP 代理来实现的，对被代理对象的每个方法进行拦截，在方法执行前启动事务，方法执行后根据'是否有异常'和'异常的种类'进行提交或回滚。
```

> 事务的传播行为

```sh
#事务传播行为，是指事务方法A 调用 事务方法B，n那么，A的事务该如何传播？
Required      如果存在一个事务，则支持当前事务。如果没有事务则开启。`需要`
Required_New  总是开启一个新的事务。如果已有一个事务，则将这个存在的事务挂起。`需要新的`

Supports      如果存在一个事务，支持当前事务。如果没有事务，则非事务的执行。`支持，有则有，无则无，佛系`
Not_Supports  总是非事务地执行，并挂起任何存在的事务。`不支持`

Mandatory     如果已经存在一个事务，支持当前事务。如果没有一个活动的事务，则抛出异常。`强制，必须有，没有则异常`
Never         总是非事务地执行，如果存在一个活动事务，则抛出异常。`不能有，有则抛异常`

Nested：      如果有就嵌套、没有就开启事务。`嵌套`
```

> 事务的隔离级别

```sh
#并发事务可能导致的问题
脏读     ：T1改未提   T2读     T1回滚   #写的过程，被人插队
不可重复读：T1读      T2改并提  T1再读   #读的过程，被人插队
幻读     ：T1修改全部 T2新增    T1再读，发现还有未修改

`脏读`     ：一个事务读取到另一个事务未提交的更新数据
`不可重复读`：在同一事务中，多次读取同一数据返回的结果有所不同
`幻读`     ：一个事务读取到另一个事务已提交的 insert 数据
```

```sh
#mysql默认是【可重复读】，Spring默认是【读已提交】
`读未提交`：这是事务最低的隔离级别，它允许另外一个事务可以读取本事务尚未提交的数据。'不能解决'：脏读，不可重复读和幻读
`读已提交`：保证一个事务修改的数据提交后才能被另外一个事务读取。另外一个事务不能读取该事务未提交的数据。'不能解决'：不可重复读和幻读
`可重复读`：读取数据的事务在读取过程中锁定数据，禁止其他事务修改，但可以读取。'不能解决'：幻读
`串行化`：读取数据的事务在读取过程中锁定数据，进制其他事务新增。要求事务只能一个接着一个地执行，不能并发执行。'能解决所有，但效率低'
```

```sh
##mysql默认是【可重复读】，Spring默认是【读已提交】
为什么要有事务的隔离级别？这是因为并发事务容易产生一些问题，事务的隔离级别就是解决这些问题产生的。
首先，最基础的事务级别是：'读未提交'，也就是说事务 T2 可以读取事务 T1 已修改但未提交的数据，这将导致如果 T1 回滚则 T2 读取到的就是无效数据，
即所谓的'脏读'。为了解决这一问题，而提出了'读已提交'，要求：一个事务只能读取其他事务已提交的数据。

但是，在事务 T2 读取数据的过程中，数据被事务 T1 更改了，将导致事务 T2 两次读取数据不一致现象，即'不可重复读'。
为了解决这一问题，提出了更严格的隔离级别'可重复读'，要求：事务读取数据过程中，锁定数据，禁止其他事务修改，只能读取。

但这也不是最完美的，比如事务 T1 要把数据表中所有行的某一列改为 3，在 T1 执行过程中，事务 T2 向数据表中又新加了 1 行数据，
事务 T1 执行完发现，并没有把所有行都改为 3，这就是所谓的'幻读'。为此，提出了最为严格的'串行化'。
串行化要求：事务执行过程中，锁定数据，不允许其他事务增删改。事务只能一个一个串行化执行，不能并发执行，大大降低效率。
```





## 概念区分

> 路径前缀 /

```sh
'由服务器解析'：web应用的根目录 --> http:localhost:8090/demo/
'由浏览器解析'：web站点的根目录 --> http:localhost:8090/
```

```shell
@GetMapping("/hello")                                 #由服务器解析
req.getRequestDispatcher("/hello").forward(req, res);
```

```shell
<a th:href="@{/hello}">测试</a>                        #由浏览器解析
<form method="post" th:action="@{/hello}">
req.sendRedirect("/demo/hello");
```
> 过滤器 & 拦截器 & AOP

```sh
#过滤器
'过滤器拦截web访问url地址'。严格意义上讲，filter只是适用于web中，依赖于Servlet容器，利用'Java的回调机制'进行实现。
Filter过滤器：和框架无关，可以控制最初的http请求，但是更细一点的类和方法控制不了。
过滤器可以拦截到方法的请求和响应(ServletRequest, ServletResponse)，并对请求响应做出像响应的过滤操作，
比如设置字符编码，鉴权操作等
```

```sh
#拦截器
'拦截器拦截以 .action结尾的url，拦截Action的访问'。Interfactor是基于'Java的反射机制（APO思想）'进行实现，不依赖Servlet容器。
拦截器可以在方法执行之前(preHandle)和方法执行之后(afterCompletion)进行操作，回调操作(postHandle)，可以获取执行的方法的名称，请求(HttpServletRequest)
Interceptor：可以控制请求的控制器和方法，但控制不了请求方法里的参数(只能获取参数的名称，不能获取到参数的值)
用于处理页面提交的请求响应并进行处理（例如做国际化，做主题更换，过滤等）。
```

```sh
#AOP
只能拦截Spring管理Bean的访问（业务层Service）。具体AOP详情参照 Spring-AOP：原理、通知、连接点、切点、切面、表达式
实际开发中，AOP常和事务结合：Spring的事务管理 ---> 声明式事务管理（切面）
AOP操作可以对操作进行横向的拦截，最大的优势在于他可以获取执行方法的参数( ProceedingJoinPoint.getArgs() )，对方法进行统一的处理。
Aspect: 可以自定义切入的点，有方法的参数，但是拿不到http请求，可以通过其他方式如RequestContextHolder获得
常见使用日志，事务，请求参数安全验证等
```

```sh
#拦截器 VS 过滤器
拦截器是基于java的反射机制，使用代理模式，而过滤器是基于函数回调。
拦截器不依赖servlet容器，依赖于Spring容器；过滤器依赖于servlet容器。
拦截器只能对action起作用，而过滤器可以对几乎所有的请求起作用（可以保护资源）。
拦截器可以访问action上下文，堆栈里面的对象，而过滤器不可以。
```

```sh
#执行顺序：过滤前-拦截前-Action处理-拦截后-过滤后。
拦截器相比过滤器有更细粒度的控制，依赖于Spring容器，可以在请求之前或之后启动，过滤器主要依赖于servlet，过滤器能做的，拦截器基本上都能做。
```

## MVC

![](assets/mvc-0.png)

>MVC的工作流程

```sh
1. 用户向服务器发送请求，请求被Spring 前端控制Servelt DispatcherServlet捕获；
2. DispatcherServlet对请求URL进行解析，得到请求资源标识符（URI）。然后根据该URI，调用HandlerMapping获得该Handler配置的所有相关的对象（包括Handler对象以及Handler对象对应的拦截器），最后以HandlerExecutionChain对象的形式返回；
3. DispatcherServlet 根据获得的Handler，选择一个合适的HandlerAdapter。（附注：如果成功获得HandlerAdapter后，此时将开始执行拦截器的preHandler(...)方法）
4.  提取Request中的模型数据，填充Handler入参，开始执行Handler（Controller)。 在填充Handler的入参过程中，根据你的配置，Spring将帮你做一些额外的工作：
HttpMessageConveter： 将请求消息（如Json、xml等数据）转换成一个对象，将对象转换为指定的响应信息
数据转换：对请求消息进行数据转换。如String转换成Integer、Double等
数据根式化：对请求消息进行数据格式化。 如将字符串转换成格式化数字或格式化日期等
数据验证： 验证数据的有效性（长度、格式等），验证结果存储到BindingResult或Error中
5.  Handler执行完成后，向DispatcherServlet 返回一个ModelAndView对象；
6.  根据返回的ModelAndView，选择一个适合的ViewResolver（必须是已经注册到Spring容器中的ViewResolver)返回给DispatcherServlet ；
7. ViewResolver 结合Model和View，来渲染视图
8. 将渲染结果返回给客户端。
```

```sh
1、用户向服务器发送请求，请求被 Spring '前端控制器' DispatcherServlet 捕获

2、DispatcherServlet 对请求URL进行解析，得到请求资源标识符（URI）。
然后根据该URI，通过 '处理器映射器' HandlerMapping 获得该Handler配置的所有相关的对象（包括Handler对象以及Handler对象对应的拦截器），最后以HandlerExecutionChain对象的形式返回；#查找handler

3、DispatcherServlet 根据获得的Handler，选择一个合适的 '处理器适配器' HandlerAdapter。
提取Request中的模型数据，填充Handler入参，开始执行Handler（Controller)。
Handler执行完成后，向DispatcherServlet 返回一个ModelAndView对象。#执行handler

4、DispatcherServlet 根据返回的ModelAndView，选择一个适合的ViewResolver（必须是已经注册到Spring容器中的ViewResolver) #选择ViewResolver

5、通过ViewResolver 结合Model和View，来渲染视图，DispatcherServlet 将渲染结果返回给客户端。#渲染返回
```

```sh
#快速记忆技巧：核心控制器捕获请求、查找Handler、执行Handler、选择ViewResolver，通过ViewResolver渲染视图并返回
```



```sh
（1）用户发起请求到'前端控制器'（DispatcherServlet），该控制器会过滤出哪些请求可以访问Servlet、哪些不能访问。
就是 url-pattern 的作用，并且会加载 springmvc.xml 配置文件。

（2）前端控制器会找到'处理器映射器'（HandlerMapping），通过HandlerMapping完成url到controller映射的组件。
简单来说，就是将在 springmvc.xml 中配置的或者注解的url与对应的处理类找到并进行存储，用 map<url,handler> 这样的方式来存储。

（3）HandlerMapping有了映射关系，并且找到url对应的处理器，HandlerMapping就会将其'处理器'（Handler）返回，在返回前，会加上很多'拦截器'。

（4）DispatcherServlet拿到Handler后，找到'处理器适配器'（HandlerAdapter），通过它来访问处理器，并执行处理器。

（5）HandlerAdapter在执行Handler方法的过程中，可通过Service和DAO进行数据库相关操作，最终返回一个 ModelAndView 对象，封装了模型数据和视图名称。

（6）HandlerAdapter 将 ModelAndView 返回给 DispatcherServlet，DispatcherServlet 将 ModelAndView 传递给视图解析器（ViewResolver）
进行视图解析，根据逻辑视图名解析成真正的视图(jsp)，其实就是将ModelAndView对象中存放视图的名称进行查找，找到对应的页面形成视图对象

5 执行处理器
6 处理器会返回一个ModelAndView对象给HandlerAdapter
7 通过HandlerAdapter将ModelAndView对象返回给前端控制器(DispatcherServlet)
8 前端控制器请求视图解析器(ViewResolver)去进行视图解析，根据逻辑视图名解析成真正的视图(jsp)，其实就是将ModelAndView对象中存放视图的名称进行查找，找到对应的页面形成视图对象
9 返回视图对象到前端控制器。
10 视图渲染，就是将ModelAndView对象中的数据放到request域中，用来让页面加载数据的。
11 通过第8步，通过名称找到了对应的页面，通过第10步，request域中有了所需要的数据，那么就能够进行视图渲染了。最后将其返回即可。

```



```sh
（1）浏览器发送请求，请求交给 '前端控制器'DispatcherServlet 处理
（2）前端控制器 通过 '处理器映射器'HandlerMapping 维护的请求和 '控制器'Controller 之间的映射关系，找到相应的 Controller 组件处理请求
（3）

处理器映射器找到具体的处理器，生成处理器对象及处理器拦截器（如果有则生成）一并返回给DispatcherServlet。
DispatcherServlet调用HandlerAdapter处理器适配器。
HandlerAdapter经过适配调用具体的处理器（controller，也叫后端控制器）。
Controller执行完成返回ModelAndView。
HandlerAdapter将Controller返回的执行结果ModelAndView返回给DispatcherServlet。
DispatcherServlet将ModelAndView传给ViewReslover视图解析器。
ViewReslover解析之后返回具体的view。
DispatcherServlet根据View进行渲染视图（即将模型数据填充至视图jsp/freemaker..中）。
DispatcherServlet响应用户。
```



## SSM

> SSM三大框架中高内聚、低耦合是哪个框架实现的？

```sh

```

