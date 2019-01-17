
//{--------------<<<lambda>>>-------------------------------------------------------
#lambda
	0.更新前
		List<String> list = Arrays.asList("java", "scala", "python");//数组转list
		list.forEach(new Consumer<String>() {

			@Override
			public void accept(String x) {
				System.out.println(x);
			}
		});
	(1).可省-参数类型; //可由编译器推断得出,称为"类型推断"
	(2).可省-参数括号; //当只有一个参数时
	(3).可省-方法体的大括号 和 return; //当lambda体只有一条语句
	
	1.更新后
		list.forEach(x -> System.out.println(x));
		list.forEach(System.out::println);//进一步更新(待讲)

	2.语法糖—底层还是匿名内部类
		匿名内部类使用同级别的成员变量,需要将变量定义为 final,lambda也是如此.
		只不过jdk-1.7 之前必须显示指定 final,1.8 以后则可省 final,由底层自动添加.
	
#方法引用
	定义: 当要传递给lambda体的操作,已经有方法实现了,可以直接使用方法引用!
	//使用操作符"::"将方法名和对象或类的名字分隔开来.
	//引用方法的参数列表 和 返回值,与函数式接口的一致,就可以方法引用
	
	1.类の静态方法
		Comparator<Integer> com0 = (x, y) -> Integer.compare(x, y);
		Comparator<Integer> com1 = Integer::compare;

	2.对象の非静态方法
		x -> System.out.println(x);
		System.out::println; //PrintStream ps = System.out;//对象
	
	3.类の实例方法
		//当lambda参数 arg0 是引用方法的调用者,arg1 是引用方法的参数(或无参数),可用 ClassName::methodName
		Comparator<Integer> com2 = (x, y) -> x.compareTo(y);
		Comparator<Integer> com3 = Integer::compareTo;
	
#构造器引用 ClassName::new
	与函数式接口相结合,自动与函数式接口中方法兼容.
	可以把构造器引用赋值给定义的方法,与构造器参数列表要与接口中抽象方法的参数列表一致.
	//需要调用的构造器参数列表,与函数式接口的参数列表保持一致
	
	1.无参构造器
		Supplier<Flower> supplier = () -> new Flower();
	
		Supplier<Flower> supplier = Flower::new;
		Flower flower = supplier.get();

	2.两个参数的构造器
		BiFunction<Integer, String, Flower> biFunction = (x, y) -> new Flower(x, y);
		
		BiFunction<Integer, String, Flower> biFunction = Flower::new;
		Flower flower = biFunction.apply(5, "55");
	
#数组引用 type[]::new
		//返回长度x的String数组
		Function<Integer, String[]> function = (x) -> new String[x];
		
		Function<Integer, String[]> function = String[]::new;
		String[] res = function.apply(10);
		

#函数式接口 -> 只包含一个抽象方法的接口	
	可以在任意接口上使用注解 @FunctionalInterface, 来检查是否是函数式接口.
	同时 javadoc 也会包含一条声明,说明这个接口是一个函数式接口.
	
#四大函数式接口
	1.消费型接口
		Consumer<T> { void accept(T t);	} //入参T类型,返回无
	
	2.供给型接口
		Supplier<T> { T get(); } //无入参,返回T类型

	3.函数型接口
		Function<T, R> { R apply(T t); } //入参T类型,返回R类型
		
	4.断定型接口
		Predicate<T> { boolean test(T t); } //入参T类型,返回boolean类型

//}		

//{--------------<<<Stream>>>-------------------------------------------------------
#Stream是数据渠道, 是用于操作数据源(集合,数组等)所生成的元素序列.
	//集合讲的是数据,Stream讲的是计算.
	1.Stream不会存储元素.
	2.Stream不会改变源对象. 相反,它会返回一个持有结果集的新Stream.
	3.Stream操作是延迟执行的. 意味着它会等到需要结果时才执行. //(详见Demo)
	
	///创建流(转化数据源) --> 中间操作(定义中间操作链,但不会立即执行) --> 终止操作(执行中间操作链,并产生结果)
	
