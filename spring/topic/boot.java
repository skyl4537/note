	http://start.spring.io/
	
//{--------<<<基础概念>>>------------------------------------------------------------------
#Spring Boot并不是对 Spring 功能上的增强,而是提供了一种快速使用 Spring 的方式.

#boot优势
	简化依赖管理
		将各种功能模块进行划分,封装成一个个启动器(Starter), 更容易的引入和使用
		提供一系列的Starter,将各种功能性模块进行了划分与封装
		更容易的引入和使用,有效避免了用户在构建传统Spring应用时维护大量依赖关系而引发的jar冲突等问题

	自动化配置 //为每一个Starter都提供了自动化的java配置类
	嵌入式容器 //嵌入式tomcat,无需部署war文件
	监控の端点 //通过Actuator模块暴露的http接口,可以轻松的了解和控制 Boot 应用的运行情况
	
#Boot启动器(jar包集合,一共44个)
	spring-boot-starter-web		//支持全栈式的 web 开发,包括 tomcat 和 SpringMVC 等jar包
	spring-boot-starter-jdbc	//支持 Spring 以 jdbc 方式操作数据库的jar包的集合
	spring-boot-starter-redis	//支持 redis 键值存储的数据库操作
	
//}
	
//{--------<<<2.x注意点>>>-----------------------------------------------------------------
#不重新打包的前提下,修改配置文件
	1.打包直接执行
		<plugin>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-maven-plugin</artifactId>
			
			//此配置将使得 jar/war 包在linux环境下可直接执行
			//勿需命令 jar -jar *.jar, 直接 ./*.jar 即可执行
			<configuration> 
				<executable>true</executable>
			</configuration>
		</plugin>
		
	2.可执行jar包存在的弊端
		以上设置,虽可在linux下直接执行, 但在不重新打包前提下修改配置文件,则做不到.		

#匹配带后缀url访问
	@Configuration
	public class MyWebMvcConfigurer implements WebMvcConfigurer {
		@Override
		public void configurePathMatch(PathMatchConfigurer configurer) {
			//boot2.x默认将'/test'和'/test.do'作为2个url
			configurer.setUseRegisteredSuffixPatternMatch(true); //true,统一以上两个url
		}
	}
	
	@Bean
	public ServletRegistrationBean servletRegistrationBean(DispatcherServlet dispatcherServlet) {
		ServletRegistrationBean<DispatcherServlet> bean = new ServletRegistrationBean<>(dispatcherServlet);
		
		bean.addUrlMappings("*.do"); //拦截'.do'结尾的url
		return bean;
	}

//}

//{--------<<<启动>>>----------------------------------------------------------------------
#两种启动
	1.脚本启动
		#!/bin/bash
		PID=$(lsof -t -i:8090)
		
		if [ $PID ]
		then
			kill -9 $PID
			echo "kill -9 port 8090 PID: $PID"
		else
			echo "8090 NO PID!"
		fi

		cd /var/tmp
		chmod 777 demo.jar
		nohup jdk1.8.0_191/bin/java -jar demo.jar >/dev/null 2>&1 &
		echo "start OK!~!"

	2.linux服务启动
		//pom.xml设置
		<plugin>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-maven-plugin</artifactId>
			<configuration>
				<executable>true</executable> //可执行,必不可少. 将导致jar包不可修改
			</configuration>
		</plugin>
	
		//将jar包部署到linux, 并赋予可执行权限
		chmod +x /var/tmp/blue/demo.jar
		
		//将jar包软连接到 /etc/init.d 目录. 其中, /etc/init.d/demo 结尾 demo 为该服务的别名
		ln -s /var/tmp/blue/demo.jar /etc/init.d/demo
		
		//通过linux服务命令形式 启动/关闭/重启/查询 该服务
		service demo start|stop|restart|status
			
		//该服务日志默认的存储路径: /var/log/demo.log
		//使用自定义 *.conf 更改默认配置, jar包同路径下新建配置文件 demo.conf
		JAVA_HOME=/usr/jdk1.7.0_79/bin
		JAVA_OPTS=-Xmx1024M
		LOG_FOLDER=/var/tmp/blue/logs/		//该目录必须存在

//}

//{--------<<<login>>>---------------------------------------------------------------------
#webjars -> 将前端资源(js,css等)打成jar包,使用Maven统一管理. http://www.webjars.org/
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>webjars-locator</artifactId> //页面引用时,可省略版本号.(如 3.3.1)
            <version>0.32</version>
        </dependency>
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>bootstrap</artifactId>
            <version>3.3.7-1</version>
        </dependency>
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>jquery</artifactId>
            <version>3.3.1</version>
        </dependency>
	
#前台表单
	<!DOCTYPE html>
	<html lang="en" xmlns:th="http://www.thymeleaf.org"> //声明 thymeleaf
	<head>
		<meta charset="UTF-8">
		<title>login</title>
		<link rel="shortcut icon" th:href="@{/favicon.ico}"/> //小叶子,存放目录 /static
		
		//webjars-locator: 页面引用时,可省略版本号.(如 3.3.1)
		//省略前: <script th:src="@{/webjars/jquery/3.3.1/jquery.min.js}"></script>
		//....后: <script th:src="@{/webjars/jquery/jquery.min.js}"></script>

		<script th:src="@{/webjars/jquery/jquery.min.js}"></script>
		<script th:src="@{/webjars/bootstrap/js/bootstrap.min.js}"></script>
		<link rel="stylesheet" th:href="@{/webjars/bootstrap/css/bootstrap.min.css}"/>
	</head>	
	<body>
		<form method="post" th:action="@{/login}"> //表单提交: post + action
			<table align="center">
				<tr><td>账户: <input type="text" th:name="name" th:value="${name}"></td></tr> //${name} -> 无值则显示""
				<tr><td>密码: <input type="password" th:name="pwd"></td></tr>
				<tr>
					<td align="center">
						<button class="btn btn-primary" type="submit">登录</button>
						<p th:if="${! #strings.isEmpty(msg)}" th:text="${msg}"></p> //th:if -> msg为空则不显示
					</td>
				</tr>
			</table>
		</form>
	</body>
	
#后台逻辑
	@PostMapping("/login")
	public String login(@RequestParam String name, @RequestParam String pwd, HttpSession session, Model model) {
		if (!StringUtils.isEmpty(pwd)) {
			session.setAttribute("user", name); //保存Session,用于登陆验证
			return "redirect:/emp/emps"; //重定向到接口 --> 防止表单重复提交! ---> 中间不能有空格!!

		} else {
			model.addAttribute("name", name); //表单回显
			model.addAttribute("msg", "用户名或密码不正确!");
			return "/login"; //转发到页面: /templates/login.html
		}
	}

#登陆拦截
	public class LoginInterceptor implements HandlerInterceptor {

		@Override //在目标方法之前被调用 ---> 适用于权限,日志,事务等.
		public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
			if (null != request.getSession().getAttribute("user")) {
				return true; //有Session,则继续调用后续的拦截器和目标方法; 没有,则转发到登录页
			} else {
				request.getRequestDispatcher("/").forward(request, response);
				return false;
			}
		}
	}
	
#注册拦截器
	@Configuration
	public class MyWebMvcConfigurer implements WebMvcConfigurer {

		@Override //静态资源映射
		public void addResourceHandlers(ResourceHandlerRegistry registry) {
			//url访问: x/logs/demo.log --> 类路径/resources/log/demo.log (或) war包所在目录/logs/demo.log
			registry.addResourceHandler("/logs/**")
					.addResourceLocations("classpath:/log/", "file:logs/");
		}

		@Override //视图映射
		public void addViewControllers(ViewControllerRegistry registry) {
			//url访问: ip:port/demo/ --> 对应资源: /templates/login.html
			registry.addViewController("/").setViewName("/login");
		}

		@Override //注册拦截器
		public void addInterceptors(InterceptorRegistry registry) {
			registry.addInterceptor(new LoginInterceptor())
					.addPathPatterns("/**")
					.excludePathPatterns("/webjars/**", "/*.html", "/img/**", "/logs/**") //非拦截: 静态资源
					.excludePathPatterns("/", "/login"); //非拦截: 登陆接口
		}
	}
	
