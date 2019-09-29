package com.github.kokorin.lru

class LRUCache<K, V>(cacheSize: Int) : AbstractLRUCache<K, V>(cacheSize) {
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
            assert((size() > 0) xor (head == null))
        }
        if (node == tail) {
            tail = node.prev
            assert((size() > 0) xor (tail == null))
        }
    }

    // Makes node, that was already presented in cache, head of the linked list
    private fun repushNodeToLinkedList(node: ListNode<K, V>) {
        assert(cache[node.key] == node)
        node.prev = null
        node.next = head
        if (head != null) {
            head!!.prev = node
        }
        head = node
    }

    // Adds new node to the head of the linked list
    private fun pushNodeToLinkedList(node: ListNode<K, V>) {
        assert(node.prev == null && node.next == head)
        if (head != null) {
            head!!.prev = node
        }
        head = node
        if (tail == null) {
            assert(size() == 0)
            tail = node
        }
    }

    private fun checkElementPresence() {
        assert(head != null)
        assert(tail != null)
        assert(size() > 0)
    }

    override fun doGet(key: K): V? {
        val resultNode = cache[key] ?: return null
        // Otherwise, at least one element is presented
        checkElementPresence()

        deleteNodeFromLinkedList(resultNode)
        repushNodeToLinkedList(resultNode)
        return resultNode.value
    }

    override fun doPut(key: K, value: V): V? {
        val maybeResultNode = cache[key]
        if (maybeResultNode != null) {
            checkElementPresence()
            val oldValue = maybeResultNode.value
            deleteNodeFromLinkedList(maybeResultNode)
            maybeResultNode.value = value
            repushNodeToLinkedList(maybeResultNode)
            return oldValue
        } else {
            if (nonFull()) {
                val newNode = ListNode(key = key, value = value, prev = null, next = head)
                pushNodeToLinkedList(newNode)
                cache[key] = newNode
            } else {
                checkElementPresence()
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
        // Otherwise, at least one element is presented
        checkElementPresence()

        deleteNodeFromLinkedList(resultNode)

        val result = resultNode.value
        val removedValue = cache.remove(key)?.value
        assert(removedValue == result)

        return result
    }

    override fun doSize(): Int = cache.size
}
