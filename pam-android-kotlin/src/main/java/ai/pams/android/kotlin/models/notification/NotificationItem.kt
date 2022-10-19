package ai.pams.android.kotlin.models.notification


import ai.pams.android.kotlin.flex.parser.FlexParser
import ai.pams.android.kotlin.flex.parser.PImage
import ai.pams.android.kotlin.http.Http
import ai.pams.android.kotlin.utils.DateUtils
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject
import org.threeten.bp.LocalDateTime



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
): Parcelable {
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

    @Deprecated("use read() instead of trackOpen()")
    fun trackOpen() {
        read()
    }

    fun read() {
        pixel?.let {
            Http.getInstance().get(it)
        }
    }


    //PARCEL IMPLEMENTATION
    constructor(parcel: Parcel) : this(
        //date
        convertStringToDate(parcel.readString() ),
        //deliverId
        parcel.readString(),
        //description
        parcel.readString(),
        //flex
        parcel.readString(),
        //isOpen
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        //payload
        jsonToMap( parcel.readString()),
        //pixel
        parcel.readString(),
        //thumbnailUrl
        parcel.readString(),
        //title
        parcel.readString(),
        //url
        parcel.readString(),
        //popupType
        parcel.readString()
    ) {
        bannerUrl = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        //date
        if( date != null ){
            var dateStr = ""
            date?.let{
                dateStr = DateUtils.localDateToServerFormat(it)
            }
            parcel.writeString(dateStr)
        }else{
            parcel.writeString("-")
        }

        parcel.writeString(deliverId)
        parcel.writeString(description)
        parcel.writeString(flex)
        parcel.writeValue(isOpen)
        parcel.writeValue(payload)
        parcel.writeString(pixel)
        parcel.writeString(thumbnailUrl)
        parcel.writeString(title)
        parcel.writeString(url)
        parcel.writeString(popupType)
        parcel.writeString(bannerUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NotificationItem> {
        override fun createFromParcel(parcel: Parcel): NotificationItem {
            return NotificationItem(parcel)
        }

        override fun newArray(size: Int): Array<NotificationItem?> {
            return arrayOfNulls(size)
        }

        fun jsonToMap(jsonStr: String?): Map<String, Any>? {
            if(jsonStr == null){
                return null
            }
            val json = JSONObject(jsonStr)
            val m = mutableMapOf<String, Any>()
            json.keys().forEach {
                if(json.get(it) is Int ){
                    m[it] = json.optInt(it)
                }else if(json.get(it) is String){
                    m[it] = json.optString(it)
                }else if(json.get(it) is Boolean){
                    m[it] = json.optBoolean(it)
                }else if(json.get(it) is Double){
                    m[it] = json.optDouble(it)
                }else{
                    m[it] = json.get(it)
                }
            }
            return m.toMap()
        }
        fun convertStringToDate(str: String?): LocalDateTime?{
            str?.let{
                return@convertStringToDate DateUtils.localDateTimeFromString(str)
            }
            return null
        }
    }
}