
## Transform API ##

**概述**

* Transform API： 允许第三方 Plugin 在Android打包过程中，由class转换成dex文件之前的编译过程中，加入开发者自定义的处理逻辑操作，它是一种获取 class的方式，在代码编译之后，生成dex之前起作用。
* Transform 作用：
	* 1、Transform 主要处理和转换两种资源流，一种是会被消费掉，一种只是参与了转换过程，并不会被消费掉
	* 2、资源流存储在一个资源池，Transform 从这个资源池收集资源流，然后经过一定的规则转换生成新的资源流放在资源池里，同时将未消耗的资源流也放回这个池子里去，下一个 Transform 重复之前的流程
	
	
**工作流程**

* 每个Transform其实都是一个gradle task，Android编译器中的TaskManager将每个Transform串连起来，第一个Transform接收来自javac编译的结果，以及已经拉取到在本地的第三方依赖（jar. aar），还有resource资源，注意，这里的resource并非android项目中的res资源，而是asset目录下的资源。这些编译的中间产物，在 Transform组成的链条上流动，每个Transform节点可以对class进行处理再传递给下一个Transform。我们常见的混淆，Desugar等逻辑，它们的实现如今都是封装在一个个Transform中，而我们自定义的Transform，会插入到这个Transform链条的最前面。

* 1、如何获取 class 文件
	* 1、Transform 将输入进行处理，然后写入到指定的目录下作为下一个 Transform 的输入源
	* 2、配置 Transform 的输入类型为 Class，作用域为全工程。 这样在 transform() 方法中，inputs 会传入工程内所有的 class 文件
		 * 1、inputs 包含两个部分： jar 包和目录。子 module 的 java 文件在编译过程中也会生成一个 jar 包然后编译到主工程中。
	 	 * 2、outputProvider 获取到输出目录，最后将修改的文件复制到输出目录，这一步必须做不然编译会报错
	* 3、TransformAPI 无法直接操作 class 文件，需通过 ams 或 javassist 第三方框架操作
* 2、Transform 与 Gradle Task 之间的关系？
	* Gradle 中有一个 TransformManage 的类，调用 addTransform() 管理所有的 Transform ，会将 Transform 包装成一个 AndroidTask 对象，可以理解为一个Transform就是一个Task
* 3、Gradle 是如何控制 Transform 的作用域的？
	* TransformManage类调用 createPostCompilationTasks() 方法，此方法在javaCompile 之后调用，会遍历所有的 transform，然后一一添加进TransformManager，先加载自定义的Transform 之后，再添加 Proguard，JarMergeTransform，MultiDex，Dex 等 Transform
* 4、为什么最后一定要把jar文件复制到输出目录呢？
	* 因为Gradle 是通过一个一个 Task 执行完成整个流程（就像一个串起来的链表），而Task 有一个重要的概念：inputs 和 outputs。 Task 通过 inputs拿到一些需要的参数，处理完毕之后就输出 outputs，而下一个 Task 的 inputs 则是上一个 Task 的outputs。 所以一定要复制到输出目录。


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

	* isIncremental: 当前 Transform 是否支持增量编译，并非每次编译过程都是支持增量的
		@Override
		boolean isIncremental() {
		    return false
		}
		增量编译，则要检查每个文件的Status，Status分四种
		1、NOTCHANGED: 当前文件不需处理，甚至复制操作都不用
		2、ADDED、CHANGED: 正常处理，输出给下一个任务
		3、REMOVED: 移除outputProvider获取路径对应的文件

	* transform()
		* 若源代码改变，需要重新生成class文件时，此方法被调用
		public void transform(@NonNull TransformInvocation transformInvocation) {
			
		}
		
	* isIncremental()：判断当前编译是否是增量编译
	
	* getInputs()：消费型输入，可以从中获取jar包和class文件夹路径。必须输出给下一个任务
	
	* getReferencedInputs()：引用型输入，无需输出，则不应该被transform
	
	* getOutputProvider()：OutputProvider管理输出路径，如果消费型输入为空，则OutputProvider == null
	
	* TransformInput：所谓 Transform 就是对输入的class文件转变成目标字节码文件，TransformInput就是这些输入文件的抽象。包括：
		* DirectoryInput：以源码方式参与项目编译的所有目录结构及其目录下的源码文件，可借助它来修改输出文件的目录结构、目标字节码文件
		* JarInput：以jar包方式参与项目编译的所有本地jar包或远程jar包，可以借助于它来动态添加jar包
		
	* TransformOutputProvider：它代表的是Transform的输出，例如可以通过它来获取输出路径


**Transform优化**

* 增量编译
	* 增量变异的触发机制
* 并发编译
	* 并发编译的运行机制
* 字节码框架：javassist asm
	* 修改dir中的class文件
	* 修改jar包的class文件
