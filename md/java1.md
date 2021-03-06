[TOC]



# 基础阶段

## 运算符

> ④种修饰符

```shell
private ---> 当前类
default ---> 当前类  当前包
protect ---> 当前类  当前包  子孙类
public  ---> 当前类  当前包  子孙类  其他包
```

> 数组拷贝

```java
public void test() {
    String[] src = {"a", "b", "c"};
    String[] dest = new String[2];

    //从原数组的第1个开始拷贝，目标数组从第0个位置开始接收，拷贝2个元素
    System.arraycopy(src, 1, dest, 0, 2);
    Arrays.stream(dest).forEach(System.out::println); //b-c
}
```

> `Long l = Math.round(double num);`返回值类型

```java
long l = Math.round(-1.45); //-1
long l = Math.round(-1.55); //-2 --> 同sql，x轴上向最近的整数取整

double d = Math.ceil(-1.45);  //-1.0
double d = Math.floor(-1.45); //-2.0 -->同sql，x轴上向最近（最远）的整数取整

double d = Math.floor(-10 / -3); //3.0
double d = Math.floor(-10 % -3); //-1.0 -->同sql，结果符号位和被除数（-10）一致。a%b = a-a/b*b
```

```

> &，&&：都是逻辑 "与" 运算符，但后者为`短路运算`

```sh
'& ' 既是逻辑运算符，也是位运算符。既能操作 boolean 类型，也能操作数值类型
'&&' 只是逻辑运算符，只能操作 boolean 类型。'短路操作：左为false，则右不再计算'
```

```java
int x = 0;
if (1 < 0 && x++ > 0) {} //&& 短路运算，左边 false，所以右边不执行。即 x 依旧等于 0
```

> 其他运算符

```sh
# 7%4 = 3; 7%-4 = 3; -7%4 = -3; -7%-4 = -3; 7%9 = 7; -7%-9 = -7; -7%-1= 0
%(取模)      --> 先取绝对值，再进行运算。符号同左。

^(异或)      --> 左右同为 false，左右不同为 true。(和其他差别很大)

<<(位运算-左) --> 乘以 2 的移动位数次幂  #3<<2 = 3 * 2^2 =12
>>(位运算-右) --> 除以 2 ...........   #6>>2 = 6 / 2^2 =1

b=a++; #先使用，再++
```

##关键字

> switch - break

```shell
switch：参数是一个整数表达式。支持 byte，char,short,int（都可转换成 int），enum，String（jdk7新增）

break ：当遇到break，switch语句终止。如果没有break出现，程序会继续执行下一条case语句，'直到出现 break 语句'。
```

> for

```shell
for(初始化； boolean表达式; 更新){ }

'初始化'   ：对循环可能要用到的值进行初始化，相当于for循环内部的一个局部变量
'布尔表达式'：当表达式结果为 true 时继续执行，为 false 时终止循环
'更新'     ：在一次循环结束后进行更新，一般用于修改初始化值，从而影响循环布尔表达式的值
```

```java
int count = 0;
int num = 0;
for (int i = 0; i < 100; i++) {
    num = ++num;     //先自增，再使用
    count = count++; //先使用，再自增
}
System.out.println(num +" - "+ count); //100 - 0
```

```java
//如何跳出当前的多重嵌套循环？让外层的循环条件表达式的结果，可以受到里层循环体代码的控制。
public void test() {
    int arr[][] = {{1, 2, 3}, {4, 5, 6, 7}, {9}};
    
    boolean found = false;        
    for (int i = 0; i < arr.length && !found; i++) {
        for (int j = 0; j < arr[i].length; j++) {
            System.out.println("i =" + i + ", j = " + j);
            if (arr[i][j] == 5) {
                found = true;
                break;
            }
        }
    }
}
```
> final & finally & finalize

```shell
'finally'：异常处理的一部分，代码肯定会被执行，常用于释放资源。
'finalize'：Object类的一个方法，用于垃圾回收。
```

```shell
'final-类'：不可被继承，即 final 类没有子类。 final 类中的所有方法默认全是 final 方法。
'final-方法'：不能被重写。#其中 private 和 static 方法默认就是 final

'final-变量'：即常量。值类型，不能修改其值；引用类型，不能修改其对应的堆内存地址，即不能重新再赋值。
#但是，该内存地址所指向的那个对象还是可以变的。就像你记住了人家的门牌号，但你不能管人家家里人员数量。
```

```java
public void doFinal(final int i, final StringBuilder sb) {
    // i = i + 1; //编译报错，因为final修饰的基本类型  --> 值不能变
    // sb = new StringBuilder(); //同上，修饰引用类型 --> 堆内存地址不能变,即引用不能变

    sb.append("java"); //编译通过 -> 引用变量所指向的对象中的内容，可以改变
}
```
```shell
#为什么内部线程中引用外部对象要加final修饰符呢？
被内部线程引用的外部对象受到外部线程作用域的制约，有其特定的生命周期。

