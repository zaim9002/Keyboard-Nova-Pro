package com.example.language.pack

import com.example.ime.layout.KeyModel
import com.example.ime.layout.KeyType

object ArabicLanguagePack {
    val tashkeelSymbols = listOf("َ", "ً", "ُ", "ٌ", "ِ", "ٍ", "ْ", "ّ", "ـ", "؟")
    val arabicNumerals = listOf("١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩", "٠")
    val englishNumerals = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")

    val row1 = listOf(
        KeyModel("ض", secondaryText = "١", popupOptions = listOf("ض", "1")),
        KeyModel("ص", secondaryText = "٢", popupOptions = listOf("ص", "2")),
        KeyModel("ث", secondaryText = "٣", popupOptions = listOf("ث", "3")),
        KeyModel("ق", secondaryText = "٤", popupOptions = listOf("ق", "4")),
        KeyModel("ف", secondaryText = "٥", popupOptions = listOf("ف", "5", "ڤ")),
        KeyModel("غ", secondaryText = "٦", popupOptions = listOf("غ", "6")),
        KeyModel("ع", secondaryText = "٧", popupOptions = listOf("ع", "7")),
        KeyModel("ه", secondaryText = "٨", popupOptions = listOf("ه", "8", "ة", "هـ")),
        KeyModel("خ", secondaryText = "٩", popupOptions = listOf("خ", "9")),
        KeyModel("ح", secondaryText = "٠", popupOptions = listOf("ح", "0")),
        KeyModel("ج", popupOptions = listOf("چ")),
        KeyModel("د", popupOptions = listOf("دّ"))
    )

    val row2 = listOf(
        KeyModel("ش"),
        KeyModel("س"),
        KeyModel("ي", popupOptions = listOf("ى", "ئ", "ي")),
        KeyModel("ب", popupOptions = listOf("پ")),
        KeyModel("ل", popupOptions = listOf("لا", "لإ", "لأ", "لآ")),
        KeyModel("ا", popupOptions = listOf("أ", "إ", "آ", "ء", "ٱ")),
        KeyModel("ت", popupOptions = listOf("ة", "ت")),
        KeyModel("ن"),
        KeyModel("م"),
        KeyModel("ك", popupOptions = listOf("گ", "ك")),
        KeyModel("ط", popupOptions = listOf("ظ"))
    )

    val row3 = listOf(
        KeyModel("ـَ", type = KeyType.TASHKEEL, weight = 1.3f),
        KeyModel("ئ"),
        KeyModel("ء"),
        KeyModel("ؤ"),
        KeyModel("ر", popupOptions = listOf("ڕ", "ر")),
        KeyModel("ى", popupOptions = listOf("ئ")),
        KeyModel("ة", popupOptions = listOf("ه")),
        KeyModel("و", popupOptions = listOf("ؤ", "و")),
        KeyModel("ز", popupOptions = listOf("ژ")),
        KeyModel("ظ"),
        KeyModel("delete", type = KeyType.BACKSPACE, weight = 1.3f)
    )

    val richVocabulary = listOf(
        "الله", "السلام", "عليكم", "ورحمة", "وبركاته", "شكرا", "جزيلا", "صباح",
        "الخير", "مساء", "النور", "كيف", "حالك", "الحمد", "لله", "أنا", "بخير",
        "إن", "شاء", "الله", "ما", "تبارك", "أهلاً", "وسهلاً", "مرحباً", "بالتوفيق",
        "ألف", "مبروك", "كل", "عام", "وأنتم", "بخير", "رمضان", "مبارك", "عيد",
        "سعيد", "جزاك", "خيراً", "بارك", "فيك", "أعتذر", "عن", "التأخير",
        "نعم", "بالتأكيد", "حسناً", "تمام", "أبشر", "من", "فضلك", "لو", "سمحت",
        "في", "أمان", "الله", "وحفظه", "مع", "السلامة", "إلى", "اللقاء", "أراك",
        "قريباً", "دمت", "سيدنا", "محمد", "صلى", "عليه", "وسلم", "العفو", "يسعدك"
    )
}
