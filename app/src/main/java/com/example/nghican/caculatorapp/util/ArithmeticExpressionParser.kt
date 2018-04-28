package com.example.nghican.caculatorapp.util

import java.math.MathContext
import java.math.RoundingMode
import java.util.*

/**
 * This class implement Shunting-yard algorithm from https://en.wikipedia.org/wiki/Shunting-yard_algorithm
 *
 */
object ArithmeticExpressionParser {

    enum class Operator(val token: String) {
        SUM("+"),
        SUB("-"),
        TIMES("*"),
        DIVIDE("/"),
        PARENTHESES_OPEN("("),
        PARENTHESES_CLOSE(")");

        companion object {
            fun fromToken(token: String): Operator {
                for (operator in values()) {
                    if (operator.token == token) {
                        return operator
                    }
                }

                throw IllegalArgumentException("Do not support this token")
            }
        }

        fun precedence(): Int {
            return when (this) {

                PARENTHESES_OPEN -> 1

                SUM, SUB -> 2

                TIMES, DIVIDE -> 3

                else -> -1
            }
        }
    }

    private fun isNumeric(s: String): Boolean {
        return try {
            s.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }


    private fun isOperator(s: String): Boolean {
        return try {
            Operator.fromToken(s)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    fun evaluate(tokens: List<String>): Double {
        val rpn = infixToRpn(tokens)
        return rpnToDouble(rpn)
    }

    private fun infixToRpn(tokens: List<String>): List<String> {

        val output = arrayListOf<String>()
        val stackOperator = Stack<Operator>()
        tokens.forEach {
            when {

                isNumeric(it) -> output.add(it)

                isOperator(it) -> {
                    val operator = Operator.fromToken(it)
                    var topOperator = if (stackOperator.isNotEmpty()) stackOperator.peek() else null

                    when (operator) {

                        Operator.PARENTHESES_CLOSE -> {
                            while (topOperator != null && topOperator != Operator.PARENTHESES_OPEN) {
                                output.add(topOperator.token)
                                stackOperator.pop()
                                topOperator = if (stackOperator.isNotEmpty()) stackOperator.peek() else null
                            }

                            if (stackOperator.isNotEmpty()) {
                                stackOperator.pop()
                            }
                        }

                        Operator.PARENTHESES_OPEN -> stackOperator.push(operator)

                        else -> {
                            while (topOperator != null && topOperator.precedence() > operator.precedence()) {
                                output.add(topOperator.token)
                                stackOperator.pop()
                                topOperator = if (stackOperator.isNotEmpty()) stackOperator.peek() else null
                            }

                            stackOperator.push(operator)
                        }
                    }
                }

                else -> throw IllegalArgumentException("Expression is not valid")
            }
        }

        while (stackOperator.isNotEmpty()) {
            output.add(stackOperator.pop().token)
        }

        return output
    }

    private fun rpnToDouble(tokens: List<String>): Double {
        try {
            val stack = Stack<String>()

            tokens.forEach {
                if (isNumeric(it)) {
                    stack.push(it)
                } else {

                    val d2 = stack.pop().toBigDecimal()
                    val d1 = stack.pop().toBigDecimal()

                    val result = when (Operator.fromToken(it)) {

                        Operator.SUM -> d1.plus(d2)

                        Operator.SUB -> d1.minus(d2)

                        Operator.TIMES -> d1.times(d2)

                        Operator.DIVIDE -> d1.divide(d2, MathContext.DECIMAL128)

                        else -> throw NumberFormatException()
                    }

                    stack.push(result.toString())
                }
            }

            return stack.pop().toDouble()

        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Expression is not valid")
        } catch (e: EmptyStackException) {
            throw IllegalArgumentException("Expression is not valid")
        }
    }
}