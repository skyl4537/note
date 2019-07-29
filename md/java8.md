[TOC]





# 时间API

##jdk7弊端

> java8之前 `java.util.Date 和 java.util.Calendar` 的弊端

【最重要】都是`线程不安全`。星期和月份都是从 0 开始计数。

```java
private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

ExecutorService service = Executors.newFixedThreadPool(25);
for (int i = 0; i < 20; i++) {
    service.execute(() -> {
        try {
            System.out.println(sdf.parse("2019-04-15 09:45:59"));
        } catch (ParseException e) {
        }
    });
}
// 等待上述的线程执行完，再关闭线程池。二者配合使用
service.shutdown();
service.awaitTermination(1, TimeUnit.DAYS);
```

## LocalDate

LocalDate，LocalTime，LocalDateTime：`人读的时间（非时间戳），都是线程安全的`。

> 通过 `now(); parse(); of();` 三种静态方法获取实例对象

```java
LocalDateTime now = LocalDateTime.now(); //北京时间：2019-4-29T12:37:46.354
now = LocalDateTime.parse("2019-4-29T12:37:46.354");//必须有T
now = LocalDateTime.of(2019, 10, 26, 12, 10, 55, 255 * 1000 * 1000); //最后参数为纳秒
```

> 增减操作：分别使用 `plus 和 minus` 关键字

```java
LocalDateTime plusDays = now.plusDays(3); //3天以后
minusYear = now.minus(Period.ofYears(2)); //2年以前
```

> 提取 年，月，日...，年份天数（1-366），月份天数（1-31），星期几（`DayOfWeek枚举值`）

```java
int year = now.getYear(); //年份：2019
int month = now.getMonthValue(); //月份：4

int dayOfYear = now.getDayOfYear(); //年份天数：119

DayOfWeek dayOfWeek = now.getDayOfWeek(); //DayOfWeek枚举: SATURDAY
int weekValue = dayOfWeek.getValue(); //星期：1
```

> 将月份天数，年份天数，月份，年份 修改为指定值，并返回新的 LocalDateTime对象

```java
LocalDateTime withDayOfYear = ldt.withDayOfYear(2); //今年第二天：2019-01-02T09:49:05.637
LocalDateTime withYear = ldt.withYear(1990); //1990年的今天：1990-10-20T09:49:05.637
```

> 获取两个LocalDate的相差天数`until();`

```java
LocalDateTime plusDays = ldt.plusDays(3);
long until = ldt.until(plusDays, ChronoUnit.DAYS);//plusDays-ldt=3，若将二者调换,则返回负数
```

> 比较两个日期的先后顺序：`isBefore(); isAfter();`

```java
boolean before = plusDays.isBefore(ldt); //false
```

> 判断是否是闰年：`LocalDate特有方法，isLeapYear();`

```java
boolean leapYear = LocalDate.now().isLeapYear();
```

## 格式化

> 日期格式化，线程安全。

```java
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

String format = formatter.format(LocalDateTime.now()); //format
LocalDateTime parse = LocalDateTime.parse("2019-04-29 15:59:54.943", formatter); //parse
```

## Instant

> `时间戳对象`，以Unix元年（1970年1月1日0点）开始所经历的毫秒数进行运算。

```java
Instant instant = Instant.now();//默认获取 UTC 时区时间戳,北京是 UTC+8

//效果等同于 System.currentTimeMillis()，但 Instant 获取的是 UTC 时区时间戳
long epochMilli = instant.toEpochMilli();//毫秒值，时间戳
long epochSecond = instant.getEpochSecond();//秒值
```

## ChronoUnit

> Duration，Period：分别表示时间间隔的两个维度，`前者用时分秒，后者用年月日`。

> Duration：表示两个时间之间相隔的时分秒。

```java
LocalDateTime begin = LocalDateTime.of(2017, 12, 31, 10, 0, 0);
LocalDateTime end = LocalDateTime.now();

Duration duration = Duration.between(begin, end);
System.out.println(duration); //PT12514H45M34.598S
```

