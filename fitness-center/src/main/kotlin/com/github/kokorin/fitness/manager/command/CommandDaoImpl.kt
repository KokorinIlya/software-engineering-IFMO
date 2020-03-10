package com.github.kokorin.fitness.manager.command

import com.github.jasync.sql.db.SuspendingConnection
import com.github.kokorin.fitness.common.clock.Clock
import com.github.kokorin.fitness.common.dao.CommonDao
import org.joda.time.LocalDateTime
import java.util.concurrent.atomic.AtomicReference
import kotlin.IllegalArgumentException

class CommandDaoImpl(
    private val connection: SuspendingConnection,
    private val clock: Clock,
    private val poolSize: Int = 10
) : CommonDao(),
    CommandDao {
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
                    transactionConnection.sendQuery(getMaxUidCommand).rows[0].getInt("max_id")!!
                } else {
                    availableUids.maxAvailableUid
                }
                val nextMaxId = curMaxUid + poolSize
                // Пополняем пул
                val result =
                    transactionConnection.sendPreparedStatement(changeMaxUidCommand, listOf(nextMaxId, curMaxUid))
                val resultId = curMaxUid + 1 // Теперь это самый большой из используемых uid'ов
                // Если не получилось обновить пул так как пул был обновлён другим потоком, выдаём ошибку
                if (result.rowsAffected != 1L ||
                    !availableUidsRef.compareAndSet(
                        availableUids,
                        AvailableUids(
                            resultId,
                            nextMaxId
                        )
                    )
                ) {
                    throw IllegalStateException("Max UID was changed concurrently")
                }
                return resultId
            } else {
                // пробуем взять uid из незакончившегося пула
                val resultId = availableUids.maxUsedUid + 1
                val newAvailableUids =
                    AvailableUids(
                        resultId,
                        availableUids.maxAvailableUid
                    )
                if (availableUidsRef.compareAndSet(availableUids, newAvailableUids)) {
                    return resultId
                }
            }
        }
    }

    override suspend fun registerNewUser(): Int = connection.inTransaction {
        val newUid = getNewUid(it)
        it.sendPreparedStatement(newUserEventCommand, listOf(newUid))
        newUid
    }

    override suspend fun subscriptionRenewal(uid: Int, until: LocalDateTime) = connection.inTransaction {
        val curDate = clock.now()
        if (!curDate.isBefore(until)) {
            throw IllegalArgumentException("Cannot process renewal until $until at $curDate")
        }
        if (!doesUserExist(uid, it)) {
            throw IllegalArgumentException("User with uid $uid doesn't exist")
        }
        val maxSubscriptionDate = getMaxSubscriptionDate(uid, it)
        if (maxSubscriptionDate != null) {
            if (!maxSubscriptionDate.isBefore(until)) {
                throw IllegalArgumentException(
                    "Cannot process renewal until $until, because current renewal is until $maxSubscriptionDate"
                )
            }
        }
        val maxUserEventId = getMaxUserEventId(uid, it)
        val curUserEventId = maxUserEventId + 1
        it.sendPreparedStatement(newEventCommand, listOf(uid, curUserEventId))
        it.sendPreparedStatement(renewalCommand, listOf(uid, curUserEventId, until))
        Unit
    }

    companion object {
        val getMaxUidCommand =
            """
                SELECT MaxIds.max_id
                FROM MaxIds
                WHERE MaxIds.entity = 'USER';
            """.trimIndent()

        val renewalCommand =
            """
                INSERT INTO SubscriptionRenewalsEvents (user_id, user_event_id, end_date)
                VALUES (?, ?, ?);
            """.trimIndent()

        val newEventCommand =
            """
                INSERT INTO Events (user_id, user_event_id)
                VALUES (?, ?);
            """.trimIndent()

        val newUserEventCommand =
            """
                INSERT INTO Events (user_id, user_event_id)
                VALUES (?, 0);
            """.trimIndent()

        val changeMaxUidCommand =
            """
                UPDATE MaxIds
                SET max_id = ?
                WHERE entity = 'USER'
                  AND max_id = ?;
            """.trimIndent()
    }
}
