1.利弊分析
2.4种线程池
3.4类线程底层实现

4.自定义线程池及线程的名字
5.线程池相关属性
6.线程池提交のexecute和submit区别
7.线程池之submit方法(3个)

8.Thread.run和Thread.start区别
9.多线程与final

10.Timer和ScheduledExecutorService

------------------------------------------------------------------------------------------------------------------------------
1.利弊分析
	使用new Thread()弊端:
		a. 每次new Thread新建对象性能差
		b. 缺乏统一管理,可能无限制新建线程,占用过多系统资源导致死机或oom
		c. 缺乏更多功能,如定时执行、定期执行、线程中断
	
   线程池优势：
		a. 复用已有线程,减少线程创建/销毁的开销,节省系统资源
		b. 根据系统环境,调整线程池大小,提高系统资源利用率,同时避免资源竞争,避免堵塞
		c. 提供定时执行、定期执行、单线程、并发数控制等功能
	
// 2.4种线程池
	// a).newCachedThreadPool: "可缓存线程池,适用于大量短生命周期的异步任务(many short-lived asynchronous task)"
		
		// /**
		 // * 线程池最大容量 Integer.MAX_VALUE. 初始大小为0.
		 // * <p>
		 // * 有闲置则使用; 无闲置则新建,并将其加入线程池中. 
		 // * 当线程池大小超过了任务所需,则会自动回收部分闲置线程(60秒未被使用的)
		 // * 因此,该线程池在长时间空闲后不会消耗任何资源
		 // */
		// public static void cached() {
			// System.out.println("start -> " + Thread.currentThread().getName() + " -> " + CommUtils.getNow());

			// ExecutorService executor = Executors.newCachedThreadPool();
			// for (int i = 0; i < 3; i++) {
				// int index = i;

				// // 启动3个任务,每个任务执行完毕,休息 index*1 ms(第一个任务完毕休息0ms)
				// // 当执行1任务时0任务[未]完成，则会新建线程去执行1任务
				// // 当执行2任务时1任务[已]完成，则会复用1任务的线程，而不用新建线程
				// //
				// // start -> main -> 2018-06-05 16:31:03
				// // 0 -> pool-1-thread-1 -> 2018-06-05 16:31:03
				// // 1 -> pool-1-thread-2 -> 2018-06-05 16:31:03
				// // 2 -> pool-1-thread-2 -> 2018-06-05 16:31:04
				// executor.execute(() -> {
					// System.out.println(index + " -> " + Thread.currentThread().getName() + " -> " + CommUtils.getNow());
				// });

				// try {
					// TimeUnit.SECONDS.sleep(index * 1);// 第一次循环休息0ms
				// } catch (InterruptedException e) {
					// e.printStackTrace();
				// }
			// }
			// executor.shutdown();
		// }
		
	// b).newFixedThreadPool: "复用 固定数量 线程处理 一个共享的无边界队列"
			
		// /**
		 // * 任何时间点,最多只有 n 个线程处于活动状态. 新提交任务一直在队列中等待,直到有线程可用. 如果任何线程在执行过程中因为错误而中止,则会使用
		 // * (新建线程/替代线程) 执行后续任务. 所有线程都会一直存于线程池中,直到显式的调用 ExecutorService.shutdown() 关闭.
		 // * <p>
		 // * 线程池大小最好根据系统资源进行设置: Runtime.getRuntime().availableProcessors()+1
		 // * <p>
		 // * 对于CPU密集型应用,线程池大小应设置为 N+1. 对于IO密集型应用,线程池大小设置为 2N+1 (N为CPU总核数)
		 // */
		// public static void fixed() {
			// System.out.println("start -> " + SystemUtils.getAll());

			// int nThreads = Runtime.getRuntime().availableProcessors() + 1;// 5
			// ExecutorService executor = Executors.newFixedThreadPool(2);
			// for (int i = 0; i < 3; i++) {
				// int index = i;

				// // 启动3个任务,但线程池大小为2,且每个任务先sleep3秒后再输出 , 所以先打印2个数字,再复用线程打印第3个数字
				// //
				// // start -> main -> 2018-06-05 16:45:12
				// // 1 -> pool-1-thread-2 -> 2018-06-05 16:45:15
				// // 0 -> pool-1-thread-1 -> 2018-06-05 16:45:15
				// // 2 -> pool-1-thread-2 -> 2018-06-05 16:45:18
				// executor.execute(() -> {
					// try {
						// Thread.sleep(3 * 1000);
					// } catch (InterruptedException e) {
						// e.printStackTrace();
					// }
					
					// System.out.println(index + " -> " + SystemUtils.getAll());
				// });
			// }
			// executor.shutdown();
		// }
	
	// c).newSingleThreadExecutor: "使用 单个线程 处理一个无边界的【队列-FIFO】,按顺序执行三个线程"
		
		// /**
		 // * 有且仅有一个线程处于活动状态, 保证所有任务按指定顺序(FIFO,LIFO,优先级)执行.
		 // * <p>
		 // * 当线程执行过程中出现异常, 则会创建一个新的线程替换它执行后续任务.
		 // * <p>
		 // * 和 newFixedThreadPool(1) 的区别在于 --> 如果线程因错误而中止, 后者是无法使用替代线程的, 后续任务也将终止.
		 // */
		// public static void singled() {
			// ExecutorService excutor = Executors.newSingleThreadExecutor();

			// // 2018-06-05 16:51:52.866 -> start 1
			// // 2018-06-05 16:51:56.866 ---> end 1
			// // 2018-06-05 16:51:56.866 -> start 2 #执行异常,没有end2，但不影响3执行
			// // 2018-06-05 16:51:56.867 -> start 3
			// // 2018-06-05 16:51:58.868 ---> end 3
			// for (int i = 1; i < 4; i++) {
				// int count = i;
				// excutor.execute(() -> {
					// try {
						// System.out.println(CommUtils.getNow(true) + " -> start " + count);
						// int j = (2 == count) ? 1 / 0 : 0;// 故意制造异常
						// TimeUnit.SECONDS.sleep(5 - count);
						// System.out.println(CommUtils.getNow(true) + " ---> end " + count);
					// } catch (InterruptedException e) {
						// // e.printStackTrace();
					// }
				// });
			// }
			// excutor.shutdown();
		// }
		
		// "三个线程顺序执行的另一种实现方案"
		// public static void main(String[] args) throws InterruptedException {
			// final Thread t1 = new Thread(() -> {
				// System.out.println(CommUtils.getNow(true) + " - " + CommUtils.getThreadName() + " -> start ");
			// }, "Thread-T1");

			// final Thread t2 = new Thread(() -> {
				// try {
					// // 主线程生成并起动子线程,在子线程里进行耗时操作,当主线程处理完逻辑后,
					// // 需要用到子线程的处理结果,这个时候就要用到join();方法了
					// t1.join(); // 主线程等待子线程终止
					// System.out.println(CommUtils.getNow(true) + " - " + CommUtils.getThreadName() + " -> start ");
				// } catch (InterruptedException e) {
					// e.printStackTrace();
				// }
			// }, "Thread-T2");

			// final Thread t3 = new Thread(() -> {
				// try {
					// t2.join();
					// System.out.println(CommUtils.getNow(true) + " - " + CommUtils.getThreadName() + " -> start ");
				// } catch (InterruptedException e) {
					// e.printStackTrace();
				// }
			// }, "Thread-T3");

			// // 2018-06-01 20:34:25.906 - Thread-T1 -> start
			// // 2018-06-01 20:34:25.907 - Thread-T2 -> start
			// // 2018-06-01 20:34:25.907 - Thread-T3 -> start
			// t2.start();
			// t3.start();
			// t1.start();
		// }
	
	// d).newScheduledThreadPool: "比 Timer 更安全,功能更强大"
	
		// /**
		 // * 定长为3的线程池,执行延时任务
		 // * <p>
		 // * 如果任务执行过程中抛出异常,则会跳出,不会影响下次循环.
		 // */
		// public static void delay() {
			// System.out.println("start -> " + SystemUtils.getAll());

			// ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
			// for (int i = 1; i < 4; i++) {
				// int index = i;

				// // 相对于 start 分别延迟 index 秒执行
				// //
				// // start -> main -> 2018-06-05 17:52:40.791
				// // delay 1 seconds for start -> pool-1-thread-1 -> 2018-06-05 17:52:41.841
				// // delay 3 seconds for start -> pool-1-thread-2 -> 2018-06-05 17:52:43.842
				// executor.schedule(() -> {
					// int j = (2 == index) ? 1 / 0 : 0;// 故意制造异常
					// System.out.println("delay " + index + " seconds for start" + " -> " + Thread.currentThread().getName()
							// + " -> " + CommUtils.getNow(true));
				// }, index, TimeUnit.SECONDS);
			// }
			// executor.shutdown();
		// }
		
		// // 下次循环起始时间 = (任务消耗时间 > period) ? 上次循环[结束]时间 : (上次循环[起始]时间 + period)
		// // 1s执行1次循环!!! 但1次任务需要2s,所以第1次循环完成,第2次循环立即开始
		// scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit);
		
		// // 下次循环起始时间 = (上次循环[结束]时间 + period)
		// // 1s执行1次循环!!! 第1次循环完成后,等待1s,然后再开始第2次循环
		// scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit)
		
