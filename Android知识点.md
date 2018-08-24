

	* 为什么用服务而不是线程？
		* 当Android应用程序把所有的界面关闭时进程还没有被销毁，不过处于的是空进程状态，Thread运行在空进程中很容易的被销毁了。
		* 服务不容易被销毁, 如果非法状态下被销毁了, 系统会在内存够用时, 重新启动。

	* adb常用命令
		* 1、查询Activity实例对象：adb shell dumpsys activtiy activities
		* 2、查询keystore信息：keytool -list -v -keystore debug.keystore

	* A界面数据发生改变时通知B界面
		* 1、回调接口
		* 2、发送广播

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

	* 进程优先级
		1、前台进程
		2、可见进程
		3、服务进程
		4、后台进程：
		5、空进程：优先级低，系统会经常终止这种进程

	* Toast的创建需要依赖Handler，存在handler的话，子线程也可以弹出toast
	
	* Intent传递对象时，是将对象拷贝了一份进行传递

	* dp、px、sp相关内容
	
2.6.1.	dip	
缩写：dp
一个基于density(密度)的抽象单位，这个和设备硬件有关，通常在开发中设置一些view的宽高推荐用这个，一般情况下，在不同分辨率，都不会有缩放的感觉。在运行时, Android根据使用中的屏幕的实际密度, 透明地处理任何所需dip单位的缩放。不依赖设备像素，依据设备自动适应大小，推荐使用。


2.6.2.	sp
同dip/dp相似，会根据用户的字体大小偏好来缩放，专门用于设置字体的大小。


2.6.3.	px
像素，是屏幕的物理像素点，与密度相关，密度大了，单位面积上的px会比较多。在不同分辨率下会有不同的效果，通常不推荐使用这个


2.6.4.	dp和px的区别
首先明确一点，HVGA屏density=160；QVGA屏density=120；WVGA屏density=240；WQVGA屏density=120。

density值表示每英寸有多少个显示点，与分辨率是两个概念。dip到px的转换公式: px = dip * (density / 160)。