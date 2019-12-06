

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

//{--------<<<Annotation>>>------------------------X
#概念
    1.基础概念
        注解不是程序本身,可看作注释.    -> 这一点和普通注释没区别.
        可被其他程序(如: 编译器)读取.    -> 又区别于注释.

    2.常见注解
        @Override, @Deprecated, @SuppressWarnings("*") //用于抑制编译器产生警告信息
        
        @SuppressWarnings("unchecked") //抑制单类型的警告
        @SuppressWarnings(value={"unchecked", "rawtypes"}) //多类型
        @SuppressWarnings("all") //所有类型
    
#自定义注解
    #使用 @interface 自定义注解(接口)时,自动继承 java.lang.annotation.Annotation. <---> 接口 extends 接口
    
    #元注解 ---> 负责注解其他注解.    
    #@Target: 描述注解的使用范围
        ElementType.TYPE        -> //Class, interface (including annotation type), enum
        ElementType.FIELD        -> //Field (includes enum constants)
        ElementType.METHOD        -> //Method 
        ElementType.PARAMETER    -> //parameter
        
    #@Retention: 需要在什么级别保留该注解信息
        RetentionPolicy.SOURCE    -> //源文件中保留.
        RetentionPolicy.CLASS    -> //class文件中保留.
        RetentionPolicy.RUNTIME    -> //运行时保留,可被反射机制读取.(常用)

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
        准备    -> //为 NUM 分配内存,并设置默认值,即 int 的初始值 0.
        初始化    -> //为 NUM 赋值,即 55.
        
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
            & |        ---> 左无论真假,右都进行运算
            && ||    ---> 左为真(假),右参与运算; 左为假(真),则右不参与运算


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
    boolean Mkdir();          //只用于创建单层目录
    boolean Mkdirs();          //用于创建多层目录
    
    boolean renameTo();          //重命名抽象路径名表示的文件
        
    1.递归删除
        @Test
        public void test01() {
            removeDirs(new File("F:\\var\\lib\\webpark\\logs\\aiRes"));
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
        应用层                 --> 传输层 --> 网际层 --> 主机至网络层
        
    3.常用协议
        应用层: HTTP/FTP;    传输层: UDP/TCP;    传输层: IP;

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





//{--------<<<Object>>>----------------------------
#ABC
    1.命名规则
        包名统一小写,统一单数,类名使用复数.  //com.example.mydemo.util.CommUtils
        常量命名全部大写,单词间用下划线隔开. //MAX_STOCK_COUNT
        
    2.运算符
        %(取模)        -> 左<边,值为左. //-1%5==5
        
        ^(异或)        -> 左右同为 false,左右不同为 true. (和其他差别很大)
        
        '&'和'&&'    -> //都是逻辑与运算符,但后者为短路运算
            单&, 左边无论真假,右边都要运算
          双&, 如果左边为真,右边参与运算; 如果左边为假,那么右边不参与运算
        
        <<(位运算)
            <<(左移) -> 乘以 2 的移动位数次幂 //3<<2 = 3 * 2^2 =12
            >>(右移) -> 除以 2 .............. //6>>2 = 6 / 2^2 =1
            
    3.栈内存 //值类型
        存储局部变量. 当数据使用完,所占空间会自动释放
        
    4.堆内存 //引用类型
        (1).数组和对象. 通过 new 建立的实例都存放在堆内存中
        (2).每一个实体都有内存地址值
        (3).实体中的变量都有默认初始化值
        (4).实体不再被使用,会在不确定的时间内被系统的垃圾回收器回收
        
        //首先,在 堆内存 中划分一个长度为 4 的int数组
        //其次,在 栈内存 中划分一个名为 arr 的变量
        //最后,再将 int 数组第一个元素的内存地址赋给 arr 变量
        int[] arr = new int[4];
        
    5.extends
        java 只支持'单继承',不支持'多继承'. 但支持'多层继承'和'多实现'.
        //当多个父类中定义了相同函数名的函数,而各个函数的实现各不相同,则子类就不确定要调用哪一个
        
    6.implements
        接口可看作一个特殊的抽象类. //方法全是抽象的
        一个类可以实现多个接口
        类与类之间是'继承关系', 类与接口之间是'实现关系', 接口与接口之间是'继承关系'
        
    7.基本数据类型
        boolean 类型(1位,1bit)
        字符类型(char-2字节,2byte)
        数值类型(整数类型 + 浮点类型)
            byte-1, short-2, int-4, long-8
            float-4, double-8

#Object
    Java中所有类的父类, 其定义的 11 个方法子类都可以使用.
    
    1.toString    //默认返回: 对象的类型名+@+内存地址值
        public String toString() {
            return getClass().getName() + "@" + Integer.toHexString(hashCode());
        }

    2.equals    //判断两对象是否是相同(2种方式: 默认和自定义).        
        (1).默认对于运算符"=="采用内存地址进行比较
        
        (2).自定义比较方式,重写 equals()
        
        public boolean equals(Object o) {
            // 如果对象地址一样，则认为相同
            if (this == o)
                return true;

            // 如果参数为空,或者类型信息不一样,则认为不同
            if (o == null || this.getClass() != o.getClass())
                return false;

            Person person = (Person) o;
            // 比较基本类型(age)相等,
            // 比较引用类型(name)交给java.util.Objects类的equals静态方法取用结果
            return age == person.age && Objects.equals(name, person.name);
        }
        
#Objects
        //类中的方法都是'空指针安全的'或'容忍空指针的'
        public static boolean equals(Object a, Object b) {
            return (a == b) || (a != null && a.equals(b));
        }
        
#System
    1.currentTimeMillis    //返回以毫秒为单位的当前时间
        public static native long currentTimeMillis();
        
    2.arraycopy    //数组拷贝,非常高效    
        public void test() {
            List<String> list = Arrays.asList("a", "b", "c");
            String[] src = list.toArray(new String[list.size()]); //src
            
            String[] dest = new String[2]; //dest
            System.arraycopy(src, 1, dest, 0, 2);
            Arrays.stream(dest).forEach(System.out::println); //b-c
        }

#Date
    1.getTime
        //把日期对象转换成对应的时间毫秒值,等同于后者
        System.out.println(new Date().getTime() == System.currentTimeMillis());
    
#DateFormat
    1.计算个人已经出生了多少天
        public void test() throws ParseException {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            Date birth = format.parse("1990-04-21 09:31:30.110"); //String -> Date
            long len = System.currentTimeMillis() - birth.getTime();
            long day = len / (24 * 60 * 60 * 1000);

            String now = format.format(new Date()); //Date -> String
            System.out.println("今天: " + now + ", 已出生: " + day + "天");
        }
        
//}


//{--------<<<Collection>>>------------------------X
#Collection
    数组 -> 固定长度, 只能放一种类型
  集合 -> 可变长度, 可以存多种类型(不考虑泛型)

    //集合放原始类型,其实是通过装箱拆箱来实现的. 以前原生类型只能用数组,现在集合也可以了
    List<Integer> list = new ArrayList<>();
    list.add(5);
    
  1.list //排列有序,可重复
        Vector     -> 线程安全        效率低,被 ArrayList 替代
        
        ArrayList  -> 线程不安全    查询快,增删慢        底层数组实现,新增时会涉及到数组拷贝
        LinkedList -> 线程不安全    查找慢,增删快        底层双向循环链表实现
        
    2.set //排列无序(存入和取出的顺序不一定相同),不可重复
        HashSet        -> 线程不安全    存取速度快            底层哈希表实现,内部是HashMap
        TreeSet        -> 线程不安全    排序存储(可排序)    底层二叉树实现,内部是TreeMap
        
    3.list和set比对
        list 的 contains() 和 remove()底层调用的都是 equals()
        Set  ....................................... hashCode() 和 equals()        
        
    4.Set如何保证元素唯一性???
        先比较 hashCode(), 如果相同,继续比较 equals() 是否为同一个对象.
        
        //hashCode相同, equals不同, 怎么存储呢???
        在同样的哈希值下顺延(可认为哈希值相同的元素放在一个哈希桶中),也就是哈希一样的存一列.        
        
    5.TreeSet排序是如何进行的呢???
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
    
        //(2).集合添加比较器
        //元素自身不具备比较性,或具备的比较性不满足要求时. 需要让 TreeSet 集合自身具备比较性
        TreeSet<Dog> dogSet = new TreeSet<>(new Comparator<Dog>() {
            @Override
            public int compare(Dog o1, Dog o2) {
                if (o1.getName().compareTo(o2.getName()) == 0) { //先比较 name,再比较 age
                    return Integer.compare(o1.getAge(), o2.getAge());
                } else {
                    return o2.getName().compareTo(o1.getName());
                }
            }
        });

#具体子类
    6.Vector
        线程安全,效率慢. 支持线程的同步,即某一时刻只有一个线程能够写 Vector
    
    7.ArrayList
        底层是通过'数组'实现. 当 add/remove 时,需要对数组进行复制,移动, 代价比较高
        
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
        
#Map
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
    
#常见错误        
    0.初始化
        //集合初始化时,尽量指定集合初始值大小. 大小应和实际存储元素个数相近,减少扩容次数.
        List<String> list = new ArrayList<>(5);
    1.asList
        //asList() 返回对象是 Arrays 内部类,并没有实现集合的修改方法.(add/remove/clear)
        public void test() {
            List<String> asList = Arrays.asList("a", "b", "c");
            // asList.add("d"); -> UnsupportedOperationException

            ArrayList<String> list = new ArrayList<>(asList); //正解,先转换
            list.add("d");
        }
        
    2.subList
        //subList() 返回的是 ArrayList 的内部类 SubList,并不是 ArrayList
        //而是 ArrayList 的一个视图,对于 SubList 子列表的所有操作最终会反映到原列表上
        public void test() {
            ArrayList<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));
            List<String> subList = list.subList(0, 2);
            
            // ArrayList<String> list1 = (ArrayList<String>) subList; -> ClassCastException
        }
        
    3.toArray
        //直接使用 toArray() 无参方法返回值只能是 Object[]. <<不推荐>>
        public void test() {
            ArrayList<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));
            String[] array = list.toArray(new String[list.size()]); //推荐使用
        }
    
    4.foreach
        //不要在 foreach 循环里进行元素的 remove/add 操作
        //remove 元素请使用 Iterator 方式,如果并发操作,需要对 Iterator 对象加锁
        public void test() {
            ArrayList<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));

            // for (String s : list) {
            //     if ("a".equalsIgnoreCase(s)) {
            //         list.remove(s); //异常 -> ConcurrentModificationException
            //     }
            // }
            // System.out.println(JSON.toJSON(list));

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

    5.foreach
        //使用 entrySet 遍历 Map 类集合 KV, 而不是 keySet 方式进行遍历
        public void test() {
            Map<String, String> map = new HashMap<String, String>() {{
                put("k1", "v1");
                put("k2", "v2");
            }};

            //(1).二次取值 -> 其实是遍历了 2 次,一次是转为 Iterator 对象,另一次是从 hashMap 中取出 key 所对应的 value
            for (String key : map.keySet()) {
                System.out.println(key + ":" + map.get(key));
            }

            //(2).推荐使用 -> 只遍历了一次就把 key 和 value 都放到了 entry 中,效率更高. JDK8使用 Map.forEach()
            for (Map.Entry<String, String> entry : map.entrySet()) {
                System.out.println(entry.getKey() + ":" + entry.getValue());
            }

            //(3).JDK8 -> Map.forEach()
            map.forEach((k, v) -> System.out.println(k + ":" + v));
        }
        
    6.sort
        //JDK7 以上, Comparator 要满足自反性,传递性,对称性.
        //不然 Arrays.sort(), Collections.sort() 会报异常 IllegalArgumentException 
        new Comparator<Person>() {

            @Override
            public int compare(Person o1, Person o2) {
                //return o1.getAge() > o2.getAge() ? 1 : -1; //没有处理相等的情况

                return Integer.compare(o1.getAge(), o2.getAge()); //推荐使用此方法
            }
        };

    7.null
        //高度注意 Map 类集合 K/V 能不能存储 null 值的情况
        集合类                Key                Value            Super            说明
        -------------------------------------------------------------------------------
        Hashtable            不允许为 null    不允许为 null    Dictionary        线程安全
        ConcurrentHashMap    不允许为 null    不允许为 null    AbstractMap        分段锁技术
        TreeMap                不允许为 null    允许为 null        AbstractMap        线程不安全
        HashMap                允许为 null        允许为 null        AbstractMap        线程不安全
        
    8.去重
        //对于存储大量不重复元素,应该选用 Set 集合,利用其元素唯一性特点
        //而不应该选用 list,使用 List.contains() 进行遍历,对比,去重操作
    
