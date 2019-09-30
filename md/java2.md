[TOC]

# Socket

> 基础概念

```sh
Socket 就是为网络服务提供的一种机制，网络通信其实就是 Socket 间的通信。通信两端都是 Socket，数据在两个 Socket 间通过 IO 传输。
```

> 域名解析过程

```sh
在浏览器地址栏中输入'https://www.baidu.com'后，系统会首先自动从本地 hosts 文件中寻找对应的IP地址。
一旦找到，系统会立即打开对应网页，如果没有找到，则系统会再将网址提交DNS域名解析服务器进行IP地址的解析。

#本地Hosts文件所在位置：C:\Windows\System32\drivers\etc
配置本地Hosts文件，一方面可以加快网站的访问速度（配置网站对应的正确IP），另一方面可以阻止某些流氓软件的网络请求（配置网站的IP为127.0.0.1）
```

> 网络模型

```sh
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

> 相关API

```java
// InetAddress inet = InetAddress.getLocalHost(); //本机
InetAddress inet = InetAddress.getByName("192.168.8.8"); //指定ip

String name = inet.getHostName(); //主机名
String ip = inet.getHostAddress(); //IP字符串
```

##UDP

> UDP & TCP

```sh
UDP：面向无连接。数据包一次传输最大64K。不可靠，容易丢包，但是速度快。      #例子：发短信
TCP：需要先通过3次握手建立链接，所以是可靠协议，但效率稍低。传输数据量无限制。#例子：打电话
```

> DEMO

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

> 三次握手

```sh
A --> B: B在吗?           #发送SYN-A同步包
B --> A: A在，B收到了吗？   #解析，知道是A请求建立链接，发送ACK-A确认包 + SYN-B同步包
A --> B: B收到了,开始传输!  #解析，知道B同意建立连接，并发送ACK-B同意建立连接
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


# Collection

## 基础概念

> 数组 & 集合

```sh
数组：固定长度（不能动态改变）。只能放同一类型的元素。数组可以存储基本类型
集合：可变长度。不考虑泛型的前提下，可以存多种类型。集合中只能存储引用类型
```

```java
List<Integer> list = new ArrayList<>();
list.add(5); //集合放基本类型，其实是通过【装箱拆箱】来实现的。以前基本类型只能用数组，现在集合也可以了
```

```java
'双括号初始化 ---> 简洁但效率低';
（1）.双大括号初始化方法生成的.class文件要比常规方法多;
（2）.双大括号初始化方法运行时间要比常规方法长;
（3）.可能造成内存泄漏;

第一层花括号，定义了一个继承自 HashMap 的匿名内部类;
第二层花括号，在匿名内部类中定义了一个 构造代码块;

//通过 new 得到 HashMap 的子类的实例化，然后上转型为 Map 的引用，得到的 map 实际上是 HashMap 的子类的引用。
//但在功能上没有任何改变，相比于常规标准方式进行初始化要简洁许多，但代码可读性相对会差
Map<Integer, String> map = new HashMap<Integer, String>() {{
    put(1, "a"); put(2, "b"); put(3, "c");
}};
```



```sh
Vector：线程安全，效率慢。支持线程的同步，即某一时刻只有一个线程能够写 Vector

ArrayList：底层是通过'数组'实现. 当 add/remove 时,需要对数组进行复制,移动, 代价比较高

8.HashSet
通过 hashCode 来确定元素在内存中的位置.一个 hashCode 位置上可以存放多个元素(哈希桶).

9.TreeSet
使用二叉树的原理对新 add() 的对象按照指定的顺序排序(升序,降序)
每增加一个对象都会进行排序,将对象插入的二叉树指定的位置

0.HashTable
继承自 Dictionary 类,线程安全. 并发性不如 ConcurrentHashMap,后者引入了分段锁.
不推荐使用. 不需要线程安全 ---> HashMap, 需要线程安全 ---> ConcurrentHashMap

1.TreeMap
可针对key值进行排序,默认按key的升序排列,也可以自定义排序比较器.
key 必须 implements Comparable,或在构造 TreeMap 传入自定义的 Comparator

0.Map和Collection
Map 存储的是键值对. Map的Key要保证唯一性
Map 存储元素使用 put(), Collection 使用 add()
Map 集合没有直接取出元素的方法,而是先转成 Set 集合,再通过迭代获取元素

1.Map常见子类 //k不可重复,v可
HashTable -> 线程安全        k,v都不可为 null    底层哈希表实现    已被 HashMap 替代
HashMap   -> 线程不安全        k,v都可为 null        ..............    

TreeMap   -> 线程不安全        k不为,v可为 null    底层二叉树实现    可排序 key

3.关于hashCode和equals
(1).只要重写 equals, 就必须重写 hashCode
(2).Set 存储的对象,必须重写这两个方法. // Set存储的元素要保证唯一

(3).自定义对象做为Map的key,必须重写这两个方法. //Map的Key要保证唯一
```

