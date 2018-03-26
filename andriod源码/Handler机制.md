## Android之Handler机制 ##

**UI线程中Handler、Looper、MessageQueue三者之间的关系**

* 1、Android应用启动的时候会创建 UI主线程 的 Looper 对象，它存在于整个应用的生命周期，用于处理消息队列里的 Message。

* 2、Android的消息队列和消息循环都是针对具体线程的，一个线程可以存在一个消息队列和消息循环，特定线程的消息只能分发给本线程，
不能跨线程和跨进程通讯。但是创建的工作线程默认是没有消息队列和消息循环的，如果想让工作线程具有消息队列和消息循环，就需要在线程中
先调用Looper.prepare()来创建消息队列，然后调用Looper.loop()进入消息循环
	
		public void run(){  
		   Looper.prepare();  
		   handler=new MyHandler();  
		   Looper.loop();  
		} 
	
		Handler handler = new Handler();那么这个会默认用当前线程的looper
	    Handler handler = new Handler(Looper.getMainLooper());若是实例化的时候用Looper.getMainLooper()就表示放到主UI线程去处理。

		* 检测当前线程是否为主线程

		1、Looper.myLooper() == Looper.getMainLooper();
		2、Looper.getMainLooper().getThread() == Thread.currentThread() 
	
**Loop类**

* 1、通过Looper.prepare()来创建消息队列，并将当前线程与Looper对象进行绑定

		* 1、通过hreadLocal判断当前线程是否已经绑定Looper对象，已存在抛出异常
		* 2、当前线程不存在Looper对象时，创建Looper对象，通过ThreadLocal将当前线程与Looper对象进行绑定
		
			public static void prepare() {
			    prepare(true);
			}
			// 先判断当前线程是否已经绑定Looper对象，一个线程只能绑定一个Looper对象
			private static void prepare(boolean quitAllowed) {
			    if (sThreadLocal.get() != null) {
			        throw new RuntimeException("Only one Looper may be created per thread");
			    }
			    sThreadLocal.set(new Looper(quitAllowed));
			}

		* 3、在创建Looper对象时，底层实际创建MessageQueue对象，获取当前线程对象
		
		    private Looper(boolean quitAllowed) {
		        mQueue = new MessageQueue(quitAllowed);
		        mThread = Thread.currentThread();
		    }

* 2、通过Looper.loop()进入消息循环，处理消息，底层实际上调用Handler对象dispatchMessage处理消息

		* 1、先获取当前线程的Looper对象，如果为空，抛出异常，提示未调用Looper.prepare()，同时获取Looper对象中的MessageQueue对象
	
			    final Looper me = myLooper();
		        if (me == null) {
		            throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
		        }
				final MessageQueue queue = me.mQueue;
	
		* 2、开启死循环，遍历消息队列。通过MessageQueue对象取出Message对象，获取Message对象中的Handler对象，调用Handler对象dispatchMessage处理消息
		 
				// 开启死循环，遍历消息队列
			  	for (;;) {
		            Message msg = queue.next(); // might block
		            if (msg == null) {
		                // No message indicates that the message queue is quitting.
		                return;
		            }
					// 获取Message对象中的Handler对象，调用Handler对象dispatchMessage处理消息
		            msg.target.dispatchMessage(msg);
					// 从消息池中释放消息
		            msg.recycleUnchecked();
		        }

* 3、调用Looper对象的quitSafely和quit方法底层实际是调用MessageQueue对象的方法，退出消息队列

**Handler类**

* 1、创建Handler对象，通过构造方法传入Looper对象，底层获取MessageQueue对象。传入Looper对象，底层实际调用另一个构造方法。同时获取Looper对象上绑定的MessageQueue对象
	
		public Handler(Looper looper) {
	        this(looper, null, false);
	    }
		
		public Handler(Looper looper, Callback callback, boolean async) {
	        mLooper = looper;
	        mQueue = looper.mQueue;
	        mCallback = callback;
	        mAsynchronous = async;
	    }

* 2、通过Handler对象发送消息，底层实际调用sendMessageAtTime()方法。
	
		* 当存在Message对象时，底层实际调用sendMessageDelayed()方法。
		
			public final boolean sendMessage(Message msg){
		        return sendMessageDelayed(msg, 0);
		    }
	
		* 当不存在Message对象时，发送空消息时，Handler底层会主动创建消息，然后调用sendMessageDelayed()方法
		   
			public final boolean sendEmptyMessageDelayed(int what, long delayMillis) {
		        Message msg = Message.obtain();
		        msg.what = what;
		        return sendMessageDelayed(msg, delayMillis);
		    }
			
			创建Message对象的时候，有三种方式，分别为：
			Message msg = new Message();  直接初始化一个Message对象
			Message msg1 = Message.obtain(); 从整个Messge池中返回一个新的Message实例，消息池中那些已经创建但不再使用的对象。 
			Message msg2 = handler.obtainMessage();  handler.obtainMessage最终也是调用了Message的obtain方法
       
		* sendMessageDelayed()方法底层实际调用sendMessageAtTime()方法发送消息
		
			public final boolean sendMessageDelayed(Message msg, long delayMillis){
		        if (delayMillis < 0) {
		            delayMillis = 0;
		        }
		        return sendMessageAtTime(msg, SystemClock.uptimeMillis() + delayMillis);
			}

		* sendMessageAtTime()方法底层首先获取到MessageQueue对象，然后调用enqueueMessage()方法处理消息
		
			public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
		        MessageQueue queue = mQueue;
		        if (queue == null) {
		            RuntimeException e = new RuntimeException(
		                    this + " sendMessageAtTime() called with no mQueue");
		            Log.w("Looper", e.getMessage(), e);
		            return false;
		        }
		        return enqueueMessage(queue, msg, uptimeMillis);
		    }

		* enqueueMessage()底层实际上是将Handler对象与Message对象进行绑定，然后调用MessageQueue对象的enqueueMessage()方法将Msg添加至消息队列中
		
			private boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis) {
				// 将Handler对象与Message对象进行绑定
		        msg.target = this;
		        if (mAsynchronous) {
		            msg.setAsynchronous(true);
		        }
				// 将Msg添加至消息队列中
		        return queue.enqueueMessage(msg, uptimeMillis);
		    }

* 3、Loop对象在检测MessageQueue时，获取到Msg，通过Message对象获取到Handler对象，调用dispatchMessage处理消息
			
		public void dispatchMessage(Message msg) {
	        if (msg.callback != null) {
	            handleCallback(msg);
	        } else {
	            if (mCallback != null) {
	                if (mCallback.handleMessage(msg)) {
	                    return;
	                }
	            }
				// 底层实际调用 handleMessage() 方法处理消息
	            handleMessage(msg);
	        }
	    }


* Message里的消息池问题      

		在这个类中sPool代表这个消息池的头消息，sPoolSize表示消息池中可用的消息的个数即没有被使用的Message对象的个数，next表示下一个可用的消息Message对象。
		可以看到obtain()方法说会从全局消息池中取消息，假设是第一次获得一个Message对象，那么sPool肯定为null，也就说第一次获取消息Message对象时是还没有消息
    	池的，必须通过Message的构造方法获取一个Message对象的，Message的构造方法为空。
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
		在Looper的loop()方法的最后调用了Message对象msg的recycle()方法来回收这个Message对象，通过recycle()将这个
	    Message对象的数据清空然后链接到消息池中（采用的头插法）。