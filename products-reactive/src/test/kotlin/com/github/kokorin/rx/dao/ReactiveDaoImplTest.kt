package com.github.kokorin.rx.dao

import com.github.kokorin.rx.currency.CurrencyConverter
import com.github.kokorin.rx.model.Currency
import com.github.kokorin.rx.model.Product
import com.github.kokorin.rx.model.User
import com.github.kokorin.rx.model.UserListingItem
import com.github.kokorin.rx.providers.Provider
import com.mongodb.rx.client.FindObservable
import com.mongodb.rx.client.MongoCollection
import com.mongodb.rx.client.MongoDatabase
import com.mongodb.rx.client.Success
import org.bson.Document
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Matchers
import org.mockito.Mockito.*
import rx.Observable
import java.lang.IllegalArgumentException

@SuppressWarnings("unchecked")
class ReactiveDaoImplTest {
    @Test
    fun testGetUser() {
        val db = mock(MongoDatabase::class.java)
        val collection = mock(MongoCollection::class.java) as MongoCollection<Document>
        `when`(db.getCollection("users")).thenReturn(collection)
        val userId = 42L
        val findResult = mock(FindObservable::class.java) as FindObservable<Document>
        `when`(collection.find(Matchers.any())).then {
            val firstArg = it.arguments[0]
            if (firstArg.toString() == "Filter{fieldName='id', value=42}") {
                findResult
            } else {
                throw IllegalArgumentException("Incorrect argument")
            }

        }
        val document = Document(
            mutableMapOf(
                Pair("id", userId),
                Pair("currency", Currency.RUB.toString())
            )
        )
        `when`(findResult.toObservable()).thenReturn(Observable.just(document))
        val currencyConversion = mock(Provider::class.java) as Provider<CurrencyConverter>
        val dao = ReactiveDaoImpl(db, currencyConversion)
        val response = dao.getUserById(userId).toBlocking().iterator.asSequence().toList()
        val expectedResponse = listOf(
            User(id = 42L, preferredCurrency = Currency.RUB)
        )
        assertTrue(response == expectedResponse)
    }

    @Test
    fun testGetNonExistingUser() {
        val db = mock(MongoDatabase::class.java)
        val collection = mock(MongoCollection::class.java) as MongoCollection<Document>
        `when`(db.getCollection("users")).thenReturn(collection)
        val userId = 42L
        val findResult = mock(FindObservable::class.java) as FindObservable<Document>
        `when`(collection.find(Matchers.any())).then {
            val firstArg = it.arguments[0]
            if (firstArg.toString() == "Filter{fieldName='id', value=42}") {
                findResult
            } else {
                throw IllegalArgumentException("Incorrect argument")
            }

        }
        `when`(findResult.toObservable()).thenReturn(Observable.empty())
        val currencyConversion = mock(Provider::class.java) as Provider<CurrencyConverter>
        val dao = ReactiveDaoImpl(db, currencyConversion)
        val response = dao.getUserById(userId).toBlocking().iterator.asSequence().toList()
        val expectedResponse = listOf<User>()
        assertTrue(response == expectedResponse)
    }

    @Test
    fun testGetProduct() {
        val db = mock(MongoDatabase::class.java)
        val collection = mock(MongoCollection::class.java) as MongoCollection<Document>
        `when`(db.getCollection("products")).thenReturn(collection)
        val productId = 42L
        val findResult = mock(FindObservable::class.java) as FindObservable<Document>
        `when`(collection.find(Matchers.any())).then {
            val firstArg = it.arguments[0]
            if (firstArg.toString() == "Filter{fieldName='id', value=42}") {
                findResult
            } else {
                throw IllegalArgumentException("Incorrect argument")
            }

        }
        val document = Document(
            mutableMapOf(
                Pair("id", productId),
                Pair("name", "product_name"),
                Pair("price", 2.82),
                Pair("currency", Currency.RUB.toString())
            )
        )
        `when`(findResult.toObservable()).thenReturn(Observable.just(document))
        val currencyConversion = mock(Provider::class.java) as Provider<CurrencyConverter>
        val dao = ReactiveDaoImpl(db, currencyConversion)
        val response = dao.getProductById(productId).toBlocking().iterator.asSequence().toList()
        val expectedResponse = listOf(
            Product(id = productId, name = "product_name", price = 2.82, currency = Currency.RUB)
        )
        assertTrue(response == expectedResponse)
    }

    @Test
    fun testGetNonExistingProduct() {
        val db = mock(MongoDatabase::class.java)
        val collection = mock(MongoCollection::class.java) as MongoCollection<Document>
        `when`(db.getCollection("products")).thenReturn(collection)
        val productId = 42L
        val findResult = mock(FindObservable::class.java) as FindObservable<Document>
        `when`(collection.find(Matchers.any())).then {
            val firstArg = it.arguments[0]
            if (firstArg.toString() == "Filter{fieldName='id', value=42}") {
                findResult
            } else {
                throw IllegalArgumentException("Incorrect argument")
            }

        }
        `when`(findResult.toObservable()).thenReturn(Observable.empty())
        val currencyConversion = mock(Provider::class.java) as Provider<CurrencyConverter>
        val dao = ReactiveDaoImpl(db, currencyConversion)
        val response = dao.getProductById(productId).toBlocking().iterator.asSequence().toList()
        val expectedResponse = listOf<Product>()
        assertTrue(response == expectedResponse)
    }

