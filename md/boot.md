

# 额外功能

## 小众功能

> 异步任务

```sh
'注意'：异步方法和调用方法'一定要写在不同的类中'，写在同一类中则不起作用。此种情况类似于 @Transactional

'原因'：Spring扫描具有 @Transactional 注解方法的类时，是生成一个代理类，由代理类去开启关闭事务。
而在同一个类中，方法调用是在类体内执行的，Spring无法截获这个方法调用。
```

```java
@EnableAsync //全局注解
@Async       //异步注解，可配置自定义的线程池Bean，如 @Async("demoThreadPool")
public void sendA() throws Exception {
    //...
}
```



# WebSocket

## 基础概念

> 简介

```sh
B/S 结构的软件项目中有时客户端需要实时的获得服务器消息，但默认HTTP协议只支持 请求响应模式。
对于这种需求可以通过 polling，Long-polling，长连接，Flash-Socket，HTML5中定义的WebSocket 完成。

HTTP模式可以简化Web服务器，减少服务器的负担，加快响应速度，因为服务器不需要与客户端长时间建立一个通信链接。
但不容易直接完成实时的消息推送功能（如聊天室，后台信息提示，实时更新数据等）。

应用程序通过 Socket 向网络发出请求或者应答网络请求。Socket 可以使用TCP/IP协议或UDP协议。
```

```sh
TCP协议：面向连接的，可靠的，基于字节流的传输层通信协议，负责数据的可靠性传输问题。
UDP协议："无连接，不可靠"，基于报文的传输层协议，优点：发送后不用管，速度比TCP快。

HTTP协议："无状态协议"，通过 Internet 发送请求消息和响应消息，默认使用80端口。（底层Socket）
```

> Http协议

```sh
HTTP 协议原本是设计用于传输简单的文档和文件，而非实时的交互。

根据 HTTP 协议，一个客户端如浏览器，向服务器打开一个连接，发出请求，等待回应，之后关闭连接。
如果客户端需要更多数据，则需要打开一个新连接，以此循环往复。如果服务器有了新的信息，它必须等待客户端发出请求而不是立即发送消息。

那么要看到页面中要展示信息的最新情况，应该怎么办？不断刷新！

缺点：这种方式现在已经被完全淘汰，发送了很多不必要的请求，浪费大量带宽，页面不断刷新，用户体验差，
而且做不到真正的实时，服务端有了新数据也不能立马推送给客户端，使得秒级的实时信息交互难以实现。

HTTP协议决定了服务器与客户端之间的连接方式，无法直接实现消息推送（F5已坏），一些变相的解决办法：
```

> 双向通信

```sh
Websocket：Html5 提供的一种通过 js 与远程服务器建立连接，从而实现客户端与服务器间双向的通信。
优点：事件驱动，异步，使用ws或者wss协议的客户端socket，能够实现真正意义上的推送功能。
缺点：少部分浏览器不支持，浏览器支持的程度与方式有区别
```

## 客户端

>java websocket如何设置心跳保持连接
>
>https://jingyan.baidu.com/article/67508eb461cb6c9ccb1ce442.html

```html
<head>
    <meta charset="UTF-8">
    <title>WebSocket-测试</title>

    <script>
        var websocket = null;
        if ('WebSocket' in window) {
            websocket = new WebSocket("ws://192.168.5.78:8080/webpark/websocket/all,127.0.0.1"); //注意大小写
        } else {
            alert("浏览器不支持WebSocket!")
        }

        //连接建立
        websocket.onopen = function (event) {
            console.log("连接建立: " + event);
        };

        //连接关闭
        websocket.onclose = function (event) {
            console.log("连接关闭: " + event);
            websocket.send(event.code);
        };

        //连接错误
        websocket.onerror = function (event) {
            setMessageInnerHTML("连接错误: " + event);
        };

        //监听事件 -> 监听窗口关闭事件
        //当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常
        window.onbeforeunload = function () {
            if (null != websocket) {
                websocket.close();
            }
        };

        //接收到服务器发来消息
        websocket.onmessage = function (event) {
            console.log("接收到服务器发来消息: " + event.data);
            setMessageInnerHTML(event.data);
        };

        //将消息显示在网页上
        function setMessageInnerHTML(innerHTML) {
            document.getElementById('message').innerHTML += innerHTML + '<br/>';
            // $('#message').html(innerHTML);
        }

        //向远程服务器发送数据
        function send() {
            var message = document.getElementById('text').value;
            websocket.send(message);
            // websocket.send($('#text'));
        }
    </script>
</head>
<body>
    <input id="text" type="text"/>
    <button onclick="send()">Send</button>
    <div id="message"></div>
</body>
```

## 服务端

````xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>
````

```java
@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
```