> Iterator原理

```sh
在调用 Iterator 的 next()方法之前，迭代器的索引位于第一个元素之前，不指向任何元素。
当第一次调用迭代器的 next()方法后，迭代器的索引会向后移动一位，指向第一个元素并将该元素返回。
当再次调用 next()方法时，迭代器的索引会指向第二个元素并将该元素返回。
依此类推，直到 hasNext()方法返回 false，表示到达了集合的末尾，终止对元素的遍历。

#增强for循环：底层原理也是Iterator迭代器，所以在遍历的过程中，不能对集合中的元素进行增删操作。【faset-fail】
```

> 泛型

```sh
集合是可以存放任意类型的，将元素存入集合后，元素都会被提升为 Object 类型。那么在取出元素时，就必须采用类型转换。
在存储过程中，存入其他类型也不会报错。但是，在取出时进行类型转换时则报异常。

'泛型'：（1）将运行时期的 ClassCastException，转移到了编译时期变成了编译失败。（2）避免了类型强转的麻烦。
```



## 数据结构

> 栈

```sh
'栈'：stack，又称堆栈。它是'一种运算受限的线性表'，其限制是仅允许在表的一端进行插入和删除操作，不允许在其他任何位置进行插入、查找、删除等操作。

（1）先进后出（即，存进去的元素，要在后它后面的元素依次取出后，才能取出该元素）
（2）栈的入口、出口的都是栈的顶端位置

'压栈'：就是存元素。即，把元素存储到栈的顶端位置，栈中已有元素依次向栈底方向移动一个位置。
'弹栈'：就是取元素。即，把栈的顶端位置元素取出，栈中已有元素依次向栈顶方向移动一个位置。
```

> 队

```sh
'队列'：queue，简称队。它同堆栈一样，也是'一种运算受限的线性表'，其限制是仅允许在表的一端进行插入，而在表的另一端进行删除。

（1）先进先出（即，存进去的元素，要在后它前面的元素依次取出后，才能取出该元素）。
（2）队列的入口、出口各占一侧。
```

> 数组

```sh
数组：Array，是有序的元素序列，数组是在内存中开辟'一段连续的空间'，并在此空间存放元素。

（1）查找元素快：通过索引，可以快速访问指定位置的元素
（2）增删元素慢

（2-1）指定索引位置'增加'元素：创建新数组，将新元素添加指定位置，复制原数组中的所有元素
（2-2）指定索引位置'删除'元素：创建新数组，删除指定位置元素，复制其他元素到新数组
```

> 链表

```sh
链表：linked_list，由一系列结点node（链表中每一个元素称为结点）组成，结点可以在运行时i动态生成。
每个结点包括'两个部分'：一个是存储数据元素的数据域，另一个是存储'下一个结点地址的指针域'。常说的链表结构有单向链表与双向链表。

（1）多个结点之间，通过地址进行连接
（2）查找元素慢：想查找某个元素，需要通过连接的节点，依次向后查找指定元素
（3）增删元素快：只需要修改连接下个元素的地址即可
```

> 红黑树

```sh
二叉树：binary_tree，每个结点不超过2的有序树（tree）。顶上的叫根结点，两边被称作'左子树'和'右子树'。

```





## Collection

> 子类介绍

```sh
顶级接口：Collection（单列集合） + Map（键值对集合）。#List Set 是 Collection 的二级接口。

List：存取有序，可重复。可通过索引操作元素
Set ：存取无序，不可重复
```

