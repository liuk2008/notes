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
		 A-->index=0-->Entry[0]=A,A.next=null
		 B-->index=0-->Entry[0]=B,B.next=A,A.next=null
		 C-->index=0-->Entry[0]=C,C.next=B,B.next=A,A.next=null

	* 2、HashMap底层调用put()方法添加元素时，会判断对象元素的hashCode是否相等，及调用对象的equals()方法
	
		// put()方法源码
		public V put(K key, V value) {
	        if (key == null) {
	            return putValueForNullKey(value);      	// null key总是存放在Entry[]数组的第一个元素。
	        }
	
	        int hash = Collections.secondaryHash(key); 	// 获取Key的hash值
	        HashMapEntry<K, V>[] tab = table;           // HashMapEntry数组
	        int index = hash & (tab.length - 1);       	// 计算元素的素组索引值
			// 判断是否存在相同key值，替换value值
	        for (HashMapEntry<K, V> e = tab[index]; e != null; e = e.next) {
	            if (e.hash == hash && key.equals(e.key)) { // 调用元素的hashCode()、equals()方法
	                preModify(e);
	                V oldValue = e.value;
	                e.value = value;  					// 替换Value值，并返回旧Value值
	                return oldValue;
	            }
	        }
	
	        modCount++;
	        if (size++ > threshold) {
	            tab = doubleCapacity();
	            index = hash & (tab.length - 1);
	        }
	        addNewEntry(key, value, hash, index);		// 创建Entry对象，关联Entry节点
	        return null;
		}

		// addNewEntry()源码
		void addNewEntry(K key, V value, int hash, int index) {
			// 创建对应索引的数组元素，HashMapEntry对象
        	table[index] = new HashMapEntry<K, V>(key, value, hash, table[index]);
		}

		// HashMapEntry构造函数，创建Entry节点值
		HashMapEntry(K key, V value, int hash, HashMapEntry<K, V> next) {
            this.key = key;
            this.value = value;
            this.hash = hash;
            this.next = next;
        }

	* 3、HashMap底层通过entrySet()、keySet()、valueSet()获取对应的值，底层返回EntrySet、KeySet、ValueSet集合，
	* 这三个继承于AbstractSet集合，底层实现iterator()方法，返回自定义迭代器EntryIterator对象
		
		public Set<Entry<K, V>> entrySet() {
	        Set<Entry<K, V>> es = entrySet;
	        return (es != null) ? es : (entrySet = new EntrySet()); // 返回EntrySet集合
	    }
		
		// EntrySet类
		private final class EntrySet extends AbstractSet<Entry<K, V>> {
	        public Iterator<Entry<K, V>> iterator() {
	            return newEntryIterator();							// 返回自定义迭代器
	        }
	        public boolean contains(Object o) {
	            if (!(o instanceof Entry))
	                return false;
	            Entry<?, ?> e = (Entry<?, ?>) o;
	            return containsMapping(e.getKey(), e.getValue());
	        }
	        public boolean remove(Object o) {
	            if (!(o instanceof Entry))
	                return false;
	            Entry<?, ?> e = (Entry<?, ?>)o;
	            return removeMapping(e.getKey(), e.getValue());
	        }
	        public int size() {
	            return size;
	        }
	        public boolean isEmpty() {
	            return size == 0;
	        }
	        public void clear() {
	            HashMap.this.clear();
	        }
	    }

	* 4、HashMap底层EntryIterator对象继承于HashIterator，HashIterator类底层通过HashMapEntry与Entry[]数组结合使用，返回对应的Key值和Value值
	
		private final class EntryIterator extends HashIterator
	            implements Iterator<Entry<K, V>> {
	        public Entry<K, V> next() { return nextEntry(); }
	    }
		
		private abstract class HashIterator {
	        int nextIndex; // 下一个元素索引	

			// entryForNullKey 默认为null值，当存入null键值，entryForNullKey不为空
	        HashMapEntry<K, V> nextEntry = entryForNullKey; // 下一个数组元素

	        HashMapEntry<K, V> lastEntryReturned; // 上一个数组元素
	        int expectedModCount = modCount;
	
	        HashIterator() {
	            if (nextEntry == null) {
	                HashMapEntry<K, V>[] tab = table;
	                HashMapEntry<K, V> next = null;
					// 这里利用了index的初始值为0，从0开始依次向后遍历，直到找到不为null的元素就退出循环。
					// 注意：此处只遍历了Entry[]数组，未遍历链表
	                while (next == null && nextIndex < tab.length) {
	                    next = tab[nextIndex++];  // 返回数组上的Entry对象
	                }
	                nextEntry = next;  
	            }
	        }
	
	        public boolean hasNext() {
	            return nextEntry != null;
	        }	

			// 遍历Entry[]数组上Entry元素挂载的单向链表
	        HashMapEntry<K, V> nextEntry() {
	            if (modCount != expectedModCount)
	                throw new ConcurrentModificationException();
	            if (nextEntry == null)
	                throw new NoSuchElementException();
	
	            HashMapEntry<K, V> entryToReturn = nextEntry; // 
	            HashMapEntry<K, V>[] tab = table;
	            HashMapEntry<K, V> next = entryToReturn.next; // 获取指向下一个节点的Entry对象
				
	            while (next == null && nextIndex < tab.length) { // 当指向下一个节点的Entry对象为空，则继续遍历数组上的元素
	                next = tab[nextIndex++];
	            }
	            nextEntry = next; // 记录下一个元素的值
	            return lastEntryReturned = entryToReturn; // 返回当前Entry对象
	        }
	
	        public void remove() {
	            if (lastEntryReturned == null)
	                throw new IllegalStateException();
	            if (modCount != expectedModCount)
	                throw new ConcurrentModificationException();
	            HashMap.this.remove(lastEntryReturned.key);
	            lastEntryReturned = null;
	            expectedModCount = modCount;
	        }
		}
	
	
	* 注意：
	* 1、当数组元素指向的链表增长时，HashMap底层会自动扩容保证查询速度。
	* 当哈希表的容量超过默认容量时，必须调整table的大小。
	* 当容量已经达到最大可能值时，那么该方法就将容量调整到Integer.MAX_VALUE返回，这时，需要创建一张新表，将原表映射到新表中。
	* 2、HashMap允许空键和空值的存在，会排在集合第一位
	* 3、通过KeySet、ValueSet、EntrySet集合获取的数据，实际是让Set集合持有HashIterator迭代器对象，从而访问对应元素，但是Set集合底层未存储任何元素

	HashTable与HashMap的区别
	* 1、HashTable线程安全，因为底层方法添加了synchronize关键字，适用于多线程。Hashmap线程不安全，适用于单线程。
	* 2、HashTable不允许null键、null值，HashMap允许null键和null值
	* 3、HashMap是“从前向后”的遍历数组；再对数组具体某一项对应的链表，从表头开始进行遍历。
	* Hashtabl是“从后往前”的遍历数组；再对数组具体某一项对应的链表，从表头开始进行遍历。
	* 


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
