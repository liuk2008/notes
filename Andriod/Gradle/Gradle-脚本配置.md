
**Gradle脚本配置**

	/**
	 * Gradle的Android插件常用类型：
	 * 应用程序插件，插件id为com.android.application，会生成一个APK
	 * 库插件，插件id为com.android.library，会生成一个AAR，提供给其他应用程序模块用
	 */	
    apply plugin: 'com.android.application' 
 
    def releaseTime() {                                      // 配置打包时间 
    	return new Date().format("yyyy-MM-dd", TimeZone.getTimeZone("UTC"))
	}

	def yunxin_app_key = "35e80edc0b5c1ad9a4d15a657d7d8bee"  // 声明变量

	android {
	    compileSdkVersion    22 			    			 // 编译的SDK版本
	    buildToolsVersion    "22.0.1" 			 			 // 编译的 Tools 版本

	    // 默认配置
	    defaultConfig {  								
	        applicationId    "com.nd.famlink"   			 // 应用程序的包名
	        minSdkVersion     8                 			 // 支持的最低版本
	        targetSdkVersion  19                 			 // 支持的目标版本
	        versionCode    	  3                				 // 版本号
	        versionName      "3.0.1"            			 // 版本名
            testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
            multiDexEnabled true                             // dex突破65535的限制
			flavorDimensions ""          					 // 配置多渠道打包

			// 配置ndk使用的so文件 
		    ndk {
		            abiFilters "armeabi", "armeabi-v7a"
		    }

			// 在 AndroidManifest 配置引用 ${YUNXIN_APP_KEY}，可配置多个参数
		    manifestPlaceholders = [
		                YUNXIN_APP_KEY: yunxin_app_key
		    ]
	    }

		// 配置不同版本的签名
	    signingConfigs {  						 		
	        release {  							
	            storeFile file("fk.keystore")  // 密钥库路径，当前module路径下
	            storePassword  "123" 		   // 密钥库密码
	            keyAlias       "fk" 		   // 密钥别名
	            keyPassword    "123" 		   // 密钥密码
				v2SigningEnabled false         // 禁止7.0 以上版本进行V2签名校验，兼容python多渠道打包                  
	        }
	        debug { 										 
	            storeFile file("../debug.keystore")          // 根目录下
	            storePassword  "123"
	            keyAlias       "fk"
	            keyPassword    "123"
	        }
	    }

		// buildTypes块用于配置构建不同类型的APK，默认提供给debug、release两个配置
        buildType { 		
			
	        release {  						
	            minifyEnabled  true 						 //混淆开启
                zipAlignEnabled false                        // Zipalign优化，默认值为false 
                shrinkResources false                        // 移除无用的resource文件，默认值为false
                debuggable true                              // 开启调试
	            proguardFiles  getDefaultProguardFile('proguard-android.txt'),'proguard-android.txt'  // 指定混淆规则文件
	            signingConfig  signingConfigs.release 		 // 设置签名信息
                applicationIdSuffix ".release"               // 配置applicationId的后缀
                buildConfigField "String", "server_url", "\"http://10.30.50.152:8071/qingqinew\""    // 配置服务器地址
                manifestPlaceholders = [
                    mapbar_push_key: "test-769b0e2dcb354bf99ed73f4ab23a2f30"  //在 AndroidManifest 配置引用 ${mapbar_push_key}               
           		]
				jniDebuggable    fale                        // 表示是否可以调试NDK代码
                multiDexEnabled  false                       // 是否启用自动拆分多个Dex的功能
	        }

			qa {
				// library包中默认buildType：release，debug，matchingFallbacks指定lib包需要加载的变体版本
           		matchingFallbacks = ['debug']
			}

	    }

		// 配置多渠道打包，可自定义配置
		productFlavors{
	        xiaomi {
			    applicationId "xxx.xxx.xx" // 配置不同的签名
	            versionName "1.0"
				signingConfig signingConfigs.myConfig
				// 如果配置参数相同，优先取值buildTypes中的参数
	            buildConfigField "String", "server_url", "\"http://61.132.221.5:80\""
				// 编写配置信息，在AndroidManifest.xml中动态引用
	            manifestPlaceholders = [
	                    YUNXIN_APP_KEY: "xxx"
	            ]
			}
	    }

		// 配置目录指向
		sourceSets {
	        main {
				manifest.srcFile  'AndroidManifest.xml'      // 指定 AndroidManifest 文件
	            java.srcDirs = ['src']  					 // 指定 source 目录
	            resources.srcDirs = ['src']                  // 指定 source 目录
	            aidl.srcDirs = ['src']                       // 指定 source 目录
	            renderscript.srcDirs = ['src']               // 指定 source 目录
	            res.srcDirs = ['res']                        // 指定资源目录
	            assets.srcDirs = ['assets']                  // 指定 assets 目录
	            jniLibs.srcDirs = ['libs']                   // 指定 lib 库目录
	        }
			// 路径：app\build-types\release\res\layout\xxx.xml
	        debug.setRoot('build-types/debug')               // 指定 debug 模式的路径
            release.setRoot('build-types/release')           // 指定 release 模式的路径
	    }

	    // 指定 aar 路径
	    repositories {
	        flatDir {
	            dirs 'libs'
	        }
	    }
			
		// java版本
	    compileOptions {
	        sourceCompatibility JavaVersion.VERSION_1_8
	        targetCompatibility JavaVersion.VERSION_1_8
	    }

		// lint 时候终止错误上报，防止编译的时候莫名的失败
	    lintOptions {
	        abortOnError   false 	
			disable 'GoogleAppIndexingWarning'
	    }

		// 配置打包名称
	    android.applicationVariants.all { variant ->
	        variant.outputs.all { output ->
	            def outputFileName = output.outputFileName
	            if (outputFileName != null && outputFileName.endsWith('.apk')) {
	                def fileName = "AndroidDemo_v${defaultConfig.versionName}_${rootProject.date}_${variant.name}.apk"
	                output.outputFileName = new File(fileName)
	            }
	        }
	    }

    }

	dependencies  {
	    implementation  fileTree(include: ['*.jar'], dir: 'libs')  // 编译lib 目录下的 jar 文件
	    implementation  project(':Easylink')                       // 编译附加的项目
	    implementation 'com.jakewharton:butterknife:8.4.0'         // 编译第三方开源
		implementation(name: 'common-1.0.13', ext: 'aar')		   // aar 引用
	}


**Gradle 多渠道打包**	
	
* 配置两个维度

		flavorDimensions "company","channel"

* 配置产品的多维度

		productFlavors{
	        companyA{
	            dimension "company"
	            buildConfigField "String","FLAVOR_NAME","\"companyA\""
	        }
	        companyB{
	            dimension "company"
	            buildConfigField "String","FLAVOR_NAME","\"companyB\""
	        }
	        channelA{
	            dimension "channel"
	            buildConfigField "String","FLAVOR_NAME","\"channelA\""
	        }
	        channelB{
	            dimension "channel"
	            buildConfigField "String","FLAVOR_NAME","\"channelB\""
	        }
	    }

* 通过productFlavors配置，会修改buildConfig类中的FLAVOR字段，这会导致生成的不同渠道包的dex是不一样，不过可以单独配置各个渠道参数（无法使用热修复功能）