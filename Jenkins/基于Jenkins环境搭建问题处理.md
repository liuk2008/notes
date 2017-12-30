* 使用Jenkins打包时常见问题处理

  1、gradle路径配置错误，提示以下错误
	[Gradle] - [ERROR] Can't retrieve the Gradle executable.
	Build step 'Invoke Gradle script' marked build as failure
    
	解决办法：在 全局工具配置 里面修改gradle路径
    例：C:\Users\Administrator\.gradle\wrapper\dists\gradle-3.3-all\55gk2rcmfc6p2dg9u9ohc3hw9\gradle-3.3      
   
 
  2、打包时提示“Could not load Logmanager "org.apache.juli.ClassLoaderLogManager"”
    
    解决办法：修改Tomcat的bin目录下的catalina.bat文件。
    将
      set "JAVA_OPTS=%JAVA_OPTS% %LOGGING_CONFIG%"
	  set "JAVA_OPTS=%JAVA_OPTS% %LOGGING_MANAGER%"
    修改为
      rem set "JAVA_OPTS=%JAVA_OPTS% %LOGGING_CONFIG%"  
      rem set "JAVA_OPTS=%JAVA_OPTS% %LOGGING_MANAGER%"

   
