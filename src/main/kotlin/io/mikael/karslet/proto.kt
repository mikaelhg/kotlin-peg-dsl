package io.mikael.karslet

interface Parser {
    fun parse(input: String): ParseResults
}

interface ParseResults

class FakeResults : ParseResults

fun rule(init: SequentialMatchAll.() -> Unit): SequentialMatchAll {
    val rule = SequentialMatchAll()
    rule.init()
    return rule
}

abstract class Rule(var name: String?) : Parser {

    override fun parse(input: String): ParseResults = FakeResults()

    infix fun named(name: String) {
        this.name = name
    }

}

open class RuleContainer(name: String?) : Rule(name) {

    val children: MutableList<Rule> = mutableListOf()

    override fun parse(input: String): ParseResults = FakeResults()

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

    fun dynamic(init: DynamicRule.() -> (String) -> ParseResults): DynamicRule {
        val rule = DynamicRule(null)
        rule.init()
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

/**
 * When parsing, every child must match in order
 */
class SequentialMatchAll : RuleContainer(null), Parser {
    override fun toString() = "SequentialMatchAll($name, $children)"
}

/**
 * When parsing, try each child, return the first match.
 */
class SequentialMatchAny : RuleContainer(null), Parser {
    override fun toString() = "SequentialMatchAny($name, $children)"
}

class StringRule(name: String?, var value: String = "") : Rule(name) {
    override fun toString() = "StringRule($name, $value)"
}

class MatchRule(name: String?, var value: Regex = "".toRegex()) : Rule(name) {
    override fun toString() = "MatchRule($name, $value)"
}

class DynamicRule(name: String?) : Rule(name) {
    override fun toString() = "DynamicRule($name)"
}
