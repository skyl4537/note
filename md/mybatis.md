[TOC]

# Ali规约

> 1，2，4，5，6，8，9，11

```sql
获取多个对象的方法用 'list' 做前缀，复数形式结尾如：listObjects
获取单个对象的方法用 'get' 做前缀
插入的方法用 save/'insert' 做前缀
修改的方法用 'update' 做前缀
删除的方法用 remove/'delete' 做前缀
获取统计值的方法用 'count' 做前缀
```

```sql
'1.强制' 在表查询中，一律不要使用 * 作为查询的字段列表，需要哪些字段必须明确写明。
-- 说明：(1).增加查询分析器解析成本。(2).增减字段容易与 resultMap 配置不一致。
```
```sql
'2.强制' POJO 类 boolean属性不能加 is，而数据库字段必须加 is_，要求在 resultMap 中进行字段与属性的映射。
-- 说明：参见定义 POJO 类以及数据库字段定义规定，在 sql.xml 增加映射，是必须的。
```
```sql
'3.强制' 不要用 resultClass 当返回参数，即使所有类属性名与数据库字段一一对应，也需要定义；反过来，每一个表也必然有一个与之对应。
-- 说明：配置映射关系，使字段与 DO 类解耦，方便维护。
```
```sql
'4.强制' xml 配置中参数注意使用：#{}，#param# 不要使用${}，此种方式容易出现 SQL 注入。
```
```sql
'5.强制' iBATIS 自带的 queryForList(String statementName, int start, int size) 不推荐使用。
-- 说明：此方法在数据库取到 statementName 对应的SQL语句的所有记录，再通过 subList 取 start,size 的子集合，线上因为这个原因曾经出现过 OOM

-- 正例：在 sqlmap.xml 中引入 #start#, #size#
Map<String, Object> map = new HashMap<String, Object>();
map.put("start", start);
map.put("size", size);
```
```sql
'6.强制' 不允许直接拿 HashMap 与 Hashtable 作为查询结果集的输出。
-- 反例：某同学为避免写一个<resultMap>，直接使用 HashMap 来接收数据库返回结果，
--      结果出现异常是把 bigint 转成 long 值，而线上由于数据库版本不一致，，解析成 BigInteger，导致线上问题。
```
```sql
'7.强制' 更新数据表记录时，必须同时更新记录对应的 gmt_modified 字段值为当前时间。（gmt 格林威治时间）
```
```sql
'8.推荐' 不要写一个大而全的数据更新接口，传入为 POJO 类，不管是不是自己的目标更新字段，都进行 update table set c1=value1, c2=value2, c3=value3; 这是不对的。
-- 执行 SQL时，尽量不要更新无改动的字段，一是易出错； 二是效率低； 三是 binlog 增加存储。
```
```sql
'9.参考' @Transactional 事务不要滥用。事务会影响数据库的 QPS。
另外使用事务的地方需要考虑各方面的回滚方案，包括缓存回滚、搜索引擎回滚、消息补偿、统计修正等。
```
```sql
'10.参考' <isEqual>中的 compareValue 是与属性值对比的常量，一般是数字，表示相等时带上此条件；
<isNotEmpty>表示不为空且不为 null 时执行； <isNotNull>表示不为 null 值时执行。
```
```sql

```


# 基本概念

## 常见概念

> 类库 & 框架

- 类库：`提供的类没有封装一定逻辑`。举例：类库就是名言警句，写作文时引入名言警句
- 框架：`区别与类库，里面有一些约束`。举例：框架是填空题

框架是软件的半成品。为解决问题制定的一套整体解决方案，在提供功能基础上进行扩充。框架中不能被封装的代码（变量），需要使用配置文件（xml）。

> 数据库连接池

在高频率访问数据库时，使用数据库连接池可以降低服务器系统压力，提升程序运行效率。对于小型项目不适用数据库连接池。

关闭数据库连接对象，`只是把连接对象归还给数据库连接池，并将其状态变成 Idle，并不是销毁连接`。

>JDK动态代理

Dao接口没有实现类，当调用接口方法时，通过 `全限定名+方法名`拼接字符串作为key值，唯一定位一个MappedStatement。在Mybatis中，每一个select、insert、update、delete标签，都会被解析为一个MapperStatement对象。

Mapper 接口的工作原理是JDK动态代理，Mybatis运行时会使用JDK动态代理为Mapper接口生成代理对象proxy，代理对象会拦截接口方法，转而执行MapperStatement所代表的sql，然后将sql执行结果返回。

`Dao接口里的方法不能重载`！ 因为`全限定名+方法名`保存和寻找策略（接口可以重载，但是xml不能多个相同id）。

> Mybatis是如何将sql执行结果封装为目标对象并返回的？都有哪些映射形式？

第一种是使用 resultMap 标签，逐一定义数据库列名和对象属性名之间的映射关系。

第二种是使用sql列的别名功能，将列的别名书写为对象属性名。有了列名与属性名的映射关系后，Mybatis通过反射创建对象，同时使用反射给对象的属性逐一赋值并返回，那些找不到映射关系的属性，是无法完成赋值的。

> Xml映射文件中，常用的标签有哪些？

insert，delete，select，update，parameterMap，resultMap，resultType，sql，include，selectKey，加上动态sql的9个标签（where，set，if，foreach，choose）。

> 延迟加载原理

使用 CGLIB 创建目标对象的代理对象，当调用目标方法时，进入拦截器方法，比如调用 a.getB().getName()，拦截器invoke() 方法发现 a.getB() 是null值，那么就会单独发送事件保存好的查询关联B对象的sql，把B查询上来，然后调用a.setB(b)，于是a的对象b属性就有值了，接着完成 a.getB().getName() 方法的调用。这就是延迟加载的原理。

当然了，不光是Mybatis，几乎所有的包括Hibernate，支持延迟加载的原理都是一样的。

> 小知识点

SqlSession 实例非线程安全，不能被共享。每次用完必须关闭。

`增删改没有 resultType 属性`，返回值表示受影响的行数，类型可以是：boolean，int，long，void。



## mybatis特点

> 自身特点

MyBatis是一个半自动的ORM（Object Relation Mapping，对象关系映射）框架，底层是对JDBC的封装，开发者只需要关注SQL本身，而不需要去处理Jdbc繁杂的过程代码（如：注册驱动，创建connection，创建statement，手动设置参数，结果集检索等）。

