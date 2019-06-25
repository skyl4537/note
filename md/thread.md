[TOC]





#线程基础

## 基础概念

多线程的目的是为了更好的利用计算机的 CPU 资源。比如在一个进程中，如果只有一个线程（也叫主线程），那么如果当这个线程因为某种原因阻塞（等待用户输入数据等情况）的时候，那么相对应的这个进程也让出了 CPU 资源并暂停执行了。试想一下，如果在一个进程中添加多个线程，那么当这个进程中某个线程阻塞的时候，其余线程还可以继续执行，做它们自己的工作，这样的话计算机的利用效率就提高了。

> 线程创建

1. 继承于 Thread 类并且重写其 run 方法。不推荐使用，java 单继承，多实现。
2. 新建一个 Runnable 对象并将其作为一个参数传入 Thread 类的构造方法中。
3. 实现 Callable 接口，并使用线程池调用。
4. ​

```java
//区别 ① 和 ②
前者，每个线程执行自己的 run方法。
后者，多个.......同一个 run...。
```

> 线程启动

通过调用线程对象的 `start()` 方法来开启一个线程`（该方法只能被调用一次）`。线程开启后便进入就绪状态，但并不会马上执行，而是等待线程调度器的调度。一旦线程调度器调度了该线程之后，该线程便可获得 CPU 资源，然后进入运行状态，开始执行 run() 方法。run() 方法运行结束，该线程也随即终止。

```java
//区别 start() 和 run()
start，用来启动线程，真正实现多线程运行，无需等待 run方法体代码执行完毕，而继续执行下面的代码。

run  ，不会开启新的线程，直接在调用线程中执行 run()方法体中的内容，程序还是顺序执行。
```

> 线程暂停

通过调用线程对象的 `sleep(long millis)` 方法来让线程休眠指定的秒数，调用这个方法之后线程将会让出 CPU 进入休眠。休眠完成之后，线程并不会直接获得 CPU 资源，而是会进入就绪状态，等待线程调度器的调度来获取 CPU 资源。

> 线程退出

- 调用 `System.exit(0)` 或 `Runtime.getRuntime().exit(0)`，并且安全管理器允许程序退出。


- 所有的非守护线程结束运行，包括：线程中 run() 正常执行完毕，或者在 run() 执行过程中发生异常。

```java
//System.exit(0) 底层调用的还是 Runtime.getRuntime().exit(0)，都是用来结束当前运行的java虚拟机。
try {
    int i = Integer.parseInt("666");
    System.exit(0); //正常执行完毕，直接退出，将整个虚拟机的内容都停掉。
} catch (NumberFormatException e) {
    e.printStackTrace();
    System.exit(1); //捕获到异常，非正常退出（参数取值非0）
}
```
- 【官方推荐】用一个 boolean 变量来标记任务是否完成，在任务完成后直接退出循环 或 修改这个标记变量。

```java
public void run() {
    boolean isFinish = false; //记录线程任务是否完成
    
    while (!isFinish) {
        if (/*任务完成*/) {
            break; //或者 isFinish = true;
        } else {
            // do something ...
        }
    }
}
```
- 利用系统预定义的`中断标识：Thread.isInterrupted()`

```java
public void run() {
    while (!Thread.currentThread().isInterrupted()) {//获取当前线程对象的中断标志(true/false)
        if (/*任务完成*/) {
            Thread.currentThread().interrupt(); //实例方法，将线程对象的中断标志设置为 true
        } else {
            // do something ...
        }
    }
}
```
- `中断标识 存在的问题：`其本身不会影响线程的执行，但是和其他方法混用时，就有可能影响线程的执行。例如，和 sleep() 混用，如果当前线程处于中断状态，再调用 sleep() 方法后，不仅会抛出异常，而且还会打断当前的中断状态。

```java
new Thread(() -> {
    for (int i = 0; !Thread.currentThread().isInterrupted() && i < 3; i++) {
        if (i > 0) {
            Thread.currentThread().interrupt(); //大于0，中断标识设为 true
        }
        System.out.println("i: " + i);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            //中断状态的线程调用 sleep() 不仅会抛出此异常，并且还会打断 中断状态
            e.printStackTrace();
        }
    }
}).start();
```

```java
//结果输出
i: 0
java.lang.InterruptedException: sleep interrupted
    ... ...
java.lang.InterruptedException: sleep interrupted
    ... ...
i: 1
i: 2
```
> 线程优先级

每个线程都有一个优先级，默认为 5。线程的优先级越高，其越容易得到 CPU 资源，`但不是一定能得到 CPU 资源`。线程的优先级设置一定要在调用 start() 方法之前。

在一个线程中创建另一个新的线程，那么这个新线程的优先级默认和创建它的线程的优先级相同。并且，`在守护线程中默认创建的线程还是守护线程`。

```java
thread.setPriority(Thread.MAX_PRIORITY); //设置线程的优先级，在 start() 之前调用
thread.start();

public final static int MIN_PRIORITY = 1;
public final static int NORM_PRIORITY = 5; //系统预设的三种优先级，默认为5
public final static int MAX_PRIORITY = 10;
```

> 守护线程

**守护线程**：是为非守护线程（用户线程）服务的；jvm停止不用等守护线程执行完毕。

非守护线程执行完毕，则jvm退出，此时守护线程也会被强制结束。所以，一些重要的任务不该放在守护线程中。

- 主线程 是非守护线程，优先级为默认的 5。
- 非守护线程 也叫用户线程，由用户创建。


- 守护线程 是指在程序运行的时候在后台提供一种通用服务的线程。如gc。
- 其中，主线程和守护线程一起销毁；主线程和非守护线程互不影响。

```java
//守护线程 -> 每隔 1 秒打印一次 i 的值，循环一共需要执行 5 次，也就是打印 5 次 i 的值。
//主线程   -> 休眠 3 秒后，打印一句结束语后结束。
public static void daemonThreadTest() {
    Thread daemonThread = new Thread(() -> {
        for (int i = 0; i < 5; i++) {
            System.out.println(Thread.currentThread().getName() + "打印: " + i);
            TimeUnit.SECONDS.sleep(1);
        }
    }, "守护线程");
    
    daemonThread.setDaemon(true); //守护线程在调用 start() 方法之前设置，否则抛异常
    daemonThread.start();
}

public static void main(String[] args) {
    System.out.println("主线程-START");
    daemonThreadTest();
    TimeUnit.SECONDS.sleep(3);
    System.out.println("主线程-END!");
}
```

```java
主线程-START       主线程-START       主线程-START 
守护线程打印: 0     守护线程打印: 0     守护线程打印: 0
守护线程打印: 1     守护线程打印: 1     守护线程打印: 1
守护线程打印: 2     守护线程打印: 2     守护线程打印: 2
主线程-END!        守护线程打印: 3     主线程-END!
                  主线程-END!        守护线程打印: 3
```

```java
//为什么守护线程没有执行完成（任务是打印 5 次，但是结果只打印了 3~4 次）？
在这个程序中，主线程是唯一的非守护线程。主线程执行完毕，则jvm退出，即守护线程也会被强制结束。所以一些重要的任务不应该放在守护线程中完成。
```

```java
//为什么后两个运行结果守护线程会多打印一次 i 值？
这其实是线程之间的调度导致的。
在守护线程中先打印 i 的值然后再进行休眠，当主线程休眠完 3 秒，守护线程也正好休眠完并且在准备下一次的打印。如果在主线程打印'主线程-END!'这句话之后，并且在 main()结束之前，CPU 执行了守护线程的话，那么守护线程就会执行第 4 次打印。即出现 '守护线程打印: 3' 这一行。这个当然也是有概率的。
```
## 生命周期

