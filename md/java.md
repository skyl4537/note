[TOC]






# JVM

## 栈堆方法区

> 栈的特点

- 方法执行的内存模型。`每个方法被调用都会创建一个栈帧`（存储局部变量、操作数、方法出口等）。
- `JVM为每个线程创建一个栈`，用于存放该线程执行方法的信息（实际参数、局部变量等）。
- 栈属于线程私有，不能实现线程间的共享！
- 栈的存储特性是“先进后出，后进先出”。
- 栈是由系统自动分配，速度快！栈是一个连续的内存空间（相比于堆）。

> 堆的特点

- 用于存储创建好的对象和数组（数组也是对象）。

- `JVM只有一个堆，被所有线程共享！`

- 堆是一个不连续的内存空间，分配灵活，速度慢!

>方法区（特殊的堆）的特点

- `JVM只有一个方法区，被所有线程共享！`
- 方法区实际也是堆，只是用于存储类、常量相关的信息！
- 用来存放程序中永远不变或唯一的内容（类信息{Class对象}、静态变量、字符串常量等）。

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

public class Test {
    public static void main(String[] args) { //程序入口
        Student stu = new Student(18, "王");
        stu.dog = new Dog("Yello");
        stu.play();
    }
}
```

![](assets/内存变化.jpg)

> 程序执行过程的内存分析

## 对象初始化

> 对于语句`”Person p = new Person(“zhangsan”,20);`在内存中究竟做了什么事？

- 1). 因为 new 用到了 Person.class，所以会先找到 Person.class 文件并加载到内存中。
- 2). 执行该类中的` 静态代码块`。
- 3). 在堆内存中开辟空间，分配内存地址。
- 4). 在属性进行`默认初始化`（string 类型默认为 null，int 默认为 0） 。
- 5). 对属性进行`显式初始化`（即定义类时，属性含有默认值 `private String name = “xiaowang”`） 。
- 6). 对对象进行`构造代码块`初始化（没有static修饰的代码块）。
- 7). 对对象进行`构造函数`初始化。
- 8). 在栈内存中生成变量 p，将`内存地址赋给`栈内存中的 p 变量。

> 对象调用成员过程，`p.setName(”lisi”)`的执行过程？

- 1). 上述代码执行完后， 在栈内存中存在变量 p1， 在堆内存中存在Person对象。其中name=”zhangsan”和 age=20，方法区存在 setName()方法。其中，栈内存中的变量 p1 指向堆内存中的 Person 对象。
- 2). 当执行 `p1.setNmae(“lisi”);`时，先在栈内存中生成 setName()方法中的两个变量 this.name 和 name。由于是变量 p1 调用 setName()方法，所以 this 指向的是变量 p1 指向的对象。
- 3). 所以，当执行完 setName()方法后，堆内存中的 name=”zhangsan”变为 name=”lisi”。

```java
static class A {
    B b = new B();

    A() {
        System.out.print("A");
    }
}

static class B {
    B() {
        System.out.print("B");
    }
}

static class C extends A {
    B b = new B();

    private C() {
        System.out.print("C");
    }

    public static void main(String[] args) {
        new C();
    }
    //实例化C类需调用父类构造方法，实例化B，输出B，输出父类A。
    //回到子类，实例化B输出B，输出本身C。最终结果：BABC
}
```

- 子类通过构造函数进行实例化时，会先调用父类的构造函数。如果没有显示的指明调用哪个父类构造时，`默认调用父类的无参构造`。
- 普通成员变量的`显示初始化`优先于构造方法。

##静态代码块

> 静态代码块 > main方法 > 构造代码块 > 构造函数
>
> 父类静态代码块、子类静态代码块、main、父类构造代码块、父类构造函数、子类构造代码块、子类构造数

**静态代码块**：在类中只使用 static 关键字声明的代码块。`每个静态代码块只会执行一次`，用于初始化类的属性。由于 JVM 在加载类时会执行静态代码块，所以静态代码块优先主方法执行。

**构造代码块**：在类中直接用{ }扩起来的代码块。`每次new对象时都会被调用`，执行顺序`优先于`所有的构造函数。作用：给所有的对象进行统一、共性的初始化。

**构造函数**：每个类都有一个默认的无参构造函数，这个构造函数的权限和类保持一致。作用：是给通过此构造函数（构造函数不止一种）new 的对象进行初始化。

```java
static class F {
    static {
        System.out.print("F-S{} ");
    }

    {
        System.out.print("F{} ");
    }

    F() {
        System.out.print("F() ");
    }

    public static void main(String[] args) {
        System.out.print("F-M() ");
        new S(); //F-S{} F-M() S-S{} F{} F() S{} S()
    }
}

static class S extends F {
    static {
        System.out.print("S-S{} ");
    }

    {
        System.out.print("S{} ");
    }

    S() {
        System.out.print("S() ");
    }
}
```

```java
static class Add {
    static {
        int i = 5; //局部变量，不会影响i值。
    } //执行顺序在静态变量初始化之后（即如果设为非局部变量，则会影响静态变量的值）。

    private static int i, j; 

    public static void main(String[] args) { //i++，先++再使用；++i，先使用再++
        i--; //输出：-1
        add();
        System.out.println(i + " - " + j); //输出：1-0
        System.out.println(i + j + ++i); //输出：3
    }

    private static void add() {
        j = i++ + ++i; //输出：0(0)+0(1)
        System.out.println(i + " - " + j); //输出：1-0
    }
}
```

- **栈内存：** 在`函数`中定义的一些`基本类型的变量`和`对象的引用变量`都在函数的栈内存中分配。


- **堆内存：** 用来存放所有`new创建的对象`和`数组的数据`。


- **常量池：** 存放`字符串常量`和`基本类型常量`（public static final）。它们永不改变。



# Object

## 基础规约

```java
包名统一小写，统一单数，类名使用复数。 //com.example.mydemo.util.CommUtils
常量命名全部大写，单词间用下划线隔开。 //MAX_STOCK_COUNT
```
| 修饰符 \ 作用域 | 当前类 | 同一包 | 子孙类 | 其他包 |
| :-------------: | :----: | :----: | :----: | :----: |
|     public      |   √    |   √    |   √    |   √    |
|    protected    |   √    |   √    |   √    |        |
|     default     |   √    |   √    |        |        |
|     private     |   √    |        |        |        |

> toString()

```java
//默认返回：对象的类型名+@+内存地址值。---> 子类需要重写
public String toString() {
    return getClass().getName() + "@" + Integer.toHexString(hashCode());
}
```
> arraycopy()

```java
//数组拷贝，非常高效 ---> 此方法隶属于System
public void test() {
    List<String> list = Arrays.asList("a", "b", "c");
    String[] src = list.toArray(new String[list.size()]); //src
    String[] dest = new String[2]; //dest
    
    //从原数组的第1个开始拷贝，目标数组从第0个位置开始接收，拷贝2个元素
    System.arraycopy(src, 1, dest, 0, 2);
    Arrays.stream(dest).forEach(System.out::println); //b-c
}
```

## 运算符

> `Math.round(double num);`函数是取整函数，只关注小数点后第一位小数值。

```java
long l = Math.round(-1.45); //-1
long l = Math.round(-1.55); //-2 --> 同sql，先绝对值，再四舍五入，最后加符号

double d = Math.ceil(-1.45);  //-1.0
double d = Math.floor(-1.45); //-2.0 -->同sql，向下取值，即向x轴负方向取值

double d = Math.floor(-10 / -3); //3.0
double d = Math.floor(-10 % -3); //-1.0 -->同sql，结果符号位和被除数一致。 a%b = a-a/b*b
```

> 保留两位有效小数（两种方式）

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

> &，&& ---> 都是逻辑与运算符，但后者为短路运算

&， 左边无论真假，右边都要运算
&&，如果左边为真，右边参与运算； 如果左边为假，那么右边不参与运算。

```java
if (x < 1 & ++y > 0)  //y 会增长
if (x < 1 && ++y > 0) //y 不会增长
```

```sql
%(取模)      --> 左<边，值为左。//7%4= 3; 7%-4= 3; -7%4= -3; -7%-4= -3

