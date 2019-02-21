

//{--------<<<hello>>>--------------------------------------------------------------------

//}

//{--------<<<线程池-abc>>>-------------------------------------------------------------------
#优点
	(0).降低资源消耗: 通过重复利用已创建的线程,降低线程创建和销毁造成的消耗
	(1).提高响应速度: 当任务到达时,任务可以不需要等待线程的创建就能立即执行
	(2).提高线程的可管理性: 线程是稀缺资源,如果无限制的创建,不仅会消耗系统资源,还会降低系统的稳定性,
		使用线程池可以进行统一的分配,调优和监控.
		
#生命周期
	线程池是一个进程级的重量级资源. 默认生命周期和 jvm 一致. 即从开启线程池开始,到 jvm 关闭为止.
	如果手工调用 shutdown() 方法,那么线程池执行所有的任务后,自动关闭.


//}

//{--------<<<线程池状态>>>---------------------------------------------------------------
	public class ThreadPoolExecutor extends AbstractExecutorService {	
		volatile int runState;
		static final int RUNNING    = 0;
		static final int SHUTDOWN   = 1;
		static final int STOP       = 2;
		static final int TERMINATED = 3;
	}	
	
#runState
	当前线程池的状态,volatile 保证线程之间的可见性. //取值范围以下四种:

#RUNNING
	线程池创建后,初始处于 RUNNING 状态

#SHUTDOWN
	当调用了 shutdown(),则线程池处于 SHUTDOWN 状态
	此时线程池不能够接受新的任务,它会等待所有任务执行完毕

#STOP
	当调用了 shutdownNow(),则线程池处于 STOP 状态
	............................,并且会尝试终止正在执行的任务
				
#TERMINATED
	当线程池处于状态 SHUTDOWN/STOP,并且所有工作线程已经销毁
	任务缓存队列已经清空或执行结束后,线程池被设置为 TERMINATED 状态
//}

//{--------<<<核心参数>>>-----------------------------------------------------------------
	public class ThreadPoolExecutor extends AbstractExecutorService {
		
		//前面三个构造器,底层都是调用的第四个构造器进行的初始化工作
		public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue);
	 
		public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory);
	 
		public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler);
	 
		public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler);
		//...
	}
	
#poolSize //线程池中当前的线程数		

#corePoolSize //核心池的大小
	默认情况下,线程池创建后池中线程总数为 0.
	当然,也可以预创建线程,/*详见<<线程池中的线程初始化>>*/.
	
	当有任务来之后,就会创建一个线程去执行任务,直到线程池中的线程数目达到 corePoolSize
	当 poolSize > corePoolSize 时,新提交的任务会被放进任务缓存队列 queue
	
#maximumPoolSize //线程池最大线程数
	表示在线程池中最多能创建多少个线程		
	
#keepAliveTime //线程空闲的时间达到多久会被终止
	默认情况下,只有当线程池中的线程数 >corePoolSize 时,keepAliveTime 才会起作用
	即当线程池中的线程数  > corePoolSize,如果一个线程空闲的时间达到 keepAliveTime, 则会终止
	直到线程池中的线程数 <= corePoolSize
	
	但是,如果调用了 allowCoreThreadTimeOut(boolean),
	在线程池中的线程数 <=corePoolSize 时,keepAliveTime 也会起作用,直到线程池中的线程数为 0
	
#unit //keepAliveTime 的时间单位,7种取值:
	TimeUnit.DAYS; .HOURS; .MINUTES; .SECONDS; .MILLISECONDS(毫秒); .MICROSECONDS(微妙); .NANOSECONDS(纳秒)
	
#threadFactory //线程工厂,主要用来创建线程

#workQueue //缓冲队列,用来存储等待执行的任务.(都是线程安全的)	
	1.LinkedBlockingQueue //常用
		基于链表的 FIFO 队列(先进先出); 创建时不指定大小,则使用默认值, Integer.MAX_VALUE
	
	2.ArrayBlockingQueue
		基于数组的 FIFO 队列,此队列创建时必须指定大小
		当队列为空,消费者线程被阻塞; 当队列装满,生产者线程被阻塞
		
	3.SynchronousQueue
		不会缓存任务,而是直接新建一个线程来执行新来的任务
	
	4.PriorityBlockingQueue
		按照'优先级'进行排序的/*无限队列*/,优先级最高的元素将始终排在队列的头部
		存放在其中的元素必须 implements Comparable,这样才能通过实现 compareTo() 进行排序

#handler //任务拒绝策略.以下两种情况,会触发:
	(1).等待队列已满 && poolSize = maximumPoolSize
	(2).调用 shutdown(),会等待线程池里的任务执行完毕,再真正 SHUTDOWN. 在等待间,会拒绝新任务

	1.ThreadPoolExecutor.AbortPolicy(默认)
		丢弃任务,并抛出异常 RejectedExecutionException

	2.ThreadPoolExecutor.DiscardPolicy
		丢弃任务,但不抛出异常. 会导致被丢弃的任务无法再次被执行

	3.ThreadPoolExecutor.DiscardOldestPolicy
		丢弃最旧的未处理请求,然后重新尝试执行任务(重复此过程)

	4.ThreadPoolExecutor.CallerRunsPolicy
		直接在 execute() 的调用线程(可能是主线程)中运行被拒绝的任务
		执行完之后,尝试将下一个任务添加到线程池中,可有效降低向线程池内添加任务的速度

	
//}

//{--------<<<核心方法>>>-----------------------------------------------------------------
	public class ThreadPoolExecutor extends [AbstractExecutorService implements (ExecutorService extends Executor)] {

		// 在 Executor 中声明的方法,在 ThreadPoolExecutor 进行了具体的实现
		// 通过这个方法可以向线程池提交一个任务,交由线程池去执行
		execute();
		
		// 在 ExecutorService 中声明的方法,在 AbstractExecutorService 就已经有了具体的实现
		// 在 ThreadPoolExecutor 中并没有对其进行重写
		// 也是用来向线程池提交任务的,但和 execute() 不同,它能够返回任务执行的结果
		// 底层还是调用 execute(),只不过利用了 Future 来获取任务执行结果
		submit();
		
		//都是用来关闭线程池的,但有差异. 详见<<线程池状态>>
		shutdown();		
		shutdownNow();

	}
