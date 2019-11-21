package com.github.kokorin.events.stats

import com.github.kokorin.events.clock.Clock
import java.time.Instant
import java.util.*
import kotlin.collections.HashMap
import java.time.temporal.ChronoUnit.HOURS

class RPMEventStatistics(private val clock: Clock) : EventStatistics {
    companion object {
        private val minutesInHour: Double = HOURS.duration.toMinutes().toDouble()
    }

    private val eventsQueue: Queue<Pair<String, Instant>> = ArrayDeque()
    private val eventsCount: MutableMap<String, Int> = HashMap()

    private fun lastEventShouldBeRemoved(requestTime: Instant): Boolean {
        val lastEventTime = eventsQueue.peek().second
        return HOURS.between(lastEventTime, requestTime) >= 1
    }

    private fun removeOldEvents(requestTime: Instant) {
        while (eventsQueue.size > 0 && lastEventShouldBeRemoved(requestTime)) {
            val (name, _) = eventsQueue.poll()
            val curEventCount = eventsCount.getOrElse(name) {
                throw AssertionError("Invariant failed")
            }
            if (curEventCount >= 2) {
                eventsCount[name] = curEventCount - 1
            } else {
                eventsCount.remove(name)
            }
        }
    }

    override fun incEvent(name: String) {
        val requestTime = clock.now()
        removeOldEvents(requestTime)
        eventsQueue.add(Pair(name, requestTime))
        eventsCount.compute(name) { _, curCount -> curCount?.plus(1) ?: 1 }
    }

    override fun getEventStatisticByName(name: String): Double {
        val requestTime = clock.now()
        removeOldEvents(requestTime)
        return eventsCount.getOrDefault(name, 0).toDouble() / minutesInHour
    }

    override fun getAllEventStatistic(): Map<String, Double> {
        val requestTime = clock.now()
        removeOldEvents(requestTime)
        return eventsCount.toMap().mapValues { it.value.toDouble() / minutesInHour }
    }

    override fun printStatistic() {
        val stats = getAllEventStatistic()
        println(stats)
    }
}