![](assets/thread0.png)

![](assets/thread1.png)

> 线程状态

- `新生状态` 用 new 关键字建立一个线程对象后，该线程对象就处于新生状态。处于新生状态的线程有自己的内存空间，通过调用 start 方法进入就绪状态。
- `就绪状态` 处于就绪状态线程具备了运行条件，但还没分配到CPU，处于线程就绪队列，等待系统为其分配CPU。当系统选定一个等待执行的线程后，它就会从就绪状态进入执行状态，该动作称之为 CPU调度。
- `运行状态` 处于运行状态的线程执行 run方法体代码，直到等待某资源而阻塞 或 完成任务而死亡。如果在给定的时间片内没有执行结束，就会被系统给换下来回到就绪状态，等待下一次线程调度器的调度。
- `阻塞状态` 处于运行状态的线程在某些情况下，如执行了 sleep方法，或等待I/O设备等资源，将让出CPU并暂时停止自己的运行，进入阻塞状态。在阻塞状态的线程不能进入就绪队列。只有当引起阻塞的原因消除时，如睡眠时间已到，或等待的I/O设备空闲下来，线程便转入就绪状态，重新到就绪队列中排队等待，被系统选中后从原来停止的位置开始继续运行。
- `死亡状态` 死亡状态是线程生命周期中的最后一个阶段。线程死亡的原因有三个。①是线程被强制性地终止，如通过执行 stop来终止一个线程【不推荐使用】；②是正常运行的线程完成了它的全部工作，③是线程抛出未捕获的异常。

> 进入阻塞状态的4种情况（都会让出CPU）

```java
sleep(); --> 抱着 资源锁 睡大觉，自己不用，也不给别人。
wait();  --> 放开 资源锁，自己站旁边看别人执行。
join();  --> 阻塞指定线程等到另一个线程完成以后再继续执行
其他操作  --> 如IO中的read()，write();
```

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
sleep()方法时间到，线程直接恢复到就绪状态，再次获得线程调度才能恢复到运行状态。 sleep()方法如果没有设置时间，就必须通过 notify()或者 notifyAll()来唤醒，才会进入锁池，再次获得对象锁才会进入运行状态。
```
```java
每个对象都有一个锁来控制同步访问， synchronized 关键字可以和对象的锁交互，来实现同步方法或同步块。 sleep()方法正在执行的线程主动让出CPU（然后CPU就可以去执行其他任务），在sleep指定时间后CPU再回到该线程继续往下执行(注意：sleep方法只让出了CPU，而并不会释放同步资源锁！！！)；
```
```java
wait()方法则是使当前线程让出CPU资源，释放同步资源锁，以便线程获取到资源锁而运行，只有调用了 notify()方法，wait状态才会解除，可以去参与竞争同步资源锁，进而得到执行。（注意：notify的作用相当于叫醒睡着的人，而并不会给他分配任务，就是说notify只是让之前调用wait的线程有权利重新参与线程的调度）；
```
> 锁池 和 等待池

```java
如果线程调用了对象的wait()方法，那么线程就处于该对象的'等待池'中，等待池中的线程不会去争夺锁的拥有权。

当线程调用了该对象的 notify()方法或者 notifyAll()方法，被唤醒的线程进入'锁池'，准备争夺锁的拥有权。假如某个线程没有争夺到锁，它仍旧停留在锁池中等待下一次的争夺，只有再次调用 wait()方法才会进入等待池中。

以上线程拿到锁要干嘛？ '拿到锁，进入就绪状态，等待CPU时间片开始运行'。
```



> DEMO：sleep 和 wait

```java
public static void main(String[] args) throws InterruptedException {
    new Thread(new ThreadTest().new Thread01()).start();
    TimeUnit.SECONDS.sleep(2);
    new Thread(new ThreadTest().new Thread02()).start();
}

private class Thread01 implements Runnable {
    @Override
    public void run() {
        //Thread1 和 Thread2 内部run()方法要用同一对象作为监视器，如果用this则不是同一对象
        synchronized (ThreadTest.class) {
            System.out.println(LocalDateTime.now() + " Thread01 START...");
            //释放锁的两种方式
            // (1).程序自然离开监视器的范围，即离开 synchronized 同步代码块
            // (2).在 synchronized 同步代码块中调用监视器对象的 wait()方法
            ThreadTest.class.wait();
            System.out.println(LocalDateTime.now() + " Thread01 END!!!");
        }
    }
}

private class Thread02 implements Runnable {
    @Override
    public void run() {
        synchronized (ThreadTest.class) {
            System.out.println(LocalDateTime.now() + " Thread02 START...");

            //notify()方法并不释放锁。即使thread2调用了下面的sleep方法休息1s，但thread1仍然不会执行
            //因为thread2没有释放锁，所以Thread1得不到锁而无法执行
            ThreadTest.class.notify();
            TimeUnit.SECONDS.sleep(1);
            System.out.println(LocalDateTime.now() + " Thread02 END!!!");
        }
    }
}
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
> 线程状态-DEMO

```java
public void doThreadState() throws InterruptedException {
    Thread thread = new Thread(() -> {
        boolean isFinish = false;
        long l = System.currentTimeMillis();

        while (!isFinish) {
            if (System.currentTimeMillis() - l == 500) {
                TimeUnit.SECONDS.sleep(1);
            }
            if (System.currentTimeMillis() - l == 2500) {
                synchronized (ThreadTest.class) {
                    ThreadTest.class.wait();
                }
            }
            if (System.currentTimeMillis() - l > 4500) {
                isFinish = true;
            }
        }
    });

    System.out.println("S" + " - " + thread.getState());
    for (int i = 0; i < 7; i++) {
        if (0 == i) thread.start();

        if (5 == i) {
            synchronized (ThreadTest.class) {
                ThreadTest.class.notifyAll();
            }
        }
        System.out.println(i + " - " + thread.getState());
        TimeUnit.MILLISECONDS.sleep(1000);
    }

    // S - NEW
    // 0 - RUNNABLE
    // 1 - TIMED_WAITING
    // 2 - RUNNABLE
    // 3 - WAITING
    // 4 - WAITING
    // 5 - BLOCKED
    // 6 - TERMINATED
}
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

> 线程的私有工作内存和主内存之间的联系：

线程私有工作内存 类比于 CPU和主内存之间的高速缓存，特点：`读写速度比内存快，接近于CPU的速度`。

```
Java 线程只能对其私有工作内存进行直接的IO读取和写入操作，而不能对主内存进行直接的IO操作。
主内存对所有的 Java 线程都可见，即所有的 Java 线程都可以通过其工作内存来间接的修改主内存中的数据。
线程的工作内存只对其对应的 Java 线程可见，不同的 Java 线程不共享其工作内存。
```

> 线程修改主内存数据，通过以下步骤：

```
通过工作内存读取主内存中的变量值，并拷贝一份副本。
线程修改工作内存中的副本值。
工作内存将修改后的结果写入到主内存中。
```

> volatile：用于保证数据同步，即可见性。禁止指令重排。

`可见性`：多个线程访问同一个变量时，一个线程修改了这个变量的值，其他线程能够立即看到修改后的值。

```java
private /*volatile*/ static int num = 5;

private void doVolatile() {
    new Thread(() -> num++, "Thread-01").start();
    new Thread(() -> System.out.println(num), "Thread-02").start(); //5 或 6
}