//}

//{--------<<<任务的执行>>>---------------------------------------------------------------
#poolSize  < corePoolSize 
	每来一个任务,就会创建一个线程去执行这个任务

#poolSize >= corePoolSize
	每来一个任务,会尝试将其添加到任务缓存队列当中.
	若添加成功,则该任务会等待空闲线程将其取出去执行
	若添加失败(一般来说是任务缓存队列已满),则会尝试创建新的线程去执行这个任务
	
#poolSize = maximumPoolSize
	则会采取'任务拒绝策略'进行处理

#poolSize > corePoolSize
	如果某线程空闲时间超过 keepAliveTime,线程将被终止,直至 poolSize <= corePoolSize.
	如果设置了 allowCoreThreadTimeOut(boolean),那么核心线程也会被终止,直至 poolSize=0.

#举个栗子: corePoolSize = 10, maximumPoolSize = (10+4)
	假如有一个工厂,工厂里面有10个工人,每个工人同时只能做一件任务
	
	因此只要当10个工人中有工人是空闲的,来了任务就分配给空闲的工人做
	
	当10个工人都有任务在做时,如果还来了任务,就把任务进行排队等待
	
	如果说新任务数目增长的速度远大于工人做任务的速度,那么此时工厂主管可能会想补救措施,比如重新招4个临时工人
	然后就将任务也分配给这4个临时工人做
	
	如果说14个工人做任务的速度还是不够,此时工厂主管可能就要考虑不再接收新的任务或者抛弃前面的一些任务了
	
	当这14个工人当中有人空闲时,而新任务增长的速度又比较缓慢,工厂主管可能就考虑辞掉4个临时工了
	只保持原来的10个工人,毕竟请额外的工人是要花钱的

//}

//{--------<<<线程池中的线程初始化>>>-----------------------------------------------------
#默认情况,创建线程池之后,线程池中是没有线程的,需要提交任务之后才会创建线程

#如果需要预创建线程,可通过以下两个方法实现. 创建完成后,等待任务队列中有任务
	prestartCoreThread();	  //初始化  1   个核心线程
	prestartAllCoreThreads(); //初始化 core 个核心线程

//}

//{--------<<<线程池的关闭>>>-------------------------------------------------------------
#shutdown();
	不会立即终止线程池,而是要等缓存队列中的所有任务都执行完后才终止,但再也不会接受新的任务
		
#shutdownNow();
	立即终止线程池,并尝试打断正在执行的任务/*,并且清空任务缓存队列*/,返回尚未执行的任务列表

//}

//{--------<<<线程池容量的动态调整>>>-----------------------------------------------------
#当下列参数从小变大时,ThreadPoolExecutor 进行线程赋值,还可能立即创建新的线程来执行任务

#setCorePoolSize();
	设置核心池大小
	
#setMaximumPoolSize();
	设置线程池最大能创建的线程数目大小
//}

//{--------<<<如何合理配置线程池的大小>>>-------------------------------------------------
	//获取cpu核心数
	int cpuSize = Runtime.getRuntime().availableProcessors();
	
#对于CPU密集型应用, 最大线程数设为 N+1.
#......IO........., .............. 2N+1 (N为CPU总核数)

#对于每天执行一次的低频任务, 核心线程数设为 0.

	/**
	 * 假设系统每秒任务数为500~1000,每个任务耗时0.1秒,最大响应时间2s. tasks=500-1000; taskcost=0.1; resptime=1.
	 *
	 * core = tasks/(1/taskcost) = tasks*taskcost = (500~1000)*0.1 = 50~100, core应该大于50
	 * 然后根据8020原则,即80%情况下,每秒任务数小于1000*20%=200,那么 core=20
	 * 一般,处理请求数 = (10~18) * core;
	 * 
	 * queue = (core/taskcost)*resptime = 20/0.1*2 = 400
	 * 切记不能使用默认值Integer.MAX_VALUE,这样队列会很大,线程数只会保持在corePoolSize大小. 当任务陡增时,不能新开线程来执行,响应时间会随之陡增.
	 * 
	 * max = (max(tasks) - queue)/(1/taskcost) = (1000-400)/10 = 60
	 * 
	 * 以上都是理想值,实际情况下要根据机器性能来决定. 如果在未达到最大线程数的情况机器cpu load已经满了,则需要通过升级硬件和优化代码,降低taskcost来处理.
	 */

//}

//{--------<<<示例-DEMO>>>----------------------------------------------------------------
#自定义类
    static class MyTask implements Runnable {
        private int taskNum;

        MyTask(int num) {
            this.taskNum = num;
        }

        @Override
        public void run() {
            System.out.println(SystemUtils.getAll() + " ---> tasking " + taskNum);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(SystemUtils.getAll() + " ---> task-over: " + taskNum);
        }
    }
	
#测试方法
    @Test
    public void test() throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, //线程池最大容量 4+2
                200, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(2));

        // executor.allowCoreThreadTimeOut(true);
		
        // int b = executor.prestartAllCoreThreads();
        // boolean b = executor.prestartCoreThread();

        print(executor); //开始执行任务前,线程池属性
        for (int i = 1; i <= 7; i++) {
            try {
                executor.execute(new MyTask(i));
            } catch (Exception e) {
                e.printStackTrace(); //线程池属性 -> 添加任务后
            }
            print(executor);
        }
        // executor.shutdown();

        Thread.sleep(6 * 1000); //线程池属性 -> 所有任务执行完毕后
        print(executor);
    }
	
    private static void print(ThreadPoolExecutor executor) {
        System.out.println(SystemUtils.getAll() + " ---> poolSize: " + executor.getPoolSize()
                + "; queueSize: " + executor.getQueue().size()
                + "; completedTask: " + executor.getCompletedTaskCount());
    }
	
