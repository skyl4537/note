[TOC]

#mybatis

## boot整合

>必要配置

```xml

```

```properties

```

```java

```

> 非必要配置

```properties

```

> xml版本

```xml

```

> 注解版

```java

```

> 资源拷贝插件

```xml

```

## 基本语法

> 参数相关

```sh
多个入参：使用注解 @Param("id")，或者封装 Map/pojo

一行回参：返回多列，'推荐'封装 pojo 接收，不推荐直接使用 Map
多行回参：接口返回值定义'List<Pojo>'，但xml中的'resultType=pojo'，因为mybatis是对jdbc的封装，一行一行读取数据
```

> 回参Map：`有坑`

```shell
{"喇叭花":{"flowerName":"喇叭花","flowerId":1},"牵牛花":{"flowerName":"牵牛花","flowerId":2}...}
```

```java
//‘flowerName’表示java属性名，而非数据库字段名。所以，xml中的 flower_name 必须改别名。切记！
//key 不能重复，重复就覆盖。也就是说，对于多个 key 的flower，只能返回最后一条
@MapKey("flowerName")
Map<String, Flower> listByName(@Param("flowerName") String flowerName);
```

```xml
<select id="listByName" resultType="com.x.Flower">
    select flower_id,flower_name flowerName from flower where flower_name like #{flowerName}
</select>
```

> 抽取引用：`<sql/> + <include/>`

```xml
<sql id="ref"> id,name,age,address,companyId </sql>
```

```xml
<select id="selById" resultType="com.heiketu.pojo.Users">
    select
    <include refid="ref" /> <!-- 引用 -->
    from usrs where id = #{id}
</select>
```

> 字段映射：当数据库的字段名与对象的属性名不一致时，`2种解决方案`

```sql
-- 使用 sql 的别名语法
select order_id id, order_price price, order_no orderNo from orders where order_id=#{id};
```

```xml
<!-- 使用 mybatis 的 resultMap 标签 -->
<select id="getOrder" resultMap="orderMap"> <!-- resultMap 取代 resultType -->
    select * from orders where order_id=#{id}
</select>
```

```xml
<resultMap type="com.x.order" id="orderMap">
    <id property="id" column="order_id"/> <!-- id-主键，result-非主键 -->
    <result property="price" column="order_price"/>
</reslutMap>
```

> 模糊查询

```sql
-- java代码中拼接参数：%张%
SELECT * FROM user WHERE name LIKE #{name}
```

```sql
-- 使用sql函数 concat()
SELECT * FROM user WHERE name LIKE concat('%', #{username}, '%') -- 张
```

```xml
<!-- xml 中使用mybatis的 bind 标签-->
<select id="selectLike">
    <bind name="name" value="'%'+_parameter+'%'" />
    select * from user where name like #{name} <!-- 张 -->
</select>
```

> 存储过程

```xml
<!-- 使用标签 select && statementType="CALLABLE" -->
<select id="getStudentByTeacherId" statementType="CALLABLE" resultType="com.example.mybatis.po.Student">
    {CALL get_student_by_teacher_id(#{teacherId})}
</select>
```

##分页查询

> `插件版`：在插件的拦截方法内拦截待执行的sql，然后重写sql，添加对应的物理分页语句和分页参数

