
//{--------<<<ABC>>>-------------------------
#什么是 Struts
	1.Struts 是一个按 MVC 模式设计的 Web 层框架,其实它就是一个大大的 servlet,
		这个 Servlet 名为 ActionServlet,或是 ActionServlet 的子类. 
		可以在 web.xml 文件中将符合某种特征的所有请求交给这个 Servlet 处理,
		这个 Servlet 再参照一个配置文件(通常: /WEB-INF/struts-config.xml)
		将各个请求分别分配给不同的 action 去处理.
		//扩展: struts 的配置文件可以有多个,可以按模块配置各自的配置文件,有效防止配置文件的过度膨胀.
		
	2.ActionServlet把请求交给action去处理之前, 会将请求参数封装成一个formbean对象 （就
		是一个 java 类,这个类中的每个属性对应一个请求参数） ,封装成一个什么样的 formbean
		对象呢？看配置文件.
		
	3.要说明的是, ActionServlet 把 formbean 对象传递给 action 的 execute 方法之前,可能
		会调用 formbean 的 validate 方法进行校验,只有校验通过后才将这个 formbean 对象传递
		给 action 的 execute 方法, 否则, 它将返回一个错误页面, 这个错误页面由 input 属性指定,
		（看配置文件）作者为什么将这里命名为 input 属性,而不是 error 属性,我们后面结合实
		际的运行效果进行分析.
	
	4.action 执行完后要返回显示的结果视图,这个结果视图是用一个 ActionForward 对象来表
		示的,actionforward 对象通过 struts-config.xml 配置文件中的配置关联到某个 jsp 页面,因
		为程序中使用的是在 struts-config.xml 配置文件为 jsp 页面设置的逻辑名,这样可以实现
		action 程序代码与返回的 jsp 页面名称的解耦.
		
	

//}

//{--------<<<hello>>>------------------------
#新建 web 项目;导入 struts2 的jar包
#web.xml	
	<filter>
		<filter-name>struts2</filter-name> //struts2 核心过滤器
		<filter-class>org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>struts2</filter-name>
		<url-pattern>*.action</url-pattern> //struts底层基于拦截器,拦截 .action 结尾的请求.
	</filter-mapping>
	
#struts.xml(src目录下) ///文件头,直接拷贝 core 包下的 struts-default.xm 即可.
	<package name="hello" namespace="/" extends="struts-default">
		<action name="hello" class="cn.x.action.HelloAction" method="hello">
			<result name="success" type="dispather">/success.jsp</result>
			<result name="failed">/error.jsp</result>
		</action>
	</package>
	
#后台Action
	///Servlet 默认执行 service(); Struts2 默认执行 execute().
	//Servlet.service(HttpServletRequest, HttpServletResponse) -> 无返回值
	//Struts2 方法都是 public,返回值都是 String, 并且方法都没有参数
	public class HelloAction {
		public String execute(){
			System.out.println("hello struts2 !!");
			return "success";
		}
	}

#测试url
	http://localhost:8080/struts2/hello.action
	
//}

//{--------<<<config>>>-----------------------
#配置文件加载顺序
	struts-default.xml --> struts-plugin.xml --> struts.xml
	
#package	
	//name		-> 自定义,模块标识.
	//namespace	-> 模块对应的前缀 url,类比Spring的类注解 @RequestMapping.
	//extends	-> 直接或间接继承 struts-default
	<package name="hello" namespace="/" extends="struts-default">
	
		//name		-> 请求名称,不要前缀 /,不要后缀 .action
		//class		-> Action的包名+类名
		//method	-> hello请求对应的处理方法,默认 execute();适用于多个请求放同一个处理类中.
		<action name="hello" class="cn.x.action.HelloAction" method="hello">
		
			//result	-> 结果集
			//name		-> 匹配请求处理方法的返回值,默认 "success"
			//type		-> 结果处理类型,默认转发 dispather
			<result name="success" type="dispather">/success.jsp</result>
			<result name="failed">/error.jsp</result>
		</action>
	</package>
	
