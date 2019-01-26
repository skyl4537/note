
	'TRACE < DEBUG < INFO < WARN < ERROR'

//{--------<<<x>>>------------------------------------------------------------------------

//}

//{--------<<<概念>>>---------------------------------------------------------------------
#基础概念
	日志门面 ---> 日志的接口 //如: slf4j jcl jboss-logging
	日志实现 ---> 具体实现类 //如: logback log4j log4j2 jul

#配置文件
	logback.xml: 直接被日志框架加载
	logback-spring.xml: 跳过日志框架,直接被SpringBoot加载,可以使用高级特性 Profile
	
#配置多环境
	//application.properties 中激活开发环境: spring.profiles.active=dev
	开发环境: application-dev.properties ---> logging.level=DEBUG
	生产环境: application-pro.properties ---> logging.level=INFO
	
	//动态读取 application.properties 中配置的日志级别
	log4j.rootLogger = ${logging.level}, stdout, file
	log4j.logger.com.x.controller = ${logging.level}, ctrl	

//}
	
//{--------<<<格式>>>---------------------------------------------------------------------
#log4j格式
	%t	-> 线程名   
	%m	-> 日志主体
	%n	-> 平台换行符
	%r	-> 自应用启动到输出该log信息耗费的毫秒数
	%p	-> 日志级别 //{%-5p} --> 5字符长度,左边补空格
	%d	-> 时间及格式 //%d{yyyy-MMM-dd HH:mm:ss,SSS} --> 2002-10-18 22:10:28,921

#不建议使用,影响效率
	//a.不输入:		表示输出完整的<包名>+<类名>
	//b.输入0:		表示只输出<类名>
	//c.任意数字:	表示输出小数点最后边点号之前的字符数量
	%c	-> %clength} -> length有三种情况(↑) -> 类全名
	
	%l -> 日志发生位置: 包括类目名,发生的线程,以及在代码中的行数
	
#输出线程id
	///slf4j默认不提供线程id输出,不过可利用 'MDC' 特性实现

	1.配置拦截器 //在线程开始时加入 ThreadId; 在线程结束时删除 ThreadId
		public class ThreadIdInterceptor implements HandlerInterceptor {
			private final static String THREAD_ID = "ThreadId";

			@Override
			public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
				String ThreadId = java.util.UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
				MDC.put(THREAD_ID, ThreadId); //加入 ThreadId
				return true;
			}

			@Override
			public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
				// .... 其他逻辑代码

				MDC.remove(THREAD_ID); //删除 ThreadId
			}
		}
		
	2.注册拦截器
		@Configuration
		public class MyWebMvcConfigurer implements WebMvcConfigurer {
			@Override
			public void addInterceptors(InterceptorRegistry registry) {
				registry.addInterceptor(new ThreadIdInterceptor()).addPathPatterns("/**");
			}
		}
	
	3.logback.xml
		<property name="CONSOLE_PATTERN" //%X{ThreadId} ---> 输出MDC中key的值
			value="%d{yyyy-MM-dd HH:mm:ss.SSS} %5p [%t] %X{ThreadId} %c{0} - %m%n" />
	
	4.输出结果
		2018-12-06 20:59:02.427  INFO [http-nio-8090-exec-1] 41C85FC1684A4F37B64BEFC22D288C0C MyAopConfig - the method: com.example.controller.HelloController.asyncTask() begin with: []
		2018-12-06 20:59:04.436  INFO [http-nio-8090-exec-1] 41C85FC1684A4F37B64BEFC22D288C0C HelloController - 2018-12-06 20:59:04.436 -> 50 - http-nio-8090-exec-1 ==>  java.lang.RuntimeException: asyncFuture - / by zero
		2018-12-06 20:59:04.436  INFO [http-nio-8090-exec-1] 41C85FC1684A4F37B64BEFC22D288C0C MyAopConfig - the method: com.example.controller.HelloController.asyncTask() end!! spend(ms): 2010
		2018-12-06 20:59:04.437  INFO [http-nio-8090-exec-1] 41C85FC1684A4F37B64BEFC22D288C0C MyAopConfig - the method: com.example.controller.HelloController.asyncTask() end with: null

