package io.mikael.karslet

import java.io.Reader

internal fun Reader.readChar(): Pair<Int, Char> {
    val i = read()
    val c = i.toChar()
    return Pair(i, c)
}

internal fun Reader.peekChar(): Pair<Int, Char> {
    mark(1)
    val i = read()
    val c = i.toChar()
    reset()
    return Pair(i, c)
}

internal fun Reader.readUntil(until: (Char) -> Boolean): String {
    return StringBuilder()
        .also { sb -> doUntil(until, sb::append) }
        .let(StringBuilder::toString)
}

internal fun Reader.doUntil(terminal: (Char) -> Boolean, action: (Char) -> Unit) {
    while (true) {
        mark(1)
        val (v, ch) = readChar()
        if (v == -1 || terminal(ch)) {
            reset()
            break
        } else {
            action(ch)
        }
    }
}
