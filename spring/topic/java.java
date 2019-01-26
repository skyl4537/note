


//{--------<<<基础>>>-------------------------------------------------------------------------
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

//{--------<<<Reflect>>>----------------------------------------------------------------------
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
		
	3.反射方式 //动态创建; 效率相对低下
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
        Constructor<?>[] constructors = clazz.getConstructors(); //该类的 public
        Constructor<?>[] declaredConstructors = clazz.getDeclaredConstructors(); //该类的 all

	2.无参构造器
        Constructor<?> constructor = clazz.getConstructor();
        Person p9 = (Person) constructor.newInstance(); //新建对象 <--等同于--> clazz.newInstance();
	
	3.有参构造器
        Constructor<?> constructor0 = clazz.getConstructor(boolean.class);
        Person p8 = (Person) constructor0.newInstance(true);
		
#属性
	1.所有属性
		Field[] fields = clazz.getFields(); //该类及[所有父类]的	--> public fields
		Field[] declaredFields = clazz.getDeclaredFields(); //该类	--> all fields.(private + public +protected + default)
	
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
        Method[] declaredMethods = clazz.getDeclaredMethods();

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


//}

//{--------<<<Annotation>>>-------------------------------------------------------------------


//}

//{--------<<<x>>>----------------------------------------------------------------------------

//}