#测试结果
	2019-02-20 16:14:18.042 - 1 - main ---> poolSize: 0; queueSize: 0; completedTask: 0 //初始状态
	
	2019-02-20 16:14:18.057 - 1 - main ---> poolSize: 1; queueSize: 0; completedTask: 0 //Task-1
	2019-02-20 16:14:18.057 - 1 - main ---> poolSize: 2; queueSize: 0; completedTask: 0 //Task-2.(poolSize = core)
	
	2019-02-20 16:14:18.057 - 1 - main ---> poolSize: 2; queueSize: 1; completedTask: 0 //Task-3
	2019-02-20 16:14:18.057 - 1 - main ---> poolSize: 2; queueSize: 2; completedTask: 0 //Task-4.(queue 已满)
	
	2019-02-20 16:14:18.058 - 1 - main ---> poolSize: 3; queueSize: 2; completedTask: 0 //Task-5.(扩充 core -> max)
	2019-02-20 16:14:18.058 - 1 - main ---> poolSize: 4; queueSize: 2; completedTask: 0 //Task-6.(core = max)
	
	java.util.concurrent.RejectedExecutionException:									//Task-7.(任务拒绝策略)
		Task x rejected from y[Running, pool size = 4, active threads = 4, queued tasks = 2, completed tasks = 0]
	
	2019-02-20 16:14:18.059 - 11 - pool-1-thread-1 ---> tasking 1
	2019-02-20 16:14:18.059 - 12 - pool-1-thread-2 ---> tasking 2
	2019-02-20 16:14:18.059 - 14 - pool-1-thread-4 ---> tasking 6 //执行先于 Task-3,4 
	2019-02-20 16:14:18.060 - 13 - pool-1-thread-3 ---> tasking 5
	2019-02-20 16:14:20.059 - 11 - pool-1-thread-1 ---> task-over: 1
	2019-02-20 16:14:20.059 - 12 - pool-1-thread-2 ---> task-over: 2
	2019-02-20 16:14:20.059 - 11 - pool-1-thread-1 ---> tasking 3
	2019-02-20 16:14:20.059 - 12 - pool-1-thread-2 ---> tasking 4
	2019-02-20 16:14:20.060 - 13 - pool-1-thread-3 ---> task-over: 5
	2019-02-20 16:14:20.060 - 14 - pool-1-thread-4 ---> task-over: 6
	2019-02-20 16:14:22.059 - 12 - pool-1-thread-2 ---> task-over: 4
	2019-02-20 16:14:22.059 - 11 - pool-1-thread-1 ---> task-over: 3
	
	2019-02-20 16:14:24.061 - 1 - main ---> poolSize: 2; queueSize: 0; completedTask: 6 //所有任务完毕后,停止 >core 的线程

#有个问题
	当 queue 已满,新来一个 task,恰好 core 有一个空闲,哪种情况正确:
	(1).空闲 core 直接执行新来的 task
	(2).空闲 core 取出 queue 头部的任务执行,而将新来的任务 task 放入队尾
	
	//解答: 有空闲 core 就使用,没有则加入队列(队列未满)或新建线程(队列已满). 所以选(1)
	//		从以上 DEMO 中的 Task-5,6 执行先于 Task-3,4 可以验证


//}

//{--------<<<jdk-自带线程池>>>-----------------------------------------------------------
#4种内置线程池
	1.单例线程池
		//有且仅有1个线程处于活动状态,适用于有序执行任务.
		public static ExecutorService newSingleThreadExecutor() {
			return new FinalizableDelegatedExecutorService
				(new ThreadPoolExecutor(1, 1,
										0L, TimeUnit.MILLISECONDS,
										new LinkedBlockingQueue<Runnable>())); //queue Max
		}

	2.固定线程池
		//复用'固定数量'线程处理一个无限队列
		public static ExecutorService newFixedThreadPool(int nThreads) {
			return new ThreadPoolExecutor(nThreads, nThreads,
										  0L, TimeUnit.MILLISECONDS,
										  new LinkedBlockingQueue<Runnable>()); //queue Max
		}
	
	3.缓存线程池
		//适用于大量短生命周期的异步任务(many short-lived asynchronous task)
		public static ExecutorService newCachedThreadPool() {
			return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
										  60L, TimeUnit.SECONDS,
										  new SynchronousQueue<Runnable>());
		}

	4.定时线程池
		//比Timer更安全,功能更强大. --> 如果有任务执行过程中抛出异常,则会跳出,不会影响下次循环.
		public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
			return new ScheduledThreadPoolExecutor(corePoolSize);
		}
	
		public ScheduledThreadPoolExecutor(int corePoolSize) {
			super(corePoolSize, Integer.MAX_VALUE, 0, NANOSECONDS,
				  new DelayedWorkQueue());
		}
	
#两个弊端 
	//参考ali开发文档
	线程池不允许使用 Executors 去创建,而是通过 ThreadPoolExecutor 的方式,
	这样的处理方式让写的同学更加明确线程池的运行规则,规避资源耗尽的风险.
	
	(1).Fixed  & Single   --> queue: MAX, 容易因请求队列堆积,耗费大量内存,甚至OOM
	(2).Cached & Schedule --> max: MAX, 可能会创建非常多的线程,甚至OOM
	
//}

//{--------<<<推荐方案>>>-----------------------------------------------------------------
#DEMO-1
	//org.apache.commons.lang3.concurrent.BasicThreadFactory
	ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
			new BasicThreadFactory.Builder().namingPattern("demo-schedule-pool-%d").daemon(true).build());
		   
#DEMO-2
	//com.google.common.util.concurrent.ThreadFactoryBuilder
	ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("demo-pool-%d").build();

	//Common Thread Pool
	ExecutorService pool = new ThreadPoolExecutor(5, 200,
			0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<>(1024),
			namedThreadFactory, 
			new ThreadPoolExecutor.AbortPolicy());

	pool.execute(() -> System.out.println(Thread.currentThread().getName()));
	pool.shutdown();//gracefully shutdown
		   
