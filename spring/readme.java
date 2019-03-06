05
	// 防止表单重复提交
	
		// 0.防止表单重复提交 - https://www.cnblogs.com/lwj-0923/p/7367517.html
			// 转发: 
				// req.getRequestDispatcher("show").forward(req, res);
				// req.getRequestDispatcher("add.jsp").forward(req, res);
			// 重定向: 
				// req.sendRedirect("show");
				// req.sendRedirect("add.jsp");
	
	页面非空验证jquery
07
	// 什么是框架?
		// //软件的半成品.为解决问题制定的一套约束,在提供功能基础上进行扩充.
		// //框架中一些不能被封装的代码(变量),需要使用框架者新建一个xml文件,在文件中添加变量内容
		// // 2.1 需要建立特定位置和特定名称的配置文件.
		// // 2.2 需要使用 xml 解析技术和反射技术
	// 类库vs框架?
		// // 3.1 类库:提供的类没有封装一定逻辑. 举例:类库就是名言警句,写作文时引入名言警句
		// // 3.2 框架:区别与类库,里面有一些约束. 举例:框架是填空题
07
	// MyBatis: 数据访问层框架; 底层是对JDBC的封装; 无需编写实现类,只需写sql
08
	// xml文件提示,导入dtd或schema
	
	// //sqlSession实例化; 涉及到'工厂+builder'设计模式
	// InputStream is = Resources.getResourceAsStream("myabtis.xml");
	// SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);
	// SqlSession session = factory.openSession();
	// List<Flower> list = session.selectList("a.b.listAll"); //调用sql
	// session.close(); //关闭连接!!
11
	// 数据库连接池?
		// // 在高频率访问数据库时,使用数据库连接池可以降低服务器系统压力,提升程序运行效率.
		// // 小型项目不适用数据库连接池		
		// // 当关闭连接对象时,把连接对象归还给数据库连接池,把状态改变成 Idle
	常用属性
		driverClassName="com.mysql.jdbc.Driver"
		url="jdbc:mysql://localhost:3306/ssm"
		username="root"
		password="smallming"
		maxActive="50"
		maxIdle="20"
		name="test"
		auth="Container"
		maxWait="10000"
		type="javax.sql.DataSource"
12
	// 三种查询: selectList; selectOne; selectMap;
	//其中,selectMap的结果集Map<Object,Object>中key为列值,value为当前行封装的pojo
	
2-1
	在'数据访问层'和'控制器层'处理异常,service中只抛出异常
	
	注解赋值格式为(属性名=属性值)
	大部分注解都有默认值,如果只给默认值赋值,可简写为(属性值)
	属性值为数组类型,格式为(属性名={值1,值2}),如果只有一个值,可省去大括号
	如果类不是基本数据类型或String,格式为(属性名=@类型),@表示引用注解声明
	
	路径以/开头都是全路径; 从项目根目录出发找到其他资源的过程.
	不以/开头都是相对路径; 从当前资源出发找到其他资源的过程.
	
2-7
	功能:从应用程序角度出发,软件具有哪些功能.
	业务:从代码角度出发,完成功能时的逻辑.对应 Service 中一个方法
	事务:从数据库角度出发,完成业务时需要执行的 SQL 集合,统称一个事务.
		//事务回滚: 如果在一个事务中某个 SQL 执行事务,希望回归到事务的原点,保证数据库数据的完整性.
	
3-1
	单纯判断某一列是否存在,sql语句 select count(*) 比 select * 高效!!
	所有定义在接口中的常量,默认都是 public static final *;
	
	final 修饰的变量不允许重新实例化
	
	存储过程
		(0).减少java代码的业务逻辑,将逻辑转移到数据库层面.
		(1).减少客户端与服务器之间的网络IO,只传存储过程名和参数.
		(2).存储过程直接加载到数据库的内存区域,省去sql编译过程,运行速度快.
		[3].大量编写存储过程,占用服务器内存.(弊端)
	
4-1
	内容较多
	
	/*************************Spring********************************************/
