















#------------------------------------------------------------------------------------------------------------------
0.Docker
	一个开源的应用容器引擎; 一个轻量级容器技术!
	将软件做好配置; 编译成一个镜像; 将镜像发布出去; 其他使用者就可以直接使用这个镜像
	运行中的这个镜像称为容器,容器启动是非常快速的!
	
	主机(Host)		->	安装了Docker程序的机器 (Docker直接安装在操作系统之上)
	客户端(Client)	->	连接docker主机进行操作
	仓库(Registry)	->	用来保存各种打包好的软件镜像
	镜像(Images)	->	软件打包好的镜像;放在docker仓库中
	容器(Container)	->	镜像启动后的实例称为一个容器; 容器是独立运行的一个或一组应用

	#Ubuntu安装Docker:
		$ uname -a							//内核版本必须是3.10及以上
		$ apt-get install docker.io			//安装Docker -(可能存在权限错误,使用时添加 sudo 前缀)
		$ service docker.io status/start	//启动服务和守护进程
		$ docker version					//检测是否安装成功
		$ ln -sf /usr/bin/docker.io /usr/local/bin/docker	//创建软连接-(方便使用docker命令)

	#CentOS安装Docker:
		$ yum install docker
		$ systemctl start/stop docker
		$ docker -v							//docker版本
		$ systemctl enable docker			//开机启动
		
	#镜像下载:
		1).docker search x 或 https://hub.docker.com/explore/	//查找镜像
		2).docker pull x:y	//下载镜像,x为镜像名,y为版本号. (可以使用镜像加速 registry.docker-cn.com/library/x:y)
		3).
	
		去Docker仓库找到这个软件对应的镜像
		3).使用Docker运行这个镜像,这个镜像就会生成一个Docker容器
		4).对容器的启动停止就是对软件的启动停止	

	#docker命令:
		docker search 关键字		-> 检索软件的相关信息(eg: docker search redis)
		docker pull 镜像名:tag		-> 版本号tag可选,默认为最新版latest
		docker images				-> 查看本地所有镜像及id
		docker rmi 镜像id			-> 删除指定的本地镜像
		
		docker stop/start/rm 容器id	->	停止/启动/移除 运行中的容器
		docker rm 容器id			->	移除容器(一定要是停止状态); rm->移除容器; rmi->移除镜像!
		docker ps					->	查看运行中的容器
		docker ps ‐a				->	查看所有的容器
		
		docker logs 容器名/容器id	-> 查看容器日志
		
		docker run ‐‐name mytomcat ‐d tomcat:latest		->	根据镜像启动容器
		docker run ‐d ‐p 8888:8080 tomcat		->	启动一个做了端口映射的tomcat
			'‐d: 后台运行;	‐p: 端口映射 -> 主机端口:容器内部的端口; --name:容器别名'
		
		#正确启动mysql镜像,并做了端口映射
		docker run ‐p 3306:3306 ‐‐name mysql02 ‐e MYSQL_ROOT_PASSWORD=123456 ‐d mysql
		
		#把主机的 /conf/mysql 文件夹挂载到 mysqldocker容器的 /etc/mysql/conf.d 文件夹里面
		#以后mysql的配置可以直接在 自定义文件夹下(/conf/mysql)更改
		docker run ‐‐name mysql03 ‐v /conf/mysql:/etc/mysql/conf.d ‐e MYSQL_ROOT_PASSWORD=my‐secret‐pw ‐d mysql:tag

		#指定mysql的一些配置参数
		docker run ‐‐name mysql04 ‐e MYSQL_ROOT_PASSWORD=my‐secret‐pw ‐d mysql:tag ‐‐character‐set‐server=utf8mb4 ‐‐collation‐server=utf8mb4_unicode_ci
			
		#更多命令: https://docs.docker.com/engine/reference/commandline/docker/