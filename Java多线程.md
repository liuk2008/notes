## Java多线程 ##

**1、线程生命周期**

    为了测试某个线程是否已经死亡，可以调用线程对象isAlive（）方法，当线程处于就绪、运行、阻塞三种状态时，该方法返回true；
	当线程处于新建、死亡两种状态，该方法将返回false。
	注意：不要试图对一个死亡的线程调用start方法使它重新启动，死亡就是死亡，该线程不可能再次作为线程执行。
	（如果重新启动的话，运行之后将引发IllegalThreadStateException异常，这表明处于死亡状态的线程无法再次运行了。）

**2、scheduleAtFixedRate与scheduleWithFixedDelay区别**

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
	
	* 以上述代码为例：
	
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