/*
1-2S
	Spring核心功能: IoC/DI(控制反转/依赖注入); AOP(面向切面编程); 声明式事务

1-5S
	Spring创建对象的三种方式
	1.构造方法
		//默认调用无参构造方法; 或 明确配置调用有参构造方法.
		(1).在类中提供有参构造方法
		(2).在 applicationContext.xml 中配置调用哪个有参构造
			
			// 如果设定的条件匹配多个构造方法,执行最后一个
			<bean id="people" class="com.x.pojo.People">
				// index: 参数的索引,从 0 开始
				// name: 参数名
				// type: 类型.(区分 int 和 Integer)
				// value: 基本数据类型或String
				// ref: 引用另一个 bean
				<constructor-arg index="0" name="id" type="int" value="123" />
				<constructor-arg index="1" name="name" type="java.lang.String" value=" 张三" />
				<constructor-arg index="2" name="desk" ref="desk" />
			</bean>
			
			<bean id="desk" class="com.x.pojo.Desk"></bean>
	
	2.实例工厂 - //需要先创建工厂,才能生产对象
		(1).编写一个实例工厂
			public class PeopleFactory {
				public People newInstance(){
					return new People(1,"测试");
				}
			}
		(2).在 applicationContext.xml 中配置工厂对象和需要创建的对象
			//factory-bean: 工厂类; factory-method: 调用工厂类的哪个方法
			<bean id="factory" class="com.x.pojo.PeopleFactory"></bean>
			<bean id="peo1" factory-bean="factory" factory-method="newInstance"></bean>
	
	3.静态工厂 - //不需要创建工厂,快速创建对象.
		(1).编写一个静态工厂(static 方法)
			public class PeopleFactory {
				public static People newInstance(){
					return new People(1,"测试");
				}
			}
		(2).配置 applicationContext.xml
			<bean id="peo2" class="com.x.pojo.PeopleFactory" factory-method="newInstance"></bean>
	
1-6S
	如何给Bean的属性赋值(注入)???
	1.通过有参构造方法.
	
	2.设置注入(调用set方法)
		<bean id="people" class="com.x.pojo.People">
			<property name="id" value="222"></property>
			<property name="name" value="张三"></property>
		</bean>
		
		//等价于
			<bean id="people" class="com.x.pojo.People">
				<property name="id">
					<value>456</value>
				</property>
				<property name="name">
					<value>zhangsan</value>
				</property>
			</bean>
			
		//如果属性是基本数据类型或String,直接使用标签 <value/>
		//但对于集合 Set<?>; List<?>; Arrays; 需要添加一层<set/> <list/> <array/>
			<property name="list" >
				<list>
					<value>1</value>
					<value>2</value>
				</list>
			</property>
		//如果属性是 Map<?,?>
			<property name="map">
				<map>
					<entry key="a" value="b" ></entry>
					<entry key="c" value="d" ></entry>
				</map>
			</property>
	
		//如果属性是 Properties 类型
			<property name="demo">
				<props>
					<prop key="key">value0</prop>
					<prop key="key1">value1</prop>
				</props>
			</property>
		
		//对于 Set|List|Array|Map|Properties 等,
		//如果只有一个值,可以将 <list/>和<value/> 标签省略,直接写为:
			<property name="list" value="1,2,3"></property>
	
1-7S
	DI: Dependency Injection,依赖注入;
		//当类(A)需要依赖类(B)对象时,把 B 赋值给 A 的过程就叫做依赖注入
		<bean id="people" class="com.x.pojo.People">
			<property name="desk" ref="desk"></property>
		</bean>
		
		<bean id="desk" class="com.x.pojo.Desk">
			<property name="id" value="1"></property>
			<property name="price" value="12"></property>
		</bean>
	
1-8S
	实例化DEMO
	1.DataSource
		<bean id="dataSouce" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
			<property name="driverClassName" value="com.mysql.jdbc.Driver"></property>
			<property name="url" value="jdbc:mysql://localhost:3306/test"></property>
			<property name="username" value="root"></property>
			<property name="password" value="root"></property>
		</bean>
	2.sqlSessionFactory
		<bean id="factory" class="org.mybatis.spring.SqlSessionFactoryBean">
			//<!-- 依赖注入,数据库连接信息来源于 dataSource -->
			<property name="dataSource" ref="dataSouce"></property>
		</bean>
	
	3.mybatis扫描器
		//<!-- 扫描器相当于 mybatis.xml 中 mappers 下 package 标签,扫描后会给对应接口创建接口对象-->
		<bean id="mapperScanner" (id可省) class="org.mybatis.spring.mapper.MapperScannerConfigurer">
			//<!-- 要扫描哪个包 -->
			<property name="basePackage" value="com.x.mapper"></property>
			//<!-- 依赖注入,让 factory 知道要扫描的包 -->
			<property name="sqlSessionFactory" ref="factory"></property>
		</bean>
		
	4.加载Spring配置
		//显示读取配置文件
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		
		//获取所有已定义的bean
		String[] beans = context.getBeanDefinitionNames(); 
		
		//获取指定类对象(args0:必须是实现类的类名小驼峰)
		FlowerService flowerService = context.getBean("flowerService", FlowerServiceImpl.class);
	
	4.1.简化版
		#将加载 applicationContext.xml 工作交给tomcat完成. tomcat启动时,默认加载 web.xml 文件,在此文件配置:
		
		//<!-- 设置spring配置文件的路径 -->
		<context-param>
			<param-name>contextConfigLocation</param-name>
			<param-value>classpath:applicationContext.xml</param-value>
		</context-param>
		//<!-- 加载Spring配置文件 -->
		<listener>
			<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
		</listener>
	
		//tomcat加载 web.xml 后,所有信息保存在 ServletContext 对象中.
		ServletContext servletContext = getServletContext();
		
		//spring和web整合后,所有配置信息保存在 WebApplicationContext. (ApplicationContext的子类)
		ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
*/
	4.2.如果要给类的某个属性赋值—这个属性所在类必须要被Spring所管理(***)
		//<!-- 由 spring 管理 service 实现类 -->
		<bean id="flowerService" class="com.x.service.impl.FlowerServiceImpl">
			//依赖注入,依赖Mapper
			<property name="flowerMapper" ref="FlowerMapper"></property>
		</bean>
		
		FlowerService flowerService = context.getBean("flowerService", FlowerServiceImpl.class);