```sh
"元素唯一性？" 
List 是通过 equals 来比较两个对象是否相等，如 contains() remove()方法的底层都是。
而 Set 是通过 hashCode 和 equals 共同起作用。

"Set的 hashCode 相同，但 equals 不同，怎么存存储？"
在同样的哈希值下顺延（可认为哈希值相同的元素放在一个哈希桶中），也就是哈希一样的存一列。
```

```sh
Vector     -> 线程安全      效率低，被 ArrayList 替代
ArrayList  -> 线程不安全    查询快，增删慢        底层'数组'实现，新增时会涉及到数组拷贝
LinkedList -> 线程不安全    查找慢，增删快        底层'双向循环链表'实现

#Vector和ArrayList 底层采用'数组'实现。当需要增长时，Vector 默认增长一倍，ArrayList 却是 0.5
```

> LinkedList

```java
//底层是一个双向链表，经常涉及到首尾操作，提供了大量首尾操作的方法。
public void addFirst(E e);
public E getFirst();
public E removeFirst();
```

```java
//在开发时，LinkedList集合也可以作为堆栈，队列的结构使用
public E pop();           //从此列表所表示的堆栈处弹出一个元素。
public void push(E e);    //将元素推入此列表所表示的堆栈。
public boolean isEmpty(); //如果列表不包含元素，则返回true。
```



>Set

```sh
HashSet  -> 线程不安全    存取速度快           底层'哈希表'实现，内部是 HashMap
TreeSet  -> 线程不安全    排序存储（可排序）    底层'二叉树'实现，内部是 TreeMap
```

```sh
#TreeSet 排序是如何进行的呢？【二者都有，以后者为主】
（1）元素 implements Comparable

（2）元素自身不具备比较性，或具备的比较性不满足要求时。需要让 TreeSet 集合自身具备比较性：比较器 Comparator
TreeSet<Dog> dogSet = new TreeSet<>((o1, o2) -> Integer.compare(o1.getAge(), o2.getAge()));
```

>HashSet

```sh
HashSet 底层的实现其实是一个 HashMap。
根据对象的 hashCode 来确定元素在集合中的存储位置，因此具有良好的存取和查找性能。
#保证元素唯一性的方式依赖于： hashCode() 与 equals() 方法。
```

```sh
在 JDK1.8 之前，哈希表底层采用'数组+链表'实现，即使用链表处理哈希冲突，同一 hashCode 值的元素都存储在一个链表里。
但是当位于一个桶中的元素较多，即 hashCode 值相等的元素较多时，通过 key 值依次查找的效率较低。

而 JDK1.8 中，哈希表存储采用'数组+链表+红黑树'实现，当链表长度超过阈值（8）时，将链表转换为红黑树，这样大大减少了查找时间。
```

![](assets/java4.png)

![](assets/java5.png)

> LinkedHashSet

```sh
链表 和 哈希表组合的一个数据存储结构。可以保证元素有序，即存入和取出顺序一致
```



```java

```

```sh

```



>list & set

```sh
list 的 contains() 和 remove()底层调用的都是 equals()
Set  .................................... hashCode() 和 equals()   
```

```

```



## Map

> map & Collection

```sh
Map 存储的是键值对（key唯一）。#Collection 存储的是单列集合
Map 存储元素使用 put()。     #Collection 使用 add()
Map 集合没有直接取出元素的方法，而是先转成 Set 集合，再通过迭代获取元素

#自定义对象做为Map的key，必须重写 hashCode 和 equals（保证key唯一）。
```

```sh
#高度注意：Map类集合 K/V 能不能存储 null 值的情况

集合类                Key             Value          Super          说明
-------------------------------------------------------------------------------
HashTable            不允许为 null    不允许为 null    Dictionary     '线程安全'
ConcurrentHashMap    不允许为 null    不允许为 null    AbstractMap    分段锁技术
TreeMap              不允许为 null    允许为 null     AbstractMap     线程不安全
HashMap              允许为 null      允许为 null     AbstractMap     线程不安全
```

```sh

```



```java
//使用 put 方法新增元素时，当前 key 在集合中不存在，即没有对应的 value，则返回 null。如果有对应的 value，则返回覆盖前的 value。
public V put(K key, V value);
```