当外部对象在外部线程中生命周期已经结束，而内部线程中还在持续使用，怎样解决问题？'内部线程变量要访问一个已不存在的外部变量？'
在外部变量前添加 final 修饰符，其实内部线程使用的这个变量就是外部变量的一个'复制品'，即使外部变量生命周期已经结束，内部复制品依然可用。
```

> instanceof

```java
A a = new B(); //class B extends A{}
System.out.println(a instanceof A); //true. instanceof 用来判断对象是否是一个类的一个实例
```

> 序列化

```sh
序列化就是一种用来处理对象流的机制，所谓对象流也就是将对象的内容进行流化。可以对流化后的对象进行读写操作，也可将流化后的对象传输于网络之间。
序列化是为了解决在对对象流进行读写操作时所引发的问题。

'序列化的实现'：将需要被序列化的类 implements Serializable 接口，该接口没有需要实现的方法，只是为了标注该对象是可被序列化的，
然后使用一个输出流(如：FileOutputStream)来构造一个ObjectOutputStream(对象流)对象，
接着，使用ObjectOutputStream对象的writeObject(Object obj)方法就可以将参数为obj的对象写出(即保存其状态)，要恢复的话则用输入流。
```
## 对象初始化

> 堆，栈，方法区

```sh
#栈
- JVM为每个线程创建一个栈，用于存放该线程执行方法的信息（实际参数、局部变量等）。
- 栈属于线程私有，不能实现线程间的共享！
- 栈是由系统自动分配，速度快！栈是一个连续的内存空间（相比于堆）。 
- 栈的存储特性是 "先进后出，后进先出"。
- 每个方法被调用都会创建一个栈帧（存储局部变量、操作数、方法出口等）。

#堆
- 用于存储创建好的对象和数组（'数组也是对象'）。
- JVM只有一个堆，被所有线程共享！
- 堆是一个不连续的内存空间，分配灵活，速度慢!

#方法区（特殊的堆）
- 方法区实际也是堆，用来存放程序中永远不变或唯一的内容（'类信息{Class对象}、静态变量、字符串常量等'）。
- JVM只有一个方法区，被所有线程共享！
```

```java
public class Dog {
    private String name;

    public Dog(String name) {
        this.name = name;
    }
}

public class Student {
    public static Boolean gender = false;
    private Integer age;
    private String name;

    public Dog dog;

    public Student(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public void play() {
        System.out.println("play with " + this.dog.name);
    }
}
```

```java
public class Test {
    public static void main(String[] args) { //程序入口
        Student stu = new Student(18, "王");
        stu.dog = new Dog("Yello");
        stu.play();
    }
}
```

![](assets/java3.jpg)

>对象初始化

```sh
#对于语句 Person p = new Person(“zhangsan”,20); 在内存中究竟做了什么事？

- 动态加载 Person.class 文件并加载到内存中。
- 静态字段初始化
- 静态代码块
- 在堆内存中开辟空间，分配内存地址。
- '普通字段的默认初始化' #（String默认 null，int默认 0）
- 开始执行构造函数的第一行，默认是 super(); 跳转到父类的构造函数
- '普通字段的显示初始化' # int age = 18
- 构造代码块
- 构造函数初始化
- 在栈内存中生成变量 p，将内存地址赋给栈内存中的 p 变量。

'静态字段 -> 静态代码块' -> 开辟堆内存 -> '普通字段默认初始化 -> 普通字段显示初始化 -> 构造代码块 -> 构造函数' ---> 赋值引用
```

```java
public class A {
    B b = new B();

    A() { System.out.print("A"); }
}

public class B {
    B() { System.out.print("B"); }
}

public class C extends A {
    B b = new B();

