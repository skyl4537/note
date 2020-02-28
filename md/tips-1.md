[TOC]



# 基础部分

## 开发宝典



## 常见概念

> == & equals()

```sh
`==`：既可用于基础数据类型，也可用于引用类型。
对于基础数据类型，比较其值是否相等。对于引用类型，比较两个引用的内存地址是否相同，即是否是同一个对象。

`equas()` 只能用于引用数据类型。默认调用 ==，即比较内存地址。可根据业务覆写此方法，用来判断两对象的内容是否相同
```

```java

```


#常用の类

##异常

> ### Throwable

```shell
#Throwable 有两个子类：Error 和 Exception
Error    ：程序无法处理的系统错误，一般指与虚拟机相关的问题，建议程序终止。常见：'内存溢出，深递归导致栈溢出等'。
Exception：程序可以处理的异常，应该捕获并进行相应的处理。
```
> Exception

```sh
#CheckedException：编译期异常。编译时期就会检查，不处理则编译不通过
#不是具体的java类，是指 RuntimeException 以外的异常，类型上属于Exception类及其子类
IOException；SQLException；InterruptedException；ParseException（日期解析异常），ClassNotFoundException（反射-类不存在时异常）
```

```sh
#运行时异常（RuntimeException）：在运行时期，检查异常。在编译时期，不处理也不会报错。
空指针; 除数为0; 数组越界; 字符串解析成数字; 类转换异常（ClassCastException）; 非法参数异常（IllegalArgumentException）

new Timer().schedule(timerTask, new Date(), 0); #非法参数异常，定时任务频率不能为 0
Arrays.asList("a", "b").add("c");               #java.lang.UnsupportedOperationException
list.forEach(x -> {if("b".equals(x)){ list.add("c"); }}); #并发修改异常（ConcurrentModificationException）
```
>异常处理

```sh
#使用关键字 throws 引发异常。异常处理的两种方式
throw    ：异常不处理，直接抛出
try-catch：捕获异常，进行针对性的处理。#try-catch 比 if 更耗性能，所以不要使用 try-catch 作为逻辑处理手段。

throw  ：用在'方法体内'，用来明确地抛出一个异常
throws ：用在'方法声明'，用来标明一个方法可能抛出的各种异常
finally：存放的代码无论是否发生异常，都会执行，常用于释放资源。如释放IO资源，数据库连接，网络连接等。#如果有 return 语句，肯定返回此结果。
```
>### 练习题

>多异常处理时，先捕获小异常，最后捕获大异常 Exception

```java
//父类方法抛出异常，子类方法只能抛出相同异常或子异常
//父类方法没有抛出异常，子类方法也不能抛出异常，只能自己处理（如 run()）
private void catchException() {
    try {
        int i = 1 / 0;
        System.out.println("结果为: " + i);       //不执行。抛出异常，直接跳出
    } catch (ArithmeticException e) {
        System.out.println("捕获异常：被除数为0");  //会执行。捕获异常的'就近原则'
    } catch (RuntimeException e) {
        System.out.println("捕获异常：捕获大异常"); //不执行。异常只会被捕获一次
    }
}
```

> try 中的 return 在 finally 之前还是之后执行？

```java
public int finallyTest() {
    int num = 3;
    try {
        return num;
    } finally {
        System.out.println("执行 finally"); //会打印，说明 try 中return，在 finally 之后执行
        num = 5;
        // return num; //注掉此行，返回 3. 放开此行，返回 5
    }
}
```



##String

> ### Final

```sh
#字符串对象一旦创建（保存在字符串常量池中），就无法修改
String类的所有方法都没有改变字符串本身的值，而是返回了一个新的 String对象

#String对象不可变（√） 与 String对象的引用变量不可变（X）

final类不一定线程安全，如: StringBuilder。
常见final类：LocalDateTime，StringBuffer，StringBuilder（非线程安全），Integer等
```

```sh
#String不可变特性的应用
'高效性' ：不可变性能保证其 hashCode 永远保持一致，不需要重新计算。这就使得字符串很适合作为 Map 中的 Key，字符串的处理速度要快过其它的键对象。
'安全性' ：String被广泛的使用在其他Java类中充当参数。比如网络连接、打开文件等操作。如果字符串可变，那么类似操作可能导致安全问题。
'线程安全'：因为不可变对象不能被改变，所以他们可以自由地在多个线程之间共享。不需要任何同步处理。
```

> 字符串常量池

```sh
#Java为了避免产生大量的String对象，设计了一个字符串常量池
工作原理：创建一个字符串时，JVM首先会检查字符串常量池中是否有'值相等'的字符串，如果有，则不再创建，直接返回该字符串的引用地址；
如果没有，则创建，然后放到字符串常量池中，并返回新创建的字符串的引用地址。

当遇到 'new String("Hollis");' 时，还会在堆内存上创建一个新的String对象，存储'hollis'，并将内存上的引用地址返回。
```

