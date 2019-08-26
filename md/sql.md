

# 基础命令

# 名词

> 名词解析

```sql
DB    --Data-Base，数据库，存储一系列有组织的数据
DBMS  --Data-Base-Manage-System，数据库管理系统软件，如：Mysql，Oracle，sql-server
SQL   --Structure-Query-Language，结构化查询语言，与数据库通信
```

> 基础语法

```sql
不区分大小写。建议关键字大写，表名和列名小写。每条命令使用分号结尾。单引号和双引号都可以表示字符串。

mysql的字段名、表名通常不需要加任何引号，如果非要加上引号，必须加反引号。

mysql的别名可不加引号，如果加，单引号和双引号以及反引号都可以。别名含有特殊字符，则必须使用双引号。
```

>赋予权限

```sql
-- GRANT [权限内容] ON [库名.表名] TO [用户名@'IP地址'] IDENTIFIED BY ['密码']WITH GRANT OPTION;
-- 赋予用户名 MAO，密码 MIAOMIAO 的用户可以在任意设备上操作所有数据库表的权限
GRANT ALL PRIVILEGES ON *.* TO MAO@'%' IDENTIFIED BY 'MIAOMIAO' WITH GRANT OPTION;
```

> linux环境mysql日志配置在`my.cnf 或 mysql.cnf`

```shell
find / -name my.cnf(mysql.cnf) #查找这两个文件所在位置
log-error=/var/log/mysql.log   #在上述两个文件中配置
```



## DDL

> sql分类

```sql
DDL：Data-Define-Languge，数据定义语言。         --如，create，drop，alter
DML：Data-Manipulate-Language，数据操作语言。    --如，insert，update，delete
DQL：Data-Query-Language，数据查询语言。         --如，select

TCL：Transaction-Control-Language，事务控制语言。--如，commit，rollback  
```

> CREATE

```sql
CREATE TABLE `city` (
    `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `name` varchar(10) DEFAULT NULL COMMENT '用户名', --VARCHAR 记得指定长度
    PRIMARY KEY (`id`)
) COMMENT '用户';

SHOW CREATE TABLE city; --查看创建过程
DESC city;              --DESC 查看表的详细信息
```

> DROP

```sql
DROP TABLE IF EXISTS `city`; --先删除
```

> ALERT：`ADD|CHANGE|MODIFY|DROP`

```sql
ALTER TABLE tbName ADD COLUMN 列名 列类型 [列参数] [NOT NULL DEFAULT]  --增加列
ALTER TABLE tbName CHANGE COLUMN 旧列名 新列名 列类型 [列参数]          --修改列名（注意，新列的类型）
ALTER TABLE tbName MODIFY COLUMN 列名 新列类型 [列参数]                --修改列类型
ALTER TABLE tbName DROP COLUMN 列名;                                 --删除列

ALTER TABLE tbName RENAME TO newName; -- 修改表名
```

> LIKE：表的复制

```sql
CREATE TABLE city0 LIKE city; -- 仅复制表的整体结构

CREATE TABLE city1 SELECT id,name FROM city WHERE id=2; -- 复制表的部分结构 + 数据（id，name两列）
```

## DML

> INSERT

```sql
INSERT INTO city(id,name) VALUES --列与值要严格对应。不写则默认为所有列赋值
(DEFAULT,'晋州'),                 --自增主键赋值 DEFAULT，不指定的列默认赋值 NULL
(DEFAULT,'朔州');                 --支持批量插入
```

```sql
INSERT INTO city(id,name)
SELECT id,name FROM employee WHERE id=3; --将查询结果插入到表中
```

```sql
--(1).第一种方式支持批量插入，第二种不支持
--(2).第二种方式支持子查询，第一种不支持
```

> UPDATE

```sql
UPDATE city SET age=900 WHERE name='苏州'; --修改字段值，必须添加条件，否则全表修改
```

> DELETE

```sql
DELETE FROM city WHERE name='晋州'; --单表删除

DELETE city,student
FROM city JOIN student ON student.name=city.name
WHERE city.name='晋州';             --联表删除
```

```sql
DELETE FROM city;    -- 清空表1
TRUNCATE TABLE city; -- 清空表2

-- DELETE 可以加 WHERE 过滤条件，TRUNCATE 不可以。
-- DELETE 可以 rollback 回滚。TRUNCATE 速度更快，不可恢复，清空后释放内存。
-- DELETE 删除有返回值。TRUNCATE 删除没有。

