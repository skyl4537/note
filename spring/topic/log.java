
	' TRACE < DEBUG < INFO < WARN < ERROR '
	
0.基础概念
	日志门面(日志的接口) -> 如: slf4j jcl jboss-logging
	日志实现(具体实现类) -> 如: logback log4j log4j2 jul

	#配置文件
		logback.xml: 直接被日志框架加载
		logback-spring.xml: 跳过日志框架,直接被SpringBoot加载,可以使用高级 Profile 功能
		
	#配置多环境
		//在 application.properties 中激活开发环境: spring.profiles.active=dev
		开发环境: application-dev.properties	->	logging.level=DEBUG
		生产环境: application-pro.properties	->	logging.level=INFO
		
		//动态读取 application.properties 中配置的日志级别
		log4j.rootLogger = ${logging.level}, stdout, file
		log4j.logger.com.x.controller = ${logging.level}, ctrl	

1.日志格式
	#通用格式
		%t -> 线程名;   %m->日志主体;   %n->平台换行符;
		%r -> 自应用启动到输出该log信息耗费的毫秒数
		%p -> 日志级别 -> %5p -> 5字符长度,左边补空格
		%d -> 时间及格式 -> %d{yyyy-MMM-dd HH:mm:ss,SSS} -> 2002-10-18 22:10:28,921
		
		//1.不输入: 表示输出完整的<包名>+<类名>
		//2.输入0: 表示只输出<类名>
		//3.输入其他数字: 表示输出小数点最后边点号之前的字符数量
		%c -> %c{length} -> length有三种情况(↑) -> 类全名
	
	#不建议格式(影响效率)
		%l -> 日志发生位置: 包括类目名,发生的线程,以及在代码中的行数
		
	#输出线程id
		slf4j默认不提供线程id输出,不过可利用 'MDC' 特性实现这一目标.
		
		//(1).配置拦截器-->在线程开始的地方加入ThreadId; 同样在线程结束的时候删除该ThreadId
		public class ThreadIdInterceptor implements HandlerInterceptor {
			private final static String THREAD_ID = "ThreadId";

			@Override
			public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
				String ThreadId = java.util.UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
				MDC.put(THREAD_ID, ThreadId); //放ThreadId
				return true;
			}

			@Override
			public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
				// .... 其他逻辑代码

				MDC.remove(THREAD_ID); //删除ThreadId
			}
		}
			
		//(2).使用拦截器
		@Configuration
		public class MyWebMvcConfigurer implements WebMvcConfigurer {
			
			@Override
			public void addInterceptors(InterceptorRegistry registry) {
				//注册拦截器
				registry.addInterceptor(new ThreadIdInterceptor()).addPathPatterns("/**");
			}
		}
		
		//(3).配置logback.xml
		<property name="CONSOLE_PATTERN" //%X{ThreadId} ---> 输出MDC中key的值
			value="%d{yyyy-MM-dd HH:mm:ss.SSS} %5p [%t] %X{ThreadId} %c{0} - %m%n" />
		
		//(4).输出结果
		2018-12-06 20:59:02.427  INFO [http-nio-8090-exec-1] 41C85FC1684A4F37B64BEFC22D288C0C MyAopConfig - the method: com.example.controller.HelloController.asyncTask() begin with: []
		2018-12-06 20:59:04.436  INFO [http-nio-8090-exec-1] 41C85FC1684A4F37B64BEFC22D288C0C HelloController - 2018-12-06 20:59:04.436 -> 50 - http-nio-8090-exec-1 ==>  java.lang.RuntimeException: asyncFuture - / by zero
		2018-12-06 20:59:04.436  INFO [http-nio-8090-exec-1] 41C85FC1684A4F37B64BEFC22D288C0C MyAopConfig - the method: com.example.controller.HelloController.asyncTask() end!! spend(ms): 2010
		2018-12-06 20:59:04.437  INFO [http-nio-8090-exec-1] 41C85FC1684A4F37B64BEFC22D288C0C MyAopConfig - the method: com.example.controller.HelloController.asyncTask() end with: null

		
		
