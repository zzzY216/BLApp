package com.software.biliapp.ui.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.software.biliapp.data.mongo.bili.BiliSessionManager
import com.software.biliapp.domain.usecase.BiliGetReplyListUseCase
import com.software.biliapp.domain.usecase.BiliGetVideoDetailUseCase
import com.software.biliapp.domain.usecase.BiliGetVideoPlayUrlUseCase
import com.software.biliapp.domain.usecase.BiliHasStatVideoUseCase
import com.software.biliapp.domain.usecase.BiliLikeVideoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BiliDetailViewModel @Inject constructor(
    private val biliGetVideoPlayUrlUseCase: BiliGetVideoPlayUrlUseCase,
    val player: ExoPlayer,
    private val biliGetVideoDetailUseCase: BiliGetVideoDetailUseCase,
    private val biliGetReplyListUseCase: BiliGetReplyListUseCase,
    private val biliLikeVideoUseCase: BiliLikeVideoUseCase,
    private val sessionManager: BiliSessionManager,
    private val biliHasStatVideoUseCase: BiliHasStatVideoUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(BiliDetailState())
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = Channel<BiliDetailUiEffect>()
    val uiEffect = _uiEffect.receiveAsFlow()

    fun onEvent(event: BiliDetailEvent) {
        when (event) {
            is BiliDetailEvent.LoadData -> loadData(
                event.avid,
                event.cid,
                event.qn,
                event.type,
                event.platform
            )

            is BiliDetailEvent.GetVideoDetail -> getVideoDetail(event.aid, event.bvid)
            is BiliDetailEvent.GetReplyList -> getReplyList(
                event.oid,
                event.type,
                event.sort,
                event.pn,
                event.ps
            )

            is BiliDetailEvent.LikeVideo -> likeVideo()
        }
    }

    private fun hasLikeVideo(
        aid: String? = null,
        bvid: String
    ) {
        viewModelScope.launch {
            biliHasStatVideoUseCase.invoke(aid, bvid).fold(
                onSuccess = { stat ->
                    _uiState.update {
                        it.copy(
                            isLike = stat.data != 0,
                        )
                    }
                    Log.d("BiliDetailViewModel", uiState.value.isLike.toString())
                    _uiEffect.send(BiliDetailUiEffect.ShowToast("获取点赞信息${stat.data}"))
                },
                onFailure = {
                    Log.e("BiliDetailViewModel", it.toString())
                    _uiEffect.send(BiliDetailUiEffect.ShowToast(it.toString()))
                }
            )
        }
    }

    private fun likeVideo() {
        Log.d("BiliDetailViewModel", "likeVideo")
        val currentState = _uiState.value
        val isCurrentlyListed = currentState.isLike
        val targetLikeAction = if (isCurrentlyListed) 2 else 1
        val bvid = currentState.videoDetail?.bvid ?: return
        viewModelScope.launch {
            biliLikeVideoUseCase.invoke(
                aid = null,
                bvid = bvid,
                like = targetLikeAction,
            ).fold(
                onSuccess = {
                    _uiState.update { state ->
                        val oldStat = state.videoDetail?.stat
                        val newLikeCount = if (isCurrentlyListed) {
                            (oldStat?.like ?: 1) - 1
                        } else  {
                            (oldStat?.like ?: 0) + 1
                        }
                        state.copy(
                            isLike = !isCurrentlyListed,
                            videoDetail = (oldStat?.copy(
                                like = newLikeCount
                            ) ?: oldStat)?.let { it1 ->
                                state.videoDetail?.copy(
                                    stat = it1
                                )
                            }
                        )
                    }
                    _uiEffect.send(BiliDetailUiEffect.ShowToast("点赞成功"))
                },
                onFailure = {
                    _uiEffect.send(BiliDetailUiEffect.ShowToast("点赞失败"))
                }
            )
        }
    }

    private fun getReplyList(
        oid: Long,
        type: Int,
        sort: Int,
        pn: Int,
        ps: Int
    ) {
        viewModelScope.launch {
            _uiEffect.send(BiliDetailUiEffect.ShowToast("开始获取评论"))
            biliGetReplyListUseCase.invoke(oid, type, sort, pn, ps).fold(
                onSuccess = { replyData ->
                    Log.d("BiliDetailViewModel", "right")
                    _uiState.update {
                        it.copy(
                            replyData = replyData
                        )
                    }
                },
                onFailure = {
                    Log.e("BiliDetailViewModel", it.toString())
                    _uiEffect.send(BiliDetailUiEffect.ShowToast(it.toString()))
                }
            )
        }
    }

    private fun getVideoDetail(aid: String, bvid: String) {
        viewModelScope.launch {
            biliGetVideoDetailUseCase.invoke(aid, null).fold(
                onSuccess = { videoDetail ->
                    _uiState.update {
                        it.copy(
                            state = State.Success,
                            videoDetail = videoDetail
                        )
                    }
                    val currentBvid = _uiState.value.videoDetail?.bvid
                    if (!currentBvid.isNullOrBlank()) {
                        hasLikeVideo(bvid = currentBvid)
                    } else {
                        Log.w("BiliDetailViewModel", "bvid 为空，跳过获取点赞状态")
                    }
                },
                onFailure = { errorMessage ->
                    Log.e("BiliDetailViewModel", errorMessage.toString())
                    _uiEffect.send(BiliDetailUiEffect.ShowToast(errorMessage.toString()))
                }
            )
        }
    }

    private fun loadData(
        avid: String,
        cid: String,
        qn: Int,
        type: String,
        platform: String
    ) {
        _uiState.update {
            it.copy(state = State.Loading)
        }
        viewModelScope.launch {
            biliGetVideoPlayUrlUseCase.invoke(avid, cid, qn, type, platform).fold(
                onSuccess = { videoData ->
                    val videoUrl = videoData.durl[0].url
                    val mediaItem = MediaItem.fromUri(videoUrl)
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.playWhenReady = true // 准备之后自动播放
                    _uiState.update {
                        it.copy(
                            state = State.Success
                        )
                    }
                },
                onFailure = { errorMessage ->
                    Log.e("BiliDetailViewModel", errorMessage.toString())
                    _uiState.update {
                        it.copy(
                            state = State.Error(errorMessage.toString())
                        )
                    }
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}