//}



//{--------<<<static>>>----------------------------
#static 是一个修饰符,用于修饰类, 类的成员变量, 类的成员方法
    1.随着类的加载而加载
        当类被内存所调用的同时, static 修饰的成员也同时在内存中(方法区)划分存储空间.
        也就是说,静态成员会随着类的消失而消失,说明它的生命周期最长.
        //所以,不建议定义更多的静态变量.
        
    2.优先于类的对象存在，被类的所有对象所共享~
        静态方法中不可以使用关键字 this, super. 直接被类名所调用

#实例变量和静态变量
    1.存放位置
        实例变量-随着对象的建立,存在于堆内存中
        静态变量-随着类的加载而建立,存在于方法区中
        
    2.生命周期
        实例变量-随着对象的消失而消失
        静态变量-生命周期最长,随着类的消失而消失

#成员变量和局部变量
    1.成员变量
        成员变量定义在类中,在整个类中都可以被访问
        成员变量随着对象的建立而建立,存在于对象所在的堆内存中
        生命周期分: 成员变量和静态成员变量
        //成员变量有默认初始化值

    2.局部变量
        局部变量只定义在局部范围内. 如: 函数内,语句内等
        局部变量存在于栈内存中
        作用的范围结束,变量空间会自动释放
        //局部变量没有默认初始化值
    
