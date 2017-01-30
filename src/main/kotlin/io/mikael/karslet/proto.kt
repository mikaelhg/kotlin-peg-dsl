package io.mikael.karslet

fun main(args: Array<String>) {

    val r1 = rule {

        string { "foobar" } named "fooValue"

        match { "[0-9a-f]".toRegex() } named "barValue"

        any {
            string { "a" }
            string { "b" }
        } named "abbas"

    }

    r1.parse("xyzzy")

    println(r1)

    val r2 = rule {
        string { "agfa" }
        + r1
    }

    println(r2)

}

interface Parser {
    fun parse(input: String): ParseResults
}

interface ParseResults

fun rule(init: SequentialMatchAll.() -> Unit): SequentialMatchAll {
    val rule = SequentialMatchAll()
    rule.init()
    return rule
}

interface Rule : Parser {

    var name: String?

    override fun parse(input: String): ParseResults = object : ParseResults {}

    infix fun named(name: String) {
        this.name = name
    }

}

open class RuleContainer(override var name: String?) : Rule {

    val children: MutableList<Rule> = mutableListOf()

    fun string(init: StringRule.() -> String): StringRule {
        val rule = StringRule(null)
        rule.value = rule.init()
        children.add(rule)
        return rule
    }

    fun match(init: MatchRule.() -> Regex): MatchRule {
        val rule = MatchRule(null)
        rule.value = rule.init()
        children.add(rule)
        return rule
    }

    fun any(init: SequentialMatchAny.() -> Unit): SequentialMatchAny {
        val rule = SequentialMatchAny()
        rule.init()
        children.add(rule)
        return rule
    }

    operator fun Rule.unaryPlus() {
        children.add(this)
    }

}

class SequentialMatchAll : RuleContainer(null), Parser {

    override fun parse(input: String): ParseResults = object : ParseResults {}

    /* When parsing, every child must match in order */

    override fun toString() = "SequentialMatchAll($name, $children)"

}

class SequentialMatchAny : RuleContainer(null), Parser {

    override fun parse(input: String): ParseResults = object : ParseResults {}

    /* When parsing, try each child, return the first match */

    override fun toString() = "SequentialMatchAny($name, $children)"

}

class StringRule(override var name: String?, var value: String = "") : Rule {
    override fun toString() = "StringRule($name, $value)"
}

class MatchRule(override var name: String?, var value: Regex = "".toRegex()) : Rule {
    override fun toString() = "MatchRule($name, $value)"
}
