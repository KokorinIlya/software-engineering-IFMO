package com.github.kokorin.fitness.common.dao

import com.github.jasync.sql.db.SuspendingConnection
import org.jetbrains.annotations.Contract

abstract class CommonCommandDao {
    @Contract("!doesUserExist(uid, connection) -> fail")
    protected suspend fun getMaxUserEventId(uid: Int, connection: SuspendingConnection): Int {
        return connection
            .sendPreparedStatement(maxUserEventIdQuery, listOf(uid))
            .rows[0]
            .getInt("max")!!
    }

    companion object {
        val maxUserEventIdQuery =
            """
                SELECT max(Events.user_event_id)
                FROM Events
                WHERE Events.user_id = ?;
            """.trimIndent()
    }
}