#result.name
	Action.SUCCESS	-> 执行成功,跳转下一个视图
	Action.ERROR	-> 执行失败,显示失败视图
	Action.NONE		-> 执行成功,不需要视图显示
	Action.LOGIN	-> 需要登录后,才能执行
	
#result.type
	dispather	->	转发,和 servlet 一致. 如果 request 中有数据要到视图显示,那么使用 dispatcher.
	redirect	->	重定向,如果是重定向到 jsp 页面,可以直接重定向;
					如果是重定向到另一个 action,需注意是否配置了 action 的后缀名.
					如果要求有后缀名,那么重定向的 action 一定要加上后缀名.
				   
	redirectAction:	重定向到另一个 action,不用加后缀名 action.
	stream		->	以流的形式显示(文件下载).
	
#配置全局结果集
	<global-results>
		<result name="login">/login.jsp</result>
	</global-results>

#常量配置(2种方式)
	1.struts.xml
		<constant name="struts.i18n.encoding" value="UTF-8"/> //乱码解决
		<constant name="struts.action.extension" value="action,,do"/> //自定义扩展名(逗号分隔)
		
		<constant name="struts.devMode" value="true"/> //设置开发模式,自动加载修改文件
		<constant name="struts.configuration.xml.reload" value="true"/>
	
	2.struts.properties(src目录下)
		struts.i18n.encoding = UTF-8

#团队协作开发配置 include
	<include file="config/x/struts/user.xml"/> //用户模块
	<include file="config/x/struts/order.xml"/> //订单模块
	
//}
	
//{--------<<<配置优化>>>---------------------
#传统方式(日益庞大)
	1.后台Action
		public class UserAction {
			public String save(){
				return Action.SUCCESS;
			}
			
			public String find(){
				return Action.SUCCESS;
			}
		}
		
	2.struts.xml
		<action name="save" class="cn.x.action.UserAction" method="save"> //增
			<result type="redirectAction">find</result>
		</action>
		<action name="find" class="cn.x.action.UserAction" method="find"> //查
			<result>/list.jsp</result>
		</action>
		
#使用DMI动态方法调用(存在安全隐患,不推荐)

#使用通配符(推荐)
	1.后台Action
		public class UserAction {
			public String save(){
				return Action.SUCCESS;
			}
			
			public String find(){
				return "list";
			}
		}
		
	2.struts.xml
		//*			-> 表示匹配一个或多个字符
		//占位符{1}	-> 表示第一个*匹配的内容
		<action name="*" class="cn.x.action.AddAction" method="{1}">
			<result type="redirect">find</result>
			<result name="list">/list.jsp</result>
		</action>
		
	3.执行顺序
		(1).客户端发送 save.action, action的*匹配 save, method的{1}匹配 save,即方法 save()
		(2).返回 SUCCESS,匹配 result 第一个,重定向到 find.action
		(3).继续匹配 action 的*,匹配到 find,执行 find()
		(4).返回 "list",匹配 result 第二个,即页面 list.jsp

//}

//{--------<<<数据处理>>>---------------------
#3种数据处理模式 --> ///建议在实体类属性比较多时,采用 模型驱动 进行开发
		<package name="user" extends="struts-default"> //struts.xml
			<action name="register"	class="cn.x.action.RegisterAction" method="register">
				<result name="success">/show.jsp</result>
			</action>
		</package>
		
#属性驱动
	1.前台页面
		<form method="post" action="register.action">
			账户: <input type="text" name="username" />
			密码: <input type="password" name="password" />
			<input type="submit" value="提交" />
		</form>
		
	2.后台Action
		public class RegisterAction {
			private String username; //GET/SET省; 属性
			private String password; 

			public String register() {
				System.out.println(username);
				return Action.SUCCESS;
			}
		}
		
	3.回显页面show.jsp
		<span>恭喜用户 ${username} 注册成功!!</span>

