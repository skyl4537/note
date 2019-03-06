

//{--------<<<@注解>>>--------------------------------------------------------------------
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
	
//}		
	
//{--------<<<@Request*>>>----------------------------------------------------------------
#@RequestMapping ///可标注在(类/方法)上
	#value		-> 请求地址,默认属性. //@RequestMapping(value = "/hello") == @RequestMapping("/hello")
	#name		-> 给这个mapping分配一个名称,类似于注释
	#method		-> 请求方式. //RequestMethod.GET; .POST; .PUT; .DELETE; ...
	#params		-> 请求参数必须满足条件,才能进行处理.
	#headers	-> 同上,不常用.
	
		//参数必须包含'name'; 如有'age',则不能等于10
		@GetMapping(value = "/hello", params = { "name", "age!=10" })
		public String hello() { }
			
		http://127.0.0.1:8090/demo/hello?name=5			(T)
		http://127.0.0.1:8090/demo/hello?name=5&age=11	(T)
		http://127.0.0.1:8090/demo/hello?name=5&age=10	(F)
		http://127.0.0.1:8090/demo/hello?names=5&age=11	(F)
		
#@PathVariable ///将url中的占位符映射到方法的入参

		@GetMapping("/hello/{name}") //两个'name'必须一致,入参名'args'无所谓
		public String hello(@PathVariable("name") String args) { 
			return MessageFormat.format("{0} {1}", HELLO, args); 
		}		
		http://127.0.0.1:8090/demo/hello/spring ---> hello spring
	
#@RequestParam ///将{请求参数}映射到方法的入参
	#value: 参数名,必须和url中的参数名保持一致
	#required: 是否为必须,默认true 
	#defaultValue: 参数的默认值,当url中没有该参数时取默认值
	
		@GetMapping("/hello")
		public String hello(@RequestParam(value = "name", required = true) String arg0,
							@RequestParam(value = "age", required = false, defaultValue = "18") Integer arg1) {
			return MessageFormat.format("{0} {1} {2}", HELLO, arg0, arg1);
		}
		
		http://127.0.0.1:8090/demo/hello?name=wang&sex=99 ---> hello wang 18
		
#@RequestHeader ///Header属性.(属性同上)
#@CookieValue///Cookie属性.(属性同上)
	
		@GetMapping("/hello")
		public String hello(@RequestHeader("Connection") String arg0) {	}

		@GetMapping("/hello")
		public String hello(@CookieValue("JSESSIONID") String arg0) { }
		
#请求参数自动填充到pojo ///支持级联填充

		@GetMapping("/hello") //@PostMapping("/hello")
		public String hello(Car arg0) { }
		
		http://127.0.0.1:8090/demo/hello?brand=Audi&price=720000&address.province=shanxi&address.city=yuncheng
		//Car(brand=Audi, price=720000.0, address=Address(province=shanxi, city=yuncheng))
		
#Servlet原生API作为入参
	#HttpServletRequest; HttpServletResponse; HttpSession; InputStream; OutputStream;
	#java.security.Principal; Locale; Reader; Writer
	
		@GetMapping("/hello")
		public void hello(HttpServletRequest request) {
			String method = request.getMethod(); //GET
			String ip = request.getRemoteAddr(); //192.168.8.7
			String url = request.getRequestURL().toString(); //http://192.168.8.7:8090/demo/hello
		}
		
#@ResponseBody
	#方法的返回结果直接写入 HTTP 响应正文中.
	#返回非页面, 而是其他数据格式时使用(如json,xml等). 一般用于异步获取数据.
	
#@RequestBody
	#获取请求体的数据,可转换成javaBean对象,也可以转换成Map类型.
	#默认使用 HttpMessageConverter 解析,然后把相应数据绑定到 controller 方法的参数上
	#multipart/form-data: //不能处理
	#application/x-www-form-urlencoded: //PUT时必须; GET,POST时可选(@RequestParam, @ModelAttribute 也可以)
	#其他(application/json, application/xml等): //必须
		
	1.前台传递JSON
        function login(e) {
            $.ajax({
                url: $(e).attr('url'),
                type: 'POST',
                contentType: "application/json; charset=utf-8", //解析时,必须使用 @RequestBody
                data: JSON.stringify({'name': $('#name').val(), 'pwd': $('#pwd').val()}), //JSON对象 -> JSON字符串
                success: function (data) {
                    alert(JSON.parse(data).name); //JSON字符串 -> JSON对象
                }
            });
        }
		
		//后台解析 -> @RequestBody
		@PostMapping("/login")
		@ResponseBody //***
		public String login(@RequestBody User user) {
			return JSON.toJSONString(user);
		}
		
	2.前台传递String
        function login(e) {
            $.ajax({
                url: $(e).attr('url'),
                type: 'POST',
                contentType: "application/x-www-form-urlencoded; charset=utf-8", //默认,可省
                data: {'name': $('#name').val(), 'pwd': $('#pwd').val()}, //字符串
                success: function (data) {
                    alert(data);
                }
            });
        }
		
		//后台解析 -> @RequestParam
		@PostMapping("/login")
		@ResponseBody
		public String login(@RequestParam String name, @RequestParam String pwd) {
			return name + " - " + pwd;
		}
		
