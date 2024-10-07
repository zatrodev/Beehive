package com.example.beehive.data.credential

import android.graphics.drawable.Drawable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.beehive.data.user.User
import java.util.Date

@Entity(tableName = "credential")
data class Credential(
    @PrimaryKey val id: Int,
    val username: String = "",
    val password: String,
    val userId: Int,
    val deletionDate: Date? = null,
    @Embedded val app: PasswordApp,
)

data class PasswordApp(
    val name: String,
    val packageName: String,
) {
    @Ignore
    var icon: Drawable? = null
}

data class CredentialAndUser(
    @Embedded val credential: Credential,
    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )

    val user: User,
)