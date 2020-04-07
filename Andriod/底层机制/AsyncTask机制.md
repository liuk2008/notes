
## AsyncTask分析 ##

* Android的类AsyncTask对线程间通讯进行了包装，提供了简易的编程方式来使后台线程和UI线程进行通讯，后台线程执行异步任务，并把操作结果通知UI线程。

* 内部实现是：线程池+Handler。其中Handler是为了处理线程之间的通信，AsyncTask内部实现了两个线程池，用来管理和调度进程中的所有Task的，通过重用原来创建的线程执行新的任务，分别是：串行线程池和固定线程数量的线程池。而这个固定线程数量则是通过CPU的数量决定的。

**AsyncTask启动方式**

* 1、通过execute()方法启动

	* 底层实际调用executeOnExecutor，使用AsyncTask默认创建SerialExecutor线程池执行任务，按先后顺序每次只运行一个，每个任务都是串行执行的。
	
	* 1、Executor SERIAL_EXECUTOR = new SerialExecutor(); 单任务运行
		* 底层使用双端队列，当有任务到来时，就把任务入队，然后在scheduleNext中出队一个任务，然后放到THREAD_POOL_EXECUTOR里去执行， 每次只放一个任务，SerialExecutor的作用就是将任务排好队，然后放入THREAD_POOL_EXECUTOR线程池中去执行
		
	* 2、Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);  多任务运行
		* 创建队列：最大等待任务数128个。BlockingQueue<Runnable> sPoolWorkQueue =new LinkedBlockingQueue<Runnable>(128)
		* 创建线程数： 核心线程数=CPU核数+1，最大线程数=CPU核数*2+1，此后重复使用已创建的线程

	
* 2、通过executeOnExecutor()方法启动

	* 1、使用系统默认的线程池
		* task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)  执行并发任务
		* task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR)       执行串行任务 此线程池是AsyncTask内部默认使用
		
	* 2、使用Java线程池执行并发任务
		* Executors.newCachedThreadPool()：每个AsyncTask任务都单独起一个线程执行，也就是说所有的都是并发的。
		* Executors.newFixedThreadPool(3)：创建指定线程数量的线程池，并发数上限就是指定的线程数。但新任务产生，没有空闲的线程，就只能等待。
	* 3、使用自定义线程池

**AysncTask内部方法的使用**

* 1、AsyncTask对象不可重复使用，也就是说一个AsyncTask对象只能execute()一次，否则会有异常抛出"java.lang.IllegalStateException

* 2、各个方法掉调用时机
	* void onPreExecute() 加载前的初始化，在UI线程启动AsyncTask时，运行在UI线程。在工作线程启动AsyncTask时，运行在工作线程。
	* Result doInBackground(Params... params) 在onPreExecute方法执行后马上执行，在子线程运行
	* void onProgressUpdate(Progress... values) 在doInBackground调用publishProgress时触发,在UI线程运行
	* void onPostExecute(Result result) 在doInBackground 执行完成后，onPostExecute方法将被UI线程调用，result为doInBackground执行后的返回值
	* void onCancelled(Result result) 在主线程中调用cancel()的时候触发

	* 当调用cancel()后，只是AsyncTask对象设置了一个标识位。此时doInBackground()和onProgressUpdate()还会继续执行，而doInBackground()执行完后将会调用onCancelled()，不再调用onPostExecute()。此外，我们可以在doInBackground()不断的检查 isCancelled()的返回值，当其返回true时就停止执行，特别是有循环的时候，从而停止任务的运行。

* 3、AsyncTask的使用注意事项
	* 1、改善你的设计，少用异步处理。对于一般性的数据库查询，少量的I/O操作是没有必要启动线程的。
	* 2、与主线程有交互时用AsyncTask，否则就用Thread。
	* 3、当有需要大量线程执行任务时，一定要创建线程池。
		
**AysncTask内部实现机制**

* 1、创建执行串行任务的线程池

		public static final Executor SERIAL_EXECUTOR = new SerialExecutor();         
		private static volatile Executor sDefaultExecutor = SERIAL_EXECUTOR;
		// 创建线程池对象
		private static class SerialExecutor implements Executor {
			final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
			Runnable mActive;

			public synchronized void execute(final Runnable r) {
			    mTasks.offer(new Runnable() {
				public void run() {
				    try {
					r.run();
				    } finally {
					scheduleNext();
				    }
				}
			    });
			    if (mActive == null) {
				scheduleNext();
			    }
			}

			protected synchronized void scheduleNext() {
			    if ((mActive = mTasks.poll()) != null) {
				THREAD_POOL_EXECUTOR.execute(mActive);
			    }
			}
		}


