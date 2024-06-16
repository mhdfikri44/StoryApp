package com.fikri.storyapp.view.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fikri.storyapp.data.UserRepository
import com.fikri.storyapp.data.response.AddResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class AddViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _addResponse = MutableLiveData<AddResponse>()
    val addResponse: LiveData<AddResponse> = _addResponse

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun addStory(photo: MultipartBody.Part, description: RequestBody) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = userRepository.addStory(photo, description)
                _addResponse.value = response

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, AddResponse::class.java)
                _error.value = errorResponse.message
            }
            _isLoading.value = false
        }
    }
}