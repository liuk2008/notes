
**Gradle基础知识**

	* Gradle构建工具
		* Android构建系统就是由Gradle的Android插件组成的，Gradle是一个高级构建工具包，它管理依赖项并允许开发者自定义构建逻辑。
		* Android Studio使用Gradle wrapper来集成Gradle的Android插件。需要注意的是，Gradle的Android插件也可以独立于AndroidStudio运行。 
	* 在Gradle中，有两个基本概念：项目和任务。请看以下详解：
		* 项目是指我们的构建产物（比如Jar包）或实施产物（将应用程序部署到生产环境），一个项目包含一个或多个任务
		* 任务是指不可分的最小工作单元，执行构建工作（比如编译项目或执行测试）
		* 每一次Gradle的构建都包含一个或多个项目。
	* build --> project --> task
		* Gradle构建脚本（build.gradle）指定了一个项目和它的任务
		* Gradle属性文件（gradle.properties）用来配置构建属性
		* Gradle设置文件（gradle.settings）对于只有一个项目的构建而言是可选的，如果我们的构建中包含多于一个项目，那么它就是必须的
	* Gradle Wrapper
	    * 主要作用是在本地没有安装Gradle的情况下可以运行Gradle构建，指定Gradle版本，开发人员可以快速启动并运行Gradle项目，而不必手动安装，这样就标准化了项目
	    * 当使用Gradle Wrapper启动Gradle时，如果指定版本的Gradle没有被下载关联，会先从Gradle官方仓库下载该版本Gradle到用户本地，进行解包并执行批处理文件
	* Gradle任务
		task hello {  // task代表一个独立的原子性操作
		    doLast {
		        println 'Hello world!' // doLast 代表task执行的最后一个action，通俗来讲就是task执行完毕后会回调doLast中的代码
		    }
		}

		task hello << {
		    println 'Hello world!'
		}
	* Gradle插件
		* 脚本插件：其实就是一个普通的build.gradle
		* 对象插件：也叫二进制插件，是实现了org.gradle.api.Plugins<Project>接口的插件，对象插件可以分为内部插件和第三方插件
	* 全局配置
		* 使用ext块配置，在项目build.gradle中使用ext块，如下
			ext {
			    android =[
			            compileSdkVersion : 28,
			            buildToolsVersion : "28.0.3",
			            minSdkVersion : 19,
			            targetSdkVersion : 28,
			            versionCode : 1,
			            versionName : "1.0.0"
			    ]
			    dependencies = [
			        "appcompat-v7":"com.android.support:appcompat-v7:28.0.0"
			    ]
			    date = getDate()
			}
		* 在module中，引用方式：
			* rootProject.ext.date 
			* rootProject.ext.android.minSdkVersion
			* rootProject.ext.dependencies["appcompat-v7"] 
			
**Gradle常用命令**

    * gradlew tasks：查看任务列表
    * gradlew -v 版本号
    * gradlew clean 清除9GAG/app目录下的build文件夹
    * gradlew build 检查依赖并编译打包
    * gradlew assemle 编译打包
    * gradlew assembleDebug 编译并打Debug包
    * gradlew assembleRelease 编译并打Release的包
    * gradlew installRelease Release模式打包并安装
    * gradlew uninstallRelease 卸载Release模式包
    * build任务会执行一个完整的项目构建，执行自动化测试。
    * assemble任务会编译程序中的源代码，并打包生成Jar文件，这个任务不执行单元测试。
    * clean任务会删除构建目录。
    * compileJava任务会编译程序中的源代码。