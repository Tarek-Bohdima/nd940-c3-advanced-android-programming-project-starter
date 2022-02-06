package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import com.udacity.databinding.ActivityMainBinding
import com.udacity.util.sendNotification


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var binding: ActivityMainBinding

    private lateinit var notificationManager: NotificationManager
    private lateinit var loadingButton: LoadingButton
    private var downloadedUrl: String? = null
    lateinit var downloadManager: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)

        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager

        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager

        createChannel(getString(R.string.notification_channel_id), getString(R.string.app_name))

        loadingButton = binding.contentMain.customButton

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        loadingButton.setOnClickListener {
            checkRadioButton()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private fun checkRadioButton() {
        val radioGroup = binding.contentMain.radioGroup
        when (radioGroup.checkedRadioButtonId) {
            R.id.button_glide -> {
                download(GLIDE_URL)
                downloadedUrl = getString(R.string.radio_label_glide)
            }
            R.id.button_loadApp -> {
                download(LOADAPP_URL)
                downloadedUrl = getString(R.string.radio_label_loadApp)
            }
            R.id.button_retrofit -> {
                download(RETROFIT_URL)
                downloadedUrl = getString(R.string.radio_label_retrofit)
            }
            else -> {
                loadingButton.changeButtonState(ButtonState.Completed)
                Toast.makeText(this, getString(R.string.toast_error_text),
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            var status: Int? = null
            val statusMessage: String
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (id == downloadID) {
                //DownloadManager.Query() is used to filter DownloadManager queries
                val query = id.let { DownloadManager.Query().setFilterById(it) }

                var cursor: Cursor? = null
                try {
                    cursor = downloadManager.query(query)

                    if (cursor.moveToFirst()) {
                        status =
                            cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                    }
                } finally {
                    cursor?.close()
                }
            }

            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                statusMessage = "Success"
                loadingButton.changeButtonState(ButtonState.Completed)
                notificationManager.sendNotification(downloadedUrl!!,
                    this@MainActivity,
                    statusMessage)
            }
            else if (status == DownloadManager.STATUS_FAILED) {
                statusMessage = "Fail"
                loadingButton.changeButtonState(ButtonState.Completed)
                notificationManager.sendNotification(downloadedUrl!!,
                    this@MainActivity,
                    statusMessage)
            }
        }
    }

    private fun download(url: String) {
        loadingButton.changeButtonState(ButtonState.Loading)
        changeRadioGroupState()
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        loadingButton.changeButtonState(ButtonState.Loading)

        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun changeRadioGroupState() {
        val radioGroup = binding.contentMain.radioGroup
        radioGroup.children.forEach {
            it.isEnabled = false
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                description = getString(R.string.app_description)
            }
            notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide"
        private const val LOADAPP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit"
    }

}
