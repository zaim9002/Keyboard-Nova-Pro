package com.example.engine

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.concurrent.ConcurrentHashMap

object TranslationEngine {
    private const val TAG = "TranslationEngine"
    private val memoryCache = ConcurrentHashMap<String, String>()

    data class SupportedLanguage(val code: String, val nameAr: String, val nameEn: String, val flag: String)

    val supportedLanguages = listOf(
        SupportedLanguage("ar", "العربية", "Arabic", "🇸🇦"),
        SupportedLanguage("en", "الإنجليزية", "English", "🇺🇸"),
        SupportedLanguage("fr", "الفرنسية", "French", "🇫🇷"),
        SupportedLanguage("es", "الإسبانية", "Spanish", "🇪🇸"),
        SupportedLanguage("de", "الألمانية", "German", "🇩🇪"),
        SupportedLanguage("tr", "التركية", "Turkish", "🇹🇷"),
        SupportedLanguage("ru", "الروسية", "Russian", "🇷🇺"),
        SupportedLanguage("ur", "الأوردو", "Urdu", "🇵🇰"),
        SupportedLanguage("it", "الإيطالية", "Italian", "🇮🇹"),
        SupportedLanguage("id", "الإندونيسية", "Indonesian", "🇮🇩"),
        SupportedLanguage("fa", "الفارسية", "Persian", "🇮🇷"),
        SupportedLanguage("zh-CN", "الصينية", "Chinese", "🇨🇳"),
        SupportedLanguage("ja", "اليابانية", "Japanese", "🇯🇵"),
        SupportedLanguage("ko", "الكورية", "Korean", "🇰🇷"),
        SupportedLanguage("hi", "الهندية", "Hindi", "🇮🇳"),
        SupportedLanguage("pt", "البرتغالية", "Portuguese", "🇧🇷"),
        SupportedLanguage("nl", "الهولندية", "Dutch", "🇳🇱"),
        SupportedLanguage("el", "اليونانية", "Greek", "🇬🇷"),
        SupportedLanguage("he", "العبرية", "Hebrew", "🇮🇱"),
        SupportedLanguage("sv", "السويدية", "Swedish", "🇸🇪"),
        SupportedLanguage("pl", "البولندية", "Polish", "🇵🇱"),
        SupportedLanguage("uk", "الأوكرانية", "Ukrainian", "🇺🇦"),
        SupportedLanguage("ms", "الملايو", "Malay", "🇲🇾"),
        SupportedLanguage("th", "التايلاندية", "Thai", "🇹🇭")
    )

    private val offlineArToEn = mapOf(
        "مرحبا" to "Hello",
        "أهلا" to "Welcome",
        "السلام عليكم" to "Peace be upon you",
        "وعليكم السلام" to "And upon you peace",
        "كيف حالك" to "How are you?",
        "كيفك" to "How are you?",
        "أنا بخير" to "I am doing well",
        "الحمد لله" to "Praise be to God",
        "شكرا" to "Thank you",
        "شكرا جزيلا" to "Thank you very much",
        "عفوا" to "You're welcome",
        "لو سمحت" to "Please",
        "من فضلك" to "Excuse me",
        "نعم" to "Yes",
        "لا" to "No",
        "مع السلامة" to "Goodbye",
        "إلى اللقاء" to "See you later",
        "صباح الخير" to "Good morning",
        "مساء الخير" to "Good evening",
        "تصبح على خير" to "Good night",
        "مبروك" to "Congratulations",
        "ألف مبروك" to "Warm congratulations",
        "بالتوفيق" to "Good luck",
        "أحبك" to "I love you",
        "أنا في الطريق" to "I am on my way",
        "أين أنت" to "Where are you?",
        "ما رأيك" to "What do you think?",
        "ماذا تفعل" to "What are you doing?",
        "حسنا" to "Alright / Okay",
        "ممتاز" to "Great / Perfect",
        "تمام" to "Okay / Fine",
        "إن شاء الله" to "God willing",
        "ان شاء الله" to "God willing",
        "كل عام وأنتم بخير" to "Happy New Year / Best wishes",
        "رمضان مبارك" to "Ramadan Mubarak",
        "عيد مبارك" to "Eid Mubarak",
        "أحتاج مساعدة" to "I need help",
        "هل يمكنك مساعدتي" to "Can you help me?",
        "كم السعر" to "How much is it?",
        "كم الساعة" to "What time is it?",
        "أنا آسف" to "I am sorry",
        "سامحني" to "Forgive me",
        "لا مشكلة" to "No problem",
        "بكل سرور" to "With pleasure",
        "أراك لاحقا" to "See you later",
        "اتصل بي" to "Call me",
        "أرسل لي رسالة" to "Send me a message"
    )

