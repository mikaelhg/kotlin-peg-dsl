package io.mikael.karslet

fun main(args: Array<String>) {

    val r = rule {

        string { "foobar" } named "fooValue"

        match { "[0-9a-f]".toRegex() } named "barValue"

    }

    r.parse("xyzzy")

    println(r.children)

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

open class Rule : Parser {

    var name: String = ""

    override fun parse(input: String): ParseResults = object : ParseResults {}

    infix fun named(name: String) {
        this.name = name
    }

}

open class RuleContainer {

    val children: MutableList<Rule> = mutableListOf()

    fun string(init: StringRule.() -> String): StringRule {
        val rule = StringRule()
        rule.value = rule.init()
        children.add(rule)
        return rule
    }

    fun match(init: MatchRule.() -> Regex): MatchRule {
        val rule = MatchRule()
        rule.value = rule.init()
        children.add(rule)
        return rule
    }

}

class SequentialMatchAll : RuleContainer(), Parser {

    override fun parse(input: String): ParseResults = object : ParseResults {}

    /* When parsing, every child must match in order */

}

class SequentialMatchAny : RuleContainer(), Parser {

    override fun parse(input: String): ParseResults = object : ParseResults {}

    /* When parsing, try each child, return the first match */

}

class StringRule(var value: String = "") : Rule() {

    override fun toString() = "StringRule($name, $value)"

}

class MatchRule(var value: Regex = "".toRegex()) : Rule() {
    override fun toString() = "MatchRule($name, $value)"
}
