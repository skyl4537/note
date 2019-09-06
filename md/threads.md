[TOC]





# 线程概念

## 基础概念

> 并行 & 并发

```sh
并行：'同一时刻'处理多个任务。多 CPU 系统可以做到并行
并发：'同一时间段内'处理多个任务，不要求'同时'。单 CPU 系统只能做到并发
```

> 进程 & 线程

```sh
进程：是指一个内存中运行的应用程序，每个进程都有一个独立的内存空间，'一个应用程序可以同时运行多个进程'；
-----进程也是程序的一次执行过程，是系统运行程序的基本单位；系统运行一个程序即是一个进程从创建、运行到消亡的过程。

线程：进程内部的一个独立执行单元；'一个进程可以同时并发的运行多个线程'，可以理解为一个进程便相当于一个单 CPU 操作系统，
-----而线程便是这个系统中运行的多个任务。
```





## 高级概念

> 线程优先级

```sh
每个线程都有优先级概念，取值范围 [1,10]，默认是'5'。可通过 setPriority() 指定
'子线程的优先级与父线程相同'。即在线程A中创建线程B，则线程B的优先级与线程A相同。

#线程的优先级越高，其越容易得到 CPU 资源。但并不是一定能获取到 CPU 资源。
```

> 守护线程

```sh
'守护线程'：是指在程序运行的时候，在后台提供一种通用服务的线程，如gc。（'守护线程中创建的子线程,默认也是守护线程'）
'非.....'：也叫用户线程，是由用户创建的。主线程也是非守护线程，优先级为默认'5'。

#非守护线程执行完毕，则jvm退出，此时守护线程也会被强制结束。所以，一些重要的任务不该放在守护线程中。
```

```java
public static void main(String[] args) {
    System.out.println("主线程-START");
    daemonThreadTest(); //启动 守护线程
    TimeUnit.SECONDS.sleep(3);
    System.out.println("主线程-END!");
}

public static void daemonThreadTest() {
    Thread daemonThread = new Thread(() -> {
        for (int i = 0; i < 5; i++) {
            System.out.println(Thread.currentThread().getName() + "打印: " + i);
            TimeUnit.SECONDS.sleep(1);
        }
    }, "守护线程");

    daemonThread.setDaemon(true); //设置线程为守护线程，在 start() 之前调用。否则抛异常
    daemonThread.start();
}
```

```sh
#主线程   -> 休眠 3 秒后结束
#守护线程 -> 每隔 1 秒打印一次，共打印 5 次

主线程-START       主线程-START       主线程-START 
守护线程打印: 0     守护线程打印: 0     守护线程打印: 0
守护线程打印: 1     守护线程打印: 1     守护线程打印: 1
守护线程打印: 2     守护线程打印: 2     守护线程打印: 2
主线程-END!        守护线程打印: 3     主线程-END!
                  主线程-END!        守护线程打印: 3
```

```sh
#为什么守护线程没有打印 5 次？
主线程是非守护线程，非守护线程执行完毕，jvm退出。此时，守护线程也强制结束。所以，一些重要的任务不应该放在守护线程中完成。
```

```sh
#为什么后两个运行结果守护线程会多打印一次 i 值？
这其实是由线程调度引起的。
刨去守护线程中的打印耗时，可以近似的理解：在程序运行的第 3 秒，主线程和守护线程都从'休眠状态转化为就绪状态'，
此时，如果'线程调度器'先调度主线程，则出现第（1）和第（3）种结果；如果先调度守护线程，则出现第（2）种结果。

#第（3）种结果比较特殊：线程调度器先调度主线程，jvm退出，但退出相对缓慢，线程调度器又调度了1次守护线程。
```





# 线程状态

## 生命周期

> 五种状态

