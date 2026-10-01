package com.example.core.layout

object ArabicKeyMatrix {

    val harakatRow = listOf("َ", "ً", "ُ", "ٌ", "ِ", "ٍ", "ْ", "ّ", "ـ", "؟")

    val row1 = listOf("ض", "ص", "ث", "ق", "ف", "غ", "ع", "ه", "خ", "ح", "ج", "د")
    val row2 = listOf("ش", "س", "ي", "ب", "ل", "ا", "ت", "ن", "م", "ك", "ط")
    val row3 = listOf("ئ", "ء", "ؤ", "ر", "ى", "ة", "و", "ز", "ظ")

    val numberRow = listOf("١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩", "٠")

    val longPressVariants = mapOf(
        "ا" to listOf("أ", "إ", "آ", "ء", "ٱ"),
        "ي" to listOf("ى", "ئ", "ي"),
        "و" to listOf("ؤ", "و"),
        "ت" to listOf("ة", "ت"),
        "ه" to listOf("ة", "هـ"),
        "ل" to listOf("لا", "لإ", "لأ", "لآ"),
        "ك" to listOf("گ", "ك"),
        "ف" to listOf("ڤ", "ف"),
        "ب" to listOf("پ", "ب"),
        "ج" to listOf("چ", "ج"),
        "ز" to listOf("ژ", "ز"),
        "؟" to listOf("?", "!", "،", "؛", "…"),
        "." to listOf("،", "؛", ":", "!", "؟", "-")
    )
}
