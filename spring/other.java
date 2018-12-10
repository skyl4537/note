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