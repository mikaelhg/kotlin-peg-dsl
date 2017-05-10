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

abstract class Rule(var name: String? = null) : Parser {

    override fun parse(input: String): ParseResults = FakeResults()

    infix fun named(name: String) {
        this.name = name
    }

}

open class RuleContainer : Rule() {

    val children: MutableList<Rule> = mutableListOf()

    override fun parse(input: String): ParseResults = FakeResults()

    fun str(init: StringRule.() -> String): StringRule {
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

    fun or(init: SequentialMatchAny.() -> Unit) = any(init)

    fun any(init: SequentialMatchAny.() -> Unit): SequentialMatchAny {
        val rule = SequentialMatchAny()
        rule.init()
        children.add(rule)
        return rule
    }

    fun and(init: SequentialMatchAll.() -> Unit) = all(init)

    fun all(init: SequentialMatchAll.() -> Unit): SequentialMatchAll {
        val rule = SequentialMatchAll()
        rule.init()
        children.add(rule)
        return rule
    }

    fun dynamic(init: DynamicRule.() -> (String) -> ParseResults): DynamicRule {
        val rule = DynamicRule()
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
class SequentialMatchAll : RuleContainer(), Parser {
    override fun toString() = "SequentialMatchAll($name, $children)"
}

/**
 * When parsing, try each child, return the first match.
 */
class SequentialMatchAny : RuleContainer(), Parser {
    override fun toString() = "SequentialMatchAny($name, $children)"
}

class StringRule(var value: String = "") : Rule() {
    override fun toString() = "StringRule($name, $value)"
}

class MatchRule(var value: Regex = "".toRegex()) : Rule() {
    override fun toString() = "MatchRule($name, $value)"
}

class DynamicRule : Rule() {
    override fun toString() = "DynamicRule($name)"
}