//根据线程修改内存数据的3个步骤，可能出现：线程-01在执行完第2个步骤之后，CPU时间划分给线程-02，
//即，线程-01修改的值没有真正的同步到主内存中，导致线程-02从主内存中读取数据还是修改之前的值。
//volitale 关键字提供了一个功能，就是被其修饰的变量在被修改后会被强制刷入到主内存中。
```

```java
在 CPU 计算过程中，会将计算过程需要的数据加载到 CPU 计算缓存中，当 CPU 计算中断时，有可能刷新缓存，重新读取内存中的数据。在线程运行的过程中，如果某变量被其他线程修改，可能造成数据不一致的情况，从而导致结果错误。

volatile 修饰的变量是线程可见的，当 JVM 解释 volatile 修饰的变量时，会通知 CPU，在计算过程中，每次使用变量参与计算时，都会检查内存中的数据是否发生变化，而不是一直使用 CPU 缓存中的数据，可以保证计算结果的正确。

volatile 只是通知底层计算时，CPU 检查内存数据，而不是让一个变量在多个线程中同步。
```

>ReentrantLock：重入锁，建议应用的同步方式。相对效率比 synchronized 高。量级较轻。

```java
synchronized 在 JDK1.5 版本开始，尝试优化。到 JDK1.7 版本后，优化效率已经非常好了。在绝对效率上，不比 reentrantLock 差多少。
使用重入锁，'必须必须必须' 手工释放锁标记。一般都是在 finally 代码块中定义释放锁标记的 unlock 方法。
```



> volitale、synchronized、RetreenLock



## 同步机制

多个线程同时操作同一资源，容易出现线程安全问题。需要让线程同步，保证数据安全（确保资源在某一时刻只被一个线程使用）。

> 两种实现方案

```java
 synchronized(obj){ } //同步代码块
```

```java
private synchronized void makeWithdrawal(int amt) { } //同步方法
```

> 同步监视器

- synchronized (obj){ }中的 `obj` 称为同步监视器
- 同步代码块中同步监视器可以是任何对象，但是推荐使用共享资源作为同步监视器
- 同步方法中无需指定同步监视器，因为同步方法的同步监视器是this，也就是该对象本事

```java
//同步监视器的执行过程
• 第一个线程访问，锁定同步监视器，执行其中代码
• 第二个线程访问，发现同步监视器被锁定，无法访问
• 第一个线程访问完毕，解锁同步监视器
• 第二个线程访问，发现同步监视器未锁，锁定并访问
```





## synchronized

> synchronized 锁什么？锁对象。

```java
同步代码块 中可以使用临界资源，即
同步代码块 和 同步方法，锁的是调用该方法的当前对象。 this
静态方法中的同步代码块 和 静态同步方法，锁的是当前类型的类对象。如 Test.class
```

>加锁的目的： 就是为了保证操作的原子性

```java
int sum;

private void demo1() {
    for (int i = 0; i < 500; i++) {
        new Thread(() -> {
            //synchronized (this) {
                System.out.println(LocalTime.now() + " - " + sum++); //不加同步 <500
            //}
        }).start();
    }
}
```

>`同步方法只阻塞使用同一锁资源的同步方法`。不阻塞其他线程调用非同步方，或调用其他锁资源的同步方法。

```java
private void demo2() {
    ThreadDemo demo = new ThreadDemo();
    new Thread(() -> demo.m20()).start();
    new Thread(() -> demo.m21()).start(); //m1 和 m0 使用相同的资源锁，故被阻塞
    new Thread(() -> demo.m22()).start(); //非同步方法
    new Thread(() -> demo.m23()).start(); //m3 和 m0 使用不同的资源锁
    
    // 20:46:28.825 START 0
    // 20:46:28.826 START 3
    // 20:46:28.858 START 2 --> 以上 3 个方法同时执行，未被阻塞
    // 20:46:31.826 START 1 --> 被阻塞
}

synchronized void m20() { //同步方法 比 同步代码块，更加重量级
    System.out.println(LocalTime.now() + " START 0");
    TimeUnit.SECONDS.sleep(3);
}

void m21() {
    synchronized (this) {
        System.out.println(LocalTime.now() + " START 1");
        TimeUnit.SECONDS.sleep(3);
    }
}

void m22() {
    System.out.println(LocalTime.now() + " START 2");
    TimeUnit.SECONDS.sleep(3);
}

final Object obj = new Object(); //作为同步资源锁，最好 final

void m23() {
    synchronized (obj) {
        System.out.println(LocalTime.now() + " START 3");
        TimeUnit.SECONDS.sleep(3);
    }
}
```

> 同步方法`只能保证当前方法的原子性`，不能保证多个业务方法之间的互相访问的原子性。

注意：在商业开发中，多方法要求结果访问原子操作，需要多个方法都加锁，且锁定同一个资源。

```java
private void demo3() throws InterruptedException {
    ThreadDemo demo = new ThreadDemo();
    for (int i = 0; i < 2000; i++) {
        new Thread(() -> demo.m30()).start();
    }
    demo.m31();

    TimeUnit.SECONDS.sleep(3);
    demo.m31();
    
    // 09:31:15.751 - 148 //多个方法之间不保证原子性
    // 09:31:18.753 - 2000
}

void m30() {
    TimeUnit.MILLISECONDS.sleep(1); //模拟网络等耗时操作
    synchronized (this) { //同步保证原子性
        this.num += 1;
    }
}

void m31() { 
    //synchronized (obj) {
        System.out.println(LocalTime.now() + " - " + this.num);
    //}
}
```

> `锁可重入(1)`：同一个线程，多次调用同步代码，锁定同一个锁对象，可重入。

```java
synchronized void demo4() throws InterruptedException {
    System.out.println(LocalTime.now() + " START 4");
    TimeUnit.SECONDS.sleep(1);
    m40();
    System.out.println(LocalTime.now() + " END 4");
    
    // 09:52:01.011 START 4
    // 09:52:02.014 START 40 --> 方法40 和 方法4 使用的是同一个资源锁，同一个线程从 4 进入到 40
    // 09:52:03.014 END 40   --> 不会阻塞，只会在资源锁的标记上+1，出了方法 01 同步范围后，在资源锁
    // 09:52:03.014 END 4    --> 的标记上-1，直至减到0，然后释放资源锁
}

synchronized void m40() throws InterruptedException {
    System.out.println(LocalTime.now() + " START 40");
    TimeUnit.SECONDS.sleep(1);
    System.out.println(LocalTime.now() + " END 40");
}
```

> `锁可重入(2)`：同步方法の继承，子类同步方法覆盖父类同步方法。可以指定调用父类的同步方法。

```java
synchronized void demo5() throws InterruptedException {
    System.out.println(LocalTime.now() + " START SUPER");
    TimeUnit.SECONDS.sleep(1);
    
    // 11:08:44.986 START SUB  --> 子类同步方法 和 父类的同步方法 使用的都是同一个资源锁
    // 11:08:44.986 START SUPER
}

static class SubThreadDemo extends ThreadDemo {
    
    synchronized void demo5() throws InterruptedException { //子类同步方法
        System.out.println(LocalTime.now() + " START SUB");
        super.demo5();
    }
}
```

> `锁与异常`：当同步方法中发生异常时，自动释放锁资源。不会影响其他线程的执行。

```java
private void demo6() {
    for (int i = 0; i < 3; i++) {
        int index = i;

        new Thread(() -> {
            synchronized (ThreadDemo.class) {
                System.out.println(LocalTime.now() + " - " + index);
                TimeUnit.SECONDS.sleep(1);
                
                if (1 == index) {
                    int num = 1 / 0;
                }
            }
        }).start();
    }
    
    // 11:39:47.878 - 1
    // Exception in thread ....
    // 11:39:48.953 - 2 --> 释放同步锁的两种方式：（1）显示调用锁资源的 wait()方法
    // 11:39:48.953 - 2 --> （2）方法执行出了同步代码块，包括正常执行完，或者异常跳出
}
```

> `锁对象的变更`：同步代码一旦加锁后，那么会有一个临时的锁引用执行锁对象，和真实的引用无直接关联。在锁未释放之前，修改锁对象引用，不会影响同步代码的执行。

```java
Object obj = new Object(); //作为同步资源锁，最好 final

