package com.github.kokorin.lru

import org.junit.Test
import org.junit.Assert.*

class LRUCacheTest {
    @Test
    fun emptyCacheTest() {
        val cacheAbstract: LRUCache<Int, String> = LRUCache(5)
        assertEquals(cacheAbstract.size(), 0)
        assertEquals(cacheAbstract.cacheSize, 5)
        assertEquals(cacheAbstract.isFull(), false)
        assertEquals(cacheAbstract.nonFull(), true)
        assertEquals(cacheAbstract.isEmpty(), true)
        assertEquals(cacheAbstract.nonEmpty(), false)
    }

    @Test(expected = IllegalArgumentException::class)
    fun incorrectCacheTest() {
        LRUCache<Int, String>(-1)
    }

    @Test
    fun trivialAddCacheTest() {
        val cacheAbstract: LRUCache<Int, String> = LRUCache(5)
        assertTrue(cacheAbstract.iterator().asSequence().toList() == listOf<Pair<Int, String>>())
        cacheAbstract.put(1, "1")
        assertTrue(cacheAbstract.iterator().asSequence().toList() == listOf(Pair(1, "1")))
        cacheAbstract.put(3, "3")
        assertTrue(cacheAbstract.iterator().asSequence().toList() == listOf(Pair(3, "3"), Pair(1, "1")))
        cacheAbstract.put(5, "5")
        assertTrue(cacheAbstract.iterator().asSequence().toList() == listOf(Pair(5, "5"), Pair(3, "3"), Pair(1, "1")))
    }

    @Test
    fun getCacheTest() {
        val cacheAbstract: LRUCache<Int, String> = LRUCache(5)
        cacheAbstract.put(1, "1")
        cacheAbstract.put(3, "3")
        cacheAbstract.put(5, "5")

        assertTrue(cacheAbstract.iterator().asSequence().toList() == listOf(Pair(5, "5"), Pair(3, "3"), Pair(1, "1")))
        assertEquals(cacheAbstract.get(2), null)
        assertTrue(cacheAbstract.iterator().asSequence().toList() == listOf(Pair(5, "5"), Pair(3, "3"), Pair(1, "1")))
        assertEquals(cacheAbstract.get(3), "3")
        assertTrue(cacheAbstract.iterator().asSequence().toList() == listOf(Pair(3, "3"), Pair(5, "5"), Pair(1, "1")))
        assertEquals(cacheAbstract.get(4), null)
        assertTrue(cacheAbstract.iterator().asSequence().toList() == listOf(Pair(3, "3"), Pair(5, "5"), Pair(1, "1")))
        assertEquals(cacheAbstract.get(1), "1")
        assertTrue(cacheAbstract.iterator().asSequence().toList() == listOf(Pair(1, "1"), Pair(3, "3"), Pair(5, "5")))
    }

    @Test
    fun containsCacheTest() {
        val cacheAbstract: LRUCache<Int, String> = LRUCache(5)
        cacheAbstract.put(1, "1")
        cacheAbstract.put(3, "3")
        cacheAbstract.put(5, "5")

        assertTrue(cacheAbstract.iterator().asSequence().toList() == listOf(Pair(5, "5"), Pair(3, "3"), Pair(1, "1")))
        assertEquals(cacheAbstract.contains(2), false)
        assertTrue(cacheAbstract.iterator().asSequence().toList() == listOf(Pair(5, "5"), Pair(3, "3"), Pair(1, "1")))
        assertEquals(cacheAbstract.contains(3), true)
        assertTrue(cacheAbstract.iterator().asSequence().toList() == listOf(Pair(5, "5"), Pair(3, "3"), Pair(1, "1")))
        assertEquals(cacheAbstract.contains(4), false)
        assertTrue(cacheAbstract.iterator().asSequence().toList() == listOf(Pair(5, "5"), Pair(3, "3"), Pair(1, "1")))
        assertEquals(cacheAbstract.contains(1), true)
        assertTrue(cacheAbstract.iterator().asSequence().toList() == listOf(Pair(5, "5"), Pair(3, "3"), Pair(1, "1")))
    }

