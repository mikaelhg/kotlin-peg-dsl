package io.mikael.karslet

import org.junit.jupiter.api.Test
import java.io.StringReader

data class PxKeyword(val keyword: String, val language: String?, val specifiers: List<String>?)

typealias PxRow = Pair<PxKeyword, List<String>>

class Demos {

    fun stringOrList() = MatchAll<List<String>>().apply {
        characters { min = 1; max = 1; match { it == '"' } }
        val first = characters { min = 0; match { it != '"' } }
        characters { min = 1; max = 1; match { it == '"' } }
        val last = repeat<List<String>> {
            min = 0
            val results = mutableListOf<String>()
            characters { min = 1; max = 1; match { it == ',' } }
            characters { min = 1; max = 1; match { it == '"' } }
            val current = characters { min = 0; match { it != '"' } }
            characters { min = 1; max = 1; match { it == '"' } }
            onIteration { results += current.value() }
            onSuccess { results }
        }
        onSuccess { listOf(first.value()) + last.value() }
    }

    fun keywordLanguage() = MatchRepeat<String?>().apply {
        var result: String? = null
        min = 0
        characters { min = 1; max = 1; match { it == '[' } }
        val lang = characters { min = 2; max = 2; match { it.isLetter() } }
        characters { min = 1; max = 1; match { it == ']' } }
        onIteration { result = lang.value() }
        onSuccess { result }
    }

    fun keywordSpecifiers() = MatchRepeat<List<String>?>().apply {
        var result: List<String>? = null
        min = 0
        characters { min = 1; max = 1; match { it == '(' } }
        val strings = include(stringOrList())
        characters { min = 1; max = 1; match { it == ')' } }
        onIteration { result = strings.value() }
        onSuccess { result }
    }

    fun pxKeyword() = MatchAll<PxKeyword>().apply {
        val kw = characters { min = 1; match { it != '[' && it != '(' && it != '=' } }
        val lang = include(keywordLanguage())
        val spec = include(keywordSpecifiers())
        onSuccess { PxKeyword(kw.value(), lang.value(), spec.value()) }
    }

    @Test
    fun demos() {
        val px = Karslet.parser<List<PxRow>> {

            val rows = repeat<List<PxRow>> {
                val results = mutableListOf<PxRow>()
                val row = all<PxRow> {
                    val k = include(pxKeyword())
                    characters { min = 1; max = 1; match { it == '=' } }
                    val v = characters { match { it != ';' } }
                    characters { min = 1; max = 1; match { it == ';' } }
                    characters { min = 0; match { it.isWhitespace() } }
                    onSuccess { PxRow(k.value(), listOf(v.value())) }
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
