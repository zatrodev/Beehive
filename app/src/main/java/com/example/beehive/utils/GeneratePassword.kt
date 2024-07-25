package com.example.beehive.utils

import com.example.beehive.ui.password.OptionType

fun generatePassword(
    length: Int,
    options: Map<OptionType, Boolean>,
): String {
    val usableChars = mutableListOf<Char>()

    if (options[OptionType.LowerCase] == true) usableChars.addAll('a'..'z')
    if (options[OptionType.UpperCase] == true) usableChars.addAll('A'..'Z')
    if (options[OptionType.Punctuations] == true) usableChars.addAll(
        listOf(
            '!',
            '@',
            '#',
            '$',
            '%',
            '^',
            '&',
            '*',
            '(',
            ')',
            '_',
            '-'
        )
    )
    if (options[OptionType.Numbers] == true) usableChars.addAll('0'..'9')

    return (1..length)
        .map { usableChars.random() }
        .joinToString("")
}

fun generatePassword(
    length: Int,
): String {
    val allChars = ('a'..'z') + ('A'..'Z') + ('0'..'9') + listOf(
        '!',
        '@',
        '#',
        '$',
        '%',
        '^',
        '&',
        '*',
        '(',
        ')',
        '_',
        '-'
    )

    return (1..length)
        .map { allChars.random() }
        .joinToString("")

}