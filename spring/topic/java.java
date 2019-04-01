


//{--------<<<基础>>>------------------------------
#访问级别修饰符
		-		同一个类	同一个包	不同包的子类	不同包的非子类
	private		√
	default		√			√
	protected	√			√			√
	public		√			√			√				√

#Object.equals()方法
    public boolean equals(Object obj) {
        if (this == obj) //入参对象和当前对象引用一样,则肯定是同一个对象实例, true
            return true;

        if (obj == null) //入参对象为null, false
            return false;

        if (getClass() != obj.getClass()) //入参对象和当前对象类型不一样,肯定不相等, false
            return false;

        Dog other = (Dog) obj; //至此,入参对象和当前对象类型一样,直接强转

        if (age != other.age) //先比较age属性,不一样就返回 false
            return false;

        //再检查name属性, 由于name是字符串,不能直接用==比较.
        //这里之所以没有直接 name.equals(other.name) 是因为如果name为null,会引起NullPointerException
        if (name == null) {
            if (other.name != null) //this.name为null,而入参.name却不为null,肯定不相等, false
                return false;
        } else if (!name.equals(other.name)) //this.name不为null,可直接调用String的equals()判断两个name是否一样
            return false;

        return true; //前面都满足,则说明两个对象相等 true
    }

    public boolean equals1(Object o) { //上述方法简化版
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Dog dog = (Dog) o;
        return age == dog.age &&
                Objects.equals(name, dog.name);
    }

#成员变量和属性
	//一般情况下,成员变量和属性名相同.
	public class Flower {
		public String name; //name: 成员变量

		public void setName(String name) { //setName中的name: 属性名
			this.name = name;
		}
	}
	
#replace - replaceAll - replaceFirst
    //(1).replace()参数是'char'和'CharSequence'; 即支持'字符和字符串'的替换
    //(2).replaceAll()参数是'regex'; 即基于'正则表达式'的替换; ---> 可通过replaceAll("\\d", "*")把所有的数字替换成星号;
    //(3).replaceFirst()参数也是'regex'; 但不同的是只替换第一个,以上是全部替换
    private void doReplace() {
        String src = "aa333";
        System.out.println(src.replace('a', 'f')); //ff333
        System.out.println(src.replace("a", "f")); //ff333
        System.out.println(src.replaceAll("\\d", "f")); //aafff
        System.out.println(src.replaceAll("a", "f")); //ff333
        System.out.println(src.replaceFirst("\\d", "f")); //aaf33
        System.out.println(src.replaceFirst("a", "f")); //fa333
    }

//}

//{--------<<<Reflect>>>---------------------------
#基础
	java三大特性: 封装; 继承; 多态.
	java核心思想: 面向对象 --> 万事万物皆对象.
	
#创建对象
	1.显示创建 //传统方式; 必须预先知道要使用的类 -> 引用类改变,就必须修改源码
        Person person = new Person(true);
		
	2.Cloneable //克隆
		public class Person implements Cloneable{
			@Override
			protected Object clone() throws CloneNotSupportedException {
				return super.clone();
			}
		}
        Person clone = (Person) person.clone(); //clone();		
		
	3.反射方式 //动态创建; 效率相对低下,传统方式的3倍
        Class<?> clazz = Class.forName("com.example.reflect.Person"); //1
        Class<?> clazz = this.getClass().getClassLoader().loadClass("com.example.reflect.Person"); //2
		Class<? extends clazz> person03 = new Person().getClass(); //3
		Class<Person> clazz = Person.class; //4
		
		//先获取 clazz 对象(4种方式,1&2常用), 再创建此 clazz 对象所表示的类的一个新实例
		Object instance = clazz.newInstance();

#DEMO
	public class Person {
		public static String city = "SX";
		public boolean gender;
		protected String name;
		int age;
		private boolean young;
	}
			
#构造器
	1.所有构造器
        Constructor<?>[] constructors = clazz.getConstructors(); //public
        Constructor<?>[] constructors = clazz.getDeclaredConstructors(); //all(private + public +protected + default)

	2.无参构造器
        Constructor<?> constructor = clazz.getConstructor();
        Person p9 = (Person) constructor.newInstance(); //等同于 new Person();
	
	3.有参构造器
        Constructor<?> constructor = clazz.getConstructor(boolean.class);
        Person p8 = (Person) constructor.newInstance(true); // new Person(true);
		
#属性
	1.所有属性
		Field[] fields = clazz.getFields(); //public
		Field[] fields = clazz.getDeclaredFields(); //all
	
	2.具体属性
		Field gender = clazz.getField("gender"); //public boolean gender;
		Field name = clazz.getDeclaredField("name"); //protected String name;
	
	3.属性操作
		Field city = clazz.getDeclaredField("city"); //static -> 不依赖对象,传参 null
        city.set(null, "BJ");
        System.out.println(city.get(null));

		Field gender = clazz.getDeclaredField("gender"); //non static --> 依附于对象
        Object p1 = clazz.newInstance(); 
        gender.set(p1, true);
        System.out.println(gender.get(p1));
		
        Field young = clazz.getDeclaredField("young"); //private --> 依附于对象,并暴力访问
        Object p2 = clazz.newInstance();
        young.setAccessible(true); //暴力膜
        young.set(p2, true);
        System.out.println(young.get(p2));
		
