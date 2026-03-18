package com.software.biliapp.ui.detail

sealed class BiliDetailEvent {
    class LoadData(
        val avid: String,
        val cid: String,
        val qn: Int,
        val type: String,
        val platform: String
    ) : BiliDetailEvent()
    class GetVideoDetail(val aid: String, val bvid: String): BiliDetailEvent()
    data class GetReplyList(
        val oid: Long,
        val type: Int,
        val sort: Int,
        val pn: Int,
        val ps: Int
    ) : BiliDetailEvent()
    object LikeVideo : BiliDetailEvent()
}

sealed class BiliDetailUiEffect {
    data class ShowToast(val message: String) : BiliDetailUiEffect()
}