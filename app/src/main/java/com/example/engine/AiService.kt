package com.example.engine

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

enum class WritingTone(val title: String, val description: String, val icon: String) {
    FORMAL("رسمي", "صياغة فصحى راقية ومؤدبة", "👔"),
    PROFESSIONAL("احترافي", "أسلوب أعمال متزن وواضح", "💼"),
    FRIENDLY("ودي", "صياغة لطيفة ودافئة للأصدقاء", "😊"),
    CONCISE("مختصر", "حذف الزوائد والتركيز على الفكرة", "⚡"),
    POETIC("شعري / أدبي", "لغة بليغة ذات وقع موسيقي", "✨"),
    CASUAL_EMOJI("مرح مع إيموجي", "تعبير عفوي ملون بالإيموجي", "🎉"),
    PERSUASIVE("مقنع وقوي", "صياغة مؤثرة لتحفيز الطرف الآخر", "🎯")
}

data class ProofreadResult(
    val original: String,
    val corrected: String,
    val improvements: List<String>
)

data class SmartReply(
    val label: String,
    val text: String,
    val icon: String
)

object AiService {
    private const val TAG = "AiService"
    private const val GEMINI_MODEL = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$GEMINI_MODEL:generateContent"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .build()

    fun getResolvedApiKey(customKey: String?): String? {
        if (!customKey.isNullOrBlank()) return customKey.trim()
        val buildConfigKey = try {
            val field = com.example.BuildConfig::class.java.getField("GEMINI_API_KEY")
            val key = field.get(null) as? String
            if (key != null && key.isNotBlank() && key != "MY_GEMINI_API_KEY") key else null
        } catch (e: Throwable) {
            null
        }
        return buildConfigKey
    }

    suspend fun rewriteWithTone(
        text: String,
        tone: WritingTone,
        apiKey: String? = null
    ): String = withContext(Dispatchers.IO) {
        val clean = text.trim()
        if (clean.isEmpty()) return@withContext ""

        val effectiveKey = getResolvedApiKey(apiKey)
        if (!effectiveKey.isNullOrBlank()) {
            val prompt = """
                أعد صياغة النص التالي باللغة العربية بأسلوب (${tone.title}: ${tone.description}).
                حافظ على المعنى الأصلي دون إضافة مقدمات أو تفسيرات، أعد فقط النص المصاغ مباشرة:
                
                $clean
            """.trimIndent()
            val onlineResult = callGeminiRest(prompt, effectiveKey)
            if (!onlineResult.isNullOrBlank()) {
                return@withContext onlineResult.trim().removeSurrounding("\"", "\"")
            }
        }

        return@withContext localRewriteWithTone(clean, tone)
    }

    suspend fun proofreadAndCorrect(
        text: String,
        apiKey: String? = null
    ): ProofreadResult = withContext(Dispatchers.IO) {
        val clean = text.trim()
        if (clean.isEmpty()) return@withContext ProofreadResult("", "", emptyList())

        val effectiveKey = getResolvedApiKey(apiKey)
        if (!effectiveKey.isNullOrBlank()) {
            val prompt = """
                صحح الأخطاء الإملائية والنحوية والهمزات وعلامات الترقيم في النص التالي.
                أرجع الناتج بصيغة JSON حصراً بهذا الشكل:
                {
                  "corrected": "النص المصحح هنا",
                  "improvements": ["تصحيح همزة القطع", "تصحيح تاء مربوطة"]
                }
                
                النص:
                $clean
            """.trimIndent()
            val onlineResult = callGeminiRest(prompt, effectiveKey)
            if (!onlineResult.isNullOrBlank()) {
                try {
                    val jsonStr = extractJsonString(onlineResult)
                    val json = JSONObject(jsonStr)
                    val corrected = json.optString("corrected", clean)
                    val arr = json.optJSONArray("improvements")
                    val imps = mutableListOf<String>()
                    if (arr != null) {
                        for (i in 0 until arr.length()) {
                            imps.add(arr.getString(i))
                        }
                    }
                    if (imps.isEmpty()) imps.add("تم تدقيق النص بنجاح")
                    return@withContext ProofreadResult(clean, corrected, imps)
                } catch (e: Throwable) {
                    Log.w(TAG, "Error parsing online proofread JSON", e)
                }
            }
        }

        return@withContext localProofread(clean)
    }

