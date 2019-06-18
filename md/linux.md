[TOC]



# shell基础

## 系统相关

> 命令格式

linux区分大小写；命令过长，则使用 \ 进行换行。

> 关机重启（reboot）

```shell
shutdown -r now    #立即重启，等同于 reboot

shutdown -h now    #立即关机
shutdown -h +10    #10分钟后关机
shutdown -h 11:33  #定时关机，今天的11::33
```

> file属性

```shell
[root@linux]# ls -lh

total 4.9G #总计大小
drwxr-xr-x 8 parkmanager root 4.0K  9月 16  2014 nginx-1.6.2
-rw-r--r-- 1 parkmanager root 786K  8月 13  2018 nginx-1.6.2.tar.gz
```

| drwxr-xr-x |  8   | parkmanager | root | 4.0K | 9月 16  2014 | nginx-1.6.2 |
| :--------: | :--: | :---------: | :--: | :--: | :----------: | :---------: |
|  档案属性  | 连接 |   拥有者    | 分组 | 大小 |   修改日期   |   文件名    |

> 用户&分组

```shell
more /etc/passwd      #所有用户名信息
more /etc/group       #用户组信息

useradd -g group user #新增用户，分组
useradd user          #不指定分组，则分组名和用户名相同

groupadd police       #新增分组 police

userdel -r user       #若不加参数 -r，则仅删除用户帐号，而不删除相关文件

passwd user           #修改账户密码

chgrp [-R] user dir       #更改'档案'所属分组
chown [-R] user:root dir  #............拥有者，分组
```

> 档案属性 `d rwx r-x r-x`

```shell
#(0).是否是目录。d表示目录，-表示文件。

#(1).文件拥有者 所具有的读写权限。 u
#(2).文件拥有者同组用户........  g
#(3).其他用户.................  o

chmod [-R] 777 dir  #更改档案属性

chmod u-x file      #移除file所属用户（u）的执行权限
chmod a-x file      #.......所有用户（a = u+g+o）的执行权限
```
> rwx（读，写，执行）

```shell
x: #与能否进入该目录有关
r: #当目录有读取（r）权限时，就可以利用 ls 这个指令展示该目录的列表

w: #当目录有写入（w）权限时，将具有移动该目录结构清单的权限:
   (1). 建立新的档案与目录
   (2). 删除已经存在的档案与目录（不论该档案是属于谁）
   (3). 将已存在的档案或目录进行更名
   (4). 搬移该目录内的档案，目录位置
```

> RPM软件包管理器（RPM Package Manager）用于联网下载安装包

```shell
rpm –qa | more          #查询系统所安装的所有rpm软件包，并以分页的形式显示

rpm –qa | grep mysql    #查询系统所安装的所有rpm软件包，并返回和mysql相关的RPM包
rpm –q mysql            #查询软件是否被安装，是则返回该软件相关的版本信息，否则返回 is not installed.

rpm –i RPM包的全路径      #安装RPM包
rpm –ivh RPM包的全路径    #安装并有进度条等提示（i=install安装 v=verbose提示 h=hash进度条）

rpm –e RPM包名           #删除相关的rpm包(若果有依赖则报错，一般使用下者)
rpm –e RPM包名 –nodeps   #解除依赖，强制删除
```
> YUM包管理：相比rpm的好处是 自动解决软件包依赖关系 + 方便的软件包升级

```shell
2.安装 yum install sudo          #指定软件名，系统会自动的从网上获取安装镜像，并自动解决依赖关系

3.检测升级 yum check-update sudo  #如果有则返回新版本的版本号
4.升级 yum update sudo           #升级指定的也是软件名

5.软件包查询 yum list | more      #返回yum源所有的软件包名，以及是否安装过的信息
6.相关查询 yum list | grep sudo   #返回yum源中和sudo相关的软件包

7.软件包信息 yum info sudo        #返回软件sudo相关的信息
8.软件卸载 yum remove sudo
9.软件帮助 yum –help 或者 man yum
```

> 升级内核，docker要求内核在3.10以上