```sh
'新生状态'：线程对象创建后，即处于新生状态。处于新生状态的线程有自己的内存空间，调用 start() 方法，线程进入就绪状态。

'就绪状态'：就绪状态的线程处于线程就绪队列中，等待系统为其分配CPU。当系统为其分配了CPU资源后，线程进入运行状态。

'运行状态'：执行 run() 方法体代码，直到等待某资源而阻塞 或 完成任务而死亡。
----------如果在给定的时间片内没有执行结束，就会被系统给换下来回到就绪状态，等待下一次线程调度器的调度。

'阻塞状态'：处于运行状态的线程，如果调用了 sleep() 方法 或 等待IO设备等资源，则会主动让出CPU，进入阻塞状态。
----------当阻塞原因消除时，如 sleep 时间已到 或 等待的IO设备空闲下来，'阻塞线程便转为就绪状态，而不是转为运行状态'。

'死亡状态'：死亡状态是线程生命周期中的最后一个阶段。线程死亡的原因有三个。
----------（1）.线程被强制性地终止，如通过执行 stop() 来终止一个线程【不推荐使用】
----------（2）.线程代码正常的执行完毕
----------（3）.线程代码抛出未捕获的异常
```

![](assets/thread0.png)

## 阻塞状态

> 三类阻塞状态

```sh
'TIMED_WAITING'：计时等待。常见情形是调用 sleep()。单独的线程也可以调用，不一定非要有协作关系
----------------与资源锁无关，线程睡眠到期自动苏醒，并返回到就绪状态。

'BLOCKED'      ：锁阻塞。线程A与线程B代码中使用同一锁，如果线程A获取到锁，线程A进入到运行状态，
----------------那么线程B就进入到 'BLOCKED' 锁阻塞状态。

'WAITING'      ：无限等待。运行中的线程调用了某个对象的 wait() 就会转化为 WAITING 无限等待状态
----------------对于A，B两个线程，如果A线程在运行状态中调用了 wait()，那么A线程就进入 'WAITING' 状态，同时失去了同步锁。
----------------假如，这个时候B线程获取到了同步锁，在运行状态中调用了 notify()，那么就会将处于 'WAITING' 状态的A线程唤醒。
----------------注意只是唤醒，如果A线程获取到了锁对象，那么A线程唤醒后就进入就绪状态；如果没有获取锁对象，那么就进入到 'BLOCKED' 状态。
```

![](assets/thread1.png)

> sleep & wait

```sh

```





```sh
sleep()  ---> 抱着 资源锁 睡大觉，自己不用，也不给别人
join()   ---> 阻塞指定线程等到另一个线程完成以后再继续执行
wait()   ---> 释放 资源锁，自己站旁边，看别人使用
其他.     ---> 如IO中的read()，write();
```



## 线程退出

> `官方推荐`：变量标记

```java
public void run() {
    boolean isFinish = false; //记录线程任务是否完成

    while (!isFinish) {
        if (/*任务完成*/) {
            isFinish = true; //或者 break;
        } else {
            // do something ...
        }
    }
}
```

>中断标识

```java
public void run() {
    while (!Thread.currentThread().isInterrupted()) {//当前线程的中断标识
        if (/*任务完成*/) {
            Thread.currentThread().interrupt(); //实例方法，将线程的中断标识设为 true
        } else {
            // do something ...
        }
    }
}
```

> `弊端`：中断标识

```sh
中断标识 本身不会影响线程的执行，但是和其他方法混用时，就有可能影响线程的执行。
例如，和 sleep() 混用，如果当前线程处于中断状态，再调用 sleep() 方法后，不仅会抛出异常，而且还会打断当前线程的中断状态。
```

```java
new Thread(() -> {
    for (int i = 0; !Thread.currentThread().isInterrupted() && i < 3; i++) {
        if (i > 0) {
            Thread.currentThread().interrupt(); //大于0，中断标识设为 true
        }
        System.out.println(LocalTime.now() + " - " + i);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println(LocalTime.now() + " - " + e.getMessage());
        }
    }
}).start();
```

```sh
20:46:30.274 - 0
20:46:31.275 - 1
20:46:31.275 - sleep interrupted #中断状态的线程调用 sleep()，不仅会抛出异常，而且不再 sleep，不再 中断
20:46:31.275 - 2
20:46:31.275 - sleep interrupted
```





# 线程安全





