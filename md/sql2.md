# 事务相关

##基础概念

> 事务分类：隐式事务 和 显式事务

```shell
事务：一个操作序列，这些操作要么都执行成功，要么都执行失败。它是一个不可分割的工作单位。

'隐式事务'：事务没有明显的开启和关闭标识。例如，普通的sql语句：INSERT，UPDATE，DELETE
'显示事务'：事务语句被 BEGIN END 包裹，必须先设置《自动提交》功能为禁用状态
```

```sql
SHOW engines; --查看当前数据库支持的存储引擎，只有 Innodb 支持事务

SHOW VARIABLES LIKE 'autocommit'; -- 查看《自动提交》功能是否开启

SET autocommit=0;                 -- 关闭《自动提交》，只针对当前会话起作用。所以每条事务都要以这条语句开始
START TRANSACTION; -- 可选语句，开始事务

UPDATE trans SET fmoney = fmoney - 700 WHERE fname='小李'; -- 事务正文sql
UPDATE trans SET fmoney = fmoney + 700 WHERE fname='老王';

COMMIT; -- 提交事务
-- ROLLBACK; --回滚事务，与提交事务，二选一
```

>设置隔离级别

```sql
SELECT @@tx_isolation; -- 查看当前的隔离级别。默认是：可重复读（repeatable_read）

SET transaction isolation level repeatable read;       -- 设置隔离级别，仅对当前连接起效
SET global transaction isolation level read committed; -- 设置全局的隔离级别
```






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
DROP PROCEDURE IF EXISTS get_student_by_teacher_id; --删除
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

> `系统变量`：系统预定义好的变量，用户不能新增。只能修改已有变量的值，重启将被重置

```sql
--变量以 '@@' 开始，形式为 '@@变量名'。修改必须具有 SUPER 权限

show variables LIKE "%event%";  --显示已有的变量
SET @@event_scheduler=value;
SELECT @@event_scheduler;
```

> `用户变量`：只在当前会话生效

```sql
--变量以 '@' 开始，形式为 '@变量名'

SET @var='hello sql';
SELECT @var;           --默认为 NULL
```

> `局部变量`：一般用在存储过程、函数中

```sql
--不需要使用 '@'，声明使用关键字 declare

declare var int default 666; -- 可选项 default 设置默认值
set var = 值；
select 值 into var;
```

>用户变量 & 局部变量

```sql
前缀符号：用户变量是以 "@" 开头的。局部变量没有这个符号
定义方式：用户变量使用 set 语句，局部变量使用 declare 语句定义 
作用范围：局部变量只在 begin-end 语句块之间有效，出了范围就失效

-- 为了和 oracle 赋值相匹配，尽量都使用 ":=" 来赋值
```

##函数

> 函数：存储着一系列sql语句，调用函数就是一次性执行这些语句。所以，函数可以降低语句重复。

```sql
-- 【区别】存储过程
函数   ： 返回值 - 有且仅有一个，'不允许返回一个结果集'。函数强调返回值，所以函数不允许返回多个值的情况，即使是查询语句。
存储过程： 返回值 - 有0个或者多个。

函数   ： 适合做处理数据，并返回一个结果。
存储过程： 适用于批量插入，或批量更新等
```

> 函数创建

```sql
-- 定义有参函数
CREATE FUNCTION fun_test(name varchar(15)) RETURNS int
BEGIN 
    declare c int default 0; --定义局部变量
    select id from class where cname=name into c; --局部变量赋值
    return c;
END;
```

```sql
select fun_test("python");  --调用函数

show create function 函数名; --查看指定函数
show function status [like 'pattern']; --查看所有函数
drop function 函数名;        --删除函数
```

## 触发器

> 触发器（trigger）：<https://blog.csdn.net/qq_36396104/article/details/80469997>

```sql
监视某种情况，并触发某种操作，它是与表事件相关的特殊的存储过程，它的执行不是由程序调用，也不是手工启动，而是由事件来触发。
例如，当对一个表进行操作（insert delete update）时就会激活它执行。
```

```sql
--四要素：
1.监视地点(table) --注意：触发器只能作用在永久表，不能作用于临时表。
2.监视事件(insert update delete) 
3.触发时间(after/before) 
4.触发事件(insert update delete)
```

```sql
CREATE TRIGGER `trigger_name` BEFORE/AFTER INSERT/UPDATE/DELETE ON `表名` FOR EACH ROW BEGIN --固定语句
    --触发器主体，可以使用 old 和 new 来引用触发器中发生变化的记录内容
END
```

```sql
CREATE TRIGGER `system_set_before_update` BEFORE UPDATE ON `system_set` FOR EACH ROW BEGIN
  update basic_data_change set  batch_id=batch_id+1 where id =1;
  set new.memo=trim(new.memo); --old 表示修改前的内容
if  old.memo<>new.memo then
    insert log_operate(memo,operator_id)values(concat('update system_set: ',old.name,' 从 ',old.memo,' 改为 ',new.memo),1);
end if;
END
```