MyBatis通过`xml或注解`的方式将java对象和sql语句映射生成最终执行的sql语句，最后由mybatis框架执行sql并将结果映射成java对象并返回。

> mybatis优点

- SQL写在XML里，解除sql与程序代码的耦合，便于统一管理
- 与JDBC相比，消除了JDBC大量冗余的代码，不需要手动开关连接
- 能够与Spring很好的集成
- 提供映射标签，支持对象与数据库的ORM字段关系映射；提供对象关系映射标签，支持对象关系组件维护。

> mybatis缺点

- SQL语句的编写工作量较大，尤其当字段多、关联表多时，对开发人员编写SQL语句的功底有一定要求。
- SQL语句依赖于数据库，导致数据库移植性差，不能随意更换数据库。

> 和jdbc比较

mybatis抽离出数据库的连接，关闭的操作。抽离了sql语句，并且可以自动的进行参数的设置，封装结果集。

> 和hibernate比较

- Mybatis和hibernate不同，它不完全是一个ORM框架，因为MyBatis需要程序员自己编写Sql语句。
- Mybatis直接编写原生态sql，可以严格控制sql执行性能，灵活度高。但是灵活的前提是mybatis无法做到数据库无关性，如果需要实现支持多种数据库的软件，则需要自定义多套sql映射文件，工作量大。 
- Hibernate对象/关系映射能力强，数据库无关性好。 


> 结论

- JDBC：sql包含在代码中，硬编码高耦合。实际开发中sql频繁修改，维护不易。
- mybatis：半自动化ORM框架。sql和java编码分开，一个专注数据，一个专注业务，低耦合。
- Hibernate: 全自动ORM。自动产生sql，但不灵活。

##jdbc不足

> JDBC编程有哪些不足之处，MyBatis是如何解决这些问题的？

① 数据库连接的创建、释放频繁造成系统资源浪费，从而影响系统性能，如果使用数据库链接池可解决此问题。

解决：在`Mybatis-Config.xml`中配置数据链接池，使用连接池管理数据库链接。

② Sql语句写在代码中造成代码不易维护，实际应用sql变化的可能较大，sql变动需要改变java代码。

解决：将Sql语句配置在 XXmapper.xml 文件中与java代码分离。

③ 向sql语句传参数麻烦，因为sql语句的where条件不一定，可能多也可能少，占位符需要和参数一一对应。 

解决： Mybatis自动将java对象映射至sql语句，通过statement中的parameterType定义输入参数的类型。

④ 对结果集解析麻烦，sql变化导致解析代码变化，系统不易维护。

解决：Mybatis自动将sql执行结果映射至java对象，通过statement中的resultType定义输出结果的类型。

```java
public void doJDBC() throws ClassNotFoundException, SQLException, IOException {
    Properties properties = new Properties();
    InputStream in = getClass().getClassLoader().
        getResourceAsStream("application.properties");
    properties.load(in);
    // [1] 声明连接参数
    String url = properties.getProperty("spring.datasource.url");
    String user = properties.getProperty("spring.datasource.username");
    String password = properties.getProperty("spring.datasource.password");
    // [2] 注册驱动
    Class.forName(properties.getProperty("spring.datasource.driverClassName"));
    // [3] 建立数据库连接, 需要用到驱动管理器
    Connection conn = DriverManager.getConnection(url, user, password);
    // [4] 定义SQL语句
    String sql = "select id, gender, name from student";
    // [5] 创建sql发送器, 是由连接对象创建的
    Statement statement = conn.createStatement();
    // [6] 发送并执行sql语句, 得到结果集
    ResultSet rs = statement.executeQuery(sql);
    // [7] 处理结果集
    while (rs.next()) {
        int id = rs.getInt(1);// 数据库列索引从1开始
        String gender = rs.getString("gender");
        String name = rs.getString(3);
        System.out.println(id + " - " + gender + " - " + name);
    }
    // [8] 关闭资源, 先开的后关
    rs.close();
    statement.close();
    conn.close();
}
```



##与Hibernate

- Mybatis和Hibernate不同，它不是一个全自动的ORM框架，因为MyBatis需要程序员自己编写Sql语句。mybatis可以通过XML或注解方式灵活配置要运行的sql语句，并将java对象和sql语句映射生成最终执行的sql，最后将sql执行的结果再映射生成java对象。


- mybatis无法做到数据库无关性，如果需要实现支持多种数据库，则需要自定义多套sql映射文件，工作量大。Hibernate对象/关系映射能力强，数据库无关性好。但是Hibernate的学习门槛高，要精通门槛更高。
- Hibernate属于全自动ORM映射工具，使用Hibernate查询关联对象或者关联集合对象时，可以根据对象关系模型直接获取，所以它是全自动的。而Mybatis在查询关联对象或关联集合对象时，需要手动编写sql来完成，所以，称之为半自动ORM映射工具。


## jdbc流程

- 注册数据库驱动
- 创建并获取数据库链接
- 创建jdbc statement对象
- 设置sql语句，及sql语句中的参数(使用preparedStatement)
- 通过statement执行sql并获取结果，并对执行结果进行解析处理，while(resultSet.next)
- 释放资源（resultSet，preparedStatement，connection）



##MyBatis流程


- 配置`mybatis.xml`。主要包括配置数据库连接池（数据库连接相关信息url，username，password），jdbc事务管理，mapper文件的存储路径。

- 通过mybatis环境等配置信息构造`SqlSessionFactory`即会话工厂。

  ```java
  //(1).创建SqlSessionFactory
  InputStream is = Resources.getResourceAsStream("myabtis.xml");
  SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is);

  //(2).通过SqlSessionFactory创建SqlSession
  SqlSession session = factory.openSession();

  //(3).通过sqlsession执行数据库操作
  FlowerMapper flowerMapper = session.getMapper(FlowerMapper.class);
  List<Flower> res = flowerMapper.listAll();

  //(4).调用session.commit()提交事务

  //(5).调用session.close()关闭会话
  session.close();
  ```

- 由会话工厂创建sqlSession即会话，操作数据库需要通过sqlSession进行。`sqlSession是线程不安全的，每个线程都应该有它独自的sqlSession`，使用完就关闭。

- mybatis底层自定义了Executor执行器接口操作数据库，Executor接口有两个实现，一个是基本执行器、一个是缓存执行器。 （Executor才是真正操作数据库的，不过是底层，所以我们认为是sqlSession在进行操作）。