    suspend fun generateSmartReplies(
        contextMessage: String,
        apiKey: String? = null
    ): List<SmartReply> = withContext(Dispatchers.IO) {
        val clean = contextMessage.trim()
        val effectiveKey = getResolvedApiKey(apiKey)
        if (clean.isNotEmpty() && !effectiveKey.isNullOrBlank()) {
            val prompt = """
                بناءً على الرسالة التالية:
                "$clean"
                اقترح 5 ردود ذكية وسريعة بصيغة JSON مصفوفة:
                [
                  {"label": "شكر", "text": "شكراً جزيلاً، أسعدتني!", "icon": "🙏"},
                  {"label": "موافقة", "text": "تمام، متفقين إن شاء الله.", "icon": "👍"},
                  {"label": "اعتذار", "text": "عذراً منك، لا أستطيع حالياً.", "icon": "🤝"},
                  {"label": "استفسار", "text": "هل يمكنك توضيح المزيد؟", "icon": "🤔"},
                  {"label": "تحية", "text": "أهلاً وسهلاً بك!", "icon": "👋"}
                ]
            """.trimIndent()
            val onlineResult = callGeminiRest(prompt, effectiveKey)
            if (!onlineResult.isNullOrBlank()) {
                try {
                    val jsonStr = extractJsonString(onlineResult)
                    val arr = JSONArray(jsonStr)
                    val replies = mutableListOf<SmartReply>()
                    for (i in 0 until arr.length()) {
                        val obj = arr.getJSONObject(i)
                        replies.add(
                            SmartReply(
                                label = obj.optString("label", "رد"),
                                text = obj.optString("text", ""),
                                icon = obj.optString("icon", "💬")
                            )
                        )
                    }
                    if (replies.isNotEmpty()) return@withContext replies
                } catch (e: Throwable) {
                    Log.w(TAG, "Error parsing online replies JSON", e)
                }
            }
        }

        return@withContext localSmartReplies(clean)
    }

    suspend fun continueText(
        text: String,
        apiKey: String? = null
    ): String = withContext(Dispatchers.IO) {
        val clean = text.trim()
        if (clean.isEmpty()) return@withContext ""

        val effectiveKey = getResolvedApiKey(apiKey)
        if (!effectiveKey.isNullOrBlank()) {
            val prompt = """
                أكمل كتابة الجملة أو الفكرة التالية بأسلوب عربي طبيعي وسلس وبدون تكرار:
                "$clean"
            """.trimIndent()
            val onlineResult = callGeminiRest(prompt, effectiveKey)
            if (!onlineResult.isNullOrBlank()) {
                return@withContext onlineResult.trim().removeSurrounding("\"", "\"")
            }
        }

        return@withContext when {
            clean.endsWith("إن شاء الله") -> "$clean سننجز كل المهام بنجاح وتفوق."
            clean.endsWith("السلام عليكم") -> "$clean ورحمة الله وبركاته، كيف الحال؟"
            clean.endsWith("شكراً لك") -> "$clean على اهتمامك ودعمك المتواصل."
            clean.endsWith("أرجو منكم") -> "$clean التكرم بالاطلاع وإفادتي برأيكم الكريم."
            clean.endsWith("يسعدني") -> "$clean ويشرفني التواصل معكم دائماً."
            else -> "$clean متمنياً لك دوام التوفيق والنجاح."
        }
    }

