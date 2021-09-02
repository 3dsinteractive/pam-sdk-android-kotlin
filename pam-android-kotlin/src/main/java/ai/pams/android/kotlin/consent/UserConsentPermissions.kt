package ai.pams.android.kotlin.consent

import org.json.JSONObject

data class UserConsentPermissions(
    val consentID: String?,
    val type: ConsentType?,
    val consentMessageId: String?,
    val version: Int?,
    val permissions: List<ConsentPermission>?,
    val needToReview: Boolean?,
    val lastConsentVersion: Int?,
    val contactID: String?,
    val lastConsentAt: String?
) {
    companion object {
        fun parse(json: JSONObject): UserConsentPermissions {

            val consentID = json.optString("consent_id")

            val type = when (json.optString("consent_message_type")) {
                "tracking_type" -> ConsentType.Tracking
                "contacting_type" -> ConsentType.Contacting
                else -> null
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
                lastConsentAt = lastConsentAt,
            )
        }

        private fun parsePermission(json: JSONObject?): List<ConsentPermission> {
            val list = mutableListOf<ConsentPermission>()

            json?.optJSONObject("tracking_permission")?.let { json ->

                json.optBoolean(ConsentPermissionName.TermsAndConditions.key)?.let {
                    val perm = ConsentPermission(
                        name = ConsentPermissionName.TermsAndConditions,
                        require = true,
                        allow = it,
                        fullDescription = null,
                        shortDescription = null,
                        fullDescriptionEnabled = false
                    )
                    list.add(perm)
                }

                json.optBoolean(ConsentPermissionName.PrivacyOverview.key)?.let {
                    val perm = ConsentPermission(
                        name = ConsentPermissionName.PrivacyOverview,
                        require = true,
                        allow = it,
                        fullDescription = null,
                        shortDescription = null,
                        fullDescriptionEnabled = false
                    )
                    list.add(perm)
                }

                json.optBoolean(ConsentPermissionName.NecessaryCookies.key)?.let {
                    val perm = ConsentPermission(
                        name = ConsentPermissionName.NecessaryCookies,
                        require = true,
                        allow = it,
                        fullDescription = null,
                        shortDescription = null,
                        fullDescriptionEnabled = false
                    )
                    list.add(perm)
                }

                json.optBoolean(ConsentPermissionName.PreferencesCookies.key)?.let {
                    val perm = ConsentPermission(
                        name = ConsentPermissionName.PreferencesCookies,
                        require = false,
                        allow = it,
                        fullDescription = null,
                        shortDescription = null,
                        fullDescriptionEnabled = false
                    )
                    list.add(perm)
                }

                json.optBoolean(ConsentPermissionName.AnalyticsCookies.key)?.let {
                    val perm = ConsentPermission(
                        name = ConsentPermissionName.AnalyticsCookies,
                        require = false,
                        allow = it,
                        fullDescription = null,
                        shortDescription = null,
                        fullDescriptionEnabled = false
                    )
                    list.add(perm)
                }

                json.optBoolean(ConsentPermissionName.MarketingCookies.key)?.let {
                    val perm = ConsentPermission(
                        name = ConsentPermissionName.MarketingCookies,
                        require = false,
                        allow = it,
                        fullDescription = null,
                        shortDescription = null,
                        fullDescriptionEnabled = false
                    )
                    list.add(perm)
                }

                json.optBoolean(ConsentPermissionName.SocialMediaCookies.key)?.let {
                    val perm = ConsentPermission(
                        name = ConsentPermissionName.SocialMediaCookies,
                        require = false,
                        allow = it,
                        fullDescription = null,
                        shortDescription = null,
                        fullDescriptionEnabled = false
                    )
                    list.add(perm)
                }

            }

            json?.optJSONObject("contacting_permission")?.let { json ->

                json.optBoolean(ConsentPermissionName.Email.key)?.let {
                    val perm = ConsentPermission(
                        name = ConsentPermissionName.Email,
                        require = false,
                        allow = it,
                        fullDescription = null,
                        shortDescription = null,
                        fullDescriptionEnabled = false
                    )
                    list.add(perm)
                }

                json.optBoolean(ConsentPermissionName.SMS.key)?.let {
                    val perm = ConsentPermission(
                        name = ConsentPermissionName.SMS,
                        require = false,
                        allow = it,
                        fullDescription = null,
                        shortDescription = null,
                        fullDescriptionEnabled = false
                    )
                    list.add(perm)
                }

                json.optBoolean(ConsentPermissionName.Line.key)?.let {
                    val perm = ConsentPermission(
                        name = ConsentPermissionName.Line,
                        require = false,
                        allow = it,
                        fullDescription = null,
                        shortDescription = null,
                        fullDescriptionEnabled = false
                    )
                    list.add(perm)
                }

                json.optBoolean(ConsentPermissionName.FacebookMessenger.key)?.let {
                    val perm = ConsentPermission(
                        name = ConsentPermissionName.FacebookMessenger,
                        require = false,
                        allow = it,
                        fullDescription = null,
                        shortDescription = null,
                        fullDescriptionEnabled = false
                    )
                    list.add(perm)
                }

                json.optBoolean(ConsentPermissionName.PushNotification.key)?.let {
                    val perm = ConsentPermission(
                        name = ConsentPermissionName.PushNotification,
                        require = false,
                        allow = it,
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