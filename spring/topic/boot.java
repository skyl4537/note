    http://start.spring.io/
    
//{--------<<<基础概念>>>----------------------------X
https://start.spring.io/

#SpringBoot 并不是对 Spring 功能上的增强,而是提供了一种快速使用 Spring 的方式.
    简化依赖管理
        将各种功能模块进行划分,封装成一个个启动器(Starter), 更容易的引入和使用
        提供一系列的Starter,将各种功能性模块进行了划分与封装
        更容易的引入和使用,有效避免了用户在构建传统Spring应用时维护大量依赖关系而引发的jar冲突等问题

    自动化配置 //为每一个Starter都提供了自动化的java配置类
    嵌入式容器 //嵌入式tomcat,无需部署war文件
    监控の端点 //通过Actuator模块暴露的http接口,可以轻松的了解和控制 Boot 应用的运行情况
    
#Boot启动器(jar包集合,一共44个)
    spring-boot-starter-web        //支持全栈式的 web 开发,包括 tomcat 和 SpringMVC 等jar包
    spring-boot-starter-jdbc    //支持 Spring 以 jdbc 方式操作数据库的jar包的集合
    spring-boot-starter-redis    //支持 redis 键值存储的数据库操作
    
//}
    
//{--------<<<2.x注意点>>>---------------------------X
#不重新打包的前提下,修改配置文件
    1.打包直接执行
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            
            //此配置将使得 jar/war 包在linux环境下可直接执行
            //勿需命令 jar -jar *.jar, 直接 ./*.jar 即可执行
            <configuration> 
                <executable>true</executable>
            </configuration>
        </plugin>
        
    2.可执行jar包存在的弊端
        以上设置,虽可在linux下直接执行, 但在不重新打包前提下修改配置文件,则做不到.        

#匹配带后缀url访问
    @Configuration
    public class MyWebMvcConfigurer implements WebMvcConfigurer {
        @Override
        public void configurePathMatch(PathMatchConfigurer configurer) {
            //boot2.x默认将'/test'和'/test.do'作为2个url
            configurer.setUseRegisteredSuffixPatternMatch(true); //true,统一以上两个url
        }
    }
    
    @Bean
    public ServletRegistrationBean servletRegistrationBean(DispatcherServlet dispatcherServlet) {
        ServletRegistrationBean<DispatcherServlet> bean = new ServletRegistrationBean<>(dispatcherServlet);
        
        bean.addUrlMappings("*.do"); //拦截'.do'结尾的url
        return bean;
    }

//}

//{--------<<<启动>>>--------------------------------X
#两种启动
    1.脚本启动
        #!/bin/bash
        PID=$(lsof -t -i:8090)
        
        if [ $PID ]
        then
            kill -9 $PID
            echo "kill -9 port 8090 PID: $PID"
        else
            echo "8090 NO PID!"
        fi

        cd /var/tmp
        chmod 777 demo.jar
        nohup jdk1.8.0_191/bin/java -jar demo.jar >/dev/null 2>&1 &
        echo "start OK!~!"

    2.linux服务启动
        //pom.xml设置
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <executable>true</executable> //可执行,必不可少. 将导致jar包不可修改
            </configuration>
        </plugin>
    
        //将jar包部署到linux, 并赋予可执行权限
        chmod +x /var/tmp/blue/demo.jar
        
        //将jar包软连接到 /etc/init.d 目录. 其中, /etc/init.d/demo 结尾 demo 为该服务的别名
        ln -s /var/tmp/blue/demo.jar /etc/init.d/demo
        
        //通过linux服务命令形式 启动/关闭/重启/查询 该服务
        service demo start|stop|restart|status
            
        //该服务日志默认的存储路径: /var/log/demo.log
        //使用自定义 *.conf 更改默认配置, jar包同路径下新建配置文件 demo.conf
        JAVA_HOME=/usr/jdk1.7.0_79/bin
        JAVA_OPTS=-Xmx1024M
        LOG_FOLDER=/var/tmp/blue/logs/        //该目录必须存在

//}

//{--------<<<login>>>-------------------------------X
#webjars -> 将前端资源(js,css等)打成jar包,使用Maven统一管理. http://www.webjars.org/
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>webjars-locator</artifactId> //页面引用时,可省略版本号.(如 3.3.1)
            <version>0.32</version>
        </dependency>
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>bootstrap</artifactId>
            <version>3.3.7-1</version>
        </dependency>
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>jquery</artifactId>
            <version>3.3.1</version>
        </dependency>
    
#前台表单
    <!DOCTYPE html>
    <html lang="en" xmlns:th="http://www.thymeleaf.org"> //声明 thymeleaf
    <head>
        <meta charset="UTF-8">
        <title>login</title>
        <link rel="shortcut icon" th:href="@{/favicon.ico}"/> //小叶子,存放目录 /static
        
        //webjars-locator: 页面引用时,可省略版本号.(如 3.3.1)
        //省略前: <script th:src="@{/webjars/jquery/3.3.1/jquery.min.js}"></script>
        //....后: <script th:src="@{/webjars/jquery/jquery.min.js}"></script>

        <script th:src="@{/webjars/jquery/jquery.min.js}"></script>
        <script th:src="@{/webjars/bootstrap/js/bootstrap.min.js}"></script>
        <link rel="stylesheet" th:href="@{/webjars/bootstrap/css/bootstrap.min.css}"/>
    </head>    
    <body>
        <form method="post" th:action="@{/login}"> //表单提交: post + action
            <table align="center">
                <tr><td>账户: <input type="text" th:name="name" th:value="${name}"></td></tr> //${name} -> 无值则显示""
                <tr><td>密码: <input type="password" th:name="pwd"></td></tr>
                <tr>
                    <td align="center">
                        <button class="btn btn-primary" type="submit">登录</button>
                        <p th:if="${! #strings.isEmpty(msg)}" th:text="${msg}"></p> //th:if -> msg为空则不显示
                    </td>
                </tr>
            </table>
        </form>
    </body>
    
#后台逻辑
    @PostMapping("/login")
    public String login(@RequestParam String name, @RequestParam String pwd, HttpSession session, Model model) {
        if (!StringUtils.isEmpty(pwd)) {
            session.setAttribute("user", name); //保存Session,用于登陆验证
            return "redirect:/emp/emps"; //重定向到接口 --> 防止表单重复提交! ---> 中间不能有空格!!

        } else {
            model.addAttribute("name", name); //表单回显
            model.addAttribute("msg", "用户名或密码不正确!");
            return "/login"; //转发到页面: /templates/login.html
        }
    }

#登陆拦截
    public class LoginInterceptor implements HandlerInterceptor {

        @Override //在目标方法之前被调用 ---> 适用于权限,日志,事务等.
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            if (null != request.getSession().getAttribute("user")) {
                return true; //有Session,则继续调用后续的拦截器和目标方法; 没有,则转发到登录页
            } else {
                request.getRequestDispatcher("/").forward(request, response);
                return false;
            }
        }
    }
    
#注册拦截器
    @Configuration
    public class MyWebMvcConfigurer implements WebMvcConfigurer {

        @Override //静态资源映射
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            //url访问: x/logs/demo.log --> 类路径/resources/log/demo.log (或) war包所在目录/logs/demo.log
            registry.addResourceHandler("/logs/**")
                    .addResourceLocations("classpath:/log/", "file:logs/");
        }

        @Override //视图映射
        public void addViewControllers(ViewControllerRegistry registry) {
            //url访问: ip:port/demo/ --> 对应资源: /templates/login.html
            registry.addViewController("/").setViewName("/login");
        }

        @Override //注册拦截器
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new LoginInterceptor())
                    .addPathPatterns("/**")
                    .excludePathPatterns("/webjars/**", "/*.html", "/img/**", "/logs/**") //非拦截: 静态资源
                    .excludePathPatterns("/", "/login"); //非拦截: 登陆接口
        }
    }
    
// #静态资源映射
        // classpath:/public/
        // classpath:/resources/
        // classpath:/static/
        // classpath:/META-INFO/resouces/
        
    // 0.默认存放在以上目录的资源都可以直接访问
        // classpath:/static/a.html        ---> http://127.0.0.1:8090/demo/a.html
        // classpath:/static/abc/c.html    ---> http://127.0.0.1:8090/demo/abc/c.html
        // classpath:/static/img/sql.png    ---> http://127.0.0.1:8090/demo/img/sql.png
        
    // 1.自定义静态资源目录//代码版
        // classpath:/log/demo.log            ---> http://127.0.0.1:8090/demo/logs/demo.log
        // jar包同级目录/logs/test.log        ---> http://127.0.0.1:8090/demo/logs/test.log
        
        // @Configuration
        // public class MyWebMvcConfigurer implements WebMvcConfigurer {
            // @Override
            // public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // //url访问: x/logs/demo.log --> 类路径/resources/log/demo.log (或) war包所在目录/logs/demo.log
                // registry.addResourceHandler("/logs/**")
                        // .addResourceLocations("classpath:/log/", "file:logs/");
            // }
        // }
        
    // 3.自定义静态资源目录//配置文件版
        // spring.mvc.static-path-pattern=/logs/**
        // spring.resources.static-locations=classpath:/log/,file:logs/
        // //此配置会覆盖boot默认配置,即导致不能再访问 /static; /public/, /resources/等目录资源

//}

//{--------<<<restful>>>-----------------------------X
#restful是对于同一个服务器资源的一组不同的操作,包括: GET/POST/PUT/DELETE/PATCH/HEAD/OPTIONS
    
1.http请求的安全和幂等
安全 -> 请求不会影响资源的状态. 只读的请求: GET,HEAD,OPTIONS
幂等 -> 多次相同的请求,目的一致.
        
GET /emp/list --> 只读请求,不改变资源状态. //安全,幂等.

PUT /emp/5    --> 多次请求都是将id为 5 的员工姓名修改成'wang'. //不安全,幂等.

POST /emp     --> 多次请求会新增多条相同的数据. //不安全,不幂等.

DELETE /emp/5 --> 多次请求目的都是删除id为 5 的员工. //不安全,幂等.
///注意: 第一次成功删除,第二次及以后,虽资源已不存在,但也得返回 200 OK,不能返回 404.
        


                
//}

//{--------<<<CRUD>>>--------------------------------X

    | 列表页面        | /emp/list    | GET      |
    | 跳转页面(新增)  | /emp        | GET      |
    | 新增接口        | /emp        | POST     |
    | 跳转页面(修改)  | /emp/{id}    | GET      |
    | 修改接口        | /emp        | PUT      |
    | 删除接口        | /emp/{id}    | DELETE   |
    
#POST转化为PUT,DELETE
    1.配置HiddenHttpMethodFilter. (boot已自动配置)
        <filter>
            <filter-name>HiddenHttpMethodFilter</filter-name>  
            <filter-class>org.springframework.web.filter.HiddenHttpMethodFilter</filter-class>  
        </filter>
        
    2.页面创建(POST表单 + 隐藏标签)
        <form method="post" th:action="@{/emp/}+${emp.id}">
            <input type="hidden" name="_method" value="delete"> //隐藏标签 name + value
            
            <a href="#" onclick="delEmp(this)" th:attr="url=@{/emp/}+${emp.id}">删除</a>
        </form>
    
#列表
    0.跳转列表页面
        <a th:href="@{/emp/list}">列表页面</a> //超链接对应请求 GET
        
    1.跳转逻辑
        @RequestMapping("/emp/list")
        public String list(Model model) {
            model.addAttribute("emplist", EmpUtils.listAll());
            return "/emp/list";
        }
        
    2.响应页面
        <table border="1" cellpadding="5" cellspacing="0" align="center">
            <tr>
                <th>姓名</th>
                <th>年龄</th>
                <th>城市</th>
                <th>操作</th>
            </tr>
            <tr th:if="${null==emplist || 0==emplist.size()}">
                <td colspan="4" th:text="列表为空"></td>
            </tr>
            <tr th:each="emp:${emplist}" th:object="${emp}"> //配合使用 th:object 和 *{...}
                <td th:text="*{name}"></td>
                <td th:text="*{gender?'男':'女'}"></td>
                <td th:text="*{city.name}"></td>
                <td colspan="2">
                    <a th:href="@{/emp/}+*{id}">修改</a>
                    <a href="#" onclick="delEmp(this)" th:attr="url=@{/emp/}+${emp.id}">删除</a>
                </td>
            </tr>
        </table>
    
#新增
    0.跳转新增页面
        <a th:href="@{/emp}">新增</a>
        
    1.跳转逻辑
        @RequestMapping("/emp")
        public String add(Model model) {
            model.addAttribute("cityList", EmpUtils.listCity()); //初始化列表 City
            return "/emp";
        }
        
    2.新增页面(同修改)
        
    3.新增接口
        @PostMapping("/emp")
        public String add(Emp emp) {
            EmpUtils.empList.add(emp);
            return "redirect:/emp/list";
        }
        
#修改
    0.跳转修改页面
        <a th:href="@{/emp/}+${emp.id}">修改</a> //路径拼接
        
    1.跳转逻辑
        @GetMapping("/emp/{id}")
        public String add(@PathVariable Integer id, Model model) {
            model.addAttribute("emp", EmpUtils.getById(id));
            model.addAttribute("cityList", EmpUtils.listCity());
            return "/emp";
        }
        
    2.回显数据修改页面
        //增加和修改使用同一页面,区分方式: 回显 emp 是否为空 --> ${null!=person}
        <form method="post" th:action="@{/emp}">
            //修改: PUT请求 + emp.id
            <input type="hidden" name="_method" value="put" th:if="${null!=emp}">
            <input type="hidden" name="id" th:if="${null!=emp}" th:value="${emp.id}">

            <table>
                <tr>
                    <td>姓名:</td>
                    <td><input type="text" name="name" th:value="${null!=emp}?${emp.name}"></td>
                </tr>
                <tr>
                    <td>性别:</td> //th:checked --> radio标签是否选中.
                    <td><input type="radio" name="gender" value="1" th:checked="${null!=emp}?${emp.gender}">男
                        <input type="radio" name="gender" value="0" th:checked="${null!=emp}?${!emp.gender}">女
                    </td>
                </tr>
                <tr>
                    <td>住址:</td>
                    <td><select name="city.id"> //th:selected --> 回显emp.city.id == 遍历city.id,则选中
                        <option th:each="city:${cityList}" th:object="${city}" th:value="*{id}" th:text="*{name}"
                                th:selected="${null!=emp}?${emp.city.id}==*{id}"></option>
                    </select></td>
                </tr>
                <tr> //回显 emp 为空,则显示'新增'; 否则,显示'修改'.
                    <td colspan="2"><input type="submit" th:value="${null!=emp}?'修改':'新增'"></td>
                </tr>
            </table>
        </form>
    
    3.修改接口
        @PutMapping("/emp")
        public String updateById(Emp emp) {
            EmpUtils.empList.update(emp);
            return "redirect:/emp/emps";
        }
    
#删除
    0.点击删除//(DELETE请求需要: <form/> + 隐藏标签)
        <a href="#" onclick="delEmp(this)" th:attr="url=@{/emp/}+${emp.id}">删除</a>

        <form id="delForm" method="post" action="#"> //独立于列表Table的<form/>
            <input type="hidden" name="_method" value="DELETE">
        </form>
        
        <script>
            function delEmp(e) {
                alert($(e).attr('url')); //当前按钮的'url'属性
                $('#delForm').attr('action', $(e).attr('url')).submit(); //动态设置<form>的action属性,并提交
                return false; //取消按钮的默认行为
            }
        </script>

    1.删除逻辑
        @DeleteMapping("/emp/{id}")
        public String delete(@PathVariable Integer id) {
            EmpUtils.empList.deleteById(id);
            return "redirect:/emp/list";
        }
        
    3.升级版 //不使用<form/>发送DELETE,使用ajax异步删除
        <a href="#" onclick="delEmp(this)" th:attr="url=@{/emp/}+*{id}">删除</a>
        
        <script>
            function delEmp(e) {
                $.ajax({
                    type: 'DELETE', //仅部分浏览器支持
                    url: $(e).attr('url'),
                    dataType: 'text',
                    success: function (data) { //请求成功,回调函数
                        $(e).parent().parent().remove(); //--->动态删除<a/>所在的行
                    },
                    error: function (data) { //发生错误时调用
                        var res = JSON.parse(data.responseText); //转化json
                        alert(res.status + " + " + res.error + " + " + res.message);
                    }
                });
                return false;
            }
        </script>
        
    4.ajax后台逻辑
        @DeleteMapping("/emp/{id}")
        @ResponseBody
        public String delete(@PathVariable Integer id) {
            EmpUtils.empList.deleteById(id);
            return "success";
        }
        
//}

    
//{--------<<<thymeleaf>>>---------------------------X
    模板引擎:'将后台数据 填充 到前台模板的表达式中!' //thymeleaf; freemarker; jsp; velocity;

#sts代码提示
    0).下载STS插件: https://github.com/thymeleaf/thymeleaf-extras-eclipse-plugin/releases
    1).在STS安装目录dropins下新建文件夹: thymeleaf-2.1.2
    2).只将压缩包中的 features 和 plugins 文件夹拷贝到以上目录并重启eclise!!!
    3).在thymeleaf的html页面引入命名空间: 
        <html lang="en" xmlns:th="http://www.thymeleaf.org">
        
#templates
    //thymeleaf 模板文件存放位置: src/main/resources/templates
    templates 目录是安全的. 意味着该目录下的内容不允许外界直接访问,必须经过服务器的渲染.
    
#低版本异常
    #org.xml.sax.SAXParseException: 元素类型 "meta" 必须由匹配的结束标记 "</meta>" 终止
    #这是由于低版本对于html语法解析比较严格,必须有头有尾.
    
    (1).html标记按照严谨的语法去编写
        <meta charset="UTF-8" />
        
    (2).升级为高版本
        //Thymeleaf.jar: 更新为 3.0 以上的版本
        //thymeleaf-layout-dialect.jar: 更新为 2.0 以上的版本
        <properties>
            <java.version>1.8</java.version>
            <thymeleaf.version>3.0.2.RELEASE</thymeleaf.version>
            <thymeleaf-layout-dialect.version>2.0.4</thymeleaf-layout-dialect.version>
        </properties>
        
