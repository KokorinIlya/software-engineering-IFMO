package com.github.kokorin.calculator.visitor

import com.github.kokorin.calculator.utils.getOrThrow
import com.github.kokorin.calculator.tokenization.*
import java.util.*
import kotlin.collections.ArrayList

class ConvertToRPNVisitor : TokenVisitor {
    companion object {
        private val priorities: Map<ArithmeticOperationToken, Int> = mapOf(
            Pair(PlusToken, 0),
            Pair(MinusToken, 0),
            Pair(DivToken, 1),
            Pair(MulToken, 1)
        )
    }

    private val stack: Stack<Token> = Stack()
    private val result: ArrayList<EvaluationToken> = ArrayList()

    fun getParsed(): List<EvaluationToken> {
        return result.toList()
    }

    override fun visit(token: NumberToken) {
        result.add(token)
    }

    override fun visit(token: BracketToken) {
        when (token) {
            is LeftBracketToken -> stack.push(token)
            is RightBracketToken -> {
                loop@ while (!stack.empty()) {
                    when (val lastToken = stack.peek()) {
                        is LeftBracketToken -> {
                            stack.pop()
                            break@loop
                        }
                        is ArithmeticOperationToken -> {
                            result.add(lastToken)
                            stack.pop()
                        }
                        is RightBracketToken, is NumberToken ->
                            throw IllegalStateException("Wrong stack state: state is ${stack.toList()}")
                    }
                }
            }
        }
    }

    override fun visit(token: ArithmeticOperationToken) {
        while (!stack.empty()) {
            val lastToken = stack.peek()
            if (lastToken is ArithmeticOperationToken &&
                priorities.getOrThrow(token) <= priorities.getOrThrow(lastToken)
            ) {
                result.add(lastToken)
                stack.pop()
            } else {
                break
            }
        }
        stack.push(token)
    }

    override fun visit(tokens: List<Token>) {
        tokens.forEach { it.accept(this) }

        while (!stack.empty()) {
            val lastToken = stack.peek()
            if (lastToken is ArithmeticOperationToken) {
                result.add(lastToken)
                stack.pop()
            } else {
                throw IllegalStateException(
                    "Not only operations in the end of transformation," +
                            " no matching closing bracket, stack is ${stack.toList()}"
                )
            }
        }
    }
}