```java
@Slf4j
@Component
@ServerEndpoint("/webSocket")
public class WebSocket {

    //区别: 静态变量 和 非静态变量
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    // 旧版：concurrent包的线程安全Set，用来存放每个客户端对应的 WebSocket 对象
    private static final Set<WebSocket> WEB_SOCKET_SET = new CopyOnWriteArraySet<>();

    //客户端注册时调用
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        WEB_SOCKET_SET.add(this);
        log.info("【WebSockt消息】 有连接建立，总连接数: {}", WEB_SOCKET_SET.size());
    }

    //客户端关闭
    @OnClose
    public void onClose(Session session, CloseReason reason) {
        WEB_SOCKET_SET.remove(this);
        log.info("【WebSockt消息】 有连接断开，总连接数: {}", WEB_SOCKET_SET.size());
    }

    //客户端异常
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("【WebSockt消息】 有连接异常: {}", error);
    }

    //收到浏览器客户端消息后调用的方法
    @OnMessage
    public void onMessage(Session session, String message) {
        log.info("【WebSockt消息】 收到客户端消息: {}", message);

        // sendMsg2One("服务器->客户端: " + message);
        sendMsg2All("服务器->客户端: " + message);
    }

    //群发消息 --> 可供外部调用
    public void sendMsg2All(String message) {
        WEB_SOCKET_SET.forEach(webSocket -> webSocket.sendMsg(message));
    }

    //点对点发送消息
    public void sendMsg2One(String message) {
        sendMsg(message);
    }

    //实现服务器主动推送
    private void sendMsg(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
            // this.session.getAsyncRemote().sendText(message);
        } catch (Exception e) {
            log.error("【WebSockt消息】 向客户端发送消息异常: {}", e);
        }
    }

    //更高级的注解，MaxMessageSize 属性可以被用来定义消息字节最大限制，
    //在示例程序中，如果超过6个字节的信息被接收，就报告错误和连接关闭。
    // @Message(maxMessageSize = 6)
    // public void receiveMessage(String s) {}
}
```

# 上传下载

## 文件上传

> 前台页面

```sh
'form表单'： POST + enctype="multipart/form-data"
'file标签'：必须要有 name 属性
'后台使用'： MultipartFile 接收文件资源
```

```html
<form action="/upload" method="post" enctype="multipart/form-data">
    File0: <input type="file" name="file"><br>
    <!-- File1: <input type="file" name="file"><br> --> <!-- 多文件上传，name必须一致 -->
    <!-- Files：<input type="file" name="file" multiple> --> <!-- multiple标签表示支持多文件上传 -->
    Desc: <input type="text" name="desc"><br>
    <input type="submit" value="提交">
</form>
```

> 单个上传

```java
@PostMapping("/upload")
public void upload(@RequestParam("file") MultipartFile file,
                   @RequestParam("desc") String desc) {
    log.info("文件名: {}. 文件大小: {}. 文件描述: {}", file.getOriginalFilename(), file.getSize(), desc);
    file.transferTo(new File(getUploadDir(), file.getOriginalFilename())); //文件另存
}
```

```java
public File getUploadDir() throws FileNotFoundException {
    File dir = new File(ResourceUtils.getURL("").getPath(), "/upload"); //项目根目录下
    if (!dir.exists()) {
        dir.mkdirs(); //创建当前及父目录.(区别于 mkdir())
    }
    return dir;
}
```

> 批量上传

```java
@PostMapping("/uploads")
public void batchUplocad(@RequestParam("file") List<MultipartFile> files,
                         @RequestParam("desc") List<String> descs) {
    files.forEach(x -> x.transferTo(getUploadDir(), x.getOriginalFilename())); //文件另存 
    descs.forEach(log::info); //文件描述
}
```

> 相关配置

```properties
#单个上传文件的大小
spring.servlet.multipart.max-file-size=10MB
#一次请求上传文件的总容量
spring.servlet.multipart.max-request-size=20MB
```

## 文件下载

> 文件下载一般都借助于以下两个 `响应头` 达到效果

```sh
'Content—Type'：告知浏览器当前的响应体是什么类型的数据
当为'application/octet-stream'时，就说明 body 里是一堆不知道是啥的二进制数据

'Content—Disposition'：用于向浏览器提供一些关于如何处理响应内容的额外的信息，同时也可以附带一些其它数据
比如，在保存响应体到本地的时候应该使用什么样的文件名
```

> 代码实现

```java
@GetMapping("/{fileName}")
public void download(@PathVariable String fileName, HttpServletResponse resp) {
    try (InputStream in = new FileInputStream(new File(getUploadDir(), fileName));
         OutputStream out = resp.getOutputStream()) {
        resp.setContentType("application/x-download");
        resp.addHeader("Content-Disposition", "attachment;filename=" + fileName); //注意中文乱码
        IOUtils.copy(in, out);
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```



# 跨域问题

##基础概念

> 什么是跨域？

```sh
'跨域'：是指跨域名的访问，浏览器不能执行其他网站的脚本。'域名、端口、协议任一不同'，就是跨域。
采用前后端分离开发，前后端分离部署，必然会存在跨域问题。
#注意：localhost 和 127.0.0.1 虽然都指向本机，但也属于跨域。
```