```sh
字符串常量池中的对象是'在编译期确定'，在类被加载时创建。如果类加载时，该字符串常量在常量池中已存在，那就跳过，不会重新创建一个。
与之相反，堆中的对象是'在运行期才确定'，在代码执行到 new 的时候创建的。
```

```java
String s1 = "abc";
String s2 = "ab" + "c";
//字符串常量池中的对象是在【编译期】确定，在类被加载时创建。所以，对于字符串常量相加表达式，在'编译期'直接处理成 s2 = "abc".
System.out.println(s1 == s2); //true

String s3 = "ab";
String s4 = s3 + "c";
//对于 s4 是在【运行期】使用 new StringBuilder().append(s3).append("c").toString(); 拼接生成新的对象，存在于堆内存.
System.out.println(s1 == s4); //false
```

> StringBuilder

```sh
String          #线程安全     直接进行字符串拼接，会产生大量中间字符串，并且时间消耗长
StringBuffer    #线程安全     支持同步锁，性能稍差
StringBuilder   #线程不安全   单线程进行大量字符串操作时，推荐使用（√）
```

```sh
#避免在【循环体】中声明【创建对象】，也不要使用 + 进行字符串【拼接】
每次循环，都需要创建一个 StringBuilder 对象（创建对象需要耗费时间和内存）。
随着循环次数的增大，res字符串也就越长，把res中的字符复制到新建的 StringBuilder 中花费的时间也就越长。
而且，StringBuilder(res).append(str).toString(); 会创建一个临时的字符串，随着循环次数的增加，这个操作花费的时间也会越来越长。
总之，随着循环变量 i 的增大，每次循环会变得越来越慢。
```

> ### intern()

```sh

```



> ### 正则表达式

```sh
符合一定规则的表达式，专门用于操作字符串。#学习正则表达式，就是学习一些特殊符号的使用
```

> 预定义字符

```sh
.     任何字符（行结束符 可能匹配、也可能不匹配）
\d    数字：[0-9]
\s    空白字符：[ \t\n\x0B\f\r]
\w    单词字符（字母、数字、下划线）：[a-zA-Z_0-9]
\b	  匹配单词的开始或结束

\D    非数字：[^0-9]
\S    非空白字符：[^\s]
\W    匹配任意不是字母，数字，下划线，汉字的字符：[^\w]
\B    匹配不是单词开头或结束的位置

[f-k]    匹配 f-k 之间的任意一个字母
[^aei]   匹配除了 a e i 这几个字母以外的任意字符
[\d.\-+] 匹配数字，小数点，+，-
```

```sh
^	匹配输入字符串的'开始位置'。在方括号表达式中使用，表示不接受该字符集合
$	匹配输入字符串的'结尾位置'。如果设置了 RegExp 对象的 Multiline 属性，则 $ 也匹配 '\n' 或 '\r'

*	匹配前面的子表达式'0次或多次'
+	匹配前面的子表达式'1次或多次'
?	匹配前面的子表达式'0次或1次'，或指明一个非贪婪限定符

( )	标记一个子表达式的开始和结束位置。子表达式可以获取供以后使用
[	标记一个中括号表达式的开始，表示一个范围
{	标记'限定符表达式'的开始
|	指明两项之间的一个选择。要匹配 |，请使用 \|
\	转义符。例如，序列 '\\' 匹配"\"，而'\(' 则匹配 "("
```

> 限定符

```sh
*	0次或多次，等价于 {0,}。例如，zo* 能匹配 "z" 以及 "zoo"
+	1次或多次，等价于 {1,}。例如，'zo+' 能匹配 "zo" 以及 "zoo"，但不能匹配 "z"
?	0次或1次，等价于 {0,1}。例如，"do(es)?" 可以匹配 "do"；"does" 中的 "does"；"doxy" 中的 "do"
```

```sh
{n}	  匹配确定的 n 次。例如，'o{2}' 不能匹配 "Bob" 中的 o，但是能匹配 "food" 中的两个 o
{n,}  至少匹配 n 次。例如，'o{2,}' 不能匹配 "Bob" 中的 o，但能匹配 "foooood" 中的所有 o

{n,m} 最少匹配n次且最多匹配m次。例如，"o{1,3}" 将匹配 "fooooood" 中的前三个 o
#'o{0,1}' 等价于 'o?'。请注意在逗号和两个数之间不能有空格。
```
> ### 练习题

> 保留两位小数

```java
//round()返回与参数最接近的长整数，参数加1/2后求其 floor().
double val0 = (Math.round(1.124 * 100)) / 100.0; //1.124 -> 1.12 1.1 -> 1.1
double val1 = (Math.round(1.125 * 100)) / 100.0; //1.125 -> 1.13 1.1 -> 1.1
```

