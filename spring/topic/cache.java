

//{--------<<<x>>>-------------------------------------------------------------------------

//}

//{--------<<<abc>>>-----------------------------------------------------------------------

#@Cacheable -> 开启缓存,标注在(类/方法)上; 适用于查询
	1.value/cacheNames
		用来指定缓存的名称; 必须指定
		数组形式,表示可以缓存到多个Cache中; (value = {"people","emp"})		
	2.key
		用来指定存储缓存时使用的key,默认使用方法参数对应的值
		不指定则使用默认策略生成key; 支持SpringEL表达式 //详见附表
		
	0.工作流程
		(0).目标方法调用之前,先检查缓存,有则返回; 无则查库,并将结果放入缓存
		(1).检查缓存时, 先根据 Value 找到对应的Cache对象
		(2).再根据 key 从Cache对象(ConcurrentMapCache<K,V>)中取出对应的缓存值
		
		@Override
		@Cacheable(value = "people", key = "#person.id") //自定义key: person.id
		public Person getOneById(Person person) {
			return personMapper.getOneById(person.id);
		}

#@CacheEvict -> 清空缓存; 适用于添加,删除
	1.value
		缓存名称,同上
	2.allEntries
		是否清空对应 value 中的所有缓存; 默认 false
	3.beforeInvocation
		清空动作是否在方法调用之前; 默认 false,即方法调用出错,则缓存不会清空

		@Override
		@CacheEvict(value = "people"/*, allEntries = true*/) //默认key为方法参数值
		public int deleteOneById(int id) {
			return personMapper.deleteOneById(id);
		}

//}

//{--------<<<ehcache>>>-------------------------------------------------------------------
#ehcache - 纯java的进程内缓存框架! 快速,精干
	0.pom文件
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-cache</artifactId>
		</dependency>
		<dependency>
			<groupId>net.sf.ehcache</groupId>
			<artifactId>ehcache</artifactId>
		</dependency>
		
	1.properties
		spring.cache.type=ehcache //缓存类型, 如用redis改为redis
		spring.cache.ehcache.config=classpath:ehcache.xml //存放'/resources'目录下
	
	2.ehcahe.xml
		<ehcache xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:noNamespaceSchemaLocation="ehcache.xsd"
			updateCheck="true" monitoring="autodetect" dynamicConfig="true">

			<diskStore path="java.io.tmpdir/ehcache" />
			
			// <!-- 的默认缓存策略 -->
			<defaultCache
				eternal="false" //是否永不过期?? 默认false. true则属性 timeTo* 将不起作用. 
				timeToIdleSeconds="120" //缓存最大闲置时间. 0无限.
				timeToLiveSeconds="120" //........存活............
				
				maxEntriesLocalHeap="100" //内存缓存最大个数. 0无限. 过时属性 maxElementsInMemory
				maxEntriesLocalDisk="100" //磁盘.......................... maxElementsOnDisk
				
				//磁盘缓存相关
				overflowToDisk="true" //内存中缓存过量是否输出到磁盘?? 默认true
				diskSpoolBufferSizeMB="30" //写入磁盘时IO缓冲区大小. 默认30MB. 每个Cache一个缓冲区
				diskExpiryThreadIntervalSeconds="120" //磁盘缓存清理线程的运行间隔. 默认120s.
				diskPersistent="false" //磁盘缓存在jvm重启后是否保持. 默认为false
				
				memoryStoreEvictionPolicy="LRU"> //内存中缓存过量后的移除策略. 默认 LRU(最近最少使用)
				
				<persistence strategy="localTempSwap" />
			</defaultCache>
			
			// <!-- 自定义缓存策略 -->
			<cache
				name="system_set"
				eternal="false"
				timeToLiveSeconds="300"
				maxEntriesLocalHeap="2"
				overflowToDisk="true"
				maxEntriesLocalDisk="5">
			</cache>
		</ehcache>
	
	3.配置说明
		diskStore: 过量缓存输出到磁盘的存储路径.
			//默认 path="java.io.tmpdir".
			//windows-> "C:\Users\当前用户\AppData\Local\Temp\"; linux-> "/tmp"
			//缓存文件名为缓存name, 后缀为data. 如: C:\Users\当前用户\AppData\Local\Temp\system_set.data
		
		当 maxEntriesLocalHeap 过量时,两种情况: 
			//overflowToDisk=true  --> 过量缓存输出磁盘. 
			//overflowToDisk=false --> 则按照 memoryStoreEvictionPolicy 从内存中移除缓存
			
		clearOnFlush: 调用 flush() 方法时,是否清空内存缓存??? 默认true.
			//设为true, 则系统在初始化时会在磁盘中查找 CacheName.index 缓存文件, 如 system_set.index. 找到后将其加载到内存.
			//注意: 在使用 net.sf.ehcache.Cache 的 void put (Element element) 方法后要使用 void flush() 方法
		
