# 基于Jenkins+git+gradle的android持续集成

 1、Jenkins安装与配置
         
    1、下载地址：http://mirrors.jenkins-ci.org/war/latest/jenkins.war。将下载的jenkins.war包直接放到tomcat下的webapps目录，
     启动tomcat即可安装完成。也可以通过官网下载安装。

    2、Tomcat的webapps文件夹下，会生成jenkins工程文件，需添加到系统环境变量中使用
       Administrator文件夹下默认也会生成jenkins工程文件

    3、编码问题：进入jenkins系统管理页面，会出现如图提示，可修改tomcat的server.xml配置，在Connector  标签添加上 URIEncoding="UTF-8"。    

    4、默认管理员账号：admin，密码保存在 initialAdminPassword 文件中

    5、下载常用插件：git，gradle，Git Parameter Plug-In等
    
    6、配置Jenkins参数：
       1、系统设置 - 全局属性下配置AndroidSDK本地路径
                  - 配置 Jenkins Location 中的系统管理员邮箱地址
                  - 配置 Extended E-mail Notification 邮件发送地址，使用SMTP服务器
                  - 配置邮件发送内容
	                    $PROJECT_NAME - Build # $BUILD_NUMBER - $BUILD_STATUS:
						Check console output at $BUILD_URL to view the results.
						<hr/>
						(本邮件是程序自动下发的，请勿回复！)<br/><hr/>
						项目名称：$PROJECT_NAME<br/><hr/>
						构建编号：$BUILD_NUMBER<br/><hr/>
						构建状态：$BUILD_STATUS<br/><hr/>
						触发原因：${CAUSE}<br/><hr/>
						构建日志地址：<a href="${BUILD_URL}console">${BUILD_URL}console</a><br/><hr/>
						构建地址：<a href="$BUILD_URL">$BUILD_URL</a><br/><hr/>
						变更集:${JELLY_SCRIPT,template="html"}<br/><hr/>
                  - 配置邮件触发器（Default Triggers）
       2、全局工具设置 - 配置JDK、gradle本地路径
 
  2、构建项目，自动打包

    1、新建->构建一个自由风格的项目，编写项目信息，创建的项目路径在 jenkins\jobs 下。
    2、在Jenkins项目主页选择“配置”选项，进行参数设置。其配置文件是config.xml
	   1、在Job Notifications下配置参数化构建过程。
          * Git Parameter可以设置git代码，关联相关分支，设置默认值。    
          * Choice Parameter可以设置固定选项值
          * Boolean Parameter可以设置boolean值
          * String Parameter可以设置字符串值
	        参数名 			 	参数类型 	参数值列表
			BUILD_TYPE 	     	Choice 		Release or Debug
			IS_JENKINS 			Boolean 	true，设置是否通过Jenkins打包
			VERSION_CODE 		String 	    自定义显示在App上的版本号
       2、配置源码管理，关联git仓库。设置Branches to build 值为Git Parameter配置的name值。例如：$GIT_BRANCH
       3、构建触发器：Poll SCM：定时检查源码变更（根据SCM软件的版本号），如果有更新就checkout最新code下来，然后执行构建动作。
       4、构建：选择Invoke Gradle Script构建，配置gradle命令。选择Invoke Gradle script之后可以选Invoke Gradle和Use Gradle Wrapper。
          1、选择Invoke Gradle就是调用本地安装配置好的Gradle，此时需要指定Gradle路径。使用Invoke Gradle构建，命令配置在task下
          2、为了方便所有开发者统一Gradle版本，一般都使用Gradle Wrapper。使用Gradle Wrapper构建，命令配置在Switches下
		     1、clean build  --stacktrace --debug
             2、clean assemble${PRODUCT_FLAVORS}${BUILD_TYPE}
       5、构建后操作
         1、配置了Archive the artifacts
            在“用于存档的文件”中填写需要存档的文件名，可以使用通配符。比如上面我配置了app/build/outputs/apk/*.apk，构建完之后在任务首页可以下载存档的文件
         2、配置Editable Email Notification 发送邮件    
            在“Triggers”中，设置邮件发送时机及收件人地址        

  3、使用Jenkins打包时常见问题处理

	  1、gradle路径配置错误，提示以下错误
		[Gradle] - [ERROR] Can't retrieve the Gradle executable.
		Build step 'Invoke Gradle script' marked build as failure
	    
		解决办法：在 全局工具配置 里面修改gradle路径
	    例：C:\Users\Administrator\.gradle\wrapper\dists\gradle-3.3-all\55gk2rcmfc6p2dg9u9ohc3hw9\gradle-3.3      
	   
	 
	  2、打包时提示“Could not load Logmanager "org.apache.juli.ClassLoaderLogManager"”
	    
	    解决办法：修改Tomcat的bin目录下的catalina.bat文件。
	    将
	      set "JAVA_OPTS=%JAVA_OPTS% %LOGGING_CONFIG%"
		  set "JAVA_OPTS=%JAVA_OPTS% %LOGGING_MANAGER%"
	    修改为
	      rem set "JAVA_OPTS=%JAVA_OPTS% %LOGGING_CONFIG%"   
      rem set "JAVA_OPTS=%JAVA_OPTS% %LOGGING_MANAGER%"

配置Jenkins文档：

	http://blog.csdn.net/mabeijianxi/article/details/52680283
	https://www.jianshu.com/p/38b2e17ced73/ 

配置邮件步骤：

	http://blog.csdn.net/leonranri/article/details/49306663           