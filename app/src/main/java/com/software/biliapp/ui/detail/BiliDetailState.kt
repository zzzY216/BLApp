package com.software.biliapp.ui.detail

import com.software.biliapp.domain.model.ReplyDataDomain
import com.software.biliapp.domain.model.VideoDetailDomain

data class BiliDetailState(
    val state: State = State.Idle,
    val videoDetail: VideoDetailDomain? = null,
    val replyData: ReplyDataDomain? = null,
    val isLike: Boolean = false,
    val isFavorite: Boolean = false,
    val isCoin: Boolean = false,
    val isShare: Boolean = false,
    val isDislike: Boolean = false
)

sealed class State {
    object Idle : State()
    object Loading : State()
    object Success : State()
    class Error(val message: String) : State()
}