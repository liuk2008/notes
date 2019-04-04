
**Gradle脚本配置**

    apply plugin: 'com.android.application' //说明 module 的类型，com.android.application 为程序
 
    def releaseTime() {                                      // 配置打包时间 
    	return new Date().format("yyyy-MM-dd", TimeZone.getTimeZone("UTC"))
	}
	def yunxin_app_key = "35e80edc0b5c1ad9a4d15a657d7d8bee"  // 声明变量

	android {
	    compileSdkVersion    22 			    			 //编译的SDK版本
	    buildToolsVersion    "22.0.1" 			 			 //编译的 Tools 版本

	    // 默认配置
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
    
        // 目录指向配置
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
 
        // 签名配置，可以配置不同版本的签名
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

                buildConfigField "String", "server_url", "\"http://10.30.50.152:8071/qingqinew\""    // 配置服务器地址

                //在 AndroidManifest 配置引用 ${mapbar_push_key}
                manifestPlaceholders = [
                    mapbar_push_key: "test-769b0e2dcb354bf99ed73f4ab23a2f30"
           		]

	        }

			qa {
				// library包中默认buildType：release，debug，matchingFallbacks指定lib包需要加载的变体版本
           		matchingFallbacks = ['debug']
			}

            //  配置打包信息，注意修改包名
		      android.applicationVariants.all { variant ->
	            variant.outputs.all { output ->
	                def outputFileName = output.outputFileName
	                if (outputFileName != null && outputFileName.endsWith('.apk')) {
	                    def fileName = "Jenkins_v${defaultConfig.versionName}_${rootProject.date}_${variant.name}.apk"
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

