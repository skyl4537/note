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

> 

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



