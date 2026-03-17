package com.software.biliapp.ui.recommend

sealed class BiliRecommendUiEvent {
    data class RecommendList(val idx: Long) : BiliRecommendUiEvent()
}