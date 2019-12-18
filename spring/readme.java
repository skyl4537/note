05
    页面非空验证jquery
07

07
    // MyBatis: 数据访问层框架; 底层是对JDBC的封装; 无需编写实现类,只需写sql
08

11

12
    
2-1
    在'数据访问层'和'控制器层'处理异常,service中只抛出异常
    
2-7
    功能:从应用程序角度出发,软件具有哪些功能.
    业务:从代码角度出发,完成功能时的逻辑.对应 Service 中一个方法
    事务:从数据库角度出发,完成业务时需要执行的 SQL 集合,统称一个事务.
        //事务回滚: 如果在一个事务中某个 SQL 执行事务,希望回归到事务的原点,保证数据库数据的完整性.
    
3-1
    单纯判断某一列是否存在,sql语句 select count(*) 比 select * 高效!!
    所有定义在接口中的常量,默认都是 public static final *;
    
    final 修饰的变量不允许重新实例化
    
    存储过程
        (0).减少java代码的业务逻辑,将逻辑转移到数据库层面.
        (1).减少客户端与服务器之间的网络IO,只传存储过程名和参数.
        (2).存储过程直接加载到数据库的内存区域,省去sql编译过程,运行速度快.
        [3].大量编写存储过程,占用服务器内存.(弊端)
    
4-1
    内容较多
    
    /*************************Spring********************************************/

1-2S

1-5S

1-6S

1-7S
    
1-8S
    实例化DEMO
    1.DataSource
        <bean id="dataSouce" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
            <property name="driverClassName" value="com.mysql.jdbc.Driver"></property>
            <property name="url" value="jdbc:mysql://localhost:3306/test"></property>
            <property name="username" value="root"></property>
            <property name="password" value="root"></property>
        </bean>
    2.sqlSessionFactory
        <bean id="factory" class="org.mybatis.spring.SqlSessionFactoryBean">
            //<!-- 依赖注入,数据库连接信息来源于 dataSource -->
            <property name="dataSource" ref="dataSouce"></property>
        </bean>
    
    3.mybatis扫描器
        //<!-- 扫描器相当于 mybatis.xml 中 mappers 下 package 标签,扫描后会给对应接口创建接口对象-->
        <bean id="mapperScanner" (id可省) class="org.mybatis.spring.mapper.MapperScannerConfigurer">
            //<!-- 要扫描哪个包 -->
            <property name="basePackage" value="com.x.mapper"></property>
            //<!-- 依赖注入,让 factory 知道要扫描的包 -->
            <property name="sqlSessionFactory" ref="factory"></property>
        </bean>
        
    4.加载Spring配置
        //显示读取配置文件
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        
        //获取所有已定义的bean
        String[] beans = context.getBeanDefinitionNames(); 
        
        //获取指定类对象(args0:必须是实现类的类名小驼峰)
        FlowerService flowerService = context.getBean("flowerService", FlowerServiceImpl.class);
    
    4.1.简化版
        #将加载 applicationContext.xml 工作交给tomcat完成. tomcat启动时,默认加载 web.xml 文件,在此文件配置:
        
        //<!-- 设置spring配置文件的路径 -->
        <context-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:applicationContext.xml</param-value>
        </context-param>
        //<!-- 加载Spring配置文件 -->
        <listener>
            <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
        </listener>
    
        //tomcat加载 web.xml 后,所有信息保存在 ServletContext 对象中.
        ServletContext servletContext = getServletContext();
        
        //spring和web整合后,所有配置信息保存在 WebApplicationContext. (ApplicationContext的子类)
        ApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);

    4.2.如果要给类的某个属性赋值—这个属性所在类必须要被Spring所管理(***)
        //<!-- 由 spring 管理 service 实现类 -->
        <bean id="flowerService" class="com.x.service.impl.FlowerServiceImpl">
            //依赖注入,依赖Mapper
            <property name="flowerMapper" ref="FlowerMapper"></property>
        </bean>
        
        FlowerService flowerService = context.getBean("flowerService", FlowerServiceImpl.class);

#2-1S
    1.刷新验证码功能
        //刷新验证码的功能放在 servlet 中完成
        客户端请求 图片或Servlet,服务端都是返回字节流,对于浏览器响应都是一样的.
        所以,直接访问静态资源; 或访问在servlet,在servlet中返回静态资源输出流,效果都是一样.
    
        //获取当前项目 images 文件夹在磁盘中的完整路径.("D:\blue\images")
        String path = servletContext.getRealPath("images");
        
    2.生成验证码
    
    3.浏览器请求验证码
    
#2-2S
    *ServiceImpl 中 *Mapper 对象必须设置 get/set 方法.
    而 *Controller 中的 *Service 对象不需要,为什么????
    //设置get/set是为spring在管理 *Service 时,依赖注入使用.
    //后者是直接给全局属性 *Service 赋值,所以可以不设置get/set.
    
    
 
#2-3S
    AOP(Aspect Oriented Programming): 面向切面编程
    
    AOP是将既有程序定义为一个切入点,然后在其前后切入不同的执行内容,
    比如常见的有: 打开数据库连接/关闭数据库连接; 打开事务/关闭事务; 记录日志等。
   
#2-4S
    切入点表达式
        execution (* com.x.service.impl..*.*(..))
        //1. execution(): 表达式主体
        //2. 第一个*号: 返回值类型, *号表示所有!
        //3. 包名: 需要拦截的包名, 后面两个点表示当前包及其子孙包
        //4. 第二个*号: 类名
        //5. *(..): 最后*表示方法名, ()里面表示方法的参数,两个点表示任何参数
    
#2-5S
    
#2-6S
        
#2-7S
    
#2-8S
    AspectJ-所有通知
    
#2-9S

    
#2-10S
    使用注解(基于 AspectJ)
     Spring不会自动去寻找注解,必须告诉 spring 哪些包下的类中可能有注解
    
#2-11S
    设计模式: 前人总结的一套解决特定问题的代码
    
    代理设计模式
        优点: 保护真实对象; 让真实对象职责更明确; 扩展.
        三个角色: 真实对象(Laoban); 代理对象(Mishu); 抽象对象(Gongneng)
    
    静态代理设计模式
        //由代理对象代理所有真实对象的功能.
        //缺点: 当抽象对象里的功能比较多时,代理类中方法需要写很多
        (1).自定义代理类(Mishu)
        (2).抽象对象(Gongneng)的每个功能都需要代理类(Mishu)单独编写
    
        public interface Gongneng{ //抽象对象
            void mubiao();
            void chifan();
        }
        
        public class Laoban implements Gongneng{ //真实对象
            public void mubiao(){ syso("目标"); }
            public void chifan(){ syso("吃饭"); }
        }
        
        public class Mishu implements Gongneng{ //代理对象
            pivate Laoban laoban = new Laoban(); //持有真实对象的引用
            
            public void mubiao(){
                syso("约时间");
                laoban.mubiao();
                syso("谈合作");
            }
            public void chifan(){ /*...*/ }
        }
    
