





# -----mybatis-----

#基本使用

## boot整合

>必要配置

```xml
<!-- Mybatis 启动器 -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>1.3.2</version>
</dependency>
<!-- druid 数据库连接池 -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
    <version>1.1.10</version>
</dependency>
<!-- mysql 数据库驱动 -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```

```properties
spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://192.168.8.7:33306/test0329?useSSL=false&serverTimezone=GMT%2B8
spring.datasource.username=bluecardsoft
spring.datasource.password=#$%_BC13439677375

#xml路径
mybatis.mapper-locations=classpath:com/example/mybatis/mapper/sqlxml/*.xml
#相关xml配置 <-> 与上不能同时使用
#mybatis.config-location=mybatis.xml
#驼峰命名
mybatis.configuration.map-underscore-to-camel-case=true
#mybatis的sql打印，可看到sql结果
logging.level.com.example.demo.mapper=trace
```

```java
//全局注解。可省去每个Mapper文件上的 @Mapper
@MapperScan(value = "com.example.mybatis.mapper")
```

> 非必要配置

```properties
#数据库返回为 null，也调用映射对象的setter方法
mybatis.configuration.call-setters-on-nulls=true
#全局设置：是否启用延迟加载
mybatis.configuration.lazy-loading-enabled=true
#局部设置：是否不启用延迟加载
mybatis.configuration.aggressive-lazy-loading=true
```

> 【推荐】xml版本：代码与sql解耦分离

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.example.base.mapper.LabelMapper">
    <select id="listLabelsByPage" resultType="com.example.base.pojo.Label">
        SELECT `id`, `labelname`, `state`, `count`, `recommend`, `fans` FROM tb_label
    </select>
</mapper>
```

> 【不推荐】注解版：直接在java文件写sql，省去对应的xml

```java
@Select("SELECT sname FROM student WHERE sid=#{id}")
String getNameById(int id);
```

> 资源拷贝插件

```xml
<!-- mvn默认只把 src/main/resources 里的非java文件编译到classes中 -->
<!-- 如果希望 src/main/java 下的文件（如mapper.xml）也编译到 classes 中，在pom.xml中配置 -->
<build>
    <finalName>demo-user</finalName> <!--打包名称-->
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

## 基本语法

> 多个入参：当mybatis接口中有多个入参时，`④种解决方案`

```shell
【推荐】在Mapper接口的参数列表使用注解 @Param("passageId")

【不推荐】第 1 个参数 ---> #{param1}
【不推荐】第 1 个参数 ---> #{arg0}
【不推荐】手动将多参数封装成 pojo 或 Map<String, Object>
```

```java
String getMemo(@Param("mark") String mark, @Param("passageId") int passageId);
```

```xml
<select id="getMemo" resultType="java.lang.String">
    SELECT memo FROM system_set WHERE mark=#{mark} and passage_id=#{passageId}
</select>
```

> 多个回参：对于多列返回值，可以使用Map接收，也可以自定义pojo

```java
Map<String, Object> getMemoAndTypeId(String args);
```

```xml
<select id="getMemoAndTypeId" resultType="java.util.Map">
    SELECT memo,type_id typeId FROM system_set WHERE mark=#{args}
</select>
```

> 多行，多列回参：接口返回 List<map>，但xml的 resultType="map"

```java
List<Map<String, Object>> getAllPassage();
```

```xml
<select id="getAllPassage" resultType="java.util.Map">
    select id,old_park_id,park_id from passage where state=1
</select>
```

> 回参Map：`对于有驼峰命名的key，必须将数据表中的字段使用别名`

```shell
#其中，key为某一列name，value为每一行封装成的 pojo 或 Map。
如：{"喇叭花":{"flowerName":"喇叭花","flowerId":1},"牵牛花":{"flowerName":"牵牛花","flowerId":2}...}
```

