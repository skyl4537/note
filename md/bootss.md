[TOC]

# 基础功能

> 基础概念

```shell
简化依赖管理
    #将各种功能模块进行划分，封装成一个个启动器(Starter)，更容易的引入和使用
    #提供一系列的Starter，将各种功能性模块进行了划分与封装
    #更容易的引入和使用，有效避免了用户在构建传统Spring应用时维护大量依赖关系，而引发的jar冲突等问题

自动化配置 #为每一个Starter都提供了自动化的java配置类
嵌入式容器 #嵌入式tomcat，无需部署war文件
监控の端点 #通过 Actuator 模块暴露的http接口，可以轻松的了解和控制应用的运行情况
```

##pom

>SpringBoot 并不是对 Spring 功能上的增强，而是提供了一种快速使用 Spring 的方式

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId> <!-- 热部署 -->
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope> <!-- 只在运行时使用 -->
        <optional>true</optional> <!--只在当前项目生效，不会传递-->
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope> <!--只在测试时使用-->
    </dependency>
</dependencies>

<build>
    <finalName>demo-user</finalName> <!--配置项目打包名-->
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
    <resources> <!--资源拷贝插件-->
        <resource>
            <directory>src/main/java</directory>
            <includes>
                <include>**/*.xml</include>
            </includes>
        </resource>
        <resource>
            <directory>src/main/resources</directory>
        </resource>
    </resources>
</build>
```





#高级功能

## 启动脚本

> 启动脚本

```shell
#!/bin/bash
DEMO_DIR="/var/tmp/demo"
DEMO_JAR="demo-eureka.jar"
DEMO_PID_FILE="demo.pid"

cd $DEMO_DIR
chmod 777 $DEMO_JAR

nohup java -jar $DEMO_JAR >/dev/null 2>&1 &
echo $! > $DEMO_PID_FILE #记录进程号，方便后续使用

PID=`cat "$DEMO_PID_FILE"`
echo "START AT PID: "$PID
```

> 停止脚本

```shell
#!/bin/bash
DEMO_JAR="demo-eureka.jar"
DEMO_PID_FILE="demo.pid"

if [ -f "$DEMO_PID_FILE" ]; then #-f: 是否为普通文件(既不是目录，也不是设备文件)
  PID=`cat "$DEMO_PID_FILE"`
  echo "STOP PID: "$PID
  kill -9 $PID
  true>$DEMO_PID_FILE #清空文件
fi
```

## login

>视图映射xml

```xml
<!--(1).使用此标签后必须配置 <mvc:annotation-driven />，否则会造成所有的 @Controller 注解无法解析，导致404错误-->
<!--(2).如果请求存在处理器，则这个标签对应的请求处理将不起作用。因为请求是先去找处理器处理，如果找不到才会去找这个标签配置-->
<mvc:view-controller path="/toView" view-name="view"/>
<mvc:annotation-driven />
```



# CRUD

##restful

>restful是对于同一个服务器资源的一组不同的操作，包括：GET，POST，PUT，DELETE，PATCH，HEAD，OPTIONS

```shell
http请求的安全和幂等，是指多次调用同一个请求对资源状态的影响。
'安全' -> 请求不会影响资源的状态。只读的请求：GET，HEAD，OPTIONS
'幂等' -> 多次相同的请求，目的一致。
```
```shell 
GET    /emps      查询员工列表  -> 只是请求，不改变资源状态                    #安全，幂等
POST   /emps/emp  新增一个员工  -> 多次请求会新增多条相同的数据                #不安全，不幂等
PUT    /emps/emp  更新员工信息  -> 多次请求都是将id为 5 的员工姓名修改成'wang'  #不安全，幂等
DELETE /emps/{id} 删除员工信息  -> 多次请求目的都是删除id为 5 的员工           #不安全，幂等
                                 #第一次成功删除，第二次及以后虽资源已不存在，但也得返回 200 OK，不能返回 404

GET    /emps/emp  跳转新增页面
GET    /emps/{id} 跳转更新页面
```

> `请求转化`：将 POST 转化为 PUT，DELETE

```xml
<!--（1）配置 HiddenHttpMethodFilter，SpringBoot 默认已配置-->
<filter>
    <filter-name>HiddenHttpMethodFilter</filter-name>  
    <filter-class>org.springframework.web.filter.HiddenHttpMethodFilter</filter-class>  
</filter>
```

```html
<!--（2）页面创建（POST表单 + 隐藏标签）-->
<form method="post" th:action="@{/emp/}+${emp.id}">
    <input type="hidden" name="_method" value="delete"> <!--隐藏标签 name + value-->

    <a href="#" onclick="delEmp(this)" th:attr="url=@{/emp/}+${emp.id}">删除</a>
</form>
```
##列表：get

> 跳转到列表页面 `a标签对应的是 GET 请求`

```html
<a th:href="@{/emps}">员工列表</a>
```

>跳转逻辑

```java
@Slf4j
@Controller
@RequestMapping("/emps")
public class EmployeeController {

    @GetMapping
    public String list(Model model) {
        model.addAttribute("emps", EmpUtils.listAll());
        return "/emps/list"; //默认转发，forward
    }
}
```

> 列表页面

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
    <head>
        <meta charset="UTF-8">
        <title>列表页面</title>

        <script th:src="@{/webjars/jquery/jquery.min.js}"></script>
        <script th:src="@{/webjars/bootstrap/js/bootstrap.min.js}"></script>
        <link rel="stylesheet" th:href="@{/webjars/bootstrap/css/bootstrap.min.css}"/>

        <script>/*删除记录的js...*/</script>
    </head>
    <body>
        <table>
            <tr>
                <th>姓名</th>
                <th>年龄</th>
                <th>城市</th>
                <th>操作</th>
            </tr>
            <tr th:if="${null==emps || 0==emps.size()}">
                <td colspan="4" th:text="员工列表为空"></td>
            </tr>
            <tr th:each="emp:${emps}" th:object="${emp}"> <!--th:object 和 *{...} 配合使用-->
                <td th:text="${emp.name}"></td>
                <td th:text="*{gender}?'男':'女'"></td>
                <td th:text="*{city.name}"></td>
                <td>
                    <a th:href="@{/emps/}+*{id}">修改</a> <!--路径拼接-->
                    <a href="#" onclick="deleteEmp(this)" th:attr="url=@{/emps/}+${emp.id}">删除</a>
                </td>
            </tr>
        </table>
        <a th:href="@{/emps/emp}">新增员工</a>
    </body>