#2-12S
    动态代理设计模式(JDK)
        和 cglib 动态代理对比
        (1).优点: jdk自带,不需要导入额外jar
        (2).缺点: 真实对象必须实现接口; 利用反射机制,效率不高.
    
        public interface Gongneng{
            void mubiao();
            void chifan();
        }
        
        public class Laoban implements Gongneng{
            public void mubiao(){ syso("目标"); }
            public void chifan(){ syso("吃饭"); }
        }
        
        public class Mishu implements InvocationHandler { //JDK
            pivate Laoban laoban = new Laoban();
            
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("约时间");
                Object result = method.invoke(laoban, args);
                System.out.println("谈合作");
                return result;
            }
        }
        
        public static void main(String[] args) {
            //ClassLoader 全局唯一!!!
            //System.out.println(Women.class.getClassLoader()==Laoban.class.getClassLoader());
            
            Mishu mishu = new Mishu();
            //args0: 反射时使用的类加载器
            //args1: Proxy需要实现什么接口
            //args2: 通过接口对象调用方法时,需要调用哪个类的invoke方法
            Gongneng gongneng = (Gongneng) Proxy.newProxyInstance(Women.class.getClassLoader(), new Class[]{Gongneng.class}, mishu);
            gongneng.chifan();
        }
    
    动态代理设计模式(cglib)
        优点: 不需要实现接口; 基于字节码,生成真实对象的子类,运行效率高.
    
        public class Laoban implements Gongneng{ //真实对象
            public void mubiao(){ syso("目标"); }
            public void chifan(){ syso("吃饭"); }
        }
    
        public class Mishu implements MethodInterceptor{ //cglib
            //Object: cglib动态生成的代理类实例(子类)
            //Method: 被代理方法(父类方法)
            //Object[]: 参数值列表
            //MethodProxy: 代理类实现的被代理方法(子类重写的方法)
            public Object intercept(Object obj, Method method, Object[] arg, MethodProxy proxy) throws Throwable {
                syso("约时间");
                // method.invoke(obj, arg); //调用子类重写的方法(也就是 intercept(),产生迭代,错误!!!)
                
                //调用子类(obj)重写方法(proxy)的父类方法. (即实体类 Laoban 中的方法)
                Object result = proxy.invokeSuper(obj, arg); 
                syso("谈合作");
                return result;
            }
        }
    
        public static void main(String[] args) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(Laoban.class);
            enhancer.setCallback(new Mishu());
            
            Laoban laoban = (Laoban) enhancer.create();
            laoban.chifan(); //调用父类方法
        }
    
#3-1S
    
#3-2S
    自动注入
    
#3-3S
    加载properties文件
    
#3-4S
    
#3-5S
    单例设计模式
    在应用程序中保证最多只能有一个实例
    好处: 提升运行效率(不用new,直接用); 实现数据共享.
    
    application 对象(四大作用域对象),从tomcat启动到关闭,一直有效.
    通过 getServletContext() 方法取出该对象,获取其中信息.
    
    0.饿汉式
    //构造方法: 方法名同类名,无返回值, 因为必须返回该类的类型,所有不用写.
    (0).私有化构造方法,其他方法不能new该对象.
    (1).对外提供一个公共访问入口 static SingleTon getInstance()
    (2).instance对象被 getInstance() 调用,加上 static,不能被外部访问,加上 private
    
    //加锁和二次判断,都是为了防止多线程异步访问时,创建多个对象.
    (1).线程 1,2 同时到达 synchronized,锁住,排队等待
    (1).线程 1 先进去,创建 instance 对象,返回,释放锁
    (2).线程 2 进去,如果没有二次判断,也会继续创建 instance 对象.
    
        public class SingleTon{ //对象只有被调用时才去创建 -> 懒汉式
            private SingleTon instance;
            
            private SingleTon(){ }
            
            public static SingleTon getInstance(){
                if(null==instance){
                    synchronized(SingleTon.class){
                        if(null==instance){ //二次判断
                            instance=new SingleTon();
                        }
                    }
                }
                return instance;
            }
        }
    
    1.饿汉式
        public class SingleTon{ //在类加载时进行实例化 -> 饿汉式
            private static SingleTon instance=new SingleTon();
            
            private SingleTon(){ }
            
            public static SingleTon getInstance(){
                return instance;
            }
        }
    
    2.二者对比
        懒汉式: 由于添加了锁,所以导致效率低
        饿汉式: 解决了懒汉式中多线程访问可能出现同一个对象和效率低问题
    
#3-7S
    
#3-10S
   
#3-11S
    
#3-12S


http://192.168.5.25:8080/webpark/pass/getSimPlateList.do


{
carPlate: "",
carTypeId: 0,
endTime: "2019-12-11 23:59:59",
pageNumber: 0,
pageSize: 15,
parkId: 0,
passageId: "0",
startTime: "2019-12-11 00:00:00"
}

'简化依赖管理'：提供一系列的Starter，将各种功能性模块进行了划分与封装
'自动化配置'：为每一个Starter都提供了自动化的java配置类
'嵌入式容器'：嵌入式tomcat，无需部署war文件
'监控の端点'：通过 Actuator 模块暴露的http接口，可以轻松的了解和控制应用的运行情况
    
    
#1-1C
    
    
#10.1寸的是 101.206
#4.3寸的是 101.207

http://192.168.101.207/cgi-bin/setting2.cgi
{"softTrigger":1}

    
    
    
    ORM: Object Relation Mapping,对象关系映射; //将程序中的对象持久化到数据库中
    
    IoC: Inversion of Control,控制反转; //作用: 解耦(解除对象管理和程序员之间的耦合)
    //控制->控制类的对象; 反转->转交给Spring负责.
    
    //以前,类(A)中用到类(B)的对象b,需要在A的代码中显式的new一个B的对象.
    //采用依赖注入DI技术之后,A的代码只需要定义一个私有的B对象,不需要直接new来获得这个对象,
    //而是通过相关的容器控制程序来将B对象在外部new出来并注入到A类里的引用中.
    //而具体获取的方法、对象被获取时的状态由配置文件(如XML)来指定.
    
    DI: Dependency Injection,依赖注入;
    //当类(A)需要依赖类(B)对象时,把 B 赋值给 A 的过程就叫做依赖注入

    
    
    
我也遇到了这个问题，后来解决了，原因是如果有注入bean的那个类，在被其他类作为对象引用的话（被调用）。
这个被调用的类也必须选择注解的方式，注入到调用他的那个类中，不能用 new出来做对象，
new出来的对象再注入其他bean就会 发生获取不到的现象。所以要被调用的javabean，都需要@service，
交给Spring去管理才可以，这样他就默认注入了。
    
    
    
0226
    SQL和NoSQL区别?
    
    一对一,一对多靠主外键关联, 多对多靠中间表.
    
    NoSQL特点: 数据量大; 价值较低; 写入操作频繁(非重要).
    
    
#十次方-04
    数据库优化: 设计优化(必须熟悉业务), 查询优化.
    
    查询优化
        (1).查询语句不要用 SELECT * FROM
        (2).避免全局扫描,即涉及到 非 逻辑 NOT IN....
        (3).创建索引,加速查询，但影响增删改
        
    // 搜索分类: 搜索引擎, 站内搜索
    
    // ElasticSearch && Solar: 前者可以实现'分布式搜索'
    

    
