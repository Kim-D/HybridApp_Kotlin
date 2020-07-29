package kr.co.kimd.hybridapp.webview

import android.os.Handler
import android.util.Log
import android.webkit.JavascriptInterface
import org.json.JSONObject

//{"action":"open_fullpopup","param":{"title":"","hookingUri":"","loginUri":""},"callback":"setCode"}

interface AndroidBridgeInterface {
    fun openFullPopup(param: JSONObject?, callback: String?)
}

class AndroidBridge {
    companion object {
        private val TAG = AndroidBridge::class.java.simpleName
    }

    var handler: Handler
    var bridgeInterface: AndroidBridgeInterface?

    constructor() {
        this.handler = Handler()
        this.bridgeInterface = null
    }

    fun setAndroidBridgeInterface(bridgeInterface: AndroidBridgeInterface) {
        this.bridgeInterface = bridgeInterface
    }

    @JavascriptInterface
    fun bridge(message: String) {
        Log.i(TAG, message)
        val jsonObject = JSONObject(message)
        val action: String? = jsonObject.getString("action")
        val param: JSONObject? = jsonObject.getJSONObject("param")
        val callback: String? = jsonObject.getString("callback")

        this.handler.post(Runnable {
            if ("open_fullpopup".equals(action)) {
                this.bridgeInterface?.openFullPopup(param, callback)
            }
        })
    }
}