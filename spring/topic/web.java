
///JavaWeb的技术体系图--->附件

//{--------<<<框架>>>--------------------------------------------------------------------
#封装-代码重用
	框架: 可重用的半成品.
	
	基础语法 --> 方法 --> 类 --> jar --> 框架 //每一层的递进,都是代码重用的结果

//}

//{--------<<<路径>>>--------------------------------------------------------------------
#路径前缀 /
	1.由服务器解析 -> web应用的根目录 -> http:127.0.0.1:8090/demo/
		Serlvet映射的访问路径 //@RequestMapping("/hello")
		请求转发时 //req.getRequestDispatcher("/hello").forward(req, res);
	
	2.由浏览器解析 -> web站点的根目录 -> http:127.0.0.1:8090/
		超链接 //<a href="/hello.html">测试</a>
		表单中的action //<form method="post" action="/hello">
		请求重定向 //req.sendRedirect("/hello");
//}

//{--------<<<Servlet>>>-----------------------------------------------------------------
#Tomcat的work目录存放
	(1).jsp先翻译成Servlet,再编译的class文件.
	(2).Session被持久化的 SESSIONS.ser 文件.
	
#开发模式回顾
	#MODEL-1 --> 在 jsp 内嵌 javaBean 代码
		优势: 执行效率高; 开发效率高; 适合小项目
		劣势: 逻辑耦合,代码混乱; 维护成本高,不利于扩展
		
	#MODEL-2 --> MVC模式: 模型-Model,视图-View,控制器-Controller
		优势: 业务和显示分离; 结构清晰,分工明确,便于维护; 适合中大型项目
		劣势: 执行效率相对model1低; 代码量大; 重复代码比较多
	
#Servlet作用
	将url映射到一个java类的处理方法上.
	接收请求的数据, 并与其他服务器资源进行通信(如数据库,基于java的应用程序).
	如何将处理结果展示到页面.
	如何进行页面跳转.

	编译存储在目录"web应用/WEB-INF/classes/*".
	
#Servlet容器
	Servlet本质是一个java接口类,部署运行在Servlet容器中.
	Servlet容器管理Servlet的整个生命周期, 并负责调用Servlet方法响应客户端请求.
	Servlet和客户端的通信采用"请求/响应"模式.
	
#Servlet生命周期方法(由容器调用) ---> 非线程安全!!
	构造器: //只在第一次请求时调用,创建<单实例>的Servlet对象.
	init(): //只被调用一次(调用构造器方法后立即被调用). 用于初始化当前Servlet
	service(): //每次请求都会调用. 用于响应客户端请求.
	destory(): //只被调用一次. 应用被卸载前调用. 用于释放Servlet所占用的资源(如数据库连接).

#实现Servlet接口
	1.web.xml中配置和映射实现类
		<servlet>
			<servlet-name>helloServlet</servlet-name> //类别名
			<servlet-class>com.x.javaweb.HelloServlet</servlet-class>
		</servlet>
		
		<servlet-mapping>
			<servlet-name>helloServlet</servlet-name>
			<url-pattern>/hello</url-pattern> //访问路径. 其中,'/'代表当前web应用的根目录
			<load-on-startup>1</load-on-startup> //指定 Servlet 被创建的时机
		</servlet-mapping>

	2.参数说明
		load-on-startup: 负数 --> 在第一次请求时被创建; 
						 正数或0 --> 在当前应用被Serlvet容器加载时创建实例, 且数值越小越早被创建.

		同一个Servlet可以被映射到多个URL上. 即一个<Servlet>可以对应多个<servlet-mapping>.

		Servlet映射URL中可以使用通配符"*",但只能两种固定格式: '/*' 或 '*.html(do,action等)'
		其他,如 '/*.html' 都是不合法的.

#init()方法
	0.ServletConfig --> /**封装了 Serlvet 的配置信息,并且可以获取 ServletContext 对象*/	
		//配置Serlvet的初始化参数
		<servlet>
			<servlet-name>helloServlet</servlet-name>
			<servlet-class>com.x.javaweb.HelloServlet</servlet-class>
			<init-param> //配置 Serlvet 的初始化参数. 且节点必须在<load-on-startup>之前
				<param-name>user</param-name>
				<param-value>123</param-value>
			</init-param>
			<load-on-startup>-1</load-on-startup>
		</servlet>

		//获取Servlet的初始化参数
		public void init(ServletConfig config) throws ServletException {
			String user = config.getInitParameter("user"); //获取单个
			System.out.println("user: " + user);

			Enumeration<String> names = config.getInitParameterNames(); //获取所有
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				String value = config.getInitParameter(name);
				System.out.println("获取Servlet的初始化参数 -> " + name + ":" + value);
			}
		}
		
	1.ServletContext --> /**代表当前WEB应用,可以从中获取到应用的各个方面信息*/	
		//配置web应用的初始化参数
		<context-param>
			<param-name>driver</param-name>
			<param-value>com.mysql.jdbc.Driver</param-value>
		</context-param>
		
		//获取web应用的初始化参数
		public void init(ServletConfig config) throws ServletException {
			ServletContext context = config.getServletContext(); //由 ServletConfig 获取
			String driver = context.getInitParameter("driver"); //获取单个
			System.out.println("driver: " + driver);

			Enumeration<String> names = context.getInitParameterNames(); //获取所有
			while (names.hasMoreElements()) {
				String name = names.nextElement();
				String value = context.getInitParameter(name);
				System.out.println("获取web应用的初始化参数 -> " + name + ":" + value);
			}
		}
		
	2.ServletContext常用方法
        context.getRealPath("abc.log"); //获取某个文件在服务器上的绝对路径,而非部署前路径
        context.getContextPath(); //获取当前应用的名称 server.servlet.context-path=/demo
		
        InputStream in = context.getResourceAsStream("my.properties"); //获取某个文件对应的输入流
		//InputStream in = getClass().getClassLoader().getResourceAsStream("my.properties"); //效果同上
        Properties properties = new Properties();
        properties.load(in);
        String url = properties.getProperty("url");
		
		//属性相关的三个方法
        context.setAttribute("user", "123"); //设置
        String user = (String) context.getAttribute("user"); //获取
        context.removeAttribute("user"); //移除
		
