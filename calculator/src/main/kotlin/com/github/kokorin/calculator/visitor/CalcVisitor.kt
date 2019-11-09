package com.github.kokorin.calculator.visitor

import com.github.kokorin.calculator.getOrThrow
import com.github.kokorin.calculator.tokenization.*
import java.util.*
import kotlin.IllegalStateException
import kotlin.properties.Delegates

class CalcVisitor : TokenVisitor {
    companion object {
        private val operations: Map<ArithmeticOperationToken, (Int, Int) -> Int> = mapOf(
            Pair(PlusToken, Int::plus),
            Pair(MinusToken, Int::minus),
            Pair(DivToken, Int::div),
            Pair(MulToken, Int::times)
        )
    }

    private val stack: Stack<Int> = Stack()
    private var result by Delegates.notNull<Int>()

    fun getCalcResult(): Int = result

    override fun visit(token: NumberToken) {
        stack.add(token.n)
    }

    override fun visit(token: BracketToken) {
        throw IllegalStateException("Brackets are not supported when calculating expression in RPN")
    }

    override fun visit(token: ArithmeticOperationToken) {
        check(stack.size >= 2) { "Bad RPN: two arguments should be given for arithmetic operation" }
        val a = stack.pop()
        val b = stack.pop()
        val op = operations.getOrThrow(token)
        stack.push(op(b, a))
    }

    override fun visit(tokens: List<Token>) {
        tokens.forEach { it.accept(this) }
        require(stack.size == 1) { "Bad RPN, only one number should remain in stack after calculation" }
        result = stack.pop()
    }
}
