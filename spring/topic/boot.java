	http://start.spring.io/
	
///-----------------------<<<boot>>>----------------------------------	
#简化依赖管理
	将各种功能模块进行划分,封装成一个个 Starter, 更容易的引入和使用,
	提供一系列的Starter,将各种功能性模块进行了划分与封装,
	让我们可以更容易的引入和使用，有效的避免了用户在构建传统Spring应用时维护大量依赖关系而引发的JAR冲突等问题。

#自动化配置: 为每一个Starter都提供了自动化的Java配置类
#嵌入式容器: 使得应用的打包运行变得非常的轻量级
#监控端点: 通过Actuator模块暴露的http接口,可以轻松的了解和控制 Boot 应用的运行情况
	
///-----------------------<<<注意点>>>----------------------------------

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
	
	
///-----------------------<<<项目启动>>>----------------------------------
	0.脚本启动
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

	1.服务启动
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





///----------------------<<<静态资源>>>------------------------------------------------------
#静态资源	http://www.webjars.org/
	///webjars -> 将web前端资源(js,css等)打成jar包,然后借助Maven进行统一管理
	
	0.mvn引用
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>webjars-locator</artifactId> 
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
		
	1.页面引用
		//webjars-locator: 作用是可以省略 webjars 的版本.(比如省略下面 3.3.1)
		//<script th:src="@{/webjars/jquery/3.3.1/jquery.min.js}"></script>
		
		<script th:src="@{/webjars/jquery/jquery.min.js}"></script>
		<script th:src="@{/webjars/bootstrap/js/bootstrap.min.js}"></script>
		<link rel="stylesheet" th:href="@{/webjars/bootstrap/css/bootstrap.min.css}"/>

	2.代码配置
		@Configuration
		public class MyWebMvcConfigurer implements WebMvcConfigurer {

			// 静态资源映射: url访问路径 --> 资源存放的真实路径(可变长度,可配置多个).
			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				// x/imgs/b.jpg --> 类路径/resources/image/b.jpg (或) war包目录/imgs/b.jpg
				registry.addResourceHandler("/imgs/**")
						.addResourceLocations("classpath:/image/", "file:imgs/");

				// x/logs/demo.log --> war包目录/logs/demo.log
				registry.addResourceHandler("/logs/**")
						.addResourceLocations("file:logs/");
			}

			// 视图映射
			@Override
			public void addViewControllers(ViewControllerRegistry registry) {
				// url访问 / 或 /index.html, 都会响应页面 /login.html
				registry.addViewController("/").setViewName("login");
				registry.addViewController("/index.html").setViewName("login");

				// http://ip:port/blue/main.html --> /templates/dashboard.html
				registry.addViewController("/main.html").setViewName("dashboard");
			}

			// 注册拦截器
			@Override
			public void addInterceptors(InterceptorRegistry registry) {
				WebMvcConfigurer.super.addInterceptors(registry);

				registry.addInterceptor(new MyHandlerInterceptor())
						// 需要拦截 (/**表所有)
						.addPathPatterns("/**")
						// 不拦截
						.excludePathPatterns("/asserts/**", "/webjars/**", "/imgs/**", "/logs/**", "/", "/index.html",
								"/user/login", "/error");
			}
		}
		
	3.配置解释
		(0).静态资源映射:
			将请求url为 'addResourceHandler' 映射到 'addResourceLocations'.
		(1).视图映射: 
			对于url访问'/'或'/index.html',服务器都会响应页面'/templates/login.html'.
		(2).声明拦截器: 
			对于'拦截'的页面或接口,直接url访问,Session为空,则转发至"/index.html",后续逻辑(0).
		(3).注册拦截器: 
			对于'不拦截',都是直接访问,不经拦截器逻辑. /*其中,登陆接口不能拦截*/
			
			
	4.boot约定
		动态模板: 放在 templates 目录.
		静态资源: 放在 static 目录, 包括html静态页面,静态资源(图片,CSS等). 
		
		\static\filter\list.html --> http://192.168.8.7:8090/demo/filter/list.html (直接访问,无需映射)

