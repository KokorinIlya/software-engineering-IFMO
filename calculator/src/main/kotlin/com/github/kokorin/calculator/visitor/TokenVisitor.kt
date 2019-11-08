package com.github.kokorin.calculator.visitor

import com.github.kokorin.calculator.tokenization.ArithmeticOperationToken
import com.github.kokorin.calculator.tokenization.BracketToken
import com.github.kokorin.calculator.tokenization.NumberToken
import com.github.kokorin.calculator.tokenization.Token

interface TokenVisitor {
    fun visit(token: NumberToken)
    fun visit(token: BracketToken)
    fun visit(token: ArithmeticOperationToken)
    fun visit(tokens: List<Token>)
}