#测试DEMO
	0.bean
		@Data
		public class Student implements Serializable { //对于磁盘缓存,必须'序列化'
			private  int id;
			private String name;
		}
	
	1.Service
		@Service
		public class HelloServiceImpl implements HelloService {
			
			@Override
			@Cacheable(value = "student") //开启缓存, 全局注解 @EnableCaching
			public Student selStudentById(int id) {
				return helloMapper.selStudentById(id);
			}
		}
	2.单元测试
		@RunWith(SpringRunner.class)
		@SpringBootTest
		public class ApplicationTests {
			@Autowired
			HelloService helloService;

			@Test
			public void test() {
				System.out.println(helloService.selStudentById(3));
				System.out.println(helloService.selStudentById(3)); //从日志看出,第二次不再查库
			}
		}
		
//}

//{--------<<<redis>>>---------------------------------------------------------------------
#安装
	0.ubuntu安装
		sudo apt-get install redis-server //安装完成后,redis服务会自动启动
		
		ps -aux|grep redis		//检查Redis服务器系统进程
		netstat -nlt|grep 6379	//通过启动命令检查Redis服务器状态
		sudo /etc/init.d/redis-server status //同上

	1.其他方式
		yum install gcc-c++				//安装gcc
		tar -zxvf redis-3.0.0.tar.gz	//解压
		cd redis-3.0.0					//进入解压后的目录
		make							//进行编译
		make PREFIX=/usr/local/redis install	//将redis安装到指定目录
		./redis-server					//启动redis
		
#配置 -> 使用命令'whereis redis', 查找redis的安装路径,编辑文件 redis.conf
	1.Redis的访问账号
		//默认,访问Redis服务器是不需要密码的. 现配置密码为: redis
		requirepass redis //取消 requirepass 这一行的注释,或者新增一行

	2.配置Redis服务器可以远程访问
		//默认,Redis服务器不允许远程访问,只允许本机访问
		#bind 127.0.0.1 //注释此行
		sudo /etc/init.d/redis-server restart //重启redis

	3.redis客户端可视化工具推荐 -> RedisDesktopManager

