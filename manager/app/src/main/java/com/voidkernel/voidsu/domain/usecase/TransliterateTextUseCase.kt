package com.voidkernel.voidsu.domain.usecase

import com.voidkernel.voidsu.domain.text.TextTransliterator

class TransliterateTextUseCase(private val transliterator: TextTransliterator) {
    operator fun invoke(value: String): String = transliterator.transliterate(value)
}
