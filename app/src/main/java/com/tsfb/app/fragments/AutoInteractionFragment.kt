private fun checkOverlayPermission() {
    if (!Settings.canDrawOverlays(requireContext())) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${requireContext().packageName}")
        )
        startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
    } else {
        startFloatingBubble()
    }
}

private fun startFloatingBubble() {
    val intent = Intent(requireContext(), FloatingBubbleService::class.java)
    requireContext().startService(intent)
    requireActivity().moveTaskToBack(true)
}

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
        if (Settings.canDrawOverlays(requireContext())) {
            startFloatingBubble()
        }
    }
}

companion object {
    private const val OVERLAY_PERMISSION_REQUEST_CODE = 100
}

// Trong onViewCreated, thay đổi click listener của startButton:
binding.startButton.setOnClickListener {
    checkOverlayPermission()
} 