2.日志过滤
	#LevelFilter -> 级别过滤器. 根据配置的过滤级别,选择性的接收或拒绝日志
		/** DENY - 日志将立即被抛弃,不再经过其他过滤器
		  * ACCEPT - 日志会被立即处理,不再经过剩余过滤器。
		  * NEUTRAL - 有序列表里的下一个过滤器会接着处理日志 */
	
		//(1) logback.xml
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level> <!-- 过滤级别error -->
            <onMatch>ACCEPT</onMatch> <!-- 符合过滤级别 -->
            <onMismatch>DENY</onMismatch> <!-- 不符合 -->
        </filter>
		
		//(2) log4j.properties
		log4j.logger.com.x.sm = DEBUG, sm, err //sm包日志级别, 输出路径1, 输出路径2

		log4j.appender.err.filter.a=org.apache.log4j.varia.LevelRangeFilter
		log4j.appender.err.filter.a.LevelMin=ERROR
		log4j.appender.err.filter.a.LevelMax=ERROR
		log4j.appender.err.filter.a.acceptOnMatch=true
		log4j.appender.err=org.apache.log4j.DailyRollingFileAppender
		log4j.appender.err.File=/var/lib/webpark/logs/sm/error
		log4j.appender.err.DatePattern='.'yyyyMMdd'.log'
		log4j.appender.err.layout=org.apache.log4j.PatternLayout
		log4j.appender.err.layout.ConversionPattern=%d{HH:mm:ss.SSS} - %m%n
		
	#ThresholdFilter -> 临界值过滤器,过滤<配置级别,只输出>=
		//(1) logback.xml
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>INFO</level>
        </filter>
		
		//(2) log4j.properties
		log4j.logger.com.x.sm = DEBUG, sm, err

		log4j.appender.err.Threshold=ERROR
		log4j.appender.err=org.apache.log4j.DailyRollingFileAppender
		log4j.appender.err.File=/var/lib/webpark/logs/sm/error
		log4j.appender.err.DatePattern='.'yyyyMMdd'.log'
		log4j.appender.err.layout=org.apache.log4j.PatternLayout
		log4j.appender.err.layout.ConversionPattern=%d{HH:mm:ss.SSS} - %m%n
		
3.异步输出
	//每次输出日志就会发生一次磁盘IO,损耗性能.
	//异步输出,不让此次写日志发生磁盘IO,阻塞日志线程,从而减少不必要的性能损耗.
		<appender name="info" class="ch.qos.logback.core.rolling.RollingFileAppender">
			//... ...
		</appender>

		<!--  异步输出,异步的 appender 节点必须跟在同步后面,否则不起作用  -->
		<appender name="async4info" class="ch.qos.logback.classic.AsyncAppender">
			<!--当BlockingQueue还有20%容量,将丢弃TRACE、DEBUG和INFO级别的event,只保留WARN和ERROR级别的event-->
			<!--为保持所有的events,将该值设置为0. 默认值20 -->
			<discardingThreshold>0</discardingThreshold>
			<!-- BlockingQueue的最大容量,该值影响性能. 默认值256 -->
			<queueSize>256</queueSize>
			<!-- 异步appender并不自己写日志,只是将日志输出到BlockingQueue,最终还是具体的appender将日志输出到文件 -->
			<!-- 图示详见: http://www.importnew.com/27247.html -->
			<appender-ref ref="info"/>
		</appender>
	
4.日志框架切换
	#从logback切换到log4j -> 根据Dependency Hierarchy界面,搜索"logback"找到其父依赖
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-thymeleaf</artifactId>
			<exclusions>
				<exclusion>
					<artifactId>logback-classic</artifactId> //排除'logback'
					<groupId>ch.qos.logback</groupId>
				</exclusion>
				<exclusion>
					<artifactId>log4j-over-slf4j</artifactId> //排除'log4j适应slf4j的替换包'
					<groupId>org.slf4j</groupId>
				</exclusion>
			</exclusions>
		</dependency>

		<dependency> //引入'log4j'
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-log4j</artifactId>
		</dependency>
	
	#从logback切换到log4j2
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
		<dependency>
			<groupId>org.springframework.boot</groupId>	//引入'log4j2'包
			<artifactId>spring-boot-starter-log4j2</artifactId>
		</dependency>	
		
4.动态修改日志级别
	http://127.0.0.1:8090/demo/actuator/loggers //所有模块的日志级别
	
	http://127.0.0.1:8090/demo/actuator/loggers/com.example.controller //具体模块的日志级别
	
	//发送 'POST' 请求到以上路径, 动态修改以上模块的日志级别为 'DEBUG', 成功状态码为 '204'
	POST - 请求体: {"configuredLevel": "DEBUG"} - Content-Type: application/json
	
	
	
