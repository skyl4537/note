[TOC]

# ali规约

>  建表规约
```sql

```
```sql

```
```sql

```
```sql

```
```sql

```
```sql

```
```sql

```
```sql

```
```sql

```
```sql

```
```sql

```
```sql

```
```sql

```










```sql

```
```sql

```
```sql

```
```sql

```
```sql

```
```sql

```
```sql

```
```sql

```
```sql

```
```sql

```
```sql

```


```sql
'1.强制' 不要使用 COUNT(列名) 或 COUNT(常量) 来替代 COUNT(*)。 COUNT(*)就是 SQL92 定义的标准统计行数的语法，跟数据库无关，跟 NULL 和非 NULL 无关。
-- 说明：COUNT(*) 会统计值为 NULL 的行，而 COUNT(列名) 不会统计此列为 NULL 值的行。

'2.强制' COUNT(DISTINCT col) 计算该列除 NULL 之外的不重复数量。
-- 例子：SELECT COUNT(DISTINCT gender) FROM student;
-- 注意：COUNT(DISTINCT col1, col2)如果其中一列全为 NULL，那么即使另一列有不同的值，也返回为 0。

'3.强制' 当某一列的值全是 NULL 时， COUNT(col)的返回结果为 0，但SUM(col)的返回结果为NULL，因此使用 SUM()时需注意 NPE 问题。
-- 正例：避免 SUM 的 NPE 问题： SELECT IF(ISNULL(SUM(g)) ,0, SUM(g)) FROM table;

'4.强制' 使用 ISNULL() 来判断是否为 NULL 值。 -- 注意： NULL 与任何值的直接比较都为 NULL。
-- (1) NULL<>NULL 的返回结果是 NULL，而不是 false。
-- (2) NULL=NULL 的返回结果是 NULL，而不是 true。
-- (3) NULL<>1 的返回结果是 NULL，而不是 true 。

'5.强制' 在代码中写分页查询逻辑时，若总条数 count 为 0 应直接返回，避免执行后面的分页语句。

"6.强制" 不得使用外键与级联，一切外键概念必须在应用层解决。
-- 说明：（概念解释）学生表中的 student_id 是主键，那么成绩表中的 student_id 则为外键。如果更新学生表中的 student_id，同时触发成绩表中的 student_id 更新，则为级联更新。外键与级联更新适用于单机低并发，不适合分布式、高并发集群；级联更新是强阻塞，存在数据库更新风暴的风险；外键影响数据库的插入速度。

'7.强制' 禁止使用存储过程，存储过程难以调试和扩展，更没有移植性。

"8.强制" 数据订正时，删除和修改记录时，要先 select，避免出现误删除，确认无误才能执行更新语句。

'9.推荐' IN操作能避免则避免，实在避免不了，需要仔细评估 IN 后边的集合元素数量，控制在 1000之内。

'10.参考'如果有全球化需要，所有的字符存储与表示，均以 utf-8 编码，那么字符计数方法注意：
-- SELECT LENGTH("轻松工作")； 返回为 12
-- SELECT CHARACTER_LENGTH("轻松工作")； 返回为 4
-- 如果要使用表情，那么使用 utfmb4 来进行存储，注意它与 utf-8 编码的区别

'11.参考'TRUNCATE TABLE 比 DELETE 速度快，且使用的系统和事务日志资源少，但 TRUNCATE 无事务且不触发 trigger，有可能造成事故，故不建议在开发代码中使用此语句。
-- 说明： TRUNCATE TABLE 在功能上与不带 WHERE 子句的 DELETE 语句相同。
```

> 7.【强制】禁止使用存储过程，存储过程难以调试和扩展，更没有移植性。

```sql
调试：线上调试一般就是打日志，在应用层，日志可以在任何一步打，但是存储过程的话，日志没法跟踪详细的执行过程。

扩展：譬如你的 产品购买流程 要增加一个动作，这时候就要修改存储过程到db里，你这时候要直接操作db，而在大公司，直接操作db只能有dba来进行，其他都要审批后使用公司自己开发的工作来进行，且只能是简单的crud。

移植：你用mysql写的存储过程，到了sql-server不一定能直接用。但是在应用层的话，程序里的crud的基础sql基本上通用的，修改下连接串一般就ok了。
```

