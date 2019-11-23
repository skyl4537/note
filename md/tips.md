





# mybatis

## 手册

> 开发手册

```sh
#【参考】 各层命名规约
get，list，count（统计值），insert/save（推荐），delete/remove（推荐），update

#【强制】 xml 配置中参数注意使用：#{}，不要使用${}，此种方式容易出现 SQL 注入。

#【强制】 在表查询中，一律不要使用 * 作为查询的字段列表，需要哪些字段必须明确写明。
-- 说明：（1）增加查询分析器解析成本。（2）增减字段容易与 resultMap 配置不一致。（3）无用字段增加网络消耗，尤其是 text 类型的字段。

#【强制】 POJO类 boolean 属性不能加 is，而数据库字段必须加 is_，要求在 resultMap 中进行字段与属性的映射。
-- 说明：参见定义POJO类以及数据库字段定义规定，在 sql.xml 增加映射，是必须的。

#【推荐】 不要写一个大而全的数据更新接口，传入为 POJO 类，不管是不是自己的目标更新字段，
-- 都进行 update table set c1=value1, c2=value2, c3=value3; 这是不对的。
-- 执行 SQL时，尽量不要更新无改动的字段。（1）易出错； （2）效率低； （3）binlog 增加存储。
```

```sh
#【强制】 不允许直接拿 HashMap 与 Hashtable 作为查询结果集的输出。
-- HashMap 会置入字段名和属性值，但是值的类型不可控。

#【强制】 不要用 resultClass 当返回参数，即使所有类属性名与数据库字段一一对应，也需要定义；反过来，每一个表也必然有一个与之对应。
-- 说明：配置映射关系，使字段与 DO 类解耦，方便维护。

#【强制】 mybatis 自带的 queryForList(String statementName, int start, int size) 不推荐使用。
-- 说明：此方法属于'逻辑分页'，会先查询所有记录，然后再代码中进行分页

#'【参考】 @Transactional 事务不要滥用。事务会影响数据库的 QPS。
-- 另外使用事务的地方需要考虑各方面的回滚方案，包括缓存回滚、搜索引擎回滚、消息补偿、统计修正等。
```

## 基础

> 池化技术：减少系统消耗，提升系统性能

```sh
#连接池就是用于存储连接的集合。集合必须是线程安全的，不能两个线程拿到同一个连接。集合还必须实现队列的特征：先进先出
限定数据库连接的个数，不会造成由于数据库连接过多而导致系统运行缓慢或崩溃。  `连接池`
数据库连接不需要每次都去创建或销毁，节约了资源。数据库连接用完之后，归还给连接池，并将其状态变成'Idle'，并不是销毁连接。
数据库连接不需要每次都去创建，响应时间更快。
```

```sh
限定线程的个数，不会造成由于线程过多导致系统运行缓慢或崩溃。              `线程池`
线程池不需要每次都去创建或销毁，节约了资源。
线程池不需要每次都去创建，响应时间更快。
```

>ORM

```sh
#什么是 ORM？
ORM（Object-Relational-Mapping）：对象-关系映射。Object 是指java语言中的对象，Relational 是指关系型数据库。
ORM 就是把数据库表和实体类及实体类的属性对应起来，用操作实体类的方式来完成数据库操作的一种技术。

#为什么使用 ORM?
当实现一个应用程序时，可能会写特别多数据访问层的代码，增删查改，而这些代码都是重复的。使用ORM则会大大减少重复性代码。

#ORM 优缺点
优点：(1).面向对象编程，易于理解，不必编写sql。(2).可能很好的做到数据库无关性。(3).只适用于业务逻辑简单的场景。
缺点：(1).自动生成sql，无法调节其性能。(2).对于复杂场景，ORM无法处理。
```

```sh
#举例说明，使用原生 jdbc 查询，需要编写sql
res = db.excSql("SELECT name,age FROM person WHERE id=10;");
name = res[0]["name"];

#使用 ORM 思想操作，则代码编写为：
person = PersonDao.getById(10);
name = person.getName();

#二者相比较，ORM使用面向对象编程，与数据对象直接交互，不用编写sql，封装了底层数据访问的细节。
数据库的表（table） --> 类（class）
记录（record）     --> 对象（object）
字段（field）      --> 对象的属性（attribute）
```

>JDBC

```sh
（1）建立连接 （2）创建Statement对象 （3）组装sql及参数 （4）执行sql （5）读取结果集并解析 （6）关闭连接，释放资源
```

```java
public static void main(String[] args) throws Exception {
    Class.forName("com.mysql.jdbc.Driver");
    Connection conn = DriverManager.getConnection(url, username, password);              //（1）
    Statement st = conn.createStatement();                                               //（2）
    ResultSet rs = st.executeQuery("select id,name,password,email,birthday from users"); //（3-4）
    while(rs.next()){
        System.out.println("name=" + rs.getObject("name"));                              //（5）
    }
    rs.close();
    st.close();
    conn.close();                                                                         //（6）
}
```

>JPA

```sh
#JPA 全称 Java-Persistence-API，即Java持久化API。JPA规范本质上就是一种ORM规范，注意不是ORM框架。
因为JPA并未提供ORM实现，它只是制订了一些规范，提供了一些编程的API接口，但具体实现则由服务厂商来提供实现。

#JPA & Hibernate
JPA 和 Hibernate 的关系就像JDBC和JDBC驱动的关系，JPA是规范，Hibernate 除了作为ORM框架之外，它也是一种JPA实现。

#JPA 能否取代 Hibernate？ JDBC规范可以驱动底层数据库吗？
答案是否定的，也就是说，如果使用JPA规范进行数据库操作，底层需要 Hibernate 作为其实现类完成数据持久化工作。
```

