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
//数组拷贝，非常高效 ---> 此方法隶属于System
public void test() {
    String[] src = {"a", "b", "c"}; //src
    String[] dest = new String[2]; //dest

    //从原数组的第1个开始拷贝，目标数组从第0个位置开始接收，拷贝2个元素
    System.arraycopy(src, 1, dest, 0, 2);
    Arrays.stream(dest).forEach(System.out::println); //b-c
}
```

> `Math.round(double num);`函数是取整函数，只关注小数点后第一位小数值。

```java
long l = Math.round(-1.45); //-1
long l = Math.round(-1.55); //-2 --> 同sql，x轴上向最近的整数取整

double d = Math.ceil(-1.45);  //-1.0
double d = Math.floor(-1.45); //-2.0 -->同sql，x轴上向最近（最远）的整数取整

double d = Math.floor(-10 / -3); //3.0
double d = Math.floor(-10 % -3); //-1.0 -->同sql，结果符号位和被除数（-10）一致。a%b = a-a/b*b
```

> 保留两位有效小数（2种方式）

```java
double val0 = (Math.round(1.1249 * 100)) / 100.0;
double val1 = (Math.round(1.1250 * 100)) / 100.0;
System.out.println(val0 + " - " + val1); //1.12 - 1.13
```

```java
String val0 = String.format("%.2f", 1.1249);
String val1 = String.format("%.2f", 1.1250);
System.out.println(val0 + " - " + val1); //1.12 - 1.13
```

> &，&&：都是逻辑 "与" 运算符，但后者为短路运算

```java
&  ：左边无论真假，右边都要运算
&& ：如果左边为真，右边参与运算； 如果左边为假，那么右边不参与运算。
```

```java
if (x < 1 && ++y > 0) {} System.out.println(x + "-" + y);

int x = 0, y = 0; //&：0-1； &&：0-1
int x = 2, y = 0; //&：2-1； &&：2-0
//&& 属于短路运算，即当 x=2 时，x<1 为 false。不管右侧如何，整体恒为 false。所以，不再进行 ++y>0 判断，即 y 不再自增。
```

```java
if (x < 1 || ++y > 0) {} System.out.println(x + "-" + y);

int x = 0, y = 0; //|：0-1； ||：0-0
int x = 2, y = 0; //|：2-1； ||：2-1
//||也是短路运算，道理同上。
```

> 其他运算符

```java
// 7%4 = 3; 7%-4 = 3; -7%4 = -3; -7%-4 = -3; 7%9 = 7; -7%-9 = -7; -7%-1= 0
%(取模)      --> 先取绝对值，再进行运算。符号同左。

^(异或)      --> 左右同为 false，左右不同为 true。(和其他差别很大)

<<(位运算-左) --> 乘以 2 的移动位数次幂 //3<<2 = 3 * 2^2 =12
>>(位运算-右) --> 除以 2 ...........  //6>>2 = 6 / 2^2 =1
```

##关键字

> switch - break

```shell
switch：支持 byte，char,short,int（都可转换成 int），enum，String（jdk7新增）

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
    num = ++num; //先自增，再使用
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

>finally

```java
int num = 5;
try {
    // int _res = num;
    // return _res;
    return num + 1; //这一语句 等价为 上面两行语句，所以在 finally 块中对 num 进行操作将不起作用。返回 6
} finally {
    num = num + 2;
    // return num + 2; //7 --> 若 finally 块中也有 return 语句，则以 finally 为主，即返回 7
}
```

> instanceof

```java
A a = new B(); //class B extends A{}
System.out.println(a instanceof A); //true. instanceof 用来判断对象是否是一个类的一个实例
```





## Integer

>基本数据类型：三大类，八小种。引用类型：类，接口，数组。

