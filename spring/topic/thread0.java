1.���׷���
2.4���̳߳�
3.4���̵߳ײ�ʵ��

4.�Զ����̳߳ؼ��̵߳�����
5.�̳߳��������
6.�̳߳��ύ��execute��submit����
7.�̳߳�֮submit����(3��)

8.Thread.run��Thread.start����
9.���߳���final

10.Timer��ScheduledExecutorService

------------------------------------------------------------------------------------------------------------------------------
1.���׷���
	ʹ��new Thread()�׶�:
		a. ÿ��new Thread�½��������ܲ�
		b. ȱ��ͳһ����,�����������½��߳�,ռ�ù���ϵͳ��Դ����������oom
		c. ȱ�����๦��,�綨ʱִ�С�����ִ�С��߳��ж�
	
   �̳߳����ƣ�
		a. ���������߳�,�����̴߳���/���ٵĿ���,��ʡϵͳ��Դ
		b. ����ϵͳ����,�����̳߳ش�С,���ϵͳ��Դ������,ͬʱ������Դ����,�������
		c. �ṩ��ʱִ�С�����ִ�С����̡߳����������Ƶȹ���
	
// 2.4���̳߳�
	// a).newCachedThreadPool: "�ɻ����̳߳�,�����ڴ������������ڵ��첽����(many short-lived asynchronous task)"
		
		// /**
		 // * �̳߳�������� Integer.MAX_VALUE. ��ʼ��СΪ0.
		 // * <p>
		 // * ��������ʹ��; ���������½�,����������̳߳���. 
		 // * ���̳߳ش�С��������������,����Զ����ղ��������߳�(60��δ��ʹ�õ�)
		 // * ���,���̳߳��ڳ�ʱ����к󲻻������κ���Դ
		 // */
		// public static void cached() {
			// System.out.println("start -> " + Thread.currentThread().getName() + " -> " + CommUtils.getNow());

			// ExecutorService executor = Executors.newCachedThreadPool();
			// for (int i = 0; i < 3; i++) {
				// int index = i;

				// // ����3������,ÿ������ִ�����,��Ϣ index*1 ms(��һ�����������Ϣ0ms)
				// // ��ִ��1����ʱ0����[δ]��ɣ�����½��߳�ȥִ��1����
				// // ��ִ��2����ʱ1����[��]��ɣ���Ḵ��1������̣߳��������½��߳�
				// //
				// // start -> main -> 2018-06-05 16:31:03
				// // 0 -> pool-1-thread-1 -> 2018-06-05 16:31:03
				// // 1 -> pool-1-thread-2 -> 2018-06-05 16:31:03
				// // 2 -> pool-1-thread-2 -> 2018-06-05 16:31:04
				// executor.execute(() -> {
					// System.out.println(index + " -> " + Thread.currentThread().getName() + " -> " + CommUtils.getNow());
				// });

				// try {
					// TimeUnit.SECONDS.sleep(index * 1);// ��һ��ѭ����Ϣ0ms
				// } catch (InterruptedException e) {
					// e.printStackTrace();
				// }
			// }
			// executor.shutdown();
		// }
		
	// b).newFixedThreadPool: "���� �̶����� �̴߳��� һ��������ޱ߽����"
			
		// /**
		 // * �κ�ʱ���,���ֻ�� n ���̴߳��ڻ״̬. ���ύ����һֱ�ڶ����еȴ�,ֱ�����߳̿���. ����κ��߳���ִ�й�������Ϊ�������ֹ,���ʹ��
		 // * (�½��߳�/����߳�) ִ�к�������. �����̶߳���һֱ�����̳߳���,ֱ����ʽ�ĵ��� ExecutorService.shutdown() �ر�.
		 // * <p>
		 // * �̳߳ش�С��ø���ϵͳ��Դ��������: Runtime.getRuntime().availableProcessors()+1
		 // * <p>
		 // * ����CPU�ܼ���Ӧ��,�̳߳ش�СӦ����Ϊ N+1. ����IO�ܼ���Ӧ��,�̳߳ش�С����Ϊ 2N+1 (NΪCPU�ܺ���)
		 // */
		// public static void fixed() {
			// System.out.println("start -> " + SystemUtils.getAll());

			// int nThreads = Runtime.getRuntime().availableProcessors() + 1;// 5
			// ExecutorService executor = Executors.newFixedThreadPool(2);
			// for (int i = 0; i < 3; i++) {
				// int index = i;

				// // ����3������,���̳߳ش�СΪ2,��ÿ��������sleep3�������� , �����ȴ�ӡ2������,�ٸ����̴߳�ӡ��3������
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
	
	// c).newSingleThreadExecutor: "ʹ�� �����߳� ����һ���ޱ߽�ġ�����-FIFO��,��˳��ִ�������߳�"
		
		// /**
		 // * ���ҽ���һ���̴߳��ڻ״̬, ��֤��������ָ��˳��(FIFO,LIFO,���ȼ�)ִ��.
		 // * <p>
		 // * ���߳�ִ�й����г����쳣, ��ᴴ��һ���µ��߳��滻��ִ�к�������.
		 // * <p>
		 // * �� newFixedThreadPool(1) ���������� --> ����߳���������ֹ, �������޷�ʹ������̵߳�, ��������Ҳ����ֹ.
		 // */
		// public static void singled() {
			// ExecutorService excutor = Executors.newSingleThreadExecutor();

			// // 2018-06-05 16:51:52.866 -> start 1
			// // 2018-06-05 16:51:56.866 ---> end 1
			// // 2018-06-05 16:51:56.866 -> start 2 #ִ���쳣,û��end2������Ӱ��3ִ��
			// // 2018-06-05 16:51:56.867 -> start 3
			// // 2018-06-05 16:51:58.868 ---> end 3
			// for (int i = 1; i < 4; i++) {
				// int count = i;
				// excutor.execute(() -> {
					// try {
						// System.out.println(CommUtils.getNow(true) + " -> start " + count);
						// int j = (2 == count) ? 1 / 0 : 0;// ���������쳣
						// TimeUnit.SECONDS.sleep(5 - count);
						// System.out.println(CommUtils.getNow(true) + " ---> end " + count);
					// } catch (InterruptedException e) {
						// // e.printStackTrace();
					// }
				// });
			// }
			// excutor.shutdown();
		// }
		
		// "�����߳�˳��ִ�е���һ��ʵ�ַ���"
		// public static void main(String[] args) throws InterruptedException {
			// final Thread t1 = new Thread(() -> {
				// System.out.println(CommUtils.getNow(true) + " - " + CommUtils.getThreadName() + " -> start ");
			// }, "Thread-T1");

			// final Thread t2 = new Thread(() -> {
				// try {
					// // ���߳����ɲ������߳�,�����߳�����к�ʱ����,�����̴߳������߼���,
					// // ��Ҫ�õ����̵߳Ĵ�����,���ʱ���Ҫ�õ�join();������
					// t1.join(); // ���̵߳ȴ����߳���ֹ
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
	
	// d).newScheduledThreadPool: "�� Timer ����ȫ,���ܸ�ǿ��"
	
		// /**
		 // * ����Ϊ3���̳߳�,ִ����ʱ����
		 // * <p>
		 // * �������ִ�й������׳��쳣,�������,����Ӱ���´�ѭ��.
		 // */
		// public static void delay() {
			// System.out.println("start -> " + SystemUtils.getAll());

			// ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
			// for (int i = 1; i < 4; i++) {
				// int index = i;

				// // ����� start �ֱ��ӳ� index ��ִ��
				// //
				// // start -> main -> 2018-06-05 17:52:40.791
				// // delay 1 seconds for start -> pool-1-thread-1 -> 2018-06-05 17:52:41.841
				// // delay 3 seconds for start -> pool-1-thread-2 -> 2018-06-05 17:52:43.842
				// executor.schedule(() -> {
					// int j = (2 == index) ? 1 / 0 : 0;// ���������쳣
					// System.out.println("delay " + index + " seconds for start" + " -> " + Thread.currentThread().getName()
							// + " -> " + CommUtils.getNow(true));
				// }, index, TimeUnit.SECONDS);
			// }
			// executor.shutdown();
		// }
		
		// // �´�ѭ����ʼʱ�� = (��������ʱ�� > period) ? �ϴ�ѭ��[����]ʱ�� : (�ϴ�ѭ��[��ʼ]ʱ�� + period)
		// // 1sִ��1��ѭ��!!! ��1��������Ҫ2s,���Ե�1��ѭ�����,��2��ѭ��������ʼ
		// scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit);
		
		// // �´�ѭ����ʼʱ�� = (�ϴ�ѭ��[����]ʱ�� + period)
		// // 1sִ��1��ѭ��!!! ��1��ѭ����ɺ�,�ȴ�1s,Ȼ���ٿ�ʼ��2��ѭ��
		// scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit)
		