```java
//%.2f：格式化目标数值，生成两位小数（四舍五入）的字符串
String val0 = String.format("%.2f", 1.124); //1.124 -> 1.12   1.1 -> 1.10 
String val1 = String.format("%.2f", 1.125); //1.124 -> 1.13   1.1 -> 1.10
```
> 格式化

```java
String format = MessageFormat.format("{0}和{1}", "我", "你"); //我和你
format = String.format("%s和%s", "我", "你"); //我和你
```

> String替换

```java
replaceAll(String regex, String replacement);   //参数是 regex，即基于正则表达式的替换
replaceFirst(String regex, String replacement); //参数也是 regex，但不同的是只替换第一个，即基于正则替换第一个满足条件的

replace(char|CharSequence target, char|CharSequence replacement); //参数不同，即只支持【字符】和【字符串】的替换
```

> 正则练习

```java
public void splitStringTest() {
    String str = "我 和 你!";
    String regex = "[\\u4E00-\\u9FBF]+"; //汉字正则

    boolean matches = str.matches(regex);    //完全匹配. 全是汉字？ false
    Matcher matcher = Pattern.compile(regex).matcher(str);
    while (matcher.find()) {                 //是否包含. 包含汉字？ true
        System.out.println(matcher.group()); //找出包含的汉字
    }
    String[] split = str.split("\\s"); //正则切割
    String replaceAll = str.replaceAll("\\s", ""); //正则替换
}
```









# 集合部分

## XX

> 输出每个数出现的频率（正负数算一个），并按频率进行排序

```java
@Test
public void countTest() {
    int[] nums = {5, 0, -5, 2, -4, 5, 10, 3, -5, 2, -4, 3, 4, 9, 1};
    HashMap<Integer, Integer> countMap = new HashMap<>();
    Arrays.stream(nums).boxed().map(Math::abs).forEach(x -> countMap.merge(x, 1, (a, b) -> a + b));
    System.out.println("频率统计: " + countMap);

    List<Map.Entry<Integer, Integer>> collect = countMap.entrySet().stream()
        .sorted(Comparator.comparing(Map.Entry::getValue)) //排序：升序
        .collect(Collectors.toList());
    System.out.println("排序后结果: " + collect);
}
```



# 高级部分

## IO&编码

> 文件拷贝：字节流 & 字符流

```sh
按数据流向：输入流，输出流
按操作数据：字节流 （如音频，图片等 - 'InputStream,OutputStream'），字符流（如文本 - 'Reader,Writer'）
```
> 常见编码格式

```sh
'ASCII'  : 一共128个代码，包括26个小写字母、26个大写字母、10个数字、32个符号、33个控制代码和1个空格
'ANSI'   : 使用两个字节(2^16)。本身是对 ASCII 码的拓展，前128个与 ASCII 码相同，之后的字符全是某个国家语言的所有字符。
中国有中国的ANSI，日本有日本的ANSI，各国有各国的标准，并且不能互相转换，这就会导致在多语言混合的文本中会有乱码。

'GB2312' : 中文对 ASCII 的扩编，可以表示6000多个常用汉字
'GBK'    : 汉字实在是太多了，包括繁体和各种字符，于是产生了 GBK 编码，它包括了 GB2312 中的编码，同时扩充了很多
'GB18030': 中国是个多民族国家，各个民族几乎都有自己独立的语言系统，为了表示那些字符，继续把 GBK 编码扩充为 GB18030 编码

'UNICODE': 每个国家都像中国一样，把自己的语言编码，于是出现了各种各样的编码，如果你不安装相应的编码，就无法解释相应编码想表达的内容。
终于，有个叫 ISO 的组织看不下去了。他们一起创造了一种编码 UNICODE ，这种编码非常大，大到可以容纳世界上任何一个文字和标志。
所以只要电脑上有 UNICODE 这种编码系统，无论是全球哪种文字，只需要保存文件的时候，保存成 UNICODE 编码就可以被其他电脑正常解释。
UNICODE 在网络传输中，出现了两个标准 UTF-8 和 UTF-16，分别每次传输 8个位和 16个位。

'UTF-8' : 一种变长的编码方式: 使用 1~4 个字节表示一个符号，根据不同的符号而变化字节长度。当字符在 ASCII 码的范围时，就用1个字节表示。
值得注意的是: UNICODE 编码中一个中文字符占2个字节，而 UTF-8 一个中文字符占3个字节。
```

> 记事本鄙视【联通】

```sh
#Windows自带的记事本，默认编码格式为 ANSI
如果在 ANSI 的编码环境输入汉字，那么实际就是 GB 系列的编码方式保存，"联通"的内码是：
11000001 10101010 11001101 10101000 #两个汉字，4个字节
也符合 UTF-8 的编码规则。所以再次打开记事本，记事本误以为是 UTF-8 编码的文件，以 UTF-8 解码就乱码
```