#service()方法
	0.http连接
		建立连接 -> 发送请求信息 -> 回送响应信息 -> 关闭连接.
		每次访问一个页面,浏览器和服务器都要单独建立一次连接.
		每次连接只处理一个请求和响应. 每次请求都会调用一次方法 service().
		
	1.ServletRequest --> /**封装了请求信息.可以从中获取到任何的请求信息*/
		public void service(ServletRequest req, ServletResponse res) {
			Enumeration<String> names = req.getParameterNames();
			while (names.hasMoreElements()) {
				String name = names.nextElement(); //请求参数key

				String value = req.getParameter(name); //若请求参数有多个值(如 checkbox),该方法只能获取到第一个value.
				String[] values = req.getParameterValues(name); //这个方法可以获取到请求参数的所有values.
			}

			Map<String, String[]> map = req.getParameterMap(); //请求参数的key-value
			map.forEach((name, values) -> System.out.println(name + ":" + Arrays.toString(values)));

			//强转(撕破伪装) -> http*封装更详细的请求信息
			HttpServletRequest request = (HttpServletRequest) req; 
			String method = request.getMethod(); //获取请求方式: GET或POST...等
			String queryString = request.getQueryString(); //GET请求 ?后面内容
			String uri = request.getRequestURI(); //URI -> /demo/hello/listener
			StringBuffer url = request.getRequestURL(); //URL -> http://127.0.0.1:8090/demo/hello/listener
		}
	
	3.ServletResponse --> /**封装了响应信息,如果想给用户什么响应,具体可以使用该接口的方法实现*/
		public void service(ServletRequest req, ServletResponse res) throws IOException {
			PrintWriter writer = res.getWriter();
			writer.print("HELLO WORLD!"); //响应内容打印到浏览器
			res.setContentType("application/msword"); //设置响应的内容类型: word文档

			HttpServletResponse resp = (HttpServletResponse) res; //强转
			resp.sendRedirect("/list"); //请求重定向
		}
		
#HttpServlet
	能够处理HTTP请求的Servlet. 添加了一些与HTTP协议相关方法, 
	HttpServlet覆写 service() 方法时,根据请求方式不同,将请求分别分发到 doGet() doPost() doPut() doDelete().
	//所以,实际开发中继承 HttpServlet, 并覆写相应的方法 doXXX().
	
	HttpServlet extends GenericServlet implements Servlet, ServletConfig, Serializable //与Servlet的继承关系

//}

//{--------<<<jsp>>>---------------------------------------------------------------------
#jsp页面(Java_Server_Pages)
	本质就是一个Servlet; jsp善于处理页面显示; Servlet善于处理业务逻辑, 二者结合使用.
	执行时,先转化为java文件,再编译成class文件.
	转化过程: java代码照搬; html+css+表达式等通过流输出 out.write() 
		
	0.9大内置对象
		HttpServletRequest request; //同一个请求
		JspWriter out = response.getWriter(); //用于页面显示信息, out.println();
		
		HttpSession session;
		ServletContext application;
		PageContext pageContext; //当前页面的上下文.(可从中获取到其余 8 个隐含对象)
	
		Throwable exception //<%@ page isErrorPage="true" %>,才可以使用(X)
		
		HttpServletResponse response; //几乎不用(X)
		ServletConfig config; //Servlet.ServletConfig.(X) 
		Object page = this; //jsp对象本身,或编译后的Servlet对象; 实际上就是this.(X)
		
		//pageContext < request < session < application (作用域:从小到大)
		
	1.jsp中的java
		<% 
			Date date = new Date(); //嵌入jsp的Java代码段
			out.print(date);
		%>

		<%= date %> //脚本表达式.(上述的简化版)
		
