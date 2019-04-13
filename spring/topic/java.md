[TOC]



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



## 运算符

```java
%(取模)	-> 左<边，值为左。//-1 % 5 == -1;

^(异或)	-> 左右同为 false，左右不同为 true。 (和其他差别很大)

'&'和'&&' -> //都是逻辑与运算符，但后者为短路运算
&， 左边无论真假，右边都要运算
&&，如果左边为真，右边参与运算； 如果左边为假，那么右边不参与运算。
	if (x == 33 & ++y > 0)  //y 会增长
	if (x == 33 && ++y > 0) //y 不会增长

<<(位运算-左) -> 乘以 2 的移动位数次幂 //3<<2 = 3 * 2^2 =12
>>(位运算-右) -> 除以 2 ...........  //6>>2 = 6 / 2^2 =1
```
## 继承 & 实现

> java 只支持`单继承`，不支持`多继承`。 但支持`多层继承`和`多实现`。

当多个父类中定义了相同函数名的函数，而各个函数的实现各不相同，则子类就不确定要调用哪一个。

> **接口：** 可看作一个特殊的抽象类，方法全是抽象的。`一个类可以实现多个接口`。

 类与类之间是`继承关系`，类与接口之间是`实现关系`，接口与接口之间是`继承关系`。

##常用方法

1. **toString()**

   ```java
   //默认返回：对象的类型名+@+内存地址值。---> 子类需要重写
   public String toString() {
       return getClass().getName() + "@" + Integer.toHexString(hashCode());
   }
   ```

2. **arraycopy()**

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



# String#

## 字符串常量池

> 存放String对象，共享使用，提高效率。由于`final String`，一经创建就不可改变，因此不用担心String对象共享而带来程序的混乱。
>
> 因为字符串是不可变的，所以在创建的时候 hashcode 就被缓存了，不需要重新计算。这就使得字符串很适合作为 Map 中的 Key，字符串的处理速度要快过其它的键对象，这就使得 HashMap 中的键往往使用字符串。

- **栈内存：** 在`函数`中定义的一些`基本类型的变量`和`对象的引用变量`都在函数的栈内存中分配。



-  **堆内存：** 用来存放所有`new创建的对象`和`数组的数据`。



-  **常量池：** 存放`字符串常量`和`基本类型常量`（public static final）。它们永不改变。


## DEMO

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

- `String s1 = "abc";` 先在常量池中查找是否存在"abc"（使用 equals() 确定）， 存在则让 s1 指向这个值，没有则新建。
- `String s2 = "abc";` 同上
- `String s3 = new String("abc");` 其中，String s3 只是定义了一个名为 s3 的String类型变量，并没有创建对象。new String() 才是真正的在堆空间上创建一个字符串对象，然后将 s3 指向新建对象的堆内存地址，所以 s1 == s3 比较结果为false。
- `String s4 = "ab" + "c";` 先在常量池中创建 2 个字符串对象，再将 s4 指向已有的 "abc"。
- `String s6 = s5 + "c";` 和 `String s4 = "ab" + "c";` 的区别： 对于字符串常量相加的表达式，不是等到运行期才去进行加法运算处理，而是在编译期直接将其编译成一个这些常量相连的结果。因此，`String s4 = "ab" + "c";`可以转化为`String s4 = "abc";`，但前者由于不是字符串常量直接相加，所以不能转化。


##StringBuilder##

> 内部拥有一个数组用来存放字符串内容。当进行字符串拼接时，直接在数组中加入新内容，并自动维护数组的扩容，不会产生中间字符串。

|     类型      |    安全    |                            特点                            |
| :-----------: | :--------: | :--------------------------------------------------------: |
|    String     | `线程安全` | 直接进行字符串拼接，会产生大量中间字符串，并且时间消耗长。 |
| StringBuffer  |  线程安全  |                   支持同步锁，性能稍差。                   |
| StringBuilder | 线程不安全 |         单线程进行大量字符串操作时，推荐使用(√)。          |

