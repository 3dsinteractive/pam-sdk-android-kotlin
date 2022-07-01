package ai.pams.android.kotlin.models.notification


import ai.pams.android.kotlin.flex.parser.FlexParser
import ai.pams.android.kotlin.flex.parser.PImage
import ai.pams.android.kotlin.http.Http
import android.content.Context
import com.google.gson.annotations.SerializedName
import org.threeten.bp.LocalDateTime
import java.util.*

data class NotificationItem(
    var date: LocalDateTime? = null,
    val deliverId: String? = null,
    val description: String? = null,
    val flex: String? = null,
    val isOpen: Boolean? = null,
    val payload: Map<String, Any>? = null,
    val pixel: String? = null,
    val thumbnailUrl: String? = null,
    val title: String? = null,
    val url: String? = null,
    val popupType: String? = null,
) {
    var bannerUrl: String? = null

    @Deprecated("createdDate is deprecated use date instead.", ReplaceWith("date"))
    val createdDate: LocalDateTime?
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

    fun trackOpen() {
        pixel?.let {
            Http.getInstance().get(it)
        }
    }
}