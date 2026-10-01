package com.example.engine

data class GifItem(
    val id: String,
    val title: String,
    val previewIcon: String,
    val description: String,
    val category: String,
    val gifUrl: String,
    val tags: List<String> = emptyList()
)

object GifData {
    val categories = listOf(
        "ضحك", "حب", "إعجاب", "حزن", "غضب", "حفلة", "موافق", "شكراً", "ميمز", "قطط"
    )

    val allGifs = listOf(
        GifItem(
            id = "g_laugh_1",
            title = "ضحك شديد",
            previewIcon = "😂",
            description = "ضحك هستيري",
            category = "ضحك",
            gifUrl = "https://media.giphy.com/media/10JhviFuU2gWD6/giphy.gif",
            tags = listOf("ضحك", "وناسة", "فرح", "lol", "laugh")
        ),
        GifItem(
            id = "g_laugh_2",
            title = "قط يضحك",
            previewIcon = "😸",
            description = "قطة لطيفة تبتسم",
            category = "قطط",
            gifUrl = "https://media.giphy.com/media/JIX9t2j0ZTN9S/giphy.gif",
            tags = listOf("قط", "حيوان", "كيوت", "cat")
        ),
        GifItem(
            id = "g_love_1",
            title = "قلوب حب",
            previewIcon = "💖",
            description = "قلوب متحركة",
            category = "حب",
            gifUrl = "https://media.giphy.com/media/l4pTdcifPZLpDjL1e/giphy.gif",
            tags = listOf("حب", "عشق", "قلب", "love")
        ),
        GifItem(
            id = "g_admire_1",
            title = "تصفيق وإعجاب",
            previewIcon = "👏",
            description = "تصفيق حار",
            category = "إعجاب",
            gifUrl = "https://media.giphy.com/media/26BRv0ThflsHCqDrG/giphy.gif",
            tags = listOf("برافو", "فنان", "إعجاب", "clap")
        ),
        GifItem(
            id = "g_party_1",
            title = "احتفال ومرح",
            previewIcon = "🎉",
            description = "ألعاب نارية وحفلة",
            category = "حفلة",
            gifUrl = "https://media.giphy.com/media/26tOZ42Mg6pbTUPHW/giphy.gif",
            tags = listOf("حفلة", "احتفال", "مبروك", "party")
        ),
        GifItem(
            id = "g_yes_1",
            title = "موافق تماماً",
            previewIcon = "👍",
            description = "إبهام للأعلى",
            category = "موافق",
            gifUrl = "https://media.giphy.com/media/111ebonMs90YLu/giphy.gif",
            tags = listOf("نعم", "موافق", "تمام", "yes")
        ),
        GifItem(
            id = "g_thanks_1",
            title = "شكراً جزيلاً",
            previewIcon = "🙏",
            description = "انحناءة شكر وتقدير",
            category = "شكراً",
            gifUrl = "https://media.giphy.com/media/osjgQPWRx3cac/giphy.gif",
            tags = listOf("شكرا", "تسلم", "ممتن", "thanks")
        ),
        GifItem(
            id = "g_meme_1",
            title = "ميمز رائج",
            previewIcon = "🕶️",
            description = "ميمز مضحك",
            category = "ميمز",
            gifUrl = "https://media.giphy.com/media/xT9IgG50Fb7Mi0prBC/giphy.gif",
            tags = listOf("ميمز", "ترند", "فلة", "meme")
        )
    )

    fun getByCategory(category: String): List<GifItem> {
        val direct = allGifs.filter { it.category == category }
        if (direct.isNotEmpty()) return direct
        val cleanCat = category.replace(Regex("[^\\p{L}\\p{Nd}]"), "").trim()
        return allGifs.filter { item ->
            val itemCatClean = item.category.replace(Regex("[^\\p{L}\\p{Nd}]"), "").trim()
            itemCatClean.contains(cleanCat) || cleanCat.contains(itemCatClean)
        }.ifEmpty { allGifs.take(6) }
    }

    fun searchGifs(query: String): List<GifItem> {
        if (query.isBlank()) return allGifs
        val q = query.trim().lowercase()
        return allGifs.filter { item ->
            item.title.lowercase().contains(q) ||
            item.description.lowercase().contains(q) ||
            item.category.lowercase().contains(q) ||
            item.tags.any { it.lowercase().contains(q) }
        }
    }
}
