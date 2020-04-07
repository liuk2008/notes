## Java线程池 ##

**1、ThreadPoolExecutor详细说明**

* 1、ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler)

	* corePoolSize:核心线程数，能够同时执行的任务数量。默认情况下核心线程会一直存活，即使处于闲置状态也不会受存keepAliveTime限制，除非将allowCoreThreadTimeOut设置为true。且keepAliveTime不为0
	
	* maximumPoolSize: 线程池中的最大线程数量，此数量包含核心线程数。
	
	* keepAliveTime: 线程池维护线程所允许的空闲时间。当线程池中的线程数量超过了corePoolSize时，它表示多余的空闲线程的存活时间，即：多余的空闲线程在超过keepAliveTime时间内没有任务的话则被销毁。
	
	* unit: 时间单位
	
	* workQueue: 任务队列，用来存储已经提交但未被执行的任务，线程会将超过核心线程的任务放在任务队列中排队。一般使用LinkedBlockingDeque
	
		* 1、LinkedBlockingQueue：无界的队列。当没有大小限制时，线程池的最大线程数设置是无效的，会一直往里边添加，没有限制。线程数最多不会超过核心线程数。
		* 2、SynchronousQueue：直接提交的队列。线程池会创建新线程执行任务，这些任务也不会被放在任务队列中。	 
		* 3、DelayedWorkQueue：等待队列

	* threadFactory: 创建线程的工厂，使用系统默认的类
	
	* handler: 通常叫做拒绝策略，1、在线程池已经关闭的情况下 2、任务太多导致最大线程数和任务队列已经饱和，无法再接收新的任务。在上面两种情况下，只要满足其中一种时，在使用execute()来提交新的任务时将会拒绝，而默认的拒绝策略是抛一个RejectedExecutionException异常	
	
		* AbortPolicy：直接抛出异常，这是默认策略
		* CallerRunsPolicy：用调用者所在的线程来执行任务
		* DiscardOldestPolicy：丢弃阻塞队列中靠最前的任务，并执行当前任务
		* DiscardPolicy：直接丢弃任务

* 2、执行流程：

	* 1、先向corePool添加任务，当线程数小于corePoolSize时，每添加一个任务，则立即开启线程执行
	
    * 2、当corePoolSize满的时候，后面添加的任务将放入缓冲队列workQueue等待
    
	* 3、当workQueue也满的时候，看是否超过maximumPoolSize线程数，如果超过，默认拒绝执行。如果未超过，添加任务，创建线程。
	
 	* 假如：corePoolSize=2，maximumPoolSize=3，workQueue容量为8;最开始，执行的任务A，B，此时corePoolSize已用完，再次执行任务C，则C将被放入缓冲队列workQueue中等待着，如果后来又添加了7个任务，此时workQueue已满，则后面再来的任务将会和maximumPoolSize比较，由于maximumPoolSize为3，所以只能容纳1个了，因为有2个在corePoolSize中运行了，所以后面来的任务默认都会被拒绝。
		
* 3、其他

	* 一个任务通过 execute(Runnable)方法被添加到线程池，任务就是一个Runnable类型的对象，任务的执行方法就是Runnable类型对象的run()方法。
	
	* 通常核心线程数可以设为CPU数量+1，而最大线程数可以设为CPU的数量*2+1	

	* 关闭方法：
		* 1、shutdown()方法在终止前允许执行以前提交的任务。 
		* 2、shutdownNow()方法则是阻止正在任务队列中等待任务的启动并试图停止当前正在执行的任务。

	* ExecutorService 提供了两种提交任务的方法：
		* execute()：提交不需要返回值的任务，参数是一个 Runnable，提交后无法判断该任务是否被线程池执行成功。
		* submit()：提交需要返回值的任务，有三种重载，参数可以是 Callable 也可以是 Runnable。同时它会返回一个 Funture 对象，通过它我们可以判断任务是否执行成功。获得执行结果调用 Future.get() 方法，这个方法会阻塞当前线程直到任务完成。


**2、系统默认线程池类型**

* 1、newFixedThreadPool()：new ThreadPoolExecutor(nThreads,nThreads,0L,TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());

	* 作用：
		该方法返回一个固定线程数量的线程池，该线程池中的线程数量始终不变，即不会再创建新的线程，也不会销毁已经创建好的线程，自始自终都是那几个固定的线程在工作，所以该线程池可以控制线程的最大并发数。 
	* 例子：
		假如有一个新任务提交时，线程池中如果有空闲的线程则立即使用空闲线程来处理任务，如果没有，则会把这个新任务存在一个任务队列中，一旦有线程空闲了，则按FIFO方式处理任务队列中的任务。

* 2、newCachedThreadPool()：new ThreadPoolExecutor(0, Integer.MAX_VALUE,60L,TimeUnit.SECONDS,new SynchronousQueue<Runnable>());
	
	* 作用：
		该方法返回一个可以根据实际情况调整线程池中线程的数量的线程池。即该线程池中的线程数量不确定，是根据实际情况动态调整的。 
	* 例子：
		假如该线程池中的所有线程都正在工作，而此时有新任务提交，那么将会创建新的线程去处理该任务，若之前有一些线程完成了任务，现在又有新任务提交，那么将不会创建新线程去处理，而是复用空闲的线程去处理新任务。此线程池设置空闲线程的空闲时间为60s。

* 3、newSingleThreadExecutor()：new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>())
	
	* 作用：该方法返回只有一个线程的线程池，即每次只能执行一个线程任务，多余的任务会保存到任务队列中，当这个线程空闲了再按FIFO方式顺序执行任务队列中的任务。
	