#DEMO-3
	<bean id="userThreadPool"
		class="org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor">
		<property name="corePoolSize" value="10" />
		<property name="maxPoolSize" value="100" />
		<property name="queueCapacity" value="2000" />

		<property name="threadFactory" value= threadFactory />
		<property name="rejectedExecutionHandler">
			<ref local="rejectedExecutionHandler" />
		</property>
	</bean>
	
	//in code
	userThreadPool.execute(thread);
		
#DEMO-3-注解版
	@Bean("demoThreadPool") //默认情况, @Bean 注解的参数名称和方法名相同,也可以显示定义
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setThreadNamePrefix("demo-pool-"); //线程名前缀

		executor.setCorePoolSize(5); //核心线程数. 默认 1
		executor.setMaxPoolSize(10); //最大线程数. 默认 Integer.MAX_VALUE
		executor.setQueueCapacity(10); //缓冲队列大小. 默认 Integer.MAX_VALUE

		executor.setAllowCoreThreadTimeOut(true); //核心线程也会超时关闭, 默认false
		executor.setKeepAliveSeconds(KEEP_ALIVE_TIME); //空闲线程的最大存活时间,超过则被回收. 默认60s
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy()); //拒绝策略
		//executor.setThreadFactory(r -> null);

		executor.initialize();
		return executor;
	}

//}


//{--------<<<Spring-异步方法>>>--------------------------------------------------------------------
https://www.cnblogs.com/yangfanexp/p/7747225.html
http://www.cnblogs.com/yangfanexp/p/7594557.html
	
	0.
		@Slf4j
		@Configuration
		public class MyAsyncConfigurer implements AsyncConfigurer {
			/**
			 * x = 线程池中线程个数; y = 缓冲队列中线程个数;
			 *
			 * x < core ---> 新建线程处理任务,即使缓存队列处于空闲状态
			 * core =< x && y < queue ---> 任务放入缓存队列
			 * core =< x && y = queue && x < max ---> 新建线程,扩充core至max
			 * core =< x && y = queue && x = max ---> 根据handler指定的拒绝策略来拒绝任务
			 *
			 * core < x ---> 某线程的空闲时间 > KEEP_ALIVE_TIME,此线程将被终止. 这样,线程池中的线程数恒定维持在 CORE_POOL_SIZE 左右.
			 */

			/**
			 * 任务拒绝处理器(4种),默认AbortPolicy. 以下2种情况会触发:
			 * (0).当 x = max && y = queue, 会拒绝新任务
			 * (1).当调用 executor.shutdown(); 会等待线程池里的任务执行完毕,再真正shutdown. 在此期间,会拒绝新任务
			 *
			 * //直接抛出异常: java.util.concurrent.RejectedExecutionException
			 * ThreadPoolExecutor.AbortPolicy policy = new ThreadPoolExecutor.AbortPolicy();
			 *
			 * // 直接在execute方法的调用线程(可能是主线程)中运行被拒绝的任务; 执行完之后尝试添加下一个任务到线程池中,可有效降低向线程池内添加任务的速度
			 * ThreadPoolExecutor.CallerRunsPolicy policy = new ThreadPoolExecutor.CallerRunsPolicy();
			 *
			 * // 丢弃最旧的未处理请求,然后重试execute
			 * ThreadPoolExecutor.DiscardOldestPolicy policy = new ThreadPoolExecutor.DiscardOldestPolicy();
			 *
			 * // 丢弃被拒绝的任务,会导致被丢弃的任务无法再次被执行
			 * ThreadPoolExecutor.DiscardPolicy policy = new ThreadPoolExecutor.DiscardPolicy();
			 */

			/**
			 * 假设系统每秒任务数为500~1000,每个任务耗时0.1秒,最大响应时间2s. tasks=500-1000; taskcost=0.1; resptime=1.
			 *
			 * core = tasks/(1/taskcost) = tasks*taskcost = (500~1000)*0.1 = 50~100, core应该大于50
			 * 然后根据8020原则,即80%情况下,每秒任务数小于1000*20%=200,那么 core=20
			 * 一般,处理请求数 = (10~18) * core;
			 * 
			 * queue = (core/taskcost)*resptime = 20/0.1*2 = 400
			 * 切记不能使用默认值Integer.MAX_VALUE,这样队列会很大,线程数只会保持在corePoolSize大小. 当任务陡增时,不能新开线程来执行,响应时间会随之陡增.
			 * 
			 * max = (max(tasks) - queue)/(1/taskcost) = (1000-400)/10 = 60
			 * 
			 * 以上都是理想值,实际情况下要根据机器性能来决定. 如果在未达到最大线程数的情况机器cpu load已经满了,则需要通过升级硬件和优化代码,降低taskcost来处理.
			 */
			@Override //Spring线程池
			@Bean("taskExecutor") //默认情况,bean的名称和方法名称相同,也可以通过name属性显示定义
			public Executor getAsyncExecutor() {
				ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
				executor.setThreadNamePrefix("demo-"); //线程名前缀
				executor.setAllowCoreThreadTimeOut(true); //核心线程也会超时关闭, 默认false
				
				executor.setCorePoolSize(CORE_POOL_SIZE); //核心线程数,线程池维护线程的最少数量. 默认1
				executor.setMaxPoolSize(MAX_POOL_SIZE); //最大线程数. 默认Integer.MAX_VALUE
				executor.setQueueCapacity(QUEUE_CAPACITY); //缓冲队列大小. 默认Integer.MAX_VALUE
				executor.setKeepAliveSeconds(KEEP_ALIVE_TIME); //空闲线程的最大存活时间,超过则被回收. 默认60s

				ThreadPoolExecutor.DiscardPolicy policy = new ThreadPoolExecutor.DiscardPolicy();
				executor.setRejectedExecutionHandler(policy);
				//executor.setThreadFactory(r -> null);
				executor.initialize();
				return executor;
			}

			@Override //线程异常处理
			public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
				return (throwable, method, obj) -> {
					StringBuilder sb = new StringBuilder("Method:");
					sb.append(method.getDeclaringClass().getName()).append(".").append(method.getName()).append(";");
					
					sb.append(" Params:");
					Arrays.stream(obj).forEach(sb::append);
					sb.append(";");
					
					sb.append(" Exception: ").append(throwable.getMessage());
					log.info(sb.toString());
				};
			}
		}

	1.使用线程池
		@Slf4j
		@Component
		public class AsyncTask {
			
			@Async("taskExecutor") //标记此方法为异步方法,并使用线程池"taskExecutor"
			public void asyncSimple() {
				try {
					log.info(SystemUtils.getAll() + " - asyncSimple - 0");
					int i = 1 / 0; //模拟异常
				} catch (Exception e) {
					throw new RuntimeException("asyncSimple - " + e.getMessage());
				}
			}
			
			public Future<String> asyncFuture(int index) { //异步调用返回Future
				try {
					log.info(SystemUtils.getAll() + "asyncFuture ---> {} - 0", index);
					int i = 1 / 0;
					return new AsyncResult<>("success:" + index);
				} catch (Exception e) {
					throw new RuntimeException("asyncFuture - " + e.getMessage());
				}
			}
		}
		
	2.测试方法
		@RequestMapping("/hello")
		@RestController
		public class HelloCtrl {
			@Autowired
			AsyncTask asyncTask;
			
			@GetMapping("/asyncTask")
			public void asyncTask() {
				try {
					asyncTask.asyncSimple();
					Future<String> future = asyncTask.asyncFuture(99);
					log.info("{}", future.get()); //在此处处理异常
				} catch (InterruptedException | ExecutionException e) {
					log.info("{}{}", SystemUtils.getAll(), e.getMessage());
				}
			}
		}

		// 类名 - 时间 - 线程id - 线程名 - info信息
		// AsyncTask - 2018-12-06 17:03:33.846 -> 86 - demo-1 --> asyncSimple - 0
		// AsyncTask - 2018-12-06 17:03:33.846 -> 87 - demo-2 --> asyncFuture ---> 99 - 0
		// MyAsyncConfigurer - Method:com.example.task.AsyncTask.asyncSimple; Params:; Exception: asyncSimple - / by zero
		// HelloCtrl - 2018-12-06 17:03:35.847 -> 52 - http-nio-8090-exec-1 --> java.lang.RuntimeException: asyncFuture - / by zero
		
	3.测试结论		
		(0).使用全局注解开启异步模式 @EnableAsync
		(1).局部注解 @Async("*") 标记方法,在调用过程中,自动使用括号内指定的线程池,并且不需要显示的创建线程去执行.
		(3).@Async 也可标记在类上,标志该类中所有的方法均是异步方法.
		(4).异步方法和调用方法一定要写在'不同的类中'; 同一类中不起效果.
		
	4.异步方法的异常处理
		(5).对于返回值是Futrue的异步方法: 
			(a).在调用 future.get() 时捕获异常;
			(b).在异常方法 asyncFuture() 直接捕获异常;
		(6).对于返回值是void的异步方法: 通过 getAsyncUncaughtExceptionHandler() 处理异常;
	
