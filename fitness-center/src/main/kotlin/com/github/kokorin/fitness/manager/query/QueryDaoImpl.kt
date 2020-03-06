package com.github.kokorin.fitness.manager.query

import com.github.jasync.sql.db.SuspendingConnection
import com.github.kokorin.fitness.common.dao.CommonDao
import com.github.kokorin.fitness.manager.model.User

class QueryDaoImpl(private val connection: SuspendingConnection) : CommonDao(), QueryDao {
    override suspend fun getUser(uid: Int): User? = connection.inTransaction {
        if (!doesUserExist(uid, it)) {
            null
        } else {
            User(uid, getMaxSubscriptionDate(uid, it))
        }
    }
}
