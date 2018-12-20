
#@SpringBootApplication '组合注解'
	@ComponentScan //组件扫描, 指定Spring在哪些包及其下级包中寻找bean.
	@SpringBootConfiguration //使用Spring基于Java的配置,而非xml.
	@EnableAutoConfiguration //自动配置, 称为 @Abracadabra(咒语)!!!

#@Controller & @RestController
	@Controller: //ctrl方法返回页面, 默认视图解析器 InternalResourceViewResolver
	@RestController: //ctrl方法返回JSON,XML等到页面时使用, 组合注解(@ResponseBody + @Controller)
	
#@Configuration 
	Spring配置类
#@Bean: 
	只会被Spring调用一次; 产生一个id为方法名的Bean对象; 对象管理在IOC容器中; 配合@Configuration,@Component使用
	
		@Configuration
		public class AppConfig {
			@Bean("infoService00") //默认情况,bean的id和方法名相同,也可通过name属性显示定义
			public TnfoService infoService() {
				return new TnfoServiceImpl();
			}
		}
		<beans> //等同于
			<bean id="infoService00" class="com.example.TnfoServiceImpl"/>
		</beans>
		
	
///------------------------------------------------------------------------------------------------
	
#请求映射
	0.RequestMapping ///处理请求地址映射的注解, 可标注在类或方法上.
		(0).value: 请求的实际地址; 默认属性,不显示指定则是配置此值. //@RequestMapping("/hello")
		(1).method: 请求方式; GET,POST,PUT,DELETE...等.
			//@RequestMapping(method = RequestMethod.GET) ===> @GetMapping
		(2).params: 请求中必须包含某些参数值,才让该方法处理.
		(3).headers: 同上.
			//参数必须包含'name'; 如有'age',则'age'不能等于10
			@GetMapping(value = "/hello", params = { "name", "age!=10" })
			public String hello() {
				
				//http://127.0.0.1:8090/demo/hello?name=5			(T)
				//http://127.0.0.1:8090/demo/hello?name=5&age=11	(T)
				//http://127.0.0.1:8090/demo/hello?name=5&age=10	(F)
				//http://127.0.0.1:8090/demo/hello?names=5&age=11	(F)
			}
			
	1.PathVariable ///将url中的占位符映射到方法的入参
		//两个'name'必须一致,入参名'args'无所谓
		@GetMapping("/hello/{name}")
		public String hello(@PathVariable("name") String args) {
		
			//http://127.0.0.1:8090/demo/hello/spring ---> hello spring
			return MessageFormat.format("{0} {1}", HELLO, args);
		}

	2.RequestParam ///将{请求参数}映射到方法的入参
		/**
		 * value: 参数名,必须和url中的参数名保持一致
		 * required: 是否为必须,默认true 
		 * defaultValue: 参数的默认值,当url中没有该参数时取默认值
		 */
		@GetMapping("/hello")
		public String hello(@RequestParam(value = "name", required = true) String args0,
				@RequestParam(value = "age", required = false, defaultValue = "18") Integer args1) {
					
			//http://127.0.0.1:8090/demo/hello?name=wang&sex=99 ---> hello wang 18
			return MessageFormat.format("{0} {1} {2}", HELLO, args0, args1);
		}	
		
	3.RequestHeader ///Header属性.(属性同上)
		@GetMapping("/hello")
		public String hello(@RequestHeader("Connection") String args0) {
			
			//http://127.0.0.1:8090/demo/hello1 ---> hello keep-alive
			return MessageFormat.format("{0} {1}", HELLO, args0);
		}
		
	4.CookieValue///Cookie属性.(属性同上)
		@GetMapping("/hello")
		public String hello(@CookieValue("JSESSIONID") String args0) {
			
			//http://127.0.0.1:8090/demo/hello ---> hello 877E8C669E698112CC3227F61743AB1B
			return MessageFormat.format("{0} {1}", HELLO, args0);
		}
		
	5.将请求参数自动填充到pojo///支持级联填充
		@GetMapping("/hello")
		//@PostMapping("/hello") //(form表单提交)
		public String hello(Car args0) {
			
			//http://127.0.0.1:8090/demo/hello?brand=Audi&price=720000&address.province=shanxi&address.city=yuncheng
			//hello Car(brand=Audi, price=720000.0, address=Address(province=shanxi, city=yuncheng))
			return MessageFormat.format("{0} {1}", HELLO, args0);
		}
		
	6.使用Servlet原生API作为入参
		//HttpServletRequest; HttpServletResponse; HttpSession;
		//java.security.Principal; Locale; InputStream; OutputStream; Reader; Writer
		@PostMapping("/hello")
		public String hello(HttpServletRequest request) {
			String method = request.getMethod();//GET
			String ip = request.getRemoteAddr();//192.168.8.7
			String url = request.getRequestURL().toString();

			//hello POST 192.168.8.7 http://192.168.8.7:8090/demo/hello
			return MessageFormat.format("{0} {1} {2} {3}", HELLO, method, ip, url);
		}
		
	7.ResponseBody
		///方法的返回结果直接写入 HTTP 响应正文(ResponseBody)中
		///返回数据不是html页面,而是其他格式数据时(如json,xml等)使用.(即异步获取数据)
	
	8.RequestBody
		///读取body部分数据,使用默认 HttpMessageConverter 解析,然后把相应数据绑定到 ctrl 方法的参数上
		multipart/form-data: //不能处理
		application/x-www-form-urlencoded: //PUT时必须; GET,POST时可选(@RequestParam, @ModelAttribute 也可以)
		其他(application/json, application/xml等): //必须
		
		//前台页面发出异步请求
		function login() {
			var user= {"username" : username,"password" : password};
			$.ajax({
					url : "http://...../user/login",
					type : "POST",
					async : true,
					contentType: "application/json; charset=utf-8", //解析必须使用 @RequestBody
					data : JSON.stringify(user), //必须使用JSON.stringify()将JSON对象转化为JSON字符串
					dataType : 'json',
					success : function(data) {
						alert("success!!!")
					},
					error : function(XMLHttpRequest, textStatus, errorThrown) {
						alert("出现异常,异常信息："+textStatus,"error");
					}
			 });
		};
		
		//后台解析
		@ResponseBody //这样就不会再被解析为跳转路径,而是直接将user对象写入 HTTP 响应正文中
		@RequestMapping("/user/login")
		public String test(@RequestBody User user){ //将ajax(datas)发出的请求写入 User 对象中
			String username = user.getUsername();
			String password = user.getPassword();
			return HELLO;
		}
		
		//另一种: 前台页面
		function login() {
			var user= {"username" : username,"password" : password};
			$.ajax({
				url : "http://...../user/login", //请求的ContentType默认是'application/x-www-form-urlencoded'
				type : "POST",
				async : true,
				data : user,
				dataType : 'json',
				success : function(data) {
					alert("success!!!")
				}
			});
		};
		
		//对应后台解析
		@RequestMapping("/user/login")
		public void test(User user, String username, String password){
			//可封装User对象, 亦可使用单个参数username, 甚至User对象和单个username参数混用
			System.out.println(user);
			System.out.println("username: " + username);
			System.out.println("password: " + password);
		}
		
	9.转发和重定向
		//一般情况下,控制器方法返回字符串都会被当成逻辑视图名处理.
		//但如果返回的字符串中带 'forward:' 或 'redirect:' 前缀时,SpringMVC 会特殊处理.
	