#创建流
	Person p1 = new Person(1, "zhao", 17, 197.5, Gender.MAN);
	Person p2 = new Person(2, "qian", 18, 187.5, Gender.MAN);
	Person p3 = new Person(3, "sui", 19, 177.5, Gender.MAN);
	Person p4 = new Person(4, "li", 20, 167.5, Gender.WOMEN);
	Person p5 = new Person(5, "wang", 21, 157.5, Gender.WOMEN);
	List<Person> list = Arrays.asList(p1, p2, p3, p4, p5);

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

#中间
	1.筛选与切片
		惰性求值: 中间操作不会立即执行; 只有执行了终止操作(如forEach()),中间操作才会执行
		内部迭代: 迭代操作 forEach 是由Stream API自动完成
		短路操作: 以下 age=21 不打印,体现了短路操作.
		
		list.stream()
				.filter(x -> {
					System.out.println("比较");
					return x.age > 17; //过滤条件
				})
				.limit(3) //结果集大小
				.skip(1) //跳过结果集中的前n个元素; 当n大于元素总数,返回空流.
				.distinct() //通过 hashCode() 和 equals() 去重
				.forEach(System.out::println);//终止操作 -> 最终结果集为 3-1 个
				
		// 比较 (17 -> 不满足)
		// 比较 (18 -> 满足,但跳过)
		// 比较 (19 -> 满足,打印输出)
		// 比较 (20 -> 满足,打印输出)
		// 		(21 -> 短路操作,不比较) 
		
	2.映射
		list.stream()
				.peek(x -> x.age += 5) //区别 map() 和 peek()
				
				// .map(x -> {
				//     x.age += 5; //传入lambda,转换流中所有元素,产生新的元素
				//     return x;
				// })
				
				//.flatmap() //和map类似,但元素转换后得到新流, 结果是将子流中的元素压缩到父流中!!
				.forEach(System.out::println);
				
		(1).map()和peek()
			//map()入参: <R> Stream<R> map(Function<? super T, ? extends R> mapper);
			Function<Integer,String> f = x -> {return  "hello" + i;};
			
			//peek()入参: Stream<T> peek(Consumer<? super T> action);
			Consumer<Integer> c =  i -> System.out.println("hello" + i);
			
			/所以,Function 比 Consumer 多了一个 return.
		
		(2).map()和flatmap() //类比list的add()和addAll()
			List<String> list0 = Arrays.asList("a", "b");

			List list1 = new ArrayList<>();
			list1.add("aa");

			list1.add(list0); //list0作为list1中的一个元素
			System.out.println(list1); //[aa, [a, b]]

			list1.addAll(list0); //list0中的元素融入list1中
			System.out.println(list1); //[aa, a, b]
		
	3.排序
		list.stream()
				//.sorted() //自然排序(调用compare()方法); 非自然排序(自定义排序方法)
				
				//.sorted((x, y) -> (x.name).compareTo(y.name)) //按名字排序
				
				//.sorted((x, y) -> Double.compare(x.height, y.height)) //按身高排序
				
				.sorted((x, y) -> { //先按年龄排序,再按性别排序
					if (x.age == y.age) {
						return x.gender.compareTo(y.gender);
					} else {
						return Integer.compare(x.age, y.age);
					}
				}).forEach(System.out::println);

