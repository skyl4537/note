



# JSON

## 创建

>JSON_OBJECT：生成 JSON 对象。如果有KEY为 NULL 或参数个数为奇数，则抛错

```sql
mysql> SET @j = (SELECT JSON_OBJECT('age', 20, 'birthday', now(), 'datas', (SELECT JSON_OBJECT('name', 'lucy'))) json);

mysql> SELECT @j;
+----------------------------------------------------------------------------------+
| @j                                                                               |
+----------------------------------------------------------------------------------+
| {"age": 20, "datas": {"name": "lucy"}, "birthday": "2019-08-02 09:17:03.000000"} |
+----------------------------------------------------------------------------------+
```

> JSON_ARRAY：生成 JSON 数组

```sql
mysql> SELECT JSON_ARRAY(JSON_OBJECT('age',20,'name','lucy'), JSON_OBJECT('age',18,'name','lily')) json_arrary;
+------------------------------------------------------------+
| json_arrary                                                |
+------------------------------------------------------------+
| [{"age": 20, "name": "lucy"}, {"age": 18, "name": "lily"}] |
+------------------------------------------------------------+
```

## 解析

> JSON_VALID：检验 JSON 是否合法

```sql
mysql> SELECT JSON_VALID('{"a": "1", "b"}');
+-------------------------------+
| JSON_VALID('{"a": "1", "b"}') |
+-------------------------------+
|                             0 |
+-------------------------------+
```

> JSON_TYPE：查询 JSON 的类型，是 JSON-OBJECT 还是 JSON-ARRARY。

```sql
mysql> SELECT JSON_TYPE('{"a": "1"}');
+-------------------------+
| JSON_TYPE('{"a": "1"}') |
+-------------------------+
| OBJECT                  |
+-------------------------+
```

```sql
mysql> SELECT JSON_TYPE('[{"a": "1"}, {"B": 2}]');
+-------------------------------------+
| JSON_TYPE('[{"a": "1"}, {"B": 2}]') |
+-------------------------------------+
| ARRAY                               |
+-------------------------------------+
```

>JSON_CONTAINS_PATH：查询 KEY 值是否存在。存在返回1，否则返回0

```sql
-- JSON_CONTAINS_PATH(json_doc, one_or_all, path[, path] ...)：参数2，只能取值 one/all。参数3是变长，可查单个key，也可查多个key
```

```sql
-- 查询单个key时，参数2可以是 one/all。
mysql> SELECT JSON_CONTAINS_PATH(@j, 'one', '$.age');
+----------------------------------------+
| JSON_CONTAINS_PATH(@j, 'one', '$.age') |
+----------------------------------------+
|                                      1 |
+----------------------------------------+
```

```sql
-- 查询多个key时，参数2根据情况选择。one：表示只要有一个存在即可；all：表示所有的都存在才行。
mysql> SELECT JSON_CONTAINS_PATH(@j, 'one', '$.age', '$.datas.address');
+-----------------------------------------------------------+
| JSON_CONTAINS_PATH(@j, 'one', '$.age', '$.datas.address') |
+-----------------------------------------------------------+
|                                                         1 |
+-----------------------------------------------------------+

mysql> SELECT JSON_CONTAINS_PATH(@j, 'all', '$.age', '$.datas.address');
+-----------------------------------------------------------+
| JSON_CONTAINS_PATH(@j, 'all', '$.age', '$.datas.address') |
+-----------------------------------------------------------+
|                                                         0 |
+-----------------------------------------------------------+
```

>JSON_CONTAINS：查询 KEY-VALUE 键值对是否存在

```sql
-- JSON_CONTAINS(json_doc, val[, path])：参数1是待查json，参数2是value，参数3是key。

--【注意】参数3的 value 有些限制条件，比如正整数1，需要写成 '1'；如果是字符串"lucy"，要写成 '"lnmp"'。sql才是有效的
mysql> SELECT JSON_CONTAINS(@j, "lucy", '$.datas.name');
ERROR 3141 (22032): Invalid JSON text in argument 2 to function json_contains: "Invalid value." at position 0.

mysql> SELECT JSON_CONTAINS(@j, '"lucy"', '$.datas.name');
+---------------------------------------------+
| JSON_CONTAINS(@j, '"lucy"', '$.datas.name') |
+---------------------------------------------+
|                                           1 |
+---------------------------------------------+
```

