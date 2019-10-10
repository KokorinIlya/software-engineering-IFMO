package com.github.kokorin.products.connection

import java.sql.Connection

interface ConnectionProvider {
    fun getConnection(): Connection
}
