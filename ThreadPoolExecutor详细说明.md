
# ThreadPoolExecutor详细说明

  * ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler)

	corePoolSize: 
    核心线程数，能够同时执行的任务数量。默认情况下核心线程会一直存活，即使处于闲置状态也不会受存keepAliveTime限制。除非将allowCoreThreadTimeOut设置为true。

	maximumPoolSize：
    除去缓冲队列中等待的任务，最大能容纳的任务数。线程池所能容纳的最大线程数。超过这个数的线程将被阻塞。当任务队列为没有设置大小的LinkedBlockingDeque时，这个值无效。

	keepAliveTime：超出workQueue的等待任务的存活时间。非核心线程的闲置超时时间，超过这个时间就会被回收。

	unit：时间单位。当将allowCoreThreadTimeOut设置为true时对corePoolSize生效。

	workQueue:线程池中的任务队列.常用的有三种队列，SynchronousQueue,LinkedBlockingDeque,ArrayBlockingQueue。
    阻塞等待线程的队列，一般使用new LinkedBlockingDeque<Runnable>()这个，如果不指定容量，会一直往里边添加，没有限制

	threadFactory：创建线程的工厂，使用系统默认的类

	handler：当任务数超过maximumPoolSize时，对任务的处理策略，默认策略是拒绝添加

  * 执行流程：当线程数小于corePoolSize时，每添加一个任务，则立即开启线程执行
 
    当corePoolSize满的时候，后面添加的任务将放入缓冲队列workQueue等待；
	当workQueue也满的时候，看是否超过maximumPoolSize线程数，如果超过，默认拒绝执行

  * 举例说明：

    假如：corePoolSize=2，maximumPoolSize=3，workQueue容量为8;最开始，执行的任务A，B，此时corePoolSize已用完，再次执行任务C，则
    C将被放入缓冲队列workQueue中等待着，如果后来又添加了7个任务，此时workQueue已满，则后面再来的任务将会和maximumPoolSize比较，
    由于maximumPoolSize为3，所以只能容纳1个了，因为有2个在corePoolSize中运行了，所以后面来的任务默认都会被拒绝。

   一个任务通过 execute(Runnable)方法被添加到线程池，任务就是一个 Runnable类型的对象，任务的执行方法就是Runnable类型对象的run()方法。

  * 当一个任务通过execute(Runnable)方法欲添加到线程池时：

    如果此时线程池中的数量小于corePoolSize，即使线程池中的线程都处于空闲状态，也要创建新的线程来处理被添加的任务。

    如果此时线程池中的数量等于corePoolSize，但是缓冲队列 workQueue未满，那么任务被放入缓冲队列。

    如果此时线程池中的数量大于corePoolSize，缓冲队列workQueue满，并且线程池中的数量小于maximumPoolSize，建新的线程来处理被添加的任务。

    如果此时线程池中的数量大于corePoolSize，缓冲队列workQueue满，并且线程池中的数量等于maximumPoolSize，那么通过handler所指定的策略
    来处理此任务。也就是：处理任务的优先级为：核心线程corePoolSize、任务队列workQueue、最大线程maximumPoolSize，如果三者都满了，使用handler处理被拒绝的任务。

    当线程池中的线程数量大于corePoolSize时，如果某线程空闲时间超过keepAliveTime，线程将被终止。这样，线程池可以动态的调整池中的线程数。