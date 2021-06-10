package io.mikael.karslet

object Karslet {

    fun <T> any(init: MatchAny<T>.() -> Unit) = MatchAny<T>().also(init)

    fun <T> all(init: MatchAll<T>.() -> Unit) = MatchAll<T>().also(init)

    fun <T> repeat(init: MatchRepeat<T>.() -> Unit) = MatchRepeat<T>().also(init)

}
