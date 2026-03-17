package com.software.biliapp.ui.recommend

import com.software.biliapp.domain.model.RecommendDataDomain

data class BiliRecommendUiState(
    val recommendVideoList: RecommendDataDomain? = null,
    val isLoading: Boolean = false
)