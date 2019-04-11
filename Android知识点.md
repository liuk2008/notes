
**相关总结**

	1、A界面数据发生改变时通知B界面：1、回调接口，2、发送广播

	2、Toast的创建需要依赖Looper，底层调用Looper.myLooper()获取Looper对象，若存在，在线程也可以弹出Toast，若不存在，抛出异常

	3、Intent传递对象时，是将对象拷贝了一份进行传递

	4、增量更新、热修复、插件化
	   * 增量更新：通过生成差分包的供下载，再合并达到更新的方式
	   * 热修复（热更新）：强调的是修改线上版本的bug，用技术去实现不更新整个apk的条件下，修改掉bug。
	   * 
	5、热启动、冷启动
	   * 冷启动：当应用启动时，后台没有该应用的进程，这时系统会重新创建一个新的进程分配给该应用，这个启动方式就叫做冷启动（后台不存在该应用进程）。
	   * 热启动：当应用已经被打开，但是被按下返回键或Home键时回到桌面或者切换到其他程序时，再重新打开该app时，这个方式叫做热启动（后台已经存在该应用进程）。
	   
	6、class文件、dex文件
	   * class文件：class文件是一种能够被JVM识别，加载并且执行的文件格式。
	   * dex文件：能够被DVM或者Art虚拟机执行并且加载的文件格式。先生成class文件，再根据class文件生成dex文件
	   
	7、为什么用服务而不是线程？
	   * 当Android应用程序把所有的界面关闭时进程还没有被销毁，不过处于的是空进程状态，Thread运行在空进程中很容易的被销毁了。
	   * 服务不容易被销毁, 如果非法状态下被销毁了, 系统会在内存够用时, 重新启动。
	   
	8、内存抖动和内存泄漏
	   * 内存抖动：在短时间内有大量的对象被创建或者被回收的现象
	   * 内存泄漏：某一段内存在程序里已经不需要了，但是GC回收内存时检测那段内存还是被需要的，不能正常被回收，这种在程序中在没有使用的但是又不能被回收的内存
		 就是被泄漏的内存。一般检查这段内存是否存在引用和被引用关系，不存在这关系时，就认为可回收，若还存在引用或被引用关系，就认为不可回收。
	   * 注意：执行GC操作的时候，任何线程的任何操作都会需要暂停，等待GC操作完成之后，其他操作才能够继续运行（所以垃圾回收运行次数越少，对性能影响就越少）。
	   
	9、SharedPreference.Editor的apply和commit方法异同
	   * 1、apply没有返回值而commit返回boolean表明修改是否提交成功 
	   * 2、apply是将修改数据原子提交到内存, 而后异步真正提交到硬件磁盘, 而commit是同步的提交到硬件磁盘
	   * 3、apply方法不会提示任何失败的提示
	   * 由于在一个进程中，sharedPreference是单实例，一般不会出现并发冲突，如果对提交的结果不关心的话，建议使用apply
	
	10、消息推送三种模式：
	    * 1、APP正在运行时，进程存在时收到消息–任何页面可以弹出通知栏，跳转页面
	    * 2、APP完全退出后，进程存在时收到消息–弹出通知栏
		     1、通过通知栏启动APP，清除通知，消息状态为已读  
		     2、通过桌面启动APP，清除通知，消息状态为已读  
	    * 3、APP进程不存时，重新登录进入时收到消息–进入主页时弹出通知栏，跳转页面
	
	11、设置自定义通知布局，并设置监听 PendingIntent，注意事项：
		* 1、在Notification中的PendingIntent会默认开启新的任务栈。
		* 2、设置PendingIntent后，自定义消息通知无法消失，无论是通过getBroadcast、getActivity发送广播
		* 3、非Activity的Context都需要启动Activity都需要新建任务栈去放置，不仅仅是广播里面的context 	 

**任务栈相关内容**
		
	1、Android程序打开时会创建一个任务栈，用于存储当前程序的Activity，所有的Activity属于一个任务栈。
	2、只有在任务栈栈顶的Activity才可以跟用户进行交互。
	3、任务栈可以移动到后台并且保留了每一个Activity的状态，并且有序的给用户列出它们的任务，而且还不丢失它们状态信息。
	4、退出应用程序时：当把任务栈中所有的Activity清除出栈时，任务栈会被销毁，程序退出。
	