#静态资源映射
		classpath:/public/
		classpath:/resources/
		classpath:/static/
		classpath:/META-INFO/resouces/
		
	0.默认存放在以上目录的资源都可以直接访问
		classpath:/static/a.html		---> http://127.0.0.1:8090/demo/a.html
		classpath:/static/abc/c.html	---> http://127.0.0.1:8090/demo/abc/c.html
		classpath:/static/img/sql.png	---> http://127.0.0.1:8090/demo/img/sql.png
		
	1.自定义静态资源目录//代码版
		classpath:/log/demo.log			---> http://127.0.0.1:8090/demo/logs/demo.log
		jar包同级目录/logs/test.log		---> http://127.0.0.1:8090/demo/logs/test.log
		
		@Configuration
		public class MyWebMvcConfigurer implements WebMvcConfigurer {
			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				//url访问: x/logs/demo.log --> 类路径/resources/log/demo.log (或) war包所在目录/logs/demo.log
				registry.addResourceHandler("/logs/**")
						.addResourceLocations("classpath:/log/", "file:logs/");
			}
		}
		
	3.自定义静态资源目录//配置文件版
		spring.mvc.static-path-pattern=/logs/**
		spring.resources.static-locations=classpath:/log/,file:logs/
		//此配置会覆盖boot默认配置,即导致不能再访问 /static; /public/, /resources/等目录资源

//}

//{--------<<<CRUD>>>----------------------------------------------------------------------

	| 列表页面        | /emp/list	| GET      |
	| 跳转页面(新增)  | /emp		| GET      |
	| 新增接口        | /emp		| POST     |
	| 跳转页面(修改)  | /emp/{id}	| GET      |
	| 修改接口        | /emp		| PUT      |
	| 删除接口        | /emp/{id}	| DELETE   |
	
#POST转化为PUT,DELETE
	1.配置HiddenHttpMethodFilter. (boot已自动配置)
		<filter>
			<filter-name>HiddenHttpMethodFilter</filter-name>  
			<filter-class>org.springframework.web.filter.HiddenHttpMethodFilter</filter-class>  
		</filter>
		
	2.页面创建(POST表单 + 隐藏标签)
		<form method="post" th:action="@{/emp/}+${emp.id}">
			<input type="hidden" name="_method" value="delete"> //隐藏标签 name + value
			
			<a href="#" onclick="delEmp(this)" th:attr="url=@{/emp/}+${emp.id}">删除</a>
		</form>
	
#列表
	0.跳转列表页面
		<a th:href="@{/emp/list}">列表页面</a> //超链接对应请求 GET
		
	1.跳转逻辑
		@RequestMapping("/emp/list")
		public String list(Model model) {
			model.addAttribute("emplist", EmpUtils.listAll());
			return "/emp/list";
		}
		
	2.响应页面
		<table border="1" cellpadding="5" cellspacing="0" align="center">
			<tr>
				<th>姓名</th>
				<th>年龄</th>
				<th>城市</th>
				<th>操作</th>
			</tr>
			<tr th:if="${null==emplist || 0==emplist.size()}">
				<td colspan="4" th:text="列表为空"></td>
			</tr>
			<tr th:each="emp:${emplist}" th:object="${emp}"> //配合使用 th:object 和 *{...}
				<td th:text="*{name}"></td>
				<td th:text="*{gender?'男':'女'}"></td>
				<td th:text="*{city.name}"></td>
				<td colspan="2">
					<a th:href="@{/emp/}+*{id}">修改</a>
					<a href="#" onclick="delEmp(this)" th:attr="url=@{/emp/}+${emp.id}">删除</a>
				</td>
			</tr>
		</table>
	
#新增
	0.跳转新增页面
		<a th:href="@{/emp}">新增</a>
		
	1.跳转逻辑
		@RequestMapping("/emp")
		public String add(Model model) {
			model.addAttribute("cityList", EmpUtils.listCity()); //初始化列表 City
			return "/emp";
		}
		
	2.新增页面(同修改)
		
	3.新增接口
		@PostMapping("/emp")
		public String add(Emp emp) {
			EmpUtils.empList.add(emp);
			return "redirect:/emp/list";
		}
		
#修改
	0.跳转修改页面
		<a th:href="@{/emp/}+${emp.id}">修改</a> //路径拼接
		
	1.跳转逻辑
		@GetMapping("/emp/{id}")
		public String add(@PathVariable Integer id, Model model) {
			model.addAttribute("emp", EmpUtils.getById(id));
			model.addAttribute("cityList", EmpUtils.listCity());
			return "/emp";
		}
		
	2.回显数据修改页面
		//增加和修改使用同一页面,区分方式: 回显 emp 是否为空 --> ${null!=person}
		<form method="post" th:action="@{/emp}">
			//修改: PUT请求 + emp.id
			<input type="hidden" name="_method" value="put" th:if="${null!=emp}">
			<input type="hidden" name="id" th:if="${null!=emp}" th:value="${emp.id}">

			<table>
				<tr>
					<td>姓名:</td>
					<td><input type="text" name="name" th:value="${null!=emp}?${emp.name}"></td>
				</tr>
				<tr>
					<td>性别:</td> //th:checked --> radio标签是否选中.
					<td><input type="radio" name="gender" value="1" th:checked="${null!=emp}?${emp.gender}">男
						<input type="radio" name="gender" value="0" th:checked="${null!=emp}?${!emp.gender}">女
					</td>
				</tr>
				<tr>
					<td>住址:</td>
					<td><select name="city.id"> //th:selected --> 回显emp.city.id == 遍历city.id,则选中
						<option th:each="city:${cityList}" th:object="${city}" th:value="*{id}" th:text="*{name}"
								th:selected="${null!=emp}?${emp.city.id}==*{id}"></option>
					</select></td>
				</tr>
				<tr> //回显 emp 为空,则显示'新增'; 否则,显示'修改'.
					<td colspan="2"><input type="submit" th:value="${null!=emp}?'修改':'新增'"></td>
				</tr>
			</table>
		</form>
	
	3.修改接口
		@PutMapping("/emp")
		public String updateById(Emp emp) {
			EmpUtils.empList.update(emp);
			return "redirect:/emp/emps";
		}
	
#删除
	0.点击删除//(DELETE请求需要: <form/> + 隐藏标签)
		<a href="#" onclick="delEmp(this)" th:attr="url=@{/emp/}+${emp.id}">删除</a>

		<form id="delForm" method="post" action="#"> //独立于列表Table的<form/>
			<input type="hidden" name="_method" value="DELETE">
		</form>
		
		<script>
			function delEmp(e) {
				alert($(e).attr('url')); //当前按钮的'url'属性
				$('#delForm').attr('action', $(e).attr('url')).submit(); //动态设置<form>的action属性,并提交
				return false; //取消按钮的默认行为
			}
		</script>

	1.删除逻辑
		@DeleteMapping("/emp/{id}")
		public String delete(@PathVariable Integer id) {
			EmpUtils.empList.deleteById(id);
			return "redirect:/emp/list";
		}
		
	3.升级版 //不使用<form/>发送DELETE,使用ajax异步删除
		<a href="#" onclick="delEmp(this)" th:attr="url=@{/emp/}+*{id}">删除</a>
		
		<script>
			function delEmp(e) {
				$.ajax({
					type: 'DELETE', //仅部分浏览器支持
					url: $(e).attr('url'),
					dataType: 'text',
					success: function (data) { //请求成功,回调函数
						$(e).parent().parent().remove(); //--->动态删除<a/>所在的行
					},
					error: function (data) { //发生错误时调用
						var res = JSON.parse(data.responseText); //转化json
						alert(res.status + " + " + res.error + " + " + res.message);
					}
				});
				return false;
			}
		</script>
		
	4.ajax后台逻辑
		@DeleteMapping("/emp/{id}")
		@ResponseBody
		public String delete(@PathVariable Integer id) {
			EmpUtils.empList.deleteById(id);
			return "success";
		}
		
//}

//{--------<<<email>>>----------------------------------------------------------------------
#邮件相关
	0.依赖配置
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-mail</artifactId>
		</dependency>
		
		//邮箱开启SMTP功能: https://blog.csdn.net/caimengyuan/article/details/51224269
		spring.mail.host=smtp.163.com
		spring.mail.username=***@163.com
		spring.mail.password=*** //授权码作为密码使用

	1.邮件/*普通*附件*静态资源*模板*/
		@RestController
		@RequestMapping("mail")
		public class MailController {
			private static final String EMAIL_FROM = "dongyan3131@163.com";
			private static final String EMAIL_TO = "453705197@qq.com";
			private static final String EMAIL_SUBJECT = "主题：邮件主题";

			@Autowired
			JavaMailSender mailSender;

			@Autowired
			private TemplateEngine templateEngine;

			@GetMapping("/simple") //普通邮件
			public void simpleEmail() {
				SimpleMailMessage message = new SimpleMailMessage();
				message.setFrom(EMAIL_FROM);
				message.setTo(EMAIL_TO);
				message.setSubject(EMAIL_SUBJECT);
				message.setText("内容：邮件内容");

				mailSender.send(message);
			}

			@GetMapping("/attach") //三种复杂邮件
			public void attachEmail() throws Exception {

				// 含附件, 静态资源, 模板,则增加第二个参数,并为true
				MimeMessage mimeMessage = this.mailSender.createMimeMessage();
				MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
				helper.setFrom(EMAIL_FROM); // 发送方
				helper.setTo(EMAIL_TO); // 接收方
				helper.setSubject(EMAIL_SUBJECT); //主题

				// 1.附件
				File file = new File(SystemUtils.getFilePath(), "/logs/sm/sm.log");//附件位置
				if (file.exists()) {
					String path = file.getPath();
					String fileName = path.substring(path.lastIndexOf(File.separator) + 1);
					helper.addAttachment(fileName, file); //添加附件(附件名,附件路径)
				}

				// 2.静态资源 -> 在邮件正文中查看图片,而非附件
				String sb = "<h1>大标题-h1</h1>" +
						"<p style='color:#F00'>红色字</p>" +
						"<p style='text-align:right'>右对齐</p>" +
						"<p><img src=\"cid:weixin\"></p>";
				helper.setText(sb, true); //true表示启动HTML格式的邮件
				file = new File(SystemUtils.getFilePath(), "/imgs/a.jpg");
				if (file.exists()) {
					// 注意: 资源名称"weixin" 需要与正文中 cid:weixin 对应起来
					helper.addInline("weixin", file);
				}

				// 3.模板邮件 -> 固定的场景,如重置密码、注册确认等,只有小部分是变化的
				org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
				context.setVariable("username", "skyl");
				String content = templateEngine.process("email", context);
				helper.setText(content, true);

				mailSender.send(mimeMessage);
			}
		}
		
//}

//{--------<<<config>>>--------------------------------------------------------------------
#properties默认
	server.port=8090
	server.servlet.context-path=/demo

	spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
	spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
	spring.datasource.url=jdbc:mysql://192.168.8.7:33306/test0329?useSSL=false&allowMultiQueries=true&serverTimezone=GMT%2B8
	spring.datasource.username=bluecardsoft
	spring.datasource.password=#$%_BC13439677375

	//动态web修改 context-path -> 项目右键 -> properties -> 搜索web -> 修改 Web Project Settings

#YAML文件
	k:(空格)v //表示一对键值对(空格必须有),其中属性和值大小写敏感
	
	字符串默认不用加上单引号或双引号
		加'' -> '会'转义字符串里面的特殊字符
		加"" -> "不会"...................... -> mysql密码含特殊字符: "#$%_BC134"

	数组(List/Set): 
		标准写法 -> pets: 
					 - cat
					 - dog
		行内写法 -> pets: [cat,dog,pig]

	随机数: ${random.int}; ${random.int(10)}; ${random.int(10,100)}

	占位符: //获取之前配置的值,如果没有可以是用:指定默认值
		person.age = ${random.int}
		person.last_name = 张三${person.age:18}

#配置文件加载顺序		
	– classpath:/			#路径src/main/resources
	– classpath:/config/
	– file:./				#当前项目的根路径,与pom同级. (jar包同级目录)
	– file:./config/

	//优先级别: 低--->高. 高优先级覆盖低优先级.
	//加载顺序: 先--->后. (由里到外). 后加载的覆盖先加载的. [互补配置]
	
	0.以上是开发时配置文件的位置—对于打成jar包
		由于 classpath 会被打成jar包; 而 file 则不会,所以应该把配置文件放到jar包同级目录.
		//jar包同级 '/config/*.yml' 优先级最高, jar包内部默认位置的 '*.yml' 优先级最低!
	
	1.配置外部log
		//在配置文件中指定log位置.(内部或外部yml都可以)
		//推荐外部 -> logback的 scan 和 scanPeriod 两个属性保证了 热部署,即改即生效!!!
		logging.config=file:./config/logback-spring.xml