#方法
	1.所有方法
        Method[] methods = clazz.getMethods(); //同上
        Method[] methods = clazz.getDeclaredMethods();

	2.无参 public static
        Method staticHello = clazz.getMethod("staticHello");
		Object invoke = staticHello.invoke(null); //invoke为返回值; 调用 --> 不依赖对象
		
	3.无参 private ///依赖对象+暴力膜
        Method privateHello = clazz.getDeclaredMethod("privateHello");
        privateHello.setAccessible(true);
        Object p0 = clazz.newInstance();
        System.out.println("privateHello: " + privateHello.invoke(p0));
		
	4.有参 private ///依赖对象+暴力膜
        Method privateHello1 = clazz.getDeclaredMethod("privateHello", String.class, int.class);
        privateHello1.setAccessible(true);
        Object p3 = clazz.newInstance();
        System.out.println("privateHello1: " + privateHello1.invoke(p3, "SSS", 888));
		
#特殊方法 main()
	//怎样传递参数?
	//按照jdk1.5,,整个数组是一个参数; jdk1.4数组中的每一个元素是一个参数,
	//把一个字符串数组作为参数传递到 invoke(),jvm怎么解析?
	//1.5肯定没问题; 1.4则会将字符串数组打散成一个个字符串作为参数,出现参数个数异常
	
	//正确做法: jvm不会将参数作为数组处理,也不会将数组打散为若干个字符串...
	
        Class<?> clazz = this.getClass().getClassLoader().loadClass("com.example.reflect.Person");
        Method helloArray = clazz.getMethod("main", String[].class); //参数类型: String[].class

        helloArray.invoke(null, (Object) new String[]{"aaa", "bbb"}); //正确1
        // helloArray.invoke(null, new Object[]{new String[]{"aaa", "bbb"}}); //正确2

        // helloArray.invoke(null, new String[]{"aaa", "bbb"}); //错误写法

#性能问题
	反射相对传统方式,性能下降,约3倍速度.
	
		//是否取消访问时的安全检查 -> true,不仅可以访问级别 private,还可以提高反射的运行速度.
		setAccessible(true);
	
#反射操作泛型
	public class Demo04 {
		public void test01(Map<String, Person> map, List<Person> list) {
			System.out.println("Demo04.test01()");
		}

		public Map<Integer, Person> test02() {
			System.out.println("Demo04.test02()");
			return null;
		}

		@Test //获得指定方法参数的泛型信息
		public void paramsType() throws NoSuchMethodException {
			Method method = Demo04.class.getMethod("test01", Map.class, List.class);
			Type[] types = method.getGenericParameterTypes();

			for (Type paramType : types) {
				System.out.println("参数: " + paramType);

				if (paramType instanceof ParameterizedType) {
					Type[] genericTypes = ((ParameterizedType) paramType).getActualTypeArguments();
					for (Type genericType : genericTypes) {
						System.out.println("参数,泛型类型：" + genericType);
					}
					System.out.println();
				}
			}
			// 参数: java.util.Map<java.lang.String, com.example.reflect.Person>
			// 参数,泛型类型：class java.lang.String
			// 参数,泛型类型：class com.example.reflect.Person
			//
			// 参数: java.util.List<com.example.reflect.Person>
			// 参数,泛型类型：class com.example.reflect.Person
		}

		@Test //获得指定方法返回值泛型信息
		public void returnType() throws NoSuchMethodException {
			Method m2 = Demo04.class.getMethod("test02");
			Type returnType = m2.getGenericReturnType();

			if (returnType instanceof ParameterizedType) {
				Type[] genericTypes = ((ParameterizedType) returnType).getActualTypeArguments();

				for (Type genericType : genericTypes) {
					System.out.println("返回值,泛型类型：" + genericType);
				}
			}
			// 返回值,泛型类型：class java.lang.Integer
			// 返回值,泛型类型：class com.example.reflect.Person
		}
	}

//}

//{--------<<<Annotation>>>------------------------
#概念
	1.基础概念
		注解不是程序本身,可看作注释.	-> 这一点和普通注释没区别.
		可被其他程序(如: 编译器)读取.	-> 又区别于注释.

	2.常见注解
		@Override, @Deprecated, @SuppressWarnings("*") //用于抑制编译器产生警告信息
		
		@SuppressWarnings("unchecked") //抑制单类型的警告
		@SuppressWarnings(value={"unchecked", "rawtypes"}) //多类型
		@SuppressWarnings("all") //所有类型
	
