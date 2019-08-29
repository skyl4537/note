
//{--------<<<hello>>>--------------------------------------------------------------------
#

//}

//{--------<<<@ComponentScan>>>--------------------------------------------------------------------X
// #自动扫描组件 & 指定扫描规则
    // 1.xml版
        // // <!-- springIOC 组件扫描 -->
        // <context:component-scan base-package="com.example.spring"> //<!-- 排除Controller -->
            // <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Controller" />
        // </context:component-scan>
        
    // 2.注解版
        // @ComponentScan(value = "com.example.spring", useDefaultFilters = false, //禁用默认规则
                // excludeFilters = { //排除注解 @Controller 标注的组件
                        // @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = {Controller.class})
                // })
//}

//{--------<<<@Configuration & @Bean>>>--------------------------------------------------------------------
#给容器中注册组件
    1.xml版
        <bean id="person01" class="com.example.spring.bean.Person01" scope="prototype">
            <property name="age" value="18"></property>
            <property name="name" value="zhangsan"></property>
        </bean>
    
        @Test
        public void test() {
            ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml"); //加载
            Person01 person01 = (Person01) context.getBean("person01"); //通过id获取
            System.out.println("xml: " + JSON.toJSON(person01));
        }
        
    2.注解版
        @Configuration //标明配置类
        public class BeansConfig {

            @Bean("person01") //给容器中注册一个Bean,默认id为'方法名'
            public Person01 getPerson01() {
                return new Person01(29, "wangwu");
            }
        }
    
        @Test
        public void test() {
            //@Autowired person01
            System.out.println("注解: " + JSON.toJSON(person01));
        }
    
//}

//{--------<<<@PropertySource>>>--------------------------------------------------------------------
    @Component
    @PropertySource(value = {"file:./logs/sm/task/info.properties"}, ignoreResourceNotFound = true, encoding = "utf-8")
    @ConfigurationProperties(prefix = "info", ignoreUnknownFields = true, ignoreInvalidFields = true)
    public class Info { }
    
#相关属性
        value={"**,xx,--"}        -> //可指定多个配置文件
        ignoreResourceNotFound    -> //当指定的配置文件不存在是否报错,默认 false
        encoding                -> //编码格式,很重要...

#两种取值方式
    1.读取单个属性
        @Value("${info.enabled}");
        
    2.将配置文件映射到javabean
        prefix = "person"        -> //属性前缀, 匹配属性通过'prefix+字段名'
        ignoreUnknownFields        -> //是否忽略未知的字段
        ignoreInvalidFields        -> //是否忽略验证失败的字段(包括类型转换异常)
    

    
    
    
    
    

//}





























