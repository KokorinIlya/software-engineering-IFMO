package com.github.kokorin.calculator.visitor

import com.github.kokorin.calculator.tokenization.ArithmeticOperationToken
import com.github.kokorin.calculator.tokenization.BracketToken
import com.github.kokorin.calculator.tokenization.NumberToken
import com.github.kokorin.calculator.tokenization.Token

class PrintVisitor : TokenVisitor {

    override fun visit(tokens: List<Token>) {
        tokens.forEach {
            it.accept(this)
            print(" ")
        }
        println()
    }

    override fun visit(token: NumberToken) {
        print(token.toString())
    }

    override fun visit(token: BracketToken) {
        print(token.toString())
    }

    override fun visit(token: ArithmeticOperationToken) {
        print(token.toString())
    }
}
