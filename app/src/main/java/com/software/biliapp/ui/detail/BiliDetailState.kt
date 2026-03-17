package com.software.biliapp.ui.detail

import com.software.biliapp.domain.model.ReplyDataDomain
import com.software.biliapp.domain.model.VideoDetailDomain

data class BiliDetailState(
    val state: State = State.Idle,
    val videoDetail: VideoDetailDomain? = null,
    val replyData: ReplyDataDomain? = null,
)

sealed class State {
    object Idle : State()
    object Loading : State()
    object Success : State()
    class Error(val message: String) : State()
}