#观察者
#异步回调机制

#GoF23: Group of Four,四个人提出的23中设计模式.



//{--------<<<ABC>>>---------------------------------
#创建型模式
    单例(Singleton), 工厂, 抽象工厂, Builder, 原型.

#结构型模式
    适配器模式、桥接模式、装饰模式(Decorator), 组合模式、外观模式、享元模式、代理模式。

#行为型模式
    模版方法模式、命令模式、迭代器模式、观察者模式、中介者模式、备忘录模式、
    解释器模式、状态模式、策略模式、职责链模式、访问者模式。

//}

//{--------<<<Singleton>>>---------------------------X
#单例: 解决一个类在内存中只存在一个对象的问题.(适用于实例化需要较多资源的对象)
    (1).私有构造函数, 避免其他类实例化该类对象
    (2).为了让其他程序可以访问到该类对象,在本类中自定义一个对象
    (3).对外提供一个获取类对象的方法 ---> 必须 static
    
    0.区别对比
        饿汉式  --->  线程安全,调用效率高. 不能'延时加载'
        懒汉式  --->  线程安全,但效率不高. 可以'延时加载'
        
        双重检测锁 -> 饿汉式是方法锁,此方法是局部锁.
        静态内部类 -> 线程安全,调用效率高, 可以'延时加载'!!
        
        枚举实现   -> 线程安全,调用效率高, 不能'延时加载'
                   -> 枚举本身就是单例模式,由 JVM 从根本上提供保障! 避免通过反射和反序列化的漏洞!
                   
    0.如何选用
        占用资源少,不要 延时加载: //枚举式     > 饿汉式
        占用资源大,需要 延时加载: //静态内部类 > 懒汉式

    1.饿汉式
        public class Singleton {
            private Singleton() {}

            private static Singleton instance = new Singleton();

            //(a).static 变量会在类装载时初始化,此时也不会涉及多个线程访问该对象的问题.
            //(b).虚拟机保证只会装载一次该类,肯定也不会发生并发访问的问题. 
            //      因此,可以省略关键字 synchronized
            public static /*synchronized*/ Singleton newInstance() {
                return instance;
            }
        }
    
    2.懒汉式
        public class Singleton {
            private Singleton() {}

            //延迟加载 ---> 假如 newInstance() 从不调用,则也不会实例化对象,有效避免了资源的浪费.
            private static Singleton instance;

            //懒汉式 ---> 对于多线程访问容易出问题,加上同步锁
            public static synchronized Singleton newInstance() {
                if (null == instance) {
                    instance = new Singleton();
                }
                return instance;
            }
        }
    
    3.双重检测锁 ---> (有问题,不建议使用) ---> https://www.cnblogs.com/xz816111/p/8470048.html
        public class Singleton {
            private Singleton() {}
            
            private volatile static Singleton instance; ///关键字: volatile

            //双重检测锁
            //(1).内层: 必须有
            //(2).外层: 使用同步锁会消耗系统资源,加上外层判断,可减少判断次数,稍微提高效率
            public static Singleton newInstance() {

                if (null == instance) {
                    synchronized (Singleton.class) { //静态方法的同步锁一般选为当前类的 class 文件
                        if (null == instance) {
                            instance = new Singleton();
                        }
                    }
                }
                return instance;
            }
        }
        
    4.静态内部类
        public class Singleton {
            private Singleton() {}

            // – 外部类没有 static 属性,则不会像饿汉式那样立即加载对象
            // – 只有真正调用 newInstance(),才会加载静态内部类.加载类时是线程安全的.
            // - static final instance, 保证了内存中只有这样一个实例存在,而且只能被赋值一次,从而保证了线程安全性.
            // – 兼备了并发高效调用和延迟加载的优势!!!
            private static class SingletonInner {
                private static final Singleton instance = new Singleton();
            }

            public static Singleton newInstance() {
                return SingletonInner.instance;
            }
        }
        
    5.枚举实现
        public enum Singleton {
            INSTANCE;

            public void doSth() {
            }
        }
    
    6.反射破解(不包括枚举)
        @Test
        public void test05() throws Exception {
            Singleton instance0 = Singleton.newInstance();
            Singleton instance1 = Singleton.newInstance();
            System.out.println(instance0 == instance1); ///true

            Class<?> clazz = Class.forName("com.example.spring.test.Singleton");
            Constructor<?> constructor = clazz.getDeclaredConstructor(); //无参构造
            constructor.setAccessible(true); //必要
            Object instance2 = constructor.newInstance();
            System.out.println(instance2 == instance0); ///false
        }
        
    7.反序列化破解(不包括枚举)
        @Test
        public void test05() {
            Singleton instance0 = Singleton.newInstance();

            String path = "d:/1.txt";
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
                 ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {

                oos.writeObject(instance0); //序列化, implements Serializable

                Singleton instance3 = (Singleton) ois.readObject(); //反序列化
                
                System.out.println(instance0 == instance3); ///false
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        
    8.效率测试
        public static void main(String[] args) throws InterruptedException {
            long start = System.currentTimeMillis();
            int nThread = 10;

            //同步辅助类,它允许一个或多个线程一直等待,直到其他线程的操作执行完后再执行
            CountDownLatch count = new CountDownLatch(nThread); //初始值为线程的数量 10

            for (int i = 0; i < nThread; i++) {
                new Thread(() -> {
                    for (int j = 0; j < 10000; j++) {
                        Singleton instance = Singleton.newInstance();
                    }

                    count.countDown(); //每当一个线程完成了自己的任务后,计数器的值就会减 1
                }).start();
            }

            count.await(); // main线程阻塞,直到 count 减到0,才继续向下执行
            System.out.println("耗时: " + (System.currentTimeMillis() - start));
        }
        
#常见场景
    Windows 的 Task-Manager(任务管理器) 和 Recycle-Bin(回收站). 在整个系统运行过程中,一直维护着仅有的一个实例
    操作系统的文件系统,也是大的单例模式实现的具体例子,一个操作系统只能有一个文件系统
    
    项目中读取配置文件的类. 没必要每次使用配置文件数据,每次 new 一个对象去读取.
    
    网站的计数器,一般也是采用单例模式实现,否则难以同步
    
    应用程序的日志应用,一般都何用单例模式实现,这一般是由于共享的日志文件一直处于打开状态,因为只能有一个实例去操作,否则内容不好追加
    
    数据库连接池的设计一般也是采用单例模式,因为数据库连接是一种数据库资源
    
    Application 也是单例的典型应用(Servlet编程中会涉及到)
    在servlet编程中,每个Servlet也是单例
    
    在Spring中,每个Bean默认就是单例的,这样做的优点是Spring容器可以管理
    在Spring MVC框架/struts1框架中,控制器对象也是单例


//}


//{--------<<<装饰>>>--------------------------------X
#基础
    (0).装饰模式(Decorator),也叫包装模式(Wrapper)
    (1).动态的为一个对象增加新的功能
    
#优点
    (2).是一种用于代替'继承'的技术. /*勿需继承就能扩展子类的功能*/
    (3).使用对象的关联关系代替继承关系,更加灵活. /*有效避免类型体系的快速膨胀*/

#缺点
    (4).产生很多小对象. 大量小对象占据内存,一定程度上影响性能
    (5).装饰模式易于出错,调试排查比较麻烦
    
#DEMO
    1.抽象构件角色Component
public interface IMan {
    void doit();
}

    2.具体构件角色ConcreteComponent(真实对象)
        class Man implements IMan {

            @Override
            public void doit() {
                System.out.println("da da da...");
            }
        }

    3.装饰角色Decorator 
class ISuperMan implements IMan {
    protected IMan man;

    ISuperMan(IMan man) {
        this.man = man;
    }

    @Override
    public void doit() {
        man.doit();
    }
}

    4.1.具体装饰角色ConcreteDecorator 
class SpiderMan extends ISuperMan {

    SpiderMan(IMan man) {
        super(man);
    }

    @Override
    public void doit() {
        super.doit();
        System.out.println("fly fly fly...");
    }
}

    4.2.具体装饰角色ConcreteDecorator 
class IronMan extends ISuperMan {

    IronMan(IMan man) {
        super(man);
    }

    @Override
    public void doit() {
        super.doit();
        System.out.println("money money money...");
    }
}
        
    5.TEST
@Test
public void test() {
    Man man = new Man();
    man.doit();

    SpiderMan spiderMan = new SpiderMan(man);
    spiderMan.doit();

    IronMan ironMan = new IronMan(new SpiderMan(new Man()));
    ironMan.doit();
}
    
#常见场景
    (0).Servlet API 中的 HttpServletRequestWrapper 增强了 request 对象的功能
    (1).IO流实现细节, 举例以 BufferedOutputStream
    
1.抽象构件角色Component 
public interface Flushable {
    
    void flush() throws IOException;
}

2.具体构件角色ConcreteComponent(真实对象)
public abstract class OutputStream implements Closeable, Flushable {
    
    public void flush() throws IOException {
    }
}        

3.装饰角色Decorator 
public class FilterOutputStream extends OutputStream {
    protected OutputStream out;
    
    public FilterOutputStream(OutputStream out) {
        this.out = out;
    }
    
    public void flush() throws IOException {
        out.flush();
    }
}

4.具体装饰角色ConcreteDecorator 
public class BufferedOutputStream extends FilterOutputStream {
    
    public BufferedOutputStream(OutputStream out) {
        this(out, 8192);
    }
    
    public synchronized void flush() throws IOException {
        flushBuffer();
        out.flush();
    }
}

//}

//{--------<<<abc>>>---------------------------------
#观察者 Observer Pattern
    //One object changes state, all of its dependents are updated automatically.
    在对象之间定义了一对多的依赖; 这样一来,当一个对象改变状态,依赖它的对象会收到通知并自动更新.
    
    应用场景:
        邮件订阅
        聊天室程序,服务器转发给所有客户端
        servlet中监听器的实现
        android中的广播机制; app推送消息

    场景举例:
        购票后, 发送短信 + 记录日志 + 下发优惠卷...
        常规写法,在订阅火车票内部实现,若以后还需要增加新功能则需要修改核心代码
        
        public class ObserverTest {
            @Test
            public void test0() {
                TicketSubject ts = new ObserverTest().new TicketSubject();

                ts.addObserver(new ObserverTest().new SMSObserver()); //短信通知
                ts.addObserver((x, y) -> System.out.println("log 4 it: " + y)); //记录日志

                ts.set("buy a ticket - LION KING");
            }

            class TicketSubject extends Observable { //被观察者类(火车票类)

                void set(String ticket) {
                    System.out.println(ticket);

                    setChanged();
                    notifyObservers(ticket);
                }
            }

            class SMSObserver implements Observer { //观察者类(短信通知类)

                @Override
                public void update(Observable o, Object arg) {
                    TicketSubject subject = (TicketSubject) o;
                    System.out.println("sms 4 it: " + arg);
                }
            }
        }
        
#异步回调机制
    //观察者模式: 
        //定义对象的一种一对多的依赖关系,当一个对象的状态发送改变时,所有对他依赖的对象都被通知到并更新
        //它是一种模式, 通过接口回调的方法实现的, 即它是一种回调的体现.
    //接口回调:
        //与观察者模式的区别: 机制是原理, 模式是实现

        /**
         * @author BlueCard
         * @desc: 异步回调
         * @date 2018年8月4日 上午9:04:18
         */
        public class CallbackTest {
            public static void main(String[] args) {
                Server server = new Server();
                Client client = new Client(server);

                //Server监听Client的发送消息动作,响应相应逻辑,然后回调Client处理成功的方法
                client.sendMsg("Server,Hello~");
            }
        }

        //回调模式-回调接口类
        interface CSCallBack {
            void process(String status);
        }

        class Client implements CSCallBack {

            private Server server;

            Client(Server server) {
                this.server = server;
            }

            void sendMsg(final String msg) {
                System.out.println("客户端：发送的消息为：" + msg);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        server.getClientMsg(Client.this, msg);
                    }
                }).start();
                System.out.println("客户端：异步发送成功");
            }

            @Override
            public void process(String status) {
                System.out.println("客户端：服务端回调状态为：" + status);
            }
        }

        class Server {

            void getClientMsg(CSCallBack csCallBack, String msg) {
                System.out.println("服务端：服务端接收到客户端发送的消息为:" + msg);
                
                try {
                    Thread.sleep(5 * 1000); // 模拟服务端需要对数据处理
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("服务端:数据处理成功，返回成功状态 200");
                String status = "200";
                csCallBack.process(status);
            }
        }
        
#Builder
    Construct a complex object from simple objects step by step.
    构建一个类时,需要做一系列的准备工作. 准备好之后,通过build()方法,把具有需要功能的类构建出来
    
    主要解决: 
        创建'一个复杂对象', 通常由各个部分的子对象用一定的算法构成;
        由于需求的变化, 这个复杂对象的各个部分经常面临着剧烈的变化, 但是将它们组合在一起的算法却相对稳定
        
    优点
        1.可读性强 //把不同的功能放在不同的方法里, 链式调用使得代码更加简洁
        
        2.可选择性强 
        //例如类的属性非常多,但在实例化时可能根据不同场景,使用的属性数量不一样
        //假如使用构造方法来实例化, 就需要生成很多不同参数的构造方法,不仅会增加大量用处不大的代码,可读性也不强; 
        //相反, Builder模式可以轻松解决多变需求！
    
    缺点: 增加了一个内部类Builder的代码量. 需要根据不同的场景进行定夺是否使用(不可滥用)
        
    注意：与工厂模式的区别是：建造者模式更加关注与零件装配的顺序。

    场景: Person类含三个属性name,age,heiget. 其中name为必须属性,age和heiget为可选属性. 
          不同场景需要构造函数的参数个数不同,怎么解决????
    
    (1).重叠构造器
        //1.当可变参数过多就无法控制 
        //2.对于多个可选参数的类型相同时,代码阅读性差(如以下2和3)
        public Person(String name) {}
        public Person(String name, int age) {}
        public Person(String name, int heiget) {}
        public Person(String name, int age, int heiget) {}
    
    (2).javaBean模式
        //构造过程被分到多个调用中,在构造中javaBean可能处于不一致的状态.
        //因为只能检测对象实例是否为空,却无法检测对象域值的有效性.
        
        //如: 多线程情况,当线程1执行 Person p = new Person(1) 后, 此时线程2需要使用p实例, 
        //但p是一个没有被完全构造的对象实例,导致程序出错,而且这样的错误难以调试.
        public Person(String name){this.name = name; }
        public void setAge(int age){ this.age = age; }
        public void setHeight(int heiget){ this.heiget = heiget; }
    
    (3).Builder模式生成不可变对象

        class Person {
            private final String name;// 必要参数
            private int age;// 可选参数
            private int height;// 可选参数

            // (b).构造方法私有 -> 以Builder的build()方法替代
            private Person(String name, int age, int height) {
                this.name = name;
                this.age = age;
                this.height = height;
            }

            // (a).静态内部类 -> 与外部类Person剥离关系,否则就耦合在一起. Builder类相当于一个独立文件类的效果
            static final class Builder {
                private final String name;
                private int age;
                private int height;

                public Builder(String name) {
                    this.name = name;
                }

                // (c).Builder类的每个方法都返回Builder,只有这样,才能在每一次设置时都是针对同一个Builder实例进行实例
                public Builder age(int age) {
                    this.age = age;
                    return this;
                }

                public Builder height(int height) {
                    this.height = height;
                    return this;
                }

                // (d).在build()方法对Person类进行初始化,可以避免过早的对Person类初始化.
                // 当然,也可以在Builder的构造函数里对Person进行初始化,见仁见智.
                public Person build() {
                    return new Person(name, age, height);
                }
            }
        }

#装饰模式 Decorator Pattern
    add additional features or behaviors to a particular instance of a class, while not modifying the other instances of same class
    在保持原有类方法签名完整性的前提下, 提供额外的功能
    
    //装饰和继承的区别：
        1.装饰模式比继承要灵活，避免了继承体系臃肿。而且降低了类与类之间的关系。
        2.装饰类因为增强已有对象，具备的功能和已有的是相同的，只不过提供更强大的功能。
        3.装饰类和被装饰类通常是都属于同一个体系。

        //自定义类将已有对象传入,基于已有的功能,并提供加强功能,则自定义类称为装饰类
        class Person{
            public void chifan(){
                System.out.println("吃饭 chifan");
            }
        }

        //自定义装饰类
        class SuperPerson{
            private Person p;
            
            //将已有对象传入
            SuperPerson(Person p){
                this.p=p;
            }
            public void superChifan(){
                System.out.println("叉子 chazi");
                p.chifan();
            }
        }    



//}




















--- Abstract Factory
    Provides one level of interface higher than the factory pattern. It is used to return one of several factories.
--- Builder
    Construct a complex object from simple objects step by step.
--- Factory Method
    Provides an abstraction or an interface and lets subclass or implementing classes decide which class or method should be
    instantiated or called, based on the conditions or parameters given.
--- Prototype
    Cloning an object by reducing the cost of creation.
// --- Singleton
    // One instance of a class or one value accessible globally in an application.
Structural Patterns
--- Adapter
    Convert the existing interfaces to a new interface to achieve compatibility and reusability of the unrelated classes
    in one application. Also known as Wrapper pattern.
--- Bridge
    Decouple an abstraction or interface from its implementation so that the two can vary independently.
--- Composite
    Build a complex object out of elemental objects and itself like a tree structure.
--- Facade
 Provide a unified interface to a set of interfaces in a subsystem. Facade defines a higher-level interface that makes the subsystem easier to use. Wrap a complicated subsystem with a simpler interface.
--- Flyweight
    Make instances of classes on the fly to improve performance efficiently, like individual characters or icons on the screen.
--- Proxy
    Use a simple object to represent a complex one or provide a placeholder for another object to control access to it.
Behavioral Patterns
--- Chain of Responsibility
    Let more than one object handle a request without their knowing each other. Pass the request to chained objects until
    it has been handled.
--- Command
    Streamlize objects by providing an interface to encapsulate a request and make the interface implemented by subclasses
    in order to parameterize the clients.
--- Interpreter
    Provides a definition of a macro language or syntax and parsing into objects in a program.
--- Iterator
    Define an object that encapsulates details and other objects interact with such object. The relationships are loosely decoupled.
--- Mediator
Decouple the direct communication between objects by introducing a middle object, the mediator, that facilitates the communication
 between the objects.
--- Memento
    To record an object internal state without violating encapsulation and reclaim it later without knowledge of the original object.
--- State
    An object's behavior change is represented by its member classes, which share the same super class.
--- Strategy
    Group several algorithms in a single module to provide alternatives. Also known as policy.
--- Template Method
    Provide an abstract definition for a method or a class and redefine its behavior later or on the fly without changing its structure.
--- Visitor
    Define a new operation to deal with the classes of the elements without changing their structures.