## 执行结构

> 分支结构

```sql
CREATE PROCEDURE pro_test(in a char(1))
BEGIN
    IF 条件 THEN
        sql语句; -- 注意每个结束符号 ;
    ELSEIF 条件 THEN
        sql语句;
    ELSE
        sql语句;
    END IF;
END;
```

>循环结构

```sql
CREATE PROCEDURE pro_while(IN p_count INT)
BEGIN
    DECLARE i INT DEFAULT 0;
    WHILE i<p_count DO
        INSERT INTO student(s_name,s_pwd) VALUES(CONCAT('john',i),'123');
        SET i=i+1; -- 循环体内 ++
    END WHILE;
END;
```

# 高级概念

## 索引相关

>索引

```sql
-- 索引：表的目录，在查找内容之前可以先在目录中查找索引位置，以此快速定位查询数据。
不使用索引，MySQL必须从第一条记录开始读完整个表，直到找出相关的行，表越大，查询数据所花费的时间就越多。
如果表中查询的列有一个索引，MySQL能够快速到达一个位置去搜索数据文件，而不必查看所有数据，那么将会节省很大一部分时间。

例如：有一张person表，其中有2W条记录，记录着2W个人的信息。有一个Phone的字段记录每个人的电话号码，现在想要查询出电话号码为xxxx的人的信息。
如果没有索引，那么将从表中第一条记录一条条往下遍历，直到找到该条信息为止。
如果有了索引，那么会将该Phone字段，通过一定的方法进行存储，好让查询该字段上的信息时，能够快速找到对应的数据，而不必在遍历2W条数据了。
```

> 优缺点

```sql
1、所有的MySql列类型(字段类型)都可以被索引，也就是可以给任意字段设置索引
2、大大加快数据的查询速度

1、创建索引和维护索引要耗费时间，并且随着数据量的增加所耗费的时间也会增加
2、索引也需要占空间，我们知道数据表中的数据也会有最大上线设置的，如果我们有大量的索引，索引文件可能会比数据文件更快达到上线值
3、当对表中的数据进行增加、删除、修改时，索引也需要动态的维护，降低了数据的维护速度。
```

> 使用原则

```sql
--通过上面说的优点和缺点，我们应该可以知道，并不是每个字段度设置索引就好，也不是索引越多越好，而是需要自己合理的使用。
1、对经常更新的表就避免对其进行过多的索引，对经常用于查询的字段应该创建索引，
2、数据量小的表最好不要使用索引，因为由于数据较少，可能查询全部数据花费的时间比遍历索引的时间还要短，索引就可能不会产生优化效果。
3、在一同值少的列上（字段上）不要建立索引，比如在学生表的'性别'字段上只有男，女两个不同值。相反的，在一个字段上不同值较多可以建立索引。
```

```sql
1、对于那些在查询中很少使用或者参考的列'不应该创建索引'。
2、对于那些只有很少数据值的列也不应该增加索引。因为本来结果集合就是相当于全表查询了，所以没有必要。
3、对于那些定义为 text，image，bit 数据类型的列不应该增加索引。这是因为，这些列的数据量要么相当大，要么取值很少。
4、当修改性能远远大于检索性能时，不应该创建索引。
5、不会出现在 where 条件中的字段不该建立索引。
```

> 索引方法

```sql
--注意：索引是在存储引擎中实现的，也就是说不同的存储引擎，会使用不同的索引
MyISAM 和 InnoDB 存储引擎：只支持'BTREE'索引， 也就是说默认使用BTREE，不能够更换
MEMORY/HEAP 存储引擎：支持'HASH'和'BTREE'索引

HASH  是以 key-value 的形式进行索引存储
BTREE 是以二叉树方式进行索引存储。（默认存储索引类型）
```

> 索引分类

```sql
--统属于 单列索引 ---> 一个索引只包含单个列，但一个表中可以有多个单列索引。
'普通索引'：没有什么限制，允许在定义索引的列中插入重复值和'空值'，纯粹为了查询数据更快一点。
'唯一索引'：索引列中的值必须是唯一的，但是允许为空值
'主键索引'：一种特殊的唯一索引，不允许有空值。
```

```sql
'组合索引'：在表中的多个字段组合上创建的索引。遵循'最左匹配原则'。

'最左匹配原则'：只要查询的条件中用到了最左边的列，索引一般就会被使用。
例如，这里由 id、name、age 3个字段构成的组合索引，索引行就按 id/name/age 的顺序存放。
使用索引的组合：（id,name,age）、（id,name）、（id,age）、（id） --列顺序可以调换
不会使用索引：（age）、（name）、（name,age）
```

