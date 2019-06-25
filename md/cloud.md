[TOC]





# 基础概念

## 概念相关

> 镜像，容器，微服务

```
子项目做成镜像，镜像做成容器。

微服务就是同类容器的集合，一个微服务包括一组容器。如，已有的 mysql 容器，或者自定义的 交友 容器等。
```

> 微服务的子项目中是否需要接口？ `不需要`

```
普通项目分为MVC三层，不同层由不同人员维护，所以在不同层进行相互调用时，就需要一套规范，即不同层的接口。

但是对于微服务项目，每一个子项目都是单独的一个服务，由单独的人员进行维护，所以也就不需要定义接口。
```

> 概念解析

```java
'SpringBoot'：Spring的纯注解版，零配置文件，以最少的配置来开发项目。是一种开发风格。

'SpringCloud'：Spring整合第三方技术的架构，将已有的第三方技术以'SpringBoot'的形式（即零配置的形式）封装到Spring框架中。
               所以，SpringCloud 依赖于 SpringBoot，但 SpringBoot 不依赖 SpringCloud。

'SpringMVC'：一种web层MVC框架，用于替代servlet（处理-响应请求，获取表单参数，表单校验等）。

'SpringData'：持久层框架。不仅能够适用于关系型数据库，还能够适用于非~。如 MongoDB，Redis，Hadoop
```


#父项目

> 区别 IDEA 中的 project 和 module

```

```

> 父项目创建：<https://start.spring.io/>

```
项目名：demo_parent，最好使用下划线进行分割。由于父项目不写代码逻辑，所以可将 src 目录删除
```

>对于微服务的父项目而言，pom.xml中的打包类型选择 pom 类型

```xml
<groupId>com.example</groupId>
<artifactId>demo_parent</artifactId>
<version>0.0.1-SNAPSHOT</version>
<packaging>pom</packaging> <!--父项目必须设置pom-->
```

> 父项目的 pom.xml 只写通用的jar包。如 mysql 驱动包只在部分子模块使用，就不要写在父项目中

```xml
<dependencies>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <scope>provided</scope> <!--参与编译，测试，运行，但不会打包-->
        <optional>true</optional> <!--true: 依赖不会传递，但是该依赖卸载父项目则所有子类都可用。false: 会传递-->
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

> 配置 Spring 的仓库地址，与 build 标签同级

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>

<repositories> <!--jar包仓库-->
    <repository>
        <id>spring-snapshots</id>
        <name>Spring Snapshots</name>
        <url>https://repo.spring.io/snapshot</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
    <repository>
        <id>spring-milestones</id>
        <name>Spring Milestones</name>
        <url>https://repo.spring.io/milestone</url>
    </repository>
</repositories>
<pluginRepositories> <!--插件仓库-->
    <pluginRepository>
        <id>spring-snapshots</id>
        <name>Spring Snapshots</name>
        <url>https://repo.spring.io/snapshot</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </pluginRepository>
    <pluginRepository>
        <id>spring-milestones</id>
        <name>Spring Milestones</name>
        <url>https://repo.spring.io/milestone</url>
    </pluginRepository>
</pluginRepositories>
```



# 公共模块

> 项目创建：demo_common

```
选中父项目，然后右键选择 new -> module，项目名称：demo_common

对于公共模块只写公共方法，不写业务逻辑，所以 pom.xml 不用引用其他jar包
```

> pom.xml

```xml
<parent>
    <artifactId>demo_parent</artifactId> <!--指定父项目-->
    <groupId>com.example</groupId>
    <version>0.0.1-SNAPSHOT</version>
</parent>
<modelVersion>4.0.0</modelVersion>

<artifactId>demo_common</artifactId> <!--当前项目名。GV同父项目，所以省略-->

<dependencies>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
        <version>3.8.1</version>
    </dependency>
    <dependency>
        <groupId>commons-collections</groupId>
        <artifactId>commons-collections</artifactId>
        <version>3.2.2</version>
    </dependency>
</dependencies>
```

>创建返回结果实体类：用于控制器类返回结果