#Spring定时线程池
	0.系统配置
		默认, @Scheduled() 使用大小为1的线程池运行. 可通过配置文件进行配置 'application.properties'
			spring.task.scheduling.pool.size=5 //线程池大小,默认1
			spring.task.scheduling.thread-name-prefix=demoscheduling- //线程名前缀,默认'scheduling-'
		
	1.使用线程池
		@Slf4j
		@Component
		public class ScheduledTask {
			@Autowired
			AsyncTask demoTask;

			@Autowired
			Executor taskExecutor;

			@Scheduled(cron = "*/1 * * * * ?") //1s执行一次,定时获取线程池属性
			public void task2() throws Exception {
				System.out.println(SystemUtils.getAll() + "task2--------------------------");
				
				ThreadPoolTaskExecutor executor = (ThreadPoolTaskExecutor) taskExecutor;
				StringBuilder sb = new StringBuilder("线程属性 -> ");
				sb.append("核心线程数: ").append(executor.getCorePoolSize()).append("; ");
				sb.append("线程池最大数量: ").append(executor.getMaxPoolSize()).append("; ");
				sb.append("活动线程数: ").append(executor.getActiveCount()).append("; ");
				sb.append("线程总数: ").append(executor.getPoolSize()).append("; ");
				
				ThreadPoolExecutor poolExecutor = executor.getThreadPoolExecutor();
				sb.append("曾经创建过的最大线程数量: ").append(poolExecutor.getLargestPoolSize()).append("; ");
				sb.append("任务总数: ").append(poolExecutor.getTaskCount()).append("; ");
				sb.append("已完成任务数: ").append(poolExecutor.getCompletedTaskCount()).append("; ");
				sb.append("线程处理队列长度: ").append(poolExecutor.getQueue().size());
				log.info(sb.toString());
			}

			@Scheduled(initialDelay = 3, fixedRate = 1) //定时任务
			public void task1() {
				try {
					log.info(SystemUtils.getAll() + "task1----------------------------");
					demoTask.asyncSimple();
					log.info("res---{}", demoTask.asyncFuture(6).get());
				} catch (InterruptedException | ExecutionException e) {
					log.info("{}asyncFuture---Exception: {}", SystemUtils.getAll(), e.getMessage());
				}
			}
		}
		
		// ScheduledTask - 2018-12-06 17:52:03.001 -> 91 - demoscheduling-1 --> task2--------------------------
		// ScheduledTask - 线程属性 -> 核心线程数: 2; 线程池最大数量: 6; 活动线程数: 1; 线程总数: 2; 任务总数: 75; 已完成任务数: 74; 线程处理队列长度: 0
		// AsyncTask - 2018-12-06 17:52:03.128 -> 53 - demo-2 --> asyncFuture ---> 6 - 1
		// ScheduledTask - 2018-12-06 17:52:03.128 -> 95 - demoscheduling-2 --> asyncFuture---Exception: java.lang.RuntimeException: asyncFuture - / by zero
		// ScheduledTask - 2018-12-06 17:52:03.128 -> 95 - demoscheduling-2 --> task1----------------------------
		// AsyncTask - 2018-12-06 17:52:03.128 -> 52 - demo-1 --> asyncSimple - 0
		// ScheduledTask - 2018-12-06 17:52:04.001 -> 104 - demoscheduling-3 --> task2--------------------------
		// ScheduledTask - 线程属性 -> 核心线程数: 2; 线程池最大数量: 6; 活动线程数: 2; 线程总数: 2; 任务总数: 78; 已完成任务数: 75; 线程处理队列长度: 1
		
	2.测试结论
		(0).以上有3个定时线程 'demoscheduling-*',且线程前缀符合配置文件.
		(1).方法 asyncFuture() 是在调用 get() 方法时进行异常处理.
		(2).通过 @Autowired Executor taskExecutor; 这种方式获取线程池对象; 并且检查线程池的运行状况.
		