#读取配置文件
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
		
	0.my.properties
		voice.in=欢迎光临

		info.enabled=false
		info.remote-address=192.168.1.1
		info.security.username=user
		info.security.password=pwd
		info.security.roles=USER,ADMIN
		
	1.读取配置
		//@PropertySource: 加载指定的配置文件
		//value: 设置需要加载的属性文件,可以一次性加载多个. (默认参数)
		//encoding: 编码格式,默认""
		//ignoreResourceNotFound: 当指定的配置文件不存在是否报错,默认 false
		//name: 在Springboot的环境中必须唯一. 默认"class path resource [config/my.properties]"

		//@Value: 用于读取单个属性值
		
		//@ConfigurationProperties: 用于批量读取属性值
		//prefix: 属性前缀,通过 'prefix+字段名' 匹配属性
		//ignoreUnknownFields: 是否忽略未知的字段
		//ignoreInvalidFields: 是否忽略验证失败(类型转换异常)的字段
		//-----------<<<<<<一定要有GET/SET方法>>>>--------------------
		
		@Data// lombok插件,自动生成GET/SET
		@Component
		@ConfigurationProperties(/* prefix = "info", */ ignoreUnknownFields = true, ignoreInvalidFields = true)
		@PropertySource(value = { "file:./my.properties",
				"classpath:my.properties" }, encoding = "utf-8", ignoreResourceNotFound = false, name = "my.properties")
		public class MyProperties {
			// @Value("${info.enabled}")
			// public String infoEnabled;

			public Voice voice; //voice开头
			public Info info; //info开头

			@Data
			public static class Voice {
				public String in;
			}

			@Data
			private static class Info {
				private boolean enabled;
				private InetAddress remoteAddress;// 下划线语法,驼峰语法等都能匹配属性
				private Security security;
			}

			@Data
			private static class Security {
				private String username;
				private String password;
				private List<String> roles = new ArrayList<>(Collections.singleton("USER"));
			}
		}
		
		//通过 @Autowired 方式取值 MyProperties
		logger.info("MyProperties--res: {}", JSON.toJSON(myProperties));
			{
			  "info": {
				"enabled": false,
				"remoteAddress": "192.168.1.1",
				"security": {
				  "password": "pwd",
				  "roles": [
					"USER",
					"ADMIN"
				  ],
				  "username": "user"
				}
			  },
			  "voice": {
				"in": "欢迎光临"
			  }
			}
		
	2.总结
		//(1).对于单个属性
		可以采用 @Value 注解; 
		也可以采用 '@Autowired Environment env; env.getProperty("spring.mail.password");'
		
		//(2).对于批量属性
		建议采用以上 @ConfigurationProperties 方式.
		
	3.通过IO读取
		// 默认从此类所在包下读取,path需要添加前缀"/"
		// InputStream in = getClass().getResourceAsStream("/my.properties");
		
		// 默认从ClassPath下读取,path不需要添加前缀
		InputStream in = getClass().getClassLoader().getResourceAsStream("my.properties");
		Properties properties = new Properties();
		properties.load(new InputStreamReader(in, "UTF-8")); //U8方式读取
		properties.forEach((key, value) -> log.info(key + " - " + value));
		
		
#@Value("#{}")与@Value("${}")的区别
	//(1).@Value("#{}") -> 通过SpEl表达式获取: 常量; bean属性值; 调用bean的某个方法	
		@Value("#{1}")
		private int number; // 获取数字1

		@Value("#{'Spring Expression Language'}") // 获取字符串常量
		private String language;

		@Value("#{info.remoteAddress}") // 获取bean的属性
		InetAddress address;
	
	//(2).@Value("${}") -> 获取属性文件中定义的属性值	
		@Value("${info.enabled:}")
		public String enabled; //获取配置属性,默认空字符串
	
	//(3).总结
		${ property : default_value }
		#{ obj.property? : default_value } //二者取默认值时,语法不同(多个?)
		#{ '${}' } //二者可以结合使用,注意单引号!~! 但不能反过来,如: ${ '#{}' }

#@PostConstruct
	//...

#多环境切换 - profile特性 - 不同环境加载不同配置. 
	//以下文件 与 默认application.yml 存放在同级目录下.
	//其中,前者配置特殊信息, 后者配置公用信息. 二者相互补充
	application-dev.yml		->	开发环境
	application-test.yml	->	测试环境 
	application-prod.yml	->	生产环境

	0.激活profile特性的三种方法.
		1).在默认配置文件中激活: spring.profiles.active=dev
		2).(略)命令行: java -jar demo.jar --spring.profiles.active=dev
		3).(略)虚拟机参数(VM argumments): -Dspring.profiles.active=dev

	1.log的profile特性
		<springProfile name="dev"> //<!-- 控制台 * 测试环境 -->
			<root level="info">
				<appender-ref ref="console" />
			</root>
		</springProfile>

		<springProfile name="prod"> //<!-- 控制台 * 生产环境 -->
			<root level="warn">
				<appender-ref ref="console" />
			</root>
		</springProfile>

//}

//{--------<<<junit>>>---------------------------------------------------------------------
#单元测试 junit
	1.pom文件
		// <!-- 添加 junit 环境的 jar 包 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
		
	2.测试DEMO		
		@RunWith(SpringRunner.class) //junit 与 spring 进行整合; 也可用 SpringJUnit4ClassRunner.class
		@SpringBootTest//(classes = {SpringMain.class}) //加载项目启动类,可省
		public class HelloServiceTest {

			@Autowired
			private HelloService helloService;

			@Test
			public void test() {
				helloService.hello();
			}
		}

//}

//{--------<<<热部署>>>--------------------------------------------------------------------
#插件 SpringLoader(两种方式)
	//缺点: 只对 java 代码生效, 对页面更改无能为力.
	
	1-0.以maven插件方式使用SpringLoader
		// <!-- springloader 插件 -->
		<build>
			<plugins>
				<plugin>
					<groupId>org.springframework.boot</groupId>
					<artifactId>spring-boot-maven-plugin</artifactId>
					<dependencies>
						<dependency>
							<groupId>org.springframework</groupId>
							<artifactId>springloaded</artifactId>
							<version>1.2.5.RELEASE</version>
						</dependency>
					</dependencies>
				</plugin>
			</plugins>
		</build>
		
	1-1.使用maven命令启动项目
		//其中, maven插件起作用,必须使用maven命令进行启动
		//缺点: mvn插件形式的热部署程序是在系统后台以进程的形式来运行. 需要手动关闭该进程(java.exe *32)
		Run As... --> mvn build... ---> Main --> Goals填写: spring-boot:run

	2-0.项目中直接使用jar包
		目录'/lib'添加 springloader-1.2.5.RELEASE.jar 
		
	2-1.启动命令
		Run Configuration... --> Arguments --> VM argumments填写: -javaagent:.\lib\springloaded-1.2.5.RELEASE.jar -noverify

#工具 DevTools
	0.部署项目时使用的方式
		SpringLoader --> 热部署; DevTools --> 重新部署.

	1.pom文件
        // <!-- DevTools -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <optional>true</optional> //<!-- 依赖只在当前项目生效,不会传递到子项目中 -->
        </dependency>

//}

//{--------<<<fastjson>>>------------------------------------------------------------------
#fastjson解析json数据
	//sp2.x默认使用jacksonJson解析json数据现在转换为fastjson
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.47</version>
        </dependency>
		
	1.添加配置类
		@Configuration
		public class WebMvcConfig extends WebMvcConfigurationSupport {
			Logger logger = LoggerFactory.getLogger(getClass());

			//利用fastjson替换掉jackson,且解决中文乱码问题
			@Override
			public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
				//1.构建了一个消息转换器 converter
				FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();

				//2.添加fastjson配置,如: 是否格式化返回的json数据;设置编码方式
				FastJsonConfig config = new FastJsonConfig();

				config.setSerializerFeatures(SerializerFeature.PrettyFormat);//格式化

				List<MediaType> list = new ArrayList<>();//中文乱码
				list.add(MediaType.APPLICATION_JSON_UTF8);
				converter.setSupportedMediaTypes(list);

				//3.在消息转换器中添加fastjson配置
				converter.setFastJsonConfig(config);
				converters.add(converter);
			}
		}
		
	2.指定日期格式
		class Demo {
			@JSONField(format = "yyyy-MM-dd HH:mm:ss") //fastjson格式化
			//@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") //jackson格式化
			private Date date;
		}

//}

