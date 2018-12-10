1.jar war ear	
	jar: 包含class、properties文件，是文件封装的最小单元。包含Java类的普通库、资源(resources)、辅助文件(auxiliary files)等
	war: Servlet、JSP页面、JSP标记库、JAR库文件、HTML/XML文档和其他公用资源文件，如图片、音频文件等
	ear: 包括整个项目，内含多个 EJB Module（JAR文件）和 Web Module（WAR文件）
	
	jar: 通常是开发时要引用通用(JAVA)类，打成包便于存放管理；
	war: 是做好一个(web)应用后，通常是网站，打成包部署到容器中；
	ear: 企业级应用，实际上EAR包中包含WAR包和几个企业级项目的配置文件。
	
	注意: 将web项目以war的方式导出后，直接放在tomcat容器的webapps下，启动tomcat服务，即可运行该项目。
		该war包会自动解压出一个同名的文件夹。

2.eclipse项目有红叉但里面没错
	打开'Problems'视图: Description	Resource Path Location Type Java compiler level does not match the version of 
	'解决方案:'
		windows-proferences-java–compiler设置jdk为1.8
		windows-proferences-java–Installed JREs设置jdk为1.8
		点击项目右键–properties-java Compiler 设置jdk为1.8
		点击项目右键-properties-eclipse-project Facets设置java为1.8
		点击项目右键–properties-eclipse-project Facets设置Dynamic Web Module 为3.1(这是你在web.xml配置的servlet版本)
		点击项目右键–Maven-update project 错误应该可以消除
		
3.sp项目配置
	//必须为元素类型"insert" 声明属性 "resultType"
	删除xml文件中<insert/>, <delete/>等标签中的resultType属性
		
	//Consider defining a bean of type "com.x.x.service.PersonService" in your configuration
	(1)类 PersonServiceImpl 是否添加 @Service 注解; 
	(2)sp项目启动类是否添加 @MapperScan(value = "com.x.x.mapper")
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
#Eclipse卡在"Initializing Java Tools 1%...."
	删除当前工作目录下的WORKSPACE/.metadata/.plugins/org.eclipse.core.resources/.project, 然后重新启动Eclipse
	
#Failed to execute goal org.apache.maven.plugins:maven-surefire-plugin:2.12.4:test (default-test) on
	//项目打包时,出现这个问题: 可能是测试类和maven-surefire-plugin:2.12.4:test (default-test)出现了冲突
	
		@Ignore //测试方法增加此注解; 这个注解的意思是忽略掉当前的测试类
		@Test
		public void testCreeper() throws Exception {
			ImoocPageProcessor imoocPageProcessor = new ImoocPageProcessor();
			imoocPageProcessor.creeper();
		}	
	
#Invalid bound statement (not found): com.example.blue.mapper.PersonMapper.getOneById
	原因: 新版 IntelliJ IDEA 不再编译source folder下的xml文件.
	解决: 在maven的pom.xml中build标签下插入以下片段, 手工将src下所有xml文件引入编译!
	
			<resources>
			  <resource>
				<directory>src/main/java</directory>
				<includes>
				  <include>**/ *.xml</include> //去掉空格
				</includes>
			  </resource>
			</resources>

#Parameter Maps collection does not contain value for java.lang.Integer	
	检查mybatis的xml配置, 肯定在某处配错 "parameterType / resultType" --> "parameterMap / resultMap"
	
#No constructor found in com.example.demo.entity.Person matching [java.lang.Integer, java.lang.Integer, java.lang.String]] with root cause
	javabean中加上默认的无参构造函数
	
	
	
	
	
	
	
	
	
	
	
	
	