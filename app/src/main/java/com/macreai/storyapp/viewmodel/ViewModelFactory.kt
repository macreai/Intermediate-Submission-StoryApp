package com.macreai.storyapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.macreai.storyapp.model.remote.api.ApiConfig
import com.macreai.storyapp.repo.StoryRepository
import com.macreai.storyapp.util.UserPreferences

class ViewModelFactory(private val preferences: UserPreferences): ViewModelProvider.NewInstanceFactory() {

    private val apiService = ApiConfig.getApiService()

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(preferences) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(preferences, StoryRepository(apiService, preferences)) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(preferences) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(preferences) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel Class: ${modelClass.name}")
        }
    }

}