//}

//{--------<<<过滤>>>---------------------------------------------------------------------
#LevelFilter -> 级别过滤器. 根据配置的过滤级别,选择性的接收或拒绝日志
	//DENY		-> 日志将立即被抛弃,不再经过其他过滤器
	//ACCEPT	-> 日志会被立即处理,..................
	//NEUTRAL	-> 有序列表里的下一个过滤器会接着处理日志

	1.logback.xml
		<filter class="ch.qos.logback.classic.filter.LevelFilter"> //过滤ERROR
			<level>ERROR</level>
			<onMatch>ACCEPT</onMatch>
			<onMismatch>DENY</onMismatch>
		</filter>
	
	2.log4j.properties
		log4j.logger.com.x.sm = DEBUG, sm, err //sm包日志级别, 输出路径1, 输出路径2

		log4j.appender.err.filter.a=org.apache.log4j.varia.LevelRangeFilter //过滤ERROR
		log4j.appender.err.filter.a.LevelMin=ERROR
		log4j.appender.err.filter.a.LevelMax=ERROR
		log4j.appender.err.filter.a.acceptOnMatch=true
		log4j.appender.err=org.apache.log4j.DailyRollingFileAppender
		log4j.appender.err.File=/var/lib/X/logs/sm/error
		log4j.appender.err.DatePattern='.'yyyyMMdd'.log'
		log4j.appender.err.layout=org.apache.log4j.PatternLayout
		log4j.appender.err.layout.ConversionPattern=%d{HH:mm:ss.SSS} - %m%n
		
#ThresholdFilter -> 临界值过滤器,过滤 <配置级别, 只输出 >=
	1.logback.xml
		<filter class="ch.qos.logback.classic.filter.ThresholdFilter">
			<level>INFO</level>
		</filter>
	
	2.log4j.properties
		log4j.logger.com.x.sm = DEBUG, sm, err

		log4j.appender.err.Threshold=ERROR
		log4j.appender.err=org.apache.log4j.DailyRollingFileAppender
		log4j.appender.err.File=/var/lib/webpark/logs/sm/error
		log4j.appender.err.DatePattern='.'yyyyMMdd'.log'
		log4j.appender.err.layout=org.apache.log4j.PatternLayout
		log4j.appender.err.layout.ConversionPattern=%d{HH:mm:ss.SSS} - %m%n

//}

//{--------<<<异步>>>---------------------------------------------------------------------
#每次输出日志就会发生一次磁盘IO,损耗性能.
#异步输出,不让此次写日志发生磁盘IO,阻塞日志线程,从而减少不必要的性能损耗.

		//同步appender
		<appender name="info" class="ch.qos.logback.core.rolling.RollingFileAppender">
			//... ...
		</appender>

		//异步appender 必须跟在同步后面,否则不起作用
		<appender name="async4info" class="ch.qos.logback.classic.AsyncAppender">

			//当 BlockingQueue 还有20%容量,将丢弃 TRACE、DEBUG和INFO级别的event,只保留WARN和ERROR级别的event
			//为保持所有的events,将该值设置为0. 默认值20
			<discardingThreshold>0</discardingThreshold>
			
			<queueSize>256</queueSize> //BlockingQueue 的最大容量,该值影响性能. 默认值256
			
			//异步appender并不自己写日志,只是将日志输出到 BlockingQueue,最终还是具体的appender将日志输出到文件
			//图示详见: http://www.importnew.com/27247.html
			<appender-ref ref="info"/>
		</appender>

//}
	
//{--------<<<切换>>>---------------------------------------------------------------------
#logback -> log4j
		
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
            <exclusions>
                <exclusion> ///根据 Dependency Hierarchy 界面,搜索"logback"找到其父依赖
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-logging</artifactId> //排除boot自带'logging'
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId> //引入'log4j'
            <artifactId>spring-boot-starter-log4j</artifactId>
            <version>1.3.8.RELEASE</version>
        </dependency>

#logback -> log4j2

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
			<exclusions>
				<exclusion>
					<groupId>org.springframework.boot</groupId> //排除boot自带'logging'
					<artifactId>spring-boot-starter-logging</artifactId>
				</exclusion>
			</exclusions>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>	//引入'log4j2'包
			<artifactId>spring-boot-starter-log4j2</artifactId>
		</dependency>

