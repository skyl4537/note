








#----------------------------------------------------------------------#
	'Redis - Remote Dictionary Server'
	
0.Redis安装 - linux下载安装包的方式. (docker方式没试通)
	$ wget http://download.redis.io/releases/redis-2.8.17.tar.gz
	$ tar -zxvf redis-2.8.17.tar.gz
	$ cd redis-2.8.17
	$ make		//make编译完后会生成 src/redis-server 和 src/redis-cli, 服务端和客户端.

	#服务端
		$ ./src/redis-server	//以默认配置启动redis
		$ ./src/redis-server redis.conf		//读取配置启动

	#配置后台启动 - redis.conf
		daemonize no -> daemonize yes
	
	#开机自启: vi /etc/rc.local 
		添加：/usr/local/redis/bin/redis-server /usr/local/redis/etc/redis.conf (意思就是开机调用这段开启redis的命令)
		
	#检测及关闭
		$ ps -ef | grep redis
		$ lsof -i:6379
		$ netstat -anp | grep redis
		$ pkill redis	//停止redis

	#卸载
		$ rm -rf /usr/local/redis //删除安装目录
		$ rm -rf /usr/bin/redis-* //删除所有redis相关命令脚本
		$ rm -rf /root/download/redis-4.0.4 //删除redis解压文件夹

	#客户端
		$ ./src/redis-cli 	//另起控制台,以此命令启动redis客户端
		
1.基础命令
	#config
		config get requirepass		//查看redis当前密码,(默认"",即无密码)
		config set requirepass 123	//设置密码123
		auth 123					//使用123登录
		
		shutdown 	//关闭服务

	#keys
		set/get/del key		//增 查 删
	
		keys *			//当前数据库的所有key
		EXISTS key		//判断某个key是否存在
		TYPE key		//获取key对应value的类型
		MOVE key db		//将某个key从当前数据库移动到指定数据库
			///MOVE msg 1; SELECT 1; EXISTS msg; -> 1.(移动成功)
			
		EXPIRE key seconds	//设置key的过期时间,超过时间后,将会自动删除该key.(单位秒)
		TTL key				//获取key的有效时间.(-1:永不过期; -2:已过期)
			///set hello world; EXPIRE hello 10; TTL hello; -> 8.
			
		LPUSH key list			//从队列的左边入队一个或多个元素
		LRANGE key start end	//返回存储在key列表里指定范围的元素.(下标从0开始计数,负数表示从list尾部开始计数)
			///LPUSH list 1 2 3 4; LRANGE list 0 -1; -> 取出所有.(从第一个到最后一个)
		
	#String
		set/get/del/append/strlen	//增 查 删 追加 长度
		
		incr/decr/incrby/decrby		//对数字进行加减(incr默认加1)
			///set num 10; incr num; incrby num 2; -> 先加1,再加2;结果13
	
		getrange/setrange			//获取&设定 指定区间范围内的值,类似 between...and
			///set hello world; getrange hello 1 2; -> "or"
			///setrange hello 1 xx; -> "wxxld"
			///setrange hello 7 xx; -> "wxxld\x00\x00XX" (7大于wxxld总长度,则补0)
			
		setex key 10 value		//设定值并附加过期时间
		setnx hello value		//如果key不存在,可以设定值.反之不可以设定
		
		mset/mget/msetnx		//一次设定多个,获取多个,不存在的前提下设定多个
		getset					//先取值,再设值. (返回旧值)
			///MSET k1 v1 k2 v2 k3 v3
			///MGET k1 k2 k3
			///MSETNX k1 v1 k4 v4 -> 不成功,因为k1
			///GETSET k1 value1 -> 返回v1
			
2.持久化
	#RDB(Redis DataBae)
	#AOF(Append Only File)
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
0.redis.conf
	1m => 1000*1000 bytes; 1mb => 1024*1024 bytes	//大小写不敏感,并且1m和1mb有区别

	#GENERAL
		daemonize no //redis是否以守护进程方式运行???
		pidfile /var/run/redis.pid //如果配置redis为守护进程,默认会把pid写入 /var/run/redis.pid
		port 6379 //redis端口
	
		tcp-backlog 511 //设置tcp的backlog
			//backlog是一个连接队列, backlog队列总和=未完成三次握手队列+已完成三次握手队列. 
			//高并发环境下,需要一个高backlog值来避免慢客户端连接问题.
			//注意: linux内核会将此值减小到 /proc/sys/net/core/somaxconn, 
			//		即需要同时增大 somaxconn 和 tcp_max_syn_backlog 两个值来达到效果
	
		bind 127.0.0.1 //绑定的主机地址
		timeout 300 //当客户端闲置多长时间后关闭连接, 0表示永不关闭.
		tcp-keepalive 0 //检测客户端网络中断时间间隔(秒), 0表示不检测. 建议设置为60
		
		loglevel verbose //指定日志记录级别. redis支持: debug; verbose; notice; warning
		logfile stdout //日志记录方式,默认标准输出. 
			//如果配置redis为守护进程,而这里又配置日志记录方式为标准输出,则日志将会发送给/dev/null

		databases 16 //设置数据库数量, 默认值为16.
	
