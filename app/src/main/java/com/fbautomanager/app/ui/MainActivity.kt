package com.fbautomanager.app.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.fbautomanager.app.FBAutoManagerApp
import com.fbautomanager.app.R
import com.fbautomanager.app.databinding.ActivityMainBinding
import com.fbautomanager.app.service.AutomationService
import com.fbautomanager.app.ui.help.HelpFragment
import com.fbautomanager.app.ui.home.HomeFragment
import com.fbautomanager.app.ui.logs.LogsFragment
import com.fbautomanager.app.ui.settings.SettingsFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.appBarMain.toolbar)
        
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.appBarMain.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        
        binding.navView.setNavigationItemSelectedListener(this)
        
        // Kiểm tra quyền root
        if (!FBAutoManagerApp.isRooted) {
            showRootRequiredDialog()
        }
        
        // Kiểm tra quyền accessibility service
        if (!isAccessibilityServiceEnabled()) {
            showAccessibilityServiceDialog()
        }
        
        // Hiển thị fragment mặc định (Home)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, HomeFragment())
                .commit()
            binding.navView.setCheckedItem(R.id.nav_home)
        }
    }
    
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Xử lý chuyển đổi giữa các fragment
        val fragment: Fragment = when (item.itemId) {
            R.id.nav_home -> HomeFragment()
            R.id.nav_settings -> SettingsFragment()
            R.id.nav_logs -> LogsFragment()
            R.id.nav_help -> HelpFragment()
            else -> HomeFragment()
        }
        
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_content_main, fragment)
            .commit()
        
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
    
    /**
     * Kiểm tra xem accessibility service đã được bật chưa
     */
    private fun isAccessibilityServiceEnabled(): Boolean {
        val accessibilityEnabled = try {
            Settings.Secure.getInt(contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
        } catch (e: Settings.SettingNotFoundException) {
            0
        }
        
        if (accessibilityEnabled == 1) {
            val services = Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false
            
            return services.contains("${packageName}/${AutomationService::class.java.name}")
        }
        
        return false
    }
    
    /**
     * Hiển thị hộp thoại yêu cầu quyền root
     */
    private fun showRootRequiredDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.root_required)
            .setMessage(R.string.root_required_message)
            .setPositiveButton(R.string.confirm) { _, _ ->
                Toast.makeText(this, "Vui lòng cấp quyền root và khởi động lại ứng dụng", Toast.LENGTH_LONG).show()
                finish()
            }
            .setCancelable(false)
            .show()
    }
    
    /**
     * Hiển thị hộp thoại yêu cầu bật accessibility service
     */
    private fun showAccessibilityServiceDialog() {
        AlertDialog.Builder(this)
            .setTitle("Yêu cầu quyền Accessibility Service")
            .setMessage("Ứng dụng cần quyền Accessibility Service để tự động hóa các tác vụ. Vui lòng bật dịch vụ này trong cài đặt.")
            .setPositiveButton(R.string.confirm) { _, _ ->
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivity(intent)
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                Toast.makeText(this, "Ứng dụng sẽ không hoạt động đầy đủ nếu không có quyền này", Toast.LENGTH_LONG).show()
            }
            .show()
    }
} 