```shell
$ cat /etc/issue    #查看发行版信息：CentOS release 6.9 (Final)
$ uname -r  #查看当前内核版本

#（1）导入public-key
rpm --import https://www.elrepo.org/RPM-GPG-KEY-elrepo.org

#（2.1）为 CentOS-6 或 RHEL-6，SL-6安装ELRepo
rpm -Uvh http://www.elrepo.org/elrepo-release-6-8.el6.elrepo.noarch.rpm

#（2.2）为 CentOS-7 或 RHEL-7，SL-7安装ELRepo：
rpm -Uvh http://www.elrepo.org/elrepo-release-7.0-3.el7.elrepo.noarch.rpm (external link)

#（3.1）长期支持版本（更稳定）：kernel-lt
yum --enablerepo=elrepo-kernel install kernel-lt -y 

#（3.2）主线最新版本：kernel-ml
yum --enablerepo=elrepo-kernel install kernel-ml -y

#（4）修改文件 /etc/grub.conf，将 default 值改为0
#boot=/dev/vda
default=0
timeout=5

#（5）重启服务器，重启之后执行 uname -r 查看最新内核
reboot 或 shutdown -r now
```



> nano编辑器

```shell
yum -y install nano #安装
nano 路径+文件名      #新建/打开

退出: Ctrl+x （y确认）   保存修改: Ctrl+o    取消返回: Ctrl+c
剪贴/删除整行: Ctrl+k    复制整行: Alt+6        粘贴: Ctrl+U 
```
> linux 定时任务 crontab

```shell
rpm -qa | grep crontab #检查是否安装（rpm: Red-Hat Package Manager）

sudo /etc/init.d/cron start(stop/restart) #命令形式-启动
sudo service cron start(stop/restart)     #服务形式-启动

crontab -l(e/r)    #列出（编辑/删除）当前用户的定时任务

/var/log/cron.log  #centOS日志的存储位置，对于ubuntu: http://www.cnblogs.com/nieqibest/p/9353927.html
```
```shell
#分 时 日 月 周 (user可省) cmd
m h dom mon dow (user)  command

* * * * * cmd           #每分钟执行一次任务  
0 * * * * cmd           #每小时的0点执行一次任务，如6:00; 10:00
6,10 * 2 * * cmd        #每个月2号，每小时的6分和10分执行一次任务
*/3,*/5 * * * * cmd     #每隔3分钟或5分钟执行一次任务，比如10:03, 10:05, 10:06 
0 23-7/2,8 * * * cmd    #晚上11点到早上8点之间每2个小时和早上8点 
20 3 * * * (xxx; yyy)   #每天早晨3点20分执行用户目录下的两个指令（每个指令以;分隔）
0 11 4 * mon-wed /etc/init.d/smb restart  #每月的4号与每周1到周3的11点重启smb 
```
```shell
#"%"是特殊字符（换行），所以命令中必须对其进行转义（\%）
*/2 * * * * echo $(date '+\%Y-\%m-\%d \%H:\%M:\%S')  >> file 
```

> Linux定时脚本

```shell
#!/bin/bash 
DATE=$(date '+%Y-%m-%d %H:%M:%S') #'%'对于 crontab 是关键字，而对于 shell 脚本则不是，即不需要转义
LSOF=$(lsof -p $(lsof -t +D /var/lib/webpark/logs/device) |wc -l)
CLOSE=$(netstat -anp |grep java |grep CLOSE |wc -l) #执行结果赋值给变量

cd /var/lib/webpark/logs/sm/file
echo $DATE '---' $LSOF >> lsof
echo $DATE '---' $CLOSE > close
grep -d skip -n 'Init---args' ../_* > ztj
```
> windows 定时任务

```java
'右键'计算机 -> 管理 -> 系统工具 -> 任务计划程序 -> '右侧'创建任务 ->
    常规 -> '填写'名称
    触发器 -> 新建 -> 每天 -> 开始 2019-6-11 20:10:23
    操作 -> 新建 -> 操作 -> '可选择'启动程序 -> 浏览 -> 选择'*.bat文件'
```




## 组合命令

>`nohup java -jar springboot.jar > /dev/null 2>&1 &` 后台启动java项目

```shell
nohup java -jar blue.jar > ./logs/blue.log 2>&1 &               #输出日志
nohup java -jar blue.jar > ./logs/blue.log >/dev/null 2>&1 &    #不再输出日志

nohup：该命令可以在 '退出帐户/关闭终端' 之后继续运行相应的进程。默认情况，该作业的所有输出都被重定向到一个名为'nohup.out'的文件中
```
```shell
&：表示在后台运行。nohup COMMAND & #命令永久的在后台执行
```
```shell
2>&1：默认，0表示标准输入，1表示标准输出，2表示标准错误。

nohup command > myout.file 2>&1 &
#表示将标准错误(2)重定向到标准输出(&1),标准输出(&1)再被重定向输入到 'myout.file' 文件中
```

