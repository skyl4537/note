#图片上传
    org.apache.commons.io.FileUtils.copyInputStreamToFile();
    req.getServletContext().getRealPath("files");
    MultipartFile.getOriginalFilename();

#区分
    @RequestMapping("/") //其中'/'表示控制器名为空; http://ip:port/

    <servlet-mapping>
        <servlet-name>springMvc</servlet-name>
        <url-pattern>/</url-pattern> //其中'/'表示匹配所有
    </servlet-mapping>
    
    
存储过程和函数：类似java中的方法
    好处：提高代码的重用性。简化操作。
    
存储过程
    一组预先编译好的sql语句的集合，理解成批处理语句
    
    1.提高代码的重用性。
    2.简化操作。
    3.减少编译次数，只编译一次
    4.减少和数据库服务器的连接次数，提高了效率
    
CREATE PROCEDURE sp_test(参数列表)
BEGIN
  -- 存储过程体（一组合法的sql语句）
END
    
参数列表包含三部分：
    参数模式：IN OUT INOUT
    参数名
    参数类型
    
IN：该参数只能输入
OUT：.........输出
INOUT：该参数既能输入，也能输出

存储过程体只有一句话，BEGIN END可以省略


    
    
    