```java
@MapKey("flowerName") //‘flowerName’表示java属性名，而非数据库字段名。所以xml中的 flower_name 必须改别名。切记！
Map<String, Flower> listByName(String flowerName);
```

```xml
<select id="listByName" resultType="com.x.Flower">
    select flower_id,flower_name flowerName from flower where flower_name like #{flowerName}
</select>
```

> 抽取引用：`<select/> + <include/>`

```xml
<sql id="ref">
    id,name,age,address,companyId <!-- sql标签 用于抽取可重用的sql片段 -->
</sql>
```

```xml
<select id="selById" resultType="com.heiketu.pojo.Users">
    select
    <include refid="ref" /> <!-- include标签 用于引用前者 -->
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
    <id property="id" column="order_id" /> <!-- id-主键 -->
    <result property="price" column="order_price" /> <!-- result-非主键 -->
    <result property="orderNo" column="order_no" /> <!-- property-属性名; column-字段名 -->
</reslutMap>
```

> 模糊查询：`④种解决方案`

```sql
-- java代码中拼接参数：%张%
SELECT * FROM user WHERE name LIKE #{name}
```

```sql
-- 使用sql函数 concat()
SELECT * FROM user WHERE name LIKE concat('%', #{username}, '%') -- 张
```

```sql
-- xml中使用标签 $
SELECT * FROM user WHERE name LIKE '%${name}%' -- 张 -> '%张%'

SELECT * FROM user WHERE name LIKE '%#{name}%' -- 张 -> "%'张'%" --> 错误
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
<!-- 使用标签<select/> && statementType="CALLABLE" -->
<select id="get_park_free_count0" resultType="java.util.Map" statementType="CALLABLE">
    {call get_park_free_count(#{parkId})}
</select>
```

> 特殊符号

```shell
#{}：预编译处理，防止SQL注入，安全。自动对传入参数添加一个单（双）引号。可以通过OGNL方式取值：参数.属性。
     原理是将sql中的 #{} 替换为 ?，然后调用 PreparedStatement.set() 方法来赋值。
```

```shell
${}：sql字符串拼接。需手动添加单（双）引号。也可以通过OGNL方式取值：参数.属性。
     配合使用 statementType="STATEMENT"，原理是把 ${} 替换成变量的值。
```

```sql
-- 如果使用#{}，那么生成的SQL为：
select name from student where xCode = ?       --传值为'S123456'；

-- 如果使用${}，那么生成的SQL为：
select name from student where xCode = S123456 --直接字符串拼接

--所以，如果 xCode 的数据类型为varchar,那么使用${}就会报错。
```

```xml
<!--一般，建议使用#{}；特殊情况必须要用${}。如：动态传入字段，表名-->
<!--用 #{} order by 'id,name'，变为根据字符串排序，与需求不符-->
<!--用 ${} order by id,name，符合需求-->
<select id="get_res_by_field" resultType="map" statementType="STATEMENT">
    SELECT * FROM person ORDER BY ${field} DESC LIMIT 10
</select>
```



##分页查询

> `插件版`：在插件的拦截方法内拦截待执行的sql，然后重写sql，添加对应的物理分页语句和分页参数。

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

> `分插件版`：xml占位符 ? 不允许在关键字前后进行数学运算，所以需要在代码中完成计算，然后再传递到 xml 中

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

> `方法4`：分步查询