```shell
/dev/null：表示空设备，这里就是把日志记录到空设备里，就是不记录日志。

/dev/null 2>&1 #将产生的所有信息丢弃
```

> SpringBoot项目启动脚本

```shell
#!/bin/bash
PID=$(lsof -t -i:8090) #查看8090端口程序的pid

#-gt greaterthan,大于; -lt lessthan,小于; -eq equal,等于; -a and,且; -o or,或
if [ $PID ]
then
	echo "8090 PID: $PID"
	kill -9 $PID
else
	echo "8090 NO PID!"
fi

cd /var/tmp
chmod 777 demo.jar
nohup jdk1.8.0_181/bin/java -jar demo.jar >/dev/null 2>&1 &  #赋权，并以后台启动
echo "start OK!~!"
```

>kill -9 \`lsof -t -i:8090\`

```shell
kill -9 `lsof -i:8090 | awk '{print $2}' | sed -n '2p'`         #等同于上面命令

（1）用``表示，引用命令的执行结果
（2）awk '{print $2}' #表示输出第二列内容
（3）sed -n '2p'      #表示输出第二行
（4）nohup cmd &      #表示后台执行命令cmd。nohup是不挂起的意思(no hang up)，它保证在退出帐户之后继续运行相应的进程
（5）/dev/null        #表示文件黑洞，起到'禁止输出'功能

cmd > /dev/null 2>&1 #屏蔽指令的 stdout 和 stderr

#一般情况下，每个 Unix/Linux 命令运行时都会打开三个文件:
（a）标准输入文件(stdin): stdin的文件描述符为0, Unix程序默认从stdin读取数据。
（b）标准输出文件(stdout): stdout 的文件描述符为1, Unix程序默认向stdout输出数据。
（c）标准错误文件(stderr): stderr的文件描述符为2, Unix程序会向stderr流中写入错误信息。

# 2>&1 -> 表示stderr的输出方式同stdout，都是禁止输出
```






##相似命令

> 文件读取

```shell
cat -n file  #从第1行开始,显示全部（n：示行号）
tac file     #从倒1行开始,显示全部

nl file      #从第1行开始,显示全部.自动显示行号

more file       #1次显示1屏内容（空格：下1屏内容）
more -5 file    #设置1屏只显示5行
more +100 file  #从100行开始显示

less file       #从倒1行开始，1次显示1屏内容

head -n 10 file #只显示前10行
tail -n 10 file #.....倒....

sed -n '[i],[j]p' #输出第i到第j行内容。其中，j<=i时，只输出第i行
sed -n '3,5p'     #显示第3到第5行的内容

cat -n file | head -n 5 | tail -n +3 #效果同上
cat -n file | tail -n +3 | head -n 3 #效果同上
```

> 文件查找

```shell
先考虑 whereis 或 locate，最后才考虑 find。#前两者是利用数据库来搜寻数据，相当快速！

whereis java  #只用于查找二进制文件，源代码和man手册页；一般文件的定位需使用locate命令

which java    #在环境变量 $PATH 中查找符合条件的文件

locate mysql.cnf #查找 mysql.cnf 文件

find . -name 'sm*' #（递归）查找当前目录下名为 sm* 的文件及文件夹
```





#常用命令

## ls

> 显示指定目录下的内容（文件及子目录）

```shell
#-a: 显示所有，包含.开头的隐藏文件
#-l: 显示文件的详细信息
#-h: 以K，M，G等形式显示文件大小
#-R: 迭代所有文件（包含子目录下文件）

#-t: 以最后修改时间降序排列（先新后旧）
#-F: 在文件名称后加一符号（如，可执行文档加'*',目录则加'/'）
#-r: 以相反次序显示（原以英文字母次序）

ls -lh s*    #列出目录下所有名称 s 开头的文件和文件夹
```
## mkdir

>创建目录

```shell

```

## lsof

> 列出当前系统打开的文件（list open files）安装：yum install lsof

```shell
默认: #没有选项，列出所有活跃进程打开的所有文件
-t:  #仅获取进程 PID
-c string: #显示 COMMAND 列中包含指定字符的进程所有打开的文件，如 -c java

fileName:  #显示指定'文件'相关的所有进程
+d /DIR/:  #........'目录'..............
+D /DIR/:  #........'目录及子目录'...... （递归显示）

-p<进程号>: #列出指定进程号所打开的文件

-u:  #显示所属user进程打开的文件
-n:  #不将IP转换为hostname，缺省是不加上-n参数
-a:  #所有参数都满足时才显示结果
```
```shell
lsof -t -c java -i:8080       #java项目的pid，-c -i 默认是 或 的关系
lsof (-a) -t -c java -i:8080  #-a 代表 且 的关系

