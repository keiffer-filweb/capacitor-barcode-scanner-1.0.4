package com.capacitorjs.barcodescanner

import android.content.Intent
import androidx.activity.result.ActivityResult
import com.getcapacitor.JSObject
import com.getcapacitor.Plugin
import com.getcapacitor.PluginCall
import com.getcapacitor.PluginMethod
import com.getcapacitor.annotation.ActivityCallback
import com.getcapacitor.annotation.CapacitorPlugin
// OutSystems AAR removed; using internal ScannerActivity with ML Kit

@CapacitorPlugin(name = "CapacitorBarcodeScanner")
class CapacitorBarcodeScannerPlugin : Plugin() {
    companion object {
        private const val ERROR_FORMAT_PREFIX = "OS-PLUG-BARC-"
        private const val SCAN_REQUEST_CODE = 112
    }

    override fun load() {
        super.load()
    }

    @PluginMethod
    fun scanBarcode(call: PluginCall) {
        val hint = call.getInt("hint")
        val scanInstructions = call.getString("scanInstructions")
        val scanButton = call.getBoolean("scanButton", false)
        val scanText = call.getString("scanText", "")
        val cameraDirection = call.getInt("cameraDirection")

        val nativeOptions = call.getObject("native")

        val scanOrientation = nativeOptions?.getInteger("scanOrientation")
        val androidScanningLibrary = nativeOptions?.getJSObject("android")?.getString("scanningLibrary")

    val scanIntent = Intent(activity, ScannerActivity::class.java)
    // Pass simple options as extras for possible future use by the ScannerActivity
    scanIntent.putExtra("scanInstructions", scanInstructions)
    scanIntent.putExtra("scanButton", scanButton)
    scanIntent.putExtra("scanText", scanText)
    scanIntent.putExtra("cameraDirection", cameraDirection)
    scanIntent.putExtra("hint", hint)
    scanIntent.putExtra("scanOrientation", scanOrientation)
    scanIntent.putExtra("androidScanningLibrary", androidScanningLibrary)

    startActivityForResult(call, scanIntent, "handleScanResult")
    }

    @ActivityCallback
    fun handleScanResult(call: PluginCall, result: ActivityResult) {
        // Minimal stubbed handling: return a cancelled or empty scan result.
        if (result.resultCode == android.app.Activity.RESULT_OK && result.data != null) {
            val ret = JSObject()
            val scanned = result.data?.getStringExtra("scanned_value") ?: ""
            ret.put("ScanResult", scanned)
            call.resolve(ret)
        } else {
            call.reject("Scan cancelled or failed", formatErrorCode(1))
        }
    }

    private fun formatErrorCode(code: Int): String {
        return ERROR_FORMAT_PREFIX + code.toString().padStart(4, '0')
    }
}
