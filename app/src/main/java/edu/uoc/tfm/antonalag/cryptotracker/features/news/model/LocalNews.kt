package edu.uoc.tfm.antonalag.cryptotracker.features.news.model

import androidx.annotation.NonNull
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import edu.uoc.tfm.antonalag.cryptotracker.features.user.model.User
import java.util.*

@Entity(tableName = "SavedNews")
data class LocalNews(
    @PrimaryKey(autoGenerate = true) @NonNull val id: Long,
    val title: String,
    val url: String,
    val date: Date,
    val userId: Long
) {
    constructor(title: String, url: String, date: Date, userId: Long): this(0L, title, url, date, userId)
}
