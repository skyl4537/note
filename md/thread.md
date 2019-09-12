[TOC]





#线程基础

## 基础概念

> 并行 & 并发

```sh

```

> 线程 & 进程

```sh

```

> 线程调度

```sh
计算机通常只有一个CPU时，在任意时刻只能执行一条计算机指令，每一个进程只有获得CPU的使用权才能执行指令。
所谓多进程并发运行，从宏观上看，其实是各个进程轮流获得CPU的使用权，分别执行各自的任务。

那么,在可运行池中，会有多个线程处于就绪状态等到CPU，JVM就负责了线程的调度。
JVM采用的是'抢占式调度'，没有采用分时调度，因此可以能造成多线程执行结果的的随机性。
```

> 多线程意义

```
多线程的目的是为了更好的利用计算机的 CPU 资源。
比如在一个进程中，如果只有一个线程（也叫主线程），那么如果当这个线程因为某种原因阻塞（等待用户输入数据等情况）的时候，
那么相对应的这个进程也让出了 CPU 资源并暂停执行了。试想一下，如果在一个进程中添加多个线程，那么当这个进程中某个线程阻塞的时候，
其余线程还可以继续执行，做它们自己的工作，这样的话计算机的利用效率就提高了。
```

> 线程创建

```sh
- extends Thread，'不推荐使用'，java 单继承，多实现  #每个线程执行自己的 run()方法（Thread implements Runnable）。
- implements Runnable，作为参数传入 Thread，'推荐'。#多个.......同一个 run()...
- implements Callable，使用线程池调用

```

```sh
#Runnable 比 Thread 具有的优势
多个线程共享同一个 run()方法，共同操作同一份资源
可以避免java中的单继承的局限性
增加程序的健壮性，实现解耦操作，代码可以被多个线程共享，代码和数据独立
线程池只能放入实现 Runable 或 Callable 类线程，不能直接放入继承Thread的类
```

> 线程启动

```sh
通过调用线程对象的 start() 方法来启动一个线程（该方法只能被调用一次）。
线程开启后便进入'就绪状态'，但并不会马上执行，而是等待线程调度器的调度。
一旦线程调度器调度了该线程之后，该线程便可获得 CPU 资源，然后进入'运行状态'，开始执行 run() 方法。run() 方法运行结束，该线程也随即终止。
```

```sh
#区别 start() 和 run()

```

> 线程暂停

```sh
通过调用线程对象的 sleep(long millis) 方法来让线程休眠指定的秒数，调用这个方法之后，线程将会让出 CPU 进入'休眠状态'。
休眠完成之后，线程并不会直接获得 CPU 资源，而是会进入'就绪状态'，等待线程调度器的调度来获取 CPU 资源。
```

## 生命周期