```sql
'全文索引'：只有在'MyISAM'引擎上才能使用，只能在 CHAR,VARCHAR,TEXT 类型字段上使用全文索引。
就是在一堆文字中，通过其中的某个关键字等，就能找到该字段所属的记录行。比如有"你是个靓仔，靓女..." 通过靓仔，'可能'就可以找到该条记录。
```

```sql
ALTER TABLE tbl_name ADD PRIMARY KEY (column_list);         --主键索引，意味着索引值必须是唯一的，且不能为NULL
ALTER TABLE tbl_name ADD UNIQUE index_name (column_list);   --唯一索引（除了NULL外，NULL可能会出现多次）
ALTER TABLE tbl_name ADD INDEX index_name (column_list);    --普通索引，索引值可出现多次
ALTER TABLE tbl_name ADD FULLTEXT index_name (column_list); --全文索引

ALTER TABLE tbl_name DROP INDEX c; --删除索引
SHOW INDEX FROM table_name;        --显示索引
```

> 普通索引

```sql
CREATE TABLE `person` (
    `id` int(5) DEFAULT NULL,
    `age` int(2) DEFAULT NULL,
    `name` varchar(10) DEFAULT NULL,
    `birth` varchar(10) DEFAULT NULL,
    KEY `name` (`name`)              --普通索引
) ENGINE=InnoDB DEFAULT CHARSET=utf8
```

```sql
mysql> EXPLAIN SELECT * FROM person WHERE `name`='张三'; --EXPLAIN：用来查看索引是否被使用
+----+-------------+--------+------------+------+---------------+------+---------+-------+------+----------+-------+
| id | select_type | table  | partitions | type | possible_keys | key  | key_len | ref   | rows | filtered | Extra |
+----+-------------+--------+------------+------+---------------+------+---------+-------+------+----------+-------+
|  1 | SIMPLE      | person | NULL       | ref  | name          | name | 33      | const |    1 |   100.00 | NULL  |
+----+-------------+--------+------------+------+---------------+------+---------+-------+------+----------+-------+
1 row in set, 1 warning (0.03 sec)
```

```sql
--最主要的是看 possible_keys 和 key 这两个属性，上面显示了 key 为 name。说明使用了索引。
```

```sql
id:　SELECT识别符。这是SELECT的查询序列号,也就是一条语句中，该select是第几次出现。在次语句中，select就只有一个，所以是1.

select_type：所使用的SELECT查询类型，SIMPLE表示为简单的SELECT，不实用UNION或子查询，就为简单的SELECT。也就是说在该SELECT查询时会使用索引。
其他取值，PRIMARY：最外面的SELECT。在拥有子查询时，就会出现两个以上的SELECT。
UNION：union(两张表连接)中的第二个或后面的select语句  SUBQUERY：在子查询中，第二SELECT。

table：数据表的名字。他们按被读取的先后顺序排列，这里因为只查询一张表，所以只显示book

type：指定本数据表和其他数据表之间的关联关系，该表中所有符合检索值的记录都会被取出来和从上一个表中取出来的记录作联合。
ref用于连接程序使用键的最左前缀或者是该键不是 primary key 或 unique索引（换句话说，就是连接程序无法根据键值只取得一条记录）的情况。
当根据键值只查询到少数几条匹配的记录时，这就是一个不错的连接类型。
(注意，个人这里不是很理解，百度了很多资料，全是大白话，等以后用到了这类信息时，在回过头来补充，这里不懂对后面的影响不大。)可能的取值有 system、const、eq_ref、index和All

possible_keys：MySQL在搜索数据记录时可以选用的各个索引，该表中就只有一个索引，year_publication

key：实际选用的索引

key_len：显示了mysql使用索引的长度(也就是使用的索引个数)，当 key 字段的值为 null时，索引的长度就是 null。
注意，key_len的值可以告诉你在联合索引中mysql会真正使用了哪些索引。这里就使用了1个索引，所以为1，

ref:给出关联关系中另一个数据表中数据列的名字。常量（const），这里使用的是1990，就是常量。

rows：MySQL在执行这个查询时预计会从这个数据表里读出的数据行的个数。

extra：提供了与关联操作有关的信息，没有则什么都不写。
```

> 组合索引

```sql
CREATE TABLE `person` (
    `id` int(5) DEFAULT NULL,
    `age` int(2) DEFAULT NULL,
    `name` varchar(10) DEFAULT NULL,
    `birth` varchar(10) DEFAULT NULL,
    KEY `multi` (`id`,`name`,`age`) USING BTREE --组合索引
) ENGINE=InnoDB DEFAULT CHARSET=utf8
```