// 3.4���̵߳ײ�ʵ��

		// /**
		 // * @param corePoolSize
		 // *            ���ĳ�
		 // * @param maximumPoolSize
		 // *            ���� ---> TreadPoolExecutor��������ֵ�Զ������ش�С��������execute()�ύʱ:
		 // *            ��������߳�С��corePoolSize, �򴴽����߳�����������, ��ʹ���������߳��ǿ��еġ�
		 // *            ������е��̶߳���corePoolSize, ������maximumPoolSize, �����������ʱ�Ŵ����µ��̡߳�
		 // *            ������õ�corePoolSize��maximumPoolSize��ͬ, �򴴽��˹̶���С���̳߳ء�
		 // *            ���maximumPoolSize����Ϊ�������޽�ֵ(��Integer.MAX_VALUE), �������̳߳���Ӧ���������Ĳ�������
		 // * @param keepAliveTime
		 // *            ���ֻʱ�� ---> ��������ж���corePoolSize���߳�,����Щ�߳��ڿ���ʱ�䳬��keepAliveTime��ᱻ��ֹ��
		 // * @param unit
		 // *            ʱ�䵥λ ---> NANOSECONDS:����; MILLISECONDS:����;
		 // * @param workQueue
		 // *            ���� ---> ����BlockingQueue�������ڴ���ͱ����ύ�����񡣿���ʹ�ô˶�����ش�С���н�����
		 // *            ������е��߳�����corePoolSize, ��Executorʼ����ѡ����µ��߳�,���������Ŷӡ�
		 // *            ������е��̵߳��ڻ����corePoolSize, ��Executorʼ����ѡ������������, ��������µ��̡߳�
		 // *            ����޷�������������, �򴴽��µ��߳�, ���Ǵ������̳߳���maximumPoolSize, �����������,
		 // *            ���񽫱��ܾ�(�׳�RejectedExecutionException)��
		 // */
		// public static ExecutorService newCachedThreadPool() {
			// return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
										  // 60L, TimeUnit.SECONDS,
										  // new SynchronousQueue<Runnable>());		"ֱ���ύ"
		// }
		
		// public static ExecutorService newFixedThreadPool(int nThreads) {
			// return new ThreadPoolExecutor(nThreads, nThreads,
										  // 0L, TimeUnit.MILLISECONDS,
										  // new LinkedBlockingQueue<Runnable>());		"�޽����"
		// }
		
		// public static ExecutorService newSingleThreadExecutor() {
			// return new FinalizableDelegatedExecutorService
				// (new ThreadPoolExecutor(1, 1,
										  // 0L, TimeUnit.MILLISECONDS,
										  // new LinkedBlockingQueue<Runnable>()));	"�޽����"
		// }

		// public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
			// return new ThreadPoolExecutor(corePoolSize, Integer.MAX_VALUE,
										  // 0L, NANOSECONDS,
										  // new DelayedWorkQueue());					""
		// }	

	// "�Ŷ�������ͨ�ò���"
		// ֱ���ύ ---> �������е�Ĭ��ѡ���� synchronousQueue,��������ֱ���ύ���̶߳�����������. 
		// �ڲ����ڿ�������������������߳�,����ͼ�����������н�ʧ�ܣ���˻ṹ��һ���µ��߳�.
		// �˲��Կ��Ա����ڴ�����ܾ����ڲ������Ե�����ʱ������. 
		// ֱ���ύͨ��Ҫ���޽�maximumPoolSizes, �Ա���ܾ����ύ������.
		// �������Գ����������ܴ����ƽ������������ʱ���˲��������޽��߳̾������ӵĿ�����.
		
		// �޽���� ---> ʹ���޽����(��: ������Ԥ����������LinkedBlockingQueue),
		// ����������corePoolSize�̶߳���æʱ, �������ڶ����еȴ�.	
		// ����, �������߳̾Ͳ��ᳬ��corePoolSize (���,maximumPoolSize��ֵҲ����Ч��).
		
		// �н���� ---> �� maximumPoolSizes Ϊ����ֵʱ, �н����(��: ArrayBlockingQueue)�����ڷ�ֹ��Դ�ľ�, 
		// ���ǿ��ܽ��ѵ����Ϳ���.	���д�С�����ش�С������Ҫ�໥����: 
		// ʹ�ô��Ͷ��к�С�ͳؿ�������޶ȵĽ���CPUʹ���ʡ�����ϵͳ��Դ���������л�����,���ǿ��ܵ����˹�����������.
		// �������Ƶ������, ��ϵͳ����Ϊ��������ɵĸ����̰߳���ʱ��,
		// ʹ��С�Ͷ���ͨ��Ҫ��ϴ�ĳش�С, CPUʹ���ʽϸ�, ���ǿ����������ɽ��ܵĵ��ȿ���, �����ɻή��������.
		
