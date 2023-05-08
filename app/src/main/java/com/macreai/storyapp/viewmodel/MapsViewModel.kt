package com.macreai.storyapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.macreai.storyapp.model.remote.api.ApiConfig
import com.macreai.storyapp.model.remote.response.ListStoryItem
import com.macreai.storyapp.model.remote.response.ListStoryResponse
import com.macreai.storyapp.util.UserPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private val preferences: UserPreferences): ViewModel() {

    private val _listStories = MutableLiveData<List<ListStoryItem>>()
    val listStories: LiveData<List<ListStoryItem>> = _listStories

    fun getUser(): LiveData<String> = preferences.getUser().asLiveData()

    fun getStoriesLocation(user: String){
        val client = ApiConfig.getApiService().getStoriesLocation("Bearer $user")
        client.enqueue(object : Callback<ListStoryResponse> {
            override fun onResponse(
                call: Call<ListStoryResponse>,
                response: Response<ListStoryResponse>
            ) {
                if (response.isSuccessful){
                    _listStories.postValue(response.body()?.listStory as List<ListStoryItem>)
                } else {
                    Log.e(TAG, "onResponse: ${response.message()}" )
                }
            }

            override fun onFailure(call: Call<ListStoryResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }

        })
    }

    companion object{
        private const val TAG = "MapsViewModel"
    }
}