```java
public void test() {
    String result = "";
    String str = "hello";
    
    for (int i = 0; i < 1000; i++) {
        result += str;
        
        //上一行代码，编译器最终会编译为类似下面的代码：
        //result = new StringBuilder(result).append(str).toString();
    }
}
```

**解析：** 每次循环都需要创建一个` StringBuilder对象`（创建对象需要耗费时间和内存），随着循环次数的增大， result 字符串就会越来越长，把 result 中的字符复制到新建的 StringBuilder 中花费的时间也就越长，而且 `StringBuilder(result).append(str).toString();` 会创建一个临时的字符串，随着循环次数的增加，这个操作花费的时间也会越来越长。总之，随着循环变量 i 的增大，每次循环会变得越来越慢。
## 字符串连接符

> `+` 在java中两种含义：运算符，字符串连接符。

- 两边都是数值类型时，为运算符，即相加求和。
- 两边至少有一个为字符串类型时， 则为字符串连接符。
- 字符串拼接的底层原理是通过 `StringBuilder.append()`方法实现的。


## 常用方法

1. **equals()**

   >**==：** 对于基本数据类型，比较其值； 对于引用数据类型，比较其堆内存地址。
   >
   >**equals：** `Object`中默认调用`==`。`String重写为：比较字符串内容。`

2. **length()**

   ```java
   public void test() {
       String[] array = {"a", "b", "c"};
       System.out.println("数组的长度: " + array.length); //数组的属性-length

       System.out.println("字符串长度: " + "abc".length()); //字符串的方法-length()
   }
   ```

3. **split()** 切割结果，需检查最后一个分隔符后有无内容，否则可能抛` IndexOutOfBoundsException`

   ```java
   public void test() {
       String str = "a,b,c,,";
       String[] ary = str.split(",");
       System.out.println(ary.length); //预期大于 3，结果是 3
   }
   ```

4. **reverse**

   ```java
   //先转换为 StringBuilder，再调用 StringBuilder.reverse()
   String str = new StringBuilder("asdfghjjkk").reverse().toString();
   ```


# Integer

##基本数据类型

```java
//三大类，八小种
布尔类型： boolean （1bit，1位）
字符类型： char （1byte，8位，[-128, 127]）
数值类型-整型： byte （1byte）， short （2byte）， int （4byte）, long （8byte） 
数值类型-浮点型： float （4byte）， double （8byte）

//引用类型：类，接口，数组
```

## 装箱 & 拆箱

```
public void test() {
    Integer i0 = new Integer(3);
    Integer i1 = Integer.valueOf(3); //装箱: 基本类型 -> 包装类

    int num0 = i0.intValue(); //拆箱: 包装类 -> 基本类型

    //自动装箱： 相当于Integer i = Integer.valueOf(4);
    Integer i = 4;

    //自动拆箱： 等号右边，将i对象转成基本数值 i.intValue() + 5;
    //加法运算完成后,再次装箱,把基本数值转成对象
    i = i + 5;
}
```

## 使用标准

1. 所有 POJO 类属性必须使用`包装数据类型`，并且`不要设定默认值`。不设初值是提醒使用者在需要使用时必须自己显式地进行赋值。任何 `NPE（NullPointerExceptionrn） `问题，或者入库检查，都由使用者来保证。
2. RPC 方法的返回值和参数必须使用`包装数据类型`。
3. 所有的局部变量 【推荐】 使用`基本数据类型`。

```java
//【反例】在更新其它字段时，会附带更新此字段，导致创建时间被修改成当前时间
Date editTime = new Date();
```
## 常用方法

1. **equals()**

   >**【强制】** `所有的相同类型的包装类对象之间值的比较，全部使用 equals()。` `i0.equals(i1)`

   >**【说明】** 对于 Integer 对象，取值区间在 [-128, 127]，会复用已有对象，可直接使用 == 进行比较。但区间之外的取值，都会在堆上产生，并不会复用已有对象，推荐使用 equals() 进行判断。

   ```java
   public void test() {
       int j0 = 128, j1 = 128;
       System.out.println(j0 == j1); //true

       Integer i0 = 128, i1 = 128;
       System.out.println(i0 == i1); //false
       System.out.println(i0.equals(i1)); //true
   }
   ```