#常用符号
    #~{...}    ---> 片段引用表达式
    
    #@{...}    ---> 定义URL
        //http://ip:8080/order/details/3
        <a href="emp" th:href="@{/details/}+${emp.id}">相对路径-传参-restful</a>
    
        //http://ip:8080/order/details?orderId=3
        <a th:href="@{http://ip:8080/order/details(orderId=${o.id})}">绝对路径-传参</a>

        //http://ip:8080/order/details?orderId=3
        <a th:href="@{/details(orderId=${o.id})}">相对路径-传参</a>

        //http://ip:8080/order/3/details?orderId=3 ---> 同上
        <a th:href="@{/{orderId}/details(orderId=${o.id}, orderName=${o.name})}">相对路径-传参-restful</a>
    
    #${...}    ---> 变量值
        (1).获取对象的属性,调用方法; //${person.name}
        (2).使用内置的基本对象; //${! #strings.isEmpty(msg)} https://www.cnblogs.com/xiaohu1218/p/9634126.html
        (3).内置的一些工具对象; //
        
    ##{...}    ---> 用于获取 properties 文件内容,常用于'国际化'场景
        home.welcome=this messages is from home.properties! //properties文件
        
        <p th:text="#{home.welcome}">This text will not be show!</p> //读取properties文件中的 home.welcome

    ##maps    ---> 工具对象表达式; #dates #calendars #numbers #strings #objects #bools #arrays #lists #sets
        //有msg对象则显示<p>; 反之不显示
        <p style="color:red" th:text="${msg}" th:if="${not #strings.isEmpty(msg)}" />
        
    #*{...}    ---> 类似${}功能, 配合th:object使用,获取指定对象的变量值
        <div> //(1).类似${}功能
            <p>Name: <span th:text="*{session.user.name}">Sebastian</span>.</p>
            <p>Surname: <span th:text="*{session.user.surname}">Pepper</span>.</p>
            <p>Nationality: <span th:text="*{session.user.nationality}">Saturn</span>.</p>
        </div>
        
        <div> //(2-1).原始表达式
            <p>Name: <span th:text="${session.user.firstName}">Sebastian</span>.</p>
            <p>Surname: <span th:text="${session.user.lastName}">Pepper</span>.</p>
            <p>Nationality: <span th:text="${session.user.nationality}">Saturn</span>.</p>
        </div>
    
        <div th:object="${session.user}"> //(2-2).指定对象
            <p>Name: <span th:text="*{firstName}">Sebastian</span>.</p>
            <p>Surname: <span th:text="*{lastName}">Pepper</span>.</p>
            <p>Nationality: <span th:text="*{nationality}">Saturn</span>.</p>
        </div>

        <div th:object="${session.user}"> //(2-3).混合使用
            <p>Name: <span th:text="*{firstName}">Sebastian</span>.</p> //指定对象
            <p>Surname: <span th:text="${session.user.lastName}">Pepper</span>.</p> //上下文变量取值
            <p>Nationality: <span th:text="${#object.nationality}">Saturn</span>.</p> //从 #object 中获取属性
        </div>
    
#th:href
    <a th:href="@{/emp}">用户添加</a> //发送get请求到 '/项目名/emp'
    
#th:src -> 图片类地址引入
    <img alt="App-Logo" th:src="@{/img/logo.png}" />

#th:id
    <div th:id = "stu+(${rowStat.index}+1)" class="student"></div> //动态指定id属性
    
#th:each
    //每次遍历都会产生当前标签<tr>
    <tr th:each="item : ${list}">
        <td th:text="${item.name}">name</td>
        <td>[[${item.price}]]</td>
        <td>[(${item.production})]</td>
        <td th:text="${itemStat.odd}?'odd':${item.memo}">memo</td>
    </tr>
    
    //(1).itemStat 称作状态变量,属性有:
        index        -> 当前迭代对象的索引. (从0开始)
        count        -> 当前迭代对象的计数. (从1开始)
        size        -> 迭代集合的长度
        current        -> 当前迭代变量
        even/odd    -> 布尔值,当前循环是否是偶数/奇数. (从0开始)
        first/last    -> 布尔值,当前循环是否是第一个/最后一个

    //(2).用法: ${itemStat.index}; ${itemStat.odd}; ${itemStat.current.name}... ...
    
    //(3).在写 th:each="obj,objStat:${objList}",可不写 objStat,自动添加,默认命名 objStat (如itemStat)
    
#th:value
    //可以将一个值放入到 input 标签的 value 中
    
#th:text
    <div th:text="${emp.name}">将被替换</div> //一般写法
    <div>[[${emp.name}]]</div> //行内写法行内写法
    
    //[[...]] & [(...)]
    <p>The message is "[[${msg}]]"</p> //会转义 <==> th:text
    <p>The message is "This is &lt;b&gt;great!&lt;/b&gt;"</p>

    <p>The message is "[(${msg})]"</p> //不会转义 <==> th:utext
    <p>The message is "This is <b>great!</b>"</p>

    //禁用行内写法(禁用内联)
    <p th:inline="none">A double array looks like this: [[1, 2, 3], [4, 5]]!</p> 
    
    //js内联
    <script th:inline="javascript">
        ...
        var username = [[${session.user.name}]];
        ...
    </script>
    
    //css内联
    <style th:inline="css">
        .[[${classname}]] {
          text-align: [[${align}]];
        }
    </style>
    
#th:action -> 表单提交的地址
    //对于操作符'@',模板解析时会自动加上 'context-path' 作为前缀
    <form th:action="@{/batch/upload}" method="post" enctype="multipart/form-data">
        File: <input type="file" name="file"><br>
        Desc: <input type="text" name="desc"><br>
        <input type="submit" value="提交">
    </form>
    
#th:selected -> selected选择框,选中情况
    <td>住址:</td> //th:text -> 用于显示; th:value -> 用于存值. 
    <td><select name="city.id"> //th:selected -> 回显对象person.city.id和遍历city.id相同,则选中
        <option th:each="city : ${cityList}" th:value="${city.id}" th:text="${city.name}"
                th:selected="${null!=person}?${person.city.id}==${city.id}"></option>
    </select></td>

    <td><select name="city.id"> //配合使用 th:object
        <option th:each="city : ${cityList}" th:object="${city}" th:value="*{id}" th:text="*{name}"
                th:selected="${null!=person}?${person.city.id}==*{id}"></option>
    </select></td>
    
#th:if -> 条件判断
    <p th:if="${! #strings.isEmpty(msg)}" th:text="${msg}"></p> //msg不为空,则显示<p>
    
    ///th:if="${xx}" 表达式为 true 的各种情况:
        boolean xx =true; int xx !=0; character xx !=0; 
        String xx !="false","off","no";
        If xx is not a boolean, a number, a character or a String
    
#th:switch -> 多路选择,配合使用 th:case 
    <div th:switch="${user.role}">
        <p th:case="'admin'">管理员</p>
        <p th:case="#{roles.manager}">管理者</p>
        <p th:case="*">其他</p> //默认值
    </div>
    
#th:with -> 变量赋值运算 
    <div th:with="first=${persons[0]}"> //th:with="x=${y}"
        <p>    The name of the first person is <span th:text="${first.name}">Julius Caesar</span>.</p>
    </div>
    
#th:attr -> 设置标签属性,多个属性用逗号分隔
    th:attr="src=@{/image/aa.jpg},title=#{logo}" //一般用于自定义标签
    
#th:remove -> 删除某个属性
    <tr th:remove="all"> //all:删除包含标签和所有的孩子
    
#th:errors
    数据校验相关; 用于显示数据校验的错误信息.

