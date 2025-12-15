package com.mobilecomputing.myfinance

import android.app.Application
import com.mobilecomputing.myfinance.data.AppContainer
import com.mobilecomputing.myfinance.data.DefaultAppContainer

class MyFinanceApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
