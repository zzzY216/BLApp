package com.software.biliapp

import kotlinx.serialization.Serializable

@Serializable
object RouteBiliLogin

@Serializable
object RouteBiliHome

@Serializable
object RouteBiliDynamic

@Serializable
object RouteBiliRecommend

@Serializable
object RouteBiliProfile

@Serializable
class RouteBiliDetail(
    val avid: String = "",
    val cid: String = "",
    val qn: Int = 64,
    val type: String = "mp4",
    val platform: String = "html5"
)