private void m01() {
    synchronized (obj) {
        while (true) {
            System.out.println(LocalTime.now() + " - " + Thread.currentThread().getName());
            TimeUnit.SECONDS.sleep(1);
        }
    }
}

private void demo0() throws InterruptedException {
    new Thread(() -> m01(), "Thread-01").start();

    TimeUnit.SECONDS.sleep(2);
    obj = new Object();
    new Thread(() -> m01(), "Thread-02").start();
    
    // 16:20:32.612 - Thread-01
    // 16:20:33.614 - Thread-01
    // 16:20:34.571 - Thread-02 --> 锁对象的变更，不影响线程-01 的执行，其使用的是线程私有内存中
    // 16:20:34.614 - Thread-01 --> 锁对象，和主内存中的对象无关，即线程-01和-02 不是同一个锁对象
}
```

>在定义同步代码块时，`不要使用常量对象作为锁对象`。

```java
String str0 = "hello";
String str1 = "hello";

private void m101() {
    synchronized (str0) {
        while (true) {
            System.out.println(LocalTime.now() + " - " + Thread.currentThread().getName());
            TimeUnit.SECONDS.sleep(1);
        }
    }
}

private void m102() {
    synchronized (str1) {
        while (true) {
            System.out.println(LocalTime.now() + " - " + Thread.currentThread().getName());
            TimeUnit.SECONDS.sleep(1);
        }
    }
}

private void demo10() {
    new Thread(() -> m101(), "Thread-01").start();
    new Thread(() -> m102(), "Thread-02").start();
    
    // 16:34:05.468 - Thread-01
    // 16:34:06.481 - Thread-01 --> 只有线程-01执行，02没有执行。这说明 2 个线程使用同一个锁对象
    // 16:34:07.481 - Thread-01 --> 进一步说明 str0 和 str1 是同一个对象
}
```

> `volatile` 修饰的变量，在计算过程中，每次使用变量参与计算时，都会检查内存中的数据是否发生变化，而不是一直使用 CPU 缓存中的数据，可以保证计算结果的正确。`但不是让一个变量在多个线程中同步。`

```java
volatile boolean flag = false;

private void demo7() throws InterruptedException {
    new Thread(() -> {
        System.out.println(LocalTime.now() + " START");
        while (!flag) { }
        System.out.println(LocalTime.now() + " END");
    }).start();

    TimeUnit.SECONDS.sleep(1);
    flag = true;
    
    // 11:49:56.659 START --> 不写 volatile，则while()循环一直执行，每次使用的都是线程私有内存中的值
    // 11:49:57.764 END   --> 加上 volatile，则会终止循环，因为每次使用都会获取最新的值
}
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

## 线程组

线程池：为了在子线程中处理大量的任务，同时又避免频繁的创建和销毁线程带来的系统资源开销而产生的。

线程组：为了方便和统一多个线程的管理而产生的。

>线程组中不仅可以包含线程，也可以包含线程组。

有点类似于文件夹的概念，线程对应的就是文件，线程组对应的就是文件夹，文件夹中不仅可以包含文件，也可以包含文件夹。

需要注意的是，当新建一个线程 / 线程组之后，如果你没有给这个新建的线程 / 线程组指定一个父线程组，那么其默认会将当前执行创建线程 / 线程组代码的线程所属的父线程组作为新的线程 / 线程组的父线程组。

**同时，一个线程只有调用了其 start 方法之后，其才真正算是被添加到了对应的线程组中。**

```java
public static void stopThreadsByThreadGroup() throws InterruptedException {
    ThreadGroup group = new ThreadGroup("线程组1");

    for (int i = 0; i < 10; i++) {
        new Thread(group, () -> { //线程组添加 10 个线程
            while (!Thread.currentThread().isInterrupted()) { //中断标识
                System.out.println(Thread.currentThread().getName() + " RUNNING");
            }
        }, "Thread-" + i).start();
    }

    TimeUnit.SECONDS.sleep(1); //1s后，中断整个线程组
    group.interrupt();
}
```



























# 线程池

## 基础概念

> 线程池优点

    (0).降低资源消耗：通过重复利用已创建的线程，降低线程创建和销毁造成的消耗
    (1).提高响应速度：当任务到达时，任务可以不需要等待线程的创建就能立即执行
    (2).提高可管理性：线程是稀缺资源，如果无限制的创建，不仅会消耗系统资源，还会降低系统的稳定性，使用线程池可以进行统一的分配，调优和监控。
> 生命周期

```
线程池是一个进程级的重量级资源。默认生命周期和 jvm 一致。即从开启线程池开始，到 jvm 关闭为止。

如果手工调用 shutdown() 方法，那么线程池执行所有的任务后，自动关闭。
```

> 5种状态

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
```java
//runState：线程池的状态，volatile 保证线程之间的可见性。
RUNNING    ：线程池创建后,初始处于 RUNNING 状态，接受新的任务，处理队列任务
SHUTDOWN   ：调用 shutdown()后的状态。不再接受新的任务，处理队列任务
STOP       ：调用 shutdownNow()....。不再接受新任务，不处理队列任务，并尝试中断正在执行的任务线程

TERMINATED ：线程池已结束，即 terminated()方法执行完。
```

> 线程池初始化

默认，线程池创建之后，池中是没有线程的，需要接收任务之后才会创建线程。如果需要`预创建线程`，可通过以下两个方法实现。

```java
prestartCoreThread();     //初始化  1   个核心线程
prestartAllCoreThreads(); //初始化 core 个核心线程
```
> 线程池关闭

```java
shutdown(); //不再接受新的任务，处理队列任务，以及当前任务

shutdownNow(); //不再接受新任务，不处理队列任务，并尝试中断正在执行的任务线程
```

> `区别：execute() 和 submit()`

```java
//可以接受的任务类型
execute(); Runnable接口
submit();  Callable接口，Runnable接口，Runnable接口 + 返回值Result
```

```java
//有无返回值
execute(); 没有返回值
submit();  有返回值，所以需要返回值的时候必须使用submit
```

```java
//异常处理
execute(); 参数是Runnable接口的实现，所以只能使用 try-catch 来捕获Exception
submit();  不管提交的是Runnable还是Callable类型的任务，如果不对返回值Future调用get()方法，都会吃掉异常。
```

> 



## 核心相关

> 核心参数

```java
public ThreadPoolExecutor(int corePoolSize, 
                          int maximumPoolSize, 
                          long keepAliveTime,
                          TimeUnit unit, BlockingQueue<Runnable> workQueue,
                          ThreadFactory threadFactory,
                          RejectedExecutionHandler handler);
```

```java
//poolSize：当前线程池中的线程数
```

```java
//corePoolSize：线程池中的最大核心线程数
默认，线程池创建后池中线程总数为 0，只有在有任务提交到线程池中时才会创建线程。当然，也可以预创建线程。

每次新来一个任务，会创建一个线程去执行，直到 poolSize = corePoolSize
当'poolSize > corePoolSize'时，新提交的任务会被放进任务缓存队列 queue
```

```java
//maximumPoolSize：线程池中允许创建的最大线程数
表示在线程池中最多能创建多少个线程，非核心线程数 = maximumPoolSize - corePoolSize
```