    @Test
    fun deleteCacheTest() {
        val cacheAbstract: LRUCache<Int, String> = LRUCache(5)
        cacheAbstract.put(1, "1")
        cacheAbstract.put(3, "3")
        cacheAbstract.put(5, "5")

        assertTrue(cacheAbstract.iterator().asSequence().toList() == listOf(Pair(5, "5"), Pair(3, "3"), Pair(1, "1")))
        assertEquals(cacheAbstract.delete(5), "5")
        assertTrue(cacheAbstract.iterator().asSequence().toList() == listOf(Pair(3, "3"), Pair(1, "1")))
    }

    @Test
    fun updateCacheTest() {
        val cacheAbstract: LRUCache<Int, String> = LRUCache(5)
        cacheAbstract.put(1, "1")
        cacheAbstract.put(3, "3")
        cacheAbstract.put(5, "5")

        assertTrue(cacheAbstract.iterator().asSequence().toList() == listOf(Pair(5, "5"), Pair(3, "3"), Pair(1, "1")))
        assertEquals(cacheAbstract.put(3, "33"), "3")
        assertTrue(cacheAbstract.iterator().asSequence().toList() == listOf(Pair(3, "33"), Pair(5, "5"), Pair(1, "1")))
    }

    @Test
    fun addInvalidateCacheTest() {
        val cacheAbstract: LRUCache<Int, String> = LRUCache(5)
        assertTrue(cacheAbstract.iterator().asSequence().toList() == listOf<Pair<Int, String>>())
        cacheAbstract.put(1, "1")
        assertTrue(cacheAbstract.iterator().asSequence().toList() == listOf(Pair(1, "1")))
        cacheAbstract.put(3, "3")
        assertTrue(cacheAbstract.iterator().asSequence().toList() == listOf(Pair(3, "3"), Pair(1, "1")))
        cacheAbstract.put(5, "5")
        assertTrue(cacheAbstract.iterator().asSequence().toList() == listOf(Pair(5, "5"), Pair(3, "3"), Pair(1, "1")))
        cacheAbstract.put(2, "2")
        assertTrue(
            cacheAbstract.iterator().asSequence().toList() == listOf(
                Pair(2, "2"),
                Pair(5, "5"),
                Pair(3, "3"),
                Pair(1, "1")
            )
        )
        cacheAbstract.put(4, "4")
        assertTrue(
            cacheAbstract.iterator().asSequence().toList() == listOf(
                Pair(4, "4"),
                Pair(2, "2"),
                Pair(5, "5"),
                Pair(3, "3"),
                Pair(1, "1")
            )
        )
        cacheAbstract.put(6, "6")
        assertTrue(
            cacheAbstract.iterator().asSequence().toList() == listOf(
                Pair(6, "6"),
                Pair(4, "4"),
                Pair(2, "2"),
                Pair(5, "5"),
                Pair(3, "3")
            )
        )
        cacheAbstract.put(8, "8")
        assertTrue(
            cacheAbstract.iterator().asSequence().toList() == listOf(
                Pair(8, "8"),
                Pair(6, "6"),
                Pair(4, "4"),
                Pair(2, "2"),
                Pair(5, "5")
            )
        )
        cacheAbstract.put(9, "9")
        assertTrue(
            cacheAbstract.iterator().asSequence().toList() == listOf(
                Pair(9, "9"),
                Pair(8, "8"),
                Pair(6, "6"),
                Pair(4, "4"),
                Pair(2, "2")
            )
        )
        assertEquals(cacheAbstract.get(2), "2")
        assertTrue(
            cacheAbstract.iterator().asSequence().toList() == listOf(
                Pair(2, "2"),
                Pair(9, "9"),
                Pair(8, "8"),
                Pair(6, "6"),
                Pair(4, "4")
            )
        )
        assertEquals(cacheAbstract.put(10, "10"), null)
        assertTrue(
            cacheAbstract.iterator().asSequence().toList() == listOf(
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
        val cacheAbstract: LRUCache<Int, String> = LRUCache(5)
        cacheAbstract.put(1, "1")
        cacheAbstract.put(3, "3")
        cacheAbstract.put(5, "5")
        val iter = cacheAbstract.iterator()
        while (true) {
            iter.next()
        }
    }

    @Test
    fun addDeleteTest() {
        val cacheAbstract: LRUCache<Int, String> = LRUCache(5)
        cacheAbstract.put(1, "1")
        cacheAbstract.put(3, "3")
        cacheAbstract.put(5, "5")
        cacheAbstract.put(7, "7")
        cacheAbstract.put(9, "9")
        assertTrue(
            cacheAbstract.iterator().asSequence().toList() == listOf(
                Pair(9, "9"),
                Pair(7, "7"),
                Pair(5, "5"),
                Pair(3, "3"),
                Pair(1, "1")
            )
        )
        assertEquals(cacheAbstract.delete(1), "1")
        assertTrue(
            cacheAbstract.iterator().asSequence().toList() == listOf(
                Pair(9, "9"),
                Pair(7, "7"),
                Pair(5, "5"),
                Pair(3, "3")
            )
        )
        cacheAbstract.put(2, "2")
        assertTrue(
            cacheAbstract.iterator().asSequence().toList() == listOf(
                Pair(2, "2"),
                Pair(9, "9"),
                Pair(7, "7"),
                Pair(5, "5"),
                Pair(3, "3")
            )
        )
        cacheAbstract.put(4, "4")
        assertTrue(
            cacheAbstract.iterator().asSequence().toList() == listOf(
                Pair(4, "4"),
                Pair(2, "2"),
                Pair(9, "9"),
                Pair(7, "7"),
                Pair(5, "5")
            )
        )
        assertEquals(cacheAbstract.delete(10), null)
        assertEquals(cacheAbstract.delete(9), "9")
        assertTrue(
            cacheAbstract.iterator().asSequence().toList() == listOf(
                Pair(4, "4"),
                Pair(2, "2"),
                Pair(7, "7"),
                Pair(5, "5")
            )
        )
        assertEquals(cacheAbstract.delete(9), null)
        assertEquals(cacheAbstract.delete(5), "5")
        assertTrue(
            cacheAbstract.iterator().asSequence().toList() == listOf(
                Pair(4, "4"),
                Pair(2, "2"),
                Pair(7, "7")
            )
        )
        assertEquals(cacheAbstract.put(11, "11"), null)
        assertTrue(
            cacheAbstract.iterator().asSequence().toList() == listOf(
                Pair(11, "11"),
                Pair(4, "4"),
                Pair(2, "2"),
                Pair(7, "7")
            )
        )
        assertEquals(cacheAbstract.put(7, "77"), "7")
        assertTrue(
            cacheAbstract.iterator().asSequence().toList() == listOf(
                Pair(7, "77"),
                Pair(11, "11"),
                Pair(4, "4"),
                Pair(2, "2")
            )
        )
        assertEquals(cacheAbstract.put(4, "44"), "4")
        assertTrue(
            cacheAbstract.iterator().asSequence().toList() == listOf(
                Pair(4, "44"),
                Pair(7, "77"),
                Pair(11, "11"),
                Pair(2, "2")
            )
        )
        assertEquals(cacheAbstract.put(25, "25"), null)
        assertTrue(
            cacheAbstract.iterator().asSequence().toList() == listOf(
                Pair(25, "25"),
                Pair(4, "44"),
                Pair(7, "77"),
                Pair(11, "11"),
                Pair(2, "2")
            )
        )
        assertEquals(cacheAbstract.put(17, "17"), null)
        assertTrue(
            cacheAbstract.iterator().asSequence().toList() == listOf(
                Pair(17, "17"),
                Pair(25, "25"),
                Pair(4, "44"),
                Pair(7, "77"),
                Pair(11, "11")
            )
        )
    }

}
