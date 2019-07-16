#!/bin/bash

//{--------<<<基础配置>>>--------------------------------
#putty配置
    字体大小: Windows -> Appearance -> Font Settings -> Consolas(12)
    绿色字体: Windows -> Color -> Default Foregroud -> 0 255 0
    保存配置: Session -> Saved Sessions -> 选中默认 -> Save -> Apply

#shell命令注意点: 
    (1).区分大小写; cd和CD表示不同指令. 
    (2).指令太长,可使用进行换行处理 \

#输入(输出)重定向
    #command > file        //将输出重定向到file
    #command >> file    //将输出 [追加] 到file
    #command < file        //将输入重定向到file


//}

//{--------<<<单一指令>>>--------------------------------X
 // kill -9 `lsof -t -i:8090`
    // #kill -9 `lsof -i:8090 | awk '{print $2}' | sed -n '2p'`        //等同于上面命令
    // #nohup java -jar blue.jar > ./logs/blue.log >/dev/null 2>&1 &    //不再输出日志
    // nohup java -jar blue.jar > ./logs/blue.log 2>&1 &                //输出日志

    // 1). `` -> 引用其命令的执行结果
    // 2). awk '{print $2}' -> 输出第二列内容
    // 3). sed -n '2p' -> 输出第二行
    // 4). nohup command & -> &为后台执行的意思; nohup是不挂起的意思(no hang up),它保证在退出帐户之后继续运行相应的进程.
    // 5). /dev/null 文件 -> 看作'黑洞'
        // #非常等价于一个只写文件. 所有写入它的内容都会永远丢失. 而尝试从它那儿读取内容则什么也读不到. 
        // #但 /dev/null 文件非常有用,将命令的输出重定向到它,会起到"禁止输出"的效果。

        // #如果希望屏蔽 stdout 和 stderr,可以这样写:
        // command > /dev/null 2>&1
        
        // #一般情况下,每个 Unix/Linux 命令运行时都会打开三个文件:
        // a).标准输入文件(stdin): stdin的文件描述符为0, Unix程序默认从stdin读取数据。
        // b).标准输出文件(stdout): stdout 的文件描述符为1, Unix程序默认向stdout输出数据。
        // c).标准错误文件(stderr): stderr的文件描述符为2, Unix程序会向stderr流中写入错误信息。
        // # 2>&1 -> 表示stderr的输出方式同stdout, 都是禁止输出
    
// #lsof: list open files -> 列出当前系统打开的文件. 安装 -> yum install lsof
    // #默认: 没有选项, 列出所有活跃进程打开的所有文件
    // #-u uName: 显示所属user进程打开的文件
    // #-n: 不将IP转换为hostname,缺省是不加上-n参数
    // #-a: 所有参数都满足时才显示结果
    // #-c string: 显示 COMMAND 列中包含指定字符的进程所有打开的文件
    // #-t: 仅获取进程ID
    
        // lsof -t -c java -i:8080        //过滤条件 -c -i 默认是 或 的关系
        // lsof (-a) -t -c java -i:8080    //-a 代表 且 的关系
    
    // #fileName: 显示指定'文件'相关的所有进程
    // #+d /DIR/: ........'目录'..............
    // #+D /DIR/: ........'目录及子目录'...... (递归显示)
    
        // lsof blue/logs/info        
        // lsof +d blue/logs        
        // lsof +D blue/logs        //迭代显示,当前目录及其子目录的打开文档信息
    
    // #-p<进程号>: 列出指定进程号所打开的文件
        // lsof -p PID1,,PID2 |wc -l    //显示进程号(可以多个)打开文件的总数, (wc -l 文件行数)
    
    
    // #lsof -i[46] [protocol][@hostname|hostaddr][:service|port]
    // #    46 --> IPv4 or IPv6
    // #    protocol --> TCP or UDP
    // #    hostname --> Internet host name
    // #    hostaddr --> IPv4地址
    // #    service --> /etc/service中的 service name (可以不只一个)
    // #    port --> 端口号 (可以不只一个)
    
        // lsof -i:8090        //端口8090相关的网络信息
        // lsof -t -i:8090        //仅显示端口8090对应的进程PID
        
        // lsof -i6:8090        //端口8090的IPV6进程
        // lsof -i tcp            //仅显示tcp连接(udp同理)
        // lsof -i@host        //显示基于指定主机的连接
        // lsof -i@host:port    //显示基于主机与端口的连接

    
    
