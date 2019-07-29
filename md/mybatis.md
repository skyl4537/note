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

```
> 必要设置

```properties

```
```java

```

```java

```

> 非必要设置

```properties

```
> mapper.xml文件

```xml

```

>资源拷贝插件

```xml

```
##xml提示

```shell

```
## 相关概念

> parameterType & resultType & resultMap

```shell
parameterType： 指定输入参数类型，mybatis通过 OGNL 从输入对象中获取参数值拼接在sql中。

resultType： 指定输出结果类型，mybatis将sql查询结果的一行记录数据映射为resultType指定类型的对象。如果有多条数据，则分别进行映射，并把对象放到容器List中

resultMap： 字段和java对象中属性不一致时，使用resultMap。resultMap常用作复杂的映射配置（多表查询）.
```

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

```
##抽取引用

```xml

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

```
> 【不推荐】自定义别名

```java

```
## 字段别名

> sql语句别名

```sql

```

> 使用标签resultMap

```xml

```
## 多参数取值

> 多个入参

- 【不推荐】第 1 个参数 ---> #{param1}

- 【不推荐】第 1 个参数 ---> #{0}

- 【推荐】手动`将多参数封装成 pojo 或 Map<String, Object>`

- 【推荐】在Mapper接口的参数列表使用注解 @Param("passage_id")

  ```java

  ```

> 多列回参

```java

```

> 多行，多列回参

```java

```

> 回参Map：`对于有驼峰命名的key，必须将数据表中的字段使用别名`

```java

```
## 自增主键

> 入参javabean ---> 采用自增长策略，自动生成的键值在 insert 方法执行完后可以被设置到传入的参数对象中。

```java

```
> 入参Map

```xml

```

> 主键UUID

```java

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

```

> 使用占位符${ }

```java

```

> xml中拼接

```java

```

# 动态（X）

> 执行原理： 根据表达式的值完成逻辑判断并动态拼接sql的功能。

## where

- 去掉where标签体的第一个and
- 标签体里有内容则生成关键字 where，没有则不生成。`但对于and写在条件之后的也会出问题：name like #{name} and`

```java

```

## set

- 去掉set标签体的最后一个逗号
- 标签体里有内容则生成关键字 set，没有不生成

```java

```

## choose

- 只要一个分支满足，其他都不执行。相当于 if-elseif-else

```java

```

## foreach

> 遍历查询

```java

```

> 批量新增

```java

```

#分页查询





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




# ---mapper---

# 基础配置

> 什么是通用Mapper？

```
通用Mapper就是为了解决单表增删改查，基于Mybatis的插件。
开发人员不需要编写SQL，不需要在DAO中增加方法，只要写好实体类，就能支持相应的增删改查方法。
```

> 基础配置

```xml
<!-- https://mvnrepository.com/artifact/tk.mybatis/mapper-spring-boot-starter -->
<dependency>
    <groupId>tk.mybatis</groupId>
    <artifactId>mapper-spring-boot-starter</artifactId>
    <version>2.1.5</version>
</dependency>
```

```properties
#mapper
mapper.mappers=com.example.friend.base.IBaseMapper
mapper.not-empty=true
mapper.identity=MYSQL

#mybatis的sql打印
logging.level.com.example.friend.mapper=debug

#database
spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://192.168.8.7:33306/demo_friend?useSSL=false&allowMultiQueries=true&serverTimezone=GMT%2B8
spring.datasource.username=bluecardsoft
spring.datasource.password=#$%_BC13439677375
```

```java
@MapperScan("com.example.friend.mapper") //tk.mybatis.spring.annotation.MapperScan，启动类
```

> 实体类和接口

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "table_emp")
public class Employee {
    @Id //主键id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //自增
    private Integer empId;

    @Column(name = "emp_name") //指定数据表中的列名
    private String empName;
    private Double empSalary;
    private Integer empAge;
}
```

```java
public interface EmployeeMapper extends Mapper<Employee>, MySqlMapper<Employee> {}
```

#常用方法

> 测试方法

```java
//xxxByPrimaryKey()：需要使用 @Id 注解标明主键，否则，通用 Mapper 会将所有实体类字段作为联合主键。

public void selectByPrimaryKey() { //主键查询
    Employee employee = employeeMapper.selectByPrimaryKey(5);
}
```

```java
//xxxSelective：非主键字段如果为 null 值，则不加入到 SQL 语句中。
UPDATE table_emp SET emp_salary = ? WHERE emp_id = ?

public void updateByPrimaryKeySelective() {
    Employee employee = new Employee(7, null, 2222.22, null);    
    int update = employeeMapper.updateByPrimaryKeySelective(employee);
}
```

```java
//自增主键直接赋值到 employee
INSERT INTO table_emp ( emp_id,emp_name,emp_salary,emp_age ) VALUES( ?,?,?,? )
SELECT LAST_INSERT_ID()

public void insert() { 
    Employee employee = new Employee(null, "xiao3", 5555.55, 20);
    int insert = employeeMapper.insert(employee);
}
```

```java
//使用非空的值生成 WHERE 子句，在条件表达式中使用 "=" 进行比较

public void selectOne() {
    Employee emp = new Employee(null, "bob", null, null);
    Employee employee = employeeMapper.selectOne(emp);
}
```

```java
//同上。使用非空的值生成 WHERE 子句。特别注意，当参数为 null 或者 参数字段都为 null 时，将生成可怕的sql
DELETE FROM table_emp

public void delete() {
    Employee employee = null;
    int delete = employeeMapper.delete(employee);
}
```

