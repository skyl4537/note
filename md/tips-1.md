#常用类

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



#高级部分

##IO&编码

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



##Socket

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





# JAVA8

## Optional

>只用于返回类型，而不是参数，也不是字段

```sh
#一个容器类，代表一个值存在或不存在。
原来用 null 表示一个值不存在，现在 Optional 可以更好的表达这个概念，并且可以避免空指针异常
```

> 优雅判 NULL

```java
public String getDogName(Person person) throws IllegalArgumentException {
    if (null != person) {
        Person.Dog dog = person.getDog();
        if (null != dog) {
            return dog.getName();
        }
    }
    throw new IllegalArgumentException("param isn't available.");
}
```

```java
public String getDogName(Person person) throws IllegalArgumentException {
    return Optional.ofNullable(person)
        .map(person -> person.getDog())
        .map(dog -> dog.getName())
        // .orElse("Unknown") //以上都为 null，则设置输出默认值 或 直接抛出异常
        .orElseThrow(() -> new IllegalArgumentException("param isn't available."));
}
```
## 时间API

> 问题：Date，Calendar，SimpleDateFormat 都是`线程不安全`

```java
// 把 SimpleDateFormat 实例定义为静态变量，在多线程情况下会被多个线程共享，容易出现问题
private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

public void parse() throws InterruptedException {
    ExecutorService pool = Executors.newFixedThreadPool(25);
    for (int i = 0; i < 20; i++) {
        //在多并发情况下，format() 和 parse() 都是线程不安全的
        pool.execute(() -> System.out.println(sdf.parse("2019-04-15 09:45:59")));
    }
}
```

> 问题原因

```sh
# calendar 是共享变量，并且这个共享变量没有做线程安全控制
当多个线程同时使用相同的 static SimpleDateFormat.format()时，多个线程会同时调用 calendar.setTime()方法，
可能一个线程刚设置好 time 值，另外的一个线程马上把设置的 time 值给修改了，导致返回的格式化时间可能是错误的。
```
```java
protected Calendar calendar;

private StringBuffer format(Date date, StringBuffer toAppendTo, FieldDelegate delegate) {
    calendar.setTime(date); // 这里使用的 calendar 是类成员，多线程调用 setTime()会有线程安全问题
    //... ...
}
```
> 3 种解决方案

```java
//(1).只在需要的时候创建实例，不用static修饰。【缺点】加重了创建对象的负担，会频繁地创建和销毁对象，效率较低
public static String format(Date date) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return sdf.format(date);
}

//(2).synchronized 大法好。【缺点】并发量大的时候，会对性能有影响，线程阻塞
public static String format(Date date) throws ParseException {
    synchronized (sdf) {
        return sdf.format(date);
    }
}

//(3).ThreadLocal。确保每个线程单独一个SimpleDateFormat对象
private static ThreadLocal<DateFormat> threadLocal = 
    ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

public static String format(Date date) {
    return threadLocal.get().format(date);
}
```

> ### 推荐方案

```java
//(4).使用 JDK8 的 DateTimeFormatter
private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

public static String format(LocalDateTime date) {
    return formatter.format(date);
}

public static LocalDateTime parse(String dateStr) {
    return LocalDateTime.parse(dateStr, formatter);
}
```

> 时间间隔

```java
@Test
public void periodTest() {
    LocalDate start = LocalDate.now(); //2019-12-10
    LocalDate end = LocalDate.of(2020, 12, 20);

    // ChronoUnit：两个时间点间隔的 具体天数、月数、甚至秒数
    long between = ChronoUnit.DAYS.between(start, end); //相隔 376 天

    // Period：两个时间点相隔的 X年X月X日
    Period period = Period.between(start, end); //P1Y10D

    // Duration：两个时间点相隔的 X时X分X秒（只适用 LocalDateTime 类型）
    Duration duration = Duration.between(start, end); //PT14735H53M57.538S
}
```

> 新旧接口转换

```java
Date date = Date.from(Instant.now()); // 旧的 = 旧的.from(新的)
Instant instant = date.toInstant();   // 新的 = 旧的.to()
```

> 其他用法