*/
#2-1S
	1.刷新验证码功能
		//刷新验证码的功能放在 servlet 中完成
		客户端请求 图片或Servlet,服务端都是返回字节流,对于浏览器响应都是一样的.
		所以,直接访问静态资源; 或访问在servlet,在servlet中返回静态资源输出流,效果都是一样.
	
		//获取当前项目 images 文件夹在磁盘中的完整路径.("D:\blue\images")
		String path = servletContext.getRealPath("images");
		
	2.生成验证码
		//创建一张图片(单位:像素)
		BufferedImage image = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
		
		//向图片上画内容之前必须先设置画笔.
		Graphics2D gra = image.createGraphics();
		
		gra.setColor(Color.WHITE); //画笔颜色
		gra.fillRect(0, 0, 200, 100); //填充矩形区域(起始x,y,矩形x,y)
		
		List<Integer> randList = new ArrayList<Integer>();//生成随机数4个
		Random random =new Random();
		for (int i = 0 ;i<4;i++) {
			randList.add(random.nextInt(10));
		}
		
		//设置字体(类型,格式,大小)
		gra.setFont(new Font("宋体",Font.ITALIC|Font.BOLD,40));
		Color[] colors = new Color[]{Color.RED,Color.YELLOW,Color.BLUE,Color.GREEN,Color.PINK,Color.GRAY};
		for (int i = 0; i < randList.size(); i++) {
			gra.setColor(colors[random.nextInt(colors.length)]); //随机颜色
			//将随机数画到图片.(随机数,坐标x,上下浮动的坐标y)
			gra.drawString(randList.get(i)+"", i*40, 70+(random.nextInt(21)-10)); 
		}
		
		for (int i = 0; i < 2; i++) {
			gra.setColor(colors[random.nextInt(colors.length)]);
			//画出干扰线(起点x,y,终点x,y)
			gra.drawLine(0, random.nextInt(101), 200, random.nextInt(101));
		}
		
		//将图片写出到指定输出流
		ImageIO.write(image, "jpg", resp.getOutputStream());
		
		//把验证码放入到session中.
		//在登陆按钮点击时,需要验证用户填写的验证码和session中是否相同.
		HttpSession session = req.getSession();
		session.setAttribute("code", ""+randList.get(0)+randList.get(1)+randList.get(2)+randList.get(3));
	
	3.浏览器请求验证码
		<script type="text/javascript">
		$(function(){
			$("a").click(function(){
				//浏览器带有缓存功能,不会多次请求相同url的数据.(所以添加 ?date='')
				$("img").attr("src","validcode?date="+new Date());
				return false;
			})
		})
		</script>
		
		<img src="validcode" width="80" height="40"/><a href="">看不清</a>
	