///-----------------------<<<demo-logback>>>----------------------------------
0.logback和log4j
	<?xml version="1.0" encoding="UTF-8"?>

	<!-- scan: 配置文件如果发生改变,是否被重新加载,默认值为 true -->
	<!-- scanPeriod: 监测配置文件是否改变的频率(scan为true时才生效),默认1分钟; 如未给出时间单位,默认毫秒 -->
	<!-- debug: 是否打印logback内部日志信息,实时查看logback运行状态. 默认值为 false -->
	<configuration scan="true" scanPeriod="60 seconds" debug="false">

		<!-- 设置上下文, 一旦设置，不能修改,可以通过 %contextName 在日志中输出上下文对应的值 -->
		<!-- <contextName>logback</contextName> <property name="FILE_PATTERN" value="%d{HH:mm:ss.SSS}
			%contextName [%5level] %logger{5} - %msg%n" /> -->

		<!-- 加载外部的yml配置文件. (文件名不能使用 logback.xml,加载太早,必须改为 logback-spring.xml) -->
		<!-- scope固定值,用${}取值; name配置文件中属性对应的变量名; source配置文件中属性，defaultValue为缺省值 -->
		<!--<springProperty scope="context" name="LOG_HOME"
			source="aopAll.home" defaultValue="blues/logs" />-->

		<!-- 设置变量 -->
		<!--<property name="LOG_HOME" value="${LOG_HOME}" />--><!--对应上面的加载外部配置文件-->
		<!--<property name="APP_NAME" value="blue"/>-->
		<!--<property name="LOG_HOME" value="${APP_NAME}/logs"/>-->

		<property name="LOG_HOME" value="logs"/>
		<property name="CONSOLE_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} %5p [%t] %c{0} - %m%n"/>
		<property name="FILE_PATTERN" value="%d{HH:mm:ss.SSS} [%5p] %c{0} - %msg%n"/>

		<!-- error -->
		<!-- 滚动记录文件: 先将日志记录到指定文件,当符合某个条件时,再将日志归档到指定文件 -->
		<appender name="error" class="ch.qos.logback.core.rolling.RollingFileAppender">

			<!-- 可选节点,归档前过渡文件的路径,不指定则直接写入归档后的文件路径 -->
			<file>${LOG_HOME}/error/error</file>

			<!-- 按照'大小和时间'两种策略综合滚动 -->
			<rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
				<!-- 必选节点,归档后文件路径 -->
				<!-- 支持.zip和.gz压缩模式,(单个文件超过最大容量才会压缩,后缀名改为.zip即可压缩) -->
				<!-- 每小时一归档: 当大小超过maxFileSize时,按照 i 进行文件滚动 -->
				<fileNamePattern>${LOG_HOME}/error/error_%d{yyyyMMdd_HH}_%i.zip</fileNamePattern>

				<!-- 单个日志文件最大5MB, 最多保存5个小时的日志, 总日志大小不能超过5MB -->
				<!-- 当满足 MaxHistory 或 totalSizeCap 其中一个时,自动删除旧的日志 -->
				<maxFileSize>1MB</maxFileSize>
				<MaxHistory>5</MaxHistory>
				<totalSizeCap>5MB</totalSizeCap>
			</rollingPolicy>

			<encoder><!-- 日志输出格式 -->
				<pattern>${FILE_PATTERN}</pattern>
			</encoder>

			<!-- ThresholdFilter: 临界值过滤器,过滤掉低于指定临界值的日志(只输出等于或高于临界值的日志). -->
			<!-- LevelFilter: 级别过滤器. 等于配置级别,根据onMath和onMismatch接收或拒绝日志 -->
			<filter class="ch.qos.logback.classic.filter.LevelFilter">
				<level>ERROR</level> <!-- 过滤级别 -->
				<onMatch>ACCEPT</onMatch> <!-- 符合过滤级别 -->
				<onMismatch>DENY</onMismatch> <!-- 不符合 -->
			</filter>
		</appender>

		<!-- info -->
		<appender name="info" class="ch.qos.logback.core.rolling.RollingFileAppender">
			<file>${LOG_HOME}/info</file>
			<rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
				<fileNamePattern>${LOG_HOME}/info_%d{yyyyMMdd_HH}_%i.log</fileNamePattern>
				<maxFileSize>1MB</maxFileSize>
				<MaxHistory>5</MaxHistory>
				<totalSizeCap>5MB</totalSizeCap>
			</rollingPolicy>
			<encoder>
				<pattern>${FILE_PATTERN}</pattern>
			</encoder>
		</appender>
		
		<!--  异步输出,异步的 appender 节点必须跟在同步后面,否则不起作用  -->
		<appender name="async4info" class="ch.qos.logback.classic.AsyncAppender">
			<!--当BlockingQueue还有20%容量,将丢弃TRACE、DEBUG和INFO级别的event,只保留WARN和ERROR级别的event-->
			<!--为保持所有的events,将该值设置为0. 默认值20 -->
			<discardingThreshold>0</discardingThreshold>
			<!-- BlockingQueue的最大容量,该值影响性能. 默认值256 -->
			<queueSize>256</queueSize>
			<!-- 异步appender并不自己写日志,只是将日志输出到BlockingQueue,最终还是具体的appender将日志输出到文件 -->
			<!-- 图示详见: http://www.importnew.com/27247.html -->
			<appender-ref ref="info"/>
		</appender>

		<!-- config -->
		<appender name="config" class="ch.qos.logback.core.rolling.RollingFileAppender">
			<file>${LOG_HOME}/config/config</file>
			<rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
				<fileNamePattern>${LOG_HOME}/config/config_%d{yyyyMMdd_HH}_%i.log</fileNamePattern>
				<maxFileSize>1MB</maxFileSize>
				<MaxHistory>5</MaxHistory>
				<totalSizeCap>5MB</totalSizeCap>
			</rollingPolicy>
			<encoder>
				<pattern>${FILE_PATTERN}</pattern>
			</encoder>
		</appender>

		<!-- ctrl4all -->
		<appender name="ctrl4all" class="ch.qos.logback.core.rolling.RollingFileAppender">
			<file>${LOG_HOME}/ctrl/ctrl</file>
			<rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
				<fileNamePattern>${LOG_HOME}/ctrl/ctrl_%d{yyyyMMdd_HH}_%i.log</fileNamePattern>
				<maxFileSize>1MB</maxFileSize>
				<MaxHistory>5</MaxHistory>
				<totalSizeCap>5MB</totalSizeCap>
			</rollingPolicy>
			<encoder>
				<pattern>${FILE_PATTERN}</pattern>
			</encoder>
		</appender>

		<!-- ctrl4warn -->
		<appender name="ctrl4warn" class="ch.qos.logback.core.rolling.RollingFileAppender">
			<file>${LOG_HOME}/ctrl/warn/warn</file>
			<rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
				<fileNamePattern>${LOG_HOME}/ctrl/warn/warn_%d{yyyyMMdd}_%i.log</fileNamePattern>
				<maxFileSize>1MB</maxFileSize>
				<MaxHistory>5</MaxHistory>
				<totalSizeCap>5MB</totalSizeCap>
			</rollingPolicy>
			<encoder>
				<pattern>${FILE_PATTERN}</pattern>
			</encoder>
			<filter class="ch.qos.logback.classic.filter.LevelFilter">
				<level>WARN</level>
				<onMatch>ACCEPT</onMatch>
				<onMismatch>DENY</onMismatch>
			</filter>
		</appender>

		<!-- ctrl4err -->
		<appender name="ctrl4err" class="ch.qos.logback.core.rolling.RollingFileAppender">
			<file>${LOG_HOME}/ctrl/err/err</file>
			<rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
				<fileNamePattern>${LOG_HOME}/ctrl/err/err_%d{yyyyMMdd}_%i.log</fileNamePattern>
				<maxFileSize>1MB</maxFileSize>
				<MaxHistory>5</MaxHistory>
				<totalSizeCap>5MB</totalSizeCap>
			</rollingPolicy>
			<encoder>
				<pattern>${FILE_PATTERN}</pattern>
			</encoder>
			<filter class="ch.qos.logback.classic.filter.LevelFilter">
				<level>ERROR</level>
				<onMatch>ACCEPT</onMatch>
				<onMismatch>DENY</onMismatch>
			</filter>
		</appender>

		<!-- 控制台 -->
		<appender name="console" class="ch.qos.logback.core.ConsoleAppender">
			<encoder>
				<pattern>${CONSOLE_PATTERN}</pattern>
			</encoder>
		</appender>

		<!-- 必选节点; 特殊的logger元素,用来指定最基础的日志输出级别 -->
		<root level="info" additivity="true">
			<appender-ref ref="console"/>
			<appender-ref ref="async4info"/>
			<appender-ref ref="error"/>
		</root>

		<!-- 可选节点; 设置某一个包或具体某一个类的日志打印级别,以及指定<appender> (覆盖root节点的输出级别) -->
		<!-- name: 受此logger约束的某一个包或具体某一个类 -->
		<!-- level: 日志级别, 默认继承上级的打级别 -->
		<!-- additivity: 是否向上级logger传递打印信息. 默认是true -->
		<logger name="com.example.demo.config" level="debug" additivity="true">
			<appender-ref ref="config"/>
		</logger>

		<logger name="com.example.demo.controller" level="debug"> //ctrl包按照日志级别分别输出到三个文件
			<appender-ref ref="ctrl4all"/> //输出 INFO 级别以上的日志
			<appender-ref ref="ctrl4warn"/> //只输出 WARN 级别
			<appender-ref ref="ctrl4err"/> //只输出 ERROR 级别
		</logger>

		<logger name="com.example.demo.mapper" level="debug">//mapper包也输出到 ctrl4all
			<appender-ref ref="ctrl4all"/>
		</logger>
	</configuration>
	
