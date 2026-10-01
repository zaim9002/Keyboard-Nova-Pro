package com.example.ime.util

object CharacterVariants {
    private val arabicVariants: Map<String, List<String>> = mapOf(
        "ا" to listOf("أ", "إ", "آ", "ء", "ٱ", "1"),
        "ت" to listOf("ة", "ت"),
        "ي" to listOf("ى", "ئ", "ي"),
        "و" to listOf("ؤ", "و"),
        "ه" to listOf("ة", "هـ"),
        "ك" to listOf("گ", "ك"),
        "ف" to listOf("ڤ", "ف"),
        "ب" to listOf("پ", "ب"),
        "ج" to listOf("چ", "ج"),
        "ز" to listOf("ژ", "ز"),
        "ل" to listOf("لا", "لإ", "لأ", "لآ")
    )

    private val latinVariants: Map<Char, List<String>> = mapOf(
        'a' to listOf("a", "á", "à", "â", "ä", "ã"),
        'c' to listOf("c", "ç"),
        'e' to listOf("e", "é", "è", "ê", "ë"),
        'i' to listOf("i", "í", "ì", "î", "ï"),
        'n' to listOf("n", "ñ"),
        'o' to listOf("o", "ó", "ò", "ô", "ö", "õ"),
        's' to listOf("s", "ß", "$"),
        'u' to listOf("u", "ú", "ù", "û", "ü")
    )

    fun getVariants(primaryText: String, explicitPopupOptions: List<String> = emptyList()): List<String> {
        val result = mutableListOf<String>()
        if (explicitPopupOptions.isNotEmpty()) {
            result.addAll(explicitPopupOptions)
        }
        arabicVariants[primaryText]?.let {
            result.addAll(it)
        }
        if (primaryText.length == 1 && primaryText[0].isLetter()) {
            val char = primaryText[0]
            val isUpper = char.isUpperCase()
            val lowerChar = char.lowercaseChar()
            latinVariants[lowerChar]?.let { variants ->
                val adjusted = variants.map { if (isUpper) it.uppercase() else it.lowercase() }
                result.addAll(adjusted)
            }
        }
        if (!result.contains(primaryText)) {
            result.add(0, primaryText)
        }
        return result.distinct().filter { it.isNotBlank() }
    }
}