```java
//keepAliveTime：线程池中 非核心线程 允许闲置的最长时间
超过这个时间的'非核心线程'将会被回收，对于任务很多并且每个任务处理时间较短的的情况，可以适当增大这个参数来提高线程利用率。

当设置 allowCoreThreadTimeOut(true)时，keepAliveTime 参数也会作用到核心线程上，即 corePoolSize 也会被回收。
```

```java
//unit：keepAliveTime 的时间单位,7种取值
DAYS，HOURS，MINUTES，SECONDS，MILLISECONDS（毫秒），MICROSECONDS（微妙），NANOSECONDS（纳秒）
```

```java
//threadFactory：线程工厂，主要用来创建线程
```

```java
//workQueue：储存任务的阻塞队列（线程安全），用于提交和存储待处理任务
(1).poolSize <  corePoolSize，添加新线程
(2).poolSize >= corePoolSize，将请求加入缓冲队列
(3).poolSize <  maximumPoolSize，缓冲队列已满，则扩充 corePoolSize 至 maximumPoolSize
(4).poolSize >= maximumPoolSize，请求无法加入缓冲队列，任务将被拒绝
```

```java
//handler：任务拒绝策略。以下两种情况，会触发：
(1).等待队列已满 && poolSize = maximumPoolSize
(2).调用 shutdown()，会等待线程池里的任务执行完毕，才真正 SHUTDOWN。在等待间，会拒绝新任务
```

```java
AbortPolicy //默认方式，丢弃新的任务，并抛出异常 RejectedExecutionException

DiscardPolicy //丢弃任务，但不抛出异常。会导致被丢弃的任务无法再次被执行

DiscardOldestPolicy //丢弃最旧的未处理请求，然后执行当前提交的任务

CallerRunsPolicy //直接在 execute() 的调用线程（可能是主线程）中运行被拒绝的任务
                 //执行完之后,尝试将下一个任务添加到线程池中,可有效降低向线程池内添加任务的速度
```
> 常见阻塞队列

```java
//队列的三种通用策略：直接提交，无界队列，有界队列
LinkedBlockingQueue
  - 常用，基于链表的 FIFO 队列（先进先出），创建时不指定大小，则使用默认值. Integer.MAX_VALUE

ArrayBlockingQueue
  - 基于数组的 FIFO 队列，创建时必须指定大小。
  - 当队列为空，消费者线程被阻塞；当队列装满，生产者线程被阻塞

SynchronousQueue
  - 直接提交，不进行缓存，直接新建一个线程来执行任务

PriorityBlockingQueue
  - 按照'优先级'进行排序的/*无限队列*/，优先级最高的元素将始终排在队列的头部
  - 存放在其中的元素必须 implements Comparable，这样才能通过实现 compareTo() 进行排序
```



> 核心方法

```java
public class ThreadPoolExecutor 
        extends [AbstractExecutorService implements (ExecutorService extends Executor)] {

    // 在 Executor 中声明的方法，在 ThreadPoolExecutor 进行了具体的实现
    // 通过这个方法可以向线程池提交一个任务，交由线程池去执行
    execute();

    // 在 ExecutorService 中声明的方法，在 AbstractExecutorService 就已经有了具体的实现
    // 在 ThreadPoolExecutor 中并没有对其进行重写
    // 也是用来向线程池提交任务的，但和 execute() 不同，它能够返回任务执行的结果
    // 底层还是调用 execute()，只不过利用了 Future 来获取任务执行结果
    submit();

    //关闭线程池。不会接收新的任务，但会处理阻塞队列中的任务，以及当前正在执行的任务。
    void shutdown();
    
    //立刻关闭线程池。即不会接收新的任务，也不处理阻塞队列中的任务，甚至还会尝试中断正在执行的任务
    //返回值list为 阻塞列表中未被执行的任务
    List<Runnable> shutdownNow();
}
```

> 线程池扩充

```java
//poolSize  < corePoolSize
新来任务，就会创建一个线程去执行这个任务
```

```java
//poolSize > corePoolSize && 缓冲队列未满
新来任务，尝试将其添加到任务缓存队列当中，等待空闲线程将其取出去执行
```

```java
//poolSize > corePoolSize && 缓冲队列已满 && poolSize < maximumPoolSize
新来任务，任务缓存队列已满，则会尝试创建新的线程（即扩充 corePoolSize）去执行这个任务
```

```java
//poolSize > corePoolSize && 队列已满 && poolSize = maximumPoolSize
新来任务，任务缓存队列已满，corePoolSize 已扩充至 maximumPoolSize，则会采取'任务拒绝策略'进行处理
```

```java
当高峰期已过，如果某线程空闲时间超过 keepAliveTime，线程将被终止，直至 poolSize <= corePoolSize。

如果设置了 allowCoreThreadTimeOut(true)，那么核心线程也会被终止（默认不会），直至 poolSize=0。
```

>举个栗子：corePoolSize = 10，maximumPoolSize = (10+5)

```java
假如有一个工厂，工厂里面有 10 个工人，每个工人同时只能做一件任务。

因此，只要当10个工人中有工人是空闲的，来了任务就分配给空闲的工人做。当10个工人都有任务在做时，如果还来了任务，就把任务进行排队等待。

如果新任务增长的速度远大于工人做任务的速度，那么此时工厂主管可能会想补救措施，如再招 5 个临时工人，然后就将任务也分配给这 5 个临时工人做

如果说 （10+5） 个工人做任务的速度还是不够，此时工厂主管可能就要考虑不再接收新的任务或者抛弃前面的一些任务了

当这 （10+5） 个工人中有人空闲时，而新任务增长的速度又比较缓慢，工厂主管可辞掉 5 个临时工。只保持原来的10个工人，毕竟请额外的工人是要花钱
```

## 容量相关

>动态调整容量

当下列参数从小变大时，ThreadPoolExecutor 进行线程赋值，还可能立即创建新的线程来执行任务

```java
setCorePoolSize(); //设置核心池大小

setMaximumPoolSize(); //设置线程池最大线程数目大小
```

> 容量合理大小

```java
int cpuSize = Runtime.getRuntime().availableProcessors(); //获取cpu核心数

//对于每天执行一次的低频任务，核心线程数设为 0。

//对于CPU密集型应用, 最大线程数设为 N+1.
//....IO........., ........... 2N+1.（N为CPU总核数）
```
```java
/**
 * 假设系统每秒任务数为500~1000，每个任务耗时0.1秒，最大响应时间2s。
 * tasks=500-1000; taskcost=0.1; resptime=1.
 *
 * core = tasks/(1/taskcost) = tasks*taskcost = (500~1000)*0.1 = 50~100, core应该大于50
 * 然后根据8020原则,即80%情况下,每秒任务数小于1000*20%=200,那么 core=20
 * 一般,处理请求数 = (10~18) * core;
 * 
 * queue = (core/taskcost)*resptime = 20/0.1*2 = 400
 * 切记不能使用默认值Integer.MAX_VALUE,这样队列会很大,线程数只会保持在corePoolSize大小. 
 * 当任务陡增时,不能新开线程来执行,响应时间会随之陡增.
 * 
 * max = (max(tasks) - queue)/(1/taskcost) = (1000-400)/10 = 60
 * 
 * 以上都是理想值,实际情况下要根据机器性能来决定. 如果在未达到最大线程数的情况机器cpu load已经满了,
 * 则需要通过升级硬件和优化代码,降低taskcost来处理.
 */
```
## DEMO

> 当 queue 已满，新来一个 task，恰好 core 有一个空闲，哪种情况正确：`（1）`