#处理模型数据
	0.ModelAndView ///既包含'视图'信息,也包含'模型数据'信息
		@GetMapping("/hello")
		public ModelAndView hello(Car args0, ModelAndView mv) {
			mv.setViewName(HELLO);//视图
			mv.addObject("hello", SystemUtils.getAll());//模型数据

			//SpringMVC 会把 ModelAndView 的 model 中数据放入到 request 域对象中
			return mv;
		}

	1.Map及Model及ModelMap ///可做参数,或返回值; 自动将数据添加到模型中
		@GetMapping("/hello")
		public String hello(Model model) {
			model.addAttribute("hello", SystemUtils.getAll());
			
			//底层是将数据封装到 ModelAndView 的 ModelMap 对象中, 将返回值 HELLO 封装到 View对象中
			return HELLO;
		}

	2.SessionAttributes
		//若希望在多个请求之间共用某个模型属性数据,则可以在控制器类上标注一个 @SessionAttributes
		//SpringMVC 将模型中对应的属性暂存到 HttpSession 中.
		@RequestMapping("/testSessionAttributes")
		public String testSessionAttributes(Map<String, Object> map){
			User user = new User("Tom", "123456", "tom@x.com", 15);
			map.put("user1", user);
			map.put("school", "x");
			return SUCCESS;
		}
	
		//除了可以通过属性名指定需要放到会话中的属性(实际上使用的是 value 属性值),
		//还可以通过模型属性的对象类型指定哪些模型属性需要放到会话中(实际上使用的是 types 属性值)
		//注意: 该注解只能放在类的上面. 而不能修饰放方法. 
		@SessionAttributes（value={"user1","user2"},type={Dept.class})
		
		//前台页面获取数据
		request user: ${requestScope.user1 }
		<br><br>
		session user: ${sessionScope.user1 }
		<br><br>
		request school: ${requestScope.school }
		<br><br>
		session school: ${sessionScope.school }

	3.ModelAttribute ///每个控制器方法执行之前,都会先执行一次此注解标注的方法
		//执行流程:
		(0).执行 @ModelAttribute 修饰的方法, 从数据库中取出对象, 把对象放入到了 Map 中. 其中键为: user
		(1).SpringMVC 从 Map 中取出 User 对象, 并把表单的请求参数赋给该 User 对象的对应属性,重新组装 User
		(2).SpringMVC 把新组装的 User 作为参数传入目标方法
	
		//注意: 放入 Map 的key需和目标方法入参类型的小驼峰一致! 如'user'
		@ModelAttribute
		public void getUser(@RequestParam(value="id", required=false)Integer id, Map<String, Object> map){
			if(id != null){
				User user = new User(id,"wang", "123456", 12);//模拟从数据库中获取对象(id,name,pwd,age)
				map.put("user", user);
			}
		}

		//场景: 修改年龄 ---> 前台传参缺少密码,不完整.
		//使用以上注解,则可在调用此控制器之前,先查询一次数据库,获得对应id的User对象
		//然后再将前台表单数据赋值到此user对象(pwd除外),此时User为完整对象.
		@RequestMapping(value="/testModelAttribute")
		public String testModelAttribute(User user){
		   System.out.println("修改" + user);
		   return HELLO;
		}
		
	4.SpringMVC确定目标方法入参POJO类型的过程
		(1).确定key
			1.1).若目标方法的参数没有使用 @ModelAttribute 作为修饰, 则 key 为 POJO 类型名小驼峰 'person'
			1.2).若使用了 @ModelAttribute 来修饰, 则 key 为注解的 value 属性值 'p'
			
			@RequestMapping(value = "/testModelAttribute")
			public String testModelAttribute(/*@ModelAttribute(value = "p")*/ Person person) {
				System.out.println("修改" + person);
				return HELLO;
			}
		
		(2).在 implicitModel(即Map) 中查找 key 对应的对象是否存在???
			2.1).存在, 则作为入参传入
			
			2.2).不存在. 检查当前 Handler 是否使用注解 @SessionAttributes???
				2.2.1).使用. 检查注解的 value 属性值是否包含 key??? 
					包含,且能找到对应的value对象, 则会从 HttpSession 中获取 key 对应的对象,直接传入到目标方法的入参中
					包含,但不能找到对应的value对象,抛出异常 //增加标记 @ModelAttribute 注解的方法
					
				2.2.2).未使用. 或使用但不包含. 则会通过反射来创建 POJO 类型的参数,传入为目标方法的参数
				
		(3).SpringMVC 会把 key 和 POJO 类型的对象保存到 implicitModel 中, 进而会保存到 request 中