#自定义注解
	#使用 @interface 自定义注解(接口)时,自动继承 java.lang.annotation.Annotation. <---> 接口 extends 接口
	
	#元注解 ---> 负责注解其他注解.	
	#@Target: 描述注解的使用范围
		ElementType.TYPE		-> //Class, interface (including annotation type), enum
		ElementType.FIELD		-> //Field (includes enum constants)
		ElementType.METHOD		-> //Method 
		ElementType.PARAMETER	-> //parameter
		
	#@Retention: 需要在什么级别保留该注解信息
		RetentionPolicy.SOURCE	-> //源文件中保留.
		RetentionPolicy.CLASS	-> //class文件中保留.
		RetentionPolicy.RUNTIME	-> //运行时保留,可被反射机制读取.(常用)

	1.自定义注解
		@Target(ElementType.TYPE)
		@Retention(RetentionPolicy.RUNTIME)
		public @interface Table {
			String value();
		}
		
		@Target(ElementType.FIELD)
		@Retention(RetentionPolicy.RUNTIME)
		public @interface Column {
			String name();

			String type() default "varchar"; 

			int length();
		}
		
	2.使用自定义
		@Table("t_student") //等同于 @Table(value = "t_student"); 只需要设置单个属性,且属性名为 value,则可省
		public class Student {
			@Column(name = "s_name", /*type = "varchar",*/ length = 10) //有默认值,则可不指定
			private String name;

			@Column(name = "s_age", type = "int", length = 3)
			private int age;
		}

	3.反射读取
		@Test
		public void Test01() throws Exception {
			Class<?> clazz = Class.forName("com.example.annotation.Student");

			Table table = clazz.getAnnotation(Table.class);
			System.out.println(table.value()); //t_student

			Field[] declaredFields = clazz.getDeclaredFields();
			Arrays.stream(declaredFields).forEach(x -> {
				Column column = x.getAnnotation(Column.class);

				// name: s_name,varchar,10
				System.out.println(x.getName() + ": " + column.name() + "," + column.type() + "," + column.length());
			});
		}

//}

//{--------<<<动态编译>>>--------------------------
#动态编译
		@Test
		public void Test02() {
			JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
			//args: 标准输入, 标准输出, 标准失败输出
			int run = javaCompiler.run(null, null, null, "G:\\java\\hello.java");
			System.out.println(0 == run ? "编译成功" : "编译失败");
		}

#动态调用class文件(编译好的类)
	1.通过—Runtime
		@Test
		public void Test02() throws IOException {
			//-cp: 指定class文件路径 --> 目录'G:\java'下的 Hello.class
			Process process = Runtime.getRuntime().exec("java -cp g:/java Hello");

			InputStream inputStream = process.getInputStream(); //执行结果打印
			String res = IOUtils.toString(inputStream, Charset.forName("UTF-8"));
			System.out.println(res);
		}
	2.通过反射—ClassLoader
		@Test
		public void Test02() throws Exception {
			URL[] urls = {new URL("file:/G:/java/")};
			URLClassLoader classLoader = new URLClassLoader(urls);
			Class<?> clazz = classLoader.loadClass("Hello");
			
			Method main = clazz.getDeclaredMethod("main", String[].class);
			Object invoke = main.invoke(null, (Object) new String[]{}); //调用 main()
			System.out.println(invoke); //null
		}

//}

//{--------<<<ClassLoader>>>-----------------------
https://blog.csdn.net/w1196726224/article/details/54428493

#类加载过程
	#JVM把class文件加载到内存,并对数据进行校验,解析和初始化,最终形成'JVM可以直接使用的Java类型'的过程
		加载 --> 链接(验证,准备,解析) --> 初始化 --> 使用 --> 卸载
		
	1.加载
		将class字节码加载到内存中(/*需要类加载器参与*/),并将这些静态数据转换成方法区中的运行时数据结构,
		在堆中生成一个代表这个类的 java.lang.Class 对象,作为方法区类数据的访问入口.
		
	2.链接
		将Java类的二进制代码合并到JVM的运行状态之中的过程
		(1).验证: //确保加载的类信息符合JVM规范,没有安全方面的问题
		(2).准备: //正式为类变量(static变量)分配内存,并设置类变量默认值的阶段,这些内存都将在方法区中进行分配
		(3).解析: //虚拟机常量池内的符号引用替换为直接引用(内存地址)的过程
		
	3.初始化
		执行'类构造器<clinit>()方法'的过程.
		类构造器<clinit>()方法: 编译器自动为所有类变量赋值,执行静态语句块(static块)的过程.
		
		当初始化一个类的时候,如果发现其父类还没有进行过初始化,则需要先出发其父类的初始化
		虚拟机会保证一个类的<clinit>()方法在多线程环境中被正确'加锁和同步'
		
	0.区别 准备&初始化
		对于类变量 public static int NUM = 55;
		准备	-> //为 NUM 分配内存,并设置默认值,即 int 的初始值 0.
		初始化	-> //为 NUM 赋值,即 55.
		
	#总结: new Person(),先初始化 Person 类的 static 变量,再执行 static {}代码块,最后才调用构造函数.

//}

//{--------<<<运算符>>>----------------------------



#位运算 ---> (int, long, short, char, byte)
	1.按位左右移运算 
		//左移n位,相当于乘以2^n, 右移相当于除以
		byte aByte = (1115 >> 8) & 0xFF;
		byte aByte = (1115 / 256) & 0xFF;

	2.异或运算
		& 如果相对应位都是1,  则结果为1, 否则为0
		| 如果相对应位都是0,  则结果为0, 否则为1
		^ 如果相对应位值相同, 则结果为0, 否则为1
		
		~ 按位取反运算符, 翻转操作数的每一位, 即0变成1, 1变成0
		
	3.DEMO	
		public static void main(String[] args) {
			int a = 60; /** 60 = 0011 1100 */
			int b = 13; /** 13 = 0000 1101 */
			int c = 0;
			
			c = a & b;       /** 12 = 0000 1100 */
			System.out.println("a & b = " + c);

			c = a | b;       /** 61 = 0011 1101 */
			System.out.println("a | b = " + c);

			c = a ^ b;       /** 49 = 0011 0001 */
			System.out.println("a ^ b = " + c);

			c = ~a;          /** -61 = 1100 0011 */
			System.out.println("~a = " + c);

			c = a << 2;     /** 240 = 1111 0000 */
			System.out.println("a << 2 = " + c);

			c = a >> 2;     /** 15 = 1111 */
			System.out.println("a >> 2  = " + c);

			c = a >>> 2;     /** 15 = 0000 1111 */
			System.out.println("a >>> 2 = " + c);
		}

