package com.github.kokorin.fitness.manager.query

sealed class Query

data class GetUserQuery(val uid: Int) : Query()
