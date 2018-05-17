## Java集合 ##

**1、HashSet、HashMap、HashTable**
	
	散列表（Hash table，也叫哈希表），是根据关键码值(Key value)而直接进行访问的数据结构。也就是说，它通过把关键码值映射到表中一个位置来访问记录，
	以加快查找的速度。这个映射函数叫做散列函数，存放记录的数组叫做散列表。
	注意：当集合里面的对象属性被修改后，再调用remove()方法时不起作用。
	
	HashSet集合
	* HashSet-->AbstractSet-->AbstractCollection
	* 1、HashSet底层是通过HashMap实现的，Key值为add进去的元素，Value值为当前HashSet对象
	* 2、AbstractCollection重写了toString()方法，底层通过调用Iterator迭代器遍历元素
	* 3、可以让Set集合持有其他集合的Iterator迭代器，而访问其他集合元素
		步骤：	
		1、创建一个ArrayList集合，添加元素
		2、创建一个AbstractSet的实现类，并将ArrayList中的迭代器Iterator传入
		3、在AbstractSet实现类中重写iterator()方法，底层创建一个Iterator对象并返回。
		4、重写Iterator对象的next()、hasNext()等方法，底层实际上调用传入的迭代器Iterator的next()、hasNext()方法
		
		注意：
		1、此过程实际上让Set集合持有其他集合（源集合）的Iterator，但是Set集合底层未添加任何元素	
		2、当源集合元素出现变动时，Set集合也会出现变动。比如，ArrayList添加了一个重复元素，此时的Set集合也会出现重复元素

	HashMap集合
	* 1、底层是hash表结构，实现方式是数组+链表，数组、链表类型为Entry型，Entry主要存储Key值和Value值，及下一个节点值next
	* 存储的思想都是：通过table数组存储，数组的每一个元素都是一个Entry；而一个Entry就是一个单向链表，Entry链表中的每一个节点就保存了key-value键值对数据。
	     1、底层将Key值通过哈希函数转换对应的数组下标，并将Key值和Value值存入Entry对象中
		 2、当多个元素经过转换后数组下标相同时，底层结构将在对应数组元素上挂载链表结构，Entry类里面有一个next属性，作用是指向下一个Entry
		 A-->index=0-->Entry[0]=A,A.next=A
		 B-->index=0-->Entry[0]=B,B.next=A,A.next=A
		 C-->index=0-->Entry[0]=C,C.next=B,B.next=A,A.next=A

	* 2、HashMap底层调用put()方法添加元素时，会判断对象元素的hashCode是否相等，及调用对象的equals()方法
	
		int hash = Collections.secondaryHash(key); 	// 获取Key的hashcode
		HashMapEntry<K, V>[] tab = table; 			// HashMapEntry数组
		int index = hash & (tab.length - 1);   		// 计算元素的素组索引值
		for (HashMapEntry<K, V> e = tab[index]; e != null; e = e.next) { // 判断是否存在相同key值，替换value值
		    if (e.hash == hash && key.equals(e.key)) {
		        preModify(e);
		        V oldValue = e.value;
		        e.value = value;
		        return oldValue;
		    }
		}
		
		modCount++;
		if (size++ > threshold) {
		    tab = doubleCapacity();
		    index = hash & (tab.length - 1);
		}
		addNewEntry(key, value, hash, index);  // 创建Entry对象，关联Entry节点

		// 
		void addNewEntry(K key, V value, int hash, int index) {
        	table[index] = new HashMapEntry<K, V>(key, value, hash, table[index]);
		}

		// 
		HashMapEntry(K key, V value, int hash, HashMapEntry<K, V> next) {
            this.key = key;
            this.value = value;
            this.hash = hash;
            this.next = next;
        }

	* 注意：
	* 1、当数组元素指向的链表增长时，HashMap底层会自动扩容保证查询速度。
	* 2、HashMap允许空值、空键，但是
	

**2、ArrayList底层迭代器（ArrayListIterator）实现**

	 private class ArrayListIterator implements Iterator<E> {

		// 记录未遍历的元素个数
        private int remaining = size;
		// 数组中元素的索引值
        private int removalIndex = -1;
		// 未遍历的元素个数为0时，返回false，及遍历完集合中所有元素
        public boolean hasNext() {
            return remaining != 0;
        }

        @SuppressWarnings("unchecked") public E next() {
            ArrayList<E> ourList = ArrayList.this;
            int rem = remaining; // 未遍历时，剩余元素个数
            if (ourList.modCount != expectedModCount) { // 遍历元素时，又同时修改集合元素，则抛出异常
                throw new ConcurrentModificationException();
            }
            if (rem == 0) { // 集合已经遍历完，还继续遍历则抛出异常
                throw new NoSuchElementException();
            }
            remaining = rem - 1; // 遍历后，剩余元素个数
            return (E) ourList.array[removalIndex = ourList.size - rem];
        }

    }
