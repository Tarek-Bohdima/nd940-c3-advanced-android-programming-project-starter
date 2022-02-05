package com.udacity

import android.app.NotificationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.udacity.databinding.ActivityDetailBinding
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
    }

}
