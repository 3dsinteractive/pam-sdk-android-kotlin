package ai.pams.android.kotlin.consent

import org.json.JSONObject

data class UserConsentPermissions(
    val consentID:String?,
    val type: ConsentType?,
    val consentMessageId:String?,
    val version: Int?,
    val permissions: List<ConsentPermission>?,
    val needToReview: Boolean?,
    val lastConsentVersion: Int?,
    val contactID: String?,
    val lastConsentAt: String?
)
{
    companion object{
        fun parse(json: JSONObject): UserConsentPermissions{

            val consentID = json.optString("consent_id")

            val type = when( json.optString("consent_message_type") ){
                "tracking_type"-> ConsentType.Tracking
                "contacting_type"-> ConsentType.Contacting
                else-> null
            }

            val consentMessageId = json.optString("consent_message_id")
            val version = json.optInt("version")
            val needToReview = json.optBoolean("need_consent_review")
            val lastConsentVersion = json.optInt("last_consent_version")

            val permissions = parsePermission(json)

            val contactID = json.optString("contact_id")
            val lastConsentAt = json.optString("last_consent_at")

            return UserConsentPermissions(
                consentID = consentID,
                type = type,
                consentMessageId = consentMessageId,
                version = version,
                needToReview = needToReview,
                lastConsentVersion = lastConsentVersion,
                permissions = permissions,
                contactID = contactID,
                lastConsentAt = lastConsentAt
            )
        }

        private fun parsePermission(json:JSONObject?): List<ConsentPermission>{
            val list = mutableListOf<ConsentPermission>()

            json?.optJSONObject("tracking_permission")?.let{ json->

                json.optBoolean("terms_and_conditions")?.let{
                    val perm = ConsentPermission(
                        name = "Terms and Conditions",
                        key="terms_and_conditions",
                        require = true,
                        accept = it,
                        fullDescription = null,
                        shortDescription = null,
                        fullDescriptionEnabled = false
                    )
                    list.add(perm)
                }

                json.optBoolean("privacy_overview")?.let{
                    val perm = ConsentPermission(
                        name = "Privacy overview",
                        key="privacy_overview",
                        require = true,
                        accept = it,
                        fullDescription = null,
                        shortDescription = null,
                        fullDescriptionEnabled = false
                    )
                    list.add(perm)
                }

                json.optBoolean("necessary_cookies")?.let{
                    val perm = ConsentPermission(
                        name = "Necessary cookies",
                        key="necessary_cookies",
                        require = true,
                        accept = it,
                        fullDescription = null,
                        shortDescription = null,
                        fullDescriptionEnabled = false
                    )
                    list.add(perm)
                }

                json.optBoolean("preferences_cookies")?.let{
                    val perm = ConsentPermission(
                        name = "Preferences cookies",
                        key="preferences_cookies",
                        require = false,
                        accept = it,
                        fullDescription = null,
                        shortDescription = null,
                        fullDescriptionEnabled = false
                    )
                    list.add(perm)
                }

                json.optBoolean("analytics_cookies")?.let{
                    val perm = ConsentPermission(
                        name = "Analytics cookies",
                        key="analytics_cookies",
                        require = false,
                        accept = it,
                        fullDescription = null,
                        shortDescription = null,
                        fullDescriptionEnabled = false
                    )
                    list.add(perm)
                }

                json.optBoolean("marketing_cookies")?.let{
                    val perm = ConsentPermission(
                        name = "Marketing cookies",
                        key="marketing_cookies",
                        require = false,
                        accept = it,
                        fullDescription = null,
                        shortDescription = null,
                        fullDescriptionEnabled = false
                    )
                    list.add(perm)
                }

                json.optBoolean("social_media_cookies")?.let{
                    val perm = ConsentPermission(
                        name = "Social media cookies",
                        key="social_media_cookies",
                        require = false,
                        accept = it,
                        fullDescription = null,
                        shortDescription = null,
                        fullDescriptionEnabled = false
                    )
                    list.add(perm)
                }

            }

            json?.optJSONObject("contacting_permission")?.let{ json->

                json.optBoolean("email")?.let{
                    val perm = ConsentPermission(
                        name = "Email",
                        key="email",
                        require = false,
                        accept = it,
                        fullDescription = null,
                        shortDescription = null,
                        fullDescriptionEnabled = false
                    )
                    list.add(perm)
                }

                json.optBoolean("sms")?.let{
                    val perm = ConsentPermission(
                        name = "SMS",
                        key="sms",
                        require = false,
                        accept = it,
                        fullDescription = null,
                        shortDescription = null,
                        fullDescriptionEnabled = false
                    )
                    list.add(perm)
                }

                json.optBoolean("line")?.let{
                    val perm = ConsentPermission(
                        name = "Line",
                        key="line",
                        require = false,
                        accept = it,
                        fullDescription = null,
                        shortDescription = null,
                        fullDescriptionEnabled = false
                    )
                    list.add(perm)
                }

                json.optBoolean("facebook_messenger")?.let{
                    val perm = ConsentPermission(
                        name = "Facebook Messenger",
                        key="facebook_messenger",
                        require = false,
                        accept = it,
                        fullDescription = null,
                        shortDescription = null,
                        fullDescriptionEnabled = false
                    )
                    list.add(perm)
                }

                json.optBoolean("push_notification")?.let{
                    val perm = ConsentPermission(
                        name = "Push notification",
                        key="push_notification",
                        require = false,
                        accept = it,
                        fullDescription = null,
                        shortDescription = null,
                        fullDescriptionEnabled = false
                    )
                    list.add(perm)
                }

            }

            return list
        }

    }
}