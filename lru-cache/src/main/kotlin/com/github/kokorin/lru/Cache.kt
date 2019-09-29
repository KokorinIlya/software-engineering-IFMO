package com.github.kokorin.lru

/**
 * Cache, that stores non-null key-value pairs
 * @author Ilya Kokorin, kokorin.ilya.1998@yandex.ru
 */
interface Cache<K, V> : Iterable<Pair<K, V>> {
    /**
     * Returns value, associated with key, if key is present in cache.
     * Otherwise, returns null
     * @param key key to get associated value
     * @return Value, associated with the specified key, if key is present in cache, null otherwise.
     */
    fun get(key: K): V?

    /**
     * Associates specified value with specified key. If some other value was associated with
     * the specified key before, it is replaced by new value. If no value was associated with
     * the specified key, adds new key-value pair to the cache.
     * @param key key to associate value with
     * @param value value to associate with key
     * @return old value, that was associated with the specified key before. If no value
     * was associated with the specified key before insertion, null is returned
     */
    fun put(key: K, value: V): V?

    /**
     * If some value was associated with the specified key, deletes this key-value pair from cache,
     * returning deleted value. Otherwise, does not modify cache, returning null.
     * @param key key to delete associated value, if present.
     * @return deleted value, if key was presented in the cache, null otherwise.
     */
    fun delete(key: K): V?

    /**
     * Checks, if key is presented in cache.
     * @param key key to check presence of associated value
     * @return true, if some value is associated with the specified key, false otherwise
     */
    fun contains(key: K): Boolean

    /**
     * Returns number of key-value pairs in cache
     * @return current cache size
     */
    fun getCurrentSize(): Int

    /**
     * Checks, if cache contains zero key-value pairs
     * @return true, if cache size is zero, false otherwise
     */
    fun isEmpty(): Boolean = getCurrentSize() == 0

    /**
     * Checks, if cache contains at leas one key-value pair
     * @return true, if cache size is not equal to zero, false otherwise
     */
    fun nonEmpty(): Boolean = !isEmpty()

    /**
     * Gets iterator, that can be used to traverse cache content
     * @return iterator to traverse cache as a collection of key-value pairs
     */
    override fun iterator(): Iterator<Pair<K, V>>
}