//{--------<<<exception>>>-----------------------------------------------------------------
#Boot对于异常处理提供了五种处理方式 --> //推荐: 3/5  http://blog.51cto.com/13902811/2170945?source=dra

	1.自定义错误页面(默认)
		一旦程序出现异常, SpringBoot 会向url '/error' 发送请求.
		通过默认的 BasicExceptionController 来处理请求 '/error',然后跳转到默认异常页面,显示异常信息.
		
		所以, 如果需要将所有异常统一跳转到自定义错误页面,需新建页面 /templates/error.html //必须叫 error.html
		缺点: 不符合实际需求; 应该对于不同错误跳转不同页面.

	2.注解处理异常@ExceptionHandler
	3.注解处理异常@ExceptionHandler + @ControllerAdvice	
		//处理顺序: 本类 --> @ControllerAdvice 标识类
		当执行过程中出现异常,首先在本类中查找 @ExceptionHandler 标识的方法
		找不到, 再去查找 @ControllerAdvice 标识类中的 @ExceptionHandler 标识方法来处理异常.
		
		//处理优先级: 异常的最近继承关系
		例如发生异常 NullPointerException; 但是声明的异常有 RuntimeException 和 Exception
		此时,根据异常的最近继承关系,找到继承深度最浅的那个, 即 RuntimeException 的声明方法

		@ControllerAdvice //异常处理类
		public class GlobalException {
			/**
			 * 参数(可选):
			 * 		异常参数(包括自定义异常);
			 * 		请求或响应对象(HttpServletRequest; ServletRequest; PortleRequest/ActionRequest/RenderRequest) 
			 * 		Session对象(HttpSession; PortletSession) 
			 * 		WebRequest; NativeWebRequest; Locale; 
			 * 		InputStream/Reader; OutputStream/Writer; Model
			 * 
			 * 返回值(可选):
			 * 		ModelAndView; Model; Map; View; String; @ResponseBody;
			 * 		HttpEntity<?>或ResponseEntity<?>; 以及void
			 */
			@ExceptionHandler(ArithmeticException.class) //ex对应发生的异常对象
			public ModelAndView arithmeticException(HttpServletRequest request, ArithmeticException ex) {
				
				//区分 URL & URI: http://ip:port/demo/hello/hello & /demo/hello/hello
				log.info("{} & {}", request.getRequestURL(), request.getRequestURI());

				ModelAndView mv = new ModelAndView("error1");
				mv.addObject("errMsg", ex.getLocalizedMessage());
				return mv; //跳转异常页-并携带异常信息
			}
			
			@ExceptionHandler(RuntimeException.class)
			public ModelAndView runtimeException(HttpServletRequest request, RuntimeException ex) {				
				ModelAndView mv = new ModelAndView("error2");
				mv.addObject("errMsg", ex.getLocalizedMessage());
				return mv;
			}
		}

	4.配置 SimpleMappingExceptionResolver(3的简化)
		//优点: 在全局异常类的一个方法中完成所有异常的统一处理
		//缺点: 只能进行异常与视图的映射, 不能传递异常信息.

		@Configuration //(1).此处的注解不同
		public class GlobalException {
			
			@Bean //(2).方法必须有返回值.返回值类型必须是: SimpleMappingExceptionResolver
			public SimpleMappingExceptionResolver getSimpleMappingExceptionResolver() {
				SimpleMappingExceptionResolver resolver = new SimpleMappingExceptionResolver();
				Properties mappings = new Properties();

				//arg0: 异常的类型,注意必须是异常类型的全名; arg1: 视图名称
				mappings.put("java.lang.ArithmeticException", "error1");
				mappings.put("java.lang.RuntimeException", "error2");

				//(3).设置异常与视图的映射,但不能传递异常信息
				resolver.setExceptionMappings(mappings);
				return resolver;
			}
		}

	5.自定义类处理异常 HandlerExceptionResolver
		@Configuration
		public class GlobalException implements HandlerExceptionResolver {

			@Override
			public ModelAndView resolveException(
					HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
				ModelAndView mv = new ModelAndView();

				//不同异常类型,不同视图跳转
				if (ex instanceof ArithmeticException) {
					mv.setViewName("error1");
				}
				if (ex instanceof NullPointerException) {
					mv.setViewName("error2");
				}
				//并传递异常信息
				mv.addObject("errMsg", ex.toString());
				return mv;
			}
		}

#参照 ErrorMvcAutoConfiguration ---> 错误处理的自动配置.
	#一旦系统出现 4xx 或 5xx 之类的错误, 'ErrorPageCustomizer' 就会生效, 它会发送'/error'请求;
		@Value("${error.path:/error}")    
		private String path = "/error";
	
	#'/error'请求会被 'BasicErrorController' 处理, 它有两种处理机制:
		@Controller
		@RequestMapping("${server.error.path:${error.path:/error}}")
		public class BasicErrorController extends AbstractErrorController {
			// 针对浏览器请求的响应页面, 产生html类型的数据
			@RequestMapping(produces = "text/html")
			public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) { }
			
			// 针对其他客户端请求的响应数据, 产生json数据
			@RequestMapping    
			@ResponseBody 
			public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) { }
		}
		
	#其中, html类型数据由 'DefaultErrorViewResolver' 解析产生, 规则为:
		1).有模板引擎的情况下: error/状态码
		2).没有模板引擎 (模板引擎找不到这个错误页面), 静态资源文件夹下找
		3).以上都没有错误页面, 就使用SpringBoot默认的错误提示页面
	
	#json类型数据由 'DefaultErrorAttributes'提供, 其中包括:
		timestamp: 时间戳;	status: 状态码; 
		error: 错误提示;	exception: 异常对象
		message: 异常消息;	errors: JSR303数据校验的错误都在这里
			
	1.定制错误页面
		将错误页面命名为 '错误状态码.html', 存放路径: 'templates/error/ *.html', 发生错误就会来到 对应状态码的页面.
		文件名也可以使用 4xx 和 5xx 来模糊匹配状态码, 当然精确匹配优先考略!!!

	2.定制错误的json数据
		//controller的一个辅助类,最常用作全局异常处理的AOP切面类
		@ControllerAdvice
		public class GlobalExceptionHandler {
			
			//(第1版).接口和浏览器返回皆为json -> 没有做到自适应!!!
			@ResponseBody
			@ExceptionHandler(BlueException.class)
			private Map<Object, Object> notFound(BlueException e) {
				Map<Object, Object> map = new HashMap<>();
				map.put("errMsg", e.errMsg);
				return map;
			}
		}
	
		//(第2版).转发到/error,进行自适应响应处理 -> 未能显示用户自定义的异常信息
		@ExceptionHandler(BlueException.class)
		private String notFound(HttpServletRequest req, BlueException e) {
			Map<Object, Object> map = new HashMap<>();
			map.put("errMsg", e.errMsg);

			// 传入自定义的错误状态码 4xx 5xx,否则就不会进入定制错误页面的解析流程
			req.setAttribute("javax.servlet.error.status_code", 500);

			// 转发到/error
			return "forword:/error";
		}
			
		//(第3版).错误请求的自适应反馈 (转发到定制错误页面或返回json), 以及携带自定义的数据内容
		@ExceptionHandler(BlueException.class)
		private String notFound(HttpServletRequest req, BlueException e) {
			Map<Object, Object> map = new HashMap<>();
			map.put("errCode", e.errCode);
			map.put("errMsg", e.errMsg);

			req.setAttribute("javax.servlet.error.status_code", 500);
			req.setAttribute("ext", map);

			return "forward:/error";
		}

		'再次强调: 错误页面的数据集合由 DefaultErrorAttributes.getErrorAttributes() 提供!!!'
		//(配合第3版共同使用).给容器中加入我们自己定义的ErrorAttributes
		@Component
		class BlueErrorAttributes extends DefaultErrorAttributes {
			@Override
			public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes, boolean includeStackTrace) {
				Map<String, Object> map = super.getErrorAttributes(requestAttributes, includeStackTrace);

				// 取出上述方法的'ext',放入错误页面的数据集合
				// 第二个参数: 0代表从 request 中读取数据; 1代表从 session 中
				map.put("data", requestAttributes.getAttribute("ext", 0));

				// 此map就是页面和json都能获取到的所有字段
				return map;
			}
		}

//}

//{--------<<<Actuator>>>------------------------------------------------------------------
#配置监控Actuator
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
		
	0.boot2.x所有端点访问路径都移到了/actuator. //(默认只暴露两个端点: health,info)
		management.endpoints.web.exposure.include=* //暴露节点: 所有
		management.endpoints.web.exposure.exclude=env //不暴露节点: env
		
		http://localhost:8090/demo/actuator //返回所有已暴露的端点
		http://localhost:8090/demo/actuator/health //访问health端点
		
	1.各个端点endpoint
		auditevents		-	审计事件
		beans			-	应用程序上下文里全部的Bean,以及它们的关系
		health			-	报告应用程序的健康指标,这些值由 HealthIndicator 的实现类提供
		conditions		-	自动配置报告,记录哪些自动配置条件通过了,哪些没通过
		configprops		-	描述配置属性(包含默认值)如何注入Bean
		info			-	显示配置文件中以 'info' 打头的属性
		threaddump		-	获取线程活动的快照
		scheduledtasks	-	定时任务
		httptrace		-	跟踪 HTTP 请求-响应交换的情况
		mappings		-	描述全部的URI路径，以及它们和控制器(包含Actuator端点)的映射关系
		(*)metrics		-	报告各种应用程序度量信息,比如内存用量和HTTP请求计数.
		(*)loggers		-	显示和修改应用程序中的loggers配置
		(*)env			-	获取全部环境属性
		
		#其中,带(*)表示当前路径只能获取目录信息, 详情信息得需要进一步访问获取. 如:
			http://localhost:8090/demo/actuator/metrics/system.cpu.count //获取系统cpu个数
		
			//也可以通过这个地址更改当前的日志级别????????????
			http://localhost:8090/demo/actuator/loggers/com.example.demo.controller //获取某个包的日志级别
					
	2.定制端点endpoint
		//开启远程应用关闭功能.【post请求】
		management.endpoint.shutdown.enabled=true 
		
		management.server.port=8091
		//只有在设置了 management.server.port 时才有效 (可选)
		management.server.servlet.context-path=/management
		//管理端的基本路径 (可选)
		management.endpoints.web.base-path=/application
		
		http://localhost:8091/demo/management/application/health //设置了以上三项,则访问 health 端点路径
		
		//若要恢复 1.x 方式(即用 /health 代替 /actuator/health), 设置以下属性:
		management.endpoints.web.base-path=/
		
		//关闭端点 - health
		management.endpoint.health.enabled=false
		
		//默认只显示health部分信息,开启显示全部信息
		management.endpoint.health.show-details=always
		
		//配置端口info信息, 访问 http://192.168.8.7:8090/demo/actuator/info
		info.myinfo.port=9527
		
	3.Bean
		druid: { //Spring应用程序上下文中的Bean名称或ID
			aliases: [ ], //
			scope: "singleton", //Bean的作用域.(通常是单例,这也是默认作用域)
			type: "com.alibaba.druid.pool.DruidDataSource", //Bean的Java类型
				//.class文件的物理位置,通常是一个URL,指向构建出的JAR文件.会随着应用程序的构建和运行方式发生变化
			resource: "class path resource [com/example/demo/config/DruidConfig.class]", 
			dependencies: [ ] //当前Bean注入的Bean ID列表
		},
		
	4.Conditions
		positiveMatches: { //成功条件
			DruidDataSourceAutoConfigure: [
				{	//检查 Classpath 里是否存在
					condition: "OnClassCondition",'com.alibaba.druid.pool.DruidDataSource'
					message: "@ConditionalOnClass found required class 'com.alibaba.druid.pool.DruidDataSource'; @ConditionalOnMissingClass did not find unwanted class"
				}
			],
		}
		//...
		negativeMatches: { //失败条件
			DruidDataSourceAutoConfigure#dataSource: {
				notMatched: [
					{	//检查 Classpath 里是否存在
						condition: "OnBeanCondition",
						message: "@ConditionalOnMissingBean (types: javax.sql.DataSource; SearchStrategy: all) found beans of type 'javax.sql.DataSource' druid"
					}
				],
				matched: [ ]
			},
		}

