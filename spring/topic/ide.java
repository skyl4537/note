


//{--------<<<idea-快捷键>>>----------------------------------------------------------------------------
	psvm, sout, fori	///main(),syso,for(i)

	Ctrl+Alt+V | M		//抽取变量(方法)
	
	F3 | F3+Shift		//跳到下(上)一个选择项
	F7+Alt | F7+Ctrl	//在全局(当前类)查找方法调用,可配合F3使用
	Ctrl+H				//查看类的继承关系
	Alt+7				//查看类结构
	Ctrl+Shift+Alt+N	//当前工作空间查找java文件
	
	Ctrl+G				//定位行
	Ctrl+Shift+BackSpace	//返回上次修改
	
	Shift+Alt+↑/↓		//上下移动-单行语句
	Shift+Ctrl+↑/↓		//上下移动-整个方法
	Ctrl+D				//整行复制
	Ctrl+X				//整行删除
	Ctrl+Shift+U		//大小写转化
//}

//{--------<<<idea-设置>>>----------------------------------------------------------------------------
	//黑色主题
	Appearance & Behavior - Appearance - Theme(选为Darcula) 
	勾选 Override default fonts by(......) - Name(Mircrosoft Yahei UI) - Size(12) //更改设置界面的字体大小，非代码字体
		
	//改变代码的字体和大小
	Editor - Colors & Fonts
	//首先,点击 Save As...，自定义一个名为 skyl 的样式
	//然后,选择具体的字体和大小 Primary font(Source Code Pro) - Size(15)
		
	//缩进采用4个空格,禁止使用tab字符
	Editor - Code Style - java - Tabs and Indents - Use tab character(取消勾选)
		
	//自动换行
	Editor - Code Style - Java
	右侧标签 Wrapping and Braces, (√) Line breaks 和 (√) Ensure right margin is not exceeded
		
	//悬浮文档提示
	Editor - General - Show quick documentation on...
		
	//代码提示忽略大小写
	Editor - General - Code Completion - Case sensitive...(None)
	//代码补全快捷键: Ctrl + Alt + Space
		
	//编码格式
	Editor - File Encodings - 3个UTF-8

	//显示行号等
	Editor - General - Appearance //勾选以下
		(√)Show line number(行号) + (√)Show right margin(右边线) + (√)Show method sep...(方法分割线)

	//自动导包
	Editor - General - Auto Import 
		Insert imports...(All) + (√)Add unambiguous... + (√)Optimize imports...
		
	//设置文件和代码的模板
	Editor - File and Code Templates - Includes - 自行添加
	
	//取消单行显示tabs
	Editor - General - Editor Tabs - (X)show tabs in single...
		
	//自动编译
	Build,Exe... - Compiler - (√)Build project automatically
		
	//Gradle配置
	Build,Exe... - Build Tools - Gradle - Offline work
	
//}	

	
	
	Ctrl+Shift + Enter，语句完成