// 3.4类线程底层实现

		// /**
		 // * @param corePoolSize
		 // *            核心池
		 // * @param maximumPoolSize
		 // *            最大池 ---> TreadPoolExecutor根据这俩值自动调整池大小。新任务execute()提交时:
		 // *            如果运行线程小于corePoolSize, 则创建新线程来处理请求, 即使其他辅助线程是空闲的。
		 // *            如果运行的线程多于corePoolSize, 而少于maximumPoolSize, 则仅当队列满时才创建新的线程。
		 // *            如果设置的corePoolSize和maximumPoolSize相同, 则创建了固定大小的线程池。
		 // *            如果maximumPoolSize设置为基本的无界值(如Integer.MAX_VALUE), 则允许线程池适应任意数量的并发任务。
		 // * @param keepAliveTime
		 // *            保持活动时间 ---> 如果池中有多于corePoolSize的线程,则这些线程在空闲时间超过keepAliveTime后会被终止。
		 // * @param unit
		 // *            时间单位 ---> NANOSECONDS:纳秒; MILLISECONDS:毫秒;
		 // * @param workQueue
		 // *            队列 ---> 所有BlockingQueue都可用于传输和保持提交的任务。可以使用此队列与池大小进行交互：
		 // *            如果运行的线程少于corePoolSize, 则Executor始终首选添加新的线程,而不进行排队。
		 // *            如果运行的线程等于或多于corePoolSize, 则Executor始终首选将请求加入队列, 而不添加新的线程。
		 // *            如果无法将请求加入队列, 则创建新的线程, 除非创建此线程超出maximumPoolSize, 在这种情况下,
		 // *            任务将被拒绝(抛出RejectedExecutionException)。
		 // */
		// public static ExecutorService newCachedThreadPool() {
			// return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
										  // 60L, TimeUnit.SECONDS,
										  // new SynchronousQueue<Runnable>());		"直接提交"
		// }
		
		// public static ExecutorService newFixedThreadPool(int nThreads) {
			// return new ThreadPoolExecutor(nThreads, nThreads,
										  // 0L, TimeUnit.MILLISECONDS,
										  // new LinkedBlockingQueue<Runnable>());		"无界队列"
		// }
		
		// public static ExecutorService newSingleThreadExecutor() {
			// return new FinalizableDelegatedExecutorService
				// (new ThreadPoolExecutor(1, 1,
										  // 0L, TimeUnit.MILLISECONDS,
										  // new LinkedBlockingQueue<Runnable>()));	"无界队列"
		// }

		// public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
			// return new ThreadPoolExecutor(corePoolSize, Integer.MAX_VALUE,
										  // 0L, NANOSECONDS,
										  // new DelayedWorkQueue());					""
		// }	

	// "排队有三种通用策略"
		// 直接提交 ---> 工作队列的默认选项是 synchronousQueue,它将任务直接提交给线程而不保持它们. 
		// 在不存在可用于立即运行任务的线程,则试图把任务加入队列将失败，因此会构造一个新的线程.
		// 此策略可以避免在处理可能具有内部依赖性的请求集时出现锁. 
		// 直接提交通常要求无界maximumPoolSizes, 以避免拒绝新提交的任务.
		// 当命令以超过队列所能处理的平均数连续到达时，此策略允许无界线程具有增加的可能性.
		
		// 无界队列 ---> 使用无界队列(如: 不具有预定义容量的LinkedBlockingQueue),
		// 将导致所有corePoolSize线程都在忙时, 新任务在队列中等待.	
		// 这样, 创建的线程就不会超过corePoolSize (因此,maximumPoolSize的值也就无效了).
		
		// 有界队列 ---> 当 maximumPoolSizes 为有限值时, 有界队列(如: ArrayBlockingQueue)有助于防止资源耗尽, 
		// 但是可能较难调整和控制.	队列大小和最大池大小可能需要相互折衷: 
		// 使用大型队列和小型池可以最大限度的降低CPU使用率、操作系统资源和上下文切换开销,但是可能导致人工降低吞吐量.
		// 如果任务频繁阻塞, 则系统可能为超过您许可的更多线程安排时间,
		// 使用小型队列通常要求较大的池大小, CPU使用率较高, 但是可能遇到不可接受的调度开销, 这样可会降低吞吐量.
		