- （1）空闲 core 直接执行新来的 task
- （2）空闲 core 取出 queue 头部的任务执行，而将新来的任务 task 放入队尾

解答：有空闲 core 就使用，没有则加入队列（队列未满）或新建线程（队列已满），所以选(1)。从以下 DEMO 中的 Task-5，Task-6 执行先于 Task-3，Task-4 可以验证
>获取线程池属性

```java
public void test() throws InterruptedException {
    ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 4, 200,  //线程池最大容量 4+2
                                  TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(2));
    printPoolProperty(executor); //任务开始执行前，获取线程池属性
    for (int i = 1; i <= 7; i++) {
        int taskNum = i;
        try {
            executor.submit(() -> {
                System.out.println(LocalTime.now() + " - "
                        + Thread.currentThread().getName() + " START " + taskNum);
                TimeUnit.SECONDS.sleep(2);
            });
            printPoolProperty(executor); //任务添加后，获取线程池属性
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Thread.sleep(6 * 1000); //所有任务执行完毕后，获取线程池属性
    printPoolProperty(executor);
    executor.shutdown();
}

private static void printPoolProperty(ThreadPoolExecutor executor) {
    System.out.println(LocalTime.now() + " - "
                       + "poolSize: " + executor.getPoolSize()
                       + "; queueSize: " + executor.getQueue().size()
                       + "; completedTask: " + executor.getCompletedTaskCount());
}
```
```java
20:22:59.169 - poolSize: 0; queueSize: 0; completedTask: 0 //初始状态

20:22:59.192 - poolSize: 1; queueSize: 0; completedTask: 0 //Task-1
20:22:59.192 - poolSize: 2; queueSize: 0; completedTask: 0 //Task-2.(poolSize = core)

20:22:59.193 - poolSize: 2; queueSize: 1; completedTask: 0 //Task-3
20:22:59.193 - poolSize: 2; queueSize: 2; completedTask: 0 //Task-4.(queue 已满)

20:22:59.193 - poolSize: 3; queueSize: 2; completedTask: 0 //Task-5.(扩充 poolSize -> max)
20:22:59.193 - poolSize: 4; queueSize: 2; completedTask: 0 //Task-6.(poolSize = max)

20:22:59.193 - pool-1-thread-2 START 2
20:22:59.196 - pool-1-thread-1 START 1
20:22:59.196 - pool-1-thread-3 START 5
20:22:59.196 - pool-1-thread-4 START 6

java.util.concurrent.RejectedExecutionException: ...       //Task-7.(任务拒绝策略)

20:23:01.194 - pool-1-thread-2 START 3
20:23:01.197 - pool-1-thread-3 START 4
20:23:05.195 - poolSize: 2; queueSize: 0; completedTask: 6 //所有任务完毕后，poolSize = core
```

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
public class MyThreadPool {
    private int nThreads = 5; //默认的初始线程数
    private TaskThread[] taskThreads; //工作线程
    private final List<Runnable> taskQueue; //阻塞队列

    private MyThreadPool() {
        taskThreads = new TaskThread[nThreads];
        taskQueue = new CopyOnWriteArrayList<>(); //线程安全的list

        for (int i = 0; i < nThreads; i++) {
            taskThreads[i] = new TaskThread();
            taskThreads[i].start(); //预创建好工作线程
        }
    }

    private static class ThreadHolder {
        private static MyThreadPool pool = new MyThreadPool();
    }

    public static MyThreadPool getInstance() { //单例 --> 静态内部类方式
        return MyThreadPool.ThreadHolder.pool;
    }

    //提交任务 ---> 其实只是把任务加入任务队列，什么时候执行有线程池管理器决定
    public void execute(Runnable r) {
        synchronized (taskQueue) {
            taskQueue.add(r);
            taskQueue.notify(); //唤醒工作线程
        }
    }

