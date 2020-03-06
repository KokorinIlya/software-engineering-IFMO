package com.github.kokorin.fitness.common.dao

import com.github.jasync.sql.db.SuspendingConnection
import org.jetbrains.annotations.Contract
import org.joda.time.LocalDateTime

abstract class CommonDao {
    protected suspend fun doesUserExist(uid: Int, connection: SuspendingConnection): Boolean {
        val getUserQuery =
            """
               SELECT *
               FROM Events
               WHERE Events.user_id = ?
               LIMIT 1;
            """.trimIndent()
        return connection.sendPreparedStatement(getUserQuery, listOf(uid)).rows.size > 0
    }

    protected suspend fun getMaxSubscriptionDate(uid: Int, connection: SuspendingConnection): LocalDateTime? {
        val query =
            """
                WITH CurUserEvents AS (
                    SELECT SubscriptionRenewalsEvents.user_event_id,
                           SubscriptionRenewalsEvents.end_date
                    FROM SubscriptionRenewalsEvents
                    WHERE SubscriptionRenewalsEvents.user_id = ?
                )
                SELECT CurUserEvents.end_date
                FROM CurUserEvents
                WHERE CurUserEvents.user_event_id = (
                    SELECT max(CurUserEvents.user_event_id)
                    FROM CurUserEvents
                );
            """.trimIndent()
        val result = connection.sendPreparedStatement(query, listOf(uid)).rows
        return if (result.size > 0) {
            result[0].getAs<LocalDateTime>("end_date")
        } else {
            null
        }
    }

    @Contract("doesUserExist(uid, connection) == true")
    protected suspend fun getMaxUserEventId(uid: Int, connection: SuspendingConnection): Int {
        val maxUserEventIdQuery =
            """
                SELECT max(Events.user_event_id)
                FROM Events
                WHERE Events.user_id = ?;
            """.trimIndent()
        return connection
            .sendPreparedStatement(maxUserEventIdQuery, listOf(uid))
            .rows[0]
            .getInt("max")!!
    }
}
