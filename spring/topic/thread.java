

#线程池
	0.优点
		(0).降低资源消耗: 通过重复利用已创建的线程,降低线程创建和销毁造成的消耗
		(1).提高响应速度: 当任务到达时,任务可以不需要等待线程的创建就能立即执行
		(2).提高线程的可管理性: 线程是稀缺资源,如果无限制的创建,不仅会消耗系统资源,还会降低系统的稳定性,
			使用线程池可以进行统一的分配,调优和监控.
			
	1.生命周期
		线程池是一个进程级的重量级资源. 默认生命周期和 JVM 一致. 即从开启线程池开始,到 JVM 关闭为止.
		如果手工调用 shutdown() 方法,那么线程池执行所有的任务后,自动关闭.
		
		
#jdk线程池
	0.单例线程池
		/**
		 * ---> 有且仅有一个线程处于活动状态,适用于有序(FIFO,LIFO,优先级)执行任务.
		 *  
		 * 区别于 newFixedThreadPool(1) --> 如果线程因任务错误而中止, fixed是无法使用'替代线程'(创建一个新的线程替换它执行后续任务),后续任务也将终止.
		 *  
		 * 2018-12-05 16:59:14.427 -> 11 - pool-1-thread-1 ---> 0
		 * 2018-12-05 16:59:16.429 -> 11 - pool-1-thread-1 ---> 1
		 * 2018-12-05 16:59:16.429 -> 11 - pool-1-thread-1 ---> 2 -> Exception:/ by zero //2执行异常! 但不影响3执行
		 * 2018-12-05 16:59:18.429 -> 11 - pool-1-thread-1 ---> 3 //3使用'替代线程'
		 */
		private static void single() {
			ExecutorService excutor = Executors.newSingleThreadExecutor();
			for (int i = 0; i < 4; i++) {
				int index = i;
				excutor.execute(() -> {
					try {
						int j = (2 == index) ? 1 / 0 : 0;// 故意制造异常
						TimeUnit.SECONDS.sleep(2);
						System.out.println(SystemUtils.getAll() + " ---> " + index);
					} catch (Exception e) {
						System.out.println(SystemUtils.getAll() + " ---> " + index + " -> Exception:" + e.getMessage());
					}
				});
			}
			excutor.shutdown();
		}
		
	1.固定线程池
		/**
		 * --->复用'固定数量'线程处理一个共享的无边界队列
		 *
		 * 任何时间,最多只有 n 个线程处于活动状态. 新提交任务一直在队列中等待,直到有线程可用.
		 * 如果任何线程在执行过程中因为错误而中止,则会使用(新建线程/替代线程) 执行后续任务.
		 * 所有线程都会一直存于线程池中,直到显式的调用 shutdown() 关闭.
		 *
		 * 线程池大小最好根据系统资源进行设置: Runtime.getRuntime().availableProcessors()+1
		 * 对于CPU密集型应用,线程池大小应设置为 N+1. 对于IO密集型应用,线程池大小设置为 2N+1 (N为CPU总核数)
		 *
		 * 线程池大小为2,启动4个任务,每个任务先sleep2秒再打印, 所以先打印2个数字,再'复用线程'打印第3.4个数字
		 *
		 * 2018-12-05 16:58:02.117 -> 11 - pool-1-thread-1 ---> 0
		 * 2018-12-05 16:58:02.119 -> 12 - pool-1-thread-2 ---> 1
		 * 2018-12-05 16:58:04.118 -> 11 - pool-1-thread-1 ---> 2 -> Exception:/ by zero
		 * 2018-12-05 16:58:04.118 -> 11 - pool-1-thread-1 ---> 3
		 */
		private static void fixed() {
			// System.out.println(SystemUtils.getAll() + " -> start");
			ExecutorService executor = Executors.newFixedThreadPool(2);
			for (int i = 0; i < 4; i++) {
				int index = i;
				executor.execute(() -> {
					try {
						int j = (2 == index) ? 1 / 0 : 0;// 故意制造异常
						System.out.println(SystemUtils.getAll() + " ---> " + index);
						TimeUnit.SECONDS.sleep(index);
					} catch (Exception e) {
						System.out.println(SystemUtils.getAll() + " ---> " + index + " -> Exception:" + e.getMessage());
					}
				});
			}
			executor.shutdown();
		}

	2.缓存线程池
		/**
		 * ---> 适用于大量短生命周期的异步任务(many short-lived asynchronous task)
		 *
		 * 有闲置则使用; 无闲置则新建,并将其加入线程池中.
		 * 当线程池大小超过了任务所需,则会自动回收部分闲置线程(60秒未被使用). 因此,该线程池在长时间空闲后不会消耗任何资源
		 *
		 * 分别在 0-0-1-3, 4个时间点执行4个任务
		 * 首先,直接新建两个线程执行任务0和1. 当2和3任务执行时,0和1任务[已]完成,则会复用1任务的线程,而不用新建线程
		 *
		 * 2018-12-05 17:29:31.123 -> 11 - pool-1-thread-1 ---> 0
		 * 2018-12-05 17:29:31.125 -> 12 - pool-1-thread-2 ---> 1
		 * 2018-12-05 17:29:32.117 -> 12 - pool-1-thread-2 ---> 2
		 * 2018-12-05 17:29:34.117 -> 12 - pool-1-thread-2 ---> 3
		 */
		private static void cached() throws InterruptedException {
			ExecutorService executor = Executors.newCachedThreadPool();
			for (int i = 0; i < 4; i++) {
				int index = i;
				executor.execute(() -> System.out.println(SystemUtils.getAll() + " ---> " + index));
				TimeUnit.SECONDS.sleep(index);
			}
			executor.shutdown();
		}

	3.定时线程池
		/**
		 * ---> 比Timer更安全,功能更强大.
		 *
		 * //下次循环起始时间 = (任务消耗时间 > period) ? 上次循环[结束]时间 : (上次循环[起始]时间 + period)
		 * //1s执行1次循环!!! 但1次任务需要2s,所以第1次循环完成,第2次循环立即开始
		 * scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit);
		 *
		 * //下次循环起始时间 = (上次循环[结束]时间 + period)
		 * //1s执行1次循环!!! 第1次循环完成后,等待1s,然后再开始第2次循环
		 * scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit)
		 *  
		 * 如果有任务执行过程中抛出异常,则会跳出,不会影响下次循环.
		 *  
		 * 2018-12-05 17:53:03.826 -> 1 - main -> start
		 * 2018-12-05 17:53:03.938 -> 12 - pool-1-thread-2 ---> 0
		 * 2018-12-05 17:53:04.937 -> 11 - pool-1-thread-1 ---> 1
		 * 2018-12-05 17:53:05.937 -> 11 - pool-1-thread-1 ---> 2 -> Exception:/ by zero
		 * 2018-12-05 17:53:06.938 -> 12 - pool-1-thread-2 ---> 3
		 */
		private static void scheduled() {
			System.out.println(SystemUtils.getAll() + " -> start");
			ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
			for (int i = 0; i < 4; i++) {
				int index = i;
				executor.schedule(() -> {
					try {
						int j = (2 == index) ? 1 / 0 : 0;// 故意制造异常
						System.out.println(SystemUtils.getAll() + " ---> " + index);
					} catch (Exception e) {
						System.out.println(SystemUtils.getAll() + " ---> " + index + " -> Exception:" + e.getMessage());
					}
				}, index, TimeUnit.SECONDS);
			}
			executor.shutdown();
		}