-- DELETE 清空表后，添加新的数据时自增列接着自增。TRUNCATE 则是从1开始重新计数。
```





#基础函数

> 常用命令

```sql
-- [-h] 服务器ip; [-P] 端口号(默认3306，非默认则必须显示指定); [-u] 用户名; [-p] 密码（#$%_BC13439677375）
mysql -h 192.168.5.25 -P 33306 -u bluecardsoft -p
```

```sql
 select version();   -- 版本
 
 use test0806;       -- 切换数据库
 show tables;        -- 当前数据库下的所有表
 select database();  -- 当前所使用的数据库
 
 SELECT ROW_COUNT(); -- 返回受影响的行数
```

## 单行函数

> NULL

```sql
ISNULL(expr)  -- 表达式为 NULL，返回 1，否则返回 0
```

```sql
IFNULL(v1,v2) -- v1 为 NULL，返回 v2，否则返回 v1

IF(expr,v1,v2) -- 表达式成立，返回 v1；否则返回 v2
```

> CONCAT

```sql
SELECT 5 + 'aa'; -- 对于非数值的一方，作取0处理。最终结果：5
SELECT 5 + NULL; -- NULL和任何值相加，结果：NULL
```

```sql
SELECT CONCAT(5,'a') caoncat; -- 字符串拼接：'5a'
```

> BETWEEN 和 IN

```sql
BETWEEN 5 AND 10 <==> 5<= x <=10
IN(5, 10)        <==> x=5 || x=10
```

> CASE简单函数：SWITCH `（适合离散的等值条件）`

```sql
SELECT employee_id,
    (CASE sex
     WHEN '1' THEN '男'
     WHEN '0' THEN '女'
     ELSE '其他' END) sex
FROM employees
```

> CASE搜索函数：IF-ELSE `（适合逻辑逻辑条件）`

```sql
SELECT employee_id,
    (CASE
     WHEN sex=1 THEN '男'
     WHEN sex=0 THEN '女'
     ELSE '其他' END) sex
FROM employees
```

> 数学函数 `（a%b = a-a/b*b）`

```sql
SELECT ROUND(-1.45) n1, ROUND(-1.65) n2;         -- -1，-2 -->先绝对值，再四舍五入，最后加符号
SELECT ROUND(-1.455, 2) n1, ROUND(-1.451, 2) n2; -- -1.46, -1.45 -->保留两位小数

SELECT CEIL(-1.451) n1;        -- -1 -->向上取整，返回 >= 参数的最小整数
SELECT FLOOR(-1.451) n1;       -- -2 -->向下取整，... <= ............

SELECT TRUNCATE(-1.499, 1) n1; -- -1.4 -->截断，保留一位小数

SELECT MOD(-10, -3) n1;        -- -1 -->取余%，取余操作的符号位和被除数一致。 a%b = a-a/b*b
```

> 字符串函数 `（索引从1开始计数）`

```sql
SELECT SUBSTR('RUNOOB', 2) AS subStr;     -- UNOOB，从 1 开始计数
SELECT SUBSTR('RUNOOB', 2, 3) AS subStr;  -- UNO --> 同 MID()
SELECT SUBSTR('RUNOOB', -3) AS subStr;    -- OOB --> 同 RIGHT()
```

```sql
SELECT LEFT('RUNOOB', 3) AS leftStr;      -- RUN --> 左侧前3位字符
SELECT RIGHT('RUNOOB', 3) AS rightStr;    -- OOB --> 右侧后3......
SELECT MID('RUNOOB', 2, 3) AS midStr;     -- UNO --> 中间第2位字符开始(从1开始计数)，截取3位
```

```sql
SELECT SUBSTRING_INDEX('192.168.5.120', '.', 2) AS subIndex;  -- 192.168 --> 截第二个'.'之前
SELECT SUBSTRING_INDEX('192.168.5.120', '.', -2) AS subIndex; -- 5.120   --> ...........后
SELECT SUBSTRING_INDEX('192.168.5.120', '..', 2) AS subIndex; -- 192.168.5.120 --> 无返回所有
```

```sql
SELECT UCASE("runoob") AS uCase; -- RUNOOB
SELECT LCASE("RUNOOB") AS lCase; -- runoob --> 大小写转换
```

```sql
SELECT LENGTH('测试') AS length;          -- 6 --> 以'字节'为单位（汉字：utf-8占3字节，GBK占2）
SELECT CHAR_LENGTH('测试') AS charLength; -- 2 --> '字符'【推荐】。VARCHAR(10)，10指的是字符
```

```sql
SELECT REPLACE('192.168.5.120','.','') INTO @repStr; -- 1921685120 --> 替换所有
```

```sql
SELECT INSTR('ASDFGH', 'SD') AS instr;   -- 2 --> 子串第一次出现的索引，找不到返回0