```java
布尔类型： boolean （1byte，8位） //以前，误以为是 1bit，1位。好像是错误的。
字符类型： char （2byte，2字节，8位，[-128, 127]）
数值类型-整型： byte （1byte）， short （2byte）， int （4byte）, long （8byte） 
数值类型-浮点型： float （4byte）， double （8byte）
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
（1）某业务的交易报表上显示成交总额涨跌情况。调用的RPC服务，调用不成功时，返回的是默认值，页面显示：0%，这是不合理的，应该显示成：中划线-。
（2）搜索条件对象，一般 null 值表示该字段不做限制，而 0 表示该字段的值必须为0。

#所以，包装数据类型的 null 值，能够表示额外的信息，如：远程调用失败，异常退出等
```

> 装箱 & 拆箱

```java
Integer i0 = new Integer(3);
Integer i1 = Integer.valueOf(3); //装箱：基本类型 --> 包装类
int num0 = i0.intValue();        //拆箱：包装类 --> 基本类型
```

```java
Integer i = 4; //自动装箱：相当于 Integer i = Integer.valueOf(4);
i = i + 5;     //自动拆箱：等号右边，将i对象转成基本数值 i.intValue() + 5，加法运算完成后，再次装箱，把基本数值转成对象
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

```java
short s1 = 1; s1=s1+1; '与' s1+=1; '二者的区别？'

（1）s1本是 short 类型，与 1 相加，自动提升为 int 类型，即等号右侧的结果已是 int 类型，将 int 赋值给 short 将报类型转换异常。
（2）对于运算符 +=，编译器会做特殊处理，不会报错。
```

```java
int x = 10; double res = (x > 10) ? 9.9 : 9; '结果 res 为何？'

程序将计算后的结果，自动提升为两者之间较大的数据类型，即 double 类型： 9.0。
```

```java
float f = 22.2; //22.2 为double类型，赋值给float类型需要进行强制类型转换，否则无法通过编译
int i = 0.0;    //0.0 虽然看似是0，但是表示的是double类型，赋值给int类型时同样需要强制类型转换，否则无法通过编译

double d0 = 2;  //int 类型自动提升为 double 类型，编译通过
Double d1 = 2;  //但是，包装类Double接收 int 时，不会进行自动类型转换，因此无法通过编译
```

> char 类型存储中文

```java
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

> 字符串对象一旦创建（字符串常量池中），就无法修改。

```shell
String类的所有方法都没有改变字符串本身的值，而是返回了一个新的String对象。

'String对象不可变（√）' 与 'String对象的引用变量不可变（×）'。

final 类不一定线程安全，如: StringBuilder。
#常见 final 类：LocalDateTime，StringBuffer，StringBuilder（非线程安全），Integer等。
```

```shell
#String 不可变特性的应用
'高效性'：不可变能保证其 hashcode 永远保持一致，不需要重新计算。这就使得字符串很适合作为 Map 中的 Key，字符串的处理速度要快过其它的键对象。

'安全性'：String被广泛的使用在其他Java类中充当参数。比如网络连接、打开文件等操作。如果字符串可变，那么类似操作可能导致安全问题。

'线程安全'：因为不可变对象不能被改变，所以他们可以自由地在多个线程之间共享。不需要任何同步处理。
```

> 字符串常量池

```sh
#Java为了避免产生大量的String对象，设计了一个字符串常量池。
工作原理是这样的，创建一个字符串时，JVM首先为检查字符串常量池中是否有'值相等'的字符串，如果有，则不再创建，直接返回该字符串的引用地址；
如果没有，则创建，然后放到字符串常量池中，并返回新创建的字符串的引用地址。

当遇到 'new String("Hollis");' 时，还会在内存（不是字符串常量池中，而是在堆里面）上创建一个新的String对象，
存储"hollis"，并将内存上的引用地址返回。
```

```sh
字符串常量池中的对象是'在编译期确定'，在类被加载时创建。如果类加载时，该字符串常量在常量池中已存在，那就跳过，不会重新创建一个。

与之相反，堆中的对象是'在运行期才确定'，在代码执行到 new 的时候创建的。
```

