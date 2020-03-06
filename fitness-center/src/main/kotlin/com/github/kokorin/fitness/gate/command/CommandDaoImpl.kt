package com.github.kokorin.fitness.gate.command

import com.github.jasync.sql.db.SuspendingConnection
import com.github.kokorin.fitness.common.dao.CommonDao
import org.joda.time.LocalDateTime

class CommandDaoImpl(private val connection: SuspendingConnection) : CommonDao(), CommandDao {
    private suspend fun getLastUserGateEventType(uid: Int, transactionConnection: SuspendingConnection): String? {
        // TODO: Enum
        val query =
            """
                WITH CurUserGateEvents AS (
                    SELECT GateEvents.user_event_id,
                           GateEvents.gate_event_type
                    FROM GateEvents
                    WHERE GateEvents.user_id = ?
                )
                SELECT CurUserGateEvents.gate_event_type
                FROM CurUserGateEvents
                WHERE CurUserGateEvents.user_event_id = (
                    SELECT max(CurUserGateEvents.user_event_id)
                    FROM CurUserGateEvents
                );
            """.trimIndent()
        val result = transactionConnection.sendPreparedStatement(query, listOf(uid)).rows
        return if (result.size == 0) {
            null
        } else {
            result[0].getString("gate_event_type")
        }
    }

    private suspend fun addGateEvent(
        uid: Int,
        eventType: String,
        eventTimestamp: LocalDateTime,
        transactionConnection: SuspendingConnection
    ) {
        val maxEventId = getMaxUserEventId(uid, transactionConnection)
        val curEventId = maxEventId + 1
        val newEventCommand =
            """
                INSERT INTO Events (user_id, user_event_id)
                VALUES (?, ?);
            """.trimIndent()
        transactionConnection.sendPreparedStatement(newEventCommand, listOf(uid, curEventId))
        val newGateEventCommand =
            """
                INSERT INTO GateEvents (user_id, user_event_id, gate_event_type, event_timestamp)
                VALUES (?, ?, ?, ?);
            """.trimIndent()
        transactionConnection
            .sendPreparedStatement(newGateEventCommand, listOf(uid, curEventId, eventType, eventTimestamp))
    }

    override suspend fun processEnter(uid: Int, enterTime: LocalDateTime) = connection.inTransaction {
        if (!doesUserExist(uid, it)) {
            throw IllegalArgumentException("User with uid = $uid doesn't exist")
        }
        val subscriptionDate = getMaxSubscriptionDate(uid, it)
        if (subscriptionDate == null || !enterTime.isBefore(subscriptionDate)) {
            throw IllegalArgumentException(
                "Cannot process enter at $enterTime, because subscription is active only before $subscriptionDate"
            )
        }
        val prevEventType = getLastUserGateEventType(uid, it)
        if (prevEventType == "ENTER") {
            throw IllegalArgumentException("Previous event for user $uid was ENTER, cannot enter now")
        }
        addGateEvent(uid, "ENTER", enterTime, it)
    }

    override suspend fun processExit(uid: Int, exitTime: LocalDateTime) = connection.inTransaction {
        // TODO: send to stats enter & exit time
        if (!doesUserExist(uid, it)) {
            throw IllegalArgumentException("User with uid = $uid doesn't exist")
        }
        val prevEventType = getLastUserGateEventType(uid, it)
        if (prevEventType != "ENTER") {
            throw IllegalArgumentException("Previous event for user $uid was $prevEventType, cannot exit now")
        }
        addGateEvent(uid, "EXIT", exitTime, it)
    }
}
