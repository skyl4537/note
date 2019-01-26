#!/bin/bash

//{--------<<<基础配置>>>-----------------------------------------------------------------
#putty配置
	字体大小: Windows -> Appearance -> Font Settings -> Consolas(12)
	绿色字体: Windows -> Color -> Default Foregroud -> 0 255 0
	保存配置: Session -> Saved Sessions -> 选中默认 -> Save -> Apply

#shell命令注意点: 
	(1).区分大小写; cd和CD表示不同指令. 
	(2).指令太长,可使用进行换行处理 \

#输入(输出)重定向
	#command > file		//将输出重定向到file
	#command >> file	//将输出 [追加] 到file
	#command < file		//将输入重定向到file


//}

//{--------<<<单一指令>>>-----------------------------------------------------------------
 kill -9 `lsof -t -i:8090`
	#kill -9 `lsof -i:8090 | awk '{print $2}' | sed -n '2p'`		//等同于上面命令
	#nohup java -jar blue.jar > ./logs/blue.log >/dev/null 2>&1 &	//不再输出日志
	nohup java -jar blue.jar > ./logs/blue.log 2>&1 &				//输出日志

	1). `` -> 引用其命令的执行结果
	2). awk '{print $2}' -> 输出第二列内容
	3). sed -n '2p' -> 输出第二行
	4). nohup command & -> &为后台执行的意思; nohup是不挂起的意思(no hang up),它保证在退出帐户之后继续运行相应的进程.
	5). /dev/null 文件 -> 看作'黑洞'
		#非常等价于一个只写文件. 所有写入它的内容都会永远丢失. 而尝试从它那儿读取内容则什么也读不到. 
		#但 /dev/null 文件非常有用,将命令的输出重定向到它,会起到"禁止输出"的效果。

		#如果希望屏蔽 stdout 和 stderr,可以这样写:
		command > /dev/null 2>&1
		
		#一般情况下,每个 Unix/Linux 命令运行时都会打开三个文件:
		a).标准输入文件(stdin): stdin的文件描述符为0, Unix程序默认从stdin读取数据。
		b).标准输出文件(stdout): stdout 的文件描述符为1, Unix程序默认向stdout输出数据。
		c).标准错误文件(stderr): stderr的文件描述符为2, Unix程序会向stderr流中写入错误信息。
		# 2>&1 -> 表示stderr的输出方式同stdout, 都是禁止输出
	
#lsof: list open files -> 列出当前系统打开的文件. 安装 -> yum install lsof
	#默认: 没有选项, 列出所有活跃进程打开的所有文件
	#-u uName: 显示所属user进程打开的文件
	#-n: 不将IP转换为hostname,缺省是不加上-n参数
	#-a: 所有参数都满足时才显示结果
	#-c string: 显示 COMMAND 列中包含指定字符的进程所有打开的文件
	#-t: 仅获取进程ID
	
		lsof -t -c java -i:8080		//过滤条件 -c -i 默认是 或 的关系
		lsof (-a) -t -c java -i:8080	//-a 代表 且 的关系
	
	#fileName: 显示指定'文件'相关的所有进程
	#+d /DIR/: ........'目录'..............
	#+D /DIR/: ........'目录及子目录'...... (递归显示)
	
		lsof blue/logs/info		
		lsof +d blue/logs		
		lsof +D blue/logs		//迭代显示,当前目录及其子目录的打开文档信息
	
	#-p<进程号>: 列出指定进程号所打开的文件
		lsof -p PID1,,PID2 |wc -l	//显示进程号(可以多个)打开文件的总数, (wc -l 文件行数)
	
	
	#lsof -i[46] [protocol][@hostname|hostaddr][:service|port]
	#	46 --> IPv4 or IPv6
	#	protocol --> TCP or UDP
	#	hostname --> Internet host name
	#	hostaddr --> IPv4地址
	#	service --> /etc/service中的 service name (可以不只一个)
	#	port --> 端口号 (可以不只一个)
	
		lsof -i:8090		//端口8090相关的网络信息
		lsof -t -i:8090		//仅显示端口8090对应的进程PID
		
		lsof -i6:8090		//端口8090的IPV6进程
		lsof -i tcp			//仅显示tcp连接(udp同理)
		lsof -i@host		//显示基于指定主机的连接
		lsof -i@host:port	//显示基于主机与端口的连接