#jsp指令
	并不直接产生任何可见输出,而只是告诉jsp引擎如何解析jsp页面的其余部分.
	
	1.page指令 //用于定义jsp页面的各种属性
		import: 当前jsp对应的Servlet需要导入的类. //<%@ page import="java.text.DateFormat" %>
		session: 访问当前页面是否生成HttpSession对象. //<%@ page session="false" %>
		isELIgnored: 当前页面是否可以使用 EL 表达式, 默认 false.
		
		isErrorPage: 是否为错误处理页面, 即是否可用 exception 隐藏变量. //<%@ page isErrorPage="true" %>
		errorPage: 若当前页面出现错误, 实际响应什么页面. //<%@ page errorPage="/error.jsp" %> 根目录下的error.jsp
	
		//web.xml中配置错误页面(另一种实现方式)
		<error-page>
			<error-code>404</error-code> 
			<location>/WEB-INF/error.jsp</location> //404响应的页面
		</error-page>
		  
		<error-page>
			<exception-type>java.lang.ArithmeticException</exception-type>
			<location>/WEB-INF/error.jsp</location> //具体某一个异常 对应的错误页面
		</error-page>
		
	2.include指令 
		通知jsp引擎在翻译当前页面时,将指定文件中的内容 合并进当前页面转换成的 Servlet 源文件中.
		这种在源文件级别进行引入的方式称之为静态引入. //<%@ include file="b.jsp" %>

#el表达式 -> ${标识符}	
	用于替换jsp中的脚本表达式(<%= %>),此处从'四大域对象'中检索java对象,获取数据等.
	如果有则输出数据,如果为null则什么也不输出.
	// pageScope < requestScope < sessionScope < applicationScope;
	
		//1.bean属性    2.list(index)       3.map<key>  4.map<特殊key>  5.session取特殊key
		${person.age}	${list[1].name}		${map.c}	${map["c.d"]}	${sessionScope["c.d"].name}

#jstl(JSP标准标签库) //JSP标签集合,封装了JSP应用的通用核心功能.	
	1.导入标签库jar包
		<dependency>
			<groupId>jstl</groupId>
			<artifactId>jstl</artifactId>
			<version>1.2</version>
		</dependency>
	
	2.在jsp页面引入标签库
		<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%> //声明

		//test: 判断条件; var: 用于存储条件结果的变量; scope: var属性的作用域;
		<c:if test="${salary > 20000}" var="flag" scope="session">
		   <p>我的工资为: <c:out value="${salary}"/><p>
		</c:if>
		<c:if test="${not flag}"> //等同于: ${!flag}
		   <p>我的工钱为: <c:out value="${salary}"/><p>
		</c:if>

		<c:if test="${!empty emps}">
			<table border="1px" width="70%" align="center" cellspacing="0px">
				//items: 要迭代的集合; var: 当前迭代出的元素
				<c:forEach items="${emps}" var="emp"> 
					<tr align="center">
						<td>${emp.id }</td>
						<td>${emp.lastName }</td>
						<td>${emp.gender==0?"女":"男" }</td>
						<td>${emp.dept.deptName }</td>
						<td>
							<a href="#">修改</a> &nbsp;&nbsp; <a href="#">删除</a>
						</td>
					</tr>
				</c:forEach>
			</table>
		</c:if>
		<c:if test="${empty  emps}">
			<h2 align="center">没有任何员工信息。</h2>
		</c:if>

//}

//{--------<<<转发&重定向>>>-------------------------------------------------------------
https://blog.csdn.net/zhouysh/article/details/380364
#转发
	Servlet接收到浏览器请求后,进行一定的处理,先不进行响应,
	而是在服务端内部 0转发0 给其他Servlet继续处理.
	浏览器只发出 1次请求,'浏览器url不会改变',用户也感知不到请求被转发.

#重定向
	Servlet接收到浏览器请求并处理后,响应浏览器 302状态码和新地址;
	状态码302要求浏览器去请求新地址,整个过程浏览器发出 2次请求.
	
#二者区别
	1; 2	//发送请求次数(request个数)
	否; 是	//浏览器地址是否改变
	是; 否	//是否共享对象 request
	是; 否	//是否传递request中数据	
	是; 否	//目标资源是否可以是WEB-INF下资源
	
	转发 -> 只能是当前web应用的资源; 重定向 -> 任意资源,甚至网络资源.
	
	转发 -> '/'代表当前'web应用'的根目录; //http://localhost:8090/demo/
	重定向 -> '/'代表当前'web站点'的根目录. //http://localhost:8090/
	
#代码实现
	req.setAttribute(key, value); //转发前绑定数据,在目标资源取出数据
	req.getRequestDispatcher("转发地址").forward(req, resp);

	resp.sendRedirect("重定向地址"); //重定向
	