// #tail: 默认显示指定文件的末尾10行; 如给定多个文件,则在显示时,每个文件前加一个文件名标题. 如未指定文件或文件名为'-',则读取标准输入
    // #-c<N>或——bytes=<N>: 文件尾部的N个字节内容
    // #-n<N>或——line=<N>: 文件尾部的 N 行内容.
    // #-f: 动态显示文件最新的追加内容 (适合查看日志)
    // #-s<秒数>或——sleep-interal=<秒数>: 与'-f'选项连用,指定监视文件变化的间隔秒数
    // #--pid=<进程号>:与'-f'选项连用,当指定的进程号的进程终止后,自动退出tail命令

    // tail -n 5 file    //最后5行内容
    // tail -n +5 file    //第5行至末尾(包含第5行)    
    
    // tail -f -n 3 file    //循环查看文件的最后三行内容
    
    // tail -c 10 file    //最后10个字符    
    


    
    
// #du: 显示指定的目录或文件所占用的磁盘空间
    // #--max-depth=<目录层数>: 超过指定层数的目录后,予以忽略

    // # 相结合使用,查看磁盘情况
    // df -lh
    // du -h --max-depth=1 /var/lib/webpark/logs | sort -nr
    
// #echo: ""会将内容转义; ''不会转义,原样输出
    // echo "$(date)"            //输出当前时间
    // echo '$(date)'            //输出 $(date)
    
    // echo $(date '+%Y-%m-%d %H:%M:%S')    //输出时间格式化

    // echo a b c | awk '{print $1,$3}'    //查看一行的第一和第三列
    
    // //执行结果两种表达方式: `date`  $(date) ,后者适用于嵌套情况
    // echo $(lsof -p $(lsof -t +D /var/lib/webpark/logs/device) |wc -l)
    
    // //,如: $(lsof -p $(lsof -t -i:8080|sed -n '1p') |wc -l)
    // echo $(date) '---' $(lsof -p $(lsof -t -i:8080|sed -n '1p') |wc -l) >> /file
    
// #split: 切割文件
    // #-d: 使用数字作为后缀
    // #-b: 指定每多少字节切成一个小文件
    // #-l <行数> / -<行数>: 指定每多少行切成一个小文件
    
    // split [-6/-l 6] a.log        #将a.log每6行分割成一个文件
    // split -b 10m park.log        #每个小文件大小为10MB
    // split -b 10k sm.log sm -d    #小文件10kb/个,前缀sm (默认是x), 后缀从00开始的数字
    
    
// #grep: 根据 [文件内容] 进行查找
    // #-n: 显示匹配行及行号
    // #-w: 只匹配整个单词,而不是字符串的一部分(如匹配'magic',而不匹配'magical')
    // #-r: 迭代查找,包含子目录
    // #-d: 对象为目录时,使用此命令 grep -d skip
    // #-i: 不区分大小写
    // #-h: 查询多文件时不显示文件名
    // #-l: 查询多文件时只输出包含匹配字符的文件名
    // #-a: 不要忽略二进制数据。
    // #-A <行数n>: 除显示符合范式那一列之外,并显示该行之[后]的n行内容
    // #-B <行数n>: 除显示符合范式那一行之外,并显示该行之[前]的n行内容
    
    // grep 'test' d* --color=auto            //显示所有以d开头的文件中 包含test 的行,并标记颜色
    // grep -w 'test' aa bb cc                //显示在aa,bb,cc文件中 单词test 的行
    // grep -win 'error' *.log                //显示当前目录下 以.log结尾, 包含单词error, 所以行及行号
    // grep 'magic' /usr/src                //显示/usr/src目录下的文件(不含子目录)包含magic的行
    // grep -r 'magic' /usr/src            //显示/usr/src目录下的文件(包含子目录)包含magic的行
    
    // grep -d skip -n '代扣' * > file        //忽略子目录
    // grep -r ...                            //迭代搜索子目录
    
    // #使用正则表达式 -E 选项
        // # :    忽略正则表达式中特殊字符的原有含义
        // # ^:    匹配正则表达式的开始行
        // # $:    匹配正则表达式的结束行
        // # <:    从匹配正则表达式的行开始
        // # >:    到匹配正则表达式的行结束
        // # []:    单个字符,如[A]即A符合要求 
        // # .:    所有的单个字符
        // # *:    有字符,长度可以为0
        // # [-]:    范围,如[A-Z],即A到Z都符合要求
        
    // grep -inE '*c29a2' *.log > file        #功能==后者
    // egrep -in '*c29a2' *.log > file        #过滤出日志中包含"c29a2"字符串的行,并导出到file文件

    
// #ls: 显示指定目录下的内容(文件及子目录)
    // #-a: 显示所有,包含.开头的隐藏文件        -l: 显示文件的详细信息    -h: 以MB,GB等形式显示文件大小
    // #-t: 以最后修改时间降序排列(先新后旧)    -F: 在文件名称后加一符号(如,可执行文档加'*',目录则加'/')
    // #-r: 以相反次序显示(原以英文字母次序)    -R: 迭代所有文件(包含子目录下文件)
    
    // ls -lhtr s*        //列出目录下所有名称 s 开头的文件,以最后修改时间升序排列.(r代表排序取反)