//}	

//{--------<<<动态修改日志级别>>>---------------------------------------------------------
#SpringBoot.Actuator

	http://127.0.0.1:8090/demo/actuator/loggers //所有模块的日志级别
	
	http://127.0.0.1:8090/demo/actuator/loggers/com.example.controller //具体模块的日志级别
	
	//发送 'POST' 请求到以上路径, 动态修改以上模块的日志级别为 'DEBUG', 成功状态码为 '204'
	POST - 请求体: {"configuredLevel": "DEBUG"} - Content-Type: application/json
	
//}

//{--------<<<demo-logback>>>-------------------------------------------------------------
		<?xml version="1.0" encoding="UTF-8"?>

		//<!-- scan: 配置文件发生改变,是否重新加载,默认 true --><!-- scanPeriod: 监测配置文件是否改变的频率(scan为true时才生效),默认1分钟; 如未给出时间单位,默认毫秒 --><!-- debug: 是否打印logback内部日志信息,实时查看logback运行状态. 默认 false -->
		<configuration scan="true" scanPeriod="60 seconds" debug="false">

			//<!-- 设置上下文, 一旦设置，不能修改,可以通过 %contextName 在日志中输出上下文对应的值 -->
			//<!-- <contextName>logback</contextName> 
			//	 <property name="PATTERN_FILE" value="%d{HH:mm:ss.SSS} %contextName [%5level] %logger{5} - %m%n" /> -->

			//<!-- 加载外部的yml配置文件. (文件名不能使用 logback.xml,加载太早,必须改为 logback-spring.xml) -->
			//<!-- scope固定值,用${}取值; name配置文件中属性对应的变量名; source配置文件中属性，defaultValue为缺省值 -->
			//<!-- <springProperty scope="context" name="LOG_HOME" source="aopAll.home" defaultValue="blues/logs" />-->

			//<!-- 设置变量 -->
			//<!-- <property name="LOG_HOME" value="${LOG_HOME}" />--> //<!-- 对应上面的加载外部配置文件-->
			//<!-- <property name="APP_NAME" value="blue"/>-->
			//<!-- <property name="LOG_HOME" value="${APP_NAME}/logs"/>-->

			<property name="LOG_HOME" value="logs" />
			<property name="PATTERN_CONSOLE" value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%5p] [%t] - %m%n" />
			<property name="PATTERN_FILE" value="%d{HH:mm:ss.SSS} [%5p] [%t] - %m%n" />

			//<!-- CTRL_DEBUG -->
			//<!-- 滚动记录文件: 先将日志记录到临时文件,当符合某个条件时,再将日志归档到目标文件 -->
			<appender name="CTRL_DEBUG" class="ch.qos.logback.core.rolling.RollingFileAppender">

				//<!-- 可选节点,归档前临时文件的路径,不指定则直接写入归档后的目标文件 -->
				<file>${LOG_HOME}/ctrl/debug/debug</file>

				//<!-- 按照'大小和时间'两种策略综合滚动 -->
				<rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
					//<!-- 必选节点,归档后文件路径 -->
					//<!-- 支持.zip和.gz压缩模式,(单个文件超过最大容量才会压缩,后缀名改为.zip即可压缩) -->
					//<!-- 每小时一归档: 当大小超过 maxFileSize 时,按照 i 进行文件归档 -->
					<fileNamePattern>${LOG_HOME}/ctrl/debug/debug_%d{yyyyMMdd_HH}_%i.zip</fileNamePattern>

					//<!-- 单个日志文件最大1MB, 最多保存5个小时的日志, 总日志大小不能超过5MB -->
					//<!-- 当 MaxHistory 或 totalSizeCap 都满足时,自动删除旧的日志 -->
					<maxFileSize>1MB</maxFileSize>
					<MaxHistory>5</MaxHistory>
					<totalSizeCap>5MB</totalSizeCap>
				</rollingPolicy>

				<encoder>  //<!-- 日志输出格式 -->
					<pattern>${PATTERN_FILE}</pattern>
				</encoder>
			</appender>

			//<!--  异步appender 必须跟在同步后面,否则不起作用  -->
			<appender name="CTRL_DEBUG_ASYNC" class="ch.qos.logback.classic.AsyncAppender">
				//<!--当 BlockingQueue 还有20%容量,将丢弃 TRACE,DEBUG,INFO 级别的event,只保留 WARN和ERROR 级别的event-->
				//<!--为保持所有的events,将该值设置为0. 默认值20 -->
				<discardingThreshold>0</discardingThreshold>
				//<!-- BlockingQueue 的最大容量,该值影响性能. 默认值256 -->
				<queueSize>256</queueSize>
				//<!-- 异步appender并不自己写日志,只是将日志输出到 BlockingQueue,最终还是具体的appender将日志输出到文件 -->
				//<!-- 图示详见: http:www.importnew.com/27247.html -->
				<appender-ref ref="CTRL_DEBUG" />
			</appender>

			//<!-- CTRL_WARN -->
			<appender name="CTRL_WARN" class="ch.qos.logback.core.rolling.RollingFileAppender">
				<file>${LOG_HOME}/ctrl/warn/warn</file>
				<rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
					<fileNamePattern>${LOG_HOME}/ctrl/warn/warn_%d{yyyyMMdd_HH}_%i.log</fileNamePattern>
					<maxFileSize>1MB</maxFileSize>
					<MaxHistory>5</MaxHistory>
					<totalSizeCap>5MB</totalSizeCap>
				</rollingPolicy>
				<encoder>
					<pattern>${PATTERN_FILE}</pattern>
				</encoder>

				//<!-- LevelFilter: 级别过滤器. 等于配置级别,根据 onMath 和 onMismatch 接收或拒绝日志 -->
				//<!-- ThresholdFilter: 临界值过滤器,过滤掉低于指定临界值的日志(只输出等于或高于临界值的日志). -->
				<filter class="ch.qos.logback.classic.filter.LevelFilter">
					<level>WARN</level>  //<!-- ONLY WARN -->
					<onMatch>ACCEPT</onMatch>
					<onMismatch>DENY</onMismatch>
				</filter>
			</appender>

			//<!-- CTRL_ERROR -->
			<appender name="CTRL_ERROR" class="ch.qos.logback.core.rolling.RollingFileAppender">
				<file>${LOG_HOME}/ctrl/error/error</file>
				<rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
					<fileNamePattern>${LOG_HOME}/ctrl/error/error_%d{yyyyMMdd_HH}_%i.log</fileNamePattern>
					<maxFileSize>1MB</maxFileSize>
					<MaxHistory>5</MaxHistory>
					<totalSizeCap>5MB</totalSizeCap>
				</rollingPolicy>
				<encoder>
					<pattern>${PATTERN_FILE}</pattern>
				</encoder>
				<filter class="ch.qos.logback.classic.filter.ThresholdFilter">
					<level>ERROR</level>  //<!-- ERROR+ -->
				</filter>
			</appender>

			//<!-- 控制台 -->
			<appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
				<encoder>
					<pattern>${PATTERN_CONSOLE}</pattern>
				</encoder>
			</appender>

			//<!-- LOGFILE -->
			<appender name="LOGFILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
				<file>${LOG_HOME}/log</file>
				<rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
					<fileNamePattern>${LOG_HOME}/log_%d{yyyyMMdd}_%i.log</fileNamePattern>
					<maxFileSize>1MB</maxFileSize>
					<MaxHistory>5</MaxHistory>
					<totalSizeCap>5MB</totalSizeCap>
				</rollingPolicy>
				<encoder>
					<pattern>${PATTERN_FILE}</pattern>
				</encoder>
			</appender>

			//<!-- 必选节点; 特殊的logger元素,用来指定最基础的日志输出级别 -->
			<root level="info" additivity="true">
				<appender-ref ref="CONSOLE" />
				<appender-ref ref="LOGFILE" />
			</root>

			//<!-- 可选节点; 设置某个包或具体某个类的日志级别,以及<appender> (覆盖root节点的输出级别) -->
			//<!-- name: 受此logger约束的某一个包或具体某一个类 -->
			//<!-- level: 日志级别, 默认继承上级的打级别 -->
			//<!-- additivity: 是否向上级logger传递打印信息. 默认是true -->
			<logger name="com.example.spring.controller" level="DEBUG">
				<appender-ref ref="CTRL_DEBUG_ASYNC" /> //<!-- DEBUG+ -->
				<appender-ref ref="CTRL_WARN" /> //<!-- ONLY WARN -->
				<appender-ref ref="CTRL_ERROR" /> //<!-- ERROR+ -->
			</logger>
		</configuration>
	
