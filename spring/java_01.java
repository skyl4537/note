





    
#java.util.Collections.singleton()
    //返回一个不可变集只包含指定对象
    
        String init[] = { "One", "Two", "Three", "One", "Two", "Three" };

        List<String> list1 = new ArrayList<String>(Arrays.asList(init));
        List<String> list2 = new ArrayList<String>(Arrays.asList(init));

        list1.remove("One");
        // List1 value: [Two, Three, One, Two, Three]
        System.out.println("List1 value: " + list1);

        list2.removeAll(Collections.singleton("One"));
        // The SingletonList is :[Two, Three, Two, Three]
        System.out.println("The SingletonList is :" + list2);

jvm内存可分为三个区域：栈stack，堆heap，方法区method-area（其中，方法区也存在于堆中）。


    
    
#-----------------------------------------------------------------------------------------------------------------


        
0.关键字 static '不能修饰类, 修饰类的成员变量,成员函数'
    #1. 随着类的加载而加载~
    当类被内存调用时, static 修饰的成员也同时在内存中 (方法区) 划分存储空间
    也就是说, 静态成员会随着类的消失而消失,它的生命周期最长. //所以不建议定义过多的静态变量

    #2. 优先于类的对象存在,被类的所有对象所共享~. 静态方法中不可以定义 this super 关键字
    
    #3. 除被类的对象调用外, 还可以直接被类所调用~
    非static修饰的变量也称为成员变量,实例变量. static 修饰的变量称为静态成员变量,类变量
    
    '实例变量 & 类变量'
    1). 存放位置: 实例变量随着对象的建立而存在于堆内存中. 类变量随着类的加载而存在于方法区中~
    2). 生命周期: 实例变量随着对象的消失而消失. 类变量生命周期最长,随着类的消失而消失~

    '实例变量 & 局部变量'
    1).    实例变量定义在类中,在整个类中都可以被访问~
        实例变量随着对象的建立而建立,存在于对象所在的堆内存中~
        生命周期分: 实例变量和静态成员变量~
        实例变量有默认初始化值~
        
    2). 局部变量只定义在局部范围内. 如:函数内,语句内等~
        局部变量存在于栈内存中~
        作用的范围结束,变量空间会自动释放~
        局部变量没有默认初始化值~


    
0.对象初始化过程
    对于 Person p = new Person("zhangsan", 20); 在内存中究竟做了什么事？
    1) 因为 new 用到了 Person.class ,所以会先找到 Person.class 文件并加载到内存中。
    2) 执行该类中的 static 静态代码块, 如果有的话, 给 Person.class 类进行初始化。
    3) 在堆内存中开辟空间, 分配内存地址。
    4) 在堆内存中建立对象的特有属性. 并进行默认初始化(String 类型默认为 null, int 默认为 0)
    5) 对属性进行显示初始化 (即定义类时,属性含有默认的值 private String name = "xiaowang")
    6) 对对象进行构造代码块初始化。
    7) 对对象进行对应的构造函数初始化。
    8) 在栈内存中生成变量 p1, 将内存地址赋给占内存中的 p1 变量
    
1.内存结构
    Java程序在运行时,为提高运算效率,对内存空间进行了不同区域的划分, 每一片区域都有特定的处理数据方式和内存管理方式.
    
    '栈内存(值类型)' ---> '站直'
        用于存储局部变量. 数据用完,所占空间就会自动释放.
    
    '堆内存(引用类型)'
        1).数组 对象 通过new建立的实例 都存放在堆内存中
        2).每一个实例都有内存地址值
        3).实例中的变量都有默认初始化值
        4).实例不再被使用, 会在不确定的时间内被系统的垃圾回收器回收
        
        // 执行这条语句后, 首先在堆内存中划分一个长度为4的int数组
        // 其次在栈内存中划分一个名为arr的变量, 最后再将int数组第一个元素的内存地址赋给arr变量
        int[] arr = new int[4]; 
        
    方法区, 本地方法区, 寄存器


3.get与set
    当getAge() 和 setAge()方法内部只放一个赋值语句, 其实和 public int age 并没什么区别.
    '控制非法参数赋值': 如 age 代表年龄,则在 setAge() 中可以控制只能赋值1-100以内的年龄
        
