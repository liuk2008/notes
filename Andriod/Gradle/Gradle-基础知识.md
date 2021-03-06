
## Gradle基础知识 ##

**Gradle构建工具**

* Android构建系统就是由Gradle的Android插件组成的，Gradle是一个高级构建工具包，它管理依赖项并允许开发者自定义构建逻辑。
* Android Studio使用Gradle wrapper来集成Gradle的Android插件。需要注意的是，Gradle的Android插件也可以独立于AndroidStudio运行。
* 在Gradle中，有两个基本概念：Project和Task
	* 每个项目的编译至少有一个 Project，一个 build.gradle就代表一个project，
	* 每个project里面包含了多个task，task 里面又包含很多action，action是一个代码块，里面包含了需要被执行的代码。
	* build --> project --> task，没有被依赖的 task 会首先被执行
* Gradle Wrapper
    * 主要作用是在本地没有安装Gradle的情况下可以运行Gradle构建，指定Gradle版本，开发人员可以快速启动并运行Gradle项目，而不必手动安装，这样就标准化了项目
    * 当使用Gradle Wrapper启动Gradle时，如果指定版本的Gradle没有被下载关联，会先从Gradle官方仓库下载该版本Gradle到用户本地，进行解包并执行批处理文件

**Gradle插件**

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
* gradlew -v：版本号
* gradlew clean：清除9GAG/app目录下的build文件夹
* gradlew build 检查依赖并编译打包
* gradlew assemle：编译打包
* gradlew assembleDebug：编译并打Debug包
* gradlew assembleRelease：编译并打Release的包
* gradlew installRelease：Release模式打包并安装
* gradlew uninstallRelease：卸载Release模式包
* build：任务会执行一个完整的项目构建，执行自动化测试。
* assemble：任务会编译程序中的源代码，并打包生成Jar文件，这个任务不执行单元测试
* clean：任务会删除构建目录
* compileJava：任务会编译程序中的源代码


**Gradle依赖**

* implementation：
	* 1、在编译时不会将依赖的实现暴露给其他module，也就是只有在运行时其他module才能访问这个依赖中的实现（因为apk的所有dex都会放到classLoader的dexPathList中）
	* 2、libA与libB之间使用implementation依赖，libB与libC之间使用implementation依赖，当libA接口修改后重新编译时，只会重新编译libA、libB
* api：
	* 1、其他module无论在编译时和运行时都可以访问这个依赖
	* 2、libA与libB之间使用api依赖，libB与libC之间使用implementation依赖，当libA接口修改后重新编译时，会重新编译libA、libB、libC（即使libC中并没有用到修改的libA的接口）
* 解决依赖冲突 ：
	* 其存在冲突的module中的build.gradle文件中加入下面代码，原理就是通过遍历所有依赖，并修改指定库的版本号
	
			configurations.all {
			    resolutionStrategy.eachDependency { DependencyResolveDetails details ->
			        def requested = details.requested
			        if (requested.group == 'com.android.support') { // com.android.support表示要修改的依赖库
			            if (!requested.name.startsWith("multidex")) {
			                details.useVersion '28.0.0' //  28.0.0表示要修改的版本号
			            }
			        }
			    }
			}	


**Gradle任务**

* apply

		* apply plugin: 'com.android.application'
		* 底层调用了project对象的apply方法，传入了一个以plugin为key的map。完整写出来就是这样的：
		* project.apply([plugin: 'com.android.application'])
	
* dependencies

		dependencies {
			implementation 'xxx.xx.xx:xx:1.0'
		}
		* 底层调用的时候会传入一个DependencyHandler的闭包,代码如下:
		project.dependencies({
			add('implementation','xxx.xx.xx:xx:1.0',{
		
		    })
		})

* task

		task hello {  // task代表一个独立的原子性操作
		// doLast 代表task执行的最后一个action，通俗来讲就是task执行完毕后会回调doLast中的代码
		    doLast {
		        println 'Hello world!' 
		    }
		}
		task hello << { // 操作符<< 是doLast方法的快捷版本
		    println 'Hello world!'
		}

* dependsOn

		task task1 <<{
		    println 'task1'
		}
		
		task task2 <<{
		    println 'task2'
		}
		task2.dependsOn task1
		
		gradlew task2同时执行两个任务，gradlew task1只执行task1任务
