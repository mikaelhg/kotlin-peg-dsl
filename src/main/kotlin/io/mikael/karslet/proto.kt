package io.mikael.karslet

import java.io.Reader

interface Parser<T> {

    fun parse(r: Reader): Boolean

    fun resetParserState()

}

abstract class NonTerminalMatcher<T> : Parser<T> {

    lateinit var contextCreator: () -> T

    val children: MutableList<Parser<*>> = mutableListOf()

    fun characters(init: MatchCharacters.() -> Unit) = add(MatchCharacters(), init)

    fun context(contextCreator: () -> T) {
        this.contextCreator = contextCreator
    }

    fun <T> any(init: MatchAny<T>.() -> Unit) = add(MatchAny(), init)

    fun <T> all(init: MatchAll<T>.() -> Unit) = add(MatchAll(), init)

    fun <T> repeat(init: MatchRepeat<T>.() -> Unit) = add(MatchRepeat(), init)

    fun <TT : Parser<*>> add(child: TT, init: TT.() -> Unit) = child.also(init).also(children::add)

    operator fun NonTerminalMatcher<Any>.unaryPlus() {
        children.add(this)
    }

    override fun resetParserState() {
        children.forEach(Parser<*>::resetParserState)
    }

    override fun parse(r: Reader): Boolean {
        r.mark(Integer.MAX_VALUE)
        val success = children.all { it.parse(r) }
        println("$this $success $children")
        if (!success) r.reset()
        return success
    }

}

abstract class TerminalMatcher<T> : Parser<T>