```sql
-- 存储过程的优点（适合处理场景固定的复杂事务，不要求并发性）：
1.效率高。
    (1).与数据库服务的通信次数少；
    (2).可以直接编译成物理计划，少了parse和查询优化步骤；有些数据库会做得更激进，对过程做JIT；
    (3).发生不可串行化冲突时，事务可以直接在数据库服务端重新执行；
    (4).许多优化都是基于存储过程（近来的许多并发控制的paper都假设事务在存储过程里执行）。
2.极大的简化开发。
    很多逻辑，用JAVA写要写好多代码，而在存储过程里可能几行很简单的SQL就搞定了；上一条的(3)，也有同样的效果。写JAVA代码处理回滚，不仅要做很多处理，也很容易犯错，因事务不可串行化导致的数据bug往往很难发现。
```

```sql
-- 存储过程的缺点：
1.并不是所有开发人员都熟悉怎么使用存储过程，包括像怎么用SQL表示各种复杂逻辑，怎么调试存储过程。
2.SQL是标准的，但存储过程以及控制逻辑，都是各家数据库自已的方言，不可移植到其它数据库。
3.存储过程也是代码，但却和代码分离开了，存储在数据库里。版本控制困难，进而造成从开发，测试，到上线整个流程的复杂度增加。
4.写JAVA代码的人和DBA通常是两波人，引来许多管理上的问题，如，数据库权限，两边人员打乒乓球。
5.工具支持不完善，不好调试（这点取决于用什么数据库）。
```










# 基础相关

##基础概念

> 完全卸载

```java

```

> mysql安装

```java

```

##查询顺序


> 执行顺序

```sql
(8)SELECT (9)DISTINCT <select list>

(1)FROM [left_table]
(3)<join_type> JOIN <right_table>
(2)ON <join_condition>
(4)WHERE <where_condition>
(5)GROUP BY <group_by_list>
(6)WITH <CUBE | RollUP>
(7)HAVING <having_condition>

(10)ORDER BY <order_by_list>
(11)LIMIT <limit_number>
```

- (1).FROM：对FROM子句中的前两个表执行笛卡尔积(交叉联接），生成虚拟表 VT1。
- (2).ON：对VT1应用ON筛选器。只有那些使<join_condition>为真的行才被插入 VT2。
- (3).JOIN：如果指定了OUTER JOIN（相对于CROSS JOIN 或 INNER JOIN），主表中未找到匹配的行将作为外部行添加到 VT2，生成VT3。
- 如果FROM子句包含两个以上的表，则对上一个联接生成的结果表和下一个表重复执行步骤1到步骤3，直到处理完所有的表为止。
- (4).WHERE：对VT3应用WHERE筛选器。只有使<where_condition>为true的行才被插入VT4。
- (5).GROUP BY：按GROUP BY子句中的列对VT4中的行分组，生成VT5。
- (6).CUBE | ROLLUP：把超组（Suppergroups）插入VT5，生成VT6。
- (7).HAVING：对VT6应用HAVING筛选器。只有使<having_condition>为true的组才会被插入VT7。
- (8).SELECT：处理SELECT列表，产生VT8。
- (9).DISTINCT：将重复的行从VT8中移除，产生VT9。
- (10).ORDER BY：将VT9中的行按ORDER BY 子句中的列列表排序，生成游标（VC10)。
- (11).LIMIT：从VC10的开始处选择指定数量或比例的行，生成表VT11，并返回调用者。

**注意**：步骤10，按ORDER BY子句中的列排序上步返回的行，返回游标VC10。这一步是第一步也是唯一一步可以使用SELECT列表中的列别名的步骤。这一步不同于其它步骤的是，它不返回有效的表，而是返回一个游标。SQL是基于集合理论的。集合set不会预先对它的行排序，它只是成员的逻辑集合，成员的顺序无关紧要。对表进行排序的查询可以返回一个对象，包含按特定物理顺序组织的行。ANSI把这种对象称为游标。理解这一步是正确理解SQL的基础。

所以，不要为表中的行假设任何特定的顺序。换句话说，`除非确定要有序行，否则不要指定ORDER BY子句`。排序是需要成本的，mysql需要执行有序索引扫描或使用排序运行符。