#2-2S
	*ServiceImpl 中 *Mapper 对象必须设置 get/set 方法.
	而 *Controller 中的 *Service 对象不需要,为什么????
	//设置get/set是为spring在管理 *Service 时,依赖注入使用.
	//后者是直接给全局属性 *Service 赋值,所以可以不设置get/set.
/*	
#2-3S
	AOP(Aspect Oriented Programming): 面向切面编程
	
	AOP是将既有程序定义为一个切入点,然后在其前后切入不同的执行内容,
	比如常见的有: 打开数据库连接/关闭数据库连接; 打开事务/关闭事务; 记录日志等。
	
	//在不改变原有逻辑的前提下,提供额外功能,提高系统的扩展性.
	
	常用概念
		切入点: 原有方法, pointcut
		前置通知: 在切点之前执行的功能. before advice
		后置通知: 在切点之后执行的功能. after advice
		异常通知: 如果切点执行过程中出现异常,会触发异常通知. throws advice
		切面: 所有功能总称叫做切面.
		织入: 把切面嵌入到原有功能的过程叫做织入
	
#2-4S
	切入点表达式
		execution (* com.x.service.impl..*.*(..))
		//1. execution(): 表达式主体
		//2. 第一个*号: 返回值类型, *号表示所有!
		//3. 包名: 需要拦截的包名, 后面两个点表示当前包及其子孙包
		//4. 第二个*号: 类名
		//5. *(..): 最后*表示方法名, ()里面表示方法的参数,两个点表示任何参数

	DEMO
		https://www.cnblogs.com/duenboa/p/6665474.html
		https://blog.csdn.net/xubo_ob/article/details/78182014
	
		//第一个*代表返回类型; 第二个*代表方法名; 而..代表任意入参的方法
		execution(public * *(..)) //匹配所有类的public方法
	
		execution(* *To(..)) //匹配所有以To为后缀的方法
	
		execution(* com.x.service.*.*(..)) //匹配 com.x.service 包下所有类的所有方法
		
		execution(* com.x.service..*(..)) //匹配 com.x.service 包,及其子孙包下所有类的所有方法
		
		//匹配包名前缀为com, 包下类名后缀为Dao, 类中方法名前缀为find 的所有方法
		execution(* com..*.*Dao.find*(..))
		
		//??? ???
		execution(* com.x.Waiter.*(..)) //匹配Waiter接口的所有方法

		//??? ???
		execution(* com.x.Waiter+.*(..)) //匹配Waiter接口及其所有实现类的方法
	
		//匹配 joke() 方法, 且joke()方法的第一个入参是String; 第二个入参是int
		execution(* joke(String,int)))
	
		//同上, 第二个入参可以是任意类型
		execution(* joke(String,*)))
	
		//同上, 第二个参数及以后可以有任意个入参且入参类型不限
		execution(* joke(String,..)))
	
		//匹配 joke() 方法, 方法拥有一个入参,且入参是Object类型或该类的子类
		execution(* joke(Object+)))
	
	前置和后置通知
		前置-可获取: 切点方法对象; 切点方法参数; 切点方法所在类的对象
		后置-可获取: 切点方法的返回值; 切点方法对象; 切点方法参数; 切点方法所在类的对象 
	
#2-5S	
	异常通知(AspectJ)
	//只有当切点报异常才能触发异常通知
	(1).定义任意类,任意方法,其中方法参数名必须和 <aop:after-throwing> 的 throwing 相同
		public class MyThrowsAdvice{
			public void myexception(Exception e) {
				System.out.println("执行异常通知");
			}
		}
	
	(2).配置 ApplicationContext.xml
		// <aop:aspect>的 ref 属性表示: 异常通知方法在哪个类中.
		// <aop:xxx/>: xxx表示什么通知, after-throwing 表示异常通知
		// method: 当触发异常通知时,调用哪个方法
		// throwing: 异常对象名, 必须和通知方法中参数名相同.(可以不在通知中声明异常对象)
		<bean id="mythrow" class="com.x.advice.MyThrowsAdvice"></bean>
		<aop:config>
			<aop:aspect ref="mythrow">
				<aop:pointcut expression="execution(* com.x.test.Demo.demo1())" id="mypoint"/>
				<aop:after-throwing method="myexception" pointcut-ref="mypoint" throwing="e"/>
			</aop:aspect>
		</aop:config>
	
#2-6S
	异常通知(Schema-based)
	(1).自定义类 MyThrowsAdvice,并实现 ThrowsAdvice 接口
	(2).自定义方法,方法名必须是 afterThrowing; 方法参数必须是 1 或 4 个
	(3).方法参数中的异常类型,必须要与切点报的异常类型一致
	
		public class MyThrowsAdvice implements ThrowsAdvice{
			public void afterThrowing(Method m, Object[] args, Object target, Exception ex) {
				System.out.println("执行异常通知");
			}
			
			// public void afterThrowing(Exception ex) throws Throwable {
				// System.out.println("执行异常通过-schema-base 方式");
			// }
		}
	
	(4).配置 ApplicationContext.xml 
		<bean id="mythrow" class="com.x.advice.MyThrowsAdvice"></bean>
		<aop:config>
			<aop:pointcut expression="execution(*com.x.test.Demo.demo1())" id="mypoint"/>
			<aop:advisor advice-ref="mythrow" pointcut-ref="mypoint" />
		</aop:config>
		
#2-7S
	环绕通知(Schema-based)
	//把前置通知和后置通知都写到一个通知中,组成了环绕通知
	(0).自定义类实现 MethodInterceptor
		public class MyArround implements MethodInterceptor {
			@Override
			public Object invoke(MethodInvocation arg0) throws Throwable {
				System.out.println("环绕-前置");
				Object result = arg0.proceed();//放行,调用切点方式
				System.out.println("环绕-后置");
				return result;
			}
		}
	(1).配置 applicationContext.xml
		<bean id="myarround" class="com.x.advice.MyArround"></bean>
		<aop:config>
			<aop:pointcut expression="execution(*com.x.test.Demo.demo1())" id="mypoint"/>
			<aop:advisor advice-ref="myarround" pointcut-ref="mypoint" />
		</aop:config>
	
#2-8S
	AspectJ-所有通知
	(0).定义任意类,任意方法.
		class MyAdvice {
			public void mybefore(String name1, int age1) {
				System.out.println("前置" + name1);
			}

			public void mybefore1(String name1) {
				System.out.println("前置:" + name1);
			}

			public void myaftering() {
				System.out.println("后置 2");
			}

			public void myafter() {
				System.out.println("后置 1");
			}

			public void mythrow() {
				System.out.println("异常");
			}

			public Object myarround(ProceedingJoinPoint p) throws Throwable {
				System.out.println("执行环绕");
				System.out.println("环绕-前置");
				Object result = p.proceed();
				System.out.println("环绕后置");
				return result;
			}
		}
	
	(1).配置 applicationContext.xml
		// <aop:after/> 后置通知,是否出现异常都执行
		// <aop:after-returing/> 后置通知,只有当切点正确执行时执行
		// <aop:after/> 和 <aop:after-returing/> 和 <aop:after-throwing/> 执行顺序和配置顺序有关
		// execution() 括号不能扩上 args, 中间使用 and, 不能使用&&, 由 spring 把 and 解析成&&
		// args(名称) 名称自定义的. 顺序和 demo1(参数0,参数1)对应
		// <aop:before/> arg-names="名称", 名称来源于expression="" 中 args(),名称必须一样
		// args() 有几个参数,arg-names 里面必须有几个参数
		// arg-names="" 里面名称必须和通知方法参数名对应
		<bean id="myadvice" class="com.x.advice.MyAdvice"></bean>
		<aop:config>
			<aop:aspect ref="myadvice">
				<aop:pointcut expression="execution(*com.x.test.Demo.demo1(String,int)) and	args(name1,age1)" id="mypoint"/>
				<aop:pointcut expression="execution(*com.x.test.Demo.demo1(String)) and args(name1)" id="mypoint1"/>
				<aop:before method="mybefore" pointcut-ref="mypoint" arg-names="name1,age1"/>
				<aop:before method="mybefore1" pointcut-ref="mypoint1" arg-names="name1"/>
				<!-- <aop:after method="myafter" pointcut-ref="mypoint"/>
				<aop:after-returning method="myaftering" pointcut-ref="mypoint"/>
				<aop:after-throwing method="mythrow" pointcut-ref="mypoint"/>
				<aop:around method="myarround" pointcut-ref="mypoint"/>-->
			</aop:aspect>
		</aop:config>
	
#2-9S
	AspectJ-所有通知-获取参数
	(1).如上
	
#2-10S
	使用注解(基于 AspectJ)
	 Spring不会自动去寻找注解,必须告诉 spring 哪些包下的类中可能有注解
*/	
	
