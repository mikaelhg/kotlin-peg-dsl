package io.mikael.karslet

import io.mikael.karslet.Rules.parser
import org.junit.jupiter.api.Test

class Calculator {

    @Test
    fun calculate() {

        val number = parser<Any> {
            match { "[+\\-]".toRegex() }.maybe()
        }

    }

}

class Demos {

    @Test
    fun demos() {

        val r1 = parser<Any> {

            binary { constant("0xcafebabe") }

            binary { bitmask("1xx1001") }

            string { "foobar" } captureTo "fooValue"

            match { "[0-9a-f]".toRegex() } captureTo "barValue"

            any {
                string { "a" }
                string { "b" }
            } captureTo "ab"

        }

        r1.parse("xyzzy")

        println(r1)

        val r2 = parser<Any> {
            string("agfa")
            r1()
            string { "canon" }
            dynamic { _ -> FakeResults() }
        }

        println(r2)

        val r3 = parser<Any> {
            any {
                all {
                    string { "s" }
                    string { "equence" }
                }
                all {
                    string { "se" }
                    string { "quence" }
                }
            }
        }

        println(r3)

        val r4 = parser<Any> {
            or {
                and {
                    string { "s" }
                    string { "equence" }
                }
                and {
                    string { "se" }
                    string { "quence" }
                }
            }
        }

        println(r4)
    }

}