>HashMap

```sh
'HashMap'      ：'哈希表'结构。元素'存取无序'。由于要保证键的唯一、不重复，需要重写 key 的'hashCode()方法、equals()方法'。
'LinkedHashMap'：HashMap 的子类，'链表+哈希表'结构。元素'存取有序'（链表保证）。也需要重写以上两个方法
```



>HashMap & HashTable & ConcurrentHashMap

```sh
HashTable线程安全, 在多线程情况下, 同步操作能保证程序执行的正确性.
但是, HashTable每次同步执行时,都要锁住整个结构.


ConcurrentHashMap 正是为了解决这个问题而诞生的.
ConcurrentHashMap 锁的方式是稍微细粒度的, 将hash表分为16个桶(默认值), 诸如get,put,remove等常用操作只锁当前需要用到的桶。
原来只能一个线程进入, 现在却能同时16个写线程进入 (写线程才需要锁定,而读线程几乎不受限制), 并发性的提升是显而易见的!!!

ConcurrentHashMap的'读取操作没有用到锁定',所以读取操作几乎是完全的并发操作.
而'写操作锁定的粒度又非常细', 比起之前又更加快速(桶越多,表现越明显). '只有在求size等操作时才需要锁定整个表'
```



# Collection-Plus

## 常见问题




##List去重

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
#equals相同 --> hashCode相同 --X--> equals相同
equals相同，但hashCode不同，称为'哈希冲突'。冲突会导致操作哈希表的时间开销增大，所以尽量定义好 hashCode()方法，能加快哈希表的操作。
```

```shell
#重写 equals()方法，必须也重写定 hashCode()方法
对于，String a = new String(“abc”); String b = new String(“abc”);
如果只覆写 equals()，不覆写 hashCode() 的话，那么 a和b 的 hashCode 就会不同。
把这两个类当做 key 存到 HashMap 中的话就会出现问题，就会和 key 的唯一性相矛盾。
```

## 快速失败

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

```sh
增强for循环，其实是Java提供的'语法糖'，其实现底层原理还是借助 Iterator 迭代器实现。

ArrayList 非线程安全，因此在使用迭代器的过程中，如果有其他线程修改了list，那么将抛出并发修改异常，即'fast-fail'机制。
```

> 快速失败

```sh
主要是通过 modCount（修改次数）实现，对 ArrayList 内容的修改都将增加这个值。

在 Iterator 初始化过程中，会将这个值赋给迭代器的 expectedModCount。
在迭代过程中，判断 modCount 跟 expectedModCount 是否相等，如果不相等就表示已经有其他线程修改了 list。
#注意：modCount 声明为 volatile，保证线程之间修改的可见性。
```

> 解决方案

```java
//方案1： 普通for循环。因为普通for循环并没有用到 Iterator 的遍历。
for (int i = 0; i < 1; i++) {
    if (list.get(i).equals("a")) {
        list.remove(i);
    }
}
```

```java
//方案2： 拷贝源集合
List<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));
ArrayList<String> copyList = new ArrayList<>(list); //源集合的拷贝
for (String s : copyList) {
    if ("a".equals(s)) {
        list.remove(s);
    }
}
```

```java
//方案3： 使用 Iterator 进行增删操作，而不是集合 list 本身
Iterator<String> iterator = list.iterator();
while (iterator.hasNext()) {
    String next = iterator.next();
    if ("a".equalsIgnoreCase(next)) {
        // list.remove(next); //也会抛异常 -> ConcurrentModificationException

        iterator.remove(); //OK
    }
}
```

```java
//方案4： 使用 Jdk1.8 中提供的 filter 过滤
list = list.stream().filter(x -> !x.equalsIgnoreCase("a")).collect(Collectors.toList());
```

```java
//方案5： 使用 fail-safe 的集合类 ConcurrentLinkedDeque
ConcurrentLinkedDeque<String> list = new ConcurrentLinkedDeque<>();
list.add("a");
list.add("b");
for (String s : list) {
    if (s.equals("a")) {
        list.remove();
    }
}
```



# 相关问题

## 相关练习

> List的交集、并集、差集

```java
List<Integer> list1 = new ArrayList<>(Arrays.asList(1, 5, 7));
List<Integer> list2 = new ArrayList<>(Arrays.asList(21, 5, 17));

