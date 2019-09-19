

# 异常日志

##异常处理

```sh
#【强制】Java 类库中定义的可以通过预检查方式规避的 RuntimeException 异常不应该通过catch 的方式来处理，
--比如： NullPointerException ， IndexOutOfBoundsException 等等。
说明：无法通过预检查的异常除外，比如，在解析字符串形式的数字时，不得不通过 catch NumberFormatException 来实现。

正例：if (obj != null) {...}
反例：try { obj.method(); } catch (NullPointerException e) {…}

#【强制】异常不要用来做流程控制，条件控制
说明：异常设计的初衷是解决程序运行中的各种意外情况，且异常的处理效率比条件判断方式要低很多

#【强制】catch 时请分清稳定代码和非稳定代码，稳定代码指的是无论如何不会出错的代码。
--对于非稳定代码的 catch 尽可能进行区分异常类型，再做对应的异常处理。
说明：对大段代码进行 try-catch，使程序无法根据不同的异常做出正确的应激反应，也不利于定位问题，这是一种不负责任的表现。

正例：用户注册的场景中，如果用户输入非法字符，或用户名称已存在，或用户输入密码过于简单，在程序上作出分门别类的判断，并提示给用户。

#【强制】捕获异常是为了处理它，不要捕获了却什么都不处理而抛弃之。如果不想处理它，请将该异常抛给它的调用者。
--最外层的业务使用者，必须处理异常，将其转化为用户可以理解的内容。
 
#【强制】有 try 块放到了事务代码中， catch 异常后，如果需要回滚事务，一定要注意手动回滚事务。

#【强制】 finally 块必须对资源对象、流对象进行关闭，有异常也要做 try - catch 。
说明：如果 JDK7 及以上，可以使用 try-with-resources 方式

#【强制】不要在 finally 块中使用 return
说明： finally 块中的 return 返回后方法结束执行，不会再执行 try 块中的 return 语句。

#【强制】捕获异常与抛异常，必须是完全匹配，或者捕获异常是抛异常的父类（不能抛小了）。
说明：如果预期对方抛的是绣球，实际接到的是铅球，就会产生意外情况。
```

```sh
#【推荐】方法的返回值可以为 null ，不强制返回空集合，或者空对象等，必须添加注释充分说明什么情况下会返回 null 值。
说明：本手册明确防止 NPE 是调用者的责任。即使被调用方法返回空集合或者空对象，对调用者来说，也并非高枕无忧，
必须考虑到远程调用失败、序列化失败、运行时异常等场景返回 null 的情况。

#【推荐】定义时区分 unchecked/checked 异常，避免直接抛出 new RuntimeException()，更不允许抛出 Exception 或者 Throwable，
--应使用有业务含义的自定义异常。'推荐业界已定义过的自定义异常'，如：DAOException/ServiceException 等。

#【参考】对于公司外的 http/api 开放接口必须使用“错误码”；而应用内部推荐异常抛出；
#-------跨应用间 RPC 调用优先考虑使用 Result 方式，封装 isSuccess()方法 、“错误码”、“错误简短信息”。
说明：关于 RPC 方法返回方式使用 Result 方式的理由：
（1） 使用抛异常返回方式，调用方如果没有捕获到就会产生运行时错误。
（2） 如果不加栈信息，只是 new 自定义异常，加入自己的理解的 error message，对于调用端解决问题的帮助不会太多。
-----如果加了栈信息，在频繁调用出错的情况下，数据序列化和传输的性能损耗也是问题。

#【推荐】防止 NPE，是程序员的基本修养，注意 NPE 产生的场景：
（1）返回类型为基本数据类型，return 包装数据类型的对象时，自动拆箱有可能产生 NPE 。
----反例： public int f()  {  return Integer 对象}， 如果为 null ，自动解箱抛 NPE 。
（2）数据库的查询结果可能为 null
（3）集合里的元素即使 isNotEmpty，取出的数据元素也可能为 null
（4）远程调用返回对象时，一律要求进行空指针判断，防止 NPE
（5）对于 Session 中获取的数据，建议 NPE 检查，避免空指针
（6）级联调用 obj.getA().getB().getC()； 一连串调用，易产生 NPE

----正例：使用 JDK8 的 Optional 类来防止 NPE 问题
```

## 日志规约

```sh
#【强制】对 trace/debug/info 级别的日志输出，必须使用条件输出形式或者使用占位符的方式
说明：logger.debug("Processing trade with id: " +  id + " and symbol: " +  symbol);
如果日志级别是 warn，上述日志不会打印，但是会执行字符串拼接操作。
如果 symbol 是对象，会执行 toString() 方法，浪费了系统资源，执行了上述操作，最终日志却没有打印。
```

```java
//正例：（条件）建设采用如下方式
if (logger.isDebugEnabled()) {
    logger.debug("Processing trade with id: " + id + " and symbol: " + symbol);
}
//正例：（占位符）
logger.debug("Processing trade with id: {} and symbol : {} ", id, symbol);
```