# Collection

## 数组 & 集合

>**数组：** 固定长度（不能动态改变数组的长度），只能放一种类型。
>
>**集合：** 可变长度， 可以存多种类型（在不考虑泛型的前提下）。

```java
List list = new ArrayList();
list.add(5); //集合放原始类型，其实是通过装箱拆箱来实现的。以前原生类型只能用数组，现在集合也可以了。
list.add("5");
list.add(new Integer(5));
```

## 双括号初始化

>使用双括号初始化（double-brace syntax）快速建立并初始化，`简洁但效率低`。
>
>1. 双大括号初始化方法生成的.class文件要比常规方法多
>2. 双大括号初始化方法运行时间要比常规方法长
>3. 可能造成内存泄漏

```java
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

- 第一层花括号，定义了一个继承自 ArrayList 的`匿名内部类`
- 第二层花括号，在匿名内部类中定义了一个 `构造代码块`
- 通过 new 得到ArrayList的子类的实例化，然后上转型为ArrayList的引用
- 得到的 list 实际上是ArrayList的子类的引用，但在功能上没有任何改变
- 相比于常规标准方式进行初始化要简洁许多（但代码可读性相对会差）

## List & Set

>**List：** 排列有序（存入和取出的顺序一定相同），元素可重复。
>
>**Set：** 排列无序，元素不可重复。

List的`contains()`和`remove()`底层调用的都是`equals()`，但是Set却是`hashCode()`和`equals()`。

>Set如何保证元素唯一性？？？

先比较 hashCode()，如果相同，继续比较 equals() 是否为同一个对象。

> Set中，hashCode()相同，equals()不同，怎么存储呢？？？

 在同样的哈希值下顺延（可认为哈希值相同的元素放在一个哈希桶中），也就是哈希一样的存一列。

>TreeSet排序是如何进行的呢？？

1. 元素实现接口Comparable

   ```java
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

