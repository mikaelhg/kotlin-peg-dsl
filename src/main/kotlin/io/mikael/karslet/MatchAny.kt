package io.mikael.karslet

import java.io.Reader

/**
 * When parsing, try each child, return the first match.
 * At least one match is required.
 */
class MatchAny<T> : NonTerminalMatcher<T>() {

    override fun parse(r: Reader): Boolean {
        TODO()
    }

}
