package com.example.beehive.service.autofill

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.PendingIntent
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.IntentSender
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.ImageButton
import android.widget.LinearLayout
import com.example.beehive.R
import com.example.beehive.auth.AuthActivity
import com.example.beehive.auth.SecretKeyManager
import com.example.beehive.data.container.AuthContainer
import com.example.beehive.data.container.AuthContainerImpl
import com.example.beehive.data.container.BeehiveContainer
import com.example.beehive.data.container.BeehiveContainerImpl
import com.example.beehive.data.credential.CredentialAndUser
import com.example.beehive.service.autofill.BeehiveAutofillService.Companion.EXTRA_FROM_SERVICE
import com.example.beehive.service.autofill.BeehiveAutofillService.Companion.EXTRA_IS_CHOOSE
import com.example.beehive.service.autofill.BeehiveAutofillService.Companion.EXTRA_PASSWORD_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class WebAutofillService : AccessibilityService(), IntentSender.OnFinished {
    private lateinit var beehiveContainer: BeehiveContainer
    private lateinit var authContainer: AuthContainer
    private lateinit var overlayView: LinearLayout
    private lateinit var windowManager: WindowManager

    private var showSuggestions = false
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onServiceConnected() {
        super.onServiceConnected()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        overlayView = LinearLayout(this)
        authContainer = AuthContainerImpl(this.applicationContext)

        val serviceInfo = AccessibilityServiceInfo()
        serviceInfo.apply {
            eventTypes =
                AccessibilityEvent.TYPE_VIEW_FOCUSED
            notificationTimeout = 100
            flags = flags or AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
        }

        this.serviceInfo = serviceInfo
    }

    override fun onSendFinished(
        intentSender: IntentSender?,
        intent: Intent?,
        resultCode: Int,
        resultData: String?,
        resultExtras: Bundle?,
    ) {
        windowManager.removeView(overlayView)
        overlayView = LinearLayout(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null)
            return

        val isEditText = event.className?.contains("EditText") ?: false

        if (!isEditText && overlayView.childCount > 0) {
            windowManager.removeView(overlayView)
            overlayView = LinearLayout(this)

            return
        }

        if (!isEditText || overlayView.childCount > 0)
            return

        val parser = Parser(rootInActiveWindow)
        if (parser.isViewAutofillable(rootInActiveWindow)) {
            val pageUrl =
                getUrl(rootInActiveWindow, addressBarId(event.source!!.packageName.toString()))
                    ?: return

            coroutineScope.launch {
                SecretKeyManager.setPassphrase(authContainer.dataStore)
                beehiveContainer = BeehiveContainerImpl(applicationContext)

                val credentials =
                    beehiveContainer.credentialRepository.getCredentialsByApp(
                        pageUrl
                    ).first()

                inflateAutofillButton()
                inflateAutofillSuggestions(credentials)

                windowManager.addView(overlayView, overlayView.layoutParams)
            }
        }
    }

    override fun onInterrupt() {
        println()
    }

    private fun inflateAutofillButton() {
        val inflater = LayoutInflater.from(this)
        val autofillButton = inflater.inflate(R.layout.autofill_button, overlayView)

        autofillButton.layoutParams = WindowManager.LayoutParams().apply {
            type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            flags = flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            format = PixelFormat.TRANSPARENT
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.END
        }

        autofillButton.findViewById<ImageButton>(R.id.autofill_button).setOnClickListener {
            showSuggestions = !showSuggestions
            overlayView.findViewById<LinearLayout>(R.id.container).visibility =
                if (showSuggestions) View.VISIBLE else View.GONE
        }
    }

    private fun inflateAutofillSuggestions(
        credentials: List<CredentialAndUser>,
    ) {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.autofill_suggestions, overlayView)

        val container = view.findViewById<LinearLayout>(R.id.container)

        credentials.forEach { credentialAndUser ->
            val autofillItemView = createAutofillItemView(
                title = credentialAndUser.user.email,
                subtitle = credentialAndUser.credential.username,
                parent = container,
            ).apply {
                setOnClickListener {
                    val intentSender = createIntentSender(id = credentialAndUser.credential.id)
                    intentSender.sendIntent(applicationContext, 0, null, ::onSendFinished, null)
                }
            }

            container.addView(autofillItemView)
        }

        val choosePasswordView = createAutofillItemView(
            title = "Choose a saved password",
            parent = container
        ).apply {
            setOnClickListener {
                val intentSender = createIntentSender()
                intentSender.sendIntent(applicationContext, 0, null, ::onSendFinished, null)
                windowManager.removeView(view)
            }
        }

        container.addView(choosePasswordView)
    }

    private fun getUrl(info: AccessibilityNodeInfo, addressBarId: String): String? {
        val nodes = info.findAccessibilityNodeInfosByViewId(addressBarId)
        if (nodes == null || nodes.isEmpty())
            return null

        val addressBarNodeInfo = nodes[0]
        val regex = Regex("^(?:https?://)?(?:[^@\\n]+@)?(?:www\\.)?(?:m\\.)?([^:/\\n?]+)")
        val matchResult = regex.find(addressBarNodeInfo.text)
        val hostname = matchResult?.groupValues?.getOrNull(1)

        return hostname
    }

    private fun createIntentSender(
        id: Int? = null,
    ): IntentSender {
        val authIntent = Intent(this, AuthActivity::class.java).apply {
            if (id == null)
                putExtra(EXTRA_IS_CHOOSE, true)
            else {
                putExtra(EXTRA_PASSWORD_ID, id)
                putExtra(EXTRA_FROM_SERVICE, true)
            }

            flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TOP
        }

        val intentSender: IntentSender = PendingIntent.getActivity(
            this,
            id ?: -1,
            authIntent,
            PendingIntent.FLAG_MUTABLE
        ).intentSender

        return intentSender
    }

    private fun createAutofillItemView(
        title: String,
        subtitle: String = "",
        appIcon: Drawable? = null,
        parent: View,
    ): View {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(
            R.layout.autofill_suggestion_item,
            parent as ViewGroup,
            false
        )

        if (appIcon != null) {
            val iconView = view.findViewById<android.widget.ImageView>(R.id.app_icon)
            iconView.setImageDrawable(appIcon)
            iconView.visibility = View.VISIBLE
        }

        view.findViewById<android.widget.TextView>(R.id.title).text = title

        if (subtitle.isNotBlank()) {
            val subtitleText = view.findViewById<android.widget.TextView>(R.id.subtitle)
            subtitleText.text = subtitle
            subtitleText.visibility = View.VISIBLE
        }

        return view
    }