// #wget: 从指定的URL下载文件
    // #-o: 下载信息写入日志,而非显示控制台
    // #-O: 下载并以不同的文件名保存.(默认以最后一个/后面的字符来命名,动态链接文件名会不正确)
    // #-c: 继续执行上次终端的任务.(断点续传)
    // #-b: 进行后台的方式运行wget
    // #-i<文件>: 从指定文件获取要下载的URL地址
    
    // wget -o download.log URL 
    // wget -O wordpress.zip http://www.linuxde.net/download.aspx?id=1080
    // wget -c http://www.linuxde.net/testfile.zip
    
    // wget -b http://www.linuxde.net/testfile.zip
        // //Continuing in background, pid 1840.
        // //Output will be written to `wget-log'.
    // tail -f wget-log //后台下载,使用以下命令来察看下载进度
    
    // wget -i filelist.txt //先将各url写入文件.(一行一个??)
    
// #ulimit: 限制系统用户对shell资源的访问
    // #-a: 显示当前所有的资源限制
    // #-n <size>: 指定(显示)同一时间最多打开文件数
    // #-u <size>: 指定(显示)用户最多可开启的程序数目
    
    // sudo nano /etc/security/limits.conf //最大句柄数(永久生效)
    // //追加: *                -       nofile          10240

// #uniq 统计重复行,一般与 sort 结合使用
    // #-u: 仅显示出一次的行列
    // #-c: 在行首显示该行重复出现的次数

    // //当重复的行并不相邻时, uniq 命令不起作用. 此时必须配合 sort 使用.
    // sort file | uniq -c | sort -r
    
// #sort 将文件内容进行排序
    // #-n: 正序排序(按数值大小,默认正序)
    // #-r: 反序排列
    // #-u: 忽略相同行        
    // #-t<分隔符>: 指定排序时所用的栏位分隔字符    +<起始栏位>-<结束栏位>: 以指定的栏位来排序,范围由起始栏位到结束栏位的前一栏位
    
    // sort (-r) file    //正序(倒序)排列
    // sort -t $'\t' -k 2.7 file    //以 TAB 分割为列,对第2列的第7个字符进行排序
    
// #cut 用于显示每行从开头算起 num1 到 num2 的文字
    // #-b: 以字节为单位进行分割. 这些字节位置将忽略多字节字符边界,除非也指定了 -n 标志。
    // #-c: 以字符为单位进行分割.
    // #-d: 自定义分隔符,默认为制表符.
    
    // who|cut -b 3    //提取每一行的第3个字节
    // who|cut -b 3-    //.....................到行尾

//}

//{--------<<<常用指令>>>--------------------------------
'常用命令': http://www.runoob.com/w3cnote/linux-useful-command.html

// #touch 新建空文件,或修改文件的时间属性.(ls -l 查看文件的时间属性)
    // touch file        //新建空文件file
    

    
// #清空文件
    // (1).  > file                //使用重定向方法
    // (2). true > file            //使用true命令重定向清空文件
    // (3). echo -n "" > file    //要加上"-n"参数,默认情况下会有"\n",即有个空行
    
    

    
#软件相关
    apt-get update                //更新安装列表

    dpkg -l | grep x            //从已安装软件中确定是否安装了软件x 
    apt-get --purge remove x    //删除软件及配置
    apt-get autoremove x        //卸载软件及其依赖的安装包
    
    wget url //下载指定链接
    
#系统相关
    lsb_release -a //系统相关信息
    
#防火墙
    sudo apt-get install ufw            //安装ufw
    sudo ufw status/enable/disable        //查看/开启/关闭 (active / inactive)
    sudo ufw default deny        //开启防火墙,并随系统启动同时关闭所有外部对本机的访问. (本机访问外部正常)
    
    sudo ufw allow 80            //允许外部访问80端口
    sudo ufw delete allow 80    //禁止外部访问80 端口
    
    sudo ufw allow from 192.168.1.1    //允许此IP访问所有的本机端口
    

    
#free -> 当前内存的使用.(-m: 以MB为单位)
        parkmanager@bc-ai-server:/var/lib/webpark/logs/sm/task$ free -h
                     total       used       free     shared    buffers     cached
        Mem:          7.7G       7.3G       450M       176M       251M       3.7G
        -/+ buffers/cache:       3.3G       4.4G
        Swap:         7.9G       148M       7.7G

//}

        