#终止 -> 从流的流水线生成结果,结果可以是不是流的任何值. 如:List,Integer,甚至void
	(1).短路操作 -> forEach(); forEachOrdered(); / collect(); toArray(); reduce(); / min(); max(); count();
	(2).非...... -> findFirst(); findAny(); / allMatch(); anyMatch(); noneMatch();
	
	1.forEach和forEachOrdered
        String str = "my name is 007";
		
        //并行流: 输出的顺序不一定(效率更高)
        str.chars().parallel().forEach(x -> System.out.print((char) x)); //is 070 anemy m
        //并行流: 输出的顺序与元素的顺序严格一致
        str.chars().parallel().forEachOrdered(x -> System.out.print((char) x)); //my name is 007

        //非并行流: forEach() == forEachOrdered() == parallel.forEachOrdered()
        str.chars().forEach(x -> System.out.print((char) x)); //my name is 007
		
	2.查找和匹配
		// allMatch / anyMatch / noneMatch(): 是否都满足 / 有一个满足 / 都不满足.
		boolean allMatch = list.stream().allMatch((x) -> x.age > 30);

		// findFirst(): 返回第一个元素. (Optional表示结果可能为空)
		// findAny(): 返回任意一个. (多用于并行流)
		Optional<Person> findFirst = list.stream().findFirst();

		// count(): 返回流中元素总个数
		long count = list.stream().count();

		// max(): 返回流中最大值. min(): 最小值
		Optional<Person> max = list.stream() //获取最高身高者的所有属性
				.max((x, y) -> Double.compare(x.height, y.height));

		Optional<Double> min = list.stream() //只获取最低身高的值
				.map(x -> x.height)
				.min(Double::compare);

	6.归约
		//reduce(): 将流中元素反复结合,最终生成一个值		
		Optional<Double> reduce = list.stream().map(x -> x.height).reduce((x, y) -> x + y);
		
		//有初始值,所以肯定不为空,即不用Optional<T>
		//参数列表 -> arg0: 初始值; arg1: 求和操作
		Double reduce2 = list.stream().map(Person::getHeight).reduce(1.0, Double::sum);

	6.收集
		//collect(): 将流中元素转化为 -> <R, A> R collect(Collector<? super T, A, R> collector);
		
		(1).Collector 接口定义了如何对流执行收集操作(如收集到List,Set,Map等).
		(2).但 Collectors 实用类提供了系统实现的收集器实例. 
		(3).类似: Executor,Executors; Collection<E>,Collections;
		
		// toList(); toSet(); toCollection(); 
		List<String> collect = list.stream().map(Person::getName).collect(Collectors.toList());//转化list
			
		Long collect2 = list.stream().collect(Collectors.counting());//计算流中元素的个数
		
		//summingInt(); averagingInt(); summingDouble(); averagingDouble(); ...
		int collect3 = list.stream().collect(Collectors.summingInt(Person::getAge));//对流中元素的int属性求和
		
		//summarizingInt(); summarizingDouble();
		DoubleSummaryStatistics collect4 = list.stream().collect(Collectors.summarizingDouble(Person::getHeight));
		//收集流中Double属性的统计值.如: 元素个数, 总和, 最小值, 平均值, 最大值.
		//DoubleSummaryStatistics包含属性: {count=5, sum=887.500000, min=157.500000, average=177.500000, max=197.500000}
		System.out.println(collect4.getAverage());,

		//joining(): 连接流中每个字符串
		//参数列表 -> delimiter: 连接符; prefix: 结果的前缀; suffix: 结果的后缀.
		String collect5 = list.stream().map(Person::getName).collect(Collectors.joining("-", "(", ")"));//(zhao-qian-sui)

		//maxBy(); minBy();
		Optional<Person> collect6 = list.stream()//根据比较器选择最大值
				.collect(Collectors.maxBy((x, y) -> Double.compare(x.getHeight(), y.getHeight())));

		//reducing();
		Optional<Double> collect7 = list.stream().map(Person::getHeight).collect(Collectors.reducing(Double::sum));
		//参数列表 -> arg0: 初始值; arg1: 哪个属性; arg2: 求和操作.
		Double collect8 = list.stream().collect(Collectors.reducing(0.0, Person::getHeight, Double::sum));
		
		//collectingAndThen(); 包裹另一个收集器,对其结果转换函数
		Integer collect9 = list.stream().collect(Collectors.collectingAndThen(Collectors.toList(), List::size));

		//groupingBy();
		Map<Gender, List<Person>> collect10 = list.stream().collect(Collectors.groupingBy(Person::getGender));//分组: 性别

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
		Map<Boolean, List<Person>> collect12 = list.stream().collect(Collectors.partitioningBy(x -> x.age > 20));
			
#parallelStream
	//并行流: 就是把一个数据集分成多个数据块,并用不同的线程分别处理每个数据块的流
	
	//Fork/Join框架: 在必要的情况下,先将一个大任务拆分(fork)成若干小任务,然后再将小任务运算结果进行 join 汇总.
	
	///Fork/Join框架与传统线程池的区别
	(1)."工作窃取"模式 (work-stealing)
		当执行新任务时,它可以将其拆分成更小的任务执行,并将小任务加到线程队列中.
		当某个线程的任务队列全都完成时,它会从一个随机线程的队列中偷一个放到自己的队列中.
	
	(2).相对于一般的线程池实现,fork/join框架的优势体现在对其中包含任务的处理方式上.
		在一般的线程池中,如果一个线程正在执行的任务由于某些原因无法继续运行,那么该线程会处于等待状态.
		而在fork/join框架实现中,如果某个子问题由于等待另外一个子问题的完成而无法继续运行.
		那么处理该子问题的线程会主动寻找其他尚未运行的子问题来执行.这种方式减少了线程的等待时间,提高了性能.
		
