package com.github.kokorin.fitness.gate.command

import com.github.jasync.sql.db.QueryResult
import com.github.jasync.sql.db.ResultSet
import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.SuspendingConnection
import com.github.kokorin.fitness.common.dao.CommonCommandDao
import com.github.kokorin.fitness.common.dao.CommonDao
import com.github.kokorin.fitness.gate.model.GateEventType
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.joda.time.LocalDateTime
import org.junit.Test
import org.junit.Assert.*

class CommandDaoImplTest {
    @Test
    fun<T> firstCorrectEnterTest() = runBlocking {
        val enterTime = LocalDateTime.parse("1862-04-14T20:00:00")
        val mainConnection = mockk<SuspendingConnection>()
        coEvery { mainConnection.inTransaction(any<suspend (SuspendingConnection) -> T>()) }
            .coAnswers {
                val callback = args[0] as suspend (SuspendingConnection) -> T
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
                    every { rowData.getAs<LocalDateTime>("end_date") }.returns(
                        LocalDateTime.parse("1862-04-15T20:00:00")
                    )
                    every { rows[0] }.returns(rowData)
                    QueryResult(rowsAffected = 0, statusMessage = "OK", rows = rows)
                }

                coEvery {
                    transactionConnection.sendPreparedStatement(CommandDaoImpl.lastGateEventQuery, listOf(1))
                }.answers {
                    val rows = mockk<ResultSet>()
                    every { rows.size }.returns(0)
                    QueryResult(rowsAffected = 0, statusMessage = "OK", rows = rows)
                }

                coEvery {
                    transactionConnection.sendPreparedStatement(CommonCommandDao.maxUserEventIdQuery, listOf(1))
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
                    transactionConnection.sendPreparedStatement(
                        CommandDaoImpl.newGateEventCommand,
                        listOf(1, 15, GateEventType.ENTER, enterTime)
                    )
                }.answers {
                    QueryResult(rowsAffected = 1, statusMessage = "OK")
                }

                callback(transactionConnection)
            }
        val dao = CommandDaoImpl(mainConnection)

