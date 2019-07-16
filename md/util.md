[TOC]



# Utils

包名统一使用小写，点分隔符之间有且仅有一个自然语义的英语单词。**包名统一使用单数形式**，但是类名如果有复数含义，**类名可以使用复数形式**。`com.example.spring.util.CommonUtils`

## 常用包

> apache

```xml
<!-- 该版本完全支持 Java5 的特性，如泛型和可变参数。该版本无法兼容以前的版本，简化很多平时经常要用到的写法，如判断字符串是否为空等等 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.8.1</version>
</dependency>

<!-- IO工具类，文件操作及字符串比较功能 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-io</artifactId>
    <version>1.3.2</version>
</dependency>

<!-- 对象池的实现，显著的提升了性能和可伸缩性，特别是在高并发加载的情况下 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
    <version>2.4.2</version>
</dependency>

<!-- email -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-email</artifactId>
    <version>1.4</version>
</dependency>
<!-- spring-boot email -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
</dependency>
```

> Spring

```xml

```
## 工具类

> 命名规则

```java
//【强制】包名统一使用小写，点分隔符之间有且仅有一个自然语义的英语单词。包名统一使用单数形式，但是类名如果有复数含义，类名可以使用复数形式。

正例：应用工具类包名为 com.alibaba.ai.util、类名为 MessageUtils（此规则参考 spring 的框架结构）
```



> 常用方法

```java
//获取项目class路径
public static String getClassPath() throws FileNotFoundException {
    // ClassUtils.getDefaultClassLoader().getResource("").getPath();//等同
    return ResourceUtils.getURL("classpath:").getPath();
}

//获取项目根路径
public static String getFilePath() throws FileNotFoundException {
    return ResourceUtils.getURL("").getPath();
}

//系统换行
public static String newline() {
    return System.getProperty("line.separator");
}
```

> 使用占位符拼接字符串

```java
//域名"www.qq.com"被访问了123.456次
MessageFormat.format("域名{0}被访问了{1}次", "\"www.qq.com\"", 123.456);

//创建格式化的字符串，及连接多个字符串对象：域名"www.qq.com"被访问了123.46次
String.format("域名%s被访问了%3.2f次", "\"www.qq.com\"", 123.456); 

//先转化十六进制,再高位补0
String.format("%04d",Integer.parseInt(String.format("%x", 16))); //0010
```

> 定时任务：不建议使用Timer

```java
//【强制】线程池不允许使用 Executors 去创建，而是通过 ThreadPoolExecutor 的方式。
//       这样的处理方式让写的同学更加明确线程池的运行规则，规避资源耗尽的风险。
private static ScheduledExecutorService scheduledExecutor = null;

public static ScheduledExecutorService getScheduleExecutor() {
    if (scheduledExecutor == null) {
        synchronized (Test02.class) {
            if (scheduledExecutor == null) {                
                scheduledExecutor = new ScheduledThreadPoolExecutor(10,
                        //源自：org.apache.commons.lang3.concurrent.BasicThreadFactory
                        new BasicThreadFactory.Builder().namingPattern("schedule-pool-%d").daemon(true).build());
            }
        }
    }
    return scheduledExecutor;
}
```

```java
public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command,
                                                 long initialDelay,
                                                 long delay,
                                                 TimeUnit unit);
```

>通过类名获取类的对象

```java
@Component // 获取bean的工具类
public class ApplicationUtils implements ApplicationContextAware {

    private static ApplicationContext context;

    // 实现接口的回调方法,设置上下文环境
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        MyApplicationContextAware.context = context;
    }

    // 获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return context;
    }

    // 通过name获取Bean.
    public static Object getBean(String name) {
        return context.getBean(name);
    }

    // 通过clazz获取Bean.
    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    // 通过name及clazz返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return context.getBean(name, clazz);
    }
}
```

## 其他概念

> 淘汰算法

