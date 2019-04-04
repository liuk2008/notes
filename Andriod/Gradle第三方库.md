
**Gradle引入第三方库文件的总结**

  1、引入jar文件

    将jar文件复制至app module目录下的libs文件夹下，然后打开app module目录下的
    build.gradle配置文件，在dependencies项中添加配置命令，这里有两种配置方式可供选择：

    * 一次性引入libs目录下所有jar文件
     implementation fileTree(include: ['*.jar'], dir: 'libs')

    * 单个逐一引入jar文件
     implementation files('libs/universal-image-loader-1.8.6-with-sources.jar') 

  2、引入so文件
   
    Gradle 新版本

    新版Gradle实现了自动打包编译so文件的功能，并且为so文件指定了默认的目录app/src/main/jniLibs，
    当然默认是没有这个文件夹的，我们只需要新建一个jniLibs文件夹，并将so文件复制到该文件夹下，编译运行即可。

    通常，为了更好地管理第三方库文件，或者更简单地将Eclipse项目转化为Android Studio项目，建议将jar文
    件和so文件放在一起，统一搁置在app/libs目录下，此时，我们只需要在build.gradle的android一栏中添加
    如命令，指定so文件的目录即可：

    sourceSets {
        main {
                jniLibs.srcDirs = ['libs']
                // jniLibs.srcDir 'libs'
        }
    }

  3、引入aidl文件
   
    aidl文件指定了默认的目录app/src/main/aidl，我们只需要新建一个aidl文件夹，根据aidl文件中的包名创建对应包，
    并将aidl文件复制到该包下，编译运行即可。
 
  4、引入Library库文件

    将第三方Library库文件复制到项目根目录下，打开项目根目录下的settings.gradle文件，添加配置命令，如：
    include ':app', ':PullToRefresh'
    在module目录下的build.gradle，添加配置命令，如：
    implementation project(':PullToRefresh')
  
  5、引入aar文件

    将aar文件复制到app module目录下的libs文件夹中，然后打开app module目录下的build.gradle配置文件，在android一栏中添加依赖：
     repositories {
     	  flatDir {
        	dirs 'libs'
    	 }
	 }
    然后再在dependencies一栏中添加：
    implementation(name:'qiniu-android-sdk-7.2.0', ext:'aar')

 6、引入jcenter、maven仓库文件

    在项目根目录的build.gradle文件中添加仓库，如：

	allprojects {
	    repositories {
	        jcenter()
	        maven { url 'https://dl.bintray.com/aerozhonghuan/maven/' }
	    }
	}

    然后再各个Module的build.gradle配置文件的dependencies项中添加依赖，格式为｀compile ‘name:version’｀，如：
    implementation 'com.squareup.okhttp3:okhttp:3.2.0'



**Gradle设置使用多种类型的仓库**

    Maven central repository 	这是Maven的中央仓库，无需配置，直接声明就可以使用。但不支持https协议访问
	Maven JCenter repository 	JCenter中央仓库，实际也是是用的maven搭建的，但相比Maven仓库更友好，通过CDN分发，并且支持https访问。
	Maven local repository 	    Maven本地的仓库，可以通过本地配置文件进行配置
	Maven repository 	        常规的第三方Maven仓库，可设置访问Url
	Ivy repository 	            Ivy仓库，可以是本地仓库，也可以是远程仓库
	Flat directory repository 	使用本地文件夹作为仓库

  1、Maven central repository

     在build.gradle中配置如下参数，就可以直接使用了。

		repositories {
		    mavenCentral()
		}

  2、Maven JCenter repository

    最常用也是Android Studio默认配置：

		repositories {
		    jcenter()
		}

    这时使用jcenter仓库是通过https访问的，如果想切换成http协议访问，需要修改配置：

		repositories {
		    jcenter {
		        url "http://jcenter.bintray.com"
		    }
		}
  
  3、Local Maven repository

    可以使用Maven本地的仓库。默认情况下，本地仓库位于USER_HOME/.m2/repository（例如windows环境中，在C:\Users\NAME.m2.repository），
    同时可以通过USER_HOME/.m2/下的settings.xml配置文件修改默认路径位置。若使用本地仓库在build.gradle中进行如下配置：

		repositories {
		    mavenLocal()
		}
 
  4、Maven repositories

    第三方的配置也很简单，直接指明url即可：

		repositories {
		    maven {
		        url "http://repo.mycompany.com/maven2"
		    }
		}   

  5、Ivy repository

    配置如下：

		repositories {
		    ivy {
		        url "http://repo.mycompany.com/repo"
		    }
		}

  6、Flat directory repository

    使用本地文件夹，这个也比较常用。直接在build.gradle中声明文件夹路径：

		repositories {
		    flatDir {
		        dirs 'lib'
		    }
	
     	    flatDir {
		        dirs 'lib1', 'lib2'
		    }
		}

    使用本地文件夹时，就不支持配置元数据格式的信息了（POM文件）。并且Gradle会优先使用服务器仓库中的库文件：
    例如同时声明了jcenter和flatDir，当flatDir中的库文件同样在jcenter中存在，gradle会优先使用jcenter的。
