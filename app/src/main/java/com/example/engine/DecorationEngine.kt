package com.example.engine

data class DecoratedItem(
    val title: String,
    val result: String,
    val category: String
)

object DecorationEngine {
    val readyPhrases = listOf(
        "﷽",
        "ﷺ",
        "ﷻ",
        "سبحان الله وبحمده سبحان الله العظيم",
        "لا إله إلا الله محمد رسول الله",
        "لا حول ولا قوة إلا بالله العلي العظيم",
        "أستغفر الله العظيم وأتوب إليه",
        "اللهم صل وسلم على نبينا محمد",
        "جزاك الله خيراً وبارك فيك",
        "السلام عليكم ورحمة الله وبركاته",
        "وعليكم السلام ورحمة الله وبركاته",
        "الحمد لله حمداً كثيراً طيباً مباركاً فيه",
        "حسبي الله ونعم الوكيل",
        "ما شاء الله تبارك الله",
        "كل عام وأنتم بخير وصحة وسلامة"
    )

    fun decorateText(input: String): List<DecoratedItem> {
        val text = input.trim()
        if (text.isEmpty()) return emptyList()

        val results = mutableListOf<DecoratedItem>()

        // 1. Arabic Kashida / Tatweel
        val kashida = text.mapIndexed { index, c ->
            if (index < text.length - 1 && isArabicLetter(c)) "${c}ـ" else "$c"
        }.joinToString("")
        results.add(DecoratedItem("كشيدة ومد", kashida, "تشكيل"))

        // 2. Ornaments and Borders
        results.add(DecoratedItem("أقواس مزخرفة", "« $text »", "أطر"))
        results.add(DecoratedItem("أقواس مزدوجة", "﴾ $text ﴿", "إسلامي"))
        results.add(DecoratedItem("إطار نجوم", "★ $text ★", "زخرفة"))
        results.add(DecoratedItem("إطار لمعان", "✨ $text ✨", "زخرفة"))
        results.add(DecoratedItem("إطار تاج", "👑 $text 👑", "زخرفة"))
        results.add(DecoratedItem("إطار قلوب", "💖 $text 💖", "رموز"))
        results.add(DecoratedItem("إطار وردة", "🌸 $text 🌸", "رموز"))
        results.add(DecoratedItem("إطار ماسي", "💎 $text 💎", "رموز"))
        results.add(DecoratedItem("إطار نار", "🔥 $text 🔥", "رموز"))
        results.add(DecoratedItem("إطار زهور", "❀ $text ❀", "زخرفة"))
        results.add(DecoratedItem("مربعات أنيقة", "【 $text 】", "أطر"))
        results.add(DecoratedItem("أقواس دائرية", "『 $text 』", "أطر"))
        results.add(DecoratedItem("إطار فراشة", "🦋 $text 🦋", "رموز"))
        results.add(DecoratedItem("خطوط عريضة", "━❮ $text ❯━", "أطر"))
        results.add(DecoratedItem("إطار نيون", "░▒▓█ $text █▓▒░", "سيبراني"))

        // 3. English Unicode Styles (if contains English letters)
        if (text.any { it in 'a'..'z' || it in 'A'..'Z' }) {
            results.add(DecoratedItem("دوائر (Circles)", toCircled(text), "لاتيني"))
            results.add(DecoratedItem("مربعات (Squares)", toSquared(text), "لاتيني"))
            results.add(DecoratedItem("مخطط (Outline)", toDoubleStruck(text), "لاتيني"))
            results.add(DecoratedItem("عريض مائل (Bold Italic)", toBoldItalic(text), "لاتيني"))
            results.add(DecoratedItem("متباعد (Spaced)", text.map { "$it " }.joinToString("").trim(), "لاتيني"))
        }

        return results
    }

    private fun isArabicLetter(c: Char): Boolean {
        return c in '\u0600'..'\u06FF' && c !in listOf('َ', 'ُ', 'ِ', 'ْ', 'ّ', 'ً', 'ٌ', 'ٍ')
    }

    private fun toCircled(text: String): String {
        return text.map { c ->
            when (c) {
                in 'a'..'z' -> Character.toChars(0x24D0 + (c - 'a')).concatToString()
                in 'A'..'Z' -> Character.toChars(0x24B6 + (c - 'A')).concatToString()
                in '1'..'9' -> Character.toChars(0x2460 + (c - '1')).concatToString()
                '0' -> "⓪"
                else -> c.toString()
            }
        }.joinToString("")
    }

    private fun toSquared(text: String): String {
        return text.map { c ->
            when (c) {
                in 'a'..'z' -> Character.toChars(0x1F130 + (c - 'a')).concatToString()
                in 'A'..'Z' -> Character.toChars(0x1F130 + (c - 'A')).concatToString()
                else -> c.toString()
            }
        }.joinToString("")
    }

