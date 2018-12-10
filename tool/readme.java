

#idea plus
	插件离线安装: settings -> plugins -> install plugin from disk -> 选择*.zip
	
	lombok: //减少重复性Bean代码, Getter/Setter/toString()
	alibaba: //代码质量检测,国产版 SonarQube 
	MybatisPlugin: //自动在mybatis.xml生成部分代码
	MavenRunHelper: //依赖冲突可视化解决



















#JSON-Handle ///Chrome插件
	浏览器输入: chrome://extensions/
	将下载后的文件拖入浏览器即可

#cmder	///cmd升级版
	下载: http://cmder.net/	(mini与full版: 差别在于有没有内建msysgit工具)
	
	右键菜单: '配置系统环境变量,然后使用系统cmd执行命令: Cmder.exe /register ALL'
	中文乱码: 'settings -> Environment -> 添加: set LANG=zh_CN.UTF-8'
	
#lombok
	///减少很多重复代码的书写. 比如:getter/setter/toString等
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>		

	0.eclipse
		下载: https://projectlombok.org/download
		将 lombok.jar 放在eclipse安装目录下,与 eclipse/sts.ini 同级
		当前目录打开cmder, 使用命令: "java -jar ./lombok.jar",弹框选择 Install/Update
		成功标识: sts.ini最后一行, -javaagent:F:\sts-bundle\sts-3.9.3.RELEASE\lombok.jar
	
	1.idea
		下载: https://github.com/mplushnikov/lombok-intellij-plugin/releases
		Settings-Plugins-'install from disk',选择下载的*.zip.即可安装成功	
		
	2.常用注解
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
	




















///--------<<<<<<Maven>>>>>>---------------------------------------------------------------------
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
		///(0).配置mvn
		<server> 
		  <id>thirdparty</id>
		  <username>admin</username>
		  <password>admin123</password>
		</server>
		
		///(1).使用cmd命令将第三方包加入私服
		mvn deploy:deploy-file -DgroupId=com.bluecard -DartifactId=wxpay-sdk-0.0.3 -Dversion=0.0.3 -Dpackaging=jar -Dfile=G:\wxpay-sdk-0.0.3.jar -Durl=http://192.168.102.20:8081/nexus/content/repositories/thirdparty/ -DrepositoryId=thirdparty
		//其中, -DgroupId="随意指定"; -DartifactId="第三方jar包名"; -Dversion="版本号"; -Dfile="jar所在本地路径"; -Durl="私服路径"

		///(2).项目pom文件添加引用
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



///-----------<<<SonarQube>>>---------------------------------------------------------------------	
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
		if (flag0) { if (flag1) { // dosth... } }
		if (flag0 && flag1) { // dosth... } //修改后
		
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
