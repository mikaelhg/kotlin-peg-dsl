package io.mikael.karslet

import java.io.Reader

interface Parser {

    fun parse(r: Reader): Boolean

}

abstract class NonTerminalMatcher : Parser {

    val children: MutableList<Parser> = mutableListOf()

    override fun parse(r: Reader) = true

}

abstract class TerminalMatcher : Parser {

    override fun parse(r: Reader) = true

}

@KarsletMarker
abstract class MatcherContainer : NonTerminalMatcher() {

    fun characters(init: MatchCharacters.() -> Unit) = add(MatchCharacters(), init)

    fun any(init: MatchAny.() -> Unit) = add(MatchAny(), init)

    fun all(init: MatchAll.() -> Unit) = add(MatchAll(), init)

    fun repeat(init: MatchAll.() -> Unit) = add(MatchAll(), init)

    private fun <T : Parser> add(child: T, init: T.() -> Unit) = child.also(init).also(children::add)

    operator fun NonTerminalMatcher.unaryPlus() {
        children.add(this)
    }

}

/**
 * When parsing, every child must match in order
 */
class MatchAll : MatcherContainer()

/**
 * When parsing, try each child, return the first match.
 * At least one match is required.
 */
class MatchAny : MatcherContainer()

@KarsletMarker
class MatchCharacters : TerminalMatcher() {
    lateinit var matcher: (Char) -> Boolean
    var min = 1
    var max = -1
    fun match(matcher: (Char) -> Boolean) {
        this.matcher = matcher
    }
}

@KarsletMarker
abstract class RepeatingMatcher {
    var exact = -1
    var min = 1
    var max = -1
}

@KarsletMarker
class MatchRepeat : NonTerminalMatcher() {
    var exact = -1
    var min = 1
    var max = -1
    fun match(matcher: (Char) -> Boolean) {
        this.matcher = matcher
    }
}
