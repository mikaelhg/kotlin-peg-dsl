package io.mikael.karslet.operators

import io.mikael.karslet.Parser

/**
 * A matcher with no children.
 */
abstract class TerminalOperator<T> : Parser<T>