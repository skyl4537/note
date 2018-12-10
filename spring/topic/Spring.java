
#概念
	框架: 为解决某些问题而提出的一整套解决方案.
	非侵入式(轻量级): 不需要实现框架的接口.
	


///--------------<<<IoC>>>-------------------------------------------------------------
#核心功能
	IoC/DI: 控制反转/依赖注入
	AOP(Aspect Oriented Programming): 面向切面编程
	声明式事务

#IoC和DI
	//IoC(Inversion of Control)控制反转: 反转资源的获取方向. 
	传统的资源查找方式是组件主动向容器发起查找资源请求, 作为回应, 容器适时的返回资源. 
	而应用了 IOC 之后, 则是容器主动地将资源推送给它所管理的组件,
	组件所要做的仅是选择一种合适的方式来接受资源. 这种行为也被称为查找的被动形式.

	//DI(Dependency Injection): 依赖注入 — IOC 的另一种表述方式.
	组件以一些预先定义好的方式(如: setter方法)接受来自如容器的资源注入.
	IoC是一种思想,DI是具体的实现.
	
#IoC容器创建
	0.非Web应用
		///直接在 main() 中创建IoC容器.
		public static void main(String[] args) {
			
			//ApplicationContext: IoC容器的接口.
			//ClassPathXmlApplicationContext: 上面接口的实现类,从类路径下记载配置文件.
			ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
			
			//(1).根据Type获取IoC容器中的bean -> 要求容器中{有且只有一个}该类型的bean
			//(2).根据id获取IoC容器中的bean
			//(3).根据id和Type获取IoC容器中的bean
			Person person1 = context.getBean(Person.class); 
			Person person2 = (Person) context.getBean("person"); 
			Person person3 = context.getBean("person", Person.class);
		}
		
	1.Web应用(详见附表1)
		//在 Web 应用被tomcat加载时创建IoC容器,
		//然后放到 ServletContext(即 Application 域)的属性中,供其他模块使用.
		
		///(1).tomcat启动时,默认加载'web.xml'文件,在此文件配置"applicationContext.xml"位置信息.
		<context-param> 
			<param-name>contextConfigLocation</param-name>
			<param-value>classpath:applicationContext.xml</param-value> //Spring配置文件的名称和位置
		</context-param>
		
		<listener> //启动 IOC 容器的 ServletContextListener
			<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
		</listener>
		
		///(2).tomcat读取'web.xml'
		public void contextInitialized(ServletContextEvent sce) {
			ServletContext sc = sce.getServletContext();//获取 Spring 配置文件的名称. 
			String config = sc.getInitParameter("contextConfigLocation");
			
			ApplicationContext context = new ClassPathXmlApplicationContext(config); //创建IOC容器
			sc.setAttribute("ApplicationContext", context); //把IOC容器放在 ServletContext 的一个属性中
			
			//Spring和web整合后,SpringMVC所有配置信息保存在 WebApplicationContext. (ApplicationContext的子类)
			ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(sc);
		}

#实例化bean -> ///反射原理
	0.无参构造
		//id: bean名称; 在IOC容器中必须唯一; 默认使用类名小驼峰
		<bean id="person" class="com.x.pojo.Person"></bean>
	
	1.有参构造
		<bean id="people" class="com.x.pojo.People">
			<constructor-arg name="id" value="123" />
			<constructor-arg name="car" ref="car" /> //ref: 为bean的{属性/构造器参数}指定其他bean的引用
		</bean>
		
		<bean id="car" class="com.x.pojo.Car"></bean>
		
	2.引入c命名空间简化有参构造(简化版)
		xmlns:c="http://www.springframework.org/schema/c" ///引入c命名空间
		
		<bean id="people" class="com.x.pojo.People" c:id="123" c:car-ref="car" />
		<bean id="car" class="com.x.pojo.Car" />
		
	3.静态工厂(无需创建工厂,直接生产对象)
		//factory-method: 调用工厂类的哪个方法
		//其中,PeopleFactory 实现 FactoryBean<T> 接口.
		<bean id="people" class="com.x.pojo.PeopleFactory" factory-method="newInstance" />
		
	4.实例工厂(先创建工厂,再生产对象)
		//factory-bean: 指定工厂类
		<bean id="factory" class="com.x.pojo.PeopleFactory" />
		<bean id="peo1" factory-bean="factory" factory-method="newInstance" />
	