> `intern()`：①.将字符串字面量放入常量池（如果池中没有的话）②.返回这个常量的引用。

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
先在字符串常量池中查找是否含有'值相同'的字符串常量，然后再在内存中（堆）创建一个String对象
```

![](assets/string1.jpg)

> 字符串拼接

```java
String s1 = "abc";
String s2 = "ab" + "c";

String s3 = "ab";
String s4 = s3 + "c";

System.out.println(s1 == s2); //true
System.out.println(s1 == s4); //false
```

```sh
对于字符串常量相加的表达式，不是等到运行期才去进行加法运算处理，而是在编译期直接将其编译成一个这些常量相连的结果。
因此，String s2 = "ab" + "c"; 可转化为 String s2 = "abc"; 但是， s4 并不是字符串常量相加，不能转化。
```

> StringBuilder

```sh
#内部拥有一个数组用来存放字符串内容。当进行字符串拼接时，直接在数组中加入新内容，并自动维护数组的扩容，不会产生中间字符串。
```

```sh
String          线程安全     直接进行字符串拼接，会产生大量中间字符串，并且时间消耗长。
StringBuffer    线程安全     支持同步锁，性能稍差。
StringBuilder   线程不安全   单线程进行大量字符串操作时，推荐使用（√）。
```

```java
String res = str0 + str1;

String res = new StringBuilder(str0).append(str1).toString(); //底层实现
```

```sh
#循环中拼接字符串
每次循环都需要创建一个 StringBuilder 对象（创建对象需要耗费时间和内存）。
随着循环次数的增大，res字符串也就越长，把res中的字符复制到新建的 StringBuilder 中花费的时间也就越长。
而且，StringBuilder(res).append(str).toString(); 会创建一个临时的字符串，随着循环次数的增加，
这个操作花费的时间也会越来越长。总之，随着循环变量 i 的增大，每次循环会变得越来越慢。
```

> 字符串替换

```sh
replace();      #参数是 char 和 CharSequence，即支持字符和字符串的替换
replaceAll();   #参数是 regex，即基于正则表达式的替换
replaceFirst(); #参数也是 regex，但不同的是只替换第一个，即基于正则替换第一个满足条件的
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
@NoArgsConstructor
@AllArgsConstructor
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
不同之处是：enum 不能使用 extends 关键字继承其他类，因为 enum 已经继承了 java.lang.Enum（java是单一继承）。
```

## Exception



# 高级阶段

##概念区分

>== & equals()

```shell
#==  System.out.println(2 == 2.0); //true
当比较的是基本数据类型时，比较的是值；
当比较的是引用数据类型时，比较的是地址值。
```

```shell
#equals
不能用于基本数据类型的比较；
当比较的是引用数据类型时，默认也是比较地址值（即调用==）。
只不过像String、Date、File、包装类等都重写了Object类中的 equals()。实际开发中，常常需要根据业务需要重写 equals()。
```



> 抽象类 & 接口

```shell
二者的定义 #抽象类使用关键字 abstract，接口使用 interface
共同点    #不能实例化；用于被其他类实现和继承，以多态的方式使用；都可以包含抽象方法
```

```shell
#不同点    单继承，多实现
接口不能包含构造器。    抽象类可以包含构造器（让抽象类的子类调用这些构造器来完成属于抽象类的初始化操作）
接口不能包含初始化块。  抽象类可以包含初始代码块

接口只能定义静态常量，不能定义普通成员变量。抽象类既可以定义普通成员变量，也可以定义静态常量
接口只包含抽象方法、静态方法和默认方法（java8新增），不能为普通方法提供方法实现。抽象类完全包含普通方法
```





##方法相关

> 方法传参：递遵循值传递原则`（传递的是值或引用的拷贝，不改变原有值）`

```shell
基本类型传递的是数据值的拷贝。在方法内对值类型操作不会改变原有值。