#对象驱动 
	1.前台页面
		<form method="post" action="register.action">
			账户: <input type="text" name="user.username" /> //前台使用 user.username
			密码: <input type="password" name="user.password" />
			<input type="submit" value="提交" />
		</form>
	
	2.后台Action
		public class RegisterAction {
			private User user; //GET/SET省; 将属性封装到对象 User -> 一定要有无参构造方法.

			public String register() {
				System.out.println(user.username + " - " + user.password);
				return Action.SUCCESS;
			}
		}
		
	3.回显页面show.jsp
		<span>恭喜用户 ${user.username} 注册成功!!</span> //前台使用 user.username

#模型驱动
	1.前台页面
		<form method="post" action="register.action">
			账户: <input type="text" name="username" /> //前台使用 username
			密码: <input type="password" name="password" />
			<input type="submit" value="提交" />
		</form>
	
	2.后台Action
		public class RegisterAction implements ModelDriven<User> {
			private User user = new User(); //GET/SET省; 属性封装到对象,对象转换成模型 Model.
			
			public User getModel() {
				return user;
			}

			public String register() {
				System.out.println(user.username + " - " + user.password);
				return Action.SUCCESS;
			}
		}
		
	3.回显页面show.jsp
		<span>恭喜用户 ${username} 注册成功!!</span> //前台使用 username

//}

//{--------<<<Action实现>>>-------------------
#定义一个 pojo
		//自定义普通的 java 类,不具有侵入型	---> 常用√
		public class PojoAction {
			public String execute() {
				System.out.println("pojo action");
				return "success";
			}
		}

#实现接口 Action 
		//使得编写的代码更加规范	
		public class InterfaceAction implements Action {
			public String execute() throws Exception {
				System.out.println("interface action");
				return SUCCESS;
			}
		}

#继承类 ActionSupport
		//可以继承一些已实现的功能; 如: 验证,国家化等
		public class ExtendsAction extends ActionSupport {
			@Override
			public String execute() throws Exception {
				System.out.println("extends action");
				return SUCCESS;
			}
		}
 //}

//{--------<<<ActionContext>>>----------------
#Map结构的容器,Action的上下文; 本质是一个 ThreadLocal.
#客户端的每次请求都会生成一个 AC 对象; Servlet 只在第一次请求时创建; --> 所以前者是线程安全,后者不是.

#存放 Action 执行过程中数据信息,包括: 
	(1).request -> HttpServletRequest; (2).session -> HttpSession; (3).application -> ServletContext;
	(4).patameters -> 请求参数;
	(5).attr -> request + session + application 中的数据.
	(6).valueStack -> 业务处理类 *Action 的相关属性.
	
#获取 ServletAPI ---> 解耦方式 + 耦合方式 ///解耦使得 struts2 测试时不需要启动服务器,在一定程度上提高开发效率.
	1.解耦方式
        Map<String, Object> request = (Map) ActionContext.getContext().get("request"); //request
        Map<String, Object> session = ActionContext.getContext().getSession(); //session
        Map<String, Object> application = ActionContext.getContext().getApplication(); //application

        Map<String, Object> parameters = ActionContext.getContext().getParameters(); //parameters		
		String name = ((String[]) parameters.get("name"))[0]; //parameters.name
			
	2.耦合方式
		//1.通过 ActionContext 直接获取
		HttpServletRequest request = (HttpServletRequest) ActionContext.getContext().get(StrutsStatics.HTTP_REQUEST);
		
		//2.实现 ServletRequestAware 接口
		public class LoginAction2 implements ServletRequestAware {
			HttpServletRequest request;
			
			public String execute() { //处理方法
				request.getSession().setAttribute("user", name);
				return "login";
			}

			public void setServletRequest(HttpServletRequest request) { //实现方法
				this.request = request;
			}
		}


//}
 