```xml
<select id="listAllStudent" resultMap="stuMap">
    select id,name,tid from student
</select>

<resultMap type="com.example.mybatis.po.Student" id="stuMap">
    <!--student表的其他列，使用 AutoMapping 自动装配！但对于二次查询的参数tid，必须显示装配一次-->
    <!--<result column="tid" property="tid"/>-->

    <!--association   -> 一对一关联查询-->
    <!--property      -> Student类中的属性名-->
    <!--fetchType     -> 是否懒加载：lazy-懒加载，eager-立即加载-->
    <!--select        -> 通过哪个查询可以查出这个对象的信息-->
    <!--column        -> 把当前表哪个列的值作为参数传递给select-->
    <!--对于分步查询传参多列,可以使用形式 column="{key1=id, key2=name}"; mybatis底层将参数封装成map-->
    <association property="teacher" fetchType="lazy" select="com.example.mybatis.mapper.TeacherMapper.selById"
                 column="tid" />
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
    <id column="tid" property="id" />
    <result column="tname" property="name" />

    <!--collection -> 当property是集合类型时使用-->
    <!--ofType     -> 集合的泛型是哪个类-->
    <collection property="studentList" ofType="com.example.spring.bean.Student">
        <id column="sid" property="id" />
        <result column="sname" property="name" />
    </collection>
</resultMap>
```

> `方法4`：分步查询

```xml
<select id="listAllTeacher" resultMap="teacherMap">
    SELECT id, name FROM teacher
</select>

<resultMap type="com.example.spring.bean.Teacher" id="teacherMap">
    <id column="id" property="id" />
    <result column="name" property="name" />

    <collection property="studentList" column="id" 
                select="com.example.spring.mapper.StudentMapper.selByTid" />
</resultMap>
```

```xml
<!--此方法定义在 StudentMapper.xml-->
<select id="selByTid" resultType="com.example.spring.bean.Student">
    SELECT id, name FROM student WHERE tid=#{tid}
</select>
```





# 其他资料

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

>执行原理：根据表达式的值，完成逻辑判断并动态拼接sql的功能。

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

> 一级缓存：作用域为一个 SqlSession，默认开启。

```shell
MyBatis会在一次会话，一个SqlSession对象中创建一个本地缓存(local cache)，
对于每一次查询，都会尝试根据查询的条件去本地缓存中查找是否在缓存中，
如果在缓存中，就直接从缓存中取出，然后返回给用户；否则，从数据库读取数据，将查询结果存入缓存并返回给用户。

Mybatis内部存储缓存使用HashMap，key为 'hashCode + sqlId + Sql' 语句。value为从查询出来映射生成的java对象。
```

> 二级缓存：作用域为一个 NameSpace，默认关闭。

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





# 相关概念

> 物理分页 & 逻辑分页

```shell
#物理分页：依赖的是某一物理实体，这个物理实体就是数据库。
比如，MySQL数据库提供了 limit 关键字，程序员只需要编写带有limit关键字的SQL语句，数据库返回的就是分页结果。

#逻辑分页：依赖的是程序员编写的代码。数据库返回的不是分页结果，而是全部数据，由程序员通过代码获取分页数据。
常用的操作是一次性从数据库中查询出全部数据并存储到List集合中，因为List集合有序，再根据索引获取指定范围的数据。
```

```shell
mybatis的 RowBounds 是逻辑分页，不推荐使用。
```


#--mybatis-plus--

# 基本使用

> BOOT整合

```xml
<!-- mybatis-plus（内置 mybatis-starter） -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.1.2</version>
</dependency>
```

```properties
#mybatis-plus
#主键策略（默认 ID_WORKER）
mybatis-plus.global-config.db-config.id-type=ID_WORKER_STR
#表名前缀
mybatis-plus.global-config.db-config.table-prefix=tb_

#也支持'mybatis'的配置，配置名换成'mybatis-plus'
#驼峰命名（默认开启）
mybatis-plus.configuration.map-underscore-to-camel-case=true
#xml路径
mybatis-plus.mapper-locations=
```

```java
@MapperScan(value = "com.example.mybatis.mapper") //全局注解，使用'mybatis'注解
```

> 主键策略

```shell
AUTO             #数据库自增
ID_WORKER        #分布式全局唯一ID 长整型类型（java属性中的主键使用 Long 类型）
ID_WORKER_STR    #分布式全局唯一ID 字符串类型（String）

UUID             #32位UUID 字符串
INPUT            #自行输入
NONE             #无状态
```