>Period：表示两个日期之间相隔的X年X月X日，`不能转化为具体的多少天`。

```java
LocalDate begin = LocalDate.of(2017, 12, 31);
LocalDate end = LocalDate.now();

Period between = Period.between(begin, end);
System.out.println(between); //P1Y5M5D
System.out.printf("%d年%d月%d日", between.getYears(), between.getMonths(), between.getDays()); //1年5月5日
```

> ChronoUnit：计算两个日期或时间相隔的`具体天数，月数，甚至秒数`。

```java
LocalDateTime begin = LocalDateTime.of(2017, 12, 31, 10, 0, 0);
LocalDateTime end = LocalDateTime.now();

long days = ChronoUnit.DAYS.between(begin, end);
long months = ChronoUnit.MONTHS.between(begin, end);
long seconds = ChronoUnit.SECONDS.between(begin, end); //对于相隔秒数，必须使用 LocalDateTime
System.out.println(days + " - " + months + " - " + seconds); //521 - 17 - 45029290
```

##Temporal...

> 时间校正器。提供了日期操纵的接口，如：将日期调整到"下个周末"。TemporalAdjusters 是系统提供的接口实现类。`类似 Excutor，Excutors`

```java
LocalDateTime now = LocalDateTime.now();//今天周六
LocalDateTime with = now.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));//返回下周六
with = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));//返回本周六，即今天

with = now.with(TemporalAdjusters.lastInMonth(DayOfWeek.SATURDAY));//本月最后一个周六
with = now.with(TemporalAdjusters.lastDayOfMonth());//本月最后一天
```

##旧接口

>与传统日期处理的转换：`遗留类.from(); 遗留类.to新类();`

```java
Date date = Date.from(Instant.now());// Sat Oct 20 14:35:01 CST 2018
Instant instant = date.toInstant();// 2018-10-20T06:35:01.958Z

Timestamp timestamp = Timestamp.from(instant);//2018-10-20 14:52:12.611
Instant instant2 = timestamp.toInstant();//2018-10-20T06:52:12.611Z
```
# lambda

##基础概念

>一个语法糖，底层实现还是匿名内部类。

```shell
#匿名内部类使用同级别的成员变量，需要将变量定义为 final，lambda 也是如此。
只不过 jdk1.7 之前必须显示指定 final，1.8以后则可省 final，由底层自动添加.
```

```java
//(1).可省-参数类型：可由编译器推断得出，称为"类型推断"
//(2).可省-参数括号：当只有一个参数时
//(3).可省-方法体的大括号 和 return：当 lambda 体只有一条语句
list.forEach(new Consumer<String>() {

    @Override
    public void accept(String x) {
        System.out.println(x);
    }
});
```
```java
list.forEach(x -> System.out.println(x));
list.forEach(System.out::println); //进一步更新（待讲）
```

##方法引用

> 当要传递给lambda体的操作，已经有方法实现了，可以直接使用方法引用！

```shell
使用操作符 '::' 将方法名和对象或类的名字分隔开来。
引用方法的参数列表 和 返回值，与函数式接口的一致，就可以方法引用
```

>类の静态方法

```java
Comparator<Integer> com0 = (x, y) -> Integer.compare(x, y);
Comparator<Integer> com1 = Integer::compare;
```

>类の实例方法（1）

```java
//当lambda参数 arg0 是引用方法的调用者，arg1 是引用方法的参数（或无参数），可用 ClassName::methodName
Comparator<Integer> com2 = (x, y) -> x.compareTo(y);
Comparator<Integer> com3 = Integer::compareTo;
```

>类の实例方法（2）

```java
x -> System.out.println(x);
System.out::println; //PrintStream ps = System.out; 对象
```
##构造器引用

>无参构造器

