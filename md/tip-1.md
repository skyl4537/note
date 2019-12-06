# 基础相关

## 异常

> Throwable

```shell
#Throwable 有两个子类：Error 和 Exception
Error    ：程序无法处理的系统错误，一般指与虚拟机相关的问题，建议程序终止。常见：'内存溢出，深递归导致栈溢出等'。
Exception：程序可以处理的异常，应该捕获并进行相应的处理。
```
> Exception

```sh
#CheckedException：编译期异常。编译时期就会检查，不处理则编译不通过
#不是具体的java类，是指 RuntimeException 以外的异常，类型上属于Exception类及其子类
IOException；SQLException；InterruptedException；ParseException（日期解析异常），ClassNotFoundException（反射-类不存在时异常）
```

```sh
#运行时异常（RuntimeException）：在运行时期，检查异常。在编译时期，不处理也不会报错。
空指针; 除数为0; 数组越界; 字符串解析成数字; 类转换异常（ClassCastException）; 非法参数异常（IllegalArgumentException）

new Timer().schedule(timerTask, new Date(), 0); #非法参数异常，定时任务频率不能为 0
Arrays.asList("a", "b").add("c");               #java.lang.UnsupportedOperationException
list.forEach(x -> {if("b".equals(x)){ list.add("c"); }}); #并发修改异常（ConcurrentModificationException）
```
>异常处理

```sh
#使用关键字 throws 引发异常。异常处理的两种方式
throw    ：异常不处理，直接抛出
try-catch：捕获异常，进行针对性的处理。必须连用，不能单独使用。#try-catch 比 if 更耗性能，所以不要使用 try-catch 作为逻辑处理手段。

throw  ：用在'方法体内'，用来明确地抛出一个异常
throws ：用在'方法声明'，用来标明一个方法可能抛出的各种异常
finally：存放的代码无论是否发生异常，都会执行，常用于释放资源。如释放IO资源，数据库连接，网络连接等。#如果有 return 语句，肯定返回此结果。
```
>注意事项

```sh
#多异常处理时，先捕获小异常，最后捕获大异常 Exception
父类方法抛出异常，子类方法只能抛出相同异常或子异常
父类方法没有抛出异常，子类方法也不能抛出异常，只能自己处理（如 run()）
```

```java
private int doWork() {
    try {
        int i = 1 / 0;
        System.out.println("结果为: " + i); //不执行。抛出异常，直接跳出
    } catch (ArithmeticException e) {
        System.out.println("被除数为0");    //会执行。捕获异常的'就近原则'
        return 0;
    } catch (RuntimeException e) {
        System.out.println("捕获大异常");   //不执行。异常只会被捕获一次
        return 1;
    } finally {
        System.out.println("finally");    //肯定执行。最终结果返回：2
        return 2;
    }
}
```


















# 练习代码

## String

> 输出每个数出现的频率（正负数算一个），并按频率进行排序

```java
@Test
public void countTest() {
    int[] nums = {5, 0, -5, 2, -4, 5, 10, 3, -5, 2, -4, 3, 4, 9, 1};
    HashMap<Integer, Integer> countMap = new HashMap<>();
    Arrays.stream(nums).boxed().map(Math::abs).forEach(x -> countMap.merge(x, 1, (a, b) -> a + b));
    System.out.println("频率统计: " + countMap);

    List<Map.Entry<Integer, Integer>> collect = countMap.entrySet().stream()
        .sorted(Comparator.comparing(Map.Entry::getValue)) //排序：升序
        .collect(Collectors.toList());
    System.out.println("排序后结果: " + collect);
}
```