// 0.restful操作
    // 新建索引
        // PUT http://127.0.0.1:9002/myindex
        
    // 新建文档
        // POST http://127.0.0.1:9002/myindex/article
        
             // body <-> {"title":"boot2","content":"新版本!"}
             
    // 修改文档
        // //类似 SpringData 的更新API,查询时: 有ID则更新,无ID则新增
        // PUT  http://127.0.0.1:9002/myindex/article/AODIEWNUWEU9H
            
             // body <-> {"title":"boot2正式版","content":"新版本发布了吗?"}
        
    // 查询所有
        // GET  http://127.0.0.1:9002/myindex/article/_search
        
    // 查询ID
        // GET  http://127.0.0.1:9002/myindex/article/1
        
    // 基本查询
        // //查询关键字是一个词(如: 正式),则不能称之为模糊查询
        // GET  http://127.0.0.1:9002/myindex/article/_search?q=title:正式
        
        // GET  http://127.0.0.1:9002/myindex/article/_search?q=title:boot2正式版
    
    // 模糊查询
        // //*代表任意字符; 模糊查询是针对每个单词而言,而不是整个句子
        // GET  http://127.0.0.1:9002/myindex/article/_search?q=title:*b*
        
    // 删除文档
        // DELETE http://127.0.0.1:9002/myindex/article/1
        
1.head插件操作

2.IK分词器
    // 下载 https://github.com/medcl/elasticsearch-analysis-ik/releases
    
    // 安装 解压,拷贝到ES的 /plugins 目录,重启ES即可
    
    // 测试
        // //中华人民共和国, 国歌
        // http://localhost:9200/_analyze?pretty&analyzer=ik_smart&text=中华人民共和国国歌
        
        // //分词粒度更细: 中华人民共和国,中华人民,中华,华人,人民共和国,人民,人,民,共和国,共和,和,国国,国歌
        // http://localhost:9200/_analyze?pretty&analyzer=ik_max_word&text=中华人民共和国国歌
    
    // 自定义词库
        // //默认不作为一个词
        // http://localhost:9200/_analyze?pretty&analyzer=ik_smart&text=人艰不拆
        
        // (1).在'/elasticsearch/plugins/ik/config/'新建 my.dic (编码UTF-8)
            // 人艰不拆
            
        // (2).修改'/~/ik/config/IKAnalyzer.cfg.xml'
            // <properties> 
                // <comment>IK Analyzer 扩展配置</comment> 
                // // <!--用户可以在这里配置自己的扩展字典 --> 
                // <entry key="ext_dict">my.dic</entry> 
                 // // <!--用户可以在这里配置自己的扩展停止词字典--> 
                // <entry key="ext_stopwords"></entry>
                
                // // <!--用户可以在这里配置远程扩展字典 --> 
                // <entry key="remote_ext_dict">location</entry> 
                // // <!--用户可以在这里配置远程扩展停止词字典--> 
                // <entry key="remote_ext_stopwords">location</entry> 
            // </properties> 
        
// 3.API操作
        // <dependency>
            // <groupId>org.springframework.data</groupId>
            // <artifactId>spring-data-elasticsearch</artifactId>
        // </dependency>

    // application.properties
        // spring.data.elasticsearch.cluster-nodes=127.0.0.1:9300
        
    // 注解详解
        // public @interface Document {
            // String indexName(); //索引库的名称,个人建议以项目的名称命名
             
            // String type() default ""; //类型,个人建议以实体的名称命名
             
            // short shards() default 5; //默认分区数
             
            // short replicas() default 1; //每个分区默认的备份数
             
            // String refreshInterval() default "1s"; //刷新间隔
             
            // String indexStoreType() default "fs"; //索引文件存储类型
        // }
        
        // public @interface Field {
            // FieldType type() default FieldType.Auto; //自动检测属性的类型,可以根据实际情况自己设置
             
            // FieldIndex index() default FieldIndex.analyzed; //默认情况,一般默认分词就好,除非这个字段你确定查询时不会用到
             
            // DateFormat format() default DateFormat.none; //时间类型的格式化
             
            // String pattern() default ""; 
             
            // boolean store() default false; //默认情况下不存储原文
             
            // String searchAnalyzer() default ""; //指定字段搜索时使用的分词器
             
            // String indexAnalyzer() default ""; //指定字段建立索引时指定的分词器
             
            // String[] ignoreFields() default {}; //如果某个字段需要被忽略
             
            // boolean includeInParent() default false;
        // }

    // javabean
        // //索引库的名称, 类型名称(同实体名称)
        // @Document(indexName = "myindex", type = "article")
        // public class Article {
            // //mysql中的id,非索引库中的id
            // @Id
            // private String id;

            // // 对应索引库中的域
            // // index: 是否被索引(能否被搜索); 是否分词(搜索时是整体匹配还是分词匹配); 是否存储(是否在页面上显示)
            // // analyzer: 存储时使用的分词策略
            // // searchAnalyzer 查询时的.....(二者必须一致)
            // @Field(index = true, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
            // private String title;

            // @Field(index = true, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
            // private String content;
        // }


#logstash
    轻量级的日志搜集处理框架.


// #0326-短信注册
    // service, dao --> 抛出异常;
    // Controller/全局异常处理器 --> 处理异常.
    
    // @Service
    // public class PersonService{

        // private void add(Person person, String smsCode) {
            // if (StringUtils.isEmpty(smsCode))
                // throw new RuntimeException("请输入验证码");

            // if (!smsCodeRedis.equalsIgnoreCase(smsCode))
                // throw new RuntimeException("验证码不正确或已过期");

            // personDao.add(person); //写库
        // }
    // }


#0329-BCrypt 密码加密
    // //SpringSecurity 提供的 BCrypt 强哈希方法每次加密的结果都不一样. 
    
        // <dependency>
            // <groupId>org.springframework.boot</groupId>
            // <artifactId>spring-boot-starter-security</artifactId>
        // </dependency>
    
        // @Bean
        // public BCryptPasswordEncoder encoder(){
            // return new BCryptPasswordEncoder();
        // }
        
        // @Autowired
        // BCryptPasswordEncoder encoder;
        
        // @Test //测试加密
        // public void encoder() {
            // String encode = encoder.encode("123");
            // System.out.println("encode: " + encode); //加密: $2a$10$ujGzEaaHHU0y72yzfwMk.OA0KUNpKfRFr291I5YuGqnWawmnAQV1y

            // boolean matches = encoder.matches("123", "$2a$10$ujGzEaaHHU0y72yzfwMk.OA0KUNpKfRFr291I5YuGqnWawmnAQV1y");
            // System.out.println("matches: " + matches); //比对: true
        // }
        
        // //登陆逻辑S
        // public User login(String loginName, String loginPwd) {
            // User user = helloMapper.findByName(loginName);
            // if (null != user && encoder.matches(loginPwd, user.getPwd())) {
                // System.out.println("登陆成功");
                // return user;
            // } else {
                // System.out.println("用户名或密码不正确");
                // return null;
            // }
        // }

#0330
    SpringMVC 配置的基本功能
        组件扫描; 
        配置视图解析器; 
        开启MVC注解支持; 
        释放静态资源.

    
#服务端发现
    Eureka，负载均衡属于软负载，
    消费者从服务端（如Eureka-Server）拉取已注册的生产者列表，
    根据负载均衡策略从中选择一个生产者，然后发送请求。
    整个过程都在客户端完成，并不需要服务端参与。

