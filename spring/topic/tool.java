

//{-----------<<<mini-tool>>>--------------------------------------------------------------
#JSON-Handle //Chrome插件
	浏览器输入: chrome://extensions/
	将下载后的文件拖入浏览器即可

#cmder	//cmd升级版
	下载: http://cmder.net/	(mini与full版: 差别在于有没有内建msysgit工具)
	
	右键菜单: '配置系统环境变量,然后使用系统cmd执行命令: Cmder.exe /register ALL'
	中文乱码: 'settings -> Environment -> 添加: set LANG=zh_CN.UTF-8'
	
//}


//{-----------<<<SystemUtil>>>>------------------------------------------------------------
#常用工具
	public class SystemUtil {
		//获取项目class路径
		public static String getClassPath() throws FileNotFoundException {
			// ClassUtils.getDefaultClassLoader().getResource("").getPath();//等同
			return ResourceUtils.getURL("classpath:").getPath();
		}

		//获取项目根路径
		public static String getFilePath() throws FileNotFoundException {
			return ResourceUtils.getURL("").getPath();
		}
		
		//系统换行
		public static String newline() {
			return System.getProperty("line.separator");
		}
	}
	
#常用解析
	0.org.apache.commons.lang3.StringUtils
		isEmpty(); //为空判断标准是: str==null 或 str.length()==0
		isBlank(); //在isEmpty()的基础上增加: 制表符,换行符,换页符,回车符...等

	1.保留两位有效小数
		double num = 12.1250/12.1251;
		String num0 = String.format("%.2f", num);// 12.13/12.13
		String num1 = new DecimalFormat("#0.00").format(num);// 12.12/12.13
		
		//DecimalFormat特殊字符说明
		//	"0"指定位置不存在数字则显示为0: 123.123 ->0000.0000 ->0123.1230
		//	"#"指定位置不存在数字则不显示: 123.123 ->####.####  ->123.123
		//	"."小数点
		//	"%"会将结果数字乘以100 后面再加上% 123.123 ->#.00%  ->12312.30%

	2.使用占位符拼接字符串
		MessageFormat.format("域名{0}被访问了{1}次", "\"www.qq.com\"", 123.456); //域名"www.qq.com"被访问了123.456次
		
		//String.format() ==> 创建格式化的字符串; 及连接多个字符串对象.
		String.format("域名%s被访问了%3.2f次", "\"www.qq.com\"", 123.456); //域名"www.qq.com"被访问了123.46次
		
		//先转化十六进制,再高位补0
		String.format("%04d",Integer.parseInt(String.format("%x", 16))); //0010
		
		/String.format()具体详见: https://www.cnblogs.com/Dhouse/p/7776780.html
		
#通过类目获取类的对象
	@Component
	public class MyApplicationContextAware implements ApplicationContextAware {// 获取bean的工具类

		private static ApplicationContext context;

		// 实现ApplicationContextAware接口的回调方法，设置上下文环境
		@Override
		public void setApplicationContext(ApplicationContext context) throws BeansException {
			MyApplicationContextAware.context = context;
		}

		// 获取applicationContext
		public static ApplicationContext getApplicationContext() {
			return context;
		}

		// 通过name获取Bean.
		public static Object getBean(String name) {
			return context.getBean(name);

		}

		// 通过clazz获取Bean.
		public static <T> T getBean(Class<T> clazz) {
			return context.getBean(clazz);
		}

		// 通过name及clazz返回指定的Bean
		public static <T> T getBean(String name, Class<T> clazz) {
			return context.getBean(name, clazz);
		}
	}
	
//}
	
//{-----------<<<lombok>>>------------------------------------------------------------------
	///减少很多重复代码的书写. 比如:getter/setter/toString等
	<dependency>
		<groupId>org.projectlombok</groupId>
		<artifactId>lombok</artifactId>
		<scope>provided</scope>
	</dependency>		

#eclipse
	下载: https://projectlombok.org/download
	将 lombok.jar 放在eclipse安装目录下,与 eclipse/sts.ini 同级
	当前目录打开cmder, 使用命令: "java -jar ./lombok.jar",弹框选择 Install/Update
	成功标识: sts.ini最后一行, -javaagent:F:\sts-bundle\sts-3.9.3.RELEASE\lombok.jar

#idea
	下载: https://github.com/mplushnikov/lombok-intellij-plugin/releases
	Settings-Plugins-'install from disk',选择下载的*.zip.即可安装成功	
	
#常用注解
	@Slf4j: 生成slf4j注解式logger
	@NonNull: 调用字段的setter方法时,传参为null,则报空指针异常.
	@Data: 组合注解 //包含 @Getter; @Setter; @ToString, @EqualsAndHashCode; 无参构造函数.
	
	@Accessors: 定制化@Getter与@Setter
	//(chain = true): 链式编写setter方法,如 Person hua = new Person().setName("HUA").setAge(18);
	//(fluent  = true): 流式编写setter方法,如 Person wang = new Person().name("WANG").age(18);
	
	@NoArgsConstructor: 无参构造
	@AllArgsConstructor: 全参构造.(此时没有无参构造)
	@RequiredArgsConstructor: 只含 @NonNull字段 的构造函数
	
	@SneakyThrows(*.class): 
	//用在'方法'上,可将方法中的代码用 try-catch 语句包裹起来.
	//捕获异常并在 catch 中用 Lombok.sneakyThrow(e) 把异常抛出
	
//}	
	
//{-----------<<<thymeleaf>>>---------------------------------------------------------------
	模板引擎:'将后台数据 填充 到前台模板的表达式中!' //thymeleaf; freemarker; jsp; velocity;

#sts代码提示
	0).下载STS插件: https://github.com/thymeleaf/thymeleaf-extras-eclipse-plugin/releases
	1).在STS安装目录dropins下新建文件夹: thymeleaf-2.1.2
	2).只将压缩包中的 features 和 plugins 文件夹拷贝到以上目录并重启eclise!!!
	3).在thymeleaf的html页面引入命名空间: 
		<html lang="en" xmlns:th="http://www.thymeleaf.org">
		