#登陆逻辑
	0.前台页面
		//<p>是否显示: msg不为空则显示
		<form method="post" th:action="@{/user/login}">
			<td align="center" colspan="2"><input type="submit" value="登录">
				<p th:if="${! #strings.isEmpty(msg)}" th:text="${msg}"></p>
			</td>
		</form>
	
	1.后台逻辑		
		@PostMapping("/user/login") //post请求
		public String userlogin(@RequestParam("username") String userName, @RequestParam("userpwd") String userPwd, Model
				model, HttpSession session) {
			if (StringUtils.isEmpty(userPwd)) {
				model.addAttribute("msg", "用户名密码不正确!!");
				return "login"; //响应页面login.html (其中,必须去掉前缀'/')
			} else {
				session.setAttribute("user", userName); //保存Session,用于拦截验证
				return "redirect:/main.html"; //重定向: 不会造成表单重复提交.(forward会)
			}
		}

	2.拦截未登录用户
		//声明拦截器 (注册拦截器见上面)
		public class MyHandlerInterceptor implements HandlerInterceptor {

			// 在目标方法之前被调用 ---> 适用于权限,日志,事务等.
			@Override
			public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws
					ServletException, IOException {
				log.info(System.getProperty("line.separator"));//系统级别的换行符

				String url = request.getRequestURL().toString();
				log.info("url - " + url + "; method - " + request.getMethod());//请求url

				StringBuilder sb = new StringBuilder();
				Map<String, String[]> map = request.getParameterMap();
				map.forEach((x, y) -> sb.append(x).append(":").append(Arrays.toString(map.get(x))).append("; "));
				log.info("params - {{}}", sb);//请求params

				if (null == request.getSession().getAttribute("user")) {//未登录
					request.setAttribute("msg", "没有权限,请先登录!");
					request.getRequestDispatcher("/index.html").forward(request, response);//转发

					return false;//不会再调用后续的拦截器和目标方法
				} else {
					return true;//继续调用后续的拦截器和目标方法
				}
			}
		}
		
	3.防止表单重复提交
	#(0).表单提交后转发到目标页面; 不做其他操作,直接刷新页面,表单会提交多次
		原因: 转发到目标页面,点击刷新会一直都会刷新之前的请求。
		解决: 不用转发,直接重定向到目标页面

    #(1).提交表单时,如果网速较差,可能会导致按钮点击多次,这种情况也会导致表单重复提交
		解决: 点击提交按钮之后,通过js使按钮不可用.

    #(2).表单提交成功后,直接点击浏览器上回退按钮,不刷新页面,然后点击提交按钮再次提交表单
		原因: 服务器在处理请求时,不检查是否为重复提交的请求
		解决: 使用一个token的机制(token -> 令牌)
			服务器在处理请求之前先来检查浏览器的token.
			token由服务器来创建,并交给浏览器,浏览器在向服务器发送请求时需要带着这个token.
			服务器处理请求前检查token是否正确,如果正确,则正常处理; 否则返回一个错误页面.
			服务器所创建的token只能使用一次,token一般使用一个唯一的标识.

///---------------------<<<CRUD>>>---------------------------------------------------------------
#CRUD
	| 列表页面        | emp/list  | GET      |
	| 跳转页面(新增)  | emp       | GET      |
	| 新增接口        | emp       | POST     |
	| 跳转页面(修改)  | emp/{id}  | GET      |
	| 修改接口        | emp       | PUT      |
	| 删除接口        | emp/{id}  | DELETE   |
	
	//将请求 POST 转化为 PUT,DELETE
	// (1).配置 HiddenHttpMethodFilter. (boot已自动配置)
		<filter>
			<filter-name>HiddenHttpMethodFilter</filter-name>  
			<filter-class>org.springframework.web.filter.HiddenHttpMethodFilter</filter-class>  
		</filter>
	// (2).页面创建一个post表单
	// (3).新建<input/>隐藏标签,name="_method"，value="put/delete"
	
		<form method="post" th:action="@{/person/}+${person.id}">
			<input type="hidden" name="_method" value="delete">
			<input type="submit" value="删除">
		</form>
	
#员工列表
	0.列表页面 
		<a th:href="@{/person/list}">列表页面</a>
		
	1.初始化列表页面		/// ---> /persons - GET
		@GetMapping("/list")
		public String listAll(Model model) {
			List<Person> personList = personMapper.listAll();
			model.addAttribute("personList", personList);

			return "person/list";//响应页面: person/list.html
		}
		
	2.列表页面
		<body>
		<table border="1" cellpadding="20" cellspacing="0" align="center">
			<tr>
				<th>姓名</th> <th>性别</th> <th>住址</th> <th>操作</th>
			</tr>
			<tr th:if="${0==personList.size()}">
				<td colspan="4" th:text="当前列表为空"></td>
			</tr>
			<tr th:each="person : ${personList}" th:object="${person}"> //配合使用 th:object 和 *{...}
				<td th:text="*{name}"></td>
				<td th:text="*{gender}?'男':'女'"></td>
				<td th:text="*{city.name}"></td>
				<td colspan="2">
					<a class="modify" th:href="@{/person/}+*{id}">修改</a>
					&nbsp;&nbsp;
					<a class="deletePerson" href="#" th:attr="del_uri=@{/person/}+*{id}">删除</a>
				</td>
			</tr>
		</table>
		<div style="text-align: center;">
			<a th:href="@{/person}">新增员工</a>
		</div>
		
		<form method="post" id="deletePersonForm">
			<input type="hidden" name="_method" value="delete">
		</form>
		<script> //动态js
			$('.deletePerson').click(function () {
				var del_uri = $(this).attr("del_uri");//获取当前按钮的'del_uri'属性
				$("#deletePersonForm").attr("action", del_uri).submit();//动态设置<form>的action属性,并提交
				return false;//取消按钮的默认行为
			});
		</script>
		</body>	
	
