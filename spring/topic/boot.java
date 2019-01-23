	http://start.spring.io/
	
#boot优势
	简化依赖管理
		将各种功能模块进行划分,封装成一个个 Starter, 更容易的引入和使用,
		提供一系列的Starter,将各种功能性模块进行了划分与封装,
		让我们可以更容易的引入和使用，有效的避免了用户在构建传统Spring应用时维护大量依赖关系而引发的JAR冲突等问题。

	自动化配置: 为每一个Starter都提供了自动化的Java配置类
	嵌入式容器: 使得应用的打包运行变得非常的轻量级
	监控の端点: 通过Actuator模块暴露的http接口,可以轻松的了解和控制 Boot 应用的运行情况
	
//{--------<<<注意点>>>--------------------------------------------------------------------
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


#控制器返回页面
	@GetMapping("/list")
	public String listAll(Model model) {
		List<Person> personList = personMapper.listAll();
		model.addAttribute("personList", personList);

		return "person/list";//响应页面,不能加前缀'/'
	}
//}
	
//{--------<<<启动>>>----------------------------------------------------------------------
#脚本启动
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

#linux服务启动
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
		LOG_FOLDER=/var/tmp/blue/logs/		#该目录必须存在

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

	| 列表页面        | emp/emps  | GET      |
	| 跳转页面(新增)  | emp/emp   | GET      |
	| 新增接口        | emp/emp   | POST     |
	| 跳转页面(修改)  | emp/{id}  | GET      |
	| 修改接口        | emp/emp   | PUT      |
	| 删除接口        | emp/{id}  | DELETE   |
	
#POST转化为PUT,DELETE
	1.配置HiddenHttpMethodFilter. (boot已自动配置)
		<filter>
			<filter-name>HiddenHttpMethodFilter</filter-name>  
			<filter-class>org.springframework.web.filter.HiddenHttpMethodFilter</filter-class>  
		</filter>
		
	2.页面创建(POST表单 + 隐藏标签)
		<form method="post" th:action="@{/person/}+${person.id}">
			<input type="hidden" name="_method" value="delete"> //隐藏标签 name + value
			
			<a href="#" onclick="delEmp(this)" th:attr="url=@{/emp/}+${emp.id}">删除</a>
		</form>
	
#列表
	0.跳转列表页面
		<a th:href="@{/person/list}">列表页面</a> //超链接对应请求 GET
		
	1.跳转逻辑
		@RequestMapping("/emps")
		public String list(Model model) {
			model.addAttribute("emplist", EmpUtils.listAll());
			return "/emp/emps";
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
		<a th:href="@{/emp/emp}">新增</a>
		
	1.跳转逻辑
		@RequestMapping("/emp")
		public String add(Model model) {
			model.addAttribute("cityList", EmpUtils.listCity()); //初始化列表 City
			return "/emp/emp";
		}
		
	2.新增页面(同修改)
		
	3.新增接口
		@PostMapping("/emp")
		public String add(Emp emp) {
			EmpUtils.empList.add(emp);
			return "redirect:/emp/emps";
		}
		
#修改
	0.跳转修改页面
		<a th:href="@{/emp/}+${emp.id}">修改</a> //路径拼接
		
	1.跳转逻辑
		@GetMapping("/{id}")
		public String add(@PathVariable Integer id, Model model) {
			model.addAttribute("emp", EmpUtils.getById(id));
			model.addAttribute("cityList", EmpUtils.listCity());
			return "emp/emp";
		}
		
	2.回显数据修改页面
		//增加和修改使用同一页面,区分方式: 回显 emp 是否为空 --> ${null!=person}
		<form method="post" th:action="@{/emp/emp}">
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
		@DeleteMapping("/{id}")
		public String delete(@PathVariable Integer id) {
			EmpUtils.empList.deleteById(id);
			return "redirect:/emp/emps";
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
		@DeleteMapping("/{id}")
		@ResponseBody
		public String delete(@PathVariable Integer id) {
			EmpUtils.empList.deleteById(id);
			return "success";
		}
		
//}

//{--------<<<emil>>>----------------------------------------------------------------------
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

//{--------<<<ORM>>>-----------------------------------------------------------------------
#整合Druid数据源 //sp1.x默认数据源为: org.apache.tomcat.jdbc.pool.DataSource
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.10</version>
        </dependency>
	
	0.配置文件
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
		
#整合JdbcTemplate
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

#整合JPA
	//JPA(Java-Persistence-API): Java持久化规范,即把内存中的数据保存到可永久存储的设备中
	//也是基于ORM(Object Relational Mapping)思想
	
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
	
	0.配置文件
		//可选参数create: 每次启动都会删除旧表,新建一个空表
		spring.jpa.hibernate.ddl-auto=update //根据实体类创建/更新数据库表
		spring.jpa.show-sql=true //控制台显示sql语句
		
	1.数据表和实体类之间的映射关系
		@Data
		@Entity //表明是一个JPA实体->自动建表
		@Table(name = "tb_person") //默认表名为类名小写
		public class Person {
			
			@Id //主键,自增
			@GeneratedValue(strategy = GenerationType.IDENTITY)
			private int id;
			
			@Column(name = "age") //和数据表对应的列,及列名
			private int age;

			@Column //省略,则默认列名为->first_name
			private String firstName;
		}
	
	2.DAO操作类
		//JpaRepository<实体类, 数据表主键id的类型>
		public interface PersonRepository extends JpaRepository<Person, Integer> { }
		
		//接口 CrudRepository -> 最基本的增删改查功能
		
		//接口 PagingAndSortingRepository -> 分页和排序功能
			Iterable<T> findAll(Sort sort); //排序
			Page<T> findAll(Pageable pageable); //分页
	
	3.测试Controller
		@Autowired
		PersonRepository personRepository;

		//	http://localhost:8080/blue/person?name=ceshi&age=20
		//	{"id":4,"name":"ceshi","age":20}
		@GetMapping("/person")
		public Person insert(Person person) {
			return personRepository.save(person);
		}

		//	http://localhost:8080/blue/person/1
		//	{"id":1,"name":"zhang","age":18}
		@GetMapping("/person/{id}")
		public Person get(@PathVariable("id") Integer id) {
			return personRepository.findOne(id);
		}

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
#参照 ErrorMvcAutoConfiguration -> 错误处理的自动配置.
	//一但系统出现 4xx 或者 5xx 之类的错误, 'ErrorPageCustomizer' 就会生效, 它会发送/error请求;
		@Value("${error.path:/error}")    
		private String path = "/error";
	
	//'/error'请求会被 'BasicErrorController' 处理, 它有两种处理机制:
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
		
	//其中, html类型数据由 'DefaultErrorViewResolver' 解析产生, 规则为:
		1).有模板引擎的情况下: error/状态码
		2).没有模板引擎 (模板引擎找不到这个错误页面), 静态资源文件夹下找
		3).以上都没有错误页面, 就使用SpringBoot默认的错误提示页面
	
	//错误页面的数据信息由 'DefaultErrorAttributes'提供, 其中包括:
		timestamp: 时间戳; status: 状态码; 
		error: 错误提示; exception: 异常对象
		message: 异常消息; errors: JSR303数据校验的错误都在这里
			
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