#Spring线程池
	//jdk线程池的弊端(参考ali开发文档):
		(0).固定&单例-线程池: queue为MAX, 容易因请求队列堆积,耗费大量内存,甚至OOM
		(1).缓存&定时-线程池: max为MAX, 可能会创建非常多的线程,甚至OOM
		
		Single	(core:1, max:1, queue:LinkedBlockingQueue<Runnable>) //queue=MAX; 无界队列; core==max==1
		Fixed	(core:n, max:n, queue:LinkedBlockingQueue<Runnable>) //queue=MAX; 无界队列; core==max
		Cached	(core:0, max:MAX, queue:SynchronousQueue<Runnable>) //直接将任务提交给线程,而不保持它们
		Schedule(core:n, max:MAX, queue:DelayedWorkQueue) //定时队列

	//Queue
		ConcurrentLinkedQueue ==> 基础链表同步队列.
		LinkedBlockingQueue ==> 阻塞队列,队列容量为0时自动阻塞.
		ArrayBlockingQueue ==> 底层数组实现的有界队列,容量不足自动阻塞.
		
		DelayQueue ==> 延时队列. 根据自定义比较机制,将队列中任务进行时间排序. 常用于定时任务.
		//假如定义为升序队列,调用 take() 方法, 则可以从队列中依次取出距离当前时间间隔最短的任务.
		
		LinkedTransferQueue ==> 转移队列. 使用 transfer() 方法,实现数据的即时处理. 没有消费者,就阻塞.
		
		synchronousqueue ==> 同步队列. 容量为 0 的队列,特殊的 LinkedTransferQueue.
		//必须现有消费线程等待,才能使用的队列.
	
	0.Spring线程池
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
		// AsyncTask - 2018-12-06 17:03:33.846 -> 86 - demo-1 ==> asyncSimple - 0
		// AsyncTask - 2018-12-06 17:03:33.846 -> 87 - demo-2 ==> asyncFuture ---> 99 - 0
		// MyAsyncConfigurer - Method:com.example.task.AsyncTask.asyncSimple; Params:; Exception: asyncSimple - / by zero
		// HelloCtrl - 2018-12-06 17:03:35.847 -> 52 - http-nio-8090-exec-1 ==> java.lang.RuntimeException: asyncFuture - / by zero
		
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
		
		// ScheduledTask - 2018-12-06 17:52:03.001 -> 91 - demoscheduling-1 ==> task2--------------------------
		// ScheduledTask - 线程属性 -> 核心线程数: 2; 线程池最大数量: 6; 活动线程数: 1; 线程总数: 2; 任务总数: 75; 已完成任务数: 74; 线程处理队列长度: 0
		// AsyncTask - 2018-12-06 17:52:03.128 -> 53 - demo-2 ==> asyncFuture ---> 6 - 1
		// ScheduledTask - 2018-12-06 17:52:03.128 -> 95 - demoscheduling-2 ==> asyncFuture---Exception: java.lang.RuntimeException: asyncFuture - / by zero
		// ScheduledTask - 2018-12-06 17:52:03.128 -> 95 - demoscheduling-2 ==> task1----------------------------
		// AsyncTask - 2018-12-06 17:52:03.128 -> 52 - demo-1 ==> asyncSimple - 0
		// ScheduledTask - 2018-12-06 17:52:04.001 -> 104 - demoscheduling-3 ==> task2--------------------------
		// ScheduledTask - 线程属性 -> 核心线程数: 2; 线程池最大数量: 6; 活动线程数: 2; 线程总数: 2; 任务总数: 78; 已完成任务数: 75; 线程处理队列长度: 1
		
	2.测试结论
		(0).以上有3个定时线程 'demoscheduling-*',且线程前缀符合配置文件.
		(1).方法 asyncFuture() 是在调用 get() 方法时进行异常处理.
		(2).通过 @Autowired Executor taskExecutor; 这种方式获取线程池对象; 并且检查线程池的运行状况.
		