#整合测试
	0.pom文件
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
		
	1.properties
		spring.cache.type=redis
		spring.redis.host=192.168.5.25
		spring.redis.password=redis
		spring.redis.jedis.pool.max-idle=10
		spring.redis.jedis.pool.min-idle=5
		spring.redis.jedis.pool.max-active=20
		spring.redis.jedis.pool.max-wait=-1ms
		
	2.以json序列化
		@Configuration
		public class RedisConfig {
			//对于显示操作生效
			@Bean
			public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory factory) {
				RedisTemplate<Object, Object> template = new RedisTemplate<>();
				template.setConnectionFactory(factory);

				StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
				template.setKeySerializer(stringRedisSerializer); //key

				GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
				template.setValueSerializer(jsonRedisSerializer); //value
				template.setHashValueSerializer(jsonRedisSerializer);
				return template;
			}

			//对于注解生效,如 @Cacheable
			@Bean
			public RedisCacheConfiguration redisCacheConfiguration() {
				// Jackson2JsonRedisSerializer<Object> redisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
				// ObjectMapper om = new ObjectMapper();
				// om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
				// om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
				// redisSerializer.setObjectMapper(om);

				GenericJackson2JsonRedisSerializer redisSerializer = new GenericJackson2JsonRedisSerializer();
				RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
				redisCacheConfiguration = redisCacheConfiguration.serializeValuesWith(
						RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer)
				).entryTtl(Duration.ofMinutes(30));
				return redisCacheConfiguration;
			}
		}
	3.显示操作DEMO
		@Test
		public void test00() {
			Student student = new Student(1, "009");
			redisTemplate.opsForValue().set("student", student, 5, TimeUnit.SECONDS); //有效期5s,写入

			Student student = (Student) redisTemplate.opsForValue().get("student");
			System.out.println("student: " + student.getName()); //读取
		}
	
	4.注解DEMO
		@Override
		@Cacheable(value = "student", key = "'id-' + #p0")
		public Student selStudentById(int id) {
			return helloMapper.selStudentById(id);
		}
		
		@Test
		public void test() {
			System.out.println(helloService.selStudentById(2)); //Person implements Serializable
			System.out.println(helloService.selStudentById(2)); //第二次不读库
		}
	
	5.GenericJackson2JsonRedisSerializer VS Jackson2JsonRedisSerializer
		(1).G* 比 J* 效率低,占用内存高
		
		(2).使用 G* 序列化时,会保存序列化对象的全类名, 即'@class'
			反序列化时以此标识就可以反序列化成指定的对象. 如下所示
	
			{ //redis中数据
			  "@class": "com.example.spring.bean.Student",
			  "id": 6,
			  "name": "张四",
			  "teacher": null
			}
		
		

//}

//{------------------<<<基础概念>>>--------------------------------------------------------
0.应用场景
	#高频热点数据 -> 频繁访问数据库,数据库压力过大
	#临时性的数据 -> 手机号发送的验证码,三分钟有效,过期删掉
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-cache</artifactId>
        </dependency>
		
	#全局注解 - @EnableCaching
	#默认使用缓存 - ConcurrentMapCacheManager

1.抽象概念
	Java Caching 定义了5个核心接口: CachingProvider, CacheManager, Cache, Entry 和 Expiry
		//CacheManager, Cache, Entry: 都是一对多的关系!
		
		'CachingProvider'(数据库): 定义创建,配置,获取,管理和控制多个CacheManager. 一个应用在运行期访问多个CachingProvider.
		'CacheManager'(数据表): 定义创建,配置,获取,管理和控制多个唯一命名的Cache,
			这些Cache存在于CacheManager的上下文中. 一个CacheManager仅被一个CachingProvider所拥有.
		'Cache'(数据行): 一个类似Map的数据结构,并临时存储以Key为索引的值. 一个Cache仅被一个CacheManager所拥有.	
		'Entry'(数据字段): 一个存储在Cache中的key-value对.
	
		'Expiry': 每一个存储在Cache中的条目有一个定义的有效期. 一旦过期,条目将不可访问,更新和删除.
			缓存有效期可以通过ExpiryPolicy设置.