#2-11S
	设计模式: 前人总结的一套解决特定问题的代码
	
	代理设计模式
		优点: 保护真实对象; 让真实对象职责更明确; 扩展.
		三个角色: 真实对象(Laoban); 代理对象(Mishu); 抽象对象(Gongneng)
	
	静态代理设计模式
		//由代理对象代理所有真实对象的功能.
		//缺点: 当抽象对象里的功能比较多时,代理类中方法需要写很多
		(1).自定义代理类(Mishu)
		(2).抽象对象(Gongneng)的每个功能都需要代理类(Mishu)单独编写
	
		public interface Gongneng{ //抽象对象
			void mubiao();
			void chifan();
		}
		
		public class Laoban implements Gongneng{ //真实对象
			public void mubiao(){ syso("目标"); }
			public void chifan(){ syso("吃饭"); }
		}
		
		public class Mishu implements Gongneng{ //代理对象
			pivate Laoban laoban = new Laoban(); //持有真实对象的引用
			
			public void mubiao(){
				syso("约时间");
				laoban.mubiao();
				syso("谈合作");
			}
			public void chifan(){ /*...*/ }
		}
	
#2-12S
	动态代理设计模式(JDK)
		和 cglib 动态代理对比
		(1).优点: jdk自带,不需要导入额外jar
		(2).缺点: 真实对象必须实现接口; 利用反射机制,效率不高.
	
		public interface Gongneng{
			void mubiao();
			void chifan();
		}
		
		public class Laoban implements Gongneng{
			public void mubiao(){ syso("目标"); }
			public void chifan(){ syso("吃饭"); }
		}
		
		public class Mishu implements InvocationHandler { //JDK
			pivate Laoban laoban = new Laoban();
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				System.out.println("约时间");
				Object result = method.invoke(laoban, args);
				System.out.println("谈合作");
				return result;
			}
		}
		
		public static void main(String[] args) {
			//ClassLoader 全局唯一!!!
			//System.out.println(Women.class.getClassLoader()==Laoban.class.getClassLoader());
			
			Mishu mishu = new Mishu();
			//args0: 反射时使用的类加载器
			//args1: Proxy需要实现什么接口
			//args2: 通过接口对象调用方法时,需要调用哪个类的invoke方法
			Gongneng gongneng = (Gongneng) Proxy.newProxyInstance(Women.class.getClassLoader(), new Class[]{Gongneng.class}, mishu);
			gongneng.chifan();
		}
	
	动态代理设计模式(cglib)
		优点: 不需要实现接口; 基于字节码,生成真实对象的子类,运行效率高.
	
		public class Laoban implements Gongneng{ //真实对象
			public void mubiao(){ syso("目标"); }
			public void chifan(){ syso("吃饭"); }
		}
	
		public class Mishu implements MethodInterceptor{ //cglib
			//Object: cglib动态生成的代理类实例(子类)
			//Method: 被代理方法(父类方法)
			//Object[]: 参数值列表
			//MethodProxy: 代理类实现的被代理方法(子类重写的方法)
			public Object intercept(Object obj, Method method, Object[] arg, MethodProxy proxy) throws Throwable {
				syso("约时间");
				// method.invoke(obj, arg); //调用子类重写的方法(也就是 intercept(),产生迭代,错误!!!)
				
				//调用子类(obj)重写方法(proxy)的父类方法. (即实体类 Laoban 中的方法)
				Object result = proxy.invokeSuper(obj, arg); 
				syso("谈合作");
				return result;
			}
		}
	
		public static void main(String[] args) {
			Enhancer enhancer = new Enhancer();
			enhancer.setSuperclass(Laoban.class);
			enhancer.setCallback(new Mishu());
			
			Laoban laoban = (Laoban) enhancer.create();
			laoban.chifan(); //调用父类方法
		}
	
