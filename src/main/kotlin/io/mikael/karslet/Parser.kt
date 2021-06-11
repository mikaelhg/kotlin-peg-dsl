package io.mikael.karslet

import java.nio.CharBuffer

interface Parser<T> {

    fun parse(r: CharBuffer): Boolean

    fun resetParserState()

    fun value(): T

}
