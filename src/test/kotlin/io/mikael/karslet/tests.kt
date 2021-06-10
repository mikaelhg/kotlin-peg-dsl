package io.mikael.karslet

import org.junit.jupiter.api.Test
import java.io.StringReader

data class PxKeyword(val keyword: String, val language: String?, val specifiers: List<String>?)

data class PxValue(var numberValue: Long?, var stringValue: String?, var listValue: List<String>?)

typealias PxRow = Pair<PxKeyword, PxValue>

class Demos {

    fun stringOrList() = Karslet.all<List<String>> {
        character('"')
        val first = characters(0) { it != '"' }
        character('"')
        val last = repeat<List<String>>(min = 0) {
            val results = mutableListOf<String>()
            beforeAttempt { results.clear() }
            character(',')
            character('"')
            val current = characters(0) { it != '"' }
            character('"')
            onIteration { results += current.value() }
            onSuccess { results }
        }
        onSuccess { listOf(first.value()) + last.value() }
    }

    fun keywordLanguage() = Karslet.repeat<String?>(min = 0) {
        var state: String? = null
        beforeAttempt { state = null }
        character('[')
        val lang = characters(2, 2) { it.isLetter() }
        character(']')
        onIteration { state = lang.value() }
        onSuccess { state }
    }

    fun keywordSpecifiers() = Karslet.repeat<List<String>?>(min = 0) {
        var state: List<String>? = null
        beforeAttempt { state = null }
        character('(')
        val strings = include(stringOrList())
        character(')')
        onIteration { state = strings.value() }
        onSuccess { state }
    }

    fun pxKeyword() = Karslet.all<PxKeyword> {
        val kw = characters(1) { it !in arrayOf('[', '(', '=') }
        val lang = include(keywordLanguage())
        val spec = include(keywordSpecifiers())
        onSuccess { PxKeyword(kw.value(), lang.value(), spec.value()) }
    }

    fun pxValue() = Karslet.any<PxValue> {
        val strings = include(stringOrList())
        val numbers = characters(0) { it.isDigit() }
        val letters = characters(0) { it.isLetterOrDigit() }
        onSuccess { PxValue(numbers.value().toLongOrNull(), letters.value(), strings.value()) }
    }

    @Test
    fun pxParserDemo() {

        val parser = Karslet.repeat<List<PxRow>> {
            val state = mutableListOf<PxRow>()

            val row = all<PxRow> {
                val k = include(pxKeyword())
                character('=')
                val v = include(pxValue())
                character(';')
                whitespace()

                onSuccess { PxRow(k.value(), v.value()) }
            }

            beforeAttempt { state.clear() }
            onIteration { state += row.value() }
            onSuccess { state }
        }

        PxTestData.rows.forEach { row ->
            val success = parser.parse(StringReader(row))
            println("$success ${parser.value()}")
        }

    }

}
