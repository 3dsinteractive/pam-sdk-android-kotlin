package pamsdk

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

const val PamSDKName: String = "PamSDK"
const val sharedPreferenceKey: String = "PamSDK"

data class IPamResponse(
    @SerializedName("code") val code: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("contact_id") val contactID: String? = null
)

data class IPamOption(val pamServer: String, val publicDbAlias: String, val loginDbAlias: String)

typealias ListenerFunction = (Map<String, String>) -> Unit

enum class PamEvent {
    page_view,
    add_to_cart,
    purchase_success,
    favourite,
    save_push,
}

enum class PamCallback {
    onToken,
}

class PamSDK {
    companion object {
        var app: Application? = null
        var options: IPamOption? = null

        var onTokenListener = mutableListOf<ListenerFunction>()


        fun listen(eventName: String, callBack: ListenerFunction) {
            if (eventName == PamCallback.onToken.toString()) {
                onTokenListener.add(callBack)
            }
        }

        fun dispatch(eventName: String, args: Map<String, String>) {
            if (eventName == PamCallback.onToken.toString()) {
                onTokenListener.forEach { callBack ->
                    callBack(args)
                }
            }
        }

        fun askNotificationPermission() {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }

                track(
                    "save_push", mutableMapOf(
                        "android_notification" to task.result.toString()
                    )
                )
                saveToSharedPref("push_key", task.result.toString())
                dispatch(
                    PamCallback.onToken.toString(), mutableMapOf(
                        "token" to task.result.toString()
                    )
                )
            })
        }


        fun init(application: Application) {
            this.app = application
            val config = this.app!!.packageManager.getApplicationInfo(
                this.app!!.packageName,
                PackageManager.GET_META_DATA
            )
            this.options = IPamOption(
                pamServer = config.metaData.get("pam-server").toString(),
                publicDbAlias = config.metaData.get("public-db-alias").toString(),
                loginDbAlias = config.metaData.get("login-db-alias").toString()
            )

            Log.d(PamSDKName, "Pam has initial\n")
        }

        private fun saveToSharedPref(key: String, value: Any): Boolean? {
            val sharedPref =
                app?.getSharedPreferences(sharedPreferenceKey, Context.MODE_PRIVATE)
            val editor = sharedPref?.edit()
            editor?.putString(key, value.toString())
            return editor?.commit()
        }

        private fun removeFromSharedPref(key: String): Boolean? {
            val sharedPref =
                app?.getSharedPreferences(sharedPreferenceKey, Context.MODE_PRIVATE)
            val editor = sharedPref?.edit()
            editor?.remove(key)
            return editor?.commit()
        }

        private fun getFromSharedPref(key: String, default: String): String {
            val sharedPref = app?.getSharedPreferences(sharedPreferenceKey, Context.MODE_PRIVATE)
            return sharedPref?.getString(key, default)!!
        }

        private fun replaceFormFieldsValueIfNull(
            obj: MutableMap<String, Any>,
            key: String,
            value: Any
        ) {
            if (value.toString() != "") {
                obj["form_fields"].let {
                    val newObj: MutableMap<String, String>
                    if (it == null) {
                        newObj = mutableMapOf()
                    } else {
                        newObj = it as MutableMap<String, String>
                    }

                    if (newObj[key] == null) {
                        newObj[key] = value.toString()
                    }

                    obj["form_fields"] = newObj
                }
            }
        }

        fun userLogin(customerID: String) {
            this.saveToSharedPref("customer_id", customerID)
            this.saveToSharedPref("_database", this.options!!.loginDbAlias)
            this.track(
                "login", mutableMapOf(
                    "form_fields" to mutableMapOf(
                        "customer" to customerID
                    )
                )
            )
        }

        fun userLogout() {
            this.track("logout", mutableMapOf())
            this.removeFromSharedPref("_contact_id")
            this.removeFromSharedPref("customer_id")
            this.removeFromSharedPref("_database")
        }

        fun savePushKey(pushKey: String) {
            this.track(
                PamEvent.save_push.toString(), mutableMapOf(
                    "form_fields" to mutableMapOf(
                        "android_notification" to pushKey
                    )
                )
            )
        }

        fun track(eventName: String, payload: MutableMap<String, Any>) {
            payload["platform"] = "android"
            payload["event"] = eventName
            replaceFormFieldsValueIfNull(
                payload,
                "_database",
                getFromSharedPref("_database", this.options!!.publicDbAlias)
            )
            replaceFormFieldsValueIfNull(
                payload,
                "_contact_id",
                this.getFromSharedPref("_contact_id", "")
            )
            replaceFormFieldsValueIfNull(
                payload,
                "customer",
                this.getFromSharedPref("customer_id", "")
            )

            Log.d(PamSDKName, "track payload $payload\n")

            Http.getInstance().post(
                url = "${this.options!!.pamServer}/trackers/events",
                headers = mapOf(),
                queryString = mapOf(),
                data = payload
            ) { text, err ->
                val response = Gson().fromJson(text, IPamResponse::class.java)
                if (response.contactID != null) {
                    Log.d(PamSDKName, "track response is $text\n")
                    saveToSharedPref("_contact_id", response.contactID).let {
                        if (!it!!) {
                            Log.d(PamSDKName, "track error $err\n")
                        }
                    }
                } else if (response.code != null && response.message != null) {
                    Log.d(PamSDKName, "track error $err\n")
                }
            }

            Log.d(PamSDKName, "track has been send\n")
        }
    }
}