#静态代码块 > main() > 构造代码块 > 构造函数
    1.静态代码块
        在类中只使用关键字 static{} 声明的代码块.
        每个静态代码块只会执行一次,用于初始化类的属性. 
        JVM 在加载类时,会先执行静态代码块, 所以静态代码块优先 main()
        
    2.构造代码块
        直接在类中用{ }扩起来的代码块.
        每次 new 对象时都会被调用. 执行顺序优先于构造函数
        作用: 给所有的对象进行统一,共性的初始化.
        
    3.构造函数
        每个类都有一个默认的无参构造函数.
        作用: 给通过此构造函数(构造函数不止一种) new 的对象进行初始化
        
#对象初始化过程
    Person p = new Person("zhangsan",20);
    
    (1).因为用到了 Person.class, 所以会先找到 Person.class 字节码文件并加载到内存中
    (2).执行该类中的 static '静态代码块', 给 Person.class 类进行初始化
    (3).在'堆内存'中开辟空间,分配内存地址
    (4).在'堆内存'中建立对象的特有属性. 并进行默认初始化(string 类型默认为 null, int 默认为 0)
    (5).对属性进行显示初始化 (即定义类时,属性含有默认的值 private String name = "xiaowang")
    (6).对对象进行'构造代码块'初始化
    (7).对对象进行对应的'构造函数'初始化
    (8).在栈内存中生成变量 p1,将内存地址赋给占内存中的 p1 变量
    
