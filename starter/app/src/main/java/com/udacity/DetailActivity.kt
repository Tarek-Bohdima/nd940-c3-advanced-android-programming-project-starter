package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.udacity.databinding.ActivityDetailBinding
import com.udacity.util.DOWNLOADED_FILENAME_KEY
import com.udacity.util.DOWNLOAD_STATUS_KEY
import com.udacity.util.cancelNotifications

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        setSupportActionBar(binding.toolbar)

        val notificationManager = ContextCompat.getSystemService(this,
            NotificationManager::class.java) as NotificationManager
        notificationManager.cancelNotifications()

        val downloadedFile = intent.getStringExtra(DOWNLOADED_FILENAME_KEY)
        val downloadStatus = intent.getStringExtra(DOWNLOAD_STATUS_KEY)
        binding.contentDetail.apply {
            downloadedFileData = downloadedFile
            statusData = downloadStatus
        }

        binding.contentDetail.textStatus.setTextColor(if (binding.contentDetail.statusData == "Success") Color.BLACK else Color.RED)

        val intent = Intent(this, MainActivity::class.java)
        binding.contentDetail.buttonBack.setOnClickListener {
            startActivity(intent)
        }
    }

}
