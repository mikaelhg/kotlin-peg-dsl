package io.mikael.karslet

import io.mikael.karslet.operators.SequenceOperator
import io.mikael.karslet.operators.OrderedChoiceOperator
import io.mikael.karslet.operators.RepeatingOperator

const val MAX_REPEATS = Integer.MAX_VALUE

/**
 * Karslet is a Parsing Expression Grammar (PEG) domain specific language (DSL) library.
 */
object Karslet {

    fun <T> choice(init: OrderedChoiceOperator<T>.() -> Unit) = OrderedChoiceOperator<T>().also(init)

    fun <T> sequence(init: SequenceOperator<T>.() -> Unit) = SequenceOperator<T>().also(init)

    fun <T> repeat(min: Int = 0, max: Int = MAX_REPEATS, init: RepeatingOperator<T>.() -> Unit) =
        RepeatingOperator<T>(min, max).also(init)

    fun <T> optional(init: RepeatingOperator<T>.() -> Unit) = repeat(min = 0, max = 1, init = init)

    fun <T> zeroOrMore(init: RepeatingOperator<T>.() -> Unit) = repeat(min = 0, init = init)

    fun <T> oneOrMore(init: RepeatingOperator<T>.() -> Unit) = repeat(min = 1, init = init)

}