```java
Supplier<Flower> supplier = () -> new Flower();

Supplier<Flower> supplier = Flower::new;
Flower flower = supplier.get();
```

> 有参构造器

```java
BiFunction<Integer, String, Flower> biFunction = (x, y) -> new Flower(x, y);

BiFunction<Integer, String, Flower> biFunction = Flower::new;
Flower flower = biFunction.apply(5, "55");
```

## 数组引用

```java
//返回长度x的String数组
Function<Integer, String[]> function = (x) -> new String[x];

Function<Integer, String[]> function = String[]::new;
String[] res = function.apply(10);
```
## 函数式接口

>只包含一个抽象方法的接口

```java
可以在任意接口上使用注解 @FunctionalInterface，来检查是否是函数式接口。
同时 javadoc 也会包含一条声明，说明这个接口是一个函数式接口。
```

>四大函数式接口

```java
//（1）.供给型接口（无入，有出）
Supplier<T> { T get(); }
```
```java
//（2）.消费型接口（有入，无出）
Consumer<T> { void accept(T t); }

list.forEach(person -> System.out.println(person.age)); //输出每个人的年龄
```

```java
//（3）.函数型接口（有入，有出）
Function<T, R> { R apply(T t); }

list.stream().map(person -> {
    return person.age; //取出每个人的年龄
}).forEach(age -> System.out.println(age));
```

```java
//（4）.断定型接口（有入，有出，返回boolean）
Predicate<T> { boolean test(T t); }

list.stream().filter(person -> {
    return person.age > 20; //过滤 age>20
}).forEach(person -> System.out.println(person));
```

#Stream

>Stream是数据管道，用于操作数据源（集合，数组等），产生新的元素集合。`集合讲的是存储，Stream讲的是操作`

```shell
Stream 不会存储元素
Stream 不会改变源对象。相反，它会返回一个持有结果集的新 Stream。
Stream 操作是延迟执行的。意味着它会等到需要结果时才执行。#详见Demo
```

`创建流（转化数据源） --> 中间操作（定义中间操作链，但不会立即执行） --> 终止操作（执行中间操作链，并产生结果）`

## 创建流

> 数据准备

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    public int id;
    public String name;
    public int age;
    public double height;
    public Gender gender;
}
```

```java
Person p1 = new Person(1, "zhao", 17, 197.5, Gender.MAN);
Person p2 = new Person(2, "qian", 18, 187.5, Gender.MAN);
Person p3 = new Person(3, "sui", 19, 177.5, Gender.MAN);
Person p4 = new Person(4, "li", 20, 167.5, Gender.WOMEN);
Person p5 = new Person(5, "wang", 21, 157.5, Gender.WOMEN);
List<Person> list = Arrays.asList(p1, p2, p3, p4, p5);
```

> 创建流的几种方式

```java
//Collection.stream() 或 parallelStream()
Stream<Person> stream = list.stream();
Stream<Person> parallelStream = list.parallelStream(); //并行流

//Arrays.stream()
Stream<Person> stream = Arrays.stream(array);

//Stream.of()
Stream<Person> stream = Stream.of(p1, p2, p3); //可变参数,也可传数组

//创建无限流(迭代+生成)
Stream<Integer> stream0 = Stream.iterate(1, x -> x + 3).limit(10); //迭代(必须限制大小)
Stream<Double> stream1 = Stream.generate(Math::random).limit(10); //生成
```
##操作流

> 中间操作的特点

```shell
惰性求值: 中间操作不会立即执行，只有执行了终止操作（如forEach()），中间操作才会执行
内部迭代: 迭代操作 forEach 是由 Stream-API 自动完成
短路操作: 以下 age=21 不打印，体现了短路操作。
```

>筛选与切片









# 接口变动

接口中变量的修饰符默认是 `public static final`，方法的修饰符默认是 `public abstract`。`都只能是`。







## java8变化





> java8之前接口中只允许存在：全局常量 和 抽象方法。java8新增 `默认方法` 和 静态方法（jdk7???）。



## 类优先原则

> 当 父类F 和 父接口I 都实现了 sayHello() 方法，`子类调用 父类F 实现`。

```java
@FunctionalInterface
interface I {
    public static final String NAME = "D";