#Stream结论
	(1).所有操作是链式操作,一个元素迭代一次.
	(2).每一个中间操作返回一个新的流, 流里面有一个属性 sourceStage 指向同一个地方,即链表的头 Head.
	(3).Head -> peek -> filter -> ... -> null
	
	(4).有状态操作会把无状态操作 截断,单独处理. 先peek+filter; 再sorted; 最后peek
	(5).有状态操作的入参为2个, 无状态为1个
	
	(6).并行环境下,有状态的中间操作不一定能并行操作
	
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
	
	
	
//}	
	
//{--------------<<<时间API>>>--------------------------------------------------
#java8之前 java.util.Date 和 Calendar 的弊端
	首先, 所有属性都是可变的,且线程不安全. 【**最重要**】
	其次, 星期和月份从 0 开始计数.
	
		public static void main(String[] args) { //演示线程不安全.
			Calendar birth = Calendar.getInstance();
			birth.set(1995, Calendar.MAY, 26);
			Calendar now = Calendar.getInstance();
			System.out.println(daysBetween(birth, now)); // 输出结果为14963,值不固定
			System.out.println(daysBetween(birth, now)); // 输出结果显示 0?
		}

		//如果连续计算两个Date实例的话,第二次会取得0
		//因为Calendar状态是可变的,考虑到重复计算的场合,最好复制一个新的Calendar. 修改代码如下(注释部分)
		public static long daysBetween(Calendar begin, Calendar end) {
			long daysBetween = 0;
			//Calendar calendar = (Calendar) begin.clone(); //先拷贝一份
			//while (calendar.before(end)) {
			//	calendar.add(Calendar.DAY_OF_MONTH, 1);
			//	daysBetween++;
			//}
			while (begin.before(end)) {
				begin.add(Calendar.DAY_OF_MONTH, 1);
				daysBetween++;
			}
			return daysBetween;
		///}

#LocalDate+LocalTime+LocalDateTime ///类的实例是不可变的对象
	分别表示使用 ISO-8601 日历系统的日期,时间,日期和时间. 人读的时间(非时间戳).
	它们提供了简单的日期或时间,并不包含当前的时间信息.也不包含与时区相关的信息。
	
	//LocalDate, LocalTime, LocalDateTime 分别表示本地日期(年月日),时间(时分秒),及时间日期
	//三者的API使用方式类似,下面以 LocalDateTime 为例介绍

	//通过 now(); parse(); of(); 三种静态方法获取实例对象
	LocalDateTime ldt0 = LocalDateTime.now(); //2018-10-19T20:36:26.216
	LocalDateTime ldt1 = LocalDateTime.parse("2018-01-02T20:58:30.123");//必须有T
	LocalDateTime ldt2 = LocalDateTime.of(2016, 10, 26, 12, 10, 55, 255 * 1000 * 1000); //最后参数为纳秒

	//增减操作: 分别使用 plus 和 minus 关键字
	//plusDays(n); plusWeeks(n);... plus(Period.ofDays(n)); plus(Period.ofWeeks(n));...
	LocalDateTime plusDays = ldt0.plusDays(2);
	LocalDateTime plusDays = ldt0.plus(Period.ofDays(2));
	
	//获得 年,月,日...,年份天数(1-366),月份天数(1-31),星期几(DayOfWeek枚举值)
	int monthValue = ldt0.getMonthValue();
	int dayOfYear = ldt0.getDayOfYear(); //int类型293
	DayOfWeek dayOfWeek = ldt0.getDayOfWeek();
	System.out.println(dayOfWeek + " * " + dayOfWeek.getValue()); //DayOfWeek类型: SATURDAY * 6
	
	//将月份天数,年份天数,月份,年份 修改为指定值,并返回新的 LocalDate 对象
	LocalDateTime withDayOfYear = ldt0.withDayOfYear(2); //2018-01-02T09:49:05.637
	LocalDateTime withYear = ldt0.withYear(1990); //1990-10-20T09:49:05.637
	
	//until(); 获取两个LocalDate的相差天数
	LocalDateTime target = LocalDateTime.parse("2018-12-31T23:59:59");
	long until = ldt0.until(target, ChronoUnit.DAYS);//72, 若将ldt0和target调换,则返回负数
	
	//isBefore(); isAfter(); 比较两个日期的先后顺序
	boolean before = ldt0.isBefore(target);
	
	//isLeapYear(); LocalDate特有方法,判断是否是闰年
	boolean leapYear = LocalDate.now().isLeapYear();
		
#Instant ///时间戳对象, 以Unix元年(1970年1月1日0点)开始所经历的描述进行运算.
	
	//通过now()静态方法获取对象
	Instant instant = Instant.now();//默认获取 UTC 时区时间戳,北京是 UTC+8
	OffsetDateTime atOffset = instant.atOffset(ZoneOffset.ofHours(8));//北京时间戳
	
	long epochMilli = instant.toEpochMilli();//毫秒, 等同于 System.currentTimeMillis()
	long epochSecond = instant.getEpochSecond();//秒
		
#Duration+Period ///分别表示时间间隔的两个维度. 前者用秒和纳秒来区分时间,后者用年月日.
	//between();
	Duration duration = Duration.between(LocalDateTime.parse("2018-02-12T22:30:10"),
			LocalDateTime.parse("2018-02-20T22:58:20"));
	System.out.println(duration.getSeconds());//时间间隔n秒		
	System.out.println(duration.toDays() + " * " + duration.toHours() + " * " + duration.toMinutes() + " * "
			+ duration.toMillis());//时间间隔分别转化为 日,时,分,秒

	Period period = Period.between(LocalDate.parse("2018-02-18"), LocalDate.parse("2019-05-14"));
	System.out.println(period.getYears() + " * " + period.getMonths() + " * " + period.getDays());//年月日
		
#TemporalAdjuster
	///时间校正器. 提供了日期操纵的接口. 如:将日期调整到"下个周末"
	///TemporalAdjusters是系统提供的接口实现类. 类似 Excutor,Excutors
	
	LocalDateTime ldt = LocalDateTime.now();//今天周六
	LocalDateTime with0 = ldt.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));//返回今天日期
	LocalDateTime with1 = ldt.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));//返回下周六日期
	
	LocalDateTime with2 = ldt.with(TemporalAdjusters.lastInMonth(DayOfWeek.SATURDAY));//本月最后一个周六日期
	LocalDateTime with3 = ldt.with(TemporalAdjusters.lastDayOfMonth());//本月最后一天
		
