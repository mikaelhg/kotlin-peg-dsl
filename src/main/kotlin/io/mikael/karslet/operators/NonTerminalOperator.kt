package io.mikael.karslet.operators

import io.mikael.karslet.Parser
import io.mikael.karslet.ParserConfiguration

abstract class NonTerminalOperator<T> : Parser<T> {

    val children: MutableList<Parser<*>> = mutableListOf()

    var beforeAttemptAction: () -> Unit = {}

    lateinit var successAction: () -> T

    @ParserConfiguration
    fun beforeAttempt(beforeAttemptAction: () -> Unit) {
        this.beforeAttemptAction = beforeAttemptAction
    }

    @ParserConfiguration
    fun onSuccess(successAction: () -> T) {
        this.successAction = successAction
    }

    fun characters(min: Int = 1, max: Int = Integer.MAX_VALUE, matcher: (Char) -> Boolean) =
        add(MatchCharacters()) { this.min = min; this.max = max; match(matcher) }

    fun characters(min: Int = 1, max: Int = Integer.MAX_VALUE, char: Char) =
        add(MatchCharacters()) { this.min = min; this.max = max; match { it == char } }

    fun character(char: Char) =
        add(MatchCharacters()) { min = 1; max = 1; match { it == char } }

    fun character(vararg char: Char) =
        add(MatchCharacters()) { min = 1; max = 1; match { it in char } }

    fun characters(init: MatchCharacters.() -> Unit) = add(MatchCharacters(), init)

    fun whitespace(min: Int = 0, max: Int = Integer.MAX_VALUE) =
        add(MatchCharacters()) { this.min = min; this.max = max; match { it.isWhitespace() } }

    fun <T> any(init: OrderedChoiceOperator<T>.() -> Unit) = add(OrderedChoiceOperator(), init)

    fun <T> all(init: SequenceOperator<T>.() -> Unit) = add(SequenceOperator(), init)

    fun <T> repeat(init: RepeatingOperator<T>.() -> Unit) = add(RepeatingOperator(), init)

    fun <T> repeat(min: Int = 1, max: Int = Integer.MAX_VALUE, init: RepeatingOperator<T>.() -> Unit) =
        add(RepeatingOperator<T>()) { this.min = min; this.max = max; this.init() }

    @SuppressWarnings("WeakerAccess")
    protected fun <T : Parser<*>> add(child: T, init: T.() -> Unit) = child.also(init).also(children::add)

    fun <T: Parser<*>> include(item: T): T = item.also(children::add)

    override fun resetParserState() {
        children.forEach(Parser<*>::resetParserState)
    }

    override fun value() = successAction()

}