```sh
#【强制】应用中不可直接使用日志系统（log4j、logback）中的 API，而应依赖使用日志框架 slf4j 中的 API
--使用门面模式的日志框架，有利于维护和各个类的日志处理方式统一。
private static final Logger logger = LoggerFactory.getLogger(Abc.class);

#【强制】异常信息应该包括两类信息：案发现场信息和异常堆栈信息。如果不处理，那么通过关键字 throws 往上抛出
正例： logger.error(各类参数或者对象 toString() + "_" + e.getMessage(), e);

#【强制】日志文件至少保存 15 天，因为有些异常具备以“周”为频次发生的特点

#【强制】应用中的扩展日志（如打点、临时监控、访问日志等）命名方式：appName_logType_logName.log
--logType：日志类型，如 stats/monitor/access 等 ；logName：日志描述。
这种命名的好处：通过文件名就可知道日志文件属于什么应用，什么类型，什么目的，也有利于归类查找。

正例： mppserver 应用中单独监控时区转换异常，如：mppserver_monitor_timeZoneConvert.log
说明：推荐对日志进行分类，如将错误日志和业务日志分开存放，便于开发人员查看，也便于通过日志对系统进行及时监控

#【强制】避免重复打印日志，浪费磁盘空间，务必在 log4j.xml 中设置 additivity = false
正例： <logger name="com.taobao.dubbo.config" additivity="false">

#【推荐】谨慎地记录日志。生产环境禁止输出 debug 日志；有选择地输出 info 日志；
--如果使用 warn 来记录刚上线时的业务行为信息，一定要注意日志输出量的问题，避免把服务器磁盘撑爆，并记得及时删除这些观察日志。
说明：大量地输出无效日志，不利于系统性能提升，也不利于快速定位错误点。
记录日志时请思考：这些日志真的有人看吗？看到这条日志你能做什么？能不能给问题排查带来好处？

#【推荐】可以使用 warn 日志级别来记录用户输入参数错误的情况，避免用户投诉时，无所适从。如非必要，请不要在此场景打出 error 级别，避免频繁报警。
说明：注意日志输出的级别，error 级别只记录系统逻辑出错、异常或者重要的错误信息。
```

```

```



# 数据库



## ORM映射

```java
获取多个对象的方法用 'list' 做前缀，复数形式结尾如：listObjects
获取单个对象的方法用 'get' 做前缀
插入的方法用 save/'insert' 做前缀
修改的方法用 'update' 做前缀
删除的方法用 remove/'delete' 做前缀
获取统计值的方法用 'count' 做前缀
```

```sh
#'1.强制' 在表查询中，一律不要使用 * 作为查询的字段列表，需要哪些字段必须明确写明。
-- 说明：(1).增加查询分析器解析成本。(2).增减字段容易与 resultMap 配置不一致。

#'2.强制' POJO 类 boolean属性不能加 is，而数据库字段必须加 is_，要求在 resultMap 中进行字段与属性的映射。
-- 说明：参见定义 POJO 类以及数据库字段定义规定，在 sql.xml 增加映射，是必须的。

#'3.强制' 不要用 resultClass 当返回参数，即使所有类属性名与数据库字段一一对应，也需要定义；反过来，每一个表也必然有一个与之对应。
-- 说明：配置映射关系，使字段与 DO 类解耦，方便维护。

#'4.强制' xml 配置中参数注意使用：#{}，#param# 不要使用${}，此种方式容易出现 SQL 注入。

#'5.强制' iBATIS 自带的 queryForList(String statementName, int start, int size) 不推荐使用。
-- 说明：此方法在数据库取到 statementName 对应的SQL语句的所有记录，再通过 subList 取 start,size 的子集合，线上因为这个原因曾经出现过 OOM

-- 正例：在 sqlmap.xml 中引入 #start#, #size#
Map<String, Object> map = new HashMap<String, Object>();
map.put("start", start);
map.put("size", size);

#'6.强制' 不允许直接拿 HashMap 与 Hashtable 作为查询结果集的输出。
-- 反例：某同学为避免写一个<resultMap>，直接使用 HashMap 来接收数据库返回结果，
--      结果出现异常是把 bigint 转成 long 值，而线上由于数据库版本不一致，，解析成 BigInteger，导致线上问题。

#'7.强制' 更新数据表记录时，必须同时更新记录对应的 gmt_modified 字段值为当前时间。（gmt 格林威治时间）

#'8.推荐' 不要写一个大而全的数据更新接口，传入为 POJO 类，不管是不是自己的目标更新字段，
---都进行 update table set c1=value1, c2=value2, c3=value3; 这是不对的。
-- 执行 SQL时，尽量不要更新无改动的字段，一是易出错； 二是效率低； 三是 binlog 增加存储。

#'9.参考' @Transactional 事务不要滥用。事务会影响数据库的 QPS。
另外使用事务的地方需要考虑各方面的回滚方案，包括缓存回滚、搜索引擎回滚、消息补偿、统计修正等。

#'10.参考' <isEqual>中的 compareValue 是与属性值对比的常量，一般是数字，表示相等时带上此条件；
<isNotEmpty>表示不为空且不为 null 时执行； <isNotNull>表示不为 null 值时执行。
```





#Dubbo

## ZooKeeper

> 基础概念

```shell
ZooKeeper 是一个分布式的，开放源码的分布式应用程序协调服务。它是一个为分布式应用提供一致性服务的软件，
提供的功能包括：配置维护、域名服务、分布式同步、组服务等。
```



> 安装配置

```shell
docker pull zookeeper

#镜像需要端口 2181 2888 3888（客户端端口，从机端口，选举端口）
docker run --name zk01 --restart always -d -p 2181:2181 zookeeper
```

## 基础概念

> 将服务提供者注册到注册中心

```shell
引入 dubbo 和 zk 依赖
配置 dubbo 扫描宝和注册中心地址
使用 @Service（dubbo的注解） 发布服务
```

>服务消费者消费服务

```shell
引入 dubbo 和 zk 依赖
配置 dubbo 的注册中心地址
将服务提供者的接口定义拷贝到消费者
引入服务 @Reference
```