#more: 基于vi编辑器的文本过滤器,按页显示文件内容,支持vi中的关键字定位操作
	#一次显示一屏文本,满屏后停下来,并在屏幕底部提示至今己显示的百分比: --More--(XX%)
	#Enter: 下一行内容;	空格: 下一屏(页)内容;	B:上一屏内容;	Q: 退出命令
	
	more -5 file	//只显示5行内容(非显示一屏)
	more +100 file	//从100行开始显示	
	
#tail: 默认显示指定文件的末尾10行; 如给定多个文件,则在显示时,每个文件前加一个文件名标题. 如未指定文件或文件名为'-',则读取标准输入
	#-c<N>或——bytes=<N>: 文件尾部的N个字节内容
	#-n<N>或——line=<N>: 文件尾部的 N 行内容.
	#-f: 动态显示文件最新的追加内容 (适合查看日志)
	#-s<秒数>或——sleep-interal=<秒数>: 与'-f'选项连用,指定监视文件变化的间隔秒数
	#--pid=<进程号>：与'-f'选项连用,当指定的进程号的进程终止后,自动退出tail命令

	tail -n 5 file	//最后5行内容
	tail -n +5 file	//第5行至末尾(包含第5行)	
	
	tail -f -n 3 file	//循环查看文件的最后三行内容
	
	tail -c 10 file	//最后10个字符	
	
#sed: 利用script来处理文本文件
	#-n: 仅显示script处理后的结果(通常与p相结合使用)

	#sed -n '[i],[j]p' -> 输出第i到第j行内容. 其中,j<=i时,只输出第i行
	
	#head -n i file -> 文件的前i行内容
	
	#查看文件第3行到第5行内容. (顺序不同,执行过程不同)
	sed -n '3,5p'
	
	cat file | head -n 5 | tail -n +3
	cat file | tail -n +3 | head -n 3
	
	'cat tail head more sed': cat只能查看整个文件,查看指定行数得配合head或tail使用
	
	# cat -n file -> 显示文件的行号,从1开始
	cat -n error | head -n 10	等同于 head -n 10
	
#df: 显示目前在Linux系统上的文件系统的磁盘使用情况统计
	#-l: --local 限制列出的文件结构

#du: 显示指定的目录或文件所占用的磁盘空间
	#--max-depth=<目录层数>: 超过指定层数的目录后,予以忽略

	# 相结合使用,查看磁盘情况
	df -lh
	du -h --max-depth=1 /var/lib/webpark/logs
	
#zip: zip压缩和unzip解压缩
	#-q: 执行时不显示任何信息 
	#-r: 迭代压缩目录及子目录
	#-l: 显示压缩文件内所包含的文件(不解压的前提)
	
	zip -qr sm.zip /logs/sm		//将目录 /logs/sm 下所有文件和文件夹压缩为当前目录下的 sm.zip
	zip -qr sm.zip *			//当前目录为 /logs/sm ,则使用此命令完成以上功能
	
	zip -d sm.zip a.log			//从压缩文件中删除文件a.log	
	
	unzip -l sm.zip				//查看压缩包中的文件信息(不解压)	
	
#tar: 打包文件(可指定压缩), 用于备份文件!!!(相比zip更优)
	#-z: 通过gzip指令处理备份文件	->	gzip
	#-v: 显示指令执行过程			->	verbose
	#-f: 指定备份文件				->	file
	#-c: 建立新的备份文件			->	create
	#-x: 从备份文件中还原文件		->	extrac
	#-t: 列出备份文件的内容			->	list
	#-r: 新增文件到已备份文件的尾部	->	append
	#-g: 增量备份

	tar -zcvf test.tar.gz test		//压缩文件test
	
	tar -zxvf test.tar.gz			//解压到<当前>目录
	tar -zxvf test.tar.gz -C test/	//解压到<指定>目录
	
	tar -ztvf test.tar.gz			//列出归档文件的内容
	
	echo -n "123" > test	//-n表示不换行,即结尾没有换行符
	tar -g snapshot -zcvf test0.tar.gz test	//第1次归档(123)
	echo "456" >> test		//追加test末尾
	tar -g snapshot -zcvf test1.tar.gz test	//第2次归档(123456)
	tar -g snapshot -zcvf test2.tar.gz test	//第3次归档(空的,因为没有修改)
	
