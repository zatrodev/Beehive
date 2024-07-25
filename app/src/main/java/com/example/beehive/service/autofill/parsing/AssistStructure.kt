package com.example.beehive.service.autofill.parsing

import android.app.assist.AssistStructure
import com.example.beehive.service.autofill.ParsedStructure
import com.example.beehive.utils.windows

fun parseStructure(
    structure: AssistStructure,
): ParsedStructure {
    return structure.windows
        .mapNotNull { it.rootViewNode }
        .fold(ParsedStructure()) { parsed, node -> parseNode(node, parsed) }
}