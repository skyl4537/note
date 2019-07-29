[TOC]



# 基础阶段

## 枚举

>`java.lang.Enum` 是一个抽象类，默认有两个属性：name + ordinal

```java
public enum EnumTest {    
    MON, TUE, WED, THU, FRI, SAT, SUN; 
    
    // new Enum<EnumTest>("MON", 0);
    // new Enum<EnumTest>("TUE", 1);
    // ...
}
```

```java
枚举类型使用关键字 enum，每个枚举值都是 java.lang.Enum 的子类（java.lang.Enum 是一个抽象类）。

枚举类型符合通用模式 Class Enum<E extends Enum<E>>，而 E 表示枚举类型的名称。
枚举类型的每一个值都将映射到 protected Enum(String name, int ordinal) 构造函数中。
每个值的名称都被转换成一个字符串，并且序数设置表示了此设置被创建的顺序。

所以，以上的枚举类，实际上调用了 7 次 Enum(String name, int ordinal)：
```

```java
EnumTest sun = EnumTest.SUN;
System.out.println(sun.name() + "-" + sun.ordinal()); //SUN-6
```

> 枚举自定义属性和方法

```java
@Getter //只能有 get
@NoArgsConstructor
@AllArgsConstructor
public enum EnumTest {
    MON(1, "周一"), TUE, WED, THU, FRI, SAT, SUN(7, "周末");

    private Integer code;
    private String msg;
}
```

```java
EnumTest[] values = EnumTest.values();
for (EnumTest value : values) {
    System.out.println(value.getCode() + "-" + value.getMsg() + "-" + value.ordinal()); //7-周末-6
}
```

> 总结：`switch 底层只支持 int + 枚举(底层也是int)`

```java
可以把 enum看成是一个普通的 class，它们都可以定义一些属性和方法。
不同之处是：enum 不能使用 extends 关键字继承其他类，因为 enum 已经继承了 java.lang.Enum（java是单一继承）。
```

## 运算符

> &，&&：都是逻辑 "与" 运算符，但后者为短路运算

```java
&  ：左边无论真假，右边都要运算
&& ：如果左边为真，右边参与运算； 如果左边为假，那么右边不参与运算。
```

```java
if (x < 1 && ++y > 0) {} System.out.println(x + "-" + y);

int x = 0, y = 0; //&：0-1； &&：0-1
int x = 2, y = 0; //&：2-1； &&：2-0
//&& 属于短路运算，即当 x=2 时，x<1 为 false。不管右侧如何，整体恒为 false。所以，不再进行 ++y>0 判断，即 y 不再自增。
```

```java
if (x < 1 || ++y > 0) {} System.out.println(x + "-" + y);

int x = 0, y = 0; //|：0-1； ||：0-0
int x = 2, y = 0; //|：2-1； ||：2-1
//||也是短路运算，道理同上。
```

> 其他运算符

```java
// 7%4 = 3; 7%-4 = 3; -7%4 = -3; -7%-4 = -3; 7%9 = 7; -7%-9 = -7; -7%-1= 0
%(取模)      --> 先取绝对值，再进行运算。符号同左。

^(异或)      --> 左右同为 false，左右不同为 true。(和其他差别很大)

<<(位运算-左) --> 乘以 2 的移动位数次幂 //3<<2 = 3 * 2^2 =12
>>(位运算-右) --> 除以 2 ...........  //6>>2 = 6 / 2^2 =1
```



# Collection

## 基础概念





# Collection-Plus

##list去重

> 目标：取出 Id 不重复的元素

```java
List<User> users = Arrays.asList(new User(1, "a"), new User(2, "b"), new User(1, "aa")); //源集合
```

> （1）遍历源集合，用遍历元素和结果集中的每个元素比较

```java
ArrayList<User> list = new ArrayList<>(); //结果集
users.forEach(x -> {
    boolean match = list.stream().anyMatch(y -> y.getId().equals(x.getId()));
    if (!match) {
        list.add(x);
    }
});
```

> （2）使用HashSet。`重写 hashCode() + equals()`

```java
//先放入 Set 去重，再取出来放入 list
Set<User> set = new HashSet<>(users);
List<User> list = new ArrayList<>(set);
```

> （3）对于大数据量，应采用 Stream 并行流的 distinct 方法。`重写 hashCode() + equals() `

```java
List<User> list = users.parallelStream()
    .distinct() //底层是通过 equals() 进行去重
    .collect(Collectors.toList());
```

> （0）重写 hashCode + equals

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(id, user.id) /*&& Objects.equals(name, user.name)*/;
}

@Override
public int hashCode() {
    return Objects.hash(id/*, name*/); //只比较 id
}
```

> 总结向

```java
//HashSet 底层是通过 HashMap 实现
//HashMap 确保key值唯一：先比较 hashCode()，如果相同；继续比较 equals() 是否为同一个对象
public HashSet() {
    map = new HashMap<>();
}

//存在则返回 false，不存在的返回 true
public boolean add(E e) {
    return map.put(e, PRESENT)==null;
}
```

```shell
#equals() 返回 true 的时候，hashcode() 的返回值必须相同。

如果两个对象相同（equals 返回 true），则它们的 hashCode 必须相同。但如果两个对象不同，则它们的 hashCode 不一定不同。
如果两个不同对象的 hashCode 相同，这种现象称为"哈希冲突"。冲突会导致操作哈希表的时间开销增大，所以尽量定义好的hashCode()方法，能加快哈希表的操作。
```

```shell
#覆写了 equals() 方法之后，一定要覆盖 hashCode() 方法

对于，String a = new String(“abc”); String b = new String(“abc”);
如果只覆写 equals()，不覆写 hashCode() 的话，那么 a和b 的 hashCode 就会不同。
把这两个类当做 key 存到 HashMap 中的话就会出现问题，就会和 key 的唯一性相矛盾。
```