#echo: ""会将内容转义; ''不会转义,原样输出
	echo "$(date)"			//输出当前时间
	echo '$(date)'			//输出 $(date)
	
	echo $(date '+%Y-%m-%d %H:%M:%S')	//输出时间格式化

	echo a b c | awk '{print $1,$3}'	//查看一行的第一和第三列
	
	//执行结果两种表达方式: `date`  $(date) ,后者适用于嵌套情况
	echo $(lsof -p $(lsof -t +D /var/lib/webpark/logs/device) |wc -l)
	
	//,如: $(lsof -p $(lsof -t -i:8080|sed -n '1p') |wc -l)
	echo $(date) '---' $(lsof -p $(lsof -t -i:8080|sed -n '1p') |wc -l) >> /file
	
#split: 切割文件
	#-d: 使用数字作为后缀
	#-b: 指定每多少字节切成一个小文件
	#-l <行数> / -<行数>: 指定每多少行切成一个小文件
	
	split [-6/-l 6] a.log		#将a.log每6行分割成一个文件
	split -b 10m park.log		#每个小文件大小为10MB
	split -b 10k sm.log sm -d	#小文件10kb/个,前缀sm (默认是x), 后缀从00开始的数字
	
#find: 根据 [文件属性] 进行 [递归] 查找
	#-name: 根据文件名查找;	iname: 忽略大小写
	#-size: 文件大小;	-user: 所属用户;	-empty: 空文件

	find . -name 'sm*'		//(递归)-当前目录下名为 'sm' 或 'sm*' 的文件及文件夹
	find . -empty 　　		//(递归)-.......... '空' 文件或者文件夹
	find . -size +10M		//(递归)-.......... 大于10MB的文件(c:字节, w:双字, k:KB, M:MB, G:GB) (+:大于,-:小于)

	#混合查找 ---> 参数: !; -and(-a); -or(-o)
	find /tmp -size +10000c -and -mtime +2	#在/tmp目录下查找大于10000字节并在最后2分钟内修改的文件
	find / -user fred -or -user george		#在/目录下查找用户是fred或者george的文件文件
	find /tmp ! -user panda					#在/tmp目录中查找所有不属于panda用户的文件
	
#grep: 根据 [文件内容] 进行查找
	#-n: 显示匹配行及行号
	#-w: 只匹配整个单词,而不是字符串的一部分(如匹配'magic',而不匹配'magical')
	#-r: 迭代查找,包含子目录
	#-d: 对象为目录时,使用此命令 grep -d skip
	#-i: 不区分大小写
	#-h: 查询多文件时不显示文件名
	#-l: 查询多文件时只输出包含匹配字符的文件名
	#-a: 不要忽略二进制数据。
	#-A <行数n>: 除显示符合范式那一列之外,并显示该行之[后]的n行内容
	#-B <行数n>: 除显示符合范式那一行之外,并显示该行之[前]的n行内容
	
	grep 'test' d* --color=auto			//显示所有以d开头的文件中 包含test 的行,并标记颜色
	grep -w 'test' aa bb cc				//显示在aa,bb,cc文件中 单词test 的行
	grep -win 'error' *.log				//显示当前目录下 以.log结尾, 包含单词error, 所以行及行号
	grep 'magic' /usr/src				//显示/usr/src目录下的文件(不含子目录)包含magic的行
	grep -r 'magic' /usr/src			//显示/usr/src目录下的文件(包含子目录)包含magic的行
	
	grep -d skip -n '代扣' * > file		//忽略子目录
	grep -r ...							//迭代搜索子目录
	
	#使用正则表达式 -E 选项
		# :	忽略正则表达式中特殊字符的原有含义
		# ^:	匹配正则表达式的开始行
		# $:	匹配正则表达式的结束行
		# <:	从匹配正则表达式的行开始
		# >:	到匹配正则表达式的行结束
		# []:	单个字符,如[A]即A符合要求 
		# .:	所有的单个字符
		# *:	有字符,长度可以为0
		# [-]:	范围,如[A-Z],即A到Z都符合要求
		
	grep -inE '*c29a2' *.log > file		#功能==后者
	egrep -in '*c29a2' *.log > file		#过滤出日志中包含"c29a2"字符串的行,并导出到file文件
	