</html>
```

##新增：post

>跳转新增页面

```html
<a th:href="@{/emps/emp}">新增员工</a>
```

>跳转逻辑

```java
@GetMapping("/emp")
public String toAdd(Model model) {
    model.addAttribute("citys", EmpUtils.listCity()); //新增页面要显示的城市列表信息
    return "/emps/emp"; //转发-页面
}
```

> 新增页面（同修改页面，略）

> 新增接口

```java
@PostMapping("/emp")
public String add(Emp emp) {
    empList.add(emp);
    return "redirect:/emps"; //重定向 -> 接口
}
```

##修改：put

>跳转修改页面

```html
<a th:href="@{/emps/}+*{id}">修改</a> <!--路径拼接-->
```

>跳转逻辑

```java
@GetMapping("/{id}")
public String toUpdate(@PathVariable Integer id, Model model) {
    Emp emp = EmpUtils.empList.get(id); //根据ID查找
    
    model.addAttribute("emp", emp);
    model.addAttribute("citys", EmpUtils.cityList); //用于页面回显
    return "/emps/emp";
}
```

>回显数据到修改页面

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
    <head>
        <meta charset="UTF-8">
        <title>员工信息页</title>
    </head>
    <body>
        <form method="post" th:action="@{/emps/emp}">
            <!--新增和修改使用同一页面，区分方式：回显 emp 是否为空 ${null!=person}-->
            <input type="hidden" name="_method" value="put" th:if="${null!=emp}">
            <!--修改：PUT请求 + emp.id-->
            <input type="hidden" name="id" th:value="${emp.id}" th:if="${null!=emp}">

            <table>
                <tr>
                    <td>姓名：</td>
                    <td><input type="text" name="name" th:value="${null!=emp}?${emp.name}"></td>
                </tr>
                <tr>
                    <td>性别：</td>
                    <td>
                        <!--th:checked radio标签是否选中-->
                        <input type="radio" name="gender" value="1" th:checked="${null!=emp}?${emp.gender}">男
                        <input type="radio" name="gender" value="0" th:checked="${null!=emp}?${!emp.gender}">女
                    </td>
                </tr>
                <tr>
                    <td>住址：</td>
                    <td>
                        <select name="city.id">
                            <!--th:selected 回显emp.city.id == 遍历city.id，则选中-->
                            <option th:each="city:${citys}" th:object="${city}" th:value="*{id}" th:text="*{name}"
                                    th:selected="${null!=emp}?${emp.city.id}==*{id}"></option>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td colspan="2">
                        <!--回显 emp 为空，则显示'新增'；否则显示'修改'-->
                        <input type="submit" th:value="${null==emp}?'新增':'修改'">
                    </td>
                </tr>
            </table>
        </form>
    </body>
</html>
```

>修改接口

```java
@PutMapping("/emp")
public String update(Emp emp) {
    EmpUtils.empList.update(emp);
    return "redirect:/emps"; //重定向 -> 接口
}
```

##删除：delete

>删除方式（1）form表单

```html
<a href="#" onclick="deleteEmp(this)" th:attr="url=@{/emp/}+${emp.id}">删除</a>
```

```html
<form id="deleteForm" method="post" action="#"> <!--独立于列表Table的<form/>表单-->
    <input type="hidden" name="_method" value="DELETE">
</form>
```

```html
<script>
    function deleteEmp(e) {
        alert($(e).attr('url')); //按钮的url属性

        //动态设置<form>的action属性，并提交
        $('#deleteForm').attr('action', $(e).attr('url')).submit();
        return false; //取消按钮的默认行为
    }
</script>
```

>删除方式（1）后台逻辑

```java
@DeleteMapping("/{id}")
public String delete(@PathVariable Integer id) {
    EmpUtils.empList.remove(id.intValue());
    return "redirect:/emps";
}
```

> 删除方式（2）不使用form表单，而使用ajax异步请求

```html
<a href="#" onclick="deleteEmp(this)" th:attr="url=@{/emps/}+${emp.id}">删除</a>
```

```html
<script>
    function deleteEmp(e) {
        $.ajax({
            type: 'delete',
            url: $(e).attr('url'),
            dataType: 'text',
            success: function (data) {
                //e 表示当前emp所在行的标签<a/>
                //$(e).parent().parent() 表示<a/> -> td -> tr
                $(e).parent().parent().remove();
                alert(data);
            },
            error: function (data) {
                var res = JSON.parse(data.responseText); //转化json
                alert(res.status + " - " + res.error + " - " + res.message);
            }
        });
        return false;
    }
</script>
```

> 删除方式（2）后台逻辑

```java
@DeleteMapping("/{id}")
@ResponseBody
public String delete(@PathVariable Integer id) {
    EmpUtils.empList.deleteById(id);
    return "success";
}
```



# 其他相关

<https://www.cnblogs.com/moonlightL/p/7891806.html>