//}

//{--------<<<定时任务>>>------------------------------------------------------------------
#任务调度可以用'Quartz'; 但对于简单的定时任务可以使用内置的'Scheduled'. linux系统级别定时任务使用'crontab'.

#Scheduled
	#initialDelay	//项目启动后,延迟多少毫秒执行任务
	#fixedRate		//每隔多少毫秒执行一次 (当 任务耗时>频率 时,下次开始时间=上次结束时间);
	#fixedDelay		//每次执行完毕,延迟多少毫秒再次执行
	#cron(******)	//详细配置方法执行频率

	// cron表达式: [秒] [分] [时] [日] [月] [周] [年(可省)]
	// 秒(0~59); 分(0~59); 时(0~23); 日(1~31,和月份有关); 月(1~12); 星期(1~7,1为周日); 年(1970~2099)
	
	 #* 所有字段; 表示对应时间域的每一个时刻; 如分钟字段,表示"每分钟"
	 #- ........; 表示一个范围; 如小时字段'10-12',表示从10到12点, 即 10,11,12
	 #, ........; 表示一个列表值; 如星期字段"MON,WED,FRI", 表示星期一,星期三和星期五
	 #/ ........; 表示一个等步长序列; x/y: x 为起始值,y 为增量步长值.
		//如分钟字段: 0/15表示 0,15,30,45; 5/15表示 5,20,35,50,
		//也可以使用 */y,等同于 0/y,即 y秒触发一次.
		
	 #? '日期'和'星期'; 通常指定为"无意义的值",相当于占位符. --> 因为日和星期是有冲突的.
	 #L ..............; 代表"Last"的意思,但它在两个字段中意思不同.
		//在日期中,表示这个月的最后一天; 如一月的 31 号,非闰年二月的 28 号.
		//在星期中,则表示星期六,等同于 7.
		//如果 L 出现在星期字段里,而且在前面有一个数值 X,则表示"这个月的最后星期 (X-1)"; 如 6L 表示该月的最后星期五
	 
		@Slf4j
		@Component //不可省
		public class ScheduledTask {
			@Scheduled(cron = "*/5 * * * * ?") ///配合使用-全局注解 @EnableScheduling
			public void task() {
				log.info("ScheduledTask---{}", SystemUtils.getNow());
			}
		}	

#Scheduled-DEMO
	// 0 0 10,14,16 * * ?	每天上午10点，下午2点，4点
	// 0 0/30 9-17 * * ?	朝九晚五工作时间内每半小时
	// 0 0 12 ? * WED		每个星期三中午12点
	// 0 0 12 * * ?			每天12点触发
	// 0 15 10 ? * *		每天10点15分触发
	// 0 15 10 * * ?		每天10点15分触发
	// 0 15 10 * * ? *		每天10点15分触发
	// 0 15 10 * * ? 2005	2005年每天10点15分触发
	// 0 * 14 * * ?			每天下午的 2点到2点59分 每分触发一次
	// 0 0/5 14 * * ?		每天下午的 2点到2点59分(整点开始，每隔5分触发)
	// 0 0/5 14,18 * * ?	每天下午的 2点到2点59分、18点到18点59分(整点开始，每隔5分触发)
	// 0 0-5 14 * * ?		每天下午的 2点到2点05分每分触发
	// 0 10,44 14 ? 3 WED	3月每周三下午的 2点10分和2点44分触发
	// 0 15 10 ? * MON-FRI	从周一到周五每天上午的10点15分触发
	// 0 15 10 15 * ?		每月15号上午10点15分触发
	// 0 15 10 L * ?		每月最后一天的10点15分触发
	// 0 15 10 ? * 6L		每月最后一周的星期五的10点15分触发
	// 0 15 10 ? * 6L 2002-2005	从2002年到2005年每月最后一周的星期五的10点15分触发
	// 0 15 10 ? * 6#3		每月的第三周的星期五开始触发
	// 0 0 12 1/5 * ?		每月的第一个中午开始每隔5天触发一次
	// 0 11 11 11 11 ?		每年的11月11号 11点11分触发(光棍节)

#Quartz
	任务调度(job scheduling)的开源框架.	可以与J2EE与J2SE结合,也可以单独使用.
	可用来创建简单或运行十个,百个,甚至于好几万个 jobs 复杂的程序.
	
	#job		- 任务		- 你要做什么事?
	#Trigger	- 触发器	- 你什么时候去做?
	#Scheduler	- 任务调度	- 你什么时候需要去做什么事?
	
	1.简单DEMO
		private static void task01() throws SchedulerException {
			JobDetail job = JobBuilder.newJob(JobDemo.class).build();

			//(1).通过 Quartz 内置方法来完成简单的重复调用,每秒执行一次
			// Trigger trigger = TriggerBuilder.newTrigger()
			//         .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever()).build();

			//(2).自定义 Cron 表达式来给定触发的时间
			Trigger trigger = TriggerBuilder.newTrigger()
					.withSchedule(CronScheduleBuilder.cronSchedule("0/2 * * * * ?")).build();

			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.scheduleJob(job, trigger);

			scheduler.start();
		}
#Quartz&Boot(2种方式)
	#(1).创建普通job类,直接调用.(灵活,非侵入)
	#(2).job类继承 QuartzJobBean,实现方法 executeInternal(),此方法就是被调度的任务体
	
        <dependency>
            <groupId>org.quartz-scheduler</groupId>
            <artifactId>quartz</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId> //作用详见<<常用包>>
            <artifactId>spring-context-support</artifactId>
        </dependency>
	
#MethodInvokingJobDetailFactoryBean
	1.普通job类
		@Component
		public class JobDemo01 {
			@Autowired
			HelloService helloService; //service 层

			public void job() {
				helloService.hello();
				System.out.println("job01: " + SystemUtils.getNow());
			}
		}
		
	2.调度配置	
		@Configuration
		public class QuartzConfig01 {
			@Bean("job01")
			public MethodInvokingJobDetailFactoryBean job01(JobDemo01 jobDemo01) {
				MethodInvokingJobDetailFactoryBean job = new MethodInvokingJobDetailFactoryBean();
				job.setName("my-job01"); // 任务的名字
				job.setGroup("my"); // 任务的分组

				job.setConcurrent(false); // 是否并发
				job.setTargetObject(jobDemo01); // 被执行的对象
				job.setTargetMethod("job"); // 被执行的方法
				return job;
			}

			@Bean(name = "tigger01")
			public CronTriggerFactoryBean tigger01(@Qualifier("job01") MethodInvokingJobDetailFactoryBean job01) {
				CronTriggerFactoryBean tigger = new CronTriggerFactoryBean();
				tigger.setName("my-tigger01");
				tigger.setJobDetail(Objects.requireNonNull(job01.getObject()));
				tigger.setCronExpression("0/5 * * * * ?"); //cron
				return tigger;
			}

			@Bean(name = "scheduler01")
			public SchedulerFactoryBean scheduler01(@Qualifier("tigger01") Trigger tigger01) {
				SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
				scheduler.setStartupDelay(5); // 延时启动定时任务,避免系统未完全启动却开始执行定时任务的情况
				scheduler.setOverwriteExistingJobs(true); // 覆盖已存在的任务
				scheduler.setTriggers(tigger01); // 注册触发器
				return scheduler;
			}
		}
		
#JobDetailFactoryBean
	1.指定job类
		///@Component -> 无需此注解,区别于方式1
		public class JobDemo02 extends QuartzJobBean {
			@Override
			protected void executeInternal(JobExecutionContext context) {
				HelloService helloService = (HelloService) context.getMergedJobDataMap().get("helloService"); //取参 - service
				helloService.hello();
				System.out.println("job02: " + SystemUtils.getNow());
			}
		}
	
	2.调度配置
		@Configuration
		public class QuartzConfig02 {
			@Autowired
			HelloService helloService;

			@Bean("job02")
			public JobDetailFactoryBean job02() {
				JobDetailFactoryBean job = new JobDetailFactoryBean();
				job.setJobClass(JobDemo02.class);

				Map<String, Object> map = new HashMap<>();
				map.put("helloService", helloService);
				job.setJobDataAsMap(map); //传参 -> helloService
				return job;
			}

			@Bean(name = "tigger02")
			public CronTriggerFactoryBean cronTriggerFactoryBean(JobDetailFactoryBean job02) {
				CronTriggerFactoryBean tigger = new CronTriggerFactoryBean();
				tigger.setJobDetail(Objects.requireNonNull(job02.getObject()));
				tigger.setCronExpression("0/5 * * * * ?"); //cron
				return tigger;
			}

			@Bean(name = "scheduler02")
			public SchedulerFactoryBean schedulerFactoryBean(CronTriggerFactoryBean tigger02) {
				SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
				scheduler.setTriggers(tigger02.getObject());
				return scheduler;
			}
		}

//}

//{--------<<<异步任务>>>------------------------------------------------------------------















//}

//{--------<<<常用包>>>--------------------------------------------------------------------
#org.apache.commons
        // <!-- 该版本完全支持 Java5 的特性,如泛型和可变参数. 该版本无法兼容以前的版本,简化很多平时经常要用到的写法,如判断字符串是否为空等等 -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.8.1</version>
        </dependency>

        // <!-- 对象池的实现,显著的提升了性能和可伸缩性,特别是在高并发加载的情况下 -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
            <version>2.4.2</version>
        </dependency>

        // <!-- email -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-email</artifactId>
            <version>1.4</version>
        </dependency>
        // <!-- spring-boot email -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-mail</artifactId>
        </dependency>

        // <!-- IO工具类,文件操作及字符串比较功能 -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-io</artifactId>
            <version>1.3.2</version>
        </dependency>

