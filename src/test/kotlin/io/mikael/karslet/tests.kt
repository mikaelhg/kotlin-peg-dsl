package io.mikael.karslet

import org.junit.jupiter.api.Test
import java.io.StringReader

data class PxKeyword(val keyword: String, val language: String?, val specifiers: List<String>?)

data class PxValue(var numberValue: Long?, var stringValue: String?, var listValue: List<String>?)

typealias PxRow = Pair<PxKeyword, PxValue>

class Demos {

    fun stringOrList() = MatchAll<List<String>>().apply {
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

    fun keywordLanguage() = MatchRepeat<String?>(min = 0).apply {
        var state: String? = null
        beforeAttempt { state = null }
        character('[')
        val lang = characters(2, 2) { it.isLetter() }
        character(']')
        onIteration { state = lang.value() }
        onSuccess { state }
    }

    fun keywordSpecifiers() = MatchRepeat<List<String>?>(min = 0).apply {
        var state: List<String>? = null
        beforeAttempt { state = null }
        character('(')
        val strings = include(stringOrList())
        character(')')
        onIteration { state = strings.value() }
        onSuccess { state }
    }

    fun pxKeyword() = MatchAll<PxKeyword>().apply {
        val kw = characters(1) { it !in arrayOf('[', '(', '=') }
        val lang = include(keywordLanguage())
        val spec = include(keywordSpecifiers())
        onSuccess { PxKeyword(kw.value(), lang.value(), spec.value()) }
    }

    fun pxValue() = MatchAny<PxValue>().apply {
        val strings = include(stringOrList())
        val numbers = characters(0) { it.isDigit() }
        val letters = characters(0) { it.isLetterOrDigit() }
        onSuccess { PxValue(numbers.value().toLongOrNull(), letters.value(), strings.value()) }
    }

    @Test
    fun pxParserDemo() {
        val px = Karslet.parser<List<PxRow>> {

            val rows = repeat<List<PxRow>> {
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

            onSuccess { rows.value() }
        }

        PxTestData.rows.forEach { row ->
            val success = px.parse(StringReader(row))
            println("$success ${px.value()}")
        }

    }

}