//}

//{--------<<<join()>>>--------------------------------------------------------------------
#三个线程顺序执行 --> 两种解决方案
	1.使用'单例线程池'
	
	2.使用join方法
		public static void main(String[] args) {
			final Thread t1 = new Thread(() -> {
				try {
					System.out.println(SystemUtils.getAll());
					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException ignored) {
				}
			}, "Thread-T1");

			final Thread t2 = new Thread(() -> {
				try {
					t1.join(1000); // 等待线程t1终止,最大等待时间1000ms
					System.out.println(SystemUtils.getAll());
					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException ignored) {
				}
			}, "Thread-T2");

			final Thread t3 = new Thread(() -> {
				try {
					t2.join();
					System.out.println(SystemUtils.getAll());
				} catch (InterruptedException ignored) {
				}
			}, "Thread-T3");

			// 2018-12-05 20:04:31.198 -> 11 - Thread-T1
			// 2018-12-05 20:04:32.200 -> 12 - Thread-T2 //等待1s.(只等待T1执行1s)
			// 2018-12-05 20:04:34.200 -> 13 - Thread-T3 //等待2s.(等待T2执行完毕)
			t2.start(); t3.start(); t1.start();
		}
		
#join()
	#0.当调用某个线程的 join() 方法时,这个方法会挂起调用线程,直到被调用线程结束执行,调用线程才会继续执行.
	#  即 t2 调用 t1.join(),导致 t2 挂起,直至 t1 执行完毕才继续执行 t2
	
	#1.父线程调用子线程,join会让父线程等待子线程结束之后才能继续运行.
	
	#(*-*)2.如果线程被创建了,但还未被起动,调用它的 join() 是没有作用的,将直接继续向下执行!!!!
	
		private static void test01() {
			ArrayList<Thread> threads = new ArrayList<>();
			for (int i = 0; i < 2; i++) {
				int index = i;
				threads.add(new Thread(() -> {
                    System.out.println(SystemUtils.getAll() + index + " - start");
                    TimeUnit.SECONDS.sleep(2);
                    System.out.println(SystemUtils.getAll() + index + " - end");
				}));
			}
			threads.forEach(Thread::start); //启动所有线程
			
			//当主线程main调用t1的join(n)时,main会被挂起,等待n毫秒,然后继续执行.
			threads.forEach(x.join(100));
			System.out.println(SystemUtils.getAll() + "end!");
		}

		//x.join(100); =--> main等待n毫秒,然后继续执行.
		2018-12-07 11:04:18.235 -> 11 - Thread-0 --> 0 - start
		2018-12-07 11:04:18.237 -> 12 - Thread-1 --> 1 - start
		2018-12-07 11:04:18.426 -> 1 - main --> end!
		2018-12-07 11:04:20.238 -> 12 - Thread-1 --> 1 - end
		2018-12-07 11:04:20.238 -> 11 - Thread-0 --> 0 - end
		
		//x.join(); =--> 默认,main无限等待,直到t1执行完毕
		2018-12-07 11:11:44.922 -> 11 - Thread-0 --> 0 - start
		2018-12-07 11:11:44.922 -> 12 - Thread-1 --> 1 - start
		2018-12-07 11:11:46.924 -> 11 - Thread-0 --> 0 - end
		2018-12-07 11:11:46.924 -> 12 - Thread-1 --> 1 - end
		2018-12-07 11:11:46.924 -> 1 - main --> end!
//}

//{--------<<<execute()&&submit()>>>--------------------------------------------------------------------
#两种任务
	'java5之后,任务分两类': 一类 implements Runnable; 另一类 implements Callable.
	(0).两者都可以被 ExecutorService 执行,但前者没有返回值,后者有.
	(1).Callable 只能通过 submit(Callable task) 执行,返回表示任务等待完成的Future.
		
#不同区别
	(1).excute()木有返回值, 会抛出异常
	(2).submit()返回 Future<?>, 不会抛出异常,除非调用 Future.get()
		
#submit三重载
	// <T> Future<T> submit(Callable<T> task); --> 获取结果: future.get()
	private void submitCall() throws InterruptedException, ExecutionException {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<String> future = executor.submit(() -> "submitCall---success!!");
		System.out.println(SystemUtils.getAll() + future.get());
	}

	// <T> Future<T> submit(Runnable task, T result); --> 获取结果: future.get().res
	private void submitRunRes() throws InterruptedException, ExecutionException {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Data data = new Data();
		Future<Data> future = executor.submit(() -> data.res = "submitRunRes---success!!", data);
		System.out.println(SystemUtils.getAll() + future.get().res);
	}

	// <T> Future<T> submit(Runnable task); --> Runnable方法在成功完成时,将会返回 null
	private void submitRun() throws InterruptedException, ExecutionException {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<?> future = executor.submit(() -> System.out.println(SystemUtils.getAll() + "submitRun---success!!"));
		System.out.println("isSuccess: " + (null == future.get()));
	}
	
	// 2018-12-07 20:19:40.342 -> 50 - http-nio-8090-exec-1 --> submitCall---success!!
	// 2018-12-07 20:19:40.342 -> 50 - http-nio-8090-exec-1 --> submitRunRes---success!!
	// 2018-12-07 20:19:40.343 -> 86 - pool-5-thread-1 --> submitRun---success!!
	// isSuccess: true
		