```xml
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper-spring-boot-starter</artifactId>
    <version>1.2.5</version>
    <exclusions> <!-- 整合 mybatis-plus,排斥依赖,避免报错 -->
        <exclusion>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
        </exclusion>
        <exclusion>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis-spring</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

```properties
#分页合理化参数，默认false。设为 true 时，pageNum <= 0 时会查询第一页，pageNum > pages（超过总数时），会查询最后一页
pagehelper.reasonable=true
pagehelper.helper-dialect=MYSQL
pagehelper.support-methods-arguments=true
pagehelper.params=count=countSql
```

```java
@GetMapping("/person/{pageNum}/{pageSize}")
public PageInfo<Person> listByPage(@PathVariable int pageNum, @PathVariable int pageSize) {
    PageHelper.startPage(pageNum, pageSize); //查询之前设置：页码数，页容量
    List<Person> list = service.listAll();
    PageInfo<Person> pageInfo = new PageInfo<>(res, 5); //arg2为连续显示的页码个数
    return pageInfo;
}
```

> `非插件版`：xml占位符 ? 不允许在关键字前后进行数学运算，所以需要在代码中完成计算，然后再传递到 xml 中

```java
int page = 2, size = 2;
Map<String, Object> map = new HashMap<>();
map.put("pageSize", size); //页容量
map.put("pageStart", size * (page - 1)); //起始行
List<People> list = service.listPage(map);
```

```xml
<select id="listPage" resultType="com.x.People">
    select * from people limit #{pageStart}, #{pageSize}
</select>
```

> PageHelper 中默认 PageInfo 的成员变量

```java
public class PageInfo<T> extends PageSerializable<T> {
    private int pageNum;  //当前页
    private int pageSize; //每页的数量
    private int size;     //当前页的数量

    protected long    total; //总记录数
    protected List<T> list;  //结果集

    //由于startRow和endRow不常用，这里说个具体的用法
    //可以在页面中"显示startRow到endRow 共size条数据"

    private int startRow; //当前页面第一个元素在数据库中的行号
    private int endRow;   //当前页面最后一个元素在数据库中的行号

    private int pages;    //总页数
    private int prePage;  //前一页
    private int nextPage; //下一页

    private boolean isFirstPage = false;     //是否为第一页
    private boolean isLastPage = false;      //是否为最后一页
    private boolean hasPreviousPage = false; //是否有前一页
    private boolean hasNextPage = false;     //是否有下一页

    private int navigatePages;      //导航页码数
    private int[] navigatepageNums; //所有导航页号
    private int navigateFirstPage;  //导航条上的第一页
    private int navigateLastPage;   //导航条上的最后一页
}
```

# 关联查询

## 联表查询对象

>查询学生时，把老师信息也查出

```java
class Student{
    private Integer id;
    private String name;

    private Teacher teacher; 
}
```

```java
List<Student> listAllStudent();
```

> `方法1`：列别名 + 自动映射（当列别名和 pojo 属性一致时，mybatis自动映射赋值）

```xml
<!-- 在SQL中'.'是关键字符，所以在两侧添加反单引号 `teacher.id`-->
<select id="listAllStudent" resultType="com.x.pojo.Student">
    SELECT s.id id, s.name name, t.id `teacher.id`, t.name `teacher.name`
    FROM student s LEFT JOIN teacher t
    ON t.id=s.tid LIMIT 5
</select>
```

> `方法2`：列别名 + 手动映射（resultMap）

```xml
<!--左外连接(left outer join)：可查询所有的学生信息-->
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

> `方法3`：关联查询`（association：一对一关联查询）`

```xml
<select id="listAllStudent" resultMap="studentMap">
    SELECT s.id sid, s.name sname, t.id tid, t.name tname
    FROM student s LEFT OUTER JOIN teacher t
    ON s.tid=t.id LIMIT 5
</select>

<resultMap type="com.example.mybatis.po.Student" id="studentMap">
    <id column="sid" property="id" />
    <result column="sname" property="name" />

    <!--association 等同于 reslutMap-->
    <!--property：Student类中的属性名； javaType：装配后返回的java类型-->
    <association property="teacher" javaType="com.example.mybatis.po.Teacher">
        <id column="tid" property="id" />
        <result column="tname" property="name" />
    </association>
</resultMap>
```

> `方法4`：多次查询

