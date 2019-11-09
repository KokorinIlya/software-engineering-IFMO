package com.github.kokorin.tokenization

import com.github.kokorin.calculator.tokenization.*
import org.junit.Test
import org.junit.Assert.*

class TokenizerTest {
    @Test
    fun testSimple() {
        val tokenizer = Tokenizer()
        val s = "3 + 7"
        val tokens = tokenizer.tokenize(s)
        assertTrue(tokens == listOf(NumberToken(3), PlusToken, NumberToken(7)))
    }

    @Test
    fun withoutWhitespacesTest() {
        val tokenizer = Tokenizer()
        val s = "3+7/2"
        val tokens = tokenizer.tokenize(s)
        assertTrue(tokens == listOf(NumberToken(3), PlusToken, NumberToken(7), DivToken, NumberToken(2)))
    }

    @Test
    fun bracesTest() {
        val tokenizer = Tokenizer()
        val s = "(3 + 7) / 2"
        val tokens = tokenizer.tokenize(s)
        assertTrue(
            tokens ==
                    listOf(
                        LeftBracketToken,
                        NumberToken(3),
                        PlusToken,
                        NumberToken(7),
                        RightBracketToken,
                        DivToken,
                        NumberToken(2)
                    )
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun incorrectTest() {
        val tokenizer = Tokenizer()
        val s = "3 + 7 a"
        val tokens = tokenizer.tokenize(s)
    }
}
