package com.github.kokorin.lru

/**
 * Least Recently Used cache, that stores non-null key-value pairs.
 * Maximal cache size is specified, when the cache is created.
 * All the key-value pairs, presented in cache, are stored in the linked list, from newest (head) to
 * oldest (tail). Position in the list is called priority, e.g. head element has maximal priority, while
 * tail element has the lowest priority.
 * If the cache is full (current cache size is equal to maximal cache size) and new key-value pair needs to be
 * inserted, key-value pair with the lowest priority (that is stored in the tail element) is deleted.
 * @author Ilya Kokorin, kokorin.ilya.1998@yandex.ru
 */
abstract class AbstractLRUCache<K, V>(cacheSize: Int) : Cache<K, V> {
    protected data class ListNode<K, V>(val key: K, var value: V, var next: ListNode<K, V>?, var prev: ListNode<K, V>?)

    init {
        require(cacheSize > 0) { "Cache size must be > 0" }
    }

    /**
     * Maximal cache size
     */
    val cacheSize: Int = cacheSize

    protected var head: ListNode<K, V>? = null
    protected var tail: ListNode<K, V>? = null
    protected val cache: MutableMap<K, ListNode<K, V>> = mutableMapOf()

    /**
     * Returns value, associated with key, if key is present in cache.
     * Otherwise, returns null. If the specified key is presented in the cache, updates it's priority,
     * so it becomes the newest key in the cache.
     * @param key key to get associated value
     * @return Value, associated with the specified key, if key is present in cache, null otherwise.
     */
    override fun get(key: K): V? {
        val oldSize = size()

        val result = doGet(key)

        val newSize = size()

        assert(oldSize == newSize)
        assert(result == cache[key]?.value)
        assert(cache.containsKey(key) && head?.key == key || !cache.containsKey(key))
        assert((size() > 0) xor (head == null && tail == null))

        return result
    }

    protected abstract fun doGet(key: K): V?

    /**
     * Associates specified value with specified key. If some other value was associated with
     * the specified key before, it is replaced by new value. If no value was associated with
     * the specified key, adds new key-value pair to the cache. In both cases, the inserted key-value
     * pair becomes the newest in the cache.
     * Also, if new key needs to be inserted (this happens if no old value is associated
     * with specified key) and the cache is full, the oldest inserted key is deleted.
     * @param key key to associate value with
     * @param value value to associate with key
     * @return old value, that was associated with the specified key before. If no value
     * was associated with the specified key before insertion, null is returned
     */
    override fun put(key: K, value: V): V? {
        val oldSize = size()
        val wasPresented = cache.containsKey(key)
        val wasFull = isFull()

        val result = doPut(key, value)

        val newSize = size()

        assert(
            wasPresented && oldSize == newSize ||
                    !wasPresented && !wasFull && oldSize + 1 == newSize ||
                    !wasPresented && wasFull && oldSize == newSize
        )
        assert(cache[key]?.value == value)
        assert(head?.key == key && head?.value == value)
        assert(newSize >= 1)
        assert((size() > 0) xor (head == null && tail == null))

        return result
    }

    protected abstract fun doPut(key: K, value: V): V?

    /**
     * {@inheritDoc}
     */
    override fun delete(key: K): V? {
        val oldSize = size()
        val wasPresented = cache.containsKey(key)

        val result = doDelete(key)

        val newSize = size()
        val isPresented = cache.containsKey(key)

        assert(
            wasPresented && oldSize == newSize + 1
                    || !wasPresented && oldSize == newSize
        )
        assert(!isPresented)
        assert((size() > 0) xor (head == null && tail == null))

        return result
    }

    protected abstract fun doDelete(key: K): V?

    /**
     * Checks, if key is presented in cache. Note, that this method doesn't change the priority of the specified key.
     * @param key key to check presence of associated value
     * @return true, if some value is associated with the specified key, false otherwise
     */
    override fun contains(key: K): Boolean {
        return cache.contains(key)
    }

    /**
     * {@inheritDoc}
     */
    override fun size(): Int {
        val result = doSize()

        assert(result >= 0)

        return result
    }

    protected abstract fun doSize(): Int

    /**
     * Gets iterator, that can be used to traverse cache content from the newest to
     * the oldest key-value pair.
     * @return iterator to traverse cache as a collection of key-value pairs
     */
    override fun iterator(): Iterator<Pair<K, V>> {
        return object : Iterator<Pair<K, V>> {
            private var curPointer: ListNode<K, V>? = head

            override fun hasNext(): Boolean {
                return curPointer != null
            }

            override fun next(): Pair<K, V> {
                if (hasNext()) {
                    val result = Pair(curPointer!!.key, curPointer!!.value)
                    curPointer = curPointer!!.next
                    return result
                } else {
                    throw NoSuchElementException("Iterator doesn't have next element")
                }
            }
        }
    }

    /**
     * Checks, if cache is full (current cache size is equal to maximal cache size)
     * @return true, if size is equal to maximal cache size, false otherwise.
     * @see size
     * @see cacheSize
     */
    fun isFull() = size() == cacheSize

    /**
     * Checks, if cache is not full (current cache size is less than maximal cache size)
     * @return true, if size is less than maximal cache size, false otherwise.
     * @see size
     * @see cacheSize
     */
    fun nonFull() = !isFull()
}
