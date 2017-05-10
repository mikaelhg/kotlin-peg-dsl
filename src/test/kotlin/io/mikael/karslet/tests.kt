package io.mikael.karslet

fun main(args: Array<String>) {

    val r1 = rule {

        str { "foobar" } named "fooValue"

        match { "[0-9a-f]".toRegex() } named "barValue"

        any {
            str { "a" }
            str { "b" }
        } named "ab"

    }

    r1.parse("xyzzy")

    println(r1)

    val r2 = rule {
        str { "agfa" }
        + r1
        str { "canon" }
        dynamic { { _ -> FakeResults() } }
    }

    println(r2)

    val r3 = rule {
        any {
            all {
                str { "s" }
                str { "equence" }
            }
            all {
                str { "se" }
                str { "quence" }
            }
        }
    }

    println(r3)

    val r4 = rule {
        or {
            and {
                str { "s" }
                str { "equence" }
            }
            and {
                str { "se" }
                str { "quence" }
            }
        }
    }

    println(r4)

}
