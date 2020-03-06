package com.github.kokorin.fitness.manager.query

import com.github.kokorin.fitness.common.processor.Processor

class QueryProcessor(private val queryDao: QueryDao) : Processor<Query> {
    override suspend fun doProcess(t: Query): String {
        return when (t) {
            is GetUserQuery -> {
                queryDao.getUser(t.uid)?.toString() ?: "No such user"
            }
        }
    }
}
