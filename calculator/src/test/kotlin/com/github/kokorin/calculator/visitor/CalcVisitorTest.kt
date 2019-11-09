package com.github.kokorin.calculator.visitor

import com.github.kokorin.calculator.tokenization.DivToken
import com.github.kokorin.calculator.tokenization.MinusToken
import com.github.kokorin.calculator.tokenization.NumberToken
import com.github.kokorin.calculator.tokenization.PlusToken
import org.junit.Test
import org.junit.Assert.*

class CalcVisitorTest {
    @Test
    fun simpleTest() {
        val calcVisitor = CalcVisitor()
        val tokens = listOf(
            NumberToken(2),
            NumberToken(3),
            PlusToken
        )
        calcVisitor.visit(tokens)
        assertEquals(calcVisitor.getCalcResult(), 5)
    }

    @Test
    fun complexExpressionTest() {
        val calcVisitor = CalcVisitor()
        val tokens = listOf(
            NumberToken(2),
            NumberToken(3),
            NumberToken(5),
            PlusToken,
            MinusToken,
            NumberToken(2),
            DivToken
        )
        calcVisitor.visit(tokens)
        assertEquals(calcVisitor.getCalcResult(), -3)
    }

    @Test(expected = IllegalStateException::class)
    fun errorTest() {
        val calcVisitor = CalcVisitor()
        val tokens = listOf(
            NumberToken(2),
            PlusToken
        )
        calcVisitor.visit(tokens)
    }

    @Test(expected = IllegalArgumentException::class)
    fun noOperationsTest() {
        val calcVisitor = CalcVisitor()
        val tokens = listOf(
            NumberToken(2),
            NumberToken(3),
            NumberToken(5),
            PlusToken
        )
        calcVisitor.visit(tokens)
    }
}
