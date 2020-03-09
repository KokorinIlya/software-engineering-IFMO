package com.github.kokorin.fitness.manager.command

import com.github.jasync.sql.db.QueryResult
import com.github.jasync.sql.db.ResultSet
import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.SuspendingConnection
import com.github.kokorin.fitness.common.clock.Clock
import com.github.kokorin.fitness.common.clock.TimeTravelClock
import com.github.kokorin.fitness.common.dao.CommonDao
import org.junit.Test
import org.junit.Assert.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.joda.time.LocalDateTime

class CommandDaoImplTest {
    @Test
    fun registerOneUserTest() = runBlocking {
        val mainConnection = mockk<SuspendingConnection>()
        coEvery { mainConnection.inTransaction(any<suspend (SuspendingConnection) -> Int>()) }
            .coAnswers {
                val callback = args[0] as suspend (SuspendingConnection) -> Int
                val transactionConnection = mockk<SuspendingConnection>()
                coEvery {
                    transactionConnection.sendQuery(CommandDaoImpl.getMaxUidCommand)
                }.answers {
                    val rows = mockk<ResultSet>()
                    val rowData = mockk<RowData>()
                    every { rowData.getInt("max_id") }.returns(0)
                    every { rows[0] }.returns(rowData)
                    QueryResult(rowsAffected = 0, statusMessage = "OK", rows = rows)
                }

                coEvery {
                    transactionConnection.sendPreparedStatement(CommandDaoImpl.changeMaxUidCommand, listOf(10, 0))
                }.answers {
                    QueryResult(rowsAffected = 1, statusMessage = "OK")
                }

                coEvery {
                    transactionConnection.sendPreparedStatement(CommandDaoImpl.newUserEventCommand, listOf(1))
                }.answers {
                    QueryResult(rowsAffected = 1, statusMessage = "OK")
                }

                callback(transactionConnection)
            }
        val clock = mockk<Clock>()
        val dao = CommandDaoImpl(mainConnection, clock, poolSize = 10)
        val newUid = dao.registerNewUser()
        assertEquals(newUid, 1)
    }

    @Test
    fun registerMultipleUsersWithPoolTest() = runBlocking {
        val mainConnection = mockk<SuspendingConnection>()
        coEvery { mainConnection.inTransaction(any<suspend (SuspendingConnection) -> Int>()) }
            .coAnswers {
                val callback = args[0] as suspend (SuspendingConnection) -> Int
                val transactionConnection = mockk<SuspendingConnection>()
                coEvery {
                    transactionConnection.sendQuery(CommandDaoImpl.getMaxUidCommand)
                }.answers {
                    val rows = mockk<ResultSet>()
                    val rowData = mockk<RowData>()
                    every { rowData.getInt("max_id") }.returns(0)
                    every { rows[0] }.returns(rowData)
                    QueryResult(rowsAffected = 0, statusMessage = "OK", rows = rows)
                }

                coEvery {
                    transactionConnection.sendPreparedStatement(CommandDaoImpl.changeMaxUidCommand, listOf(10, 0))
                }.answers {
                    QueryResult(rowsAffected = 1, statusMessage = "OK")
                }

                coEvery {
                    transactionConnection.sendPreparedStatement(CommandDaoImpl.newUserEventCommand, listOf(1))
                }.answers {
                    QueryResult(rowsAffected = 1, statusMessage = "OK")
                }

                coEvery {
                    transactionConnection.sendPreparedStatement(CommandDaoImpl.newUserEventCommand, listOf(2))
                }.answers {
                    QueryResult(rowsAffected = 1, statusMessage = "OK")
                }

                callback(transactionConnection)
            }
        val clock = mockk<Clock>()
        val dao = CommandDaoImpl(mainConnection, clock, poolSize = 10)
        val firstUid = dao.registerNewUser()
        assertEquals(firstUid, 1)
        val secondUid = dao.registerNewUser()
        assertEquals(secondUid, 2)
    }

