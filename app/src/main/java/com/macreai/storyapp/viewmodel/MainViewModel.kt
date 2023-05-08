package com.macreai.storyapp.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.macreai.storyapp.model.remote.response.ListStoryItem
import com.macreai.storyapp.repo.StoryRepository
import com.macreai.storyapp.util.UserPreferences
import kotlinx.coroutines.launch

class MainViewModel(private val preferences: UserPreferences, storyRepository: StoryRepository): ViewModel() {

    val story: LiveData<PagingData<ListStoryItem>> =
        storyRepository.getStories().cachedIn(viewModelScope)

    fun getUser(): LiveData<String> = preferences.getUser().asLiveData()

    fun saveUser(user: String){
        viewModelScope.launch {
            preferences.saveUser(user)
        }
    }
}