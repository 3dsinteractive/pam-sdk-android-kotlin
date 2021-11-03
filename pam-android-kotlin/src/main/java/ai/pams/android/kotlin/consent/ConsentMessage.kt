package ai.pams.android.kotlin.consent

import android.graphics.Color
import androidx.annotation.ColorInt
import org.json.JSONObject

data class ConsentDialogStyleConfiguration(
    val icon: String,
    @ColorInt val primaryColor: Int,
    @ColorInt val secondaryColor: Int,
    @ColorInt val buttonTextColor: Int,
    @ColorInt val textColor: Int
)

data class StyleConfiguration(
    @ColorInt val backgroundColor: Int,
    @ColorInt val textColor: Int,
    val barBackgroundOpacity: Double,
    @ColorInt val buttonBackgroundColor: Int,
    @ColorInt val buttonTextColor: Int,
    val dialogStyle: ConsentDialogStyleConfiguration?
) {
    companion object{
        fun parse(json: JSONObject?): StyleConfiguration?{
            if(json == null) return null
            val backgroundColor = json.optString("bar_background_color", "#000000")
            val textColor = json.optString("bar_text_color", "#000000")
            val barBackgroundOpacity = json.optDouble("bar_background_opacity_percentage", 100.0)/100.0
            val buttonBackgroundColor = json.optString("bar_text_color", "#FFFFFF")
            val buttonTextColor = json.optString("button_text_color", "#000000")

            var dialogStyle: ConsentDialogStyleConfiguration? = null
            json.optJSONObject("consent_detail")?.let{

                val icon = it.optString("popup_main_icon")
                val primaryColor = it.optString("primary_color", "#FFFFFF")
                val secondaryColor = it.optString("secondary_color", "#5C5C5C")
                val buttonTextColor = it.optString("button_text_color", "#000000")
                val textColor = it.optString("text_color", "#000000")

                dialogStyle = ConsentDialogStyleConfiguration(
                    icon = icon,
                    primaryColor = Color.parseColor(primaryColor),
                    secondaryColor  = Color.parseColor(secondaryColor),
                    buttonTextColor = Color.parseColor(buttonTextColor),
                    textColor = Color.parseColor(textColor),
                )
            }

            return StyleConfiguration(
                backgroundColor = Color.parseColor(backgroundColor),
                textColor = Color.parseColor(textColor),
                barBackgroundOpacity = barBackgroundOpacity,
                buttonBackgroundColor = Color.parseColor(buttonBackgroundColor),
                buttonTextColor = Color.parseColor(buttonTextColor),
                dialogStyle = dialogStyle
            )
        }
    }
}

enum class LocaleText{
    En,
    Th
}
data class Text(val en:String?, val th: String?){
    fun get(prefer: LocaleText):String{
        when(prefer){
            LocaleText.En->{
                if(en != null){ getSome@return en }
                else if(th != null){getSome@return th}
            }
            LocaleText.Th->{
                if(th != null){ getSome@return th }
                else if(en != null){getSome@return en}
            }
        }
        return ""
    }
}

sealed class BaseConsentMessage
enum class ConsentType{
    Tracking,
    Contacting
}

data class ValidationResult(val isValid: Boolean, val errorMessage: String?, val errorField: List<String>?)