#DEMO
	public void submit() {
		ExecutorService executor = Executors.newCachedThreadPool();
		List<Future<String>> resultList = new ArrayList<>();

		for (int i = 0; i < 3; i++) {
			int index = i;
			Future<String> future = executor.submit(() -> {
				System.out.println(SystemUtils.getAll() + "submit---args: " + index);
				int j = (1 == index) ? 1 / 0 : index;// 制造异常
				return "submit---res: " + index;
			});
			resultList.add(future);
		}

		for (Future<String> future : resultList) {
			try {
				System.out.println(SystemUtils.getAll() + future.get(500, TimeUnit.MICROSECONDS)); //最大等待500ms
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				System.out.println(SystemUtils.getAll() + "Exception---future.get(): " + e.getMessage());
			} finally {
				executor.shutdown();//顺序关闭 -> 执行提交前的任务,但不接受新的任务
			}
		}
	}

	// future.get(); =--> 可能造成两种异常: InterruptedException, ExecutionException
	// future.get(long timeout, TimeUnit unit); =--> 加上获取结果的最大时间限制,可能造成异常: TimeoutException
	2018-12-07 17:06:22.614 -> 12 - pool-1-thread-2 --> submit---args: 1
	2018-12-07 17:06:22.614 -> 11 - pool-1-thread-1 --> submit---args: 0
	2018-12-07 17:06:22.616 -> 1 - main --> Exception---java.util.concurrent.TimeoutException //异常-获取结果超时
	2018-12-07 17:06:22.614 -> 13 - pool-1-thread-3 --> submit---args: 2
	2018-12-07 17:06:22.617 -> 1 - main --> Exception---java.util.concurrent.ExecutionException: java.lang.ArithmeticException: by zero //异常-执行过程错误
	2018-12-07 17:06:22.617 -> 1 - main --> submit---res: 2 //正常结果
//}

//{--------<<<附加>>>--------------------------------------------------------------------
#区别Thread.run()和start()
	.run(): //不会新开线程;直接在调用线程中执行run()方法内容
	
	.start(): 'run()方法必须是public访问权限,返回类型void' //真正实现多线程运行!!! 
		线程的执行是随机的,不是说start了就代表马上执行! 
		而是处于就绪(可运行)状态,一旦得到cpu时间片,将自动执行run方法内的线程体. 
		new出来的线程在run方法结束,run抛出异常,jvm退出时会自动销毁.

#Timer和ScheduledExecutorService
	'Timer: 单线程,串行执行; 单任务异常,整体任务停止'
	'Executors: 并行执行,互不影响; 单任务异常,自身停止,其他任务不影响' 
         
	//一开始, Task_A 能正常1秒执行一次. 
	//Task_B 启动后, 由于 Task_B 完成需要2秒, 导致 Task_A 要等到 Task_B 执行完才能执行.
	//更可怕的是, Task_C 启动后, 抛了异常, 导致整个定时任务全部挂了!!!
	// A A A B A B C(x)
	Timer timer = new Timer();
	timer.schedule(Task_A, 0, 1 * 1000); //1s执行一次
	timer.schedule(Task_B, 2 * 1000, 2 * 1000); //延迟2s,2s执行一次. (完成消耗2s)
	timer.schedule(Task_C, 5 * 1000, 5 * 1000); //延迟5s,5s执行一次. (抛出异常)

	//Task_B, Task_C 不再影响 Task_A 定时执行
	//Task_C 抛出异常后, 只影响自身不再执行, 其他无碍!!!
	// A A B A A A C(x) A B A A A A B
	ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
	pool.scheduleWithFixedDelay(Task_A, 0, 1, TimeUnit.SECONDS);
	pool.scheduleWithFixedDelay(Task_B, 2, 2, TimeUnit.SECONDS);
	pool.scheduleWithFixedDelay(Task_C, 5, 5, TimeUnit.SECONDS);
	
	0.需要注意的是
		//(1).newSingleThreadScheduledExecutor(); 等效于 newScheduledThreadPool(1);'
		//单线程,串行执行; 但单任务抛出异常,只影响自身,不影响其他
		//(2).对于抛出异常的任务,若想在抛出异常后,还可以继续循环执行 -> try-catch包裹!
		
	1.取消单个或整体
		Task_A.cancel(); 
		timer.cancel();
	
		ScheduledFuture<?> future = scheduled.scheduleWithFixedDelay(task1, 1, 1, TimeUnit.SECONDS);
		future.cancel(true); //单个
		pool.shutdown();//整体
//}