```sql
mysql> EXPLAIN SELECT * FROM person WHERE id=5 and age=18;
+----+-------------+--------+------------+------+---------------+-------+---------+-------+------+----------+-----------------------+
| id | select_type | table  | partitions | type | possible_keys | key   | key_len | ref   | rows | filtered | Extra                 |
+----+-------------+--------+------------+------+---------------+-------+---------+-------+------+----------+-----------------------+
|  1 | SIMPLE      | person | NULL       | ref  | multi         | multi | 5       | const |    1 |   100.00 | Using index condition |
+----+-------------+--------+------------+------+---------------+-------+---------+-------+------+----------+-----------------------+
1 row in set, 1 warning (0.00 sec)
```

##约束相关

> 约束：为了保证表中数据的`准确性和可靠性`而实现的一套机制。

```sql
约束是为了'保证表数据的完整性'，索引是为了'提高查询效率'，两者作用不一样！其次种类也不一样。
```

> 约束分类

```sql
NOT NULL    -- 非空约束，保证该字段的值不能为空。姓名
UNIQUE      -- 唯一约束，只保证该字段的值具有唯一性，可为空。座位号
PRIMARY KEY -- 主键约束（主键约束 = 非空约束 + 唯一约束），保证该字段的值具有唯一性，且非空。学号

DEFAULT        -- 默认约束，保证该字段有默认值。性别
AUTO_INCREMENT -- 自增约束
FOREIGN KEY    -- 外键约束，表中该字段的值必须来自主表的关联列的值
CHECK          -- 检查约束，但 mysql 不支持。如性别只能写男女，年龄只能写1-200等
```

> 主键索引 & 唯一索引

```sql
主键 -- 每个表中只能有一个，值必须保证唯一性，且不能为 null。
唯一 -- .......可以有多个，.............，但可以为 null（最多只能有一行 null）。

对于 组合主键 或 组合唯一，二者都支持，但不推荐使用。
组合主键 和 组合唯一 的唯一性：如id，name，只有两条数据的id，name都相同才报错，即不满足唯一性。
```

```sql
SHOW INDEX FROM `coupon`;
SHOW KEYS FROM `coupon`; -- 查看索引

ALTER TABLE `coupon` DROP index 索引名; -- 删除索引

ALTER TABLE `coupon` ADD key (索引名);        -- 新增 普通索引
ALTER TABLE `coupon` ADD primary key 索引名;  -- 新增 主键索引
ALTER TABLE `coupon` ADD UNIQUE key (索引名); -- 新增 唯一索引
```

> 外键约束

```sql
外键要求在'从表'上进行设置
从表的外键类型和主表的关联列的'类型'要求一致或兼容，名字不一定要一样
主表的关联列必须是一个 key（主键或唯一键）
```

> 标识列：又称为自增长列，含义：可以不用手动的插入值，系统提供默认的序列值

```sql
标识列必须和主键搭配吗？ -- 不一定，但要求是一个key（也可以是unique）
一个表可以有几个标识列？ -- 至多一个！
标识列的类型只能是'数值型'
标识列可以通过 SET auto_increment_increment=3;设置步长；另外可以通过 手动插入值，设置起始值
```



```sql

```





# 基础概念

## 基础概念

> 三大范式

```sql
范式就是规范，就是关系型数据库在设计表时，要遵循的三个规范。
--要想满足第（二）范式必须先满足第（一）范式，要满足第（三）范式必须先满足第（二）范式。

第一范式：确保每列保持原子性。即每列内容'不可拆分'，如，学校信息包含：学历 + 班级，就可拆分为两列
第二范式：确保表中的每列都和主键相关。即要有'主键'，并且其他字段都依赖于主键。如，学生表中必须包含学生的唯一标识学号。
第三范式：确保表中每一列都和主键列直接相关,而不是间接相关。即要消除间接依赖，消除冗余，对于间接依赖要使用'外键'。
```

```sql
--反三范式
（1）不设置外键。
外键会严重影响数据库读写的效率；数据删除时会比较麻烦；在代码中使用逻辑来维护表关系，而非数据库中的外键。

（2）适当添加冗余字段，增加效率。
如，订单表和订单详情表，可以通过订单详情表中的 数量*单价 推断出订单的总价，但还是在订单表中添加总价字段，提升效率。
```

>mysq的默认最大连接数？

```sql
show variables like '%max_connections%'; --默认100
```

> 分页 mysql & oracle

```sql
为什么需要分页？ --在很多数据时，不可能完全显示数据。进行分段显示。

mysql是使用关键字 limit 来进行分页的 limit offset,size 表示从多少索引取多少位。
Oracle的分页有点儿记不住了，只记得一些大概。是使用了'三层嵌套查询'。如果在工作中使用了，可以到原来的项目中拷贝或上网查询。
```

```sql
select * from students order by id limit (pageSize * (pageNumber-1)), pageSize; --mysql
```