    @Test
    fun testAddNewUser() {
        val db = mock(MongoDatabase::class.java)
        val collection = mock(MongoCollection::class.java) as MongoCollection<Document>
        val collectionToAdd = mock(MongoCollection::class.java) as MongoCollection<Document>
        `when`(db.getCollection("users")).thenReturn(collection).thenReturn(collectionToAdd)
        val userId = 42L
        val findResult = mock(FindObservable::class.java) as FindObservable<Document>
        `when`(collection.find(Matchers.any())).then {
            val firstArg = it.arguments[0]
            if (firstArg.toString() == "Filter{fieldName='id', value=42}") {
                findResult
            } else {
                throw IllegalArgumentException("Incorrect argument")
            }

        }
        `when`(findResult.toObservable()).thenReturn(Observable.empty())
        val currencyConversion = mock(Provider::class.java) as Provider<CurrencyConverter>
        val dao = ReactiveDaoImpl(db, currencyConversion)

        val document = Document(
            mapOf(
                Pair("id", userId),
                Pair("currency", Currency.RUB.toString())
            )
        )
        `when`(collectionToAdd.insertOne(document)).thenReturn(Observable.just(Success.SUCCESS))

        val response = dao.addUser(User(userId, Currency.RUB)).toBlocking().iterator.asSequence().toList()
        val expectedResponse = listOf(true)
        assertTrue(response == expectedResponse)
    }

    @Test
    fun testAddExistingUser() {
        val db = mock(MongoDatabase::class.java)
        val collection = mock(MongoCollection::class.java) as MongoCollection<Document>
        `when`(db.getCollection("users")).thenReturn(collection)
        val userId = 42L
        val findResult = mock(FindObservable::class.java) as FindObservable<Document>
        `when`(collection.find(Matchers.any())).then {
            val firstArg = it.arguments[0]
            if (firstArg.toString() == "Filter{fieldName='id', value=42}") {
                findResult
            } else {
                throw IllegalArgumentException("Incorrect argument")
            }
        }
        val document = Document(
            mapOf(
                Pair("id", userId),
                Pair("currency", Currency.RUB.toString())
            )
        )
        `when`(findResult.toObservable()).thenReturn(Observable.just(document))
        val currencyConversion = mock(Provider::class.java) as Provider<CurrencyConverter>
        val dao = ReactiveDaoImpl(db, currencyConversion)
        val response = dao.addUser(User(userId, Currency.RUB)).toBlocking().iterator.asSequence().toList()
        val expectedResponse = listOf(false)
        assertTrue(response == expectedResponse)
    }

    @Test
    fun testAddNewProduct() {
        val db = mock(MongoDatabase::class.java)
        val collection = mock(MongoCollection::class.java) as MongoCollection<Document>
        val collectionToAdd = mock(MongoCollection::class.java) as MongoCollection<Document>
        `when`(db.getCollection("products")).thenReturn(collection).thenReturn(collectionToAdd)
        val productId = 42L
        val findResult = mock(FindObservable::class.java) as FindObservable<Document>
        `when`(collection.find(Matchers.any())).then {
            val firstArg = it.arguments[0]
            if (firstArg.toString() == "Filter{fieldName='id', value=42}") {
                findResult
            } else {
                throw IllegalArgumentException("Incorrect argument")
            }

        }
        `when`(findResult.toObservable()).thenReturn(Observable.empty())
        val currencyConversion = mock(Provider::class.java) as Provider<CurrencyConverter>
        val dao = ReactiveDaoImpl(db, currencyConversion)

        val document = Document(
            mapOf(
                Pair("id", productId),
                Pair("name", "some_name"),
                Pair("price", 2.82),
                Pair("currency", Currency.RUB.toString())
            )
        )
        `when`(collectionToAdd.insertOne(document)).thenReturn(Observable.just(Success.SUCCESS))

        val response = dao
            .addProduct(Product(productId, "some_name", 2.82, Currency.RUB))
            .toBlocking()
            .iterator
            .asSequence()
            .toList()
        val expectedResponse = listOf(true)
        assertTrue(response == expectedResponse)
    }