    C() { System.out.print("C"); }
}
```

```java
public static void main(String[] args) {
    new C(); //C 构造函数的第一行是 super(); 跳到父类 A中，A先进行 b=new B();即打印 B。再执行构造函数，打印 A
}            //执行完 A 的构造函数，跳转回 C 中，C也先进行 b=new B(); 即打印 B，然后再执行 C 的构造，打印 C ---> BABC
```

> 静态代码块

```sh
静态代码块 -> main -> 构造代码块 -> 构造函数

父类静态代码块 -> 子类静态代码块 -> main -> 父类构造代码块 -> 父类构造函数 -> 子类构造代码块 -> 子类构造数
```

```sh
'静态代码块'：只在jvm首次加载类时调用一次，优先于 main()执行。  #作用：初始化类的属性
'构造代码块'：每次 new 对象时都会被调用，优先于所有的构造函数。  #作用：给所有的对象进行统一、共性的初始化
'构造函数'  ：每个类都有一个默认的无参构造，访问权限和类保持一致。#作用：给通过此构造函数（构造函数不止一种）new 的对象进行初始化
```



##方法相关

> 方法传参：遵循`值传递`原则

```sh
#基本类型：传递的是该数据值的copy
所以，在方法内对值类型操作不会改变原有值

#引用类型：传递的是对象引用的copy，即堆内存的地址，真正的值是放在堆内存中。但指向的是同一个对象
所以，在方法内对引用类型进行'重新赋引用'，不会改变原有值。但是，'对原有引用的属性进行操作'时，可改变这个引用的属性值
```

```java
int num = 30;

public void add(int param) {
    param = 100;
}
```

```sh
当执行了 int num = 30; 这句代码后，程序在栈内存中开辟了一块地址为AD8500的内存，里边放的值是30

执行到add()方法时，程序在栈内存中又开辟了一块地址为AD8600的内存，将num的值30传递进来，此时这块内存里边放的值是30，
执行param = 100;后，AD8600中的值变成了100。

地址AD8600中用于存放param的值，和存放num的内存没有任何关系，无论你怎么改变param的值，实际改变的是地址为AD8600的内存中的值，
而AD8500中的值并未改变，所以num的值也就没有改变。
```

![](assets/java00.jpg) ![](assets/java0.jpg)

```java
String[] array = new String[] { "huixin" }; 

public void reset(String[] param) {
    param[0] = "hello,world!";
}
```

```sh
当程序执行了 String[] array = new String[] {"huixin"}; 后，程序在栈内存中开辟了一块地址编号为AD9500内存空间，
用于存放array[0]的引用地址，里边放的值是堆内存中的一个地址，示例中的值为BE2500，可以理解为有一个指针指向了堆内存中的编号为BE2500的地址。
堆内存中编号为BE2500的这个地址中存放的才是array[0]的值：'huixin'。

当程序进入 reset() 方法后，将array的值，也就是对象的引用BE2500传了进来。这时，程序在栈内存中又开辟了一块编号为AD9600的内存空间，
里边放的值是传递过来的值，即AD9600。可以理解为栈内存中的编号为AD9600的内存中有一个指针，也指向了堆内存中编号为BE2500的内存地址。
这样一来，栈内存AD9500和AD9600(即array[0]和param的值)都指向了编号为BE2500的堆内存。

在 reset() 方法中将param的值修改为'hello,world!'后，改变对象param的值实际上是改变param这个栈内存所指向的堆内存中的值。
param这个对象在栈内存中的地址是AD9600，里边存放的值是BE2500，所以堆内存BE2500中的值就变成了hello,world!。

程序放回main方法之后，堆内存BE2500中的值仍然为hello,world!，main方法中array[0]的值时，从栈内存中找到array[0]的值是BE2500，
然后去堆内存中找编号为BE2500的内存，里边的值是hello,world!。所以main方法中打印出来的值就变成了'hello,world!'
```

![](assets/java1.jpg)![](assets/java2.jpg)

>重写 & 重载`（与返回值无关）`

```shell
'重写'：在子类中，出现和父类中一摸一样的方法。
'重载'：同一类中，出现多个方法名一样，但参数列表（参数类型 + 个数 + 顺序）不一样的方法。
```

```java
private + static + final 各修饰的方法，子类都不可重写。
其中，子类中的 private + static 同名方法，表示重新定义的方法，与父类无关。而子类的 final 方法则编译报错。

protected, default, public 对子类可见，可重写。'但重写时不能缩小修饰符范围'，即不能将父类 public 方法重写为 protected 方法。

//当方法被重写后，调用子类实例的同名方法时会优先调用子类的重写方法，不会再调用父类的方法。
```

```java
public class Parent {
    private void fun1(){}
    void fun2(){}
    protected void fun3(){}
    public static void fun4(){}
}

//fun1：私有权限，无法被子类继承，因此无法被重写
//fun2：包权限，因此在同一个包内继承时，可以重写，但其他包继承无法重写
//fun3：子类访问权限，因此无论如何继承，都可以被重写。
//fun4：虽然是公有的访问权限，但为静态方法，无法被继承。
```

```java
public void test() {
    Father father = new Son(); //多态思想

    //所有属性 和 静态方法 --> 看左，即和父类保持一致，调用父类的
    System.out.println(father.staticNum);
    System.out.println(father.num);
    father.doStatic(); //FATHER-STATIC

    //非静态方法 --> 若子类有重写，则调用子类方法。否则调用父类方法
    father.doSth(); //Father 或 SON
}
```

> 构造方法

```shell
'this'：子类调用【子类】的同名成员或方法。'super'： 子类调用【父类】的同名成员或方法。
构造方法间调用使用 this() 或 super(name) 语句，并且该语句只能放在构造函数第一行。

#对于子类的构造函数，不管无参还是有参，如果没有显示指明调用哪个父类构造时，默认调用父类无参构造。
#当编译器尝试在子类中往这两个构造方法插入 super() 方法时，因为父类没有默认无参构造方法，所以编译器报错。下面 DEMO
```

```java
class Super {
    private int id;
    public Super(int id) { this.id = id; } //没有无参构造
}

class Sub extends Super {    
    public Sub() { } //编译错误