#客户端负载均衡
    



#ribbon
    服务发现       
        
    
    服务选择规则
    
    
    服务监听：检测失效服务

// @RequestBody 注解必须配合 @PostMapping 使用，不能用 @GetMapping

#多模块
    product-server：所有业务逻辑
    
    product-client：外部暴露接口
    
    product-common：公用的对象

#统一配置中心
    可以通过



#网关
    GET http://192.168.8.7:9002/friend/info
    
    #zuul端口
    server.port=9011
  
    GET http://192.168.8.7:9011/demo-friend/friend/info
    
    
    #配置网关路由
    zuul.routes.demo-friend.path=/friends/**
    zuul.routes.demo-friend.service-id=demo-friend
    #简写以上配置
    zuul.routes.demo-friend=/friends/**

    GET http://192.168.8.7:9011/demo-friend/friend/info
    GET http://192.168.8.7:9011/friends/friend/info
    

    #Actuator-暴露端点
    management.endpoints.web.exposure.include=routes
    
    #查看已配置的网关路由（后者更为详细）
    http://localhost:9011/actuator/routes
    http://localhost:9011/actuator/routes/details
    

    #排除某些路由的通配，如排除路径中包含'/friend/info'
    zuul.ignored-patterns=/**/friend/info
    
    #都不可访问
    GET http://192.168.8.7:9011/demo-friend/friend/info
    GET http://192.168.8.7:9011/friends/friend/info

    
    #忽略原真实服务名（忽略全部使用*）
    zuul.ignored-services=demo-friend

    #不可访问
    GET http://192.168.8.7:9011/demo-friend/friend/info
    #可以访问
    GET http://192.168.8.7:9011/friends/friend/info




