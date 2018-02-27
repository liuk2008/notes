# Activity的四种启动模式：

    1. standard
        默认启动模式，每次激活Activity时都会创建Activity，并放入任务栈中。

    2. singleTop
        如果在任务的栈顶正好存在该Activity的实例就重用该实例，否者就会创建新的实例并放入栈顶(即使栈中已经存在该Activity实例，只要不在栈顶，都会创建实例)。

    3. singleTask
        如果在栈中已经有该Activity的实例，就重用该实例(会调用实例的onNewIntent())。重用时会让该实例回到栈顶，因此在它上面的实例将会被移除栈。
        如果栈中不存在该实例，将会创建新的实例放入栈中。 

    4. singleInstance

        在一个新栈中创建该Activity实例，并让多个应用共享该栈中的该Activity实例。一旦该模式的Activity的实例存在于某个栈中，任何应用再激活该
        Activity时都会重用该栈中的实例，其效果相当于多个应用程序共享一个应用，不管谁激活该Activity都会进入同一个应用中。
   
    区别：
    1、singleTask：
    	Activity设置启动模式为singleTask，当在此Activity栈中时，会清除此Activity上面的其他Activity实例，而不会清除栈低存在的其他Activity，并且复用之前的Activity实例

    2、使用Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK标记：
    	1、必须同时设置这两个标记，否则没有效果
    	2、设置此标记时会清除栈内所有的Activity，并且会新开启一个任务栈重新生成一个Activity实例对象

    3、启动模式为singleTop或者为singleTast时，重用Activity时会调用onNewIntent方法
       当调用到onNewIntent(intent)的时候，需要在onNewIntent()中使用setIntent(intent)赋值给Activity的Intent.否则，后续的getIntent()都是得到老的Intent。





