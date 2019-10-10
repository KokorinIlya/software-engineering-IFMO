package com.github.kokorin.products.response

import org.junit.Test
import org.junit.Assert.*
import org.jsoup.Jsoup

class ResponseBuildierTest {
    @Test
    fun testAdd() {
        val responseBuilder = ResponseBuilder("Prefix")
        responseBuilder.addResponseElement("element 1")
        responseBuilder.addResponseElement("element 2")
        val answer = Jsoup.parse(responseBuilder.buildAnswer())
        val expectedAnswer =
            "<html>\n" +
            " <head></head>\n" +
            " <body>\n" +
            "   Prefix element 1\n" +
            "  <br> element 2\n" +
            "  <br> \n" +
            " </body>\n" +
            "</html>"
        assertEquals(answer.toString(), expectedAnswer)
    }

    @Test
    fun testAddEmptyPrefix() {
        val responseBuilder = ResponseBuilder()
        responseBuilder.addResponseElement("element 1")
        responseBuilder.addResponseElement("element 2")
        val answer = Jsoup.parse(responseBuilder.buildAnswer())
        val expectedAnswer =
            "<html>\n" +
            " <head></head>\n" +
            " <body>\n" +
            "   element 1\n" +
            "  <br> element 2\n" +
            "  <br> \n" +
            " </body>\n" +
            "</html>"
        assertEquals(answer.toString(), expectedAnswer)
    }

    @Test
    fun testAddNoElements() {
        val responseBuilder = ResponseBuilder("Prefix")
        val answer = Jsoup.parse(responseBuilder.buildAnswer())
        val expectedAnswer =
            "<html>\n" +
            " <head></head>\n" +
            " <body>\n" +
            "   Prefix \n" +
            " </body>\n" +
            "</html>"
        assertEquals(answer.toString(), expectedAnswer)
    }

    @Test
    fun testAddNoElementsNoPrefix() {
        val responseBuilder = ResponseBuilder()
        val answer = Jsoup.parse(responseBuilder.buildAnswer())
        val expectedAnswer =
            "<html>\n" +
            " <head></head>\n" +
            " <body> \n" +
            " </body>\n" +
            "</html>"
        assertEquals(answer.toString(), expectedAnswer)
    }
}