#zuul
#配置请求URL的请求规则，指定Eureka注册中心中的服务id，转发请求头（默认过滤请求头）
zuul.routes.demo-user.path=/users/**
zuul.routes.demo-user.service-id=demo-user
zuul.routes.demo-user.custom-sensitive-headers=true
zuul.routes.demo-friend.path=/friends/**
zuul.routes.demo-friend.service-id=demo-friend
zuul.routes.demo-friend.custom-sensitive-headers=true
#忽略所有微服务
#zuul.ignored-services=*
#所有访问都加前缀
zuul.prefix=/demo

#查看所有的网关映射
management.endpoints.web.exposure.include=routes


// ##maven
// #一个项目就是一个工程
// 如果项目非常庞大，就不适合使用package来划分模块，最好是每一个模块对应一个工程，利于分工协作。
// 借助于maven就可以将一个项目拆分成多个工程

// #项目中使用jar包，需要 复制+粘贴 项目的lib中
// 同样的jar包重复的出现在不同的项目工程中，你需要做不停的复制粘贴的重复工作。
// 借助于maven，可以将jar包保存在“仓库”中，不管在哪个项目只要使用引用即可就行。

// #jar包需要的时候每次都要自己准备好或到官网下载
// 借助于maven我们可以使用统一的规范方式下载jar包，规范

// #jar包版本不一致的风险
// 不同的项目在使用jar包的时候，有可能会导致各个项目的jar包版本不一致，导致未执行错误。
// 借助于maven，所有的jar包都放在“仓库”中，所有的项目都使用仓库的一份jar包。

// #一个jar包依赖其他的jar包需要自己手动的加入到项目中
// 借助maven，它会自动的将依赖的jar包导入进来。


// 构建：把动态的Web工程经过编译得到的编译结果部署到服务器上的整个过程。


// 清理clean：将以前编译得到的旧文件class字节码文件删除
// 编译compile：将java源程序编译成class字节码文件
// 测试test：自动测试，自动调用junit程序
// 报告report：测试程序执行的结果
// 打包package：动态Web工程打War包，java工程打jar包
// 安装install：Maven特定的概念-----将打包得到的文件复制到“仓库”中的指定位置
// 部署deploy：将动态Web工程生成的war包复制到Servlet容器下，使其可以运行

// #执行maven命令必须进入到 pom.xml 的目录中进行执行
// mvn clean：清理
// mvn compile：编译主程序
// mvn test-compile：编译测试程序
// mvn test：执行测试
// mvn package：打包
// mvn install：安装


// #依赖
// 如果依赖的是自己或者团队开发的maven工程，需要先使用install命令把被依赖的maven工程的jar包导入到本地仓库中


// #依赖范围
// compile，默认值，适用于所有阶段（开发、测试、部署、运行），本jar会一直存在所有阶段。
// provided，只在开发、测试阶段使用，目的是不让Servlet容器和你本地仓库的jar包冲突 。如servlet.jar。
// runtime，只在运行时使用，如JDBC驱动，适用运行和测试阶段。
// test，只在测试时使用，用于编译和运行测试代码。不会随项目发布。
// system，类似provided，需要显式提供包含依赖的jar，Maven不会在Repository中查找它。
    
// #生命周期

// compile  #缺省值，适用于所有阶段，会随着项目一起发布。编译范围依赖在所有的 classpath 中可用，同时它们也会被打包。

// provided #类似compile，但期望JDK、容器或使用者会提供这个依赖。如：servlet.jar，lombok。
         // #例如，在开发 web 应用时，编译期需要一个 servlet.jar 来编译程序中的 servlet，但打包时，不需要此 servlet.jar。
         // #因为，程序运行时，由servlet容器（tomcat）来提供 servlet.jar。

// runtime #只在运行时使用，适用运行和测试阶段。如：JDBC驱动，

// test    #只在测试时使用，用于编译和运行测试代码。不会随项目发布。如：junit

// compile  #编译阶段(√); 测试阶段(√); 打包(√); 部署(√); 如: spring-core
// provied  #编译阶段(√); 测试阶段(√); 打包(x); 部署(x); 如: servlet-api（tomcat提供），lombok
// test     #编译阶段(x); 测试阶段(√); 打包(x); 部署(X); 如: junit
// runtime  #编译阶段(x); 测试阶段(√); 打包(√); 部署(√); 如: mysql-connector-java

// 依赖的传递：Optional

// true 该依赖只能在本项目中传递，不会传递到引用该项目的父项目中，父项目需要主动引用该依赖才行。

// //A依赖B，B依赖C，A能否使用C呢？
// '非compile'范围的依赖不能传递，必须在有需要的工程 A 中单独加入 C.

// 依赖的排除
 
// <dependency>
    // <groupId>org.springframework.boot</groupId>
    // <artifactId>spring-boot-starter-web</artifactId>
    // <exclusions>
        // <exclusion>
            // <artifactId>spring-boot-starter-logging</artifactId> <!--排除'sp默认logback包'-->
            // <groupId>org.springframework.boot</groupId>
        // </exclusion>
    // </exclusions>
// </dependency>




#sell

-- ----------------------------
-- Table structure for product_info
-- ----------------------------
DROP TABLE IF EXISTS `product_info`;
CREATE TABLE `product_info` (
  `product_id` varchar(32) NOT NULL,
  `product_name` varchar(64) NOT NULL COMMENT '商品名称',
  `product_price` decimal(8,2) NOT NULL COMMENT '单价',
  `product_stock` int(11) NOT NULL COMMENT '库存',
  `product_description` varchar(64) DEFAULT NULL COMMENT '描述',
  `product_icon` varchar(512) DEFAULT NULL COMMENT '小图',
  `product_status` tinyint(3) DEFAULT '0' COMMENT '商品状态，0正常1下架',
  `category_type` int(11) NOT NULL COMMENT '类目编号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='商品表';


-- ----------------------------
-- Table structure for product_category
-- ----------------------------
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category` (
  `category_id` int(11) NOT NULL AUTO_INCREMENT,
  `category_name` varchar(64) NOT NULL COMMENT '类目名称',
  `category_type` int(11) NOT NULL COMMENT '类目编号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `uqe_category_type` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='类目表';


##手动设置cookies
    5-4 00:18:13
    
#
    



#taotao-02
##声明式事务
    1.service
        @Override
        public int insTbItemDesc(TbItem tbItem, TbItemDesc desc) throws Exception {
            int index =0;
            try {
                index= tbItemMapper.insertSelective(tbItem);
                index+= tbItemDescMapper.insertSelective(desc);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            //当出错时继续运行
            if(index==2){
                return 1;
            }else{
                //通过手动抛出异常，可以使用 dubbo 的 provider 和 consumer 交互的问题.
                throw new Exception("新增失败,数据还原");
            }
        }
    
        //使用注解
        @Transactional(rollbackFor = Exception.class)

    2.controller
        @RequestMapping("item/save")
        @ResponseBody
        public EgoResult insert(TbItem item,String desc){
            EgoResult er = new EgoResult();
            int index;
            try {
                index = tbItemServiceImpl.save(item, desc);
                System.out.println("controler:index:"+index);
                if(index==1){
                    er.setStatus(200);
                }
            } catch (Exception e) {
                // e.printStackTrace();
                er.setData(e.getMessage());
            }
            return er;
        }
    

    
    
// #控制层也往外抛异常 ---> 异常全局处理器 GlobalExceptionConfig

#时间格式化
    https://blog.csdn.net/zhou520yue520/article/details/81348926

#返回值为null
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class ResultVO {
        private Integer code;

        //如果此字段是必须返回字段，则可以赋初始值 ""
        //不然，就会被下面的配置影响，无此字段返回
        private String msg;

        //全局配置 - 配置文件 - spring.jackson.default-property-inclusion=non_null
        // @JsonInclude(JsonInclude.Include.NON_NULL) //值为 null 则不参加序列化
        private Object data;

        public ResultVO(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }


#mysql支持json -> https://www.cnblogs.com/ooo0/p/9309277.html
    // //JSON_OBJECT：生成json对象。如果有key为NULL或参数个数为奇数，则抛错。
    // mysql> SET @j = (SELECT JSON_OBJECT('age', 20, 'birthday', now(), 'datas', (SELECT JSON_OBJECT('name', 'lucy'))) json);
    // Query OK, 0 rows affected (0.00 sec)

    // mysql> SELECT @j;
    // +------------------------------------------------------------------------------+
    // | @j                                                                           |
    // +------------------------------------------------------------------------------+
    // | {"age": 20, "time": "2019-07-30 16:55:32.000000", "datas": {"name": "lucy"}} |
    // +------------------------------------------------------------------------------+
    // 1 row in set (0.00 sec)
    
    // //JSON_ARRAY: json数组
    // mysql> SELECT JSON_ARRAY(JSON_OBJECT('age',20,'name','lucy'), JSON_OBJECT('age',18,'name','lily')) json_arrary;
    // +------------------------------------------------------------+
    // | json_arrary                                                |
    // +------------------------------------------------------------+
    // | [{"age": 20, "name": "lucy"}, {"age": 18, "name": "lily"}] |
    // +------------------------------------------------------------+
    // 1 row in set (0.00 sec)
    
    // //JSON_CONTAINS_PATH(json_doc, one_or_all, path[, path] ...) : 查询 key 值是否存在
    // //one_or_all只能取值"one"或"all"，one表示只要有一个存在即可；all表示所有的都存在才行。
    
    // //查询单个 key 时，第二个参数传 one/all 都可以
    // mysql> SELECT JSON_CONTAINS_PATH(@j, 'one', '$.age');
    // +----------------------------------------+
    // | JSON_CONTAINS_PATH(@j, 'one', '$.age') |
    // +----------------------------------------+
    // |                                      1 |
    // +----------------------------------------+
    // 1 row in set (0.00 sec)
    
    // //查询多个 key
    // mysql> SELECT JSON_CONTAINS_PATH(@j, 'one', '$.age', '$.datas.namessss');
    // +---------------------------------------------------------+
    // | JSON_CONTAINS_PATH(@j, 'one', '$.age', '$.datas.namessss') |
    // +---------------------------------------------------------+
    // |                                                       1 |
    // +---------------------------------------------------------+
    // 1 row in set (0.00 sec)

    // mysql> SELECT JSON_CONTAINS_PATH(@j, 'all', '$.age', '$.datas.namessss');
    // +---------------------------------------------------------+
    // | JSON_CONTAINS_PATH(@j, 'all', '$.age', '$.datas.namessss') |
    // +---------------------------------------------------------+
    // |                                                       0 |
    // +---------------------------------------------------------+
    // 1 row in set (0.00 sec)
    
    
    // //JSON_CONTAINS(json_doc, val[, path]) : 查询 key-value 键值对是否存在
    // //包含则返回1，否则返回0。如果有参数为NULL或path不存在，则返回NULL。
    // //第一个参数是json，第二个参数是value，第三个参数是key。
    // //这里的value有些限制条件，比如正整数1，需要写成'1'；如果是字符串"lnmp"，要写成'"lnmp"'。sql才是有效的
    
    // mysql> SELECT JSON_CONTAINS(@j, "lucy", '$.datas.name');
    // ERROR 3141 (22032): Invalid JSON text in argument 2 to function json_contains: "Invalid value." at position 0.
    
    // mysql> SELECT JSON_CONTAINS(@j, '"lucy"', '$.datas.name'); //
    // +---------------------------------------------+
    // | JSON_CONTAINS(@j, '"lucy"', '$.datas.name') |
    // +---------------------------------------------+
    // |                                           1 |
    // +---------------------------------------------+
    // 1 row in set (0.00 sec) 

    
    // //JSON_EXTRACT(json_doc, path[, path] ...) : 解析json
    // mysql> SELECT JSON_EXTRACT(@j, '$.datas.name');
    // +----------------------------------+
    // | JSON_EXTRACT(@j, '$.datas.name') |
    // +----------------------------------+
    // | "lucy"                           |
    // +----------------------------------+
    // 1 row in set (0.00 sec)
    
    // //JSON_KEYS(json_doc[, path]) : 查询所有的key
    // mysql> SELECT JSON_KEYS(@j);
    // +--------------------------+
    // | JSON_KEYS(@j)            |
    // +--------------------------+
    // | ["age", "time", "datas"] |
    // +--------------------------+
    // 1 row in set (0.00 sec)


    // mysql> SELECT JSON_KEYS(@j, '$.datas');
    // +--------------------------+
    // | JSON_KEYS(@j, '$.datas') |
    // +--------------------------+
    // | ["name"]                 |
    // +--------------------------+
    // 1 row in set (0.00 sec)
    
    // //JSON_INSERT(json_doc, path, val[, path, val] ...) : json追加 key。
    // //如果 key 已存在（age），且 value 类型相同，则忽略此val（不存在才插入）。
    // mysql> SELECT JSON_INSERT(@j, '$.age', 18, '$.id', '001');
    // +-------------------------------------------------------------------------------------------+
    // | JSON_INSERT(@j, '$.age', 18, '$.id', '001')                                               |
    // +-------------------------------------------------------------------------------------------+
    // | {"id": "001", "age": 20, "time": "2019-07-30 16:55:32.000000", "datas": {"name": "lucy"}} |
    // +-------------------------------------------------------------------------------------------+
    // 1 row in set (0.00 sec)
    
    // //如果 key 已存在（age），但 value 类型相同，则插入。
    // mysql> SELECT JSON_INSERT(@j, '$.age', '18', '$.id', '001');
    // +-------------------------------------------------------------------------------------------+
    // | JSON_INSERT(@j, '$.age', '18', '$.id', '001')                                             |
    // +-------------------------------------------------------------------------------------------+
    // | {"id": "001", "age": 20, "time": "2019-07-30 16:55:32.000000", "datas": {"name": "lucy"}} |
    // +-------------------------------------------------------------------------------------------+
    // 1 row in set (0.00 sec)
    
    // //JSON_REPLACE: 替换 KEY 对应的 VALUE 值。KEY 不存在则忽略
    // mysql> SELECT @j;
    // +------------------------------------------------------------------------------+
    // | @j                                                                           |
    // +------------------------------------------------------------------------------+
    // | {"age": 20, "time": "2019-07-30 16:55:32.000000", "datas": {"name": "lucy"}} |
    // +------------------------------------------------------------------------------+
    // 1 row in set (0.00 sec)

    // mysql> SELECT JSON_REPLACE(@j, '$.age', 18, '$.id', '001');
    // +------------------------------------------------------------------------------+
    // | JSON_REPLACE(@j, '$.age', 18, '$.id', '001')                                 |
    // +------------------------------------------------------------------------------+
    // | {"age": 18, "time": "2019-07-30 16:55:32.000000", "datas": {"name": "lucy"}} |
    // +------------------------------------------------------------------------------+
    // 1 row in set (0.00 sec)
    
    // //JSON_SET: 直接插入，不管存不存在
    // mysql> SELECT JSON_SET(@j, '$.age', 18, '$.id', '001');
    // +-------------------------------------------------------------------------------------------+
    // | JSON_SET(@j, '$.age', 18, '$.id', '001')                                                  |
    // +-------------------------------------------------------------------------------------------+
    // | {"id": "001", "age": 18, "time": "2019-07-30 16:55:32.000000", "datas": {"name": "lucy"}} |
    // +-------------------------------------------------------------------------------------------+
    // 1 row in set (0.00 sec)
    
    
    // //JSON_REMOVE: 删除指定key。key不存在则忽略
    // mysql> SELECT JSON_REMOVE(@j, '$.age', '$.id');
    // +-------------------------------------------------------------------+
    // | JSON_REMOVE(@j, '$.age', '$.id')                                  |
    // +-------------------------------------------------------------------+
    // | {"time": "2019-07-30 16:55:32.000000", "datas": {"name": "lucy"}} |
    // +-------------------------------------------------------------------+
    // 1 row in set (0.00 sec)
    
    // //JSON_UNQUOTE: 去掉value值的外层引号。默认字符串类型带引号
    // mysql> SELECT JSON_EXTRACT(@j, '$.datas.name');
    // +----------------------------------+
    // | JSON_EXTRACT(@j, '$.datas.name') |
    // +----------------------------------+
    // | "lucy"                           |
    // +----------------------------------+
    // 1 row in set (0.00 sec)
    
    // mysql> SELECT JSON_UNQUOTE(JSON_EXTRACT(@j, '$.datas.name'));
    // +------------------------------------------------+
    // | JSON_UNQUOTE(JSON_EXTRACT(@j, '$.datas.name')) |
    // +------------------------------------------------+
    // | lucy                                           |
    // +------------------------------------------------+
    // 1 row in set (0.00 sec)
    
    // //JSON_TYPE: 查询JSON的类型，是 JSONOBJECT 还是 JSONARRARY。
    // mysql> SELECT JSON_TYPE(@j);
    // +---------------+
    // | JSON_TYPE(@j) |
    // +---------------+
    // | OBJECT        |
    // +---------------+
    // 1 row in set (0.00 sec)
    
    // //JSON_VALID: 查询是否是有效的json
    // mysql> SELECT JSON_VALID(@j);
    // +----------------+
    // | JSON_VALID(@j) |
    // +----------------+
    // |              1 |
    // +----------------+
    // 1 row in set (0.00 sec)
    
    
    
    // //和数据字段相结合使用
    // mysql> SELECT * FROM log ORDER BY id DESC limit 1;
    // +--------+------------------------------------------------------------------------------+---------+-------------+---------------------+
    // | id     | memo                                                                         | type_id | operator_id | editflag            |
    // +--------+------------------------------------------------------------------------------+---------+-------------+---------------------+
    // | 470941 | {"age": 20, "time": "2019-07-30 16:55:32.000000", "datas": {"name": "lucy"}} |    NULL |           1 | 2019-07-30 17:55:33 |
    // +--------+------------------------------------------------------------------------------+---------+-------------+---------------------+
    // 1 row in set (0.00 sec)
    
    // mysql> SELECT JSON_UNQUOTE(JSON_EXTRACT(memo, '$.datas.name')) name FROM log ORDER BY id DESC limit 1;
    // +------+
    // | name  |
    // +------+
    // | lucy |
    // +------+
    // 1 row in set (0.00 sec)
       
    // //JSON_EXTRACT 的简化写法
    // mysql> SELECT JSON_UNQUOTE(memo -> '$.datas.name') age FROM log ORDER BY id DESC limit 1;
    // +------+
    // | age  |
    // +------+
    // | lucy |
    // +------+
    // 1 row in set (0.00 sec)


// #事务-mysql
    // 只有使用了 Innodb 数据库引擎的数据库或表才支持事务

    // 事务是由一组 sql 语句组成，这些sql语句要么都执行成功，要么都执行失败

    // 事务保证了数据的'完整性'和'一致性'

// #事务传播行为
// 当事务方法被另一个事务方法调用时，必须指定事务应该如何传播。
// 例如：方法可能继续在现有事务中运行，也可能开启一个新事务，并在自己的事务中运行。
// 事务的传播行为可以由传播属性指定。
// Spring 定义了 7 种类传播行为。

// 事务传播属性可以在 @Transactional 注解的 propagation 属性中定义。

    // // propagation    -> 事务的传播行为, 默认 REQUIRED
    // // isolation    -> 事务的隔离度, 默认 DEFAULT
    // // timeout        -> 事务的超时时间, 默认 -1,永不.(设置10(秒),则10s后事务还没完成,就则自动回滚事务)
    // // readOnly        -> 否为只读事务，默认 false.(忽略那些不需要事务的方法,比如读取数据,可以设置 readOnly 为 true)
    // // rollbackFor    -> 指定能够触发事务回滚的异常类型,用逗号分隔. {xxx1.class, xxx2.class, ...}
    // // noRollbackFor-> 指定不触发事务回滚的异常类型, 同上.
    
// 当 bookService 的 purchase()方法被另一个事务方法 checkout()调用时，它默认会在现有的事务内运行。
// 因此在 checkout()方法的开始和终止边界内只有一个事务。
// 这个事务只在 checkout()方法结束的时候被提交，结果

// 数据库系统必须具有隔离并发运行各个事务的能力， 使它们不会相互影响， 避免各种并
// 发问题。 一个事务与其他事务隔离的程度称为隔离级别。SQL 标准中规定了多种事务隔离级
// 别， 不同隔离级别对应不同的干扰程度， 隔离级别越高， 数据一致性就越好， 但并发性越弱。
// 1) 读未提交：READ UNCOMMITTED
// 允许 Transaction01 读取 Transaction02 未提交的修改。
// 2) 读已提交：READ COMMITTED
// 要求 Transaction01 只能读取 Transaction02 已提交的修改。
// 3) 可重复读：REPEATABLE READ
// 确保 Transaction01 可以多次从一个字段中读取到相同的值， 即 Transaction01 执行
// 期间禁止其它事务对这个字段进行更新。
// 4) 串行化：SERIALIZABLE
// 确保 Transaction01 可以多次从一个表中读取到相同的行， 在 Transaction01 执行期
// 间，禁止其它事务对这个表进行添加、更新、删除操作。可以避免任何并发问题，但性
// 能十分低下。

    // <!-- 事务管理器 -->
    // <bean id="dataSourceTransactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        // <property name="dataSource" ref="dataSource"></property>
    // </bean>
    // <!-- 基于注解的事务管理 -->
    // <tx:annotation-driven transaction-manager="dataSourceTransactionManager"/>
    
    
#mybatis-plus

// ##MyBatis-Plus 内置 MyBatis-Spring

    // <!-- mybatis-plus（内置 mybatis-spring） -->
    // <dependency>
        // <groupId>com.baomidou</groupId>
        // <artifactId>mybatis-plus-boot-starter</artifactId>
        // <version>3.1.2</version>
    // </dependency> 
    
// ##mybatis vs mp
    // 基于 Mybatis
        // 需要编写 EmployeeMapper 接口，并手动编写 CRUD 方法
        // 提供 EmployeeMapper.xml 映射文件，并手动编写每个方法对应的 SQL 语句.
    // 基于 MP
        // 只需要创建 EmployeeMapper 接口, 并继承 BaseMapper 接口，Over！！！

// ##实体类

    // @Data
    // @NoArgsConstructor
    // @AllArgsConstructor
    // @TableName(value = "user") //表名
    // public class User {

        // /**
         // * AUTO          数据库自增
         // * ID_WORKER     分布式全局唯一ID 长整型类型
         // * ID_WORKER_STR 分布式全局唯一ID 字符串类型
         // * UUID          32位UUID 字符串
         // * INPUT         自行输入
         // * NONE          无状态
         // */
        // @TableId(value = "userId", type = IdType.ID_WORKER_STR)
        // private String userId;

        // @TableField("user_name")
        // private String userName;
        // private Integer account;
    // }
        