![(assets/thread0.png)

![(assets/thread1.png)

> 区别 sleep() 和 wait()

```java
/*相同点*/：二者都可以使线程处于阻塞状态。
```

```java
sleep()是 Thread 类的方法，必须指定时间参数。 sleep()方法可以在任何地方使用。
```
```java
wait()是 Object 类的方法，可以指定时间参数，也可以不指定。 wait()方法只能在同步方法或同步代码块中使用。
```
```java
当二者都在同步语句中使用时，二者都会让出CPU资源，/* sleep(),notify()方法不会释放资源锁，wait()会 */
```
```java
sleep()方法时间到，线程直接恢复到就绪状态，再次获得线程调度才能恢复到运行状态。 wait()方法如果没有设置时间，就必须通过 notify()或者 notifyAll()来唤醒，才会进入锁池，再次获得对象锁才会进入运行状态。
```
```java
每个对象都有一个锁来控制同步访问， synchronized 关键字可以和对象的锁交互，来实现同步方法或同步块。 sleep()方法正在执行的线程主动让出CPU（然后CPU就可以去执行其他任务），在sleep指定时间后CPU再回到该线程继续往下执行(注意：sleep方法只让出了CPU，而并不会释放同步资源锁！！！)；
```
```java
wait()方法则是使当前线程让出CPU资源，释放同步资源锁，以便线程获取到资源锁而运行，只有调用了 notify()方法，wait状态才会解除，可以去参与竞争同步资源锁，进而得到执行。（注意：notify的作用相当于叫醒睡着的人，而并不会给他分配任务，就是说notify只是让之前调用wait的线程有权利重新参与线程的调度）；
```
> 锁池 和 等待池

```java

```



> DEMO：sleep 和 wait

```java

```

```java
2019-05-25T10:02:31.893 Thread01 START... //wait()阻塞线程-01，并释放资源锁
2019-05-25T10:02:33.858 Thread02 START... //线程-02获取资源锁
2019-05-25T10:02:34.858 Thread02 END!!!   //线程-02唤醒线程-01，但notify()没有释放资源锁
2019-05-25T10:02:34.858 Thread01 END!!!   //线程-02出了同步代码块，释放资源锁，线程-01获得资源锁
```
## 线程状态

在给定时间点上，一个线程只能处于一种状态。这些状态是虚拟机状态，它们并没有反映所有操作系统线程状态。


> 线程状态-API：Thread.State

```java
'NEW'：至今尚未启动的线程处于这种状态。
'RUNNABLE'：正在 Java 虚拟机中执行的线程处于这种状态。
'TERMINATED'：已退出的线程处于这种状态。

'BLOCKED'：受阻塞并且正在等待监视器锁。如进入 synchronized 或调用 wait()后再次进入同步的块/方法。 

'WAITING'：无限期地等待另一个线程执行某一特定操作。如调用不带超时的 wait(); join();

'TIMED_WAITING'：具有指定等待时间的某一等待线程的线程状态。如调用 sleep(n); wait(n); join(n);
```
## 相关API

> 线程插队：`t1.join(millis)`

线程 t2 调用 t1.join()，导致 t2 挂起，挂起指定时间（有参） 或 直至 t1 执行完毕（无参），才继续执行 t2。

```java
Thread t1 = new Thread(() -> {
    System.out.println(LocalDateTime.now() + " Thread-T1");
    TimeUnit.SECONDS.sleep(2);
});

Thread t2 = new Thread(() -> {
    t1.join(1000); //等待，最大等待时间1000ms
    System.out.println(LocalDateTime.now() + " Thread-T2");
    TimeUnit.SECONDS.sleep(2);
});

Thread t3 = new Thread(() -> {
    t2.join(); //等待，直到线程t1终止
    System.out.println(LocalDateTime.now() + " Thread-T3");
});

t1.start();
t2.start();
t3.start(); //【模拟】三个线程顺序执行
System.out.println(LocalDateTime.now() + " main");
```
```java
2019-05-22T09:19:27.880 - main //主线程和 T1 几乎同时执行
2019-05-22T09:19:27.883 - Thread-T1
2019-05-22T09:19:28.983 - Thread-T2 //等待1s.(只等待T1执行1s)
2019-05-22T09:19:30.983 - Thread-T3 //等待2s.(等待T2执行完毕)
```
> 如果线程被创建了，但还未启动，调用它的 `join()` 方法是没有作用的，将直接继续向下执行！

```java
Thread thread = new Thread(() -> TimeUnit.SECONDS.sleep(2));
System.out.println(LocalDateTime.now() + " " + " 111");

thread.join(); //线程还未启动
System.out.println(LocalDateTime.now() + " " + " 222");

thread.start(); //线程启动
thread.join();
System.out.println(LocalDateTime.now() + " " + " 333");
```

```java
2019-05-22T10:00:16.611  111
2019-05-22T10:00:16.611  222 //子线程未启动，调用子线程的 join() 不会挂起主线程
2019-05-22T10:00:18.616  333 //启动后，则会挂起
```
> `join() + synchronized`

```java
Thread thread = new Thread(() -> {
    synchronized (Thread.currentThread()) {
        TimeUnit.SECONDS.sleep(5);
    }
});
thread.start();
System.out.println(LocalDateTime.now() + " Main Start");

thread.join(1000);
System.out.println(LocalDateTime.now() + " Main Finished");
```

```java
2019-05-27T11:10:27.522 Main Start
2019-05-27T11:10:32.433 Main Finished //相隔 5s，而非join()设置的 1s

//由于先调用了 thread.start()方法，线程进入 synchronized 代码段
//main线程等待 1 秒以后，还是得不到线程 thread 的对象锁，只能继续等待，直到线程 thread 结束，释放锁。
```

> 线程谦让`yield()`

```java
//t2调用 Thread.yield(); 是提示线程调度器让出 t2 的 CPU 资源，让其他线程使用。
//这只是一种提示，线程调度器可以忽略这种提示，所以 CPU 资源是否让出并不是一定的，是有一定概率的。
//注意：成功让出后，t2进入就绪状态，而非阻塞状态。
new Thread(() -> {
    while (true) {
        System.out.println("子** - 正在占用CPU");
        Thread.yield(); //请求线程调度器让出当前线程的 CPU 资源
    }
}).start();

for (; ; ) {
    System.out.println("主线程 - 正在占用CPU");
    Thread.yield();
}
```

```java
主线程 - 正在占用CPU //主线程调用 yield(); 是一定概率的让出CPU资源，但不是一定让出
主线程 - 正在占用CPU
子** - 正在占用CPU
... ...
```
> 其他API

```java
//让调用这个方法的线程让出 CPU，休眠参数指定的毫秒数。
//休眠完成之后，线程并不会直接获得 CPU 资源，而是进入就绪状态，等待线程调度器的调度来获取 CPU 资源。
Thread.sleep(long millis);
```

```java
Object.wait(); //让调用这个方法的线程陷入等待状态，可以通过参数设置等待时间，
// 如果不设置参数将使得线程一直等待。
// 注意这个方法只能在 synchronized 关键字修饰的代码块中调用，
// 这个我们会在后面的文章中细讲。

Object.notify(); //唤醒一个因调用当前对象的 wait() 方法而陷入等待状态的线程，具体哪个线程未知。
// 这个方法也只能在 synchronized 关键字修饰的代码块中执行

Object.notifyAll(); //唤醒所有因调用当前对象的 wait() 方法而陷入等待状态的线程。
// 同样，这个方法也只能在 synchronized 关键字修饰的代码块中执行。
```




# 线程同步

##内存模型


![](assets/thread2.png)

> ReentrantLock：重入锁，建议应用的同步方式。相对效率比 synchronized 高。量级较轻。

```java
synchronized 在 JDK1.5 版本开始，尝试优化。到 JDK1.7 版本后，优化效率已经非常好了。在绝对效率上，不比 reentrantLock 差多少。
使用重入锁，'必须必须必须' 手工释放锁标记。一般都是在 finally 代码块中定义释放锁标记的 unlock 方法。
```



> volitale、synchronized、RetreenLock







## synchronized

> `volatile` 修饰的变量，在计算过程中，每次使用变量参与计算时，都会检查内存中的数据是否发生变化，而不是一直使用 CPU 缓存中的数据，可以保证计算结果的正确。`但不是让一个变量在多个线程中同步。`

```java

```

> `volatile`： 只能保证可见性，不能保证原子性。

```java
volatile int sum;

/*synchronized*/ void m80() {
    for (int j = 0; j < 1000; j++) {
        sum++;
    }
}

private void demo8() throws InterruptedException {
    ArrayList<Thread> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
        list.add(new Thread(() -> m80()));
    }
    for (Thread thread : list) {
        thread.start();
    }
    for (Thread thread : list) {
        thread.join(); //线程-A 中调用 线程-B.join(),则线程-A 等待线程-B 执行完成。
    }
    System.out.println(LocalTime.now() + " - " + sum);
    
    // 13:04:49.806 - 9422 --> volatile只能保证 sum 在各个线程中使用时，都是最新的值，但不能保证...
}
```

>`AtomicInteger`：原子操作类型。其中的每个方法都是原子操作。可以保证线程安全。

```java
AtomicInteger atomicSum = new AtomicInteger(0);

void m90() {
    for (int j = 0; j < 1000; j++) {
        atomicSum.incrementAndGet(); //自增操作，线程安全，勿需加锁
    }
}

private void demo9() throws InterruptedException {
    ArrayList<Thread> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
        list.add(new Thread(() -> m90()));
    }
    for (Thread thread : list) {
        thread.start();
    }
    for (Thread thread : list) {
        thread.join();
    }
    System.out.println(LocalTime.now() + " - " + atomicSum);
}
```

>`注意`：原子类型中的方法是保证了原子操作，但多个方法之间是没有原子性的。

```java
//get()方法 和 incrementAndGet()方法都是原子操作。但复合使用时，无法保证原子性，仍旧可能出现数据错误。
AtomicInteger atomicNum = new AtomicInteger(0);
if (atomicNum.get() != 5) {
    atomicNum.incrementAndGet();
}
```





# 线程池

## DEMO

> 三个线程顺序执行

- 方法1：使用'单例线程池'

```java
Runnable task01 = () -> System.out.println(LocalDateTime.now() + " Thread-T1");
Runnable task02 = () -> System.out.println(LocalDateTime.now() + " Thread-T2");
Runnable task03 = () -> System.out.println(LocalDateTime.now() + " Thread-T3");

ExecutorService pool = Executors.newSingleThreadExecutor();
pool.execute(task01);
pool.execute(task02);
pool.execute(task03); //任务启动顺序 和 提交顺序一致：1-2-3
pool.shutdown();
```

- 方法2：使用 join() 方法

```java
Thread t1 = new Thread(() -> {
    System.out.println(LocalDateTime.now() + " Thread-T1");
});

Thread t2 = new Thread(() -> {
    t1.join(); //等待，直到线程 t1 终止
    System.out.println(LocalDateTime.now() + " Thread-T2");
});

Thread t3 = new Thread(() -> {
    t2.join(); //等待，直到线程 t2 终止
    System.out.println(LocalDateTime.now() + " Thread-T3");
});

t3.start();
t2.start();
t1.start(); //无论启动顺序，执行顺序都是：1-2-3
```

> 自定义线程池

```java

```





#异步任务

```
1. 使用全局注解开启异步模式 @EnableAsync

2. 局部注解 @Async("*") 标记方法，调用过程中自动使用指定的线程池，并且不需要显示的创建线程去执行

3. @Async 也可标记在类上，标志该类中所有的方法均是异步方法

4. 异步方法和调用方法一定要写在不同的类中，同一类中不起效果
```

```java
@Async //异步注解，可配置自定义的线程池Bean，如 @Async("demoScheduler")
@EnableAsync //全局注解
@EnableScheduling
@Component
public class ScheduledTask {
    //... task1,2 同上
}
```






# ThreadLocal

> 基础介绍

```java
'ThreadLocal 用于存储线程局部变量，能够保证相同线程数据共享，不同线程数据隔离。'
即每个线程只能访问到自己的，多线程之间互不干扰，可有效防止本线程的变量被其它线程篡改。
官方建议，将 ThreadLocal 变量设置为 private static 访问级别。

本质是一个类似 Map 的数据结构，可看作：Map<当前线程的ThreadLocal对象,当前线程的局部变量>。

一个ThreadLocal对象只能存放当前线程的一个局部变量。所以，对多个局部变量需实例化多个ThreadLocal对象。

ThreadLocal中的数据不会随着线程结束而回收，必须手动 remove() 防止内存泄露。
```

```java
操作系统中，线程和进程数量是有上限的，确定线程和进程的唯一条件就是线程或进程id。
操作系统在回收线程或进程的时候，并不一定杀死。系统繁忙时,只会清空其栈内数据，然后重复使用。
所以，对于存储在 ThreadLocal 中的数据，如若不 remove()，则有可能在线程 t2 获取到 t1 的数据。
```

> Thread，ThreadLocal，ThreadLocalMap

```java
//Thread 中有个 ThreadLocal.ThreadLocalMap 类型的成员变量 threadLocals
public class Thread implements Runnable {
    ThreadLocal.ThreadLocalMap threadLocals = null;
}
```

```java
//ThreadLocalMap 是 ThreadLocal 的内部类，它是一个类似Map，它的Key是 ThreadLocal 类型对象！
public class ThreadLocal<T> {
    
    //通过 ThreadLocal 对象的set方法，把ThreadLocal对象自己当做key，放进了ThreadLoalMap中。
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
            ThreadLocalMap.Entry e = map.getEntry(this); //key为 this，而不是当前线程t
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
```

> hash冲突

在插入过程中，根据ThreadLocal对象的hash值，定位到table中的位置i，过程如下：

```properties
1、如果当前位置是空的，那么正好，就初始化一个Entry对象放在位置i上；

2、不巧，位置i已经有Entry对象了，如果这个Entry对象的key正好是即将设置的key，那么重新设置Entry中的value；

3、很不巧，位置i的Entry对象，和即将设置的key没关系，那么只能找下一个空位置；
```

> 典型应用 - hibernate

```java
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
```

> ThreadLocal：用于解决`不同线程间的数据隔离问题`，而不是多线程共享数据问题。

```java
//官方建议设置为 private static，用于关联线程上下文。可以设置默认值
// private static ThreadLocal<String> threadLocal = new ThreadLocal<>();
private static ThreadLocal<String> threadLocal = ThreadLocal.withInitial(() -> "666");

public static void main(String[] args) {
    new Thread(new ThreadTest().new MyRunnable()).start();
    // main: 777
    // Thread-0: 666
}

class MyRunnable implements Runnable {
    MyRunnable() {
        threadLocal.set("777"); //【易错处】这里还属于主线程
        System.out.println(Thread.currentThread().getName() + ": " + threadLocal.get());
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + ": " + threadLocal.get());
    }
}
```
> InheritableThreadLocal：ThreadLocal 子类，继承上下文线程环境的数据，拷贝一份给子线程

```java
private static ThreadLocal<String> threadLocal = new InheritableThreadLocal<>();

public static void main(String[] args) {
    threadLocal.set("777");

    System.out.println(Thread.currentThread().getName() + ": " + threadLocal.get());
    new Thread(() -> {
        //父线程中创建子线程，子线程继承主线程的 ThreadLocal 环境变量
        //此外，子线程的优先级属性 和 是否是守护线程属性 都和父线程相同。
        System.out.println(Thread.currentThread().getName() + ": " + threadLocal.get());
    }).start();
    
    // main: 777
    // Thread-0: 777
}
```

# CountDown...

> 基础概念

多线程通讯的一个辅助类型。相当于在一个门上加多个锁，当线程调用 await() 方法时，会检查门闩数量，如果门闩数量大于 0，线程会阻塞等待。当线程调用 countDown() 时，会递减门闩的数量。当门闩数量为 0 时，await() 阻塞线程可执行。

由于countDown() 方法可以用在任何地方，所以这里说的N个点，可以是N个线程，也可以是1个线程里的N个执行步骤。用在多个线程时，你只需要把这个CountDownLatch的引用传递到线程里。

> 常用方法

```java
//调用 await() 方法的线程会被挂起，它会等待直到 count 值为0才继续执行
public void await() throws InterruptedException { };

//和 await() 类似，只不过等待一定的时间后，count 值还没变为0的话也会继续执行
public boolean await(long timeout, TimeUnit unit) throws InterruptedException { };

//将count值减1
public void countDown() { };
```

> 使用场景

- `开始执行前，等待N个线程完成各自任务：`例如，应用程序启动类要确保在处理用户请求前，所有N个外部系统已经启动和运行了。
- `实现最大的并行性：`有时想同时启动多个线程，实现最大程度的并行性。
- `死锁检测：`可以使用n个线程访问共享资源，在每次测试阶段的线程数目是不同的，并尝试产生死锁。


> 开始执行前，等待N个线程完成各自任务

```java
//模拟100米赛跑，8名选手准备就绪，只等裁判一声令下。当所有人都到达终点时，比赛结束。
private static void doCountDownLatch() throws InterruptedException {
    int nThreads = 8;
    ExecutorService pool = Executors.newFixedThreadPool(nThreads); //8名选手

    CountDownLatch begin = new CountDownLatch(1); //开始的倒数锁
    CountDownLatch end = new CountDownLatch(nThreads); //结束的倒数锁

    for (int i = 0; i < nThreads; i++) {
        final int index = i + 1;
        Runnable run = () -> {
            try {
                begin.await(); //等待枪响
                System.out.println(LocalDateTime.now() + " START: " + index);
                TimeUnit.MILLISECONDS.sleep((long) (Math.random() * 10000));
                System.out.println(LocalDateTime.now() + " ARRIVED: " + index);
            } catch (InterruptedException e) {
            } finally {
                end.countDown(); //每个选手到达终点时，end就减一
            }
        };
        pool.submit(run);
    }
    System.out.println(LocalDateTime.now() + " --- GAME START");
    begin.countDown(); //begin减一，开始游戏
    end.await(); //等待end变为0，即所有选手到达终点
    System.out.println(LocalDateTime.now() + " --- GAME OVER!");
    pool.shutdown();
}
```

> 实现最大的并行性

```java
//【不推荐】以下是一般测试的并发代码，但不严谨，所有线程都是顺序创建，并不符合并发（至少在启动那一刻）
for (int i = 0; i < nThreads; i++) {
    pool.execute(() -> doWork());
}
```

```java
//启动20个线程往 ArrayList 里增加数据，每个线程增加100个，最后输出这个集合的长度
//如果 ArrayList 线程安全，最后结果应该是2000，但并不安全，所以结果应该是小于 2000 或出现下标越界
public void doTestWithCountDown() throws InterruptedException {
    List<Integer> list = new ArrayList<>(); //线程安全的 CopyOnWriteArrayList
    int nThreads = 20;
    ExecutorService pool = Executors.newFixedThreadPool(nThreads);
    CountDownLatch countDownLatch = new CountDownLatch(1);

    Runnable task = () -> {
        countDownLatch.await(); //在倒计时结束前，await将一直阻塞，保证不会有那个线程先执行
        
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
    };

    for (int i = 0; i < nThreads; i++) {
        pool.execute(task);        
    }
    countDownLatch.countDown(); //所有任务提交完毕后执行

    pool.shutdown();
    pool.awaitTermination(5, TimeUnit.SECONDS);
    System.out.println(list.size());
}
```

> 实现一容器，提供两方法：add + size。写两个线程，线程1添加10个元素到容器中，线程2实现监控元素的个数，当个数到5个时，线程2给出提示并结束

```java
static class MyContainer {
    int count;

    public void add() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1); //延时处理
        this.count++;
    }

    public int size() {
        return count;
    }

    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(5);
        MyContainer container = new MyContainer();

        new Thread(() -> {
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(LocalDateTime.now() + " 元素个数：" + container.size());
        }, "Thread-1").start();

        new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    //add()方法执行速度过快，必须保证 CountDownLatch 通讯完再做下一次add()
                    //所以在 count++ 之前（必须之前）做延时处理
                    container.add();

                    countDownLatch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Thread-2").start();
    }
}
```





<https://blog.csdn.net/m0_37125796/article/details/81105099>