#spring
        // <!-- 为Spring核心提供了大量扩展.可以找到使用 Spring ApplicationContext 特性时所需的全部类,JDNI所需的全部类,
        //		UI模板引擎(Templating),如 Velocity、FreeMarker、JasperReports, 以及校验 Validation 方面的相关类 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>4.3.3.RELEASE</version>
        </dependency>

        // <!-- 含支持UI模版(Velocity,FreeMarker,JasperReports),邮件服务,脚本服务(JRuby),缓存Cache(EHCache),
        // 		任务计划Scheduling(uartz)方面的类. 外部依赖spring-context, (spring-jdbc, Velocity, FreeMarker,
        // 		JasperReports, BSH, Groovy, JRuby, Quartz, EHCache) -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context-support</artifactId>
            <version>4.3.3.RELEASE</version>
        </dependency>

        // <!-- spring测试框架,需要配合 junit 进行使用创建单元测试. spring测试所需包: sring的相关组件,spring-test,junit -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>4.3.3.RELEASE</version>
        </dependency>

        // <!-- 包含Spring 框架基本的核心工具类. Spring 其它组件要都要使用到这个包里的类，是其它组件的基本核心.
        //		也可以在自己的应用系统中使用这些工具类.外部依赖Commons-logging,Log4J -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
            <version>4.3.3.RELEASE</version>
        </dependency>

        // <!-- 所有应用都要用到的,它包含访问配置文件,创建和管理bean 以及 进行(IoC/DI)操作相关的所有类.
		//		如果应用只需基本的IoC/DI 支持，引入spring-core.jar 及spring-beans.jar 文件就可以了. 外部依赖spring-core，(CGLIB)。 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-beans</artifactId>
            <version>4.3.3.RELEASE</version>
        </dependency>

        // <!-- https://mvnrepository.com/artifact/org.springframework/spring-web
            // 包含Web 应用开发时，用到Spring 框架时所需的核心类，包括自动载入Web Application Context
            // 特性的类、Struts  与JSF 集成类、文件上传的支持类、Filter 类和大量工具辅助类。
            // 外部依赖spring-context, Servlet API, (JSP API, JSTL, Commons FileUpload, COS)。 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-web</artifactId>
            <version>4.3.3.RELEASE</version>
        </dependency>

        // <!-- https://mvnrepository.com/artifact/org.springframework/spring-webmvc
            // 包含Spring MVC 框架相关的所有类。包括框架的Servlets，Web MVC框架，控制器和视图支持。
            // 当然，如果你的应用使用了独立的MVC
            // 框架，则无需这个JAR 文件里的任何类。
            // 外部依赖spring-web, (spring-support，Tiles，iText，POI)。 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>4.3.3.RELEASE</version>
        </dependency>

        // <!-- https://mvnrepository.com/artifact/org.springframework/spring-jdbc
            // 包含对Spring 对JDBC 数据访问进行封装的所有类。 外部依赖spring-beans，spring-dao。 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>4.3.3.RELEASE</version>
        </dependency>

        // <!-- https://mvnrepository.com/artifact/org.springframework/spring-aop
            // AOP（Aspect Oriented Programming），即面向切面编程（也叫面向方面编程，面向方法编程）。
            // 其主要作用是，在不修改源代码的情况下给某个或者一组操作添加额外的功能。像日志记录，事务处理，
            // 权限控制等功能，都可以用AOP来“优雅”地实现，使这些额外功能和真正的业务逻辑分离开来，
            // 软件的结构将更加清晰。AOP是OOP的一个强有力的补充。 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aop</artifactId>
            <version>4.3.3.RELEASE</version>
        </dependency>

        // <!-- https://mvnrepository.com/artifact/org.springframework/spring-tx 事物控制 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-tx</artifactId>
            <version>4.3.3.RELEASE</version>
        </dependency>

        // <!-- https://mvnrepository.com/artifact/org.springframework/spring-orm
            // 包含Spring对DAO特性集进行了扩展，使其支持 iBATIS、JDO、OJB、TopLink， 因为Hibernate已经独立成包了，现在不包含在这个包里了。这个jar文件里大部分的类都要
            // 依赖spring-dao.jar里的类，用这个包时你需要同时包含spring-dao.jar包。 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-orm</artifactId>
            <version>4.3.3.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aspects</artifactId>
            <version>4.3.3.RELEASE</version>
        </dependency>

        //<!-- https://mvnrepository.com/artifact/org.springframework/spring-expression
        //    SPEL表达式支持:
        //    一、基本表达式：字面量表达式、关系，逻辑与算数运算表达式、字符串连接及截取表达式、
        //        三目运算及Elivis表达式、正则表达式、括号优先级表达式；
        //    二、类相关表达式：类类型表达式、类实例化、instanceof表达式、变量定义及引用、赋值表达式、
        //        自定义函数、对象属性存取及安全导航表达式、对象方法调用、Bean引用；
        //    三、集合相关表达式：内联List、内联数组、集合，字典访问、列表，字典，数组修改、集合投影、
        //        集合选择；不支持多维内联数组初始化；不支持内联字典定义；
        //    四、其他表达式：模板表达式。
        //    注：SpEL表达式中的关键字是不区分大小写的。-->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-expression</artifactId>
            <version>4.3.3.RELEASE</version>
        </dependency>
	

//}

//{--------<<<restful>>>-------------------------------------------------------------------
#restful是对于同一个服务器资源的一组不同的操作,包括: GET/POST/PUT/DELETE/PATCH/HEAD/OPTIONS
	
	1.http请求的安全和幂等
		安全 -> 请求不会影响资源的状态. 只读的请求: GET,HEAD,OPTIONS
		幂等 -> 多次相同的请求,目的一致.
				
		GET /emp/list --> 只读请求,不改变资源状态. //安全,幂等.
		
		PUT /emp/5    --> 多次请求都是将id为 5 的员工姓名修改成'wang'. //不安全,幂等.
		
		POST /emp     --> 多次请求会新增多条相同的数据. //不安全,不幂等.
		
		DELETE /emp/5 --> 多次请求目的都是删除id为 5 的员工. //不安全,幂等.
		///注意: 第一次成功删除,第二次及以后,虽资源已不存在,但也得返回 200 OK,不能返回 404.
		


				
//}

//{--------<<<druid>>>---------------------------------------------------------------------
#数据源Druid
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.10</version>
        </dependency>
	
	0.配置文件
		//sp1.x默认数据源为: org.apache.tomcat.jdbc.pool.DataSource
		spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
		
		spring.datasource.url=jdbc:mysql://127.0.0.1:3306/test0329?useSSL=false
		spring.datasource.driver-class-name=com.mysql.jdbc.Driver
		spring.datasource.username=***
		spring.datasource.password=***

	1.监控druid	//http://localhost:8080/blue/druid/login.html
		@Configuration
		public class DruidConfig {

			@ConfigurationProperties(prefix = "spring.datasource")

			@Bean
			public DataSource druid() {
				return new DruidDataSource();
			}

			// 1.配置一个管理后台的Servlet
			@Bean
			public ServletRegistrationBean statViewServlet() {
				ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");

				Map<String, String> initParams = new HashMap<>();
				initParams.put("loginUsername", "admin");
				initParams.put("loginPassword", "123456");
				initParams.put("allow", ""); //允许所有访问
				initParams.put("deny", "192.168.15.21"); //黑名单阻止访问 (存在共同时,deny优先于allow)
				bean.setInitParameters(initParams);
				return bean;
			}

			// 2.配置一个web监控的filter
			@Bean
			public FilterRegistrationBean webStatFilter() {
				FilterRegistrationBean bean = new FilterRegistrationBean();

				bean.setFilter(new WebStatFilter());
				Map<String, String> initParams = new HashMap<>();
				initParams.put("exclusions", "*.js,*.css,/druid/*");
				bean.setInitParameters(initParams); //添加需要忽略的格式信息
				bean.setUrlPatterns(Collections.singletonList("/*")); //添加过滤规则
				return bean;
			}
		}
		
#JdbcTemplate
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-jdbc</artifactId>
		</dependency>

	0.代码使用
		@Autowired
		JdbcTemplate jdbcTemplate;

		List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from student");


//}

//{--------<<<JPA>>>-----------------------------------------------------------------------
#比对概念
		JPA					-> //Java-Persistence-API, 对持久层操作的标准(接口+文档)
		
		Hibernate			-> //全自动化的ORM框架.
		Hibernate JPA		-> //实现了 JPA 标准的 Hibernate(Hibernate-3.2+).
		
		Spring Data			-> //用于简化数据库(SQL,NoSQL...)访问,并支持云服务的开源框架.
		Spring Data JPA		-> //Spring Data的一个子模块,实现了 JPA 标准的 Spring Data, 底层是 Hibernate.
		
		Spring Data Redis	-> //通过简单配置, 实现对reids各种操作,异常处理及序列化,支持发布订阅.
	
	0.pom文件
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
	
	1.配置文件
		//可选参数-create: 每次启动都会删除旧表,新建一个空表
		//可选参数-update: 根据实体类创建/更新数据库表
		spring.jpa.hibernate.ddl-auto=update
		spring.jpa.show-sql=true
		
	2.实体类
		@Data
		@Entity //表明是一个JPA实体->自动建表
		@Table(name = "t_emp") //默认表名为类名小写
		@NoArgsConstructor
		@AllArgsConstructor
		public class Employee {
			@Id
			@GeneratedValue(strategy = GenerationType.IDENTITY)
			private Integer id;

			@Column(name = "first_name")
			private String firstName;

			@Column
			private Boolean genderFlag; //默认列名 -> gender_flag

		}
	
#Repository ///Spring-Data-JPA 顶层接口,标识接口,空接口.
	1.方法名称命名的方式
		//驼峰命名规则: findBy(关键字) + 属性名称(首字母大写) + 查询条件(首字母大写,Like,OrderBy...)
		public interface EmployeeRepoDao extends Repository<Employee, Integer> {

			//SELECT * FROM t_emp WHERE first_name='zhang' AND gender_flag=TRUE
			List<Employee> findByFirstNameAndGenderFlag(String firstName, boolean genderFlag);

			//SELECT * FROM t_emp WHERE first_name LIKE '%ang%' OR gender_flag=TRUE ORDER BY id DESC
			List<Employee> findByFirstNameLikeOrGenderFlagOrderByIdDesc(String firstName, boolean genderFlag);
		}
		
	2.基于注解的方式
		public interface EmployeeRepoDao extends Repository<Employee, Integer> {

			//(1).hql --> 使用bean属性名称替代数据库字段进行查询
			@Query("FROM Employee WHERE firstName LIKE ?1 OR genderFlag=?2 ORDER BY id DESC")
			List<Employee> queryByHQL(String firstName, boolean genderFlag);

			//(2-1).原生sql --> 参数序号从①开始; nativeQuery=true
			@Query(value = "SELECT * FROM t_emp WHERE first_name LIKE ?1 OR gender_flag = ?2 ORDER BY id DESC", 
					nativeQuery = true)
			List<Employee> queryBySQL(String firstName, boolean genderFlag);
			
			//(2-2).原生sql --> @Param("参数名")
			@Query(value = "SELECT * FROM t_emp WHERE first_name LIKE :fName OR gender_flag = :lName ORDER BY id DESC", 
					nativeQuery = true)
			List<Employee> queryBySQL(@Param("fName") String firstName, @Param("lName") boolean genderFlag);
			
			@Modifying //更新|删除,必须添加此注解
			//@Transactional 
			@Query(value = "UPDATE t_emp SET first_name=?1 WHERE id=?2", nativeQuery = true)
			Integer updateBySQL(String firstName, Integer id);
		}
		
	3.事务
		业务逻辑层 Service 调用多个 Repository 方法时, 需要在 Service 方法上声明事务 @Transactional
		
