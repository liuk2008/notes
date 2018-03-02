# IntentService源码分析

  IntentService，可以看做是Service和HandlerThread的结合体，在完成了使命之后会自动停止，适合需要在工作线程处理UI无关任务的场景。IntentService使用队列的方式将请求加入队列，然后开启worker thread(线程)来处理队列中的请求。

  1、IntentService在OnCreate方法会创建HandlerThread线程并开启子线程，获取子线程中的looper，同时创建子线程的handler对象。

    @Override
    public void onCreate() {
        super.onCreate();
		// 1、创建子线程
        HandlerThread thread = new HandlerThread("IntentService[" + mName + "]");
        thread.start();
		// 2、获取子线程looper对象
        mServiceLooper = thread.getLooper();
		// 3、创建子线程的handler对象
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }
  
  2、在onStart方法中创建msg对象，使用handler发送消息，而onStartCommand方法底层其实也是在调用onStart方法

    @Override
    public void onStart(Intent intent, int startId) {
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
    }
  
  3、在IntentService内部会创建一个handler对象，该对象使用的是HandlerThread中的looper对象，在handlerMessage方法中调用onHandleIntent方法
当onHandleIntent方法执行完毕后，IntentService自动销毁并退出looper循环。

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            onHandleIntent((Intent)msg.obj);
            stopSelf(msg.arg1);
        }
    }


# Android中ResultReceiver使用

  使用ResultReceiver在Activity与Service之间进行通信，流程如下：
  Activity在启动service时传递ResultReceiver实例给service，然后service执行结束后，调用resulrReceiver.send(); 然后在onReceiveResult里面处理回调逻辑。

  1、通过构造函数创建ResultReceiver对象，只需要传入一个Handler并且这个Handler是可以为null的。这个Handler的作用只有一个，就是控制回调函数执行在创建Handler的
线程。如果在Activity主线程创建的handler实例，则回调也会在主线程执行。就可以直接在回调中操作UI。 

    public ResultReceiver(Handler handler) {
	    mLocal = true;
	    mHandler = handler;
	}

  2、调用send方法发送数据，当我们的耗时任务执行完，我们将调用这个send方法。这里可以看到。若是实例化时handler传Null，则直接在调用send方法的线程中执行
onReceiveResult回调。若是handler不为空，就用handler来post。

    public void send(int resultCode, Bundle resultData) {
	    if (mLocal) {
	        if (mHandler != null) {
	            mHandler.post(new MyRunnable(resultCode, resultData));
	        } else {
	            onReceiveResult(resultCode, resultData);
	        }
	        return;
	    }
	    
	    if (mReceiver != null) {
	        try {
	            mReceiver.send(resultCode, resultData);
	        } catch (RemoteException e) {
	        }
	    }
	}

  3、当handler对象不为空时，通过post请求运行线程任务，onReceiveResult回调函数执行在创建Handler对象的线程。
	
    class MyRunnable implements Runnable {
	    final int mResultCode;
	    final Bundle mResultData;
	    
	    MyRunnable(int resultCode, Bundle resultData) {
	        mResultCode = resultCode;
	        mResultData = resultData;
	    }
	    
	    public void run() {
	        onReceiveResult(mResultCode, mResultData);
	    }
	}