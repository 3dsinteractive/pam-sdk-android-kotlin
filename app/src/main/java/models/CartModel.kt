package models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartModel(
    var Products: MutableList<CartProductModel>? = null,
    var TotalPrice: Double? = null
) : Parcelable

@Parcelize
data class CartProductModel(
    val Id: String? = null,
    val CategoryId: String? = null,
    val Image: String? = null,
    val Title: String? = null,
    var Quantity: Int? = null,
    val UnitPrice: Double? = null,
    var TotalPrice: Double? = null
) : Parcelable