4.集合相关
    Array: 数组,能根据下标直接找到相应地址,检索速度快,增删节点慢,复杂度O(1)
            唯一缺点是不能动态改变数组的长度.

    List: 链表,搜索速度慢,增删节点快,复杂度O(n).

    ArrayList: 默认初始化数组大小10. 每次新增元素,先检查当前数组长度是否不足,不足则增加'0.5倍的当前长度'
            一个通过申请新内存来扩容的数组！

5.String连接符"+"

    // "+"在java中两种含义: 运算符, 连接符.
    // (1).两边都是数值类型时, 为运算符, 即相加求和.
    // (2).两边至少有一个为非值类型时 (应该特指字符串类型????), 则为连接符.
    public static void main(String[] args) {
        Date a = new Date(), b = new Date();

        // System.out.println(a + b);// 报错, 两个普通变量之间是不能使用"+"进行 运算 或 连接的
        // System.out.println(a + b + ""); // 报错, 顺序与下不同

        System.out.println("" + a + b); // 正确, 本质是先后调用了a和b的toString()方法

        /* ("" + a + b)过程拆解 */
        String s1 = "", s2 = a.toString(); // 调用a.toString()方法
        StringBuilder sb = new StringBuilder();
        sb.append(s1).append(s2);
        String res = sb.toString(); // 完成 ""+a 这一过程!!!

        String s3 = res, s4 = b.toString(); // 调用b.toString()方法
        StringBuilder sb2 = new StringBuilder();
        sb2.append(s3).append(s4);
        String res2 = sb2.toString(); // 完成 "a"+b 这一过程,结束!!!

        // 总结:
        // (1)"+"为String类型特有的方法 .(自我总结,待证?????)
        // 任何类型变量都可以通过"+"和字符串进行拼接,其原理是先调用toString()方法转为String类型,
        // 然后通过String类型的 + 方法把两个字符串进行拼接!!!
        // (2)String拼接的底层实现是通过 StringBuilder 的append()方法.(确认)
    }