11、指定在多长时间内，有多少次更新操作，就将数据同步到数据文件，可以多个条件配合：
	save <seconds><changes>
	save 300 10：表示300秒内有10个更改就将数据同步到数据文件
12、指定存储至本地数据库时是否压缩数据，默认为yes，redis采用LZF压缩，如果为了节省CPU时间，可以关闭该选项，
	但会导致数据库文件变得巨大：
	rdbcompression yes
13、指定本地数据库文件名，默认值为dump.rdb：
   dbfilename dump.rdb
14、指定本地数据库存放目录：
   dir ./
15、设置当本机为slave服务时，设置master服务的IP地址及端口，在redis启动时，它会自动从master进行数据同步：
   slaveof <masterip><masterport>
16、当master服务设置了密码保护时，slave服务连接master的密码：
   masterauth <master-password>
17、设置redis连接密码，如果配置了连接密码，客户端在连接redis时需要通过auth <password>命令提供密码，默认
   关闭：
   requirepass foobared
18、设置同一时间最大客户端连接数，默认无限制，redis可以同时打开的客户端连接数为redis进程可以打开的最大文件
   描述符数，如果设置maxclients 0，表示不作限制。当客 户端连接数到达限制时，redis会关闭新的连接并向客户端返
   回 max number of clients reached错误消息：
   maxclients 128
19、指定redis最大内存限制，redis在启动时会把数据加载到内存中，达到最大内存后，redis会先尝试清除已到期或即将
   到期的key，当次方法处理后，仍然到达最大内存设置，将无法再进行写入操作，但仍然可以进行读取操作。Redis新的
   vm机制， 会把key存放内存，value会存放在swap区：
   maxmemory <bytes>
   
		maxmemory-policy volatile-lru	//移除过期缓存策略
			// volatile-lru -> lru算法, 只对设置了过期时间的key
			// allkeys-lru -> lru算法
			// volatile-random -> 随机移除, 只对设置了过期时间的key
			// allkeys-random -> 随机移除
			// volatile-ttl -> 移除ttl最小的key,即将要过期的key
			// noeviction -> 永不过期,写满了就报错
			
		maxmemory-samples 5	//设置样本数量. lru和最小ttl都不是精确算法,设置一个采样值,在采样值范围内,使用lru和ttl.
			#Redis中的lru不是严格意义上的lru算法实现,是一种近似的lru实现,主要是为了节约内存占用以及提升性能.

21、指定是否在每次更新操作后进行日志记录，redis在默认情况下是异步的把数据写入磁盘，如果不开启，可能会在断电
   时导致一段时间内数据丢失。因为redis本身同步数据文件是按上面save条件来同步的，所以有的数据会在一段时间内置
   存在于内存中。默认为no：
   appendonly no
22、指定更新日志文件名，默认为appendonly.aof：
   appendfilename appendonly.aof
23、指定更新日志条件，共有3个可选值：
   no：表示等操作系统进行数据缓存同步到磁盘（快）；
   always：表示每次更新操作后手动调用fsync()将数据写到磁盘（慢，安全）；
   everysec：表示每秒同步一次（折中，默认值）
   appendfsync everysec
24、指定是否启用虚拟内存机制，默认值为no，简单介绍一下，VM机制将数据分页存放，由redis将访问量较小的页即
   冷数据 swap到磁盘上，访问多的页面由磁盘自动换出到内存中：
   vm-enabled no
25、虚拟内存文件路径，默认值为/tmp/redis.swap，不可多个redis实例共享：
   vm-swap-file /tmp/redis.swap
26、将所有大于vm-max-memory的数据存入虚拟内存，无论vm-max-memory设置多小，所有索引数据都是内存存储
   的（redis的索引数据就是keys），也就是说，当vm-max-memory设置为0的时候，其实是所有value都存在于磁盘。
   默认值为 0：
   vm-max-memory 0
27、redis swap文件分成了很多的page，一个对象可以保存在多个page上面，但一个page上不能被多个对象共享，
   vm-page-size是根据存储的数据大小来设定的，作者建议如果储存很多小对象，page大小最好设置为32或者64bytes；
   如果存储很多大对象，则可以使用更大的page，如果不确定，就使用默认值：
   vm-page-size 32
28、设置swap文件中page数量，由于页表（一种表示页面空闲或使用的bitmap）是放在内存中的，在磁盘上每8个
   pages将消耗1byte的内存：
   vm-pages 134217728
29、设置访问swap文件的线程数，最好不要超过机器的核数，如果设置为0，那么所有对swap文件的操作都是串行的，
   可能会造成长时间的延迟。默认值为4：
   vm-max-threads 4
30、设置在客户端应答时，是否把较小的包含并为一个包发送，默认为开启：
   glueoutputbuf yes
31、指定在超过一定数量或者最大的元素超过某一临界值时，采用一种特殊的哈希算法：
   hash-max-zipmap-entries 64
   hash-max-zipmap-value 512
32、指定是否激活重置hash，默认开启：
   activerehashing yes
33、指定包含其他配置文件，可以在同一主机上多个redis实例之间使用同一份配置文件，而同时各个实例又拥有自己的
   特定配置文件：
   include /path/to/local.conf	
	
	
	