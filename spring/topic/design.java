#观察者
#异步回调机制

#builder

#----------------------------------------------------------------------------------------------

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
























--- Abstract Factory
    Provides one level of interface higher than the factory pattern. It is used to return one of several factories.
--- Builder
    Construct a complex object from simple objects step by step.
--- Factory Method
    Provides an abstraction or an interface and lets subclass or implementing classes decide which class or method should be
    instantiated or called, based on the conditions or parameters given.
--- Prototype
    Cloning an object by reducing the cost of creation.
--- Singleton
    One instance of a class or one value accessible globally in an application.
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