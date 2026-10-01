package com.example.engine

import com.example.data.repository.ShortcutRepository
import com.example.data.repository.UserWordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SuggestionEngine(
    private val userWordRepository: UserWordRepository,
    private val shortcutRepository: ShortcutRepository
) {
    private val arabicNextWords = mapOf(
        "السلام" to listOf("عليكم", "ورحمة", "للجميع"),
        "عليكم" to listOf("ورحمة", "السلام"),
        "ورحمة" to listOf("الله", "وبركاته"),
        "صباح" to listOf("الخير", "الورد", "النور", "السعادة"),
        "مساء" to listOf("الخير", "الورد", "النور", "الجمال"),
        "كيف" to listOf("حالك", "صحتك", "الأمور"),
        "إن" to listOf("شاء", "شاء الله"),
        "شاء" to listOf("الله"),
        "ما" to listOf("شاء الله", "رأيك", "أخبارك"),
        "جزاك" to listOf("الله", "الله خيراً"),
        "بارك" to listOf("الله", "الله فيك"),
        "الحمد" to listOf("لله", "لله رب العالمين"),
        "ألف" to listOf("مبروك", "شكر", "تحية"),
        "كل" to listOf("عام", "عام وأنتم بخير", "يوم"),
        "في" to listOf("أمان", "أمان الله", "حفظ الله"),
        "مع" to listOf("السلامة", "خالص التقدير"),
        "شكرا" to listOf("جزيلا", "لك"),
        "شكراً" to listOf("جزيلاً", "لك"),
        "أنا" to listOf("بخير", "في الطريق", "هنا"),
        "أراك" to listOf("لاحقا", "قريبا"),
        "لا" to listOf("إله إلا الله", "حول ولا قوة", "مشكلة")
    )

    private val englishNextWords = mapOf(
        "how" to listOf("are", "is", "about", "do"),
        "thank" to listOf("you", "you very much", "you so much"),
        "good" to listOf("morning", "afternoon", "evening", "night", "luck"),
        "see" to listOf("you", "you soon", "you later", "it"),
        "let" to listOf("me", "us", "it", "know"),
        "i" to listOf("am", "will", "have", "think", "love", "can"),
        "you" to listOf("are", "can", "have", "will", "know"),
        "please" to listOf("let me know", "find attached", "call me", "check"),
        "looking" to listOf("forward", "for", "at", "good"),
        "what" to listOf("is", "are", "do you think", "time"),
        "have" to listOf("a great day", "a good one", "been", "done"),
        "nice" to listOf("to meet you", "work", "day", "one"),
        "where" to listOf("are you", "is", "were you"),
        "call" to listOf("me", "back", "you later")
    )

    private val arabicDictionary = (listOf(
        "الله", "محمد", "سلام", "السلام", "عليكم", "شكرا", "شكراً",
        "صباح", "مساء", "الخير", "كيف", "حالك", "بخير", "الحمد",
        "جميل", "رائع", "ممتاز", "أهلاً", "وسهلاً", "مرحباً", "مبروك",
        "توفيق", "سعادة", "صحة", "سلامة", "عافية", "صديقي", "أخي",
        "نعم", "كلا", "حسناً", "تمام", "أبشر", "عظيم", "مهم",
        "الآن", "اليوم", "غداً", "أمس", "هنا", "هناك", "معاً",
        "كتاب", "قلم", "جامعة", "مدرسة", "عمل", "وظيفة", "نجاح"
    ) + com.example.language.pack.ArabicLanguagePack.richVocabulary).distinct()

    private val englishDictionary = listOf(
        "the", "be", "to", "of", "and", "a", "in", "that", "have", "i",
        "it", "for", "not", "on", "with", "he", "as", "you", "do", "at",
        "this", "but", "his", "by", "from", "they", "we", "say", "her", "she",
        "or", "an", "will", "my", "one", "all", "would", "there", "their", "what",
        "so", "up", "out", "if", "about", "who", "get", "which", "go", "me",
        "when", "make", "can", "like", "time", "no", "just", "him", "know", "take",
        "people", "into", "year", "your", "good", "some", "could", "them", "see", "other",
        "than", "then", "now", "look", "only", "come", "its", "over", "think", "also",
        "back", "after", "use", "two", "how", "our", "work", "first", "well", "way",
        "even", "new", "want", "because", "any", "these", "give", "day", "most", "us",
        "hello", "thanks", "welcome", "please", "sorry", "meeting", "message", "today"
    )

    private val arabicTypoMap = mapOf(
        "انشاء" to "إن شاء", "ان" to "إن", "الى" to "إلى", "او" to "أو",
        "اذا" to "إذا", "اكبر" to "أكبر", "اصغر" to "أصغر", "احسن" to "أحسن",
        "افضل" to "أفضل", "اريد" to "أريد", "اكتب" to "أكتب", "اشكرك" to "أشكرك",
        "شكره" to "شكرة", "مدينه" to "مدينة", "مدرسه" to "مدرسة", "مكتبه" to "مكتبة",
        "جميله" to "جميلة", "عظيمه" to "عظيمة", "قويه" to "قوية", "مهمه" to "مهمة",
        "علي" to "على", "حتي" to "حتى", "متي" to "متى"
    )

    private val englishTypoMap = mapOf(
        "teh" to "the", "recieve" to "receive", "seperate" to "separate", "definately" to "definitely",
        "dont" to "don't", "cant" to "can't", "wont" to "won't", "im" to "I'm", "youre" to "you're",
        "theyre" to "they're", "alot" to "a lot", "untill" to "until"
    )

    private val arabicDictionarySet by lazy { arabicDictionary.toHashSet() }
    private val englishDictionarySet by lazy { englishDictionary.toHashSet() }

    fun warmUp() {
        try {
            arabicDictionarySet.size
            englishDictionarySet.size
        } catch (e: Throwable) {}
    }

    fun getAutoCorrection(word: String, isArabic: Boolean): String? {
        val clean = word.trim()
        if (clean.isEmpty()) return null
        if (isArabic) {
            arabicTypoMap[clean]?.let { return it }
        } else {
            englishTypoMap[clean.lowercase()]?.let { return it }
        }
        return null
    }

    suspend fun isWordKnown(word: String, isArabic: Boolean): Boolean {
        val clean = word.trim()
        if (clean.isEmpty()) return true
        val dictSet = if (isArabic) arabicDictionarySet else englishDictionarySet
        if (dictSet.contains(clean)) return true
        val userMatches = userWordRepository.getMatchingWords(clean)
        return userMatches.any { it.equals(clean, ignoreCase = true) }
    }

    suspend fun getSuggestions(
        currentWord: String,
        previousWord: String?,
        isArabic: Boolean
    ): List<String> = withContext(Dispatchers.Default) {
        val cleanCurrent = currentWord.trim()
        val suggestions = mutableListOf<String>()

        // 1. Shortcut
        if (cleanCurrent.isNotEmpty()) {
            val shortcut = shortcutRepository.findByTrigger(cleanCurrent)
            if (shortcut != null) {
                suggestions.add(shortcut.replacement)
            }
        }

        // 2. Correction
        if (cleanCurrent.isNotEmpty()) {
            val correction = getAutoCorrection(cleanCurrent, isArabic)
            if (correction != null && !correction.equals(cleanCurrent, ignoreCase = true)) {
                suggestions.add(correction)
            }
        }

        // 3. Next word prediction
        if (cleanCurrent.isEmpty()) {
            val prev = previousWord?.trim()?.lowercase() ?: ""
            if (prev.isNotEmpty()) {
                val nexts = if (isArabic) arabicNextWords[prev] else englishNextWords[prev]
                if (!nexts.isNullOrEmpty()) {
                    suggestions.addAll(nexts.take(4))
                }
            }
            if (suggestions.isEmpty()) {
                if (isArabic) {
                    suggestions.addAll(listOf("السلام", "شكراً", "صباح", "أهلاً"))
                } else {
                    suggestions.addAll(listOf("Hello", "Thanks", "How are you", "Sounds good"))
                }
            }
            return@withContext suggestions.distinct().take(10)
        }

        // 4. Personalized words
        val userMatches = userWordRepository.getMatchingWords(cleanCurrent)
        suggestions.addAll(userMatches)

        // 5. Dictionary prefix matching
        val dict = if (isArabic) arabicDictionary else englishDictionary
        val prefixMatches = dict.asSequence()
            .filter { it.startsWith(cleanCurrent, ignoreCase = true) && !it.equals(cleanCurrent, ignoreCase = true) }
            .take(8)
            .toList()
        suggestions.addAll(prefixMatches)

        if (suggestions.isEmpty()) {
            suggestions.add(cleanCurrent)
        }
        return@withContext suggestions.distinct().take(10)
    }

    suspend fun learnWord(word: String) {
        userWordRepository.learnWord(word)
    }
}