```java
@Test
public void localDateTest() {
    LocalDate now = LocalDate.now();
    int year = now.getYear();                      //当前年份: 2019
    int dayOfYear = now.getDayOfYear();            //今天是今年的第 344 天
    int weekValue = now.getDayOfWeek().getValue(); //今天星期 2.(星期从1开始)

    LocalDate localDate0 = now.plusDays(5);        //5天以后日期: 2019-12-15
    LocalDate localDate1 = now.withYear(1990);     //1990年的今天: 1990-12-10
    LocalDate localDate2 = now.withDayOfYear(2);   //今年的第2天是: 2019-01-02

    long until = now.until(localDate0, ChronoUnit.DAYS); //两日期相差天数: localDate0-now=5
    boolean after = localDate0.isAfter(now);             //localDate0 是否在 now 之后？ true
    boolean leapYear = now.isLeapYear();                 //2019是否为闰年？ false
}
```

## lambda

> 匿名内部类的`语法糖`？

```sh
'语法糖'：指使用更加方便，但是原理不变的代码语法。如，增强foreach，底层的实现仍然是迭代器
从应用层面来讲，java 中的 lambda 可以被当做是匿名内部类的 "语法糖"。但是，`二者在原理上是不同的`
```

```sh
'函数式接口'：有且仅有一个抽象方法的接口
可以在任意接口上使用注解 @FunctionalInterface，来检查是否是函数式接口（同 @overwrite）
```

## Stream

>集合讲的是`存储`，Stream 讲的是`操作`

```sh
#Stream 是数据管道，用于操作数据源（集合，数组等），产生新的元素集合
Stream 其实是一个集合元素的函数模型，它并不是集合，也不是数据结构，其本身并不存储任何元素（或其地址值）。
Stream 不会改变源对象。相反，它会返回一个持有结果集的新 Stream。
Stream 操作是延迟执行的。意味着它会等到需要结果时才执行。#详见Demo
```

```sh
#可以把 Stream 当成一个高级版本的 Iterator
原始版本的 Iterator，用户只能一个一个的遍历元素并对其执行某些操作；
高级版本的 Stream，用户只要给出需要对其包含的元素执行什么操作，比如 "过滤掉长度大于10的字符串"、"获取每个字符串的首字母"等，
Stream 会隐式地在内部进行遍历，做出相应的数据转换。
```

> `创建流`（转化数据源）   -->   中间操作（定义中间操作链，但不会立即执行） -->   终止操作（执行中间操作链，并产生结果）

```java
private static List<Person> PERSON_LIST;

static {
    Person p1 = new Person(1, "zhao", 17, 197.5, true); //id,name,age,height,gender
    Person p2 = new Person(2, "qian", 18, 187.5, true);
    Person p3 = new Person(3, "sui", 19, 177.5, true);
    Person p4 = new Person(4, "li", 20, 167.5, false);
    Person p5 = new Person(5, "wang", 21, 157.5, false);
    PERSON_LIST = new ArrayList<>(Arrays.asList(p1, p2, p3, p4, p5));
}
```

```java
Stream<Person> stream = list.stream();         //list
Stream<Person> stream = Arrays.stream(array);  //array
Stream<Person> stream = Stream.of(p1, p2, p3); //可变参数，也可传数组

Stream<Person> parallelStream = list.parallelStream(); //并行流

//创建无限流（迭代 + 生成）
Stream<Integer> stream0 = Stream.iterate(1, x -> x + 3).limit(10); //迭代（必须限制大小）
Stream<Double> stream1 = Stream.generate(Math::random).limit(10);  //生成
```

> 操作流程

```sh
'惰性求值': 中间操作不会立即执行，只有执行了终止操作（如forEach()），中间操作才会执行
'内部迭代': 迭代操作 forEach 是由 Stream-API 自动完成
'短路操作': 以下 age=21 不打印，体现了短路操作。
```

```java
PERSON_LIST.stream()
    .filter(x -> x.getAge() > 17) //过滤条件
    .limit(3)   //结果集大小
    .skip(1)    //跳过结果集中的前n个元素; 当n大于元素总数,返回空流.
    .distinct() //通过 hashCode() 和 equals() 去重
    .sorted(Comparator.comparing(Person::getAge)) //根据年龄排序
    .forEach(System.out::println);//终止操作 -> 最终结果集为 3-1 个

// 比较: 17 -> 不满足
// 比较: 18 -> 满足，但挑过
// 比较: 19 -> 满足，输出
// 比较: 20 -> 满足，输出
// ***: 21 -> 虽然满足，但是 limit(3) 已满足，则不再比较。这就体现了【短路操作】
```

