

1��gradle��װ·��
Winƽ̨��Ĭ�����ص� C:\Documents and Settings<�û���>.gradle\wrapper\dists Ŀ¼

2��build.gradle�����ļ�����

// ������Android����
apply plugin: 'com.android.application'

// ��Ӵ��ʱ��
def releaseTime() {
    return new Date().format("yyyy-MM-dd", TimeZone.getTimeZone("UTC"))
}

android {
    // ����SDK�İ汾
    compileSdkVersion 21
    // build tools�İ汾
    buildToolsVersion "21.1.1"

    defaultConfig {
     // Ӧ�õİ���
        applicationId "me.storm.ninegag"
        minSdkVersion 14
        targetSdkVersion 21
        versionCode 1
        versionName "1.0.0"
	// dexͻ��65535������
        multiDexEnabled true
	// ���Ա�дĬ��������Ϣ����AndroidManifest.xml�ж�̬����
	    manifestPlaceholders = [
                BAIDU_API_KEY: "AqnDKxCPkdexd8wcCFbyYH24",
       ]

	}

    // ǩ�������ļ� 
    signingConfigs {
        myConfig {
            storeFile file("release.keystore")
            storePassword "aerozhonghuan"
            keyAlias "bd56"
            keyPassword "aerozhonghuan"
        }
    }

    // java�汾
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_7
        targetCompatibility JavaVersion.VERSION_1_7
    }
    
	// �����ͬ������APK����̬���õ�����������BuildConfig�У�����ͨ�����붯̬����BuildConfig
    productFlavors {

        Flavor1 {
            applicationId "com.android.application.Flavors1";
            signingConfig signingConfigs.myConfig
			// ��̬���ò�ͬ�汾�µ��ַ���
            buildConfigField "String", "server_release", "\"http://61.132.221.5:80\";"
            buildConfigField "String", "server_debug", "\"http://211.103.179.53:8080\";"
            buildConfigField "String", "server_debug_internal", "\"http://192.168.66.46:9999\";"
			// ��д������Ϣ����AndroidManifest.xml�ж�̬����
            manifestPlaceholders = [
                    BAIDU_API_KEY: "AqnDKxCPkdexd8wcCFbyYH25",
            ]
        }

		Flavor2 {
            applicationId "com.android.application.Flavors2";
            signingConfig signingConfigs.myConfig
			// ��̬���ò�ͬ�汾�µ��ַ���
            buildConfigField "String", "server_release", "\"http://61.132.221.5:80\";"
            buildConfigField "String", "server_debug", "\"http://211.103.179.53:8080\";"
            buildConfigField "String", "server_debug_internal", "\"http://192.168.66.46:9999\";"
			// ��д������Ϣ����AndroidManifest.xml�ж�̬����
            manifestPlaceholders = [
                    BAIDU_API_KEY: "AqnDKxCPkdexd8wcCFbyYH26",
            ]
        }
   }


    buildTypes {
        debug {
            // debugģʽ
        }
        
        release {
			// ��ʾLog
            buildConfigField "boolean", "LOG_DEBUG", "true"
            // �Ƿ���л���
            minifyEnabled false
            // �����ļ���λ��
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.txt'
		    // ʹ��ǩ���ļ�
	        signingConfig signingConfigs.myConfig
	        // �Ƴ����õ�resource�ļ�
	        shrinkResources true
		    // Zipalign�Ż�
			zipAlignEnabled false
			// ��������
            debuggable true
        }
    }
    
    // �Ƴ�lint����error
    lintOptions {
      abortOnError false
    }

   android.applicationVariants.all { variant ->
    variant.outputs.each { output ->
	def outputFile = output.outputFile
	if (outputFile != null && outputFile.name.endsWith('.apk')) {
	    // ���apk����ΪDriverTrainingTV_v1.0_2015-01-15_debug/release.apk
	    def fileName = "DriverTrainingTV_v${defaultConfig.versionName}_${releaseTime()}_${variant.name}.apk"
	    output.outputFile = new File(outputFile.parent, fileName)
	 }
      }
   }
   
    // �ֶ�ָ��jnilib��
    sourceSets {
        main {
            jniLibs.srcDir 'libs'
        }
    }
}

dependencies {
    // ����libsĿ¼�µ�����jar��
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile files('libs/android_api_3_4.jar')
    // ����extrasĿ¼�µ�ShimmerAndroidģ��
    compile project(':extras:ShimmerAndroid')
    compile 'com.android.support:support-v4:21.0.2'
}

3��gradle��������
   
   gradlew -v �汾��
   gradlew clean ���9GAG/appĿ¼�µ�build�ļ���
   gradlew build ���������������
   gradlew assembleDebug ���벢��Debug��
   gradlew assembleRelease ���벢��Release�İ�
   gradlew installRelease Releaseģʽ�������װ
   gradlew uninstallRelease ж��Releaseģʽ��

 4��gradle���������

   ������ͳ��Ϊ���������Channel_ID����������ʾ�����ǵ�Ŀ������ڱ����ʱ�����ֵ�ܹ��Զ��仯��
   <meta-data
    android:name="UMENG_CHANNEL"
    android:value="Channel_ID" />

   ��һ�� ��AndroidManifest.xml������PlaceHolder
   <meta-data
    android:name="UMENG_CHANNEL"
    android:value="${UMENG_CHANNEL_VALUE}" />

   �ڶ��� ��build.gradle����productFlavors
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
   ���������޸�

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


 5��assemble���Build Variants������task

 assemble �� Product Flavor ��ϴ����µ�������ʵ assemble �Ǻ� Build Variants һ����ʹ�õģ��� 
 Build Variants = Build Type + Product Flavor �� �ٸ����Ӵ�Ҿ������ˣ�
 
 1������һ��flavor������release�汾��ִ����������ͺ��ˣ�gradlew assembleFlavor1Release
 
 2������ָ��flavor������APK��gradlew assembleFlavor1�����������Flavor1������Release��Debug�汾.
 
 3������productFlavor�µ����������İ汾��gradlew assembleDebug������ָ��buildType������APK��
    Flavor1Debug��Flavor2Debug����Variant�汾��

