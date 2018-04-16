## Java多线程 ##

**进程&&线程概念**
	
 	* 进程：进程就是正在运行的程序，是系统进行资源分配和调用的独立单位。每一个进程都有它自己的内存空间和系统资源。
 	* 多进程有什么意义呢?
 	* 		多进程的作用不是提高执行速度，而是提高CPU的使用率。
 	* 		单核计算机：CPU多个进程间进行高效的切换，在任意一个时刻，只能有一个进程运行。

	* 线程： 线程是指进程中的一个执行流程，一个进程可以运行多个线程。线程是程序中单个顺序的控制流，是程序使用CPU的基本单位。
	* 线程是一个操作系统级别的概念。JAVA语言（包括其他编程语言）本身不创建线程；而是调用操作系统层提供的接口创建、控制、销毁线程实例。
	* 多线程有什么意义呢?
 	* 		多线程的作用不是提高执行速度，而是为了提高应用程序的使用率。 
 	* 		因为多个线程共享同一个进程的资源(堆内存和方法区)，但是栈内存是独立的，一个线程一个栈。
 	* 		所以他们仍然是在抢CPU的资源执行。一个时间点上只有能有一个线程执行。

	* 在同一个JVM进程中，有且只有一个进程，就是它自己。在这个JVM环境中，所有程序代码的运行都是以线程来运行。
	
	* 对于一个进程中的多个线程来说，多个线程共享进程的内存块，当有新的线程产生的时候，操作系统不分配新的内存，而是让新线程共享原有的进程块的内存。
	* 因此，线程间的通信很容易，速度也很快。不同的进程因为处于不同的内存块，因此进程之间的通信相对困难。
	* 进程主要是针对是在内存管理的虚拟对象，线程主要是针对CPU管理的虚拟对象
	* 
	* 操作系统的设计，因此可以归结为三点：

（1）以多进程形式，允许多个任务同时运行；

（2）以多线程形式，允许单个任务分成不同的部分运行；

（3）提供协调机制，一方面防止进程之间和线程之间产生冲突，另一方面允许进程之间和线程之间共享资源。

Java中线程会按优先级分配CPU时间片运行
https://blog.csdn.net/qq_33530388/article/details/62448212
https://blog.csdn.net/ziwen00/article/details/38097297
https://blog.csdn.net/nalanmingdian/article/details/77748326
	JVM调度 CPU调度



	2、JVM底层创建线程
	Java线程在Window下的实现是使用内核线程。
	内核线程：由操作系统内核创建和撤销，内核维护进程及线程的上下文信息以及线程的切换，一个内核线程由于I/O操作而阻塞，
			 不会影响其他线程的运行。
	用户线程：由应用进程利用线程库创建和管理，不依赖操作系统的核心，不需要用户态/内核态的切换，速度快，操作系统内核不知道

	线程的调度是JVM的一部分，在一个CPU的机器上上，实际上一次只能运行一个线程。一次只有一个线程栈执行。
	JVM线程调度程序决定实际运行哪个处于可运行状态的线程。
	众多可运行线程中的某一个会被选中做为当前线程。可运行线程被选择运行的顺序是没有保障的。


**1、线程生命周期**
		
	1、

    为了测试某个线程是否已经死亡，可以调用线程对象isAlive（）方法，当线程处于就绪、运行、阻塞三种状态时，该方法返回true；
	当线程处于新建、死亡两种状态，该方法将返回false。
	注意：不要试图对一个死亡的线程调用start方法使它重新启动，死亡就是死亡，该线程不可能再次作为线程执行。
	（如果重新启动的话，运行之后将引发IllegalThreadStateException异常，这表明处于死亡状态的线程无法再次运行了。）

**2、FutureTask&&Callable**

	1、通过实现 Callable 接口，创建线程任务，可以返回任务执行结果。
	2、调用 FutureTask 类，传递 Callable 任务，开启线程执行任务。
	3、在主线程中调用 FutureTask 类的 get() 获取任务执行完毕后的结果，但是会阻塞主线程。
	4、在 FutureTask 类中，线程执行完毕以及取消任务后，done()方法会被调用。
	   * 1、可以在done()方法中调用get()获取到任务执行结果，且不会阻塞当前线程，done()方法执行在工作线程中
	   * 2、调用 FutureTask 类 cancel() 方法后，会抛出 CancellationException 异常，可以在done()方法中捕获进行处理。此时done()方法执行在主线程中
	  
		MyCallable<String> callable = new MyCallable();
		MyFutureTask<String> task = new MyFutureTask<>(callable);
		new Thread(task).start();
		String result = task.get();  // 调用get()阻塞当前

	不同点：
    	* 1、实现Callable接口的任务线程能返回执行结果；而实现Runnable接口的任务线程不能返回结果
   		* 2、Callable接口的call()方法允许抛出异常；而Runnable接口的run()方法的异常只能在内部消化，不能继续上抛
		* 3、Callable接口支持返回执行结果，此时需要调用FutureTask.get()方法实现，此方法会阻塞当前直到获取结果
 
