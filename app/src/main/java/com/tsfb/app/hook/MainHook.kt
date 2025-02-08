package com.tsfb.app.hook

import com.github.kyuubiran.ezxhelper.EzXHelper
import com.github.kyuubiran.ezxhelper.HookFactory.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import android.content.Context

class MainHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName != "com.facebook.katana") return
        
        try {
            EzXHelper.initHandleLoadPackage(lpparam)
            EzXHelper.setLogTag("TSfb")
            EzXHelper.setToastTag("TSfb")
            
            val context = getSystemContext()
            val repository = AppRepository(context)
            val securityManager = SecurityManager(context)
            val automation = FacebookAutomation(repository, securityManager)
            val hooks = FacebookHooks(lpparam, automation, repository)
            
            hooks.init()
        } catch (e: Throwable) {
            EzXHelper.log("Lỗi khởi tạo module: ${e.message}")
        }
    }

    private fun getSystemContext(): Context {
        val activityThread = Class.forName("android.app.ActivityThread")
        val currentActivityThread = activityThread.getMethod("currentActivityThread").invoke(null)
        return activityThread.getMethod("getSystemContext").invoke(currentActivityThread) as Context
    }
} 