    @Test
    fun testAddExistingProduct() {
        val db = mock(MongoDatabase::class.java)
        val collection = mock(MongoCollection::class.java) as MongoCollection<Document>
        `when`(db.getCollection("products")).thenReturn(collection)
        val productId = 42L
        val findResult = mock(FindObservable::class.java) as FindObservable<Document>
        `when`(collection.find(Matchers.any())).then {
            val firstArg = it.arguments[0]
            if (firstArg.toString() == "Filter{fieldName='id', value=42}") {
                findResult
            } else {
                throw IllegalArgumentException("Incorrect argument")
            }
        }
        val document = Document(
            mapOf(
                Pair("id", productId),
                Pair("name", "some_name"),
                Pair("price", 2.82),
                Pair("currency", Currency.RUB.toString())
            )
        )
        `when`(findResult.toObservable()).thenReturn(Observable.just(document))
        val currencyConversion = mock(Provider::class.java) as Provider<CurrencyConverter>
        val dao = ReactiveDaoImpl(db, currencyConversion)
        val response =
            dao.addProduct(Product(productId, "some_name", 2.82, Currency.RUB))
                .toBlocking()
                .iterator
                .asSequence()
                .toList()
        val expectedResponse = listOf(false)
        assertTrue(response == expectedResponse)
    }

    @Test
    fun testListForExistingUser() {
        val db = mock(MongoDatabase::class.java)
        val usersCollection = mock(MongoCollection::class.java) as MongoCollection<Document>
        `when`(db.getCollection("users")).thenReturn(usersCollection)
        val userId = 42L
        val userFindResult = mock(FindObservable::class.java) as FindObservable<Document>
        `when`(usersCollection.find(Matchers.any())).then {
            val firstArg = it.arguments[0]
            if (firstArg.toString() == "Filter{fieldName='id', value=42}") {
                userFindResult
            } else {
                throw IllegalArgumentException("Incorrect argument")
            }

        }
        val userDocument = Document(
            mutableMapOf(
                Pair("id", userId),
                Pair("currency", Currency.RUB.toString())
            )
        )
        `when`(userFindResult.toObservable()).thenReturn(Observable.just(userDocument))
        val currencyConversion = mock(Provider::class.java) as Provider<CurrencyConverter>

        val conversionMap = mapOf(
            Pair(Pair(Currency.RUB, Currency.RUB), 1.0),
            Pair(Pair(Currency.USD, Currency.USD), 1.0),
            Pair(Pair(Currency.EUR, Currency.EUR), 1.0),

            Pair(Pair(Currency.RUB, Currency.USD), 0.5),
            Pair(Pair(Currency.RUB, Currency.EUR), 0.25),

            Pair(Pair(Currency.USD, Currency.RUB), 2.0),
            Pair(Pair(Currency.USD, Currency.EUR), 0.5),

            Pair(Pair(Currency.EUR, Currency.RUB), 4.0),
            Pair(Pair(Currency.EUR, Currency.USD), 2.0)
        )
        `when`(currencyConversion.get()).thenReturn(CurrencyConverter(conversionMap.toMap()))

        val productsCollection = mock(MongoCollection::class.java) as MongoCollection<Document>
        `when`(db.getCollection("products")).thenReturn(productsCollection)
        val productsFindResult = mock(FindObservable::class.java) as FindObservable<Document>
        `when`(productsCollection.find()).thenReturn(productsFindResult)
        val productDocument1 = Document(
            mapOf(
                Pair("id", 1L),
                Pair("name", "name_1"),
                Pair("price", 1.0),
                Pair("currency", Currency.USD.toString())
            )
        )
        val productDocument2 = Document(
            mapOf(
                Pair("id", 2L),
                Pair("name", "name_2"),
                Pair("price", 2.0),
                Pair("currency", Currency.EUR.toString())
            )
        )
        `when`(productsFindResult.toObservable()).thenReturn(Observable.just(productDocument1, productDocument2))

        val dao = ReactiveDaoImpl(db, currencyConversion)
        val response = dao.getProductsForUser(userId).toBlocking().iterator.asSequence().toList()
        val expectedResponse = listOf(
            UserListingItem(id = 1L, name = "name_1", price = 2.0),
            UserListingItem(id = 2L, name = "name_2", price = 8.0)
        )
        assertTrue(response == expectedResponse)
    }

    @Test
    fun testListForNonExistingUser() {
        val db = mock(MongoDatabase::class.java)
        val usersCollection = mock(MongoCollection::class.java) as MongoCollection<Document>
        `when`(db.getCollection("users")).thenReturn(usersCollection)
        val userId = 42L
        val userFindResult = mock(FindObservable::class.java) as FindObservable<Document>
        `when`(usersCollection.find(Matchers.any())).then {
            val firstArg = it.arguments[0]
            if (firstArg.toString() == "Filter{fieldName='id', value=42}") {
                userFindResult
            } else {
                throw IllegalArgumentException("Incorrect argument")
            }

        }
        `when`(userFindResult.toObservable()).thenReturn(Observable.empty())
        val currencyConversion = mock(Provider::class.java) as Provider<CurrencyConverter>
        val dao = ReactiveDaoImpl(db, currencyConversion)
        val response = dao.getProductsForUser(userId).toBlocking().iterator.asSequence().toList()
        val expectedResponse = listOf<UserListingItem>()
        assertTrue(response == expectedResponse)
    }
}
