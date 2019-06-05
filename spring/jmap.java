


#使用Java监控工具出现 Can't attach to the process (jmap -heap pid)
    //新版Linux加入'ptrace-scope'机制,这种机制不允许了访问当前正在运行的进程的内存和状态.
    (1).临时解决: echo 0 | sudo tee /proc/sys/kernel/yama/ptrace_scope
    (2).永久解决: 修改文件'/etc/sysctl.d/10-ptrace.conf'
        kernel.yama.ptrace_scope = 0 //0:允许, 1:不允许
        
#jstat -gcutil pid 2000
    S0        Heap上的 Survivor space 0 段已使用空间的百分比
    S1        Heap上的 Survivor space 1 段已使用空间的百分比
    E        Heap上的 Eden space 段已使用空间的百分比
    O        Heap上的 Old space 段已使用空间的百分比
    P        Perm space 已使用空间的百分比
    YGC        从程序启动到采样时发生Young GC的次数
    YGCT    Young GC所用的时间(单位秒)
    FGC        从程序启动到采样时发生Full GC的次数
    FGCT    Full GC所用的时间(单位秒)
    GCT        用于垃圾回收的总时间(单位秒) 



