```xml
<select id="listAllStudent" resultMap="stuMap">
    select id,name,tid from student
</select>

<!--student表的其他列，使用 AutoMapping 自动装配！但对于二次查询的参数tid，必须显示装配一次-->
<!--<result column="tid" property="tid"/>-->

<!--association   -> 一对一关联查询-->
<!--property      -> Student类中的属性名-->
<!--fetchType     -> 是否懒加载：lazy-懒加载，eager-立即加载-->
<!--select        -> 通过哪个查询可以查出这个对象的信息-->
<!--column        -> 把当前表哪个列的值作为参数传递给select-->
<!--对于分步查询传参多列,可以使用形式 column="{key1=id, key2=name}"; mybatis底层将参数封装成map-->
<resultMap type="com.example.mybatis.po.Student" id="stuMap">
    <association property="teacher" fetchType="lazy" column="tid"
                 select="com.example.mybatis.mapper.TeacherMapper.selById"/>
</resultMap>
```

```xml
<!--该方法定义在 TeacherMapper-->
<select id="selById" resultType="com.example.mybatis.po.Teacher">
    SELECT id, name FROM teacher WHERE id=#{id}
</select>
```

##联表查询集合

>查询老师时，把所有学生信息查出

```java
class Teacher{
    private Integer id;
    private String name;

    private List<Student> studentList; 
}
```

```java
List<Teacher> listAllTeacher();
```

>`方法3`：关联查询 `（collection：一对多关联，association：一对一关联）`

```xml
<select id="listAllTeacher" resultMap="teacherMap">
    SELECT t.id tid, t.name tname, s.id sid, s.name sname
    FROM teacher t LEFT JOIN student s
    ON t.id=s.tid LIMIT 10
</select>

<resultMap type="com.example.spring.bean.Teacher" id="teacherMap">
    <id column="tid" property="id"/>
    <result column="tname" property="name"/>

    <!--collection -> 当property是集合类型时使用-->
    <!--ofType     -> 集合的泛型是哪个类-->
    <collection property="studentList" ofType="com.example.spring.bean.Student">
        <id column="sid" property="id"/>
        <result column="sname" property="name"/>
    </collection>
</resultMap>
```

> `方法4`：多次查询

```xml
<select id="listAllTeacher" resultMap="teacherMap">
    SELECT id, name FROM teacher
</select>

<resultMap type="com.example.spring.bean.Teacher" id="teacherMap">
    <id column="id" property="id"/>
    <result column="name" property="name"/>

    <collection property="studentList" column="id"
                select="com.example.spring.mapper.StudentMapper.selByTid"/>
</resultMap>
```

```xml
<!--此方法定义在 StudentMapper.xml-->
<select id="selByTid" resultType="com.example.spring.bean.Student">
    SELECT id, name FROM student WHERE tid=#{tid}
</select>
```

# 其他用法

## xml提示

> eclipse的xml提示

```shell
下载dtd文件: "mybatis-3-mapper.dtd"和"mybatis-3-config.dtd"
打开 eclipse -> windows -> preferences -> xml -> xmlcatalog -> add,依次填写:

#mapper.xml 的代码提示
location    -> 选择file_system,找到"mybatis-3-mapper.dtd"存放的本地路径
key_type    -> URI0
key         -> http://mybatis.org/dtd/mybatis-3-mapper.dtd

#mybatis-config.xml 的代码提示
location    -> 选择file_system,找到"mybatis-3-config.dtd"存放的本地路径
key_type    -> URI0
key         -> http://mybatis.org/dtd/mybatis-3-config.dtd
```

##基本使用

> 内置参数：`_parameter ` 和 `_databaseId`

```shell
对于单个入参，_paramerer 指代的是这个入参
对于多个入参，_parameter 指代的是 mybatis 封装的 map（mybatis 将多个参数封装成一个 map）。

对于配置了 databaseIdProvider，_databaseId 指代的是当前数据库的别名（mysql，oracle，sql-server...）
```

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

> 参数的别名

```shell
int _int; boolean _boolean #一般数据类型，加前缀 _
Integer int; String string; Map map; List list #一般类型的包装类，类型小写
```