#成员内部类 + 静态内部类 + 局部内部类 + 匿名内部类
    1.成员内部类
        定义在外部类中,可以访问外部类的所有成员变量和方法.
    
    2.静态内部类
        如果内部类中有 static 成员,则内部类一定是 static 类
        当外部类的 static 法方法访问内部类时,内部类也必须是 static
        
        静态内部类 只可以访问外部类的 static 静态成员
        
    3.局部内部类
        //定义在方法中,只能访问方法中定义的 final 类型的常量.
        像局部变量一样,不能被 public, protected, private 和 static 修饰
        
    4.匿名内部类
        没有名字的局部内部类. 隐式地 extends 一个父类或 implements 一个接口.
        
    
//}


//{--------<<<HashMap>>>---------------------------
HashMap 根据键的 hashCode 值存储数据，大多数情况下可以直接定位到它的值，因而具有很快
的访问速度，但遍历顺序却是不确定的。 HashMap 最多只允许一条记录的键为 null，允许多条记
录的值为 null。HashMap 非线程安全，即任一时刻可以有多个线程同时写 HashMap，可能会导
致数据的不一致。如果需要满足线程安全，可以用 Collections 的 synchronizedMap 方法使
HashMap 具有线程安全的能力，或者使用 ConcurrentHashMap。我们用下面这张图来介绍 HashMap 的结构。

JAVA7 实现
    大方向上，HashMap 里面是一个数组，然后数组中每个元素是一个单向链表。上图中，每个绿色
    的实体是嵌套类 Entry 的实例，Entry 包含四个属性：key, value, hash 值和用于单向链表的 next。
    1.  capacity：当前数组容量，始终保持 2^n，可以扩容，扩容后数组大小为当前的 2 倍。
    2.  loadFactor：负载因子，默认为 0.75。
    3.  threshold：扩容的阈值，等于 capacity * loadFactor
    