///-----------------------<<<demo-log4j>>>----------------------------------
	
	#系统 -> 配置 -> (基础-级别, 控制台, 文件1, 文件2)
	#log4j.rootCategory=INFO, stdout, info, error //rootCategory已过时
	log4j.rootLogger=INFO, stdout, info, error

	#系统 -> INFO -> 控制台
	log4j.appender.stdout=org.apache.log4j.ConsoleAppender
	log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
	log4j.appender.stdout.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss.SSS} %p - %m%n

	#系统 -> INFO -> 文件1
	log4j.appender.info=org.apache.log4j.DailyRollingFileAppender
	log4j.appender.info.file=/logs/info
	log4j.appender.info.DatePattern='_'yyyyMMdd'.log'
	log4j.appender.info.layout=org.apache.log4j.PatternLayout
	log4j.appender.info.layout.ConversionPattern=%d{HH:mm:ss.SSS} %p - %m%n

	#系统 -> ERROR -> 文件2
	log4j.appender.error.Threshold=ERROR //输出 ERROR 以上的日志
	log4j.appender.error=org.apache.log4j.DailyRollingFileAppender
	log4j.appender.error.file=/logs/error
	log4j.appender.error.DatePattern='_'yyyyMMdd'.log'
	log4j.appender.error.layout=org.apache.log4j.PatternLayout
	log4j.appender.error.layout.ConversionPattern=%d{HH:mm:ss.SSS} %p - %m%n

	#ctrl -> 配置 -> (包-级别, 文件1, 文件2)
	log4j.logger.com.example.blue.controller=INFO, ctrl, ctrl4warn

	#ctrl -> INFO -> 文件1
	log4j.appender.ctrl=org.apache.log4j.DailyRollingFileAppender
	log4j.appender.ctrl.File=/logs/ctrl/ctrl
	log4j.appender.ctrl.DatePattern='_'yyyyMMdd_HH'.log'
	log4j.appender.ctrl.layout=org.apache.log4j.PatternLayout
	log4j.appender.ctrl.layout.ConversionPattern=%d{HH:mm:ss.SSS} %p - %m%n
	
	#ctrl -> ERROR -> 文件2
	log4j.appender.ctrl4warn.filter.a=org.apache.log4j.varia.LevelRangeFilter
	log4j.appender.ctrl4warn.filter.a.LevelMin=WARN
	log4j.appender.ctrl4warn.filter.a.LevelMax=WARN
	log4j.appender.ctrl4warn.filter.a.acceptOnMatch=true //只输出 WARN 级别日志
	log4j.appender.ctrl4warn=org.apache.log4j.DailyRollingFileAppender
	log4j.appender.ctrl4warn.File=/var/lib/webpark/logs/sm/warn
	log4j.appender.ctrl4warn.DatePattern='.'yyyyMMdd'.log'
	log4j.appender.ctrl4warn.layout=org.apache.log4j.PatternLayout
	log4j.appender.ctrl4warn.layout.ConversionPattern=%d{HH:mm:ss.SSS} - %m%n

	#config -> 配置 -> (包-级别, 文件1)
	log4j.logger.com.example.blue.config=INFO, config
	
	#ctrl -> INFO -> 文件1
	log4j.appender.config=org.apache.log4j.DailyRollingFileAppender
	log4j.appender.config.File=/logs/config/config
	log4j.appender.config.DatePattern='_'yyyyMMdd'.log'
	log4j.appender.config.layout=org.apache.log4j.PatternLayout
	log4j.appender.config.layout.ConversionPattern=%d{HH:mm:ss.SSS} %p - %m%n

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	