    public Sub(int id) { } //编译错误
}
```

> static 方法

```shell
#static 方法不可以调用非 static 方法
因为非 static 方法是要与对象关联在一起的，必须创建一个对象后，才可以在该对象上进行方法调用。
而 static 方法调用时不需要创建对象，可以通过类直接调用。

在 static 方法中调用非 static 方法时，可能还没有实例化对象， 这就与以上逻辑不符。
```

```shell

```



#高级阶段

## Integer

>基本数据类型：三大类，八小种。引用类型：类，接口，数组。

```java
布尔类型     ： boolean （1byte，8位） //以前，误以为是 1bit，1位。好像是错误的。
字符类型     ： char （2byte，2字节，16位，[-128, 127]）
数值类型整型  : byte （1byte）， short （2byte）， int （4byte）, long （8byte） 
数值类型浮点型： float （4byte）， double （8byte）
```

> 使用标准

```shell
（1）所有 POJO 类属性必须使用'包装数据类型'，并且不要设定默认值。
不设初值是提醒使用者在使用时必须自己显式地进行赋值。任何 NPE（NullPointerExceptionrn） 问题，或入库检查，都由使用者来保证。

（2）RPC 方法的返回值和参数必须使用'包装数据类型'。

（3）所有的局部变量【推荐】使用基本数据类型。
```

```java
//【反例】设定默认值，会在更新其它字段时，附带更新此字段，导致创建时间被修改成当前时间
Date editTime = new Date();
```

```shell
（1）某业务的交易报表上显示成交总额涨跌情况。调用的RPC服务，调用不成功时返回默认值，页面显示：0%，这是不合理的，应该显示成：中划线-。
（2）搜索条件对象，一般 null 值表示该字段不做限制，而 0 表示该字段的值必须为0。
#所以，包装数据类型的 null 值，能够表示额外的信息，如：远程调用失败，异常退出等
```

> 装箱 & 拆箱

```java
Integer v1 = 100;
// Integer v1 = Integer.valueOf(100); //底层：自动装箱（触发IntegerCache）
Integer v2 = 200;
int sum = v1 + v2;
//int sum = v1.intValue() + v2.intValue(); //底层：自动拆箱
```

>缓存策略

```java
Integer m = 128, n = 128;
System.out.println(m.equals(n)); //true  --> 对比【值】
System.out.println(m == n);      //false --> 对比【内存地址】

Integer m1 = 127, n1 = 127;
System.out.println(m1.equals(n1)); //true
System.out.println(m1 == n1);      //true

//jvm启动时，预先创建 [-128,127] 之间整数的 Integer 实例，然后保存到缓存数组 IntegerCache
//以后，再创建此区间整数的包装类实例时，直接从缓存取，不会再 new 新对象，即使用同一个对象。

//所以，所有的相同类型的 包装类对象之间值的比较，全部使用 equals()
```

> 类型的自动提升

```sh
#short s1 = 1; s1=s1+1; 与 s1+=1; 二者的区别？

（1）s1本是 short 类型，与 1 相加，自动提升为 int 类型，即等号右侧的结果已是 int 类型，将 int 赋值给 short 将报类型转换异常。
（2）对于运算符 +=，编译器会做特殊处理，不会报错。
```

```sh
#int x = 10; double res = (x > 10) ? 9.9 : 9; 结果 res 为何？

程序将计算后的结果，自动提升为两者之间较大的数据类型，即 double 类型： 9.0。
```

```java
float f = 22.2; //22.2 为double类型，赋值给float类型需要进行强制类型转换，否则无法通过编译
int i = 0.0;    //0.0 虽然看似是0，但是表示的是double类型，赋值给int类型时同样需要强制类型转换，否则无法通过编译

double d0 = 2;  //int 类型自动提升为 double 类型，编译通过
Double d1 = 2;  //但是，包装类Double接收 int 时，不会进行自动类型转换，因此无法通过编译
```

> char 类型存储中文

```sh
char 类型变量用来存储 Unicode 编码的字符集，Unicode 编码字符集中包含了汉字。所以，char 型变量中当然可以存储汉字。
不过，如果某个特殊的汉字没有被包含在 Unicode 编码字符集中，那么，这个 char 型变量中就不能存储这个特殊汉字。

'补充：Unicode 编码占用两个字节，char 类型的变量也占两个字节'
```

> 进制转换

```java
//八进制以 0 开头，十六进制以 0x 开头。都是数字 0，而不是字母 O

int i = 0144; //100，八进制表示方式: 4*8^0 + 4*8^1 + 1*8^2 = 4+32+64
int i = 0x64; //100，十六进制表示方式: 4*16^0 + 6*16^1 = 4+96

String octal = Integer.toOctalString(100); //144，十进制转八进制
String hex = Integer.toHexString(100);     //64，十进制转十六进制
```

## String


> `intern()`

```java
String s1 = "Hollis"; 
String s2 = new String("Hollis");
String s3 = new String("Hollis").intern();

System.out.println(s1 == s2); //false
System.out.println(s1 == s3); //true
```

```sh
#如何理解 String 的 intern()方法？
可以简单的理解 s1 和 s3 做的事情是一样的。
都是定义一个字符串对象，然后将'字符串字面量'保存在常量池中，并把这个'字面量的引用'返回给定义好的对象引用。

对于 s3，在不调 intern()情况，s3指向的是JVM在堆中创建的那个对象的引用的（如图中的s2）。
但是当执行了 intern()方法时，s3将指向字符串常量池中的那个字符串常量。
```

```sh
#String s = new String("hollis"); 定义了几个对象？
首先，在字符串常量池中查找是否含有'值相同'的字符串常量，然后再在内存中（堆）创建一个 String 对象
```

![](assets/string1.jpg)




<https://www.toutiao.com/a6758732626153964045/?timestamp=1573690282&app=news_article_lite&group_id=6758732626153964045&req_id=201911140811220100260790143BAA346D>

<https://www.cnblogs.com/pokid/p/10437716.html>

<https://blog.csdn.net/u011635492/article/details/81048150>

<https://blog.csdn.net/u013366617/article/details/83618361>



<https://blog.csdn.net/wojiao228925661/article/details/100280041>

```java
/**
 * s.intern()：保证堆中不再生成与 s 相等的其他对象或字面量。
 * 已有字面量则让新的对象指向字面量，已有对象则让新对象指向对象。
 */
@Test //1
public void test03() {
    String s1 = new String("a") + new String("b"); //堆中只有对象 obj1(值为"ab")。s1->obj1
    String i1 = s1.intern(); //不再生成与"ab"相等的对象或字面量。i1->obj1
    String s2 = "ab"; //s2->obj1

    System.out.println(s1 == s2); //1
    System.out.println(i1 == s1); //1
    System.out.println(i1 == s2); //1

    String s3 = new String("1"); //堆中有字面量 "1" 和对象 obj2(值为"1")，s3->obj2
    String i2 = s3.intern(); //不再生成。i2->"1"
    String s4 = "1"; //s4->"1"

    System.out.println(s3 == s4); //0
    System.out.println(i2 == s3); //0
    System.out.println(i2 == s4); //1
}
```

```java
@Test //2
public void test02() {
    String s1 = new String("a") + new String("b"); //堆中只有对象 obj1(值为"ab")。s1->obj1
    String s2 = "ab"; //此时堆中有 obj1 和字面量"ab"。s2->"ab"
    String i1 = s1.intern(); //不再生成。i1->"ab"

    System.out.println(s1 == s2); //0
    System.out.println(i1 == s1); //0
    System.out.println(i1 == s2); //1

    String s3 = new String("1"); //堆中有字面量 "1" 和 obj2 (值为"1")。s3->obj2
    String s4 = "1"; //s4->"1"
    String i2 = s3.intern(); //i2->"1"

    System.out.println(s3 == s4); //0
    System.out.println(i2 == s3); //0
    System.out.println(i2 == s4); //1
}
```





## 枚举

>`java.lang.Enum` 是一个抽象类，默认有两个属性：name + ordinal

```java
public enum EnumTest {    
    MON, TUE, WED, THU, FRI, SAT, SUN; 
    