    suspend fun summarizeText(
        text: String,
        apiKey: String? = null
    ): String = withContext(Dispatchers.IO) {
        val clean = text.trim()
        if (clean.isEmpty()) return@withContext ""

        val effectiveKey = getResolvedApiKey(apiKey)
        if (!effectiveKey.isNullOrBlank()) {
            val prompt = """
                لخص النص التالي في سطر أو سطرين مركزين وواضحين:
                "$clean"
            """.trimIndent()
            val onlineResult = callGeminiRest(prompt, effectiveKey)
            if (!onlineResult.isNullOrBlank()) {
                return@withContext onlineResult.trim()
            }
        }

        val sentences = clean.split(Regex("[.!?\n]+")).filter { it.isNotBlank() }
        if (sentences.size <= 2) return@withContext clean
        return@withContext "${sentences.first().trim()} ${sentences.last().trim()}."
    }

    // --- Local Smart NLP Algorithms ---

    private fun localRewriteWithTone(text: String, tone: WritingTone): String {
        val clean = text.trim()
        return when (tone) {
            WritingTone.FORMAL -> {
                var res = clean
                res = res.replace(Regex("^(أريد|بدي)"), "أرجو التكرم بـ")
                res = res.replace("مشغول", "مرتبط بمهام أخرى")
                res = res.replace("بسرعة", "في أقرب فرصة ممكنة")
                res = res.replace("مع السلامة", "مع خالص التقدير والاحترام")
                res = res.replace("شكرا", "أتوجه لسيادتكم بجزيل الشكر والعرفان")
                res = res.replace("تمام", "تمت الموافقة والاعتماد")
                res = res.replace("أوكي", "حسناً، سيتم الأمر على النحو المطلوب")
                if (!res.startsWith("تحية طيبة") && !res.startsWith("السلام عليكم")) {
                    res = "تحية طيبة وبعد، $res"
                }
                if (!res.endsWith(".")) {
                    res = "$res، وتفضلوا بقبول فائق الاحترام والتقدير."
                }
                res
            }
            WritingTone.PROFESSIONAL -> {
                var res = clean
                res = res.replace(Regex("^(أريد|بدي)"), "يرجى التكرم بـ")
                res = res.replace("مشكلة", "تحدي يتطلب معالجة")
                res = res.replace("غلط", "ملاحظة بحاجة لمراجعة")
                res = res.replace("باي", "دمتم بخير ونجاح")
                res = res.replace("شكرا", "شكراً جزيلاً لتعاونكم المثمر")
                res = res.replace("تمام", "تم إنجاز المهمة بالشكل المطلوب")
                if (!res.endsWith(".")) {
                    res = "$res. نتطلع إلى استمرار التنسيق البناء."
                }
                res
            }
            WritingTone.FRIENDLY -> {
                var res = clean
                if (!res.startsWith("أهلاً") && !res.startsWith("مرحباً") && !res.startsWith("يا هلا")) {
                    res = "يا هلا والله! $res"
                }
                res = res.replace("شكرا", "تسلم يا غالي ويسعدك ربي")
                res = res.replace("تمام", "أحلى خبر والله يا بطل")
                if (!res.contains("🌸") && !res.contains("❤️")) {
                    res = "$res 🌸 كل الود والتقدير!"
                }
                res
            }
            WritingTone.CONCISE -> {
                var res = clean
                res = res.replace(Regex("^(أود أن أخبرك أن|في الحقيقة أن)"), "")
                res = res.replace(Regex("(بشكل كبير جداً|في أسرع وقت ممكن)"), "عاجلاً")
                res = res.replace(Regex("\\s+"), " ").trim()
                res
            }
            WritingTone.POETIC -> {
                var res = clean
                res = res.replace("صباح الخير", "أشرقت شمس الصباح بنور محياك")
                res = res.replace("مساء الخير", "طاب مساؤك بنفحات المسك والعنبر")
                res = res.replace("شكرا", "لك في حنايا القلب شكر لا توفيه الكلمات")
                res = res.replace("سلام", "سلام يعانق روحك بالسكينة والضياء")
                if (!res.endsWith(".")) {
                    res = "$res، كأنه نسيم عاطر يتهادى بين الكلمات ✨"
                }
                res
            }
            WritingTone.CASUAL_EMOJI -> {
                var res = clean
                res = res.replace("شكرا", "تسلملي يا فنان 🙏✨")
                res = res.replace("سلام", "سلامات يا غالي 👋😎")
                res = res.replace("مبروك", "ألف ألف مبروك يا وحش 🥳🎉")
                res = res.replace("تمام", "كله تمام التمام 💯👌")
                if (!res.contains("🔥") && !res.contains("✨")) {
                    res = "$res 🔥🚀"
                }
                res
            }
            WritingTone.PERSUASIVE -> {
                var res = clean
                if (!res.startsWith("من المؤكد") && !res.startsWith("الفرصة الآن")) {
                    res = "الفرصة الآن مواتية: $res"
                }
                if (!res.endsWith(".")) {
                    res = "$res. هذه خطوتنا الصحيحة نحو تحقيق أفضل النتائج المضمونة."
                }
                res
            }
        }
    }

