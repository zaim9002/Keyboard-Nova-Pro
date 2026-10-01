package com.example.ime.ui

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView

class SafeFallbackKeyboardView(
    context: Context,
    private val onTextInput: (String) -> Unit,
    private val onDelete: () -> Unit,
    private val onEnter: () -> Unit,
    private val onRetryCompose: () -> Unit
) : LinearLayout(context) {

    private var isArabic = true
    private var isEmojiMode = false
    private val contentLayout: LinearLayout

    private val arabicRows = listOf(
        listOf("ض", "ص", "ث", "ق", "ف", "غ", "ع", "ه", "خ", "ح", "ج", "د"),
        listOf("ش", "س", "ي", "ب", "ل", "ا", "ت", "ن", "م", "ك", "ط"),
        listOf("ئ", "ء", "ؤ", "ر", "ى", "ة", "و", "ز", "ظ")
    )

    private val englishRows = listOf(
        listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p"),
        listOf("a", "s", "d", "f", "g", "h", "j", "k", "l"),
        listOf("z", "x", "c", "v", "b", "n", "m")
    )

    private val numbersRow = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    private val popularEmojis = listOf("😀", "😂", "😍", "👍", "❤️", "🙏", "🔥", "✨", "🎉", "👏", "🌹", "💯", "🤲", "🇸🇦", "🤍", "😊")

    init {
        orientation = VERTICAL
        setBackgroundColor(Color.parseColor("#1E293B"))
        val pad = dpToPx(4)
        setPadding(pad, pad, pad, pad)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        val header = LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dpToPx(8), dpToPx(4), dpToPx(8), dpToPx(4))
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        val title = TextView(context).apply {
            text = "كيبورد محمد"
            setTextColor(Color.parseColor("#94A3B8"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            typeface = Typeface.DEFAULT_BOLD
            layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
        }

        val retryBtn = Button(context).apply {
            text = "إعادة تحميل"
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            background = createButtonDrawable(Color.parseColor("#0284C7"), dpToPx(6))
            setPadding(dpToPx(10), dpToPx(2), dpToPx(10), dpToPx(2))
            setOnClickListener { onRetryCompose() }
        }

        header.addView(title)
        header.addView(retryBtn)
        addView(header)

        addView(createKeyRow(numbersRow))

        contentLayout = LinearLayout(context).apply {
            orientation = VERTICAL
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
        addView(contentLayout)

        val bottomRow = LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        val langBtn = createActionButton(if (isArabic) "عربي" else "EN", 1.2f) { btn ->
            isArabic = !isArabic
            isEmojiMode = false
            (btn as? Button)?.text = if (isArabic) "عربي" else "EN"
            renderKeys()
        }
        bottomRow.addView(langBtn)

        val emojiBtn = createActionButton("😀", 1f) {
            isEmojiMode = !isEmojiMode
            renderKeys()
        }
        bottomRow.addView(emojiBtn)

        val spaceBtn = createActionButton("مسافة", 3.5f) {
            onTextInput(" ")
        }
        bottomRow.addView(spaceBtn)

        val deleteBtn = createActionButton("⌫", 1.2f) {
            onDelete()
        }
        bottomRow.addView(deleteBtn)

        val enterBtn = createActionButton("↵", 1.2f) {
            onEnter()
        }.apply {
            background = createButtonDrawable(Color.parseColor("#0284C7"), dpToPx(6))
        }
        bottomRow.addView(enterBtn)

        addView(bottomRow)
        renderKeys()
    }

    private fun renderKeys() {
        contentLayout.removeAllViews()
        if (isEmojiMode) {
            val scroll = HorizontalScrollView(context).apply {
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            }
            val emojiRow = LinearLayout(context).apply {
                orientation = HORIZONTAL
                setPadding(dpToPx(4), dpToPx(8), dpToPx(4), dpToPx(8))
            }
            for (emoji in popularEmojis) {
                val btn = Button(context).apply {
                    text = emoji
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                    background = createButtonDrawable(Color.parseColor("#334155"), dpToPx(6))
                    val p = LayoutParams(dpToPx(44), dpToPx(44)).apply {
                        setMargins(dpToPx(2), 0, dpToPx(2), 0)
                    }
                    layoutParams = p
                    setOnClickListener { onTextInput(emoji) }
                }
                emojiRow.addView(btn)
            }
            scroll.addView(emojiRow)
            contentLayout.addView(scroll)
        } else {
            val rows = if (isArabic) arabicRows else englishRows
            for (row in rows) {
                contentLayout.addView(createKeyRow(row))
            }
        }
    }

    private fun createKeyRow(keys: List<String>): LinearLayout {
        val row = LinearLayout(context).apply {
            orientation = HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
        for (k in keys) {
            val btn = Button(context).apply {
                text = k
                setTextColor(Color.WHITE)
                setTextSize(TypedValue.COMPLEX_UNIT_SP, if (isArabic) 18f else 16f)
                typeface = Typeface.DEFAULT_BOLD
                background = createButtonDrawable(Color.parseColor("#334155"), dpToPx(6))
                val p = LayoutParams(0, dpToPx(42), 1f).apply {
                    setMargins(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2))
                }
                layoutParams = p
                setPadding(0, 0, 0, 0)
                setOnClickListener { onTextInput(k) }
            }
            row.addView(btn)
        }
        return row
    }

    private fun createActionButton(label: String, weight: Float, onClick: (View) -> Unit): Button {
        return Button(context).apply {
            text = label
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            typeface = Typeface.DEFAULT_BOLD
            background = createButtonDrawable(Color.parseColor("#475569"), dpToPx(6))
            val p = LayoutParams(0, dpToPx(42), weight).apply {
                setMargins(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2))
            }
            layoutParams = p
            setPadding(0, 0, 0, 0)
            setOnClickListener { onClick(it) }
        }
    }

    private fun createButtonDrawable(color: Int, radiusPx: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = radiusPx.toFloat()
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}
