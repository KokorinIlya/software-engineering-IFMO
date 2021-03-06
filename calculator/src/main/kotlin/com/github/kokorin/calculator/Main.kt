package com.github.kokorin.calculator

import com.github.kokorin.calculator.io.ConsolePrinter
import com.github.kokorin.calculator.tokenization.Tokenizer
import com.github.kokorin.calculator.visitor.CalcVisitor
import com.github.kokorin.calculator.visitor.ConvertToRPNVisitor
import com.github.kokorin.calculator.visitor.PrintVisitor

fun main() {
    try {
        val input = readLine() ?: throw IllegalArgumentException("Input string shouldn't be null")

        val tokenizer = Tokenizer()
        val tokens = tokenizer.tokenize(input)

        val printVisitor = PrintVisitor(ConsolePrinter)
        println("Tokens after tokenization:")
        printVisitor.visit(tokens)

        val parseVisitor = ConvertToRPNVisitor()
        parseVisitor.visit(tokens)
        val transformed = parseVisitor.getParsed()

        println("Tokens after transforming to RPN:")
        printVisitor.visit(transformed)

        val calcVisitor = CalcVisitor()
        calcVisitor.visit(transformed)

        println("Result of calculating the expression:")
        print(calcVisitor.getCalcResult())
    } catch (e: Throwable) {
        println("Error while executing program: ${e.message}")
    }
}