    private val offlineEnToAr = offlineArToEn.entries.associate { (k, v) ->
        v.lowercase().trimEnd('?', '!') to k
    }

    fun translate(text: String, sourceLang: String = "ar", targetLang: String = "en"): String {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return ""
        val cacheKey = "${sourceLang}_${targetLang}_$trimmed"
        memoryCache[cacheKey]?.let { return it }

        if (sourceLang == "ar" && targetLang == "en") {
            offlineArToEn[trimmed]?.let { return it }
        } else if (sourceLang == "en" && targetLang == "ar") {
            offlineEnToAr[trimmed.lowercase().trimEnd('?', '!')]?.let { return it }
        }
        return trimmed
    }

    fun isArabicText(text: String): Boolean {
        return text.any { it in '\u0600'..'\u06FF' || it in '\u0750'..'\u077F' || it in '\u08A0'..'\u08FF' || it in '\uFB50'..'\uFDFF' || it in '\uFE70'..'\uFEFF' }
    }

    suspend fun translateAsync(
        text: String,
        sourceLang: String = "ar",
        targetLang: String = "en"
    ): String = withContext(Dispatchers.IO) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return@withContext ""

        var actualSource = sourceLang
        var actualTarget = targetLang
        val containsAr = isArabicText(trimmed)
        if (containsAr && actualTarget == "ar") {
            actualTarget = "en"
            actualSource = "ar"
        } else if (!containsAr && actualSource == "ar" && actualTarget != "ar") {
            actualSource = "auto"
            actualTarget = if (targetLang == "en") "ar" else targetLang
        }

        val cacheKey = "${actualSource}_${actualTarget}_$trimmed"
        memoryCache[cacheKey]?.let { return@withContext it }

        // Primary Online Endpoint (Google Translate Client GTX)
        try {
            val encodedQuery = URLEncoder.encode(trimmed, "UTF-8")
            val urlString = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=$actualSource&tl=$actualTarget&dt=t&q=$encodedQuery"
            val url = URL(urlString)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 3500
                readTimeout = 3500
                setRequestProperty("User-Agent", "Mozilla/5.0")
            }
            if (conn.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
                val jsonArray = JSONArray(response.toString())
                val sentencesArray = jsonArray.optJSONArray(0)
                if (sentencesArray != null && sentencesArray.length() > 0) {
                    val resultBuilder = StringBuilder()
                    for (i in 0 until sentencesArray.length()) {
                        val sentence = sentencesArray.optJSONArray(i)
                        if (sentence != null) {
                            resultBuilder.append(sentence.optString(0, ""))
                        }
                    }
                    val translated = resultBuilder.toString().trim()
                    if (translated.isNotEmpty()) {
                        memoryCache[cacheKey] = translated
                        return@withContext translated
                    }
                }
            }
        } catch (e: Throwable) {
            Log.w(TAG, "Google translation failed: ${e.message}")
        }

        // Secondary Online Fallback (MyMemory API)
        try {
            val encodedQuery = URLEncoder.encode(trimmed, "UTF-8")
            val src = if (actualSource == "auto") (if (containsAr) "ar" else "en") else actualSource
            val urlString = "https://api.mymemory.translated.net/get?q=$encodedQuery&langpair=$src|$actualTarget"
            val url = URL(urlString)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 3500
                readTimeout = 3500
                setRequestProperty("User-Agent", "Mozilla/5.0")
            }
            if (conn.responseCode == 200) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                reader.close()
                val json = org.json.JSONObject(response.toString())
                val resData = json.optJSONObject("responseData")
                val translated = resData?.optString("translatedText")?.trim() ?: ""
                if (translated.isNotEmpty() && !translated.startsWith("MYMEMORY WARNING")) {
                    memoryCache[cacheKey] = translated
                    return@withContext translated
                }
            }
        } catch (e: Throwable) {
            Log.w(TAG, "MyMemory fallback failed: ${e.message}")
        }

        // Offline Direct Fallback
        if (containsAr || actualSource == "ar") {
            offlineArToEn[trimmed]?.let {
                memoryCache[cacheKey] = it
                return@withContext it
            }
            val words = trimmed.split("\\s+".toRegex())
            val translatedWords = words.map { word -> offlineArToEn[word] ?: word }
            val combined = translatedWords.joinToString(" ")
            if (combined != trimmed) {
                return@withContext combined
            }
        } else {
            val key = trimmed.lowercase().trimEnd('?', '!')
            offlineEnToAr[key]?.let {
                memoryCache[cacheKey] = it
                return@withContext it
            }
            val words = trimmed.split("\\s+".toRegex())
            val translatedWords = words.map { word -> offlineEnToAr[word.lowercase()] ?: word }
            val combined = translatedWords.joinToString(" ")
            if (combined != trimmed) {
                return@withContext combined
            }
        }
        return@withContext trimmed
    }
}