SELECT SUBSTR(email, 1, INSTR(email, '@')-1) name FROM employee; --截取 email 中的 name
```

> 时间函数

```sql
SELECT now();      -- 2019-08-20 11:15:43
SELECT curdate();  -- 2019-08-20
SELECT curtime();  -- 11:16:55
```

```sql
SELECT date('2019-08-20 11:15:43') date; -- 2019-08-20 --> 截取日期部分
SELECT time('2019-08-20 11:15:43') time; -- 11:15:43   --> 截取时间部分

SELECT year('2019-08-20 11:15:43') year;              -- 2019 -->截取年份
SELECT EXTRACT(YEAR FROM '2019-08-20 11:15:43') year; -- 同上
```

```sql
SELECT DATE_ADD('2019-08-20 11:15:43', INTERVAL 1 DAY); -- 1天以后（-1表示一天以前）【DATE_SUB() 也可以表示一天以前】
```

```sql
SELECT DATEDIFF('2008-12-30','2008-12-31') AS dateDiff; -- -1，时间差（前面减后面）
```

```sql
SELECT DATE_FORMAT('2019/08/20 11:15:43', '%Y-%m-%d %H:%i:%s') AS dateFormat; -- 2019-01-11 14:56:19

SELECT STR_TO_DATE('23/04/2019', '%d/%m/%Y') str2date; -- 2019-04-23，字符串转日期
```

```sql
%Y：4位的年份； %m：两位的月份； %y：2位的年份； %c：1位的月份； %d：两位的天数
```

##分组函数





# JSON相关

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





# 事务相关

##基础概念

> 事务分类：隐式事务 和 显式事务

```shell
事务：一个操作序列，这些操作要么都执行成功，要么都执行失败。它是一个不可分割的工作单位。
```

```sql
show engines; --查看当前数据库支持的存储引擎，只有 Innodb 支持事务
```

```sql
-- 【隐式事务】事务没有明显的开启和关闭标识。例如，普通的sql语句：INSERT，UPDATE，DELETE
```

```sql
--【显示事务】必须先设置《自动提交》功能为禁用状态

SHOW VARIABLES LIKE 'autocommit'; -- 查看《自动提交》功能是否开启

SET autocommit=0;                 -- 关闭《自动提交》，只针对当前会话起作用。所以每条事务都要以这条语句开始
START TRANSACTION; -- 可选语句，开始事务

UPDATE trans SET fmoney = fmoney - 700 WHERE fname='小李'; -- 事务正文sql
UPDATE trans SET fmoney = fmoney + 700 WHERE fname='老王';

COMMIT; -- 提交事务
-- ROLLBACK; --回滚事务，与提交事务，二选一
```

> ACID

```sql
-- 原子性（Atomicity）
事务是一个'不可再分割'的工作单位，事务中的操作要么都发生，要么都不发生。
```

```sql
-- 一致性（Consistency）
事务执行会使数据从 一个一致性状态 ->（变换到）-> 另外一个一致性状态（转账之前和之后，金钱总额不变）。
执行过程中，如果某一个或某几个操作失败了，则必须将其他所有操作撤销，将数据恢复到事务执行之前的状态，这就是'回滚'。
```

```sql
-- 隔离性（Isolation）
多个事务并发执行，应保证各个事务之间不能互相干扰。

