package com.example.twinsound

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    private lateinit var audioManager: AudioManager
    private lateinit var status: TextView
    private lateinit var devices: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 40, 40, 40)
        }

        val title = TextView(this).apply { text = "TwinSound"; textSize = 30f }
        status = TextView(this).apply { textSize = 17f; setPadding(0, 12, 0, 18) }
        devices = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }

        val refresh = Button(this).apply {
            text = "Scan for audio outputs"
            setOnClickListener { scan() }
        }

        val system = Button(this).apply {
            text = "Open Android audio / Bluetooth settings"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
            }
        }

        root.addView(title)
        root.addView(status)
        root.addView(devices)
        root.addView(refresh)
        root.addView(system)
        setContentView(root)

        requestPermissionsIfNeeded()
        scan()
    }

    private fun requestPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= 31 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN),
                100
            )
        }
    }

    private fun scan() {
        devices.removeAllViews()
        val outputs = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)

        val bluetooth = outputs.filter {
            it.type == AudioDeviceInfo.TYPE_BLUETOOTH_A2DP ||
            (Build.VERSION.SDK_INT >= 31 && (
                it.type == AudioDeviceInfo.TYPE_BLE_HEADSET ||
                it.type == AudioDeviceInfo.TYPE_BLE_SPEAKER ||
                it.type == AudioDeviceInfo.TYPE_BLE_BROADCAST
            ))
        }

        val le = bluetooth.filter {
            Build.VERSION.SDK_INT >= 31 &&
            (it.type == AudioDeviceInfo.TYPE_BLE_HEADSET ||
             it.type == AudioDeviceInfo.TYPE_BLE_SPEAKER ||
             it.type == AudioDeviceInfo.TYPE_BLE_BROADCAST)
        }

        status.text = when {
            bluetooth.size >= 2 && le.size >= 2 ->
                "✓ Two Bluetooth outputs detected. LE Audio appears available; your phone/system may be able to synchronize them."
            bluetooth.size >= 2 ->
                "✓ Two Bluetooth outputs detected. Automatic simultaneous A2DP playback depends on your phone manufacturer/system."
            bluetooth.size == 1 ->
                "1 Bluetooth audio output detected. Connect the second speaker first."
            else ->
                "No Bluetooth audio outputs are exposed to the app. Connect your speakers in Android settings."
        }

        bluetooth.forEachIndexed { index, d ->
            val kind = when (d.type) {
                AudioDeviceInfo.TYPE_BLUETOOTH_A2DP -> "Bluetooth A2DP"
                AudioDeviceInfo.TYPE_BLE_HEADSET -> "Bluetooth LE Audio"
                AudioDeviceInfo.TYPE_BLE_SPEAKER -> "Bluetooth LE speaker"
                AudioDeviceInfo.TYPE_BLE_BROADCAST -> "Bluetooth LE broadcast"
                else -> "Bluetooth"
            }
            devices.addView(TextView(this).apply {
                text = "🔊 ${index + 1}. ${d.productName ?: "Unknown speaker"}\n   $kind"
                textSize = 16f
                setPadding(0, 10, 0, 10)
            })
        })

        if (bluetooth.size >= 2) {
            val hint = TextView(this).apply {
                text = "\nNext: if your phone exposes a multi-output/group route, Android's system output switcher is the component that actually controls it. TwinSound deliberately does not fake a second route when Android doesn't provide one."
                textSize = 14f
            }
            devices.addView(hint)
        }
    }
}