#视图View和视图解析器ViewResolver 
	///控制器方法执行完成后,最终返回一个"ModelAndView对象"
	//对于返回 String,ModelMap 等方法,SpringMVC 也会在内部封装成一个 ModelAndView 对象(包含逻辑名和模型对象的视图)

	SpringMVC 借助视图解析器(ViewResolver)得到最终的视图对象(View),最终可以是 JSP,Excel 等各种表现形式的视图
	为了实现视图模型和具体实现技术的解耦，Spring 在 org.springframework.web.servlet 包中定义了一个高度抽象的 View 接口:

	'视图对象'由'视图解析器'负责实例化. 由于视图是无状态的,所以不会有线程安全的问题

	///视图解析器
		SpringMVC 为逻辑视图名的解析提供了不同的策略,可以在 SpringWeb 上下文中'配置一种或多种解析策略'
		并指定他们之间的先后顺序. 每一种映射策略对应一个具体的视图解析器实现类.

	0.设置默认页面(两种方案,等同)
		//(1).ctrl里添加一个"/"的映射路径
		@GetMapping("/")
		public String index() {
			return HELLO;
		}

		//(2).设置默认的View跳转页面
		@Configuration
		public class MyWebMvcConfigurer implements WebMvcConfigurer {

			//视图映射
			@Override
			public void addViewControllers(ViewControllerRegistry registry) {
				//http://ip:port/blue/ 或 http://ip:port/blue/index.html
				//--> 都会访问 /templates/index.html
				registry.addViewController("/").setViewName("index");
				registry.addViewController("/index.html").setViewName("index");

				//http://ip:port/blue/main.html --> /templates/main.html
				registry.addViewController("/main.html").setViewName("main");
			}
			
			//视图映射 <==> 以下内容
			@RequestMapping(value = { "/", "/index.html" })
			public String sign_in() {
				return "index";
			}
		}

