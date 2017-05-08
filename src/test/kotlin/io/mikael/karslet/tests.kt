package io.mikael.karslet

fun main(args: Array<String>) {

    val r1 = rule {

        string { "foobar" } named "fooValue"

        match { "[0-9a-f]".toRegex() } named "barValue"

        any {
            string { "a" }
            string { "b" }
        } named "ab"

    }

    r1.parse("xyzzy")

    println(r1)

    val r2 = rule {
        string { "agfa" }
        + r1
        string { "canon" }
        dynamic { { _ -> FakeResults() } }
    }

    println(r2)

}
