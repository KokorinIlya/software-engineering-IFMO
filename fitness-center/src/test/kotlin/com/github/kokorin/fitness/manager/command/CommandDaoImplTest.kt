package com.github.kokorin.fitness.manager.command

import com.github.jasync.sql.db.QueryResult
import com.github.jasync.sql.db.ResultSet
import com.github.jasync.sql.db.RowData
import com.github.jasync.sql.db.SuspendingConnection
import org.junit.Test
import org.junit.Assert.*
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking

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
        val dao = CommandDaoImpl(mainConnection, poolSize = 10)
        val newUid = dao.registerNewUser()
        assertEquals(newUid, 1)
    }
}