隔离级别：读未提交（Read uncommitted）、读提交（Read committed）、可重复读（Repeatable read）和串行化（Serializable）。
```

```sql
-- 持久性（Durability）
事务一旦提交，对数据的改变将是永久的，不会被其他操作所影响。比如，删除一条数据。
```

> ACID & CAP

```shell
数据库对于 ACID 中的一致性的定义是这样的：如果一个事务原子地在一个一致地数据库中独立运行，那么在它执行之后，数据库的状态一定是一致的。
对于这个概念，它的第一层意思就是对于数据完整性的约束，包括主键约束、引用约束以及一些约束检查等等，
在事务的执行的前后以及过程中不会违背对数据完整性的约束，所有对数据库写入的操作都应该是合法的，并不能产生不合法的数据状态。
```

```shell
CAP 定理中的数据一致性，其实是说分布式系统中的各个节点中对于同一数据的拷贝有着相同的值；
而 ACID 中的一致性是指数据库的规则，如果 schema 中规定了一个值必须是唯一的，那么一致的系统必须确保在所有的操作中，该值都是唯一的。
由此来看 CAP 和 ACID 对于一致性的定义有着根本性的区别。
```

```shell
数据库的一致性是：应用系统从一个正确的状态到另一个正确的状态。
而 ACID 就是说事务能够通过 AID 来保证这个 C 的过程. C 是目的, AID 都是手段.
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
声明式事务管理：将事务管理代码从业务方法中分离出来，'以声明的方式来实现事务管理'。

事务管理代码的固定模式作为一种横切关注点，可以通过 AOP 方法模块化，进而借助'Spring AOP'框架实现声明式事务管理。
Spring 在不同的事务管理 API 之上定义了一个 抽象层，通过 配置的方式使其生效。
从而让应用程序开发人员不必了解事务管理 API 的底层实现细节，就可以使用 Spring 的事务管理机制。
```

```sql
Spring 的核心事务管理抽象是'PlatformTransactionManager'。它为事务管理封装了一组独立于技术的方法。

-- DataSourceTransactionManager：在应用程序中只需要处理一个数据源，而且通过 JDBC 存取。（常用）
-- JtaTransactionManager       ：在 JavaEE 应用服务器上用 JTA(Java Transaction API)进行事务管理
-- HibernateTransactionManager ：用 Hibernate 框架存取数据库
```

>在需要进行事务控制的方法上加注解

```java
@EnableTransactionManagement //全局注解
```

```java
@Transactional
public void purchase(int bookId, int bookCount) {
    //（1）获取书籍信息（单价，库存）
    Book book = helloMapper.findBookById(bookId);
    
    //（2）扣除书籍库存
    boolean update = helloMapper.updateBookStock(bookId, bookStock);

    //（3）扣除用户金额
    update = helloMapper.updateUserAccount(1, userAccount);
}
```

> @Transactional 注解相关参数

```java
/**
 * @param propagation   事务的传播行为。默认：REQUIRED
 * @param isolation     事务的隔离级别。默认：READ_COMMITED.（和 mysql 默认的隔离不同）
 *
 * @param rollbackFor   需要回滚的异常类，可以为多个。默认：捕获到 RuntimeException 或 Error 时回滚，而捕获到编译时异常不回滚
 * @param noRollbackFor 不需要回滚的异常类，可以为多个
 * @param readOnly      指定事务是否为只读。表示这个事务只读取数据但不更新数据，这样可以帮助数据库引擎优化事务
 * @param timeout       事务执行时间超过这个时间就强制回滚。单位：秒
 */
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED,
               rollbackFor = RuntimeException.class, noRollbackFor = RuntimeException.class,
               readOnly = false, timeout = -1)
```

## 隔离级别

> mysql 默认：REPEATABLE_READ`（可重复读）`。Spring 默认：READ_COMMITED`（读已提交）`

> 数据库的事务并发可能引起的问题

```shell
丢失修改    -> 更新   #T1,T2 读入同一数据并修改，T2提交的结果被 T1 破坏了，导致 T1 的修改丢失。（订票系统）

脏读       -> 更新   #T2改  T1读   T2回滚（T1读取的无效值）
不可重复读  -> 更新   #T1读  T2改提  T1读（和第一次读取的不一样）
幻读       -> 新增   #T1读  T2新增  T1读（多了几行）
```

> 事务的隔离级别要得到底层数据库引擎的支持，而不是应用程序或者框架的支持。`mysql支持4种 > oracle的2种`

```shell
#读未提交，避免【丢失修改】
事务可以看到其他事务'尚未提交'的修改
```

```shell
#读已提交，避免【脏读】
'读取数据的事务'允许其他事务继续访问该行数据，但是'未提交的写事务'将会禁止其他事务访问该行。
```

```shell
#可重复读，避免【脏读】和【不可重复读】
'读取数据的事务'将会禁止写事务（但允许读事务），'写事务'则禁止任何其他事务。
```

```shell
#串行化，消除【以上问题】，但性能低下
要求事务序列化执行，事务只能一个接着一个地执行，'不能并发执行'。

