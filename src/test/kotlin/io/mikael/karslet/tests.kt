package io.mikael.karslet

import org.junit.jupiter.api.Test
import java.io.StringReader

typealias PxRow = Pair<String, List<String>>

class Demos {

    @Test
    fun demos() {
        val px = Karslet.parser<List<PxRow>> {

            val rows = repeat<List<PxRow>> {
                val results = mutableListOf<PxRow>()
                val row = all<PxRow> {
                    val k = characters { match { it != '=' } }
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