#execute()&&submit()
	0.两种任务
		'java5之后,任务分两类': 一类实现Runnable接口; 另一类实现Callable接口.
		(0).两者都可以被 ExecutorService 执行,但Runnable没有返回值,而Callable有返回值.
		(1).Callable 只能通过 ExecutorService.submit(Callable task) 来执行,返回表示任务等待完成的Future.
		
	1.不同区别
		(1).excute()木有返回值, 会抛出异常
		(2).submit()返回Future<?>, 不会抛出异常,除非调用Future.get()
		
	2.submit三重载
		// <T> Future<T> submit(Callable<T> task); ===> 获取结果: future.get()
		private void submitCall() throws InterruptedException, ExecutionException {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			Future<String> future = executor.submit(() -> "submitCall---success!!");
			System.out.println(SystemUtils.getAll() + future.get());
		}

		// <T> Future<T> submit(Runnable task, T result); ===> 获取结果: future.get().res
		private void submitRunRes() throws InterruptedException, ExecutionException {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			Data data = new Data();
			Future<Data> future = executor.submit(() -> data.res = "submitRunRes---success!!", data);
			System.out.println(SystemUtils.getAll() + future.get().res);
		}

		// <T> Future<T> submit(Runnable task); ===> Runnable方法在成功完成时,将会返回 null
		private void submitRun() throws InterruptedException, ExecutionException {
			ExecutorService executor = Executors.newSingleThreadExecutor();
			Future<?> future = executor.submit(() -> System.out.println(SystemUtils.getAll() + "submitRun---success!!"));
			System.out.println("isSuccess: " + (null == future.get()));
		}
		
		// 2018-12-07 20:19:40.342 -> 50 - http-nio-8090-exec-1 ==> submitCall---success!!
		// 2018-12-07 20:19:40.342 -> 50 - http-nio-8090-exec-1 ==> submitRunRes---success!!
		// 2018-12-07 20:19:40.343 -> 86 - pool-5-thread-1 ==> submitRun---success!!
		// isSuccess: true
		
	3.代码demo
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

		// future.get(); ===> 可能造成两种异常: InterruptedException, ExecutionException
		// future.get(long timeout, TimeUnit unit); ===> 加上获取结果的最大时间限制,可能造成异常: TimeoutException
		2018-12-07 17:06:22.614 -> 12 - pool-1-thread-2 ==> submit---args: 1
		2018-12-07 17:06:22.614 -> 11 - pool-1-thread-1 ==> submit---args: 0
		2018-12-07 17:06:22.616 -> 1 - main ==> Exception---java.util.concurrent.TimeoutException //异常-获取结果超时
		2018-12-07 17:06:22.614 -> 13 - pool-1-thread-3 ==> submit---args: 2
		2018-12-07 17:06:22.617 -> 1 - main ==> Exception---java.util.concurrent.ExecutionException: java.lang.ArithmeticException: by zero //异常-执行过程错误
		2018-12-07 17:06:22.617 -> 1 - main ==> submit---res: 2 //正常结果
	

	