    private fun localProofread(text: String): ProofreadResult {
        var corrected = text
        val improvements = mutableListOf<String>()

        // 1. Hamzas
        val hamzaPairs = listOf(
            Regex("\\bان\\b") to "إن",
            Regex("\\bالى\\b") to "إلى",
            Regex("\\bاو\\b") to "أو",
            Regex("\\bام\\b") to "أم",
            Regex("\\bاذا\\b") to "إذا",
            Regex("\\bاذ\\b") to "إذ",
            Regex("\\bاكثر\\b") to "أكثر",
            Regex("\\bاكبر\\b") to "أكبر",
            Regex("\\bاصغر\\b") to "أصغر",
            Regex("\\bاحسن\\b") to "أحسن",
            Regex("\\bافضل\\b") to "أفضل",
            Regex("\\bاريد\\b") to "أريد",
            Regex("\\bاعلم\\b") to "أعلم",
            Regex("\\bاكتب\\b") to "أكتب",
            Regex("\\bاشكرك\\b") to "أشكرك"
        )
        for ((regex, rep) in hamzaPairs) {
            if (regex.containsMatchIn(corrected)) {
                corrected = corrected.replace(regex, rep)
                if (!improvements.contains("تصحيح همزات القطع")) {
                    improvements.add("تصحيح همزات القطع")
                }
            }
        }

        // 2. Taa Marbuta
        val taaPairs = listOf(
            Regex("\\bشكره\\b") to "شكرة",
            Regex("\\bمدينه\\b") to "مدينة",
            Regex("\\bمدرسه\\b") to "مدرسة",
            Regex("\\bمكتبه\\b") to "مكتبة",
            Regex("\\bجميله\\b") to "جميلة",
            Regex("\\bعظيمه\\b") to "عظيمة",
            Regex("\\bقويه\\b") to "قوية",
            Regex("\\bمهمه\\b") to "مهمة",
            Regex("\\bخاصه\\b") to "خاصة"
        )
        for ((regex, rep) in taaPairs) {
            if (regex.containsMatchIn(corrected)) {
                corrected = corrected.replace(regex, rep)
                if (!improvements.contains("ضبط التاء المربوطة")) {
                    improvements.add("ضبط التاء المربوطة")
                }
            }
        }

        // 3. Yaa & Alef Maqsura
        val yaaPairs = listOf(
            Regex("\\bعلي\\b") to "على",
            Regex("\\bحتي\\b") to "حتى",
            Regex("\\bالي\\b") to "إلى",
            Regex("\\bمتي\\b") to "متى"
        )
        for ((regex, rep) in yaaPairs) {
            if (regex.containsMatchIn(corrected)) {
                corrected = corrected.replace(regex, rep)
                if (!improvements.contains("ضبط الياء والألف المقصورة")) {
                    improvements.add("ضبط الياء والألف المقصورة")
                }
            }
        }

        // 4. Common Compound Blunders
        if (corrected.contains("ان شاء الله") || corrected.contains("إنشاء الله") || corrected.contains("انشاء الله")) {
            corrected = corrected.replace(Regex("(إنشاء الله|انشاء الله|ان شاء الله)"), "إن شاء الله")
            improvements.add("تصحيح كتابة (إن شاء الله)")
        }
        if (corrected.contains("ماشاء الله") || corrected.contains("ما شاءالله")) {
            corrected = corrected.replace(Regex("(ماشاء الله|ما شاءالله)"), "ما شاء الله")
            improvements.add("تصحيح كتابة (ما شاء الله)")
        }
        if (corrected.contains("هاذا")) {
            corrected = corrected.replace(Regex("\\bهاذا\\b"), "هذا")
            improvements.add("تصحيح كتابة (هذا)")
        }
        if (corrected.contains("لكن")) {
            corrected = corrected.replace(Regex("\\bلاكن\\b"), "لكن")
            improvements.add("تصحيح كتابة (لكن)")
        }

        // 5. Spacing around punctuation
        val punctuationFixed = corrected
            .replace(Regex("\\s+([،؛:.!?])"), "$1")
            .replace(Regex("([،؛:.!?])(?=[^\\s0-9])"), "$1 ")
        if (punctuationFixed != corrected) {
            corrected = punctuationFixed
            improvements.add("ضبط مسافات علامات الترقيم")
        }

        if (improvements.isEmpty()) {
            improvements.add("النص خالي من الأخطاء الإملائية الواضحة")
        }

        return ProofreadResult(text, corrected, improvements)
    }