#转发和重定向
	#一般情况下,控制器方法返回字符串都会被当成逻辑视图名处理.
	#但如果返回的字符串中带 'forward:' 或 'redirect:' 前缀时,SpringMVC 会特殊处理.
	
//}

//{--------<<<模型数据>>>-----------------------------------------------------------------
#Map; Model; ModelMap; ModelAndView
	#ModelAndView: 既包含'视图',也包含'模型数据'

		@GetMapping("/hello")
		public ModelAndView hello(ModelAndView mv) {
			mv.setViewName("hello"); //视图
			mv.addObject("data", SystemUtils.getAll()); //模型数据
			return mv;
		}
		
		//等同于,更常用
		@GetMapping("/hello")
		public String hello(Model model) {
			model.addAttribute("data", SystemUtils.getAll()); //模型数据
			return "/hello"; //视图
		}

#SessionAttributes
	#若希望在多个请求之间共用某个模型属性数据,则可以在控制器类上标注一个 @SessionAttributes
	#SpringMVC 将模型中对应的属性暂存到 HttpSession 中.
	
		@RequestMapping("/testSessionAttributes")
		public String testSessionAttributes(Map<String, Object> map){
			User user = new User("Tom", "123456", "tom@x.com", 15);
			map.put("user1", user);
			map.put("school", "x");
			return SUCCESS;
		}
	
	#除了可以通过属性名指定需要放到会话中的属性(实际上使用的是 value 属性值),
	#还可以通过模型属性的对象类型指定哪些模型属性需要放到会话中(实际上使用的是 types 属性值)
	
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

#ModelAttribute 
	#每个控制器方法执行之前,都会先执行一次此注解标注的方法
	执行流程:
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

#SpringMVC确定目标方法入参POJO类型的过程
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
	#控制器方法执行完成后,最终返回一个对象 ModelAndView. (包含逻辑名和模型数据的视图)
	#对于返回 String, ModelMap, SpringMVC 也会将其封装成 ModelAndView 对象.
	
		SpringMVC 借助视图解析器(ViewResolver)得到最终的视图对象(View),最终可以是 JSP, Excel 等各种表现形式的视图.
		为了实现视图模型和具体实现技术的解耦,Spring 在 org.springframework.web.servlet 包中定义了一个高度抽象的 View 接口:

		'视图对象'由'视图解析器'负责实例化. 由于视图是无状态的,所以不会有线程安全的问题.

	#视图解析器
		SpringMVC 为逻辑视图名的解析提供了不同的策略,可以在 SpringWeb 上下文中'配置一种或多种解析策略'
		并指定他们之间的先后顺序. 每一种映射策略对应一个具体的视图解析器实现类.

#设置默认页面の两种方案
		http://ip:port/blue/ 或 http://ip:port/blue/index.html --> 都会访问 /templates/index.html
		
		@GetMapping(value = { "/", "/index.html" }) //(1).添加映射路径
		public String index() {
			return "index";
		}
		
		@Configuration //(2).设置视图映射
		public class MyWebMvcConfigurer implements WebMvcConfigurer {
			@Override
			public void addViewControllers(ViewControllerRegistry registry) {
				registry.addViewController("/").setViewName("index");
				registry.addViewController("/index.html").setViewName("index");
			}
		}	
	
//}