JAVA8 实现
    Java8 对 HashMap 进行了一些修改，最大的不同就是利用了红黑树，所以其由 数组+链表+红黑
    树 组成。
    根据 Java7 HashMap 的介绍，我们知道，查找的时候，根据 hash 值我们能够快速定位到数组的
    具体下标，但是之后的话，需要顺着链表一个个比较下去才能找到我们需要的，时间复杂度取决
    于链表的长度，为 O(n)。为了降低这部分的开销，在 Java8 中，当链表中的元素超过了 8 个以后，
    会将链表转换为红黑树，在这些位置进行查找的时候可以降低时间复杂度为 O(logN)。

#ConcurrentHashMap
    1.Segment
        ConcurrentHashMap 和 HashMap 思路是差不多的，但是因为它支持并发操作，所以要复杂一
        些。整个 ConcurrentHashMap 由一个个 Segment 组成，Segment 代表”部分“或”一段“的
        意思，所以很多地方都会将其描述为分段锁。注意，行文中，我很多地方用了“槽”来代表一个
        segment。
        
    2.线程安全
        简单理解就是，ConcurrentHashMap 是一个 Segment 数组，Segment 通过继承
        ReentrantLock 来进行加锁，所以每次需要加锁的操作锁住的是一个 segment，这样只要保证每
        个 Segment 是线程安全的，也就实现了全局的线程安全。
    
    3.并行度
        concurrencyLevel：并行级别、并发数、Segment 数，怎么翻译不重要，理解它。默认是 16，
        也就是说 ConcurrentHashMap 有 16 个 Segments，所以理论上，这个时候，最多可以同时支
        持 16 个线程并发写，只要它们的操作分别分布在不同的 Segment 上。这个值可以在初始化的时
        候设置为其他值，但是一旦初始化以后，它是不可以扩容的。再具体到每个 Segment 内部，其实
        每个 Segment 很像之前介绍的 HashMap，不过它要保证线程安全，所以处理起来要麻烦些。
        
    4.Java8
        Java8 对 ConcurrentHashMap 进行了比较大的改动,Java8 也引入了红黑树。    
    
//}


//{--------<<<java0>>>-----------------------------
#this-super
    this:  子类调用'本类'的同名成员时
    super: 子类调用'父类'............
    
    0.DEMO
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
    
// #final-finally-finalize
    // finally  --> 异常处理的一部分,代码肯定会被执行. 常用于释放资源.
    // finalize --> Object类的一个方法,用于垃圾回收.
    
    // final    --> 修饰类 -> 不能被继承; 修饰方法 -> 不能被重写; 修饰变量 -> 就是常量(只能被赋值一次).
    
#一个".java"源文件中是否可以包括多个类(不是内部类)？有什么限制？
    可以有多个类,但只能有一个 public 的类,并且 public 的类名必须与文件名相一致
    
#&和&&
    都是逻辑与的运算符. 其中,后者为短路操作.
    
        if (x == 33 & ++y > 0) //y 会增长
        if (x == 33 && ++y > 0) //y 不会增长
    
#如何跳出当前的多重嵌套循环？
    //让外层的循环条件表达式的结果,可以受到里层循环体代码的控制
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

#switch能否作用在 byte 上,能否作用在 long 上,能否作用在 String 上?
    switch 作用在 byte,short,char,int 这几个基本数据类型和封装类型 或 enum 枚举常量.
    其中, byte,short,char 都可以隐含转换为 int. //不支持 boolean
    
    另外, java7支持 String类型,是通过调用 String.hashCode(), 将String转换为 int
    
#short s1 = 1; s1=s1+1; 和 s1+=1;
    前者, s1+1 运算时会/*自动提升表达式的类型*/,所以结果是 int 型,
    再赋值给 short 类型 s1时,编译器将报告需要强制转换类型的错误.
    
    后者, //+= 编译器会进行特殊处理,因此可以正确编译.
    
#char 型变量中能不能存贮一个中文汉字? 
    char 型变量是/*用来存储 Unicode 编码的字符集*/,Unicode 编码字符集中包含了汉字,
    所以,char 型变量中当然可以存储汉字.
    
    不过,如果某个特殊的汉字没有被包含在 Unicode 编码字符集中,
    那么,这个 char 型变量中就不能存储这个特殊汉字. 
    
    //补充: Unicode 编码占用两个字节, char 类型的变量也占两个字节.
    