#whereis: 
	只能用于查找二进制文件,源代码和man手册页; 一般文件的定位需使用locate命令

#which: 在环境变量 $PATH 设置的目录里查找符合条件的文件
	whereis或which java		//显示java所在位置
	
#locate: 搜索linux系统中的文件
	locate mysql.cnf	//查找mysql.cnf文件
		
	'locate与findの不同': find是去硬盘找, locate只在(/var/lib/slocate.db)资料数据库中找,速度快,但不是实时的
	locate -u			#//ocate数据库一般是系统自己维护,也可以通过命令手工升级数据库
	
#ls: 显示指定目录下的内容(文件及子目录)
	#-a: 显示所有,包含.开头的隐藏文件		-l: 显示文件的详细信息	-h: 以MB,GB等形式显示文件大小
	#-t: 以最后修改时间降序排列(先新后旧)	-F: 在文件名称后加一符号(如,可执行文档加'*',目录则加'/')
	#-r: 以相反次序显示(原以英文字母次序)	-R: 迭代所有文件(包含子目录下文件)
	
	ls -lhtr s*		//列出目录下所有名称 s 开头的文件,以最后修改时间升序排列.(r代表排序取反)

#chmod: 变更文件或目录的权限
	#-R: 递归处理,将目录下所有文件及子目录一并处理

	chmod -R 777 ./sm	//sm目录及其子目录皆赋值777权限

#wget: 从指定的URL下载文件
	#-o: 下载信息写入日志,而非显示控制台
	#-O: 下载并以不同的文件名保存.(默认以最后一个/后面的字符来命名,动态链接文件名会不正确)
	#-c: 继续执行上次终端的任务.(断点续传)
	#-b: 进行后台的方式运行wget
	#-i<文件>: 从指定文件获取要下载的URL地址
	
	wget -o download.log URL 
	wget -O wordpress.zip http://www.linuxde.net/download.aspx?id=1080
	wget -c http://www.linuxde.net/testfile.zip
	
	wget -b http://www.linuxde.net/testfile.zip
		//Continuing in background, pid 1840.
		//Output will be written to `wget-log'.
	tail -f wget-log //后台下载,使用以下命令来察看下载进度
	
	wget -i filelist.txt //先将各url写入文件.(一行一个??)
	
#ulimit: 限制系统用户对shell资源的访问
	#-a: 显示当前所有的资源限制
	#-n <size>: 指定(显示)同一时间最多打开文件数
	#-u <size>: 指定(显示)用户最多可开启的程序数目
	
	sudo nano /etc/security/limits.conf //最大句柄数(永久生效)
	//追加: *                -       nofile          10240

#uniq 统计重复行,一般与 sort 结合使用
	#-u: 仅显示出一次的行列
	#-c: 在行首显示该行重复出现的次数

	//当重复的行并不相邻时, uniq 命令不起作用. 此时必须配合 sort 使用.
	sort file | uniq -c | sort -r
	
#sort 将文件内容进行排序
	#-n: 正序排序(按数值大小,默认正序)
	#-r: 反序排列
	#-u: 忽略相同行		
	#-t<分隔符>: 指定排序时所用的栏位分隔字符	+<起始栏位>-<结束栏位>: 以指定的栏位来排序,范围由起始栏位到结束栏位的前一栏位
	
	sort (-r) file	//正序(倒序)排列
	sort -t $'\t' -k 2.7 file	//以 TAB 分割为列,对第2列的第7个字符进行排序
	
