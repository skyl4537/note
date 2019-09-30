[TOC]




# Optional

> 只用于返回类型，而不是参数，也不是字段

```shell
是一个容器类，代表一个值存在或不存在。原来用 null 表示一个值不存在，现在 Optional 可以更好的表达这个概念，并且可以避免空指针异常。
```

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
> 优雅判 NULL

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


# 时间API

##jdk7弊端

> 问题：Date，Calendar，SimpleDateFormat 都是`线程不安全`

```java
//把 SimpleDateFormat 实例定义为静态变量，在多线程情况下会被多个线程共享。容易出现问题
private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

public void test() throws InterruptedException {
    ExecutorService service = Executors.newFixedThreadPool(25);
    for (int i = 0; i < 20; i++) {
        service.execute(() -> {
            System.out.println(sdf.parse("2019-04-15 09:45:59"));
        });
    }
    // 等待上述的线程执行完，再关闭线程池。二者配合使用
    service.shutdown();
    service.awaitTermination(1, TimeUnit.DAYS);
}
```

> 方案1：只在需要的时候创建实例，不用static修饰。`缺点`：加重了创建对象的负担，会频繁地创建和销毁对象，效率较低。

```java
public static String formatDate(Date date) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return sdf.format(date);
}

public static Date parse(String strDate) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return sdf.parse(strDate);
}
```

> 方案2： synchronized大法好。`缺点`：并发量大的时候会对性能有影响，线程阻塞。

```java
private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

public static String formatDate(Date date) throws ParseException {
    synchronized (sdf) {
        return sdf.format(date);
    }
}

public static Date parse(String strDate) throws ParseException {
    synchronized (sdf) {
        return sdf.parse(strDate);
    }
}
```

> 方案3：ThreadLocal，确保每个线程单独一个SimpleDateFormat对象。

```java
private static ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>() {
    @Override
    protected DateFormat initialValue() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
};

//上式的lambda简化版
//private static ThreadLocal<DateFormat> threadLocal = 
//        ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

public static Date parse(String dateStr) throws ParseException {
    return threadLocal.get().parse(dateStr);
}

public static String format(Date date) {
    return threadLocal.get().format(date);
}
```

> `方案4`：基于JDK1.8的 DateTimeFormatter

```java
private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

public static String formatDate2(LocalDateTime date) {
    return formatter.format(date);
}

public static LocalDateTime parse2(String dateNow) {
    return LocalDateTime.parse(dateNow, formatter);
}
```

## 格式化

> 日期格式化，`线程安全`。

```java
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

String format = formatter.format(LocalDateTime.now()); //format
LocalDateTime parse = LocalDateTime.parse("2019-04-29 15:59:54.943", formatter); //parse
```
## LocalDate

> LocalDate，LocalTime，LocalDateTime：`人读的时间（非时间戳），都是线程安全的`。

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

## 时间戳

> `Instant`，以Unix元年（1970年1月1日0点）开始所经历的毫秒数进行运算。

```java
Instant instant = Instant.now();//默认获取 UTC 时区时间戳,北京是 UTC+8

//效果等同于 System.currentTimeMillis()，但 Instant 获取的是 UTC 时区时间戳
long epochMilli = instant.toEpochMilli();//毫秒值，时间戳
long epochSecond = instant.getEpochSecond();//秒值
```

## 时间间隔

> Duration：两个时间点相隔的 `X时X分X秒`

```java
LocalDateTime start = LocalDateTime.of(2017, 12, 31, 10, 0, 0);
LocalDateTime end = LocalDateTime.now();

Duration between = Duration.between(start, end); //PT14735H53M57.538S
```

>Period：两个时间点相隔的 `X年X月X日`

```java
LocalDate start = LocalDate.of(2017, 12, 31);
LocalDate end = LocalDate.now();

Period between = Period.between(start, end); //P1Y8M6D
```