#依赖注入 -> ///当类(A)需要依赖类(B)对象时,把(B)赋值给(A)的过程就叫做依赖注入
	0.构造器注入
		如上2.
	
	1.属性注入(setter)
		<bean id="people" class="com.x.pojo.People">
			<property name="id" value="123"></property>
			<property name="car" ref="car"></property> //ref: 引用
			<property name="dog">
				<bean /*id="dog"(可省)*/ class="com.x.pojo.People.Dog"> //内部bean: 不能使用在任何其他地方,可省id
					<constructor-arg index="1" name="name" type="java.lang.String" value="yellow" />
				</bean>
			</property>
		</bean>

		<bean id="car" class="com.x.pojo.Car">
			<property name="id" value="1"></property>
			<property name="price" value="720000.0"></property>
		</bean>
		
	2.引入p命名空间简化属性注入(简化版)
		xmlns:p="http://www.springframework.org/schema/p" ///引入p命名空间
	
		<bean id="people" class="com.x.pojo.People" p:id="123" p:car-ref="car" p:dog-ref="dog">
			<bean id="dog" class="com.x.pojo.People.Dog" c:name="yellow" />
		</bean>
		<bean id="car" class="com.x.pojo.Car" p:id="1" p:price="720000.0" />

#集合属性
	//如果属性是 基本数据类型或String,直接使用标签 <value/>
	//但对于集合 Set<?>; List<?>; Arrays; Property; 需要添加一层<set/> <list/> <array/> <props/>
	
		<property name="list" > //(1).List<?>属性
			<list>
				<value>1</value>
				<value>2</value>
			</list>
		</property>
	
		<property name="map"> //(2).Map<?,?>属性
			<map>
				<entry key="a" value="a0" ></entry>
				<entry key="b" value="b0" ></entry>
			</map>
		</property>

		<bean id="dataSource" class="com.x.pojo.DataSource">
			<property name="properties"> //(3).Properties属性
				<props>
					<prop key="username">bluecardsoft</prop>
					<prop key="password">123456</prop>
					<prop key="url">jdbc:mysql://192.168.8.7:33306/test0329</prop>
					<prop key="driverClassName">com.mysql.jdbc.Driver</prop>
				</props>
			</property>
		</bean>

#自动装配
	///Spring IOC 容器可以自动装配 Bean. 需要做的仅仅是在<bean> 的 autowire 属性里指定自动装配的模式.
	0.byType(根据类型自动装配)
		//若 IOC 容器中存在多个 Car,将无法判定哪个 Car 最适合该属性,所以不能执行自动装配
		<bean id="car" class="com.x.pojo.Car" p:brand="Audi" /> //多个Car
		<bean id="car0" class="com.x.pojo.Car" p:brand="Bens" />
		<bean id="people" class="com.x.pojo.People" p:name="wang" /*p:car-ref="car"*/ autowire="byType" />
	
	1.byName(根据名称自动装配)
		//必须将 People 的属性名和目标Bean的 id 设置的完全相同.
		<bean id="car" class="com.x.pojo.Car" p:brand="Audi" /> //换成'car1'则不能自动装配(XXX)
		<bean id="people" class="com.x.pojo.People" p:name="wang" /*p:car-ref="car"*/ autowire="byName" />
		
	2.自动装配缺点
		(1).属性 autowire 作用于 Bean 的所有属性. 所以,希望只自动装配个别属性时,不能实现.
		(2).属性 autowire 要么 byType, 要么 byName, 不能两者兼得.
		(3).所以, 实际项目中很少使用自动装配功能, 明确清晰的配置文档更有说服力.