```sql
 select * from (
     select *,rownum rid from (
         select * from students order by postime desc
     ) where rid<= (pagesize * pagenumber)
 )as t where t> (pageSize*(pageNumber-1));
 --注: 其实两层就可以，不过，两层嵌套查询不会用到oracle的外层条件内推机制，效率慢了点
```

> 索引 & 约束

```sql
约束是为了'保证表数据的完整性'，索引是为了'提高查询效率'，两者作用不一样！其次种类也不一样。
```

> 




## 概念区分

> delete & truncate

```sql
DELETE FROM city;    -- 清空表（1）
TRUNCATE TABLE city; -- 清空表（2）

-- DELETE 可以加 WHERE 过滤条件，TRUNCATE 不可以
-- DELETE 删除有返回值。TRUNCATE 删除没有
-- DELETE 对每条记录的删除均需要记录日志，所有速度慢，但可回滚。TRUNCATE 只记录页删除的日志，速度快，不可恢复

-- DELETE 清空表后，添加新的数据时自增列接着自增。TRUNCATE 则是从1开始重新计数
```

> where & having

```sql

```

```sql

```

```sql

```

```sql

```

> timestamp & datetime

```sql
-- 二者异同
'相同点'：两者都可用来表示YYYY-MM-DD HH:MM:SS[.fraction]类型的日期。

'不同点'：（1）两者的存储方式不一样
对于 TIMESTAMP，它把客户端插入的时间从当前时区 -> 转化为UTC（世界标准时间）进行存储。查询时，将其又 -> 转化为客户端当前时区进行返回。
而，对于 DATETIME，不做任何改变，基本上是原样输入和输出。

'不同点'：（2）两者所能存储的时间范围不一样
TIMESTAMP：占用4个字节，表示范围 '1970-01-01 00:00:01.000000' to '2038-01-19 03:14:07.999999'
DATETIME ：占用8个字节，表示范围 '1000-01-01 00:00:00.000000' to '9999-12-31 23:59:59.999999'

-- 总结：TIMESTAMP 和 DATETIME 除了存储范围和存储方式不一样，没有太大区别。当然，对于跨时区的业务，TIMESTAMP 更为合适。
```

```sql
如果存进去的是 NULL, TIMESTAMP 会自动储存当前时间，DATETIME 会储存 NULL。
TIMESTAMP 适合用来记录数据的最后修改时间，因为只要更改了记录中其他字段的值，该字段的值都会被自动更新（可设置不自动更新）。
```

```sql
-- 保存毫秒值
无论 datetime 还是 timestamp：数据长度一栏选择 3，不然不保留毫秒值。'2019-5-17 20:09:10.456'
```

> char & varchar

```sql
--区别1：定长和变长
char 表示定长，长度固定，varchar表示变长，即长度可变。
当所插入的字符串超出它们的长度时，视情况来处理，如果是严格模式，则会拒绝插入并提示错误信息，如果是宽松模式，则会截取然后插入。
如果插入的字符串长度小于定义长度时，则会以不同的方式来处理。
如char（10），表示存储的是10个字符，无论你插入的是多少，都是10个，如果少于10个，则用空格填满。
而varchar（10），小于10个的话，则插入多少个字符就存多少个。

varchar 怎么知道所存储字符串的长度呢？
实际上，对于varchar字段来说，需要使用一个（如果字符串长度小于255）或两个字节（长度大于255）来存储字符串的长度。
```

```sql
--区别2：存储的容量不同
对 char 来说，最多能存放的字符个数 255，和编码无关。而 varchar 呢，最多能存放 65532 个字符。
在 varchar 存字符串的时候，第 1 个字节是空的，然后还需要 2 个字节来存放字符串的长度。所以有效长度就是 65535 - 1 - 2 = 65532
```

```sql
无论是否通过索引，varchar类型的数据检索略优于char的扫描。那实际开发中，我们使用哪种呢？
当确定字符串为定长、数据变更频繁、数据检索需求少时，使用 char；
当不确定字符串长度、对数据的变更少、查询频繁时，使用 varchar。

相关题目：若一个表定义为 create table t1(c int, c2 char(30), c3 varchar(N)) charset=utf8; 问 N 的最大值又是多少？
（65535 - 1 - 2 - 4 - 30 * 3 ）/3
```

> ip如何保存？

```sql
SELECT INET_ATON('192.168.5.25'); --3232236825
SELECT INET_NTOA(3232236825);     --192.168.5.25

--查出范围在 192.168.1.3 到 192.168.1.20 之间的 ip 地址
select * from ip_table where inet_aton(ip) > inet_aton('192.168.1.3') and inet_aton(ip) < inet_aton('192.168.1.20');
```



# 优化相关

>基础优化

```sql
查询语句不要用 SELECT *            --增加很多不必要的消耗（CPU、IO、内存、网络带宽）
创建索引，加速查询，但影响增删改

如果排序字段没有用到索引，就尽量少排序 

避免全局扫描，即涉及到 非 逻辑，不要用 NOT IN，可以用 EXISTS 代替
```