```java
//LRU（least_recently_used）：最近最少使用
将最近使用的条目存放到缓存的顶部位置。达到缓存极限时，从底部开始移除

这里会使用到昂贵的算法，而且它需要记录"年龄位"来精确显示条目是何时被访问的。
此外，当一个LRU缓存算法删除某个条目后，"年龄位"将随其他条目发生改变。

[]; 'A'->[A]; 'B'->[B,A]; 'C'->[C,B,A]; 'D'->[D,C,B,A]; 'C'->[C,D,B,A]; 'E'->[E,C,D,B]
缓存容量4，初始为空。访问A则缓存为[A]，...当再次访问C时，将C提到首位；最后访问E，由于缓存已达上限，则将最后的A移除
```
```java
//LFU（least_frequently_used）：最不经常使用
使用一个计数器来记录条目被访问的频率，最低访问频率的条目首先被移除

此算法并不经常使用，因为它无法对一个拥有最初高访问率，但之后长时间没有被访问的条目缓存负责

[A-32,B-30,C-26,D-26]; 'D'->[A-32,B-30,D-27,C-26]; 'B'->[A-32,B-31,D-27,C-26]; 'F'->[A-32,B-31,D-27,F-1]
首先访问D，则D的频率+1，并和C调换位置；再访问B，将B频率+1；最后访问F，由于容量为4，则必须将末位C移除，并将F加入，评率设为1
```
```java
//FIFO（first_in_first_out）：先进先出
与普通存储器的区别是没有外部读写地址线，这样使用起来非常简单

但缺点就是只能顺序写入数据，顺序的读出数据，其数据地址由内部读写指针自动加1完成。不能像普通存储器那样可以由地址线决定读取或写入某个指定的地址
```

```java
//MRU（most_recently_used）：最近最常使用
最先移除最近最常使用的条目。一个MRU算法擅长处理一个条目越久，越容易被访问的情况
```



#Commons

## Lang

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
</dependency>
```

> StringUtils

```java
//null和""
boolean empty = StringUtils.isEmpty(" "); //false

//null和""，以及" "
boolean blank = StringUtils.isBlank(""); //true

//删除所有空格（空格+制表符+换行）
String deleteWhitespace = StringUtils.deleteWhitespace("   ab   c  "); //abc

//trim()的升级版，去除前导和后续的指定字符，不再限于空白符
String trim = StringUtils.strip("01 2 30", "0"); //1 2 3

//stripAll：去除字符串数组中每个元素中的指定字符
String[] strs = {"010", "02", "30"};
String[] stripAll = StringUtils.stripAll(strs, "0"); //1 2 3
```

```java
//判断是否包含这个字符
boolean contains = StringUtils.contains("中华人民共和国", "共和"); //true

//截取从from开始字符串，区分大小写。截取失败返回空字符串""
String from = StringUtils.substringAfter("SELECT * FROM PERSON", "from"); //""

//截取左边两个字符
String left = StringUtils.left("中华人民共和国", 2); //中华

//在左边填充指定字符,使之总长度为6
String x = StringUtils.leftPad("123", 6, '0'); //000123
String format = String.format("%06d", 123); //jdk自带，不好用。000123
```

```java
//判断该字符串是不是为数字(0~9)组成，如果是，返回true。但该方法不识别有小数点和 请注意。
boolean numeric = StringUtils.isNumeric("45453.4");//false

//将数组中的内容以","分隔
List<String> list = Arrays.asList("a", "b", "c");
String join = StringUtils.join(list, ","); //a,b,c

//首字母大写
String capitalize = StringUtils.capitalize("中华人民共和国"); //Abc
```

```java
//字符串进行省略操作，省略字符以省略号填充，最小长度为4（省略号占3个字符）
StringUtils.abbreviate("abcdefg", 6); //abc...
StringUtils.abbreviate("abcdefg", 4); //a...
StringUtils.abbreviate("abcdefg", 3); //IllegalArgumentException
```

> NumberUtils

```java
int i = NumberUtils.toInt("5f", 5); //字符串转int，默认值5