// ##mp 全局策略配置
    // #驼峰命名
    // @TableField("user_name") //字段名
    // private String userName;

    // mybatis-plus.configuration.map-underscore-to-camel-case=true
    
    
    // #主键策略
    // @TableId(value = "userId", type = IdType.ID_WORKER_STR)
    // private String userId;
    
    // mybatis-plus.global-config.db-config.id-type=id_worker_str
    
    
    // #表名前缀
    // @TableName(value = "tb_user") //表名
    // public class User {}

    // mybatis-plus.global-config.db-config.table-prefix=tb_


// ##直接获取新插入的 id，勿需额外配置

    // User entity = new User(null, "dong", 35, false);
    // int insert = userMapper.insert(entity);
    // System.out.println("id: " + entity.getUserId());

// #update

    // // UPDATE user SET account=? WHERE userId=?
    // User entity = new User("2", null, 80, false);
    // int update = userMapper.updateById(entity);

    
    // //UPDATE user SET account=? WHERE user_name = ? 
    // //UpdateWrapper: 实体对象封装操作类（可以为 null，里面的 entity 用于生成 where 语句）
    // User entity = new User(null, null, 80, false);
    // UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
    // updateWrapper.eq("user_name", "dong");
    // int update = userMapper.update(entity, updateWrapper); //返回修改成功记录数