- Mapped Statement也是mybatis一个底层封装对象，它包装了mybatis配置信息及sql映射信息等。`mapper.xml文件中一个sql对应一个Mapped Statement对象`，sql的id即是Mapped statement的id。 

- Executor通过Mapped Statement`在执行sql前`将输入的java对象映射至sql中，输入参数映射就是jdbc编程中对preparedStatement设置参数。 

- Executor通过Mapped Statement`在执行sql后`将输出结果映射至java对象中，输出结果映射过程相当于jdbc编程中对结果的解析处理过程。








##设计模式

- Builder模式，例如SqlSessionFactoryBuilder、XMLConfigBuilder、XMLMapperBuilder；
- 工厂模式，例如SqlSessionFactory、ObjectFactory、MapperProxyFactory；
- 单例模式，例如ErrorContext和LogFactory；
- 代理模式，`Mybatis实现的核心`，比如MapperProxy、ConnectionLogger，用的jdk的动态代理；还有executor.loader包使用了cglib或者javassist达到延迟加载的效果；
- 组合模式，例如SqlNode和各个子类ChooseSqlNode等；
- 模板方法模式，例如BaseExecutor和SimpleExecutor，还有BaseTypeHandler和所有的子类例如IntegerTypeHandler；
- 适配器模式，例如Log的Mybatis接口和它对jdbc、log4j等各种日志框架的适配实现；
- 装饰者模式，例如Cache包中的cache.decorators子包中等各个装饰者的实现；
- 迭代器模式，例如迭代器模式PropertyTokenizer；








#基本配置

## boot整合

> pom.xml

```xml
<!-- Mybatis 启动器 -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>1.3.2</version>
</dependency>
<!-- mysql 数据库驱动 -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
<!-- druid 数据库连接池 -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.1.17</version>
</dependency>
```
> 必要设置

```properties
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
spring.datasource.url=jdbc:mysql://192.168.8.7:33306/test0329?useSSL=false&allowMultiQueries=true&serverTimezone=GMT%2B8
spring.datasource.username=bluecardsoft
spring.datasource.password=#$%_BC13439677375

#xml路径
mybatis.mapper-locations=classpath*:com/example/demo/mapper/sqlxml/_*.xml
#相关xml配置 <-> 与上不能同时使用
//mybatis.config-location=mybatis.xml

#mybatis的sql打印
logging.level.com.example.amqp_publisher.mapper=debug
```
```java
//全局注解; 可省去每个Mapper文件上的 @Mapper
@MapperScan(value = "com.example.base.*.mapper")
```

```java
//【不推荐】注解版xml; 直接在java文件写sql,省去对应的xml文件
@Select("SELECT sname FROM student WHERE sid=#{id}")
String getNameById(int id);
```

> 非必要设置

```properties
#驼峰命名
mybatis.configuration.map-underscore-to-camel-case=true
#数据库返回为null，也调用映射对象的setter方法
mybatis.configuration.call-setters-on-nulls=true

#全局设置：是否启用延迟加载
mybatis.configuration.lazy-loading-enabled=true
#局部设置：是否不启用延迟加载
mybatis.configuration.aggressive-lazy-loading=true
```
> mapper.xml文件

```xml
<?xml version="1.0" encoding="UTF-8"?>

<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.example.base.mapper.LabelMapper">
    <select id="listLabelsByPage" resultType="com.example.base.pojo.Label">
        SELECT `id`, `labelname`, `state`, `count`, `recommend`, `fans` FROM tb_label
    </select>
</mapper>
```

>资源拷贝插件

```xml
<!-- mvn默认只把 src/main/resources 里的非java文件编译到classes中 -->
<!-- 如果希望 src/main/java 下的文件（如mapper.xml）也编辑到 classes 中，在pom.xml中配置，与<plugins/>节点同级 -->
<build>
    <resources>
        <resource>
            <directory>src/main/java</directory>
        </resource>
        <resource>
            <directory>src/main/resources</directory>
        </resource>
    </resources>
</build>
```
##xml提示

```java
下载dtd文件: "mybatis-3-mapper.dtd"和"mybatis-3-config.dtd"
打开 eclipse -> windows -> preferences -> xml -> xmlcatalog -> add,依次填写:
     //mapper.xml 的代码提示
     location    -> 选择file_system,找到"mybatis-3-mapper.dtd"存放的本地路径
     key_type    -> URI0
     key            -> http://mybatis.org/dtd/mybatis-3-mapper.dtd
     
     //mybatis-config.xml 的代码提示
     location    -> 选择file_system,找到"mybatis-3-config.dtd"存放的本地路径
     key_type    -> URI0
     key            -> http://mybatis.org/dtd/mybatis-3-config.dtd
```
## 相关概念

> parameterType & resultType & resultMap

**parameterType：** 指定输入参数类型，mybatis通过 OGNL 从输入对象中获取参数值拼接在sql中。

**resultType：** 指定输出结果类型，mybatis将sql查询结果的一行记录数据映射为resultType指定类型的对象。如果有多条数据，则分别进行映射，并把对象放到容器List中

**resultMap：** 字段和java对象中属性不一致时，使用resultMap。resultMap常用作复杂的映射配置（多表查询）.

> mapper动态代理，只需要写接口，不需要写实现类，实现类由mybatis框架自动创建。使用规则：

- Mapper.xml文件中的`namespace`要和Mapper接口中的类路径（全限定名）一致
- Mapper.xml文件中的`statement的id`要和mapper接口中的方法的名称一致
- Mapper.xml文件中的`parameterType`要和mapper接口中的方法的参数类型一致
- Mapper.xml文件中的`resultType`要和mapper接口中的方法的返回值数据类型一致



## 使用经验

> 1.手动增量配置映射文件

当有工具生成Mapper等配置文件的时候，很多人就不愿意手动写了。其实MyBatis的生成工具不是特别有用，生成的方法几乎不可用，删删改改老半天还不如自己手写快。而且需要新加或修改属性、方法时，也是没法使用生成的文件，因为需要保留好原有的一些属性和方法。手写映射文件时先定义出用到的字段，这样配置文件会简洁清晰，同时结果映射时效率会更高。

> 2.Mapper层参数为Map，由Service层负责重载

