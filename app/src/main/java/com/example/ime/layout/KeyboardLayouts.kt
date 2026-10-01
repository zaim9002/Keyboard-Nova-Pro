package com.example.ime.layout

data class KeyModel(
    val primaryText: String,
    val secondaryText: String? = null,
    val type: KeyType = KeyType.CHARACTER,
    val popupOptions: List<String> = emptyList(),
    val weight: Float = 1f
)

enum class KeyType {
    CHARACTER,
    SHIFT,
    BACKSPACE,
    ENTER,
    SPACE,
    SWITCH_MODE,
    SWITCH_LANGUAGE,
    TASHKEEL,
    ACTION,
    SETTINGS
}

object KeyboardLayouts {
    val arabicTashkeel = listOf("َ", "ً", "ُ", "ٌ", "ِ", "ٍ", "ْ", "ّ", "ـ", "؟")

    val arabicQuickRow = listOf(
        KeyModel("ﷺ"),
        KeyModel("ﷻ"),
        KeyModel("لا", popupOptions = listOf("لإ", "لأ", "لآ")),
        KeyModel("إ", popupOptions = listOf("أ", "إ", "آ", "ء", "ٱ")),
        KeyModel("ة", popupOptions = listOf("ه", "ـة")),
        KeyModel("ى", popupOptions = listOf("ي", "ئ")),
        KeyModel("؟", popupOptions = listOf("?", "!", "،", "؛"))
    )

    val arabicNumbersRow = listOf(
        KeyModel("١", secondaryText = "1"),
        KeyModel("٢", secondaryText = "2"),
        KeyModel("٣", secondaryText = "3"),
        KeyModel("٤", secondaryText = "4"),
        KeyModel("٥", secondaryText = "5"),
        KeyModel("٦", secondaryText = "6"),
        KeyModel("٧", secondaryText = "7"),
        KeyModel("٨", secondaryText = "8"),
        KeyModel("٩", secondaryText = "9"),
        KeyModel("٠", secondaryText = "0")
    )

    val englishNumbersRow = listOf(
        KeyModel("1", secondaryText = "١"),
        KeyModel("2", secondaryText = "٢"),
        KeyModel("3", secondaryText = "٣"),
        KeyModel("4", secondaryText = "٤"),
        KeyModel("5", secondaryText = "٥"),
        KeyModel("6", secondaryText = "٦"),
        KeyModel("7", secondaryText = "٧"),
        KeyModel("8", secondaryText = "٨"),
        KeyModel("9", secondaryText = "٩"),
        KeyModel("0", secondaryText = "٠")
    )

    val arabicRow1 = listOf(
        KeyModel("ض", secondaryText = "+", popupOptions = listOf("+", "ض")),
        KeyModel("ص", secondaryText = "×", popupOptions = listOf("×", "ص")),
        KeyModel("ث", secondaryText = "÷", popupOptions = listOf("÷", "ث")),
        KeyModel("ق", secondaryText = "=", popupOptions = listOf("=", "ق")),
        KeyModel("ف", secondaryText = "/", popupOptions = listOf("/", "ڤ", "ف")),
        KeyModel("غ", secondaryText = "-", popupOptions = listOf("-", "غ")),
        KeyModel("ع", secondaryText = "<", popupOptions = listOf("<", "ع")),
        KeyModel("ه", secondaryText = ">", popupOptions = listOf(">", "ة", "هـ")),
        KeyModel("خ", secondaryText = "[", popupOptions = listOf("[", "خ")),
        KeyModel("ح", secondaryText = "]", popupOptions = listOf("]", "ح")),
        KeyModel("ج", secondaryText = "~", popupOptions = listOf("~", "چ", "ج")),
        KeyModel("د", secondaryText = "#", popupOptions = listOf("#", "د"))
    )

    val arabicRow2 = listOf(
        KeyModel("ش", secondaryText = "!"),
        KeyModel("س", secondaryText = "@"),
        KeyModel("ي", secondaryText = "#", popupOptions = listOf("ى", "ئ", "ي")),
        KeyModel("ب", secondaryText = "$", popupOptions = listOf("پ", "ب")),
        KeyModel("ل", secondaryText = "%", popupOptions = listOf("لا", "لإ", "لأ", "لآ")),
        KeyModel("ا", secondaryText = "أ", popupOptions = listOf("أ", "إ", "آ", "ء", "ٱ")),
        KeyModel("ت", secondaryText = "^", popupOptions = listOf("ة", "ت")),
        KeyModel("ن", secondaryText = "&"),
        KeyModel("م", secondaryText = "*"),
        KeyModel("ك", secondaryText = ")", popupOptions = listOf("گ", "ك")),
        KeyModel("ط", secondaryText = "(", popupOptions = listOf("ظ", "ط"))
    )

