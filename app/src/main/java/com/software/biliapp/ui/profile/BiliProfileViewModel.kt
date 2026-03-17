package com.software.biliapp.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.software.biliapp.data.mongo.bili.BiliSessionManager
import com.software.biliapp.domain.usecase.BiliGetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BiliProfileViewModel @Inject constructor(
    private val biliGetUserInfoUseCase: BiliGetUserInfoUseCase,
    private val biliSessionManager: BiliSessionManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(BiliProfileState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: BiliProfileEvent) {
        when (event) {
            BiliProfileEvent.GetUserInfo -> getUserInfo()
        }
    }

    init {
        getUserInfo()
    }

    private fun getUserInfo() {
        viewModelScope.launch {
            val actualCookie = biliSessionManager.cookieFlow.first()

            Log.d("BiliProfileViewModel", "真实的 Cookie: $actualCookie")

            if (actualCookie.isEmpty()) {
                Log.e("BiliProfileViewModel", "Cookie 为空，用户可能未登录")
                return@launch
            }

            biliGetUserInfoUseCase.invoke(actualCookie).fold(
                onSuccess = { data ->
                    Log.d("BiliProfileViewModel", "获取成功: $data")
                    _uiState.update {
                        it.copy(userInfo = data)
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(userInfo = null)
                    }
                    Log.e("BiliProfileViewModel", "获取失败: ${error.message}")
                }
            )
        }
    }
}