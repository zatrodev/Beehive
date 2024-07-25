package com.example.beehive.service.autofill.parsing

import android.app.assist.AssistStructure.ViewNode
import android.text.InputType
import android.view.autofill.AutofillId
import com.example.beehive.service.autofill.ParsedStructure


fun parseNode(
    viewNode: ViewNode,
    parsedStructure: ParsedStructure = ParsedStructure(),
): ParsedStructure {
    parsedStructure.usernameId = parsedStructure.usernameId ?: identifyEmailField(viewNode)
    parsedStructure.usernameValue = parsedStructure.usernameValue.ifEmpty {
        parsedStructure.usernameId?.let { viewNode.autofillValue?.toString() } ?: ""
    }

    parsedStructure.passwordId = parsedStructure.passwordId ?: identifyPasswordField(viewNode)
    parsedStructure.passwordValue = parsedStructure.passwordValue.ifEmpty {
        parsedStructure.passwordId?.let { viewNode.autofillValue?.toString() } ?: ""
    }

    parsedStructure.appUri = viewNode.idPackage ?: parsedStructure.appUri

    for (i in 0 until viewNode.childCount)
        parseNode(viewNode.getChildAt(i), parsedStructure)

    return parsedStructure
}

private fun identifyEmailField(
    viewNode: ViewNode
): AutofillId? {
    val className = viewNode.className ?: return null
    if (!className.contains("EditText")) return null

    if (viewNode.inputType and InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS != 0)
        return viewNode.autofillId

    if (viewNode.text?.contains(
            "email",
            ignoreCase = true
        ) == true || viewNode.text?.contains("username", ignoreCase = true) == true
    ) return viewNode.autofillId


    return null
}

private fun identifyPasswordField(
    viewNode: ViewNode
): AutofillId? {
    val className = viewNode.className ?: return null
    if (!className.contains("EditText")) return null

    if (viewNode.inputType and InputType.TYPE_TEXT_VARIATION_PASSWORD != 0)
        return viewNode.autofillId

    if (!viewNode.autofillHints.isNullOrEmpty()) {
        if (viewNode.autofillHints!!.contains("password")) return viewNode.autofillId
    }

    if (viewNode.text?.contains("password", ignoreCase = true) == true ||
        viewNode.hint?.contains("password", ignoreCase = true) == true
    ) {
        return viewNode.autofillId
    }

    return null
}