**Activity的四种启动模式**

	1、standard：默认启动模式，每次创建新的Activity放入任务栈中。
	2、singleTop：若存在Activity实例且在栈顶，则复用该Activity实例，若不在栈顶或不存在Activity实例，也会创建新的Activity实例。
    3、singleTask：
	   1、若不存在Activity实例，则创建新的Activity实例并放入栈顶。
	   2、若存在Activity实例但是不在栈顶，则复用该实例并让该实例回到栈顶，会清除他上面其他Activity实例，而不会清除栈低存在的其他Activity实例
	4、singleInstance：在一个新栈中创建该Activity实例，并让多个应用共享该栈中的该Activity实例。

	注意：
		1、使用Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK标记：
		   1、必须同时设置这两个标记，否则没有效果
		   2、设置此标记时会清除栈内所有的Activity，并且会新开启一个任务栈重新生成一个Activity实例对象
		2、启动模式为singleTop或者为singleTask时，重用Activity时会调用onNewIntent方法。我们需要在onNewIntent()中使用setIntent(intent)
		   赋值给Activity的Intent，否则后续的getIntent()都是得到老的Intent。

**分辨率、dp、px相关内容**

	1、分辨率：图像在水平和垂直方向上所容纳最大像素个数。例如 960*640 表示表示水平像素数为960个，垂直像素数640个，像素大小为960*640，约60万像素。
	   注意：在像素大小确定之后，分辨率越高则图像尺寸越小显示效果越好，反之则尺寸越大效果越差。
	2、dp、dip、sp：虚拟像素，在不同的像素密度的设备上会自动适配，sp同dip/dp相似，会根据用户的字体大小偏好来缩放，专门用于设置字体的大小。
	3、px：像素，1px代表屏幕上一个物理的像素点
	4、屏幕像素密度（density）：每英寸像素数量，dip到px的转换公式: px = dip * density。

	注意：Android规定，在屏幕像素密度为160dpi的情况下，1dp=1px。而在像素密度为320dpi的情况下，1dp=2px，以此类推。计算公式：1dp=（像素密度/160dpi）*1px

**StickyBroadcast广播**
	
	1、无序广播：这种广播可以传递给各个处理器去处理
	2、有序广播：这种广播在处理器端的处理顺序是按照处理器的不同优先级来区分的，高优先级的处理器会优先截获这个消息，并且可以将这个消息删除
	3、粘性广播：粘性消息在发送后就一直存在于系统的消息容器里面，等待对应的处理器去处理，如果暂时没有处理器处理这个消息则一直在消息容器里面处于等待状态

	注意：
	1、无序广播和粘性消息不能被截获，而有序广播是可以被截获的
	2、粘性广播Receiver如果被销毁，那么下次重建时会自动接收到消息数据
	3、粘性广播是指广播接收器一注册马上就能收到广播的一种机制，当然首先系统要存在广播。普通广播要先注册广播接收器，然后广播被发送到系统，广播接收器才能收到广播
	4、Android 8.0以上大部分静态注册的广播无法接受到，解决办法：
       if(Build.VERSION.SDK_INT >= 26){
            ComponentName componentName=new ComponentName(packagename,"xx.xx.xx.xxReceiver");//参数1-包名 参数2-广播接收者所在的路径名
            intent.setComponent(componentName);
       }
	   // 解决在android8.0系统以上2个module之间发送广播接收不到的问题
	   if(Build.VERSION.SDK_INT >= 26){
			intent.addFlags(0x01000000);
	   }
	
**解决Gradle依赖冲突**

	* gradlew -q app:dependencies 查询APP所有依赖包
	* 其存在冲突的module中的build.gradle文件中加入下面代码，原理就是通过遍历所有依赖，并修改指定库的版本号            
		configurations.all {
		    resolutionStrategy.eachDependency { DependencyResolveDetails details ->
		        def requested = details.requested
		        if (requested.group == 'com.android.support') { // com.android.support表示要修改的依赖库
		            if (!requested.name.startsWith("multidex")) {
		                details.useVersion '28.0.0' //  28.0.0表示要修改的版本号
		            }
		        }
		    }
		}	
