package com.fbautomanager.app

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.fbautomanager.app.data.AppDatabase
import com.fbautomanager.app.util.RootUtil
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FBAutoManagerApp : Application() {

    companion object {
        lateinit var instance: FBAutoManagerApp
            private set
        lateinit var database: AppDatabase
            private set
        var isRooted = false
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Khởi tạo Shell cho quyền root
        Shell.enableVerboseLogging = BuildConfig.DEBUG
        Shell.setDefaultBuilder(Shell.Builder.create()
            .setFlags(Shell.FLAG_REDIRECT_STDERR)
            .setTimeout(10)
        )
        
        // Kiểm tra quyền root
        CoroutineScope(Dispatchers.IO).launch {
            isRooted = RootUtil.isRooted()
        }
        
        // Khởi tạo database
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "fbautomanager-database"
        ).build()
    }
    
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }
} 