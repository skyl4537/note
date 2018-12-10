0.普通类中获取注解类对象




5.sp定时任务
6.linux定时任务

7.fastjson

#--------------------------------------------------------------------------------------------------------------------------
0.普通类中获取注解类对象 -> 如Demo类中获取service对象

	@Component
	public class SpringUtils implements ApplicationContextAware { // 获取bean的工具类

		private static ApplicationContext context;

		@Override
		public void setApplicationContext(ApplicationContext context) throws BeansException {
			SpringUtils.context = context; // 设置上下文环境
		}

		@SuppressWarnings("unchecked")
		public static <T> T getBean(String beanName) {
			if (null == context || !context.containsBean(beanName)) {
				return null;
			}
			return (T) context.getBean(beanName);
		}

		public static <T> Map<String, T> getBeansOfType(Class<T> baseType) {
			return context.getBeansOfType(baseType);
		}
	}
	
	@Service("personService") // service实现类 --> 添加注解及括号内容beanName
	public class PersonServiceImpl implements PersonService { }
	
	//使用Demo
	PersonService personService = SpringUtils.getBean("personService"); 
	
1.读取请求体内容

	public String getParam(HttpServletRequest request) throws IOException {
		BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = streamReader.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
5.sp定时任务
	// '任务调度可以用Quartz,但对于简单的定时任务可以使用内置的Scheduled'
		// initialDelay: 项目启动后,延迟多少毫秒执行任务
		// fixedRate: 每隔多少毫秒执行一次 (当 任务耗时>频率 时,下次开始时间=上次结束时间);
		// fixedDelay: 每次执行完毕,延迟多少毫秒再次执行
		// cron: 详细配置方法执行频率

	// /**
	 // * cron表达式: [秒] [分] [时] [日] [月] [周] [年(可省)]
	 // * 
	 // * 秒(0~59); 分(0~59); 时(0~23); 天(0~31,和月份有关); 月(0~11); 星期(1~7,1为周日); 年(1970~2099)
	 // * 
	 // * * 表示所有值. 比如在分钟里表示每一分钟触发
	 // * 
	 // * ? 表示不指定值,不关心当前位置设置的值. 比如不关心是周几,则周的位置填写?
	 // * 
	 // * - 表示区间. 小时设置为10-12表示10,11,12点均会触发
	 // * 
	 // * , 表示多个值. 小时设置成10,12表示10点和12点都会触发
	 // * 
	 // * / 表示递增触发. 5/15表示从第5秒开始,每隔15秒触发
	 // */
	// @Scheduled(cron = "*/5 * * * * ?")
	// public void task() {
		// log.debug("ScheduledTask---{}", CommUtils.getNow(true));
	// }
	
	// // 0 0 10,14,16 * * ? 每天上午10点，下午2点，4点
	// // 0 0/30 9-17 * * ? 朝九晚五工作时间内每半小时
	// // 0 0 12 ? * WED 表示每个星期三中午12点
	// // 0 0 12 * * ? 每天12点触发
	// // 0 15 10 ? * * 每天10点15分触发
	// // 0 15 10 * * ? 每天10点15分触发
	// // 0 15 10 * * ? * 每天10点15分触发
	// // 0 15 10 * * ? 2005 2005年每天10点15分触发
	// // 0 * 14 * * ? 每天下午的 2点到2点59分每分触发
	// // 0 0/5 14 * * ? 每天下午的 2点到2点59分(整点开始，每隔5分触发)
	// // 0 0/5 14,18 * * ? 每天下午的 2点到2点59分、18点到18点59分(整点开始，每隔5分触发)
	// // 0 0-5 14 * * ? 每天下午的 2点到2点05分每分触发
	// // 0 10,44 14 ? 3 WED 3月每周三下午的 2点10分和2点44分触发
	// // 0 15 10 ? * MON-FRI 从周一到周五每天上午的10点15分触发
	// // 0 15 10 15 * ? 每月15号上午10点15分触发
	// // 0 15 10 L * ? 每月最后一天的10点15分触发
	// // 0 15 10 ? * 6L 每月最后一周的星期五的10点15分触发
	// // 0 15 10 ? * 6L 2002-2005 从2002年到2005年每月最后一周的星期五的10点15分触发
	// // 0 15 10 ? * 6#3 每月的第三周的星期五开始触发
	// // 0 0 12 1/5 * ? 每月的第一个中午开始每隔5天触发一次
	// // 0 11 11 11 11 ? 每年的11月11号 11点11分触发(光棍节)
	
6.linux定时任务
	// 'nano编辑器:' yum -y install nano
		// 新建/打开: nano 路径+文件名
		// 退出: Ctrl+x (y确认); 保存修改: Ctrl+o; Ctrl+c: 取消返回
		// 剪贴/删除一整行: Ctrl+k; 复制一整行: Alt+6; 粘贴: Ctrl+U 
	
	// '检查是否安装crontab:' rpm -qa | grep crontab	//rpm: Red-Hat Package Manager
		// crontab的执行日志存放在 /var/log/cron.log 或者 
		
		// crontab -l(e/r)		列出(编辑/删除)当前用户的定时任务		
	
		// //"%"是特殊字符(换行),所以命令中必须对其进行转义(\%).
		// */2 * * * * echo $(date '+\%Y-\%m-\%d \%H:\%M:\%S')  >> file
	
		// m h dom mon dow (user)  command	// 分 时 日 月 周 (user可省) cmd
		// * * * * * cmd			// 每隔一分钟执行一次任务  
		// 0 * * * * cmd			// 每小时的0点执行一次任务, 如6:00; 10:00
		// 6,10 * 2 * * cmd		// 每个月2号, 每小时的6分和10分执行一次任务
		// */3,*/5 * * * * cmd		// 每隔3分钟或5分钟执行一次任务, 比如10:03, 10:05, 10:06 
		// 0 23-7/2,8 * * * cmd	// 晚上11点到早上8点之间每2个小时和早上8点 
		// 20 3 * * * (xxx; yyy)	// 每天早晨3点20分执行用户目录下的两个指令(每个指令以;分隔)
		// 0 11 4 * mon-wed /etc/init.d/smb restart	// 每月的4号与每周1到周3的11点重启smb 
	
	// '当然,也可以定时执行shell脚本:' * * * * * sh ./file/cron.sh

		// #!/bin/bash
		// #执行结果赋值给变量
		// #crontab对于%是关键字; 而shell不是,不需要转义
		// DATE=$(date '+%Y-%M-%D %H:%m:%s')
		// LSOF=$(lsof -p $(lsof -t +D /var/lib/webpark/logs/device) |wc -l)
		// CLOSE=$(netstat -anp |grep java |grep CLOSE |wc -l)

		// cd /var/lib/webpark/logs/sm/file

		// echo $DATE '---' $LSOF >> lsof
		// echo $DATE '---' $CLOSE > close
		// grep -d skip -n 'Init---args' ../ * > ztj

7.fastjson
	fastjson入口类是 com.alibaba.fastjson.JSON, 常用的序列化操作都可以在JSON类上的静态方法直接完成.
	
	public static final Object parse(String text); // 把JSON文本parse为JSONObject或者JSONArray

	public static final JSONObject parseObject(String text)； // 把JSON文本parse成JSONObject    

	public static final JSONArray parseArray(String text); // 把JSON文本parse成JSONArray

	public static final <T> T parseObject(String text, Class<T> clazz); // 把JSON文本parse为JavaBean

	public static final <T> List<T> parseArray(String text, Class<T> clazz); // 把JSON文本parse成JavaBean集合

	public static final String toJSONString(Object object); // 将JavaBean转化为JSON文本

	public static final String toJSONString(Object object, boolean prettyFormat); // 将JavaBean转化为带格式的JSON文本.(有空格和换行)

	public static final Object toJSON(Object javaObject); // 将JavaBean转换为JSONObject或者JSONArray。

	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
		
	
	
	
	
	
	
	