package kr.co.kimd.hybridapp.webview

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.os.Build
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.webkit.*
import kr.co.kimd.hybridapp.activities.FullPopupActivity
import kr.co.kimd.hybridapp.util.ActivityCode
import kr.co.kimd.hybridapp.util.Util
import org.json.JSONObject

enum class WebViewAction {
    PageStarted,
    PageFinished,
    ShouldOverrideUrlLoading
}

typealias WebViewActionHandler = (WebViewAction, Any?) -> Unit

class CustomWebView : WebView, AndroidBridgeInterface {
    companion object {
        private val TAG = CustomWebView::class.java.simpleName
        private var actionHandler: WebViewActionHandler? = null
        private var mContext: Context? = null
        private var javascriptCallback: String? = null
    }

    private var androidBridge: AndroidBridge? = null


    constructor(context: Context) : super(context) {
        mContext = context
        initWebView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mContext = context
        initWebView()
    }

    @SuppressLint("JavascriptInterface")
    fun initWebView() {
        androidBridge = AndroidBridge()
        androidBridge?.setAndroidBridgeInterface(this)

        val settings = this.settings
        //enable java script in web view
        settings.javaScriptEnabled = true

        //필요한 settings 찾아 보자
        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            if (0 != (mContext?.applicationInfo!!.flags.and(ApplicationInfo.FLAG_DEBUGGABLE))) {
                setWebContentsDebuggingEnabled(true)
            }
        }

        settings.textZoom = 100
        settings.setAppCacheEnabled(true)
        settings.cacheMode = WebSettings.LOAD_NO_CACHE

        settings.domStorageEnabled = true
        //settings.setGeolocationEnabled(true)

        this.webViewClient = CustomWebViewClient()
        this.webChromeClient = CustomWebChormeClient()

        this.addJavascriptInterface(androidBridge, "android")
    }

    fun setWebViewAction(webViewActionHandler: WebViewActionHandler?) {
        if (webViewActionHandler != null) actionHandler = webViewActionHandler
    }

    fun executeJavaScript(script: String) {
        Log.i(TAG, "====== executeJavaScript -" + script)
        this.loadUrl("javascript:" + script)
    }

    class CustomWebViewClient : WebViewClient {
        constructor() : super()

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            actionHandler?.invoke(WebViewAction.PageStarted, null)
            super.onPageStarted(view, url, favicon)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            actionHandler?.invoke(WebViewAction.PageFinished, url)
            super.onPageFinished(view, url)
        }

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            actionHandler?.invoke(WebViewAction.ShouldOverrideUrlLoading, request?.url.toString())
            return super.shouldOverrideUrlLoading(view, request)
        }
    }

    class CustomWebChormeClient() : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
        }

        override fun onCreateWindow(
            view: WebView?,
            isDialog: Boolean,
            isUserGesture: Boolean,
            resultMsg: Message?
        ): Boolean {
            return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
        }
    }

    override fun openFullPopup(param: JSONObject?, callback: String?) {
        val topActivity = Util.getInstance().getActiveActivity()
        Log.i(TAG, "====== top activity - " + topActivity)
        val intent = Intent(topActivity, FullPopupActivity::class.java)
        intent.putExtra("title", param?.getString("title"))
        intent.putExtra("loadUrl", param?.getString("loginUri"))
        intent.putExtra("hookingUrl", param?.getString("hookingUri"))
        javascriptCallback = callback
        topActivity?.startActivityForResult(intent, Util.getInstance().getActivityRequestCode(ActivityCode.FullPopup))
    }

    fun closeFullPopup(code: String?) {
        Log.i(TAG, "====== get code -" + code)
        if (javascriptCallback != null && !"".equals(javascriptCallback)) {
            executeJavaScript(javascriptCallback + "(\"" + code + "\")")
            javascriptCallback = null
        }
    }
}