list1.retainAll(list2);
System.out.println(list1); //交集：[5]

list1.removeAll(list2);
list1.addAll(list2);
System.out.println(list1); //并集：[1, 7, 21, 5, 17]

list1.removeAll(list2);
System.out.println(list1); //差集：[1, 7]
```



## 概念区分

> Collection & Collections

```sh
'Collection'  是集合类的上级接口，继承于他的接口主要有 Set，List
'Collections' 是针对集合类的一个帮助类，提供一系列静态方法实现对各种集合的搜索、排序、线程安全化等操作
```

> Vector & ArrayList & LinkedList

```sh

```



# 算法相关

## 排序算法

> 冒泡排序

```sh
#比较相邻的元素。如果第一个比第二个大，就交换它们两个
最佳情况：T(n) = O(n)  最差情况：T(n) = O(n2)  平均情况：T(n) = O(n2)
```

```java
private static void bubbleSort() {
    int[] arr = {9, 56, 72, 16}; //升序排列：相邻比较，大者排后
    int temp;
    for (int i = 0; i < arr.length; i++) { //表示趟数，一共 arr.length-1 次
        for (int j = 0; j < arr.length - 1 - i; j++) { //每趟遍历元素个数，和当前趟数有关
            if (arr[j] > arr[j + 1]) {
                temp = arr[j];
                arr[j] = arr[j + 1];
                arr[j + 1] = temp;
            }
        }
    }
}
```

```sh
#改进方案：
数据的顺序排好之后，冒泡算法仍然会继续进行下一轮的比较，直到 arr.length-1 次，后面的比较没有意义的。

设置标志位flag，如果发生了交换flag设置为 true；如果没有交换就设置为 false。
这样当一轮比较结束后如果flag仍为 false，即：这一轮没有发生交换，说明数据的顺序已经排好，没有必要继续进行下去。
```

```java
private static void bubbleSort() {
    int[] arr = {9, 56, 72, 16}; //升序排列：相邻比较，大者排后
    int temp;
    boolean flag; //置换标识位
    for (int i = 0; i < arr.length; i++) { //表示趟数，一共 arr.length-1 次
        flag = false;
        for (int j = 0; j < arr.length - 1 - i; j++) { //每趟遍历元素个数，和当前趟数有关
            if (arr[j] > arr[j + 1]) {
                temp = arr[j];
                arr[j] = arr[j + 1];
                arr[j + 1] = temp;
                flag = true;
            }
        }
        if (!flag) break;
    }
}
```

> 选择排序

```java
//首先，在未排序序列中找到最小（大）元素，存放到排序序列的起始位置
//然后，再从剩余未排序元素中继续寻找最小（大）元素，放到已排序序列的末尾。
//以此类推，直到所有元素均排序完毕

```



##其他算法

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
```

```java
//(2).Map值排序
List<Map.Entry<Character, Integer>> list = new ArrayList<>(map.entrySet());
Collections.sort(list, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));
System.out.println(JSON.toJSONString(list));
```


# 开发手册

## 集合处理

> hashCode 和 equals 的处理

```sh
（1）只要重写 equals，就必须重写 hashCode
（2）Set 集合元素不重复的依据是 hashCode 和 equals。所以，Set 存储的对象必须重写这两个方法。
（3）Map 集合的 Key 也不能重复，原因同上
```

> 推荐使用集合的 `isEmpty() 代替 0==collection.size()`

```java
if (0 == imageSet.size()){}
if (imageSet.isEmpty()){} //size()是通过迭代集合得到，随着元素数量增加，调用 size()变得越来越慢。
```
> 初始值大小

```sh
initialCapacity = (需要存储的元素个数 / 负载因子) + 1。
#注意负载因子（即loaderfactor）默认为 0.75， 如果暂时无法确定初始值大小，请设置为 16（即默认值）。

HashMap 需要放置 1024 个元素，由于没有设置容量初始大小，随着元素不断增加，容量 7 次被迫扩大， resize 需要重建 hash 表，严重影响性能。
```

