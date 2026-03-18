package com.software.biliapp.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.software.biliapp.data.mongo.bili.BiliSessionManager
import com.software.biliapp.domain.usecase.BiliGetPopularListUseCase
import com.software.biliapp.domain.usecase.BiliGetUserInfoUseCase
import com.software.biliapp.domain.usecase.GetRecommendVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BiliHomeUiViewModel @Inject constructor(
    private val biliGetUserInfoUseCase: BiliGetUserInfoUseCase,
    private val biliSessionManager: BiliSessionManager,
    private val biliGetPopularListUseCase: BiliGetPopularListUseCase,
    private val getRecommendVideosUseCase: GetRecommendVideosUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(BiliHomeUiState())
    val uiState = _uiState.asStateFlow()

    val popularListPagingFlow = biliGetPopularListUseCase.invoke().cachedIn(viewModelScope)
    val recommendListPagingFlow = getRecommendVideosUseCase.invoke().cachedIn(viewModelScope)

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