```sql
--尽量用 union-all 代替 union
后者需要将结果集合并后再进行唯一性过滤操作，这就会涉及到排序，增加大量的CPU运算，加大资源消耗及延迟
当然，union all的前提条件是两个结果集没有重复数据

--避免在where子句中对字段进行null值判断
对于null的判断会导致引擎放弃使用索引而进行全表扫描。

--避免隐式类型转换
where子句中出现column字段的类型和传入的参数类型不一致的时候发生的类型转换，建议先确定where中的参数类型。

```

## 索引优化

> person表，age，birth分别为单独索引，id + name为组合索引

```sql
CREATE TABLE `person` (
    `id` int(5) NOT NULL,
    `age` int(2) DEFAULT NULL,
    `name` varchar(10) DEFAULT NULL,
    `birth` varchar(10) DEFAULT NULL,
    `gender` int(2) DEFAULT NULL,
    `info` varchar(50) DEFAULT NULL,
    PRIMARY KEY (`id`),                 -- 主键索引
    KEY `age` (`age`),                  -- 单独索引
    KEY `name` (`name`),                -- 单独索引
    KEY `multiIndex` (`birth`,`info`)   -- 组合索引
) ENGINE=InnoDB DEFAULT CHARSET=utf8
```

```sql
分页查询很重要，如果查询数据量超过30%，MYSQL不会使用索引。
单表索引数不超过5个、单个索引字段数不超过5个。
字符串可使用前缀索引，前缀长度控制在5-8个字符。
字段唯一性太低，增加索引没有意义，如：是否删除、性别。
```

>如果列是字符型，传入的是数字，则不会使用索引

```sql
explain select * from person where birth = '2019'; -- 使用
explain select * from person where birth = 2019;   -- 不使用
```

>LIKE 查询，% 不能放在第 1 位 `那如何查询 %name% ？ 使用全文索引`

```sql
explain select * from person where birth like '2019%'; -- 使用
explain select * from person where birth like '%2019'; -- 不会使用
```

>OR 条件`前后都有索引`才能被使用，而且必须是 `单独索引 ？`

```sql
explain select * from person where id=1 or age=18;         -- 不使用（主键索引 + 单独索引，v5.7.13不使用，其他版本待证）
explain select * from person where name='zhang' or age=18; -- 不使用（单独索引 + 单独索引）
explain select * from person where name='zhang' or birth='2019';  -- 不使用（单独索引 + 组合索引）
explain select * from person where id=1 or gender=1;              -- 不使用（主键索引 + 无索引）

explain (select * from person where id=5) union (select * from person where gender=1); --使用，使用 union 优化
```
>`<>` 操作符无法使用索引

```sql
explain select * from person where age<>18; --不使用（!= 也一样）
explain (select * from person where age<18) union (select * from person where age>18); --使用
```

>

```sql

```

##语法优化

> 使用`exists/join`代替 IN

```sql
select a.* from A where a.id in (select b.id from B b);                -- IN 包含的值不应过多
select a.* from A where exists (select b.id from B b where a.id=b.id); -- 效果同上
```

```sql
-- A > B，使用 IN 查询。A <= B，使用 EXISTS 查询。
IN 查询时，全程扫描子表，即 B，使用 A 表的索引 a.id
EXISTS 查询时，全程扫描主表，即 A，使用 B 表的索引 b.id

-- 如果查询语句使用了 NOT IN 那么内外表都进行全表扫描，没有用到索引；而 NOT EXISTS 的子查询依然能用到表上的索引。
-- 所以无论那个表大，用 NOT EXISTS 都比 NOT IN 要快。
```



```sql

```

```sql
select id from orders where user_id in (select id from user where level = 'VIP');
select o.id from orders o left join user u on o.user_id = u.id where u.level = 'VIP'; -- JOIN 代替 IN
```

```sql
select * from A表 where a.id not in (select b.id from B表);
select * from A表 left join B表 on a.id = b.id where b.id is null; -- JOIN 代替 IN
```

>不做列运算（在where子句中对字段进行运算操作，导致索引失效）

```sql
select user_id,user_project from user_base where age*2=36;
select user_id,user_project from user_base where age=36/2; --优化后
```

```sql
select id from order where date_format(create_time，'%Y-%m-%d') = '2019-07-01';
select id from order where create_time between '2019-07-01 00:00:00' and '2019-07-01 23:59:59'; --优化后
```

>`Limit`优化（分页查询时越往后翻性能越差）

```sql
select id,name from emp limit 1747390, 10;         --随着表数据量的增加，直接使用 limit 分页查询会越来越慢

select id,name from emp WHERE id>1747390 LIMIT 10; --优化：取前一页的最大行数的id，然后以此id来限制下一页的起点
```

> 分批处理

