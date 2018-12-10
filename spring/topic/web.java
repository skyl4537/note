
///JavaWeb的技术体系图--->附件

#HTML标签
	radio: 单选框,只有加上'name'才具有互斥效果. 表单提交的是'value'值.
		//男 <input type="radio" name="gender" value="1" />


#重定向和转发
	0.重定向
		Servlet接收到浏览器请求并处理后,响应浏览器 302状态码和新地址;
		状态码302要求浏览器去请求新地址,整个过程浏览器发出 2次请求.

	1.转发
		Servlet接收到浏览器请求后,进行一定的处理,先不进行响应,
		而是在服务器端内部 0转发0 给其他Servlet程序继续处理.
		浏览器只发出 1次请求,'浏览器url不会改变',用户也感知不到请求被转发.
		
	2.区别重定向和转发
		2; 1	//发送请求次数
		是; 否	//浏览器地址是否改变
		否; 是	//是否共享对象 request
		否; 是	//是否传递request中数据
		否; 是	//目标资源是否可以是WEB-INF下资源
		
		response.sendRedirect("重定向地址"); //重定向
	
		request.setAttribute(key, value); //转发前绑定数据,在目标资源取出数据
		request.getRequestDispatcher("转发地址").forward(request, response);

#jsp页面(Java Server Pages)
	本质就是一个Servlet; JSP善于处理页面显示; Servlet善于处理业务逻辑, 二者结合使用.
	执行时,先转化为java文件,再编译成class文件.
	转化过程: java代码照搬; html+css+表达式等通过流输出 out.write() 
		
	0.内置对象
		request			//HttpServletRequest 对象
		response		//HttpServletResponse 对象
		out				//用于页面显示信息,相当于 out=response.getWriter(); 
		application		//ServletContex t对象
		session			//HttpSession 对象
		
		page			//对应当前Servlet对象,实际上就是this
		pageContext		//当前页面的上下文,也是一个域对象
		config			//对应Servlet中的 ServletConfig
		exception		//错误页面中异常对象 Throwable
		
	1.jsp脚本片段
		<% 
			String hello = "hello jsp!!!"; //嵌入到jsp中的Java代码段
			syso(hello);
		%> 
		
	2.jsp表达式
		<%=hello %> //将Java变量输出到页面中

	3.EL表达式
		用于代替jsp表达式(<%= %>),在页面中做输出操作
		使用EL表达式输出数据时,如果有则输出数据,如果为null则什么也不输出
		EL取值的四个域: pageScope; requestScope; sessionScope; applicationScope;
		
		//从以上4个Scope域中,从小到大依次查找key为 loginMsg 对应的值
		<span>${requestScope.loginMsg}</span> //简化: <span>${loginMsg}</span>
	
#js
	//事件驱动
	用户事件: 用户操作; 如单击,鼠标移入,鼠标移出等
	系统事件: 由系统触发的事件; 如文档加载完成

		document.getElementById("id"); //根据id值查询.(具体的元素节点)
		document.getElementsByTagName("tag"); //根据标签名查询.(元素节点数组)
		document.getElementsByName("name值"); //根据name属性值查询.(元素节点数组)
	
#jQuery(js函数库)
	通过选取html元素,并对选取的元素执行某些操作.

		$(this).hide()		//隐藏当前元素
		$("p").hide()		//隐藏所有 <p> 元素.(tag)
		$("p.test").hide()	//隐藏所有 class="test" 的 <p> 元素.(.class)
		$("#test").hide()	//隐藏所有 id="test" 的元素.(#id)
	
		$("p:first")		//选取第一个 <p> 元素
		$("[href]")			//选取带有 href 属性的元素
	