# 线程池

##基础概念

> 基本概念

```sh
#一个可以容纳多个线程的容器，其中的线程可以反复使用，省去了频繁创建和销毁线程的系统开销

降低资源消耗：通过重复利用已有的线程，降低线程创建和销毁造成的消耗
提高响应速度：当任务到达时，任务可以不需要等待线程的创建就能立即执行
提高可管理性：线程是稀缺资源，如果无限制的创建，不仅会消耗系统资源，还会降低系统的稳定性，使用线程池可以进行统一的分配，调优和监控。
```

> VS 线程组

```sh

```

> 生命周期

```sh
线程池是一个进程级的重量级资源。默认生命周期和 jvm 一致。即从开启线程池开始，到 jvm 关闭为止。
如果手工调用 shutdown()，那么线程池执行所有的任务后，自动关闭。
```

> 五种状态

```java
public class ThreadPoolExecutor extends AbstractExecutorService {
    volatile int runState;
    private static final int RUNNING    = -1 << COUNT_BITS;
    private static final int SHUTDOWN   =  0 << COUNT_BITS;
    private static final int STOP       =  1 << COUNT_BITS;
    private static final int TIDYING    =  2 << COUNT_BITS;
    private static final int TERMINATED =  3 << COUNT_BITS;
}
```

```sh
#runState：线程池的状态，volatile 保证线程之间的可见性
'RUNNING'   ：线程池创建后，初始处于 RUNNING 状态。接受新的任务，处理队列任务
'SHUTDOWN'  ：调用 shutdown()后的状态。不再接受新的任务，处理队列任务
'STOP'      ：调用 shutdownNow()....。.............，不处理队列任务，并尝试中断正在执行的任务线程
'TERMINATED'：线程池已结束，即 terminated()方法执行完。
```

> 线程池初始化

```sh
默认，线程池创建之后，池中是没有线程的，需要接收任务之后才会创建线程。如果需要预创建线程，可通过以下两个方法实现。

prestartCoreThread();     #初始化  1   个核心线程
prestartAllCoreThreads(); #初始化 core 个核心线程
```

> 线程池关闭

```sh
shutdown();    #不再接受新的任务，处理队列任务，以及当前任务
shutdownNow(); #.............，不处理队列任务，并尝试中断正在执行的任务线程
```

## 高级概念

>核心参数

```java
public ThreadPoolExecutor(int corePoolSize, 
                          int maximumPoolSize, 
                          long keepAliveTime,
                          TimeUnit unit, BlockingQueue<Runnable> workQueue,
                          ThreadFactory threadFactory,
                          RejectedExecutionHandler handler);
```

```sh
'corePoolSize'：线程池中的核心线程数。
默认，线程池创建后池中线程总数为 0，只有在有任务提交到线程池中时才会创建线程。当然，也可以预创建线程。

每次新来一个任务，会创建一个线程去执行，直到 poolSize = corePoolSize
当'poolSize > corePoolSize'时，新提交的任务会被放进任务缓存队列 queue
```

```sh
'maximumPoolSize'：线程池中允许创建的最大线程数

表示在线程池中最多能创建多少个线程，非核心线程数 = maximumPoolSize - corePoolSize
```

```sh
'keepAliveTime'：线程池中《非核心线程》允许闲置的最大时间，超过这个时间将会被回收
----------------对于任务很多，并且每个任务处理时间较短的的情况，可以适当增大这个参数来提高线程利用率。
----------------当设置 allowCoreThreadTimeOut(true) 时，此参数也会作用到核心线程上，即 corePoolSize 也会被回收。
```

```sh
'workQueue'：存储任务的缓存队列（线程安全），用于提交和存储待处理的任务

(1).poolSize <  corePoolSize，添加新线程
(2).poolSize >= corePoolSize，将请求加入缓冲队列
(3).poolSize <  maximumPoolSize，缓冲队列已满，则扩充 corePoolSize 至 maximumPoolSize
(4).poolSize >= maximumPoolSize，请求无法加入缓冲队列，任务将被拒绝
```

