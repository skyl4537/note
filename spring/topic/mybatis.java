#xml文件代码提示
	下载dtd文件: "mybatis-3-mapper.dtd"和"mybatis-3-config.dtd"
	打开 eclipse-windows-preferences-xml-xmlcatalog-add, 依次填写:	
		//mapper.xml文件的代码提示
		location: 选择file_system,找到"mybatis-3-mapper.dtd"存放的本地路径
		key_type: URI0
		key: http://mybatis.org/dtd/mybatis-3-mapper.dtd
		
		//mybatis-config.xml文件的代码提示
		location: 选择file_system,找到"mybatis-3-config.dtd"存放的本地路径
		key_type: URI0
		key: http://mybatis.org/dtd/mybatis-3-config.dtd
	
#Spring整合
	mybatis.mapper-locations=classpath*:com/example/demo/mapper/sqlxml/ *.xml //xml路径
	//mybatis.config-location=mybatis.xml //相关xml配置 <-> 与上不能同时使用
	
	mybatis.configuration.mapUnderscoreToCamelCase=true //驼峰命名
	mybatis.configuration.callSettersOnNulls=true //数据库返回为null也调用映射对象的setter方法
	
	mybatis.configuration.lazyLoadingEnabled=true//全局设置: 是否【启用】延迟加载.
	mybatis.configuration.aggressiveLazyLoading=true//局部设置: 是否【不启用】延迟加载.
	
	//全局注解; 可省去每个Mapper文件上的 @Mapper
	@MapperScan(value = "com.example.blue.mapper") 
	
	//(不推荐).注解版xml; 直接在java文件写sql,省去对应的xml文件
	@Select("SELECT sname FROM student WHERE sid=#{id}")
	String getNameById(int id);

///-----------------<<<常用标签>>>----------------------------------------------------------
#内置参数
	0.—parameter //代表整个参数
		单个参数: _parameter 就是这个参数
		多个参数: mybatis封装成map, _parameter 表示这个map.
		
	1.—databaseId
		如果配置了databaseIdProvider, _databaseId 就是当前数据库的别名.(mysql,oracle,sql server...)
		
		  <select id="getEmpsTestInnerParameter" resultType="com.x.bean.Employee">
				<if test="_databaseId=='mysql'"> //mysql数据库 
					select * from tb_employee
					<if test="_parameter!=null"> //参数不为空
						where last_name like #{lastName} //_parameter 此时表示lastName
					</if>
				</if>
				<if test="_databaseId=='oracle'">//oracle数据库 
					select * from employees
					<if test="_parameter!=null">
						where last_name like #{_parameter.lastName} //_parameter 此时表示Emp对象
					</if>
				</if>
		  </select>
		  
#标签sql—include
	//sql用于抽取可重用的sql片段; include用于引用.
		<sql id="ref">
			id,name,age,address,companyId
		</sql>

		<select id="selById" resultType="com.heiketu.pojo.Users">
			select <include refid="ref"/> from usrs where id = #{id}
		</select>
	