        val result = dao.processEnter(1, enterTime)
        assertEquals(result, Unit)
    }

    @Test
    fun<T> enterAfterExitTest() = runBlocking {
        val enterTime = LocalDateTime.parse("1862-04-14T20:00:00")
        val mainConnection = mockk<SuspendingConnection>()
        coEvery { mainConnection.inTransaction(any<suspend (SuspendingConnection) -> T>()) }
            .coAnswers {
                val callback = args[0] as suspend (SuspendingConnection) -> T
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
                    every { rowData.getAs<LocalDateTime>("end_date") }.returns(
                        LocalDateTime.parse("1862-04-15T20:00:00")
                    )
                    every { rows[0] }.returns(rowData)
                    QueryResult(rowsAffected = 0, statusMessage = "OK", rows = rows)
                }

                coEvery {
                    transactionConnection.sendPreparedStatement(CommandDaoImpl.lastGateEventQuery, listOf(1))
                }.answers {
                    val rows = mockk<ResultSet>()
                    every { rows.size }.returns(1)
                    val rowData = mockk<RowData>()
                    every { rowData.getString("gate_event_type") }.returns("EXIT")
                    every { rowData.getAs<LocalDateTime>("event_timestamp") }.returns(
                        LocalDateTime.parse("1862-04-13T20:00:00")
                    )
                    every { rows[0] }.returns(rowData)
                    QueryResult(rowsAffected = 0, statusMessage = "OK", rows = rows)
                }

                coEvery {
                    transactionConnection.sendPreparedStatement(CommonCommandDao.maxUserEventIdQuery, listOf(1))
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
                    transactionConnection.sendPreparedStatement(
                        CommandDaoImpl.newGateEventCommand,
                        listOf(1, 15, GateEventType.ENTER, enterTime)
                    )
                }.answers {
                    QueryResult(rowsAffected = 1, statusMessage = "OK")
                }

                callback(transactionConnection)
            }
        val dao = CommandDaoImpl(mainConnection)

        val result = dao.processEnter(1, enterTime)
        assertEquals(result, Unit)
    }

    @Test(expected = IllegalArgumentException::class)
    fun<T> nonExistingUserTest() = runBlocking {
        val enterTime = LocalDateTime.parse("1862-04-14T20:00:00")
        val mainConnection = mockk<SuspendingConnection>()
        coEvery { mainConnection.inTransaction(any<suspend (SuspendingConnection) -> T>()) }
            .coAnswers {
                val callback = args[0] as suspend (SuspendingConnection) -> T
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
        val dao = CommandDaoImpl(mainConnection)
        dao.processEnter(1, enterTime)
    }

    @Test(expected = IllegalArgumentException::class)
    fun<T> enterWithNoSubscriptionTest() = runBlocking {
        val enterTime = LocalDateTime.parse("1862-04-14T20:00:00")
        val mainConnection = mockk<SuspendingConnection>()
        coEvery { mainConnection.inTransaction(any<suspend (SuspendingConnection) -> T>()) }
            .coAnswers {
                val callback = args[0] as suspend (SuspendingConnection) -> T
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
                    transactionConnection.sendPreparedStatement(CommandDaoImpl.lastGateEventQuery, listOf(1))
                }.answers {
                    val rows = mockk<ResultSet>()
                    every { rows.size }.returns(0)
                    QueryResult(rowsAffected = 0, statusMessage = "OK", rows = rows)
                }

                callback(transactionConnection)
            }
        val dao = CommandDaoImpl(mainConnection)
        dao.processEnter(1, enterTime)
    }

    @Test(expected = IllegalArgumentException::class)
    fun<T> enterWithIncorrectSubscriptionTest() = runBlocking {
        val enterTime = LocalDateTime.parse("1862-04-14T20:00:00")
        val mainConnection = mockk<SuspendingConnection>()
        coEvery { mainConnection.inTransaction(any<suspend (SuspendingConnection) -> T>()) }
            .coAnswers {
                val callback = args[0] as suspend (SuspendingConnection) -> T
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
                    every { rowData.getAs<LocalDateTime>("end_date") }.returns(
                        LocalDateTime.parse("1862-04-13T20:00:00")
                    )
                    every { rows[0] }.returns(rowData)
                    QueryResult(rowsAffected = 0, statusMessage = "OK", rows = rows)
                }
                callback(transactionConnection)
            }
        val dao = CommandDaoImpl(mainConnection)
        dao.processEnter(1, enterTime)
    }

    @Test(expected = IllegalArgumentException::class)
    fun<T> enterAfterEnter() = runBlocking {
        val enterTime = LocalDateTime.parse("1862-04-14T20:00:00")
        val mainConnection = mockk<SuspendingConnection>()
        coEvery { mainConnection.inTransaction(any<suspend (SuspendingConnection) -> T>()) }
            .coAnswers {
                val callback = args[0] as suspend (SuspendingConnection) -> T
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
                    every { rowData.getAs<LocalDateTime>("end_date") }.returns(
                        LocalDateTime.parse("1862-04-15T20:00:00")
                    )
                    every { rows[0] }.returns(rowData)
                    QueryResult(rowsAffected = 0, statusMessage = "OK", rows = rows)
                }

                coEvery {
                    transactionConnection.sendPreparedStatement(CommandDaoImpl.lastGateEventQuery, listOf(1))
                }.answers {
                    val rows = mockk<ResultSet>()
                    every { rows.size }.returns(1)
                    val rowData = mockk<RowData>()
                    every { rowData.getString("gate_event_type") }.returns("ENTER")
                    every { rowData.getAs<LocalDateTime>("event_timestamp") }.returns(
                        LocalDateTime.parse("1862-04-13T20:00:00")
                    )
                    every { rows[0] }.returns(rowData)
                    QueryResult(rowsAffected = 0, statusMessage = "OK", rows = rows)
                }

                callback(transactionConnection)
            }
        val dao = CommandDaoImpl(mainConnection)
        dao.processEnter(1, enterTime)
    }

    @Test
    fun<T> correctExitTest() = runBlocking {
        val exitTime = LocalDateTime.parse("1862-04-14T20:00:00")
        val enterTime = LocalDateTime.parse("1862-04-13T20:00:00")
        val mainConnection = mockk<SuspendingConnection>()
        coEvery { mainConnection.inTransaction(any<suspend (SuspendingConnection) -> T>()) }
            .coAnswers {
                val callback = args[0] as suspend (SuspendingConnection) -> T
                val transactionConnection = mockk<SuspendingConnection>()

                coEvery {
                    transactionConnection.sendPreparedStatement(CommandDaoImpl.lastGateEventQuery, listOf(1))
                }.answers {
                    val rows = mockk<ResultSet>()
                    every { rows.size }.returns(1)
                    val rowData = mockk<RowData>()
                    every { rowData.getString("gate_event_type") }.returns("ENTER")
                    every { rowData.getAs<LocalDateTime>("event_timestamp") }.returns(enterTime)
                    every { rows[0] }.returns(rowData)
                    QueryResult(rowsAffected = 0, statusMessage = "OK", rows = rows)
                }

                coEvery {
                    transactionConnection.sendPreparedStatement(CommonCommandDao.maxUserEventIdQuery, listOf(1))
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
                    transactionConnection.sendPreparedStatement(
                        CommandDaoImpl.newGateEventCommand,
                        listOf(1, 15, GateEventType.EXIT, exitTime)
                    )
                }.answers {
                    QueryResult(rowsAffected = 1, statusMessage = "OK")
                }

                callback(transactionConnection)
            }
        val dao = CommandDaoImpl(mainConnection)
        val result = dao.processExit(1, exitTime)
        assertEquals(result, enterTime)
    }

    @Test(expected = IllegalArgumentException::class)
    fun<T> exitAfterExitTest() = runBlocking {
        val exitTime = LocalDateTime.parse("1862-04-14T20:00:00")
        val prevExitTime = LocalDateTime.parse("1862-04-13T20:00:00")
        val mainConnection = mockk<SuspendingConnection>()
        coEvery { mainConnection.inTransaction(any<suspend (SuspendingConnection) -> T>()) }
            .coAnswers {
                val callback = args[0] as suspend (SuspendingConnection) -> T
                val transactionConnection = mockk<SuspendingConnection>()

                coEvery {
                    transactionConnection.sendPreparedStatement(CommandDaoImpl.lastGateEventQuery, listOf(1))
                }.answers {
                    val rows = mockk<ResultSet>()
                    every { rows.size }.returns(1)
                    val rowData = mockk<RowData>()
                    every { rowData.getString("gate_event_type") }.returns("EXIT")
                    every { rowData.getAs<LocalDateTime>("event_timestamp") }.returns(prevExitTime)
                    every { rows[0] }.returns(rowData)
                    QueryResult(rowsAffected = 0, statusMessage = "OK", rows = rows)
                }
                callback(transactionConnection)
            }
        val dao = CommandDaoImpl(mainConnection)
        dao.processExit(1, exitTime)
        Unit
    }

    @Test(expected = IllegalArgumentException::class)
    fun<T> exitAfterNothingTest() = runBlocking {
        val exitTime = LocalDateTime.parse("1862-04-14T20:00:00")
        val mainConnection = mockk<SuspendingConnection>()
        coEvery { mainConnection.inTransaction(any<suspend (SuspendingConnection) -> T>()) }
            .coAnswers {
                val callback = args[0] as suspend (SuspendingConnection) -> T
                val transactionConnection = mockk<SuspendingConnection>()

                coEvery {
                    transactionConnection.sendPreparedStatement(CommandDaoImpl.lastGateEventQuery, listOf(1))
                }.answers {
                    val rows = mockk<ResultSet>()
                    every { rows.size }.returns(0)
                    QueryResult(rowsAffected = 0, statusMessage = "OK", rows = rows)
                }
                callback(transactionConnection)
            }
        val dao = CommandDaoImpl(mainConnection)
        dao.processExit(1, exitTime)
        Unit
    }
}
