package com.fikri.storyapp.data

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.fikri.storyapp.data.pref.UserModel
import com.fikri.storyapp.data.pref.UserPreference
import com.fikri.storyapp.data.response.AddResponse
import com.fikri.storyapp.data.response.DetailStoryResponse
import com.fikri.storyapp.data.response.ListStoryItem
import com.fikri.storyapp.data.response.LoginResponse
import com.fikri.storyapp.data.response.RegisterResponse
import com.fikri.storyapp.data.response.StoryResponse
import com.fikri.storyapp.data.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
) {
    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.registerUser(name, email, password)
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.loginUser(email, password)
    }

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = { StoryPagingSource(apiService) }
        ).liveData
    }

    suspend fun getStoriesWithLocation(): StoryResponse {
        return apiService.getStoriesWithLocation()
    }

    suspend fun getDetailStory(id: String): DetailStoryResponse {
        return apiService.getDetailStory(id)
    }

    suspend fun addStory(photo: MultipartBody.Part, description: RequestBody): AddResponse {
        return apiService.addStory(photo, description)
    }

    companion object {
        fun getInstance(apiService: ApiService, userPreference: UserPreference) =
            UserRepository(apiService, userPreference)
    }
}