^(异或)      --> 左右同为 false，左右不同为 true。(和其他差别很大)

<<(位运算-左) --> 乘以 2 的移动位数次幂 //3<<2 = 3 * 2^2 =12
>>(位运算-右) --> 除以 2 ...........  //6>>2 = 6 / 2^2 =1
```




## 继承和实现

> 抽象类和接口的异同？

```java
(2).共同点：不能实例化；以多态的方式使用
(3).不通电：单继承；多实现

(1).二者的定义
    a.声明的方式：抽象类使用关键字 abstract，接口使用关键字 interface.
    b.内部的结构：如下所示
    
interface MyInterface {
    //java7 -> 只能声明全局常量(public static final,可省略不写)和抽象方法(public abstract)
    public static final int STATIC_NUM = 7;

    void method();

    //java8 -> 声明 静态方法 和 默认方法
    static void staticMethod() {
        System.out.println("java8-静态方法");
    }

    default void defaultMethod() {
        System.out.println("java8-默认方法");
    }

    //java9 -> 声明 私有方法（静态和非静态两种）
    private static void privateMethod() {
        System.out.println("java9-私有方法"); //将冗余代码提取到通用的私有方法中
    }
}
```

> java 只支持`单继承`，不支持多继承。 但支持`多层继承`和`多实现`。

当多个父类中定义了相同函数名的函数，而各个函数的实现各不相同，则子类就不确定要调用哪一个。

**抽象类**：不能创建实例和允许有 abstract 方法的普通类。抽象类中可以有非抽象方法（如静态方法）。继承抽象类就必须实现其中的抽象方法，除非子类也是抽象类。

**接口：** 可看作一个特殊的抽象类，方法全是抽象的。一个类可以实现多个接口。接口`extends`接口。

```java
class AAA {}

abstract class ABC extends AAA { //抽象类 extends 具体类
    abstract void abc();
}

abstract class CDE extends ABC { //抽象类 extends 抽象类，但没有实现其抽象方法，默认可不写父类的抽象
    void cde() {
        System.out.println("cde");
    }
}

class DEF extends CDE { //具体类 extends 抽象类，必须得实现父类的抽象方法（包括父类的父类）。
    @Override
    void abc() {} //爷爷类的抽象方法
}
```

> this & super

- **this：** 子类调用【子类】的同名成员或方法。**super：** 子类调用【父类】的...。
- 构造函数间调用使用 `this() 或 super(name)` 语句，并且该语句只能放在构造函数第一行。
- 对于子类的构造函数，不管无参还是有参，如果没有显示指明调用哪个父类构造时，默认调用父类无参构造。

```java
class Outter {
    int num = 10;

    class Inner {
        int num = 20;
        
        void show() {
            int num = 30;
            
            System.out.println(num); //30
            System.out.println(this.num); //20
            System.out.println(Outter.this.num); //10 --> 切记不能使用 super
}}}
```

```java
class Super {
    private int id;

    public Super(int id) {
        this.id = id;
    }
}

//对于子类，不管是无参构造还是有参构造，如果没有显示指明调用哪个父类构造时，都会默认调用父类的无参构造
//当编译器尝试在子类中往这两个构造方法插入super()方法时，因为父类没有默认无参构造方法，所以编译器报错
class Sub extends Super {    
    public Sub() { //编译错误
    }
    public Sub(int id) { //编译错误
    }
}
```

> OverWrite & OverLoad：`重载与方法的返回值类型无关`

**重写：** 在子类中，出现和父类中一摸一样的方法。

**重载：** 同一类中，出现多个方法名一样，但参数列表（参数类型 + 个数 + 顺序）不一样的方法。

方法包括：  修饰符（可选）， 返回值类型，方法名，参数列表，方法体。
假设定义了两个只有返回类型不一样的方法： int add(Object o); 和 boolean add(Object o); 当调用者不关心返回值时，写作：`add(obj);`编译器如何区分到底调用的是哪个方法？？

> 方法重写

- final修饰的方法不可以被重写，如果子类对final修饰的方法进行重写，则编译报错。
- private修饰的方法对于子类不可见，不可重写。同样的 private，static 方法名同时出现在父类和子类，表示重新定义的方法，与父类无关。
- protect、default、public则对子类可见，可重写。重写时`不能缩小修饰符范围`，即不能将父类 public 方法重写为 private 方法。当方法被重写后，调用子类实例的同名方法时会`优先调用子类的重写方法`，不会再调用父类的方法。

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
//fun4：虽然是公有的访问权限，但为静态方法，无法被继承，并且子类无法定义同名方法。
```

```java
static class Father {
    public static int staticNum = 6;
    public int num = 6;

    public static void doStatic() {
        System.out.println("FATHER-STATIC");
    }

    public void doSth() {
        System.out.println("FATHER");
    }
}

static class Son extends Father {
    public static int staticNum = 8;
    public int num = 8;

    //不能重写父类的 static 方法，只是重新定义
    public static void doStatic() {
        System.out.println("SON-STATIC");
    }

    // @Override
    // public void doSth() {
    //     System.out.println("SON");
    // }
}

@Test
public void test() {

    Father father = new Son();

    //所有属性 和 静态方法 --> 看左，即和父类保持一致，调用父类的
    System.out.println(father.staticNum); //6
    System.out.println(father.num); //6
    father.doStatic(); //FATHER-STATIC

    //非静态方法 --> 若子类有重写,则调用子类方法。否则调用父类方法
    father.doSth(); //Father 或 SON
}
```



>多态原理

同一方法可以根据调用者的不同而具有不同的实现。

实现的原理是【动态绑定】，程序调用的方法在运行期才动态绑定。

追溯源码可以发现， JVM  通过参数的自动转型来找到合适的办法。





# 2.基础阶段

## Integer

> 基本数据类型：三大类，八小种。引用类型：类，接口，数组。

```java
布尔类型： boolean （1bit，1位）
字符类型： char （1byte，8位，[-128, 127]）
数值类型-整型： byte （1byte）， short （2byte）， int （4byte）, long （8byte） 
数值类型-浮点型： float （4byte）， double （8byte）
```

> 装箱 & 拆箱

```java
Integer i0 = new Integer(3);
Integer i1 = Integer.valueOf(3); //装箱：基本类型 --> 包装类

int num0 = i0.intValue(); //拆箱：包装类 --> 基本类型

Integer i = 4; //自动装箱：相当于Integer i = Integer.valueOf(4);

//自动拆箱：等号右边，将i对象转成基本数值 i.intValue() + 5;
//加法运算完成后，再次装箱，把基本数值转成对象
i = i + 5;
```

> 使用标准

- 所有 POJO 类属性必须使用`包装数据类型`，并且`不要设定默认值`。不设初值是提醒使用者在使用时必须自己显式地进行赋值。任何 `NPE（NullPointerExceptionrn） `问题，或入库检查，都由使用者来保证。
- RPC 方法的返回值和参数必须使用`包装数据类型`。
- 所有的局部变量 【推荐】 使用`基本数据类型`。

```java
//【反例】设定默认值，会在更新其它字段时，附带更新此字段，导致创建时间被修改成当前时间
Date editTime = new Date();
```
```
【反例】某业务的交易报表上显示成交总额涨跌情况，即正负 x%，x 为基本数据类型。
调用的RPC 服务，调用不成功时，返回的是默认值，页面显示：0%，这是不合理的，应该显示成中划线-。
所以包装数据类型的 null 值，能够表示额外的信息，如：远程调用失败，异常退出
```

>`【强制】`所有的相同类型的包装类对象之间值的比较，全部使用 equals()。

```java
Integer m = 128, n = 128;
System.out.println(m.equals(n)); //true --> 对比【值】
System.out.println(m == n); //false     -->  对比【内存地址】

Integer m1 = 127, n1 = 127;
System.out.println(m1.equals(n1)); //true
System.out.println(m1 == n1); //true

【Integer的缓存策略】jvm启动时，预先创建 [-128,127] 之间整数的 Integer 实例，然后保存到缓存数组 
IntegerCache。以后再创建此区间整数的包装类实例时，直接从缓存取，不会再 new 新对象，即使用同一个对象。
```


