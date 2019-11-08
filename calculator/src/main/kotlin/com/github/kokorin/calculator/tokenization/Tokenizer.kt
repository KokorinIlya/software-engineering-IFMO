package com.github.kokorin.calculator.tokenization

import java.lang.UnsupportedOperationException

class Tokenizer {
    private val START_STATE = StartState()
    private val EOF_STATE = EOFState()

    private var tokenizerState: TokenizerState = START_STATE;
    private val tokens: ArrayList<Token> = ArrayList()

    private fun processSymbol(c: Char) {
        tokenizerState.process(c)
    }

    private fun processEOF() {
        tokenizerState.processEOF()
    }

    fun tokenize(s: String): List<Token> {
        require(tokenizerState == START_STATE) { "Tokenizer should be in start state to process input" }
        s.forEach { processSymbol(it) }
        processEOF()
        return tokens.toList()
    }

    private abstract inner class TokenizerState {
        abstract fun process(c: Char)

        open fun processEOF() {
            this@Tokenizer.tokenizerState = EOF_STATE
        }
    }

    private inner class EOFState : TokenizerState() {
        override fun process(c: Char) {
            throw UnsupportedOperationException("Cannot process input in EOF state")
        }

        override fun processEOF() {}

    }

    private inner class NumericState : TokenizerState() {
        private var number = 0

        override fun processEOF() {
            this@Tokenizer.tokens.add(NumberToken(number))
            super.processEOF()
        }

        override fun process(c: Char) {
            when (c) {
                in '0'..'9' -> {
                    number = number * 10 + (c - '0')
                }
                else -> {
                    this@Tokenizer.tokens.add(NumberToken(number))
                    this@Tokenizer.tokenizerState = START_STATE
                    this@Tokenizer.processSymbol(c)
                }
            }
        }

    }

    private inner class StartState : TokenizerState() {
        override fun process(c: Char) {
            when (c) {
                '(' -> this@Tokenizer.tokens.add(LeftBracketToken)
                ')' -> this@Tokenizer.tokens.add(RightBracketToken)
                '+' -> this@Tokenizer.tokens.add(PlusToken)
                '-' -> this@Tokenizer.tokens.add(MinusToken)
                '*' -> this@Tokenizer.tokens.add(MulToken)
                '/' -> this@Tokenizer.tokens.add(DivToken)
                in '0'..'9' -> {
                    this@Tokenizer.tokenizerState = NumericState()
                    this@Tokenizer.processSymbol(c)
                }
                else -> {
                    require(c.isWhitespace()) { "unexpected char $c" }
                }
            }
        }
    }
}