```sh
'threadFactory'：线程工厂，主要用来创建线程
```

```sh
'handler'：任务拒绝策略。以下两种情况，会触发：
----------(1).等待队列已满 && poolSize = maximumPoolSize
----------(2).调用 shutdown()，会等待线程池里的任务执行完毕，才真正 SHUTDOWN。在等待间，会拒绝新任务
```

> 任务拒绝策略

```sh
'AbortPolicy'        ：默认方式，丢弃新的任务，并抛出异常 RejectedExecutionException
'DiscardPolicy'      ：丢弃任务，但不抛出异常。会导致被丢弃的任务无法再次被执行
'DiscardOldestPolicy'：丢弃最旧的未处理请求，然后执行当前提交的任务
'CallerRunsPolicy'   ：直接在 execute() 的调用线程（可能是主线程）中运行被拒绝的任务。
----------------------执行完之后，尝试将下一个任务添加到线程池中，可有效降低向线程池内添加任务的速度
```

> 核心方法

```java
public class ThreadPoolExecutor 
    extends [AbstractExecutorService implements (ExecutorService extends Executor)] {

    // 向线程池提交一个任务，交由线程池去执行
    execute();

    // 也是用来向线程池提交任务的，但和 execute() 不同，它能够返回任务执行的结果
    // 底层还是调用 execute()，只不过利用了 Future 来获取任务执行结果
    submit();

    //关闭线程池。不会接收新的任务，但会处理缓存队列中的任务，以及当前正在执行的任务。
    void shutdown();

    //立刻关闭线程池。即不会接收新的任务，也不处理缓存队列中的任务，甚至还会尝试中断正在执行的任务
    //返回结果为 阻塞列表中未被执行的任务
    List<Runnable> shutdownNow();
}
```

> 线程池扩充

```sh
'poolSize = 0'：默认，线程池创建之后，池中是没有线程，需要接收任务之后才会创建线程
'poolSize < corePoolSize'：新来任务，就会创建一个新的线程去执行
'poolSize > corePoolSize && 缓存队列未满'：新来任务，尝试将其添加到缓存队列当中，等待空闲线程将其取出去执行

'poolSize > corePoolSize && 缓存队列已满 && poolSize < maximumPoolSize'：
--------------------------新来任务，缓存队列已满，则会尝试创建新的线程（即扩充 corePoolSize）去执行这个任务

'poolSize > corePoolSize && 缓存队列已满 && poolSize = maximumPoolSize'：
--------------------------新来任务，缓存队列已满，corePoolSize 已扩充至 maximumPoolSize，则会采取'任务拒绝策略'进行处理
```

```sh
当高峰期已过，如果某线程空闲时间超过 keepAliveTime，线程将被终止，直至 poolSize <= corePoolSize。
如果设置了 allowCoreThreadTimeOut(true)，那么核心线程也会被终止（默认不会），直至 poolSize = 0。
```

> 举个栗子：corePoolSize = 10，maximumPoolSize = (10+5)

```sh
假如有一个工厂，工厂里面有 10 个工人，每个工人同时只能做一件任务。

因此，只要当10个工人中有工人是空闲的，来了任务就分配给空闲的工人做。当10个工人都有任务在做时，如果还来了任务，就把任务进行排队等待。

如果新任务增长的速度远大于工人做任务的速度，那么此时工厂主管可能会想补救措施，如再招 5 个临时工人，然后就将任务也分配给这 5 个临时工人做

如果说 （10+5） 个工人做任务的速度还是不够，此时工厂主管可能就要考虑不再接收新的任务或者抛弃前面的一些任务了

当这 （10+5） 个工人中有人空闲时，而新任务增长的速度又比较缓慢，工厂主管可辞掉 5 个临时工。只保持原来的10个工人，毕竟请额外的工人是要花钱
```

> 容量合理大小

```sh

```

## 基本使用

> JDK 内置的 4 种线程池