#数据格式化及校验
	///JSR303 是Java为Bean数据合法性校验提供的标准框架,它已经包含在 JavaEE 6.0 中.

	@Null / @NotNull				//必须为null(不为null)
	@AssertTrue / @AssertFalse		//必须为true(false)
	@Min(value) / @Max(value)		//必须是一个整数值,其值必须>=value
	@DecimalMin(value) / @DecimalMax(value)		//必须是一个小数值(精度问题),其值必须>=value
	@Size(max=, min=)				//元素size必须在指定的范围内.(适用类型: String,Collection,Map,数组)
	@Digits(integer,fraction)		//integer->指定整数部分的数字位数; fraction->指定小数部分的数字位数
	
	@Past / @Future					//被注释的元素必须是一个过去的日期(将来)    
	@Pattern(regex=,flag=)			//被注释的元素必须符合指定的正则表达式
		//regexp->正则表达式; flags->指定 Pattern.Flag 的数组,表示正则表达式的相关选项

	///hiberate validation 注解
	@NotEmpty   				//被注释的字符串的必须非空    
	@Email						//被注释的元素必须是电子邮箱地址    
	@Length(min=,max=)			//被注释的字符串的大小必须在指定的范围内    
	@Range(min=,max=,message=)	//被注释的元素必须在合适的范围内
	@URL(protocol=,host=,port=,regexp=,flags=)		//合法的url

	0.在bean属性上添加相应注解
		public class Car {
			@NotBlank(message = "用户名不能为空")
			@Length(min = 5, max = 20, message = "用户名长度必须在5-20之间")
			@Pattern(regexp = "^[a-zA-Z_]\\w{4,19}$", message = "用户名必须以字母下划线开头")
			public String brand;

			@Range(max = 1000000, min = 10000, message = "金额必须在0-1000000之间")
			@NumberFormat(pattern = "####.##")
			public Double price;

			@Past(message = "日期不能晚于当前时间")
			@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
			public Date birth;
		}

	1.后台控制器方法
		///注意: @Valid 和 BindingResult 两个入参必须紧挨着,中间不能插入其他入参
		@PostMapping("/car")
		public String car(@Valid Car args0, BindingResult result) {//必须有注解 @Valid 
			log.info(MessageFormat.format("{0} {1}", HELLO, args0));

			if (result.hasErrors()) { //校验有错
				result.getAllErrors().stream().forEach(x -> log.info(x.getDefaultMessage()));//错误信息
			}
			return HELLO;
		}

	2.将校验错误信息抽取到配置文件'ValidationMessages.properties'
		car.brand.notBlank=名字不能为空
	
		@NotBlank(message = "{car.brand.notBlank}") //对应属性的注解设置
		public String brand;

