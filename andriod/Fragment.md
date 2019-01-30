##Fragment##

**1、使用场景**
	
	* 当你有一个activity，想让这个activity根据事件响应可以对应不同的界面时，就可以创建几个fragment，将fragment绑定到该activity

**2、添加方式**

	动态添加Fragment主要分为4步：
	1、获取到FragmentManager，在V4包中通过getSupportFragmentManager，在系统中原生的Fragment是通过getFragmentManager获得的。
	2、开启一个事务，通过调用beginTransaction方法开启。
	3、向容器内加入Fragment，一般使用add或者replace方法实现，需要传入容器的id和Fragment的实例。
	4、提交事务，调用commit方法提交。 

	FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentTransaction.replace(Window.ID_ANDROID_CONTENT, fragment, tag);
    fragmentTransaction.commit();
	
	getFragmentManager()：所得到的是所在fragment的父容器的管理器
	getChildFragmentManager()：所得到的是在fragment里面子容器的管理器
	getFragmentManager到的是activity对所包含fragment的Manager，而如果是fragment嵌套fragment，那么就需要利用getChildFragmentManager()了。

	注意：常用Fragment可能会经常遇到这样Activity状态不一致：checkStateLoss这样的错误。主要是因为：commit方法一定要在Activity.onSaveInstance()之前调用。
		 只能在activity存储它的状态（当用户要离开activity时）之前调用commit()，如果在存储状态之后调用commit()，将会抛出一个异常。
		 这是因为当activity再次被恢复时commit之后的状态将丢失。如果丢失也没关系，那么使用commitAllowingStateLoss()方法。

**3、FragmentManager中add和replace的区别**

	1、add不会重新初始化fragment，replace每次都会。所以如果在fragment生命周期内获取获取数据,使用replace会重复获取。
	2、添加相同的fragment时，replace不会有任何变化，add会报IllegalStateException异常。
	3、replace会先清空父布局容器，再显示当前fragment，而add是覆盖前一个fragment。所以如果使用add一般会伴随hide()和show()，避免布局重叠。

**4、有关回滚——FragmentTransaction**

	1、使用回滚功能，
	   * 1、在commit()之前，使用addToBackStack()将其添加到回退栈中。
	   * 2、需要回退时，使用popBackStack()将最上层的操作弹出回退栈。
	2、popBackStack()是弹出默认的最上层的栈顶内容
       * 1、回滚是以提交的事务为单位进行的
       * 2、调用该方法后会将事物操作插入到FragmentManager的操作队列，只有当轮询到该事物时才能执行。
       * 3、popBackStack(String name, int flags)
		    * int flags有两个取值：0或FragmentManager.POP_BACK_STACK_INCLUSIVE；
		    * 当取值0时，表示除了参数一指定这一层之上的所有层都退出栈，指定的这一层为栈顶层； 
		    * 当取值POP_BACK_STACK_INCLUSIVE时，表示连着参数一指定的这一层一起退出栈
	   * 4、popBackStackImmediate() 调用该方法后会将事物操作插入到FragmentManager的操作队列，立即执行事物

**5、attach()和detach()区别**

	1、attach()：一方面利用fragment的onCreateView()来重建视图，一方面将此fragment添加到ADD队列中，由于是将fragment添加到ADD队列，
       所以只能添加到列队头部，所以attach()操作的结果是，最新操作的页面始终显示在最前面，调用fragment::isAdded()将返回True
	2、detach()：会将view与fragment分离，将此将view从viewtree中删除，而且将fragment从Activity的ADD队列中移除，所以在使用detach()后，
       使用fragment::isAdded()返回的值是false。但此fragment实例并不会删除，此fragment的状态依然保持着使用，所以在fragmentManager中仍
       然可以找到，即通过FragmentManager::findViewByTag()仍然是会有值的。 
	3、Activity的ADD队列，其实说是container的ADD队列更贴切些，因为一个Activity上面可以有多个Container来盛装Fragment实例组，每一个
       Container都会被分配一个ADD队列来记录当前通过add()方法，添加到这个container里的所有fragment实例。

**6、Activity与Fragment生命周期**
	
	1、首先是Activity被创建，紧接着fragment与Activity绑定(onAttach)，然后fragment被创建(onCreate)，fragment的视图被创建(onCreatView)，
    fragment所在的Activity的onCreate()方法已经被返回(onActivityCreated)，最后Actvity启动，fragment启动，Activity处于运行状态，fragment处于运行状态。
    2、退出的时候，首先是fragment暂停、Activity暂停，然后fragment停止、Activity停止，然后销毁fragment的视图(onDestoryView，与onCreatView相对应)、
    与之前绑定的Activity解除关联(onDetach，与onAttach相对应)，最后是Activity的销毁。

**7、Fragment之间参数传递 **

    1、Activity把值传递给Fragment
	   * 1、通过Bundle传递参数
	   * 2、是在宿主Activity中定义方法，将要传递的值传递到Fragment中，在Fragment中的onAttach方法中，获取到这个值。
	   * 3、在Activity中通过单例模式创建Fragment，通过方法入参传递参数
	2、Fragment把值传递给Activity
	   * 1、通过在Fragment内部定义回调接口，向Activity传值
	3、Fragment与Fragment之间的传值
       * 1、通过共同的Activity传递
       * 2、通过调用Fragment中的onActivityResult方法实现传值

**8、监听Fragment回退事件**