引用类型传递的是该对象的堆内存地址，即引用拷贝，但指向同一个对象。所以，在方法内对引用类型进行'重新赋引用'，不会改变原有值。
但是'对原有引用的属性进行操作'时，可改变这个引用的属性值。
```

```java
private void doSth(int i, String s, Person p) {
    i += 1;
    s += "hello";
    p = new Person("li", 20);
    // p.age = 30; //将改变原有引用的属性值，其他则不会改变原有值
}
```

>重写 & 重载`（与返回值无关）`

```shell
'重写'：在子类中，出现和父类中一摸一样的方法。
'重载'：同一类中，出现多个方法名一样，但参数列表（参数类型 + 个数 + 顺序）不一样的方法。
```

```java
private + static + final 各修饰的方法，子类都不可重写。
其中，子类中的 private + static 同名方法，表示重新定义的方法，与父类无关。而子类的 final 方法则编译报错。

protected, default, public 对子类可见，可重写。但重写时不能缩小修饰符范围，即不能将父类 public 方法重写为 protected 方法。

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
    Father father = new Son(); //多态

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






# Collection

## 基础概念

> Exception 和 Error 都继承自 `Throwable` 类，是异常处理机制的基本组成类型

```shell
Error    ：一般指与虚拟机相关的问题，程序本身无法恢复，建议程序终止。常见：'系统崩溃，内存溢出等'。
Exception：是程序正常运行中，可以预料的意外情况，应该捕获并进行相应的处理。
```

> Exception分类： RuntimeException（运行时异常）和 CheckedException（受检查异常，即编译器异常）。

>RuntimeException：是 RuntimeException 类及其子类，`编译时能通过，在运行时出现`，出现后程序直接终止。

```java
（1）NullPointerException，（2）ClassCastException，（3）ArithmaticException（除数为0），
（4）ArrayIndexOutOfBoundsException（数组下标越界），（5）NumberFormatException（数字格式化）

Arrays.asList("a", "b").add("c"); //java.lang.UnsupportedOperationException

list.forEach(x -> {if("b".equals(x)){ list.add("c"); }}); //ConcurrentModificationException
```

>CheckedException：`不是具体的类`，是 RuntimeException 以外的异常，类型上都属于Exception类及其子类。

```java
编译时直接报错，必须使用 try-catch 进行异常捕获，或使用 throws 语句声明抛出。

（1）IOException，（2）InterruptedException，（3）SQLException
```

```java
try {
    //throw new NullPointerException("NPE"); //try 抛出的异常被 catch 捕获
    throw new IOException("IOE");
} catch (RuntimeException e0) {
    System.out.println("RuntimeException：" + e0.getMessage());
} catch (Exception e1) {
    System.out.println("Exception：" + e1.getMessage());
}

//结果分别输出: RuntimeException：NPE 和 Exception：IOE
//原因是 NPE 与 RuntimeE 之间的继承关系比 Exception 近，就近捕获原则。
```

>throws & throw

```shell
throws：通常被应用在'声明方法时'，用来指定可能抛出的异常。多个异常使用逗号隔开。
throw ：通常用在'方法体中'，并且抛出一个异常对象。程序在执行到 throw 语句时立即停止，它后面的语句都不执行。
```

```java
//throw 抛出异常后，如果想在方法调用处捕获并处理该异常，则需要用 throws 在方法声明中指明要抛出的异常。
int str2int(String str) throws NumberFormatException {
    if (str == null) {
        throw new NumberFormatException("null");
    }
    return Integer.parseInt(str);
}
```



# Collection-Plus

##list去重

> 目标：取出 Id 不重复的元素

```java
List<User> users = Arrays.asList(new User(1, "a"), new User(2, "b"), new User(1, "aa")); //源集合
```

> （1）遍历源集合，用遍历元素和结果集中的每个元素比较

```java
ArrayList<User> list = new ArrayList<>(); //结果集
users.forEach(x -> {
    boolean match = list.stream().anyMatch(y -> y.getId().equals(x.getId()));
    if (!match) {
        list.add(x);
    }
});
```

> （2）使用HashSet。`重写 hashCode() + equals()`

