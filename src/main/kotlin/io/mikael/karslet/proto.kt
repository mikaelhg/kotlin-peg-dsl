package io.mikael.karslet

import java.io.Reader

interface Parser {

    fun parse(r: Reader): Boolean

}

abstract class NonTerminalMatcher : Parser {

    val children: MutableList<Parser> = mutableListOf()

    fun characters(init: MatchCharacters.() -> Unit) = add(MatchCharacters(), init)

    fun <T> any(init: MatchAny<T>.() -> Unit) = add(MatchAny(), init)

    fun <T> all(init: MatchAll<T>.() -> Unit) = add(MatchAll(), init)

    fun <T> repeat(init: MatchRepeat<T>.() -> Unit) = add(MatchRepeat(), init)

    fun <T : Parser> add(child: T, init: T.() -> Unit) = child.also(init).also(children::add)

    operator fun NonTerminalMatcher.unaryPlus() {
        children.add(this)
    }

    override fun parse(r: Reader): Boolean {
        r.mark(10240)
        val success = children.all { it.parse(r) }
        println("$this $success $children")
        if (!success) r.reset()
        return success
    }

}

abstract class TerminalMatcher : Parser