#拦截器
	///动态拦截action调用的对象. 
	提供一种机制: 使开发者可以在一个action执行的前后自定义代码; 也可以在一个action执行前阻止其执行

	0.多个拦截器的执行顺序
		//(1).第二个拦截器第一个方法返回true
		interceptor1: preHandle()
		interceptor2: preHandle() --->return true;
			//...调用目标方法
		interceptor2: postHandle()
		interceptor1: postHandle()
			//...渲染视图
		interceptor2: afterCompletion()
		interceptor1: afterCompletion()
		
		//(2).第二个拦截器第一个方法返回false
		interceptor1: preHandle()
		interceptor2: preHandle() --->return false;
			//...不再调用目标方法和渲染视图
		interceptor1: afterCompletion()

	1.自定义拦截器
		public class MyHandlerInterceptor implements HandlerInterceptor {

			//在目标方法之前被调用 ---> 适用于权限. 日志, 事务等.
			//返回true: 继续调用后续的拦截器和目标方法
			//返回false: 不会再调用后续的拦截器和目标方法
			@Override
			public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
					throws Exception {
				log.info(System.getProperty("line.separator"));//系统级别的换行符
				String url = request.getRequestURL().toString();
				log.info("url: " + url); //请求url

				for (Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
					for (String value : entry.getValue()) {
						log.info("params: " + entry.getKey() + " - " + value);//参数列表
					}
				}
				return true;
			}

			//调用目标方法之后,但渲染视图之前 ---> 可以对请求域中的属性或视图做出修改
			@Override
			public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
					ModelAndView modelAndView) throws Exception {
				HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
			}

			//渲染视图之后被调用 ---> 释放资源
			@Override
			public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
					throws Exception {
				HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
			}
		}

	2.注册拦截器
		@Configuration
		public class MyWebMvcConfigurer implements WebMvcConfigurer {

			//注册拦截器
			@Override
			public void addInterceptors(InterceptorRegistry registry) {
				WebMvcConfigurer.super.addInterceptors(registry);

				//静态资源: *.css; *.js --> SpringBoot已做好了静态资源映射
				registry.addInterceptor(new MyHandlerInterceptor())//
						.addPathPatterns("/**")//需要拦截 (/**表所有)
						.excludePathPatterns("/flower/**");//不拦截
			}
		}

