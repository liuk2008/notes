
##Gradle引入第三方库文件的总结##

**1、引入jar文件**

* 将jar文件复制至app module目录下的libs文件夹下，然后打开app module目录下的build.gradle配置文件，在dependencies项中添加配置命令，这里有两种配置方式可供选择：

	    * 一次性引入libs目录下所有jar文件
	     implementation fileTree(include: ['*.jar'], dir: 'libs')
	
	    * 单个逐一引入jar文件
	     implementation files('libs/universal-image-loader-1.8.6-with-sources.jar') 


**2、引入so文件**

* 1、新版Gradle实现了自动打包编译so文件的功能，并且为so文件指定了默认的目录app/src/main/jniLibs，当然默认是没有这个文件夹的，我们只需要新建一个jniLibs文件夹，并将so文件复制到该文件夹下，编译运行即可。
* 2、jar文件和so文件放在一起时，统一搁置在app/libs目录下，此时，我们只需要在build.gradle的android一栏中添加如命令，指定so文件的目录即可：

	    sourceSets {
	        main {
	                jniLibs.srcDirs = ['libs']
	                // jniLibs.srcDir 'libs'
	        }
	    }


**3、引入aidl文件**
   
* aidl文件指定了默认的目录app/src/main/aidl，我们只需要新建一个aidl文件夹，根据aidl文件中的包名创建对应包，并将aidl文件复制到该包下，编译运行即可。
 

**4、引入Library库文件**

* 将第三方Library库文件复制到项目根目录下，打开项目根目录下的settings.gradle文件，添加配置命令，如：include ':xxx'，在module目录下的build.gradle，添加配置命令，如：implementation project(':xxxx')
 
 
**5、引入aar文件**

* 将aar文件复制到app module目录下的libs文件夹中，然后打开app module目录下的build.gradle配置文件：

		在android一栏中添加依赖：
	     repositories {
	     	  flatDir {
	        	dirs 'libs'
	    	 }
		 }
		然后再在dependencies一栏中添加：
		implementation(name:'qiniu-android-sdk-7.2.0', ext:'aar')


**6、引入jcenter、maven仓库文件**

* 在项目根目录的build.gradle文件中添加仓库，然后再各个Module的build.gradle配置文件的dependencies项中添加依赖

		allprojects {
		    repositories {
		        jcenter()
		        maven { url 'https://dl.bintray.com/aerozhonghuan/maven/' }
		    }
		}
    	implementation 'com.squareup.okhttp3:okhttp:3.2.0'


**Gradle设置使用多种类型的仓库**

* 1、Maven central repository，这是Maven的中央仓库，无需配置，直接声明就可以使用。但不支持https协议访问

		在build.gradle中配置如下参数，就可以直接使用了
		repositories {
		    mavenCentral()
		}

* 2、Maven JCenter repository，JCenter中央仓库，实际也是是用的maven搭建的，但相比Maven仓库更友好，通过CDN分发，并且支持https访问
		
		https：
			repositories {
			    jcenter()
			}
		http:
			repositories {
			    jcenter {
			        url "http://jcenter.bintray.com"
			    }
			}
  
* 3、Local Maven repository，Maven本地的仓库，可以通过本地配置文件进行配置

		repositories {
		    mavenLocal()
		}
 
* 4、Maven repositories，常规的第三方Maven仓库，可设置访问Url

		repositories {
		    maven {
		        url "http://repo.mycompany.com/maven2"
		    }
		}   

* 5、Ivy repository，Ivy仓库，可以是本地仓库，也可以是远程仓库

		repositories {
		    ivy {
		        url "http://repo.mycompany.com/repo"
		    }
		}

* 6、Flat directory repository，使用本地文件夹作为仓库，直接在build.gradle中声明文件夹路径：
		
		repositories {
		    flatDir {
		        dirs 'lib'
		    }
		}