`Mapper由于机制的问题，不能重载，参数一般设置成Map`，但这样会使参数变得模糊，如果想要使代码变得清晰，可以通过service层来实现重载的目的，对外提供的Service层是重载的，但这些重载的Service方法其实是调同一个Mapper，只不过相应的参数并不一致。

也许有人会想，为什么不在Service层也设置成Map呢？我个人是不推荐这么做的，虽然为了方便，我在之前的项目中也大量采用了这种方式，但 很明显会给日后的维护工作带来麻烦。因为这么做会使你整个MVC都依赖于Map模型，这个模型其实是很不错的，方便搭框架，但存在一个问题：仅仅看方法签名，你不清楚Map中所拥有的参数个数、类型、每个参数代表的含义。

试想，你只对Service层变更，或者DAO层变更，你需要清楚整个流程中Map传递过来的参数，除非你注释或者文档良好，否则必须把每一层的 代码都了解清楚，你才知道传递了哪些参数。针对于简单MVC，那倒也还好，但如果层次复杂之后，代码会变得异常复杂，而且如果我增加一个参数，需要把每一 个层的注释都添加上。相对于注释，使用方法签名来保证这种代码可控性会来得更可行一些，因为注释有可能是过时的，但方法签名一般不太可能是陈旧的。

> 3.尽量少用if choose等语句，降低维护的难度

Mybatis的配置SQL时，尽量少用if choose 等标签，`推荐使用SQL原生的方式来解决一些动态问题`，而不应该完全依赖Mybatis来完成动态分支的判断，因为判断分支过于复杂，而且难以维护。

一方面，如果需要调试Mybatis中的SQL，需要去除大量的判断语句，非常麻烦。另一方面，大量的if判断，会使生成的SQL中包含大量的空格，增加网络传输的时间，也不可取。

> 4.用XML注释取代SQL注释

 Mybatis中原SQL的注释尽量不要保留，注释会引发一些问题，如果需要使用注释，可以在XML中用来注释，保证在生成的SQL中不会存在SQL注释，从而降低问题出现的可能性。这样做还有一个好处，就是在IDE中可以很清楚的区分注释与 SQL。

```xml
<select id="countStudent" resultType="java.lang.Integer">
    select count(id) from student -- 这是注释
    where id>10
</select>
```

> 5.尽可能使用#{ }，而不是${ }

对于`#{}`会生成预编译SQL，会正确的处理数据的类型，而`${}`仅仅是文本替换。 对于特殊情况，如动态注入列名、表名等，可以使用${ }。


# 基本使用

## 内置参数

> **_parameter** 

- 单个参数：_parameter 就是这个参数
- 多个参数：mybatis封装成map，_parameter 表示这个map。

> **_databaseId：** 对于配置了`databaseIdProvider`，_databaseId 是指当前数据库的别名（mysql，oracle，sql-server...）

```xml
<select id="getEmpsByInnerParameter" resultType="com.x.bean.Employee">
    <if test="_databaseId=='mysql'"> <!-- mysql -->
        select * from tb_employee
        <if test="_parameter!=null">
            where last_name like #{_parameter} <!--参数不为空，_parameter 表示lastName-->
        </if>
    </if>
    <if test="_databaseId=='oracle'"> <!-- oracle -->
        select * from employees
        <if test="_parameter!=null">
            where last_name like #{_parameter.lastName} <!-- _parameter 表示Emp对象 -->
        </if>
    </if>
</select>
```
##抽取引用

```xml
<sql id="ref">
    id,name,age,address,companyId <!-- sql标签 用于抽取可重用的sql片段 -->
</sql>

<select id="selById" resultType="com.heiketu.pojo.Users">
    select
    <include refid="ref" />
    from usrs where id = #{id} <!-- include标签 用于引用前者 -->
</select>
```
## 存储过程

```xml
<!-- 使用标签<select/> && statementType="CALLABLE" -->
<select id="get_park_free_count0" resultType="java.util.Map" statementType="CALLABLE">
    {call get_park_free_count(#{parkId})}
</select>
```

## 类型别名

> 系统内置别名

```java
int _int; boolean _boolean //一般数据类型，加前缀_
Integer int; String string; Map map; List list //一般类型的包装类，类型小写
```
> 【不推荐】自定义别名

```java
@Alias("author") //注解-单独类起别名
mybatis.type-aliases-package=com.x.x.entity //配置某个包下所有类的别名(类名小写)-批量别名
```
## 字段别名

> sql语句别名

```sql
select order_id id, order_price price, order_no orderNo from orders where order_id=#{id};
```

> 使用标签resultMap

```xml
<select id="getOrder" resultMap="orderMap"> <!-- resultMap 取代 resultType -->
    select * from orders where order_id=#{id}
</select>

<resultMap type="com.x.order" id="orderMap"> 
    <id property="id" column="order_id" /> <!-- id-主键 -->
    <result property="price" column="order_price" /> <!-- result-非主键 -->
    <result property = "orderNo" column ="order_no" /> <!-- property-属性名; column-字段名 -->
</reslutMap>
```
## 多参数取值

> 多个入参

- 【不推荐】第 1 个参数 ---> #{param1}

- 【不推荐】第 1 个参数 ---> #{0}

- 【推荐】手动`将多参数封装成 pojo 或 Map<String, Object>`

- 【推荐】在Mapper接口的参数列表使用注解 @Param("passage_id")

  ```java
  String getMemo(@Param("mark") String mark, @Param("passageId") int passageId);

  <select id="getMemo" resultType="java.lang.String">
      SELECT memo FROM system_set WHERE mark=#{mark} and passage_id=#{passageId}
  </select>
  ```

> 多列回参

```java
//对于多列返回值，可以使用Map接收，也可以自定义pojo
Map<String, Object> getMemoAndTypeId(String args);

<select id="getMemoAndTypeId0" resultType="java.util.Map">
    SELECT memo,type_id typeId FROM system_set WHERE mark=#{args}
</select>
```

> 多行，多列回参

```java
//接口返回list<map>，但xml的resultType="map"
List<Map<String, Object>> getAllPassage();

<select id="getAllPassage" resultType="java.util.Map">
    select id,old_park_id,park_id from passage where state=1
</select>
```

> 回参Map：`对于有驼峰命名的key，必须将数据表中的字段使用别名`