//{--------<<<shell>>>-----------------------------------
#算术运算符
    #原生bash不支持简单的数学运算,可通过其他命令来实现. 如: expr.
        a=10; b=20;
        
        val=`expr $a + $b` 
        val=`expr $a \* $b`    //转义符'\'
        if [ $a == $b ]        //运算符 == 前后都有空格; 且 [ 之后也有空格.

#关系运算符
    #关系运算符只支持数字,不支持字符串; 除非字符串的值是数字,如ASCII表.
    #常用: gt(>); lt(<); eq(==); ne(!=); ge(>=); le(<=); o(||); a(&&)
        a=10; b=20;
        
        if(a>0 && (b>0 || c>0))  
        <-->  if [ $b -gt 0 || $c -gt 0 -a && -gt 0 ]; then //等同
        <-->  if [ $b -gt 0 -o $c -gt 0 -a $a -gt 0 ]; then //等同

#字符串运算符
        a="abc"; b="efg";
     
        if [ $a = $b ]    //两个字符串是否相等
        if [ -z $a ]    //长度是否为0
        if [ -n "$a" ]    //长度是否不为0
        if [ $a ]        //检测是否不为空

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

//{--------<<<系统级别>>>--------------------------------

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

//{--------<<<awk命令>>>---------------------------------
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

//{--------<<<组合指令>>>--------------------------------
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
    (4).对于MAC地址,第5个字段就是MAC; 而对于IP地址,还需要对第2个字段截取第6个字符之后的内容}}


//}

//{--------<<<thread>>>----------------------------------X


#ps xH | wc -l //查看linux所有存在的线程数

#ps -mp <pid> | wc -l        //查看一个进程的线程数
#pstree -p <pid> | wc -l    //同上

#cat /proc/${pid}/status    //查看一个进程的所有相关信息

//}



//{--------<<<123>>>-------------------------------------X
// #查询PID
    // 1.jps -l ///列出所有java进程的 pid + 项目名
        // 16874 webpark.war 
        
    // 2.ps -ef | grep webpark
        // // C        --> CPU占用率            STIME    --> 开始时间
        // // TIME        --> 进程运行的总时间    CMD        --> 启动命令
        // UID            PID        PPID    C    STIME    TTY        TIME    CMD
        // parkman+    16874        1    11    11:01    ?    00:20:51    /usr/java/jdk1.8.0_191/bin/java -jar webpark.war
        
    // 3.netstat ///显示网络状态 -> 过滤端口
        // netstat -anp | grep 8080

    // 4.lsof ///
        // lsof -t -i:8080
        
        
// #Windows
    // netstat -aon | findstr 8080        //根据端口查找pid
    // taskkill -f /pid 9984            //强制杀死pid 9984
    
    // tasklist | findstr 10876        //根据pid查找进程名
    
// #kill 杀死某个进程
    // kill -9 16874 //杀死进程
    
    

//}

//{--------<<<ABC>>>-------------------------------------X
#常用参数
    -l    //除文件名称外,亦将文件型态,权限,拥有者,文件大小等资讯详细列出.
    -h    //以方便阅读方式显示,如以MB为单位显示存储
    
    -f    //强制操作,勿需确认
    -i    //操作前,逐一询问确认
    
    -r    //递归操作目录(recursion)

// #ls 用于显示指定工作目录下的内容
    // [root@linux]# ls -lh
    // total 4.9G //总计大小
    // drwxrwxrwx 3 parkmanager root 4.0K  1月 25 17:12 dir
    // |    1   | 2  |    3    |   4  | 5  |      6     |   7  |
    // |档案属性|连接|  拥有者 | 分组 |大小|  修改日期  |文件名|
    
// #mkdir 创建目录
    // mkdir -p ./test/test01/test02 //-p: 创建多层目录

// #rm 删除文件或目录
    // rm -rf /var/log            //递归+强制-删除

// #cp 复制文件或目录    
    // cp –rf ./test ./newtest    //批量复制

// #mv    移动,或重命名
    // mv /a.txt /b.txt        //两目录一致, 指定新文件名 --> 重命名
    // mv /a.txt /test/c.txt     //.....不..., 指定新文件名 --> 移动+重命名
    
    // mv /a.txt /test/        //.....不..., [没]指定新文件名 --> 移动
    
    // mv /student/_* .         //批量移动
    
// #zip 压缩和解压(unzip)
    // #-q: 不显示压缩过程信息
    // #-l: 在不解压的前提下,显示压缩文件内所包含的文件
    
    // zip -qr err.zip ./err*    //将当前目录下所有以'err'开头的文件压缩到'err.zip'
    // zip -qr sm.zip /logs/sm    //将目录 /logs/sm 下所有文件夹和文件压缩到'sm.zip'
        
    // zip -d sm.zip a.log        //从压缩文件中删除文件a.log    
    
    // unzip sm.zip            //解压到当前目录
    // unzip err.zip -d ./err    //......指定目录    
    
    // unzip -l sm.zip            //查看压缩包中的文件信息(不解压)
    
    // j=0; for i in ./_*; do unzip $i -d $j; let j++; done //批量解压
    
