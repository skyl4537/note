


0.命名规约
	#1.命名不能以 _ 或 $ 开始(结束).
	#2.类名采用大驼峰; 方法,参数,成员变量,局部变量采用小驼峰; 常量全部大写,单词间 _ 隔开.
	#3.






#-----------------------------------------------------------------------------------#

(一)命名规约
	1.代码中的命名均不能以下划线或美元符号开始，也不能以下划线或美元符号结束。
		反例： _name / __name / name_ / $Object / name$ / Object$
		
	2.代码中的命名严禁使用拼音与英文混合的方式，更不允许直接使用中文的方式。
		#正确的英文拼写和语法可以让阅读者易于理解,避免歧义. 注意: 即使纯拼音命名方式也要避免采用。
		正例： alibaba /  taobao /  youku /  hangzhou 等国际通用的名称,可视同英文
		反例： DaZhePromotion [ 打折 ] /  getPingfenByName() [ 评分 ] /  int 某变量 = 3
	
	3.类名使用UpperCamelCase风格，必须遵从驼峰形式 (领域模型 DO / BO / DTO / VO等除外)
		正例： MarcoPolo /  UserDO /  XmlService /  TcpUdpDeal /  TaPromotion
		反例： macroPolo /  UserDo /  XMLService /  TCPUDPDeal /  TAPromotion
	
	4.方法名、参数名、成员变量、局部变量都统一使用lowerCamelCase风格，必须遵从驼峰形式。
		正例： localValue /  getHttpMessage() /  inputUserId
		
	5.常量命名全部大写，单词间用下划线隔开，力求语义表达完整清楚，不要嫌名字长。
		正例： MAX _ STOCK _ COUNT
		反例： MAX _ COUNT
		
	--6.抽象类命名使用Abstract或Base开头；异常类命名使用Exception结尾；测试类命名以它要测试的类的名称开始，以Test结尾。
	
	7.中括号是数组类型的一部分，数组定义如下: String[] args
		反例： 请勿使用 String args[] 的方式来定义
		
	8.POJO类中布尔类型的变量，都不要加is，否则部分框架解析会引起序列化错误。
		反例： boolean isSuccess; - isSuccess() //RPC框架在反向解析时,'以为'对应的属性名为 success,导致属性获取不到,进而抛异常。
		
	--9.包名统一使用小写，点分隔符之间有且仅有一个自然语义的英语单词。包名统一使用单数形式，但是类名如果有复数含义，类名可以使用复数形式。
		#包名: 小写 & 单数; 类名: 大写 & 复数(可)
		正例：应用工具类包名: com.alibaba.open.util 、类名: MessageUtils //此规则参考spring框架结构
		
	10.杜绝完全不规范的缩写，避免望文不知义。
		反例： AbstractClass -> AbsClass; condition -> condi //此类随意缩写严重降低了代码的可阅读性
	
	--11.如果使用到了设计模式，建议在类名中体现出具体模式。
		//将设计模式体现在名字中，有利于阅读者快速理解架构设计思想。
		正例： public class OrderFactory;	public class LoginProxy;	public class ResourceObserver;
		
	--12.接口类中的方法和属性不要加任何修饰符号（public也不要加），保持代码的简洁性，并加上有效的Javadoc注释。
		//尽量不要在接口里定义变量，如果一定要定义变量，肯定是与接口方法相关，并且是整个应用的基础常量。
		正例： 接口方法签名： void f();		接口基础常量表示： String COMPANY = "alibaba";
		反例： 接口方法定义： public abstract void f();
		//说明： JDK8中接口允许有默认实现，那么这个 default 方法，是对所有实现类都有价值的默认实现。
		
	--13.接口和实现类的命名有两套规则：
		//(1).对于 Service 和 DAO 类,基于 SOA 的理念,暴露出来的服务一定是接口,内部实现类用 Impl 的后缀与接口区别。
		正例： CacheServiceImpl 实现 CacheService 接口
		
		//(2).如果是形容能力的接口名称,取对应的形容词做接口名 (通常 able 的形式)
		正例： AbstractTranslator 实现  Translatable
		
	--14.枚举类名建议带上Enum后缀，枚举成员名称需要全大写，单词间用下划线隔开。
		//枚举其实就是特殊的常量类,且构造方法被默认强制是私有。
		正例： 枚举类 -> DealStatusEnum;	成员名: SUCCESS / UNKOWN_REASON
		
	--15.各层命名规约：
		A).Service / DAO 层方法命名规约
			1).获取单个对象的方法用 get 做前缀。
			2).....多个........... list ......。
			3).....统计值......... count .....。
			4).插入............... save(推荐) 或 insert ...。
			5).删除............... remove(推荐) 或 delete ....。
			6).修改............... update ....。
			
		B).领域模型命名规约
			1).数据对象: xxxDO, xxx 即为数据表名。
			2).数据传输对象: xxxDTO, xxx 为业务领域相关的名称。
			3).展示对象: xxxVO, xxx 一般为网页名称。
			4).POJO 是 DO / DTO / BO / VO 的统称,禁止命名成 xxxPOJO 。	
		
