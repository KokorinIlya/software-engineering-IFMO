package com.github.kokorin.calculator.visitor

import com.github.kokorin.calculator.io.Printer
import com.github.kokorin.calculator.tokenization.*
import org.junit.Test
import org.junit.Assert.*

class PrintVisitorTest {
    @Test
    fun simpleTest() {
        val tokens = listOf(
            NumberToken(4),
            NumberToken(5),
            PlusToken,
            MinusToken,
            MulToken,
            DivToken,
            LeftBracketToken,
            RightBracketToken
        )
        val printer = object : Printer {
            val stringBuilder = StringBuilder()

            override fun write(s: String) {
                stringBuilder.append(s)
            }

            override fun writeLn() {
                stringBuilder.append("\n")
            }
        }

        val printVisitor = PrintVisitor(printer)
        printVisitor.visit(tokens)

        assertEquals(printer.stringBuilder.toString(), "NUMBER(4) NUMBER(5) PLUS MINUS MUL DIV LEFT RIGHT\n")
    }
}
