package com.github.kokorin.fitness.stats.state

import com.github.jasync.sql.db.SuspendingConnection
import com.github.kokorin.fitness.stats.command.UpdatableStats
import com.github.kokorin.fitness.stats.model.UserStats
import com.github.kokorin.fitness.stats.query.QueryableStats
import org.joda.time.LocalDateTime
import org.joda.time.Period
import java.util.concurrent.ConcurrentHashMap

class StatsState : UpdatableStats, QueryableStats {
    private val state = ConcurrentHashMap<Int, UserStats>()

    /*
    Should be called once from a single thread.
    Call of init should happen-before call of updateState
    Reference should be correctly published
     */
    suspend fun init(connection: SuspendingConnection) {
        val result = connection.sendQuery(getStatsQuery).rows
        for (curRow in result) {
            val uid = curRow.getInt("user_id")!!
            val interval = curRow.getAs<Period>("total_interval")
            val visitsCount = curRow.getLong("visits_count")!!.toInt()
            state[uid] = UserStats(interval, visitsCount)
        }
    }

    // Thread-safe
    override fun updateState(uid: Int, enterTime: LocalDateTime, exitTime: LocalDateTime) {
        val period = Period.fieldDifference(enterTime, exitTime)
        state.compute(uid) { _, curStats ->
            if (curStats == null) {
                UserStats(period.normalizedStandard(), 1)
            } else {
                UserStats(curStats.totalTime.plus(period).normalizedStandard(), curStats.visitsCount + 1)
            }
        }
    }

    override fun getUserStats(uid: Int): UserStats? = state[uid]

    companion object {
        val getStatsQuery =
            """
                WITH ExitSumByUser AS (
                    SELECT GateEvents.user_id,
                           sum(GateEvents.event_timestamp - '1862-04-14T00:00:00' :: TIMESTAMP) AS exit_sum
                    FROM GateEvents
                    WHERE GateEvents.gate_event_type = 'EXIT'
                    GROUP BY GateEvents.user_id
                ),
                     EnterSumByUser AS (
                         SELECT GateEvents.user_id,
                                sum(GateEvents.event_timestamp - '1862-04-14T00:00:00' :: TIMESTAMP) AS enter_sum
                         FROM GateEvents
                         WHERE GateEvents.gate_event_type = 'ENTER'
                         GROUP BY GateEvents.user_id
                     ),
                     LastEventIdByUser AS (
                         SELECT GateEvents.user_id,
                                max(GateEvents.user_event_id) AS user_event_id
                         FROM GateEvents
                         GROUP BY GateEvents.user_id
                     ),
                     LastEventByUser AS (
                         SELECT GateEvents.user_id,
                                GateEvents.user_event_id,
                                GateEvents.event_timestamp,
                                GateEvents.gate_event_type
                         FROM GateEvents
                                  NATURAL JOIN LastEventIdByUser
                     ),
                     SubtractByUser AS (
                         SELECT (LastEventByUser.event_timestamp - '1862-04-14T00:00:00') *
                                (CASE WHEN LastEventByUser.gate_event_type = 'EXIT' THEN 0 ELSE 1 END)
                                    AS subtraction
                         FROM LastEventByUser
                     ),
                     OnlyExitEvents AS (
                         SELECT GateEvents.user_id,
                                GateEvents.user_event_id
                         FROM GateEvents
                         WHERE GateEvents.gate_event_type = 'EXIT'
                     ),
                     ExitsCountByUser AS (
                         SELECT OnlyExitEvents.user_id,
                                count(*) AS exits_count
                         FROM OnlyExitEvents
                         GROUP BY OnlyExitEvents.user_id
                     )
                SELECT ExitSumByUser.user_id,
                       ExitSumByUser.exit_sum - (EnterSumByUser.enter_sum - SubtractByUser.subtraction) AS total_interval,
                       ExitsCountByUser.exits_count                                                     AS visits_count
                FROM ExitSumByUser
                         NATURAL JOIN EnterSumByUser
                         NATURAL JOIN SubtractByUser
                         NATURAL JOIN ExitsCountByUser;
            """.trimIndent()
    }
}
