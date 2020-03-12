package com.github.kokorin.fitness.common.dao

import com.github.jasync.sql.db.SuspendingConnection
import org.joda.time.LocalDateTime

abstract class CommonDao : CommonCommandDao() {
    protected suspend fun doesUserExist(uid: Int, connection: SuspendingConnection): Boolean {
        return connection.sendPreparedStatement(getUserQuery, listOf(uid)).rows.size > 0
    }

    protected suspend fun getMaxSubscriptionDate(uid: Int, connection: SuspendingConnection): LocalDateTime? {
        val result = connection.sendPreparedStatement(maxSubscriptionDateQuery, listOf(uid)).rows
        return if (result.size > 0) {
            result[0].getAs<LocalDateTime>("end_date")
        } else {
            null
        }
    }

    companion object {
        val getUserQuery =
            """
               SELECT *
               FROM Events
               WHERE Events.user_id = ?
               LIMIT 1;
            """.trimIndent()

        val maxSubscriptionDateQuery =
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
    }
}