**Android签名文件**

	1、公钥证书（也称为数字证书或身份证书）包含公钥/私钥对的公钥，以及标识密钥所有者的一些其他元数据（例如名称和位置）。证书的所有者持有对应的私钥。
	2、密钥库是一种包含一个或多个私钥的二进制文件。
	3、在您签署 APK 时，签署工具会将公钥证书附加到 APK（在签署应用软件包时也是如此）。公钥证书充当“指纹”，用于将 APK 或应用软件包唯一关联到您及您的对应私钥。 
	   这有助于 Android 确保应用将来的所有更新都是原版更新且来自原始作者。用于创建此证书的密钥称为应用签名密钥。

	* 密钥库
		Key store path：选择创建密钥库的 位置。
		Password：为您的密钥库创建并确认安全的密码。
	* 密钥
		Alias：为您的密钥输入一个标识名。
		Password：为您的密钥创建并确认安全的密码。此密码应当与您为密钥库选择的密码不同。
		Validity (years)：以年为单位设置密钥的有效时长。密钥的有效期应至少为 25 年，以便您可以在应用的整个生命期内使用相同的密钥签署应用更新。
		Certificate：为证书输入一些关于您自己的信息。此信息不会显示在应用中，但会作为 APK 的一部分包含在您的证书中。

**v1签名方案和v2签名方案的区别**

	1、v1：在v1中只对未压缩的文件内容进行了验证，所以在APK签名之后可以进行很多修改——文件可以移动，甚至可以重新压缩。即可以对签名后的文件在进行处理 
		   META-INF 目录下生成三个文件：MANIFEST.MF、CERT.SF、CERT.RSA，最终构成了 APK 签名信息。
	2、v2：v2签名验证了归档中的所有字节，而不是单独的ZIP条目，如果您在构建过程中有任何定制任务，包括篡改或处理APK文件，请确保禁用它们，否则您可能会
	       使v2签名失效，从而使您的APK与Android 7.0和以上版本不兼容。
 
	* v1签名是对jar进行签名，v2签名是对整个apk签名
	* v1和v2的签名使用：
		1、只勾选v1签名并不会影响什么，但是在7.0上不会使用更安全的验证方式
		2、只勾选v2签名7.0以下会直接安装完显示未安装，7.0以上则使用了v2的方式验证
		3、同时勾选v1和v2则所有机型都没问题
	
	注意：
    	在默认情况下，Android Studio 2.2 以上 和 Android Plugin for Gradle 2.2 以上会使用 APK Signature Scheme v2 和传统签名方案来签署应用
		查看APK采用哪种签名方案：
		java -jar apksigner.jar verify -v xxx.apk
		重新对APK进行签名：
		java -jar apksigner.jar sign --ks keyStorePath --ks-key-alias alias --ks-pass pass:KeyStorePwd --key-pass pass:aliasPwd --out out.apk input.apk

**ANR总结**

	1、造成ANR的原因一般有两种：
	   1、当前的事件没有机会得到处理（即主线程正在处理前一个事件，没有及时的完成或者looper被某种原因阻塞住了）
	   2、当前的事件正在处理，但没有及时完成

	2、Looper为什么要无限循环？
	   ActivityThread是主线程入口的类，其中main方法主要就是做消息循环，一旦退出消息循环，那么应用也就退出了。

	3、主线程中的Looper循环为什么不影响运行程序运行？
	   当主线程Looper从消息队列读取消息，当读完所有消息时，主线程阻塞。子线程往消息队列发送消息，并且往管道文件写数据，主线程即被唤醒，
       从管道文件读取数据，主线程被唤醒只是为了读取消息，当消息读取完毕，再次睡眠。因此loop的循环并不会对CPU性能有过多的消耗。

	总结：
       1、Looer.loop()方法可能会引起主线程的阻塞，但只要它的消息循环没有被阻塞，能一直处理事件就不会产生ANR异常。	
	   2、Activity的每个生命周期都是由消息驱动的。接收不同的消息，进行不同的生命周期方法，比如：现在轮询到了onResume这个消息，这时Activity应该执行
		  onResume()。假如这时我们在onResume()里执行耗时操作，同时又进行点击事件，那么点击事件就不会得到及时的处理。这样的话就会造成卡顿，然后ANR了。