```java
//(1).单例线程池：只用一个核心线程来处理任务，适用于有序执行任务
public static ExecutorService newSingleThreadExecutor() {
    return new FinalizableDelegatedExecutorService
        (new ThreadPoolExecutor(1, 1,
                                0L, TimeUnit.MILLISECONDS,
                                new LinkedBlockingQueue<Runnable>())); //queue MAX
}
```

```java
//(2).固定线程池：核心线程数和最大线程数相同，不存在非核心线程。处理一个无限队列
public static ExecutorService newFixedThreadPool(int nThreads) {
    return new ThreadPoolExecutor(nThreads, nThreads,
                                  0L, TimeUnit.MILLISECONDS,
                                  new LinkedBlockingQueue<Runnable>()); //queue MAX
}
```

```java
//(3).缓存线程池：不使用核心线程，使用无限大的非核心线程，每个线程的过期时间为 60 秒
//适用于大量需要立即处理，并且每个任务耗时较少的任务集合
public static ExecutorService newCachedThreadPool() {
    return new ThreadPoolExecutor(0, Integer.MAX_VALUE, //max MAX
                                  60L, TimeUnit.SECONDS,
                                  new SynchronousQueue<Runnable>());
}
```

```java
//(4).定时线程池：比Timer更安全，功能更强大。--> 如果有任务执行过程中抛出异常，则会跳出，不会影响下次循环
public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
    return new ScheduledThreadPoolExecutor(corePoolSize, Integer.MAX_VALUE, //max MAX
                                           0, NANOSECONDS,
                                           new DelayedWorkQueue());
}
```

> 内置线程池的弊端

```sh
Single & Fixed    #queue：MAX，容易因请求队列堆积，耗费大量内存，甚至OOM
Cached & Schedule #max  ：MAX，可能会创建非常多的线程，甚至OOM
```

> `ali 推荐`

```java
//org.apache.commons.lang3.concurrent.BasicThreadFactory
ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("demo-pool-%d").build();
```

```java
//com.google.common.util.concurrent.ThreadFactoryBuilder
ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("demo-pool-%d").build();
```

```java
//普通任务的线程池
ExecutorService threadPool = new ThreadPoolExecutor(5, 200,
                                                    0L, TimeUnit.MILLISECONDS,
                                                    new LinkedBlockingQueue<>(1024),
                                                    threadFactory,
                                                    new ThreadPoolExecutor.AbortPolicy());
```

```java
//定时任务的线程池
ScheduledExecutorService threadPool = new ScheduledThreadPoolExecutor(1, threadFactory);
```

##异常处理

> 如何正确处理子线程中的异常？

```sh
子线程中发生了异常，如果没有任何类来接手处理的话，是会直接退出的
所以，不做异常处理，也不记录日志，则会出现子线程任务既没执行成功，也没有任何日志提示的诡异现象
```

```sh
#java中两种异常：编译器异常(CheckedExceptions)，运行时异常(RuntimeExceptions)

'编译器异常'：因为 run()不接受 throws 语句，所以需要手动 try-catch
'运行时异常'：将异常信息记录到日志，然后退出程序。常见的APP崩溃，正是基于这一原理
```

> 异常处理方式（1）：手动 try-catch

>异常处理方式（2）：局部线程的异常处理器 

```java
new Thread(() -> {
    Thread.currentThread().setUncaughtExceptionHandler( //只能捕获当前线程的异常
        (t, e) -> log.info("局部线程的异常处理器: {} - {}", t.getName(), e.toString()));
    int i = 1 / 0;
}).start();

new Thread(() -> System.out.println("aa".split("_")[1])).start();

//局部线程的异常处理器: Thread-0 - java.lang.ArithmeticException: / by zero
```

>异常处理方式（3）：全局线程的异常处理器

```sh
#选择顺序
优先选择，当前线程的异常处理器（默认没有）
然后选择，当前线程所属'线程组'的异常处理器
最后选择，全局的异常处理器
最后的最后，都没有设置异常处理器，则主线程默默退出
```