// 4.�Զ����̳߳ؼ��̵߳�����

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

		// //�Զ���ThreadFactory��
		// static class SelfThreadFactory implements ThreadFactory {
			// private static final AtomicInteger poolNumber = new AtomicInteger(1);
			// private final ThreadGroup group;
			// private final AtomicInteger threadNumber = new AtomicInteger(1);
			// private final String namePrefix;

			// /**
			 // * @param poolName   �̳߳ص�����
			 // * @param threadName �߳�����
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

		
		
		
		
// 5.�̳߳��������
		// /**
		 // * @�̳߳��������: corePoolSize(�̳߳�������,�����̵߳���С����); activeCount(��Ծ��);
		 // * 		taskCount(��ִ��������); completedTaskCount(�����������)
		 // * 
		 // * @corePoolSize - �̳߳�������,�����̵߳���С����.
		 // * @ע��: �մ����̳߳�ʱ,�̲߳�������������,���ǵȵ��������ύʱ�Ż�����, ������ʾ����
		 // *      ThreadPoolExecutor��prestartCoreThread() / prestartAllCoreThreads()
		 // *      �������������߳�
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

			// /* ��һ�� ����2������ */
			// service.execute(command);
			// service.execute(command);

			// TimeUnit.MILLISECONDS.sleep(10);// �ӳ�һ��,��Ϊ2����������̳߳���Ҫʱ��
			// System.out.println("��һ��ִ��ju -> " + getServiceInfo(service));

			// TimeUnit.SECONDS.sleep(2);// �ȴ������߳�ִ�����
			// System.out.println("��һ�ν��� -> " + getServiceInfo(service));

			// /* �ڶ��� ����1������ */
			// service.execute(command);

			// TimeUnit.MILLISECONDS.sleep(10);
			// System.out.println("�ڶ���ִ�� -> " + getServiceInfo(service));

			// TimeUnit.SECONDS.sleep(2);
			// System.out.println("�ڶ��ν��� -> " + getServiceInfo(service));

			// // ��һ��ִ�� -> 2018-06-06 16:52:21.061
			// // - corePoolSize: 0; activeCount: 2; taskCount: 2; completedTaskCount: 0
			// // ��һ�ν��� -> 2018-06-06 16:52:23.062
			// // - corePoolSize: 0; activeCount: 0; taskCount: 2; completedTaskCount: 2
			// // �ڶ���ִ�� -> 2018-06-06 16:52:23.072
			// // - corePoolSize: 0; activeCount: 1; taskCount: 3; completedTaskCount: 2
			// // �ڶ��ν��� -> 2018-06-06 16:52:25.072
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