>ChronoUnit：两个时间点间隔的 `具体天数，月数，甚至秒数`

```java
LocalDateTime start = LocalDateTime.of(2019, 9, 6, 10, 03, 0);
LocalDateTime end = LocalDateTime.now();

long between = ChronoUnit.MILLIS.between(start, end); //99262 毫秒
```

##时间校正器

> 提供了日期操纵的接口，如：将日期调整到"下个周末"。TemporalAdjusters 是系统提供的接口实现类。`类似 Excutor，Excutors`

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

>lambda 可以被当做是匿名内部类的`语法糖`，但是二者在原理上是不同的

```sh
'语法糖'：指使用更加方便，但是原理不变的代码语法。如，增强foreach，底层的实现仍然是迭代器

从应用层面来讲，java 中的 lambda 可以被当做是匿名内部类的 "语法糖"，但是二者在原理上是不同的
```

```shell
#匿名内部类使用同级别的成员变量，需要将变量定义为 final。lambda 也是如此。
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
//当 lambda 参数 arg0 是引用方法的调用者，arg1 是引用方法的参数（或无参数），可用 ClassName::methodName
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

>有且仅有一个抽象方法的接口

```java
可以在任意接口上使用注解 @FunctionalInterface，来检查是否是函数式接口（同 @overwrite）。
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
Stream 其实是一个集合元素的函数模型，它并不是集合，也不是数据结构，其本身并不存储任何元素（或其地址值）。
Stream 不会改变源对象。相反，它会返回一个持有结果集的新 Stream。
Stream 操作是延迟执行的。意味着它会等到需要结果时才执行。#详见Demo
```

```shell
#可以把 Stream 当成一个高级版本的 Iterator。

原始版本的 Iterator，用户只能一个一个的遍历元素并对其执行某些操作；
高级版本的 Stream，用户只要给出需要对其包含的元素执行什么操作，比如 "过滤掉长度大于10的字符串"、"获取每个字符串的首字母"等，
Stream 会隐式地在内部进行遍历，做出相应的数据转换。
```

## 创建流

> `创建流（转化数据源） --> 中间操作（定义中间操作链，但不会立即执行） --> 终止操作（执行中间操作链，并产生结果）`

```java
Person p1 = new Person(1, "zhao", 17, 197.5, Gender.MAN); //id,name,age,height,gender
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

>过滤

```java
list.stream()
        .filter(x -> {
            System.out.println("比较: " + x.age);
            return x.age > 17; //过滤条件
        })
        .limit(3) //结果集大小
        .skip(1) //跳过结果集中的前n个元素; 当n大于元素总数,返回空流.
        .distinct() //通过 hashCode() 和 equals() 去重
        .forEach(System.out::println);//终止操作 -> 最终结果集为 3-1 个

// 比较: 17 -> 不满足
// 比较: 18 -> 满足，但挑过
// 比较: 19 -> 满足，输出
// 比较: 20 -> 满足，输出
// ***: 21 -> 虽然满足，但是 limit(3) 以满足，则不再比较 
```

> 映射

```java
//map()入参为 ：函数型接口，有入有出。
<R> Stream<R> map(Function<? super T, ? extends R> mapper);

//peek()入参为：消费型接口，只有入参，没有出参。更多情况用于 debug 逻辑是否正确。
Stream<T> peek(Consumer<? super T> action);
```

```java
list.stream()
    .map(person -> {
        person.age += 5;
        return person;
    })
    .forEach(System.out::println);

list.stream().peek(person -> person.age += 5) //逻辑等同，互相替换
    .forEach(System.out::println);
```

```java
//map() 和 flatmap() 类比于： list.add() 和 addAll()

List<String> list0 = Arrays.asList("a", "b");

List list1 = new ArrayList<>();
list1.add("aa");

list1.add(list0); //list0作为list1中的一个元素。[aa, [a, b]]

list1.addAll(list0); //list0中的元素融入list1中。[aa, a, b]
```

