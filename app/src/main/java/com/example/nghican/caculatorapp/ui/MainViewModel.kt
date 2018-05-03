package com.example.nghican.caculatorapp.ui

import android.app.Application
import android.databinding.ObservableField
import com.example.nghican.caculatorapp.base.BaseViewModel
import com.example.nghican.caculatorapp.data.model.Empty
import com.example.nghican.caculatorapp.util.ArithmeticExpressionParser
import io.reactivex.subjects.PublishSubject
import net.objecthunter.exp4j.ExpressionBuilder
import java.util.regex.Pattern

class MainViewModel(application: Application) : BaseViewModel(application) {

    val expression = ObservableField<String>()
    val result = ObservableField<String>()

    val inputErrorSubject: PublishSubject<Empty> = PublishSubject.create()

    fun onResult() {
        val input = expression.get()?.trim()
        if (validateInput(input)) {
            //computeWithExp4j(input!!)
            computeWithShuntingYardAlgorithm(input!!).toString()
        } else {
            inputErrorSubject.onNext(Empty())
        }
    }

    fun onClear() {
        expression.set(null)
        result.set(null)
    }

    private fun validateInput(expression: String?): Boolean {

        return if (expression?.isBlank() == true) {
            false
        } else {
            val pattern = Pattern.compile(REGEX_ARITHMETIC_EXPRESSION)
            pattern.matcher(expression).matches()
        }
    }

    private fun computeWithExp4j(expression: String) {
        try {
            val exp = ExpressionBuilder(expression).build()
            val result = exp.evaluate().toString()
            this.result.set(result)
        } catch (e: IllegalArgumentException) {
            inputErrorSubject.onNext(Empty())
        }
    }

    private fun computeWithShuntingYardAlgorithm(expression: String) {
        val pattern = Pattern.compile(REGEX_SPLIT_NUMBER)
        val subExpression = expression.split(pattern)

        val input = arrayListOf<String>()
        subExpression.forEach {
            if (it.isNotEmpty()) {
                input.add(it)
            }
        }

        try {
            val result = ArithmeticExpressionParser.evaluate(input)
            this.result.set(if (result == Math.floor(result) && !result.isInfinite())
                result.toInt().toString() else result.toString())
        } catch (e: IllegalArgumentException) {
            inputErrorSubject.onNext(Empty())
        }
    }

    companion object {
        private const val REGEX_ARITHMETIC_EXPRESSION = "^[0-9+*-/()., ]+$"
        private const val REGEX_SPLIT_NUMBER = "(?=[-+/*()])|(?<=[-+/*()])"
    }
}