#隔离级别越高，越能保证数据的完整性和一致性，但是对并发性能的影响也越大。
```

> 设置隔离级别

```sql
SELECT @@tx_isolation; -- 查看当前的隔离级别。默认是：可重复读（repeatable_read）

SET transaction isolation level repeatable read;       -- 设置隔离级别，仅对当前连接起效
SET global transaction isolation level read committed; -- 设置全局的隔离级别
```
##传播行为

> 当事务方法被另一个事务方法调用时，必须指定事务应该如何传播？

```java
场景：用户买 5 本书，结账 checkout() 时，调用5次 purchase() 方法，这两个方法都是 声明式事务管理。这就涉及到'事务的传播行为'。
```

```java
@Transactional //结账
public void checkout(String userId, List<Integer> bookIds) {
    for (Integer bookId : bookIds) {
        bookService.purchase(userId, bookId);
    }
}
```

```java
@Transactional //买书
public void purchase(String userId, Integer bookId) {
    //（1）获取书籍信息（单价，库存）
    //（2）扣除书籍库存
    //（3）扣除用户金额
}
```

>REQUIRED：默认配置。在当前已有的事务中继续执行

```java
当 purchase() 方法被另一个事务方法 checkout() 调用时，它默认会在 checkout() 方法的事务内运行。
因此在 checkout() 方法的开始和终止边界内只有一个事务，这个事务只在 checkout() 方法结束的时候被提交。

所以，当用户余额不足购买 5 本书时，整个事务都会回滚，结果导致用户一本书都买不了。
```

![](assets/sql01.png)

>REQUIRES_NEW：挂起当前已有的事务，开启新的事务执行

```java
每一次调用 purchase()方法，都会开启一个新的事务去执行。 checkout()方法会被挂起，直到所有 purchase()方法执行结束，才会提交。
 
所以，当用户余额不足购买 5 本书时，只会导致某一次的 purchase()方法回滚，用户可以买到部分书籍。
```

![](assets/sql02.png)



# 高级命令

## 存储过程

> 一组预先编译好的sql语句集合，可以理解成批处理语句。

```sql
-- 优点
封装并隐藏复杂的业务逻辑，简化sql操作，并提高代码的重用性
减少编译次数，只编译一次
减少和数据库服务器的连接次数，提高了效率

-- 缺点
切换到其他厂商的数据库系统时，需要重写原有的存储过程
存储过程的性能调校与撰写，受限于各种数据库系统
```

> 基础操作

```sql
-- 查看：存储过程详细的定义信息
SHOW CREATE PROCEDURE 数据库.存储过程名;

-- 修改：只能改变存储过程的特征（注释和权限），不能修改过程的参数以及过程体
ALTER {PROCEDURE | FUNCTION} ...

-- 删除：删除后再创建，可以修改存储过程的参数和过程体
DROP PROCEDURE [IF EXISTS] db_name.sp_name;
```

> 创建操作：当过程体只有一句话时，BEGIN END可以省略

```sql
-- 参数模式：IN（只能做输入），OUT（只能做输出），INOUT（既能...又能...）
CREATE PROCEDURE pro_test(参数模式 参数名 参数类型...)
BEGIN
  -- 存储过程体（一组合法的sql语句）
END
```

>创建の参数模式 IN `常用`

```sql
DROP PROCEDURE IF EXISTS get_student_by_teacher_id;

CREATE PROCEDURE get_student_by_teacher_id(IN p_teacher_id INT)
BEGIN
	SELECT s.id id,s.`name` name,t.id `teacher.id`,t.`name` `teacher.name` -- t.id `teacher.id` 关联查询
	FROM student s RIGHT JOIN (SELECT id,`name`,sid FROM teacher WHERE id=p_teacher_id) t
	ON s.tid=t.id LIMIT 5;