#防止表单重复提交
	0.表单重复提交3种场景
		(1).网络延迟,用户多次点击submit按钮
		(2).表单提交后,转发到目标页面,用户点击'刷新'按钮
		(3).提交表单后,点击浏览器的'后退'按钮,回退到表单页面,再次进行'提交'
		
		(0).点击'返回','刷新'原表单页面,再'提交' ---> 不属于表单重复提交
	
	1.用js控制表单只能提交一次 --> 针对场景(1)
		<script>
			//(1).只能提交一次
			var isCommitted = false;//表单是否已提交,默认false
			function doSubmit() {
				if (false === isCommitted) {
					isCommitted = true;
					return true;//返回true,让表单正常提交
				} else {
					return false;//返回false,则表单将不提交
				}
			}
			
			//(2).提交后,提交按钮置为不可用
			function doSubmit() {
				var btnSubmit = document.getElementById("submit"); //获取表单提交按钮
				btnSubmit.disabled = true; //置为不可用
				return true;
			}
		</script>
		<form id="form-login" method="post" onsubmit="doSubmit()" th:action="@{/user/login}"></form>
	
	2.表单提交后直接重定向到目标页面 --> 针对场景(2)
		//转发到目标页面,点击[刷新]会一直请求之前的表单.
		resp.sendRedirect("重定向地址"); //重定向
	
	3.利用Session的token机制 --> 针对场景(2)(3)
		//(1).生成token; 并转发token.html页面
		@GetMapping("/token")
		public String token(HttpSession session) {
			session.setAttribute("token", UUID.randomUUID().toString()); //token存Session中
			return "/mvc/token";
		}
		
		//(2).前台页面使用hidden存储token
		<form id="form-login" method="post" th:action="@{/mvc/login}">
			<input type="hidden" name="token" th:value="${session.token}"/>
			//...
			<button class="btn btn-primary" type="submit">登录</button>
		</form>
	
		//(3).后台处理表单提交
		@PostMapping("/login")
		public String login(HttpServletRequest request, HttpServletResponse response) {
			if (isRepeatSubmit(request)) {
				return "表单重复提交";
			}

			request.getSession().removeAttribute("token"); //非重复提交,则移除Session中的token
			return "登陆成功";
		}
		
		//(4).表单重复提交判断
		private boolean isRepeatSubmit(HttpServletRequest request) {
			String clientToken = request.getParameter("token");
			if (clientToken == null) { //<1>.request中没有token,则为重复提交
				return true;
			}

			String serverToken = (String) request.getSession().getAttribute("token");
			if (null == serverToken) { //<2>.当前用户的Session中不存在Token,则为重复提交
				return true;
			}

			if (!clientToken.equals(serverToken)) { //<3>.Session中Token与表单提交的Token不同,则为重复提交
				return true;
			}
			return false;
		}

	4.Token机制的另一种实现 --> 结合拦截器Interceptor,缓存Cache等
	
		https://blog.battcn.com/2018/06/12/springboot/v2-cache-locallock/
		https://www.jianshu.com/p/09c6b05b670a
	
//}

//{--------<<<Cookie&Session>>>----------------------------------------------------------
https://blog.csdn.net/u013210620/article/details/52318884

#Cookie
	1.基本概念
		HTTP是无状态协议,服务器不能记录浏览器的访问状态. 
		也就是说服务器不能区分两次请求是否由同一个客户端发出.
		
		Cookie 实际上就是服务器保存在浏览器上的一段信息,完成会话跟踪的一种机制.
		第一次访问,没有Cookie,服务器返回,浏览器保存.
		一旦浏览器有了 Cookie,以后每次请求都会带上,服务器收到请求后,就可以根据该信息处理请求。
		
        储存空间比较小 4KB
		数量限制,每一个域名下最多建 20个.
		用户可以清除 Cookie; 客户端还可以禁用 Cookie.
	
	2.自动删除
		//持久化Cookie: 设置了过期时间,浏览器就会把Cookie持久化到磁盘,再次打开浏览器,依然有效,直到过期!!!
		
		会话Cookie ---> 是在会话结束时(浏览器关闭)会被删除
		持久Cookie ---> 在到达失效日期时会被删除
		数量的上限 ---> 浏览器中的 Cookie 数量达到上限(默认20)
	
	3.相关属性
		cookie.setMaxAge(30); //最大时效,单位秒. 0:立即删除. 负数:永不删除, 默认-1.
		cookie.setPath(req.getContextPath()); //Cookie生效的路径,默认当前路径及子路径
	
    4.DEMO ---> 1自动登录(30s内) + 2浏览历史展示(待续)
        String name = req.getParameter("name");
        if (!StringUtils.isEmpty(name)) { //(1).请求为登录请求
            System.out.println("hello " + name);

            Cookie cookie = new Cookie("cookieName", "abc");
            cookie.setMaxAge(30);
			cookie.setPath(req.getContextPath());
            resp.addCookie(cookie);
        } else {
            Cookie[] cookies = req.getCookies();
            if (null != cookies && cookies.length > 0) { //(2).请求存在Cookie
                for (Cookie cookie : cookies) {
                    if ("cookieName".equalsIgnoreCase(cookie.getName())) {
                        name = cookie.getValue();

                        System.out.println("hello " + name);
                    }
                }
            } else { //(3).请求不是登录,也不存在Cookie
                resp.sendRedirect("/login"); //重定向登录界面
            }
        }

#Session概念
	当程序需要为某客户端的请求创建一个session时,
	服务器首先检查这个客户端的请求里是否已包含了一个session标识(SessionId)???
	
	如果已包含, 则说明之前已为此客户端创建过session, 
	服务器就按照 SessionId 把这个session检索出来使用(检索不到,新建一个);
	
	如果客户端请求不包含, 则为此客户端新建一个Session, 并将与此session相关联的 SessionId 返回给客户端保存.
	
	保存 SsessionId 的方式可以采用Cookie, 这样在交互过程中浏览器可以自动的按照规则把这个标识发挥给服务器.
	
#Session对比
	//Cookie 保存在客户端;
	用户可删除或禁用 Cookie;
	浏览器对 Cookie 的数量限制(20); 
	增加客户端与服务端之间的数据传输量; 
	
	//Session 保存在服务端.
	传递给用户端一个名为'JSESSIONID'的 Cookie, 通过它可以获取到用户信息对象 Session.

