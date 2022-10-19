package ai.pams.android.kotlin.models.notification


import ai.pams.android.kotlin.flex.parser.FlexParser
import ai.pams.android.kotlin.flex.parser.PImage
import ai.pams.android.kotlin.http.Http
import ai.pams.android.kotlin.utils.DateUtils
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class NotificationItem(
    var date: Date? = null,
    val deliverId: String? = null,
    val description: String? = null,
    val flex: String? = null,
    val isOpen: Boolean? = null,
    val payload: @RawValue Map<String, Any>? = null,
    val pixel: String? = null,
    val thumbnailUrl: String? = null,
    val title: String? = null,
    val url: String? = null,
    val popupType: String? = null,
): Parcelable {
    var bannerUrl: String? = null

    @Deprecated("createdDate is deprecated use date instead.", ReplaceWith("date"))
    val createdDate: Date?
        get() = date

    fun parseFlex(context: Context) {
        flex?.let {
            val parser = FlexParser(context)
            val flexView = parser.parse(flex)
            (flexView?.childs?.get(0) as? PImage)?.let {
                bannerUrl = it.props["src"]
            }
        }
    }

    @Deprecated("use read() instead of trackOpen()")
    fun trackOpen() {
        read()
    }

    fun read() {
        pixel?.let {
            Http.getInstance().get(it)
        }
    }

}