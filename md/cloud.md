



#Cloud

## Eureka

> `服务端`の单机版

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```

```java
@EnableEurekaServer           //全局注解-eureka
http://eureka-8761.com:8761/  //页面验证
```

```properties
server.port=8761
spring.application.name=cloud-eureka

eureka.instance.hostname=eureka-8761.com
eureka.client.service-url.defaultZone=http://${eureka.instance.hostname}:${server.port}/eureka/
```

```properties
#是否注册到eureka; 是否从eureka中获取服务. 默认都为 true
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false

#关闭自我保护
eureka.server.enable-self-preservation=false
#每隔5秒进行一次服务列表清理
eureka.server.eviction-interval-timer-in-ms=5
```

> `服务端`の集群版

```properties
# 8761
server.port=8761
spring.application.name=cloud-eureka

eureka.instance.hostname=eureka-8761.com
eureka.client.service-url.defaultZone=http://eureka-8762.com:8762/eureka/,http://eureka-8763.com:8763/eureka/
```

```properties
# 8762
server.port=8762
spring.application.name=cloud-eureka

eureka.instance.hostname=eureka-8762.com
eureka.client.service-url.defaultZone=http://eureka-8761.com:8761/eureka/,http://eureka-8763.com:8763/eureka/
```

```properties
# 8763
server.port=8763
spring.application.name=cloud-eureka

eureka.instance.hostname=eureka-8763.com
eureka.client.service-url.defaultZone=http://eureka-8761.com:8761/eureka/,http://eureka-8762.com:8762/eureka/
```

```sh
# 本地 hosts 文件
127.0.0.1 eureka-8761.com
127.0.0.1 eureka-8762.com
127.0.0.1 eureka-8763.com
```

```sh
# 可能出现问题：集群各节点均在 unavailable-replicas 下的问题
(1).各个节点'application-name'要保持一致
(2).本地测试时，各个节点都在同一台机器，'instance.hostname'需要在本地 hosts 文件中填写，各个节点使用自己的host
(3).'prefer-ip-address'设置为 true 或者默认不填写
(4).'register-with-eureka'和'fetch-registry'都为 true
(5).'defaultZone'不能使用 localhost，需要使用自己在host中配置的域名，配置项为除自己以外的集群中所有节点
```

> 客户端

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

```properties
#将ip注册到 Eureka。默认注册的是主机名
eureka.instance.prefer-ip-address=true
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
```

```properties
#自定义服务名称信息，默认显示 -> 主机名:微服务名:端口号
#当 多个实例 以相同名字注册到eureka服务中时，只会显示最后一个实例。所以，最好用不同的名字
#eureka.instance.instance-id=${spring.application.name}:${server.port}
```

## Feign

>`demo-emp` 中调用 `demo-dept`。所以，在 `demo-emp` 中添加依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

```java
@EnableFeignClients //全局注解-feign
```

> `demo-emp` 中新建包 `com.example.emp.client`，存放 `demo-dept` 中的接口

```java
@Component
@FeignClient(name = "cloud-dept"/*, configuration = FeignConfig.class*/) //指定微服务名，不能包含下划线
public interface DeptClient {

    @GetMapping("/dept/{deptId}")
    ResultVO selectById(@PathVariable("deptId") Integer deptId);
}
```

> `demo-emp`使用 Client

```java
@RequestMapping("/emp")
@RestController
public class EmpController {

    @Autowired
    DeptClient deptClient;

    @GetMapping("/{empId}")
    public ResultVO selectById(@PathVariable("empId") Integer empId) {
        Emp emp = empMapper.selectById(empId);
        ResultVO resultVO = deptClient.selectById(emp.getDeptId());
        Dept dept = ResultVOUtil.getDataObject(resultVO, Dept.class);
        emp.setDept(dept);
        return ResultVOUtil.success(emp);
    }
}
```

> 负载均衡

```sh
#java -jar .\cloud-dept.jar --server.port=8001
使用以上命令，动态指定项目端口，启动多个 cloud-dept，多次请求，会轮流调用 cloud-dept 接口。

#IDEA 单个 SpringBoot 项目，多次启动方式：
首先，点击启动绿三角左边的 'Edit Config...'，选中待启动项目，取消勾选'Single-instance-only'
然后，每次运行项目前，手动修改配置文件中的端口
```

## Hystrix

