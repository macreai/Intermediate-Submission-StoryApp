package com.macreai.storyapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.macreai.storyapp.util.UserPreferences

class AddStoryViewModel(private val preferences: UserPreferences): ViewModel() {

    fun getUser(): LiveData<String> = preferences.getUser().asLiveData()
}