```shell
@Alias("author") #对于自定义类型，可使用注解：单独类起别名

mybatis.type-aliases-package=com.x.x.entity #也可以配置某个包下所有类的别名：批量起别名（类名小写）
```

> 主键自增

```xml
<!-- 入参为 javabean，数据库采用自增长策略，则新增返回的 id 被设置到传入的参数对象中 -->
<!-- 是否("useGeneratedKeys")将产生的主键赋值到属性("keyProperty")中 -->
<insert id="addOne" useGeneratedKeys="true" keyProperty="id">
    INSERT INTO person VALUES(default, #{age}, #{name})
</insert>
```

```xml
<!-- 入参为Map，数据库采用自增长策略，则使用 selectKey 标签 -->
<insert id="addOne"> <!--order: 相对于insert的执行顺序.(BEFORE|AFTER)-->
    <selectKey keyProperty="id" resultType="int" order="AFTER">
        SELECT LAST_INSERT_ID()
    </selectKey>
    INSERT INTO person (age,name) VALUES(#{age},#{name})
</insert>
```

```xml
<!-- 对于主键采用 UUID 策略 -->
<insert id="addOne">
    <selectKey keyProperty="id" resultType="String" order="BEFORE">
        SELECT uuid()
    </selectKey>
    INSERT INTO    person (id,age,name) VALUES(#{id},#{age},#{name})
</insert>
```

##动态查询

>`不推荐使用`：分支复杂，难以维护。大量空格，增加网络传输。

> where：去掉where标签体的第一个and，标签体里有内容则生成关键字 where，没有则不生成 。

```xml
<!-- 对于and写在条件之后的也会出问题：name like #{name} and-->

<select id="listByIf" resultType="com.x.bean.Flower">
    select * from flower <!--where true（替换）-->
    <where> 
        <if test="null!=name and ''!=name">
            and name like #{name}
        </if>
        <!--@class@method(args)调用类的静态方法 - 详见附表-->
        <if test="@org.apache.commons.lang3.math.NumberUtils@isParsable(price)">
            and price > #{price} <!--检验是否为数字??? 是则增加为查询条件，否则不增加-->
        </if>
    </where>
</select>
```

> set：去掉set标签体的最后一个逗号，标签体里有内容则生成关键字 set，没有不生成。

```xml
<update id="updateByIf">
    update flower
    <set>
        id=#{id}, <!--防止<set/>内容为空，不生成 set 关键字 -->

        <if test="name!=null and name!=''"> <!--满足条件，则追加到更新条件-->
            name=#{name},
        </if>
        <if test="price>=0">
            price=#{price},
        </if>
    </set>
    where id=#{id}
</update>
```

> choose：只要一个分支满足，其他都不执行。相当于 if - elseif - else

```xml
<select id="listByChoose" resultType="com.x.bean.Flower">
    select * from flower
    <where>
        <choose>
            <when test="name!=null and name!=''">
                name like #{name} <!-- if - 先判断,满足即结束，不再去判断elseif-->
            </when>
            <when test="@org.apache.commons.lang3.math.NumberUtils@isParsable(price)">
                price > #{price} //elseif
            </when>
            <otherwise>
                production like #{production} <!--else-->
            </otherwise>
        </choose>
    </where>
</select>
```

> foreach：遍历

```java
//遍历查询
List<Passage> listByForeach(List<int> passageIdList);
```

```xml
<!--collection  ->  要遍历的集合；对于list类型会封装到一个特殊的map中，其key就为list-->
<!--item        ->  集合中的每一个对象-->
<!--separator   ->  每个元素之间的分隔符-->
<!--index       ->  索引。遍历list，index表示索引，item为对应的值；遍历map，index为key，item为map值-->
<!--open-close  ->  遍历所有结果拼接一个开始（结束）的字符-->
<select id="listByForeach" resultType="com.x.bean.Passage">
    select * from Passage where id in
    <foreach collection="list" item="item" index="index" open="(" close=")" separator=",">
        #{item} <!--(1,2,5,7)-->
    </foreach>
</select>
```