> 查询字句顺序

五种子句严格顺序：`where → group by → having → order by → limit`

## 约束相关

> 约束：一种限制，用于限制表中的数据，为了保证表中数据的准确性和可靠性。

```sql
NOT NULL-- 非空约束，保证该字段的值不能为空。姓名
DEFAULT -- 默认约束，保证该字段有默认值。性别

PRIMARY KEY -- 主键约束，保证该字段的值具有唯一性，且非空。学号
UNIQUE      -- 唯一约束，只保证该字段的值具有唯一性，可为空。如座位号

CHECK       -- 检查约束，但mysql不支持。如性别只能写男女，年龄只能写1-200等。
FOREIGN KEY -- 外键约束，表中该字段的值必须来自主表的关联列的值。
```
```sql
SHOW INDEX FROM `coupon`;
SHOW KEYS FROM `coupon`; -- 查看索引

ALTER TABLE `coupon` DROP index 索引名; -- 删除索引

ALTER TABLE `coupon` ADD key (索引名); -- 新增 普通索引
ALTER TABLE `coupon` ADD primary key 索引名; -- 新增 主键索引
ALTER TABLE `coupon` ADD UNIQUE key (索引名); -- 新增 唯一索引
```

> 主键 & 唯一

```sql
主键 -- 每个表中只能有一个，值必须保证唯一性，且不能为 null。
唯一 -- .......可以有多个，.............，但可以为 null（最多只能有一行 null）。

对于 组合主键 或 组合唯一，二者都支持，但不推荐使用。
组合主键 和 组合唯一 的唯一性：如id，name，只有两条数据的id，name都相同才报错，即不满足唯一性。
```

> 外键约束

```sql
1.外键要求在'从表'上进行设置
2.从表的外键类型和主表的关联列的'类型'要求一致或兼容，名字不一定要一样
3.主表的关联列必须是一个 key（主键或唯一键）
```

> 标识列：又称为自增长列，含义：可以不用手动的插入值，系统提供默认的序列值

```sql
1.标识列必须和主键搭配吗？ -- 不一定，但要求是一个key（也可以是unique）
2.一个表可以有几个标识列？ -- 至多一个！
3.标识列的类型只能是'数值型'
4.标识列可以通过 SET auto_increment_increment=3;设置步长；另外可以通过 手动插入值，设置起始值
```



# 相关练习

## 其他练习

> 查询出2门及2门以上不及格者的平均成绩。

```sql
-- 表达式score < 60返回0和1，通过sum( )函数可以计算出不及格科目大于2的学生信息
SELECT student_id,SUM(num<60) gk,AVG(num) pj
FROM score sc
GROUP BY student_id
HAVING gk>1;
```

> 查询平均工资最低的部门信息

```sql
-- 子查询方式
SELECT * -- 4.部门信息
FROM departments
WHERE department_id=(
    SELECT department_id -- 3.平均工资=最低平均工资的部门id
    FROM employees
    GROUP BY department_id
    HAVING AVG(salary)=(
        SELECT MIN(ag) -- 2.最低的平均工资
        FROM(
            SELECT AVG(salary) ag -- 1.各部门的平均工资
            FROM employees
            GROUP BY department_id
        )A
    )
);
```

```sql
-- 排序方式
SELECT * -- 2.部门信息
FROM departments
WHERE department_id=(
    SELECT department_id -- 1.各部门的平均工资，按平均工资正序排序，并取第一条
    FROM employees
    GROUP BY department_id
    ORDER BY AVG(salary)
    LIMIT 1
);
```





## 数据准备

```sql

```

## 练习01-10

> 

```sql

```

> *7.查询学过"001"并且也学过编号"002"课程的同学的学号，姓名

```sql
-- 先查到既选择001又选择002课程的所有同学
-- 根据学生进行分组，如果学生数量等于2表示，两门均已选择

SELECT s.sid,s.sname
FROM
(SELECT student_id,course_id FROM score WHERE course_id BETWEEN 1 AND 2) AS A
LEFT JOIN student AS s ON s.sid=A.student_id
GROUP BY s.sid
HAVING COUNT(A.course_id)>1;
```

