[TOC]





# 正则表达

##基础语法

> 基础概念

```sh
'正则表达式'：符合一定规则的表达式，专门用于操作字符串。使用一些特定的符号来表示一些代码操作，简化代码书写。
#学习正则表达式，就是学习一些特殊符号的使用。
```

> 预定义字符类

```sh
.     任何字符（行结束符 可能匹配、也可能不匹配） 
\d    数字：[0-9] 
\D    非数字： [^0-9] 
\s    空白字符：[ \t\n\x0B\f\r] 
\S    非空白字符：[^\s] 
\w    单词字符（字母、数字、下划线）：[a-zA-Z_0-9] 
\W    匹配任意不是字母，数字，下划线，汉字的字符：[^\w] 
\b	  匹配单词的开始或结束
\B    匹配不是单词开头或结束的位置

[f-k]    匹配 f-k 之间的任意一个字母
[^aei]   匹配除了 a e i 这几个字母以外的任意字符
[\d.\-+] 匹配数字，小数点，+，-
```

> 特殊字符

```sh
^	匹配输入字符串的'开始位置'。在方括号表达式中使用，表示不接受该字符集合。
$	匹配输入字符串的'结尾位置'。如果设置了 RegExp 对象的 Multiline 属性，则 $ 也匹配 '\n' 或 '\r'。

*	匹配前面的子表达式'零次或多次'。
+	匹配前面的子表达式'一次或多次'。
?	匹配前面的子表达式'零次或一次'，或指明一个非贪婪限定符。

( )	标记一个子表达式的开始和结束位置。子表达式可以获取供以后使用。
[	标记一个中括号表达式的开始，表示一个范围。
{	标记'限定符表达式'的开始。
|	指明两项之间的一个选择。要匹配 |，请使用 \|。
\	转义符。例如，序列 '\\' 匹配"\"，而'\(' 则匹配 "("。
```

> 限定符`{}`

```sh
*	零次或多次，等价于 {0,}。例如，zo* 能匹配 "z" 以及 "zoo"
+	一次或多次，等价于 {1,}。例如，'zo+' 能匹配 "zo" 以及 "zoo"，但不能匹配 "z"
?	零次或一次，等价于 {0,1}。例如，"do(es)?" 可以匹配 "do"；"does" 中的 "does"；"doxy" 中的 "do"
```

```sh
{n}	    n是一个非负整数。匹配确定的 n 次。例如，'o{2}' 不能匹配 "Bob" 中的 'o'，但是能匹配 "food" 中的两个 o
{n,}	n是一个非负整数。至少匹配 n 次。例如，'o{2,}' 不能匹配 "Bob" 中的 'o'，但能匹配 "foooood" 中的所有 o

{n,m}	m和n均为非负整数，其中 n<=m。最少匹配n次且最多匹配m次。例如，"o{1,3}" 将匹配 "fooooood" 中的前三个 o
#'o{0,1}' 等价于 'o?'。请注意在逗号和两个数之间不能有空格。
```

##常见DEMO

>字符类

```sh
[a-zA-Z]        #a到z 或 A到Z，两头的字母包括在内（范围） 
[a-d[m-p]]      #a到d 或 m到p：[a-dm-p]（并集） 

[a-z&&[def]]    #d、e 或 f（交集） 
[a-z&&[^bc]]    #a 到 z，除了 b 和 c：[ad-z]（减去） 
[a-z&&[^m-p]]   #a 到 z，而非 m 到 p：[a-lq-z]（减去）
```

```sh
中文字符：   [\u4e00-\u9fa5]
空白行：     \s

email：     \w[-\w.+]*@([A-Za-z0-9][-A-Za-z0-9]+\.)+[A-Za-z]{2,14}
网址url：    ^((https|http|ftp|rtsp|mms)?:\/\/)[^\s]+
国内手机：    0?(13|14|15|17|18|19)[0-9]{9}
国内电话：    [0-9-()（）]{7,18}
腾讯QQ：     [1-9]([0-9]{5,11})
邮政编码：    \d{6}
身份证：     \d{17}[\d|x]|\d{15}  或者 ^\d{15}(\d[2][0-9xX]})?$
```