    @Test
    fun registerMultipleUsersWithPoolUpdateTest() = runBlocking {
        val mainConnection = mockk<SuspendingConnection>()
        coEvery { mainConnection.inTransaction(any<suspend (SuspendingConnection) -> Int>()) }
            .coAnswers {
                val callback = args[0] as suspend (SuspendingConnection) -> Int
                val transactionConnection = mockk<SuspendingConnection>()
                coEvery {
                    transactionConnection.sendQuery(CommandDaoImpl.getMaxUidCommand)
                }.answers {
                    val rows = mockk<ResultSet>()
                    val rowData = mockk<RowData>()
                    every { rowData.getInt("max_id") }.returns(0)
                    every { rows[0] }.returns(rowData)
                    QueryResult(rowsAffected = 0, statusMessage = "OK", rows = rows)
                }

                coEvery {
                    transactionConnection.sendPreparedStatement(CommandDaoImpl.changeMaxUidCommand, listOf(2, 0))
                }.answers {
                    QueryResult(rowsAffected = 1, statusMessage = "OK")
                }

                coEvery {
                    transactionConnection.sendPreparedStatement(CommandDaoImpl.newUserEventCommand, listOf(1))
                }.answers {
                    QueryResult(rowsAffected = 1, statusMessage = "OK")
                }

                coEvery {
                    transactionConnection.sendPreparedStatement(CommandDaoImpl.newUserEventCommand, listOf(2))
                }.answers {
                    QueryResult(rowsAffected = 1, statusMessage = "OK")
                }

                coEvery {
                    transactionConnection.sendPreparedStatement(CommandDaoImpl.changeMaxUidCommand, listOf(4, 2))
                }.answers {
                    QueryResult(rowsAffected = 1, statusMessage = "OK")
                }

                coEvery {
                    transactionConnection.sendPreparedStatement(CommandDaoImpl.newUserEventCommand, listOf(3))
                }.answers {
                    QueryResult(rowsAffected = 1, statusMessage = "OK")
                }

                callback(transactionConnection)
            }
        val clock = mockk<Clock>()
        val dao = CommandDaoImpl(mainConnection, clock, poolSize = 2)
        val firstUid = dao.registerNewUser()
        assertEquals(firstUid, 1)
        val secondUid = dao.registerNewUser()
        assertEquals(secondUid, 2)
        val thirdUid = dao.registerNewUser()
        assertEquals(thirdUid, 3)
    }

    @Test
    fun firstCorrectRenewalTest() = runBlocking {
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
                    every { rows.size }.returns(0)
                    QueryResult(rowsAffected = 0, statusMessage = "OK", rows = rows)
                }

                coEvery {
                    transactionConnection.sendPreparedStatement(CommonDao.maxUserEventIdQuery, listOf(1))
                }.answers {
                    val rows = mockk<ResultSet>()
                    val rowData = mockk<RowData>()
                    every { rowData.getInt("max") }.returns(14)
                    every { rows[0] }.returns(rowData)
                    QueryResult(rowsAffected = 0, statusMessage = "OK", rows = rows)
                }

                coEvery {
                    transactionConnection.sendPreparedStatement(CommandDaoImpl.newEventCommand, listOf(1, 15))
                }.answers {
                    QueryResult(rowsAffected = 1, statusMessage = "OK")
                }

                coEvery {
                    transactionConnection.sendPreparedStatement(CommandDaoImpl.renewalCommand, listOf(1, 15, until))
                }.answers {
                    QueryResult(rowsAffected = 1, statusMessage = "OK")
                }

                callback(transactionConnection)
            }
        val clock = TimeTravelClock(LocalDateTime.parse("1862-04-14T20:00:00"))
        val dao = CommandDaoImpl(mainConnection, clock, poolSize = 2)
        val ans = dao.subscriptionRenewal(1, until)
        assertEquals(ans, Unit)
    }
}
