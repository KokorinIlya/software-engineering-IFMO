package com.github.kokorin.rx.dao

import com.github.kokorin.rx.model.Product
import com.github.kokorin.rx.model.User
import com.github.kokorin.rx.model.UserListingItem
import rx.Observable

interface ReactiveReadDao {
    fun getUserById(userId: Long): Observable<User>

    fun getProductsForUser(userId: Long): Observable<UserListingItem>

    fun getProductById(productId: Long): Observable<Product>
}
