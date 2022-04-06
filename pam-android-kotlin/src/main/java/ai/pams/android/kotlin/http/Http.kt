package ai.pams.android.kotlin.http

import ai.pams.android.kotlin.Pam
import android.util.Log
import com.moczul.ok2curl.CurlInterceptor
import com.moczul.ok2curl.logger.Loggable
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

typealias HttpCallBack = (String?, IOException?) -> Unit

class Http {
    companion object {
        private var sharedInstance: Http? = null
        private var httpclient: OkHttpClient? = null

        private fun sharedHttpClient(): OkHttpClient {
            if (httpclient == null) {
                val clientBuilder = OkHttpClient.Builder()

                clientBuilder.addInterceptor(CurlInterceptor(Loggable {
                    //if(Pam.shared.enableLog){
                        Log.d("PAM-HTTP!", it);
                    //}
                }))

                httpclient = clientBuilder.build()
            }
            return httpclient!!
        }

        fun getInstance(): Http {
            if (sharedInstance == null) {
                sharedInstance = Http()
            }
            return sharedInstance!!
        }
    }

    fun post(
        url: String,
        headers: Map<String, String> = mapOf(),
        queryString: Map<String, String> = mapOf(),
        data: Map<String, Any> = mapOf(),
        callBack: HttpCallBack? = null
    ) {
        val urlBuilder = url.toHttpUrlOrNull()?.newBuilder()
        for ((k, v) in queryString) {
            urlBuilder?.addQueryParameter(k, v)
        }

        val requestBuilder = Request.Builder()
            .url(urlBuilder?.build()!!)

        for ((k, v) in headers) {
            requestBuilder.addHeader(k, v)
        }

        val jsonString = JSONObject(data).toString()
        val requestBody = jsonString.toRequestBody()
        requestBuilder.post(requestBody)

        val request = requestBuilder.build()

        sharedHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if(Pam.shared.isLogEnable){
                    Log.d("HTTP!", ">> ${e.localizedMessage}")
                }
                callBack?.invoke(null, e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use { res ->
                    val bodyResult = res.body!!.string()
                    if(Pam.shared.isLogEnable){
                        Log.d("HTTP!", ">> $bodyResult")
                    }
                    callBack?.invoke(bodyResult, null)
                }
            }
        })
    }

    fun get(
        url: String,
        headers: Map<String, String> = mapOf(),
        queryString: Map<String, String> = mapOf(),
        callBack: HttpCallBack? = null
    ) {

        val urlBuilder = url.toHttpUrlOrNull()?.newBuilder()
        for ((k, v) in queryString) {
            urlBuilder?.addQueryParameter(k, v)
        }

        val requestBuilder = Request.Builder()
            .url(urlBuilder?.build()!!)

        for ((k, v) in headers) {
            requestBuilder.addHeader(k, v)
        }

        val request = requestBuilder.build()

        sharedHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if(Pam.shared.isLogEnable){
                    Log.d("PAM-HTTP!", "ERROR >> ${e.localizedMessage}")
                }
                callBack?.invoke(null, e)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use { res ->
                    val bodyResult = res.body!!.string()
                    if(Pam.shared.isLogEnable){
                        Log.d("PAM-HTTP!", "${res.code} >> $bodyResult")
                    }
                    callBack?.invoke(bodyResult, null)
                }
            }
        })
    }
}