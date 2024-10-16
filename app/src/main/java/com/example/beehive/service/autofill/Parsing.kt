package com.example.beehive.service.autofill

import android.app.assist.AssistStructure
import android.app.assist.AssistStructure.ViewNode
import android.text.InputType
import android.view.autofill.AutofillId
import androidx.autofill.HintConstants.AUTOFILL_HINT_EMAIL_ADDRESS
import androidx.autofill.HintConstants.AUTOFILL_HINT_PASSWORD
import androidx.autofill.HintConstants.AUTOFILL_HINT_USERNAME
import com.example.beehive.utils.windows

fun parseStructure(
    structure: AssistStructure,
): ParsedStructure {
    return structure.windows
        .mapNotNull { window -> window.rootViewNode }
        .fold(ParsedStructure()) { parsed, node -> parseNode(node, parsed) }
}

fun getValueFromStructure(
    structure: AssistStructure,
    autofillId: AutofillId,
): String? {
    structure.windows.forEach { window ->
        val value = getValueFromNode(window.rootViewNode, autofillId)
        if (value != null)
            return value
    }

    return null
}

fun getValueFromNode(
    viewNode: ViewNode,
    autofillId: AutofillId,
): String? {
    if (viewNode.autofillId == autofillId)
        return viewNode.autofillValue?.toString()

    return null
}

data class ParsedStructure(
    var usernameId: AutofillId? = null,
    var passwordId: AutofillId? = null,
    var usernameValue: String = "",
    var passwordValue: String = "",
    var focusedFieldId: AutofillId? = null,
    var appUri: String = "",
)

fun parseNode(
    viewNode: ViewNode,
    parsedStructure: ParsedStructure = ParsedStructure(),
): ParsedStructure {
    if (parsedStructure.usernameId != null && parsedStructure.passwordId != null)
        return parsedStructure

    parsedStructure.usernameId = parsedStructure.usernameId ?: identifyEmailField(viewNode)
    parsedStructure.usernameValue = parsedStructure.usernameValue.ifEmpty {
        parsedStructure.usernameId?.let { viewNode.autofillValue?.toString() } ?: ""
    }

    parsedStructure.passwordId = parsedStructure.passwordId ?: identifyPasswordField(viewNode)
    parsedStructure.passwordValue = parsedStructure.passwordValue.ifEmpty {
        parsedStructure.passwordId?.let { viewNode.autofillValue?.toString() } ?: ""
    }

    parsedStructure.focusedFieldId =
        parsedStructure.focusedFieldId ?: identifyFocusedField(viewNode)

    parsedStructure.appUri = viewNode.idPackage ?: parsedStructure.appUri

    for (i in 0 until viewNode.childCount)
        parseNode(viewNode.getChildAt(i), parsedStructure)

    return parsedStructure
}

private fun identifyFocusedField(
    viewNode: ViewNode,
): AutofillId? {
    if (!viewNode.isFocused)
        return null

    val className = viewNode.className ?: return null
    if (!className.contains("EditText"))
        return null

    return viewNode.autofillId
}

private fun identifyEmailField(
    viewNode: ViewNode,
): AutofillId? {
    val className = viewNode.className ?: return null
    if (!className.contains("EditText")) return null

    if (viewNode.autofillHints?.contains(AUTOFILL_HINT_EMAIL_ADDRESS) == true || viewNode.autofillHints?.contains(
            AUTOFILL_HINT_USERNAME
        ) == true
    )
        return viewNode.autofillId


    if (viewNode.text?.contains(
            "email",
            ignoreCase = true
        ) == true || viewNode.hint?.contains("email", ignoreCase = true) == true
    ) return viewNode.autofillId

    if (viewNode.idEntry?.isNotBlank() == true)
        return null

    if (viewNode.inputType and (InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) != 0)
        return viewNode.autofillId

    return null
}

private fun identifyPasswordField(
    viewNode: ViewNode,
): AutofillId? {
    val className = viewNode.className ?: return null
    if (!className.contains("EditText")) return null

    if (viewNode.autofillHints?.contains(AUTOFILL_HINT_PASSWORD) == true)
        return viewNode.autofillId

    if (viewNode.inputType and InputType.TYPE_TEXT_VARIATION_PASSWORD != 0)
        return viewNode.autofillId

    if (viewNode.text?.contains("password", ignoreCase = true) == true ||
        viewNode.hint?.contains("password", ignoreCase = true) == true
    ) {
        return viewNode.autofillId
    }

    return null
}