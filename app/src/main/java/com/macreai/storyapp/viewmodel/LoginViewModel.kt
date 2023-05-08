package com.macreai.storyapp.viewmodel

import androidx.lifecycle.*
import com.macreai.storyapp.util.UserPreferences
import kotlinx.coroutines.launch


class LoginViewModel(private val preference: UserPreferences): ViewModel() {

    fun saveUser(user: String){
        viewModelScope.launch {
            preference.saveUser(user)
        }
    }

}