boolean parsable = NumberUtils.isParsable("5.5"); //字符串是否是数字? true
boolean digits = NumberUtils.isDigits("5.5"); //字符串中是否全为数字? false
```

> RandomStringUtils：指定长度的随机 数字，字母，字母和数字

```java
String randomNumeric = RandomStringUtils.randomNumeric(5); //60954
String randomAlphabetic = RandomStringUtils.randomAlphabetic(5); //MgQgI
String randomAlphanumeric = RandomStringUtils.randomAlphanumeric(5); //Mq985
```

> ClassUtils

```java
//取得类名和包名
String shortClassName = ClassUtils.getShortClassName(Test.class);
String packageName = ClassUtils.getPackageName(Test.class);
```

## IO

```xml
<!-- https://mvnrepository.com/artifact/commons-io/commons-io -->
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.6</version>
</dependency>
```

> IOUtils

```java
IOUtils.closeQuietly(in); //不再推荐使用这种关闭流方式，推荐使用java7新特性：try-with-resources
```

```java
try (FileInputStream in = new FileInputStream(src);
     FileWriter out = new FileWriter(dest)) {
    IOUtils.copy(in, out, "UTF-8"); //拷贝流，从输入到输出
} catch (IOException e) {
    e.printStackTrace();
}
```

```java
//拷贝较大的数据流，比如2G以上
IOUtils.copyLarge(new FileInputStream(src), new FileOutputStream(dest));
```

```java
String line = IOUtils.toString(in, "UTF-8"); //读取流中的字符串
```

```java
IOUtils.write("1234", new FileOutputStream(dest), "UTF-8"); //字符串写入输出流
```

> FileUtils

```java
List<String> lines = FileUtils.readLines(file, "UTF-8"); //读取文件
```

```java
FileUtils.listFiles(dir, null, true); //迭代遍历目录

FileUtils.listFiles(dir, EmptyFileFilter.NOT_EMPTY, null); //过滤非空文件，不过滤目录

FileUtils.deleteDirectory(new File(path)); //迭代删除文件夹
```


```java
FileUtils.copyFile(src, dest); //拷贝文件
```

```java
URLCodec urlCodec = new URLCodec();
String url = "http://192.168.5.25:8080/webpark/image/20190518/" +
    urlCodec.encode("十二pass.log", "UTF-8"); //url中文 进行编码和解码
String dest = "C:\\Users\\BlueCard\\Desktop";

URL httpUrl = new URL(url);
String fileName = urlCodec.decode(FilenameUtils.getName(httpUrl.getFile()), "UTF-8");

//下载URL资源，注意设置超时时间,单位毫秒
FileUtils.copyURLToFile(httpUrl, new File(dest, fileName), 5 * 1000, 5 * 1000);
```

> FilenameUtils

```java
String filePath = "D:\\abc\\123.txt";
String name = FilenameUtils.getName(filePath); //123.txt
String baseName = FilenameUtils.getBaseName(filePath); //123
String extension = FilenameUtils.getExtension(filePath); //txt
```

> Files

```java
//已过时，推荐使用：java.nio.file.Files
long freeSpace = FileSystemUtils.freeSpace("D:/");

FileStore fileStore = Files.getFileStore(Paths.get("D:/"));
long totalSpace = fileStore.getTotalSpace(); //总容量
long usableSpace = fileStore.getUsableSpace(); //可用容量
```

## codec

```xml
<!-- https://mvnrepository.com/artifact/commons-codec/commons-codec -->
<dependency>
    <groupId>commons-codec</groupId>
    <artifactId>commons-codec</artifactId>
    <version>1.12</version>
</dependency>
```

> Base64

```java
try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
     BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dest))) {
    Base64 base64 = new Base64();

    byte[] buff = new byte[(int) new File(src).length()];
    bis.read(buff);
    String encode = base64.encodeAsString(buff);
    System.out.println("encode：" + encode); //File -> Base64

    byte[] decode = base64.decode(encode);
    for (int i = 0; i < decode.length; ++i) {
        if (decode[i] < 0) { //调整异常数据
            decode[i] += 256;
        }
    }
    bos.write(decode); //Base64 -> File
} catch (IOException e) {
    e.printStackTrace();
}
```

> MD5

```java
//org.apache.commons.codec.digest;
String md5Hex = DigestUtils.md5Hex("123456");

//org.springframework.util.DigestUtils;
String md5Hex = DigestUtils.md5DigestAsHex("123456".getBytes());
```

> URLCode

```java
URLCodec urlCodec = new URLCodec();
String encode = urlCodec.encode("abcdef", "UTF-8"); 