##method





> 方法调用，参数传递遵循值传递原则（`传递的是值或引用的拷贝，不改变原有值`）。

- 基本类型传递的是数据值的拷贝。在方法内对值类型操作不会改变原有值。

- 引用类型传递的是该对象的堆内存地址，即引用拷贝，但指向同一个对象。所以，在方法内对引用类型进行重新赋引用，不会改变原有值。但是对原有引用的属性进行操作时，可改变这个引用的属性值。

```java
private void doSth(int i, String s, Person p) {
    i += 1;
    s += "hello";
    p = new Person("li", 20);
    // p.age = 30; //将改变原有引用的属性值，其他则不会改变原有值
}
```


##final

>final，finally，finalize

```java
'final-类'：不可被继承，即 final 类没有子类。 final 类中的所有方法默认全是final方法。

'final-方法'：不能被重写。其中 private 和 static 方法默认就是 final

'final-变量'：即常量。值类型，不能修改其值；引用类型，不能修改其对应的堆内存地址，即不能重新再赋值。
```

```java
'finally'：异常处理的一部分，代码肯定会被执行，常用于释放资源。

'finalize'：Object类的一个方法，用于垃圾回收。
```

>字符串一经创建就不可改变 `final String`。以下代码，原始的 String对象 中的内容到底变了没有？

```java
String str = "Hello";
str = str + "world!";

没有改变。这段代码中，str 原始指向 "Hello"，对 str 进行了 + 操作运算之后，str 不再指向 "hello"，
而是指向 "Hello world!"，但字符串 "hello" 依然存在于内存之中，只是 str 这个引用变量不再指向它
```

> final修饰变量

final 修饰值类型（基本数据类型）的变量，那么这个变量的值就定了，不能变了。

final 修饰引用类型的变量，那么该变量存的是一个内存地址，该地址就不能变了。但是，该内存地址所指向的那个对象还是可以变的。就像你记住了人家的门牌号，但你不能管人家家里人员数量。

```java
public void doFinal(final int i, final StringBuilder sb) {
    // i = i + 1; //编译报错，因为final修饰的基本类型 --> 值不能变
    // sb = new StringBuilder(); //同上，修饰引用类型 --> 堆内存地址不能变,即引用不能变

    sb.append("java"); //编译通过 -> 引用变量所指向的对象中的内容，可以改变
}
```


> 为什么内部线程中引用外部对象要加final修饰符呢

```java
被内部线程引用的外部对象受到外部线程作用域的制约，有其特定的生命周期。
当外部对象在外部线程中生命周期已经结束，而内部线程中还在持续使用，怎样解决问题？'内部线程变量要访问一个已不存在的外部变量？'在外部变量前添加 final 修饰符，其实内部线程使用的这个变量就是外部变量的一个'复制品'，即使外部变量生命周期已经结束，内部复制品依然可用。
```



##finally

> try 中的 return 语句和 finally 代码块执行的先后顺序？

```java
int num = 5;
try {
    // int _res = num;
    // return _res;
    return num + 1; //这一语句 等价为 上面两行语句，所以在 finally 块中对 num 进行操作将不起作用
} finally {
    num = num + 2; //6
    // return num + 2; //7 --> 若 finally 块中也有 return 语句，则以 finally 为主，即返回 7
}
```













## Exception

![](assets/exception.png)

>Exception 和 Error 都继承自 `Throwable` 类。是异常处理机制的基本组成类型。

```java
Error：一般指与虚拟机相关的问题，程序本身无法恢复，建议程序终止。常见：'系统崩溃，内存溢出等'。
Exception：是程序正常运行中，可以预料的意外情况，应该捕获并进行相应的处理。
```

>Exception分类： RuntimeException（运行时异常）和 CheckedException（受检查异常，即编译器异常）。

```java
'RuntimeException''：是 RuntimeException 类及其子类，编译时能通过，在运行时出现，出现后程序直接终止。

（1）NullPointerException，（2）ClassCastException，（3）ArithmaticException（除数为0），
（4）ArrayIndexOutOfBoundsException（数组下标越界），（5）NumberFormatException（数字格式化）

Arrays.asList("a", "b").add("c"); //java.lang.UnsupportedOperationException

list.forEach(x -> {if("b".equals(x)){ list.add("c"); }}); //ConcurrentModificationException
```

```java
'CheckedException'：是 RuntimeException 以外的异常，类型上都属于Exception类及其子类（'不是具体的类'）。编译时直接报错，必须使用 try-catch 进行异常捕获，或使用 throws 语句声明抛出。

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
> throws & throw

```java
throws：通常被应用在'声明方法时'，用来指定可能抛出的异常。多个异常使用逗号隔开。
throw：通常用在'方法体中'，并且抛出一个异常对象。程序在执行到 throw 语句时立即停止，它后面的语句都不执行。

//throw 抛出异常后，如果想在方法调用处捕获并处理该异常，则需要用 throws 在方法声明中指明要抛出的异常。
int str2int(String str) throws NumberFormatException {
    if (str == null) {
        throw new NumberFormatException("null");
    }
    return Integer.parseInt(str);
}
```

> 自定义异常 一般继承`RuntimeException`，待证 ？？？








# String

## 基础概念

> 不可变性

字符串对象一旦在内存（堆）中创建，就无法修改。注意：

- `String类的所有方法都没有改变字符串本身的值，而是返回了一个新的String对象。`


- `对象不可变 与 对象的引用不可变 并不相等。`


- `final类不一定线程安全，如StringBuilder。`

字符串对象保存在字符串常量池。常量池中的对象是在**编译期确定**，在类被加载时创建。如果类加载时，该字符串常量在常量池中已存在，那就跳过，不会重新创建一个。与之相反，堆中的对象是在**运行期才确定**，在代码执行到new的时候创建的。

```java
//常见 final 类
LocalDateTime，StringBuilder（非线程安全），StringBuffer，Integer等。
```

```java
//不可变性的应用
'高效性'： 字符串的不可变能保证其 hashcode 永远保持一致，不需要重新计算。这就使得字符串很适合作为 Map 中的 Key，字符串的处理速度要快过其它的键对象。

'安全性'： String被广泛的使用在其他Java类中充当参数。比如网络连接、打开文件等操作。如果字符串可变，那么类似操作可能导致安全问题。

'线程安全'： 因为不可变对象不能被改变，所以他们可以自由地在多个线程之间共享。不需要任何同步处理。
```

> 创建字符串，可直接使用双引号的方式。如需在堆中创建一个新的对象，可以选择构造函数的方式。

```java
String s1 = new String("Hollis");
String s2 = new String("Hollis");
System.out.println(s1 == s2); //false
```

![](assets/string0.webp)

## 常用方法

> `intern()`：①.将字符串字面量放入常量池（如果池没有的话）②.返回这个常量的引用。

```java
String s1 = "Hollis"; 
String s2 = new String("Hollis");
String s3 = new String("Hollis").intern();

System.out.println(s1 == s2); //false
System.out.println(s1 == s3); //true

可以简单的理解 s1 和 s3 做的事情是一样的。
都是定义一个字符串对象，然后将其字符串字面量保存在常量池中，并把这个字面量的引用返回给定义好的对象引用。

对于s3，在不调 intern()情况，s3指向的是JVM在堆中创建的那个对象的引用的（如图中的s2）。
但是当执行了 intern()方法时，s3将指向字符串常量池中的那个字符串常量。
```

![](assets/string1.webp)

> 两个问题

- **Q1：** `String s = new String("hollis");`定义了几个对象。


- **Q2：** 如何理解`String`的`intern()`方法？

```java
A1： 若常量池中已经存在"hollis"，则直接引用，也就是只会创建一个对象。如果常量池中不存在，则先创建"hollis"后引用，也就是有两个。
A2： 当一个String实例调用 intern()方法时，JVM会查找常量池中是否有相同Unicode的字符串常量，如果有，则返回其的引用，如果没有，则在常量池中增加一个Unicode等于str的字符串，并返回它的引用。