>mybatis

```sh
'半自动的 ORM 框架'，底层是对JDBC的封装，开发者只需要关注 sql 本身，而不需要去处理Jdbc繁杂的过程代码。
通过xml或注解的方式将 mapper接口和参数 映射生成 最终执行的sql语句，由框架调用执行，并将结果映射成 javabean 对象返回。
```

```sh
#优点
- 与 jdbc 相比，消除了 jdbc 大量冗余的代码，不需要手动开关连接
- sql 写在 xml 里，解除 sql 与程序代码的耦合，便于统一管理
- 能够与 Spring 很好的集成
- 提供映射标签，支持对象与数据库的字段关系映射；提供对象关系映射标签，支持对象关系组件维护。
```

```sh
#缺点
- sql 语句的编写工作量较大，尤其当字段多、关联表多时，对开发人员编写SQL语句的功底有一定要求。
- sql 语句依赖于数据库，导致数据库移植性差，不能随意更换数据库（数据库无关性差）。
```

>Hibernate

```sh
- mybatis 和 hibernate 不同，它是一个半自动 ORM 框架，需要程序员自己编写 sql 语句。
- mybatis 直接编写原生态sql，可以严格控制sql执行性能，灵活度高。
- Hibernate 对象/关系映射能力强，能够做到数据库无关性。mybatis 如果要实现数据无关性，则需要编写多套sql映射文件，工作量大。

#Hibernate： 全自动。自动生成sql，不能控制sql性能；但能做到数据库无关性。
#mybatis  ： 半自动。手写sql，可以优化sql性能；不能做到数据库无关性（数据库切换就得重新写sql）
```

>总结

```sh
- JDBC     ：sql包含在代码中，硬编码，高耦合，不易维护。还需要编写大量的冗余代码。
- mybatis  ：半自动化ORM。sql和java编码分开，一个专注数据，一个专注业务，低耦合。
- Hibernate：全自动ORM。自动产生sql，但不灵活。
```

## 原理

>运行原理

```java
@Test
public void mybatisTest() throws IOException {
    InputStream is = Resources.getResourceAsStream("mybatis.xml");
    SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is); //（1）

    SqlSession session = factory.openSession();                           //（2）

    EmpMapper empMapper = session.getMapper(EmpMapper.class);             //（3）
    //根据多态原则，调用接口的 selectList()，其实际调用的是：实现类的 selectList()
    List<Emp> emps = empMapper.selectList(null);
    session.commit();
    session.close();
}
```

```sh
mybatis 开始运行时，通过 'Resources' 读取配置文件，使用 'XMLConfigBuilder' 进行解析，并把解析结果存放在 'Configuration' 中，
然后通过 'SqlSessionFactoryBuilder' 对象的 build() 方法，以 Configuration 作为参数构建一个 'SqlSessionFactory' 对象。

Configuration：保存 mybatis 的全部xml配置信息，包括mapper.xml。
<parameterMap>标签会被解析为 ParameterMap 对象，其每个子元素会被解析为 ParameterMapping 对象。
<resultMap>标签会被解析为 ResultMap 对象，其每个子元素会被解析为 ResultMapping 对象。
每一个<select>、<insert>、<update>、<delete>标签均会被解析为 MappedStatement 对象，标签内的sql会被解析为 BoundSql 对象。
```

```sh
通过 SqlSessionFactory 的 openSession() 方法获取到 'SqlSession' 对象。
执行过程中会读取 Configuration 中的数据源信息，创建一个执行器对象 'Executor'。

#SqlSession：（1）创建接口的代理对象。（2）定义通用的增删改查方法
#Executor  ：真正地实现了与数据库的交互。负责执行SQL语句，并且封装结果集
```

```sh
使用'jdk动态代理技术'动态生成一个接口 EmpMapper 的实现类，
根据 mapper 接口的'全限定名'（全类名）从 Configuration 中查找对应的 sql 语句，
最后，使用 Executor 对象从数据库中查找数据，并封装范围结果到 javabean。
```

>常用API和方法

```sh
（1）SqlSessionFactoryBuilder：读取mybatis的配置文件，构建 SqlSessionFactory 对象
```

```sh
（2）SqlSessionFactory：负责创建 SqlSession 对象。
-- 特点：一旦创建就会存在应用程序的整个运行生命周期（需要做单例）。作用域：一个应用的生命周期

'sqlSessionFactory.openSession();执行结果'
1、将会启用一个事务作用域（即不会主动提交，需要手动提交）
2、将从正在生效的运行环境所配置的数据中获取一个连接对象
3、事务隔离级别是由驱动或数据源使用的默认级别
4、PreparedStatements 不会被重用，也不会进行批量更新
```

```sh
（3）SqlSession ---> 包含了所有的执行数据库sql语句，事务操作，获取mapper方法
-- 特点：每个线程都有一个SqlSession实例，sqlSession不是共享的，也不是线程安全的
-- 作用域：是Request 或 method。在一次会话结束时需要将sqlSession关闭

'SqlSession主要功能'
执行SQL语句、提交或回滚事务、sqlSession关闭、mapper接口映射、缓存操作

'执行sql语句'
传入待执行的sql及参数，获取结果，并将结果映射为javabean对象
<T> T selectOne(String statement, Object parameter);

'mapper接口映射'
通过使用mapper接口执行mapper文件中的映射语句
mapper中方法名称，参数名称，参数数量与配置文件xml中方法名称，参数名称，参数数量相互对应
mapper中也可以使用 RowsBounds 来限制查询结果，逻辑分页，不推荐
```





## 问题