String decode = urlCodec.decode(encode, "UTF-8");
```

## Collections

```xml
<!-- https://mvnrepository.com/artifact/org.apache.commons/commons-collections4 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.3</version>
</dependency>
```

<http://www.imooc.com/article/271570>

> CollectionUtils：公用的接口和工具类（覆盖所有子类）

```java
List<String> list0 = Arrays.asList("1", "3", "5");
List<String> list1 = Arrays.asList("1", "13", "5");

//得到两个集合中相同的元素：[1, 5]
Collection<String> retainAll = CollectionUtils.retainAll(list0, list1);

//移除第二集合中的元素：[3]
Collection<String> removeAll = CollectionUtils.removeAll(list0, list1);
```

> ArrayUtils

```java
int[] array = {1, 3, 5, 7, 8};
int[] removeElement = ArrayUtils.removeElement(array, 5); //删除指定元素：1 3 7 8
```

```java
int[] insert = ArrayUtils.insert(3, array, 0, 69); //在 index 为3的位置添加两个元素 0,69
```

```java
ArrayUtils.reverse(array); //数组反转
```
# fastjson

##基础概念

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

##相互转化

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
##Null值处理

> null值处理：list ---> JSONString

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

##Boot配置

> SpringBoot2.x默认使用 jacksonJson 解析，现转换为 fastjson，并且解决中文乱码问题。

```java
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
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

#JackJson

>ObjectMapper是JSON操作的核心，Jackson的所有JSON操作都是在ObjectMapper中实现

```java
ObjectMapper mapper = new ObjectMapper();
```

```java
String json = mapper.writeValueAsString(employee); //X -> String
```

```java
String jsonArrary = mapper.writeValueAsString(employees); //list -> String
```

```java
Employee value = mapper.readValue(json, Employee.class); //String -> X
```

```java
JavaType list = mapper.getTypeFactory().constructParametricType(List.class, Employee.class); //String -> list/map
//JavaType map = mapper.getTypeFactory().constructParametricType(HashMap.class, String.class, Employee.class);
List<Employee> employees = mapper.readValue(jsonArrary, list);
```

```java
List<Employee> values = mapper.readValue(jsonArrary, new TypeReference<List<Employee>>() {}); //String -> list/map
```

# HttpClient

##基础概念

```xml
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
</dependency>
```
> 区别 GET & POST

```java
超链接<a/>    ---> //只能用 GET 提交HTTP请求
表单<form/>   ---> //可以用 GET，POST .......

GET          ---> //参数只能在请求行（request-line）
POST         ---> //参数可在请求行，亦可在请求体（request-body）
```
> 区别 URL & URI：http://ip:port/demo/hello/hello & /demo/hello/hello

<https://www.cnblogs.com/wuyun-blog/p/5706703.html>

<https://blog.csdn.net/koflance/article/details/79635240>

## GET-请求行

> 两种方式获取HttpGet

```java
//(1).直接将参数拼接在 URI 之后
String uri = "http://127.0.0.1:8090/demo/http/get?name=中国&age=70";
HttpGet httpGet = new HttpGet(uri);
```
```java
//(2).通过 URIUtils 工具类生成带参数的 URI
String param = "name=中国&age=70";
// String param = "name=" + URLEncoder.encode("中国", "UTF-8") + "&age=70"; //中文参数,encode
URI uri = URIUtils.createURI("http", "127.0.0.1", 8090, "/demo/http/get", param, null);
HttpGet httpGet = new HttpGet(uri);
```

## POST-请求行

> 两种方式获取httpPost （同GET）

```java
//(1).拼接字符串
String uri = "http://127.0.0.1:8090/demo/http/post?name=中国&age=70";
HttpPost httpPost = new HttpPost(uri);
```

```java
//(2).工具类 URIUtils
String param = "name=中国&age=70";
// String param = "name=" + URLEncoder.encode("中国", "UTF-8") + "&age=70"; //中文参数,encode
URI uri = URIUtils.createURI("http", "127.0.0.1", 8090, "/demo/http/post", param, null);
HttpPost httpPost = new HttpPost(uri);
```