#Session创建
	//Session 机制也是依赖于 Cookie 来实现的.
	当'第一次'访问jsp或Servlet, 并且该资源显示指定'需要创建Session'. 此时服务器才会创建一个 Session 对象.
	Session 创建之后,同时还会自动创建一个名为'JSESSIONID'的 Cookie,返回给客户端.
	客户端收到 Cookie 后, 存储在浏览器内存中(可持久化到磁盘). 
	以后浏览器在发送就会携带这个特殊的 Cookie 对象.
	服务器通过'JSESSIONID'查找与之对应的Session对象,以区分不同的用户.
	
	(1).显示指定(需要)创建Session对象	
		request.getSession(true); //若存在则返回,否则新建一个Session.(默认为true)
		request.getSession(false); //若存在则返回,否则返回null
	
	(2).jsp显示指定(不需要)创建Session对象
		<%@ page language="java" ... session="false" %> //默认是true
		
		//若当前jsp是客户端访问的第一个资源, 由于页面指定, 则服务端不会创建Session对象.
		//若当前jsp不是..., 如果之前页面已创建,则返回之前Session; 之前没有,此页面也不会创建.
		
#Session销毁:
	(1).服务器关闭或web应用卸载;
	(2).HttpSession.invalidate(); //服务端显示销毁

	(3).Session过期. 浏览器关闭,或闲置一段时间(session-timeout值)没有请求服务器,session会自动销毁.
	   // 这个时间是根据服务器来计算的,而不是客户端. 所以在调试程序时,应该修改服务器端时间来测试,而不是客户端.
	   (A).配置apache-tomcat-9.0.13\conf\web.xml,默认30min
			<session-config>
				<session-timeout>30</session-timeout> //单位分钟
			</session-config>
	   (B).在项目的web.xml中设置,方法同上
	   (C).代码设置: session.setMaxInactiveInterval(30*60);//单位秒

	(4).关闭浏览器,但不意味着Session被销毁. // Session的创建和销毁是在服务器端进行的.
	   当浏览器访问服务器就会创建一个 SessionID,浏览器通过这个ID来访问服务器中所存储的Session.
	   当浏览器关闭再打开,此时浏览器已丢失之前的 SessionID,也就找不到服务器端的Session对象,所以无法自动登录.
	   
	   之前的Session对象会在<session-timeout>后自动销毁.
	   但是,关闭再打开,访问时携带上次的 SessionID,则可以重新找到之前的Session对象.
	   //http://127.0.0.1:8090/demo/session;JSESSIONID=FAAABD1D2B89791DEB647E74D49A7C3D (URL重写)
	   
#URL重写
	客户端保存'JSESSIONID',默认采用 Cookie 实现.
	当浏览器禁用 Cookie 时,可通过URL重写实现: //URL;jsessionid=xxx (将JSESSIONID拼接URL后面)
	
		String encodeURL = response.encodeURL(url); //url重写
		response.sendRedirect(encodeURL);
	   
#Session持久化
	/**持久化Session --> 持久化Cookie --> 设置Cookie过期时间*/
	Cookie cookie = new Cookie("JSESSIONID",session.getId());
	cookie.setMaxAge(90); //持久化该Cookie对象
	response.addCookie(cookie); //将Cookie对象发送给浏览器

	默认保存: C:\Users\BlueCard\AppData\Local\Temp\9121B10A811596BD85A3431BFBE71078B2880509\servlet-sessions

//}	

//{--------<<<filter>>>------------------------------------------------------------------
#过滤器(vs拦截器) --> 对发送到 Servlet 的请求进行拦截,并对响应也进行拦截.
	0.相关属性
		优先级: 多个filter,先配置,优先级高. --> @Order(n) n越小,优先级越高
		
		dispatcher: 指定过滤器所拦截的资源被 Servlet 容器调用的方式. 同一个filter可设置多个.
			REQUEST -> (默认) 'GET/POST直接'访问目标资源时,才会被被filter拦截. 
			FORWARD -> 通过 RequestDispatcher.forward() '转发'..,...
			INCLUDE -> 通过 RequestDispatcher.include() 访问时...
			ERROR   -> 通过声明式异常处理机制调用时...

	1.xml配置
		<filter> //xml配置
			<filter-name>testFilter</filter-name>
			<filter-class>com.atguigu.login.filter.TestFilter</filter-class>
		</filter>
		<filter-mapping>
			<filter-name>testFilter</filter-name>
			<url-pattern>/_*</url-pattern>
			<dispatcher>REQUEST</dispatcher>
		</filter-mapping>
		
	2.boot配置
		@Order(1) //boot配置 + 全局配置: @ServletComponentScan
		@WebFilter(filterName = "testFilter", urlPatterns = "/abc", dispatcherTypes = DispatcherType.REQUEST)
		public class TestFilter implements Filter {

			@Override //Servlet容器Tomcat启动时,加载filter初始化方法,且只调用一次. 单例
			public void init(FilterConfig filterConfig) throws ServletException { }

			@Override //过滤器核心逻辑,每次请求过滤都会调用一次
			public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
					throws IOException, ServletException { }

			@Override //销毁方法,释放资源,只被调用一次
			public void destroy() { }
		}
	
	3.登陆检测过滤器
		@Order(1)
		@WebFilter(filterName = "loginFilter", urlPatterns = "/filter/*")
		public class LoginFilter extends HttpFilter {

			@Override
			protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
					throws IOException, ServletException {
				if (null == request.getSession().getAttribute("username")) {
					response.sendRedirect("/login.html"); //未登录,拦截,转发到登录页面
				} else {
					chain.doFilter(request, response); //已登录,则继续后续处理. (FilterChain: 过滤器链对象)
				}
			}
		}
		