> JSON_KEYS：列出所有的 KEY

```sql
-- 查询普通 JSON
mysql> SELECT JSON_KEYS(@j);
+------------------------------+
| JSON_KEYS(@j)                |
+------------------------------+
| ["age", "datas", "birthday"] |
+------------------------------+

-- 查询内嵌 JSON
mysql> SELECT JSON_KEYS(@j, '$.datas');
+--------------------------+
| JSON_KEYS(@j, '$.datas') |
+--------------------------+
| ["name"]                 |
+--------------------------+
```

> JSON_EXTRACT：解析JSON。`【注意】默认查询结果中，字符串类型带有引号`

```sql
-- 如果想去掉外层引号，配合 JSON_UNQUOTE 使用
mysql> SELECT JSON_EXTRACT(@j, '$.datas.name');
+----------------------------------+
| JSON_EXTRACT(@j, '$.datas.name') |
+----------------------------------+
| "lucy"                           |
+----------------------------------+
```

> JSON_UNQUOTE：去掉 VALUE 值的外层引号

```sql
-- 类比于上个查询，外层无引号
mysql> SELECT JSON_UNQUOTE(JSON_EXTRACT(@j, '$.datas.name'));
+------------------------------------------------+
| JSON_UNQUOTE(JSON_EXTRACT(@j, '$.datas.name')) |
+------------------------------------------------+
| lucy                                           |
+------------------------------------------------+
```



##操作

> JSON_INSERT：追加 KEY，KEY 不存在，才能插入。`以下操作均生成新的JSON，不会影响原有的JSON`

```sql
-- 只插入了 id。因为 age 已存在，所以未插入。
mysql> SELECT JSON_INSERT(@j, '$.age', 18, '$.id', '001');
+-----------------------------------------------------------------------------------------------+
| JSON_INSERT(@j, '$.age', 18, '$.id', '001')                                                   |
+-----------------------------------------------------------------------------------------------+
| {"id": "001", "age": 20, "datas": {"name": "lucy"}, "birthday": "2019-08-02 09:17:34.000000"} |
+-----------------------------------------------------------------------------------------------+
```

>JSON_SET：直接插入，不管 KEY 是否存在

```sql
mysql> SELECT JSON_SET(@j, '$.age', 18, '$.id', '001');
+-----------------------------------------------------------------------------------------------+
| JSON_SET(@j, '$.age', 18, '$.id', '001')                                                      |
+-----------------------------------------------------------------------------------------------+
| {"id": "001", "age": 18, "datas": {"name": "lucy"}, "birthday": "2019-08-02 09:17:34.000000"} |
+-----------------------------------------------------------------------------------------------+
```

>JSON_REMOVE：删除指定 KEY，若 KEY 不存在则忽略

```sql
mysql> SELECT JSON_REMOVE(@j, '$.age', '$.gender');
+-----------------------------------------------------------------------+
| JSON_REMOVE(@j, '$.age', '$.gender')                                  |
+-----------------------------------------------------------------------+
| {"datas": {"name": "lucy"}, "birthday": "2019-08-02 09:17:34.000000"} |
+-----------------------------------------------------------------------+
```

>JSON_REPLACE：替换 KEY 对应的 VALUE 值，KEY 不存在则忽略

```sql
mysql> SELECT JSON_REPLACE(@j, '$.age', 18, '$.gender', '女');
+----------------------------------------------------------------------------------+
| JSON_REPLACE(@j, '$.age', 18, '$.gender', '女')                                  |
+----------------------------------------------------------------------------------+
| {"age": 18, "datas": {"name": "lucy"}, "birthday": "2019-08-02 09:17:34.000000"} |
+----------------------------------------------------------------------------------+
```

##结合表

>和数据字段相结合使用。 `注意语法糖 ->`

```sql
mysql> SELECT memo FROM log WHERE id=470941;
+------------------------------------------------------------------------------+
| memo                                                                         |
+------------------------------------------------------------------------------+
| {"age": 20, "time": "2019-07-30 16:55:32.000000", "datas": {"name": "lucy"}} |
+------------------------------------------------------------------------------+
```

