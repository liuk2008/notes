
**Gradle基础知识**

	* Gradle构建工具
		* Android构建系统就是由Gradle的Android插件组成的，Gradle是一个高级构建工具包，它管理依赖项并允许开发者自定义构建逻辑。
		* Android Studio使用Gradle wrapper来集成Gradle的Android插件。需要注意的是，Gradle的Android插件也可以独立于AndroidStudio运行。 
	* 在Gradle中，有两个基本概念：Project和Task
		* 每个项目的编译至少有一个 Project，一个 build.gradle就代表一个project，
		* 每个project里面包含了多个task，task 里面又包含很多action，action是一个代码块，里面包含了需要被执行的代码。
		* build --> project --> task，没有被依赖的 task 会首先被执行
	* Gradle Wrapper
	    * 主要作用是在本地没有安装Gradle的情况下可以运行Gradle构建，指定Gradle版本，开发人员可以快速启动并运行Gradle项目，而不必手动安装，这样就标准化了项目
	    * 当使用Gradle Wrapper启动Gradle时，如果指定版本的Gradle没有被下载关联，会先从Gradle官方仓库下载该版本Gradle到用户本地，进行解包并执行批处理文件
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
    * assemble任务会编译程序中的源代码，并打包生成Jar文件，这个任务不执行单元测试
    * clean任务会删除构建目录
    * compileJava任务会编译程序中的源代码

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
		    doLast {
		        println 'Hello world!' // doLast 代表task执行的最后一个action，通俗来讲就是task执行完毕后会回调doLast中的代码
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
		* gradlew task2同时执行两个任务，gradlew task1只执行task1任务
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
		    doLast {
		        println 'Hello world!' // doLast 代表task执行的最后一个action，通俗来讲就是task执行完毕后会回调doLast中的代码
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
		* gradlew task2同时执行两个任务，gradlew task1只执行task1任务

**Groovy in Gradle**

	* 变量：Groovy中用def关键字来定义变量，可以不指定变量的类型，默认访问修饰符是public
	* 方法：方法使用返回类型或def关键字定义，方法可以接收任意数量的参数，这些参数可以不申明类型，如果不提供可见性修饰符，则该方法为public。
			def add(int a,int b) { 
			 println a+b 
			}  

			def minus(a,b) {
			 println a-b
			}

			int minus(a,b) { 
			  return a-b 
			}

			int minus(a,b) { 
			  a-b 
			}
	* 数据类型主要有以下几种：
		* Java中的基本数据类型
		* Groovy中的容器类：List、Map
		* 闭包：一个开放的、匿名的、可以接受参数和返回值的代码块
			* { [closureParameters -> ] statements } 参数列表部分[closureParameters -> ]和语句部分 statements
			* 参数列表部分是可选的，如果闭包只有一个参数，参数名是可选的，Groovy会隐式指定it作为参数名
				{ it -> println it }   //it是一个显示参数 
				{ String a, String b ->                                
				    println "${a} is a ${b}"
				}
