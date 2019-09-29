package com.github.kokorin.lru

import org.junit.Test
import org.junit.Assert.*

class LRUCacheTest {
    @Test
    fun emptyCacheTest() {
        val cache: LRUCache<Int, String> = LRUCache.getCache(5)
        assertEquals(cache.size(), 0)
        assertEquals(cache.cacheSize, 5)
        assertEquals(cache.isFull(), false)
        assertEquals(cache.nonFull(), true)
        assertEquals(cache.isEmpty(), true)
        assertEquals(cache.nonEmpty(), false)
    }

    @Test(expected = IllegalArgumentException::class)
    fun incorrectCacheTest() {
        LRUCache.getCache<Int, String>(-1)
    }

    @Test
    fun trivialAddCacheTest() {
        val cache: LRUCache<Int, String> = LRUCache.getCache(5)
        assertTrue(cache.iterator().asSequence().toList() == listOf<Pair<Int, String>>())
        cache.put(1, "1")
        assertTrue(cache.iterator().asSequence().toList() == listOf(Pair(1, "1")))
        cache.put(3, "3")
        assertTrue(cache.iterator().asSequence().toList() == listOf(Pair(3, "3"), Pair(1, "1")))
        cache.put(5, "5")
        assertTrue(cache.iterator().asSequence().toList() == listOf(Pair(5, "5"), Pair(3, "3"), Pair(1, "1")))
    }

    @Test
    fun getCacheTest() {
        val cache: LRUCache<Int, String> = LRUCache.getCache(5)
        cache.put(1, "1")
        cache.put(3, "3")
        cache.put(5, "5")

        assertTrue(cache.iterator().asSequence().toList() == listOf(Pair(5, "5"), Pair(3, "3"), Pair(1, "1")))
        assertEquals(cache.get(2), null)
        assertTrue(cache.iterator().asSequence().toList() == listOf(Pair(5, "5"), Pair(3, "3"), Pair(1, "1")))
        assertEquals(cache.get(3), "3")
        assertTrue(cache.iterator().asSequence().toList() == listOf(Pair(3, "3"), Pair(5, "5"), Pair(1, "1")))
        assertEquals(cache.get(4), null)
        assertTrue(cache.iterator().asSequence().toList() == listOf(Pair(3, "3"), Pair(5, "5"), Pair(1, "1")))
        assertEquals(cache.get(1), "1")
        assertTrue(cache.iterator().asSequence().toList() == listOf(Pair(1, "1"), Pair(3, "3"), Pair(5, "5")))
    }

    @Test
    fun containsCacheTest() {
        val cache: LRUCache<Int, String> = LRUCache.getCache(5)
        cache.put(1, "1")
        cache.put(3, "3")
        cache.put(5, "5")

        assertTrue(cache.iterator().asSequence().toList() == listOf(Pair(5, "5"), Pair(3, "3"), Pair(1, "1")))
        assertEquals(cache.contains(2), false)
        assertTrue(cache.iterator().asSequence().toList() == listOf(Pair(5, "5"), Pair(3, "3"), Pair(1, "1")))
        assertEquals(cache.contains(3), true)
        assertTrue(cache.iterator().asSequence().toList() == listOf(Pair(5, "5"), Pair(3, "3"), Pair(1, "1")))
        assertEquals(cache.contains(4), false)
        assertTrue(cache.iterator().asSequence().toList() == listOf(Pair(5, "5"), Pair(3, "3"), Pair(1, "1")))
        assertEquals(cache.contains(1), true)
        assertTrue(cache.iterator().asSequence().toList() == listOf(Pair(5, "5"), Pair(3, "3"), Pair(1, "1")))
    }

