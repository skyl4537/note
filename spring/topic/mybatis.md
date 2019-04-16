[TOC]

# 基本概念

##



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
</dependency>
```
> 必要设置

```java
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
spring.datasource.url=jdbc:mysql://192.168.8.7:33306/test0329?useSSL=false&allowMultiQueries=true&serverTimezone=GMT%2B8
spring.datasource.username=root
spring.datasource.password=123456

mybatis.mapper-locations=classpath*:com/example/demo/mapper/sqlxml/_*.xml //xml路径
//mybatis.config-location=mybatis.xml //相关xml配置 <-> 与上不能同时使用

//全局注解; 可省去每个Mapper文件上的 @Mapper
@MapperScan(value = "com.example.*.mapper") 

//【不推荐】注解版xml; 直接在java文件写sql,省去对应的xml文件
@Select("SELECT sname FROM student WHERE sid=#{id}")
String getNameById(int id);
```
> 非必要设置

```java
mybatis.configuration.mapUnderscoreToCamelCase=true //驼峰命名
mybatis.configuration.callSettersOnNulls=true //数据库返回为null也调用映射对象的setter方法

mybatis.configuration.lazyLoadingEnabled=true //全局设置: 是否启用延迟加载
mybatis.configuration.aggressiveLazyLoading=true //局部设置: 是否不启用延迟加载
```
##xml代码提示

```java
下载dtd文件: "mybatis-3-mapper.dtd"和"mybatis-3-config.dtd"
打开 eclipse -> windows -> preferences -> xml -> xmlcatalog -> add,依次填写:
	//mapper.xml 的代码提示
	location	-> 选择file_system,找到"mybatis-3-mapper.dtd"存放的本地路径
	key_type	-> URI0
	key			-> http://mybatis.org/dtd/mybatis-3-mapper.dtd
	
	//mybatis-config.xml 的代码提示
	location	-> 选择file_system,找到"mybatis-3-config.dtd"存放的本地路径
	key_type	-> URI0
	key			-> http://mybatis.org/dtd/mybatis-3-config.dtd
```
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
    select <include refid="ref"/> from usrs where id = #{id} <!-- include标签 用于引用前者 -->
</select>
```
## 存储过程

```xml
<!-- 使用标签<select/> && statementType="CALLABLE" -->
<select id="get_park_free_count" resultType="java.util.Map" statementType="CALLABLE">
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

> 使用标签<resultMap />

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

<select id="getMemoAndTypeId" resultType="java.util.Map">
	SELECT memo,type_id
	typeId FROM system_set WHERE mark=#{args}
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

> 回参Map

```java
//其中，key为某一列name，value为每一行封装成的 pojo 或 Map。如：
//{"喇叭花":{"name":"喇叭花","id":1},"牵牛花":{"name":"牵牛花","id":2}... ...}
@MapKey("name")
Map<String,Flower> listByName(String name);

<select id="listByName" resultType="com.x.Flower">
		select * from flower where name like #{name}