new String() 所谓的'如果有的话就直接引用'，指的是Java堆中创建的String对象中包含的字符串字面量，直接引用字符串池中的字面量对象。也就是说，还是要在堆里面创建对象的。
而 intern() 中说的'如果有的话就直接返回其引用'，指的是会把字面量对象的引用直接返回给定义的对象。这个过程是不会在Java堆中再创建一个String对象的。
```

> 常见测试

```java
public void test() {
    String s1 = "abc";
    String s2 = "abc";
    String s3 = new String("abc");
    String s4 = "ab" + "c";

    System.out.println(s1 == s2); //true
    System.out.println(s1 == s3); //false
    System.out.println(s1 == s4); //true
        
    String s5 = "ab";
    String s6 = s5 + "c";
    System.out.println(s4 == s6); //false
}
```

- `s1` 先在常量池中查找是否存在"abc"（使用 equals() 确定）， 存在则让 s1 指向这个值，没有则新建。
- `s2` 同上
- `s3` 其中，String s3 只是定义了一个名为 s3 的String类型变量，并没有创建对象。new String() 才是真正的在堆空间上创建一个字符串对象，然后将 s3 指向新建对象的堆内存地址，所以 s1 == s3 比较结果为false。s1保存在字符串常量池，而 s3 保存在堆内存中。
- `s4` 先在常量池中创建 2 个字符串对象，再将 s4 指向已有的 "abc"。
- `String s6 = s5 + "c";` 和 `String s4 = "ab" + "c";` 的区别： 对于字符串常量相加的表达式，不是等到运行期才去进行加法运算处理，而是在编译期直接将其编译成一个这些常量相连的结果。因此，`String s4 = "ab" + "c";`可转化为`String s4 = "abc";`，但s6并不是字符串常量相加，不能转化。

> StringBuilder

StringBuilder 内部拥有一个数组用来存放字符串内容。当进行字符串拼接时，直接在数组中加入新内容，并自动维护数组的扩容，不会产生中间字符串。

|     类型      |    安全    |                            特点                            |
| :-----------: | :--------: | :--------------------------------------------------------: |
|    String     | `线程安全` | 直接进行字符串拼接，会产生大量中间字符串，并且时间消耗长。 |
| StringBuffer  |  线程安全  |                   支持同步锁，性能稍差。                   |
| StringBuilder | 线程不安全 |        单线程进行大量字符串操作时，推荐使用（√）。         |

```java
public void test() {
    String res = "";
    String str = "hello";
    
    for (int i = 0; i < 1000; i++) {
        res += str;
        
        //上一行代码，编译器最终会编译为类似下面的代码：
        //res = new StringBuilder(res).append(str).toString();
    }
}

//每次循环都需要创建一个 StringBuilder 对象（创建对象需要耗费时间和内存）。
//随着循环次数的增大，res字符串也就越长，把res中的字符复制到新建的 StringBuilder 中花费的时间也就越长。
//而且 StringBuilder(res).append(str).toString(); 会创建一个临时的字符串，随着循环次数的增加，
//这个操作花费的时间也会越来越长。总之，随着循环变量 i 的增大，每次循环会变得越来越慢。
```

## 其他API

> 字符串连接符 `+`

- 两边都是数值类型时，为运算符，即相加求和。
- 两边至少有一个为字符串类型时， 则为字符串连接符。底层原理 `StringBuilder.append()`。

> == 和 equals()

==： 对于基本数据类型，比较其值； 对于引用数据类型，比较其堆内存地址。

equals()： Object中默认调用`==`，根据需求重写此方法。String类重写为：比较字符串内容。

> switch中的String：`switch只支持 int 和 枚举类型`

```java
'char, byte, short, int, Character, Byte, Short, Integer, String, or an enum'
都可以隐式转换成int类型。其中，jdk1.7之后支持的 String，底层原理采用的是：先进行 hashCode() 比较，再进行 equals()比较（可能出现hash值碰撞）。 不支持 boolean 类型。
```

>length()

```java
public void test() {
    String[] array = {"a", "b", "c"};
    System.out.println("数组的长度: " + array.length); //数组的属性-length

    System.out.println("字符串长度: " + "abc".length()); //字符串的方法-length()
}
```

> split()

```java
public void test() {
    String str = "a,b,c,,";
    String[] ary = str.split(",");
    
    //字符串切割，需检查最后一个分隔符后有无内容，否则可能抛 IndexOutOfBoundsException
    System.out.println(ary.length); //预期大于 3，结果是 3
}
```
> reverse 字符串反转

```java
//方式1：借用StringBuilder
String str = "我是中华人民共和国公民";
StringBuilder reverse = new StringBuilder(str).reverse();
```
```java
//方式2：for循环
char[] chars = str.toCharArray();
StringBuilder sb = new StringBuilder();
for (int i = chars.length - 1; i >= 0; i--) {
    sb.append(chars[i]);
}
```

> replace()，replaceAll()，replaceFirst()

`replace()`：参数是 char 和 CharSequence，即支持字符和字符串的替换。

`replaceAll()`：参数是 regex，即基于正则表达式的替换。

`replaceFirst()`：参数也是 regex，但不同的是只替换第一个，即基于正则替换第一个满足条件的。

```java
str.replaceAll(".", "*"); //把字符串中所有字符转换成星号，"."在正则表达式中表示所有字符。
```

> 特殊空格1

```java
char char1 = ' '; //普通空格，ASCII值是32
char char2 = ' '; //特殊的空格，ASCII值为160
System.out.println((int) char1); //32
System.out.println((int) char2); //160
```

特殊空格是一个不间断空格(non-breaking space)，本质就是页面上`&nbsp;`所产生的空格，作用就是在页面换行时不被打断。

如果使用普通的空格，在换行时人名就会被打断，导致 Zhang 在第一行末尾，而 Xiaoming 跑到第二行开头。但是，如果使用不间断空格，则可以保持完整的人名在同一行的末尾（word中也有这种空格的使用）。

```java
页面某一行的末尾是一个人名Zhang Xiaoming //不间断空格

页面某一行的末尾是一个人名Zhang //使用普通空格
Xiaoming
```

**注意**：不间断空格有个问题，就是它无法被`trim()`所裁剪，也无法被正则表达式的`\s`所匹配，也无法被StringUtils的`isBlank()`所识别，也就是说，无法像裁剪普通空格那样移除这个不间断空格。

**正确做法**：利用不间断空格的Unicode编码（`\u00A0`）或者ASCII值（160）先进行替换，然后再`trim()`。

```java
String str = "abc ";
// String replace = str.replaceAll("\\u00A0", " ").trim(); //Unicode编码
String replace = str.replace((char) 160, ' ').trim(); //ASCII值
System.out.println(str.length() + " - " + replace.length()); //4 - 3
```

> 特殊空格2

对于全角空格，使用 trim() 方法也无法去除。必须利用其ASCII值（12288）。

```java
String str = "abc　";
String replace = str.replace((char) 12288, ' ').trim();
System.out.println(str.length() + " - " + replace.length()); //4 - 3
```



# Collection

![](assets/collection.png)

##基础概念

> 数组 & 集合

数组： 固定长度（不能动态改变数组的长度），只能放一种类型。
集合： 可变长度， 可以存多种类型（在不考虑泛型的前提下）。

```java
List list = new ArrayList();
list.add(5); //集合放原始类型，其实是通过装箱拆箱来实现的。以前原生类型只能用数组，现在集合也可以了。
list.add("5");
list.add(new Integer(5));
```

> 双括号初始化

使用双括号初始化（double-brace syntax）快速建立并初始化，`简洁但效率低`。原因：

(1). 双大括号初始化方法生成的.class文件要比常规方法多

(2). 双大括号初始化方法运行时间要比常规方法长

(3). 可能造成内存泄漏

```java
//第一层花括号，定义了一个继承自 ArrayList 的匿名内部类
//第二层花括号，在匿名内部类中定义了一个 构造代码块
//通过 new 得到ArrayList的子类的实例化，然后上转型为ArrayList的引用
//得到的 list 实际上是ArrayList的子类的引用，但在功能上没有任何改变
//相比于常规标准方式进行初始化要简洁许多，但代码可读性相对会差
Map<Integer, String> map = new HashMap<Integer, String>() {{
    put(1, "a");
    put(2, "b");
    put(3, "c");
}};

