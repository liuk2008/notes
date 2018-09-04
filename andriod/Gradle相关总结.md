

**Gradle相关总结**

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
		   android {  
		    productFlavors {
		        xiaomi {
		            manifestPlaceholders = [UMENG_CHANNEL_VALUE: "xiaomi"]
		        }
		        _360 {
		            manifestPlaceholders = [UMENG_CHANNEL_VALUE: "_360"]
		        }
		        baidu {
		            manifestPlaceholders = [UMENG_CHANNEL_VALUE: "baidu"]
		        }
		        wandoujia {
		            manifestPlaceholders = [UMENG_CHANNEL_VALUE: "wandoujia"]
		        }
		     }  
		   }
		   或者批量修改
		
		   android {  
				productFlavors {
					xiaomi {}
					_360 {}
					baidu {}
					wandoujia {}
				}  
		
				productFlavors.all { 
					flavor -> flavor.manifestPlaceholders = [UMENG_CHANNEL_VALUE: name] 
				}
		  }

	* assemble结合Build Variants来创建task

		 assemble 和 Product Flavor 结合创建新的任务，其实 assemble 是和 Build Variants 一起结合使用的，而 
		 Build Variants = Build Type + Product Flavor ， 举个例子大家就明白了：
		 
		 1、构建一个flavor渠道的release版本，执行如下命令就好了：gradlew assembleFlavor1Release
		 
		 2、构建指定flavor的所有APK：gradlew assembleFlavor1此命令会生成Flavor1渠道的Release和Debug版本.
		 
		 3、构建productFlavor下的所有渠道的版本：gradlew assembleDebug允许构建指定buildType的所有APK，
		    Flavor1Debug和Flavor2Debug两个Variant版本。

**Android Studio 使用Gradle引入第三方库文件的总结**

  1、引入jar文件

    将jar文件复制至app module目录下的libs文件夹下，然后打开app module目录下的
    build.gradle配置文件，在dependencies项中添加配置命令，这里有两种配置方式可供选择：

    * 一次性引入libs目录下所有jar文件
     compile fileTree(include: ['*.jar'], dir: 'libs')

    * 单个逐一引入jar文件
     compile files('libs/universal-image-loader-1.8.6-with-sources.jar') 

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

    然后打开app module目录下的build.gradle，添加配置命令，如：
    compile project(':PullToRefresh')

    小技巧：推荐在项目根目录下新建一个文件夹，如extras文件夹，将所有Library库文件都复制到该文件下，方便统一浏览管理，
    这样上面两步对应的配置命令将变成：

    include ':app', ':extras:PullToRefresh'
    和
    compile project(':extras:PullToRefresh')
  
  5、引入aar文件
   
    aar其实也是一个压缩文件，相比jar文件，它能够含带res资源文件等，aar文件的引入方式有两种：

    1、Module形式引入
    选择File菜单，或者打开Project Structure界面，添加新的Module（New Module…），选择Import .JAR/.AAR Package，
    选择目标aar文件导入。导入之后，在项目根目录下会自动生成一个新的文件夹放置aar文件及其配置文件，然后打开app module目录
    下的build.gradle配置文件，在dependencies依赖项中添加配置即可：
     
    compile project(':qiniu-android-sdk-7.2.0')

    注意：这种引入方式无法查看aar文件中的代码和资源等文件。

    2、libs目录中引入
    将aar文件复制到app module目录下的libs文件夹中，然后打开app module目录下的build.gradle配置文件，在android一栏中添加依赖：
     repositories {
     		flatDir {
        		dirs 'libs'
    	 }
	}
    然后再在dependencies一栏中添加：
    compile(name:'qiniu-android-sdk-7.2.0', ext:'aar')

 6、引入jcenter、maven仓库文件

    在项目根目录的build.gradle文件中添加仓库，如：

	allprojects {
	    repositories {
	        jcenter()
	        maven { url 'https://dl.bintray.com/aerozhonghuan/maven/' }
	    }
	}

    然后再各个Module的build.gradle配置文件的dependencies项中添加依赖，格式为｀compile ‘name:version’｀，如：
    compile 'com.squareup.okhttp3:okhttp:3.2.0'



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