#文件上传
	0.流程步骤
		(1).<form>表单: POST提交 + enctype="multipart/form-data"
		(2).文件上传域(<file>标签)必须要有name属性: <input type='file' name="file">
		(2).后台使用 MultipartFile 接收文件资源
	
	1.单文件上传
		<form action="/upload" method="post" enctype="multipart/form-data">
			File0: <input type="file" name="file"><br>
			//File1: <input type="file" name="file"><br> //多文件上传
			Desc: <input type="text" name="desc"><br>
			<input type="submit" value="提交">
		</form>
		
		@ResponseBody
		@RequestMapping("/upload")
		public String upload(@RequestParam("file") MultipartFile file, @RequestParam("desc") String desc) {
            //<form>表单中文件name, 文件名字, 文件大小
            System.out.println(file.getName() + " - " + file.getOriginalFilename() + " - " + file.getSize());
			file.transferTo(new File(getUploadDir(), file.getOriginalFilename())); //文件另存
		}
		
		public File getUploadDir() throws FileNotFoundException {
			File dir = new File(ResourceUtils.getURL("").getPath(), "/upload"); //项目根目录下
			if (!dir.exists()) {
				dir.mkdirs(); //创建当前及父目录.(区别于 mkdir())
			}
			return dir;
		}
	
	2.多文件上传
		@PostMapping("/uploads")
		public void batchUplocad(@RequestParam("file") List<MultipartFile> files,
								   @RequestParam("desc") List<String> descs) {
			files.forEach(x->x.transferTo(getUploadDir(),x.getOriginalFilename())); //文件另存 
			descs.forEach(log::info); //文件描述
		}
		
	3.配置上传文件的大小
		spring.servlet.multipart.max-file-size=10MB //单个文件
		spring.servlet.multipart.max-request-size=20MB //一次请求多个文件
		
		@Bean //或者,代码配置
		MultipartConfigElement multipartConfigElement() {
			MultipartConfigFactory factory = new MultipartConfigFactory();
			factory.setMaxFileSize("10MB");//单个文件
			factory.setMaxRequestSize("20MB");//一次请求多个文件
			return factory.createMultipartConfig();
		}
		
	4.
		
#文件下载
	response.setContentType("application/force-download"); //设置强制下载不打开
	response.addHeader("Content-Disposition", "attachment;fileName=" + fileName); //设置文件名
	
		
#异常处理
	0.处理顺序
		//当方法执行过程中出现异常,首先在本类中查找 @ExceptionHandler 标识的方法,
		//找不到, 再去查找 @ControllerAdvice 标识类中的 @ExceptionHandler 标识方法来处理异常.
	1.处理优先级
		//例如发生异常 NullPointerException; 但是声明的异常有 RuntimeException 和 Exception，
		//此时,根据异常的最近继承关系,找到继承深度最浅的那个, 即声明 RuntimeException 的方法
	
		@ControllerAdvice //标识为异常处理类
		public class MyHandlerExceptionResolver {
			/**
			 * 参数(可选):
			 * 		异常参数(包括自定义异常);
			 * 		请求或响应对象(HttpServletRequest; ServletRequest; PortleRequest/ActionRequest/RenderRequest) 
			 * 		Session对象(HttpSession; PortletSession) 
			 * 		WebRequest; NativeWebRequest; Locale; 
			 * 		InputStream/Reader; OutputStream/Writer; Model
			 * 
			 * 返回值(可选)：
			 * 		ModelAndView; Model; Map; View; String; @ResponseBody;
			 * 		HttpEntity<?>或ResponseEntity<?>; 以及void
			 */
			@ExceptionHandler(Exception.class) //ex对应发生的异常对象
			public ModelAndView handlerException(HttpServletRequest request, Exception ex) {
				
				//区分: URL & URI
				//http://ip:port/demo/hello/hello - /demo/hello/hello - /by zero - /by zero
				log.info("{} - {} - {}", request.getRequestURL(), request.getRequestURI(), ex.getMessage());

				ModelAndView mv = new ModelAndView("error");
				mv.addObject("errMsg", ex.getLocalizedMessage());
				return mv; //跳转异常页
			}
		}