1.运算符
	取模运算(%求余数): 左小右则取左; n%p结果正负由被除数n决定,与p无关. 如: 7%4= 3; 7%-4= 3; -7%4= -3; -7%-4= -3
	
	异或运算: 左右相同为false, 左右不同为true!
	
		"&"和"&&","|"和"||"の区别：'后者&& ||皆为短路运算'
			& |		---> 左无论真假,右都进行运算
			&& ||	---> 左为真(假),右参与运算; 左为假(真),则右不参与运算


//}

//{--------<<<Convert>>>---------------------------
#int <-> byte[]
	1.大端模式 //高位在前,低位在后
		public static byte[] int2Bytes(int value, int len) {
			if (len > 4) throw new RuntimeException("int 最大长度4个字节");
			
			byte[] bytes = new byte[len];
			for (int i = 0; i < len; i++) {
				bytes[i] = (byte) ((value >> 8 * (len - 1 - i)) & 0xFF);
			}
			return bytes;
		}
	
		//offset: 从数组的第offset位开始
		public static int bytes2Int(byte[] bytes) {
			byte[] dest = new byte[4];
			System.arraycopy(bytes, 0, dest, 4 - bytes.length, bytes.length);
			return (dest[0] & 0xFF) << 24
					| ((dest[1] & 0xFF) << 16)
					| ((dest[2] & 0xFF) << 8)
					| (dest[3] & 0xFF << 0);
		}
	
	2.小端模式 //低位在前,高位在后	
		public static byte[] int2Bytes(int value, int len) {
			if (len > 4) throw new RuntimeException("int 最大长度4个字节");
			
			byte[] bytes = new byte[len];
			for (int i = 0; i < len; i++) {
				bytes[i] = (byte) ((value >> 8 * i) & 0xFF);
			}
			return bytes;
		}
		
		public static int bytes2Int(byte[] bytes, int offset) {
			return (bytes[offset + 0] & 0xFF)
					| ((bytes[offset + 1] & 0xFF) << 8)
					| ((bytes[offset + 2] & 0xFF) << 16)
					| ((bytes[offset + 3] & 0xFF) << 24);
		}
	
#int <-> Hex
		public static String int2Hex(int value) {
			return Integer.toHexString(value);
		}
		
		private static int hex2Int(String hexString) {
			return Integer.parseInt(hexString, 16);
		}
		
#String <-> Hex
		public static String string2Hex(String value) {
			StringBuilder hexString = new StringBuilder();
			for (char aChar : value.toCharArray()) {
				hexString.append(Integer.toHexString(aChar));
			}
			return hexString.toString();
		}
		
#String <-> byte[]
        byte[] bytes = "hello".getBytes(Charset.forName("utf-8"));
		
        String s = new String(bytes, Charset.forName("utf-8"));

#校验和	
		/**
		 * 第13位 -> 校验和 -> 前面所有字节的异或
		 */
		data[13] = data[0];//校验和->13
		for (int i = 1; i < 13; i++) {
			data[13] = (byte) (data[13] ^ data[i]);
		}
	
#数组拷贝

		/**
		 * 拷贝数组
		 *
		 * @param src     源数组
		 * @param srcPos  源数组要复制的起始位置
		 * @param dest    目的数组
		 * @param destPos 目的数组放置的起始位置
		 * @param length  复制的长度
		 */
		public static void copyArray(Object src, int srcPos, Object dest, int destPos, int length) {
			System.arraycopy(src, srcPos, dest, destPos, length);
		}

//}

//{--------<<<hello>>>-----------------------------

//}

//{--------<<<idea-快捷键>>>-----------------------
#快捷键
	psvm, sout, fori, iter, Ctrl+Alt+T	///main(),syso,for(i),foreach(),try/catch
	
	
	Ctrl+P				//参数提示
	Ctrl+Alt+V | M		//抽取变量(方法)
	
	F3 | F3+Shift		//跳到下(上)一个选择项
	F7+Alt | F7+Ctrl	//在全局(当前类)查找方法调用,可配合F3使用
	Ctrl+H				//查看类的继承关系
	Alt+7				//查看类结构
	Ctrl+Shift+Alt+N	//当前工作空间查找java文件
	
	Ctrl+G				//定位行
	Ctrl+Shift+BackSpace	//返回上次修改
	
	Shift+Alt+↑/↓		//上下移动-单行语句
	Shift+Ctrl+↑/↓		//上下移动-整个方法
	Ctrl+D				//整行复制
	Ctrl+X				//整行删除
	Ctrl+Shift+U		//大小写转化
	