> java使用

```java
boolean matches = "453705188".matches("[1-9]([0-9]{5,11})"); //匹配 String.matches(regex)
String[] split = "1  55 -4    8 7".split("\\s+");            //切割 String.split(regex)
String replace = "1  55 -4    8 7".replaceAll("\\s", "*");   //替换 String.replaceAll(old, new)
```

```java
public void test05() { 
    String target = "我的QQ号是：453705188";    //获取：从目标字符串中的获取符合规则的子字符串。
    String regex = "[1-9]([0-9]{5,11})";
    Pattern pattern = Pattern.compile(regex);  //1.将正则表达式封装成对象
    Matcher matcher = pattern.matcher(target); //2.将正则表达式和目标字符串关联，获取匹配器对象

    boolean b = matcher.find();         //3.使用规则进行匹配
    if (b) {
        String group = matcher.group(); //4.获取匹配结果
        System.out.println(group);
    }
    // boolean matches = matcher.matches(); //整体匹配，String.matches(regex) 底层实现即是此方法
}
```



# Reflect

## 创建对象

>（1）new创建：传统方式，必须预先知道要使用的类。引用类改变，就必须修改源码。

```java
Person person = new Person();
```

> （2）Cloneable方式：不推荐

```java
public class Person implements Cloneable{} //实现克隆接口

Person clone = (Person) person.clone();
```

> （3）反射方式：动态创建，效率相对低下，耗时是传统方式的 `3` 倍

```java
//先获取 clazz 对象（4种方式，常用1和2），再创建此 clazz 对象所表示的类的一个新实例
Class<?> clazz = Class.forName("com.example.reflect.Person"); //1
Class<?> clazz = getClass().getClassLoader().loadClass("com.example.reflect.Person"); //2
Class<? extends clazz> clazz = new Person().getClass(); //3
Class<Person> clazz = Person.class; //4

Object instance = clazz.newInstance(); //创建实例。当没有无参构造时，将报错
```

## 常用方法

> 构造器

```java
aClass.getConstructors();        //构造器：public
clazz.getDeclaredConstructors(); //构造器：all
```

```java
Constructor<?> constructor = clazz.getConstructor();
Person p = (Person) constructor.newInstance(); //等同于 new Person();

Constructor<?> constructor = aClass.getConstructor(String.class, Integer.class);
Object instance = constructor.newInstance("li", 18); //new Person("li", 18);
```

> 属性

```java
//public static String city;
Field city = aClass.getField("city"); //static -> 不依赖对象，传参 null
city.set(null, "Beijing");
System.out.println(city.get(null));

//public Boolean gender;
Field gender = aClass.getField("gender"); //non static --> 依附于对象 p1
Object p1 = aClass.newInstance();
gender.set(p1, true);
System.out.println(gender.get(p1));

//private Boolean young;
Field young = aClass.getDeclaredField("young"); //private --> 依附于对象，并暴力访问
Object p2 = aClass.newInstance();
young.setAccessible(true); //暴力膜
young.set(p2, true);
System.out.println(young.get(p2));
```

> 方法

```java
//public static String staticHello() {}
Method staticHello = aClass.getMethod("staticHello");
Object invoke = staticHello.invoke(null); //invoke为返回值; 调用 --> 不依赖对象

//public String publicHello(String name, Integer age) {}
Method privateHello1 = aClass.getMethod("publicHello", String.class, Integer.class);
Object p3 = aClass.newInstance();
invoke = privateHello1.invoke(p3, "li", 20); //依赖对象

//private String privateHello() {}
Method privateHello = aClass.getDeclaredMethod("privateHello");
privateHello.setAccessible(true);
Object p0 = aClass.newInstance();
Object invoke = privateHello.invoke(p0); //依赖对象 + 暴力膜
```

> main方法怎样传递参数？

```shell
把一个字符串数组作为参数传递到 invoke()，jvm怎么解析？？

按照jdk1.5，整个数组是一个参数； jdk1.4数组中的每一个元素是一个参数。
jdk1.5肯定没问题，但对于jdk1.4则会将字符串数组打散成一个个字符串作为参数，就会出现参数个数异常。
```

