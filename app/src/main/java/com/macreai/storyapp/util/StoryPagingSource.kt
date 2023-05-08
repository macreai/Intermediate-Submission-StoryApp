package com.macreai.storyapp.util

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.macreai.storyapp.model.remote.api.ApiService
import com.macreai.storyapp.model.remote.response.ListStoryItem
import kotlinx.coroutines.flow.first
import java.lang.Exception

class StoryPagingSource(private val apiService: ApiService, private val preferences: UserPreferences)
    : PagingSource<Int, ListStoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val name = preferences.getUser().first()
            val responseData = apiService.getAllStories("Bearer $name", page, params.loadSize).listStory ?: emptyList()


            LoadResult.Page(
                data = responseData,
                if (page == INITIAL_PAGE_INDEX) null else page - 1,
                if (responseData.isEmpty()) null else page + 1
            )
        } catch (exception: Exception){
            Log.d("StoryPagingSource", "Data loaded from server: $exception")
            return LoadResult.Error(exception)
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }
}