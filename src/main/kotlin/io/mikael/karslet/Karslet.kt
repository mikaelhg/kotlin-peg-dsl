package io.mikael.karslet

import io.mikael.karslet.operators.SequenceOperator
import io.mikael.karslet.operators.OrderedChoiceOperator
import io.mikael.karslet.operators.RepeatingOperator

object Karslet {

    fun <T> choice(init: OrderedChoiceOperator<T>.() -> Unit) = OrderedChoiceOperator<T>().also(init)

    fun <T> sequence(init: SequenceOperator<T>.() -> Unit) = SequenceOperator<T>().also(init)

    fun <T> oneOrMore(init: RepeatingOperator<T>.() -> Unit) =
        repeat(min = 1, init = init)

    fun <T> zeroOrMore(init: RepeatingOperator<T>.() -> Unit) =
        repeat(min = 0, init = init)

    fun <T> optional(init: RepeatingOperator<T>.() -> Unit) =
        repeat(min = 0, max = 1, init = init)

    fun <T> repeat(min: Int = 0, max: Int = Integer.MAX_VALUE, init: RepeatingOperator<T>.() -> Unit) =
        RepeatingOperator<T>(min, max).also(init)

}
