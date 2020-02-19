package com.github.kokorin.rx.dao

import com.github.kokorin.rx.model.Product
import com.github.kokorin.rx.model.User
import com.mongodb.rx.client.Success
import rx.Observable

interface ReactiveWriteDao {
    fun addUser(user: User): Observable<Boolean>

    fun addProduct(product: Product): Observable<Boolean>

    fun deleteAllUsers(): Observable<Success>

    fun deleteAllProducts(): Observable<Success>
}