//
//    private fun isViewAutofillable(
//        viewNode: AccessibilityNodeInfo,
//        autofillFields: AutofillFields = AutofillFields(),
//    ): Boolean {
//        autofillFields.usernameNode =
//            autofillFields.usernameNode ?: identifyEmailField(viewNode)
//        autofillFields.passwordNode =
//            autofillFields.passwordNode ?: identifyPasswordField(viewNode)
//
//        if (autofillFields.usernameNode != null || autofillFields.passwordNode != null)
//            return true
//
//        (0 until viewNode.childCount).forEach { i ->
//            val child = viewNode.getChild(i) ?: return@forEach
//            if (isViewAutofillable(child, autofillFields)) {
//                return true
//            }
//        }
//
//        return false
//    }
//
//    private fun identifyEmailField(
//        viewNode: AccessibilityNodeInfo,
//    ): AccessibilityNodeInfo? {
//        val className = viewNode.className ?: return null
//        if (!className.contains("EditText")) return null
//
//        if (viewNode.text?.contains(
//                "email",
//                ignoreCase = true
//            ) == true || viewNode.hintText?.contains(
//                "email",
//                ignoreCase = true
//            ) == true || viewNode.text?.contains(
//                "username",
//                ignoreCase = true
//            ) == true || viewNode.hintText?.contains("username", ignoreCase = true) == true
//        ) return viewNode
//
//        if (viewNode.inputType and (InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) != 0)
//            return viewNode
//
//        return null
//    }
//
//    private fun identifyPasswordField(
//        viewNode: AccessibilityNodeInfo,
//    ): AccessibilityNodeInfo? {
//        val className = viewNode.className ?: return null
//        if (!className.contains("EditText")) return null
//
//        if (viewNode.isPassword)
//            return viewNode
//
//        if (viewNode.text?.contains("password", ignoreCase = true) == true ||
//            viewNode.hintText?.contains("password", ignoreCase = true) == true
//        ) {
//            return viewNode
//        }
//
//        return null
//    }
//
//    private data class AutofillFields(
//        var usernameNode: AccessibilityNodeInfo? = null,
//        var passwordNode: AccessibilityNodeInfo? = null,
//    )

    private fun addressBarId(packageName: String): String {
        return "$packageName:id/url_bar"
    }
}