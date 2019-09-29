package com.github.kokorin.lru

class LRUCacheImpl<K, V> internal constructor (cacheSize: Int) : LRUCache<K, V>(cacheSize) {
    private fun deleteNodeFromLinkedList(node: ListNode<K, V>) {
        val prevNode = node.prev
        val nextNode = node.next

        if (prevNode != null) {
            assert(prevNode.next == node)
            prevNode.next = nextNode
        }
        if (nextNode != null) {
            assert(nextNode.prev == node)
            nextNode.prev = prevNode
        }
        if (node == head) {
            head = node.next
        }
        if (node == tail) {
            tail = node.prev
        }
    }

    private fun pushNodeToLinkedList(node: ListNode<K, V>) {
        assert(node.next == head)
        assert(node.prev == null)
        if (head != null) {
            head!!.prev = node
        } else {
            assert(size() == 0)
            assert(tail == null)
            tail = node
        }
        head = node
    }

    private fun checkElementsPresence() {
        assert(head != null)
        assert(tail != null)
        assert(size() > 0)
    }

    override fun doGet(key: K): V? {
        val resultNode = cache[key] ?: return null
        // Otherwise, at least one element is presented
        checkElementsPresence()

        deleteNodeFromLinkedList(resultNode)
        resultNode.prev = null
        resultNode.next = head
        pushNodeToLinkedList(resultNode)
        return resultNode.value
    }

    override fun doPut(key: K, value: V): V? {
        val maybeResultNode = cache[key]
        if (maybeResultNode != null) {
            checkElementsPresence()
            val oldValue = maybeResultNode.value
            deleteNodeFromLinkedList(maybeResultNode)

            maybeResultNode.prev = null
            maybeResultNode.next = head
            maybeResultNode.value = value

            pushNodeToLinkedList(maybeResultNode)
            return oldValue
        } else {
            if (nonFull()) {
                val newNode = ListNode(key = key, value = value, prev = null, next = head)
                pushNodeToLinkedList(newNode)
                cache[key] = newNode
            } else {
                checkElementsPresence()
                val lastNode = tail!!
                deleteNodeFromLinkedList(lastNode)
                val removedValue = cache.remove(lastNode.key)?.value
                assert(removedValue == lastNode.value)

                val newNode = ListNode(key = key, value = value, prev = null, next = head)
                pushNodeToLinkedList(newNode)
                cache[key] = newNode
            }
            return null
        }
    }

    override fun doDelete(key: K): V? {
        val resultNode = cache[key] ?: return null
        assert(resultNode.key == key)
        // Otherwise, at least one element is presented
        checkElementsPresence()

        deleteNodeFromLinkedList(resultNode)

        val result = resultNode.value
        val removedValue = cache.remove(key)?.value
        assert(removedValue == result)

        return result
    }

    override fun doSize(): Int = cache.size
}
