package personalapi.model

import kotlinx.serialization.Serializable

@Serializable
data class CompanyStocks(val company: String, val count: Int, val price: Int)