#templates
	//thymeleaf 模板文件存放位置: src/main/resources/templates
	templates 目录是安全的. 意味着该目录下的内容不允许外界直接访问,必须经过服务器的渲染.
	
#低版本异常
	#org.xml.sax.SAXParseException: 元素类型 "meta" 必须由匹配的结束标记 "</meta>" 终止
	#这是由于低版本对于html语法解析比较严格,必须有头有尾.
	
	(1).html标记按照严谨的语法去编写
		<meta charset="UTF-8" />
		
	(2).升级为高版本
		//Thymeleaf.jar: 更新为 3.0 以上的版本
		//thymeleaf-layout-dialect.jar: 更新为 2.0 以上的版本
		<properties>
			<java.version>1.8</java.version>
			<thymeleaf.version>3.0.2.RELEASE</thymeleaf.version>
			<thymeleaf-layout-dialect.version>2.0.4</thymeleaf-layout-dialect.version>
		</properties>
		
#常用符号
	#~{...}	---> 片段引用表达式
	
	#@{...}	---> 定义URL
		//http://ip:8080/order/details/3
		<a href="emp" th:href="@{/details/}+${emp.id}">相对路径-传参-restful</a>
	
		//http://ip:8080/order/details?orderId=3
		<a th:href="@{http://ip:8080/order/details(orderId=${o.id})}">绝对路径-传参</a>

		//http://ip:8080/order/details?orderId=3
		<a th:href="@{/details(orderId=${o.id})}">相对路径-传参</a>

		//http://ip:8080/order/3/details?orderId=3 ---> 同上
		<a th:href="@{/{orderId}/details(orderId=${o.id}, orderName=${o.name})}">相对路径-传参-restful</a>
	
	#${...}	---> 变量值
		(1).获取对象的属性,调用方法; //${person.name}
		(2).使用内置的基本对象; //${! #strings.isEmpty(msg)} https://www.cnblogs.com/xiaohu1218/p/9634126.html
		(3).内置的一些工具对象; //
		
	##{...}	---> 用于获取 properties 文件内容,常用于'国际化'场景
		home.welcome=this messages is from home.properties! //properties文件
		
		<p th:text="#{home.welcome}">This text will not be show!</p> //读取properties文件中的 home.welcome

	##maps	---> 工具对象表达式; #dates #calendars #numbers #strings #objects #bools #arrays #lists #sets
		//有msg对象则显示<p>; 反之不显示
		<p style="color:red" th:text="${msg}" th:if="${not #strings.isEmpty(msg)}" />
		
	#*{...}	---> 类似${}功能, 配合th:object使用,获取指定对象的变量值
		<div> //(1).类似${}功能
			<p>Name: <span th:text="*{session.user.name}">Sebastian</span>.</p>
			<p>Surname: <span th:text="*{session.user.surname}">Pepper</span>.</p>
			<p>Nationality: <span th:text="*{session.user.nationality}">Saturn</span>.</p>
		</div>
		
		<div> //(2-1).原始表达式
			<p>Name: <span th:text="${session.user.firstName}">Sebastian</span>.</p>
			<p>Surname: <span th:text="${session.user.lastName}">Pepper</span>.</p>
			<p>Nationality: <span th:text="${session.user.nationality}">Saturn</span>.</p>
		</div>
	
		<div th:object="${session.user}"> //(2-2).指定对象
			<p>Name: <span th:text="*{firstName}">Sebastian</span>.</p>
			<p>Surname: <span th:text="*{lastName}">Pepper</span>.</p>
			<p>Nationality: <span th:text="*{nationality}">Saturn</span>.</p>
		</div>

		<div th:object="${session.user}"> //(2-3).混合使用
			<p>Name: <span th:text="*{firstName}">Sebastian</span>.</p> //指定对象
			<p>Surname: <span th:text="${session.user.lastName}">Pepper</span>.</p> //上下文变量取值
			<p>Nationality: <span th:text="${#object.nationality}">Saturn</span>.</p> //从 #object 中获取属性
		</div>
	
#th:href
	<a th:href="@{/emp}">用户添加</a> //发送get请求到 '/项目名/emp'
	
#th:src -> 图片类地址引入
	<img alt="App-Logo" th:src="@{/img/logo.png}" />

#th:id
	<div th:id = "stu+(${rowStat.index}+1)" class="student"></div> //动态指定id属性
	
#th:each
	//每次遍历都会产生当前标签<tr>
	<tr th:each="item : ${list}">
		<td th:text="${item.name}">name</td>
		<td>[[${item.price}]]</td>
		<td>[(${item.production})]</td>
		<td th:text="${itemStat.odd}?'odd':${item.memo}">memo</td>
	</tr>
	
	//(1).itemStat 称作状态变量,属性有:
		index		-> 当前迭代对象的索引. (从0开始)
		count		-> 当前迭代对象的计数. (从1开始)
		size		-> 迭代集合的长度
		current		-> 当前迭代变量
		even/odd	-> 布尔值,当前循环是否是偶数/奇数. (从0开始)
		first/last	-> 布尔值,当前循环是否是第一个/最后一个

	//(2).用法: ${itemStat.index}; ${itemStat.odd}; ${itemStat.current.name}... ...
	
	//(3).在写 th:each="obj,objStat:${objList}",可不写 objStat,自动添加,默认命名 objStat (如itemStat)
	
#th:value
	//可以将一个值放入到 input 标签的 value 中
	
#th:text
	<div th:text="${emp.name}">将被替换</div> //一般写法
	<div>[[${emp.name}]]</div> //行内写法行内写法
	
	//[[...]] & [(...)]
	<p>The message is "[[${msg}]]"</p> //会转义 <==> th:text
	<p>The message is "This is &lt;b&gt;great!&lt;/b&gt;"</p>

	<p>The message is "[(${msg})]"</p> //不会转义 <==> th:utext
	<p>The message is "This is <b>great!</b>"</p>

	//禁用行内写法(禁用内联)
	<p th:inline="none">A double array looks like this: [[1, 2, 3], [4, 5]]!</p> 
	
	//js内联
	<script th:inline="javascript">
		...
		var username = [[${session.user.name}]];
		...
	</script>
	
	//css内联
	<style th:inline="css">
		.[[${classname}]] {
		  text-align: [[${align}]];
		}
	</style>
	
