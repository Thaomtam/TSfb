package com.fbautomanager.app.ui.home.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.fbautomanager.app.R
import com.fbautomanager.app.databinding.DialogAddAccountBinding
import com.fbautomanager.app.util.RootUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddAccountDialog(
    private val onAccountAdded: (name: String, packageName: String) -> Unit
) : DialogFragment() {

    private var _binding: DialogAddAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnScan.setOnClickListener {
            scanFacebookPackages()
        }

        binding.btnAdd.setOnClickListener {
            val name = binding.etAccountName.text.toString().trim()
            val packageName = binding.etPackageName.text.toString().trim()

            if (name.isEmpty() || packageName.isEmpty()) {
                Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kiểm tra package có tồn tại không
            CoroutineScope(Dispatchers.IO).launch {
                val isInstalled = RootUtil.isPackageInstalled(packageName)

                withContext(Dispatchers.Main) {
                    if (isInstalled) {
                        onAccountAdded(name, packageName)
                        dismiss()
                    } else {
                        Toast.makeText(
                            context,
                            "Package không tồn tại trên thiết bị",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_rounded_bg)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Quét các package Facebook đã cài đặt
     */
    private fun scanFacebookPackages() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnScan.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            val packages = RootUtil.getAllFacebookPackages()

            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
                binding.btnScan.isEnabled = true

                if (packages.isNotEmpty()) {
                    // Hiển thị dialog chọn package
                    showPackageSelectionDialog(packages)
                } else {
                    Toast.makeText(
                        context,
                        "Không tìm thấy ứng dụng Facebook nào",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    /**
     * Hiển thị dialog chọn package
     */
    private fun showPackageSelectionDialog(packages: List<String>) {
        val packageNames = packages.toTypedArray()
        val packageLabels = packages.map {
            "Facebook (${it.substringAfterLast('.')})"
        }.toTypedArray()

        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Chọn ứng dụng Facebook")
            .setItems(packageLabels) { _, which ->
                val selectedPackage = packageNames[which]
                binding.etPackageName.setText(selectedPackage)
                binding.etAccountName.setText(packageLabels[which])
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
} 