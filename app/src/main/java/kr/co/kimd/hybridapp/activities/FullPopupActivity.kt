package kr.co.kimd.hybridapp.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kr.co.kimd.hybridapp.R
import kr.co.kimd.hybridapp.webview.CustomWebView
import kr.co.kimd.hybridapp.webview.WebViewAction

interface FullPopupInterface {
    fun closeFullPopup(code: String?)
}

class FullPopupActivity: AppCompatActivity() {
    companion object {
        private val TAG = FullPopupActivity::class.java.simpleName
    }

    private var webView: CustomWebView? = null
    private var fullPopupInterface: FullPopupInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = getIntent()
        this.supportActionBar?.title = intent.getStringExtra("title")
        setContentView(R.layout.activity_fullpopup)

        initWebView(intent.getStringExtra("loadUrl"), intent.getStringExtra("hookingUrl"))
    }

    fun setFullPopupDelegate(fullPopupInterface: FullPopupInterface) {
        this.fullPopupInterface = fullPopupInterface
    }

    fun initWebView(loadUrl: String, hookingUrl: String) {
        webView = findViewById(R.id.fullPopupWebView)

        webView?.setWebViewAction { webViewAction, result ->
            when(webViewAction) {
                WebViewAction.PageStarted -> Log.i(TAG,"==== call PageStarted !!!!")
                WebViewAction.PageFinished -> {
                    Log.i(TAG, "==== call PageFinished !!!!" + result)
                    val url = result as String
                    if (!url.equals(loadUrl) && url.contains(hookingUrl)) {
                        getOAuthCode(url, hookingUrl)
                    }
                }
                WebViewAction.ShouldOverrideUrlLoading -> {
                    Log.i(TAG, "==== call ShouldOverrideUrlLoading !!! - " + result)
                    val url = result as String
                    if (url.contains(hookingUrl)) {
                        getOAuthCode(url, hookingUrl)
                    }
                }
                else -> {

                }
            }
        }

        webView?.loadUrl(loadUrl)
    }

    fun getOAuthCode(url: String, hookingUrl: String) {
        val arr = url.split("?", "&")
        for (elem in arr) {
            Log.i(TAG, "==== elem - " + elem)
            if (!elem.equals(hookingUrl) && elem.contains("code")) {
                closeFullPopup(elem.split("=")[1])
                break
            }
        }
    }

    fun closeFullPopup(code: String) {
        runOnUiThread {
            val intent = Intent()
            intent.putExtra("code", code)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
}