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

4、在项目根目录的build.gradle中添加bintray插件

    // 配置bintray插件
    classpath 'com.jfrog.bintray.gradle:gradle-bintray-plugin:1.7.1'
    classpath 'com.github.dcendents:android-maven-gradle-plugin:1.5'

5、在local.properties中添加bintray认证

    bintray.user=xxxx
    bintray.apikey=xxxxx

6、修改Module中的build.gradle中的配

    1、添加配置项
	    ext {
		    /** 
		     * bintrayRepo如果没有设置repository name，使用默认的即maven。
		     * bintrayName修改成你上面创建的 package name
		     * 这里的值对应bintray建立的中心仓库
			 */
		    bintrayRepo = 'test'
		    bintrayName = 'all-utils'
		
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
		    publishedGroupId = 'com.aerozhonghuan' // GroupId
		    libraryName = 'all-utils'  // library包名
		    artifact = 'all-utils'     // ArtifactId
		    libraryVersion = '1.0.01'  // VersionId
		
			// 定义描述信息
		    libraryDescription = '测试jcenter'
		    siteUrl = 'https://gitee.com/liuk2008/notes.git'
		    gitUrl = 'https://gitee.com/liuk2008/notes.git'
		
		    developerId = 'LiuK'
		    developerName = 'LiuK'
		    developerEmail = ' liuk9527@gmail.com'
		
		    licenseName = 'Test 1.0'
		    licenseUrl = 'https://gitee.com/liuk2008/notes.git'
		    allLicenses = ["Apache-2.0"]
		}
    2、引用其他的gradle配置

		apply from: 'build1.gradle'
					
			apply plugin: 'com.github.dcendents.android-maven'
			// Maven Group ID for the artifact
			group = publishedGroupId
			install {
			    repositories.mavenInstaller {
			        // This generates POM.xml with proper parameters
			        pom {
			            project {
			                packaging 'aar'
			                groupId publishedGroupId
			                artifactId artifact
			                // Add your description here
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

		apply from: 'build2.gradle'

			apply plugin: 'com.jfrog.bintray'
			// This is the library version used when deploying the artifact
			version = libraryVersion
			if (project.hasProperty("android")) { // Android libraries
			    task sourcesJar(type: Jar) {
			        classifier = 'sources'
			        from android.sourceSets.main.java.srcDirs
			    }
			
			    task javadoc(type: Javadoc) {
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
			
			task javadocJar(type: Jar, dependsOn: javadoc) {
			    classifier = 'javadoc'
			    from javadoc.destinationDir
			}
			
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
			        repo = bintrayRepo
			        name = bintrayName
			        desc = libraryDescription
			        websiteUrl = siteUrl
			        vcsUrl = gitUrl
			        licenses = allLicenses
			        publish = true
			        publicDownloadNumbers = true
			        version {
			            desc = libraryDescription
			            gpg {
			                sign = true //Determines whether to GPG sign the files. The default is false
			                passphrase = properties.getProperty("bintray.gpg.password")
			                //Optional. The passphrase for GPG signing'
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

网站：

https://www.virag.si/2015/01/publishing-gradle-android-library-to-jcenter/

http://idisfkj.github.io/2016/06/13/%E5%A6%82%E4%BD%95%E4%BD%BFAndroid-Studio%E9%A1%B9%E7%9B%AE%E5%8F%91%E5%B8%83%E5%88%B0Jcenter%E4%B8%AD/

http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0623/3097.html

关联：

github 上传代码 -> 发布到maven中-> 建立远程引用

