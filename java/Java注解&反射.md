## 注解&反射 ##

**自定义注解（掌握）**

	注解的概述
    * 在某个类上，或者某个方法，变量都可以使用注解，怎么编写。@注解名称（@Test）
    * 注解有什么用？
        * 注解给计算机看的，给JVM看的，注解可以决定类、方法怎么来执行。
        * 注解还可以去替换掉传统的配置文件的方式。
	
	1、自定义的注解的语法，属性要怎么声明？会看其他的注解使用。
	2、定义类使用class关键字，定义接口使用interface，定义自定义的注解，使用关键字 @interface，注解的名称。
		* 例子：public @interface Anno1{}	声明的注解没有任何含义。
		* 在某个方法或者类上使用，@Anno1	使用Anno1注解，没有任何含义。
	
	3、声明注解的属性
		* 语法：类型 变量名称 ();	如果属性没有指定默认值的话，那么你再使用注解的时候，必须要给属性赋值。
			* 注解中可以使用属性的类型，有哪些？
				* 基本数据类型
				* 字符串类型
				* Class类型
				* 注解类型
				* 枚举类型
				* 以上数据类型的一维数组
				
		* 声明属性的时候，可以指定属性的默认值
			* 类型 属性名称 () default 值;
			* 如果指定了默认值，属性就不用赋值了。

		* 如果注解中有属性的名称是value，那么在使用注解的时候，@Anno3(value=值)，那么value=就可以省略不写了
		* 但是如果同时存在value与其他属性，value= 必须存在
		
	4、声明注解的运行周期及使用范围，JDK提供的元注解
		* 注解默认存在的阶段，在编译后的阶段。但是咱们可以设置存放哪个阶段？
			* 使用JDK提供的元注解，Retention可以设置内容。
				* @Retention(RetentionPolicy.SOURCE) 1、自定义注解类只会在源码中出现，当源码编译成字节码时注解信息会被清除
 				* @Retention(RetentionPolicy.CLASS) 2、自定义注解类被编译在字节码中，当虚拟机加载该字节码时注解信息会被清除
 				* @Retention(RetentionPolicy.RUNTIME) 3、自定义注解类会被加载到虚拟机中
				
		* 使用JDK提供的元注解，Target可以设置制当前自定义注解类作用的范围
				* @Target		-- 规定注解编写在方法、变量、类上？

	5、总结
		* 会自定义注解	@interface 注解名称
		* 会在其他类中获取方法会使用 @注解名称
		* 会自定义注解的属性
			* 类型 属性名称 ();
			* 属性可以出现的类型：基本数据类，String，Class，注解，枚举和以上数据类型的一维数组
		* 属性也可以定义默认值
			* 类型 属性名称 () default 值;
		* 如果属性是名称是value，那么value就可以省略不写了	

**注解管理器**
	
	注解管理器（Annotation Processing Tool）
	注解处理器是（APT）是javac的一个工具，用来在编译时扫描和处理注解，一个注解处理器它以Java代码或者（编译过的字节码）作为输入，生成文件（通常是java文件）。
	这些生成的Java文件不能修改，并且会同其手动编写的Java代码一样会被javac编译。

	简单来说：就是把标记了注解的类、变量等作为输入内容，经过注解处理器处理，生成想要生成的java代码，强调的是编译时

	1、处理要素：注解处理器（AbstractProcess）+代码处理（javaPoet）+处理器注册（AutoService）
	2、处理过程：
		* 1、定义注解（lib-annotation）
		* 2、创建注解处理器（lib-processor）
			* 1、创建AnnotatedClass对象，根据注解元素所对应的类名生成，统一管理一个类下所有的注解元素值
				* 1、将注解元素添加到AnnotatedClass对象中统一管理
				* 2、获取注解元素上的值，进行赋值操作
				* 3、动态构建Java文件
			* 2、编写注解处理器（AbstractProcessor）
				* 1、照指定的规则对扫描到的Annotation进行处理
				* 2、创建AnnotationClass对象，统一管理注解元素
				* 3、调用AnnotationClass生成指定的源文件
		* 3、创建工具包（apt-library）
			* 1、通过反射调用注解管理器生成的Java源文件

**反射**

	1、反射的本质：获取类的class文件对象后，反向获取对象中的属性信息
	2、反射的核心：JVM在运行时才动态加载类或调用方法/访问属性，它不需要事先（写代码的时候或编译期）知道运行对象是谁。
	3、反射的作用：
		* 通过反射创建对象：
		* 1、通过Class对象的newInstance创建对应类的实例，无入参，newInstance()，调用对象的默认构造方法。
		* 2、通过Class对象获取指定的Constructor对像创建，入参是可变参数，newInstance(Object... args) 
		* 通过反射运行配置文件的内容
		* 通过反射越过泛型检查,泛型在编译时有效，在运行时会跳过检查
		* 动态代理，通过反射生成一个代理对象
		* 可以判断任意一个对象所属的类
	4、Java对象运行
		* 1、通过new关键字创建一个对象后，编译生成class文件
		* 2、类加载器将class文件加载到JVM内存中，生成Class对象
		* 3、Class对象是在加载类时由Java虚拟机以及通过调用类加载器中的defineClass方法自动构造的。一个类只产生一个Class对象
		* 4、jvm创建对象前，会先检查类是否加载，寻找类对应的class对象，若加载好，则为你的对象分配内存，初始化也就是代码:new Object()。
	5、获取字节码文件对象方式，注意每个方式下类的加载时机
		* 1、AdminDao.class
		* 2、adminDao.getClass()
        * 3、Class.forName("com.aerozhonghuan.java.reflect.AdminDaoImpl")  // 优先考虑
        * 4、ClassLoader.getSystemClassLoader().loadClass("com.aerozhonghuan.java.reflect.AdminDaoImpl")
	注意：
		1、使用反射相对来说不安全，破坏了类的封装性
		2、通过反射调用方法时，入参为可变参数，需注意
		3、包装类的Class对象：Integer.TYPE = int.class 
		4、修改final 基本类型与String类型常量时，在编译时其值已经被替换，所有通过反射修改不起作用
		5、通过反射创建内部类对象，调用内部类的方法  
