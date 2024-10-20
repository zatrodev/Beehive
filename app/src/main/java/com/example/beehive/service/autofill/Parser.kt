package com.example.beehive.service.autofill

import android.app.assist.AssistStructure
import android.app.assist.AssistStructure.ViewNode
import android.text.InputType
import android.view.accessibility.AccessibilityNodeInfo
import android.view.autofill.AutofillId
import androidx.autofill.HintConstants.AUTOFILL_HINT_EMAIL_ADDRESS
import androidx.autofill.HintConstants.AUTOFILL_HINT_PASSWORD
import androidx.autofill.HintConstants.AUTOFILL_HINT_USERNAME
import com.example.beehive.utils.windows

class Parser<T>(
    private val structure: T,
) {
    data class AutofillData(
        var usernameId: AutofillId? = null,
        var passwordId: AutofillId? = null,
        var usernameValue: String = "",
        var passwordValue: String = "",
        var focusedFieldId: AutofillId? = null,
        var appUri: String = "",
    )

    data class AutofillNodes(
        var usernameNode: AccessibilityNodeInfo? = null,
        var passwordNode: AccessibilityNodeInfo? = null,
    )

    lateinit var autofillData: AutofillData
        private set

    companion object {
        fun findNodeByAutofillId(
            structure: AssistStructure,
            autofillId: AutofillId,
        ): ViewNode? {
            structure.windows.mapNotNull { window ->
                window.rootViewNode
            }.forEach { viewNode ->
                if (viewNode.autofillId == autofillId)
                    return viewNode
            }

            return null
        }
    }

    init {
        when (structure) {
            is AssistStructure -> autofillData = parseStructure()
            is AccessibilityNodeInfo -> Unit
        }
    }

    private fun parseStructure(): AutofillData {
        return (structure as AssistStructure).windows
            .mapNotNull { window -> window.rootViewNode }
            .fold(AutofillData()) { parsed, node -> getAutofillableFields(node, parsed) }
    }

    private fun getAutofillableFields(
        viewNode: ViewNode,
        autofillData: AutofillData = AutofillData(),
    ): AutofillData {
        if (autofillData.usernameId != null && autofillData.passwordId != null)
            return autofillData

        autofillData.usernameId = autofillData.usernameId ?: identifyEmailField(viewNode)
        autofillData.usernameValue = autofillData.usernameValue.ifEmpty {
            autofillData.usernameId?.let { viewNode.autofillValue?.toString() } ?: ""
        }

        autofillData.passwordId = autofillData.passwordId ?: identifyPasswordField(viewNode)
        autofillData.passwordValue = autofillData.passwordValue.ifEmpty {
            autofillData.passwordId?.let { viewNode.autofillValue?.toString() } ?: ""
        }

        autofillData.focusedFieldId =
            autofillData.focusedFieldId ?: identifyFocusedField(viewNode)

        autofillData.appUri = viewNode.idPackage ?: autofillData.appUri

        for (i in 0 until viewNode.childCount)
            getAutofillableFields(viewNode.getChildAt(i), autofillData)

        return autofillData
    }

    fun isViewAutofillable(
        viewNode: AccessibilityNodeInfo = structure as AccessibilityNodeInfo,
        autofillNodes: AutofillNodes = AutofillNodes(),
    ): Boolean {
        autofillNodes.usernameNode =
            autofillNodes.usernameNode ?: identifyEmailField(viewNode)
        autofillNodes.passwordNode =
            autofillNodes.passwordNode ?: identifyPasswordField(viewNode)

        if (autofillNodes.usernameNode != null || autofillNodes.passwordNode != null)
            return true

        return (0 until viewNode.childCount).any { i ->
            val child = viewNode.getChild(i) ?: return@any false
            isViewAutofillable(child, autofillNodes)
        }
    }

    private fun identifyEmailField(
        viewNode: AccessibilityNodeInfo,
    ): AccessibilityNodeInfo? {
        val className = viewNode.className ?: return null
        if (!className.contains("EditText")) return null

        if (viewNode.text?.contains(
                "email",
                ignoreCase = true
            ) == true || viewNode.hintText?.contains(
                "email",
                ignoreCase = true
            ) == true || viewNode.text?.contains(
                "username",
                ignoreCase = true
            ) == true || viewNode.hintText?.contains("username", ignoreCase = true) == true
        ) return viewNode

        if (viewNode.inputType and (InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) != 0)
            return viewNode

        return null
    }

    private fun identifyPasswordField(
        viewNode: AccessibilityNodeInfo,
    ): AccessibilityNodeInfo? {
        val className = viewNode.className ?: return null
        if (!className.contains("EditText")) return null

        if (viewNode.isPassword)
            return viewNode

        if (viewNode.text?.contains("password", ignoreCase = true) == true ||
            viewNode.hintText?.contains("password", ignoreCase = true) == true
        ) {
            return viewNode
        }

        return null
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
}