2. 集合添加比较器

   ```java
   //元素自身不具备比较性，或具备的比较性不满足要求时，需要让 TreeSet 集合自身具备比较性
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


## 常用方法

1. null

   > 高度注意 Map 类集合 K/V 能不能存储 null 值的情况

   |       集合类        |      Key      |     Value     |    Super    |     说明     |
   | :-----------------: | :-----------: | :-----------: | :---------: | :----------: |
   |      Hashtable      | 不允许为 null | 不允许为 null | Dictionary  |   线程安全   |
   | `ConcurrentHashMap` | 不允许为 null | 不允许为 null | AbstractMap | `分段锁技术` |
   |       TreeMap       | 不允许为 null | `允许为 null` | AbstractMap |  线程不安全  |
   |      `HashMap`      | `允许为 null` | `允许为 null` | AbstractMap |  线程不安全  |

2. 去重

   >对于存储大量不重复元素，应该选用 Set 集合，利用其元素唯一性特点。
   >
   >而不应该选用 List，去使用 List.contains() 进行遍历，对比，去重操作。

3. 初始化指定大小

   ```java
   //集合初始化时，尽量指定集合初始值大小。大小应和实际存储元素个数相近，减少扩容次数。
   List<String> list = new ArrayList<>(5);
   ```

4. asList()

   ```java
   //asList() 返回对象是 Arrays 内部类，并没有实现集合的修改方法(add，remove，clear)。
   public void test() {
       List<String> asList = Arrays.asList("a", "b", "c");
       // asList.add("d"); -> UnsupportedOperationException

       ArrayList<String> list = new ArrayList<>(asList); //正解,先转换
       list.add("d");
   }
   ```

5. subList()

   ```java
   //subList() 返回的是 ArrayList 的内部类 SubList，并不是 ArrayList。
   //SubList 是 ArrayList 的一个视图，对于 SubList 子列表的所有操作最终会反映到原列表上。
   public void test() {
       ArrayList<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));
       List<String> subList = list.subList(0, 2);

       ArrayList<String> list1 = (ArrayList<String>) subList; //ClassCastException
   }
   ```

6. toArray()

   ```java
   //【不推荐】直接使用 toArray() 无参方法，因为其返回值只能是 Object[]。
   public void test() {
       ArrayList<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));
       String[] array = list.toArray(new String[list.size()]); //推荐使用
       
       Object[] array1 = list.toArray(); //不推荐
   }
   ```

7. foreach()-add

   ```java
   //不要在 foreach 循环里进行元素的 remove/add 操作
   //remove 元素应该使用 Iterator 方式，如果并发操作，需要对 Iterator 对象加锁
   public void test() {
       ArrayList<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));

       // for (String s : list) {
       //     if ("a".equalsIgnoreCase(s)) {
       //         list.remove(s); //异常 -> ConcurrentModificationException
       //     }
       // }

       Iterator<String> iterator = list.iterator();
       while (iterator.hasNext()) {
           String next = iterator.next();
           if ("a".equalsIgnoreCase(next)) {
               
               // list.remove(next); //异常 -> ConcurrentModificationException
               iterator.remove();
           }
       }
       System.out.println(JSON.toJSON(list));
   }
   ```

8. foreach()-keySet

   ```java
   //使用 entrySet 遍历Map集合，而不是 keySet 方式进行遍历
   public void test() {
       Map<String, String> map = new HashMap<String, String>() {{
           put("k1", "v1");
           put("k2", "v2");
       }};

       //(1).keySet() 其实遍历了 2 次
       //一次是转为 Iterator 对象，另一次是从Map中取出 key 所对应的 value
       for (String key : map.keySet()) {
           System.out.println(key + ":" + map.get(key));
       }

       //(2).entrySet() 只遍历一次。【推荐使用】
       //遍历1次就把 kV 都放到了 entry 中，效率更高。JDK8的 Map.forEach() 就是这个原理
       for (Map.Entry<String, String> entry : map.entrySet()) {
           System.out.println(entry.getKey() + ":" + entry.getValue());
       }

       //(3).JDK8 -> Map.forEach()
       map.forEach((k, v) -> System.out.println(k + ":" + v));
   }
   ```

9. sort()

   ```java
   //JDK7 以上，Comparator 要满足自反性，传递性，对称性。
   //不然 Arrays.sort()，Collections.sort()会报异常 IllegalArgumentException
   //所以，对于基本数据类型，要采用其包装类的 Compare(x, y) 方法进行比较。
   new Comparator<Person>() {
       @Override
       public int compare(Person o1, Person o2) {
           //return o1.getAge() > o2.getAge() ? 1 : -1; //没有处理相等的情况

           return Integer.compare(o1.getAge(), o2.getAge()); //推荐使用此方法
       }
   };
   ```

10. 


# 基本概念

## static

> static 方法是否可以调用非 static 方法？

不可以。因为非 static 方法是要与对象关联在一起的，必须创建一个对象后，才可以在该对象上进行方法调用。而 static 方法调用时不需要创建对象，可以通过类直接调用。

在 static 方法中调用非 static 方法时，可能还没有实例化对象， 这就与以上逻辑不符。

>静态变量 & 实例变量

- 生命周期	

  **静态变量** static 修饰，随着类的加载而分配内存空间，随着对象的消失而消失。

  **实例变量** 随着对象的建立而分配内存空间，随着对象的消失而消失。

- 存放区域

  静态变量-存在于方法区。

  实例变量-存在于堆内存。

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






## final

>区别 final & finally & finalize

- **final：**`修饰类`，不能被继承。`修饰方法`，不能被重写。`修饰变量`，对于值类型，不能修改其值；对于引用类型，不能修改其对应的堆内存地址，即不能重新再赋值。



- **finally：**异常处理的一部分，代码肯定会被执行，常用于释放资源。



- **finalize：**Object类的一个方法，用于垃圾回收。


>final 修饰变量时，是引用不能变，还是引用的对象不能变？
>
>引用变量不能变，引用变量所指向的对象中的内容还是可以改变的。

- final 修饰基本数据类型的变量，那么这个变量的值就定了，不能变了。
- final 修饰的引用类型的变量，那么该变量存的是一个内存地址，该地址就不能变了。但是，该内存地址所指向的那个对象还是可以变的。就像你记住了人家的门牌号，但你不能管人家家里人员数量。

```java
public void doFinal(final int i, final StringBuilder sb) {
    // i = i + 1; //编译报错,因为final修饰的基本类型 --> 值不能变
    // sb = new StringBuilder(); //同上,修饰引用类型 --> 堆内存地址不能变,即引用不能变

    sb.append("java"); //编译通过 -> 引用变量所指向的对象中的内容，可以改变
}
```

>final String，字符串一经创建就不可改变。以下代码，原始的 String对象 中的内容到底变了没有？

```java
String str = "Hello";
str = str + "world!";

