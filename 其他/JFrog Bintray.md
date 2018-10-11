# 配置JFrog Bintray 

	jcenter是一个由bintray.com维护的Maven仓库。你可以在这里看到整个仓库的内容。
	
	我们在项目的build.gradle 文件中如下定义仓库，就能使用jcenter了：
	
	    allprojects {
	        repositories {
	            jcenter()
	        }
	    }
	
	Maven Central
	Maven Central 则是由sonatype.org维护的Maven仓库。你可以在这里看到整个仓库。
	注：不管是jcenter还是Maven Central ，两者都是Maven仓库

1、https://bintray.com/signup/oss 中注册个人账号，注意使用gmail邮箱

2、根据自己的需求创建maven的Repository、Package，可自定义名称

3、把项目分离成Module，设置成com.android.library

4、在项目根目录的build.gradle中添加bintray插件，版本信息可以在GitHub上查询

    // 配置bintray插件
    classpath 'com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4'
    classpath 'com.github.dcendents:android-maven-gradle-plugin:2.1'

5、在local.properties中添加bintray认证

    bintray.user=xxxx
    bintray.apikey=xxxxx

6、修改Module中的build.gradle中的配配置

6.1、在默认build.gradle添加配置项
	
	ext {
		/** 
	     * bintrayRepo如果没有设置repository name，使用默认的即maven。
	     * bintrayName修改成你上面创建的 package name，这里的值对应bintray建立的中心仓库
		 */
	    bintrayRepo = 'common'  
	    bintrayName = 'mytools' 
		
	    /** 
		 * com.aerozhonghuan:all-utils:1.0.01
		 * 它的GroupId是com.aerozhonghuan，ArtifactId是all-utils，VersionId是1.0.01。
		 * 当我们添加了依赖之后gradle会先去Maven中查找是否有该library，
		 * 如果有就会使用上面定义的形式下载
		 * http://jcenter.bintray.com/GroupId/ArtifactId/VersionId
		 * http://jcenter.bintray.com/com/aerozhonghuan/all-utils/1.0.01
		 * 
	     * 使用 Maven repositories 链接
	     * https://dl.bintray.com/bintray登录名/repository名称/
	     * https://dl.bintray.com/chanba2010/test/
		 */
	    publishedGroupId = 'com.common' // 发布的组织名称
	    artifact = 'mytools'            // 依赖的lib名称
	    libraryVersion = '1.0.03'   // 版本号
	    libraryName = 'mytools'    // lib name
	
	    // 开发者信息
	    developerId = 'liuk'
	    developerName = 'liuk'
	    developerEmail = '471636288@qq.com'
	
	    // 项目描述
	    libraryDescription = '工具类及常用代码'
	    siteUrl = 'https://github.com/liuk2008'  // 项目主页
	    gitUrl = 'https://github.com/liuk2008/Jenkins.git'   // 项目的git地址
	    licenseName = 'MyTools 1.0'
	    licenseUrl = 'https://github.com/liuk2008'
	    allLicenses = ["Apache-2.0"]
	}
	
	apply from: 'maven.gradle'
	apply from: 'bintray.gradle'

6.2、建立maven.gradle，进行配置

	apply plugin: 'com.github.dcendents.android-maven'
	// 配置maven库，生成POM.xml文件
	group = publishedGroupId  // Maven Group ID for the artifact
	install {
	    repositories.mavenInstaller {
	        // This generates POM.xml with proper parameters
	        pom {
	            project {
	                packaging 'aar'
	                // Add your description here
	                groupId publishedGroupId
	                artifactId artifact
	                name libraryName
	                description libraryDescription
	                url siteUrl
	                // Set your license
	                licenses {
	                    license {
	                        name licenseName
	                        url licenseUrl
	                    }
	                }
	                // 开发者信息
	                developers {
	                    developer {
	                        id developerId
	                        name developerName
	                        email developerEmail
	                    }
	                }
	                scm {
	                    connection gitUrl
	                    developerConnection gitUrl
	                    url siteUrl
	                }
	            }
	        }
	    }
	}
	
6.3、建立bintray.gradle，进行配置

	apply plugin: 'com.jfrog.bintray'
	
	// This is the library version used when deploying the artifact
	version = libraryVersion
	if (project.hasProperty("android")) { // Android libraries
	    task sourcesJar(type: Jar) {   // 生成源文件
	        classifier = 'sources'
	        from android.sourceSets.main.java.srcDirs
	    }
	
	    task javadoc(type: Javadoc) {  // 生成Javadoc文档
	        source = android.sourceSets.main.java.srcDirs
	        classpath += project.files(android.getBootClasspath().join(File.pathSeparator))
	        options.encoding = "UTF-8"
	        options.charSet = "UTF-8"
	    }
	} else { // Java libraries
	    task sourcesJar(type: Jar, dependsOn: classes) {
	        classifier = 'sources'
	        from sourceSets.main.allSource
	    }
	}
	// 文档打包成jar
	task javadocJar(type: Jar, dependsOn: javadoc) {
	    classifier = 'javadoc'
	    from javadoc.destinationDir
	}
	
	// 上传到JCenter所需要的源码文件
	artifacts {
	    archives javadocJar
	    archives sourcesJar
	}
	
	// Bintray
	Properties properties = new Properties()
	properties.load(project.rootProject.file('local.properties').newDataInputStream())
	
	bintray {
	    user = properties.getProperty("bintray.user")
	    key = properties.getProperty("bintray.apikey")
	
	    configurations = ['archives']
	    pkg {
	        // 注意：这里的repo、name必须要和你创建Maven仓库的时候的名字一样
	        repo = bintrayRepo
	        name = bintrayName
	        desc = libraryDescription
	        websiteUrl = siteUrl
	        vcsUrl = gitUrl
	        licenses = allLicenses
	        publish = true
	        // 配置签名
	        version {
	            gpg {
	                //Determines whether to GPG sign the files. The default is false
	                sign = false
	                //Optional. The passphrase for GPG signing'
	                passphrase = properties.getProperty("bintray.gpg.password")
	            }
	        }
	    }
	}
			  
7、Android Studio终端使用./gradlew xxx上传

    1、编译library文件  gradlew install
    2、上传            gradlew bintrayUpload

8、最后在JFrog Bintray中同步到Jcenter中

    1、在Module中的build.gradle中添加
      dependencies {
   		 compile 'com.aerozhonghuan:all-utils:1.0.01'
	  }
    2、在项目根目录的build.gradle中添加
     repositories {
	    maven {
	        url 'https://dl.bintray.com/chanba2010/test/'
	    }
	}