#final 修饰一个变量时, 是引用不能变,还是引用的对象不能变?
    引用变量不能变,引用变量所指向的对象中的内容还是可以改变的.
    
    (1).final 修饰基本数据类型的变量,那么这个变量的值就定了,不能变了.
    (2).final 修饰的引用类型的变量, 那么该变量存的是一个内存地址,该地址就不能变了.
        但是该内存地址所指向的那个对象还是可以变的, 就像你记住了人家的门牌号,但你不能管人家家里人员数量.
    
        //定义方法的参数时,通过 final 来阻止方法内部修改传进来的参数对象,
        //如果, 参数是基本数据类型,则可以实现
        //但是, 对于引用类型,则无法达到目的
        public void method(final StringBuffer sb) {
            sb.append("abc");
        }

// #== equals
    // boolean(1bit); char(2byte); byte(1byte), short(2byte), int(4byte), long(8byte), float(4byte), double(8byte)
    // //前者, 用来比较两个变量的值是否相等
    // 对于基本数据类型,比较其值. 对于引用类型,比较堆内存地址.
    
    // //后者, 用于比较两个独立对象的内容是否相同
    // 默认都是比较堆内存地址. 可根据自身需求,重写 equals()
    
    // // String.==     -> 比较两字符串的'堆内存地址'是否相同, 属于数值比较
    // // String.equals -> ..............内容................, 属于内容比较
    
        // public void test() {
            // String s1 = "abc";
            // String s2 = "abc";
            // String s3 = new String("abc");
            // String s4 = s3;

            // System.out.println(s1 == s2);        //true  字符串常量池中已有"abc",则不会再创建
            // System.out.println(s1 == s3);        //false 比较两对象的堆内存地址
            // System.out.println(s3 == s4);        //true  引用传递
            // System.out.println(s1.equals(s3));    //true  String重写equals()
        // }
    
#Math.round(11.5) 等於多少? Math.round(-11.5) 等於多少?
    double ceil = Math.ceil(-11.5);   //向上取整-返回double -11.0    
    double floor = Math.floor(-11.5); //向下取整-返回double -12.0
    
    long round = Math.round(-11.5); //四舍五入-返回long -11 (-11>-12)
    long round = Math.round(11.5);  //12
    
#public, private, protected 
    作用域        当前类    同一包    子孙类    其他包
    public            *        *        *        *
    protected        *        *        *        
    default            *        *
    private            *
    
#构造器 Constructor 是否可被 override?
    构造器 Constructor 不能被继承,因此不能重写 Override. 但可以被重载 Overload 
    
#接口-抽象类
    接口 implements 接口; 抽象类 implements 接口; 抽象类 extends 具体类; 
    抽象类中可以有静态的 main() 方法
    
    //抽象类与普通类的唯一区别: 不能创建实例对象和允许有 abstract 方法
    
#输出结果
    class A {
        int a = 1;
        double d = 2.0;

        void show() {
            System.out.println("Class A: a=" + a + "\td=" + d);
        }
    }
    class B extends A {
        String d = "Java program.";

        void show() {
            super.show();
            System.out.println("Class B: a=" + a + "\td=" + d);
        }
    }
    // Class A: a=1    d=2.0
    // Class B: a=1    d=Java program.
    
#java 中会存在内存泄漏吗,请简单描述
    分配出去的内存不再被使用,但无法GC回收
    eg: stream, file等资源在程序结束前应该被手动释放,否则容易造成内存泄漏
    
#保留两位有效小数
    double val = 1.1250; //1.1250 & 1.1251
    BigDecimal bigDecimal = new BigDecimal(val);

    //负数先取绝对值再四舍五入再负数. 四舍五入 ---> 1.13 & 1.13
    BigDecimal round0 = bigDecimal.round(new MathContext(3, RoundingMode.HALF_UP));

    //五舍六入 ---> 1.12 & 1.13
    BigDecimal round1 = bigDecimal.round(new MathContext(3, RoundingMode.HALF_DOWN));

    //(X)另一种思路
    String num0 = String.format("%.2f", val);// 12.13/12.13
    String num1 = new DecimalFormat("#0.00").format(val);// 12.12/12.13
    
    //DecimalFormat特殊字符说明
    //    "0"指定位置不存在数字则显示为0: 123.123 ->0000.0000 ->0123.1230
    //    "#"指定位置不存在数字则不显示: 123.123 ->####.####  ->123.123
    //    "."小数点
    //    "%"会将结果数字乘以100 后面再加上% 123.123 ->#.00%  ->12312.30%
    
// #String反转
    // //先转换为 StringBuilder,再调用 StringBuilder.reverse()
    // String str = new StringBuilder("asdfghjjkk").reverse().toString();
    
// #try{}里的 return 语句,在 finally 之前还是之后执行？？    
    // //return : 可当作终止语句来用,经常用来跳出当前方法,并返回一个值给调用方法. 然后该方法就结束了,不会执行return下面的语句
    // //finally: 无论try语句发生了什么,无论抛出异常还是正常执行,finally语句都会执行
    // private int doTry() {
        // int x = 1;
        // try {
            // return ++x;
        // } catch (Exception e) {
        // } finally {
            // ++x;
            // System.out.println("finally - " + x);
        // }
        // return x;
    // }
    
    // 无论 try 里执行了 return, break, continue, finally 都会继续执行.
    // 即, 使用 return 语句把控制权转移给其他的方法前,会执行 finally 语句. //执行完 finally 才执行 return
    
    // 如果try语句里有return，那么代码的行为如下：
    // 1.如果有返回值，就把返回值保存到局部变量中
    // 2.执行jsr指令跳到finally语句里执行
    // 3.执行完finally语句后，返回之前保存在局部变量表里的值
    
// #异常捕获
    // 其中, NullPointerException extends RuntimeException extends Exception, 结果如何?
    // try {
        // throw new NullPointerException("b");
    // } catch (RuntimeException e0) {
        // System.out.println("RuntimeException");
    // } catch (Exception e) {
        // System.out.println("Exception");
    // }
    
    // 结果打印: RuntimeException    
    
#用程序给出随便大小的 10 个数, 序号为1-10,按从小到大顺序输出,并输出相应的序号
    //Map 值排序
    Map<Integer, Integer> map = new HashMap<Integer, Integer>() {{
        put(1, 9);
        put(2, 7);
    }};
    List<Map.Entry<Integer, Integer>> list = new ArrayList<>(map.entrySet());
    
    // Collections.sort(list, Comparator.comparingInt(Map.Entry::getValue)); //lamda再简化
    Collections.sort(list, (o1, o2) -> Integer.compare(o1.getValue(), o2.getValue()));
    System.out.println(list);
    
    
//}

//{--------<<<java1>>>-----------------------------
#final 修饰一个变量时,是引用不能变,还是引用的对象不能变？
    引用变量不能变,引用变量所指向的对象中的内容还是可以改变的.
    
    public void doFinal(final int i, final StringBuilder sb) {
        // i = i + 1; //编译报错,因为final修饰的基本类型 --> 值不能变
        // sb = new StringBuilder(); //同上,修饰引用类型 --> 堆内存地址不能变,即引用不能变

        sb.append("java"); //但可以改变 -> 引用变量所指向的对象中的内容
    }
    
#static 方法是否可以调用非 static 方法？
    不可以, static 方法属于类,直接通过类进行调用. 
    非 static 方法属于对象,必须先实例化对象,才能通过该对象进行调用.
    
    在 static 方法中调用非 static 方法时,可能还没有实例化对象, 这就与以上逻辑不符.
    
#OverWrite & OverLoad
    重写 -> 在子类中,出现和父类中一摸一样的方法.
    重载 -> 同一类中,出现多个方法名一样,但参数列表(参数类型+个数+顺序)不一样的方法.
    
    //重载与方法的返回值类型无关. ---> 方法包括: 修饰符(可选), 返回值类型, 方法名, 参数列表, 方法体
    假设定义了两个只有返回类型不一样的方法: int add(Object o); boolean add(Object o);
    当调用者不关心返回值时,写作: add(obj); 编译器如何区分到底调用的是哪个方法???
    
#静态变量 & 实例变量
    1.生命周期
        静态变量-static 修饰, 随着类的加载而分配内存空间,随着对象的消失而消失.
        实例变量-随着对象的建立而分配内存空间,随着对象的消失而消失.
        
    2.存放区域
        静态变量-存在于方法区.
        实例变量-存在于堆内存.
        
    3.DEMO
        class Test{
            static int staticNum = 0;
            int num = 0;
            
            Test(){
                staticNum++;
                num++;
                
                //对于 staticNum,全局唯一份. 每实例化一个Test对象,staticNum 就加1
                //但是, 对于 num,每实例化一个Test对象,就会重新分配一个,所以一直都只增加1次
                sout(staticNum + " - " + num);
            }
        }
        