    private fun localSmartReplies(context: String): List<SmartReply> {
        return listOf(
            SmartReply("شكر وتقدير", "شكراً جزيلاً لك وبارك الله فيك 🙏", "🙏"),
            SmartReply("موافقة وتأكيد", "تمام، متفق معك وبإذن الله نبدأ 👍", "👍"),
            SmartReply("اعتذار بلباقة", "أعتذر منك، لا أستطيع المتابعة حالياً ويسعدني لاحقاً.", "🤝"),
            SmartReply("تحية وترحيب", "أهلاً وسهلاً بك، أسعدتني رسالتك 🌸", "👋"),
            SmartReply("طلب مهلة", "سأطلع على الموضوع وأوافيك بالرد قريباً إن شاء الله.", "⏳")
        )
    }

    private fun callGeminiRest(prompt: String, apiKey: String): String? {
        return try {
            val url = "$BASE_URL?key=$apiKey"
            val jsonBody = JSONObject().apply {
                val contentsArr = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArr = JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        }
                        put("parts", partsArr)
                    }
                    put(contentObj)
                }
                put("contents", contentsArr)
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                Log.w(TAG, "Gemini REST call failed with code: ${response.code}")
                return null
            }

            val responseBody = response.body?.string() ?: return null
            val respJson = JSONObject(responseBody)
            val candidates = respJson.optJSONArray("candidates") ?: return null
            if (candidates.length() == 0) return null
            val candidate = candidates.getJSONObject(0)
            val content = candidate.optJSONObject("content") ?: return null
            val parts = content.optJSONArray("parts") ?: return null
            if (parts.length() == 0) return null
            val text = parts.getJSONObject(0).optString("text", "")
            if (text.isNotEmpty()) text else null
        } catch (e: Throwable) {
            Log.w(TAG, "Gemini call exception: ${e.message}")
            null
        }
    }

    private fun extractJsonString(raw: String): String {
        val trimmed = raw.trim()
        val firstBrace = trimmed.indexOf('{')
        val lastBrace = trimmed.lastIndexOf('}')
        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            return trimmed.substring(firstBrace, lastBrace + 1)
        }
        val firstBracket = trimmed.indexOf('[')
        val lastBracket = trimmed.lastIndexOf(']')
        if (firstBracket != -1 && lastBracket != -1 && lastBracket > firstBracket) {
            return trimmed.substring(firstBracket, lastBracket + 1)
        }
        return trimmed
    }
}