#cut 用于显示每行从开头算起 num1 到 num2 的文字
	#-b: 以字节为单位进行分割. 这些字节位置将忽略多字节字符边界,除非也指定了 -n 标志。
	#-c: 以字符为单位进行分割.
	#-d: 自定义分隔符,默认为制表符.
	
	who|cut -b 3	//提取每一行的第3个字节
	who|cut -b 3-	//.....................到行尾

//}

//{--------<<<常用指令>>>-----------------------------------------------------------------
'常用命令': http://www.runoob.com/w3cnote/linux-useful-command.html

#touch 新建空文件,或修改文件的时间属性.(ls -l 查看文件的时间属性)
	touch file		//新建空文件file
	
#rm 用于删除一个文件或者目录
	#-i 删除前逐一询问确认
	#-r 递归删除目录
	#-f 强制删除,无需确认.(即使文件属性为只读)

	rm -rf /var/log

#mv	移动,或重命名
	mv /a.txt /b.txt		//两目录一致, 指定新文件名 --> 重命名
	mv /a.txt /test/c.txt 	//.....不..., 指定新文件名 --> 移动+重命名
	
	mv /a.txt /test/		//.....不..., [没]指定新文件名 --> 移动
	
	mv /student/_* . 		//批量移动
	
#cp 用于复制文件或目录
	#-f 覆盖已存在的目标文件而不提示
	#-i 与-f相反; 在覆盖目标文件之前给出提示,要求用户确认是否覆盖
	#-r 若源文件是目录,此时将复制该目录下所有的子目录和文件
	#-l 不复制文件,只生成链接文件
	
	cp –r test/ newtest 	//批量复制
	
#清空文件
	(1).  > file				//使用重定向方法
	(2). true > file			//使用true命令重定向清空文件
	(3). echo -n "" > file	//要加上"-n"参数,默认情况下会有"\n",即有个空行
	
	
#端口占用(windows/Linux) - windown换行为'^', linux为'\'
	netstat -aon | findstr 8080		//根据端口查找pid
	taskkill -f /pid 9984			//强制杀死pid 9984
	tasklist | findstr 10876		//根据pid查找进程名

	#linux版
	lsof -i:8080 //只显示pid -> lsof -t -i:8080
	netstat -anp | grep 8080 //netstat用于显示网络状态 - 过滤端口
	ps -aux|grep java //ps用于显示当前进程(process)的状态 - 过滤进程
	
	kill -9 21458 //杀死进程
	telnet ip port //检查端口是否可以连接
	
#软件相关
	apt-get update				//更新安装列表

	dpkg -l | grep x			//从已安装软件中确定是否安装了软件x 
	apt-get --purge remove x	//删除软件及配置
	apt-get autoremove x		//卸载软件及其依赖的安装包
	
	wget url //下载指定链接
	
#系统相关
	lsb_release -a //系统相关信息
	
#防火墙
	sudo apt-get install ufw			//安装ufw
	sudo ufw status/enable/disable		//查看/开启/关闭 (active / inactive)
	sudo ufw default deny		//开启防火墙,并随系统启动同时关闭所有外部对本机的访问. (本机访问外部正常)
	
	sudo ufw allow 80			//允许外部访问80端口
	sudo ufw delete allow 80	//禁止外部访问80 端口
	
	sudo ufw allow from 192.168.1.1	//允许此IP访问所有的本机端口
	
#jps(jdk提供的一个查看当前java进程的小工具)	
	jps		//列出pid和java主类名
	jps -l	//列出pid和java主类全称
	
#free -> 当前内存的使用.(-m: 以MB为单位)
		parkmanager@bc-ai-server:/var/lib/webpark/logs/sm/task$ free -h
					 total       used       free     shared    buffers     cached
		Mem:          7.7G       7.3G       450M       176M       251M       3.7G
		-/+ buffers/cache:       3.3G       4.4G
		Swap:         7.9G       148M       7.7G

//}

