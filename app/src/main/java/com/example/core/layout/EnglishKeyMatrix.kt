package com.example.core.layout

object EnglishKeyMatrix {

    val row1 = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
    val row2 = listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
    val row3 = listOf("z", "x", "c", "v", "b", "n", "m")

    val numberRow = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")

    val row1NumberHints = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")

    val longPressVariants = mapOf(
        "q" to listOf("1"),
        "w" to listOf("2"),
        "e" to listOf("3", "é", "è", "ê", "ë"),
        "r" to listOf("4"),
        "t" to listOf("5"),
        "y" to listOf("6"),
        "u" to listOf("7", "ú", "ù", "û", "ü"),
        "i" to listOf("8", "í", "ì", "î", "ï"),
        "o" to listOf("9", "ó", "ò", "ô", "ö"),
        "p" to listOf("0"),
        "a" to listOf("@", "á", "à", "â", "ä", "ã"),
        "s" to listOf("$", "ß", "š"),
        "c" to listOf("ç"),
        "n" to listOf("ñ"),
        "." to listOf(",", "?", "!", ":", ";", "/", "@")
    )
}