#ajax
	0.load——通常用来从Web服务器上获取静态的数据文件
		///load(url [,data][,callback])
		
		//(0).无参数传递,默认是GET方式
		$('#resText').load('test.php',function(){
			//...
		});
		
		//(1).有参数传递,自动转换为POST
		$('#resText').load('test.php',{name:'rain',age:'22'}，function(){
			//...
		});
		
		//(2).可选的 callback 参数: 
		//	responseTxt - 包含调用成功时的结果内容
		//	statusTXT - 请求状态: success,error
		//	xhr - 包含 XMLHttpRequest 对象
		$(function(){
			$("button").click(function(){
				$("#div1").load("/try/ajax/test.txt",function(responseTxt,statusTxt,xhr){
					if(statusTxt=="success")
						alert("外部内容加载成功!");
					if(statusTxt=="error")
						alert("Error: "+xhr.status+": "+xhr.statusText);
				});
			});
		});
		
	2.get ///$.get(url[,data][,callback][,type])
	
		//(0).服务器返回String
		$(function(){
		   $("#send").click(function(){
				$.get("get1.php", {
						username : $("#username").val(), 
						content : $("#content").val()  
					}, function(data, textStatus){
						$("#resText").html(data); //把返回的数据添加到页面上
					}
				);
		   })
		})
	
		//(1).服务端返回xml数据.
		header("Content-Type:text/xml; charset=utf-8"); //服务端设置xml
	
		//直接使用attr(), find(), filter()等方法
		$(function(){
			$("#send").click(function(){
				$.get("get2.php", {
				  username : $("#username").val() , 
				  content : $("#content").val()  
				}, function(data, textStatus){
					var username = $(data).find("comment").attr("username");
					var content = $(data).find("comment content").text();
					var txtHtml = "<div class='comment'><h6>"+username+":</h6><p class='para'>"+content+"</p></div>";
					
					$("#resText").html(txtHtml); //把返回的数据添加到页面上
				},'XML'); //期待服务器返回的数据格式
			});
		})
		
		//(2).服务端返回json数据.
		$(function(){
			$("#send").click(function(){
				$.get("get3.php", {
					username :  $("#username").val() , 
					content :  $("#content").val()  
				}, function(data, textStatus){
					var username = data.username;
					var content = data.content;
					var txtHtml = "<div class='comment'><h6>"+username+":</h6><p class='para'>"+content+"</p></div>";
					
					$("#resText").html(txtHtml); //把返回的数据添加到页面上
				},"json"); //期待服务器返回的数据格式
			});
		})
	
	3.post	///语法同get
		$(function(){
			$("#send").click(function(){
				$.post("post1.php", {
					username : $("#username").val() , 
					content : $("#content").val()  
				}, function (data, textStatus){
					$("#resText").html(data); // 把返回的数据添加到页面上
				});
			})
		})
		
	4.ajsx返回JSON数据
		<head>
			<meta charset="UTF-8">
			<title>员工列表</title>
			//h5中,可以省略 type="text/javascript"
			<script type="text/javascript" src="scripts/jquery-1.7.2.min.js"></script>
			<script type="text/javascript">
				$(function(){
					$("#showTable").click(function(){
						//异步请求员工数据(json格式)
						$.ajax({
							url:"GetEmps",
							type:"post",
							dataType:"json",
							success:function(data){ //框架已转换成json对象.
								var str = "<tr><th>ID</th><th>LastName</th><th>Email</th><th>Gender</th><th>DeptName</th></tr>";
								for(var i = 0 ;i <data.length;i++){
									var emp = data[i];
									str+="<tr align='center'><td>"+emp.id+"</td><td>"+emp.lastName+"</td><td>"+emp.email+"</td><td>"+emp.gender+"</td><td>"+emp.dept.deptName+"</td></tr>";
								}
								
								$("#empTable").html(str);
							}
						});	
					});
				});
			</script>
		</head>
		<body>
			<input id="showTable" type="button" value="显示员工信息列表" />
			<h1 align="center">员工信息列表</h1>
			<table id="empTable" align="center" border="1px" width="70%" cellspacing="0px"></table>
		</body>

#Cookie
	HTTP是无状态协议,服务器不能记录浏览器的访问状态.
	也就是说服务器不能区分两次请求是否由同一个客户端发出.
	
	Cookie 实际上就是服务器保存在浏览器上的一段信息. 浏览器有了 Cookie 之后,
	每次请求都会带上,服务器收到请求后,就可以根据该信息处理请求。
	
		// 保存到浏览器端
		Cookie cookie = new Cookie("username", username);
		cookie.setPath(); //默认路径: 当前web应用名
		cookie.setMaxAge(); //默认时效: 当前浏览器的关闭
		response.addCookie(cookie);
		
		// 从请求中读取
		Cookie[] cookies = request.getCookies();

#Session
	Cookie局限: 增加客户端与服务端的数据传输量; 浏览器对 Cookie 数量的限制.
	
	Session: 将用户数据保存在服务端 Session 对象中,然后传递给用户一个名为
	JSESSIONID 的 Cookie, 通过它可以获取到用户信息的 Session.
	
	0.Session的工作原理
		(0).Session 的创建时机是在 request.getSession(); 方法第一次被调用时.
		(1).Session 被创建后,同时还会创建一个名为 JSESSIONID 的 Cookie.
		(2).这个 Cookie 的默认时效就是当前会话.
		(3).简单来说,Session 机制也是依赖于 Cookie 来实现的.

	1.Session时效: 默认30分钟,即两次请求之间不能超过30分钟.
		//在tomcat的/conf/web.xml配置(不建议修改)
		<session-config>
			<session-timeout>30</session-timeout>
		</session-config>

	2.URL重写
		整个会话控制技术体系中,保持 JSESSIONID 的值主要通过 Cookie 实现.
		对于浏览器禁用 Cookie 情况,可通过URL重写实现.
		URL重写: 将 JSESSIONID 以固定格式附着在 URL 地址后面. //URL;jsessionid=xxx
	
		String encodeURL = response.encodeURL(url);
		response.sendRedirect(encodeURL);

#jstl(JSP标准标签库)
	//JSP标签集合,封装了JSP应用的通用核心功能.
	
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