#Integer & int
    int 是java提供的 8 种原始数据类型之一,默认值为 0    
    Integer是java为 int 提供的封装类,默认值为 null
    
    即 Integer 可以区分出未赋值和值为 0 的两种情况, int 则无法表达出未赋值的情况.
    如, 要想表达出没有参加考试和考试成绩为0的区别,就只能使用 Integer
    
    另外,Integer 提供了多个与整数相关的操作方法. 如,将一个字符串转换成整数.
    Integer中还定义了表示整数的最大值和最小值的常量.
    
#
    
#ArrayList, Vector, LinkedList

#Arraylist, Vector
    同步性   -> Vector 是线程安全的(同步), ArrayList 是线程序不安全的
    数据增长 -> 当需要增长时,Vector 默认增长一倍, ArrayList 却是 0.5

#HashMap, Hashtable
    
#Collection 和 Collections



//}

//{--------<<<web>>>-------------------------------X
#GET & POST

#servlet-生命周期
    
#forward & redirect
    (1).转发是 1次请求,重定向是 2次请求.
        服务端返回'302状态码+新地址',浏览器解析到302,会立刻向新的地址再次发送请求.
        
    (2).转发时浏览器地址栏地址不变, 重定向则会变.
    
    (3).转发共享 Servlet 中的 request 对象; 重定向则不共享.
    
    (4).转发只能转发到当前项目的内部资源, 重定向则没有限制,甚至可以重定向到网络资源
    
#request.getAttribute() & request.getParameter()
    getAttribute() 只能收到程序用 setAttribute() 传过来的值.
    
    ....
    

    
#页面间对象传递的方法
    request, session, application, cookie等

#JSP & Servlet


#MVC 的各个部分都有那些技术来实现? 如何实现?
    


//}


//{--------<<<nginx>>>-------------------------------
http://server.51cto.com/sOS-588810.htm

nginx + keepalived 实现高可用

轻量级的web服务器/反向代理服务器及电子邮件代理服务器。
特点：内存占用少，并发能力强。

软件负载均衡：
1.阿里云服务器均衡负载SLB
2.Nginx+Keepalived
3.其他软件如LVS(Linux Virtual Server)、haproxy等

nginx日志切分
使用 crontab 定时任务，注意：备份前关闭服务和备份后开启服务

Nginx配置
if(条件为: = ~ ~*) return, break, rewrite.
-f 是否为文件，-d 是否为目录， -e 是否存在

nginx可以对数据进行压缩，对一些图片、html、css、js进行缓存、从而实现动静分离等优化。


负载均衡
    网页访问 http://192.168.5.23:8866/
    实际返回 http://192.168.8.7:8080/index.html （2/3概率）或 http://192.168.8.7:8090/index.html （1/3概率）
    
    #设定负载均衡的服务器列表
    upstream myapp {   
        #weigth -> 参数表示权值，权值越高被分配到的几率越大
        #max_fails -> 有x个请求失败，就表示后端的服务器不可用，默认为1，将其设置为0可以关闭检查
        #fail_timeout -> 在以后的x时间内nginx不会再把请求发往已检查出标记为不可用的服务器
        
        server 192.168.8.7:8080 weight=2 max_fails=2 fail_timeout=30s;   
        server 192.168.8.7:8090 weight=1 max_fails=2 fail_timeout=30s;   
    }
    
    #当前的Nginx的配置
    server {
        listen       8866;
        server_name  localhost;

        location / {
            #设置客户端真实ip地址，而不是nginx的ip
            proxy_set_header X-real-ip $remote_addr;
            
            #负载均衡反向代理
            proxy_pass http://myapp;
            
            root   html;
            index  index.html index.htm;        
        }
    }

配置静态资源
    server {
        listen       8867;
        server_name  abc.com;
        
        #日志存储位置
        access_log  logs/abc.com.access.log;

        location / {
            root   ./abc.com;
            index  index.html index.htm;
        }
        
        #配置反向代理tomcat服务器：拦截.jsp结尾的请求转向到tomcat
        location ~ \.jsp$ {
            #设置客户端真实ip地址
            proxy_set_header X-real-ip $remote_addr;
            
            proxy_pass http://192.168.8.7:8080;
        }
        
        location ~ \.(jpg|png|jpeg|bmp|gif|swf|css)${     
            expires 30d;
            root /nginx-1.4.7;#root:
            break;
        }
    }

    #location / {
    #    root   abc.com;
    #    index  index.html index.htm;
    #}


//}


