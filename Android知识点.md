
**相关总结**

	* 为什么用服务而不是线程？
		* 当Android应用程序把所有的界面关闭时进程还没有被销毁，不过处于的是空进程状态，Thread运行在空进程中很容易的被销毁了。
		* 服务不容易被销毁, 如果非法状态下被销毁了, 系统会在内存够用时, 重新启动。

	* 1、A界面数据发生改变时通知B界面：1、回调接口，2、发送广播
	* 2、Toast的创建需要依赖Looper，底层调用Looper.myLooper()获取Looper对象，若存在，在线程也可以弹出Toast，若不存在，抛出异常
	* 3、Intent传递对象时，是将对象拷贝了一份进行传递
	* 4、增量更新、热修复、插件化
		 * 增量更新：通过生成差分包的供下载，再合并达到更新的方式
		 * 热修复（热更新）：强调的是修改线上版本的bug，用技术去实现不更新整个apk的条件下，修改掉bug。
	* 5、热启动、冷启动
		 * 冷启动：当应用启动时，后台没有该应用的进程，这时系统会重新创建一个新的进程分配给该应用，这个启动方式就叫做冷启动（后台不存在该应用进程）。
		 * 热启动：当应用已经被打开，但是被按下返回键或Home键时回到桌面或者切换到其他程序时，再重新打开该app时，这个方式叫做热启动（后台已经存在该应用进程）。
	* 6、class文件、dex文件
		 * class文件：class文件是一种能够被JVM识别，加载并且执行的文件格式。
	 	 * dex文件：能够被DVM或者Art虚拟机执行并且加载的文件格式。先生成class文件，再根据class文件生成dex文件
	 	 
	* 任务栈相关内容
		1、Android程序打开时会创建一个任务栈，用于存储当前程序的activity，所有的activity属于一个任务栈。
		2、只有在任务栈栈顶的activity才可以跟用户进行交互。
		3、任务栈可以移动到后台并且保留了每一个activity的状态，并且有序的给用户列出它们的任务，而且还不丢失它们状态信息。
		4、退出应用程序时：当把任务栈中所有的activity清除出栈时，任务栈会被销毁，程序退出。
		注意：
		1、每开启一次页面都会在任务栈中添加一个Activity，会造成数据冗余，导致内存溢出的问题
		2、只有任务栈中的Activity全部清除出栈时，任务栈才被销毁，程序才会退出
	
	* Activity的四种启动模式：
		1、standard：
	 	* 默认启动模式，每次创建新的Activity放入任务栈中。
    	2、singleTop：
		* 若不存在Activity实例，则创建新的Activity实例并放入栈顶。	
		* 若存在Activity实例且在栈顶，则复用该实例，若不在栈顶，也会创建新的Activity实例。
	    3、singleTask：
		* 若不存在Activity实例，则创建新的Activity实例并放入栈顶。
        * 若存在Activity实例但是不在栈顶，则复用该实例并让该实例回到栈顶，会清除他上面其他Activity实例，而不会清除栈低存在的其他Activity实例
    	4、singleInstance
        * 在一个新栈中创建该Activity实例，并让多个应用共享该栈中的该Activity实例。
   
  		注意：
	    1、使用Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK标记：
    		* 1、必须同时设置这两个标记，否则没有效果
    		* 2、设置此标记时会清除栈内所有的Activity，并且会新开启一个任务栈重新生成一个Activity实例对象
    	2、启动模式为singleTop或者为singleTask时，重用Activity时会调用onNewIntent方法。
		   需要在onNewIntent()中使用setIntent(intent)赋值给Activity的Intent，否则，后续的getIntent()都是得到老的Intent。


	* 内存抖动和内存泄漏
		* 内存抖动：在短时间内有大量的对象被创建或者被回收的现象
		* 内存泄漏：某一段内存在程序里已经不需要了，但是GC回收内存时检测那段内存还是被需要的，不能正常被回收，这种在程序中在没有使用的但是又
		* 不能被回收的内存就是被泄漏的内存。一般检查这段内存是否存在引用和被引用关系，不存在这关系时，就认为可回收，若还存在引用或被引用关系，就认为不可回收。

		* 注意：执行GC操作的时候，任何线程的任何操作都会需要暂停，等待GC操作完成之后，其他操作才能够继续运行（所以垃圾回收运行次数越少，对性能影响就越少）。

	* 分辨率、dp、px、sp相关内容
		* 分辨率：图像在水平和垂直方向上所容纳最大像素个数。例如 960*640 表示表示水平像素数为960个，垂直像素数640个，像素大小为960*640，约60万像素。
		* 注意：在像素大小确定之后，分辨率越高则图像尺寸越小显示效果越好，反之则尺寸越大效果越差。
		* dp：虚拟像素，在不同的像素密度的设备上会自动适配
		* px：像素，1px代表屏幕上一个物理的像素点
		* 屏幕像素密度（density）：每英寸像素数量，dip到px的转换公式: px = dip * density。
		* Android规定，在屏幕像素密度为160dpi的情况下，1dp=1px。而在像素密度为320dpi的情况下，1dp=2px，以此类推。计算公式：1dp=（像素密度/160dpi）*1px。
		* sp同dip/dp相似，会根据用户的字体大小偏好来缩放，专门用于设置字体的大小。
	
	* SharedPreference.Editor的apply和commit方法异同
		* 1、apply没有返回值而commit返回boolean表明修改是否提交成功 
		* 2、apply是将修改数据原子提交到内存, 而后异步真正提交到硬件磁盘, 而commit是同步的提交到硬件磁盘
		* 3、apply方法不会提示任何失败的提示
		* 由于在一个进程中，sharedPreference是单实例，一般不会出现并发冲突，如果对提交的结果不关心的话，建议使用apply
	
	* StickyBroadcast广播
		* 1、普通广播：这种广播可以依次传递给各个处理器去处理
		* 2、有序广播：这种广播在处理器端的处理顺序是按照处理器的不同优先级来区分的，高优先级的处理器会优先截获这个消息，并且可以将这个消息删除
		* 3、粘性广播：粘性消息在发送后就一直存在于系统的消息容器里面，等待对应的处理器去处理，如果暂时没有处理器处理这个消息则一直在消息容器里面处于等待状态，
		* 粘性广播的Receiver如果被销毁，那么下次重建时会自动接收到消息数据。
		* 注意：
		* 1、普通广播和粘性消息不能被截获，而有序广播是可以被截获的。
		* 2、粘性广播调用registerReceiver能马上接受广播，而普通广播不行。
		* 3、粘性广播，是指广播接收器一注册马上就能接收到广播的一种机制，当然首先系统要存在广播。而普通广播就是要先注册广播接收器，然后广播被发送到系统，
		* 广播接收器才能接收到广播。



	* 为什么Android更新UI只能在主线程（UI只能在创建它的线程中更新，不一定是主线程）
		* UI访问没有加锁，在多个线程访问UI是不安全的，所以Android中规定只能在UI线程中访问UI。
		* 有些UI更新可以在 onCreate 中创建子线程操作UI，不会程序崩溃，主要是因为： 
			* 1、错误是从 ViewRootImpl.requestLayout → ViewRootImpl.checkThread 中抛出
			* 2、onCreate 的时候 ViewRootImpl 还未创建
			* 3、如果子线程的操作能在 onCreate 和 创建 ViewRootImpl 过程中完成，就不会报错 

	* 解决Gradle依赖冲突
		* gradlew -q app:dependencies 查询APP所有依赖包
		* 其存在冲突的module中的build.gradle文件中加入下面代码，原理就是通过遍历所有依赖，并修改指定库的版本号
			其中 requested.group == 'com.android.support' 	com.android.support表示要修改的依赖库
			    details.useVersion '28.0.0'	                28.0.0表示要修改的版本号

			configurations.all {
			    resolutionStrategy.eachDependency { DependencyResolveDetails details ->
			        def requested = details.requested
			        if (requested.group == 'com.android.support') {
			            if (!requested.name.startsWith("multidex")) {
			                details.useVersion '28.0.0'
			            }
			        }
			    }
			}
	
	* V1签名和V2签名的区别
		* V1：在V1中只对未压缩的文件内容进行了验证，所以在APK签名之后可以进行很多修改——文件可以移动，甚至可以重新压缩。即可以对签名后的文件在进行处理 
		* V2：V2签名验证了归档中的所有字节，而不是单独的ZIP条目，如果您在构建过程中有任何定制任务，包括篡改或处理APK文件，请确保禁用它们，否则您可能会
		* 使v2签名失效，从而使您的APKs与Android 7.0和以上版本不兼容。

		* V1签名是对jar进行签名，V2签名是对整个apk签名
		* V1和V2的签名使用：
			1、只勾选v1签名并不会影响什么，但是在7.0上不会使用更安全的验证方式
			2、只勾选V2签名7.0以下会直接安装完显示未安装，7.0以上则使用了V2的方式验证
			3、同时勾选V1和V2则所有机型都没问题


	* 消息推送三种模式：
		* 1、APP正在运行时，进程存在时收到消息–任何页面可以弹出通知栏，跳转页面
		* 2、APP完全退出后，进程存在时收到消息–弹出通知栏
		     1、通过通知栏启动APP，清除通知，消息状态为已读  
		     2、通过桌面启动APP，清除通知，消息状态为已读  
		* 3、APP进程不存时，重新登录进入时收到消息–进入主页时弹出通知栏，跳转页面
	
	* 设置自定义通知布局，并设置监听 PendingIntent，注意事项：
		* 1、在Notification中的PendingIntent会默认开启新的任务栈。
		* 2、设置PendingIntent后，自定义消息通知无法消失，无论是通过getBroadcast、getActivity发送广播
		* 3、非Activity的Context都需要启动Activity都需要新建任务栈去放置，不仅仅是广播里面的context