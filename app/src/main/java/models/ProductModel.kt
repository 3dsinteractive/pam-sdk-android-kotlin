package models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val Id: String? = null,
    val Image: String? = null,
    val Title: String? = null,
    var Price: Double? = null,
    var CategoryId: String? = null
) : Parcelable