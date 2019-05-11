[TOC]





# Docker

## 基础概念

能够把应用程序自动部署到容器的开源引擎，轻量级容器技术！

简化程序：将软件做好配置依赖 --> 编译成镜像 --> 镜像发布 --> 其他使用者就可以直接使用这个镜像。

简化部署：传统做法先安装（包管理工具或者源码包编译），再配置和运行。Docker模式为复制镜像，然后运行。

```java
主机(Host)		->	安装了Docker程序的机器(Docker直接安装在操作系统之上)
客户端(Client)    ->  命令行
仓库(Registry)	->	用来保存各种打包好的软件镜像
镜像(Images)	    ->	软件打包好的镜像，放在docker仓库中
容器(Container)	->	运行中的这个镜像称为容器，容器启动是非常快速的！
```
> Ubuntu安装

```shell
$ uname -r							#内核版本必须是3.10及以上
$ apt-get install docker.io			#安装Docker -(可能存在权限错误,使用时添加 sudo 前缀)
$ service docker status/start		#启动服务和守护进程
$ docker -v							#检测是否安装成功
$ ln -sf /usr/bin/docker.io /usr/local/bin/docker	#创建软连接-(方便使用docker命令)
```
```shell
#权限问题：permission denied. Are you trying to connect to a TLS-enabled daemon without TLS?
#注意: 默认情况,执行 docker 都需要运行 sudo 命令. 如何免去 sudo?

sudo groupadd docker			#如果还没有 docker group 就添加一个
sudo gpasswd -a ${USER} docker	#将用户加入该 group 内.然后退出并重新登录就生效啦
sudo service docker restart		#重启 docker 服务
newgrp - docker					#切换当前会话到新 group
```

> CentOS安装

```shell
$ yum install docker
$ systemctl start/restart docker
$ docker -v						#docker版本
$ systemctl enable docker		#开机启动
```

## 相关指令

> 状态相关

```shell
service docker status（Start-Stop-Restart）
docker info
```

> 镜像相关

```shell
docker search mysql
docker pull mysql:5.6.7
#docker pull registry.docker-cn.com/library/mysql:5.6.7 ---> 官方加速

docker images [-q]				#-q: 只显示id
docker rmi [-f] IMAGE_ID
docker rmi $(docker image -q)	#删除所有

docker inspect IMAGE_ID			#相关信息
docker tag IMAGE_ID NEW_NAME:NEW_TAG #拷贝重命名
```

> 容器相关

```shell
docker ps [-a]				     #运行中的容器（-a: 所有）
docker start(SSR) CONTAINER_NAME #容器的启动，停止，重启
docker rm CONTAINER_NAME         #移除容器（停止状态） rm -> 移除容器; rmi -> 移除镜像！

docker top CONTAINER_NAME      #容器内进程
docker inspect CONTAINER_NAME #容器相关信息

docker logs [-t] [--tail 10] CONTAINER_NAME	#容器日志(-t: 显示时间, --tail: 最新10条)
```

> 互动相关

```shell
docker exec -it CONTAINER_NAME /bin/bash    #进入容器.(exit: 退出)

docker cp CONTAINER_NAME:SRC_PATH DEST_PATH #拷出来
docker cp DEST_PATH CONTAINER_NAME:SRC_PATH #拷进去
```

## 配置容器

```shell
--name #为容器指定一个名称：--name ES01
-d     #后台运行容器，并返回容器ID
-e     #设置环境变量：-e ES_JAVA_OPTS="-Xms256m -Xmx256m"	
-p     #端口映射（宿主机:容器） -p 9200:9200

-it    #配合 exec 使用，开启一个交互模式的终端
-v     #挂载宿主机的目录作为配置文件（宿主机目录:容器目录）：-v /conf/mysql:/etc/mysql/conf.d
```

> elasticsearch

```shell
#后台启动 elasticsearch，指定内存大小，端口号，及名称，web通信使用 9200，分布式集群的节点间通信使用 9300
docker run --name ES01 -d -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -p 9200:9200 -p 9300:9300 4f7e4c61f09d

#将 IK 插件解压到宿主机，然后配置docker容器加载宿主机 /plugins 目录
docker run --name ES02 -d -e ES_JAVA_OPTS="-Xms256m -Xmx256m" -p 9201:9200 -p 9301:9300 -v /var/tmp/plugins:/usr/share/elasticsearch/plugins 4f7e4c61f09d
```

> tomcat

```shell
#tomcat：最后一个参数是 镜像名:版本号(latest可省)
docker run --name tomcat01 -d -p 9090:8080 tomcat:8.5-jre8-alpine
```

> mysql

```shell
#mysql的root密码
docker run --name mysql01 -d -p 33066:3306 -e MYSQL_ROOT_PASSWORD=123456 mysql

#配置mysql参数
docker run --name mysql02 -d -e MYSQL_ROOT_PASSWORD=123456 mysql:tag --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

#将上述配置参数保存到宿主机文件'/conf/mysql'，启动加载宿主机的配置文件。
#即以后可通过修改宿主机的配置文件来配置mysql
docker run --name mysql02 -d -e MYSQL_ROOT_PASSWORD=123456 mysql:tag -v /conf/mysql:/etc/mysql/conf.d
```

