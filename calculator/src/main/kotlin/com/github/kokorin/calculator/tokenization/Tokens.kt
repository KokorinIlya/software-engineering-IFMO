package com.github.kokorin.calculator.tokenization

import com.github.kokorin.calculator.visitor.TokenVisitor

sealed class Token {
    abstract fun accept(tokenVisitor: TokenVisitor)
}

sealed class EvaluationToken : Token()

sealed class ArithmeticOperationToken : EvaluationToken() {
    override fun accept(tokenVisitor: TokenVisitor) {
        tokenVisitor.visit(this)
    }
}

object PlusToken : ArithmeticOperationToken() {
    override fun toString(): String {
        return "PLUS"
    }
}

object MinusToken : ArithmeticOperationToken() {
    override fun toString(): String {
        return "MINUS"
    }
}

object MulToken : ArithmeticOperationToken() {
    override fun toString(): String {
        return "MUL"
    }
}

object DivToken : ArithmeticOperationToken() {
    override fun toString(): String {
        return "DIV"
    }
}

data class NumberToken(val n: Int) : EvaluationToken() {
    override fun accept(tokenVisitor: TokenVisitor) {
        tokenVisitor.visit(this)
    }

    override fun toString(): String {
        return "NUMBER($n)"
    }
}

sealed class BracketToken : Token() {
    override fun accept(tokenVisitor: TokenVisitor) {
        tokenVisitor.visit(this)
    }
}

object LeftBracketToken : BracketToken() {
    override fun toString(): String {
        return "LEFT"
    }
}

object RightBracketToken : BracketToken() {
    override fun toString(): String {
        return "RIGHT"
    }
}