```sql
--如果大量优惠券需要更新为不可用状态，执行这条SQL可能会堵死其他SQL，分批处理伪代码
update status=0 FROM `coupon` WHERE expire_date <= #{currentDate} and status=1;
```

```java
private void updateState() {
    int pageNo = 1;
    int PAGE_SIZE = 100;
    while (true) {
        List<Integer> batchIdList = queryList("select id FROM `coupon` WHERE expire_date <= #{currentDate}" +
                                              "and status = 1 limit #{(pageNo-1) * PAGE_SIZE},#{PAGE_SIZE}");
        if (CollectionUtils.isEmpty(batchIdList)) {
            return;
        }
        update("update status = 0 FROM `coupon` where status = 1 and id in #{batchIdList}");
        pageNo++;
    }
}
```

>

```sql

```



# 开发手册

##建表规约

```sql
【强制】表达是与否概念的字段，必须使用 is_xxx 的方式命名，数据类型是 unsigned tinyint（1表示是，0表示否），此规则同样适用于 odps 建表。
--说明：任何字段如果为非负数，必须是 unsigned
--注意：POJO 类中的任何布尔类型的变量，都不要加 is 前缀。所以，需要在<resultMap/>设置从 is_xxx 到 Xxx 的映射关系。
--数据库表示是与否的值，使用 tinyint 类型，坚持 is_xxx 的命名方式是为了明确其取值含义与取值范围。
--正例：表达逻辑删除的字段名 is_deleted，1 表示删除，0 表示未删除。

CREATE TABLE `grade` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `is_delete` tinyint(1) unsigned DEFAULT 0,
    PRIMARY KEY (`id`)
);
```

```sql
【强制】数据库名、表名、字段名，必须使用小写字母或数字。禁止出现数字开头，禁止两个下划线中间只出现数字。
【强制】表名不使用复数名词。对应于 DO 类名也是单数形式，符合表达习惯。

【强制】表必备三字段：id, gmt_create, gmt_modified
--说明：其中 id 必为主键，类型为 bigint unsigned、单表时自增、步长为 1。 
--gmt_create，gmt_modified 的类型均为 datetime 类型，前者现在时表示主动创建，后者过去分词表示被动更新。

【强制】小数类型为 decimal，禁止使用 float 和 double
--说明：float 和 double 在存储的时候，存在精度损失的问题，很可能在值的比较时，得到不正确的结果。
--如果存储的数据范围超过 decimal 的范围， 建议将数据拆成整数和小数分开存储。

【强制】如果存储的字符串长度几乎相等，使用 char 定长字符串类型
【强制】变长字符串 varchar 不预先分配存储空间，长度不要超过 5000
--如果存储长度大于此值，定义字段类型为 text，独立出来一张表，用主键来对应，避免影响其它字段索引效率。

【推荐】单表行数超过 500 万行或者单表容量超过 2 GB，才推荐进行分库分表
--说明：如果预计三年后的数据量根本达不到这个级别，请不要在创建表时就分库分表。
```

```sql
【参考】合适的字符存储长度，不但节约数据库表空间、节约索引存储，更重要的是提升检索速度。
--正例：如下表，其中无符号值可以避免误存负数，且扩大了表示范围

人       150岁以内    tinyint unsigned    1个字节     无符号值：0 ~ 255
龟       数百岁       smallint unsigned   2          无符号值：0 ~ 65535
恐龙化石  数千万年     int unsigned        4          0 ~ 2^(4*8)-1
```

```sql
【强制】禁用保留字，如desc、range、match、delayed等，请参考 MySQL 官方保留字。
【强制】唯一索引名为 uk_字段名； 普通索引名则为 idx_字段名。-- 说明：uk_ 即 unique key；idx_ 即 index 的简称。

【推荐】表的命名最好是加上'业务名称_表的作用'。-- alipay_task / force_project / trade_config
【推荐】库名与应用名称尽量一致
【推荐】如果修改字段含义或对字段表示的状态追加时，需要及时更新字段注释。

【推荐】字段允许适当冗余，以提高查询性能，但必须考虑数据一致。冗余字段应遵循：
1）不是频繁修改的字段。
2）不是 varchar 超长字段，更不能是 text 字段。
--正例：商品类目名称使用频率高，字段长度短，名称基本一成不变，可在相关联的表中冗余存储类目名称，避免关联查询。
```

## 索引规约



## SQL语句

