package com.github.kokorin.fitness.manager.query

import com.github.jasync.sql.db.QueryResult
import com.github.jasync.sql.db.ResultSet
import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.SuspendingConnection
import com.github.kokorin.fitness.common.dao.CommonDao
import com.github.kokorin.fitness.manager.model.User
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.joda.time.LocalDateTime
import org.junit.Test
import org.junit.Assert.*

class QueryDaoImplTest {
    @Test
    fun getNonExistingUser() = runBlocking {
        val mainConnection = mockk<SuspendingConnection>()
        coEvery { mainConnection.inTransaction(any<suspend (SuspendingConnection) -> Int>()) }
            .coAnswers {
                val callback = args[0] as suspend (SuspendingConnection) -> Int
                val transactionConnection = mockk<SuspendingConnection>()

                coEvery {
                    transactionConnection.sendPreparedStatement(CommonDao.getUserQuery, listOf(1))
                }.answers {
                    val rows = mockk<ResultSet>()
                    every { rows.size }.returns(0)
                    QueryResult(rowsAffected = 0, statusMessage = "OK", rows = rows)
                }

                callback(transactionConnection)
            }
        val dao = QueryDaoImpl(mainConnection)
        val user = dao.getUser(1)
        assertEquals(user, null)
    }

    @Test
    fun getUserWithoutSubscriptions() = runBlocking {
        val mainConnection = mockk<SuspendingConnection>()
        coEvery { mainConnection.inTransaction(any<suspend (SuspendingConnection) -> Int>()) }
            .coAnswers {
                val callback = args[0] as suspend (SuspendingConnection) -> Int
                val transactionConnection = mockk<SuspendingConnection>()

                coEvery {
                    transactionConnection.sendPreparedStatement(CommonDao.getUserQuery, listOf(1))
                }.answers {
                    val rows = mockk<ResultSet>()
                    every { rows.size }.returns(1)
                    QueryResult(rowsAffected = 0, statusMessage = "OK", rows = rows)
                }

                coEvery {
                    transactionConnection.sendPreparedStatement(CommonDao.maxSubscriptionDateQuery, listOf(1))
                }.answers {
                    val rows = mockk<ResultSet>()
                    every { rows.size }.returns(0)
                    QueryResult(rowsAffected = 0, statusMessage = "OK", rows = rows)
                }

                callback(transactionConnection)
            }
        val dao = QueryDaoImpl(mainConnection)
        val user = dao.getUser(1)
        assertEquals(user, User(1, null))
    }

    @Test
    fun getUserWithSubscriptions() = runBlocking {
        val mainConnection = mockk<SuspendingConnection>()
        val until = LocalDateTime.parse("1862-04-15T20:00:00")
        coEvery { mainConnection.inTransaction(any<suspend (SuspendingConnection) -> Int>()) }
            .coAnswers {
                val callback = args[0] as suspend (SuspendingConnection) -> Int
                val transactionConnection = mockk<SuspendingConnection>()

                coEvery {
                    transactionConnection.sendPreparedStatement(CommonDao.getUserQuery, listOf(1))
                }.answers {
                    val rows = mockk<ResultSet>()
                    every { rows.size }.returns(1)
                    QueryResult(rowsAffected = 0, statusMessage = "OK", rows = rows)
                }

                coEvery {
                    transactionConnection.sendPreparedStatement(CommonDao.maxSubscriptionDateQuery, listOf(1))
                }.answers {
                    val rows = mockk<ResultSet>()
                    every { rows.size }.returns(1)
                    val rowData = mockk<RowData>()
                    every { rowData.getAs<LocalDateTime>("end_date") }.returns(until)
                    every { rows[0] }.returns(rowData)
                    QueryResult(rowsAffected = 0, statusMessage = "OK", rows = rows)
                }

                callback(transactionConnection)
            }
        val dao = QueryDaoImpl(mainConnection)
        val user = dao.getUser(1)
        assertEquals(user, User(1, until))
    }
}
