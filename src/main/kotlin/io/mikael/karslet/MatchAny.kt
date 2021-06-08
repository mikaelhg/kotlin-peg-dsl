package io.mikael.karslet

/**
 * When parsing, try each child, return the first match.
 * At least one match is required.
 */
class MatchAny<T> : NonTerminalMatcher<T>()
