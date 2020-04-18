package personalapi.http

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.HttpStatusCode

abstract class AbstractHttpClient {
    protected abstract val client: HttpClient

    protected suspend fun doGet(url: String): String {
        val response = client.get<HttpResponse>(url)
        val responseText = response.readText()
        check(response.status == HttpStatusCode.OK) {
            "Expected 200 OK, but received ${response.status}: $responseText"
        }
        return responseText
    }
}