```sql
-- 分别查出选了 001和002 的学生
-- 然后将上两张临时表取交集，以学生id作为关联

SELECT DISTINCT A.student_id AS sid,student.sname AS sname -- 非最优解
FROM
(SELECT student_id,course_id FROM score WHERE course_id='001') AS A
JOIN
(SELECT student_id,course_id FROM score WHERE course_id='002') AS B
ON A.student_id=B.student_id
LEFT JOIN student ON student.sid=A.student_id;
```

> *8.查询学过"李平老师"老师所教的所有课的同学的学号，姓名

```sql
-- 先查询李平老师所有课程id
-- 查询学过课程①的所有学生
-- 注意是学过李平老师的所有课程，所以对②以学生id进行分组处理，筛选选课数=李平老师所有课程数

SELECT DISTINCT student_id,sname
FROM score LEFT JOIN student ON student.sid=score.student_id
WHERE course_id IN(
    SELECT c.cid AS cid
    FROM course AS c JOIN teacher AS t ON t.tid=c.teacher_id WHERE t.tname='李平老师'
    ) AS A GROUP BY student_id
    HAVING COUNT(course_id)=(
        SELECT COUNT(1) FROM course AS c JOIN teacher AS t ON t.tid=c.teacher_id 
        WHERE t.tname='李平老师'
    );
```

> *9.查询课程编号"002"的成绩比课程编号"001"课程低的所有同学的学号，姓名

```sql
SELECT A.student_id
FROM
(SELECT student_id,num FROM score JOIN course ON course.cid=score.course_id
    WHERE cid='002') AS A
JOIN
(SELECT student_id,num FROM score JOIN course ON course.cid=score.course_id
    WHERE cid='001') AS B
ON B.student_id=A.student_id
WHERE A.num<B.num;
```

```sql
-- 交叉查询？
SELECT sid,sname 
FROM student s
WHERE(
    (SELECT num FROM score sc WHERE s.sid=sc.student_id AND sc.course_id='002')
    <
    (SELECT num from score sc WHERE s.sid=sc.student_id AND sc.course_id='001')
);
```

> 10.查询有课程成绩小于60分的同学的学号，姓名

```sql
SELECT DISTINCT student_id,s.sname
FROM score sc
JOIN student s ON s.sid=sc.student_id
WHERE num<60;
```

```sql
-- 子查询。子查询的效率 < 关联查询，因为子查询走的是笛卡尔积
SELECT sid,sname FROM student WHERE sid IN
(SELECT DISTINCT student_id FROM score WHERE num<60);
```

##练习11-20

> 11.查询没有学全所有课的同学的学号，姓名

```sql
--1.先查询所有课程数目
--2.查询选修的课程数目 <① 的学生id

SELECT s.sid,sname,COUNT(sc.course_id) cnt
FROM student s LEFT JOIN score sc ON sc.student_id=s.sid
GROUP BY s.sid
HAVING cnt<(
    SELECT COUNT(*)
    FROM course
);
```

> 12.查询至少有一门课与学号为"001"的同学所学相同的同学的学号和姓名
>
> 13.查询至少学过学号为"001"同学所选课程中任意一门课的其他同学学号和姓名

```sql
-- 0.至少学过一门，使用关键字 ANY 或者 IN。
-- 1.学号“001”所有课程号
-- 2.查出所有学过课程①的学生id，并排除"001"。

SELECT DISTINCT s.sid,s.sname -- ②
FROM student s JOIN score sc ON sc.student_id=s.sid
-- WHERE s.sid<>'001' AND sc.course_id=ANY(
WHERE s.sid<>'001' AND sc.course_id IN(
    SELECT course_id -- ①
    FROM score
    WHERE student_id='001'
);
```

> **14.查询和"002"号的同学学习的课程完全相同的其他同学学号和姓名

```sql
-- 1.查出002学过的课程id
-- 2.查出学过①以外课程的学生id，这些学生的课程肯定和002不一样
-- 3.查出②以外的学生id，这些同学的课程都是002的子集，再通过课程数量排除002的真子集

SELECT * -- ③
FROM score
WHERE student_id<>'002' AND student_id NOT IN(
    SELECT student_id -- ②
    FROM score
    WHERE course_id NOT IN(
        SELECT course_id -- ①
        FROM score
        WHERE student_id='002'
    )
)
GROUP BY student_id
HAVING COUNT(course_id)=(SELECT COUNT(course_id) FROM score WHERE student_id='002');
```