    // new Enum<EnumTest>("MON", 0);
    // new Enum<EnumTest>("TUE", 1);
    // ...
}
```

```java
枚举类型使用关键字 enum，每个枚举值都是 java.lang.Enum 的子类（java.lang.Enum 是一个抽象类）。

枚举类型符合通用模式 Class Enum<E extends Enum<E>>，而 E 表示枚举类型的名称。
枚举类型的每一个值都将映射到 protected Enum(String name, int ordinal) 构造函数中。
每个值的名称都被转换成一个字符串，并且序数设置表示了此设置被创建的顺序。

所以，以上的枚举类，实际上调用了 7 次 Enum(String name, int ordinal)：
```

```java
EnumTest sun = EnumTest.SUN;
System.out.println(sun.name() + "-" + sun.ordinal()); //SUN-6
```

> 枚举自定义属性和方法

```java
@Getter //只能有 get
public enum EnumTest {
    MON(1, "周一"), TUE, WED, THU, FRI, SAT, SUN(7, "周末");

    private Integer code;
    private String msg;
}
```

```java
EnumTest[] values = EnumTest.values();
for (EnumTest value : values) {
    System.out.println(value.getCode() + "-" + value.getMsg() + "-" + value.ordinal()); //7-周末-6
}
```

> 总结：`switch 底层只支持 int + 枚举(底层也是int)`

```java
可以把 enum看成是一个普通的 class，它们都可以定义一些属性和方法。
不同之处是：enum 不能使用 extends 关键字继承其他类，因为 enum 已经 extends java.lang.Enum（java是单一继承）。
```

## 接口

> 接口的变迁

```java
public interface IMyInterface {
    //java7 --> 只能声明 全局常量 和 抽象方法
    /*public static final*/ int STATIC_NUM = 7; //变量的默认修饰符，即全局常量