```sql
【强制】不要使用 COUNT(列名)或 COUNT(常量)来替代 COUNT(*)。COUNT(*)就是SQL92定义的标准统计行数的语法，跟数据库无关，跟NULL和非NULL无关。
-- 说明：COUNT(*) 会统计值为 NULL 的行，而 COUNT(列名) 不会统计此列为 NULL 值的行。【使用场景不同】

【强制】 COUNT(DISTINCT col) 计算该列除 NULL 之外的不重复数量。
-- 注意：COUNT(DISTINCT col1,col2)如果其中一列全为 NULL，那么即使另一列有不同的值，也返回为 0。

【强制】当某一列的值全是 NULL时， COUNT(col)的返回结果为 0。但 SUM(col)的返回结果为NULL，因此使用 SUM()时需注意 NPE 问题。
-- 正例：避免 SUM的 NPE问题： SELECT IF(ISNULL(SUM(g)) ,0, SUM(g)) FROM table;

【强制】使用 ISNULL() 来判断是否为 NULL 值。 -- 注意： NULL 与任何值的直接比较都为 NULL。
-- (1) NULL<>NULL 的返回结果是 NULL，而不是 false。
-- (2) NULL=NULL 的返回结果是 NULL，而不是 true。
-- (3) NULL<>1 的返回结果是 NULL，而不是 true 。
```

```sql
【强制】在代码中写'分页'查询逻辑时，若总条数 COUNT 为 0 应直接返回，避免执行后面的分页语句。

【强制】不得使用外键与级联，一切外键概念必须在应用层解决。
-- 说明：（概念解释）学生表中的 student_id 是主键，那么成绩表中的 student_id 则为外键。
-- 如果更新学生表中的 student_id，同时触发成绩表中的 student_id 更新，则为级联更新。
-- 外键与级联更新适用于单机低并发，不适合分布式、高并发集群。级联更新是强阻塞，存在数据库更新风暴的风险。外键影响数据库的插入速度。

【强制】数据订正时，删除和修改记录时，要先 SELECT，避免出现误删除，确认无误才能执行更新语句。

【推荐】 IN 操作能避免则避免，实在避免不了，需要仔细评估 IN 后边的集合元素数量，控制在 1000之内。

【参考】如果有全球化需要，所有的字符存储与表示，均以 UTF-8 编码，那么字符计数方法注意：
-- SELECT LENGTH("轻松工作")； 返回为 12
-- SELECT CHARACTER_LENGTH("轻松工作")； 返回为 4
-- 如果要使用表情，那么使用 utfmb4 来进行存储，注意它与 utf-8 编码的区别

【参考】TRUNCATE TABLE比 DELETE速度快，且使用的系统和事务日志资源少。
但 TRUNCATE无事务且不触发 trigger，有可能造成事故，故不建议在开发代码中使用此语句。
-- 说明： TRUNCATE TABLE 在功能上与不带 WHERE 子句的 DELETE 语句相同。
```

>【强制】禁止使用存储过程，存储过程难以调试和扩展，更没有移植性。

```sql
'调试'：线上调试一般就是打日志，在应用层，日志可以在任何一步打，但是存储过程的话，日志没法跟踪详细的执行过程。

'扩展'：譬如你的产品购买流程要增加一个动作，这时候就要修改存储过程到db里，你这时候要直接操作db。
而在大公司，直接操作db只能有dba来进行，其他都要审批后使用公司自己开发的工作来进行，且只能是简单的crud。

'移植'：你用mysql写的存储过程，到了sql-server不一定能直接用。但是在应用层的话，程序里的crud的基础sql基本上通用的，修改下连接串一般就ok了。
```

```sql
-- 存储过程的优点（适合处理场景固定的复杂事务，不要求并发性）：
1.效率高。
  (1).与数据库服务的通信次数少；
  (2).可以直接编译成物理计划，少了parse和查询优化步骤；有些数据库会做得更激进，对过程做JIT；
  (3).发生不可串行化冲突时，事务可以直接在数据库服务端重新执行；
  (4).许多优化都是基于存储过程（近来的许多并发控制的paper都假设事务在存储过程里执行）。
2.极大的简化开发。
  很多逻辑，用JAVA写要写好多代码，而在存储过程里可能几行很简单的SQL就搞定了；
  上一条的(3)，也有同样的效果。写JAVA代码处理回滚，不仅要做很多处理，也很容易犯错，因事务不可串行化导致的数据bug往往很难发现。
```

```sql
-- 存储过程的缺点：
1.并不是所有开发人员都熟悉怎么使用存储过程，包括像怎么用SQL表示各种复杂逻辑，怎么调试存储过程。
2.SQL是标准的，但存储过程以及控制逻辑，都是各家数据库自已的方言，不可移植到其它数据库。
3.存储过程也是代码，但却和代码分离开了，存储在数据库里。版本控制困难，进而造成从开发，测试，到上线整个流程的复杂度增加。
4.写JAVA代码的人和DBA通常是两波人，引来许多管理上的问题，如，数据库权限，两边人员打乒乓球。
5.工具支持不完善，不好调试（这点取决于用什么数据库）。
```



##ORM映射



## 设计相关

> 10分钟内只允许 3 此登陆错误

```sql

```



```sql

```