    @Test
    fun deleteCacheTest() {
        val cache: LRUCache<Int, String> = LRUCache.getCache(5)
        cache.put(1, "1")
        cache.put(3, "3")
        cache.put(5, "5")

        assertTrue(cache.iterator().asSequence().toList() == listOf(Pair(5, "5"), Pair(3, "3"), Pair(1, "1")))
        assertEquals(cache.delete(5), "5")
        assertTrue(cache.iterator().asSequence().toList() == listOf(Pair(3, "3"), Pair(1, "1")))
    }

    @Test
    fun updateCacheTest() {
        val cache: LRUCache<Int, String> = LRUCache.getCache(5)
        cache.put(1, "1")
        cache.put(3, "3")
        cache.put(5, "5")

        assertTrue(cache.iterator().asSequence().toList() == listOf(Pair(5, "5"), Pair(3, "3"), Pair(1, "1")))
        assertEquals(cache.put(3, "33"), "3")
        assertTrue(cache.iterator().asSequence().toList() == listOf(Pair(3, "33"), Pair(5, "5"), Pair(1, "1")))
    }

    @Test
    fun addInvalidateCacheTest() {
        val cache: LRUCache<Int, String> = LRUCache.getCache(5)
        assertTrue(cache.iterator().asSequence().toList() == listOf<Pair<Int, String>>())
        cache.put(1, "1")
        assertTrue(cache.iterator().asSequence().toList() == listOf(Pair(1, "1")))
        cache.put(3, "3")
        assertTrue(cache.iterator().asSequence().toList() == listOf(Pair(3, "3"), Pair(1, "1")))
        cache.put(5, "5")
        assertTrue(cache.iterator().asSequence().toList() == listOf(Pair(5, "5"), Pair(3, "3"), Pair(1, "1")))
        cache.put(2, "2")
        assertTrue(
            cache.iterator().asSequence().toList() == listOf(
                Pair(2, "2"),
                Pair(5, "5"),
                Pair(3, "3"),
                Pair(1, "1")
            )
        )
        cache.put(4, "4")
        assertTrue(
            cache.iterator().asSequence().toList() == listOf(
                Pair(4, "4"),
                Pair(2, "2"),
                Pair(5, "5"),
                Pair(3, "3"),
                Pair(1, "1")
            )
        )
        cache.put(6, "6")
        assertTrue(
            cache.iterator().asSequence().toList() == listOf(
                Pair(6, "6"),
                Pair(4, "4"),
                Pair(2, "2"),
                Pair(5, "5"),
                Pair(3, "3")
            )
        )
        cache.put(8, "8")
        assertTrue(
            cache.iterator().asSequence().toList() == listOf(
                Pair(8, "8"),
                Pair(6, "6"),
                Pair(4, "4"),
                Pair(2, "2"),
                Pair(5, "5")
            )
        )
        cache.put(9, "9")
        assertTrue(
            cache.iterator().asSequence().toList() == listOf(
                Pair(9, "9"),
                Pair(8, "8"),
                Pair(6, "6"),
                Pair(4, "4"),
                Pair(2, "2")
            )
        )
        assertEquals(cache.get(2), "2")
        assertTrue(
            cache.iterator().asSequence().toList() == listOf(
                Pair(2, "2"),
                Pair(9, "9"),
                Pair(8, "8"),
                Pair(6, "6"),
                Pair(4, "4")
            )
        )
        assertEquals(cache.put(10, "10"), null)
        assertTrue(
            cache.iterator().asSequence().toList() == listOf(
                Pair(10, "10"),
                Pair(2, "2"),
                Pair(9, "9"),
                Pair(8, "8"),
                Pair(6, "6")
            )
        )
    }

    @Test(expected = NoSuchElementException::class)
    fun iteratorTest() {
        val cache: LRUCache<Int, String> = LRUCache.getCache(5)
        cache.put(1, "1")
        cache.put(3, "3")
        cache.put(5, "5")
        val iter = cache.iterator()
        while (true) {
            iter.next()
        }
    }

