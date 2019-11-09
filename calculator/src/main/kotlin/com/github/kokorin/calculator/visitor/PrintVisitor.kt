package com.github.kokorin.calculator.visitor

import com.github.kokorin.calculator.io.Printer
import com.github.kokorin.calculator.tokenization.ArithmeticOperationToken
import com.github.kokorin.calculator.tokenization.BracketToken
import com.github.kokorin.calculator.tokenization.NumberToken
import com.github.kokorin.calculator.tokenization.Token

class PrintVisitor(private val printer: Printer) : TokenVisitor {
    override fun visit(tokens: List<Token>) {
        tokens.forEachIndexed { index, token ->
            token.accept(this)
            if (index != tokens.size - 1) {
                printer.write(" ")
            }
        }
        printer.writeLn()
    }

    override fun visit(token: NumberToken) {
        printer.write(token.toString())
    }

    override fun visit(token: BracketToken) {
        printer.write(token.toString())
    }

    override fun visit(token: ArithmeticOperationToken) {
        printer.write(token.toString())
    }
}