// 4.自定义线程池及线程的名字

		// public static void selfName() {
			// ScheduledExecutorService executor = Executors.newScheduledThreadPool(3,
					// new SelfThreadFactory("SelfPool", "SelfThread"));

			// // SelfPool-1-SelfThread-1 -> 2018-06-05 21:00:26.015
			// // SelfPool-1-SelfThread-1 -> 2018-06-05 21:00:27.017
			// // SelfPool-1-SelfThread-2 -> 2018-06-05 21:00:28.018
			// executor.scheduleWithFixedDelay(() -> {
				// System.out.println(SystemUtils.getAll());
			// }, 1, 1, TimeUnit.SECONDS);
		// }

		// //自定义ThreadFactory类
		// static class SelfThreadFactory implements ThreadFactory {
			// private static final AtomicInteger poolNumber = new AtomicInteger(1);
			// private final ThreadGroup group;
			// private final AtomicInteger threadNumber = new AtomicInteger(1);
			// private final String namePrefix;

			// /**
			 // * @param poolName   线程池的名字
			 // * @param threadName 线程名字
			 // */
			// SelfThreadFactory(String poolName, String threadName) {
				// SecurityManager s = System.getSecurityManager();
				// group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
				// namePrefix = poolName + "-" + poolNumber.getAndIncrement() + "-" + threadName + "-";
			// }

			// public Thread newThread(Runnable r) {
				// Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
				// if (t.isDaemon())
					// t.setDaemon(false);
				// if (t.getPriority() != Thread.NORM_PRIORITY)
					// t.setPriority(Thread.NORM_PRIORITY);
				// return t;
			// }
		// }

		
		
		
		