6.Stringのfinal
    'String类被final修饰'. 不可变对象 ---> 一个对象在创建完成后,不能再改变它的状态,那么这个对象就是不可变的

    String cx ="abc";
    System.out.println(cx);
    cx = "nice";
    System.out.println(cx); //abc nice ---> 不可变对象已经变了????
    //首先,创建一个String对象"abc",把其放入字符串常量池中. 然后让cx指向abc值. 然后,又创建一个String对象"nice",再让cx指向nice值. 
    //但这是一个大的误区: cx是一个String对象(引用数据类型)的引用,不是对象本身. 之所以变了,变的是cx的指向, 而不是String对象本身！
    
    String cx ="cx";
    System.out.println(cx);
    cx = cx.replace("c", "d");
    System.out.println(cx); //cx dx ---> cx的值发生了改变,但它的String对象却并没有改变!
    //调用replace()时,方法内部返回了一个新的String对象. 所以cx最开始指向的String对象"cx"并没有改变!!!
    
    String cx ="cx";
    System.out.println(cx);
    cx.replace("c", "C");
    System.out.println(cx); //cx cx ---> 调用replace() subString() toLowerCase()等方法时,会返回一个新的String对象
    
    String a = "HELLO";
    String b = "HELLO";
    String c = new String("HELLO");
    String d = new String("HELLO");
    System.out.println(a == b); //*
    System.out.println(b == c); //-
    System.out.println(c == d); //-
    System.out.println(a.equals(b));//*
    System.out.println(b.equals(c));//*
    System.out.println(c.equals(d)); //*true, false, false, true, true, true 
    //(1).字符串池(String pools)是为了提高java内存利用率而采用的措施!!!
    //    当遇到String a = "HELLO"时, 首先检查字符串池中是否存在"HELLO"对象?? 没有则先新建,再添加到池中,最后让变量 a 指向这个对象的内存地址;
    //    当遇到String b = "HELLO"时, 由于已存在"HELLO"对象 (底层通过equals()方法确定), 所以直接将 b 指向此对象内存地址, 省去再分配的麻烦!!
    
    //(2).java中, "=="对于两个基本类型,判断内容是否相等; 对于对象判断两个对象的地址是否相等; 所以此时的a, b的地址相等, 返回true
    
    //(3).对于String c = new String("Hello"),则不会检查字符串池, 而是直接为变量 c 开辟空间,然后将值写入空间.
    //    所以b == c返回false; c == d返回false. 
    
    //(4).至于String的equals()方法, 因为它比较的不是对象的地址, 而是对象的值; 所以都返回true...
    
    /**
     * java虚拟机有一个字符串池,保存着几乎所有的字符串对象. 字符串变量总指向字符串池中的一个对象.
     * 注: 如果池中已有相同的字符串--–使用equals()确定, 则直接将变量指向池中的字符串; 否则先将字符串添加到池中,再返回
     * 使用new操作创建的字符串对象不指向字符串池中的对象; 但是可以使用 intern 方法使其指向字符串池中的对象
     *
     * 因为字符串不可变,所以线程安全. 同一个字符串实例可以被多个线程共享. 这样便不用因为线程安全问题而使用同步. 字符串自己便是线程安全的!!
     *
     * (注：String interning 是指对不同的字符串仅仅只保存一个, 即不会保存多个相同的字符串)
     */
     
     /**
        1、只有当字符串是不可变的,字符串池才有可能实现字符串池的实现可以在运行时节约很多heap空间,因为不同的字符串变量都指向池中的同一个字符串但如果字符串是可变的,
        那么String interning将不能实现(注：String interning是指对不同的字符串仅仅只保存一个,即不会保存多个相同的字符串),因为这样的话,如果变量改变了它的值,那么其它指向这个值的变量的值也会一起改变

        2、如果字符串是可变的,那么会引起很严重的安全问题譬如,数据库的用户名、密码都是以字符串的形式传入来获得数据库的连接,或者在socket编程中,主机名和端口都是以字符串的形式传入因为字符串是不可变的,所以它的值是不可改变的,否则黑客们可以钻到空子,改变字符串指向的对象的值,造成安全漏洞

        3、因为字符串是不可变的,所以是多线程安全的,同一个字符串实例可以被多个线程共享这样便不用因为线程安全问题而使用同步字符串自己便是线程安全的

        4、类加载器要用到字符串,不可变性提供了安全性,以便正确的类被加载譬如你想加载java.sql.Connection类,而这个值被改成了myhacked.Connection,那么会对你的数据库造成不可知的破坏

        5、因为字符串是不可变的,所以在它创建的时候hashcode就被缓存了,不需要重新计算这就使得字符串很适合作为Map中的键,字符串的处理速度要快过其它的键对象这就是HashMap中的键往往都使用字符串
     */
     
    
8.java怎么在函数内改变传入的值？
    1、对于基本类型参数，在方法体内对参数进行重新赋值，并不会改变原有变量的值。
    2、....引用........，........................赋引用，..................所持有的引用。 
    3、方法体内对参数进行运算，不影响原有变量的值。 
    4、方法体内对参数所指向对象的属性进行操作，将改变原有变量所指向对象的属性值。 
    
    对于基本数据类型，实现的是传值，只是个形参(会在函数内部copy一分放在栈中)，不会改变原有值。
    对于引用数据类型，对这个引用进行操作，其实也是相当于对形参的操作，不会改变原来的引用。
    但是，当对这个引用的属性进行操作的时候，相当于CPP中的传址调用，可以改变这个引用的属性的值。

    
    
    




/*9999 接口*/
//接口可以继承接口。抽象类可以实现(implements)接口，抽象类可以继承具体类。抽象类中可以有静态的 main 方法。
//抽象类与普通类的区别：允许有 abstract 方法和不能被实例化(但是可以有构造函数)

//抽象类是否可继承实体类 (concrete class)???
//可以继承，但是和实体类的继承一样，也要求父类可继承，并且拥有子类可访问到的构造器。

/*abstract class 和 interface 有什么区别?
1.抽象类可以有构造方法，【接口中不能有构造方法】。
2.抽象类中可以包含非抽象的普通方法，【接口中的所有方法必须都是抽象的】
3.抽象类中的抽象方法访问类型可以是 public，protected 和 默认类型，【接口中抽象方法只能是public abstract类型】。
4.抽象类中可以包含静态方法，【接口中不能包含静态方法】
5.抽象类中可以有普通成员变量，【接口中没有普通成员变量】
6.抽象类和接口中都可以包含静态成员变量，抽象类中的静态成员变量的访问类型可以任意【接口中定义的变量只能是 public static final 类型】
7.一个类可以实现多个接口，但只能继承一个抽象类。*/

