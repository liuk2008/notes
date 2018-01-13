* Handler一定要在主线程实例化吗?

  new Handler()和new Handler(Looper.getMainLooper())的区别
	
	如果你不带参数的实例化：Handler handler = new Handler();那么这个会默认用当前线程的looper
	
	一般而言，如果你的Handler是要来刷新操作UI的，那么就需要在主线程下跑。 情况:
	
	1.要刷新UI，handler要用到主线程的looper。那么在主线程 Handler handler = new Handler();，
      如果在其他线程，也要满足这个功能的话，要Handler handler = new Handler(Looper.getMainLooper());
	
	2.不用刷新ui,只是处理消息。 当前线程如果是主线程的话，Handler handler = new Handler();
      不是主线程的话，Looper.prepare(); Handler handler = new Handler();Looper.loop();
      或者Handler handler = new Handler(Looper.getMainLooper());
	  若是实例化的时候用Looper.getMainLooper()就表示放到主UI线程去处理。 
      如果不是的话，因为只有UI线程默认Loop.prepare();Loop.loop();过，其他线程需要手动调用这两个，否则会报错。
  
  message.what,message.arg1,message.arg2,message.obj，他们在之间有什么区别呢？
	
	what就是一般用来区别消息的，比如你传进去的时候msg.what = 3; 然后处理的时候判断msg.what == 3是不是成立的，
    是的话，表示这个消息是干嘛干嘛的（自己能区别开）
	
	至于arg1,arg2，其实也就是两个传递数据用的，两个int值，看你自己想要用它干嘛咯。如果你的数据只是简单的int值，那么用这两个，比较方便。 
    其实这里你还少说了个，setData(Bundle),上面两个arg是传递简单int的，这个是传递复杂数据的。
	
	msg.obj呢，这个就是传递数据了，msg中能够携带对象，在handleMessage的时候，可以把这个数据取出来做处理了。
    不过呢，如果是同一个进程，最好用上面的setData就行了，这个一般是Messenger类来用来跨进程传递可序列化的对象的，这个比起上面的来，更消耗性能一些。
	
	相关链接:
  
  http://www.cnblogs.com/xpxpxp2046/archive/2012/04/13/2445395.html
  
  http://www.cnblogs.com/xpxpxp2046/archive/2012/04/13/2445355.html

* HandlerThread的特点

    HandlerThread将loop转到子线程中处理，说白了就是将分担MainLooper的工作量，降低了主线程的压力，使主界面更流畅。
    开启一个线程起到多个线程的作用。处理任务是串行执行，按消息发送顺序进行处理。HandlerThread本质是一个线程，在线程内部，代码是串行处理的。
	但是由于每一个任务都将以队列的方式逐个被执行到，一旦队列中有某个任务执行时间过长，那么会导致后续的任务都会被延迟处理。
	HandlerThread拥有自己的消息队列，它不会干扰或阻塞UI线程。对于网络IO操作，HandlerThread并不适合，因为它只有一个线程，还得排队一个一个等着。
		
	博客链接：http://www.jianshu.com/p/b7fec0545368