#CrudRepository
	0.最基础的CRUD; extends Repository;
		public interface EmployeeCrudDao extends CrudRepository<Employee, Integer> {}
		
	1.1.测试DEMO
		@Test
		public void daoCrud() {
			//save(): 先查询数据表中是否存在该id数据??? 无则新增; 有则更新
			Employee save = employeeCrudDao.save(new Employee(7, "张0三", true));
			Iterable<Employee> all = employeeCrudDao.findAll();
			System.out.println(save + " - " + JSON.toJSON(all));
		}

#PagingAndSortingRepository
	0.分页和排序功能; extends CrudRepository;
	
		public interface EmployeePSDao extends PagingAndSortingRepository<Employee, Integer> {}
		
	1.测试DEMO		
		@Test
		public void daoPS() {
			Sort sort = Sort.by(Sort.Direction.DESC, "firstName", "id"); //(1).排序
			Iterable<Employee> all = employeePSDao.findAll(sort);

			Pageable pageable = PageRequest.of(0, 2); //(2).页码从0开始; 分页
			Page<Employee> all = employeePSDao.findAll(pageable);
			
			Sort sort = Sort.by(Sort.Direction.DESC, "id");
			PageRequest pageable = PageRequest.of(1, 2, sort);
			Page<Employee> all = employeePSDao.findAll(pageable); //(3).排序+分页
			System.out.println("daoPS - " + JSON.toJSON(all));
		}
		
#JpaRepository(**常用**)
	0.对父接口方法的返回值进行适配处理; extends PagingAndSortingRepository;
	
		public interface EmployeeJpaDao extends JpaRepository<Person, Integer> {}
		
#JpaSpecificationExecutor
	0.提供多条件查询—分页—排序—独立于以上接口存在—所以需配合以上接口使用
		public interface EmployeeDao extends JpaSpecificationExecutor<Employee>, JpaRepository<Employee, Integer> {}
		
	1.测试DEMO
		@Test
		public void daoDao() {
			Specification<Employee> spec = new Specification<Employee>() {
				/**
				 * @param root		查询对象的属性封装,即 Employee
				 * @param query		查询关键字 SELECT, WHERE, ORDER BY ...
				 * @param builder	查询条件 =, >, LIKE
				 * @return			封装整个查询条件
				 */
				@Override
				public Predicate toPredicate(Root<Employee> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
					// SELECT * FROM t_emp
					// WHERE first_name LIKE '%ang%'
					// OR gender_flag=TRUE
					// AND id BETWEEN 3 AND 9
					// AND id>=4
					// ORDER BY first_name DESC, id ASC;
					Predicate or = builder.or(builder.like(root.get("firstName").as(String.class), "%ang%"),
							builder.equal(root.get("genderFlag").as(boolean.class), true)); //OR

					List<Predicate> list = new ArrayList<>(); //AND
					list.add(or);
					list.add(builder.between(root.get("id").as(Integer.class), 3, 9));
					list.add(builder.greaterThanOrEqualTo(root.get("id").as(Integer.class), 4));
					Predicate[] predicates = new Predicate[list.size()];
					
					Predicate predicate = builder.and(list.toArray(predicates)); //所有条件
					query.where(predicate); //WHERE ... OR ... AND ... AND ...

					query.multiselect(root.get("id"), root.get("firstName")); //SELECT *,*

					query.orderBy(builder.desc(root.get("firstName")),
							builder.asc(root.get("id"))); //ORDER BY ..., ...

					return query.getRestriction();
				}
			};

			// Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "firstName"),
			//         new Sort.Order(Sort.Direction.ASC, "id")); //ORDER BY ..., ...
			
			PageRequest pageable = PageRequest.of(1, 2/*, sort*/); //分页 LIMIT 1,2; 页码从0开始

			Page<Employee> all = employeeDao.findAll(spec, pageable);
			System.out.println("daoDao - " + JSON.toJSON(all));
		}
		
#一对多关联映射
	0.dept与emp是一对多关系
		@Data
		@Entity
		@Table(name = "t_emp")
		public class Employee {
			//... ...
			
			/**
			 * PERSIST  持久保存拥有方实体时,也会持久保存该实体的所有相关数据。
			 * MERGE    将分离的实体重新合并到活动的持久性上下文时,也会合并该实体的所有相关数据。
			 * REMOVE   删除一个实体时,也会删除该实体的所有相关数据。
			 * ALL      以上都适用。
			 */
			@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}) //emp->dept: 多对一
			@JoinColumn(name = "dept_id") //外键
			private Dept dept;
		}
		
		@Data
		@Entity
		@Table(name = "t_dept")
		public class Dept {
			@Id
			@GeneratedValue(strategy = GenerationType.IDENTITY)
			@Column(name = "dept_id")
			private Integer deptId;

			@Column(name = "dept_name")
			private String deptName;

			@OneToMany(mappedBy = "dept") //dept->emp: 一对多关系
			private List<Employee> emps = new ArrayList<>();
		}
	
	1.新增DEMO
		@Test
		public void saveOne2Many() {
			//新建 emp - dept
			Employee employee = new Employee("whang", false);
			Dept dept = new Dept("软件");

			//关联
			employee.setDept(dept);
			dept.getEmps().add(employee);
			
			//写库
			employeeDao.save(employee);
		}
		
	2.查询DEMO
		@Test
		public void findOne2Many() {
			Optional<Employee> optional = employeeDao.findById(10);
			if (optional.isPresent()) {
				Employee employee = optional.get();
				System.out.println("DeptName: " + employee.getDept().getDeptName());
			}
		}
	