//没有改变。这段代码中，str 原始指向 "Hello"，对 str 进行了 + 操作运算之后，str 不再指向 "hello"，
//而是指向 "Hello world!"，但字符串 "hello" 依然存在于内存之中，只是 str 这个引用变量不再指向它
```





## switch

>switch能否作用在 byte 上，能否作用在 long 上，能否作用在 String 上？

switch 作用在 byte，short，char，int 这几个基本数据类型和封装类型 或 enum 枚举常量。其中，byte，short，char 都可以隐含转换为 int。 但是不支持 boolean。

另外，java7支持 String类型，其实是通过调用 `String.hashCode()`，将String转换为 int。

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

## OverWrite

- **重写：** 在子类中，出现和父类中一摸一样的方法。


- **重载：** 同一类中，出现多个方法名一样，但参数列表（参数类型+个数+顺序）不一样的方法。

> 重载与方法的返回值类型无关。

**方法包括：**  修饰符（可选）， 返回值类型，方法名，参数列表，方法体。
假设定义了两个只有返回类型不一样的方法： `int add(Object o); boolean add(Object o);`
当调用者不关心返回值时，写作：`add(obj);`编译器如何区分到底调用的是哪个方法???

## this

> this & super

- **this：** 子类调用【子类】的同名成员或方法。**super：** 子类调用【父类】的...。
- 构造函数间调用使用 `this() 或 super(name, age)` 语句，并且该语句只能放在构造函数第一行。

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
        }
    }
}
new Outter().new Inner().show();
```

> 抽象类 & 接口

接口`extends`接口， 抽象类`implements`接口， 抽象类`extends`具体类。

抽象类与普通类的区别：不能创建实例对象和允许有 abstract 方法。抽象类中可以有静态的 main() 方法。





> **多态：** 父类的引用指向子类的对象。

