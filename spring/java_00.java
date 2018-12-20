




0.快捷键Eclipse+IDEA
	创建main方法		main Alt+/补全		psvm
	输出控制台			syso				sout
	for循环				foreach				fori
	
	注释				ctrl+/				Ctrl+/ 和 Ctrl+Shift+/
	格式化代码			Ctrl+Shift+f		Ctrl+Alt+L
	批量重命名			Shift+Alt+R			Shift+F6
	
	代码提示/补全		Alt+/				Ctrl+Alt+Space
	导包,自动修正		*					Alt+Enter
	跳到下/上个错误		Ctrl+.				F2 或 Shift+F2
	优化导入的包		Ctrl+Shift+o		Ctrl+Alt+O
	
	// 抽取变量			Shift+Alt+L			Ctrl+Alt+V
	// 抽取方法			Shift+Alt+M			Ctrl+Alt+M
	// 查看类的继承关系	Ctrl+T				Ctrl+H
	// 查看方法调用		ctrl+alt+H			alt+F7

	剪切当前行或选中的内容	Ctrl+D			Ctrl+X
	粘贴当前行或选中的内容	Ctrl+Alt+↓		Ctrl+D
	// 整个方法上下移动		*				Shift+Alt+↑/↓
	// 单行语句上下移动		Alt+↑/↓			Shift+Ctrl+↑/↓
	折叠/展开代码块			Shift+Ctrl+/ *	Shift+Ctrl+减/加号
	
	自动生成get/set			*				Alt+Insert
	自动实现方法			*				Ctrl+I
	自动重写方法			*				Ctrl+O
	
	跳到下一步				F6				F8
	进入代码				*				F7
	
	/**---------------------Idea---------------------------------*/
	F3 -> 跳到下一个查找项.(配合查找 Ctrl+F 使用)
	F4 -> 跳至变量的定义位置
	

	/**---------------------Eclipse---------------------------------*/
	Ctrl+O						显示类中方法和属性的大纲
	Ctrl+Q						定位到最后编辑的地方
	Ctrl+Shift+g / Ctrl+Alt+h	查看调用此方法的所有类(两个都有用)
	Shift+Alt+R					批量重命名.(对于类的属性,可以两次"Alt+Shift+r",实现get及set方法的自动重命名)
	'Alt+Shift+C'				修改函数结构(比较实用,有N个函数调用了这个方法,修改一次搞定)
	
	Ctrl+Shift+T				当前工作区查找'java文件'
	Ctrl+Shift+R				..............'所有类型的文件',但只限手动编写的文件,'不包含'引用jar包中的类,接口
	
	Ctrl+. Ctrl+1				下个错误 快速修改 (二者配合使用)
	F2 / F3 / F4				显示类的注解; 打开类,方法和属性的声明; 显示类的继承关系

	Ctrl+K / Ctrl+Shfit+K		在当前文件中 向下/向上 查找和选中相同的字符串
	
/**--------------------------------------------------------------------------------------------------------*/
// 0.IDEA基础配置
	// #不用Tab,而用4个spaces: 
		// Editor - Code Style - java - Tabs and Indents - Use tab character(取消勾选)
		
	// #自动换行
		// Preferences - Editor - Code Style - Java
		// 右侧标签 Wrapping and Braces, (√) Line breaks 和 (√) Ensure right margin is not exceeded
		
	// #黑色主题
		// Appearance & Behavior - Appearance - Theme(选为Darcula) 
		// 勾选 Override default fonts by(......) - Name(Mircrosoft Yahei UI) - Size(12) //更改设置界面的字体大小，非代码字体
		
	// #改变代码的字体和大小
		// Editor - Colors & Fonts
		// //首先,点击 Save As...，自定义一个名为 skyl 的样式
		// //然后,选择具体的字体和大小 Primary font(Source Code Pro) - Size(15)
		
	// #快速文档提示
		// Editor - General - Show quick documentation on...
		
	// #代码提示
		// Editor - General - Code Completion - Case sensitive...(选择 None, 即大小写不敏感)
		// //代码补全快捷键: Ctrl + Alt + Space
		
	// #编码格式
		// Editor - File Encodings - 3个UTF-8

	// #显示行号等
		// Editor - General - Appearance //勾选以下
			// Show right margin：右边线.
			// Show line number：行号.
			// Show method separators：方法分割线.

	// #自动导包 (导错包的弊端)
		// Editor - General - Auto Import - Add unambiguous...(勾选)
		
	// #设置文件和代码的模板
		// Editor - File and Code Templates - Includes - 自行添加
		
	// #自动编译
		// Build,Exe... - Compiler - (勾选)Make project automatically(...)
		
	// #Gradle配置
		// Build,Exe... - Build Tools - Gradle - Offline work
	
1.Eclipse基础设置
	#开启代码折叠
		windows->perferences->Java->Editors->Folding->Enable folding(勾选)

	#Eclipse编码格式UTF-8
		A).当前java文件的编码格式: 当前*.java文件中 -> Alt+回车 -> other -> UTF-8
		B).当前项目的编码格式: 项目右键 -> Properties -> other -> UTF-8
		C).整个工作空间的编码格式: Window -> Preference -> 搜索框encod -> workspace -> UTF-8
	







/**----------------------------------------------------------------------*/




#SNAPSHOT->Alpha->Beta->Release->GA
	'Alpha': 内部测试版. 一般不向外部发布,会有很多Bug.一般只有测试人员使用
	
	'Beta': 测试版. 这个阶段的版本会一直加入新的功能.在Alpha版之后推出
	RC(Release Candidate): 候选版本. 不会再加入新的功能,主要着重于除错
	'SNAPSHOT': 不稳定,尚处于开发中的版本 
	
	'GA(General Availability)': 正式发布版本. 在国外都是用GA来说明release版本的
	
	RTM(Release to Manufacture): 是给工厂大量压片的版本. 内容跟正式版是一样的
	OEM: 随机版. 给计算机厂商随着计算机贩卖的,只能随机器出货
	RVL: 正式版. 其实RVL根本不是版本的名称它是中文版/英文版文档破解出来的 
	
	α,β,λ常用来表示软件测试过程中的三个阶段: α是第一阶段,一般只供内部测试使用; 
		β是第二个阶段,已经消除了软件中大部分的不完善之处,但仍有可能还存在缺陷和漏洞,一般只提供给特定的用户群来测试使用;
		λ是第三个阶段,此时产品已经相当成熟,只需在个别地方再做进一步的优化处理即可上市发行