    void sayHi();

    default void sayHello() { //jdk8新增默认方法
        System.out.println("sayHello-I");
    }

    static void sayHah() { //
        System.out.println("sayHah-I");
    }
}
```

## 接口冲突

> 当实现多个接口，且每个接口中都有同名default实现方法，就会报错。必须手动选择一个接口的default方法作为实现。







## Optional

Optional<T> 是一个容器类，代表一个值存在或不存在。原来用 null 表示一个值不存在，现在 Optional 可以更好的表达这个概念，并且可以避免空指针异常。`Optional应该只用于返回类型，而不是参数，也不是字段。`

> 常用方法

```java
Optional.empty(); //空实例

Optional.of(obj);         //参数不能为null，否则 NPE
Optional.ofNullable(obj); //obj不为 null，创建实例；否则创建空实例。【常用】

public void ifPresent(Consumer<? super T> consumer)
```

```java
Dog dog = optional.orElse(null); //有则返回，无则为 null
Dog dog = optional.orElseGet(() -> new Dog(5, "yellow")); //...无则创建
Dog dog = optional.orElseThrow(() -> new RuntimeException("NPE")); //...无则抛异常

optional.ifPresent(x -> System.out.println(x.getName())); //有则打印
```

```java
public<U> Optional<U> map(Function<? super T, ? extends U> mapper)

public T orElse(T other)
public T orElseGet(Supplier<? extends T> other)
public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptioSupplier) throws X

public Optional<T> filter(Predicate<? super T> predicate)
public<U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper)
```
> 优雅判 null

```java
public String getDogName0(Person person) throws IllegalArgumentException { //繁琐
    if (null != person) {
        Person.Pet pet = person.getPet();
        if (null != pet) {
            Person.Pet.Dog dog = pet.getDog();
            if (null != dog) {
                return dog.getName();
            }
        }
    }
    throw new IllegalArgumentException("param isn't available.");
}
```

```java
public String getDogName1(Person person) throws IllegalArgumentException { //优雅
    return Optional.ofNullable(person)
        .map(x -> x.getPet())
        .map(x -> x.getDog())
        .map(x -> x.getName())
        // .orElse("Unknown") //以上都为null，则设置默认值 或 抛出异常
        .orElseThrow(() -> new IllegalArgumentException("param isn't available."));
}
```



# java7

##类型推断

> Switch判断可以是`byte，short，char，int，以及String和枚举类型`，都可以隐式转为int。



> 泛型实例化时的类型自动推断

```java
List<String> list = new ArrayList</*String*/>();
```

## 资源关闭

资源对象在程序结束之后必须关闭。`try-with-resources`确保在语句的最后每个资源都会被关闭。

> 任何实现了`java.lang.AutoCloseable 和 java.io.Closeable`的对象，在出了 try 大括号范围之后，都会自动关闭。所以，任意 catch 或者 finally 块都是在 `资源被关闭以后才运行的`。

```java
//jdk7之前
public static String doIo6(String path) throws IOException {
    BufferedReader br = null;
    try {
        br = new BufferedReader(new FileReader(path));
        return br.readLine();
    } finally {// 必须在这里关闭资源
        if (br != null) {
            br.close();
        }
    }
}
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
public static String doIo8(String path) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(path));
    try (br) {
        return br.readLine();
    }
}
```

> 异常抑制。如果`对资源对象的处理`和`对资源对象的关闭`均遭遇了异常， 则关闭异常将被抑制。处理异常将被抛出，但关闭异常并没有丢失，而是存在处理异常的被抑制的异常列表中。通过异常的 `getSuppressed()`方法，可以提取出被抑制的异常。

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