```java
//遍历新增：mysql特有的批量插入 <--> insert into t_customer (id, c_name) values (?,?),(?,?)....
boolean saveBatch(List<Flower> list);
```

```xml
<insert id="saveBatch">
    insert into flower (name, price) values
    <foreach collection="list" item="item" index="index" separator=",">
        (#{item.name}, #{item.price})
    </foreach>
</insert>
```

## 缓存相关

> 一级缓存：作用域为一个 `SqlSession`，默认开启。

```shell
myBatis会在一次会话，一个SqlSession对象中创建一个本地缓存(local cache)，
对于每一次查询，都会尝试根据查询的条件去本地缓存中查找是否在缓存中，
如果在缓存中，就直接从缓存中取出，然后返回给用户；否则，从数据库读取数据，将查询结果存入缓存并返回给用户。

mybatis内部存储缓存使用HashMap，key为 'hashCode + sqlId + Sql' 语句。value为从查询出来映射生成的java对象。
```

> 二级缓存：作用域为一个 `NameSpace`，默认关闭。

```shell
同一个 NameSpace 中，查询sql可以从缓存中获取数据。
```

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

```shell
#参数说明
eviction       -> 缓存的回收策略，默认LRU（可选值：LRU，FIFO，SOFT，WEAK）
flushInterval  -> 缓存失效时间，默认永不失效（单位毫秒）
size           -> 缓存存放多少个元素，默认1024
type           -> 指定自定义缓存的全类名（需要实现Cache接口，自定义缓存类）
readOnly       -> 是否只读，默认false
```

> 总结

```sh
- myBatis 的二级缓存相对于一级缓存来说，实现了 SqlSession 之间缓存数据的共享，同时粒度更加的细，能够到 namespace 级别，
  通过 Cache 接口实现类不同的组合，对Cache的可控性也更强。
- myBatis 在多表查询时，极大可能会出现脏数据，有设计上的缺陷，安全使用二级缓存的条件比较苛刻。
- 在分布式环境下，由于默认的 MyBatis Cache 实现都是基于本地的，分布式环境下必然会出现读取到脏数据，
  需要使用集中式缓存将 MyBatis 的 Cache 接口实现，有一定的开发成本，直接使用 Redis、Memcached 等分布式缓存可能成本更低，安全性也更高。
```

## 底层原理

> 过滤器优化

```java
@WebFilter("/*")
public class OpenSessionInView implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) {
        SqlSession sqlSession = MyBatisUtil.getSqlSession();
        try {
            chain.doFilter(req, resp);
            sqlSession.commit();           //提交
        } catch (Exception e) {
            sqlSession.rollback();         //回滚
        } finally {
            MyBatisUtil.closeSqlSession(); //关闭
        }
    }
}
```

```java
public class MyBatisUtil {
    private static SqlSessionFactory factory;
    //过滤器 和 DAO 都同属于同一个线程，可使用 ThreadLocal 存储线程变量
    private static ThreadLocal<SqlSession> tl = new ThreadLocal();

    //静态代码块：factory实例化的过程是一个比较耗费性能的过程。保证有且只有一个factory
    static {
        InputStream is = Resources.getResourceAsStream("mybatis.xml");
        factory = new SqlSessionFactoryBuilder().build(is);
    }

    //获取 SqlSession ---> ThreadLocal的经典案例
    public static SqlSession getSqlSession() {
        SqlSession sqlSession = tl.get();
        if (null == sqlSession) {
            sqlSession = factory.openSession();
            tl.set(sqlSession);
        }
        return sqlSession;
    }

    public static void closeSqlSession() {
        SqlSession sqlSession = tl.get();
        if (null != sqlSession) {
            sqlSession.close();
            tl.set(null);
        }
    }
}
```

