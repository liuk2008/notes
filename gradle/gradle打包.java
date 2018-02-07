

1、gradle安装路径
Win平台会默认下载到 C:\Documents and Settings<用户名>.gradle\wrapper\dists 目录

2、build.gradle配置文件内容

// 声明是Android程序
apply plugin: 'com.android.application'

// 添加打包时间
def releaseTime() {
    return new Date().format("yyyy-MM-dd", TimeZone.getTimeZone("UTC"))
}

android {
    // 编译SDK的版本
    compileSdkVersion 21
    // build tools的版本
    buildToolsVersion "21.1.1"

    defaultConfig {
     // 应用的包名
        applicationId "me.storm.ninegag"
        minSdkVersion 14
        targetSdkVersion 21
        versionCode 1
        versionName "1.0.0"
	// dex突破65535的限制
        multiDexEnabled true
	// 可以编写默认配置信息，在AndroidManifest.xml中动态引用
	    manifestPlaceholders = [
                BAIDU_API_KEY: "AqnDKxCPkdexd8wcCFbyYH24",
       ]

	}

    // 签名配置文件 
    signingConfigs {
        myConfig {
            storeFile file("release.keystore")
            storePassword "aerozhonghuan"
            keyAlias "bd56"
            keyPassword "aerozhonghuan"
        }
    }

    // java版本
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_7
        targetCompatibility JavaVersion.VERSION_1_7
    }
    
	// 打包不同包名的APK，动态设置的数据生成在BuildConfig中，可以通过代码动态引用BuildConfig
    productFlavors {

        Flavor1 {
            applicationId "com.android.application.Flavors1";
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

		Flavor2 {
            applicationId "com.android.application.Flavors2";
            signingConfig signingConfigs.myConfig
			// 动态设置不同版本下的字符串
            buildConfigField "String", "server_release", "\"http://61.132.221.5:80\";"
            buildConfigField "String", "server_debug", "\"http://211.103.179.53:8080\";"
            buildConfigField "String", "server_debug_internal", "\"http://192.168.66.46:9999\";"
			// 编写配置信息，在AndroidManifest.xml中动态引用
            manifestPlaceholders = [
                    BAIDU_API_KEY: "AqnDKxCPkdexd8wcCFbyYH26",
            ]
        }
   }


    buildTypes {
        debug {
            // debug模式
        }
        
        release {
			// 显示Log
            buildConfigField "boolean", "LOG_DEBUG", "true"
            // 是否进行混淆
            minifyEnabled false
            // 混淆文件的位置
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.txt'
		    // 使用签名文件
	        signingConfig signingConfigs.myConfig
	        // 移除无用的resource文件
	        shrinkResources true
		    // Zipalign优化
			zipAlignEnabled false
			// 开启调试
            debuggable true
        }
    }
    
    // 移除lint检查的error
    lintOptions {
      abortOnError false
    }

   android.applicationVariants.all { variant ->
    variant.outputs.each { output ->
	def outputFile = output.outputFile
	if (outputFile != null && outputFile.name.endsWith('.apk')) {
	    // 输出apk名称为DriverTrainingTV_v1.0_2015-01-15_debug/release.apk
	    def fileName = "DriverTrainingTV_v${defaultConfig.versionName}_${releaseTime()}_${variant.name}.apk"
	    output.outputFile = new File(outputFile.parent, fileName)
	 }
      }
   }
   
    // 手动指明jnilib在
    sourceSets {
        main {
            jniLibs.srcDir 'libs'
        }
    }
}

dependencies {
    // 编译libs目录下的所有jar包
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile files('libs/android_api_3_4.jar')
    // 编译extras目录下的ShimmerAndroid模块
    compile project(':extras:ShimmerAndroid')
    compile 'com.android.support:support-v4:21.0.2'
}

3、gradle常用命令
   
   gradlew -v 版本号
   gradlew clean 清除9GAG/app目录下的build文件夹
   gradlew build 检查依赖并编译打包
   gradlew assembleDebug 编译并打Debug包
   gradlew assembleRelease 编译并打Release的包
   gradlew installRelease Release模式打包并安装
   gradlew uninstallRelease 卸载Release模式包

 4、gradle多渠道打包

   以友盟统计为例：里面的Channel_ID就是渠道标示。我们的目标就是在编译的时候这个值能够自动变化。
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


 5、assemble结合Build Variants来创建task

 assemble 和 Product Flavor 结合创建新的任务，其实 assemble 是和 Build Variants 一起结合使用的，而 
 Build Variants = Build Type + Product Flavor ， 举个例子大家就明白了：
 
 1、构建一个flavor渠道的release版本，执行如下命令就好了：gradlew assembleFlavor1Release
 
 2、构建指定flavor的所有APK：gradlew assembleFlavor1此命令会生成Flavor1渠道的Release和Debug版本.
 
 3、构建productFlavor下的所有渠道的版本：gradlew assembleDebug允许构建指定buildType的所有APK，
    Flavor1Debug和Flavor2Debug两个Variant版本。

