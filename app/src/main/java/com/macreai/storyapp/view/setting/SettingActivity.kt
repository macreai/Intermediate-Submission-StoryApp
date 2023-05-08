package com.macreai.storyapp.view.setting

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.macreai.storyapp.R
import com.macreai.storyapp.databinding.ActivitySettingBinding
import com.macreai.storyapp.util.UserPreferences
import com.macreai.storyapp.view.main.MainActivity
import com.macreai.storyapp.viewmodel.MainViewModel
import com.macreai.storyapp.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private lateinit var mainViewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreferences.getInstance(dataStore)
        mainViewModel = ViewModelProvider(this, ViewModelFactory(pref))[MainViewModel::class.java]

        binding.localization.setOnClickListener{
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }

        binding.logout.setOnClickListener{
            mainViewModel.saveUser("")
            startActivity(Intent(this@SettingActivity, MainActivity::class.java))
            Toast.makeText(this, getString(R.string.user_logout), Toast.LENGTH_SHORT).show()
        }
    }
}