```java
//map()参数为 ：函数型接口，有入有出
//peek()参数为：消费型接口，只有入参，没有出参。更多情况用于 debug 逻辑是否正确
PERSON_LIST.stream()
    .map(person -> {
        person.setAge(person.getAge() + 5);
        return person;
    })
    .forEach(System.out::println);

PERSON_LIST.stream()
    .peek(person -> person.setAge(person.getAge() + 5)) //逻辑等同，可以互相替换
    .forEach(System.out::println);
```

```java
//map() 和 flatmap() 类比于： list.add() 和 addAll()
List<String> list0 = new ArrayList<>(Arrays.asList("a", "b"));
List list1 = new ArrayList<>(Arrays.asList("11"));

list1.add(list0);    // list0 作为 list1 中的一个元素：[11, [a, b]]
list1.addAll(list0); // list0 中的元素融入 list1 中：  [11, a, b]
```

> 终止流

```java
String str = "my name is 007";

//forEach：并行流中，输出的顺序不一定（效率更高）
str.chars().parallel().forEach(x -> System.out.print((char) x)); //is 070 anemy m
//forEachOrdered：并行流中，输出的顺序与元素的顺序严格一致
str.chars().parallel().forEachOrdered(x -> System.out.print((char) x)); //my name is 007

//非并行流: forEach() == forEachOrdered() == parallel.forEachOrdered()
str.chars().forEach(x -> System.out.print((char) x)); //my name is 007
```

>Collect

```sh
(1).Collector 接口定义了如何对流执行收集操作（如收集到List，Set，Map等）
(2).Collectors 实用类提供了系统实现的收集器实例
(3).类似：Executor，Executors； Collection<E>，Collections
```

```java
// toList(); toSet(); toCollection();
List<String> nameList = PERSON_LIST.stream()
    .map(person -> person.getName()).collect(Collectors.toList());
```

```java
Long count = PERSON_LIST.stream() //.count() 等效
    .filter(person -> person.getAge() > 18).collect(Collectors.counting());
```

```java
//joining(): 连接流中每个字符串。参数列表：delimiter-连接符; prefix-结果的前缀; suffix-结果的后缀.
String nameList = PERSON_LIST.stream()
    .map(person -> person.getName()).collect(Collectors.joining(",", "{", "}"));
```

```java
//maxBy(); minBy();
Optional<Person> collect6 = PERSON_LIST.stream()
    // .max(Comparator.comparingDouble(Person::getHeight));
    .collect(Collectors.maxBy((x, y) -> Double.compare(x.getHeight(), y.getHeight())));
```

```java
//reducing() --> 参数列表 arg0: 初始值; arg1: 哪个属性; arg2: 求和操作
Optional<Double> collect7 = PERSON_LIST.stream()
    .map(Person::getHeight).collect(Collectors.reducing(Double::sum));

Double collect8 = PERSON_LIST.stream()
    .collect(Collectors.reducing(0.0, Person::getHeight, Double::sum));
```

```java
//collectingAndThen(); 包裹另一个收集器,对其结果转换函数
Integer collect9 = PERSON_LIST.stream()
    .collect(Collectors.collectingAndThen(Collectors.toList(), List::size));
```

```java
Integer ageSum = PERSON_LIST.stream()
    // .mapToInt(person -> person.getAge()).sum() 等效
    .collect(Collectors.summingInt(person -> person.getAge()));
```

```java
//summarizingInt(); summarizingDouble();
DoubleSummaryStatistics collect4 = PERSON_LIST.stream()
    .collect(Collectors.summarizingDouble(Person::getHeight));
System.out.println(collect4.getAverage());

//收集流中Double属性的统计值.如: 元素个数, 总和, 最小值, 平均值, 最大值.
//DoubleSummaryStatistics包含属性: {count=5, sum=887.500000, min=157.500000, average=177.500000, max=197.500000}
```

```java
//groupingBy(); 分组函数
Map<Boolean, List<Person>> genderMap = PERSON_LIST.stream()
    .collect(Collectors.groupingBy(Person::getGender)); //分组: 性别

Map<Boolean, Map<String, List<Person>>> groupMap = PERSON_LIST.stream() //多级分组: 先性别，再姓名
    .collect(Collectors.groupingBy(Person::getGender, Collectors.groupingBy(Person::getName)));

Map<Boolean, Map<String, List<Person>>> groupMap = PERSON_LIST.stream() //多级分组: 先性别，再年龄
    .collect(Collectors.groupingBy(Person::getGender, Collectors.groupingBy(person -> {
        if (person.getAge() < 18) {
            return "少年";
        } else if (person.getAge() < 30) {
            return "青年";
        } else {
            return "中年";
        }
    })));
```