//{--------<<<数据校验>>>-----------------------------------------------------------------
#JSR303 是Java为Bean数据合法性校验提供的标准框架,它已经包含在 JavaEE 6.0 中.
#Bean-Validation(即JSR303) 内置的 constraint
    /** 
     * @Null				必须为 null
     * @NotNull				必须不为 null
     * @AssertTrue			必须为 true
     * @AssertFalse			必须为 false
	 *
     * @Min(value)			必须是数字,其值必须 >= 指定的最小值
     * @Max(value)			必须是数字,其值必须 <= 指定的最大值
     * @DecimalMin(value)	必须是数字,其值必须 >= 指定的最小值
     * @DecimalMax(value)	必须是数字,其值必须 <= 指定的最大值
     * @Size(max=, min=)	大小必须在指定的范围内
	 *
     * @Digits(integer, fraction)	必须是数字,其值必须在可接受的范围内.
	 *								(integer->指定整数部分的数字位数; fraction->指定小数部分的数字位数)
	 *
     * @Past					必须是一个过去的日期
     * @Future					必须是一个将来的日期
	 *
     * @Pattern(regex=,flag=)	必须符合指定的正则表达式
	 * 							regex->正则表达式; flag->指定 Pattern.Flag 的数组,表示正则表达式的相关选项
	 */

#Hibernate-Validator 附加的 constraint
	/**
     * @NotBlank(message=)	字符串非null,非空. (去掉首尾空格)
     * @NotEmpty			字符串必须非空. (不会去掉...)
     * @Length(min=,max=)	字符串长度必须在指定的范围内
     * @Range(min=,max=,message=)	数值必须在合适的范围内 
	 *
     * @Email				必须是电子邮箱地址
	 * @URL(protocol=,host=,port=,regexp=,flags=)	合法的url
     */
	
#校验DEMO
	0.在实体类中添加校验规则
		public class Car {
			@NotBlank(message = "车标不能为空")
			@Length(min = 3, max = 10, message = "车标长度必须在3-10之间")
			@Pattern(regexp = "^[a-zA-Z_]\\w{2,9}$", message = "车标必须以字母下划线开头")
			public String brand;

			@Range(max = 1000000, min = 0, message = "价格必须在0-1000000之间")
			@NumberFormat(pattern = "####.##")
			public Double price;

			@Past(message = "日期不能晚于当前时间")
			@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
			public Date birth;
		}
		
	1.也可将校验信息抽取到配置文件
		//在'resources'目录下新建配置文件 ValidationMessages.properties
		car.brand.notBlank=车标不能为空

		@NotBlank(message = "{car.brand.notBlank}") //对应属性的注解设置
		public String brand;

	2.控制器中开启校验
		///注意: @Valid 和 BindingResult 两个入参必须紧挨着,中间不能插入其他入参
		@PostMapping("/car")
		@ResponseBody //@Valid: 开启校验; BindingResult: 封装了校验的结果
		public String car(@Valid @RequestParam Car car, BindingResult result) {
			log.info(MessageFormat.format("{0} {1}", "hello", car));

			if (result.hasErrors()) { //校验有错
				result.getAllErrors().forEach(x -> log.info(x.getDefaultMessage()));//错误信息
				return "car";
			}
			return "success";
		}
		
	3.前台页面回显校验错误 
		//使用 thymeleaf 的内置标签 th:errors
		//第一次跳转该页面时,没有局部变量 car,所以应该判断: th:if="${null!=car}"		
		<form th:action="@{/save}" method="post">
			车标：<input type="text" name="brand"/> 
				  <font color="red" th:if="${null!=car}" th:errors="${car.name}"></font><br/>
			<input type="submit" value="添加"/>
		</form>
		
#常见异常
	#java.lang.IllegalStateException: Neither BindingResult nor plain target object for bean name 'car' available as request attribute
	#这是因为第一次跳转'新增'页面时,没有局部变量 car,导致解析异常. 两种解决方案:
	
	1.判断是否存在局部变量
		车标：<input type="text" name="brand"/> 
			  <font color="red" th:if="${null!=car}" th:errors="${car.name}"></font><br/>
		
	2.跳转页面的方法中注入一个对象 //默认名称: 类名第一个字母小写
		车标：<input type="text" name="brand"/> 
			  <font color="red" th:errors="${car.name}"></font><br/>
		
		@RequestMapping("/addUser")
		public String showPage(Users users){
			return "add";
		}
		
	2-1.如何更改默认名称???
		车标：<input type="text" name="brand"/> 
			  <font color="red" th:errors="${aaa.name}"></font><br/>
		
		//如果想为传递的对象更改名称,可使用 @ModelAttribute("aaa"), 表示当前传递的对象的key为 aaa
		@RequestMapping("/addUser")
		public String showPage(@ModelAttribute("aaa") Users users){
			return "add";
		}
		
		@RequestMapping("/save")
		public String saveUser(@ModelAttribute("aaa") @Valid Users users,BindingResult result){
			if(result.hasErrors()){
				return "add";
			}
			return "ok";
		}
		