>排序

```java
list.stream()
    //.sorted() //自然排序(调用compare()方法); 非自然排序(自定义排序方法)
    //.sorted((x, y) -> (x.name).compareTo(y.name)) //按名字排序
    //.sorted((x, y) -> Double.compare(x.height, y.height)) //按身高排序
    
    .sorted((x, y) -> { //先按年龄排序，再按性别排序
        if (x.age == y.age) {
            return x.gender.compareTo(y.gender);
        } else {
            return Integer.compare(x.age, y.age);
        }
    }).forEach(System.out::println);
```

##终止流

>从流的流水线生成结果，结果可以是非流的任何值。如：List，Integer，甚至void

```shell
短路操作  #forEach(); forEachOrdered(); collect(); toArray(); reduce(); min(); max(); count();
非短路..  #findFirst(); findAny(); allMatch(); anyMatch(); noneMatch();
```

>forEach 和 forEachOrdered

```sh
#forEach() 并不保证元素的逐一消费动作，在流中是被有序执行的
```

```java
String str = "my name is 007";

//并行流: 输出的顺序不一定(效率更高)
str.chars().parallel().forEach(x -> System.out.print((char) x)); //is 070 anemy m
//并行流: 输出的顺序与元素的顺序严格一致
str.chars().parallel().forEachOrdered(x -> System.out.print((char) x)); //my name is 007

//非并行流: forEach() == forEachOrdered() == parallel.forEachOrdered()
str.chars().forEach(x -> System.out.print((char) x)); //my name is 007
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

>收集

```sh
#collect(): 将流中元素转化为 -> <R, A> R collect(Collector<? super T, A, R> collector);

(1).Collector 接口定义了如何对流执行收集操作（如收集到List，Set，Map等）
(2).Collectors 实用类提供了系统实现的收集器实例
(3).类似：Executor，Executors； Collection<E>，Collections
```

```java
// toList(); toSet(); toCollection(); 
List<String> collect = list.stream()
        .map(Person::getName)
        .collect(Collectors.toList());//转化list

Long collect2 = list.stream()
        .filter(person -> person.age > 18)
        // .count()
        .collect(Collectors.counting());//计算流中元素的个数

//summingInt(); averagingInt(); summingDouble(); averagingDouble(); ...
int collect3 = list.stream()
        // .mapToInt(Person::getAge).sum();
        .collect(Collectors.summingInt(Person::getAge));//对流中元素的int属性求和

//summarizingInt(); summarizingDouble();
DoubleSummaryStatistics collect4 = list.stream()
        .collect(Collectors.summarizingDouble(Person::getHeight));
//收集流中Double属性的统计值.如: 元素个数, 总和, 最小值, 平均值, 最大值.
//DoubleSummaryStatistics包含属性: {count=5, sum=887.500000, min=157.500000, average=177.500000, max=197.500000}
System.out.println(collect4.getAverage());

//joining(): 连接流中每个字符串
//参数列表 -> delimiter: 连接符; prefix: 结果的前缀; suffix: 结果的后缀.
String collect5 = list.stream()
        .map(Person::getName)
        .collect(Collectors.joining("-", "(", ")"));//(zhao-qian-sui)

//maxBy(); minBy();
Optional<Person> collect6 = list.stream()
        // .max(Comparator.comparingDouble(Person::getHeight));
        .collect(Collectors.maxBy((x, y) -> Double.compare(x.getHeight(), y.getHeight())));

//reducing();
Optional<Double> collect7 = list.stream()
        .map(Person::getHeight)
        .collect(Collectors.reducing(Double::sum));

//参数列表 -> arg0: 初始值; arg1: 哪个属性; arg2: 求和操作.
Double collect8 = list.stream()
        .collect(Collectors.reducing(0.0, Person::getHeight, Double::sum));

