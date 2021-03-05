package models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notification(
    val Image: String? = null,
    val Title: String? = null,
    val Message: String? = null,
    val Date: String? = null
): Parcelable