// 6.�̳߳��ύ��execute��submit����
	// 1).excute()ľ�з���ֵ, ���߻᷵��Future<?>
	// 2).excute()���׳��쳣, ���߲����׳��쳣,���ǵ���Future.get()

	// 'java5֮��,���������': һ��ʵ��Runnable�ӿ�; ��һ��ʵ��Callable�ӿ�.
	// a).���߶����Ա�ExecutorServiceִ��,��Runnableû�з���ֵ,��Callable�з���ֵ.
	// b).Callable�� call() ֻ��ͨ�� ExecutorService �� submit(Callable task) ��ִ��,���ر�ʾ����ȴ���ɵ�Future.
	
		// // submit---args: 0 -> 2018-06-06 17:05:17.473
		// // submit---args: 1 -> 2018-06-06 17:05:17.517
		// // submit---args: 2 -> 2018-06-06 17:05:17.517
		// //
		// // Exception---future.get(): java.util.concurrent.TimeoutException			#�쳣---��ȡ�����ʱ
		// // Exception---submit: 1 -> / by zero -> 2018-06-06 17:05:18.517			#�쳣---ִ�й��̴���
		// // submit---res: 2 -> 2018-06-06 17:05:17.517 -> 2018-06-06 17:05:18.517	#����ִ��
		// public static void submit() {
			// ExecutorService executor = Executors.newCachedThreadPool();
			// List<Future<String>> resultList = new ArrayList<Future<String>>();

			// for (int i = 0; i < 3; i++) {
				// System.out.println("submit---args: " + i + " -> " + CommUtils.getNow(true));

				// int index = i;
				// Future<String> future = executor.submit(() -> {// ��Callable��������Ľ��������Future����
					// String res = "";
					// try {
						// res = "submit---res: " + index + " -> " + CommUtils.getNow(true);
						// TimeUnit.SECONDS.sleep(1);
						// int j = (1 == index) ? 1 / 0 : 0;// �����쳣
					// } catch (Exception e) {
						// res = "Exception---submit: " + index + " -> " + e.getMessage();
					// }
					// return res;
				// });
				// resultList.add(future);
			// }

			// for (Future<String> future : resultList) {
				// try {
					// // String res = future.get();// �ȴ����������
					// String res = future.get(998, TimeUnit.MILLISECONDS);// �ȴ����ʱ��(����δִ�����,�����쳣)

					// System.out.println(res + " -> " + CommUtils.getNow(true)); // ��ӡ���������ִ�н��(�����쳣)
				// } catch (InterruptedException | ExecutionException | TimeoutException e) {
					// System.out.println("Exception---future.get(): " + e);// �ȴ���ʱ�쳣
				// } finally {
					// executor.shutdown();// ˳��ر� -> ִ���ύǰ������,���������µ�����
				// }
			// }
		// }
	
	--------------------------------------------------------------------