```java
//其中，key为某一列name，value为每一行封装成的 pojo 或 Map。
如：{"喇叭花":{"flowerName":"喇叭花","flowerId":1},"牵牛花":{"flowerName":"牵牛花","flowerId":2}... ...}

@MapKey("flowerName") //这里的‘flowerName’表示的是javabean中的属性名，而非数据库中的字段名。所以xml中的 flower_name 必须改别名。切记！
Map<String, Flower> listByName(String flowerName);

<select id="listByName" resultType="com.x.Flower">
    select flower_id,flower_name flowerName from flower where flower_name like #{flowerName}
</select>
```
## 自增主键

> 入参javabean ---> 采用自增长策略，自动生成的键值在 insert 方法执行完后可以被设置到传入的参数对象中。

```java
//是否("useGeneratedKeys")将产生的主键赋值到属性("keyProperty")中
<insert id="addOne" useGeneratedKeys="true" keyProperty="id">
    INSERT INTO person VALUES(default, #{age}, #{name})
</insert>
```
> 入参Map

```xml
<insert id="addOne"> <!--order: 相对于insert的执行顺序.(BEFORE|AFTER)-->
    <selectKey keyProperty="id" resultType="int" order="AFTER">
            SELECT LAST_INSERT_ID()
    </selectKey>
        INSERT INTO person (age,name) VALUES(#{age},#{name})
    </insert>
```

> 主键UUID

```java
<insert id="addOne">
    <selectKey keyProperty="id" resultType="String" order="BEFORE">
        SELECT uuid()
    </selectKey>
    INSERT INTO    person (id,age,name) VALUES(#{id},#{age},#{name})
</insert>
```

## 特殊符号

> **#{ }**    预编译处理，`防止SQL注入`，安全；自动对传入参数添加一个单（双）引号； 可以通过OGNL方式取值： 参数.属性。原理是将sql中的 #{} 替换为 ?，然后调用 PreparedStatement.set() 方法来赋值。

> **${ }**     sql字符串拼接； 需手动添加单（双）引号；也可以通过OGNL方式取值：参数.属性。配合使用`statementType="STATEMENT"`。原理是把 ${} 替换成变量的值。

对于 #{ } 会生成预编译SQL，会正确的处理数据的类型，而 ${ } 仅仅是文本替换。

```java
//如果使用#{}，那么生成的SQL为：
select name from student where xCode = ?       //传值为'S123456'；
//如果使用${}，那么生成的SQL为：
select name from student where xCode = S123456 //直接字符串拼接

//所以，如果 xCode 的数据类型为varchar,那么使用${}就会报错。
```



```java
//一般，建议使用#{ }；特殊情况必须要用${ }。如：动态传入字段，表名
//用 #{} ---> order by 'id,name' ,变为根据字符串排序,与需求不符
//用 ${} ---> order by id,name ,符合需求
<select id="get_res_by_field" resultType="map" statementType="STATEMENT">
    SELECT * FROM person ORDER BY ${field} DESC LIMIT 10
</select>
```
## 模糊查询

> 【推荐】java中拼接

```java
SELECT * FROM user WHERE name LIKE #{name} //java代码中传参：%张%
```

> 使用占位符${ }

```java
SELECT * FROM user WHERE name LIKE '%${name}%' //张 -> '%张%'
SELECT * FROM user WHERE name LIKE '%#{name}%' //张 -> "%'张'%" -> 错误
```

> xml中拼接

```java
SELECT * FROM user WHERE name LIKE concat('%', #{username}, '%') //张

<select id="selectLike">
    <bind name="name" value="'%'+_parameter+'%'" />
    select * from user where name like #{name} //张
</select>
```

# 动态（X）

> 执行原理： 根据表达式的值完成逻辑判断并动态拼接sql的功能。

## where

- 去掉where标签体的第一个and
- 标签体里有内容则生成关键字 where，没有则不生成。`但对于and写在条件之后的也会出问题：name like #{name} and`

```java
<select id="listByIf" resultType="com.x.bean.Flower">
    select * from flower /** where true(替换) */
    <where> 
        <if test="null!=name and ''!=name">
            and name like #{name}
        </if>
        //@class@method(args)调用类的静态方法 - 详见附表
        <if test="@org.apache.commons.lang3.math.NumberUtils@isParsable(price)">
            and price > #{price} //检验是否为数字??? 是则增加为查询条件，否则不增加
        </if>
    </where>
</select>
```

## set

- 去掉set标签体的最后一个逗号
- 标签体里有内容则生成关键字 set，没有不生成

```java
<update id="updateByIf">
    update flower
    <set>
        id=#{id}, //防止<set/>内容为空,不生成 set 关键字
        
        <if test="name!=null and name!=''"> //满足条件,则追加到更新条件
            name=#{name},
        </if>
        <if test="price>=0">
            price=#{price},
        </if>
    </set>
    where id=#{id}
</update>
```

## choose

- 只要一个分支满足，其他都不执行。相当于 if-elseif-else

```java
<select id="listByChoose" resultType="com.x.bean.Flower">
    select * from flower
    <where>
        <choose>
            <when test="name!=null and name!=''">
                name like #{name} //if - 先判断,满足即结束,不再去判断elseif
            </when>
            <when test="@org.apache.commons.lang3.math.NumberUtils@isParsable(price)">
                price > #{price} //elseif
            </when>
            <otherwise>
                production like #{production} //else
            </otherwise>
        </choose>
    </where>
</select>

```

## foreach

> 遍历查询

```java
List<Passage> listByForeach(List<int> passageIdList);

//collection    -> 要遍历的集合；对于list类型会封装到一个特殊的map中，其key就为list
//item            -> 集合中的每一个对象
//separator        -> 每个元素之间的分隔符
//index            -> 索引。遍历list，index表示索引，item为对应的值；遍历map，index为key，item为map值
//open-close    -> 遍历所有结果拼接一个开始（结束）的字符
<select id="listByForeach" resultType="com.x.bean.Passage">
    select * from Passage where id in
    <foreach collection="list" item="item" index="index" open="(" close=")" separator=",">
        #{item} //(1,2,5,7)
    </foreach>
</select>
```

> 批量新增

```java
//mysql特有的批量插入 <--> insert into t_customer (id, c_name) values (?,?),(?,?)....
boolean saveBatch(List<Flower> list);

<insert id="saveBatch">
    insert into flower (name, price) values
    <foreach collection="list" item="item" index="index" separator=",">
        (#{item.name}, #{item.price})
    </foreach>
</insert>
```