//}
	
//{--------<<<listener>>>----------------------------------------------------------------
#基础概念
	1.监听器
		用于对其他对象身上发生的事件或状态改变进行监听和相应处理的对象,当被监视的对象发生情况时,立即采取相应的行动.

	2.Servlet监听器
		//四大域对象: ServletContext; HttpSession; ServletRequest; PageContext
		用于监听web程序'三大域对象(PageContext 除外)'的创建与销毁事件, 及属性发生变化事件.
		其中,PageContext 生命周期为当前页面,所以不用监听.
		注册和调用过程都是由web容器自动完成的; 当监听的事件被触发时,自动调用自定义的处理方法.
		一个web程序只会为每个事件监听器创建一个对象,所以在自定义事件监听器时,应考虑多线程安全问题.
		
	3.Servlet监听器分类
		三大域对象'创建和销毁'的事件监听器.
		三大域对象'属性变更'的事件监听器. //较少使用
		感知Session绑定的事件监听器. //较少使用
		
#三大域对象创建和销毁的事件监听器
	1.配置方式
		<listener> //xml配置
			<listener-class>com.example.config.MyServletListener</listener-class>
		</listener>
		
		@WebListener + @ServletComponentScan //注解配置
	
	2.代码实现	
		@WebListener
		public class MyServletListener implements ServletContextListener, HttpSessionListener, ServletRequestListener {

			//SC对象创建时调用(web程序在服务器上部署时).
			//    用途: 创建数据库连接池; 创建Spring的IOC容器; 读取当前WEB应用的初始化参数
			@Override
			public void contextInitialized(ServletContextEvent sce) {
				//SC对象为整个web程序的共享内存,任何请求都可以访问其中的内容. 
				//SC对象在整个web程序的生命周期中最早被创建,最晚被销毁.
				ServletContext sc = sce.getServletContext();

				sc.setAttribute("start", System.currentTimeMillis()); //设置属性 -> 启动时的时间戳
				log.info("Servlet START AT: " + LocalDateTime.now());
			}

			//SC对象销毁时调用(web程序从服务器卸载时)
			//    SpringBoot项目 --> 只有'Run_As->Boot_App'这种方式启动,关闭时才会回调此方法
			@Override
			public void contextDestroyed(ServletContextEvent sce) {
				long start = (long) sce.getServletContext().getAttribute("start");// 读取属性 -> 应用总的运行时间(ms)
				log.info("Servlet END AT: " + LocalDateTime.now() + ", TIME(ms): " + (System.currentTimeMillis() - start));
			}

			@Override
			public void requestInitialized(ServletRequestEvent sre) {
				System.out.println("Request被创建之后调用 -> 客户端发送请求时");
			}

			@Override
			public void requestDestroyed(ServletRequestEvent sre) {
				System.out.println("Request销毁之前调用 -> 请求结束时");
			}

			@Override
			public void sessionCreated(HttpSessionEvent se) {
				System.out.println("Session对象创建时调用 -> 浏览器与服务器建立会话时");
			}

			@Override
			public void sessionDestroyed(HttpSessionEvent se) {
				System.out.println("Session对象销毁时调用 -> 具体详见<Session销毁>");
			}
		}
	
	
#三大域对象属性变更的事件监听器
	1.代码实现
		@WebListener
		public class MyAttributeListener implements ServletContextAttributeListener, HttpSessionAttributeListener,
				ServletRequestAttributeListener {
			@Override
			public void attributeAdded(ServletRequestAttributeEvent srae) {
				//三大域对象,各有3个属性变更的事件监听器,只以Request为例
				System.out.println("Request -> 添加..." + srae.getName() + " - " + srae.getValue());
			}

			@Override
			public void attributeReplaced(ServletRequestAttributeEvent srae) {
				System.out.println("Request -> 替换..." + srae.getName() + " - " + srae.getValue());
			}

			@Override
			public void attributeRemoved(ServletRequestAttributeEvent srae) {
				System.out.println("Request -> 删除..." + srae.getName() + " - " + srae.getValue());
			}
		}
		
	2.测试接口
		@GetMapping("/attribute")
		public void attribute(HttpServletRequest request) {
			request.setAttribute("attr", "123"); // Request -> 添加...attr - 123
			request.setAttribute("attr", "456"); // Request -> 替换...attr - 123
			request.removeAttribute("attr");     // Request -> 删除...attr - 456
		}
	
