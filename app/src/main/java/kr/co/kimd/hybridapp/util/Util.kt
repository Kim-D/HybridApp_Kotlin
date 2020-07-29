package kr.co.kimd.hybridapp.util

import android.app.Activity

enum class ActivityCode {
    Main,
    FullPopup
}

class Util {
    companion object {
        private var instance: Util? = null
        private var app: App? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: Util().also { instance = it }
        }

    }

    fun getActivityRequestCode(code: ActivityCode) : Int {
        var ret: Int? = null
        when(code) {
            ActivityCode.Main -> ret = 1000
            ActivityCode.FullPopup -> ret = 1001
            else -> ret = -1
        }
        return ret
    }

    fun setApplication(application: App) {
        app = application
    }

    fun getActiveActivity(): Activity? {
        return app?.getActiveActivity()
    }
}