// #select
    
    // //SELECT userId,user_name,account FROM user WHERE userId=?
    // User user = userMapper.selectById("1");

    // //SELECT userId,user_name,account FROM user WHERE userId IN ( ? , ? )
    // List<String> idList = Arrays.asList("1", "2");
    // List<User> users = userMapper.selectBatchIds(idList);

    // //SELECT userId,user_name,account FROM user WHERE user_name = ?
    // Map<String, Object> columnMap = new HashMap<>();
    // columnMap.put("user_name", "dong");
    // List<User> users = userMapper.selectByMap(columnMap);

        // // SELECT userId,user_name,account FROM user WHERE user_name = ? 
        // QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // queryWrapper.eq("user_name", "wang");
        // User user = userMapper.selectOne(queryWrapper);

        // // SELECT COUNT( 1 ) FROM user WHERE user_name LIKE ? AND account = ?
        // QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // queryWrapper.like("user_name", "li").eq("account", 35);
        // Integer count = userMapper.selectCount(queryWrapper);


        // // SELECT userId,user_name,account FROM user WHERE user_name LIKE ? AND account = ?
        // QueryWrapper<User> queryWrapper=new QueryWrapper<>();
        // queryWrapper.like("user_name", "li").eq("account", 35);
        // List<User> users = userMapper.selectList(queryWrapper);

        // // ？？？？ 物理分页 --> 不使用！！！
        // // SELECT userId,user_name,account FROM user WHERE user_name LIKE ?
        // IPage<User> page = new Page<>(1, 2);
        // QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // queryWrapper.like("user_name", "li");
        // IPage<User> userIPage = userMapper.selectPage(page, queryWrapper);
        
        // // 同上
        // IPage<User> page = new Page<>(1, 2);
        // QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // queryWrapper.like("user_name", "li");
        // IPage<Map<String, Object>> mapIPage = userMapper.selectMapsPage(page, queryWrapper);


// #delete

        // int delete = userMapper.deleteById(id);
        // int delete = userMapper.deleteBatchIds(idList);

        // // DELETE FROM user WHERE user_name = ?
        // QueryWrapper<User> wrapper = new QueryWrapper<>();
        // wrapper.eq("user_name", "lin");
        // int delete = userMapper.delete(wrapper);


// ##条件构造器

    // 使用的是数据库字段，而不是java属性



