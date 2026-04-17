package rs.edu.raf.rma.android

import android.app.Application
import android.util.Log
import org.koin.android.ext.koin.androidContext
import rs.edu.raf.rma.di.initKoin

class MovieApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("MovieApp", "Application started")

        initKoin {
            androidContext(this@MovieApplication)
        }
    }
}