// 7.�̳߳�֮submit����(3��)
// ����//(1). <T> Future<T> submit(Callable<T> task);
	// private static String submitCall() throws InterruptedException, ExecutionException {
		// Future<String> submit = CommUtils.getExcutor().submit(() -> {
			// return "now: " + CommUtils.getNow(true);
		// });

		// //�����������������ĵȴ�,�����ʹ�� result = exec.submit(aCallable).get(); ��ʽ�Ĺ���.(��̫����??????)
		
		// //�ύһ���з���ֵ����������ִ��,����һ����ʾ�����δ������� Future. �� Future �� get �����ڳɹ����ʱ���᷵�ظ�����Ľ��
		// return submit.get();
	// }

	// //(2). <T> Future<T> submit(Runnable task, T result);
	// private static String submitRun4Res() throws InterruptedException, ExecutionException {
		// Data data = new TestDemo().new Data();
		// Future<Data> submit = CommUtils.getExcutor().submit(() -> {
			// data.setData("now: " + CommUtils.getNow(true));
		// }, data);

		// // return data.getData();// ֱ�ӵ��� data.getData() ����null

		// // submit(*,*)�ڶ�������data���ڽ��շ���ֵ: Data data = submit.get();
		// return submit.get().getData();
	// }
	
	// //(3). <T> Future<T> submit(Runnable task);
	// private static boolean submitRun() throws InterruptedException, ExecutionException {
		// Future<?> submit = CommUtils.getExcutor().submit(() -> {
			// System.out.println("now: " + CommUtils.getNow(true));
		// });

		// // Runnable�����ڳɹ����ʱ,���᷵�� null
		// return null == submit.get();
	// }

	--------------------------------------------------------------------