> 15.查询学习"李平老师"老师课的Score表记录

```sql
SELECT *
FROM score
WHERE course_id IN(
    SELECT cid -- 李平老师教过的课程id
    FROM course c JOIN teacher t ON t.tid=c.teacher_id
    WHERE t.tname='李平老师'
);
```

```sql
SELECT sc.* --连表查询
FROM score sc JOIN course c ON c.cid=sc.course_id
RIGHT JOIN teacher t ON t.tid=c.teacher_id
WHERE t.tname='李平老师';
```

> 16.向SC表中插入一些记录：①没有上过编号"002"课程的同学学号；②插入"002"号课程的平均成绩

```sql
-- 学过002的学生id，再取反
-- 查出002课程的平均成绩

INSERT INTO score(student_id, course_id, num)
SELECT sid,'002',(SELECT AVG(num) FROM score WHERE course_id='002') avg_num
FROM student
WHERE sid NOT IN(
    SELECT student_id
    FROM score
    WHERE course_id='002'
);
```

> 16.按平均成绩从低到高显示所有学生的"语文"."数学"."英语"三门的课程成绩，如下形式显示： 学生ID,生物成绩,物理,美术,有效课程数,有效平均分

```sql
SELECT student_id '学生id',
(SELECT num FROM course c JOIN score sc ON sc.course_id=c.cid 
    WHERE c.cname='生物' AND sc.student_id=score.student_id) '生物', -- 子查询和父查询，字段结合
(SELECT num FROM course c JOIN score sc ON sc.course_id=c.cid 
    WHERE c.cname='物理' AND sc.student_id=score.student_id) '物理',
(SELECT num FROM course c JOIN score sc ON sc.course_id=c.cid 
    WHERE c.cname='美术' AND sc.student_id=score.student_id)  '美术',
COUNT(score.course_id) '有效课程数',
AVG(num) '平均有效分'
FROM score
GROUP BY student_id
ORDER BY AVG(num) ASC;
```

> 17.查询各科成绩最高和最低的分：以如下形式显示：课程ID，最高分，最低分

```sql
SELECT course_id '课程id',MAX(num) '最高分',MIN(num) '最低分'
FROM score
GROUP BY course_id;
```

> *18.按各科平均成绩从低到高和及格率的百分数从高到低顺序

```sql
-- 难点在于 统计及格人数，使用 CASE WHEN...THEN...
SELECT course_id,AVG(num) avg,
    SUM(CASE WHEN score.num<60 THEN 0 ELSE 1 END)/COUNT(1)*100 percent
FROM score
GROUP BY course_id
ORDER BY avg ASC, percent DESC;
```

> 19.课程平均分从高到低显示（显示任课老师）

```sql
SELECT AVG(num) avg,c.cid,t.tname
FROM score sc
JOIN course c ON c.cid=sc.course_id 
JOIN teacher t ON t.tid=c.teacher_id
GROUP BY sc.course_id
ORDER BY avg DESC;
```

> *20.查询各科成绩前三名的记录（不考虑成绩并列情况） 

```sql
-- 各科成绩
SELECT c.cid AS 课程ID,c.cname AS 课程,
(SELECT num FROM score AS s WHERE s.course_id=c.cid GROUP BY num ORDER BY num DESC LIMIT 0,1) AS 第一, 
(SELECT num FROM score AS s WHERE s.course_id=c.cid GROUP BY num ORDER BY num DESC LIMIT 1,1) AS 第二, 
(SELECT num FROM score AS s WHERE s.course_id=c.cid GROUP BY num ORDER BY num DESC LIMIT 2,1) AS 第三 
FROM course c;
```

##练习21-30

> 32.查询选修"杨艳"老师所授课程的学生中，成绩最高的学生姓名及其成绩

```sql

```

> 34.查询不同课程但成绩相同的学生的学号.课程号.学生成绩

```sql

```

> 35.查询每门课程成绩最好的前两名

```sql

```

> 37.查询全部学生都选修的课程的课程号和课程名

```sql

```

> 38.查询没学过"叶平"老师讲授的任一门课程的学生姓名

```sql

```