</select>
```
## 自增主键

> 入参javabean

```java
//是否("useGeneratedKeys")将产生的主键赋值到属性("keyProperty")中
<insert id="addOne" useGeneratedKeys="true" keyProperty="id">
	INSERT INTO person VALUES(default, #{age}, #{name})
</insert>
```
> 入参Map

```java
<insert id="addOne">
	<selectKey keyProperty="id" resultType="int" order="AFTER"> 
		SELECT LAST_INSERT_ID() //order: 相对于insert的执行顺序.(BEFORE|AFTER)
	</selectKey>
	INSERT INTO	person (age,name) VALUES(#{age},#{name})
</insert>
```

> 主键UUID

```java
<insert id="addOne">
	<selectKey keyProperty="id" resultType="String" order="BEFORE">
		SELECT uuid()
	</selectKey>
	INSERT INTO	person (id,age,name) VALUES(#{id},#{age},#{name})
</insert>
```

## 特殊符号

> **#{ }**	安全，预编译处理，防止SQL注入；自动对传入参数添加一个单（双）引号； 可以通过OGNL方式取值： 参数.属性。原理是将sql中的 #{} 替换为 ?，然后调用 PreparedStatement.set() 方法来赋值。

> **${ }** 	字符串替换，拼接sql串； 需手动添加单（双）引号；也可以通过OGNL方式取值：参数.属性。配合使用`statementType="STATEMENT"`

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

#动态sql

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

//collection	-> 要遍历的集合；对于list类型会封装到一个特殊的map中，其key就为list
//item			-> 集合中的每一个对象
//separator		-> 每个元素之间的分隔符
//index			-> 索引。遍历list，index表示索引，item为对应的值；遍历map，index为key，item为map值
//open-close	-> 遍历所有结果拼接一个开始（结束）的字符
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

```java
<dependency>
	<groupId>com.github.pagehelper</groupId>
	<artifactId>pagehelper-spring-boot-starter</artifactId>
	<version>1.2.5</version>
</dependency>

@GetMapping("/person/{pageNum}/{pageSize}")
public PageInfo<Person> listByPage(@PathVariable int pageNum, @PathVariable int pageSize) {
	PageHelper.startPage(pageNum, pageSize); //查询之前设置：页码数，页容量
	List<Person> list = service.listAll();
	
	//PageInfo包含了非常全面的分页属性: isFirstPage,hasPreviousPage,prePage,pages,startRow....
	PageInfo<Person> pageInfo = new PageInfo<>(res, 5);//包含导航页码的PageInfo结果集 - 详见附表
	int[] nums = pageInfo.getNavigatepageNums();//导航页码
	
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
	
	// association	-> 当property对应的'teacher'是一个对象时使用
	// property		-> Student类中的属性名
	// fetchType	-> 是否懒加载; lazy-懒加载,eager-立即加载.
	// select		-> 通过哪个查询可以查出这个对象的信息
	// column		-> 把当前表哪个列的值作为参数传递给select
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

> 关联查询（collection --> 查询一对多时使用，区分与association ---> 查询一对一时使用）

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

# 缓存

## 一级缓存

`MyBatis` 默认是开启一级缓存的。`MyBatis`会在一次会话中，一个`SqlSession`对象中创建一个本地缓存(local cache)，对于每一次查询，都会尝试根据查询的条件去本地缓存中查找是否在缓存中，如果在缓存中，就直接从缓存中取出，然后返回给用户；否则，从数据库读取数据，将查询结果存入缓存并返回给用户。

## 二级缓存

默认情况下，查出的数据先放在一级缓存中。只有当 `sqlSession.commit() 或 close()`，一级缓存数据才会转移到二级缓存中。

Mybatis的二级缓存与一级缓存其机制相同，默认也是采用`PerpetualCache`，HashMap存储，不同在于其存储作用域为 `Mapper(Namespace)`，也是一个`内存级别的缓存`。

> 缓存设置

- ```java
  mybatis.configuration.cache-enabled=true //默认开启
  ```

- 实体类POJO实现序列化接口 `implements Serializable（<cache/>设置 readOnly="true"，则可省）`。

- xml 新增标签： `<cache eviction="LRU" flushInterval="60000" size="1024" readOnly="true"/>`

>cache标签参数

1. eviction		-> 缓存的回收策略，默认LRU（可选值：LRU，FIFO，SOFT，WEAK）。
	. flushInterval	-> 缓存失效时间，默认永不失效（单位毫秒）。
	. size			-> 缓存存放多少个元素，默认1024。
	. type			-> 指定自定义缓存的全类名（需要实现Cache接口，自定义缓存类）。
	. readOnly		-> 是否只读，默认false。

# 附表

## OGNL表达式

```java
e1 lt(小于) e2; //lte 小于等于; gt 大于; gte 大于等于; eq 等于; neq 不等于
e1 or(and) e2; e1 in(not in) e2; e1 +(- * / %) e2;  
!e(非); not e(求反)
e.method(args)		//调用对象方法
e.property			//对象属性值
e1[ e2 ]			//对于List，数组和Map，按索引取值
@class@method(args)	//调用类的静态方法
@class@field		//调用类的静态字段值
```
## 转义字符

```java
//字符转义
< > & ' "	//	&lt; &gt; &amp; &apos; &quot;

//使用<![CDATA[]]>
< > & ' "	//	<![CDATA[ < ]]> <![CDATA[ > ]]> <![CDATA[ & ]]> ...

//<![CDATA[]]>和xml转义字符的关系
(1).对于短字符串<![CDATA[]]>写起来啰嗦，对于长字符串转义字符写起来可读性差。
(2).<![CDATA[]]>表示xml解析器忽略解析，所以解析更快。
(3).<![CDATA[]]>不适用所有情况，转义字符可以。

//<![CDATA[]]>不适合情况
(1).此部分不能再包含"]]>"
(2).不允许嵌套使用
(3)."]]>"这部分不能包含空格或者换行.
```






