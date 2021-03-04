package pams.ai.demo

import android.app.Application
import android.util.Log
import pamsdk.PamSDK
import pamsdk.PamSDKName

class PamDemoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        PamSDK.init(application = this)

        PamSDK.listen("onToken") { args ->
            Log.d(PamSDKName, args.toString())
        }
    }
}