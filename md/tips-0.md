



# Commons

## lang3

```xml
<!-- https://mvnrepository.com/artifact/org.apache.commons/commons-lang3 -->
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.9</version>
</dependency>
```

> NumberUtils

```java
int i = NumberUtils.toInt("abc", 5); //字符串转int，默认值5

boolean parsable = NumberUtils.isParsable("5.5"); //字符串是否是数字? true
boolean digits = NumberUtils.isDigits("5.5");     //字符串中是否全为数字? false
```

> RandomStringUtils

```java
String randomNumeric = RandomStringUtils.randomNumeric(5);           //60954 --> 纯数字
String randomAlphabetic = RandomStringUtils.randomAlphabetic(5);     //MgQgI --> 纯字母
String randomAlphanumeric = RandomStringUtils.randomAlphanumeric(5); //Mq985 --> 数字+字母
```
> StringUtils

```java
boolean empty = StringUtils.isEmpty(" "); //false --> null和""
boolean blank = StringUtils.isBlank("");  //true  --> null和"" 以及" "

//清除空白字符（空格+制表符+换行）
String deleteWhitespace = StringUtils.deleteWhitespace("   ab   c  "); //abc

//trim()的升级版，去除前导和后续的【指定字符】，不再限于空白符
String trim = StringUtils.strip("01 2 30", "0"); //1 2 3
//stripAll：去除字符串数组中每个元素中的【指定字符】
String[] strs = {"010", "02", "30"};
String[] stripAll = StringUtils.stripAll(strs, "0"); //1 2 3

//split()升级版
String str = "12.34|56,78.90";
String[] array1 = StringUtils.split(str, " .|,");

//将数组中的内容以","分隔
List<String> list = Arrays.asList("a", "b", "c");
String join = StringUtils.join(list, ","); //a,b,c

//首字母大写
String capitalize = StringUtils.capitalize("abc"); //Abc

//字符串的缩写（最后1个参数必须大于4，因为省略号占3个字符。对于3个参数时，参数2代表偏移量，从1开始）
String str = "1234567890";
String abbreviate = StringUtils.abbreviate(str, 5); //12...
String abbreviate1 = StringUtils.abbreviate(str, 5, 9); //...678...

//查找嵌套字符串
String htmlContent = "123First456-123Second456";
String between = StringUtils.substringBetween(htmlContent, "1231", "456"); //返回第1个匹配结果，否则 null
String[] betweens = StringUtils.substringsBetween(htmlContent, "123", "456"); //返回所有匹配结果，否则 null
//取得某字符串在另一字符串中出现的次数
int matches = StringUtils.countMatches("Chinese People", "e"); //4
//判断是否包含这个字符
boolean contains = StringUtils.contains("中华人民共和国", "共和"); //true

//在左边填充指定字符,使之总长度为6
String x = StringUtils.leftPad("123", 6, '0'); //000123
String format = String.format("%06d", 123); //jdk自带，不好用。000123

//截取从from开始字符串，区分大小写。截取失败返回空字符串""
String from = StringUtils.substringAfter("SELECT * FROM PERSON", "from"); //""
//截取左边两个字符
String left = StringUtils.left("中华人民共和国", 2); //中华

//重复字符串
String repeat = StringUtils.repeat("*", 5); //*****
//把 arg0 插入将 arg2 重复多次后的字符串中间，得到字符串的总长为 arg1
String center = StringUtils.center("China", 11, "*"); //***China***

//颠倒字符串
String reverse = StringUtils.reverse("ABCDE"); //EDCBA

//判断字符串内容的类型（该方法不识别有小数点和 请注意）
String state = "Virginia";
boolean numeric = StringUtils.isNumeric(state); //全由数字组成: false
boolean alpha = StringUtils.isAlpha(state);     //全由字母组成: true
boolean alphanumeric = StringUtils.isAlphanumeric(state); //全由数字或数字组成: true
boolean alphaSpace = StringUtils.isAlphaSpace(state);     //全由字母或空格组成: true
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
String srcPath = "C:\\Users\\BlueCard\\Desktop\\src.txt";
FileInputStream in = new FileInputStream(srcPath);
FileReader reader = new FileReader(srcPath);

//读取流，返回: String（全部内容）、List<String>（每行内容）、LineIterator
String toString = IOUtils.toString(in, Charset.forName("UTF-8"));
List<String> lines = IOUtils.readLines(reader);
LineIterator lineIterator = IOUtils.lineIterator(reader);
```

> FileUtils

