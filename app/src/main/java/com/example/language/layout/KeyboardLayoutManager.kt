package com.example.language.layout

import com.example.ime.layout.KeyModel
import com.example.ime.layout.KeyType
import com.example.language.model.LayoutFamily
import com.example.language.pack.ArabicLanguagePack

data class KeyboardLayoutData(
    val id: String,
    val row1: List<KeyModel>,
    val row2: List<KeyModel>,
    val row3: List<KeyModel>,
    val numberRow: List<String> = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0"),
    val spaceLabel: String = "Space",
    val isRtl: Boolean = false,
    val hasShift: Boolean = true
)

object KeyboardLayoutManager {
    fun getLayout(langId: String, layoutFamily: LayoutFamily): KeyboardLayoutData {
        return when {
            langId.startsWith("ar") || layoutFamily == LayoutFamily.ARABIC -> getArabicLayout(langId)
            langId == "fr" || layoutFamily == LayoutFamily.AZERTY -> getAzertyLayout()
            langId == "de" || layoutFamily == LayoutFamily.QWERTZ -> getQwertzLayout()
            langId == "ru" || langId == "uk" || layoutFamily == LayoutFamily.CYRILLIC -> getCyrillicLayout(langId)
            langId == "es" -> getSpanishLayout()
            langId == "tr" -> getTurkishLayout()
            else -> getStandardQwertyLayout(langId)
        }
    }

    private fun getArabicLayout(langId: String = "ar"): KeyboardLayoutData {
        val label = when (langId) {
            "ar-sa" -> "العربية (السعودية)"
            "ar-eg" -> "العربية (مصر)"
            "ar-ae" -> "العربية (الإمارات)"
            "ar-iq" -> "العربية (العراق)"
            "ar-sy" -> "العربية (سوريا)"
            "ar-ma" -> "العربية (المغرب)"
            "ar-dz" -> "العربية (الجزائر)"
            else -> "العربية"
        }
        return KeyboardLayoutData(
            id = langId,
            row1 = ArabicLanguagePack.row1,
            row2 = ArabicLanguagePack.row2,
            row3 = ArabicLanguagePack.row3,
            numberRow = ArabicLanguagePack.arabicNumerals,
            spaceLabel = label,
            isRtl = true,
            hasShift = false
        )
    }

    private fun getStandardQwertyLayout(langId: String): KeyboardLayoutData {
        val label = when (langId) {
            "en" -> "English"
            "en-us" -> "English (US)"
            "en-gb" -> "English (UK)"
            "id" -> "Indonesia"
            "it" -> "Italiano"
            "pt" -> "Português"
            else -> "Space"
        }
        val r1 = listOf(
            KeyModel("q", secondaryText = "1"),
            KeyModel("w", secondaryText = "2"),
            KeyModel("e", secondaryText = "3", popupOptions = listOf("é", "è", "ê", "ë")),
            KeyModel("r", secondaryText = "4"),
            KeyModel("t", secondaryText = "5"),
            KeyModel("y", secondaryText = "6"),
            KeyModel("u", secondaryText = "7", popupOptions = listOf("ú", "ù", "û", "ü")),
            KeyModel("i", secondaryText = "8", popupOptions = listOf("í", "ì", "î", "ï")),
            KeyModel("o", secondaryText = "9", popupOptions = listOf("ó", "ò", "ô", "ö")),
            KeyModel("p", secondaryText = "0")
        )
        val r2 = listOf(
            KeyModel("a", popupOptions = listOf("á", "à", "â", "ä", "ã")),
            KeyModel("s", popupOptions = listOf("ß", "$")),
            KeyModel("d"), KeyModel("f"), KeyModel("g"), KeyModel("h"),
            KeyModel("j"), KeyModel("k"), KeyModel("l")
        )
        val r3 = listOf(
            KeyModel("shift", type = KeyType.SHIFT, weight = 1.3f),
            KeyModel("z"), KeyModel("x"),
            KeyModel("c", popupOptions = listOf("ç")),
            KeyModel("v"), KeyModel("b"),
            KeyModel("n", popupOptions = listOf("ñ")),
            KeyModel("m"),
            KeyModel("delete", type = KeyType.BACKSPACE, weight = 1.3f)
        )
        return KeyboardLayoutData("en", r1, r2, r3, spaceLabel = label)
    }

    private fun getSpanishLayout(): KeyboardLayoutData {
        val r1 = listOf(
            KeyModel("q", secondaryText = "1"), KeyModel("w", secondaryText = "2"),
            KeyModel("e", secondaryText = "3", popupOptions = listOf("é")),
            KeyModel("r", secondaryText = "4"), KeyModel("t", secondaryText = "5"),
            KeyModel("y", secondaryText = "6"),
            KeyModel("u", secondaryText = "7", popupOptions = listOf("ú", "ü")),
            KeyModel("i", secondaryText = "8", popupOptions = listOf("í")),
            KeyModel("o", secondaryText = "9", popupOptions = listOf("ó")),
            KeyModel("p", secondaryText = "0")
        )
        val r2 = listOf(
            KeyModel("a", popupOptions = listOf("á")), KeyModel("s"), KeyModel("d"),
            KeyModel("f"), KeyModel("g"), KeyModel("h"), KeyModel("j"), KeyModel("k"),
            KeyModel("l"), KeyModel("ñ")
        )
        val r3 = listOf(
            KeyModel("shift", type = KeyType.SHIFT, weight = 1.3f),
            KeyModel("z"), KeyModel("x"), KeyModel("c"), KeyModel("v"),
            KeyModel("b"), KeyModel("n"), KeyModel("m"),
            KeyModel("delete", type = KeyType.BACKSPACE, weight = 1.3f)
        )
        return KeyboardLayoutData("es", r1, r2, r3, spaceLabel = "Español")
    }

