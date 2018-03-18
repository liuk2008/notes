# Java引用类型

1、强引用（StrongReference） JVM停止运行时停止

    只要某个对象有强引用与之关联，JVM必定不会回收这个对象，即使在内存不足的情况下，JVM宁愿抛出OutOfMemory错误也不会回收这种对象。比如：Person person = new Person
    当 person=null 时，JVM在合适的时间就会回收该对象

2、软引用（SoftReference） 内存不足时终止，

    软引用是用来描述一些有用但并不是必需的对象，在Java中用java.lang.ref.SoftReference类来表示。对于软引用关联着的对象，只有在内存不足的时候JVM才会回收该对象。
    因此，这一点可以很好地用来解决OOM的问题，并且这个特性很适合用来实现缓存：比如网页缓存、图片缓存等。
    软引用可以和一个引用队列（ReferenceQueue）联合使用，如果软引用所引用的对象被JVM回收，这个软引用就会被加入到与之关联的引用队列中。
    软引用构建敏感数据的缓存
  
3、弱引用（WeakReference） gc运行后终止
   
    弱引用也是用来描述非必需对象的，当JVM进行垃圾回收时，无论内存是否充足，都会回收被弱引用关联的对象。在java中，用java.lang.ref.WeakReference类来表示。
    弱引用解决内存泄露问题

4、虚引用（PhantomReference） gc运行后终止

    虚引用并不决定对象生命周期，如果一个对象只具有虚引用，那么它和没有任何引用一样，任何时候都可能被回收。虚引用主要用来跟踪对象被垃圾回收器回收的活动。
    与软引用和弱引用不同的是，虚引用必须关联一个引用队列。

https://www.cnblogs.com/dolphin0520/p/3784171.html