```java
Charset utf8 = Charset.forName("UTF-8");
String url = "http://192.168.5.25:8080/webpark/view/index.jsp";
File srcFile = new File("C:\\Users\\BlueCard\\Desktop\\src.txt");
File destFile = new File("C:\\Users\\BlueCard\\Desktop\\dest.txt");
File dir = new File("C:\\Users\\BlueCard\\Desktop\\config");

// 下载URL资源，注意设置超时时间,单位毫秒
FileUtils.copyURLToFile(new URL(url), srcFile, 5 * 1000, 5 * 1000);

String fileContent = FileUtils.readFileToString(srcFile, utf8); // 读取文件
List<String> list = FileUtils.readLines(srcFile, utf8);

FileUtils.write(destFile, "测试数据", utf8); //输出文件
FileUtils.copyFile(srcFile, destFile);            //拷贝文件
boolean equals = FileUtils.contentEquals(srcFile, destFile); //比较两个文件的内容是否相同

long size = FileUtils.sizeOf(srcFile); //文件大小：单位字节

// 遍历目录(arg2: 是否递归): 过滤doc文件(只显示文件名,不显示后缀)
List<File> fileList = (List<File>) FileUtils.listFiles(dir, new String[]{"doc"}, true);
// List<File> fileList = (List<File>) FileUtils.listFiles(dir, null, false);

boolean deleteQuietly = FileUtils.deleteQuietly(dir); //删除文件/目录
FileUtils.cleanDirectory(dir); //清空目录，但不删除最外层目录

//相比于JDK的delete()有两个不同: (1).它不需要被删除目录下的内容为空 (2).它不会抛出IOException
FileUtils.deleteDirectory(dir); //只能删除目录，目标为文件则报错
```

> FilenameUtils

```java
String filePath = "D:\\abc\\123.txt";
String name = FilenameUtils.getName(filePath); //123.txt
String baseName = FilenameUtils.getBaseName(filePath); //123
String extension = FilenameUtils.getExtension(filePath); //txt
```

> File `getCanonicalPath()`

```java
//返回定义时的路径，可能是相对路径，也可能是绝对路径，这个取决于定义时用的是相对路径还是绝对路径。
//如果定义时用的是绝对路径，那么结果跟 getAbsolutePath() 一样
file.getPath(); // ..\test1.txt

//返回的是定义时的路径对应的相对路径，但不会处理"."和".."的情况
file.getAbsolutePath(); // F:\sp_project\spring\..\test1.txt

//返回的是规范化的绝对路径，相当于将 getAbsolutePath() 中的"."和".."解析成对应的正确的路径
file.getCanonicalPath(); // F:\sp_project\test1.txt
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

> MD5

```java
//org.apache.commons.codec.digest;
String md5Hex = DigestUtils.md5Hex("123456");

//org.springframework.util.DigestUtils;
String md5Hex = DigestUtils.md5DigestAsHex("123456".getBytes());
```
> URLCode

```java
String url = "http://192.168.5.25:8080/webpark/plate?plate=京A88888";
URLCodec urlCodec = new URLCodec();
String encode = urlCodec.encode(url, "UTF-8");
String decode = urlCodec.decode(encode, "UTF-8");
```
```java
String encode = URLEncoder.encode(url, "UTF-8"); //JDK 自带方法
String decode = URLDecoder.decode(url, "UTF-8");
```

> Base64

```java
Charset utf8 = Charset.forName("UTF-8");
File file = new File("C:\\Users\\BlueCard\\Desktop\\dest.txt");
String content = FileUtils.readFileToString(file, utf8);

Base64 base64 = new Base64();
String encode = base64.encodeAsString(content.getBytes()); //转成 Base64
byte[] decode = base64.decode(encode);
String decodeStr = new String(decode);
```
```java
byte[] encode = Base64.getEncoder().encode(content.getBytes()); //JDK 自带方法
byte[] decode = Base64.getDecoder().decode(encode);
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
> CollectionUtils

```java
List<String> list0 = Arrays.asList("1", "3", "5");
List<String> list1 = Arrays.asList("1", "13", "5");

// 集合1 和 集合2 相同的元素：[1, 5]
Collection<String> retainAll = CollectionUtils.retainAll(list0, list1);

// 从 集合1 中移除 集合2 中的元素，剩下的是：[3]
Collection<String> removeAll = CollectionUtils.removeAll(list0, list1);
```
> ArrayUtils

```java
int[] array = {1, 3, 5, 7, 8};

ArrayUtils.reverse(array); //数组反转
int[] removeElement = ArrayUtils.removeElement(array, 5); //删除指定元素：1 3 7 8
int[] insert = ArrayUtils.insert(3, array, 0, 69); //在index为 3 的位置（5和7之间）添加两个元素 0,69
```
## BeanUtils

```xml
<!-- https://mvnrepository.com/artifact/commons-beanutils/commons-beanutils -->
<dependency>
    <groupId>commons-beanutils</groupId>
    <artifactId>commons-beanutils</artifactId>
    <version>1.9.4</version>
</dependency>
```

>属性名相同，类型相同（包括基本类型和封装类型）。可以被复制

```java
//springframework.beans.BeanUtils  --> 两个类的参数顺序不同
BeanUtils.copyProperties(source， target);

//commons.beanutils.BeanUtils
BeanUtils.copyProperties(target, source);

//如果希望哪个属性不被复制，使用重载方法。ignoreProperties 传属性名称
public static void copyProperties(Object source, Object target, String... ignoreProperties)
```

```java
//BeanUtils.copyProperties()对bean属性进行复制，属于浅复制。并且，不能复制集合和数组
//对于 list，map，数组等，不能通过以上方法进行复制的，可通过 JSON 工具实现。前提是需要有无参构造

List<Dog> A = new ArrayList<>();
List<Dog> B = new ArrayList<>();
B = JSON.parseArray(JSON.toJSONString(A), Dog.class);
```