#th:action -> 表单提交的地址
	//对于操作符'@',模板解析时会自动加上 'context-path' 作为前缀
	<form th:action="@{/batch/upload}" method="post" enctype="multipart/form-data">
		File: <input type="file" name="file"><br>
		Desc: <input type="text" name="desc"><br>
		<input type="submit" value="提交">
	</form>
	
#th:selected -> selected选择框,选中情况
	<td>住址:</td> //th:text -> 用于显示; th:value -> 用于存值. 
	<td><select name="city.id"> //th:selected -> 回显对象person.city.id和遍历city.id相同,则选中
		<option th:each="city : ${cityList}" th:value="${city.id}" th:text="${city.name}"
				th:selected="${null!=person}?${person.city.id}==${city.id}"></option>
	</select></td>

	<td><select name="city.id"> //配合使用 th:object
		<option th:each="city : ${cityList}" th:object="${city}" th:value="*{id}" th:text="*{name}"
				th:selected="${null!=person}?${person.city.id}==*{id}"></option>
	</select></td>
	
#th:if -> 条件判断
	<p th:if="${! #strings.isEmpty(msg)}" th:text="${msg}"></p> //msg不为空,则显示<p>
	
	///th:if="${xx}" 表达式为 true 的各种情况:
		boolean xx =true; int xx !=0; character xx !=0; 
		String xx !="false","off","no";
		If xx is not a boolean, a number, a character or a String
	
#th:switch -> 多路选择,配合使用 th:case 
	<div th:switch="${user.role}">
		<p th:case="'admin'">管理员</p>
		<p th:case="#{roles.manager}">管理者</p>
		<p th:case="*">其他</p> //默认值
	</div>
	
#th:with -> 变量赋值运算 
	<div th:with="first=${persons[0]}"> //th:with="x=${y}"
		<p>	The name of the first person is <span th:text="${first.name}">Julius Caesar</span>.</p>
	</div>
	
#th:attr -> 设置标签属性,多个属性用逗号分隔
	th:attr="src=@{/image/aa.jpg},title=#{logo}" //一般用于自定义标签
	
#th:remove -> 删除某个属性
	<tr th:remove="all"> //all:删除包含标签和所有的孩子
	
#th:errors
	数据校验相关; 用于显示数据校验的错误信息.