//collectingAndThen(); 包裹另一个收集器,对其结果转换函数
Integer collect9 = list.stream()
        .collect(Collectors.collectingAndThen(Collectors.toList(), List::size));

//groupingBy();
Map<Gender, List<Person>> collect10 = list.stream()
        .collect(Collectors.groupingBy(Person::getGender));//分组: 性别

Map<Gender, Map<Integer, List<Person>>> collect12 = list.stream() //多级分组: 先性别,再年龄
        .collect(Collectors.groupingBy(Person::getGender, Collectors.groupingBy(Person::getAge)));

Map<Gender, Map<String, List<Person>>> collect13 = list.stream() //多级分组: 先性别,再年龄
        .collect(Collectors.groupingBy(Person::getGender, Collectors.groupingBy((x) -> {
            if (((Person) x).age < 18) {
                return "少年";
            } else if (((Person) x).age < 30) {
                return "青年";
            } else {
                return "中年";
            }
        })));
collect13.forEach((x, y) -> {
    System.out.println(x); //x -> MAN | WOMAN
    y.forEach((m, n) -> System.out.println(m + " - " + n)); // m -> 少年,青年，中年
});

//partitioningBy(); 根据条件进行分区
//false:[{"age":17...},{"age":18...}],true:[{"age":21...}]}
Map<Boolean, List<Person>> collect14 = list.stream()
        .collect(Collectors.partitioningBy(x -> x.age > 20));
```

##并行流

> 就是把一个数据集分成多个数据块，并用不同的线程分别处理每个数据块的流

```shell
#Fork/Join框架：在必要的情况下，先将一个大任务拆分(fork)成若干小任务，然后再将小任务运算结果进行 join 汇总。

#Fork/Join框架与传统线程池的区别
(1)."工作窃取"模式 (work-stealing)
当执行新任务时，它可以将其拆分成更小的任务执行，并将小任务加到线程队列中。
当某个线程的任务队列全都完成时，它会从一个随机线程的队列中偷一个放到自己的队列中。

(2).相对于一般的线程池实现，fork/join框架的优势体现在对其中包含任务的处理方式上。
在一般的线程池中，如果一个线程正在执行的任务由于某些原因无法继续运行，那么该线程会处于等待状态。
而在fork/join框架实现中，如果某个子问题由于等待另外一个子问题的完成而无法继续运行。
那么处理该子问题的线程会主动寻找其他尚未运行的子问题来执行。这种方式减少了线程的等待时间，提高了性能。
```
##结论

```shell
(1).所有操作是链式操作，一个元素迭代一次。
(2).每一个中间操作返回一个新的流，流里面有一个属性 sourceStage 指向同一个地方，即链表的头 Head。
(3).Head -> peek -> filter -> ... -> null

(4).有状态操作会把无状态操作截断，单独处理。先peek+filter，再sorted，最后peek
(5).有状态操作的入参为2个，无状态为1个

(6).并行环境下，有状态的中间操作不一定能并行操作
```

```java
long count = Stream.generate(() -> new Random().nextInt())
        .limit(50)
        .peek(x -> System.out.println("peek: " + x)) //无状态操作
        .filter(x -> {
            System.out.println("filter: " + x); //无状态
            return x > 1000;
        }).sorted((x, y) -> {
            System.out.println("sorted: " + x); //有状态
            return x.compareTo(y);
        }).peek(x -> System.out.println("peek: " + x)) //无状态
        .count();
```

# java7

> Switch新增支持 String 类型

```shell
switch 支持的'byte，short，char，int，enum'都可以隐式的转换成 int 类型。不支持 boolean。

另外，java7支持 String 类型，其实是通过调用'String.hashCode()'，将String转换为 int。
```

> 泛型实例化时的类型自动推断

```java
List<String> list = new ArrayList</*String*/>();
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
#如果对资源对象的处理和对资源对象的关闭均遭遇了异常，则关闭异常将被抑制，处理异常将被抛出。
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

