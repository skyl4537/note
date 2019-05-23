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
```
> 测试DEMO

```shell
ls -lh s*    #列出目录下所有名称 s 开头的文件和文件夹
```

## grep

> 根据 [文件内容] 进行查找

```shell
#-n: 显示匹配行及行号
#-w: 只匹配整个单词,而不是字符串的一部分（如匹配'magic',而不匹配'magical'）
#-r: 迭代查找,包含子目录
#-i: 不区分大小写
#-A <行数n>: 除显示符合范式那一列之外，并显示该行之[后]的n行内容
#-B <行数n>: ................................[前]........

#-d: 对象为目录时,使用此命令 grep -d skip
#-h: 查询多文件时不显示文件名
#-l: 查询多文件时只输出包含匹配字符的文件名
#-a: 不要忽略二进制数据。
```
> 测试DEMO

```shell
grep -n 'magic' /logs/sm/* > magic  #查找并将结果重定向到文件。结果附加行号，查找时默认跳过目录
grep -win 'magic' * > magic         #匹配单词'magic'，不区分大小写，结果附加行号

grep 'magic' d* --color=auto        #从以d开头的文件中查找，并将结果标记颜色
grep -w 'magic' aa bb cc            #从指定的三个文件中查找，匹配magic单词，而非包含magic的

grep -d skip -n 'magic' *           #忽略子目录查找
grep -r 'magic' *                   #迭代........
```
## split

> 切割文件

```shell
#-d: 使用数字作为后缀
#-b: 指定每多少字节切成一个小文件
#-l <行数> / -<行数>: 指定每多少行切成一个小文件
```
> 测试DEMO

```shell
split -5 test.log       #切割文件每个5行
split -l 5 test.log     #同上

split -b 100k test.log  #切割文件每个100KB

split -b 100k test.log test -d #切割文件并指定前缀为test，后缀为从00开始的数字
```




