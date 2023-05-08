package com.macreai.storyapp.view.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.macreai.storyapp.R
import com.macreai.storyapp.databinding.ActivityMainBinding
import com.macreai.storyapp.adapter.LoadingStateAdapter
import com.macreai.storyapp.adapter.StoryAdapter
import com.macreai.storyapp.util.UserPreferences
import com.macreai.storyapp.view.login.LoginActivity
import com.macreai.storyapp.view.map.MapsActivity
import com.macreai.storyapp.view.setting.SettingActivity
import com.macreai.storyapp.view.story.AddStoryActivity
import com.macreai.storyapp.viewmodel.MainViewModel
import com.macreai.storyapp.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var adapter: StoryAdapter

    private var menuClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreferences.getInstance(dataStore)
        mainViewModel = ViewModelProvider(this, ViewModelFactory(pref))[MainViewModel::class.java]

        binding.rvStories.layoutManager = LinearLayoutManager(this)

        tokenCheck()
        buttonClick()
    }

    private fun buttonClick() {

        binding.menu.setOnClickListener {
            onButtonClicked()
        }

        binding.addStory.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }

        binding.map.setOnClickListener {
            val intentToMap = Intent(this, MapsActivity::class.java)
            startActivity(intentToMap)
        }
    }

    private fun onButtonClicked() {
        if (!menuClicked){
            binding.apply {
                addStory.visibility = View.VISIBLE
                map.visibility = View.VISIBLE

                val fadeInAdd = ObjectAnimator.ofFloat(binding.addStory, "alpha", 0f, 1f).setDuration(500)
                val fadeInMap = ObjectAnimator.ofFloat(binding.map, "alpha", 0f, 1f).setDuration(500)

                AnimatorSet().apply {
                    playTogether(fadeInAdd, fadeInMap)
                }.start()
            }
        } else {
            binding.apply {
                addStory.visibility = View.GONE
                map.visibility = View.GONE

                val fadeOutAdd = ObjectAnimator.ofFloat(binding.addStory, "alpha", 1f, 0f).setDuration(500)
                val fadeOutMap = ObjectAnimator.ofFloat(binding.map, "alpha", 1f, 0f).setDuration(500)
                AnimatorSet().apply {
                    playTogether(fadeOutAdd, fadeOutMap)
                }.start()
            }
        }

        menuClicked = !menuClicked
    }

    private fun tokenCheck() {
        mainViewModel.getUser().observe(this, Observer{
            if (it.isEmpty()){
                val intentNoToken = Intent(this, LoginActivity::class.java)
                startActivity(intentNoToken)
                finish()
            } else {
                getData()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.setting -> startActivity(Intent(this@MainActivity, SettingActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getData(){
        adapter = StoryAdapter()
        binding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                adapter.retry()
            }
        )
        mainViewModel.story.observe(this@MainActivity, Observer {
            adapter.submitData(lifecycle, it)
            Log.d("MainActivity", "getData: $it")
        })

    }
}