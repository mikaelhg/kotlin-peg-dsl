package io.mikael.karslet

import java.io.Reader

interface Parser<T> {

    fun parse(r: Reader): Boolean

    fun resetParserState()

    fun value(): T

}
