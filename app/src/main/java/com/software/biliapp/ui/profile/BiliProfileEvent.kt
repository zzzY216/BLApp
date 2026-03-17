package com.software.biliapp.ui.profile

sealed class BiliProfileEvent {
    object GetUserInfo : BiliProfileEvent()
}