#感知Session绑定的事件监听器
	///监听实现该接口的 Java 类对象被绑定到 Session 或从 Session 中解除绑定的事件.
	
	1.代码实现
		//不需要添加注解 @WebListener
		public class MySessionBindingListener implements HttpSessionBindingListener, HttpSessionActivationListener, Serializable {
			@Override
			public void valueBound(HttpSessionBindingEvent event) {
				System.out.println("当前对象 -> 被Session绑定..." + event.getName() + " - " + event.getValue() + " - " + event.getSession());
			}

			@Override
			public void valueUnbound(HttpSessionBindingEvent event) {
				System.out.println("当前对象 -> 被Session解绑..." + event.getName() + " - " + event.getValue() + " - " + event.getSession());
			}
			
			// Session对象创建时调用 -> 浏览器与服务器建立会话时
			// 当前对象 -> 被Session绑定...bind - com.x.MySessionBindingListener@3760f033 - org.x.StandardSessionFacade@1377c8e6
			// Session对象销毁时调用
			// 当前对象 -> 被Session解绑...bind - com.x.MySessionBindingListener@3760f033 - org.x.StandardSessionFacade@1377c8e6
			
			@Override
			public void sessionWillPassivate(HttpSessionEvent se) {
				// 当javaBean实现 HttpSessionActivationListener 和 Serializable 接口,
				// 就可以感知到随Session被钝化和活化事件. (程序关闭,Session钝化磁盘.)
				System.out.println("当前对象'钝化' ->从内存写入磁盘..." + se.getSession());
			}

			@Override
			public void sessionDidActivate(HttpSessionEvent se) {
				System.out.println("当前对象'活化' ->从磁盘读到内存..." + se.getSession());
			}
		}
	
	2.测试接口(绑定/解绑)
		@GetMapping("/bind")
		public void bind(HttpSession session) {
			MySessionBindingListener listener = new MySessionBindingListener(); //实例化
			session.setAttribute("bind", listener); //绑定
			session.setMaxInactiveInterval(5); //5s后Session自动过期
		}
	
	3.测试接口(活化/钝化)
		@GetMapping("/session")
		public void session(HttpServletRequest request, HttpSession session) {
			MySessionBindingListener bind = (MySessionBindingListener) session.getAttribute("bind");
			if (null == bind) {
				System.out.println("新建并放入Session!!!");
				bind = new MySessionBindingListener();
				session.setAttribute("bind", bind);
			} else {
				System.out.println("从Session读取到 -> " + bind);
			}
		}
//}/

//{--------<<<RBAC>>>--------------------------------------------------------------------
#RBAC -> 基于角色的访问控制(Role-Base-Access-Control)
	//一种思想.根据 RBAC 思想进行数据库设计,根据数据库设计更好的完成权限控制.
	常用分类:(1).菜单功能; (2).url控制; (3).资源可见性控制;

	使用前: 用户表 -> 用户&菜单 <- 菜单表
	使用后: 用户表 -> 用户&角色 <- 角色表 -> 角色&菜单 <- 菜单表

//}

//{--------<<<ajax>>>--------------------------------------------------------------------
#html
	<radio>: 单选框,只有加上'name'才具有互斥效果. 表单提交的是'value'值.
		   //男 <input type="radio" name="gender" value="1" />
		   //女 <input type="radio" name="gender" value="0" />
	
#js(事件驱动)
	用户事件: 用户操作; 如单击,鼠标移入,鼠标移出等
	系统事件: 由系统触发的事件; 如文档加载完成

		document.getElementsByTagName("tag");	--->	$('tag') //根据 tag 查询.(元素数组)
		document.getElementById("id");			--->	$('#id') //根据 id 查询.(单个元素)
		document.getElementsByClassName("class"); ->	$('.class') //根据 class 查询.(元素数组)
		
		document.getElementsByName("name");		//返回带有指定 name 的对象集合
	
#jQuery(js函数库)
	通过选取html元素,并对选取的元素执行某些操作.

		$(this).hide()		//隐藏当前元素
		$("p").hide()		//隐藏所有 <p> 元素.(tag)
		$("p.test").hide()	//隐藏所有 class="test" 的 <p> 元素.
		$("#test").hide()	//隐藏所有 id="test" 的元素.(#id)
	
		$("p:first")		//选取第一个 <p> 元素
		$("[href]")			//选取带有 href 属性的元素	

#click
	1.动态绑定事件
		<button id="btn0">动态绑定事件</button>
		
        $(function () { //简写前: $(document).ready(function () {
            $("#btn0").click(function () {
                alert("点我上王者..." + $(this).attr('id'));
            });
        });
	
	2.静态指定方法
		<button id="btn1" onclick="clickBtn1(this)">静态指定方法</button>
		
        function clickBtn1(e) {
            alert("点我上王者..." + $(e).attr('id')); //两种获取对象属性的方法 --> $(e).attr('id')
        }

#ajax.load -> 从服务器上获取静态的数据文件
	///load(url [,data][,callback])
	
	1.无参且GET(默认)
		$('#resText').load('test.php', function(){ ... });
		
	2.有参并POST		
		$('#resText').load('test.php', {name:'rain',age:'22'}, function(){ ... });
		
	3.有回调函数的DEMO		
		$(function () {  //简写前: $(document).ready(function () {
			$('#loadBtn').click(function () {
				var msgUrl = $(this).attr('href');

				//callback: [0]请求成功时的结果; [1]请求状态(success,error); [2]包含 XMLHttpRequest 对象
				$('#showMsg').load(msgUrl, function (responseTxt, statusTxt, xhr) {
					alert(responseTxt + " - " + statusTxt + " - " + xhr); //2019-01-04 11:26:15.559 - success - [object Object]
					if ("success" == statusTxt) {
						$('#showMsg').val(responseTxt); //简写前: $('#showMsg').attr('value', responseTxt);
					}
				});
			});
		});
		
		<div>
			<input id="showMsg" type="text">
			<button id="loadBtn" th:href="@{/mvc/getMsg}">load</button>
		</div>
		