```shell
#正确做法
（1）将字符串数组转换成 Object 对象
（2）将字符串数组作为 Object 数组的一个元素
```

```java
Class<?> clazz = this.getClass().getClassLoader().loadClass("com.example.reflect.Person");
Method helloArray = clazz.getMethod("main", String[].class); //参数类型: String[].class

helloArray.invoke(null, (Object) new String[]{"aaa", "bbb"}); //正确1
// helloArray.invoke(null, new Object[]{new String[]{"aaa", "bbb"}}); //正确2

// helloArray.invoke(null, new String[]{"aaa", "bbb"}); //错误写法
```



# IO流

## IO

> 文件拷贝：字节流 + 字符流

```sh
按流向        ：输入流，输出流。
按操作数据     ：字节流 （如音频，图片等），字符流（如文本）。

字节流的'抽象基类'：InputStream，OutputStream。字符流的'抽象基类'：Reader，Writer。
```

```java
try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
     BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dest))) {
    int len;
    byte[] buf = new byte[1024 * 4]; //字节流
    while (-1 != (len = bis.read(buf))) {
        bos.write(buf, 0, len);
    }
} catch (IOException e) {
    System.out.println("系统找不到指定的文件：" + src);
}
```

```java
try (BufferedReader br = new BufferedReader(new FileReader(src));
     BufferedWriter bw = new BufferedWriter(new FileWriter(dest))) {
    String line;
    while (null != (line = br.readLine())) { ///如果已到达流末尾，则返回 null
        bw.write(line);
        bw.newLine(); //由于 readLine()方法不返回行的终止符，所以手动写入一个行分隔符

        bw.flush(); //只要用到缓冲区技术，就一定要调用 flush() 方法刷新该流中的缓冲
    }
} catch (IOException e) {
    System.out.println("系统找不到指定的文件：" + src);
}
```

> 转换流：字节流 ---> 字符流

```java
BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
```

> 字符编码

```sh
GBK   ：占用两个字节，比 GB2312 编码多了很多汉字，如"镕"字。
UTF-8 ：Unicode一种具体的编码实现。是一种变长编码方式，使用 1-4 个字节进行编码，有利于节约网络流量。
```

```sh
#UTF-8编码规则
① 对于单字节的符号，字节的第一位设为0，后面7位为这个符号的unicode码。因此对于英语字母，UTF-8编码和ASCII码是相同的。
② 对于n字节的符号，第一个字节的前n位都设为1，第n+1位设为0，后面字节的前两位一律设为10。剩下的没有提及的二进制位，全部为这个符号的unicode码。

假如有个字符占用3个字节，则：第一个字节以 1110 开始，第二三个字节以 10 开始。
```

```java
byte[] bytes = "联通".getBytes("GBK");
for (byte aByte : bytes) {
    // 11000001 10101010 11001101 10101000 --> 两个汉字，4个字节
    System.out.println(Integer.toBinaryString(aByte & 255));
}
```

## File

> 获取文件路径：`getCanonicalPath()`

```java
//返回定义时的路径，可能是相对路径，也可能是绝对路径，这个取决于定义时用的是相对路径还是绝对路径。
//如果定义时用的是绝对路径，那么结果跟getAbsolutePath()一样
file.getPath(); // ..\test1.txt
```

```java
//返回的是定义时的路径对应的相对路径，但不会处理"."和".."的情况
file.getAbsolutePath(); // F:\sp_project\spring\..\test1.txt
```

```java
//返回的是规范化的绝对路径，相当于将getAbsolutePath()中的"."和".."解析成对应的正确的路径
file.getCanonicalPath(); // F:\sp_project\test1.txt
```

> 常用方法

```java
boolean Mkdir();    //用于创建单层目录
boolean Mkdirs();   //.......多.....

boolean renameTo(); //重命名
```

##Properties

> `class Properties extends Hashtable` 线程安全