#3-1S
	
	******
	
#3-2S
	自动注入
	//当Person类cat属性的 ref 值与Cat类的 id 值相同时,则可以不用配置 <property/>
	<bean id="cat" class="com.x.pojo.Cat"></bean>
	<bean id="person" class="com.x.pojo.Person">
		<property name="cat" ref="cat"></property>
	</bean>
	
	//简化为
	<bean id="cat" class="com.x.pojo.Cat"></bean>
	<bean id="person" class="com.x.pojo.Person" autowire="byName"></bean>
	
	autowire取值范围
		default: 默认值,根据全局 default-autowire=""值.(全局默认no,不自动注入)
		no: 不自动注入.
		byName: 名称. 根据 name 去匹配所有bean的id
		byType: 类型. .... ref .................,当出现两个相同Type时,报错!!!
		constructor: 构造方法. 根据构造方法的参数名去匹配所有bean的id, 底层使用byName!
	
	局部和全局
		在<bean>中通过 autowire="" 配置,只对这个<bean>生效
		在<beans>中通过 default-autowire=""配置,对当前文件中所有<bean>生效
	
#3-3S
	加载properties文件
		//不明白
		
	// Spring将配置文件内容记载到自身容器中,所有被Spring管理的对象都可以获取到配置文件中的内容
	// Controller不被Spring管理,所以不能获取容器内容??? ???
	
	注解@Value("${key}")
		(1).添加注解扫描
			<context:component-scanbase-package="com.bjsxt.service.impl"></context:component-scan>
		
		(2).变量名和key可以不相同; 类型任意,只要保证 key 对应的 value 能转换成这个类型就可以.
			@Value("${my.info}")
			private String info;
	
