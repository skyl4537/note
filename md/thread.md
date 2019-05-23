[TOC]







# 线程基础

##基础概念

多线程的目的是为了更好的利用计算机的 CPU 资源。比如在一个进程中，如果只有一个线程（也叫主线程），那么如果当这个线程因为某种原因阻塞（等待用户输入数据等情况）的时候，那么相对应的这个进程也让出了 CPU 资源并暂停执行了。试想一下，如果在一个进程中添加多个线程，那么当这个进程中某个线程阻塞的时候，其余线程还可以继续执行，做它们自己的工作，这样的话计算机的利用效率就提高了。

> 线程创建（3种方式，可指定线程名）

1. 继承于 Thread 类并且重写其 run 方法。不推荐使用，java 单继承，多实现。
2. 新建一个 Runnable 对象并将其作为一个参数传入 Thread 类的构造方法中。
3. 实现 Callable 接口，并使用线程池调用。
4. ​

**1 和 2 的区别**：前者每个线程都执行自己的 run() 方法。后者多个线程执行同一个 Runnable 对象的 run() 方法。

> 现场启动 & 退出

通过调用线程对象的 start() 方法来开启一个线程`（这个方法只能被调用一次）`。线程开启后便进入就绪状态，但并不会马上执行，而是等待线程调度器的调度，一旦线程调度器调度了该线程之后，该线程便可获得 CPU 资源，然后进入运行状态。

虚拟机启动时会创建一个非守护线程（即主线程），主线程退出的两种情况：

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

```java
主线程 是非守护线程，优先级为默认的 5。
非守护线程 也叫用户线程，由用户创建。

守护线程 是指在程序运行的时候在后台提供一种通用服务的线程。如gc。
其中，'主线程和守护线程一起销毁；主线程和非守护线程互不影响'。
```
```java
//守护线程 -> 每隔 1 秒打印一次 i 的值，循环一共需要执行 5 次，也就是打印 5 次 i 的值。
//主线程   -> 休眠 3 秒后，打印一句结束语后结束。
public static void daemonThreadTest() {
    Thread daemonThread = new Thread(() -> {
        for (int i = 0; i < 5; i++) {
            System.out.println(Thread.currentThread().getName() + "打印: " + i);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }, "守护线程");
    
    daemonThread.setDaemon(true); //守护线程在调用 start() 方法之前设置，否则抛异常
    daemonThread.start();
}

public static void main(String[] args) {
    System.out.println("主线程-START");
    daemonThreadTest();
    try {
        TimeUnit.SECONDS.sleep(3);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
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

##生命周期

![](assets/thread0.png)

![](assets/thread1.png)

> 线程状态

```java
'1.新生状态' 用 new 关键字建立一个线程对象后，该线程对象就处于新生状态。处于新生状态的线程有自己的内存空间，通过调用 start() 进入就绪状态。
```
```java
'2.就绪状态' 处于就绪状态线程具备了运行条件，但还没分配到CPU，处于线程就绪队列，等待系统为其分配CPU。当系统选定一个等待执行的线程后，它就会从就绪状态进入执行状态，该动作称之为 CPU调度。
```
```java
'3.运行状态' 处于运行状态的线程执行 run()方法体代码，直到等待某资源而阻塞 或 完成任务而死亡。如果在给定的时间片内没有执行结束，就会被系统给换下来回到等待执行状态。
```
```java
'4.阻塞状态' 处于运行状态的线程在某些情况下，如执行了 sleep()方法，或等待I/O设备等资源，将让出CPU并暂时停止自己的运行，进入阻塞状态。在阻塞状态的线程不能进入就绪队列。只有当引起阻塞的原因消除时，如睡眠时间已到，或等待的I/O设备空闲下来，线程便转入就绪状态，重新到就绪队列中排队等待，被系统选中后从原来停止的位置开始继续运行。

//进入阻塞状态的4种情况：
sleep(); --> 抱着 资源锁 睡大觉，自己不用，也不给别人。
wait();  --> 放开 资源锁，自己站旁边看别人执行。
join();  --> 阻塞指定线程等到另一个线程完成以后再继续执行
其他操作  --> 如IO中的read()，write();
```


```java
'5.死亡状态' 死亡状态是线程生命周期中的最后一个阶段。线程死亡的原因有三个。①是正常运行的线程完成了它的全部工作；②是线程被强制性地终止，如通过执行 stop()来终止一个线程【不推荐使用】，③是线程抛出未捕获的异常。
```

> 线程状态-API：Thread.State

```java
//在给定时间点上，一个线程只能处于一种状态。这些状态是虚拟机状态，它们并没有反映所有操作系统线程状态。
'1.NEW'：至今尚未启动的线程处于这种状态。
'3.RUNNABLE'：正在 Java 虚拟机中执行的线程处于这种状态。
'5.TERMINATED'：已退出的线程处于这种状态。

'4.1.BLOCKED'：受阻塞并且正在等待监视器锁。如进入 synchronized 或调用 wait()后再次进入同步的块/方法。 

'4.2.WAITING'：无限期地等待另一个线程执行某一特定操作。如调用不带超时的 wait(); join();

