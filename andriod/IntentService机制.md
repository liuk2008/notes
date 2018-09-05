
## IntentService&ResultReceiver ##

### IntentService ###

**IntentService可以认为是一个异步服务，在不创建线程的情况下可以onHandleIntent方法中进行耗时操作而不影响主线程。**

	* 1、底层通过Handler和HandlerThread实现了异步操作，通过开启WorkerThread(线程)来处理
	* 2、由于使用Handler进行异步操作，则请求实际上是以队列方式的进行处理，每次只能处理一个请求，当所有请求完成了使命之后会自动停止。
	
	* 自定义Handler类，在handlemessage方法中进行调用onHandleIntent方法进行异步操作，执行完毕后，IntentService自动销毁
	
       private final class ServiceHandler extends Handler {
	        public ServiceHandler(Looper looper) {
	            super(looper);
	        }
	
	        @Override
	        public void handleMessage(Message msg) {
	            onHandleIntent((Intent)msg.obj);      // 调用onHandleIntent方法进行异步操作
	            stopSelf(msg.arg1);
	        }
		}

	* IntentService在OnCreate方法会创建HandlerThread线程，同时创建Handler对象。而此处HandlerThread的名称通过IntentService构造函数传递。

	    public void onCreate() {
	        super.onCreate();
	        HandlerThread thread = new HandlerThread("IntentService[" + mName + "]");  // 1、创建HandlerThread线程
	        thread.start();															   // 2、必须先运行线程
	        mServiceLooper = thread.getLooper();  									   // 2、获取线程looper对象
	        mServiceHandler = new ServiceHandler(mServiceLooper);  					   // 3、创建线程的handler对象
	    }

	* 在onStart方法中创建msg对象，使用handler发送消息，而onStartCommand方法底层其实也是在调用onStart方法。
	
	    public void onStart(Intent intent, int startId) {
	        Message msg = mServiceHandler.obtainMessage();
	        msg.arg1 = startId;
	        msg.obj = intent;
	        mServiceHandler.sendMessage(msg);
	    }


### ResultReceiver ###

**使用ResultReceiver在Activity与Service之间进行通信，流程如下：**

	* 1、Activity在启动service时，通过Intent传递ResultReceiver实例给service
	* 2、service执行结束后，调用resulrReceiver.send(); 然后在onReceiveResult里面处理回调逻辑
	
	* 创建ResultReceiver对象，需要传入一个Handler，不过这个Handler是可以为null，作用是控制回调函数执行在创建Handler的线程
	
			ResultReceiver resultReceiver = new ResultReceiver(handler) {
			    @Override
			    protected void onReceiveResult(int resultCode, Bundle resultData) {
			        super.onReceiveResult(resultCode, resultData);
			        ToastUtils.getToast(getApplication(), resultData.getString("test"));
			    }
			}

			* 当handler对象为空时，则直接在调用send方法的线程中执行onReceiveResult回调，例如：
			 	* 在Service的OnStartCommand方法中调用send方法，onReceiveResult回调执行在主线程中
				* main-> onReceiveResult()
				* 在IntentService的onHandleIntent方法中调用send方法，onReceiveResult回调执行在子线程中。
				* IntentService[MyIntentService]-> onReceiveResult()
			* 当handler对象不为空，onReceiveResult回调执行在创建Handler的线程。

	* ResultReceiver底层同时实例化一个IResultReceiver对象

		class MyResultReceiver extends IResultReceiver.Stub {
	        public void send(int resultCode, Bundle resultData) {
	            if (mHandler != null) {
	                mHandler.post(new MyRunnable(resultCode, resultData));
	            } else {
	                onReceiveResult(resultCode, resultData);	
	            }
	        }
	    }

	* 调用send方法发送数据，若是实例化时handler传Null，则直接在调用send方法的线程中执行onReceiveResult回调。
	* 若是handler不为空，通过post请求运行线程任务，onReceiveResult回调函数执行在创建Handler对象的线程。Handler对象的线程可以通过Looper来指定。

		public ResultReceiver(Handler handler) {
	        mLocal = true;
	        mHandler = handler;
	    }
		
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