//}

//{--------<<<拦截器>>>-------------------------------------------------------------------
#动态拦截Action调用的对象
	#提供一种机制: 使开发者可以在一个action执行的前后自定义代码; 也可以在一个action执行前阻止其执行

#多个拦截器的执行顺序
	1.第二个拦截器第一个方法返回true
		interceptor1: preHandle()
		interceptor2: preHandle() --->return true;
			//...调用目标方法
		interceptor2: postHandle()
		interceptor1: postHandle()
			//...渲染视图
		interceptor2: afterCompletion()
		interceptor1: afterCompletion()
		
	2.第二个拦截器第一个方法返回false
		interceptor1: preHandle()
		interceptor2: preHandle() --->return false;
			//...不再调用目标方法和渲染视图
		interceptor1: afterCompletion()

#登录验证拦截器
	1.自定义拦截器
		public class LoginInterceptor implements HandlerInterceptor {

			//在目标方法之前被调用 ---> 适用于权限. 日志, 事务等.
			@Override
			public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
				if (null != request.getSession().getAttribute("user")) {
					return true; //有Session -> true, 则继续调用后续的拦截器和目标方法; 没有,则转发到登录页
				} else {
					request.getRequestDispatcher("/").forward(request, response);
					return false;
				}
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

			@Override
			public void addInterceptors(InterceptorRegistry registry) {
				registry.addInterceptor(new LoginInterceptor())//
						.addPathPatterns("/**") //需要拦截 (/**表所有)
						.excludePathPatterns("/flower/**"); 
			}
		}

//}

//{--------<<<上传下载>>>-----------------------------------------------------------------
#文件上传
	0.流程步骤
		(1).<form>表单: POST + enctype="multipart/form-data"
		(2).文件上传域(<file>)必须要有name属性: <input type='file' name="file">
		(2).后台使用 MultipartFile 接收文件资源
	
	1.单文件上传
		<form action="/upload" method="post" enctype="multipart/form-data">
			File0: <input type="file" name="file"><br>
			//File1: <input type="file" name="file"><br> //多文件上传
			Desc: <input type="text" name="desc"><br>
			<input type="submit" value="提交">
		</form>
		
		@ResponseBody
		@PostMapping("/upload")
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
			files.forEach(x -> x.transferTo(getUploadDir(), x.getOriginalFilename())); //文件另存 
			descs.forEach(log::info); //文件描述
		}
		
	3.配置上传文件的大小
		spring.servlet.multipart.max-file-size=10MB //单个上传文件的大小
		spring.servlet.multipart.max-request-size=20MB //一次请求上传文件的总容量
		
		@Bean //或者,代码配置
		MultipartConfigElement multipartConfigElement() {
			MultipartConfigFactory factory = new MultipartConfigFactory();
			factory.setMaxFileSize("10MB");//单个文件
			factory.setMaxRequestSize("20MB");//一次请求多个文件
			return factory.createMultipartConfig();
		}
		
	4.
		
