package com.example.beehive.utils

import com.example.beehive.data.Password

fun List<Password>.findFirstMatchingIndex(query: String): Int {
    return indexOfFirst { it.doesMatchSearchQuery(query) }
}