```java
package entity; //新建 entity 包，包下创建类 Result

@Data
public class Result {
    private boolean flag; //是否成功 （规范2）
    private Integer code; //返回码 （规范1）
    private String message; //返回信息
    private Object data; //返回数据

    public Result() {
    }

    public Result(boolean flag, Integer code, String message) {
        this.flag = flag;
        this.code = code;
        this.message = message;
    }

    public Result(boolean flag, Integer code, String message, Object data) {
        this.flag = flag;
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
```

```java
//【强制】所有的 POJO 类属性必须使用包装数据类型。
//【强制】RPC 方法的返回值和参数必须使用包装数据类型。

//【推荐】所有的局部变量使用基本数据类型。

说明：POJO 类属性没有初值是提醒使用者在需要使用时，必须自己显式地进行赋值，任何 NPE 问题，或者入库检查，都由使用者来保证。

正例：数据库的查询结果可能是 null，因为自动拆箱，用基本数据类型接收有 NPE 风险。

反例：比如显示成交总额涨跌情况，即正负 x%，x 为基本数据类型，调用的 RPC 服务，调用不成功时，返回的是默认值，页面显示为 0%，这是不合理的，应该显示成中划线。所以包装数据类型的 null 值，能够表示额外的信息，如：远程调用失败，异常退出。
```

```java
//【强制】POJO 类中布尔类型变量都不要加 is 前缀，否则部分框架解析会引起序列化错误。

说明：在本文 MySQL 规约中的建表约定第一条，表达是与否的值采用 is_xxx 的命名方式，所以，需要在<resultMap>设置从 is_xxx 到 xxx 的映射关系。

反例：定义为基本数据类型 Boolean isDeleted 的属性，它的方法也是 isDeleted()，RPC 框架在反向解析的时候，“误以为”对应的属性名称是 deleted，导致属性获取不到，进而抛出异常。
```

> 状态码实体类

```java
public class StatusCode {
    public static final int OK = 20000; //成功
    public static final int ERROR = 20001; //失败
    public static final int LOGIN_ERROR = 20002; //用户名或密码错误
    public static final int ACCESS_ERROR = 20003; //权限不足
    public static final int REMOTE_ERROR = 20004; //远程调用失败
    public static final int REP_ERROR = 20005; //重复操作
}
```

>创建类PageResult：用于返回分页结果

```java
package entity;

@Data
@NoArgsConstructor //无参构造
@AllArgsConstructor //有参构造
public class PageResult<T> {
    private Long total;
    private List<T> rows;
}
```

>分布式ID生成器

```java
由于数据库在生产环境中要分片部署（MyCat），所以不能使用数据库本身的自增功能来产生主键值，只能由程序来生成唯一的主键值。
采用开源的 twitter 的 snowflake（雪花）算法，总长度64bit。

'优点'：（1）整体上按照时间自增排序（2）整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分) （3）效率较高
经测试，SnowFlake每秒能够产生26万ID左右。

0        //最高位是符号位,始终为0,不可用.
1-41     //41 位的时间序列，精确到毫秒级，可使用到2082年。时间位另一作用是可以根据时间进行排序
42-51    //10 位的机器标识，10位的长度最多支持部署1024个节点（2^10）
52-63    //12 位的计数序列，是一系列的自增id，支持每个节点每毫秒产生4096个ID序号（2^12）
```

![](assets/cloud0.png)







#基础微服务

> 基础微服务：demo_base

```java
//与公共模块的区别：
公共模块只写一些公共类，不写逻辑代码，最终是以 jar 包形式在各个微服务中调用。
基础微服务是整个项目的一个子模块，也是微服务的一个。

但创建方式相同，都是右键选择 new -> module，项目名称：demo_base
```

> pom.xml

```xml
<parent>
    <artifactId>demo_parent</artifactId> <!--父项目-->
    <groupId>com.example</groupId>
    <version>0.0.1-SNAPSHOT</version>
</parent>
<modelVersion>4.0.0</modelVersion>

<artifactId>demo_base</artifactId>

<dependencies>
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>demo_common</artifactId> <!--公共模块 demo_common-->
        <version>0.0.1-SNAPSHOT</version>
    </dependency>

    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>druid-spring-boot-starter</artifactId>
        <version>1.1.10</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
    </dependency>
</dependencies>
```

