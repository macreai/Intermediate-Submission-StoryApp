package com.macreai.storyapp.repo

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.macreai.storyapp.model.remote.api.ApiService
import com.macreai.storyapp.model.remote.response.ListStoryItem
import com.macreai.storyapp.util.StoryPagingSource
import com.macreai.storyapp.util.UserPreferences

class StoryRepository(private val apiService: ApiService, private val preferences: UserPreferences) {

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, preferences)
            }
        ).liveData
    }
}