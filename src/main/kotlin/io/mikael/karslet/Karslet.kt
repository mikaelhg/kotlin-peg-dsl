package io.mikael.karslet

object Karslet {

    fun <T> any(init: MatchAny<T>.() -> Unit) = MatchAny<T>().also(init)

    fun <T> all(init: MatchAll<T>.() -> Unit) = MatchAll<T>().also(init)

    fun <T> repeat(min: Int = 0, max: Int = Integer.MAX_VALUE, init: MatchRepeat<T>.() -> Unit) =
        MatchRepeat<T>(min, max).also(init)

}