lsof blue/logs/info  #和文件 info 相关的进程信息
lsof +d blue/logs    #和目录 logs ............
lsof +D blue/logs    #迭代显示，当前目录及其子目录的打开文档信息

lsof -p 2853  #该进程相关的文件
```
```shell
#lsof -i[46] [protocol][@hostname|hostaddr][:service|port]

lsof -i:8090    #端口8090相关的网络信息
lsof -t -i:8090 #............进程pid

lsof -i6:8090     #端口8090的 IPV6 进程
lsof -i tcp       #仅显示tcp连接（udp同理）
lsof -i@host      #显示基于指定主机的连接
lsof -i@host:port #显示基于主机与端口的连接
```



## grep

> 根据 [文件内容] 进行查找

```shell
#-n: 显示匹配行及行号
#-w: 只匹配整个单词,而不是字符串的一部分（如匹配'magic',而不匹配'magical'）
#-r: 迭代查找,包含子目录
#-i: 不区分大小写
#-v: 显示不包含匹配文本的所有行，即排除某个结果，反向过滤
#-A <行数n>: 除显示符合范式那一列之外，并显示该行之[后]的n行内容
#-B <行数n>: ................................[前]........

#-d: 对象为目录时,使用此命令 grep -d skip
#-h: 查询多文件时不显示文件名
#-l: 查询多文件时只输出包含匹配字符的文件名
#-a: 不要忽略二进制数据
```
```shell
grep -n 'magic' /logs/sm/* > magic  #查找并将结果重定向到文件。结果附加行号，查找时默认跳过目录
grep -win 'magic' * > magic         #匹配单词'magic'，不区分大小写，结果附加行号

grep 'magic' d* --color=auto        #从以d开头的文件中查找，并将结果标记颜色
grep -w 'magic' aa bb cc            #从指定的三个文件中查找，匹配magic单词，而非包含magic的

grep -d skip -n 'magic' *           #忽略子目录查找
grep -r 'magic' *                   #迭代........

ps -ef | grep 33306 | grep -v grep | awk '{print($2)}' #mysql的pid。（-v 反向过滤）
```
## split

> 切割文件

```shell
#-d: 使用数字作为后缀
#-b: 指定每多少字节切成一个小文件
#-l <行数> / -<行数>: 指定每多少行切成一个小文件

split -5 test.log       #切割文件每个5行
split -l 5 test.log     #同上

split -b 100k test.log  #切割文件每个100KB

split -b 100k test.log test -d #切割文件并指定前缀为test，后缀为从00开始的数字
```
```shell

```

## sort

> 将结果进行排序

```shell
#-n: 按数值大小，正序排序（默认正序）
#-r: 以相反的顺序来排序
#-u: 忽略相同行
#-b: 忽略每行前面开始出的空格字符
#-k: 以哪个区间 (field) 来进行排序
#-t<分隔符>: 指定分隔符，默认的分隔符为空白字符和非空白字符之间的空字符
#+<起始栏位>-<结束栏位>: 以指定的栏位来排序，范围由起始栏位到结束栏位的前一栏位

sort (-r) file #以默认的方式将文本文件的第一列以 ASCII码的次序排列，并将结果输出到标准输出

ls -l | sort -n -k 5       #以默认的空格 分割为例，按照第5列的数值大小进行排序
sort -t $'\t' -k 2.7 file  #以 TAB 分割为列，对第2列的第7个字符进行排序
```
```shell

```

## uniq

> 用于检查及删除文本文件中重复出现的行列，一般与 sort 命令结合使用

```shell
#-c: 在行首显示该行重复出现的次数
#-d: 仅显示重复出现的行列
#-w<字符位置>: 指定要比较的字符
#-u: 仅显示出一次的行列

sort file | uniq -c | sort -r
```
```shell

```



# 文件相关

##touch

> 新建空文件，或修改文件的时间属性（ls -l 查看文件的时间属性）

```shell
touch file   #新建空文件file
```

>清空文件的③种方式

```shell
 > file            #使用重定向方法
true > file        #使用true命令重定向清空文件
echo -n "" > file  #要加上"-n"参数，默认情况下会有"\n"，即有个空行
```
##echo

> 显示普通字符串，或转义字符。" "会将内容转义，' '不会转义，原样输出

```shell
echo "$(date)"   #输出当前时间
echo '$(date)'   #输出 $(date)

echo $(date '+%Y-%m-%d %H:%M:%S') #输出时间格式化

echo a b c | awk '{print $1,$3}'  #查看一行的 第一列 和 第三列
```
>显示变量，或执行结果。shell中引用执行结果，有两种表达方式：'date' 和 $(date)，后者适用于嵌套情况

```shell
#查看文件句柄数（1）
echo $(lsof -p $(lsof -t +D /var/lib/webpark/logs/device) |wc -l) #执行结果

#查看文件句柄数（1）
echo $(date) '---' $(lsof -p $(lsof -t -i:8080|sed -n '1p') |wc -l) >> /file

#查看close_wait
#!/bin/bash
DATE=$(date '+%Y-%m-%d %H:%M:%S')
CLOSE=$(netstat -anp |grep java |grep CLOSE |wc -l)

echo $DATE '---' $CLOSE >> close #变量
```

> 是否显示换行。

```shell
#!/bin/sh
echo -n "OK!" #-n表示不换行，即只输出一行
echo "It is a test"
```







## cp

>复制文件或目录

```shell
#-f：覆盖已经存在的目标文件而不给出提示。
#-i：与-f选项相反，在覆盖目标文件之前给出提示，要求用户确认是否覆盖，回答"y"时目标文件将被覆盖。
#-r：若给出的源文件是一个目录文件，此时将复制该目录下所有的子目录和文件。

#-a：此选项通常在复制目录时使用，它保留链接、文件属性，并复制目录下的所有内容。其作用等于dpR参数组合。
#-d：复制时保留链接。这里所说的链接相当于Windows系统中的快捷方式。
#-p：除复制文件的内容外，还把修改时间和访问权限也复制到新文件中。
#-l：不复制文件，只是生成链接文件。

cp test.log ./tmp   #单个复制
cp –rf ./test ./tmp #批量复制
```

##mv

> 对文件或目录，进行移动或重命名

```shell
#-i: 提示，若指定目录已有同名文件，则先询问是否覆盖旧文件;
#-f: 不提示，在mv操作要覆盖某已有的目标文件时不给任何指示;

mv /a.txt /b.txt       #前后两目录一致，指定新文件名 --> 重命名
mv /a.txt /test/c.txt  #.......不...，指定新文件名 --> 移动 + 重命名

mv /a.txt /test/       #.......不...，【没有】指定新文件名 --> 移动

mv /student/* .       #批量移动到当前目录
```
##mkdir

> 创建目录及子目录

```shell
#-p：如果上级目录没有创建，即创建输入路径上的所有目录

mkdir a b c      #一次性创建多个目录
mkdir -p /a/c/v  #创建多层目录
```

##rm

> 删除文件或目录

```shell
#-i：删除前逐一询问确认。
#-f：强制删除，即使原档案属性设为唯读，亦直接删除，无需逐一确认。
#-r：迭代删除，将目录及以下之档案亦逐一删除。

rm -rf /var/log   #递归 + 强制 删除
```

##tail

> 默认显示末尾10行，如给定多个文件，则在显示时，每个文件前加一个文件名标题。如未指定文件或文件名为'-'，则读取标准输入

```shell
#-n<N>或——line=<N>: 文件尾部的 N 行内容
#-f: 动态显示文件最新的追加内容 (适合查看日志)
#-s<秒数>或——sleep-interal=<秒数>: 与'-f'选项连用,指定监视文件变化的间隔秒数

#-c<N>或——bytes=<N>: 文件尾部的N个字节内容
#--pid=<进程号>:与'-f'选项连用，当指定的进程号的进程终止后，自动退出tail命令

tail -n 5 file    #最后5行内容
tail -n +5 file   #第5行至末尾（包含第5行）

tail -f -n 3 file #循环查看文件的最后 3 行内容

tail -c 10 file   #最后 10 个字符
```





#其他命令

##df

>显示系统的磁盘使用情况

```shell
#-l: 列出文件结构
#-h: 以人类可读的格式显示大小

df -lh          #当前linux系统所有目录的磁盘使用情况
df -lh --total  #增加统计信息

df -lh /var/lib/webpark/logs/sm  #查看指定目录所属挂载点，及挂载点的磁盘使用情况
```

##du

>显示指定的目录或文件所占用的磁盘空间

```shell
#--max-depth=<目录层数>: 超过指定层数的目录后,予以忽略

df -lh
du -h --max-depth=1 /var/lib/webpark/logs | sort -nr  #相结合使用,查看磁盘情况
```







