```java
String filePath = "application.properties";
Properties properties = new Properties();
InputStream in = getClass().getClassLoader().getResourceAsStream(filePath);
if (null == in) {
    System.out.println("配置文件不存在：" + filePath);
} else {
    try {
        properties.load(in);
        String property = properties.getProperty("server.port", "8080"); //arg2: 默认值
        System.out.println("读取配置：" + property);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

## Convert

> int，byte[] `大端模式：高位在前，低位在后。【常用模式】`

```java
public static byte[] int2Bytes(int value, int len) {
    if (len > 4 || len <= 0) throw new RuntimeException("int 最大长度4个字节");

    byte[] bytes = new byte[len];
    for (int i = 0; i < len; i++) {
        bytes[i] = (byte) ((value >> 8 * (len - 1 - i)) & 0xFF);
    }
    return bytes;
}
```

```java
public static int bytes2Int(byte[] bytes) {
    byte[] dest = new byte[4];
    System.arraycopy(bytes, 0, dest, 4 - bytes.length, bytes.length);
    return (dest[0] & 0xFF) << 24
            | ((dest[1] & 0xFF) << 16)
            | ((dest[2] & 0xFF) << 8)
            | (dest[3] & 0xFF << 0);
}

```

> int，byte[] `小端模式：低位在前，高位在后`

```java
public static byte[] int2Bytes(int value, int len) {
    if (len > 4 || len <= 0) throw new RuntimeException("int 最大长度4个字节");
    
    byte[] bytes = new byte[len];
    for (int i = 0; i < len; i++) {
        bytes[i] = (byte) ((value >> 8 * i) & 0xFF);
    }
    return bytes;
}

```

```java
//offset: 从数组的第offset位开始
public static int bytes2Int(byte[] bytes, int offset) {
    return (bytes[offset + 0] & 0xFF)
            | ((bytes[offset + 1] & 0xFF) << 8)
            | ((bytes[offset + 2] & 0xFF) << 16)
            | ((bytes[offset + 3] & 0xFF) << 24);
}

```

> int，Hex

```java
public static String int2Hex(int value) {
    return Integer.toHexString(value);
}
```

```java
private static int hex2Int(String hexString) {
    return Integer.parseInt(hexString, 16);
}
```

> String，Hex

```java
public static String string2Hex(String value) {
    StringBuilder hexString = new StringBuilder();
    for (char aChar : value.toCharArray()) {
        hexString.append(Integer.toHexString(aChar));
    }
    return hexString.toString();
}
```

> String，byte[]

```java
byte[] bytes = "hello".getBytes(Charset.forName("utf-8"));
```

```java
String s = new String(bytes, Charset.forName("utf-8"));
```

> 校验和

```java
//第13位 -> 校验和 -> 前面所有字节的异或
data[13] = data[0];
for (int i = 1; i < 13; i++) {
    data[13] = (byte) (data[13] ^ data[i]);
}
```


# Socket

> 基础概念

```sh
Socket 就是为网络服务提供的一种机制，网络通信其实就是 Socket 间的通信。通信两端都是 Socket，数据在两个 Socket 间通过 IO 传输。
```

> 域名解析过程

```sh
在浏览器地址栏中输入'https://www.baidu.com'后，系统会首先自动从本地 hosts 文件中寻找对应的IP地址。
一旦找到，系统会立即打开对应网页，如果没有找到，则系统会再将网址提交DNS域名解析服务器进行IP地址的解析。

#本地Hosts文件所在位置：C:\Windows\System32\drivers\etc
配置本地Hosts文件，一方面可以加快网站的访问速度（配置网站对应的正确IP），另一方面可以阻止某些流氓软件的网络请求（配置网站的IP为127.0.0.1）
```

> 网络模型

```sh
OSI七层模型        TCP/IP四层模型
------------------------------------
应用层
表示层            应用层 ---> http/https FTP
会话层
-------------------------------------
传输层            传输层 ----> TCP/UDP
-------------------------------------
网络层            网际层 ----> IP协议
--------------------------------------
数据链路层
物理层            主机至网络层
--------------------------------------
```

> 相关API

```java
// InetAddress inet = InetAddress.getLocalHost(); //本机
InetAddress inet = InetAddress.getByName("192.168.8.8"); //指定ip