#bean之间关系
	1.继承
		//Spring 允许继承 bean 的配置, 被继承的 bean 称为父 bean. 继承的 Bean 称为子 Bean.
		(0).子Bean 可以'继承并覆盖'父Bean 中的配置(属性 autowire, abstract 除外).
		(1).若父Bean 只作为模板, 可以设置 abstract 属性为 true, 这样 Spring 将不会实例化这个Bean.
		(2).父Bean 可不配置 class 属性(必须设 abstract 为 true), 让子Bean 自己指定类, 只继承父bean 其他的属性.
	
		<bean id="people" p:id="123" p:name="wang" abstract="true" />
		<bean id="chinese" class="com.x.pojo.Chinese" /*p:id="123" p:name="wang"*/ parent="people" /> //parent: 指定父bean
	
	2.依赖
		//Spring 允许用户通过 depends-on 属性设定前置依赖Bean. 
		//依赖Bean 会在本Bean 实例化之前创建好.
		///依赖关系不等于引用关系; people即使依赖car,也可以不引用它。
		<bean id="car" class="com.x.pojo.Car" p:brand="Audi" p:price="720000" />
		<bean id="people" class="com.x.pojo.people" p:id="123" p:name="wang" depends-on="car" />

#bean作用域
	1.singleton: //默认值; 单例; 只在IoC容器初始化时创建一次.
	
	2.prototype: //原型的; IoC容器初始化时并不会创建; 而是在每次调用时重新创建一个新的对象.
		<bean id="car" class="com.x.pojo.Car" scope="prototype" p:brand="Audi" p:price="720000" />

#使用外部属性文件
	1.导入属性文件
		<context:property-placeholder location="classpath:db.properties" />
		
	2.使用外部属性文件的配置
		<bean id="dataSource"
			class="com.alibaba.druid.pool.DruidDataSource">
			<property name="username" value="${username}" />
			<property name="password" value="${password}" />
			<property name="url" value="${url}" />
			<property name="driverClassName" value="${driverClassName}" />
		</bean>
		
#SpEL表达式
	///Spring表达式语言; 支持运行时查询和操作对象图的强大的表达式语言
	(0).通过 bean 的 id 对 bean 进行引用
	(1).调用方法以及引用对象中的属性
	(2).计算表达式的值
	(3).正则表达式的匹配
	
		<bean class="com.x.pojo.Car" p:brand="Audi" p:price="720000" />
		<bean class="com.x.pojo.People" p:name="wang" 
			p:car="#{car}" //引用对象, 等价于 p:car-ref="car"
			p:pet="#{car.brand}" //引用对象的属性
			p:info="#{car.price > 300000 ? '金领':'白领'}" //三元运算符,单引号

#IoC容器中bean的生命周期
	(0).通过构造器或工厂方法创建 Bean 实例		//constuctor...
	(1).为 Bean 的属性赋值和对其他 Bean 的引用	//setter...
	(2).将 Bean 实例传递给 Bean 后置处理器的 postProcessBeforeInitialization 方法
	(3).调用 Bean 的初始化方法 //init...
	(4).将 Bean 实例传递给 Bean 后置处理器的 postProcessAfterInitialization 方法
	(5).Bean 此时可以使用了 //Car [brand=Audi, price=720000.0]
	(6).当容器关闭时, 调用 Bean 的销毁方法 //destroy...
		
		//首先,javabean中必须定义 Init(); destroy(); 方法
		//其次,实现bean后置处理器: implements BeanPostProcessor
		<bean id="car" class="com.example.bean.Car" init-method="init"
			destroy-method="destroy" p:brand="Audi" p:price="720000" /> //配置init(),destroy()方法
		<bean class="com.x.config.myBeanPostProcessor" /> //配置bean后置处理器
	
	
