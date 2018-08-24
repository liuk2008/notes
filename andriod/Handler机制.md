## Android之Handler机制 ##

**UI线程中Handler、Looper、MessageQueue三者之间的关系**

* 1、ActivityThread中会在main()方法创建Looper对象

* 2、Android应用启动的时候会创建 UI主线程 的 Looper 对象，它存在于整个应用的生命周期，用于处理消息队列里的 Message。

* 3、Android的消息队列和消息循环都是针对具体线程的，一个线程可以存在一个消息队列和消息循环，特定线程的消息只能分发给本线程，不能跨线程和跨进程通讯。
	
		// 创建子线程中的Looper对象
		public void run(){  
		   Looper.prepare();  
		   handler=new MyHandler();  
		   Looper.loop();  
		} 
	
		Handler handler = new Handler();那么这个会默认用当前线程的looper
	    Handler handler = new Handler(Looper.getMainLooper());入参自定义Looper
	
**Loop类**

* 1、通过Looper.prepare()来创建消息队列，并将当前线程与Looper对象进行绑定，在一个线程中只能调用一次

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

		** Looper.prepare() 为当前线程，绑定一个唯一的looper对象，同时，也有了唯一的messageQueue对象。

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

* 1、创建Handler对象，通过构造方法传入当前线程绑定的Looper对象，底层获取Looper对象上绑定的MessageQueue对象
	
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
			Message msg = new Message();  // 直接初始化一个Message对象
			Message msg1 = Message.obtain();  // 从整个Messge池中返回一个新的Message实例，消息池中那些已经创建但不再使用的对象。 
			Message msg2 = handler.obtainMessage();  // 最终也是调用了Message的obtain方法
       
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
		        msg.target = this;  	// 将Handler对象与Message对象进行绑定
		        if (mAsynchronous) {
		            msg.setAsynchronous(true);
		        }
		        return queue.enqueueMessage(msg, uptimeMillis); // 将Msg添加至消息队列中
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

**MessageQueue类**

* 1、调用enqueueMessage检测消息队列

		boolean enqueueMessage(Message msg, long when) {
	
	        synchronized (this) {  // 可能有多个线程，同时发送handler信息，所在要同步
	            if (mQuitting) {
	                IllegalStateException e = new IllegalStateException(
	                        msg.target + " sending message to a Handler on a dead thread");
	                Log.w(TAG, e.getMessage(), e);
	                msg.recycle();
	                return false;
	            }
	
	            msg.markInUse();
	            msg.when = when;
	            Message p = mMessages;
	            boolean needWake;
	            if (p == null || when == 0 || when < p.when) {  // 如果对列中没有message时
	                msg.next = p; 	 		 // 将当前msg的next指为null
	                mMessages = msg; 		// 将当前的msg赋值给成员变量msg
	                needWake = mBlocked;
	            } else {
	                needWake = mBlocked && p.target == null && msg.isAsynchronous();
	                Message prev;
	                for (;;) {
	                    prev = p;
	                    p = p.next;
	                    if (p == null || when < p.when) {
	                        break;
	                    }
	                    if (needWake && p.isAsynchronous()) {
	                        needWake = false;
	                    }
	                }
	                msg.next = p; // invariant: p == prev.next
	                prev.next = msg;
	            }
	
	            // We can assume mPtr != 0 because mQuitting is false.
	            if (needWake) {
	                nativeWake(mPtr);
	            }
	        }
	        return true;	
		}


		** enqueueMessage 方法中，是将所有的message 按照消息的执行时间为顺序，从小到大，以单链表的形式，存储的。**
		** 时间越小，表示，执行的时间越早。这样的话，单链表中第一个message,就是下一个应该执行的message **	

* 2、调用next()取出消息进行处理

		 while (true) { 
	            
	            synchronized (this) {
	                now = SystemClock.uptimeMillis(); // 获得当前时间
	                Message msg = pullNextLocked(now); // 取出message, 如果队列为空，或第一个message的执行时间都没有到，那么，pullNextLocked 方法，返回null
	                if (msg != null) return msg; // 取到了要执行的message ,直接返回给looper 来执行。
	                ...
	            }
	
				 synchronized (this) {
	                    if (mMessages != null) { // 说明，队列是有消息，但第一个消息的执行时间还没有到。
	                        if (mMessages.when-now > 0) {
	                            Binder.flushPendingCommands();
	                            this.wait(mMessages.when-now); // 当前线程，休息一段时间，当有新的message加入队列时，会唤醒线程，再继续执行
	                        }
	                    } else {
	                        Binder.flushPendingCommands();
	                        this.wait();
	                    }
	                }
	            }
	
	
		** next 方法中，也是开了一个循环，目的是不论如何，要拿到一个message 交给looper来用,如果当前没有要执行的message那就等待一会再拿。**


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