#文件下载 -> 一般都借助于以下两个 header 达到效果:
	1.Content—Type
		告知浏览器当前的响应体是什么类型的数据.
		当为 application/octet-stream 时,就说明 body 里是一堆不知道是啥的二进制数据.

	2.Content—Disposition
		用于向浏览器提供一些关于如何处理响应内容的额外的信息,同时也可以附带一些其它数据,
		比如,在保存响应体到本地的时候应该使用什么样的文件名.
		
		常用取值:
			(1).inline		-> 建议浏览器使用默认的行为处理响应体
			(2).attachment	-> ..........将响应体保存到本地,而不是正常处理响应体
			
		返回文件名的两种形式:
			(1).filename=yourfilename.suffix			-> 直接指明文件名和后缀
			(2).filename*=utf-8''yourfilename.suffix	-> 指定文件名及编码方式
					其中,编码后面的那对单引号中还可以填入内容,此处不赘述,可参考规范 https://tools.ietf.org/html/rfc6266
					
			(*).以上标识有些浏览器不认识,估计是太复杂, 所以最好再带上 filename=yourfilename.suffix 。
		
		//response.addHeader("Content-Type", "application/force-download"); //不推荐使用
		response.addHeader("Content-Type", "application/octet-stream");
		response.addHeader("Content-Disposition", "attachment; filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + "\".xlsx;filename*=UTF-8''" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1"));
	
//}

//{--------<<<父子容器>>>-----------------------------------------------------------------
#父容器Spring & 子容器SpringMVC
	(1).Spring		-> @service, @component, @Repository, @mapper
	(2).SpringMVC	-> @controller
	
	(0).父容器不能访问子容器对象,但反之可以.
	
	(3).Controller 要配置在子容器(springmvc.xml)中,否则客户端请求时会找不到对应的 Controller 而出错	
	(4).通常, 整合其他框架(Service,Dao,事务等)的配置都放在父容器中,即 applicationContext.xml
		//读取配置文件
		<context:property-placeholder location="classpath:*.properties"/>

//}

//{--------<<<国际化>>>-------------------------------------------------------------------
#两种方式
	(1).页面根据'浏览器的语言设置'对文本(不是内容),时间,数值进行本地化处理
	(2).页面可以通过'超链接'切换, 而不再依赖于浏览器的语言设置
	
#资源文件 '基名_语言代码_国家代码'
	目录'/resources/i18n/'新建'properties'文件, login; login_zh_CN; login_en_US
	分别对应: ①默认配置; ②中文环境; ③英文环境
	分别编辑: login.user=用户名; login.user=用户名; login.user=user
	
#配置baseName
	<bean id="messageSource" //xml实现
		class="org.springframework.context.support.ResourceBundleMessageSource">
		<property name="basename" value="i18n.login"></property> //value值: /i18n/login
	</bean>

	//boot默认已配置 ResourceBundleMessageSource 
	spring.messages.basename=i18n.login //(默认basename: messages)

#前台页面
	<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> //jsp ==> fmt标签
	<fmt:bundle basename="i18n.login"> //可省,由basename指定
		<fmt:message key="login.user"/>
	</fmt:bundle>
	
	<span th:text="#{login.user}"/> //thymeleaf ==> #{}标签
	
#超链接切换
	#到此,已经可以实现方式(1), 对于方式(2),还需以下两步配置:
	#注意: 实现方式(2),则方式(1)不起作用.
	
	1.页面配置超链接
		<a href="<%=request.getContextPath()%>/?l=zh_CN">中文</a>
		<a href="<%=request.getContextPath()%>/?l=en_US">英文</a>
		<span th:text="#{login.user}"/>
	
	2.后台注册自定义国际化配置
		@Configuration
		public class MyWebMvcConfigurer implements WebMvcConfigurer {
			
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
		}

#底层原理
	//国际化(CHINA,US): 时间-数值-货币
	Locale LOCALE = Locale.US;
	double NUMBER = 12345.67;

	DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM, LOCALE);
	String now = dateFormat.format(new Date()); //2018年12月14日 11:03:30; December 14, 2018 11:13:24 AM

	NumberFormat numberInstance = NumberFormat.getNumberInstance(LOCALE);
	String number = numberInstance.format(NUMBER); //12,345.67; 12,345.67

	NumberFormat currencyInstance = NumberFormat.getCurrencyInstance(LOCALE);
	String currency = currencyInstance.format(NUMBER); //￥12,345.67; $12,345.67

	ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n.login", LOCALE);
	resourceBundle.getString("login.user"); //获取指定国际化资源文件的参数值

//}

//{--------<<<页面跳转>>>---------------------------------------------------------------------
#页面跳转 -> 没有任何业务逻辑,只是单纯的路由过程(点击按钮跳转到一个页面)
	1.配置Controller
		@RequestMapping("/toView")
		public String view(){
			return "view";
		}
		
	2.实现接口
		@Configuration
		public class MyWebMvcConfigurer implements WebMvcConfigurer {
			@Override
			public void addViewControllers(ViewControllerRegistry registry) {
				//url访问: ip:port/demo/toView ---> 对应资源: /templates/view.html
				registry.addViewController("/toView").setViewName("view");
			}
		}
		
	3.配置xml
		//(1).使用此标签后必须配置 <mvc:annotation-driven />,否则会造成所有的 @Controller 注解无法解析,导致404错误
		//(2).如果请求存在处理器,则这个标签对应的请求处理将不起作用. 因为请求是先去找处理器处理,如果找不到才会去找这个标签配置
		<mvc:view-controller path="/toView" view-name="view"/>
		<mvc:annotation-driven />
		
	4.boot项目
		存放在 /resources/static/ 目录下的资源,可直接通过浏览器访问,勿需映射.

//}