**3、corePoolSize、maximumPoolSize、workQueue三者关系**

* 1、如果此时线程池中的数量小于corePoolSize，即使线程池中的线程都处于空闲状态，也要创建新的线程来处理被添加的任务。

* 2、如果此时线程池中的数量等于corePoolSize，但是缓冲队列workQueue未满，那么任务被放入缓冲队列。

* 3、如果此时线程池中的数量大于corePoolSize，缓冲队列workQueue满，并且线程池中的数量小于maximumPoolSize，建新的线程来处理被添加的任务。

* 4、如果此时线程池中的数量大于corePoolSize，缓冲队列workQueue满，并且线程池中的数量等于maximumPoolSize，将通过handler所指定的策略来处理此任务。

* 5、当线程池中的线程数量大于corePoolSize时，如果某线程空闲时间超过keepAliveTime，线程将被终止。这样，线程池可以动态的调整池中的线程数。

* 处理任务的优先级为：核心线程corePoolSize、任务队列workQueue、最大线程maximumPoolSize，如果三者都满了，使用handler处理被拒绝的任务。


**4、scheduleAtFixedRate与scheduleWithFixedDelay区别**

	// 1、创建线程池
	ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(1);
	// 2、创建任务
    Task task = new Task();
	// 3、开启定时任务
    threadPoolExecutor.scheduleAtFixedRate(task, 2, 2, TimeUnit.SECONDS);

	// 任务内容
	class Task implements Runnable {
        @Override
        public void run() {
            LogUtils.logd(TAG, LogUtils.getThreadName() + "====begin=====");
            SystemClock.sleep(5000);
            LogUtils.logd(TAG, LogUtils.getThreadName() + "====end=====");
        }
    }
	
	以上述代码为例：
	
	1、scheduleAtFixedRate(Runnable command,long initialDelay,long period,TimeUnit unit)	
	* 1、initialDelay：在initialDelay时间后，首次执行任务
	* 2、period：从任务首次开始执行完成时，period为一个周期，开始执行任务
		* 当任务执行周期时长 > period周期时：当前任务执行完毕后，立即执行下一次任务
		*   修改参数为：threadPoolExecutor.scheduleAtFixedRate(task, 2, 2, TimeUnit.SECONDS)
		*   修改参数为：SystemClock.sleep(5000);
			03-15 11:21:49.528 Pid:25291,main-> onResume() 
			03-15 11:21:51.529 Pid:25291,pool-6-thread-1-> run() ====begin=====
			03-15 11:21:56.529 Pid:25291,pool-6-thread-1-> run() ====end=====
			03-15 11:21:56.530 Pid:25291,pool-6-thread-1-> run() ====begin=====
			03-15 11:22:01.530 Pid:25291,pool-6-thread-1-> run() ====end=====
		* 当任务执行周期时长 < period周期时：当前任务执行完毕后，等待时间为：period周期-任务执行周期，然后开始下一次任务
		* 	修改参数为：threadPoolExecutor.scheduleAtFixedRate(task, 5, 5, TimeUnit.SECONDS)
		*   修改参数为：SystemClock.sleep(3000);
			03-15 11:27:56.599 Pid:30732,main-> onResume()
			03-15 11:28:01.601 Pid:30732,pool-7-thread-1-> run() ====begin=====
			03-15 11:28:04.602 Pid:30732,pool-7-thread-1-> run() ====end=====
			03-15 11:28:06.601 Pid:30732,pool-7-thread-1-> run() ====begin=====
			03-15 11:28:09.602 Pid:30732,pool-7-thread-1-> run() ====end===== 
	
	2、scheduleWithFixedDelay(Runnable command,long initialDelay,long delay,TimeUnit unit)
	* 1、initialDelay：在initialDelay时间后，首次执行任务
	* 2、period：从任务首次开始执行完成时，period为一个周期，开始执行任务
		* 当任务执行周期时长 > period周期时：当前任务执行完毕后，等待时间为：period周期，然后开始下一次任务
		*   修改参数为：threadPoolExecutor.scheduleWithFixedDelay(task, 2, 2, TimeUnit.SECONDS)
		*   修改参数为：SystemClock.sleep(5000);
			03-15 13:52:55.083 Pid:3833,main-> onResume() 
			03-15 13:52:57.085 Pid:3833,pool-7-thread-1-> run() ====begin=====
			03-15 13:53:02.086 Pid:3833,pool-7-thread-1-> run() ====end=====
			03-15 13:53:04.086 Pid:3833,pool-7-thread-1-> run() ====begin=====
			03-15 13:53:09.088 Pid:3833,pool-7-thread-1-> run() ====end=====
		* 当任务执行周期时长 < period周期时：当前任务执行完毕后，等待时间为：period周期，然后开始下一次任务
		* 	修改参数为：threadPoolExecutor.scheduleWithFixedDelay(task, 5, 5, TimeUnit.SECONDS)
		*   修改参数为：SystemClock.sleep(3000);
			03-15 13:48:03.578 Pid:31542,main-> onResume() 
			03-15 13:48:08.579 Pid:31542,pool-6-thread-1-> run() ====begin=====
			03-15 13:48:11.579 Pid:31542,pool-6-thread-1-> run() ====end=====
			03-15 13:48:16.581 Pid:31542,pool-6-thread-1-> run() ====begin=====
			03-15 13:48:19.582 Pid:31542,pool-6-thread-1-> run() ====end=====