#基于注解配置Bean	
	///@ComponentScan: 指定Spring在哪些包及其下级包中寻找bean
		(1).Boot项目,该注解包含在 @SpringBootApplication 中,所以只需在启动类中配置前者即可.
		
		(2).非Boot项目,可使用注解和xml两种方式配置:
			@Configuration
			@ComponentScan({"com.x.demo.pojo","com.x.demo.service"})
			public class SpringConfiguration { }
			
			<context:component-scan base-package="com.x.demo.pojo, com.x.demo.service" />
	
	///@Component, @Respository, @Service, @Controller: 被Spring框架所扫描,并注入到IoC容器来进行管理
		@Component: //通用注解(基础); 指明是一个bean,使用spring进行管理,通常配合@Bea使用
		@Repository: //持久层注解; 具有将数据库操作抛出的原生异常,翻译转化为Spring持久层异常的功能.
		@Controller: //控制层注解; 具有将请求进行转发,重定向的功能.
		@Service: //业务逻辑层注解; 只是标注该类处于业务逻辑层.
		
	///@Autowired, @Resource, @Inject: 自动装配'具有兼容类型'的单个 Bean属性
		(1).若 CarService 不被Spring所管理,将抛异常. 
			//解决方案: 设置 @Autowired(required = false)
			
		(2).当存在多个 CarService 类时,自动装配也无法完成. 
			//解决方案: @Service("carService01") + @Qualifier("carService01")
		
		@component //标记为Spring所管理
		class PeopleController{
			
			@autowire //自动装配属性
			CarService carService; 
		}
		
	///@Resource @Autowired @Qualifier	
		@Autowired: 类型 //如想按名称来转配注入,则需要结合 @Qualifier 一起使用
		@Resource: 名称 //只有当找不到与 name 属性匹配的bean,才会按照 type 属性来装配注入

		//@Resource 由J2EE提供,@Autowired 由spring提供,故减少系统对spring的依赖,建议使用 @Resource 的方式
		//@Resource 和 @Autowired 都可以标注在字段或字段的setter方法上
			
			@Resource(name="infoService") //先找名称为 infoService 的bean来装配字段 infoService. 不存在,则按照类型查找
			private InfoService infoService;
			
			@Autowired //从spring配置文件中查找类型为 InfoService 的bean
			private InfoService infoService;
		
		///当 InfoService 有两个实现类时,则不能简单的使用 @AutoWired 进行注入,两种解决方案:
			@Service("up")
			public class UpInfoServiceImpl implements InfoService { }
			
			@Service("down")
			public class DownInfoServiceImpl implements InfoService { }
			
			@Autowired
			@Qualifier("up") //(1).配合 @Qualifier(name) 使用
			InfoService upInfoService;
			
			@Resource(name="up") //(2).直接使用 @Resource 
			InfoService upInfoService;
		
///-----------------<<<AOP>>>-------------------------------------------------------------------
#基本概念
	AOP(Aspect-Oriented Programming): 面向切面编程, 通过'动态代理'实现程序功能的统一维护。
	
	AOP通过对既有程序定义一个切入点,然后在其前后切入不同的执行内容,
	比如常见的有: 打开数据库连接/关闭数据库连接 打开事务/关闭事务 记录日志等。
	
	基于AOP不会破坏原来程序逻辑,因此它可以很好的对业务逻辑的各个部分进行隔离,
	从而使得业务逻辑各部分之间的耦合度降低,提高程序的可重用性,同时提高了开发的效率。
	
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-aop</artifactId>
		</dependency>
		