> 39.查询两门以上不及格课程的同学的学号及其平均成绩

```sql

```

> 

```sql

```

> 

```sql

```

> 查询选修“李平老师”所授课程的学生中，成绩最高的学生姓名及其成绩

```sql
SELECT s.sid,s.sname,sc.num
FROM score sc JOIN student s ON s.sid=sc.student_id
WHERE num=(
SELECT MAX(num)
FROM score sc LEFT JOIN course c ON c.cid=sc.course_id LEFT JOIN teacher t ON t.tid=c.teacher_id
WHERE t.tname='李平老师'
);
```

> 查询不同课程但成绩相同的学生的学号.课程号.学生成绩；

```sql

```

> 查询每门课程成绩最好的前两名

```sql
SELECT cid AS 课程ID,cname AS 课程, 
(SELECT num FROM score AS s WHERE s.course_id=c.cid GROUP BY num ORDER BY num DESC LIMIT 0,1) AS 第一, 
(SELECT num FROM score AS s WHERE s.course_id=c.cid GROUP BY num ORDER BY num DESC LIMIT 1,1) AS 第二 
FROM course AS c ---？？？
```

> 查询全部学生都选修的课程的课程号和课程名

```sql
SELECT course_id,COUNT(1) cnt
FROM score
GROUP BY course_id
HAVING cnt=(
    SELECT COUNT(1)
    FROM student
);
```

> 查询没学过"叶平"老师讲授的任一门课程的学生姓名

```sql
SELECT student.sid,student.sname
FROM student s
WHERE s.sid NOT IN 
    (SELECT DISTINCT student_id FROM score WHERE course_id IN
        (SELECT cid FROM course LEFT JOIN teacher ON course.teacher_id=teacher.tid 
        WHERE teacher.tname="李平老师")
    );
```

> 查询两门以上不及格课程的同学的学号及其平均成绩

```sql
SELECT student_id, AVG(num) avg_num,
SUM((CASE WHEN num<60 THEN 1 ELSE 0 END)) sum_60
FROM score
GROUP BY student_id
HAVING sum_60>1;
```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```

> 

```sql

```



## 题目

```
2.查询"生物"课程比"物理"课程成绩高的所有学生的学号；

3.查询平均成绩大于60分的同学的姓名和平均成绩； 

4.查询所有同学的学号.姓名.选课数.总成绩；

5.查询姓"李"的老师的个数；

6.查询没学过"李平"老师课的同学的学号.姓名；

7.查询学过"001"并且也学过编号"002"课程的同学的学号.姓名；

8.查询学过"叶平"老师所教的所有课的同学的学号.姓名；

9.查询课程编号"002"的成绩比课程编号"001"课程低的所有同学的学号.姓名；

10.查询有课程成绩小于60分的同学的学号.姓名；

11.查询没有学全所有课的同学的学号.姓名；

12.查询至少有一门课与学号为"001"的同学所学相同的同学的学号和姓名；

13.查询至少学过学号为"001"同学所有一门课的其他同学学号和姓名；

14.查询和"002"号的同学学习的课程完全相同的其他同学学号和姓名；

15.删除学习"叶平"老师课的SC表记录；

16.向SC表中插入一些记录，这些记录要求符合以下条件：①没有上过编号"002"课程的同学学号；②插入"002"号课程的平均成绩； 

17.按平均成绩从低到高显示所有学生的"语文"."数学"."英语"三门的课程成绩，按如下形式显示： 学生ID,语文,数学,英语,有效课程数,有效平均分；

18.查询各科成绩最高和最低的分：以如下形式显示：课程ID，最高分，最低分；

19.按各科平均成绩从低到高和及格率的百分数从高到低顺序；

20.课程平均分从高到低显示（现实任课老师）；

21.查询各科成绩前三名的记录:(不考虑成绩并列情况) 



32.查询选修"杨艳"老师所授课程的学生中，成绩最高的学生姓名及其成绩；


34.查询不同课程但成绩相同的学生的学号.课程号.学生成绩；

35.查询每门课程成绩最好的前两名；


37.查询全部学生都选修的课程的课程号和课程名；

38.查询没学过"叶平"老师讲授的任一门课程的学生姓名；

39.查询两门以上不及格课程的同学的学号及其平均成绩；



```











