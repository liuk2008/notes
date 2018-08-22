
## AsyncTask分析 ##

**Android的类AsyncTask对线程间通讯进行了包装，提供了简易的编程方式来使后台线程和UI线程进行通讯，后台线程执行异步任务，并把操作结果通知UI线程。**

	* AsyncTask通过其execute方法启动执行，底层通过线程池来管理和调度进程中的所有Task的，通过重用原来创建的线程执行新的任务。

	1、通过execute()方法启动
	* execute 底层实际调用executeOnExecutor，使用AsyncTask默认创建SerialExecutor线程池执行任务，按先后顺序每次只运行一个，每个任务都是串行执行的。
	* 1、Executor SERIAL_EXECUTOR = new SerialExecutor(); 单任务运行
		* 底层使用双端队列，当有任务到来时，就把任务入队，然后在scheduleNext中出队一个任务，然后放到THREAD_POOL_EXECUTOR里去执行，
		* 每次只放一个任务，SerialExecutor的作用就是将任务排好队，然后放入THREAD_POOL_EXECUTOR线程池中去执行
	* 2、Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor();  多任务运行
		* 创建队列：最大等待任务数128个。BlockingQueue<Runnable> sPoolWorkQueue =new LinkedBlockingQueue<Runnable>(128)
		* 创建线程数： 核心线程数=CPU核数+1，最大线程数=CPU核数*2+1，此后重复使用已创建的线程
		* Executor THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
        * TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);
	
	2、通过executeOnExecutor()方法启动
	* 1、使用系统默认的线程池
		* task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)  执行并发任务
		* task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR)       执行串行任务 此线程池是AsyncTask内部默认使用
	* 2、使用Java线程池执行并发任务
		* Executors.newCachedThreadPool()：每个AsyncTask任务都单独起一个线程执行，也就是说所有的都是并发的。
		* Executors.newFixedThreadPool(3)：创建指定线程数量的线程池，并发数上限就是指定的线程数。但新任务产生，没有空闲的线程，就只能等待。
	* 3、使用自定义线程池

**AysncTask内部方法的使用**

	1、AsyncTask对象不可重复使用，也就是说一个AsyncTask对象只能execute()一次，否则会有异常抛出"java.lang.IllegalStateException
	2、各个方法掉调用时机
		* void onPreExecute() 加载前的初始化，在UI线程启动AsyncTask时，运行在UI线程。在工作线程启动AsyncTask时，运行在工作线程。
		* Result doInBackground(Params... params) 在onPreExecute方法执行后马上执行，在子线程运行
		* void onProgressUpdate(Progress... values) 在doInBackground调用publishProgress时触发,在UI线程运行
		* void onPostExecute(Result result) 在doInBackground 执行完成后，onPostExecute方法将被UI线程调用，result为doInBackground执行后的返回值
		* void onCancelled(Result result) 在主线程中调用cancel()的时候触发
		注意：
		* 当调用cancel()后，只是AsyncTask对象设置了一个标识位。此时doInBackground()和onProgressUpdate()还会继续执行，而doInBackground()执行完后
		* 将会调用onCancelled()，不再调用onPostExecute()。
		* 此外，我们可以在doInBackground()不断的检查 isCancelled()的返回值，当其返回true时就停止执行，特别是有循环的时候，从而停止任务的运行。
	3、AsyncTask的使用注意事项
		* 1、改善你的设计，少用异步处理。对于一般性的数据库查询，少量的I/O操作是没有必要启动线程的。
		* 2、与主线程有交互时用AsyncTask，否则就用Thread。
		* 3、当有需要大量线程执行任务时，一定要创建线程池。
		
**AysncTask内部实现机制**