// #tar 打包压缩, 用于备份文件!!!(相比zip更优)
    // #-z: 通过gzip指令处理备份文件    ->    gzip
    // #-v: 显示指令执行过程            ->    verbose
    // #-f: 指定备份文件                ->    file
    // #-t: 列出备份文件的内容            ->    list
    // #-r: 新增文件到已备份文件的尾部    ->    append
    // #-g: 增量备份
    
    // #-c: (压缩)建立新的备份文件        ->    create
    // #-x: (解压)从备份文件中还原文件    ->    extrac

    // tar -zcvf test.tar.gz test        //压缩文件test
    
    // tar -zxvf test.tar.gz            //解压到'当前'目录
    // tar -zxvf test.tar.gz -C test/    //......'指定'....
    
    // tar -ztvf test.tar.gz            //列出归档文件的内容
    
    // echo -n "123" > test    //-n表示不换行,即结尾没有换行符
    // tar -g snapshot -zcvf test0.tar.gz test    //第1次归档(123)
    // echo "456" >> test        //追加test末尾
    // tar -g snapshot -zcvf test1.tar.gz test    //第2次归档(123456)
    // tar -g snapshot -zcvf test2.tar.gz test    //第3次归档(空的,因为没有修改)
    
// #find 根据 [文件属性] 进行 [递归] 查找
    // #-name: 根据文件名查找.(-iname: 忽略大小写)
    // #-size: 文件大小    
    // #-user: 所属用户
    // #-empty: 空文件

    // find . -name 'sm*'        //(递归)-当前目录下名为 'sm' 或 'sm*' 的文件及文件夹
    // find . -empty 　　        //(递归)-.......... '空' 文件或者文件夹
    // find . -size +10M        //(递归)-.......... 大于10MB的文件(c:字节, w:双字, k:KB, M:MB, G:GB) (+:大于,-:小于)

    // ///混合查找 ---> !(非); -and(且); -or(或)
    // find /tmp -size +10000c -and -mtime +2    //在/tmp目录下查找大于10000字节并在最后2分钟内修改的文件
    // find / -user fred -or -user george        //在/目录下查找用户是fred或者george的文件文件
    // find /tmp ! -user panda                    //在/tmp目录中查找所有不属于panda用户的文件

        
//}

//{--------<<<XYZ>>>-------------------------------------X
// #ln 为某一个文件在另外一个位置建立一个同步的链接
    // #-f: 强制执行
    // #-s: 软链接(符号链接)

    // //硬 & 软链接  ---> 无论软还是硬,文件都保持同步变化
    // ln src dest    ---> 在选定的位置上生成一个和源文件大小相同的文件
    
    // ln –s src dest ---> 只在指定的位置上生成一个文件的镜像,不会占用磁盘空间,类似'快捷方式'
    
// #df 显示系统的磁盘使用情况
    // #-l: 列出文件结构
    // #-h: 以人类可读的格式显示大小
    
    // df -lh                //当前linux系统所有目录的磁盘使用情况
    // df -lh --total        //增加统计信息

    // //查看指定目录所属挂载点,及挂载点的磁盘使用情况
    // df -lh /var/lib/webpark/logs/sm

// #date 详见SHELL-格式化
    // date '+%Y-%m-%d %H:%M:%S'    //格式化输出当前时间
    
// #alias 临时简化命令,重新打开CMD则不起作用
    // alias datef='date "+%Y-%m-%d %H:%M:%S"'    //''不可省, =号不可省



//}


//{--------<<<SHELL>>>-----------------------------------x
#0.格式
    文件第一行必须是 "#!/bin/sh"; 注释符号为 '#'.

#1.变量
    不需要声明; 只能由字母,数字,下划线组成,不能以数字开头
    echo $JAVA_HOME //输出变量的值

#2.转义符
    单引号不解析变量            //echo '$JAVA_HOME' 输出"$JAVA_HOME"
    
    双引号会解析变量            //echo "$JAVA_HOME" 输出"/usr/java"
    飘号为执行内容,类似于$(...)    //echo `$JAVA_HOME` 输出"/usr/java"
    
#3.输入参数
    执行脚本时,传入的参数按照先后顺序使用 $1,$2 等顺序引用变量值($0 就是文件名)
    test.sh abc 123 //在 test.sh 中,可通过 $2 读取 123.

