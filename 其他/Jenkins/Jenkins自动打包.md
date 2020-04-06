# 基于Jenkins+git+gradle的android持续集成

**1、Jenkins安装**
         
* 1、下载jenkins.war包，地址：http://mirrors.jenkins-ci.org/war/latest/jenkins.war。
* 2、将jenkins.war包直接放到tomcat下的webapps目录，添加 xxxx\webapps\jenkins 至系统环境变量中，启动Tomcat则在webapps文件夹下生成jenkins工程文件
* 3、默认管理员账号：admin，密码保存在 jenkins\secrets\initialAdminPassword 文件中
* 4、下载常用插件：git，gradle，Git Parameter Plug-In、build timestamp pluin等
* 5、常见问题：

		1、更改 tomcat 的 web.xml 配置文件中有一个属性值 listing，更改为true，则展示虚拟目录
		2、编码问题：进入jenkins系统管理页面，会出现如图提示，可修改 tomcat 的server.xml配置，在Connector 标签添加上 URIEncoding="UTF-8"
		3、打包时提示：Could not load Logmanager "org.apache.juli.ClassLoaderLogManager"，解决办法：修改Tomcat的bin目录下的catalina.bat文件
			将
			  set "JAVA_OPTS=%JAVA_OPTS% %LOGGING_CONFIG%"
			  set "JAVA_OPTS=%JAVA_OPTS% %LOGGING_MANAGER%"
			修改为
			  rem set "JAVA_OPTS=%JAVA_OPTS% %LOGGING_CONFIG%"   
			  rem set "JAVA_OPTS=%JAVA_OPTS% %LOGGING_MANAGER%"
     
**2、Jenkins配置**

* 1、系统设置 

		1、全局属性：配置AndroidSDK本地路径
		2、Jenkins Location：配置系统管理员邮箱地址
		3、E-mail Notification：配置邮件发送地址，使用SMTP服务器，配置邮件发送内容：
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

* 2、全局工具设置：配置JDK、Gradle本地路径
 
**3、构建项目**

* 1、新建->构建一个自由风格的项目，编写项目信息，创建的项目路径在 jenkins\jobs 下。
* 2、在Jenkins项目主页选择“配置”选项，进行参数设置。其配置文件是config.xml

	* 1、在Job Notifications下
	
			BUILD_TIMESTAMP：启用时间戳
			参数化构建过程：
			* Git Parameter可以设置git代码，关联相关分支，设置默认值。    
			* Choice Parameter可以设置固定选项值
			* Boolean Parameter可以设置boolean值
			* String Parameter可以设置字符串值
			  参数名 			 	参数类型 	参数值列表
			  BUILD_TYPE 	     	Choice 		Release or Debug
			  IS_JENKINS 			Boolean 	true，设置是否通过Jenkins打包
			  VERSION_CODE 		    String 	    自定义显示在App上的版本号
 
	* 2、配置源码管理，关联git仓库。设置Branches to build 值为Git Parameter配置的name值。例如：$GIT_BRANCH
	
	* 3、构建触发器：Poll SCM：定时检查源码变更（根据SCM软件的版本号），如果有更新就checkout最新code下来，然后执行构建动作
	
	* 4、构建：
	
			1、构建Gradle命令
			   1、选择Invoke Gradle Script构建，配置Gradle命令，可以选Invoke Gradle和Use Gradle Wrapper。
			   2、添加Task：clean assemble${BUILD_TYPE}
			2、构建Windows批命令

	* 5、构建后操作
	
			1、配置了Archive the artifacts
			   在“用于存档的文件”中配置路径，可以使用通配符，构建完之后在任务首页可以下载存档的文件。比如app/build/outputs/apk/$BUILD_TYPE/*.apk
			2、配置Editable Email Notification 发送邮件    
			   在“Triggers”中，设置邮件发送时机及收件人地址        

