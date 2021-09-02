package ai.pams.android.kotlin.consent

enum class ConsentPermissionName(val key: String, val nameStr: String) {
     TermsAndConditions("terms_and_conditions", "Terms and Conditions"),
     PrivacyOverview("privacy_overview", "Privacy overview"),
     NecessaryCookies("necessary_cookies", "Necessary cookies"),
     PreferencesCookies("preferences_cookies", "Preferences cookies"),
     AnalyticsCookies("analytics_cookies", "Analytics cookies"),
     MarketingCookies("marketing_cookies", "Marketing cookies"),
     SocialMediaCookies("social_media_cookies", "Social media cookies"),
     Email("email", "Email"),
     SMS("sms", "SMS"),
     Line("line", "LINE"),
     FacebookMessenger("facebook_messenger", "Facebook Messenger"),
     PushNotification("push_notification", "Push Notification"),
}