#4.格式化
    date '+%Y-%m-%d %H:%M:%S'    //格式化输出
    
    date '+%S'                     //提取当前时间的秒数
    date '+%s'                     //自 1970-01-01 00:00:00 以来的总秒数
    
    echo $(date '+%Y-%m-%d %H:%M:%S')    //shell中输出日期
    echo `date '+%Y-%m-%d %H:%M:%S'`    //同上
    
    date --date='3 days ago'    //3天以前
    date --date="3 days ago" "+%Y-%m-%d %H:%M:%S"    //3天以前,并格式化
    
    //shell中以日期命名文件
    FILE=$(date '+%Y%m%d-%H%M%S')
    sudo zip -qr slow-$FILE.zip slow.log >/dev/null 2>&1
    
#5.重定向
    '>>': 追加更新 '>': 覆盖更新

#6.if
    0.DEMO
        file="/var/lib/webpark/logs/sm/task/file/lsof"

        if [ -e $file ] //if 和 fi 是一对闭合体,少一个则报语法错误
        then
           echo "文件存在"
        else
           echo "文件不存在"
        fi
    
#7.fori
    1.行内风格
        for i in {1..10}; do echo $i; done
        
        for ((i=1; i<11; i++)); do echo $i; done
    
    2.脚本文件
        for((i=1; i<11; i++))
        do
           echo $i
        done
    
#8.foreach
    1.行内风格
        for file in /var/lib/webpark/logs/sm/err/_*; do echo FILE_PATH: $file; done
    
    2.脚本风格
        for file in /var/lib/webpark/logs/sm/err/_*;
        do
            echo FILE_PATH: $file
        done
        
#9.引用其他shell
    0.定义SHELL(func.sh)
        #!/bin/bash
        count=$1 //取值第一个参数

        echo $(date --date="$count days ago" "+%Y-%m-%d %H:%M:%S")
        
    1.引用SHELL(test.sh)
        #!/bin/bash
        source ./func.sh 3 //引用shell,并传参3
        
#自定义函数
    0.函数定义(func.sh)
        #!/bin/bash
        function daysAgo(){
            date --date="$1 days ago" "+%Y-%m-%d %H:%M:%S"
        }

        function daysAfter(){
            date --date="-$1 days ago" "+%Y-%m-%d %H:%M:%S"
        }
    
    1.调用函数(test.sh)
        #!/bin/bash
        source ./func.sh

        echo "daysAgo:" $(daysAgo 3)
        echo "daysAfter:" $(daysAfter 3) //调用函数 daysAfter()

//}


//{--------<<<CMD>>>-------------------------------------X
// #nohup command>/dev/null 2>&1 &
    // 1.nohup
        // 该命令可以在 '退出帐户/关闭终端' 之后继续运行相应的进程.
        // 默认情况,该作业的所有输出都被重定向到一个名为 'nohup.out' 的文件中
        
    // 2.&
        // 表示在后台运行.        
        // nohup COMMAND &    //使命令永久的在后台执行
        
    // 3.2>&1
        // 默认, 0表示标准输入, 1表示标准输出, 2表示标准错误.
        
        // //nohup command > myout.file 2>&1 &
        // 表示将标准错误(2)重定向到标准输出(&1),标准输出(&1)再被重定向输入到 'myout.file' 文件中
    
    // 4./dev/null
        // 表示空设备,这里就是把日志记录到空设备里,就是不记录日志.
        // /dev/null 2>&1    //将产生的所有信息丢弃
        
// #文档查阅
    // 1.cat -n file            //从第1行开始,显示全部(-n: 显示行号)
        
    // 2.tac file                //从倒1行开始,显示全部
        
    // 3.nl file                //从第1行开始,显示全部.自动显示行号
    
    // 4.more file                //1次显示1屏内容(空格: 下1屏内容)
        // more -5 file        //设置1屏只显示5行
        // more +100 file        //从100行开始显示
        
    // 5.less file                //从倒1行开始,1次显示1屏内容
    
    // 6.head -n 10 file        //只显示前10行
    
    // 7.tail -n 10 file        //只显示倒10行
    
    // 8.sed -n '[i],[j]p' ---> 输出第i到第j行内容. 其中,j<=i时,只输出第i行
        // sed -n '3,5p'        //显示第3到第5行的内容
        
        // cat -n file | head -n 5 | tail -n +3    //效果同上
        // cat -n file | tail -n +3 | head -n 3    //效果同上
        
// #文档检索
    // 0.先考虑 whereis 或 locate, 最后才考虑 find. ---> 前两者是利用数据库来搜寻数据,相当快速!!
    
    // 1.whereis java        //只用于查找二进制文件,源代码和man手册页; 一般文件的定位需使用locate命令
        
    // 2.which java        //在环境变量 $PATH 中查找符合条件的文件
        
    // 3.locate mysql.cnf    //查找mysql.cnf文件
    
    // 4.find . -name 'sm*'//(递归)-查找当前目录下名为 'sm' 或 'sm*' 的文件及文件夹

    
    

        

    