#技巧	
	//复制警告或者错误信息
	鼠标光标悬浮在报错的地方,待错误提示出现后,键盘按住 Alt,同时点击鼠标左键, Ctrl+V 到度娘即可
	
	//men打包时,跳过Test
	打开右侧标签页'Maven-Project' --> 当前项目'Lifecycle' --> 选中'Test' --> 菜单栏的'闪电'
	
//}

//{--------<<<idea-设置>>>-------------------------
	//黑色主题
	Appearance & Behavior - Appearance - Theme(选为Darcula) 
	勾选 Override default fonts by(......) - Name(Mircrosoft Yahei UI) - Size(12) //更改设置界面的字体大小,非代码字体
		
	//改变代码的字体和大小
	Editor - Colors & Fonts
	//首先,点击 Save As...,自定义一个名为 skyl 的样式
	//然后,选择具体的字体和大小 Primary font(Source Code Pro) - Size(15)
		
	//缩进采用4个空格,禁止使用tab字符
	Editor - Code Style - java - Tabs and Indents - Use tab character(取消勾选)
		
	//自动换行
	Editor - Code Style - Java
	右侧标签 Wrapping and Braces, (√) Line breaks 和 (√) Ensure right margin is not exceeded
		
	//悬浮文档提示
	Editor - General - Show quick documentation on...
		
	//代码提示忽略大小写
	Editor - General - Code Completion - Case sensitive...(None)
	//代码补全快捷键: Ctrl + Alt + Space
		
	//编码格式
	Editor - File Encodings - 3个UTF-8

	//显示行号等
	Editor - General - Appearance //勾选以下
		(√)Show line number(行号) + (√)Show right margin(右边线) + (√)Show method sep...(方法分割线)

	//自动导包
	Editor - General - Auto Import 
		Insert imports...(All) + (√)Add unambiguous... + (√)Optimize imports...
		
	//设置文件和代码的模板
	Editor - File and Code Templates - Includes - 自行添加
	
	//取消单行显示tabs
	Editor - General - Editor Tabs - (X)show tabs in single...
		
	//自动编译
	Build,Exe... - Compiler - (√)Build project automatically
		
	//Gradle配置
	Build,Exe... - Build Tools - Gradle - Offline work
	
//}	


//{--------<<<Eclipse-快捷键>>>--------------------
	
	Ctrl+1		//快速修复(最经典的快捷键,就不用多说了)
	Alt+/      ///代码补全
	F2			//查看方法说明	
	Ctrl+左键	//进入方法内部
	
	Alt+Shift+L	//抽取本地变量
	Alt+Shift+M	//........方法
	Alt+Shift+R	//重命名

	Ctrl+Shift+O	//自动导包
	Ctrl+/		   ///注释当前行,再按则取消注释
	Ctrl+D			//删除当前行
	
	Ctrl+T			//快速显示当前类的继承结构
	Ctrl+Alt+H		//查看函数的调用
	
	Ctrl+Alt+↓		//复制当前行到下一行(复制增加)
	Alt+↓			//当前行和下面一行交互位置

//}


//{--------<<<IO>>>--------------------------------
#IO操作
	输入流,输出流. <---> 字节流,字符流. <---> InputStream, OutputStream <---> Reader, Writer
	//注意: 在 finally 代码中调用 close() 对流进行关闭.
	
	0.区别: flush(); close();
		//A: close() 关闭流对象,但是先刷新一次缓冲区,关闭之后,流对象将不可用
		//B: flush() 仅仅是刷新缓冲区(一般写字符时要用,因为字符是先进入的缓冲区),流对象还可以继续使用

	1.转换流: 字节流 ---> 字符流
		InputStream in = System.in;
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
#File
	boolean createNewFile();  //只用于创建新文件,不能创建目录及多层文件
	boolean Mkdir();		  //只用于创建单层目录
	boolean Mkdirs();		  //用于创建多层目录
	boolean renameTo();		  //重命名抽象路径名表示的文件
	                          
	boolean delete();		  //只能单层删除文件或目录
	void dedeleteOnExit();	  //在 jvm 退出的时候删除
	                          
	boolean exists();		  //测试文件或目录是否存在
	boolean isFile();		  //测试是否是一个标准文件.(先 exists())
	boolean isDirectory();	  //同上

	String getAbsolutePath(); //返回绝对路径
	File getAbsoluteFile();	  //返回绝对路径名的file
	
	File[]   listFiles();	  //目录下的所有文件
	String[] List();		  //文件调用此方法,返回 null.(先 exists())
	String[] list(FilenameFilter filter); //返回指定过滤器的文件和目录
	
	0.过滤遍历
		@Test
		public void test01() {
			File file = new File("F:");
			//过滤 ---> 以 .log 结尾的文件
			String[] files = file.list((dir, name) -> name.endsWith(".log"));
			//注意: null==files ??
			Arrays.stream(null != files ? files : new String[0]).forEach(System.out::println);
		}
		
	1.递归删除
		@Test
		public void test01() {
			File file = new File("F:\\var\\lib\\webpark\\logs\\aiRes");
			removeDirs(file);
		}
		
		public void removeDirs(File dir) {
			Arrays.stream(Objects.requireNonNull(dir.listFiles())).forEach(x -> {
				if (x.isDirectory()) {
					removeDirs(x);
				} else {
					x.delete(); //删除文件
				}
			});
			dir.delete(); //删除目录
		}
		