```java
Thread.setDefaultUncaughtExceptionHandler(
    (t, e) -> log.info("全局线程的异常处理器: {} - {}", t.getName(), e.toString()));

new Thread(() -> {
    Thread.currentThread().setUncaughtExceptionHandler(
        (t, e) -> log.info("局部线程的异常处理器: {} - {}", t.getName(), e.toString()));
    int i = 1 / 0;
}).start();

new Thread(() -> System.out.println("aa".split("_")[1])).start();

//全局线程的异常处理器: Thread-1 - java.lang.ArrayIndexOutOfBoundsException: 1
//局部线程的异常处理器: Thread-0 - java.lang.ArithmeticException: / by zero
```

>异常处理方式（4）：在父线程中捕获异常

```java
ExecutorService executor = Executors.newSingleThreadExecutor();
Future<Object> future = executor.submit(() -> {
    try {
        int i = 1 / 0;
    } catch (Exception e) {
        throw new RuntimeException(e.getMessage()); //抛出异常
    }
    return "SUCCESS";
});

try {
    log.info("res: {}", future.get()); //get 获取结果
} catch (InterruptedException | ExecutionException e) {
    log.error("父线程中捕获异常: {}", e.toString());
    executor.shutdown();
}

//父线程中捕获异常: java.util.concurrent.ExecutionException: java.lang.RuntimeException: / by zero
```

## 常见问题

> 当缓存队列已满时，新来一个任务，恰好核心线程有一个空闲，哪种情况正确：`（1）`

```sh
（1）.新任务直接在空闲的核心线程中执行
（2）.空闲的核心线程从缓冲队列中获取任务，然后将新任务放入到缓存队列

#正确答案（1）：有空闲的核心线程就直接使用；没有则加入队列（队列未满）或新建线程（队列已满）。以下demo可验证
```

> 线程池最大容量为 4+2，依次添加 7 个任务，怎么执行？

```java
ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 200,  //线程池最大容量 4+2
                                                     TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(2));
printPoolProperty(executor); //任务开始执行前，获取线程池属性
for (int i = 1; i < 8; i++) {
    int taskNum = i;
    try {
        executor.submit(() -> {
            System.out.println(LocalTime.now() + " - " + Thread.currentThread().getName() + " START " + taskNum);
            TimeUnit.SECONDS.sleep(1);
        });
        printPoolProperty(executor); //任务添加后，获取线程池属性
    } catch (Exception e) {
        log.error("任务执行异常: {}", e.toString());
    }
}

Thread.sleep(6 * 1000);
printPoolProperty(executor); //所有任务执行完毕后，获取线程池属性
executor.shutdown();
```

```sh
13:21:17.983 - poolSize: 0; queueSize: 0; completedTask: 0 #初始状态

13:21:17.992 - poolSize: 1; queueSize: 0; completedTask: 0 #Task-1.
13:21:17.992 - poolSize: 2; queueSize: 0; completedTask: 0 #Task-2.（poolSize = core）

13:21:17.992 - poolSize: 2; queueSize: 1; completedTask: 0 #Task-3.
13:21:17.992 - poolSize: 2; queueSize: 2; completedTask: 0 #Task-4.（queue 已满）

13:21:17.992 - poolSize: 3; queueSize: 2; completedTask: 0 #Task-5.（扩充 poolSize -> max）
13:21:17.992 - poolSize: 4; queueSize: 2; completedTask: 0 #Task-6.（poolSize = max）

13:21:17.995 - 任务执行异常: java.util.concurrent.RejectedExecutionException: ... #Task-7.（拒绝策略）

13:21:17.994 - pool-1-thread-1 START 1
13:21:17.994 - pool-1-thread-2 START 2
13:21:18.006 - pool-1-thread-4 START 6 #后提交的 6和5 先于 3和4 执行
13:21:18.007 - pool-1-thread-3 START 5
13:21:18.995 - pool-1-thread-2 START 3
13:21:18.995 - pool-1-thread-1 START 4

13:21:24.007 - poolSize: 2; queueSize: 0; completedTask: 6 #所有任务完毕后，poolSize = core
```

> 三个线程顺序执行





#定时调度

## Timer

> 单线程，串行执行。单个任务异常，整体任务停止

