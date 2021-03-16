package models

import android.content.Context
import com.google.gson.Gson
import pams.ai.demo.MainApplication


class AppData {
    companion object{
        private var user: UserModel? = null

        fun getUser(): UserModel? {
            if(user != null){
                return user
            }
            val sharePref = MainApplication.appContext?.getSharedPreferences(
                "app_pref",
                Context.MODE_PRIVATE
            )!!
            val userJson = sharePref.getString("login-user", null)
            return Gson().fromJson(userJson, UserModel::class.java)
        }

        fun setUser(user: UserModel?){
            val sharePref = MainApplication.appContext?.getSharedPreferences(
                "app_pref",
                Context.MODE_PRIVATE
            )!!
            val jsonString = Gson().toJson(user)
            sharePref.edit().putString("login-user", jsonString).apply()
        }
    }
}