

**Gradle命令总结**

	1、Gradle基础知识

	* 在Gradle中，有两个基本概念：项目和任务。请看以下详解：
		* 项目是指我们的构建产物（比如Jar包）或实施产物（将应用程序部署到生产环境）。一个项目包含一个或多个任务。
		* 任务是指不可分的最小工作单元，执行构建工作（比如编译项目或执行测试）。
		* 每一次Gradle的构建都包含一个或多个项目。
	* build --> project --> task
		* Gradle构建脚本（build.gradle）指定了一个项目和它的任务。
		* Gradle属性文件（gradle.properties）用来配置构建属性。
		* Gradle设置文件（gradle.settings）对于只有一个项目的构建而言是可选的，如果我们的构建中包含多于一个项目，那么它就是必须的，
    * 因为它描述了哪一个项目参与构建。每一个多项目的构建都必须在项目结构的根目录中加入一个设置文件。
    * Gradle安装路径：Win平台会默认下载到 C:\Documents and Settings<用户名>.gradle\wrapper\dists 目录

	2、Gradle 命令
	    * gradlew tasks：查看任务列表
	    * gradlew -v 版本号
	    * gradlew clean 清除9GAG/app目录下的build文件夹
	    * gradlew build 检查依赖并编译打包
	    * gradlew assemle 编译打包
	    * gradlew assembleDebug 编译并打Debug包
	    * gradlew assembleRelease 编译并打Release的包
	    * gradlew installRelease Release模式打包并安装
  	    * gradlew uninstallRelease 卸载Release模式包
	    * assemble任务会编译程序中的源代码，并打包生成Jar文件，这个任务不执行单元测试。
	    * build任务会执行一个完整的项目构建，执行自动化测试。
	    * clean任务会删除构建目录。
	    * compileJava任务会编译程序中的源代码。
		
	3、Gradle多渠道打包

	* 以友盟统计为例：里面的Channel_ID就是渠道标示。我们的目标就是在编译的时候这个值能够自动变化。
		   <meta-data
		    android:name="UMENG_CHANNEL"
		    android:value="Channel_ID" />
		
		   第一步 在AndroidManifest.xml里配置PlaceHolder
		   <meta-data
		    android:name="UMENG_CHANNEL"
		    android:value="${UMENG_CHANNEL_VALUE}" />
		
		   第二步 在build.gradle设置productFlavors

		    productFlavors {
		        xiaomi {
		            manifestPlaceholders = [UMENG_CHANNEL_VALUE: "xiaomi"]
		        }

		        baidu {
		            manifestPlaceholders = [UMENG_CHANNEL_VALUE: "baidu"]
		        } 
		    }
		   或者批量修改
				productFlavors {
					xiaomi {}
					baidu {}
				}  
		
				productFlavors.all { 
					flavor -> flavor.manifestPlaceholders = [UMENG_CHANNEL_VALUE: name] 
				}

	* assemble结合Build Variants来创建task

		 assemble 和 Product Flavor 结合创建新的任务，其实 assemble 是和 Build Variants 一起结合使用的，而 
		 Build Variants = Build Type + Product Flavor ， 举个例子大家就明白了：
		 
		 1、构建一个flavor渠道的release版本，执行如下命令就好了：gradlew assembleFlavor1Release
		 
		 2、构建指定flavor的所有APK：gradlew assembleFlavor1此命令会生成Flavor1渠道的Release和Debug版本.
		 
		 3、构建productFlavor下的所有渠道的版本：gradlew assembleDebug允许构建指定buildType的所有APK，
		    Flavor1Debug和Flavor2Debug两个Variant版本。

