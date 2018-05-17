## Java常见问题 ##

**常见问题**
	
	* 1、String、StringBuffer、StringBuilder的区别
	* 2、创建对象的方式
	* 3、如何判断两个对象是否相等
	* 4、序列化、反序列化操作
	* 5、创建线程的方式及线程池的运用
	* 6、内部类的作用及注意事项
	* 局部内部类、匿名内部类、成员内部类、静态内部类
	* 7、JVM运用原理及对象创建过程
	* 8、Java的GC机制
	* 9、引用类型及泛型的使用

**判断两个对象是否相等**
	
	1、hashCode():返回该对象的哈希码值。哈希值不是地址值，可以理解为地址值。
	2、equals():底层通过“==”判断对象是否相等，默认比较的是对象的地址值是否相同。
	3、重写equals()方法，必须重写hashCode()方法。Java规则相等的对象必须具有相等的散列码（hashCode）
	4、hashCode()重写规则，需考虑
	4、equal()与hashCode()使用
	   * 1、hashCode()相等，两个对象equals()方法可能相等，也可能不相等
	   * 2、hashCode()不相等，两个对象equals()方法一定不相等
	   * 3、equals()相等，两个对象的hashCode()方法一定相等
	   * 4、equals()不相等，两个对象的hashCode()方法可能相等，也可能不相等
	   
	注意：使用场景，当两个不同对象的某些属性值相同时就认为他们相同，所以重写equals()方法，未复写HashCode()方法
	   * 1、在使用HashSet集合过程中，hashCode()方法返回值不同，equals()返回true,这时HashSet会把这两个对象都存进去，这就和Set集合不重复的规则相悖
	   * 2、在使用HashMap集合过程中，若不复写hashCode()方法，则会导致两个同样的key值存入集合，与HashMap集合key不能重复相悖
	   * 3、在Java应用程序执行期间，在对同一对象多次调用hashCode方法时，必须一致地返回相同的整数，前提是将对象进行hashcode比较时所用的信息没有被修改。	
			List<Long> test1 = new ArrayList<Long>(); 
			test1.add(1L); 
			test1.add(2L); 
			System.out.println(test1.hashCode()); //994 
			test1.set(0,2L); 
			System.out.println(test1.hashCode()); //1025
	总结：如果重写equals而未重写hashcode方法，就会出现两个没有关系的对象equals相同（equals是根据对象的特征进行重写的），但hashcode确实不相同。 

**内部类为什么不能用静态方法**
		
	1、非static的内部类，在外部类加载的时候，并不会加载它，所以它里面不能有静态变量或者静态方法。
    2、static类型的属性和方法，在类加载的时候就会存在于内存中，要使用某个类的static属性或者方法，那么这个类必须要加载到jvm中。
    
	如果一个非static的内部类如果具有static的属性或者方法，那么就会出现一种情况：内部类未加载，但是却试图在内存中创建static的属性和方法，这当然是错误的。
    原因：类还不存在，但却希望操作它的属性和方法。
	
	如果内部类没有static的话，就需要实例化内部类才能调用，说明非static的内部类不是自动跟随主类加载的，而是被实例化的时候才会加载。
    而static的语义，就是主类能直接通过内部类名来访问内部类中的static方法，而非static的内部类又是不会自动加载的，所以这时候内部类也要static，否则会前后冲突。

**类加载器**

	1、一个Java类加载到JVM中会经过三个步骤，
	  * 装载：（查找和导入类或接口的二进制数据），找到class对应的字节码文件。
	  * 链接：（校验：检查导入类或接口的二进制数据正确性，准备：类的静态变量分配并初始化存储空间，解析：将符号引用转成直接引用），将对应字节码文件读入到JVM中。
	  * 初始化：（激活类的静态变量的初始化Java代码和静态Java代码块），对class做相应的初始化动作。

	2.Java中两种加载class到JVM中的方式
	  * 2.1：Class.forName("className");
	        其实这种方法调运的是：Class.forName(className, true, ClassLoader.getCallerClassLoader())方法
	        参数一：className，需要加载的类的名称。
	        参数二：true，是否对class进行初始化（需要initialize）
	        参数三：classLoader，对应的类加载器
	  * 2.2：ClassLoader.laodClass("className");
	        其实这种方法调运的是：ClassLoader.loadClass(name, false)方法
	        参数一：name,需要加载的类的名称
	        参数二：false，这个类加载以后是否需要去连接（不需要linking）
	  * 2.3:两种方式的区别
	        forName("")得到的class是已经初始化完成的
	        loadClass("")得到的class是还没有连接的
	        一般情况下，这两个方法效果一样，都能装载Class。
	        但如果程序依赖于Class是否被初始化，就必须用Class.forName(name)了。