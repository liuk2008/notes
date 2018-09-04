
## HandlerThread源码分析 ##

**HandlerThread 是一个包含 Looper 的 Thread，本质上就是一个普通Thread，只不过内部建立了Looper提供给外部使用。这个looper也能被用来创建一个handler对象（意思就是把looper以参数形式传递到handler的构造器），将HandlerThread提供的Looper传递进去绑定线程，此时可以在handleMessage() 方法中进行耗时任务。**

1、通过构造函数指定线程名称以及线程优先级，此处的优先级是android.os.Process

	// 1、初始化线程对象
	   public HandlerThread(String name) {
	        super(name);
	        mPriority = Process.THREAD_PRIORITY_DEFAULT;
	   }

	// 2、执行初始化任务
	   protected void onLooperPrepared() {
	   }


2、通过getLooper方法可以获取Looper对象，此时内部先判断线程是否存活，当线程已消亡时返回null。若线程存活通过一个循环检测Looper对象是否存在，不存在时线程进入等待状态。

    public Looper getLooper() {
        if (!isAlive()) {
            return null;
        }
        
        // If the thread has been started, wait until the looper has been created.
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


3、在线程的run方法中，对Looper对象进行实例化，此时获取Looper对象唤醒线程，并且开启Looper轮询消息队列。


    @Override
    public void run() {
        mTid = Process.myTid();
        Looper.prepare();
        synchronized (this) {
            mLooper = Looper.myLooper();
            notifyAll();
        }
        Process.setThreadPriority(mPriority);
        onLooperPrepared();
        Looper.loop();
        mTid = -1;
    }

总结：当HandlerThread被创建后，必须调用start方法开启线程后才能正常使用子线程的Looper对象。并创建Handler对象将Looper对象传递进去，通过发送msg消息通知可以在handleMessage() 方法中进行耗时任务。但是，每一个任务都将以队列的方式逐个被执行到，一旦队列中有某个任务执行时间过长，那么会导致后续的任务都会被延迟处理，另外HandlerThread在使用完毕后必须手动退出循环队列，否则会造成内存泄漏。