#日期格式化
    <input type="text" name="birth" placeholder="birth day"
        th:value="${#dates.format(emp.birth, 'yyyy-MM-dd HH:mm:ss')}">
        
        // 格式化日期,默认以浏览器默认语言为格式化标准
        ${#dates.format(key)}
        ${#dates.format(key,'yyy/MM/dd')}
        
        // 按照自定义的格式做日期转换.(取值年月日)
        ${#dates.year(key)}
        ${#dates.month(key)}
        ${#dates.day(key)}
        
#字符串相关
    1.调用内置对象一定要用#
    2.大部分的内置对象都以s结尾: strings, numbers, dates
        // 判断字符串是否为空,如果为空返回 true,否则返回 false
        ${#strings.isEmpty(key)}
        
        // 判断字符串是否包含指定的子串,如果包含返回 true,否则返回 false
        ${#strings.contains(msg, 'T')}
        
        // 判断当前字符串是否以子串开头,如果是返回 true,否则返回 false
        ${#strings.startsWith(msg, 'a')}
        
        // 判断当前字符串是否以子串结尾,如果是返回 true,否则返回 false
        ${#strings.endsWith(msg, 'a')}
        
        // 返回字符串的长度
        ${#strings.length(msg)}
        
        // 查找子串的位置,并返回该子串的下标,如果没找到则返回-1
        ${#strings.indexOf(msg, 'h')}
        
        // 截取子串,用法与 jdk String 类下 SubString 方法相同
        ${#strings.substring(msg, 13)}
        ${#strings.substring(msg, 13, 15)}
        
        // 字符串转大小写
        ${#strings.toUpperCase(msg)}
        ${#strings.toLowerCase(msg)}
    
#页面公共元素抽取
    1.抽取公共片段
        <footer th:fragment="copy">
            &copy; 2011 The Good Thymes Virtual Grocery
        </footer>
        
    2.语法
        ~{templatename::selector} //模板名::选择器
        ~{templatename::fragmentname} //模板名::片段名
        //对于一般写法可以省略 ~{}; 但是,对于行内写法不可省! [[~{x::y}]] 或 [(x::y)]
        
    3.不同引入
        <div th:insert="footer :: copy"></div>    //footer为文件名: footer.html

        <div th:replace="footer :: copy"></div>

        <div th:include="footer :: copy"></div>//已过时
        
    4.不同效果
        <div> //insert - div内层才是公共元素
            <footer>
                &copy; 2011 The Good Thymes Virtual Grocery
            </footer>
        </div>
        
        <footer> //replace - 公共元素替换div
            &copy; 2011 The Good Thymes Virtual Grocery
        </footer>
        
        <div> //include - 公共元素
            &copy; 2011 The Good Thymes Virtual Grocery
        </div>
        
#引入页面公共元素时传参(两种方式)
    1.引用fragment时传入'参数名和参数值'
        <div th:replace="comm/bar :: sidebar(activeUri='main.html')"></div> //主页面
    
        <div th:replace="comm/bar :: sidebar(activeUri='logDir')"></div> //目录页面

    2.定义fragment时指定'参数名'
        <nav th:fragment="sidebar(activeUri)">
        
    2.引用fragment时传入'参数值'
        <div th:replace="comm/bar :: sidebar('main.html')"></div>
        
        <div th:replace="comm/bar :: sidebar('logDir')"></div>
    
    3.都是根据传入参数值改变a的class属性
        <a th:href="@{/main.html}" //activeUri为 'main.html' 时,高亮
            th:class="${activeUri=='main.html'?'nav-link active':'nav-link'}">
    
        <a th:href="@{/logDir}" //activeUri为 'logDir' 时,高亮
            th:class="${activeUri=='logDir'?'nav-link active':'nav-link'}">>
            
#域对象操作
    0.HttpServletRequest
        // request.setAttribute("req", "hello");
        Request: <span th:text="${#httpServletRequest.getAttribute('req')}"></span><br/>
        
    1.HttpSession
        // request.getSession().setAttribute("sess", "world");
        Session: <span th:text="${session.sess}"></span><br/>
        
    2.ServletContext
        // request.getServletContext().setAttribute("app", "java");
        Application: <span th:text="${application.app}"></span>
    
//}

//{--------<<<WebSocket>>>---------------------------X
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>    
    
#ABC
B/S结构的软件项目中有时客户端需要实时的获得服务器消息，但默认HTTP协议只支持 '请求响应模式'。 
对于这种需求可以通过 polling， Long-polling， 长连接， Flash-Socket， HTML5中定义的WebSocket 完成。

HTTP模式可以简化Web服务器，减少服务器的负担，加快响应速度，
因为服务器不需要与客户端长时间建立一个通信链接。
但不容易直接完成实时的消息推送功能(如聊天室，后台信息提示，实时更新数据等)。

#Socket简介
    Socket 又称'套接字',应用程序通过 Socket 向网络发出请求或者应答网络请求.
    Socket 可以使用TCP/IP协议或UDP协议.
    
    //TCP协议:  面向连接的,可靠的,基于字节流的传输层通信协议,负责数据的可靠性传输问题.
    //UDP协议:  无连接,不可靠,基于报文的传输层协议; 优点 ----> 发送后不用管,速度比TCP快.
    
    //HTTP协议: 无状态协议, 通过 Internet 发送请求消息和响应消息, 使用端口接收和发送消息,默认80端口. (底层Socket)

#双向通信
    HTTP协议决定了服务器与客户端之间的连接方式,无法直接实现消息推送(F5已坏),一些变相的解决办法:
    
    1.轮询 
        客户端定时向服务器发送Ajax请求,服务器接到请求后马上返回响应信息并关闭连接.
        
        优点: 后端程序编写比较容易. 
        缺点: 请求中有大半是无用,浪费带宽和服务器资源. 
        实例: 适于小型应用.
        
    2.长轮询 
        客户端向服务器发送Ajax请求,服务器接到请求后hold住连接,直到有新消息才返回响应信息并关闭连接,
        客户端处理完响应信息后再向服务器发送新的请求. 
        
        优点: 在无消息的情况下不会频繁的请求,耗费资小. 
        缺点: 服务器hold连接会消耗资源,返回数据顺序无保证,难于管理维护. Comet异步的ashx,
        实例: WebQQ、Hi网页版、Facebook-IM.

    3.长连接 
        在页面里嵌入一个隐蔵iframe,将这个隐蔵iframe的src属性设为对一个长连接的请求或是采用xhr请求,
        服务器端就能源源不断地往客户端输入数据. 
        
        优点: 消息即时到达,不发无用请求,管理起来也相对便. 
        缺点: 服务器维护一个长连接会增加开销. 
        实例: Gmail聊天
        
    4.Flash—Socket
        在页面中内嵌入一个使用了Socket类的 Flash 程序, JavaScript通过调用此Flash程序提供的Socket接口
        与服务器端的Socket接口进行通信,    JavaScript在收到服务器端传送的信息后控制页面的显示. 
        
        优点: 实现真正的即时通信,而不是伪即时. 
        缺点: 客户端必须安装Flash插件,非HTTP协议,无法自动穿越防火墙. 
        实例: 网络互动游戏.
        
    5.Websocket
        HTML5提供的一种浏览器与服务器间进行全双工通讯的网络技术. 依靠这种技术可以实现客户端和服务器端的长连接,双向实时通信.
        
        //特点: 事件驱动, 异步, 使用ws或者wss协议的客户端socket, 能够实现真正意义上的推送功能,
        //缺点: 少部分浏览器不支持,浏览器支持的程度与方式有区别.
        
        Websocket 允许通过js与远程服务器建立连接,从而实现客户端与服务器间双向的通信.
        
        Websocket 的url开头是ws, 如果需要ssl加密可以使用wss,
        当调用构造方法构建一个 Websocket 对象后,就可以进行即时通信了. ---> new WebSocket(url)
            
#客户端说明
        <body>
            <input id="text" type="text"/>
            <button onclick="send()">Send</button>
            <button onclick="closeWebSocket()">Close</button>
            <div id="message"></div>
        </body>
        <script type="text/javascript">
            var websocket = null;

            //判断当前浏览器是否支持WebSocket
            if (!'WebSocket' in window) {
                alert('Not support websocket')
            } else {
                var userId = parseInt(Math.random() * (99 + 1), 10); //生成[0,99]的任意随机数
                websocket = new WebSocket("ws://localhost:8090/demo/websocket?id=" + userId);

                //监听事件 -> 连接成功建立时触发该事件
                websocket.onopen = function (event) {
                    setMessageInnerHTML("open: " + new Date());
                };

                //监听事件 -> 连接关闭
                websocket.onclose = function (event) {
                    setMessageInnerHTML("close: " + new Date() + " - " + event.code);
                    websocket.send(event.code);
                };

                //监听事件 -> 接收到服务器发来的消息
                websocket.onmessage = function (event) {
                    setMessageInnerHTML(event.data);
                };

                //监听事件 -> 连接发生错误
                websocket.onerror = function () {
                    setMessageInnerHTML("error: " + new Date());
                };

                //监听事件 -> 监听窗口关闭事件,当窗口关闭时,主动去关闭websocket连接,防止连接还没断开就关闭窗口,server端会抛异常
                window.onbeforeunload = function () {
                    if (null != websocket) {
                        websocket.close();
                    }
                };

                //将消息显示在网页上
                function setMessageInnerHTML(innerHTML) {
                    document.getElementById('message').innerHTML += innerHTML + '<br/>';
                }

                //关闭连接
                function closeWebSocket() {
                    websocket.close();
                }

                //向远程服务器发送数据
                function send() {
                    var message = document.getElementById('text').value;
                    websocket.send(message);
                }
            }
        </script>
    
#服务端说明
        @Configuration
        public class WebSocketConfig {

            //这个bean会自动注册使用 @ServerEndpoint 注解声明的 WebSocket-Endpoint.
            //注意: 如果使用独立的servlet容器,而不是直接使用 SpringBoot 内置容器,就不要注入此bean,因为它将由容器自己提供和管理
            @Bean
            public ServerEndpointExporter serverEndpointExporter() {
                return new ServerEndpointExporter();
            }
        }

        // 使用 SpringBoot 要使用注解 @Component
        // 而使用独立容器(tomcat)是由容器自己管理 WebSocket,但在 SpringBoot 中连容器都是 Spring 管理.
        //
        // 虽然 @Component 默认是单例模式的
        // 但 SpringBoot 还是会为每个 WebSocket 连接初始化一个bean,所以可以用一个静态 Set/Map 保存起来.
        @Component

        // 使用注解 @ServerEndpoint 可以将一个普通Java类作为 WebSocket 服务器的端点
        //
        // WebSocket 服务端运行在 ws://[Server端IP或域名]:[Server端口]/项目/push
        // 客户端浏览器已经可以对WebSocket客户端API发起 <<<HTTP长连接>>>.
        //
        // 使用 ServerEndpoint 注解的类必须有一个公共的无参数构造函数.
        @ServerEndpoint("/push")
        public class EchoEndpoint {

            //客户端注册时调用
            @OnOpen
            public void onOpen(Session session) {
            }

            //客户端关闭
            @OnClose
            public void onClose(Session session, CloseReason reason) {
            }

            //客户端异常
            @OnError
            public void onError(Throwable t) {
            }

            //收到浏览器客户端消息后调用
            @OnMessage
            public void onMessage(String message) {
            }

            //更高级的注解,MaxMessageSize 属性可以被用来定义消息字节最大限制,在示例程序中,如果超过6个字节的信息被接收,就报告错误和连接关闭.
            // @Message(maxMessageSize = 6)
            // public void receiveMessage(String s) {
            // }
        }

        
#后台DEMO
        @Component
        @ServerEndpoint(value = "/websocket")
        public class MyWebSocket {
            
            //静态变量,用来记录当前在线连接数. 应该把它设计成线程安全的
            private static int onlineCount = 0;

            //旧版: concurrent包的线程安全Set,用来存放每个客户端对应的 MyWebSocket 对象
            // private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<>();

            //新版: 使用map对象,便于根据 userId 来获取对应的 MyWebSocket
            private static ConcurrentHashMap<String, MyWebSocket> webSocketMap = new ConcurrentHashMap<>();

            //区别: 非静态变量 和 静态变量
            //与某个客户端的连接会话,需要通过它来给客户端发送数据
            private Session session;

            //当前会话session对应的显式id
            private String userId = "";

            //客户端注册
            @OnOpen
            public void onOpen(Session session) {
                String id = this.userId = session.getRequestParameterMap().get("id").get(0);
                this.session = session;
                addOnlineCount(); //在线数加1
                webSocketMap.put(id, this); //加入Map
                System.out.println("有新连接加入: " + this.userId + " 当前在线人数为: " + getOnlineCount());

                sendMsg2All(this.userId + " - 已上线! 欢迎");
            }

            //客户端关闭
            @OnClose
            public void onClose() {
                if (null != webSocketMap.get(this.userId)) {
                    subOnlineCount(); //在线数减1
                    webSocketMap.remove(this.userId); //从Map中删除
                    System.out.println("有一连接关闭: " + this.userId + " 当前在线人数为: " + getOnlineCount());

                    sendMsg2All(this.userId + " - 已下线! 再见");
                }
            }

            //客户端异常
            @OnError
            public void onError(Session session, Throwable error) {
                System.out.println("发生错误: " + error);
                error.printStackTrace();
            }

            ///收到浏览器客户端消息后调用的方法
            @OnMessage
            public void onMessage(String message, Session session) {
                System.out.println("来自客户端的消息: " + this.userId + " - " + message);

                if (message.contains("-")) {
                    String[] split = message.split("-");
                    webSocketMap.keySet().forEach(x -> {
                        if (split[0].equalsIgnoreCase(x))
                            sendMsg2One(this.userId + "->" + x + " - " + split[1], x); //点对点
                    });
                } else {
                    sendMsg2All(userId + " - " + message); //群发
                }
            }

            ///群发消息
            public static void sendMsg2All(String message) {
                webSocketMap.values().forEach(x -> x.sendMsg(message));
            }

            ///点对点发送消息
            public static void sendMsg2One(String message, String userId) {
                webSocketMap.get(userId).sendMsg(message);
            }

            ///实现服务器主动推送
            private void sendMsg(String message) {
                try {
                    this.session.getBasicRemote().sendText(message);
                    // this.session.getAsyncRemote().sendText(message);
                } catch (IOException e) {
                    System.out.println("异常---发送消息: " + e);
                }
            }

            //三个同步方法,线程安全
            private static synchronized int getOnlineCount() {
                return onlineCount;
            }

            private static synchronized void addOnlineCount() {
                MyWebSocket.onlineCount++;
            }

            private static synchronized void subOnlineCount() {
                MyWebSocket.onlineCount--;
            }
        }

//}


//{--------<<<druid>>>-------------------------------X
#数据源Druid
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>1.1.10</version>
        </dependency>
    
    0.配置文件
        //sp1.x默认数据源为: org.apache.tomcat.jdbc.pool.DataSource
        //sp1.x默认数据源为：com.zaxxer.hikari.HikariDataSource
        spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
        
        spring.datasource.url=jdbc:mysql://127.0.0.1:3306/test0329?useSSL=false
        spring.datasource.driver-class-name=com.mysql.jdbc.Driver
        spring.datasource.username=***
        spring.datasource.password=***
        
        //0-1        1-10    10-100    100-1000    1-10    10-100    100-1000    1000+

    1.监控druid    //http://localhost:8080/blue/druid/login.html
        @Configuration
        public class DruidConfig {

            @ConfigurationProperties(prefix = "spring.datasource")

            @Bean
            public DataSource druid() {
                return new DruidDataSource();
            }

            // 1.配置一个管理后台的Servlet
            @Bean
            public ServletRegistrationBean statViewServlet() {
                ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");

                Map<String, String> initParams = new HashMap<>();
                initParams.put("loginUsername", "admin");
                initParams.put("loginPassword", "123456");
                initParams.put("allow", ""); //允许所有访问
                initParams.put("deny", "192.168.15.21"); //黑名单阻止访问 (存在共同时,deny优先于allow)
                bean.setInitParameters(initParams);
                return bean;
            }

            // 2.配置一个web监控的filter
            @Bean
            public FilterRegistrationBean webStatFilter() {
                FilterRegistrationBean bean = new FilterRegistrationBean();

                bean.setFilter(new WebStatFilter());
                Map<String, String> initParams = new HashMap<>();
                initParams.put("exclusions", "*.js,*.css,/druid/*");
                bean.setInitParameters(initParams); //添加需要忽略的格式信息
                bean.setUrlPatterns(Collections.singletonList("/*")); //添加过滤规则
                return bean;
            }
        }
        
#JdbcTemplate
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>

    0.代码使用
        @Autowired
        JdbcTemplate jdbcTemplate;

        List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from student");


//}

//{--------<<<JPA>>>---------------------------------X
#比对概念
        JPA                    -> //Java-Persistence-API, 对持久层操作的标准(接口+文档)
        
        Hibernate            -> //全自动化的ORM框架.
        Hibernate JPA        -> //实现了 JPA 标准的 Hibernate(Hibernate-3.2+).
        
        Spring Data            -> //用于简化数据库(SQL,NoSQL...)访问,并支持云服务的开源框架.
        Spring Data JPA        -> //Spring Data的一个子模块,实现了 JPA 标准的 Spring Data, 底层是 Hibernate.
        
        Spring Data Redis    -> //通过简单配置, 实现对reids各种操作,异常处理及序列化,支持发布订阅.
    
    0.pom.xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
    
    1.配置文件
        //可选参数-create: 每次启动都会删除旧表,新建一个空表
        //可选参数-update: 根据实体类创建/更新数据库表
        spring.jpa.hibernate.ddl-auto=update
        spring.jpa.show-sql=true
        
    2.实体类
        @Data
        @Entity //表明是一个JPA实体->自动建表
        @Table(name = "t_emp") //默认表名为类名小写
        @NoArgsConstructor
        @AllArgsConstructor
        public class Employee {
            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            private Integer id;

            @Column(name = "first_name")
            private String firstName;

            @Column
            private Boolean genderFlag; //默认列名 -> gender_flag

        }
    
#Repository ///Spring-Data-JPA 顶层接口,标识接口,空接口.
    1.方法名称命名的方式
        //驼峰命名规则: findBy(关键字) + 属性名称(首字母大写) + 查询条件(首字母大写,Like,OrderBy...)
        public interface EmployeeRepoDao extends Repository<Employee, Integer> {

            //SELECT * FROM t_emp WHERE first_name='zhang' AND gender_flag=TRUE
            List<Employee> findByFirstNameAndGenderFlag(String firstName, boolean genderFlag);

            //SELECT * FROM t_emp WHERE first_name LIKE '%ang%' OR gender_flag=TRUE ORDER BY id DESC
            List<Employee> findByFirstNameLikeOrGenderFlagOrderByIdDesc(String firstName, boolean genderFlag);
        }
        
    2.基于注解的方式
        public interface EmployeeRepoDao extends Repository<Employee, Integer> {

            //(1).hql --> 使用bean属性名称替代数据库字段进行查询
            @Query("FROM Employee WHERE firstName LIKE ?1 OR genderFlag=?2 ORDER BY id DESC")
            List<Employee> queryByHQL(String firstName, boolean genderFlag);

            //(2-1).原生sql --> 参数序号从①开始; nativeQuery=true
            @Query(value = "SELECT * FROM t_emp WHERE first_name LIKE ?1 OR gender_flag = ?2 ORDER BY id DESC", 
                    nativeQuery = true)
            List<Employee> queryBySQL(String firstName, boolean genderFlag);
            
            //(2-2).原生sql --> @Param("参数名")
            @Query(value = "SELECT * FROM t_emp WHERE first_name LIKE :fName OR gender_flag = :lName ORDER BY id DESC", 
                    nativeQuery = true)
            List<Employee> queryBySQL(@Param("fName") String firstName, @Param("lName") boolean genderFlag);
            
            @Modifying //更新|删除,必须添加此注解
            //@Transactional 
            @Query(value = "UPDATE t_emp SET first_name=?1 WHERE id=?2", nativeQuery = true)
            Integer updateBySQL(String firstName, Integer id);
        }
        
    3.事务
        业务逻辑层 Service 调用多个 Repository 方法时, 需要在 Service 方法上声明事务 @Transactional
        
#CrudRepository
    0.最基础的CRUD; extends Repository;
        public interface EmployeeCrudDao extends CrudRepository<Employee, Integer> {}
        
    1.1.测试DEMO
        @Test
        public void daoCrud() {
            //save(): 先查询数据表中是否存在该id数据??? 无则新增; 有则更新
            Employee save = employeeCrudDao.save(new Employee(7, "张0三", true));
            Iterable<Employee> all = employeeCrudDao.findAll();
            System.out.println(save + " - " + JSON.toJSON(all));
        }

#PagingAndSortingRepository
    0.分页和排序功能; extends CrudRepository;
    
        public interface EmployeePSDao extends PagingAndSortingRepository<Employee, Integer> {}
        
    1.测试DEMO        
        @Test
        public void daoPS() {
            Sort sort = Sort.by(Sort.Direction.DESC, "firstName", "id"); //(1).排序
            Iterable<Employee> all = employeePSDao.findAll(sort);

            Pageable pageable = PageRequest.of(0, 2); //(2).页码从0开始; 分页
            Page<Employee> all = employeePSDao.findAll(pageable);
            
            Sort sort = Sort.by(Sort.Direction.DESC, "id");
            PageRequest pageable = PageRequest.of(1, 2, sort);
            Page<Employee> all = employeePSDao.findAll(pageable); //(3).排序+分页
            System.out.println("daoPS - " + JSON.toJSON(all));
        }
        
#JpaRepository(**常用**)
    0.对父接口方法的返回值进行适配处理; extends PagingAndSortingRepository;
    
        public interface EmployeeJpaDao extends JpaRepository<Person, Integer> {}
        
#JpaSpecificationExecutor
    0.提供多条件查询—分页—排序—独立于以上接口存在—所以需配合以上接口使用
        public interface EmployeeDao extends JpaSpecificationExecutor<Employee>, JpaRepository<Employee, Integer> {}
        
    1.测试DEMO
        @Test
        public void daoDao() {
            Specification<Employee> spec = new Specification<Employee>() {
                /**
                 * @param root        查询对象的属性封装,即 Employee
                 * @param query        查询关键字 SELECT, WHERE, ORDER BY ...
                 * @param builder    查询条件 =, >, LIKE
                 * @return            封装整个查询条件
                 */
                @Override
                public Predicate toPredicate(Root<Employee> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
                    // SELECT * FROM t_emp
                    // WHERE first_name LIKE '%ang%'
                    // OR gender_flag=TRUE
                    // AND id BETWEEN 3 AND 9
                    // AND id>=4
                    // ORDER BY first_name DESC, id ASC;
                    Predicate or = builder.or(builder.like(root.get("firstName").as(String.class), "%ang%"),
                            builder.equal(root.get("genderFlag").as(boolean.class), true)); //OR

                    List<Predicate> list = new ArrayList<>(); //AND
                    list.add(or);
                    list.add(builder.between(root.get("id").as(Integer.class), 3, 9));
                    list.add(builder.greaterThanOrEqualTo(root.get("id").as(Integer.class), 4));
                    Predicate[] predicates = new Predicate[list.size()];
                    
                    Predicate predicate = builder.and(list.toArray(predicates)); //所有条件
                    query.where(predicate); //WHERE ... OR ... AND ... AND ...

                    query.multiselect(root.get("id"), root.get("firstName")); //SELECT *,*

                    query.orderBy(builder.desc(root.get("firstName")),
                            builder.asc(root.get("id"))); //ORDER BY ..., ...

                    return query.getRestriction();
                }
            };

            // Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "firstName"),
            //         new Sort.Order(Sort.Direction.ASC, "id")); //ORDER BY ..., ...
            
            PageRequest pageable = PageRequest.of(1, 2/*, sort*/); //分页 LIMIT 1,2; 页码从0开始

            Page<Employee> all = employeeDao.findAll(spec, pageable);
            System.out.println("daoDao - " + JSON.toJSON(all));
        }
        
#一对多关联映射
    0.dept与emp是一对多关系
        @Data
        @Entity
        @Table(name = "t_emp")
        public class Employee {
            //... ...
            
            /**
             * PERSIST  持久保存拥有方实体时,也会持久保存该实体的所有相关数据。
             * MERGE    将分离的实体重新合并到活动的持久性上下文时,也会合并该实体的所有相关数据。
             * REMOVE   删除一个实体时,也会删除该实体的所有相关数据。
             * ALL      以上都适用。
             */
            @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}) //emp->dept: 多对一
            @JoinColumn(name = "dept_id") //外键
            private Dept dept;
        }
        
        @Data
        @Entity
        @Table(name = "t_dept")
        public class Dept {
            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            @Column(name = "dept_id")
            private Integer deptId;

            @Column(name = "dept_name")
            private String deptName;

            @OneToMany(mappedBy = "dept") //dept->emp: 一对多关系
            private List<Employee> emps = new ArrayList<>();
        }
    
    1.新增DEMO
        @Test
        public void saveOne2Many() {
            //新建 emp - dept
            Employee employee = new Employee("whang", false);
            Dept dept = new Dept("软件");

            //关联
            employee.setDept(dept);
            dept.getEmps().add(employee);
            
            //写库
            employeeDao.save(employee);
        }
        
    2.查询DEMO
        @Test
        public void findOne2Many() {
            Optional<Employee> optional = employeeDao.findById(10);
            if (optional.isPresent()) {
                Employee employee = optional.get();
                System.out.println("DeptName: " + employee.getDept().getDeptName());
            }
        }
    
#多对多关联映射
    0.emp和role是多对多关系
    @Data
    @Entity
    @Table(name = "t_emp")
    public class Employee {
        //... ...
        
        @ManyToMany(mappedBy = "emps", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
        private Set<Role> roles = new HashSet<>();
    }
    
    @Data
    @Entity
    @Table(name = "t_role")
    public class Role {
        //... ...
        
        @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
        //JoinTable: 中间表信息(配置在两张表中的任意一个)
        //joinColumns: 该表主键在中间表中的字段
        //inverseJoinColumns: 另一个表(即emp)主键在中间表中的字段
        @JoinTable(name = "t_emp_role", joinColumns = @JoinColumn(name = "role_id"),
                inverseJoinColumns = @JoinColumn(name = "emp_id"))
        private Set<Employee> emps = new HashSet<>();
    }
        
    1.新增DEMO
    @Test
    public void saveMany2Many() {
        //新建 emp - role
        Employee li = new Employee("li", false);
        Employee zhang = new Employee("zhang", true);
        Role admin = new Role("管理员");
        Role finance = new Role("财务");

        //关联 
        li.getRoles().add(admin);
        li.getRoles().add(finance);
        admin.getEmps().add(li);
        admin.getEmps().add(zhang);
        
        //写库
        employeeDao.save(li);
        employeeDao.save(zhang);
    }
    
    2.查询DEMO
        @Test
        public void findMany2Many() {
            Optional<Employee> optional = employeeDao.findById(12);
            System.out.println(optional.get().getRoles());
        }

//}


//{--------<<<定时任务>>>----------------------------X
#任务调度可以用'Quartz'; 但对于简单的定时任务可以使用内置的'Scheduled'. linux系统级别定时任务使用'crontab'.

#Scheduled
    #initialDelay    //项目启动后,延迟多少毫秒执行任务
    #fixedRate        //每隔多少毫秒执行一次 (当 任务耗时>频率 时,下次开始时间=上次结束时间);
    #fixedDelay        //每次执行完毕,延迟多少毫秒再次执行
    #cron(******)    //详细配置方法执行频率

    // cron表达式: [秒] [分] [时] [日] [月] [周] [年(可省)]
    // 秒(0~59); 分(0~59); 时(0~23); 日(1~31,和月份有关); 月(1~12); 星期(1~7,1为周日); 年(1970~2099)
    
     #* 所有字段; 表示对应时间域的每一个时刻; 如分钟字段,表示"每分钟"
     #- ........; 表示一个范围; 如小时字段'10-12',表示从10到12点, 即 10,11,12
     #, ........; 表示一个列表值; 如星期字段"MON,WED,FRI", 表示星期一,星期三和星期五
     #/ ........; 表示一个等步长序列; x/y: x 为起始值,y 为增量步长值.
        //如分钟字段: 0/15表示 0,15,30,45; 5/15表示 5,20,35,50,
        //也可以使用 */y,等同于 0/y,即 y秒触发一次.
        
     #? '日期'和'星期'; 通常指定为"无意义的值",相当于占位符. --> 因为日和星期是有冲突的.
     #L ..............; 代表"Last"的意思,但它在两个字段中意思不同.
        //在日期中,表示这个月的最后一天; 如一月的 31 号,非闰年二月的 28 号.
        //在星期中,则表示星期六,等同于 7.
        //如果 L 出现在星期字段里,而且在前面有一个数值 X,则表示"这个月的最后星期 (X-1)"; 如 6L 表示该月的最后星期五
     
        @Slf4j
        @Component //不可省
        public class ScheduledTask {
            @Scheduled(cron = "*/5 * * * * ?") ///配合使用-全局注解 @EnableScheduling
            public void task() {
                log.info("ScheduledTask---{}", SystemUtils.getNow());
            }
        }    

#Scheduled-DEMO
    // 0 0 10,14,16 * * ?    每天上午10点，下午2点，4点
    // 0 0/30 9-17 * * ?    朝九晚五工作时间内每半小时
    // 0 0 12 ? * WED        每个星期三中午12点
    // 0 0 12 * * ?            每天12点触发
    // 0 15 10 ? * *        每天10点15分触发
    // 0 15 10 * * ?        每天10点15分触发
    // 0 15 10 * * ? *        每天10点15分触发
    // 0 15 10 * * ? 2005    2005年每天10点15分触发
    // 0 * 14 * * ?            每天下午的 2点到2点59分 每分触发一次
    // 0 0/5 14 * * ?        每天下午的 2点到2点59分(整点开始，每隔5分触发)
    // 0 0/5 14,18 * * ?    每天下午的 2点到2点59分、18点到18点59分(整点开始，每隔5分触发)
    // 0 0-5 14 * * ?        每天下午的 2点到2点05分每分触发
    // 0 10,44 14 ? 3 WED    3月每周三下午的 2点10分和2点44分触发
    // 0 15 10 ? * MON-FRI    从周一到周五每天上午的10点15分触发
    // 0 15 10 15 * ?        每月15号上午10点15分触发
    // 0 15 10 L * ?        每月最后一天的10点15分触发
    // 0 15 10 ? * 6L        每月最后一周的星期五的10点15分触发
    // 0 15 10 ? * 6L 2002-2005    从2002年到2005年每月最后一周的星期五的10点15分触发
    // 0 15 10 ? * 6#3        每月的第三周的星期五开始触发
    // 0 0 12 1/5 * ?        每月的第一个中午开始每隔5天触发一次
    // 0 11 11 11 11 ?        每年的11月11号 11点11分触发(光棍节)

#Quartz
    任务调度(job scheduling)的开源框架.    可以与J2EE与J2SE结合,也可以单独使用.
    可用来创建简单或运行十个,百个,甚至于好几万个 jobs 复杂的程序.
    
    #job        - 任务        - 你要做什么事?
    #Trigger    - 触发器    - 你什么时候去做?
    #Scheduler    - 任务调度    - 你什么时候需要去做什么事?
    
    1.简单DEMO
        private static void task01() throws SchedulerException {
            JobDetail job = JobBuilder.newJob(JobDemo.class).build();

            //(1).通过 Quartz 内置方法来完成简单的重复调用,每秒执行一次
            // Trigger trigger = TriggerBuilder.newTrigger()
            //         .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever()).build();

            //(2).自定义 Cron 表达式来给定触发的时间
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withSchedule(CronScheduleBuilder.cronSchedule("0/2 * * * * ?")).build();

            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.scheduleJob(job, trigger);

            scheduler.start();
        }
#Quartz&Boot(2种方式)
    #(1).创建普通job类,直接调用.(灵活,非侵入)
    #(2).job类继承 QuartzJobBean,实现方法 executeInternal(),此方法就是被调度的任务体
    
        <dependency>
            <groupId>org.quartz-scheduler</groupId>
            <artifactId>quartz</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId> //作用详见<<常用包>>
            <artifactId>spring-context-support</artifactId>
        </dependency>
    
#MethodInvokingJobDetailFactoryBean
    1.普通job类
        @Component
        public class JobDemo01 {
            @Autowired
            HelloService helloService; //service 层

            public void job() {
                helloService.hello();
                System.out.println("job01: " + SystemUtils.getNow());
            }
        }
        
    2.调度配置    
        @Configuration
        public class QuartzConfig01 {
            @Bean("job01")
            public MethodInvokingJobDetailFactoryBean job01(JobDemo01 jobDemo01) {
                MethodInvokingJobDetailFactoryBean job = new MethodInvokingJobDetailFactoryBean();
                job.setName("my-job01"); // 任务的名字
                job.setGroup("my"); // 任务的分组

                job.setConcurrent(false); // 是否并发
                job.setTargetObject(jobDemo01); // 被执行的对象
                job.setTargetMethod("job"); // 被执行的方法
                return job;
            }

            @Bean(name = "tigger01")
            public CronTriggerFactoryBean tigger01(@Qualifier("job01") MethodInvokingJobDetailFactoryBean job01) {
                CronTriggerFactoryBean tigger = new CronTriggerFactoryBean();
                tigger.setName("my-tigger01");
                tigger.setJobDetail(Objects.requireNonNull(job01.getObject()));
                tigger.setCronExpression("0/5 * * * * ?"); //cron
                return tigger;
            }

            @Bean(name = "scheduler01")
            public SchedulerFactoryBean scheduler01(@Qualifier("tigger01") Trigger tigger01) {
                SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
                scheduler.setStartupDelay(5); // 延时启动定时任务,避免系统未完全启动却开始执行定时任务的情况
                scheduler.setOverwriteExistingJobs(true); // 覆盖已存在的任务
                scheduler.setTriggers(tigger01); // 注册触发器
                return scheduler;
            }
        }
        
#JobDetailFactoryBean
    1.指定job类
        ///@Component -> 无需此注解,区别于方式1
        public class JobDemo02 extends QuartzJobBean {
            @Override
            protected void executeInternal(JobExecutionContext context) {
                HelloService helloService = (HelloService) context.getMergedJobDataMap().get("helloService"); //取参 - service
                helloService.hello();
                System.out.println("job02: " + SystemUtils.getNow());
            }
        }
    
    2.调度配置
        @Configuration
        public class QuartzConfig02 {
            @Autowired
            HelloService helloService;

            @Bean("job02")
            public JobDetailFactoryBean job02() {
                JobDetailFactoryBean job = new JobDetailFactoryBean();
                job.setJobClass(JobDemo02.class);

                Map<String, Object> map = new HashMap<>();
                map.put("helloService", helloService);
                job.setJobDataAsMap(map); //传参 -> helloService
                return job;
            }

            @Bean(name = "tigger02")
            public CronTriggerFactoryBean cronTriggerFactoryBean(JobDetailFactoryBean job02) {
                CronTriggerFactoryBean tigger = new CronTriggerFactoryBean();
                tigger.setJobDetail(Objects.requireNonNull(job02.getObject()));
                tigger.setCronExpression("0/5 * * * * ?"); //cron
                return tigger;
            }

            @Bean(name = "scheduler02")
            public SchedulerFactoryBean schedulerFactoryBean(CronTriggerFactoryBean tigger02) {
                SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
                scheduler.setTriggers(tigger02.getObject());
                return scheduler;
            }
        }

//}

//{--------<<<异步任务>>>----------------------------？？？？？















//}


//{--------<<<Config>>>------------------------------X
#properties默认
    server.port=8090
    server.servlet.context-path=/demo

    spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
    spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
    spring.datasource.url=jdbc:mysql://192.168.8.7:33306/test0329?useSSL=false&allowMultiQueries=true&serverTimezone=GMT%2B8
    spring.datasource.username=bluecardsoft
    spring.datasource.password=#$%_BC13439677375
    
    //(1).引用配置变量(无则使用默认值 spring)
    info.msg=hello ${server.servlet.context-path : spring}
    
    //(2).随机值; 微服务中不需要记录 IP:prot,所以可随机指定端口
    info.random=${random.int[1000,9999]}

    //动态web修改 context-path -> 项目右键 -> properties -> 搜索web -> 修改 Web Project Settings

#YAML文件
    k:(空格)v //表示一对键值对(空格必须有),其中属性和值大小写敏感

    1.数组(List/Set)
        标准写法 -> pets: 
                     - cat
                     - dog
        行内写法 -> pets: [cat,dog,pig]

    2.随机数 ${random.int}; ${random.int(10)}; ${random.int(10,100)}

    3.引用配置变量
        person.age = ${random.int}
        person.last_name = 张三${person.age:18}
    
    4.转义
        spring:
          datasource:
            url: jdbc:mysql://192.168.8.7:33306/test0329?useSSL=false&allowMultiQueries=true
            username: bluecardsoft
            password: "#$%_BC13439677375" //""双引号里的内容不会转义符,''单括号则会
            driver-class-name: com.mysql.jdbc.Driver
            type: com.alibaba.druid.pool.DruidDataSource

#加载顺序(先后)
    – classpath:/            #路径src/main/resources
    – classpath:/config/
    – file:./                #当前项目的根路径,与pom同级. (jar包同级目录)
    – file:./config/

    //优先级别: 低--->高. 高优先级覆盖低优先级.
    //加载顺序: 先--->后. (由里到外). 后加载的覆盖先加载的. [互补配置]
    
    0.以上是开发时配置文件的位置—对于打成jar包
        由于 classpath 会被打成jar包; 而 file 则不会,所以应该把配置文件放到jar包同级目录.
        //jar包同级 '/config/*.yml' 优先级最高, jar包内部默认位置的 '*.yml' 优先级最低!
    
    1.配置外部log
        //在配置文件中指定log位置.(内部或外部yml都可以)
        //推荐外部 -> logback的 scan 和 scanPeriod 两个属性保证了 热部署,即改即生效!!!
        logging.config=file:./config/logback-spring.xml

#读取配置文件
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
            <optional>true</optional>
        </dependency>
        
    0.my.properties
        voice.in=欢迎光临

        info.enabled=false
        info.remote-address=192.168.1.1
        info.security.username=user
        info.security.password=pwd
        info.security.roles=USER,ADMIN
        
    1.批量读取
        //@PropertySource: 加载指定的配置文件
        //value: 设置需要加载的属性文件,可以一次性加载多个. (默认参数)
        //encoding: 编码格式,默认""
        //ignoreResourceNotFound: 当指定的配置文件不存在是否报错,默认 false
        //name: 在Springboot的环境中必须唯一. 默认"class path resource [config/my.properties]"

        //@Value: 用于读取单个属性值
        
        //@ConfigurationProperties: 用于批量读取属性值
        //prefix: 属性前缀,通过 'prefix+字段名' 匹配属性
        //ignoreUnknownFields: 是否忽略未知的字段
        //ignoreInvalidFields: 是否忽略验证失败(类型转换异常)的字段
        //-----------<<<<<<一定要有GET/SET方法>>>>--------------------
        
        @Data// lombok插件,自动生成GET/SET
        @Component
        @ConfigurationProperties(/* prefix = "info", */ ignoreUnknownFields = true, ignoreInvalidFields = true)
        @PropertySource(value = { "file:./my.properties",
                "classpath:my.properties" }, encoding = "utf-8", ignoreResourceNotFound = false, name = "my.properties")
        public class MyProperties {
            public Voice voice; //voice开头
            public Info info; //info开头

            @Data
            public static class Voice {
                public String in;
            }

            @Data
            private static class Info {
                private boolean enabled;
                private InetAddress remoteAddress;// 下划线语法,驼峰语法等都能匹配属性
                private Security security;
            }

            @Data
            private static class Security {
                private String username;
                private String password;
                private List<String> roles = new ArrayList<>(Collections.singleton("USER"));
            }
        }
        
        //通过 @Autowired 方式取值 MyProperties
        logger.info("MyProperties--res: {}", JSON.toJSON(myProperties));
            {
              "info": {
                "enabled": false,
                "remoteAddress": "192.168.1.1",
                "security": {
                  "password": "pwd",
                  "roles": [
                    "USER",
                    "ADMIN"
                  ],
                  "username": "user"
                }
              },
              "voice": {
                "in": "欢迎光临"
              }
            }
        
    3.单个读取
        @Value("${info.enabled}") //(1). @Value
        public String infoEnabled;
        
        @Autowired //(2). Environment
        Environment env;
    
        String pwd = env.getProperty("spring.mail.password");
        
    3.通过IO读取
        // 默认从此类所在包下读取,path需要添加前缀"/"
        // InputStream in = getClass().getResourceAsStream("/my.properties");
        
        // 默认从ClassPath下读取,path不需要添加前缀
        InputStream in = getClass().getClassLoader().getResourceAsStream("my.properties");
        Properties properties = new Properties();
        properties.load(new InputStreamReader(in, "UTF-8")); //U8方式读取
        properties.forEach((key, value) -> log.info(key + " - " + value));
        
        
#@Value("#{}")与@Value("${}")的区别
    //(1).@Value("#{}") -> 通过SpEl表达式获取: 常量; bean属性值; 调用bean的某个方法    
        @Value("#{1}")
        private int number; // 获取数字1

        @Value("#{'Spring Expression Language'}") // 获取字符串常量
        private String language;

        @Value("#{info.remoteAddress}") // 获取bean的属性
        InetAddress address;
    
    //(2).@Value("${}") -> 获取属性文件中定义的属性值    
        @Value("${info.enabled:}")
        public String enabled; //获取配置属性,默认空字符串
    
    //(3).总结
        ${ property : default_value }
        #{ obj.property? : default_value } //二者取默认值时,语法不同(多个?)
        #{ '${}' } //二者可以结合使用,注意单引号!~! 但不能反过来,如: ${ '#{}' }

#@PostConstruct
    //...

#多环境切换 - profile特性 - 不同环境加载不同配置. 
    //以下文件 与 默认application.yml 存放在同级目录下.
    //其中,前者配置特殊信息, 后者配置公用信息. 二者相互补充
    application-dev.yml        ->    开发环境
    application-test.yml    ->    测试环境 
    application-prod.yml    ->    生产环境

    0.激活profile特性的三种方法.
        (1).在默认配置文件中激活: spring.profiles.active=dev
        (2).(略)命令行: java -jar demo.jar --spring.profiles.active=dev
        (3).(略)虚拟机参数(VM argumments): -Dspring.profiles.active=dev

    1.log的profile特性
        <springProfile name="dev"> //<!-- 控制台 * 测试环境 -->
            <root level="info">
                <appender-ref ref="console" />
            </root>
        </springProfile>

        <springProfile name="prod"> //<!-- 控制台 * 生产环境 -->
            <root level="warn">
                <appender-ref ref="console" />
            </root>
        </springProfile>

//}

//{--------<<<热部署>>>------------------------------X
#插件 SpringLoader(两种方式)
    //缺点: 只对 java 代码生效, 对页面更改无能为力.
    
    1-0.以maven插件方式使用SpringLoader
        // <!-- springloader 插件 -->
        <build>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                    <dependencies>
                        <dependency>
                            <groupId>org.springframework</groupId>
                            <artifactId>springloaded</artifactId>
                            <version>1.2.5.RELEASE</version>
                        </dependency>
                    </dependencies>
                </plugin>
            </plugins>
        </build>
        
    1-1.使用maven命令启动项目
        //其中, maven插件起作用,必须使用maven命令进行启动
        //缺点: mvn插件形式的热部署程序是在系统后台以进程的形式来运行. 需要手动关闭该进程(java.exe *32)
        Run As... --> mvn build... ---> Main --> Goals填写: spring-boot:run

    2-0.项目中直接使用jar包
        目录'/lib'添加 springloader-1.2.5.RELEASE.jar 
        
    2-1.启动命令
        Run Configuration... --> Arguments --> VM argumments填写: -javaagent:.\lib\springloaded-1.2.5.RELEASE.jar -noverify

#工具 DevTools
    0.部署项目时使用的方式
        SpringLoader --> 热部署; DevTools --> 重新部署.

    1.pom.xml
        // <!-- DevTools -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <optional>true</optional> //<!-- 依赖只在当前项目生效,不会传递到引用项目中 -->
        </dependency>

//}

//{--------<<<Exception>>>---------------------------X
#Boot对于异常处理提供了五种处理方式 --> //推荐: 3/5  http://blog.51cto.com/13902811/2170945?source=dra

#1.自定义错误页面(默认)
    一旦程序出现异常, SpringBoot 会向url '/error' 发送请求.
    通过默认的 BasicExceptionController 来处理请求 '/error',然后跳转到默认异常页面,显示异常信息.
    
    所以, 如果需要将所有异常统一跳转到自定义错误页面,需新建页面 /templates/error.html //必须叫 error.html
    缺点: 不符合实际需求; 应该对于不同错误跳转不同页面.

#2.注解处理异常@ExceptionHandler
#3.注解处理异常@ExceptionHandler + @ControllerAdvice    
    //处理顺序: 本类 --> @ControllerAdvice 标识类
    当执行过程中出现异常,首先在本类中查找 @ExceptionHandler 标识的方法
    找不到, 再去查找 @ControllerAdvice 标识类中的 @ExceptionHandler 标识方法来处理异常.
    
    //处理优先级: 异常的最近继承关系
    例如发生异常 NullPointerException; 但是声明的异常有 RuntimeException 和 Exception
    此时,根据异常的最近继承关系,找到继承深度最浅的那个, 即 RuntimeException 的声明方法

    @ControllerAdvice //异常处理类
    public class GlobalException {
        /**
         * 参数(可选):
         *         异常参数(包括自定义异常);
         *         请求或响应对象(HttpServletRequest; ServletRequest; PortleRequest/ActionRequest/RenderRequest) 
         *         Session对象(HttpSession; PortletSession) 
         *         WebRequest; NativeWebRequest; Locale; 
         *         InputStream/Reader; OutputStream/Writer; Model
         * 
         * 返回值(可选):
         *         ModelAndView; Model; Map; View; String; @ResponseBody;
         *         HttpEntity<?>或ResponseEntity<?>; 以及void
         */
        @ExceptionHandler(ArithmeticException.class) //ex对应发生的异常对象
        public ModelAndView arithmeticException(HttpServletRequest request, ArithmeticException ex) {
            
            //区分 URL & URI: http://ip:port/demo/hello/hello & /demo/hello/hello
            log.info("{} & {}", request.getRequestURL(), request.getRequestURI());

            ModelAndView mv = new ModelAndView("error1");
            mv.addObject("errMsg", ex.getLocalizedMessage());
            return mv; //跳转异常页-并携带异常信息
        }
        
        @ExceptionHandler(RuntimeException.class)
        public ModelAndView runtimeException(HttpServletRequest request, RuntimeException ex) {                
            ModelAndView mv = new ModelAndView("error2");
            mv.addObject("errMsg", ex.getLocalizedMessage());
            return mv;
        }
    }

#4.配置 SimpleMappingExceptionResolver(3的简化)
    //优点: 在全局异常类的一个方法中完成所有异常的统一处理
    //缺点: 只能进行异常与视图的映射, 不能传递异常信息.

    @Configuration //(1).此处的注解不同
    public class GlobalException {
        
        @Bean //(2).方法必须有返回值.返回值类型必须是: SimpleMappingExceptionResolver
        public SimpleMappingExceptionResolver getSimpleMappingExceptionResolver() {
            SimpleMappingExceptionResolver resolver = new SimpleMappingExceptionResolver();
            Properties mappings = new Properties();

            //arg0: 异常的类型,注意必须是异常类型的全名; arg1: 视图名称
            mappings.put("java.lang.ArithmeticException", "error1");
            mappings.put("java.lang.RuntimeException", "error2");

            //(3).设置异常与视图的映射,但不能传递异常信息
            resolver.setExceptionMappings(mappings);
            return resolver;
        }
    }

#5.自定义类处理异常 HandlerExceptionResolver
    @Configuration
    public class GlobalException implements HandlerExceptionResolver {

        @Override
        public ModelAndView resolveException(
                HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
            ModelAndView mv = new ModelAndView();

            //不同异常类型,不同视图跳转
            if (ex instanceof ArithmeticException) {
                mv.setViewName("error1");
            }
            if (ex instanceof NullPointerException) {
                mv.setViewName("error2");
            }
            //并传递异常信息
            mv.addObject("errMsg", ex.toString());
            return mv;
        }
    }

#参照 ErrorMvcAutoConfiguration ---> 错误处理的自动配置.
    #一旦系统出现 4xx 或 5xx 之类的错误, 'ErrorPageCustomizer' 就会生效, 它会发送'/error'请求;
        @Value("${error.path:/error}")    
        private String path = "/error";
    
    #'/error'请求会被 'BasicErrorController' 处理, 它有两种处理机制:
        @Controller
        @RequestMapping("${server.error.path:${error.path:/error}}")
        public class BasicErrorController extends AbstractErrorController {
            // 针对浏览器请求的响应页面, 产生html类型的数据
            @RequestMapping(produces = "text/html")
            public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) { }
            
            // 针对其他客户端请求的响应数据, 产生json数据
            @RequestMapping    
            @ResponseBody 
            public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) { }
        }
        
    #其中, html类型数据由 'DefaultErrorViewResolver' 解析产生, 规则为:
        1).有模板引擎的情况下: error/状态码
        2).没有模板引擎 (模板引擎找不到这个错误页面), 静态资源文件夹下找
        3).以上都没有错误页面, 就使用SpringBoot默认的错误提示页面
    
    #json类型数据由 'DefaultErrorAttributes'提供, 其中包括:
        timestamp: 时间戳;    status: 状态码; 
        error: 错误提示;    exception: 异常对象
        message: 异常消息;    errors: JSR303数据校验的错误都在这里
            
    1.定制错误页面
        将错误页面命名为 '错误状态码.html', 存放路径: 'templates/error/ *.html', 发生错误就会来到 对应状态码的页面.
        文件名也可以使用 4xx 和 5xx 来模糊匹配状态码, 当然精确匹配优先考略!!!

    2.定制错误的json数据
        //controller的一个辅助类,最常用作全局异常处理的AOP切面类
        @ControllerAdvice
        public class GlobalExceptionHandler {
            
            //(第1版).接口和浏览器返回皆为json -> 没有做到自适应!!!
            @ResponseBody
            @ExceptionHandler(BlueException.class)
            private Map<Object, Object> notFound(BlueException e) {
                Map<Object, Object> map = new HashMap<>();
                map.put("errMsg", e.errMsg);
                return map;
            }
        }
    
        //(第2版).转发到/error,进行自适应响应处理 -> 未能显示用户自定义的异常信息
        @ExceptionHandler(BlueException.class)
        private String notFound(HttpServletRequest req, BlueException e) {
            Map<Object, Object> map = new HashMap<>();
            map.put("errMsg", e.errMsg);

            // 传入自定义的错误状态码 4xx 5xx,否则就不会进入定制错误页面的解析流程
            req.setAttribute("javax.servlet.error.status_code", 500);

            // 转发到/error
            return "forword:/error";
        }
            
        //(第3版).错误请求的自适应反馈 (转发到定制错误页面或返回json), 以及携带自定义的数据内容
        @ExceptionHandler(BlueException.class)
        private String notFound(HttpServletRequest req, BlueException e) {
            Map<Object, Object> map = new HashMap<>();
            map.put("errCode", e.errCode);
            map.put("errMsg", e.errMsg);

            req.setAttribute("javax.servlet.error.status_code", 500);
            req.setAttribute("ext", map);

            return "forward:/error";
        }

        '再次强调: 错误页面的数据集合由 DefaultErrorAttributes.getErrorAttributes() 提供!!!'
        //(配合第3版共同使用).给容器中加入我们自己定义的ErrorAttributes
        @Component
        class BlueErrorAttributes extends DefaultErrorAttributes {
            @Override
            public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes, boolean includeStackTrace) {
                Map<String, Object> map = super.getErrorAttributes(requestAttributes, includeStackTrace);

                // 取出上述方法的'ext',放入错误页面的数据集合
                // 第二个参数: 0代表从 request 中读取数据; 1代表从 session 中
                map.put("data", requestAttributes.getAttribute("ext", 0));

                // 此map就是页面和json都能获取到的所有字段
                return map;
            }
        }

//}
    
//{--------<<<lombok>>>------------------------------X
    ///减少很多重复代码的书写. 比如:getter/setter/toString等
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <scope>provided</scope>
    </dependency>        

#eclipse
    下载: https://projectlombok.org/download
    将 lombok.jar 放在eclipse安装目录下,与 eclipse/sts.ini 同级
    当前目录打开cmder, 使用命令: "java -jar ./lombok.jar",弹框选择 Install/Update
    成功标识: sts.ini最后一行, -javaagent:F:\sts-bundle\sts-3.9.3.RELEASE\lombok.jar

#idea
    下载: https://github.com/mplushnikov/lombok-intellij-plugin/releases
    Settings-Plugins-'install from disk',选择下载的*.zip.即可安装成功    
    
#常用注解
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
    
//}    

//{--------<<<junit>>>-------------------------------X
#单元测试 junit
    1.pom.xml
        // <!-- 添加 junit 环境的 jar 包 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope> 
        </dependency>
        
    2.测试DEMO        
        @RunWith(SpringRunner.class) //junit 与 spring 进行整合; 也可用 SpringJUnit4ClassRunner.class
        @SpringBootTest//(classes = {SpringMain.class}) //加载项目启动类,可省
        public class HelloServiceTest {

            @Autowired
            private HelloService helloService;

            @Test
            public void test() {
                helloService.hello();
            }
        }

//}

//{--------<<<DEBUG>>>-------------------------------X
#开启DEBUG
    debug=true //配置文件中添加
    
#分析日志
    Positive match //列出匹配到对应类的配置项
    Negative match //不包括某个配置项的原因
    
#举例分析
    1.ConditionalOnClass
        //所必须的类在classpath路径下存在时,才会去解析对应的配置文件. .
        对于'DataSourceAutoConfiguration'而言,所必须的类是指: '*.DataSource'和'*.EmbeddedDatabaseType',
        只有这两个类都存在时,才会去配置对应的数据库资源.    

    2.ConditionalOnMissingClass
        //所必须的类在classpath路径下找不到.
        
    3.探测条件
        OnClassCondition    //表示匹配的类型存在与否
        OnBeanCondition     //指定bean实例存在与否
        OnPropertyCondition //检查指定属性是否存在
        
        DataSourceAutoConfiguration matched:
          - @ConditionalOnClass found required classes 'javax.sql.DataSource', 'org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType' (OnClassCondition)

        DataSourceAutoConfiguration.PooledDataSourceConfiguration matched:
          - AnyNestedCondition 2 matched 0 did not; NestedCondition on DataSourceAutoConfiguration.PooledDataSourceCondition.PooledDataSourceAvailable PooledDataSource found supported DataSource; NestedCondition on DataSourceAutoConfiguration.PooledDataSourceCondition.ExplicitType @ConditionalOnProperty (spring.datasource.type) matched (DataSourceAutoConfiguration.PooledDataSourceCondition)
          - @ConditionalOnMissingBean (types: javax.sql.DataSource,javax.sql.XADataSource; SearchStrategy: all) did not find any beans (OnBeanCondition)

//}


//{--------<<<SonarQube>>>---------------------------X
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
        if (flag0) { if (flag1) {  dosth... } }
        if (flag0 && flag1) {  dosth... } //修改后
        
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
        
//}        

//{--------<<<Actuator>>>----------------------------X
#配置
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        
#boot2.x所有端点访问路径都移到了/actuator. 
    //默认只暴露两个端点: health,info
    management.endpoints.web.exposure.include=* //暴露所有
    management.endpoints.web.exposure.exclude=env //不暴露: env
        
    http://localhost:8090/demo/actuator //返回所有已暴露的端点
    http://localhost:8090/demo/actuator/health //访问health端点
        
#各个端点endpoint
    auditevents        -    审计事件
    beans            -    应用程序上下文里全部的Bean,以及它们的关系
    health            -    报告应用程序的健康指标,这些值由 HealthIndicator 的实现类提供
    conditions        -    自动配置报告,记录哪些自动配置条件通过了,哪些没通过
    configprops        -    描述配置属性(包含默认值)如何注入Bean
    info            -    显示配置文件中以 'info' 打头的属性
    threaddump        -    获取线程活动的快照
    scheduledtasks    -    定时任务
    httptrace        -    跟踪 HTTP 请求-响应交换的情况
    mappings        -    描述全部的URI路径，以及它们和控制器(包含Actuator端点)的映射关系
    (*)metrics        -    报告各种应用程序度量信息,比如内存用量和HTTP请求计数.
    (*)loggers        -    显示和修改应用程序中的loggers配置
    (*)env            -    获取全部环境属性
        
    #其中,带(*)表示当前路径只能获取目录信息,详情信息得需要进一步访问获取. 如:
    http://localhost:8090/demo/actuator/metrics/system.cpu.count //获取系统cpu个数
    
    //也可以通过这个地址更改当前的日志级别????????????
    http://localhost:8090/demo/actuator/loggers/com.example.demo.controller //获取某个包的日志级别
                    
#定制端点endpoint
    //开启远程应用的关闭功能.【post请求】
    management.endpoint.shutdown.enabled=true 
    
    management.server.port=8091
    //只有在设置了 management.server.port 时才有效 (可选)
    management.server.servlet.context-path=/management
    //管理端的基本路径 (可选)
    management.endpoints.web.base-path=/application
    
    http://localhost:8091/demo/management/application/health //设置了以上三项,则访问 health 端点路径
    
    //若要恢复 1.x 方式(即用 /health 代替 /actuator/health), 设置以下属性:
    management.endpoints.web.base-path=/
    
    //关闭端点 - health
    management.endpoint.health.enabled=false
    
    //默认只显示health部分信息,开启显示全部信息
    management.endpoint.health.show-details=always
    
    //配置端口info信息, 访问 http://192.168.8.7:8090/demo/actuator/info
    info.myinfo.port=9527
        
#Bean
    druid: { //Spring应用程序上下文中的Bean名称或ID
        aliases: [ ], //
        scope: "singleton", //Bean的作用域.(通常是单例,这也是默认作用域)
        type: "com.alibaba.druid.pool.DruidDataSource", //Bean的Java类型
            //.class文件的物理位置,通常是一个URL,指向构建出的JAR文件.会随着应用程序的构建和运行方式发生变化
        resource: "class path resource [com/example/demo/config/DruidConfig.class]", 
        dependencies: [ ] //当前Bean注入的Bean ID列表
    },
        
#Conditions
    positiveMatches: { //成功条件
        DruidDataSourceAutoConfigure: [
            {    //检查 Classpath 里是否存在
                condition: "OnClassCondition",'com.alibaba.druid.pool.DruidDataSource'
                message: "@ConditionalOnClass found required class 'com.alibaba.druid.pool.DruidDataSource'; @ConditionalOnMissingClass did not find unwanted class"
            }
        ],
    }
    //...
    negativeMatches: { //失败条件
        DruidDataSourceAutoConfigure#dataSource: {
            notMatched: [
                {    //检查 Classpath 里是否存在
                    condition: "OnBeanCondition",
                    message: "@ConditionalOnMissingBean (types: javax.sql.DataSource; SearchStrategy: all) found beans of type 'javax.sql.DataSource' druid"
                }
            ],
            matched: [ ]
        },
    }

//}

//{--------<<<Admin>>>-------------------------------X
https://github.com/codecentric/spring-boot-admin
#SpringBoot-Admin 用于监控BOOT项目,基于 Actuator 的可视化 WEB UI

#客户端(被监控者)
    1.pom.xml
        <dependency>
            <groupId>de.codecentric</groupId>
            <artifactId>spring-boot-admin-starter-client</artifactId>
            <version>2.1.3</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        
    2.properties
        #actuator
        management.endpoints.web.exposure.include=*
        
        #admin服务端
        spring.boot.admin.client.url=http://localhost:9090/hello
        
    3.配置类
        @Configuration
        public /*static*/ class SecurityPermitAllConfig extends WebSecurityConfigurerAdapter {
            @Override
            protected void configure(HttpSecurity http) throws Exception {
                http.authorizeRequests() //Security权限-授权的固定格式
                        .anyRequest().permitAll() //所有请求, 所有权限都可以访问
                        .and().csrf().disable(); //固定写法: 使csrf拦截失效
            }
        }
    
#服务端
    1.pom.xml
        <dependency>
            <groupId>de.codecentric</groupId>
            <artifactId>spring-boot-admin-starter-server</artifactId>
            <version>2.1.3</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
    2.全局注解
        @EnableAdminServer

//}


//{--------<<<Docker>>>------------------------------X
#ABC
    能够把应用程序自动部署到容器的开源引擎; 轻量级容器技术!
    
    简化程序
        将软件做好配置依赖 -> 编译成镜像 -> 镜像发布 -> 其他使用者就可以直接使用这个镜像
    
    简化部署
        传统  : 安装(包管理工具或者源码包编译) -> 配置 -> 运行
        Docker: 复制 -> 运行        
    
    主机(Host)        ->    安装了Docker程序的机器 (Docker直接安装在操作系统之上)
    客户端(Client)    ->    命令行
    仓库(Registry)    ->    用来保存各种打包好的软件镜像
    镜像(Images)    ->    软件打包好的镜像,放在docker仓库中
    容器(Container)    ->    运行中的这个镜像称为容器,容器启动是非常快速的!

#Ubuntu安装
    $ uname -r                            //内核版本必须是3.10及以上
    $ apt-get install docker.io            //安装Docker -(可能存在权限错误,使用时添加 sudo 前缀)
    $ service docker status/start        //启动服务和守护进程
    $ docker -v                            //检测是否安装成功
    $ ln -sf /usr/bin/docker.io /usr/local/bin/docker    //创建软连接-(方便使用docker命令)
    
    1.权限问题
        #permission denied. Are you trying to connect to a TLS-enabled daemon without TLS?
        #注意: 默认情况,执行 docker 都需要运行 sudo 命令. 如何免去 sudo?
        sudo groupadd docker            //如果还没有 docker group 就添加一个
        sudo gpasswd -a ${USER} docker    //将用户加入该 group 内.然后退出并重新登录就生效啦
        sudo service docker restart        //重启 docker 服务
        newgrp - docker                    //切换当前会话到新 group

#CentOS安装
$ yum install docker
$ systemctl start/restart docker
$ docker -v                        //docker版本
$ systemctl enable docker        //开机启动
    
#相关命令 
    //http://www.runoob.com/docker/docker-command-manual.html
    1.状态
service docker status(SSR)
docker info
        
    2.镜像
docker search mysql
docker pull mysql:5.6.7
/// docker pull registry.docker-cn.com/library/mysql:5.6.7 ---> 官方加速

docker images [-q]                //-q: 只显示id
docker rmi [-f] IMAGE_ID
docker rmi $(docker image -q)    //删除所有

docker inspect IMAGE_ID            //相关信息
docker tag IMAGE_ID NEW_NAME:NEW_TAG //拷贝重命名
    
    3.容器
docker ps [-a]                //运行中的容器.(-a: 所有)
docker start(SSR) CONTAINER //容器的启动,停止,重启
docker rm CONTAINER            //移除容器(停止状态); rm->移除容器; rmi->移除镜像!

docker top CONTAINER        //容器内进程
docker inspect CONTAINER    //容器相关信息

docker logs [-t] [--tail 10] CONTAINER    //容器日志(-t: 显示时间, --tail: 最新10条)
    
    4.互动
docker exec -it CONTAINER /bin/bash        //进入容器.(exit: 退出)

docker cp CONTAINER:SRC_PATH DEST_PATH    //拷出来
docker cp DEST_PATH CONTAINER:SRC_PATH    //拷进去
    
#启动容器(docker run)
(0).--name ---> 为容器指定一个名称 //--name ES01
(1).-d ---> 后台运行容器,并返回容器ID

(2).-e ---> 设置环境变量  //-e ES_JAVA_OPTS="-Xms256m -Xmx256m"
    
(3).-p ---> 端口映射,格式为: 主机(宿主):容器 //-p 9200:9200

(4).-v ---> 容器启动时,挂载宿主机的目录作为配置文件 //-v /conf/mysql:/etc/mysql/conf.d

(5).-it --> 配合 exec 使用,开启一个交互模式的终端    
    
#DEMO-RUN
    0.ES
//后台启动ES,指定内存大小,端口号,及自定义名称
//ES的web通信使用 9200, 分布式集群的节点间通信使用 9300
docker run --name ES01 -d -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -p 9200:9200 -p 9300:9300 4f7e4c61f09d

//将 IK 插件解压到宿主机,然后配置docker容器加载宿主机 /plugins 目录.
docker run --name ES02 -d -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -p 9201:9200 -p 9301:9300 -v /var/tmp/plugins:/usr/share/elasticsearch/plugins 4f7e4c61f09d

    1.tomcat
//最后参数 ---> 镜像名:版本号(latest可省)
docker run --name tomcat01 -d -p 9090:8080 tomcat:8.5-jre8-alpine
        
    2.mysql
//指定root密码
docker run --name mysql01 -d -p 33066:3306 -e MYSQL_ROOT_PASSWORD=123456 mysql

//启动,并指定一些配置参数
docker run --name mysql02 -d -e MYSQL_ROOT_PASSWORD=123456 mysql:tag --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

//将上述配置参数保存到宿主机文件'/conf/mysql'
//启动,指定 docker-mysql 加载宿主机的配置文件. 即可以修改宿主机文件来配置mysql
docker run --name mysql02 -d -e MYSQL_ROOT_PASSWORD=123456 mysql:tag -v /conf/mysql:/etc/mysql/conf.d
        
    3.mysql-8.0.4
///对于 8.0.4 之后的mysql版本,不能简单的通过 '-e MYSQL_ROOT_PASSWORD=123456' 来指定root密码.
docker exec -it 1457d60b0375  /bin/bash //进入mysql所在docker

mysql -u root -p //进入docker-mysql

ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '123456'; //修改root密码

exit //执行两次,依次退出 docker-mysql, docker.
    
#构建镜像
    1.commit
//通过已有的容器,打包成新的镜像
// -a: 作者相关; -m: 描述信息; mysql01: 已有容器; skyl/mysql: 新镜像
docker commit -a 'skyl' -m 'hello skyl' mysql01 skyl/mysql

//使用新镜像
docker run --name skyl-mysql -d -e MYSQL_ROOT_PASSWORD=123456 mysql
        
    2.build
#First Dockerfile                //#   : 注释
FROM ubuntu:14.01                //FROM: 基础镜像, 必须写在第一行
MAINTAINER skyl 'skyl@qq.com'    //MAI*: 作者相关
RUN apt-get update                //RUN : 
RUN apt-get install -y nginx
EXPOSE 80                        //运行该容器所使用的端口

//build-构建(dockerfile所在目录)
docker build -t 'skyl-nginx' /var/tmp/docker/
    
#镜像加速
aliyun加速
    https://cr.console.aliyun.com/cn-hangzhou/mirrors
    
daocloud加速
    https://hub.daocloud.io/

//直接设置 –registry-mirror 参数,仅对当前的命令有效 
docker run hello-world --registry-mirror=https://docker.mirrors.ustc.edu.cn

//修改 /etc/default/docker,加入 DOCKER_OPTS=”镜像地址”,可以有多个 
DOCKER_OPTS="--registry-mirror=https://docker.mirrors.ustc.edu.cn"

//支持 systemctl 的系统,通过 sudo systemctl edit docker.service
//会生成 etc/systemd/system/docker.service.d/override.conf 覆盖默认的参数,在该文件中加入如下内容
[Service] 
ExecStart= 
ExecStart=/usr/bin/docker -d -H fd:// --registry-mirror=https://docker.mirrors.ustc.edu.cn

//新版的 Docker 推荐使用 json 配置文件的方式,默认为 /etc/docker/daemon.json
//非默认路径需要修改 dockerd 的 –config-file,在该文件中加入如下内容
{"registry-mirrors": ["https://docker.mirrors.ustc.edu.cn"]}        
        
//}

//{--------<<<ES>>>----------------------------------X
#搜索分两类: 搜索引擎, 站内搜索.
#ES & Solar: 前者可以实现'分布式搜索',后者依赖插件.

#下载启动
    docker search elasticsearch //以docker形式,检索
    docker pull registry.docker-cn.com/library/elasticsearch //下载,镜像加速
    
    //后台启动ES,指定内存大小,端口号,及自定义名称
    //ES的web通信使用 9200, 分布式集群的节点间通信使用 9300
    docker run --name ES01 -d -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -p 9200:9200 -p 9300:9300 4f7e4c61f09d
    
    http://192.168.5.23:9200/ //检测是否启动成功
    
#基础概念
    1.ES与MySQL逻辑结构的概念对比
        索引(index)            库(database)
        类型(type)            表(table)
        文档(document)        行(row)
        //1个ES集群 --> 多个索引(index) --> 多个类型(type) --> 多个文档(document) --> 多个属性.
        
    2.DEMO说明
        每个雇员对应一个文档,包含该雇员的所有信息
        每个文档都将是 employee 类型
        该类型位于 索引 megacorp 内
        该索引保存在我们的 Elasticsearch 集群中
        
#基础操作        
    0.新建索引(库)
        PUT http://192.168.5.23:9200/megacorp
        
    1.新增文档(行)
        PUT http://192.168.5.23:9200/megacorp/employee/1
        {
            "first_name": "二狗",
            "last_name":  "王",
            "age":        30,
            "about":      "没事打游戏",
            "interests":  ["吹牛", "农药"]
        }
             
    2.修改文档
//类似 SpringData 的更新API,新增时: 有ID则更新,无ID则新增
PUT  http://192.168.5.23:9200/megacorp/employee/1
{
    "first_name": "二狗",
    "last_name":  "王",
    "age":        33,
    "about":      "没事打游戏",
    "interests":  ["吹牛", "农药"]
}
        
    3.判断是否存在
        //根据响应码判断, 200: 存在; 404: 不存在.
        HEAD http://192.168.5.23:9200/megacorp/employee/4
        
    4.删除文档
        DELETE http://192.168.5.23:9200/megacorp/employee/4
        
    5.查询所有文档
        GET  http://192.168.5.23:9200/megacorp/employee/_search
        
    6.查询根据ID
        GET  http://192.168.5.23:9200/megacorp/employee/1
        
    7.查询根据条件
        //first_name 中包含"狗"
        GET  http://192.168.5.23:9200/megacorp/employee/_search?q=first_name:狗
        
    8.查询根据表达式
        //first_name 包含"狗"
        POST  http://192.168.5.23:9200/megacorp/employee/_search
        {
            "query" : {
                "match" : {
                    "first_name" : "狗"
                }
            }
        }
        
        //first_name 包含"狗", age > 30
        POST  http://192.168.5.23:9200/megacorp/employee/_search
        {
            "query" : {
                "bool": {
                    "must": {
                        "match" : {
                            "first_name" : "狗" 
                        }
                    },
                    "filter": {
                        "range" : {
                            "age" : { "gt" : 30} 
                        }
                    }
                }
            }
        }
    
#整合boot
    SpringBoot 默认支持两种技术和ES进行交互: jest, SpringData(默认).
    
#(1).jest    
        // <!-- https://mvnrepository.com/artifact/io.searchbox/jest -->
        <dependency>
            <groupId>io.searchbox</groupId>
            <artifactId>jest</artifactId>
            <version>5.3.4</version>
        </dependency>
 
        //#properties
        spring.elasticsearch.jest.uris=192.168.5.23:9200

#(2).SpringData(默认)
        <dependency>
            <groupId>org.springframework.data</groupId>
            <artifactId>spring-data-elasticsearch</artifactId>
        </dependency>
        
        //#properties
        spring.data.elasticsearch.cluster-name=elasticsearch
        spring.data.elasticsearch.cluster-nodes=192.168.5.23:9300
        
    0.javabean
        public @interface Document {
            String indexName(); //索引库的名称,个人建议以项目的名称命名
             
            String type() default ""; //类型,个人建议以实体的名称命名
             
            short shards() default 5; //默认分区数
             
            short replicas() default 1; //每个分区默认的备份数
             
            String refreshInterval() default "1s"; //刷新间隔
             
            String indexStoreType() default "fs"; //索引文件存储类型
        }
        
        public @interface Field {
            FieldType type() default FieldType.Auto; //自动检测属性的类型,可以根据实际情况自己设置
             
            FieldIndex index() default FieldIndex.analyzed; //默认情况,一般默认分词就好,除非这个字段你确定查询时不会用到
             
            DateFormat format() default DateFormat.none; //时间类型的格式化
             
            String pattern() default ""; 
             
            boolean store() default false; //默认情况下不存储原文
             
            String searchAnalyzer() default ""; //指定字段搜索时使用的分词器
             
            String indexAnalyzer() default ""; //指定字段建立索引时指定的分词器
             
            String[] ignoreFields() default {}; //如果某个字段需要被忽略
             
            boolean includeInParent() default false;
        }
    
        @Document(indexName = "book", type = "article")
        public class Article {
            //mysql中的id,非索引库中的id
            @Id
            private String id;

            // 对应索引库中的域
            // index: 是否被索引(能否被搜索); 是否分词(搜索时是整体匹配还是分词匹配); 是否存储(是否在页面上显示)
            // analyzer: 存储时使用的分词策略
            // searchAnalyzer 查询时的.....(二者必须一致)
            @Field(index = true, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
            private String title;

            @Field(index = true, analyzer = "ik_smart", searchAnalyzer = "ik_smart")
            private String content;
        }
    
    1.ElasticsearchTemplate(方式1,略)
    
    2.ElasticsearchRepository(方式2)
        @Repository
        public interface ArticleDao extends ElasticsearchRepository<Article, java.lang.String> {
            List<Article> findByTitleLike(String title); //查询接口
        }
        
        @Autowired
        ArticleDao articleDao;

        @Test
        public void test() {
            Object index = articleDao.index(new Article("1", "三国演义", "群雄逐鹿中原")); //新增

            List<Article> articleList = articleDao.findByTitleLike("三"); //查询
        }
    
        
//}

//{--------<<<IK分词>>>------------------------------X
#Windows版
    //下载,解压,拷贝到ES的 '/plugins' 目录,重启ES即可
    https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v5.6.12/elasticsearch-analysis-ik-5.6.12.zip            
    
#Docker版
    //(F1).下载(版本一定要对应),解压,拷贝到docker容器,重启ES
    docker exec -it ES01 /bin/bash
    docker cp /var/lib/elasticsearch-analysis-ik-5.6.12 ES01:/usr/share/elasticsearch/plugins
    
    //(F2).下载,解压,启动image时挂载外部配置文件
    docker run --name ES02 -d -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -p 9201:9200 -p 9301:9300 -v /var/tmp/plugins:/usr/share/elasticsearch/plugins 4f7e4c61f09d
    
#两种测试
    //最少切分: 中华人民共和国, 国歌
    http://localhost:9200/_analyze?pretty&analyzer=ik_smart&text=中华人民共和国国歌
    
    //最细切分: 中华人民共和国,中华人民,中华,华人,人民共和国,人民,共和国,共和,国歌
    http://localhost:9200/_analyze?pretty&analyzer=ik_max_word&text=中华人民共和国国歌

#自定义词库
    //默认不作为一个词
    http://localhost:9200/_analyze?pretty&analyzer=ik_smart&text=人艰不拆
    
    (1).在'/elasticsearch/plugins/ik/config/'新建 my.dic (编码UTF-8),编写内容'人艰不拆'            
        
    (2).修改'/~/ik/config/IKAnalyzer.cfg.xml',重启ES
        <properties> 
            <comment>IK Analyzer 扩展配置</comment> 
            <entry key="ext_dict">my.dic</entry> //配置此项
            <entry key="ext_stopwords"></entry>
            
            <entry key="remote_ext_dict">location</entry> 
            <entry key="remote_ext_stopwords">location</entry> 
        </properties>
//}

//{--------<<<logstash>>>----------------------------x
#开源的服务器端数据处理管道,可以同时从多个数据源获取数据,并对其进行转换,然后将其发送到指定的"存储目的地".

#Docker安装,运行并加载自定义配置
    1.新增配置文件'mysql.conf'
        input {
            stdin {
            }
            jdbc {
              # mysql 数据库链接,shop为数据库名
              jdbc_connection_string => "jdbc:mysql://127.0.0.1:3306/shop"
              # 用户名和密码
              jdbc_user => "root"
              jdbc_password => "123456"
              # 驱动
              jdbc_driver_library => "./elasticsearch/logstash-5.5.2/mysqletc/mysql-connector-java-5.0.8.jar"
              jdbc_driver_class => "com.mysql.jdbc.Driver"
              # 分页相关
              jdbc_paging_enabled => "true"
              jdbc_page_size => "50000"
              # 执行的sql 文件路径+名称
              statement_filepath => "./elasticsearch/logstash-5.5.2/mysqletc/shop.sql"
              # 设置监听间隔  各字段含义(由左至右)分、时、天、月、年,全部为*默认含义为每分钟都更新
              schedule => "* * * * *"
              # 索引类型
              type => "content"
            }
        }
         
        filter {
            json {
                source => "message"
                remove_field => ["message"]
            }
        }
         
        output {
            elasticsearch {
                # ES信息, %{id} 表示使用上述sql结果的id
                hosts => ["localhost:9200"]
                index => "shop_content"
                document_id => "%{id}"
            }
            stdout {
                codec => json_lines
            }
        }
    
    2.加载配置文件启动
        logstash -f ../mysqletc/mysql.conf

//}


//{--------<<<RabbitMQ>>>----------------------------X
#消息队列中间件
    常用: ActiveMQ(*), RabbitMQ(*), Kafka(*), ZeroMq, MetaMQ, RocketMQ
    
    执行速度(安全性相反): K > R > A
    
#功能作用
    1.流量削峰(秒杀服务)
        服务器接收用户请求后,首先写入消息队列,依次处理.
        假如消息队列长度超过最大数量,则直接抛弃用户请求或跳转到错误页面

    2.同步变异步
        //(1).原始过程: 用户下单 到 生成订单,总共花费 60ms, 同步过程,强耦合.
        用户下单 -> 短信通知(20ms) -> 邮件通知(20ms) -> app通知(20ms) --> 生成订单....
        
        //(2).线程池技术: 自己实现线程池,强耦合
        用户下单 -> 短信通知(thread) -> 邮件通知(thread) -> app通知(thread) --> 生成订单....
        
        //(3).消息机制: 异步,解耦
        用户下单 -> 短信通知(msg) -> 邮件通知(msg) -> app通知(msg) --> 生成订单....
        
        @Test
        public void sendSms(String mobile) {
            //(1).生成验证码
            String checkCode = RandomStringUtils.randomNumeric(6); //org.apache.commons.lang3

            //(2).存入redis-5分钟失效
            redisTemplate.opsForValue().set("checkCode_" + mobile, checkCode, 5, TimeUnit.MINUTES);

            //(3).发送消息RabbitMQ-短信验证
            JSONObject object = new JSONObject();
            object.put("mobile", mobile);
            object.put("checkCode", checkCode);
            rabbitTemplate.convertAndSend("spring.sms", object);
        }
    
#核心概念
    1.Message
        消息, 由消息头和消息体组成. 消息体不透明, 而消息头由一系列的可选属性组成:
            routing-key        --> 路由键
            priority        --> 相对于其他消息的优先权
            delivery-mode    --> 该消息可能需要持久性存储
    
    2.Publisher
        消息的生产者,一个向交换器(Exchange)发布消息的客户端应用程序
        
    3.Consumer
        消息的消费者, 表示一个从消息队列中取得消息的客户端应用程序
    
    4.Queue
        消息队列, 用来保存消息直到发送给消费者.    它是消息的容器,也是消息的终点
        
        一个消息可投入一个或多个队列. 消息一直在队列里面,等待消费者连接到这个队列将其取走.
        
    5.Exchange
        交换器, 用来接收生产者发送的消息并将该消息路由给服务器中的队列.
        1.Message(2.Publisher) --> 4.Exchange --> 5.Queue(3.Consumer)
        
        Exchange(4种类型): direct(默认), fanout, topic, headers(几乎不用).
    
    6.Binding
        绑定, 用于消息队列和交换器之间的关联. 
        一个绑定就是基于路由键将交换器和消息队列连接起来的路由规则,所以可以将交换器理解成一个由绑定构成的路由表
        ///Exchange 和 Queue 的绑定可以是多对多的关系.
    
    7.Routingkey
        路由键. RabbitMQ 决定消息该投递到哪个队列的规则
        队列通过路由键绑定到交换器
        消息发送到 MQ 服务器时,消息将拥有一个路由键,即便是空的,RabbitMQ 也会将其和绑定使用的路由键进行匹配
        如果相匹配,消息将会投递到该队列
        如果不匹配,消息将会进入黑洞
        
    8.Connection
        链接. RabbitMQ 服务器和服务建立的 TCP 链接.
        
    9.Channel
        信道. TCP 里面的虚拟链接. 
        例如: 电缆相当于 TCP,信道是一个独立光纤束,一条 TCP 连接上创建多条信道是没有问题的
        TCP 一旦打开,就会创建 AMQP 信道
        无论是发布消息,接收消息,订阅队列,这些动作都是通过信道完成的
        
    10.VirtualHost
        虚拟主机. 表示一批交换器,消息队列和相关对象
        虚拟主机是共享相同的身份认证和加密环境的独立服务器域.
        每个 vhost 本质上就是一个 mini 版的 RabbitMQ 服务器,拥有自己的队列,交换器,绑定和权限机制
        vhost 是 AMQP 概念的基础,必须在链接时指定,RabbitMQ 默认的 vhost 是/
        
    11.Borker
        表示消息队列服务器实体
        
#两个问题
    0.交换器和队列的关系
        交换器是通过'路由键'和队列绑定在一起的,
        如果消息拥有的路由键跟队列和交换器的路由键匹配,那么消息就会被路由到该绑定的队列中.
        也就是说, 消息到队列的过程中,消息首先会经过交换器,接下来交换器在通过路由键匹配分发消息到具体的队列中
        //路由键 -> 可以理解为匹配的规则
        
    1.RabbitMQ为什么需要信道—为什么不是TCP直接通信
        (1).TCP 的创建和销毁开销特别大. 创建需要 3 次握手,销毁需要 4 次分手.
        
        (2).使用 TCP 链接 Rabbit,高峰时每秒成千上万条链接会造成资源巨大的浪费,
            而且操作系统每秒处理 TCP 链接数也是有限制的,必定造成性能瓶颈
        
        (3).信道的原理是一条线程一个通道, 多条线程多个通道,共用一条 TCP 链接.
            一条 TCP 链接可以容纳无限的信道,即使每秒成千上万的请求也不会成为性能的瓶颈

#消息可靠性
    消息的可靠性是 RabbitMQ 的一大特色,那么 RabbitMQ 是如何保证消息可靠性的呢？
        ——> 消息持久化,    以及消息确认-ACK.
        
    0.消息持久化
        /// @param durable        是否持久化,即 rabbitmq 重启是否还存在?? 默认 true
        /// @param autoDelete    当所有绑定队列都不在使用时,是否自动删除交换器?? 默认 false
    
    1.什么是消息确认ACK
        如果消费者服务器在处理消息的过程中出现异常,那这条正在处理的消息就没有完成销费,
        数据就会丢失. 为确保数据不会丢失,RabbitMQ 支持消息确认ACK.(默认开启)
        
    2.ACK的消息确认机制
        消费者处理完消息后反馈给 RabbitMQ, RabbitMQ 收到反馈后才将消息从队列中删除.
        
        (1).对于单个消费者. 消费出现异常,则不会有 ACK-反馈. RabbitMQ 就认为此消息没有
            正常消费,则会将消息'重新放入队列'.
        (2).对于集群消费者. 单个消费节点出现异常,RabbitMQ 则会立即将这个消息推送给
            其他节点的消费者,保证消息不会丢失.
        (3)./** ACK-确认机制默认开启. 没有收到ACK,则消息永远不会从 RabbitMQ 删除.*/
        
    3.ACK机制注意
        如果忘了 ACK,后果很严重. 当消费者退出后,消息会一直重新分发, 然后 RabbitMQ 内存
        也越来越大. 长时间运行就会导致'内存泄露'. //解决方案
        //(1).消息消费端,添加 try-catch 异常捕获

        //(2).添加配置,开启异常重试,添加最大重试次数,默认3
        spring.rabbitmq.template.retry.enabled=true
        spring.rabbitmq.template.retry.max-attempts=3 ///发送端

        spring.rabbitmq.listener.direct.retry.max-attempts=3
        spring.rabbitmq.listener.simple.retry.enabled=true ///接收端
    
#docker启动
    //必须下载'management'版本才能有管理界面
    // 4369: erlang发现; 5672: client通信; 15672: UI管理界面; 25672: server间内部通信    
    docker run --name rabbitmq01 -d -p 5671:5671 -p 5672:5672 -p 4369:4369 -p 15671:15671 -p 15672:15672 -p 25672:25672 rabbitmq
    
    http://localhost:15672/ //UI管理页面,默认用户名密码: guest
        
#boot整合
    0.先决条件
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
        
        #rabbitmq
        spring.rabbitmq.host=192.168.5.23
        spring.rabbitmq.port=5672
        spring.rabbitmq.username=guest //默认,可省
        spring.rabbitmq.password=guest
        
        ///全局注解   + 监听注解
        @EnableRabbit + @RabbitListener
        
    1.序列化器 ---> ///默认以java序列化,现配置json序列化
        @Configuration
        public class AMQPConfig {
            @Bean
            public MessageConverter messageConverter() {
                return new Jackson2JsonMessageConverter(); 
            }
        }
        
    2.代码创建和删除—Queue-Exchange-Binding
        @Test
        public void admin() {
            /**
             * @param name         队列名称
             * @param durable     是否持久化,即 rabbitmq 重启是否还存在?? 默认 true
             * @param exclusive     排他性. true: 申明这个queue的connection断了,那么这个队列就被删除了,包括里面的消息. 默认 false
             * @param autoDelete 当所有消费客户端连接断开后,是否自动删除队列?? 默认 false
             */
            amqpAdmin.declareQueue(new Queue("queue.admin.0", true, false, false)); //创建
            amqpAdmin.deleteQueue("queue.admin.0"); //移除

            /**
             * @param name         交换器名称
             * @param durable     是否持久化,即 rabbitmq 重启是否还存在?? 默认 true
             * @param autoDelete 当所有绑定队列都不在使用时,是否自动删除交换器?? 默认 false
             */
            amqpAdmin.declareExchange(new FanoutExchange("exchange.admin", true, true));

            amqpAdmin.declareBinding(new Binding("queue.admin.0", Binding.DestinationType.QUEUE,
                    "exchange.admin", "admin.#", null)); //绑定(通道,交换器,路由键)
        }
        
    3.直接模式 --> direct(1对1) --> Msg 直接发送到 Queue
        //页面访问 http://192.168.5.23:15672/#/queues  Add a new queue --> queue.direct
        
        @Component ///消费者
        public class Customer {

            //启动2个消费者都监听"direct.queue",
            //则每次发送消息只会被2者中的一个接收到,并且负载均衡,每个接收者收到消息概率相同
            @RabbitListener(queues = "queue.direct")
            public void recvDirect0(Dog dog) {
                System.out.println("queue.direct-0: " + dog);
            }

            @RabbitListener(queues = "queue.direct")
            public void recvDirect1(Dog dog) {
                System.out.println("queue.direct-1: " + dog);
            }
        }

        @Test ///生产者
        public void send() {
            /**
             * @param exchange     交换器,点对点模式-不用指定
             * @param routingKey 路由键
             * @param object     消息对象
             */
            rabbitTemplate.convertAndSend("", "queue.direct", new Dog(18, "小王"));
        }

    4.路由模式 --> fanout(多对多) --> Msg 发送到 Exchange,路由到绑定的 Queue
        //新增: queue.fanout.0, queue.fanout.1
        //新增: exchange.fanout (Type: fanout)
        
        //绑定: exchange.fanout <--> queue.fanout.0 queue.fanout.1

        @Component ///消费者
        public class Customer {

            @RabbitListener(queues = "queue.fanout.0")
            public void recvDirect0(Dog dog) {
                System.out.println("queue.fanout.0: " + dog);
            }

            @RabbitListener(queues = "queue.fanout.1")
            public void recvDirect1(Dog dog) {
                System.out.println("queue.fanout.1: " + dog);
            }
        }
        
        @Test ///生产者
        public void send() {
            /**
             * @param exchange     交换器
             * @param routingKey 路由键,路由模式-不用指定
             * @param object     消息对象
             */
            rabbitTemplate.convertAndSend("exchange.fanout", "", new Dog(18, "小王"));
        }

    5.主题模式 --> topic(多对多) --> Exchange 可以模糊匹配路由键,类似于SQL中 =和like 的关系
        //新增: queue.topic.0, queue.topic.1, queue.topic.2
        //新增: exchange.topic (Type: topic)
        
        //绑定(Routing_key)
        // exchange.topic --> queue.topic.0    -> blue.# //#匹配一个或多个字符, *匹配一个字符
        // exchange.topic --> queue.topic.1    -> #.log
        // exchange.topic --> queue.topic.2    -> blue.log
        
        @Component ///消费者
        public class Customer {

            @RabbitListener(queues = "queue.topic.0")
            public void recvDirect0(Dog dog) {
                System.out.println("queue.fanout.0: " + dog);
            }

            @RabbitListener(queues = "queue.topic.1")
            public void recvDirect1(Dog dog) {
                System.out.println("queue.fanout.1: " + dog);
            }

            @RabbitListener(queues = "queue.topic.2")
            public void recvDirect2(Dog dog) {
                System.out.println("queue.fanout.2: " + dog);
            }
        }
        
        @Test ///生产者
        public void send() {
            rabbitTemplate.convertAndSend("exchange.topic", "blue.x", new Dog(18, "blue.x"));          //0
            rabbitTemplate.convertAndSend("exchange.topic", "x.log", new Dog(18, "x.log"));              //1
            rabbitTemplate.convertAndSend("exchange.topic", "blue.log", new Dog(18, "blue.log"));      //0,1,2
            rabbitTemplate.convertAndSend("exchange.topic", "blue.x.log", new Dog(18, "blue.x.log")); //0,1
        }
    
//}

//{--------<<<Email>>>-------------------------------X
#依赖配置
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-mail</artifactId>
        </dependency>
        
        //邮箱开启SMTP功能: https://blog.csdn.net/caimengyuan/article/details/51224269
        spring.mail.host=smtp.163.com
        spring.mail.username=***@163.com
        spring.mail.password=*** //授权码作为密码使用

#邮件(普通*附件*静态资源*模板)
    @RestController
    @RequestMapping("mail")
    public class MailController {
        private static final String EMAIL_FROM = "dongyan3131@163.com";
        private static final String EMAIL_TO = "453705197@qq.com";
        private static final String EMAIL_SUBJECT = "主题：邮件主题";

        @Autowired
        JavaMailSender mailSender;

        @Autowired
        private TemplateEngine templateEngine;

        @GetMapping("/simple") //普通
        public void simpleEmail() {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(EMAIL_FROM);
            message.setTo(EMAIL_TO);
            message.setSubject(EMAIL_SUBJECT);
            message.setText("内容：邮件内容");

            mailSender.send(message);
        }

        @GetMapping("/attach") //三种复杂邮件
        public void attachEmail() throws Exception {

            // 含附件, 静态资源, 模板,则增加第二个参数,并为true
            MimeMessage mimeMessage = this.mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(EMAIL_FROM); // 发送方
            helper.setTo(EMAIL_TO); // 接收方
            helper.setSubject(EMAIL_SUBJECT); //主题

            // 1.附件
            File file = new File(SystemUtils.getFilePath(), "/logs/sm/sm.log");//附件位置
            if (file.exists()) {
                String path = file.getPath();
                String fileName = path.substring(path.lastIndexOf(File.separator) + 1);
                helper.addAttachment(fileName, file); //添加附件(附件名,附件路径)
            }

            // 2.静态资源 -> 在邮件正文中查看图片,而非附件
            String sb = "<h1>大标题-h1</h1>" +
                    "<p style='color:#F00'>红色字</p>" +
                    "<p style='text-align:right'>右对齐</p>" +
                    "<p><img src=\"cid:weixin\"></p>";
            helper.setText(sb, true); //true表示启动HTML格式的邮件
            file = new File(SystemUtils.getFilePath(), "/imgs/a.jpg");
            if (file.exists()) {
                // 注意: 资源名称"weixin" 需要与正文中 cid:weixin 对应起来
                helper.addInline("weixin", file);
            }

            // 3.模板邮件 -> 固定的场景,如重置密码、注册确认等,只有小部分是变化的
            org.thymeleaf.context.Context context = new org.thymeleaf.context.Context();
            context.setVariable("username", "skyl");
            String content = templateEngine.process("email", context);
            helper.setText(content, true);

            mailSender.send(mimeMessage);
        }
    }
        
//}

//{--------<<<SMS>>>---------------------------------X
#短信服务使用'阿里云通信'
    (1).注册, 登陆, 实名, 产品选择'短信服务'
    (2).申请签名, 申请模板, 创建 AccessKey, 充值
    
#代码编写
    1.pom.xml
        //<!-- https://mvnrepository.com/artifact/com.aliyun/aliyun-java-sdk-dysmsapi -->
        <dependency>
            <groupId>com.aliyun</groupId>
            <artifactId>aliyun-java-sdk-dysmsapi</artifactId>
            <version>1.1.0</version>
        </dependency>
        //<!-- https://mvnrepository.com/artifact/com.aliyun/aliyun-java-sdk-core -->
        <dependency>
            <groupId>com.aliyun</groupId>
            <artifactId>aliyun-java-sdk-core</artifactId>
            <version>3.2.8</version>
        </dependency>
        
    2.properties
        aliyun.sms.accessKeyId=*
        aliyun.sms.accessKeySecret=*
        aliyun.sms.templateCode=sms_20190328    //模板CODE
        aliyun.sms.signName=短信测试            //签名名称
        
    3.SMS工具类
        package com.example.spring.util;

        import com.aliyuncs.DefaultAcsClient;
        import com.aliyuncs.IAcsClient;
        import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
        import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
        import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
        import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
        import com.aliyuncs.exceptions.ClientException;
        import com.aliyuncs.profile.DefaultProfile;
        import com.aliyuncs.profile.IClientProfile;

        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.core.env.Environment;
        import org.springframework.stereotype.Component;

        import java.text.SimpleDateFormat;
        import java.util.Date;

        /**
         * 短信工具类
         *
         * @author Administrator
         */
        @Component
        public class SmsUtil {

            //产品名称:云通信短信API产品,开发者无需替换
            static final String product = "Dysmsapi";
            //产品域名,开发者无需替换
            static final String domain = "dysmsapi.aliyuncs.com";

            @Autowired
            private Environment env;

            // TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)

            /**
             * 发送短信
             *
             * @param mobile        手机号
             * @param template_code 模板号
             * @param sign_name     签名
             * @param param         参数
             * @return
             * @throws ClientException
             */
            public SendSmsResponse sendSms(String mobile, String template_code, String sign_name, String param) throws
                    ClientException {
                String accessKeyId = env.getProperty("aliyun.sms.accessKeyId");
                String accessKeySecret = env.getProperty("aliyun.sms.accessKeySecret");
                //可自助调整超时时间
                System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
                System.setProperty("sun.net.client.defaultReadTimeout", "10000");
                //初始化acsClient,暂不支持region化
                IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
                DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
                IAcsClient acsClient = new DefaultAcsClient(profile);
                //组装请求对象-具体描述见控制台-文档部分内容
                SendSmsRequest request = new SendSmsRequest();
                //必填:待发送手机号
                request.setPhoneNumbers(mobile);
                //必填:短信签名-可在短信控制台中找到
                request.setSignName(sign_name);
                //必填:短信模板-可在短信控制台中找到
                request.setTemplateCode(template_code);
                //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
                request.setTemplateParam(param);
                //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
                //request.setSmsUpExtendCode("90997");
                //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
                request.setOutId("yourOutId");
                //hint 此处可能会抛出异常，注意catch
                SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
                return sendSmsResponse;
            }

            public QuerySendDetailsResponse querySendDetails(String mobile, String bizId) throws ClientException {
                String accessKeyId = env.getProperty("accessKeyId");
                String accessKeySecret = env.getProperty("accessKeySecret");
                //可自助调整超时时间
                System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
                System.setProperty("sun.net.client.defaultReadTimeout", "10000");
                //初始化acsClient,暂不支持region化
                IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
                DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
                IAcsClient acsClient = new DefaultAcsClient(profile);
                //组装请求对象
                QuerySendDetailsRequest request = new QuerySendDetailsRequest();
                //必填-号码
                request.setPhoneNumber(mobile);
                //可选-流水号
                request.setBizId(bizId);
                //必填-发送日期 支持30天内记录查询，格式yyyyMMdd
                SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
                request.setSendDate(ft.format(new Date()));
                //必填-页大小
                request.setPageSize(10L);
                //必填-当前页码从1开始计数
                request.setCurrentPage(1L);
                //hint 此处可能会抛出异常，注意catch
                QuerySendDetailsResponse querySendDetailsResponse = acsClient.getAcsResponse(request);
                return querySendDetailsResponse;
            }
        }

    4.SMS发送
        @Value("${aliyun.sms.templateCode}")
        String templateCode;
        
        @Value("${aliyun.sms.signName}")
        String signName;
        
        @Autowired
        SmsUtil smsUtil;
        
        //发送短信
        smsUtil.sendSms(moblieNum, templateCode, signName, "{\"checkCode\":\"" + checkCode + "\"}");
        
#用户注册
    1.发送短信Ctrl
        @PostMapping("/sendSms/{mobile}")
        public String sendSms(@PathVariable String mobile) {
            if (StringUtils.isEmpty(mobile)) { //正则检测略
                return "手机号不合法";
            }
            return HelloService.sendSms(mobile);
        }
        
    2.发送短信Service
        public void sendSms(String mobile) {
            //(1).生成验证码
            String checkCode = RandomStringUtils.randomNumeric(6); //org.apache.commons.lang3

            //(2).存入redis-5分钟失效
            redisTemplate.opsForValue().set("checkCode_" + mobile, checkCode, 5, TimeUnit.MINUTES);

            //(3).发送消息RabbitMQ-短信验证
            JSONObject object = new JSONObject();
            object.put("mobile", mobile);
            object.put("checkCode", checkCode);
            rabbitTemplate.convertAndSend("spring.sms", object);
        }

    3.用户注册C
        @PostMapping("/regist/{mobile}/{checkCode}")
        public String regist(@PathVariable String mobile, @PathVariable String checkCode) {
            if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(checkCode)) { //正则检测略
                return "手机号或验证码不合法";
            }
            return HelloService.regist(mobile, checkCode);
        }
    
    4.用户注册S
        //service, dao --> 抛出异常; Controller/全局异常处理器 --> 处理异常.
        private void regist(String mobile, String checkCode) {
            if (StringUtils.isEmpty(checkCode))
                throw new RuntimeException("请输入验证码");

            if (!smsCodeRedis.equalsIgnoreCase(checkCode))
                throw new RuntimeException("验证码不正确或已过期");

            personDao.add(person); //写库
        }
        
//}

//{--------<<<BCrypt加密>>>--------------------------X
#SpringSecurity 提供的 BCrypt 强哈希方法每次加密的结果都不一样. 
    0.pom.xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
    
        @Bean
        public BCryptPasswordEncoder encoder(){
            return new BCryptPasswordEncoder();
        }
        
    1.测试加密
        @Autowired
        BCryptPasswordEncoder encoder;
        
        @Test
        public void encoder() {
            String encode = encoder.encode("123");
            System.out.println("encode: " + encode); //加密: $2a$10$ujGzEaaHHU0y72yzfwMk.OA0KUNpKfRFr291I5YuGqnWawmnAQV1y

            boolean matches = encoder.matches("123", "$2a$10$ujGzEaaHHU0y72yzfwMk.OA0KUNpKfRFr291I5YuGqnWawmnAQV1y");
            System.out.println("matches: " + matches); //比对: true
        }
        
    2.登陆逻辑S
        public User login(String loginName, String loginPwd) {
            User user = helloMapper.findByName(loginName);
            if (null != user && encoder.matches(loginPwd, user.getPwd())) {
                System.out.println("登陆成功");
                return user;
            } else {
                System.out.println("用户名或密码不正确");
                return null;
            }
        }


//}


//{--------<<<认证机制>>>----------------------------X
#有状态登陆 & 无状态登陆
    有状态: 服务端需要保存用户信息,如 SessionId
    
    无....: ......不需要..........

#HTTP Basic Auth(无状态)
    每次请求API时都提供用户的username和password.
    存在将用户名密码暴露给第三方客户端的风险, 避免使用.
    
#Cookie Auth(有状态)
    一次请求认证后, 在服务端创建一个Session对象,同时在客户端创建了一个Cookie对象.
    通过客户端每次请求带上来Cookie对象,与服务器端的Session对象匹配来实现状态管理.
    
    默认情况下, 当关闭浏览器时,Cookie会被删除, 可修改其 expire-time.

#OAuth
    一种授权的协议,第三方授权码模式. 详见<<<OAuth2.0>>>
    
#Token Auth(无状态)
    1. 客户端使用用户名跟密码请求登录
    2. 服务端收到请求,去验证用户名与密码
    3. 验证成功后,服务端会签发一个 Token,再把这个 Token 发送给客户端
    4. 客户端收到 Token 以后可以把它存储起来,比如放在 Cookie 里
    5. 客户端每次向服务端请求资源的时候需要带着服务端签发的 Token
    6. 服务端收到请求,然后去验证客户端请求里面带着的 Token. 如果验证成功,就向客户端返回请求的数据

#Token相对于Cookie又有什么好处呢?
    1.支持跨域访问 Cookie不允许, Token机制只需要传输的用户认证信息通过HTTP头传输.
    
    2.无状态 Token机制在服务端不需要存储Session信息.
        因为 Token 自身包含了所有登录用户的信息,只需要在客户端的cookie或本地介质存储状态信息.
        
    3.更适用CDN 可以通过内容分发网络请求你服务端的所有资料（如：javascript,
    HTML,图片等）,而你的服务端只要提供API即可.
    
    4.去耦 不需要绑定到一个特定的身份验证方案. Token可以在任何地方生成,
        只要在你的API被调用的时候,你可以进行Token生成调用即可.
    
    5.更适用于移动应用 当客户端是一个原生平台(iOS, Android等)时,
        Cookie是不被支持的(需要通过Cookie容器进行处理),这时采用Token认证机制就会简单得多
    
    6.CSRF 因为不再依赖于Cookie,所以不需要考虑对CSRF(跨站请求伪造)的防范
    
    7.性能 一次网络往返时间(通过数据库查询session信息)
        总比做一次 HMACSHA256 计算的Token验证和解析要费时得多.
    
    8.不需要为登录页面做特殊处理 
        如果使用 Protractor 做功能测试的时候,不再需要为登录页面做特殊处理.
        
    9.基于标准化 API可以采用标准化的 JSON Web Token (JWT). 
        这个标准已经存在多个后端库(.NET, Ruby, Java,Python, PHP)和多家公司的支持
        

//}

//{--------<<<JWT认证>>>-----------------------------X
#JSON Web Token 规范是一种 Token Auth,允许使用JWT在用户和服务器之间传递安全可靠的信息

#JWT三部分组成
    1.头部Header
        存储JWT的最基本信息. 如其类型,签名所用的算法等.
        
        //DEMO
        {"typ":"JWT","alg":"HS256"}
    
    2.载荷Playload
        存放有效信息的地方.包括:
        
        (A).标准中注册的声明(建议但不强制使用)
            iss: jwt签发者
            sub: jwt所面向的用户                                (*)
            aud: 接收jwt的一方
            exp: jwt的过期时间,这个过期时间必须要大于签发时间    (*)
            nbf: 定义在什么时间之前,该jwt都是不可用的
            iat: jwt的签发时间                                    (*)
            jti: jwt的唯一身份标识,主要用来作为一次性token,从而回避重放攻击
            
        (B).公共的声明
            一般添加用户自定义的信息,如用户角色.
            
        (C).私有的声明
        
        //DEMO        
        {"sub":"1234567890","name":"John Doe","admin":true}
    
    3.签名Signature
        是一个签证信息.
        由 BASE64加密后的header 和 BASE64加密后的payload使用 '.' 连接组成的字符串,
        然后通过header中声明的加密方式(如HS256)进行 加盐Secret 组合加密构成jwt的第三部分
        
        //注意: secret保存在服务器端, jwt签也在服务器端, secret就是用来进行jwt的签发和jwt的验证,
        //        所以,它就是你服务端的私钥,在任何场景都不应该流露出去.
        //        一旦客户端得知这个secret, 那就意味着客户端是可以自我签发jwt
    
#BASE64编码
    BASE64 是一种基于64个可打印字符来表示二进制数据的表示方法.
    JDK 中 BASE64Encoder 和 BASE64Decoder 用于 BASE64 的编码和解码.
    
    由于2的6次方等于64, 所以每6个byte为一个单元, 对应某个可打印字符.
    三个字节有24个byte, 对应于4个Base64单元, 即3个字节需要用4个可打印字符来表示.
    
    0.DEMO    
        @Test
        public void demo01() throws IOException {
            String encode = new BASE64Encoder().
                    encode("{\"typ\":\"JWT\",\"alg\":\"HS256\"}".getBytes(Charset.forName("UTF-8")));
            System.out.println("BASE64 编码: " + encode);

            String decode = new String(new BASE64Decoder().decodeBuffer("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9"));
            System.out.println("BASE64 解码: " + decode);
        }
    
#Demo
    1.工具类
        @Data
        @ConfigurationProperties("jwt.config")
        public class JwtConfig {
            private String key;    //加盐Secret
            private long ttl;    //过期时间

            //生成JWT
            public String createJWT(String id, String subject, String roles) {
                long nowMillis = System.currentTimeMillis();
                JwtBuilder builder = Jwts.builder()
                        .setId(id)
                        .setSubject(subject)
                        .setIssuedAt(new Date(nowMillis))
                        .signWith(SignatureAlgorithm.HS256, key)
                        .claim("roles", roles);
                if (ttl > 0) {
                    builder.setExpiration(new Date(nowMillis + ttl));
                }
                return builder.compact();
            }

            //解析JWT
            public Claims parseJWT(String jwtStr) {
                return Jwts.parser()
                        .setSigningKey(key)
                        .parseClaimsJws(jwtStr)
                        .getBody();
            }
        }

    2.注入bean
        @Bean
        public JwtConfig jwtConfig(){
            return new JwtConfig();
        }
        
    3.登陆生成
        @PostMapping("/login")
        public Result login(@RequestBody Map<String, String> loginMap) { //restful接口参数封装map
            User user = helloService.login(loginMap.get("loginName"), loginMap.get("loginPwd"));
            if (null != user) {
                String userName = user.getName();
                String userRole = user.getRole();
                String token = jwtUtil.jwtConfig(user.getId() + "", userName, userRole);
                HashMap<String, String> map = new HashMap<>();
                map.put("token", token);
                map.put("name", userName);
                map.put("role", userRole); //返回前台,不用解析即可使用
                return new Result(true, StatusCode.OK, map);
            } else {
                return new Result(false, StatusCode.LOGIN_ERROR, "用户名或密码错误");
            }
        }

    4.删除验证
        //删除用户   - 必须拥有管理员权限,否则不能删除
        //前后端约定 - 前端请求微服务时需要添加头信息 Authorization : Bearer+空格+token
        @DeleteMapping("/user/{id}")
        public Result delete(@RequestHeader String authorization, @PathVariable String id) {
            
            //eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMTQ1OTY2ODcyODk5NjA4NTc2Iiwic3ViIjoiYWFhIiwiaWF0IjoxNTYyMDU0NzY4LCJyb2xlcyI6ImFkbWluIiwiZXhwIjoxNTYyMDU1MDY4fQ.qBax-Ut4jpd7jjCPu2XN6goa2R1aDyQUD3iS8SN1jDo
            log.info("{} - {}", authorization, id);

            if (null == authorization) {
                return new Result(false, StatusCode.ACCESS_ERROR, "权限不足");
            }

            String mark = "Bearer ";
            if (!authorization.startsWith(mark)) {
                return new Result(false, StatusCode.ACCESS_ERROR, "权限不足");
            }

            String token = authorization.substring(mark.length());
            Claims claims = jwtConfig.parseJWT(token);
            if (null == claims) {
                return new Result(false, StatusCode.ACCESS_ERROR, "权限不足");
            }

            String role = claims.get("role").toString();
            if (!"admin".equalsIgnoreCase(role)) {
                return new Result(false, StatusCode.ACCESS_ERROR, "权限不足");
            }

            helloService.delete(id);
            return new Result(true, StatusCode.OK, "删除成功");
        }

#使用拦截器方式实现token鉴权
    //以上代码冗余,并且每次关乎权限的方法都要判断一遍...拦截器
    
    1.定义拦截器
    
    
    2.注册拦截器
        
//}



//{--------<<<mini-tool>>>---------------------------X
#JSON-Handle //Chrome插件
    浏览器输入: chrome://extensions/
    将下载后的文件拖入浏览器即可

#cmder    //cmd升级版
    下载: http://cmder.net/    (mini与full版: 差别在于有没有内建msysgit工具)
    
    右键菜单: '配置系统环境变量,然后使用系统cmd执行命令: Cmder.exe /register ALL'
    中文乱码: 'settings -> Environment -> 添加: set LANG=zh_CN.UTF-8'
    
//}

//{--------<<<SystemUtil>>>>-------------------------X
#常用工具
    public class SystemUtil {
        //获取项目class路径
        public static String getClassPath() throws FileNotFoundException {
            // ClassUtils.getDefaultClassLoader().getResource("").getPath();//等同
            return ResourceUtils.getURL("classpath:").getPath();
        }

        //获取项目根路径
        public static String getFilePath() throws FileNotFoundException {
            return ResourceUtils.getURL("").getPath();
        }
        
        //系统换行
        public static String newline() {
            return System.getProperty("line.separator");
        }
    }
    
#常用解析
    0.org.apache.commons.lang3.StringUtils
        isEmpty(); //为空判断标准是: str==null 或 str.length()==0
        isBlank(); //在isEmpty()的基础上增加: 制表符,换行符,换页符,回车符...等

    1.保留两位有效小数
        double num = 12.1250/12.1251;
        String num0 = String.format("%.2f", num);// 12.13/12.13
        String num1 = new DecimalFormat("#0.00").format(num);// 12.12/12.13
        
        //DecimalFormat特殊字符说明
        //    "0"指定位置不存在数字则显示为0: 123.123 ->0000.0000 ->0123.1230
        //    "#"指定位置不存在数字则不显示: 123.123 ->####.####  ->123.123
        //    "."小数点
        //    "%"会将结果数字乘以100 后面再加上% 123.123 ->#.00%  ->12312.30%

    2.使用占位符拼接字符串
        MessageFormat.format("域名{0}被访问了{1}次", "\"www.qq.com\"", 123.456); //域名"www.qq.com"被访问了123.456次
        
        //String.format() ==> 创建格式化的字符串; 及连接多个字符串对象.
        String.format("域名%s被访问了%3.2f次", "\"www.qq.com\"", 123.456); //域名"www.qq.com"被访问了123.46次
        
        //先转化十六进制,再高位补0
        String.format("%04d",Integer.parseInt(String.format("%x", 16))); //0010
        
        /String.format()具体详见: https://www.cnblogs.com/Dhouse/p/7776780.html
        
#通过类目获取类的对象
    @Component
    public class MyApplicationContextAware implements ApplicationContextAware {// 获取bean的工具类

        private static ApplicationContext context;

        // 实现ApplicationContextAware接口的回调方法,设置上下文环境
        @Override
        public void setApplicationContext(ApplicationContext context) throws BeansException {
            MyApplicationContextAware.context = context;
        }

        // 获取applicationContext
        public static ApplicationContext getApplicationContext() {
            return context;
        }

        // 通过name获取Bean.
        public static Object getBean(String name) {
            return context.getBean(name);

        }

        // 通过clazz获取Bean.
        public static <T> T getBean(Class<T> clazz) {
            return context.getBean(clazz);
        }

        // 通过name及clazz返回指定的Bean
        public static <T> T getBean(String name, Class<T> clazz) {
            return context.getBean(name, clazz);
        }
    }
    
//}

//{--------<<<<Maven>>>------------------------------X
    '约定 > 配置 > 编码' -> 能用配置解决的问题就不编码,能基于约定的就不进行配置
    
#基本概念
    0.什么是构建???
        构建并不是创建,创建一个工程并不等于构建一个项目.
        构建是以 Java源码; 框架配置文件; JSP页面; html; 图片...等静态资源作为'原材料',
        去'生产'出一个可以运行的项目的过程.
        
    1.pom(Project Object Model,项目对象模型)
        将Java工程'project'的相关信息封装为对象'object',作为便于操作和管理的模型'model'. Maven工程的核心配置.

    2.mvn命令
        ///与项目构建相关的命令,必须切换到'pom.xml'同级目录
        mvn clean            //删除以前的编译结果,为重新编译做准备
        mvn compile            //编译主程序
        mvn test-compile    //编译测试程序
        mvn test            //执行测试
        mvn package            //打包
        mvn install            //将打包的结果(jar/war)安装到本地仓库中
        mvn site            //生成站点
        
    3.依赖的范围
        compile //主程序(√); 测试程序(√); 打包(√); 部署(√); 如: spring-core
        provied //主程序(√); 测试程序(√); 打包(x); 部署(x); 如: servlet-api(tomcat提供)
        test    //主程序(x); 测试程序(√); 打包(x); 部署(X); 如: junit
        
        runtime //主程序(x); 测试程序(√); 打包(√); 部署(√); 如: mysql-connector-java

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
        <server> //(0).配置mvn
          <id>thirdparty</id>
          <username>admin</username>
          <password>admin123</password>
        </server>
        
        //(1).使用cmd命令将第三方包加入私服
        mvn deploy:deploy-file -DgroupId=com.bluecard -DartifactId=wxpay-sdk-0.0.3 -Dversion=0.0.3 -Dpackaging=jar -Dfile=G:\wxpay-sdk-0.0.3.jar -Durl=http://192.168.102.20:8081/nexus/content/repositories/thirdparty/ -DrepositoryId=thirdparty
        //其中, -DgroupId="随意指定"; -DartifactId="第三方jar包名"; -Dversion="版本号"; -Dfile="jar所在本地路径"; -Durl="私服路径"

        //(2).项目pom.xml添加引用
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
            <scope>provided</scope>//依赖的范围(详见1.3)
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
    
//}
    
//{--------<<<fastjson>>>----------------------------X
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.47</version>
</dependency>

// QuoteFieldNames --> 输出key时是否使用双引号,默认为true
// WriteMapNullValue --> 是否输出值为null的字段,默认为false
// WriteNullListAsEmpty --> List字段如果为null,输出为[],而非null
// WriteNullNumberAsZero --> 数值字段如果为null,输出为0,而非null
// WriteNullStringAsEmpty --> 字符类型字段如果为null,输出为"",而非null (√)
// WriteNullBooleanAsFalse --> Boolean字段如果为null,输出为false,而非null
    
    //list -> JSONString
    JSON.toJSONString(list, SerializerFeature.WriteNullStringAsEmpty); 
    
    //Demo
    JSONObject json = new JSONObject();
    System.out.println(json.getInteger("a")); //null
    System.out.println(json.getIntValue("a")); //0
    
    //X->String
String json = JSON.toJSONString(list / map / bean);
String json = JSON.toJSONString(list, true);//args1: json是否格式化(有空格和换行).
    
    //X->JSON (必须有get/set)
JSONObject obj = JSON.parseObject(JSON.toJSONString(person));//javabean
JSONObject obj = JSON.parseObject(JSON.toJSONString(map));//map
JSONArray array = JSON.parseArray(JSON.toJSONString(list));//list

    //JSON->X (必须有空构造方法)
Person person = JSON.parseObject(json, Person.class);
Map map = JSON.parseObject(json, Map.class);
List<Person> list = JSON.parseArray(json, Person.class);
    
#boot整合
    //boot2.x默认使用 jacksonJson 解析,现转换为 fastjson
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    Logger logger = LoggerFactory.getLogger(getClass());

    //利用fastjson替换掉jackson,且解决中文乱码问题
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //1.构建了一个消息转换器 converter
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();

        //2.添加fastjson配置,如: 是否格式化返回的json数据;设置编码方式
        FastJsonConfig config = new FastJsonConfig();

        config.setSerializerFeatures(SerializerFeature.PrettyFormat);//格式化

        List<MediaType> list = new ArrayList<>();//中文乱码
        list.add(MediaType.APPLICATION_JSON_UTF8);
        converter.setSupportedMediaTypes(list);

        //3.在消息转换器中添加fastjson配置
        converter.setFastJsonConfig(config);
        converters.add(converter);
    }
}
        
    2.指定日期格式
        class Demo {
            @JSONField(format = "yyyy-MM-dd HH:mm:ss") //fastjson格式化
            //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") //jackson格式化
            private Date date;
        }

//}

//{--------<<<HttpClient>>>--------------------------X
#pom.xml
        //<!-- HttpClient -->
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpclient</artifactId>
        </dependency>

#GET/POST区别
    超链接<a/>    ---> ///只能用 GET 提交HTTP请求
    表单</form>    ---> ///可以用 GET,POST .......

    GET            ---> ///参数只能在请求行(request-line)
    POST        ---> ///参数可在请求行,亦可在请求体(request-body)

#GET -> request-line
    1.两种方式获取HttpGet
        #(1).直接将参数拼接在 URI 之后
        String uri = "http://127.0.0.1:8090/demo/http/get?name=中国&age=70";
        HttpGet httpGet = new HttpGet(uri);
        
        #(2).通过 URIUtils 工具类生成带参数的 URI
        String param = "name=中国&age=70";
        // String param = "name=" + URLEncoder.encode("中国", "UTF-8") + "&age=70"; //中文参数,encode
        URI uri = URIUtils.createURI("http", "127.0.0.1", 8090, "/demo/http/get", param, null);
        HttpGet httpGet = new HttpGet(uri);
        
    2.执行请求获取结果
        @Test
        public void doGet() {
            String uri = "http://127.0.0.1:8090/demo/http/get?name=中国&age=70";
            HttpGet httpGet = new HttpGet(uri); //组装请求-GET
            // HttpPost httpPost = new HttpPost(uri); //组装请求-POST

            try (CloseableHttpResponse httpResponse = HttpClients.createDefault().execute(httpGet)) { //发送请求
                if (null != httpResponse && HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()) {
                    String res = EntityUtils.toString(httpResponse.getEntity(), "UTF-8"); //获取结果
                    System.out.println(res);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
#POST -> request-line
    1.两种方式获取httpPost //(同上)
        #(1).拼接字符串
        String uri = "http://127.0.0.1:8090/demo/http/post?name=中国&age=70";
        HttpPost httpPost = new HttpPost(uri);
        
        #(2).工具类 URIUtils
        String param = "name=中国&age=70";
        // String param = "name=" + URLEncoder.encode("中国", "UTF-8") + "&age=70"; //中文参数,encode
        URI uri = URIUtils.createURI("http", "127.0.0.1", 8090, "/demo/http/post", param, null);
        HttpPost httpPost = new HttpPost(uri);

#POST -> request-body -> keyValue
    1.POST表单
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("name", "中国"));
        nvps.add(new BasicNameValuePair("age", "70"));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps, "UTF-8"); //中文乱码

        HttpPost httpPost = new HttpPost("http://127.0.0.1:8090/demo/http/post");
        httpPost.setEntity(entity);
        
    2.查看HTTP数据
        System.out.println(entity.getContentType()); //Content-Type: application/x-www-form-urlencoded; charset=UTF-8
        System.out.println(entity.getContentLength()); //30
        System.out.println(EntityUtils.toString(entity)); //name=%E4%B8%AD%E5%9B%BD&age=70

#POST -> request-body -> json
    1.中文乱码
        String json = "{\"name\":\"中国\",\"age\":\"70\"}";
        StringEntity entity = new StringEntity(json, "UTF-8"); //中文乱码,默认"ISO-8859-1"
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");//设置contentType --> json

        HttpPost httpPost = new HttpPost("http://127.0.0.1:8090/demo/http/postBody");
        httpPost.setEntity(entity);

#POST -> request-body -> file
    1.pom.xml
        //<!-- HttpClient-File -->
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpmime</artifactId>
        </dependency>
        
    2.中文乱码
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        ContentType contentType = ContentType.create("text/plain", "UTF-8"); //中文乱码,默认"ISO-8859-1"
        builder.addTextBody("fileName", "中国", contentType);
        builder.addBinaryBody("file", new File("C:\\Users\\BlueCard\\Desktop\\StatusCode.png"));
        HttpEntity entity = builder.build();

        HttpPost httpPost = new HttpPost("http://127.0.0.1:8090/demo/http/postFile");
        httpPost.setEntity(entity);
    
    3.前台页面    
        <form action="http://127.0.0.1:8090/demo/http/postFile" method="POST" enctype="multipart/form-data">  
            <input type="text" name="fileName" value="中国"/>  
            <input type="file" name="file"/>  
            <inupt type="submit" value="提交"/>  
        </form>  
    
    

//}








//{--------<<<常用包>>>------------------------------X
#org.apache.commons
        // <!-- 该版本完全支持 Java5 的特性,如泛型和可变参数. 该版本无法兼容以前的版本,简化很多平时经常要用到的写法,如判断字符串是否为空等等 -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-lang3</artifactId>
            <version>3.8.1</version>
        </dependency>

        // <!-- 对象池的实现,显著的提升了性能和可伸缩性,特别是在高并发加载的情况下 -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-pool2</artifactId>
            <version>2.4.2</version>
        </dependency>

        // <!-- email -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-email</artifactId>
            <version>1.4</version>
        </dependency>
        // <!-- spring-boot email -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-mail</artifactId>
        </dependency>

        // <!-- IO工具类,文件操作及字符串比较功能 -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-io</artifactId>
            <version>1.3.2</version>
        </dependency>

#spring
        // <!-- 为Spring核心提供了大量扩展.可以找到使用 Spring ApplicationContext 特性时所需的全部类,JDNI所需的全部类,
        //        UI模板引擎(Templating),如 Velocity、FreeMarker、JasperReports, 以及校验 Validation 方面的相关类 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context</artifactId>
            <version>4.3.3.RELEASE</version>
        </dependency>

        // <!-- 含支持UI模版(Velocity,FreeMarker,JasperReports),邮件服务,脚本服务(JRuby),缓存Cache(EHCache),
        //         任务计划Scheduling(uartz)方面的类. 外部依赖spring-context, (spring-jdbc, Velocity, FreeMarker,
        //         JasperReports, BSH, Groovy, JRuby, Quartz, EHCache) -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-context-support</artifactId>
            <version>4.3.3.RELEASE</version>
        </dependency>

        // <!-- spring测试框架,需要配合 junit 进行使用创建单元测试. spring测试所需包: sring的相关组件,spring-test,junit -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-test</artifactId>
            <version>4.3.3.RELEASE</version>
        </dependency>

        // <!-- 包含Spring 框架基本的核心工具类. Spring 其它组件要都要使用到这个包里的类，是其它组件的基本核心.
        //        也可以在自己的应用系统中使用这些工具类.外部依赖Commons-logging,Log4J -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-core</artifactId>
            <version>4.3.3.RELEASE</version>
        </dependency>

        // <!-- 所有应用都要用到的,它包含访问配置文件,创建和管理bean 以及 进行(IoC/DI)操作相关的所有类.
        //        如果应用只需基本的IoC/DI 支持，引入spring-core.jar 及spring-beans.jar 文件就可以了. 外部依赖spring-core，(CGLIB)。 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-beans</artifactId>
            <version>4.3.3.RELEASE</version>
        </dependency>

        // <!-- https://mvnrepository.com/artifact/org.springframework/spring-web
            // 包含Web 应用开发时，用到Spring 框架时所需的核心类，包括自动载入Web Application Context
            // 特性的类、Struts  与JSF 集成类、文件上传的支持类、Filter 类和大量工具辅助类。
            // 外部依赖spring-context, Servlet API, (JSP API, JSTL, Commons FileUpload, COS)。 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-web</artifactId>
            <version>4.3.3.RELEASE</version>
        </dependency>

        // <!-- https://mvnrepository.com/artifact/org.springframework/spring-webmvc
            // 包含Spring MVC 框架相关的所有类。包括框架的Servlets，Web MVC框架，控制器和视图支持。
            // 当然，如果你的应用使用了独立的MVC
            // 框架，则无需这个JAR 文件里的任何类。
            // 外部依赖spring-web, (spring-support，Tiles，iText，POI)。 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>4.3.3.RELEASE</version>
        </dependency>

        // <!-- https://mvnrepository.com/artifact/org.springframework/spring-jdbc
            // 包含对Spring 对JDBC 数据访问进行封装的所有类。 外部依赖spring-beans，spring-dao。 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>4.3.3.RELEASE</version>
        </dependency>

        // <!-- https://mvnrepository.com/artifact/org.springframework/spring-aop
            // AOP（Aspect Oriented Programming），即面向切面编程（也叫面向方面编程，面向方法编程）。
            // 其主要作用是，在不修改源代码的情况下给某个或者一组操作添加额外的功能。像日志记录，事务处理，
            // 权限控制等功能，都可以用AOP来“优雅”地实现，使这些额外功能和真正的业务逻辑分离开来，
            // 软件的结构将更加清晰。AOP是OOP的一个强有力的补充。 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aop</artifactId>
            <version>4.3.3.RELEASE</version>
        </dependency>

        // <!-- https://mvnrepository.com/artifact/org.springframework/spring-tx 事物控制 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-tx</artifactId>
            <version>4.3.3.RELEASE</version>
        </dependency>

        // <!-- https://mvnrepository.com/artifact/org.springframework/spring-orm
            // 包含Spring对DAO特性集进行了扩展，使其支持 iBATIS、JDO、OJB、TopLink， 因为Hibernate已经独立成包了，现在不包含在这个包里了。这个jar文件里大部分的类都要
            // 依赖spring-dao.jar里的类，用这个包时你需要同时包含spring-dao.jar包。 -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-orm</artifactId>
            <version>4.3.3.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aspects</artifactId>
            <version>4.3.3.RELEASE</version>
        </dependency>

        //<!-- https://mvnrepository.com/artifact/org.springframework/spring-expression
        //    SPEL表达式支持:
        //    一、基本表达式：字面量表达式、关系，逻辑与算数运算表达式、字符串连接及截取表达式、
        //        三目运算及Elivis表达式、正则表达式、括号优先级表达式；
        //    二、类相关表达式：类类型表达式、类实例化、instanceof表达式、变量定义及引用、赋值表达式、
        //        自定义函数、对象属性存取及安全导航表达式、对象方法调用、Bean引用；
        //    三、集合相关表达式：内联List、内联数组、集合，字典访问、列表，字典，数组修改、集合投影、
        //        集合选择；不支持多维内联数组初始化；不支持内联字典定义；
        //    四、其他表达式：模板表达式。
        //    注：SpEL表达式中的关键字是不区分大小写的。-->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-expression</artifactId>
            <version>4.3.3.RELEASE</version>
        </dependency>
    

//}