2.相关概念
	'cacheManager/cacheResolver': 缓存管理器, 默认使用ConcurrentMapCacheManager
	
	'cache': CacheManager管理多个Cache对象, 对缓存的真正CRUD操作是基于Cache对象的. 默认使用 ConcurrentMapCache
			每个Cache对象都有唯一一个名字.

	'value/cacheNames': 缓存的名字,可以指定多个; 
			(value = {"people","emp"}) //方法执行结果同时放入两个缓存中
	
	'key': 缓存数据使用的Key; "默认使用方法参数对应的值". 也可以使用SpEL表达式自定义 //详见附表			
	
	'keyGenerator': key值生成器, key和keyGenerator二选一! 默认使用keyGenerator (具体实现为: SimpleKeyGenerator). //自定义见附表
			//SimpleKeyGenerator生成key的策略:
			//	方法无参 - key = new SimpleKey();
			//	一个参数 - key = 参数的值
			//	多个参数 - key = new SimpleKey(params);

	'condition': 指定缓存条件,符合条件才进行缓存! 
			(condition = “#id>0”) //id>0才进行缓存
			(condition = “#a0<100”) //第一个参数<100才进行缓存
			(condition = "null!=#result") //结果不为空才进行缓存

	'sync': 是否采用异步模式
	
2.Cacheable - 根据方法的请求参数对其结果进行缓存 - '适用于查询'

		@Cacheable(value = "people", key = "#id")
		@Override
		public Person getOneById(int id) {
			ConcurrentMapCache
			Person person = personMapper.getOneById(id);
			logger.info("缓存为空，直接读库---args: {} - {}", id, person);
			return person;
		}
	
	//工作流程 - 目标方法调用之前,先检查缓存,有则返回; 无则调用,并将执行结果放入缓存
	//	(1).使用 cacheManager 通过 value/cacheNames 找到对应的 cache对象 
	//	(2).缓存key值使用 keyGenerator, 默认使用 SimpleKeyGenerator.
	
		1. 方法调用之前,先根据 value/cacheNames 从CacheManager中查找对应的Cache对象,没有则新建
			public class ConcurrentMapCacheManager {
				public Cache getCache(String name) { //参数为 value/cacheNames
					Cache cache = this.cacheMap.get(name);
					if (cache == null && this.dynamic) { //单例模式
						synchronized (this.cacheMap) {
							cache = this.cacheMap.get(name);
							if (cache == null) {
								cache = createConcurrentMapCache(name); //新建Cache对象
								this.cacheMap.put(name, cache); //放入Map集合
							}
						}
					}
					return cache;
				}
			}

		2. 根据 key 从Cache对象的 ConcurrentMap<Object, Object> store 中查找对应的value值
			public class ConcurrentMapCache{
				protected Object lookup(Object key) {
					return this.store.get(key);
				}
			}
	
		3. 上一步的value有值则直接返回,没有则调用目标方法,并将结果保存到 cache 对象的store中
			public class ConcurrentMapCache{
				public void put(Object key, @Nullable Object value) {
					this.store.put(key, toStoreValue(value));
				}
			}
			
#缓存key值 - 根据SpEL表达式生成

	名字			位置					描述											示例
	method			root object				当前被调用的方法								#root.method.name
	methodName		root object				当前被调用的方法名								#root.methodName+'['+#id+']' //key为: getOneById[66]
	target			root object				当前被调用的目标对象							#root.target
	targetClass		root object				当前被调用的目标对象类							#root.targetClass
	args			root object				当前被调用的方法的参数列表						#root.args[0]
		
	caches			root object				当前方法调用使用的缓存列表						#root.caches[0].name
											(value={"cache1", "cache2"})//两个cache		
																							
	argument name	evaluation context		方法参数的名字									#iban; #a0; #p0
											可 #参数名,也可 #p0或#a0 的形式,0 为参数索引	
											 												
	result			evaluation context		方法执行后的返回值								#result
											(仅当方法执行之后的判断有效,如unless, 				
											cache put表达式, cache evict表达式, beforeInvocation=false)
#自定义 KeyGenerator

		@Configuration
		public class CacheConfig {

			@Bean(value = "myKeyGenerator") //自定义 KeyGenerator
			public KeyGenerator keyGenerator() {
				//lamda表达式,效果相同
				//return (target, method, params) -> method.getName() + "[" + Arrays.asList(params) + "]";
				
				return new KeyGenerator() {

					@Override
					public Object generate(Object target, Method method, Object... params) {
						return method.getName() + "[" + Arrays.asList(params) + "]";
					}
				};
			}
		}
		
		//使用自定义
		@Cacheable(value = "people", keyGenerator = "myKeyGenerator")
	
3.CachePut - 既调用方法,又更新缓存 - '适用于更新'
	
		@CachePut(value = "people", key = "#person.id")
		@Override
		public void updateOneById(Person person) {
			logger.info("更新缓存---args: {}", person);
			personMapper.updateOneById(person);
		}

		//此时若不指定 @CachePut 的value和key,则不能更新 @Cacheable 的缓存. 即查询到的仍旧是更新前的数据.
		//这是因为默认的 key 为方法参数对应的值,即 @CachePut 是以 Person对象 作为缓存的key
		
		//也可以这样指定: @CachePut(value = "people", key = "#result.id")
		//但是, @Cacheable 则不可以!!!! 因为后者 #result.id 是在方法调用之前取值,为null,报错
	
4.CacheEvict - 清空缓存 - '适用于删除'
	
		@CacheEvict(value = "people"/*, allEntries = true*/) //默认key为方法参数值
		@Override
		public int deleteOneById(int id) {
			logger.info("删除缓存---args: {}", id);
			return personMapper.deleteOneById(id);
		}
	
		// allEntries = true 代表清空 value 对应的所有缓存; 默认为false
		// beforeInvocation = true 代表清空动作在方法调用之前; 默认为false. (默认情况下,方法调用出错,则缓存不会清空)
	
5.Caching - 复杂场景使用的组合注解

		@Caching(
				cacheable = {
						@Cacheable(value = "people", key = "#result.name")
				},
				put = {
						@CachePut(value = "people", key = "#result.id"),
						@CachePut(value = "emp", key = "@result.id")
				}
		)
		public int getOneByName(String name) {
			logger.info("增加缓存---args: {}", name);
			return personMapper.deleteOneById(1);
		}
		
		//方法调用后,将结果保存到 people:<name,VALUE>; people:<id,VALUE>; emp:<id,VALUE>; 三组缓存中!!!
		
		//此时,根据 value = "people" 的 id 查找,直接从缓存读取.
		//但是,根据 value = "people" 的 name 查找,则会调用方法,这是因为组合注解中包含 put 的缘故!
	
6.CacheConfig - 将类中 缓存共有配置 抽取到类的全局注解

	//对于类中缓存配置与类全局定义不一致者,可显示定义
	@CacheConfig(cacheNames = "people")
	public class PersonServiceImpl { }
	
//}
	
	
//{--------<<<淘汰算法>>>------------------------------------------------------------------
#LRU(least_recently_used) - ///最近最少使用
	//将最近使用的条目存放到缓存的顶部位置; 达到缓存极限时,从底部开始移除.		
	这里会使用到昂贵的算法,而且它需要记录"年龄位"来精确显示条目是何时被访问的.
	此外,当一个LRU缓存算法删除某个条目后,"年龄位"将随其他条目发生改变.

	[]; 'A'->[A]; 'B'->[B,A]; 'C'->[C,B,A]; 'D'->[D,C,B,A]; 'C'->[C,D,B,A]; 'E'->[E,C,D,B]
	//缓存容量4,初始为空. 访问A则缓存为[A]; ... 当再次访问C时,将C提到首位; 最后访问E,由于缓存已达上限,则将最后的A移除
	
#LFU(least frequently used) - ///最不经常使用
	'使用一个计数器来记录条目被访问的频率,最低访问频率的条目首先被移除'.
	此算法并不经常使用, 因为它无法对一个拥有最初高访问率但之后长时间没有被访问的条目缓存负责.
	
	[A-32,B-30,C-26,D-26]; 'D'->[A-32,B-30,D-27,C-26]; 'B'->[A-32,B-31,D-27,C-26]; 'F'->[A-32,B-31,D-27,F-1]
	//首先访问D,则D的频率+1,并和C调换位置; 再访问B,将B频率+1; 最后访问F,由于容量为4,则必须将末位C移除,并将F加入,评率设为1
		
#FIFO(first in first out) - ///先进先出
	与普通存储器的区别是没有外部读写地址线,这样使用起来非常简单,
	但缺点就是只能顺序写入数据,顺序的读出数据,其数据地址由内部读写指针自动加1完成,
	不能像普通存储器那样可以由地址线决定读取或写入某个指定的地址
		
#MRU(most recently used) - ///最近最常使用
	'最先移除最近最常使用的条目'. 一个MRU算法擅长处理一个条目越久,越容易被访问的情况
	
//}	
///-------------------<<<附录>>>--------------------------------------------------------------------

	
	
	