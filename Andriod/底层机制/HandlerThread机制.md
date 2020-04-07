
## HandlerThread源码分析 ##

* HandlerThread 是一个包含 Looper 的 Thread，本质上就是一个普通Thread，只不过内部建立了Looper提供给外部使用。
	
* 1、通过构造函数指定线程名称以及线程优先级，此处的优先级是android.os.Process

		// 初始化线程对象
		public HandlerThread(String name) {
		    super(name);
		    mPriority = Process.THREAD_PRIORITY_DEFAULT;
		}

* 2、在线程的run方法中，对Looper对象进行实例化，此时获取Looper对象唤醒线程，并且开启Looper轮询消息队列。

		// 执行初始化任务
		protected void onLooperPrepared() {
		}

		@Override
		public void run() {
		   mTid = Process.myTid();
		   Looper.prepare();
		   synchronized (this) {
			mLooper = Looper.myLooper();	// 获取当前线程绑定的looper对象
			notifyAll();
		   }
		   Process.setThreadPriority(mPriority);
		   onLooperPrepared();
		   Looper.loop();
		   mTid = -1;
		}
		
* 3、提供getLooper方法，获取当前线程绑定的Looper对象

		public Looper getLooper() {
		   // 先判断线程是否存活，当线程已消亡时返回null
		   if (!isAlive()) {                  
		       return null;
	   	   }

		   // 存活通过一个循环检测Looper对象是否存在，不存在时线程进入等待状态。
		   synchronized (this) {
		       while (isAlive() && mLooper == null) {
			   try {
			          wait();
			       } catch (InterruptedException e) {
			     
			   }
		       }
		   }
		   return mLooper;
		}

* 4、当HandlerThread被创建后，必须调用start方法开启线程，以便于初始化Looper对象

* 5、创建Handler对象，获取HandlerThread中的Looper对象，以参数形式传递到Handler的构造器，此时Handler对象中的handleMessage方法运行在HandlerThread线程中

* 6、通过Handler发送msg可以在handleMessage方法中进行耗时任务，每一个任务都将以队列的方式逐个被执行到，一旦队列中有某个任务执行时间过长，那么会导致后续的任务都会被延迟处理，另外HandlerThread在使用完毕后必须手动退出循环队列，否则会造成内存泄漏
	
		HandlerThread thread = new HandlerThread("MyHandlerThread");
		thread.start();
		// 创建子线程的handler对象
		Handler handler = new Handler(thread.getLooper()) {   
		   @Override
		   public void handleMessage(Message msg) {     // 执行耗时任务 
			super.handleMessage(msg);
		      }
		   };
		}
		
* 总结：
* 1、创建HandlerThread对象，调用start方法，开启线程，底层实际是创建Looper对象
* 2、创建Handler对象，必须入参HandlerThread中的Looper对象，以保证Handler中的MessageQueue与HandlerThread线程使用MessageQueue一致
* 3、通过Handler发送msg，在handleMessage方法中执行耗时任务