```sql
-- 解析 JSON 类型字段中的某个 KEY
mysql> SELECT JSON_UNQUOTE(JSON_EXTRACT(memo, '$.datas.name')) name FROM log WHERE id=470941;
+------+
| name |
+------+
| lucy |
+------+
```

```sql
-- 【注意】JSON 解析的 -> 语法。参数1只能为数据表中的字段，不能为 JSON字符串 或 表达式。
mysql> SELECT JSON_UNQUOTE(memo -> '$.datas.name') name FROM log WHERE id=470941;
+------+
| name |
+------+
| lucy |
+------+
```





# 事务

##基础概念

> 存储引擎只有 Innodb 支持事务

```sql
-- 事务保证了数据的 完整性 和 一致性
事务是由一组 sql 语句组成，这些sql语句要么都执行成功，要么都执行失败

show engines; --查看当前数据库支持的存储引擎
```

> 隐式事务 和 显式事务

```sql
-- 【隐式事务】事务没有明显的开启和关闭标识。例如，普通的sql语句：INSERT，UPDATE，DELETE
```

```sql
--【显示事务】必须先设置《自动提交》功能为禁用状态

SHOW VARIABLES LIKE 'autocommit'; -- 查看《自动提交》功能是否开启

SET autocommit=0; -- 关闭《自动提交》，只针对当前会话起作用。所以每条事务都要以这条语句开始

START TRANSACTION; -- 可选语句，开始事务
UPDATE trans SET fmoney=fmoney-700 WHERE fname='小李'; -- 事务正文sql
UPDATE trans SET fmoney=fmoney+700 WHERE fname='老王';
COMMIT; -- 提交事务
-- ROLLBACK; --回滚事务，与提交事务，二选一
```

> ACID

```sql
-- 原子性（Atomicity）
事务中涉及到的多个操作在逻辑上缺一不可，'不可再分'。要么都执行成功，要么都执行失败。
```

```sql
-- 一致性（Consistency）
事务执行会使数据从一个一致性状态变换到另外一个一致性状态（转账之前和之后，金钱总额不变）。
执行过程中，如果某一个或某几个操作失败了，则必须将其他所有操作撤销，将数据恢复到事务执行之前的状态，这就是'回滚'。
```

```sql
-- 隔离性（Isolation）
事务往往是并发执行的，所以很有可能有许多事务同时处理相同的数据，因此每个事务都应该与其他事务隔离开来，防止数据损坏。
隔离性原则要求'多个事务在并发执行过程中不会互相干扰'。

隔离级别：读未提交（Read uncommitted）、读提交（read committed）、可重复读（repeatable read）和串行化（Serializable）。
```

```sql
-- 持久性（Durability）
一个事务一旦提交，则会永久的改变数据库中的数据，不能撤销。比如，删除一条数据。
通常情况下， 事务对数据的修改应该被'写入到持久化存储器中'。
```

##Boot配置

> 

```sql

```

>

```sql

```

>

```sql

```

## 隔离级别

> 数据库事务的并发问题

```sql
-- 脏读（更新场景） --> T2更新 T1读 T2回滚

T1 将某条记录的 AGE 值从 20 修改为 30，但未提交。
T2 读取了 T1 更新后的值：30。
T1 回滚，AGE 值恢复到了 20。
T2 读取到的 30 就是一个无效的值。
```

```sql
-- 不可重复读 --> T1读 T2更新提交 T1再读

T1 读取了 AGE 值为 20。
T2 将 AGE 值修改为 30。
T1 再次读取 AGE 值为 30，和第一次读取不一致。
```

```sql
-- 幻读（新增场景） --> T1读 T2新增 T1读

T1 读取了 STUDENT 表中的一部分数据。
T2 向 STUDENT 表中插入了新的行。
T1 读取了 STUDENT 表时，多出了一些行。
```

> 事务的隔离级别要得到底层数据库引擎的支持，而不是应用程序或者框架的支持。`mysql支持4种 > oracle的2种`

```sql
-- READ_UNCOMMITTED（读未提交）：以上③种问题都可能发生
允许 T1 读取 T2 未提交的修改。
```

```sql
-- READ_COMMITTED（读提交）：只可以避免【脏读】
要求 T1 只能读取 T2 已提交的修改。
```

