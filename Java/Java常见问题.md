## Java常见问题 ##

**常见问题**
	
	1、String、StringBuffer、StringBuilder的区别
		String：String的值是不可变的，每次对String的操作都会生成新的String对象，可以空赋值
		StringBuffer：StringBuffer类的对象能够被多次的修改，并且不产生新的未使用对象，线程安全
		StringBuilder：StringBuffer类的对象能够被多次的修改，并且不产生新的未使用对象，线程不安全
	2、创建对象的方式
	3、如何判断两个对象是否相等
	4、序列化、反序列化操作
	5、创建线程的方式及线程池的运用
	6、内部类的作用及注意事项：局部内部类、匿名内部类、成员内部类、静态内部类
	7、JVM运用原理及对象创建过程
	8、Java的GC机制
	9、引用类型及泛型的使用


**判断两个对象是否相等**
	
* 1、hashCode():返回该对象的哈希码值。哈希值不是地址值，可以理解为地址值。
* 2、equals():底层通过“==”判断对象是否相等，默认比较的是对象的地址值是否相同。
* 3、重写equals()方法，必须重写hashCode()方法。Java规则相等的对象必须具有相等的散列码（hashCode）
* 4、hashCode()重写规则，需考虑
* 5、equal()与hashCode()使用
	* 1、hashCode()相等，两个对象equals()方法可能相等，也可能不相等
	* 2、hashCode()不相等，两个对象equals()方法一定不相等
	* 3、equals()相等，两个对象的hashCode()方法一定相等
	* 4、equals()不相等，两个对象的hashCode()方法可能相等，也可能不相等
* 6、默认情况下equals方法都是调用Object类的equals方法，而Object的equals方法主要用于判断对象的内存地址引用是不是同一个地址（是不是同一个对象），equals方法对于字符串来说是比较内容的，而对于非字符串来说是比较，其指向的对象是否相同的。

2 、要是类中覆盖了equals方法，那么就要根据具体的代码来确定equals方法的作用了，覆盖后一般都是通过对象的内容是否相等来判断对象是否相等。
* 注意：
	* 使用场景，当两个不同对象的某些属性值相同时就认为他们相同，所以重写equals()方法，未复写HashCode()方法
   	* 1、在使用HashSet集合过程中，hashCode()方法返回值不同，equals()返回true,这时HashSet会把这两个对象都存进去，这就和Set集合不重复的规则相悖
   	* 2、在使用HashMap集合过程中，若不复写hashCode()方法，则会导致两个同样的key值存入集合，与HashMap集合key不能重复相悖
   	* 3、在Java应用程序执行期间，在对同一对象多次调用hashCode方法时，必须一致地返回相同的整数，前提是将对象进行hashcode比较时所用的信息没有被修改。	
   
			List<Long> test1 = new ArrayList<Long>(); 
			test1.add(1L); 
			test1.add(2L); 
			System.out.println(test1.hashCode()); //994 
			test1.set(0,2L); 
			System.out.println(test1.hashCode()); //1025

* 总结：如果重写equals而未重写hashcode方法，就会出现两个没有关系的对象equals相同（equals是根据对象的特征进行重写的），但hashcode确实不相同。 


**内部类为什么不能用静态方法**
		
* 1、非static的内部类，在外部类加载的时候，并不会加载它，所以它里面不能有静态变量或者静态方法。

* 2、static类型的属性和方法，在类加载的时候就会存在于内存中，要使用某个类的static属性或者方法，那么这个类必须要加载到jvm中。

* 3、如果一个非static的内部类如果具有static的属性或者方法，那么就会出现：内部类未加载，但是却试图在内存中创建static的属性和方法，这当然是错误的。原因：类还不存在，但却希望操作它的属性和方法。

* 4、如果内部类没有static的话，就需要实例化内部类才能调用，说明非static的内部类不是自动跟随主类加载的，而是被实例化的时候才会加载。而static的语义，就是主类能直接通过内部类名来访问内部类中的static方法，而非static的内部类又是不会自动加载的，所以这时候内部类也要static，否则会前后冲突。


