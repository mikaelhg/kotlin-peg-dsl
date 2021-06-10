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
        val last = repeat<List<String>> {
            min = 0
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

    fun keywordLanguage() = MatchRepeat<String?>().apply {
        var result: String? = null
        beforeAttempt { result = null }
        min = 0
        character('[')
        val lang = characters(2, 2) { it.isLetter() }
        character(']')
        onIteration { result = lang.value() }
        onSuccess { result }
    }

    fun keywordSpecifiers() = MatchRepeat<List<String>?>().apply {
        var result: List<String>? = null
        beforeAttempt { result = null }
        min = 0
        character('(')
        val strings = include(stringOrList())
        character(')')
        onIteration { result = strings.value() }
        onSuccess { result }
    }

    fun pxKeyword() = MatchAll<PxKeyword>().apply {
        val kw = notCharacters('[', '(', '=') { min = 1 }
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
    fun demos() {
        val px = Karslet.parser<List<PxRow>> {

            val rows = repeat<List<PxRow>> {
                val results = mutableListOf<PxRow>()
                val row = all<PxRow> {
                    val k = include(pxKeyword())
                    character('=')
                    val v = include(pxValue())
                    character(';')
                    characters(0) { it.isWhitespace() }
                    onSuccess { PxRow(k.value(), v.value()) }
                }
                beforeAttempt { results.clear() }
                onIteration { results += row.value() }
                onSuccess { results }
            }

            onSuccess { rows.value() }
        }

        PxTestData.rows.forEach { row ->
            val success = px.parse(StringReader(row))
            println("$success ${px.value()}")
        }

    }

}
