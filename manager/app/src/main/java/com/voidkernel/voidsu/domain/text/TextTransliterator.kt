package com.voidkernel.voidsu.domain.text

fun interface TextTransliterator {
    fun transliterate(value: String): String
}
