* Android之Handler机制

  1、Handler机制   

    Android系统中的Looper负责管理线程的消息队列和消息循环。通过Looper.myLooper()得到当前线程的Looper对象，
    通过Looper.getMainLooper()得到当前进程的主线程的Looper对象。

    前面提到，Android的消息队列和消息循环都是针对具体线程的，一个线程可以存在一个消息队列和消息循环，特定线程的消息只能分发给本线程，
    不能跨线程和跨进程通讯。但是创建的工作线程默认是没有消息队列和消息循环的，如果想让工作线程具有消息队列和消息循环，就需要在线程中
    先调用Looper.prepare()来创建消息队列，然后调用Looper.loop()进入消息循环

  2、创建Message对象的时候，有三种方式，分别为：

	1.Message msg = new Message();  直接初始化一个Message对象
	2.Message msg1 = Message.obtain(); 从整个Messge池中返回一个新的Message实例，消息池中那些已经创建但不再使用的对象。 
	3.Message msg2 = handler.obtainMessage();  handler.obtainMessage最终也是调用了Message的obtain方法
    
  4、Handler实例化过程
		
    Android 应用启动的时候会创建 UI 主线程的 Looper 对象，它存在于整个应用的生命周期，用于处理消息队列里的 Message。

    1.在线程的run()方法里调用Looper.prepare()，实例化一个Handler对象，调用Looper.loop()使线程进入消息循环。
      Handler对象的实例话必须在Looper.prepare()之后。
      public void run(){  
		   Looper.prepare();  
		   handler=new MyHandler();  
		   Looper.loop();  
	  } 
	
	2.当我们要给具有消息循环的线程发送消息时，我们先要获得具有消息循环的线程的 Handler 对象（或者先获取具有消息循环
      的线程的Looper对象，再使用这个Looper对象构造Handler对象），构造一个Message对象，然后调用Handler对象的sendMessage方法
    
	3.Message里的消息池问题
		public static Message obtain() {  
		    synchronized (sPoolSync) {  
		        if (sPool != null) {  
		            Message m = sPool;  
		            sPool = m.next;  
		            m.next = null;  
		            sPoolSize--;  
		            return m;  
		        }  
		    }  
		    return new Message();  
		}  
      在这个类中sPool代表这个消息池的头消息，sPoolSize表示消息池中可用的消息的个数即没有被使用的Message对象的个数，next表示下一个可用的消息Message对象。
      可以看到obtain()方法说会从全局消息池中取消息，假设是第一次获得一个Message对象，那么sPool肯定为null，也就说第一次获取消息Message对象时是还没有消息
      池的，必须通过Message的构造方法获取一个Message对象的，Message的构造方法为空。


	  在Looper的loop()方法的最后调用了Message对象msg的recycle()方法来回收这个Message对象，通过recycle()将这个
      Message对象的数据清空然后链接到消息池中（采用的头插法）。

  5、Handler一定要在主线程实例化吗?

  new Handler()和new Handler(Looper.getMainLooper())的区别
	
	如果你不带参数的实例化：Handler handler = new Handler();那么这个会默认用当前线程的looper
    Handler handler = new Handler(Looper.getMainLooper());若是实例化的时候用Looper.getMainLooper()就表示放到主UI线程去处理。 
	
	一般而言，如果你的Handler是要来刷新操作UI的，那么就需要在主线程下跑。情况:

	1.要刷新UI，handler要用到主线程的looper。那么在主线程 Handler handler = new Handler();，
      如果在其他线程，也要满足这个功能的话，要Handler handler = new Handler(Looper.getMainLooper());

	2.不用刷新ui,只是处理消息。 当前线程如果是主线程的话，Handler handler = new Handler();
      如果不是主线程的话，因为只有UI线程默认Loop.prepare();Loop.loop();过，其他线程需要手动调用这两个，否则会报错。
      Looper.prepare(); Handler handler = new Handler();Looper.loop();

  6、message.what,message.arg1,message.arg2,message.obj，他们在之间有什么区别呢？
	
	what就是一般用来区别消息的，比如你传进去的时候msg.what = 3; 然后处理的时候判断msg.what == 3是不是成立的，
    是的话，表示这个消息是干嘛干嘛的（自己能区别开）
	
	至于arg1,arg2，其实也就是两个传递数据用的，两个int值，看你自己想要用它干嘛咯。如果你的数据只是简单的int值，那么用这两个，比较方便。 
    其实这里你还少说了个，setData(Bundle),上面两个arg是传递简单int的，这个是传递复杂数据的。
	
	msg.obj呢，这个就是传递数据了，msg中能够携带对象，在handleMessage的时候，可以把这个数据取出来做处理了。
    不过呢，如果是同一个进程，最好用上面的setData就行了，这个一般是Messenger类来用来跨进程传递可序列化的对象的，这个比起上面的来，更消耗性能一些。
	

* HandlerThread的特点

    Android 提供了一个线程类 HanderThread 类，HanderThread类继承了Thread类并封装了 Looper 对象，在用到 HandlerThread 时，同样必须调用start方法。
    这个类能方便的开启一个包含looper的线程，这个looper也能被用来创建一个handler对象（意思就是把looper以参数形式传递到handler的构造器），

    MessageQueue是在Looper的私有构造函数Looper()中实例化的；

    HandlerThread是被显式地通过new创建的实例，而与它绑定在一起的Looper是在HandlerThread的执行过程中被实例化的，相应的MessageQueue也是在这个过程中实例化的。
    looper.loop实际上就是一个while(true)的死循环，MessageQueue是Looper保留的一份引用，通过它的next()[序列1]获取MessageQueue中的下一个要处理的消息，
    这个过程中如果没有相应的消息，执行它的线程会用this.wait()释放它所拥有的MessageQueue的对象锁而等待。

    HandlerThread将loop转到子线程中处理，说白了就是将分担MainLooper的工作量，降低了主线程的压力，使主界面更流畅。
    开启一个线程起到多个线程的作用。处理任务是串行执行，按消息发送顺序进行处理。HandlerThread本质是一个线程，在线程内部，代码是串行处理的。
	但是由于每一个任务都将以队列的方式逐个被执行到，一旦队列中有某个任务执行时间过长，那么会导致后续的任务都会被延迟处理。
	HandlerThread拥有自己的消息队列，它不会干扰或阻塞UI线程。对于网络IO操作，HandlerThread并不适合，因为它只有一个线程，还得排队一个一个等着。
		
	博客链接：http://www.jianshu.com/p/b7fec0545368


* 检测当前线程是否为主线程

	1、Looper.myLooper() == Looper.getMainLooper();
	2、Looper.getMainLooper().getThread() == Thread.currentThread()