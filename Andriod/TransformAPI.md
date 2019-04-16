
## Transform API ##

**概述**

	* Transform API： 允许第三方 Plugin 在打包 dex 文件之前的编译过程中操作 .class 文件。
		* Transform API 是新引进的操作 class 的方式
		* Transform API 在编译之后，生成 dex 之前起作用
		* 目前 jarMerge、proguard、multi-dex、Instant-Run 都已经换成 Transform 实现。

**重点方法**

	* name：
		* Task名称：TransformClassesWith + getName() + For + buildTypes
		* 生成目录：build/intermediates/transforms/xxxx/
	    @Override
	    String getName() {
	        return xxxx
	    }

	* inputTypes: transform 要处理的数据类型
		* 需要处理的数据类型：
		* CONTENT_CLASS：表示处理java的class文件，可能是 jar 包也可能是目录
		* CONTENT_RESOURCES：表示处理java的资源
		@Override
		Set<QualifiedContent.ContentType> getInputTypes() {
		    return TransformManager.CONTENT_CLASS
		}

	* scopes：transform 的作用域
		* Transform的作用范围
		*     PROJECT                       只有项目内容
		*     PROJECT_LOCAL_DEPS            只有项目的本地依赖(本地jar,aar)
		*     SUB_PROJECTS                  只有子项目
		*     SUB_PROJECTS_LOCAL_DEPS       只有子项目的本地依赖(本地jar,aar)
		*     PROVIDED_ONLY                 只提供本地或远程依赖项
		*     EXTERNAL_LIBRARIES            只有外部库
		*     TESTED_CODE                   测试代码
		Set<? super QualifiedContent.Scope> getScopes() {
		    return TransformManager.SCOPE_FULL_PROJECT
		}

	* isIncremental: 当前 Transform 是否支持增量编译
		@Override
		boolean isIncremental() {
		    return false
		}

	* transform()
		* 若源代码改变，需要重新生成class文件时，此方法被调用
		public void transform(@NonNull TransformInvocation transformInvocation) {}
		* TransformInput：所谓Transform就是对输入的class文件转变成目标字节码文件，TransformInput就是这些输入文件的抽象。目前它包括两部分：DirectoryInput集合与JarInput集合。
		* DirectoryInput：它代表着以源码方式参与项目编译的所有目录结构及其目录下的源码文件，可以借助于它来修改输出文件的目录结构、已经目标字节码文件。
		* JarInput：它代表着以jar包方式参与项目编译的所有本地jar包或远程jar包，可以借助于它来动态添加jar包。
		* TransformOutputProvider：它代表的是Transform的输出，例如可以通过它来获取输出路径。

		
**工作流程**

	* 1、如何获取 class 文件
		* 1、Transform 将输入进行处理，然后写入到指定的目录下作为下一个 Transform 的输入源
		* 2、配置 Transform 的输入类型为 Class，作用域为全工程。 这样在 transform() 方法中，inputs 会传入工程内所有的 class 文件
			 * 1、inputs 包含两个部分： jar 包和目录。子 module 的 java 文件在编译过程中也会生成一个 jar 包然后编译到主工程中。
		 	 * 2、outputProvider 获取到输出目录，最后将修改的文件复制到输出目录，这一步必须做不然编译会报错
	* 2、Transform 与 Gradle Task 之间的关系？
		* Gradle 中有一个 TransformManage 的类，调用 addTransform() 管理所有的 Transform ，会将 Transform 包装成一个 AndroidTask 对象，
		* 可以理解为一个Transform就是一个Task
	* 3、Gradle 是如何控制 Transform 的作用域的？
		* TransformManage类调用 createPostCompilationTasks() 方法，此方法在 javaCompile 之后调用，会遍历所有的 transform，然后一一添加
		* 进 TransformManager，先加载自定义的 Transform 之后，再添加 Proguard, JarMergeTransform, MultiDex, Dex 等 Transform

**注意事项**
	
	* 为什么最后一定要把jar文件复制到输出目录呢？
		* 因为Gradle 是通过一个一个 Task 执行完成整个流程（就像一个串起来的链表），而Task 有一个重要的概念：inputs 和 outputs。 Task 通过 inputs
		* 拿到一些需要的参数，处理完毕之后就输出 outputs，而下一个 Task 的 inputs 则是上一个 Task 的outputs。 所以一定要复制到输出目录。