> properties

```properties
server.port=9001

#微服务名称只能用-进行分割，不能用下划线
spring.application.name=demo-base

spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://192.168.8.7:33306/demo_base?useSSL=false&allowMultiQueries=true&serverTimezone=GMT%2B8
spring.datasource.username=bluecardsoft
spring.datasource.password=#$%_BC13439677375

spring.jpa.database=mysql
spring.jpa.show-sql=true
#是否自动生成ddl
spring.jpa.generate-ddl=true
spring.jpa.open-in-view=false
```

> 启动类

```java
@SpringBootApplication
public class BaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaseApplication.class, args);
    }

    //id生成器，并不是所有的微服务项目都使用，所以不要在 IdWork 类上加 @Component
    //而应该在使用 id生成器 的微服务中用 @Bean 标签注入
    @Bean
    public IdWorker idWorker() {
        return new IdWorker();
    }
}
```

> pojo

```java
@Data
@Entity
@Table(name = "tb_label") //同数据表名
public class Label {
    @Id
    private String id;
    private String labelname;//标签名称
    private String state;//状态
    private Long count;//使用数量
    private Long fans;//关注数
    private String recommend;//是否推荐
}
```

> Controller

```java
@Slf4j
@RequestMapping("/label")
@RestController
public class LabelController {

    @Autowired
    LabelService labelService;

    //获取多个对象的方法用 list 做前缀，复数形式结尾如：listObjects
    @GetMapping
    public Result listLabels() {
        List<Label> labels = labelService.listLabels();
        log.info("listLabels: {}", labels);
        return new Result(true, StatusCode.OK, "查询成功", labels);
    }

    //获取单个对象的方法用 get 做前缀
    @GetMapping("/{labelId}")
    public Result getById(@PathVariable String labelId) { //获取请求行参数

        Optional<Label> label = labelService.getById(labelId);
        return new Result(true, StatusCode.OK, "查询成功", label);
    }

    //插入的方法用 save/insert 做前缀
    @PostMapping
    public Result insertLabel(@RequestBody Label label) { //获取请求体参数
        log.info("insertLabel: {}", label);
        labelService.insertLabel(label);
        return new Result(true, StatusCode.OK, "插入成功");
    }

    //修改的方法用 update 做前缀
    @PutMapping("/{labelId}")
    public Result updateById(@PathVariable String labelId, @RequestBody Label label) {
        labelService.updateById(labelId, label);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    //删除的方法用 remove/delete 做前缀
    @DeleteMapping("/{labelId}")
    public Result deleteById(@PathVariable String labelId) {
        labelService.deleteById(labelId);
        return new Result(true, StatusCode.OK, "删除成功");
    }
}
```

> Service：`微服务中不需要使用接口，直接写实现类即可`

```java
@Slf4j
@Service
@Transactional
public class LabelService {

    @Autowired
    LabelDao labelDao;

    @Autowired
    IdWorker idWorker;

    //查询全部
    public List<Label> listLabels() {
        Iterable<Label> iterable = labelDao.findAll();
        List<Label> labels = new ArrayList<>();
        iterable.forEach(labels::add);
        return labels;
    }

    //根据Id查找
    public Optional<Label> getById(String id) {
        return labelDao.findById(id)/*.get()*/;
    }

    //新增一条
    public void insertLabel(Label label) {
        label.setId(idWorker.nextId() + "");
        labelDao.save(label); //先根据Id进行查询，有结果则更新，无结果则新增
    }

    //根据id更新
    public void updateById(String labelId, Label label) {
        label.setId(labelId + "");
        labelDao.save(label);
    }

    //根据id删除
    public void deleteById(String labelId) {
        labelDao.deleteById(labelId);
    }
}
```

> DAO

```java
public interface LabelDao extends CrudRepository<Label, String> { }
```



































































































































































































