







## log

对 trace/debug/info 级别的日志输出，必须使用条件输出形式 或者 使用占位符的方式。

```java
//正例：（条件）
if (logger.isDebugEnabled()) {
    logger.debug("Processing trade with id: " + id + " symbol: " + symbol);
}
```

```java
//正例：（占位符）
logger.debug("Processing trade with id: {} and symbol : {} ", id, symbol);
```