#存储过程
	//(1).使用<select/>标签; (2).添加 statementType="CALLABLE"
	<select id="get_park_free_count" resultType="java.util.Map" statementType="CALLABLE">
		{call get_park_free_count(#{parkId, mode=IN, jdbcType=INTEGER})}
	</select>

#类型别名
	0.系统内置别名
		int _int; boolean _boolean //一般数据类型,加前缀_
		Integer int; String string; Map map; List list //一般类型的包装类,类型小写
	
	1.自定义别名(不推荐)
		@Alias("author") //单独类起别名-注解
		mybatis.type-aliases-package=com.x.x.entity //配置某个包下所有类的别名(类名小写)
	
#字段名与列名不一致
	0.使用别名
		select order_id id, order_price price, order_no orderNo from orders where order_id=#{id}; 
	
	1.使用标签<resultMap/>
		<select id="getOrder" resultMap="orderMap"> //resultMap 取代 resultType
			select * from orders where order_id=#{id}
		</select>
		
		<resultMap type="com.x.order" id="orderMap"> 
			<id property="id" column="order_id" /> //id-主键
			<result property="price" column="order_price" /> //result-非主键
			<result property = "orderNo" column ="order_no" /> //property-属性名; column-字段名
		</reslutMap>

#多参数取值
	0.单个入参 -> 不会做特殊处理,xml中直接使用 #{参数名} 取值. 
		
	1.多个入参 //mybatis将多个参数封装到一个map中
		(不推荐).其中key为param1...; value为参数值. 如 #{param1} 取出第一个参数值.
		(不推荐).也可以通过参数序列号取值, 如 #{0} 也可以取出第一个参数值.
		
		(推荐).使用注解 @Param("id"), 可以通过 #{id} 取出对应参数值.
		(推荐).将多个参数封装成 pojo 或 Map<String,Object>
		
	2.回参'多列'或'多行' //结果集封装 pojo 或 Map<String, Object>
		List<Map<String, Object>> listPassage(int passage_id); 
		
		//接口返回list,但xml的resultType="map"
		<select id="listPassage" resultType="map" statementType="CALLABLE">
			{call listPassage(#{passage_id})}
		</select>
			
	3.回参Map<String, Object>
		//其中, key为某一列name, value为每一行封装成的 pojo 或 Map.
		//如 -> {"喇叭花":{"name":"喇叭花","id":1},"牵牛花":{"name":"牵牛花","id":2}... ...}
		@MapKey("name")
		Map<String,Flower> listByName(String name);
		
		<select id="listByName" resultType="com.x.Flower">
			select * from flower where name like #{name}
		</select>
		
#自增主键
	0.入参javabean
		//是否("useGeneratedKeys")将产生的主键赋值到属性("keyProperty")中
		<insert id="addOne" useGeneratedKeys="true" keyProperty="id">
			INSERT INTO person VALUES(default,#{age},#{name})
		</insert>
		
	1.入参Map
		<insert id="addOne">
			<selectKey keyProperty="id" resultType="int" order="AFTER"> 
				SELECT LAST_INSERT_ID() //order: 相对于insert的执行顺序.(BEFORE|AFTER)
			</selectKey>
			INSERT INTO	person (age,name) VALUES(#{age},#{name})
		</insert>

	2.主键UUID
		<insert id="addOne">
			<selectKey keyProperty="id" resultType="String" order="BEFORE">
				SELECT uuid()
			</selectKey>
			INSERT INTO	person (id,age,name) VALUES(#{id},#{age},#{name})
		</insert>
		
#特殊符号
	(0).#{}: 安全,预编译处理,防止SQL注入; 自动对传入参数添加一个单(双)引号; 可以通过OGNL方式取值: 参数.属性
		原理: 将sql中的#{}替换为?, 然后调用PreparedStatement的set方法来赋值 

	(1).${}: 字符串替换; 需手动添加单(双)引号; 也可以通过OGNL方式取值: 参数.属性
		配合使用 -> statementType="STATEMENT"
	
	#一般,建议使用#{}; 特殊情况必须要用${}, 比如: 动态传入字段,表名
		//用 #{} ---> order by 'id,name' ,变为根据字符串排序,与需求不符
		//用 ${} ---> order by id,name ,符合需求
		<select id="get_res_by_field" resultType="map" statementType="STATEMENT">
			SELECT * FROM person ORDER BY ${field} DESC LIMIT 10
		</select>
	
#模糊查询
	0.java中拼接(推荐)
		SELECT * FROM user WHERE name LIKE #{name} //%张%
	
	1.使用占位符${}
		SELECT * FROM user WHERE name LIKE '%${name}%' //张
	
	2.xml中拼接
		SELECT * FROM user WHERE name LIKE concat('%', #{username}, '%') //张
		
		<select id="selectLike">
			<bind name="name" value="'%'+_parameter+'%'" />
			select * from user where name like #{name} //张
		</select>
	
#动态sql
	0.where - //去掉第一个and; <where/>里有内容则生成 where 关键字,没有不生成
		//但对于and写在条件之后的也会出问题: name like #{name} and
		
		<select id="listByIf" resultType="com.x.bean.Flower">
			select * from flower /** where true(替换) */
			<where> 
				<if test="name!=null and name!=''">
					and name like #{name}
				</if>
				//@class@method(args)调用类的静态方法 - 详见附表
				<if test="@org.apache.commons.lang3.math.NumberUtils@isParsable(price)">
					and price > #{price} //检验是否为数字??? 是则增加为查询条件,否则不增加
				</if>
			</where>
		</select>
	
	1.set - //去掉最后一个逗号; <set/>里有内容则生成 set 关键字,没有不生成
		<update id="updateByIf">
			update flower
			<set>
				id=#{id}, //防止<set/>内容为空,不生成 set 关键字
				<if test="name!=null and name!=''">
					name=#{name}, //满足条件,则追加到更新条件
				</if>
				<if test="price>=0">
					price=#{price}, //同上
				</if>
			</set>
			where id=#{id}
		</update>	
	
	2.choose - //只要一个分支满足,其他都不执行; 相当于 if-else if-else
		<select id="listByChoose" resultType="com.x.bean.Flower">
			select * from flower
			<where>
				<choose>
					<when test="name!=null and name!=''">
						name like #{name} //if - 先判断,满足即结束,不再去判断else if
					</when>
					<when test="@org.apache.commons.lang3.math.NumberUtils@isParsable(price)">
						price > #{price} //else if
					</when>
					<otherwise>
						production like #{production} //else
					</otherwise>
				</choose>
			</where>
		</select>
	
	3.foreach - (遍历查询)
		//collection: 要遍历的集合; 对于list类型会封装到一个特殊的map中,其key就为list
		//separator: 每个元素之间的分隔符
		//item: 将当前遍历出来的元素赋值给指定的变量
		//index: 索引.	遍历list,index表示索引,item为对应的值.
		//				遍历map,index表示map的key,item为map值
		//open-close: 遍历所有结果拼接一个开始(结束)的字符
		<select id="listByForeach" resultType="com.x.bean.Flower">
			select * from flower where id in
			<foreach collection="list" item="item" index="index" open="(" close=")" separator=",">
				#{item}
			</foreach>
		</select>
	
	4.foreach - (批量新增)
		boolean saveBatch(List<Flower> list);
		
		//mysql特有的批量插入 <-> insert into t_customer (id,c_name) values (?,?),(?,?)....
		<insert id="saveBatch">
			insert into flower (name,price,production) values
			<foreach collection="list" item="item" index="index" separator=",">
				(#{item.name}, #{item.price}, #{item.production})
			</foreach>
		</insert>
	
	5.sql—include - (抽取和引用sql片段)
		<sql id="column">
			id,name,price,production,memo,edit_flag
		</sql>
		
		<select id="">
			select 
				<include refid="column"></include>
			from flower
		</select>
	
#分页查询
	//Mybatis使用 RowBounds对象 进行分页, 它是针对 ResultSet结果集 执行的内存分页,而非物理分页;
	//可以在sql内直接书写带有物理分页的参数来完成物理分页功能,也可以使用分页插件来完成物理分页.
	
	0.插件版
		//在插件的拦截方法内拦截待执行的sql,然后重写sql,根据dialect方言,添加对应的物理分页语句和物理分页参数.
			<dependency>
				<groupId>com.github.pagehelper</groupId> //分页插件
				<artifactId>pagehelper-spring-boot-starter</artifactId>
				<version>1.2.5</version>
			</dependency>

		@GetMapping("/person/{pageNum}/{pageSize}")
		public PageInfo<Person> listByPage(@PathVariable int pageNum, @PathVariable int pageSize) {
			PageHelper.startPage(pageNum, pageSize); //查询之前设置: 页码数,页容量
			List<Person> list = service.listAll();
			
			#PageInfo包含了非常全面的分页属性: isFirstPage,hasPreviousPage,prePage,pages,startRow....
			PageInfo<Person> pageInfo = new PageInfo<>(res, 5);//包含导航页码的PageInfo结果集 - 详见附表
			int[] nums = pageInfo.getNavigatepageNums();//导航页码
			
			return pageInfo;
		}
		
	1.非插件版
		//xml占位符 ? 不允许在关键字前后进行数学运算, 所以需要在代码中完成计算,然后再传递到 mapper.xml 中
		int pageSize = 2, pageNum = 2;
		Map<String, Object> map = new HashMap<>();
		map.put("pageSize", pageSize);
		map.put("pageStart", pageSize * (pageNum - 1));
		List<People> list = service.listPage(map);

		<select id="listPage" resultType="com.x.People">
			select * from people limit #{pageStart}, #{pageSize}
		</select>
		
		
#联表查询对象
	List<Student> listAll();

	class Student{ //查询学生时,把老师信息查出. Student类中包含了一个 Teacher 对象
		//... ...
		private Teacher teacher; 
	}
	
	1.列别名及Auto—Mapping
		//在SQL中'.'是关键字符,所以在两侧添加反单引号 `teacher.id`
		<select id="listAll" resultType="com.x.pojo.Student">
			select t.id `teacher.id`,t.name `teacher.name`,s.id id,s.name name
			from student s LEFT JOIN teacher t on t.id=s.tid
		</select>
		
	2.级联查询 - /**左外连接(left outer join): 可查询所有的学生信息*/
		<select id="listAll" resultMap="studentMap">
			select s.id sid,s.name sname,t.id tid,t.name tname 
			FROM student s left outer join teacher t on s.tid=t.id
		</select> 
		
		<resultMap type="com.x.pojo.Student" id="studentMap">
			<id column="sid" property="id"/>
			<result column="sname" property="name"/>
			<result column="tid" property="tid"/>
			
			<result column="tid" property="teacher.id"/>
			<result column="tname" property="teacher.name"/>
		</resultMap>
	
	3.联合查询
		<select id="listAll" resultMap="studentMap">
			select s.id sid,s.name sname,t.id tid,t.name tname 
			FROM student s left outer join teacher t on s.tid=t.id
		</select> 

		<resultMap type="com.x.pojo.Student" id="studentMap">
			<id column="sid" property="id"/>
			<result column="sname" property="name"/>
			<result column="tid" property="tid"/>
			
			/** 此处, <association/>可看做<reslutMap/>
			  * property: Student类中的属性名
			  * javaType: 装配后返回的java类型 */
			<association property="teacher" javaType="com.x.pojo.teacher" >
				<id column="tid" property="id"/>
				<result column="tname" property="name"/>
			</association>
		</resultMap>
		
	4.分步查询 - //(N+1查询); 先查询 (1次) 学生表的所有信息,然后根据学生的外键tid查询 (N次) 老师表的所有信息.
		/** 1.效率低, 但满足: 只查询学生信息 和 查询学生和老师信息 两种情况.
		  * 2.懒加载模式, 只使用学生信息时,不会进行老师信息查询; 只有使用老师信息时,才去二次查询! */
	
		<select id="listAll" resultMap="stuMap">
			select * from student
		</select>
		
		/** student表的其他列,使用 Auto Mapping 特性,自动装配!
		  * 但, 对于二次查询的参数tid,必须显示装配一次
		  *
		  * association: 当property对应的'teacher'是一个对象时使用
		  * property: Student类中的属性名
		  * fetchType: 是否懒加载; lazy-懒加载,eager-立即加载.
		  * select: 通过哪个查询可以查出这个对象的信息
		  * column: 把当前表哪个列的值作为参数传递给select
		  * 对于分步查询传参多列,可以使用形式 column="{key1=id,key2=name}"; mybatis底层将参数封装成map
		  */
		<resultMap type="com.x.pojo.Student" id="stuMap">
			<result column="tid" property="tid"/>
			
			<association property="teacher" fetchType="lazy" select="com.x.mapper.TeacherMapper.selById" column="tid">
			</association>
		</resultMap>
	
#联表查询集合
	List<Person> listAll();
	
	class Teacher{ //查询老师时,把所有学生信息查出. Teacher类中包含了一个 Student 集合
		//... ...
		private List<Student> list; 
	}
	
	1.联合查询
		<select id="listAll" resultMap="teaMap">
			select t.id tid,t.name tname,s.id sid,s.name sname,age,tid 
			from teacher t LEFT JOIN student s on t.id=s.tid;
		</select>
		
		<resultMap type="com.x.pojo.Teacher" id="teaMap">
			<id column="tid" property="id"/>
			<result column="tname" property="name"/>
			
			//collection: 当property是集合类型时使用
			//ofType: 集合的泛型是哪个类
			<collection property="list" ofType="com.x.pojo.Student" >
				<id column="sid" property="id"/>
				<result column="sname" property="name"/>
				<result column="tid" property="tid"/>
			</collection>
		</resultMap>
	
	2.分步查询
		<select id="listAll" resultMap="teaMap">
			select * from teacher
		</select>
		
		<resultMap type="teacher" id="teaMap"> //可以auto mapping
			<id column="id" property="id"/>
			<result column="name" property="name"/>
			
			<collection property="list" column="id" 
				select="com.x.mapper.StudentMapper.selByTid"></collection>
		</resultMap>
	
	3.注解实现
		@Results(value={ //相当于<resultMap/>
			@Result(id=true,property="id",column="id"), //相当于<id/>或<result/>, id=true则相当于id
			@Result(property="name",column="name"),
			@Result(property="list",column="id", //@Many相当于<collection/>, @One相当于<association/>
				many=@Many(select="com.x.mapper.StudentMapper.selByTid"))
		})
		@Select("select * from teacher")
		List<Teacher> selTeacher();
		
#鉴别器
	//根据某列值的不同,选取不同的 resultMap
	
		<select id="getEmployee" resultMap="employeeMap">
			select id, name, sex from employee where id =#{id}
		</select>
		
		<resultMap id="employeeMap" type="com.x.pojo.Employee">
			<id property="id" column="id"/>
			<result property="name" column="name"/>
			<result property="sex" column="sex"/>

			<association property="empCard" column="id"
						 select="com.x.mapper.EmpCardMapper.selByEmpId"/>
			<collection property="projectList" column="id"
						select="com.x.mapper.ProjectMapper.listByEmpId"/>

			<discriminator javaType="int" column="sex">
				<case value="0" resultMap="femaleEmpMap"/>
				<case value="1" resultMap="maleEmpMap"/>
			</discriminator>
		</resultMap>
		
		//不同Employee.sex, 不同Employee.sexList
		//extends: 等同于java的继承
		<resultMap id="femaleEmpMap" type="com.x.pojo.FemaleEmployee" extends="employeeMap">
			<association property="sexList" column="id"
						select="com.x.mapper.FemaleEmpMapper.selById"/>
		</resultMap>
		
		<resultMap id="maleEmpMap" type="com.x.pojo.MaleEmployee" extends="employeeMap">
			<association property="sexList" column="id"
						select="com.x.mapper.MaleEmpMapper.selById"/>
		</resultMap>

///----------------<<<mybatis缓存>>>-----------------------------------------------------------------------

1.一级缓存(一直开启,无需配置) - //本地缓存; sqlSession级别缓存.
		执行<select/>,先查询本地缓存, 没有数据才查询数据库.	
		
	#一级缓存失效情况:
		(1).sqlSession 不同.
		(2).sqlSession 相同,查询条件不同.(当前一级缓存中还没有这个数据)
		(3).sqlSession 相同,但两次查询之间执行了增删改操作.(这次增删改可能对当前数据有影响)
		(4).sqlSession 相同,手动清除了一级缓存.(如: sqlSession.clearCache())
		
2.二级缓存 - //全局缓存; NameSpace级别缓存.
		默认情况下,查出的数据先放在一级缓存中.
		只有当 sqlSession.commit() 或 close(),一级缓存数据才会转移到二级缓存中.
	
	#全局设置(NameSpace级别)
		(1).mybatis.configuration.cache-enabled=true (默认开启)
		(2).实体类POJO实现序列化接口 implements Serializable. (<cache/>设置 readOnly="true",则可省)
		(3).mapper.xml 新增标签: <cache eviction="LRU" flushInterval="60000" size="1024" readOnly="true"/>
		
	#<cache/>参数
		(1).eviction: 缓存的回收策略,默认LRU. (可选值: LRU;FIFO;SOFT;WEAK)
		(2).flushInterval: 缓存失效时间,默认永不失效. (单位毫秒)
		(4).size: 缓存存放多少个元素,默认1024.
		(5).type: 指定自定义缓存的全类名. (需要实现Cache接口,自定义缓存类)
		(3).readOnly: 是否只读,默认false.
			//true: 相同sql返回都是同一个对象. (性能提高,但并发操作同一条数据时,可能不安全)
			//false: 相同sql,后面访问的是cache的clone副本
			
	#局部设置(select级别)
		(1).useCache="false" //二级关闭,一级可用
		(2).flushCache="true" //一级,二级都会清除. 其中,<select/>默认false; 其他默认true!!!
			
		(3).sqlSession.clearCache(); //清除当前session的一级缓存.
		(4).mybatis.configuration.cache-enabled=false //二级关闭,一级可用
	
	
///----------------<<<附表>>>---------------------------------------------------------------------------
	
0.常用OGNL表达式
	e1 lt(小于) e2; (lte 小于等于; gt 大于; gte 大于等于; eq 等于; neq 不等于)
	e1 or(and) e2; e1 in(not in) e2; e1 +(- * / %) e2;  
	!e(非); not e(求反)
	e.method(args) //调用对象方法
	e.property //对象属性值
	e1[ e2 ] //对于List,数组和Map,按索引取值
	@class@method(args) //调用类的静态方法
	@class@field //调用类的静态字段值

0.转义字符
	#字符转义
		< > & ' "	->	&lt; &gt; &amp; &apos; &quot;

	#使用<![CDATA[]]>
		< > & ' "	->	<![CDATA[ < ]]> <![CDATA[ > ]]> <![CDATA[ & ]]> ...
	
	#<![CDATA[]]>和xml转义字符的关系
		(1).<![CDATA[]]>不适用所有情况,转义字符可以. //(1).此部分不能再包含"]]>"; (2).不允许嵌套使用; (3)."]]>"这部分不能包含空格或者换行.
		(2).对于短字符串<![CDATA[]]>写起来啰嗦,对于长字符串转义字符写起来可读性差
		(3).<![CDATA[]]>表示xml解析器忽略解析,所以解析更快

0.PageHelper中默认PageInfo的成员变量
    private int pageNum;//当前页
    private int pageSize;//每页的数量
    private int size;//当前页的数量
	
    //由于startRow和endRow不常用，这里说个具体的用法
    //可以在页面中"显示startRow到endRow 共size条数据"
    
    private int startRow; //当前页面第一个元素在数据库中的行号
    private int endRow;//当前页面最后一个元素在数据库中的行号
	
    private long total;//总记录数
    private int pages;//总页数
    private List<T> list;//结果集
    private int navigatePages;//导航页码数
    private int[] navigatepageNums;//所有导航页号
    
    private int firstPage;//第一页
    private int prePage;//前一页
    private boolean isFirstPage = false;//是否为第一页
    private boolean isLastPage = false;//是否为最后一页
    private boolean hasPreviousPage = false;//是否有前一页
    private boolean hasNextPage = false;//是否有下一页



3.事务 - 同时成功,同时失败
	@Transactional //serviceImpl的方法上添加此注解
	
	编程式事务&声明式事务:
		编程式事务 -> 需要你在代码中直接加入处理事务的逻辑,可能需要在代码中显式调用beginTransaction(),
		commit(),rollback()等事务管理相关的方法,如在执行a方法时候需要事务处理,
		你需要在a方法开始时候开启事务,处理完后。在方法结束时候,关闭事务.
		
		声明式事务 -> 在a方法外围添加注解或者直接在配置文件中定义,a方法需要事务处理,
		在spring中会通过配置文件在a方法前后拦截,并添加事务.
		
		二者区别 -> 编程式事务侵入性比较强,但处理粒度更细.

///----------------<<<概念>>>-----------------------------------------------------------------------

#数据库连接池
	在高频率访问数据库时,使用数据库连接池可以降低服务器系统压力,提升程序运行效率.
	小型项目不适用数据库连接池
	//关闭连接对象,只是把连接对象归还给数据库连接池,并将其状态变成 Idle

#什么是框架?
	框架是软件的半成品. 为解决问题制定的一套整体解决方案,在提供功能基础上进行扩充.
	框架中不能被封装的代码(变量),需要使用配置文件(xml).
	// 1. 需要建立特定位置和特定名称的配置文件.
	// 2. 需要使用 xml解析技术和反射技术
	
#类库vs框架?
	类库: 提供的类没有封装一定逻辑.  //举例: 类库就是名言警句,写作文时引入名言警句
	框架: 区别与类库,里面有一些约束. //举例: 框架是填空题
	
#mybatis介绍
	数据访问层框架; 底层是对JDBC的封装; 无需编写实现类,只需写sql
	
	JDBC: sql包含在代码中,硬编码高耦合. 实际开发中sql频繁修改,维护不易.
	mybatis: 半自动化ORM框架. sql和java编码分开,一个专注数据,一个专注业务. 低耦合.
	Hibernate: 全自动ORM(Object Relation Mapping,对象关系映射),自动产生sql, 但不灵活.
	
	// 查询关联对象或者关联集合对象时,
	// Hibernate 根据对象关系模型直接产生sql, 所以它是全自动的.
	// Mybatis 需要手动编写sql, 所以是半自动ORM映射工具.
	
	(1).SqlSession 实例非线程安全,不能被共享. 每次用完必须关闭.
	(2).dtd('Document Type Definition,文档类型定义')文件定义xml文档的合法构建模块,方便编写xml时有提示.
	(3).databaseIdProvider - 支持多数据库厂商(详见官网)
	(4).statementType取值范围{ PREPARED:预编译,#{}); STATEMENT(非预编译,${}); CALLABLE(存储过程)} 
	(5).增删改-没有 resultType 属性,返回值表示受影响的行数,类型可以是: boolean, int, long, void
	
	//原理 - JDK动态代理
	使用JDK动态代理为Dao接口生成代理对象proxy,拦截接口方法,转而执行MappedStatement所代表的sql,然后将sql执行结果返回.
	Dao接口没有实现类,当调用接口方法时,通过 <全限定名+方法名> 拼接字符串作为key值,唯一定位一个MappedStatement.
	Dao接口里的方法不能重载!!! 因为 <全限定名+方法名> 保存和寻找策略.
	
	private void getMapper() throws IOException {
		// '工厂+builder'设计模式
		InputStream is = Resources.getResourceAsStream("myabtis.xml");
		SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(is); //(1)
		
		SqlSession session = factory.openSession(); //(2)

		FlowerMapper flowerMapper = session.getMapper(FlowerMapper.class); //(3)
		List<Flower> res = flowerMapper.listAll();
		session.close();
	}
	
#运行流程:
	(1).加载解析 "myabtis.xml" 和 "*mapper.xml",封装到全局配置 Configuration 对象.
		build(Configuration) 返回封装了 Configuration 的 DefaultSqlSessionFactory. (SqlSessionFactory 接口的实现类)
		//mapper.xml 中每一个 增/删/改/查 标签都封装成一个 MappedStatement 对象.
		
	(2).openSession() 先通过全局配置 Configuration 的 newExcutor() 返回 Excutor 对象,
		根据全局属性 defaultExecutorType 的不同生成不同类型的Excutor对象: SimpleExcutor/ReuseExcutor/BatchExcutor
		如果配置二级缓存,则返回带有缓存功能的 CachingExcutor
		然后调用 interceptorChain.pluginAll(excutor) 使用每一个拦截器重新包装Excutor对象. //mybatis插件的主要逻辑
		最后,将 Configuration 和 Excutor 封装到 DefaultSqlSession 对象. 
		//sqlSession = new DefaultSqlSession(configuration, excutor, autoCommit);
	
	(3).getMapper(X) 先根据接口类型获取 MapperProxyFactory 对象,
		然后根据 mapperProxyFactory.newInstace(sqlSession) 方法创建 MapperProxy 代理对象.
		//MapperProxy 是一个 InvocationHandler 动态代理接口
		//MapperProxy 包含	sqlSession; sqlSession 包含 excutor 和 configuration
	
	(4).listAll() 先判断当前执行方法是否为 Object 方法 (toString(),hashCode()等),是则直接执行,不是则调用底层.
		再判断当前方法类型(增/删/改/查); 封装参数(多个参数封装成map); 
		然后 DefaultSqlSession -> Excutor -> StatementHandler 迭代调用底层的 query() 方法,
		其中, StatementHandler 查询涉及到 ParameterHandler 和 ResultSetHandler 两个对象,及 TypeHandler 对象.
		//StatementHandler: 处理sql语句预编译,设置参数值等相关工作
		//ParameterHandler: 设置sql的参数
		//ResultSetHandler: 处理结果集
		//TypeHandler: 在整个过程中,进行数据库类型和javabean类型的映射
		
		//先查询本地一级缓存是否有值,没有才去查询数据库,并将结果保存到一级缓存.
		//底层最终还是调用 JDBC 的 StateMent 和 PreparedStatement.
		
	/**总结*/
		(1).根据全局配置文件,初始化 Configuration 对象
		(2).创建 DefaultSqlSession 对象,包含 Configuration 及 Excutor(根据全局配置文件中的 DefaultExcutorType)
		(3).MapperProxy proxy = DefaultSqlSession.getMapper(); //获取Mapper接口的代理对象
		(4).调用 DefaultSqlSession(底层Excutor) 的增删改查方法: 
			过程中涉及到 ParameterHandler,ResultSetHandler,TypeHandler 三个对象.








































