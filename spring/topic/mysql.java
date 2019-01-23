
#基础
	不区分大小写; 建议关键字大写; 表-列名小写.
	每条命令使用分号结尾.
	
//{--------<<<sql>>>--------------------------------------------------------------------
#命令行
	//[-h] 服务器ip; [-P] 端口号(默认3306,非默认必须显示指定); [-u] 用户名; [-p] 密码
	mysql -h 192.168.5.25 -P 33306 -u bluecardsoft -p
	
#sql分类
	DQL(Data Query Language)数据查询语言			//select
	DML(Data Manipulate Language)数据操作语言		//insert, update, delete
	DDL(Data Define Languge)数据定义语言			//create, drop, alter
	TCL(Transaction Control Language)事务控制语言	//commit, rollback	

#DDL
	//CREATE TABLE 表名(字段 类型 约束 COMMENT 列注释, ...) COMMENT 表注释
	
	DROP TABLE IF EXISTS flower;
	CREATE TABLE flower(
		id INT(10)  PRIMARY KEY auto_increment COMMENT '编号', //主键自增,注释
		name VARCHAR(30) NOT NULL COMMENT '花名',
		price FLOAT NOT NULL COMMENT '价格',
		production VARCHAR(50) NOT NULL COMMENT '原产地'
	) COMMENT '花花'
	
	DESC flower; //DESC 查看表的详细信息
	
	INSERT INTO flower VALUES(default,'牵牛花',1.1,'山西'); //default 自增主键传值



#-----------------------------------------------------------------------------------------------------------------#
1.if...else...逻辑
	//表 passage_system_set 中有符合条件的记录,则取之; 反之,取表 system_set 的记录
	SELECT (CASE WHEN (SELECT memo FROM passage_system_set WHERE passage_id=1 and mark="park_number") IS NULL
			THEN
				(SELECT memo FROM system_set WHERE mark="park_number")
			ELSE
				(SELECT memo FROM passage_system_set WHERE passage_id=1 and mark="park_number")
			END) memo

	//利用三目函数 IF(e1, e2, e3)
	SELECT IF ((SELECT memo FROM passage_system_set WHERE passage_id=1 and mark="park_number") IS NULL,
		(SELECT memo FROM system_set WHERE mark="park_number"),
		(SELECT memo FROM passage_system_set WHERE passage_id=1 and mark="park_number")) memo
			
	//利用函数 IFNULL(e1, e2) 
	SELECT IFNULL ((SELECT memo FROM passage_system_set WHERE passage_id=1 and mark="park_number"),
		(SELECT memo FROM system_set WHERE mark="park_number")) memo

2.
	INSERT INTO display_num(id,name)values(9,'限行信息') on DUPLICATE KEY UPDATE editflag=now();




//}







	
//{--------<<<Method>>>--------------------------------------------------------------------
	SELECT VERSION(); //数据库版本
	
	SELECT ROW_COUNT(); //返回受影响的行数
	SELECT name, DATABASE() FROM flower; //查看当前所在的数据库或表

	
	
	
	
	
	
	
	
#时间函数(MICROSECOND,SECOND,MINUTE,HOUR,DAY,WEEK,MONTH,YEAR)
	//NOW(); 当前的日期和时间	2008-12-29 16:25:46
	//CURDATE(); 当前的日期		2008-12-29
	//CURTIME(); 当前的时间		16:25:46
	
	//DATE(); 提取字符串中的日期部分. SELECT DATE("2019-01-11 14:38:11") AS Date		
	//EXTRACT(); 提取字符串的各时间部分.
	SET @OrderDate=NOW();
	SELECT EXTRACT(YEAR FROM @OrderDate) AS OrderYear, //2019
		   EXTRACT(HOUR FROM @OrderDate) AS OrderHour; //14
	
	//DATE_ADD()/DATE_SUB(); 给定日期添加(减少)指定的时间间隔
	SET @now=NOW(); SELECT DATE_ADD(@now, INTERVAL 1 DAY);	//1天后的NOW.(-1表示1天前)
	
	//DATEDIFF(); 两个日期之间的天数
	SELECT DATEDIFF('2008-12-30','2008-12-31') AS DiffDate //-1
	
	//DATE_FORMAT(); 日期时间格式化
	SELECT DATE_FORMAT(NOW(),'%Y-%m-%d %H:%i:%s') AS FormatNow; //2019-01-11 14:56:19
	
	
	
//}

//{--------<<<概念>>>--------------------------------------------------------------------
#数据库优势
	实现数据持久化存储(文件也可以). 使用管理系统统一管理和操作(文件不具备)

#相关概念
	DB(Data Base): 数据库,存储一系列有组织的数据
	DBMS(Data Base Manage System): 数据库管理系统软件,Mysql,Oracle,sql-server
	SQL(Structure Query Language): 结构化查询语言,与数据库通信
	
#linux下mysql日志
	//查找这两个文件所在位置: find / -name my.cnf(mysql.cnf)
	log-error=/var/log/mysql.log	//在上述两个文件中配置

#完全卸载
	控制面板—>程序和功能->卸载mysql server!
	删除mysql文件夹下的所有文件
	删除 "C:ProgramDataMySQL" 所有文件，如果删除不了则用360粉碎掉即可
	其中programData文件默认是隐藏的，设置显示后即可见！删除后重启电脑，重装mysql就成功了。
	
	如依然安装失败,则运行"regedit"文件，打开注册表:
		删除 "HKEY_LOCAL_MACHINESYSTEMControlSet001ServicesEventlogApplicationMySQL" 文件夹
		删除 "HKEY_LOCAL_MACHINESYSTEMControlSet002ServicesEventlogApplicationMySQL" 文件夹
		删除 "HKEY_LOCAL_MACHINESYSTEMCurrentControlSetServicesEventlogApplicationMySQL" 文件夹
		
#mysql安装
	//配置方式
	Detailed Configuration: 精确配置,需手动. (选择)
	Standard Configuration: 标准配置,自动化配置
	
	//服务器类型
	Developer Machine: 开发测试机器,mysql占用很少资源 (选择)
	Server Machine: 服务器机器,mysql占用较多资源
	Dedicated MySQL Server Machine: 专门的数据库服务器,mysql占用所有可用资源
	
	//数据库用途
	Multifunctional Database: 通用多功能型 (选择)
	Transactional Database Only: 事务处理型
	Non-Transactional Database Only: 非事务处理型
	
	//InnoDB Tablespace - 为InnoDB数据库文件选择一个存储空间
	自定义的话,要记住位置,重装的时候要选择一样的地方,否则可能会造成数据库损坏
	
	//mysql访问量,即同时连接的数目
	Decision Support(DSS)/OLAP: 20个左右
	Online Transaction Processing(OLTP): 500个左右
	Manual Setting: 自定义数目
	
	//是否启用TCP/IP连接 (启用)
	Port Number: 3306
	Enable Strict Mode: 是否启用标准模式,即是否允许细小的语法错误. (选择)
	
	//西文编码
	第三个: utf8
	
	//windows服务
	是否将mysql安装为windows服务. (选择)
	Service Name: 自定义服务名
	是否将mysql的bin目录加入到Windows PATH. (选择)
	
	//是否要修改默认root用户(超级管理)的密码
	New root password: 自定义root密码. (默认空)
	Enable root access from remote machines: 是否允许root用户在其它的机器上登陆. (否) 
	Create An Anonymous Account: 新建一个匿名用户,只可连接,不可操作,包括查询. (否)
	

//}





