```shell
#对于 mysql-8.0.4 之后版本，不能简单的通过 '-e MYSQL_ROOT_PASSWORD=123456' 来指定root密码
docker exec -it 1457d60b0375  /bin/bash #进入mysql所在docker

mysql -u root -p //进入docker-mysql

ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY '123456'; #修改root密码

exit #执行两次，依次退出 docker-mysql 和 docker。
```

## 构建镜像

> commit：通过已有的容器，打包成新的镜像

```shell
#-a：作者相关，-m：描述信息，mysql01：已有容器，skyl/mysql：新镜像
docker commit -a 'skyl' -m 'hello skyl' mysql01 skyl/mysql

#使用新镜像
docker run --name skyl-mysql -d -e MYSQL_ROOT_PASSWORD=123456 mysql
```

> build：从0开始构建，先创建 dockerfile

```shell
#First Dockerfile				##为注释
FROM ubuntu:14.01				#FROM：基础镜像，必须写在第一行
MAINTAINER skyl 'skyl@qq.com'	#MAI*: 作者相关
RUN apt-get update				
RUN apt-get install -y nginx
EXPOSE 80						#运行该容器所使用的端口

#build-构建(dockerfile所在目录)
docker build -t 'skyl-nginx' /var/tmp/docker/
```

## 镜像加速

```shell
#aliyun加速
https://cr.console.aliyun.com/cn-hangzhou/mirrors
	
#daocloud加速
https://hub.daocloud.io/

#直接设置 –registry-mirror 参数,仅对当前的命令有效 
docker run hello-world --registry-mirror=https://docker.mirrors.ustc.edu.cn

#修改 /etc/default/docker，加入 DOCKER_OPTS=”镜像地址”，可以有多个
DOCKER_OPTS="--registry-mirror=https://docker.mirrors.ustc.edu.cn"

#支持 systemctl 的系统,通过 sudo systemctl edit docker.service
#会生成 etc/systemd/system/docker.service.d/override.conf 覆盖默认的参数,在该文件中加入如下内容
[Service] 
ExecStart= 
ExecStart=/usr/bin/docker -d -H fd:// --registry-mirror=https://docker.mirrors.ustc.edu.cn

#新版的 Docker 推荐使用 json 配置文件的方式,默认为 /etc/docker/daemon.json
#非默认路径需要修改 dockerd 的 –config-file,在该文件中加入如下内容
{"registry-mirrors": ["https://docker.mirrors.ustc.edu.cn"]}		
```





# 模块功能

## fastjson

```xml
<dependency>
	<groupId>com.alibaba</groupId>
	<artifactId>fastjson</artifactId>
	<version>1.2.47</version>
</dependency>
```

>`getIntValue()和getInteger()`的区别

```java
json.getInteger("a"); //null --->对于空的key
json.getIntValue("a"); //0
```

> X ---> JSONString 

```java
String json = JSON.toJSONString(list / map / javabean);
String json = JSON.toJSONString(list, true);//args1: json是否格式化(有空格和换行).
```

> JSONString --->X `必须有空构造方法`

```java
Dog dog = JSON.parseObject(json, Dog.class);
Map map = JSON.parseObject(json, Map.class);
List<Dog> list = JSON.parseArray(json, Dog.class);
```

> X ---> JSONObject，先转换为JSONString。其中，`javabean必须有get/set`

```java
JSONObject obj = JSON.parseObject(JSON.toJSONString(dog));//javabean
JSONObject obj = JSON.parseObject(JSON.toJSONString(map));//map
JSONArray array = JSON.parseArray(JSON.toJSONString(list));//list
```
> **null值处理**：list ---> JSONString

```java
QuoteFieldNames         //输出key时是否使用双引号，默认为true
WriteMapNullValue       //是否输出值为null的字段，默认为false
WriteNullListAsEmpty    //List字段如果为null，输出为[]，而非null
WriteNullNumberAsZero   //数值字段如果为null，输出为0，而非null
WriteNullBooleanAsFalse //Boolean字段如果为null，输出为false，而非null

WriteNullStringAsEmpty  //字符类型字段如果为null，输出为""，而非null (√，默认不输出null字段)
```

```java
List<Dog> list = Arrays.asList(new Dog("11", 11), new Dog(null, 22));

// [{"age":11,"name":"11"},{"age":22}] ---> 默认不输出null字段
// String json = JSON.toJSONString(list);

// [{"age":11,"name":"11"},{"age":22,"name":""}]
String json = JSON.toJSONString(list, SerializerFeature.WriteNullStringAsEmpty);
```

> SpringBoot2.x默认使用 jacksonJson 解析，现转换为 fastjson

```java
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
	Logger logger = LoggerFactory.getLogger(getClass());

	//利用fastjson替换掉jackson,且解决中文乱码问题
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		//1.构建了一个消息转换器 converter
		FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();

		//2.添加fastjson配置,如: 是否格式化返回的json数据;设置编码方式
		FastJsonConfig config = new FastJsonConfig();

		config.setSerializerFeatures(SerializerFeature.PrettyFormat);//格式化

		List<MediaType> list = new ArrayList<>();//中文乱码
		list.add(MediaType.APPLICATION_JSON_UTF8);
		converter.setSupportedMediaTypes(list);

		//3.在消息转换器中添加fastjson配置
		converter.setFastJsonConfig(config);
		converters.add(converter);
	}
}
```