//{--------<<<数据校验>>>---------------------
#编码验证
	#如果 XAction extends ActionSupport; 那么该 action 将会获得系统提供的默认功能. 如: 验证.
	
	(1).如果执行的是 Action.execute(), 则只会执行 validate().
	(2)...自定义方法 Action.register(), 则会执行 validateRegister() -> validate() -> register().
	
	1.前台jsp
		<%@taglib prefix="s" uri="/struts-tags" %>
		
		用户名: <input type="text" name="user.name" /><s:fielderror fieldName="user.name" /> //验证错误信息的回显

	2.后台Action
		public class RegisterAction extends ActionSupport {
			private int age; //GET/SET省

			public void validateRegister() {
				System.out.println("000 --- validate age"); //验证-注册
				if (age > 100 || age < 1) {
					this.addActionError("年龄不合法");
				}
			}

			public void validate() {
				System.out.println("111 --- validate"); //验证-所有
			}

			public String register() {
				System.out.println("222 --- register"); //逻辑-注册
				return Action.SUCCESS;
			}
		}
	
	3.struts.xml
		<package name="default" extends="struts-default" namespace="/">
			<action name="register" class="cn.x.action.RegisterAction" method="register">
				<result>/index.jsp</result>
				<result name="input">/register.jsp</result> //验证失败
			</action>
		</package>
	
#配置校验
	#在 Action 类的同级目录下新增一个对应的配置文件. RegisterAction -> RegisterAction-validation.xml

	1.前台jsp(省)
	2.后台Action
		public class RegisterAction {
			private String name; //GET/SET省

			public String register() {
				System.out.println("222 --- register"); //逻辑-注册
				return Action.SUCCESS;
			}
		}
		
	3.struts.xml(同上)
	4.RegisterAction—validation.xml
		<?xml version="1.0" encoding="UTF-8"?>
		<!DOCTYPE validators PUBLIC "-//Apache Struts//XWork Validator1.0.3//EN"
			"http://struts.apache.org/dtds/xwork-validator-1.0.3.dtd">
		<validators>
			<field name="name"> //验证-NAME
				<field-validator type="requiredstring"> //类型必须String
					<param name="trim">true</param>
					<message>用户名不能为空</message>
				</field-validator>
				
				<field-validator type="stringlength"> //长度限制, 4<name<10
					<param name="trim">true</param>
					<param name="maxLength">10</param>
					<param name="minLength">4</param>
					<message>用户名长度必须为${minLength}到${maxLength}</message>
				</field-validator>
			</field>
			
			<field name="age">//验证-AGE
				<field-validator type="int"> //1<age<150
					<param name="min">1</param>
					<param name="max">150</param>
					<message>年龄范围为 1~150</message>
				</field-validator>
			</field>
		</validators>
 
//}

//{--------<<<拦截器>>>-----------------------
#概念 -> Struts2 的核心功能都是通过拦截器来实现
	拦截器: //在 action 执行的前后执行,处理一些公共逻辑. 如: 权限控制,日志等.
	拦截器栈: //由多个拦截器组成. 多个拦截器之间的执行采用责任链设计模式来实现.

#拦截器vs过滤器


#自定义
	1.编写拦截器(implements Interceptor || extends AbstractInterceptor)
		public class TimeInterceptor extends AbstractInterceptor {
			@Override
			public String intercept(ActionInvocation invocation) {
				long start = System.currentTimeMillis();
				String result = invocation.invoke(); //执行下一个拦截器,当拦截器执行完后执行 Action
				long end = System.currentTimeMillis();
				System.out.println("执行该 Action 所用时间为: " + (end - start) + "ms");
				return result;
			}
		}

	2.struts.xml
		<package name="default" extends="struts-default" namespace="/">
			<interceptors> //配置拦截器
				<interceptor name="time" class="cn.x.interceptor.TimeInterceptor"/>
			</interceptors>
			
			<action name="hello" class="cn.x.action.HelloAction">
				<result>/index.jsp</result>
				<interceptor-ref name="time"/> //应用拦截器 -> 当请求 hello.action 时将会执行该拦截器
			</action>
		</package>
		
	3.注意点
		(1).当引用了自定义拦截器时,/默认拦截器将不起作用
		
		(2).默认拦截器: 在 struts-default.xml 中,配置了默认拦截器
			当配置默认拦截器以后,如果不引用拦截器,那么默认的拦截器将起作用
			<default-interceptor-ref name="defaultStack"/>
			
		(3).当引用自定义拦截器后,又想使用默认拦截器,那么需要手动引用
			<action name="hello" class="cn.x.action.HelloAction">
				<result>/index.jsp</result>
				
				<interceptor-ref name="time"/> //自定义
				<interceptor-ref name="defaultStack"/> //默认
			</action>
			
		(4).当 action 引用的拦截器个数比较多时,可以将多个拦截器放入一个拦截器栈中
			<interceptor-stack name="myStack"> //定义拦截器栈
				<interceptor-ref name="time"/>
				<interceptor-ref name="defaultStack"/>
			</interceptor-stack>
			
			<action name="hello" class="cn.x.action.HelloAction"> //引用-自定义拦截器栈
				<result>/index.jsp</result>
				<interceptor-ref name="myStack"/>
			</action>
			
		(5).当自定义(拦截器/栈)在这个包下的所有 action 都使用时,可以定义为默认的(拦截器/栈).
			<default-interceptor-ref name="myStack"/> //定义默认的拦截器/栈
		