// 5.线程池相关属性
		// /**
		 // * @线程池相关属性: corePoolSize(线程池启动后,池中线程的最小数量); activeCount(活跃数);
		 // * 		taskCount(总执行任务数); completedTaskCount(已完成任务数)
		 // * 
		 // * @corePoolSize - 线程池启动后,池中线程的最小数量.
		 // * @注意: 刚创建线程池时,线程并不会立即启动,而是等到有任务提交时才会启动, 除非显示调用
		 // *      ThreadPoolExecutor的prestartCoreThread() / prestartAllCoreThreads()
		 // *      事先启动核心线程
		 // *
		 // * @throws InterruptedException
		 // */
		// private static void getActiveCount() throws InterruptedException {
			// ExecutorService service = Executors.newCachedThreadPool();
			// Runnable command = () -> {
				// try {
					// TimeUnit.SECONDS.sleep(2);
				// } catch (InterruptedException e) {
					// e.printStackTrace();
				// }
			// };

			// /* 第一轮 放入2个任务 */
			// service.execute(command);
			// service.execute(command);

			// TimeUnit.MILLISECONDS.sleep(10);// 延迟一下,因为2个任务放入线程池需要时间
			// System.out.println("第一次执行ju -> " + getServiceInfo(service));

			// TimeUnit.SECONDS.sleep(2);// 等待工作线程执行完毕
			// System.out.println("第一次结束 -> " + getServiceInfo(service));

			// /* 第二轮 放入1个任务 */
			// service.execute(command);

			// TimeUnit.MILLISECONDS.sleep(10);
			// System.out.println("第二次执行 -> " + getServiceInfo(service));

			// TimeUnit.SECONDS.sleep(2);
			// System.out.println("第二次结束 -> " + getServiceInfo(service));

			// // 第一次执行 -> 2018-06-06 16:52:21.061
			// // - corePoolSize: 0; activeCount: 2; taskCount: 2; completedTaskCount: 0
			// // 第一次结束 -> 2018-06-06 16:52:23.062
			// // - corePoolSize: 0; activeCount: 0; taskCount: 2; completedTaskCount: 2
			// // 第二次执行 -> 2018-06-06 16:52:23.072
			// // - corePoolSize: 0; activeCount: 1; taskCount: 3; completedTaskCount: 2
			// // 第二次结束 -> 2018-06-06 16:52:25.072
			// // - corePoolSize: 0; activeCount: 0; taskCount: 3; completedTaskCount: 3
			// service.shutdown();
		// }
		
		// private static String getServiceInfo(ExecutorService service) {
			// ThreadPoolExecutor pool = (ThreadPoolExecutor) service;
			// int corePoolSize = pool.getCorePoolSize();
			// int activeCount = pool.getActiveCount();
			// long taskCount = pool.getTaskCount();
			// long completedTaskCount = pool.getCompletedTaskCount();
			// String res = CommUtils.getNow(true) + " - corePoolSize: " + corePoolSize + "; activeCount: " + activeCount
					// + "; taskCount: " + taskCount + "; completedTaskCount: " + completedTaskCount;
			// return res;
		// }