'4.3.TIMED_WAITING'：具有指定等待时间的某一等待线程的线程状态。如调用 sleep(n); wait(n); join(n);
```

> 线程状态-DEMO

```java
private void doThreadState() throws InterruptedException {
    Thread thread = new Thread(() -> {
        boolean isFinish = false;
        long l = System.currentTimeMillis();

        while (!isFinish) {
            if (System.currentTimeMillis() - l == 500) { //启动 500ms 后休眠 2s
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (System.currentTimeMillis() - l > 3 * 1000) {
                isFinish = true;
            }
        }
    });
    System.out.println("111 " + thread.getState()); //子线程 new 而未启动：NEW

    thread.start();
    System.out.println("222 " + thread.getState()); //子线程启动并执行：RUNNABLE

    TimeUnit.SECONDS.sleep(1); //休眠 1s，此时子线程处于 休眠状态：TIMED_WAITING
    System.out.println("333 " + thread.getState());

    TimeUnit.SECONDS.sleep(5);
    System.out.println("444 " + thread.getState()); //子线程执行完毕：TERMINATED
}
```

## 线程控制

线程的控制简单来说无非 3 种：开启线程、暂停线程、停止线程。

**开启线程**：调用线程对象的 `start()` 方法（该方法只能被调用一次），使得该线程进入就绪状态，等待线程调度器调度该线程，一旦线程调度器调度了该线程，该线程便可获得 CPU 资源，进入正在运行状态。

**暂停线程**：通过调用该线程对象的 `sleep(long millis)` 方法来让该线程休眠指定的秒数，调用这个方法之后线程将会让出 CPU 进入休眠。休眠完成之后的线程并不会直接获得 CPU 资源，而是会进入就绪状态，等待着线程调度器的调度来获取 CPU 资源。

> **停止线程**：使得线程的 `run()` 方法正常执行完毕。

```java
//【官方推荐】利用一个 boolean 变量来标记任务是否完成，在任务完成后直接退出循环 或 修改这个标记变量。
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

```java
//利用系统预定义的"中断标识"
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

> 中断标识 - 存在的问题：`Thread.isInterrupted()`

**中断标识** 其本身不会影响线程的执行，但是和其他方法混用时，就有可能影响线程的执行。例如，和 sleep() 混用，如果当前线程处于中断状态，再调用 sleep() 方法后，不仅会抛出异常，而且还会打断当前的中断状态。

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
            //中断线程 调用 sleep() 不仅会抛出此异常，并且还会打断 中断状态
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

##相关API

> t2调用 `t1.join(millis)`，导致 t2 挂起，挂起指定时间（有参方法） 或 直至 t1 执行完毕，才继续执行 t2

```java
Thread t1 = new Thread(() -> {
    try {
        System.out.println(LocalDateTime.now() + " - "
                           + Thread.currentThread().getName());
        TimeUnit.SECONDS.sleep(2);
    } catch (InterruptedException ignored) {
    }
}, "Thread-T1");

Thread t2 = new Thread(() -> {
    try {
        t1.join(1000); // 等待线程t1终止,最大等待时间1000ms
        System.out.println(LocalDateTime.now() + " - "
                           + Thread.currentThread().getName());
        TimeUnit.SECONDS.sleep(2);
    } catch (InterruptedException ignored) {
    }
}, "Thread-T2");

Thread t3 = new Thread(() -> {
    try {
        t2.join();
        System.out.println(LocalDateTime.now() + " - "
                           + Thread.currentThread().getName());
    } catch (InterruptedException ignored) {
    }
}, "Thread-T3");

t3.start(); //【模拟】三个线程顺序执行
t2.start();
t1.start();
System.out.println(LocalDateTime.now() + " - " + Thread.currentThread().getName());

```

```java
2019-05-22T09:19:27.880 - main //主线程和 T1 几乎同时执行
2019-05-22T09:19:27.883 - Thread-T1
2019-05-22T09:19:28.983 - Thread-T2 //等待1s.(只等待T1执行1s)
2019-05-22T09:19:30.983 - Thread-T3 //等待2s.(等待T2执行完毕)
```

> 如果线程被创建了，但还未启动，调用它的 `join()` 方法是没有作用的，将直接继续向下执行！

```java
Thread thread = new Thread(() -> {
    try {
        TimeUnit.SECONDS.sleep(2);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
});
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

> 线程t2调用 `Thread.yield();` 是提示线程调度器让出 t2 的 CPU 资源，让其他线程使用。这只是一种提示，线程调度器可以忽略这种提示，所以 CPU 资源是否让出并不是一定的，是有一定概率的。`注意`：成功让出后，t2进入就绪状态，而非阻塞状态。

```java
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
主线程 - 正在占用CPU
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

多个线程同时操作同一资源，容易出现线程安全问题。需要让线程同步，保证数据安全（确保资源在某一时刻只被一个线程使用）。

##内存模型


![](assets/thread2.png)

> java 线程的私有工作内存和主内存之间的联系：

1、Java 线程只能直接对其私有工作内存进行IO读取和写入操作，而不能直接对主内存进行IO操作。

2、主内存对所有的 Java 线程都可见，即所有的 Java 线程都可以通过其工作内存来间接的修改主内存中的数据。

3、线程的工作内存只对其对应的 Java 线程可见，不同的 Java 线程不共享其工作内存。

> Java 线程修改主内存数据，通过以下步骤：

1、线程通过其工作内存读取主内存中待修改的变量值，并且拷贝一份副本留在该线程的工作内存中。

2、线程执行相关代码在其工作内存中修改这个副本值。

3、修改完之后，线程的工作内存将修改后的值写入到主内存中。







## ReentrantLock 























































































