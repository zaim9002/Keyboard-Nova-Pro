package com.example.engine

data class WordDefinition(
    val word: String,
    val definition: String,
    val synonyms: List<String>,
    val example: String
)

object DictionaryEngine {
    private val entries = mapOf(
        "سلام" to WordDefinition("سلام", "الأمان والسكينة، وهو اسم من أسماء الله الحسنى وتحية الإسلام.", listOf("سكينة", "أمان", "هدوء", "طمأنينة"), "عم السلام والوئام في ربوع الوطن."),
        "شكرا" to WordDefinition("شكرا", "التعبير عن الامتنان والعرفان بالجميل لصاحب المعروف.", listOf("امتنان", "تقدير", "عرفان"), "شكراً لك على حسن المساعدة والتعاون."),
        "علم" to WordDefinition("علم", "المعرفة واليقين وإدراك الشيء على حقيقته بحجة وبرهان.", listOf("معرفة", "دراية", "ثقافة"), "العلم نور يضيء دروب الحياة."),
        "عمل" to WordDefinition("عمل", "المهنة والنشاط والجهد المبذول لتحقيق نتيجة نافعة.", listOf("مهنة", "صنعة", "وظيفة"), "أتقن عملك تبلغ أملك."),
        "كتاب" to WordDefinition("كتاب", "مجموعة من الأوراق المطبوعة أو المخطوطة والمجلدة معاً.", listOf("مجلد", "سفر", "مخطوط"), "خير جليس في الزمان كتاب."),
        "نجاح" to WordDefinition("نجاح", "الوصول إلى الأهداف المنشودة وتحقيق الإنجاز والتفوق.", listOf("فوز", "توفيق", "ظفر"), "النجاح ثمرة الصبر والمثابرة."),
        "صبر" to WordDefinition("صبر", "حبس النفس على ما تكره وتحمل المشاق بهدوء دون جزع.", listOf("حلم", "أناة", "جلد"), "الصبر مفتاح الفرج."),
        "جمال" to WordDefinition("جمال", "صفة تلحظ في الأشياء وتبعث في النفس سروراً ورضاً.", listOf("بهاء", "حسن", "روعة"), "جمال الروح يعكس بهاء الوجه."),
        "حب" to WordDefinition("حب", "شعور عميق بالانجذاب والود والمودة تجاه الآخرين.", listOf("ود", "عشق", "مودة"), "الحب الصادق أساس كل علاقة متينة."),
        "قوة" to WordDefinition("قوة", "القدرة والصلابة والتمكن من مقاومة الشدائد والقيام بالأعباء.", listOf("عزم", "بأس", "طاقة"), "القوة الحقيقية تكمن في ضبط النفس عند الغضب."),
        // English words
        "hello" to WordDefinition("hello", "Used as a greeting or to begin a phone conversation.", listOf("hi", "greetings", "welcome"), "Hello, how are you today?"),
        "peace" to WordDefinition("peace", "Freedom from disturbance, tranquility, and harmony.", listOf("tranquility", "serenity", "calm"), "May peace prevail across the world."),
        "friend" to WordDefinition("friend", "A person whom one knows and with whom one has a bond of mutual affection.", listOf("companion", "pal", "ally"), "A friend in need is a friend indeed."),
        "code" to WordDefinition("code", "Instructions for a computer or a system of symbols.", listOf("program", "cipher", "script"), "The application is built with clean Kotlin code."),
        "smart" to WordDefinition("smart", "Having or showing a quick-witted intelligence.", listOf("clever", "intelligent", "sharp"), "This is a smart and fast mobile keyboard.")
    )

    fun lookup(query: String): WordDefinition? {
        val clean = query.trim().lowercase()
        return entries[clean] ?: entries.values.find {
            it.word.contains(clean, ignoreCase = true) || it.synonyms.any { s -> s.contains(clean, ignoreCase = true) }
        }
    }
}
