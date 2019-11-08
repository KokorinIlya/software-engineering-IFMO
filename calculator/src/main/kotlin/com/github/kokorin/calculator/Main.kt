package com.github.kokorin.calculator

import com.github.kokorin.calculator.tokenization.Tokenizer
import com.github.kokorin.calculator.visitor.PrintVisitor

fun main() {
    val input = readLine() ?: throw IllegalArgumentException("Input string shouldn't be null")
    val tokenizer = Tokenizer()
    val tokens = tokenizer.tokenize(input)

    val printVisitor = PrintVisitor()
    printVisitor.visit(tokens)

}