List<String> list = new ArrayList<String>() {{
    add("a");
    add("b");
    add("c");
}};
```

## List & Set

> 二者特点

List： 排列有序（存入和取出的顺序一定相同，存在索引），元素可重复。
Set： 排列无序，元素不可重复。

区别：List的`contains()`和`remove()`底层调用`equals()`。但Set却是`hashCode()`和`equals()`。

>Set相关

```java
"Set如何保证元素唯一性？"
先比较 hashCode()，如果相同，继续比较 equals() 是否为同一个对象。

"Set的hashCode()相同，但equals()不同，怎么存储呢？"
在同样的哈希值下顺延（可认为哈希值相同的元素放在一个哈希桶中），也就是哈希一样的存一列。
```

>TreeSet排序是如何进行的呢？？`两种比较器同时存在，以集合自身比较器为准`

```java
//(1).元素实现接口Comparable
public class Dog implements Comparable<Dog> {
    @Override
    public int compareTo(Dog o) {
        if (this.name.compareTo(o.getName()) == 0) { //先比较 name,再比较 age
            return Integer.compare(this.age, o.getAge());
        } else {
            return this.name.compareTo(o.getName());
        }
    }
}
```

```java
//(2).集合添加比较器：当元素自身不具备比较性，或具备的比较性不满足要求时，让集合自身具备比较性
TreeSet<Dog> dogSet = new TreeSet<>(new Comparator<Dog>() {
    @Override
    public int compare(Dog o1, Dog o2) {
        int compare = o1.getName().compareTo(o2.getName()); //先比较 name，再比较 age
        if (compare == 0) {
            return Integer.compare(o1.getAge(), o2.getAge());
        } else {
            return compare;
        }
    }
});
```


> ArrayList & Vector & LinkedList

同步性：Vector 是同步的（线程安全），ArrayList线程序不安全
数据增长：当需要增长时，Vector 默认增长一倍，ArrayList 却是 0.5






## Iterator

> 禁止在 foreach 里进行元素的 remove/add 操作。

```java
public void test() {
    List<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));
    
    for (String s : list) {
        if ("a".equalsIgnoreCase(s)) {
            list.remove(s); //抛异常 -> ConcurrentModificationException
        }
    }
    System.out.println(JSON.toJSON(list));
}
```

- 增强for循环，其实是Java提供的语法糖，其实现底层原理还是借助 Iterator 实现。
- ArrayList不是线程安全的，因此在使用 Iterator 的过程中，如果有其他线程修改了list，那么将抛出ConcurrentModificationException，这就是所谓 fail-fast机制。

**fail-fast机制：主要是通过 modCount （修改次数）实现，对ArrayList内容的修改都将增加这个值。**

在 Iterator  初始化过程中会将这个值赋给迭代器的 expectedModCount。在迭代过程中，判断 modCount 跟 expectedModCount 是否相等，如果不相等就表示已经有其他线程修改了 list。

注意：modCount 声明为 volatile，保证线程之间修改的可见性。

> **方案1：** 直接使用普通for循环进行操作。因为普通for循环并没有用到 Iterator 的遍历。

```java
for (int i = 0; i < 1; i++) {
    if (list.get(i).equals("a")) {
        list.remove(i);
    }
}
```

> **方案2： ** 直接使用Iterator进行操作。

```java
Iterator<String> iterator = list.iterator();
while (iterator.hasNext()) {
    String next = iterator.next();
    if ("a".equalsIgnoreCase(next)) {
        // list.remove(next); //也会抛异常 -> ConcurrentModificationException
        
        iterator.remove();
    }
}
```
> **方案3： ** 使用 Jdk1.8 中提供的 filter 过滤

```java
list = list.stream().filter(x -> !x.equalsIgnoreCase("a")).collect(Collectors.toList());
```

> **【并发场景】方案4：** 直接使用 fail-safe 的集合类。这些集合容器在遍历时，`不是直接在集合内容上访问的，而是先复制原有集合内容，在拷贝的集合上进行遍历`。

```java
ConcurrentLinkedDeque<String> list = new ConcurrentLinkedDeque<>(Arrays.asList("a", "b", "c"));
for (String s : list) {
    if (s.equals("a")) {
        list.remove();
    }
}
```





## Map

Map 存储的是键值对。Map 集合中 Key 要保证唯一性。

Map 集合没有直接取出元素的方法，而是先将key集合或value集合转成 Set 集合，在通过迭代获取元素。

Map  存储元素使用 put() 方法， Collection  使用 add() 方法。

**Map/Set的key为自定义对象时，必须重写hashCode()和equals()**



## 常用方法

> NULL：高度注意 Map 类集合 K/V 能不能存储 null 值的情况

|       集合类        |      Key      |     Value     |    Super    |     说明     |
| :-----------------: | :-----------: | :-----------: | :---------: | :----------: |
|      Hashtable      | 不允许为 null | 不允许为 null | Dictionary  |   线程安全   |
| `ConcurrentHashMap` | 不允许为 null | 不允许为 null | AbstractMap | `分段锁技术` |
|       TreeMap       | 不允许为 null | `允许为 null` | AbstractMap |  线程不安全  |
|      `HashMap`      | `允许为 null` | `允许为 null` | AbstractMap |  线程不安全  |

> 去重：不应该使用 List.contains() 进行遍历

对于存储大量不重复元素，应该选用 Set 集合，利用其元素唯一性特点。而不应该选用 List，去使用 List.contains() 进行遍历，对比，去重操作。

> 初始化大小：集合初始化时，尽量指定初始值大小。大小应和实际存储元素个数相近，减少扩容次数。
> <https://www.jianshu.com/p/64f6de3ffcc1>

```java
List<String> list = new ArrayList<>(5); //默认16，加载因子0.75
```

> asList()：返回对象是 Arrays 内部类，并没有实现集合的修改方法（add，remove，clear）。

```java
List<String> asList = Arrays.asList("a", "b", "c");
// asList.add("d"); -> UnsupportedOperationException

ArrayList<String> list = new ArrayList<>(asList); //正解,先转换
list.add("d");
```
> subList()：返回的是 ArrayList 的内部类 SubList，本质是 ArrayList 的一个视图，对于 SubList 子列表的所有操作最终会反映到原列表上。

```java
ArrayList<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));
List<String> subList = list.subList(0, 2);

ArrayList<String> list1 = (ArrayList<String>) subList; //异常：ClassCastException
```
> foreach()：`Map遍历推荐使用 entrySet 集合`，而不是 keySet 方式进行遍历

```java
Map<String, String> map = new HashMap<String, String>() {{
    put("k1", "v1");
    put("k2", "v2");
}};

//(1).keySet() 其实遍历了 2 次
//一次是转为 Iterator 对象，另一次是从Map中取出 key 所对应的 value
for (String key : map.keySet()) {
    System.out.println(key + ":" + map.get(key));
}

//(2).entrySet() 只遍历一次。
//遍历1次就把 kV 都放到了 entry 中，效率更高。JDK8的 Map.forEach() 就是这个原理
for (Map.Entry<String, String> entry : map.entrySet()) {
    System.out.println(entry.getKey() + ":" + entry.getValue());
}

//(3).JDK8 -> Map.forEach()
map.forEach((k, v) -> System.out.println(k + ":" + v));
```
> sort()：JDK7 以上，Comparator 要满足自反性，传递性，对称性。不然 Arrays.sort()，Collections.sort()会报异常 IllegalArgumentException。所以，`对于基本数据类型，要采用其包装类的 Compare(x, y) 进行比较`。

```java
new Comparator<Person>() {
    @Override
    public int compare(Person o1, Person o2) {
        //return o1.getAge() > o2.getAge() ? 1 : -1; //没有处理相等的情况

        return Integer.compare(o1.getAge(), o2.getAge()); //推荐使用
    }
};
```

> 相互转化

```java
String[] strArray = {"a", "b", "c"};
List<String> list = Arrays.asList(strArray); //Array 转换 List