    @Test
    fun addDeleteTest() {
        val cache: LRUCache<Int, String> = LRUCache.getCache(5)
        cache.put(1, "1")
        cache.put(3, "3")
        cache.put(5, "5")
        cache.put(7, "7")
        cache.put(9, "9")
        assertTrue(
            cache.iterator().asSequence().toList() == listOf(
                Pair(9, "9"),
                Pair(7, "7"),
                Pair(5, "5"),
                Pair(3, "3"),
                Pair(1, "1")
            )
        )
        assertEquals(cache.delete(1), "1")
        assertTrue(
            cache.iterator().asSequence().toList() == listOf(
                Pair(9, "9"),
                Pair(7, "7"),
                Pair(5, "5"),
                Pair(3, "3")
            )
        )
        cache.put(2, "2")
        assertTrue(
            cache.iterator().asSequence().toList() == listOf(
                Pair(2, "2"),
                Pair(9, "9"),
                Pair(7, "7"),
                Pair(5, "5"),
                Pair(3, "3")
            )
        )
        cache.put(4, "4")
        assertTrue(
            cache.iterator().asSequence().toList() == listOf(
                Pair(4, "4"),
                Pair(2, "2"),
                Pair(9, "9"),
                Pair(7, "7"),
                Pair(5, "5")
            )
        )
        assertEquals(cache.delete(10), null)
        assertEquals(cache.delete(9), "9")
        assertTrue(
            cache.iterator().asSequence().toList() == listOf(
                Pair(4, "4"),
                Pair(2, "2"),
                Pair(7, "7"),
                Pair(5, "5")
            )
        )
        assertEquals(cache.delete(9), null)
        assertEquals(cache.delete(5), "5")
        assertTrue(
            cache.iterator().asSequence().toList() == listOf(
                Pair(4, "4"),
                Pair(2, "2"),
                Pair(7, "7")
            )
        )
        assertEquals(cache.put(11, "11"), null)
        assertTrue(
            cache.iterator().asSequence().toList() == listOf(
                Pair(11, "11"),
                Pair(4, "4"),
                Pair(2, "2"),
                Pair(7, "7")
            )
        )
        assertEquals(cache.put(7, "77"), "7")
        assertTrue(
            cache.iterator().asSequence().toList() == listOf(
                Pair(7, "77"),
                Pair(11, "11"),
                Pair(4, "4"),
                Pair(2, "2")
            )
        )
        assertEquals(cache.put(4, "44"), "4")
        assertTrue(
            cache.iterator().asSequence().toList() == listOf(
                Pair(4, "44"),
                Pair(7, "77"),
                Pair(11, "11"),
                Pair(2, "2")
            )
        )
        assertEquals(cache.put(25, "25"), null)
        assertTrue(
            cache.iterator().asSequence().toList() == listOf(
                Pair(25, "25"),
                Pair(4, "44"),
                Pair(7, "77"),
                Pair(11, "11"),
                Pair(2, "2")
            )
        )
        assertEquals(cache.put(17, "17"), null)
        assertTrue(
            cache.iterator().asSequence().toList() == listOf(
                Pair(17, "17"),
                Pair(25, "25"),
                Pair(4, "44"),
                Pair(7, "77"),
                Pair(11, "11")
            )
        )
    }
    
    @Test
    fun boxTest() {
        val box: LRUCache<Int, String> = LRUCache.getCache(1)
        assertTrue(box.iterator().asSequence().toList() == listOf<Pair<Int, String>>())
        box.put(1, "1")
        assertTrue(box.iterator().asSequence().toList() == listOf(Pair(1, "1")))
        box.put(2, "2")
        assertTrue(box.iterator().asSequence().toList() == listOf(Pair(2, "2")))
        box.put(3, "3")
        assertTrue(box.iterator().asSequence().toList() == listOf(Pair(3, "3")))
        box.delete(1)
        assertTrue(box.iterator().asSequence().toList() == listOf(Pair(3, "3")))
        box.delete(3)
        assertTrue(box.iterator().asSequence().toList() == listOf<Pair<Int, String>>())
    }

}
