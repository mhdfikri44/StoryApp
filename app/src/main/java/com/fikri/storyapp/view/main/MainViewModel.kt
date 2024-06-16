package com.fikri.storyapp.view.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.fikri.storyapp.data.UserRepository
import com.fikri.storyapp.data.pref.UserModel
import com.fikri.storyapp.data.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    val story: LiveData<PagingData<ListStoryItem>> =
        userRepository.getStories().cachedIn(viewModelScope)

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}