## POST-请求体

> 传输 表单键值对 keyValue

```java
//1.POST表单
List<NameValuePair> nvps = new ArrayList<>(2);
nvps.add(new BasicNameValuePair("name", "中国"));
nvps.add(new BasicNameValuePair("age", "70"));
UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps, Charset.forName("UTF-8")); //中文乱码

HttpPost httpPost = new HttpPost("http://127.0.0.1:8090/demo/http/post");
httpPost.setEntity(entity);
```

```java
//2.查看HTTP数据
System.out.println(entity.getContentType()); //Content-Type: application/x-www-form-urlencoded; charset=UTF-8
System.out.println(entity.getContentLength()); //30
System.out.println(EntityUtils.toString(entity)); //name=%E4%B8%AD%E5%9B%BD&age=70
```

> 传输 JSON

```java
String json = "{\"name\":\"中国\",\"age\":\"70\"}";
StringEntity entity = new StringEntity(json, "UTF-8"); //中文乱码,默认"ISO-8859-1"
entity.setContentEncoding("UTF-8");
entity.setContentType("application/json");//设置contentType --> json

HttpPost httpPost = new HttpPost("http://127.0.0.1:8090/demo/http/postBody");
httpPost.setEntity(entity);
```

> 传输 File

```xml
<!-- HttpClient-File -->
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpmime</artifactId>
</dependency>
```

```html
<!-- 前台页面 -->
<form action="http://127.0.0.1:8090/demo/http/postFile" method="POST" enctype="multipart/form-data">  
    <input type="text" name="fileName" value="中国"/>  
    <input type="file" name="file"/>  
    <inupt type="submit" value="提交"/>  
</form>
```

```java
//后台逻辑
MultipartEntityBuilder builder = MultipartEntityBuilder.create();
ContentType contentType = ContentType.create("text/plain","UTF-8");//中文乱码,默认"ISO-8859-1"
builder.addTextBody("fileName", "中国", contentType);
builder.addBinaryBody("file", new File("C:\\Users\\BlueCard\\Desktop\\StatusCode.png"));
HttpEntity entity = builder.build();

HttpPost httpPost = new HttpPost("http://127.0.0.1:8090/demo/http/postFile");
httpPost.setEntity(entity);
```

## 请求结果解析

请求结果解析通用于 GET 和 POST。

```java
String uri = "http://127.0.0.1:8090/demo/http/get?name=中国&age=70";
HttpGet httpGet = new HttpGet(uri); //组装请求-GET
// HttpPost httpPost = new HttpPost(uri); //组装请求-POST

try (CloseableHttpResponse httpResponse =
             HttpClients.createDefault().execute(httpGet)) { //发送请求，连接自动关闭
    if (null != httpResponse && HttpStatus.SC_OK ==
            httpResponse.getStatusLine().getStatusCode()) {
        String res = EntityUtils.toString(httpResponse.getEntity(), "UTF-8"); //获取结果
        System.out.println(res);
    }
} catch (IOException e) {
    e.printStackTrace();
}
```

# 雪花算范

>分布式ID生成器

```java
由于数据库在生产环境中要分片部署（MyCat），所以不能使用数据库本身的自增功能来产生主键值，只能由程序来生成唯一的主键值。
采用开源的 twitter 的 snowflake（雪花）算法，总长度64bit。
```

```java
'优点'：（1）整体上按照时间自增排序（2）整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分) （3）效率较高
经测试，SnowFlake每秒能够产生26万ID左右。

0        //最高位是符号位,始终为0,不可用.
1-41     //41 位的时间序列，精确到毫秒级，可使用到2082年。时间位另一作用是可以根据时间进行排序
42-51    //10 位的机器标识，10位的长度最多支持部署1024个节点（2^10）
52-63    //12 位的计数序列，是一系列的自增id，支持每个节点每毫秒产生4096个ID序号（2^12）
```

![](assets/util0.png)

> 算法实现