**类加载器**

* 1、一个Java类加载到JVM中会经过三个步骤：

  * 装载：（查找和导入类或接口的二进制数据），找到class对应的字节码文件。
  
  * 链接：（校验：检查导入类或接口的二进制数据正确性，准备：类的静态变量分配并初始化存储空间，解析：将符号引用转成直接引用），将对应字节码文件读入到JVM中。
  
  * 初始化：（激活类的静态变量的初始化Java代码和静态Java代码块），对class做相应的初始化动作。

* 2、Java中两种加载class到JVM中的方式

	* 1、Class.forName("className")：得到的class是已经初始化完成的
	* 2、ClassLoader.loadClass("className")：得到的class是还没有连接的
	* 区别：一般情况下，这两个方法效果一样，都能装载Class但如果程序依赖于Class是否被初始化，就必须用Class.forName(name)了


**引用类型**

* 1、强引用（StrongReference）

	* 只要某个对象有强引用与之关联，JVM必定不会回收这个对象，即使在内存不足的情况下，JVM宁愿抛出OutOfMemory错误也不会回收这种对象。比如：Person person = new Person，当 person=null 时，JVM在合适的时间就会回收该对象

* 2、软引用（SoftReference） 

	* 软引用是用来描述一些有用但并不是必需的对象，在Java中用SoftReference类来表示。对于软引用关联着的对象，只有在内存不足的时候JVM才会回收该对象。
	
   	* 软引用可以和一个引用队列（ReferenceQueue）联合使用，如果软引用所引用的对象被JVM回收，这个软引用对象就会被加入到与之关联的引用队列中。

* 3、弱引用（WeakReference）

    * 弱引用也是用来描述非必需对象的，当JVM进行垃圾回收时，无论内存是否充足，都会回收被弱引用关联的对象。

* 4、虚引用（PhantomReference）

    * 虚引用并不决定对象生命周期，如果一个对象只具有虚引用，那么它和没有任何引用一样，任何时候都可能被回收。虚引用主要用来跟踪对象被垃圾回收器回收的活动。与软引用和弱引用不同的是，虚引用必须关联一个引用队列。

* 5、引用队列（ReferenceQueue）

    * 当gc准备回收一个对象时，如果发现它还仅有软引用(弱引用、虚引用)指向它，就会在回收该对象之前，把这个软引用（弱引用、虚引用）加入到与之关联的引用队列（ReferenceQueue）中。如果一个软引用（弱引用、虚引用）对象本身在引用队列中，就说明该引用对象所指向的对象被回收了。

* 6、（WeakHashMap）

	* WeakHashMap里的entry可能会被GC自动删除，即使没有调用remove()或者clear()方法。底层的Entry继承WeakReference，HashMap和WeakHashMap的区别也在于此，HashMap的key是对实际对象的强引用。

* 当软引用（弱引用、虚引用）对象所指向的对象被回收了，那么这个引用对象本身就没有价值了，如果程序中存在大量的这类对象（注意，我们创建的软引用、弱引用、虚引用对象本身是个强引用，不会自动被gc回收），就会浪费内存。因此我们这就可以手动回收位于引用队列中的引用对象本身。


**序列化&反序列化**

* 对象序列化是将对象状态转换为可保持或传输的过程。一般的格式是与平台无关的二进制流，可以将这种二进制流持久保存在磁盘上，或通过网络传输到另一个网络结点。

* 1、为什么要实现序列化?
 	* 为了让对象在文件中持久存储，或者在网络中传输。
* 2、如何实现序列化?
	* 实现序列化接口Serializable
		 * NotSerializableException:未实现序列化接口异常。
		 * Serializable：类通过实现 java.io.Serializable 接口以启用其序列化功能。未实现此接口的类将无法使其任何状态序列化或反序列化。
		 * 如果以后你再看到接口没有方法的现象，请问该接口有什么用呢？有。这种接口被称为标记接口。 		
