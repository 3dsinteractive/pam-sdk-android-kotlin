package webservices

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


data class LoginResponse(val data: LoginCustomer)
data class LoginCustomer(
    @SerializedName("customer_id") val customerID:String
)

data class RegisterResponse(val data: RegisterCustomer)
data class RegisterCustomer(
    @SerializedName("customer_id") val customerID:String,
    @SerializedName("email") val email:String,
)

class DemoAPI {
    companion object {
        private const val apiServer = "https://demo-server-a.pams.ai"
        val http = OkHttpClient.Builder().build()

        fun login(
            email: String,
            password: String,
            callBack: ((LoginResponse) -> Unit)? = null
        ) {
            val url = "$apiServer/login"
            val urlBuilder = url.toHttpUrlOrNull()?.newBuilder()

            val requestBuilder = Request.Builder()
                .url(urlBuilder?.build()!!)

            val data = mapOf(
                "email" to email,
                "password" to password
            )

            val jsonString = JSONObject(data).toString()
            val requestBody = jsonString.toRequestBody()
            requestBuilder.post(requestBody)

            val request = requestBuilder.build()

            http.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                }

                override fun onResponse(call: Call, response: Response) {
                    response.use { res ->
                        val bodyResult = res.body!!.string()
                        val model = Gson().fromJson(bodyResult, LoginResponse::class.java)
                        callBack?.invoke(model)
                    }
                }
            })
        }

        fun register(
            email: String,
            mobile: String,
            password: String,
            consentId: String,
            callBack: ((RegisterResponse) -> Unit)? = null
        ) {
            val url = "$apiServer/register"
            val urlBuilder = url.toHttpUrlOrNull()?.newBuilder()

            val requestBuilder = Request.Builder()
                .url(urlBuilder?.build()!!)

            val data = mapOf(
                "email" to email,
                "password" to password,
                "mobile" to mobile,
                "consent_ids" to consentId
            )

            val jsonString = JSONObject(data).toString()
            val requestBody = jsonString.toRequestBody()
            requestBuilder.post(requestBody)

            val request = requestBuilder.build()

            http.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                }

                override fun onResponse(call: Call, response: Response) {
                    response.use { res ->
                        val bodyResult = res.body!!.string()
                        val model = Gson().fromJson(bodyResult, RegisterResponse::class.java)
                        callBack?.invoke(model)
                    }
                }
            })
        }

    }
}