#登陆拦截器
	1.编写拦截器
		public class LoginInterceptor extends AbstractInterceptor {
			@Override
			public String intercept(ActionInvocation invocation) {
				String actionName = invocation.getProxy().getActionName();
				if ("login".equals(actionName)) { //actionName 没有扩展名 -> 不拦截请求 login
					return invocation.invoke();
				}
				Object obj = invocation.getInvocationContext().getSession().get("user");
				if (obj == null) { //没有登录
					return Action.LOGIN;
				}
				return invocation.invoke();
			}
		}
		
	2.struts.xml		
		<package name="default" extends="struts-default" namespace="/">
			<interceptors>
				<interceptor name="time" class="cn.x.interceptor.TimeInterceptor"/>
				<interceptor name="login" class="cn.x.interceptor.LoginInterceptor"/>

				<interceptor-stack name="myStack"> //定义拦截器栈
					<interceptor-ref name="time"/>
					<interceptor-ref name="login"/>
					<interceptor-ref name="defaultStack"/> //引用默认的拦截器栈
				</interceptor-stack>
			</interceptors>
			
			<default-interceptor-ref name="myStack"/> //定义默认的拦截器/栈
			
			<global-results>
				<result name="login">/login.jsp</result> //全局结果集 -> 先在具体的<action>节点查找,再在此查找
			</global-results>
		
			<action name="hello" class="cn.x.action.HelloAction">
				<result>/WEB-INF/index.jsp</result>
			</action>
			
			<action name="login" class="cn.x.action.LoginAction">
				<result>/success.jsp</result>
			</action>
		</package>

//}

//{--------<<<方法拦截器>>>-------------------
#方法拦截器比 Action 拦截器控制更加细粒度, 主体实现和 Action 拦截器一致.

	1.编写拦截器(extends MethodFilterInterceptor; 重写 doIntercept())

	2.struts.xml //和普通拦截器的区别,只是多配置参数param
		<interceptor-ref name="method">
			<param name="includeMethods">list,add</param> //配置被拦截的方法
			<param name="excludeMethods">login</param>
		</interceptor-ref>

//}

//{--------<<<文件上传>>>---------------------
#常用的组件
	Smartupload: 小巧; Commons-fileupload: 大而全. //(Strts2采用后者)
	
#实现步骤
	1.前台jsp
		(1).<form>表单: POST + enctype="multipart/form-data"
		(2).文件上传域(<file>)必须要有name属性: <input type='file' name="file">
		
	2.后台Action
		public class UploadAction extends ActionSupport {
			private File file; //属性名=表单域名; 即file
			private String fileFileName; //属性名=表单域名+FileName
			private String fileContentType; //属性名=表单域名+ContentType

			public String upload() throws IOException {
				String path = ServletActionContext.getRequest().getRealPath("/upload");
				FileUtils.copyFile(file, new File(path, fileFileName));
				return Action.SUCCESS;
			}
		}
		
	3.struts.xml
		<struts>
			<constant name="struts.multipart.saveDir" value="c:\"/> //设置临时目录
			<constant name="struts.multipart.maxSize" value="20971520"/> //总文件的最大值, struts.multipart.maxSize >= maximumSize
			
			<package name="default" extends="struts-default" namespace="/">
				<action name="upload" class="cn.x.action.UploadAction" method="upload">
					<result>/index.jsp</result>
					
					<interceptor-ref name="fileUpload">
						<param name="maximumSize">20971520</param> //单个文件最大值
					</interceptor-ref>
					
					<interceptor-ref name="defaultStack"/> //当引用了具体的拦截器时,默认拦截器将不起作用
				</action>
			</package>
		</struts>
		