// 6.线程池提交のexecute和submit区别
	// 1).excute()木有返回值, 后者会返回Future<?>
	// 2).excute()会抛出异常, 后者不会抛出异常,除非调用Future.get()

	// 'java5之后,任务分两类': 一类实现Runnable接口; 另一类实现Callable接口.
	// a).两者都可以被ExecutorService执行,但Runnable没有返回值,而Callable有返回值.
	// b).Callable的 call() 只能通过 ExecutorService 的 submit(Callable task) 来执行,返回表示任务等待完成的Future.
	
		// // submit---args: 0 -> 2018-06-06 17:05:17.473
		// // submit---args: 1 -> 2018-06-06 17:05:17.517
		// // submit---args: 2 -> 2018-06-06 17:05:17.517
		// //
		// // Exception---future.get(): java.util.concurrent.TimeoutException			#异常---获取结果超时
		// // Exception---submit: 1 -> / by zero -> 2018-06-06 17:05:18.517			#异常---执行过程错误
		// // submit---res: 2 -> 2018-06-06 17:05:17.517 -> 2018-06-06 17:05:18.517	#正常执行
		// public static void submit() {
			// ExecutorService executor = Executors.newCachedThreadPool();
			// List<Future<String>> resultList = new ArrayList<Future<String>>();

			// for (int i = 0; i < 3; i++) {
				// System.out.println("submit---args: " + i + " -> " + CommUtils.getNow(true));

				// int index = i;
				// Future<String> future = executor.submit(() -> {// 将Callable类型任务的结果保存在Future变量
					// String res = "";
					// try {
						// res = "submit---res: " + index + " -> " + CommUtils.getNow(true);
						// TimeUnit.SECONDS.sleep(1);
						// int j = (1 == index) ? 1 / 0 : 0;// 制造异常
					// } catch (Exception e) {
						// res = "Exception---submit: " + index + " -> " + e.getMessage();
					// }
					// return res;
				// });
				// resultList.add(future);
			// }

			// for (Future<String> future : resultList) {
				// try {
					// // String res = future.get();// 等待至任务完成
					// String res = future.get(998, TimeUnit.MILLISECONDS);// 等待最大时长(可能未执行完成,而抛异常)

					// System.out.println(res + " -> " + CommUtils.getNow(true)); // 打印各个任务的执行结果(包括异常)
				// } catch (InterruptedException | ExecutionException | TimeoutException e) {
					// System.out.println("Exception---future.get(): " + e);// 等待超时异常
				// } finally {
					// executor.shutdown();// 顺序关闭 -> 执行提交前的任务,但不接受新的任务
				// }
			// }
		// }
	
	--------------------------------------------------------------------