    private fun getAzertyLayout(): KeyboardLayoutData {
        val r1 = listOf(
            KeyModel("a", secondaryText = "1", popupOptions = listOf("à", "â")),
            KeyModel("z", secondaryText = "2"),
            KeyModel("e", secondaryText = "3", popupOptions = listOf("é", "è", "ê", "ë")),
            KeyModel("r", secondaryText = "4"), KeyModel("t", secondaryText = "5"),
            KeyModel("y", secondaryText = "6"),
            KeyModel("u", secondaryText = "7", popupOptions = listOf("ù", "û", "ü")),
            KeyModel("i", secondaryText = "8", popupOptions = listOf("î", "ï")),
            KeyModel("o", secondaryText = "9", popupOptions = listOf("ô", "œ")),
            KeyModel("p", secondaryText = "0")
        )
        val r2 = listOf(
            KeyModel("q"), KeyModel("s"), KeyModel("d"), KeyModel("f"),
            KeyModel("g"), KeyModel("h"), KeyModel("j"), KeyModel("k"), KeyModel("l"),
            KeyModel("m")
        )
        val r3 = listOf(
            KeyModel("shift", type = KeyType.SHIFT, weight = 1.3f),
            KeyModel("w"), KeyModel("x"),
            KeyModel("c", popupOptions = listOf("ç")),
            KeyModel("v"), KeyModel("b"), KeyModel("n"),
            KeyModel("delete", type = KeyType.BACKSPACE, weight = 1.3f)
        )
        return KeyboardLayoutData("fr", r1, r2, r3, spaceLabel = "Français")
    }

    private fun getQwertzLayout(): KeyboardLayoutData {
        val r1 = listOf(
            KeyModel("q", secondaryText = "1"), KeyModel("w", secondaryText = "2"),
            KeyModel("e", secondaryText = "3"), KeyModel("r", secondaryText = "4"),
            KeyModel("t", secondaryText = "5"), KeyModel("z", secondaryText = "6"),
            KeyModel("u", secondaryText = "7"), KeyModel("i", secondaryText = "8"),
            KeyModel("o", secondaryText = "9"), KeyModel("p", secondaryText = "0"),
            KeyModel("ü")
        )
        val r2 = listOf(
            KeyModel("a", popupOptions = listOf("ä")), KeyModel("s", popupOptions = listOf("ß")),
            KeyModel("d"), KeyModel("f"), KeyModel("g"), KeyModel("h"), KeyModel("j"),
            KeyModel("k"), KeyModel("l"), KeyModel("ö"), KeyModel("ä")
        )
        val r3 = listOf(
            KeyModel("shift", type = KeyType.SHIFT, weight = 1.3f),
            KeyModel("y"), KeyModel("x"), KeyModel("c"), KeyModel("v"),
            KeyModel("b"), KeyModel("n"), KeyModel("m"),
            KeyModel("delete", type = KeyType.BACKSPACE, weight = 1.3f)
        )
        return KeyboardLayoutData("de", r1, r2, r3, spaceLabel = "Deutsch")
    }

    private fun getCyrillicLayout(langId: String): KeyboardLayoutData {
        val r1 = listOf(
            KeyModel("й"), KeyModel("ц"), KeyModel("у"), KeyModel("к"),
            KeyModel("е"), KeyModel("н"), KeyModel("г"), KeyModel("ш"),
            KeyModel("щ"), KeyModel("з"), KeyModel("х"), KeyModel("ъ")
        )
        val r2 = listOf(
            KeyModel("ф"), KeyModel("ы"), KeyModel("в"), KeyModel("а"),
            KeyModel("п"), KeyModel("р"), KeyModel("о"), KeyModel("л"),
            KeyModel("д"), KeyModel("ж"), KeyModel("э")
        )
        val r3 = listOf(
            KeyModel("shift", type = KeyType.SHIFT, weight = 1.3f),
            KeyModel("я"), KeyModel("ч"), KeyModel("с"), KeyModel("м"),
            KeyModel("и"), KeyModel("т"), KeyModel("ь"), KeyModel("б"), KeyModel("ю"),
            KeyModel("delete", type = KeyType.BACKSPACE, weight = 1.3f)
        )
        return KeyboardLayoutData(langId, r1, r2, r3, spaceLabel = "Русский")
    }

    private fun getTurkishLayout(): KeyboardLayoutData {
        val r1 = listOf(
            KeyModel("q"), KeyModel("w"), KeyModel("e"), KeyModel("r"), KeyModel("t"),
            KeyModel("y"), KeyModel("u"), KeyModel("ı"), KeyModel("o"), KeyModel("p"),
            KeyModel("ğ"), KeyModel("ü")
        )
        val r2 = listOf(
            KeyModel("a"), KeyModel("s"), KeyModel("d"), KeyModel("f"), KeyModel("g"),
            KeyModel("h"), KeyModel("j"), KeyModel("k"), KeyModel("l"), KeyModel("ş"),
            KeyModel("i")
        )
        val r3 = listOf(
            KeyModel("shift", type = KeyType.SHIFT, weight = 1.3f),
            KeyModel("z"), KeyModel("x"), KeyModel("c"), KeyModel("v"), KeyModel("b"),
            KeyModel("n"), KeyModel("m"), KeyModel("ö"), KeyModel("ç"),
            KeyModel("delete", type = KeyType.BACKSPACE, weight = 1.3f)
        )
        return KeyboardLayoutData("tr", r1, r2, r3, spaceLabel = "Türkçe")
    }
}