//}


//{--------<<<vi>>>--------------------------------------X
#vi使用
    // vi hello.sh        //使用Vi编辑器,新建并打开hello.sh文件
    
    // 默认初始为'命令模式',使用以下操作进入'编辑模式'    //使用Esc (进入或退出命令模式)
        // i - 光标[前]插入文本    I - 本行[开始]插入文本
        // a - 光标[后]插入文本    A - 本行[末尾]插入文本
        // o - 光标[下]插入新行    O - 光标[上]插入新行
                        
    // '命令模式'常用命令
        // x - 删除光标所在字符    15x - 删除光标所在的15个字符
        // d - 删除当前行            3dd - 删除光标所在的3行    //dG - 删除光标所在到末尾的所有行
        // D - 删除光标所在到行结尾    
        
        // '底行模式' :n1,n2d - 删除指定范围的行

        // gg - 到第一行            G - 到最后一行    //667G - 定位到667行, :667 - 也是同样定位到667行
        
        // yy/Y - 复制当前行        //nyy/nY - 复制当前及以下n行
        // dd - 剪切当前行            //ndd - 剪切当前及以下n行
        // p/P - 粘贴在当前光标所在行的上/下
        
        // r - 替换光标所在字符    R - 从光标所在处开始替换字符，按Esc结束
        // u - 取消上一步操作

    // 在'命令模式'下按':'进入'底行模式'
        // :set nu - 显示行号        :set nonu - 隐藏行号
        // :w - 保存修改            :w filename - 另存为新的文件名
        // :q - 退出                :q! - 强制退出
        // :wq - 保存修改并退出    //等同于ZZ
        // //对于只读文档修改时,提示"readonly", 使用 :wq! 强制保存退出(root有效).
        
        
28.Vim编辑器
i, I      i为『从光标所在处插入』，I 为『在光标所在行的第一个非空格符处开始插入』。 (常用)
a,A     a为『从光标所在的下一个字符开始插入』，A为『从光标所在行最后一个字符开始插入』。(常用)
o, O    o为『在光标所在的下一行插入新的一行』；O为在光标所在处的上一行插入新行！(常用)
r, R    取代：r会取代光标所在的那一个字符；R会一直取代光标所在的文字，直到按下ESC为止；(常用)

G         移动到这个档案的最后一行(常用)        gg    移动到这个档案的第一行！(常用)
n<Enter>  n为数字，光标向下移动n行(常用)    
0           数字0，移到行的第一字符处 (常用)    $      移动到这一行的最后面字符处(常用)

/word     向光标之下寻找一个名称为 word 的字符串。例如要在档案内搜寻 hello 这个字符串，就输入/hello即可！(常用)
:n1,n2s/word1/word2/g        n1与n2为数字。在第n1与n2行之间寻找 word1 这个字符串，并将该字符取代为word2！举例来说，在100到200行之间搜寻hello并取代为 HELLO则：『:100,200s/hello/HELLLO/g』。
:1,$s/word1/word2/g        从第一行到最后一行寻找 word1字符串，并将该字符串取代为word2！(常用)
:1,$s/word1/word2/gc        从第一行到最后一行寻找 word1字符串，并将该字符串取代为word2！且在取代前显示提示字符给使用者确认 (conform) 是否需要取代！(常用)

    x,X        在一行字当中，x为向后删除一个字符(相当于[del]按键)，X为向前删除一个字符(相当于[backspace]亦即是退格键)(常用)
u        撤销前一个动作。(常用)
[Ctrl]+r      重做上一个动作。(常用)
.            不要怀疑！这就是小数点！意思是重复前一个动作的意思。如果您想要重复删除、重复贴上等等动作，按下小数点『.』就好了！(常用)
dd          删除游标所在的那一整行(常用)
ndd        n为数字。删除光标所在的向下n列，例如 20dd 则是删除20列(常用)
yy/Y        复制游标所在的那一行(常用)
nyy        n为数字。复制光标所在的向下n列，例如 20yy则是复制20行(常用)
vi复制一个单词的方法：光标移到想要被复制词的词首，输入yw，光标移到想到粘贴的位置，输入p

p, P        p为将已复制的数据在光标下一行贴上，P则为贴在游标上一行！举例来说，目前光标在第 20 行，且已经复制了 10 行数据。则按下 p 后， 那 10 行数据会贴在原本的 20 行之后，亦即由 21行开始贴。但如果是按下 P 呢？那么原本的第 20 行会被推到变成 30 行。(常用)