```sh
#非跨域
http://www.123.com/index.html  --->  http://www.123.com/server.PHP

# 跨域（主域名不同：123/456）
http://www.123.com/index.html  --->  http://www.456.com/server.php

# 跨域（子域名不同：abc/def）
http://abc.123.com/index.html  --->  http://def.123.com/server.php

# 跨域（端口不同：8080/8081）
http://www.123.com:8080/index.html  --->  http://www.123.com:8081/server.php

# 跨域（协议不同：http/https）
http://www.123.com/index.html  --->  https://www.123.com/server.php
```

> 为什么有跨域问题？

```sh
#跨域不一定会有跨域问题。
因为跨域问题是浏览器对于ajax请求的一种安全限制：一个页面发起的ajax请求，只能用于当前页同域名的路径，这能有效的阻止跨站攻击。
因此：'跨域问题是针对ajax的一种限制'。
但是，这却给开发带来了不便，而且在实际生成环境中，肯定会有很多台服务器之间交互，地址和端口都可能不同，怎么办？
```

> 解决方案

```sh
#Jsonp
最早的解决方案，利用 <script/> 标签可以跨域的原理实现。限制：
（1）需要服务的支持
（2）只能发起GET请求
```

```sh
#nginx反向代理
思路：利用 nginx 反向代理把跨域转为不跨域，支持各种请求方式
缺点：需要在 nginx 进行额外配置，语义不清晰

在 manage.leyou.com 中需要访问接口 api.leyou.com/api/item/list，这就涉及到跨域访问。
nginx反向代理的实现方案做法是：不直接访问 api.leyou.com，而是访问 manage.leyou.com/api/item/list，
在 nginx 中将路径 /api/item/list 单独做一个映射，将其映射到 api.leyou.com 服务器。
```

```sql
server{
    listen 80
    server_name manage.leyou.com
    location /api/item/list{ --单独映射跨域路径。跨域访问多，此配置就越多
    	proxy_pass http://127.0.0.1:10010; #api.leyou.com 端口
    }
}
```

```sh
#CORS
规范化的跨域请求解决方案，安全可靠。
优势：在服务端进行控制是否允许跨域，可自定义规则。支持各种请求方式
缺点：会产生额外的请求
```

## CORS

> 什么是 CORS？

```sh
CORS是一个 w3c 标准，全称是"跨域资源共享"（Cross-origin resource sharing）。
它允许浏览器向跨源服务器，发出'XMLHttpRequest'请求，从而克服了'ajax'只能'同源使用'的限制。

CORS需要浏览器和服务器同时支持。目前，所有浏览器都支持该功能，IE浏览器不能低于IE10。
#浏览器端：
目前，所有浏览器都支持该功能（IE10以下不行）。整个CORS通信过程，都是浏览器自动完成，不需要用户参与。
#服务端：
CROS通信与ajax没有任何差别，因此不需要改变以前的业务逻辑。
只不过，浏览器会在请求中携带一些头信息，需要以此判断是否运行其跨域，然后在响应头中加入一些信息即可。这一般通过'过滤器'完成即可。
```

> 基本原理

```sh
浏览器会将ajax请求分为两类，其处理方案略有差异：简单请求、特殊请求。

#简单请求：HEAD，GET，POST
当浏览器发现发现的ajax请求是简单请求时，会在请求头中携带一个字段：Origin。如 'Origin: http://manage.leyou.com'
Origin中会指出当前请求属于哪个域（'协议+域名+端口'）。服务会根据这个值决定是否允许其跨域。

如果服务器允许跨域，需要在返回的响应头中携带下面信息：
Access-Control-Allow-Origin: http://manage.leyou.com #可接受的域，是一个具体域名 或 代表任意的 *
Access-Control-Allow-Credentials: true #是否允许携带cookie，默认情况下，cors不会携带cookie，除非这个值是true
Content-Type: text/html; charset=utf-8
```

```sh
#特殊请求：除简单以外的请求
特殊请求会在正式通信之前，增加一次HTTP查询请求，称为'预检请求'（preflight）。

浏览器先询问服务器，当前网页所在的域名是否在服务器的许可名单之中，以及可以使用哪些HTTP动词和头信息字段。
只有得到肯定答复，浏览器才会发出正式的'XMLHttpRequest'请求，否则就报错。
```

##解决方案

> 细粒度

```java
//允许可访问的域列表；准备响应前的[缓存持续]的最大时间（以秒为单位）
@CrossOrigin(origins = "http://manage.leyou.com", maxAge = 3600)
@GetMapping("/java")
public String java() {
    return LocalDateTime.now().toString();
}
```

> 粗粒度

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://manage.leyou.com") //允许端口 9005 访问
            .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE", "HEAD", "PATCH")
            .maxAge(3600)
            .allowCredentials(true);
    }
}
```
