package com.fikri.storyapp.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fikri.storyapp.data.UserRepository
import com.fikri.storyapp.data.response.Story
import kotlinx.coroutines.launch

class DetailViewModel(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _story = MutableLiveData<Story>()
    val story: LiveData<Story> = _story

    fun getStory(id: String) {
        viewModelScope.launch {
            val response = userRepository.getDetailStory(id)
            _story.value = (response.story)
        }
    }
}