#代理
    代理模式是Java设计模式中的一种，其特征为代理类与委托类有同样的接口，
    代理类主要负责为委托类预处理消息、过滤消息、把消息转发给委托类，以及事后处理消息等。
    
    代理类与委托类之间通常存在关联关系，一个代理类的对象与一个委托类的对象关联，代理类的对象本身并不真正实现业务，
    而是通过调用委托类对象的相关方法来提供具体业务。

    在Java中的 java.lang.reflect 包下提供了一个Proxy类和一个InvocationHandler接口，
    通过这个类和接口可以生成JDK动态代理或动态代理对象。
    
    #按照代理的创建时间不同，可以分为两种：
    静态代理：手动创建，再对其编译。在程序运行前，代理类的.class文件就已经存在。
    动态代理：在程序运行时，通过反射机制动态创建而成。


    #动态代理的实现原理有些类似于过滤器的实现原理，但有所不同。
    动态代理的代理类与委托类之间的关系更像是明星与经纪人之间的关系，也就是说，如果你想找某个明星演出的话，
    并不是找他本人，而是找到他的经纪人就可以了。动态代理的实现过程很类似于这个过程，具体请看下图：

    #Proxy类是Java的 java.lang.reflect 包下提供的，该类用于创建动态代理类和代理对象的静态方法，它也是所有动态代理类的父类。
    如果在程序中为一个或多个接口动态地生成实现类，就可以用Proxy类来创建动态代理类；
    
    如果需要为一个或多个接口动态地创建实例，也可以使用Proxy类来创建动态代理实例。

    //创建一个动态代理类所对应的Class对象，该代理类将实现interfaces所指定的多个接口。
    //第一个ClassLoader参数指定生成动态代理类的类加载器。
    static Class<?> getProxyClass(ClassLoader loader, Class<?>... interfaces)
    
    //直接创建一个动态代理对象，该代理对象的实现类实现了 interfaces 指定的系列接口，
    //执行代理对象的每个方法时都会被替换执行 InvocationHandler 对象的 invoke() 方法。
    static Object newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h)

    
    #InvocationHandler

    InvocationHandler 接口提供了invoke()方法，用于替换代理对象的每一个方法。
    真实业务类可以通过代理类对象调用 InvocationHandler 接口提供的 invoke() 方法，来替代调用委托类的真实方法。

    //在代理实例上处理方法调用并返回结果。在与方法关联的代理实例上调用方法时，将在调用处理程序上调用此方法。
    //proxy   表示代理类对象，也就是Proxy.newProxyInstance()方法返回的对象，通常用不上。
    //method  表示当前被调用方法的反射对象，
    //args    表示调用目标方法时传入的实参。
    Object invoke(Object proxy, Method method, Object[] args)
    
        public Object proxyInstance() {
            /**
             * @param loader     目标对象的类加载器
             * @param interfaces 目标对象实现的接口列表
             * @param h          目标对象执行的方法
             * @return a proxy instance with the specified invocation handler of a
             * proxy class that is defined by the specified class loader
             * and that implements the specified interfaces
             */
            return Proxy.newProxyInstance(
                    target.getClass().getClassLoader(),
                    target.getClass().getInterfaces(),
                    (proxy, method, args) -> {
                        System.out.println("代理老师--上课");

                        /**
                         * 执行动态代理对象的所有方法时,都会被替换成执行下面的invoke()方法.
                         * proxy   代表动态代理对象.
                         * method  代表正在执行的方法.
                         * args    代表调用目标方法时传入的实参.
                         */
                        Object invoke = method.invoke(target, args);
                        System.out.println("代理老师--下课");
                        return invoke;
                    });
        }

    #动态代理的作用
        通过Java提供的Proxy类和InvocationHandler接口生成的动态代理类，
        可以阻止调用委托类的方法、过滤参数及修改对应方法的返回值等作用。实现业务接口方法的实现类即委托类，具体操作如下：



#初始化数据--jpa
spring:
 datasource:
 schema: classpath:db/schema.sql
 data: classpath:db/data.sql
 sql-script-encoding: utf-8
 jpa:
 hibernate:
 ddl-auto: none
schema ：脚本中创建表的语句
data ：脚本中初始化数据的预计
sql-script-encoding：设置脚本的编码
Spring Boot 项目启动的时候会自动执行脚本。

ddl-auto 四个值的解释

create： 每次加载hibernate时都会删除上一次的生成的表，然后根据你的model类再重新来生成新表，哪怕两次没有任何改变也要这样执行，这就是导致数据库表数据丢失的一个重要原因。
create-drop ：每次加载hibernate时根据model类生成表，但是sessionFactory一关闭,表就自动删除。
update：最常用的属性，第一次加载hibernate时根据model类会自动建立起表的结构（前提是先建立好数据库），以后加载hibernate时根据 model类自动更新表结构，即使表结构改变了但表中的行仍然存在不会删除以前的行。要注意的是当部署到服务器后，表结构是不会被马上建立起来的，是要等 应用第一次运行起来后才会。
validate ：每次加载hibernate时，验证创建数据库表结构，只会和数据库中的表进行比较，不会创建新表，但是会插入新值。 5、 none : 什么都不做。


在生产中，这两种模式都建议慎用！



#策略设计模式
    //1.过滤出list中长度>5的
    //2.过滤出list中包含a的
    //3....N多情况...
    List<String> list = Arrays.asList("java", "scala", "python");

    public interface Filterable<T> {//策略接口
        boolean filter(T t);
    }

    public List<String> filterList(List<String> list, Filterable<String> filter) {
        List<String> res = new ArrayList<>();
        for (String str : list) {
            if (filter.filter(str)) { //使用策略
                res.add(str);
            }
        }
        return res;
    }

    //lambda前
    List<String> res = filterList(list, new Filterable<String>() {

        @Override
        public boolean filter(String t) {
            return t.length() > 3; //不同过滤条件,不同策略
        }
    });
    res.forEach(t -> System.out.println(t));

    //lambda后
    List<String> res = filterList(list, t -> t.length() > 3);
    res.forEach(System.out::println);



#如何设计API接口，实现统一格式返回？
https://cloud.tencent.com/developer/article/1443918



#异步通知-支付成功
    1.验证签名（sdk已完成）
    2.支付的状态（sdk已完成）
    3.支付金额
    4.支付人（下单人与支付人是同一个？根据需求而定）


#web
    #redirect 跳转时，
    最好使用绝对地址: http://192.168.8.7:8090/sell/order/list
    不要使用相对地址: /sell/order/list

    #清除 cookie
    Cookie cookie = new Cookie(name, value); //清除时, value 设为null
    cookie.setPath("/");
    cookie.setMaxAge(maxAge); //清除时,设为 0
    response.addCookie(cookie); //HttpServletResponse





<!--(1).使用此标签后必须配置 <mvc:annotation-driven />，否则会造成所有的 @Controller 注解无法解析，导致404错误-->
<!--(2).如果请求存在处理器，则这个标签对应的请求处理将不起作用。因为请求是先去找处理器处理，如果找不到才会去找这个标签配置-->
<mvc:view-controller path="/toView" view-name="view"/>
<mvc:annotation-driven />


3-3
    在url声明中使用正则表达式，约束传参类型

    @JsonView 控制json输出内容
    展示用户列表时，不需要显示密码。查询个人信息时，展示用户密码



#Spring Security技术栈开发企业级认证与授权@
3-1.
    url描述资源。 传统url描述对资源的操作行为
    使用HTTP请求方式（GET POST PUT DELETE）描述行为。使用HTTP状态码来表示不同的结果
    使用json交互数据
    restful只是一种风格，并不是强制的标准
    
    

支付开闸-outTradeNo: 本地-p180929163009_3_ 1368442 _192934 轮询推送-p180929163009_0_ 1368842 _192949









