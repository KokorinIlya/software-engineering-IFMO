package com.github.kokorin.fitness.manager.dao

import com.github.jasync.sql.db.SuspendingConnection
import com.github.kokorin.fitness.manager.model.User
import java.util.*
import java.util.concurrent.atomic.AtomicReference

class ManagerDao(private val connection: SuspendingConnection) : CommandDao, QueryDao {
    data class AvailableUids(val maxUsedUid: Int, val maxAvailableUid: Int)

    private val availableUidsRef: AtomicReference<AvailableUids> =
        AtomicReference(AvailableUids(-1, -1))

    private suspend fun getNewUid(transactionConnection: SuspendingConnection): Int {
        while (true) {
            val availableUids = availableUidsRef.get()
            if (availableUids.maxUsedUid == availableUids.maxAvailableUid) {
                // Доступный пул uid'ов исчерпан
                val curMaxUid = if (availableUids.maxUsedUid == -1) {
                    // Приложение только что запущено и нужно узнать, какой максимальный uid в БД
                    val getMaxUidCommand =
                        """
                            SELECT MaxIds.max_id
                            FROM MaxIds
                            WHERE MaxIds.entity = 'USER';
                        """.trimIndent()
                    transactionConnection.sendQuery(getMaxUidCommand).rows[0].getInt("max_id")!!
                } else {
                    availableUids.maxAvailableUid
                }
                val nextMaxId = curMaxUid + 10
                // Пополняем пул
                val changeMaxUidCommand =
                    """
                        UPDATE MaxIds
                        SET max_id = ?
                        WHERE entity = 'USER'
                          AND max_id = ?;
                    """.trimIndent()
                val result =
                    transactionConnection.sendPreparedStatement(changeMaxUidCommand, listOf(nextMaxId, curMaxUid))
                val resultId = curMaxUid + 1 // Теперь это самый большой из используемых uid'ов
                if (result.rowsAffected != 1L ||
                    !availableUidsRef.compareAndSet(availableUids, AvailableUids(resultId, nextMaxId))
                ) {
                    throw IllegalStateException("Max UID was changed concurrently")
                }
                return resultId
            } else {
                // пробуем взять uid из незакончившегося пула
                val resultId = availableUids.maxUsedUid + 1
                val newAvailableUids = AvailableUids(resultId, availableUids.maxAvailableUid)
                if (availableUidsRef.compareAndSet(availableUids, newAvailableUids)) {
                    return resultId
                }
            }
        }
    }

    override suspend fun registerNewUser(): Int {
        return connection.inTransaction {
            val newUid = getNewUid(it)
            for (tableName in listOf("Events", "NewUserEvents")) {
                val newUserEventCommand =
                    """
                        INSERT INTO $tableName (user_id, user_event_id)
                        VALUES (?, 0);
                    """.trimIndent()
                it.sendPreparedStatement(newUserEventCommand, listOf(newUid))
            }
            newUid
        }
    }

    override suspend fun subscriptionRenewal(uid: Int, until: Date) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getUser(uid: Int): User? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