// 7.线程池之submit方法(3个)
// 　　//(1). <T> Future<T> submit(Callable<T> task);
	// private static String submitCall() throws InterruptedException, ExecutionException {
		// Future<String> submit = CommUtils.getExcutor().submit(() -> {
			// return "now: " + CommUtils.getNow(true);
		// });

		// //如果想立即阻塞任务的等待,则可以使用 result = exec.submit(aCallable).get(); 形式的构造.(不太明白??????)
		
		// //提交一个有返回值的任务用于执行,返回一个表示任务的未决结果的 Future. 该 Future 的 get 方法在成功完成时将会返回该任务的结果
		// return submit.get();
	// }

	// //(2). <T> Future<T> submit(Runnable task, T result);
	// private static String submitRun4Res() throws InterruptedException, ExecutionException {
		// Data data = new TestDemo().new Data();
		// Future<Data> submit = CommUtils.getExcutor().submit(() -> {
			// data.setData("now: " + CommUtils.getNow(true));
		// }, data);

		// // return data.getData();// 直接调用 data.getData() 返回null

		// // submit(*,*)第二个参数data用于接收返回值: Data data = submit.get();
		// return submit.get().getData();
	// }
	
	// //(3). <T> Future<T> submit(Runnable task);
	// private static boolean submitRun() throws InterruptedException, ExecutionException {
		// Future<?> submit = CommUtils.getExcutor().submit(() -> {
			// System.out.println("now: " + CommUtils.getNow(true));
		// });

		// // Runnable方法在成功完成时,将会返回 null
		// return null == submit.get();
	// }

	--------------------------------------------------------------------
// 8.Thread.run和Thread.start区别 
	// new Thread(){}.run(): //不会新开线程;继续在调用线程中执行run()方法内容
	
	// new Thread(){}.start(): 'run()方法必须是public访问权限,返回类型void' //真正实现多线程运行!!! 
		// 线程的执行是随机的,不是说start了就代表马上执行! 
		// 而是处于就绪(可运行)状态,一旦得到cpu时间片,将自动执行run方法内的线程体. 
		// new出来的线程在run方法结束、run抛出异常、jvm退出时会自动销毁.
	