#批量上传
	1.前台jsp
		<script type="text/javascript">
			$(function(){
				$('#btn').click(function(){
					var field="<p><input type='file' name='file'/><input type='button' value='删除' onclick='removed(this);'/></p>";
					$('#files').append(field);
				});
			});
			
			function removed(e){
				$(e).parent().remove();
			}
		</script>

	2.后台Action
		public class BatchUploadAction extends ActionSupport {
			private File[] file;
			private String[] fileFileName;
			private String[] fileContentType;

			public String batchUpload() throws IOException {
				String path = ServletActionContext.getRequest().getRealPath("/upload");
				for(int i=0; i<file.length; i++){
					FileUtils.copyFile(file[i], new File(path, fileFileName[i]));
				}
				return Action.SUCCESS;
			}
		}
	3.struts.xml(同上)
	
#表单批量提交 -> 基础数据类型
	1.前台jsp
		<form action="add.action" method="post">
			用户名:<input type="text" name="name"/>
			<br>
			爱好:<input type="checkbox" name="hobbies" value="football"/>足球
			<input type="checkbox" name="hobbies" value="basketball"/> 蓝球
			<input type="checkbox" name="hobbies" value="pingpang"/> 乒乓球
			<input type="checkbox" name="hobbies" value="yumaoqiu"/> 羽毛球
			<br>
			喜欢游戏:<input type="checkbox" name="games" value="lol"/> 英雄联盟
			<input type="checkbox" name="games" value="dota"/>dota
			<input type="checkbox" name="games" value="war3"/>魔兽争霸
			<input type="checkbox" name="games" value="cs"/>反恐精英
			<br>
			<input type="submit" value=" 提交 "/>
		</form>
		
	2.后台Action
		public class UserAction {
			private String name; //GET/SET省
			private String[] hobbies;
			private List<String> games;

			public String execute() {
				System.out.println("name=" + name);
				System.out.print("hobbies=");
				for (int i = 0; i < hobbies.length; i++) {
					System.out.print(hobbies[i] + " ");
				}
				System.out.println("games=" + games);
				return "success";
			}
		}
		
##表单批量提交 -> 自定义数据类型
	1.前台jsp
		<form action="batch.action" method="post">
			用户名:<input type="text" name="users.name"/>
			年龄:<input type="text" name="users.age">
			<br>
			用户名:<input type="text" name="users.name"/>
			年龄:<input type="text" name="users.age">
			<br>
			用户名:<input type="text" name="users.name"/>
			年龄:<input type="text" name="users.age">
			<br>
			<input type="submit" value=" 提交 "/>
		</form>
		
	2.后台Action
		public class BatchAction {
			private List<User> users; //需要自定义拦截器来实现 -> struts-list.jar
			
			public String execute() {
				for (User u : users) {
					System.out.println(u);
				}
				return "success";
			}
		}
	
	3.struts.xml
		<package name="default" extends="list-default" namespace="/">
			<action name="batch" class="cn.x.action.BatchAction">
				<result>/index.jsp</result>
				
				<interceptor-ref name="listStack"/> //自定义拦截器
			</action>
		</package>

//}