#父子容器
	(1).Spring是父容器,SpringMVC是其中的一个子容器.	父容器不能访问子容器对象,但反之可以.
	(2).SSM框架中, @controller 注解的类对象扫描到 SpringMVC 容器中, 
		而 @service, @component, @Repository 注解扫描到 Spring 容器中.

	(3).通常情况下, Service, Dao, 数据源, 事务, 整合其他框架都是放在 Spring 的配置文件中.
	
		//将加载properties文件的配置 <context:property-placeholder location="classpath:*.properties"/>
		//写在 Spring 的 'applicationContext-*.xml' 中,可以正常加载到属性; 
		//但写在 'springmvc.xml' 中却加载不到.
		<context />写在子容器,而service配置在父容器,父无法访问子中对象,所以加载不到properties文件中的属性.
		另外, controller要配置在springmvc.xml(子容器)中,否则客户端请求时会找不到对应的controller而出错


#国际化
	0.两种方式
		(1).页面能够根据'浏览器的语言设置'情况对文本(不是内容),时间,数值进行本地化处理
		(2).页面可以通过超链接切换 Locale, 而不再依赖于'浏览器的语言设置'情况
		
	1.资源文件 '基名_语言代码_国家代码'
		目录'/resources/i18n/'新建'properties'文件, login; login_zh_CN; login_en_US
		分别对应: ①是默认配置; ②是中文环境; ③是英文环境
		分别编辑: login.user=用户名; login.user=用户名; login.user=user
		
	2.配置basename
		<bean id="messageSource" //xml实现
			class="org.springframework.context.support.ResourceBundleMessageSource">
			<property name="basename" value="i18n.login"></property> //注意value值
		</bean>
	
		//boot默认已配置 ResourceBundleMessageSource 
		spring.messages.basename=i18n.login //(默认basename: messages)
	
	3.前台页面
		<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> //jsp ==> fmt标签
		<fmt:bundle basename="i18n.login"> //可省,由basename指定
			<fmt:message key="login.user"/>
		</fmt:bundle>
		
		<span th:text="#{login.user}"/> //thymeleaf ==> #{}标签
		
	4.超链接切换
		//到此,已经可以实现方式(1), 对于方式(2),还需以下两步配置:
		//注意: 实现方式(2),则方式(1)不起作用.
		
		//1.页面配置超链接
		<a href="<%=request.getContextPath()%>/?l=zh_CN">中文</a>&nbsp;&nbsp;
		<a href="<%=request.getContextPath()%>/?l=en_US">英文</a>&nbsp;&nbsp;
		<span th:text="#{login.user}"/>
		
		//2.后台注册自定义国际化配置
		@Configuration
		public class MyWebMvcConfigurer implements WebMvcConfigurer {
			@Bean
			public LocaleResolver localeResolver() { //注册自定义国际化配置
				return new LocaleResolver() {
					@Override
					public Locale resolveLocale(HttpServletRequest request) {
						Locale locale = Locale.getDefault();//默认
						String parameter = request.getParameter("l");//获取自定义
						if (!StringUtils.isEmpty(parameter)) {
							String[] split = parameter.split("_");//zh_CN
							locale = new Locale(split[0], split[1]);
						}
						return locale;

					@Override
					public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {}
					}
				};
			}
		}
	
	5.底层原理
		//国际化(CHINA,US): 时间-数值-货币
        Locale locale = Locale.US;
        double NUMBER = 12345.67;

        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM, locale);
        String now = dateFormat.format(new Date()); //2018年12月14日 11:03:30; December 14, 2018 11:13:24 AM

        NumberFormat numberInstance = NumberFormat.getNumberInstance(locale);
        String number = numberInstance.format(NUMBER); //12,345.67; 12,345.67

        NumberFormat currencyInstance = NumberFormat.getCurrencyInstance(locale);
        String currency = currencyInstance.format(NUMBER); //￥12,345.67; $12,345.67

        ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n.login", locale);
        resourceBundle.getString("login.user"); //获取指定国际化资源文件的参数值












































