```java
//简化后的测试方法
public void test() throws IOException {
    EmpMapper empMapper = session.getMapper(EmpMapper.class);
    List<Emp> emps = empMapper.selectList(null);
    emps.forEach(System.out::println);
}
```



> 

```java

```

```java

```

```java

```

```java

```

```java

```

# JPA

##基础概念

>基础概念

```shell
JPA                #Java-Persistence-API，对持久层操作的标准（接口 + 文档）

Hibernate          #全自动化的ORM框架
Hibernate JPA      #实现了 JPA 标准的 Hibernate（Hibernate-3.2+）

Spring Data        #用于简化数据库（SQL，NoSQL...）访问，并支持云服务的开源框架
Spring Data JPA    #Spring Data的一个子模块，实现了 JPA 标准的 Spring Data，底层是 Hibernate

Spring Data Redis  #通过简单配置，实现对reids各种操作，异常处理及序列化，支持发布订阅
```

```shell
#一些缺点
屏蔽了SQL的优雅，发明了一种自己的查询方式。这种查询方式并不能够覆盖所有的SQL场景
增加了代码的复杂度，需要花更多的时间来理解DAO
DAO操作变的特别的分散，分散到多个java文件中，或者注解中（虽然也支持XML）。如果进行一些扫描，或者优化，重构成本大
不支持复杂的SQL，DBA流程不好切入
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
spring.jpa.generate-ddl=true

#可选参数-create: 每次启动都会删除旧表，新建一个空表
#可选参数-update: 根据实体类创建/更新数据库表
#spring.jpa.hibernate.ddl-auto=update
```

> 实体类

```java
@Entity //表明是一个JPA实体
@Table(name = "t_emp") //默认表名为类名小写

@DynamicInsert
@DynamicUpdate //只更新修改过的字段

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id //主键标识，对于复合主键，两个注解@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //自增策略
    private Integer id;

    @Column(name = "first_name") //显示指定列名，默认将驼峰转下划线
    private String firstName;

    @Column //默认列名 -> gender_flag
    private Boolean genderFlag;
}
```

## 两种查询

> 基于方法名称的查询：驼峰命名规则，findBy（关键字） + 属性名称（首字母大写） + 查询条件（首字母大写，Like，OrderBy...）

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

> 事务相关

```shell
业务逻辑层 'Service' 调用多个 Repository 方法时，需要在 Service 方法上声明事务 '@Transactional'。
```

##常用接口

> `Repository`：JPA 顶层接口，标识接口，空接口。`常用接口：JpaRepository`

> `CrudRepository`：最基础的CRUD，extends Repository

```java
@Test
public void daoCrud() {
    //save(): 先查询数据表中是否存在该id数据??? 无则新增; 有则更新
    Employee save = employeeCrudDao.save(new Employee(7, "张三", true));
    
    Iterable<Employee> all = employeeCrudDao.findAll();
}
```

> `PagingAndSortingRepository`：分页和排序功能，extends CrudRepository

```java
@Test
public void daoPS() {
    Sort sort = Sort.by(Sort.Direction.DESC, "firstName", "id"); //(1).排序
    Iterable<Employee> all = employeePSDao.findAll(sort);

    Pageable pageable = PageRequest.of(0, 2); //(2).页码从【0】开始; 分页
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

>`JpaSpecificationExecutor`：提供多条件查询，分页，排序，独立于以上接口存在，所以`需配合以上接口使用`

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

# 问题

##高级概念


> Dao接口的工作原理

```sh
Dao接口，即mapper接口。接口的全限名，就是映射文件中的namespace值。接口的方法名，就是映射文件中 MappedStatement 的id值。
接口方法内的参数，就是传递给sql的参数。

Dao 接口没有实现类，当调用接口方法时，通过'接口全限定名+方法名' 拼接字符串作为key值，唯一定位一个MappedStatement。
在mybatis中，每一个 select、insert、update、delete 标签，都会被解析为一个 MapperStatement 对象。

