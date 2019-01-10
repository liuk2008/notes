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

**3、FragmentManager中add和replace的区别**

	1、add不会重新初始化fragment，replace每次都会。所以如果在fragment生命周期内获取获取数据,使用replace会重复获取。
	2、添加相同的fragment时，replace不会有任何变化，add会报IllegalStateException异常。
	3、replace会先清空父布局容器，再显示当前fragment，而add是覆盖前一个fragment。所以如果使用add一般会伴随hide()和show()，避免布局重叠。
	4、使用add，如果应用放在后台，或以其他方式被系统销毁，再打开时，hide()中引用的fragment会销毁，所以依然会出现布局重叠bug，可以使用replace或使用add时，添加一个tag参数。

**3、组件单独调试**
	
	首先是Activity被创建，紧接着fragment与Activity绑定(onAttach)，然后fragment被创建(onCreat)，
fragment的视图被创建(onCreatView)，fragment所在的Activity的onCreate()方法已经被返回(onActivityCreated)，
最后Actvity启动，fragment启动，Activity处于运行状态，fragment处于运行状态。

可以看到退出的时候，首先是fragment暂停、Activity暂停，然后fragment停止、Activity停止，
然后销毁fragment的视图(onDestoryView，与onCreatView相对应)、与之前绑定的Activity解除关联(onDetach，与onAttach相对应)，最后是Activity的销毁。