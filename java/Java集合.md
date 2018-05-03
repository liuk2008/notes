## Java集合 ##

**1、HashSet集合**
	
	1、HashSet底层是由HashMap实现
	

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
