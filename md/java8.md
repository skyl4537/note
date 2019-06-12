



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

## LocalDateTime

LocalDate，LocalTime，LocalDateTime：`人读的时间（非时间戳），都是线程安全的`。

> 通过 `now(); parse(); of();` 三种静态方法获取实例对象

```java
LocalDateTime now = LocalDateTime.now(); //北京时间：2019-4-29T12:37:46.354
now = LocalDateTime.parse("2019-4-29T12:37:46.354");//必须有T
now = LocalDateTime.of(2019, 10, 26, 12, 10, 55, 255 * 1000 * 1000); //最后参数为纳秒
```

>与传统日期处理的转换：`遗留类.from(); 遗留类.to新类();`

```java
Date date = Date.from(Instant.now());// Sat Oct 20 14:35:01 CST 2018
Instant instant = date.toInstant();// 2018-10-20T06:35:01.958Z

Timestamp timestamp = Timestamp.from(instant);//2018-10-20 14:52:12.611
Instant instant2 = timestamp.toInstant();//2018-10-20T06:52:12.611Z
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

## DateTimeFor...

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
>
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

##TemporalAd...

> 时间校正器。提供了日期操纵的接口，如：将日期调整到"下个周末"。TemporalAdjusters 是系统提供的接口实现类。`类似 Excutor，Excutors`

```java
LocalDateTime now = LocalDateTime.now();//今天周六
LocalDateTime with = now.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));//返回下周六
with = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));//返回本周六，即今天

with = now.with(TemporalAdjusters.lastInMonth(DayOfWeek.SATURDAY));//本月最后一个周六
with = now.with(TemporalAdjusters.lastDayOfMonth());//本月最后一天
```



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
Optional.of(obj); //参数不能为null，否则 NPE
Optional.empty(); //空实例

Optional.ofNullable(obj); //obj不为 null，创建实例；否则创建空实例。【常用】
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
public void ifPresent(Consumer<? super T> consumer)

public Optional<T> filter(Predicate<? super T> predicate)
public<U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper)
public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptioSupplier) throws X
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