    // 内部类 --> 工作线程 --> 如果阻塞队列不空，则取出任务执行；否则，等待
    private class TaskThread extends Thread {
        public void run() {
            try {
                for (; ; ) {
                    if (taskQueue.isEmpty()) {
                        synchronized (taskQueue) {
                            taskQueue.wait(20); //阻塞队列为空，等待
                        }
                    } else {
                        Runnable r = taskQueue.remove(0); //取出第一个任务执行（FIFO模式）
                        if (null != r) {
                            r.run();
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```

## ali推荐

> JDK 内置的 4 种线程池

```java
//(1).单例线程池：只用一个核心线程来处理任务，适用于有序执行任务
public static ExecutorService newSingleThreadExecutor() {
	return new FinalizableDelegatedExecutorService
		(new ThreadPoolExecutor(1, 1,
								0L, TimeUnit.MILLISECONDS,
								new LinkedBlockingQueue<Runnable>())); //queue Max
}
```
```java
//(2).固定线程池：核心线程数和最大线程数相同，不存在非核心线程。处理一个无限队列
public static ExecutorService newFixedThreadPool(int nThreads) {
	return new ThreadPoolExecutor(nThreads, nThreads,
								  0L, TimeUnit.MILLISECONDS,
								  new LinkedBlockingQueue<Runnable>()); //queue Max
}
```
```java
//(3).缓存线程池：不使用核心线程，使用无限大的非核心线程，每个线程的过期时间为 60 秒
//适用于大量需要立即处理，并且每个任务耗时较少的任务集合
public static ExecutorService newCachedThreadPool() {
	return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
								  60L, TimeUnit.SECONDS,
								  new SynchronousQueue<Runnable>());
}
```
```java
//(4).定时线程池：比Timer更安全，功能更强大。--> 如果有任务执行过程中抛出异常，则会跳出，不会影响下次循环
public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
	return new ScheduledThreadPoolExecutor(corePoolSize, Integer.MAX_VALUE,
                                           0, NANOSECONDS,
                                           new DelayedWorkQueue());
}
```

> JDK 内置线程池的 2 个弊端（参考 ali 开发文档）

```java
(1).Fixed  & Single   --> queue ：MAX，容易因请求队列堆积，耗费大量内存，甚至OOM

(2).Cached & Schedule --> max   ：MAX，可能会创建非常多的线程，甚至OOM
```
> `ali 推荐方案`

- ThreadFactory对象（两种方式）

```java
//org.apache.commons.lang3.concurrent.BasicThreadFactory
BasicThreadFactory threadFactory = new BasicThreadFactory.Builder()
    .namingPattern("demo-pool-%d")/*.daemon(true)*/.build();
```

```java
//com.google.common.util.concurrent.ThreadFactoryBuilder
ThreadFactory threadFactory = new ThreadFactoryBuilder()
    .setNameFormat("demo-pool-%d").build();
```

- 普通任务的线程池

```java
ExecutorService threadPool = new ThreadPoolExecutor(5, 200,
        0L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<>(1024),
        threadFactory,
        new ThreadPoolExecutor.AbortPolicy());
```
- 定时任务的线程池

```java
ScheduledExecutorService threadPool = new ScheduledThreadPoolExecutor(1, threadFactory);
```

- xml配置版


```xml
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

userThreadPool.execute(thread); <!-- java代码使用 -->
```
- 注解版


```java
@Bean("demoThreadPool") //默认情况，@Bean 注解的参数名称和方法名相同，也可以显示定义
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

    executor.initialize(); //初始化
    return executor;
}
```

## 异常处理

>如何正确处理子线程中的异常呢

注意：子线程中发生了异常，如果没有任何类来接手处理的话，是会直接退出的，而不会记录任何日志。所以，如果什么都不做的话，是会出现子线程任务既没执行成功，也没有任何日志提示的诡异现象的。

```
java中两种异常：已检测异常(Checked-Exceptions)，未检测异常(Unchecked-Exceptions)

（1）因为 run()不接受 throws 语句，所以当抛出已检测异常时，需要手动 try...catch.. 做相应的处理。
（2）未检测异常：将异常信息记录到日志，然后退出程序。常见的APP崩溃，正是基于这一原理.
```

>子线程处理（1）：try-catch

```java
public class ChildThread implements Runnable {            
    public void run() {
        //...1...                
        try {
            int i = 1/0;
        } catch (Exception e) {
            System.out.println(String.format("handle exception in child thread. %s", e));
        }
        //...2...
    }
}
```
> 子线程处理（2）：当前线程的异常处理器 `setUncaughtExceptionHandler`

1. 优先选择，当前线程的异常处理器（默认没有）。
2. 然后选择，当前线程所属线程组的异常处理器。
3. 最后选择，全局的异常处理器。
4. 最后的最后，都没有设置异常处理器，则主线程默默退出。

```java
new Thread(() -> {
    Thread.currentThread().setUncaughtExceptionHandler((t, e) ->
           System.out.println(String.format("%s 当前线程的异常处理器: %s", t.getName(), e)));

    System.out.println(SystemUtils.getAll() + " - 1");
    int i = 1 / 0;
    System.out.println(SystemUtils.getAll() + " - 2");
}).start();

new Thread(() -> {
    System.out.println(SystemUtils.getAll() + " - a");
    String s = "aa".split("_")[1];
    System.out.println(SystemUtils.getAll() + " - b");
}).start();

// 2019-05-30 20:19:27.481 - 12 - Thread-1 - a ---> 该线程未设置异常处理器，故没有捕获异常
// 2019-05-30 20:19:27.481 - 11 - Thread-0 - 1
// Thread-0 当前线程的异常处理器: java.lang.ArithmeticException: / by zero
```
>子线程处理（3）：全局的异常处理器 `setDefaultUncaughtExceptionHandler`

```java
Thread.setDefaultUncaughtExceptionHandler((t, e) ->
       System.out.println(String.format("%s 全局的异常处理器: %s", t.getName(), e)));

new Thread(() -> {
    Thread.currentThread().setUncaughtExceptionHandler((t, e) ->
           System.out.println(String.format("%s 当前线程的异常处理器: %s", t.getName(), e)));

    System.out.println(SystemUtils.getAll() + " - 1");
    int i = 1 / 0;
    System.out.println(SystemUtils.getAll() + " - 2");
}).start();

new Thread(() -> {
    System.out.println(SystemUtils.getAll() + " - a");
    String s = "aa".split("_")[1];
    System.out.println(SystemUtils.getAll() + " - b");
}).start();

// 2019-05-30 20:20:52.459 - 12 - Thread-1 - a
// 2019-05-30 20:20:52.466 - 11 - Thread-0 - 1
// Thread-0 当前线程的异常处理器: java.lang.ArithmeticException: / by zero
// Thread-1 全局的异常处理器: java.lang.ArrayIndexOutOfBoundsException: 1
```

> 父线程处理`推荐`：通过 Future.get() 捕获异常

```java
ExecutorService executor = Executors.newSingleThreadExecutor();

Future<Object> future = executor.submit(() -> {
    System.out.println(SystemUtils.getAll() + " - 1");
    try {
        int i = 1 / 0;
    } catch (Exception e) {
        throw new RuntimeException(e.getMessage()); //抛出异常
    }
    System.out.println(SystemUtils.getAll() + " - 2");
    return "";
});

try {
    System.out.println("res: " + future.get()); //get捕获异常
} catch (InterruptedException | ExecutionException e) {
    System.out.println("Future-Exception-Handler: " + e);
    executor.shutdown();
}

// 2019-05-30 20:27:19.961 - 11 - pool-1-thread-1 - 1
// Future-Exception-Handler: java.util.concurrent.Exe...on: java.lang.Run...ion: / by zero
```



# 定时调度

任务调度可以用 Quartz，但对于简单的定时任务可以用Spring内置 Scheduled，linux系统定时任务用 Crontab。

##Timer

> `Timer`：单线程，串行执行，单个任务异常，整体任务停止

```java
//一开始, Task_A 能正常1秒执行一次. 
//Task_B 启动后, 由于 Task_B 完成需要2秒, 导致 Task_A 要等到 Task_B 执行完才能执行.
//更可怕的是, Task_C 启动后, 抛了异常, 导致整个定时任务全部挂了!!!
// A A A B A B C(x)

Timer timer = new Timer();
timer.schedule(Task_A, 0, 1 * 1000); //1s执行一次
timer.schedule(Task_B, 2 * 1000, 2 * 1000); //延迟2s，2s执行一次 (完成消耗2s)
timer.schedule(Task_C, 5 * 1000, 5 * 1000); //延迟5s，5s执行一次 (抛出异常)
```
```java
Task_A.cancel(); //取消单个任务
timer.cancel(); //取消整体任务
```

##定时线程池

> `ScheduledExecutorService`：并行执行，互不影响，单任务异常，异常任务停止，其他任务不影响

对于抛出异常的任务，若想在抛出异常后，还可以继续循环执行，使用 `try-catch`包裹。

```java
//Task_B, Task_C 不再影响 Task_A 定时执行
//Task_C 抛出异常后, 只影响自身不再执行, 其他无碍!!!
// A A B A A A C(x) A B A A A A B

ScheduledExecutorService pool = Executors.newScheduledThreadPool(2);
pool.scheduleWithFixedDelay(Task_A, 0, 1, TimeUnit.SECONDS);
pool.scheduleWithFixedDelay(Task_B, 2, 2, TimeUnit.SECONDS);
pool.scheduleWithFixedDelay(Task_C, 5, 5, TimeUnit.SECONDS);
```
```java
ScheduledFuture<?> future = scheduled.scheduleWithFixedDelay(task1, 1, 1, TimeUnit.SECONDS);
future.cancel(true); //取消单个任务
pool.shutdown(); //取消整体任务
```
##@Scheduled

> `@Scheduled`：Spring内置的定时线程池

```java
@EnableScheduling //使用前提，必须配置两个注解
@Component
public class ScheduledTask { }
```

```java
initialDelay  //项目启动后,延迟多少毫秒执行任务
fixedRate     //每隔多少毫秒执行一次 (当 任务耗时>频率 时,下次开始时间=上次结束时间);
fixedDelay    //每次执行完毕,延迟多少毫秒再次执行
@Scheduled(cron = "0/1 * * * * ?")   //详细配置方法执行频率，1s1次
    
cron表达式: [秒] [分] [时] [日] [月] [周] [年(可省)]
秒(0~59); 分(0~59); 时(0~23); 日(1~31,和月份有关); 月(1~12); 星期(1~7,1为周日); 年(1970~2099)
```

```java
* 所有字段，表示对应时间域的每一个时刻。如分钟字段，表示"每分钟"
    
- .......，表示一个范围。如小时字段'10-12',表示从10到12点, 即 10,11,12
    
, .......，表示一个列表值。如星期字段"MON,WED,FRI"，表示星期一、星期三和星期五

/ .......，表示一个等步长序列。x/y表示：x为起始值，y 为增量步长值
  如分钟字段： 0/15表示 0,15,30,45； 5/15表示 5,20,35,50,
  也可以使用 */y，等同于 0/y，即 y秒触发一次。
    
? 日期和星期，通常指定为"无意义的值"，相当于占位符。因为日和星期是有冲突的。

L .........，代表Last的意思，但它在两个字段中意思不同。
  在日期中，表示这个月的最后一天。如一月的 31 号，非闰年二月的 28 号
  在星期中，则表示星期六，等同于 7
  L 出现在星期字段，而且在前面有一个数值 X，则表示"这个月的最后星期 (X-1)"; 如 6L 表示该月最后星期五
```
> 此注解默认使用`单例线程池`去处理任务

```java
@EnableScheduling //全局注解
@Component //必要注解
public class ScheduledTask {

    @Scheduled(initialDelay = 1 * 1000, fixedRate = 2 * 1000)
    public void test1() throws InterruptedException {
        System.out.println(SystemUtils.getAll() + " -> task1 - start!");
        Thread.sleep(3 * 1000);
        System.out.println(SystemUtils.getAll() + " -> task1 - end !!");
    }

    @Scheduled(initialDelay = 1 * 1000, fixedRate = 2 * 1000)
    public void test2() throws InterruptedException {
        System.out.println(SystemUtils.getAll() + " -> task2 - start!");
        Thread.sleep(3 * 1000);
        System.out.println(SystemUtils.getAll() + " -> task2 - end !!");
        // int i = 1 / 0;
    }
}

//@Scheduled 默认使用'单例线程池'，源码详见：
//org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler#poolSize=1
//所以 task1,2 虽然同时启动，但还是顺序执行
//但是 task2 执行过程出现异常，也不会影响 task1 及 task2的后续执行
2019-03-04 19:53:19.709 - 29 - scheduling-1 -> task1 - start!
2019-03-04 19:53:22.710 - 29 - scheduling-1 -> task1 - end !!
2019-03-04 19:53:22.710 - 29 - scheduling-1 -> task2 - start!
2019-03-04 19:53:25.711 - 29 - scheduling-1 -> task2 - end !!
```
> 配置定时任务的线程池大小

```
1. 使用全局注解开启异步模式 @EnableAsync

2. 局部注解 @Async("*") 标记方法，调用过程中自动使用指定的线程池，并且不需要显示的创建线程去执行

3. @Async 也可标记在类上，标志该类中所有的方法均是异步方法

4. 异步方法和调用方法一定要写在不同的类中，同一类中不起效果
```

```properties
#线程池大小，默认1
spring.task.scheduling.pool.size=5
#线程名前缀，默认 scheduling-
spring.task.scheduling.thread-name-prefix=demoscheduling-
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
## Quartz

任务调度（Job-Scheduling）的开源框架。可以与JavaEE与JavaSE结合，也可以单独使用。可用来创建简单或运行十个、百个、甚至于好几万个 Jobs 复杂的程序。

> 核心概念

```java
job       //任务    - 你要做什么事？
Trigger   //触发器   - 你什么时候去做？
Scheduler //任务调度  - 你什么时候需要去做什么事？
```
```java
//DSL：Domain-Specific-Language，领域特定语言，声明式编程。格式为：方法链，例如 StringBuilder。
@Override
public StringBuilder append(String str) {
    super.append(str);
    return this; //底层实现原理：返回 this
}
```

> javaSE

```java
private static void task01() throws SchedulerException {
    //1.job
    JobDetail job = JobBuilder.newJob(JobDemo.class)
        .withIdentity("job-01", "group-01").build();

    //2.trigger
    //方式一：通过 Quartz 内置方法来完成简单的重复调用，每秒执行一次
    // Trigger trigger = TriggerBuilder.newTrigger()
    //         .withSchedule(SimpleScheduleBuilder.repeatSecondlyForever()).build();

    //方式一：自定义 Cron 表达式来给定触发的时间
    Trigger trigger = TriggerBuilder.newTrigger()
        .withIdentity("trigger-11", "group-11")
        .withSchedule(CronScheduleBuilder.cronSchedule("0/2 * * * * ?")).build();

    //3.scheduler
    Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
    scheduler.scheduleJob(job, trigger);

    scheduler.start();
}
```

```java
//JOB类必须定义为 public
public class JobDemo implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        System.out.println(LocalDateTime.now() + " - 任务被触发时调用！");
    }
}
```

> SpringBoot（两种方式）

```xml
<dependency>
    <groupId>org.quartz-scheduler</groupId>
    <artifactId>quartz</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context-support</artifactId>
</dependency>
```
> SpringBoot方式一：创建普通job类，直接调用。灵活，非侵入。

```java
@Component
public class JobDemo01 {
    @Autowired
    HelloService helloService; //service 层

    public void job() {
        helloService.hello();
        System.out.println("job01: " + SystemUtils.getNow());
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

    @Bean(name = "tigger01")
    public CronTriggerFactoryBean tigger01(@Qualifier("job01") 
                                           MethodInvokingJobDetailFactoryBean job01) {
        CronTriggerFactoryBean tigger = new CronTriggerFactoryBean();
        tigger.setName("my-tigger01");
        tigger.setJobDetail(Objects.requireNonNull(job01.getObject()));
        tigger.setCronExpression("0/5 * * * * ?"); //cron
        return tigger;
    }

    @Bean(name = "scheduler01")
    public SchedulerFactoryBean scheduler01(@Qualifier("tigger01") Trigger tigger01) {
        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setStartupDelay(5); // 延时启动定时任务,避免系统未完全启动却开始执行定时任务的情况
        scheduler.setOverwriteExistingJobs(true); // 覆盖已存在的任务
        scheduler.setTriggers(tigger01); // 注册触发器
        return scheduler;
    }
}
```
> SpringBoot方式二：job继承特定类，实现其方法，方法就是被调度的任务体。

```java
//@Component -> 无需此注解,区别于方式1
public class JobDemo02 extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) {
        HelloService helloService = (HelloService) context.getMergedJobDataMap()
            .get("helloService"); //获取 service 对象，参数由以下方法传入
        helloService.hello();
        System.out.println("job02: " + SystemUtils.getNow());
    }
}
```
```java
@Configuration
public class QuartzConfig02 {
    @Autowired
    HelloService helloService;

    @Bean("job02")
    public JobDetailFactoryBean job02() {
        JobDetailFactoryBean job = new JobDetailFactoryBean();
        job.setJobClass(JobDemo02.class);

        Map<String, Object> map = new HashMap<>();
        map.put("helloService", helloService);
        job.setJobDataAsMap(map); //传参 -> helloService
        return job;
    }

    @Bean(name = "tigger02")
    public CronTriggerFactoryBean cronTriggerFactoryBean(JobDetailFactoryBean job02) {
        CronTriggerFactoryBean tigger = new CronTriggerFactoryBean();
        tigger.setJobDetail(Objects.requireNonNull(job02.getObject()));
        tigger.setCronExpression("0/5 * * * * ?"); //cron
        return tigger;
    }

    @Bean(name = "scheduler02")
    public SchedulerFactoryBean schedulerFactoryBean(CronTriggerFactoryBean tigger02) {
        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setTriggers(tigger02.getObject());
        return scheduler;
    }
}
```
# ThreadLocal

> 基础介绍

ThreadLocal 用于存储`线程局部变量`，能够保证**相同线程数据共享，不同线程数据隔离**。即，每个线程只能访问到自己的，多线程之间互不干扰，可有效防止本线程的变量被其它线程篡改。官方建议，将 ThreadLocal 变量设置为 private static 访问级别。

本质是一个类似 Map 的数据结构，可看作`Map<当前线程的ThreadLocal对象,当前线程的局部变量>`。

一个ThreadLocal对象只能存放当前线程的一个局部变量。所以，对多个局部变量需实例化多个ThreadLocal对象。

ThreadLocal中的数据不会随着线程结束而回收，必须手动 remove() 防止内存泄露。

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