## Socket

> #### TCP UDP

```sh
UDP：面向无连接，不可靠，容易丢包，但速度快。数据包一次传输最大64K。      #例子：发短信
TCP：需要通过3次握手建立链接，所以是可靠协议，但速度稍慢。传输数据量无限制。#例子：打电话
```

> 三次握手

```sh
A --> B: B在吗?           #发送SYN-A同步包
B --> A: A在，B收到了吗？   #解析，知道是A请求建立链接，发送ACK-A确认包 + SYN-B同步包
A --> B: B收到了,开始传输!  #解析，知道B同意建立连接，并发送ACK-B同意建立连接
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
> DEMO：UDP

```java
//发送数据
DatagramSocket ds = new DatagramSocket(7001);
byte[] buf = "hello world!".getBytes();
ds.send(new DatagramPacket(buf, buf.length, InetAddress.getByName("192.168.8.7"), 8001));

//接收数据
DatagramSocket ds1 = new DatagramSocket(8001);
byte[] buf = new byte[4 * 1024];
DatagramPacket dp = new DatagramPacket(buf, buf.length);
ds.receive(dp);
String host = dp.getAddress().getHostName(); //解析接受内容
int port = dp.getPort();
String msg = new String(dp.getData(), 0, dp.getLength());
```

> DEMO：TCP

```java
public class SocketServerTest {
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
            System.out.println("Server <- Client：" + join);

            String recv = LocalTime.now().toString();
            IOUtils.write(recv, out, "UTF-8");
            System.out.println("Server -> Client：" + recv);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

```java
public class SocketClientTest {
    public static void main(String[] args) throws InterruptedException {
        int nThreads = 2;
        ExecutorService pool = Executors.newFixedThreadPool(nThreads);
        for (int i = 0; i < nThreads; i++) {
            int index = i;
            pool.execute(() -> sendMsg(index));
        }
        // 等待上述的线程执行完，再关闭线程池。二者配合使用
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
    }

    private static void sendMsg(int index) {
        try (Socket socket = new Socket("127.0.0.1", 8100);
             InputStream in = socket.getInputStream();
             OutputStream out = socket.getOutputStream()) {

            String send = "msg-" + index;
            IOUtils.write(send, out, "UTF-8");
            System.out.println("Client -> Server：" + send);

            // List<String> lines = IOUtils.readLines(in, "UTF-8");
            // String join = StringUtils.join(lines, "");
            // System.out.println("Client <- Server：" + join);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

##Reflect

> 创建对象 の 3种方式

```java
Person person = new Person(); // 传统方式，必须预先知道要使用的类。引用类改变，就必须修改源码

public class Person implements Cloneable{} // 实现克隆接口. 不推荐
Person clone = (Person) person.clone();

Class<?> clazz = Class.forName("com.example.reflect.Person"); // 反射创建，效率相对低下，耗时是传统方式的 3 倍
Object instance = clazz.newInstance(); // 必须有无参构造，否则报错!!!
```

> 常用方法

```java
// public static String staticHello() {}
Method staticHello = aClass.getMethod("staticHello");
Object invoke = staticHello.invoke(null); // invoke为返回值; static方法 --> 不依赖对象

// public String publicHello(String name, Integer age) {}
Method privateHello1 = aClass.getMethod("publicHello", String.class, Integer.class);
Object p3 = aClass.newInstance();
invoke = privateHello1.invoke(p3, "li", 20); // 实例方法 --> 依赖对象

// private String privateHello() {}
Method privateHello = aClass.getDeclaredMethod("privateHello");
privateHello.setAccessible(true);
Object p0 = aClass.newInstance();
Object invoke = privateHello.invoke(p0); // 依赖对象 + 暴力膜
```
> main方法怎样传递参数？

```sh
#把一个字符串数组作为参数传递到 invoke()，jvm 怎么解析？？
按照 jdk5，整个数组是一个参数； jdk4 数组中的每一个元素是一个参数。
jdk5 肯定没问题，但对于 jdk4 则会将字符串数组打散成一个个字符串作为参数，就会出现参数个数异常。

#正确做法
(1).将字符串数组转换成 Object 对象
(2).将字符串数组作为 Object 数组的一个元素
```

```java
Class<?> clazz = this.getClass().getClassLoader().loadClass("com.example.reflect.Person");
Method helloArray = clazz.getMethod("main", String[].class); //参数类型: String[].class

helloArray.invoke(null, (Object) new String[]{"aaa", "bbb"}); //正确1
// helloArray.invoke(null, new Object[]{new String[]{"aaa", "bbb"}}); //正确2

// helloArray.invoke(null, new String[]{"aaa", "bbb"}); //错误写法
```