:w          保存当前文档(常用)
:w!        若档案属性为『只读』时，强制写入该档案。不过，到底能不能写入， 还是跟您对该档案的档案权限有关啊！
:q          离开vi (常用)
:q!         强制离开不保存档案
:wq        保存并离开，若为:wq!则为强制保存并离开 (常用)
ZZ        若档案没有更动，则不储存离开，若档案已经经过更动，则储存后离开！
:e!          将档案还原到最原始的状态！

:! command    暂时离开vi到指令模式，执行command的显示结果！例如『:! ls /home』即可在vi当中察看/home 底下以 ls 输出的档案信息！
:set nu          显示行号，设定之后，会在每一行的前缀显示该行的行号
:set nonu      与 set nu 相反，为取消行号！

//}


//{--------<<<Crontab>>>---------------------------------X
#nano编辑器
    yum -y install nano        //安装
    nano 路径+文件名        //新建/打开
    退出: Ctrl+x (y确认)    保存修改: Ctrl+o    取消返回: Ctrl+c
    剪贴/删除整行: Ctrl+k    复制整行: Alt+6        粘贴: Ctrl+U 
    
#crontab    
    rpm -qa | grep crontab //检查是否安装(rpm: Red-Hat Package Manager)
    
    sudo /etc/init.d/cron start(stop/restart)    //命令形式
    sudo service cron start(stop/restart)        //服务形式

    crontab -l(e/r)            //列出(编辑/删除)当前用户的定时任务

    日志位置: /var/log/cron.log; //对于ubuntu: http://www.cnblogs.com/nieqibest/p/9353927.html
    
    1.DEMO
        //分 时 日 月 周 (user可省) cmd
        m h dom mon dow (user)  command
        * * * * * cmd            // 每分钟执行一次任务  
        0 * * * * cmd            // 每小时的0点执行一次任务, 如6:00; 10:00
        6,10 * 2 * * cmd        // 每个月2号, 每小时的6分和10分执行一次任务
        */3,*/5 * * * * cmd        // 每隔3分钟或5分钟执行一次任务, 比如10:03, 10:05, 10:06 
        0 23-7/2,8 * * * cmd    // 晚上11点到早上8点之间每2个小时和早上8点 
        20 3 * * * (xxx; yyy)    // 每天早晨3点20分执行用户目录下的两个指令(每个指令以;分隔)
        0 11 4 * mon-wed /etc/init.d/smb restart    // 每月的4号与每周1到周3的11点重启smb 

    2.关键字
        //"%"是特殊字符(换行),所以命令中必须对其进行转义(\%).
        */2 * * * * echo $(date '+\%Y-\%m-\%d \%H:\%M:\%S')  >> file 

    3.shell脚本
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


//{--------<<<System>>>----------------------------------X
#系统目录
    1./etc
        存放系统的配置文件. 包括账号与密码(/etc/passwd, /etc/shadow),开机时各项设定值(/etc/sysconfig/_*)...
    
    2./usr/local
        将自己开发或自行额外安装的软件放置在 /usr/local 或 /opt.
    
    3./var
        存放系统运作过程中的中间暂存数据(/var/lib, /var/log, /var/run), 以及部分最终数据,如邮件(/var/spool/mail).
        另外,几乎所有服务的登录文件(可以记录谁,什么时候,由哪里登入主机,做了什么事等等信息!) 都放在 /var/log 这个目录下.
        
    4./tmp
        存放临时文件
        
#磁盘分配
    /        -->    根目录可以分配 1GB
    /boot    -->    大概在 50MB 就可以了,因为开机档案并不大
    /var    -->    至少需要 1GB 以上,因为 mail,proxy 预设的储存区都在这个目录中,除非要将一些设定改变
    /usr    -->    因为所安装的软件和数据都是在 /usr/ 当中,所以 /usr 大概 10G 左右
    /home    --> 用户数据放置在 /home 当中,因此通常建议你将所剩下的磁盘空间分配给这个目录

#启动级别(7)
    0        --> 关机,机器关闭. 即开机之后马上又关机 
    1        --> 单用户模式. 就像Win9x下的安全模式类似
    2        --> 多用户模式,但是没有NFS支持
    3        --> 完整的多用户模式. 标准的运行级. //默认使用
    4        --> 一般不用,特殊情况下使用. 例如在笔记本电脑的电池用尽时,可以切换到这个模式来做一些设置 
    5        --> 就是X11,进到X Window图形界面 //默认使用
    6        --> 重启. 即开机之后马上又重启
    ***        --> 使用命令 runlevel 查看当前的运行级别
    
//查询某程序的线程或进程数
pstree -p <pid> | wc -l

//查询当前整个系统已用的线程或进程数
pstree -p | wc -l

//}