**onSaveInstanceState()和onRestoreInstanceState()**

	Android系统的回收机制会在未经用户主动操作的情况下销毁activity，而为了避免系统回收activity导致数据丢失，Android为我们提供了
    onSaveInstanceState(Bundle outState)和onRestoreInstanceState(Bundle savedInstanceState)用于保存和恢复数据。

	* onSaveInstanceState()：当Activity有可能被系统回收的情况下，则调用该方法保存数据，而且是在onStop()之前
	* onSaveInstanceState()会在以下情况被调用： 
		1、当用户按下HOME键时。 
		2、从最近应用中选择运行其他的程序时。 
		3、按下电源按键（关闭屏幕显示）时。 
		4、从当前activity启动一个新的activity时。 
		5、屏幕方向切换时(无论竖屏切横屏还是横屏切竖屏都会调用)。
	* onRestoreInstanceState()：只有在Activity被系统回收，重新创建Activity的情况下才会被调用，在onStart()之后
	    1、回收Activity时机：1、切换横竖屏，2、系统内存不足
		2、如果onRestoreInstanceState被调用了，则页面必然被回收过，则onSaveInstanceState必然被调用过

**Android更新UI问题**

	1、Android为什么更新UI只能在主线程？
	   1、Android的UI访问是没有加锁的，这样在多个线程访问UI是不安全的。在多线程中并发访问可能会导致UI控件处于不可预期的状态，比如主线程正在绘制页面，
          而另外能操作UI的线程对View进行了操作，当主线程绘制完上方的View后，这个被其他线程操作后的VIew的很有可能会覆盖到其他View之上，出现异常。
	   2、不对UI控件的访问加上锁机制的原因有：1、上锁会让UI控件变得复杂和低效，2、上锁后会阻塞某些进程的执行

	2、Android中为什么不能在子线程中更新UI？
	   不管调用View的什么方法，它都会去调用 ViewRootImpl 中的 checkThread() 去检测线程，所以关键在于checkThread()这个方法。
			void checkThread() {
			    if (mThread != Thread.currentThread()) {
			        throw new CalledFromWrongThreadException(
			                "Only the original thread that created a view hierarchy can touch its views.");
			    }
			}
	   其中，Thread.currentThread()是子线程，而mThread是在构造方法中初始化的，是主线程 [ MainThread ]。 每一次访问了UI，Android都会重新绘制View。

	3、部分条件下在onCreate中可以创建子线程操作UI，不会程序崩溃。原因：
       1、在执行onCreate方法的时候ViewRootImpl还没创建，ViewRootImpl的创建在onResume方法回调之后，所以无法去检查当前线程。
       2、ViewRootImpl是在WindowManagerGlobal的addView方法中创建的。

	总结：
		由于Android的UI访问是没有加锁的，当多个线程访问View时会出现不可预期的状态，所以Android中规定只能在UI线程中访问UI，并且在View绘制的过程中
        校验当前线程是否是主线程，否则就抛出异常。


**AppCompatActivity \ view \ window 之间的关系**

	1、调用AppCompatActivity的setContentView()方法，底层实际上通过调用生成AppCompatDelegate对象的setContentView()方法
	2、AppCompatDelegate对象底层PhoneWindow、DecorView创建过程：
       1、先调用PhoneWindow相关方法：generateDecor()和generateLayout()，生成DecorView以及加载对应的布局文件 
	   2、根据不同的属性加载不同的XML布局，生成subDecorView
       3、找到Window中的 android.R.id.content 布局，通过循环删除FrameLayout里面的子View，删除的同时将这些子View添加到这个兼容的View里面即subDecorView
	   4、清除Window中android.R.id.content的id，同时将subDecorView中的id替换为Window中android.R.id.content的id
	   5、最后将subDecor作为参数传到PhoneWindow的setContentView()方法中;
	3、在AppCompatDelegate对象的setContentView(View view)方法中，先查找subDecor中id为android.R.id.content的布局，然后将view通过addView添加到此布局中

**R文件**

    1、Android主项目R文件中控件id是常量，lib包中则不是常量
       * Android在编译APK时会将lib包代码引入，为了防止主项目与lib包中的控件id值一样造成冲突，所以lib包中的控件id不是常量，方便Android编译时进行修改。

    2、Android主项目与lib包同时生成R文件，但是里面的控件id值不同
       * Android在不同的项目中都生成R文件，但在运行时控件id全部从主项目中的R文件取值

    3、Android中不同的布局文件中控件id可以一致
       * 因为在Android的框架设计中，每一个控件都隶属于一棵控件树，每个控件都被其父控件所管理与调配，而根控件是一个容器控件，所有的子控件都是构造在这个根
       * 控件之上，这样并形成了一个控件树的控件域，在这个控件域中是不允许重名的，超出了这个控件域则这些控件的ID是无效的，也就是说在容器控件中的子控件是不
       * 允许重名的，而不在同一容器控件中的两个控件重名也无所谓。