```java
//先放入 Set 去重，再取出来放入 list
Set<User> set = new HashSet<>(users);
List<User> list = new ArrayList<>(set);
```

> （3）对于大数据量，应采用 Stream 并行流的 distinct 方法。`重写 hashCode() + equals() `

```java
List<User> list = users.parallelStream()
    .distinct() //底层是通过 equals() 进行去重
    .collect(Collectors.toList());
```

> （0）重写 hashCode + equals

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(id, user.id) /*&& Objects.equals(name, user.name)*/;
}

@Override
public int hashCode() {
    return Objects.hash(id/*, name*/); //只比较 id
}
```

> 总结向

```java
//HashSet 底层是通过 HashMap 实现
//HashMap 确保key值唯一：先比较 hashCode()，如果相同；继续比较 equals() 是否为同一个对象
public HashSet() {
    map = new HashMap<>();
}

//存在则返回 false，不存在的返回 true
public boolean add(E e) {
    return map.put(e, PRESENT)==null;
}
```

```shell
#equals() 返回 true 的时候，hashcode() 的返回值必须相同。

如果两个对象相同（equals 返回 true），则它们的 hashCode 必须相同。但如果两个对象不同，则它们的 hashCode 不一定不同。
如果两个不同对象的 hashCode 相同，这种现象称为"哈希冲突"。冲突会导致操作哈希表的时间开销增大，所以尽量定义好的hashCode()方法，能加快哈希表的操作。
```

```shell
#覆写了 equals() 方法之后，一定要覆盖 hashCode() 方法

对于，String a = new String(“abc”); String b = new String(“abc”);
如果只覆写 equals()，不覆写 hashCode() 的话，那么 a和b 的 hashCode 就会不同。
把这两个类当做 key 存到 HashMap 中的话就会出现问题，就会和 key 的唯一性相矛盾。
```



# IO

## IO

> 文件拷贝：字节流 + 字符流

```sh
按流向        ：输入流，输出流。
按操作数据     ：字节流 （如音频，图片等），字符流（如文本）。

字节流的抽象基类：InputStream，OutputStream。字符流的抽象基类：Reader，Writer。
```

```java
try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
     BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dest))) {
    int len;
    byte[] buf = new byte[1024 * 4]; //字节流
    while (-1 != (len = bis.read(buf))) {
        bos.write(buf, 0, len);
    }
} catch (IOException e) {
    System.out.println("系统找不到指定的文件：" + src);
}
```

```java
try (BufferedReader br = new BufferedReader(new FileReader(src));
     BufferedWriter bw = new BufferedWriter(new FileWriter(dest))) {
    String line;
    while (null != (line = br.readLine())) { ///如果已到达流末尾，则返回 null
        bw.write(line);
        bw.newLine(); //由于 readLine()方法不返回行的终止符，所以手动写入一个行分隔符

        bw.flush(); //只要用到缓冲区技术，就一定要调用 flush() 方法刷新该流中的缓冲
    }
} catch (IOException e) {
    System.out.println("系统找不到指定的文件：" + src);
}
```

> 转换流：字节流 ---> 字符流

```java
BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
```

> close() & flush()

```sh
close(); #先刷新一次缓冲区，再关闭流对象。关闭之后，流对象将不可用
flush(); #仅仅刷新缓冲区（一般写字符时，先写入缓冲区）。刷新之后，流对象还可以继续使用
```

> 字符编码

```sh
GBK   ：占用两个字节，比 GB2312 编码多了很多汉字，如"镕"字。
UTF-8 ：Unicode一种具体的编码实现。是一种变长编码方式，使用 1-4 个字节进行编码，有利于节约网络流量。
```

```sh
#UTF-8编码规则
① 对于单字节的符号，字节的第一位设为0，后面7位为这个符号的unicode码。因此对于英语字母，UTF-8编码和ASCII码是相同的。
② 对于n字节的符号，第一个字节的前n位都设为1，第n+1位设为0，后面字节的前两位一律设为10。剩下的没有提及的二进制位，全部为这个符号的unicode码。