//{--------<<<vi>>>-----------------------------------------------------------------------
#vi使用
	vi hello.sh		//使用Vi编辑器,新建并打开hello.sh文件
	
	默认初始为'命令模式',使用以下操作进入'编辑模式'	//使用Esc (进入或退出命令模式)
		i - 光标[前]插入文本	I - 本行[开始]插入文本
		a - 光标[后]插入文本	A - 本行[末尾]插入文本
		o - 光标[下]插入新行	O - 光标[上]插入新行
						
	'命令模式'常用命令
		x - 删除光标所在字符	15x - 删除光标所在的15个字符
		d - 删除当前行			3dd - 删除光标所在的3行	//dG - 删除光标所在到末尾的所有行
		D - 删除光标所在到行结尾	
		
		'底行模式' :n1,n2d - 删除指定范围的行

		gg - 到第一行			G - 到最后一行	//667G - 定位到667行, :667 - 也是同样定位到667行
		
		yy/Y - 复制当前行		//nyy/nY - 复制当前及以下n行
		dd - 剪切当前行			//ndd - 剪切当前及以下n行
		p/P - 粘贴在当前光标所在行的上/下
		
		r - 替换光标所在字符	R - 从光标所在处开始替换字符，按Esc结束
		u - 取消上一步操作

	在'命令模式'下按':'进入'底行模式'
		:set nu - 显示行号		:set nonu - 隐藏行号
		:w - 保存修改			:w filename - 另存为新的文件名
		:q - 退出				:q! - 强制退出
		:wq - 保存修改并退出	//等同于ZZ
		//对于只读文档修改时,提示"readonly", 使用 :wq! 强制保存退出(root有效).

//}
		
//{--------<<<定时任务>>>-----------------------------------------------------------------
#nano编辑器
	yum -y install nano //安装
	nano 路径+文件名 //新建/打开
	退出: Ctrl+x (y确认); 保存修改: Ctrl+o; 取消返回: Ctrl+c
	剪贴/删除整行: Ctrl+k; 复制整行: Alt+6; 粘贴: Ctrl+U 
	
#crontab
	rpm -qa | grep crontab //检查是否安装(rpm: Red-Hat Package Manager)
	
	sudo /etc/init.d/cron start-stop-restart //命令形式
	sudo service cron start-stop-restart //服务形式

	执行日志在 /var/log/cron.log; //对于ubuntu: http://www.cnblogs.com/nieqibest/p/9353927.html
	crontab -l(e/r) //列出(编辑/删除)当前用户的定时任务

	//分 时 日 月 周 (user可省) cmd
	m h dom mon dow (user)  command
	* * * * * cmd			// 每隔一分钟执行一次任务  
	0 * * * * cmd			// 每小时的0点执行一次任务, 如6:00; 10:00
	6,10 * 2 * * cmd		// 每个月2号, 每小时的6分和10分执行一次任务
	*/3,*/5 * * * * cmd		// 每隔3分钟或5分钟执行一次任务, 比如10:03, 10:05, 10:06 
	0 23-7/2,8 * * * cmd	// 晚上11点到早上8点之间每2个小时和早上8点 
	20 3 * * * (xxx; yyy)	// 每天早晨3点20分执行用户目录下的两个指令(每个指令以;分隔)
	0 11 4 * mon-wed /etc/init.d/smb restart	// 每月的4号与每周1到周3的11点重启smb 

	//"%"是特殊字符(换行),所以命令中必须对其进行转义(\%).
	*/2 * * * * echo $(date '+\%Y-\%m-\%d \%H:\%M:\%S')  >> file 

	#!/bin/bash
	#执行结果赋值给变量
	DATE=$(date '+%Y-%m-%d %H:%M:%S') //%对于crontab是关键字; 而对于shell脚本则不是,不需要转义
	LSOF=$(lsof -p $(lsof -t +D /var/lib/webpark/logs/device) |wc -l)
	CLOSE=$(netstat -anp |grep java |grep CLOSE |wc -l)

	cd /var/lib/webpark/logs/sm/file
	echo $DATE '---' $LSOF >> lsof
	echo $DATE '---' $CLOSE > close
	grep -d skip -n 'Init---args' ../_* > ztj
	
//}

//{--------<<<shell>>>--------------------------------------------------------------------
#算术运算符
	#原生bash不支持简单的数学运算,可通过其他命令来实现. 如: expr.
		a=10; b=20;
		
		val=`expr $a + $b` 
		val=`expr $a \* $b`	//转义符'\'
		if [ $a == $b ]		//运算符 == 前后都有空格; 且 [ 之后也有空格.