```java
/**
 * <p>名称：IdWorker.java</p>
 * <p>描述：分布式自增长ID</p>
 * <pre>
 *     Twitter的 Snowflake　JAVA实现方案
 * </pre>
 * 核心代码为其IdWorker这个类实现，其原理结构如下，我分别用一个0表示一位，用—分割开部分的作用：
 * 1||0---0000000000 0000000000 0000000000 0000000000 0 --- 00000 ---00000 ---000000000000
 * 在上面的字符串中，第一位为未使用（实际上也可作为long的符号位），接下来的41位为毫秒级时间，
 * 然后5位datacenter标识位，5位机器ID（并不算标识符，实际是为线程标识），
 * 然后12位该毫秒内的当前毫秒内的计数，加起来刚好64位，为一个Long型。
 * 这样的好处是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞（由datacenter和机器ID作区分），
 * 并且效率较高，经测试，snowflake每秒能够产生26万ID左右，完全满足需要。
 * <p>
 * 64位ID (42(毫秒)+5(机器ID)+5(业务编码)+12(重复累加))
 *
 * @author Polim
 */
public class IdWorker {
    // 时间起始标记点，作为基准，一般取系统的最近时间（一旦确定不能变动）
    private final static long twepoch = 1288834974657L;
    // 机器标识位数
    private final static long workerIdBits = 5L;
    // 数据中心标识位数
    private final static long datacenterIdBits = 5L;
    // 机器ID最大值
    private final static long maxWorkerId = -1L ^ (-1L << workerIdBits);
    // 数据中心ID最大值
    private final static long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
    // 毫秒内自增位
    private final static long sequenceBits = 12L;
    // 机器ID偏左移12位
    private final static long workerIdShift = sequenceBits;
    // 数据中心ID左移17位
    private final static long datacenterIdShift = sequenceBits + workerIdBits;
    // 时间毫秒左移22位
    private final static long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    private final static long sequenceMask = -1L ^ (-1L << sequenceBits);
    /* 上次生产id时间戳 */
    private static long lastTimestamp = -1L;
    // 0，并发控制
    private long sequence = 0L;

    private final long workerId;
    // 数据标识id部分
    private final long datacenterId;

    public IdWorker() {
        this.datacenterId = getDatacenterId(maxDatacenterId);
        this.workerId = getMaxWorkerId(datacenterId, maxWorkerId);
    }

    /**
     * @param workerId     工作机器ID
     * @param datacenterId 序列号
     */
    public IdWorker(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0",
                    maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0",
                    maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    /**
     * 获取下一个ID
     *
     * @return
     */
    public synchronized long nextId() {
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d " +
                    "milliseconds", lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            // 当前毫秒内，则+1
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                // 当前毫秒内计数满了，则等待下一秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        // ID偏移组合生成最终的ID，并返回ID
        long nextId = ((timestamp - twepoch) << timestampLeftShift)
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift) | sequence;
        return nextId;
    }

    private long tilNextMillis(final long lastTimestamp) {
        long timestamp = this.timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = this.timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * <p>
     * 获取 maxWorkerId
     * </p>
     */
    protected static long getMaxWorkerId(long datacenterId, long maxWorkerId) {
        StringBuffer mpid = new StringBuffer();
        mpid.append(datacenterId);
        String name = ManagementFactory.getRuntimeMXBean().getName();
        if (!name.isEmpty()) {
            /*
             * GET jvmPid
             */
            mpid.append(name.split("@")[0]);
        }
        /*
         * MAC + PID 的 hashcode 获取16个低位
         */
        return (mpid.toString().hashCode() & 0xffff) % (maxWorkerId + 1);
    }

    /**
     * <p>
     * 数据标识id部分
     * </p>
     */
    protected static long getDatacenterId(long maxDatacenterId) {
        long id = 0L;
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            if (network == null) {
                id = 1L;
            } else {
                byte[] mac = network.getHardwareAddress();
                id = ((0x000000FF & (long) mac[mac.length - 1])
                        | (0x0000FF00 & (((long) mac[mac.length - 2]) << 8))) >> 6;
                id = id % (maxDatacenterId + 1);
            }
        } catch (Exception e) {
            System.out.println(" getDatacenterId: " + e.getMessage());
        }
        return id;
    }
}
```