#3-4S
	*** ***
	
#3-5S
	单例设计模式
	在应用程序中保证最多只能有一个实例
	好处: 提升运行效率(不用new,直接用); 实现数据共享.
	
	application 对象(四大作用域对象),从tomcat启动到关闭,一直有效.
	通过 getServletContext() 方法取出该对象,获取其中信息.
	
	0.饿汉式
	//构造方法: 方法名同类名,无返回值, 因为必须返回该类的类型,所有不用写.
	(0).私有化构造方法,其他方法不能new该对象.
	(1).对外提供一个公共访问入口 static SingleTon getInstance()
	(2).instance对象被 getInstance() 调用,加上 static,不能被外部访问,加上 private
	
	//加锁和二次判断,都是为了防止多线程异步访问时,创建多个对象.
	(1).线程 1,2 同时到达 synchronized,锁住,排队等待
	(1).线程 1 先进去,创建 instance 对象,返回,释放锁
	(2).线程 2 进去,如果没有二次判断,也会继续创建 instance 对象.
	
		public class SingleTon{ //对象只有被调用时才去创建 -> 懒汉式
			private SingleTon instance;
			
			private SingleTon(){ }
			
			public static SingleTon getInstance(){
				if(null==instance){
					synchronized(SingleTon.class){
						if(null==instance){ //二次判断
							instance=new SingleTon();
						}
					}
				}
				return instance;
			}
		}
	
	1.饿汉式
		public class SingleTon{ //在类加载时进行实例化 -> 饿汉式
			private static SingleTon instance=new SingleTon();
			
			private SingleTon(){ }
			
			public static SingleTon getInstance(){
				return instance;
			}
		}
	
	2.二者对比
		懒汉式: 由于添加了锁,所以导致效率低
		饿汉式: 解决了懒汉式中多线程访问可能出现同一个对象和效率低问题
	
#3-7S
	编程式事务: 由程序员编写事务控制代码; OpenSessionInView 编程式事务
	声明式事务: 
		事务控制代码已经由 spring 写好. 
		程序员只需要声明在哪些方法需要进行事务控制和如何进行事务控制.
	
	声明式事务都是针对于 ServiceImpl 类下方法的
	//Service中是业务,一个业务对应一个事务. (三者区分: 功能,业务,事务)
	
	事务管理器基于通知(advice)的. 类似AOP
	
	
#3-10S
	rollback-for="异常类型全限定路径" //建议:给定该属性值.
		当出现什么异常时需要进行回滚, 手动抛异常一定要给该属性值.
	
	#阻止程序正常流程的几种方案: return; throw Exception; ... ...
	
	no-rollback-for=""
		当出现什么异常时不滚回事务.
	
	
