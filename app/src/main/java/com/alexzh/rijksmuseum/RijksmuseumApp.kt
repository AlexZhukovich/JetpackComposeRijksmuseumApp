package com.alexzh.rijksmuseum

import android.app.Application
import com.alexzh.rijksmuseum.data.remote.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class RijksmuseumApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@RijksmuseumApp)
            modules(
                networkModule,
            )
        }
    }
}