//{--------<<<多线程与final>>>--------------------------------------------------------------------
	final + 类			//该类不能作为父类被继承; String
	final + 方法		//该方法不能被重写
	final + 基本变量	//通常用作常量; 圆周率 PI
	final + 引用变量	//保证不会再指向别的引用,"但引用里的值可能变动"
		
	1.方法传参の传的是拷贝の不影响原有值
		public static void main(String[] args) {
			final int num = 99;
			final String str = "ss";
			final Info info = new Info();
			info.data = "info";

			//99 ss info100  ===--> 影响的只有 info.data
			dosth(num, str, info);
			System.out.println(num + " " + str + " " + info.data);
		}
		
		// 只是传入了一份"拷贝", 不会改变原有的值
		private static void dosth(int num, String str, Info info) {
			num++;
			str += num;
			info.data += num; //不改变info拷贝的指向 ---> 影响

			info = new Info(); //改变............... ---> 不影响
			info.data = "new";
		}
	
	2.线程传参の传的是对象の所以不能改变参数的指向
		public static void main(String[] args) {
			final int num = 99;
			final String str = "ss";
			final Info info = new Info();
			info.data = "info";

			new Thread(() -> {
				// num = 88; str = "aa"; info = new Info(); //都报错,final引用的指向不能更改
				info.data = "thread"; //不报错,可以改变final对象的属性,但不能更改final引用

				dosth(num, str, info); //99 ss thread100
				System.out.println(num + " " + str + " " + info.data);
			}).start();
		}
			
	3.为什么内部线程中引用外部对象要加final修饰符呢
		上述代码中传入内部线程的 info, 其实际上是对象 new Info(),而不是引用 info.
		所以, info引用必须加关键字 final, 即不能再随意的指向其他对象,不然会导致内外部指向不一致,产生错乱.
		
		假如传的是引用info, 则每时每刻线程内外 info 的指向都一致,则不会产生错乱.
	
		(/待证/)另外,被内部线程引用的外部对象受到外部线程作用域的制约,有其特定的生命周期.
		//当外部对象在外部线程中生命周期已经结束,而内部线程中还在持续使用,怎样解决问题???'		
		//内部线程变量要访问一个已不存在的外部变量??? 
		在外部变量前添加final修饰符, 其实内部线程使用的这个变量就是外部变量的一个'复制品', 
		即使外部变量生命周期已经结束,内部复制品依然可用.
		
	4.final变量
		如果是'基本数据类型',则其数值一旦在初始化之后便不能更改;
		如果是'引用类型....',则在对其初始化之后便不能再让其指向另一个对象.
		
		方法传参 ---> 传的是引用的一份'拷贝',不会改变原有的值. 但可以改变原有的属性.
		
		
//}

//{--------<<<ThreadLocal>>>--------------------------------------------------------------------
#用于存储 线程局部变量; 本质可看作一个Map,其中key为 Thread.currentThread(), Value为 当前线程的局部变量.
#一个 ThreadLocal 对象只能存放当前线程的一个局部变量; 所以对于多个局部变量,需实例化多个 tl 对象.
#ThreadLocal 中的数据不会随着线程结束而回收,必须手动 remove(),防止内存泄露.
	
    // 操作系统中,线程和进程数量是有上限的, 确定线程和进程的唯一条件就是线程或进程id.
    // 操作系统在回收线程或进程的时候,并不一定杀死; 系统繁忙时,只会清空其栈内数据,然后重复使用.
    // 所以,对于存储在 ThreadLocal 中的数据,如若不 remove(),则有可能在线程 t2 获取到 t1 的数据.
	
#代码demo
	//通常定义为常量,用于关联线程上下文
	private static final ThreadLocal<String> local1 = new ThreadLocal<>();
	
	public void test() {
		local1.set("m1");
		new Thread(() -> local1.set("t1")).start();
		System.out.println("main -> " + local1.get()); //main -> m1
	}
	
#hibernate 中的典型应用
	private static final ThreadLocal threadSession = new ThreadLocal();
	 
	public static Session getSession() throws InfrastructureException {
		Session s = (Session) threadSession.get();
		try {
			if (s == null) {
				s = getSessionFactory().openSession();
				threadSession.set(s);
			}
		} catch (HibernateException ex) {
			throw new InfrastructureException(ex);
		}
		return s;
	}
	
#三者关系: Thread, ThreadLocal, ThreadLocalMap
	// Thread 中有个 ThreadLocal.ThreadLocalMap 类型的成员变量 threadLocals
	public class Thread implements Runnable {
		ThreadLocal.ThreadLocalMap threadLocals = null;
	}
	
	// ThreadLocalMap 是 ThreadLocal 的内部类. 它是一个Map, 它的Key是ThreadLocal类型对象!!
	public class ThreadLocal<T> {
		public void set(T value) {
			Thread t = Thread.currentThread();
			ThreadLocalMap map = t.threadLocals; // 获取当前线程的 threadLocals 变量
			if (map != null) {
				map.set(this, value); // key -> ThreadLocal对象自身; value -> 局部变量
			} else {
				t.threadLocals = new ThreadLocalMap(this, value);
			}
		}
		
		public T get() {
			Thread t = Thread.currentThread();
			ThreadLocalMap map = t.threadLocals; //获取当前线程的 threadLocals 变量
			if (map != null) {
				ThreadLocalMap.Entry e = map.getEntry(this);
				if (e != null) {
					@SuppressWarnings("unchecked")
					T result = (T)e.value;
					return result;
				}
			}
			return null;
		}
		
		static class ThreadLocalMap /* <ThreadLocal<?>, Object> //自己加的,便于理解 */ { 
			//...
		}
	}
		
#总结:	
	//ThreadLocal不是用来解决: 共享对象的多线程访问问题 && 协调线程同步问题
	每个线程内部都有一个 ThreadLocal.ThreadLocalMap 类型的成员变量 threadLocals, 可以将本线程的局部变量保存到其中,多线程之间互不干扰.
	其他线程访问不到本线程的, 每个线程只能访问到自己的.
	
	ThreadLocal 本身并不存储线程局部变量, 只是提供一个在当前线程中找到局部变量的索引key.
	为每一个线程隔离一个类的实例,该实例的作用范围仅限于线程内部.

	'每个ThreadLocal对象只能放一个线程局部变量' 
	局部变量存储在 ThreadLocal.ThreadLocalMap 中, 是以 ThreadLocal对象 为key.
	所以, 一个线程欲存放多个局部变量,则需实例化多个 ThreadLocal 对象
	ThreadLocal 变量通常被 private static 修饰, 用于关联线程上下文.
	
#不正确的理解
	ThreadLocal为解决多线程并发访问提供了一种新的思路
	ThreadLocal的目的是为了解决多线程访问资源时的共享问题
		
//}



