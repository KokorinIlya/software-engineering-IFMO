package com.github.kokorin.fitness.gate.command

import com.github.jasync.sql.db.SuspendingConnection
import com.github.kokorin.fitness.common.dao.CommonDao
import com.github.kokorin.fitness.gate.model.Event
import com.github.kokorin.fitness.gate.model.GateEventType
import org.joda.time.LocalDateTime

class CommandDaoImpl(private val connection: SuspendingConnection) : CommonDao(), CommandDao {
    private suspend fun getLastUserGateEventType(
        uid: Int,
        transactionConnection: SuspendingConnection
    ): Event? {
        val result = transactionConnection.sendPreparedStatement(lastGateEventQuery, listOf(uid)).rows
        return if (result.size == 0) {
            null
        } else {
            val eventType = GateEventType.valueOf(result[0].getString("gate_event_type")!!)
            val eventTimestamp = result[0].getAs<LocalDateTime>("event_timestamp")
            Event(eventType, eventTimestamp)
        }
    }

    private suspend fun addGateEvent(
        uid: Int,
        eventType: GateEventType,
        eventTimestamp: LocalDateTime,
        transactionConnection: SuspendingConnection
    ) {
        val maxEventId = getMaxUserEventId(uid, transactionConnection)
        val curEventId = maxEventId + 1
        transactionConnection.sendPreparedStatement(newEventCommand, listOf(uid, curEventId))
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
        val prevEvent = getLastUserGateEventType(uid, it)
        if (prevEvent?.eventType == GateEventType.ENTER) {
            throw IllegalArgumentException("Previous event for user $uid was ENTER, cannot enter now")
        }
        addGateEvent(uid, GateEventType.ENTER, enterTime, it)
    }

    override suspend fun processExit(uid: Int, exitTime: LocalDateTime): LocalDateTime = connection.inTransaction {
        val prevEvent = getLastUserGateEventType(uid, it)
        if (prevEvent?.eventType != GateEventType.ENTER) {
            throw IllegalArgumentException("Previous event for user $uid was $prevEvent, cannot exit now")
        }
        addGateEvent(uid, GateEventType.EXIT, exitTime, it)
        prevEvent.eventTimestamp
    }

    companion object {
        val lastGateEventQuery =
            """
                WITH CurUserGateEvents AS (
                    SELECT GateEvents.user_event_id,
                           GateEvents.gate_event_type,
                           GateEvents.event_timestamp
                    FROM GateEvents
                    WHERE GateEvents.user_id = ?
                )
                SELECT CurUserGateEvents.gate_event_type,
                       CurUserGateEvents.event_timestamp
                FROM CurUserGateEvents
                WHERE CurUserGateEvents.user_event_id = (
                    SELECT max(CurUserGateEvents.user_event_id)
                    FROM CurUserGateEvents
                );
            """.trimIndent()

        val newEventCommand =
            """
                INSERT INTO Events (user_id, user_event_id)
                VALUES (?, ?);
            """.trimIndent()

        val newGateEventCommand =
            """
                INSERT INTO GateEvents (user_id, user_event_id, gate_event_type, event_timestamp)
                VALUES (?, ?, ?, ?);
            """.trimIndent()
    }
}