假如有个字符占用3个字节，则：第一个字节以 1110 开始，第二三个字节以 10 开始。
```

```java
byte[] bytes = "联通".getBytes("GBK");
for (byte aByte : bytes) {
    // 11000001 10101010 11001101 10101000 --> 两个汉字，4个字节
    System.out.println(Integer.toBinaryString(aByte & 255));
}
```

## File

> 获取文件路径：`getCanonicalPath()`

```java
//返回定义时的路径，可能是相对路径，也可能是绝对路径，这个取决于定义时用的是相对路径还是绝对路径。
//如果定义时用的是绝对路径，那么结果跟getAbsolutePath()一样
file.getPath(); // ..\test1.txt
```

```java
//返回的是定义时的路径对应的相对路径，但不会处理"."和".."的情况
file.getAbsolutePath(); // F:\sp_project\spring\..\test1.txt
```

```java
//返回的是规范化的绝对路径，相当于将getAbsolutePath()中的"."和".."解析成对应的正确的路径
file.getCanonicalPath(); // F:\sp_project\test1.txt
```

> 常用方法

```java
boolean Mkdir();    //用于创建单层目录
boolean Mkdirs();   //.......多.....

boolean renameTo(); //重命名
```

##Properties

> 继承Hashtable，所以具有 map 集合的特点

```java
String filePath = "application.properties";
Properties properties = new Properties();
InputStream in = getClass().getClassLoader().getResourceAsStream(filePath);
if (null == in) {
    System.out.println("配置文件不存在：" + filePath);
} else {
    try {
        properties.load(in);
        String property = properties.getProperty("server.port", "8080"); //arg2: 默认值
        System.out.println("读取配置：" + property);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

## Convert

> int，byte[] `大端模式：高位在前，低位在后。【常用模式】`

```java
public static byte[] int2Bytes(int value, int len) {
    if (len > 4 || len <= 0) throw new RuntimeException("int 最大长度4个字节");

    byte[] bytes = new byte[len];
    for (int i = 0; i < len; i++) {
        bytes[i] = (byte) ((value >> 8 * (len - 1 - i)) & 0xFF);
    }
    return bytes;
}
```

```java
public static int bytes2Int(byte[] bytes) {
    byte[] dest = new byte[4];
    System.arraycopy(bytes, 0, dest, 4 - bytes.length, bytes.length);
    return (dest[0] & 0xFF) << 24
            | ((dest[1] & 0xFF) << 16)
            | ((dest[2] & 0xFF) << 8)
            | (dest[3] & 0xFF << 0);
}

```

> int，byte[] `小端模式：低位在前，高位在后`

```java
public static byte[] int2Bytes(int value, int len) {
    if (len > 4 || len <= 0) throw new RuntimeException("int 最大长度4个字节");
    
    byte[] bytes = new byte[len];
    for (int i = 0; i < len; i++) {
        bytes[i] = (byte) ((value >> 8 * i) & 0xFF);
    }
    return bytes;
}

```

```java
//offset: 从数组的第offset位开始
public static int bytes2Int(byte[] bytes, int offset) {
    return (bytes[offset + 0] & 0xFF)
            | ((bytes[offset + 1] & 0xFF) << 8)
            | ((bytes[offset + 2] & 0xFF) << 16)
            | ((bytes[offset + 3] & 0xFF) << 24);
}

```

> int，Hex

```java
public static String int2Hex(int value) {
    return Integer.toHexString(value);
}
```

```java
private static int hex2Int(String hexString) {
    return Integer.parseInt(hexString, 16);
}
```

> String，Hex

```java
public static String string2Hex(String value) {
    StringBuilder hexString = new StringBuilder();
    for (char aChar : value.toCharArray()) {
        hexString.append(Integer.toHexString(aChar));
    }
    return hexString.toString();
}
```

> String，byte[]

```java
byte[] bytes = "hello".getBytes(Charset.forName("utf-8"));
```

```java
String s = new String(bytes, Charset.forName("utf-8"));
```

> 校验和

```java
//第13位 -> 校验和 -> 前面所有字节的异或
data[13] = data[0];
for (int i = 1; i < 13; i++) {
    data[13] = (byte) (data[13] ^ data[i]);
}
```


# Reflect

## 创建对象

>（1）new创建：传统方式，必须预先知道要使用的类。引用类改变，就必须修改源码。

```java
Person person = new Person();
```

> （2）Cloneable方式：不推荐

```java
public class Person implements Cloneable{} //实现克隆接口

Person clone = (Person) person.clone();
```

> （3）反射方式：动态创建，效率相对低下，耗时是传统方式的 `3` 倍

```java
//先获取 clazz 对象（4种方式，常用1和2），再创建此 clazz 对象所表示的类的一个新实例
Class<?> clazz = Class.forName("com.example.reflect.Person"); //1
Class<?> clazz = getClass().getClassLoader().loadClass("com.example.reflect.Person"); //2
Class<? extends clazz> clazz = new Person().getClass(); //3
Class<Person> clazz = Person.class; //4

Object instance = clazz.newInstance(); //创建实例。当没有无参构造时，将报错
```

## 常用方法

> 构造器

```java
aClass.getConstructors();        //构造器：public
clazz.getDeclaredConstructors(); //构造器：all
```

```java
Constructor<?> constructor = clazz.getConstructor();
Person p = (Person) constructor.newInstance(); //等同于 new Person();

Constructor<?> constructor = aClass.getConstructor(String.class, Integer.class);
Object instance = constructor.newInstance("li", 18); //new Person("li", 18);
```

> 属性

```java
//public static String city;
Field city = aClass.getField("city"); //static -> 不依赖对象，传参 null
city.set(null, "Beijing");
System.out.println(city.get(null));

//public Boolean gender;
Field gender = aClass.getField("gender"); //non static --> 依附于对象 p1
Object p1 = aClass.newInstance();
gender.set(p1, true);
System.out.println(gender.get(p1));

//private Boolean young;
Field young = aClass.getDeclaredField("young"); //private --> 依附于对象，并暴力访问
Object p2 = aClass.newInstance();
young.setAccessible(true); //暴力膜
young.set(p2, true);
System.out.println(young.get(p2));
```

> 方法

```java
//public static String staticHello() {}
Method staticHello = aClass.getMethod("staticHello");
Object invoke = staticHello.invoke(null); //invoke为返回值; 调用 --> 不依赖对象

//public String publicHello(String name, Integer age) {}
Method privateHello1 = aClass.getMethod("publicHello", String.class, Integer.class);
Object p3 = aClass.newInstance();
invoke = privateHello1.invoke(p3, "li", 20); //依赖对象

//private String privateHello() {}
Method privateHello = aClass.getDeclaredMethod("privateHello");
privateHello.setAccessible(true);
Object p0 = aClass.newInstance();
Object invoke = privateHello.invoke(p0); //依赖对象 + 暴力膜
```

> main方法怎样传递参数？

```shell
把一个字符串数组作为参数传递到 invoke()，jvm怎么解析？？

按照jdk1.5，整个数组是一个参数； jdk1.4数组中的每一个元素是一个参数。
jdk1.5肯定没问题，但对于jdk1.4则会将字符串数组打散成一个个字符串作为参数，就会出现参数个数异常。
```

```shell
#正确做法
（1）将字符串数组转换成 Object 对象
（2）将字符串数组作为 Object 数组的一个元素
```

```java
Class<?> clazz = this.getClass().getClassLoader().loadClass("com.example.reflect.Person");
Method helloArray = clazz.getMethod("main", String[].class); //参数类型: String[].class

helloArray.invoke(null, (Object) new String[]{"aaa", "bbb"}); //正确1
// helloArray.invoke(null, new Object[]{new String[]{"aaa", "bbb"}}); //正确2

// helloArray.invoke(null, new String[]{"aaa", "bbb"}); //错误写法
```

