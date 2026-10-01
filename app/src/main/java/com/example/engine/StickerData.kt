package com.example.engine

data class StickerPack(
    val name: String,
    val icon: String,
    val stickers: List<StickerItem>
)

data class StickerItem(
    val id: String,
    val text: String,
    val displayIcon: String,
    val packName: String
)

object StickerData {
    val packs = listOf(
        StickerPack(
            name = "تحيات",
            icon = "👋",
            stickers = listOf(
                StickerItem("s1", "أهلاً وسهلاً يا غالي ✨", "👋", "تحيات"),
                StickerItem("s2", "صباح الورد والياسمين 🌸", "☀️", "تحيات"),
                StickerItem("s3", "مساء الأنوار والسرور 🌙", "🌆", "تحيات"),
                StickerItem("s4", "جمعة مباركة وطيبة 🕌", "🕌", "تحيات"),
                StickerItem("s5", "ألف ألف مبروك النجاح 🎉", "🎓", "تحيات"),
                StickerItem("s6", "كل عام وأنت بألف خير 🎂", "🎈", "تحيات"),
                StickerItem("s7", "الحمد لله على السلامة 💐", "💐", "تحيات"),
                StickerItem("s8", "في أمان الله وحفظه 🤲", "🤲", "تحيات")
            )
        ),
        StickerPack(
            name = "ردود سريعة",
            icon = "⚡",
            stickers = listOf(
                StickerItem("c1", "تمام، جاري المتابعة فوراً 🚀", "🚀", "ردود سريعة"),
                StickerItem("c2", "وصلت المعلومة، تسلم 🙏", "👌", "ردود سريعة"),
                StickerItem("c3", "أبشر بعزك، تم الأمر 💯", "💯", "ردود سريعة"),
                StickerItem("c4", "دقائق وأكون عندك 🚗", "🚗", "ردود سريعة"),
                StickerItem("c5", "أعتذر عن التأخير في الرد ⏳", "⏳", "ردود سريعة"),
                StickerItem("c6", "بانتظار التفاصيل يا بطل 📝", "📝", "ردود سريعة")
            )
        ),
        StickerPack(
            name = "أدعية",
            icon = "🤲",
            stickers = listOf(
                StickerItem("r1", "جزاك الله خيراً وأحسن إليك 🤲", "🤲", "أدعية"),
                StickerItem("r2", "بارك الله فيك وفي أهلك 🌟", "🌟", "أدعية"),
                StickerItem("r3", "أسأل الله لك التوفيق والسداد 🤍", "🤍", "أدعية"),
                StickerItem("r4", "شفاك الله وعافاك طهور إن شاء الله 🌿", "🌿", "أدعية"),
                StickerItem("r5", "رزقك الله من واسع فضله 💎", "💎", "أدعية"),
                StickerItem("r6", "حفظك الرحمن من كل شر وسوء 🛡️", "🛡️", "أدعية")
            )
        ),
        StickerPack(
            name = "كاوموجي ياباني",
            icon = "٩(◕‿◕)۶",
            stickers = listOf(
                StickerItem("k1", "(づ｡◕‿‿◕｡)づ", "🤗", "كاوموجي ياباني"),
                StickerItem("k2", "(ﾉ◕ヮ◕)ﾉ*:･ﾟ✧", "✨", "كاوموجي ياباني"),
                StickerItem("k3", "(ง'̀-'́)ง", "🥊", "كاوموجي ياباني"),
                StickerItem("k4", "¯\\_(ツ)_/¯", "🤷", "كاوموجي ياباني"),
                StickerItem("k5", "(｡♥‿♥｡)", "😍", "كاوموجي ياباني")
            )
        )
    )
}