#过滤器(区别于拦截器)
	(1).在指定Web资源收到请求之前,浏览器收到响应之前, 分别对请求和响应信息做一些相应的处理.
	(2).多个filter,先配置,优先级高.

	///主页面访问权限控制,登陆才可访问
	0.xml配置
		<filter>
			<filter-name>LoginFilter</filter-name>
			<filter-class>com.atguigu.login.filter.LoginFilter</filter-class>
		</filter>
		<filter-mapping>
			<filter-name>LoginFilter</filter-name>
			<url-pattern>/_*</url-pattern>
		</filter-mapping>
	
	1.代码实现
		public class LoginFilter implements Filter {
			//servlet容器tomcat启动时,加载filter初始化方法
			@Override
			public void init(FilterConfig filterConfig) throws ServletException {}
			
			@Override
			public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
					throws IOException, ServletException {
				HttpServletRequest req = (HttpServletRequest) request; 
				HttpServletResponse resp = (HttpServletResponse) response ;
				
				String url = req.getRequestURL().toString(); //请求url
				if(url.endsWith("/main.jsp")) {
					resp.sendRedirect("login.jsp"); //过滤,引导去往登录页面
				}else {
					//放行,继续执行后续的处理. FilterChain: 过滤器链对象
					chain.doFilter(req, resp);
				}
			}
			
			//销毁方法
			@Override
			public void destroy() {}
		}

#监听器
	用于监听JavaWeb程序中的事件(生命周期; 数据绑定).
	如: ServletContext,HttpSession,ServletRequest 的创建,修改和删除.
	
	0.ServletContext -> 统计网站访问量
		// 每个 Web 应用中都有一个 ServletContext, 其存储的资源可被应用内的各个模块所共享. 
		// 在 web 应用的生命周期中, ServletContext 对象是最早被创建, 最晚被销毁.
	
		//主要作用: 在app 启动/停止 时,添加要处理的逻辑!!
		项目启动后,读取数据库内容,设置全局属性, 在其它 Servlet 进行全局属性访问
		app关闭时,存储当前访问量到本地文件(数据库); app启动时,读取本地文件中的访问量,开始自增
	
		//xml配置:
		<listener>
			<listener-class>com.atguigu.login.listener.MyServletContextListener</listener-class>
		</listener>
	
		//boot配置:
		全局注解和局部注解: @ServletComponentScan, @WebListener
	
		@Slf4j
		@WebListener
		public class MyServletContextListener implements ServletContextListener {

			@Autowired
			ServletContext context; //注解获取对象

			// web应用启动时调用
			@Override
			public void contextInitialized(ServletContextEvent sce) {
				// 添加此方法, 防止 @Autowired 失败!!
				// 因为项目启动时,相应的 BeanFactory 还没加载,出现空指针问题
				WebApplicationContextUtils.getRequiredWebApplicationContext(sce.getServletContext())
						.getAutowireCapableBeanFactory().autowireBean(this);

				ServletContext sc = sce.getServletContext();
				sc.setAttribute("start", getNowInstant());// 设置自定义属性-启动时的时间戳
				log.warn("SPRINGBOOT START AT: " + LocalDateTime.now());
			}

			// web应用关闭时调用 -> (只有 Run_As->sp_app 这种方式启动,关闭时才会回调此方法)
			@Override
			public void contextDestroyed(ServletContextEvent sce) {
				long start = (long) context.getAttribute("start");// 读取自定义属性-应用总的运行时间(ms)
				log.warn("SPRINGBOOT END AT: " + LocalDateTime.now() + ", ALL TIME(ms): " + (getNowInstant() - start));
			}

			public long getNowInstant() { //默认不是东八区
				return Instant.now().atOffset(ZoneOffset.ofHours(8)).toEpochSecond();
			}
		}
	
	0.HttpSession -> 在线人数统计
		@WebListener
		public class MyHttpSessionListener implements HttpSessionListener {
			
			//session对象创建时执行
			public void sessionCreated(HttpSessionEvent se) {
				ServletContext sc = se.getSession().getServletContext();
				Object count = sc.getAttribute("count"); //在线人数count
				if(count == null) {
					sc.setAttribute("count", 1);
				}else {
					sc.setAttribute("count", (Integer)count+1);
				}
			}

			//session对象销毁时执行
			public void sessionDestroyed(HttpSessionEvent se) {}
		}

		
		
///--------<<<RBAC>>>------------------------------------------------------------------------
#RBAC -> 基于角色的访问控制(Role-Base-Access-Control)
	//一种思想.根据 RBAC 思想进行数据库设计,根据数据库设计更好的完成权限控制.
	常用分类:(1).菜单功能; (2).url控制; (3).资源可见性控制;

	使用前: 用户表 -> 用户&菜单 <- 菜单表
	使用后: 用户表 -> 用户&角色 <- 角色表 -> 角色&菜单 <- 菜单表

