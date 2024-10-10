package com.example.beehive.data.user

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.example.beehive.data.Searchable
import com.example.beehive.data.credential.Credential

@Entity(tableName = "user")
data class User(
    @PrimaryKey val id: Int,
    val email: String,
) : Searchable {
    @get:Ignore
    override val searchText: String
        get() = email
}

data class UserWithCredentials(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val credentials: List<Credential>,
)