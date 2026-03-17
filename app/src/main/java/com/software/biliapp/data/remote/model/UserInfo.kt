package com.software.biliapp.data.remote.model

import com.software.biliapp.domain.model.UserInfoDomain
import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val isLogin: Boolean,
    val face: String,
    val uname: String
)

fun UserInfo.toDomain(): UserInfoDomain {
    return UserInfoDomain(
        isLogin = isLogin,
        face = face,
        uname = uname
    )
}