#分页查询

## 插件版

>  在插件的拦截方法内拦截待执行的sql，然后重写sql，添加对应的物理分页语句和分页参数。

```xml
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper-spring-boot-starter</artifactId>
    <version>1.2.5</version>
</dependency>
```

```java
@GetMapping("/person/{pageNum}/{pageSize}")
public PageInfo<Person> listByPage(@PathVariable int pageNum, @PathVariable int pageSize) {
    PageHelper.startPage(pageNum, pageSize); //查询之前设置：页码数，页容量
    List<Person> list = service.listAll();
    
    //PageInfo包含了非常全面的分页属性: isFirstPage,hasPreviousPage,prePage,pages,startRow....
    PageInfo<Person> pageInfo = new PageInfo<>(res);//包含导航页码的PageInfo结果集 - 详见附表
    //int[] nums = pageInfo.getNavigatepageNums();//导航页码
    
    return pageInfo;
}
```

##非插件版

> xml占位符 ? 不允许在关键字前后进行数学运算，所以需要在代码中完成计算，然后再传递到 xml 中

```java
int pageSize = 2, pageNum = 2;
Map<String, Object> map = new HashMap<>();
map.put("pageSize", pageSize); //页容量
map.put("pageStart", pageSize * (pageNum - 1)); //起始行
List<People> list = service.listPage(map);

<select id="listPage" resultType="com.x.People">
    select * from people limit #{pageStart}, #{pageSize}
</select>        
```

## PageHelper

```java
//PageHelper中默认 PageInfo 的成员变量
public class PageInfo<T> extends PageSerializable<T> {
    //当前页
    private int pageNum;
    //每页的数量
    private int pageSize;
    //当前页的数量
    private int size;

    //由于startRow和endRow不常用，这里说个具体的用法
    //可以在页面中"显示startRow到endRow 共size条数据"

    //当前页面第一个元素在数据库中的行号
    private int startRow;
    //当前页面最后一个元素在数据库中的行号
    private int endRow;
    //总页数
    private int pages;

    //前一页
    private int prePage;
    //下一页
    private int nextPage;

    //是否为第一页
    private boolean isFirstPage = false;
    //是否为最后一页
    private boolean isLastPage = false;
    //是否有前一页
    private boolean hasPreviousPage = false;
    //是否有下一页
    private boolean hasNextPage = false;
    
    //导航页码数
    private int navigatePages;
    //所有导航页号
    private int[] navigatepageNums;
    //导航条上的第一页
    private int navigateFirstPage;
    //导航条上的最后一页
    private int navigateLastPage;
}
```

# 联表查询

## 联表查询对象

```java
//查询学生时，把老师信息也查出
List<Student> listAllStudent();

class Student{
    private Integer id;
    private String name;
    
    private Teacher teacher; 
}
```

> 列别名 + 自动映射（AutoMapping ---> `当列别名和 pojo 属性一致时，mybatis自动映射赋值`）

```java
//在SQL中'.'是关键字符,所以在两侧添加反单引号 `teacher.id`
<select id="listAllStudent" resultType="com.x.pojo.Student">
    SELECT s.id id, s.name name, t.id `teacher.id`, t.name `teacher.name`
    FROM student s LEFT JOIN teacher t
    ON t.id=s.tid LIMIT 5
</select>
```

> 列别名 + 手动映射（resultMap）

```java
//左外连接(left outer join)：可查询所有的学生信息
<select id="listAllStudent" resultMap="studentMap">
    SELECT s.id sid, s.name sname, t.id tid, t.name tname
    FROM student s LEFT OUTER JOIN teacher t
    ON s.tid=t.id LIMIT 5
</select>

<resultMap type="com.example.*.bean.Student" id="studentMap">
    <id column="sid" property="id"/>
    <result column="sname" property="name"/>

    <result column="tid" property="teacher.id"/>
    <result column="tname" property="teacher.name"/>
</resultMap>
```

> 关联查询（association ---> 即嵌套的ResultMap）

```java
<select id="listAllStudent" resultMap="studentMap">
    SELECT s.id sid, s.name sname, t.id tid, t.name tname
    FROM student s LEFT OUTER JOIN teacher t
    ON s.tid=t.id LIMIT 5
</select>

<resultMap type="com.example.spring.bean.Student" id="studentMap">
    <id column="sid" property="id" />
    <result column="sname" property="name" />

    //<association/>可看做<reslutMap/>
    //property：Student类中的属性名； javaType：装配后返回的java类型
    <association property="teacher" javaType="com.example.spring.bean.Teacher">
        <id column="tid" property="id" />
        <result column="tname" property="name" />
    </association>
</resultMap>
```

> 分步查询（association ---> 即嵌套的select语句）

先查询（1次）学生表的所有信息，然后根据学生的外键 tid 查询（N次）老师表的所有信息`（注：此方法会产生N+1查询问题，尽量不要用嵌套的select语句）`。

- 效率低，但满足：只查询学生信息 和 查询学生和老师信息 两种情况。
- 懒加载模式，只使用学生信息时，不会进行老师信息查询；只有使用老师信息时，才去二次查询！

```java
<select id="listAll" resultMap="stuMap">
    select * from student
</select>

<resultMap type="com.x.pojo.Student" id="stuMap">
    // student表的其他列,使用 AutoMapping 自动装配! 但对于二次查询的参数tid,必须显示装配一次
    <result column="tid" property="tid"/>
    
    // association   -> 当property对应的'teacher'是一个对象时使用
    // property      -> Student类中的属性名
    // fetchType     -> 是否懒加载; lazy-懒加载,eager-立即加载.
    // select        -> 通过哪个查询可以查出这个对象的信息
    // column        -> 把当前表哪个列的值作为参数传递给select
    // 对于分步查询传参多列,可以使用形式 column="{key1=id, key2=name}"; mybatis底层将参数封装成map
    <association property="teacher" fetchType="lazy" 
         select="com.x.mapper.TeacherMapper.selById" column="tid"></association>
</resultMap>

```

## 联表查询集合

```java
//查询老师时，把所有学生信息查出
List<Teacher> listAllTeacher();

class Teacher{
    private Integer id;
    private String name;
    
    private List<Student> list; 
}
```