//{--------<<<文件下载>>>---------------------
#servlet 中文件下载是通过流来实现的; struts2也是如此.
	1.前台jsp
		<body>
			<a href="download.action?fileName=Struts2.chm">struts2的文档</a>
			<a href="download.action?fileName=Struts1.3.chm">struts1的文档</a>
		</body>

	2.后台Action
		public class DownloadAction {
			private String fileName;

			public String execute() {
				return Action.SUCCESS;
			}

			public InputStream getInputStream() {
				HttpServletRequest req = ServletActionContext.getRequest();
				String path = req.getRealPath("/download"); //获取路径
				return new FileInputStream(new File(path, fileName));
			}
		}
		
	3.struts.xml
		<action name="download" class="cn.x.action.DownloadAction">
			<result type="stream">
				<param name="inputName">inputStream</param>
				<param name="contentDisposition">attachment;filename=${fileName}</param>
			</result>
		</action>

//}

//{--------<<<ajax>>>-------------------------
#原生Servlet
	1.前台jsp
		$(function(){
			$('#btn').click(function(){
				$.post("ajax.action",function(data){
					$('#msg').html(data);
				});
			});
		});
		
	2.后台Action
		public class AjaxAction {
			public String execute() throws IOException {
				HttpServletResponse res = ServletActionContext.getResponse();
				res.setCharacterEncoding("utf-8");
				res.getWriter().print("struts ajax");
				return null;
			}
		}
	
	3.struts.xml
		<package name="default" extends="list-default" namespace="/">
			<action name="ajax" class="cn.x.action.AjaxAction"></action>
		</package>
		
#struts框架
	0.导入相关的jar包
		ezmorph-1.0.6.jar; struts2-json-plugin-2.2.1.jar; json-lib-2.1-jdk15.jar; ...
	
	1.前台jsp
        $(function () {
            $('#btn').click(function () {
                $.post("json.action", function (data) {
                    var html = "";
                    for (var i = 0; i < data.length; i++) {
                        html += "<tr><td>" + data[i].name + "</td><td>" + data[i].age + "</td> </tr>";
                    }
                    $('#content').html(html);
                }, 'json');
            });
        });
		
		<body>
		<input type="button" id="btn" value=" 获取数据json"/>
		<table width="80%" align="center">
			<tr>
				<td>姓名</td>
				<td>年龄</td>
			</tr>
			<tbody id="content"></tbody>
		</table>
		</body>
	
	2.后台Action
		public class JsonAction {
			private JSONArray jsonArray;

			public String execute() {
				List<User> list = new ArrayList<User>();
				list.add(new User("111", 23));
				list.add(new User("222", 22));
				list.add(new User("333", 21));
				jsonArray = JSONArray.fromObject(list);
				return "success";
			}
		}
		
	3.struts.xml
		<package name="default" extends="json-default" namespace="/">
			<action name="json" class="cn.x.action.JsonAction">
				<result type="json">
					<param name="root">jsonArray</param> //对应属性 jsonArray
				</result>
			</action>
		</package>

//}

//{--------<<<异常处理>>>---------------------
#将程序中的异常信息处理后,以友好的方式提示用户
	0.异常类
		@Slf4j
		public class UserException extends Exception {
			public UserException() {
				super();
			}

			public UserException(String message) {
				super(message);
				log.error("UserException: {}", message); //log
			}
		}

	1.ServiceImpl
		public class UserServiceImpl implements UserService {
			@Override
			public void deleteById(int id) throws UserException {
				if (0 == id) {
					throw new UserException("当前用户不存在!");
				}
				//DAO
			}
		}
	
	2.后台Action
		public class ExceptionAction {
			private UserService userService = new UserServiceImpl();
			
			public String delete() throws UserException {
				userService.deleteById(0);
				return "success";
			}
		}
		
	3.struts.xml
		<global-exception-mappings>
			<exception-mapping result="excPage" exception="cn.x.exception.UserException" /> //全局异常
		</global-exception-mappings>	
		<package name="default" extends="struts-default" namespace="/">
			<action name="delete" class="cn.x.action.ExceptionAction">
				<result>/success.jsp</result>
				<result name="excPage">/error.jsp</result>
			</action>
		</package>

//}

//{--------<<<x>>>----------------------------

//}