```java
//partitioningBy(); 分区函数
//二者区别：分区函数只能将数据分为两组，即ture和false两组数据。分组函数会将数据分组成多个key的形式。
Map<Boolean, List<Person>> collect14 = list.stream()
    .collect(Collectors.partitioningBy(x -> x.age > 20));
```
>查找和匹配

```java
// allMatch / anyMatch / noneMatch(): 是否都满足 / 有一个满足 / 都不满足.
boolean allMatch = list.stream().allMatch((x) -> x.age > 30);

// findFirst(): 返回第一个元素. (Optional表示结果可能为空)
// findAny(): 返回任意一个. (多用于并行流)
Optional<Person> findFirst = list.stream().findFirst();

// count(): 返回流中元素总个数
long count = list.stream().filter(person -> person.age > 18).count();
System.out.println("count: " + count);

// max(): 返回流中最大值. min(): 最小值
// Optional<Person> max = list.stream() //获取最高身高者的所有属性
//         .max((x, y) -> Double.compare(x.height, y.height));

Optional<Person> max = list.stream() //获取最高身高者的所有属性 -> 简化版
        .max(Comparator.comparingDouble(x -> x.height));

Optional<Double> min = list.stream() //只获取最低身高的值
        .map(x -> x.height)
        .min(Double::compare);
```

>归约

```java
//reduce(): 将流中元素反复结合,最终生成一个值        
Optional<Double> reduce = list.stream()
    .map(x -> x.height)
    .reduce((x, y) -> x + y);

//有初始值,所以肯定不为空,即不用Optional<T>
//参数列表 -> arg0: 初始值; arg1: 求和操作
Double reduce2 = list.stream()
    .map(Person::getHeight)
    .reduce(1.0, Double::sum);
```
> 并行流

```sh
'并行流'：就是把一个数据集分成多个数据块，并用不同的线程分别处理每个数据块的流

#Fork/Join框架：在必要的情况下，先将一个大任务拆分(fork)成若干小任务，然后再将小任务运算结果进行 join 汇总。
#Fork/Join框架 & 传统线程池 的区别
(1)."工作窃取"模式 (work-stealing)
当执行新任务时，它可以将其拆分成更小的任务执行，并将小任务加到线程队列中。
当某个线程的任务队列全都完成时，它会从一个随机线程的队列中偷一个放到自己的队列中。

(2).相对于一般的线程池实现，fork/join框架的优势体现在对其中包含任务的处理方式上。
在一般的线程池中，如果一个线程正在执行的任务由于某些原因无法继续运行，那么该线程会处于等待状态。
而在fork/join框架实现中，如果某个子问题由于等待另外一个子问题的完成而无法继续运行。
那么处理该子问题的线程会主动寻找其他尚未运行的子问题来执行。这种方式减少了线程的等待时间，提高了性能。
```

## JAVA7

> Switch新增支持 String 类型

```shell
switch 支持的'byte，short，char，int，enum'都可以隐式的转换成 int 类型。不支持 boolean。
另外，java7支持 String 类型，其实是通过调用'String.hashCode()'，将String转换为 int。
```

> 资源对象在程序结束之后必须关闭，`try-with-resources`确保在语句的最后每个资源都会被关闭。

```shell
任何实现了'java.lang.AutoCloseable'和'java.io.Closeable'的对象，在出了 try 大括号范围之后，都会自动关闭。
所以，任意 catch 或者 finally 块都是在资源被关闭以后才运行的。
```

```java
//jdk7之后
public static String doIo7(String path) {
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
        return br.readLine();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return null;
}
```

```java
//jdk9之后
public static String doIo9(String path) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(path));
    try (br) {
        return br.readLine();
    }
}
```

>异常抑制

```shell
#如果对资源对象的处理，和对资源对象的关闭均遭遇了异常，则关闭异常将被抑制，处理异常将被抛出。
但关闭异常并没有丢失，而是存在处理异常的被抑制的异常列表中。通过异常的 getSuppressed()方法，可以提取出被抑制的异常。
```

```java
try {
    String s = doIo7("C:\\Users\\BlueCard\\Desktop\\123");
    System.out.println(s);
} catch (IOException e) {
    System.out.println("资源处理异常：" + e);
    
    Throwable[] suppressed = e.getSuppressed();
    System.out.println("资源关闭异常：" + Arrays.toString(suppressed));
}
```