```java
Timer timer = new Timer();
timer.schedule(Task_A, 0, 1 * 1000); //1s执行一次
timer.schedule(Task_B, 2 * 1000, 2 * 1000); //延迟2s，2s执行一次 (完成消耗2s)
timer.schedule(Task_C, 5 * 1000, 5 * 1000); //延迟5s，5s执行一次 (抛出异常)
```

```sh
#最终结果：A A A B A B C(x)
一开始，Task_A 能正常1秒执行一次。
Task_B 启动后，由于 Task_B 完成需要2秒，导致 Task_A 要等到 Task_B 执行完才能执行。
更可怕的是，Task_C 启动后，抛了异常，导致整个定时任务全部挂了！！！
```

```java
Task_A.cancel(); //取消单个任务
timer.cancel();  //取消整体任务
```

##定时线程池

> 并行执行，互不影响。单任务异常，异常任务停止，其他任务不影响

```java
ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
pool.scheduleWithFixedDelay(Task_A, 0, 1, TimeUnit.SECONDS);
pool.scheduleWithFixedDelay(Task_B, 2, 2, TimeUnit.SECONDS);
pool.scheduleWithFixedDelay(Task_C, 5, 5, TimeUnit.SECONDS); //Task_C 抛出异常后，将停止执行。如若想继续执行，使用 try-catch
```

```sh
#最终结果：A A B A A A C(x) A B A A A A B
Task_B，Task_C 不再影响 Task_A 定时执行
Task_C 抛出异常后，只影响自身不再执行，其他无碍！！！
```

```java
ScheduledFuture<?> future = scheduled.scheduleWithFixedDelay(task1, 1, 1, TimeUnit.SECONDS);
future.cancel(true); //取消单个任务
pool.shutdown();     //取消整体任务
```

> 停止任务时，任务是否已放入任务集map？

```java
private static void scheduledTest() {
    String key = "JOB_KEY";
    final Map<String, Future> futures = new HashMap<>(); //job_map
    LocalTime init = LocalTime.now();
    ScheduledExecutorService pool = Executors.newScheduledThreadPool(5);

    ScheduledFuture<?> future = pool.scheduleWithFixedDelay(() -> {
        if (ChronoUnit.MILLIS.between(init, LocalTime.now()) > 3 * 1000) { //启动3s后，停止任务
            futures.get(key).cancel(true);
            pool.shutdown();
        }
    }, 1, 3, TimeUnit.SECONDS);

    //先于任务执行，即任务执行过程中，future 肯定已放入 Map
    futures.put(key, future);
}
```

## Scheduled

> Spring内置的定时线程池 `（默认使用单例线程池）`

```java
@Scheduled(fixedRate = 6000)  //上一次开始执行时间点之后6秒再执行
@Scheduled(fixedDelay = 6000) //.....执行完毕时间点...........
@Scheduled(initialDelay=1000, fixedRate=6000) //第一次延迟1秒后执行，之后按 fixedRate 的规则每6秒执行一次

@Scheduled(cron = "0/1 * * * * ?")   //详细配置方法执行频率，1s1次
```

```sh
#cron表达式: [秒] [分] [时] [日] [月] [周] [年(可省)]
秒(0~59); 分(0~59); 时(0~23); 日(1~31,和月份有关); 月(1~12); 星期(1~7,1为周日); 年(1970~2099)

* 适用于所有字段。表示对应时间域的'每一个时刻'。如分钟字段，表示每分钟

- 适用于所有字段。表示'一个范围'。如小时字段10-12，表示从10到12点，即 10,11,12

, 适用于所有字段。表示'一个列表值'。如星期字段"MON,WED,FRI"，表示星期一、星期三和星期五

/ 适用于所有字段。表示'一个等步长序列'。x/y表示：x为起始值，y 为增量步长值
  如，分钟字段： 0/15表示 0,15,30,45； 5/15表示 5,20,35,50。'*/y == 0/y'

? 日期和星期字段。通常指定为'无意义的值'，相当于占位符。因为日和星期是有冲突的
```

>配置定时任务的线程池大小

