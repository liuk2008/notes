* 基于Jenkins+git+gradle的android持续集成

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

                  - 配置 Extended E-mail Notification，邮件发送地址 
       2、全局工具设置下配置JDK、gradle本地路径
 
  2、构建项目，自动打包

    1、新建->构建一个自由风格的项目，编写项目信息，创建的项目路径在 jenkins\jobs 下，config.xml 文件对应相关参数设置

    2、在Jenkins项目主页选择“配置”选项，进行参数设置。其配置文件是config.xml

		1、在Job Notifications下配置参数化构建过程。
	        参数名 			 	参数类型 	参数值列表
			BUILD_TYPE 	     	Choice 		Release or Debug
			IS_JENKINS 			Boolean 	true，设置是否通过Jenkins打包
			VERSION_CODE 		String 	    自定义显示在App上的版本号
	        BUILD_TIME 			Dynamic     return new Date().format('MMddHHmm')

        2、在Git Parameter可以设置git代码，关联相关分支，设置默认值。
    
        3、配置源码管理，关联git仓库。设置Branches to build 值为Git Parameter配置的name值。例如：$GIT_BRANCH

        4、构建触发器：Poll SCM：定时检查源码变更（根据SCM软件的版本号），如果有更新就checkout最新code下来，然后执行构建动作。

        5、构建：选择Invoke Gradle Script构建，配置gradle命令。
		   选择Invoke Gradle script之后可以选Invoke Gradle和Use Gradle Wrapper，选择Invoke Gradle就是调用本地安装配置好的Gradle，此时需要指定Gradle路径。
           为了方便所有开发者同意Gradle版本，一般都使用Gradle Wrapper。

           使用Invoke Gradle构建，命令配置在task下
           使用Gradle Wrapper构建，命令配置在Switches下
		   1、clean build  --stacktrace --debug
           2、clean assemble${PRODUCT_FLAVORS}${BUILD_TYPE}

        6、构建后操作
           1、配置了Archive the artifacts
              在“用于存档的文件”中填写需要存档的文件名，可以使用通配符。比如上面我配置了app/build/outputs/apk/*.apk，构建完之后在任务首页可以下载存档的文件

           2、配置Editable Email Notification，发送邮件             
            