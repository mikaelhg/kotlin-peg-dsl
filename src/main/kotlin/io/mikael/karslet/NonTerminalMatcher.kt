package io.mikael.karslet

abstract class NonTerminalMatcher<T> : Parser<T> {

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

    fun <T> any(init: MatchAny<T>.() -> Unit) = add(MatchAny(), init)

    fun <T> all(init: MatchAll<T>.() -> Unit) = add(MatchAll(), init)

    fun <T> repeat(init: MatchRepeat<T>.() -> Unit) = add(MatchRepeat(), init)

    fun <T : Parser<*>> add(child: T, init: T.() -> Unit) = child.also(init).also(children::add)

    fun <T: Parser<*>> include(item: T): T = item.also(children::add)

    override fun resetParserState() {
        children.forEach(Parser<*>::resetParserState)
    }

    override fun value() = successAction()

}