* 3、序列化数据后，再次修改类文件，读取数据会出问题，如何解决呢？
	 * InvalidClassException: 序列化反序列化失败报错
	    * 1、被序列化流操作的对象所属的类必须实现序列化接口。
	    * 2、一个java文件，在生存class文件的时候，也会有一个序列化id值，在序列化到文件的时候，它会把这个id值写到文件。
	  		Person.java		id=100
	  	    Person.class	id=100
	 		oos.txt			id=100
	     * 3、一但这个java文件做了修改，这个id值就会相应的发生改变
	  		Person.java		id=200
	 		Person.class 	id=200
	  		oos.txt			id=100
	 * 有些时候，我们做了一些简单的修改，不想在重新写数据，有没有办法保证读取数据不报错呢？有。让每次产生的id值是固定的就可以了。
* 4、使用transient关键字声明不需要序列化的成员变量，但在被反序列化后Transient变量的值被设为初始值，如int型的是0，对象型的是null。

* 注意：
	* 1、在Java中，只要一个类实现了java.io.Serializable接口，那么它就可以被序列化。
	* 2、通过ObjectOutputStream和ObjectInputStream对对象进行序列化及反序列化
	* 3、虚拟机是否允许反序列化，不仅取决于类路径和功能代码是否一致，一个非常重要的一点是两个类的序列化id是否一致
	* 4、序列化并不保存静态变量。
	* 5、要想将父类对象也序列化，就需要让父类也实现Serializable 接口。


**SOF与OOM**

* 1、SOF：StackOverFlowError
   * 当启动一个新的线程时虚拟机会为其分配一个栈空间，java栈以帧为单位保存线程运行状态，当线程调用一个方法时JVM会压入一个新的栈帧到这个线程的栈空间中，
   * 只要这个方法还没返回则这个栈帧就会一直存在。所以当方法嵌套调用层次太多，随着栈帧的增加导致总和大于JVM设置的-Xss值，就会抛出SOF异常	
* 2、OOM：OutOfMemoryError
   * 堆内存溢出
   * 栈内存溢出


**集合与数组之间的转换**

* 1、基本类型不能作为 Arrays.asList方法的参数，否则会被当做一个参数
* 2、Arrays.asList 返回的 List 不支持增删操作
* 3、使用Arrays.asLis的时候，对原始数组的修改会影响到我们获得的那个List

	// 分配一个长度与list的长度相等的字符串数组
    String[] array2 = (String[]) list.toArray(new String[list.size()]);

    // 将数组装换为list，直接使用Arrays的asList方法
    ArrayList<String> list = Arrays.asList(array);

	
**合理使用异常**

* 1、不要把异常定义为静态变量
* 2、生产环境不要使用e.printStackTrace()，因为它占用太多内存，造成锁死，并且，日志交错混合，也不易读。正确使用：log.error("异常日志正常打印方式",e)
* 3、如果是使用submit方法提交到线程池的异步任务，异常会被吞掉的，所以在日常发现中，如果会有可预见的异常，可以采取这几种方案处理：
	* 1、在任务代码try/catch捕获异常
	* 2、通过Future对象的get方法接收抛出的异常，再处理
	* 3、为工作者线程设置UncaughtExceptionHandler，在uncaughtException方法中处理异常
	* 4、重写ThreadPoolExecutor的afterExecute方法，处理传递的异常引用 
* 4、一个方法是不会出现两个异常的呢，所以finally的异常会把try的异常覆盖。正确的使用方式应该是，finally 代码块负责自己的异常捕获和处理。

		public void right() {
		    try {
		        log.info("try");
		        throw new RuntimeException("try");
		    } finally {
		        log.info("finally");
		        try {
		            throw new RuntimeException("finally");
		        } catch (Exception ex) {
		            log.error("finally", ex);
		        }
		    }
		}