///------------------<<<附加>>>-----------------------------------------------------------------------
#三个线程顺序执行
	0.使用'单例线程池'
	
	1.使用join方法
		private static void test00() {
			final Thread t1 = new Thread(() -> {
				try {
					System.out.println(SystemUtils.getAll() + " -> start ");
					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException ignored) {}
			}, "Thread-T1");

			final Thread t2 = new Thread(() -> {
				try {
					t1.join(1000); // 等待线程t1终止,最大等待时间1000ms
					System.out.println(SystemUtils.getAll() + " -> start ");
					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException ignored) {}
			}, "Thread-T2");

			final Thread t3 = new Thread(() -> {
				try {
					t2.join();
					System.out.println(SystemUtils.getAll() + " -> start ");
				} catch (InterruptedException ignored) {}
			}, "Thread-T3");

			// 2018-12-05 20:04:31.198 -> 11 - Thread-T1 -> start 
			// 2018-12-05 20:04:32.200 -> 12 - Thread-T2 -> start //等待1000ms
			// 2018-12-05 20:04:34.200 -> 13 - Thread-T3 -> start //等待2s
			t2.start(); t3.start(); t1.start();
		}

#join()
	//(*-*)2.如果线程被生成了,但还未被起动,调用它的join()方法是没有作用的,将直接继续向下执行!!!!
	//0.当我们调用某个线程的这个方法时,这个方法会挂起调用线程,直到被调用线程结束执行,调用线程才会继续执行.
	//1.父线程调用子线程,join会让父线程等待子线程结束之后才能继续运行.
	
		private static void test01() {
			ArrayList<Thread> threads = new ArrayList<>();
			for (int i = 0; i < 2; i++) {
				int index = i;
				threads.add(new Thread(() -> {
                    System.out.println(SystemUtils.getAll() + index + " - end");
                    TimeUnit.SECONDS.sleep(2);
                    System.out.println(SystemUtils.getAll() + index + " - end");
				}));
			}
			threads.forEach(Thread::start); //启动所有线程
			
			//当主线程main调用t1的join(n)时,main会被挂起,等待n毫秒,然后继续执行.
			threads.forEach(x.join(100));
			System.out.println(SystemUtils.getAll() + "end!");
		}

		//x.join(100); ===> main等待n毫秒,然后继续执行.
		2018-12-07 11:04:18.235 -> 11 - Thread-0 ==> 0 - start
		2018-12-07 11:04:18.237 -> 12 - Thread-1 ==> 1 - start
		2018-12-07 11:04:18.426 -> 1 - main ==> end!
		2018-12-07 11:04:20.238 -> 12 - Thread-1 ==> 1 - end
		2018-12-07 11:04:20.238 -> 11 - Thread-0 ==> 0 - end
		
		//x.join(); ===> 默认,main无限等待,直到t1执行完毕
		2018-12-07 11:11:44.922 -> 11 - Thread-0 ==> 0 - start
		2018-12-07 11:11:44.922 -> 12 - Thread-1 ==> 1 - start
		2018-12-07 11:11:46.924 -> 11 - Thread-0 ==> 0 - end
		2018-12-07 11:11:46.924 -> 12 - Thread-1 ==> 1 - end
		2018-12-07 11:11:46.924 -> 1 - main ==> end!