#关系运算符
	#关系运算符只支持数字,不支持字符串; 除非字符串的值是数字,如ASCII表.
	#常用: gt(>); lt(<); eq(==); ne(!=); ge(>=); le(<=); o(||); a(&&)
		a=10; b=20;
		
		if(a>0 && (b>0 || c>0))  
		<-->  if [ $b -gt 0 || $c -gt 0 -a && -gt 0 ]; then //等同
		<-->  if [ $b -gt 0 -o $c -gt 0 -a $a -gt 0 ]; then //等同

#字符串运算符
		a="abc"; b="efg";
	 
		if [ $a = $b ]	//两个字符串是否相等
		if [ -z $a ]	//长度是否为0
		if [ -n "$a" ]	//长度是否不为0
		if [ $a ]		//检测是否不为空

#文件测试运算符
	#用于检测 Unix 文件的各种属性
		file="/var/lib/webpark/logs/sm/task/file/aaa"
		
		if [ -e $file ] //文件存在
		if [ -r $file ] //可读
		if [ -w $file ] //可写
		if [ -x $file ] //可执行
		
		if [ -d $file ] //是否为目录
		if [ -s $file ] //不为空
		if [ -f $file ] //是否为普通文件(既不是目录,也不是设备文件)

#条件判断
	1.只有if
		if 条件 then
			命令
		fi
		
	2.if—elseif—else
		if 条件0 then 
			命令0
		elif 条件1 then
			命令1
		else
			命令2
		fi


#!/bin/bash
PID=$(lsof -t -i:8090)

if [ $PID ]
then
	kill -9 $PID
	echo "kill -9 port 8090 PID: $PID"
else
	echo "8090 NO PID!"
fi

cd /var/tmp
chmod 777 demo.jar

if [ ! -e "jdk1.8.0_191/bin/java" -o ! -f demo.jar ];then //-e: 文件是否存在
    echo "jdk路径或jar包不存在"
else
	nohup jdk1.8.0_191/bin/java -jar demo.jar >/dev/null 2>&1 &
	echo "start OK!~!"
fi
//}

//{--------<<<系统级别>>>-----------------------------------------------------------------
#目录结构	
	/tmp - 存放临时文件.
	/etc - 系统管理所需要的配置文件和子目录.
	/usr - 用户很多应用程序和文件都放在这个目录,类似windows下的 program file 目录.
	
#vmstat -> 给定时间间隔的服务器的状态值,包括内存,cpu,虚拟交换,IO读写等使用情况
	vmstat 2 1 //2秒采集一次,一共采集1次. (默认0,0)
	
		parkmanager@bc-ai-server:~$ vmstat -SM
		procs -----------memory---------- ---swap-- -----io---- -system-- ------cpu-----
		 r  b   swpd   free   buff  cache   si   so    bi    bo   in   cs us sy id wa st
		54  0    148    340    250   3761    0    0     4   194   11    7 23  4 72  1  0

	///重点参数: r, b, swpd, free, buff, cache, si, so, bi, bo
	
	r: 运行队列(即多少个进程真的分配到CPU).
	b: 阻塞的进程.
	
	swpd: 虚拟内存已使用的大小. //大于0,表示物理内存不足,不是程序内存泄露,就该升级内存,或把耗内存的任务迁移到其他机器
	free: 空闲的物理内存的大小. 我的机器内存总共8G,剩余3415M
	buff: 缓冲大小,一般对块设备的读写才需要缓冲
	cache: cpu和内存之间的缓冲. 一般作为文件系统进行缓冲,频繁访问的文件都会被缓存,如果cache值非常大说明缓存文件比较多,如果此时io中的bi比较小,说明文件系统效率比较好
	
	si: 每秒从磁盘读入虚拟内存的大小. 如果这个值大于0,表示物理内存不够用或者内存泄露了,要查找耗内存进程解决掉
	so: 每秒虚拟内存写入磁盘的大小. 如果这个值大于0,同上
	bi: 块设备每秒接收的块数量. 这里的块设备是指系统上所有的磁盘和其他块设备,默认块大小是1024byte,(4MB)
	bo: 块设备每秒发送的块数量. 例如读取文件,bo就要大于0. bi和bo一般都要接近0,不然就是IO过于频繁,需要调整
	
	in: 每秒CPU的中断次数,包括时间中断
	cs: 每秒上下文切换次数. //这两个值越大,会看到由内核消耗的cpu时间会越多
	
	us: 用户CPU时间. //超过50%的使用,就该考虑优化程序算法或其他措施了
	sy: 系统CPU时间. 如果太高,表示系统调用时间长,例如是IO操作频繁. //sy的值过高时,说明系统内核消耗的cpu资源多
	id: 空闲CPU时间.
	wa: 等待IO CPU时间. //一般来说, us + sy + id + wa = 100;