mapper 接口的工作原理是'JDK动态代理'，mybatis运行时会使用JDK动态代理为Mapper接口生成代理对象proxy，
代理对象会拦截接口方法，转而执行 MapperStatement 所代表的sql，然后将sql执行结果返回。

#dao 接口里的方法'不能重载'！ 因为 全限定名+方法名 保存和寻找策略（接口可以重载，但是 xml 中不能多个相同id）。
```

> mybatis的Xml映射文件中，不同的Xml映射文件，id是否可以重复？

```sh
不同的Xml映射文件，如果配置了namespace，那么id可以重复；如果没有配置 namespace，那么id不能重复；
毕竟'namespace不是必须的'，只是最佳实践而已。

原因就是'namespace+id'是作为 Map<String, MappedStatement> 的key使用的，如果没有namespace，就剩下id。
那么，id重复会导致数据互相覆盖。有了namespace，自然id就可以重复，namespace不同，namespace+id自然也就不同。
```

> mybatis是如何将sql执行结果封装为目标对象并返回的？都有哪些映射形式？

```sh
- 使用 resultMap 标签，逐一定义数据库列名和对象属性名之间的映射关系。
- 使用 sql 列的别名功能，将列的别名书写为对象属性名。
有了列名与属性名的映射关系后，mybatis通过反射创建对象，同时使用反射给对象的属性逐一赋值并返回，那些找不到映射关系的属性，是无法完成赋值的。
```

> sqlSession对象是否线程安全？

```sh
SqlSession 实例'非线程安全'，不能被共享。每次用完必须关闭。
#增删改没有 resultType 属性，返回值表示受影响的行数，类型可以是：boolean，int，long，void。
```

> parameterType & resultType & resultMap

```sh
parameterType： 指定输入参数类型，mybatis通过 OGNL 从输入对象中获取参数值拼接在sql中。
resultType   ： 指定输出结果类型，mybatis将sql查询结果的一行记录数据映射为resultType指定类型的对象。
resultMap    ： 字段和java对象中属性不一致时使用。常用作复杂的映射配置（多表查询）.
```

> 设计模式

```sh
- Builder模式，例如SqlSessionFactoryBuilder、XMLConfigBuilder、XMLMapperBuilder；
- 工厂模式，例如SqlSessionFactory、ObjectFactory、MapperProxyFactory；
- 单例模式，例如ErrorContext和LogFactory；
- 代理模式，Mybatis实现的核心，比如MapperProxy、ConnectionLogger，用的jdk的动态代理；还有executor.loader包使用了cglib或者javassist达到延迟加载的效果；
- 组合模式，例如SqlNode和各个子类ChooseSqlNode等；
- 模板方法模式，例如BaseExecutor和SimpleExecutor，还有BaseTypeHandler和所有的子类例如IntegerTypeHandler；
- 适配器模式，例如Log的Mybatis接口和它对jdbc、log4j等各种日志框架的适配实现；
- 装饰者模式，例如Cache包中的cache.decorators子包中等各个装饰者的实现；
- 迭代器模式，例如迭代器模式PropertyTokenizer；
```

> Mybatis映射文件中，如果A标签通过include引用了B标签的内容，请问，B标签能否定义在A标签的后面，还是说必须定义在A标签的前面？

```sh
虽然Mybatis解析Xml映射文件是按照顺序解析的，但是，被引用的B标签依然可以定义在任何地方，Mybatis都可以正确识别。

原理是，Mybatis解析A标签，发现A标签引用了B标签，但是B标签尚未解析到，尚不存在，此时，Mybatis会将A标签标记为未解析状态。
然后继续解析余下的标签，包含B标签，待所有标签解析完毕，Mybatis会重新解析那些被标记为未解析的标签，
此时再解析A标签时，B标签已经存在，A标签也就可以正常解析完成了。
```