(二)常量定义
	1.不允许出现任何魔法值（即未经定义的常量）直接出现在代码中。
		反例: String key ="Id#taobao_"+ tradeId；
			  cache.put(key, value);
			  
	#2.long或Long初始赋值时，必须使用大写的L，不能是小写的l，小写容易跟数字1混淆，造成误解。
		说明: Long a = 2l; 写的是数字的 21, 还是 Long 型的 2?
		
	#3.不要使用一个常量类维护所有常量，应该按常量功能进行归类，分开维护。
		缓存相关的常量放在类： CacheConsts 下 ； 
		系统配置相关的常量放在类： ConfigConsts 下。
		说明：大而全的常量类，非得使用查找功能才能定位到修改的常量，不利于理解和维护。

	#4.常量的复用层次有五层:
		1).跨应用共享常量: 放置在二方库中，通常是 client.jar 中的 constant 目录下。
		2).应用内共享常量: 放置在一方库的 modules 中的 constant 目录下。
			反例：易懂变量也要统一定义成应用内共享常量，两位攻城师在两个类中分别定义了表示“是”的变量：
			类 A 中： public static final String YES = "yes" ;
			类 B 中： public static final String YES = "y" ;
			A.YES.equals(B.YES), 预期是 true,但实际返回为 false,导致产生线上问题。
			
		3).子工程内部共享常量: 即在当前子工程的 constant 目录下。
		4).包内共享常量: 即在当前包下单独的 constant 目录下。
		5).类内共享常量: 直接在类内部 private static final 定义。
		
	#5.如果变量值仅在一个范围内变化用Enum类。如果还带有名称之外的延伸属性，必须使用Enum类
		#下面正例中的数字就是延伸信息，表示星期几。
		正例: public Enum { MONDAY(1), TUESDAY(2), WEDNESDAY(3)...; }	
		
(三)格式规约
	1.大括号的使用约定。
		#如果是大括号内为空,则简洁地写成{}即可,不需要换行
		#如果是非空代码块则:
			1).左大括号前不换行。
			2).左大括号后换行。
			3).右大括号前换行。
			4).右大括号后还有 else 等代码则不换行; 表示终止右大括号后必须换行。

	2.左括号和后一个字符之间不出现空格; 同样,右括号和前一个字符之间也不出现空格。详见第 5 条下方正例提示。
	
	3.if/for/while/switch/do等保留字与左右括号之间都必须加空格。
	
	4.任何运算符左右必须加一个空格。
		说明: 运算符包括赋值运算符=、逻辑运算符&&、加减乘除符号、三目运行符等。

	5.缩进采用4个空格，禁止使用tab字符。
		说明: 如果使用 tab 缩进，必须设置 1 个 tab 为 4 个空格。
			IDEA 设置 tab 为 4 个空格时，请勿勾选 Use tab character;
			而在 eclipse 中，必须勾选 insert spaces for tabs.
		正例: (涉及 1-5 点)
	
		
		
		
		
		
		
		
		
		
		
0.命名规范
	3.数据访问层: dao, persist, mapper
	4.实体: entity, model, bean,javabean, pojo
	5.业务逻辑: service ,biz
	6.控制器: controller, servlet,action,web
	7.过滤器: filter
	8.异常: exception
	9.监听器: listener	
		
		