**3、ThreadPoolExecutor详细说明**

	1、ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler)
		* corePoolSize:核心线程数，能够同时执行的任务数量。
		  默认情况下核心线程会一直存活，即使处于闲置状态也不会受存keepAliveTime限制，除非将allowCoreThreadTimeOut设置为true。且keepAliveTime不为0
		* maximumPoolSize: 线程池中的最大线程数量，此数量包含核心线程数。
		* keepAliveTime: 
		  当线程池中的线程数量超过了corePoolSize时，它表示多余的空闲线程的存活时间，即：多余的空闲线程在超过keepAliveTime时间内没有任务的话则被销毁。
		* unit: 时间单位
		* workQueue: 任务队列，主要用来存储已经提交但未被执行的任务
		  阻塞等待线程的队列，一般使用new LinkedBlockingDeque<Runnable>()这个，如果不指定容量，会一直往里边添加，没有限制
		  1、LinkedBlockingQueue：无界的队列
			 线程会将超过核心线程的任务放在任务队列中排队。当没有大小限制时，线程池的最大线程数设置是无效的，线程数最多不会超过核心线程数。
		  2、SynchronousQueue：直接提交的队列
             线程池会创建新线程执行任务，这些任务也不会被放在任务队列中。	 
          3、DelayedWorkQueue：等待队列
		* threadFactory: 创建线程的工厂，使用系统默认的类
		* handler: 通常叫做拒绝策略，1、在线程池已经关闭的情况下 2、任务太多导致最大线程数和任务队列已经饱和，无法再接收新的任务
		  在上面两种情况下，只要满足其中一种时，在使用execute()来提交新的任务时将会拒绝，而默认的拒绝策略是抛一个RejectedExecutionException异常

	2、执行流程：
		* 1、先向corePool添加任务，当线程数小于corePoolSize时，每添加一个任务，则立即开启线程执行
	    * 2、当corePoolSize满的时候，后面添加的任务将放入缓冲队列workQueue等待；
		* 3、当workQueue也满的时候，看是否超过maximumPoolSize线程数，如果超过，默认拒绝执行。如果未超过，添加任务，创建线程。
	 	举例说明：
	    假如：corePoolSize=2，maximumPoolSize=3，workQueue容量为8;最开始，执行的任务A，B，此时corePoolSize已用完，再次执行任务C，则
	    C将被放入缓冲队列workQueue中等待着，如果后来又添加了7个任务，此时workQueue已满，则后面再来的任务将会和maximumPoolSize比较，
        由于maximumPoolSize为3，所以只能容纳1个了，因为有2个在corePoolSize中运行了，所以后面来的任务默认都会被拒绝。

		一个任务通过 execute(Runnable)方法被添加到线程池，任务就是一个Runnable类型的对象，任务的执行方法就是Runnable类型对象的run()方法。

	3、系统默认创建的线程池
		* 1、newFixedThreadPool()： 
		作用：
			该方法返回一个固定线程数量的线程池，该线程池中的线程数量始终不变，即不会再创建新的线程，也不会销毁已经创建好的线程，
			自始自终都是那几个固定的线程在工作，所以该线程池可以控制线程的最大并发数。 
		例子：
			假如有一个新任务提交时，线程池中如果有空闲的线程则立即使用空闲线程来处理任务，如果没有，则会把这个新任务存在一个任务队列中，
			一旦有线程空闲了，则按FIFO方式处理任务队列中的任务。
		* 2、newCachedThreadPool()： 
		作用：
			该方法返回一个可以根据实际情况调整线程池中线程的数量的线程池。即该线程池中的线程数量不确定，是根据实际情况动态调整的。 
		例子：
			假如该线程池中的所有线程都正在工作，而此时有新任务提交，那么将会创建新的线程去处理该任务，若之前有一些线程完成了任务，现在又有新任务提交，
			那么将不会创建新线程去处理，而是复用空闲的线程去处理新任务。此线程池设置空闲线程的空闲时间为60s。
		* 3、newSingleThreadExecutor()： 
		作用：该方法返回只有一个线程的线程池，即每次只能执行一个线程任务，多余的任务会保存到任务队列中，当这个线程空闲了再按FIFO方式顺序执行任务队列中的任务。
		
		* 1、newFixedThreadPool()—>LinkedBlockingQueue 
		* 2、newSingleThreadExecutor()—>LinkedBlockingQueue 
		* 3、newCachedThreadPool()—>SynchronousQueue 
		
		关闭方法：
		* 1、shutdown()方法在终止前允许执行以前提交的任务。 
		* 2、shutdownNow()方法则是阻止正在任务队列中等待任务的启动并试图停止当前正在执行的任务。

	4、当一个任务通过execute(Runnable)方法欲添加到线程池时

	    * 1、如果此时线程池中的数量小于corePoolSize，即使线程池中的线程都处于空闲状态，也要创建新的线程来处理被添加的任务。
	
	    * 2、如果此时线程池中的数量等于corePoolSize，但是缓冲队列workQueue未满，那么任务被放入缓冲队列。
	
	    * 3、如果此时线程池中的数量大于corePoolSize，缓冲队列workQueue满，并且线程池中的数量小于maximumPoolSize，建新的线程来处理被添加的任务。
	
	    * 4、如果此时线程池中的数量大于corePoolSize，缓冲队列workQueue满，并且线程池中的数量等于maximumPoolSize，将通过handler所指定的策略来处理此任务。
	         处理任务的优先级为：核心线程corePoolSize、任务队列workQueue、最大线程maximumPoolSize，如果三者都满了，使用handler处理被拒绝的任务。
	
	    * 5、当线程池中的线程数量大于corePoolSize时，如果某线程空闲时间超过keepAliveTime，线程将被终止。这样，线程池可以动态的调整池中的线程数。


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