“！”，否定完成，输入表达式时按 “！”键
Ctrl+E，最近的文件
Ctrl+Shift+E，最近更改的文件
Shift+Click，可以关闭文件
Ctrl+[ OR ]，可以跑到大括号的开头与结尾
Ctrl+F12，可以显示当前文件的结构
Ctrl+F7，可以查询当前元素在当前文件中的引用，然后按 F3 可以选择
Ctrl+N，可以快速打开类
Ctrl+Shift+N，可以快速打开文件
Alt+Q，可以看到当前方法的声明
Ctrl+P，可以显示参数信息
Ctrl+Shift+Insert，可以选择剪贴板内容并插入
Alt+Insert，可以生成构造器/Getter/Setter等
Ctrl+Alt+V，可以引入变量。例如：new String(); 自动导入变量定义
Ctrl+Alt+T，可以把代码包在一个块内，例如：try/catch
Ctrl+Enter，导入包，自动修正
Ctrl+Alt+L，格式化代码
Ctrl+Alt+I，将选中的代码进行自动缩进编排，这个功能在编辑 JSP 文件时也可以工作
Ctrl+Alt+O，优化导入的类和包
Ctrl+R，替换文本
Ctrl+F，查找文本
Ctrl+Shift+Space，自动补全代码
Ctrl+空格，代码提示（与系统输入法快捷键冲突）
Ctrl+Shift+Alt+N，查找类中的方法或变量
Alt+Shift+C，最近的更改
Alt+Shift+Up/Down，上/下移一行
Shift+F6，重构 – 重命名
Ctrl+X，删除行
Ctrl+D，复制行
Ctrl+/或Ctrl+Shift+/，注释（//或者/**/）
Ctrl+J，自动代码（例如：serr）
Ctrl+Alt+J，用动态模板环绕
Ctrl+H，显示类结构图（类的继承层次）
Ctrl+Q，显示注释文档
Alt+F1，查找代码所在位置
Alt+1，快速打开或隐藏工程面板
Ctrl+Alt+left/right，返回至上次浏览的位置
Alt+left/right，切换代码视图
Alt+Up/Down，在方法间快速移动定位
Ctrl+Shift+Up/Down，向上/下移动语句
F2 或 Shift+F2，高亮错误或警告快速定位
Tab，代码标签输入完成后，按 Tab，生成代码
Ctrl+Shift+F7，高亮显示所有该文本，按 Esc 高亮消失
Alt+F3，逐个往下查找相同文本，并高亮显示
Ctrl+Up/Down，光标中转到第一行或最后一行下
Ctrl+B/Ctrl+Click，快速打开光标处的类或方法（跳转到定义处）
Ctrl+Alt+B，跳转到方法实现处
Ctrl+Shift+Backspace，跳转到上次编辑的地方
Ctrl+O，重写方法
Ctrl+Alt+Space，类名自动完成
Ctrl+Alt+Up/Down，快速跳转搜索结果
Ctrl+Shift+J，整合两行
Alt+F8，计算变量值
Ctrl+Shift+V，可以将最近使用的剪贴板内容选择插入到文本
Ctrl+Alt+Shift+V，简单粘贴
Shift+Esc，不仅可以把焦点移到编辑器上，而且还可以隐藏当前（或最后活动的）工具窗口
F12，把焦点从编辑器移到最近使用的工具窗口
Shift+F1，要打开编辑器光标字符处使用的类或者方法 Java 文档的浏览器
Ctrl+W，可以选择单词继而语句继而行继而函数
Ctrl+Shift+W，取消选择光标所在词
// Alt+F7，查找整个工程中使用地某一个类、方法或者变量的位置
Ctrl+I，实现方法
Ctrl+Shift+U，大小写转化
Ctrl+Y，删除当前行


Shift+Enter，向下插入新行
psvm/sout，main/System.out.println(); Ctrl+J，查看更多
Ctrl+Shift+F，全局查找
Ctrl+F，查找/Shift+F3，向上查找/F3，向下查找
Ctrl+Shift+S，高级搜索
Ctrl+U，转到父类
Ctrl+Alt+S，打开设置对话框
Alt+Shift+Inert，开启/关闭列选择模式
Ctrl+Alt+Shift+S，打开当前项目/模块属性
// Ctrl+G，定位行
Alt+Home，跳转到导航栏
Ctrl+Enter，上插一行
Ctrl+Backspace，按单词删除
Ctrl+”+/-”，当前方法展开、折叠
Ctrl+Shift+”+/-”，全部展开、折叠
【调试部分、编译】
Ctrl+F2，停止
Alt+Shift+F9，选择 Debug
Alt+Shift+F10，选择 Run
Ctrl+Shift+F9，编译
Ctrl+Shift+F10，运行
Ctrl+Shift+F8，查看断点
F8，步过
F7，步入
Shift+F7，智能步入
Shift+F8，步出
Alt+Shift+F8，强制步过
Alt+Shift+F7，强制步入
Alt+F9，运行至光标处
Ctrl+Alt+F9，强制运行至光标处
F9，恢复程序
Alt+F10，定位到断点
Ctrl+F8，切换行断点
Ctrl+F9，生成项目
Alt+1，项目
Alt+2，收藏
Alt+6，TODO
// Alt+7，结构
Ctrl+Shift+C，复制路径
Ctrl+Alt+Shift+C，复制引用，必须选择类名
Ctrl+Alt+Y，同步
Ctrl+~，快速切换方案（界面外观、代码风格、快捷键映射等菜单）
Shift+F12，还原默认布局
Ctrl+Shift+F12，隐藏/恢复所有窗口
Ctrl+F4，关闭
Ctrl+Shift+F4，关闭活动选项卡
Ctrl+Tab，转到下一个拆分器
Ctrl+Shift+Tab，转到上一个拆分器
【重构】
Ctrl+Alt+Shift+T，弹出重构菜单
Shift+F6，重命名
F6，移动
F5，复制
Alt+Delete，安全删除
Ctrl+Alt+N，内联
【查找】
// Ctrl+F，查找
// Ctrl+R，替换
// F3，查找下一个
// Shift+F3，查找上一个
Ctrl+Shift+F，在路径中查找
Ctrl+Shift+R，在路径中替换
Ctrl+Shift+S，搜索结构
Ctrl+Shift+M，替换结构
// Alt+F7，查找用法
Ctrl+Alt+F7，显示用法
Ctrl+F7，在文件中查找用法
Ctrl+Shift+F7，在文件中高亮显示用法