#ajax.get -> 通过GET请求从服务器请求数据
	///$.get(url[,data][,callback][,type])
	
	//[url] 请求url(默认当前页面);   [data] 请求参数; 
	//[callback] 请求成功时回调函数; [type] 预期的服务器响应的数据类型.
	
	1.服务端返回json
		<button id="getJSON" onclick="getJsonFun(this)" th:url="@{/mvc/getJSON}">getJSON</button> //thyeleaf自定义属性 th:url
	
		//(1-0).静态指定方法
        function getJsonFun(e) {
            $.get($(e).attr('url'),
                {username: $('#username').val(), password: $('#password').val()},
                function (data, status) { //参数顺序不能调换
                    if ('success' === status) {
						alert(JSON.stringify(data) + " - " + textStatus); //{"uName":"admin","uPwd":"11"} - success
                        alert(data.uName + " - " + data.uPwd); //admin - 11
                    }
                }, 'json');
        }
		
		//(1-1).对于服务器返回json的可直接使用 $.getJSON(); 替换 $.get();
        function getJsonFun(e) {
            $.getJSON($(e).attr('url'),
                {username: $('#username').val(), password: $('#password').val()},
                function (data) {
                    alert(data.uName + " - " + data.uPwd);
                });
        }
		
		//(2-0).动态绑定方法
		<button id="btnGetJSON" th:attr="url=@{/mvc/getJSON}">getJSON</button>
		
		$(function () { //简写前: $(document).ready(function () {
            $('#btnGetJSON').click(function () {
                $.getJSON($(this).attr('url'),
                    {username: $('#username').val(), password: $('#password').val()},
                    function (data) {
                        alert(data.uName + " - " + data.uPwd);
                    });
            });
        });
		
		//(3-0).后台代码
		@GetMapping("/getJSON")
		public JSONObject getJSON(@RequestParam("username") String uName, @RequestParam("password") String uPwd) {
			//... ...
			return json;
		}
		
	2.服务端返回String
		<button id="getStr" onclick="getStrFun(this)" th:attr="url=@{/mvc/getMsg}">getStr</button>
		
        function getStrFun(e) {
            $.get($(e).attr('url'),
                function (data) {
                    alert(data);
                });
        }
		
	3.服务端返回xml
		header("Content-Type:text/xml; charset=utf-8"); //服务端设置xml
		
		$("#send").click(function(){
			$.get("get2.php", {
			  username : $("#username").val(), 
			  content : $("#content").val()  
			}, function(data, status){ //直接使用attr(), find(), filter()等方法
				var username = $(data).find("comment").attr("username");
				var content = $(data).find("comment content").text();
				var txtHtml = "<div class='comment'><h6>"+username+":</h6><p class='para'>"+content+"</p></div>";
				
				$("#resText").html(txtHtml);
			},'XML'); //期待服务端返回xml
		});
		
#ajax.post
	1.服务端返回json
		<button id="postJSON" onclick="postJSONFun(this)" th:attr="url=@{/mvc/postJSON}">postJSON</button>
		
        function postJSONFun(e) {
            $.post($(e).attr('url'),
                {username: $('#username').val(), password: $('#password').val()},
                function (data) {
                    alert(data.uName + " - " + data.uPwd);
                }, 'json');
        }
		
#ajax.delete
	1.restful风格
		$('#deleteBtn').click(function () {
			$.ajax({
				type: 'DELETE', //仅部分浏览器支持
				url: $(this).attr('href'),
				dataType: 'text',
				success: function (data) {
					$('#showMsg').val(data);
				}
			});
		});
		
		<button id="deleteBtn" th:href="@{/mvc/deleteMsg/5}">deleteMsg</button>
		
		@DeleteMapping("/deleteMsg/{id}")
		public String deleteMsg(@PathVariable String id) {}
		
	2.传统风格
		$('#deleteBtn0').click(function () {
			$.ajax({
				type: 'DELETE',
				url: $(this).attr('href'),
				data: {'id': 5, 'name': 'wang'},
				dataType: 'text',
				success: function (data) {
					$('#showMsg').val(data);
				}
			});
		});
		
		<button id="deleteBtn" th:href="@{/mvc/deleteMsg}">deleteMsg</button>
		
		@DeleteMapping("/deleteMsg")
		public String delete(@RequestParam String id, @RequestParam String name) { }
		
#ajax.ajax -> 封装方法; get/post是其具体实现, 如果需要在出错时执行函数,请使用 $.ajax
	1.返回json
		<button id="ajaxBtn" onclick="clickAjax(this)" th:attr="url=@{/mvc/getJSON}">ajax</button>
		
        function clickAjax(e) {
            $.ajax({
                url: $(e).attr('url'), //url
                type: 'GET', //GET/POST/DELETE/PUT
                data: {username: $('#username').val(), password: $('#password').val()}, //args; 后台接收 @RequestParam
                dataType: 'json', //返回json
                success: function (data, status) { //成功时,回调
                    alert(JSON.stringify(data) + " - " + status);
                },
                error: function (data, status) { 
                    alert(JSON.stringify(data) + " - " + status);
                }
            });
        }

//}




































