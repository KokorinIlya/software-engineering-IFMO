package com.github.kokorin.products.response

class ResponseBuilder(private val header: String = "") {
    private val curAnswer = StringBuilder()

    fun addResponseElement(element: String) {
        curAnswer.append("$element<br>\n")
    }

    fun buildAnswer(): String {
        return "<html><body>\n$header\n${curAnswer.toString()}</body></html>"
    }
}