END
```

```sql
-- sql 测试
CALL get_student_by_teacher_id(1);
```

```xml
<!-- mybatis 测试 -->
<select id="getStudentByTeacherId" statementType="CALLABLE" resultType="com.example.mybatis.po.Student">
    {CALL get_student_by_teacher_id(#{teacherId})}
</select>
```

>创建の参数模式 OUT

```sql
-- 参数模式 OUT
CREATE PROCEDURE get_student_info_by_id(IN p_in_id INT, OUT p_out_name VARCHAR(20))
BEGIN
    SELECT name INTO p_out_name
    FROM student WHERE id=p_in_id;
END
```

```sql
-- 测试
-- SET @sname='', @gender=''; -- 使用前先声明，声明时必须指定默认值（也可以省略声明过程，直接使用用户变量）

CALL get_student_info_by_id(3, @sname);
SELECT @sname;
```

> 创建の参数模式 INOUT

```sql
-- 参数模式 INOUT
CREATE PROCEDURE double_self(INOUT p_a INT, INOUT p_b INT)
BEGIN
    SET p_a=p_a*2;
    SET p_b=p_b*2;
END
```

```sql
SET @a=3, @b=5; -- 对于模式 INOUT，必须先声明并赋值

CALL double_self(@a, @b);
SELECT @a,@b;
```

## 变量

> 变量分类： 局部变量、用户变量、会话变量 和 全局变量 

```sql
其中，会话变量 和 全局变量 统称为 系统变量。而，局部变量 只存在于函数和存储过程之中
```

> 系统变量：会话变量 和 全局变量

```sql
系统已经提前定义好了的变量，一般都有其特殊意义。如，代表字符集、代表某些mysql文件位置

-- 系统变量，用户不能新增，只能修改已有的
```

>会话变量：只在当前会话生效

```sql
show session variables;              -- 查看所有的会话变量
show session variables LIKE "%var%"; -- 过滤部分

set session var_name = value;        -- 赋值
```

>全局变量：影响服务器整体操作。但是一旦重启，这些设置会被重置。注意要想更改全局变量，必须具有SUPER权限。

```sql
show session variables;
show global variables like "%var%";

set global var_name = value;
```

> 用户变量：用户定义的变量，`以 @ 为前缀`。仅在当前会话生效

```sql
-- 可以不声明定义，直接使用，默认为 null
变量名对大小写不敏感
变量不能在要求字面值的地方使用，比如 select 中的 limit 语句等
调用用户变量的表达式的计算顺序实际上是未定义的 -- SELECT @a = 0, @a := @a + 1; 两列都可能是 0
为用户变量赋值时，会先确定表达式的值         -- SET @m = 0; SET @m = 3, @n = @m; SELECT @n; 结果为 0
虽然用户变量的类型可以动态修改，但不建议这么操作
```

```sql
set @变量名 = 1;
select @变量名: = 值; -- 因为 =，有很多地方都用来判断是否等于，为了避免歧义，也可以使用 := 来赋值
select 值 into @变量名;
```

> 局部变量：一般用在存储过程、函数等。`用户变量的一种，不需要使用 @`

```sql
declare var int default 666; -- 局部变量使用 declare 声明，可选项 default 设置默认值
set var= 值；
select 值 into var;
```

```sql
-- 局部变量 与 用户变量：
前缀符号：用户变量是以 "@" 开头的。局部变量没有这个符号
定义方式：用户变量使用 set 语句，局部变量使用 declare 语句定义 
作用范围：局部变量只在 begin-end 语句块之间有效，出了范围就失效
```

##函数

> 函数：存储着一系列sql语句，调用函数就是一次性执行这些语句。所以，函数可以降低语句重复。

```sql

```





## 执行顺序

```sql
(7) - SELECT
(8) - DISTINCT <select_list>
(1) - FROM <left_table>
(3) - <join_type> JOIN <right_table>
(2) - ON <join_condition>
(4) - WHERE <where_condition>
(5) - GROUP BY <group_by_list>
(6) - HAVING <having_condition>
(9) - ORDER BY <order_by_condition>
(10 - LIMIT <limit_number>
```

```sql
FROM 才是 SQL 语句执行的第一步，并非 SELECT。
数据库在执行 SQL 语句的第一步是将数据从硬盘加载到数据缓冲区中，以便对这些数据进行操作。
```

```sql
SELECT 是在大部分语句执行了之后才执行的，严格的说是在 FROM 和 GROUP BY 之后执行的。
理解这一点是非常重要的，这就是你'不能'在 WHERE 中使用在 SELECT 中设定别名的字段作为判断条件的原因。
```

```sql
无论在语法上还是在执行顺序上， UNION 总是排在在 ORDER BY 之前。
很多人认为每个 UNION 段都能使用 ORDER BY 排序，但是根据 SQL 语言标准和各个数据库 SQL 的执行差异来看，这并不是真的。
尽管某些数据库允许 SQL 语句对子查询（subqueries）或者派生表（derived tables）进行排序，
但是，这并不说明这个排序在 UNION 操作过后仍保持排序后的顺序。
```

