package com.example.beehive.ui.home

import com.example.beehive.data.credential.CredentialAndUser

sealed class GroupingOption {
    data object ByApp : GroupingOption()
    data object ByUser : GroupingOption()

    fun getKey(credential: CredentialAndUser): Any = when (this) {
        ByApp -> credential.credential.app
        ByUser -> credential.user
    }
}