#多对多关联映射
	0.emp和role是多对多关系
		@Data
		@Entity
		@Table(name = "t_emp")
		public class Employee {
			//... ...
			
			@ManyToMany(mappedBy = "emps", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
			private Set<Role> roles = new HashSet<>();
		}
		
		@Data
		@Entity
		@Table(name = "t_role")
		public class Role {
			//... ...
			
			@ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
			//JoinTable: 中间表信息(配置在两张表中的任意一个)
			//joinColumns: 该表主键在中间表中的字段
			//inverseJoinColumns: 另一个表(即emp)主键在中间表中的字段
			@JoinTable(name = "t_emp_role", joinColumns = @JoinColumn(name = "role_id"),
					inverseJoinColumns = @JoinColumn(name = "emp_id"))
			private Set<Employee> emps = new HashSet<>();
		}
		
	1.新增DEMO
		@Test
		public void saveMany2Many() {
			//新建 emp - role
			Employee li = new Employee("li", false);
			Employee zhang = new Employee("zhang", true);
			Role admin = new Role("管理员");
			Role finance = new Role("财务");

			//关联 
			li.getRoles().add(admin);
			li.getRoles().add(finance);
			admin.getEmps().add(li);
			admin.getEmps().add(zhang);
			
			//写库
			employeeDao.save(li);
			employeeDao.save(zhang);
		}
	
	2.查询DEMO
		@Test
		public void findMany2Many() {
			Optional<Employee> optional = employeeDao.findById(12);
			System.out.println(optional.get().getRoles());
		}

//}


//{--------<<<WebSocket>>>-----------------------------------------------------------------
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-websocket</artifactId>
        </dependency>	
	
#ABC
	B/S结构的软件项目中有时客户端需要实时的获得服务器消息,但默认HTTP协议只支持 '请求响应模式'. 
	对于这种需求可以通过 polling, Long-polling, 长连接, Flash-Socket, HTML5中定义的WebSocket 完成.
	
	HTTP模式可以简化Web服务器,减少服务器的负担,加快响应速度,
	因为服务器不需要与客户端长时间建立一个通信链接.
	但不容易直接完成实时的消息推送功能(如聊天室,后台信息提示,实时更新数据等).

#Socket简介
	Socket 又称'套接字',应用程序通常通过 Socket 向网络发出请求或者应答网络请求.
	Socket 可以使用TCP/IP协议或UDP协议.
	
	//TCP协议:  面向连接的,可靠的,基于字节流的传输层通信协议,负责数据的可靠性传输的问题.
	//UDP协议:  无连接,不可靠,基于报文的传输层协议; 优点 ----> 发送后不用管,速度比TCP快.
	
	//HTTP协议: 无状态协议, 通过 Internet 发送请求消息和响应消息, 使用端口接收和发送消息,默认为80端口. (底层Socket)

#双向通信
	HTTP协议决定了服务器与客户端之间的连接方式,无法直接实现消息推送(F5已坏),一些变相的解决办法:
	
	1.轮询 
		客户端定时向服务器发送Ajax请求,服务器接到请求后马上返回响应信息并关闭连接.
		
		优点: 后端程序编写比较容易. 
		缺点: 请求中有大半是无用,浪费带宽和服务器资源. 
		实例: 适于小型应用.
		
	2.长轮询 
		客户端向服务器发送Ajax请求,服务器接到请求后hold住连接,直到有新消息才返回响应信息并关闭连接,
		客户端处理完响应信息后再向服务器发送新的请求. 
		
		优点: 在无消息的情况下不会频繁的请求,耗费资小. 
		缺点: 服务器hold连接会消耗资源,返回数据顺序无保证,难于管理维护. Comet异步的ashx,
		实例: WebQQ、Hi网页版、Facebook-IM.

	3.长连接 
		在页面里嵌入一个隐蔵iframe,将这个隐蔵iframe的src属性设为对一个长连接的请求或是采用xhr请求,
		服务器端就能源源不断地往客户端输入数据. 
		
		优点: 消息即时到达,不发无用请求,管理起来也相对便. 
		缺点: 服务器维护一个长连接会增加开销. 
		实例: Gmail聊天
		
	4.Flash—Socket
		在页面中内嵌入一个使用了Socket类的 Flash 程序, JavaScript通过调用此Flash程序提供的Socket接口与服务器端的Socket接口进行通信,
		JavaScript在收到服务器端传送的信息后控制页面的显示. 
		
		优点: 实现真正的即时通信,而不是伪即时. 
		缺点: 客户端必须安装Flash插件,非HTTP协议,无法自动穿越防火墙. 
		实例: 网络互动游戏.
		
	5.Websocket
		HTML5提供的一种浏览器与服务器间进行全双工通讯的网络技术. 依靠这种技术可以实现客户端和服务器端的长连接,双向实时通信.
		//特点: 事件驱动, 异步, 使用ws或者wss协议的客户端socket, 能够实现真正意义上的推送功能,
		//缺点: 少部分浏览器不支持,浏览器支持的程度与方式有区别.
		
		Websocket 允许通过js与远程服务器建立连接,从而实现客户端与服务器间双向的通信.
		
		Websocket 的url开头是ws, 如果需要ssl加密可以使用wss,
		当调用构造方法构建一个 Websocket 对象后,就可以进行即时通信了. ---> new WebSocket(url)
			
#客户端说明
		<body>
			<input id="text" type="text"/>
			<button onclick="send()">Send</button>
			<button onclick="closeWebSocket()">Close</button>
			<div id="message"></div>
		</body>
		<script type="text/javascript">
			var websocket = null;

			//判断当前浏览器是否支持WebSocket
			if (!'WebSocket' in window) {
				alert('Not support websocket')
			} else {
				var userId = parseInt(Math.random() * (99 + 1), 10); //生成[0,99]的任意随机数
				websocket = new WebSocket("ws://localhost:8090/demo/websocket?id=" + userId);

				//监听事件 -> 连接成功建立时触发该事件
				websocket.onopen = function (event) {
					setMessageInnerHTML("open: " + new Date());
				};

				//监听事件 -> 连接关闭
				websocket.onclose = function (event) {
					setMessageInnerHTML("close: " + new Date() + " - " + event.code);
					websocket.send(event.code);
				};

				//监听事件 -> 接收到服务器发来的消息
				websocket.onmessage = function (event) {
					setMessageInnerHTML(event.data);
				};

				//监听事件 -> 连接发生错误
				websocket.onerror = function () {
					setMessageInnerHTML("error: " + new Date());
				};

				//监听事件 -> 监听窗口关闭事件,当窗口关闭时,主动去关闭websocket连接,防止连接还没断开就关闭窗口,server端会抛异常
				window.onbeforeunload = function () {
					if (null != websocket) {
						websocket.close();
					}
				};

				//将消息显示在网页上
				function setMessageInnerHTML(innerHTML) {
					document.getElementById('message').innerHTML += innerHTML + '<br/>';
				}

				//关闭连接
				function closeWebSocket() {
					websocket.close();
				}

				//向远程服务器发送数据
				function send() {
					var message = document.getElementById('text').value;
					websocket.send(message);
				}
			}
		</script>
	
#服务端说明
		@Configuration
		public class WebSocketConfig {

			//这个bean会自动注册使用 @ServerEndpoint 注解声明的 WebSocket-Endpoint.
			//注意: 如果使用独立的servlet容器,而不是直接使用 SpringBoot 内置容器,就不要注入此bean,因为它将由容器自己提供和管理
			@Bean
			public ServerEndpointExporter serverEndpointExporter() {
				return new ServerEndpointExporter();
			}
		}

		// 使用 SpringBoot 要使用注解 @Component
		// 而使用独立容器(tomcat)是由容器自己管理 WebSocket,但在 SpringBoot 中连容器都是 Spring 管理.
		//
		// 虽然 @Component 默认是单例模式的
		// 但 SpringBoot 还是会为每个 WebSocket 连接初始化一个bean,所以可以用一个静态 Set/Map 保存起来.
		@Component

		// 使用注解 @ServerEndpoint 可以将一个普通Java类作为 WebSocket 服务器的端点
		//
		// WebSocket 服务端运行在 ws://[Server端IP或域名]:[Server端口]/项目/push
		// 客户端浏览器已经可以对WebSocket客户端API发起 <<<HTTP长连接>>>.
		//
		// 使用 ServerEndpoint 注解的类必须有一个公共的无参数构造函数.
		@ServerEndpoint("/push")
		public class EchoEndpoint {

			/**
			 * 客户端注册时调用
			 *
			 * @param session 封装客户端的更多细节信息
			 */
			@OnOpen
			public void onOpen(Session session) {
			}

			/**
			 * 客户端关闭
			 *
			 * @param session 同上
			 * @param reason  关闭细节,如为什么关闭
			 */
			@OnClose
			public void onClose(Session session, CloseReason reason) {
			}

			/**
			 * 客户端异常
			 *
			 * @param t 异常信息
			 */
			@OnError
			public void onError(Throwable t) {
			}

			/**
			 * 收到浏览器客户端消息后调用
			 *
			 * @param message 客户端消息,可以是文本,也可以是二进制
			 */
			@OnMessage
			public void onMessage(String message) {
			}

			//更高级的注解,MaxMessageSize 属性可以被用来定义消息字节最大限制,在示例程序中,如果超过6个字节的信息被接收,就报告错误和连接关闭.
			// @Message(maxMessageSize = 6)
			// public void receiveMessage(String s) {
			// }
		}

		
#后台DEMO
		@Component
		@ServerEndpoint(value = "/websocket")
		public class MyWebSocket {
			
			//静态变量,用来记录当前在线连接数. 应该把它设计成线程安全的
			private static int onlineCount = 0;

			//旧版: concurrent包的线程安全Set,用来存放每个客户端对应的 MyWebSocket 对象
			// private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<>();

			//新版: 使用map对象,便于根据 userId 来获取对应的 MyWebSocket
			private static ConcurrentHashMap<String, MyWebSocket> webSocketMap = new ConcurrentHashMap<>();

			//区别: 非静态变量 和 静态变量
			//与某个客户端的连接会话,需要通过它来给客户端发送数据
			private Session session;

			//当前会话session对应的显式id
			private String userId = "";

			//客户端注册
			@OnOpen
			public void onOpen(Session session) {
				String id = this.userId = session.getRequestParameterMap().get("id").get(0);
				this.session = session;
				addOnlineCount(); //在线数加1
				webSocketMap.put(id, this); //加入Map
				System.out.println("有新连接加入: " + this.userId + " 当前在线人数为: " + getOnlineCount());

				sendMsg2All(this.userId + " - 已上线! 欢迎");
			}

			//客户端关闭
			@OnClose
			public void onClose() {
				if (null != webSocketMap.get(this.userId)) {
					subOnlineCount(); //在线数减1
					webSocketMap.remove(this.userId); //从Map中删除
					System.out.println("有一连接关闭: " + this.userId + " 当前在线人数为: " + getOnlineCount());

					sendMsg2All(this.userId + " - 已下线! 再见");
				}
			}

			//客户端异常
			@OnError
			public void onError(Session session, Throwable error) {
				System.out.println("发生错误: " + error);
				error.printStackTrace();
			}

			///收到浏览器客户端消息后调用的方法
			@OnMessage
			public void onMessage(String message, Session session) {
				System.out.println("来自客户端的消息: " + this.userId + " - " + message);

				if (message.contains("-")) {
					String[] split = message.split("-");
					webSocketMap.keySet().forEach(x -> {
						if (split[0].equalsIgnoreCase(x))
							sendMsg2One(this.userId + "->" + x + " - " + split[1], x); //点对点
					});
				} else {
					sendMsg2All(userId + " - " + message); //群发
				}
			}

			///群发消息
			public static void sendMsg2All(String message) {
				webSocketMap.values().forEach(x -> x.sendMsg(message));
			}

			///点对点发送消息
			public static void sendMsg2One(String message, String userId) {
				webSocketMap.get(userId).sendMsg(message);
			}

			///实现服务器主动推送
			private void sendMsg(String message) {
				try {
					this.session.getBasicRemote().sendText(message);
					// this.session.getAsyncRemote().sendText(message);
				} catch (IOException e) {
					System.out.println("异常---发送消息: " + e);
				}
			}

			//三个同步方法,线程安全
			private static synchronized int getOnlineCount() {
				return onlineCount;
			}

			private static synchronized void addOnlineCount() {
				MyWebSocket.onlineCount++;
			}

			private static synchronized void subOnlineCount() {
				MyWebSocket.onlineCount--;
			}
		}

//}

//{--------<<<DEBUG>>>---------------------------------------------------------------------
#开启DEBUG
	debug=true //配置文件中添加
	
#分析日志
	Positive match //列出匹配到对应类的配置项
	Negative match //不包括某个配置项的原因
	
#举例分析
	1.ConditionalOnClass
		//所必须的类在classpath路径下存在时,才会去解析对应的配置文件. .
		对于'DataSourceAutoConfiguration'而言,所必须的类是指: '*.DataSource'和'*.EmbeddedDatabaseType',
		只有这两个类都存在时,才会去配置对应的数据库资源.	

	2.ConditionalOnMissingClass
		//所必须的类在classpath路径下找不到.
		
	3.探测条件
		OnClassCondition    //表示匹配的类型存在与否
		OnBeanCondition     //指定bean实例存在与否
		OnPropertyCondition //检查指定属性是否存在
		
		DataSourceAutoConfiguration matched:
		  - @ConditionalOnClass found required classes 'javax.sql.DataSource', 'org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType' (OnClassCondition)

		DataSourceAutoConfiguration.PooledDataSourceConfiguration matched:
		  - AnyNestedCondition 2 matched 0 did not; NestedCondition on DataSourceAutoConfiguration.PooledDataSourceCondition.PooledDataSourceAvailable PooledDataSource found supported DataSource; NestedCondition on DataSourceAutoConfiguration.PooledDataSourceCondition.ExplicitType @ConditionalOnProperty (spring.datasource.type) matched (DataSourceAutoConfiguration.PooledDataSourceCondition)
		  - @ConditionalOnMissingBean (types: javax.sql.DataSource,javax.sql.XADataSource; SearchStrategy: all) did not find any beans (OnBeanCondition)

//}