#DateTimeFormatter ///格式化
	// String转日期
	LocalDateTime ldt0 = LocalDateTime.parse("2018-12-02T16:46:48.154", DateTimeFormatter.ISO_LOCAL_DATE_TIME); //有个T
	LocalDateTime ldt1 = LocalDateTime.parse("2018-12-02 16:46:48.154",
			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));

	// 日期转String
	String formatDate0 = ldt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);// 2018-10-20T14:23:29.578
	String formatDate1 = ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));// 2018-10-20 14:23:29.578
		
#与传统日期处理的转换 ///遗留类.from(); 遗留类.to新类();
	Date date = Date.from(instant);// Sat Oct 20 14:35:01 CST 2018
	Instant instant = date.toInstant();// 2018-10-20T06:35:01.958Z
	
	Timestamp timestamp = Timestamp.from(instant);//2018-10-20 14:52:12.611
	Instant instant2 = timestamp.toInstant();//2018-10-20T06:52:12.611Z

//}
	
//{--------------<<<接口变动>>>-----------------------------------------------------
#接口
	以前接口中只允许存在: 全局常量 和 抽象方法;
	java8新增 默认方法 和 静态方法(jdk7???).
		
		@FunctionalInterface
		public interface ITest {
			public static final String NAME = "D";

			void sayHi();

			default void sayHello() { //jdk8新增默认方法
				System.out.println("hello ITest");
			}

			static void sayHah() { //
				System.out.println("hah ITest");
			}
		}
		
	1.类优先原则
		//当 父类Tester 和 父接口ITestor 都实现了 sayHello() 方法,子类调用 父类Tester 实现.		
		public class Test extends Tester implements ITest {}
		new Test().sayHi();//hi Tester
		
	2.接口冲突
		//当实现多个接口,且每个接口中都有同名default实现方法,就会报错. 
		//必须手动选择一个接口的default方法作为实现.
		
		public class Test /* extends Tester */ implements ITest0, ITest1 {
			@Override
			public String sayHi() {
				return ITest0.super.sayHi(); //选择ITest0
			}
		}