#日期格式化
	<input type="text" name="birth" placeholder="birth day"
		th:value="${#dates.format(emp.birth, 'yyyy-MM-dd HH:mm:ss')}">
		
		// 格式化日期,默认以浏览器默认语言为格式化标准
		${#dates.format(key)}
		${#dates.format(key,'yyy/MM/dd')}
		
		// 按照自定义的格式做日期转换.(取值年月日)
		${#dates.year(key)}
		${#dates.month(key)}
		${#dates.day(key)}
		
#字符串相关
	1.调用内置对象一定要用#
	2.大部分的内置对象都以s结尾: strings, numbers, dates
		// 判断字符串是否为空,如果为空返回 true,否则返回 false
		${#strings.isEmpty(key)}
		
		// 判断字符串是否包含指定的子串,如果包含返回 true,否则返回 false
		${#strings.contains(msg, 'T')}
		
		// 判断当前字符串是否以子串开头,如果是返回 true,否则返回 false
		${#strings.startsWith(msg, 'a')}
		
		// 判断当前字符串是否以子串结尾,如果是返回 true,否则返回 false
		${#strings.endsWith(msg, 'a')}
		
		// 返回字符串的长度
		${#strings.length(msg)}
		
		// 查找子串的位置,并返回该子串的下标,如果没找到则返回-1
		${#strings.indexOf(msg, 'h')}
		
		// 截取子串,用法与 jdk String 类下 SubString 方法相同
		${#strings.substring(msg, 13)}
		${#strings.substring(msg, 13, 15)}
		
		// 字符串转大小写
		${#strings.toUpperCase(msg)}
		${#strings.toLowerCase(msg)}
	
#页面公共元素抽取
	1.抽取公共片段
		<footer th:fragment="copy">
			&copy; 2011 The Good Thymes Virtual Grocery
		</footer>
		
	2.语法
		~{templatename::selector} //模板名::选择器
		~{templatename::fragmentname} //模板名::片段名
		//对于一般写法可以省略 ~{}; 但是,对于行内写法不可省! [[~{x::y}]] 或 [(x::y)]
		
	3.不同引入
		<div th:insert="footer :: copy"></div>	//footer为文件名: footer.html

		<div th:replace="footer :: copy"></div>

		<div th:include="footer :: copy"></div>//已过时
		
	4.不同效果
		<div> //insert - div内层才是公共元素
			<footer>
				&copy; 2011 The Good Thymes Virtual Grocery
			</footer>
		</div>
		
		<footer> //replace - 公共元素替换div
			&copy; 2011 The Good Thymes Virtual Grocery
		</footer>
		
		<div> //include - 公共元素
			&copy; 2011 The Good Thymes Virtual Grocery
		</div>
		
#引入页面公共元素时传参(两种方式)
	1.引用fragment时传入'参数名和参数值'
		<div th:replace="comm/bar :: sidebar(activeUri='main.html')"></div> //主页面
	
		<div th:replace="comm/bar :: sidebar(activeUri='logDir')"></div> //目录页面

	2.定义fragment时指定'参数名'
		<nav th:fragment="sidebar(activeUri)">
		
	2.引用fragment时传入'参数值'
		<div th:replace="comm/bar :: sidebar('main.html')"></div>
		
		<div th:replace="comm/bar :: sidebar('logDir')"></div>
	
	3.都是根据传入参数值改变a的class属性
		<a th:href="@{/main.html}" //activeUri为 'main.html' 时,高亮
			th:class="${activeUri=='main.html'?'nav-link active':'nav-link'}">
	
		<a th:href="@{/logDir}" //activeUri为 'logDir' 时,高亮
			th:class="${activeUri=='logDir'?'nav-link active':'nav-link'}">>
			
#域对象操作
	0.HttpServletRequest
		// request.setAttribute("req", "hello");
		Request: <span th:text="${#httpServletRequest.getAttribute('req')}"></span><br/>
		
	1.HttpSession
		// request.getSession().setAttribute("sess", "world");
		Session: <span th:text="${session.sess}"></span><br/>
		
	2.ServletContext
		// request.getServletContext().setAttribute("app", "java");
		Application: <span th:text="${application.app}"></span>
	
//}

//{-----------<<<<Maven>>>------------------------------------------------------------------
	'约定>配置>编码' -> 能用配置解决的问题就不编码,能基于约定的就不进行配置
	
#基本概念
	0.什么是构建???
		构建并不是创建,创建一个工程并不等于构建一个项目.
		构建是以 Java源码; 框架配置文件; JSP页面; html; 图片...等静态资源作为'原材料',
		去'生产'出一个可以运行的项目的过程.
		
	1.pom(Project Object Model,项目对象模型)
		将Java工程'project'的相关信息封装为对象'object',作为便于操作和管理的模型'model'. Maven工程的核心配置.

	2.mvn命令
		///与项目构建相关的命令,必须切换到'pom.xml'同级目录
		mvn clean			//删除以前的编译结果,为重新编译做准备
		mvn compile			//编译主程序
		mvn test-compile	//编译测试程序
		mvn test			//执行测试
		mvn package			//打包
		mvn install			//将打包的结果(jar/war)安装到本地仓库中
		mvn site			//生成站点
		
	3.依赖的范围
		compile: //主程序(√);测试程序(√);打包(√);部署(√); 如: spring-core
		provied: //主程序(√);测试程序(√);打包(x);部署(x); 如: servlet-api(tomcat提供)
		test:	 //主程序(x);测试程序(√);打包(x);部署(X); 如: junit

	4.依赖的传递
		A依赖B,B依赖C, A能否使用C呢???
		//非compile范围的依赖不能传递,必须在有需要的工程中单独加入
		
	5.依赖的排除
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
			<exclusions>
				<exclusion>
					<artifactId>spring-boot-starter-logging</artifactId> //排除'sp默认logback包'
					<groupId>org.springframework.boot</groupId>
				</exclusion>
			</exclusions>
		</dependency>
		
	6.统一声明版本号
		<properties>
			<activemq.version>5.15.4</activemq.version> //声明
		</properties>

		<dependency>
			<groupId>org.apache.activemq</groupId>
			<artifactId>activemq-amqp</artifactId>
			<version>${activemq.version}</version> //使用
		</dependency>

	7.仓库种类
		本地仓库: 本机电脑上的 Maven 仓库.
		私服仓库: 架设在本地局域网内的 Maven 仓库, 直连中央仓库.
		中央仓库: 架设在 Internet 上,为全世界所有 Maven 工程服务.

#下载配置
	https://maven.apache.org/download.cgi //配置环境变量,cmd命令验证'mvn -version'
	
	//配置eclipse
	本地mvn: window - preferences - maven - installations - add - External - 本地maven路径
		
	mvn仓库: window - preferences - maven - user_settings - add 
		Global_Settings 和 User_Settings 都选择本地mvn的"settings.xml"文件
	
#配置mvn ///配置文件'settings.xml'
	1.配置本地仓库
		<localRepository>D:\apache-maven-3.3.9-repo</localRepository> 
		
	2.配置阿里云镜像加速下载
		<mirror> 
			<id>alimaven</id>
			<name>aliyun maven</name> 
			<url>http://maven.aliyun.com/nexus/content/groups/public/</url> 
			<mirrorOf>central</mirrorOf> 
		</mirror> 
		
	3.配置下载jar包源码和文档
		//相应jar包或整个项目,右键'Maven->Download_Sources/javaDoc',即可下载
		<profile>  
			<id>downloadSources</id>
			<properties>  
				<downloadSources>true</downloadSources>  
				<downloadJavadocs>true</downloadJavadocs>             
			</properties>  
		</profile>
		
		<activeProfiles>
			<activeProfile>downloadSources</activeProfile>  
		</activeProfiles>
		
	4.配置mvn私服
		<profile>  
		  <id>nexus</id>  
		  <repositories> //<!--私服库地址-->
			<repository>  
			  <id>central</id>
			  <url>http://192.168.102.20:8081/nexus/content/groups/public</url>  
			  <releases><enabled>true</enabled></releases>  
			  <snapshots><enabled>true</enabled></snapshots>  
			</repository>  
		  </repositories>  
		 <pluginRepositories> //<!--插件库地址-->
			<pluginRepository>  
			  <id>central</id>  
			  <url>http://maven.com:8081/nexus/content/groups/public</url>  
			  <releases><enabled>true</enabled></releases>  
			  <snapshots><enabled>true</enabled></snapshots>  
			</pluginRepository>  
		  </pluginRepositories>  
		</profile>
		
#配置nexus
	0.阿里云仓库
		以用户名 "admin" 密码 "admin123", 登陆 http://localhost:8081/nexus
	
		主界面 -> Add... -> Proxy Repository
		Repository ID: aliyun
		Repository Name: Aliyun Repository
		Remote Storage Location: http://maven.aliyun.com/nexus/content/groups/public/
	
		选中仓库组"Public Repositories" -> Configuration
		把"Aliyun Repository"从右侧移到左侧, 并拖到"Central"上边
		这样,就可以优先访问阿里云仓库了.
		
	1.加入第三方jar
		<server> //(0).配置mvn
		  <id>thirdparty</id>
		  <username>admin</username>
		  <password>admin123</password>
		</server>
		
		//(1).使用cmd命令将第三方包加入私服
		mvn deploy:deploy-file -DgroupId=com.bluecard -DartifactId=wxpay-sdk-0.0.3 -Dversion=0.0.3 -Dpackaging=jar -Dfile=G:\wxpay-sdk-0.0.3.jar -Durl=http://192.168.102.20:8081/nexus/content/repositories/thirdparty/ -DrepositoryId=thirdparty
		//其中, -DgroupId="随意指定"; -DartifactId="第三方jar包名"; -Dversion="版本号"; -Dfile="jar所在本地路径"; -Durl="私服路径"

		//(2).项目pom文件添加引用
		<dependency>
		  <groupId>com.bluecard</groupId> //同上文 -DgroupId
		  <artifactId>wxpay-sdk-0.0.3</artifactId> //同上文 -DartifactId
		  <version>0.0.3</version> //同上文 -Dversion
		</dependency>
		
#mvn项目之间的关系
	1.依赖关系
		使用标签<dependency>把另一个项目的 jar 引入到当前项目
		自动下载另一个项目所依赖的其他项目
		
	2.继承关系
		pom类型表示逻辑父项目,只要一个项目有子项目,则它必须是 pom 类型
		父项目必须是 pom 类型. 如果子项目(jar/war)还是其他项目的父项目,子项目也必须是 pom 类型.
		
			//(1).继承-父项目,pom.xml 中看不到有哪些子项目(只在逻辑上具有父子关系).
			
			//(2).继承-子项目,出现<parent>标签. GV标签同父项目,即可省.
			<parent>
				<groupId>com.example</groupId>
				<artifactId>parent</artifactId>
				<version>0.0.1-SNAPSHOT</version>
			</parent>
			//<!-- <groupId>com.example</groupId> -->//可省
			<artifactId>child</artifactId>
			//<!-- <version>0.0.1-SNAPSHOT</version> -->//可省
		
	3.聚合关系(分布式项目推荐)
		前提是继承关系.父项目会把子项目包含到父项目中.
		新建聚合项目的子项目时,点击父项目右键新建 "Maven Module",而不是 "maven project".
		
			//(1).聚合-父项目,可在 pom.xml 中查看所有子项目.
			<modules>
				<module>child-module</module>
			</modules>
		
			//(2).聚合-子项目,可在 pom.xml 中查看父项目.
			<parent>
				<groupId>com.example</groupId>
				<artifactId>parent</artifactId>
				<version>0.0.1-SNAPSHOT</version>
			</parent>
			<artifactId>child-module</artifactId>
	
	4.二者意义和区别
		意义: 统一管理各个子项目的依赖版本.(子项目GV默认继承自父项目)
		区别: (1).聚合项目 可在父项目的 pom.xml 中查看所有子项目.
			  (2).'继承'必须得先install父项目,再install子项目; '聚合'则可以直接install子项目.
	
	5.依赖管理
		将父项目中的<dependencies>和<plugin>,用<dependencyManagement>和<pluginManagement>管理起来.
		
			//(1).父项目中,声明所有可能用到的jar; 再使用<properties>抽取版本,方便集中管理.
			<properties>
				<spring-version>4.1.6.RELEASE</spring-version>//自定义标签
			</properties>
		
			<dependencyManagement>
				<dependencies>
					<dependency>
						<groupId>org.springframework</groupId>
						<artifactId>spring-webmvc</artifactId>
						<version>${spring-version}</version>//引用自定义标签
					</dependency>
				</dependencies>
			</dependencyManagement>
		
			//(2).子项目中,也不是立即引用,也得写GAV,不过<Version>继承自父项目,即可省.
			<dependencies>
				<dependency>
					<groupId>org.springframework</groupId>
					<artifactId>spring-webmvc</artifactId>
				</dependency>
			</dependencies>
			
#资源拷贝插件
	//mvn默认只把 src/main/resources 里的非java文件编译到classes中
	//如果希望 src/main/java 下的文件(如mapper.xml)也被编辑到 classes 中,在 pom.xml 中配置
        <resources>
            <resource>
                <directory>src/main/java</directory>
            </resource>
            <resource>
                <directory>src/main/resources</directory>
            </resource>
        </resources>	
	
#新建war项目
	(1).创建 maven project 时, packaging 选择 war.
	(2).在 webapp 文件夹下新建"META-INF"和"WEB-INF/web.xml"
	(3).在 pom.xml 中添加 javaEE 相关的三个 jar
		<dependency>
			<groupId>javax.servlet</groupId>
			<artifactId>javax.servlet-api</artifactId>
			<version>3.0.1</version>
			<scope>provided</scope>//依赖的范围(详见1.3.)
		</dependency>
		<dependency>
			<groupId>javax.servlet.jsp</groupId>
			<artifactId>jsp-api</artifactId>
			<version>2.2</version>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>jstl</groupId>
			<artifactId>jstl</artifactId>
			<version>1.2</version>
		</dependency>
		
	(4)使用 tomcat 插件,而非本地tomcat. 可实现不同项目发布到不同的tomcat,端口号不能相同.
		<plugin>
			<groupId>org.apache.tomcat.maven</groupId>
			<artifactId>tomcat7-maven-plugin</artifactId>
			<version>2.2</version>
			<configuration>
				//本地启动时项目的端口号; 热部署到远程服务器则不起作用,以远程tomcat端口号为准
				<port>8099</port> 
				
				//项目发布到 tomcat 后的名称,只写'/'则名称为 ROOT
				//测试tomcat http://localhost:8080/ 其访问的是tomcat的ROOT项目
				<path>/hello</path>
			</configuration>
		</plugin>
		
	(5).项目启动: 右键项目 --> run as --> maven build-->Goals中输入 "clean tomcat7:run"
	
#热部署(远程部署)
	(1).修改 tomcat/conf/tomcat-users.xml 添加角色,然后重启tomcat
		<role rolename="manager-gui"/> //图形界面角色
		<role rolename="manager-script"/> //脚本角色
		<user username="tomcat" password="tomcat" roles="manager-gui,manager-script"/>

	(2).在 pom.xml 中 tomcat 插件的<configuration>里配置
		</configuration>
			//...
			<username>tomcat</username>
			<password>tomcat</password>
			<url>http://192.168.8.8:8080/manager/text</url>
		</configuration>

	(3).右键项目--> run as --> maven build(以前写过,选择第二个) -->输入
		tomcat7:deploy(第一次发布); tomcat7:redeploy(非第一次发布).
	
//}
	
//{-----------<<<SonarQube>>>---------------------------------------------------------------
	'代码质量管理平台,可以快速的定位代码中潜在的或者明显的错误'
	
#下载配置
	0.下载解压
		//其中汉化包plugins拷入 F:\sonarqube-7.3\extensions\plugins
		server: https://www.sonarqube.org/downloads/
		plugins: https://github.com/SonarQubeCommunity/sonar-l10n-zh/releases
		client(可省): https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner
	
	1.配置文件  F:\sonarqube-7.3\conf\sonar.properties
		//其中,数据库默认使用内置H2,推荐使用mysql. 新建mysql数据库'sonarqube'
		sonar.web.host=0.0.0.0
		sonar.web.port=9000
		sonar.login=admin
		sonar.password=admin
		sonar.jdbc.username=bluecardsoft
		sonar.jdbc.password=#$%_BC13439677375
		sonar.jdbc.url=jdbc:mysql://192.168.8.7:33306/sonarqube?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true&useConfigs=maxPerformance&useSSL=false
		#sonar.web.context=/your_prefix  //非必须,若要在访问sonarqube时加上统一前缀则配置此项

	2.启动服务
		启动脚本: "F:\sonarqube-7.3\bin\windows-x86-64\StartSonar.bat"
		cmd验证: 屏幕最后出现"xxx SonarQube is up"
		web验证: 默认用户名密码admin, 连接 http://localhost:9000
		
		停止服务: 命令行Ctrl+C 或 kill端口'netstat -aon | findstr 9000 ===> taskkill -f /pid xxx'
		异常日志: "F:\sonarqube-7.3\logs\sonar.log"
	
	3.分析项目
		项目-->分析新项目-->新建令牌(admin)-->待测项目的pom同级目录执行以下命令
		mvn sonar:sonar \
		  -Dsonar.host.url=http://localhost:9000 \
		  -Dsonar.login=ea23d2ae8d458cf020f8028b7f2b32fca909c83f	
	
	4.idea插件
		解压安装包,将'SonarLint'文件夹拷贝至'idea安装目录/plugins'
		idea->settings->plugins->Install plugin from disk,选中'sonarlint-intellij-4.0.0.2916.jar'
		
		//重启idea,完成安装. 以下进行配置sona:
		idea->File-->Settings-->Other Settings-->SonarLint General Settings
	
	10.配置Client /*可省*/
		配环境变量: name=SONAR_HOME, value=F:\sonar-scanner-3.2.0.1227-windows
		path前添加: %SONAR_HOME%\bin; cmd验证: 'sonar-scanner -v'
		
		//在分析项目demo的根目录下新建文件 F:\sp_project\demo\sonar-project.properties
		sonar.projectKey=TGB-demo
		sonar.projectName=demo
		sonar.projectVersion=1.0
		sonar.sources=src
		sonar.language=java
		sonar.sourceEncoding=UTF-8
		sonar.java.binaries=F:/sp_project/demo/target/classes

		切换到分析项目demo根目录 "F:\sp_project\demo", 
		使用命令分析项目: 'F:\sonar-scanner-3.2.0.1227-windows\bin\sonar-scanner.bat'
		打开web,查看分析结果: http://localhost:9000/
	
#常见问题及修改 
	https://blog.csdn.net/happyzwh/article/details/77991095
	https://www.jianshu.com/p/b50f01eeba4d
	
	//(1).Make this class field a static final constant or non-public and provide accessors if needed.
		//类变量不应该有公开访问权限
		public int count; ---> private int count;
		
	//(2).The user-supplied array 'objArray' is stored directly
		//数组类型不能直接保存
		public Object[] getObjArray() {
			return objArray;
		}

		public void setObjArray(Object[] objArray) {
			this.objArray = objArray;
		}

		//修改后
		public Object[] getObjArray() {
			return null != objArray ? Arrays.copyOf(objArray, objArray.length) : null;
		}

		public void setObjArray(Object[] objArray) {
			this.objArray = null != objArray ? Arrays.copyOf(objArray, objArray.length) : null;
		}
		
	//(3).Rename this constant name to match the regular expression '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
		//常量名应该遵守命名规则
		protected static final String HomophoneRule="Homophone"; ---> HOMOPHONE_RULE="Homophone";
		
	//(4).Rename this method name to match the regular expression '^[a-z][a-zA-Z0-9]*$'
		//方法名命名规则
		private boolean ISpasstype(String type){} ---> iSpasstype(String type){}
	
	//(5).避免在循环体中声明创建对象
		for (int i = 0; i < 100; i++) {
			Object obj = new Object();
			System.out.println("obj=" + obj);
		}
		
		//修改后
		Object obj = null;
		for (int i = 0; i < 100; i++) {
			obj = new Object();
			System.out.println("obj=" + obj);
		}	
		
	//(6).Merge this if statement with the enclosing one.
		//合并可折叠的"if"语句
		if (flag0) { if (flag1) {  dosth... } }
		if (flag0 && flag1) {  dosth... } //修改后
		
	//(7).Variable 'bEnvInited' explicitly initialized to 'false' (default value for its type).
		//显示初始化不需要,boolean类型默认false ????????
		private static boolean bEnvInited = false; ---> private static boolean bEnvInited;
		
	//(8).Avoid concatenating nonliterals in a StringBuffer constructor or append().
		//低效的StringBuffer
		retStr.append("PNR："+locatorID); -> retStr.append("PNR："); retStr.append(locatorID);
		
	//(9).Avoid instantiating Integer objects. Call Integer.valueOf() instead.
		properties.put("CCSID", new Integer(req.getQueueCCSID()));
		properties.put("CCSID", Integer.valueOf(req.getQueueCCSID())); //修改后
	
	//(10).Consider simply returning the value vs storing it in local variable 'flightLineQueryForm'.
		//避免冗余本地变量
		public Date getDate() {
			Date date = new Date();
			return date;
		}
		
		public Date getDate() { //修改后
			return new Date();
		}

	//(11).Use isEmpty() to check whether the collection is empty or not.
		if (0 == imageSet.size()){} ---> if (imageSet.isEmpty()){}

	//(12).Move the "0" string literal on the left side of this string comparison.
		//在进行比较时,字符串文本应该放在左边
		if (str.equalsIgnoreCase("123")){} ---> if ("123".equalsIgnoreCase(str)){}
	
	//(13).String.indexOf(char) is faster than String.indexOf(String).
		int index = str.indexOf("s"); ---> int index = str.indexOf('s');
		
//}		

//{-----------<<<fastjson>>>-----------------------------------------------------------------
	<dependency>
		<groupId>com.alibaba</groupId>
		<artifactId>fastjson</artifactId>
		<version>1.2.47</version>
	</dependency>

	// QuoteFieldNames --> 输出key时是否使用双引号,默认为true
	// WriteMapNullValue --> 是否输出值为null的字段,默认为false
	// WriteNullListAsEmpty --> List字段如果为null,输出为[],而非null
	// WriteNullNumberAsZero --> 数值字段如果为null,输出为0,而非null
	// WriteNullStringAsEmpty --> 字符类型字段如果为null,输出为"",而非null (√)
	// WriteNullBooleanAsFalse --> Boolean字段如果为null,输出为false,而非null
	
	//list -> JSONString
	JSON.toJSONString(list, SerializerFeature.WriteNullStringAsEmpty); 
	
	//Demo
	JSONObject json = new JSONObject();
	System.out.println(json.getInteger("a")); //null
	System.out.println(json.getIntValue("a")); //0
	
	//X->String
	String json = JSON.toJSONString(list / map / bean);
	String json = JSON.toJSONString(list, true);//args1: json是否格式化(有空格和换行).
	
	//X->JSON (必须有get/set)
	JSONObject obj = JSON.parseObject(JSON.toJSONString(person));//javabean
	JSONObject obj = JSON.parseObject(JSON.toJSONString(map));//map
	JSONArray array = JSON.parseArray(JSON.toJSONString(list));//list

	//JSON->X (必须有空构造方法)
	Person person = JSON.parseObject(json, Person.class);
	Map map = JSON.parseObject(json, Map.class);
	List<Person> list = JSON.parseArray(json, Person.class);

//}
	
//{-----------<<<docker>>>-------------------------------------------------------------------
#abc
	一个开源的应用容器引擎; 一个轻量级容器技术!
	将软件做好配置; 编译成一个镜像; 将镜像发布出去; 其他使用者就可以直接使用这个镜像
	运行中的这个镜像称为容器,容器启动是非常快速的!
	
	主机(Host)		->	安装了Docker程序的机器 (Docker直接安装在操作系统之上)
	客户端(Client)	->	连接docker主机进行操作
	仓库(Registry)	->	用来保存各种打包好的软件镜像
	镜像(Images)	->	软件打包好的镜像;放在docker仓库中
	容器(Container)	->	镜像启动后的实例称为一个容器; 容器是独立运行的一个或一组应用

#Ubuntu安装Docker
	$ uname -a							//内核版本必须是3.10及以上
	$ apt-get install docker.io			//安装Docker -(可能存在权限错误,使用时添加 sudo 前缀)
	$ service docker status/start	//启动服务和守护进程
	$ docker version					//检测是否安装成功
	$ ln -sf /usr/bin/docker.io /usr/local/bin/docker	//创建软连接-(方便使用docker命令)
	
	1.权限问题
		#permission denied. Are you trying to connect to a TLS-enabled daemon without TLS?
		#注意: 默认情况,执行 docker 都需要运行 sudo 命令. 如何免去 sudo?
		sudo groupadd docker			//如果还没有 docker group 就添加一个
		sudo gpasswd -a ${USER} docker	//将用户加入该 group 内.然后退出并重新登录就生效啦
		sudo service docker restart		//重启 docker 服务
		newgrp - docker					//切换当前会话到新 group

#CentOS安装Docker
	$ yum install docker
	$ systemctl start/stop docker
	$ docker -v							//docker版本
	$ systemctl enable docker			//开机启动
	
#镜像下载:
	1).docker search x 或 https://hub.docker.com/explore/	//查找镜像
	2).docker pull x:y	//下载镜像,x为镜像名,y为版本号. (可用镜像加速 registry.docker-cn.com/library/x:y)
	3).

	去Docker仓库找到这个软件对应的镜像
	3).使用Docker运行这个镜像,这个镜像就会生成一个Docker容器
	4).对容器的启动停止就是对软件的启动停止	
	
	0.国内镜像
		https://registry.docker-cn.com //Docker 官方中国区
		http://hub-mirror.c.163.com //网易
		https://docker.mirrors.ustc.edu.cn //ustc
	
	1.修改镜像源地址
		//直接设置 –registry-mirror 参数,仅对当前的命令有效 
		docker run hello-world --registry-mirror=https://docker.mirrors.ustc.edu.cn
		
		//修改 /etc/default/docker,加入 DOCKER_OPTS=”镜像地址”，可以有多个 
		DOCKER_OPTS="--registry-mirror=https://docker.mirrors.ustc.edu.cn"

		//支持 systemctl 的系统,通过 sudo systemctl edit docker.service
		//会生成 etc/systemd/system/docker.service.d/override.conf 覆盖默认的参数,在该文件中加入如下内容
		[Service] 
		ExecStart= 
		ExecStart=/usr/bin/docker -d -H fd:// --registry-mirror=https://docker.mirrors.ustc.edu.cn
		
		//新版的 Docker 推荐使用 json 配置文件的方式,默认为 /etc/docker/daemon.json
		//非默认路径需要修改 dockerd 的 –config-file，在该文件中加入如下内容
		{"registry-mirrors": ["https://docker.mirrors.ustc.edu.cn"]}

#docker命令:
	docker search 关键字		-> //检索软件的相关信息(eg: docker search redis)
	docker pull 镜像名:tag		-> //版本号tag可选,默认为最新版latest
	docker images				-> //查看本地所有镜像及id
	docker rmi 镜像id			-> //删除指定的本地镜像
	
	docker stop/start/rm 容器id	-> //停止/启动/移除 运行中的容器
	docker rm 容器id			-> //移除容器(一定要是停止状态); rm->移除容器; rmi->移除镜像!
	docker ps					-> //查看运行中的容器
	docker ps ‐a				-> //查看所有的容器
	
	docker logs 容器名/容器id	-> //查看容器日志
	
	docker run ‐‐name mytomcat ‐d tomcat:latest		-> //根据镜像启动容器
	
	//‐d: 后台运行;‐p: 端口映射(主机端口:容器内部的端口); --name:容器别名
	docker run ‐d ‐p 8888:8080 tomcat		->	//启动一个做了端口映射的tomcat
	
	//正确启动mysql镜像,并做了端口映射
	docker run ‐p 3306:3306 ‐‐name mysql02 ‐e MYSQL_ROOT_PASSWORD=123456 ‐d mysql
	
	//把主机的 /conf/mysql 文件夹挂载到 mysqldocker容器的 /etc/mysql/conf.d 文件夹里面
	//以后mysql的配置可以直接在 自定义文件夹下(/conf/mysql)更改
	docker run ‐‐name mysql03 ‐v /conf/mysql:/etc/mysql/conf.d ‐e MYSQL_ROOT_PASSWORD=my‐secret‐pw ‐d mysql:tag

	//指定mysql的一些配置参数
	docker run ‐‐name mysql04 ‐e MYSQL_ROOT_PASSWORD=my‐secret‐pw ‐d mysql:tag ‐‐character‐set‐server=utf8mb4 ‐‐collation‐server=utf8mb4_unicode_ci
		
#更多命令: https://docs.docker.com/engine/reference/commandline/docker/
		
//}

//{-----------<<<HttpClient>>>---------------------------------------------------------------
#pom文件
        //<!-- HttpClient -->
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpclient</artifactId>
        </dependency>

#GET/POST区别
	超链接<a/>	---> ///只能用 GET 提交HTTP请求
	表单</form>	---> ///可以用 GET,POST .......

	GET			---> ///参数只能在请求行(request-line)
	POST		---> ///参数可在请求行,亦可在请求体(request-body)

#GET -> request-line
	1.两种方式获取HttpGet
		#(1).直接将参数拼接在 URI 之后
		String uri = "http://127.0.0.1:8090/demo/http/get?name=中国&age=70";
		HttpGet httpGet = new HttpGet(uri);
		
		#(2).通过 URIUtils 工具类生成带参数的 URI
		String param = "name=中国&age=70";
        // String param = "name=" + URLEncoder.encode("中国", "UTF-8") + "&age=70"; //中文参数,encode
        URI uri = URIUtils.createURI("http", "127.0.0.1", 8090, "/demo/http/get", param, null);
        HttpGet httpGet = new HttpGet(uri);
		
	2.执行请求获取结果
		@Test
		public void doGet() {
			String uri = "http://127.0.0.1:8090/demo/http/get?name=中国&age=70";
			HttpGet httpGet = new HttpGet(uri); //组装请求-GET
			// HttpPost httpPost = new HttpPost(uri); //组装请求-POST

			try (CloseableHttpResponse httpResponse = HttpClients.createDefault().execute(httpGet)) { //发送请求
				if (null != httpResponse && HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()) {
					String res = EntityUtils.toString(httpResponse.getEntity(), "UTF-8"); //获取结果
					System.out.println(res);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
#POST -> request-line
	1.两种方式获取httpPost //(同上)
		#(1).拼接字符串
        String uri = "http://127.0.0.1:8090/demo/http/post?name=中国&age=70";
        HttpPost httpPost = new HttpPost(uri);
		
		#(2).工具类 URIUtils
		String param = "name=中国&age=70";
        // String param = "name=" + URLEncoder.encode("中国", "UTF-8") + "&age=70"; //中文参数,encode
        URI uri = URIUtils.createURI("http", "127.0.0.1", 8090, "/demo/http/post", param, null);
        HttpPost httpPost = new HttpPost(uri);

#POST -> request-body -> keyValue
	1.POST表单
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("name", "中国"));
        nvps.add(new BasicNameValuePair("age", "70"));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps, "UTF-8"); //中文乱码

        HttpPost httpPost = new HttpPost("http://127.0.0.1:8090/demo/http/post");
        httpPost.setEntity(entity);
		
    2.查看HTTP数据
        System.out.println(entity.getContentType()); //Content-Type: application/x-www-form-urlencoded; charset=UTF-8
        System.out.println(entity.getContentLength()); //30
        System.out.println(EntityUtils.toString(entity)); //name=%E4%B8%AD%E5%9B%BD&age=70

#POST -> request-body -> json
	1.中文乱码
        String json = "{\"name\":\"中国\",\"age\":\"70\"}";
        StringEntity entity = new StringEntity(json, "UTF-8"); //中文乱码,默认"ISO-8859-1"
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");//设置contentType --> json

        HttpPost httpPost = new HttpPost("http://127.0.0.1:8090/demo/http/postBody");
        httpPost.setEntity(entity);

#POST -> request-body -> file
	1.pom文件
        //<!-- HttpClient-File -->
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpmime</artifactId>
        </dependency>
		
	2.中文乱码
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        ContentType contentType = ContentType.create("text/plain", "UTF-8"); //中文乱码,默认"ISO-8859-1"
        builder.addTextBody("fileName", "中国", contentType);
        builder.addBinaryBody("file", new File("C:\\Users\\BlueCard\\Desktop\\StatusCode.png"));
        HttpEntity entity = builder.build();

        HttpPost httpPost = new HttpPost("http://127.0.0.1:8090/demo/http/postFile");
        httpPost.setEntity(entity);
	
	3.前台页面	
		<form action="http://127.0.0.1:8090/demo/http/postFile" method="POST" enctype="multipart/form-data">  
			<input type="text" name="fileName" value="中国"/>  
			<input type="file" name="file"/>  
			<inupt type="submit" value="提交"/>  
		</form>  
	
	

//}


	
	
	
	
	
	
	
	
	
	
	
	
	
	