    private fun toDoubleStruck(text: String): String {
        return text.map { c ->
            when (c) {
                in 'a'..'z' -> Character.toChars(0x1D552 + (c - 'a')).concatToString()
                in 'A'..'Z' -> Character.toChars(0x1D538 + (c - 'A')).concatToString()
                in '0'..'9' -> Character.toChars(0x1D7D8 + (c - '0')).concatToString()
                else -> c.toString()
            }
        }.joinToString("")
    }

    private fun toBoldItalic(text: String): String {
        return text.map { c ->
            when (c) {
                in 'a'..'z' -> Character.toChars(0x1D482 + (c - 'a')).concatToString()
                in 'A'..'Z' -> Character.toChars(0x1D468 + (c - 'A')).concatToString()
                else -> c.toString()
            }
        }.joinToString("")
    }

    val letterDecorationsMap: Map<String, List<String>> = mapOf(
        "ا" to listOf("أ", "إ", "آ", "ٱ", "ٲ", "ٳ", "إ", "ٵ", "اِ", "اُ", "اَ"),
        "ب" to listOf("پ", "ٻ", "ڀ", "بـ", "بّ", "بْ", "بَ", "بُ", "بِ"),
        "ت" to listOf("ٺ", "ٽ", "ٿ", "ټ", "تـ", "تّ", "تَ", "تُ", "تِ"),
        "ث" to listOf("ثـ", "ثّ", "ثَ", "ثُ", "ثِ"),
        "ج" to listOf("چ", "ڄ", "ڇ", "جـ", "جّ"),
        "ح" to listOf("حـ", "حّ", "حَ", "حُ", "حِ", "ځ"),
        "خ" to listOf("خـ", "خّ", "خَ", "خُ", "خِ"),
        "د" to listOf("ڈ", "ډ", "دّ", "دَ", "دُ"),
        "ذ" to listOf("ذّ", "ذَ", "ذُ"),
        "ر" to listOf("ڑ", "ڕ", "رّ", "رَ", "رُ", "رِ"),
        "ز" to listOf("ژ", "زّ", "زَ", "زُ"),
        "س" to listOf("سـ", "سّ", "سَ", "سُ", "سِ", "ښ", "ڛ"),
        "ش" to listOf("شـ", "شّ", "شَ", "شُ", "شِ", "ۺ"),
        "ص" to listOf("صـ", "صّ", "صَ", "صُ", "صِ", "ڝ"),
        "ض" to listOf("ضـ", "ضّ", "ضَ", "ضُ", "ضِ", "ڞ"),
        "ط" to listOf("طـ", "طّ", "طَ", "طُ"),
        "ظ" to listOf("ظـ", "ظّ", "ظَ", "ظُ"),
        "ع" to listOf("عـ", "عّ", "عَ", "عُ", "عِ", "ڠ"),
        "غ" to listOf("غـ", "غّ", "غَ", "غُ", "غِ"),
        "ف" to listOf("ڤ", "ڥ", "فـ", "فّ", "فَ", "فُ", "فِ", "ڡ"),
        "ق" to listOf("قـ", "قّ", "قَ", "قُ", "قِ"),
        "ك" to listOf("گ", "ک", "ڪ", "كـ", "كّ", "كِ"),
        "ل" to listOf("لـ", "لّ", "لَ", "لُ", "لِ", "لا", "لإ"),
        "م" to listOf("مـ", "مّ", "مَ", "مُ", "مِ", "۾"),
        "ن" to listOf("نـ", "نّ", "نَ", "نُ", "نِ", "ڼ"),
        "ه" to listOf("هـ", "ھ", "ة", "هّ", "هَ", "هـ"),
        "و" to listOf("ؤ", "ۄ", "ۅ", "ۆ", "وّ", "وَ"),
        "ي" to listOf("ى", "ئ", "يـ", "يّة", "يَ", "يُ", "يِ", "ێ", "ې", "ے"),
        "ء" to listOf("ئ", "ؤ", "ء"),
        "ة" to listOf("ه", "ة", "ـة"),
        "؟" to listOf("?", "!", "،", "؛", "…")
    )

    fun getLetterDecorations(letter: String): List<String> {
        val lower = letter.lowercase()
        val customList = letterDecorationsMap[lower] ?: letterDecorationsMap[letter]
        if (customList != null) return customList
        if (lower.length == 1 && lower[0] in 'a'..'z') {
            val c = lower[0]
            val circled = Character.toChars(0x24D0 + (c - 'a')).concatToString()
            val squared = Character.toChars(0x1F130 + (c - 'a')).concatToString()
            val outline = Character.toChars(0x1D552 + (c - 'a')).concatToString()
            val bold = Character.toChars(0x1D41A + (c - 'a')).concatToString()
            val italic = Character.toChars(0x1D44E + (c - 'a')).concatToString()
            return listOf(circled, squared, outline, bold, italic)
        }
        return emptyList()
    }
}