// 8.Thread.run��Thread.start���� 
	// new Thread(){}.run(): //�����¿��߳�;�����ڵ����߳���ִ��run()��������
	
	// new Thread(){}.start(): 'run()����������public����Ȩ��,��������void' //����ʵ�ֶ��߳�����!!! 
		// �̵߳�ִ���������,����˵start�˾ʹ�������ִ��! 
		// ���Ǵ��ھ���(������)״̬,һ���õ�cpuʱ��Ƭ,���Զ�ִ��run�����ڵ��߳���. 
		// new�������߳���run����������run�׳��쳣��jvm�˳�ʱ���Զ�����.
	
9.���߳���final
	final+��������	->	ͨ����������; ��Բ����
	final+���ñ���	->	��֤������ָ��������, '���������ֵ���ܱ䶯'
	final+��		->	���಻����Ϊ���౻�̳�
	final+����		->	�÷������ܱ���д
	
	public static void main(String[] args) throws JSONException {
		final int i1 = 99;
		final String s1 = "s";
		final Person p1 = new Person();
		p1.setAge(18);
		
		dosth(i1, s1, p1);
		System.out.println(i1 + " " + s1 + " " + p1.getAge());// 99 s 28 --> Ϊʲô����s; ageȴ���28???

		CommUtils.getExcutor().execute(() -> {
			// i1 = i1 + 5;// ����
			// s1 = s1 + i1;// ����
			// p1 = new Person();// ����--> ���ò��ɱ�,�����ݿɱ�

			try {
				TimeUnit.SECONDS.sleep(1);
				dosth(i1, s1, p1);
				System.out.println(i1 + " " + s1 + " " + p1.getAge());// 99 s 28 
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		});
	}
	
	// ֻ�Ǵ�����һ��"����", ����ı�ԭ�е�ֵ
	private static void dosth(int i, String s, Person person) {
		i = i++; // i1���������100
		s = s + i; // s1����ָ����"s100"
		person.setAge(28); // p1��������ָ��p1����Ӧ�Ķ���,Ȼ��ı���age����
	}
	
	0Ϊʲô�ڲ��߳��������ⲿ����Ҫ��final���η���???
		��Ϊ,���ڲ��߳����õ��ⲿ�����ܵ��ⲿ�߳����������Լ,�����ض�����������.
	'���ⲿ�������ⲿ�߳������������Ѿ�����, ���ڲ��߳��л��ڳ���ʹ��, �����������?'
	���ʱ�����Ҫ���ⲿ����ǰ���final���η�, 
	��ʵ�ڲ��߳�ʹ�õ�������������ⲿ������һ��'����Ʒ', ��ʹ�ⲿ�������������Ѿ�����,�ڲ�����Ʒ��Ȼ����.
	
10.Timer��ScheduledExecutorService
	'Timer: ���߳�,����ִ��; �������쳣,��������ֹͣ'
	'Executors: ����ִ��,����Ӱ��; �������쳣,����ֹͣ,��������Ӱ��' 
         
	//һ��ʼ, Task_A ������1��ִ��һ��. 
	//Task_B ������, ���� Task_B �����Ҫ2��, ���� Task_A Ҫ�ȵ� Task_B ִ�������ִ��.
	//�����µ���, Task_C ������, �����쳣, ����������ʱ����ȫ������!!!
	// A A A B A B C(x)
	Timer timer = new Timer();
	timer.schedule(Task_A, 0, 1 * 1000); //1sִ��һ��
	timer.schedule(Task_B, 2 * 1000, 2 * 1000); //�ӳ�2s,2sִ��һ��. (�������2s)
	timer.schedule(Task_C, 5 * 1000, 5 * 1000); //�ӳ�5s,5sִ��һ��. (�׳��쳣)

	//Task_B, Task_C ����Ӱ�� Task_A ��ʱִ��
	//Task_C �׳��쳣��, ֻӰ��������ִ��, �����ް�!!!
	// A A B A A A C(x) A B A A A A B
	ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
	pool.scheduleWithFixedDelay(Task_A, 0, 1, TimeUnit.SECONDS);
	pool.scheduleWithFixedDelay(Task_B, 2, 2, TimeUnit.SECONDS);
	pool.scheduleWithFixedDelay(Task_C, 5, 5, TimeUnit.SECONDS);
	
	'��Ҫע�����:'
		'1. newSingleThreadScheduledExecutor(); ��Ч�� newScheduledThreadPool(1);'
		//���߳�,����ִ��; ���������׳��쳣,ֻӰ������,��Ӱ������
	
		'2. �����׳��쳣������,�������׳��쳣��,�����Լ���ѭ��ִ�� -> try-catch����!'
		
	'ȡ������/����'
		Task_A.cancel(); 
		timer.cancel();
	
		ScheduledFuture<?> future = scheduled.scheduleWithFixedDelay(task1, 1, 1, TimeUnit.SECONDS);
		future.cancel(true); //����
		pool.shutdown();//����
	
11.���߳�����
	������ CPU �����ÿ���̵߳�ʱ��Ƭ��ͬ = ������������ָ�ÿ���߳�, ���Կͻ��˿������߳�Խ��, ������ռ������Դ

	//1.ȷ��ÿ���߳����ض�������
	
	
	
	
// #ThreadLocal - //�߳�����; ��һ�� Object ����,ֻҪ�̲߳���,������ʱȡ��.
	// 0.hibernate�еĵ���Ӧ��
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

	// 1.���߹�ϵ: Thread, ThreadLocal, ThreadLocalMap 	
		// // Thread ���и� ThreadLocal.ThreadLocalMap ���͵ĳ�Ա���� threadLocals
		// public class Thread implements Runnable {
			// ThreadLocal.ThreadLocalMap threadLocals = null;
		// }
		
		// // ThreadLocalMap �� ThreadLocal ���ڲ���. ����һ��Map, ����Key��ThreadLocal���Ͷ���!!
		// public class ThreadLocal<T> {
			
			// public void set(T value) {
				// Thread t = Thread.currentThread();
				// ThreadLocalMap map = t.threadLocals; // ��ȡ��ǰ�̵߳� threadLocals ����
				// if (map != null) {
					// map.set(this, value); // key -> ThreadLocal��������; value -> �ֲ�����
				// } else {
					// t.threadLocals = new ThreadLocalMap(this, value);
				// }
			// }
			
			// public T get() {
				// Thread t = Thread.currentThread();
				// ThreadLocalMap map = t.threadLocals; //��ȡ��ǰ�̵߳� threadLocals ����
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
			
			// static class ThreadLocalMap /* <ThreadLocal<?>, Object> //�Լ��ӵ�,������� */ { 
				// //...
			// }
		// }
		
	// 2.�߳���������δ洢	
		// ThreadLocal.set(v1) �ǽ� v1 ���浽��ǰ�̵߳� ThreadLocal.ThreadLocalMap ���ͱ��� threadLocals ��,
		// ����keyΪ ThreadLocal ��������, value Ϊ�ֲ߳̾����� v1
		
	// 3.�ܽ�:	//ThreadLocal�����������: �������Ķ��̷߳������� && Э���߳�ͬ������
		// ÿ���߳��ڲ�����һ�� ThreadLocal.ThreadLocalMap ���͵ĳ�Ա���� threadLocals, ���Խ����̵߳ľֲ��������浽����,���߳�֮�以������.
		// �����̷߳��ʲ������̵߳�, ÿ���߳�ֻ�ܷ��ʵ��Լ���.
		
		// ThreadLocal �������洢�ֲ߳̾�����, ֻ���ṩһ���ڵ�ǰ�߳����ҵ��ֲ�����������key.
		// Ϊÿһ���̸߳���һ�����ʵ��,��ʵ�������÷�Χ�������߳��ڲ�.

		// 'ÿ��ThreadLocal����ֻ�ܷ�һ���ֲ߳̾�����' 
		// �ֲ������洢�� ThreadLocal.ThreadLocalMap ��, ���� ThreadLocal���� Ϊkey.
		// ����, һ���߳�����Ŷ���ֲ�����������ʵ������� ThreadLocal ����
		// ThreadLocal ����ͨ���� private static ����, ���ڹ����߳�������.
		
	// 4.����ȷ�����
		// ThreadLocalΪ������̲߳��������ṩ��һ���µ�˼·
		// ThreadLocal��Ŀ����Ϊ�˽�����̷߳�����Դʱ�Ĺ�������
		
	// //������
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
			
			// //������
			// 111 - t1	222 - null	001 - m1	002 - m2
		// }
	
	
	
	
	
	
	
	