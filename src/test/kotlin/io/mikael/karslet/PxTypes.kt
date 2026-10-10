package io.mikael.karslet

data class PxKeyword(
    val keyword: String,
    val language: String?,
    val specifiers: List<String>?
)

data class PxValue(
    val numberValue: Long?,
    val stringValue: String?,
    val listValue: List<String>?
)

typealias PxRow = Pair<PxKeyword, PxValue>
