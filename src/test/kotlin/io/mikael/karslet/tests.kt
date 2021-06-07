package io.mikael.karslet

import org.junit.jupiter.api.Test

typealias PxRow = Pair<String, List<String>>

class Demos {

    @Test
    fun demos() {

        val px = Karslet.parser<List<PxRow>> {
            val results = mutableListOf<PxRow>()
            all {
                val k = characters { match { it != '=' } }
                characters { match { it == '=' } }
                val v = characters { match { it != ';' } }
                characters { match { it == ';' } }
                characters {
                    match { it.isWhitespace() }
                    min = 0
                }
            }
        }

    }

}