/* abstract 是否可和 static 同用, 是否 native，同用 ，是否 synchronized 同用?*/
/*1.abstract 的 method 不可以是 static 的，因为抽象的方法是要被子类实现的，而 static 与子类扯不上关系！
2.native 方法表示该方法要用另外一种依赖平台的编程语言实现的， 不存在着被子类实现的问题，所以，它也不能是抽象的，不能与 abstract 混用。
3.关于 synchronized 与 abstract 合用的问题，我觉得也不行， 因为在我几年的学习和开发中，从来没见到过这种情况，
并且我觉得 synchronized 应该是作用在一个具体的方法上才有意义。
而且，方法上的 synchronized 同步所使用的同步锁对象是 this，而抽象方法上无法确定 this 是什么。*/



/*什么是内部类？ Static Neste(嵌套) Class 和 Inner Class  的不同。*/
//Java 中的内部类共分为四种：静态内部类 + 成员内部类 + 局部内部类 + 匿名内部类

/*静态内部类 Static Nested(Inner) Class：
1.如果内部类中有 static 成员，则一定是 static 内部类。
2.当外部类的 static 方法访问内部类时，内部类也必须是 static。
3.静态内部类只可以访问外部类的 static 静态成员，包括 private 私有的。
4.其他类中生成静态内部类对象的方式：OuterClass.InnerClass inner = new OuterClass.InnerClass();
5.静态内部类被编译成一个完全独立的.class 文件，名称为 OuterClass$InnerClass.class 的形式。
*/
/*成员内部类 Member Inner Class
1.不用 static 修饰的内部类，它可以访问外部类中所有成员，不管是 static 还是 private 。
2.在外部类中创建成员内部类的实例：this. new Innerclass();
3.在内部类中访问外部类的成员~：Outerclass.this.member
4.在其他类中创建内部类的实例~：new Outerclass().new Innerclass();
*/
/*局部内部类  Local Inner Class
1.定义在方法中，只能访问方法中定义的 final 常量。是 最少 用到的一种类型。
2.像局部变量一样，【不能】被 public, protected, private 和 static 修饰。
3.只能在方法当中生成局部内部类的实例并且调用其方法。
*/
/*匿名内部类  Anonymous Inner Class
1.匿名内部类就是没有名字的局部内部类，不能使用关键字 class, extends, implements, 没有构造方法。
2.匿名内部类【必须】隐式地继承了一个父类或者实现了一个接口~。
3.生成的.class 文件中，匿名类会生成 OuterClass$1.class 文件，数字根据是第几个匿名类而类推。
4.简单理解：就是创建一个自带实现内容的父类或接口的子类匿名对象~。
5.格式为：new 父类名或接口名(){ 覆盖父类或者接口中的代码}.调用的方法*/
    new AbsDemo(){
        void show(){
            System.out.println("show:"+num);
        }
    }.show();

/*内部类可以引用外部类的成员吗？有没有什么限制？*/
//完全可以。如果不是静态内部类，那没有什么限制！
//静态内部类不可以访问外部类的普通成员变量，而只能访问外部类中的static静态成员，

/*Anonymous Inner Class(匿名内部类)是否可以 extends 其它类，是否可以 implements 其他interface*/
//可以继承其他类或实现其他接口。不仅是可以，而是必须!!!


/*super.getClass() 方法调用*/
import java.util.Date;
public class Test extends Date{
    public static void main(String[] args){
        new Test().test();
    }
    public void test(){
        //getClass()在 Object 类中定义成了 final，子类不能覆盖该方法
        //此方法的返回值是：此 Object 的运行时类。即Test
        System.out.println(getClass().getName());
        
        //super().**其实最终调用的也是Object的getClass()方法
        System.out.println(super.getClass().getName());
        
        //getSuperclass()方法：返回此对象所表示类的超类。 
        System.out.println(this.getClass().getSuperclass().getName());
        
        System.out.println(super.getClass().getSuperclass().getName());
    }
} 














