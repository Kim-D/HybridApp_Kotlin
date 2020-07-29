package kr.co.kimd.hybridapp.util

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log

class App: Application() {
    companion object {
        private val TAG = App::class.java.simpleName
        private var activeActivity: Activity? = null
    }

    override fun onCreate() {
        super.onCreate()
        setupActivityListener()
    }

    fun setupActivityListener() {
        registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(acitivity: Activity, savedInstanceState: Bundle?) {
                Log.i(TAG,"==== call onActivityCreated !!!!")
            }

            override fun onActivityStarted(acitivity: Activity) {
                Log.i(TAG,"==== call onActivityStarted !!!!")
            }

            override fun onActivityResumed(acitivity: Activity) {
                Log.i(TAG,"==== call onActivityResumed !!!!")
                activeActivity = acitivity
            }

            override fun onActivityPaused(acitivity: Activity) {
                Log.i(TAG,"==== call onActivityPaused !!!!")
                activeActivity = null
            }

            override fun onActivityStopped(acitivity: Activity) {
                Log.i(TAG,"==== call onActivityStopped !!!!")
            }

            override fun onActivityDestroyed(acitivity: Activity) {
                Log.i(TAG,"==== call onActivityDestroyed !!!!")
            }

            override fun onActivitySaveInstanceState(acitivity: Activity, p1: Bundle) {
                Log.i(TAG,"==== call onActivitySaveInstanceState !!!!")
            }
        })
    }

    open fun getActiveActivity(): Activity? {
        return activeActivity
    }
}