9.多线程与final
	final+基本变量	->	通常用作常量; 像圆周率
	final+引用变量	->	保证不会再指向别的引用, '但引用里的值可能变动'
	final+类		->	该类不能作为父类被继承
	final+方法		->	该方法不能被重写
	
	public static void main(String[] args) throws JSONException {
		final int i1 = 99;
		final String s1 = "s";
		final Person p1 = new Person();
		p1.setAge(18);
		
		dosth(i1, s1, p1);
		System.out.println(i1 + " " + s1 + " " + p1.getAge());// 99 s 28 --> 为什么还是s; age却变成28???

		CommUtils.getExcutor().execute(() -> {
			// i1 = i1 + 5;// 报错
			// s1 = s1 + i1;// 报错
			// p1 = new Person();// 报错--> 引用不可变,但数据可变

			try {
				TimeUnit.SECONDS.sleep(1);
				dosth(i1, s1, p1);
				System.out.println(i1 + " " + s1 + " " + p1.getAge());// 99 s 28 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
	}
	
	// 只是传入了一份"拷贝", 不会改变原有的值
	private static void dosth(int i, String s, Person person) {
		i = i++; // i1拷贝变成了100
		s = s + i; // s1拷贝指向了"s100"
		person.setAge(28); // p1拷贝还是指向p1所对应的对象,然后改变其age属性
	}
	
	0为什么内部线程中引用外部对象要加final修饰符呢???
		因为,被内部线程引用的外部对象受到外部线程作用域的制约,有其特定的生命周期.
	'当外部对象在外部线程中生命周期已经结束, 而内部线程中还在持续使用, 怎样解决问题?'
	这个时候就需要在外部变量前添加final修饰符, 
	其实内部线程使用的这个变量就是外部变量的一个'复制品', 即使外部变量生命周期已经结束,内部复制品依然可用.
	
10.Timer和ScheduledExecutorService
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
	
	'需要注意的是:'
		'1. newSingleThreadScheduledExecutor(); 等效于 newScheduledThreadPool(1);'
		//单线程,串行执行; 但单任务抛出异常,只影响自身,不影响其他
	
		'2. 对于抛出异常的任务,若想在抛出异常后,还可以继续循环执行 -> try-catch包裹!'
		
	'取消单个/整体'
		Task_A.cancel(); 
		timer.cancel();
	
		ScheduledFuture<?> future = scheduled.scheduleWithFixedDelay(task1, 1, 1, TimeUnit.SECONDS);
		future.cancel(true); //单个
		pool.shutdown();//整体
	
11.多线程下载
	服务器 CPU 分配给每条线程的时间片相同 = 服务器带宽均分给每条线程, 所以客户端开启的线程越多, 就能抢占更多资源

	//1.确定每条线程下载多少数据
	
	
	
	
// #ThreadLocal - //线程容器; 绑定一个 Object 内容,只要线程不变,可以随时取出.
	// 0.hibernate中的典型应用
		// private static final ThreadLocal threadSession = new ThreadLocal();
		 
		// public static Session getSession() throws InfrastructureException {
			// Session s = (Session) threadSession.get();
			// try {
				// if (s == null) {
					// s = getSessionFactory().openSession();
					// threadSession.set(s);
				// }
			// } catch (HibernateException ex) {
				// throw new InfrastructureException(ex);
			// }
			// return s;
		// }

	// 1.三者关系: Thread, ThreadLocal, ThreadLocalMap 	
		// // Thread 中有个 ThreadLocal.ThreadLocalMap 类型的成员变量 threadLocals
		// public class Thread implements Runnable {
			// ThreadLocal.ThreadLocalMap threadLocals = null;
		// }
		
		// // ThreadLocalMap 是 ThreadLocal 的内部类. 它是一个Map, 它的Key是ThreadLocal类型对象!!
		// public class ThreadLocal<T> {
			
			// public void set(T value) {
				// Thread t = Thread.currentThread();
				// ThreadLocalMap map = t.threadLocals; // 获取当前线程的 threadLocals 变量
				// if (map != null) {
					// map.set(this, value); // key -> ThreadLocal对象自身; value -> 局部变量
				// } else {
					// t.threadLocals = new ThreadLocalMap(this, value);
				// }
			// }
			
			// public T get() {
				// Thread t = Thread.currentThread();
				// ThreadLocalMap map = t.threadLocals; //获取当前线程的 threadLocals 变量
				// if (map != null) {
					// ThreadLocalMap.Entry e = map.getEntry(this);
					// if (e != null) {
						// @SuppressWarnings("unchecked")
						// T result = (T)e.value;
						// return result;
					// }
				// }
				// return null;
			// }
			
			// static class ThreadLocalMap /* <ThreadLocal<?>, Object> //自己加的,便于理解 */ { 
				// //...
			// }
		// }
		
	// 2.线程所需变量の存储	
		// ThreadLocal.set(v1) 是将 v1 保存到当前线程的 ThreadLocal.ThreadLocalMap 类型变量 threadLocals 中,
		// 其中key为 ThreadLocal 对象自身, value 为线程局部变量 v1
		
	// 3.总结:	//ThreadLocal不是用来解决: 共享对象的多线程访问问题 && 协调线程同步问题
		// 每个线程内部都有一个 ThreadLocal.ThreadLocalMap 类型的成员变量 threadLocals, 可以将本线程的局部变量保存到其中,多线程之间互不干扰.
		// 其他线程访问不到本线程的, 每个线程只能访问到自己的.
		
		// ThreadLocal 本身并不存储线程局部变量, 只是提供一个在当前线程中找到局部变量的索引key.
		// 为每一个线程隔离一个类的实例,该实例的作用范围仅限于线程内部.

		// '每个ThreadLocal对象只能放一个线程局部变量' 
		// 局部变量存储在 ThreadLocal.ThreadLocalMap 中, 是以 ThreadLocal对象 为key.
		// 所以, 一个线程欲存放多个局部变量，则需实例化多个 ThreadLocal 对象
		// ThreadLocal 变量通常被 private static 修饰, 用于关联线程上下文.
		
	// 4.不正确的理解
		// ThreadLocal为解决多线程并发访问提供了一种新的思路
		// ThreadLocal的目的是为了解决多线程访问资源时的共享问题
		
	// //检测代码
		// private static ThreadLocal<String> local1 = new ThreadLocal<>();
		// private static ThreadLocal<String> local2 = new ThreadLocal<>();

		// public static void main(String[] args) throws InterruptedException {
			// local1.set("m1");
			// local2.set("m2");

			// new Thread(() -> {
				// local1.set("t1");
				// System.out.println("111 - " + local1.get());
			// }).start();

			// new Thread(() -> System.out.println("222 - " + local1.get())).start();

			// new Thread(() -> {
				// local2.set("t3");
				// System.out.println("333 - " + local2.get());
			// }).start();

			// System.out.println("001 - " + local1.get());
			// System.out.println("002 - " + local2.get());
			
			// //输出结果
			// 111 - t1	222 - null	001 - m1	002 - m2
		// }
	
	
	
	
	
	
	
	