    /*public abstract*/ void method();          //方法的默认修饰符

    //java8 --> 声明 静态方法 和 默认方法
    static void staticMethod() {
        System.out.println("java8-静态方法");
    }

    default void defaultMethod() {
        System.out.println("java8-默认方法");
    }

    //java9 --> 声明 私有方法（静态和非静态两种）
    private void privateMethod() {
        //当有多个 java8的静态方法和默认方法时，可以将冗余代码提取到通用的私有方法中
        System.out.println("java9-私有方法");
    }

    private static void privateStaticMethod() {
        System.out.println("java9-私有静态方法");
    }
}
```

> `类优先原则`：当父类和父接口（default方法）中都实现了相同的方法时，应该以父类中的方法优先。

```java
public class FatherClass {
    public void sayHello() {
        System.out.println("say hello");
    }
}
```

```java
public interface IFatherInterface {
    default void sayHello() {
        System.out.println("default say hello");
    }
}
```

```java
public class Test extends FatherClass implements IFatherInterface {
    public static void main(String[] args) {
        Test test = new Test();
        test.sayHello(); //输出："say hello"
    }
}
```

> `接口冲突`：当实现多个接口，且每个接口中都有同名的 default 方法，就会报错。必须手动选择一个 default 方法作为实现。

```java
public interface IFatherInterface1 {
    default void sayHi() {
        System.out.println("默认方法-1");
    }
}
```

```java
public interface IFatherInterface2 {
    default void sayHi() {
        System.out.println("默认方法-2");
    }
}
```

```java
public class Test implements IFatherInterface1, IFatherInterface2 {
    @Override
    public void sayHi() {
        IFatherInterface2.super.sayHi(); //手动指定
    }

