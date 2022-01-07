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
    fun beforeAttempt(beforeAttemptAction: () -> Unit) {
        this.beforeAttemptAction = beforeAttemptAction
    }

    @ParserConfiguration
    fun onSuccess(successAction: () -> T) {
        this.successAction = successAction
    }

    /* State management */

    override fun resetParserState() {
        children.forEach(Parser<*>::resetParserState)
    }

    override fun value() = successAction()

    /* Terminal operations */

    fun characters(init: MatchCharacters.() -> Unit) = MatchCharacters().also(init).also(children::add)

    fun characters(min: Int = 1, max: Int = MAX_REPEATS, char: Char) = characters(min, max) { it == char }

    fun character(char: Char) = characters(1, 1) { it == char }

    fun character(vararg char: Char) = characters(1, 1) { it in char }

    fun characters(min: Int = 1, max: Int = MAX_REPEATS, matcher: (Char) -> Boolean) =
        characters(init = { this.min = min; this.max = max; match(matcher) })

    fun whitespace(min: Int = 0, max: Int = MAX_REPEATS) =
        characters(min, max) { it.isWhitespace() }

    /* Non-terminal operations */

    fun <T> choice(init: OrderedChoiceOperator<T>.() -> Unit) = include(Karslet.choice(init))

    fun <T> sequence(init: SequenceOperator<T>.() -> Unit) = include(Karslet.sequence(init))

    fun <T> repeat(min: Int = 1, max: Int = MAX_REPEATS, init: RepeatingOperator<T>.() -> Unit) =
        include(Karslet.repeat(min, max, init))

    fun <T> optional(init: RepeatingOperator<T>.() -> Unit) = include(Karslet.optional(init))

    fun <T> oneOrMore(init: RepeatingOperator<T>.() -> Unit) = include(Karslet.oneOrMore(init))

    fun <T> zeroOrMore(init: RepeatingOperator<T>.() -> Unit) = include(Karslet.zeroOrMore(init))

    fun <T: Parser<*>> include(item: T): T = item.also(children::add)

}
