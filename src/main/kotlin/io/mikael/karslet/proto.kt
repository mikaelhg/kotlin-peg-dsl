package io.mikael.karslet

interface Parser {
    fun parse(input: String): ParseResults
}

interface ParseResults

class FakeResults : ParseResults

object Rules {

    fun rule(init: MatchAll.() -> Unit): MatchAll {
        val rule = MatchAll()
        rule.init()
        return rule
    }

}

abstract class Matcher(var name: String? = null) : Parser {

    override fun parse(input: String): ParseResults = FakeResults()

    infix fun named(name: String) {
        this.name = name
    }

}

abstract class MatcherContainer : Matcher() {

    val children: MutableList<Matcher> = mutableListOf()

    override fun parse(input: String): ParseResults = FakeResults()

    fun string(init: MatchString.() -> String): MatchString {
        val rule = MatchString()
        rule.value = rule.init()
        children.add(rule)
        return rule
    }

    fun match(init: MatchRegex.() -> Regex): MatchRegex {
        val rule = MatchRegex()
        rule.value = rule.init()
        children.add(rule)
        return rule
    }

    fun or(init: MatchAny.() -> Unit) = any(init)

    fun any(init: MatchAny.() -> Unit): MatchAny {
        val rule = MatchAny()
        rule.init()
        children.add(rule)
        return rule
    }

    fun and(init: MatchAll.() -> Unit) = all(init)

    fun all(init: MatchAll.() -> Unit): MatchAll {
        val rule = MatchAll()
        rule.init()
        children.add(rule)
        return rule
    }

    fun dynamic(init: MatchDynamic.() -> (String) -> ParseResults): MatchDynamic {
        val rule = MatchDynamic()
        rule.init()
        children.add(rule)
        return rule
    }

    operator fun Matcher.unaryPlus() {
        children.add(this)
    }

}

/**
 * When parsing, every child must match in order
 */
class MatchAll : MatcherContainer(), Parser {
    override fun toString() = if (name != null) "all($name, $children)" else "all($children)"
}

/**
 * When parsing, try each child, return the first match.
 * At least one match is required.
 */
class MatchAny : MatcherContainer(), Parser {
    override fun toString() = if (name != null) "any($name, $children)" else "any($children)"
}

class MatchString(var value: String = "") : Matcher() {
    override fun toString() = if (name != null) "string($name, \"$value\")" else "string(\"$value\")"
}

class MatchRegex(var value: Regex = "".toRegex()) : Matcher() {
    override fun toString() = if (name != null) "match($name, r\"$value\")" else "match(r\"$value\")"
}

class MatchDynamic : Matcher() {
    override fun toString() = if (name != null) "dynamic($name)" else "dynamic()"
}
