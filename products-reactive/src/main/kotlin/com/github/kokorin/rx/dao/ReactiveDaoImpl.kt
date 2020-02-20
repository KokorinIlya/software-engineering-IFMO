package com.github.kokorin.rx.dao

import com.github.kokorin.rx.currency.CurrencyConverter
import com.github.kokorin.rx.model.Currency
import com.github.kokorin.rx.model.Product
import com.github.kokorin.rx.model.User
import com.github.kokorin.rx.model.UserListingItem
import com.github.kokorin.rx.providers.Provider
import com.mongodb.client.model.Filters
import com.mongodb.rx.client.MongoDatabase
import com.mongodb.rx.client.Success
import rx.Observable
import rx.Scheduler
import rx.schedulers.Schedulers
import org.bson.Document

class ReactiveDaoImpl(
    private val db: MongoDatabase,
    private val currencyConverterProvider: Provider<CurrencyConverter>
) : ReactiveDao {
    override fun getUserById(userId: Long): Observable<User> {
        return db.getCollection("users")
            .find(Filters.eq("id", userId))
            .toObservable()
            .map {
                val currency = Currency.fromString(it.getString("currency"))
                User(userId, currency)
            }.subscribeOn(scheduler)
    }

    override fun getProductById(productId: Long): Observable<Product> {
        return db.getCollection("products")
            .find(Filters.eq("id", productId))
            .toObservable()
            .map {
                val name = it.getString("name")
                val currency = Currency.fromString(it.getString("currency"))
                val price = it.getDouble("price")
                Product(productId, name, price, currency)
            }.subscribeOn(scheduler)
    }

    override fun getProductsForUser(userId: Long): Observable<UserListingItem> {
        val converter = currencyConverterProvider.get()
        return getUserById(userId).flatMap { user ->
            db.getCollection("products")
                .find()
                .toObservable()
                .map {
                    val itemId = it.getLong("id")
                    val name = it.getString("name")
                    val currency = Currency.fromString(it.getString("currency"))
                    val price = it.getDouble("price")
                    val convertedPrice =
                        converter.convert(amount = price, from = currency, to = user.preferredCurrency)
                    UserListingItem(itemId, name, convertedPrice)
                }.subscribeOn(scheduler)
        }
    }

    override fun addUser(user: User): Observable<Boolean> {
        return getUserById(user.id).singleOrDefault(null).flatMap { userOpt ->
            if (userOpt != null) {
                Observable.just(false)
            } else {
                val document = Document(
                    mutableMapOf(
                        Pair("id", user.id),
                        Pair("currency", user.preferredCurrency.toString())
                    )
                )
                db.getCollection("users")
                    .insertOne(document)
                    .asObservable()
                    .isEmpty
                    .map { !it }
            }
        }
    }

    override fun addProduct(product: Product): Observable<Boolean> {
        return getProductById(product.id).singleOrDefault(null).flatMap { productOpt ->
            if (productOpt != null) {
                Observable.just(false)
            } else {
                val document = Document(
                    mutableMapOf(
                        Pair("id", product.id),
                        Pair("name", product.name),
                        Pair("price", product.price),
                        Pair("currency", product.currency.toString())
                    )
                )
                db.getCollection("products")
                    .insertOne(document)
                    .asObservable()
                    .isEmpty
                    .map { !it }
            }
        }

    }

    override fun deleteAllUsers(): Observable<Success> {
        return db.getCollection("users").drop().subscribeOn(scheduler)
    }

    override fun deleteAllProducts(): Observable<Success> {
        return db.getCollection("products").drop().subscribeOn(scheduler)
    }


    companion object {
        val scheduler: Scheduler = Schedulers.io()
    }
}