//【不推荐】直接使用 toArray() 无参方法，因为其返回值只能是 Object[]。
String[] array = list.toArray(new String[list.size()]); //List 转 Array
```

## 算法相关

> 冒泡排序

```java
int[] nums = {3332, 6367, 25623, 241, 12834};
for (int i = 0; i < nums.length - 1; i++) {

    // 第一趟排序考虑(0, 总趟数-0)
    // 第一趟排序过后，最后一位一定是最大的，不再考虑。即第二趟排序只需考虑(0, 总趟数-1)
    // 第三次为(0, 总趟数-2)....总结即：总趟数(length-1)减去趟次 (j)
    for (int j = 0; j < nums.length - i - 1; j++) {
        if (nums[j] > nums[j + 1]) { //升序
            int tmp;
            tmp = nums[j + 1];
            nums[j + 1] = nums[j];
            nums[j] = tmp;
        }
    }
}
Arrays.stream(nums).forEach(System.out::println); //升序排列
```

> 选择排序

```java
//首先，在未排序序列中找到最小（大）元素，存放到排序序列的起始位置
//然后，再从剩余未排序元素中继续寻找最小（大）元素，放到已排序序列的末尾。
//以此类推，直到所有元素均排序完毕

```

> 单词计数 & map值排序

```java
//(1).单词计数
String str = "我是中华人民共和国公民";
HashMap<Character, Integer> map = new HashMap<>();
char[] chars = str.toCharArray();
for (char aChar : chars) {
    // if (null == count) { //未统计到的字符
    //     map.put(aChar, 1);
    // } else {
    //     map.put(aChar, 1 + count);
    // }
    map.merge(aChar, 1, (a, b) -> b + a); //lambda简化
}
map.forEach((x, y) -> System.out.println(x + " - " + y));

//(2).map值排序
List<Map.Entry<Character, Integer>> list = new ArrayList<>(map.entrySet());
Collections.sort(list, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));
// Collections.sort(list, Comparator.comparing(Map.Entry::getValue)); //lambda简化
System.out.println(JSON.toJSONString(list));
```







# Date

## SimpleDateFormat

> `SimpleDateFormat`并不是一个线程安全的类，在多线程情况下，会出现异常。

```java
private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

