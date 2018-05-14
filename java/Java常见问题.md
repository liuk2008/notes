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
	
	1、通过equal()判断，底层实际判断对象的hashcode，也就是对象的内存地址
	2、使用HashSet与HashMap集合时，可以手动重写equal()与hashCode()方法
	3、equal()与hashCode()使用
	   * 1、equal()相等的两个对象他们的hashCode()肯定相等，也就是用equal()对比是绝对可靠的。
       * 2、hashCode()相等的两个对象他们的equal()不一定相等，也就是hashCode()不是绝对可靠的。
	   * 3、在使用集合的过程中，添加的对象如果复写了equal()方法，一定要复写hashCode()方法。

	注意：
	1、equals方法用于比较对象的内容是否相等（覆盖以后）
	2、hashcode方法只有在集合中用到
    3、当覆盖了equals方法时，比较对象是否相等将通过覆盖后的equals方法进行比较（判断对象的内容是否相等）。
    4、将对象放入到集合中时，首先判断要放入对象的hashcode值与集合中的任意一个元素的hashcode值是否相等，
    如果不相等直接将该对象放入集合中。如果hashcode值相等，然后再通过equals方法判断要放入对象与集合中的任意一个对象是否相等，
    如果equals判断不相等，直接将该元素放入到集合中，否则不放入。

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