```sql
-- REPEATABLE_READ（可重复读）:可以避免【脏读】+【不可重复读】。默认配置
执行 T1 期间，禁止其它事务对这个字段进行更新，确保 T1 可以多次从一个字段中读取到相同的值。
```

```sql
-- SERIALIZABLE（串行化）：以上③个问题都可以避免，但效率低。不推荐
在 T1 执行期间，禁止其它事务对这个表进行添加、更新、删除操作。可确保 T1 可以多次从一个表中读取到相同的行，避免任何并发问题，但性能十分低下。

锁定整个范围的键，并一直持有锁，直到事务完成。

在 REPEATABLE_READ 的基础上，增加了在事务完成之前，其他事务不能向事务已读取的范围'插入新行'的限制。
```

> 设置隔离级别

```sql
SELECT @@tx_isolation; -- 查看当前的隔离级别。默认是：可重复读（repeatable read）

SET transaction isolation level repeatable read; -- 设置隔离级别，仅对当前连接起效
SET global transaction isolation level read committed; -- 设置全局的隔离级别
```

## 事务管理

>编程式事务管理器：使用原生JDBC

```sql
--> （1）获取数据库连接 Connection 对象 --> （2）取消事务的自动提交 --> （3）执行操作
--> （4）正常完成操作时手动提交事务 --> （4）执行失败时回滚事务
--> （5）关闭相关资源
```

```sql
编程式事务管理：需要将事务管理代码'嵌入到业务方法中'来控制事务的提交和回滚。

在使用编程的方式管理事务时，必须在每个事务操作中包含额外的事务管理代码。相对于'核心业务'而言，事务管理的代码显然属于'非核心业务'，
如果多个模块都使用同样模式的代码进行事务管理，显然会造成较大程度的'代码冗余'。
```

>声明式事务管理器

```sql
声明式事务管理：将事务管理代码从业务方法中分离出来，以声明的方式来实现事务管理。

事务管理代码的固定模式作为一种横切关注点，可以通过 AOP 方法模块化，进而借助'Spring AOP'框架实现声明式事务管理。

Spring 在不同的事务管理 API 之上定义了一个 抽象层，通过 配置的方式使其生效。
从而让应用程序开发人员 不必了解事务管理 API 的底层实现细节，就可以使用 Spring 的事务管理机制。
```

```sql
Spring 的核心事务管理抽象是'PlatformTransactionManager'。它为事务管理封装了一组独立于技术的方法。

-- DataSourceTransactionManager：在应用程序中只需要处理一个数据源，而且通过 JDBC 存取。（常用）
-- JtaTransactionManager： 在 JavaEE 应用服务器上用 JTA(Java Transaction API)进行事务管理
-- HibernateTransactionManager：用 Hibernate 框架存取数据库
```

> XML配置

```xml
<!-- 配置事务管理器 -->
<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <property name="dataSource" ref="dataSource" />
</bean>

<!-- 开启事务注解 -->
<tx:annotation-driven transaction-manager="transactionManager" />
```

```java
//Spring Boot 使用的全局注解
@EnableTransactionManagement
```

```java
//在需要进行事务控制的方法上加注解
@Transactional
public void buyBook(int bookId, int bookCount) {
    //（1）获取书籍信息（单价，库存）
    Book book = helloMapper.findBookById(bookId);
    
    //（2）扣除书籍库存
    boolean update = helloMapper.updateBookStock(bookId, bookStock);

    //（3）扣除用户金额
    update = helloMapper.updateUserAccount(1, userAccount);
}
```

##传播行为

> 当事务方法被另一个事务方法调用时，必须指定事务应该如何传播。

```java
//默认配置。如果有事务正在运行，当前方法就在这个事务内运行，否则，就启动一个新的事务运行。
@Transactional(propagation = Propagation.REQUIRED)

当 bookService 的 purchase()方法被另一个事务方法 checkout()调用时，它默认会在现有的事务内运行。
因此在 checkout()方法的开始和终止边界内只有一个事务。
这个事务只在 checkout()方法结束的时候被提交，结果用户一本书都买不了。
```

```java
//当前的方法必须启动新的事务运行，如果有事务正在运行，就将它挂起。
@Transactional(propagation = Propagation.REQUIRES_NEW)
```

