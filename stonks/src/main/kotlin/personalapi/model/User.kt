package personalapi.model

data class User(val name: String, val balance: Int, val stocksAmountByCompanyName: Map<String, Int>)
