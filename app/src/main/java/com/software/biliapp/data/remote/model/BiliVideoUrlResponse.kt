package com.software.biliapp.data.remote.model

import com.google.gson.annotations.SerializedName
import com.software.biliapp.domain.model.PlayUrlDataDomain
import com.software.biliapp.domain.model.VideoDUrlDomain
import kotlinx.serialization.Serializable

@Serializable
data class BiliVideoUrlResponse<T>(
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: T?
)

@Serializable
data class PlayUrlData(
    val quality: Int,
    val format: String,
    val timelength: Long,
    val durl: List<VideoDUrl>,
    @SerializedName("accept_description") val acceptDescription: List<String>?= emptyList(),
    @SerializedName("accept_quality") val acceptQuality: List<Int> = emptyList()
)

@Serializable
data class VideoDUrl(
    val order: Int,
    val length: Long,
    val size: Long,
    val url: String,
    @SerializedName("backup_url") val backupUrl: List<String>? = emptyList()
)


fun PlayUrlData.toDomain(): PlayUrlDataDomain {
    return PlayUrlDataDomain(
        quality = quality,
        format = format,
        timelength = timelength,
        durl = durl.map { it.toDomain() },
        acceptDescription = acceptDescription,
        acceptQuality = acceptQuality
    )
}

fun VideoDUrl.toDomain(): VideoDUrlDomain {
    return VideoDUrlDomain(
        order = order,
        length = length,
        size = size,
        url = url,
        backupUrl = backupUrl
    )
}
