package kr.co.kimd.hybridapp.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kr.co.kimd.hybridapp.R
import kr.co.kimd.hybridapp.util.ActivityCode
import kr.co.kimd.hybridapp.util.App
import kr.co.kimd.hybridapp.util.Util
import kr.co.kimd.hybridapp.webview.CustomWebView
import kr.co.kimd.hybridapp.webview.WebViewAction

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private var webView: CustomWebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportActionBar?.title = "타이틀 수정"
        setContentView(R.layout.activity_main)

        val app = this.application as App
        Util.getInstance().setApplication(app)
        Log.i(TAG,"==== ComponentNamed !!!!" + app.getActiveActivity())

        initWebView()
    }

    fun initWebView() {
        webView = findViewById(R.id.webView)

        webView?.setWebViewAction { webViewAction, result ->
            when(webViewAction) {
                WebViewAction.PageStarted -> Log.i(TAG,"==== call PageStarted !!!!")
                WebViewAction.PageFinished -> Log.i(TAG, "==== call PageFinished !!!!")
                WebViewAction.ShouldOverrideUrlLoading -> Log.i(TAG, "==== call ShouldOverrideUrlLoading !!! - " + result)
                else -> {

                }
            }
        }

        webView?.loadUrl("http://10.10.10.108:3000")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i(TAG,"==== onActivityResult !!!!" + requestCode)
        if (resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                Util.getInstance().getActivityRequestCode(ActivityCode.FullPopup) -> {
                    webView?.closeFullPopup(data?.getStringExtra("code"))
                }
            }
        }
    }
}