String name = inet.getHostName(); //主机名
String ip = inet.getHostAddress(); //IP字符串
```

##UDP

> UDP & TCP

```sh
UDP：面向无连接。数据包一次传输最大64K。不可靠，容易丢包，但是速度快。      #例子：发短信
TCP：需要先通过3次握手建立链接，所以是可靠协议，但效率稍低。传输数据量无限制。#例子：打电话
```

> DEMO

```java
public static void main(String[] args) throws Exception {
    ExecutorService pool = Executors.newCachedThreadPool();

    DatagramSocket ds = new DatagramSocket(7001); //数据包对象 DatagramSocket
    pool.execute(() -> sendMsg(ds, "192.168.8.7", 8001, "hello", "客户端-发送："));
    pool.execute(() -> recvMsg(ds, "客户端-接收："));

    DatagramSocket ds1 = new DatagramSocket(8001);
    pool.execute(() -> sendMsg(ds1, "192.168.8.7", 7001, "world", "服务端-发送："));
    pool.execute(() -> recvMsg(ds1, "服务端-接收："));
}
```

```java
// 发送消息
private static void sendMsg(DatagramSocket ds, String ip, int port, String msg, String mark) {
    byte[] buf = msg.getBytes();
    try {
        //发送数据包，数据包内容：数据的字节数组，目标ip，目标端口号
        ds.send(new DatagramPacket(buf, buf.length, InetAddress.getByName(ip), port));

        System.out.println(mark + msg);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

```java
// 接收消息
private static void recvMsg(DatagramSocket ds, String mark) {
    while (true) {
        try {
            byte[] buf = new byte[4 * 1024];
            DatagramPacket dp = new DatagramPacket(buf, buf.length);
            ds.receive(dp); //接收数据包

            String host = dp.getAddress().getHostName(); //解析数据包
            int port = dp.getPort();
            String msg = new String(dp.getData(), 0, dp.getLength());

            System.out.println(mark + host + ":" + port + " - " + msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## TCP

> 三次握手

```sh
A --> B: B在吗?           #发送SYN-A同步包
B --> A: A在，B收到了吗？   #解析，知道是A请求建立链接，发送ACK-A确认包 + SYN-B同步包
A --> B: B收到了,开始传输!  #解析，知道B同意建立连接，并发送ACK-B同意建立连接
```

> 服务端

```java
public static void main(String[] args) {
    ExecutorService pool = Executors.newCachedThreadPool();
    try (ServerSocket server = new ServerSocket(8100)) {

        while (true) {
            Socket socket = server.accept(); //阻塞方法，接受客户端请求
            pool.execute(() -> recvMsg(socket)); //一个客户端一个线程去处理
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

private static void recvMsg(Socket socket) {
    try (InputStream in = socket.getInputStream();
         OutputStream out = socket.getOutputStream()) {

        List<String> lines = IOUtils.readLines(in, "UTF-8"); //读取流中的每一行
        String join = StringUtils.join(lines, ""); //多行合并为一行
        System.out.println("S<-C：" + join);

        String recv = LocalDateTime.now().toString();
        IOUtils.write(recv, out, "UTF-8");
        System.out.println("S->C：" + recv);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

> 客户端

```java
public static void main(String[] args) {
    int nThreads = 2;
    ExecutorService pool = Executors.newFixedThreadPool(nThreads);

    for (int i = 0; i < nThreads; i++) {
        int index = i;
        pool.execute(() -> sendMsg(index));
    }
}

private static void sendMsg(int index) {

    try (Socket socket = new Socket("127.0.0.1", 8100);
         InputStream in = socket.getInputStream();
         OutputStream out = socket.getOutputStream()) {

        String send = index + "";
        IOUtils.write(send, out, "UTF-8");
        System.out.println("C->S：" + send);

        List<String> lines = IOUtils.readLines(in, "UTF-8");
        String join = StringUtils.join(lines, "");
        System.out.println("C<-S：" + join);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```