```properties
#线程池大小，默认1
spring.task.scheduling.pool.size=5
#线程名前缀，默认 scheduling-
#spring.task.scheduling.thread-name-prefix=demoscheduling-
```

> 异步任务

```sh

```

##Quartz

> 核心概念

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-quartz</artifactId>
</dependency>
```

```sh
Job       #任务    - 你要做什么事？
Trigger   #触发器   - 你什么时候去做？
Scheduler #任务调度 - 你什么时候需要去做什么事？
```

> javaSE

```java
//JOB类必须定义为 public
public class JobDemo implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        log.info("{} - 任务被触发时调用！", LocalTime.now());
    }
}
```

```java
private static void task01() throws SchedulerException {
    //1.job
    JobDetail job = JobBuilder.newJob(JobDemo.class)
        .withIdentity("job-01", "group-01").build();

    //2.trigger
    // 方式一：通过 Quartz 内置方法来完成简单的重复调用，2秒一次
    Trigger trigger = TriggerBuilder.newTrigger()
        .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(2)).build();

    //方式二：自定义 Cron 表达式来给定触发的时间
    // Trigger trigger = TriggerBuilder.newTrigger()
    //         .withIdentity("trigger-11", "group-11")
    //         .withSchedule(CronScheduleBuilder.cronSchedule("0/2 * * * * ?")).build();

    //3.scheduler
    Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
    scheduler.scheduleJob(job, trigger);
    scheduler.start();
}
```

> SpringBoot（1）：创建普通job类，直接调用。灵活，非侵入

```java
@Slf4j
@Component
public class JobDemo01 {
    @Autowired
    HelloController helloController;

    public void job() {
        String hello = helloController.hello();
        log.info("定时任务执行: {}", hello);
    }
}
```

```java
@Configuration
public class QuartzConfig01 {
    @Bean("job01")
    public MethodInvokingJobDetailFactoryBean job01(JobDemo01 jobDemo01) {
        MethodInvokingJobDetailFactoryBean job = new MethodInvokingJobDetailFactoryBean();
        job.setName("my-job01"); // 任务的名字
        job.setGroup("my"); // 任务的分组
        job.setConcurrent(false); // 是否并发
        job.setTargetObject(jobDemo01); // 被执行的对象
        job.setTargetMethod("job"); // 被执行的方法
        return job;
    }

    @Bean(name = "trigger01")
    public CronTriggerFactoryBean tigger01(@Qualifier("job01") MethodInvokingJobDetailFactoryBean job01) {
        CronTriggerFactoryBean tigger = new CronTriggerFactoryBean();
        tigger.setName("my-tigger01");
        tigger.setJobDetail(Objects.requireNonNull(job01.getObject()));
        tigger.setCronExpression("0/5 * * * * ?"); //cron
        return tigger;
    }

    @Bean(name = "scheduler01")
    public SchedulerFactoryBean scheduler01(@Qualifier("trigger01") Trigger trigger01) {
        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setStartupDelay(5); // 延时启动定时任务，避免系统未完全启动却开始执行定时任务的情况
        scheduler.setOverwriteExistingJobs(true); // 覆盖已存在的任务
        scheduler.setTriggers(trigger01); // 注册触发器
        return scheduler;
    }
}
```

> SpringBoot（2）：job继承特定类，实现其方法，方法就是被调度的任务体

```java
@Slf4j
//@Component -> 无需此注解，区别于方式1
public class JobDemo02 extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) {
        HelloController helloController = (HelloController) context.getMergedJobDataMap().get("helloController");
        String hello = helloController.hello();
        log.info("定时任务执行: {}", hello);
    }
}
```

```java
@Configuration
public class QuartzConfig02 {
    @Autowired
    HelloController helloController;

    @Bean("job02")
    public JobDetailFactoryBean job02() {
        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setJobClass(JobDemo02.class);

        Map<String, Object> map = new HashMap<>();
        map.put("helloController", helloController);
        job.setJobDataAsMap(map); //传参 -> helloController
        return job;
    }
    //...同上...
}
```

