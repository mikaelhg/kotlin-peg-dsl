package io.mikael.karslet.operators

import io.mikael.karslet.Karslet
import io.mikael.karslet.MAX_REPEATS
import io.mikael.karslet.Parser
import io.mikael.karslet.ParserConfiguration

@Suppress("MemberVisibilityCanBePrivate")
abstract class NonTerminalOperator<T> : Parser<T> {

    val children: MutableList<Parser<*>> = mutableListOf()

    /*
     * This lambda will be called by the parse() method, before the children's parse is called.
     * Since operators can be reused, and the previous call might have polluted the state, this
     * is where you'd want to reset your state, and make it like freshly fallen snow.
     */
    var beforeAttemptAction: () -> Unit = {}

    /*
     * This lambda will be called by the parent of this operator, to fetch this operator's state
     * after a successful (or otherwise) execution.
     */
    lateinit var successAction: () -> T

    @ParserConfiguration
    open fun beforeAttempt(beforeAttemptAction: () -> Unit) {
        this.beforeAttemptAction = beforeAttemptAction
    }

    @ParserConfiguration
    open fun onSuccess(successAction: () -> T) {
        this.successAction = successAction
    }

    /* State management */

    override fun resetParserState() {
        children.forEach(Parser<*>::resetParserState)
    }

    override fun value() = successAction()

    /* Terminal operations */

    open fun characters(init: MatchCharacters.() -> Unit) = MatchCharacters().also(init).also(children::add)

    open fun characters(min: Int = 1, max: Int = MAX_REPEATS, char: Char) = characters(min, max) { it == char }

    open fun character(char: Char) = characters(1, 1) { it == char }

    open fun character(vararg char: Char) = characters(1, 1) { it in char }

    open fun characters(min: Int = 1, max: Int = MAX_REPEATS, matcher: (Char) -> Boolean) =
        characters(init = { this.min = min; this.max = max; match(matcher) })

    open fun whitespace(min: Int = 0, max: Int = MAX_REPEATS) =
        characters(min, max) { it.isWhitespace() }

    /* Non-terminal operations */

    open fun <T> choice(init: OrderedChoiceOperator<T>.() -> Unit) = include(Karslet.choice(init))

    open fun <T> sequence(init: SequenceOperator<T>.() -> Unit) = include(Karslet.sequence(init))

    open fun <T> repeat(min: Int = 1, max: Int = MAX_REPEATS, init: RepeatingOperator<T>.() -> Unit) =
        include(Karslet.repeat(min, max, init))

    open fun <T> optional(init: RepeatingOperator<T>.() -> Unit) = include(Karslet.optional(init))

    open fun <T> oneOrMore(init: RepeatingOperator<T>.() -> Unit) = include(Karslet.oneOrMore(init))

    open fun <T> zeroOrMore(init: RepeatingOperator<T>.() -> Unit) = include(Karslet.zeroOrMore(init))

    open fun <T: Parser<*>> include(item: T): T = item.also(children::add)

}