data class ConsentMessage(
    val id: String,
    val type: ConsentType,
    val name:String,
    val description: String,
    val style: StyleConfiguration?,
    val version: Int,
    val revision: Int,
    val displayText: Text?,
    val acceptButtonText: Text?,
    val consentDetailTitle: Text?,
    val availableLanguages: List<String>,
    val defaultLanguage: String,
    val permission: List<ConsentPermission>
): BaseConsentMessage(){

    companion object{

        private fun getText(json:JSONObject?): Text?{
            if(json == null ) return null
            return Text(
                en = json.optString("en"),
                th = json.optString("th"),
            )
        }

        fun parse(json: JSONObject): ConsentMessage {
            val id = json.optString("consent_message_id")
            val name = json.optString("name")
            val description = json.optString("description")
            val style = StyleConfiguration.parse(json.optJSONObject("style_configuration"))
            val setting = json.optJSONObject("setting") ?: JSONObject()
            val version = setting.optInt("version")
            val revision = setting.optInt("version")
            val displayText = getText(setting.optJSONObject("display_text"))
            val acceptButtonText = getText(setting.optJSONObject("accept_button_text"))
            val consentDetailTitle = getText(setting.optJSONObject("consent_detail_title"))

            val availableLanguages = mutableListOf<String>()
            setting.optJSONArray("available_languages")?.let {
                for (i in 0 until it.length()) {
                    availableLanguages.add(it.getString(i))
                }
            }

            val defaultLanguage = setting.optString("default_language")
            val permissions = ConsentPermission.parse(setting)

            return ConsentMessage(
                id = id,
                type = ConsentType.Tracking,
                name = name,
                description = description,
                style = style,
                version = version,
                revision = revision,
                displayText = displayText,
                acceptButtonText = acceptButtonText,
                consentDetailTitle = consentDetailTitle,
                availableLanguages = availableLanguages.toList(),
                defaultLanguage = defaultLanguage,
                permission = permissions
            )
        }
    }

    fun allowAll(){
        for(it in permission){
            it.allow = true
        }
    }

    fun denyAll(){
        for(it in permission){
            it.allow = false
        }
    }

    fun setPermission(name: ConsentPermissionName, isAllow: Boolean) {
        for(item in this.permission){
            if( item.name.key == name.key ){
                item.allow = false
            }
        }
    }

    fun validate(): ValidationResult{
        var pass = true
        var errorField: MutableList<String>? = null
        var errorMessage:String? = null
        for(p in permission){
            if(p.require && !p.allow){
                pass = false
                if(errorField == null){
                    errorField = mutableListOf()
                }
                errorField.add(p.name.nameStr)
            }
        }
        if(!pass){
            val fields = errorField?.reduce{ acc, str->
                "$acc , $str"
            }
            errorMessage = "You must accept the required permissions ($fields)"
        }
        return ValidationResult(isValid=pass, errorMessage=errorMessage, errorField=errorField)
    }

}

data class ConsentMessageError(val errorMessage: String, val errorCode: String): BaseConsentMessage()

data class ConsentPermission(
    val name: ConsentPermissionName,
    val shortDescription: Text?,
    val fullDescription: Text?,
    val fullDescriptionEnabled: Boolean,
    val require: Boolean,
    var allow: Boolean
){
    companion object {
        private fun getText(json:JSONObject?): Text?{
            if(json == null ) return null
            return Text(
                en = json.optString("en"),
                th = json.optString("th"),
            )
        }

        private fun parsePermission(json:JSONObject?, name: ConsentPermissionName, require: Boolean ): ConsentPermission?{
            json?.optJSONObject(name.key)?.let{ item->
                return ConsentPermission(
                    name = name,
                    shortDescription = getText(item.optJSONObject("brief_description")),
                    fullDescription = getText(item.optJSONObject("full_description")),
                    require = require,
                    fullDescriptionEnabled = item.optBoolean("is_full_description_enabled"),
                    allow = true
                )
            }
            return null
        }

        fun parse(json: JSONObject?): List<ConsentPermission>{
            val list = mutableListOf<ConsentPermission>()

            parsePermission(
                json,
                ConsentPermissionName.TermsAndConditions,
                true)?.let { perm ->
                list.add(perm)
            }

            parsePermission(
                json,
                ConsentPermissionName.PrivacyOverview,
                true)?.let { perm ->
                list.add(perm)
            }

            parsePermission(
                json,
                ConsentPermissionName.NecessaryCookies,
                true)?.let { perm ->
                list.add(perm)
            }

            parsePermission(
                json,
                ConsentPermissionName.PreferencesCookies,
                false)?.let { perm ->
                list.add(perm)
            }

            parsePermission(
                json,
                ConsentPermissionName.AnalyticsCookies,
                false)?.let { perm ->
                list.add(perm)
            }

            parsePermission(
                json,
                ConsentPermissionName.MarketingCookies,
                false)?.let { perm ->
                list.add(perm)
            }

            parsePermission(
                json,
                ConsentPermissionName.SocialMediaCookies,
                false)?.let { perm ->
                list.add(perm)
            }

            parsePermission(
                json,
                ConsentPermissionName.Email,
                false)?.let { perm ->
                list.add(perm)
            }

            parsePermission(
                json,
                ConsentPermissionName.SMS,
                false)?.let { perm ->
                list.add(perm)
            }

            parsePermission(
                json,
                ConsentPermissionName.Line,
                false)?.let { perm ->
                list.add(perm)
            }

            parsePermission(
                json,
                ConsentPermissionName.FacebookMessenger,
                false)?.let { perm ->
                list.add(perm)
            }

            parsePermission(
                json,
                ConsentPermissionName.PushNotification,
                false)?.let { perm ->
                list.add(perm)
            }

            return list
        }
    }

    fun getSubmitKey() = "_allow_${name}"
}