#3-11S
	常用注解
		@Component: 创建类对象,相当于配置<bean/>		
		@Controller: 写在控制器类上. (功能同 @Component)
		@Service: 写在 ServiceImpl 类上. (...)
		@Repository: 写在数据访问层类上. (...,mybatis不用,hibernate用)
		
		@Resource: java注解
			(1).不需要写对象的 get/set
			(2).默认按照 byName 注入; 如果没有名称对象,按照 byType 注入
			//建议把 对象名称 和 spring容器中对象名(类型名)相同
		
		@Autowired: spring注解
			(1).不需要写对象的 get/set
			(2).默认按照 byType 注入.
		
		@Value(): 获取 properties 文件中内容
		
		@Aspect() 定义切面类
		@Pointcut() 定义切点
		@Before() 前置通知
		@After 后置通知
		@AfterReturning 后置通知,必须切点正确执行
		@AfterThrowing 异常通知
		@Arround 环绕通知
	
#3-12S
	Ajax
	标准请求响应时浏览器的动作(同步操作)
	//浏览器请求什么资源,跟随显示什么资源
	
	ajax:异步请求. 
	局部刷新,通过异步请求,请求到服务器资源数据后,通过脚本修改页面中部分内容.
	
	ajax 由 javascript 推出的.
	由 jquery 对 js 中 ajax 代码进行的封装,达到使用方便的效果.
	
	jquery 中 ajax 分类
	(1). $.ajax({属性名:值,属性名:值}) //功能最全.代码相对最麻烦
		/** url: 请求服务器地址
			data: 请求参数
			type: 请求方式
			dataType: 服务器返回数据类型
			error: 请求出错执行的功能
			success: 请求成功执行的功能,function(data) data 服务器返回的数据.
		*/
		$("a").click(function(){
			$.ajax({
				url:'demo',
				data:{"name":"张三"},
				type:'POST',
				dataType:'html',
				error:function(){
					alert("请求出错.")
				},
				success:function(data){
					alert("请求成功"+data)
				}
			});
			return false;
		})
	
	(2).简化$.ajax()
		$.get(url,data,success,dataType))
		$.post(url,data,success,dataType)
		
	(3).简化$.get()
		$.getJSON(url,data,success) //相当于设置 $.get() 中 dataType=”json”
		$.getScript(url,data,success) //相当于设置 $.get 中 dataType=”script”
	
	#如果服务器返回数据是从表中取出.为了方便客户端操作返回的数据,服务器端返回的数据设置成 json
	#客户端把 json 当作对象或数组操作.
	
	ajax小练习 *** ***
	
#1-1C
	
	
	
	
	
	
	ORM: Object Relation Mapping,对象关系映射; //将程序中的对象持久化到数据库中
	
	IoC: Inversion of Control,控制反转; //作用: 解耦(解除对象管理和程序员之间的耦合)
	//控制->控制类的对象; 反转->转交给Spring负责.
	
	//以前,类(A)中用到类(B)的对象b,需要在A的代码中显式的new一个B的对象.
	//采用依赖注入DI技术之后,A的代码只需要定义一个私有的B对象,不需要直接new来获得这个对象,
	//而是通过相关的容器控制程序来将B对象在外部new出来并注入到A类里的引用中.
	//而具体获取的方法、对象被获取时的状态由配置文件(如XML)来指定.
	
	DI: Dependency Injection,依赖注入;
	//当类(A)需要依赖类(B)对象时,把 B 赋值给 A 的过程就叫做依赖注入

	
	
	
我也遇到了这个问题，后来解决了，原因是如果有注入bean的那个类，在被其他类作为对象引用的话（被调用）。
这个被调用的类也必须选择注解的方式，注入到调用他的那个类中，不能用 new出来做对象，
new出来的对象再注入其他bean就会 发生获取不到的现象。所以要被调用的javabean，都需要@service，
交给Spring去管理才可以，这样他就默认注入了。
	
	
	
0226
	SQL和NoSQL区别?
	
	一对一,一对多靠主外键关联, 多对多靠中间表.
	
	NoSQL特点: 数据量大; 价值较低; 写入操作频繁(非重要).
	
	
	

	
	
	
	
	
	
	

	