#Properties
	//继承, 所以具有 map 集合的特点.
	class Properties extends Hashtable<Object,Object> {}
	
	0.DEMO
		public void test04() {
			Properties properties = new Properties();
			try (FileReader reader = new FileReader(new File("application.properties"))) {
				properties.load(reader); //读取配置
				
				Enumeration<?> enumeration = properties.propertyNames();
				while (enumeration.hasMoreElements()) {
					Object key = enumeration.nextElement();
					Object value = properties.get(key);
					System.out.println(key + " = " + value); //自动过滤 # 开头的注释
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
//}

//{--------<<<Socket>>>----------------------------
#IP&Port(协议端口)
	IPv4由4段组成,每段取值范围: 0~255 (2^8-1)

	如果把IP地址比作一间房子,端口就是出入这间房子的门.
	一个IP地址的端口可以有 65536. 2^16 . 端口号 0~65535, 2^16-1.
	
	//网络聊天数据发送到本机,为什么QQ接收,而不是MSN接收??
	端口号进行区分,聊天数据实际是发送到 ip:port.
	
#网络参考模型
	1.OSI参考模型(7)
		应用层,表示层,会话层 --> 传输层 --> 网络层 --> 数据链路层,物理层
	
	2.TCP-IP参考模型(4)
		应用层				 --> 传输层 --> 网际层 --> 主机至网络层
		
	3.常用协议
		应用层: HTTP/FTP;	传输层: UDP/TCP;	传输层: IP;

#Socket(插座,套接字)
 Socket 就是为网络服务提供的一种机制, //网络通信其实就是 Socket 间通信.
 通信两端都是 Socket,数据在两个 Socket 间通过 IO 传输.

#UDP&TCP
	UDP --> 面向无连接; 传输速度快; 但不可靠; 数据包大小 64 k. //类比'邮局'
		0.广播模式
			IP指定"192.168.8.255",代表'192.168.8.*'这一网段的所有主机都能收到
		1.点对点
			必须手动指定服务器IP,该IP主机才能收到UDP信息.
			
	TCP --> 需要建立连接(三次握手); 速度慢; 可靠协议; 可进行大数据量传输. //类比'打电话'
	
	三次握手: A在吗; 我在; 我知道A在了.
	
#DEMO-UDP
	1.发送和接受2线程
		public static void main(String[] args) {
			new Thread(new UDPSend(8500)).start();
			new Thread(new UDPRecv(8500)).start();
		}

		private static class UDPSend implements Runnable {
			private int sendPort;

			UDPSend(int sendPort) {
				this.sendPort = sendPort;
			}

			@Override
			public void run() {
				try (DatagramSocket send = new DatagramSocket(); //切记: 关闭操作
					 BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
					String line;
					while (null != (line = br.readLine())) {
						if (line.endsWith("8"))
							break;

						//数据包: 数据, 目标ip, 目标port
						byte[] bytes = line.getBytes(Charset.forName("UTF-8"));
						DatagramPacket packet = new DatagramPacket(bytes, bytes.length,
								InetAddress.getByName("192.168.8.255"), sendPort);
						System.out.println("send: " + line);

						//发送
						send.send(packet);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private static class UDPRecv implements Runnable {
			private int recvPort;

			UDPRecv(int recvPort) {
				this.recvPort = recvPort;
			}

			@Override
			public void run() {
				try (DatagramSocket recv = new DatagramSocket(recvPort)) {
					while (true) {
						byte[] bytes = new byte[1024 * 64]; //数据包最大 64k
						DatagramPacket packet = new DatagramPacket(bytes, bytes.length);

						//接收: 阻塞方法,线程的 wait-notify 机制.
						recv.receive(packet);

						String hostAddress = packet.getAddress().getHostAddress();
						String data = new String(packet.getData(), Charset.forName("UTF-8"));
						System.out.println("recv: " + hostAddress + " - " + data);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
#DEMO-TCP
	0.客户端 ---> //模拟多客户端向同一服务器端发送请求		
		public static void main(String[] args) {
			String host = "192.168.8.7";
			int port = 8100;
			int nThread = 5;
			ExecutorService pool = Executors.newFixedThreadPool(nThread);
			for (int i = 0; i < nThread; i++) {
				int index = i;
				pool.execute(() -> {
					try (Socket socket = new Socket(host, port); 
						 OutputStream out = socket.getOutputStream();
						 InputStream in = socket.getInputStream()) {
						String request = "hello, my No is " + index;
						out.write(request.getBytes(Charset.forName("UTF-8")));
						out.flush();
						System.out.println("客户端-发送: " + request); //发送

						int len;
						byte[] inBytes = new byte[1024];
						StringBuilder response = new StringBuilder();
						while (-1 != (len = in.read(inBytes))) {
							response.append(new String(inBytes, 0, len, Charset.forName("UTF-8")));
						}
						System.out.println("客户端-接收: " + response.toString()); //接收
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			}
		}

	1.TCP-服务端
		public static void main(String[] args) {
			int port = 8100;
			try (ServerSocket server = new ServerSocket(port)) {
				int count = 0;
				while (count < 10) {
					//获取客户端Socket.(阻塞方法)
					//每监听到一个客户端的链接,则新开一个线程去处理
					Socket socket = server.accept();

					Executors.newSingleThreadExecutor().execute(() -> {
						try (InputStream in = socket.getInputStream(); 
							 OutputStream out = socket.getOutputStream()) {
							int len;
							byte[] inBytes = new byte[1024];
							StringBuilder request = new StringBuilder();
							while (-1 != (len = in.read(inBytes))) {
								request.append(new String(inBytes, 0, len, Charset.forName("UTF-8")));
							}
							System.out.println("服务器-接收: " + request);

							String response = "服务器接已收到请求: " + request;
							out.write(response.getBytes(Charset.forName("UTF-8")));
							out.flush();
							System.out.println("服务器-回复: " + response);
						} catch (IOException e) {
							e.printStackTrace();
						}
					});
					count++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
//}

//{--------<<<ABC>>>-------------------------------
#Web-Service
	基于网络的,分布式的模块化组件,它执行特定的任务,遵守具体的技术规范,这些规范使得 Web-Service 能与其他兼容的组件进行互操作.
	
#JAXP(Java API for XML Parsing)
	定义了在Java中使用 DOM, SAX, XSLT 的通用的接口. 
	这样在程序中只要使用这些通用的接口,当需要改变具体的实现时候也不需要修改代码
	
#SOAP(Simple Object Access Protocol)
	简单对象访问协议,它是用于交换XML编码信息的轻量级协议
	
#JAXM(Java API for XML Messaging) 
	为 SOAP 通信提供访问方法和传输机制的API
	
#WSDL
	一种 XML 格式,用于将网络服务描述为一组端点,这些端点对包含面向文档信息或面向过程信息的消息进行操作.
	这种格式首先对操作和消息进行抽象描述,然后将其绑定到具体的网络协议和消息格式上以定义端点.
	相关的具体端点即组合成为抽象端点(服务)
	
#UDDI 
	目的是为电子商务建立标准, UDDI是一套基于Web的分布式的,为Web-Service提供的,信息注册中心的实现标准规范,
	同时也包含一组使企业能将自身提供的Web Service注册,以使别的企业能够发现的访问协议的实现标准

//}



//{--------<<<Object>>>----------------------------
#ABC
	Java语言中所有类的父类, 其中定义的 11 个方法子类都可以使用.
	
#toString() -> 返回该对象的字符串表示
	默认返回: 对象的类型+@+内存地址值
	


//}

//{--------<<<String>>>----------------------------
    @Test
    public void demo01() throws IOException {
        String s1 = "abc";
        String s2 = "abc";
        String s3 = new String("abc");
        String s4 = "ab" + "c";
		
        System.out.println(s1 == s2); //true
        System.out.println(s1 == s3); //false
        System.out.println(s1 == s4); //true
	}
	
#字符串常量池
	栈内存: 基本类型的变量和对象的引用变量
	堆内存: new 出来的对象和数组
	
	常量池: (A).字面量   -> 字符串, final-常量, 基本数据类型的值
			(B).符号引用 -> 类和结构的完全限定名, 字段名称和描述符, 方法名称和描述符
			
	字符串常量池 保存着很多String对象,并且可以被共享使用,提高了效率.
	由于String是 final 的,一经创建就不可改变,因此不用担心String对象共享而带来程序的混乱.
	
	0.分步解析	
		String s1 = "abc"; //先在常量池中查找是否存在"abc", 存在则让s1指向这个值, 没有则创建
		String s2 = "abc"; //同上
		
	1.目前还只创建了 1个字符串对象
	
		//String s3 只是定义了一个名为 s3 的String类型变量,并没有创建对象
		//new String() 在堆空间上创建1个字符串对象
		String s3 = new String("abc");
		
	2至此一共创建了 2个对象.(1个在堆内存上,1个在字符串常量池)
	
		String s4 = "ab" + "c"; //先在常量池中创建 2 个对象,再将 s4 指向已有的"abc"
	
	3.最后一共存在 4个对象.(1个在堆内存,3个在字符串常量池)
	
	4其中栈内存中存在 4个引用变量 s1,s2,s3,s4
		
	
	

//}


//{---------快捷键----

	Ctrl+Shift + Enter,语句完成
“！”,否定完成,输入表达式时按 “！”键
Ctrl+E,最近的文件
Ctrl+Shift+E,最近更改的文件
Shift+Click,可以关闭文件
Ctrl+[ OR ],可以跑到大括号的开头与结尾
Ctrl+F12,可以显示当前文件的结构
Ctrl+F7,可以查询当前元素在当前文件中的引用,然后按 F3 可以选择
Ctrl+N,可以快速打开类
Ctrl+Shift+N,可以快速打开文件
Alt+Q,可以看到当前方法的声明
Ctrl+P,可以显示参数信息
Ctrl+Shift+Insert,可以选择剪贴板内容并插入
Alt+Insert,可以生成构造器/Getter/Setter等
Ctrl+Alt+V,可以引入变量。例如：new String(); 自动导入变量定义
Ctrl+Alt+T,可以把代码包在一个块内,例如：try/catch
Ctrl+Enter,导入包,自动修正
Ctrl+Alt+L,格式化代码
Ctrl+Alt+I,将选中的代码进行自动缩进编排,这个功能在编辑 JSP 文件时也可以工作
Ctrl+Alt+O,优化导入的类和包
Ctrl+R,替换文本
Ctrl+F,查找文本
Ctrl+Shift+Space,自动补全代码
Ctrl+空格,代码提示（与系统输入法快捷键冲突）
Ctrl+Shift+Alt+N,查找类中的方法或变量
Alt+Shift+C,最近的更改
Alt+Shift+Up/Down,上/下移一行
Shift+F6,重构 – 重命名
Ctrl+X,删除行
Ctrl+D,复制行
Ctrl+/或Ctrl+Shift+/,注释（//或者/**/）
Ctrl+J,自动代码（例如：serr）
Ctrl+Alt+J,用动态模板环绕
Ctrl+H,显示类结构图（类的继承层次）
Ctrl+Q,显示注释文档
Alt+F1,查找代码所在位置
Alt+1,快速打开或隐藏工程面板
Ctrl+Alt+left/right,返回至上次浏览的位置
Alt+left/right,切换代码视图
Alt+Up/Down,在方法间快速移动定位
Ctrl+Shift+Up/Down,向上/下移动语句
F2 或 Shift+F2,高亮错误或警告快速定位
Tab,代码标签输入完成后,按 Tab,生成代码
Ctrl+Shift+F7,高亮显示所有该文本,按 Esc 高亮消失
Alt+F3,逐个往下查找相同文本,并高亮显示
Ctrl+Up/Down,光标中转到第一行或最后一行下
Ctrl+B/Ctrl+Click,快速打开光标处的类或方法（跳转到定义处）
Ctrl+Alt+B,跳转到方法实现处
Ctrl+Shift+Backspace,跳转到上次编辑的地方
Ctrl+O,重写方法
Ctrl+Alt+Space,类名自动完成
Ctrl+Alt+Up/Down,快速跳转搜索结果
Ctrl+Shift+J,整合两行
Alt+F8,计算变量值
Ctrl+Shift+V,可以将最近使用的剪贴板内容选择插入到文本
Ctrl+Alt+Shift+V,简单粘贴
Shift+Esc,不仅可以把焦点移到编辑器上,而且还可以隐藏当前（或最后活动的）工具窗口
F12,把焦点从编辑器移到最近使用的工具窗口
Shift+F1,要打开编辑器光标字符处使用的类或者方法 Java 文档的浏览器
Ctrl+W,可以选择单词继而语句继而行继而函数
Ctrl+Shift+W,取消选择光标所在词
// Alt+F7,查找整个工程中使用地某一个类、方法或者变量的位置
Ctrl+I,实现方法
Ctrl+Shift+U,大小写转化
Ctrl+Y,删除当前行


Shift+Enter,向下插入新行
psvm/sout,main/System.out.println(); Ctrl+J,查看更多
Ctrl+Shift+F,全局查找
Ctrl+F,查找/Shift+F3,向上查找/F3,向下查找
Ctrl+Shift+S,高级搜索
Ctrl+U,转到父类
Ctrl+Alt+S,打开设置对话框
Alt+Shift+Inert,开启/关闭列选择模式
Ctrl+Alt+Shift+S,打开当前项目/模块属性
// Ctrl+G,定位行
Alt+Home,跳转到导航栏
Ctrl+Enter,上插一行
Ctrl+Backspace,按单词删除
Ctrl+”+/-”,当前方法展开、折叠
Ctrl+Shift+”+/-”,全部展开、折叠
【调试部分、编译】
Ctrl+F2,停止
Alt+Shift+F9,选择 Debug
Alt+Shift+F10,选择 Run
Ctrl+Shift+F9,编译
Ctrl+Shift+F10,运行
Ctrl+Shift+F8,查看断点
F8,步过
F7,步入
Shift+F7,智能步入
Shift+F8,步出
Alt+Shift+F8,强制步过
Alt+Shift+F7,强制步入
Alt+F9,运行至光标处
Ctrl+Alt+F9,强制运行至光标处
F9,恢复程序
Alt+F10,定位到断点
Ctrl+F8,切换行断点
Ctrl+F9,生成项目
Alt+1,项目
Alt+2,收藏
Alt+6,TODO
// Alt+7,结构
Ctrl+Shift+C,复制路径
Ctrl+Alt+Shift+C,复制引用,必须选择类名
Ctrl+Alt+Y,同步
Ctrl+~,快速切换方案（界面外观、代码风格、快捷键映射等菜单）
Shift+F12,还原默认布局
Ctrl+Shift+F12,隐藏/恢复所有窗口
Ctrl+F4,关闭
Ctrl+Shift+F4,关闭活动选项卡
Ctrl+Tab,转到下一个拆分器
Ctrl+Shift+Tab,转到上一个拆分器
【重构】
Ctrl+Alt+Shift+T,弹出重构菜单
Shift+F6,重命名
F6,移动
F5,复制
Alt+Delete,安全删除
Ctrl+Alt+N,内联
【查找】
// Ctrl+F,查找
// Ctrl+R,替换
// F3,查找下一个
// Shift+F3,查找上一个
Ctrl+Shift+F,在路径中查找
Ctrl+Shift+R,在路径中替换
Ctrl+Shift+S,搜索结构
Ctrl+Shift+M,替换结构
// Alt+F7,查找用法
Ctrl+Alt+F7,显示用法
Ctrl+F7,在文件中查找用法
Ctrl+Shift+F7,在文件中高亮显示用法
	
//}