    public static void main(String[] args) {
        Test test = new Test();
        test.sayHi();
    }
}
```























# 常见问题

##基础概念

> java跨平台

```sh
我们编写的Java源码，编译后会生成一种 .class 文件，称为'字节码文件'。字节码不能直接运行，必须通过 JVM 翻译成机器码才能运行。
JVM是一个软件，不同的平台有不同的版本，将相同的字节码文件编译成对应平台识别的机器码。
```

```sh
那么，跨平台是怎样实现的呢？这就要谈及Java虚拟机（Java Virtual Machine，简称 JVM）。JVM也是一个软件，不同的平台有不同的版本。我们编写的Java源码，编译后会生成一种 .class 文件，称为字节码文件。Java虚拟机就是负责将字节码文件翻译成特定平台下的机器码然后运行。也就是说，只要在不同平台上安装对应的JVM，就可以运行字节码文件，运行我们编写的Java程序。而这个过程中，我们编写的Java程序没有做任何改变，仅仅是通过JVM这一”中间层“，就能在不同平台上运行，真正实现了”一次编译，到处运行“的目的。JVM是一个”桥梁“，是一个”中间件“，是实现跨平台的关键，Java代码首先被编译成字节码文件，再由JVM将字节码文件翻译成机器语言，从而达到运行Java程序的目的。注意：编译的结果不是生成机器码，而是生成字节码，字节码不能直接运行，必须通过JVM翻译成机器码才能运行。不同平台下编译生成的字节码是一样的，但是由JVM翻译成的机器码却不一样。所以，运行Java程序必须有JVM的支持，因为编译的结果不是机器码，必须要经过JVM的再次翻译才能执行。即使你将Java程序打包成可执行文件（例如 .exe），仍然需要JVM的支持。注意：跨平台的是Java程序，不是JVM。JVM是用C/C++开发的，是编译后的机器码，不能跨平台，不同平台下需要安装不同版本的JVM。
```

> 面向对象的特征

```sh
#封装、继承、多态、（抽象）
封装性，即将对象封装成一个高度自治和相对封闭的个体，对象状态（属性）由这个对象自己的行为（方法）来读取和改变。
张三这个人，他的姓名等属性，要有自己提供的获取或改变的方法来操作。private name; setName(); getName()


```



> 基本数据类型 `8`

```java
boolean 1； byte 1； char 2; short 2； int 4； long 8; float：4； double 8； //1 byte = 8 bit
```

> 有了基本数据类型，为什么还需要包装类型？

```sh

```

> String & StringBuffer & StringBuilder

```sh
String是 final 类，即一旦声明便不可修改，底层
```




## 概念区分

> 抽象类 & 接口

```sh
二者的定义 #抽象类使用关键字 abstract，接口使用 interface
共同点    #不能实例化；用于被其他类实现和继承，以多态的方式使用；都可以包含抽象方法

抽象类和普通类的区别：不能创建实例对象 和 拥有抽象方法。
```

```sh
#不同点   单继承，多实现
接口不能包含构造器。       抽象类可以包含构造器（让抽象类的子类调用这些构造器来完成属于抽象类的初始化操作）
接口不能包含初始化代码块。  抽象类可以包含初始代码块（静态代码块和非静态代码块）

接口只能定义静态常量，不能定义普通成员变量。    抽象类既可以定义普通成员变量，也可以定义静态常量
接口只包含抽象方法、静态方法和默认方法（java8新增），不能为普通方法提供方法实现。     抽象类完全包含普通方法
```

> 静态变量 & 实例变量

```sh
'静态变量'：static 修饰，随着类的加载而分配内存空间，随着类的消失而消失。存在于'方法区'，被类的所有实例共享
'实例变量'：随着对象的建立而分配内存空间，随着对象的消失而消失。存在于'堆内存'
```

```java
class Test{
    Test(){
        staticNum++; //静态变量
        num++;       //实例变量

        //对于 staticNum，全局唯一份。每实例化一个Test对象，staticNum 就加1
        //但是，对于 num，每实例化一个Test对象，就会重新分配一个，所以一直都是1
        sout(staticNum + " - " + num);
    }
}
```

>

```sh

```

> int & Integer

```sh
int 是java提供的8种原始数据类型之一，默认值为0。
Integer是java为 int 提供的封装类，默认值为 null，即 Integer 可以区分出未赋值和值为0的区别，int 则无法表达出未赋值的情况，
例如，要想表达出没有参加考试和考试成绩为0的区别，则只能使用 Integer。

另外， Integer 提供了多个与整数相关的操作方法，例如，将一个字符串转换成整数，Integer中还定义了表示整数的最大值和最小值的常量。
```

```java
String str = num + "";      //速度最慢. new StringBuilder()
str = String.valueOf(num);
str = Integer.toString(num);

num = Integer.parseInt(str);
num = Integer.valueOf(str);
```

>重写 & 重载`（与返回值无关）`

```shell
'重写'：在子类中，出现和父类中一摸一样的方法。子类对象调用此方法时，将调用子类中的定义，对它而言，父类中的定义如同被"屏蔽"了。
'重载'：同一类中，出现多个方法名一样，但参数列表（参数类型 + 个数 + 顺序）不一样的方法。

方法的重写和重载是'Java多态性'的不同表现。重写是父类与子类之间多态性的一种表现，重载是一个类中多态性的一种表现。
```

> close() & flush()

```sh
close(); #先刷新一次缓冲区，再关闭流对象。关闭之后，流对象将不可用
flush(); #仅仅刷新缓冲区（一般写字符时，先写入缓冲区）。刷新之后，流对象还可以继续使用
```

> CheckedException & UncheckedException

```sh
CheckedException  ：
UncheckedException：
```

```sh

```



## 概念知识

> 一些常识

```sh
#一个.java文件
.java文件可以包含多个类。但是，只能有一个 public 类，并且此类的类名必须和文件名相同。

#导包注意
import java.util.*; 只能导入'java.util'包下的所有类，但不能导入'java.util.concurrent'包下的类

#数组相关
数组是一个对象，不同类型的数组具有不同的类（如，int数组 和 double数组）。数组长度不可以动态调整。
数组中的元素'连续存储'在同一块内存中，所以，可以通过下标（偏移量）的方式访问
两个数组使用 equals()方法进行比较时，其实际比较的还是 内存地址。数组没有覆写 Object.equals()方法。

```

> 字符串与数组中的 Length

```sh
'数组和字符串都是对象'。数组在创建的时候，长度就已经确定了，所以，可以利用 'length属性' 表示其长度。
而字符串本质也是一个字符数组，没必要再用这个属性表示其长度。于是，就封装了一个 'length方法'，其源码如下：
```

```java
public int length() {
    return value.length;
}
```

>JVM加载class文件的原理机制？

```sh
JVM中类的装载是由 ClassLoader 和它的子类来实现的。
Java ClassLoader 是一个重要的Java运行时系统组件，它负责在运行时查找和装入类文件的类。
```

>两个对象值相同(x.equals(y) == true)，但却可有不同的hash code，这句话对`不对`？

```sh
Java对于eqauls方法和hashCode方法是这样规定的：
#（1）相等的对象应该具有相同的hashCode（equals方法返回true，则hashCode一定相同）。
#（2）如果两个对象的hashCode相同，它们并不一定相同。
当然，你未必要按照要求去做，但是如果你违背了上述原则就会发现在使用容器时，相同的对象可以出现在Set集合中，
同时增加新元素的效率会大大下降（对于使用哈希存储的系统，如果哈希码频繁的冲突'hash冲突'将会造成存取性能急剧下降）。
```

```sh
equals() 方法必须满足：
#自反性（x.equals(x)必须返回true）、
#对称性（x.equals(y)返回true时，y.equals(x)也必须返回true）、
#传递性（x.equals(y)和y.equals(z)都返回true时，x.equals(z)也必须返回true）
#一致性（当x和y引用的对象信息没有被修改时，多次调用x.equals(y)应该得到同样的返回值）
#而且对于任何非null值的引用x， x.equals(null) 必须返回false。
```

```sh
实现高质量的equals方法的诀窍包括：
（1）使用==操作符检查"参数是否为这个对象的引用"；
（2）使用instanceof操作符检查"参数是否为正确的类型"；
（3）对于类中的关键属性，检查参数传入对象的属性是否与之相匹配；
（4）编写完equals方法后，问自己它是否满足对称性、传递性、一致性；
（5）重写equals时总是要重写hashCode；
（6）不要将equals方法参数中的Object对象替换为其他的类型，在重写时不要忘掉@Override注解。
```

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    City city = (City) o;
    return Objects.equals(id, city.id) && Objects.equals(name, city.name);
}

@Override
public int hashCode() {
    return Objects.hash(id, name);
}
```

> 当一个对象被当作参数传递到一个方法后，此方法可改变这个对象的属性，并可返回变化后的结果，那么这里到底是值传递还是引用传递

```sh

```





# 开发手册

## 命名风格

> 变量，常量

```sh
方法名、参数名、成员变量、局部变量都统一使用'小驼峰'风格。
'常量'命名全部大写，单词间用'下划线'隔开，力求语义表达完整清楚，不要嫌名字长。
#POJO类中布尔类型的变量，都不要加 is，否则部分框架解析会引起序列化错误。

long或Long初始赋值时，必须使用大写的L，不能是小写的l，小写容易跟数字1混淆，造成误解。
```

> 包名，类名

```sh
包名统一使用'单数'形式，但是类名如果有复数含义，类名可以使用'复数'形式。 #com.example.safe.util.CommonUtils
'接口类'中的方法和属性不要加任何修饰符号（public也不要加）。

枚举类名建议带上'Enum'后缀，枚举成员名称需要全大写，单词间用下划线隔开。
测试类命名以它要测试的类的名称开始，以'Test'结尾。
```

##OOP规约

>推荐使用 `Integer.valueOf()`

```java
properties.put("CCSID", new Integer(5));
properties.put("CCSID", Integer.valueOf(5)); //触发 IntegerCache 机制，范围：[-128, 127]
```

> 所有的相同类型的`包装类对象`之间值的比较，全部使用 equals 方法比较

```sh
#享元模式（Flyweight Pattern）：复用内存中已存在的对象。
对于 Integer 在[-128,127]范围内的赋值，对象是在 IntegerCache.cache 产生，会复用已有对象。
所以，在此范围内的 v1==v2 效果同 v1.equals(v2) 相同，都是 true
但是，此范围之外的不会复用已有的对象，而是在堆空间新生成。所以，v1==v2 返回 false。v1.equals(v2) 返回 true
```

> 字符串比较时，将常量放在 `equals()` 方法的左侧

```java
if (str.equalsIgnoreCase("123")){} 
if ("123".equalsIgnoreCase(str)){} //避免 str 的空指针问题
```

> `String.indexOf(char)` 速度更快

```java
int index = str.indexOf("s");
int index = str.indexOf('s'); //速度更快
```

> String 的 `split()` 得到数组后，需做最后一个分隔符后有无内容的检查，否则会有抛 `数组越界` 的风险

```java
String str = "a,b,c,,";
String[] ary = str.split(",");
System.out.println(ary.length); // 预期大于 3，结果是 3
```



##控制语句

> Switch

```sh
在一个 switch 块内，每个 case 要么通过 break/return 等来终止，要么注释说明程序将继续执行到哪一个 case 为止；
在一个 switch 块内，都必须包含一个 default 语句并且放在最后，即使空代码。
```

> 高并发情况，等值击穿

```sh
在高并发场景中，避免使用 "等于" 判断作为中断或退出的条件。
说明：如果并发控制没有处理好，容易产生等值判断被 '击穿' 的情况，使用大于或小于的区间判断条件来代替。
反例：判断剩余奖品数量等于 0 时，终止发放奖品，但因为并发处理错误导致奖品数量瞬间变成了负数，这样的话，活动无法终止。
```

> if 条件

```sh
除常用方法（如 getXxx/isXxx）等外，不要在条件判断中执行其它复杂的语句，将复杂逻辑判断的结果赋值给一个有意义的布尔变量名，以提高可读性。
```

```java
final boolean existed = (file.open(fileName, "w") != null) && (...) || (...);
if (existed) {
    //...
}

if ((file.open(fileName, "w") != null) && (...) || (...)) { //【反例】不要这么写
    //...
}
```

> 循环体

```sh
循环体中的语句要考量性能，以下操作尽量移至循环体外处理：#如定义对象、变量、获取数据库连接，进行不必要的 try-catch 操作（这个 try-catch 是否可以移至循环体外） 
```

> 避免采用取反逻辑运算符

```java
//说明：取反逻辑不利于快速理解，并且取反逻辑写法必然存在对应的正向逻辑写法。
正例：使用 if (x < 628)      来表达 x 小于 628。
反例：使用 if (!(x >= 628))  来表达 x 小于 628。
```

