





# ---day07---

# 域名访问

## 域名解析

>域名解析

```sh
一个域名可以被解析为一个或多个ip。#解析过程分为：本地域名解析 + 域名服务器解析。

#本地域名解析
浏览器首先会在本机的 hosts 文件中查找域名映射的ip，有则用。一般本地解析都会失败，因为这个文件默认是空的。
Windows下的hosts文件地址：  'C:/Windows/System32/drivers/etc/hosts'
Linux下的hosts文件所在路径： '/etc/hosts '

#域名服务器解析
本地解析失败，才会进行域名服务器解析。
域名服务器就是网络中的一台计算机，里面记录了所有注册备案的域名和ip映射关系，一般只要域名是正确的，并且备案通过，一定能找到。
```

> 伪造本地hosts

```sh
推荐使用软件'SwitchHosts'修改本地的hosts文件，然后通过 ping 命令查看是否畅通。

# leyou
127.0.0.1 api.leyou.com #网关Zuul
127.0.0.1 manage.leyou.com #后台系统
```

## 端口问题

> Nginx

```sh
虽然域名解决了，但是现在如果我们要访问，还得自己加上端口：http://manage.leyou.com:9001。
这就不够优雅了。希望的是直接域名访问：http://manage.leyou.com。这种情况下端口默认是80，如何才能把请求转移到9001端口呢？
这里就要用到反向代理工具：Nginx
```

```sh
#Nginx 是一个高性能的 Web 和反向代理服务器，它具有有很多非常优越的特性：
'作为 Web 服务器'：相比 Apache，Nginx使用更少的资源，支持更多的并发连接（50000个），体现更高的效率
'作为负载均衡服务器'：Nginx既可以在内部直接支持 Rails 和 PHP，也可以支持作为 HTTP代理服务器 对外进行服务。
'安装非常的简单'：配置文件非常简洁（还能够支持perl语法），Bugs非常少的服务器。Nginx启动特别容易，并且几乎可以做到7*24不间断运行，
即使运行数个月也不需要重新启动。你还能够在不间断服务的情况下进行软件版本的升级。
```

```sh
启动   ：start nginx.exe
停止   ：nginx.exe -s stop
重新加载：nginx.exe -s reload
```

```sh
#网关必备的功能：反向代理，负载均衡，动态路由，请求过滤
```

```sh
#Web服务器分2类：
web应用服务器，如：tomcat，jetty
web服务器，如：Apache，Nginx
#区分：web服务器不能解析jsp等页面，只能处理js、css、html等静态资源。 并发：【web服务器】的并发能力远高于【web应用服务器】。
```

```js
server {
    listen       80;
    server_name  manage.leyou.com;

    proxy_set_header X-Forwarded-Host $host;
    proxy_set_header X-Forwarded-Server $host;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;

    location / {
        proxy_pass http://127.0.0.1:9001;
        proxy_connect_timeout 600;
        proxy_read_timeout 600;
    }
}
```

> 域名解析过程

```sh
浏览器准备发起请求，访问'http://mamage.leyou.com'，但需要进行域名解析
优先进行本地域名解析，因为我们修改了hosts，所以解析成功，得到地址：127.0.0.1
请求被发往解析得到的ip，并且默认使用80端口：http://127.0.0.1:80

本机的nginx一直监听80端口，因此捕获这个请求
nginx中配置了反向代理规则，将'manage.leyou.com'代理到 127.0.0.1:9001，因此请求被转发
后台系统的 webpack-server 监听的端口是9001，得到请求并处理，完成后将响应返回到nginx
nginx将得到的结果返回到浏览器
```



# axios

## axios入门

```sh
异步查询数据，自然是通过ajax查询，大家首先想起的肯定是jQuery。但jQuery与MVVM的思想不吻合，而且ajax只是jQuery的一小部分。
因此不可能为了发起ajax请求而去引用这么大的一个库。Vue官方推荐的ajax请求框架叫做：axios。

#注意：POST请求传参，不需要像 GET 请求那样定义一个对象，在对象的params参数中传参。
#post()方法的第二个参数对象，就是将来要传递的参数。 PUT 和 DELETE 请求与POST请求类似
```

```sh
#axios是ajax，ajax不止axios。
axios 是通过promise实现对ajax技术的一种封装，就像jQuery实现ajax封装一样。
简单来说： ajax技术实现了网页的局部数据刷新，axios实现了对ajax的封装。
```

```js
$.ajax({
    url: '/getUsers',
    type: 'get',
    dataType: 'json', //ajax
    data: {
        //'a': 1,
        //'b': 2,
    },
    success: function (response) {
        console.log(response)；
    }
})
```

```js
axios({
    url: '/getUsers',
    method: 'get',
    responseType: 'json', //axios，默认值json
    data: {
        //'a': 1,
        //'b': 2,
    }
}).then(function (response) { //成功
    console.log(response);
    console.log(response.data);
}).catch(function (error) {  //失败
    console.log(error);
}）
```

```js

```

```js

```