* 2、创建执行并发任务的线程池

		private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors(); 
		private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4)); 
		private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;		
		// 线程池中非核心线程存活时间
		private static final int KEEP_ALIVE_SECONDS = 30;									
		// 创建线程工厂对象
		private static final ThreadFactory sThreadFactory = new ThreadFactory() {			
			private final AtomicInteger mCount = new AtomicInteger(1);

			public Thread newThread(Runnable r) {
			    return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
			}
		};

		// 一个长度为128的阻塞队列，也就是说这个线程池的任务队列中可以同时有128个任务等待执行
		private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(128);

		// 创建线程池
    		public static final Executor THREAD_POOL_EXECUTOR;
		
		static {
			ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
				CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS, TimeUnit.SECONDS,
				sPoolWorkQueue, sThreadFactory);
			threadPoolExecutor.allowCoreThreadTimeOut(true);
			THREAD_POOL_EXECUTOR = threadPoolExecutor;
		}


* 3、AsyncTask构造函数，底层使用Callable存储任务与FutureTask执行任务

		public AsyncTask(@Nullable Looper callbackLooper) {

		// 创建Handler对象
		mHandler = callbackLooper == null || callbackLooper == Looper.getMainLooper()
		    ? getMainHandler(): new Handler(callbackLooper);

		// 实现Java提供的Callable接口，存储线程执行任务
		mWorker = new WorkerRunnable<Params, Result>() {		
				public Result call() throws Exception {
					mTaskInvoked.set(true);
					Result result = null;
					try {
					    Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
					    //noinspection unchecked
					    result = doInBackground(mParams);
					    Binder.flushPendingCommands();
					} catch (Throwable tr) {
					    mCancelled.set(true);
					    throw tr;
					} finally {
					    // 底层通过Handler发送
					    postResult(result);						
					}
					return result;
				}
			};

		// 使用Java提供的FutureTask，执行Callable中的任务，同时可以获取任务执行结果
		mFuture = new FutureTask<Result>(mWorker) {
			@Override
			protected void done() {
					try {
					   // 调用FutureTask的get()方法获取Callable中的执行结果
					    postResultIfNotInvoked(get());			
					} catch (InterruptedException e) {
					    android.util.Log.w(LOG_TAG, e);
					} catch (ExecutionException e) {
					    throw new RuntimeException("An error occurred while executing doInBackground()",
						    e.getCause());
					} catch (CancellationException e) {
					    postResultIfNotInvoked(null);
					}
				}
			};
		}

		// 底层调用Handler将执行结果发送到主线程中
		private void postResultIfNotInvoked(Result result) {	
			// 默认值是false
			final boolean wasTaskInvoked = mTaskInvoked.get();	
			// 在Callable中设置成true，所以此处代码不执行
			if (!wasTaskInvoked) {								
			    postResult(result);
			}
		}

		private Result postResult(Result result) {
			@SuppressWarnings("unchecked")
			Message message = getHandler().obtainMessage(MESSAGE_POST_RESULT,
				new AsyncTaskResult<Result>(this, result));
			message.sendToTarget();
			return result;
		}

* 4、调用线程池执行FutureTask任务

		public final AsyncTask<Params, Progress, Result> executeOnExecutor(Executor exec,
		    Params... params) {
			if (mStatus != Status.PENDING) {
			    switch (mStatus) {
				case RUNNING:
				    throw new IllegalStateException("Cannot execute task:"
					    + " the task is already running.");
				case FINISHED:
				    throw new IllegalStateException("Cannot execute task:"
					    + " the task has already been executed "
					    + "(a task can be executed only once)");
			    }
			}
			mStatus = Status.RUNNING;
			onPreExecute();
			mWorker.mParams = params;
			// 执行FutureTask任务
			exec.execute(mFuture);			
			return this;
		}


* 5、底层Handler实现主线程方法回调，通过调用FutureTask取消任务

		private static class InternalHandler extends Handler {
			public InternalHandler(Looper looper) {
			    super(looper);
			}

			@Override
			public void handleMessage(Message msg) {
			    AsyncTaskResult<?> result = (AsyncTaskResult<?>) msg.obj;
			    switch (msg.what) {
				case MESSAGE_POST_RESULT:
				    // There is only one result
				    result.mTask.finish(result.mData[0]);
				    break;
				case MESSAGE_POST_PROGRESS:
				    result.mTask.onProgressUpdate(result.mData);
				    break;
			    }
			}
		}

		private void finish(Result result) {
			if (isCancelled()) {
			onCancelled(result);
			} else {
			onPostExecute(result);
			}
			mStatus = Status.FINISHED;
		}

		// 取消线程任务
		public final boolean cancel(boolean mayInterruptIfRunning) {
			mCancelled.set(true);
			return mFuture.cancel(mayInterruptIfRunning);
		}