#新增员工
	0.跳转新增页面
		<a th:href="@{/person}">新增员工</a>
		
	1.初始化新增页面		/// ---> /person - GET
		@GetMapping("")
		public String toPersonPage(Model model) {
			List<City> cityList = flowerMapper.getCityList();
			model.addAttribute("cityList", cityList);//初始化city列表

			return "person/person";//响应页面: person/person.html
		}
		
	2.新增页面(同修改)
		
	3.新增接口				/// ---> /person - POST
		@PostMapping("")
		public String savePerson(Person person) {
			personMapper.savePerson(person);

			return "redirect:/person/list"; //重定向接口: person/list
		}
		
#修改员工
	0.跳转修改页面
		<a th:href="@{/person/}+${person.id}">修改</a> //路径拼接
		
	1.初始化修改页面		/// ---> /person/{id} - GET
		@GetMapping("/{id}")
		public String toPersonPage(@PathVariable Integer id, Model model) {
			Person person = personMapper.getById(id);
			model.addAttribute("person", person);
			model.addAttribute("cityList", flowerMapper.getCityList());

			return "person/person";//响应页面: person/person.html
		}
		
	2.回显数据修改页面
		//增加和修改使用同一页面,区分方式: person对象是否为空 ${null!=person}
		<form method="post" th:action="@{/person}">
			//修改: 发送 PUT 请求
			<input type="hidden" name="_method" value="put" th:if="${null!=person}">
			//修改: 需要提交id
			<input type="hidden" name="id" th:value="${person.id}" th:if="${null!=person}">

			<table>
				<tr>
					<td>姓名:</td>
					<td><input type="text" name="name" th:value="${null!=person}?${person.name}"></td>
				</tr>
				<tr>
					<td>性别:</td> //th:checked -> radio标签是否选中.
					<td><input type="radio" name="gender" value="1" th:checked="${null!=person}?${person.gender}">男
						<input type="radio" name="gender" value="0" th:checked="${null!=person}?${!person.gender}">女
					</td>
				</tr>
				<tr>
					<td>住址:</td>
					<td><select name="city.id"> //th:selected -> 回显对象person.city.id和遍历city.id相同,则选中
						<option th:each="city : ${cityList}" th:value="${city.id}" th:text="${city.name}"
								th:selected="${null!=person}?${person.city.id==city.id}"></option>
					</select></td>
				</tr>
				<tr> //回显对象person是否为空,不同显示'修改','新增'
					<td colspan="2"><input type="submit" th:value="${null!=person}?'修改':'新增'"></td>
				</tr>
			</table>
		</form>
	
	3.修改接口			/// ---> /person - PUT
		@PutMapping("")
		public String updateById(Person person) {
			personMapper.updatePersonById(person);

			return "redirect:/person/list"; //重定向接口: person/list
		}
	
#删除员工
	0.点击删除
		//delete请求必须<form>,且有一个隐藏标签<input>
        <td colspan="2">
            <a th:href="@{/person/}+${person.id}">修改</a>
            &nbsp;&nbsp;
            <form method="post" th:action="@{/person/}+${person.id}">
                <input type="hidden" name="_method" value="delete">
                <input type="submit" value="删除">
            </form>
        </td>

	1.删除接口			/// ---> /person/{id} - DELETE
		@DeleteMapping("/{id}")
		public String deleteById(@PathVariable Integer id) {
			personMapper.deleteById(id);

			return "redirect:/person/list"; //重定向接口: person/list
		}

	2.机制改进 -> //以上方式,页面有多少条数据,就会产生多少个 form 表单. 待优化!!!
		<table border="1" cellpadding="20" cellspacing="0" align="center">
			<tr>
				<td colspan="2">
					<a class="modify" th:href="@{/person/}+${person.id}">修改</a>
					&nbsp;&nbsp;
					<a class="deletePerson" href="#" th:attr="del_uri=@{/person/}+${person.id}">删除</a>
				</td>
			</tr>
		</table>
		
		//将<form>从<table>提出,单独处理
		<form method="post" id="deletePersonForm">
			<input type="hidden" name="_method" value="delete">
		</form>
	
		//动态添加'删除'的点击事件
		<script>
			$('.deletePerson').click(function () {
				var del_uri = $(this).attr("del_uri");//获取当前按钮的'del_uri'属性
				$("#deletePersonForm").attr("action", del_uri).submit();//动态设置<form>的action属性,并提交
				return false;//取消按钮的默认行为
			});
		</script>














///---------------------<<<邮件>>>--------------------------------------------------------
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

///---------------------<<<配置>>>------------------------------------------------------------------
#文件格式yaml
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


///---------------------<<<多环境切换>>>------------------------------------------
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


///---------------------<<<ORM>>>------------------------------------------
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

///---------------------<<<Druid>>>------------------------------------------
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

///---------------------<<<fastjson>>>------------------------------------------
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



///---------------------<<<错误处理机制>>>------------------------------------------
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



///---------------------<<<Actuator>>>------------------------------------------
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