```java
static class Father {
    public static int staticNum = 6;
    public int num = 6;

    public static void doStatic() {
        System.out.println("Father-Static");
    }

    public void doSth() {
        System.out.println("Father");
    }
}

static class Child extends Father {
    public static int staticNum = 8;
    public int num = 8;

    //不能重写 static 方法
    public static void doStatic() {
        System.out.println("Child-Static");
    }

    @Override
    public void doSth() {
        System.out.println("Child");
    }

    public void doChild() {
        System.out.println("Child-do");
    }
}

@Test
public void test() {
    Father father = new Child();

    //看左-静态
    System.out.println(father.staticNum); //6
    father.doStatic(); //Father-Static

    //看左-实例变量
    System.out.println(father.num); //6

    //看右-实例方法 --> 重写
    father.doSth(); //Child

    //类型为 Father 的变量不能直接执行 Child 类的方法 --> 编译看左
    ((Child) father).doChild(); //Child-do
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

> char 型变量中能不能存贮一个中文汉字? 

`char 类型变量用来存储 Unicode 编码的字符集`，Unicode 编码字符集中包含了汉字。所以，char 型变量中当然可以存储汉字。不过，如果某个特殊的汉字没有被包含在 Unicode 编码字符集中，那么，这个 char 型变量中就不能存储这个特殊汉字。

**补充：** Unicode 编码占用两个字节,，char 类型的变量也占两个字节。











## Exception##

>Exception 和 Error 都继承自 `Throwable` 类。是异常处理机制的基本组成类型。

- **Error：** 一般是指与虚拟机相关的问题，遇到这种问题，仅靠程序本身无法恢复，建议让程序终止。常见：系     统崩溃，内存溢出等。
- **Exception：** 是程序正常运行中，可以预料的意外情况，应该捕获并进行相应的处理。

>**Exception分类：** 受检查异常（Checked Exception）和 运行时异常（Runtime Exception）。

- **受检查异常：** 是`RuntimeException`以外的异常，类型上都属于`Exception`类及其子类。编译时直接报错，必须使用 try-catch 进行异常捕获，或使用 throws 语句声明抛出。常见：`IOException`，`SQLException`。
- **运行时异常：** 是`RuntimeException`类及其子类异常。编译时能通过，在运行时出现，出现后程序直接终止。常见：`NullPointerException `，`ClassCastException`，`ArithmaticException `（除数为0），`ArrayIndexOutOfBoundsException `（数组下标越界）。

> 自定义异常 一般继承`RuntimeException`，待证 ？？？



> 其中，`NullPointerException extends RuntimeException extends Exception`，结果如何？

```java
try {
	throw new NullPointerException("空指针异常"); //try 代码块中也可以抛出异常
} catch (RuntimeException e0) {
	System.out.println("RuntimeException");
} catch (Exception e) {
	System.out.println("Exception");
}

//结果输出: RuntimeException
```
> try 中的 return 语句和 finally 代码块执行的先后顺序？

- **return** 可当作终止语句来用，经常用来跳出当前方法，并返回一个值给调用方法。
- **finally** 无论 try 里执行了 return， break， continue，甚至抛出异常，finally语句都会执行。finally 语句块常用于释放资源。

```java
private int doTry() {
    int x = 1;
    try {
        return ++x;
    } finally {
        return ++x;
    }
}

//try 中的 return 并不是让函数马上返回，而是把返回结果放进函数栈中，等 finally 语句执行完才真正的返回
System.out.println(doTry()); //3
```





> throws & throw

- **throws：** 通常被应用在声明方法时，用来指定可能抛出的异常。多个异常可以使用逗号隔开。
- **throw：**  通常用在方法体中，并且抛出一个异常对象。程序在执行到 throw 语句时立即停止，它后面的语句都不执行。

```java
//throw 抛出异常后，如果想在方法调用处捕获并处理该异常，则需要用 throws 在方法声明中指明要抛出的异常。
private int string2int(String str, int def) throws NumberFormatException {
    if (!StringUtils.isEmpty(str)) {
        try {
            def = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("非法字符串");
        }
    }
    return def;
}
```





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

​	HashTable线程安全, 在多线程情况下, 同步操作能保证程序执行的正确性.
	但是, HashTable每次同步执行时,都要锁住整个结构.
	

	ConcurrentHashMap 正是为了解决这个问题而诞生的.
	ConcurrentHashMap 锁的方式是稍微细粒度的, 将hash表分为16个桶(默认值), 诸如get,put,remove等常用操作只锁当前需要用到的桶。
	原来只能一个线程进入, 现在却能同时16个写线程进入 (写线程才需要锁定,而读线程几乎不受限制), 并发性的提升是显而易见的!!!
	
	ConcurrentHashMap的'读取操作没有用到锁定',所以读取操作几乎是完全的并发操作.
	而'写操作锁定的粒度又非常细', 比起之前又更加快速(桶越多,表现越明显). '只有在求size等操作时才需要锁定整个表'
##ArrayList & Vector & LinkedList

	同步性   -> Vector 是线程安全的(同步), ArrayList 是线程序不安全的
	数据增长 -> 当需要增长时,Vector 默认增长一倍, ArrayList 却是 0.5

##HashMap & Hashtable