//}	
	
//{--------<<<demo-log4j>>>---------------------------------------------------------------
	
		#系统 -> 配置(基础-级别, 控制台, 文件)
		#log4j.rootCategory=INFO, CONSOLE, LOGFILE //rootCategory 已过时
		log4j.rootLogger=INFO, CONSOLE, LOGFILE

		#系统 -> 控制台
		log4j.appender.CONSOLE=org.apache.log4j.ConsoleAppender
		log4j.appender.CONSOLE.layout=org.apache.log4j.PatternLayout
		log4j.appender.CONSOLE.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss.SSS} [%5p] [%t] - %m%n

		#系统 -> 文件
		log4j.appender.LOGFILE=org.apache.log4j.DailyRollingFileAppender
		log4j.appender.LOGFILE.file=/logs/log
		log4j.appender.LOGFILE.DatePattern='_'yyyyMMdd'.log'
		log4j.appender.LOGFILE.layout=org.apache.log4j.PatternLayout
		log4j.appender.LOGFILE.layout.ConversionPattern= %d{HH:mm:ss.SSS} [%5p] [%t] - %m%n

		#controller -> 配置 -> (包-级别, 文件1, 文件2, 文件3)
		log4j.logger.com.example.spring.controller=DEBUG, CTRL_DEBUG, CTRL_WARN, CTRL_ERROR

		#controller -> 文件1 -> DEBUG+
		log4j.appender.CTRL_DEBUG=org.apache.log4j.DailyRollingFileAppender
		log4j.appender.CTRL_DEBUG.File=/logs/ctrl/debug
		log4j.appender.CTRL_DEBUG.DatePattern='_'yyyyMMdd_HH'.log'
		log4j.appender.CTRL_DEBUG.layout=org.apache.log4j.PatternLayout
		log4j.appender.CTRL_DEBUG.layout.ConversionPattern=%d{HH:mm:ss.SSS} [%5p] [%t] - %m%n

		#controller -> 文件2 -> ONLY WARN
		log4j.appender.CTRL_WARN.filter.a=org.apache.log4j.varia.LevelRangeFilter
		log4j.appender.CTRL_WARN.filter.a.LevelMin=WARN
		log4j.appender.CTRL_WARN.filter.a.LevelMax=WARN
		log4j.appender.CTRL_WARN.filter.a.acceptOnMatch=true
		log4j.appender.CTRL_WARN=org.apache.log4j.DailyRollingFileAppender
		log4j.appender.CTRL_WARN.File=/logs/ctrl/warn/warn
		log4j.appender.CTRL_WARN.DatePattern='_'yyyyMMdd_HH'.log'
		log4j.appender.CTRL_WARN.layout=org.apache.log4j.PatternLayout
		log4j.appender.CTRL_WARN.layout.ConversionPattern=%d{HH:mm:ss.SSS} [%5p] - %m%n

		#controller -> 文件3 -> ERROR+
		log4j.appender.CTRL_ERROR.Threshold=ERROR
		log4j.appender.CTRL_ERROR=org.apache.log4j.DailyRollingFileAppender
		log4j.appender.CTRL_ERROR.File=/logs/ctrl/error/error
		log4j.appender.CTRL_ERROR.DatePattern='_'yyyyMMdd_HH'.log'
		log4j.appender.CTRL_ERROR.layout=org.apache.log4j.PatternLayout
		log4j.appender.CTRL_ERROR.layout.ConversionPattern=%d{HH:mm:ss.SSS} [%5p] [%t] - %m%n

//}
	
	
	
	
	