#demo配置-注解
	@Slf4j // lombok的简写log
	@Aspect // 标明该类是一个切面类
	@Order(5) // 切面的优先级 -> 值越小,优先级越高
	@Component // Spring管理
	public class MyAopConfig {

		/**
		 * 使用注解 @Order(i)定义每个切面的优先级. i的值越小,优先级越高
		 * 
		 * 切面 CheckNameAspect @Order(10), LogAspect @Order(5),所以后者具有更高的优先级.
		 * 
		 * 执行顺序： 在 @Before 中优先执行 @Order(5),再执行 @Order(10). 在 @After 和 @AfterReturning
		 * 中优先执行 @Order(10),再执行 @Order(5)
		 * 
		 * 总结： 前置通知,从小到大. 后置返回(通知),从大到小.
		 */

		/**
		 * execution表达式: (<修饰符> + 返回值类型 + 方法全名 + (参数列表) + <异常列表>) 其中,<>内容为可选值.
		 * 
		 * 用'*'匹配任意类型; 用'..'匹配任意多个任意类型入参,或当前包及其子孙包
		 * 
		 * (*,String) -> 匹配第一个参数任意类型,第二个参数String; (..) -> 匹配零个或多个任意类型的参数
		 * 
		 * 可以使用 && || ! 三个运算符连接各个切点签名.(&&表示同时满足)
		 */

		// execution(public * *(..)) //匹配所有类的public方法
		// execution(* *To(..)) //匹配所有以To为后缀的方法
		// execution(* com..*.*Dao.find*(..)) //匹配包名前缀com,类名后缀Dao,方法名前缀find的所有方法
		// execution(* com.x.service.InfoService.*(..)) //匹配 com.x.service.InfoService 类的所有方法
		// execution(* com.x.service.*.*(..)) //匹配 com.x.service 包下所有类的所有方法
		// execution(* com.x.service..*(..)) //匹配 com.x.service 包,及其子孙包下所有类的所有方法

		// 定义切点(重用机制): 起点表达式 + 切点签名
		@Pointcut("execution(* com.example.controller..*(..))") // 切点表达式
		private void ctrl() { // 切点签名
		}

		@Pointcut("execution(* com.example.service..*(..))")
		private void service() {
		}

		// 可以将一些公用的 切点 放到一个类中,以供整个应用程序使用. 使用时,指定完整的类名加切点签名.
		// 如: @Before("com.x.config.MyAopConfig.Pointcuts.ctrl()")
		class Pointcuts {
			@Pointcut("execution(* com.example.controller..*(..))")
			private void ctrl() {
			}
		}

		// {目标} -> 记录一次请求所需时间
		// 定义一个成员变量来给 @doBefore 和 @doAfterReturning 一起访问??? 是否会有同步问题???
		// 答案是肯定的。正确做法是引入ThreadLocal对象
		ThreadLocal<Long> startTime = new ThreadLocal<>();

		// (0).前置通知 - 在方法执行之前执行
		@Before("ctrl() || service()")
		public void doBefore(JoinPoint joinPoint) {
			startTime.set(System.currentTimeMillis()); // 请求开始时间

			log.info(System.getProperty("line.separator"));// 系统级别的换行符
			ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
			HttpServletRequest request = attributes.getRequest();
			String method = request.getMethod();// GET
			String ip = request.getRemoteAddr();// 192.168.8.7
			StringBuffer url = request.getRequestURL();// http://192.168.8.7:8090/demo/hello/hello

			List<Object> args = Arrays.asList(joinPoint.getArgs());
			log.info("the method: " + getAllName(joinPoint) + " begin with: " + args);
		}

		// (1).后置通知 - 在方法返回结果或抛出异常,都会执行
		@After("ctrl()")
		public void doAfter(JoinPoint joinPoint) {
			long spendTime = System.currentTimeMillis() - startTime.get();// 请求总消耗时间

			log.info("the method: " + getAllName(joinPoint) + " end!! spend: " + spendTime);
		}

		// (2).返回通知 - 正常返回才会执行,抛异常则不会.
		@AfterReturning(value = "ctrl()", returning = "result")
		public void doAfterReturning(JoinPoint joinPoint, Object result) {// 第二个参数名必须和 returning 属性值相同
			log.info("the method: " + getAllName(joinPoint) + " end with: " + result);
		}

		// (3).异常通知 - 只在连接点抛出异常时才执行.
		@AfterThrowing(value = "ctrl()", throwing = "t")
		public void doAfterThrowing(JoinPoint joinPoint, Throwable t) {// Throwable是所有错误和异常类的超类,推荐
			log.info("the method: " + getAllName(joinPoint) + " throws: " + t);
		}

		// (4).环绕通知
		// @Around(value = "")
		public void doAround(JoinPoint joinPoint) {
			// TODO
		}

		// 获取完整方法名 - 类全名.方法名()
		public String getAllName(JoinPoint joinPoint) {
			Signature signature = joinPoint.getSignature();
			String className = signature.getDeclaringTypeName();// 类全名
			String methodName = signature.getName(); // 方法名
			return className + "." + methodName + "()";
		}
	}
		
