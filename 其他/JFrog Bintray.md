# 配置JFrog Bintray 

jcenter是一个由bintray.com维护的Maven仓库。你可以在这里看到整个仓库的内容。我们在项目的根目录 build.gradle 文件中如下定义仓库，就能使用jcenter了：

    allprojects {
        repositories {
            jcenter()
        }
    }

1、https://bintray.com/signup/oss 中注册个人账号，注意使用gmail邮箱

2、根据自己的需求创建maven的Repository、Package，可自定义名称

3、把项目构建成lib包，设置成com.android.library或java-library

4、在项目根目录的build.gradle中添加bintray插件，版本信息可以在GitHub上查询

    // 配置bintray插件
    classpath 'com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4'
    classpath 'com.github.dcendents:android-maven-gradle-plugin:2.1'

5、在Module中的build.gradle添加配置项

	// 1、编译library文件  gradlew install
	// 2、上传            gradlew bintrayUpload
	ext {
		
	    bintrayRepo = 'library'   // 修改成bintrayRepo上创建的 maven name，未设置时默认使用maven。
	    bintrayName = 'common'    // 修改成bintrayRepo上创建的 package name
 
		// http://jcenter.bintray.com/bintray登录名/repository名称/GroupId/ArtifactId/VersionId

	    publishedGroupId = 'com.android.library' // 发布的组织名称
	    artifact = 'common'                      // 依赖的lib名称
	    libraryVersion = '1.0'                   // 版本号
	
	    username = 'chanba2010'                               // bintray账号
	    userkey = 'c45f983479dd72be69d3af6a09ad9045a6312909'  // 账号key
	
	    // 开发者信息
	    developer = [
	            developerId   : 'liuk',
	            developerName : 'liuk',
	            developerEmail: '471636288@qq.com',
	    ]
	
	    // 项目描述
	    library = [
	            libraryName       : 'lib-common',
	            libraryDescription: '公共模块',
	            siteUrl           : 'https://github.com/liuk2008/AndriodLibrary.git'
	    ]
	
	}
	
	group = publishedGroupId
	version = libraryVersion
	
	// 配置 bintray
	apply plugin: 'com.jfrog.bintray'
	// This is the library version used when deploying the artifact
	if (project.hasProperty("android")) { // Android libraries
	    task sourcesJar(type: Jar) {   // 生成源文件
	        classifier = 'sources'
	        from android.sourceSets.main.java.srcDirs
	    }
	
	    task javadoc(type: Javadoc) {  // 生成Javadoc文档
	        source = android.sourceSets.main.java.srcDirs
	        classpath += project.files(android.getBootClasspath().join(File.pathSeparator))
	//        options.encoding = "UTF-8"
	//        options.charSet = "UTF-8"
	    }
	} else { // Java libraries
	    task sourcesJar(type: Jar, dependsOn: classes) {
	        classifier = 'sources'
	        from sourceSets.main.allSource
	    }
	}
	
	// 解决上传Javadoc generation failed. Generated Javadoc options
	tasks.withType(Javadoc) {
	    options.addStringOption('encoding', 'UTF-8')
	    options.addStringOption('charSet', 'UTF-8')
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
	
	bintray {
	    user = username
	    key = userkey
	    configurations = ['archives']
	    pkg {
	        // 注意：这里的repo、name必须要和你创建Maven仓库的时候的名字一样
	        repo = bintrayRepo
	        name = bintrayName
	        desc = library.libraryDescription
	        websiteUrl = library.siteUrl
	        publish = true
	    }
	}
	
	// 配置maven库
	apply plugin: 'com.github.dcendents.android-maven'
	install {
	    repositories.mavenInstaller {
	        // This generates POM.xml with proper parameters
	        pom {
	            project {
	                packaging 'aar'
	                // Add your description here
	                groupId publishedGroupId
	                artifactId artifact
	                name library.libraryName
	                description library.libraryDescription
	                url library.siteUrl
	                // 开发者信息
	                developers {
	                    developer {
	                        id developer.developerId
	                        name developer.developerName
	                        email developer.developerEmail
	                    }
	                }
	            }
	        }
	    }
	}
			  
6、Android Studio终端使用./gradlew xxx上传

    1、编译library文件  gradlew install
    2、上传            gradlew bintrayUpload

7、最后在JFrog Bintray中同步到Jcenter中

    1、在项目根目录的build.gradle中添加
       repositories {
	 	   maven {
			   // https://dl.bintray.com/bintray登录名/repository名称/
	 	       url 'https://dl.bintray.com/chanba2010/test/'
	 	   }
	   }

    2、在Module中的build.gradle中添加
       dependencies {
		   // GroupId是com.aerozhonghuan，ArtifactId是all-utils，VersionId是1.0.01。
		   compile 'com.aerozhonghuan:all-utils:1.0.01'
	   }