public void test() throws InterruptedException {
    ExecutorService service = Executors.newFixedThreadPool(25);
    for (int i = 0; i < 20; i++) {
        service.execute(() -> {
            try {
                System.out.println(sdf.parse("2019-04-15 09:45:59"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }
    // 等待上述的线程执行完，再关闭线程池。二者配合使用
    service.shutdown();
    service.awaitTermination(1, TimeUnit.DAYS);
}
```

`把 SimpleDateFormat 实例定义为静态变量，在多线程情况下会被多个线程共享。`B线程会读取到A线程的时间，就会出现时间差异和其它各种问题。SimpleDateFormat 和它继承的 DateFormat 类都不是线程安全的。

> **方案1：** 只在需要的时候创建实例，不用static修饰。

```java
//缺点：加重了创建对象的负担，会频繁地创建和销毁对象，效率较低。
public static String formatDate(Date date) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return sdf.format(date);
}

public static Date parse(String strDate) throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return sdf.parse(strDate);
}
```

> **方案2：** synchronized大法好。

```java
//缺点：并发量大的时候会对性能有影响，线程阻塞。
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

> **【推荐】方案3：** ThreadLocal，确保每个线程单独一个SimpleDateFormat对象。

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

> **【推荐】方案4：** `基于JDK1.8的 DateTimeFormatter`

```java
private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

public static String formatDate2(LocalDateTime date) {
    return formatter.format(date);
}

public static LocalDateTime parse2(String dateNow) {
    return LocalDateTime.parse(dateNow, formatter);
}
```



# 基本概念

## static

> static 方法是否可以调用非 static 方法？

不可以。因为非 static 方法是要与对象关联在一起的，必须创建一个对象后，才可以在该对象上进行方法调用。而 static 方法调用时不需要创建对象，可以通过类直接调用。

在 static 方法中调用非 static 方法时，可能还没有实例化对象， 这就与以上逻辑不符。

>静态变量 & 实例变量

**静态变量**：static 修饰，随着类的加载而分配内存空间，随着类的消失而消失。存在于方法区。

**实例变量**：随着对象的建立而分配内存空间，随着对象的消失而消失。存在于堆内存。

```java
class Test{
    static int staticNum = 0;
    int num = 0;
    
    Test(){
        staticNum++;
        num++;

        //对于 staticNum，全局唯一份。每实例化一个Test对象，staticNum 就加1
        //但是，对于 num，每实例化一个Test对象，就会重新分配一个，所以一直都是1
        sout(staticNum + " - " + num);
    }
}
```







## for

> `for(初始化; 布尔表达式; 更新)` 等同于 `初始化; while(布尔表达式){ 更新; }`

**初始化**：对循环可能要用到的值进行初始化，相当于for循环内部的一个局部变量

**布尔表达式**：当表达式结果为true时继续执行，为false时终止循环

**更新**：在一次循环结束后进行更新，一般用于修改初始化值，从而影响循环布尔表达式的

```java
private boolean print(char c) {
    System.out.print(c);
    return true;
}

@Test
public void test() {
    int i = 0;
    for (print('a'); print('b') && (i < 2); print('c')) {
        i++;
        print('d');
    }
}
//输出结果为：a-bdc-bdc-b.（注意最后一个b）
```

```java
public void test() {
    int count = 0;
    int num = 0;
    for (int i = 0; i < 100; i++) {
        num = ++num; //先自增，再使用
        count = count++; //先使用，再自增
    }
    System.out.println(num +" - "+ count); //100 - 0
}
```





## switch

>switch能否作用在 byte 上，能否作用在 long 上，能否作用在 String 上？

switch 作用在 byte，short，char，int 这几个基本数据类型和封装类型 或 enum 枚举常量。其中，byte，short，char 都可以隐含转换为 int。 但是不支持 boolean。

另外，java7支持 String类型，其实是通过调用 `String.hashCode()`，将String转换为 int。

> **break关键字：** 当遇到break，switch语句终止。如果没有break出现，程序会继续执行下一条case语句，直到出现break语句。

```java
private int getValue(int i) {
    int result = 0;
    switch (i) {
        case 1:
            result += i;
        case 2:
            result += i * 2;
        case 3:
            result += i * 3;
    }
    return result;
}

System.out.println(getValue(2)); //2*2+2*3=10
```

> 如何跳出当前的多重嵌套循环？

让外层的循环条件表达式的结果，可以受到里层循环体代码的控制。

```java
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








## instanceof

> instanceof关键字可以用来判断对象是否是一个类的一个实例。

```java
class A{}
class B extends A{}
class C extends A{}
class D extends B{}

@Test
public void test() {
    A d = new D();
    System.out.println(d instanceof A); //true
    System.out.println(d instanceof B); //true
    System.out.println(d instanceof C); //false
    System.out.println(d instanceof D); //true
}
```



## Integer

> int & Integer

```java
int 是java提供的 8 种原始数据类型之一，默认值为 0    
Integer是java为 int 提供的封装类，默认值为 null

Integer 可以区分出未赋值和值为 0 的两种情况，int 则无法表达出未赋值的情况。
如要想表达出没有参加考试和考试成绩为0的区别，就只能使用 Integer

另外，Integer 提供了多个与整数相关的操作方法。如将一个字符串转换成整数。
Integer中还定义了表示整数的最大值和最小值的常量。
```

> `short s1 = 1; s1=s1+1; 和 s1+=1;`有无区别？

前者，s1+1 运算时会`自动提升表达式的类型，`所以结果是 int 型。再赋值给 short 类型 s1 时，编译器将报错。

后者，`编译器对于 += 运算符会进行特殊处理`，因此可以正确编译。

```java
double d = 5.1e11;
float f = 22.2;
int i = 0.0;
Double oD = 2;

//（1）5.1e11表示5.1*10^11，在double的取值范围内正确。
//（2）22.2为double类型，赋值给float类型需要进行强制类型转换否则无法通过编译。
//（3）0.0虽然看似是0，但是表示的是double类型，赋值给int类型时同样需要强制类型转换，否则无法通过编译。
//（4）double od = 2可以，但包装类Double接收时不会进行自动类型转换，因此接收整型值2时无法通过编译。
```



> char 型变量中能不能存贮一个中文汉字? 

`char 类型变量用来存储 Unicode 编码的字符集`，Unicode 编码字符集中包含了汉字。所以，char 型变量中当然可以存储汉字。不过，如果某个特殊的汉字没有被包含在 Unicode 编码字符集中，那么，这个 char 型变量中就不能存储这个特殊汉字。

**补充：** Unicode 编码占用两个字节,，char 类型的变量也占两个字节。































## Collection



> map值排序：统计单词频率，并按照频率排序

```java
public void test() {
    String str = "asafaaffafaee";

    //(1).统计单词频率
    Map<Character, Integer> map = new TreeMap<>();
    char[] chars = str.toCharArray();
    for (char aChar : chars) {
        // Integer count = map.get(aChar);
        // if (null == count) {
        //     map.put(aChar, 1);
        // } else {
        //     map.put(aChar, count + 1);
        // }
        map.merge(aChar, 1, (a, b) -> a + b); //lambda简化
    }

    //(2).map值排序
    List<Map.Entry<Character, Integer>> list = new ArrayList<>(map.entrySet());
    Collections.sort(list, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));
    // Collections.sort(list, Comparator.comparing(Map.Entry::getValue)); //lambda简化

    //[{"s":1},{"e":2},{"f":4},{"a":6}]
    System.out.println(JSON.toJSONString(list));
}
```



> HashMap & HashTable & ConcurrentHashMap

​    HashTable线程安全, 在多线程情况下, 同步操作能保证程序执行的正确性.
    但是, HashTable每次同步执行时,都要锁住整个结构.
    

    ConcurrentHashMap 正是为了解决这个问题而诞生的.
    ConcurrentHashMap 锁的方式是稍微细粒度的, 将hash表分为16个桶(默认值), 诸如get,put,remove等常用操作只锁当前需要用到的桶。
    原来只能一个线程进入, 现在却能同时16个写线程进入 (写线程才需要锁定,而读线程几乎不受限制), 并发性的提升是显而易见的!!!
    
    ConcurrentHashMap的'读取操作没有用到锁定',所以读取操作几乎是完全的并发操作.
    而'写操作锁定的粒度又非常细', 比起之前又更加快速(桶越多,表现越明显). '只有在求size等操作时才需要锁定整个表'

##HashMap & Hashtable









# IO

##IO流

`IO操作推荐使用：org.apache.commons.io`

按流向分为：输入流，输出流。

按操作数据分为：字节流 （如音频，图片等），字符流（如文本）。

字节流的抽象基类：InputStream，OutputStream。字符流的抽象基类：Reader，Writer。

> 文件拷贝：字节流 + 字符流

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
        bw.newLine(); //由于 readLine()方法不返回行的终止符，所以手动写入一个行分隔符。

        bw.flush(); //只要用到缓冲区技术，就一定要调用 flush()方法刷新该流中的缓冲。
    }
} catch (IOException e) {
    System.out.println("系统找不到指定的文件：" + src);
}
```

> 转换流：字节流转换成字符流 InputStreamReader()

```java
br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
```

> 区别 close(); flush();

- **close()** `先刷新一次缓冲区，再关闭流对象`，关闭之后，流对象将不可用
- **flush()** `仅仅刷新缓冲区`（一般写字符时，先写入缓冲区），刷新之后，流对象还可以继续使用

> 字符编码

**GBK**：占用两个字节，比GB2312编码多了很多汉字，如"镕"字。

**UTF-8**：Unicode一种具体的编码实现。是一种变长编码方式，使用1-4个字节进行编码，有利于节约网络流量。

```java
//UTF-8编码规则
① 对于单字节的符号，字节的第一位设为0，后面7位为这个符号的unicode码。因此对于英语字母，UTF-8编码和ASCII码是相同的。

② 对于n字节的符号（n>1），第一个字节的前n位都设为1，第n+1位设为0，后面字节的前两位一律设为10。剩下的没有提及的二进制位，全部为这个符号的unicode码。

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

> file.getPath()，getAbsolutePath()，`getCanonicalPath() 推荐`

```java
File file = new File("..\\test1.txt");

//返回定义时的路径，可能是相对路径，也可能是绝对路径，这个取决于定义时用的是相对路径还是绝对路径。
//如果定义时用的是绝对路径，那么结果跟getAbsolutePath()一样
file.getPath();// ..\test1.txt

//返回的是定义时的路径对应的相对路径，但不会处理"."和".."的情况
file.getAbsolutePath();// F:\sp_project\spring\..\test1.txt

//返回的是规范化的绝对路径，相当于将getAbsolutePath()中的"."和".."解析成对应的正确的路径
file.getCanonicalPath();// F:\sp_project\test1.txt
```

> 常用方法

```java
boolean Mkdir();    //用于创建单层目录
boolean Mkdirs();   //.......多.....

boolean renameTo(); //重命名
boolean b = new File(src).renameTo(new File(dest)); //重命名-DEMO
```

## Properties

> 继承Hashtable，所以具有 map 集合的特点：`class Properties extends Hashtable`

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













































# Socket

Socket 就是为网络服务提供的一种机制，网络通信其实就是 Socket 间的通信。通信两端都是 Socket，数据在两个 Socket 间通过 IO 传输。

> 域名解析过程

在浏览器地址栏中输入<https://www.baidu.com/>后，系统会首先自动从Hosts文件中寻找对应的IP地址，一旦找到，系统会立即打开对应网页，如果没有找到，则系统会再将网址提交DNS域名解析服务器进行IP地址的解析。

本地Hosts文件所在位置：`C:\Windows\System32\drivers\etc`

配置本地Hosts文件，一方面可以加快网站的访问速度（配置网站对应的正确IP），另一方面可以阻止某些流氓软件的网络请求（配置网站的IP为127.0.0.1）。

> 网络模型

```sql
OSI七层模型        TCP/IP四层模型
------------------------------------
应用层
表示层            应用层 ---> http/https FTP
会话层
-------------------------------------
传输层            传输层 ----> TCP/UDP
-------------------------------------
网络层            网际层 ----> IP协议
--------------------------------------
数据链路层
物理层            主机至网络层
--------------------------------------
```

> InetAddress API使用

```java
// InetAddress inet = InetAddress.getLocalHost(); //本机
InetAddress inet = InetAddress.getByName("192.168.8.8"); //指定ip

String name = inet.getHostName(); //主机名
String ip = inet.getHostAddress(); //IP字符串
```

## UDP

UDP：面向无连接。数据包一次传输最大64K。不可靠，容易丢包，但是速度快。`例子：发短信`

TCP：需要先通过3次握手建立链接，所以是可靠协议，但效率稍低。传输数据量无限制。`例子：打电话`

>UDP通信

```java
public static void main(String[] args) throws Exception {
    ExecutorService pool = Executors.newCachedThreadPool();

    DatagramSocket ds = new DatagramSocket(7001); //数据包对象 DatagramSocket
    pool.execute(() -> sendMsg(ds, "192.168.8.7", 8001, "hello", "客户端-发送："));
    pool.execute(() -> recvMsg(ds, "客户端-接收："));

    DatagramSocket ds1 = new DatagramSocket(8001);
    pool.execute(() -> sendMsg(ds1, "192.168.8.7", 7001, "world", "服务端-发送："));
    pool.execute(() -> recvMsg(ds1, "服务端-接收："));
}
```

```java
// 发送消息
private static void sendMsg(DatagramSocket ds, String ip, int port, String msg, String mark) {
    byte[] buf = msg.getBytes();
    try {
        //发送数据包，数据包内容：数据的字节数组，目标ip，目标端口号
        ds.send(new DatagramPacket(buf, buf.length, InetAddress.getByName(ip), port));
        
        System.out.println(mark + msg);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

```java
// 接收消息
private static void recvMsg(DatagramSocket ds, String mark) {
    while (true) {
        try {
            byte[] buf = new byte[4 * 1024];
            DatagramPacket dp = new DatagramPacket(buf, buf.length);
            ds.receive(dp); //接收数据包

            String host = dp.getAddress().getHostName(); //解析数据包
            int port = dp.getPort();
            String msg = new String(dp.getData(), 0, dp.getLength());

            System.out.println(mark + host + ":" + port + " - " + msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## TCP

> TCP三次握手



```sql
A --> B: B在吗？           发送SYN-A同步包
B --> A: A在，B收到了吗？   解析，知道是A请求建立链接，发送ACK-A确认包 + SYN-B同步包
A --> B: B收到了,开始传输！  解析，知道B同意建立连接，并发送ACK-B同意建立连接
```



> 服务端

```java
public static void main(String[] args) {
    ExecutorService pool = Executors.newCachedThreadPool();
    try (ServerSocket server = new ServerSocket(8100)) {

        while (true) {
            Socket socket = server.accept(); //阻塞方法，接受客户端请求
            pool.execute(() -> recvMsg(socket)); //一个客户端一个线程去处理
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

private static void recvMsg(Socket socket) {
    try (InputStream in = socket.getInputStream();
         OutputStream out = socket.getOutputStream()) {

        List<String> lines = IOUtils.readLines(in, "UTF-8"); //读取流中的每一行
        String join = StringUtils.join(lines, ""); //多行合并为一行
        System.out.println("S<-C：" + join);

        String recv = LocalDateTime.now().toString();
        IOUtils.write(recv, out, "UTF-8");
        System.out.println("S->C：" + recv);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

> 客户端

```java
public static void main(String[] args) {
    int nThreads = 2;
    ExecutorService pool = Executors.newFixedThreadPool(nThreads);

    for (int i = 0; i < nThreads; i++) {
        int index = i;
        pool.execute(() -> sendMsg(index));
    }
}

private static void sendMsg(int index) {

    try (Socket socket = new Socket("127.0.0.1", 8100);
         InputStream in = socket.getInputStream();
         OutputStream out = socket.getOutputStream()) {

        String send = index + "";
        IOUtils.write(send, out, "UTF-8");
        System.out.println("C->S：" + send);

        List<String> lines = IOUtils.readLines(in, "UTF-8");
        String join = StringUtils.join(lines, "");
        System.out.println("C<-S：" + join);
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```





# Reflect

##创建对象

> （1）new创建：传统方式，必须预先知道要使用的类；引用类改变，就必须修改源码。

```java
Person person = new Person();
```

> （2）Cloneable方式：不推荐

```java
public class Person implements Cloneable{
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
Person clone = (Person) person.clone(); //克隆
```

> （3）反射方式：动态创建，效率相对低下，耗时是传统方式的3倍

```java
//先获取 clazz 对象（4种方式，常用1和2），再创建此 clazz 对象所表示的类的一个新实例
Class<?> clazz = Class.forName("com.example.reflect.Person"); //1
Class<?> clazz = getClass().getClassLoader().loadClass("com.example.reflect.Person"); //2
Class<? extends clazz> clazz = new Person().getClass(); //3
Class<Person> clazz = Person.class; //4

Object instance = clazz.newInstance(); //创建实例
```

## 构造器

> 所有构造器

```java
Constructor<?>[] constructors = clazz.getConstructors(); //public-级别的所有构造器
Constructor<?>[] constructors = clazz.getDeclaredConstructors(); //all
```

> 无参构造器

```java
Constructor<?> constructor = clazz.getConstructor();
Person p = (Person) constructor.newInstance(); //等同于 new Person();
```

> 有参构造器

```java
Constructor<?> constructor = clazz.getConstructor(boolean.class);
Person p8 = (Person) constructor.newInstance(true); //new Person(true);
```

## 属性

> 所有属性

```java
public class Person {
    public static String city = "SX";
    public boolean gender;
    protected String name;
    int age;
    private boolean young;
}

Field[] fields = clazz.getFields(); //public-only
Field[] fields = clazz.getDeclaredFields(); //all
```

> 具体属性

```java
Field gender = clazz.getField("gender"); //public boolean gender;
Field name = clazz.getDeclaredField("name"); //protected String name;
```

> 属性操作

```java
Field city = clazz.getDeclaredField("city"); //static -> 不依赖对象,传参 null
city.set(null, "BJ");
System.out.println(city.get(null));

Field gender = clazz.getDeclaredField("gender"); //non static --> 依附于对象 p1
Object p1 = clazz.newInstance(); 
gender.set(p1, true);
System.out.println(gender.get(p1));

Field young = clazz.getDeclaredField("young"); //private --> 依附于对象,并暴力访问
Object p2 = clazz.newInstance();
young.setAccessible(true); //暴力膜
young.set(p2, true);
System.out.println(young.get(p2));
```
## 方法

> 所有方法

```java
Method[] methods = clazz.getMethods(); //同上
Method[] methods = clazz.getDeclaredMethods();
```
> 无参 public static

```java
Method staticHello = clazz.getMethod("staticHello");
Object invoke = staticHello.invoke(null); //invoke为返回值; 调用 --> 不依赖对象
```
> 无参 private

```java
Method privateHello = clazz.getDeclaredMethod("privateHello");
privateHello.setAccessible(true);
Object p0 = clazz.newInstance();
System.out.println("privateHello: " + privateHello.invoke(p0)); //依赖对象 + 暴力膜
```
> 有参 private

```java
Method privateHello1 = clazz.getDeclaredMethod("privateHello", String.class, int.class);
privateHello1.setAccessible(true);
Object p3 = clazz.newInstance();
System.out.println("privateHello1: " + privateHello1.invoke(p3, "SSS", 888)); //依赖+暴力膜
```

## 两个特殊

> main方法怎样传递参数？ `public static void main(String[] args){}`

按照jdk1.5，整个数组是一个参数； jdk1.4数组中的每一个元素是一个参数。把一个字符串数组作为参数传递到 invoke()，jvm怎么解析？？

jdk1.5肯定没问题，但对于jdk1.4则会将字符串数组打散成一个个字符串作为参数，出现参数个数异常。

**正确做法：** 

1. 将字符串数组转换成Object对象；
2. 将字符串数组作为Object数组的一个元素

```java
Class<?> clazz = this.getClass().getClassLoader()
        .loadClass("com.example.reflect.Person");
Method helloArray = clazz.getMethod("main", String[].class); //参数类型: String[].class

helloArray.invoke(null, (Object) new String[]{"aaa", "bbb"}); //正确1
// helloArray.invoke(null, new Object[]{new String[]{"aaa", "bbb"}}); //正确2

// helloArray.invoke(null, new String[]{"aaa", "bbb"}); //错误写法
```

> 泛型相关

```java
public Map<Integer, Person> method(Map<String, Person> map, List<Person> list) {
    return null;
}

public void doArgs() throws NoSuchMethodException {
    Method method = getClass().getMethod("method", Map.class, List.class);
    Type[] types = method.getGenericParameterTypes();

    for (Type paramType : types) {
        System.out.println("参数-类型: " + paramType);

        if (paramType instanceof ParameterizedType) {
            Type[] genericTypes = ((ParameterizedType) paramType).getActualTypeArguments();
            for (Type genericType : genericTypes) {
                System.out.println("参数-泛型类型：" + genericType);
            }
            System.out.println();
        }
    }

    Type returnType = method.getGenericReturnType();
    System.out.println("返回值-类型：" + returnType);

    if (returnType instanceof ParameterizedType) {
        Type[] genericTypes = ((ParameterizedType) returnType).getActualTypeArguments();

        for (Type genericType : genericTypes) {
            System.out.println("返回值-泛型类型：" + genericType);
        }
    }
}
```















# 算法相关

## 基础算法

> 某校为 x 个学生分配宿舍，每6个人一间房（不考虑性别差异），问需要多少房？

```java
int ceil = (int) Math.ceil(x / 6.0); //或者：(x+5)/6
```

> 让数值在 0～9 之间循环

```java
for (int i = 0; i < 1000; i++) {
    System.out.println(i % 10);
}
```