#demo配置-xml		
	// <!-- 配置切面的 bean. -->
	<bean id="myAopConfig" class="com.example.config.MyAopConfig"></bean>

	<bean id="myAopConfig0" class="com.example.config.MyAopConfig0"></bean>

	// <!-- 配置 AOP -->
	<aop:config>
		// <!-- 配置切点表达式 -->
		<aop:pointcut id="pointcut" expression="execution(* com.example.controller..*(..))"/>
		
		// <!-- 配置切面及通知 -->
		<aop:aspect ref="myAopConfig" order="2">
			<aop:before method="doBefore" pointcut-ref="pointcut"/>
			<aop:after method="doAfter" pointcut-ref="pointcut"/>
			<aop:after-returning method="doAfterReturning" pointcut-ref="pointcut" returning="result"/>
			<aop:after-throwing method="doAfterThrowing" pointcut-ref="pointcut" throwing="t"/>
			// <!-- <aop:around method="aroundMethod" pointcut-ref="pointcut"/> --> 环绕通知
		</aop:aspect>	
		<aop:aspect ref="myAopConfig0" order="1"> //测试优先级
			<aop:before method="validateArgs" pointcut-ref="pointcut"/>
		</aop:aspect>
	</aop:config>	
		
///-----------------<<<声明式事务>>>-------------------------------------------------------------------		
#基本概念(ACID)
	一系列的动作, 一个单独的工作单元. 这些动作要么全部完成, 要么全部失败.
	
	原子性(Atomicity): //原子操作; 要么全部完成,要么完全不起作用.
	一致性(Consistency): //一旦所有事务动作完成, 事务就被提交. 数据和资源就处于一种满足业务规则的一致性状态中.
	隔离性(Isolation): //可能有许多事务同时处理相同的数据, 因此每个事务都应该与其他事务隔离开来, 防止数据损坏.
	持久性(Durability): //一旦事务完成, 无论发生什么系统错误, 它的结果都不应该受到影响. 通常情况下, 事务的结果被写到持久化存储器中.
		
	'编程式事务' 
		由程序员编写事务控制代码; OpenSessionInView 编程式事务
		
	'声明式事务'
		事务控制代码已经由 Spring 写好.(事务管理器是基于通知advice的,类似AOP) 
		程序员只需要声明在哪些方法进行事务控制和如何进行事务控制.
		
	事务的传播行为
		当事务方法被另一个事务方法调用时, 必须指定事务应该如何传播. 
		//例如: 方法可能继续在现有事务中运行('REQUIRED'), 也可能自己新开一个事务再运行('REQUIRES_NEW').
		
	事务的隔离级别
		///事务的隔离级别要得到底层数据库引擎的支持,而不是应用程序或者框架的支持.(mysql支持4种 > oracle的2种)
		从理论上来说, 事务应该彼此完全隔离, 以避免并发事务所导致的问题. 
		然而, 那样会对性能产生极大的影响, 因为事务必须按顺序运行.
		//READ_COMMITTED: 只允许事务读取已经被其他事务提交的变更,可以避免脏读,但不可重复读和幻读问题仍然可能存在.
		
#使用步骤
	全局性注解 @EnableTransactionManagement
	
	局部性注解 @Transactional (一般定义在 ServiceImpl)
		
#实例demo
	// propagation: 事务的传播行为, 默认 REQUIRED
	// isolation: 事务的隔离度, 默认 DEFAULT
	// timeout: 事务的超时时间, 默认 -1,永不.(设置10(秒),则10s后事务还没完成,就则自动回滚事务)
	// readOnly: 否为只读事务，默认 false.(忽略那些不需要事务的方法,比如读取数据,可以设置 readOnly 为 true)
	// rollbackFor: 指定能够触发事务回滚的异常类型,用逗号分隔. {xxx1.class, xxx2.class, ...}
	// noRollbackFor: 指定不触发事务回滚的异常类型, 同上.
	@Override
	@Transactional
	public void purchaseBook(String userId, String bookId) {
		// (0).获取书单价
		int price = bookMapper.selPriceByBookId(bookId);

		// (1).更新书库存
		bookMapper.updBookStock(bookId);

		// (2).更新账户余额
		userMapper.updUserAccount(userId, price);
	}
		
		
		

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		

































































