**module 的 build.gradle 文件注解**

    apply plugin: 'com.android.application' //说明 module 的类型，com.android.application 为程序
 
    def releaseTime() {                                      // 配置打包时间 
    	return new Date().format("yyyy-MM-dd", TimeZone.getTimeZone("UTC"))
	}
	def yunxin_app_key = "35e80edc0b5c1ad9a4d15a657d7d8bee"  // 声明变量

	android {
	    compileSdkVersion    22 			    			 //编译的SDK版本
	    buildToolsVersion    "22.0.1" 			 			 //编译的 Tools 版本

	    //默认配置
	    defaultConfig {  								
	        applicationId    "com.nd.famlink"   			 //应用程序的包名
	        minSdkVersion     8                 			 //支持的最低版本
	        targetSdkVersion  19                 			 //支持的目标版本
	        versionCode    	  3                				 //版本号
	        versionName      "3.0.1"            			 //版本名
            testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
            multiDexEnabled true                             //dex突破65535的限制

			// 配置ndk使用的so文件 
		    ndk {
		            abiFilters "armeabi", "armeabi-v7a"
		    }

			// 在 AndroidManifest 配置引用 ${YUNXIN_APP_KEY}，可配置多个参数
		    manifestPlaceholders = [
		                YUNXIN_APP_KEY: yunxin_app_key,
		    ]
	    }

	    // java版本
	    compileOptions {
	        sourceCompatibility JavaVersion.VERSION_1_8
	        targetCompatibility JavaVersion.VERSION_1_8
	    }
    
        //目录指向配置
    	sourceSets {     								 
       		main {
	            manifest.srcFile  'AndroidManifest.xml'      //指定 AndroidManifest 文件
	            java.srcDirs = ['src']  					 //指定 source 目录
	            resources.srcDirs = ['src']                  //指定 source 目录
	            aidl.srcDirs = ['src']                       //指定 source 目录
	            renderscript.srcDirs = ['src']               //指定 source 目录
	            res.srcDirs = ['res']                        //指定资源目录
	            assets.srcDirs = ['assets']                  //指定 assets 目录
	            jniLibs.srcDirs = ['libs']                   //指定 lib 库目录
            }
            debug.setRoot('build-types/debug')               //指定 debug 模式的路径
            release.setRoot('build-types/release')           //指定 release 模式的路径
        }
 
        //签名配置，可以配置不同版本的签名

	    signingConfigs {  						 		
	        release {  										 //发布版本签名配置
	            storeFile file("fk.keystore")   			 //密钥文件路径，当前module路径下
	            storePassword  "123" 						 //密钥文件密码
	            keyAlias       "fk" 						 //key 别名
	            keyPassword    "123" 						 //key 密码
	        }
	        debug { 										 //debug版本签名配置
	            storeFile file("../debug.keystore")          //根目录下
	            storePassword  "123"
	            keyAlias       "fk"
	            keyPassword    "123"
	        }
	    }

		// productFlavors 产品渠道，默认不提供任何默认配置，在实际发布中，根据不同渠道，我们可能需要用不同的包名，服务器地址等
		productFlavors{
		     productA{
		            applicationId "com.crazyman.product.a" // 配置不同的签名
		            versionName "version-a-1.0"
					signingConfig signingConfigs.myConfig
					// 动态设置不同版本下的字符串
		            buildConfigField "String", "server_release", "\"http://61.132.221.5:80\";"
		            buildConfigField "String", "server_debug", "\"http://211.103.179.53:8080\";"
		            buildConfigField "String", "server_debug_internal", "\"http://192.168.66.46:9999\";"
					// 编写配置信息，在AndroidManifest.xml中动态引用
		            manifestPlaceholders = [
		                    BAIDU_API_KEY: "AqnDKxCPkdexd8wcCFbyYH25",
		            ]
		     }

	         productB{
		            applicationId "com.crazyman.product.b"
		            versionName "version-b-1.0"
	         }
        }

        //build 构建类型，AndroidStudio的Gradle组件默认提供给了“debug”“release”两个默认配置，此处用于配置是否需要混淆、是否可调试等
		//BuildVariants：每个buildtype和flavor组成一个buildvariant
	    
        buildType { 		
			
	        release {  						
	            minifyEnabled  true 						 //混淆开启
                zipAlignEnabled false                        // Zipalign优化
                shrinkResources false                        // 移除无用的resource文件
                debuggable true                              // 开启调试
	            proguardFiles  getDefaultProguardFile('proguard-android.txt'),'proguard-android.txt'  //指定混淆规则文件
	            signingConfig  signingConfigs.release 		 //设置签名信息
                applicationIdSuffix ".release"               //配置不同的applicationId
                buildConfigField "String", "server_url", "\"http://10.30.50.152:8071/qingqinew\";"    // 配置服务器地址
                //在 AndroidManifest 配置引用 ${mapbar_push_key}
                manifestPlaceholders = [
                    mapbar_push_key: "test-769b0e2dcb354bf99ed73f4ab23a2f30"
           		]
	        }

            //  配置打包信息，注意修改包名
	        android.applicationVariants.all { variant ->
	            variant.outputs.all { output ->
	                def outputFileName = output.outputFileName
	                if (outputFileName != null && outputFileName.endsWith('.apk')) {
	                    def fileName = "Jenkins_v${defaultConfig.versionName}_${releaseTime()}_${variant.name}.apk"
	                    output.outputFileName = new File(fileName)
	                }
	            }
	        }

	    }

		// lint 时候终止错误上报，防止编译的时候莫名的失败
	    lintOptions {
	        abortOnError   false 						
	    }

		// 手动指明jnilib在
	    sourceSets {
	        main {
	            jniLibs.srcDir 'libs'
	        }
	        debug.setRoot('build-types/debug')  // 指定 debug 模式的路径，配置不同的文件
	        release.setRoot('build-types/release')
	    }

	    // 指定aar 路径
	    repositories {
	        flatDir {
	            dirs 'libs'
	        }
	    }

    }

	dependencies  {
	    implementation  fileTree(include: ['*.jar'], dir: 'libs')  //编译lib 目录下的 jar 文件
	    implementation  project(':Easylink')                       //编译附加的项目
	    implementation 'com.jakewharton:butterknife:8.4.0'         //编译第三方开源
	}

