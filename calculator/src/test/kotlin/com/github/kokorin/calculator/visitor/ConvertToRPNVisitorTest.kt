package com.github.kokorin.calculator.visitor

import com.github.kokorin.calculator.tokenization.*
import org.junit.Test
import org.junit.Assert.*

class ConvertToRPNVisitorTest {
    @Test
    fun simpleTest() {
        val tokens = listOf(
            NumberToken(2),
            PlusToken,
            NumberToken(3)
        )
        val visitor = ConvertToRPNVisitor()
        visitor.visit(tokens)
        assertTrue(
            visitor.getParsed() == listOf(
                NumberToken(2),
                NumberToken(3),
                PlusToken
            )
        )
    }

    @Test
    fun bracketsTest() {
        val tokens = listOf(
            LeftBracketToken,
            NumberToken(2),
            PlusToken,
            NumberToken(3),
            MulToken,
            NumberToken(5),
            RightBracketToken,
            DivToken,
            NumberToken(1)
        )
        val visitor = ConvertToRPNVisitor()
        visitor.visit(tokens)
        assertTrue(
            visitor.getParsed() == listOf(
                NumberToken(2),
                NumberToken(3),
                NumberToken(5),
                MulToken,
                PlusToken,
                NumberToken(1),
                DivToken
            )
        )
    }

    @Test
    fun prioritiesTest() {
        val tokens = listOf(
            NumberToken(2),
            PlusToken,
            NumberToken(3),
            MulToken,
            NumberToken(5),
            DivToken,
            NumberToken(1),
            PlusToken,
            NumberToken(2)
        )
        val visitor = ConvertToRPNVisitor()
        visitor.visit(tokens)
        assertTrue(
            visitor.getParsed() == listOf(
                NumberToken(2),
                NumberToken(3),
                NumberToken(5),
                MulToken,
                NumberToken(1),
                DivToken,
                PlusToken,
                NumberToken(2),
                PlusToken
            )
        )
    }

    @Test(expected = IllegalStateException::class)
    fun noMatchingBracketTest() {
        val tokens = listOf(
            LeftBracketToken,
            NumberToken(2),
            PlusToken,
            NumberToken(3)
        )
        val visitor = ConvertToRPNVisitor()
        visitor.visit(tokens)
    }
}