> 关联查询（`collection --> 一对多映射时使用，association ---> 一对一映射时使用`）

```java
<select id="listAllTeacher" resultMap="teacherMap">
    SELECT t.id tid, t.name tname, s.id sid, s.name sname
    FROM teacher t LEFT JOIN student s
    ON t.id=s.tid LIMIT 10
</select>

<resultMap type="com.example.spring.bean.Teacher" id="teacherMap">
    <id column="tid" property="id" />
    <result column="tname" property="name" />

    //collection -> 当property是集合类型时使用
    //ofType     -> 集合的泛型是哪个类
    <collection property="studentList" ofType="com.example.spring.bean.Student">
        <id column="sid" property="id" />
        <result column="sname" property="name" />
    </collection>
</resultMap>
```

> 分步查询

```java
<select id="listAllTeacher" resultMap="teacherMap">
    SELECT id, name FROM teacher
</select>

<resultMap type="com.example.spring.bean.Teacher" id="teacherMap">
    <id column="id" property="id" />
    <result column="name" property="name" />

    <collection property="studentList" column="id" 
         select="com.example.spring.mapper.StudentMapper.selStuByTid" />
</resultMap>

//此方法定义在 StudentMapper.xml
<select id="selStuByTid" resultType="com.example.spring.bean.Student">
  SELECT id, name FROM student WHERE tid=#{tid}
</select>
```

> 注解实现

```java
@Results(value={ //相当于<resultMap/>
    @Result(id=true, property="id", column="id"), //相当于<id/>或<result/>, id=true则相当于id
    @Result(property="name", column="name"),
    @Result(property="list", column="id", //@Many相当于<collection/>, @One相当于<association/>
        many=@Many(select="com.x.mapper.StudentMapper.selByTid"))
})
@Select("select * from teacher")
List<Teacher> selTeacher();
```

##鉴别器

根据某列值的不同，选取不同的 resultMap。

```java
<select id="getEmployee" resultMap="employeeMap">
    select id, name from employee where id =#{id}
</select>

<resultMap id="employeeMap" type="com.x.pojo.Employee">
    <id property="id" column="id"/>
    <result property="name" column="name"/>
    <result property="sex" column="sex"/>

    <association property="empCard" column="id" 
         select="com.x.mapper.EmpCardMapper.selByEmpId"/>
    <collection property="projectList" column="id" 
         select="com.x.mapper.ProjectMapper.listByEmpId"/>

    <discriminator javaType="int" column="sex"> //鉴别器
        <case value="0" resultMap="femaleEmpMap"/>
        <case value="1" resultMap="maleEmpMap"/>
    </discriminator>
</resultMap>

//不同Employee.sex, 不同Employee.sexList. extends: 等同于java的继承
<resultMap id="femaleEmpMap" type="com.x.pojo.FemaleEmployee" extends="employeeMap">
    <association property="sexList" column="id"
         select="com.x.mapper.FemaleEmpMapper.selById"/>
</resultMap>

<resultMap id="maleEmpMap" type="com.x.pojo.MaleEmployee" extends="employeeMap">
    <association property="sexList" column="id" 
         select="com.x.mapper.MaleEmpMapper.selById"/>
</resultMap>
```

# 缓存相关

## 一级缓存

>MyBatis 默认开启一级缓存。一级缓存的作用域为一个SqlSession。

```sql
MyBatis会在一次会话中，一个SqlSession对象中创建一个本地缓存(local cache)，
对于每一次查询，都会尝试根据查询的条件去本地缓存中查找是否在缓存中，
如果在缓存中，就直接从缓存中取出，然后返回给用户；否则，从数据库读取数据，将查询结果存入缓存并返回给用户。

Mybatis内部存储缓存使用HashMap，key为 'hashCode+sqlId+Sql' 语句。value为从查询出来映射生成的java对象。
```

## 二级缓存

>默认情况下，查出的数据先放在一级缓存中。只有当 `sqlSession.commit() 或 close()`，一级缓存数据才会转移到二级缓存中。

```
Mybatis的二级缓存即查询缓存，它的作用域是一个mapper的NameSpace，即在同一个NameSpace中查询sql可以从缓存中获取数据。

二级缓存是可以跨SqlSession的，也是一个内存级别的缓存。
```

> 缓存设置

```properties
#（1）默认开启
mybatis.configuration.cache-enabled=true
```

```java
（2）实体类POJO实现序列化接口 implements Serializable。（<cache/>设置 readOnly="true"，则可省）
```

```xml
（3）<cache eviction="LRU" flushInterval="60000" size="1024" readOnly="true"/> <!--mapper.xml 新增-->
```

>cache标签参数

```
eviction       -> 缓存的回收策略，默认LRU（可选值：LRU，FIFO，SOFT，WEAK）
flushInterval  -> 缓存失效时间，默认永不失效（单位毫秒）
size           -> 缓存存放多少个元素，默认1024
type           -> 指定自定义缓存的全类名（需要实现Cache接口，自定义缓存类）
readOnly       -> 是否只读，默认false。
```



# 附表

## OGNL表达式

```java
e1 lt(小于) e2; //lte 小于等于; gt 大于; gte 大于等于; eq 等于; neq 不等于
e1 or(and) e2; e1 in(not in) e2; e1 +(- * / %) e2;
!e(非); not e(求反)

e.method(args)        //调用对象方法
e.property            //对象属性值
e1[ e2 ]              //对于List，数组和Map，按索引取值

@class@method(args)   //调用类的静态方法
@class@field          //调用类的静态字段值
```
## 转义字符

```java
//字符转义
< > & ' "        -> &lt; &gt; &amp; &apos; &quot;

//使用<![CDATA[]]>
< > & ' "        -> <![CDATA[ < ]]> <![CDATA[ > ]]> <![CDATA[ & ]]> ...
```

> <![CDATA[]]>和xml转义字符的关系：

```xml
(1).对于短字符串<![CDATA[]]>写起来啰嗦，对于长字符串转义字符写起来可读性差。
(2).<![CDATA[]]>表示xml解析器忽略解析，所以解析更快。
(3).<![CDATA[]]>不适用所有情况，转义字符可以。
```

> <![CDATA[]]>不适合情况：

```java
(1).此部分不能再包含"]]>"
(2).不允许嵌套使用
(3)."]]>"这部分不能包含空格或者换行.
```



#华丽分割线



