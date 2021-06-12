package io.mikael.karslet

import org.junit.jupiter.api.Test
import java.nio.CharBuffer

data class PxKeyword(val keyword: String, val language: String?, val specifiers: List<String>?)

data class PxValue(val numberValue: Long?, val stringValue: String?, val listValue: List<String>?)

typealias PxRow = Pair<PxKeyword, PxValue>

class Demos {

    private fun stringOrList() = Karslet.sequence<List<String>> {

        character('"')
        val first = characters(min = 0) { it != '"' }
        character('"')

        val last = zeroOrMore<List<String>> {
            val state = mutableListOf<String>()
            beforeAttempt { state.clear() }

            character(',')
            character('"')
            val current = characters(min = 0) { it != '"' }
            character('"')

            onIteration { state += current.value() }
            onSuccess { state }
        }

        onSuccess { listOf(first.value()) + last.value() }
    }

    private fun keywordLanguage() = Karslet.optional<String?> {

        var state: String? = null
        beforeAttempt { state = null }

        character('[')
        val lang = characters(min = 2, max = 2) { it.isLetter() }
        character(']')

        onIteration { state = lang.value() }
        onSuccess { state }
    }

    private fun keywordSpecifiers() = Karslet.optional<List<String>?> {

        var state: List<String>? = null
        beforeAttempt { state = null }

        character('(')
        val strings = include(stringOrList())
        character(')')

        onIteration { state = strings.value() }
        onSuccess { state }
    }

    private fun pxKeyword() = Karslet.sequence<PxKeyword> {
        val kw = characters(min = 1) { it !in arrayOf('[', '(', '=') }
        val lang = include(keywordLanguage())
        val spec = include(keywordSpecifiers())

        onSuccess { PxKeyword(kw.value(), lang.value(), spec.value()) }
    }

    private fun pxValue() = Karslet.choice<PxValue> {
        val strings = sequence<List<String>?> {
            val x = include(stringOrList())
            character(';')
            onSuccess { x.value() }
        }
        val numbers = sequence<Long?> {
            val x = characters(min = 1) { it.isDigit() }
            character(';')
            onSuccess { x.value().toLongOrNull() }
        }
        val letters = sequence<String?> {
            val x = characters(min = 1) { it.isLetterOrDigit() }
            character(';')
            onSuccess { x.value() }
        }
        onSuccess { PxValue(numbers.value(), letters.value(), strings.value()) }
    }

    @Test
    fun pxParserDemo() {

        val parser = Karslet.zeroOrMore<List<PxRow>> {
            val state = mutableListOf<PxRow>()

            val row = sequence<PxRow> {
                val k = include(pxKeyword())
                character('=')
                val v = include(pxValue())
                whitespace()

                onSuccess { PxRow(k.value(), v.value()) }
            }

            beforeAttempt { state.clear() }
            onIteration { state += row.value() }
            onSuccess { state }
        }

        PxTestData.rows.forEach { row ->
            val success = parser.parse(CharBuffer.wrap(row))
            println("$success ${parser.value()}")
        }

    }

}