    val arabicRow3 = listOf(
        KeyModel("ئ", secondaryText = "ء"),
        KeyModel("ء"),
        KeyModel("ؤ"),
        KeyModel("ر", secondaryText = "'"),
        KeyModel("ى", secondaryText = "\""),
        KeyModel("ة", secondaryText = ":"),
        KeyModel("و", secondaryText = ";"),
        KeyModel("ز", secondaryText = ","),
        KeyModel("ظ", secondaryText = "؟")
    )

    val englishRow1 = listOf(
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

    val englishRow2 = listOf(
        KeyModel("a", popupOptions = listOf("á", "à", "â", "ä", "ã")),
        KeyModel("s", popupOptions = listOf("ß", "$")),
        KeyModel("d"),
        KeyModel("f"),
        KeyModel("g"),
        KeyModel("h"),
        KeyModel("j"),
        KeyModel("k"),
        KeyModel("l")
    )

    val englishRow3 = listOf(
        KeyModel("shift", type = KeyType.SHIFT, weight = 1.3f),
        KeyModel("z"),
        KeyModel("x"),
        KeyModel("c", popupOptions = listOf("ç")),
        KeyModel("v"),
        KeyModel("b"),
        KeyModel("n", popupOptions = listOf("ñ")),
        KeyModel("m"),
        KeyModel("delete", type = KeyType.BACKSPACE, weight = 1.3f)
    )

    val symbols1Row1 = listOf(
        KeyModel("1"), KeyModel("2"), KeyModel("3"), KeyModel("4"), KeyModel("5"),
        KeyModel("6"), KeyModel("7"), KeyModel("8"), KeyModel("9"), KeyModel("0")
    )
    val symbols1Row2 = listOf(
        KeyModel("+"), KeyModel("×"), KeyModel("÷"), KeyModel("="), KeyModel("/"),
        KeyModel("_"), KeyModel("<"), KeyModel(">"), KeyModel("["), KeyModel("]")
    )
    val symbols1Row3 = listOf(
        KeyModel("!"), KeyModel("@"), KeyModel("#"), KeyModel("~"), KeyModel("%"),
        KeyModel("^"), KeyModel("&"), KeyModel("*"), KeyModel("("), KeyModel(")")
    )
    val symbols1Row4 = listOf(
        KeyModel("-"), KeyModel("'"), KeyModel("\""), KeyModel(":"), KeyModel("؛"),
        KeyModel("،"), KeyModel("؟")
    )

    val symbols2Row1 = listOf(
        KeyModel("1"), KeyModel("2"), KeyModel("3"), KeyModel("4"), KeyModel("5"),
        KeyModel("6"), KeyModel("7"), KeyModel("8"), KeyModel("9"), KeyModel("0")
    )
    val symbols2Row2 = listOf(
        KeyModel("`"), KeyModel("•"), KeyModel("\\"), KeyModel("|"), KeyModel("√"),
        KeyModel("π"), KeyModel("{"), KeyModel("}"), KeyModel("©"), KeyModel("®")
    )
    val symbols2Row3 = listOf(
        KeyModel("£"), KeyModel("€"), KeyModel("¥"), KeyModel("¢"), KeyModel("°"),
        KeyModel("★"), KeyModel("$"), KeyModel("✓"), KeyModel("™"), KeyModel("¿")
    )
    val symbols2Row4 = listOf(
        KeyModel("¡"), KeyModel("§"), KeyModel("∆"), KeyModel("¶"), KeyModel("…"),
        KeyModel("“"), KeyModel("”")
    )

    val numpadRow1 = listOf(
        KeyModel("ABC", type = KeyType.SWITCH_MODE, weight = 1.2f),
        KeyModel("1", weight = 1f),
        KeyModel("2", weight = 1f),
        KeyModel("3", weight = 1f)
    )
    val numpadRow2 = listOf(
        KeyModel("+", weight = 1f),
        KeyModel("4", weight = 1f),
        KeyModel("5", weight = 1f),
        KeyModel("6", weight = 1f)
    )
    val numpadRow3 = listOf(
        KeyModel("-", weight = 1f),
        KeyModel("7", weight = 1f),
        KeyModel("8", weight = 1f),
        KeyModel("9", weight = 1f)
    )
    val numpadRow4 = listOf(
        KeyModel("0", weight = 1.5f),
        KeyModel(".", weight = 1f),
        KeyModel("⌫", type = KeyType.BACKSPACE, weight = 1.2f)
    )
}