# 基础配置

> 基础概念

```java
JPA                -> //Java-Persistence-API，对持久层操作的标准（接口 + 文档）

Hibernate          -> //全自动化的ORM框架
Hibernate JPA      -> //实现了 JPA 标准的 Hibernate（Hibernate-3.2+）

Spring Data        -> //用于简化数据库（SQL，NoSQL...）访问，并支持云服务的开源框架.
Spring Data JPA    -> //Spring Data的一个子模块，实现了 JPA 标准的 Spring Data，底层是 Hibernate

Spring Data Redis  -> //通过简单配置，实现对reids各种操作，异常处理及序列化，支持发布订阅
```
> 常用配置

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```
```properties
spring.jpa.database=mysql
spring.jpa.show-sql=true

#可选参数-create: 每次启动都会删除旧表，新建一个空表
#可选参数-update: 根据实体类创建/更新数据库表
spring.jpa.hibernate.ddl-auto=update
```
> 实体类

```java
@Data
@Entity //表明是一个JPA实体，自动建表
@Table(name = "t_emp") //默认表名为类名小写
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id //主键标识，对于复合主键，两个注解@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //自增
    private Integer id;

    @Column(name = "first_name") //显示指定列名，默认将驼峰转下划线
    private String firstName;

    @Column //默认列名 -> gender_flag
    private Boolean genderFlag;
}
```

#两种查询

>基于方法名称的查询：驼峰命名规则，findBy（关键字） + 属性名称（首字母大写） + 查询条件（首字母大写，Like，OrderBy...）

```java
public interface EmployeeRepoDao extends Repository<Employee, Integer> {

    //SELECT * FROM t_emp WHERE first_name='zhang' AND gender_flag=TRUE
    List<Employee> findByFirstNameAndGenderFlag(String firstName, boolean genderFlag);

    //SELECT * FROM t_emp WHERE first_name LIKE '%ang%' OR gender_flag=TRUE ORDER BY id DESC
    List<Employee> findByFirstNameLikeOrGenderFlagOrderByIdDesc(String firstName, boolean genderFlag);
}
```
>基于注解的查询（一）：使用HQL，使用bean属性名称替代数据库字段进行查询。`没有 SELECT`

```java
public interface EmployeeRepoDao extends Repository<Employee, Integer> {

    @Query("FROM Employee WHERE firstName LIKE ?1 OR genderFlag=?2 ORDER BY id DESC") //没有 SELECT
    List<Employee> queryByHQL(String firstName, boolean genderFlag);
}
```
> 基于注解的查询（二）：使用SQL，`nativeQuery = true`

```java
public interface EmployeeRepoDao extends Repository<Employee, Integer> {

    //（1）直接使用参数序号，从 1 开始，使用 ？1 进行占位。
    @Query(value = "SELECT * FROM t_emp WHERE first_name LIKE ?1 OR gender_flag = ?2 ORDER BY id DESC", 
           nativeQuery = true) 
    List<Employee> queryBySQL(String firstName, boolean genderFlag);

    //(2)使用 @Param("参数名") 进行参数重命名，并且使用 :参数名 进行占位。
    @Query(value = "SELECT * FROM t_emp WHERE first_name LIKE :fName OR gender_flag = :genderFlag ORDER BY id DESC", 
           nativeQuery = true)
    List<Employee> queryBySQL(@Param("fName") String firstName, @Param("genderFlag") boolean genderFlag);
}
```

> 基于注解的 增删改：必须添加注解 `@Modifying`

```java
public interface EmployeeRepoDao extends Repository<Employee, Integer> {

    //（3）.对于增加，删除 或 更新，必须添加注解 @Modifying
    @Modifying
    //@Transactional 
    @Query(value = "UPDATE t_emp SET first_name=?1 WHERE id=?2", nativeQuery = true)
    Integer updateBySQL(String firstName, Integer id);
}
```

>事务相关

```java
业务逻辑层 'Service' 调用多个 Repository 方法时，需要在 Service 方法上声明事务 '@Transactional'。
```

#常用接口

> `Repository`：Spring-Data-JPA 顶层接口，标识接口，空接口。

> `CrudRepository`：最基础的CRUD，extends Repository

```java
public interface EmployeeCrudDao extends CrudRepository<Employee, Integer> {}
```

```java
@Test
public void daoCrud() {
    //save(): 先查询数据表中是否存在该id数据??? 无则新增; 有则更新
    Employee save = employeeCrudDao.save(new Employee(7, "张三", true));
    Iterable<Employee> all = employeeCrudDao.findAll();
    System.out.println(save + " - " + JSON.toJSON(all));
}
```
> `PagingAndSortingRepository`：分页和排序功能，extends CrudRepository

```java
public interface EmployeePSDao extends PagingAndSortingRepository<Employee, Integer> {}
```

```java
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
```
> `JpaRepository`：对父接口方法的返回值进行适配处理，extends PagingAndSortingRepository

```java
public interface EmployeeJpaDao extends JpaRepository<Person, Integer> {}
```

> `JpaSpecificationExecutor`：提供多条件查询，分页，排序，独立于以上接口存在，所以`需配合以上接口使用`

```java
public interface EmployeeDao extends JpaSpecificationExecutor<Employee>, JpaRepository<Employee, Integer> {}
```

```java
@Test
public void daoDao() {
    Specification<Employee> spec = new Specification<Employee>() {
        /**
          * @param root     查询对象的属性封装,即 Employee
          * @param query    查询关键字 SELECT, WHERE, ORDER BY ...
          * @param builder  查询条件 =, >, LIKE
          * @return         封装整个查询条件
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
```
#关联查询

##一对多关联

> 一对多关联映射：dept 与 emp 是一对多关系

```java
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
```
```java
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
```

>新增DEMO

```java
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
```
>查询DEMO

```java
@Test
public void findOne2Many() {
    Optional<Employee> optional = employeeDao.findById(10);
    if (optional.isPresent()) {
        Employee employee = optional.get();
        System.out.println("DeptName: " + employee.getDept().getDeptName());
    }
}
```
##多对多关联

>多对多关联映射：emp 和 role 是多对多关系

```java
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
```
>新增DEMO

```java
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
```
>查询DEMO

```java
@Test
public void findMany2Many() {
    Optional<Employee> optional = employeeDao.findById(12);
    System.out.println(optional.get().getRoles());
}
```