//}

//{--------<<<awk命令>>>-----------------------------------------------------------------
awk是一种处理文本文件的语言,是一个强大的文本分析工具.

#-F 相当于内置变量FS,指定分割字符, 默认'任何空格'
	awk '{print $1,$3}' file //每行按默认进行分割,输出分割后的第1,3项
	
	awk '{printf "%3s %-2s\n",$1,$3}' file //格式化输出, %3s显示长度最小为3个字符,不足右侧补空格; %-2s左侧补
	
	awk -F',' '{print $1,$3}' file //自定义按','分割,默认不起作用
	
	awk -F'[, ]' '{print $1}' file //多个分隔符使用 [] 括起来; 先用','分割,再用' '分割
	awk 'BEGIN{FS="[, ]"} {print $1}' file //使用内建变量,效果同上
	
#-v 设置变量
	awk -va=1 '{print $1,$1+a}' file //分割后第1项和变量a相加
		1 2 3
		A B C //源文件
		
		1 2
		A 1 //处理后输出.(第一项为数字则直接相加,第一项不为数字则当做0进行相加)

	awk -va=1 -vb=s '{print $1,$1+a,$1b}' file //分割后, 第1项和变量a相加, 第1项和变量b拼接
	
#运算符
	awk '$1>0 && $2=="#" {print $1,$3}' file //过滤第1列大于0,且第2列等于字符串"#"的行, 再输出结果的第1,3列. 
											 //对于非数字,按照ASCII进行比较
	
	
	
//}

//{--------<<<组合指令>>>-----------------------------------------------------------------
//查看不同状态的连接数数量
# netstat -n | awk '/^tcp/ {++y[$NF]} END {for(w in y) print w, y[w]}'
	TIME_WAIT 251 
	CLOSE_WAIT 16
	FIN_WAIT1 7
	FIN_WAIT2 125
	ESTABLISHED 2412
	LAST_ACK 4

//查看每个ip跟服务器建立的连接数
# netstat -nat|awk '{print$5}'|awk -F : '{print$1}'|sort|uniq -c|sort -rn
	31 45.116.147.178 
	20 45.116.147.186
	12 23.234.45.34
	11 103.56.195.17
	
	显示第5列; [-F :]以:分割; [sort]排序; [uniq -c]统计排序过程中的重复行; [sort -rn]按纯数字进行逆序排序
	
//查看每个ip建立的(ESTABLISHED/TIME_OUT)状态的连接数
# netstat -nat|grep ESTABLISHED|awk '{print$5}'|awk -F : '{print$1}'|sort|uniq -c|sort -rn
     94 127.0.0.1
     22 192.168.8.93
     20 192.168.8.66
     12 192.168.8.127

//获取eth0网卡的IP地址和MAC地址
#ifconfig eth0 |grep "inet addr:" |awk '?print $2}' |cut -c 6- //etho的ip
#ifconfig eth0 |grep "HWaddr" |awk '{print $5}' //mac地址
	(1).获取eth0网卡的信息
	(2).过滤出IP地址的行或MAC地址的行
	(3).使用awk输出指定字段
	(4).对于MAC地址,第5个字段就是MAC; 而对于IP地址,还需要对第2个字段截取第6个字符之后的内容


//}