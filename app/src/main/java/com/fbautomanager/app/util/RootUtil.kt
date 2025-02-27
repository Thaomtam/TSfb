package com.fbautomanager.app.util

import com.topjohnwu.superuser.Shell

object RootUtil {
    /**
     * Kiểm tra xem thiết bị đã được root chưa
     * @return true nếu thiết bị đã root, false nếu chưa
     */
    suspend fun isRooted(): Boolean {
        return try {
            val result = Shell.cmd("id").exec()
            result.isSuccess && result.out.any { it.contains("uid=0") }
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Thực thi lệnh shell với quyền root
     * @param command Lệnh cần thực thi
     * @return Kết quả thực thi lệnh
     */
    suspend fun executeCommand(command: String): Shell.Result {
        return Shell.cmd(command).exec()
    }
    
    /**
     * Mở ứng dụng Facebook với package cụ thể
     * @param packageName Tên package của ứng dụng Facebook
     * @return true nếu mở thành công, false nếu thất bại
     */
    suspend fun openFacebookApp(packageName: String): Boolean {
        val result = Shell.cmd("am start -n $packageName/com.facebook.katana.LoginActivity").exec()
        return result.isSuccess
    }
    
    /**
     * Kiểm tra xem package có tồn tại trên thiết bị không
     * @param packageName Tên package cần kiểm tra
     * @return true nếu package tồn tại, false nếu không
     */
    suspend fun isPackageInstalled(packageName: String): Boolean {
        val result = Shell.cmd("pm list packages | grep $packageName").exec()
        return result.isSuccess && result.out.isNotEmpty()
    }
    
    /**
     * Lấy danh sách tất cả các package Facebook đã cài đặt
     * @return Danh sách các package Facebook
     */
    suspend fun getAllFacebookPackages(): List<String> {
        val result = Shell.cmd("pm list packages | grep facebook").exec()
        val packages = mutableListOf<String>()
        
        if (result.isSuccess) {
            for (line in result.out) {
                val packageName = line.replace("package:", "").trim()
                if (packageName.isNotEmpty()) {
                    packages.add(packageName)
                }
            }
        }
        
        return packages
    }
} 