#区别Thread.run()和start()
	.run(): //不会新开线程;直接在调用线程中执行run()方法内容
	
	.start(): 'run()方法必须是public访问权限,返回类型void' //真正实现多线程运行!!! 
		线程的执行是随机的,不是说start了就代表马上执行! 
		而是处于就绪(可运行)状态,一旦得到cpu时间片,将自动执行run方法内的线程体. 
		new出来的线程在run方法结束,run抛出异常,jvm退出时会自动销毁.

#ThreadLocal
	(0).用于存储'线程局部变量'; 本质可看作一个Map,/其中key为 Thread.currentThread()
	(1).'一个tl对象只能存放一个线程局部变量'; 所以对于多个局部变量,需实例化多个tl对象
	(2).ThreadLocal 中的数据不会随着线程结束而回收,必须手动 remove(),防止内存泄露.
	
    // 操作系统中,线程和进程数量是有上限的, 确定线程和进程唯一性的唯一条件就是线程或进程id.
    // 操作系统在回收线程或进程的时候,并不一定杀死; 系统繁忙时,只会清空其栈内数据,然后重复使用.
    // 所以,对于存储在 ThreadLocal 中的数据,如若不 remove(),则有可能在线程 t2 获取到 t1 的数据.
	
	1.代码demo
		//通常定义为常量,用于关联线程上下文
		private static final ThreadLocal<String> local1 = new ThreadLocal<>();
		
		public static void local() {
			new Thread(() -> {
				local1.set("t1");
				System.out.println("00 - thread-1 - " + local1.get()); //00 - thread-1 - t1
				local1.remove();
			}).start();
			System.out.println("00 - main - " + local1.get()); //00 - main - null

			local1.set("m1");
			new Thread(() -> System.out.println("01 - thread-2 - " + local1.get())).start(); //01 - thread-2 - null
			System.out.println("01 - main - " + local1.get()); //01 - main - m1
		}
	
	2.hibernate中的典型应用
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
	
	3.三者关系: Thread, ThreadLocal, ThreadLocalMap
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
		
	4.总结:	
		//ThreadLocal不是用来解决: 共享对象的多线程访问问题 && 协调线程同步问题
		每个线程内部都有一个 ThreadLocal.ThreadLocalMap 类型的成员变量 threadLocals, 可以将本线程的局部变量保存到其中,多线程之间互不干扰.
		其他线程访问不到本线程的, 每个线程只能访问到自己的.
		
		ThreadLocal 本身并不存储线程局部变量, 只是提供一个在当前线程中找到局部变量的索引key.
		为每一个线程隔离一个类的实例,该实例的作用范围仅限于线程内部.

		'每个ThreadLocal对象只能放一个线程局部变量' 
		局部变量存储在 ThreadLocal.ThreadLocalMap 中, 是以 ThreadLocal对象 为key.
		所以, 一个线程欲存放多个局部变量，则需实例化多个 ThreadLocal 对象
		ThreadLocal 变量通常被 private static 修饰, 用于关联线程上下文.
		
	5.不正确的理解
		ThreadLocal为解决多线程并发访问提供了一种新的思路
		ThreadLocal的目的是为了解决多线程访问资源时的共享问题



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

			//99 ss info100  =====> 影响的只有 info.data
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