> 实体类

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "user") //表名，如有统一前缀，可使用全局配置
public class User {

    @TableId(value = "userId", type = IdType.ID_WORKER_STR) //主键的数据库字段名为'userId'，及主键策略
    private String userId;

    @TableField("user_name") //数据库中字段名为'user_name'
    private String userName;

    private Integer account;

    @TableField(exist = false) //数据表中不存在的字段
    private Boolean gender;
}
```

> Mapper接口

```java
@Component
public interface UserMapper extends BaseMapper<User> {} //继承已有接口
```

# 条件构造器

>特殊说明

```shell
#（1）不支持以及不赞成在 RPC 调用中把 Wrapper 进行传输
传输 wrapper 可以类比为你的 controller 用 map 接收值（开发一时爽，维护火葬场）
正确的 RPC 调用姿势是写一个 DTO 进行传输，被调用方再根据 DTO 执行相应的操作

#（2）使用的是数据库字段，而不是java属性

#（3）两种 Wrapper --> QueryWrapper：查询和删除时使用，UpdateWrapper：更新时使用
```

> insert

```java
int insert(T entity); //插入一条记录，返回受影响的行数。可直接获取到新插入的主键 id
```

> AbstractWrapper

```sql
-- like
like("name", "王");     --name like '%王%'
notLike("name", "王");  --name not like '%王%'
likeLeft("name", "王"); --name like '%王'

-- isNull
isNull("name");        --name is null
isNotNull("name");     --name is not null

-- in相关
in("age",{1,2,3});     --age in (1,2,3) ---> arg2：数组
in("age", 1, 2, 3);    --age in (1,2,3) ---> arg2：可变数组

inSql("age", "1,2,3"); --age in (1,2,3,4,5,6) --->arg2：sql字符串
inSql("id", "select id from table where id < 3");
                       --id in (select id from table where id < 3) --->arg2：sql查询语句

-- orderBy相关
orderByDesc("name").orderByAsc("id"); --ORDER BY name DESC , id ASC.

-- having
having("sum(age) > 10");       --having sum(age) > 10
having("sum(age) > {0}", 11);  --having sum(age) > 11

-- or（主动调用 or，表示紧接着下一个方法不是用 and 连接！默认是 and）
eq("id",1).or().eq("name","老王"); --id = 1 or name = '老王'


-- apply（拼接sql）
apply("id = 1"); --id = 1
apply("date_format(dateColumn,'%Y-%m-%d') = '2008-08-08'"); 
                 --date_format(dateColumn,'%Y-%m-%d') = '2008-08-08'")
apply("date_format(dateColumn,'%Y-%m-%d') = {0}", "2008-08-08");
                 --date_format(dateColumn,'%Y-%m-%d') = '2008-08-08'")

-- last（只能调用一次，多次调用以最后一次为准。有sql注入的风险，请谨慎使用）
last("limit 1");

-- exists（拼接 EXISTS 语句）
exists("select id from table where age = 1"); --EXISTS(select id from table where age = 1)
```

>QueryWrapper

```sql
select("id", "name", "age");  --设置查询字段
```

> UpdateWrapper

```sql
set("name", "老李头");     --设置 SET 字段
set("name", "");          --数据库字段值变为 空字符串
set("name", null);        --数据库字段值变为 null

setSql("name = '老李头'"); --同上
```

#通用Mapper

> 作用同 mybatis-plus，但是不好用。

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
```

```java
@MapperScan("com.example.friend.mapper") //tk.mybatis.spring.annotation.MapperScan，启动类
```

```java
public interface EmployeeMapper extends Mapper<Employee>, MySqlMapper<Employee> {}
```

> 常用方法

```java
//xxxByPrimaryKey()：实体类需要使用 @Id 注解标明主键。否则，通用 Mapper 会将所有实体类字段作为联合主键。

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



# -------jpa-------

# 基本使用

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

##两种查询

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