//}
		
//{--------------<<<Optional>>>-----------------------------------------------------
#Optional
	//Optional<T> 是一个容器类,代表一个值存在或不存在.
	//原来用 null 表示一个值不存在,现在 Optional 可以更好的表达这个概念.并且可以避免空指针异常.
		
		Optional<Person> optional0 = Optional.of(p);// 实例化
		Optional<Person> optional1 = Optional.empty();// 空实例一个空对象
		Optional<Person> optional2 = Optional.ofNullable(p);// 若p不为 null,创建Optional实例,否则创建空实例
		
		if (optional0.isPresent()) {// 判断是否包含值
			Person person = optional0.get();// 获取容器中的值
		}

		Person person = optional1.orElse(p1);// 容器中有值则返回,无值则取值p1(类似默认值).

		Person person1 = optional1.orElseGet(() -> new Person(2, "1", 18, 1.82, Gender.MAN));// lambda

		// 如果有值对其处理,并返回处理后的Optional. 否则返回 Optional.empty()
		Optional<Double> map = optional0.map((x) -> x.height);	
//}	
		
//{--------------<<<jdk7>>>---------------------------------------------------------
#Switch()中可以使用字串	

#泛型实例化的类型自动推断
		List<String> list = new ArrayList<>();
		
#try-with-resources释放资源
	//资源对象在程序结束之后必须关闭. twr语句确保了在语句的最后每个资源都会被关闭.
	
		try(){ }catch(){ }finally{ }
	
	(1).任何实现了'java.lang.AutoCloseable'和'java.io.Closeable'的对象, 在出了 try 大括号范围之后,都会自动关闭.
		所以, 任意 catch 或者 finally 块都是在 {资源被关闭以后才运行的}.
	
		//jdk7之前
		public static String doIo(String path) throws IOException {
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

		//jdk7之后
		public static String doIo7(String path) throws IOException {
			try (BufferedReader br = new BufferedReader(new FileReader(path))) {
				return br.readLine();
			}
		}
		
		//jdk9之后
		public static String doIo8(String path) throws IOException {
			BufferedReader br = new BufferedReader(new FileReader(path));
			try (br) {
				return br.readLine();
			}
		}
	
	(2).异常抑制
		//如果"对外部资源的处理"和"对外部资源的关闭"均遭遇了异常, 则"关闭异常"将被抑制, "处理异常"将被抛出,
		//但"关闭异常"并没有丢失, 而是存在"处理异常"的被抑制的异常列表中. 
		//通过异常的getSuppressed()方法, 可以提取出被抑制的异常.
	
//}	
	

//{--------------<<<HashMap>>>---------------------------------------------------------

#策略设计模式
	//1.过滤出list中长度>5的
	//2.过滤出list中包含a的
	//3....N多情况...
	List<String> list = Arrays.asList("java", "scala", "python");

	public interface Filterable<T> {//策略接口
		boolean filter(T t);
	}

	public List<String> filterList(List<String> list, Filterable<String> filter) {
		List<String> res = new ArrayList<>();
		for (String str : list) {
			if (filter.filter(str)) { //使用策略
				res.add(str);
			}
		}
		return res;
	}

	//lambda前
	List<String> res = filterList(list, new Filterable<String>() {

		@Override
		public boolean filter(String t) {
			return t.length() > 3; //不同过滤条件,不同策略
		}
	});
	res.forEach(t -> System.out.println(t));

	//lambda后
	List<String> res = filterList(list, t -> t.length() > 3);
	res.forEach(System.out::println);

//}