```java
//集合初始化时，尽量指定集合'初始值大小'。大小应和实际存储元素个数相近，减少扩容次数。
List<String> list = new ArrayList<>(5); //默认16，加载因子0.75
```

> 不要使用集合实现来赋值静态成员变量

```java
//对于集合类型的静态成员变量，不要使用集合实现来赋值，应该使用【静态代码块】赋值
private static Map<String, Integer> map = new HashMap<>();
static {
    map.put("a", 1);
};
```

> `asList() & subList()` 

```sh
'asList()'  返回的是 Arrays 内部类，并不是真正的 ArrayList。它没有实现集合的修改方法，如 add()/remove()/clear()
'subList()' 返回的是 ArrayList 的内部类，是 ArrayList 的一个视图，对于 SubList 子列表的所有操作最终会反映到原列表上
```

```java
public void test() {
    List<String> asList = Arrays.asList("a", "b", "c");
    // asList.add("d"); -> UnsupportedOperationException

    ArrayList<String> list = new ArrayList<>(asList); //正解,先转换
    list.add("d");
}
```

```java
public void test() {
    ArrayList<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));
    List<String> subList = list.subList(0, 2);

    // ArrayList<String> list1 = (ArrayList<String>) subList; -> ClassCastException
}
```

> `toArray()`

```java
//直接使用 toArray() 无参方法返回值只能是 Object[].【不推荐】
public void test() {
    ArrayList<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));
    String[] array = list.toArray(new String[list.size()]); //推荐使用
}
```

> `foreach()`

```sh
Map遍历推荐使用 entrySet() 集合，而不是 keySet() 方式进行遍历
```

```java
//(1).keySet() 其实遍历了 2 次：一次是转为 Iterator 对象，另一次是从Map中取出 key 所对应的 value
for (String key : map.keySet()) {
    System.out.println(key + ":" + map.get(key));
}

//(2).entrySet() 只遍历 1 次：遍历1次就把 kV 都放到了 entry 中，效率更高。JDK8的 Map.forEach() 就是这个原理
for (Map.Entry<String, String> entry : map.entrySet()) {
    System.out.println(entry.getKey() + ":" + entry.getValue());
}

//(3).JDK8 -> Map.forEach() ---> 原理同（2）
map.forEach((k, v) -> System.out.println(k + ":" + v));
```

> `sort()`

```sh
JDK7 以上，Comparator 要满足以下'三个条件'，不然 Arrays.sort()，Collections.sort()会报异常 IllegalArgumentException。
'自反性'：x，y 的比较结果和 y，x 的比较结果相反
'传递性'：x > y， y > z，则 x > z
'对称性'：x = y，则 x，z 比较结果和 y，z 比较结果相同
#所以，对于基本数据类型，要采用其包装类的 Compare(x, y) 进行比较
```

````java
new Comparator<Person>() {
    @Override
    public int compare(Person o1, Person o2) {
        return Integer.compare(o1.getAge(), o2.getAge()); //推荐使用
    }
};
````

> 频繁调用 `Collection.contains()` 方法请使用 Set

```sh
在 java 集合类库中，List 的 contains 方法普遍时间复杂度是 O(n)。
如果在代码中需要频繁调用 contains 方法查找数据，可以先将 list 转换成 HashSet 实现，将 O(n) 的时间复杂度降为 O(1)。
```

```java
ArrayList<Integer> list = otherService.getList();
Set<Integer> set = new HashSet(list);
for (int i = 0; i <= Integer.MAX_VALUE; i++) {
    set.contains(i); // 时间复杂度O(1)
}
```

> 相互转化

```java
String[] strArray = {"a", "b", "c"};
List<String> list = Arrays.asList(strArray); //Array 转换 List

//【不推荐】直接使用 toArray() 无参方法，因为其返回值只能是 Object[]。
String[] array = list.toArray(new String[list.size()]); //List 转 Array
```
> 返回`空数组和空集合`而不是 null

```sh
返回 null，需要调用方强制检测 null，否则就会抛出空指针异常。
返回空数组或空集合，有效地避免了调用方因为未检测 null 而抛出空指针异常，还可以删除调用方检测 null 的语句使代码更简洁。
```

```java
public static Result[] getResults() {
    return new Result[0];
}
```

